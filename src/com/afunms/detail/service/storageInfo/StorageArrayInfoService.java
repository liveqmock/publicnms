package com.afunms.detail.service.storageInfo;

import java.util.List;

import com.afunms.temp.dao.StorageArrayTempDao;
import com.afunms.temp.model.StorageArrayNodeTemp;


public class StorageArrayInfoService {
	
	private String type;
	
	private String subtype;
	
	private String nodeid;
	

	/**
	 * @param type
	 * @param subtype
	 * @param nodeid
	 */
	public StorageArrayInfoService(String nodeid, String type, String subtype) {
		this.nodeid = nodeid;
		this.type = type;
		this.subtype = subtype;
	}
	
	
	public List<StorageArrayNodeTemp> getCurrArrayInfo(){
		StorageArrayTempDao storageArrayTempDao = new StorageArrayTempDao();
		List<StorageArrayNodeTemp> list = null;
		try {
			list = storageArrayTempDao.getStorageArrayNodeTemp(nodeid, type, subtype);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			storageArrayTempDao.close();
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
