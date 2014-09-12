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
import com.afunms.polling.om.ArrayVPNVS;
import com.afunms.topology.model.HostNode;


/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ArrayVPNVSSnmp extends SnmpMonitor {
	private Hashtable sendeddata = ShareData.getProcsendeddata();
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 
	 */
	public ArrayVPNVSSnmp() {
	}

	   public void collectData(Node node,MonitoredItem item){
		   
	   }
	   public void collectData(HostNode node){
		   
	   }
	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode){//要改为
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
			   					"1.3.6.1.4.1.7564.34.1.2.2.1.1",
			   					"1.3.6.1.4.1.7564.34.1.2.2.1.2",
			   					"1.3.6.1.4.1.7564.34.1.2.2.1.3",
			   					"1.3.6.1.4.1.7564.34.1.2.2.1.4",
			   					"1.3.6.1.4.1.7564.34.1.2.2.1.5" 
			   			}; 
			   			valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), 3, 1000);
			   			int flag = 0;
						if(valueArray != null){
						   	  for(int i=0;i<valueArray.length;i++)
						   	  {   
						   		ArrayVPNVS arrayVPNVS = new ArrayVPNVS();
						   		String vsIndex  = valueArray[i][0];
						   		String vsID = valueArray[i][1];
						   		String vsProtocol = valueArray[i][2];
						   		String vsIpAddr = valueArray[i][3];
						   		String vsPort = valueArray[i][4];
						   		
						   		arrayVPNVS.setVsIndex(Integer.parseInt(vsIndex));
						   		arrayVPNVS.setVsID(vsID);
						   		arrayVPNVS.setVsIpAddr(vsIpAddr);
						   		arrayVPNVS.setVsPort(Integer.parseInt(vsPort));
						   		arrayVPNVS.setVsProtocol(Integer.parseInt(vsProtocol));
						   		
							   	arrayVPNVS.setIpaddress(node.getIpAddress());
							   	arrayVPNVS.setCollecttime(date);
							   	arrayVPNVS.setType("VPN");
							   	arrayVPNVS.setSubtype("ArrayNetworks");
							   
							   	SysLogger.info("vsIndex:"+vsIndex+"   vsID:"+vsID
							   			+"  vsIpAddr :"+vsIpAddr+"  vsPort:"+vsPort
							   			);  
								powerVector.addElement(arrayVPNVS);	
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
			ipAllData.put("VPNVS",powerVector);
		    ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		    returnHash.put("VPNVS", powerVector);
		    return returnHash;
		}

	
}


