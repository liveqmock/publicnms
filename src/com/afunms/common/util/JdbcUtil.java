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

public class JdbcUtil {
	String strconn;// = "jdbc:jtds:sqlserver://localhost;DatabaseName=CenterDB;charset=GBK;SelectMethod=CURSOR";

	String strDriver = "net.sourceforge.jtds.jdbc.Driver";
	
	String name;
	String pass;
	Connection conn = null;

	public Statement stmt = null;

	ResultSet rs = null;
	
	public JdbcUtil(String url,String name,String pass){
		if (pass == null)pass="";
		this.strconn = url;
		this.name = name;
		this.pass = pass;
	}
	public java.sql.Connection jdbc() {
		file: //connection
		try {
			Class.forName(strDriver).newInstance();
			conn = DriverManager.getConnection(strconn, name, pass);
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

	public ResultSet executeQuery(String sql) {
		file: //select
		try {
			conn = DriverManager.getConnection(strconn, name,pass);
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery(sql);

		} catch (SQLException ex) {
			System.err.println("Ö´ÐÐSQLÓï¾ä³ö´íselect£º" + ex.getMessage());
		}
		return rs;
	}

	public ResultSet executeUpdate(String sql) {
		file: //insert ,update
		try {
			conn = DriverManager.getConnection(strconn,name,pass);
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery(sql);
			
		} catch (SQLException ex) {
			System.err.println("Ö´ÐÐSQLÓï¾ä³ö´íinsert,update:" + ex.getMessage());
		}
		return rs;
	}

	public void closeStmt() {
		file: //close statement
		try {
			if (stmt !=null)
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void closeConn() {
		file: //close connection
		try {
			if(conn != null)
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
