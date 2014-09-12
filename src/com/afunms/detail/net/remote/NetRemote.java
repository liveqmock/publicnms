/**
 * <p>Description:DiscoverManager</p>
 * <p>Company: dhcc.com</p>
 * @author nielin
 * @project afunms
 * @date 2010-12-08
 */

package com.afunms.detail.net.remote;

import java.util.List;

import com.afunms.config.model.IpAlias;
import com.afunms.detail.net.service.NetService;
import com.afunms.detail.reomte.DetailReomte;
import com.afunms.detail.reomte.model.DetailTabRemote;
import com.afunms.detail.reomte.model.InterfaceInfo;
import com.afunms.event.model.EventList;
import com.afunms.polling.om.IpMac;
import com.afunms.temp.model.FdbNodeTemp;
import com.afunms.temp.model.NodeTemp;
import com.afunms.temp.model.RouterNodeTemp;
import com.afunms.topology.model.HostNode;


/**
 * 此类用于网络设备详细信息页面远程调用
 */

public class NetRemote extends DetailReomte{
	
	public List<NodeTemp> getSystemInfo(String nodeid, String type, String subtype){
		return new NetService(nodeid, type, subtype).getSystemInfo();
	}
	
	public String getCurrDayPingAvgInfo(String nodeid, String type, String subtype){
		return new NetService(nodeid, type, subtype).getCurrDayPingAvgInfo();
	}
	
	public String getCurrCpuAvgInfo(String nodeid, String type, String subtype){
		return new NetService(nodeid, type, subtype).getCurrCpuAvgInfo();
	}
	
	public String getStautsInfo(String nodeid, String type, String subtype){
		return new NetService(nodeid, type, subtype).getStautsInfo();
	}
	
	public String getCategoryInfo(String nodeid, String type, String subtype){
		return new NetService(nodeid, type, subtype).getCategoryInfo();
	}
	
	public String getSupperInfo(String nodeid, String type, String subtype){
		return new NetService(nodeid, type, subtype).getSupperInfo();
	}
	
	public List<DetailTabRemote> getTabInfo(String nodeid, String type, String subtype){
		return new NetService(nodeid, type, subtype).getTabInfo();
	}
	
	public HostNode getHostNodeInfo(String nodeid, String type, String subtype){
		return new NetService(nodeid, type, subtype).getHostNode();
	}
	
	public List<InterfaceInfo> getInterfaceInfo(String nodeid, String type, String subtype){
		return new NetService(nodeid, type, subtype).getInterfaceInfo();
	}
	
	public List<IpMac> getARPInfo(String nodeid, String type, String subtype){
		return new NetService(nodeid, type, subtype).getARPInfo();
	}
	
	public List<FdbNodeTemp> getFDBInfo(String nodeid, String type, String subtype){
		return new NetService(nodeid, type, subtype).getFDBInfo();
	}
	
	public List<RouterNodeTemp> getRouterInfo(String nodeid, String type, String subtype){
		return new NetService(nodeid, type, subtype).getRouterInfo();
	}
	
	public List<IpAlias> getIpListInfo(String nodeid, String type, String subtype){
		return new NetService(nodeid, type, subtype).getIpListInfo();
	}
	
	public List<Object> getAlarmInfo(String nodeid, String type, String subtype, String startdateValue, String todateValue, String level1, String status){
		return new NetService(nodeid, type, subtype).getAlarmInfo(startdateValue, todateValue, level1, status);
	}
	
	public List<EventList> getAlarmDetailInfo(String nodeid, String type, String subtype, String startdateValue, String todateValue, String level1, String eventlocation, String subentity, String status){
		return new NetService(nodeid, type, subtype).getAlarmDetailInfo(startdateValue, todateValue, level1, eventlocation, subentity, status);
	}
	
}
