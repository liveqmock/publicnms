package com.afunms.detail.service.storageInfo;

import java.util.List;

import com.afunms.temp.dao.StorageVolgrpTempDao;
import com.afunms.temp.model.StorageVolgrpNodeTemp;


public class StorageVolgrpInfoService {
	
	private String type;
	
	private String subtype;
	
	private String nodeid;
	

	/**
	 * @param type
	 * @param subtype
	 * @param nodeid
	 */
	public StorageVolgrpInfoService(String nodeid, String type, String subtype) {
		this.nodeid = nodeid;
		this.type = type;
		this.subtype = subtype;
	}
	
	
	public List<StorageVolgrpNodeTemp> getCurrVolgrpInfo(){
		StorageVolgrpTempDao storageVolgrpTempDao = new StorageVolgrpTempDao();
		List<StorageVolgrpNodeTemp> list = null;
		try {
			list = storageVolgrpTempDao.getStorageVolgrpNodeTemp(nodeid, type, subtype);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			storageVolgrpTempDao.close();
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
