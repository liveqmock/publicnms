package com.afunms.util;

import java.io.File;
import java.io.FileInputStream;

import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SystemConstant;
import com.afunms.initialize.ResourceCenter;

public class ProjectProperties {
	
	private static String DBType;
    private static String DBUrl;
    private static String DBName;
    private static String DBUser;
    private static String DBPwd;

    private static String DBUrl_sms;
    private static String DBUser_sms;
    private static String DBPwd_sms;
    
    private static String TFTPserver;
    
    private static String Reporttime;
    private static String DayReporTime;
    
    private static String maxconn;
    private static String minconn;
    private static String connectCheckOutTimeout;
    private static String connectUseTimeout;
    private static String connectUseCount;

    /**
     * @roseuid 3BCE42AE03AB
     */
    private static void init() {
        try {
            java.util.Properties p = new java.util.Properties();
            //String filePath=CommonAppUtil.getAppName()+"/task/DBProjectProertipes.txt";
            String filePath=ResourceCenter.getInstance().getSysPath()+"task"+"/DBProjectProertipes.txt";
            //File test = new File(filePath);
            //if (test.)
            FileInputStream fin = new FileInputStream(new File(filePath));
            p.load(fin);
            //DBType = p.getProperty("dbtype");
            try{
            	SystemConstant.setDBType(p.getProperty("dbtype"));
            }catch(Exception e){
            	e.printStackTrace();
            }
//            SysLogger.info("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
//            SysLogger.info("$$$$$$$$   "+DBType+" $$$$$$$$$$$");
//            SysLogger.info("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
            DBUrl = p.getProperty("DBUrl");
            DBName= p.getProperty("DBName");
            DBUser = p.getProperty("DBUser");
            DBPwd = p.getProperty("DBPwd");
            
            DBUrl_sms = p.getProperty("DBUrl_sms");
            DBUser_sms = p.getProperty("DBUser_sms");
            DBPwd_sms = p.getProperty("DBPwd_sms");
            
            TFTPserver = p.getProperty("TFTPserver");
            
            Reporttime = p.getProperty("Reporttime");
            DayReporTime = p.getProperty("DayReporTime");
            
            maxconn=p.getProperty("maxconn");
            minconn=p.getProperty("minconn");
            connectCheckOutTimeout=p.getProperty("connectCheckOutTimeout");
            connectUseTimeout=p.getProperty("connectUseTimeout");
            connectUseCount=p.getProperty("connectUseCount");
            
            
            
        } catch (Exception e) {
            System.out.println("读取出错代码配置文件时候出错。");
            e.printStackTrace();
        }

    }
    
    public static String getDBType() {
        if (DBType == null) init();
        return DBType;
    }
    
    public static String getDayReporTime() {
        if (DayReporTime == null) init();
        return DayReporTime;
    }
    
    
    public static String getReporttime() {
        if (Reporttime == null) init();
        return Reporttime;
    }
    
    public static String getTFTPserver() {
        if (TFTPserver == null) init();
        return TFTPserver;
    }
    
    public static String getDBUrl_sms() {
        if (DBUrl_sms == null) init();
        return DBUrl_sms;
    }

    public static String getDBUser_sms() {
        if (DBUser_sms == null) init();
        return DBUser_sms;
    }

    public static String getDBPwd_sms() {
        if (DBPwd_sms == null) init();
        return DBPwd_sms;
    }

    public static String getDBUrl() {
        if (DBUrl == null) init();
        return DBUrl;
    }

    public static String getDBName() {
		return DBName;
	}

	public static void setDBName(String name) {
		DBName = name;
	}

	public static String getDBUser() {
        if (DBUser == null) init();
        return DBUser;
    }

    public static String getDBPwd() {
        if (DBPwd == null) init();
        return DBPwd;
    }
	public static String getMaxconn() {
		return maxconn;
	}
	public static String getMinconn() {
		return minconn;
	}
	public static String getConnectCheckOutTimeout() {
		return connectCheckOutTimeout;
	}
	public static String getConnectUseTimeout() {
		return connectUseTimeout;
	}
	public static String getConnectUseCount() {
		return connectUseCount;
	}

}


