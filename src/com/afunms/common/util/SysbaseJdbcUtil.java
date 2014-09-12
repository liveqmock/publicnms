/**
 * <p>Description:network utilities</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-11
 */

package com.afunms.common.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SysbaseJdbcUtil {
	String strconn;// = "jdbc:jtds:sqlserver://localhost;DatabaseName=CenterDB;charset=GBK;SelectMethod=CURSOR";

	String strDriver = "com.sybase.jdbc2.jdbc.SybDriver";
	//jdbc:oracle:thin:@MyDbComputerNameOrIP:1521:ORCL
	String name;
	String pass;
	Connection conn = null;

	public Statement stmt = null;

	ResultSet rs = null;
	
	public SysbaseJdbcUtil(String url,String name,String pass){
		this.strconn = url;
		this.name = name;
		this.pass = pass;
	}
	public java.sql.Connection jdbc() {
		file: //connection
		try {
			Class.forName(strDriver).newInstance();
			//SysLogger.info(strconn+"==="+name+"==="+pass);
			conn = DriverManager.getConnection(strconn, name, pass);
			stmt = conn.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

	public ResultSet query(String sql) {
		try {
			//conn = DriverManager.getConnection(strconn, name,pass); 
			Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery(sql);
		} 
		catch(Exception ex) { 
			ex.printStackTrace();
			//System.err.println(ex.getMessage());
		}
		return rs;
	}
	
	
	public ResultSet executeQuery(String sql) {
		file: //select
		try {
			//conn = DriverManager.getConnection(strconn, name,pass);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);

		} catch (Exception ex) {
			ex.printStackTrace();
			//System.err.println("Ö´ÐÐSQLÓï¾ä³ö´íselect£º" + ex.getMessage());
		}
		return rs;
	}

	public ResultSet executeUpdate(String sql) {
		file: //insert ,update
		try {
			conn = DriverManager.getConnection(strconn,name,pass);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			
		} catch (SQLException ex) {
			System.err.println("Ö´ÐÐSQLÓï¾ä³ö´íinsert,update:" + ex.getMessage());
		}
		return rs;
	}

	public void closeStmt() {
		file: //close statement
		try {
			if(stmt!=null)
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void closeConn() {
		file: //close connection
		try {
			if(conn!=null)
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}