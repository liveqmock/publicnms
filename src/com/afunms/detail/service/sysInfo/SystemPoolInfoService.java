package com.afunms.detail.service.sysInfo;

import java.util.List;

import com.afunms.topology.dao.SystemPoolForAS400Dao;
import com.afunms.topology.model.SystemPoolForAS400;

public class SystemPoolInfoService {
	
	private String type;
	
	private String subtype;
	
	private String nodeid;
	

	/**
	 * @param type
	 * @param subtype
	 * @param nodeid
	 */
	public SystemPoolInfoService(String nodeid, String type, String subtype) {
		this.nodeid = nodeid;
		this.type = type;
		this.subtype = subtype;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<SystemPoolForAS400> getCurrSystemPoolForAS400Info(){
		List<SystemPoolForAS400> systemPoolForAS400List = null;
		SystemPoolForAS400Dao systemPoolForAS400Dao = new SystemPoolForAS400Dao();
		try {
			systemPoolForAS400List = systemPoolForAS400Dao.findByNodeid(nodeid);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			systemPoolForAS400Dao.close();
		}
		return systemPoolForAS400List;
	}
	


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
