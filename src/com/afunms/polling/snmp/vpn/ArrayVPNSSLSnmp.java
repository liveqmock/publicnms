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
import com.afunms.topology.model.HostNode;


/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ArrayVPNSSLSnmp extends SnmpMonitor {
	private Hashtable sendeddata = ShareData.getProcsendeddata();
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 
	 */
	public ArrayVPNSSLSnmp() {
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
			   					"1.3.6.1.4.1.7564.20.2.4.1.1",
			   					"1.3.6.1.4.1.7564.20.2.4.1.2",
			   					"1.3.6.1.4.1.7564.20.2.4.1.3",
			   					"1.3.6.1.4.1.7564.20.2.4.1.4",
			   					"1.3.6.1.4.1.7564.20.2.4.1.5",
			   					"1.3.6.1.4.1.7564.20.2.4.1.6",
			   					"1.3.6.1.4.1.7564.20.2.4.1.7",
			   					"1.3.6.1.4.1.7564.20.2.4.1.8"
			   			}; 
			   			valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), 3, 1000);
			   			int flag = 0;
						if(valueArray != null){
						   	  for(int i=0;i<valueArray.length;i++)
						   	  {   
						   		ArrayVPNSSL arrayVPNSSL = new ArrayVPNSSL();
						   		String sslIndex = valueArray[i][0];
						   		String vhostName = valueArray[i][1];
						   		String openSSLConns = valueArray[i][2];
						   		String acceptedConns = valueArray[i][3];
						   		String requestedConns = valueArray[i][4];
							   	String resumedSess = valueArray[i][5];
							   	String resumableSess = valueArray[i][6];
							   	String missSess = valueArray[i][7]; 
							   	arrayVPNSSL.setSslIndex(Integer.parseInt(sslIndex));
							   	arrayVPNSSL.setVhostName(vhostName);
							   	arrayVPNSSL.setOpenSSLConns(Integer.parseInt(openSSLConns));
							   	arrayVPNSSL.setAcceptedConns(Integer.parseInt(acceptedConns));
							   	arrayVPNSSL.setRequestedConns(Integer.parseInt(requestedConns));
							   	arrayVPNSSL.setResumedSess(Integer.parseInt(resumedSess));
							   	arrayVPNSSL.setResumableSess(Integer.parseInt(resumableSess));
							   	arrayVPNSSL.setMissSess(Integer.parseInt(missSess));
							   	arrayVPNSSL.setIpaddress(node.getIpAddress());
							   	arrayVPNSSL.setCollecttime(date);
							   	arrayVPNSSL.setType("VPN");
							   	arrayVPNSSL.setSubType("ArrayNetworks");
							   
							   	SysLogger.info("sslIndex:"+sslIndex+"   vhostName:"+vhostName
							   			+"  openSSLConns :"+openSSLConns+"  acceptedConns:"+acceptedConns
							   			+"  requestedConns:"+requestedConns+" resumedSess:"+resumedSess
							   			+"  resumableSess:"+resumableSess+"  missSess:"+missSess);  
								powerVector.addElement(arrayVPNSSL);	
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
			ipAllData.put("SSL",powerVector);
		    ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		    returnHash.put("SSL", powerVector);
		    return returnHash;
		}

	
//	public static void main(String[] args){
//		ArrayVPNSSLSnmp arrayVPNClusterSnmp = new ArrayVPNSSLSnmp();
//		Host node = new Host();
//		node.setIpAddress("10.204.3.254");
//		node.setCommunity("oavpn-1");
//		node.setSnmpversion(1);
//		Hashtable returnHash = arrayVPNClusterSnmp.collect_Data(node);
//		System.out.println(returnHash);
//	}
}


