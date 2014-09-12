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
import com.afunms.polling.om.NetAppRaid;

//NetApp Raid采集类
public class NetAppRaidSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash = new Hashtable();
		Vector tempVector = new Vector();
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if (node == null)
			return returnHash;

		try {
			NetAppRaid netAppRaid = null;
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
				String[] oids = new String[] { ".1.3.6.1.4.1.789.1.6.2.1.1",  //索引
						 ".1.3.6.1.4.1.789.1.6.2.1.2", //磁盘名称
						 ".1.3.6.1.4.1.789.1.6.2.1.3", //Raid状态
						 ".1.3.6.1.4.1.789.1.6.2.1.4", //磁盘ID
						 ".1.3.6.1.4.1.789.1.6.2.1.5", //SCSI驱动器名称
						 ".1.3.6.1.4.1.789.1.6.2.1.6", //SCSI驱动器ID
						 ".1.3.6.1.4.1.789.1.6.2.1.7", //Raid在驱动器上使用的空间大小(MB)
						 ".1.3.6.1.4.1.789.1.6.2.1.8", //Raid在驱动器上使用的块总数
						 ".1.3.6.1.4.1.789.1.6.2.1.9", //Raid所在驱动器的总大小(MB)
						 ".1.3.6.1.4.1.789.1.6.2.1.10", //Raid所在驱动器块的总数
						 ".1.3.6.1.4.1.789.1.6.2.1.11", //Raid快速恢复比例
						 ".1.3.6.1.4.1.789.1.6.2.1.12", //Raid关联的Volume
						 ".1.3.6.1.4.1.789.1.6.2.1.13", //Raid关联的VolumeGroup
						 ".1.3.6.1.4.1.789.1.6.2.1.14", //Raid关联的Disk数目
						 ".1.3.6.1.4.1.789.1.6.2.1.15", //在此驱动器上包含的Raid Group 数目
						 ".1.3.6.1.4.1.789.1.6.2.1.16", //驱动器使用的端口
						 ".1.3.6.1.4.1.789.1.6.2.1.17", //第二块磁盘名称
						 ".1.3.6.1.4.1.789.1.6.2.1.18", //第二块磁盘使用的端口
						 ".1.3.6.1.4.1.789.1.6.2.1.19", //驱动器所在架子
						 ".1.3.6.1.4.1.789.1.6.2.1.20", //驱动器所在bay
						 ".1.3.6.1.4.1.789.1.6.2.1.21", //Raid所属Plex
						 ".1.3.6.1.4.1.789.1.6.2.1.22", //Raid所属PlexGroup
						 ".1.3.6.1.4.1.789.1.6.2.1.23", //驱动器包含的Plex数目
						 ".1.3.6.1.4.1.789.1.6.2.1.24", //Raid所属Plex名称
						 ".1.3.6.1.4.1.789.1.6.2.1.25", //Raid区块大小
						 ".1.3.6.1.4.1.789.1.6.2.1.26", //Raid驱动器序号
						 ".1.3.6.1.4.1.789.1.6.2.1.27", //Raid提供商
						 ".1.3.6.1.4.1.789.1.6.2.1.28", //Raid Model string
						 ".1.3.6.1.4.1.789.1.6.2.1.29", //Raid软件版本
						 ".1.3.6.1.4.1.789.1.6.2.1.30", //驱动器转速
						 ".1.3.6.1.4.1.789.1.6.2.1.31", //驱动器类型
						 ".1.3.6.1.4.1.789.1.6.2.1.32", //驱动器池
						 ".1.3.6.1.4.1.789.1.6.2.1.33", //目的驱动器名称
				};

				String[][] valueArray = null;
				try {
					valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), node.getSecuritylevel(), node.getSecurityName(), node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 3, 1000 * 30);
				} catch (Exception e) {
					valueArray = null;
				}
				if (valueArray != null) {
					for (int i = 0; i < valueArray.length; i++) {
						netAppRaid = new NetAppRaid();
						netAppRaid.setIpaddress(node.getIpAddress());
						netAppRaid.setCollectTime(date);
						netAppRaid.setRaidVIndex(valueArray[i][0]);
						netAppRaid.setRaidVDiskName(valueArray[i][1]);
						netAppRaid.setRaidVStatus(valueArray[i][2]);
						netAppRaid.setRaidVDiskId(valueArray[i][3]);
						netAppRaid.setRaidVScsiAdapter(valueArray[i][4]);
						netAppRaid.setRaidVScsiId(valueArray[i][5]);
						netAppRaid.setRaidVUsedMb(valueArray[i][6]);
						netAppRaid.setRaidVUsedBlocks(valueArray[i][7]);
						netAppRaid.setRaidVTotalMb(valueArray[i][8]);
						netAppRaid.setRaidVTotalBlocks(valueArray[i][9]);
						netAppRaid.setRaidVCompletionPerCent(valueArray[i][10]);
						netAppRaid.setRaidVVol(valueArray[i][11]);
						netAppRaid.setRaidVGroup(valueArray[i][12]);
						netAppRaid.setRaidVDiskNumber(valueArray[i][13]);
						netAppRaid.setRaidVGroupNumber(valueArray[i][14]);
						netAppRaid.setRaidVDiskPort(valueArray[i][15]);
						netAppRaid.setRaidVSecondaryDiskName(valueArray[i][16]);
						netAppRaid.setRaidVSecondaryDiskPort(valueArray[i][17]);
						netAppRaid.setRaidVShelf(valueArray[i][18]);
						netAppRaid.setRaidVBay(valueArray[i][19]);
						netAppRaid.setRaidVPlex(valueArray[i][20]);
						netAppRaid.setRaidVPlexGroup(valueArray[i][21]);
						netAppRaid.setRaidVPlexNumber(valueArray[i][22]);
						netAppRaid.setRaidVPlexName(valueArray[i][23]);
						netAppRaid.setRaidVSectorSize(valueArray[i][24]);
						netAppRaid.setRaidVDiskSerialNumber(valueArray[i][25]);
						netAppRaid.setRaidVDiskVendor(valueArray[i][26]);
						netAppRaid.setRaidVDiskModel(valueArray[i][27]);
						netAppRaid.setRaidVDiskFirmwareRevision(valueArray[i][28]);
						netAppRaid.setRaidVDiskRPM(valueArray[i][29]);
						netAppRaid.setRaidVDiskType(valueArray[i][30]);
						netAppRaid.setRaidVDiskPool(valueArray[i][31]);
						netAppRaid.setRaidVDiskCopyDestDiskName(valueArray[i][32]);
						tempVector.addElement(netAppRaid);
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
				ipAllData.put("raid", tempVector);
			ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		} else {
			if (tempVector != null && tempVector.size() > 0)
				((Hashtable) ShareData.getSharedata().get(node.getIpAddress())).put("raid", tempVector);
		}

		returnHash.put("raid", tempVector);

		tempVector = null;

		NetAppDataOperator op=new NetAppDataOperator();
		op.CreateResultTosql(returnHash, node.getIpAddress());

		return returnHash;
	}
}
