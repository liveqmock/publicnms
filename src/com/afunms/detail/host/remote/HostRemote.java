/**
 * <p>Description:DiscoverManager</p>
 * <p>Company: dhcc.com</p>
 * @author nielin
 * @project afunms
 * @date 2010-12-08
 */

package com.afunms.detail.host.remote;

import java.util.List;

import com.afunms.detail.host.service.HostService;
import com.afunms.detail.reomte.DetailReomte;
import com.afunms.detail.reomte.model.DetailTabRemote;
import com.afunms.detail.reomte.model.InterfaceInfo;
import com.afunms.detail.reomte.model.ProcessInfo;
import com.afunms.event.model.EventList;
import com.afunms.event.model.Syslog;
import com.afunms.polling.om.IpMac;
import com.afunms.temp.model.DeviceNodeTemp;
import com.afunms.temp.model.NodeTemp;
import com.afunms.temp.model.ServiceNodeTemp;
import com.afunms.temp.model.SoftwareNodeTemp;
import com.afunms.temp.model.StorageNodeTemp;
import com.afunms.topology.model.HostNode;


/**
 * 此类用于服务器设备详细信息页面远程调用
 */

public class HostRemote extends DetailReomte{
	
	public List<NodeTemp> getSystemInfo(String nodeid, String type, String subtype){
		return new HostService(nodeid, type, subtype).getSystemInfo();
	}
	
	public String getCurrDayPingAvgInfo(String nodeid, String type, String subtype){
		return new HostService(nodeid, type, subtype).getCurrDayPingAvgInfo();
	}
	
	public String getCurrCpuAvgInfo(String nodeid, String type, String subtype){
		return new HostService(nodeid, type, subtype).getCurrCpuAvgInfo();
	}
	
	public List<NodeTemp> getCurrPerCpuListInfo(String nodeid, String type, String subtype){
		return new HostService(nodeid, type, subtype).getCurrPerCpuListInfo();
	}
	
	public String getStautsInfo(String nodeid, String type, String subtype){
		return new HostService(nodeid, type, subtype).getStautsInfo();
	}
	
	public String getCategoryInfo(String nodeid, String type, String subtype){
		return new HostService(nodeid, type, subtype).getCategoryInfo();
	}
	
	public String getSupperInfo(String nodeid, String type, String subtype){
		return new HostService(nodeid, type, subtype).getSupperInfo();
	}
	
	public List<DetailTabRemote> getTabInfo(String nodeid, String type, String subtype){
		return new HostService(nodeid, type, subtype).getTabInfo();
	}
	
	public HostNode getHostNodeInfo(String nodeid, String type, String subtype){
		return new HostService(nodeid, type, subtype).getHostNode();
	}
	
	public List<InterfaceInfo> getInterfaceInfo(String nodeid, String type, String subtype){
		return new HostService(nodeid, type, subtype).getInterfaceInfo();
	}
	
	public List<ProcessInfo> getProcessInfo(String nodeid, String type, String subtype){
		return new HostService(nodeid, type, subtype).getProcessInfo();
	}
	
	public List<ProcessInfo> getProcessDetailInfo(String nodeid, String type, String subtype, String processName){
		return new HostService(nodeid, type, subtype).getProcessDetailInfo(processName);
	}
	
	public List<IpMac> getARPInfo(String nodeid, String type, String subtype){
		return new HostService(nodeid, type, subtype).getARPInfo();
	}
	
	public List<SoftwareNodeTemp> getSoftwareInfo(String nodeid, String type, String subtype){
		return new HostService(nodeid, type, subtype).getSoftwareInfo();
	}
	
	public List<ServiceNodeTemp> getServiceInfo(String nodeid, String type, String subtype){
		return new HostService(nodeid, type, subtype).getServiceInfo();
	}
	
	public List<Syslog> getSyslogInfo(String nodeid, String type, String subtype, String startdateValue, String todateValue, String priorityname){
		return new HostService(nodeid, type, subtype).getSyslogInfo(startdateValue, todateValue, priorityname);
	}
	
	public List<DeviceNodeTemp> getDeviceInfo(String nodeid, String type, String subtype){
		return new HostService(nodeid, type, subtype).getDeviceInfo();
	}
	
	public List<StorageNodeTemp> getStorageInfo(String nodeid, String type, String subtype){
		return new HostService(nodeid, type, subtype).getStorageInfo();
	}
	
	public List<Object> getAlarmInfo(String nodeid, String type, String subtype, String startdateValue, String todateValue, String level1, String status){
		return new HostService(nodeid, type, subtype).getAlarmInfo(startdateValue, todateValue, level1, status);
	}
	
	public List<EventList> getAlarmDetailInfo(String nodeid, String type, String subtype, String startdateValue, String todateValue, String level1, String eventlocation, String subentity, String status){
		return new HostService(nodeid, type, subtype).getAlarmDetailInfo(startdateValue, todateValue, level1, eventlocation, subentity, status);
	}
	
	
}
