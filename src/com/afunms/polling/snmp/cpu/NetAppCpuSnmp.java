package com.afunms.polling.snmp.cpu;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.application.dao.NetAppDataOperator;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.common.util.SysLogger;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.CPUcollectdata;

// NetApp Cpu利用率检测类
public class NetAppCpuSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicators) {
		Hashtable returnHash = new Hashtable();
		Vector cpuVector = new Vector();
		List cpuList = new ArrayList();
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(nodeGatherIndicators.getNodeid()));
		if (node == null)
			return returnHash;
		try {
			CPUcollectdata cpudata = null;
			Calendar date = Calendar.getInstance();
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				com.afunms.polling.base.Node snmpnode = (com.afunms.polling.base.Node) PollingEngine.getInstance().getNodeByIP(node.getIpAddress());
				Date cc = date.getTime();
				String time = sdf.format(cc);
				snmpnode.setLastTime(time);
			} catch (Exception e) {

			}
			int result = 0;
			String temp = "0";
			try {
				String[] oids = new String[] { ".1.3.6.1.4.1.789.1.2.1.3.0" // cpuBusyTime
				};
				String[][] valueArray = null;
				valueArray = SnmpUtils.getList(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), 3, 1000 * 30);
				int allvalue = 0;
				int flag = 0;
				if (valueArray != null) {
					for (int i = 0; i < valueArray.length; i++) {
						String _value = valueArray[i][0];
						allvalue = allvalue + Integer.parseInt(_value);
						flag = flag + 1;
						List alist = new ArrayList();
						alist.add(_value);
						cpuList.add(alist);
					}
				}

				if (flag > 0) {
					int intvalue = (allvalue / flag);
					temp = intvalue + "";
				}

				if (temp == null) {
					result = 0;
				} else {
					try {
						if (temp.equalsIgnoreCase("noSuchObject")) {
							result = 0;
						} else
							result = Integer.parseInt(temp);
					} catch (Exception ex) {
						ex.printStackTrace();
						result = 0;
					}
				}
				cpudata = new CPUcollectdata();
				cpudata.setIpaddress(node.getIpAddress());
				cpudata.setCollecttime(date);
				cpudata.setCategory("NetApp");
				cpudata.setEntity("Cpu");
				cpudata.setSubentity("Utilization");
				cpudata.setChname("Cpu利用率");
				cpudata.setRestype("dynamic");
				cpudata.setUnit("%");
				cpudata.setThevalue(result + "");
				cpuVector.addElement(cpudata);
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
			if (cpuVector != null && cpuVector.size() > 0)
				ipAllData.put("cpu", cpuVector);
			if (cpuList != null && cpuList.size() > 0)
				ipAllData.put("cpulist", cpuList);
			ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		} else {
			if (cpuVector != null && cpuVector.size() > 0)
				((Hashtable) ShareData.getSharedata().get(node.getIpAddress())).put("cpu", cpuVector);
			if (cpuList != null && cpuList.size() > 0)
				((Hashtable) ShareData.getSharedata().get(node.getIpAddress())).put("cpulist", cpuList);

		}
		returnHash.put("cpu", cpuVector);

		// 对CPU值进行告警检测
		Hashtable collectHash = new Hashtable();
		collectHash.put("cpu", cpuVector);
		// 告警
		try {
			if (cpuVector != null && cpuVector.size() > 0) {
				for (int i = 0; i < cpuVector.size(); i++) {
					CPUcollectdata cpucollectdata = (CPUcollectdata) cpuVector.get(0);
					if ("Utilization".equals(cpucollectdata.getEntity())) {
						CheckEventUtil checkutil = new CheckEventUtil();
						checkutil.updateData(node, nodeGatherIndicators, cpucollectdata.getThevalue());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			SysLogger.error("NetApp cpu 告警出错", e);
		}
		
		NetAppDataOperator op=new NetAppDataOperator();
		op.CreateResultTosql(returnHash, node.getIpAddress());
		
		return returnHash;
	}
}
