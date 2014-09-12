package com.afunms.detail.service.sysInfo;

import java.util.List;

import com.afunms.topology.dao.SystemValueForAS400Dao;
import com.afunms.topology.model.SystemValueForAS400;

public class SystemValueInfoService {
	
	private String type;
	
	private String subtype;
	
	private String nodeid;
	

	/**
	 * @param type
	 * @param subtype
	 * @param nodeid
	 */
	public SystemValueInfoService(String nodeid, String type, String subtype) {
		this.nodeid = nodeid;
		this.type = type;
		this.subtype = subtype;
	}
	
	
	public List<SystemValueForAS400> getCurrSystemValueForAS400Info(){
		List<SystemValueForAS400> systemValueForAS400List= null;
		SystemValueForAS400Dao systemValueForAS400Dao = new SystemValueForAS400Dao();
		try {
			systemValueForAS400List = systemValueForAS400Dao.findByNodeid(nodeid);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			systemValueForAS400Dao.close();
		}
		return systemValueForAS400List;
	}
	


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
