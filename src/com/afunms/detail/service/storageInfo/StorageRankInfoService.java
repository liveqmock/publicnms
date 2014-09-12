package com.afunms.detail.service.storageInfo;

import java.util.List;

import com.afunms.temp.dao.StorageRankTempDao;
import com.afunms.temp.model.StorageRankNodeTemp;


public class StorageRankInfoService {
	
	private String type;
	
	private String subtype;
	
	private String nodeid;
	

	/**
	 * @param type
	 * @param subtype
	 * @param nodeid
	 */
	public StorageRankInfoService(String nodeid, String type, String subtype) {
		this.nodeid = nodeid;
		this.type = type;
		this.subtype = subtype;
	}
	
	
	public List<StorageRankNodeTemp> getCurrRankInfo(){
		StorageRankTempDao storageRankTempDao = new StorageRankTempDao();
		List<StorageRankNodeTemp> list = null;
		try {
			list = storageRankTempDao.getStorageRankNodeTemp(nodeid, type, subtype);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			storageRankTempDao.close();
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
