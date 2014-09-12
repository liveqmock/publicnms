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
import com.afunms.polling.om.NetAppTree;

//NetApp Tree采集类
public class NetAppTreeSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash = new Hashtable();
		Vector tempVector = new Vector();
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if (node == null)
			return returnHash;

		try {
			NetAppTree netApptree = null;
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
				String[] oids = new String[] { ".1.3.6.1.4.1.789.1.5.10.1.1",  //tree索引
						".1.3.6.1.4.1.789.1.5.10.1.2",  //包含此tree的卷ID
						".1.3.6.1.4.1.789.1.5.10.1.3",  //包含此tree的卷名称
						".1.3.6.1.4.1.789.1.5.10.1.4",  //tree ID
						".1.3.6.1.4.1.789.1.5.10.1.5",  //tree Name
						".1.3.6.1.4.1.789.1.5.10.1.6",  //tree 类型
						".1.3.6.1.4.1.789.1.5.10.1.7",  //tree 状态
						".1.3.6.1.4.1.789.1.5.10.1.8",  //tree  oplocks选项
						
				};

				String[][] valueArray = null;
				try {
					valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), node.getSecuritylevel(), node.getSecurityName(), node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 3, 1000 * 30);
				} catch (Exception e) {
					valueArray = null;
				}
				if (valueArray != null) {
					for (int i = 0; i < valueArray.length; i++) {
						netApptree = new NetAppTree();
						netApptree.setIpaddress(node.getIpAddress());
						netApptree.setCollectTime(date);
						netApptree.setTreeIndex(valueArray[i][0]);
						netApptree.setTreeVolume(valueArray[i][1]);
						netApptree.setTreeVolumeName(valueArray[i][2]);
						netApptree.setTreeId(valueArray[i][3]);
						netApptree.setTreeName(valueArray[i][4]);
						netApptree.setTreeStatus(valueArray[i][5]);
						netApptree.setTreeStyle(valueArray[i][6]);
						netApptree.setTreeOpLocks(valueArray[i][7]);
						tempVector.addElement(netApptree);
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
				ipAllData.put("tree", tempVector);
			ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		} else {
			if (tempVector != null && tempVector.size() > 0)
				((Hashtable) ShareData.getSharedata().get(node.getIpAddress())).put("tree", tempVector);
		}

		returnHash.put("tree", tempVector);

		tempVector = null;

		NetAppDataOperator op=new NetAppDataOperator();
		op.CreateResultTosql(returnHash, node.getIpAddress());

		return returnHash;
	}
}
