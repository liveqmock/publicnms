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
import com.afunms.polling.om.NetAppVFiler;

//NetApp VFiler 概览采集类
public class NetAppVFilerSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash = new Hashtable();
		Vector tempVector = new Vector();
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if (node == null)
			return returnHash;

		try {
			NetAppVFiler netAppVFiler = null;
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
				String[] oids = new String[] { ".1.3.6.1.4.1.789.1.16.3.1.1",  //vfiler 索引
						".1.3.6.1.4.1.789.1.16.3.1.2",  //vfiler 名称
						".1.3.6.1.4.1.789.1.16.3.1.3",  //vfiler 统一标志
						".1.3.6.1.4.1.789.1.16.3.1.4",  //vfiler 关联的IP数目
						".1.3.6.1.4.1.789.1.16.3.1.5",  //vfiler 关联的存储路劲数目
						".1.3.6.1.4.1.789.1.16.3.1.6",  //vfiler 存储空间
						".1.3.6.1.4.1.789.1.16.3.1.7",  //vfiler 已被允许的协议数目
						".1.3.6.1.4.1.789.1.16.3.1.8",  //vfiler 不被允许的协议数目
						".1.3.6.1.4.1.789.1.16.3.1.9",  //vfiler 状态
				};

				String[][] valueArray = null;
				try {
					valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), node.getSecuritylevel(), node.getSecurityName(), node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 3, 1000 * 30);
				} catch (Exception e) {
					valueArray = null;
				}
				if (valueArray != null) {
					for (int i = 0; i < valueArray.length; i++) {
						netAppVFiler = new NetAppVFiler();
						netAppVFiler.setIpaddress(node.getIpAddress());
						netAppVFiler.setCollectTime(date);
						netAppVFiler.setVfIndex(valueArray[i][0]);
						netAppVFiler.setVfName(valueArray[i][1]);
						netAppVFiler.setVfUuid(valueArray[i][2]);
						netAppVFiler.setVfIpAddresses(valueArray[i][3]);
						netAppVFiler.setVfStoragePaths(valueArray[i][4]);
						netAppVFiler.setVfIpSpace(valueArray[i][5]);
						netAppVFiler.setVfAllowedProtocols(valueArray[i][6]);
						netAppVFiler.setVfDisallowedProtocols(valueArray[i][7]);
						netAppVFiler.setVfState(valueArray[i][8]);
						tempVector.addElement(netAppVFiler);
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
				ipAllData.put("vfiler", tempVector);
			ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		} else {
			if (tempVector != null && tempVector.size() > 0)
				((Hashtable) ShareData.getSharedata().get(node.getIpAddress())).put("vfiler", tempVector);
		}

		returnHash.put("vfiler", tempVector);

		tempVector = null;

		NetAppDataOperator op=new NetAppDataOperator();
		op.CreateResultTosql(returnHash, node.getIpAddress());

		return returnHash;
	}
}
