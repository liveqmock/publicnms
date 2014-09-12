package com.afunms.detail.service.storageInfo;

import java.util.List;

import com.afunms.temp.dao.StorageHostConnectTempDao;
import com.afunms.temp.model.StorageHostConnectNodeTemp;


public class StorageHostConnectInfoService {
	
	private String type;
	
	private String subtype;
	
	private String nodeid;
	

	/**
	 * @param type
	 * @param subtype
	 * @param nodeid
	 */
	public StorageHostConnectInfoService(String nodeid, String type, String subtype) {
		this.nodeid = nodeid;
		this.type = type;
		this.subtype = subtype;
	}
	
	
	public List<StorageHostConnectNodeTemp> getCurrHostConnectInfo(){
		StorageHostConnectTempDao storageHostConnectTempDao = new StorageHostConnectTempDao();
		List<StorageHostConnectNodeTemp> list = null;
		try {
			list = storageHostConnectTempDao.getStorageHostConnectNodeTemp(nodeid, type, subtype);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			storageHostConnectTempDao.close();
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
