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
import com.afunms.polling.om.ArrayVPNTcs;
import com.afunms.topology.model.HostNode;


/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ArrayVPNTcsSnmp extends SnmpMonitor {
	private Hashtable sendeddata = ShareData.getProcsendeddata();
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 
	 */
	public ArrayVPNTcsSnmp() {
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
			   					"1.3.6.1.4.1.7564.37.1.2.1.1",
			   					"1.3.6.1.4.1.7564.37.1.2.1.2",
			   					"1.3.6.1.4.1.7564.37.1.2.1.3",
			   			};
			   			
			   			valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), 3, 1000);
			   			String[] oids2 =                
							  new String[] {   
			   					"1.3.6.1.4.1.7564.37.1.2.1.4"};
			   			String[][] valueArray2 = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids2, node.getSnmpversion(), 3, 1000);
			   			int flag = 0;
						if(valueArray != null){
						   	  for(int i=0;i<valueArray.length;i++)
						   	  {   
						   		ArrayVPNTcs arrayVPNTcs = new ArrayVPNTcs();
						   		String tcsModuleIndex  = valueArray[i][0];
						   		String tcsVirtualSite = valueArray[i][1];
						   		String tcsBytesIn = valueArray[i][2];
						   		String tcsBytesOut = valueArray2[i][0];

							   	arrayVPNTcs.setIpaddress(node.getIpAddress());
							   	arrayVPNTcs.setCollecttime(date);
							   	arrayVPNTcs.setType("VPN");
							   	arrayVPNTcs.setSubtype("ArrayNetworks");
							   	SysLogger.info("tcsModuleIndex:"+tcsModuleIndex
							   			+"   tcsVirtualSite:"+tcsVirtualSite
							   			+"  tcsBytesIn :"+tcsBytesIn
							   			+"  tcsBytesOut:"+tcsBytesOut
							   			);  
								powerVector.addElement(arrayVPNTcs);	
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
			ipAllData.put("VPNTCS",powerVector);
		    ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		    returnHash.put("VPNTCS", powerVector);
		    return returnHash;
		}
	
//	public static void main(String[] args){
//		ArrayVPNTcsSnmp arrayVPNTcsSnmp = new ArrayVPNTcsSnmp();
//		Host node = new Host();
//		node.setIpAddress("10.204.3.254");
//		node.setCommunity("oavpn-1");
//		node.setSnmpversion(1);
//		Hashtable returnHash = arrayVPNTcsSnmp.collect_Data(node);
//		System.out.println(returnHash);
//	}

}


