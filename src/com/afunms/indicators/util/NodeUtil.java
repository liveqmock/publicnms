package com.afunms.indicators.util;

import java.util.ArrayList;
import java.util.List;

import com.afunms.application.dao.ApacheConfigDao;
import com.afunms.application.dao.CicsConfigDao;
import com.afunms.application.dao.DBDao;
import com.afunms.application.dao.DBTypeDao;
import com.afunms.application.dao.DHCPConfigDao;
import com.afunms.application.dao.DnsConfigDao;
import com.afunms.application.dao.DominoConfigDao;
import com.afunms.application.dao.EmailConfigDao;
import com.afunms.application.dao.FTPConfigDao;
import com.afunms.application.dao.IISConfigDao;
import com.afunms.application.dao.JBossConfigDao;
import com.afunms.application.dao.MQConfigDao;
import com.afunms.application.dao.PSTypeDao;
import com.afunms.application.dao.ResinDao;
import com.afunms.application.dao.TFTPConfigDao;
import com.afunms.application.dao.TomcatDao;
import com.afunms.application.dao.TuxedoConfigDao;
import com.afunms.application.dao.WasConfigDao;
import com.afunms.application.dao.WebConfigDao;
import com.afunms.application.dao.WeblogicConfigDao;
import com.afunms.application.model.ApacheConfig;
import com.afunms.application.model.CicsConfig;
import com.afunms.application.model.DBTypeVo;
import com.afunms.application.model.DBVo;
import com.afunms.application.model.DHCPConfig;
import com.afunms.application.model.DnsConfig;
import com.afunms.application.model.DominoConfig;
import com.afunms.application.model.DpConfig;
import com.afunms.application.model.EmailMonitorConfig;
import com.afunms.application.model.FTPConfig;
import com.afunms.application.model.GgsciConfig;
import com.afunms.application.model.IISConfig;
import com.afunms.application.model.JBossConfig;
import com.afunms.application.model.MQConfig;
import com.afunms.application.model.NasConfig;
import com.afunms.application.model.NtpConfig;
import com.afunms.application.model.OracleEntity;
import com.afunms.application.model.PSTypeVo;
import com.afunms.application.model.Resin;
import com.afunms.application.model.TFTPConfig;
import com.afunms.application.model.Tomcat;
import com.afunms.application.model.TongLink;
import com.afunms.application.model.TuxedoConfig;
import com.afunms.application.model.WasConfig;
import com.afunms.application.model.WebConfig;
import com.afunms.application.model.WeblogicConfig;
import com.afunms.application.model.webloginConfig;
import com.afunms.common.base.BaseUtil;
import com.afunms.common.base.BaseVo;
import com.afunms.common.util.BidSQLUitl;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SystemConstant;
import com.afunms.config.model.Business;
import com.afunms.config.model.Procs;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Bussiness;
import com.afunms.polling.node.DBNode;
import com.afunms.polling.node.DHCP;
import com.afunms.polling.node.DNS;
import com.afunms.polling.node.Domino;
import com.afunms.polling.node.Dp;
import com.afunms.polling.node.Ftp;
import com.afunms.polling.node.Ggsci;
import com.afunms.polling.node.Host;
import com.afunms.polling.node.IIS;
import com.afunms.polling.node.MQ;
import com.afunms.polling.node.Mail;
import com.afunms.polling.node.Nas;
import com.afunms.polling.node.Ntp;
import com.afunms.polling.node.Proces;
import com.afunms.polling.node.SocketService;
import com.afunms.polling.node.TFtp;
import com.afunms.polling.node.UPSNode;
import com.afunms.polling.node.Was;
import com.afunms.polling.node.Web;
import com.afunms.polling.node.WebLogin;
import com.afunms.polling.node.Weblogic;
import com.afunms.security.dao.MgeUpsDao;
import com.afunms.security.model.MgeUps;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.dao.ManageXmlDao;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.model.ManageXml;

/**
 * 此类为 通过 类型 和 子类型来 获取 设备
 * 
 * @author Administrator
 * 
 */

public class NodeUtil {

	// private List<NodeDTO> nodeDTOList;

	private String bid;

	private String monitorFlag;

	private boolean isSetedBid = false;

	private boolean isSetedMonitorFlag = false;

	public NodeUtil() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param bid
	 */
	public NodeUtil(String bid) {
		this.setBid(bid);
	}

	/**
	 * @return the bid
	 */
	public String getBid() {
		return bid;
	}

	/**
	 * @param bid
	 *            the bid to set
	 */
	public void setBid(String bid) {
		this.bid = bid;
		this.isSetedBid = true;
	}

	/**
	 * 根据类型 和 子类型 来获取设备 返回 BaseVo 的 列表
	 * 
	 * @param type
	 * @param subtype
	 * @return
	 */
	public List<BaseVo> getNodeByTyeAndSubtype(String type, String subtype) {
		List<BaseVo> list = new ArrayList<BaseVo>();
		if (Constant.ALL_TYPE.equals(type)) {
			list.addAll(getNetList());
			list.addAll(getFirewallList());
			list.addAll(getAtmList());
			list.addAll(getGatewayList());
			list.addAll(getF5List());
			list.addAll(getHostList());
			list.addAll(getDBList());
			list.addAll(getMiddlewareList());
			list.addAll(getServiceList());
			list.addAll(getUpsList());
			list.addAll(getBalancerList());
			list.addAll(getAirList());
			list.addAll(getStorageList());
			list.addAll(getVMWareList());

		} else if (Constant.TYPE_NET.equals(type)) {
			// 网络设备
			if (Constant.TYPE_NET_SUBTYPE_CISCO.equals(subtype)) {
				list.addAll(getNet_CiscoList());
			} else if (Constant.TYPE_NET_SUBTYPE_BDCOM.equals(subtype)) {
				list.addAll(getNet_BdcomlList());
			} else if (Constant.TYPE_NET_SUBTYPE_DLINK.equals(subtype)) {
				list.addAll(getNet_DlinklList());
			} else if (Constant.TYPE_NET_SUBTYPE_ENTRASYS.equals(subtype)) {
				list.addAll(getNet_EntrasysList());
			} else if (Constant.TYPE_NET_SUBTYPE_H3C.equals(subtype)) {
				list.addAll(getNet_H3cList());
			} else if (Constant.TYPE_NET_SUBTYPE_ZTE.equals(subtype)) {
				list.addAll(getNet_ZtelList());
			} else if (Constant.TYPE_NET_SUBTYPE_MAIPU.equals(subtype)) {
				list.addAll(getNet_MaipuList());
			} else if (Constant.TYPE_NET_SUBTYPE_NORTHTEL.equals(subtype)) {
				list.addAll(getNet_NorthtelList());
			} else if (Constant.TYPE_NET_SUBTYPE_RADWARE.equals(subtype)) {
				list.addAll(getNet_RadwareList());
			} else if (Constant.TYPE_NET_SUBTYPE_REDGIANT.equals(subtype)) {
				list.addAll(getNet_RedgiantList());
			} else if (Constant.TYPE_NET_SUBTYPE_IBM.equals(subtype)) {
				list.addAll(getNet_IbmList());
			} else if (Constant.TYPE_NET_SUBTYPE_BROCADE.equals(subtype)) {
				list.addAll(getNet_BrocadeList());
			} else if (Constant.TYPE_NET_SUBTYPE_OTHER.equals(subtype)) {
				list.addAll(getNet_OtherlList());
			} else {
				list.addAll(getNetList());
			}
		} else if (Constant.TYPE_HOST.equals(type)) {
			// 服务器
			if (Constant.TYPE_HOST_SUBTYPE_WINDOWS.equals(subtype)) {
				list.addAll(getHost_WindowsList());
			} else if (Constant.TYPE_HOST_SUBTYPE_AIX.equals(subtype)) {
				list.addAll(getHost_AixList());
			} else if (Constant.TYPE_HOST_SUBTYPE_HPUNIX.equals(subtype)) {
				list.addAll(getHost_HpunixList());
			} else if (Constant.TYPE_HOST_SUBTYPE_AS400.equals(subtype)) {
				list.addAll(getHost_As400List());
			} else if (Constant.TYPE_HOST_SUBTYPE_LINUX.equals(subtype)) {
				list.addAll(getHost_LinuxList());
			} else if (Constant.TYPE_HOST_SUBTYPE_SOLARIS.equals(subtype)) {
				list.addAll(getHost_SolarisList());
			} else if (Constant.TYPE_HOST_SUBTYPE_SCOUNIX.equals(subtype)) {
				list.addAll(getHost_ScounixList());
			} else if (Constant.TYPE_HOST_SUBTYPE_SCOOPENSERVER.equals(subtype)) {
				list.addAll(getHost_ScoopenserverList());
			} else {
				list.addAll(getHostList());
			}
		} else if (Constant.TYPE_DB.equals(type)) {
			// 数据库
			if (Constant.TYPE_DB_SUBTYPE_ORACLE.equalsIgnoreCase(subtype)) {
				list.addAll(getDB_OracleList());
			} else if (Constant.TYPE_DB_SUBTYPE_ORACLERAC.equalsIgnoreCase(subtype)) {
				list.addAll(getDB_OracleRacList());
			}else if (Constant.TYPE_DB_SUBTYPE_DB2.equalsIgnoreCase(subtype)) {
				list.addAll(getDB_DB2List());
			} else if (Constant.TYPE_DB_SUBTYPE_MYSQL.equalsIgnoreCase(subtype)) {
				list.addAll(getDB_MysqlList());
			} else if (Constant.TYPE_DB_SUBTYPE_SQLSERVER.equalsIgnoreCase(subtype)) {
				list.addAll(getDB_SQLServerList());
			} else if (Constant.TYPE_DB_SUBTYPE_SYBASE.equalsIgnoreCase(subtype)) {
				list.addAll(getDB_SybaseList());
			} else if (Constant.TYPE_DB_SUBTYPE_INFORMIX.equalsIgnoreCase(subtype)) {
				list.addAll(getDB_InformixList());
			} else {
				list.addAll(getDBList());
			}
		} else if (Constant.TYPE_MIDDLEWARE.equals(type)) {
			// 中间件
			if (Constant.TYPE_MIDDLEWARE_SUBTYPE_APACHE.equals(subtype)) {
				list.addAll(getMiddleware_ApacheList());
			} else if (Constant.TYPE_MIDDLEWARE_SUBTYPE_CICS.equals(subtype)) {
				list.addAll(getMiddleware_CICSList());
			} else if (Constant.TYPE_MIDDLEWARE_SUBTYPE_DNS.equals(subtype)) {
				list.addAll(getMiddleware_DNSList());
			} else if (Constant.TYPE_MIDDLEWARE_SUBTYPE_DOMINO.equals(subtype)) {
				list.addAll(getMiddleware_DominoList());
			} else if (Constant.TYPE_MIDDLEWARE_SUBTYPE_IIS.equals(subtype)) {
				list.addAll(getMiddleware_IISList());
			} else if (Constant.TYPE_MIDDLEWARE_SUBTYPE_JBOSS.equals(subtype)) {
				list.addAll(getMiddleware_JBossList());
			} else if (Constant.TYPE_MIDDLEWARE_SUBTYPE_MQ.equals(subtype)) {
				list.addAll(getMiddleware_MQList());
			} else if (Constant.TYPE_MIDDLEWARE_SUBTYPE_TOMCAT.equals(subtype)) {
				list.addAll(getMiddleware_TomcatList());
			} else if (Constant.TYPE_MIDDLEWARE_SUBTYPE_TUXEDO.equals(subtype)) {
				list.addAll(getMiddleware_TuxedoList());
			} else if (Constant.TYPE_MIDDLEWARE_SUBTYPE_WAS.equals(subtype)) {
				list.addAll(getMiddleware_WasList());
			} else if (Constant.TYPE_MIDDLEWARE_SUBTYPE_WEBLOGIC.equals(subtype)) {
				// SysLogger.info("########################1");
				list.addAll(getMiddleware_WeblogicList());
			} else if (Constant.TYPE_MIDDLEWARE_SUBTYPE_RESIN.equals(subtype)) {
				// SysLogger.info("########################1");
				list.addAll(getMiddleware_ResinList());
			} else {
				list.addAll(getMiddlewareList());
			}
		} else if (Constant.TYPE_SERVICE.equals(type)) {
			// 服务
			if (Constant.TYPE_SERVICE_SUBTYPE_EMAIL.equals(subtype)) {
				list.addAll(getService_EmailList());
			} else if (Constant.TYPE_SERVICE_SUBTYPE_FTP.equals(subtype)) {
				list.addAll(getService_FtpList());
			} else if (Constant.TYPE_SERVICE_SUBTYPE_TFTP.equals(subtype)) {
				list.addAll(getService_TFtpList());
			} else if (Constant.TYPE_SERVICE_SUBTYPE_DHCP.equals(subtype)) {
				list.addAll(getService_DHCPList());
			} else if (Constant.TYPE_SERVICE_SUBTYPE_HOSTPROCESS.equals(subtype)) {
				// list.addAll(getService_());
			} else if (Constant.TYPE_SERVICE_SUBTYPE_PORT.equals(subtype)) {
				list.addAll(getService_PSTypeVoList());
			} else if (Constant.TYPE_SERVICE_SUBTYPE_WEB.equals(subtype)) {
				list.addAll(getService_WebList());
			} else {
				list.addAll(getServiceList());
			}
		} else if (Constant.TYPE_UPS.equals(type)) {
			// UPS
			if (Constant.TYPE_UPS_SUBTYPE_EMS.equals(subtype)) {
				list.addAll(getUps_EmsList());
			} else if (Constant.TYPE_UPS_SUBTYPE_MGE.equals(subtype)) {
				list.addAll(getUps_MgeList());
			} else {
				list.addAll(getUpsList());
			}
		} else if (Constant.TYPE_AIR.equals(type)) {
			// 空调
			if (Constant.TYPE_AIR_SUBTYPE_EMS.equals(subtype)) {
				list.addAll(getAir_EmsList());
			} else {
				list.addAll(getAirList());
			}
		} else if (Constant.TYPE_ATM.equals(type)) {
			// ems
			if (Constant.TYPE_AIR_SUBTYPE_EMS.equals(subtype)) {
				list.addAll(getAtmList());
			} else {
				list.addAll(getAtmList());
			}
		} else if (Constant.TYPE_VPN.equals(type)) {
			// VPN
			if (Constant.TYPE_VPN_SUBTYPE_ARRAYNETWORKS.equals(subtype)) {
				list.addAll(getVPNList());
			} else {
				list.addAll(getVPNList());
			}
		} else if (Constant.TYPE_GATEWAY.equals(type)) {
			// 空调
			if (Constant.TYPE_AIR_SUBTYPE_EMS.equals(subtype)) {
				list.addAll(getGateway_CiscoList());
			} else {
				list.addAll(getGatewayList());
			}
		} else if (Constant.TYPE_BALANCER.equals(type)) {
			// 负载均衡器
			if (Constant.TYPE_F5_SUBTYPE_F5.equals(subtype)) {
				list.addAll(getF5List());
			} else if (Constant.TYPE_BALANCER_SANGFOR.equals(subtype)) {
				list.addAll(getSangforList());// 现只有一种类型
			} else {
				list.addAll(getSangforList());// 现只有一种类型
			}
		} else if (Constant.TYPE_BEHAVIOR.equals(type)) {
			type = Constant.TYPE_BEHAVIOR;
			if (Constant.TYPE_BEHAVIOR_SUBTYPE_SANGFOR.equals(subtype)) {
				list.addAll(getBehaviorList());// 现只有一种类型
			} else {
				list.addAll(getBehaviorList());// 现只有一种类型
			}
		} else if (Constant.TYPE_FLOWCONTROL.equals(type)) {
			type = Constant.TYPE_FLOWCONTROL;
			if (Constant.TYPE_FLOWCONTROL_SUBTYPE_DHC.equals(subtype)) {
				list.addAll(getFlowcontrolList());// 现只有一种类型
			} else {
				list.addAll(getFlowcontrolList());// 现只有一种类型
			}
		} else if (Constant.TYPE_DDOS.equals(type)) {
			type = Constant.TYPE_DDOS;
			if (Constant.TYPE_DDOS_SUBTYPE_NSFOCUS.equals(subtype)) {
				list.addAll(getDdosList());// 现只有一种类型
			} else {
				list.addAll(getDdosList());// 现只有一种类型
			}
		} else if (Constant.TYPE_XSCAN.equals(type)) {
			type = Constant.TYPE_XSCAN;
			if (Constant.TYPE_XSCAN_SUBTYPE_VENUSTECH.equals(subtype)) {
				list.addAll(getXscanList());// 现只有一种类型
			} else {
				list.addAll(getXscanList());// 现只有一种类型
			}
		} else if (Constant.TYPE_LOGAUDIT.equals(type)) {
			type = Constant.TYPE_LOGAUDIT;
			if (Constant.TYPE_LOGAUDIT_SUBTYPE_TOPSEC.equals(subtype)) {
				list.addAll(getLogauditList());// 现只有一种类型
			} else {
				list.addAll(getLogauditList());// 现只有一种类型
			}
		} else if (Constant.TYPE_FIREWALL.equals(type)) {
			// 防火墙
			if (Constant.TYPE_FIREWALL_SUBTYPE_HILLSTONE.equals(subtype)) {
				list.addAll(getNet_HillstoneList());
			} else if (Constant.TYPE_FIREWALL_SUBTYPE_NETSCREEN.equals(subtype)) {
				list.addAll(getNet_NetscreenList());
			} else if (Constant.TYPE_FIREWALL_SUBTYPE_NOKIA.equals(subtype)) {
				list.addAll(getNet_NokiaList());

			} else if (Constant.TYPE_FIREWALL_SUBTYPE_VENUS.equals(subtype)) {
				list.addAll(getNet_VenusList());
				// } else
				// if(Constant.TYPE_FIREWALL_SUBTYPE_NIPS.equals(subtype)){
				// list.addAll(getNet_VenusList());
			} else if (Constant.TYPE_FIREWALL_SUBTYPE_TIPPINGPOINT.equals(subtype)) {
				list.addAll(getNet_TippingPointList());
			} else if (Constant.TYPE_FIREWALL_SUBTYPE_SECWORLD.equals(subtype)) {
				list.addAll(getNet_SecWorldList());

			} else if (Constant.TYPE_FIREWALL_SUBTYPE_TOPSEC.equals(subtype)) {
				list.addAll(getNet_TopSecList());

			} else if (Constant.TYPE_FIREWALL_SUBTYPE_SECGAGE.equals(subtype)) {
				list.addAll(getNet_SecGateList());

			} else if (Constant.TYPE_FIREWALL_SUBTYPE_DPTECH.equals(subtype)) {
				list.addAll(getNet_DptechList());
			}

			else {
				list.addAll(getFirewallList());
			}
		} else if (Constant.TYPE_STORAGE.equalsIgnoreCase(type)) {
			if (Constant.TYPE_STORAGE_SUBTYPE_HDS.equals(subtype)) {
				// HDS
				list.addAll(getNet_HDSList());
			}
		} else if (Constant.TYPE_VIRTUAL.equalsIgnoreCase(type)) {
			if (Constant.TYPE_VIRTUAL_SUBTYPE_VMWARE.equals(subtype)) {
				// VMWare
				list.addAll(getNet_VMWareList());
			}
		}

		return list;
	}

	public List<BaseVo> getNet_SecWorldList() {
		// SecWorld

		List<BaseVo> list = getHostNode(getNet_SecWorldSql());

		if (list == null) {
			list = new ArrayList<BaseVo>();
		}

		return list;
	}

	public String getNet_SecWorldSql() {
		// Venus

		String sql = " where sys_oid like '1.3.6.1.4.1.24968%' and category=8";

		sql = sql + getMonFlagSql("monflag");

		sql = sql + getBidSql(BidSQLUitl.HOST_NODE);

		return sql;
	}

	public List<BaseVo> getNet_TopSecList() {
		// SecWorld

		List<BaseVo> list = getHostNode(getNet_TopSecSql());

		if (list == null) {
			list = new ArrayList<BaseVo>();
		}

		return list;
	}

	public String getNet_TopSecSql() {
		// Venus

		String sql = " where sys_oid like '1.3.6.1.4.1.14331%' and category=8";

		sql = sql + getMonFlagSql("monflag");

		sql = sql + getBidSql(BidSQLUitl.HOST_NODE);

		return sql;
	}

	public List<BaseVo> getNet_SecGateList() {
		// SecGate

		List<BaseVo> list = getHostNode(getNet_SecGateSql());

		if (list == null) {
			list = new ArrayList<BaseVo>();
		}

		return list;
	}

	public String getNet_SecGateSql() {

		String sql = " where sys_oid like '1.3.6.1.4.1.24968%' and category=8";

		sql = sql + getMonFlagSql("monflag");

		sql = sql + getBidSql(BidSQLUitl.HOST_NODE);

		return sql;
	}

	public List<BaseVo> getNet_TippingPointList() {
		// ips

		List<BaseVo> list = getHostNode(getNet_TippingPointSql());

		if (list == null) {
			list = new ArrayList<BaseVo>();
		}

		return list;
	}

	public String getNet_TippingPointSql() {
		// ips

		String sql = " where sys_oid like '1.3.6.1.4.1.10734%' and category=8";

		sql = sql + getMonFlagSql("monflag");

		sql = sql + getBidSql(BidSQLUitl.HOST_NODE);

		return sql;
	}

	/**
	 * 获取 venus 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getNet_VenusList() {
		// venus

		List<BaseVo> list = getHostNode(getNet_VenusSql());

		if (list == null) {
			list = new ArrayList<BaseVo>();
		}

		return list;
	}

	public String getNet_VenusSql() {
		// Venus

		String sql = " where sys_oid like '1.3.6.1.4.1.15227%' and category=8";

		sql = sql + getMonFlagSql("monflag");

		sql = sql + getBidSql(BidSQLUitl.HOST_NODE);

		return sql;
	}

	/**
	 * 通过 BaseVo 列表 的 转换成 NodeDTO 列表
	 * 
	 * @param type
	 * @param subtype
	 * @return
	 */
	public List<NodeDTO> conversionToNodeDTO(List<BaseVo> list) {
		List<NodeDTO> nodeDTOList = null;
		if (list != null) {
			nodeDTOList = new ArrayList<NodeDTO>();
			for (int i = 0; i < list.size(); i++) {
				nodeDTOList.add(conversionToNodeDTO(list.get(i)));
			}
		}
		return nodeDTOList;
	}

	/**
	 * 通过 BaseVo 转换成 NodeDTO
	 * 
	 * @param type
	 * @param subtype
	 * @return
	 */
	public NodeDTO conversionToNodeDTO(BaseVo baseVo) {

		if (baseVo == null) {
			return null;
		}

		NodeDTO nodeDTO = null;

		if (baseVo instanceof HostNode) {
			HostNode hostNode = (HostNode) baseVo;
			nodeDTO = creatNodeDTOByNode(hostNode);
		} else if (baseVo instanceof DBVo) {
			DBVo dbVo = (DBVo) baseVo;
			nodeDTO = creatNodeDTOByNode(dbVo);
		} else if (baseVo instanceof OracleEntity) {
			OracleEntity oracleEntity = (OracleEntity) baseVo;
			nodeDTO = creatNodeDTOByNode(oracleEntity);
		} else if (baseVo instanceof ApacheConfig) {
			ApacheConfig apacheConfig = (ApacheConfig) baseVo;
			nodeDTO = creatNodeDTOByNode(apacheConfig);
		} else if (baseVo instanceof CicsConfig) {
			CicsConfig cicsConfig = (CicsConfig) baseVo;
			nodeDTO = creatNodeDTOByNode(cicsConfig);
		} else if (baseVo instanceof DnsConfig) {
			DnsConfig dnsConfig = (DnsConfig) baseVo;
			nodeDTO = creatNodeDTOByNode(dnsConfig);
		} else if (baseVo instanceof DominoConfig) {
			DominoConfig dominoConfig = (DominoConfig) baseVo;
			nodeDTO = creatNodeDTOByNode(dominoConfig);
		} else if (baseVo instanceof IISConfig) {
			IISConfig iISConfig = (IISConfig) baseVo;
			nodeDTO = creatNodeDTOByNode(iISConfig);
		} else if (baseVo instanceof JBossConfig) {
			JBossConfig jBossConfig = (JBossConfig) baseVo;
			nodeDTO = creatNodeDTOByNode(jBossConfig);
		} else if (baseVo instanceof MQConfig) {
			MQConfig mQConfig = (MQConfig) baseVo;
			nodeDTO = creatNodeDTOByNode(mQConfig);
		} else if (baseVo instanceof Tomcat) {
			Tomcat tomcat = (Tomcat) baseVo;
			nodeDTO = creatNodeDTOByNode(tomcat);
		} else if (baseVo instanceof TuxedoConfig) {
			TuxedoConfig tuxedoConfig = (TuxedoConfig) baseVo;
			nodeDTO = creatNodeDTOByNode(tuxedoConfig);
		} else if (baseVo instanceof WasConfig) {
			WasConfig wasConfig = (WasConfig) baseVo;
			nodeDTO = creatNodeDTOByNode(wasConfig);
		} else if (baseVo instanceof WeblogicConfig) {
			WeblogicConfig weblogicConfig = (WeblogicConfig) baseVo;
			nodeDTO = creatNodeDTOByNode(weblogicConfig);
		} else if (baseVo instanceof EmailMonitorConfig) {
			EmailMonitorConfig emailMonitorConfig = (EmailMonitorConfig) baseVo;
			nodeDTO = creatNodeDTOByNode(emailMonitorConfig);
		} else if (baseVo instanceof FTPConfig) {
			FTPConfig fTPConfig = (FTPConfig) baseVo;
			nodeDTO = creatNodeDTOByNode(fTPConfig);
		} else if (baseVo instanceof TFTPConfig) {
			TFTPConfig tfTPConfig = (TFTPConfig) baseVo;
			nodeDTO = creatNodeDTOByNode(tfTPConfig);
		} else if (baseVo instanceof DHCPConfig) {
			DHCPConfig dhcpConfig = (DHCPConfig) baseVo;
			nodeDTO = creatNodeDTOByNode(dhcpConfig);
		} else if (baseVo instanceof Procs) {
			Procs procs = (Procs) baseVo;
			nodeDTO = creatNodeDTOByNode(procs);
		} else if (baseVo instanceof PSTypeVo) {
			PSTypeVo pSTypeVo = (PSTypeVo) baseVo;
			nodeDTO = creatNodeDTOByNode(pSTypeVo);
		} else if (baseVo instanceof WebConfig) {
			WebConfig webConfig = (WebConfig) baseVo;
			nodeDTO = creatNodeDTOByNode(webConfig);
		}else if (baseVo instanceof DpConfig) {
			DpConfig dpConfig = (DpConfig) baseVo;
			nodeDTO = creatNodeDTOByNode(dpConfig);
		} else if (baseVo instanceof NasConfig) {
			NasConfig nasConfig = (NasConfig) baseVo;
			nodeDTO = creatNodeDTOByNode(nasConfig);
		}else if (baseVo instanceof GgsciConfig) {
			GgsciConfig ggsciConfig = (GgsciConfig) baseVo;
			nodeDTO = creatNodeDTOByNode(ggsciConfig);
		}else if (baseVo instanceof NtpConfig) {
			NtpConfig ntpConfig = (NtpConfig) baseVo;
			nodeDTO = creatNodeDTOByNode(ntpConfig);
		}else if (baseVo instanceof ManageXml) {
			ManageXml manageXml = (ManageXml) baseVo;
			nodeDTO = creatNodeDTOByNode(manageXml);
		} else if (baseVo instanceof MgeUps) {
			MgeUps mgeUps = (MgeUps) baseVo;
			nodeDTO = creatNodeDTOByNode(mgeUps);
		} else if (baseVo instanceof TongLink) {
			TongLink tonglink = (TongLink) baseVo;
			nodeDTO = creatNodeDTOByNode(tonglink);
		} else if (baseVo instanceof webloginConfig) {
			webloginConfig weblogin = (webloginConfig) baseVo;
			nodeDTO = creatNodeDTOByNode(weblogin);
		} else if (baseVo instanceof Resin) {
			Resin resin = (Resin) baseVo;
			nodeDTO = creatNodeDTOByNode(resin);
		}
		if (nodeDTO != null) {
			nodeDTO.setBusinessName(getBusinessNameForNode(nodeDTO.getBusinessId()));
		}

		return nodeDTO;
	}

	/**
	 * 通过 Resin 转换成 NodeDTO
	 * 
	 * @param webConfig
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(com.afunms.polling.node.Resin resin) {
		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(resin.getId());
		nodeDTO.setName(resin.getAlias());
		nodeDTO.setNodeid(String.valueOf(resin.getId()));
		nodeDTO.setBusinessId(resin.getBid());
		nodeDTO.setIpaddress(resin.getIpAddress());
		String type = Constant.TYPE_MIDDLEWARE;
		String subtype = Constant.TYPE_MIDDLEWARE_SUBTYPE_RESIN;

		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);

		return nodeDTO;
	}

	/**
	 * 通过 WebConfig 转换成 NodeDTO
	 * 
	 * @param webConfig
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(webloginConfig webConfig) {

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(webConfig.getId());
		nodeDTO.setName(webConfig.getAlias());
		nodeDTO.setNodeid(String.valueOf(webConfig.getId()));
		nodeDTO.setBusinessId(webConfig.getBid());
		nodeDTO.setIpaddress(webConfig.getUrl());

		String type = Constant.TYPE_SERVICE;
		String subtype = Constant.TYPE_SERVICE_SUBTYPE_WENLOGIN;

		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);

		return nodeDTO;
	}

	public NodeDTO creatNodeDTOByNode(TongLink tongLink) {

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(tongLink.getId());
		nodeDTO.setName(tongLink.getAlias());
		nodeDTO.setNodeid(String.valueOf(tongLink.getId()));
		nodeDTO.setBusinessId(tongLink.getBusinessid());
		nodeDTO.setIpaddress(tongLink.getIpadress());
		String type = Constant.TYPE_BUSINESS;
		String subtype = Constant.TYPE_BUSINESS_SUBTYPE_TONGLINK;

		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);

		return nodeDTO;
	}

	public NodeDTO conversionToNodeDTO(Node node) {

		if (node == null) {
			return null;
		}

		NodeDTO nodeDTO = null;

		if (node instanceof Host) {
			Host host = (Host) node;
			nodeDTO = creatNodeDTOByNode(host);
		} else if (node instanceof Mail) {
			Mail mail = (Mail) node;
			nodeDTO = creatNodeDTOByNode(mail);
		} else if (node instanceof DHCP) {
			DHCP dhcp = (DHCP) node;
			nodeDTO = creatNodeDTOByNode(dhcp);
		} else if (node instanceof Ftp) {
			Ftp ftp = (Ftp) node;
			nodeDTO = creatNodeDTOByNode(ftp);
		} else if (node instanceof TFtp) {
			TFtp tftp = (TFtp) node;
			nodeDTO = creatNodeDTOByNode(tftp);
		} else if (node instanceof SocketService) {
			SocketService socket = (SocketService) node;
			nodeDTO = creatNodeDTOByNode(socket);
		} else if (node instanceof Web) {
			Web web = (Web) node;
			nodeDTO = creatNodeDTOByNode(web);
		} else if (node instanceof Dp) {
			Dp dp = (Dp) node;
			nodeDTO = creatNodeDTOByNode(dp);
		}else if (node instanceof Nas) {
			Nas nas = (Nas) node;
			nodeDTO = creatNodeDTOByNode(nas);
		}else if (node instanceof Ggsci) {
			Ggsci ggsci = (Ggsci) node;
			nodeDTO = creatNodeDTOByNode(ggsci);
		}else if (node instanceof Ntp) {
			Ntp ntp = (Ntp) node;
			nodeDTO = creatNodeDTOByNode(ntp);
		} else if (node instanceof TFtp) {
			TFtp tftp = (TFtp) node;
			nodeDTO = creatNodeDTOByNode(tftp);
		} else if (node instanceof SocketService) {
			SocketService socket = (SocketService) node;
			nodeDTO = creatNodeDTOByNode(socket);
		} else if (node instanceof Web) {
			Web web = (Web) node;
			nodeDTO = creatNodeDTOByNode(web);
		} else if (node instanceof com.afunms.polling.node.Tomcat) {
			com.afunms.polling.node.Tomcat tomcat = (com.afunms.polling.node.Tomcat) node;
			nodeDTO = creatNodeDTOByNode(tomcat);
		} else if (node instanceof Bussiness) {
			Bussiness buss = (Bussiness) node;
			nodeDTO = creatNodeDTOByNode(buss);
		} else if (node instanceof DBNode) {
			DBNode dbNode = (DBNode) node;
			nodeDTO = creatNodeDTOByNode(dbNode);
		} else if (node instanceof Proces) {
			Proces proNode = (Proces) node;
			nodeDTO = creatNodeDTOByNode(proNode);
		} else if (node instanceof Weblogic) {
			Weblogic proNode = (Weblogic) node;
			nodeDTO = creatNodeDTOByNode(proNode);
		} else if (node instanceof Was) {
			Was proNode = (Was) node;
			nodeDTO = creatNodeDTOByNode(proNode);
		} else if (node instanceof MQ) {
			MQ mqNode = (MQ) node;
			nodeDTO = creatNodeDTOByNode(mqNode);
		} else if (node instanceof IIS) {
			IIS iisNode = (IIS) node;
			nodeDTO = creatNodeDTOByNode(iisNode);
		} else if (node instanceof Domino) {
			Domino domino = (Domino) node;
			nodeDTO = creatNodeDTOByNode(domino);
		} else if (node instanceof com.afunms.polling.node.JBossConfig) {
			com.afunms.polling.node.JBossConfig jboss = (com.afunms.polling.node.JBossConfig) node;
			nodeDTO = creatNodeDTOByNode(jboss);
		} else if (node instanceof DNS) {
			DNS dns = (DNS) node;
			nodeDTO = creatNodeDTOByNode(dns);
		} else if (node instanceof com.afunms.polling.node.TuxedoConfig) {
			com.afunms.polling.node.TuxedoConfig tuxedo = (com.afunms.polling.node.TuxedoConfig) node;
			nodeDTO = creatNodeDTOByNode(tuxedo);
		} else if (node instanceof com.afunms.polling.node.ApachConfig) {
			com.afunms.polling.node.ApachConfig apache = (com.afunms.polling.node.ApachConfig) node;
			nodeDTO = creatNodeDTOByNode(apache);
		}else if (node.getType().equals("ups")) {
          nodeDTO = creatNodeDTOByNode(node);
       } else if (node instanceof WebLogin) {
			WebLogin web = (WebLogin) node;
			nodeDTO = creatNodeDTOByNode(web);
		} else if (node instanceof com.afunms.polling.node.Resin) {
			com.afunms.polling.node.Resin resin = (com.afunms.polling.node.Resin) node;
			nodeDTO = creatNodeDTOByNode(resin);
		} else {
		}
		nodeDTO.setBusinessName(getBusinessNameForNode(nodeDTO.getBusinessId()));

		return nodeDTO;
	}
public NodeDTO creatNodeDTOByNode(Node node)
  {
    NodeDTO nodeDTO = new NodeDTO();

    nodeDTO.setId(node.getId());
    nodeDTO.setName(node.getAlias());
    nodeDTO.setNodeid(String.valueOf(node.getId()));
    nodeDTO.setBusinessId(node.getBid());
    nodeDTO.setIpaddress(node.getIpAddress());

    String type = "ups";
    String subtype = "ems";

    nodeDTO.setType(type);
    nodeDTO.setSubtype(subtype);

    return nodeDTO;
  }

	public NodeDTO creatNodeDTOByNode(WebLogin web) {

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(web.getId());
		nodeDTO.setName(web.getAlias());
		nodeDTO.setNodeid(String.valueOf(web.getId()));
		nodeDTO.setBusinessId(web.getBid());
		nodeDTO.setIpaddress(web.getUrl());

		String subtype = "weblogin";

		String type = "service";

		nodeDTO.setSubtype(subtype);
		nodeDTO.setType(type);
		return nodeDTO;
	}

	/**
	 * 通过 MQConfig 转换成 NodeDTO
	 * 
	 * @param mQConfig
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(MQ mQConfig) {

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(mQConfig.getId());
		nodeDTO.setName(mQConfig.getAlias());
		nodeDTO.setNodeid(String.valueOf(mQConfig.getId()));
		nodeDTO.setBusinessId(mQConfig.getNetid());
		nodeDTO.setIpaddress(mQConfig.getIpAddress());

		String type = Constant.TYPE_MIDDLEWARE;
		String subtype = Constant.TYPE_MIDDLEWARE_SUBTYPE_MQ;

		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);

		return nodeDTO;
	}

	public NodeDTO creatNodeDTOByNode(Was was) {

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(was.getId());
		nodeDTO.setName(was.getName());
		nodeDTO.setNodeid(String.valueOf(was.getId()));
		nodeDTO.setBusinessId(was.getBid());
		nodeDTO.setIpaddress(was.getIpaddress());
		String type = Constant.TYPE_MIDDLEWARE;
		String subtype = Constant.TYPE_MIDDLEWARE_SUBTYPE_WAS;

		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);

		return nodeDTO;
	}

	public NodeDTO creatNodeDTOByNode(Weblogic weblogic) {

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(weblogic.getId());
		nodeDTO.setName(weblogic.getAlias());
		nodeDTO.setNodeid(String.valueOf(weblogic.getId()));
		nodeDTO.setBusinessId(weblogic.getBid());
		nodeDTO.setIpaddress(weblogic.getIpAddress());
		String type = Constant.TYPE_MIDDLEWARE;
		String subtype = Constant.TYPE_MIDDLEWARE_SUBTYPE_WEBLOGIC;

		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);

		return nodeDTO;
	}

	public NodeDTO creatNodeDTOByNode(IIS iis) {

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(iis.getId());
		nodeDTO.setName(iis.getAlias());
		nodeDTO.setNodeid(String.valueOf(iis.getId()));
		nodeDTO.setBusinessId(iis.getBid());
		nodeDTO.setIpaddress(iis.getIpAddress());
		String type = Constant.TYPE_MIDDLEWARE;
		String subtype = Constant.TYPE_MIDDLEWARE_SUBTYPE_IIS;

		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);

		return nodeDTO;
	}

	public String converNodeType(int type) {
		String collectType = "";
		if (SystemConstant.COLLECTTYPE_SNMP == type) {
			collectType = "SNMP";
		} else if (SystemConstant.COLLECTTYPE_PING == type) {
			collectType = "PING";
		} else if (SystemConstant.COLLECTTYPE_REMOTEPING == type) {
			collectType = "REMOTEPING";
		} else if (SystemConstant.COLLECTTYPE_SHELL == type) {
			// collectType = "SHELL";
			collectType = "代理";
		} else if (SystemConstant.COLLECTTYPE_SSH == type) {
			collectType = "SSH";
		} else if (SystemConstant.COLLECTTYPE_TELNET == type) {
			collectType = "TELNET";
		} else if (SystemConstant.COLLECTTYPE_WMI == type) {
			collectType = "WMI";
		} else if (SystemConstant.COLLECTTYPE_DATAINTERFACE == type) {
			collectType = "接口";
		}
		return collectType;
	}
	/**
	 * 系统快照状态标志
	 */
	public  String getSnap(int category) {
		String categoryStr = "";
		if (category == 1) {
			// 路由器
			categoryStr="路由器";
		} else if (category == 2) {
			// 交换机
			categoryStr="交换机";
		} else if (category == 3) {
			// 服务器
			categoryStr="服务器";
		} else if (category == 4) {
			// 数据库
			categoryStr="数据库";
		} else if (category == 5) {
			// 中间件
			categoryStr="中间件";
		} else if (category == 6) {
			// 服务
			categoryStr="服务";
		} else if (category == 7) {
			// 防火墙
			categoryStr="防火墙";
		} else if (category == 8) {
			// 数据库
			categoryStr="数据库";
		}
		return categoryStr;
	}
	public NodeDTO creatNodeDTOByNode(Domino domino) {

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(domino.getId());
		nodeDTO.setName(domino.getAlias());
		nodeDTO.setNodeid(String.valueOf(domino.getId()));
		nodeDTO.setBusinessId(domino.getBid());
		nodeDTO.setIpaddress(domino.getIpAddress());
		String type = Constant.TYPE_MIDDLEWARE;
		String subtype = Constant.TYPE_MIDDLEWARE_SUBTYPE_DOMINO;

		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);

		return nodeDTO;
	}

	public NodeDTO creatNodeDTOByNode(com.afunms.polling.node.JBossConfig jboss) {

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(jboss.getId());
		nodeDTO.setName(jboss.getAlias());
		nodeDTO.setNodeid(String.valueOf(jboss.getId()));
		nodeDTO.setBusinessId(jboss.getBid());
		nodeDTO.setIpaddress(jboss.getIpAddress());
		String type = Constant.TYPE_MIDDLEWARE;
		String subtype = Constant.TYPE_MIDDLEWARE_SUBTYPE_JBOSS;

		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);

		return nodeDTO;
	}

	public NodeDTO creatNodeDTOByNode(DNS dns) {

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(dns.getId());
		nodeDTO.setName(dns.getAlias());
		nodeDTO.setNodeid(String.valueOf(dns.getId()));
		nodeDTO.setBusinessId(dns.getBid());
		nodeDTO.setIpaddress(dns.getIpAddress());
		String type = Constant.TYPE_MIDDLEWARE;
		String subtype = Constant.TYPE_MIDDLEWARE_SUBTYPE_DNS;

		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);

		return nodeDTO;
	}

	public NodeDTO creatNodeDTOByNode(com.afunms.polling.node.TuxedoConfig tuxedo) {

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(tuxedo.getId());
		nodeDTO.setName(tuxedo.getAlias());
		nodeDTO.setNodeid(String.valueOf(tuxedo.getId()));
		nodeDTO.setBusinessId(tuxedo.getBid());
		nodeDTO.setIpaddress(tuxedo.getIpAddress());
		String type = Constant.TYPE_MIDDLEWARE;
		String subtype = Constant.TYPE_MIDDLEWARE_SUBTYPE_TUXEDO;

		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);

		return nodeDTO;
	}

	public NodeDTO creatNodeDTOByNode(Proces proces) {

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(proces.getId());
		nodeDTO.setName(proces.getName());
		nodeDTO.setNodeid(String.valueOf(proces.getId()));
		nodeDTO.setBusinessId(proces.getBid());
		nodeDTO.setIpaddress(proces.getIpaddress());
		String type = Constant.TYPE_SERVICE;
		String subtype = Constant.TYPE_SERVICE_SUBTYPE_PROCESS;

		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);

		return nodeDTO;
	}

	public NodeDTO creatNodeDTOByNode(DBNode dbNode) {

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(dbNode.getId());
		nodeDTO.setName(dbNode.getAlias());
		nodeDTO.setNodeid(String.valueOf(dbNode.getId()));
		nodeDTO.setBusinessId(dbNode.getBid());
		nodeDTO.setIpaddress(dbNode.getIpAddress());
		String type = Constant.TYPE_DB;
		String subtype = "";
		DBTypeDao typeDao = new DBTypeDao();
		try {
			DBTypeVo typeVo = (DBTypeVo) typeDao.findByID(String.valueOf(dbNode.getDbtype()));
			if (typeVo == null) {
				subtype = "";
			} else if ("oracle".equalsIgnoreCase(typeVo.getDbtype())) {
				subtype = Constant.TYPE_DB_SUBTYPE_ORACLE;
			} else if ("sqlserver".equalsIgnoreCase(typeVo.getDbtype())) {
				subtype = Constant.TYPE_DB_SUBTYPE_SQLSERVER;
			} else if ("mysql".equalsIgnoreCase(typeVo.getDbtype())) {
				subtype = Constant.TYPE_DB_SUBTYPE_MYSQL;
			} else if ("db2".equalsIgnoreCase(typeVo.getDbtype())) {
				subtype = Constant.TYPE_DB_SUBTYPE_DB2;
			} else if ("sybase".equalsIgnoreCase(typeVo.getDbtype())) {
				subtype = Constant.TYPE_DB_SUBTYPE_SYBASE;
			} else if ("informix".equalsIgnoreCase(typeVo.getDbtype())) {
				subtype = Constant.TYPE_DB_SUBTYPE_INFORMIX;
			} else if ("oraclerac".equalsIgnoreCase(typeVo.getDbtype())) {
				subtype = Constant.TYPE_DB_SUBTYPE_ORACLERAC;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			typeDao.close();
		}
		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);

		return nodeDTO;
	}

	/**
	 * 通过 Bussiness 转换成 NodeDTO
	 * 
	 * @param host
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(Bussiness buss) {

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(buss.getId());
		nodeDTO.setName(buss.getAlias());
		nodeDTO.setIpaddress(String.valueOf(buss.getId()));
		nodeDTO.setNodeid(String.valueOf(buss.getId()));
		nodeDTO.setBusinessId(buss.getBid());

		String subtype = "bus";

		String type = "bussiness";

		nodeDTO.setSubtype(subtype);
		nodeDTO.setType(type);
		return nodeDTO;
	}

	public NodeDTO creatNodeDTOByNode(com.afunms.polling.node.Tomcat tomcat) {

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(tomcat.getId());
		nodeDTO.setName(tomcat.getAlias());
		nodeDTO.setNodeid(String.valueOf(tomcat.getId()));
		nodeDTO.setBusinessId(tomcat.getBid());
		nodeDTO.setIpaddress(tomcat.getIpAddress());
		String type = Constant.TYPE_MIDDLEWARE;
		String subtype = Constant.TYPE_MIDDLEWARE_SUBTYPE_TOMCAT;

		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);

		return nodeDTO;
	}

	/**
	 * 根据 条件获取 hostNode
	 * 
	 * @param condition
	 * @return
	 */
	public List<BaseVo> getHostNode(String condition) {
		HostNodeDao hostNodeDao = new HostNodeDao();
		List<BaseVo> list = null;
		try {
			list = hostNodeDao.findByCondition(condition);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<BaseVo> getUpsNode(String condition) {
		MgeUpsDao mgeUpsDao = new MgeUpsDao();
		List<BaseVo> list = null;
		try {
			list = mgeUpsDao.loadByType(condition);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<BaseVo> getUpsNode(String type, String subtype) {
		MgeUpsDao mgeUpsDao = new MgeUpsDao();
		List<BaseVo> list = null;
		try {
			list = mgeUpsDao.loadByTypeAndSubtype(type, subtype);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	// --------------------------------------------------------------------------------------------
	// 网络设备
	// --------------------------------------------------------------------------------------------

	public List<BaseVo> getUpsList() {
		List<BaseVo> list = getUpsNode("ups");
		return list;
	}

	public List<BaseVo> getAirList() {
		List<BaseVo> list = getUpsNode("air");
		return list;
	}

	public List<BaseVo> getStorageList() {
		List<BaseVo> list = getHostNode(getNet_HDSSql());
		return list;
	}

	public List<BaseVo> getVMWareList() {
		List<BaseVo> list = getHostNode(getNet_VMWareSql());
		return list;
	}

	public List<BaseVo> getUps_EmsList() {
		List<BaseVo> list = getUpsNode("ups", "ems");
		return list;
	}

	public List<BaseVo> getUps_MgeList() {
		List<BaseVo> list = getUpsNode("ups", "mge");
		return list;
	}

	public List<BaseVo> getAir_EmsList() {
		List<BaseVo> list = getUpsNode("air", "ems");
		return list;
	}

	/**
	 * 获取网络设备 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getNetList() {
		List<BaseVo> list = getHostNode(getNetSql());
		return list;
	}

	/**
	 * 获取防火墙设备 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getFirewallList() {
		List<BaseVo> list = getHostNode(getFirewallSql());
		return list;
	}

	/**
	 * 获取邮件安全网关设备 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getGatewayList() {
		List<BaseVo> list = getHostNode(getGatewaySql());
		return list;
	}

	/**
	 * 获取负载均衡设备 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getBalancerList() {
		List<BaseVo> list = getHostNode(getBalancerSql());
		return list;
	}

	/**
	 * 获取负载均衡设备 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getF5List() {
		List<BaseVo> list = getHostNode(getF5Sql());
		return list;
	}

	/**
	 * 获取上网行为管理列表
	 * 
	 * @return
	 */
	public List<BaseVo> getBehaviorList() {
		List<BaseVo> list = getHostNode(getBehaviorSql());
		return list;
	}

	/**
	 * 获取流量控制列表
	 * 
	 * @return
	 */
	public List<BaseVo> getFlowcontrolList() {
		List<BaseVo> list = getHostNode(getFlowcontrolSql());
		return list;
	}

	/**
	 * 获取抗DDOS列表
	 * 
	 * @return
	 */
	public List<BaseVo> getDdosList() {
		List<BaseVo> list = getHostNode(getDdosSql());
		return list;
	}

	/**
	 * 获取漏洞扫描列表
	 * 
	 * @return
	 */
	public List<BaseVo> getXscanList() {
		List<BaseVo> list = getHostNode(getXscanSql());
		return list;
	}

	/**
	 * 获取安全日志审计列表
	 * 
	 * @return
	 */
	public List<BaseVo> getLogauditList() {
		List<BaseVo> list = getHostNode(getLogauditSql());
		return list;
	}

	/**
	 * 获取负载均衡设备 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getSangforList() {
		List<BaseVo> list = getHostNode(getSangforSql());
		return list;
	}

	/**
	 * 获取VPN设备 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getVPNList() {
		List<BaseVo> list = getHostNode(getVPNSql());
		return list;
	}

	public List<BaseVo> getHDSList() {
		List<BaseVo> list = getHostNode(getNet_HDSSql());
		return list;
	}

	/**
	 * 获取 负载均衡器 sql 语句
	 * 
	 * @return
	 */
	public String getBalancerSql() {

		String sql = " where category=11";

		sql = sql + getMonFlagSql("managed");
		sql = sql + getBidSql(BidSQLUitl.HOST_NODE);

		return sql;
	}

	/**
	 * 获取 网络设备 sql 语句
	 * 
	 * @return
	 */
	public String getNetSql() {

		String sql = " where category<4 or category=7";

		sql = sql + getMonFlagSql("managed");
		sql = sql + getBidSql(BidSQLUitl.HOST_NODE);

		return sql;
	}

	/**
	 * 获取 防火墙设备 sql 语句
	 * 
	 * @return
	 */
	public String getFirewallSql() {

		String sql = " where category=8";

		sql = sql + getMonFlagSql("managed");
		sql = sql + getBidSql(BidSQLUitl.HOST_NODE);

		return sql;
	}

	/**
	 * 获取 邮件安全网关设备 sql 语句
	 * 
	 * @return
	 */
	public String getGatewaySql() {

		String sql = " where category=10";

		sql = sql + getMonFlagSql("managed");
		sql = sql + getBidSql(BidSQLUitl.HOST_NODE);

		return sql;
	}

	/**
	 * 获取 负载均衡设备 sql 语句
	 * 
	 * @return
	 */
	public String getF5Sql() {

		String sql = " where category=11";

		sql = sql + getMonFlagSql("managed");
		sql = sql + getBidSql(BidSQLUitl.HOST_NODE);

		return sql;
	}

	/**
	 * 获取 负载均衡设备 sql 语句
	 * 
	 * @return
	 */
	public String getSangforSql() {

		String sql = " where sys_oid like '1.3.6.1.4.1.35047%' and category=11";

		sql = sql + getMonFlagSql("managed");
		sql = sql + getBidSql(BidSQLUitl.HOST_NODE);

		return sql;
	}

	public String getBehaviorSql() {

		String sql = " where sys_oid like 'Sangfor%' and category=13";

		sql = sql + getMonFlagSql("managed");
		sql = sql + getBidSql(BidSQLUitl.HOST_NODE);

		return sql;
	}

	public String getFlowcontrolSql() {

		String sql = " where sys_oid like 'DHC%' and category=14";

		sql = sql + getMonFlagSql("managed");
		sql = sql + getBidSql(BidSQLUitl.HOST_NODE);

		return sql;
	}

	public String getDdosSql() {

		String sql = " where sys_oid like 'Nsfocus%' and category=15";

		sql = sql + getMonFlagSql("managed");
		sql = sql + getBidSql(BidSQLUitl.HOST_NODE);

		return sql;
	}

	public String getXscanSql() {

		String sql = " where sys_oid like 'Venustech%' and category=16";

		sql = sql + getMonFlagSql("managed");
		sql = sql + getBidSql(BidSQLUitl.HOST_NODE);

		return sql;
	}

	public String getLogauditSql() {

		String sql = " where sys_oid like 'TopSec%' and category=17";

		sql = sql + getMonFlagSql("managed");
		sql = sql + getBidSql(BidSQLUitl.HOST_NODE);

		return sql;
	}

	/**
	 * 获取 VPN设备 sql 语句
	 * 
	 * @return
	 */
	public String getVPNSql() {

		String sql = " where category=12";

		sql = sql + getMonFlagSql("managed");
		sql = sql + getBidSql(BidSQLUitl.HOST_NODE);

		return sql;
	}

	/**
	 * 获取 hillstone 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getNet_HillstoneList() {
		// cisco

		List<BaseVo> list = getHostNode(getNet_HillstoneSql());

		if (list == null) {
			list = new ArrayList<BaseVo>();
		}

		return list;
	}

	/**
	 * 获取 cisco邮件安全网关 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getGateway_CiscoList() {
		// cisco

		List<BaseVo> list = getHostNode(getGateway_CiscoSql());

		if (list == null) {
			list = new ArrayList<BaseVo>();
		}

		return list;
	}

	/**
	 * 获取 hillstone Sql 语句
	 * 
	 * @return
	 */
	public String getNet_HillstoneSql() {
		// cisco

		String sql = " where sys_oid like '1.3.6.1.4.1.28557%' and category=8";

		sql = sql + getMonFlagSql("monflag");

		sql = sql + getBidSql(BidSQLUitl.HOST_NODE);

		return sql;
	}

	/**
	 * 获取 cisco邮件安全网关 Sql 语句
	 * 
	 * @return
	 */
	public String getGateway_CiscoSql() {
		// cisco

		String sql = " where sys_oid like '1.3.6.1.4.1.15497%' and category=10";

		sql = sql + getMonFlagSql("monflag");

		sql = sql + getBidSql(BidSQLUitl.HOST_NODE);

		return sql;
	}

	/**
	 * 获取 ibm 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getNet_IbmList() {
		// cisco

		List<BaseVo> list = getHostNode(getNet_IbmSql());

		if (list == null) {
			list = new ArrayList<BaseVo>();
		}

		return list;
	}

	/**
	 * 获取 Brocade 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getNet_BrocadeList() {
		// cisco

		List<BaseVo> list = getHostNode(getNet_BrocadeSql());

		if (list == null) {
			list = new ArrayList<BaseVo>();
		}

		return list;
	}

	/**
	 * 获取 nokia 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getNet_NokiaList() {
		// cisco

		List<BaseVo> list = getHostNode(getNet_NokiaSql());

		if (list == null) {
			list = new ArrayList<BaseVo>();
		}

		return list;
	}

	/**
	 * 获取 ibm Sql 语句
	 * 
	 * @return
	 */
	public String getNet_IbmSql() {
		// cisco

		String sql = " where sys_oid like '1.3.6.1.4.1.1588%' and (category<4 or category=6 or category=7)";

		sql = sql + getMonFlagSql("monflag");

		sql = sql + getBidSql(BidSQLUitl.HOST_NODE);

		return sql;
	}

	/**
	 * 获取 ibm Sql 语句
	 * 
	 * @return
	 */
	public String getNet_BrocadeSql() {
		// cisco

		String sql = " where sys_oid like '1.3.6.1.4.1.1588%' and (category<4 or category=6 or category=7)";

		sql = sql + getMonFlagSql("monflag");

		sql = sql + getBidSql(BidSQLUitl.HOST_NODE);

		return sql;
	}

	/**
	 * 获取 nokia Sql 语句
	 * 
	 * @return
	 */
	public String getNet_NokiaSql() {
		// cisco

		String sql = " where sys_oid like '1.3.6.1.4.1.94%' and category=8";

		sql = sql + getMonFlagSql("monflag");

		sql = sql + getBidSql(BidSQLUitl.HOST_NODE);

		return sql;
	}

	/**
	 * 获取 netscreen 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getNet_NetscreenList() {
		// cisco

		List<BaseVo> list = getHostNode(getNet_NetscreenSql());

		if (list == null) {
			list = new ArrayList<BaseVo>();
		}

		return list;
	}

	/**
	 * 获取 dptech 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getNet_DptechList() {
		// cisco

		List<BaseVo> list = getHostNode(getNet_DptechSql());

		if (list == null) {
			list = new ArrayList<BaseVo>();
		}

		return list;
	}

	public List<BaseVo> getNet_HDSList() {
		// cisco
		List<BaseVo> list = getHostNode(getNet_HDSSql());

		if (list == null) {
			list = new ArrayList<BaseVo>();
		}

		return list;
	}

	public List<BaseVo> getNet_VMWareList() {
		// vmware
		List<BaseVo> list = getHostNode(getNet_VMWareSql());

		if (list == null) {
			list = new ArrayList<BaseVo>();
		}

		return list;
	}

	/**
	 * 获取 dptech Sql 语句
	 * 
	 * @return
	 */
	public String getNet_DptechSql() {
		// cisco

		String sql = " where sys_oid like '1.3.6.1.4.1.31648%' and category=8";

		sql = sql + getMonFlagSql("monflag");

		sql = sql + getBidSql(BidSQLUitl.HOST_NODE);

		return sql;
	}

	public String getNet_HDSSql() {
		// cisco

		String sql = " where  category=14";

		sql = sql + getMonFlagSql("managed");

		sql = sql + getBidSql(BidSQLUitl.HOST_NODE);

		return sql;
	}

	public String getNet_VMWareSql() {
		// vmware

		String sql = " where  category=15 and ostype=40 ";

		sql = sql + getMonFlagSql("managed");

		sql = sql + getBidSql(BidSQLUitl.HOST_NODE);

		return sql;
	}

	/**
	 * 获取 netscreen Sql 语句
	 * 
	 * @return
	 */
	public String getNet_NetscreenSql() {
		// cisco

		String sql = " where sys_oid like '1.3.6.1.4.1.3224%' or '1.3.6.1.4.1.2636%' and category=8";

		sql = sql + getMonFlagSql("monflag");

		sql = sql + getBidSql(BidSQLUitl.HOST_NODE);

		return sql;
	}

	/**
	 * 获取 cisco 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getNet_CiscoList() {
		// cisco

		List<BaseVo> list = getHostNode(getNet_CiscoSql());

		if (list == null) {
			list = new ArrayList<BaseVo>();
		}

		return list;
	}

	/**
	 * 获取 cisco Sql 语句
	 * 
	 * @return
	 */
	public String getNet_CiscoSql() {
		// cisco

		String sql = " where sys_oid like '1.3.6.1.4.1.9.%' and (category<4 or category=7 or category=10)";

		sql = sql + getMonFlagSql("managed");

		sql = sql + getBidSql(BidSQLUitl.HOST_NODE);

		return sql;
	}

	/**
	 * 获取 h3c 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getNet_H3cList() {
		// h3c

		List<BaseVo> list = getHostNode(getNet_H3cSql());

		if (list == null) {
			list = new ArrayList<BaseVo>();
		}

		return list;
	}

	/**
	 * 获取 h3c Sql 语句
	 * 
	 * @return
	 */
	public String getNet_H3cSql() {
		// h3c

		String sql = " where (sys_oid like '1.3.6.1.4.1.2011.%' or sys_oid like '1.3.6.1.4.1.25506.%') and (category<4 or category=7)";

		sql = sql + getMonFlagSql("managed");

		sql = sql + getBidSql(BidSQLUitl.HOST_NODE);

		return sql;
	}

	/**
	 * 获取 entrasys 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getNet_EntrasysList() {
		// entrasys

		List<BaseVo> list = getHostNode(getNet_EntrasysSql());

		if (list == null) {
			list = new ArrayList<BaseVo>();
		}

		return list;
	}

	/**
	 * 获取 entrasys Sql 语句
	 * 
	 * @return
	 */
	public String getNet_EntrasysSql() {

		String sql = " where sys_oid like '1.3.6.1.4.1.9.2.1.57.%' and (category<4 or category=7)";

		sql = sql + getMonFlagSql("managed");

		sql = sql + getBidSql(BidSQLUitl.HOST_NODE);

		return sql;
	}

	/**
	 * 获取 radware 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getNet_RadwareList() {
		// radware

		List<BaseVo> list = getHostNode(getNet_RadwareSql());

		if (list == null) {
			list = new ArrayList<BaseVo>();
		}

		return list;
	}

	/**
	 * 获取 radware Sql 语句
	 * 
	 * @return
	 */
	public String getNet_RadwareSql() {

		String sql = " where sys_oid like '1.3.6.1.4.1.89.%' and (category<4 or category=7)";

		sql = sql + getMonFlagSql("managed");

		sql = sql + getBidSql(BidSQLUitl.HOST_NODE);

		return sql;
	}

	/**
	 * 获取 maipu 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getNet_MaipuList() {
		// maipu

		List<BaseVo> list = getHostNode(getNet_MaipuSql());

		if (list == null) {
			list = new ArrayList<BaseVo>();
		}

		return list;
	}

	/**
	 * 获取 maipu Sql 语句
	 * 
	 * @return
	 */
	public String getNet_MaipuSql() {

		String sql = " where sys_oid like '1.3.6.1.4.1.5651.%' and (category<4 or category=7)";

		sql = sql + getMonFlagSql("managed");

		sql = sql + getBidSql(BidSQLUitl.HOST_NODE);

		return sql;
	}

	/**
	 * 获取 redgiant 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getNet_RedgiantList() {
		// redgiant

		List<BaseVo> list = getHostNode(getNet_RedgiantSql());

		if (list == null) {
			list = new ArrayList<BaseVo>();
		}

		return list;
	}

	/**
	 * 获取 redgiant Sql 语句
	 * 
	 * @return
	 */
	public String getNet_RedgiantSql() {

		String sql = " where sys_oid like '1.3.6.1.4.1.4881.%' and (category<4 or category=7)";

		sql = sql + getMonFlagSql("managed");

		sql = sql + getBidSql(BidSQLUitl.HOST_NODE);

		return sql;
	}

	/**
	 * 获取 northtel 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getNet_NorthtelList() {
		// northtel

		List<BaseVo> list = getHostNode(getNet_NorthtelSql());

		if (list == null) {
			list = new ArrayList<BaseVo>();
		}

		return list;
	}

	/**
	 * 获取 northtel Sql 语句
	 * 
	 * @return
	 */
	public String getNet_NorthtelSql() {

		String sql = " where (sys_oid like '1.3.6.1.4.1.45.%' or sys_oid like '1.3.6.1.4.1.2272.%') and (category<4 or category=7)";

		sql = sql + getMonFlagSql("managed");

		sql = sql + getBidSql(BidSQLUitl.HOST_NODE);

		return sql;
	}

	/**
	 * 获取 dlink 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getNet_DlinklList() {
		// dlink

		List<BaseVo> list = getHostNode(getNet_DlinkSql());

		if (list == null) {
			list = new ArrayList<BaseVo>();
		}

		return list;
	}

	/**
	 * 获取 dlink Sql 语句
	 * 
	 * @return
	 */
	public String getNet_DlinkSql() {

		String sql = " where sys_oid like '1.3.6.1.4.1.171.%' and (category<4 or category=7)";

		sql = sql + getMonFlagSql("managed");

		sql = sql + getBidSql(BidSQLUitl.HOST_NODE);

		return sql;
	}

	/**
	 * 获取 bdcom 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getNet_BdcomlList() {
		// bdcom

		List<BaseVo> list = getHostNode(getNet_BdcomSql());

		if (list == null) {
			list = new ArrayList<BaseVo>();
		}

		return list;
	}

	/**
	 * 获取 bdcom Sql 语句
	 * 
	 * @return
	 */
	public String getNet_BdcomSql() {

		String sql = " where sys_oid like '1.3.6.1.4.1.3320.%' and (category<4 or category=7)";

		sql = sql + getMonFlagSql("managed");

		sql = sql + getBidSql(BidSQLUitl.HOST_NODE);

		return sql;
	}

	/**
	 * 获取 zte 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getNet_ZtelList() {
		// zte

		List<BaseVo> list = getHostNode(getNet_ZteSql());

		if (list == null) {
			list = new ArrayList<BaseVo>();
		}

		return list;
	}

	/**
	 * 获取 zte Sql 语句
	 * 
	 * @return
	 */
	public String getNet_ZteSql() {

		String sql = " where sys_oid like '1.3.6.1.4.1.3902.%'";

		sql = sql + getMonFlagSql("managed");

		sql = sql + getBidSql(BidSQLUitl.HOST_NODE);

		return sql;
	}

	/**
	 * 获取 other 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getNet_OtherlList() {
		// zte

		List<BaseVo> list = getHostNode(getNet_OtherSql());

		if (list == null) {
			list = new ArrayList<BaseVo>();
		}

		return list;
	}

	/**
	 * 获取 other Sql 语句
	 * 
	 * @return
	 */
	public String getNet_OtherSql() {

		String sql = " where sys_oid='other'";

		sql = sql + getMonFlagSql("managed");

		sql = sql + getBidSql(BidSQLUitl.HOST_NODE);

		return sql;
	}

	// --------------------------------------------------------------------------------------------
	// 服务器
	// --------------------------------------------------------------------------------------------

	/**
	 * 获取 服务器 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getHostList() {
		List<BaseVo> list = getHostNode(getHostSql());
		return list;
	}

	/**
	 * 获取 服务器 sql 语句
	 * 
	 * @return
	 */
	public String getHostSql() {

		String sql = " where category=4";

		sql = sql + getMonFlagSql("managed");

		sql = sql + getBidSql(BidSQLUitl.HOST_NODE);

		// SysLogger.info(sql);
		return sql;
	}

	/**
	 * 获取 windows 服务器 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getHost_WindowsList() {
		// windows
		List<BaseVo> list = getHostNode(getHost_WindowsSql());
		return list;
	}

	/**
	 * 获取 windows 服务器 sql 语句
	 * 
	 * @return
	 */
	public String getHost_WindowsSql() {

		String sql = " where sys_oid like '1.3.6.1.4.1.311.%' and category=4";

		sql = sql + getMonFlagSql("managed");

		sql = sql + getBidSql(BidSQLUitl.HOST_NODE);

		return sql;
	}

	/**
	 * 获取 linux 服务器 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getHost_LinuxList() {
		// linux
		List<BaseVo> list = getHostNode(getHost_LinuxSql());
		return list;
	}

	/**
	 * 获取 linux 服务器 sql 语句
	 * 
	 * @return
	 */
	public String getHost_LinuxSql() {

		String sql = " where sys_oid like '1.3.6.1.4.1.2021.%' and category=4";

		sql = sql + getMonFlagSql("managed");

		sql = sql + getBidSql(BidSQLUitl.HOST_NODE);
		// SysLogger.info(sql);
		return sql;
	}

	/**
	 * 获取 aix 服务器 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getHost_AixList() {
		// aix
		List<BaseVo> list = getHostNode(getHost_AixSql());
		return list;
	}

	/**
	 * 获取 scounix 服务器 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getHost_ScounixList() {
		// aix
		List<BaseVo> list = getHostNode(getHost_ScounixSql());
		return list;
	}

	/**
	 * 获取 scoopenserver 服务器 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getHost_ScoopenserverList() {
		// aix
		List<BaseVo> list = getHostNode(getHost_ScoopenserverSql());
		return list;
	}

	/**
	 * 获取 aix 服务器 sql 语句
	 * 
	 * @return
	 */
	public String getHost_AixSql() {

		String sql = " where sys_oid like '1.3.6.1.4.1.2.%' and category=4";

		sql = sql + getMonFlagSql("managed");

		sql = sql + getBidSql(BidSQLUitl.HOST_NODE);

		return sql;
	}

	/**
	 * 获取 scounix 服务器 sql 语句
	 * 
	 * @return
	 */
	public String getHost_ScounixSql() {

		String sql = " where sys_oid like 'scounix%' and category=4";

		sql = sql + getMonFlagSql("monflag");

		sql = sql + getBidSql(BidSQLUitl.HOST_NODE);

		return sql;
	}

	/**
	 * 获取 scounix 服务器 sql 语句
	 * 
	 * @return
	 */
	public String getHost_ScoopenserverSql() {

		String sql = " where sys_oid like 'scoopen%' and category=4";

		sql = sql + getMonFlagSql("monflag");

		sql = sql + getBidSql(BidSQLUitl.HOST_NODE);

		return sql;
	}

	/**
	 * 获取 hpunix 服务器 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getHost_HpunixList() {
		// hpunix
		List<BaseVo> list = getHostNode(getHost_HpunixSql());
		return list;
	}

	/**
	 * 获取 hpunix 服务器 sql 语句
	 * 
	 * @return
	 */
	public String getHost_HpunixSql() {

		String sql = " where sys_oid like '1.3.6.1.4.1.11.%' and category=4";

		sql = sql + getMonFlagSql("managed");

		sql = sql + getBidSql(BidSQLUitl.HOST_NODE);

		return sql;
	}

	/**
	 * 获取 solaris 服务器 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getHost_SolarisList() {
		// solaris
		List<BaseVo> list = getHostNode(getHost_SolarisSql());
		return list;
	}

	/**
	 * 获取 solaris 服务器 sql 语句
	 * 
	 * @return
	 */
	public String getHost_SolarisSql() {

		String sql = " where sys_oid like '1.3.6.1.4.1.42.%' and category=4";

		sql = sql + getMonFlagSql("managed");

		sql = sql + getBidSql(BidSQLUitl.HOST_NODE);

		return sql;
	}

	/**
	 * 获取 as400 服务器 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getHost_As400List() {
		// as400
		List<BaseVo> list = getHostNode(getHost_As400Sql());
		return list;
	}

	/**
	 * 获取 as400 服务器 sql 语句
	 * 
	 * @return
	 */
	public String getHost_As400Sql() {

		String sql = " where sys_oid like 'as400%' and category=4";

		sql = sql + getMonFlagSql("managed");

		sql = sql + getBidSql(BidSQLUitl.HOST_NODE);

		return sql;
	}

	// --------------------------------------------------------------------------------------------
	// 数据库
	// --------------------------------------------------------------------------------------------

	/**
	 * 获取数据库
	 * 
	 * @return
	 */
	public List<BaseVo> getDBList() {
		List<BaseVo> list = new ArrayList<BaseVo>();
		list.addAll(getDB_MysqlList());
		list.addAll(getDB_DB2List());
		list.addAll(getDB_InformixList());
		list.addAll(getDB_SQLServerList());
		list.addAll(getDB_SybaseList());
		list.addAll(getDB_OracleList());
		return list;
	}

	public List<BaseVo> getDB(String condition) {
		List<BaseVo> list = null;
		DBDao dao = new DBDao();
		try {
			list = dao.findByCondition(condition);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dao.close();
		}

		return list;
	}

	/**
	 * 获取 oracle 数据库 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getDB_OracleList() {
		// oracle
		List<BaseVo> list = null;
		DBDao oraclePartsDao = new DBDao();
		try {
			list = oraclePartsDao.findByCondition(getDB_OracleSql());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			oraclePartsDao.close();
		}

		if (list == null) {
			list = new ArrayList<BaseVo>();
		}
		return list;
	}
	
	/**
	 * 获取 oracle 数据库 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getDB_OracleRacList() {
		// oracle
		List<BaseVo> list = null;
		DBDao oraclePartsDao = new DBDao();
		try {
			list = oraclePartsDao.findByCondition(getDB_OracleSql());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			oraclePartsDao.close();
		}

		if (list == null) {
			list = new ArrayList<BaseVo>();
		}
		return list;
	}

	/**
	 * 获取 oracle 数据库 Sql 语句
	 * 
	 * @return
	 */
	public String getDB_OracleSql() {

		DBTypeVo dBTypeVo = null;
		DBTypeDao dBTypeDao = new DBTypeDao();
		try {
			dBTypeVo = dBTypeDao.findByDbtype("oraclerac");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dBTypeDao.close();
		}

		String sql = " where dbtype=" + dBTypeVo.getId();

		sql = sql + getMonFlagSql("managed");

		sql = sql + getBidSql(BidSQLUitl.DB);

		return sql;

	}

	/**
	 * 获取 mysql 数据库 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getDB_MysqlList() {
		List<BaseVo> list = getDB(getDB_MysqlSql());
		return list;
	}

	/**
	 * 获取 mysql 数据库 Sql 语句
	 * 
	 * @return
	 */
	public String getDB_MysqlSql() {

		DBTypeVo dBTypeVo = null;
		DBTypeDao dBTypeDao = new DBTypeDao();
		try {
			dBTypeVo = dBTypeDao.findByDbtype("mysql");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dBTypeDao.close();
		}

		String sql = " where dbtype=" + dBTypeVo.getId();

		sql = sql + getMonFlagSql("managed");

		sql = sql + getBidSql(BidSQLUitl.DB);

		return sql;

	}

	/**
	 * 获取 DB2 数据库 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getDB_DB2List() {
		List<BaseVo> list = getDB(getDB_DB2Sql());
		return list;
	}

	/**
	 * 获取 DB2 数据库 Sql 语句
	 * 
	 * @return
	 */
	public String getDB_DB2Sql() {

		DBTypeVo dBTypeVo = null;
		DBTypeDao dBTypeDao = new DBTypeDao();
		try {
			dBTypeVo = dBTypeDao.findByDbtype("db2");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dBTypeDao.close();
		}

		String sql = " where dbtype=" + dBTypeVo.getId();

		sql = sql + getMonFlagSql("managed");

		sql = sql + getBidSql(BidSQLUitl.DB);

		return sql;

	}

	/**
	 * 获取 SQLServer 数据库 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getDB_SQLServerList() {
		// SQLServer
		List<BaseVo> list = getDB(getDB_SQLServerSql());
		return list;
	}

	/**
	 * 获取 SQLServer 数据库 Sql 语句
	 * 
	 * @return
	 */
	public String getDB_SQLServerSql() {

		DBTypeVo dBTypeVo = null;
		DBTypeDao dBTypeDao = new DBTypeDao();
		try {
			dBTypeVo = dBTypeDao.findByDbtype("sqlserver");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dBTypeDao.close();
		}
		// System.out.println("-------------------"+dBTypeVo);
		String sql = " where dbtype=" + dBTypeVo.getId();

		sql = sql + getMonFlagSql("managed");

		sql = sql + getBidSql(BidSQLUitl.DB);

		return sql;

	}

	/**
	 * 获取 Sybase 数据库 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getDB_SybaseList() {
		// Sybase
		List<BaseVo> list = getDB(getDB_SybaseSql());
		return list;
	}

	/**
	 * 获取 Sybase 数据库 Sql 语句
	 * 
	 * @return
	 */
	public String getDB_SybaseSql() {

		DBTypeVo dBTypeVo = null;
		DBTypeDao dBTypeDao = new DBTypeDao();
		try {
			dBTypeVo = dBTypeDao.findByDbtype("sybase");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dBTypeDao.close();
		}

		String sql = " where dbtype=" + dBTypeVo.getId();

		sql = sql + getMonFlagSql("managed");

		sql = sql + getBidSql(BidSQLUitl.DB);

		return sql;

	}

	/**
	 * 获取 Informix 数据库 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getDB_InformixList() {
		// Informix
		List<BaseVo> list = getDB(getDB_InformixSql());
		return list;
	}

	/**
	 * 获取 Informix 数据库 Sql 语句
	 * 
	 * @return
	 */
	public String getDB_InformixSql() {

		DBTypeVo dBTypeVo = null;
		DBTypeDao dBTypeDao = new DBTypeDao();
		try {
			dBTypeVo = dBTypeDao.findByDbtype("informix");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dBTypeDao.close();
		}

		String sql = " where dbtype=" + dBTypeVo.getId();

		sql = sql + getMonFlagSql("managed");

		sql = sql + getBidSql(BidSQLUitl.DB);

		return sql;

	}

	// -------------------------------------------------------------------------------------------------
	// 中间件
	// -------------------------------------------------------------------------------------------------

	/**
	 * 从内存中获取 中间件 列表 wxy update 2013-3-31
	 */
	public List<BaseVo> getMiddlewareList() {

		List<BaseVo> list = new ArrayList<BaseVo>();

		// list.addAll(getMiddleware_ApacheList());
		// list.addAll(getMiddleware_CICSList());
		// list.addAll(getMiddleware_DNSList());
		// list.addAll(getMiddleware_DominoList());
		// list.addAll(getMiddleware_IISList());
		// list.addAll(getMiddleware_JBossList());
		// list.addAll(getMiddleware_MQList());
		// list.addAll(getMiddleware_TomcatList());
		// list.addAll(getMiddleware_TuxedoList());
		List<BaseVo> apacheList = new ArrayList<BaseVo>();
		this.getApacheListByMon(apacheList);
		List<BaseVo> tomcatList = new ArrayList<BaseVo>();
		this.getTomListByMon(tomcatList);
		List<BaseVo> dnsList = new ArrayList<BaseVo>();
		this.getDnsListByMon(dnsList);

		List<BaseVo> mqList = new ArrayList<BaseVo>();
		this.getMqListByMon(mqList);
		List<BaseVo> cicsList = new ArrayList<BaseVo>();
		this.getCicsListByMon(cicsList);
		List<BaseVo> dominoList = new ArrayList<BaseVo>();
		this.getDominoListByMon(dominoList);
		List<BaseVo> iisList = new ArrayList<BaseVo>();
		this.getIisListByMon(iisList);
		List<BaseVo> jbossList = new ArrayList<BaseVo>();
		this.getJbossListByMon(jbossList);
		List<BaseVo> wasList = new ArrayList<BaseVo>();
		this.getWasListByMon(wasList);
		List<BaseVo> weblogicList = new ArrayList<BaseVo>();
		this.getWeblogicListByMon(weblogicList);
		List<BaseVo> tuxdoList = new ArrayList<BaseVo>();
		this.getTuxdoListByMon(tuxdoList);
		List<BaseVo> resinList = new ArrayList<BaseVo>();
		this.getResinListByMon(resinList);

		list.addAll(apacheList);
		list.addAll(cicsList);
		list.addAll(dnsList);
		list.addAll(dominoList);
		list.addAll(iisList);
		list.addAll(jbossList);
		list.addAll(mqList);
		list.addAll(tomcatList);
		list.addAll(tuxdoList);
		list.addAll(wasList);
		// list.addAll(getMiddleware_WasList());
		list.addAll(weblogicList);
		list.addAll(resinList);
		// list.addAll(getMiddleware_WeblogicList());

		return list;
	}

	/**
	 * 获取 Tomcat 中间件 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getMiddleware_TomcatList() {
		// Tomcat

		List<BaseVo> list = null;

		TomcatDao tomcatdao = new TomcatDao();
		try {
			list = tomcatdao.findByCondition(getMiddleware_TomcatSql());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			tomcatdao.close();
		}

		if (list == null) {
			list = new ArrayList<BaseVo>();
		}

		return list;
	}

	/**
	 * 获取 Tomcat Sql 语句
	 * 
	 * @return
	 */
	public String getMiddleware_TomcatSql() {

		String sql = " where 1=1";

		sql = sql + getMonFlagSql("monflag");

		sql = sql + getBidSql(BidSQLUitl.TOMCAT);

		return sql;
	}

	/**
	 * 获取 MQ 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getMiddleware_MQList() {
		// mq
		MQConfigDao mqconfigdao = new MQConfigDao();
		List<BaseVo> list = null;
		try {
			list = mqconfigdao.findByCondition(getMiddleware_MQSql());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mqconfigdao.close();
		}

		if (list == null) {
			list = new ArrayList<BaseVo>();
		}

		return list;
	}

	/**
	 * 获取 MQ Sql 语句
	 * 
	 * @return
	 */
	public String getMiddleware_MQSql() {
		String sql = " where 1=1";

		sql = sql + getMonFlagSql("mon_flag");

		sql = sql + getBidSql(BidSQLUitl.MQ);

		return sql;
	}

	/**
	 * 获取 Domino 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getMiddleware_DominoList() {
		// domimo
		DominoConfigDao dominoconfigdao = new DominoConfigDao();
		List<BaseVo> list = null;
		try {
			list = dominoconfigdao.findByCondition(getMiddleware_DominoSql());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dominoconfigdao.close();
		}

		if (list == null) {
			list = new ArrayList<BaseVo>();
		}

		return list;
	}

	/**
	 * 获取 Domino Sql 语句
	 * 
	 * @return
	 */
	public String getMiddleware_DominoSql() {
		String sql = " where 1=1";

		sql = sql + getMonFlagSql("mon_flag");

		sql = sql + getBidSql(BidSQLUitl.DOMINO);

		return sql;
	}

	/**
	 * 获取 Was 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getMiddleware_WasList() {
		// was
		WasConfigDao wasconfigdao = new WasConfigDao();
		List<BaseVo> list = null;
		try {
			list = wasconfigdao.findByCondition(getMiddleware_WasSql());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			wasconfigdao.close();
		}

		if (list == null) {
			list = new ArrayList<BaseVo>();
		}

		return list;
	}

	/**
	 * 获取 Was Sql 语句
	 * 
	 * @return
	 */
	public String getMiddleware_WasSql() {
		String sql = " where 1=1";

		sql = sql + getMonFlagSql("mon_flag");

		sql = sql + getBidSql(BidSQLUitl.WAS);

		return sql;
	}

	/**
	 * 获取 Weblogic 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getMiddleware_WeblogicList() {
		// weblogic
		// SysLogger.info("########################2");
		WeblogicConfigDao weblogicconfigdao = new WeblogicConfigDao();
		List<BaseVo> list = null;
		try {
			list = weblogicconfigdao.findByCondition(getMiddleware_WeblogicSql());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			weblogicconfigdao.close();
		}
		if (list == null) {
			list = new ArrayList<BaseVo>();
		}
		return list;
	}

	/**
	 * 获取 Resin 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getMiddleware_ResinList() {
		// weblogic
		// SysLogger.info("########################2");
		ResinDao resindao = new ResinDao();
		List<BaseVo> list = null;
		try {
			list = resindao.findByCondition(getMiddleware_ResinSql());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			resindao.close();
		}
		if (list == null) {
			list = new ArrayList<BaseVo>();
		}
		return list;
	}

	/**
	 * 获取 Weblogic Sql 语句
	 * 
	 * @return
	 */
	public String getMiddleware_WeblogicSql() {
		String sql = " where 1=1";

		sql = sql + getMonFlagSql("mon_flag");

		sql = sql + getBidSql(BidSQLUitl.WEBLOGIC);

		return sql;
	}

	/**
	 * 获取 Weblogic Sql 语句
	 * 
	 * @return
	 */
	public String getMiddleware_ResinSql() {
		String sql = " where 1=1";

		sql = sql + getMonFlagSql("mon_flag");

		sql = sql + getBidSql(BidSQLUitl.RESIN);

		return sql;
	}

	/**
	 * 获取 IIS 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getMiddleware_IISList() {
		// IIS
		IISConfigDao iisconfigdao = new IISConfigDao();
		List<BaseVo> list = null;
		try {
			list = iisconfigdao.findByCondition(getMiddleware_IISSql());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			iisconfigdao.close();
		}
		if (list == null) {
			list = new ArrayList<BaseVo>();
		}

		return list;
	}

	/**
	 * 获取 IIS Sql 语句
	 * 
	 * @return
	 */
	public String getMiddleware_IISSql() {
		String sql = " where 1=1";

		sql = sql + getMonFlagSql("mon_flag");

		sql = sql + getBidSql(BidSQLUitl.IIS);

		return sql;
	}

	/**
	 * 获取 cics 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getMiddleware_CICSList() {
		// cics
		List<BaseVo> list = null;
		CicsConfigDao cicsconfigdao = new CicsConfigDao();
		try {
			list = cicsconfigdao.findByCondition(getMiddleware_CicsSql());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cicsconfigdao.close();
		}

		if (list == null) {
			list = new ArrayList<BaseVo>();
		}

		return list;
	}

	/**
	 * 获取 cics Sql 语句
	 * 
	 * @return
	 */
	public String getMiddleware_CicsSql() {
		String sql = " where 1=1";

		sql = sql + getMonFlagSql("flag");

		sql = sql + getBidSql(BidSQLUitl.CICS);

		return sql;
	}

	/**
	 * 获取 DNS 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getMiddleware_DNSList() {
		// DNS
		DnsConfigDao dnsconfigdao = new DnsConfigDao();
		List<BaseVo> list = null;
		try {
			list = dnsconfigdao.findByCondition(getMiddleware_DNSSql());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dnsconfigdao.close();
		}

		if (list == null) {
			list = new ArrayList<BaseVo>();
		}

		return list;
	}

	/**
	 * 获取 DNS Sql 语句
	 * 
	 * @return
	 */
	public String getMiddleware_DNSSql() {
		String sql = " where 1=1";

		sql = sql + getMonFlagSql("flag");

		sql = sql + getBidSql(BidSQLUitl.DNS);

		return sql;
	}

	/**
	 * 获取 JBoss 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getMiddleware_JBossList() {
		// JBoss
		JBossConfigDao jbossConfigDao = new JBossConfigDao();
		List<BaseVo> list = null;
		try {
			list = jbossConfigDao.findByCondition(getMiddleware_JBossSql());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			jbossConfigDao.close();
		}

		if (list == null) {
			list = new ArrayList<BaseVo>();
		}

		return list;
	}

	/**
	 * 获取 JBoss Sql 语句
	 * 
	 * @return
	 */
	public String getMiddleware_JBossSql() {
		String sql = " where 1=1";

		sql = sql + getMonFlagSql("flag");

		sql = sql + getBidSql(BidSQLUitl.JBOSS);

		return sql;
	}

	/**
	 * 获取 Apache 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getMiddleware_ApacheList() {
		// Apache
		ApacheConfigDao apacheConfigDao = new ApacheConfigDao();
		List<BaseVo> list = null;
		try {
			list = apacheConfigDao.findByCondition(getMiddleware_ApacheSql());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			apacheConfigDao.close();
		}

		if (list == null) {
			list = new ArrayList<BaseVo>();
		}

		return list;
	}

	/**
	 * 获取 Apache Sql 语句
	 * 
	 * @return
	 */
	public String getMiddleware_ApacheSql() {
		String sql = " where 1=1";

		sql = sql + getMonFlagSql("flag");

		sql = sql + getBidSql(BidSQLUitl.APACHE);

		return sql;
	}

	/**
	 * 获取 Tuxedo 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getMiddleware_TuxedoList() {
		// Tuxedo
		TuxedoConfigDao tuxedoConfigDao = new TuxedoConfigDao();
		List<BaseVo> list = null;
		try {
			list = tuxedoConfigDao.findByCondition(getMiddleware_TuxedoSql());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			tuxedoConfigDao.close();
		}

		if (list == null) {
			list = new ArrayList<BaseVo>();
		}

		return list;
	}

	/**
	 * 获取 Tuxedo Sql 语句
	 * 
	 * @return
	 */
	public String getMiddleware_TuxedoSql() {
		String sql = " where 1=1";

		sql = sql + getMonFlagSql("mon_flag");

		sql = sql + getBidSql(BidSQLUitl.TUXEDO);

		return sql;
	}

	// ---------------------------------------------------------------------------------------
	// 服务
	// ---------------------------------------------------------------------------------------

	/**
	 * 获取 服务 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getServiceList() {

		List<BaseVo> list = new ArrayList<BaseVo>();

		// list.addAll(getService_EmailList());
		// list.addAll(getService_FtpList());
		// list.addAll(getService_PSTypeVoList());
		// list.addAll(getService_WebList());
		List<BaseVo> emailList = new ArrayList<BaseVo>();
		this.getEmailListByMon(emailList);
		List<BaseVo> ftpList = new ArrayList<BaseVo>();
		this.getFtpListByMon(ftpList);
		List<BaseVo> tftpList = new ArrayList<BaseVo>();
		this.getTftpListByMon(tftpList);
		List<BaseVo> dhcpList = new ArrayList<BaseVo>();
		this.getDhcpListByMon(dhcpList);
		List<BaseVo> webconfigList = new ArrayList<BaseVo>();
		this.getWebListByMon(webconfigList);
		List<BaseVo> psList = new ArrayList<BaseVo>();
		this.getPsListByMon(psList);
		List<BaseVo> webloginList = new ArrayList<BaseVo>();
		this.getWebloginListByMon(webloginList);
		list.addAll(emailList);
		list.addAll(ftpList);
		list.addAll(tftpList);
		list.addAll(dhcpList);
		list.addAll(psList);
		list.addAll(webconfigList);
		list.addAll(webloginList);
		return list;
	}

	/**
	 * 获取 Email 服务 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getService_EmailList() {
		// mail
		List<BaseVo> list = null;

		EmailConfigDao emailConfigDao = new EmailConfigDao();
		try {
			list = emailConfigDao.findByCondition(getService_EmailSql());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			emailConfigDao.close();
		}

		if (list == null) {
			list = new ArrayList<BaseVo>();
		}

		return list;
	}

	/**
	 * 获取 Email Sql 语句
	 * 
	 * @return
	 */
	public String getService_EmailSql() {

		String sql = " where 1=1";

		sql = sql + getMonFlagSql("monflag");

		sql = sql + getBidSql(BidSQLUitl.EMAIL);

		return sql;
	}

	/**
	 * 获取 FTP 服务 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getService_FtpList() {
		// FTP

		List<BaseVo> list = null;

		FTPConfigDao ftpConfigDao = new FTPConfigDao();
		try {
			list = ftpConfigDao.findByCondition(getService_FtpSql());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ftpConfigDao.close();
		}

		if (list == null) {
			list = new ArrayList<BaseVo>();
		}

		return list;
	}

	/**
	 * 获取 TFTP 服务 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getService_TFtpList() {
		// FTP

		List<BaseVo> list = null;

		TFTPConfigDao ftpConfigDao = new TFTPConfigDao();
		try {
			list = ftpConfigDao.findByCondition(getService_TFtpSql());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ftpConfigDao.close();
		}

		if (list == null) {
			list = new ArrayList<BaseVo>();
		}

		return list;
	}

	/**
	 * 获取 DHCP 服务 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getService_DHCPList() {
		// FTP

		List<BaseVo> list = null;

		DHCPConfigDao configDao = new DHCPConfigDao();
		try {
			list = configDao.findByCondition(getService_DHCPSql());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			configDao.close();
		}

		if (list == null) {
			list = new ArrayList<BaseVo>();
		}

		return list;
	}

	/**
	 * 获取 Ftp Sql 语句
	 * 
	 * @return
	 */
	public String getService_FtpSql() {

		String sql = " where 1=1";

		sql = sql + getMonFlagSql("monflag");

		sql = sql + getBidSql(BidSQLUitl.FTP);

		return sql;
	}

	/**
	 * 获取 TFtp Sql 语句
	 * 
	 * @return
	 */
	public String getService_TFtpSql() {

		String sql = " where 1=1";

		sql = sql + getMonFlagSql("monflag");

		sql = sql + getBidSql(BidSQLUitl.TFTP);

		return sql;
	}

	/**
	 * 获取 DHCP Sql 语句
	 * 
	 * @return
	 */
	public String getService_DHCPSql() {

		String sql = " where 1=1";

		sql = sql + getMonFlagSql("monflag");

		sql = sql + getBidSql(BidSQLUitl.DHCP);

		return sql;
	}

	/**
	 * 获取 Web 服务 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getService_WebList() {
		// WEB

		List<BaseVo> list = null;

		WebConfigDao webConfigDao = new WebConfigDao();
		try {
			list = webConfigDao.findByCondition(getService_WebSql());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			webConfigDao.close();
		}

		if (list == null) {
			list = new ArrayList<BaseVo>();
		}

		return list;
	}

	/**
	 * 获取 Web Sql 语句
	 * 
	 * @return
	 */
	public String getService_WebSql() {

		String sql = " where 1=1";

		sql = sql + getMonFlagSql("flag");

		sql = sql + getBidSql(BidSQLUitl.WEB);

		return sql;
	}

	/**
	 * 获取 PSTypeVo 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getService_PSTypeVoList() {
		// PSTypeVo

		List<BaseVo> list = null;

		PSTypeDao psTypeDao = new PSTypeDao();
		try {
			list = psTypeDao.findByCondition(getService_PSTypeVoSql());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			psTypeDao.close();
		}

		if (list == null) {
			list = new ArrayList<BaseVo>();
		}

		return list;
	}

	/**
	 * 获取 PSTypeVo Sql 语句
	 * 
	 * @return
	 */
	public String getService_PSTypeVoSql() {

		String sql = " where 1=1";

		sql = sql + getMonFlagSql("monflag");

		sql = sql + getBidSql(BidSQLUitl.PSTYPE);

		return sql;
	}

	// --------------------------------------------------------------------------------------------
	// 拓扑图
	// --------------------------------------------------------------------------------------------

	/**
	 * 获取 业务 拓扑图 列表
	 * 
	 * @return
	 */
	public List<BaseVo> getTopo_BusinessList() {
		// Tomcat

		List<BaseVo> list = null;

		ManageXmlDao manageXmlDao = new ManageXmlDao();
		try {
			list = manageXmlDao.findByCondition(getTopo_BusinessSql());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			manageXmlDao.close();
		}

		if (list == null) {
			list = new ArrayList<BaseVo>();
		}

		return list;
	}

	/**
	 * 获取 业务 拓扑图 语句
	 * 
	 * @return
	 */
	public String getTopo_BusinessSql() {

		String sql = " where 1=1 and topo_type='1'";

		// 由于拓扑图暂时未有是否管理选项 故 注释
		// sql = sql + getMonFlagSql("monflag");

		sql = sql + getBidSql(BidSQLUitl.TOPO);

		return sql;
	}

	/**
	 * 根据字段名 获取 是否监控 如果未设置 是否监控 则 返回 ""
	 * 
	 * @param fieldName
	 * @return
	 */
	public String getMonFlagSql(String fieldName) {
		String sql = "";
		if (isSetedMonitorFlag) {
			String mon_flag = monitorFlag;
			if (mon_flag != null) {
				sql = " and " + fieldName + "='" + monitorFlag + "'";
			}
		}
		return sql;
	}

	// public String getConditionSQL(){
	// String sql = "";
	// if(isSetedBid){
	// sql = getBidSql();
	// }
	//		
	// if(isSetedMonitorFlag){
	// }
	//		
	// return sql;
	// }

	public String getBidSql(String fieldName) {
		return getBidSql(fieldName, bid);
	}

	public String getBidSql(String fieldName, String businessId) {
		String sql = "";
		if (isSetedBid) {
			sql = BidSQLUitl.getBidSQL(bid, fieldName);
		}
		return sql;
		// StringBuffer s = new StringBuffer();
		// int _flag = 0;
		// if (businessId != null){
		// if(businessId !="-1"){
		// String[] bids = businessId.split(",");
		// if(bids.length>0){
		// for(int i=0;i<bids.length;i++){
		// if(bids[i].trim().length()>0){
		// if(_flag==0){
		// s.append(" and ( bid like '%,"+bids[i].trim()+",%' ");
		// _flag = 1;
		// }else{
		// //flag = 1;
		// s.append(" or bid like '%,"+bids[i].trim()+",%' ");
		// }
		// }
		// }
		// s.append(") ") ;
		// }
		//				
		// }
		// }
		// String sql = s.toString();
		// //SysLogger.info(sql);
		// return sql;
	}

	/**
	 * 通过 HostNode 转换成 NodeDTO
	 * 
	 * @param hostNode
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(HostNode hostNode) {

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(hostNode.getId());
		nodeDTO.setName(hostNode.getAlias());
		nodeDTO.setIpaddress(hostNode.getIpAddress());
		nodeDTO.setNodeid(String.valueOf(hostNode.getId()));
		nodeDTO.setBusinessId(hostNode.getBid());
		nodeDTO.setSysOid(hostNode.getSysOid());

		String subtype = "";

		// SysLogger.info(hostNode.getIpAddress()+"===="+hostNode.getSysOid()+"========="+hostNode.getCategory());
		String type = "";
		if (hostNode.getCategory() == 4) {
			type = Constant.TYPE_HOST;
			if (hostNode.getSysOid() != null && hostNode.getSysOid().trim().length() > 0) {
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.311."))
					subtype = Constant.TYPE_HOST_SUBTYPE_WINDOWS;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.2021.")||hostNode.getSysOid().startsWith("1.3.6.1.4.1.8072.3.2.10"))
					subtype = Constant.TYPE_HOST_SUBTYPE_LINUX;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.2."))
					subtype = Constant.TYPE_HOST_SUBTYPE_AIX;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.11."))
					subtype = Constant.TYPE_HOST_SUBTYPE_HPUNIX;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.42."))
					subtype = Constant.TYPE_HOST_SUBTYPE_SOLARIS;
				if (hostNode.getSysOid().startsWith("as400"))
					subtype = Constant.TYPE_HOST_SUBTYPE_AS400;
				if (hostNode.getSysOid().startsWith("scounix"))
					subtype = Constant.TYPE_HOST_SUBTYPE_SCOUNIX;
				if (hostNode.getSysOid().startsWith("scoopenserver"))
					subtype = Constant.TYPE_HOST_SUBTYPE_SCOOPENSERVER;
				if (hostNode.getOstype() == 15)
					subtype = Constant.TYPE_HOST_SUBTYPE_AS400;
			}
		} else if (hostNode.getCategory() == 9) {
			type = Constant.TYPE_ATM;
			subtype = Constant.TYPE_ATM_SUBTYPE_ATM;
		} else if (hostNode.getCategory() == 12) {
			type = Constant.TYPE_VPN;
			if (hostNode.getSysOid() != null && hostNode.getSysOid().trim().length() > 0) {
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.7564"))
					subtype = Constant.TYPE_VPN_SUBTYPE_ARRAYNETWORKS;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.25506."))
					subtype = Constant.TYPE_NET_SUBTYPE_H3C;
			}
		} else if (hostNode.getCategory() == 8) {
			type = Constant.TYPE_FIREWALL;
			if (hostNode.getSysOid() != null && hostNode.getSysOid().trim().length() > 0) {
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.28557."))
					subtype = Constant.TYPE_FIREWALL_SUBTYPE_HILLSTONE;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.3224."))
					subtype = Constant.TYPE_FIREWALL_SUBTYPE_NETSCREEN;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.2636."))
					subtype = Constant.TYPE_FIREWALL_SUBTYPE_NETSCREEN;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.94."))
					subtype = Constant.TYPE_FIREWALL_SUBTYPE_NOKIA;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.31648."))
					subtype = Constant.TYPE_FIREWALL_SUBTYPE_DPTECH;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.15227."))
					subtype = Constant.TYPE_FIREWALL_SUBTYPE_VENUS;
				// if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.15227."))subtype
				// = Constant.TYPE_FIREWALL_SUBTYPE_NIPS;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.10734."))
					subtype = Constant.TYPE_FIREWALL_SUBTYPE_TIPPINGPOINT;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.24968."))
					subtype = Constant.TYPE_FIREWALL_SUBTYPE_SECWORLD;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.14331."))
					subtype = Constant.TYPE_FIREWALL_SUBTYPE_TOPSEC;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.24968."))
					subtype = Constant.TYPE_FIREWALL_SUBTYPE_SECGAGE;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.4881."))
					subtype = Constant.TYPE_FIREWALL_SUBTYPE_REDGIANT;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.25506."))
					subtype = Constant.TYPE_NET_SUBTYPE_H3C;
			}
		} else if (hostNode.getCategory() == 10) {
			type = Constant.TYPE_GATEWAY;
			subtype = Constant.TYPE_GATEWAY_SUBTYPE_CISCO;
		} else if (hostNode.getCategory() == 11) {
			type = Constant.TYPE_F5;
			subtype = Constant.TYPE_F5_SUBTYPE_F5;
			if (hostNode.getSysOid() != null && hostNode.getSysOid().trim().length() > 0) {
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.35047."))
					subtype = Constant.TYPE_BALANCER_SANGFOR;
			}
		} else if (hostNode.getCategory() == 13) {
			type = Constant.TYPE_BEHAVIOR;
			subtype = Constant.TYPE_BEHAVIOR_SUBTYPE_SANGFOR;
			if (hostNode.getSysOid() != null && hostNode.getSysOid().trim().length() > 0) {
				if (hostNode.getSysOid().startsWith("Sangfor"))
					subtype = Constant.TYPE_BEHAVIOR_SUBTYPE_SANGFOR;
			}
		} else if (hostNode.getCategory() == 14) {
			// type = Constant.TYPE_FLOWCONTROL;
			// subtype = Constant.TYPE_FLOWCONTROL_SUBTYPE_DHC;
			// if(hostNode.getSysOid() != null &&
			// hostNode.getSysOid().trim().length()>0){
			// if(hostNode.getSysOid().startsWith("DHC"))subtype =
			// Constant.TYPE_BALANCER_SANGFOR;
			// }
			type = Constant.TYPE_STORAGE;
			subtype = Constant.TYPE_STORAGE_SUBTYPE_HDS;
			if (hostNode.getSysOid() != null && hostNode.getSysOid().trim().length() > 0) {
				if (hostNode.getSysOid().startsWith("hds"))
					subtype = Constant.TYPE_STORAGE_SUBTYPE_HDS;
			}
			if ((hostNode.getSysOid() != null && hostNode.getSysOid().startsWith("1.3.6.1.4.1.789")) || hostNode.getSysOid().startsWith("netapp")) {
				subtype = Constant.TYPE_STORAGE_SUBTYPE_NETAPP;
			}
			if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.11.2.3.7.11"))
				subtype = Constant.TYPE_STORAGE_SUBTYPE_HP;
		} else if (hostNode.getCategory() == 15) {
			// type = Constant.TYPE_DDOS;
			// subtype = Constant.TYPE_DDOS_SUBTYPE_NSFOCUS;
			// if(hostNode.getSysOid() != null &&
			// hostNode.getSysOid().trim().length()>0){
			// if(hostNode.getSysOid().startsWith("Nsfocus"))subtype =
			// Constant.TYPE_DDOS_SUBTYPE_NSFOCUS;
			// }
			type = Constant.TYPE_VIRTUAL;
			if (hostNode.getSysOid() != null && hostNode.getSysOid().trim().length() > 0) {
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.6876."))
					subtype = Constant.TYPE_VIRTUAL_SUBTYPE_VMWARE;
			}
		} else if (hostNode.getCategory() == 16) {
			type = Constant.TYPE_XSCAN;
			subtype = Constant.TYPE_XSCAN_SUBTYPE_VENUSTECH;
			if (hostNode.getSysOid() != null && hostNode.getSysOid().trim().length() > 0) {
				if (hostNode.getSysOid().startsWith("Venustech"))
					subtype = Constant.TYPE_XSCAN_SUBTYPE_VENUSTECH;
                if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.476.1.42"))
                {//艾默生空调
                   type = Constant.TYPE_AIR;
                   subtype = Constant.TYPE_AIR_SUBTYPE_EMS;
                }
			}
		} else if (hostNode.getCategory() == 17) {
			type = Constant.TYPE_LOGAUDIT;
			subtype = Constant.TYPE_LOGAUDIT_SUBTYPE_TOPSEC;
			if (hostNode.getSysOid() != null && hostNode.getSysOid().trim().length() > 0) {
				if (hostNode.getSysOid().startsWith("TopSec"))
					subtype = Constant.TYPE_LOGAUDIT_SUBTYPE_TOPSEC;
                if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.13400.2.1"))
                {//艾默生UPS
                   type = Constant.TYPE_UPS;
                   subtype = Constant.TYPE_UPS_SUBTYPE_EMS;
                }
			}
		} else {
			// 网络设备,需要判断操作系统
			type = Constant.TYPE_NET;
			if (hostNode.getSysOid() != null && hostNode.getSysOid().trim().length() > 0) {
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.9."))
					subtype = Constant.TYPE_NET_SUBTYPE_CISCO;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.2011."))
					subtype = Constant.TYPE_NET_SUBTYPE_H3C;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.25506."))
					subtype = Constant.TYPE_NET_SUBTYPE_H3C;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.9.2.1.57."))
					subtype = Constant.TYPE_NET_SUBTYPE_ENTRASYS;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.89."))
					subtype = Constant.TYPE_NET_SUBTYPE_RADWARE;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.5651."))
					subtype = Constant.TYPE_NET_SUBTYPE_MAIPU;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.4881."))
					subtype = Constant.TYPE_NET_SUBTYPE_REDGIANT;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.45.") || hostNode.getSysOid().startsWith("1.3.6.1.4.1.2272.31"))
					subtype = Constant.TYPE_NET_SUBTYPE_NORTHTEL;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.171."))
					subtype = Constant.TYPE_NET_SUBTYPE_DLINK;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.3320."))
					subtype = Constant.TYPE_NET_SUBTYPE_BDCOM;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.3902."))
					subtype = Constant.TYPE_NET_SUBTYPE_ZTE;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.388.11.1.2"))
					subtype = Constant.TYPE_NET_MOTOROLA;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.1588.2.1.1."))
					subtype = Constant.TYPE_NET_SUBTYPE_BROCADE;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.800."))
					subtype = Constant.TYPE_NET_SUBTYPE_ALCATEL;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.45."))
					subtype = Constant.TYPE_NET_SUBTYPE_AVAYA;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.11.2.3.7.11"))
					subtype = Constant.TYPE_NET_SUBTYPE_HP;
				if (hostNode.getSysOid().startsWith("other"))
					subtype = Constant.TYPE_NET_SUBTYPE_OTHER;
				// if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.1588"))subtype
				// = Constant.TYPE_NET_SUBTYPE_IBM;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.2636."))
					subtype = Constant.TYPE_NET_SUBTYPE_JUNIPER;
			}
		}

		nodeDTO.setSubtype(subtype);
		nodeDTO.setType(type);
		// SysLogger.info(nodeDTO.getIpaddress()+"==="+nodeDTO.getType()+"==="+nodeDTO.getSubtype()+"=="+nodeDTO.getIpaddress());
		return nodeDTO;
	}

	/**
	 * 通过 HostNode 转换成 NodeDTO
	 * 
	 * @param hostNode
	 * @return
	 */
	public NodeDTO creatNodeDTOByHost(Host hostNode) {

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(hostNode.getId());
		nodeDTO.setName(hostNode.getAlias());
		nodeDTO.setIpaddress(hostNode.getIpAddress());
		nodeDTO.setNodeid(String.valueOf(hostNode.getId()));
		nodeDTO.setBusinessId(hostNode.getBid());

		String subtype = "";

		// SysLogger.info(hostNode.getIpAddress()+"===="+hostNode.getSysOid()+"========="+hostNode.getCategory());
		String type = "";
		if (hostNode.getCategory() == 4) {
			type = Constant.TYPE_HOST;
			if (hostNode.getSysOid() != null && hostNode.getSysOid().trim().length() > 0) {
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.311."))
					subtype = Constant.TYPE_HOST_SUBTYPE_WINDOWS;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.2021."))
					subtype = Constant.TYPE_HOST_SUBTYPE_LINUX;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.2."))
					subtype = Constant.TYPE_HOST_SUBTYPE_AIX;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.11."))
					subtype = Constant.TYPE_HOST_SUBTYPE_HPUNIX;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.42."))
					subtype = Constant.TYPE_HOST_SUBTYPE_SOLARIS;
				if (hostNode.getSysOid().startsWith("scoopenserver"))
					subtype = Constant.TYPE_HOST_SUBTYPE_SCOOPENSERVER;
				if (hostNode.getSysOid().startsWith("scounix"))
					subtype = Constant.TYPE_HOST_SUBTYPE_SCOUNIX;
				if (hostNode.getSysOid().startsWith("as400"))
					subtype = Constant.TYPE_HOST_SUBTYPE_AS400;
			}
		} else if (hostNode.getCategory() == 9) {
			type = Constant.TYPE_ATM;
			subtype = Constant.TYPE_ATM_SUBTYPE_ATM;
		} else if (hostNode.getCategory() == 12) {
			// type = Constant.TYPE_VPN;
			type = Constant.TYPE_VPN;
			if (hostNode.getSysOid() != null && hostNode.getSysOid().trim().length() > 0) {
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.7564"))
					subtype = Constant.TYPE_VPN_SUBTYPE_ARRAYNETWORKS;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.25506."))
					subtype = Constant.TYPE_NET_SUBTYPE_H3C;
			}
		} else if (hostNode.getCategory() == 8) {
			type = Constant.TYPE_FIREWALL;
			if (hostNode.getSysOid() != null && hostNode.getSysOid().trim().length() > 0) {
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.28557."))
					subtype = Constant.TYPE_FIREWALL_SUBTYPE_HILLSTONE;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.3224."))
					subtype = Constant.TYPE_FIREWALL_SUBTYPE_NETSCREEN;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.2636."))
					subtype = Constant.TYPE_FIREWALL_SUBTYPE_NETSCREEN;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.94."))
					subtype = Constant.TYPE_FIREWALL_SUBTYPE_NOKIA;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.31648."))
					subtype = Constant.TYPE_FIREWALL_SUBTYPE_DPTECH;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.15227."))
					subtype = Constant.TYPE_FIREWALL_SUBTYPE_VENUS;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.10734."))
					subtype = Constant.TYPE_FIREWALL_SUBTYPE_TIPPINGPOINT;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.24968."))
					subtype = Constant.TYPE_FIREWALL_SUBTYPE_SECWORLD;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.14331."))
					subtype = Constant.TYPE_FIREWALL_SUBTYPE_TOPSEC;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.4881."))
					subtype = Constant.TYPE_FIREWALL_SUBTYPE_REDGIANT;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.25506."))
					subtype = Constant.TYPE_NET_SUBTYPE_H3C;
			}
		} else if (hostNode.getCategory() == 10) {
			type = Constant.TYPE_GATEWAY;
			subtype = Constant.TYPE_GATEWAY_SUBTYPE_CISCO;
		} else if (hostNode.getCategory() == 11) {
			type = Constant.TYPE_F5;
			subtype = Constant.TYPE_F5_SUBTYPE_F5;
			if (hostNode.getSysOid() != null && hostNode.getSysOid().trim().length() > 0) {
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.35047."))
					subtype = Constant.TYPE_BALANCER_SANGFOR;
			}
		} else if (hostNode.getCategory() == 13) {
			type = Constant.TYPE_BEHAVIOR;
			subtype = Constant.TYPE_BEHAVIOR_SUBTYPE_SANGFOR;
			if (hostNode.getSysOid() != null && hostNode.getSysOid().trim().length() > 0) {
				if (hostNode.getSysOid().startsWith("Sangfor"))
					subtype = Constant.TYPE_BEHAVIOR_SUBTYPE_SANGFOR;
			}
		} else if (hostNode.getCategory() == 14) {
			// type = Constant.TYPE_FLOWCONTROL;
			// subtype = Constant.TYPE_FLOWCONTROL_SUBTYPE_DHC;
			type = Constant.TYPE_STORAGE;
			subtype = Constant.TYPE_STORAGE_SUBTYPE_HDS;
			// if(hostNode.getSysOid() != null &&
			// hostNode.getSysOid().trim().length()>0){
			// if(hostNode.getSysOid().startsWith("DHC"))subtype =
			// Constant.TYPE_BALANCER_SANGFOR;
			// }
			if (hostNode.getSysOid() != null && hostNode.getSysOid().trim().length() > 0) {
				if (hostNode.getSysOid().startsWith("hds"))
					subtype = Constant.TYPE_STORAGE_SUBTYPE_HDS;
			}
			if ((hostNode.getSysOid() != null && hostNode.getSysOid().startsWith("1.3.6.1.4.1.789")) || hostNode.getSysOid().startsWith("netapp")) {
				subtype = Constant.TYPE_STORAGE_SUBTYPE_NETAPP;
			}
			if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.11.2.3.7.11")||hostNode.getOstype()==44)
				subtype = Constant.TYPE_STORAGE_SUBTYPE_HP;
		} else if (hostNode.getCategory() == 15) {
			// type = Constant.TYPE_DDOS;
			// subtype = Constant.TYPE_DDOS_SUBTYPE_NSFOCUS;
			// if(hostNode.getSysOid() != null &&
			// hostNode.getSysOid().trim().length()>0){
			// if(hostNode.getSysOid().startsWith("Nsfocus"))subtype =
			// Constant.TYPE_DDOS_SUBTYPE_NSFOCUS;
			// }
			type = Constant.TYPE_VIRTUAL;
			if (hostNode.getSysOid() != null && hostNode.getSysOid().trim().length() > 0) {
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.6876."))
					subtype = Constant.TYPE_VIRTUAL_SUBTYPE_VMWARE;
			}
		} else if (hostNode.getCategory() == 16) {
			type = Constant.TYPE_XSCAN;
			subtype = Constant.TYPE_XSCAN_SUBTYPE_VENUSTECH;
			if (hostNode.getSysOid() != null && hostNode.getSysOid().trim().length() > 0) {
				if (hostNode.getSysOid().startsWith("Venustech"))
					subtype = Constant.TYPE_XSCAN_SUBTYPE_VENUSTECH;
			}
		} else if (hostNode.getCategory() == 17) {
			type = Constant.TYPE_LOGAUDIT;
			subtype = Constant.TYPE_LOGAUDIT_SUBTYPE_TOPSEC;
			if (hostNode.getSysOid() != null && hostNode.getSysOid().trim().length() > 0) {
				if (hostNode.getSysOid().startsWith("TopSec"))
					subtype = Constant.TYPE_LOGAUDIT_SUBTYPE_TOPSEC;
			}
		} else {
			// 网络设备,需要判断操作系统
			type = Constant.TYPE_NET;
			if (hostNode.getSysOid() != null && hostNode.getSysOid().trim().length() > 0) {
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.9."))
					subtype = Constant.TYPE_NET_SUBTYPE_CISCO;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.2011."))
					subtype = Constant.TYPE_NET_SUBTYPE_H3C;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.25506."))
					subtype = Constant.TYPE_NET_SUBTYPE_H3C;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.9.2.1.57."))
					subtype = Constant.TYPE_NET_SUBTYPE_ENTRASYS;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.89."))
					subtype = Constant.TYPE_NET_SUBTYPE_RADWARE;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.5651."))
					subtype = Constant.TYPE_NET_SUBTYPE_MAIPU;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.4881."))
					subtype = Constant.TYPE_NET_SUBTYPE_REDGIANT;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.45.") || hostNode.getSysOid().startsWith("1.3.6.1.4.1.2272.31"))
					subtype = Constant.TYPE_NET_SUBTYPE_NORTHTEL;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.171."))
					subtype = Constant.TYPE_NET_SUBTYPE_DLINK;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.3320."))
					subtype = Constant.TYPE_NET_SUBTYPE_BDCOM;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.3902."))
					subtype = Constant.TYPE_NET_SUBTYPE_ZTE;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.1588.2.1.1."))
					subtype = Constant.TYPE_NET_SUBTYPE_BROCADE;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.11.2.3.7.11"))
					subtype = Constant.TYPE_NET_SUBTYPE_HP;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.800."))
					subtype = Constant.TYPE_NET_SUBTYPE_ALCATEL;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.45."))
					subtype = Constant.TYPE_NET_SUBTYPE_AVAYA;
				if (hostNode.getSysOid().startsWith("other"))
					subtype = Constant.TYPE_NET_SUBTYPE_OTHER;
				// if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.1588"))subtype
				// = Constant.TYPE_NET_SUBTYPE_IBM;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.2636."))
					subtype = Constant.TYPE_NET_SUBTYPE_JUNIPER;
			}
		}

		nodeDTO.setSubtype(subtype);
		nodeDTO.setType(type);
		// SysLogger.info(nodeDTO.getIpaddress()+"==="+nodeDTO.getType()+"==="+nodeDTO.getSubtype()+"=="+nodeDTO.getIpaddress());
		return nodeDTO;
	}

	/**
	 * 通过 Host 转换成 NodeDTO
	 * 
	 * @param host
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(Host hostNode) {

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(hostNode.getId());
		nodeDTO.setName(hostNode.getAlias());
		nodeDTO.setIpaddress(hostNode.getIpAddress());
		nodeDTO.setNodeid(String.valueOf(hostNode.getId()));
		nodeDTO.setBusinessId(hostNode.getBid());

		String subtype = "";

		String type = "";
		if (hostNode.getCategory() == 4) {
			type = Constant.TYPE_HOST;
			if (hostNode.getSysOid() != null && hostNode.getSysOid().trim().length() > 0) {
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.311."))
					subtype = Constant.TYPE_HOST_SUBTYPE_WINDOWS;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.2021."))
					subtype = Constant.TYPE_HOST_SUBTYPE_LINUX;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.2."))
					subtype = Constant.TYPE_HOST_SUBTYPE_AIX;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.11."))
					subtype = Constant.TYPE_HOST_SUBTYPE_HPUNIX;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.42."))
					subtype = Constant.TYPE_HOST_SUBTYPE_SOLARIS;
				if (hostNode.getSysOid().startsWith("as400"))
					subtype = Constant.TYPE_HOST_SUBTYPE_AS400;
				if (hostNode.getSysOid().startsWith("scounix"))
					subtype = Constant.TYPE_HOST_SUBTYPE_SCOUNIX;
				if (hostNode.getSysOid().startsWith("scoopenserver"))
					subtype = Constant.TYPE_HOST_SUBTYPE_SCOOPENSERVER;
				if (hostNode.getOstype() == 15)
					subtype = Constant.TYPE_HOST_SUBTYPE_AS400;
			}
		} else if (hostNode.getCategory() == 9) {
			type = Constant.TYPE_ATM;
			subtype = Constant.TYPE_ATM_SUBTYPE_ATM;
		} else if (hostNode.getCategory() == 12) {
			type = Constant.TYPE_VPN;
			if (hostNode.getSysOid() != null && hostNode.getSysOid().trim().length() > 0) {
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.7564"))
					subtype = Constant.TYPE_VPN_SUBTYPE_ARRAYNETWORKS;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.25506."))
					subtype = Constant.TYPE_NET_SUBTYPE_H3C;

			}

		} else if (hostNode.getCategory() == 8) {
			type = Constant.TYPE_FIREWALL;
			if (hostNode.getSysOid() != null && !hostNode.getSysOid().equals("null") && hostNode.getSysOid().trim().length() > 0) {
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.28557."))
					subtype = Constant.TYPE_FIREWALL_SUBTYPE_HILLSTONE;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.3224."))
					subtype = Constant.TYPE_FIREWALL_SUBTYPE_NETSCREEN;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.2636."))
					subtype = Constant.TYPE_FIREWALL_SUBTYPE_NETSCREEN;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.94."))
					subtype = Constant.TYPE_FIREWALL_SUBTYPE_NOKIA;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.15227."))
					subtype = Constant.TYPE_FIREWALL_SUBTYPE_VENUS;
				// if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.15227."))subtype
				// = Constant.TYPE_FIREWALL_SUBTYPE_NIPS;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.10734."))
					subtype = Constant.TYPE_FIREWALL_SUBTYPE_TIPPINGPOINT;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.24968."))
					subtype = Constant.TYPE_FIREWALL_SUBTYPE_SECWORLD;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.14331."))
					subtype = Constant.TYPE_FIREWALL_SUBTYPE_TOPSEC;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.31648."))
					subtype = Constant.TYPE_FIREWALL_SUBTYPE_DPTECH;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.25506."))
					subtype = Constant.TYPE_NET_SUBTYPE_H3C;
			} else {
				if (hostNode.getOstype() == 23)
					subtype = Constant.TYPE_FIREWALL_SUBTYPE_NOKIA;
				if (hostNode.getOstype() == 24)
					subtype = Constant.TYPE_FIREWALL_SUBTYPE_NETSCREEN;
				// if(hostNode.getOstype()==25)subtype=
				// Constant.TYPE_FIREWALL_SUBTYPe;
				if (hostNode.getOstype() == 26)
					subtype = Constant.TYPE_FIREWALL_SUBTYPE_HILLSTONE;
				if (hostNode.getOstype() == 29)
					subtype = Constant.TYPE_FIREWALL_SUBTYPE_DPTECH;
				if (hostNode.getOstype() == 31)
					subtype = Constant.TYPE_FIREWALL_SUBTYPE_TIPPINGPOINT;
				if (hostNode.getOstype() == 32)
					subtype = Constant.TYPE_FIREWALL_SUBTYPE_VENUS;
				if (hostNode.getOstype() == 27)
					subtype = Constant.TYPE_FIREWALL_SUBTYPE_REDGIANT;
				if (hostNode.getOstype() == 38)
					subtype = Constant.TYPE_FIREWALL_SUBTYPE_TOPSEC;
				if (hostNode.getOstype() == 39)
					subtype = Constant.TYPE_FIREWALL_SUBTYPE_CHINAGUARD;
			}
		} else if (hostNode.getCategory() == 10) {
			type = Constant.TYPE_GATEWAY;
			subtype = Constant.TYPE_GATEWAY_SUBTYPE_CISCO;
		} else if (hostNode.getCategory() == 11) {
			type = Constant.TYPE_F5;
			subtype = Constant.TYPE_F5_SUBTYPE_F5;
			if (hostNode.getSysOid() != null && hostNode.getSysOid().trim().length() > 0) {
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.35047."))
					subtype = Constant.TYPE_BALANCER_SANGFOR;
			}
		} else if (hostNode.getCategory() == 13) {
			type = Constant.TYPE_BEHAVIOR;
			subtype = Constant.TYPE_BEHAVIOR_SUBTYPE_SANGFOR;
			if (hostNode.getSysOid() != null && hostNode.getSysOid().trim().length() > 0) {
				if (hostNode.getSysOid().startsWith("Sangfor"))
					subtype = Constant.TYPE_BEHAVIOR_SUBTYPE_SANGFOR;
			}
		} else if (hostNode.getCategory() == 14) {
			// type = Constant.TYPE_FLOWCONTROL;
			// subtype = Constant.TYPE_FLOWCONTROL_SUBTYPE_DHC;
			// if(hostNode.getSysOid() != null &&
			// hostNode.getSysOid().trim().length()>0){
			// if(hostNode.getSysOid().startsWith("DHC"))subtype =
			// Constant.TYPE_BALANCER_SANGFOR;
			// }
			// type = Constant.TYPE_FLOWCONTROL;
			// subtype = Constant.TYPE_FLOWCONTROL_SUBTYPE_DHC;
			type = Constant.TYPE_STORAGE;
			subtype = Constant.TYPE_STORAGE_SUBTYPE_HDS;
			// if(hostNode.getSysOid() != null &&
			// hostNode.getSysOid().trim().length()>0){
			// if(hostNode.getSysOid().startsWith("DHC"))subtype =
			// Constant.TYPE_BALANCER_SANGFOR;
			// }
			if (hostNode.getSysOid() != null && hostNode.getSysOid().trim().length() > 0) {
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.789."))
					subtype = Constant.TYPE_STORAGE_SUBTYPE_NETAPP;
				if (hostNode.getSysOid().startsWith("hds"))
					subtype = Constant.TYPE_STORAGE_SUBTYPE_HDS;

				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.1981.1"))
					subtype = Constant.TYPE_STORAGE_SUBTYPE_EMC_VNX;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.1981.2"))
					subtype = Constant.TYPE_STORAGE_SUBTYPE_EMC_DMX;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.1981.3"))
					subtype = Constant.TYPE_STORAGE_SUBTYPE_EMC_VMAX;
                if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.11.2.3.7.11"))
					subtype = Constant.TYPE_STORAGE_SUBTYPE_HP;
			}
			if (hostNode.getOstype()==44){
				subtype = Constant.TYPE_STORAGE_SUBTYPE_HP;
			}
		} else if (hostNode.getCategory() == 15) {
			// type = Constant.TYPE_DDOS;
			// subtype = Constant.TYPE_DDOS_SUBTYPE_NSFOCUS;
			// if(hostNode.getSysOid() != null &&
			// hostNode.getSysOid().trim().length()>0){
			// if(hostNode.getSysOid().startsWith("Nsfocus"))subtype =
			// Constant.TYPE_DDOS_SUBTYPE_NSFOCUS;
			// }
			type = Constant.TYPE_VIRTUAL;
			if (hostNode.getSysOid() != null && hostNode.getSysOid().trim().length() > 0) {
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.6876."))
					subtype = Constant.TYPE_VIRTUAL_SUBTYPE_VMWARE;
			}
		} else if (hostNode.getCategory() == 16) {
			type = Constant.TYPE_XSCAN;
			subtype = Constant.TYPE_XSCAN_SUBTYPE_VENUSTECH;
			if (hostNode.getSysOid() != null && hostNode.getSysOid().trim().length() > 0) {
				if (hostNode.getSysOid().startsWith("Venustech"))
					subtype = Constant.TYPE_XSCAN_SUBTYPE_VENUSTECH;
			}
		} else if (hostNode.getCategory() == 17) {
			type = Constant.TYPE_LOGAUDIT;
			subtype = Constant.TYPE_LOGAUDIT_SUBTYPE_TOPSEC;
			if (hostNode.getSysOid() != null && hostNode.getSysOid().trim().length() > 0) {
				if (hostNode.getSysOid().startsWith("TopSec"))
					subtype = Constant.TYPE_LOGAUDIT_SUBTYPE_TOPSEC;
			}
		} else {
			// 网络设备,需要判断操作系统
			type = Constant.TYPE_NET;
			if (hostNode.getOstype()==2)
				subtype = Constant.TYPE_NET_SUBTYPE_H3C;
			if (hostNode.getSysOid() != null && hostNode.getSysOid().trim().length() > 0) {
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.9."))
					subtype = Constant.TYPE_NET_SUBTYPE_CISCO;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.2011."))
					subtype = Constant.TYPE_NET_SUBTYPE_H3C;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.25506."))
					subtype = Constant.TYPE_NET_SUBTYPE_H3C;
				
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.9.2.1.57."))
					subtype = Constant.TYPE_NET_SUBTYPE_ENTRASYS;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.89."))
					subtype = Constant.TYPE_NET_SUBTYPE_RADWARE;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.5651."))
					subtype = Constant.TYPE_NET_SUBTYPE_MAIPU;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.4881."))
					subtype = Constant.TYPE_NET_SUBTYPE_REDGIANT;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.45.") || hostNode.getSysOid().startsWith("1.3.6.1.4.1.2272.31"))
					subtype = Constant.TYPE_NET_SUBTYPE_NORTHTEL;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.171."))
					subtype = Constant.TYPE_NET_SUBTYPE_DLINK;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.3320."))
					subtype = Constant.TYPE_NET_SUBTYPE_BDCOM;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.3902."))
					subtype = Constant.TYPE_NET_SUBTYPE_ZTE;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.1588.2.1.1."))
					subtype = Constant.TYPE_NET_SUBTYPE_BROCADE;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.11.2.3.7.11"))
					subtype = Constant.TYPE_NET_SUBTYPE_HP;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.800."))
					subtype = Constant.TYPE_NET_SUBTYPE_ALCATEL;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.45."))
					subtype = Constant.TYPE_NET_SUBTYPE_AVAYA;
				if (hostNode.getSysOid().startsWith("other"))
					subtype = Constant.TYPE_NET_SUBTYPE_OTHER;
				// if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.1588"))subtype
				// = Constant.TYPE_NET_SUBTYPE_IBM;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.2636."))
					subtype = Constant.TYPE_NET_SUBTYPE_JUNIPER;
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.2620."))
					subtype = Constant.TYPE_NET_SUBTYPE_CHECKPOINT;

			}
		}

		nodeDTO.setSubtype(subtype);
		nodeDTO.setType(type);
		return nodeDTO;
	}

	/**
	 * 通过 Mail 转换成 NodeDTO
	 * 
	 * @param host
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(Mail mail) {

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(mail.getId());
		nodeDTO.setName(mail.getAlias());
		nodeDTO.setIpaddress(mail.getIpAddress());
		nodeDTO.setNodeid(String.valueOf(mail.getId()));
		nodeDTO.setBusinessId(mail.getBid());

		String subtype = "mail";

		String type = "service";

		nodeDTO.setSubtype(subtype);
		nodeDTO.setType(type);
		return nodeDTO;
	}

	/**
	 * 通过 DHCP 转换成 NodeDTO
	 * 
	 * @param host
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(DHCP dhcp) {

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(dhcp.getId());
		nodeDTO.setName(dhcp.getAlias());
		nodeDTO.setIpaddress(dhcp.getIpAddress());
		nodeDTO.setNodeid(String.valueOf(dhcp.getId()));
		nodeDTO.setBusinessId(dhcp.getBid());

		String subtype = "dhcp";

		String type = "service";

		nodeDTO.setSubtype(subtype);
		nodeDTO.setType(type);
		return nodeDTO;
	}

	/**
	 * 通过 Ftp 转换成 NodeDTO
	 * 
	 * @param host
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(Ftp ftp) {

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(ftp.getId());
		nodeDTO.setName(ftp.getAlias());
		nodeDTO.setIpaddress(ftp.getIpAddress());
		nodeDTO.setNodeid(String.valueOf(ftp.getId()));
		nodeDTO.setBusinessId(ftp.getBid());

		String subtype = "ftp";

		String type = "service";

		nodeDTO.setSubtype(subtype);
		nodeDTO.setType(type);
		return nodeDTO;
	}

	/**
	 * 通过 tFtp 转换成 NodeDTO
	 * 
	 * @param host
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(TFtp tftp) {

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(tftp.getId());
		nodeDTO.setName(tftp.getAlias());
		nodeDTO.setIpaddress(tftp.getIpAddress());
		nodeDTO.setNodeid(String.valueOf(tftp.getId()));
		nodeDTO.setBusinessId(tftp.getBid());

		String subtype = "tftp";

		String type = "service";

		nodeDTO.setSubtype(subtype);
		nodeDTO.setType(type);
		return nodeDTO;
	}

	/**
	 * 通过 Ftp 转换成 NodeDTO
	 * 
	 * @param host
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(com.afunms.polling.node.SocketService socket) {

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(socket.getId());
		nodeDTO.setName(socket.getAlias());
		nodeDTO.setIpaddress(socket.getIpAddress());
		nodeDTO.setNodeid(String.valueOf(socket.getId()));
		nodeDTO.setBusinessId(socket.getBid());

		String subtype = "socket";

		String type = "service";

		nodeDTO.setSubtype(subtype);
		nodeDTO.setType(type);
		return nodeDTO;
	}

	/**
	 * 通过 Ftp 转换成 NodeDTO
	 * 
	 * @param host
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(Web web) {

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(web.getId());
		nodeDTO.setName(web.getAlias());
		nodeDTO.setIpaddress(web.getIpAddress());
		nodeDTO.setNodeid(String.valueOf(web.getId()));
		nodeDTO.setBusinessId(web.getBid());

		String subtype = "url";

		String type = "service";

		nodeDTO.setSubtype(subtype);
		nodeDTO.setType(type);
		return nodeDTO;
	}
	
	/**
	 * 通过 Dp 转换成 NodeDTO
	 * 
	 * @param host
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(Dp dp) {

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(dp.getId());
		nodeDTO.setName(dp.getAlias());
		nodeDTO.setIpaddress(dp.getIpAddress());
		nodeDTO.setNodeid(String.valueOf(dp.getId()));
		nodeDTO.setBusinessId(dp.getBid());

		String subtype = "dp";

		String type = "service";

		nodeDTO.setSubtype(subtype);
		nodeDTO.setType(type);
		return nodeDTO;
	}
	
	/**
	 * 通过 Nas 转换成 NodeDTO
	 * 
	 * @param host
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(Nas nas) {

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(nas.getId());
		nodeDTO.setName(nas.getAlias());
		nodeDTO.setIpaddress(nas.getIpAddress());
		nodeDTO.setNodeid(String.valueOf(nas.getId()));
		nodeDTO.setBusinessId(nas.getBid());

		String subtype = "nas";

		String type = "service";

		nodeDTO.setSubtype(subtype);
		nodeDTO.setType(type);
		return nodeDTO;
	}
	
	/**
	 * 通过 Ggsci 转换成 NodeDTO
	 * 
	 * @param host
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(Ggsci ggsci) {

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(ggsci.getId());
		nodeDTO.setName(ggsci.getAlias());
		nodeDTO.setIpaddress(ggsci.getIpAddress());
		nodeDTO.setNodeid(String.valueOf(ggsci.getId()));
		nodeDTO.setBusinessId(ggsci.getBid());

		String subtype = "ggsci";

		String type = "service";

		nodeDTO.setSubtype(subtype);
		nodeDTO.setType(type);
		return nodeDTO;
	}
	/**
	 * 通过 Ntp 转换成 NodeDTO
	 * 
	 * @param host
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(Ntp ntp) {

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(ntp.getId());
		nodeDTO.setName(ntp.getAlias());
		nodeDTO.setIpaddress(ntp.getIpAddress());
		nodeDTO.setNodeid(String.valueOf(ntp.getId()));
		nodeDTO.setBusinessId(ntp.getBid());

		String subtype = "ntp";

		String type = "service";

		nodeDTO.setSubtype(subtype);
		nodeDTO.setType(type);
		return nodeDTO;
	}

	/**
	 * 通过 DBVo 转换成 NodeDTO
	 * 
	 * @param dbVo
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(DBVo dbVo) {

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(dbVo.getId());
		nodeDTO.setName(dbVo.getAlias());
		nodeDTO.setIpaddress(dbVo.getIpAddress());
		nodeDTO.setNodeid(String.valueOf(dbVo.getId()));
		nodeDTO.setBusinessId(dbVo.getBid());

		String type = Constant.TYPE_DB;
		String subtype = "";

		DBTypeDao typeDao = new DBTypeDao();
		try {
			DBTypeVo typeVo = (DBTypeVo) typeDao.findByID(String.valueOf(dbVo.getDbtype()));
			if (typeVo == null) {
				subtype = "";
			} else if ("oracle".equalsIgnoreCase(typeVo.getDbtype())) {
				subtype = Constant.TYPE_DB_SUBTYPE_ORACLE;
			} else if ("sqlserver".equalsIgnoreCase(typeVo.getDbtype())) {
				subtype = Constant.TYPE_DB_SUBTYPE_SQLSERVER;
			} else if ("mysql".equalsIgnoreCase(typeVo.getDbtype())) {
				subtype = Constant.TYPE_DB_SUBTYPE_MYSQL;
			} else if ("db2".equalsIgnoreCase(typeVo.getDbtype())) {
				subtype = Constant.TYPE_DB_SUBTYPE_DB2;
			} else if ("sybase".equalsIgnoreCase(typeVo.getDbtype())) {
				subtype = Constant.TYPE_DB_SUBTYPE_SYBASE;
			} else if ("informix".equalsIgnoreCase(typeVo.getDbtype())) {
				subtype = Constant.TYPE_DB_SUBTYPE_INFORMIX;
			} else if ("oraclerac".equalsIgnoreCase(typeVo.getDbtype())) {
				subtype = Constant.TYPE_DB_SUBTYPE_ORACLERAC;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			typeDao.close();
		}

		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);

		return nodeDTO;
	}

	/**
	 * 通过 OracleEntity 转换成 NodeDTO
	 * 
	 * @param oracleEntity
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(OracleEntity oracleEntity) {

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(oracleEntity.getId());
		nodeDTO.setName(oracleEntity.getAlias());
		// 设置IP
		DBDao dbdao = new DBDao();
		DBVo dbvo = null;
		try {
			dbvo = (DBVo) dbdao.findByID(oracleEntity.getDbid() + "");
		} catch (Exception e) {

		} finally {
			dbdao.close();
		}
		if (dbvo != null)
			nodeDTO.setIpaddress(dbvo.getIpAddress());

		nodeDTO.setNodeid(String.valueOf(oracleEntity.getId()));
		nodeDTO.setBusinessId(oracleEntity.getBid());

		String subtype = Constant.TYPE_DB_SUBTYPE_ORACLE;

		String type = Constant.TYPE_DB;

		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);

		return nodeDTO;
	}

	/**
	 * 通过 ApacheConfig 转换成 NodeDTO
	 * 
	 * @param apacheConfig
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(com.afunms.polling.node.ApachConfig apacheConfig) {

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(apacheConfig.getId());
		nodeDTO.setName(apacheConfig.getAlias());
		nodeDTO.setNodeid(String.valueOf(apacheConfig.getId()));
		nodeDTO.setBusinessId(apacheConfig.getNetid());
		nodeDTO.setIpaddress(apacheConfig.getIpaddress());

		String type = Constant.TYPE_MIDDLEWARE;
		String subtype = Constant.TYPE_MIDDLEWARE_SUBTYPE_APACHE;

		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);

		return nodeDTO;
	}

	/**
	 * 通过 ApacheConfig 转换成 NodeDTO
	 * 
	 * @param apacheConfig
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(ApacheConfig apacheConfig) {

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(apacheConfig.getId());
		nodeDTO.setName(apacheConfig.getAlias());
		nodeDTO.setNodeid(String.valueOf(apacheConfig.getId()));
		nodeDTO.setBusinessId(apacheConfig.getNetid());
		nodeDTO.setIpaddress(apacheConfig.getIpaddress());

		String type = Constant.TYPE_MIDDLEWARE;
		String subtype = Constant.TYPE_MIDDLEWARE_SUBTYPE_APACHE;

		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);

		return nodeDTO;
	}

	/**
	 * 通过 MgeUps 转换成 NodeDTO
	 * 
	 * @param mgeUps
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(MgeUps mgeUps) {

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(mgeUps.getId());
		nodeDTO.setName(mgeUps.getAlias());
		nodeDTO.setNodeid(String.valueOf(mgeUps.getId()));
		nodeDTO.setBusinessId(mgeUps.getBid());
		nodeDTO.setIpaddress(mgeUps.getIpAddress());
		nodeDTO.setType(mgeUps.getType());
		nodeDTO.setSubtype(mgeUps.getSubtype());

		return nodeDTO;
	}

	public NodeDTO creatNodeDTOByNode(UPSNode node) {

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(node.getId());
		nodeDTO.setName(node.getAlias());
		nodeDTO.setNodeid(String.valueOf(node.getId()));
		nodeDTO.setBusinessId(node.getBid());
		nodeDTO.setIpaddress(node.getIpAddress());
		nodeDTO.setType(node.getType());
		nodeDTO.setSubtype(node.getSubtype());

		return nodeDTO;
	}

	/**
	 * 通过 CicsConfig 转换成 NodeDTO
	 * 
	 * @param cicsConfig
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(CicsConfig cicsConfig) {

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(cicsConfig.getId());
		nodeDTO.setName(cicsConfig.getAlias());
		nodeDTO.setNodeid(String.valueOf(cicsConfig.getId()));
		nodeDTO.setBusinessId(cicsConfig.getNetid());
		nodeDTO.setIpaddress(cicsConfig.getIpaddress());

		String type = Constant.TYPE_MIDDLEWARE;
		String subtype = Constant.TYPE_MIDDLEWARE_SUBTYPE_CICS;

		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);

		return nodeDTO;
	}

	/**
	 * 通过 DnsConfig 转换成 NodeDTO
	 * 
	 * @param dnsConfig
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(DnsConfig dnsConfig) {

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(dnsConfig.getId());
		nodeDTO.setName(dnsConfig.getDns());
		nodeDTO.setNodeid(String.valueOf(dnsConfig.getId()));
		nodeDTO.setBusinessId(dnsConfig.getNetid());
		nodeDTO.setIpaddress(dnsConfig.getDnsip());

		String type = Constant.TYPE_MIDDLEWARE;
		String subtype = Constant.TYPE_MIDDLEWARE_SUBTYPE_DNS;

		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);

		return nodeDTO;
	}

	/**
	 * 通过 DominoConfig 转换成 NodeDTO
	 * 
	 * @param dominoConfig
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(DominoConfig dominoConfig) {

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(dominoConfig.getId());
		nodeDTO.setName(dominoConfig.getName());
		nodeDTO.setNodeid(String.valueOf(dominoConfig.getId()));
		nodeDTO.setBusinessId(dominoConfig.getNetid());
		nodeDTO.setIpaddress(dominoConfig.getIpaddress());

		String type = Constant.TYPE_MIDDLEWARE;
		String subtype = Constant.TYPE_MIDDLEWARE_SUBTYPE_DOMINO;

		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);

		return nodeDTO;
	}

	/**
	 * 通过 IISConfig 转换成 NodeDTO
	 * 
	 * @param iISConfig
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(IISConfig iISConfig) {

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(iISConfig.getId());
		nodeDTO.setName(iISConfig.getName());
		nodeDTO.setNodeid(String.valueOf(iISConfig.getId()));
		nodeDTO.setBusinessId(iISConfig.getNetid());
		nodeDTO.setIpaddress(iISConfig.getIpaddress());

		String type = Constant.TYPE_MIDDLEWARE;
		String subtype = Constant.TYPE_MIDDLEWARE_SUBTYPE_IIS;

		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);

		return nodeDTO;
	}

	/**
	 * 通过 JBossConfig 转换成 NodeDTO
	 * 
	 * @param jBossConfig
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(JBossConfig jBossConfig) {

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(jBossConfig.getId());
		nodeDTO.setName(jBossConfig.getAlias());
		nodeDTO.setNodeid(String.valueOf(jBossConfig.getId()));
		nodeDTO.setBusinessId(jBossConfig.getNetid());
		nodeDTO.setIpaddress(jBossConfig.getIpaddress());

		String type = Constant.TYPE_MIDDLEWARE;
		String subtype = Constant.TYPE_MIDDLEWARE_SUBTYPE_JBOSS;

		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);

		return nodeDTO;
	}

	/**
	 * 通过 MQConfig 转换成 NodeDTO
	 * 
	 * @param mQConfig
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(MQConfig mQConfig) {

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(mQConfig.getId());
		nodeDTO.setName(mQConfig.getName());
		nodeDTO.setNodeid(String.valueOf(mQConfig.getId()));
		nodeDTO.setBusinessId(mQConfig.getNetid());
		nodeDTO.setIpaddress(mQConfig.getIpaddress());

		String type = Constant.TYPE_MIDDLEWARE;
		String subtype = Constant.TYPE_MIDDLEWARE_SUBTYPE_MQ;

		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);

		return nodeDTO;
	}

	/**
	 * 通过 Tomcat 转换成 NodeDTO
	 * 
	 * @param tomcat
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(Tomcat tomcat) {

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(tomcat.getId());
		nodeDTO.setName(tomcat.getAlias());
		nodeDTO.setNodeid(String.valueOf(tomcat.getId()));
		nodeDTO.setBusinessId(tomcat.getBid());
		nodeDTO.setIpaddress(tomcat.getIpAddress());

		String type = Constant.TYPE_MIDDLEWARE;
		String subtype = Constant.TYPE_MIDDLEWARE_SUBTYPE_TOMCAT;

		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);

		return nodeDTO;
	}

	/**
	 * 通过 Tomcat 转换成 NodeDTO
	 * 
	 * @param tomcat
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(Resin resin) {

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(resin.getId());
		nodeDTO.setName(resin.getAlias());
		nodeDTO.setNodeid(String.valueOf(resin.getId()));
		nodeDTO.setBusinessId(resin.getBid());
		nodeDTO.setIpaddress(resin.getIpAddress());

		String type = Constant.TYPE_MIDDLEWARE;
		String subtype = Constant.TYPE_MIDDLEWARE_SUBTYPE_RESIN;

		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);

		return nodeDTO;
	}

	/**
	 * 通过 TuxedoConfig 转换成 NodeDTO
	 * 
	 * @param tuxedoConfig
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(TuxedoConfig tuxedoConfig) {

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(tuxedoConfig.getId());
		nodeDTO.setName(tuxedoConfig.getName());
		nodeDTO.setNodeid(String.valueOf(tuxedoConfig.getId()));
		nodeDTO.setBusinessId(tuxedoConfig.getBid());
		nodeDTO.setIpaddress(tuxedoConfig.getIpAddress());

		String type = Constant.TYPE_MIDDLEWARE;
		String subtype = Constant.TYPE_MIDDLEWARE_SUBTYPE_TUXEDO;

		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);

		return nodeDTO;
	}

	/**
	 * 通过 WasConfig 转换成 NodeDTO
	 * 
	 * @param wasConfig
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(WasConfig wasConfig) {

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(wasConfig.getId());
		nodeDTO.setName(wasConfig.getName());
		nodeDTO.setNodeid(String.valueOf(wasConfig.getId()));
		nodeDTO.setBusinessId(wasConfig.getNetid());
		nodeDTO.setIpaddress(wasConfig.getIpaddress());

		String type = Constant.TYPE_MIDDLEWARE;
		String subtype = Constant.TYPE_MIDDLEWARE_SUBTYPE_WAS;

		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);

		return nodeDTO;
	}

	/**
	 * 通过 WeblogicConfig 转换成 NodeDTO
	 * 
	 * @param weblogicConfig
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(WeblogicConfig weblogicConfig) {

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(weblogicConfig.getId());
		nodeDTO.setName(weblogicConfig.getAlias());
		nodeDTO.setNodeid(String.valueOf(weblogicConfig.getId()));
		nodeDTO.setBusinessId(weblogicConfig.getNetid());
		nodeDTO.setIpaddress(weblogicConfig.getIpAddress());

		String type = Constant.TYPE_MIDDLEWARE;
		String subtype = Constant.TYPE_MIDDLEWARE_SUBTYPE_WEBLOGIC;

		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);

		return nodeDTO;
	}

	/**
	 * 通过 EmailMonitorConfig 转换成 NodeDTO
	 * 
	 * @param emailMonitorConfig
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(EmailMonitorConfig emailMonitorConfig) {

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(emailMonitorConfig.getId());
		nodeDTO.setName(emailMonitorConfig.getName());
		nodeDTO.setNodeid(String.valueOf(emailMonitorConfig.getId()));
		nodeDTO.setBusinessId(emailMonitorConfig.getBid());
		nodeDTO.setIpaddress(emailMonitorConfig.getIpaddress());

		String type = Constant.TYPE_SERVICE;
		String subtype = Constant.TYPE_SERVICE_SUBTYPE_EMAIL;

		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);

		return nodeDTO;
	}

	/**
	 * 通过 FTPConfig 转换成 NodeDTO
	 * 
	 * @param fTPConfig
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(FTPConfig fTPConfig) {

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(fTPConfig.getId());
		nodeDTO.setName(fTPConfig.getName());
		nodeDTO.setNodeid(String.valueOf(fTPConfig.getId()));
		nodeDTO.setBusinessId(fTPConfig.getBid());
		nodeDTO.setIpaddress(fTPConfig.getIpaddress());

		String type = Constant.TYPE_SERVICE;
		String subtype = Constant.TYPE_SERVICE_SUBTYPE_FTP;

		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);

		return nodeDTO;
	}

	/**
	 * 通过 tFTPConfig 转换成 NodeDTO
	 * 
	 * @param fTPConfig
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(TFTPConfig tfTPConfig) {

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(tfTPConfig.getId());
		nodeDTO.setName(tfTPConfig.getName());
		nodeDTO.setNodeid(String.valueOf(tfTPConfig.getId()));
		nodeDTO.setBusinessId(tfTPConfig.getBid());
		nodeDTO.setIpaddress(tfTPConfig.getIpaddress());

		String type = Constant.TYPE_SERVICE;
		String subtype = Constant.TYPE_SERVICE_SUBTYPE_TFTP;

		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);

		return nodeDTO;
	}

	/**
	 * 通过 tFTPConfig 转换成 NodeDTO
	 * 
	 * @param fTPConfig
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(DHCPConfig dhcpConfig) {

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(dhcpConfig.getId());
		nodeDTO.setName(dhcpConfig.getAlias());
		nodeDTO.setNodeid(String.valueOf(dhcpConfig.getId()));
		nodeDTO.setBusinessId(dhcpConfig.getNetid());
		nodeDTO.setIpaddress(dhcpConfig.getIpAddress());

		String type = Constant.TYPE_SERVICE;
		String subtype = Constant.TYPE_SERVICE_SUBTYPE_DHCP;

		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);

		return nodeDTO;
	}

	/**
	 * 通过 Procs 转换成 NodeDTO
	 * 
	 * @param procs
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(Procs procs) {

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(procs.getId());
		nodeDTO.setName(procs.getProcname());
		nodeDTO.setNodeid(String.valueOf(procs.getId()));
		// nodeDTO.setBusinessId(procs.get);
		nodeDTO.setIpaddress(procs.getIpaddress());

		String type = Constant.TYPE_SERVICE;
		String subtype = Constant.TYPE_SERVICE_SUBTYPE_HOSTPROCESS;

		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);

		return nodeDTO;
	}

	/**
	 * 通过 PSTypeVo 转换成 NodeDTO
	 * 
	 * @param psTypeVo
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(PSTypeVo psTypeVo) {

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(psTypeVo.getId());
		nodeDTO.setName(psTypeVo.getPortdesc() + "-" + psTypeVo.getPort());
		nodeDTO.setNodeid(String.valueOf(psTypeVo.getId()));
		nodeDTO.setBusinessId(psTypeVo.getBid());
		nodeDTO.setIpaddress(psTypeVo.getIpaddress());

		String type = Constant.TYPE_SERVICE;
		String subtype = Constant.TYPE_SERVICE_SUBTYPE_PORT;

		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);

		return nodeDTO;
	}

	/**
	 * 通过 WebConfig 转换成 NodeDTO
	 * 
	 * @param webConfig
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(WebConfig webConfig) {

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(webConfig.getId());
		nodeDTO.setName(webConfig.getAlias());
		nodeDTO.setNodeid(String.valueOf(webConfig.getId()));
		nodeDTO.setBusinessId(webConfig.getNetid());
		nodeDTO.setIpaddress(webConfig.getIpAddress());

		String type = Constant.TYPE_SERVICE;
		String subtype = Constant.TYPE_SERVICE_SUBTYPE_WEB;

		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);

		return nodeDTO;
	}
	
	/**
	 * 通过 DpConfig 转换成 NodeDTO
	 * 
	 * @param dpConfig
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(DpConfig dpConfig) {

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(dpConfig.getId());
		nodeDTO.setName(dpConfig.getAlias());
		nodeDTO.setNodeid(String.valueOf(dpConfig.getId()));
		nodeDTO.setBusinessId(dpConfig.getNetid());
		nodeDTO.setIpaddress(dpConfig.getIpAddress());

		String type = Constant.TYPE_SERVICE;
		String subtype = Constant.TYPE_SERVICE_SUBTYPE_DP;

		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);

		return nodeDTO;
	}
	
	/**
	 * 通过 GgsciConfig 转换成 NodeDTO
	 * 
	 * @param dpConfig
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(GgsciConfig ggsciConfig) {

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(ggsciConfig.getId());
		nodeDTO.setName(ggsciConfig.getAlias());
		nodeDTO.setNodeid(String.valueOf(ggsciConfig.getId()));
		nodeDTO.setBusinessId(ggsciConfig.getNetid());
		nodeDTO.setIpaddress(ggsciConfig.getIpAddress());

		String type = Constant.TYPE_SERVICE;
		String subtype = Constant.TYPE_SERVICE_SUBTYPE_GGSCI;

		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);

		return nodeDTO;
	}
	
	/**
	 * 通过 nasConfig 转换成 NodeDTO
	 * 
	 * @param nasConfig
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(NasConfig nasConfig) {

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(nasConfig.getId());
		nodeDTO.setName(nasConfig.getAlias());
		nodeDTO.setNodeid(String.valueOf(nasConfig.getId()));
		nodeDTO.setBusinessId(nasConfig.getNetid());
		nodeDTO.setIpaddress(nasConfig.getIpAddress());

		String type = Constant.TYPE_SERVICE;
		String subtype = Constant.TYPE_SERVICE_SUBTYPE_NAS;

		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);

		return nodeDTO;
	}
	
	/**
	 * 通过 ntpConfig 转换成 NodeDTO
	 * 
	 * @param nasConfig
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(NtpConfig ntpConfig) {

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(ntpConfig.getId());
		nodeDTO.setName(ntpConfig.getAlias());
		nodeDTO.setNodeid(String.valueOf(ntpConfig.getId()));
		nodeDTO.setBusinessId(ntpConfig.getNetid());
		nodeDTO.setIpaddress(ntpConfig.getIpAddress());

		String type = Constant.TYPE_SERVICE;
		String subtype = Constant.TYPE_SERVICE_SUBTYPE_NTP;

		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);

		return nodeDTO;
	}


	/**
	 * 通过 manageXml 转换成 NodeDTO
	 * 
	 * @param manageXml
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(ManageXml manageXml) {

		NodeDTO nodeDTO = new NodeDTO();

		nodeDTO.setId(manageXml.getId());
		nodeDTO.setName(manageXml.getTopoName());
		nodeDTO.setNodeid(String.valueOf(manageXml.getId()));
		nodeDTO.setBusinessId(manageXml.getBid());
		nodeDTO.setIpaddress("");

		String type = Constant.TYPE_TOPO;
		String subtype = Constant.TYPE_TOPO_SUBTYPE_BUSINESSVIEW;

		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);

		return nodeDTO;
	}

	/**
	 * 根据 bid 获取 bid 的名称
	 * 
	 * @param bid
	 * @return
	 */
	public String getBusinessNameForNode(String bid) {
		// BusinessDao bussdao = new BusinessDao();
		List allbuss = null;
		try {
			// allbuss = bussdao.loadAll();
			allbuss = ShareData.getAllbussness();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (bid == null) {
			bid = "";
		}
		String id[] = bid.split(",");
		List bidlist = new ArrayList();
		if (id != null && id.length > 0) {
			for (int j = 0; j < id.length; j++) {
				bidlist.add(id[j]);
			}
		}
		String bussName = "";
		if (allbuss.size() > 0) {
			for (int k = 0; k < allbuss.size(); k++) {
				Business buss = (Business) allbuss.get(k);
				if (bidlist.contains(buss.getId() + "")) {
					bussName = bussName + ',' + buss.getName();
				}
			}
		}

		return bussName;
	}

	public List<NodeDTO> getByNodeTag(String nodeTag, String category, String name, List nodelist) {
		List<NodeDTO> list = null;
		if (nodelist != null) {
			list = new ArrayList<NodeDTO>();
			for (int i = 0; i < nodelist.size(); i++) {
				String nodestr = (String) nodelist.get(i);
				// System.out.println(nodestr);
				if (nodestr.indexOf(nodeTag) != -1 && !"bus".equalsIgnoreCase(nodeTag)) {
					// SysLogger.info("name--------"+name+"=="+nodestr.substring(3));
					Node node = PollingEngine.getInstance().getNodeByCategory(name, Integer.parseInt(nodestr.substring(3)));
					// SysLogger.info("category================="+category);
					String cate[] = category.split(",");
					if (node == null)
						continue;
					if (cate != null && cate.length > 0) {
						for (int j = 0; j < cate.length; j++) {
							// SysLogger.info("cate[j]===="+cate[j]+"
							// node.getCategory():"+node.getCategory()+"
							// node.isManaged():"+node.isManaged());
							if (cate[j] != null && !"null".equalsIgnoreCase(cate[j]) && !"".equalsIgnoreCase(cate[j]) && node.getCategory() == Integer.parseInt(cate[j]) && node.isManaged()) {
								NodeDTO nodeDTO = conversionToNodeDTO(node);
								list.add(nodeDTO);
							}
						}
					}
				}
			}
		}
		return list;
	}

	public List<BaseVo> getByNodeTag(String nodeTag, String category) {
		List<BaseVo> list = new ArrayList();
		List templist = null;
		try {
			if ("net".equalsIgnoreCase(nodeTag)) {
				// 网络设备主机

				String condition = " where 1=1 " + getMonFlagSql("managed");
				if (category == null) {
					category = "";
				}
				category = "," + category + ",";
				String[] categorys = category.split(",");
				if (categorys != null && categorys.length > 0) {
					int i = 0;
					for (String category_per : categorys) {
						if (category_per != null && category_per.trim().length() > 0) {
							if (i == 0) {
								condition = condition + " and (";
							} else {
								condition = condition + " or";
							}
							condition = condition + " category ='" + category_per + "'";
							i++;
							if (i == categorys.length - 1) {
								condition = condition + ") ";
							}
						}
					}
				}
				list = getHostNode(condition);

			} else if ("mid".equalsIgnoreCase(nodeTag)) {
				// 中间件
				list = getMiddlewareList();
			} else if ("ser".equalsIgnoreCase(nodeTag)) {
				// 服务
				list = getServiceList();
			} else if ("dom".equalsIgnoreCase(nodeTag)) {
				// domino
				// list = getMiddleware_DominoList();
				// list = ShareData.getDominolist();
				// 从内存中中读取 wxy upadate
				this.getDominoListByMon(list);
			} else if ("dbs".equalsIgnoreCase(nodeTag)) {
				// 数据库
				// list = getDBList();
				list = ShareData.getDbconfiglist();
			} else if ("tom".equalsIgnoreCase(nodeTag)) {
				// Tomcat
				// list = ShareData.getTomcatlist();
				// 从内存中中读取 wxy upadate
				this.getTomListByMon(list);
			} else if ("res".equalsIgnoreCase(nodeTag)) {
				// Resin
				// list = ShareData.getTomcatlist();
				// 从内存中中读取 wxy upadate
				this.getResinListByMon(list);
			} else if ("cic".equalsIgnoreCase(nodeTag)) {
				// Cics
				// list = getMiddleware_CICSList();
				// list = ShareData.getCicslist();
				// 从内存中中读取 wxy upadate
				this.getCicsListByMon(list);

			} else if ("mqs".equalsIgnoreCase(nodeTag)) {
				// MQ
				// list = ShareData.getMqlist();
				// 从内存中中读取 wxy upadate
				this.getMqListByMon(list);
			} else if ("was".equalsIgnoreCase(nodeTag)) {
				// WAS
				// list = getMiddleware_WasList();
				// list = ShareData.getWaslist();
				// 从内存中中读取 wxy upadate
				this.getWasListByMon(list);

			} else if ("web".equalsIgnoreCase(nodeTag)) {
				// Weblogic
				// list = getMiddleware_WeblogicList();
				// list = ShareData.getWeblogiclist();
				// 从内存中中读取 wxy upadate
				this.getWeblogicListByMon(list);

			} else if ("dps".equalsIgnoreCase(nodeTag)) {
				// Weblogic
				// list = getMiddleware_WeblogicList();
				// list = ShareData.getWeblogiclist();
				// 从内存中中读取 wxy upadate
				list = ShareData.getDpconfiglist();
				//this.getDpListByMon(list);

			}else if ("ggs".equalsIgnoreCase(nodeTag)) {
				// Weblogic
				// list = getMiddleware_WeblogicList();
				// list = ShareData.getWeblogiclist();
				// 从内存中中读取 wxy upadate
				list = ShareData.getGgsciconfiglist();
				//this.getDpListByMon(list);

			}else if ("nas".equalsIgnoreCase(nodeTag)) {
				// Weblogic
				// list = getMiddleware_WeblogicList();
				// list = ShareData.getWeblogiclist();
				// 从内存中中读取 wxy upadate
				this.getNasListByMon(list);

			}else if ("ntp".equalsIgnoreCase(nodeTag)) {
				// Weblogic
				// list = getMiddleware_WeblogicList();
				// list = ShareData.getWeblogiclist();
				// 从内存中中读取 wxy upadate
				this.getNtpListByMon(list);

			}else if ("wbl".equalsIgnoreCase(nodeTag)) {
				// weblogin
				// list = ShareData.getWebloginlist();
				this.getWebloginListByMon(list);// 暂且注释 wxy
			} else if ("mai".equalsIgnoreCase(nodeTag)) {
				// Mail
				// list = getService_EmailList();
				// list = ShareData.getEmaillist();
				// 从内存中中读取 wxy upadate
				this.getEmailListByMon(list);
			} else if ("ftp".equalsIgnoreCase(nodeTag)) {
				// FTP
				// list = getService_FtpList();
				// list = ShareData.getFtplist();
				// 从内存中中读取 wxy upadate
				this.getFtpListByMon(list);
			} else if ("tft".equalsIgnoreCase(nodeTag)) {
				// TFTP
				// list = getService_FtpList();
				// list = ShareData.getTftplist();
				// 从内存中中读取 wxy upadate
				this.getTftpListByMon(list);
			} else if ("dhc".equalsIgnoreCase(nodeTag)) {
				// DHCP
				// list = getService_FtpList();
				// list = ShareData.getDhcplist();
				// 从内存中中读取 wxy upadate
				this.getDhcpListByMon(list);
			} else if ("wes".equalsIgnoreCase(nodeTag)) {
				// WEB
				// list = getService_WebList();
				// list = ShareData.getWebconfiglist();
				// 从内存中中读取 wxy upadate
				this.getWebListByMon(list);
			} else if ("iis".equalsIgnoreCase(nodeTag)) {
				// IIS
				// list = getMiddleware_IISList();
				// list = ShareData.getIislist();
				// 从内存中中读取 wxy upadate
				this.getIisListByMon(list);
			} else if ("jbo".equalsIgnoreCase(nodeTag)) {
				// JBoss
				// list = getMiddleware_JBossList();
				// list = ShareData.getJbosslist();
				// 从内存中中读取 wxy upadate
				this.getJbossListByMon(list);
			} else if ("apa".equalsIgnoreCase(nodeTag)) {
				// Apache
				// list = ShareData.getApachlist();
				// 从内存中中读取 wxy upadate
				this.getApacheListByMon(list);

			}
			// else if ("ups".equalsIgnoreCase(nodeTag)) {
				// list = getUpsList();
			//	list = ShareData.getUpslist();
				// 从内存中中读取 wxy upadate
				// this.getUpsListByMon(list);// 暂且不动
			//} 
			else if ("soc".equalsIgnoreCase(nodeTag)) {
				// list = getService_PSTypeVoList();
				// list = ShareData.getPslist();
				this.getPsListByMon(list);
			} else if ("bus".equalsIgnoreCase(nodeTag)) {
				// 业务视图
				// list = getTopo_BusinessList();
				list = ShareData.getBusinesslist();
				// } else if ("pro".equalsIgnoreCase(nodeTag)){
				//			
				// } else if ("int".equalsIgnoreCase(nodeTag)){
				//			
				// } else if ("sto".equalsIgnoreCase(nodeTag)){
				//			
				// } else if ("ggs".equalsIgnoreCase(nodeTag)){
				//			
				// } else if ("sgs".equalsIgnoreCase(nodeTag)){
				//			
			} else if ("saf".equalsIgnoreCase(nodeTag)) {// 安全设备
				// list = getSafeList();
				list = ShareData.getSafelist();
			} else if ("atm".equalsIgnoreCase(nodeTag)) {// ATM设备
				// list = getAtmList();
				list = ShareData.getAtmlist();
			} else if ("tux".equalsIgnoreCase(nodeTag)) {// TUXEDO
				// list = ShareData.getTuxdolist();
				// 从内存中中读取 wxy upadate
				this.getTuxdoListByMon(list);
			} else if ("dns".equalsIgnoreCase(nodeTag)) {// TUXEDO
				// list = ShareData.getTuxdolist();
				// 从内存中中读取 wxy upadate
				this.getDnsListByMon(list);
			}else if("ups".equalsIgnoreCase(nodeTag)){
				this.getUpsListByMon(list);
			}else if( "air".equalsIgnoreCase(nodeTag)){
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;

	}

	/**
	 * 
	 * @description 从内存中读取被监控Domino节点列表
	 * @author wangxiangyong
	 * @date Mar 31, 2013 11:32:59 AM
	 * @return List
	 * @param list
	 * @return
	 */
	public List<BaseVo> getDominoListByMon(List<BaseVo> list) {
		List dominoTempList = ShareData.getDominolist();
		if (dominoTempList != null && list != null) {
			for (int i = 0; i < dominoTempList.size(); i++) {
				DominoConfig domino = (DominoConfig) dominoTempList.get(i);
				if (domino != null) {
					if (domino.getMon_flag() == 0)
						continue;
					list.add(domino);
				}
			}
		}
		return list;
	}

	/**
	 * 
	 * @description 从内存中读取被监控Tomcat节点列表
	 * @author wangxiangyong
	 * @date Mar 31, 2013 11:32:59 AM
	 * @return List
	 * @param list
	 * @return
	 */
	public List<BaseVo> getTomListByMon(List<BaseVo> list) {
		List tomcatTempList = ShareData.getTomcatlist();
		if (tomcatTempList != null && list != null) {
			for (int i = 0; i < tomcatTempList.size(); i++) {
				Tomcat tomcat = (Tomcat) tomcatTempList.get(i);
				if (tomcat != null) {
					if (tomcat.getMonflag() == 0)
						continue;
					list.add(tomcat);
				}
			}
		}
		return list;
	}

	/**
	 * 
	 * @description 从内存中读取被监控Tomcat节点列表
	 * @author wangxiangyong
	 * @date Mar 31, 2013 11:32:59 AM
	 * @return List
	 * @param list
	 * @return
	 */
	public List<BaseVo> getCicsListByMon(List<BaseVo> list) {
		List ciscTempList = ShareData.getCicslist();
		if (ciscTempList != null && list != null) {
			for (int i = 0; i < ciscTempList.size(); i++) {
				CicsConfig cisc = (CicsConfig) ciscTempList.get(i);
				if (cisc != null) {
					if (cisc.getFlag() == 0)
						continue;
					list.add(cisc);
				}
			}
		}
		return list;
	}

	/**
	 * 
	 * @description 从内存中读取被监控MQ节点列表
	 * @author wangxiangyong
	 * @date Mar 31, 2013 11:32:59 AM
	 * @return List
	 * @param list
	 * @return
	 */
	public List<BaseVo> getMqListByMon(List<BaseVo> list) {
		List mqTempList = ShareData.getMqlist();
		if (mqTempList != null && list != null) {
			for (int i = 0; i < mqTempList.size(); i++) {
				MQConfig mqConfig = (MQConfig) mqTempList.get(i);
				if (mqConfig != null) {
					if (mqConfig.getMon_flag() == 0)
						continue;
					list.add(mqConfig);
				}
			}
		}
		return list;
	}

	/**
	 * 
	 * @description 从内存中读取被监控was节点列表
	 * @author wangxiangyong
	 * @date Mar 31, 2013 11:32:59 AM
	 * @return List
	 * @param list
	 * @return
	 */
	public List<BaseVo> getWasListByMon(List<BaseVo> list) {
		List wasTempList = ShareData.getWaslist();
		if (wasTempList != null && list != null) {
			for (int i = 0; i < wasTempList.size(); i++) {
				WasConfig wasConfig = (WasConfig) wasTempList.get(i);
				if (wasConfig != null) {
					if (wasConfig.getMon_flag() == 0)
						continue;
					list.add(wasConfig);
				}
			}
		}
		return list;
	}

	/**
	 * 
	 * @description 从内存中读取被监控weblogic节点列表
	 * @author wangxiangyong
	 * @date Mar 31, 2013 11:32:59 AM
	 * @return List
	 * @param list
	 * @return
	 */
	public List<BaseVo> getWeblogicListByMon(List<BaseVo> list) {
		List weblogicTempList = ShareData.getWeblogiclist();
		if (weblogicTempList != null && list != null) {
			for (int i = 0; i < weblogicTempList.size(); i++) {
				WeblogicConfig weblogicConfig = (WeblogicConfig) weblogicTempList.get(i);
				if (weblogicConfig != null) {
					if (weblogicConfig.getMon_flag() == 0)
						continue;
					list.add(weblogicConfig);
				}
			}
		}
		return list;
	}

	/**
	 * 
	 * @description 从内存中读取被监控Weblogin节点列表
	 * @author wangxiangyong
	 * @date Mar 31, 2013 11:32:59 AM
	 * @return List
	 * @param list
	 * @return
	 */
	public List<BaseVo> getWebloginListByMon(List<BaseVo> list) {
		List webloginTempList = ShareData.getWebloginlist();
		if (webloginTempList != null && list != null) {
			for (int i = 0; i < webloginTempList.size(); i++) {
				webloginConfig weblogin = (webloginConfig) webloginTempList.get(i);
				if (weblogin != null) {
					if (weblogin.getFlag().equals("0"))
						continue;
					list.add(weblogin);
				}
			}
		} else {
			list = new ArrayList<BaseVo>();
		}
		return list;
	}

	/**
	 * 
	 * @description 从内存中读取被监控Email节点列表
	 * @author wangxiangyong
	 * @date Mar 31, 2013 11:32:59 AM
	 * @return List
	 * @param list
	 * @return
	 */
	public List<BaseVo> getEmailListByMon(List<BaseVo> list) {
		List emailTempList = ShareData.getEmaillist();
		if (emailTempList != null && list != null) {
			for (int i = 0; i < emailTempList.size(); i++) {
				EmailMonitorConfig email = (EmailMonitorConfig) emailTempList.get(i);
				if (email != null) {
					// System.out.println("------:::::"+email.getFlag());
					if (email.getMonflag() == 0)
						continue;
					list.add(email);
				}
			}
		}
		return list;
	}

	/**
	 * 
	 * @description 从内存中读取被监控FTP节点列表
	 * @author wangxiangyong
	 * @date Mar 31, 2013 11:32:59 AM
	 * @return List
	 * @param list
	 * @return
	 */
	public List<BaseVo> getFtpListByMon(List<BaseVo> list) {
		List ftpTempList = ShareData.getFtplist();
		if (ftpTempList != null && list != null) {
			for (int i = 0; i < ftpTempList.size(); i++) {
				FTPConfig ftp = (FTPConfig) ftpTempList.get(i);
				if (ftp != null) {
					if (ftp.getMonflag() == 0)
						continue;
					list.add(ftp);
				}
			}
		}
		return list;
	}

	/**
	 * 
	 * @description 从内存中读取被监控TFTP节点列表
	 * @author wangxiangyong
	 * @date Mar 31, 2013 11:32:59 AM
	 * @return List
	 * @param list
	 * @return
	 */
	public List<BaseVo> getTftpListByMon(List<BaseVo> list) {
		List tftpTempList = ShareData.getTftplist();
		if (tftpTempList != null && list != null) {
			for (int i = 0; i < tftpTempList.size(); i++) {
				TFTPConfig tftp = (TFTPConfig) tftpTempList.get(i);
				if (tftp != null) {
					if (tftp.getMonflag() == 0)
						continue;
					list.add(tftp);
				}
			}
		}
		return list;
	}

	/**
	 * 
	 * @description 从内存中读取被监控Dhcp节点列表
	 * @author wangxiangyong
	 * @date Mar 31, 2013 11:32:59 AM
	 * @return List
	 * @param list
	 * @return
	 */
	public List<BaseVo> getDhcpListByMon(List<BaseVo> list) {
		List dhcpTempList = ShareData.getDhcplist();
		if (dhcpTempList != null && list != null) {
			for (int i = 0; i < dhcpTempList.size(); i++) {
				DHCPConfig dhcp = (DHCPConfig) dhcpTempList.get(i);
				if (dhcp != null) {
					if (dhcp.getMon_flag() == 0)
						continue;
					list.add(dhcp);
				}
			}
		}
		return list;
	}

	/**
	 * 
	 * @description 从内存中读取被监控web节点列表
	 * @author wangxiangyong
	 * @date Mar 31, 2013 11:32:59 AM
	 * @return List
	 * @param list
	 * @return
	 */
	public List<BaseVo> getWebListByMon(List<BaseVo> list) {
		List webTempList = ShareData.getWebconfiglist();
		if (webTempList != null && list != null) {
			for (int i = 0; i < webTempList.size(); i++) {
				WebConfig web = (WebConfig) webTempList.get(i);
				if (web != null) {
					if (web.getFlag() == 0)
						continue;
					list.add(web);
				}
			}
		}
		return list;
	}
	
	/**
	 * 
	 * @description 从内存中读取被监控dp节点列表
	 * @author wangxiangyong
	 * @date Mar 31, 2013 11:32:59 AM
	 * @return List
	 * @param list
	 * @return
	 */
	public List<BaseVo> getDpListByMon(List<BaseVo> list) {
		List dpTempList = ShareData.getDpconfiglist();
		if (dpTempList != null && list != null) {
			for (int i = 0; i < dpTempList.size(); i++) {
				DpConfig dp = (DpConfig) dpTempList.get(i);
				if (dp != null) {
					if (dp.getFlag() == 0)
						continue;
					list.add(dp);
				}
			}
		}
		return list;
	}
	
	/**
	 * 
	 * @description 从内存中读取被监控ggsci节点列表
	 * @author  
	 * @date Mar 31, 2013 11:32:59 AM
	 * @return List
	 * @param list
	 * @return
	 */
	public List<BaseVo> getGgsciListByMon(List<BaseVo> list) {
		List ggsciTempList = ShareData.getGgsciconfiglist();
		if (ggsciTempList != null && list != null) {
			for (int i = 0; i < ggsciTempList.size(); i++) {
				GgsciConfig ggsci = (GgsciConfig) ggsciTempList.get(i);
				if (ggsci != null) {
					if (ggsci.getFlag() == 0)
						continue;
					list.add(ggsci);
				}
			}
		}
		return list;
	}
	
	/**
	 * 
	 * @description 从内存中读取被监控nas节点列表
	 * @author  
	 * @date Mar 31, 2013 11:32:59 AM
	 * @return List
	 * @param list
	 * @return
	 */
	public List<BaseVo> getNasListByMon(List<BaseVo> list) {
		List nasTempList = ShareData.getNasconfiglist();
		if (nasTempList != null && list != null) {
			for (int i = 0; i < nasTempList.size(); i++) {
				NasConfig nas = (NasConfig) nasTempList.get(i);
				if (nas != null) {
					if (nas.getFlag() == 0)
						continue;
					list.add(nas);
				}
			}
		}
		return list;
	}
	
	/**
	 * 
	 * @description 从内存中读取被监控ntp节点列表
	 * @author  
	 * @date Mar 31, 2013 11:32:59 AM
	 * @return List
	 * @param list
	 * @return
	 */
	public List<BaseVo> getNtpListByMon(List<BaseVo> list) {
		List ntpTempList = ShareData.getNtpconfiglist();
		if (ntpTempList != null && list != null) {
			for (int i = 0; i < ntpTempList.size(); i++) {
				NtpConfig ntp = (NtpConfig) ntpTempList.get(i);
				if (ntp != null) {
					if (ntp.getFlag() == 0)
						continue;
					list.add(ntp);
				}
			}
		}
		return list;
	}

	/**
	 * 
	 * @description 从内存中读取被监控IIS节点列表
	 * @author wangxiangyong
	 * @date Mar 31, 2013 11:32:59 AM
	 * @return List
	 * @param list
	 * @return
	 */
	public List<BaseVo> getIisListByMon(List<BaseVo> list) {
		List iisTempList = ShareData.getIislist();
		if (iisTempList != null && list != null) {
			for (int i = 0; i < iisTempList.size(); i++) {
				IISConfig iis = (IISConfig) iisTempList.get(i);
				if (iis != null) {
					if (iis.getMon_flag() == 0)
						continue;
					list.add(iis);
				}
			}
		}
		return list;
	}

	/**
	 * 
	 * @description 从内存中读取被监控Jboss节点列表
	 * @author wangxiangyong
	 * @date Mar 31, 2013 11:32:59 AM
	 * @return List
	 * @param list
	 * @return
	 */
	public List<BaseVo> getJbossListByMon(List<BaseVo> list) {
		List jbossTempList = ShareData.getJbosslist();
		if (jbossTempList != null && list != null) {
			for (int i = 0; i < jbossTempList.size(); i++) {
				JBossConfig jboss = (JBossConfig) jbossTempList.get(i);
				if (jboss != null) {
					if (jboss.getFlag() == 0)
						continue;
					list.add(jboss);
				}
			}
		}
		return list;
	}

	/**
	 * 
	 * @description 从内存中读取被监控ups节点列表
	 * @author wangxiangyong
	 * @date Mar 31, 2013 11:32:59 AM
	 * @return List
	 * @param list
	 * @return
	 */
	public List<BaseVo> getUpsListByMon(List<BaseVo> list) {
		List upsTempList = ShareData.getUpslist();
		if (upsTempList != null && list != null) {
			for (int i = 0; i < upsTempList.size(); i++) {
				MgeUps ups = (MgeUps) upsTempList.get(i);
				if (ups != null) {
					if (ups.getIsmanaged().equals("0"))
						continue;
					list.add(ups);
				}
			}
		}
		return list;
	}

	/**
	 * 
	 * @description 从内存中读取被监控Apache节点列表
	 * @author wangxiangyong
	 * @date Mar 31, 2013 11:32:59 AM
	 * @return List
	 * @param list
	 * @return
	 */
	public List<BaseVo> getApacheListByMon(List<BaseVo> list) {
		List apacheTempList = ShareData.getApachlist();
		if (apacheTempList != null && list != null) {
			for (int i = 0; i < apacheTempList.size(); i++) {
				ApacheConfig apacheConfig = (ApacheConfig) apacheTempList.get(i);
				if (apacheConfig != null) {

					if (apacheConfig.getFlag() == 0)
						continue;
					list.add(apacheConfig);
				}
			}
		}

		return list;
	}

	/**
	 * 
	 * @description 从内存中读取被监控Tuxdo节点列表
	 * @author wangxiangyong
	 * @date Mar 31, 2013 11:32:59 AM
	 * @return List
	 * @param list
	 * @return
	 */
	public List<BaseVo> getTuxdoListByMon(List<BaseVo> list) {
		List tuxdoTempList = ShareData.getTuxdolist();
		if (tuxdoTempList != null && list != null) {
			for (int i = 0; i < tuxdoTempList.size(); i++) {
				TuxedoConfig tuxdoConfig = (TuxedoConfig) tuxdoTempList.get(i);
				if (tuxdoConfig != null) {

					if (tuxdoConfig.getMon_flag().equals("0"))
						continue;
					list.add(tuxdoConfig);
				}
			}
		}

		return list;
	}

	/**
	 * 
	 * @description 从内存中读取被监控Tuxdo节点列表
	 * @author wangxiangyong
	 * @date Mar 31, 2013 11:32:59 AM
	 * @return List
	 * @param list
	 * @return
	 */
	public List<BaseVo> getResinListByMon(List<BaseVo> list) {
		List resinTempList = ShareData.getResinlist();
		if (resinTempList != null && list != null) {
			for (int i = 0; i < resinTempList.size(); i++) {
				Resin resin = (Resin) resinTempList.get(i);
				if (resin != null) {

					if (resin.getMonflag() == 0)
						continue;
					list.add(resin);
				}
			}
		}

		return list;
	}

	/**
	 * 
	 * @description 从内存中读取被监控Tuxdo节点列表
	 * @author wangxiangyong
	 * @date Mar 31, 2013 11:32:59 AM
	 * @return List
	 * @param list
	 * @return
	 */
	public List<BaseVo> getDnsListByMon(List<BaseVo> list) {
		List dnsTempList = ShareData.getDnslist();
		if (dnsTempList != null && list != null) {
			for (int i = 0; i < dnsTempList.size(); i++) {
				DnsConfig dnsConfig = (DnsConfig) dnsTempList.get(i);
				if (dnsConfig != null) {

					if (dnsConfig.getFlag() == 0)
						continue;
					list.add(dnsConfig);
				}
			}
		}

		return list;
	}
	
	public List<BaseVo> getByNodeTagAndBid(String nodeTag, String category, String bid){
		List<BaseVo> list = new ArrayList();
		List templist = null;
		//SysLogger.info("category==="+category+"====nodeTag:==="+nodeTag);
		try{
			if("net".equalsIgnoreCase(nodeTag)){
				// 网络设备主机
//			if(ShareData.getNodehash() != null ){
//				String[] categorys = category.split(",");
//				if(categorys!=null && categorys.length > 0 ){
//					//SysLogger.info("categorys length=========="+categorys.length);
//					for(int k=0;k<categorys.length;k++){
//						String category_per = categorys[k];
//						
//						if(ShareData.getNodehash().containsKey(category_per)){
//							templist = (List)ShareData.getNodehash().get(category_per);
//							if(templist != null && templist.size()>0){
//								for(int j=0;j<templist.size();j++){
//									HostNode hostnode = (HostNode)templist.get(j);
//									//SysLogger.info("add-----"+category_per+"----"+hostnode.getIpAddress()+"---"+hostnode.getAlias());
//									list.add((HostNode)templist.get(j));
//								}
//							}
//						}
//					}
//				}
//				
//			}
//			
//			
				String condition = " where 1=1 " + getMonFlagSql("managed");
				if(category == null) {
					category = "";
				}
				category = "," + category + ",";
				String[] categorys = category.split(",");
				if(categorys!=null && categorys.length > 0 ){
					int i = 0 ; 
					for (String category_per : categorys) {
						if(category_per != null && category_per.trim().length() > 0){
							if(i == 0) { 
								condition = condition + " and (";
							} else {
								condition = condition + " or";
							}
							condition = condition + " category ='" + category_per + "'";
							i++;
							if(i == categorys.length -1){
								condition = condition + ") ";
							}
						}
					}
				}
				if(!BaseUtil.isEmpty(bid) && !"null".equals(bid)){
					condition = condition + " and bid='" + bid + "'";
				}
				//SysLogger.info(condition+"===========");
				list = getHostNode(condition);
				
			} else if ("mid".equalsIgnoreCase(nodeTag)){
				// 中间件
				//list = getMiddlewareList();
			}  else if ("ser".equalsIgnoreCase(nodeTag)){
				// 服务
				list = getServiceList();
			} else if ("dom".equalsIgnoreCase(nodeTag)){
				// domino
				//list = getMiddleware_DominoList();
				list = ShareData.getDominolist();
			} else if ("dbs".equalsIgnoreCase(nodeTag)){
				// 数据库
				//list = getDBList();
				list = ShareData.getDbconfiglist();
			} else if ("tom".equalsIgnoreCase(nodeTag)){
				// Tomcat
				//list = getMiddleware_TomcatList();
				list = ShareData.getTomcatlist();
			} else if ("cic".equalsIgnoreCase(nodeTag)){
				// Cics
				//list = getMiddleware_CICSList();
				list = ShareData.getCicslist();
			} else if ("weblogic".equalsIgnoreCase(nodeTag)){
				// MQ
				//list = getMiddleware_MQList();
				list = ShareData.getWeblogiclist();
			}else if ("mq".equalsIgnoreCase(nodeTag)){
				// MQ
				//list = getMiddleware_MQList();
				list = ShareData.getMqlist();
			} else if ("was".equalsIgnoreCase(nodeTag)){
				// WAS
				//list = getMiddleware_WasList();
				list = ShareData.getWaslist();
			} else if ("web".equalsIgnoreCase(nodeTag)){
				// Weblogic
				//list = getMiddleware_WeblogicList();
				list = ShareData.getWeblogiclist();
			} else if ("mai".equalsIgnoreCase(nodeTag)){
				// Mail
				//list = getService_EmailList();
				list = ShareData.getEmaillist();
			} else if ("ftp".equalsIgnoreCase(nodeTag)){
				// FTP
				//list = getService_FtpList();
				list = ShareData.getFtplist();
			} else if ("tft".equalsIgnoreCase(nodeTag)){
				// TFTP
				//list = getService_FtpList();
				list = ShareData.getTftplist();
			} else if ("dhc".equalsIgnoreCase(nodeTag)){
				// DHCP
				//list = getService_FtpList();
				list = ShareData.getDhcplist();
			} else if ("wes".equalsIgnoreCase(nodeTag)){
				// WEB
				//list = getService_WebList();
				list = ShareData.getWebconfiglist();
			}else if ("ggs".equalsIgnoreCase(nodeTag)) {
				// Weblogic
				// list = getMiddleware_WeblogicList();
				// list = ShareData.getWeblogiclist();
				// 从内存中中读取 wxy upadate
				list = ShareData.getGgsciconfiglist();
				//this.getDpListByMon(list);

			}else if ("nas".equalsIgnoreCase(nodeTag)) {
				// Weblogic
				// list = getMiddleware_WeblogicList();
				// list = ShareData.getWeblogiclist();
				// 从内存中中读取 wxy upadate
				this.getNasListByMon(list);

			}else if ("ntp".equalsIgnoreCase(nodeTag)) {
				// Weblogic
				// list = getMiddleware_WeblogicList();
				// list = ShareData.getWeblogiclist();
				// 从内存中中读取 wxy upadate
				this.getNtpListByMon(list);

			} else if ("iis".equalsIgnoreCase(nodeTag)){
				// IIS
				//list = getMiddleware_IISList();
				list = ShareData.getIislist();
			} else if ("jbo".equalsIgnoreCase(nodeTag)){
				// JBoss
				//list = getMiddleware_JBossList();
				list = ShareData.getJbosslist();
			} else if ("apa".equalsIgnoreCase(nodeTag)){
				// Apache
				//list = getMiddleware_ApacheList();
				list = ShareData.getApachlist();
			} else if ("ups".equalsIgnoreCase(nodeTag)){
				//list = getUpsList();
				list = ShareData.getUpslist();
			} else if ("soc".equalsIgnoreCase(nodeTag)){
				//list = getService_PSTypeVoList();
				list = ShareData.getPslist();
			} else if ("bus".equalsIgnoreCase(nodeTag)){
				// 业务视图
				//list = getTopo_BusinessList();
				list = ShareData.getBusinesslist();
			} else if ("pro".equalsIgnoreCase(nodeTag)){
				list = getService_ProcessVoList();
			} else if ("int".equalsIgnoreCase(nodeTag)){
				
			} else if ("sto".equalsIgnoreCase(nodeTag)){
				list = getStorageList();
			} else if ("mqs".equalsIgnoreCase(nodeTag)){
				list = ShareData.getMqlist();
			} else if ("tux".equalsIgnoreCase(nodeTag)){
				list = ShareData.getTuxdolist();
			} else if ("saf".equalsIgnoreCase(nodeTag)){//安全设备
				//list = getSafeList();
				list = ShareData.getSafelist();
			} else if ("atm".equalsIgnoreCase(nodeTag)){//ATM设备
				//list = getAtmList();
				list = ShareData.getAtmlist();
			}else if ("tux".equalsIgnoreCase(nodeTag)){//ATM设备
				//list = getAtmList();
				list = ShareData.getTuxdolist();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
		
	}
	
	/**
	 * 获取 PSTypeVo 列表
	 * @return
	 */
	public List<BaseVo> getService_ProcessVoList(){
		// PSTypeVo
		
		List<BaseVo> list = new ArrayList<BaseVo>();
		
		List nodeList = PollingEngine.getInstance().getProList();
		for(int i=0;i<nodeList.size();i++){
			Proces pro = (Proces) nodeList.get(i);
			Procs procs = new Procs();
			
			procs.setBid(pro.getBid());
			procs.setChname(pro.getAlias());
			procs.setIpaddress(pro.getIpAddress());
			procs.setNodeid(pro.getId());
			procs.setProcname(pro.getName());
			procs.setId(pro.getId());
			list.add(procs);
		}
		if(list == null){
			list = new ArrayList<BaseVo>();
		}
		
		return list;
	}

	/**
	 * 
	 * @description 从内存中读取被监控Ps节点列表
	 * @author wangxiangyong
	 * @date Mar 31, 2013 11:32:59 AM
	 * @return List
	 * @param list
	 * @return
	 */
	public List<BaseVo> getPsListByMon(List<BaseVo> list) {
		List psTempList = ShareData.getPslist();
		if (psTempList != null && list != null) {
			for (int i = 0; i < psTempList.size(); i++) {
				PSTypeVo psConfig = (PSTypeVo) psTempList.get(i);
				if (psConfig != null) {

					if (psConfig.getMonflag() == 0)
						continue;
					list.add(psConfig);
				}
			}
		}

		return list;
	}

	
	
	public List getSafeList() {
		List retList = null;
		String category = "8";
		String condition = " where 1=1 " + getMonFlagSql("managed");
		if (category == null) {
			category = "";
		}
		category = "," + category + ",";
		String[] categorys = category.split(",");
		if (categorys != null && categorys.length > 0) {
			int i = 0;
			for (String category_per : categorys) {
				if (category_per != null && category_per.trim().length() > 0) {
					if (i == 0) {
						condition = condition + " and (";
					} else {
						condition = condition + " or";
					}
					condition = condition + " category ='" + category_per + "'";
					i++;
					if (i == categorys.length - 1) {
						condition = condition + ") ";
					}
				}
			}
		}
		// System.out.println(condition);
		retList = getHostNode(condition);
		return retList;
	}
	
	/**
	 * 
	 * @description 从内存中读取被监控nas节点列表
	 * @author  
	 * @date Mar 31, 2013 11:32:59 AM
	 * @return List
	 * @param list
	 * @return
	 */
	
	
	public List<BaseVo> getAtmList() {
		List retList = null;
		String category = "9";
		String condition = " where 1=1 " + getMonFlagSql("managed");
		if (category == null) {
			category = "";
		}
		category = "," + category + ",";
		String[] categorys = category.split(",");
		if (categorys != null && categorys.length > 0) {
			int i = 0;
			for (String category_per : categorys) {
				if (category_per != null && category_per.trim().length() > 0) {
					if (i == 0) {
						condition = condition + " and (";
					} else {
						condition = condition + " or";
					}
					condition = condition + " category ='" + category_per + "'";
					i++;
					if (i == categorys.length - 1) {
						condition = condition + ") ";
					}
				}
			}
		}
		// System.out.println(condition);
		retList = getHostNode(condition);
		return retList;
	}

	/**
	 * @return the monitorFlag
	 */
	public String getMonitorFlag() {
		return monitorFlag;
	}

	/**
	 * @param monitorFlag
	 *            the monitorFlag to set
	 */
	public void setMonitorFlag(String monitorFlag) {
		this.monitorFlag = monitorFlag;
	}

	/**
	 * @return the isSetedMonitorFlag
	 */
	public boolean isSetedMonitorFlag() {
		return isSetedMonitorFlag;
	}

	/**
	 * @param isSetedMonitorFlag
	 *            the isSetedMonitorFlag to set
	 */
	public void setSetedMonitorFlag(boolean isSetedMonitorFlag) {
		this.isSetedMonitorFlag = isSetedMonitorFlag;
	}

	/**
	 * 判断该节点采用的是否为只采集ping数据的采集方式
	 * 
	 * @param host
	 * @return true:ping/telnet检测可用/ssh检测可用方式采集 false:其他方式采集
	 * 
	 */
	public static synchronized boolean isOnlyCollectPing(Host host) {
		if (host == null) {
			return false;
		}
		boolean flag = false;
		if (host.getCollecttype() == SystemConstant.COLLECTTYPE_PING || host.getCollecttype() == SystemConstant.COLLECTTYPE_TELNETCONNECT || host.getCollecttype() == SystemConstant.COLLECTTYPE_SSHCONNECT) {
			flag = true;
		}
		return flag;
	}
}
