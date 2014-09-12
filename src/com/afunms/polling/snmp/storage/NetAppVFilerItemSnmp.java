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
import com.afunms.polling.om.NetAppVFilerIpEntity;
import com.afunms.polling.om.NetAppVFilerPathEntity;
import com.afunms.polling.om.NetAppVFilerProtocolEntity;

//NetApp VFiler详细信息采集类
public class NetAppVFilerItemSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash = new Hashtable();
		Vector ipTempVector = new Vector();
		Vector pathTempVector = new Vector();
		Vector protocolTempVector = new Vector();
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if (node == null)
			return returnHash;

		try {
			NetAppVFilerIpEntity netAppVFilerIpEntity = null;
			NetAppVFilerPathEntity netAppVFilerPathEntity = null;
			NetAppVFilerProtocolEntity netAppVFilerProtocolEntity = null;
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
				String[] ipOids = new String[] { ".1.3.6.1.4.1.789.1.16.4.1.1", //VFiler 索引
						".1.3.6.1.4.1.789.1.16.4.1.2", //Ip在VFiler中的索引
						".1.3.6.1.4.1.789.1.16.4.1.3", //Ip 地址
						};

				String[] pathOids = new String[] { ".1.3.6.1.4.1.789.1.16.5.1.1",//VFiler 索引
						".1.3.6.1.4.1.789.1.16.5.1.2",//存储在VFiler中的索引
						".1.3.6.1.4.1.789.1.16.5.1.3", //存储路劲
						};

				String[] protocolOids = new String[] { ".1.3.6.1.4.1.789.1.16.6.1.1", //VFiler 索引
						".1.3.6.1.4.1.789.1.16.6.1.2", //协议在VFiler中的索引
						".1.3.6.1.4.1.789.1.16.6.1.3", //协议名称
						".1.3.6.1.4.1.789.1.16.6.1.4", //协议状态
						};

				String[][] ipValueArray = null;
				String[][] pathValueArray = null;
				String[][] protocolValueArray = null;
				try {
					ipValueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), ipOids, node.getSnmpversion(), node.getSecuritylevel(), node.getSecurityName(), node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 3, 1000 * 30);
					pathValueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), pathOids, node.getSnmpversion(), node.getSecuritylevel(), node.getSecurityName(), node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 3, 1000 * 30);
					protocolValueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), protocolOids, node.getSnmpversion(), node.getSecuritylevel(), node.getSecurityName(), node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 3, 1000 * 30);
				} catch (Exception e) {
					ipValueArray = null;
					pathValueArray = null;
					protocolValueArray = null;
				}
				if (ipValueArray != null) {
					for (int i = 0; i < ipValueArray.length; i++) {
						netAppVFilerIpEntity = new NetAppVFilerIpEntity();
						netAppVFilerIpEntity.setIpaddress(node.getIpAddress());
						netAppVFilerIpEntity.setCollectTime(date);
						netAppVFilerIpEntity.setVfFiIndex(ipValueArray[i][0]);
						netAppVFilerIpEntity.setVfIpIndex(ipValueArray[i][1]);
						netAppVFilerIpEntity.setVfIpAddr(ipValueArray[i][2]);
						ipTempVector.addElement(netAppVFilerIpEntity);
					}
				}

				if (pathValueArray != null) {
					for (int i = 0; i < pathValueArray.length; i++) {
						netAppVFilerPathEntity = new NetAppVFilerPathEntity();
						netAppVFilerPathEntity.setIpaddress(node.getIpAddress());
						netAppVFilerPathEntity.setCollectTime(date);
						netAppVFilerPathEntity.setVfFsIndex(pathValueArray[i][0]);
						netAppVFilerPathEntity.setVfSpIndex(pathValueArray[i][1]);
						netAppVFilerPathEntity.setVfSpName(pathValueArray[i][2]);
						pathTempVector.addElement(netAppVFilerPathEntity);
					}
				}

				if (protocolValueArray != null) {
					for (int i = 0; i < protocolValueArray.length; i++) {
						netAppVFilerProtocolEntity = new NetAppVFilerProtocolEntity();
						netAppVFilerProtocolEntity.setIpaddress(node.getIpAddress());
						netAppVFilerProtocolEntity.setCollectTime(date);
						netAppVFilerProtocolEntity.setVfFpIndex(protocolValueArray[i][0]);
						netAppVFilerProtocolEntity.setVfProIndex(protocolValueArray[i][1]);
						netAppVFilerProtocolEntity.setVfProName(protocolValueArray[i][2]);
						netAppVFilerProtocolEntity.setVfProStatus(protocolValueArray[i][3]);
						protocolTempVector.addElement(netAppVFilerProtocolEntity);
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
			if (ipTempVector != null && ipTempVector.size() > 0) {
				ipAllData.put("vfilerIp", ipTempVector);
				ipAllData.put("vfilerPath", pathTempVector);
				ipAllData.put("vfilerProtocol", protocolTempVector);
			}
			ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		} else {
			if (ipTempVector != null && ipTempVector.size() > 0) {
				((Hashtable) ShareData.getSharedata().get(node.getIpAddress())).put("vfilerIp", ipTempVector);
				((Hashtable) ShareData.getSharedata().get(node.getIpAddress())).put("vfilerPath", pathTempVector);
				((Hashtable) ShareData.getSharedata().get(node.getIpAddress())).put("vfilerProtocol", protocolTempVector);
			}
		}

		returnHash.put("vfilerIp", ipTempVector);
		returnHash.put("vfilerPath", pathTempVector);
		returnHash.put("vfilerProtocol", protocolTempVector);

		ipTempVector = null;
		pathTempVector = null;
		protocolTempVector = null;

		NetAppDataOperator op=new NetAppDataOperator();
		op.CreateResultTosql(returnHash, node.getIpAddress());

		return returnHash;
	}
}
