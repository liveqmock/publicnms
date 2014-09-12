/**
 * <p>Description:get mysql information</p>
 * <p>Company: afunms</p>
 * @author miiwill
 * @project afunms
 * @date 2007-1-6
 */

package com.afunms.application.util;

import java.sql.*;
import java.util.*;

import com.afunms.common.util.*;

public class MySQLHelper
{
	private Connection conn;
	private String basePath; //基本路径
	private String dataPath; //数据路径
	private String version;  //数据库版本
	private String hostOS;   //服务器操作系统
	private List tablesDetail;  //表明细
	private List sessionsDetail;  //当前连接
	
	public MySQLHelper(Connection conn)
    {
        this.conn = conn;	
    }
	
	public void init(String dbName) 
	{
		Statement stmt = null;
		ResultSet rs = null;
        try
        {
        	stmt = conn.createStatement();
        	rs = stmt.executeQuery("show variables");
        	while(rs.next())
        	{
        		if(rs.getString("variable_name").equals("basedir"))
        		   basePath = rs.getString("value");
        		else if(rs.getString("variable_name").equals("datadir"))
        		   dataPath = rs.getString("value");
        		else if(rs.getString("variable_name").equals("version"))
        		   version = rs.getString("value"); 
        		else if(rs.getString("variable_name").equals("version_compile_os"))
        		   hostOS = rs.getString("value");         		
        	}
        	rs.close();
        	rs = stmt.executeQuery("show table status from " + dbName);
        	tablesDetail = new ArrayList();
        	while(rs.next())
        	{
        		String[] item = new String[4];
        		item[0] = rs.getString("name"); //表名
        		item[1] = rs.getString("rows"); //表行数
        		item[2] = rs.getLong("data_length")/1024 + " k"; //表大小
        		item[3] = rs.getString("create_time").substring(0,16); //创建时间
        		tablesDetail.add(item);
        	}
        	rs.close();
        	rs = stmt.executeQuery("show processlist");
        	sessionsDetail = new ArrayList();
        	while(rs.next())
        	{
        		if(!rs.getString("db").equals(dbName)) continue;
        		
        		String[] item = new String[4];
        		item[0] = rs.getString("user");   //用户
        		item[1] = rs.getString("host");   //主机
        		item[2] = rs.getString("command"); //命令
        		item[3] = rs.getString("time") + " s"; //连接时间
        		sessionsDetail.add(item);
        	}        	
        }
        catch(Exception e)
        {
        	SysLogger.error("Error in MySQLHelper.init(),dbName=" + dbName);
        }
        finally
        {
        	DBPool.getInstance().close(stmt,rs);
        }
	}
	
    public String getBasePath() {
		return basePath;
	}

	public String getDataPath() {
		return dataPath;
	}

	public List getSessionsDetail() {
		return sessionsDetail;
	}

	public List getTablesDetail() {
		return tablesDetail;
	}

	public String getVersion() {
		return version;
	}
	
	public String getHostOS() {
		return hostOS;
	}			
}