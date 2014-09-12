/**
 * <p>Description:network utilities</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-11
 */

package com.afunms.common.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;
import java.util.Hashtable;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.afunms.polling.PollingEngine;


/**
 * @author Administrator
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CreateTableManager {
	/*
	 * protected DBManager conn;
	 * 
	 * public CreateTableManager() { conn = new DBManager(); } public void
	 * close(){ conn.close(); }
	 */
	public void createTable(DBManager conn, String tablename, String ipstr,String tablestr) {
		try {
			createRootTable(conn, tablename, ipstr);
		} catch (Exception ex) {
			// conn.rollback();
		} finally {
			// conn.close();
		}
	}
	
	public void createVMhostTable(DBManager conn, String tablename, String ipstr,String tablestr) {
		try {
			createVMhostTable(conn, tablename, ipstr);
		} catch (Exception ex) {
			// conn.rollback();
		} finally {
			// conn.close();
		}
	}
	public void createVMguesthostTable(DBManager conn, String tablename, String ipstr,String tablestr) {
		try {
			createVMguesthostTable(conn, tablename, ipstr);
		} catch (Exception ex) {
			// conn.rollback();
		} finally {
			// conn.close();
		}
	}
	
	public void createVMCRTable(DBManager conn, String tablename, String ipstr,String tablestr) {
		try {
			createVMCRTable(conn, tablename, ipstr);
		} catch (Exception ex) {
			// conn.rollback();
		} finally {
			// conn.close();
		}
	}
	public void createVMDSTable(DBManager conn, String tablename, String ipstr,String tablestr) {
		try {
			createVMDSTable(conn, tablename, ipstr);
		} catch (Exception ex) {
			// conn.rollback();
		} finally {
			// conn.close();
		}
	}
	public void createVMRPTable(DBManager conn, String tablename, String ipstr,String tablestr) {
		try {
			createVMRPTable(conn, tablename, ipstr);
		} catch (Exception ex) {
			// conn.rollback();
		} finally {
			// conn.close();
		}
	}
	
	
	public void createVMBaseTable(DBManager conn, String tablename, String ipstr,String tablestr) {
		try {
			createVMBaseTable(conn, tablename, ipstr);
		} catch (Exception ex) {
			// conn.rollback();
		} finally {
			// conn.close();
		}
	}
	
	public void createEmcTable(DBManager conn, String tablename, String ipstr,String tablestr) {
		try {
			createEmcTable(conn, tablename, ipstr);
		} catch (Exception ex) {
			// conn.rollback();
		} finally {
			// conn.close();
		}
	}
	

	public void createCiscoCMTSTable(DBManager conn, String tablename,String ipstr, String tablestr) {
		try {
			createCiscoCMTSRootTable(conn, tablename, ipstr);
		} catch (Exception ex) {
			// conn.rollback();
		} finally {
			// conn.close();
		}
	}
	
	public void createCiscoCMTSIPMACTable(DBManager conn, String tablename,String ipstr, String tablestr) {
		try {
			createCiscoCMTSIPMACRootTable(conn, tablename, ipstr);
		} catch (Exception ex) {
			// conn.rollback();
		} finally {
			// conn.close();
		}
	}
	
	public void createInformixLogTable(DBManager conn, String tablename,String ipstr, String tablestr) {
		try {
			createInformixLogRootTable(conn, tablename, ipstr);
		} catch (Exception ex) {
			// conn.rollback();
		} finally {
			// conn.close();
		}
	}

	public void createBNodeTable(DBManager conn, String tablename,
			String ipstr, String tablestr) {
		try {
			createBNodeRootTable(conn, tablename, ipstr);
		} catch (Exception ex) {
			conn.rollback();
		} finally {
			// conn.close();
		}
	}

	public void createGrapesTable(DBManager conn, String tablename, String ipstr) {
		try {
			createRootTable(conn, tablename, ipstr);
		} catch (Exception ex) {
			conn.rollback();
		} finally {
			// conn.close();
		}
	}

	public void createSyslogTable(DBManager conn, String tablename,
			String ipstr, String tablestr) {
		try {
			createSyslogRootTable(conn, tablename, ipstr);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
		}
	}

	public void deleteTable(DBManager conn, String tablename, String ipstr,
			String tablestr) {
		try {
			dropRootTable(conn, tablename, ipstr);
//			System.out.println("CreateTableManager.java-------160hang----->>>>"+tablename+ipstr);
		} catch (Exception ex) {
		} finally {
		}
	}

	public void createSyslogRootTable(DBManager conn, String tablename,
			String ipstr) {
		String sql = "";
		if(SystemConstant.getDBType().equals("mysql")){
			sql = "create table "
				+ tablename
				+ ipstr
				+ "(ID bigint(20) not null auto_increment,IPADDRESS VARCHAR(30),hostname VARCHAR(20),message VARCHAR(2500),"
				+ "facility bigint(10),priority bigint(10),priorityName VARCHAR(100),facilityName VARCHAR(60),"
				+ "processId bigint(10),processName VARCHAR(100),processIdStr VARCHAR(30),"
				+ "recordtime timestamp,username VARCHAR(100),eventid bigint(10), PRIMARY KEY  (ID)) ENGINE=InnoDB DEFAULT CHARSET=gb2312";
		}else if(SystemConstant.getDBType().equals("oracle")){
			sql="create table "+tablename+ipstr
		 	+"(ID number(20) not null,IPADDRESS VARCHAR2(20),hostname VARCHAR2(100),message VARCHAR2(4000),"
		 	+"facility number(10),priority number(10),priorityName VARCHAR2(100),facilityName VARCHAR2(60),"
		 	+"processId number(10),processName VARCHAR2(100),processIdStr VARCHAR2(30),"
		 	+"recordtime date,username VARCHAR2(100),eventid number(10), PRIMARY KEY  (ID)) ";	 
		}
		SysLogger.info(sql);
		try {
			conn.executeUpdate(sql);
			if(SystemConstant.DBType.equals("oracle")){
				CreateTableManager.createSeqOrcl(conn, tablename, ipstr);
				CreateTableManager.createTrigerOrcl(conn, tablename, ipstr, tablename);
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (Exception ex) {

			}

		} finally {
		}
	}

	public void createRootTable(DBManager conn, String tablename, String ipstr) {
		String sql = "";
		if(SystemConstant.getDBType().equals("mysql")){
			//mysql
			if (tablename.indexOf("hour") >= 0) {
				// 创建小时表格
				sql = "create table "
						+ tablename
						+ ipstr
						+ "(ID bigint(20) not null auto_increment,IPADDRESS VARCHAR(30),RESTYPE VARCHAR(20),CATEGORY VARCHAR(50),ENTITY VARCHAR(100),SUBENTITY VARCHAR(60),"
						+ "THEVALUE bigint(255),COLLECTTIME timestamp,UNIT VARCHAR(30),COUNT bigint(20),BAK VARCHAR(100),CHNAME VARCHAR(100),"
						+ " PRIMARY KEY  (ID)) ENGINE=InnoDB DEFAULT CHARSET=gb2312";
			} else if (tablename.indexOf("day") >= 0) {
				// 创建天的表
				sql = "create table "
						+ tablename
						+ ipstr
						+ "(ID bigint(20) not null auto_increment,IPADDRESS VARCHAR(30),RESTYPE VARCHAR(20),CATEGORY VARCHAR(50),ENTITY VARCHAR(100),SUBENTITY VARCHAR(60),"
						+ "THEVALUE bigint(255),COLLECTTIME timestamp ,UNIT VARCHAR(30),COUNT bigint(20),BAK VARCHAR(100),CHNAME VARCHAR(100),"
						+ " PRIMARY KEY  (ID)) ENGINE=InnoDB DEFAULT CHARSET=gb2312";
			} else if (tablename.indexOf("utilhdx") >= 0) {
				// 创建天的表
				sql = "create table "
						+ tablename
						+ ipstr
						+ "(ID bigint(20) not null auto_increment,IPADDRESS VARCHAR(30),RESTYPE VARCHAR(20),CATEGORY VARCHAR(50),ENTITY VARCHAR(100),SUBENTITY VARCHAR(60),"
						+ "THEVALUE bigint(255),COLLECTTIME timestamp ,UNIT VARCHAR(30),COUNT bigint(20),BAK VARCHAR(100),CHNAME VARCHAR(100),"
						+ " PRIMARY KEY  (ID)) ENGINE=InnoDB DEFAULT CHARSET=gb2312";
			} else if (tablename.indexOf("software") >= 0) {
				sql = "CREATE TABLE "
						+ tablename
						+ ipstr
						+ "(ID bigint(20) not null auto_increment,IPADDRESS VARCHAR(30),name varchar(200),swid varchar(100),"
						+ "type varchar(100),insdate varchar(100),PRIMARY KEY  (ID)) ENGINE=InnoDB DEFAULT CHARSET=gb2312";
			} 
			else {
				// 创建按分钟采集数据的表
				sql = "create table "
						+ tablename
						+ ipstr
						+ "(ID bigint(20) not null auto_increment,IPADDRESS VARCHAR(30),RESTYPE VARCHAR(20),CATEGORY VARCHAR(50),ENTITY VARCHAR(100),SUBENTITY VARCHAR(60),"
						+ "THEVALUE    VARCHAR(255),COLLECTTIME timestamp,UNIT VARCHAR(30),COUNT bigint(20),BAK VARCHAR(100),CHNAME VARCHAR(100),"
						+ " PRIMARY KEY  (ID)) ENGINE=InnoDB DEFAULT CHARSET=gb2312";
			}
		}else if(SystemConstant.getDBType().equals("oracle")){
			
			if (tablename.indexOf("hour")>=0){
		 		//创建小时表格
			 	sql="create table "+tablename+ipstr
			 	+"(ID number(20) not null ,IPADDRESS VARCHAR2(30),RESTYPE VARCHAR2(20),CATEGORY VARCHAR2(50),ENTITY VARCHAR2(100),SUBENTITY VARCHAR2(60),"
			 	+"THEVALUE VARCHAR2(255),COLLECTTIME date default sysdate-1/24,UNIT VARCHAR2(30),COUNT number(20),BAK VARCHAR2(100),CHNAME VARCHAR2(100),"
			 	+" PRIMARY KEY  (ID)) ";
		 	}else if (tablename.indexOf("day")>=0){
		 		//创建天的表
			 	sql="create table "+tablename+ipstr
			 	+"(ID number(20) not null ,IPADDRESS VARCHAR2(30),RESTYPE VARCHAR2(20),CATEGORY VARCHAR2(50),ENTITY VARCHAR2(100),SUBENTITY VARCHAR2(60),"
			 	+"THEVALUE VARCHAR2(255),COLLECTTIME date default sysdate-1,UNIT VARCHAR2(30),COUNT number(20),BAK VARCHAR2(100),CHNAME VARCHAR2(100),"
			 	+" PRIMARY KEY  (ID)) ";
		 	} else if (tablename.indexOf("utilhdx") >= 0) {
				// 创建天的表
		 		sql="create table "+tablename+ipstr
			 	+"(ID number(20) not null ,IPADDRESS VARCHAR2(30),RESTYPE VARCHAR2(20),CATEGORY VARCHAR2(50),ENTITY VARCHAR2(100),SUBENTITY VARCHAR2(60),"
			 	+"THEVALUE VARCHAR2(255),COLLECTTIME date default sysdate-1,UNIT VARCHAR2(30),COUNT number(20),BAK VARCHAR2(100),CHNAME VARCHAR2(100),"
			 	+" PRIMARY KEY  (ID)) ";
			}else if (tablename.indexOf("software")>=0){
			 		sql="CREATE TABLE "+tablename+ipstr
			 		+"(ID number(20) not null ,IPADDRESS VARCHAR2(30),name VARCHAR2(200),swid VARCHAR2(100),"
			 		+"type VARCHAR2(100),insdate VARCHAR2(100),PRIMARY KEY  (ID)) ";
		 	}else{
		 		//创建按分钟采集数据的表
			 	sql="create table "+tablename+ipstr
			 	+"(ID number(20) not null ,IPADDRESS VARCHAR2(30),RESTYPE VARCHAR2(20),CATEGORY VARCHAR2(50),ENTITY VARCHAR2(100),SUBENTITY VARCHAR2(60),"
			 	+"THEVALUE    VARCHAR2(255),COLLECTTIME date,UNIT VARCHAR2(30),COUNT number(20),BAK VARCHAR2(100),CHNAME VARCHAR2(100),"
			 	+" PRIMARY KEY  (ID)) ";
		 	}
		}
		SysLogger.info(sql);
		try {
			conn.executeUpdate(sql);
			if(SystemConstant.DBType.equals("oracle")){
				CreateTableManager.createSeqOrcl(conn, tablename, ipstr);
				CreateTableManager.createTrigerOrcl(conn, tablename, ipstr, tablename);
			}
		} catch (Exception e) {
			try {
				// conn.rollback();
			} catch (Exception ex) {

			}

		} finally {
		}
	}
	
	
	
	
	
	

	public void createBNodeRootTable(DBManager conn, String tablename,
			String ipstr) {
		String sql = "";
		if(SystemConstant.getDBType().equals("mysql")){
			if (tablename.indexOf("hour") >= 0) {
				// 创建小时表格
				sql = "create table "
						+ tablename
						+ ipstr
						+ "(ID bigint(20) not null auto_increment,THEVALUE VARCHAR(255),RESPONSETIME VARCHAR(100),COLLECTTIME timestamp, "
						+ " PRIMARY KEY  (ID)) ENGINE=InnoDB DEFAULT CHARSET=gb2312";
			} else if (tablename.indexOf("day") >= 0) {
				// 创建天的表
				sql = "create table "
						+ tablename
						+ ipstr
						+ "(ID bigint(20) not null auto_increment,THEVALUE VARCHAR(255),RESPONSETIME VARCHAR(100),COLLECTTIME timestamp, "
						+ " PRIMARY KEY  (ID)) ENGINE=InnoDB DEFAULT CHARSET=gb2312";
			} else {
				// 创建按分钟采集数据的表
				sql = "create table "
						+ tablename
						+ ipstr
						+ "(ID bigint(20) not null auto_increment,THEVALUE VARCHAR(255),RESPONSETIME VARCHAR(100),COLLECTTIME timestamp, "
						+ " PRIMARY KEY  (ID)) ENGINE=InnoDB DEFAULT CHARSET=gb2312";
			}
		}else if(SystemConstant.getDBType().equals("oracle")){
			if (tablename.indexOf("hour")>=0){
		 		//创建小时表格
			 	sql="create table "+tablename+ipstr
			 	+"(ID number(20) not null ,THEVALUE VARCHAR2(255),RESPONSETIME VARCHAR2(100),COLLECTTIME date default sysdate-1/24, "
			 	+" PRIMARY KEY  (ID)) ";
		 	}else if (tablename.indexOf("day")>=0){
		 		//创建天的表
			 	sql="create table "+tablename+ipstr
			 	+"(ID number(20) not null ,THEVALUE VARCHAR2(255),RESPONSETIME VARCHAR2(100),COLLECTTIME date default sysdate-1, "
			 	+" PRIMARY KEY  (ID)) ";
		 	}else{
		 		//创建按分钟采集数据的表
			 	sql="create table "+tablename+ipstr
			 	+"(ID number(20) not null ,THEVALUE VARCHAR2(255),RESPONSETIME VARCHAR2(100),COLLECTTIME date, "
			 	+" PRIMARY KEY  (ID)) ";
		 	}
		}
		SysLogger.info(sql);
		try {
			conn.executeUpdate(sql);
			if(SystemConstant.DBType.equals("oracle")){
				CreateTableManager.createSeqOrcl(conn, tablename, ipstr);
				CreateTableManager.createTrigerOrcl(conn, tablename, ipstr, tablename);
			}
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (Exception ex) {

			}

		} finally {
		}
	}

	public void createGrapesRootTable(DBManager conn, String tablename,
			String ipstr) {
		PreparedStatement stmt = null;
		String sql = "";
		if(SystemConstant.getDBType().equals("mysql")){
			if (tablename.indexOf("hour") >= 0) {
				// 创建小时表格
				sql = "create table "
						+ tablename
						+ ipstr
						+ "(ID bigint(20) not null auto_increment,IPADDRESS VARCHAR(30),RESTYPE VARCHAR(20),CATEGORY VARCHAR(50),ENTITY VARCHAR(100),SUBENTITY VARCHAR(60),"
						+ "THEVALUE VARCHAR(255),COLLECTTIME timestamp,UNIT VARCHAR(30),COUNT bigint(20),BAK VARCHAR(100),CHNAME VARCHAR(100),"
						+ " PRIMARY KEY  (ID)) ENGINE=InnoDB DEFAULT CHARSET=gb2312";
			} else if (tablename.indexOf("day") >= 0) {
				// 创建天的表
				sql = "create table "
						+ tablename
						+ ipstr
						+ "(ID bigint(20) not null auto_increment,IPADDRESS VARCHAR(30),RESTYPE VARCHAR(20),CATEGORY VARCHAR(50),ENTITY VARCHAR(100),SUBENTITY VARCHAR(60),"
						+ "THEVALUE VARCHAR(255),COLLECTTIME timestamp ,UNIT VARCHAR(30),COUNT bigint(20),BAK VARCHAR(100),CHNAME VARCHAR(100),"
						+ " PRIMARY KEY  (ID)) ENGINE=InnoDB DEFAULT CHARSET=gb2312";
			} else {
				// 创建按分钟采集数据的表
				sql = "create table "
						+ tablename
						+ ipstr
						+ "(ID bigint(20) not null auto_increment,IPADDRESS VARCHAR(30),RESTYPE VARCHAR(20),CATEGORY VARCHAR(50),ENTITY VARCHAR(100),SUBENTITY VARCHAR(60),"
						+ "THEVALUE    VARCHAR(255),COLLECTTIME timestamp,UNIT VARCHAR(30),COUNT bigint(20),BAK VARCHAR(100),CHNAME VARCHAR(100),"
						+ " PRIMARY KEY  (ID)) ENGINE=InnoDB DEFAULT CHARSET=gb2312";
			}

		}else if(SystemConstant.getDBType().equals("oracle")){
			if (tablename.indexOf("hour")>=0){
		 		//创建小时表格
			 	sql="create table "+tablename+ipstr
			 	+"(ID number(20) not null ,IPADDRESS VARCHAR2(30),RESTYPE VARCHAR2(20),CATEGORY VARCHAR2(50),ENTITY VARCHAR2(100),SUBENTITY VARCHAR2(60),"
			 	+"THEVALUE VARCHAR2(255),COLLECTTIME date default sysdate-1/24,UNIT VARCHAR2(30),COUNT number(20),BAK VARCHAR2(100),CHNAME VARCHAR2(100),"
			 	+" PRIMARY KEY  (ID)) ";
		 	}else if (tablename.indexOf("day")>=0){
		 		//创建天的表
			 	sql="create table "+tablename+ipstr
			 	+"(ID number(20) not null ,IPADDRESS VARCHAR2(30),RESTYPE VARCHAR2(20),CATEGORY VARCHAR2(50),ENTITY VARCHAR2(100),SUBENTITY VARCHAR2(60),"
			 	+"THEVALUE VARCHAR2(255),COLLECTTIME date default sysdate-1,UNIT VARCHAR2(30),COUNT number(20),BAK VARCHAR2(100),CHNAME VARCHAR2(100),"
			 	+" PRIMARY KEY  (ID)) ";
		 	}else{
		 		//创建按分钟采集数据的表
			 	sql="create table "+tablename+ipstr
			 	+"(ID number(20) not null ,IPADDRESS VARCHAR2(30),RESTYPE VARCHAR2(20),CATEGORY VARCHAR2(50),ENTITY VARCHAR2(100),SUBENTITY VARCHAR2(60),"
			 	+"THEVALUE    VARCHAR2(255),COLLECTTIME date,UNIT VARCHAR2(30),COUNT number(20),BAK VARCHAR2(100),CHNAME VARCHAR2(100),"
			 	+" PRIMARY KEY  (ID)) ";
		 	}
		}
		SysLogger.info(sql);
		try {
			conn.executeUpdate(sql);
			if(SystemConstant.DBType.equals("oracle")){
				CreateTableManager.createSeqOrcl(conn, tablename, ipstr);
				CreateTableManager.createTrigerOrcl(conn, tablename, ipstr, tablename);
			}
		} catch (Exception e) {
			try {
				// conn.rollback();
			} catch (Exception ex) {

			}

		} finally {
		}
	}

	public void createInformixLogRootTable(DBManager conn, String tablename,
			String ipstr) {
		PreparedStatement stmt = null;
		String sql = "";
		// 创建按分钟采集数据的表
		if(SystemConstant.getDBType().equals("mysql")){
			// 创建按分钟采集数据的表
			sql = "create table "
					+ tablename
					+ ipstr
					+ "(ID bigint(20) not null auto_increment,DBNODEID VARCHAR(30),DETAIL VARCHAR(500),"
					+ "COLLECTTIME timestamp,"
					+ " PRIMARY KEY  (ID)) ENGINE=InnoDB DEFAULT CHARSET=gb2312";
		}else if(SystemConstant.getDBType().equals("oracle")){
			//创建按分钟采集数据的表
		 	sql="create table "+tablename+ipstr
		 	+"(ID number(20) not null ,DBNODEID VARCHAR2(30),DETAIL VARCHAR2(500),"
		 	+"COLLECTTIME date,"
		 	+" PRIMARY KEY  (ID))";	
		}
		SysLogger.info(sql);
		try {
			conn.executeUpdate(sql);
			if(SystemConstant.DBType.equals("oracle")){
				CreateTableManager.createSeqOrcl(conn, tablename, ipstr);
				CreateTableManager.createTrigerOrcl(conn, tablename, ipstr, tablename);
			}
		} catch (Exception e) {
			try {
				// conn.rollback();
			} catch (Exception ex) {

			}

		} finally {
		}
	}
	
	public void createCiscoCMTSRootTable(DBManager conn, String tablename,String ipstr) {
		String sql = "";
		// 创建按分钟采集数据信噪比的表
		if(SystemConstant.getDBType().equals("mysql")){
			// 创建按分钟采集数据的表
			sql = "create table "
					+ tablename
					+ ipstr
					+ "(ID bigint(11) not null auto_increment,channelid int(11),userindex int(11),"
					+ "UpChannel int(11),DownChannel int(11),channledirect int(1),thevalue int(10),"
					+ "COLLECTTIME timestamp,"
					+ " PRIMARY KEY  (ID)) ENGINE=InnoDB DEFAULT CHARSET=gb2312";
		}else if(SystemConstant.getDBType().equals("oracle")){
			//创建按分钟采集数据的表
		 	sql="create table "+tablename+ipstr
		 	+"(ID number(11) not null ,channelid number(11),userindex number(11),"
		 	+" UpChannel number(11),DownChannel number(11),channledirect number(1),thevalue number(10)"
		 	+"COLLECTTIME date,"
		 	+" PRIMARY KEY  (ID))";	
		}
		SysLogger.info(sql);
		try {
			conn.executeUpdate(sql);
			if(SystemConstant.DBType.equals("oracle")){
				CreateTableManager.createSeqOrcl(conn, tablename, ipstr);
				CreateTableManager.createTrigerOrcl(conn, tablename, ipstr, tablename);
			}
		} catch (Exception e) {
			try {
				// conn.rollback();
			} catch (Exception ex) {

			}

		} finally {
		}
	}
	
	public void createCiscoCMTSIPMACRootTable(DBManager conn, String tablename,String ipstr) {
		String sql = "";
		// 创建按分钟采集数据信噪比的表
		if(SystemConstant.getDBType().equals("mysql")){
			// 创建按分钟采集数据的表
			sql = "create table "
					+ tablename
					+ ipstr
					+ "(ID bigint(11) not null auto_increment,ipaddress VARCHAR(15),mac VARCHAR(17),"
					+ "status int(1),"
					+ "COLLECTTIME timestamp,"
					+ " PRIMARY KEY  (ID)) ENGINE=InnoDB DEFAULT CHARSET=gb2312";
		}else if(SystemConstant.getDBType().equals("oracle")){
			//创建按分钟采集数据的表
		 	sql="create table "+tablename+ipstr
		 	+"(ID number(11) not null ,ipaddress VARCHAR2(15),mac VARCHAR2(11),"
		 	+" status number(11)"
		 	+"COLLECTTIME date,"
		 	+" PRIMARY KEY  (ID))";	
		}
		SysLogger.info(sql);
		try {
			conn.executeUpdate(sql);
			if(SystemConstant.DBType.equals("oracle")){
				CreateTableManager.createSeqOrcl(conn, tablename, ipstr);
				CreateTableManager.createTrigerOrcl(conn, tablename, ipstr, tablename);
			}
		} catch (Exception e) {
			try {
				// conn.rollback();
			} catch (Exception ex) {

			}

		} finally {
		}
	}

	public void dropRootTable(DBManager conn, String tablename, String ipstr) {
		String sql = "";
		if(SystemConstant.DBType.equals("mysql")){
			sql = "drop table if exists " + tablename + ipstr;
		}else if(SystemConstant.DBType.equals("oracle")){
			sql = "drop table " + tablename + ipstr;
			CreateTableManager.dropSeqOrcl(conn, tablename, ipstr);
		}
		// SysLogger.info(sql);
		try {
			conn.executeUpdate(sql);
		} catch (Exception e) {

			try {
				// conn.rollback();
			} catch (Exception ex) {

			}

		} finally {
			// conn.commit();
		}
	}

	public void createIndex(DBManager con, String tname, String ipstr,
			String indexsub, String tablename, String fieldname) {
		String indexstr = "";
		indexstr = "create index " + tname + ipstr + indexsub + " on "
				+ tablename + ipstr + " (" + fieldname
				+ ") tablespace DHCC_ITTABSPACE";
		//System.out.println(indexstr);
		try {
//			stmt = con.prepareStatement(indexstr);
//			stmt.execute();
//			stmt.close();
			con.executeUpdate(indexstr);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				con.rollback();
				// DataGate.freeCon(con);
			} catch (Exception ex) {

			}
		} finally {
			try {
//				if (stmt != null)
//					stmt.close();
			} catch (Exception e) {

			}
		}
	}

	public void createSeq(DBManager con, String tablestr, String ipstr) {
		String createSeqStr = "";
		createSeqStr = "create sequence "
				+ tablestr
				+ "_"
				+ ipstr
				+ "SEQ minvalue 1 maxvalue 999999999999999999999999999 start with 1 increment by 1 cache 10";
		//System.out.println(createSeqStr);
		try {
//			stmt = con.prepareStatement(createSeqStr);
//			stmt.execute();
//			stmt.close();
			con.executeUpdate(createSeqStr);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				// con.rollback();
				// DataGate.freeCon(con);
			} catch (Exception ex) {

			}

		}

	}

	public void dropSeq(DBManager con, String tablestr, String ipstr) {
		String createSeqStr = "";
		createSeqStr = "drop sequence " + tablestr + "_" + ipstr + "SEQ";
		SysLogger.info(createSeqStr);
		try {
			con.executeUpdate(createSeqStr);
		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	public void createTriger(DBManager con, String tablestr, String ipstr,
			String tablename) {
//		PreparedStatement stmt = null;
		String trigerstr = "";
		trigerstr = "create or replace trigger " + tablestr + ipstr
				+ "id before insert on " + tablename + ipstr
				+ " for each row when (new.id is null) begin " + " select "
				+ tablestr + "_" + ipstr
				+ "SEQ.nextval into :new.id from dual; end;";
		System.out.println(trigerstr);
		try {
//			stmt = con.prepareStatement(trigerstr);
//			stmt.execute();
//			stmt.close();
			con.executeUpdate(trigerstr);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				// con.rollback();
				// DataGate.freeCon(con);
			} catch (Exception ex) {

			}
		} finally {
			try {
//				if (stmt != null)
//					stmt.close();
			} catch (Exception e) {

			}
		}
	}

	public void dropDbconfigInfo(DBManager conn, String tablename, String ipstr) {
		PreparedStatement stmt = null;
		String sql = "";
		sql = "delete from " + tablename + " where ipaddress = '" + ipstr + "'";
		try {
			conn.executeUpdate(sql);
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (Exception ex) {

			}

		} finally {
		}
	}

	public void createWasTable(DBManager conn, String tablename, String ipstr) {
		try {
			// con=DataGate.getCon();
			createWasRootTable(conn, tablename, ipstr);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
		}
	}

	public void createWasRootTable(DBManager conn, String tablename,
			String ipstr) {
		String sql = "";
		if(SystemConstant.getDBType().equals("mysql")){
			if (tablename.indexOf("system") >= 0) {
				sql = "create table "
						+ tablename
						+ ipstr
						+ "(ID bigint(20) not null auto_increment,IPADDRESS VARCHAR(30),freeMemory VARCHAR(200),cpuUsageSinceServerStarted VARCHAR(200),"
						+ "cpuUsageSinceLastMeasurement VARCHAR(200),recordtime timestamp,PRIMARY KEY  (ID)) ENGINE=InnoDB DEFAULT CHARSET=gb2312";
				// }
				// System.out.println(sql);
			} else if (tablename.indexOf("jdbc") >= 0) {
				sql = "create table "
						+ tablename
						+ ipstr
						+ "(ID bigint(20) not null auto_increment,IPADDRESS VARCHAR(30),freePoolSize VARCHAR(200),useTime VARCHAR(200),"
						+ "prepStmtCacheDiscardCount VARCHAR(200),waitingThreadCount VARCHAR(200),allocateCount VARCHAR(200),"
						+ "faultCount VARCHAR(200),waitTime VARCHAR(200),createCount VARCHAR(200),jdbcTime VARCHAR(200),percentUsed VARCHAR(200),"
						+ "poolSize VARCHAR(200),closeCount VARCHAR(200),recordtime timestamp,PRIMARY KEY  (ID)) ENGINE=InnoDB DEFAULT CHARSET=gb2312";
				// }
				// System.out.println(sql);
			} else if (tablename.indexOf("session") >= 0) {
				sql = "create table "
						+ tablename
						+ ipstr
						+ "(ID bigint(20) not null auto_increment,IPADDRESS VARCHAR(30),activeCount VARCHAR(20),createCount VARCHAR(20),"
						+ "invalidateCount VARCHAR(20),lifeTime VARCHAR(20),liveCount VARCHAR(20),"
						+ "timeoutInvalidationCount VARCHAR(20),recordtime timestamp,PRIMARY KEY  (ID)) ENGINE=InnoDB DEFAULT CHARSET=gb2312";
				// }
				// System.out.println(sql);
			} else if (tablename.indexOf("jvminfo") >= 0) {
				sql = "create table "
						+ tablename
						+ ipstr
						+ "(ID bigint(20) not null auto_increment,IPADDRESS VARCHAR(30),freeMemory VARCHAR(20),heapSize VARCHAR(20),"
						+ "upTime VARCHAR(20),usedMemory VARCHAR(20),memPer VARCHAR(20),recordtime timestamp,PRIMARY KEY  (ID)) ENGINE=InnoDB DEFAULT CHARSET=gb2312";
				// }
				// System.out.println(sql);
			} else if (tablename.indexOf("cache") >= 0) {
				sql = "create table "
						+ tablename
						+ ipstr
						+ "(ID bigint(20) not null auto_increment,IPADDRESS VARCHAR(30),inMemoryCacheCount VARCHAR(20),maxInMemoryCacheCount VARCHAR(20),"
						+ "timeoutInvalidationCount VARCHAR(20),recordtime timestamp,PRIMARY KEY  (ID)) ENGINE=InnoDB DEFAULT CHARSET=gb2312";
				// //}
				// System.out.println(sql);
			} else if (tablename.indexOf("thread") >= 0) {
				sql = "create table "
						+ tablename
						+ ipstr
						+ "(ID bigint(20) not null auto_increment,IPADDRESS VARCHAR(30),activeCount VARCHAR(20),createCount VARCHAR(20),"
						+ "destroyCount VARCHAR(20),poolSize VARCHAR(20),recordtime timestamp,PRIMARY KEY  (ID)) ENGINE=InnoDB DEFAULT CHARSET=gb2312";
				// }
				// System.out.println(sql);
			} else if (tablename.indexOf("trans") >= 0) {
				sql = "create table "
						+ tablename
						+ ipstr
						+ "(ID bigint(20) not null auto_increment,IPADDRESS VARCHAR(30),activeCount VARCHAR(20),committedCount VARCHAR(20),"
						+ "globalBegunCount VARCHAR(20),globalTimeoutCount VARCHAR(20),globalTranTime VARCHAR(20),"
						+ "localActiveCount VARCHAR(20),localBegunCount VARCHAR(20),localRolledbackCount VARCHAR(20),localTimeoutCount VARCHAR(20),localTranTime VARCHAR(20),"
						+ "rolledbackCount VARCHAR(20),recordtime timestamp,PRIMARY KEY  (ID)) ENGINE=InnoDB DEFAULT CHARSET=gb2312";
				// }
				// System.out.println(sql);
			}
		}else if(SystemConstant.getDBType().equals("oracle")){
			if (tablename.indexOf("system") >= 0) {
				sql = "create table "
						+ tablename
						+ ipstr
						+ "(ID number(20) not null ,IPADDRESS VARCHAR2(30),freeMemory VARCHAR2(200),cpuUsageSinceServerStarted VARCHAR2(200),"
						+ "cpuUsageSinceLastMeasurement VARCHAR2(200),recordtime date default sysdate-1,PRIMARY KEY  (ID))";

				// }
				// System.out.println(sql);
			} else if (tablename.indexOf("jdbc") >= 0) {
				sql = "create table "
						+ tablename
						+ ipstr
						+ "(ID number(20) not null,IPADDRESS VARCHAR2(30),freePoolSize VARCHAR2(200),useTime VARCHAR2(200),"
						+ "prepStmtCacheDiscardCount VARCHAR2(200),waitingThreadCount VARCHAR2(200),allocateCount VARCHAR2(200),"
						+ "faultCount VARCHAR2(200),waitTime VARCHAR2(200),createCount VARCHAR2(200),jdbcTime VARCHAR2(200),percentUsed VARCHAR2(200),"
						+ "poolSize VARCHAR2(200),closeCount VARCHAR2(200),recordtime date default sysdate-1,PRIMARY KEY  (ID))";

				// }
				// System.out.println(sql);
			} else if (tablename.indexOf("session") >= 0) {
				sql = "create table "
						+ tablename
						+ ipstr
						+ "(ID number(20) not null,IPADDRESS VARCHAR2(30),activeCount VARCHAR2(20),createCount VARCHAR2(20),"
						+ "invalidateCount VARCHAR2(20),lifeTime VARCHAR2(20),liveCount VARCHAR2(20),"
						+ "timeoutInvalidationCount VARCHAR2(20),recordtime date default sysdate-1,PRIMARY KEY  (ID))";

				// }
				// System.out.println(sql);
			} else if (tablename.indexOf("jvminfo") >= 0) {
				sql = "create table "
						+ tablename
						+ ipstr
						+ "(ID number(20) not null,IPADDRESS VARCHAR2(30),freeMemory VARCHAR2(20),heapSize VARCHAR2(20),"
						+ "upTime VARCHAR2(20),usedMemory VARCHAR2(20),memPer VARCHAR2(20),recordtime date default sysdate-1,PRIMARY KEY  (ID))";
				// }
				// System.out.println(sql);
			} else if (tablename.indexOf("cache") >= 0) {
				sql = "create table "
						+ tablename
						+ ipstr
						+ "(ID number(20) not null,IPADDRESS VARCHAR2(30),inMemoryCacheCount VARCHAR2(20),maxInMemoryCacheCount VARCHAR2(20),"
						+ "timeoutInvalidationCount VARCHAR2(20),recordtime date default sysdate-1,PRIMARY KEY  (ID))";
				// //}
				// System.out.println(sql);
			} else if (tablename.indexOf("thread") >= 0) {
				sql = "create table "
						+ tablename
						+ ipstr
						+ "(ID number(20) not null,IPADDRESS VARCHAR2(30),activeCount VARCHAR2(20),createCount VARCHAR2(20),"
						+ "destroyCount VARCHAR2(20),poolSize VARCHAR2(20),recordtime date default sysdate-1,PRIMARY KEY  (ID))";
				// }
				// System.out.println(sql);
			} else if (tablename.indexOf("trans") >= 0) {
				sql = "create table "
						+ tablename
						+ ipstr
						+ "(ID number(20) not null,IPADDRESS VARCHAR2(30),activeCount VARCHAR2(20),committedCount VARCHAR2(20),"
						+ "globalBegunCount VARCHAR2(20),globalTimeoutCount VARCHAR2(20),globalTranTime VARCHAR2(20),"
						+ "localActiveCount VARCHAR2(20),localBegunCount VARCHAR2(20),localRolledbackCount VARCHAR2(20),localTimeoutCount VARCHAR2(20),localTranTime VARCHAR2(20),"
						+ "rolledbackCount VARCHAR2(20),recordtime date default sysdate-1,PRIMARY KEY  (ID))";
				// }
				 System.out.println(sql);
			}
		}
		try {
			conn.executeUpdate(sql);
			if(SystemConstant.DBType.equals("oracle")){
				CreateTableManager.createSeqOrcl(conn, tablename, ipstr);
				CreateTableManager.createTrigerOrcl(conn, tablename, ipstr, tablename);
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (Exception ex) {

			}

		} finally {
		}
	}

	public void createTelnetTable(DBManager conn, String tablename, String ipstr) {
		// PreparedStatement stmt = null;
		String sql = "";
		if(SystemConstant.getDBType().equals("mysql")){
			if (tablename.indexOf("baseinfo") >= 0) {

				sql = "create table "
						+ tablename
						+ ipstr
						+ "(ID bigint(20) not null auto_increment,policyName varchar(100) DEFAULT NULL,name varchar(100) DEFAULT NULL,value varchar(100) DEFAULT NULL,"
						+ "priority varchar(50) DEFAULT NULL,type varchar(20) DEFAULT NULL,collecttime timestamp NULL DEFAULT NULL,"
						+ " PRIMARY KEY  (ID)) ENGINE=InnoDB DEFAULT CHARSET=gb2312";
			} else if (tablename.indexOf("interfacepolicy") >= 0) {
				// 创建
				sql = "create table "
						+ tablename
						+ ipstr
						+ "(ID bigint(20) not null auto_increment,  interfaceName  varchar(100) DEFAULT NULL, policyName  varchar(100) DEFAULT NULL,"
						+ "className  varchar(100) DEFAULT NULL, offeredRate  bigint(11) DEFAULT NULL, dropRate  bigint(11) DEFAULT NULL, matchGroup  varchar(50) DEFAULT NULL,"
						+ " matchedPkts bigint(11) DEFAULT NULL, matchedBytes  bigint(11) DEFAULT NULL, dropsTotal  bigint(11) DEFAULT NULL, dropsBytes  bigint(11) DEFAULT NULL,"
						+ " depth  bigint(11) DEFAULT NULL, totalQueued  bigint(11) DEFAULT NULL, noBufferDrop  bigint(11) DEFAULT NULL, collecttime  timestamp NULL DEFAULT NULL,"
						+ " PRIMARY KEY  (ID)) ENGINE=InnoDB DEFAULT CHARSET=gb2312";
			} else if (tablename.indexOf("queueinfo") >= 0) {
				sql = "CREATE TABLE "
						+ tablename
						+ ipstr
						+ "(ID bigint(20) not null auto_increment,entity varchar(100) DEFAULT NULL,inputSize bigint(11) DEFAULT NULL,"
						+ "inputMax bigint(11) DEFAULT NULL,inputDrops bigint(11) DEFAULT NULL,inputFlushes bigint(11) DEFAULT NULL,outputSize bigint(11) DEFAULT NULL,"
						+ "outputMax  bigint(11) DEFAULT NULL,outputDrops  bigint(11) DEFAULT NULL,outputThreshold  bigint(11) DEFAULT NULL,"
						+ "availBandwidth bigint(11) DEFAULT NULL,collecttime timestamp NULL DEFAULT NULL,PRIMARY KEY  (ID)) ENGINE=InnoDB DEFAULT CHARSET=gb2312";
			}
		}else if(SystemConstant.getDBType().equals("oracle")){
			if (tablename.indexOf("baseinfo") >= 0) {

				sql = "create table "
						+ tablename
						+ ipstr
						+ "(ID number(20) not null,policyName varchar2(100) DEFAULT NULL,name varchar2(100) DEFAULT NULL,value varchar2(100) DEFAULT NULL,"
						+ "priority varchar2(50) DEFAULT NULL,type varchar2(20) DEFAULT NULL,collecttime date default sysdate-1 NULL DEFAULT NULL,"
						+ " PRIMARY KEY  (ID))";
			} else if (tablename.indexOf("interfacepolicy") >= 0) {
				// 创建
				sql = "create table "
						+ tablename
						+ ipstr
						+ "(ID number(20) not null,  interfaceName  varchar2(100) DEFAULT NULL, policyName  varchar2(100) DEFAULT NULL,"
						+ "className  varchar2(100) DEFAULT NULL, offeredRate  number(11) DEFAULT NULL, dropRate  number(11) DEFAULT NULL, matchGroup  varchar2(50) DEFAULT NULL,"
						+ " matchedPkts number(11) DEFAULT NULL, matchedBytes  number(11) DEFAULT NULL, dropsTotal  number(11) DEFAULT NULL, dropsBytes  number(11) DEFAULT NULL,"
						+ " depth  number(11) DEFAULT NULL, totalQueued  number(11) DEFAULT NULL, noBufferDrop  number(11) DEFAULT NULL, collecttime  date default sysdate-1 NULL DEFAULT NULL,"
						+ " PRIMARY KEY  (ID))";
			} else if (tablename.indexOf("queueinfo") >= 0) {
				sql = "CREATE TABLE "
						+ tablename
						+ ipstr
						+ "(ID number(20) not null,entity varchar2(100) DEFAULT NULL,inputSize number(11) DEFAULT NULL,"
						+ "inputMax number(11) DEFAULT NULL,inputDrops number(11) DEFAULT NULL,inputFlushes number(11) DEFAULT NULL,outputSize number(11) DEFAULT NULL,"
						+ "outputMax  number(11) DEFAULT NULL,outputDrops  number(11) DEFAULT NULL,outputThreshold  number(11) DEFAULT NULL,"
						+ "availBandwidth number(11) DEFAULT NULL,collecttime date default sysdate-1 NULL DEFAULT NULL,PRIMARY KEY  (ID))";
			}
		}

		SysLogger.info(sql);
		try {
			conn.executeUpdate(sql);
			if(SystemConstant.DBType.equals("oracle")){
				CreateTableManager.createSeqOrcl(conn, tablename, ipstr);
				CreateTableManager.createTrigerOrcl(conn, tablename, ipstr, tablename);
			}
		} catch (Exception e) {
			try {
				// conn.rollback();
			} catch (Exception ex) {

			}

		} finally {
		}
	}

	/**
	 * 删除多个设备的临时表中的数据
	 * 
	 * @param tableName
	 *            表名称
	 * @param nodeid
	 * @return
	 */
	public Boolean clearNmsTempDatas(String[] tableNames, String[] ids) {
		DBManager dbmanager = new DBManager();
		Boolean returnFlag = false;
		if (ids != null && ids.length > 0) {
			try {
				// 进行修改
				for (int i = 0; i < ids.length; i++) {
					String id = ids[i];
					PollingEngine.getInstance().deleteNodeByID(
							Integer.parseInt(id));
					for (String tableName : tableNames) {
						String sql = "delete from " + tableName
								+ " where nodeid='" + id + "'";
						
						dbmanager.addBatch(sql);
					}
				}
				dbmanager.executeBatch();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dbmanager.close();
			}
			returnFlag = true;
		}
		return returnFlag;
	}

	/**
	 * 清空多张指定表的数据
	 * 
	 * @param tableNames
	 *            表名称
	 * @param uniqueKey
	 *            唯一键 如：nodeid
	 * @param nodeids
	 *            唯一键对应的值 如：结点ID数组
	 * @return
	 */
	public Boolean clearTablesData(String[] tableNames, String uniqueKey,
			String[] uniqueKeyValues) {
		DBManager dbmanager = new DBManager();
		try {
			for (String uniqueValue : uniqueKeyValues) {
				for (String tableName : tableNames) {
					String sql = "delete from " + tableName + " where "
							+ uniqueKey + " = '" + uniqueValue + "'";
					dbmanager.addBatch(sql);
				}
			}
			// //System.out.println(sql);
			dbmanager.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}
	
	/**
	 * 删除Oracle的主键自增长的方法
	 * @param con
	 * @param tablestr
	 * @param ipstr
	 */
	 public static void dropSeqOrcl(DBManager conn,String tablestr,String ipstr){
		 	String createSeqStr="";
		 	createSeqStr="drop sequence "+tablestr+"_"+ipstr+"_SEQ";
		 	try{
		 		
//		 		System.out.println("CreateTableManager------984---------->"+tablestr);
		 		conn.executeUpdate(createSeqStr);
		 	}catch(Exception e){
		 		e.printStackTrace();
		 		try{		 			
		 			//con.rollback();
		 			//DataGate.freeCon(con);		 			
		 		}catch(Exception ex){
		 			
		 		}
		 		
		 	}finally{
		 	}		 	
		 	
		 }
	 /**
		 * 新加的方法，Oracle实现主键自增长的方法
		 * @param con
		 * @param tablestr
		 * @param ipstr
		 */
		public static void createSeqOrcl(DBManager conn,String tablestr,String ipstr){
		 	String createSeqStr="";
		 	createSeqStr="create sequence "+tablestr+"_"+ipstr+"_SEQ minvalue 1 maxvalue 999999999999999999999999999 start with 1 increment by 1 cache 10";
		 	try{
		 		conn.executeUpdate(createSeqStr);
		 	}catch(Exception e){
		 		e.printStackTrace();
		 		try{		 			
		 			//con.rollback();
		 			//DataGate.freeCon(con);		 			
		 		}catch(Exception ex){
		 			
		 		}
		 		
		 	}finally{
		 		
		 	}
		 }
		/**
		 * 创建Oracle的触发器
		 * @param con
		 * @param tablestr
		 * @param ipstr
		 * @param tablename
		 */

		public static void createTrigerOrcl(DBManager conn,String tablestr,String ipstr,String tablename){
		 	PreparedStatement stmt = null;
		 	String trigerstr="";
		 	trigerstr="create or replace trigger "+tablestr+ipstr+"id before insert on "+tablestr+ipstr+" for each row when (new.id is null) begin "
					+" select "+tablestr+"_"+ipstr+"_SEQ.nextval into :new.id from dual; end ;";
		 	try{
		 		conn.executeUpdate(trigerstr);
		 	}catch(Exception e){
		 		e.printStackTrace();
		 		try{		 			
		 			//con.rollback();
		 			//DataGate.freeCon(con);		 			
		 		}catch(Exception ex){
		 			
		 		}		 		
		 	}finally{
		 		try{
		 			if (stmt != null)stmt.close();
		 		}catch(Exception e){
		 			
		 		}
		 	}		 	
      }
	

	public static void main(String[] args) {
		// PingUtil pingU=new PingUtil("10.40.30.133");
		// Integer[] packet=pingU.ping();
		// pingU.addhis(packet);
	}
	
	public void createVMhostTable(DBManager conn, String tablename, String ipstr) {
		// PreparedStatement stmt = null;
		String sql = "";
		if(SystemConstant.getDBType().equals("mysql")){

				sql = "create table "
						+ tablename
						+ ipstr
						+ " (id bigint(20) NOT NULL auto_increment," +
								"hostid varchar(100) default NULL," +
								"cpu varchar(100) default NULL," +
								"cpuuse varchar(50) default NULL," +
								"collecttime timestamp NULL default NULL," +
								"meminc varchar(50) default NULL," +
								"memin varchar(50) default NULL," +
								"memout varchar(50) default NULL," +
								"mem varchar(50) default NULL,disk varchar(50) default NULL," +
								"PRIMARY KEY  (id)) " +
								"ENGINE=InnoDB DEFAULT CHARSET=gb2312;";
			
		}else if(SystemConstant.getDBType().equals("oracle")){

				sql = "create table "
						+ tablename
						+ ipstr
				+ " (id number(20) NOT NULL ," +
				"hostid varchar2(100) default NULL," +
				"cpu varchar2(100) default NULL," +
				"cpuuse varchar2(50) default NULL," +
				"collecttime date default sysdate-1 NULL DEFAULT NULL," +
				"meminc varchar2(50) default NULL," +
				"memin varchar2(50) default NULL," +
				"memout varchar2(50) default NULL," +
				"mem varchar2(50) default NULL," +
				"disk varchar2(50) default NULL," +
				"PRIMARY KEY  (id)) " ;
			
		}
		SysLogger.info(sql);
		try {
			conn.executeUpdate(sql);
			if(SystemConstant.DBType.equals("oracle")){
				CreateTableManager.createSeqOrcl(conn, tablename, ipstr);
				CreateTableManager.createTrigerOrcl(conn, tablename, ipstr, tablename);
			}
		} catch (Exception e) {
			try {
				// conn.rollback();
			} catch (Exception ex) {

			}

		} finally {
		}
	}
	
	public void createVMguesthostTable(DBManager conn, String tablename, String ipstr) {
		// PreparedStatement stmt = null;
		String sql = "";
		if(SystemConstant.getDBType().equals("mysql")){

				sql = "create table "
					    + tablename
					    + ipstr
					   + " (id bigint(20) NOT NULL auto_increment," +
					    "vid varchar(100) default NULL,"+
						"hostid varchar(100) default NULL," +
						"cpu varchar(100) default NULL," +
						"cpuuse varchar(50) default NULL," +
						"collecttime timestamp NULL default NULL," +
						"meminc varchar(50) default NULL," +
						"memin varchar(50) default NULL," +
						"memout varchar(50) default NULL," +
						"mem varchar(50) default NULL,disk varchar(50) default NULL,net varchar(50) default NULL," +
						"PRIMARY KEY  (id)) " +
						"ENGINE=InnoDB DEFAULT CHARSET=gb2312;";
			
		}else if(SystemConstant.getDBType().equals("oracle")){

				sql = "create table "
						+ tablename
						+ ipstr
						+ " (id number(20) NOT NULL ," +
						"vid varchar(100) default NULL,"+
						"hostid varchar2(100) default NULL," +
						"cpu varchar2(100) default NULL," +
						"cpuuse varchar2(50) default NULL," +
						"collecttime date default sysdate-1 NULL DEFAULT NULL," +
						"meminc varchar2(50) default NULL," +
						"memin varchar2(50) default NULL," +
						"memout varchar2(50) default NULL," +
						"mem varchar(50) default NULL,disk varchar(50) default NULL,net varchar(50) default NULL," +
						"PRIMARY KEY  (id)) " ;
		}
		SysLogger.info(sql);
		try {
			conn.executeUpdate(sql);
			if(SystemConstant.DBType.equals("oracle")){
				CreateTableManager.createSeqOrcl(conn, tablename, ipstr);
				CreateTableManager.createTrigerOrcl(conn, tablename, ipstr, tablename);
			}
		} catch (Exception e) {
			try {
				// conn.rollback();
			} catch (Exception ex) {

			}

		} finally {
		}
	}
	
	
	public void createVMDSTable(DBManager conn, String tablename, String ipstr) {
		// PreparedStatement stmt = null;
		String sql = "";
		if(SystemConstant.getDBType().equals("mysql")){
				sql = "create table "
					    + tablename
					    + ipstr
					    + " (id bigint(20) NOT NULL auto_increment," +
						"vid varchar(100) default NULL," +
						"used varchar(100) default NULL," +
						"assigned varchar(50) default NULL," +
						"collecttime timestamp NULL default NULL," +
						"capacity varchar(50) default NULL," +
						"useuse varchar(50) default NULL," +
						"PRIMARY KEY  (id)) " +
						"ENGINE=InnoDB DEFAULT CHARSET=gb2312;";
			
		}else if(SystemConstant.getDBType().equals("oracle")){

				sql = "create table "
						+ tablename
						+ ipstr
						 + " (id number(20) NOT NULL ," +
							"vid varchar2(100) default NULL," +
							"used varchar2(100) default NULL," +
							"assigned varchar2(50) default NULL," +
							"collecttime date default sysdate-1 NULL DEFAULT NULL," +
							"capacity varchar2(50) default NULL," +
							"use varchar2(50) default NULL," +
							"PRIMARY KEY  (id)) " ;
		}
		SysLogger.info(sql);
		try {
			conn.executeUpdate(sql);
			if(SystemConstant.DBType.equals("oracle")){
				CreateTableManager.createSeqOrcl(conn, tablename, ipstr);
				CreateTableManager.createTrigerOrcl(conn, tablename, ipstr, tablename);
			}
		} catch (Exception e) {
			try {
				// conn.rollback();
			} catch (Exception ex) {

			}

		} finally {
		}
	}
	
	public void createVMCRTable(DBManager conn, String tablename, String ipstr) {
		// PreparedStatement stmt = null;
		String sql = "";
		if(SystemConstant.getDBType().equals("mysql")){

				sql = "create table "
					    + tablename
					    + ipstr
					    + " (id bigint(20) NOT NULL auto_increment," +
						"vid varchar(100) default NULL," +
						"cpu varchar(100) default NULL," +
						"cputotal varchar(50) default NULL," +
						"collecttime timestamp NULL default NULL," +
						"mem varchar(50) default NULL," +
						"memtotal varchar(50) default NULL," +
						"PRIMARY KEY  (id)) " +
						"ENGINE=InnoDB DEFAULT CHARSET=gb2312;";
			
		}else if(SystemConstant.getDBType().equals("oracle")){

				sql = "create table "
						+ tablename
						+ ipstr
						 + " (id number(20) NOT NULL," +
							"vid varchar2(100) default NULL," +
							"cpu varchar2(100) default NULL," +
							"cputotal varchar2(50) default NULL," +
							"collecttime date default sysdate-1 NULL DEFAULT NULL," +
							"mem varchar2(50) default NULL," +
							"memtotal varchar2(50) default NULL," +
							"PRIMARY KEY  (id)) " ;
		}
		SysLogger.info(sql);
		try {
			conn.executeUpdate(sql);
			if(SystemConstant.DBType.equals("oracle")){
				CreateTableManager.createSeqOrcl(conn, tablename, ipstr);
				CreateTableManager.createTrigerOrcl(conn, tablename, ipstr, tablename);
			}
		} catch (Exception e) {
			try {
				// conn.rollback();
			} catch (Exception ex) {

			}

		} finally {
		}
	}
	
	
	public void createVMRPTable(DBManager conn, String tablename, String ipstr) {
		// PreparedStatement stmt = null;
		String sql = "";
		if(SystemConstant.getDBType().equals("mysql")){

				sql = "create table "
					    + tablename
					    + ipstr
					    + " (id bigint(20) NOT NULL auto_increment," +
						"vid varchar(100) default NULL," +
						"cpu varchar(100) default NULL," +
						"collecttime timestamp NULL default NULL," +
						"PRIMARY KEY  (id)) " +
						"ENGINE=InnoDB DEFAULT CHARSET=gb2312;";
			
		}else if(SystemConstant.getDBType().equals("oracle")){

				sql = "create table "
						+ tablename
						+ ipstr
						 + " (id number(20) NOT NULL," +
							"vid varchar2(100) default NULL," +
							"cpu varchar2(100) default NULL," +
							"collecttime date default sysdate-1 NULL DEFAULT NULL," +
							"PRIMARY KEY  (id)) " ;
		}
		SysLogger.info(sql);
		try {
			conn.executeUpdate(sql);
			if(SystemConstant.DBType.equals("oracle")){
				CreateTableManager.createSeqOrcl(conn, tablename, ipstr);
				CreateTableManager.createTrigerOrcl(conn, tablename, ipstr, tablename);
			}
		} catch (Exception e) {
			try {
				// conn.rollback();
			} catch (Exception ex) {

			}

		} finally {
		}
	}
	
	
	public void createVMBaseTable(DBManager conn, String tablename, String ipstr) {
		// PreparedStatement stmt = null;
		String sql = "";
	 if(tablename.equalsIgnoreCase("vm_basephysical")){
		if(SystemConstant.getDBType().equals("mysql")){

				sql = "create table "
					    + tablename
					    + ipstr
					    + " (id bigint(11) not null auto_increment ," +
					    		"name varchar(100) default null," +
					    		"model varchar(100) default null," +
					    		"cpunum varchar(100) default null," +
					    		"netnum  varchar(100) default null," +
					    		"memory varchar(100) default null," +
					    		"ghz varchar(100) default null," +
					    		"vid varchar(100) default null," +
					    		"hostpower varchar(100) default null,"+
						"PRIMARY KEY  (id)) " +
						"ENGINE=InnoDB DEFAULT CHARSET=gb2312;";
			
		}else if(SystemConstant.getDBType().equals("oracle")){

				sql = "create table "
						+ tablename
						+ ipstr
						 + " (id number(11) not null auto_increment ," +
				    		"name varchar2(100) default null," +
				    		"model varchar2(100) default null," +
				    		"cpunum varchar2(100) default null," +
				    		"netnum  varchar2(100) default null," +
				    		"memory varchar2(100) default null," +
				    		"ghz varchar2(100) default null," +
				    		"vid varchar2(100) default null," +
				    		"hostpower varchar2(100) default null,"+
					"PRIMARY KEY  (id)) " ;
		}
	  }else if(tablename.equalsIgnoreCase("vm_basevmware")){
		  if(SystemConstant.getDBType().equals("mysql")){

				sql = "create table "
					    + tablename
					    + ipstr
					    + " (id bigint(11) not null auto_increment ," +
					    		"name varchar(100) default null," +
					    		"fullname varchar(100) default null," +
					    		"cpu varchar(100) default null," +
					    		"memoryuse  varchar(100) default null," +
					    		"vmpower varchar(100) default null," +
					    		"hoid varchar(100) default null," +
					    		"vid varchar(100) default null," +
						"PRIMARY KEY  (id)) " +
						"ENGINE=InnoDB DEFAULT CHARSET=gb2312;";
			
		}else if(SystemConstant.getDBType().equals("oracle")){

				sql = "create table "
						+ tablename
						+ ipstr
						 + " (id number(11) not null auto_increment ," +
						    "name varchar(100) default null," +
				    		"fullname varchar(100) default null," +
				    		"cpu varchar(100) default null," +
				    		"memoryuse  varchar(100) default null," +
				    		"vmpower varchar(100) default null," +
				    		"hoid varchar(100) default null," +
				    		"vid varchar(100) default null," +
					"PRIMARY KEY  (id)) " ;
		}
	  }else if(tablename.equalsIgnoreCase("vm_baseresource")){
		  if(SystemConstant.getDBType().equals("mysql")){

				sql = "create table "
					    + tablename
					    + ipstr
					    + " (id bigint(11) not null auto_increment ," +
					    		"name varchar(100) default null," +
					    		"dcid varchar(100) default null," +
					    		"crid varchar(100) default null," +
					    		"vid varchar(100) default null," +
						"PRIMARY KEY  (id)) " +
						"ENGINE=InnoDB DEFAULT CHARSET=gb2312;";
			
		}else if(SystemConstant.getDBType().equals("oracle")){

				sql = "create table "
						+ tablename
						+ ipstr
						 + " (id number(11) not null auto_increment ," +
						    "name varchar(100) default null," +
				    		"dcid varchar(100) default null," +
				    		"crid varchar(100) default null," +
				    		"vid varchar(100) default null," +
					"PRIMARY KEY  (id)) " ;
		}
	  }else if(tablename.equalsIgnoreCase("vm_baseyun")){
		  if(SystemConstant.getDBType().equals("mysql")){

				sql = "create table "
					    + tablename
					    + ipstr
					    + " (id bigint(11) not null auto_increment ," +
					    		"name varchar(100) default null," +
					    		"disk varchar(100) default null," +
					    		"cpuuse varchar(100) default null," +
					    		"hostnum varchar(100) default null," +
					    		"mem varchar(100) default null," +
					    		"cpunum varchar(100) default null," +
					    		"vid varchar(100) default null," +
						"PRIMARY KEY  (id)) " +
						"ENGINE=InnoDB DEFAULT CHARSET=gb2312;";
			
		}else if(SystemConstant.getDBType().equals("oracle")){

				sql = "create table "
						+ tablename
						+ ipstr
						 + " (id number(11) not null auto_increment ," +
						    "name varchar(100) default null," +
				    		"disk varchar(100) default null," +
				    		"cpuuse varchar(100) default null," +
				    		"hostnum varchar(100) default null," +
				    		"mem varchar(100) default null," +
				    		"cpunum varchar(100) default null," +
				    		"vid varchar(100) default null," +
					"PRIMARY KEY  (id)) " ;
		}
	  }else if(tablename.equalsIgnoreCase("vm_basedatastore")){
		  if(SystemConstant.getDBType().equals("mysql")){

				sql = "create table "
					    + tablename
					    + ipstr
					    + " (id bigint(11) not null auto_increment ," +
					    		"name varchar(100) default null," +
					    		"store varchar(100) default null," +
					    		"unusedstore varchar(100) default null," +
					    		"vid varchar(100) default null," +
						"PRIMARY KEY  (id)) " +
						"ENGINE=InnoDB DEFAULT CHARSET=gb2312;";
			
		}else if(SystemConstant.getDBType().equals("oracle")){

				sql = "create table "
						+ tablename
						+ ipstr
						 + " (id number(11) not null auto_increment ," +
						    "name varchar(100) default null," +
				    		"store varchar(100) default null," +
				    		"unusedstore varchar(100) default null," +
				    		"vid varchar(100) default null," +
					"PRIMARY KEY  (id)) " ;
		}
	  }else if(tablename.equalsIgnoreCase("vm_basedatacenter")){
		  if(SystemConstant.getDBType().equals("mysql")){

				sql = "create table "
					    + tablename
					    + ipstr
					    + " (id bigint(11) not null auto_increment ," +
					    		"name varchar(100) default null," +
					    		"dcid varchar(100) default null," +
					    		"vid varchar(100) default null," +
						"PRIMARY KEY  (id)) " +
						"ENGINE=InnoDB DEFAULT CHARSET=gb2312;";
			
		}else if(SystemConstant.getDBType().equals("oracle")){

				sql = "create table "
						+ tablename
						+ ipstr
						 + " (id number(11) not null auto_increment ," +
						    "name varchar(100) default null," +
				    		"dcid varchar(100) default null," +
				    		"vid varchar(100) default null," +
					"PRIMARY KEY  (id)) " ;
		}
	  }
	 
	 
	    SysLogger.info(sql);
		try {
			conn.executeUpdate(sql);
			if(SystemConstant.DBType.equals("oracle")){
				CreateTableManager.createSeqOrcl(conn, tablename, ipstr);
				CreateTableManager.createTrigerOrcl(conn, tablename, ipstr, tablename);
			}
		} catch (Exception e) {
			try {
				// conn.rollback();
			} catch (Exception ex) {

			}

		} finally {
		}
	}

	
	
	public void createEmcTable(DBManager conn, String tablename, String ipstr) {
		// PreparedStatement stmt = null;
		String sql = "";
		if(ipstr.contains(".")){
			ipstr = ipstr.replace(".", "_");
		}
	 if(tablename.equalsIgnoreCase("emcDiskPer")){
		if(SystemConstant.getDBType().equals("mysql")){

				sql = "create table "
					    + tablename
					    + ipstr
					    + " (id bigint(11) not null auto_increment," +
					    		"serialnumber varchar(100) default null," +
					     "numberofreads varchar(100) default null," +
					     "numberofwrites varchar(100) default null," +
					     "softreaderrors varchar(100) default null," +
					     "softwriteerrors varchar(100) default null," +
					     "kbytesread varchar(100) default null," +
					     "kbyteswritten varchar(100) default null," +
					     "idleticks varchar(100) default null," +
					     "busyticks varchar(100) default null," +
					     "hardreaderrors varchar(100) default null," +
					     "hardwritererrors varchar(100) default null," +
					     "collecttime timestamp NULL default NULL," +
					     "primary key(id)) " +
						"ENGINE=InnoDB DEFAULT CHARSET=gb2312;";
			
		}else if(SystemConstant.getDBType().equals("oracle")){
			
				sql = "create table "
						+ tablename
						+ ipstr
						  + " (id bigint(11) not null auto_increment," +
				    		"serialnumber varchar(100) default null," +
				     "numberofreads varchar(100) default null," +
				     "numberofwrites varchar(100) default null," +
				     "softreaderrors varchar(100) default null," +
				     "softwriteerrors varchar(100) default null," +
				     "kbytesread varchar(100) default null," +
				     "kbyteswritten varchar(100) default null," +
				     "idleticks varchar(100) default null," +
				     "busyticks varchar(100) default null," +
				     "hardreaderrors varchar(100) default null," +
				     "hardwritererrors varchar(100) default null," +
				     "collecttime date default sysdate-1 NULL DEFAULT NULL," +
					"PRIMARY KEY  (id)) " ;
		}
	  }else if(tablename.equalsIgnoreCase("emcLunPer")){
		  if(SystemConstant.getDBType().equals("mysql")){

				sql = "create table "
					    + tablename
					    + ipstr
					    + " (id bigint(11) not null auto_increment ," +
					    		"name varchar(100) default null," +
					    		"totalharderrors varchar(100) default null," +
					    		"totalsofterrors varchar(100) default null," +
					    		"totalqueuelength  varchar(100) default null," +
					    		"collecttime timestamp NULL default NULL," +
						"PRIMARY KEY  (id)) " +
						"ENGINE=InnoDB DEFAULT CHARSET=gb2312;";
			
		}else if(SystemConstant.getDBType().equals("oracle")){

				sql = "create table "
						+ tablename
						+ ipstr
						 + " (id number(11) not null auto_increment ," +
						 "name varchar(100) default null," +
				    		"totalharderrors varchar(100) default null," +
				    		"totalsofterrors varchar(100) default null," +
				    		"totalqueuelength  varchar(100) default null," +
				    		 "collecttime date default sysdate-1 NULL DEFAULT NULL," +
					"PRIMARY KEY  (id)) " ;
		}
	  }else if(tablename.equalsIgnoreCase("emcenvpower")){
		  if(SystemConstant.getDBType().equals("mysql")){

				sql = "create table "
					    + tablename
					    + ipstr
					    + " (id bigint(11) not null auto_increment ," +
					            "name varchar(100) default null," +
					    		"state varchar(100) default null," +
					    		"presentwatts varchar(100) default null," +
					    		"averagewatts varchar(100) default null," +
					    		"collecttime timestamp NULL default NULL," +
						"PRIMARY KEY  (id)) " +
						"ENGINE=InnoDB DEFAULT CHARSET=gb2312;";
			
		}else if(SystemConstant.getDBType().equals("oracle")){

				sql = "create table "
						+ tablename
						+ ipstr
						 + " (id number(11) not null auto_increment ," +
						 "name varchar2(100) default null," +
						 "state varchar2(100) default null," +
				    		"presentwatts varchar2(100) default null," +
				    		"averagewatts varchar2(100) default null," +
				    		 "collecttime date default sysdate-1 NULL DEFAULT NULL," +
					"PRIMARY KEY  (id)) " ;
		}
	  }else if(tablename.equalsIgnoreCase("emcenvstore")){
		  if(SystemConstant.getDBType().equals("mysql")){

				sql = "create table "
					    + tablename
					    + ipstr
					    + " (id bigint(11) not null auto_increment ," +
					    		"name varchar(100) default null," +
					    		"AirStatus varchar(100) default null," +
					    		"PresentDegree varchar(100) default null," +
					    		"AverageDegree varchar(100) default null," +
					    		"PowerStatus varchar(100) default null," +
					    		"PresentWatts varchar(100) default null," +
					    		"AverageWatts varchar(100) default null," +
					    		"collecttime timestamp NULL default NULL," +
						"PRIMARY KEY  (id)) " +
						"ENGINE=InnoDB DEFAULT CHARSET=gb2312;";
			
		}else if(SystemConstant.getDBType().equals("oracle")){

				sql = "create table "
						+ tablename
						+ ipstr
						 + " (id number(11) not null auto_increment ," +
						 "name varchar(100) default null," +
				    		"AirStatus varchar(100) default null," +
				    		"PresentDegree varchar(100) default null," +
				    		"AverageDegree varchar(100) default null," +
				    		"PowerStatus varchar(100) default null," +
				    		"PresentWatts varchar(100) default null," +
				    		"AverageWatts varchar(100) default null," +
				    		 "collecttime date default sysdate-1 NULL DEFAULT NULL," +
					"PRIMARY KEY  (id)) " ;
		}
		}else if(tablename.equalsIgnoreCase("emcbakpower")){
			  if(SystemConstant.getDBType().equals("mysql")){

					sql = "create table "
						    + tablename
						    + ipstr
						    + " (id bigint(11) not null auto_increment ," +
						    		"name varchar(100) default null," +
						    		"PowerStatus varchar(100) default null," +
						    		"PresentWatts varchar(100) default null," +
						    		"AverageWatts varchar(100) default null," +
						    		"collecttime timestamp NULL default NULL," +
							"PRIMARY KEY  (id)) " +
							"ENGINE=InnoDB DEFAULT CHARSET=gb2312;";
				
			}else if(SystemConstant.getDBType().equals("oracle")){

					sql = "create table "
							+ tablename
							+ ipstr
							 + " (id number(11) not null auto_increment ," +
							    "name varchar(100) default null," +
					    		"PowerStatus varchar(100) default null," +
					    		"PresentWatts varchar(100) default null," +
					    		"AverageWatts varchar(100) default null," +
					    		 "collecttime date default sysdate-1 NULL DEFAULT NULL," +
						"PRIMARY KEY  (id)) " ;
			}
	 
		}
	    SysLogger.info(sql);
		try {
			conn.executeUpdate(sql);
			if(SystemConstant.DBType.equals("oracle")){
				CreateTableManager.createSeqOrcl(conn, tablename, ipstr);
				CreateTableManager.createTrigerOrcl(conn, tablename, ipstr, tablename);
			}
		} catch (Exception e) {
			try {
				// conn.rollback();
			} catch (Exception ex) {

			}

		} finally {
		}
	}
	
	
	
	
	
	
}
