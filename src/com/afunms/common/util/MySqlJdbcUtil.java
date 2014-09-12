package com.afunms.common.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySqlJdbcUtil {
	
	String strconn;
	String strDriver = "org.gjt.mm.mysql.Driver";
	//jdbc:oracle:thin:@MyDbComputerNameOrIP:1521:ORCL
	String name;
	String pass;
	Connection conn = null;

	public Statement stmt = null;

	ResultSet rs = null;
	
	public MySqlJdbcUtil(String url,String name,String pass){
		this.strconn = url;
		this.name = name;
		this.pass = pass;
	}
	public java.sql.Connection jdbc() {
		//file: //connection
		try {
			Class.forName(strDriver).newInstance();
			conn = DriverManager.getConnection(strconn, name, pass);
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
		} catch (Exception e) {
			e.printStackTrace();
			//System.out.println("error");
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
			if(stmt != null)
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
