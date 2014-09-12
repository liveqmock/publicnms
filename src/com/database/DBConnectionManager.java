package com.database;

import java.sql.*;

import com.database.config.*;
import com.mchange.v2.c3p0.*;


/**
 * 
 * 
 * 数据库建立管理实现类
 * 
 * @author itims
 *
 */

public class DBConnectionManager {
	
	private static ComboPooledDataSource cpds = null;

	private static void init() {
		
		if(cpds==null)
		{
		// 建立数据库连接池
		String DRIVER_NAME = SystemConfig.getConfigInfomation("SystemConfigResources","DRIVER_NAME"); // 驱动器
		String DATABASE_URL = SystemConfig.getConfigInfomation("SystemConfigResources","DATABASE_URL"); // 数据库连接url
		String DATABASE_USER = SystemConfig
				.getConfigInfomation("SystemConfigResources","DATABASE_USER"); // 数据库用户名
		String DATABASE_PASSWORD = SystemConfig
				.getConfigInfomation("SystemConfigResources","DATABASE_PASSWORD"); // 数据库密码
		
		int Min_PoolSize = 5;
		int Max_PoolSize = 50;
		int Acquire_Increment = 5;
		int Initial_PoolSize = 10;
		int Idle_Test_Period = 3000;// 每隔3000s测试连接是否可以正常使用
		int MAX_IdleTime=60;
		int Timeout=10000;//
		int CheckoutTimeout=3000;
		int NumHelperThreads=3;
		
		
		String Validate = SystemConfig.getConfigInfomation("SystemConfigResources","Validate");// 每次连接验证连接是否可用
		if (Validate.equals("")) {
			Validate = "false";
		}
		// 最小连接数
		try {
			Min_PoolSize = Integer.parseInt(SystemConfig
					.getConfigInfomation("SystemConfigResources","Min_PoolSize"));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		// 增量条数
		try {
			Acquire_Increment = Integer.parseInt(SystemConfig
					.getConfigInfomation("SystemConfigResources","Acquire_Increment"));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		// 最大连接数
		try {
			Max_PoolSize = Integer.parseInt(SystemConfig
					.getConfigInfomation("SystemConfigResources","Max_PoolSize"));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		// 初始化连接数
		try {
			Initial_PoolSize = Integer.parseInt(SystemConfig
					.getConfigInfomation("SystemConfigResources","Initial_PoolSize"));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		// 每隔3000s测试连接是否可以正常使用
		try {
			Idle_Test_Period = Integer.parseInt(SystemConfig
					.getConfigInfomation("SystemConfigResources","Idle_Test_Period"));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		
		// 
		try {
			MAX_IdleTime = Integer.parseInt(SystemConfig
					.getConfigInfomation("SystemConfigResources","MAX_IdleTime"));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		try {
			CheckoutTimeout = Integer.parseInt(SystemConfig
					.getConfigInfomation("SystemConfigResources","CheckoutTimeout"));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		try {
			NumHelperThreads = Integer.parseInt(SystemConfig
					.getConfigInfomation("SystemConfigResources","NumHelperThreads"));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		

		try {
			cpds = new ComboPooledDataSource();
			cpds.setDriverClass(DRIVER_NAME); // 驱动器
			cpds.setJdbcUrl(DATABASE_URL); // 数据库url
			cpds.setUser(DATABASE_USER); // 用户名
			cpds.setPassword(DATABASE_PASSWORD); // 密码
			cpds.setInitialPoolSize(Initial_PoolSize); // 初始化连接池大小
			cpds.setMinPoolSize(Min_PoolSize); // 最少连接数
			cpds.setMaxPoolSize(Max_PoolSize); // 最大连接数
			cpds.setAcquireIncrement(Acquire_Increment); // 连接数的增量
			cpds.setIdleConnectionTestPeriod(Idle_Test_Period); //测连接有效的时间间隔
			cpds.setTestConnectionOnCheckout(Boolean.getBoolean(Validate)); // 每次连接验证连接是否可用
			cpds.setMaxIdleTime(MAX_IdleTime);
			cpds.setCheckoutTimeout(CheckoutTimeout);
			cpds.setAutoCommitOnClose(true);
			cpds.setNumHelperThreads(NumHelperThreads);
			
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		}
		
	}

	

	/**
	 * 获取数据连接池
	 * @return
	 */
    public static  Connection getConnection() {    
        Connection con = null;    
        try {  
        	if (cpds == null) {
				init();
			}
        	
            con = cpds.getConnection();    
        } catch (SQLException e1) {    
            e1.printStackTrace();    
        }    
        return con;    
    }  
	
	
	
	
	/**
	 * 
	 * 关闭数据库连接（关闭数据源）
	 * 
	 */
	private static void release() {
		try {
			if (cpds != null) {
				cpds.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static void main(String arg[]) throws SQLException
	{
		
		DBConnectionManager db=new DBConnectionManager();
        db.getConnection();
        String SQL="select * from system_user";
        Connection con = null;    
        con= db.getConnection();
        //PreparedStatement pstmt = null;    
        //pstmt = con.prepareStatement(sql);    
        Statement stmt=con.createStatement();
        ResultSet rs=stmt.executeQuery(SQL);//数据库查询
        boolean flag=false;
        while(rs.next()) {    
            // 如果有记录，则执行此段代码    
            // 用户是合法的，可以登陆    
            flag = true;    
            
           }    
        db.release();


		
	}
	
	
	
}