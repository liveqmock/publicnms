package com.afunms.polling.snmp.hdc;

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
import com.afunms.polling.om.Interfacecollectdata;
import com.afunms.topology.model.HostNode;
import com.gatherdb.GathersqlListManager;

public class HdcDfSystemParameter extends SnmpMonitor{


	public HdcDfSystemParameter() {
	}

	public void collectData(Node node, MonitoredItem item) {
	}

	public void collectData(HostNode node) {
	}

	public void CreateResultTosql(Hashtable dataresult, Host node) {
		if (dataresult != null && dataresult.size() > 0) {
			Vector sysInfoVector = null;
			HdcDFMessage hdcVo =null;
			NodeDTO nodeDTO = null;
			String ip = null;
			Interfacecollectdata vo = null;
			Calendar tempCal = null;
			Date cc = null;
			String time = null;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			NodeUtil nodeUtil = new NodeUtil();
			nodeDTO = nodeUtil.creatNodeDTOByNode(node);
			

			String hendsql = "insert into hdc_sysinfo (dfSystemProductName,dfSystemMicroRevsion,dfSystemSerialNumber,dfLUNSerialNumber,dfLUNPortID,dfWWNSerialNumber,dfWWNPortID,dfWWNControlIndex,dfWWNNickName,dfWWNID,dfSwitchSerialNumber,dfSwitchPortID,dfLUNLUN,nodeid) values(";
			String endsql = "')";
			String deleteSql = "delete from brocade_sys_info where nodeid='"
					+ node.getId() + "'";
			sysInfoVector = (Vector) dataresult.get("syslist");
			Vector list = new Vector();
			if (sysInfoVector != null && sysInfoVector.size() > 0) {
				for (int i = 0; i < sysInfoVector.size(); i++) {
					hdcVo = (HdcDFMessage) sysInfoVector.elementAt(i);
					StringBuffer sbuffer = new StringBuffer(150);
					sbuffer.append(hendsql);
					sbuffer.append("'").append(hdcVo.getDfSystemProductName()).append("',");
					sbuffer.append("'").append(hdcVo.getDfSystemMicroRevision()).append("',");
					sbuffer.append("'").append(hdcVo.getDfSystemSerialNumber()).append("',");
					sbuffer.append("'").append(hdcVo.getDfLUNSerialNumber()).append("',");
					sbuffer.append("'").append(hdcVo.getDfLUNPortID()).append("',");
					sbuffer.append("'").append(hdcVo.getDfWWNSerialNumber()).append("',");
					sbuffer.append("'").append(hdcVo.getDfWWNPortID()).append("',");
					sbuffer.append("'").append(hdcVo.getDfWWNControlIndex()).append("',");
					sbuffer.append("'").append(hdcVo.getDfWWNNickname()).append("',");
					sbuffer.append("'").append(hdcVo.getDfWWNID()).append("',");
					sbuffer.append("'").append(hdcVo.getDfSwitchSerialNumber()).append("',");
					sbuffer.append("'").append(hdcVo.getDfSwitchPortID()).append("',");
					sbuffer.append("'").append(hdcVo.getDfLUNLUN()).append("',");
					sbuffer.append("'").append(node.getId());
					sbuffer.append(endsql);
					list.add(sbuffer.toString());
//					System.out.println(sbuffer.toString());
					sbuffer = null;
				}
				GathersqlListManager.AdddateTempsql(deleteSql, list);
				list = null;
			}
		}
	}
	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		SysLogger.info("########开始采集hdc的系统版本信息##########");
		Hashtable returnHash = new Hashtable();
		Vector list = new Vector();
		HdcDFMessage hdcMessage;
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
			
			
			

			String[] oids = new String[] { ".1.3.6.1.4.1.116.5.11.1.2.1.1.0",//产品号
					".1.3.6.1.4.1.116.5.11.1.2.1.2.0",//版本号
					".1.3.6.1.4.1.116.5.11.1.2.1.3.0",//序列号
					".1.3.6.1.4.1.116.5.11.1.2.5.2.1.1",//WWN序列号
					".1.3.6.1.4.1.116.5.11.1.2.5.2.1.2",//WWN端口ID
					".1.3.6.1.4.1.116.5.11.1.2.5.2.1.3",//wwn控制索引
					".1.3.6.1.4.1.116.5.11.1.2.5.2.1.5",//wwn  id
					".1.3.6.1.4.1.116.5.11.1.2.5.2.1.6",//wwn 名称
					".1.3.6.1.4.1.116.5.11.1.2.5.4.1.1",// dfLUNSerialNumber
					".1.3.6.1.4.1.116.5.11.1.2.5.4.1.2",// dfLUNPortID
					".1.3.6.1.4.1.116.5.11.1.2.5.4.1.3",// dfLUNLUN
					".1.3.6.1.4.1.116.5.11.1.2.5.1.1.1",// dfSwitchSerialNumber
					// dkc 索引
					".1.3.6.1.4.1.116.5.11.1.2.5.1.1.2",// dfSwitchPortID
					// 端口id
			};
//			valueArray = SnmpUtils.getTemperatureTableData(node
//														.getIpAddress(), node
//															.getCommunity(), oids, node
//																.getSnmpversion(), 3, 1000 * 30);
			valueArray = SnmpUtils.getTemperatureTableData(node.getIpAddress(),node.getCommunity(),oids,node.getSnmpversion(),
   		  			node.getSecuritylevel(),node.getSecurityName(),node.getV3_ap(),node.getAuthpassphrase(),node.getV3_privacy(),node.getPrivacyPassphrase(),3,1000*30);
			if (valueArray != null) {
				for (int i = 0; i < valueArray.length; i++) {
					String dfSystemProductName = valueArray[i][0];
					String dfSystemMicroRevision = valueArray[i][1];
					String dfSystemSerialNumber = valueArray[i][2];
					String dfWWNSerialNumber = valueArray[i][3];
					String dfWWNPortID = valueArray[i][4];
					String dfWWNControlIndex = valueArray[i][5];
					String dfWWNID = valueArray[i][6];
					String dfWWNNickname = valueArray[i][7];
					String dfLUNSerialNumber = valueArray[i][8];
					String dfLUNPortID = valueArray[i][9];
					String dfLUNLUN = valueArray[i][10];
					String dfSwitchSerialNumber = valueArray[i][11];
					String dfSwitchPortID = valueArray[i][12];

					
					hdcMessage = new HdcDFMessage();
					hdcMessage.setDfSystemProductName(dfSystemProductName);
					hdcMessage.setDfSystemMicroRevision(dfSystemMicroRevision);
					hdcMessage.setDfSystemSerialNumber(dfSystemSerialNumber);
					hdcMessage.setDfWWNSerialNumber(dfWWNSerialNumber);
					hdcMessage.setDfWWNPortID(dfWWNPortID);
					hdcMessage.setDfWWNControlIndex(dfWWNControlIndex);
					hdcMessage.setDfWWNID(dfWWNID);
					hdcMessage.setDfWWNNickname(dfWWNNickname);
					hdcMessage.setDfLUNSerialNumber(dfLUNSerialNumber);
					hdcMessage.setDfLUNPortID(dfLUNPortID);
					hdcMessage.setDfLUNLUN(dfLUNLUN);
					hdcMessage.setDfSwitchSerialNumber(dfSwitchSerialNumber);
					hdcMessage.setDfSwitchPortID(dfSwitchPortID);
					
//					hdcMessage.setDfWWNID("5");
//					hdcMessage.setDfWWNNickname("123456788");
//					hdcMessage.setDfLUNSerialNumber("1");
//					hdcMessage.setDfLUNPortID("2");
//					hdcMessage.setDfLUNLUN("3");
//					hdcMessage.setDfWWNSerialNumber("1");
//					hdcMessage.setDfWWNPortID("2");
//					hdcMessage.setDfWWNControlIndex("3");
//					hdcMessage.setDfSwitchSerialNumber("1");
//					hdcMessage.setDfSwitchPortID("2");
//					hdcMessage.setDfSystemProductName("日历QB123");
//					hdcMessage.setDfSystemMicroRevision("呵呵");
//					hdcMessage.setDfSystemSerialNumber("哈哈");
					list.addElement(hdcMessage);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(!(ShareData.getSharedata().containsKey(node.getIpAddress())))
		{
			Hashtable ipAllData = new Hashtable();
			if(ipAllData == null)ipAllData = new Hashtable();
			if(list != null && list.size()>0)ipAllData.put("syslist",list);
		    ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		}else
		 {
			if(list != null && list.size()>0)((Hashtable)ShareData.getSharedata().get(node.getIpAddress())).put("syslist",list);
		 }
		SysLogger.info("########完成hdc的系统版本信息采集##########");
		returnHash.put("syslist", list);
		this.CreateResultTosql(returnHash, node);
		return returnHash;
	}

}
