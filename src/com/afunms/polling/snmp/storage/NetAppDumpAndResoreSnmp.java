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
import com.afunms.polling.om.NetAppDump;
import com.afunms.polling.om.NetAppDumpList;
import com.afunms.polling.om.NetAppRestore;

//NetApp 备份 还原 信息采集类
public class NetAppDumpAndResoreSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash = new Hashtable();
		Vector dumpTempVector = new Vector();
		Vector dumpListTempVector = new Vector();
		Vector restoreTempVector = new Vector();
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if (node == null)
			return returnHash;

		try {
			NetAppDump netAppDump = null;
			NetAppDumpList netAppDumpList = null;
			NetAppRestore netAppRestore = null;
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
				String[] dumpOids = new String[] { ".1.3.6.1.4.1.789.1.14.1.1.0", //备份总次数
						".1.3.6.1.4.1.789.1.14.1.1.1", //尝试备份次数
						".1.3.6.1.4.1.789.1.14.1.1.2", //备份成功次数
						".1.3.6.1.4.1.789.1.14.1.1.3", //备份失败次数
						};

				String[] dumpListOids = new String[] { ".1.3.6.1.4.1.789.1.14.1.5.1.1", //备份索引
						 ".1.3.6.1.4.1.789.1.14.1.5.1.2", //备份路径
						 ".1.3.6.1.4.1.789.1.14.1.5.1.3", //尝试备份次数
						 ".1.3.6.1.4.1.789.1.14.1.5.1.4", //备份成功次数
						 ".1.3.6.1.4.1.789.1.14.1.5.1.5", //备份失败次数
						 ".1.3.6.1.4.1.789.1.14.1.5.1.6", //上一次备份时间
						 ".1.3.6.1.4.1.789.1.14.1.5.1.7", //备份状态
						 ".1.3.6.1.4.1.789.1.14.1.5.1.8", //备份等级
						 ".1.3.6.1.4.1.789.1.14.1.5.1.9", //备份文件数
						 ".1.3.6.1.4.1.789.1.14.1.5.1.10", //备份大小
						 ".1.3.6.1.4.1.789.1.14.1.5.1.11", //备份第一次开始时间
						 ".1.3.6.1.4.1.789.1.14.1.5.1.12", //备份持续时间
						};

				String[] restoreOids = new String[] { ".1.3.6.1.4.1.789.1.14.2.1.0", //恢复次数
						".1.3.6.1.4.1.789.1.14.2.1.1", //尝试恢复次数
						".1.3.6.1.4.1.789.1.14.2.1.2", //成功恢复次数
						".1.3.6.1.4.1.789.1.14.2.1.3", //恢复失败次数
						};

				String[][] dumpValueArray = null;
				String[][] dumpListValueArray = null;
				String[][] restoreValueArray = null;
				try {
					dumpValueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), dumpOids, node.getSnmpversion(), node.getSecuritylevel(), node.getSecurityName(), node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 3, 1000 * 30);
					dumpListValueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), dumpListOids, node.getSnmpversion(), node.getSecuritylevel(), node.getSecurityName(), node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 3, 1000 * 30);
					restoreValueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), restoreOids, node.getSnmpversion(), node.getSecuritylevel(), node.getSecurityName(), node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 3, 1000 * 30);
				} catch (Exception e) {
					dumpValueArray = null;
					dumpListValueArray = null;
					restoreValueArray = null;
				}
				if (dumpValueArray != null) {
					for (int i = 0; i < dumpValueArray.length; i++) {
						netAppDump = new NetAppDump();
						netAppDump.setIpaddress(node.getIpAddress());
						netAppDump.setCollectTime(date);
						netAppDump.setDmpActives(dumpValueArray[i][0]);
						netAppDump.setDmpAttempts(dumpValueArray[i][1]);
						netAppDump.setDmpSuccesses(dumpValueArray[i][2]);
						netAppDump.setDmpFailures(dumpValueArray[i][3]);
						dumpTempVector.addElement(netAppDump);
					}
				}

				if (dumpListValueArray != null) {
					for (int i = 0; i < dumpListValueArray.length; i++) {
						netAppDumpList = new NetAppDumpList();
						netAppDumpList.setIpaddress(node.getIpAddress());
						netAppDumpList.setCollectTime(date);
						netAppDumpList.setDmpIndex(dumpValueArray[i][0]);
						netAppDumpList.setDmpStPath(dumpValueArray[i][1]);
						netAppDumpList.setDmpStAttempts(dumpValueArray[i][2]);
						netAppDumpList.setDmpStSuccesses(dumpValueArray[i][3]);
						netAppDumpList.setDmpStFailures(dumpValueArray[i][4]);
						netAppDumpList.setDmpTime(dumpValueArray[i][5]);
						netAppDumpList.setDmpStatus(dumpValueArray[i][6]);
						netAppDumpList.setDmpLevel(dumpValueArray[i][7]);
						netAppDumpList.setDmpNumFiles(dumpValueArray[i][8]);
						netAppDumpList.setDmpDataAmount(dumpValueArray[i][9]);
						netAppDumpList.setDmpStartTime(dumpValueArray[i][10]);
						netAppDumpList.setDmpDuration(dumpValueArray[i][11]);
						dumpListTempVector.addElement(netAppDumpList);
					}
				}

				if (restoreValueArray != null) {
					for (int i = 0; i < restoreValueArray.length; i++) {
						netAppRestore = new NetAppRestore();
						netAppRestore.setIpaddress(node.getIpAddress());
						netAppRestore.setCollectTime(date);
						netAppRestore.setRstActives(dumpValueArray[i][0]);
						netAppRestore.setRstAttempts(dumpValueArray[i][1]);
						netAppRestore.setRstSuccesses(dumpValueArray[i][2]);
						netAppRestore.setRstFailures(dumpValueArray[i][3]);
						restoreTempVector.addElement(netAppRestore);
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
			if (dumpTempVector != null && dumpTempVector.size() > 0) {
				ipAllData.put("dump", dumpTempVector);
				ipAllData.put("dumpList", dumpListTempVector);
				ipAllData.put("restore", restoreTempVector);
			}
			ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		} else {
			if (dumpTempVector != null && dumpTempVector.size() > 0) {
				((Hashtable) ShareData.getSharedata().get(node.getIpAddress())).put("dump", dumpTempVector);
				((Hashtable) ShareData.getSharedata().get(node.getIpAddress())).put("dumpList", dumpListTempVector);
				((Hashtable) ShareData.getSharedata().get(node.getIpAddress())).put("restore", restoreTempVector);
			}
		}

		returnHash.put("dump", dumpTempVector);
		returnHash.put("dumpList", dumpListTempVector);
		returnHash.put("restore", restoreTempVector);

		dumpTempVector = null;
		dumpListTempVector = null;
		restoreTempVector = null;


		NetAppDataOperator op=new NetAppDataOperator();
		op.CreateResultTosql(returnHash, node.getIpAddress());
		
		return returnHash;
	}
}
