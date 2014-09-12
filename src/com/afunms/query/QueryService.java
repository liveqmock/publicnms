package com.afunms.query;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
    
//import com.sun.java_cup.internal.internal_error;
    
public class QueryService {
	// private static String driver = "com.mysql.jdbc.Driver";
	// "jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoing=utf-8";
	// private static String username = " root/";
	// private static String pwd = "root";

	private static String driver = "";
	private static String url = "";
	private static String username = "";
	private static String pwd = "";

	private Vector<Vector<String>> rows;
	private Vector<String> head;
	private String message = "";
	private int count =0;

	// 根据sql语句获得数据库中的数据
	public void getAllDataFromDB(String sql) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		boolean hasResultSet = false;
		conn = this.getConnection();

		try {
			stmt = conn.createStatement();
			sql = new String(sql.getBytes("ISO-8859-1"), "gb2312");
			// System.out.println(sql);
			// 判断sql语句是执行操作方式，查询返回true,删除、修改返回false
			hasResultSet = stmt.execute(sql);
			if (hasResultSet) {
				head = new Vector<String>();
				rows = new Vector<Vector<String>>();
				rs = stmt.getResultSet();
				// ResultSetMetaData是用于分析结果集的元数据接口
				ResultSetMetaData rsmd = rs.getMetaData();
				int columnCount = rsmd.getColumnCount();
				String columnName = "";
				for (int i = 0; i < columnCount; i++) {
					columnName = rsmd.getColumnName(i + 1);
					head.add(columnName);
				}
				// 迭代输出ResultSet对象
				while (rs.next()) {
					Vector<String> row = new Vector<String>();
					// 依次输出每列的值
					for (int i = 0; i < columnCount; i++) {
						row.addElement(rs.getString(i + 1));
					}
					rows.add(row);
				}
			} else {
				count=stmt.getUpdateCount();
				System.out
						.println("该SQL语句影响的记录有" + stmt.getUpdateCount() + "条");
				message = "该SQL语句影响的记录有" + stmt.getUpdateCount() + "条";
			}
		} catch (SQLException e) {
			message = e.toString();
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
				if(rs != null){
					rs.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public Connection getConnection() {
		Connection conn = null;
		try {
			if(driver.equals("oracle.jdbc.driver.OracleDriver")){
				if (username.equals("sys") || username.equals("system")) {
					java.util.Properties info = new java.util.Properties();
					info.put("user", username);
					info.put("password", pwd);
					info.put("internal_logon", "sysdba");
					try{
						Class.forName(driver).newInstance();
					}catch(Exception ex){
						
					}
					conn = DriverManager.getConnection(url, info);
					//stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
				} else {
					try{
						Class.forName(driver).newInstance();
					}catch(Exception ex){
						
					}
					conn = DriverManager.getConnection(url, username, pwd);
					//stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
				}
			}else{
				Class.forName(driver);	
				conn = DriverManager.getConnection(url, username, pwd);
			}
			//Class.forName(driver);
			//conn = DriverManager.getConnection(url, username, pwd);
		} catch (SQLException e) {
			message = "连接失败" + e.toString();
			System.out.println("连接失败" + e);
		} catch (ClassNotFoundException e) {
			message = "加载数据库驱动失败！" + e.toString();
			System.out.println("加载数据库驱动失败！" + e);
		}

		return conn;

	}

	public boolean testConnection(String driver, String url, String username,
			String pwd) {
		
		Connection conn = null;
		try {
			if(driver.equals("oracle.jdbc.driver.OracleDriver")){
				if (username.equals("sys") || username.equals("system")) {
					java.util.Properties info = new java.util.Properties();
					info.put("user", username);
					info.put("password", pwd);
					info.put("internal_logon", "sysdba");
					try{
						Class.forName(driver).newInstance();
					}catch(Exception ex){
						
					}
					conn = DriverManager.getConnection(url, info);
					//stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
				} else {
					try{
						Class.forName(driver).newInstance();
					}catch(Exception ex){
						
					}
					conn = DriverManager.getConnection(url, username, pwd);
					//stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
				}
			}else{
				Class.forName(driver);		
				conn = DriverManager.getConnection(url, username, pwd);
			}
			

		} catch (SQLException e) {
			System.out.println("连接失败" + e);
			return false;

		} catch (ClassNotFoundException e) {
			System.out.println("加载数据库驱动失败！" + e);
			return false;
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		this.driver = driver;
		this.url = url;
		this.username = username;
		this.pwd = pwd;
		return true;
	}

	public Vector<Vector<String>> getRows() {
		return rows;
	}

	public Vector<String> getHead() {
		return head;
	}

	public String getMessage() {
		return message;
	}

	public int getCount() {
		return count;
	}
}
