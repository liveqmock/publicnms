package com.afunms.polling.snmp.hillstone;

import java.util.Hashtable;

import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Host;

public class HillstoneBasedOnSourceIPSnmp extends SnmpMonitor
{
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public HillstoneBasedOnSourceIPSnmp(){}
	public void collectData(Node node,MonitoredItem item){}
	public static Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode)
	{
		Hashtable returnHash=new Hashtable();
		Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if(node == null)return null;
		
		String[] oids = new String[]{".1.3.6.1.4.1.28557.2.3.5.1.1.1",".1.3.6.1.4.1.28557.2.3.5.1.1.2"};
		try{
			String[][] theValue = snmp.getCpuTableData(node.getIpAddress(),node.getCommunity(),oids);
		}catch(Exception e){e.printStackTrace();}
		return returnHash;
	}
	public static Hashtable collect_Data() 
	{
		Hashtable returnHash=new Hashtable();
		String[][] theValue = null;
		String[] oids = new String[]{".1.3.6.1.4.1.28557.2.3.5.1.1.1",".1.3.6.1.4.1.28557.2.3.5.1.1.2"};//ÁÐµÄoid
		try{
			theValue = snmp.getTableData("192.168.10.74", "hkbank", oids);
			System.out.println(theValue);
		}catch(Exception e){e.printStackTrace();}
		if(theValue != null)
		{
			for(int i = 0; i < theValue.length;i++)
			{
				String s = theValue[i][0];
				
			}
		}
		return returnHash;
	}
}