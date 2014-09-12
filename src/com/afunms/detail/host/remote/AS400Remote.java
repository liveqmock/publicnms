/**
 * <p>Description:DiscoverManager</p>
 * <p>Company: dhcc.com</p>
 * @author nielin
 * @project afunms
 * @date 2010-12-08
 */

package com.afunms.detail.host.remote;

import java.util.List;

import com.afunms.detail.host.service.AS400Service;
import com.afunms.detail.reomte.model.DetailTabRemote;
import com.afunms.event.model.EventList;
import com.afunms.temp.model.NodeTemp;
import com.afunms.topology.model.DiskForAS400;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.model.JobForAS400;
import com.afunms.topology.model.SubsystemForAS400;
import com.afunms.topology.model.SystemPoolForAS400;
import com.afunms.topology.model.SystemValueForAS400;


/**
 * 此类用于 AS400 服务器设备详细信息页面远程调用
 */

public class AS400Remote extends HostRemote{
	
	public List<NodeTemp> getSystemInfo(String nodeid, String type, String subtype){
		return new AS400Service(nodeid, type, subtype).getSystemInfo();
	}
	
	public String getCurrDayPingAvgInfo(String nodeid, String type, String subtype){
		return new AS400Service(nodeid, type, subtype).getCurrDayPingAvgInfo();
	}
	
	public String getCurrCpuAvgInfo(String nodeid, String type, String subtype){
		return new AS400Service(nodeid, type, subtype).getCurrCpuAvgInfo();
	}
	
	public List<NodeTemp> getCurrPerCpuListInfo(String nodeid, String type, String subtype){
		return new AS400Service(nodeid, type, subtype).getCurrPerCpuListInfo();
	}
	
	public String getStautsInfo(String nodeid, String type, String subtype){
		return new AS400Service(nodeid, type, subtype).getStautsInfo();
	}
	
	public String getCategoryInfo(String nodeid, String type, String subtype){
		return new AS400Service(nodeid, type, subtype).getCategoryInfo();
	}
	
	public String getSupperInfo(String nodeid, String type, String subtype){
		return new AS400Service(nodeid, type, subtype).getSupperInfo();
	}
	
	public List<DetailTabRemote> getTabInfo(String nodeid, String type, String subtype){
		return new AS400Service(nodeid, type, subtype).getTabInfo();
	}
	
	public HostNode getHostNodeInfo(String nodeid, String type, String subtype){
		return new AS400Service(nodeid, type, subtype).getHostNode();
	}
	
	public List<SystemValueForAS400> getSystemValueInfo(String nodeid, String type, String subtype){
		return new AS400Service(nodeid, type, subtype).getSystemValueInfo();
	}
	
	public List<DiskForAS400> getDiskInfo(String nodeid, String type, String subtype){
		return new AS400Service(nodeid, type, subtype).getDiskInfo();
	}
	
	public List<SystemPoolForAS400> getSystemPoolInfo(String nodeid, String type, String subtype){
		return new AS400Service(nodeid, type, subtype).getSystemPoolInfo();
	}
	
	public List<JobForAS400> getJobInfo(String nodeid, String type, String subtype, String jobType, String jobSubtype, String jobActivestatus, String jobSubsystem){
		return new AS400Service(nodeid, type, subtype).getJobInfo(jobType, jobSubtype, jobActivestatus, jobSubsystem);
	}
	
	public List<String[]> getJobTypeInfo(String nodeid, String type, String subtype){
		return new AS400Service(nodeid, type, subtype).getJobTypeInfo();
	}
	
	public List<String[]> getJobSubtypeInfo(String nodeid, String type, String subtype){
		return new AS400Service(nodeid, type, subtype).getJobSubtypeInfo();
	}
	
	public List<String[]> getJobActiveStatusInfo(String nodeid, String type, String subtype){
		return new AS400Service(nodeid, type, subtype).getJobActiveStatusInfo();
	}
	
//	public List<JobForAS400> getJobBySubsystemInfo(String nodeid, String type, String subtype, String subsystem){
//		System.out.println(subsystem);
//		return new AS400Service(nodeid, type, subtype).getJobInfo(subsystem);
//	}
	
	public List<SubsystemForAS400> getSubsystemInfo(String nodeid, String type, String subtype){
		return new AS400Service(nodeid, type, subtype).getSubsystemInfo();
	}
	
	
	public List<Object> getAlarmInfo(String nodeid, String type, String subtype, String startdateValue, String todateValue, String level1, String status){
		return new AS400Service(nodeid, type, subtype).getAlarmInfo(startdateValue, todateValue, level1, status);
	}
	
	public List<EventList> getAlarmDetailInfo(String nodeid, String type, String subtype, String startdateValue, String todateValue, String level1, String eventlocation, String subentity, String status){
		return new AS400Service(nodeid, type, subtype).getAlarmDetailInfo(startdateValue, todateValue, level1, eventlocation, subentity, status);
	}
	
}
