package com.afunms.application.util;

import java.io.FileInputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.util.CreateTableManager;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;
import com.mysql.jdbc.Driver;

public class CreateDBUtil  extends BaseDao{

	private static CreateDBUtil createDBUtil = new CreateDBUtil();
	
	public synchronized static CreateDBUtil getInstance(){
		if(createDBUtil == null){
			createDBUtil = new CreateDBUtil();
		}
		return createDBUtil;
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
	 * 生成数据库表
	 * @throws Exception
	 */
	public   void createDB() throws Exception{
		//获取数据库连接
		Driver driver = (Driver)Class.forName("com.mysql.jdbc.Driver").newInstance();
		Properties properties = new Properties();
		URL u = Thread.currentThread().getContextClassLoader().getResource("SystemConfigResources.properties");
		FileInputStream fis = new FileInputStream(u.getPath());
		properties.load(fis);
		String url = properties.getProperty("DATABASE_URL");
		properties.setProperty("user", properties.getProperty("DATABASE_USER"));
		properties.setProperty("password", properties.getProperty("DATABASE_PASSWORD"));
		Connection conn1 = driver.connect(url, properties);
		Statement stmt = conn1.createStatement();
		Statement truncStmt = conn1.createStatement();//截断表
//		ResultSet rs = stmt.executeQuery("select * from topo_host_node");
		ResultSet rs = stmt.executeQuery("select * from topo_host_node");
		try {
			while(rs.next()){
				String ip = rs.getString("ip_address");
				String allipstr = SysUtil.doip(ip);
				int category=rs.getInt("category");
				int collecttype=rs.getInt("collecttype");
				String sys_oid=rs.getString("sys_oid");
				CreateTableManager ctable = new CreateTableManager();
				//SysLogger.info(node.getIpAddress()+"======category:"+node.getCategory()+"==="+node.getDiscoverstatus());
				if ((category>0 && category< 4) || category == 7 || category == 8 || category == 9 || category == 10|| category == 11){
					
						//新发现的设备
						//生成网络设备表	
						if(collecttype==3){ 
							ctable.createTable(conn,"ping",allipstr,"ping");//Ping
							ctable.createTable(conn,"pinghour",allipstr,"pinghour");//Ping
							ctable.createTable(conn,"pingday",allipstr,"pingday");//Ping
						}else{
							ctable.createTable(conn,"ping",allipstr,"ping");//Ping
							ctable.createTable(conn,"pinghour",allipstr,"pinghour");//Ping
							ctable.createTable(conn,"pingday",allipstr,"pingday");//Ping
							
							
							ctable.createTable(conn,"memory",allipstr,"mem");//内存
							ctable.createTable(conn,"memoryhour",allipstr,"memhour");//内存
							ctable.createTable(conn,"memoryday",allipstr,"memday");//内存
							
							ctable.createTable(conn,"flash",allipstr,"flash");//闪存
							ctable.createTable(conn,"flashhour",allipstr,"flashhour");//闪存
							ctable.createTable(conn,"flashday",allipstr,"flashday");//闪存
							
							ctable.createTable(conn,"buffer",allipstr,"buffer");//缓存
							ctable.createTable(conn,"bufferhour",allipstr,"bufferhour");//缓存
							ctable.createTable(conn,"bufferday",allipstr,"bufferday");//缓存
							
							ctable.createTable(conn,"fan",allipstr,"fan");//风扇
							ctable.createTable(conn,"fanhour",allipstr,"fanhour");//风扇
							ctable.createTable(conn,"fanday",allipstr,"fanday");//风扇
							
							ctable.createTable(conn,"power",allipstr,"power");//电源
							ctable.createTable(conn,"powerhour",allipstr,"powerhour");//电源
							ctable.createTable(conn,"powerday",allipstr,"powerday");//电源
							
							ctable.createTable(conn,"vol",allipstr,"vol");//电压
							ctable.createTable(conn,"volhour",allipstr,"volhour");//电压
							ctable.createTable(conn,"volday",allipstr,"volday");//电压
							
							ctable.createTable(conn,"cpu",allipstr,"cpu");//CPU
							ctable.createTable(conn,"cpuhour",allipstr,"cpuhour");//CPU
							ctable.createTable(conn,"cpuday",allipstr,"cpuday");//CPU
							
							ctable.createTable(conn,"utilhdxperc",allipstr,"hdperc");
							ctable.createTable(conn,"hdxperchour",allipstr,"hdperchour");
							ctable.createTable(conn,"hdxpercday",allipstr,"hdpercday");	
							
							ctable.createTable(conn,"portstatus",allipstr,"port");
						//	ctable.createTable(conn,"portstatushour",allipstr,"porthour");
						//	ctable.createTable(conn,"portstatusday",allipstr,"portday");
							
							ctable.createTable(conn,"utilhdx",allipstr,"hdx");
							ctable.createTable(conn,"utilhdxhour",allipstr,"hdxhour");
							ctable.createTable(conn,"utilhdxday",allipstr,"hdxday");	
							
							ctable.createTable(conn,"allutilhdx",allipstr,"allhdx");
							ctable.createTable(conn,"autilhdxh",allipstr,"ahdxh");
							ctable.createTable(conn,"autilhdxd",allipstr,"ahdxd");
							
							ctable.createTable(conn,"discardsperc",allipstr,"dcardperc");
							ctable.createTable(conn,"dcarperh",allipstr,"dcarperh");
							ctable.createTable(conn,"dcarperd",allipstr,"dcarperd");
							
							ctable.createTable(conn,"errorsperc",allipstr,"errperc");
							ctable.createTable(conn,"errperch",allipstr,"errperch");
							ctable.createTable(conn,"errpercd",allipstr,"errpercd");
							
							ctable.createTable(conn,"packs",allipstr,"packs");	
							ctable.createTable(conn,"packshour",allipstr,"packshour");
							ctable.createTable(conn,"packsday",allipstr,"packsday");
							
							ctable.createTable(conn,"inpacks",allipstr,"inpacks");	
							ctable.createTable(conn,"ipacksh",allipstr,"ipacksh");
							ctable.createTable(conn,"ipackd",allipstr,"ipackd");
							
							ctable.createTable(conn,"outpacks",allipstr,"outpacks");	
							ctable.createTable(conn,"opackh",allipstr,"opackh");
							ctable.createTable(conn,"opacksd",allipstr,"opacksd");
							
							ctable.createTable(conn,"temper",allipstr,"temper");	
							ctable.createTable(conn,"temperh",allipstr,"temperh");
							ctable.createTable(conn,"temperd",allipstr,"temperd");
						}
				}else if(category == 12){
					//生成VPN设备表	
					if(collecttype==3){ 
						ctable.createTable(conn,"ping",allipstr,"ping");//Ping
						ctable.createTable(conn,"pinghour",allipstr,"pinghour");//Ping
						ctable.createTable(conn,"pingday",allipstr,"pingday");//Ping
					}else{
						ctable.createTable(conn,"ping",allipstr,"ping");//Ping
						ctable.createTable(conn,"pinghour",allipstr,"pinghour");//Ping
						ctable.createTable(conn,"pingday",allipstr,"pingday");//Ping
						
						
						ctable.createTable(conn,"memory",allipstr,"mem");//内存
						ctable.createTable(conn,"memoryhour",allipstr,"memhour");//内存
						ctable.createTable(conn,"memoryday",allipstr,"memday");//内存
						
						ctable.createTable(conn,"cpu",allipstr,"cpu");//CPU
						ctable.createTable(conn,"cpuhour",allipstr,"cpuhour");//CPU
						ctable.createTable(conn,"cpuday",allipstr,"cpuday");//CPU
						
						ctable.createTable(conn,"utilhdxperc",allipstr,"hdperc");
						ctable.createTable(conn,"hdxperchour",allipstr,"hdperchour");
						ctable.createTable(conn,"hdxpercday",allipstr,"hdpercday");	
						
						ctable.createTable(conn,"portstatus",allipstr,"port");
					//	ctable.createTable(conn,"portstatushour",allipstr,"porthour");
					//	ctable.createTable(conn,"portstatusday",allipstr,"portday");
						
						ctable.createTable(conn,"utilhdx",allipstr,"hdx");
						ctable.createTable(conn,"utilhdxhour",allipstr,"hdxhour");
						ctable.createTable(conn,"utilhdxday",allipstr,"hdxday");	
						
						ctable.createTable(conn,"allutilhdx",allipstr,"allhdx");
						ctable.createTable(conn,"autilhdxh",allipstr,"ahdxh");
						ctable.createTable(conn,"autilhdxd",allipstr,"ahdxd");
						
						ctable.createTable(conn,"discardsperc",allipstr,"dcardperc");
						ctable.createTable(conn,"dcarperh",allipstr,"dcarperh");
						ctable.createTable(conn,"dcarperd",allipstr,"dcarperd");
						
						ctable.createTable(conn,"errorsperc",allipstr,"errperc");
						ctable.createTable(conn,"errperch",allipstr,"errperch");
						ctable.createTable(conn,"errpercd",allipstr,"errpercd");
						
						ctable.createTable(conn,"packs",allipstr,"packs");	
						ctable.createTable(conn,"packshour",allipstr,"packshour");
						ctable.createTable(conn,"packsday",allipstr,"packsday");
						
						ctable.createTable(conn,"inpacks",allipstr,"inpacks");	
						ctable.createTable(conn,"ipacksh",allipstr,"ipacksh");
						ctable.createTable(conn,"ipackd",allipstr,"ipackd");
						
						ctable.createTable(conn,"outpacks",allipstr,"outpacks");	
						ctable.createTable(conn,"opackh",allipstr,"opackh");
						ctable.createTable(conn,"opacksd",allipstr,"opacksd");					
					}
				}else if(category == 13){
					//生成CMTS设备表	
					if(collecttype==3){ 
						ctable.createTable(conn,"ping",allipstr,"ping");//Ping
						ctable.createTable(conn,"pinghour",allipstr,"pinghour");//Ping
						ctable.createTable(conn,"pingday",allipstr,"pingday");//Ping
					}else{
						ctable.createTable(conn,"ping",allipstr,"ping");//Ping
						ctable.createTable(conn,"pinghour",allipstr,"pinghour");//Ping
						ctable.createTable(conn,"pingday",allipstr,"pingday");//Ping
						
						ctable.createTable(conn,"status",allipstr,"status");//通道状态
						ctable.createTable(conn,"statushour",allipstr,"statushour");//通道状态按小时
						ctable.createTable(conn,"statusday",allipstr,"statusday");//通道状态按天
						
						ctable.createCiscoCMTSTable(conn,"noise",allipstr,"noise");//通道信躁比
						ctable.createCiscoCMTSTable(conn,"noisehour",allipstr,"noisehour");//通道信躁比按小时
						ctable.createCiscoCMTSTable(conn,"noiseday",allipstr,"noiseday");//通道信躁比按天
						
						ctable.createCiscoCMTSIPMACTable(conn,"ipmac",allipstr,"ipmac");//IPMAC信息（在线用户信息）
						ctable.createTable(conn,"utilhdxpercs",allipstr,"hdpercs");
						ctable.createTable(conn,"hdxperchours",allipstr,"hdperchours");
						ctable.createTable(conn,"hdxpercdays",allipstr,"hdpercdays");	
						
						
						ctable.createTable(conn,"utilhdxs",allipstr,"hdxs");
						ctable.createTable(conn,"utilhdxhours",allipstr,"hdxhours");
						ctable.createTable(conn,"utilhdxdays",allipstr,"hdxdays");	
						
						ctable.createTable(conn,"allutilhdxs",allipstr,"allhdxs");
						ctable.createTable(conn,"autilhdxhs",allipstr,"ahdxhs");
						ctable.createTable(conn,"autilhdxds",allipstr,"ahdxds");
						
						ctable.createTable(conn,"discardspercs",allipstr,"dcardpercs");
						ctable.createTable(conn,"dcarperhs",allipstr,"dcarperhs");
						ctable.createTable(conn,"dcarperds",allipstr,"dcarperds");
						
						ctable.createTable(conn,"errorspercs",allipstr,"errpercs");
						ctable.createTable(conn,"errperchs",allipstr,"errperchs");
						ctable.createTable(conn,"errpercds",allipstr,"errpercds");
						
						ctable.createTable(conn,"packss",allipstr,"packss");	
						ctable.createTable(conn,"packshours",allipstr,"packshours");
						ctable.createTable(conn,"packsdays",allipstr,"packsdays");
						
						ctable.createTable(conn,"inpackss",allipstr,"inpackss");	
						ctable.createTable(conn,"ipackshs",allipstr,"ipackshs");
						ctable.createTable(conn,"ipackds",allipstr,"ipackds");
						
						ctable.createTable(conn,"outpackss",allipstr,"outpackss");	
						ctable.createTable(conn,"opackhs",allipstr,"opackhs");
						ctable.createTable(conn,"opacksds",allipstr,"opacksds");					
					}
				}else if(category == 14){
					//生成存储设备表	
					
					if(collecttype==3){ 
						ctable.createTable(conn,"pings",allipstr,"pings");//Ping
						ctable.createTable(conn,"pinghours",allipstr,"pinghours");//Ping
						ctable.createTable(conn,"pingdays",allipstr,"pingdays");//Ping
					}else if(collecttype==7||(sys_oid!= null && sys_oid.trim().startsWith("1.3.6.1.4.1.11.2.3.7.11")))
					{
						//hp storage
						ctable.createTable(conn,"ping",allipstr,"ping");//Ping
						ctable.createTable(conn,"pinghour",allipstr,"pinghour");//Ping
						ctable.createTable(conn,"pingday",allipstr,"pingday");//Ping
					}				
					else if(sys_oid != null && sys_oid.trim().startsWith("1.3.6.1.4.1.789.")){
						//NETAPP存储
						ctable.createTable(conn,"pings",allipstr,"pings");//Ping
						ctable.createTable(conn,"pingshour",allipstr,"pingshour");//Ping
						ctable.createTable(conn,"pingsday",allipstr,"pingsday");//Ping
						
						ctable.createTable(conn,"cpu",allipstr,"cpu");//CPU
						ctable.createTable(conn,"cpuhour",allipstr,"cpuhour");//CPU
						ctable.createTable(conn,"cpuday",allipstr,"cpuday");//CPU
						
						ctable.createTable(conn,"utilhdxperc",allipstr,"hdperc");					
						ctable.createTable(conn,"hdxperchour",allipstr,"hdperchour");
						ctable.createTable(conn,"hdxpercday",allipstr,"hdpercday");	
						
		
						ctable.createTable(conn,"utilhdx",allipstr,"hdx");
						ctable.createTable(conn,"utilhdxhour",allipstr,"hdxhour");
						ctable.createTable(conn,"utilhdxday",allipstr,"hdxday");	
						
						ctable.createTable(conn,"allutilhdx",allipstr,"allhdx");
						ctable.createTable(conn,"autilhdxh",allipstr,"ahdxh");
						ctable.createTable(conn,"autilhdxd",allipstr,"ahdxd");
						
						ctable.createTable(conn,"discardsperc",allipstr,"dcardperc");
						ctable.createTable(conn,"dcarperh",allipstr,"dcarperh");
						ctable.createTable(conn,"dcarperd",allipstr,"dcarperd");
						
						ctable.createTable(conn,"errorsperc",allipstr,"errperc");
						ctable.createTable(conn,"errperch",allipstr,"errperch");
						ctable.createTable(conn,"errpercd",allipstr,"errpercd");
						
						ctable.createTable(conn,"packs",allipstr,"packs");	
						ctable.createTable(conn,"packshour",allipstr,"packshour");
						ctable.createTable(conn,"packsday",allipstr,"packsday");
						
						ctable.createTable(conn,"inpacks",allipstr,"inpacks");	
						ctable.createTable(conn,"ipacksh",allipstr,"ipacksh");
						ctable.createTable(conn,"ipackd",allipstr,"ipackd");
						
						ctable.createTable(conn,"outpacks",allipstr,"outpacks");	
						ctable.createTable(conn,"opackh",allipstr,"opackh");
						ctable.createTable(conn,"opacksd",allipstr,"opacksd");
					}else if(sys_oid != null && !sys_oid.trim().startsWith("1.3.6.1.4.1.11.2.3.7.11")
							&& !sys_oid.trim().startsWith("1.3.6.1.4.1.789."))
					{
						ctable.createTable(conn,"pings",allipstr,"pings");//Ping
						ctable.createTable(conn,"pinghours",allipstr,"pinghours");//Ping
						ctable.createTable(conn,"pingdays",allipstr,"pingdays");//Ping
						
						ctable.createTable(conn,"env",allipstr,"env");//存储设备环境-风扇\电源\环境状态\驱动状态					
						
						ctable.createTable(conn,"efan",allipstr,"efan");//存储设备环境-风扇					
						ctable.createTable(conn,"epower",allipstr,"epower");//存储设备环境-电源					
						ctable.createTable(conn,"eenv",allipstr,"eenv");//存储设备环境-环境状态
						ctable.createTable(conn,"edrive",allipstr,"edrive");//存储设备环境-驱动状态					
						
						ctable.createTable(conn,"rcable",allipstr,"rcable");//运行状体：内部总线状态					
						ctable.createTable(conn,"rcache",allipstr,"rcache");//运行状体：缓存状态					
						ctable.createTable(conn,"rmemory",allipstr,"rmemory");//运行状体：共享内存状态
						ctable.createTable(conn,"rpower",allipstr,"rpower");//运行状体：电源状态
						ctable.createTable(conn,"rbutter",allipstr,"rbutter");//运行状体：电池状态					
						ctable.createTable(conn,"rfan",allipstr,"rfan");//运行状体：风扇状态					
						ctable.createTable(conn,"renv",allipstr,"renv");//存储设备环境-环境状态
						ctable.createTable(conn,"rluncon",allipstr,"rluncon");
						ctable.createTable(conn,"rsluncon",allipstr,"rsluncon");
						ctable.createTable(conn,"rwwncon",allipstr,"rwwncon");	
						ctable.createTable(conn,"rsafety",allipstr,"rsafety");
						ctable.createTable(conn,"rnumber",allipstr,"rnumber");
						ctable.createTable(conn,"rswitch",allipstr,"rswitch");	
						ctable.createTable(conn,"rcpu",allipstr,"rcpu");	
						
						ctable.createTable(conn,"events",allipstr,"events");//事件
						
						ctable.createEmcTable(conn,"emcdiskper",allipstr,"emcdiskper");
						ctable.createEmcTable(conn,"emclunper",allipstr,"emclunper");
						ctable.createEmcTable(conn,"emcenvpower",allipstr,"emcenvpower");
						ctable.createEmcTable(conn,"emcenvstore",allipstr,"emcenvstore");
						ctable.createEmcTable(conn,"emcbakpower",allipstr,"emcbakpower");
						
//						ctable.createTable(conn,"utilhdxpercs",allipstr,"hdpercs");
//						ctable.createTable(conn,"hdxperchours",allipstr,"hdperchours");
//						ctable.createTable(conn,"hdxpercdays",allipstr,"hdpercdays");	
						
						
//						ctable.createTable(conn,"utilhdxs",allipstr,"hdxs");
//						ctable.createTable(conn,"utilhdxhours",allipstr,"hdxhours");
//						ctable.createTable(conn,"utilhdxdays",allipstr,"hdxdays");	
//						
//						ctable.createTable(conn,"allutilhdxs",allipstr,"allhdxs");
//						ctable.createTable(conn,"autilhdxhs",allipstr,"ahdxhs");
//						ctable.createTable(conn,"autilhdxds",allipstr,"ahdxds");
//						
//						ctable.createTable(conn,"discardspercs",allipstr,"dcardpercs");
//						ctable.createTable(conn,"dcarperhs",allipstr,"dcarperhs");
//						ctable.createTable(conn,"dcarperds",allipstr,"dcarperds");
//						
//						ctable.createTable(conn,"errorspercs",allipstr,"errpercs");
//						ctable.createTable(conn,"errperchs",allipstr,"errperchs");
//						ctable.createTable(conn,"errpercds",allipstr,"errpercds");
//						
//						ctable.createTable(conn,"packss",allipstr,"packss");	
//						ctable.createTable(conn,"packshours",allipstr,"packshours");
//						ctable.createTable(conn,"packsdays",allipstr,"packsdays");
//						
//						ctable.createTable(conn,"inpackss",allipstr,"inpackss");	
//						ctable.createTable(conn,"ipackshs",allipstr,"ipackshs");
//						ctable.createTable(conn,"ipackds",allipstr,"ipackds");
//						
//						ctable.createTable(conn,"outpackss",allipstr,"outpackss");	
//						ctable.createTable(conn,"opackhs",allipstr,"opackhs");
//						ctable.createTable(conn,"opacksds",allipstr,"opacksds");					
					}
				}else if(category == 15){
					//生成VMWare表	
					if(collecttype==3){ 
						ctable.createTable(conn,"ping",allipstr,"ping");//Ping
						ctable.createTable(conn,"pinghour",allipstr,"pinghour");//Ping
						ctable.createTable(conn,"pingday",allipstr,"pingday");//Ping
					}else{
						ctable.createTable(conn,"ping",allipstr,"ping");//Ping
						ctable.createTable(conn,"pinghour",allipstr,"pinghour");//Ping
						ctable.createTable(conn,"pingday",allipstr,"pingday");//Ping
						
						ctable.createTable(conn,"memory",allipstr,"memory");//内存利用率	
						ctable.createTable(conn,"memoryhour",allipstr,"memhour");//内存
						ctable.createTable(conn,"memoryday",allipstr,"memday");//内存
						
						ctable.createTable(conn,"cpu",allipstr,"cpu");//CPU
						ctable.createTable(conn,"cpuhour",allipstr,"cpuhour");//CPU
						ctable.createTable(conn,"cpuday",allipstr,"cpuday");//CPU
						
						ctable.createTable(conn,"state",allipstr,"state");//虚拟机电源状况（打开或关闭）。 
						ctable.createTable(conn,"gstate",allipstr,"gstate");//客户机操作系统的状况（开或关）。 
						ctable.createVMhostTable(conn,"vm_host",allipstr,"vm_host");//创建VMWare 物理机的性能信息表
						ctable.createVMguesthostTable(conn,"vm_guesthost",allipstr,"vm_guesthost");//创建VMWare 虚拟机的性能信息表
						ctable.createVMCRTable(conn,"vm_cluster",allipstr,"vm_cluster");//创建VMWare 集群的性能信息表
						ctable.createVMDSTable(conn,"vm_datastore",allipstr,"vm_datastore");//创建VMWare 存储的性能信息表
						ctable.createVMRPTable(conn,"vm_resourcepool",allipstr,"vm_resourcepool");//创建VMWare 资源池的性能信息表
						//vm_basephysical
						ctable.createVMBaseTable(conn,"vm_basephysical",allipstr,"vm_basephysical");//创建VMWare 物理机的基础信息表
						ctable.createVMBaseTable(conn,"vm_basevmware",allipstr,"vm_basevmware");//创建VMWare 虚拟机的基础信息表
						ctable.createVMBaseTable(conn,"vm_baseyun",allipstr,"vm_baseyun");//创建VMWare 云资源的基础信息表
						ctable.createVMBaseTable(conn,"vm_basedatastore",allipstr,"vm_basedatastore");//创建VMWare 存储的基础信息表
						ctable.createVMBaseTable(conn,"vm_basedatacenter",allipstr,"vm_basedatacenter");//创建VMWare 数据中心的基础信息表
						ctable.createVMBaseTable(conn,"vm_baseresource",allipstr,"vm_baseresource");//创建VMWare 资源池的基础信息表
						
					}
				}else if(category == 4){
					//主机服务器
					//生成主机表	
					if(collecttype==3){ 
						ctable.createTable(conn,"ping",allipstr,"ping");//Ping
						ctable.createTable(conn,"pinghour",allipstr,"pinghour");//Ping
						ctable.createTable(conn,"pingday",allipstr,"pingday");//Ping
					}else{
						ctable.createTable(conn,"ping",allipstr,"ping");
						ctable.createTable(conn,"pinghour",allipstr,"pinghour");
						ctable.createTable(conn,"pingday",allipstr,"pingday");
						
						/*
						ctable.createTable("disk",allipstr,"disk");
						ctable.createTable("diskhour",allipstr,"diskhour");
						ctable.createTable("diskday",allipstr,"diskday");
						*/
						ctable.createTable(conn,"pro",allipstr,"pro");//进程
						ctable.createTable(conn,"prohour",allipstr,"prohour");//进程小时
						ctable.createTable(conn,"proday",allipstr,"proday");//进程天
						
						ctable.createSyslogTable(conn,"log",allipstr,"log");//进程天
						
						ctable.createTable(conn,"memory",allipstr,"mem");//内存
						ctable.createTable(conn,"memoryhour",allipstr,"memhour");//内存
						ctable.createTable(conn,"memoryday",allipstr,"memday");//内存
						
						ctable.createTable(conn,"cpu",allipstr,"cpu");//CPU
						ctable.createTable(conn,"cpuhour",allipstr,"cpuhour");//CPU
						ctable.createTable(conn,"cpuday",allipstr,"cpuday");//CPU
						
						ctable.createTable(conn,"cpudtl",allipstr,"cpudtl");	
						ctable.createTable(conn,"cpudtlhour",allipstr,"cpudtlhour");
						ctable.createTable(conn,"cpudtlday",allipstr,"cpudtlday");
						
						ctable.createTable(conn,"disk",allipstr,"disk");//yangjun
						ctable.createTable(conn,"diskhour",allipstr,"diskhour");
						ctable.createTable(conn,"diskday",allipstr,"diskday");
						
						ctable.createTable(conn,"diskincre",allipstr,"diskincre");//磁盘增长率yangjun
						ctable.createTable(conn,"diskinch",allipstr,"diskinch");//磁盘增长率小时
						ctable.createTable(conn,"diskincd",allipstr,"diskincd");//磁盘增长率天 
		
						ctable.createTable(conn,"utilhdxperc",allipstr,"hdperc");					
						ctable.createTable(conn,"hdxperchour",allipstr,"hdperchour");
						ctable.createTable(conn,"hdxpercday",allipstr,"hdpercday");	
						
		
						ctable.createTable(conn,"utilhdx",allipstr,"hdx");
						ctable.createTable(conn,"utilhdxhour",allipstr,"hdxhour");
						ctable.createTable(conn,"utilhdxday",allipstr,"hdxday");	
						
						ctable.createTable(conn,"allutilhdx",allipstr,"allhdx");
						ctable.createTable(conn,"autilhdxh",allipstr,"ahdxh");
						ctable.createTable(conn,"autilhdxd",allipstr,"ahdxd");
						
						ctable.createTable(conn,"discardsperc",allipstr,"dcardperc");
						ctable.createTable(conn,"dcarperh",allipstr,"dcarperh");
						ctable.createTable(conn,"dcarperd",allipstr,"dcarperd");
						
						ctable.createTable(conn,"errorsperc",allipstr,"errperc");
						ctable.createTable(conn,"errperch",allipstr,"errperch");
						ctable.createTable(conn,"errpercd",allipstr,"errpercd");
						
						ctable.createTable(conn,"packs",allipstr,"packs");	
						ctable.createTable(conn,"packshour",allipstr,"packshour");
						ctable.createTable(conn,"packsday",allipstr,"packsday");
						
						ctable.createTable(conn,"inpacks",allipstr,"inpacks");	
						ctable.createTable(conn,"ipacksh",allipstr,"ipacksh");
						ctable.createTable(conn,"ipackd",allipstr,"ipackd");
						
						ctable.createTable(conn,"outpacks",allipstr,"outpacks");	
						ctable.createTable(conn,"opackh",allipstr,"opackh");
						ctable.createTable(conn,"opacksd",allipstr,"opacksd");
						
						ctable.createTable(conn,"software",allipstr,"software");
//						System.out.println("com.afunms.topology.dao---->DiscoverCompleteDao.java----1502hang------==>"+sys_oid);
						if(sys_oid.equalsIgnoreCase("1.3.6.1.4.1.2.3.1.2.1.1")){
							//生成换页率
							ctable.createTable(conn,"pgused",allipstr,"pgused");	
							ctable.createTable(conn,"pgusedhour",allipstr,"pgusedhour");
							ctable.createTable(conn,"pgusedday",allipstr,"pgusedday");
						}
					}
				}
	            else if(category == 16)
				{// 中电国际艾默生空调添加的类型
					ctable.createTable(conn,"ping",allipstr,"ping");//Ping
					ctable.createTable(conn,"pinghour",allipstr,"pinghour");//Ping
					ctable.createTable(conn,"pingday",allipstr,"pingday");//Ping
				}
				else if(category == 17)
				{
					ctable.createTable(conn,"ping",allipstr,"ping");//Ping				
					ctable.createTable(conn,"pinghour",allipstr,"pinghour");//Ping				
					ctable.createTable(conn,"pingday",allipstr,"pingday");//Ping
					ctable.createTable(conn,"input",allipstr,"input");//input			
					ctable.createTable(conn,"inputhour",allipstr,"inputhour");//input			
					ctable.createTable(conn,"inputday",allipstr,"inputday");//input			
					ctable.createTable(conn,"output",allipstr,"output");//output				
					ctable.createTable(conn,"outputhour",allipstr,"outputhour");//output				
					ctable.createTable(conn,"outputday",allipstr,"outputday");//output
				}
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
			if(conn != null){
				conn.close();
			}
			if(truncStmt != null){
				truncStmt.close();
			}
			if(conn1 != null){
				conn1.close();
			}
		}
	}
	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		// TODO Auto-generated method stub
		return null;
	}
	public static void main(String[] args){
		try {
			CreateDBUtil.getInstance().createDB();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	

}
