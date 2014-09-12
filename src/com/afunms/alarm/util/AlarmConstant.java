package com.afunms.alarm.util;

public class AlarmConstant {
	
	public static final String TYPE_STORAGEHD = null;
	public static String TYPE_DB = "db";
	public static String TYPE_HOST = "host";
	public static String TYPE_NET = "net";
	public static String TYPE_CMTS = "cmts";
	public static String TYPE_STORAGE = "storage";
	public static String TYPE_F5 = "f5";
	public static String TYPE_VPN = "vpn";
	public static String TYPE_SERVICE = "service";
	public static String TYPE_MIDDLEWARE = "middleware";
	public static String SUBTYPE_ORACLE = "Oracle";
	
	public static String DATATYPE_STRING = "String";
	
	public static String DATATYPE_NUMBER = "Number";
	public static String TYPE_FIREWALL = "firewall";
	public static String TYPE_VIRTUAL = "virtual";
	
	public static String TYPE_AIRCONDITION = "air";
	public static String TYPE_UPS = "ups";
	
	public static String getType(String type){
		if(TYPE_DB.equals(type)){
			return TYPE_DB;
		}
		return null;
	}
	
	public static String getSubtype(String subtype){
		if(SUBTYPE_ORACLE.equals(subtype)){
			return SUBTYPE_ORACLE;
		}
		return null;
	}
	
	public static String getDatatype(String datatype){
		if(DATATYPE_STRING.equals(datatype)){
			return DATATYPE_STRING;
		}else if(DATATYPE_NUMBER.equals(datatype)){
			return DATATYPE_NUMBER;
		}
		return null;
	}
	
}