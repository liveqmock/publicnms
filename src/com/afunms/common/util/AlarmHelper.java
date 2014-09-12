package com.afunms.common.util;

import java.util.Hashtable;
import java.util.List;

import com.afunms.config.dao.EnvConfigDao;
import com.afunms.config.model.EnvConfig;


public class AlarmHelper {
	
	public Hashtable<String, EnvConfig> getAlarmConfig(String ip,String entity) {
		Hashtable<String, EnvConfig> envHashtable=new Hashtable<String, EnvConfig>();
		EnvConfigDao configDao=new EnvConfigDao();
		List<EnvConfig> list=null;
		try {
			list=configDao.findByCondition(" where ipaddress='"+ip+"' and enabled=1 and entity='"+entity+"'");
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			configDao.close();
		}
		if(list!=null&&list.size()>0){
		for (int i = 0; i < list.size(); i++) {
			EnvConfig config=list.get(i);
			if(config!=null)
			envHashtable.put(config.getName(), config);
		}
		
		}
		return envHashtable;
	}
	public Hashtable<String, EnvConfig> getAlarmConfig() {
		Hashtable<String, EnvConfig> envHashtable=new Hashtable<String, EnvConfig>();
		EnvConfigDao configDao=new EnvConfigDao();
		List<EnvConfig> list=null;
		try {
			list=configDao.loadAll();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			configDao.close();
		}
		if(list!=null&&list.size()>0){
		for (int i = 0; i < list.size(); i++) {
			EnvConfig config=list.get(i);
		
			if(config!=null){
			 String key=config.getIpaddress()+":"+config.getName();
			 envHashtable.put(key, config);
			}
		}
		
		}
		return envHashtable;
	}
}