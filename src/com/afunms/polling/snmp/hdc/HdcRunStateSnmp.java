package com.afunms.polling.snmp.hdc;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.afunms.common.util.SnmpUtils;
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

public class HdcRunStateSnmp extends SnmpMonitor{

	public HdcRunStateSnmp() {
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
			String hendsql = "insert into hdc_run_state (dkcRaidListIndexSerialNumber,dkcHWProcessor,dkcHWCSW,dkcHWCache,dkcHWSM,dkcHWPS,dkcHWBattery,dkcHWFan,dkcHWEnvironment,nodeid) values(";
			String endsql = "')";
			String deleteSql = "delete from hdc_run_state where nodeid='"
					+ node.getId() + "'";
			sysInfoVector = (Vector) dataresult.get("runState");
			Vector list = new Vector();
			if (sysInfoVector != null && sysInfoVector.size() > 0) {
				for (int i = 0; i < sysInfoVector.size(); i++) {
					hdcVo = (HdcMessage) sysInfoVector.elementAt(i);
					StringBuffer sbuffer = new StringBuffer(150);
					sbuffer.append(hendsql);
					sbuffer.append("'").append(hdcVo.getDkcRaidListIndexSerialNumber())
							.append("',");
					sbuffer.append("'").append(hdcVo.getDkcHWProcessor())
							.append("',");
					sbuffer.append("'").append(hdcVo.getDkcHWCSW()).append(
							"',");
					sbuffer.append("'").append(hdcVo.getDkcHWCache()).append(
							"',");
					sbuffer.append("'").append(hdcVo.getDkcHWSM()).append(
							"',");
					sbuffer.append("'").append(hdcVo.getDkcHWPS()).append(
							"',");
					sbuffer.append("'").append(hdcVo.getDkcHWBattery()).append(
							"',");
					sbuffer.append("'").append(hdcVo.getDkcHWFan()).append(
					"',");
					sbuffer.append("'").append(hdcVo.getDkcHWEnvironment()).append(
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
		Hashtable returnHash = new Hashtable();
		Vector runState = new Vector();
		HdcMessage hdcMessage;
		Host node = (Host) PollingEngine.getInstance().getNodeByID(
				Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if (node == null)
			return null;
		if (node.getIpAddress().equals(""))
			return null;
		try {
			Calendar date = Calendar.getInstance();
			String temp = "0";
			String[][] valueArray = null;
			String[] oids = new String[] { ".1.3.6.1.4.1.116.5.11.4.1.1.6.1.1",// dkcRaidListIndexSerialNumber
					// cpu状体
					".1.3.6.1.4.1.116.5.11.4.1.1.6.1.2",// dkcHWProcessor
					// 内部总线状态
					".1.3.6.1.4.1.116.5.11.4.1.1.6.1.3",// dkcHWCSW
					// 内部总线状态
					".1.3.6.1.4.1.116.5.11.4.1.1.6.1.4",// dkcHWCache
					// 缓存状态
					".1.3.6.1.4.1.116.5.11.4.1.1.6.1.5",// dkcHWSM
					// 共享内存状态
					".1.3.6.1.4.1.116.5.11.4.1.1.6.1.6",// dkcHWPS
					// 电源状态
					".1.3.6.1.4.1.116.5.11.4.1.1.6.1.7",// dkcHWBattery
					// 电池状态
					".1.3.6.1.4.1.116.5.11.4.1.1.6.1.8",// dkcHWFan
					// 风扇状态
					".1.3.6.1.4.1.116.5.11.4.1.1.6.1.9",// dkcHWEnvironment
					// 环境状态
			};
//			valueArray = SnmpUtils.getTableData(node
//														.getIpAddress(), node
//															.getCommunity(), oids, node
//																.getSnmpversion(), 3, 1000 * 30);
			valueArray = SnmpUtils.getTableData(node.getIpAddress(),node.getCommunity(),oids,node.getSnmpversion(),
   		  			node.getSecuritylevel(),node.getSecurityName(),node.getV3_ap(),node.getAuthpassphrase(),node.getV3_privacy(),node.getPrivacyPassphrase(),3,1000*30);
			if (valueArray != null) {
				for (int i = 0; i < valueArray.length; i++) {
					String dkcRaidListIndexSerialNumber = valueArray[i][0];
					String dkcHWProcessor = valueArray[i][1];
					String dkcHWCSW = valueArray[i][2];
					String dkcHWCache = valueArray[i][3];
					String dkcHWSM = valueArray[i][4];
					String dkcHWPS = valueArray[i][5];
					String dkcHWBattery = valueArray[i][6];
					String dkcHWFan = valueArray[i][7];
					String dkcHWEnvironment = valueArray[i][8];
					hdcMessage = new HdcMessage();
					hdcMessage.setDkcRaidListIndexSerialNumber(dkcRaidListIndexSerialNumber);
					hdcMessage.setDkcHWProcessor(dkcHWProcessor);
					hdcMessage.setDkcHWCache(dkcHWCache);
					hdcMessage.setDkcHWSM(dkcHWSM);
					hdcMessage.setDkcHWPS(dkcHWPS);
					hdcMessage.setDkcHWBattery(dkcHWBattery);
					hdcMessage.setDkcHWFan(dkcHWFan);
					hdcMessage.setDkcHWEnvironment(dkcHWEnvironment);
					hdcMessage.setDkcHWCSW(dkcHWCSW);
					runState.addElement(hdcMessage);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		returnHash.put("runState", runState);
		// 把采集结果生成sql
		this.CreateResultTosql(returnHash, node);
		return returnHash;
	}
}