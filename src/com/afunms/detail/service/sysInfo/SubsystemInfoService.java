package com.afunms.detail.service.sysInfo;

import java.util.Iterator;
import java.util.List;

import com.afunms.topology.dao.SubsystemForAS400Dao;
import com.afunms.topology.model.JobForAS400;
import com.afunms.topology.model.SubsystemForAS400;

public class SubsystemInfoService {
	
	private String type;
	
	private String subtype;
	
	private String nodeid;
	

	/**
	 * @param type
	 * @param subtype
	 * @param nodeid
	 */
	public SubsystemInfoService(String nodeid, String type, String subtype) {
		this.nodeid = nodeid;
		this.type = type;
		this.subtype = subtype;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<SubsystemForAS400> getCurrSubsystemForAS400Info(){
		List<SubsystemForAS400> subsystemForAS400List = null;
		SubsystemForAS400Dao subsystemForAS400Dao = new SubsystemForAS400Dao();
		try {
			subsystemForAS400List = subsystemForAS400Dao.findByNodeid(nodeid);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			subsystemForAS400Dao.close();
		}
		
		if(subsystemForAS400List == null || subsystemForAS400List.size() == 0){ 
			return subsystemForAS400List;
		}
		JobInfoService jobInfoService = new JobInfoService(nodeid, type, subtype);
		Iterator<SubsystemForAS400> iterator = subsystemForAS400List.iterator();
		while (iterator.hasNext()) {
			SubsystemForAS400 subsystem = (SubsystemForAS400) iterator.next();
			List<JobForAS400> list = jobInfoService.getCurrJobForAS400Info(null, null, null, subsystem.getPath());
			subsystem.setJobNum(list.size() + "");
		}
		return subsystemForAS400List;
	}
	


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
