package com.afunms.detail.service.storageInfo;

import java.util.List;

import com.afunms.temp.dao.StorageExtpoolTempDao;
import com.afunms.temp.model.StorageExtpoolNodeTemp;


public class StorageExtpoolInfoService {
	
	private String type;
	
	private String subtype;
	
	private String nodeid;
	

	/**
	 * @param type
	 * @param subtype
	 * @param nodeid
	 */
	public StorageExtpoolInfoService(String nodeid, String type, String subtype) {
		this.nodeid = nodeid;
		this.type = type;
		this.subtype = subtype;
	}
	
	
	public List<StorageExtpoolNodeTemp> getCurrExtpoolInfo(){
		StorageExtpoolTempDao storageExtpoolTempDao = new StorageExtpoolTempDao();
		List<StorageExtpoolNodeTemp> list = null;
		try {
			list = storageExtpoolTempDao.getStorageExtpoolNodeTemp(nodeid, type, subtype);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			storageExtpoolTempDao.close();
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
