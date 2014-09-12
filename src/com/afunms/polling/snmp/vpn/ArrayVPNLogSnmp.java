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
import com.afunms.polling.om.ArrayVPNLog;
import com.afunms.topology.model.HostNode;


public class ArrayVPNLogSnmp extends SnmpMonitor {
	private Hashtable sendeddata = ShareData.getProcsendeddata();
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 
	 */
	public ArrayVPNLogSnmp() {
	}

	   public void collectData(Node node,MonitoredItem item){
		   
	   }
	   public void collectData(HostNode node){
		   
	   }
	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode){ 
			Hashtable returnHash=new Hashtable();
			Vector powerVector=new Vector();
			Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
			if(node == null)return null;
			try {
				
				Calendar date = Calendar.getInstance();
				Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(node.getIpAddress());
				if(ipAllData == null)ipAllData = new Hashtable();
				
				  try {
			   		  String temp = "0"; 
			   			String[][] valueArray = null;
			   			String[] oids =                
							  new String[] {
			   					"1.3.6.1.4.1.7564.24.1.1",
			   					"1.3.6.1.4.1.7564.24.1.2",
			   					"1.3.6.1.4.1.7564.24.1.3",
			   					"1.3.6.1.4.1.7564.24.2.1" 
			   			}; 
			   			valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), 3, 1000);
			   			int flag = 0;
						if(valueArray != null){
						   	  for(int i=0;i<valueArray.length;i++)
						   	  {   
						   		ArrayVPNLog arrayVPNLog = new ArrayVPNLog();
						   		String logNotificationsSent  = valueArray[i][0];
						   		String logNotificationsEnabled = valueArray[i][1]; 
						   		String logMaxSeverity = valueArray[i][2];
						   		String logHistTableMaxLength = valueArray[i][3];
						   		arrayVPNLog.setLogHistTableMaxLength(Integer.parseInt(logHistTableMaxLength));
						   		arrayVPNLog.setLogMaxSeverity(Integer.parseInt(logMaxSeverity));
						   		arrayVPNLog.setLogNotificationsEnabled(Integer.parseInt(logNotificationsEnabled));
						   		arrayVPNLog.setLogNotificationsSent(Integer.parseInt(logNotificationsSent));
							   	arrayVPNLog.setIpaddress(node.getIpAddress());
							   	arrayVPNLog.setCollecttime(date);
							   	arrayVPNLog.setType("VPN");
							   	arrayVPNLog.setSubtype("ArrayNetworks");
							   
							   	SysLogger.info("logNotificationsSent:"+logNotificationsSent+"   logNotificationsEnabled:"+logNotificationsEnabled
							   			+"  logMaxSeverity :"+logMaxSeverity+"  logHistTableMaxLength:"+logHistTableMaxLength
							   			);  
								powerVector.addElement(arrayVPNLog);	
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
			ipAllData.put("VPNLog",powerVector);
		    ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		    returnHash.put("VPNLog", powerVector);
		    return returnHash;
		}
	
}


