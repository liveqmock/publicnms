package com.afunms.polling.snmp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import com.afunms.application.model.IISConfig;
import com.afunms.application.model.IISVo;
import com.afunms.common.util.SnmpUtils;
import com.afunms.monitor.executor.base.MonitorInterface;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.topology.model.HostNode;
import com.gatherResulttosql.IISConnResultTosql;

public class IISSnmp extends SnmpMonitor implements MonitorInterface {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public List collect_Data(IISConfig iisconf) {
		List list = new ArrayList();
		try {
			Calendar date = Calendar.getInstance();
			try {
				com.afunms.polling.base.Node snmpnode = (com.afunms.polling.base.Node) PollingEngine.getInstance().getDominoByIP(iisconf.getIpaddress());
				Date cc = date.getTime();
				String time = sdf.format(cc);
				snmpnode.setLastTime(time);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				String[] oids = new String[] { "1.3.6.1.4.1.311.1.7.3.1.1", // totalBytesSentHighWord
						"1.3.6.1.4.1.311.1.7.3.1.2", // totalBytesSentLowWord
						"1.3.6.1.4.1.311.1.7.3.1.3", // totalBytesReceivedHighWord
						"1.3.6.1.4.1.311.1.7.3.1.4", // totalBytesReceivedLowWord
						"1.3.6.1.4.1.311.1.7.3.1.5", // totalFilesSent
						"1.3.6.1.4.1.311.1.7.3.1.6", // totalFilesReceived
						"1.3.6.1.4.1.311.1.7.3.1.7", // currentAnonymousUsers
						"1.3.6.1.4.1.311.1.7.3.1.9", // totalAnonymousUsers
						"1.3.6.1.4.1.311.1.7.3.1.11", // maxAnonymousUsers
						"1.3.6.1.4.1.311.1.7.3.1.13", // currentConnections
						"1.3.6.1.4.1.311.1.7.3.1.14", // maxConnections
						"1.3.6.1.4.1.311.1.7.3.1.15", // connectionAttempts
						"1.3.6.1.4.1.311.1.7.3.1.16", // logonAttempts
						"1.3.6.1.4.1.311.1.7.3.1.18", // totalGets
						"1.3.6.1.4.1.311.1.7.3.1.19", // totalPosts
						"1.3.6.1.4.1.311.1.7.3.1.43" }; // totalNotFoundErrors
				String[][] valueArray = null;
				try {
					valueArray = SnmpUtils.getTableData(iisconf.getIpaddress(),iisconf.getCommunity(),oids,0,2,10000);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (valueArray != null) {
					for (int i = 0; i < valueArray.length; i++) {
						IISVo iisvo = new IISVo();
						iisvo.setTotalBytesSentHighWord(valueArray[i][0]);
						iisvo.setTotalBytesSentLowWord(valueArray[i][1]);
						iisvo.setTotalBytesReceivedHighWord(valueArray[i][2]);
						iisvo.setTotalBytesReceivedLowWord(valueArray[i][3]);
						iisvo.setTotalFilesSent(valueArray[i][4]);
						iisvo.setTotalFilesReceived(valueArray[i][5]);
						iisvo.setCurrentAnonymousUsers(valueArray[i][6]);
						iisvo.setTotalAnonymousUsers(valueArray[i][7]);
						iisvo.setMaxAnonymousUsers(valueArray[i][8]);
						iisvo.setCurrentConnections(valueArray[i][9]);
						iisvo.setMaxConnections(valueArray[i][10]);
						iisvo.setConnectionAttempts(valueArray[i][11]);
						iisvo.setLogonAttempts(valueArray[i][12]);
						iisvo.setTotalGets(valueArray[i][13]);
						iisvo.setTotalPosts(valueArray[i][14]);
						iisvo.setTotalNotFoundErrors(valueArray[i][15]);
						list.add(iisvo);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}  

		IISConnResultTosql tosql = new IISConnResultTosql();
		tosql.CreateResultTosql(list, iisconf.getIpaddress());
		return list;
	}

	public void collectData(Node node, MonitoredItem item) {
		// TODO Auto-generated method stub

	}

	public void collectData(HostNode node) {
		// TODO Auto-generated method stub

	}

	public Hashtable collect_Data(HostNode node) {
		// TODO Auto-generated method stub
		return null;
	}
}
