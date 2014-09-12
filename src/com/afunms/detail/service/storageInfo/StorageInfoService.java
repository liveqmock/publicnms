package com.afunms.detail.service.storageInfo;

import java.util.List;

import com.afunms.temp.dao.StorageTempDao;
import com.afunms.temp.model.StorageNodeTemp;


public class StorageInfoService {
	
	private String type;
	
	private String subtype;
	
	private String nodeid;
	

	/**
	 * @param type
	 * @param subtype
	 * @param nodeid
	 */
	public StorageInfoService(String nodeid, String type, String subtype) {
		this.nodeid = nodeid;
		this.type = type;
		this.subtype = subtype;
	}
	
	
	public List<StorageNodeTemp> getCurrStorageInfo(){
		StorageTempDao storageTempDao = new StorageTempDao();
		List<StorageNodeTemp> list = null;
		try {
			list = storageTempDao.getStorageNodeTemp(nodeid, type, subtype);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			storageTempDao.close();
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
