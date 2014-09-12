/**
 * <p>Description:network utilities</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-11
 */

package com.afunms.common.util;


import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class PersistenceService implements PersistenceServiceable
{
	private static PersistenceService instance = null;
	
	/** JDBC 连接的 URL fda*/

	//private String jdbcURL = "jdbc:mysql://localhost:3306/ens?&useUnicode=true&characterEncoding=GBK";
//	/** 登录用户名 */

	//private String user = "root";
//	/** 登录密码 */

	//private String password = "";
	
	private String jdbcURL = "jdbc:microsoft:sqlserver://127.0.0.1:1433;DatabaseName=dhcc;SelectMethod=CURSOR;charset=GBK";
	private String user = "sa";
	private String password = "sa";
	
//	private String jdbcURL = "jdbc:microsoft:sqlserver://192.168.5.123:1433;DatabaseName=dhcc;SelectMethod=CURSOR;charset=GBK";
//	private String user = "sa";
//	private String password = "sa";
	
	private static String backUpDirectory = "";           // 数据备份目录
	
	private static long alarmDataBkCount = 500000;        // 告警表备份上限记录数 50W
	
	private static long hisDataBkCount   = 2000000;       // 历史表备份上限记录数 200W
	
	private PersistenceService()
	{
	    backUpDirectory = "";
	}
	
	public static PersistenceService getInstance()
	{
		if (instance == null)
		{
			instance = new PersistenceService();
		}
		return instance;
	}

	/*
	 * @see com.cn.dhcc.ENS.Persistence.PersistenceServiceable#AddOneNetworkItem(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String,
	 *      net.sf.hibernate.Session)
	 */
	public void addOneNetworkItem(String idName, String ipAddress,
			String netMask, String community, String writeCommunity,
			String commonNodes, String subNetList, String routerList,
			String switchList, String snmpRespIpAddrList,
			String pingRespIpList, String unResponseIpAddrList,
			String haveAddIpAddrMap, String discoverStatus,
			String containNetId, String isSeedNetwork, String reserved1,
			String reserved2, String reserved3, String reserved4,
			String reserved5, String reserved6, Connection outConnection) throws PersistException
	{
		/** 取得数据库连接对象 */
		Connection connection = null;
		if (outConnection != null)
			connection = outConnection;
		else
			connection = getDbPoolConnection();
        
		Statement stmt = null;
		StringBuffer insertStmt = new StringBuffer("INSERT INTO networkInfor VALUES (");
		insertStmt.append("'" + idName + "',");
		insertStmt.append("'" + ipAddress + "',");
		insertStmt.append("'" + netMask + "',");
		insertStmt.append("'" + community + "',");
		insertStmt.append("'" + writeCommunity + "',");
		insertStmt.append("'" + commonNodes + "',");
		insertStmt.append("'" + subNetList + "',");
		insertStmt.append("'" + routerList + "',");
		insertStmt.append("'" + switchList + "',");
		insertStmt.append("'" + snmpRespIpAddrList + "',");
		insertStmt.append("'" + pingRespIpList + "',");
		insertStmt.append("'" + unResponseIpAddrList + "',");
		insertStmt.append("'" + haveAddIpAddrMap + "',");
		insertStmt.append("'" + discoverStatus + "',");
		insertStmt.append("'" + containNetId + "',");
		insertStmt.append("'" + isSeedNetwork + "',");        
		insertStmt.append("'" + reserved1 + "',");
		insertStmt.append("'" + reserved2 + "',");
		insertStmt.append("'" + reserved3 + "',");
		insertStmt.append("'" + reserved4 + "',");
		insertStmt.append("'" + reserved5 + "',");
		insertStmt.append("'" + reserved6 + "'");        
		insertStmt.append(")");
        
		String deleteStmt = new String("DELETE FROM networkInfor WHERE idName = '" +
				idName + "'");        
		try
		{
			// 先删除该笔记录
			connection.setAutoCommit(false);
			stmt = connection.createStatement();            	
			stmt.execute(deleteStmt.toLowerCase());    		    
			connection.commit();
    		
			// 后插入新的记录
			stmt = connection.createStatement();             
			stmt.execute(insertStmt.toString().toLowerCase());    		
			connection.commit();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			try
			{
				connection.rollback();
			}
			catch(SQLException SqlE)
			{
				SqlE.printStackTrace();
			}
			throw new PersistException("addOneNetworkItem occur errors");
		}
		finally 
		{    
			stmt = null;
			insertStmt = null;
			if (outConnection == null)
			{
				closeDbPoolConnection(connection);            	
			}            
		}
	}
	
	public void addOneAlarmItem(String alarmId, String ipAddress,
			String idName, String alarmDate, String alarmTime,
			String severityoftheAlarm, String sourceOftheAlarm,
			String alarmDetailInfor, String alarmDealDoneState,
			String reserved1, String reserved2, String reserved3,
			Connection outConnection) throws PersistException
	{
		/** 取得数据库连接对象 */
		Connection connection = null;
		if (outConnection != null)
			connection = outConnection;
		else
			connection = getDbPoolConnection();
        
		Statement stmt = null;
		StringBuffer insertStmt = new StringBuffer("INSERT INTO AlarmInfor VALUES ( ");
		insertStmt.append("'" + alarmId + "',");
		insertStmt.append("'" + ipAddress + "',");
		insertStmt.append("'" + idName + "',");
		insertStmt.append("'" + alarmDate + "',");
		insertStmt.append("'" + alarmTime + "',");
		insertStmt.append("'" + severityoftheAlarm + "',");
		insertStmt.append("'" + sourceOftheAlarm + "',");
		insertStmt.append("'" + alarmDetailInfor + "',");
		insertStmt.append("'" + alarmDealDoneState + "',");
		insertStmt.append("'" + reserved1 + "',");        
		insertStmt.append("'" + reserved2 + "',");
		insertStmt.append("'" + reserved3 + "'");
		insertStmt.append(")");
        
		String deleteStmt = new String("DELETE FROM AlarmInfor WHERE alarmId = '" +
				alarmId + "'");        
		try
		{
			// 先删除该笔记录
			connection.setAutoCommit(false);
			stmt = connection.createStatement();            	
			stmt.execute(deleteStmt.toLowerCase());    		    
			connection.commit();
    		
			// 后插入新的记录
			stmt = connection.createStatement();             
			stmt.execute(insertStmt.toString().toLowerCase());    		
			connection.commit();
		}
		catch (Exception e)
		{
			e.printStackTrace(); 
			try
			{
				connection.rollback();
			}
			catch(SQLException SqlE)
			{
				SqlE.printStackTrace();
			}
			throw new PersistException("addOneAlarmItem occur error");
		}
		finally 
		{    
			stmt = null;
			insertStmt = null;
			if (outConnection == null)
			{
				closeDbPoolConnection(connection);            	
			}            
		}
		
//		------把消息交给发送程序----------- 20070625 聂加
		//System.out.println("执行发送短信的程序－－－－－－－－－－－－－－－－－");
		/*
		MobileMessageSevice mobileService = new MobileMessageSevice();
		mobileService.init();
		mobileService.sendMessage(ipAddress+","+alarmDetailInfor, severityoftheAlarm, ipAddress);
		*/
		//System.out.println("执行发送短信的程序－－－－－完毕");
	}
	
	//该方法用来严重告警信息发送短信
	public void addOneMobileAlarmItem(String sqlStr,
			Connection outConnection) throws PersistException
	{
		/*
		if((sqlStr.indexOf("INSERT")!=-1)||(sqlStr.indexOf("insert")!=-1)||(sqlStr.indexOf("Insert")!=-1)){
			String sqlSubStr = sqlStr.substring(sqlStr.indexOf("("),sqlStr.indexOf(")")); 
		    Pattern pt = Pattern.compile("','");
		    System.out.println("1111111111111111111111");
			String [] sqlStrArray = pt.split(sqlSubStr);
			String severity = sqlStrArray[5];
			System.out.println("222222222222222222222");
			if(severity!=null){
				System.out.println("333333333333333333333333");
				int severityInt = Integer.parseInt(severity);
				if(severityInt!=-1){
					System.out.println("44444444444444444444444");
					if(severityInt < 3){ //只发送严重报警信息
						return;
					}
					String alarmInfor = sqlStrArray[7];
					String ipAddr = sqlStrArray[1];
					MobileMessageSevice mobileService = new MobileMessageSevice();
					System.out.println("55555555555555555555");
					mobileService.init();
					System.out.println("66666666666666666666");
					mobileService.sendMessage(ipAddr+","+alarmInfor, severity, ipAddr);
					System.out.println("77777777777777777777");
				}
				
			}
		}
		
		closeDbPoolConnection(outConnection);  //关闭连接
		*/
	}

	public void addOneMonitorItem(String idname, String monitorOid,
			String monitorGroup, String monitorCategory,
			String monitorDeviceType, String monitorDisplayName,
			String monitorValue, String ipAddress, String snmpPort,
			String community, String isSnmpMonitorItem, String thresholdEnable,
			String thresholdLimit, String thresholdCheck,
			String severityoftheAlarm, String consecutivetimes,
			String reserved1, String reserved2, String reserved3,
			String reserved4, Connection outConnection) throws PersistException
	{
		/** 取得数据库连接对象 */
		Connection connection = null;
		if (outConnection != null)
			connection = outConnection;
		else
			connection = getDbPoolConnection();
        
		Statement stmt = null;
		StringBuffer insertStmt = new StringBuffer("INSERT INTO MonitorItemInfor VALUES (");
		insertStmt.append("'" + idname + "',");
		insertStmt.append("'" + monitorGroup + "',");
		insertStmt.append("'" + monitorCategory + "',");
		insertStmt.append("'" + monitorDeviceType + "',");
		insertStmt.append("'" + monitorDisplayName + "',");
		insertStmt.append("'" + monitorValue + "',");
		insertStmt.append("'" + monitorOid + "',");
		insertStmt.append("'" + ipAddress + "',");
		insertStmt.append("'" + snmpPort + "',");
		insertStmt.append("'" + community + "',");        
		insertStmt.append("'" + isSnmpMonitorItem + "',");
		insertStmt.append("'" + thresholdEnable + "',");
		insertStmt.append("'" + thresholdLimit + "',");        
		insertStmt.append("'" + thresholdCheck + "',");
		insertStmt.append("'" + severityoftheAlarm + "',");
		insertStmt.append("'" + consecutivetimes + "',");                
		insertStmt.append("'" + reserved1 + "',");        
		insertStmt.append("'" + reserved2 + "',");
		insertStmt.append("'" + reserved3 + "',");
		insertStmt.append("'" + reserved4 + "'");
		insertStmt.append(")");
                
		String deleteStmt = new String("DELETE FROM MonitorItemInfor WHERE idName = '"
				+idname +"' and monitorGroup ='"
				+monitorGroup+"' and monitorDisplayName ='"
				+monitorDisplayName+"' and monitorOid ='"+monitorOid+"'"); 
		//System.out.println("插入到monitoritem语句为:"+insertStmt.toString().toLowerCase());
		try
		{
			// 先删除该笔记录
			connection.setAutoCommit(false);
			stmt = connection.createStatement();            	
			stmt.execute(deleteStmt.toLowerCase());    		    
			connection.commit();
    		
			// 后插入新的记录
			stmt = connection.createStatement();             
			stmt.execute(insertStmt.toString().toLowerCase());    		
			connection.commit();
		}
		catch (Exception e)
		{
			e.printStackTrace(); 
			try
			{
				connection.rollback();
			}
			catch(SQLException SqlE)
			{
				SqlE.printStackTrace();
			}
			throw new PersistException("addOneMonitorItem occur error");
		}
		finally 
		{    
			stmt = null;
			insertStmt = null;
			if (outConnection == null)
			{
				closeDbPoolConnection(connection);            	
			}            
		}
	}

	public void addOneNodeObjItem(String idName, String sysObjectIdValue,
			String sysName, String sysDescr, String ipAddress, String netMask,
			String community, String writeCommunity, String snmpVersion,
			String snmpPort, String containNetId, String snmpSupport,
			String deviceCategory, String deviceType,
			String performDataSnmpTimeOut, String reserved1, String reserved2,
			String reserved3, String reserved4, String reserved5, String reserved6,
			Connection outConnection) throws PersistException
	{	    
		/** 取得数据库连接对象 */
		Connection connection = null;
		if (outConnection != null)
		{
			connection = outConnection;
		}
		else
			connection = getDbPoolConnection();
        
		Statement stmt = null;
		StringBuffer insertStmt = new StringBuffer("INSERT INTO NodeObjInfor VALUES ( ");
		insertStmt.append("'" + idName + "',");
		insertStmt.append("'" + sysObjectIdValue + "',");
		insertStmt.append("'" + sysName + "',");
		insertStmt.append("'" + sysDescr + "',");
		insertStmt.append("'" + ipAddress + "',");
		insertStmt.append("'" + netMask + "',");
		insertStmt.append("'" + community + "',");
		insertStmt.append("'" + writeCommunity + "',");
		insertStmt.append("'" + snmpVersion + "',");
		insertStmt.append("'" + snmpPort + "',");        
		insertStmt.append("'" + containNetId + "',");
		insertStmt.append("'" + snmpSupport + "',");
		insertStmt.append("'" + deviceCategory + "',");
		insertStmt.append("'" + deviceType + "',");
		insertStmt.append("'" + performDataSnmpTimeOut + "',");
		insertStmt.append("'" + reserved1 + "',");
		insertStmt.append("'" + reserved2 + "',");
		insertStmt.append("'" + reserved3 + "',");
		insertStmt.append("'" + reserved4 + "',");
		insertStmt.append("'" + reserved5 + "',");
		insertStmt.append("'" + reserved6 + "'");
		insertStmt.append(")");
        
		String deleteStmt = new String("DELETE FROM NodeObjInfor WHERE idname = '" +
				idName + "'");        
		try
		{
			// 先删除该笔记录
			connection.setAutoCommit(false);
			stmt = connection.createStatement();            	
			stmt.execute(deleteStmt.toLowerCase());    		    
			connection.commit();
    		
			// 后插入新的记录
			stmt = connection.createStatement();             
			stmt.execute(insertStmt.toString().toLowerCase());    		
			connection.commit();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			try
			{
				connection.rollback();
			}
			catch(SQLException SqlE)
			{
				SqlE.printStackTrace();
			}
			throw new PersistException("addOneNodeObjItem occur error");
		}
		finally 
		{    
			stmt = null;
			insertStmt = null;
			if (outConnection == null)
			{
				closeDbPoolConnection(connection);            	
			}            
		}
                
	}

	public void addOneRouterItem(String idName, String sysObjectIdValue,
			String sysName, String sysDescr, String ipAddress, String netMask,
			String community, String writeCommunity, String snmpSupport,
			String snmpVersion, String snmpPort, String containNetId,
			String deviceCategory, String deviceType, String theLayer,
			String performDataSnmpTimeOut, String reserved1, String reserved2,
			String reserved3, String reserved4, String reserved5, String reserved6,
			Connection outConnection) throws PersistException 
	{
		/** 取得数据库连接对象 */
		Connection connection = null;
		if (outConnection != null)
			connection = outConnection;
		else
			connection = getDbPoolConnection();
        
		Statement stmt = null;
		StringBuffer insertStmt = new StringBuffer("INSERT INTO RouterInfor VALUES ( ");
		insertStmt.append("'" + idName + "',");
		insertStmt.append("'" + sysObjectIdValue + "',");
		insertStmt.append("'" + sysName + "',");
		insertStmt.append("'" + sysDescr + "',");
		insertStmt.append("'" + ipAddress + "',");
		insertStmt.append("'" + netMask + "',");
		insertStmt.append("'" + community + "',");
		insertStmt.append("'" + writeCommunity + "',");
		insertStmt.append("'" + snmpSupport + "',");
		insertStmt.append("'" + snmpVersion + "',");
		insertStmt.append("'" + snmpPort + "',");
		insertStmt.append("'" + containNetId + "',");
		insertStmt.append("'" + deviceCategory + "',");
		insertStmt.append("'" + deviceType + "',");
		insertStmt.append("'" + theLayer + "',");
		insertStmt.append("'" + performDataSnmpTimeOut + "',");
		insertStmt.append("'" + reserved1 + "',");
		insertStmt.append("'" + reserved2 + "',");
		insertStmt.append("'" + reserved3 + "',");
		insertStmt.append("'" + reserved4 + "',");
		insertStmt.append("'" + reserved5 + "',");				
		insertStmt.append("'" + reserved6 + "'");
		insertStmt.append(")");
        
		String deleteStmt = new String("DELETE FROM RouterInfor WHERE idName = '" +
				idName + "'");        
		try
		{
			// 先删除该笔记录
			connection.setAutoCommit(false);
			stmt = connection.createStatement();            	
			stmt.execute(deleteStmt.toLowerCase());    		    
			connection.commit();
    		
			// 后插入新的记录
			stmt = connection.createStatement();             
			stmt.execute(insertStmt.toString().toLowerCase());    		
			connection.commit();
		}
		catch (Exception e)
		{
			e.printStackTrace();  
			try
			{
				connection.rollback();
			}
			catch(SQLException SqlE)
			{
				SqlE.printStackTrace();
			}
			throw new PersistException("addOneRouterItem occur error");
		}
		finally 
		{    
			stmt = null;
			insertStmt = null;
			if (outConnection == null)
			{
				closeDbPoolConnection(connection);            	
			}            
		}
	}

	public void addOneSwitchItem(String idName, String sysObjectIdValue,
			String sysName, String sysDescr, String ipAddress, String netMask,
			String community, String writeCommunity, String snmpVersion,
			String snmpPort, String containNetId, String snmpSupport,
			String deviceCategory, String deviceType,
			String performDataSnmpTimeOut, String reserved1, String reserved2,
			String reserved3, String reserved4, String reserved5, String reserved6,
			Connection outConnection) throws PersistException
	{
		/** 取得数据库连接对象 */
		Connection connection = null;
		if (outConnection != null)
			connection = outConnection;
		else
			connection = getDbPoolConnection();
        
		Statement stmt = null;
		StringBuffer insertStmt = new StringBuffer("INSERT INTO switchInfor VALUES ( ");
		insertStmt.append("'" + idName + "',");
		insertStmt.append("'" + sysObjectIdValue + "',");
		insertStmt.append("'" + sysName + "',");
		insertStmt.append("'" + sysDescr + "',");
		insertStmt.append("'" + ipAddress + "',");
		insertStmt.append("'" + netMask + "',");
		insertStmt.append("'" + community + "',");
		insertStmt.append("'" + writeCommunity + "',");        
		insertStmt.append("'" + snmpVersion + "',");
		insertStmt.append("'" + snmpPort + "',");
		insertStmt.append("'" + containNetId + "',");
		insertStmt.append("'" + snmpSupport + "',");
		insertStmt.append("'" + deviceCategory + "',");
		insertStmt.append("'" + deviceType + "',");        
		insertStmt.append("'" + performDataSnmpTimeOut + "',");
		insertStmt.append("'" + reserved1 + "',");
		insertStmt.append("'" + reserved2 + "',");
		insertStmt.append("'" + reserved3 + "',");
		insertStmt.append("'" + reserved4 + "',");
		insertStmt.append("'" + reserved5 + "',");		
		insertStmt.append("'" + reserved6 + "'");
		insertStmt.append(")");
        
		String deleteStmt = new String("DELETE FROM switchInfor WHERE idName = '" +
				idName + "'");        
		try
		{
			// 先删除该笔记录
			connection.setAutoCommit(false);
			stmt = connection.createStatement();            	
			stmt.execute(deleteStmt.toLowerCase());    		    
			connection.commit();
    		
			// 后插入新的记录
			stmt = connection.createStatement();             
			stmt.execute(insertStmt.toString().toLowerCase());    		
			connection.commit();
		}
		catch (Exception e)
		{
			e.printStackTrace();  
			try
			{
				connection.rollback();
			}
			catch(SQLException SqlE)
			{
				SqlE.printStackTrace();
			}
			throw new PersistException("addOneSwitchItem occur error");
		}
		finally 
		{    
			stmt = null;
			insertStmt = null;
			if (outConnection == null)
			{
				closeDbPoolConnection(connection);            	
			}            
		}
	}
	
	public void addOneMonitorItemCollet(String colletIpaddress, String monitorColletDisplayName,
					   String monitorColletOid, String monitorColletValue, String monitorColletDate, 
					   String monitorColletTime, String reserved1, String reserved2, String reserved3, 
					   String reserved4, Connection outConnection) throws PersistException
	{
		/** 取得数据库连接对象 */
		Connection connection = null;
		if (outConnection != null)
			connection = outConnection;
		else
			connection = getDbPoolConnection();
        
		Statement stmt = null;
		StringBuffer insertStmt = new StringBuffer("INSERT INTO monitorItemColletInfor VALUES (");
		insertStmt.append("'" + colletIpaddress + "',");
		insertStmt.append("'" + monitorColletDisplayName + "',");
		insertStmt.append("'" + monitorColletOid + "',");
		insertStmt.append("'" + monitorColletValue + "',");
		insertStmt.append("'" + monitorColletDate + "',");
		insertStmt.append("'" + monitorColletTime + "',");
		insertStmt.append("'" + reserved1 + "',");
		insertStmt.append("'" + reserved2 + "',");
		insertStmt.append("'" + reserved3 + "',");
		insertStmt.append("'" + reserved4 + "'");
		insertStmt.append(")");
		             
		try
		{
			connection.setAutoCommit(false);			
            
			// 插入新的记录
			stmt = connection.createStatement();             
			stmt.execute(insertStmt.toString().toLowerCase());    		
			connection.commit();
		}
		catch (Exception e)
		{
			e.printStackTrace();  
			try
			{
				connection.rollback();
			}
			catch(SQLException SqlE)
			{
				SqlE.printStackTrace();
			}
			throw new PersistException("addOneMonitorItemCollet occur error");
		}
		finally 
		{    
			stmt = null;
			insertStmt = null;
			if (outConnection == null)
			{
				closeDbPoolConnection(connection);            	
			}            
		}	    
	}
	

	public void addOneDispatchNet(String nodeIp, String insertTime, int timeForSum, 
				   String type, Connection outConnection) throws PersistException
		{
			/** 取得数据库连接对象 */
			Connection connection = null;
			if (outConnection != null)
				connection = outConnection;
			else
				connection = getDbPoolConnection();
			
			Statement stmt = null;
			StringBuffer insertStmt = new StringBuffer("INSERT INTO dispatchNetTmp VALUES (");
			insertStmt.append("'" + nodeIp + "',");
			insertStmt.append("'" + insertTime + "',");
			insertStmt.append("" + timeForSum + ",");
			insertStmt.append("'" + type + "'");
			insertStmt.append(")");
			          
			try
			{
				connection.setAutoCommit(false);			
			 
				// 插入新的记录
				stmt = connection.createStatement();             
				stmt.execute(insertStmt.toString().toLowerCase());    		
				connection.commit();
			}
			catch (Exception e)
			{
				e.printStackTrace();  
				try
				{
					connection.rollback();
				}
				catch(SQLException SqlE)
				{
					SqlE.printStackTrace();
				}
				throw new PersistException("addOneDispatchNet occur error");
			}
			finally 
			{    
				stmt = null;
				insertStmt = null;
				if (outConnection == null)
				{
					closeDbPoolConnection(connection);            	
				}            
			}	    
		}
	
	public void addOneProducerItem(String producerName, String reserved1, String reserved2, 
				  String reserved3, String reserved4, Connection outConnection)
				  throws PersistException
	{
		/** 取得数据库连接对象 */
		Connection connection = null;
		if (outConnection != null)
			connection = outConnection;
		else
			connection = getDbPoolConnection();
       
		Statement stmt = null;
		StringBuffer insertStmt = new StringBuffer("INSERT INTO producerInfor VALUES (");
		insertStmt.append("'" + producerName + "',");       
		insertStmt.append("'" + reserved1 + "',");
		insertStmt.append("'" + reserved2 + "',");
		insertStmt.append("'" + reserved3 + "',");
		insertStmt.append("'" + reserved4 + "'");
		insertStmt.append(")");
       
		String deleteStmt = new String("DELETE FROM producerInfor WHERE producerName = '"
			   + producerName +"'");        
		try
		{           
			// 先删除该笔记录
			connection.setAutoCommit(false);
			stmt = connection.createStatement();            	
			stmt.execute(deleteStmt.toLowerCase());    		    
			connection.commit();
   		
			// 后插入新的记录
			stmt = connection.createStatement();             
			stmt.execute(insertStmt.toString().toLowerCase());    		
			connection.commit();
		}
		catch (Exception e)
		{
			e.printStackTrace();  
			try
			{
				connection.rollback();
			}
			catch(SQLException SqlE)
			{
				SqlE.printStackTrace();
			}
			throw new PersistException("addOneProducerItem occur error");
		}
		finally 
		{    
			stmt = null;
			insertStmt = null;
			if (outConnection == null)
			{
				closeDbPoolConnection(connection);            	
			}            
		}
	}
		
	public void addOneDeviceTypeCatalogItem(String objIdValue, String typeDesc, String picture, 
					String category, String producer, String reserved1, String reserved2, String reserved3, 
					String reserved4, Connection outConnection) throws PersistException
	{
		/** 取得数据库连接对象 */
		Connection connection = null;
		if (outConnection != null)
			connection = outConnection;
		else
			connection = getDbPoolConnection();
       
		Statement stmt = null;
	   StringBuffer insertStmt = new StringBuffer("INSERT INTO DeviceTypeCatalogInfor VALUES ( ");
	   insertStmt.append("'" + objIdValue + "',");
	   insertStmt.append("'" + typeDesc + "',");
	   insertStmt.append("'" + picture + "',");
	   insertStmt.append("'" + category + "',");
	   insertStmt.append("'" + producer + "',");
	   insertStmt.append("'" + reserved1 + "',");
	   insertStmt.append("'" + reserved2 + "',");
	   insertStmt.append("'" + reserved3 + "',");
	   insertStmt.append("'" + reserved4 + "'");
	   insertStmt.append(")");
       
	   String deleteStmt = new String("DELETE FROM DeviceTypeCatalogInfor WHERE objIdValue = '" +
			objIdValue + "'");        
	   try
	   {
		   // 先删除该笔记录
		   connection.setAutoCommit(false);
		   stmt = connection.createStatement();            	
		   stmt.execute(deleteStmt.toLowerCase());    		    
		   connection.commit();
   		
		   // 后插入新的记录
		   stmt = connection.createStatement();             
		   stmt.execute(insertStmt.toString().toLowerCase());    		
		   connection.commit();
	   }
	   catch (Exception e)
	   {    
	       e.printStackTrace();
		   try
		   {
			   connection.rollback();
		   }
		   catch(SQLException SqlE)
		   {
			   SqlE.printStackTrace();
		   }		   
		   throw new PersistException("addOneDeviceTypeCatalogItem occur error");
	   }
	   finally 
	   {    
		   stmt = null;
		   insertStmt = null;
		   if (outConnection == null)
		   {
			   closeDbPoolConnection(connection);            	
		   }            
	   }
   }
		
	public void addOneSpecSnmpCommunItem(String ipaddress, String community,
					String writeCommunity, String snmpPort,
					String reserved1, String reserved2, String reserved3, 
					String reserved4, String reserved5, String reserved6,
					Connection outConnection) throws PersistException 
	{
		/** 取得数据库连接对象 */
		Connection connection = null;
		if (outConnection != null)
			connection = outConnection;
		else
			connection = getDbPoolConnection();
        
		Statement stmt = null;
		StringBuffer insertStmt = new StringBuffer("INSERT INTO specSnmpCommunInfor VALUES (");
		insertStmt.append("'" + ipaddress + "',");
		insertStmt.append("'" + community + "',");
		insertStmt.append("'" + writeCommunity + "',");
		insertStmt.append("'" + snmpPort + "',");        
		insertStmt.append("'" + reserved1 + "',");
		insertStmt.append("'" + reserved2 + "',");
		insertStmt.append("'" + reserved3 + "',");
		insertStmt.append("'" + reserved4 + "',");
		insertStmt.append("'" + reserved5 + "',");
		insertStmt.append("'" + reserved6 + "'");
		insertStmt.append(")");
        
		String deleteStmt = new String("DELETE FROM specSnmpCommunInfor WHERE ipaddress = '" +
				ipaddress + "'");        
		try
		{
			// 先删除该笔记录
			connection.setAutoCommit(false);
			stmt = connection.createStatement();            	
			stmt.execute(deleteStmt.toLowerCase());    		    
			connection.commit();
    		
			// 后插入新的记录
			stmt = connection.createStatement();             
			stmt.execute(insertStmt.toString().toLowerCase());    		
			connection.commit();
		}
		catch (Exception e)
		{    
		    e.printStackTrace();
			try
			{
				connection.rollback();
			}
			catch(SQLException SqlE)
			{
				SqlE.printStackTrace();
			}			
			throw new PersistException("addOneSpecSnmpCommunItem occur error");
		}
		finally 
		{    
			stmt = null;
			insertStmt = null;
			if (outConnection == null)
			{
				closeDbPoolConnection(connection);            	
			}            
		}
	}
	
	public void addOneIfCollectItem(String idName, String ifindex, String ifutiliRate, String ifinErrorsRate, 
					String ifoutErrorsRate, String ifinDiscardsRate, String ifoutDiscardsRate, String reserved1, 
					String reserved2, String reserved3, String reserved4, Connection outConnection)
				throws PersistException
	{
		/** 取得数据库连接对象 */
	   Connection connection = null;
	   if (outConnection != null)
		   connection = outConnection;
	   else
		   connection = getDbPoolConnection();
        
	   Statement stmt = null;
	   StringBuffer insertStmt = new StringBuffer("INSERT INTO ifCollectInfor VALUES (");
	   insertStmt.append("'" + idName + "',");
	   insertStmt.append("'" + ifindex + "',");
	   insertStmt.append("'" + ifutiliRate + "',");
	   insertStmt.append("'" + ifinErrorsRate + "',");  
	   insertStmt.append("'" + ifoutErrorsRate + "',");
	   insertStmt.append("'" + ifinDiscardsRate + "',");
	   insertStmt.append("'" + ifoutDiscardsRate + "',");      
	   insertStmt.append("'" + reserved1 + "',");
	   insertStmt.append("'" + reserved2 + "',");
	   insertStmt.append("'" + reserved3 + "',");
	   insertStmt.append("'" + reserved4 + "'");
	   insertStmt.append(")");
	   	     	          
	   try
	   {		   
		   connection.setAutoCommit(false);
		   
		   // 插入新的记录
		   stmt = connection.createStatement();             
		   stmt.execute(insertStmt.toString().toLowerCase());    		
		   connection.commit();
	   }
	   catch (Exception e)
	   {
	       e.printStackTrace();
		   try
		   {
			   connection.rollback();
		   }
		   catch(SQLException SqlE)
		   {
			   SqlE.printStackTrace();
		   }		   
		   throw new PersistException("addOneIfCollectItem occur error");
	   }
	   finally 
	   {    
		   stmt = null;
		   insertStmt = null;
		   if (outConnection == null)
		   {
			   closeDbPoolConnection(connection);            	
		   }            
	   }		
	}
	
	public void addOneEnsCoreParamInfor(String paramName, String paramValue, 
	        String paramType, String reserved1, String reserved2,
            String reserved3, String reserved4, String reserved5, Connection outConnection)
				throws PersistException
	{
		   /** 取得数据库连接对象 */
		   Connection connection = null;
		   if (outConnection != null)
			   connection = outConnection;
		   else
			   connection = getDbPoolConnection();
	        
		   Statement stmt = null;
		   StringBuffer insertStmt = new StringBuffer("INSERT INTO ensCoreParamInfor VALUES (");
		   insertStmt.append("'" + paramName + "',");
		   insertStmt.append("'" + paramValue + "',");
		   insertStmt.append("'" + paramType + "',");
		   insertStmt.append("'" + reserved1 + "',");
		   insertStmt.append("'" + reserved2 + "',");
		   insertStmt.append("'" + reserved3 + "',");
		   insertStmt.append("'" + reserved4 + "',");
		   insertStmt.append("'" + reserved5 + "'");
		   insertStmt.append(")");
		   String deleteStmt = new String("DELETE FROM ensCoreParamInfor WHERE paramName = '" +
		   		paramName + "'");  
		   try
		   {
			   // 先删除该笔记录
			   connection.setAutoCommit(false);
			   stmt = connection.createStatement();            	
			   stmt.execute(deleteStmt.toLowerCase());    		    
			   connection.commit();	 
			   // 插入新的记录
			   stmt = connection.createStatement(); 			       
			   stmt.execute(insertStmt.toString().toLowerCase());			   
			   connection.commit();
		   }
		   catch (Exception e)
		   {            
		       e.printStackTrace();
			   try
			   {
				   connection.rollback();
			   }
			   catch(SQLException SqlE)
			   {
				   SqlE.printStackTrace();
			   }			   
			   throw new PersistException("addOneEnsCoreParamInfor occur error");
		   }
		   finally 
		   {    
			   stmt = null;
			   insertStmt = null;
			   if (outConnection == null)
			   {
				   closeDbPoolConnection(connection);            	
			   }            
		   }		
	}
		
	public void addOneIfEntryInfor(String idname, String ifindex, String iftype, String ifspeed, String ifdescr, 
			         String Reserved1, String Reserved2, String Reserved3, String Reserved4,
					Connection outConnection) throws PersistException
	{
		/** 取得数据库连接对象 */
		   Connection connection = null;
		   if (outConnection != null)
			   connection = outConnection;
		   else
			   connection = getDbPoolConnection();
	        
		   Statement stmt = null;
		   
		   StringBuffer sb = new StringBuffer();    //把字段中的100,000中的","去掉;
		   for (int i = 0; i < ifspeed.length(); i++) {   
			   if(!(ifspeed.charAt(i)==',')){
				   sb.append(ifspeed.charAt(i));		   
			   }
		   }
		   ifspeed = sb.toString();
		   
		   StringBuffer insertStmt = new StringBuffer("INSERT INTO ifEntryInfor VALUES (");
		   insertStmt.append("'" + idname + "',");
		   insertStmt.append("'" + ifindex + "',");     
		   insertStmt.append("'" + iftype + "',");
		   insertStmt.append("'" + ifspeed + "',");
		   insertStmt.append("'" + ifdescr + "',");
		   insertStmt.append("'" + Reserved1 + "',");
		   insertStmt.append("'" + Reserved2 + "',");
		   insertStmt.append("'" + Reserved3 + "',");
		   insertStmt.append("'" + Reserved4 + "'");
		   insertStmt.append(")");
	        
		   String deleteStmt = new String("DELETE FROM ifEntryInfor WHERE idname = '" +
		           idname + "' and ifindex ='" + ifindex+"'");        
			try
			{
				// 先删除该笔记录
				connection.setAutoCommit(false);
				stmt = connection.createStatement();            	
				stmt.execute(deleteStmt.toLowerCase());    		    
				connection.commit();	    					
			   
				// 插入新的记录
				stmt = connection.createStatement();             
				stmt.execute(insertStmt.toString().toLowerCase());    		
				connection.commit();
		   }
		   catch (Exception e)
		   {            
		       e.printStackTrace();
			   try
			   {
				   connection.rollback();
			   }
			   catch(SQLException SqlE)
			   {
				   SqlE.printStackTrace();
			   }			   
			   throw new PersistException("addOneIfEntryInfor occur error");
		   }
		   finally 
		   {    
			   stmt = null;
			   insertStmt = null;
			   if (outConnection == null)
			   {
				   closeDbPoolConnection(connection);            	
			   }            
		   }			    
	}
	
	/* (non-Javadoc)
	 * @see com.dhcc.ens.persistence.PersistenceServiceable#addOneNodeParamInfor(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.sql.Connection)
	 */
	public void addOneNodeParamInfor(String idname, String ipaddress, String protocolDescr, String port, 
			          String useName, String password, String prompt, String reserved1, String reserved2, 
					  String reserved3, String reserved4, String reserved5, Connection outConnection)
				throws PersistException
	{
		/** 取得数据库连接对象 */
		   Connection connection = null;
		   if (outConnection != null)
			   connection = outConnection;
		   else
			   connection = getDbPoolConnection();
	        
		   Statement stmt = null;
		   StringBuffer insertStmt = new StringBuffer("INSERT INTO nodeParamInfor VALUES (");
		   insertStmt.append("'" + idname + "',");
		   insertStmt.append("'" + ipaddress + "',");     
		   insertStmt.append("'" + protocolDescr + "',");
		   insertStmt.append("'" + port + "',");
		   insertStmt.append("'" + useName + "',");
		   insertStmt.append("'" + password + "',");
		   insertStmt.append("'" + prompt + "',");
		   insertStmt.append("'" + reserved1 + "',");
		   insertStmt.append("'" + reserved2 + "',");
		   insertStmt.append("'" + reserved3 + "',");
		   insertStmt.append("'" + reserved4 + "',");
		   insertStmt.append("'" + reserved5 + "'");
		   insertStmt.append(")");
	        
		   String deleteStmt = new String("DELETE FROM nodeParamInfor WHERE idname = '" +
		           idname+"' or ipAddress ='"+ipaddress+"'");        
			try
			{
				// 先删除该笔记录
				connection.setAutoCommit(false);
				stmt = connection.createStatement();            	
				stmt.execute(deleteStmt.toLowerCase());    		    
				connection.commit();	    					
			   
				// 插入新的记录
				stmt = connection.createStatement();             
				stmt.execute(insertStmt.toString().toLowerCase());    		
				connection.commit();
		   }
		   catch (Exception e)
		   {    
		       e.printStackTrace();
			   try
			   {
				   connection.rollback();
			   }
			   catch(SQLException SqlE)
			   {
				   SqlE.printStackTrace();
			   }			   
			   throw new PersistException("addOneIfEntryInfor occur error");
		   }
		   finally 
		   {    
			   stmt = null;
			   insertStmt = null;
			   if (outConnection == null)
			   {
				   closeDbPoolConnection(connection);            	
			   }            
		   }		
	}
	
	/* (non-Javadoc)
	 * @see com.dhcc.ens.persistence.PersistenceServiceable#MonitorItemColletTmpInfor(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.sql.Connection)
	 */
	public void addOneMonitorTmpItemCollet(String colletIpaddress, String monitorColletDisplayName, String monitorColletOid, String monitorColletValue, String monitorColletDate, String monitorColletTime, String reserved1, String reserved2, String reserved3, String reserved4, Connection outConnection) 
			throws PersistException
	{	    
		/** 取得数据库连接对象 */
		Connection connection = null;
		if (outConnection != null)
			connection = outConnection;
		else
			connection = getDbPoolConnection();
        
		Statement stmt = null;
		StringBuffer insertStmt = new StringBuffer("INSERT INTO monitorItemColletTmpInfor VALUES (");
		insertStmt.append("'" + colletIpaddress + "',");
		insertStmt.append("'" + monitorColletDisplayName + "',");
		insertStmt.append("'" + monitorColletOid + "',");
		insertStmt.append("'" + monitorColletValue + "',");
		insertStmt.append("'" + monitorColletDate + "',");
		insertStmt.append("'" + monitorColletTime + "',");
		insertStmt.append("'" + reserved1 + "',");
		insertStmt.append("'" + reserved2 + "',");
		insertStmt.append("'" + reserved3 + "',");
		insertStmt.append("'" + reserved4 + "'");
		insertStmt.append(")");
				               
		try
		{            
			connection.setAutoCommit(false);
			
			// 插入新的记录
			stmt = connection.createStatement();             
			stmt.execute(insertStmt.toString().toLowerCase());    		
			connection.commit();
		}
		catch (Exception e)
		{
		    //DiscoverOper.logService.info("[20060525]debug 1.1 Error man: addOneMonitorTmpItemCollet IpAddress is "+colletIpaddress);
			e.printStackTrace();  
			try
			{
				connection.rollback();
			}
			catch(SQLException SqlE)
			{
				SqlE.printStackTrace();
			}
			throw new PersistException("addOneMonitorTmpItemCollet occur error");
		}
		finally 
		{    
			stmt = null;
			insertStmt = null;
			if (outConnection == null)
			{
				closeDbPoolConnection(connection);            	
			}            
		}	    
		
	}

	/* (non-Javadoc)
	 * @see com.dhcc.ens.persistence.PersistenceServiceable#IfCollectTmpTable(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.sql.Connection)
	 */
	public void addOneIfCollectTmpItem(String idName, String ifindex, String ifutiliRate, String ifinErrorsRate, String ifoutErrorsRate, String ifinDiscardsRate, String ifoutDiscardsRate, String reserved1, String reserved2, String reserved3, String reserved4, Connection outConnection)
			throws PersistException
	{	    
		/** 取得数据库连接对象 */
		   Connection connection = null;
		   if (outConnection != null)
			   connection = outConnection;
		   else
			   connection = getDbPoolConnection();
		   Statement stmt = null;
		   StringBuffer insertStmt = new StringBuffer("INSERT INTO ifCollectTmpTable VALUES (");
		   insertStmt.append("'" + idName + "',");
		   insertStmt.append("'" + ifindex + "',");
		   insertStmt.append("'" + ifutiliRate + "',");
		   insertStmt.append("'" + ifinErrorsRate + "',");  
		   insertStmt.append("'" + ifoutErrorsRate + "',");
		   insertStmt.append("'" + ifinDiscardsRate + "',");
		   insertStmt.append("'" + ifoutDiscardsRate + "',");      
		   insertStmt.append("'" + reserved1 + "',");
		   insertStmt.append("'" + reserved2 + "',");
		   insertStmt.append("'" + reserved3 + "',");
		   insertStmt.append("'" + reserved4 + "'");
		   insertStmt.append(")");
		   		   	          
		   try
		   {
			   connection.setAutoCommit(false);
			   
			   // 插入新的记录
			   stmt = connection.createStatement();             
			   stmt.execute(insertStmt.toString().toLowerCase());    		
			   connection.commit();
		   }
		   catch (Exception e)
		   {    
		       //DiscoverOper.logService.info("[20060525]debug 1.1 Error le man: addOneIfCollectTmpItem "+idName
			   //         +" IpAddress is "+reserved3);
		       e.printStackTrace();
			   try
			   {
				   connection.rollback();
			   }
			   catch(SQLException SqlE)
			   {
				   SqlE.printStackTrace();
			   }			   
			   throw new PersistException("addOneIfCollectTmpItem occur error");
		   }
		   finally 
		   {    
			   stmt = null;
			   insertStmt = null;
			   if (outConnection == null)
			   {
				   closeDbPoolConnection(connection);            	
			   }            
		   }					
	}


	/* (non-Javadoc)
	 * @see com.dhcc.ens.persistence.PersistenceServiceable#addOneApplicInfor(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.sql.Connection)
	 */
	public void addOneApplicInfor(String applicName, String nodeId, String applicType, String applicIpAddress, 
			              String applicNetMask, String loginUser, String loginPassword, String applicPort, 
						  String applicVersion, String dbOrServiceName, String isSSL, String reserved1, 
						  String reserved2, String reserved3, String reserved4, String reserved5, 
						  Connection outConnection)
			throws PersistException
	{
		/** 取得数据库连接对象 */
		   Connection connection = null;
		   if (outConnection != null)
			   connection = outConnection;
		   else
			   connection = getDbPoolConnection();
	        
		   Statement stmt = null;
		   StringBuffer insertStmt = new StringBuffer("INSERT INTO applicInfor VALUES (");
		   insertStmt.append("'" + applicName + "',");
		   insertStmt.append("'" + nodeId + "',");
		   insertStmt.append("'" + applicType + "',");
		   insertStmt.append("'" + applicIpAddress + "',");  
		   insertStmt.append("'" + applicNetMask + "',");
		   insertStmt.append("'" + loginUser + "',");
		   insertStmt.append("'" + loginPassword + "',");      
		   insertStmt.append("'" + applicPort + "',");
		   insertStmt.append("'" + applicVersion + "',");
		   insertStmt.append("'" + dbOrServiceName + "',");
		   insertStmt.append("'" + isSSL + "',");
		   insertStmt.append("'" + reserved1 + "',");
		   insertStmt.append("'" + reserved2 + "',");
		   insertStmt.append("'" + reserved3 + "',");
		   insertStmt.append("'" + reserved4 + "',");
		   insertStmt.append("'" + reserved5 + "'");
		   insertStmt.append(")");
		   
		   String deleteStmt = new String("DELETE FROM applicInfor WHERE ApplicName = '" +
		           applicName+"'");     	          
		   try
		   {
               // 先删除该笔记录
			   connection.setAutoCommit(false);
			   stmt = connection.createStatement();            	
			   stmt.execute(deleteStmt.toLowerCase());    		    
			   connection.commit();

			   // 插入新的记录
			   stmt = connection.createStatement();             
			   stmt.execute(insertStmt.toString().toLowerCase());    		
			   connection.commit();
		   }
		   catch (Exception e)
		   {      
		       e.printStackTrace();
			   try
			   {
				   connection.rollback();
			   }
			   catch(SQLException SqlE)
			   {
				   SqlE.printStackTrace();
			   }
			   throw new PersistException("addOneApplicInfor occur error");
		   }
		   finally 
		   {    
			   stmt = null;
			   insertStmt = null;
			   if (outConnection == null)
			   {
				   closeDbPoolConnection(connection);            	
			   }            
		   }
	}
	
	 public void addOneNotifyMessageInfor(String id, String user, String message, String createTime, String endTime, Connection outConnection) 
	                         throws PersistException {

	     /** 取得数据库连接对象 */
		   Connection connection = null;
		   if (outConnection != null)
			   connection = outConnection;
		   else
			   connection = getDbPoolConnection();
	        
		   Statement stmt = null;
		   StringBuffer insertStmt = new StringBuffer("INSERT INTO notifyMessage VALUES (");
		   insertStmt.append("'" + id + "',");
		   insertStmt.append("'" + user + "',");
		   insertStmt.append("'" + message + "',");
		   insertStmt.append("'" + createTime + "',");
		   insertStmt.append("'" + endTime + "'" );
		   insertStmt.append(")");
		   
		   String deleteStmt = new String("DELETE FROM notifyMessage WHERE id = '" +
		           id+"'");     	          
		   try
		   {
             // 先删除该笔记录
			   connection.setAutoCommit(false);
			   stmt = connection.createStatement();            	
			   stmt.execute(deleteStmt.toLowerCase());    		    
			   connection.commit();

			   // 插入新的记录
			   stmt = connection.createStatement();             
			   stmt.execute(insertStmt.toString().toLowerCase());    		
			   connection.commit();
		   }
		   catch (Exception e)
		   {      
		       e.printStackTrace();
			   try
			   {
				   connection.rollback();
			   }
			   catch(SQLException SqlE)
			   {
				   SqlE.printStackTrace();
			   }
			   throw new PersistException("addOneNotifyMessageInfor occur error");
		   }
		   finally 
		   {    
			   stmt = null;
			   insertStmt = null;
			   if (outConnection == null)
			   {
				   closeDbPoolConnection(connection);            	
			   }            
		   }
	    }
	
	public List searchNetworkItem(String idName, Connection outConnection)
	{
		Statement stmt = null;
		Connection connection = null;        
		/** 保存查询结果的List */
		List lt = new ArrayList();
		String query = "SELECT * FROM NetworkInfor WHERE idname = '" 
						+idName + "'";
        
		if (outConnection != null)
		{
			connection = outConnection;
		}
		else
			connection = getDbPoolConnection();
               
		try
		{
			stmt = connection.createStatement();
			// 发送查询语句,创建返回结果的结果集		    
			ResultSet rs = stmt.executeQuery(query.toLowerCase());            	        
			ResultSetMetaData rsmd = rs.getMetaData();	        
			int numCols = rsmd.getColumnCount();
			String[] tmpResult = new String[numCols];

			boolean more = rs.next();
			while (more){
				for (int index = 1; index <= numCols; index++) {
					tmpResult[index-1] = rs.getString(index);
				}
				
				more = rs.next();
			}               
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			stmt = null;
			if (outConnection == null)
			{
				closeDbPoolConnection(connection);            	
			}
		}
        
		return lt;        		
	}

	public List searchAlarmItem(String ipAddress, Connection outConnection)
	{	  
		
		Statement stmt = null;
		Connection connection = null;        
		List lt = new ArrayList();
		String query = "SELECT * FROM Alarminfor WHERE ipAddress = '" 
						+ipAddress + "'";
        
		if (outConnection != null)
		{
			connection = outConnection;
		}
		else
			connection = getDbPoolConnection();
               
		try
		{
			stmt = connection.createStatement();
			// 发送查询语句,创建返回结果的结果集		    
			ResultSet rs = stmt.executeQuery(query.toLowerCase());            	        
			ResultSetMetaData rsmd = rs.getMetaData();	        
			int numCols = rsmd.getColumnCount();
			String[] tmpResult = new String[numCols];

			boolean more = rs.next();
			while (more){
				for (int index = 1; index <= numCols; index++) 
				{
					tmpResult[index-1] = rs.getString(index);
				}		        
				
				more = rs.next();
			}               
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			stmt = null;
			if (outConnection == null)
			{
				closeDbPoolConnection(connection);            	
			}
		}
        
		return lt;	
		
	}

	public List searchMonitorItem(String idName, Connection outConnection)
	{
		Statement stmt = null;
		Connection connection = null;        
		List lt = new ArrayList();
		String query = "SELECT * FROM MonitorItemInfor WHERE idName = '" 
						+idName + "'";
        
		if (outConnection != null)
		{
			connection = outConnection;
		}
		else
			connection = getDbPoolConnection();
               
		try
		{
			stmt = connection.createStatement();
			// 发送查询语句,创建返回结果的结果集		    
			ResultSet rs = stmt.executeQuery(query.toLowerCase());            	        
			ResultSetMetaData rsmd = rs.getMetaData();	        
			int numCols = rsmd.getColumnCount();
			String[] tmpResult = new String[numCols];

			boolean more = rs.next();
			/*
			while (more){
				for (int index = 1; index <= numCols; index++) 
				{
					tmpResult[index-1] = rs.getString(index);
				}		        
				MonitorItemInfor tmmpMonitItemInfor = new MonitorItemInfor(tmpResult);
				lt.add(tmmpMonitItemInfor);
				more = rs.next();
			}  
			*/
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			stmt = null;
			if (outConnection == null)
			{
				closeDbPoolConnection(connection);            	
			}
		}
        
		return lt;
	}

	public List searchNodeObjItem(String idName, Connection outConnection)
	{		               
		Statement stmt = null;
		Connection connection = null;        
		/** 保存查询结果的List */
		List lt = new ArrayList();
		String query = "SELECT * FROM NodeObjInfor WHERE idname = '" 
						+idName + "'";
        
		if (outConnection != null)
		{
			connection = outConnection;
		}
		else
			connection = getDbPoolConnection();
               
		try
		{
			stmt = connection.createStatement();
			// 发送查询语句,创建返回结果的结果集		    
			ResultSet rs = stmt.executeQuery(query.toLowerCase());            	        
			ResultSetMetaData rsmd = rs.getMetaData();	        
			int numCols = rsmd.getColumnCount();
			String[] tmpResult = new String[numCols];

			boolean more = rs.next();
			while (more){
				for (int index = 1; index <= numCols; index++) {
					tmpResult[index-1] = rs.getString(index).trim();
				}
				//NodeObjInfor nodeInfo = new NodeObjInfor(tmpResult);
				//lt.add(nodeInfo);
				more = rs.next();
			}               
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			stmt = null;
			if (outConnection == null)
			{
				closeDbPoolConnection(connection);            	
			}
		}
        
		return lt;        
	}

	public List searchRouterItem(String idName, Connection outConnection)
	{
		Statement stmt = null;
		Connection connection = null;        
		/** 保存查询结果的List */
		List lt = new ArrayList();
		String query = "SELECT * FROM RouterInfor WHERE idname = '" 
						+idName + "'";
        
		if (outConnection != null)
		{
			connection = outConnection;
		}
		else
			connection = getDbPoolConnection();
               
		try
		{
			stmt = connection.createStatement();
			// 发送查询语句,创建返回结果的结果集		    
			ResultSet rs = stmt.executeQuery(query.toLowerCase());            	        
			ResultSetMetaData rsmd = rs.getMetaData();	        
			int numCols = rsmd.getColumnCount();
			String[] tmpResult = new String[numCols];

			boolean more = rs.next();
			while (more){
				for (int index = 1; index <= numCols; index++) {
					tmpResult[index-1] = rs.getString(index);
				}
				//RouterInfor routerInfo = new RouterInfor(tmpResult);
				//lt.add(routerInfo);
				more = rs.next();
			}               
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			stmt = null;
			if (outConnection == null)
			{
				closeDbPoolConnection(connection);            	
			}
		}
        
		return lt;        
	}

	public List searchSwitchItem(String idName, Connection outConnection)
	{
		Statement stmt = null;
		Connection connection = null;        
		/** 保存查询结果的List */
		List lt = new ArrayList();
		String query = "SELECT * FROM SwitchInfor WHERE idname = '" 
						+idName + "'";
        
		if (outConnection != null)
		{
			connection = outConnection;
		}
		else
			connection = getDbPoolConnection();
               
		try
		{
			stmt = connection.createStatement();
			// 发送查询语句,创建返回结果的结果集		    
			ResultSet rs = stmt.executeQuery(query.toLowerCase());            	        
			ResultSetMetaData rsmd = rs.getMetaData();	        
			int numCols = rsmd.getColumnCount();
			String[] tmpResult = new String[numCols];

			boolean more = rs.next();
			while (more){
				for (int index = 1; index <= numCols; index++) {
					tmpResult[index-1] = rs.getString(index);
				}
				//SwitchInfor switchInfo = new SwitchInfor(tmpResult);
				//lt.add(switchInfo);
				more = rs.next();
			}               
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			stmt = null;
			if (outConnection == null)
			{
				closeDbPoolConnection(connection);            	
			}
		}
        
		return lt;      
	}

	public List searchMonitorItemCollet(String colletIpaddress,
			Connection outConnection) 
	{
		Statement stmt = null;
		Connection connection = null;        
		/** 保存查询结果的List */
		List lt = new ArrayList();
		String query = "SELECT * FROM MonitorItemColletInfor WHERE colletIpaddress = '" 
						+colletIpaddress + "'";
        
		if (outConnection != null)
		{
			connection = outConnection;
		}
		else
			connection = getDbPoolConnection();
               
		try
		{
			stmt = connection.createStatement();
			// 发送查询语句,创建返回结果的结果集		    
			ResultSet rs = stmt.executeQuery(query.toLowerCase());            	        
			ResultSetMetaData rsmd = rs.getMetaData();	        
			int numCols = rsmd.getColumnCount();
			String[] tmpResult = new String[numCols];

			boolean more = rs.next();
			while (more){
				for (int index = 1; index <= numCols; index++) {
					tmpResult[index-1] = rs.getString(index);
				}
				//MonitorItemColletInfor monitItemCollectInfo = new MonitorItemColletInfor(tmpResult);
				//lt.add(monitItemCollectInfo);
				more = rs.next();
			}               
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			stmt = null;
			if (outConnection == null)
			{
				closeDbPoolConnection(connection);            	
			}
		}
	    	   
		return lt; 
	}

	public List searchProducerItem(String producerName, Connection outConnection) 
	{
		Statement stmt = null;
		Connection connection = null;        
		/** 保存查询结果的List */
		List lt = new ArrayList();
		String query = "SELECT * FROM ProducerInfor WHERE producerName = '" 
						+producerName + "'";
        
		if (outConnection != null)
		{
			connection = outConnection;
		}
		else
			connection = getDbPoolConnection();
               
		try
		{
			stmt = connection.createStatement();
			// 发送查询语句,创建返回结果的结果集		    
			ResultSet rs = stmt.executeQuery(query.toLowerCase());            	        
			ResultSetMetaData rsmd = rs.getMetaData();	        
			int numCols = rsmd.getColumnCount();
			String[] tmpResult = new String[numCols];

			boolean more = rs.next();
			while (more){
				for (int index = 1; index <= numCols; index++) {
					tmpResult[index-1] = rs.getString(index);
				}
				//ProducerInfor proInfor = new ProducerInfor(tmpResult);
				//lt.add(proInfor);
				more = rs.next();
			}               
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			stmt = null;
			if (outConnection == null)
			{
				closeDbPoolConnection(connection);            	
			}
		}
	    	   
		return lt;	     
	}

	public List searchDeviceTypeCatalogItem(String objIdValue, 
			Connection outConnection) 
	{
		Statement stmt = null;
		Connection connection = null;        
		/** 保存查询结果的List */
		List lt = new ArrayList();
		String query = "SELECT * FROM DeviceTypeCatalogInfor WHERE objIdValue = '" 
						+objIdValue + "'";
        
		if (outConnection != null)
		{
			connection = outConnection;
		}
		else
			connection = getDbPoolConnection();
               
		try
		{
			stmt = connection.createStatement();
			// 发送查询语句,创建返回结果的结果集		    
			ResultSet rs = stmt.executeQuery(query.toLowerCase());            	        
			ResultSetMetaData rsmd = rs.getMetaData();	        
			int numCols = rsmd.getColumnCount();
			String[] tmpResult = new String[numCols];

			boolean more = rs.next();
			while (more)
			{
				for (int index = 1; index <= numCols; index++)
				{
					tmpResult[index-1] = rs.getString(index);
				}
				//DeviceTypeCatalogInfor deviceTypeCataInfor = new DeviceTypeCatalogInfor(tmpResult);
				//lt.add(deviceTypeCataInfor);
				more = rs.next();
			}               
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			stmt = null;
			if (outConnection == null)
			{
				closeDbPoolConnection(connection);            	
			}
		}
	    	    	    
		return lt; 
	}

	public List searchSpecSnmpCommunItem(String ipAddress,
			Connection outConnection) 
	{	    
		Statement stmt = null;
		Connection connection = null;        
		/** 保存查询结果的List */
		List lt = new ArrayList();
		String query = "SELECT * FROM SpecSnmpCommunInfor WHERE ipaddress = '" 
						+ipAddress + "'";
        
		if (outConnection != null)
		{
			connection = outConnection;
		}
		else
			connection = getDbPoolConnection();
               
		try
		{
			stmt = connection.createStatement();
			// 发送查询语句,创建返回结果的结果集		    
			ResultSet rs = stmt.executeQuery(query.toLowerCase());            	        
			ResultSetMetaData rsmd = rs.getMetaData();	        
			int numCols = rsmd.getColumnCount();
			String[] tmpResult = new String[numCols];

			boolean more = rs.next();
			while (more)
			{
				for (int index = 1; index <= numCols; index++)
				{
					tmpResult[index-1] = rs.getString(index);
				}
		        
				//SpecSnmpCommunInfor specCommunInfor = 
					//		new SpecSnmpCommunInfor(tmpResult);
				//lt.add(specCommunInfor);
				more = rs.next();
			}               
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			stmt = null;
			if (outConnection == null)
			{
				closeDbPoolConnection(connection);            	
			}
		}
	    	    	    
		return lt;	   
	}

	public List searchIfCollectItem(String idName, 
			 Connection outConnection) 
	{
		Statement stmt = null;
		Connection connection = null;        
		/** 保存查询结果的List */
		List lt = new ArrayList();
		String query = "SELECT * FROM ifCollectInfor WHERE idName = '" 
				+idName + "'";
        
		if (outConnection != null)
		{
			connection = outConnection;
		}
		else
			connection = getDbPoolConnection();
               
		try
		{
			stmt = connection.createStatement();
			// 发送查询语句,创建返回结果的结果集		    
			ResultSet rs = stmt.executeQuery(query.toLowerCase());            	        
			ResultSetMetaData rsmd = rs.getMetaData();	        
			int numCols = rsmd.getColumnCount();
			String[] tmpResult = new String[numCols];

			boolean more = rs.next();
			while (more)
			{
				for (int index = 1; index <= numCols; index++)
				{
					tmpResult[index-1] = rs.getString(index);
				}
		        
				
				more = rs.next();
			}               
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			stmt = null;
			if (outConnection == null)
			{
				closeDbPoolConnection(connection);            	
			}
		}
	    	    	    
		return lt;	
	}
	
	public List searchEnsCoreParamInfor(String paramName, Connection outConnection) 
	{
		Statement stmt = null;
		Connection connection = null;        
		/** 保存查询结果的List */
		List lt = new ArrayList();
		String query = "SELECT * FROM ensCoreParamInfor WHERE ParamName = '" 
				+paramName + "'";
        
		if (outConnection != null)
		{
			connection = outConnection;
		}
		else
			connection = getDbPoolConnection();
               
		try
		{
			stmt = connection.createStatement();
			// 发送查询语句,创建返回结果的结果集		    
			ResultSet rs = stmt.executeQuery(query.toLowerCase());            	        
			ResultSetMetaData rsmd = rs.getMetaData();	        
			int numCols = rsmd.getColumnCount();
			String[] tmpResult = new String[numCols];

			boolean more = rs.next();
			while (more)
			{
				for (int index = 1; index <= numCols; index++)
				{
					tmpResult[index-1] = rs.getString(index);
				}
		        
				
				more = rs.next();
			}               
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			stmt = null;
			if (outConnection == null)
			{
				closeDbPoolConnection(connection);            	
			}
		}
	    	    	    
		return lt;		
	}

	public List searchIfEntryInfor(String idname, Connection outConnection) 
	{
		Statement stmt = null;
		Connection connection = null;        
		/** 保存查询结果的List */
		List lt = new ArrayList();
		String query = "SELECT * FROM ifEntryInfor WHERE IdName = '" 
				+idname + "'";
        
		if (outConnection != null)
		{
			connection = outConnection;
		}
		else
			connection = getDbPoolConnection();
               
		try
		{
			stmt = connection.createStatement();
			// 发送查询语句,创建返回结果的结果集		    
			ResultSet rs = stmt.executeQuery(query.toLowerCase());            	        
			ResultSetMetaData rsmd = rs.getMetaData();	        
			int numCols = rsmd.getColumnCount();
			String[] tmpResult = new String[numCols];

			boolean more = rs.next();
			while (more)
			{
				for (int index = 1; index <= numCols; index++)
				{
					tmpResult[index-1] = rs.getString(index);
				}
		        
				
				more = rs.next();
			}               
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			stmt = null;
			if (outConnection == null)
			{
				closeDbPoolConnection(connection);            	
			}
		}
	    	    	    
		return lt;		
	}
	
	/* (non-Javadoc)
	 * @see com.dhcc.ens.persistence.PersistenceServiceable#searchNodeParamInfor(java.lang.String, java.sql.Connection)
	 */
	public List searchNodeParamInfor(String idname, Connection outConnection) 
	{
		Statement stmt = null;
		Connection connection = null;        
		/** 保存查询结果的List */
		List lt = new ArrayList();
		String query = "SELECT * FROM nodeParamInfor WHERE IdName = '" 
				+idname + "'";
        
		if (outConnection != null)
		{
			connection = outConnection;
		}
		else
			connection = getDbPoolConnection();
               
		try
		{
			stmt = connection.createStatement();
			// 发送查询语句,创建返回结果的结果集		    
			ResultSet rs = stmt.executeQuery(query.toLowerCase());            	        
			ResultSetMetaData rsmd = rs.getMetaData();	        
			int numCols = rsmd.getColumnCount();
			String[] tmpResult = new String[numCols];

			boolean more = rs.next();
			while (more)
			{
				for (int index = 1; index <= numCols; index++)
				{
					tmpResult[index-1] = rs.getString(index);
				}
		        
				
				more = rs.next();
			}               
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			stmt = null;
			if (outConnection == null)
			{
				closeDbPoolConnection(connection);            	
			}
		}
	    	    	    
		return lt;	
	}
	
	/* (non-Javadoc)
	 * @see com.dhcc.ens.persistence.PersistenceServiceable#searchMonitorItemColletTmpInfor(java.lang.String, java.sql.Connection)
	 */
	public List searchMonitorItemColletTmpInfor(String colletIpaddress, Connection outConnection) {
		Statement stmt = null;
		Connection connection = null;        
		/** 保存查询结果的List */
		List lt = new ArrayList();
		String query = "SELECT * FROM MonitorItemColletTmpInfor WHERE colletIpaddress = '" 
						+colletIpaddress + "'";
        
		if (outConnection != null)
		{
			connection = outConnection;
		}
		else
			connection = getDbPoolConnection();
               
		try
		{
			stmt = connection.createStatement();
			// 发送查询语句,创建返回结果的结果集		    
			ResultSet rs = stmt.executeQuery(query.toLowerCase());            	        
			ResultSetMetaData rsmd = rs.getMetaData();	        
			int numCols = rsmd.getColumnCount();
			String[] tmpResult = new String[numCols];

			boolean more = rs.next();
			while (more){
				for (int index = 1; index <= numCols; index++) {
					tmpResult[index-1] = rs.getString(index);
				}
				
				more = rs.next();
			}               
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			stmt = null;
			if (outConnection == null)
			{
				closeDbPoolConnection(connection);            	
			}
		}
	    	   
		return lt; 
	}
	
	/* (non-Javadoc)
	 * @see com.dhcc.ens.persistence.PersistenceServiceable#searchIfCollectTmpTable(java.lang.String, java.sql.Connection)
	 */
	public List searchIfCollectTmpTable(String idName, Connection outConnection) {
		Statement stmt = null;
		Connection connection = null;        
		/** 保存查询结果的List */
		List lt = new ArrayList();
		String query = "SELECT * FROM ifCollectTmpTable WHERE idName = '" 
				+idName + "'";
        
		if (outConnection != null)
		{
			connection = outConnection;
		}
		else
			connection = getDbPoolConnection();
               
		try
		{
			stmt = connection.createStatement();
			// 发送查询语句,创建返回结果的结果集		    
			ResultSet rs = stmt.executeQuery(query.toLowerCase());            	        
			ResultSetMetaData rsmd = rs.getMetaData();	        
			int numCols = rsmd.getColumnCount();
			String[] tmpResult = new String[numCols];

			boolean more = rs.next();
			while (more)
			{
				for (int index = 1; index <= numCols; index++)
				{
					tmpResult[index-1] = rs.getString(index);
				}
		        
				
				more = rs.next();
			}               
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			stmt = null;
			if (outConnection == null)
			{
				closeDbPoolConnection(connection);            	
			}
		}
	    	    	    
		return lt;	
	}
	
	/* (non-Javadoc)
	 * @see com.dhcc.ens.persistence.PersistenceServiceable#searchApplicInfor(java.lang.String, java.sql.Connection)
	 */
	public List searchApplicInfor(String applicName, Connection outConnection) {
		Statement stmt = null;
		Connection connection = null;        
		/** 保存查询结果的List */
		List lt = new ArrayList();
		String query = "SELECT * FROM applicInfor WHERE applicName = '" 
				+applicName + "'";
        
		if (outConnection != null)
		{
			connection = outConnection;
		}
		else
			connection = getDbPoolConnection();
               
		try
		{
			stmt = connection.createStatement();
			// 发送查询语句,创建返回结果的结果集		    
			ResultSet rs = stmt.executeQuery(query.toLowerCase());            	        
			ResultSetMetaData rsmd = rs.getMetaData();	        
			int numCols = rsmd.getColumnCount();
			String[] tmpResult = new String[numCols];

			boolean more = rs.next();
			while (more)
			{
				for (int index = 1; index <= numCols; index++)
				{
					tmpResult[index-1] = rs.getString(index);
				}
		        
				
				more = rs.next();
			}               
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			stmt = null;
			if (outConnection == null)
			{
				closeDbPoolConnection(connection);            	
			}
		}
	    	    	    
		return lt;	
	}
	
	public List searchNotifyMessageInfor(String id, Connection outConnection) {
        
		Statement stmt = null;
		Connection connection = null;        
		/** 保存查询结果的List */
		List lt = new ArrayList();
		String query = "SELECT * FROM notifyMessage WHERE id = '" 
				+id + "'";
        
		if (outConnection != null)
		{
			connection = outConnection;
		}
		else
			connection = getDbPoolConnection();
               
		try
		{
			stmt = connection.createStatement();
			// 发送查询语句,创建返回结果的结果集		    
			ResultSet rs = stmt.executeQuery(query.toLowerCase());            	        
			ResultSetMetaData rsmd = rs.getMetaData();	        
			int numCols = rsmd.getColumnCount();
			String[] tmpResult = new String[numCols];

			boolean more = rs.next();
			while (more)
			{
				for (int index = 1; index <= numCols; index++)
				{
					tmpResult[index-1] = rs.getString(index);
				}
		        
				
				more = rs.next();
			}               
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			stmt = null;
			if (outConnection == null)
			{
				closeDbPoolConnection(connection);            	
			}
		}
	    	    	    
		return lt;	
    }
	
	public List networkInforList(Connection outConnection)
	{	    
		Connection connection = null;
		Statement stmt = null;
		/** 保存查询结果的List */
		List lt = new ArrayList();
		String query = "SELECT * FROM NetworkInfor"; 
		if (outConnection != null)
			connection = outConnection;
		else
			connection = getDbPoolConnection();
               
		try 
		{                                               
			stmt = connection.createStatement();
			// 发送查询语句,创建返回结果的结果集		    
			ResultSet rs = stmt.executeQuery (query.toLowerCase());            	        
			ResultSetMetaData rsmd = rs.getMetaData();	        
			int numCols = rsmd.getColumnCount();
			String[] tmpResult = new String[numCols];

			boolean more = rs.next();
			while (more) 
			{
				for (int index = 1; index <= numCols; index++)
				{
					tmpResult[index-1] = rs.getString(index);			
				}
				            
				more = rs.next();
			}                
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			stmt = null;
			if (outConnection == null)
			{
				closeDbPoolConnection(connection);            	
			}
		}
        
		return lt;	    		       
	}

	public List alarmInforList(Connection outConnection)
	{
		Connection connection = null;
		Statement stmt = null;
		/** 保存查询结果的List */
		List lt = new ArrayList();
		String query = "SELECT * FROM AlarmInfor"; 
		if (outConnection != null)
			connection = outConnection;
		else
			connection = getDbPoolConnection();
               
		try 
		{                                               
			stmt = connection.createStatement();
			// 发送查询语句,创建返回结果的结果集		    
			ResultSet rs = stmt.executeQuery (query.toLowerCase());            	        
			ResultSetMetaData rsmd = rs.getMetaData();	        
			int numCols = rsmd.getColumnCount();
			String[] tmpResult = new String[numCols];

			boolean more = rs.next();
			while (more) 
			{
				for (int index = 1; index <= numCols; index++)
				{
					tmpResult[index-1] = rs.getString(index);
				}
				            
				more = rs.next();
			}                
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			stmt = null;
			if (outConnection == null)
			{
				closeDbPoolConnection(connection);            	
			}
		}
		return lt;        
	}

	
	public List monitorItemInforList(Connection outConnection)
	{
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs   = null;
		ResultSetMetaData rsmd = null;
		/** 保存查询结果的List */
		List lt = new ArrayList();
		String query = "SELECT * FROM MonitorItemInfor"; 
		if (outConnection != null)
			connection = outConnection;
		else
			connection = getDbPoolConnection();
               
		try 
		{                                               
			stmt = connection.createStatement();
			// 发送查询语句,创建返回结果的结果集		    
			rs = stmt.executeQuery (query.toLowerCase());            	        
			rsmd = rs.getMetaData();	        
			int numCols = rsmd.getColumnCount();
			String[] tmpResult = new String[20];

			boolean more = rs.next();
			while (more) 
			{
				for (int index = 1; index <= numCols; index++)
				{
					tmpResult[index-1] = rs.getString(index);
				}		        
				           
				more = rs.next();
			}  
			rs.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			stmt = null;  
			rs   = null;
			rsmd = null;
			if (outConnection == null)
			{
				closeDbPoolConnection(connection);            	
			}
		}
        
		return lt;       
	}

	public List nodeObjInforList(Connection outConnection)
	{                                    
		Connection connection = null;
		Statement stmt = null;
		/** 保存查询结果的List */
		List lt = new ArrayList();
		String query = "SELECT * FROM NodeObjInfor"; 
		if (outConnection != null)
		{
			connection = outConnection;
		}
		else
			connection = getDbPoolConnection();
               
		try
		{                                               
			stmt = connection.createStatement();
			// 发送查询语句,创建返回结果的结果集		    
			ResultSet rs = stmt.executeQuery (query.toLowerCase());            	        
			ResultSetMetaData rsmd = rs.getMetaData();	        
			int numCols = rsmd.getColumnCount();
			String[] tmpResult = new String[numCols];

			boolean more = rs.next();
			while (more)
			{
				for (int index = 1; index <= numCols; index++)
				{
					tmpResult[index-1] = rs.getString(index);
				}
				            
				more = rs.next();
			}                
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally {
			stmt = null;
			if (outConnection == null)
			{
				closeDbPoolConnection(connection);            	
			}
		}
		return lt;
	}

	public List routerInforList(Connection outConnection)
	{
		Connection connection = null;
		Statement stmt = null;
		/** 保存查询结果的List */
		List lt = new ArrayList();
		String query = "SELECT * FROM RouterInfor"; 
		if (outConnection != null)
		{
			connection = outConnection;
		}
		else
			connection = getDbPoolConnection();
               
		try 
		{                                               
			stmt = connection.createStatement();
			// 发送查询语句,创建返回结果的结果集		    
			ResultSet rs = stmt.executeQuery (query.toLowerCase());            	        
			ResultSetMetaData rsmd = rs.getMetaData();	        
			int numCols = rsmd.getColumnCount();
			String[] tmpResult = new String[numCols];

			boolean more = rs.next();
			while (more)
			{
				for (int index = 1; index <= numCols; index++)
				{
					tmpResult[index-1] = rs.getString(index).trim();
				}
				          
				more = rs.next();
			}                
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			stmt = null;
			if (outConnection == null)
			{
				closeDbPoolConnection(connection);            	
			}
		}
		return lt;        
	}

	public List switchInforList(Connection outConnection)
	{
		Connection connection = null;
		Statement stmt = null;
		/** 保存查询结果的List */
		List lt = new ArrayList();
		String query = "SELECT * FROM SwitchInfor"; 
		if (outConnection != null)
			connection = outConnection;
		else
			connection = getDbPoolConnection();
               
		try 
		{                                               
			stmt = connection.createStatement();
			// 发送查询语句,创建返回结果的结果集		    
			ResultSet rs = stmt.executeQuery (query.toLowerCase());            	        
			ResultSetMetaData rsmd = rs.getMetaData();	        
			int numCols = rsmd.getColumnCount();
			String[] tmpResult = new String[numCols];

			boolean more = rs.next();
			while (more) 
			{
				for (int index = 1; index <= numCols; index++)
				{
					tmpResult[index-1] = rs.getString(index).trim();
				}
					            
				more = rs.next();
			}                
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			stmt = null;
			if (outConnection == null)
			{
				closeDbPoolConnection(connection);            	
			}
		}
        
		return lt;
	}
	
	public List monitorItemColletList(Connection outConnection) 
	{	    
		Connection connection = null;
		Statement stmt = null;
		/** 保存查询结果的List */
		List lt = new ArrayList();
		String query = "SELECT * FROM MonitorItemColletInfor"; 
		if (outConnection != null)
			connection = outConnection;
		else
			connection = getDbPoolConnection();
               
		try 
		{                                               
			stmt = connection.createStatement();
			// 发送查询语句,创建返回结果的结果集		    
			ResultSet rs = stmt.executeQuery (query.toLowerCase());            	        
			ResultSetMetaData rsmd = rs.getMetaData();	        
			int numCols = rsmd.getColumnCount();
			String[] tmpResult = new String[numCols];

			boolean more = rs.next();
			while (more) 
			{
				for (int index = 1; index <= numCols; index++)
				{
					tmpResult[index-1] = rs.getString(index);
				}
				        
				more = rs.next();
			}                
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			stmt = null;
			if (outConnection == null)
			{
				closeDbPoolConnection(connection);            	
			}
		}
        
		return lt;
	}

	public List producerInforList(Connection outConnection) 
	{	    
		Connection connection = null;
		Statement stmt = null;
		/** 保存查询结果的List */
		List lt = new ArrayList();
		String query = "SELECT * FROM ProducerInfor"; 
		if (outConnection != null)
			connection = outConnection;
		else
			connection = getDbPoolConnection();
               
		try 
		{                                               
			stmt = connection.createStatement();
			// 发送查询语句,创建返回结果的结果集		    
			ResultSet rs = stmt.executeQuery (query.toLowerCase());            	        
			ResultSetMetaData rsmd = rs.getMetaData();	        
			int numCols = rsmd.getColumnCount();
			String[] tmpResult = new String[numCols];

			boolean more = rs.next();
			while (more) 
			{
				for (int index = 1; index <= numCols; index++)
				{
					tmpResult[index-1] = rs.getString(index);
				}
				         
				more = rs.next();
			}                
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			stmt = null;
			if (outConnection == null)
			{
				closeDbPoolConnection(connection);            	
			}
		}
        
		return lt;
	}
	
   public List deviceTypeCatalogInforList(Connection outConnection) 
   {
	   Connection connection = null;
	   Statement stmt = null;
	   /** 保存查询结果的List */
	   List lt = new ArrayList();
	   String query = "SELECT * FROM DeviceTypeCatalogInfor"; 
	   if (outConnection != null)
			connection = outConnection;
		else
			connection = getDbPoolConnection();
              
	   try {                                               
			stmt = connection.createStatement();
			// 发送查询语句,创建返回结果的结果集		    
			ResultSet rs = stmt.executeQuery (query.toLowerCase());            	        
			ResultSetMetaData rsmd = rs.getMetaData();	        
			int numCols = rsmd.getColumnCount();
			String[] tmpResult = new String[numCols];

			boolean more = rs.next();
			while (more)
			{
				for (int index = 1; index <= numCols; index++)
				{
				   tmpResult[index-1] = rs.getString(index);
				}
				           
				more = rs.next();
			}                
	   }
	   catch (Exception e)
	   {
		   e.printStackTrace();
	   }
	   finally
	   {
		   stmt = null;
		   if (outConnection == null)
		   {
			   closeDbPoolConnection(connection);            	
		   }
	   }
       
	   return lt;
	}
	
	public List specSnmpCommunInforList(Connection outConnection) 
	{	    
		Connection connection = null;
		Statement stmt = null;
		/** 保存查询结果的List */
		List lt = new ArrayList();
		String query = "SELECT * FROM SpecSnmpCommunInfor"; 
		if (outConnection != null)
			connection = outConnection;
		else
			connection = getDbPoolConnection();
               
		try 
		{                                               
			stmt = connection.createStatement();
			// 发送查询语句,创建返回结果的结果集		    
			ResultSet rs = stmt.executeQuery (query.toLowerCase());            	        
			ResultSetMetaData rsmd = rs.getMetaData();	        
			int numCols = rsmd.getColumnCount();
			String[] tmpResult = new String[numCols];

			boolean more = rs.next();
			while (more) 
			{
				for (int index = 1; index <= numCols; index++)
				{
					tmpResult[index-1] = rs.getString(index);
				}
					           
				more = rs.next();
			}                
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			stmt = null;
			if (outConnection == null)
			{
				closeDbPoolConnection(connection);            	
			}
		}
        
		return lt;	   	    
	}
	
	public List ifCollectInforList(Connection outConnection) 
	{
		Connection connection = null;
		Statement stmt = null;
		/** 保存查询结果的List */
		List lt = new ArrayList();
		String query = "SELECT * FROM IfCollectInfor"; 
		if (outConnection != null)
			connection = outConnection;
		else
			connection = getDbPoolConnection();
               
		try 
		{                                               
			stmt = connection.createStatement();
			// 发送查询语句,创建返回结果的结果集		    
			ResultSet rs = stmt.executeQuery (query.toLowerCase());            	        
			ResultSetMetaData rsmd = rs.getMetaData();	        
			int numCols = rsmd.getColumnCount();
			String[] tmpResult = new String[numCols];

			boolean more = rs.next();
			while (more) 
			{
				for (int index = 1; index <= numCols; index++)
				{
					tmpResult[index-1] = rs.getString(index);
				}
				       
				more = rs.next();
			}                
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			stmt = null;
			if (outConnection == null)
			{
				closeDbPoolConnection(connection);            	
			}
		}
        
		return lt;		
	}
	
	public List ensCoreParamInforList(Connection outConnection) 
	{
		Connection connection = null;
		Statement stmt = null;
		/** 保存查询结果的List */
		List lt = new ArrayList();
		String query = "SELECT * FROM ensCoreParamInfor"; 
		if (outConnection != null)
			connection = outConnection;
		else
			connection = getDbPoolConnection();
               
		try 
		{                                               
			stmt = connection.createStatement();
			// 发送查询语句,创建返回结果的结果集		    
			ResultSet rs = stmt.executeQuery (query.toLowerCase());            	        
			ResultSetMetaData rsmd = rs.getMetaData();	        
			int numCols = rsmd.getColumnCount();
			String[] tmpResult = new String[numCols];

			boolean more = rs.next();
			while (more) 
			{
				for (int index = 1; index <= numCols; index++)
				{
					tmpResult[index-1] = rs.getString(index);
				}
				          
				more = rs.next();
			}                
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			stmt = null;
			if (outConnection == null)
			{
				closeDbPoolConnection(connection);            	
			}
		}
        
		return lt;
	}

	/*
	 * @see com.cn.dhcc.ENS.Persistence.PersistenceServiceable#getDbConnection()
	 */
	public Connection getDbPoolConnection()
	{ 
		Connection connection = null;
		try
		{
			//Class.forName("com.mysql.jdbc.Driver");
			Class.forName("com.microsoft.jdbc.sqlserver.SQLServerDriver");
			connection = DriverManager.getConnection(jdbcURL, user, password);
			//Class.forName("com.microsoft.jdbc.sqlserver.SQLServerDriver");
			//connection = DriverManager.getConnection(jdbcURL, user, password);
			//System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeejdbcURL="+jdbcURL);

		}
		catch(Exception E)
		{
			E.printStackTrace();
			System.err.println("Couldn't find driver " + E);
		}        
        
		return connection;
		
//			String jndi = "jdbc/dhccSnmp";//这个值固定了，其实是可以设置成可配置的
//			Connection conn = null;
//			Context initCtx;
//			try {
//				initCtx = new InitialContext();
//			
//		 	    DataSource ds = null;
//		 	    //if("tomcat".equals(ResourceCenter.getInstance().getAppServer()))
//		 	       ds = (DataSource)initCtx.lookup("java:comp/env/" + jndi); //tomcat
//		 	   // else
//		 	    //   ds = (DataSource)initCtx.lookup(jndi);  //weblogic
//		        conn = ds.getConnection();
//		        conn.setAutoCommit(false);        
//		        initCtx.close();
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					System.out.println("数据库连接出错！！！");
//					e.printStackTrace();
//			}
//			try {
//				conn.setAutoCommit(true);
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//			return conn;
	}

	public void closeDbPoolConnection(Connection connection) 
	{
		try {
			if (connection != null) {
				connection.close();
			}
		}
		catch (SQLException SqlE) {
			System.err.println("Problem closing connection " + SqlE);
		}   
    	
		connection = null;
	}

    /*
     * @see com.cn.dhcc.ENS.Persistence.PersistenceServiceable#addOneMonitorItem(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.sql.Connection)
     */
    public void addOneMonitorItem(String idname, String monitorOid,
            String monitorGroup, String monitorCategory,
            String monitorDeviceType, String monitDisplayOldName,
            String monitDisplayNewName, String monitorValue,
            String ipAddress, String snmpPort, String community,
            String isSnmpMonitorItem, String thresholdEnable,
            String thresholdLimit, String thresholdCheck, 
            String severityoftheAlarm, String consecutivetimes,
            String reserved1, String reserved2, String reserved3,
            String reserved4, Connection outConnection)
    {
        /** 取得数据库连接对象 */
		Connection connection = null;
		if (outConnection != null)
			connection = outConnection;
		else
			connection = getDbPoolConnection();
        
		Statement stmt = null;
		StringBuffer insertStmt = new StringBuffer("INSERT INTO MonitorItemInfor VALUES (");
		insertStmt.append("'" + idname + "',");
		insertStmt.append("'" + monitorGroup + "',");
		insertStmt.append("'" + monitorCategory + "',");
		insertStmt.append("'" + monitorDeviceType + "',");
		insertStmt.append("'" + monitDisplayNewName + "',");
		insertStmt.append("'" + monitorValue + "',");
		insertStmt.append("'" + monitorOid + "',");
		insertStmt.append("'" + ipAddress + "',");
		insertStmt.append("'" + snmpPort + "',");
		insertStmt.append("'" + community + "',");        
		insertStmt.append("'" + isSnmpMonitorItem + "',");
		insertStmt.append("'" + thresholdEnable + "',");
		insertStmt.append("'" + thresholdLimit + "',");        
		insertStmt.append("'" + thresholdCheck + "',");
		insertStmt.append("'" + severityoftheAlarm + "',");
		insertStmt.append("'" + consecutivetimes + "',");                
		insertStmt.append("'" + reserved1 + "',");        
		insertStmt.append("'" + reserved2 + "',");
		insertStmt.append("'" + reserved3 + "',");
		insertStmt.append("'" + reserved4 + "'");
		insertStmt.append(")");
                
		String deleteStmt = new String("DELETE FROM MonitorItemInfor WHERE idName = '"
				+idname +"' and monitorGroup ='"
				+monitorGroup+"' and monitorDisplayName ='"
				+monitDisplayOldName+"' and monitorOid ='"+monitorOid+"'");        
		try
		{
			// 先删除该笔记录
			connection.setAutoCommit(false);
			stmt = connection.createStatement();            	
			stmt.execute(deleteStmt.toLowerCase());    		    
			connection.commit();
    		
			// 后插入新的记录
			stmt = connection.createStatement();             
			stmt.execute(insertStmt.toString().toLowerCase());    		
			connection.commit();
		}
		catch (Exception e)
		{
			e.printStackTrace(); 
			try
			{
				connection.rollback();
			}
			catch(SQLException SqlE)
			{
				SqlE.printStackTrace();
			}
		}
		finally 
		{    
			stmt = null;
			insertStmt = null;
			if (outConnection == null)
			{
				closeDbPoolConnection(connection);            	
			}            
		}
    }
    
    public  List ifEntryInforList(Connection outConnection) 
    {
    	Connection connection = null;
		Statement stmt = null;
		/** 保存查询结果的List */
		List lt = new ArrayList();
		String query = "SELECT * FROM ifEntryInfor"; 
		if (outConnection != null)
			connection = outConnection;
		else
			connection = getDbPoolConnection();
               
		try 
		{                                               
			stmt = connection.createStatement();
			// 发送查询语句,创建返回结果的结果集		    
			ResultSet rs = stmt.executeQuery (query.toLowerCase());            	        
			ResultSetMetaData rsmd = rs.getMetaData();	        
			int numCols = rsmd.getColumnCount();
			String[] tmpResult = new String[numCols];

			boolean more = rs.next();
			while (more) 
			{
				for (int index = 1; index <= numCols; index++)
				{
					tmpResult[index-1] = rs.getString(index);
				}
				          
				more = rs.next();
			}                
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			stmt = null;
			if (outConnection == null)
			{
				closeDbPoolConnection(connection);            	
			}
		}
        
		return lt;
	}
    

	/* (non-Javadoc)
	 * @see com.dhcc.ens.persistence.PersistenceServiceable#nodeParamInforList(java.sql.Connection)
	 */
	public List nodeParamInforList(Connection outConnection) 
	{
		Connection connection = null;
		Statement stmt = null;
		/** 保存查询结果的List */
		List lt = new ArrayList();
		String query = "SELECT * FROM nodeParamInfor"; 
		if (outConnection != null)
			connection = outConnection;
		else
			connection = getDbPoolConnection();
               
		try 
		{                                               
			stmt = connection.createStatement();
			// 发送查询语句,创建返回结果的结果集		    
			ResultSet rs = stmt.executeQuery (query.toLowerCase());            	        
			ResultSetMetaData rsmd = rs.getMetaData();	        
			int numCols = rsmd.getColumnCount();
			String[] tmpResult = new String[numCols];

			boolean more = rs.next();
			while (more) 
			{
				for (int index = 1; index <= numCols; index++)
				{
					tmpResult[index-1] = rs.getString(index);
				}
				          
				more = rs.next();
			}                
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			stmt = null;
			if (outConnection == null)
			{
				closeDbPoolConnection(connection);            	
			}
		}
        
		return lt;
	}
	
	/* (non-Javadoc)
	 * @see com.dhcc.ens.persistence.PersistenceServiceable#ifCollectTmpTableList(java.sql.Connection)
	 */
	public List ifCollectTmpTableList(Connection outConnection) {
		Connection connection = null;
		Statement stmt = null;
		/** 保存查询结果的List */
		List lt = new ArrayList();
		String query = "SELECT * FROM IfCollectTmpTable"; 
		if (outConnection != null)
			connection = outConnection;
		else
			connection = getDbPoolConnection();
               
		try 
		{                                               
			stmt = connection.createStatement();
			// 发送查询语句,创建返回结果的结果集		    
			ResultSet rs = stmt.executeQuery (query.toLowerCase());            	        
			ResultSetMetaData rsmd = rs.getMetaData();	        
			int numCols = rsmd.getColumnCount();
			String[] tmpResult = new String[numCols];

			boolean more = rs.next();
			while (more) 
			{
				for (int index = 1; index <= numCols; index++)
				{
					tmpResult[index-1] = rs.getString(index);
				}
					           
				more = rs.next();
			}                
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			stmt = null;
			if (outConnection == null)
			{
				closeDbPoolConnection(connection);            	
			}
		}
        
		return lt;		
	}
    
	/* (non-Javadoc)
	 * @see com.dhcc.ens.persistence.PersistenceServiceable#monitotItemColletTmpInfor(java.sql.Connection)
	 */
	public List monitotItemColletTmpInfor(Connection outConnection) {
		Connection connection = null;
		Statement stmt = null;
		/** 保存查询结果的List */
		List lt = new ArrayList();
		String query = "SELECT * FROM MonitorItemColletTmpInfor"; 
		if (outConnection != null)
			connection = outConnection;
		else
			connection = getDbPoolConnection();
               
		try 
		{                                               
			stmt = connection.createStatement();
			// 发送查询语句,创建返回结果的结果集		    
			ResultSet rs = stmt.executeQuery (query.toLowerCase());            	        
			ResultSetMetaData rsmd = rs.getMetaData();	        
			int numCols = rsmd.getColumnCount();
			String[] tmpResult = new String[numCols];

			boolean more = rs.next();
			while (more) 
			{
				for (int index = 1; index <= numCols; index++)
				{
					tmpResult[index-1] = rs.getString(index);
				}
				           
				more = rs.next();
			}                
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			stmt = null;
			if (outConnection == null)
			{
				closeDbPoolConnection(connection);            	
			}
		}
        
		return lt;
	}


	/* (non-Javadoc)
	 * @see com.dhcc.ens.persistence.PersistenceServiceable#applicInfor(java.sql.Connection)
	 */
	public List applicInforList(Connection outConnection) {
		Connection connection = null;
		Statement stmt = null;
		/** 保存查询结果的List */
		List lt = new ArrayList();
		String query = "SELECT * FROM applicInfor"; 
		if (outConnection != null)
			connection = outConnection;
		else
			connection = getDbPoolConnection();
               
		try 
		{                                               
			stmt = connection.createStatement();
			// 发送查询语句,创建返回结果的结果集		    
			ResultSet rs = stmt.executeQuery (query.toLowerCase());            	        
			ResultSetMetaData rsmd = rs.getMetaData();	        
			int numCols = rsmd.getColumnCount();
			String[] tmpResult = new String[numCols];

			boolean more = rs.next();
			while (more) 
			{
				for (int index = 1; index <= numCols; index++)
				{
					tmpResult[index-1] = rs.getString(index);
				}
				           
				more = rs.next();
			}                
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			stmt = null;
			if (outConnection == null)
			{
				closeDbPoolConnection(connection);            	
			}
		}
        
		return lt;
	}
	
	public List notifyMessageInforList(Connection outConnection) {

	    Connection connection = null;
		Statement stmt = null;
		/** 保存查询结果的List */
		List lt = new ArrayList();
		String query = "SELECT * FROM notifyMessage"; 
		if (outConnection != null)
			connection = outConnection;
		else
			connection = getDbPoolConnection();
               
		try 
		{                                               
			stmt = connection.createStatement();
			// 发送查询语句,创建返回结果的结果集		    
			ResultSet rs = stmt.executeQuery (query.toLowerCase());            	        
			ResultSetMetaData rsmd = rs.getMetaData();	        
			int numCols = rsmd.getColumnCount();
			String[] tmpResult = new String[numCols];

			boolean more = rs.next();
			while (more) 
			{
				for (int index = 1; index <= numCols; index++)
				{
					tmpResult[index-1] = rs.getString(index);
				}
				           
				more = rs.next();
			}                
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			stmt = null;
			if (outConnection == null)
			{
				closeDbPoolConnection(connection);            	
			}
		}
        
		return lt;
    }
	
    /**
     * 执行一段 SQL 查询语句并返回结果。该方法不判断 SELECT 语句的合法性。<br>
     * 例如：设 String[][] queryResult 为查询返回的结果，可如下遍历出结果信息：<br>
     * for (int m = 0; m < queryResult.length; m++) {
     *      for (int n = 0; n < queryResult[0].length; n++) {
     *          System.out.print(queryResult[m][n] + "|");
     *      }
     *      System.out.println();
     *  }
     * @param query 指定输入的查询语句
     * @param outConnection 指定需要使用的外部连接，如果该参数为 null，则函数使用 Persistence 指定的内部JDBC连接
     * @return 查询结果[m][n]，m为行数，n为列数，注：返回的第 [0][m] 维数组为表头（Lable）
     */
	public String[][] executeQuery(String query, Connection outConnection) {
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;
        ResultSetMetaData rsmd = null;
        
        if (outConnection != null)
            connection = outConnection;
        else
            connection = getDbPoolConnection();

        String[][] result = null;
        java.util.List tmpList = new java.util.ArrayList();

        try 
        {
			stmt = connection.createStatement();
			// 发送查询语句,创建返回结果的结果集
			rs = stmt.executeQuery (query.toLowerCase());

            rsmd = rs.getMetaData();

            int numCols = rsmd.getColumnCount();
            int numRows = 0;

            tmpList = new java.util.ArrayList();

            for (int i = 1; i <= numCols; i++) {
            	tmpList.add(rsmd.getColumnLabel(i));
            }
            
			while (rs.next()) {				
                for (int index = 1; index <= numCols; index++) {
                	tmpList.add(rs.getString(index));
                }
                numRows++;
            }

            rs.close();
            stmt.close();
            
            // 将结果按格式输出
            result = new String[numRows+1][numCols];
            int rsIndex = 0;
            for (int i = 0; i < result.length; i++) {
            	for (int j = 0; j < result[i].length; j++) {
            		result[i][j] = (String) tmpList.get(rsIndex);
            		if (result[i][j] == null || result[i][j].equals("null")) {
            			result[i][j] = "";
            		}
            		rsIndex++;
            	}
            }
        }
		catch (Exception e) {
			e.printStackTrace();
			// 建立一个1行5列的二维表
			result = new String[1][5];
			for (int i = 0; i < result.length; i++) {
				for (int j = 0; j < result[i].length; j++) {
					result[i][j] = new String("");
				}
			}
		}
		finally {
		    try
		    {
		        stmt.close();
		        rs.close();			        
		    }
		    catch(Exception e)
		    {
		        ;
		    }
		    stmt = null;
		    rs = null;
		    rsmd = null;
		    
		    if (outConnection == null) {
		        closeDbPoolConnection(connection);
		    }
		    tmpList = null;
        }

        return result;
    }
    
//	*****************************kjchen 于重庆项目组*****************************
	/**
	 * 此类专为解决oracle数据库中查询回滚和SGA明细所设计的，只有OracleApplication中的三处调用了此方法
	 * 其他SQL查询的实现还是调用executeQuery()方法
	 * @author kjchen @重庆项目组
	 */
	
	public String[][] executeQueryOracle(String sql,Connection conn){
		Statement stmt = null;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		String[][] tempResult = null;	//以二维表的形式存放所取得的数据,第一行存放的是字段名称
		List tempList = new ArrayList();//暂时存放数据
		int numCols = 0;				//记录的字段个数				
		int numRows = 0;				//tempResult的行数,也就是从数据库取得的记录的个数
		int index = 0;					//tempList的元素索引
		
		try{
			stmt = conn.createStatement();	
			rs = stmt.executeQuery(sql);
			rsmd = rs.getMetaData();
			numCols = rsmd.getColumnCount();
					
			//把字段名称写入tempList
			for(int i=1;i<=numCols;i++){//切记索引是从1开始
				tempList.add(rsmd.getColumnLabel(i));
			}
			//把字段值按照每行从左到右依次写入tempList
			while(rs.next()){
				for(int j=1;j<=numCols;j++){//切记索引是从1开始
					tempList.add(rs.getString(j));
				}
				numRows++;
			}
			rsmd = null;
			rs.close();
			stmt.close();
			
			//把tempList中的值写入tempResult
			tempResult = new String[numRows + 1][numCols];
			for(int i=0;i<tempResult.length;i++){
				for(int j=0;j<tempResult[i].length;j++){
					tempResult[i][j] = (String)tempList.get(index);
					if("null".equals(tempResult[i][j]) | tempResult[i][j] == null){
						tempResult[i][j] = "";
						//System.out.println("tempResult[" + i + "][" + j + "]为： 空！" );
					}
					index++;
				}
			}
			tempList = null;
		} 
		catch(SQLException e) {
			e.printStackTrace();
		}
		
		return tempResult;
		
	}
//	*****************************kjchen 于重庆项目组*****************************
	
    /**
     * 执行SQL插入、更新、删除记录操作，该方法不判断输入参数SQL语句的合法性
     * @param update 指定需要执行的SQL语句
     * @param outConnection 指定需要使用的外部连接，如果该参数为 null，则函数使用 Persistence 指定的内部JDBC连接
     */
    public boolean executeUpdate(String update, Connection outConnection)
    {
        boolean returnbool = false;
        Connection connection = null;
        Statement stmt = null;

        if (outConnection != null)
            connection = outConnection;
        else
            connection = getDbPoolConnection();

        try 
        {
			connection.setAutoCommit(false);
			stmt = connection.createStatement();
			stmt.executeUpdate(update.toLowerCase());
			connection.commit();
            stmt.close();
            returnbool = true;
        }
		catch (Exception e) {
		    returnbool = false;
		    System.out.println("update.toLowerCase():"+update.toLowerCase());
			e.printStackTrace();
			try {
				connection.rollback();
			}
			catch(SQLException SqlE) {
				System.out.println("update.toLowerCase():"+update.toLowerCase());
				SqlE.printStackTrace();
			}
		}
		finally {
            stmt = null;
			if (outConnection == null) {
				closeDbPoolConnection(connection);
			}
		}
		
		return returnbool;
    }

    /*
     * @see com.dhcc.ens.persistence.PersistenceServiceable#backupDayData()
     */
    public boolean backupDayData(Connection outConnection)
    {
    	boolean returnbool = false;
    	/*
        HashMap<String,String> map=new HashMap<String,String>();
        EnsUserLogAction logAction=new EnsUserLogAction("ens_poll_log");
        EnsUserLogAction logAction1=new EnsUserLogAction("ens_poll_log");
        String todaydate=com.cn.dhcc.ens.util.Utilities.getTodayDate();
        String last2year=null;
        String ifdir=new com.cn.dhcc.ens.log.userlog.action.DatabaseProc().getPath()+"接口";
        String monitordir=new com.cn.dhcc.ens.log.userlog.action.DatabaseProc().getPath()+"监控项";
        String alarmdir=new com.cn.dhcc.ens.log.userlog.action.DatabaseProc().getPath()+"警报";
        int last2yearint=Integer.parseInt(todaydate.substring(0,4))-1;
        last2year=last2yearint+todaydate.substring(4);
        //删除2年前的数据
        //如果路径不存在就建立
        if(!ifdir.endsWith("/")) ifdir+="/";
        if(!monitordir.endsWith("/"))monitordir+="/";
        if(!alarmdir.endsWith("/"))alarmdir+="/";
        File alarmfile=new File(alarmdir);
        if(!alarmfile.exists())alarmfile.mkdir();
        File iffile=new File(ifdir);
        if(!iffile.exists())iffile.mkdir();
        File monitorfile=new File(monitordir);
        if(!monitorfile.exists())monitorfile.mkdir();
        try
        {
        com.cn.dhcc.ens.util.FileOperate.delFile(ifdir+last2year+".xls");
        com.cn.dhcc.ens.util.FileOperate.delFile(monitordir+last2year+".xls");
        com.cn.dhcc.ens.util.FileOperate.delFile(alarmdir+last2year+".xls");
        }catch(Exception e)
        {
        	System.out.println("至少一个删除XLS文件出错了");
        	
        }
        //贺茂庆　　２００７－０３－２４ 重庆 备份当天数据
        String ip="";
        //if(new Util().isIfBack())
        //{
        returnbool=new com.cn.dhcc.ens.log.userlog.action.DatabaseProc().exportExcell("ifcollectinfor", ifdir+com.cn.dhcc.ens.util.Utilities.getTodayDate()+".xls","", "");
        	//插入到日志当中
       
        	try {
        		ip=InetAddress.getLocalHost().toString();
				map.put("ip", ip.substring(ip.indexOf("/")+1));
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	map.put("event", "导出接口监控信息到Excell");
        	if(returnbool==true)
        	    map.put("eventdetail", "成功导出");
        	else
        		map.put("eventdetail", "导出出错");
        		map.put("logtime", "");
        		logAction.insertOneLog(map);
       // }
      // if(new Util().isMonitorBack())
      // {
       returnbool= new com.cn.dhcc.ens.log.userlog.action.DatabaseProc().exportExcell("monitoritemcolletinfor", monitordir+com.cn.dhcc.ens.util.Utilities.getTodayDate()+".xls", "", "");
       try {
    	   ip=InetAddress.getLocalHost().toString();
			map.put("ip", ip.substring(ip.indexOf("/")+1));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   	map.put("event", "导出监控信息到Excell");
   	if(returnbool==true)
   	    map.put("eventdetail", "成功导出");
   	else
   		map.put("eventdetail", "导出出错");
   		map.put("logtime", "");
   		logAction1.insertOneLog(map);
   		*/
      // }
       
       //备份警报
      // if(new Util().isAlarmBack())
      // {
    	/*
    	   returnbool=new com.cn.dhcc.ens.log.userlog.action.DatabaseProc().exportExcell("alarminfor",alarmdir+com.cn.dhcc.ens.util.Utilities.getTodayDate()+".xls","", "");
    	   try {
        	   ip=InetAddress.getLocalHost().toString();
    			map.put("ip", ip.substring(ip.indexOf("/")+1));
    		} catch (UnknownHostException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
       	map.put("event", "导出报警信息到Excell");
       	if(returnbool==true)
       	    map.put("eventdetail", "成功导出");
       	else
       		map.put("eventdetail", "导出出错");
       		map.put("logtime", "");
       		logAction1.insertOneLog(map);
       		*/
       		
     //}
//        // 取得mysql数据库所在的目录
//        boolean returnbool = false;
//        String databasePath = null;
//        String databackupPath = null;
//		String osname = System.getProperty("os.name");
//		String userdir = System.getProperty("user.dir");
//
//		if (getBackUpDirectory().equals(""))
//		{
//			if (osname.toLowerCase().indexOf("windows") >= 0) 
//			{
//	        	userdir = userdir.substring(0, userdir.lastIndexOf("\\"));
//	        	databasePath = userdir + "\\database\\data\\ens\\";
//	        	databackupPath = userdir +"\\backup\\";
//	        	System.out.println(" 备份每日数据库目录是 "+databasePath);
//	        	System.out.println(" 备份每日数据库备份目录是 "+databackupPath);
//			}
//		}
//		else
//		{
//		    userdir = userdir.substring(0, userdir.lastIndexOf("\\"));
//        	databasePath = userdir + "\\database\\data\\ens\\";
//		    databackupPath = getBackUpDirectory();
//		    DiscoverOper.logService.info(" 备份每日数据库目录是 "+databasePath);
//        	DiscoverOper.logService.info(" 备份每日数据库备份目录是 "+databackupPath);		    
//		}
//        
//		// 遍历数据备份目录是否超过设定的日期期限
//		int backupTotalCnt = FileOperate.searchFileAmount(databackupPath, "monitoritemcollettmpinfor");
//		System.out.println("数据备份监控项数据采集部分 the backupTotalCnt is "+backupTotalCnt);
//		if (backupTotalCnt == 30)
//		{
//		    FileOperate.delSpecifyFile(databackupPath, "monitoritemcollettmpinfor");
//		}
//		backupTotalCnt = FileOperate.searchFileAmount(databackupPath, "ifcollecttmptable");
//		System.out.println("数据备份接口部分 the backupTotalCnt is "+backupTotalCnt);
//		if (backupTotalCnt == 30)
//		{
//		    FileOperate.delSpecifyFile(databackupPath, "ifcollecttmptable");
//		}
//		        
//        String tmpDayBackup = "";
//        String tmpDayIfBackup = "";
//        Connection connection = null;
//        Statement stmt = null;
//
//        if (outConnection != null)
//            connection = outConnection;
//        else
//            connection = getDbPoolConnection();
//
//        try 
//        {
//			connection.setAutoCommit(false);
//			stmt = connection.createStatement();
//			// 备份每天的监控数据采集表
//			tmpDayBackup = "monitoritemcollettmpinfor_"+Utilities.getTodayDate();										   
//			String renameSql = "alter table monitoritemcollettmpinfor rename "+tmpDayBackup;
//			stmt.executeUpdate(renameSql.toLowerCase());								
//			FileOperate.copyFile(databasePath+tmpDayBackup+".frm", 
//			        databackupPath+tmpDayBackup+".frm");						
//			FileOperate.copyFile(databasePath+tmpDayBackup+".MYD", 
//			        databackupPath+tmpDayBackup+".MYD");			
//			FileOperate.copyFile(databasePath+tmpDayBackup+".MYI", 
//			        databackupPath+tmpDayBackup+".MYI");			
//			String dropTableSql = "drop table "+tmpDayBackup;
//			stmt.executeUpdate(dropTableSql.toLowerCase());
//			String createTableSql = " CREATE TABLE monitoritemcollettmpinfor (ColletIpaddress char(50) default NULL," 
//			    +" MonitorColletDisplayName char(255) default NULL, MonitorColletOid char(100) default NULL," 
//			    +" MonitorColletValue int(11) default NULL, MonitorColletDate char(10) default NULL," 
//			    +" MonitorColletTime char(20) default NULL, Reserved1 char(100) default NULL," 
//			    +" Reserved2 char(100) default NULL, Reserved3 char(100) default NULL," 
//			    +" Reserved4 char(100) default NULL," 
//			    +" KEY Mdisplayname_Reserved1_IpAddr (MonitorColletDisplayName, Reserved1, ColletIpaddress)" 
//			    +") TYPE=MyISAM; ";
//			stmt.executeUpdate(createTableSql.toLowerCase());
//			connection.commit();
//			
//			// 备份每天的接口数据表
//			tmpDayIfBackup = "ifcollecttmptable_"+Utilities.getTodayDate();
//			renameSql = "alter table ifcollecttmptable rename "+tmpDayIfBackup;
//			stmt.executeUpdate(renameSql.toLowerCase());
//			FileOperate.copyFile(databasePath+tmpDayIfBackup+".frm", 
//			        databackupPath+tmpDayIfBackup+".frm");						
//			FileOperate.copyFile(databasePath+tmpDayIfBackup+".MYD", 
//			        databackupPath+tmpDayIfBackup+".MYD");			
//			FileOperate.copyFile(databasePath+tmpDayIfBackup+".MYI", 
//			        databackupPath+tmpDayIfBackup+".MYI");			
//			dropTableSql = "drop table "+tmpDayIfBackup;
//			stmt.executeUpdate(dropTableSql.toLowerCase());
//			createTableSql = "CREATE TABLE ifcollecttmptable (IdName char(50) NOT NULL default ''," 
//			    +"IfIndex char(10) default NULL, IfUtiliRate int(11) default NULL,"
//			    +"IfInErrorsRate int(11) default NULL, IfOutErrorsRate int(11) default NULL," 
//			    +"IfInDiscardsRate int(11) default NULL, IfOutDiscardsRate int(11) default NULL," 
//			    +"Reserved1 char(20) default NULL, Reserved2 char(20) default NULL," 
//			    +"Reserved3 char(60) default NULL, Reserved4 char(100) default NULL," 
//			    +"KEY IdName (IdName), KEY if_reserved3 (IfIndex,Reserved3))" 
//			    +" TYPE=MyISAM; ";
//			stmt.executeUpdate(createTableSql.toLowerCase());
//			connection.commit();			
//			stmt.close();
//            returnbool = true;
//        }
//		catch (Exception e) {
//		    returnbool = false;
//			e.printStackTrace();
//			try {
//				connection.rollback();
//			}
//			catch(SQLException SqlE) {
//				SqlE.printStackTrace();
//			}
//		}
//		finally {
//            stmt = null;
//			if (outConnection == null) {
//				closeDbPoolConnection(connection);
//			}
//		}
//		
		return returnbool;
    }

    /*
     * @see com.dhcc.ens.persistence.PersistenceServiceable#backupHistoryData()
     */
    public boolean backupHistoryData(Connection outConnection)
    {
        // 取得mysql数据库所在的目录
        boolean returnbool = false;
//        String databasePath = null;
//        String databackupPath = null;
//        Connection connection = null;
//        Statement stmt = null;
//		String osname = System.getProperty("os.name");
//		String userdir = System.getProperty("user.dir");
//		if (getBackUpDirectory().equals(""))
//		{
//			if (osname.toLowerCase().indexOf("windows") >= 0)
//			{
//	        	userdir = userdir.substring(0, userdir.lastIndexOf("\\"));
//	        	databasePath = userdir + "\\database\\data\\ens\\";
//	        	databackupPath = userdir +"\\backup\\";
//	        	System.out.println(" 数据库目录是 "+databasePath);
//	        	System.out.println(" 数据库备份目录是 "+databackupPath);
//	        	DiscoverOper.logService.info(" [备份历史]数据库目录是 "+databasePath);
//	        	DiscoverOper.logService.info(" [备份历史]数据库备份目录是 "+databackupPath);
//			}
//		}
//		else
//		{
//		    userdir = userdir.substring(0, userdir.lastIndexOf("\\"));
//        	databasePath = userdir + "\\database\\data\\ens\\";
//		    databackupPath = getBackUpDirectory();
//		    DiscoverOper.logService.info(" [备份历史]数据库目录是 "+databasePath);
//        	DiscoverOper.logService.info(" [备份历史]数据库备份目录是 "+databackupPath);
//		}
//		
//		long recordsNum = hisDataBkCount;	// 历史表中限定的记录数
//		int hisDataTotalCount = 0;
//		String tmpBackupName = "";
//
//		// 备份monitoritemcolletinfor表
//		String countSql = "SELECT count(*) FROM monitoritemcolletinfor";
//		String[][] res = executeQuery(countSql.toLowerCase(), null);		
//		if (res != null && res.length > 0 && res[0].length > 0) {
//			hisDataTotalCount = Integer.parseInt(res[1][0]);
//		}		
//		
//		DiscoverOper.logService.info(" 0513查询记录数结果 " + hisDataTotalCount+" 当前备份上限是 "+recordsNum);
//		if (hisDataTotalCount >= recordsNum)
//		{				        	        					
//			try 
//	        {
//			    if (outConnection != null)
//					connection = outConnection;
//				else
//					connection = getDbPoolConnection();
//			    
//				connection.setAutoCommit(false);
//				stmt = connection.createStatement();
//				
//				// 备份历史监控数据采集表
//				tmpBackupName = "monitoritemcolletinfor_"+Utilities.getTodayDate();
//				String renameSql = "alter table monitoritemcolletinfor rename "+tmpBackupName;
//				stmt.executeUpdate(renameSql.toLowerCase());
//				FileOperate.copyFile(databasePath + tmpBackupName + ".frm", 
//				        databackupPath + tmpBackupName + ".frm");
//				FileOperate.copyFile(databasePath + tmpBackupName + ".MYD", 
//				        databackupPath + tmpBackupName + ".MYD");
//				FileOperate.copyFile(databasePath + tmpBackupName + ".MYI", 
//				        databackupPath + tmpBackupName + ".MYI");
//				String dropTableSql = "drop table " + tmpBackupName;
//				stmt.executeUpdate(dropTableSql.toLowerCase());
//				String createTableSql = " CREATE TABLE monitoritemcolletinfor ( ColletIpaddress char(50) default NULL," 
//				    +" MonitorColletDisplayName char(255) default NULL, MonitorColletOid char(100) default NULL," 
//				    +" MonitorColletValue int(11) default NULL, MonitorColletDate char(10) default NULL," 
//				    +" MonitorColletTime char(20) default NULL, Reserved1 char(100) default NULL," 
//				    +" Reserved2 char(100) default NULL, Reserved3 char(100) default NULL," 
//				    +" Reserved4 char(100) default NULL," 
//				    +" KEY ColletIpaddress (ColletIpaddress,MonitorColletDisplayName,Reserved1,MonitorColletDate,MonitorColletValue)" 
//				    +") TYPE=MyISAM; ";
//				stmt.executeUpdate(createTableSql.toLowerCase());
//				connection.commit();
//				stmt.close();
//	            returnbool = true;
//	        }
//			catch (Exception e) {
//			    returnbool = false;
//				e.printStackTrace();
//				try {
//					connection.rollback();
//				}
//				catch(SQLException SqlE) {
//					SqlE.printStackTrace();
//				}
//				DiscoverOper.logService.info("<-------------------> [注意]备份monitoritemcolletinfor表时发生错误 "+e.getMessage());
//			}
//			finally {
//	            stmt = null;
//				if (outConnection == null) {
//					closeDbPoolConnection(connection);
//				}
//			}
//		}
//		
//		// 备份ifcollectinfor表
//		hisDataTotalCount = 0;
//		countSql = "SELECT count(*) FROM ifcollectinfor";
//		res = executeQuery(countSql.toLowerCase(), null);		
//		if (res != null && res.length > 0 && res[0].length > 0) {
//			hisDataTotalCount = Integer.parseInt(res[1][0]);
//		}
//				
//		DiscoverOper.logService.info("IfCollect表总记录数为 " + hisDataTotalCount+" 当前备份上限是 "+recordsNum);
//		if (hisDataTotalCount >= recordsNum)
//		{		
//			try 
//	        {
//			    if (outConnection != null)
//					connection = outConnection;
//				else
//					connection = getDbPoolConnection();
//			    
//				connection.setAutoCommit(false);
//				stmt = connection.createStatement();
//			
//				// 备份历史接口数据表
//				tmpBackupName = "ifcollectinfor_" + Utilities.getTodayDate();
//				String renameSql = "alter table ifcollectinfor rename " + tmpBackupName;
//				stmt.executeUpdate(renameSql.toLowerCase());
//				FileOperate.copyFile(databasePath + tmpBackupName + ".frm", 
//				        databackupPath + tmpBackupName + ".frm");
//				FileOperate.copyFile(databasePath + tmpBackupName + ".MYD", 
//				        databackupPath + tmpBackupName + ".MYD");
//				FileOperate.copyFile(databasePath + tmpBackupName + ".MYI", 
//				        databackupPath + tmpBackupName + ".MYI");
//				String dropTableSql = "drop table " + tmpBackupName;
//				stmt.executeUpdate(dropTableSql.toLowerCase());
//				String createTableSql = "CREATE TABLE ifcollectinfor (IdName char(50) NOT NULL default ''," 
//				    +"IfIndex char(10) default NULL, IfUtiliRate int(11) default NULL,"
//				    +"IfInErrorsRate int(11) default NULL, IfOutErrorsRate int(11) default NULL," 
//				    +"IfInDiscardsRate int(11) default NULL, IfOutDiscardsRate int(11) default NULL," 
//				    +"Reserved1 char(20) default NULL, Reserved2 char(20) default NULL," 
//				    +"Reserved3 char(60) default NULL, Reserved4 char(100) default NULL," 
//				    +"KEY IdName (IdName))" 
//				    +" TYPE=MyISAM; ";
//				stmt.executeUpdate(createTableSql.toLowerCase());
//				connection.commit();			
//				stmt.close();
//	            returnbool = true;
//	        }
//			catch (Exception e) {
//			    returnbool = false;
//				e.printStackTrace();
//				try {
//					connection.rollback();
//				}
//				catch(SQLException SqlE) {
//					SqlE.printStackTrace();
//				}
//				DiscoverOper.logService.info("<-------------------> [注意]备份ifcollectinfor表时发生错误 "+e.getMessage());
//			}
//			finally {
//	            stmt = null;
//				if (outConnection == null) {
//					closeDbPoolConnection(connection);
//				}
//			}
//		}
//		
//		// 备份alarminfor表
//		hisDataTotalCount = 0;	
//		recordsNum = alarmDataBkCount;
//		countSql = "SELECT count(*) FROM alarminfor";
//		res = executeQuery(countSql.toLowerCase(), null);		
//		if (res != null && res.length > 0 && res[0].length > 0) {
//			hisDataTotalCount = Integer.parseInt(res[1][0]);
//		}
//		
//		DiscoverOper.logService.info("Alarminfor表总记录数为 " + hisDataTotalCount+" 当前备份上限是 "+recordsNum);
//		if (hisDataTotalCount >= recordsNum)
//		{
//		    try 
//	        {
//			    if (outConnection != null)
//					connection = outConnection;
//				else
//					connection = getDbPoolConnection();
//			    
//				connection.setAutoCommit(false);
//				stmt = connection.createStatement();
//			
//				// 备份告警数据表
//				tmpBackupName = "alarminfor_" + Utilities.getTodayDate();
//				String renameSql = "alter table alarminfor rename " + tmpBackupName;
//				stmt.executeUpdate(renameSql.toLowerCase());
//				FileOperate.copyFile(databasePath + tmpBackupName + ".frm", 
//				        databackupPath + tmpBackupName + ".frm");
//				FileOperate.copyFile(databasePath + tmpBackupName + ".MYD", 
//				        databackupPath + tmpBackupName + ".MYD");
//				FileOperate.copyFile(databasePath + tmpBackupName + ".MYI", 
//				        databackupPath + tmpBackupName + ".MYI");
//				String dropTableSql = "drop table " + tmpBackupName;
//				stmt.executeUpdate(dropTableSql.toLowerCase());
//				String createTableSql = "CREATE TABLE alarminfor (AlarmId varchar(50) NOT NULL default '',"
//				    	+"Ipaddress varchar(100) default NULL, IdName varchar(50) default NULL,"
//				    	+"AlarmData varchar(10) default NULL, AlarmTime varchar(20) default NULL,"
//				    	+"SeverityoftheAlarm char(1) default NULL,SourceOftheAlarm varchar(20) default NULL,"
//				    	+"AlarmDetailInfor text NOT NULL, AlarmDealDoneState char(1) NOT NULL default '',"
//				    	+"Reserved1 varchar(100) default NULL, Reserved2 varchar(100) default NULL,"
//				    	+"Reserved3 varchar(100) default NULL, PRIMARY KEY(AlarmId), "
//				    	+"KEY Ipaddress(Ipaddress,AlarmData))"
//				    	+" TYPE=MyISAM;";
//				stmt.executeUpdate(createTableSql.toLowerCase());
//				connection.commit();			
//				stmt.close();
//	            returnbool = true;
//	        }
//			catch (Exception e) {
//			    returnbool = false;
//				e.printStackTrace();
//				try {
//					connection.rollback();
//				}
//				catch(SQLException SqlE) {
//					SqlE.printStackTrace();
//				}
//				DiscoverOper.logService.info("<-------------------> [注意]备份alarminfor表时发生错误 "+e.getMessage());
//			}
//			finally {
//	            stmt = null;
//				if (outConnection == null) {
//					closeDbPoolConnection(connection);
//				}
//			}
//		}
		
        return returnbool;
    }
    
    /**
     * @return Returns the backUpDirectory.
     */
    public String getBackUpDirectory()
    {
        return backUpDirectory;
    }
    
    /**
     * @param backUpDirectory The backUpDirectory to set.
     */
    public void setBackUpDirectory(String tmpStr)
    {
        backUpDirectory = tmpStr;
    }
    
    /*
     * @see com.dhcc.ens.persistence.PersistenceServiceable#getAlarmDataBkCount()
     */
    public String getAlarmDataBkCount()
    {        
        return new Long(alarmDataBkCount).toString();
    }

    /*
     * @see com.dhcc.ens.persistence.PersistenceServiceable#setAlarmDataBkCount(int)
     */
    public void setAlarmDataBkCount(long alarmRecordCntParam)
    {
        alarmDataBkCount = alarmRecordCntParam;
    }
    
    /*
     * @see com.dhcc.ens.persistence.PersistenceServiceable#getHisDataBkCount()
     */
    public String getHisDataBkCount()
    {        
        return new Long(hisDataBkCount).toString();
    }

    /*
     * @see com.dhcc.ens.persistence.PersistenceServiceable#setHisDataBkCount()
     */
    public void setHisDataBkCount(long hisDataBkCountParam)
    {
        hisDataBkCount = hisDataBkCountParam;
    }
     /*   
    public DTO loadMessages(int pageNO,String recentId)
    {
        Connection conn = getDbPoolConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ResultSetMetaData rsmd = null;
        
        DTO dto = new DTO();
       
        int pageSize = dto.getPageSize(); 
        String currentId = getCurrentId(conn,pstmt,rs);
        int sum = getMessageNum(conn,pstmt,rs);
        
        Timestamp current = new Timestamp(System.currentTimeMillis());
        String sql = "select * from NotifyMessage where endTime > '"+ current +"' order by createTime DESC";
        try
        {
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            int row = 0;
            int nextPage = pageNO + 1;
            int maxPage = 0;
            //若为最后一页或又增加了新的记录(判断最近一条记录的id与当前id是否相同)
            while (rs.next())
            {
                rsmd = rs.getMetaData();
                int numCols = rsmd.getColumnCount();
                if( (sum % pageSize) == 0)
                {
                    maxPage = sum / pageSize;
                }
                else
                {
                    maxPage = sum /pageSize + 1;
                }
                if ((pageNO == maxPage) || (!currentId.equals(recentId)))
                {
                     nextPage = 1;
                }
                dto.setId(currentId);
                dto.setPageNO(nextPage);
                row++;
                if (row > (nextPage - 1) * pageSize && row <= nextPage * pageSize && row <= sum)
                {
                    dto.addObject(buildMessage(rs));
                }
                if (dto.getObjects().size() == pageSize){
                    break;
                }
            }
        }catch(Exception e)
        {
            System.out.println("查询数据库出错:" + e.toString());
        }
        
        
        return dto;
    }
    */
    public int getMessageNum(Connection conn,PreparedStatement pstmt,ResultSet rs)
    {
        Date date = new Date();
        ResultSetMetaData rsmd = null;
        Timestamp current = new Timestamp(System.currentTimeMillis());
//      String sql = "select count(*) from NotifyMessage where endTime > '" + current + "' order by createTime DESC";
        String sql = "select count(*) from NotifyMessage where endTime > '" + current + "'";
        int sum = 0;
        try
        {
           pstmt = conn.prepareStatement(sql);
           rs = pstmt.executeQuery();
           if (rs.next()){
              sum = rs.getInt(1);
           }
           
        }catch(Exception e)
        {
           System.out.println("查询数据库出错:" + e.toString());
        }
        return sum;
       
    }
    
    public String getCurrentId(Connection conn,PreparedStatement pstmt,ResultSet rs)
    {
        Date date = new Date();
        ResultSetMetaData rsmd = null;
        Timestamp current = new Timestamp(System.currentTimeMillis());
        String sql = "select * from NotifyMessage where endTime > '" + current + "' order by createTime DESC";
        String currentId = "";
        try
        {
           pstmt = conn.prepareStatement(sql);
           rs = pstmt.executeQuery();
           if (rs.next()){
              rsmd = rs.getMetaData();
              int numCols = rsmd.getColumnCount();
              currentId = rs.getString(1);
           }
           
        }catch(Exception e)
        {
           System.out.println("查询数据库出错:" + e.toString());
        }
        return currentId;
       
    }
    /*
    Message buildMessage(ResultSet rs){
        Message message = new Message();
        try
        {           
            message.setId(rs.getString(1));
            message.setUser(rs.getString(2));
            message.setContent(rs.getString(3));
            message.setStrCreatedTime((String)rs.getTimestamp(4).toString());
            message.setStrEndedTime((String)rs.getTimestamp(5).toString());
        }
        catch(Exception e)
        {
            System.out.println("getMessage error--" + e.toString());
        }
        return message;
        }
    */
}

