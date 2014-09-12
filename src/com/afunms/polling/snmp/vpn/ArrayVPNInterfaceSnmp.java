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
import com.afunms.polling.om.ArrayVPNInterface;
import com.afunms.topology.model.HostNode;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ArrayVPNInterfaceSnmp extends SnmpMonitor {
	private Hashtable sendeddata = ShareData.getProcsendeddata();
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 
	 */
	public ArrayVPNInterfaceSnmp() {
	}

	   public void collectData(Node node,MonitoredItem item){
		   
	   }
	   public void collectData(HostNode node){
		   
	   }
	/* (non-Javadoc)
	 * @see com.dhcc.webnms.host.snmp.AbstractSnmp#collectData()
	 */
	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode){//要改为AlarmIndicatorsNode alarmIndicatorsNode
			Hashtable returnHash=new Hashtable();
			Vector powerVector=new Vector();
			Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
			if(node == null)return null;
			try {
				
				Calendar date=Calendar.getInstance();
				Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(node.getIpAddress());
				if(ipAllData == null)ipAllData = new Hashtable(); 
				
				  try {
			   		  String temp = "0";
			   			String[][] valueArray = null;
			   			String[] oids =                
							  new String[] { 
			   					"1.3.6.1.4.1.7564.23.4.1.1",
			   					"1.3.6.1.4.1.7564.23.4.1.2",
			   					"1.3.6.1.4.1.7564.23.4.1.3",
			   					"1.3.6.1.4.1.7564.23.4.1.4"
			   			}; 
			   			valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), 3, 1000);
			   			int flag = 0;
						if(valueArray != null){
						   	  for(int i=0;i<valueArray.length;i++)
						   	  {    
						   		ArrayVPNInterface arrayVPNInterface = new ArrayVPNInterface();
						   		String infIndex = valueArray[i][0];
						   		String infDescr = valueArray[i][1];
						   		String infOperStatus = valueArray[i][2];
							   	String infAddress = valueArray[i][3];
							   	
							   	arrayVPNInterface.setInfAddress(infAddress);
							   	arrayVPNInterface.setInfDescr(infDescr);
							   	arrayVPNInterface.setInfOperStatus(infOperStatus);
							   	arrayVPNInterface.setInfIndex(Integer.parseInt(infIndex));
							   	arrayVPNInterface.setIpaddress(node.getIpAddress());
							   	arrayVPNInterface.setCollecttime(date);
							   	arrayVPNInterface.setType("VPN");
							   	arrayVPNInterface.setSubtype("ArrayNetworks");
							   	
							   	SysLogger.info("infIndex:"+infIndex
							   			+"  infDescr:"+infDescr
							   			+"  infOperStatus:"+infOperStatus+" infAddress:"+infAddress);  
								powerVector.addElement(arrayVPNInterface);	
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
			ipAllData.put("Interface",powerVector);
		    ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		    returnHash.put("Interface", powerVector);
		    return returnHash;
		}

	
}


