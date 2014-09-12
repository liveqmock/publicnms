package com.afunms.detail.net.service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Vector;

import org.apache.commons.beanutils.BeanUtils;

import com.afunms.common.util.CommonUtil;
import com.afunms.config.model.IpAlias;
import com.afunms.detail.reomte.model.DetailTabRemote;
import com.afunms.detail.reomte.model.InterfaceInfo;
import com.afunms.detail.service.DetailService;
import com.afunms.detail.service.deviceInfo.DeviceInfoService;
import com.afunms.detail.service.fdbInfo.FDBInfoService;
import com.afunms.detail.service.ipListInfo.IpListInfoService;
import com.afunms.detail.service.ipMacInfo.IpMacInfoService;
import com.afunms.detail.service.routerInfo.RouterInfoService;
import com.afunms.detail.service.serviceInfo.ServiceInfoService;
import com.afunms.detail.service.sofwareInfo.SoftwareInfoService;
import com.afunms.detail.service.storageInfo.StorageInfoService;
import com.afunms.polling.om.IpMac;
import com.afunms.polling.om.IpRouter;
import com.afunms.temp.model.DeviceNodeTemp;
import com.afunms.temp.model.FdbNodeTemp;
import com.afunms.temp.model.RouterNodeTemp;
import com.afunms.temp.model.ServiceNodeTemp;
import com.afunms.temp.model.SoftwareNodeTemp;
import com.afunms.temp.model.StorageNodeTemp;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;

public class NetService extends DetailService{
	
	protected HostNode hostNode;
	
	public NetService(String nodeid, String type, String subtype) {
		super(nodeid, type, subtype);
	}

	protected void init(){
		HostNodeDao hostNodeDao = new HostNodeDao();
		try {
			setHostNode((HostNode)hostNodeDao.findByID(nodeid));
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			hostNodeDao.close();
		}
		super.init(this.hostNode);
	}
	
	
	/**
	 * @return the hostNode
	 */
	public HostNode getHostNode() {
		return hostNode;
	}

	/**
	 * @param hostNode the hostNode to set
	 */
	public void setHostNode(HostNode hostNode) {
		this.hostNode = hostNode;
	}
	
	/**
	 * 获取网络设备的 tab 页信息
	 * @return
	 */
	public List<DetailTabRemote> getTabInfo(){
		String file = "/detail/net/netdetailtab.xml";
		// 调用父类的解析 tab 也信息
		return praseDetailTabXML(file);
	}
	
	
	/**
	 * 获取网络设备的类别的信息
	 * @param category	--- 设备类别
	 * @return
	 */
	public String getCategoryInfo(){
		// 调用父类方法
		return getCategoryInfo(this.hostNode.getCategory()+"");
	}
	
	/**
	 * 获取网络设备的供应商的信息
	 * @return
	 */
	public String getSupperInfo(){
		// 调用父类方法
		return getSupperInfo(this.hostNode.getSupperid() + "");
	}
	
	/**
	 * 获取网络设备的接口的信息
	 * @return
	 */
	public List<InterfaceInfo> getInterfaceInfo(){
		String[] subentities = {"index", "ifDescr", "ifSpeed", "ifOperStatus", 
				"ifOutBroadcastPkts", "ifInBroadcastPkts", "ifOutMulticastPkts",
				"ifInMulticastPkts", "OutBandwidthUtilHdx", "InBandwidthUtilHdx",};
		// 调用父类方法
		return getInterfaceInfo(subentities);
	}
	
	public List<IpMac> getARPInfo(){
		IpMacInfoService ipMacInfoService = new IpMacInfoService(this.nodeid, this.type, this.subtype);
		return ipMacInfoService.getCurrAllIpMacInfo(this.hostNode.getIpAddress());
	}
	
	public List<FdbNodeTemp> getFDBInfo(){
		FDBInfoService fDBInfoService = new FDBInfoService(this.nodeid, this.type, this.subtype);
		return fDBInfoService.getCurrAllFDBInfo();
	}
	
	public List<IpAlias> getIpListInfo(){
		IpListInfoService ipListInfoService = new IpListInfoService(this.nodeid, this.type, this.subtype);
		return ipListInfoService.getCurrAllIpListInfo(this.hostNode.getIpAddress());
	}
	
	public List<RouterNodeTemp> getRouterInfo(){
		RouterInfoService routerInfoService = new RouterInfoService(this.nodeid, this.type, this.subtype);
		return routerInfoService.getCurrAllRouterInfo();
	}
	
	public List<SoftwareNodeTemp> getSoftwareInfo(){
		SoftwareInfoService softwareInfoService = new SoftwareInfoService(this.nodeid, this.type, this.subtype);
		return softwareInfoService.getCurrSoftwareInfo();
	}
	
	public List<ServiceNodeTemp> getServiceInfo(){
		ServiceInfoService serviceInfoService = new ServiceInfoService(this.nodeid, this.type, this.subtype);
		return serviceInfoService.getCurrServiceInfo();
	}
	
	public List<DeviceNodeTemp> getDeviceInfo(){
		DeviceInfoService deviceInfoService = new DeviceInfoService(this.nodeid, this.type, this.subtype);
		return deviceInfoService.getCurrDeviceInfo();
	}
	
	public List<StorageNodeTemp> getStorageInfo(){
		StorageInfoService storageInfoService = new StorageInfoService(this.nodeid, this.type, this.subtype);
		return storageInfoService.getCurrStorageInfo();
	}
	
//	public Vector<IpRouter> getIpRouterVectorByList(List<RouterNodeTemp> routerList){
//		Vector<IpRouter> retVector = new Vector<IpRouter>();
//		for(int i=0; i<routerList.size(); i++){
//			RouterNodeTemp routerNodeTemp = routerList.get(i);
//			IpRouter ipRouter = new IpRouter();
//			ipRouter.setProto(Long.parseLong(routerNodeTemp.getProto()));
//			ipRouter.setType(Long.parseLong(routerNodeTemp.getType()));
//			ipRouter.setDest(routerNodeTemp.getDest());
//			ipRouter.setId(Long.parseLong(routerNodeTemp.getNodeid()));
//			ipRouter.setIfindex(routerNodeTemp.getIfindex());
//			ipRouter.setMask(routerNodeTemp.getMask());
//			ipRouter.setNexthop(routerNodeTemp.getNexthop());
//			ipRouter.setPhysaddress(routerNodeTemp.getPhysaddress());
//			retVector.add(ipRouter);
//		}
//		return retVector;
//	}
}
