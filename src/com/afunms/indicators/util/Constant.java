package com.afunms.indicators.util;


/**
 * 此类型 为 类型  和  子类型  常数
 * @author Administrator
 *
 */
public class Constant {
	
	/**
	 * 所有类型
	 */
	public static String ALL_TYPE = "-1";
	
	public static String TYPE_HOST = "host";
	
	public static String TYPE_NET = "net";
	
	public static String TYPE_DB = "db";
	
	public static String TYPE_MIDDLEWARE = "middleware";
	
	public static String TYPE_SERVICE = "service";
	
	public static String TYPE_TOPO = "topo";
	
	public static String TYPE_UPS = "ups";
	
	public static String TYPE_AIR = "air";
	

	
	public static String TYPE_FIREWALL = "firewall";
	
	public static String TYPE_GATEWAY = "gateway";
	
	public static String TYPE_ATM = "atm";
	public static String TYPE_CMTS = "cmts";
	
	//虚拟设备
	public static String TYPE_VIRTUAL = "virtual";
	public static String TYPE_VIRTUAL_SUBTYPE_VMWARE = "vmware";
	
	
	public static String TYPE_VPN = "vpn";
	
	public static String TYPE_BALANCER = "balancer";// 负载均衡器
	public static String TYPE_F5 = "f5";
	
	public static String TYPE_BUSINESS = "business";
	public static String TYPE_BEHAVIOR = "behavior";// 上网行为管理
	public static String TYPE_FLOWCONTROL = "flowcontrol";// 流量管理
	public static String TYPE_DDOS = "ddos";// 抗DDOS
	public static String TYPE_XSCAN = "xscan";// 漏洞扫描
	public static String TYPE_LOGAUDIT = "logaudit";// 安全日志审计
	/**
	 * 链路
	 */
	public static String TYPE_LINK = "link";
	/**
	 * 所有子类型
	 */
	
	public static String ALL_SUBTYPE = "-1";
	
	/**
	 * 服务器的子类型
	 */
	
	public static String TYPE_HOST_SUBTYPE_WINDOWS = "windows";
	
	public static String TYPE_HOST_SUBTYPE_LINUX = "linux";
	
	public static String TYPE_HOST_SUBTYPE_AIX  = "aix";
	
	public static String TYPE_HOST_SUBTYPE_HPUNIX  = "hpunix";
	
	public static String TYPE_HOST_SUBTYPE_SOLARIS  = "solaris";
	
	public static String TYPE_HOST_SUBTYPE_AS400  = "as400";
	
    public static String TYPE_HOST_SUBTYPE_SCOUNIX  = "scounixware";
	
	public static String TYPE_HOST_SUBTYPE_SCOOPENSERVER  = "scoopenserver";
	
    public static String TYPE_SERVICE_SUBTYPE_DP = "dp";
	
	public static String TYPE_SERVICE_SUBTYPE_GGSCI = "ggsci";
	
	public static String TYPE_SERVICE_SUBTYPE_NAS = "nas";
	
	public static String TYPE_SERVICE_SUBTYPE_NTP = "ntp";

	
	
	/**
	 * 网络设备的子类型
	 */
	
	public static String TYPE_NET_SUBTYPE_CISCO = "cisco";
	
	public static String TYPE_NET_SUBTYPE_H3C = "h3c";
	
	public static String TYPE_NET_SUBTYPE_ENTRASYS = "entrasys";
	
	public static String TYPE_NET_SUBTYPE_RADWARE = "radware";
	
	public static String TYPE_NET_SUBTYPE_MAIPU = "maipu";
	
	public static String TYPE_NET_SUBTYPE_REDGIANT = "redgiant";
	
	public static String TYPE_NET_SUBTYPE_NORTHTEL = "northtel";
	
	public static String TYPE_NET_SUBTYPE_DLINK = "dlink";
	
	public static String TYPE_NET_SUBTYPE_BDCOM = "bdcom";
	public static String TYPE_NET_SUBTYPE_ALCATEL = "alcatel";
	public static String TYPE_NET_SUBTYPE_AVAYA = "avaya";
	public static String TYPE_NET_SUBTYPE_JUNIPER = "juniper";
	public static String TYPE_NET_SUBTYPE_CHECKPOINT = "checkpoint";
	
	public static String TYPE_STORAGE_SUBTYPE_HDS = "hds";
	public static String TYPE_STORAGE_SUBTYPE_NETAPP = "netapp";
	
	public static String TYPE_STORAGE_SUBTYPE_EMC_VNX = "emc_vnx";
	
	public static String TYPE_STORAGE_SUBTYPE_EMC_DMX = "emc_dmx";
	
	public static String TYPE_STORAGE_SUBTYPE_EMC_VMAX = "emc_vmax";
	
	public static String TYPE_STORAGE_SUBTYPE_HP = "hp";
	
	public static String TYPE_STORAGE = "storage";
	
	public static String TYPE_NET_SUBTYPE_ZTE = "zte";
	
	public static String TYPE_ATM_SUBTYPE_ATM = "atm";
	public static String TYPE_ATM_SUBTYPE_CMTS = "cmts";
	
	public static String TYPE_VPN_SUBTYPE_ARRAYNETWORKS = "arraynetworks";
	
	public static String TYPE_NET_SUBTYPE_BROCADE = "brocade";
	public static String TYPE_NET_SUBTYPE_OTHER = "other";
	
    public static String TYPE_FIREWALL_SUBTYPE_HILLSTONE = "hillstone";
	
	public static String TYPE_FIREWALL_SUBTYPE_NETSCREEN = "netscreen";
	
	public static String TYPE_FIREWALL_SUBTYPE_VENUS = "venus";
	public static String TYPE_FIREWALL_SUBTYPE_TIPPINGPOINT = "tippingpoint";
	public static String TYPE_FIREWALL_SUBTYPE_SECWORLD = "secworld";
	public static String TYPE_FIREWALL_SUBTYPE_TOPSEC = "topsec";
	public static String TYPE_FIREWALL_SUBTYPE_SECGAGE = "secgate";
	public static String TYPE_FIREWALL_SUBTYPE_DPTECH = "dptech";
	public static String TYPE_FIREWALL_SUBTYPE_CHINAGUARD = "chinaguard";
	public static String TYPE_FIREWALL_SUBTYPE_REDGIANT = "redgiant";
	
	public static String TYPE_NET_SUBTYPE_IBM = "ibm";
	
	public static String TYPE_FIREWALL_SUBTYPE_NOKIA = "nokia";
	
	public static String TYPE_GATEWAY_SUBTYPE_CISCO = "cisco";
	
	public static String TYPE_F5_SUBTYPE_F5 = "f5";
	
	
	public static String TYPE_NET_MOTOROLA = "motorola";
	public static String TYPE_NET_SUBTYPE_HP = "hp";
	public static String TYPE_NET_SUBTYPE_SCOUNIX = "scounixware";
	public static String TYPE_NET_SUBTYPE_SCOUOPENSERVER = "scoopenserver";
	/**
	 * 数据库的子类型
	 */
	
	public static String TYPE_DB_SUBTYPE_ORACLE = "oracle";
	
	public static String TYPE_DB_SUBTYPE_ORACLERAC = "oraclerac";
	
	public static String TYPE_DB_SUBTYPE_SQLSERVER = "sqlserver";
	
	public static String TYPE_DB_SUBTYPE_MYSQL = "mysql";
	
	public static String TYPE_DB_SUBTYPE_DB2 = "db2";
	
	public static String TYPE_DB_SUBTYPE_SYBASE = "sybase";
	
	public static String TYPE_DB_SUBTYPE_INFORMIX = "informix";
	
	
	/**
	 * 中间件的子类型
	 */
	
	public static String TYPE_MIDDLEWARE_SUBTYPE_MQ = "mq";
	
	public static String TYPE_MIDDLEWARE_SUBTYPE_DOMINO = "domino";
	
	public static String TYPE_MIDDLEWARE_SUBTYPE_WAS = "was";
	
	public static String TYPE_MIDDLEWARE_SUBTYPE_TOMCAT = "tomcat";
	
	public static String TYPE_MIDDLEWARE_SUBTYPE_RESIN = "resin";
	
	public static String TYPE_MIDDLEWARE_SUBTYPE_IIS = "iis";
	
	public static String TYPE_MIDDLEWARE_SUBTYPE_JBOSS = "jboss";
	
	public static String TYPE_MIDDLEWARE_SUBTYPE_APACHE = "apache";
	
	public static String TYPE_MIDDLEWARE_SUBTYPE_TUXEDO = "tuxedo";
	
	public static String TYPE_MIDDLEWARE_SUBTYPE_CICS = "cics";
	
	public static String TYPE_MIDDLEWARE_SUBTYPE_DNS = "dns";
	
	public static String TYPE_MIDDLEWARE_SUBTYPE_WEBLOGIC = "weblogic";
	
	/**
	 * 服务的子类型
	 */
	
	public static String TYPE_SERVICE_SUBTYPE_FTP = "ftp";
	
	public static String TYPE_SERVICE_SUBTYPE_EMAIL = "mail";
	
	public static String TYPE_SERVICE_SUBTYPE_HOSTPROCESS = "hostprocess";
	
	public static String TYPE_SERVICE_SUBTYPE_WEB = "url";
	
	public static String TYPE_SERVICE_SUBTYPE_WENLOGIN="weblogin";
	
	public static String TYPE_SERVICE_SUBTYPE_PORT = "socket";
	public static String TYPE_SERVICE_SUBTYPE_TFTP = "tftp";
	
	public static String TYPE_SERVICE_SUBTYPE_DHCP = "dhcp";
	public static String TYPE_SERVICE_SUBTYPE_PROCESS = "process";
	/**
	 * 拓扑图的子类型
	 */
	
	public static String TYPE_TOPO_SUBTYPE_BUSINESSVIEW = "businessview";
	/**
	 * 空调的子类型
	 */
	
	//public static String TYPE_AIR_SUBTYPE_EMS = "ems";
	public static String TYPE_AIR_SUBTYPE_EMS = "emerson";
	/**
	 * UPS的子类型
	 */
	
	//public static String TYPE_UPS_SUBTYPE_EMS = "ems";
	public static String TYPE_UPS_SUBTYPE_EMS = "emerson";
	public static String TYPE_UPS_SUBTYPE_MGE = "mge";
	
	/**
	 * 业务的子类型 wupinlong add 2012/06/04
	 */
	public static String TYPE_BUSINESS_SUBTYPE_TONGLINK = "tonglink";
	/**
	 * 示意链路
	 */
	public static String TYPE_LINK_SUBTYPE_HIN = "hl";
	
	/**
	 * 负载均衡器子类型
	 */
	public static String TYPE_BALANCER_SANGFOR = "sangfor";// 深信服负载均衡器
	/**
	 * 上网行为管理
	 */
	public static String TYPE_BEHAVIOR_SUBTYPE_SANGFOR = "sangfor";//深信服
	/**
	 * 流量管理
	 */
	public static String TYPE_FLOWCONTROL_SUBTYPE_DHC = "dhc";// 东华天鹰
	/**
	 * 抗DDOS设备
	 */
	public static String TYPE_DDOS_SUBTYPE_NSFOCUS = "nsfocus";// 绿盟科技
	/**
	 * 漏洞扫描
	 */
	public static String TYPE_XSCAN_SUBTYPE_VENUSTECH = "venustech";// 启明星辰
	/**
	 * 安全日志审计
	 */
	public static String TYPE_LOGAUDIT_SUBTYPE_TOPSEC = "topsec";// 天融信
	/**
	 * 实体链路
	 */
	public static String TYPE_LINK_SUBTYPE_LINK = "";
}