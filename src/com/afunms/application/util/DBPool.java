/**
 * <p>Description:database connection pool</p>
 * <p>Company: afunms</p>
 * @author miiwill
 * @project afunms
 * @date 2007-1-6
 */

package com.afunms.application.util;

import java.sql.*;
import java.util.*;

import com.afunms.common.util.*;
import com.afunms.polling.*;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.DBNode;

public class DBPool
{
	private static HashMap connMap = new HashMap();
	private static DBPool instance = new DBPool();
	public static DBPool getInstance()
    {       
        return instance;       
    }
		
	public Connection getConnection(int nodeId)
	{
	     String id = String.valueOf(nodeId); //因为nodeId是唯一的，所以可以用它来做key
		 if(connMap.get(id)==null) //if does not exist,create a new connection
	     {
			 Connection conn = createConnection(nodeId);			 
			 connMap.put(id, conn);
			 return conn;
	     }
	     return (Connection)connMap.get(id);
	 }

	 private Connection createConnection(int nodeId)
	 {
		 Node tempNode = PollingEngine.getInstance().getNodeByID(nodeId);
		 if(tempNode==null || !(tempNode instanceof DBNode))
		 {
			 SysLogger.error("DBPool.createConnection:" + nodeId + " does not exist,or it isn't a db node");
			 return null;
		 }
		 
		 DBNode dbNode = (DBNode)tempNode;
		 if(dbNode.getCategory()==52) //与node-category.xml一致
			return connectMySQL(dbNode.getIpAddress(),dbNode.getPort(),dbNode.getDbName(),dbNode.getUser(),dbNode.getPassword());
		 else if(dbNode.getCategory()==53)
			return connectOracle(dbNode.getIpAddress(),dbNode.getPort(),dbNode.getDbName(),dbNode.getUser(),dbNode.getPassword());
		 else if(dbNode.getCategory()==54)
			return connectSQLServer(dbNode.getIpAddress(),dbNode.getPort(),dbNode.getDbName(),dbNode.getUser(),dbNode.getPassword());
		 else if(dbNode.getCategory()==55)
			return connectSybase(dbNode.getIpAddress(),dbNode.getPort(),dbNode.getDbName(),dbNode.getUser(),dbNode.getPassword());
		 else
			return null; 
	 }
	 
	  /**
	   * 连接MySQL(加入mysql-connector-java-3.0.11-stable-bin.jar)
	   */
	 public Connection connectMySQL(String ip,String port,String dbName,String user,String password)
	 {
	     Connection conn = null;
	     try
	     {
	         int timeout = 10000;
	         Driver driver = (Driver)Class.forName("com.mysql.jdbc.Driver").newInstance();
	         String connStr = "jdbc:mysql://" + ip + ":" + port + "/" + dbName + "?socketTimeout=" + timeout +"&connectTimeout=" + timeout;
	         conn = DriverManager.getConnection(connStr, user, password);
	     }
	     catch(Exception ex)
	     {
	    	 System.out.println("user=" + user + ",pwd=" + password);
	    	 SysLogger.error("Error in DBPool,can not connect MySQL_" + ip, ex);
	         conn = null;
	     }
	     return conn;
	 }
	 
	  /**
	   * 连接Oracle(加入classes12.jar)
	   */
	 public Connection connectOracle(String ip,String port,String dbName,String user,String password)
	 {
	     Connection conn = null;
	     try
	     {
	         Driver driver = (Driver)Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
	         String connStr = "jdbc:oracle:thin:@" + ip + ":" + port + ":" + dbName;
	         conn = DriverManager.getConnection(connStr, user, password);
	     }
	     catch(Exception ex)
	     {
	         SysLogger.error("Error in DBPool,can not connect Oracle_" + ip, ex);
	         conn = null;
	     }
	     return conn;
	 }

	  /**
	   * 连接SQL-Server(加入msbase.jar,mssqlserver.jar,msutil.jar;
	   */
	 public Connection connectSQLServer(String ip,String port,String dbName,String user,String password)
	 {
	     Connection conn = null;
	     try
	     {
	         Driver driver = (Driver)Class.forName("com.microsoft.jdbc.sqlserver.SQLServerDriver").newInstance();
	         conn = DriverManager.getConnection("jdbc:microsoft:sqlserver://" + ip + ":" + port + ";DatabaseName=" + dbName, user, password);
	     }
	     catch(Exception ex)
	     {
	         SysLogger.error("Error in DBPool,can not connect MsSQL_" + ip, ex);
	         conn = null;
	     }
	     return conn;
	 }

	  /**
	   * 连接Sybase,引用jConnect.jar
	   */
	 public Connection connectSybase(String ip,String port,String dbName,String user,String password)
	 {
	     Connection conn = null;
	     try
	     {
	         Driver driver = (Driver)Class.forName("com.sybase.jdbc.SybDriver").newInstance();
	         String connStr = "jdbc:sybase:Tds:" + ip + ":" + port + "/" + dbName;
	         conn = DriverManager.getConnection(connStr, user, password);
	     }
	     catch(Exception ex)
	     {
	         SysLogger.error("Error in DBPool,can not connect Sybase_" + ip, ex);
	         conn = null;
	     }
	     return conn;
	 }

	 public void close(Connection conn)
	 {
	     try
	     {
	       if(conn != null || !conn.isClosed())
	          conn.close();
	     }
	     catch(SQLException sex)
	     {}
	 }

	 public void close(Statement stmt,ResultSet rs)
	 {
	     try
	     {
	       if( stmt != null )
	          stmt.close();
	       if( rs != null)
	    	  rs.close(); 
	     }
	     catch(SQLException sex)
	     {}
	 }
	 
	 /**
	  * if db parameters are modified,we should re-create a connection,so remove the old one
	  */
	 public void removeConnect(int id)
	 {
		 if(connMap.get(id)!=null)
		    close((Connection)connMap.get(id));
		 
		 connMap.remove(String.valueOf(id));
	 }
}