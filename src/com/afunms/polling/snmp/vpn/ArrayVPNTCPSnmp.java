package com.afunms.polling.snmp.vpn;

/*
 * author ChengFeng
 *
 */

import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.common.util.SysLogger;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.ArrayVPNTCP;
import com.afunms.topology.model.HostNode;


/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ArrayVPNTCPSnmp extends SnmpMonitor {
	private Hashtable sendeddata = ShareData.getProcsendeddata();
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 
	 */
	public ArrayVPNTCPSnmp() {
	}

	   public void collectData(Node node,MonitoredItem item){
		   
	   }
	   public void collectData(HostNode node){
		   
	   }
	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode){//要改为AlarmIndicatorsNode alarmIndicatorsNode
			Hashtable returnHash=new Hashtable();
			Vector powerVector=new Vector();
			Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
			if(node == null)return null;
			try {
				
				Calendar date = Calendar.getInstance();
				Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(node.getIpAddress());
				if(ipAllData == null)ipAllData = new Hashtable();
				
				  try {
					  //-------------------------------------------------------------------------------------------电源 start
			   		  String temp = "0";
			   			String[][] valueArray = null;
			   			String[] oids =                
							  new String[] {               
			   					"1.3.6.1.4.1.7564.25.1",
			   					"1.3.6.1.4.1.7564.25.2",
			   					"1.3.6.1.4.1.7564.25.3",
			   					"1.3.6.1.4.1.7564.25.4",
			   					"1.3.6.1.4.1.7564.25.5",
			   					"1.3.6.1.4.1.7564.25.6",
			   					"1.3.6.1.4.1.7564.25.7",
			   					"1.3.6.1.4.1.7564.25.8",
			   					"1.3.6.1.4.1.7564.25.9",
			   					"1.3.6.1.4.1.7564.25.10"
			   			}; 
			   			valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), 3, 1000);
			   			int flag = 0;
						if(valueArray != null){
						   	  for(int i=0;i<valueArray.length;i++)
						   	  {   
						   		ArrayVPNTCP arrayVPNTCP = new ArrayVPNTCP();
						   		String ctcpActiveOpens = valueArray[i][0];
						   		String ctcpPassiveOpens = valueArray[i][1];
						   		String ctcpAttemptFails = valueArray[i][2];
						   		String ctcpEstabResets = valueArray[i][3];
						   		String ctcpCurrEstab = valueArray[i][4];
							   	String ctcpInSegs = valueArray[i][5];
							   	String ctcpOutSegs = valueArray[i][6];
							   	String ctcpRetransSegs = valueArray[i][7]; 
							   	String ctcpInErrs = valueArray[i][8]; 
							   	String ctcpOutRsts = valueArray[i][9];  
							   	arrayVPNTCP.setCtcpActiveOpens(Integer.parseInt(ctcpActiveOpens));
							   	arrayVPNTCP.setCtcpAttemptFails(Integer.parseInt(ctcpAttemptFails));
							   	arrayVPNTCP.setCtcpCurrEstab(Integer.parseInt(ctcpCurrEstab));
							   	arrayVPNTCP.setCtcpEstabResets(Long.parseLong(ctcpEstabResets));
							   	arrayVPNTCP.setCtcpInErrs(Integer.parseInt(ctcpInErrs));
							   	arrayVPNTCP.setCtcpInSegs(Long.parseLong(ctcpInSegs));
							   	arrayVPNTCP.setCtcpOutRsts(Long.parseLong(ctcpOutRsts));
							   	arrayVPNTCP.setCtcpOutSegs(Long.parseLong(ctcpOutSegs));
							   	arrayVPNTCP.setCtcpPassiveOpens(Long.parseLong(ctcpPassiveOpens));
							   	arrayVPNTCP.setCtcpRetransSegs(Long.parseLong(ctcpRetransSegs));
							   	arrayVPNTCP.setIpaddress(node.getIpAddress());
							   	arrayVPNTCP.setCollecttime(date);
							   	arrayVPNTCP.setType("VPN");
							   	arrayVPNTCP.setSubtype("ArrayNetworks");
							   
							   	SysLogger.info("ctcpActiveOpens:"+ctcpActiveOpens+"   ctcpPassiveOpens:"+ctcpPassiveOpens
							   			+"  ctcpAttemptFails :"+ctcpAttemptFails+"  ctcpEstabResets:"+ctcpEstabResets
							   			+"  ctcpRetransSegs:"+ctcpRetransSegs+" ctcpInSegs:"+ctcpInSegs
							   			+"  ctcpCurrEstab:"+ctcpCurrEstab+"  ctcpInSegs:"+ctcpInSegs);  
								powerVector.addElement(arrayVPNTCP);	
						}
						}
			   	  }
			   	  catch(Exception e)
			   	  {
			   	  }	   	  
				}catch(Exception e){
				}
			
			Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(node.getIpAddress());
			if(ipAllData == null)ipAllData = new Hashtable();
			ipAllData.put("TCP",powerVector);
		    ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		    returnHash.put("TCP", powerVector);
		    return returnHash;
		}

	
}


