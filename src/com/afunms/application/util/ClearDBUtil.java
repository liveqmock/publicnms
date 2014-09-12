package com.afunms.application.util;


import java.io.FileInputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import com.afunms.common.util.CreateTableManager;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;
import com.mysql.jdbc.Driver;

/**
 * @author HONGLI E-mail: ty564457881@163.com
 * @version 创建时间：Jul 27, 2011 8:35:13 PM
 * 类说明
 */
public class ClearDBUtil {
	private static ClearDBUtil clearDBUtil = new ClearDBUtil();
	
	public synchronized static ClearDBUtil getInstance(){
		if(clearDBUtil == null){
			clearDBUtil = new ClearDBUtil();
		}
		return clearDBUtil;
	}
	
	public static void println(Object obj){
		System.out.println(obj);
	}
	
	/**
	 * 执行单条截断语句
	 * @param stmt
	 * @param sql
	 * @return
	 */
	public boolean execute(Statement stmt, String sql){
		boolean b = true;
		System.out.println(sql+";");
		try {
			stmt.execute(sql);
		} catch (SQLException e) {
			b = false;
			println(e.getMessage());
		}
		return b;
	}
	
	/**
	 * 截断数据库临时表
	 * @throws Exception
	 */
	public void clearDB() throws Exception{
		//获取数据库连接
		Driver driver = (Driver)Class.forName("com.mysql.jdbc.Driver").newInstance();
		Properties properties = new Properties();
		URL u = Thread.currentThread().getContextClassLoader().getResource("SystemConfigResources.properties");
		FileInputStream fis = new FileInputStream(u.getPath());
		properties.load(fis);
		String url = properties.getProperty("DATABASE_URL");
		properties.setProperty("user", properties.getProperty("DATABASE_USER"));
		properties.setProperty("password", properties.getProperty("DATABASE_PASSWORD"));
		Connection conn = driver.connect(url, properties);
		Statement stmt = conn.createStatement();
		Statement truncStmt = conn.createStatement();//截断表
//		ResultSet rs = stmt.executeQuery("select * from topo_host_node");
		ResultSet rs = stmt.executeQuery("select * from topo_network_link");
		try {
			while(rs.next()){
				String ipaddress = rs.getString("ip_address");
				execute(truncStmt,"OPTIMIZE table allutilhdx"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table autilhdxd"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table autilhdxh"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table buffer"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table bufferday"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table bufferhour"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table cpu"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table cpuhour"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table cpuday"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table cpudtl"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table cpudtlday"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table cpudtlhour"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table dcarperd"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table dcarperh"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table discardsperc"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table disk"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table diskday"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table diskhour"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table diskincre"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table diskincd"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table diskinch"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table errorsperc"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table errpercd"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table errperch"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table fan"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table fanday"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table fanhour"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table flash"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table flashday"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table flashhour"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table hdxpercday"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table hdxperchour"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table inpacks"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table ipackd"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table ipacksh"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table memory"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table memoryday"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table memoryhour"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table outpacks"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table opacksd"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table opackh"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table packs"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table packshour"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table packsday"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table ping"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table pinghour"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table pingday"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table portstatus"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table power"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table powerday"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table powerhour"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table pro"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table proday"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table software"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table sqlping"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table sqlpingday"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table sqlpinghour"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table temper"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table temperd"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table temperh"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table utilhdx"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table utilhdxhour"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table utilhdxday"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table utilhdxperc"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table vol"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table volday"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table volhour"+SysUtil.doip(ipaddress));
			}
			execute(truncStmt,"OPTIMIZE table system_eventlist");
			execute(truncStmt,"OPTIMIZE table nms_alarminfo");
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if(fis != null){
				fis.close();
			}
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(stmt != null){
				stmt.close();
			}
			if(truncStmt != null){
				truncStmt.close();
			}
			if(conn != null){
				conn.close();
			}
		}
	}
	/**
	 * 截断数据库临时表
	 * @throws Exception
	 */
	public void clearLinkDB() throws Exception{
		//获取数据库连接
		Driver driver = (Driver)Class.forName("com.mysql.jdbc.Driver").newInstance();
		Properties properties = new Properties();
		URL u = Thread.currentThread().getContextClassLoader().getResource("SystemConfigResources.properties");
		FileInputStream fis = new FileInputStream(u.getPath());
		properties.load(fis);
		String url = properties.getProperty("DATABASE_URL");
		properties.setProperty("user", properties.getProperty("DATABASE_USER"));
		properties.setProperty("password", properties.getProperty("DATABASE_PASSWORD"));
		Connection conn = driver.connect(url, properties);
		Statement stmt = conn.createStatement();
		Statement truncStmt = conn.createStatement();//截断表
//		ResultSet rs = stmt.executeQuery("select * from topo_host_node");
		ResultSet rs = stmt.executeQuery("select * from topo_network_link");
		try {
			while(rs.next()){
				String ipaddress = rs.getString("id");
				execute(truncStmt,"OPTIMIZE table lkuhdxp"+SysUtil.doip(ipaddress));
				execute(truncStmt,"OPTIMIZE table lkping"+SysUtil.doip(ipaddress));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if(fis != null){
				fis.close();
			}
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(stmt != null){
				stmt.close();
			}
			if(truncStmt != null){
				truncStmt.close();
			}
			if(conn != null){
				conn.close();
			}
		}
	}

	public static void main(String[] args){
		try {
			ClearDBUtil.getInstance().clearLinkDB();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
