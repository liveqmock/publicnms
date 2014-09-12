package com.afunms.polling.snmp.hillstone;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;

import com.afunms.common.util.ShareData;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.CPUcollectdata;

public class HillstoneSysCPUSnmp extends SnmpMonitor
{
	public static Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) 
	{
		Hashtable returnHash=new Hashtable();
		Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if(node == null)return null;
		String oids = new String(".1.3.6.1.4.1.28557.2.2.3.0");
		String oids_ = new String(".1.3.6.1.4.1.28557.2.2.1.3.0");
		String theValue = snmp.getMibValue(node.getIpAddress(),node.getCommunity(),oids);
		if(theValue == null || theValue.equals("noSuchObject"))
			theValue = snmp.getMibValue(node.getIpAddress(),node.getCommunity(),oids_);
		if(theValue == null || theValue.equals("noSuchObject"))
			theValue = "0";
		System.out.println(theValue);
		Calendar date=Calendar.getInstance();
		CPUcollectdata collectData = new CPUcollectdata();
		collectData.setIpaddress(node.getIpAddress());
		collectData.setCollecttime(date);
		collectData.setRestype("dynamic");
		collectData.setCategory("CPU");
		collectData.setUnit("%");
		collectData.setEntity("Utilization");
		collectData.setSubentity("avg");
		collectData.setThevalue(theValue);
		Vector v = new Vector();
		v.add(collectData);
		Hashtable ipAllData = new Hashtable();
		try{
			ipAllData = (Hashtable)ShareData.getSharedata().get(node.getIpAddress());
		}catch(Exception e){
			
		}
		if(ipAllData == null)ipAllData = new Hashtable();
		ipAllData.put("cpu",v);
		ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		returnHash.put("cpu", v);
		return returnHash;
	}
	public static Hashtable collect_Data() 
	{
		Hashtable returnHash=new Hashtable();
		//Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		String oids = new String(".1.3.6.1.4.1.28557.2.2.3.0");
		String theValue = snmp.getMibValue("192.168.10.74","hkbank",oids);
		System.out.println(theValue);
		return returnHash;
	}
}
