/**
 * <p>Description:DiscoverManager</p>
 * <p>Company: dhcc.com</p>
 * @author nielin
 * @project afunms
 * @date 2010-12-08
 */

package com.afunms.detail.net.remote;

import java.util.List;

import com.afunms.detail.net.service.FibreNetService;
import com.afunms.detail.reomte.model.DetailTabRemote;
import com.afunms.detail.reomte.model.FibreCapabilityInfo;
import com.afunms.detail.reomte.model.FibreConfigInfo;
import com.afunms.detail.reomte.model.InterfaceInfo;
import com.afunms.detail.reomte.model.LightInfo;
import com.afunms.detail.reomte.model.ProcessInfo;
import com.afunms.event.model.EventList;
import com.afunms.temp.model.NodeTemp;
import com.afunms.temp.model.RouterNodeTemp;
import com.afunms.topology.model.HostNode;


/**
 * 此类用于网络设备详细信息页面远程调用
 */

public class FibreNetRemote extends NetRemote{
	
	public List<NodeTemp> getSystemInfo(String nodeid, String type, String subtype){
		return new FibreNetService(nodeid, type, subtype).getSystemInfo();
	}
	
	public String getCurrDayPingAvgInfo(String nodeid, String type, String subtype){
		return new FibreNetService(nodeid, type, subtype).getCurrDayPingAvgInfo();
	}
	
	public String getCurrCpuAvgInfo(String nodeid, String type, String subtype){
		return new FibreNetService(nodeid, type, subtype).getCurrCpuAvgInfo();
	}
	
	public String getStautsInfo(String nodeid, String type, String subtype){
		return new FibreNetService(nodeid, type, subtype).getStautsInfo();
	}
	
	public String getCategoryInfo(String nodeid, String type, String subtype){
		return new FibreNetService(nodeid, type, subtype).getCategoryInfo();
	}
	
	public String getSupperInfo(String nodeid, String type, String subtype){
		return new FibreNetService(nodeid, type, subtype).getSupperInfo();
	}
	
	public List<DetailTabRemote> getTabInfo(String nodeid, String type, String subtype){
		return new FibreNetService(nodeid, type, subtype).getTabInfo();
	}
	
	public HostNode getHostNodeInfo(String nodeid, String type, String subtype){
		return new FibreNetService(nodeid, type, subtype).getHostNode();
	}
	
	public List<InterfaceInfo> getInterfaceInfo(String nodeid, String type, String subtype){
		return new FibreNetService(nodeid, type, subtype).getInterfaceInfo();
	}
	
	public List<RouterNodeTemp> getRouterInfo(String nodeid, String type, String subtype){
		return new FibreNetService(nodeid, type, subtype).getRouterInfo();
	}
	
	public List<ProcessInfo> getProcessInfo(String nodeid, String type, String subtype){
		return new FibreNetService(nodeid, type, subtype).getProcessInfo();
	}
	
	public List<LightInfo> getLightInfo(String nodeid, String type, String subtype){
		return new FibreNetService(nodeid, type, subtype).getLightInfo();
	}
	
	public List<FibreConfigInfo> getFibreConfigInfo(String nodeid, String type, String subtype){
		return new FibreNetService(nodeid, type, subtype).getFibreConfigInfo();
	}
	
	public List<FibreCapabilityInfo> getFibreCapabilityInfo(String nodeid, String type, String subtype){
		return new FibreNetService(nodeid, type, subtype).getFibreCapabilityInfo();
	}
	
	public List<Object> getAlarmInfo(String nodeid, String type, String subtype, String startdateValue, String todateValue, String level1, String status){
		return new FibreNetService(nodeid, type, subtype).getAlarmInfo(startdateValue, todateValue, level1, status);
	}
	
	public List<EventList> getAlarmDetailInfo(String nodeid, String type, String subtype, String startdateValue, String todateValue, String level1, String eventlocation, String subentity, String status){
		return new FibreNetService(nodeid, type, subtype).getAlarmDetailInfo(startdateValue, todateValue, level1, eventlocation, subentity, status);
	}
	
}
