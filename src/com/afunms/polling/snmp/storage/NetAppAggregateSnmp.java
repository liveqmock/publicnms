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
import com.afunms.polling.om.NetAppAggregate;

//NetApp Aggregate(聚合) 采集类
public class NetAppAggregateSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash = new Hashtable();
		Vector tempVector = new Vector();
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if (node == null)
			return returnHash;

		try {
			NetAppAggregate netAppAggregate = null;
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
				String[] oids = new String[] { ".1.3.6.1.4.1.789.1.5.11.1.1",  //聚合索引
						".1.3.6.1.4.1.789.1.5.11.1.2",  //聚合名称
						".1.3.6.1.4.1.789.1.5.11.1.3",  //聚合唯一标识
						".1.3.6.1.4.1.789.1.5.11.1.4",  //聚合所属主机
						".1.3.6.1.4.1.789.1.5.11.1.5",  //聚合在线状态
						".1.3.6.1.4.1.789.1.5.11.1.6",  //聚合属性状态
						".1.3.6.1.4.1.789.1.5.11.1.7",  //聚合配置项
						".1.3.6.1.4.1.789.1.5.11.1.8",  //聚合唯一ID
						".1.3.6.1.4.1.789.1.5.11.1.9",  //聚合包含的Volume
						".1.3.6.1.4.1.789.1.5.11.1.10",  //聚合类型
						".1.3.6.1.4.1.789.1.5.11.1.11",  //保护聚合的Raid
				};

				String[][] valueArray = null;
				try {
					valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), node.getSecuritylevel(), node.getSecurityName(), node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 3, 1000 * 30);
				} catch (Exception e) {
					valueArray = null;
				}
				if (valueArray != null) {
					for (int i = 0; i < valueArray.length; i++) {
						netAppAggregate = new NetAppAggregate();
						netAppAggregate.setIpaddress(node.getIpAddress());
						netAppAggregate.setCollectTime(date);
						netAppAggregate.setAggrIndex(valueArray[i][0]);
						netAppAggregate.setAggrName(valueArray[i][1]);
						netAppAggregate.setAggrFSID(valueArray[i][2]);
						netAppAggregate.setAggrOwningHost(valueArray[i][3]);
						netAppAggregate.setAggrState(valueArray[i][4]);
						netAppAggregate.setAggrStatus(valueArray[i][5]);
						netAppAggregate.setAggrOptions(valueArray[i][6]);
						netAppAggregate.setAggrUUID(valueArray[i][7]);
						netAppAggregate.setAggrFlexvollist(valueArray[i][8]);
						netAppAggregate.setAggrType(valueArray[i][9]);
						netAppAggregate.setAggrRaidType(valueArray[i][10]);
						tempVector.addElement(netAppAggregate);
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
				ipAllData.put("aggregate", tempVector);
			ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		} else {
			if (tempVector != null && tempVector.size() > 0)
				((Hashtable) ShareData.getSharedata().get(node.getIpAddress())).put("aggregate", tempVector);
		}

		returnHash.put("aggregate", tempVector);
		tempVector = null;
		
		NetAppDataOperator op=new NetAppDataOperator();
		op.CreateResultTosql(returnHash, node.getIpAddress());
		
		return returnHash;
	}
}
