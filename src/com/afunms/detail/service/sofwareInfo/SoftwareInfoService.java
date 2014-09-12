package com.afunms.detail.service.sofwareInfo;

import java.util.List;

import com.afunms.temp.dao.SoftwareTempDao;
import com.afunms.temp.model.SoftwareNodeTemp;


public class SoftwareInfoService {
	
	private String type;
	
	private String subtype;
	
	private String nodeid;
	

	/**
	 * @param type
	 * @param subtype
	 * @param nodeid
	 */
	public SoftwareInfoService(String nodeid, String type, String subtype) {
		this.nodeid = nodeid;
		this.type = type;
		this.subtype = subtype;
	}
	
	
	public List<SoftwareNodeTemp> getCurrSoftwareInfo(){
		SoftwareTempDao softwareTempDao = new SoftwareTempDao();
		List<SoftwareNodeTemp> list = null;
		try {
			list = softwareTempDao.getSoftwareNodeTemp(nodeid, type, subtype);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			softwareTempDao.close();
		}
		return list;
	} 
	
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
