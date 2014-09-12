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
import com.afunms.polling.om.NetAppEnvironment;

//NetApp 环境 采集类
public class NetAppEnvironmentSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash = new Hashtable();
		Vector tempVector = new Vector();
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if (node == null)
			return returnHash;

		try {
			NetAppEnvironment netAppEnvironment = null;
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
				String[] oids = new String[] { ".1.3.6.1.4.1.789.1.2.4.1.0",  //温度过高次数
						".1.3.6.1.4.1.789.1.2.4.2.0",  //风扇失效次数
						".1.3.6.1.4.1.789.1.2.4.3.0",  //风扇失效消息
						".1.3.6.1.4.1.789.1.2.4.4.0",  //电源失效次数
						".1.3.6.1.4.1.789.1.2.4.5.0",  //电源失效信息
						".1.3.6.1.4.1.789.1.2.5.1.0",  //电池状态
						
				};

				String[][] valueArray = null;
				try {
					valueArray = SnmpUtils.getList(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(),  3, 1000 * 30);
				} catch (Exception e) {
					valueArray = null;
				}
				if (valueArray != null) {
					for (int i = 0; i < valueArray.length; i++) {
						netAppEnvironment = new NetAppEnvironment();
						netAppEnvironment.setIpaddress(node.getIpAddress());
						netAppEnvironment.setCollectTime(date);
						netAppEnvironment.setEnvOverTemperature(valueArray[i][0]);
						netAppEnvironment.setEnvFailedFanCount(valueArray[i][1]);
						netAppEnvironment.setEnvFailedFanMessage(valueArray[i][2]);
						netAppEnvironment.setEnvFailedPowerSupplyCount(valueArray[i][3]);
						netAppEnvironment.setEnvFailedPowerSupplyMessage(valueArray[i][4]);
						netAppEnvironment.setNvramBatteryStatus(valueArray[i][5]);
						tempVector.addElement(netAppEnvironment);
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
				ipAllData.put("environment", tempVector);
			ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		} else {
			if (tempVector != null && tempVector.size() > 0)
				((Hashtable) ShareData.getSharedata().get(node.getIpAddress())).put("environment", tempVector);
		}

		returnHash.put("environment", tempVector);

		tempVector = null;

		NetAppDataOperator op=new NetAppDataOperator();
		op.CreateResultTosql(returnHash, node.getIpAddress());

		return returnHash;
	}
}
