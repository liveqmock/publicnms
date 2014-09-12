package com.afunms.detail.service.storageInfo;

import java.util.List;

import com.afunms.temp.dao.StorageArraySiteTempDao;
import com.afunms.temp.model.StorageArraySiteNodeTemp;


public class StorageArraySiteInfoService {
	
	private String type;
	
	private String subtype;
	
	private String nodeid;
	

	/**
	 * @param type
	 * @param subtype
	 * @param nodeid
	 */
	public StorageArraySiteInfoService(String nodeid, String type, String subtype) {
		this.nodeid = nodeid;
		this.type = type;
		this.subtype = subtype;
	}
	
	
	public List<StorageArraySiteNodeTemp> getCurrArraySiteInfo(){
		StorageArraySiteTempDao storageArraySiteTempDao = new StorageArraySiteTempDao();
		List<StorageArraySiteNodeTemp> list = null;
		try {
			list = storageArraySiteTempDao.getStorageArraySiteNodeTemp(nodeid, type, subtype);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			storageArraySiteTempDao.close();
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
