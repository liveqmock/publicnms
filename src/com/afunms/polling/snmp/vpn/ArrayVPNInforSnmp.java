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
import com.afunms.polling.om.ArrayVPNInfor;
import com.afunms.topology.model.HostNode;


/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ArrayVPNInforSnmp extends SnmpMonitor {
	private Hashtable sendeddata = ShareData.getProcsendeddata();
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 
	 */
	public ArrayVPNInforSnmp() {
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
			   					"1.3.6.1.4.1.7564.32.1.2.1.2",
			   					"1.3.6.1.4.1.7564.32.1.2.1.3",
			   					"1.3.6.1.4.1.7564.32.1.2.1.4",
			   					"1.3.6.1.4.1.7564.32.1.2.1.5",
			   					"1.3.6.1.4.1.7564.32.1.2.1.6",
			   					"1.3.6.1.4.1.7564.32.1.2.1.7",
			   					"1.3.6.1.4.1.7564.32.1.2.1.8",
			   					"1.3.6.1.4.1.7564.32.1.2.1.9"
			   			}; 
			   			valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), 3, 1000);
			   			int flag = 0;
						if(valueArray != null){
						   	  for(int i=0;i<valueArray.length;i++)
						   	  {   
						   		ArrayVPNInfor arrayVPNInfor = new ArrayVPNInfor();
						   		String vpnId  = valueArray[i][0];
						   		String vpnTunnelsOpen = valueArray[i][1];
						   		String vpnTunnelsEst = valueArray[i][2];
						   		String vpnTunnelsRejected = valueArray[i][3];
						   		String vpnTunnelsTerminated = valueArray[i][4];
							   	String vpnBytesIn = valueArray[i][5];
							   	String vpnBytesOut = valueArray[i][6];
							   	String vpnUnauthPacketsIn = valueArray[i][7]; 

							   	arrayVPNInfor.setVpnBytesIn(Long.parseLong(vpnBytesIn));
							   	arrayVPNInfor.setVpnUnauthPacketsIn(Integer.parseInt(vpnUnauthPacketsIn));
							   	arrayVPNInfor.setVpnTunnelsTerminated(Integer.parseInt(vpnTunnelsTerminated));
							   	arrayVPNInfor.setVpnBytesOut(Long.parseLong(vpnBytesOut));
							   	arrayVPNInfor.setVpnTunnelsEst(Integer.parseInt(vpnTunnelsEst));
							   	arrayVPNInfor.setVpnId(vpnId);
							   	arrayVPNInfor.setVpnTunnelsOpen(Integer.parseInt(vpnTunnelsOpen));
							   	arrayVPNInfor.setVpnTunnelsRejected(Integer.parseInt(vpnTunnelsRejected));
							   	arrayVPNInfor.setIpaddress(node.getIpAddress());
							   	arrayVPNInfor.setCollecttime(date);
							   	arrayVPNInfor.setType("VPN");
							   	arrayVPNInfor.setSubtype("ArrayNetworks");
							   
							   	SysLogger.info("vpnBytesIn:"+vpnBytesIn+"   vpnUnauthPacketsIn:"+vpnUnauthPacketsIn
							   			+"  vpnTunnelsTerminated :"+vpnTunnelsTerminated+"  vpnBytesOut:"+vpnBytesOut
							   			);  
								powerVector.addElement(arrayVPNInfor);	
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
			ipAllData.put("VPNInfor",powerVector);
		    ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		    returnHash.put("VPNInfor", powerVector);
		    return returnHash;
		}

}


