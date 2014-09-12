package com.afunms.polling.snmp.dhcp;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.application.dao.DHCPConfigDao;
import com.afunms.application.model.DHCPConfig;
import com.afunms.common.util.PingUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.common.util.SysLogger;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.DHCP;

public class WindowsDhcpScopeSnmp extends SnmpMonitor {

	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash = new Hashtable();
		List dhcpList = new ArrayList();
		Vector dhcppingvector = new Vector();

		DHCP dhcp = (DHCP) PollingEngine.getInstance().getDHCPByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if (dhcp == null)
			return returnHash;
		try {
			Calendar date = Calendar.getInstance();
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date cc = date.getTime();
				String time = sdf.format(cc);
				dhcp.setLastTime(time);
			} catch (Exception e) {
				e.printStackTrace();
			}
			DHCPConfig dhcpconf = new DHCPConfig();
			DHCPConfigDao dao = new DHCPConfigDao();
			try {
				dhcpconf = (DHCPConfig) dao.findByID(alarmIndicatorsNode.getNodeid());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}

			PingUtil pingU = new PingUtil(dhcp.getIpAddress());
			try {
				Integer[] packet = pingU.ping();
				dhcppingvector = pingU.addhis(packet);
				if (dhcppingvector != null) {
					DHCPConfigDao dhcpconfigdao = new DHCPConfigDao();
					try {
						dhcpconfigdao.createHostData(dhcppingvector, dhcpconf);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						dhcpconfigdao.close();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				String[] oids = new String[] { ".1.3.6.1.4.1.311.1.3.2.1.1.1", // ×ÓÍøµØÖ·
						".1.3.6.1.4.1.311.1.3.2.1.1.2", // in use
						".1.3.6.1.4.1.311.1.3.2.1.1.3", // in free
						".1.3.6.1.4.1.311.1.3.2.1.1.4" // PendingOffers
				};
				String[][] valueArray = null;
				try {
					valueArray = SnmpUtils.getTableData(dhcp.getIpAddress(), dhcp.getCommunity(), oids, 0, 3, 1000 * 30);
				} catch (Exception e) {
					valueArray = null;
					SysLogger.error(dhcp.getIpAddress() + "_WindowsSnmp");
				}
				Hashtable scope = null;
				for (int i = 0; i < valueArray.length; i++) {
					scope = new Hashtable();
					String vbstring0 = valueArray[i][0];
					String vbstring1 = valueArray[i][1];
					String vbstring2 = valueArray[i][2];
					String vbstring3 = valueArray[i][3];
					scope.put("netadd", vbstring0);
					scope.put("inuse", vbstring1);
					scope.put("free", vbstring2);
					scope.put("pendingoffers", vbstring3);
					dhcpList.add(scope);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!(ShareData.getDhcpdata().containsKey(dhcp.getIpAddress()))) {
			Hashtable ipAllData = new Hashtable();
			if (ipAllData == null)
				ipAllData = new Hashtable();
			if (dhcpList != null && dhcpList.size() > 0)
				ipAllData.put("dhcpscopeValue", dhcpList);
			ipAllData.put("dhcpping", dhcppingvector);
			ShareData.getDhcpdata().put(dhcp.getIpAddress(), ipAllData);
		} else {
			if (dhcpList != null && dhcpList.size() > 0)
				((Hashtable) ShareData.getDhcpdata().get(dhcp.getIpAddress())).put("dhcpscopeValue", dhcpList);
		}

		returnHash.put("dhcpscopeValue", dhcpList);
		returnHash.put("dhcpping", dhcppingvector);

		return returnHash;
	}
}
