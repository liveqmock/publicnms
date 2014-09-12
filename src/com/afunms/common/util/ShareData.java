package com.afunms.common.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.application.model.MQConfig;
import com.afunms.application.util.RemoteClientInfo;
import com.afunms.common.util.threads.ThreadPool;
import com.afunms.polling.om.VMWareConnectConfig;
import com.afunms.polling.task.MonitorTimer;


public class ShareData {

  //public static int classCount = 0;  // shared by all instances
  public static int count = 0;              // separate for each servlet
  public static int firstipmac =0;
  public static String flag;
  public static String controlVoice="1";
  //public static Hashtable instances = new Hashtable();  // also shared
  public static Hashtable sharedata = new Hashtable();  // also shared
  public static Hashtable bussinessdata = new Hashtable();// also shared
  public static Hashtable pingdata = new Hashtable();  // also shared
  public static Hashtable iispingdata = new Hashtable();  // also shared
  public static Hashtable portdata = new Hashtable();  // all port data
  public static Hashtable hostdata = new Hashtable();  // all host and network
  public static Hashtable GSNdata = new Hashtable();  //浙江移动专有设备
  public static Hashtable vmdata = new Hashtable();//vmware
  public static Hashtable emcdata = new Hashtable(); //emc存储
  //主机短信息
  public static Hashtable memdata = new Hashtable();  // 主机的内存、磁盘信息的短信事件
  public static Hashtable sendeddata = new Hashtable();  // all host and network
  public static Hashtable createeventdata = new Hashtable();  // all host and network
  public static Hashtable portsendeddata = new Hashtable();  // all host and network
  public static Hashtable memsendeddata = new Hashtable();  // all host and network
  public static Hashtable procsendeddata = new Hashtable();  // 主机的PROCS进程丢失的短信事件
  public static Hashtable filesizedata = new Hashtable();  // all host and network
  public static Hashtable allpingdata = new Hashtable();  // all host and network
  public static Hashtable alldbdata = new Hashtable();  // all database
  public static Hashtable allportdata = new Hashtable();  // all host and network
  public static Hashtable packsdata = new Hashtable();  // all host and network
  public static Hashtable discardsdata = new Hashtable();  // all host and network
  public static Hashtable errorsdata = new Hashtable();  // all host and network
  public static Hashtable octetsdata = new Hashtable();  // 网络字节
  public static Hashtable realoctetsdata = new Hashtable();  // 网络字节
  public static Hashtable pksdata = new Hashtable();  // all host and network
  public static Hashtable eventdata = new Hashtable();  // all host and network
  public static Hashtable sameequipsmsdata = new Hashtable();  // all host and network
  //节点地址PING值
  public static Hashtable relateippingdata = new Hashtable();  // all host and network
  //ipmac
  public static Hashtable relateipmacdata = new Hashtable(); 
  public static Hashtable relatefdbipmacdata = new Hashtable(); 
  public static Hashtable ipmacbanddata = new Hashtable();  
  public static Hashtable fdbipmacbanddata = new Hashtable();
  public static Hashtable iprouterdata = new Hashtable();  // all host and network
  public static Hashtable policydata = new Hashtable();  // all host and network
  public static Hashtable tosroutedata = new Hashtable();  // all host and network
  public static Hashtable dominodata = new Hashtable();  // all host and network
  public static Hashtable iisdata = new Hashtable();  // 存储IIS采集的数据信息
  public static Hashtable sysbasedata = new Hashtable();  // 存储SYSBASE采集的数据信息
  public static Hashtable lostprocdata = new Hashtable();  // 存储已经丢失的进程信息
  public static Hashtable sqlserverdata = new Hashtable();  // 存储sqlserver采集的数据信息
  public static Hashtable mqdata = new Hashtable();  // 存储mq采集的数据信息
  public static Hashtable weblogicdata = new Hashtable();  // 存储weblogic采集的数据信息
  public static Hashtable dhcpdata = new Hashtable();  // 存储DHCP采集的数据信息
  public static Hashtable tomcatdata = new Hashtable();  // 存储tomcat采集的数据信息
  public static Hashtable resindata = new Hashtable();  // 存储resin采集的数据信息
  public static Hashtable ibmstoragedata = new Hashtable();  // 存储IBMStorage采集的数据信息
  public static Hashtable alldiskalarmdata = new Hashtable();  // 存储DISK是否告警数据信息
  public static Hashtable allmqalarmdata = new Hashtable();  // 存储MQ是否告警数据信息
  public static Hashtable allwlserveralarmdata = new Hashtable();  // 存储websphere是否告警数据信息
  public static Hashtable alldb2data = new Hashtable();  // 存储DB2采集的数据信息
  public static Hashtable alloracledata = new Hashtable();  // 存储ORACLE采集的数据信息
  public static Hashtable oraspacedata = new Hashtable();  // 存储ORACLE表空间采集的数据信息
  public static Hashtable alloraspacealarmdata = new Hashtable();  // 存储ORASPACE是否告警数据信息
  
  public static Hashtable informixspacedata = new Hashtable();  // 存储INFORMIX表空间采集的数据信息
  public static Hashtable allinformixspacealarmdata = new Hashtable();  // 存储INFORMIXSPACE是否告警数据信息
  
  public static Hashtable db2type6spacedata = new Hashtable();  // 存储ORASPACE是否告警数据信息
  public static Hashtable wasdata = new Hashtable();  // 存储Was采集的数据信息
  public static Hashtable sqldbdata = new Hashtable();  // 存储sqldb采集的数据信息
  //public static Hashtable sqldbdata = new Hashtable();  // 存储sqldb采集的数据信息
  public static Hashtable emersondata = new Hashtable();  // 存储空调采集的数据信息
  public static Hashtable allsyslogconfdata = new Hashtable();  // 存储空调采集的数据信息
  public static Hashtable alllegatodata = new Hashtable();  // 存储空调采集的数据信息
  public static Hashtable alltuxedodata = new Hashtable();  // 存储空调采集的数据信息
  
  public static Hashtable mysqldata = new Hashtable();  // 存储mysql采集的数据信息
  public static Hashtable mysqlmonitordata = new Hashtable();  // 存储mysql采集的数据信息
  
  
  
  public static Hashtable allstoragedata = new Hashtable();  // 存储采集的数据信息   nielin add 2010-06-28
  
  public static Hashtable checkeventdata = new Hashtable(); //存储指标对比后告警的信息
  public static Hashtable subscribeReportHash = new Hashtable();//存储报表订阅所用的TimerTask，key为nms_cycle_report_config表id，value为存储TimerTask的List
  private static ThreadPool netThreadPool;//存放线程池
  
  private static Hashtable allLinkData = new Hashtable();//链路信息  HONGLI 2011-04-13
  private static Hashtable allLinknodeInterfaceData = new Hashtable();//链路节点接口数据集合 HONGLI 2011-04-13
  private static String isStopMachine = "1";//是否继续应急关机判断
  

  
  
  
  /**
   * 存储所有从数据库中取的（网络设备或者服务器）的部分数据  
   * <p>可参见NetworkDao.java collectAllNetworkData方法 用于更新拓扑图UpdateXmlTask时 存储数据库表的实时数据</p>
   * @author HONGLI 2011-04-19
   */
  private static Hashtable allNetworkData = new Hashtable();  
  
  /**
   * 存储所有从数据库中取的（网络设备或者服务器）的连通率数据  
   */
  private static Hashtable allNetworkPingData = new Hashtable();
  
  public static Hashtable connectConfigHashtable = new Hashtable(); //pingsnmp  
  
  public static Hashtable portConfigHash = new Hashtable(); //存放端口配置
  
  public static Hashtable alarmportConfigHash = new Hashtable(); //存放单个端口流速告警配置
  
  public static Hashtable gatherHash = new Hashtable(); //存放端口配置
  
  public static Hashtable checkEventHash = new Hashtable(); //存放端口配置
  
  public static Hashtable paramsHash = new Hashtable(); //TELNET配置
  
  public static Hashtable aclHash = new Hashtable(); //存放ACL匹配值
  public static Hashtable slaHash = new Hashtable(); //存放SLA匹配值
  public static Hashtable oracleGIHash = new Hashtable(); //存放ORACLE采集指标值
  public static List oracleSIDList = new ArrayList(); //存放ORACLE SID值
  public static List dbList = new ArrayList(); //存放DB值
  
  public static Hashtable weblogin = new Hashtable();//虚拟登陆
  
  public static Hashtable roomAlarmHashtable=new Hashtable();
  
  public static Hashtable getBusinessHash() {
	return businessHash;
}

public static void setBusinessHash(Hashtable businessHash) {
	ShareData.businessHash = businessHash;
}

public static Hashtable businessHash = new Hashtable();//存放业务对象
  
  
  /**
   * 所有采集的DNS数据信息
   */
  private static Hashtable allDnsData = new Hashtable();
  

  /**
   * 存放每5分钟采集的Timer集合
   * String:nodeid
   * MonitorTimer:该node对应的Timer
   */
  private static HashMap<String,MonitorTimer> m5TimerMap = new HashMap<String, MonitorTimer>();
  
  /**
   * 存放每5分钟采集到的数据集合
   */
  private static Hashtable m5Alldata = new Hashtable();
  
  /**
   * 存放每5分钟采集的任务中，已经采集的数量
   */
  private static int m5CollectedSize;
  
  /**
   * 存放每10分钟采集的Timer集合
   * String:nodeid
   * MonitorTimer:该node对应的Timer
   */
  private static HashMap<String,MonitorTimer> m10TimerMap = new HashMap<String, MonitorTimer>();
  
  /**
   * 存放每10分钟采集到的数据集合
   */
  private static Hashtable m10Alldata = new Hashtable();
  
  /**
   * 存放每10分钟采集的任务中，已经采集的数量
   */
  private static int m10CollectedSize;
  
  /**
   * 存放每30分钟采集的Timer集合
   * String:nodeid
   * MonitorTimer:该node对应的Timer
   */
  private static HashMap<String,MonitorTimer> m30TimerMap = new HashMap<String, MonitorTimer>();
  
  /**
   * 存放每30分钟采集到的数据集合
   */
  private static Hashtable m30Alldata = new Hashtable();
  
  /**
   * 存放每30分钟采集的任务中，已经采集的数量
   */
  private static int m30CollectedSize;
  
  /**
   * 存放每1小时采集的Timer集合
   * String:nodeid
   * MonitorTimer:该node对应的Timer
   */
  private static HashMap<String,MonitorTimer> h1TimerMap = new HashMap<String, MonitorTimer>();
  
  /**
   * 存放每小时采集到的数据集合
   */
  private static Hashtable h1Alldata = new Hashtable();
  
  /**
   * 存放每小时采集的任务中，已经采集的数量
   */
  private static int h1CollectedSize;
  
  /**
   * 存放每天采集的Timer集合
   * String:nodeid
   * MonitorTimer:该node对应的Timer
   */
  private static HashMap<String,MonitorTimer> d1TimerMap = new HashMap<String, MonitorTimer>();
  
  /**
   * 存放每天采集到的数据集合
   */
  private static Hashtable d1Alldata = new Hashtable();
  
  /**
   * 存放每天采集的任务中，已经采集的数量
   */
  private static int d1CollectedSize;
  
  
  
  /**
   * 存放每5分钟用SNMP采集方式服务器采集的Timer集合
   * String:nodeid
   * MonitorTimer:该node对应的Timer
   */
  private static HashMap<String,MonitorTimer> m5HostTimerMap = new HashMap<String, MonitorTimer>();
  
  /**
   * 存放服务器每5分钟服务器采集到的数据集合
   */
  private static Hashtable m5HostAlldata = new Hashtable();
  
  /**
   * 存放每5分钟服务器采集的任务中，已经采集的数量
   */
  private static int m5HostCollectedSize; 
  
  
  /**
   * 存放每10分钟用SNMP采集方式服务器采集的Timer集合
   * String:nodeid
   * MonitorTimer:该node对应的Timer
   */
  private static HashMap<String,MonitorTimer> m10HostTimerMap = new HashMap<String, MonitorTimer>();
  
  /**
   * 存放服务器每10分钟服务器采集到的数据集合
   */
  private static Hashtable m10HostAlldata = new Hashtable();
  
  /**
   * 存放每10分钟服务器采集的任务中，已经采集的数量
   */
  private static int m10HostCollectedSize; 
  
  /**
   * 存放每30分钟用SNMP采集方式服务器采集的Timer集合
   * String:nodeid
   * MonitorTimer:该node对应的Timer
   */
  private static HashMap<String,MonitorTimer> m30HostTimerMap = new HashMap<String, MonitorTimer>();
  
  /**
   * 存放服务器每30分钟服务器采集到的数据集合
   */
  private static Hashtable m30HostAlldata = new Hashtable();
  
  /**
   * 存放每30分钟服务器采集的任务中，已经采集的数量
   */
  private static int m30HostCollectedSize; 
  
  /**
   * 存放每1小时用SNMP采集方式服务器采集的Timer集合
   * String:nodeid
   * MonitorTimer:该node对应的Timer
   */
  private static HashMap<String,MonitorTimer> h1HostTimerMap = new HashMap<String, MonitorTimer>();
  
  /**
   * 存放服务器每1小时服务器采集到的数据集合
   */
  private static Hashtable h1HostAlldata = new Hashtable();
  
  /**
   * 存放每小时服务器采集的任务中，已经采集的数量
   */
  private static int h1HostCollectedSize; 
  
  /**
   * 存放每1天用SNMP采集方式服务器采集的Timer集合
   * String:nodeid
   * MonitorTimer:该node对应的Timer
   */
  private static HashMap<String,MonitorTimer> d1HostTimerMap = new HashMap<String, MonitorTimer>();
  
  /**
   * 存放服务器每1天服务器采集到的数据集合
   */
  private static Hashtable d1HostAlldata = new Hashtable();
  
  /**
   * 存放每天服务器采集的任务中，已经采集的数量
   */
  private static int d1HostCollectedSize; 
  
  /**
   * 存放检查链路采集的Timer集合
   * String:linkid
   * MonitorTimer:该node对应的Timer
   */
  private static HashMap<String,MonitorTimer> linkTimerMap = new HashMap<String, MonitorTimer>();
  
  /**
   * 存放每链路采集到的数据集合
   */
  private static Hashtable linkAlldata = new Hashtable();
  
  /**
   * 存放链路采集的任务中，已经采集的数量
   */
  private static int linkCollectedSize; 
  
  /**
   * 存放面板采集的Timer集合
   * String:linkid
   * MonitorTimer:该node对应的Timer
   */
  private static HashMap<String,MonitorTimer> panelTimerMap = new HashMap<String, MonitorTimer>();
  /**
   * 存放面板采集的任务中，已经采集的数量
   */
  private static int panelCollectedSize;
  
  /**
   * 存放设备更新的Timer集合
   * Host 
   * MonitorTimer:该node对应的Timer
   */
  private static HashMap<String,MonitorTimer> pollTimerMap = new HashMap<String, MonitorTimer>();
  /**
   * 存放设备更新的任务中，已经采集的数量
   */
  private static int pollCollectedSize; 
  
  /**
   * 存放XML更新的Timer集合
   * Host 
   * MonitorTimer:该node对应的Timer
   */
  private static HashMap<String,MonitorTimer> xmlTimerMap = new HashMap<String, MonitorTimer>();
  /**
   * 存放XML更新的任务中，已经采集的数量
   */
  private static int xmlCollectedSize; 
  
  private static Hashtable ipaliasNDP = new Hashtable();
  public static void setIpaliasNDP(String ipmac,List ipaliasList ){
	  ipaliasNDP.put(ipmac, ipaliasList);
  }
  public static Hashtable getIpaliasNDP(){
	  return ipaliasNDP;
  }
  
  
  
  private static Hashtable macNDP = new Hashtable();
  public static void setMacNDP(String ipmac,List macList ){
	  macNDP.put(ipmac, macList);
  }
  public static Hashtable getMacNDP(){
	  return macNDP;
  }
  
  private static Hashtable ifEntityNDP = new Hashtable();
  public static void setIfEntityNDP(String ipmac,List ifList ){
	  ifEntityNDP.put(ipmac, ifList);
  }
  public static Hashtable getIfEntityNDP(){
	  return ifEntityNDP;
  }
  
  
  /**
   * 存放每5分钟用代理采集方式服务器采集的Timer集合
   * String:nodeid
   * MonitorTimer:该node对应的Timer
   */
  private static HashMap<String,MonitorTimer> m5AgentHostTimerMap = new HashMap<String, MonitorTimer>();
  
  /**
   * 存放服务器每5分钟AGENT服务器采集到的数据集合
   */
  private static Hashtable m5AgentHostAlldata = new Hashtable();
  
  /**
   * 存放每5分钟服务器采集的任务中，已经采集的数量
   */
  private static int m5AgentHostCollectedSize; 
  private static Hashtable<String,RemoteClientInfo> ip_clientInfoHash = new Hashtable<String,RemoteClientInfo>();
  
  private static Hashtable timegatherhash;//存放按时间段采集的配置
  public static Hashtable getTimegatherhash() { 
		return timegatherhash;
	}

	public static void setTimegatherhash(Hashtable _timegatherhash) {
		timegatherhash = _timegatherhash;
	} 
	
	private static Hashtable allipaccountipbases;//存放所有IP计数基础信息
  	public static Hashtable getAllipaccountipbases() { 
		return allipaccountipbases;
	}

	public static void setAllipaccountipbases(Hashtable _allipaccountipbases) {
		allipaccountipbases = _allipaccountipbases;
	}
  
	private static Hashtable allportconfigs;//存放所有端口配置信息
  	public static Hashtable getAllportconfigs() { 
		return allportconfigs;
	}

	public static void setAllportconfigs(Hashtable _allportconfigs) {
		allportconfigs = _allportconfigs;
	}
	
	private static Hashtable allportconfigsbyIP;//存放按IP对应的端口配置信息
  	public static Hashtable getAllportconfigsbyIP() { 
		return allportconfigsbyIP;
	}

	public static void setAllportconfigsbyIP(Hashtable _allportconfigsbyIP) {
		allportconfigsbyIP = _allportconfigsbyIP;
	}
 
	private static Hashtable allurls;//存放所有WEB访问配置信息
	public static Hashtable getAllurls() { 
		return allurls;
	}

	public static void setAllurls(Hashtable _allurls) {
		allurls = _allurls;
	}
	
	private static List allbussness;//存放所有业务信息
	public static List getAllbussness() { 
		return allbussness;
	}

	public static void setAllbussness(List _allbussness) {
		allbussness = _allbussness;
	}
		
	private static Hashtable allhintlinks;//存放所有hintlink访问配置信息
	public static Hashtable getAllhintlinks() { 
		return allhintlinks;
	}

	public static void setAllhintlinks(Hashtable _allhintlinks) {
		allhintlinks = _allhintlinks;
	}
	
	private static Hashtable allalarmindicators;//存放所有告警指标
	public static Hashtable getAllalarmindicators() { 
		return allalarmindicators;
	}

	public static void setAllalarmindicators(Hashtable _allalarmindicators) {
		allalarmindicators = _allalarmindicators;
	}
	private static Hashtable toprelation;//存放所有指标与拓扑图关联对象
	public static Hashtable getToprelation() { 
		return toprelation;
	}

	public static void setToprelation(Hashtable _toprelation) {
		toprelation = _toprelation;
	}
	
	private static Hashtable allnodeequps;//存放所有设备图片访问配置信息
	public static Hashtable getAllnodeequps() { 
		return allnodeequps;
	}

	public static void setAllnodeequps(Hashtable _allnodeequps) {
		allnodeequps = _allnodeequps;
	}
	
	private static Hashtable managexmlhash;//存放所有topu信息
	public static Hashtable getManagexmlhash() { 
		return managexmlhash;
	}

	public static void setManagexmlhash(Hashtable _managexmlhash) {
		managexmlhash = _managexmlhash;
	}
	private static Hashtable managexmlhashtable;//存放所有topu信息
	public static Hashtable getManagexmlhashtable() { 
		return managexmlhashtable;
	}

	public static void setManagexmlhashtable(Hashtable _managexmlhashtable) {
		managexmlhashtable = _managexmlhashtable;
	}
	private static Hashtable relationhash;//
	public static Hashtable getRelationhash() { 
		return relationhash;
	}
	public static void setRelationhash(Hashtable _relationhash) {
		relationhash = _relationhash;
	}
	
	private static Hashtable relationhashtable;//
	public static Hashtable getRelationhashtable() { 
		return relationhashtable;
	}
	public static void setRelationhashtable(Hashtable _relationhashtable) {
		relationhashtable = _relationhashtable;
	}
	
	private static Hashtable allntp;//存放ntp信息
	public static Hashtable getAllntp() { 
		return allntp;
	}

	public static void setAllntp(Hashtable _allntp) {
		allntp = _allntp;
	}
	
	private static Hashtable nodehash;//
	public static Hashtable getNodehash() { 
		return nodehash;
	}
	public static void setNodehash(Hashtable _nodehash) {
		nodehash = _nodehash;
	}
	
	private static List weblogiclist;//
	public static List getWeblogiclist() { 
		return weblogiclist;
	}
	public static void setWeblogiclist(List _weblogiclist) {
		weblogiclist = _weblogiclist;
	}
	
	private static List dhcplist;//
	public static List getDhcplist() { 
		return dhcplist;
	}
	public static void setDhcplist(List _dhcplist) {
		dhcplist = _dhcplist;
	}
	
	private static List webconfiglist;//
	public static List getWebconfiglist() { 
		return webconfiglist;
	}
	public static void setWebconfiglist(List _webconfiglist) {
		webconfiglist = _webconfiglist;
	}
	
	private static List webloginlist;
	
	private static List ftplist;//
	public static List getFtplist() { 
		return ftplist;
	}
	public static void setFtplist(List _ftplist) {
		ftplist = _ftplist;
	}
	
	private static List tftplist;//
	public static List getTftplist() {
		return tftplist;
	}
	
	public static void setTftplist(List tftplist) {
		ShareData.tftplist = tftplist;
	}
	
	private static List emaillist;//

	public static List getEmaillist() { 
		return emaillist;
	}
	public static void setEmaillist(List _emaillist) {
		emaillist = _emaillist;
	}
	
	private static List waslist;//
	public static List getWaslist() { 
		return waslist;
	}
	public static void setWaslist(List _waslist) {
		waslist = _waslist;
	}
	
	private static List mqlist;//
	public static List getMqlist() { 
		return mqlist;
	}
	public static void setMqlist(List _mqlist) {
		mqlist = _mqlist;
	}
	
	private static List dpconfiglist;//
	public static List getDpconfiglist() { 
		return dpconfiglist;
	}
	public static void setDpconfiglist(List _dpconfiglist) {
		dpconfiglist = _dpconfiglist;
	}
	
	private static Hashtable alldps;//存放Dp信息
	public static Hashtable getAlldps() { 
		return alldps;
	}

	public static void setAlldps(Hashtable _alldps) {
		alldps = _alldps;
	}
	
	private static Hashtable allnas;//存放nas信息
	public static Hashtable getAllnas() { 
		return allnas;
	}

	public static void setAllnas(Hashtable _allnas) {
		allnas = _allnas;
	}
	
	private static List cicslist;//
	public static List getCicslist() { 
		return cicslist;
	}
	public static void setCicslist(List _cicslist) {
		cicslist = _cicslist;
	}
	
	private static List ggsciconfiglist;//ggsci
	public static List getGgsciconfiglist() { 
		return ggsciconfiglist;
	}
	public static void setGgsciconfiglist(List _ggsciconfiglist) {
		ggsciconfiglist = _ggsciconfiglist;
	}
	
	public static void setAllggsci(Hashtable _allggsci) {
		allnas = _allggsci;
	}
	
	private static Hashtable allggsci;//存放ggsci信息
	public static Hashtable getAllggsci() { 
		return allggsci;
	}
	
	private static List nasconfiglist;//nas
	public static List getNasconfiglist() { 
		return nasconfiglist;
	}
	public static void setNasconfiglist(List _nasconfiglist) {
		nasconfiglist = _nasconfiglist;
	}
	
	private static List tomcatlist;//
	public static List getTomcatlist() { 
		return tomcatlist;
	}
	public static void setTomcatlist(List _tomcatlist) {
		tomcatlist = _tomcatlist;
	}
	
	private static List dbconfiglist;//
	public static List getDbconfiglist() { 
		return dbconfiglist;
	}
	public static void setDbconfiglist(List _dblist) {
		dbconfiglist = _dblist;
	}
	
	private static List ntpconfiglist;//ntp
	public static List getNtpconfiglist() { 
		return ntpconfiglist;
	}
	public static void setNtpconfiglist(List _ntpconfiglist) {
		ntpconfiglist = _ntpconfiglist;
	}
	
	private static List dominolist;//
	public static List getDominolist() { 
		return dominolist;
	}
	public static void setDominolist(List _dominolist) {
		dominolist = _dominolist;
	}
	
	private static List pslist;//
	public static List getPslist() { 
		return pslist;
	}
	public static void setPslist(List _pslist) {
		pslist = _pslist;
	}
	
	private static List iislist;//
	public static List getIislist() { 
		return iislist;
	}
	public static void setIislist(List _iislist) {
		iislist = _iislist;
	}
	
	private static List jbosslist;//
	public static List getJbosslist() { 
		return jbosslist;
	}
	public static void setJbosslist(List _jbosslist) {
		jbosslist = _jbosslist;
	}
	
	private static List apachlist;//
	public static List getApachlist() { 
		return apachlist;
	}
	public static void setApachlist(List _apachlist) {
		apachlist = _apachlist;
	}
	
	private static List upslist;//
	public static List getUpslist() { 
		return upslist;
	}
	public static void setUpslist(List _upslist) {
		upslist = _upslist;
	}
	
	private static List businesslist;//
	public static List getBusinesslist() { 
		return businesslist;
	}
	public static void setBusinesslist(List _businesslist) {
		businesslist = _businesslist;
	}
	
	private static List safelist;//
	public static List getSafelist() { 
		return safelist;
	}
	public static void setSafelist(List _safelist) {
		safelist = _safelist;
	}
	
	private static List tuxdolist;//
	public static List getTuxdolist() { 
		return tuxdolist;
	}
	public static void setTuxdolist(List _tuxdolist) {
		tuxdolist = _tuxdolist;
	}
	
	private static List atmlist;//
	public static List getAtmlist() { 
		return atmlist;
	}
	public static void setAtmlist(List _atmlist) {
		atmlist = _atmlist;
	}
	
	private static List cmtslist;//
	public static List getCmtslist() { 
		return cmtslist;
	}
	public static void setCmtslist(List _cmtslist) {
		cmtslist = _cmtslist;
	}
	
	private static List storagelist;//
	public static List getStoragelist() { 
		return storagelist;
	}
	public static void setStoragelist(List _storagelist) {
		storagelist = _storagelist;
	}
	
	private static List dnslist;//
	public static List getDnslist() { 
		return dnslist;
	}
	public static void setDnslist(List _dnslist) {
		dnslist = _dnslist;
	}
	
	
	
	private static HashMap allequpimgs;//存放所有设备图片配置信息
	public static HashMap getAllequpimgs() { 
		return allequpimgs;
	}
	
	

	public static void setAllequpimgs(HashMap _allequpimgs) {
		allequpimgs = _allequpimgs;
	}
	
	private static Hashtable allnodedepend;//存放所有设备图片访问配置信息
	public static Hashtable getAllnodedepend() { 
		return allnodedepend;
	}

	public static void setAllnodedepend(Hashtable _allnodedepend) {
		allnodedepend = _allnodedepend;
	}	
	
  private static Hashtable allipalias; //存放IP-别名IP对照表
  
  public static Hashtable getAllipalias() { 
		return allipalias;
	}

	public static void setAllipalias(Hashtable _allipalias) {
		allipalias = _allipalias;
	}
	
	  private static Hashtable allipaliasVSip; //存放别名IP-IP对照表
	  
	  public static Hashtable getAllipaliasVSip() { 
			return allipaliasVSip;
		}

		public static void setAllipaliasVSip(Hashtable _allipaliasVSip) {
			allipaliasVSip = _allipaliasVSip;
		}
	
	  private static Hashtable allinterfaces; //存放接口表
	  
	  public static Hashtable getAllinterfaces() { 
			return allinterfaces;
		}

		public static void setAllinterfaces(Hashtable _allinterfaces) {
			allinterfaces = _allinterfaces;
		}
		  private static Hashtable alltracertsdata; //存放TRACERT数据
		  
		  public static Hashtable getAlltracertsdata() { 
				return alltracertsdata;
			}

			public static void setAlltracertsdata(Hashtable _alltracertsdata) {
				alltracertsdata = _alltracertsdata;
			}
	
	//ORACLE采集指标
	  public static Hashtable getOracleGIHash() { 
			return oracleGIHash;
		}

		public static void setOracleGIList(String keys,List oracleGIList) {
			oracleGIHash.put(keys, oracleGIList);
		}
  
		public static List getOracleSIDList() { 
			return oracleSIDList;
		}

		public static void setOracleSIDList(List oracleSIDLists) {
			oracleSIDList = oracleSIDLists;
		}
		
		public static List getDBList() { 
			return dbList;
		}

		public static void setDBList(List dbLists) {
			dbList = dbLists;
		}
  /**
   * 锁对象
   */
  private static Object obj = null;
  
  public synchronized static Object getInstanceOfObject(){
	  if(obj == null){
		  obj = new Object();
	  }
	  return obj;
  }
  
  public static int getM5AgentHostCollectedSize() {
		return m5AgentHostCollectedSize;
	}

	public static void setM5AgentHostCollectedSize(Integer collectedSize) {
		synchronized(getInstanceOfObject()){
			m5AgentHostCollectedSize = collectedSize;
		}
	}

	/**
	 * m5CollectedSize自增1
	 */
	public static int addM5AgentHostCollectedSize(){
		synchronized(getInstanceOfObject()){
			m5AgentHostCollectedSize = m5AgentHostCollectedSize + 1;
		}
		return m5AgentHostCollectedSize;
	}

	public static Hashtable getM5AgentHostAlldata() { 
		return m5AgentHostAlldata;
	}

	public static void setM5AgentHostAlldata(Hashtable alldata) {
		m5AgentHostAlldata = alldata;
	}

	public static HashMap<String, MonitorTimer> getM5AgentHostTimerMap() {
			return m5AgentHostTimerMap;
	  }

		public static void setM5AgentHostTimerMap(HashMap<String, MonitorTimer> timerMap) {
			m5AgentHostTimerMap = timerMap;
		}
  
  public static int getXmlCollectedSize() {
		return xmlCollectedSize;
	}

	public static void setXmlCollectedSize(Integer collectedSize) {
		synchronized(getInstanceOfObject()){
			xmlCollectedSize = collectedSize;
		}
	}

	/**
	 * xmlCollectedSize自增1
	 */
	public static int addXmlCollectedSize(){
		synchronized(getInstanceOfObject()){
			xmlCollectedSize = xmlCollectedSize + 1;
		}
		return xmlCollectedSize;
	}
	
	public static HashMap<String, MonitorTimer> getXmlTimerMap() {
		return xmlTimerMap;
}

	public static void setXmlTimerMap(HashMap<String, MonitorTimer> timerMap) {
		xmlTimerMap = timerMap;
	}
	
  
  public static int getPollCollectedSize() {
		return pollCollectedSize;
	}

	public static void setPollCollectedSize(Integer collectedSize) {
		synchronized(getInstanceOfObject()){
			pollCollectedSize = collectedSize;
		}
	}

	/**
	 * PollCollectedSize自增1
	 */
	public static int addPollCollectedSize(){
		synchronized(getInstanceOfObject()){
			pollCollectedSize = pollCollectedSize + 1;
		}
		return pollCollectedSize;
	}
	
	public static HashMap<String, MonitorTimer> getPollTimerMap() {
		return pollTimerMap;
}

	public static void setPollTimerMap(HashMap<String, MonitorTimer> timerMap) {
		pollTimerMap = timerMap;
	}
  
  public static int getPanelCollectedSize() {
		return panelCollectedSize;
	}

	public static void setPanelCollectedSize(Integer collectedSize) {
		synchronized(getInstanceOfObject()){
			panelCollectedSize = collectedSize;
		}
	}

	/**
	 * panelCollectedSize自增1
	 */
	public static int addPanelCollectedSize(){
		synchronized(getInstanceOfObject()){
			panelCollectedSize = panelCollectedSize + 1;
		}
		return panelCollectedSize;
	}
	
	public static HashMap<String, MonitorTimer> getPanelTimerMap() {
		return panelTimerMap;
  }

	public static void setPanelTimerMap(HashMap<String, MonitorTimer> timerMap) {
		panelTimerMap = timerMap;
	}
	
  
  public static int getLinkCollectedSize() {
		return linkCollectedSize;
	}

	public static void setLinkCollectedSize(Integer collectedSize) {
		synchronized(getInstanceOfObject()){
			linkCollectedSize = collectedSize;
		}
	}

	/**
	 * linkCollectedSize自增1
	 */
	public static int addLinkCollectedSize(){
		synchronized(getInstanceOfObject()){
			linkCollectedSize = linkCollectedSize + 1;
		}
		return linkCollectedSize;
	}

	public static Hashtable getLinkAlldata() { 
		return linkAlldata;
	}

	public static void setLinkAlldata(Hashtable alldata) {
		linkAlldata = alldata;
	}

	public static HashMap<String, MonitorTimer> getLinkTimerMap() {
			return linkTimerMap;
	  }

		public static void setLinkTimerMap(HashMap<String, MonitorTimer> timerMap) {
			linkTimerMap = timerMap;
		}  
  
public static int getM5CollectedSize() {
	return m5CollectedSize;
}

public static void setM5CollectedSize(Integer collectedSize) {
	synchronized(getInstanceOfObject()){
		m5CollectedSize = collectedSize;
	}
}

/**
 * m5CollectedSize自增1
 */
public static int addM5CollectedSize(){
	synchronized(getInstanceOfObject()){
		m5CollectedSize = m5CollectedSize + 1;
	}
	return m5CollectedSize;
}

public static Hashtable getM5Alldata() { 
	return m5Alldata;
}

public static void setM5Alldata(Hashtable alldata) {
	m5Alldata = alldata;
}

public static HashMap<String, MonitorTimer> getM5TimerMap() {
		return m5TimerMap;
  }

	public static void setM5TimerMap(HashMap<String, MonitorTimer> timerMap) {
		m5TimerMap = timerMap;
	}
	
	/**
	 * 10分钟采集
	 * @return
	 */
	public static int getM10CollectedSize() {
		return m10CollectedSize;
	}

	public static void setM10CollectedSize(Integer collectedSize) {
		synchronized(getInstanceOfObject()){
			m10CollectedSize = collectedSize;
		}
	}

	/**
	 * m10CollectedSize自增1
	 */
	public static int addM10CollectedSize(){
		synchronized(getInstanceOfObject()){
			m10CollectedSize = m10CollectedSize + 1;
		}
		return m10CollectedSize;
	}

	public static Hashtable getM10Alldata() { 
		return m10Alldata;
	}

	public static void setM10Alldata(Hashtable alldata) {
		m10Alldata = alldata;
	}

	public static HashMap<String, MonitorTimer> getM10TimerMap() {
			return m10TimerMap;
	  }

		public static void setM10TimerMap(HashMap<String, MonitorTimer> timerMap) {
			m10TimerMap = timerMap;
		}
		
		/**
		 * 30分钟采集
		 * @return
		 */
		public static int getM30CollectedSize() {
			return m30CollectedSize;
		}

		public static void setM30CollectedSize(Integer collectedSize) {
			synchronized(getInstanceOfObject()){
				m30CollectedSize = collectedSize;
			}
		}

		/**
		 * m10CollectedSize自增1
		 */
		public static int addM30CollectedSize(){
			synchronized(getInstanceOfObject()){
				m30CollectedSize = m30CollectedSize + 1;
			}
			return m30CollectedSize;
		}

		public static Hashtable getM30Alldata() { 
			return m30Alldata;
		}

		public static void setM30Alldata(Hashtable alldata) {
			m30Alldata = alldata;
		}

		public static HashMap<String, MonitorTimer> getM30TimerMap() {
				return m30TimerMap;
		  }

			public static void setM30TimerMap(HashMap<String, MonitorTimer> timerMap) {
				m30TimerMap = timerMap;
			}	
			
			
			/**
			 * 1小时采集
			 * @return
			 */
			public static int getH1CollectedSize() {
				return h1CollectedSize;
			}

			public static void setH1CollectedSize(Integer collectedSize) {
				synchronized(getInstanceOfObject()){
					h1CollectedSize = collectedSize;
				}
			}

			/**
			 * h1CollectedSize自增1
			 */
			public static int addH1CollectedSize(){
				synchronized(getInstanceOfObject()){
					h1CollectedSize = h1CollectedSize + 1;
				}
				return h1CollectedSize;
			}

			public static Hashtable getH1Alldata() { 
				return h1Alldata;
			}

			public static void setH1Alldata(Hashtable alldata) {
				h1Alldata = alldata;
			}

			public static HashMap<String, MonitorTimer> getH1TimerMap() {
					return h1TimerMap;
			  }

				public static void setH1TimerMap(HashMap<String, MonitorTimer> timerMap) {
					h1TimerMap = timerMap;
				}
				
		/**
		* 1天采集
		* @return
		*/
		public static int getD1CollectedSize() {
			return d1CollectedSize;
		}

		public static void setD1CollectedSize(Integer collectedSize) {
			synchronized(getInstanceOfObject()){
				d1CollectedSize = collectedSize;
			}
		}

		/**
		* d1CollectedSize自增1
		*/
		public static int addD1CollectedSize(){
			synchronized(getInstanceOfObject()){
				d1CollectedSize = d1CollectedSize + 1;
			}
			return d1CollectedSize;
		}

		public static Hashtable getD1Alldata() { 
			return d1Alldata;
		}

		public static void setD1Alldata(Hashtable alldata) {
			d1Alldata = alldata;
		}

		public static HashMap<String, MonitorTimer> getD1TimerMap() {
			return d1TimerMap;
		}

		public static void setD1TimerMap(HashMap<String, MonitorTimer> timerMap) {
			d1TimerMap = timerMap;
		}
		
		/**
		* 服务器5分钟采集
		* @return
		*/
		public static int getM5HostCollectedSize() {
			return m5HostCollectedSize;
		}

		public static void setM5HostCollectedSize(Integer collectedSize) {
			synchronized(getInstanceOfObject()){
				m5HostCollectedSize = collectedSize;
			}
		}

		/**
		* m5HostCollectedSize自增1
		*/
		public static int addM5HostCollectedSize(){
			synchronized(getInstanceOfObject()){
				m5HostCollectedSize = m5HostCollectedSize + 1;
			}
			return m5HostCollectedSize;
		}

		public static Hashtable getM5HostAlldata() { 
			return m5HostAlldata;
		}

		public static void setM5HostAlldata(Hashtable alldata) {
			m5HostAlldata = alldata;
		}

		public static HashMap<String, MonitorTimer> getM5HostTimerMap() {
			return m5HostTimerMap;
		}

		public static void setM5HostTimerMap(HashMap<String, MonitorTimer> timerMap) {
			m5HostTimerMap = timerMap;
		}		
		
		/**
		* 服务器10分钟采集
		* @return
		*/
		public static int getM10HostCollectedSize() {
			return m10HostCollectedSize;
		}

		public static void setM10HostCollectedSize(Integer collectedSize) {
			synchronized(getInstanceOfObject()){
				m10HostCollectedSize = collectedSize;
			}
		}

		/**
		* m10HostCollectedSize自增1
		*/
		public static int addM10HostCollectedSize(){
			synchronized(getInstanceOfObject()){
				m10HostCollectedSize = m10HostCollectedSize + 1;
			}
			return m10HostCollectedSize;
		}

		public static Hashtable getM10HostAlldata() { 
			return m10HostAlldata;
		}

		public static void setM10HostAlldata(Hashtable alldata) {
			m10HostAlldata = alldata;
		}

		public static HashMap<String, MonitorTimer> getM10HostTimerMap() {
			return m10HostTimerMap;
		}

		public static void setM10HostTimerMap(HashMap<String, MonitorTimer> timerMap) {
			m10HostTimerMap = timerMap;
		}
		
		/**
		* 服务器30分钟采集
		* @return
		*/
		public static int getM30HostCollectedSize() {
			return m30HostCollectedSize;
		}

		public static void setM30HostCollectedSize(Integer collectedSize) {
			synchronized(getInstanceOfObject()){
				m30HostCollectedSize = collectedSize;
			}
		}

		/**
		* m30HostCollectedSize自增1
		*/
		public static int addM30HostCollectedSize(){
			synchronized(getInstanceOfObject()){
				m30HostCollectedSize = m30HostCollectedSize + 1;
			}
			return m5HostCollectedSize;
		}

		public static Hashtable getM30HostAlldata() { 
			return m30HostAlldata;
		}

		public static void setM30HostAlldata(Hashtable alldata) {
			m30HostAlldata = alldata;
		}

		public static HashMap<String, MonitorTimer> getM30HostTimerMap() {
			return m30HostTimerMap;
		}

		public static void setM30HostTimerMap(HashMap<String, MonitorTimer> timerMap) {
			m30HostTimerMap = timerMap;
		}
		
		/**
		* 服务器1小时采集
		* @return
		*/
		public static int getH1HostCollectedSize() {
			return h1HostCollectedSize;
		}

		public static void setH1HostCollectedSize(Integer collectedSize) {
			synchronized(getInstanceOfObject()){
				h1HostCollectedSize = collectedSize;
			}
		}

		/**
		* h1HostCollectedSize自增1
		*/
		public static int addH1HostCollectedSize(){
			synchronized(getInstanceOfObject()){
				h1HostCollectedSize = h1HostCollectedSize + 1;
			}
			return h1HostCollectedSize;
		}

		public static Hashtable getH1HostAlldata() { 
			return h1HostAlldata;
		}

		public static void setH1HostAlldata(Hashtable alldata) {
			h1HostAlldata = alldata;
		}

		public static HashMap<String, MonitorTimer> getH1HostTimerMap() {
			return h1HostTimerMap;
		}

		public static void setH1HostTimerMap(HashMap<String, MonitorTimer> timerMap) {
			h1HostTimerMap = timerMap;
		}
		
		/**
		* 服务器1天采集
		* @return
		*/
		public static int getD1HostCollectedSize() {
			return d1HostCollectedSize;
		}

		public static void setD1HostCollectedSize(Integer collectedSize) {
			synchronized(getInstanceOfObject()){
				d1HostCollectedSize = collectedSize;
			}
		}

		/**
		* d1HostCollectedSize自增1
		*/
		public static int addD1HostCollectedSize(){
			synchronized(getInstanceOfObject()){
				d1HostCollectedSize = d1HostCollectedSize + 1;
			}
			return d1HostCollectedSize;
		}

		public static Hashtable getD1HostAlldata() { 
			return d1HostAlldata;
		}

		public static void setD1HostAlldata(Hashtable alldata) {
			d1HostAlldata = alldata;
		}

		public static HashMap<String, MonitorTimer> getD1HostTimerMap() {
			return d1HostTimerMap;
		}

		public static void setD1HostTimerMap(HashMap<String, MonitorTimer> timerMap) {
			d1HostTimerMap = timerMap;
		}
		

		
  
  public static Hashtable getAllNetworkData() {
	return allNetworkData;
}


public static Hashtable getAllNetworkPingData() {
	return allNetworkPingData;
}


public static void setAllNetworkPingData(Hashtable allNetworkPingData) {
	ShareData.allNetworkPingData = allNetworkPingData;
}


public static void setAllNetworkData(Hashtable allNetworkData) {
	ShareData.allNetworkData = allNetworkData;
}


public static Hashtable getSubscribeReportHash() {
		return subscribeReportHash;
	}


	public static Hashtable getAllLinknodeInterfaceData() {
	return allLinknodeInterfaceData;
}


public static void setAllLinknodeInterfaceData(
		Hashtable allLinknodeInterfaceData) {
	ShareData.allLinknodeInterfaceData = allLinknodeInterfaceData;
}


	public static void setSubscribeReportHash(Hashtable subscribeReportHash) {
		ShareData.subscribeReportHash = subscribeReportHash;
	}
  
  public static Hashtable getAllLinkData() {
		return allLinkData;
	}


	public static void setAllLinkData(Hashtable allLinkData) {
		ShareData.allLinkData = allLinkData;
	}


public static void setCheckeventdata(String key,String value){  
	  checkeventdata.put(key,value);
	  }


  public static Hashtable getCheckeventdata(){  
	  return checkeventdata;
	  }
  
  
  public static int runflag =0;
  public static int runserviceflag =0;
  
  public static Hashtable apachedata = new Hashtable();
  public static void setApachedata(String ip,Hashtable hash){  
	  apachedata.put(ip,hash);
	  }


  public static Hashtable getApachedata(){  
	  return apachedata;
	  }
  
  public static Hashtable jbossdata = new Hashtable();
  public static void setJbossdata(String ip,Hashtable hash){  
	  jbossdata.put(ip,hash);
	  }


public static Hashtable getAllDnsData() {
	return allDnsData;
}


public static void setAllDnsData(Hashtable allDnsData) {
	ShareData.allDnsData = allDnsData;
}


public static Hashtable getJbossdata(){  
	  return jbossdata;
	  }
  
  public static int getRunflag(){
	  return runflag;
  }
  public static void setRunflag(int _runflag){
	  runflag = _runflag;
  }
  
  public static int getRunserviceflag(){
	  return runserviceflag;
  }
  public static void setRunserviceflag(int _runserviceflag){
	  runserviceflag = _runserviceflag;
  }
  
  
  //nielin add 2010-06-28
  public static void setStoragedata(String ip,Hashtable hash){  
	  allstoragedata.put(ip,hash);
	  }
  
  //nielin add 2010-06-28
  public static Hashtable getStoragedata(){  
	  return allstoragedata;
	  }
  
  public static void setMySqlmonitordata(String ip,Hashtable hash){  
	  mysqlmonitordata.put(ip,hash);
	  }
 public static Hashtable getMySqlmonitordata(){
	  	return mysqlmonitordata;
	  }
  public static void setMySqldata(String ip,Hashtable hash){  
	  sqlserverdata.put(ip,hash);
	  }
 public static Hashtable getMySqldata(){
	  	return sqlserverdata;
	  }
  
  public static void setAlltuxedodata(String ip,Hashtable hash){  
	  alltuxedodata.put(ip,hash);
	  }
  public static Hashtable getAlltuxedodata(){
	  	return alltuxedodata;
	  }
  
  public static void setAlllegatodata(String ip,HashMap hash){  
	  alllegatodata.put(ip,hash);
	  }
  public static Hashtable getAlllegatodata(){
	  	return alllegatodata;
	  }
  
  public static void setAllsyslogconfdata(Hashtable hash){
	  allsyslogconfdata = hash;
  }
  public static Hashtable getAllsyslogconfdata(){
	  return allsyslogconfdata;
  }
  
  public static void setEmersondata(String ip,Hashtable hash){  
	  emersondata.put(ip,hash);
	  }
  public static Hashtable getEmersondata(){
	  	return emersondata;
	  }


  public static void setSqldbdata(String ip,Hashtable hash){  
	  sqldbdata.put(ip,hash);
	  }
  public static Hashtable getSqldbdata(){
	  	return sqldbdata;
	  }
  
  public static void setWasdata(String ip,Hashtable hash){  
	  wasdata.put(ip,hash);
	  }
  public static Hashtable getWasdata(){
	  	return wasdata;
	  }
  
  
  public static void setDb2type6spacedata(String ip,Hashtable hash){  
	  db2type6spacedata.put(ip,hash);
	  }
  public static Hashtable getDb2type6spacedata(){
	  	return db2type6spacedata;
	  }
  
  public static void setAlloraspacealarmdata(Hashtable hash){  
	  alloraspacealarmdata = hash;
	  }
  public static Hashtable getAlloraspacealarmdata(){
	  	return alloraspacealarmdata;
	  }

  public static void setInformixspacedata(String ip,List list){  
	  informixspacedata.put(ip,list);
	  }
  public static Hashtable getInformixspacedata(){
	  	return informixspacedata;
	  }
 
  public static void setAllinformixspacealarmdata(Hashtable hash){  
	  allinformixspacealarmdata = hash;
	  }
  public static Hashtable getAllinformixspacealarmdata(){
	  	return allinformixspacealarmdata;
	  }
  
  

  public static void setOraspacedata(String ip,Vector vec){  
	  oraspacedata.put(ip,vec);
	  }
  public static Hashtable getOraspacedata(){
	  	return oraspacedata;
	  }

  public static void setAlloracledata(String ip,Hashtable hash){  
	  alloracledata.put(ip,hash);
	  }
  public static Hashtable getAlloracledata(){
	  	return alloracledata;
	  }

  public static void setAlldb2data(String ip,Hashtable hash){  
	  alldb2data.put(ip,hash);
	  }
  public static Hashtable getAlldb2data(){
	  	return alldb2data;
	  }

  public static void setAllwlserveralarmdata(Hashtable hash){  
	  allwlserveralarmdata = hash;
	  }
  public static Hashtable getAllwlserveralarmdata(){
	  	return allwlserveralarmdata;
	  }

  
  public static void setAllmqalarmdata(Hashtable hash){  
	  allmqalarmdata = hash;
	  }
  public static Hashtable getAllmqalarmdata(){
	  	return allmqalarmdata;
	  }
  
  
  public static void setAlldiskalarmdata(Hashtable hash){  
	  alldiskalarmdata = hash;
	  }
  public static Hashtable getAlldiskalarmdata(){
	  	return alldiskalarmdata;
	  }
  
  
  public static void setIBMStoragedata(String ip,Hashtable hash){  
	  ibmstoragedata.put(ip,hash);
	  }
  public static Hashtable getIBMStoragedata(){
	  	return ibmstoragedata;
	  }
  
  
  public static void setWeblogicdata(String ip,Hashtable hash){  
	  weblogicdata.put(ip,hash);
	  }
  public static Hashtable getWeblogicdata(){
	  	return weblogicdata;
	  }
  
  public static void setDhcpdata(String ip,Hashtable hash){  
	  dhcpdata.put(ip,hash);
	  }
  public static Hashtable getDhcpdata(){
	  	return dhcpdata;
	  }
  
  public static void setTomcatdata(String ip,Hashtable hash){  
	  tomcatdata.put(ip,hash);
	  }
  public static Hashtable getTomcatdata(){
	  	return tomcatdata;
	  }
  private static List resinlist;//
	public static List getResinlist() { 
		return resinlist;
	}
	public static void setResinlist(List _resinlist) {
		resinlist = _resinlist;
	}
	 public static Hashtable getResindata(){
		  return resindata;
	  }
	 public static void setResindata(String ip,Hashtable hash){  
		  resindata.put(ip,hash);
	  }
  public static void addMqdata(MQConfig mqconf,Hashtable mqValue){
	  //以IP:进程名作为KEY
//System.out.println(mqconf.getIpaddress()+":"+mqconf.getManagername()+"-----");	
	  if(mqconf != null && mqconf.getIpaddress()!=null && mqconf.getManagername()!=null && mqValue != null)
	  mqdata.put(mqconf.getIpaddress()+":"+mqconf.getManagername(),mqValue);
	  }
  public static Hashtable getMqdata(){
	  	return mqdata;
	  }  
  
  public static Hashtable getProcsendeddata(){
	  	return procsendeddata;
	  }
  
  
  public static void setLostprocdata(String ip,Hashtable hash){  
	  lostprocdata.put(ip,hash);
	  }
  public static Hashtable getLostprocdata(String ip){
	  	return (Hashtable)lostprocdata.get(ip);
	  }

  
  public static void setSysbasedata(String ip,Hashtable hash){  
	  sysbasedata.put(ip,hash);
	  }
  public static Hashtable getSysbasedata(){
	  	return sysbasedata;
	  }

  public static void setSqlserverdata(String ip,Hashtable hash){  
	  sqlserverdata.put(ip,hash);
	  }
  public static Hashtable getSqlserverdata(){
	  	return sqlserverdata;
	  }
  
  
  public static Date formerUpdateTime = new Date();
  
  
  
  
  public static void setFormerUpdateTime(Date date){
	  formerUpdateTime = date;
  }
  public static Date getFormerUpdateTime(){
	  if (formerUpdateTime == null) formerUpdateTime = new Date();
	  return formerUpdateTime;
  }
  
  public static void setPolicydata(String ip,Hashtable hash){
	  policydata.put(ip,hash);
	  }
  public static Hashtable getPolicydata(){
	  	return policydata;
  }
  

  public static void setTosroutedata(String ip,List hash){
	  tosroutedata.put(ip,hash);
	  }
  public static Hashtable getTosroutedata(){
	  	return tosroutedata;
	  }
  
  public static void setDominodata(String ip,Hashtable hash){
	  dominodata.put(ip,hash);
	  }
  public static Hashtable getDominodata(){
	  	return dominodata;
	  }
  public static void setIisdata(String ip,List list){
	  iisdata.put(ip,list);
	  }
  public static Hashtable getIisdata(){
	  	return iisdata;
	  }
  
  public static void setIprouterdata(String ip,Vector v){
  	iprouterdata.put(ip,v);
  }
  public static Hashtable getIprouterdata(){
  	return iprouterdata;
  }
  
  public static Hashtable getIpmacbanddata(){
  	return ipmacbanddata;
  }
  public static Hashtable getFdbipmacbanddata(){
	  	return fdbipmacbanddata;
	  }

  public static void setRelateipmacdata(String ip,Vector v){
  	relateipmacdata.put(ip,v);
  }
  public static Hashtable getRelateipmacdata(){
  	return relateipmacdata;
  }
  
  public static void setRelatefdbipmacdata(String ip,Vector v){
	  	relatefdbipmacdata.put(ip,v);
	  }
	  public static Hashtable getRelatefdbipmacdata(){
	  	return relatefdbipmacdata;
	  }

  public static void setRelateippingdata(String ip,Vector v){
  	relateippingdata.put(ip,v);
  }
  public static Hashtable getRelateippingdata(){
  	return relateippingdata;
  }

  public static void setSameequipsmsdata(String ip,Vector v){  	
  	sameequipsmsdata.put(ip,v);  	
  }
  public static void refreshSameequipsmsdata(){  	
  	sameequipsmsdata = new Hashtable();  	
  }
  public static Hashtable getSameequipsms(){
  	return sameequipsmsdata;
  }
  public static Vector getSameequipsmsdata(String ip){
  	return (Vector)sameequipsmsdata.get(ip);
  }

  
  
  
  public static void setEventdata(String ip,Vector v){  	
  	eventdata.put(ip,v);  	
  }
  public static Vector getEventdata(String ip){
  	return (Vector)eventdata.get(ip);
  }
  
  public static void setOctetsdata(String ip,Hashtable hash){  	
  	octetsdata.put(ip,hash);  	
  }
  public static Hashtable getOctetsdata(String ip){
  	return (Hashtable)octetsdata.get(ip);
  }
  
  public static void setRealOctetsdata(String ip,Hashtable hash){  	
	  	realoctetsdata.put(ip,hash);  	
	  }
	  public static Hashtable getRealOctetsdata(String ip){
	  	return (Hashtable)realoctetsdata.get(ip);
	  }
  
  public static void setPksdata(String ip,Hashtable hash){  	
	  	pksdata.put(ip,hash);  	
	  }
	  public static Hashtable getPksdata(String ip){
	  	return (Hashtable)pksdata.get(ip);
	  }
  
  
  public static void setPacksdata(String ip,Hashtable hash){  	
  	packsdata.put(ip,hash);  	
  }
  public static Hashtable getPacksdata(String ip){
  	return (Hashtable)packsdata.get(ip);
  }
  
  public static void setDiscardsdata(String ip,Hashtable hash){
  	discardsdata.put(ip,hash);
  }
  public static Hashtable getDiscardsdata(String ip){
  	return (Hashtable)discardsdata.get(ip);
  }
  public static void setErrorsdata(String ip,Hashtable hash){
  	errorsdata.put(ip,hash);
  }
  public static Hashtable getErrorsdata(String ip){
  	return (Hashtable)errorsdata.get(ip);
  }

  
  public static void setCount(int acount){
  	count = acount;
  }
  public static int getCount(){
  	return count;
  }

  public static void setFirstipmac(int afirstipmac){
  	firstipmac = afirstipmac;
  }
  public static int getFirstipmac(){
  	return firstipmac;
  }
  
  
  public static void setPingdat(Hashtable pdata){
  	pingdata = pdata;
  }
  public static void setPingdata(String ipaddress,Vector v){
  	if (pingdata.containsKey(ipaddress))
  		pingdata.remove(ipaddress);
  		pingdata.put(ipaddress,v);
  }
  
  public static void setIISPingdata(Hashtable pdata){
	  	iispingdata = pdata;
	  }
	  public static void setIISPingdata(String ipaddress,Vector v){
	  	if (iispingdata.containsKey(ipaddress))
	  		iispingdata.remove(ipaddress);
	  	iispingdata.put(ipaddress,v);
	  }
  /*
  public static void setAllpingdata(String ipaddress,Pingcollectdata pingcollectdata){
  	if (allpingdata.containsKey(ipaddress))
  		allpingdata.remove(ipaddress);
  	allpingdata.put(ipaddress,pingcollectdata);
  }
  */

  public static void setAlldbdata(String ipaddress,Calendar date){
  	if (alldbdata.containsKey(ipaddress))
  		alldbdata.remove(ipaddress);
  	alldbdata.put(ipaddress,date);
  }

  public static Hashtable getPingdata(){ 
  	return pingdata;
  }
  public static Hashtable getIISPingdata(){
	  	return iispingdata;
	  }
  public static void setSharedata(Hashtable sdata){
  	sharedata = sdata;
  }
  public static void setSharedata(String ipaddress,Vector v){
  	//sharedata = sdata;
  	if (sharedata.containsKey(ipaddress))
  		sharedata.remove(ipaddress);
  	sharedata.put(ipaddress,v);
  }
  
  public static void setLastPortdata(Hostlastcollectdata lastdata){
  	portdata.put(lastdata.getIpaddress()+"."+lastdata.getSubentity(),lastdata);
  }
  /*
  public static void addSendeddata(SendedSMS sendedsms){
  	sendeddata.put(sendedsms.getIpaddress(),sendedsms);
  }
  public static void addMemsendeddata(SendedSMS sendedsms){
  	memsendeddata.put(sendedsms.getIpaddress()+":"+sendedsms.getCategory()+":"+sendedsms.getSubentity(),sendedsms);
  }
  
  public static void addPortSendeddata(SendedSMS sendedsms){
  	portsendeddata.put(sendedsms.getIpaddress(),sendedsms);
  }
  */
  public static void addFilesizedata(String ipandfilename,String filesize){
  	filesizedata.put(ipandfilename,filesize);
  }

  public static Hashtable getSendeddata(){
  	return sendeddata;
  }
  public static Hashtable getCreateEventdata(){
	  	return createeventdata;
	  }
  public static Hashtable getMemsendeddata(){
  	return memsendeddata;
  }

  public static Hashtable getPortsendeddata(){
  	return portsendeddata;
  }
  public static Hashtable getFilesizedata(){
  	return filesizedata;
  }
  public static Hashtable getAllpingdata(){
  	return allpingdata;
  }
  public static Hashtable getAlldbdata(){
  	return alldbdata;
  }

  public static Hashtable getAllportdata(){
  	return allportdata;
  }

  public static Hashtable getPortdata(){
  	return portdata;
  }
  
  public static void setLastHostdata(Hostlastcollectdata lastdata){
  	hostdata.put(lastdata.getIpaddress(),lastdata);
  }
  
  public static Hashtable getHostdata(){
  	return hostdata;
  }
  public static void setLastMemdata(String index,String value){
  	hostdata.put(index,value);
  }
  public static Hashtable getMemdata(){
  	return memdata;
  }
  
  
  public static Hashtable getSharedata(){
  	if (sharedata == null )sharedata=new Hashtable();
  	return sharedata;
  }
  
  public static Hashtable informixmonitordata = new Hashtable(); // 存储informix采集的数据信息

	public static void setInfomixmonitordata(String ip, Hashtable hash) {
		informixmonitordata.put(ip, hash);
	}
public static Hashtable getInformixmonitordata() {
		return informixmonitordata;
	}

public static Hashtable getGSNdata() {
	return GSNdata;
}
public static void setGSNdata(String key,Hashtable ndata) {
	GSNdata.put(key, ndata);
}


public static ThreadPool getNetThreadPool() {
	return netThreadPool;
}


public static void setNetThreadPool(ThreadPool netThreadPool) {
	ShareData.netThreadPool = netThreadPool;
}


public static Hashtable getConnectConfigHashtable() {
	return connectConfigHashtable;
}


public static void setConnectConfigHashtable(Hashtable connectConfigHashtable) {
	ShareData.connectConfigHashtable = connectConfigHashtable;
}


public static Hashtable getPortConfigHash() {
	return portConfigHash;
}


public static void setPortConfigHash(Hashtable portConfigHash) {
	ShareData.portConfigHash = portConfigHash;
}

public static Hashtable getGatherHash() {
	return gatherHash;
}


public static void setGatherHash(Hashtable gatherHash) {
	ShareData.gatherHash = gatherHash;
}


public static Hashtable getCheckEventHash() {
	return checkEventHash;
}


public static void setCheckEventHash(Hashtable checkEventHash) {
	ShareData.checkEventHash = checkEventHash;
}

public static Hashtable getParamsHash() {
	return paramsHash;
}

public static void setParamsHash(Hashtable paramsHash) {
	ShareData.paramsHash = paramsHash;
}
public static Hashtable<String, RemoteClientInfo> getIp_clientInfoHash() {
	return ip_clientInfoHash;
}

public static void setIp_clientInfoHash(
		Hashtable<String, RemoteClientInfo> ip_clientInfoHash) {
	ShareData.ip_clientInfoHash = ip_clientInfoHash;
}

	public static Hashtable getAclHash(String ip) {
		return (Hashtable)aclHash.get(ip); 
	}
	
	public static void setAclHash(String ip,Hashtable hash) {
		aclHash.put(ip,hash);
	}
	public static Hashtable getSlaHash() {
		return slaHash; 
	}
	
	public static void setSlaHash(String configid,Hashtable hash) {
		slaHash.put(configid,hash);
	}

	public static Hashtable getBussinessdata() {
		return bussinessdata;
	}
	public static void setBussinessdata(Hashtable bussinessdata) {
		ShareData.bussinessdata = bussinessdata;
	}

	public static String getIsStopMachine() {
		return isStopMachine;
	}

	public static void setIsStopMachine(String isStopMachine) {
		ShareData.isStopMachine = isStopMachine;
	}
	private static Hashtable sendAlarmTimes = new Hashtable();//存放所有已经发送的告警方式和次数信息
	public static Hashtable getSendAlarmTimes() { 
		return sendAlarmTimes;
	}

	public static void setSendAlarmTimes(String name) {
		sendAlarmTimes.put(name, name);
	}
	
	private static Hashtable vmwareConfig = new Hashtable();//存放所有VMWare连接登陆信息

	public static Hashtable getAlarmportConfigHash() {
		return alarmportConfigHash;
	}

	public static void setAlarmportConfigHash(Hashtable alarmportConfigHash) {
		ShareData.alarmportConfigHash = alarmportConfigHash;
	}

	public static Hashtable getRoomAlarmHashtable() {
		return roomAlarmHashtable;
	}

	public static void setRoomAlarmHashtable(Hashtable roomAlarmHashtable) {
		ShareData.roomAlarmHashtable = roomAlarmHashtable;
	}
	public static Hashtable getVmwareConfig() { 
		return vmwareConfig;
	}

	public static void setVmwareConfig(String nodeid,VMWareConnectConfig config) {
		vmwareConfig.put(nodeid, config);
	}

	public static Hashtable getVmdata() {
		return vmdata;
	}

	public static void setVmdata(Hashtable vmdata) {
		ShareData.vmdata = vmdata;
	}

	public static Hashtable getEmcdata() {
		return emcdata;
	}

	public static void setEmcdata(Hashtable emcdata) {
		ShareData.emcdata = emcdata;
	}

	public static String getFlag() {
		return flag;
	}

	public static void setFlag(String flag) {
		ShareData.flag = flag;
	}

	public static List getWebloginlist() {
		return webloginlist;
	}

	public static void setWebloginlist(List webloginlist) {
		ShareData.webloginlist = webloginlist;
	}

	public static Hashtable getWeblogin() {
		return weblogin;
	}

	 public static void setWeblogin(String ip,Hashtable weblogin){  
		  weblogicdata.put(ip,weblogin);
		  }
	 
	 public static Hashtable SyslogHash = new Hashtable();//syslog监控列表
	  
	  public static Hashtable getSyslogHash() {
		return SyslogHash;
	}

	public static void setSyslogHash(Hashtable syslogHash) {
		SyslogHash = syslogHash;
	}
	
	  public static Hashtable syslogruleNode = new Hashtable();  // 存储采集的数据信息   konglingqi add 2013-03-06
	  public static Hashtable getSyslogruleNode() {
			return syslogruleNode;
		}

		public static void setSyslogruleNode(Hashtable syslogRuleHash) {
			syslogruleNode = syslogRuleHash;
		}
		
		  public static Hashtable ucastpktsdata = new Hashtable();  // all host and network
		  public static Hashtable nucastpktsdata = new Hashtable();  // all host and network
		
		public static Hashtable getUcastpktsdata(String ip) {
			return (Hashtable)ucastpktsdata.get(ip);
		}

		public static void setUcastpktsdata(String ip,Hashtable hash) {
			ucastpktsdata.put(ip,hash);
		}

		public static Hashtable getNucastpktsdata(String ip) {
			return (Hashtable)nucastpktsdata.get(ip);
		}

		public static void setNucastpktsdata(String ip,Hashtable hash) {
			nucastpktsdata.put(ip,hash);
		}

		public static String getControlVoice() {
			return controlVoice;
		}

		public static void setControlVoice(String controlVoice) {
			
			ShareData.controlVoice = controlVoice;
		}
		
}
