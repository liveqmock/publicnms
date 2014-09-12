/**
 * <p>Description:utility class,includes some methods which are often used</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-06
 */

package com.afunms.common.util;

import java.io.File;
import java.io.FileInputStream;

import com.afunms.initialize.ResourceCenter;

public class ProjectProperties {
    private static String DBUrl;
    private static String DBUser;
    private static String DBPwd;

    private static String DBUrl_sms;
    private static String DBUser_sms;
    private static String DBPwd_sms;
    
    private static String TFTPserver;
    
    private static String grapesip;
    private static String grapespath;
    private static String subdir;
    private static String filesum;
    private static String filesize;
    
    private static String radarip;
    private static String radarpath;
    private static String radarsubdir;
    private static String radarinterval;
    private static String radarfilesize;
    
    private static String plotip;
    private static String plotpath;
    //private static String radarsubdir;
    private static String plotinterval;
    private static String plotfilesize;
    
    private static String Reporttime;
    private static String DayReporTime;
    
    private static String mailsmtp;
    private static String mailaddr;
    private static String mailpassword;

    
    private static String db_host;
    private static String db_name;
    private static String db_pwd;
    private static String sourceAddr;
    private static String serviceID;
    private static String feeType;
    private static String feeCode;
    
    private static String network1;
    private static String network2;
    private static String network3;
    private static String network4;

    /**
     * @roseuid 3BCE42AE03AB
     */
    private static void init() {
        try {
            java.util.Properties p = new java.util.Properties();
            String filePath=ResourceCenter.getInstance().getSysPath()+"task/DBProjectProertipes.txt";
            //File test = new File(filePath);
            //if (test.)
            FileInputStream fin = new FileInputStream(new File(filePath));
            p.load(fin);
            DBUrl = p.getProperty("DBUrl");
            DBUser = p.getProperty("DBUser");
            DBPwd = p.getProperty("DBPwd");
            
            DBUrl_sms = p.getProperty("DBUrl_sms");
            DBUser_sms = p.getProperty("DBUser_sms");
            DBPwd_sms = p.getProperty("DBPwd_sms");
            
            TFTPserver = p.getProperty("TFTPserver");
            grapesip = p.getProperty("grapesip");
            grapespath = p.getProperty("grapespath");
            subdir = p.getProperty("subdir");
            filesum = p.getProperty("filesum");
            filesize = p.getProperty("filesize");
            
            radarip = p.getProperty("radarip");
            radarpath = p.getProperty("radarpath");
            radarsubdir = p.getProperty("radarsubdir");
            radarinterval = p.getProperty("radarinterval");
            radarfilesize = p.getProperty("radarfilesize");
            
            plotip = p.getProperty("plotip");
            plotpath = p.getProperty("plotpath");
            //radarsubdir = p.getProperty("radarsubdir");
            plotinterval = p.getProperty("plotinterval");
            plotfilesize = p.getProperty("plotfilesize");
            
            Reporttime = p.getProperty("Reporttime");
            DayReporTime = p.getProperty("DayReporTime");
            
            mailsmtp = p.getProperty("mailsmtp");            
            mailaddr = p.getProperty("mailaddr");
            mailpassword = p.getProperty("mailpassword");
            
            db_host = p.getProperty("db_host");
            db_name = p.getProperty("db_name");
            db_pwd = p.getProperty("db_pwd");
            sourceAddr = p.getProperty("sourceAddr");
            serviceID = p.getProperty("serviceID");
            feeType = p.getProperty("feeType");
            feeCode = p.getProperty("feeCode");
            
            network1 = p.getProperty("network1");
            network2 = p.getProperty("network2");
            network3 = p.getProperty("network3");
            network4 = p.getProperty("network4");
            
        } catch (Exception e) {
            System.out.println("读取出错代码配置文件时候出错。");
            e.printStackTrace();
        }

    }
    
    public static String getMailsmtp() {
        if (mailsmtp == null) init();
        return mailsmtp;
    }
    
    
    public static String getMailaddr() {
        if (mailaddr == null) init();
        return mailaddr;
    }
    
    public static String getMailpassword() {
        if (mailpassword == null) init();
        return mailpassword;
    }
    
    
    
    
    public static String getFeeCode() {
        if (feeCode == null) init();
        return feeCode;
    }
    
    
    public static String getFeeType() {
        if (feeType == null) init();
        return feeType;
    }
    
    public static String getServiceID() {
        if (serviceID == null) init();
        return serviceID;
    }
    
    public static String getSourceAddr() {
        if (sourceAddr == null) init();
        return sourceAddr;
    }
    
    
    public static String getDb_pwd() {
        if (db_pwd == null) init();
        return db_pwd;
    }
    
    public static String getDb_name() {
        if (db_name == null) init();
        return db_name;
    }
    
    public static String getDb_host() {
        if (db_host == null) init();
        return db_host;
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
    
    public static String getGrapesip() {
        if (grapesip == null) init();
        return grapesip;
    }
    public static String getGrapespath() {
        if (grapespath == null) init();
        return grapespath;
    }
    
    public static String getSubdir() {
        if (subdir == null) init();
        return subdir;
    }
    public static String getFilesum() {
        if (filesum == null) init();
        return filesum;
    }
    public static String getFilesize() {
        if (filesize == null) init();
        return filesize;
    }
    
    public static String getRadarip() {
        if (radarip == null) init();
        return radarip;
    }
    public static String getRadarpath() {
        if (radarpath == null) init();
        return radarpath;
    }
    
    public static String getRadarsubdir() {
        if (radarsubdir == null) init();
        return radarsubdir;
    }
    public static String getRadarinterval() {
        if (radarinterval == null) init();
        return radarinterval;
    }
    public static String getRadarfilesize() {
        if (radarfilesize == null) init();
        return radarfilesize;
    }
    
    public static String getPlotip() {
        if (plotip == null) init();
        return plotip;
    }
    public static String getPlotpath() {
        if (plotpath == null) init();
        return plotpath;
    }
    
    public static String getPlotinterval() {
        if (plotinterval == null) init();
        return plotinterval;
    }
    public static String getPlotfilesize() {
        if (plotfilesize == null) init();
        return plotfilesize;
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

    public static String getDBUser() {
        if (DBUser == null) init();
        return DBUser;
    }

    public static String getDBPwd() {
        if (DBPwd == null) init();
        return DBPwd;
    }

    //网络分部门
    public static String getNetwork1() {
        if (network1 == null) init();
        return network1;
    }

    public static String getNetwork2() {
        if (network2 == null) init();
        return network2;
    }

    public static String getNetwork3() {
        if (network3 == null) init();
        return network3;
    }

    public static String getNetwork4() {
        if (network4 == null) init();
        return network4;
    }
}
