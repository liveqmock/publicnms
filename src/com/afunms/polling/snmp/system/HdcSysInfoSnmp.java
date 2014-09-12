package com.afunms.polling.snmp.system;

/*
 * @author hukelei@dhcc.com.cn
 * 2012-09-11
 */
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.common.util.SysLogger;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.HdcMessage;
import com.afunms.polling.om.Interfacecollectdata;
import com.afunms.topology.model.HostNode;
import com.gatherdb.GathersqlListManager;

public class HdcSysInfoSnmp extends SnmpMonitor {

	public HdcSysInfoSnmp() {
	}

	public void collectData(Node node, MonitoredItem item) {
	}

	public void collectData(HostNode node) {
	}

	public void CreateResultTosql(Hashtable dataresult, Host node) {
		// 处理hdc—sys-info
		if (dataresult != null && dataresult.size() > 0) {
			Vector sysInfoVector = null;
			HdcMessage hdcVo =null;
			NodeDTO nodeDTO = null;
			String ip = null;
			Interfacecollectdata vo = null;
			Calendar tempCal = null;
			Date cc = null;
			String time = null;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			NodeUtil nodeUtil = new NodeUtil();
			nodeDTO = nodeUtil.creatNodeDTOByNode(node);
			String hendsql = "insert into hdc_sys_info (raidlistSerialNumber,raidlistMibNickName,raidlistDKCMainVersion,raidlistDKCProductName,nodeid) values(";
			String endsql = "')";
			String deleteSql = "delete from hdc_sys_info where nodeid='"
					+ node.getId() + "'";
			sysInfoVector = (Vector) dataresult.get("sysinfo");
			Vector list = new Vector();
			if (sysInfoVector != null && sysInfoVector.size() > 0) {
				for (int i = 0; i < sysInfoVector.size(); i++) {
					hdcVo = (HdcMessage) sysInfoVector.elementAt(i);
					StringBuffer sbuffer = new StringBuffer(150);
					sbuffer.append(hendsql);
					sbuffer.append("'").append(hdcVo.getRaidlistDKCMainVersion())
							.append("',");
					sbuffer.append("'").append(hdcVo.getRaidlistDKCProductName())
							.append("',");
					sbuffer.append("'").append(hdcVo.getRaidlistMibNickName()).append(
							"',");
					sbuffer.append("'").append(hdcVo.getRaidlistSerialNumber()).append(
							"',");
					sbuffer.append("'").append(node.getId());
					sbuffer.append(endsql);
					list.add(sbuffer.toString());
					sbuffer = null;
				}
				GathersqlListManager.AdddateTempsql(deleteSql, list);
				list = null;
			}
		}
	}

	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		SysLogger.info("########开始采集hdc的系统信息##########");
		Hashtable returnHash = new Hashtable();
		Vector sysinfo = new Vector();
		HdcMessage hdcMessage;
		Host node = (Host) PollingEngine.getInstance().getNodeByID(
				Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if (node == null)
			return null;
		if (node.getIpAddress().equals(""))
			return null;
		try {
//			Calendar date = Calendar.getInstance();
//			String temp = "0";
			String[][] valueArray = null;
			String[] oids = new String[] { ".1.3.6.1.4.1.116.5.11.4.1.1.5.1.1",// raidlistSerialNumber
					// 产品序列号
					".1.3.6.1.4.1.116.5.11.4.1.1.5.1.2",// raidlistMibNickName
					// mib名称
					".1.3.6.1.4.1.116.5.11.4.1.1.5.1.3",// raidlistDKCMainVersion
					// dkc版本
					".1.3.6.1.4.1.116.5.11.4.1.1.5.1.4",// raidlistDKCProductName
			// dkc产品名
			};
//			valueArray = SnmpUtils.getTableData(node
//														.getIpAddress(), node
//															.getCommunity(), oids, node
//																.getSnmpversion(), 3, 1000 * 30);
			valueArray = SnmpUtils.getTableData(node.getIpAddress(),node.getCommunity(),oids,node.getSnmpversion(),
   		  			node.getSecuritylevel(),node.getSecurityName(),node.getV3_ap(),node.getAuthpassphrase(),node.getV3_privacy(),node.getPrivacyPassphrase(),3,1000*30);
			if (valueArray != null) {
				for (int i = 0; i < valueArray.length; i++) {
					String raidlistSerialNumber = valueArray[i][0];
					String raidlistMibNickName = valueArray[i][1];
					String raidlistDKCMainVersion = valueArray[i][2];
					String raidlistDKCProductName = valueArray[i][3];
					hdcMessage = new HdcMessage();
					hdcMessage.setRaidlistSerialNumber(raidlistSerialNumber);
					hdcMessage.setRaidlistMibNickName(raidlistMibNickName);
					hdcMessage
							.setRaidlistDKCMainVersion(raidlistDKCMainVersion);
					hdcMessage
							.setRaidlistDKCProductName(raidlistDKCProductName);
//					hdcMessage.setRaidlistSerialNumber("QO_1566");//序列
//					hdcMessage.setRaidlistMibNickName("日历");//名称
//					hdcMessage
//							.setRaidlistDKCMainVersion("RQ234");//版本
//					hdcMessage
//							.setRaidlistDKCProductName("内存");//产品名
					sysinfo.addElement(hdcMessage);
					SysLogger.info("######################存储设备系统信息采集完毕##########");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		 if(!(ShareData.getSharedata().containsKey(node.getIpAddress())))
			{
				Hashtable ipAllData = new Hashtable();
				if(ipAllData == null)ipAllData = new Hashtable();
				if(sysinfo != null && sysinfo.size()>0)ipAllData.put("sysinfo",sysinfo);
			    ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
			}else
			 {
				if(sysinfo != null && sysinfo.size()>0)((Hashtable)ShareData.getSharedata().get(node.getIpAddress())).put("sysinfo",sysinfo);
			 }
		
		returnHash.put("sysinfo", sysinfo);
		// 把采集结果生成sql
		
		this.CreateResultTosql(returnHash, node);
		return returnHash;
	}
}
