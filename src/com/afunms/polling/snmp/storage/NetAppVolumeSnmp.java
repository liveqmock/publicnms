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
import com.afunms.polling.om.NetAppVolume;

//NetApp volume采集类
public class NetAppVolumeSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash = new Hashtable();
		Vector tempVector = new Vector();
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if (node == null)
			return returnHash;

		try {
			NetAppVolume netAppvolume = null;
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
				String[] oids = new String[] { ".1.3.6.1.4.1.789.1.5.8.1.1",  //Volume 索引
						".1.3.6.1.4.1.789.1.5.8.1.2",  //Volume 名称
						".1.3.6.1.4.1.789.1.5.8.1.3",  //Volume 唯一标识
						".1.3.6.1.4.1.789.1.5.8.1.4",  //Volume 所属主机
						".1.3.6.1.4.1.789.1.5.8.1.5",  //Volume 在线状态
						".1.3.6.1.4.1.789.1.5.8.1.6",  //Volume 属性
						".1.3.6.1.4.1.789.1.5.8.1.7",  //Volume 选项
						".1.3.6.1.4.1.789.1.5.8.1.8",  //Volume ID
						".1.3.6.1.4.1.789.1.5.8.1.9",  //Volume aggregate
						".1.3.6.1.4.1.789.1.5.8.1.10",  //Volume 类型
						
				};

				String[][] valueArray = null;
				try {
					valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), node.getSecuritylevel(), node.getSecurityName(), node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 3, 1000 * 30);
				} catch (Exception e) {
					valueArray = null;
				}
				if (valueArray != null) {
					for (int i = 0; i < valueArray.length; i++) {
						netAppvolume = new NetAppVolume();
						netAppvolume.setIpaddress(node.getIpAddress());
						netAppvolume.setCollectTime(date);
						netAppvolume.setVolIndex(valueArray[i][0]);
						netAppvolume.setVolName(valueArray[i][1]);
						netAppvolume.setVolFSID(valueArray[i][2]);
						netAppvolume.setVolOwningHost(valueArray[i][3]);
						netAppvolume.setVolState(valueArray[i][4]);
						netAppvolume.setVolStatus(valueArray[i][5]);
						netAppvolume.setVolOptions(valueArray[i][6]);
						netAppvolume.setVolUUID(valueArray[i][7]);
						netAppvolume.setVolAggrName(valueArray[i][8]);
						netAppvolume.setVolType(valueArray[i][9]);
						tempVector.addElement(netAppvolume);
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
				ipAllData.put("volume", tempVector);
			ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		} else {
			if (tempVector != null && tempVector.size() > 0)
				((Hashtable) ShareData.getSharedata().get(node.getIpAddress())).put("volume", tempVector);
		}

		returnHash.put("volume", tempVector);

		tempVector = null;

		NetAppDataOperator op=new NetAppDataOperator();
		op.CreateResultTosql(returnHash, node.getIpAddress());

		return returnHash;
	}
}
