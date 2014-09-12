/*
 * Created on 2005-3-2
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.afunms.common.util;

import java.util.Hashtable;

import org.snmp4j.smi.OID;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SnmpMibConstants {
	
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
	public static final String[]  NetWorkMibInterfaceUnit0={"","","","字节",
										"KB/秒","","","",
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
	public static final String[] NetWorkMibInterfaceDesc1={"index","ifInOctets","ifInUcastPkts","ifInNUcastPkts",
											"ifInDiscards","ifInErrors","ifInUnknownProtos","ifOutOctets",
											"ifOutUcastPkts","ifOutNUcastPkts","ifOutDiscards","ifOutErrors",
											"ifOutQLen","ifSpecific"
									};
	public static final String[] NetWorkMibInterfaceChname1={"端口索引","接收的字节","单向传输数据包","非单向传输数据包",
											"被丢弃的数据包","入站错误数据包","入站不知名的数据包","传输的字节",
											"单向传输数据包","非单向传输数据包","出站被丢弃的数据包","出站传输失败的数据包",
											"输出信息包排列的长度","Mib对端口的说明"
									};
	public static final String[] NetWorkMibInterfaceUnit1={"KB","","",
											"","","KB",
											"","","","",
											"",""
									};
	public static final int[] NetWorkMibInterfaceScale1={1024,0,1,
											0,0,1024,
											0,0,0,0,
											0,0
									};
	public static final String[] NetWorkMibSystemDesc={"sysDescr","sysUpTime","sysContact","sysName","sysLocation","sysServices"
														};
	public static final String[] NetWorkMibSystemChname={"描述","运行时间","联系人","设备名称","设备位置","服务类型"
																		};

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
	  
	  public static final Hashtable  scStatus= new Hashtable();
		static {
			scStatus.put("100", "继续发出请求");
			scStatus.put("101", "服务器根据请求转换HTTP协议版本");
			scStatus.put("200", "交易成功");
			scStatus.put("201", "新文件的URL");
			scStatus.put("202", "接受和处理、但处理未完成");
			scStatus.put("203", "返回信息不确定或不完整");
			scStatus.put("204", "请求收到，但返回信息为空");
			scStatus.put("205", "服务器完成了请求，用户代理必须复位当前已经浏览过的文件");
			scStatus.put("206", "服务器已经完成了部分用户的GET请求");
			scStatus.put("300", "请求的资源可在多处得到");
			scStatus.put("301", "删除请求数据");
			scStatus.put("302", "在其他地址发现了请求数据");
			scStatus.put("303", "建议客户访问其他URL或访问方式");
			scStatus.put("304", "客户端已经执行了GET，但文件未变化");
			scStatus.put("305", "请求的资源必须从服务器指定的地址得到");
			scStatus.put("306", "前一版本HTTP中使用的代码，现行版本中不再使用");
			scStatus.put("307", "申明请求的资源临时性删除");
			scStatus.put("400", "错误请求，如语法错误");
			scStatus.put("401", "请求授权失败");
			scStatus.put("402", "保留有效ChargeTo头响应");
			scStatus.put("403", "请求不允许");
			scStatus.put("404", "没有发现文件、查询或URl");
			scStatus.put("405", "用户在Request-Line字段定义的方法不允许");
			scStatus.put("406", "根据用户发送的Accept拖，请求资源不可访问");
			scStatus.put("407", "类似401，用户必须首先在代理服务器上得到授权");
			scStatus.put("408", "客户端没有在用户指定的饿时间内完成请求");
			scStatus.put("409", "对当前资源状态，请求不能完成");
			scStatus.put("410", "服务器上不再有此资源且无进一步的参考地址");
			scStatus.put("411", "服务器拒绝用户定义的Content-Length属性请求");
			scStatus.put("412", "一个或多个请求头字段在当前请求中错误");
			scStatus.put("413", "请求的资源大于服务器允许的大小");
			scStatus.put("414", "请求的资源URL长于服务器允许的长度");
			scStatus.put("415", "请求资源不支持请求项目格式");
			scStatus.put("416", "请求中包含Range请求头字段，在当前请求资源范围内没有range指示值，请求也不包含If-Range请求头字段");
			scStatus.put("417", "服务器不满足请求Expect头字段指定的期望值，如果是代理服务器，可能是下一级服务器不能满足请求 ");
			scStatus.put("500", "服务器产生内部错误");
			scStatus.put("501", "服务器不支持请求的函数");
			scStatus.put("502", "服务器暂时不可用，有时是为了防止发生系统过载");
			scStatus.put("503", "服务器过载或暂停维修");
			scStatus.put("504", "关口过载，服务器使用另一个关口或服务来响应用户，等待时间设定值较长");
			scStatus.put("505", "服务器不支持或拒绝支请求头中指定的HTTP版本");
			
			
		}
		
		public static final Hashtable SCWIN32STATUS = new Hashtable();

		static {
			SCWIN32STATUS.put("0", "操作已成功完成");
			SCWIN32STATUS.put("1", "错误的函数");
			SCWIN32STATUS.put("2", "系统找不到指定的文件");
			SCWIN32STATUS.put("3", "系统找不到指定的路径");
			SCWIN32STATUS.put("4", "系统无法打开文件");
			SCWIN32STATUS.put("5", "拒绝访问");
			SCWIN32STATUS.put("6", "句柄无效");
			SCWIN32STATUS.put("7", "存储区控制块已损坏");
			SCWIN32STATUS.put("8", "可用的存储区不足，无法执行该命令");
			SCWIN32STATUS.put("9", "存储区控制块地址无效");
			SCWIN32STATUS.put("10", "环境错误");
			SCWIN32STATUS.put("11", "试图使用不正确的格式加载程序");
			SCWIN32STATUS.put("12", "访问代码无效");
			SCWIN32STATUS.put("13", "数据无效");
			SCWIN32STATUS.put("14", "可用的存储区不足，无法完成该操作");
			SCWIN32STATUS.put("15", "系统找不到指定的驱动器");
			SCWIN32STATUS.put("16", "无法删除该目录");
			SCWIN32STATUS.put("17", "系统无法将文件移到其他磁盘驱动器上");
			SCWIN32STATUS.put("18", "没有其他文件");
			SCWIN32STATUS.put("19", "媒体写保护");
			SCWIN32STATUS.put("20", "系统找不到指定的设备");
			SCWIN32STATUS.put("21", "设备尚未准备好");
			SCWIN32STATUS.put("22", "设备无法识别该命令");
			SCWIN32STATUS.put("23", "数据错误（循环冗余检查）");
			SCWIN32STATUS.put("24", "程序发出命令，但是该命令的长度错误");
			SCWIN32STATUS.put("25", "驱动器在磁盘上无法定位指定的区域或磁道");
			SCWIN32STATUS.put("26", "无法访问指定的磁盘或软盘");
			SCWIN32STATUS.put("27", "驱动器找不到所请求的扇区");
			SCWIN32STATUS.put("28", "打印机缺纸");
			SCWIN32STATUS.put("29", "系统无法写入指定的设备");
			SCWIN32STATUS.put("30", "系统无法读取指定的设备");
			SCWIN32STATUS.put("31", "与系统连接的设备不能正常运转");
			SCWIN32STATUS.put("32", "其他进程正使用该文件，因此现在无法访问");
			SCWIN32STATUS.put("33", "另一程序已锁定该文件的某一部分，因此现在无法访问");
			SCWIN32STATUS.put("34", "驱动器中的软盘不正确。请将   %2   （卷标序列号:   %3）插入驱动器   %");
			SCWIN32STATUS.put("36", "打开共享的文件太多");
			SCWIN32STATUS.put("38", "已到达文件结尾");
			SCWIN32STATUS.put("39", "磁盘已满");
			SCWIN32STATUS.put("50", "不支持此网络请求");
			SCWIN32STATUS.put("51", "远程计算机无法使用");
			SCWIN32STATUS.put("52", "网络中存在重名");
			SCWIN32STATUS.put("53", "找不到网络路径");
			SCWIN32STATUS.put("54", "网络正忙");
			SCWIN32STATUS.put("55", "指定的网络资源或设备已不可用");
			SCWIN32STATUS.put("56", "已经达到网络   BIOS   命令的极限");
			SCWIN32STATUS.put("57", "网络适配器出现错误");
			SCWIN32STATUS.put("58", "指定的服务器无法运行所请求的操作");
			SCWIN32STATUS.put("59", "网络出现意外错误");
			SCWIN32STATUS.put("60", "远程适配器不兼容");
			SCWIN32STATUS.put("61", "打印机队列已满");
			SCWIN32STATUS.put("62", "服务器上没有存储等待打印的文件的空间");
			SCWIN32STATUS.put("63", "已经删除等候打印的文件");
			SCWIN32STATUS.put("64", "指定的网络名无法使用");
			SCWIN32STATUS.put("65", "拒绝访问网络");
			SCWIN32STATUS.put("66", "网络资源类型错误");
			SCWIN32STATUS.put("67", "找不到网络名");
			SCWIN32STATUS.put("68", "已超过本地计算机网络适配器卡的名称极限");
			SCWIN32STATUS.put("69", "已超过网络   BIOS   会话的极限");
			SCWIN32STATUS.put("70", "远程服务器已经暂停或者正在启动过程中");
			SCWIN32STATUS.put("71", "由于该计算机的连接数目已达到上限，此时无法再连接到该远程计算机");
			SCWIN32STATUS.put("72", "指定的打印机或磁盘设备已经暂停");
			SCWIN32STATUS.put("80", "该文件存在");
			SCWIN32STATUS.put("82", "无法创建该目录或文件");
			SCWIN32STATUS.put("83", "INT   24　失败");
			SCWIN32STATUS.put("84", "处理该请求的存储区不可用");
			SCWIN32STATUS.put("85", "正在使用该本地设备名");
			SCWIN32STATUS.put("86", "指定的网络密码不正确");
			SCWIN32STATUS.put("87", "参数错误");
			SCWIN32STATUS.put("88", "网络出现写入错误");
			SCWIN32STATUS.put("89", "此时系统无法处理其他进程");
			SCWIN32STATUS.put("100", "无法创建其他系统标志");
			SCWIN32STATUS.put("101", "属于其他处理的专用标志");
			SCWIN32STATUS.put("102", "标志已经设置，无法关闭");
			SCWIN32STATUS.put("103", "无法再次设置该标志");
			SCWIN32STATUS.put("104", "中断时无法请求专用标志");
			SCWIN32STATUS.put("105", "此标志先前的所有权已终止");
			SCWIN32STATUS.put("106", "请将软盘插入驱动器   %1");
			SCWIN32STATUS.put("107", "后续软盘尚未插入，程序停止");
			SCWIN32STATUS.put("108", "磁盘正在使用或已由其他程序锁定");
			SCWIN32STATUS.put("109", "管道已经结束");
			SCWIN32STATUS.put("110", "系统无法打开指定的设备或文件");
			SCWIN32STATUS.put("111", "文件名太长");
			SCWIN32STATUS.put("112", "磁盘空间不足");
			SCWIN32STATUS.put("113", "没有其他可用的内部文件标识符");
			SCWIN32STATUS.put("114", "目标内部文件标识符不正确");
			SCWIN32STATUS.put("117", "该应用程序所运行的   IOCTL   调用不正确");
			SCWIN32STATUS.put("118", "校验写入的开关参数值不正确");
			SCWIN32STATUS.put("119", "系统不支持所请求的命令");
			SCWIN32STATUS.put("120", "此功能仅在   Win32   模式下有效");
			SCWIN32STATUS.put("121", "标记已超时");
			SCWIN32STATUS.put("122", "传给系统调用的数据区域太小");
			SCWIN32STATUS.put("123", "文件名、目录名或卷标语法错误");
			SCWIN32STATUS.put("124", "系统调用层不正确");
			SCWIN32STATUS.put("125", "磁盘没有卷标");
			SCWIN32STATUS.put("126", "找不到指定的模块");
			SCWIN32STATUS.put("127", "找不到指定的过程");
			SCWIN32STATUS.put("128", "没有要等候的子程序");
			SCWIN32STATUS.put("129", "%1   应用程序无法在   Win32   模式下运行");
			SCWIN32STATUS.put("130", "试图使用打开的磁盘分区的文件句柄，而不是原始磁盘   I/O  进行操");
			SCWIN32STATUS.put("131", "试图将文件指针移至文件开头之前");
			SCWIN32STATUS.put("132", "无法在指定的设备或文件中设置文件指");
			SCWIN32STATUS.put("133", "对于已包含连接驱动器的驱动器，不能使用   JOIN   或   SUBST  命令");
			SCWIN32STATUS.put("134", "试图在已经连接的驱动器上使用JOIN   或   SUBST   命令");
			SCWIN32STATUS.put("135", "试图在已经替换的驱动器上使用   JOIN   或   SUBST   命令");
			SCWIN32STATUS.put("136", "系统试图删除尚未连接的驱动器的   JOIN");
			SCWIN32STATUS.put("137", "系统试图删除尚未替换的驱动器的替换项");
			SCWIN32STATUS.put("138", "系统试图将驱动器连接到已连接的驱动器下的目录");
			SCWIN32STATUS.put("139", "系统试图将驱动器替换成已替换的驱动器下的目");
			SCWIN32STATUS.put("140", "系统试图将驱动器连接到已替换的驱动器的一个目录中");
			SCWIN32STATUS.put("141", "系统试图将驱动器   SUBST   连接到已连接的驱动器下的目录");
			SCWIN32STATUS.put("142", "此时系统无法运行   JOIN   或   SUBST");
			SCWIN32STATUS.put("143", "系统无法将驱动器连接到或替换成同一驱动器下的目录");
			SCWIN32STATUS.put("144", "此目录不是该根目录的子目录");
			SCWIN32STATUS.put("145", "该目录未清空");
			SCWIN32STATUS.put("146", "指定的路径已经在替换中使用");
			SCWIN32STATUS.put("147", "资源不足，无法执行该命令");
			SCWIN32STATUS.put("148", "此时无法使用指定的路径");
			SCWIN32STATUS.put("149", "试图合并或替换某个驱动器目录，但是该驱动器目录上一次已被替换为目标目录");
			SCWIN32STATUS.put("150", "CONFIG.SYS   文件未指定系统跟踪信息，或禁止跟踪");
			SCWIN32STATUS.put("151", "DosMuxSemWait   的指定信号事件的数目不正确");
			SCWIN32STATUS.put("152", "DosMuxSemWait   没有运行；已经设置太多的标志");
			SCWIN32STATUS.put("153", "DosMuxSemWait   列表不正确");
			SCWIN32STATUS.put("154", "输入的卷标超过目标文件系统的标号字符长度极限");
			SCWIN32STATUS.put("155", "无法创建其他线程");
			SCWIN32STATUS.put("156", "接收处理拒绝该信号");
			SCWIN32STATUS.put("157", "已经放弃该区域，因此无法锁定");
			SCWIN32STATUS.put("158", "该区域已经解除锁定");
			SCWIN32STATUS.put("159", "线程标识符的地址错误");
			SCWIN32STATUS.put("160", "传到   DosExecPgm   的参数字符串错误");
			SCWIN32STATUS.put("161", "指定的路径无效");
			SCWIN32STATUS.put("162", "信号已挂起");
			SCWIN32STATUS.put("164", "系统无法创建其他线程");
			SCWIN32STATUS.put("167", "无法锁定文件的范围");
			SCWIN32STATUS.put("170", "所要求的资源正在使用中");
			SCWIN32STATUS.put("173", "锁定请求对于提供的取消区域不重要");
			SCWIN32STATUS.put("174", "文件系统不支持到锁定类型的自动更改");
			SCWIN32STATUS.put("180", "系统检测到错误的区域号码");
			SCWIN32STATUS.put("182", "操作系统无法运行   %1");
			SCWIN32STATUS.put("183", "不能创建已经存在的文件");
			SCWIN32STATUS.put("186", "传送的标志不正确");
			SCWIN32STATUS.put("187", "找不到指定的系统信号名称");
			SCWIN32STATUS.put("188", "操作系统无法运行   %1");
			SCWIN32STATUS.put("189", "操作系统无法运行   %");
			SCWIN32STATUS.put("190", "操作系统无法运行   %1");
			SCWIN32STATUS.put("191", "无法在   Win32   模式下运行   %1");
			SCWIN32STATUS.put("192", "操作系统无法运行   %1");
			SCWIN32STATUS.put("193", "%1   不是有效的   Win32   应用程序");
			SCWIN32STATUS.put("194", "操作系统无法运行   %1");
			SCWIN32STATUS.put("195", "操作系统无法运行   %1");
			SCWIN32STATUS.put("196", "操作系统无法运行此应用程序");
			SCWIN32STATUS.put("197", "操作系统当前无法运行此应用程序");
			SCWIN32STATUS.put("198", "操作系统无法运行   %1");
			SCWIN32STATUS.put("199", "操作系统无法运行此应用程序");
			SCWIN32STATUS.put("200", "代码段应小于   64KB");
			SCWIN32STATUS.put("201", "操作系统无法运行   %1");
			SCWIN32STATUS.put("202", "操作系统无法运行   %1");
			SCWIN32STATUS.put("203", "系统找不到输入的环境选项");
			SCWIN32STATUS.put("205", "在命令子树中的进程没有信号句柄");
			SCWIN32STATUS.put("206", "文件名或扩展名太长");
			SCWIN32STATUS.put("207", "环   2   堆栈正在使用中");
			SCWIN32STATUS.put("208", "输入的全局文件名字符   *   或   ?   不正确，或指定的全局文件名字符太多");
			SCWIN32STATUS.put("209", "所发送的信号不正确");
			SCWIN32STATUS.put("210", "无法设置信号处理程序");
			SCWIN32STATUS.put("212", "区域已锁定，无法重新分配");
			SCWIN32STATUS.put("214", "附加到此程序或动态链接模块的动态链接模块太多");
			SCWIN32STATUS.put("215", "无法将调用传给加载模块");
			SCWIN32STATUS.put("230", "管道状态无效");
			SCWIN32STATUS.put("231", "所有的管道实例都处于忙状态");
			SCWIN32STATUS.put("232", "管道正在关闭");
			SCWIN32STATUS.put("233", "在管道的另一端没有进程");
			SCWIN32STATUS.put("234", "有更多可用的数据");
			SCWIN32STATUS.put("240", "已取消该会话");
			SCWIN32STATUS.put("254", "指定的扩展属性名无效");
			SCWIN32STATUS.put("255", "扩展属性不一致");
			SCWIN32STATUS.put("259", "没有其他可用数据");
			SCWIN32STATUS.put("266", "无法使用   Copy   API");
			SCWIN32STATUS.put("267", "目录名无效");
			SCWIN32STATUS.put("275", "扩展属性不匹配缓冲区");
			SCWIN32STATUS.put("276", "所装载的文件系统上的扩展属性文件已被损坏");
			SCWIN32STATUS.put("277", "扩展属性表格文件已满");
			SCWIN32STATUS.put("278", "指定的扩展属性句柄无效");
			SCWIN32STATUS.put("282", "安装的文件系统不支持扩展属性");
			SCWIN32STATUS.put("288", "试图释放不属于调用者的多路同步信号");
			SCWIN32STATUS.put("298", "信号投递的次数太多");
			SCWIN32STATUS.put("299", "仅完成部分   Read/WriteProcessMemory   请求");
			SCWIN32STATUS.put("317", "在   %2   的消息文件中，系统无法找到消息号为   0x%1   的消息");
			SCWIN32STATUS.put("487", "试图访问无效地址");
			SCWIN32STATUS.put("534", "运算结果超过   32   位");
			SCWIN32STATUS.put("535", "该管道的另一方有一进程");
			SCWIN32STATUS.put("536", "等候处理以打开管道的另一端");
			SCWIN32STATUS.put("994", "拒绝对扩展属性的访问");
			SCWIN32STATUS.put("995", "由于线程退出或应用程序的要求，I/O   操作异常终止");
			SCWIN32STATUS.put("996", "重叠的   I/O   事件不处于已标记状态");
			SCWIN32STATUS.put("997", "正在处理重叠的   I/O   操作");
			SCWIN32STATUS.put("998", "对内存位置的无效访问");
			SCWIN32STATUS.put("999", "执行页内操作出错");
			SCWIN32STATUS.put("1001", "循环太深，堆栈溢出");
			SCWIN32STATUS.put("1002", "窗口无法用来发送消息");
			SCWIN32STATUS.put("1003", "无法完成此项功能");
			SCWIN32STATUS.put("1004", "标志无效");
			SCWIN32STATUS.put("1005", "卷不包含已识别的文件系统请确认所有需要的文件系统驱动程序都已经装载，而且卷没有任何损坏");
			SCWIN32STATUS.put("1006", "某文件的卷已在外部改变，因而打开的文件不再有效");
			SCWIN32STATUS.put("1007", "要求的操作无法以全屏幕模式执行");
			SCWIN32STATUS.put("1017", "系统试图将文件装载或还原到注册表中，但是，指定的文件不是注册表文件格式");
			SCWIN32STATUS.put("1018", "试图在注册表键（已经标记为删除）中完成的操作非法");
			SCWIN32STATUS.put("1019", "系统无法在注册表日志文件中分配所需的空间");
			SCWIN32STATUS.put("1020", "无法在已经有子关键字或数值的注册表关键字中创建符号链接");
			SCWIN32STATUS.put("1021", "在易失的父键下不能创建固定的子键");
			SCWIN32STATUS.put("1022", "通知的更改请求已经完成，并且返回信息还没有被送到调用者的缓冲区中调用者需要列举所有文件以找到改动的内容");
			SCWIN32STATUS.put("1051", "已将停止控制发送给与其他运行服务相关的服务");
			SCWIN32STATUS.put("1052", "所要求的控制对此服务无效");
			SCWIN32STATUS.put("1053", "服务没有及时地响应启动或控制请求");
			SCWIN32STATUS.put("1054", "无法为该服务创建线程");
			SCWIN32STATUS.put("1055", "服务数据库已锁定");
			SCWIN32STATUS.put("1056", "该服务的实例已在运行");
			SCWIN32STATUS.put("1057", "帐号名无效或者不存在");
			SCWIN32STATUS.put("1058", "指定的服务禁用，无法启动");
			SCWIN32STATUS.put("1059", "已经指定了循环服务的从属关系");
			SCWIN32STATUS.put("1060", "指定的服务不是所安装的服务");
			SCWIN32STATUS.put("1061", "该服务此时无法接收控制消息");
			SCWIN32STATUS.put("1062", "服务尚未启动");
			SCWIN32STATUS.put("1063", "服务处理无法连接到服务控制程序");
			SCWIN32STATUS.put("1064", "处理控制请求时，服务出现意外情况");
			SCWIN32STATUS.put("1065", "指定的数据库不存在");
			SCWIN32STATUS.put("1066", "服务返回服务特定的错误码");
			SCWIN32STATUS.put("1067", "处理意外地终止");
			SCWIN32STATUS.put("1068", "无法启动从属服务或组");
			SCWIN32STATUS.put("1069", "由于登录失败，没有启动服务");
			SCWIN32STATUS.put("1070", "启动后，服务保持在启动挂起状态");
			SCWIN32STATUS.put("1071", "指定的服务数据库锁定无效");
			SCWIN32STATUS.put("1072", "指定的服务已经标记为删除");
			SCWIN32STATUS.put("1073", "指定的服务已经存在");
			SCWIN32STATUS.put("1074", "系统当前正以上一次运行成功的配置运行");
			SCWIN32STATUS.put("1075", "从属服务不存在，或已经标记为删除");
			SCWIN32STATUS.put("1076", "已经接受将当前的启动用于上一次运行成功的控制设置");
			SCWIN32STATUS.put("1077", "自从上一次启动以后，没有再次启动过该服务");
			SCWIN32STATUS.put("1078", "该名称已经用作服务名或服务显示名");
			SCWIN32STATUS.put("1100", "已经到达磁带的物理尽头");
			SCWIN32STATUS.put("1101", "磁带访问到文件标记");
			SCWIN32STATUS.put("1102", "到达磁带或分区首部");
			SCWIN32STATUS.put("1103", "磁带访问到文件组的末尾");
			SCWIN32STATUS.put("1104", "磁带上没有其他数据");
			SCWIN32STATUS.put("1105", "磁带无法分区");
			SCWIN32STATUS.put("1106", "访问多重卷分区的新磁带时，当前的区块大小错误");
			SCWIN32STATUS.put("1107", "装载磁带时，找不到磁带分区信息");
			SCWIN32STATUS.put("1108", "无法锁定媒体退出功能");
			SCWIN32STATUS.put("1109", "无法卸载媒体");
			SCWIN32STATUS.put("1110", "驱动器中的媒体已经更改");
			SCWIN32STATUS.put("1111", "已经复位I/O总线");
			SCWIN32STATUS.put("1112", "驱动器中没有媒体");
			SCWIN32STATUS.put("1113", "在目标多字节代码页中不存在对单码字符的映象");
			SCWIN32STATUS.put("1114", "动态链接库(DLL)初始化例程失败");
			SCWIN32STATUS.put("1115", "正在关闭系统");
			SCWIN32STATUS.put("1116", "正在关闭系统");
			SCWIN32STATUS.put("1117", "由于I/O设备出现错误，无法运行该请求");
			SCWIN32STATUS.put("1118", "串行设备初始化失败将卸载串行驱动程序");
			SCWIN32STATUS.put("1119", "无法打开正与其他设备共享中断请求(IRQ)的设备至少有一个使用该IRQ的设备已经打开");
			SCWIN32STATUS.put("1120", "由于再次写入串行口，串行I/O操作已结束(IOCTL_SERIAL_XOFF_COUNTER为零)");
			SCWIN32STATUS.put("1121", "由于超时，串行I/O操作已结束(IOCTL_SERIAL_XOFF_COUNTER未达到零)");
			SCWIN32STATUS.put("1122", "在软盘上找不到标识符地址标记");
			SCWIN32STATUS.put("1123", "软盘扇区标识符字段与软盘控制器磁道地址不匹配");
			SCWIN32STATUS.put("1124", "软盘控制器报告的错误，软盘驱动程序无法识别它");
			SCWIN32STATUS.put("1125", "软盘控制器返回的结果和注册的不一致");
			SCWIN32STATUS.put("1126", "访问硬盘时，再校准操作失败，再试一次后也无法操作");
			SCWIN32STATUS.put("1127", "访问硬盘时，磁盘操作失败，再试一次后仍没有作用");
			SCWIN32STATUS.put("1128", "访问硬盘时，需要重启动磁盘控制器，但仍未成功");
			SCWIN32STATUS.put("1129", "磁带已卷到尽头");
			SCWIN32STATUS.put("1130", "可用的服务器存储区不足，无法执行该命令");
			SCWIN32STATUS.put("1131", "检测到潜在的死锁情况");
			SCWIN32STATUS.put("1132", "基本地址或所指定的文件偏移量没有正确对齐");
			SCWIN32STATUS.put("1140", "试图更改系统电源状态时操作被另一应用程序或驱动程序禁止");
			SCWIN32STATUS.put("1141", "系统BIOS无法更改系统电源状态");
			SCWIN32STATUS.put("1150", "指定的程序需要新的Windows版本");
			SCWIN32STATUS.put("1151", "指定的程序不是Windows或MS-DOS程序");
			SCWIN32STATUS.put("1152", "无法启动指定程序的多个实例");
			SCWIN32STATUS.put("1153", "指定的程序是为旧版的Windows编写的");
			SCWIN32STATUS.put("1154", "运行此应用程序所需的某个库文件已损没有应用程序与该操作中所指定的文件关");
			SCWIN32STATUS.put("1155", "没有应用程序与该操作中所指定的文件关联");
			SCWIN32STATUS.put("1156", "将命令发送到应用程序时出现错误");
			SCWIN32STATUS.put("1157", "找不到运行此应用程序所需的某个库文件");
			SCWIN32STATUS.put("1200", "指定的设备名无效");
			SCWIN32STATUS.put("1201", "设备当前虽然未连接，但它是记忆连接");
			SCWIN32STATUS.put("1202", "试图记起已经记住的设备");
			SCWIN32STATUS.put("1203", "网络供应商不接受给定的网络路径");
			SCWIN32STATUS.put("1204", "指定的网络供应商名无效");
			SCWIN32STATUS.put("1205", "无法打开网络连接配置文件");
			SCWIN32STATUS.put("1206", "网络连接配置文件损坏");
			SCWIN32STATUS.put("1207", "无法列举非包容类");
			SCWIN32STATUS.put("1208", "出现扩展错误");
			SCWIN32STATUS.put("1209", "指定组名的格式无效");
			SCWIN32STATUS.put("1210", "指定计算机名的格式无效");
			SCWIN32STATUS.put("1211", "指定事件名的格式无效");
			SCWIN32STATUS.put("1212", "指定域名的格式无效");
			SCWIN32STATUS.put("1213", "指定服务名的格式无效");
			SCWIN32STATUS.put("1214", "指定网络名的格式无效");
			SCWIN32STATUS.put("1215", "指定共享名的格式无效");
			SCWIN32STATUS.put("1216", "指定密码的格式无效");
			SCWIN32STATUS.put("1217", "指定的邮件名无效");
			SCWIN32STATUS.put("1218", "指定邮件目的地的格式无效");
			SCWIN32STATUS.put("1219", "所提供的凭据与现有凭据设置冲突");
			SCWIN32STATUS.put("1220", "试图与网络服务器建立会话，但目前与该服务器建立的会话太多");
			SCWIN32STATUS.put("1221", "网络上的其他计算机已经使用该工作组或域名");
			SCWIN32STATUS.put("1222", "网络不存在或者没有启动");
			SCWIN32STATUS.put("1223", "用户已经取消该操作");
			SCWIN32STATUS.put("1224", "所要求的操作无法在已经打开用户映射区域的文件中运行");
			SCWIN32STATUS.put("1225", "远程系统拒绝网络连接");
			SCWIN32STATUS.put("1226", "已经关闭网络连接");
			SCWIN32STATUS.put("1227", "网络传输的终点已经有一个地址与其关联");
			SCWIN32STATUS.put("1228", "网络终点尚未与地址关联");
			SCWIN32STATUS.put("1229", "试图在不存在的网络连接中操作");
			SCWIN32STATUS.put("1230", "试图在活动的网络连接上进行无效操作");
			SCWIN32STATUS.put("1231", "无法传输到远程网络");
			SCWIN32STATUS.put("1232", "无法传输到远程系统");
			SCWIN32STATUS.put("1233", "远程系统不支持传输协议");
			SCWIN32STATUS.put("1234", "远程系统的目标网络端点没有运行任何服务");
			SCWIN32STATUS.put("1235", "该请求已经终止");
			SCWIN32STATUS.put("1236", "本地系统已经终止网络连接");
			SCWIN32STATUS.put("1237", "无法完成操作，请再试一次");
			SCWIN32STATUS.put("1238", "无法创建到该服务器的连接，因为已经到达了该帐号同时连接的最大数目");
			SCWIN32STATUS.put("1239", "试图在该帐号未授权的时间内登录");
			SCWIN32STATUS.put("1240", "尚未授权此帐号从该站登录网络");
			SCWIN32STATUS.put("1241", "网络地址无法用于要求的操作");
			SCWIN32STATUS.put("1242", "服务已经注册");
			SCWIN32STATUS.put("1243", "指定的服务不存在");
			SCWIN32STATUS.put("1244", "由于尚未验证用户身份，无法执行要求的操作");
			SCWIN32STATUS.put("1245", "由于用户尚未登录网络，无法运行要求的操作指定的服务不存在");
			SCWIN32STATUS.put("1246", "返回需要调用者继续工作的消息");
			SCWIN32STATUS.put("1247", "完成初始化操作后，试图再次运行初始化操作");
			SCWIN32STATUS.put("1248", "没有其他本地设备");
			SCWIN32STATUS.put("1300", "不是对所有的调用程序分配引用特权");
			SCWIN32STATUS.put("1301", "帐号名与安全标识符之间的映射未完成");
			SCWIN32STATUS.put("1302", "没有为该帐号明确地设置系统配额限制");
			SCWIN32STATUS.put("1303", "没有可用的密钥返回已知的密钥");
			SCWIN32STATUS.put("1304", "NT密码太复杂，无法转换成LANManager密码返回的LANManager密码是空字符串");
			SCWIN32STATUS.put("1305", "修订级别未知");
			SCWIN32STATUS.put("1306", "表示两个修订级别不兼容");
			SCWIN32STATUS.put("1307", "无法将此安全标识符指定为该对象的拥有者");
			SCWIN32STATUS.put("1308", "无法将此安全标识符指定为主要的对象组");
			SCWIN32STATUS.put("1309", "目前未模仿客户的线程试图在模仿符号上操作");
			SCWIN32STATUS.put("1310", "不可以禁用该组");
			SCWIN32STATUS.put("1311", "目前没有可用的登录服务器处理登录请求");
			SCWIN32STATUS.put("1312", "指定的登录会话不存在该会话可能已终止");
			SCWIN32STATUS.put("1313", "指定的权限不存在");
			SCWIN32STATUS.put("1314", "客户不保留请求的特权");
			SCWIN32STATUS.put("1315", "提供的名称不是正确的帐号名称格式");
			SCWIN32STATUS.put("1316", "指定的用户已经存在");
			SCWIN32STATUS.put("1317", "指定的用户不存在");
			SCWIN32STATUS.put("1318", "指定的组已经存在");
			SCWIN32STATUS.put("1319", "指定的组不存在");
			SCWIN32STATUS.put("1320", "或者指定的用户帐号已经是某个特定组的成员，或者也可能指定的组非空而不能被删除");
			SCWIN32STATUS.put("1321", "指定的用户帐号不是所指定组帐号的成员");
			SCWIN32STATUS.put("1322", "上次保留的管理帐号无法关闭或删除");
			SCWIN32STATUS.put("1323", "无法更新密码所输入的密码不正确");
			SCWIN32STATUS.put("1324", "无法更新密码所输入的新密码包含不可用于密码的值");
			SCWIN32STATUS.put("1325", "因为违反密码更新规则，所以无法更新密码");
			SCWIN32STATUS.put("1326", "登录失败:用户名未知或密码错误");
			SCWIN32STATUS.put("1327", "登录失败:用户帐号限制");
			SCWIN32STATUS.put("1328", "登录失败:违反帐号登录时间限制");
			SCWIN32STATUS.put("1329", "登录失败:禁止用户登录到该计算机上");
			SCWIN32STATUS.put("1330", "登录失败:指定的帐号密码已过期");
			SCWIN32STATUS.put("1331", "登录失败:当前禁用帐号");
			SCWIN32STATUS.put("1332", "未完成帐号名与安全性标识符之间的映射");
			SCWIN32STATUS.put("1333", "一次请求的本地用户标识符(LUID)太多");
			SCWIN32STATUS.put("1334", "没有其他可用的本地用户标识符(LUID)");
			SCWIN32STATUS.put("1335", "对这个特定使用来说，安全标识符的子部份是无效的");
			SCWIN32STATUS.put("1336", "访问控制清单(ACL)结构无效");
			SCWIN32STATUS.put("1337", "安全标识符结构无效");
			SCWIN32STATUS.put("1338", "安全描述符结构无效");
			SCWIN32STATUS.put("1340", "无法创建继承的访问控制列表(ACL)或访问控制项目(ACE)");
			SCWIN32STATUS.put("1341", "服务器当前不可用");
			SCWIN32STATUS.put("1342", "服务器当前可以使用");
			SCWIN32STATUS.put("1343", "所提供的值是无效的标识符授权值");
			SCWIN32STATUS.put("1344", "没有更多的内存用于更新安全信息");
			SCWIN32STATUS.put("1345", "指定的属性无效，或指定的属性与整个组的属性");
			SCWIN32STATUS.put("1359", "安全帐号数据库包含内部的不一致性错误");
			SCWIN32STATUS.put("1360", "通用的访问类型包含在访问掩码中，该掩码已经映射为非通用类型");
			SCWIN32STATUS.put("1361", "安全性描述符的格式错误（绝对或自相关）");
			SCWIN32STATUS.put("1362", "请求的操作只准登录过程使用该调用过程并未被记录为登录过程");
			SCWIN32STATUS.put("1363", "无法用已经使用的标识符来启动新的登录会话");
			SCWIN32STATUS.put("1364", "指定的确认数据包未知");
			SCWIN32STATUS.put("1365", "登录会话的状态与请求的操作不一致");
			SCWIN32STATUS.put("1366", "登录会话标识符正在使用中");
			SCWIN32STATUS.put("1367", "登录请求包含无效的登录类型值");
			SCWIN32STATUS.put("1368", "直到数据从有名管道读取之前，不能通过该管道模仿");
			SCWIN32STATUS.put("1369", "注册表子树的事务状态与所请求的操作不兼容");
			SCWIN32STATUS.put("1370", "突发的内部安全性数据库故障");
			SCWIN32STATUS.put("1371", "无法在内部帐号下运行该操作");
			SCWIN32STATUS.put("1372", "无法在该内部特定组中运行该操作");
			SCWIN32STATUS.put("1373", "无法在该内部特定用户中运行该操作");
			SCWIN32STATUS.put("1374", "因为该组当前是用户的主要组，所以不能从此组中删除用户");
			SCWIN32STATUS.put("1375", "该符号已作为主要符号使用");
			SCWIN32STATUS.put("1376", "指定的本地组不存在");
			SCWIN32STATUS.put("1377", "指定的帐号名不是本地组的成员");
			SCWIN32STATUS.put("1378", "指定的帐号名已经是本地组的成员");
			SCWIN32STATUS.put("1379", "指定的本地组已经存在");
			SCWIN32STATUS.put("1380", "登录失败:用户在本计算机上没有被授与所需注册类型");
			SCWIN32STATUS.put("1381", "超过了可以存储在单个系统中的最大机密限制");
			SCWIN32STATUS.put("1382", "机密的长度超过了最大允许值");
			SCWIN32STATUS.put("1383", "本地安全授权数据库包含内部不一致的错误");
			SCWIN32STATUS.put("1384", "登录时，用户的安全性上下文累积太多的安全标识符");
			SCWIN32STATUS.put("1385", "登录失败:用户在本计算机上没有被授与所需注册类型");
			SCWIN32STATUS.put("1386", "经交叉加密的密码必须更改用户密码");
			SCWIN32STATUS.put("1387", "新成员不存在，因此无法将其添加到本地组");
			SCWIN32STATUS.put("1388", "新成员的帐号类型有误，因此无法将其添加到本地组");
			SCWIN32STATUS.put("1389", "指定的安全标识符太多");
			SCWIN32STATUS.put("1390", "经交叉加密的密码必须更改该用户密码");
			SCWIN32STATUS.put("1391", "表示ACL没有可继承的组件");
			SCWIN32STATUS.put("1392", "文件或目录已损坏，无法读取数据");
			SCWIN32STATUS.put("1393", "磁盘结构已损坏，无法读取");
			SCWIN32STATUS.put("1394", "指定的登录会话没有用户会话密钥");
			SCWIN32STATUS.put("1395", "正在访问的服务允许特定数目的连接因为连接的数目已达到服务可接受的数目，所以此时无法创建新的服务连接");
			SCWIN32STATUS.put("1400", "窗口句柄无效");
			SCWIN32STATUS.put("1401", "菜单句柄无效");
			SCWIN32STATUS.put("1402", "光标句柄无效");
			SCWIN32STATUS.put("1403", "加速键表的句柄无效");
			SCWIN32STATUS.put("1404", "挂接句柄无效");
			SCWIN32STATUS.put("1405", "多重窗口位置结构句柄无效");
			SCWIN32STATUS.put("1406", "无法创建最上层的子窗口");
			SCWIN32STATUS.put("1407", "找不到窗口类");
			SCWIN32STATUS.put("1408", "窗口无效，属于其他线程");
			SCWIN32STATUS.put("1409", "已经注册热键");
			SCWIN32STATUS.put("1410", "类已经存在");
			SCWIN32STATUS.put("1411", "类不存在");
			SCWIN32STATUS.put("1412", "类窗口仍打开着");
			SCWIN32STATUS.put("1413", "索引无效");
			SCWIN32STATUS.put("1414", "图标句柄无效");
			SCWIN32STATUS.put("1415", "使用私人对话框窗口字");
			SCWIN32STATUS.put("1416", "找不到列表框标识符");
			SCWIN32STATUS.put("1417", "找不到任何通配符");
			SCWIN32STATUS.put("1418", "线程没有打开剪贴板");
			SCWIN32STATUS.put("1419", "尚未注册热键");
			SCWIN32STATUS.put("1420", "该窗口不是有效的对话框窗口");
			SCWIN32STATUS.put("1421", "找不到控制标识符");
			SCWIN32STATUS.put("1422", "由于没有编辑控制，因此该组合框的消息无效");
			SCWIN32STATUS.put("1423", "窗口不是组合框");
			SCWIN32STATUS.put("1424", "高度必须小于256");
			SCWIN32STATUS.put("1425", "设备上下文(DC)句柄无效");
			SCWIN32STATUS.put("1426", "挂接过程类型无效");
			SCWIN32STATUS.put("1427", "挂接过程无效");
			SCWIN32STATUS.put("1428", "不能在无模块句柄的情况下设置非本地的挂接");
			SCWIN32STATUS.put("1429", "只能全局设置该挂接过程");
			SCWIN32STATUS.put("1430", "已安装日记挂接过程");
			SCWIN32STATUS.put("1431", "未安装挂接过程");
			SCWIN32STATUS.put("1432", "单选列表框的消息无效");
			SCWIN32STATUS.put("1433", "LB_SETCOUNT发送到活动的列表框");
			SCWIN32STATUS.put("1434", "该列表框不支持制表符");
			SCWIN32STATUS.put("1435", "无法破坏由其他线程所创建的对象");
			SCWIN32STATUS.put("1436", "子窗口不能有菜单");
			SCWIN32STATUS.put("1437", "窗口没有系统菜单");
			SCWIN32STATUS.put("1438", "消息框样式无效");
			SCWIN32STATUS.put("1439", "系统范围内的(SPI_*)的参数无效");
			SCWIN32STATUS.put("1440", "屏幕已经锁定");
			SCWIN32STATUS.put("1441", "多重窗口位置结构中所有窗口句柄必须具有相同的父窗口");
			SCWIN32STATUS.put("1442", "窗口不是子窗口");
			SCWIN32STATUS.put("1443", "GW_*命令无效");
			SCWIN32STATUS.put("1444", "线程标识符无效");
			SCWIN32STATUS.put("1445", "无法处理非多文档接口(MDI)窗口的消息");
			SCWIN32STATUS.put("1446", "弹出式菜单已激活");
			SCWIN32STATUS.put("1447", "窗口没有滚动条");
			SCWIN32STATUS.put("1448", "滚动条范围应小于0x7FFF");
			SCWIN32STATUS.put("1449", "无法以指定的方式显示或关闭窗口");
			SCWIN32STATUS.put("1450", "系统资源不足，无法完成所请求的服务");
			SCWIN32STATUS.put("1451", "系统资源不足，无法完成所请求的服务");
			SCWIN32STATUS.put("1452", "系统资源不足，无法完成所请求的服务");
			SCWIN32STATUS.put("1453", "配额不足，无法完成所请求的服务");
			SCWIN32STATUS.put("1454", "系统配额不够，无法完成申请的服务");
			SCWIN32STATUS.put("1455", "页面交换文件太小，无法完成此项操作");
			SCWIN32STATUS.put("1456", "找不到菜单项");
			SCWIN32STATUS.put("1500", "事件日志文件损坏");
			SCWIN32STATUS.put("1501", "无法打开事件日志文件，因此无法启动事件记录服务");
			SCWIN32STATUS.put("1502", "事件日志文件已满");
			SCWIN32STATUS.put("1503", "事件日志文件在两次读取时已发生变化");
			SCWIN32STATUS.put("1700", "串绑定无效");
			SCWIN32STATUS.put("1701", "绑定句柄的类型错误");
			SCWIN32STATUS.put("1702", "绑定句柄无效");
			SCWIN32STATUS.put("1703", "不支持   RPC   协议顺序");
			SCWIN32STATUS.put("1704", "RPC   协议序列无效");
			SCWIN32STATUS.put("1705", "字符串的全球唯一标识符(UUID)无效");
			SCWIN32STATUS.put("1706", "终点的格式无效");
			SCWIN32STATUS.put("1707", "网络地址无效");
			SCWIN32STATUS.put("1708", "未找到终点");
			SCWIN32STATUS.put("1709", "超时设置值无效");
			SCWIN32STATUS.put("1710", "找不到该对象的全球唯一标识符(UUID)");
			SCWIN32STATUS.put("1711", "该对象的全球唯一标识符(UUID)已经注册");
			SCWIN32STATUS.put("1712", "这一类型的全球唯一标识符(UUID)已经注册");
			SCWIN32STATUS.put("1713", "RPC   服务器正在监听");
			SCWIN32STATUS.put("1714", "尚未注册协议顺序");
			SCWIN32STATUS.put("1715", "RPC   服务器不处于监听状态");
			SCWIN32STATUS.put("1716", "管理程序的类型未知");
			SCWIN32STATUS.put("1717", "接口未知");
			SCWIN32STATUS.put("1718", "没有绑定");
			SCWIN32STATUS.put("1719", "没有协议序列");
			SCWIN32STATUS.put("1720", "无法创建终点");
			SCWIN32STATUS.put("1721", "资源不足，无法完成该操作");
			SCWIN32STATUS.put("1722", "RPC   服务器无法使用");
			SCWIN32STATUS.put("1723", "RPC   服务器太忙，无法完成此项操作");
			SCWIN32STATUS.put("1724", "网络选项无效");
			SCWIN32STATUS.put("1725", "该线程中不存在活动的远程过程调用");
			SCWIN32STATUS.put("1726", "远程过程调用失败");
			SCWIN32STATUS.put("1727", "远程过程调用失败并且无法执行");
			SCWIN32STATUS.put("1728", "远程过程调用(RPC)协议出现错误");
			SCWIN32STATUS.put("1730", "RPC   服务器不支持传输语法");
			SCWIN32STATUS.put("1732", "不支持这种类型的全球唯一标识符");
			SCWIN32STATUS.put("1733", "标识无效");
			SCWIN32STATUS.put("1734", "数组边界无效");
			SCWIN32STATUS.put("1735", "绑定类型中不包含项目名");
			SCWIN32STATUS.put("1736", "名称语法无效");
			SCWIN32STATUS.put("1737", "不支持这种命名语法");
			SCWIN32STATUS.put("1739", "没有可用的网络地址，无法创建全球唯一标识符(UUID)");
			SCWIN32STATUS.put("1740", "终结点重复");
			SCWIN32STATUS.put("1741", "身份验证类型未知");
			SCWIN32STATUS.put("1742", "调用次数的上限太小");
			SCWIN32STATUS.put("1743", "字符串太长");
			SCWIN32STATUS.put("1744", "找不到   RPC   协议序列");
			SCWIN32STATUS.put("1745", "过程号超出范围");
			SCWIN32STATUS.put("1746", "此次绑定不包含任何身份验证信息");
			SCWIN32STATUS.put("1747", "身份验证服务未知");
			SCWIN32STATUS.put("1748", "身份验证级别未知");
			SCWIN32STATUS.put("1749", "安全描述符无效");
			SCWIN32STATUS.put("1750", "身份验证服务未知");
			SCWIN32STATUS.put("1751", "项目无效");
			SCWIN32STATUS.put("1752", "服务器的终结点无法执行此项操作");
			SCWIN32STATUS.put("1753", "终点的映射器没有更多的终点可用");
			SCWIN32STATUS.put("1754", "没有导出任何接口");
			SCWIN32STATUS.put("1755", "项目名不完整");
			SCWIN32STATUS.put("1756", "版本选项无效");
			SCWIN32STATUS.put("1757", "没有其他成员");
			SCWIN32STATUS.put("1758", "可以导出全部内容");
			SCWIN32STATUS.put("1759", "未找到接口");
			SCWIN32STATUS.put("1760", "项目已经存在");
			SCWIN32STATUS.put("1761", "项目找不到");
			SCWIN32STATUS.put("1762", "名称服务不可用");
			SCWIN32STATUS.put("1763", "网络地址集无效");
			SCWIN32STATUS.put("1764", "不支持请求的操作");
			SCWIN32STATUS.put("1765", "没有可供冒仿的安全性描述符");
			SCWIN32STATUS.put("1766", "远程过程调用(RPC)出现内部错误");
			SCWIN32STATUS.put("1767", "RPC 服务器企图进行整除零运算");
			SCWIN32STATUS.put("1768", "RPC 服务器出现寻址错误");
			SCWIN32STATUS.put("1769", "RPC 服务器中的浮点运算产生除零错");
			SCWIN32STATUS.put("1770", "RPC 服务器产生了浮点下溢错误");
			SCWIN32STATUS.put("1771", "RPC 服务器产生了浮点上溢错误");
			SCWIN32STATUS.put("1772", "可用于自动句柄绑定的   RPC   服务器列表已经用完");
			SCWIN32STATUS.put("1773", "无法打开字符转换表文件");
			SCWIN32STATUS.put("1774", "包含字符转换表的文件小于512   个字节");
			SCWIN32STATUS.put("1775", "在远程过程调用中，客户机向主机传送了一个空的描述体句柄");
			SCWIN32STATUS.put("1777", "远程过程调用中的描述体句柄发生变化");
			SCWIN32STATUS.put("1778", "发送到远程过程调用的绑定句柄不匹配");
			SCWIN32STATUS.put("1779", "占位程序无法获得远程过程调用的句柄");
			SCWIN32STATUS.put("1780", "将空的参考指针发送给占位程序");
			SCWIN32STATUS.put("1781", "列举值超出范围");
			SCWIN32STATUS.put("1782", "字节数目太小");
			SCWIN32STATUS.put("1783", "占位程序接收到错误数据");
			SCWIN32STATUS.put("1784", "所提供的用户缓冲区对所申请的操作无效");
			SCWIN32STATUS.put("1785", "无法识别磁盘媒体。它可能还未格式化");
			SCWIN32STATUS.put("1786", "工作站没有委托密码");
			SCWIN32STATUS.put("1787", "Windows NT 服务器上的 SAM 数据库没有该工作站委托关系的计算机帐号");
			SCWIN32STATUS.put("1788", "建立主域和受托域间的委托关系失败");
			SCWIN32STATUS.put("1788", "建立工作站和主域间的委托关系失败");
			SCWIN32STATUS.put("1790", "网络登录失败");
			SCWIN32STATUS.put("1791", "该线程执行过程中已经进行了远程过程调用");
			SCWIN32STATUS.put("1792", "试图登录网络，但网络登录服务尚未启动");
			SCWIN32STATUS.put("1793", "用户帐号已到期");
			SCWIN32STATUS.put("1794", "重定向程序正在使用，无法卸载");
			SCWIN32STATUS.put("1795", "已经安装所指定的打印机驱动程序");
			SCWIN32STATUS.put("1796", "指定的端口未知");
			SCWIN32STATUS.put("1797", "打印机驱动程序未知");
			SCWIN32STATUS.put("1798", "打印处理程序未知");
			SCWIN32STATUS.put("1799", "指定的分隔符文件无效");
			SCWIN32STATUS.put("1800", "指定的优先级无效");
			SCWIN32STATUS.put("1801", "打印机名无效");
			SCWIN32STATUS.put("1802", "打印机已经存在");
			SCWIN32STATUS.put("1803", "打印机命令无效");
			SCWIN32STATUS.put("1804", "指定的数据类型无效");
			SCWIN32STATUS.put("1805", "指定的环境");

		}
	  
}
