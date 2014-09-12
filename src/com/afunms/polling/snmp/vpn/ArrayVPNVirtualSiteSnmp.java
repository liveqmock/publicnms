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
import com.afunms.polling.om.ArrayVPNSSL;
import com.afunms.polling.om.ArrayVPNVirtualSite;
import com.afunms.topology.model.HostNode;


/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ArrayVPNVirtualSiteSnmp extends SnmpMonitor {
	private Hashtable sendeddata = ShareData.getProcsendeddata();
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 
	 */
	public ArrayVPNVirtualSiteSnmp() {
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
			   		  String temp = "0";
			   			String[][] valueArray = null;
			   			String[] oids =                
							  new String[] {               
			   					"1.3.6.1.4.1.7564.31.1.2.1.2",
			   					"1.3.6.1.4.1.7564.31.1.2.1.3",
			   					"1.3.6.1.4.1.7564.31.1.2.1.4",
			   					"1.3.6.1.4.1.7564.31.1.2.1.5",
			   					"1.3.6.1.4.1.7564.31.1.2.1.6",
			   					"1.3.6.1.4.1.7564.31.1.2.1.7",
			   					"1.3.6.1.4.1.7564.31.1.2.1.8",
			   					"1.3.6.1.4.1.7564.31.1.2.1.9",
			   					"1.3.6.1.4.1.7564.31.1.2.1.10",
			   					"1.3.6.1.4.1.7564.31.1.2.1.11",
			   					"1.3.6.1.4.1.7564.31.1.2.1.12",
			   					"1.3.6.1.4.1.7564.31.1.2.1.13",
			   					"1.3.6.1.4.1.7564.31.1.2.1.14",
			   					"1.3.6.1.4.1.7564.31.1.2.1.15",
			   					"1.3.6.1.4.1.7564.31.1.2.1.16"
			   			}; 
			   			valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), 3, 1000);
			   			if(valueArray != null){
						   	  for(int i=0;i<valueArray.length;i++)
						   	  {   
						   		ArrayVPNVirtualSite arrayVPNVirtualSite = new ArrayVPNVirtualSite();
						   		 String virtualSiteId = valueArray[i][0];
						   		 String virtualSiteActiveSessions = valueArray[i][1];
						   		 String virtualSiteSuccessLogin = valueArray[i][2];
						   		 String virtualSiteFailureLogin = valueArray[i][3];
						   		 String virtualSiteErrorLogin = valueArray[i][4];
						   		 String virtualSiteSuccessLogout = valueArray[i][5];
						   		 String virtualSiteBytesIn = valueArray[i][6];
						   		 String virtualSiteBytesOut = valueArray[i][7];
						   		 String virtualSiteMaxActiveSessions = valueArray[i][8];
						   		 String virtualSiteFileAuthorizedRequests = valueArray[i][9];
						   		 String virtualSiteFileUnauthorizedRequests = valueArray[i][10];
						   		 String virtualSiteFileBytesIn = valueArray[i][11];
						   		 String virtualSiteFileBytesOut = valueArray[i][12];
						   		 String virtualSiteLockedLogin = valueArray[i][13]; 
						   		 String virtualSiteRejectedLogin = valueArray[i][14];
						   		arrayVPNVirtualSite.setVirtualSiteActiveSessions(Integer.parseInt(virtualSiteActiveSessions));
						   		arrayVPNVirtualSite.setVirtualSiteId(virtualSiteId);
						   		arrayVPNVirtualSite.setVirtualSiteErrorLogin(Integer.parseInt(virtualSiteErrorLogin));
						   		arrayVPNVirtualSite.setVirtualSiteFailureLogin(Integer.parseInt(virtualSiteFailureLogin));
						   		arrayVPNVirtualSite.setVirtualSiteBytesIn(Long.parseLong(virtualSiteBytesIn));
						   		arrayVPNVirtualSite.setVirtualSiteBytesOut(Long.parseLong(virtualSiteBytesOut));
						   		arrayVPNVirtualSite.setVirtualSiteFileAuthorizedRequests(Integer.parseInt(virtualSiteFileAuthorizedRequests));
						   		arrayVPNVirtualSite.setVirtualSiteFileBytesIn(Integer.parseInt(virtualSiteFileBytesIn));
						   		arrayVPNVirtualSite.setVirtualSiteFileBytesOut(Integer.parseInt(virtualSiteFileBytesOut));
						   		arrayVPNVirtualSite.setVirtualSiteFileUnauthorizedRequests(Integer.parseInt(virtualSiteFileUnauthorizedRequests));
						   		arrayVPNVirtualSite.setVirtualSiteLockedLogin(Integer.parseInt(virtualSiteLockedLogin));
						   		arrayVPNVirtualSite.setVirtualSiteMaxActiveSessions(Integer.parseInt(virtualSiteMaxActiveSessions));
						   		arrayVPNVirtualSite.setVirtualSiteRejectedLogin(Integer.parseInt(virtualSiteRejectedLogin));
						   		arrayVPNVirtualSite.setVirtualSiteSuccessLogin(Integer.parseInt(virtualSiteSuccessLogin));
						   		arrayVPNVirtualSite.setVirtualSiteSuccessLogout(Integer.parseInt(virtualSiteSuccessLogin));
							   	arrayVPNVirtualSite.setIpaddress(node.getIpAddress());
							   	arrayVPNVirtualSite.setCollecttime(date);
							   	arrayVPNVirtualSite.setType("VPN");
							   	arrayVPNVirtualSite.setSubtype("ArrayNetworks");
							   	SysLogger.info("virtualSiteId:"+virtualSiteId+"   virtualSiteActiveSessions:"+virtualSiteActiveSessions
							   			+"  virtualSiteFileAuthorizedRequests :"+virtualSiteFileAuthorizedRequests+"  virtualSiteFileBytesOut:"+virtualSiteFileBytesOut
							   		);  
								powerVector.addElement(arrayVPNVirtualSite);	
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
			ipAllData.put("VirtualSite",powerVector);
		    ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		    returnHash.put("VirtualSite", powerVector);
		    return returnHash;
		}

	
//	public static void main(String[] args){
//		ArrayVPNVirtualSiteSnmp arrayVPNClusterSnmp = new ArrayVPNVirtualSiteSnmp();
//		Host node = new Host();
//		node.setIpAddress("10.204.3.254");
//		node.setCommunity("oavpn-1");
//		node.setSnmpversion(1);
//		Hashtable returnHash = arrayVPNClusterSnmp.collect_Data(node);
//		System.out.println(returnHash);
//	}
}


