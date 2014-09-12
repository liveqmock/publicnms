package com.afunms.alarm.util;

import java.util.Hashtable;

public class AlarmResourceCenter {
	private static AlarmResourceCenter instance = new AlarmResourceCenter();
	
	private static Hashtable<String , Object> alarm = new Hashtable<String, Object>();
	
	public static AlarmResourceCenter getInstance(){       
       return instance;
    }
	
	public Object getAttribute(String key){
		return alarm.get(key);
	}
	
	public void setAttribute(String key , String value){
		alarm.put(key, value);
	}
	
}
