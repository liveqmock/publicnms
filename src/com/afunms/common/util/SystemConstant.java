/**
 * <p>Description:constant class</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-06
 */

package com.afunms.common.util;

import java.util.Hashtable;

public class SystemConstant
{
   private SystemConstant()
   {
   }

   public static final int ISRouter = 1;  //路由
   public static final int ISBridge = 2;  //STP   
   public static final int ISCDP = 3;  //CDP
   public static final int ISNDP = 4;  //NDP
   public static final int ISMac = 5;  //MAC

   public static final Integer ISByHand = 10;  //手工
   
   public static final int PHYSICALLINK = 0;  //物理连接
   public static final int LOGICALLINK = 1;  //逻辑连接
   
   public static final int NONEPHYSICALLINK = 1;  //开始端和结束端是都不是物理连接
   public static final int STARTPHYSICALLINK = 2;  //开始端是物理连接,结束端是逻辑连接
   public static final int ENDPHYSICALLINK = 3;  //开始端是逻辑连接,结束端是物理连接
   public static final int BOTHPHYSICALLINK = 4;  //开始端和结束端是都是物理连接
   
   public static final int SNMP_IF_TYPE_ETHERNET = 6;

   public static final int SNMP_IF_TYPE_PROP_VIRTUAL = 53;

   public static final int SNMP_IF_TYPE_L2_VLAN = 135;

   public static final int SNMP_IF_TYPE_L3_VLAN = 136;
   
   public static final int RUNNING2NET = 3; //3 running2Net
   public static final int STARTUP2NET = 6; //6 startup2Net
   
   public static final int STARTUP2CONFIG = 3; //3 startup2config
   public static final int RUNNING2CONFIG = 4; //4 running2config
   
   public static final int COLLECTTYPE_SNMP = 1; //SNMP采集方式
   public static final int COLLECTTYPE_SHELL = 2; //SHELL采集方式
   public static final int COLLECTTYPE_PING = 3; //PING采集方式
   public static final int COLLECTTYPE_REMOTEPING = 4; //远程PING采集方式
   public static final int COLLECTTYPE_WMI = 5; //WMI采集方式
   public static final int COLLECTTYPE_TELNET = 6;  // Telnet 采集方式
   public static final int COLLECTTYPE_SSH = 7;     // SSH 采集方式
   public static final int COLLECTTYPE_TELNETCONNECT = 8;     // 只TELNET检测可用性
   public static final int COLLECTTYPE_SSHCONNECT = 9;     // 只SSH检测可用性
   public static final int COLLECTTYPE_DATAINTERFACE = 10;     // 数据接口
   
   public static final int DBCOLLECTTYPE_JDBC = 1; //JDBC采集方式
   public static final int DBCOLLECTTYPE_SHELL = 2; //脚本采集方式
   
   public static final int OSTYPE_WINDOWS = 5; //WINDOWS
   public static final int OSTYPE_AIX = 6; //AIX
   public static final int OSTYPE_HPUNIX = 7; //HPUNIX
   public static final int OSTYPE_SOLARIS = 8; //SOLARIS
   public static final int OSTYPE_LINUX = 9; //LINUX
   
   public static final int MACBAND_BASE = 0; //基线
   public static final int MACBAND_IPMAC = 1; //IP-MAC绑定
   public static final int MACBAND_PORTMAC = 2; //端口-MAC绑定
   public static final int MACBAND_IPPORTMAC = 3; //IP-端口-MAC绑定
   
   public static String DBType = "mysql"; 
   
	  public static Hashtable COLLECTTYPE = null;
	  static {
		  COLLECTTYPE = new Hashtable();
		  COLLECTTYPE.put("1", "SNMP");
		  COLLECTTYPE.put("2", "代理");
		  COLLECTTYPE.put("3", "PING");
		  COLLECTTYPE.put("4", "远程PING");
		  COLLECTTYPE.put("5", "WMI");
		  COLLECTTYPE.put("6", "Telnet");
		  COLLECTTYPE.put("7", "SSH");
		  COLLECTTYPE.put("8", "TELNET检测可用性");
		  COLLECTTYPE.put("9", "SSH检测可用性");

	  }
	  
	  public static Hashtable OSTYPE = null;
	  static {
		  OSTYPE = new Hashtable();
		  OSTYPE.put("1", "Cisco");
		  OSTYPE.put("2", "H3C");
		  OSTYPE.put("3", "Entrasys");
		  OSTYPE.put("4", "Radware");
		  OSTYPE.put("5", "WINDOWS");
		  OSTYPE.put("6", "AIX");
		  OSTYPE.put("7", "HPUNIX");
		  OSTYPE.put("8", "SOLARIS");
		  OSTYPE.put("9", "LINUX");
		  OSTYPE.put("10", "MAIPU");
	  }
	  
	  public static Hashtable TYPE = null;
	  static {
		  TYPE = new Hashtable();
		  TYPE.put("1", "路由器");
		  TYPE.put("2", "路由交换机");
		  TYPE.put("3", "交换机");
		  TYPE.put("4", "服务器");
		  TYPE.put("8", "防火墙");
	  }
	public static String getDBType() {
		return DBType;
	}
	public static void setDBType(String type) {
		DBType = type;
	}
   
   
   
   
   
   
}
