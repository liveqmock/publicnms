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
import com.afunms.polling.om.ArrayVPNWeb;
import com.afunms.topology.model.HostNode;


/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ArrayVPNWebSnmp extends SnmpMonitor {
	private Hashtable sendeddata = ShareData.getProcsendeddata();
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 
	 */
	public ArrayVPNWebSnmp() {
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
			   					"1.3.6.1.4.1.7564.33.1.2.1.2",
			   					"1.3.6.1.4.1.7564.33.1.2.1.3",
			   					"1.3.6.1.4.1.7564.33.1.2.1.4",
			   					"1.3.6.1.4.1.7564.33.1.2.1.5",
			   					"1.3.6.1.4.1.7564.33.1.2.1.6"
			   			}; 
			   			valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), 3, 1000);
			   			int flag = 0;
						if(valueArray != null){
						   	  for(int i=0;i<valueArray.length;i++)
						   	  {   
						   		ArrayVPNWeb arrayVPNWeb = new ArrayVPNWeb();
						   		String webId  = valueArray[i][0];
						   		String webAuthorizedReq = valueArray[i][1];
						   		String webUnauthorizedReq = valueArray[i][2];
						   		String webBytesIn = valueArray[i][3];
						   		String webBytesOut = valueArray[i][4];
						   		
						   		arrayVPNWeb.setWebAuthorizedReq(Integer.parseInt(webAuthorizedReq));
						   		arrayVPNWeb.setWebId(webId);
						   		arrayVPNWeb.setWebBytesIn(Long.parseLong(webBytesIn));
						   		arrayVPNWeb.setWebBytesOut(Long.parseLong(webBytesOut));
						   		arrayVPNWeb.setWebUnauthorizedReq(Integer.parseInt(webUnauthorizedReq));
							   	arrayVPNWeb.setIpaddress(node.getIpAddress());
							   	arrayVPNWeb.setCollecttime(date);
							   	arrayVPNWeb.setType("VPN");
							   	arrayVPNWeb.setSubtype("ArrayNetworks");
							   
							   	SysLogger.info("webId:"+webId+"   webAuthorizedReq:"+webAuthorizedReq
							   			+"  webUnauthorizedReq :"+webUnauthorizedReq+"  webUnauthorizedReq:"+webUnauthorizedReq
							   			);  
								powerVector.addElement(arrayVPNWeb);	
						}
						}
			   	  }
			   	  catch(Exception e)
			   	  {
			   	  }	   	  
			   	  //-------------------------------------------------------------------------------------------电源 end
				}catch(Exception e){
				}
			
			Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(node.getIpAddress());
			if(ipAllData == null)ipAllData = new Hashtable();
			ipAllData.put("VPNWeb",powerVector);
		    ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		    returnHash.put("VPNWeb", powerVector);
		    return returnHash;
		}

	
}


