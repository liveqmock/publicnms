package com.afunms.polling.snmp.storage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.afunms.application.dao.NetAppDataOperator;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.NetAppConsistencyPoint;

//NetApp consistency point ≤…ºØ¿‡
public class NetAppConsistencyPointSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash = new Hashtable();
		Vector tempVector = new Vector();
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if (node == null)
			return returnHash;

		try {
			NetAppConsistencyPoint netAppConsistencyPoint = null;
			Calendar date = Calendar.getInstance();

			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				com.afunms.polling.base.Node snmpnode = (com.afunms.polling.base.Node) PollingEngine.getInstance().getNodeByIP(node.getIpAddress());
				Date cc = date.getTime();
				String time = sdf.format(cc);
				snmpnode.setLastTime(time);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				String[] oids = new String[] { ".1.3.6.1.4.1.789.1.2.6.1.0",  //
						".1.3.6.1.4.1.789.1.2.6.2.0",  //
						".1.3.6.1.4.1.789.1.2.6.3.0",  //
						".1.3.6.1.4.1.789.1.2.6.4.0",  //
						".1.3.6.1.4.1.789.1.2.6.5.0",  //
						".1.3.6.1.4.1.789.1.2.6.6.0",  //
						".1.3.6.1.4.1.789.1.2.6.7.0",  //
						".1.3.6.1.4.1.789.1.2.6.8.0",  //
						".1.3.6.1.4.1.789.1.2.6.9.0",  //
						".1.3.6.1.4.1.789.1.2.6.10.0",  //
						".1.3.6.1.4.1.789.1.2.6.11.0",  //
						".1.3.6.1.4.1.789.1.2.6.12.0",  //
						".1.3.6.1.4.1.789.1.2.6.13.0",  //
				};

				String[][] valueArray = null;
				try {
					valueArray = SnmpUtils.getList(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), 3, 1000 * 30);
				} catch (Exception e) {
					valueArray = null;
				}
				if (valueArray != null) {
					for (int i = 0; i < valueArray.length; i++) {
						netAppConsistencyPoint = new NetAppConsistencyPoint();
						netAppConsistencyPoint.setIpaddress(node.getIpAddress());
						netAppConsistencyPoint.setCollectTime(date);
						netAppConsistencyPoint.setCpTime(valueArray[i][0]);
						netAppConsistencyPoint.setCpFromTimerOps(valueArray[i][1]);
						netAppConsistencyPoint.setCpFromSnapshotOps(valueArray[i][2]);
						netAppConsistencyPoint.setCpFromLowWaterOps(valueArray[i][3]);
						netAppConsistencyPoint.setCpFromHighWaterOps(valueArray[i][4]);
						netAppConsistencyPoint.setCpFromLogFullOps(valueArray[i][5]);
						netAppConsistencyPoint.setCpFromCpOps(valueArray[i][6]);
						netAppConsistencyPoint.setCpTotalOps(valueArray[i][7]);
						netAppConsistencyPoint.setCpFromFlushOps(valueArray[i][8]);
						netAppConsistencyPoint.setCpFromSyncOps(valueArray[i][9]);
						netAppConsistencyPoint.setCpFromLowVbufOps(valueArray[i][10]);
						netAppConsistencyPoint.setCpFromCpDeferredOps(valueArray[i][11]);
						netAppConsistencyPoint.setCpFromLowDatavecsOps(valueArray[i][12]);
						tempVector.addElement(netAppConsistencyPoint);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!(ShareData.getSharedata().containsKey(node.getIpAddress()))) {
			Hashtable ipAllData = new Hashtable();
			if (ipAllData == null)
				ipAllData = new Hashtable();
			if (tempVector != null && tempVector.size() > 0)
				ipAllData.put("consistencyPoint", tempVector);
			ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		} else {
			if (tempVector != null && tempVector.size() > 0)
				((Hashtable) ShareData.getSharedata().get(node.getIpAddress())).put("consistencyPoint", tempVector);
		}

		returnHash.put("consistencyPoint", tempVector);

		tempVector = null;

		NetAppDataOperator op=new NetAppDataOperator();
		op.CreateResultTosql(returnHash, node.getIpAddress());

		return returnHash;
	}
}
