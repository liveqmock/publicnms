package com.afunms.polling.snmp;

/*
 * Created on 2005-3-2
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

import java.util.Hashtable;

import org.snmp4j.smi.OID;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SnmpMibConstants {
	
	// chen
	public static final String[] NetWorkMibInterfaceDesc = { "index", "ifDescr", "ifname", "ifType", "ifMtu", "ifSpeed", "ifPhysAddress", "ifAdminStatus", "ifOperStatus", "ifLastChange", "ifInOctets", "ifOutOctets", "ifInUcastPkts", "ifOutUcastPkts", "ifInNUcastPkts", "ifOutNUcastPkts", "ifInDiscards", "ifOutDiscards", "ifInErrors", "ifOutErrors", "ifInMulticastPkts", "ifOutMulticastPkts", "ifInBroadcastPkts", "ifOutBroadcastPkts" };
	public static final String[] NetWorkMibInterfaceUnit = { "", "", "", "", "bit", "kb/s", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "" };
	public static final String[] NetWorkMibInterfaceChname = { "端口索引", "描述", "端口名字", "类型", "最大传输字节数", "端口速度", "物理地址", "预期状态", "当前状态", "最近初始化", "接收字节数", "转发字节数", "接收单播包数", "转发单播包数", "接收非单播包数", "转发非单播包数", "接收丢包数", "转发丢包数", "接收错包数", "转发错包数", "接收组播包数", "转发组播包数", "接收广播包数", "转发广播包数" };
	public static final int[] NetWorkMibInterfaceScale = { 0, 0, 0, 0, 1, 1000, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
	//end chen

	
	
	
	
	/*
	 * 用户状态配置信息
	 */
	public static Hashtable UserStatusConfig = null;
	static {
		UserStatusConfig = new Hashtable();
		UserStatusConfig.put("OK", "启用");
		UserStatusConfig.put("Degraded", "退化");
	}
	
	/*
	 * 用户状态配置信息
	 */
	public static Hashtable NetworkStatusConfig = null;
	static {
		NetworkStatusConfig = new Hashtable();
		NetworkStatusConfig.put("2", "启用");
		NetworkStatusConfig.put("3", "禁用");
		NetworkStatusConfig.put("7", "禁用");
	}
	
	/*
	 * 用户状态配置信息
	 */
	public static Hashtable UserLocalAccountConfig = null;
	static {
		UserLocalAccountConfig = new Hashtable();
		UserLocalAccountConfig.put("True", "是");
		UserLocalAccountConfig.put("False", "否");
	}
		/*
		 * Service 启动模式配置信息
		 */
		public static Hashtable ServiceStartModeConfig = null;
		static {
			ServiceStartModeConfig = new Hashtable();
			ServiceStartModeConfig.put("Disabled", "禁用");
			ServiceStartModeConfig.put("Manual", "手动");
			ServiceStartModeConfig.put("Auto", "自动");
		}
		
		/*
		 * Service 状态配置信息
		 */
		public static Hashtable ServiceStateConfig = null;
		static {
			ServiceStateConfig = new Hashtable();
			ServiceStateConfig.put("Running", "运行");
			ServiceStateConfig.put("Stopped", "已停止");
		}
		
		/*
		 * Service 类型配置信息
		 */
		public static Hashtable ServiceTypeConfig = null;
		static {
			ServiceTypeConfig = new Hashtable();
			ServiceTypeConfig.put("Share Process", "共享");
			ServiceTypeConfig.put("Own Process", "独占");
		}
  
		/*
		 * WMI 网卡配置信息
		 */
	  public static Hashtable NetworkConfigName = null;
	  static {
		  NetworkConfigName = new Hashtable();
		  NetworkConfigName.put("IPAddress", "IP地址");
		  NetworkConfigName.put("Description", "描述");
		  NetworkConfigName.put("Index", "索引");
		  NetworkConfigName.put("IPSubnet", "子网掩码");
		  NetworkConfigName.put("MACAddress", "MAC地址");
		  NetworkConfigName.put("DNSServerSearchOrder", "DNS");
		  NetworkConfigName.put("DefaultIPGateway", "网关");
	  }
	  
	  /*
		 * WMI 网卡状态信息
		 */
	  public static Hashtable NetworkStatusName = null;
	  static {
		  NetworkStatusName = new Hashtable();
		  NetworkStatusName.put("IPAddress", "IP地址");
		  NetworkStatusName.put("Index", "索引");
		  NetworkStatusName.put("MACAddress", "MAC地址");
		  NetworkStatusName.put("Name", "名称");
		  NetworkStatusName.put("NetConnectionStatus", "状态");
	  }
	  
		/*
		 * WMI 磁盘配置信息
		 */
	  public static Hashtable HostInfoName = null;
	  static {
		  HostInfoName = new Hashtable();
		  HostInfoName.put("TotalPhysicalMemory", "物理内存");
		  HostInfoName.put("Hostname", "主机名");
		  HostInfoName.put("NumberOfProcessors", "处理器个数");
		  HostInfoName.put("Sysname", "系统名称");
		  HostInfoName.put("SerialNumber", "序列号");
		  HostInfoName.put("CSDVersion", "补丁包");
		  HostInfoName.put("CPUname", "CPU");
	  }
	  
		/*
		 * WMI 磁盘配置信息
		 */
	  public static Hashtable DiskConfigName = null;
	  static {
		  DiskConfigName = new Hashtable();
		  DiskConfigName.put("BytesPerSector", "每扇区字节数");
		  DiskConfigName.put("Caption", "描述");
		  DiskConfigName.put("Index", "索引");
		  DiskConfigName.put("InterfaceType", "类型");
		  DiskConfigName.put("Size", "大小");
	  }
	  
		/*
		 * WMI CPU性能信息
		 */
	  public static Hashtable CPUName = null;
	  static {
		  CPUName = new Hashtable();
		  CPUName.put("ContextSwitchesPersec", "进程切换率");
		  CPUName.put("ProcessorQueueLength", "队列的线程数量");
		  CPUName.put("SystemCallsPersec", "系统调用率");
	  }
	  
		/*
		 * WMI 服务信息
		 */
	  public static Hashtable ServiceName = null;
	  static {
		  ServiceName = new Hashtable();
		  ServiceName.put("DisplayName", "名称");
		  ServiceName.put("State", "状态");
		  ServiceName.put("ServiceType", "类型");
		  ServiceName.put("Description", "描述");
		  ServiceName.put("PathName", "路径");
		  ServiceName.put("StartMode", "启动模式");
	  }
	  
	  /*
		 * WMI 进程信息
		 */
	  public static Hashtable ProcessName = null;
	  static {
		  ProcessName = new Hashtable();
		  ProcessName.put("Caption", "标识");
		  ProcessName.put("CreationDate", "创建时间");
		  ProcessName.put("Description", "描述");
		  ProcessName.put("ExecutablePath", "执行路径");
		  ProcessName.put("Name", "名称");
		  ProcessName.put("WorkingSetSize", "内存大小");
		  ProcessName.put("Priority", "优先级");
		  ProcessName.put("ProcessId", "进程ID");
		  ProcessName.put("ThreadCount", "线程数量");
		  ProcessName.put("KernelModeTime", "核心态时间");
		  ProcessName.put("UserModeTime", "用户态时间");
	  }
	  
	  /*
		 * WMI 磁盘分区信息
		 */
	  public static Hashtable DiskpartionName = null;
	  static {
		  DiskpartionName = new Hashtable();
		  DiskpartionName.put("Caption", "标识");
		  DiskpartionName.put("VolumeName", "卷名称");
		  DiskpartionName.put("FileSystem", "文件系统");
		  DiskpartionName.put("FreeSpace", "空闲大小");
		  DiskpartionName.put("Size", "大小");
	  }
	  
	  /*
		 * WMI 磁盘分区信息
		 */
	  public static Hashtable CPUConfigName = null;
	  static {
		  CPUConfigName = new Hashtable();
		  CPUConfigName.put("DataWidth", "数据位");
		  CPUConfigName.put("LoadPercentage", "利用率");
		  CPUConfigName.put("ProcessorId", "处理器ID");
		  CPUConfigName.put("Name", "名称");
		  CPUConfigName.put("L2CacheSize", "二级缓存大小");
		  CPUConfigName.put("L2CacheSpeed", "二级缓存速度");
	  }
	  
	  /*
		 * WMI 内存性能信息
		 */
	  public static Hashtable MemoryName = null;
	  static {
		  MemoryName = new Hashtable();
		  MemoryName.put("AvailableBytes", "空闲的物理内存总量");
		  MemoryName.put("CommittedBytes", "利用率");
		  MemoryName.put("PageFaultsPersec", "秒处理的错误页/秒");
		  MemoryName.put("PagesInputPersec", "输入页面数/秒");
		  MemoryName.put("PagesOutputPersec", "输出页面数/秒 ");
		  MemoryName.put("PagesPersec", "读取的页数/秒");
	  }
	  
	  /*
		 * WMI 物理磁盘性能信息
		 */
	  public static Hashtable PhysicalDiskinfoName = null;
	  static {
		  PhysicalDiskinfoName = new Hashtable();
		  PhysicalDiskinfoName.put("AvgDiskQueueLength", "平均队列长度");
		  PhysicalDiskinfoName.put("AvgDiskReadQueueLength", "平均读队列长度");
		  PhysicalDiskinfoName.put("AvgDiskWriteQueueLength", "平均写队列长度");
		  PhysicalDiskinfoName.put("CurrentDiskQueueLength", "当前队列长度");
		  PhysicalDiskinfoName.put("DiskWriteBytesPersec", "磁盘写字节数/秒 ");
		  PhysicalDiskinfoName.put("PercentDiskTime", "忙时间");
	  }
	  
	  /*
		 * WMI 网络接口性能性能信息
		 */
	  public static Hashtable NetworkInterfaceinfoName = null;
	  static {
		  NetworkInterfaceinfoName = new Hashtable();
		  NetworkInterfaceinfoName.put("BytesReceivedPersec", "接受字节数/秒");
		  NetworkInterfaceinfoName.put("BytesSentPersec", "发送字节数/秒");
		  NetworkInterfaceinfoName.put("BytesTotalPersec", "字节总数/秒");
		  NetworkInterfaceinfoName.put("CurrentBandwidth", "带宽");
		  NetworkInterfaceinfoName.put("Name", "名称");
		  NetworkInterfaceinfoName.put("OutputQueueLength", "输出队列长度");
		  NetworkInterfaceinfoName.put("PacketsOutboundErrors", "错误数据包");
		  NetworkInterfaceinfoName.put("PacketsPersec", "数据包/秒");
		  NetworkInterfaceinfoName.put("PacketsReceivedDiscarded", "丢弃的接收的数据包");
		  NetworkInterfaceinfoName.put("PacketsReceivedErrors", "接收的错误数据包");
		  NetworkInterfaceinfoName.put("PacketsReceivedNonUnicastPersec", "接收的非单向数据包/秒 ");
		  NetworkInterfaceinfoName.put("PacketsReceivedPersec", "接收数据包/秒");
		  NetworkInterfaceinfoName.put("PacketsReceivedUnicastPersec", "平均读队列长度");
		  NetworkInterfaceinfoName.put("PacketsReceivedUnknown", "接收的不识别的数据包");
		  NetworkInterfaceinfoName.put("PacketsSentNonUnicastPersec", "发送的非单向数据包/秒");
		  NetworkInterfaceinfoName.put("PacketsSentPersec", "发送的数据包/秒");
		  NetworkInterfaceinfoName.put("PacketsSentUnicastPersec", "发送的单向数据包/秒");
	  }
	
	/*
	 * DB2参数
	 */
	public static final String[] DB2_Appl_status={"SQLM_INIT","SQLM_CONNECTPEND","SQLM_CONNECTED",
						"SQLM_UOWEXEC","SQLM_UOWWAIT","SQLM_LOCKWAIT","SQLM_COMMIT_ACT","SQLM_ROLLBACK_ACT",
						"SQLM_RECOMP","SQLM_COMP","SQLM_INTR","SQLM_DISCONNECTPEND","SQLM_TPREP",
						"SQLM_THCOMT","SQLM_THABRT","SQLM_TEND","SQLM_CREATE_DB","SQLM_RESTART",
						"SQLM_RESTORE","SQLM_BACKUP","SQLM_LOAD","SQLM_UNLOAD","SQLM_IOERROR_WAIT",
						"SQLM_QUIESCE_TABLESPACE","SQLM_WAITFOR_REMOTE","SQLM_REMOTE_RQST","SQLM_DECOUPLED",
						"SQLM_ROLLBACK_TO_SAVEPOINT"
						};
	
	public static final String[] DB2_Client_platform={"SQLM_PLATFORM_UNKNOWN","SQLM_PLATFORM_OS2","SQLM_PLATFORM_DOS",
		"SQLM_PLATFORM_WINDOWS","SQLM_PLATFORM_AIX","SQLM_PLATFORM_NT","SQLM_PLATFORM_HP","SQLM_PLATFORM_SUN",
		"SQLM_PLATFORM_MVS_DRDA","SQLM_PLATFORM_AS400_DRDA","SQLM_PLATFORM_VM_DRDA","SQLM_PLATFORM_VSE_DRDA","SQLM_PLATFORM_UNKNOWN_DRDA",
		"SQLM_PLATFORM_SNI","SQLM_PLATFORM_MAC","SQLM_PLATFORM_WINDOWS95","SQLM_PLATFORM_SCO","SQLM_PLATFORM_SGI",
		"SQLM_PLATFORM_LINUX","SQLM_PLATFORM_DYNIX","SQLM_PLATFORM_AIX64","SQLM_PLATFORM_SUN64","SQLM_PLATFORM_HP64",
		"SQLM_PLATFORM_NT64","SQLM_PLATFORM_LINUX390","SQLM_PLATFORM_LINUXZ64","SQLM_PLATFORM_LINUXIA64",
		"SQLM_PLATFORM_LINUXPPC","SQLM_PLATFORM_LINUXPPC64","SQLM_PLATFORM_OS390","SQLM_PLATFORM_LINUXX8664",
		"SQLM_PLATFORM_HPIA","SQLM_PLATFORM_HPIA64","SQLM_PLATFORM_SUNX86","SQLM_PLATFORM_SUNX8664"
		};
	public static final String[] DB2_Client_protocol={"SQL_PROTOCOL_APPC","SQL_PROTOCOL_NETB","SQL_PROTOCOL_APPN",
		"SQL_PROTOCOL_TCPIP","SQL_PROTOCOL_CPIC","SQL_PROTOCOL_IPXSPX","SQL_PROTOCOL_LOCAL","SQL_PROTOCOL_NPIPE"
		};

	public static final String[] DB2_db_status={"SQLM_DB_ACTIVE","SQLM_DB_QUIESCE_PEND","SQLM_DB_QUIESCED",
		"SQLM_DB_ROLLFWD"
		};
	
	public static final String[] NetWorkMibInterfaceDesc0={"index","ifDescr","ifType","ifMtu",
											"ifSpeed","ifPhysAddress","ifAdminStatus","ifOperStatus",
											"ifLastChange","ifname"
											};
	public static final String[]  NetWorkMibInterfaceUnit0={"","","","bit",
										"kb/s","","","",
										"",""
									};
	public static final String[] NetWorkMibInterfaceChname0={"端口索引","描述","类型","最大数据包",
											"每秒字节数(M)","端口Mac地址","预期状态","当前状态",
											"系统sysUpTime评估","端口描述2"
	
											
													};
	public static final int[]  NetWorkMibInterfaceScale0={0,0,0,1,
											1000,0,0,0,
											0,0
										};
	public static final String[] NetWorkMibInterfaceDesc1={"index","ifInOctets","ifInMulticastPkts","ifInBroadcastPkts",
											"ifInDiscards","ifInErrors","ifOutOctets",
											"ifOutMulticastPkts","ifOutBroadcastPkts","ifOutDiscards","ifOutErrors",
											"ifOutQLen","ifSpecific"
									};
	public static final String[] NetWorkMibInterfaceChname1={"端口索引","接收的字节","单向传输数据包","非单向传输数据包",
											"被丢弃的数据包","入站错误数据包","入站不知名的数据包","传输的字节",
											"单向传输数据包","非单向传输数据包","出站被丢弃的数据包","出站传输失败的数据包",
											"输出信息包排列的长度","Mib对端口的说明"
									};
	public static final String[] NetWorkMibInterfaceDesc3={"index","ifInMulticastPkts","ifInBroadcastPkts","ifOutMulticastPkts",
												"ifOutBroadcastPkts"
									};
	public static final String[] NetWorkMibInterfaceChname3={"端口索引","接收的多播数据包数目","接收的广播数据包数目","发送的多播数据包数目",
											"发送的广播数据包数目"
									};
	public static final String[] NetWorkMibInterfaceDesc2={"ifType","ifInOctets","ifOutOctets"};
	public static final String[] NetWorkMibInterfaceChname2={"类型","接收的字节","传输的字节"
									};
	public static final String[] NetWorkMibInterfaceUnit1={"kb","","",
											"","","kb",
											"","","","",
											"",""
									};
	public static final int[] NetWorkMibInterfaceScale1={1024,0,1,
											0,0,1024,
											0,0,0,0,
											0,0
									};
	public static final String[] NetWorkMibInterfaceUnit2={"","KB","KB"};
public static final int[] NetWorkMibInterfaceScale2={1,1024,1024};

	public static final String[] NetWorkMibSystemDesc={"sysDescr","sysUpTime","sysContact","sysName","sysLocation","sysServices"
														};
	public static final String[] NetWorkMibSystemChname={"描述","运行时间","联系人","设备名称","设备位置","服务类型"			
	};
	//public static final String[] UpsMibStatueChname={"机型容量","本机台号","并机系统A相输出总有功功率","并机系统B相输出总有功功率","并机系统C相输出总有功功率",
    //     "并机系统A相输出总视在功率","并机系统A相输出总视在功率","并机系统A相输出总视在功率",
    //     "并机系统A相输出总无功功率","并机系统A相输出总无功功率","并机系统A相输出总无功功率"};
	public static final String[] UpsMibStatueChname={"机型容量","本机台号","并机系统A相输出总有功功率","并机系统B相输出总有功功率","并机系统C相输出总有功功率",
        "并机系统A相输出总视在功率","并机系统B相输出总视在功率","并机系统C相输出总视在功率"};
//	public static final String[] UpsMibOutputChname={"输出线电压A","输出线电压B","输出线电压C","A相输出电流","B相输出电流",
//        "C相输出电流","输出频率","A相输出功率因数","B相输出功率因数","C相输出功率因数",
//        "A相输出有功功率","B相输出有功功率","C相输出有功功率","A相输出视在功率","B相输出视在功率","C相输出视在功率",
//        "A相输出无功功率","B相输出无功功率","C相输出无功功率",
//         "A相输出负载百分比","B相输出负载百分比","C相输出负载百分比",
//			"A相输出峰值比","B相输出峰值比","C相输出峰值比"};

	public static final String[] UpsMibOutputChname={"输出线电压A","输出线电压B","输出线电压C","A相输出电流","B相输出电流",
        "C相输出电流","输出频率","A相输出功率因数","B相输出功率因数","C相输出功率因数",
        "A相输出有功功率","B相输出有功功率","C相输出有功功率","A相输出视在功率","B相输出视在功率","C相输出视在功率",        
         "A相输出负载百分比","B相输出负载百分比","C相输出负载百分比",
			"A相输出峰值比","B相输出峰值比","C相输出峰值比"};
	public static final String[] UpsMibSystemDesc={"sysmodel","sysname","sysfirmware","sysDate","sysSerial ","sysFactory"};
	//public static final String[] UpsMibInputDesc={"SRXDYAB","SRXDYBC","SRXDYCA","AXSRDY","BXSRDY","CXSRDY","AXSRDL","BXSRDL","CXSRDL","SRPL","SRGLYSA","SRGLYSB","SRGLYSC"};
	public static final String[] UpsMibInputDesc={"SRXDYAB","SRXDYBC","SRXDYCA","AXSRDL","BXSRDL","CXSRDL","SRPL","SRGLYS"};
	//public static final String[] UpsMibInputChname={"输入线电压AB","输入线电压BC","输入线电压CA","A相输入电压","B相输入电压","C相输入电压","A相输入电流","B相输入电流","C相输入电流","输入频率","输入功率因数A","输入功率因数B","输入功率因数C"};
	public static final String[] UpsMibInputChname={"输入线电压AB","输入线电压BC","输入线电压CA","A相输入电流","B相输入电流","C相输入电流","输入频率","输入功率因数"};
	public static final String[] UpsMibAlarmDesc={"INDEX","ALARMTIME","ALARMID","NAME","LEVEL"};
	public static final String[] UpsMibAlarmChname={"索引","告警时间","告警事件ID","事件名称","等级"};
	public static final String[] UpsMibBypassDesc={"AXPLDY","BXPLDY","CXPLDY","PLPL"};
    public static final String[] UpsMibBypassChname={"A相旁路电压","B相旁路电压","C相旁路电压","旁路频率"};
    public static final String[] UpsMibBypassUnit={"V","V","V","Hz"};
    public static final String[] UpsMibBatteryUnit={"V","A","min","摄氏度","摄氏度","",""};
    //public static final String[] UpsMibStatueUnit={"","","kW","kW","kW","kVA","kVA","kVA","KVAR","KVAR","KVAR"};
    public static final String[] UpsMibStatueUnit={"","","kW","kW","kW","kVA","kVA","kVA"};
    //public static final String[] UpsMibInputUnit={"V","V","V","V","V","V","A","A","A","Hz","Hz","Hz","","",""};
    public static final String[] UpsMibInputUnit={"V","V","V","A","A","A","Hz",""};
    //public static final String[] UpsMibOutputUnit={"V","V","V","A","A","A","HZ","","","","kW","kW","kW","kVA","kVA","kVA","KVAR","KVAR","KVAR","%","%","%","","",""};
    public static final String[] UpsMibOutputUnit={"V","V","V","A","A","A","HZ","","","","kW","kW","kW","kVA","kVA","kVA","%","%","%","","",""};
//    public static final String[] UpsMibOutputDesc={"SCXDYA","SCXDYB","SCXDYC","AXSCDL","BXSCDL",
//		                                             "CXSCDL","SCPL","AXSCGLYS","BXSCGLYS","CXSCGLYS",
//		                                             "AXSCYGGL","BXSCYGGL","CXSCYGGL","AXSCSZGL","BXSCSZGL","CXSCSZGL",
//		                                             "AXSCWGGL","BXSCWGGL","CXSCWGGL",
//		                                             "AXSCFZBFB","BXSCFZBFB","CXSCFZBFB",
//														"AXSCFZB","BXSCFZB","CXSCFZB"};
    public static final String[] UpsMibOutputDesc={"SCXDYA","SCXDYB","SCXDYC","AXSCDL","BXSCDL",
        "CXSCDL","SCPL","AXSCGLYS","BXSCGLYS","CXSCGLYS",
        "AXSCYGGL","BXSCYGGL","CXSCYGGL","AXSCSZGL","BXSCSZGL","CXSCSZGL",        
        "AXSCFZBFB","BXSCFZBFB","CXSCFZBFB",
			"AXSCFZB","BXSCFZB","CXSCFZB"};
	public static final String[] UpsMibSystemChname={"型号","名称","固件版本","出厂日期","序列号","生产厂家"};
	//public static final String[] UpsMibStatueDesc={"JXRL","BJTH","BJXTAXSCZYGGL","BJXTBXSCZYGGL","BJXTCXSCZYGGL","BJXTAXSCZSZGL","BJXTBXSCZSZGL",
      //  "BJXTCXSCZSZGL","BJXTAXSCZWGGL","BJXTBXSCZWGGL","BJXTCXSCZWGGL"};
	public static final String[] UpsMibStatueDesc={"JXRL","BJTH","BJXTAXSCZYGGL","BJXTBXSCZYGGL","BJXTCXSCZYGGL","BJXTAXSCZSZGL","BJXTBXSCZSZGL",
      "BJXTCXSCZSZGL"};
public static final String[] UpsMibBatteryChname={"电池电压","电池电流","电池剩余后备时间","电池温度","环境温度","设备描述","设备名称"};
public static final String[] UpsMibBatteryDesc={"DCDY","DCDL","DCSYHBSJ","DCWD","HJWD","SBMS","SBMC"};
public static final String[] NetWorkMibHardwareDesc={"entPhysicalDescr1","entPhysicalDescr2","entPhysicalDescr3","entPhysicalDescr4"};
public static final String[] NetWorkMibHardwareChname={"描述1","描述2","描述3","描述4"};
public static final String[] NetWorkMibCapabilityDesc={"PortName","PortPhysAdminStatus","PortPhysOperStatus","PortC3InFrames","PortC3OutFrames","PortC3InOctets","PortC3OutOctets","PortC3Discards"};
public static final String[] NetWorkMibCapabilityChname={"光口名称","状态","当前状态","入口帧数","出口帧数","入口帧字节数","出口帧字节数","丢包数"};
public static final String[]  NetWorkMibCapabilityUnit0={"","","","帧","帧","KB/秒","KB/秒","个"};
public static final int[]  NetWorkMibCapabilityScale0={0,0,0,1,1,1024*8,1024*8,1};
public static final String[] NetWorkMibCapabilityDesc1={"PortName","PortPhysAdminStatus","PortPhysOperStatus","PortOlsIns","PortOlsOuts","PortC3InFrames","PortC3OutFrames","PortC3InOctets","PortC3OutOctets","PortC3Discards"};
public static final String[] NetWorkMibCapabilityChname1={"光口名称","状态","当前状态","PortOlsIns","PortOlsOuts","入口帧数","出口帧数","入口帧字节数","出口帧字节数","丢包数"};
public static final String[]  NetWorkMibCapabilityUnit1={"","","","","","帧","帧","KB/秒","KB/秒","个"};
public static final int[]  NetWorkMibCapabilityScale1={0,0,0,1,1,1,1,1024*8,1024*8,1};
public static final String[] NetWorkMibConfigDesc={"FabricName","ElementName","ModuleCapacity","ModuleDescr","FeModuleObjectID","ModuleOperStatus","UPTime","ModuleFxPortCapacity","ModuleName"};
public static final String[] NetWorkMibConfigChname={"描述","光纤元素名称","光纤模块的最大数量","模块描述","模块OID","模块状态","模块连续运行时间","光口数","模块名称"};

public static final String[] UPSMibInDesc={"JLSRXDYAB","JLSRXDYBC","JLSRXDYCA","AXSRDL","BXSRDL",
		    "CXSRDL","SRPL","AXSRGLYS","BXSRGLYS","CXSRGLYS"};
public static final String[] UPSMibInChname={"交流输入线电压AB","交流输入线电压BC","交流输入线电压CA","A相输入电流","B相输入电流",
			"C相输入电流","输入频率","A相输入功率因数","B相输入功率因数","C相输入功率因数"};

public static final String[] UPSMibSystemDesc={"ZLSRDY","DCDL","DCHBSJ","DCWD","HJWD"};
public static final String[] UPSMibSystemChname={"直流输入电压","电池电流","电池后备时间","电池温度","环境温度"};

public static final String[] UPSMibBranchDesc={"AXPLDY","BXPLDY","CXPLDY","PLPL"};
public static final String[] UPSMibBranchChname={"A相旁路电压","B相旁路电压","C相旁路电压","旁路频率"};

public static final String[] UPSMibOutDesc={"JLSCXDYA","JLSCXDYB","JLSCXDYC","AXSCDL","BXSCDL",
		"CXSCDL","SCPL","AXSCGLYS","BXSCGLYS","CXSCGLYS"};
public static final String[] UPSMibOutChname={"交流输出线电压A","交流输出线电压B","交流输出线电压C","A相输出电流","B相输出电流",
		"C相输出电流","输出频率","A相输出功率因数","B相输出功率因数","C相输出功率因数"};

public static final String[] UPSMibSelfOutPowerDesc={"BJAXSCYGGL","BJBXSCYGGL","BJCXSCYGGL",
		"BJAXSCSZGL","BJBXSCSZGL","BJCXSCSZGL",
		"BJAXSCWGGL","BJBXSCWGGL","BJCXSCWGGL"};
public static final String[] UPSMibSelfOutPowerChname={"本机A相输出有功功率","本机B相输出有功功率","本机C相输出有功功率",
		"本机A相输出视在功率","本机B相输出视在功率","本机C相输出视在功率",
		"本机A相输出无功功率","本机B相输出无功功率","本机C相输出无功功率"};

public static final String[] UPSMibSelfOutPerDesc={"BJAXSCFZBFB","BJBXSCFZBFB","BJCXSCFZBFB",
		"BJAXSCFZB","BJBXSCFZB","BJCXSCFZB"};
public static final String[] UPSMibSelfOutPerChname={"本机A相输出负载百分比","本机B相输出负载百分比","本机C相输出负载百分比",
		"本机A相输出峰值比","本机B相输出峰值比","本机C相输出峰值比"};


	
	public static final Hashtable allUps = new Hashtable();
					
	  static {
		  allUps.put("BJAXSCFZBFB", "本机A相输出负载百分比");
		  allUps.put("BJBXSCFZBFB", "本机B相输出负载百分比");
		  allUps.put("BJCXSCFZBFB", "本机C相输出负载百分比");
		  allUps.put("BJAXSCFZB", "本机A相输出峰值比");
		  allUps.put("BJBXSCFZB", "本机B相输出峰值比");
		  allUps.put("BJCXSCFZB", "本机C相输出峰值比");
		  allUps.put("ZLSRDY", "直流输入电压");
		  allUps.put("DCDL", "电池电流");
		  allUps.put("DCHBSJ", "电池后备时间");
		  allUps.put("DCWD", "电池温度");
		  allUps.put("HJWD", "环境温度");
		  
		  allUps.put("BJAXSCYGGL", "本机A相输出有功功率");
		  allUps.put("BJBXSCYGGL", "本机B相输出有功功率");
		  allUps.put("BJCXSCYGGL", "本机C相输出有功功率");
		  allUps.put("BJAXSCSZGL", "本机A相输出视在功率");
		  allUps.put("BJBXSCSZGL", "本机B相输出视在功率");
		  allUps.put("BJCXSCSZGL", "本机C相输出视在功率");
		  allUps.put("BJAXSCWGGL", "本机A相输出无功功率");
		  allUps.put("BJBXSCWGGL", "本机B相输出无功功率");
		  allUps.put("BJCXSCWGGL", "本机C相输出无功功率");

		  allUps.put("JLSCXDYA", "交流输出线电压A");
		  allUps.put("JLSCXDYB", "交流输出线电压B");
		  allUps.put("JLSCXDYC", "交流输出线电压C");
		  allUps.put("AXSCDL", "A相输出电流");
		  allUps.put("BXSCDL", "B相输出电流");
		  allUps.put("CXSCDL", "C相输出电流");
		  allUps.put("SCPL", "输出频率");
		  allUps.put("AXSCGLYS", "A相输出功率因数");
		  allUps.put("BXSCGLYS", "B相输出功率因数");
		  allUps.put("CXSCGLYS", "C相输出功率因数");
		  
		  allUps.put("AXPLDY", "A相旁路电压");
		  allUps.put("BXPLDY", "B相旁路电压");
		  allUps.put("CXPLDY", "C相旁路电压");
		  allUps.put("PLPL", "旁路频率");		  
		  
		  allUps.put("JLSRXDYAB", "交流输入线电压AB");
		  allUps.put("JLSRXDYBC", "交流输入线电压BC");
		  allUps.put("JLSRXDYCA", "交流输入线电压CA");
		  allUps.put("AXSRDL", "A相输入电流");
		  allUps.put("BXSRDL", "B相输入电流");
		  allUps.put("CXSRDL", "C相输入电流");
		  allUps.put("SRPL", "输入频率");
		  allUps.put("AXSRGLYS", "A相输入功率因数");
		  allUps.put("BXSRGLYS", "B相输入功率因数");
		  allUps.put("CXSRGLYS", "C相输入功率因数");

	  }
	
		public static final Hashtable allUpsEvent = new Hashtable();
		
		  static {			  			  			  			  
			  allUpsEvent.put("Mains Frequency Recovered", "主路频率恢复正常");
			  allUpsEvent.put("Input Disconnect Closed", "输入空开闭合");
			  allUpsEvent.put("Mains Voltage Recovered", "主路电压恢复正常");
			  allUpsEvent.put("UPS Inverter On", "UPS开机");
			  allUpsEvent.put("UPS in Normal Mode", "UPS主路逆变供电");
			  allUpsEvent.put("Battery Float Charging", "电池浮充");
			  allUpsEvent.put("System in Normal Mode", "系统主路逆变供电");
			  allUpsEvent.put("Battery Not Charging", "电池停止充电");
			  allUpsEvent.put("UPS Battery Testing", "电池自检中");
			  allUpsEvent.put("Battery Self Test Completed", "电池自检完成");
			  allUpsEvent.put("Battery Boost Charging", "电池均充");			  
			  allUpsEvent.put("Inverter Asynchronous", "逆变器不同步");			  	
			  allUpsEvent.put("Bypass Abnormal", "旁路超限");
			  allUpsEvent.put("UPS in Battery Mode", "UPS电池逆变供电");
			  allUpsEvent.put("Mains Voltage Abnormal", "主路掉电");
			  allUpsEvent.put("Mains Frequency Abnormal", "主路频率超限");
			  allUpsEvent.put("System in Battery Mode", "系统电池逆变供电");
			  allUpsEvent.put("Bypass Recovered", "旁路恢复正常");
			  allUpsEvent.put("Inverter Resumed to be Synchr", "逆变器同步恢复");
			  allUpsEvent.put("Battery Low Pre-warning", "电池电压预告警");
			  allUpsEvent.put("UPS Shutdown", "UPS不供电");
			  allUpsEvent.put("UPS Inverter Off", "UPS关机");
			  allUpsEvent.put("System in Bypass Mode", "系统旁路供电");
			  allUpsEvent.put("System Shutdown", "系统不供电");
			  allUpsEvent.put("Input Disconnect Open", "输入空开断开");			  			  
			  allUpsEvent.put("Output Disconnect Open", "输出空开断开");
			  allUpsEvent.put("Bypass Disconnect Open", "自动旁路空开断开");
			  allUpsEvent.put("Bypass Disconnect Closed", "自动旁路空开闭合");
			  allUpsEvent.put("Output Disconnect Closed", "输出空开闭合");
			  allUpsEvent.put("Parallel Communication Fail", "并机通讯故障");			  
			  allUpsEvent.put("Parallel Communication Recove", "并机通讯故障消除");
			  allUpsEvent.put("UPS in Bypass Mode", "UPS旁路供电");
			  allUpsEvent.put("System Battery Low Fault", "系统电池预告警");
		  }
	
	public static final String[] TopsecMibSystemDesc=
			{"tosProductType","tosSysVersion","tosDeviceName","systemTime","currentConnections","cps"};
	public static final String[] TopsecMibSystemChname=
			{"产品类型","版本","设备名称","系统时间","当前连接数","每秒新连接数"};
	
	
//	standard traps
   public static final OID coldStart =
	   new OID(new int[] { 1,3,6,1,6,3,1,1,5,1 });
   public static final OID warmStart =
	   new OID(new int[] { 1,3,6,1,6,3,1,1,5,2 });
   public static final OID authenticationFailure =
	   new OID(new int[] { 1,3,6,1,6,3,1,1,5,5 });
   public static final OID linkDown =
	 new OID(new int[] { 1,3,6,1,6,3,1,1,5,3 });
   public static final OID linkUp =
	 new OID(new int[] { 1,3,6,1,6,3,1,1,5,4 });
	 
//	mib库名称_mib分组_mib名称

	  //系统时间
	  public static final String HOST_hrSystem_hrSystemDate =
		  "1.3.6.1.2.1.25.1.2.0";
	  //运行时间	
	  public static final String HOST_hrSystem_hrSystemUptime =
		  "1.3.6.1.2.1.25.1.1.0";
	  //系统用户数
	  public static final String HOST_hrSystem_hrSystemNumUsers =
		  "1.3.6.1.2.1.25.1.5.0";
	  //运行进程数	
	  public static final String HOST_hrSystem_hrSystemProcesses =
		  "1.3.6.1.2.1.25.1.6.0";

	  //列出各种存储体的使用情况
	  //其它
	  public static final String TYPE_HOST_hrStorage_hrStorageOther =
		  "1.3.6.1.2.1.25.2.1.1";
	  //内存	
	  public static final String TYPE_HOST_hrStorage_hrStorageRam =
		  "1.3.6.1.2.1.25.2.1.2";
	  //虚拟内存	
	  public static final String TYPE_HOST_hrStorage_hrStorageVirtualMemory =
		  "1.3.6.1.2.1.25.2.1.3";
	  //硬盘	
	  public static final String TYPE_HOST_hrStorage_hrStorageFixedDisk =
		  "1.3.6.1.2.1.25.2.1.4";
	  //可移动盘
	  public static final String TYPE_HOST_hrStorage_hrStorageRemovableDisk =
		  "1.3.6.1.2.1.25.2.1.5";
	  //软盘	
	  public static final String TYPE_HOST_hrStorage_hrStorageFloppyDisk =
		  "1.3.6.1.2.1.25.2.1.6";
	  //光盘	
	  public static final String TYPE_HOST_hrStorage_hrStorageCompactDisc =
		  "1.3.6.1.2.1.25.2.1.7";
	  //RamDisk
	  public static final String TYPE_HOST_hrStorage_hrStorageRamDisk =
		  "1.3.6.1.2.1.25.2.1.8";

	  public static Hashtable TYPE_HOST_hrStorage = null;
	  static {
		  TYPE_HOST_hrStorage = new Hashtable();
		  TYPE_HOST_hrStorage.put(TYPE_HOST_hrStorage_hrStorageOther, "其它");
		  TYPE_HOST_hrStorage.put(TYPE_HOST_hrStorage_hrStorageRam, "内存");
		  TYPE_HOST_hrStorage.put(
			  TYPE_HOST_hrStorage_hrStorageVirtualMemory,
			  "虚拟内存");
		  TYPE_HOST_hrStorage.put(TYPE_HOST_hrStorage_hrStorageFixedDisk, "硬盘");
		  TYPE_HOST_hrStorage.put(
			  TYPE_HOST_hrStorage_hrStorageRemovableDisk,
			  "可移动盘");
		  TYPE_HOST_hrStorage.put(TYPE_HOST_hrStorage_hrStorageFloppyDisk, "软盘");
		  TYPE_HOST_hrStorage.put(TYPE_HOST_hrStorage_hrStorageCompactDisc, "光盘");
		  TYPE_HOST_hrStorage.put(
			  TYPE_HOST_hrStorage_hrStorageRamDisk,
			  "RamDisk");
	  }
	  //物理内存
	  public static final String HOST_hrStorage_hrMemorySize =
		  "1.3.6.1.2.1.25.2.2.0";

	  public static final String TABLE_HOST_hrStorage_hrStorageIndex =
		  "1.3.6.1.2.1.25.2.3.1.1";
	  public static final String TABLE_HOST_hrStorage_hrStorageType =
		  "1.3.6.1.2.1.25.2.3.1.2";
	  public static final String TABLE_HOST_hrStorage_hrStorageDescr =
		  "1.3.6.1.2.1.25.2.3.1.3";
	  //分配单位	
	  public static final String TABLE_HOST_hrStorage_hrStorageAllocationUnits =
		  "1.3.6.1.2.1.25.2.3.1.4";
	  //大小
	  public static final String TABLE_HOST_hrStorage_hrStorageSize =
		  "1.3.6.1.2.1.25.2.3.1.5";
	  //已使用	
	  public static final String TABLE_HOST_hrStorage_hrStorageUsed =
		  "1.3.6.1.2.1.25.2.3.1.6";
	  //分配失败	
	  public static final String TABLE_HOST_hrStorage_hrStorageAllocationFailures =
		  "1.3.6.1.2.1.25.2.3.1.7";

	  //其它设备
	  public static final String TYPE_HOST_hrDevice_hrDeviceOther =
		  "1.3.6.1.2.1.25.3.1.1";
	  //未知设备	
	  public static final String TYPE_HOST_hrDevice_hrDeviceUnknown =
		  "1.3.6.1.2.1.25.3.1.2";
	  //处理器	
	  public static final String TYPE_HOST_hrDevice_hrDeviceProcessor =
		  "1.3.6.1.2.1.25.3.1.3";
	  //网卡	
	  public static final String TYPE_HOST_hrDevice_hrDeviceNetwork =
		  "1.3.6.1.2.1.25.3.1.4";
	  //打印机	
	  public static final String TYPE_HOST_hrDevice_hrDevicePrinter =
		  "1.3.6.1.2.1.25.3.1.5";
	  //磁盘设备	
	  public static final String TYPE_HOST_hrDevice_hrDeviceDiskStorage =
		  "1.3.6.1.2.1.25.3.1.6";
	  //显示卡	
	  public static final String TYPE_HOST_hrDevice_hrDeviceVideo =
		  "1.3.6.1.2.1.25.3.1.10";
	  //声卡	
	  public static final String TYPE_HOST_hrDevice_hrDeviceAudio =
		  "1.3.6.1.2.1.25.3.1.11";
	  //协处理器
	  public static final String TYPE_HOST_hrDevice_hrDeviceCoprocessor =
		  "1.3.6.1.2.1.25.3.1.12";
	  //键盘	
	  public static final String TYPE_HOST_hrDevice_hrDeviceKeyboard =
		  "1.3.6.1.2.1.25.3.1.13";
	  //调制解调器	
	  public static final String TYPE_HOST_hrDevice_hrDeviceModem =
		  "1.3.6.1.2.1.25.3.1.14";
	  //并口	
	  public static final String TYPE_HOST_hrDevice_hrDeviceParallelPort =
		  "1.3.6.1.2.1.25.3.1.15";
	  //鼠标	
	  public static final String TYPE_HOST_hrDevice_hrDevicePointing =
		  "1.3.6.1.2.1.25.3.1.16";
	  //串口
	  public static final String TYPE_HOST_hrDevice_hrDeviceSerialPort =
		  "1.3.6.1.2.1.25.3.1.17";
	  //磁带机	
	  public static final String TYPE_HOST_hrDevice_hrDeviceTape =
		  "1.3.6.1.2.1.25.3.1.18";
	  //时钟	
	  public static final String TYPE_HOST_hrDevice_hrDeviceClock =
		  "1.3.6.1.2.1.25.3.1.19";
	  //虚拟内存	
	  public static final String TYPE_HOST_hrDevice_hrDeviceVolatileMemory =
		  "1.3.6.1.2.1.25.3.1.20";
	  //物理内存	
	  public static final String TYPE_HOST_hrDevice_hrDeviceNonVolatileMemory =
		  "1.3.6.1.2.1.25.3.1.21";

	  public static Hashtable TYPE_HOST_hrDevice = null;
	  static {
		  TYPE_HOST_hrDevice = new Hashtable();
		  TYPE_HOST_hrDevice.put(TYPE_HOST_hrDevice_hrDeviceOther, "其它设备");
		  TYPE_HOST_hrDevice.put(TYPE_HOST_hrDevice_hrDeviceUnknown, "未知设备");
		  TYPE_HOST_hrDevice.put(TYPE_HOST_hrDevice_hrDeviceProcessor, "处理器");
		  TYPE_HOST_hrDevice.put(TYPE_HOST_hrDevice_hrDeviceNetwork, "网卡");
		  TYPE_HOST_hrDevice.put(TYPE_HOST_hrDevice_hrDevicePrinter, "打印机");
		  TYPE_HOST_hrDevice.put(TYPE_HOST_hrDevice_hrDeviceDiskStorage, "磁盘设备");
		  TYPE_HOST_hrDevice.put(TYPE_HOST_hrDevice_hrDeviceVideo, "显示卡");
		  TYPE_HOST_hrDevice.put(TYPE_HOST_hrDevice_hrDeviceAudio, "声卡");
		  TYPE_HOST_hrDevice.put(TYPE_HOST_hrDevice_hrDeviceCoprocessor, "协处理器");
		  TYPE_HOST_hrDevice.put(TYPE_HOST_hrDevice_hrDeviceKeyboard, "键盘");
		  TYPE_HOST_hrDevice.put(TYPE_HOST_hrDevice_hrDeviceModem, "调制解调器");
		  TYPE_HOST_hrDevice.put(TYPE_HOST_hrDevice_hrDeviceParallelPort, "并口");
		  TYPE_HOST_hrDevice.put(TYPE_HOST_hrDevice_hrDevicePointing, "鼠标");
		  TYPE_HOST_hrDevice.put(TYPE_HOST_hrDevice_hrDeviceSerialPort, "串口");
		  TYPE_HOST_hrDevice.put(TYPE_HOST_hrDevice_hrDeviceTape, "磁带机");
		  TYPE_HOST_hrDevice.put(TYPE_HOST_hrDevice_hrDeviceClock, "时钟");
		  TYPE_HOST_hrDevice.put(
			  TYPE_HOST_hrDevice_hrDeviceVolatileMemory,
			  "虚拟内存");
		  TYPE_HOST_hrDevice.put(
			  TYPE_HOST_hrDevice_hrDeviceNonVolatileMemory,
			  "物理内存");

	  }
	  public static Hashtable HOST_hrDevice_hrDeviceStatus = null;
	  static {
		  HOST_hrDevice_hrDeviceStatus = new Hashtable();
		  HOST_hrDevice_hrDeviceStatus.put("1", "未知");
		  HOST_hrDevice_hrDeviceStatus.put("2", "运行");
		  HOST_hrDevice_hrDeviceStatus.put("3", "警告");
		  HOST_hrDevice_hrDeviceStatus.put("4", "测试");
		  HOST_hrDevice_hrDeviceStatus.put("5", "停止");
	  }

	  //主机设备情况
	  public static final String TABLE_HOST_hrDevice_hrDeviceIndex =
		  "1.3.6.1.2.1.25.3.2.1.1";
	  public static final String TABLE_HOST_hrDevice_hrDeviceType =
		  "1.3.6.1.2.1.25.3.2.1.2";
	  public static final String TABLE_HOST_hrDevice_hrDeviceDescr =
		  "1.3.6.1.2.1.25.3.2.1.3";
	  public static final String TABLE_HOST_hrDevice_hrDeviceID =
		  "1.3.6.1.2.1.25.3.2.1.4";
	  public static final String TABLE_HOST_hrDevice_hrDeviceStatus =
		  "1.3.6.1.2.1.25.3.2.1.5";
	  public static final String TABLE_HOST_hrDevice_hrDeviceErrors =
		  "1.3.6.1.2.1.25.3.2.1.6";

	  public static final String TABLE_HOST_hrDevice_hrProcessorFrwID =
		  "1.3.6.1.2.1.25.3.3.1.1";
	  public static final String TABLE_HOST_hrDevice_hrProcessorLoad =
		  "1.3.6.1.2.1.25.3.3.1.2";

	  //正在运行的进程列表
	  public static final String TABLE_HOST_hrSWRun_hrSWRunIndex =
		  "1.3.6.1.2.1.25.4.2.1.1";
	  //名称
	  public static final String TABLE_HOST_hrSWRun_hrSWRunName =
		  "1.3.6.1.2.1.25.4.2.1.2";
	  public static final String TABLE_HOST_hrSWRun_hrSWRunID =
		  "1.3.6.1.2.1.25.4.2.1.3";
	  //运行目录	
	  public static final String TABLE_HOST_hrSWRun_hrSWRunPath =
		  "1.3.6.1.2.1.25.4.2.1.4";
	  //运行参数	
	  public static final String TABLE_HOST_hrSWRunParameters =
		  "1.3.6.1.2.1.25.4.2.1.5";
	  //类别	
	  public static final String TABLE_HOST_hrSWRun_hrSWRunType =
		  "1.3.6.1.2.1.25.4.2.1.6";
	  //状态	
	  public static final String TABLE_HOST_hrSWRun_hrSWRunStatus =
		  "1.3.6.1.2.1.25.4.2.1.7";

	  public static final String TABLE_HOST_hrSWRunPerf_hrSWRunPerfCPU =
		  "1.3.6.1.2.1.25.5.1.1.1";
	  public static final String TABLE_HOST_hrSWRunPerf_hrSWRunPerfMem =
		  "1.3.6.1.2.1.25.5.1.1.2";
	  public static Hashtable HOST_hrSWRun_hrSWRunType = null;
	  static {
		  HOST_hrSWRun_hrSWRunType = new Hashtable();
		  HOST_hrSWRun_hrSWRunType.put("1", "未知");
		  HOST_hrSWRun_hrSWRunType.put("2", "操作系统");
		  HOST_hrSWRun_hrSWRunType.put("3", "设备驱动");
		  HOST_hrSWRun_hrSWRunType.put("4", "应用程序");

	  }
	  public static Hashtable HOST_hrSWRun_hrSWRunStatus = null;
	  static {
		  HOST_hrSWRun_hrSWRunStatus = new Hashtable();
		  HOST_hrSWRun_hrSWRunStatus.put("1", "正在运行");
		  HOST_hrSWRun_hrSWRunStatus.put("2", "等待");
		  HOST_hrSWRun_hrSWRunStatus.put("3", "运行等待结果");
		  HOST_hrSWRun_hrSWRunStatus.put("4", "有问题");
	  }
	  //安装的软件列表
	  public static final String TABLE_HOST_hrSWInstalled_hrSWInstalledIndex =
		  "1.3.6.1.2.1.25.6.3.1.1";
	  //名称	
	  public static final String TABLE_HOST_hrSWInstalled_hrSWInstalledName =
		  "1.3.6.1.2.1.25.6.3.1.2";
	  public static final String TABLE_HOST_hrSWInstalled_hrSWInstalledID =
		  "1.3.6.1.2.1.25.6.3.1.3";
	  //类别	
	  public static final String TABLE_HOST_hrSWInstalled_hrSWInstalledType =
		  "1.3.6.1.2.1.25.6.3.1.4";
	  //安装日期	
	  public static final String TABLE_HOST_hrSWInstalled_hrSWInstalledDate =
		  "1.3.6.1.2.1.25.6.3.1.5";
	  public static Hashtable HOST_hrSWInstalled_hrSWInstalledType = null;
	  static {
		  HOST_hrSWInstalled_hrSWInstalledType = new Hashtable();
		  HOST_hrSWInstalled_hrSWInstalledType.put("4", "应用程序");
	  }

	  //当前系统激活的所有服务
	  public static final String TABLE_LANMANAGER_server_svSvcName =
		  "1.3.6.1.4.1.77.1.2.3.1.1";
	  public static final String TABLE_LANMANAGER_server_svSvcInstalledState =
		  "1.3.6.1.4.1.77.1.2.3.1.2";
	  public static final String TABLE_LANMANAGER_server_svSvcOperatingState =
		  "1.3.6.1.4.1.77.1.2.3.1.3";
	  public static final String TABLE_LANMANAGER_server_svSvcCanBeUninstalled =
		  "1.3.6.1.4.1.77.1.2.3.1.4";
	  public static final String TABLE_LANMANAGER_server_svSvcCanBePaused =
		  "1.3.6.1.4.1.77.1.2.3.1.5";

	  //已注册用户	
	  public static final String TABLE_LANMANAGER_server_svUserName =
		  "1.3.6.1.4.1.77.1.2.25.1.1";

	  //共享文件夹。文件夹共享也存在安全隐患，管理者掌握该情况也可作相应的处理	
	  public static final String TABLE_LANMANAGER_server_svShareName =
		  "1.3.6.1.4.1.77.1.2.27.1.1";
	  public static final String TABLE_LANMANAGER_server_svSharePath =
		  "1.3.6.1.4.1.77.1.2.27.1.2";
	  public static final String TABLE_LANMANAGER_server_svShareComment =
		  "1.3.6.1.4.1.77.1.2.27.1.3";

	  public static final String RFC1213_interfaces_ifNumber =
		  "1.3.6.1.2.1.2.1.0";

	  public static final String TABLE_RFC1213_interfaces_ifIndex =
		  "1.3.6.1.2.1.2.2.1.1";
	  public static final String TABLE_RFC1213_interfaces_ifDescr =
		  "1.3.6.1.2.1.2.2.1.2";
	  public static final String TABLE_RFC1213_interfaces_ifType =
		  "1.3.6.1.2.1.2.2.1.3";

	  //输出错误数据包的数量
	  public static final String TABLE_RFC1213_interfaces_ifOutErrors =
		  "1.3.6.1.2.1.2.2.1.20";
	  //输出丢弃的数据包的数量	
	  public static final String TABLE_RFC1213_interfaces_ifOutDiscards =
		  "1.3.6.1.2.1.2.2.1.19";
	  //输入错误数据包的数量	
	  public static final String TABLE_RFC1213_interfaces_ifInErrors =
		  "1.3.6.1.2.1.2.2.1.14";
	  //输入未知协议包的数量	
	  public static final String TABLE_RFC1213_interfaces_ifInUnknownProtos =
		  "1.3.6.1.2.1.2.2.1.15";

	  public static final String TABLE_RFC1213_interfaces_ifOutUcastPkts =
		  "1.3.6.1.2.1.2.2.1.17";
	  public static final String TABLE_RFC1213_interfaces_ifOutNUcastPkts =
		  "1.3.6.1.2.1.2.2.1.18";

	  public static final String TABLE_RFC1213_interfaces_ifInUcastPkts =
		  "1.3.6.1.2.1.2.2.1.11";
	  public static final String TABLE_RFC1213_interfaces_ifInNUcastPkts =
		  "1.3.6.1.2.1.2.2.1.12";
	  public static final String TABLE_RFC1213_interfaces_ifInOctets =
		  "1.3.6.1.2.1.2.2.1.10";
	  public static final String TABLE_RFC1213_interfaces_ifOutOctets =
		  "1.3.6.1.2.1.2.2.1.16";
	  public static final String TABLE_RFC1213_interfaces_ifSpeed =
		  "1.3.6.1.2.1.2.2.1.5";

	  // ------------- microsoft IIS Server 监控	
	  //	
	  public static final String httpServer_httpStatistics_totalBytesSentHighWord =
		  "1.3.6.1.4.1.311.1.7.3.1.1.0";
	  //	
	  public static final String httpServer_httpStatistics_totalBytesSentLowWord =
		  "1.3.6.1.4.1.311.1.7.3.1.2.0";
	  //	
	  public static final String httpServer_httpStatistics_totalBytesReceivedHighWord =
		  "1.3.6.1.4.1.311.1.7.3.1.3.0";
	  //	
	  public static final String httpServer_httpStatistics_totalBytesReceivedLowWord =
		  "1.3.6.1.4.1.311.1.7.3.1.4.0";
	  //	
	  public static final String httpServer_httpStatistics_totalFilesSent =
		  "1.3.6.1.4.1.311.1.7.3.1.5.0";
	  //	
	  public static final String httpServer_httpStatistics_totalFilesReceived =
		  "1.3.6.1.4.1.311.1.7.3.1.6.0";
	  //	
	  public static final String httpServer_httpStatistics_currentAnonymousUsers =
		  "1.3.6.1.4.1.311.1.7.3.1.7.0";
	  //	
	  public static final String httpServer_httpStatistics_totalAnonymousUsers =
		  "1.3.6.1.4.1.311.1.7.3.1.9.0";
	  //	
	  public static final String httpServer_httpStatistics_maxAnonymousUsers =
		  "1.3.6.1.4.1.311.1.7.3.1.11.0";
	  //	
	  public static final String httpServer_httpStatistics_currentConnections =
		  "1.3.6.1.4.1.311.1.7.3.1.13.0";
	  //	
	  public static final String httpServer_httpStatistics_maxConnections =
		  "1.3.6.1.4.1.311.1.7.3.1.14.0";
	  //	
	  public static final String httpServer_httpStatistics_connectionAttempts =
		  "1.3.6.1.4.1.311.1.7.3.1.15.0";
	  //	
	  public static final String httpServer_httpStatistics_logonAttempts =
		  "1.3.6.1.4.1.311.1.7.3.1.16.0";
	  //	
	  public static final String httpServer_httpStatistics_totalGets =
		  "1.3.6.1.4.1.311.1.7.3.1.18.0";
	  //	
	  public static final String httpServer_httpStatistics_totalPosts =
		  "1.3.6.1.4.1.311.1.7.3.1.19.0";
	  //	
	  public static final String httpServer_httpStatistics_totalNotFoundErrors =
		  "1.3.6.1.4.1.311.1.7.3.1.43.0"; 
	  
}