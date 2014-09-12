package com.afunms.detail.service.storageInfo;

import java.util.ArrayList;
import java.util.List;

import com.afunms.temp.dao.StorageFbvolTempDao;
import com.afunms.temp.dao.StorageVolgrpFbvolTempDao;
import com.afunms.temp.model.StorageFbvolNodeTemp;
import com.afunms.temp.model.StorageVolgrpFbvolNodeTemp;


public class StorageFbvolInfoService {
	
	private String type;
	
	private String subtype;
	
	private String nodeid;
	

	/**
	 * @param type
	 * @param subtype
	 * @param nodeid
	 */
	public StorageFbvolInfoService(String nodeid, String type, String subtype) {
		this.nodeid = nodeid;
		this.type = type;
		this.subtype = subtype;
	}
	
	
	public List<StorageFbvolNodeTemp> getCurrFbvolInfo(){
		StorageFbvolTempDao storageFbvolTempDao = new StorageFbvolTempDao();
		List<StorageFbvolNodeTemp> list = null;
		try {
			list = storageFbvolTempDao.getStorageFbvolNodeTemp(nodeid, type, subtype);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			storageFbvolTempDao.close();
		}
		return list;
	}
	
	public List<StorageFbvolNodeTemp> getCurrVolgrpFbvolInfo(String volgrp_id){
		List<StorageVolgrpFbvolNodeTemp> storageVolgrpFbvolNodeTempList = null;
		StorageVolgrpFbvolTempDao storageVolgrpFbvolTempDao = new StorageVolgrpFbvolTempDao();
		try {
			storageVolgrpFbvolNodeTempList = storageVolgrpFbvolTempDao.getStorageVolgrpFbvolNodeTemp(nodeid, type, subtype, volgrp_id);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			storageVolgrpFbvolTempDao.close();
		}
		
		
		List<String> idList = new ArrayList<String>();
		for(int i = 0 ; i < storageVolgrpFbvolNodeTempList.size(); i++){
			StorageVolgrpFbvolNodeTemp storageVolgrpFbvolNodeTemp = storageVolgrpFbvolNodeTempList.get(i);
			String vols = storageVolgrpFbvolNodeTemp.getVols();
			String[] vols_str = vols.split(",");
			
			if(vols_str == null){
				continue;
			}
			for(int j = 0 ; j < vols_str.length; j++){
				idList.add(vols_str[j]);
			}
		}
		
		
		StorageFbvolTempDao storageFbvolTempDao = new StorageFbvolTempDao();
		List<StorageFbvolNodeTemp> list = null;
		try {
			list = storageFbvolTempDao.getStorageFbvolNodeTemp(nodeid, type, subtype, idList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			storageFbvolTempDao.close();
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
