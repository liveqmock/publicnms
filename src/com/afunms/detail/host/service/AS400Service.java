package com.afunms.detail.host.service;

import java.util.List;

import com.afunms.common.util.SysLogger;
import com.afunms.detail.reomte.model.DetailTabRemote;
import com.afunms.detail.service.diskInfo.DiskInfoService;
import com.afunms.detail.service.sysInfo.JobInfoService;
import com.afunms.detail.service.sysInfo.SubsystemInfoService;
import com.afunms.detail.service.sysInfo.SystemPoolInfoService;
import com.afunms.detail.service.sysInfo.SystemValueInfoService;
import com.afunms.topology.model.DiskForAS400;
import com.afunms.topology.model.JobForAS400;
import com.afunms.topology.model.SubsystemForAS400;
import com.afunms.topology.model.SystemPoolForAS400;
import com.afunms.topology.model.SystemValueForAS400;




public class AS400Service extends HostService{

	public AS400Service(String nodeid, String type, String subtype) {
		super(nodeid, type, subtype);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 获取 AS400 服务器设备的 tab 页信息
	 * @return
	 */
	public List<DetailTabRemote> getTabInfo(){
		String file = "/detail/host/as400detailtab.xml";
		// 调用父类的解析 tab 也信息
		SysLogger.info(file);
		return praseDetailTabXML(file);
	}
	
	/**
	 * 获取 AS400 服务器设备的系统状态信息
	 * @return
	 */
	public List<SystemValueForAS400> getSystemValueInfo(){
		SystemValueInfoService systemValueInfoService = new SystemValueInfoService(this.nodeid, this.type, this.subtype);
		return systemValueInfoService.getCurrSystemValueForAS400Info();
	}
	
	
	/**
	 * 获取 AS400 服务器设备的系统状态信息
	 * @return
	 */
	public List<SystemPoolForAS400> getSystemPoolInfo(){
		SystemPoolInfoService systemPoolInfoService = new SystemPoolInfoService(this.nodeid, this.type, this.subtype);
		return systemPoolInfoService.getCurrSystemPoolForAS400Info();
	}
	
	/**
	 * 获取 AS400 服务器设备的系统状态信息
	 * @return
	 */
	public List<DiskForAS400> getDiskInfo(){
		DiskInfoService diskInfoService = new DiskInfoService(this.nodeid, this.type, this.subtype);
		return diskInfoService.getCurrDiskForAS400Info();
	}
	
	/**
	 * 获取 AS400 服务器设备的作业信息
	 * @return
	 */
	public List<JobForAS400> getJobInfo(String jobType, String jobSubtype, String jobActivestatus, String jobSubsystem){
		JobInfoService jobInfoService = new JobInfoService(this.nodeid, this.type, this.subtype);
		return jobInfoService.getCurrJobForAS400Info(jobType, jobSubtype, jobActivestatus, jobSubsystem);
	}
	
	/**
	 * 获取 AS400 服务器设备的作业类型信息
	 * @return
	 */
	public List<String[]> getJobTypeInfo(){
		JobInfoService jobInfoService = new JobInfoService(this.nodeid, this.type, this.subtype);
		return jobInfoService.getAllJobTypeInfo();
	}
	
	/**
	 * 获取 AS400 服务器设备的作业子类型信息
	 * @return
	 */
	public List<String[]> getJobSubtypeInfo(){
		JobInfoService jobInfoService = new JobInfoService(this.nodeid, this.type, this.subtype);
		return jobInfoService.getAllJobSubtypeInfo();
	}
	
	/**
	 * 获取 AS400 服务器设备的作业活动状态信息
	 * @return
	 */
	public List<String[]> getJobActiveStatusInfo(){
		JobInfoService jobInfoService = new JobInfoService(this.nodeid, this.type, this.subtype);
		return jobInfoService.getAllJobActiveStatusInfo();
	}
	
	/**
	 * 获取 AS400 服务器设备的子系统信息
	 * @return
	 */
	public List<SubsystemForAS400> getSubsystemInfo(){
		SubsystemInfoService subsystemInfoService = new SubsystemInfoService(this.nodeid, this.type, this.subtype);
		return subsystemInfoService.getCurrSubsystemForAS400Info();
	}
	
	
	
}
