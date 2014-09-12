/*
 * Created on 2010-06-24
 *
 */
package com.afunms.polling.task;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.application.dao.StorageDao;
import com.afunms.application.dao.StoragePingDao;
import com.afunms.application.model.Storage;
import com.afunms.common.util.PingUtil;
import com.afunms.common.util.ShareData;
import com.afunms.polling.om.Pingcollectdata;
import com.afunms.polling.snmp.StorageCollection;





/**
 * @author nielin
 * @date 2010-06-24 
 * 
 * 此类为 存储 的task
 *
 */
public class StorageTask extends MonitorTask {
	
	public void run() {
		StorageDao storageDao = new StorageDao();
		List list = storageDao.findByMon_flag("1");
		
		if(list == null || list.size() == 0){
			return;
		}
		for(int i =0 ; i < list.size(); i++){
			Storage storage = (Storage)list.get(i);
			Hashtable dataHashtable = getData(storage); 
			ShareData.setStoragedata(storage.getIpaddress(), dataHashtable);
			executePing(storage);
			
		}
	}
	
	public Hashtable getData(Storage storage){
		String ipaddress = storage.getIpaddress();
		String name = storage.getName();
		String username = storage.getUsername();
		String password = storage.getPassword();
		
		List commandList = new ArrayList();
		commandList.add("lsarraysite -dev " + storage.getSerialNumber());
		commandList.add("lsarray -dev " + storage.getSerialNumber());
		commandList.add("lsrank -dev " + storage.getSerialNumber());
		commandList.add("lsextpool -dev " + storage.getSerialNumber());
		commandList.add("lsfbvol -dev " + storage.getSerialNumber());
		commandList.add("lsvolgrp -dev " + storage.getSerialNumber());
		commandList.add("lsioport -dev " + storage.getSerialNumber());
		commandList.add("lshostconnect -dev " + storage.getSerialNumber());
		
		
		StorageCollection storageCollection = new StorageCollection();
		
		storageCollection.setParameter(ipaddress, name, username, password);
		storageCollection.init();
		Hashtable hashtable = storageCollection.execute(commandList);
		storageCollection.destroy();
		//System.out.println(hashtable.size()+"========hashtable.size()============");
		return hashtable;
	}
	
	
	public void executePing(Storage storage){
		PingUtil pingU=new PingUtil(storage.getIpaddress());
		Integer[] packet=pingU.ping();
		Vector vector=null; 
		vector=pingU.addhis(packet); 
		if(vector!=null && vector.size()>1){
			StoragePingDao storagePingDao = new StoragePingDao();
			try {
				storagePingDao.save(vector);
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally{
				storagePingDao.close();
			}
			
			String status = "0";
			//System.out.println(packet[0]+"==========packet[0]============");
			if(packet[0] == null){
				status = "0";
			}else if(packet[0]>30){
				status = "1";
			}
			storage.setStatus(status);
			StorageDao storageDao = new StorageDao();
			try {
				storageDao.update(storage);
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally{
				storageDao.close();
			}
			
		}
		
		
		
		
		
	}
	
}
