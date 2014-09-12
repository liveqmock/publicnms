package com.afunms.detail.service.storageInfo;

import java.util.List;

import com.afunms.temp.dao.StorageIOPortTempDao;
import com.afunms.temp.model.StorageIOPortNodeTemp;


public class StorageIOPortInfoService {
	
	private String type;
	
	private String subtype;
	
	private String nodeid;
	

	/**
	 * @param type
	 * @param subtype
	 * @param nodeid
	 */
	public StorageIOPortInfoService(String nodeid, String type, String subtype) {
		this.nodeid = nodeid;
		this.type = type;
		this.subtype = subtype;
	}
	
	
	public List<StorageIOPortNodeTemp> getCurrIOPortInfo(){
		StorageIOPortTempDao storageIOPortTempDao = new StorageIOPortTempDao();
		List<StorageIOPortNodeTemp> list = null;
		try {
			list = storageIOPortTempDao.getStorageIOPortNodeTemp(nodeid, type, subtype);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			storageIOPortTempDao.close();
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
