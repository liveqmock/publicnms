/*
 * Created on 2010-06-24
 *
 */
package com.afunms.polling.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.TimerTask;
import java.util.Vector;

import org.apache.commons.beanutils.BeanUtils;
import org.python.modules.thread;

import com.afunms.alarm.dao.AlarmPortDao;
import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmConstant;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.Huawei3comtelnetUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SystemConstant;
import com.afunms.config.dao.PortconfigDao;
import com.afunms.config.model.Portconfig;
import com.afunms.discovery.IpAddress;
import com.afunms.discovery.Node;
import com.afunms.event.dao.CheckEventDao;
import com.afunms.event.model.CheckEvent;
import com.afunms.indicators.dao.NodeGatherIndicatorsDao;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostLastCollectData;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.impl.HostLastCollectDataManager;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.Pingcollectdata;
import com.afunms.polling.om.Task;
import com.afunms.polling.snmp.LoadAixFile;
import com.afunms.polling.snmp.LoadHpUnixFile;
import com.afunms.polling.snmp.LoadLinuxFile;
import com.afunms.polling.snmp.LoadSunOSFile;
import com.afunms.polling.snmp.LoadWindowsWMIFile;
import com.afunms.polling.snmp.cpu.BDComCpuSnmp;
import com.afunms.polling.snmp.cpu.CiscoCpuSnmp;
import com.afunms.polling.snmp.cpu.DLinkCpuSnmp;
import com.afunms.polling.snmp.cpu.EnterasysCpuSnmp;
import com.afunms.polling.snmp.cpu.H3CCpuSnmp;
import com.afunms.polling.snmp.cpu.LinuxCpuSnmp;
import com.afunms.polling.snmp.cpu.MaipuCpuSnmp;
import com.afunms.polling.snmp.cpu.NortelCpuSnmp;
import com.afunms.polling.snmp.cpu.RadwareCpuSnmp;
import com.afunms.polling.snmp.cpu.RedGiantCpuSnmp;
import com.afunms.polling.snmp.cpu.WindowsCpuSnmp;
import com.afunms.polling.snmp.cpu.ZTECpuSnmp;
import com.afunms.polling.snmp.device.LinuxDeviceSnmp;
import com.afunms.polling.snmp.device.WindowsDeviceSnmp;
import com.afunms.polling.snmp.disk.LinuxDiskSnmp;
import com.afunms.polling.snmp.disk.WindowsDiskSnmp;
import com.afunms.polling.snmp.fan.CiscoFanSnmp;
import com.afunms.polling.snmp.fan.H3CFanSnmp;
import com.afunms.polling.snmp.flash.BDComFlashSnmp;
import com.afunms.polling.snmp.flash.CiscoFlashSnmp;
import com.afunms.polling.snmp.flash.H3CFlashSnmp;
import com.afunms.polling.snmp.interfaces.ArpSnmp;
import com.afunms.polling.snmp.interfaces.FdbSnmp;
import com.afunms.polling.snmp.interfaces.InterfaceSnmp;
import com.afunms.polling.snmp.interfaces.PackageSnmp;
import com.afunms.polling.snmp.interfaces.RouterSnmp;
import com.afunms.polling.snmp.memory.BDComMemorySnmp;
import com.afunms.polling.snmp.memory.CiscoMemorySnmp;
import com.afunms.polling.snmp.memory.DLinkMemorySnmp;
import com.afunms.polling.snmp.memory.EnterasysMemorySnmp;
import com.afunms.polling.snmp.memory.H3CMemorySnmp;
import com.afunms.polling.snmp.memory.LinuxPhysicalMemorySnmp;
import com.afunms.polling.snmp.memory.MaipuMemorySnmp;
import com.afunms.polling.snmp.memory.NortelMemorySnmp;
import com.afunms.polling.snmp.memory.RedGiantMemorySnmp;
import com.afunms.polling.snmp.memory.WindowsPhysicalMemorySnmp;
import com.afunms.polling.snmp.memory.WindowsVirtualMemorySnmp;
import com.afunms.polling.snmp.ping.PingSnmp;
import com.afunms.polling.snmp.power.CiscoPowerSnmp;
import com.afunms.polling.snmp.power.H3CPowerSnmp;
import com.afunms.polling.snmp.process.LinuxProcessSnmp;
import com.afunms.polling.snmp.process.WindowsProcessSnmp;
import com.afunms.polling.snmp.service.WindowsServiceSnmp;
import com.afunms.polling.snmp.software.LinuxSoftwareSnmp;
import com.afunms.polling.snmp.software.WindowsSoftwareSnmp;
import com.afunms.polling.snmp.storage.LinuxStorageSnmp;
import com.afunms.polling.snmp.storage.WindowsStorageSnmp;
import com.afunms.polling.snmp.system.SystemSnmp;
import com.afunms.polling.snmp.temperature.BDComTemperatureSnmp;
import com.afunms.polling.snmp.temperature.CiscoTemperatureSnmp;
import com.afunms.polling.snmp.temperature.H3CTemperatureSnmp;
import com.afunms.polling.snmp.voltage.CiscoVoltageSnmp;
import com.afunms.polling.snmp.voltage.H3CVoltageSnmp;
import com.afunms.topology.dao.ConnectTypeConfigDao;
import com.afunms.topology.dao.DiscoverConfigDao;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.ConnectTypeConfig;
import com.afunms.topology.model.HostNode;





/**
 * @author nielin
 * @date 2010-05-16 
 * 
 * 对服务器进行采集
 *
 */
public class HostCollectDataTaskTest extends MonitorTask {
	private String nodeid;//采集的节点ID
	
	public String getNodeid() {
		return nodeid;
	}

	public void setNodeid(String nodeid) {
		this.nodeid = nodeid;
	}

	public void run(){
		if(nodeid == null){//该节点不存在,nodeid未被初始化
			SysLogger.info("该节点不存在,nodeid未被初始化");
			return;
		}
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(nodeid)); 
		if(node == null){//该节点已取消采集
			SysLogger.info("该节点nodeid:"+nodeid+"已取消采集");
			return;
		}
    	try {
        	
        	Hashtable hashv = new Hashtable();
        	LoadAixFile aix=null;
        	LoadLinuxFile linux=null;
        	LoadHpUnixFile hpunix = null;
        	LoadSunOSFile sununix = null;
        	LoadWindowsWMIFile windowswmi = null;
			
        	I_HostLastCollectData hostlastdataManager=new HostLastCollectDataManager();
        	if(node.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL){
        		//SHELL获取方式        		
        		try{
        			if(node.getOstype() == 6){
//        				SysLogger.info("采集: 开始采集IP地址为"+node.getIpAddress()+"类型为AIX主机服务器的数据");
//        				//AIX服务器
//        				try{
//        					aix = new LoadAixFile(node.getIpAddress());
//        					hashv=aix.getTelnetMonitorDetail();
//        					aix = null;
//        					ShareData.getM5AgentHostAlldata().put(node.getIpAddress(), hashv);
//        					//Vector pv = (Vector)hashv.get("process");
//        					//alldata.put(node.getIpAddress(), hashv);
//        					//hostdataManager.createHostData(node.getIpAddress(),hashv);
//        				}catch(Exception e){
//        					e.printStackTrace();
//        				}
        			}else if(node.getOstype() == 9){
//        				SysLogger.info("采集: 开始采集IP地址为"+node.getIpAddress()+"类型为LINUX主机服务器的数据");
//        				//LINUX服务器
//        				try{
//        					linux = new LoadLinuxFile(node.getIpAddress());
//        					hashv=linux.getTelnetMonitorDetail();
//        					linux = null;
//        					ShareData.getM5AgentHostAlldata().put(node.getIpAddress(), hashv);
//        					//alldata.put(node.getIpAddress(), hashv);
//        					//hostdataManager.createHostData(node.getIpAddress(),hashv);
//        				}catch(Exception e){
//        					e.printStackTrace();
//        				}
        			}else if(node.getOstype() == 7){
//        				SysLogger.info("采集: 开始采集IP地址为"+node.getIpAddress()+"类型为HPUNIX主机服务器的数据");
//        				//HPUNIX服务器
//        				try{
//        					hpunix = new LoadHpUnixFile(node.getIpAddress());
//        					hashv=hpunix.getTelnetMonitorDetail();
//        					hpunix = null;
//        					if(hashv != null){
//        						ShareData.getM5AgentHostAlldata().put(node.getIpAddress(), hashv);
//        						//alldata.put(node.getIpAddress(), hashv);
//        					}
//        					//hostdataManager.createHostData(node.getIpAddress(),hashv);
//        				}catch(Exception e){
//        					e.printStackTrace();
//        				}
        			}else if(node.getOstype() == 8){
//        				SysLogger.info("采集: 开始采集IP地址为"+node.getIpAddress()+"类型为SOLARIS主机服务器的数据");
//        				//SOLARIS服务器
//        				try{
//        					sununix = new LoadSunOSFile(node.getIpAddress());
//        					hashv=sununix.getTelnetMonitorDetail();
//        					sununix = null;
//        					ShareData.getM5AgentHostAlldata().put(node.getIpAddress(), hashv);
//        					//alldata.put(node.getIpAddress(), hashv);
//        					//hostdataManager.createHostData(node.getIpAddress(),hashv);
//        				}catch(Exception e){
//        					e.printStackTrace();
//        				}
        			}else if(node.getOstype() == 5){
        				SysLogger.info("采集: 开始用WMI方式采集IP地址为"+node.getIpAddress()+"类型为WINDOWS主机服务器的数据");
        				try{
        					windowswmi = new LoadWindowsWMIFile(node.getIpAddress());
        					hashv=windowswmi.getTelnetMonitorDetail();
        					windowswmi = null;
        					ShareData.getM5AgentHostAlldata().put(node.getIpAddress(), hashv);
        					//alldata.put(node.getIpAddress(), hashv);
        					//hostdataManager.createHostData(node.getIpAddress(),hashv);
        				}catch(Exception e){
        					e.printStackTrace();
        				}               				
        			}

        		}catch(Exception e){
        			e.printStackTrace();
        		}
				aix=null;
				hashv=null;
        	}else if(node.getCollecttype() == SystemConstant.COLLECTTYPE_WMI){
        		//WINDOWS下的WMI采集方式
        		SysLogger.info("采集: 开始用WMI方式采集IP地址为"+node.getIpAddress()+"类型为WINDOWS主机服务器的数据");
				try{
					windowswmi = new LoadWindowsWMIFile(node.getIpAddress());
					hashv=windowswmi.getTelnetMonitorDetail();
					windowswmi = null;
					ShareData.getM5AgentHostAlldata().put(node.getIpAddress(), hashv);
					//alldata.put(node.getIpAddress(), hashv);
					//hostdataManager.createHostData(node.getIpAddress(),hashv);
				}catch(Exception e){
					e.printStackTrace();
				}
				aix=null;
				hashv=null;
        	}
    	}catch(Exception exc){
        	exc.printStackTrace();
        }finally{
        	int m5AgentHostCollectedSize = -1;
        	if(!nodeid.equals("")){
        		m5AgentHostCollectedSize = ShareData.addM5AgentHostCollectedSize();
        	}
        	int needCollectNodesSize = ShareData.getM5AgentHostTimerMap().keySet().size();
        	//System.out.println("####nodeid:"+nodeid+"####needCollectNodesSize:"+needCollectNodesSize+"====ShareData.getM5CollectedSize():"+m5CollectedSize);
        	//判断所有Task是否运行完毕
			if(needCollectNodesSize == m5AgentHostCollectedSize){//需要采集的设备个数 和 已采集的设备个数相等，则保存
	    		ShareData.setM5AgentHostCollectedSize(0);
				Date _enddate = new Date();
				
				HostCollectDataManager hostdataManager=new HostCollectDataManager(); 
        		try{
        			Date startdate = new Date();
        			hostdataManager.createMultiHostData(ShareData.getM5AgentHostAlldata(),"host"); 
    	    		Date enddate = new Date();
    	    		SysLogger.info("##############################");
    	    		SysLogger.info("### 所有服务器（SNMP）入库时间 "+(enddate.getTime()-startdate.getTime())+"####");
    	    		SysLogger.info("##############################");
        		}catch(Exception e){
        			
        		}
        		hostdataManager = null;
	    		
	    		ShareData.setM5AgentHostAlldata(new Hashtable());//清空
				SysLogger.info("********M5AgentHostTask Thread Count : "+Thread.activeCount());
//				System.gc();//每五分钟调用一次垃圾回收
			}
		}
	}
	
	/**
	 * 查询数据库配置 并把数据更新到内存中
	 */
	public void updateConnectTypeConfig(){
		 ConnectTypeConfigDao connectTypeConfigDao = new ConnectTypeConfigDao();
	        Hashtable connectConfigHashtable = new Hashtable();
			List configList = new ArrayList();
			try{
				configList = connectTypeConfigDao.loadAll();
			}catch(Exception e){
				
			}finally{
				connectTypeConfigDao.close();
				connectTypeConfigDao = null;
			}
			if(configList != null && configList.size()>0){
				for(int i=0;i<configList.size();i++){
					ConnectTypeConfig connectTypeConfig = (ConnectTypeConfig)configList.get(i);
					connectConfigHashtable.put(connectTypeConfig.getNode_id(), connectTypeConfig);
				}
			}
			
			ShareData.getConnectConfigHashtable().put("connectConfigHashtable", connectConfigHashtable);

			//装载告警历史
			Hashtable checkEventHashtable = new Hashtable();
			CheckEventDao checkeventdao = new CheckEventDao();
			List eventlist = new ArrayList();
			try{
				eventlist = checkeventdao.loadAll();
			}catch(Exception e){
				
			}finally{
				checkeventdao.close();
			}
			if(eventlist != null && eventlist.size()>0){
				CheckEvent vo = null;
				for(int i=0;i<eventlist.size();i++){
					vo = (CheckEvent)eventlist.get(i);
//					checkEventHashtable.put(vo.getName(), vo.getAlarmlevel());
				}
			}
			ShareData.setCheckEventHash(checkEventHashtable);
			
			List portconfiglist = new ArrayList();
			PortconfigDao configdao = new PortconfigDao(); 			
			Portconfig portconfig = null;
			Hashtable portconfigHash = new Hashtable();
			try {
				portconfiglist = configdao.getAllBySms();
			} catch (RuntimeException e) {
				e.printStackTrace();
			} finally {
				configdao.close();
			}
			if(portconfiglist != null && portconfiglist.size()>0){
				for(int i=0;i<portconfiglist.size();i++){
					portconfig = (Portconfig)portconfiglist.get(i);
					if(portconfigHash.containsKey(portconfig.getIpaddress())){
						List portlist = (List)portconfigHash.get(portconfig.getIpaddress());
						portlist.add(portconfig);
						portconfigHash.put(portconfig.getIpaddress(), portlist);
					}else{
						List portlist = new ArrayList();
						portlist.add(portconfig);
						portconfigHash.put(portconfig.getIpaddress(), portlist);
					}
				}
			} 
			ShareData.setPortConfigHash(portconfigHash);
	}
	
	/**
	 * 获取所有5分钟采集节点的采集指标集合 
	 * key:nodeid  value:采集指标集合
	 * @return
	 */
	public static Hashtable getDocollcetHash(){
		NodeGatherIndicatorsDao indicatorsdao = new NodeGatherIndicatorsDao();
		List<NodeGatherIndicators> monitorItemList = new ArrayList<NodeGatherIndicators>();
		try {
			// 获取被启用的所有被监视指标
			monitorItemList = indicatorsdao.getByIntervalAndType("5", "m", 1,"host");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			indicatorsdao.close();
		}
		if (monitorItemList == null)
			monitorItemList = new ArrayList<NodeGatherIndicators>();
		HostNodeDao nodedao = new HostNodeDao();
		List nodelist = new ArrayList();
		try {
			nodelist = nodedao.loadMonitorByMonCategory(1,4);
		} catch (Exception e) {

		} finally {
			nodedao.close();
		}
		Hashtable nodehash = new Hashtable();
		if (nodelist != null && nodelist.size() > 0) {
			for (int i = 0; i < nodelist.size(); i++) {
				HostNode node = (HostNode) nodelist.get(i);
				nodehash.put(node.getId() + "", node.getId());
			}
		}
		Hashtable docollcetHash = new Hashtable();
		
		Date _startdate = new Date();
		for (int i = 0; i < monitorItemList.size(); i++) {
			NodeGatherIndicators nodeGatherIndicators = (NodeGatherIndicators) monitorItemList
					.get(i);
			if (docollcetHash.containsKey(nodeGatherIndicators.getNodeid())) {
				Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(nodeGatherIndicators.getNodeid()));
				// 过滤掉不监视的设备
				if (!nodehash.containsKey(nodeGatherIndicators.getNodeid()))
					continue;
				List tempList = (List) docollcetHash.get(nodeGatherIndicators
						.getNodeid());
				tempList.add(nodeGatherIndicators);
				docollcetHash.put(nodeGatherIndicators.getNodeid(), tempList);
			} else {
				Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(nodeGatherIndicators.getNodeid()));
				// 过滤掉不监视的设备
				if (!nodehash.containsKey(nodeGatherIndicators.getNodeid()))
					continue;
				List tempList = new ArrayList();
				tempList.add(nodeGatherIndicators);
				docollcetHash.put(nodeGatherIndicators.getNodeid(), tempList);
			}
		}
		return docollcetHash;
	}
}
