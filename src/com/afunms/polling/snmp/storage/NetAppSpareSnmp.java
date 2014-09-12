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
import com.afunms.polling.om.NetAppSpare;

//NetApp Spare盘采集类
public class NetAppSpareSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash = new Hashtable();
		Vector tempVector = new Vector();
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if (node == null)
			return returnHash;

		try {
			NetAppSpare netAppSpare = null;
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
				String[] oids = new String[] { ".1.3.6.1.4.1.789.1.6.3.1.1",  //Spare盘索引
						".1.3.6.1.4.1.789.1.6.3.1.2",  //Spare盘名称
						".1.3.6.1.4.1.789.1.6.3.1.3",  //Spare盘状态
						".1.3.6.1.4.1.789.1.6.3.1.4",  //Spare盘标识
						".1.3.6.1.4.1.789.1.6.3.1.5",  //Spare盘使用的SCSI adapter名称
						".1.3.6.1.4.1.789.1.6.3.1.6",  //Spare盘在SCSI adapter上的 ID
						".1.3.6.1.4.1.789.1.6.3.1.7",  //Spare盘物理大小(Mb)
						".1.3.6.1.4.1.789.1.6.3.1.8",  //Spare盘总区块
						".1.3.6.1.4.1.789.1.6.3.1.9",  //Spare盘使用的port
						".1.3.6.1.4.1.789.1.6.3.1.10",  //Spare盘第二块盘名称
						".1.3.6.1.4.1.789.1.6.3.1.11",  //Spare盘第二块盘使用的port
						".1.3.6.1.4.1.789.1.6.3.1.12",  //Spare盘所在架子
						".1.3.6.1.4.1.789.1.6.3.1.13",  //Spare盘所在bay
						".1.3.6.1.4.1.789.1.6.3.1.14",  //Spare盘所在pool
						".1.3.6.1.4.1.789.1.6.3.1.15",  //Spare盘所在区块大小
						".1.3.6.1.4.1.789.1.6.3.1.16",  //Spare盘序列号
						".1.3.6.1.4.1.789.1.6.3.1.17",  //Spare盘提供商
						".1.3.6.1.4.1.789.1.6.3.1.18",  //Spare盘Model
						".1.3.6.1.4.1.789.1.6.3.1.19",  //Spare盘软件版本
						".1.3.6.1.4.1.789.1.6.3.1.20",  //Spare盘转速
						".1.3.6.1.4.1.789.1.6.3.1.21",  //Spare盘类型
				};

				String[][] valueArray = null;
				try {
					valueArray = SnmpUtils.getSubTreeList(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), node.getSecuritylevel(), node.getSecurityName(), node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 3, 1000 * 30);
				} catch (Exception e) {
					valueArray = null;
				}
				if (valueArray != null) {
					for (int i = 0; i < valueArray.length; i++) {
						netAppSpare = new NetAppSpare();
						netAppSpare.setIpaddress(node.getIpAddress());
						netAppSpare.setCollectTime(date);
						netAppSpare.setSpareIndex(valueArray[i][0]);
						netAppSpare.setSpareDiskName(valueArray[i][1]);
						netAppSpare.setSpareStatus(valueArray[i][2]);
						netAppSpare.setSpareDiskId(valueArray[i][3]);
						netAppSpare.setSpareScsiAdapter(valueArray[i][4]);
						netAppSpare.setSpareScsiId(valueArray[i][5]);
						netAppSpare.setSpareTotalMb(valueArray[i][6]);
						netAppSpare.setSpareTotalBlocks(valueArray[i][7]);
						netAppSpare.setSpareDiskPort(valueArray[i][8]);
						netAppSpare.setSpareSecondaryDiskName(valueArray[i][9]);
						netAppSpare.setSpareSecondaryDiskPort(valueArray[i][10]);
						netAppSpare.setSpareShelf(valueArray[i][11]);
						netAppSpare.setSpareBay(valueArray[i][12]);
						netAppSpare.setSparePool(valueArray[i][13]);
						netAppSpare.setSpareSectorSize(valueArray[i][14]);
						netAppSpare.setSpareDiskSerialNumber(valueArray[i][15]);
						netAppSpare.setSpareDiskVendor(valueArray[i][16]);
						netAppSpare.setSpareDiskModel(valueArray[i][17]);
						netAppSpare.setSpareDiskFirmwareRevision(valueArray[i][18]);
						netAppSpare.setSpareDiskRPM(valueArray[i][19]);
						netAppSpare.setSpareDiskType(valueArray[i][20]);
						tempVector.addElement(netAppSpare);
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
				ipAllData.put("spare", tempVector);
			ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		} else {
			if (tempVector != null && tempVector.size() > 0)
				((Hashtable) ShareData.getSharedata().get(node.getIpAddress())).put("spare", tempVector);
		}

		returnHash.put("spare", tempVector);

		tempVector = null;

		NetAppDataOperator op=new NetAppDataOperator();
		op.CreateResultTosql(returnHash, node.getIpAddress());

		return returnHash;
	}
}
