package com.afunms.polling.snmp.hillstone;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;

import com.afunms.common.util.ShareData;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.Memorycollectdata;

public class HillstoneMemoryUtilizationSnmp extends SnmpMonitor
{
	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) 
	{
		Hashtable returnHash=new Hashtable();
		Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if(node == null)return null;
		String oids = new String(".1.3.6.1.4.1.28557.2.2.5.0");
		String oids_total = new String(".1.3.6.1.4.1.28557.2.2.4.0");
		String oids_ = new String(".1.3.6.1.4.1.28557.2.2.1.5.0");
		String oids_total_ = new String(".1.3.6.1.4.1.28557.2.2.1.4.0");
		String theValue = snmp.getMibValue(node.getIpAddress(),node.getCommunity(),oids);
		String totalValue = snmp.getMibValue(node.getIpAddress(), node.getCommunity(), oids_total);
		if(theValue == null || theValue.equals("noSuchObject"))
			theValue = snmp.getMibValue(node.getIpAddress(),node.getCommunity(),oids_);
		if(totalValue == null || totalValue.equals("noSuchObject")) 
			totalValue = snmp.getMibValue(node.getIpAddress(), node.getCommunity(), oids_total_);
		if(theValue == null || theValue.equals("noSuchObject"))
			theValue = "0";
		if(totalValue == null || totalValue.equals("noSuchObject")) 
			totalValue = "0";
		Float utilization = 0f;
		if(!totalValue.equals("0")&&!totalValue.equals("noSuchObject")&&!theValue.equals("noSuchObject")){
			utilization = Float.parseFloat(theValue)/Float.parseFloat(totalValue);
		}
		utilization = utilization*100;
		DecimalFormat df=new DecimalFormat("#.##");
		df.format(utilization);
		Calendar date=Calendar.getInstance();
		Memorycollectdata collectData = new Memorycollectdata();
		collectData.setIpaddress(node.getIpAddress());
		collectData.setCollecttime(date);
		collectData.setRestype("dynamic");
		collectData.setEntity("Utilization");
		collectData.setCategory("Memory");
		collectData.setSubentity("Utilization");
		collectData.setUnit("%");
		collectData.setThevalue(df.format(utilization));
		
		Memorycollectdata curCollectData = new Memorycollectdata();
		curCollectData.setIpaddress(node.getIpAddress());
		curCollectData.setCollecttime(date);
		curCollectData.setRestype("static");
		curCollectData.setEntity("sysCurMemory");
		curCollectData.setCategory("Memory");
		curCollectData.setSubentity("sysCurMemory");
		curCollectData.setUnit("KB");
		curCollectData.setThevalue(theValue);
		
		Memorycollectdata totalCollectData = new Memorycollectdata();
		totalCollectData.setIpaddress(node.getIpAddress());
		totalCollectData.setCollecttime(date);
		totalCollectData.setRestype("static");
		totalCollectData.setEntity("PhysicalMemory");
		totalCollectData.setCategory("Memory");
		totalCollectData.setSubentity("PhysicalMemory");
		totalCollectData.setUnit("KB");
		totalCollectData.setThevalue(totalValue);
		Vector v = new Vector();
		v.add(curCollectData);
		v.add(totalCollectData);
		v.add(collectData);
		Hashtable ipAllData = new Hashtable();
		try{
			ipAllData = (Hashtable)ShareData.getSharedata().get(node.getIpAddress());
		}catch(Exception e){
			
		}
		if(ipAllData == null)ipAllData = new Hashtable();
		ipAllData.put("memory",v);
		ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		returnHash.put("memory", v);
		return returnHash;
	}
}
