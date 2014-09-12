package com.afunms.polling.snmp.hillstone;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;

import com.afunms.common.util.ShareData;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.HillstoneCommonCollectData;

public class HillstoneBaseInfoSnmp extends SnmpMonitor
{
	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash=new Hashtable();
		Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
//		System.out.println("开始采集hillstone数据==="+alarmIndicatorsNode.getNodeid());
		if(node == null)return null;
		String[] oids = {
							".1.3.6.1.4.1.28557.2.2.7.0",//sysCurSession
							".1.3.6.1.4.1.28557.2.2.6.0",//sysTotalSession
							".1.3.6.1.4.1.28557.2.2.8.0",//HAStatus
							".1.3.6.1.4.1.28557.2.2.1.0",//sysSerialNumber
							".1.3.6.1.4.1.28557.2.2.2.0"//sysSoftware
						};
		String[] oids_ = {
				".1.3.6.1.4.1.28557.2.2.1.7.0",//sysCurSession
				".1.3.6.1.4.1.28557.2.2.1.6.0",//sysTotalSession
				".1.3.6.1.4.1.28557.2.2.1.8.0",//HAStatus
				".1.3.6.1.4.1.28557.2.2.1.1.0",//sysSerialNumber
				".1.3.6.1.4.1.28557.2.2.1.2.0"//sysSoftware
			};
		String sysCurSessionValue = snmp.getMibValue(node.getIpAddress(),node.getCommunity(),oids[0]);
		String sysTotalSessionValue = snmp.getMibValue(node.getIpAddress(),node.getCommunity(),oids[1]);
		String HAStatusValue = snmp.getMibValue(node.getIpAddress(),node.getCommunity(),oids[2]);
		String sysSerialNumberValue = snmp.getMibValue(node.getIpAddress(),node.getCommunity(),oids[3]);
		String sysSoftwareValue = snmp.getMibValue(node.getIpAddress(),node.getCommunity(),oids[4]);
		if(sysCurSessionValue == null || sysCurSessionValue.equals("noSuchObject"))
			sysCurSessionValue =  snmp.getMibValue(node.getIpAddress(),node.getCommunity(),oids_[0]);
		if(sysTotalSessionValue == null || sysTotalSessionValue.equals("noSuchObject"))
			sysTotalSessionValue = snmp.getMibValue(node.getIpAddress(),node.getCommunity(),oids_[1]);
		if(HAStatusValue == null || HAStatusValue.equals("noSuchObject"))
			HAStatusValue = snmp.getMibValue(node.getIpAddress(),node.getCommunity(),oids_[2]);
		if(sysSerialNumberValue == null || sysSerialNumberValue.equals("noSuchObject"))
			sysSerialNumberValue = snmp.getMibValue(node.getIpAddress(),node.getCommunity(),oids_[3]);
		if(sysSoftwareValue == null || sysSoftwareValue.equals("noSuchObject"))
			sysSoftwareValue = snmp.getMibValue(node.getIpAddress(),node.getCommunity(),oids_[4]);
		Calendar date=Calendar.getInstance();
		HillstoneCommonCollectData sysCurSessionCollectData = new HillstoneCommonCollectData();
		sysCurSessionCollectData.setIpaddress(node.getIpAddress());
		sysCurSessionCollectData.setCollecttime(date);
		sysCurSessionCollectData.setCategory("System");
		sysCurSessionCollectData.setRestype("static");
		sysCurSessionCollectData.setEntity("baseInfo");
		sysCurSessionCollectData.setSubentity("sysCurSession");
		sysCurSessionCollectData.setUnit("个");
		sysCurSessionCollectData.setThevalue(sysCurSessionValue);
		
		HillstoneCommonCollectData sysTotalSessionCollectData = new HillstoneCommonCollectData();
		sysTotalSessionCollectData.setIpaddress(node.getIpAddress());
		sysTotalSessionCollectData.setCollecttime(date);
		sysCurSessionCollectData.setCategory("System");
		sysTotalSessionCollectData.setRestype("static");
		sysTotalSessionCollectData.setEntity("baseInfo");
		sysTotalSessionCollectData.setSubentity("sysTotalSession");
		sysTotalSessionCollectData.setUnit("个");
		sysTotalSessionCollectData.setThevalue(sysTotalSessionValue);
		
		HillstoneCommonCollectData HAStatusValueCollectData = new HillstoneCommonCollectData();
		HAStatusValueCollectData.setIpaddress(node.getIpAddress());
		HAStatusValueCollectData.setCollecttime(date);
		sysCurSessionCollectData.setCategory("System");
		HAStatusValueCollectData.setRestype("static");
		HAStatusValueCollectData.setEntity("baseInfo");
		HAStatusValueCollectData.setSubentity("HAStatusValue");
		HAStatusValueCollectData.setUnit("");
		HAStatusValueCollectData.setThevalue(HAStatusValue);
		
		HillstoneCommonCollectData sysSerialNumberCollectData = new HillstoneCommonCollectData();
		sysSerialNumberCollectData.setIpaddress(node.getIpAddress());
		sysSerialNumberCollectData.setCollecttime(date);
		sysCurSessionCollectData.setCategory("System");
		sysSerialNumberCollectData.setRestype("static");
		sysSerialNumberCollectData.setEntity("baseInfo");
		sysSerialNumberCollectData.setSubentity("sysSerialNumber");
		sysSerialNumberCollectData.setUnit("");
		sysSerialNumberCollectData.setThevalue(sysSerialNumberValue);
		
		HillstoneCommonCollectData sysSoftwareCollectData = new HillstoneCommonCollectData();
		sysSoftwareCollectData.setIpaddress(node.getIpAddress());
		sysSoftwareCollectData.setCollecttime(date);
		sysCurSessionCollectData.setCategory("System");
		sysSoftwareCollectData.setRestype("static");
		sysSoftwareCollectData.setEntity("baseInfo");
		sysSoftwareCollectData.setSubentity("sysSoftware");
		sysSoftwareCollectData.setUnit("");
		sysSoftwareCollectData.setThevalue(sysSoftwareValue);
		
		Hashtable ipAllData = new Hashtable();
		try{
			ipAllData = (Hashtable)ShareData.getSharedata().get(node.getIpAddress());
		}catch(Exception e){
			e.printStackTrace();
		}
		if(ipAllData == null)ipAllData = new Hashtable();
		
		Vector<HillstoneCommonCollectData> v = new Vector<HillstoneCommonCollectData>();
		v.add(sysCurSessionCollectData);//sysCurSession
		v.add(sysTotalSessionCollectData);//sysTotalSession
		v.add(HAStatusValueCollectData);//HAStatus
		v.add(sysSerialNumberCollectData);//sysSerialNumber
		v.add(sysSoftwareCollectData);//sysSoftware
//		System.out.println("结束采集hillstone数据==="+v.size());
		ipAllData.put("baseInfo",v);
		ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		returnHash.put("baseInfo", v);
		return returnHash;
	}
}
