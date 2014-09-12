package com.afunms.detail.service.sysInfo;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import com.afunms.topology.dao.JobForAS400Dao;
import com.afunms.topology.model.JobForAS400;
import com.afunms.topology.util.JobConstantForAS400;

public class JobInfoService {
	
	private String type;
	
	private String subtype;
	
	private String nodeid;
	

	/**
	 * @param type
	 * @param subtype
	 * @param nodeid
	 */
	public JobInfoService(String nodeid, String type, String subtype) {
		this.nodeid = nodeid;
		this.type = type;
		this.subtype = subtype;
	}
	
	@SuppressWarnings("unchecked")
	public List<JobForAS400> getCurrJobForAS400Info(){
		return getCurrJobForAS400Info(null, null, null, null);
	}
	
	@SuppressWarnings("unchecked")
	public List<JobForAS400> getCurrJobForAS400Info(String jobType, String jobSubtype, String jobActivestatus, String jobSubsystem){
		List<JobForAS400> jobForAS400List = null;
		JobForAS400Dao jobForAS400Dao = new JobForAS400Dao();
		try {
			jobForAS400List = jobForAS400Dao.getJobForAS400ListInfo(nodeid, jobType, jobSubtype, jobActivestatus, jobSubsystem);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			jobForAS400Dao.close();
		}
		return convertJobForAS400(jobForAS400List);
	}
	
	public List<String[]> getAllJobTypeInfo(){
		List<String[]> allJobType = new ArrayList<String[]>();
		Enumeration<String> typeEnumeration = JobConstantForAS400.getTypeEnumeration();
		while(typeEnumeration.hasMoreElements()){
			String jobType_per = typeEnumeration.nextElement();
			String typeDescription_per = JobConstantForAS400.getTypeDescription(jobType_per);
			if("".equals(typeDescription_per)){
				typeDescription_per = "无类型";
			}
			String[] jobType = new String[2];
			jobType[0] = jobType_per;
			jobType[1] = typeDescription_per;
			allJobType.add(jobType);
		}
		return allJobType;
	}
	
	public List<String[]> getAllJobSubtypeInfo(){
		List<String[]> allJobSubtype = new ArrayList<String[]>();
		Enumeration<String> subtypeEnumeration = JobConstantForAS400.getSubtypeEnumeration();
		while(subtypeEnumeration.hasMoreElements()){
			String jobSubtype_per = subtypeEnumeration.nextElement();
			String subtypeDescription_per = JobConstantForAS400.getSubtypeDescription(jobSubtype_per);
			if("".equals(subtypeDescription_per)){
				subtypeDescription_per = "无子类型";
			}
			String[] jobSubtype = new String[2];
			jobSubtype[0] = jobSubtype_per;
			jobSubtype[1] = subtypeDescription_per;
			allJobSubtype.add(jobSubtype);
		}
		return allJobSubtype;
	}
	
	public List<String[]> getAllJobActiveStatusInfo(){
		List<String[]> allJobActiveStatus = new ArrayList<String[]>();
		Enumeration<String> statusEnumeration = JobConstantForAS400.getActiveStatusEnumeration();
		while(statusEnumeration.hasMoreElements()){
			String jobStatus_per = statusEnumeration.nextElement();
			String statusDescription_per = JobConstantForAS400.getActiveStatusDescription(jobStatus_per);
			String[] jobActiveStatus = new String[2];
			jobActiveStatus[0] = jobStatus_per;
			jobActiveStatus[1] = statusDescription_per;
			allJobActiveStatus.add(jobActiveStatus);
		}
		return allJobActiveStatus;
	}
	
	
	private List<JobForAS400> convertJobForAS400(List<JobForAS400> jobForAS400List){
		if(jobForAS400List!= null && jobForAS400List.size() > 0){
			Iterator<JobForAS400> iterator = jobForAS400List.iterator();
			while (iterator.hasNext()) {
				JobForAS400 jobForAS400 = (JobForAS400) iterator.next();
				String jobType_per = "";
				if(jobForAS400.getType()!=null){
					jobType_per = JobConstantForAS400.getTypeDescription(jobForAS400.getType());
				}
				if(jobType_per == null){
					jobType_per = "";
				}
				jobForAS400.setType(jobType_per);
				
				String jobSubtype_per = "";
				if(jobForAS400.getSubtype()!=null){
					jobSubtype_per = JobConstantForAS400.getSubtypeDescription(jobForAS400.getSubtype());
				}
				if(jobSubtype_per == null){
					jobSubtype_per = "";
				}
				jobForAS400.setSubtype(jobSubtype_per);
				
				String jobStatus_per = "";
				if(jobForAS400.getActiveStatus()!=null){
					jobStatus_per = JobConstantForAS400.getActiveStatusDescription(jobForAS400.getActiveStatus().trim());
				}
				if(jobStatus_per == null){
					jobStatus_per = jobForAS400.getActiveStatus();
				}
				jobForAS400.setActiveStatus(jobStatus_per);
			}
		}
		return jobForAS400List;
	}
	


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
