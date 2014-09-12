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
import com.afunms.polling.om.NetAppDisk;

//NetApp Disk采集类
public class NetAppDiskSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash = new Hashtable();
		Vector tempVector = new Vector();
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if (node == null)
			return returnHash;

		try {
			NetAppDisk netAppDisk = null;
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
				String[] oids = new String[] { ".1.3.6.1.4.1.789.1.5.4.1.1",  //分区索引
						".1.3.6.1.4.1.789.1.5.4.1.2",  //分区名称
						".1.3.6.1.4.1.789.1.5.4.1.3",  //分区总大小(kb)
						".1.3.6.1.4.1.789.1.5.4.1.4",  //分区已用大小
						".1.3.6.1.4.1.789.1.5.4.1.5",  //分区可用大小
						".1.3.6.1.4.1.789.1.5.4.1.6",  //分区使用率
						".1.3.6.1.4.1.789.1.5.4.1.7",  //已用inode
						".1.3.6.1.4.1.789.1.5.4.1.8",  //空闲inode
						".1.3.6.1.4.1.789.1.5.4.1.9",  //inode使用率
						".1.3.6.1.4.1.789.1.5.4.1.10",  //挂载点
						".1.3.6.1.4.1.789.1.5.4.1.11",  //允许最大文件数
						".1.3.6.1.4.1.789.1.5.4.1.12",  //已使用的文件数
						".1.3.6.1.4.1.789.1.5.4.1.13",  //最大文件数参数
						".1.3.6.1.4.1.789.1.5.4.1.14",  //文件系统总容量（High）
						".1.3.6.1.4.1.789.1.5.4.1.15",  //文件系统总容量（low）
						".1.3.6.1.4.1.789.1.5.4.1.16",  //文件系统已使用容量（High）
						".1.3.6.1.4.1.789.1.5.4.1.17",  //文件系统已使用容量（low）
						".1.3.6.1.4.1.789.1.5.4.1.18",  //文件系统空闲容量（High）
						".1.3.6.1.4.1.789.1.5.4.1.19",  //文件系统空闲容量（low）
						".1.3.6.1.4.1.789.1.5.4.1.20",  //文件系统状态
						".1.3.6.1.4.1.789.1.5.4.1.21",  //文件系统镜像状态
						".1.3.6.1.4.1.789.1.5.4.1.22",  //文件系统Plex数目
						".1.3.6.1.4.1.789.1.5.4.1.23",  //文件系统类型
				};

				String[][] valueArray = null;
				try {
					valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), node.getSecuritylevel(), node.getSecurityName(), node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 3, 1000 * 30);
				} catch (Exception e) {
					valueArray = null;
				}
				if (valueArray != null) {
					for (int i = 0; i < valueArray.length; i++) {
						netAppDisk = new NetAppDisk();
						netAppDisk.setIpaddress(node.getIpAddress());
						netAppDisk.setCollectTime(date);
						netAppDisk.setDfIndex(valueArray[i][0]);
						netAppDisk.setDfFileSys(valueArray[i][1]);
						netAppDisk.setDfKBytesTotal(valueArray[i][2]);
						netAppDisk.setDfKBytesUsed(valueArray[i][3]);
						netAppDisk.setDfKBytesAvail(valueArray[i][4]);
						netAppDisk.setDfPerCentKBytesCapacity(valueArray[i][5]);
						netAppDisk.setDfInodesUsed(valueArray[i][6]);
						netAppDisk.setDfInodesFree(valueArray[i][7]);
						netAppDisk.setDfPerCentInodeCapacity(valueArray[i][8]);
						netAppDisk.setDfMountedOn(valueArray[i][9]);
						netAppDisk.setDfMaxFilesAvail(valueArray[i][10]);
						netAppDisk.setDfMaxFilesUsed(valueArray[i][11]);
						netAppDisk.setDfMaxFilesPossible(valueArray[i][12]);
						netAppDisk.setDfHighTotalKBytes(valueArray[i][13]);
						netAppDisk.setDfLowTotalKBytes(valueArray[i][14]);
						netAppDisk.setDfHighUsedKBytes(valueArray[i][15]);
						netAppDisk.setDfLowUsedKBytes(valueArray[i][16]);
						netAppDisk.setDfHighAvailKBytes(valueArray[i][17]);
						netAppDisk.setDfLowAvailKBytes(valueArray[i][18]);
						netAppDisk.setDfStatus(valueArray[i][19]);
						netAppDisk.setDfMirrorStatus(valueArray[i][20]);
						netAppDisk.setDfPlexCount(valueArray[i][21]);
						netAppDisk.setDfType(valueArray[i][22]);
						tempVector.addElement(netAppDisk);
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
				ipAllData.put("disk", tempVector);
			ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		} else {
			if (tempVector != null && tempVector.size() > 0)
				((Hashtable) ShareData.getSharedata().get(node.getIpAddress())).put("disk", tempVector);
		}

		returnHash.put("disk", tempVector);

		tempVector = null;

		NetAppDataOperator op=new NetAppDataOperator();
		op.CreateResultTosql(returnHash, node.getIpAddress());

		return returnHash;
	}
}
