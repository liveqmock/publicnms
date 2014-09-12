package com.afunms.detail.host.service;

import java.util.List;


import com.afunms.common.util.SysLogger;
import com.afunms.detail.reomte.model.DetailTabRemote;
import com.afunms.detail.reomte.model.InterfaceInfo;
import com.afunms.detail.reomte.model.ProcessInfo;
import com.afunms.detail.service.DetailService;
import com.afunms.detail.service.cpuInfo.CpuInfoService;
import com.afunms.detail.service.deviceInfo.DeviceInfoService;
import com.afunms.detail.service.ipMacInfo.IpMacInfoService;
import com.afunms.detail.service.processInfo.ProcessInfoService;
import com.afunms.detail.service.serviceInfo.ServiceInfoService;
import com.afunms.detail.service.sofwareInfo.SoftwareInfoService;
import com.afunms.detail.service.storageInfo.StorageInfoService;
import com.afunms.polling.om.IpMac;
import com.afunms.temp.model.DeviceNodeTemp;
import com.afunms.temp.model.NodeTemp;
import com.afunms.temp.model.ServiceNodeTemp;
import com.afunms.temp.model.SoftwareNodeTemp;
import com.afunms.temp.model.StorageNodeTemp;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;

public class HostService extends DetailService{
	
	protected HostNode hostNode;
	
	public HostService(String nodeid, String type, String subtype) {
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
	 * 获取主机服务器的 tab 页信息
	 * @return
	 */
	public List<DetailTabRemote> getTabInfo(){
		String file = "/detail/host/hostdetailtab.xml";
		// 调用父类的解析 tab 也信息
		SysLogger.info(file);
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
	 * 获取服务器设备的接口的信息
	 * @return
	 */
	public List<InterfaceInfo> getInterfaceInfo(){
		String[] subentities = {"index", "ifDescr", "ifSpeed", "ifOperStatus", 
				"ifOutBroadcastPkts", "ifInBroadcastPkts", "ifOutMulticastPkts",
				"ifInMulticastPkts", "OutBandwidthUtilHdx", "InBandwidthUtilHdx",};
		// 调用父类方法
		return getInterfaceInfo(subentities);
	}
	
	/**
	 * 获取每一个 cpu 的信息
	 * 因为只有服务器具有多个 cpu 的情况 所以该方法没有放到父类里
	 * @return 返回 每个 cpu 的信息
	 */
	public List<NodeTemp> getCurrPerCpuListInfo(){
		CpuInfoService cpuInfoService = new CpuInfoService(this.nodeid, this.type, this.subtype);
		return cpuInfoService.getCurrPerCpuListInfo();
	}
	
	/**
	 * 获取进程信息
	 * @return 返回 相同名称的进程统计后的列表 的信息
	 */
	public List<ProcessInfo> getProcessInfo(){
		ProcessInfoService processInfoService = new ProcessInfoService(this.nodeid, this.type, this.subtype);
		return processInfoService.getCountProcessInfoByName();
	}
	
	/**
	 * 返回进程的详细信息
	 * @return
	 */
	public List<ProcessInfo> getProcessDetailInfo(String processName){
		ProcessInfoService processInfoService = new ProcessInfoService(this.nodeid, this.type, this.subtype);
		return processInfoService.getCurrProcessDetailInfo(processName);
	}
	
	/**
	 * 获取 ARP 信息
	 */
	public List<IpMac> getARPInfo(){
		IpMacInfoService ipMacInfoService = new IpMacInfoService(this.nodeid, this.type, this.subtype);
		return ipMacInfoService.getCurrAllIpMacInfo(this.hostNode.getIpAddress());
	}
	
	/**
	 * 获取软件信息
	 * @return
	 */
	public List<SoftwareNodeTemp> getSoftwareInfo(){
		SoftwareInfoService softwareInfoService = new SoftwareInfoService(this.nodeid, this.type, this.subtype);
		return softwareInfoService.getCurrSoftwareInfo();
	}
	
	/**
	 * 获取软件信息
	 * @return
	 */
	public List<ServiceNodeTemp> getServiceInfo(){
		ServiceInfoService serviceInfoService = new ServiceInfoService(this.nodeid, this.type, this.subtype);
		return serviceInfoService.getCurrServiceInfo();
	}
	
	/**
	 * 获取设备信息
	 * @return
	 */
	public List<DeviceNodeTemp> getDeviceInfo(){
		DeviceInfoService deviceInfoService = new DeviceInfoService(this.nodeid, this.type, this.subtype);
		return deviceInfoService.getCurrDeviceInfo();
	}
	
	/**
	 * 获取存储信息
	 * @return
	 */
	public List<StorageNodeTemp> getStorageInfo(){
		StorageInfoService storageInfoService = new StorageInfoService(this.nodeid, this.type, this.subtype);
		return storageInfoService.getCurrStorageInfo();
	}
	
	
	
}
