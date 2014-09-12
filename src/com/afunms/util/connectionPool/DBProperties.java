package com.afunms.util.connectionPool;

import com.afunms.initialize.ResourceCenter;
import com.afunms.util.ProjectProperties;

public class DBProperties {
    //private static String drivers = "com.microsoft.jdbc.sqlserver.SQLServerDriver";
	
    //private static String drivers = "oracle.jdbc.driver.OracleDriver";
    //private static String logfile = "c:\\connectionlog.txt";
	//private static String logfile = ResourceCenter.getInstance().getSysPath()+"logs/connectionlog.log";
    
    private static String drivers = "org.gjt.mm.mysql.Driver";
    //String strDriver = "org.gjt.mm.mysql.Driver";

    //private static String url = "url=jdbc:oracle:thin:@172.16.10.118:1521:oracle";
    //private static String url = "jdbc:microsoft:sqlserver://172.16.10.130:1433;DatabaseName=pubs;SelectMethod=direct";
    //private static String user = "sa";
    //private static String password = "microsoft";
    private static String url = ProjectProperties.getDBUrl();
    private static String user = ProjectProperties.getDBUser();
    private static String password = ProjectProperties.getDBPwd();

    private static String url_sms = ProjectProperties.getDBUrl_sms();
    private static String user_sms = ProjectProperties.getDBUser_sms();
    private static String password_sms = ProjectProperties.getDBPwd_sms();

    private static int maxconn = Integer.parseInt(ProjectProperties.getMaxconn());
    private static int minconn = Integer.parseInt(ProjectProperties.getMinconn());
    private static int connectCheckOutTimeout = Integer.parseInt(ProjectProperties.getConnectCheckOutTimeout());
    private static int connectUseTimeout = Integer.parseInt(ProjectProperties.getConnectUseTimeout());
    private static int connectUseCount = Integer.parseInt(ProjectProperties.getConnectUseCount());


    /**
     * Access method for the drivers property.
     *
     * @return the current value of the drivers property
     * @roseuid
     */
    public static java.lang.String getDrivers() {
        return drivers;
    }

    /**
     * Access method for the logfile property.
     *
     * @return the current value of the logfile property
     * @roseuid
     */
//    public static java.lang.String getLogfile() {
//        return logfile;
//    }

    /**
     * Access method for the url property.
     *
     * @return the current value of the url property
     * @roseuid
     */
    public static java.lang.String getUrl() {
        return url;
    }

    /**
     * Access method for the user property.
     *
     * @return the current value of the user property
     * @roseuid
     */
    public static java.lang.String getUser() {
        return user;
    }

    /**
     * Access method for the password property.
     *
     * @return the current value of the password property
     * @roseuid
     */
    public static java.lang.String getPassword() {
        return password;
    }
    public static java.lang.String getUrl_sms() {
        return url_sms;
    }

    /**
     * Access method for the user property.
     *
     * @return the current value of the user property
     * @roseuid
     */
    public static java.lang.String getUser_sms() {
        return user_sms;
    }

    /**
     * Access method for the password property.
     *
     * @return the current value of the password property
     * @roseuid
     */
    public static java.lang.String getPassword_sms() {
        return password_sms;
    }

    /**
     * Access method for the maxconn property.
     *
     * @return the current value of the maxconn property
     * @roseuid
     */
    public static int getMaxconn() {
        return maxconn;
    }

    /**
     * Access method for the minconn property.
     *
     * @return the current value of the minconn property
     * @roseuid
     */
    public static int getMinconn() {
        return minconn;
    }

    /**
     * Access method for the connectCheckOutTimeout property.
     *
     * @return the current value of the connectCheckOutTimeout property
     * @roseuid
     */
    public static int getConnectCheckOutTimeout() {
        return connectCheckOutTimeout;
    }

    /**
     * Access method for the connectUseTimeout property.
     *
     * @return the current value of the connectUseTimeout property
     * @roseuid
     */
    public static int getConnectUseTimeout() {
        return connectUseTimeout;
    }

    /**
     * Access method for the connectUseCount property.
     *
     * @return the current value of the connectUseCount property
     * @roseuid
     */
    public static int getConnectUseCount() {
        return connectUseCount;
    }
}
