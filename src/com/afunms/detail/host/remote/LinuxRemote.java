/**
 * <p>Description:DiscoverManager</p>
 * <p>Company: dhcc.com</p>
 * @author nielin
 * @project afunms
 * @date 2010-12-08
 */

package com.afunms.detail.host.remote;

import java.util.List;

import com.afunms.detail.host.service.LinuxService;
import com.afunms.detail.reomte.model.CpuPerfInfo;
import com.afunms.detail.reomte.model.DetailTabRemote;
import com.afunms.detail.reomte.model.DiskInfo;
import com.afunms.detail.reomte.model.DiskPerfInfo;
import com.afunms.detail.reomte.model.InterfaceInfo;
import com.afunms.detail.reomte.model.MemoryConfigInfo;
import com.afunms.detail.reomte.model.NetmediaConfigInfo;
import com.afunms.detail.reomte.model.PagePerfInfo;
import com.afunms.detail.reomte.model.PageSpaceInfo;
import com.afunms.detail.reomte.model.ProcessInfo;
import com.afunms.detail.reomte.model.UserConfigInfo;
import com.afunms.event.model.EventList;
import com.afunms.event.model.Syslog;
import com.afunms.temp.model.NodeTemp;
import com.afunms.topology.model.HostNode;


/**
 * 此类用于 Linux 服务器设备详细信息页面远程调用
 */

public class LinuxRemote extends HostRemote{
	
	public List<NodeTemp> getSystemInfo(String nodeid, String type, String subtype){
		return new LinuxService(nodeid, type, subtype).getSystemInfo();
	}
	
	public String getCurrDayPingAvgInfo(String nodeid, String type, String subtype){
		return new LinuxService(nodeid, type, subtype).getCurrDayPingAvgInfo();
	}
	
	public String getCurrCpuAvgInfo(String nodeid, String type, String subtype){
		return new LinuxService(nodeid, type, subtype).getCurrCpuAvgInfo();
	}
	
	public List<NodeTemp> getCurrPerCpuListInfo(String nodeid, String type, String subtype){
		return new LinuxService(nodeid, type, subtype).getCurrPerCpuListInfo();
	}
	
	public String getStautsInfo(String nodeid, String type, String subtype){
		return new LinuxService(nodeid, type, subtype).getStautsInfo();
	}
	
	public String getCategoryInfo(String nodeid, String type, String subtype){
		return new LinuxService(nodeid, type, subtype).getCategoryInfo();
	}
	
	public String getSupperInfo(String nodeid, String type, String subtype){
		return new LinuxService(nodeid, type, subtype).getSupperInfo();
	}
	
	public List<DetailTabRemote> getTabInfo(String nodeid, String type, String subtype){
		return new LinuxService(nodeid, type, subtype).getTabInfo();
	}
	
	public HostNode getHostNodeInfo(String nodeid, String type, String subtype){
		return new LinuxService(nodeid, type, subtype).getHostNode();
	}
	
	public List<InterfaceInfo> getInterfaceInfo(String nodeid, String type, String subtype){
		return new LinuxService(nodeid, type, subtype).getInterfaceInfo();
	}
	
	public List<DiskInfo> getDiskInfo(String nodeid, String type, String subtype){
		return new LinuxService(nodeid, type, subtype).getDiskInfo();
	}
	
	public String getDiskInfoUtilizationImg(String nodeid, String type, String subtype){
		return new LinuxService(nodeid, type, subtype).getDiskInfoUtilizationImg();
	}
	
	public List<CpuPerfInfo> getCpuPerfInfo(String nodeid, String type, String subtype){
		return new LinuxService(nodeid, type, subtype).getCpuPerfInfo();
	}
	
	public List<DiskPerfInfo> getDiskPerfInfo(String nodeid, String type, String subtype){
		return new LinuxService(nodeid, type, subtype).getDiskPerfInfo();
	}
	
	public List<PagePerfInfo> getPagePerfInfo(String nodeid, String type, String subtype){
		return new LinuxService(nodeid, type, subtype).getPagePerfInfo();
	}
	
	public List<PageSpaceInfo> getPageSpaceInfo(String nodeid, String type, String subtype){
		return new LinuxService(nodeid, type, subtype).getPageSpaceInfo();
	}
	
	public List<MemoryConfigInfo> getMemoryConfigInfo(String nodeid, String type, String subtype){
		return  new LinuxService(nodeid, type, subtype).getMemoryConfigInfo();
	}
	
	public List<NetmediaConfigInfo> getNetmediaConfigInfo(String nodeid, String type, String subtype){
		return new LinuxService(nodeid, type, subtype).getNetmediaConfigInfo();
	}
	
	public List<UserConfigInfo> getUserConfigInfo(String nodeid, String type, String subtype){
		return new LinuxService(nodeid, type, subtype).getUserConfigInfo();
	}
	
	public List<ProcessInfo> getProcessInfo(String nodeid, String type, String subtype){
		return new LinuxService(nodeid, type, subtype).getProcessInfo();
	}
	
	public List<ProcessInfo> getProcessDetailInfo(String nodeid, String type, String subtype, String processName){
		return new LinuxService(nodeid, type, subtype).getProcessDetailInfo(processName);
	}
	
	public List<Syslog> getSyslogInfo(String nodeid, String type, String subtype, String startdateValue, String todateValue, String priorityname){
		return new LinuxService(nodeid, type, subtype).getSyslogInfo(startdateValue, todateValue, priorityname);
	}
	
	public List<Object> getAlarmInfo(String nodeid, String type, String subtype, String startdateValue, String todateValue, String level1, String status){
		return new LinuxService(nodeid, type, subtype).getAlarmInfo(startdateValue, todateValue, level1, status);
	}
	
	public List<EventList> getAlarmDetailInfo(String nodeid, String type, String subtype, String startdateValue, String todateValue, String level1, String eventlocation, String subentity, String status){
		return new LinuxService(nodeid, type, subtype).getAlarmDetailInfo(startdateValue, todateValue, level1, eventlocation, subentity, status);
	}
	
	
	
	
}
