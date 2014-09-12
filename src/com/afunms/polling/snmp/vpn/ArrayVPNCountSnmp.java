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
import com.afunms.polling.om.ArrayVPNCount;
import com.afunms.topology.model.HostNode;


/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ArrayVPNCountSnmp extends SnmpMonitor {
	private Hashtable sendeddata = ShareData.getProcsendeddata();
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 
	 */
	public ArrayVPNCountSnmp() {
	}

	   public void collectData(Node node,MonitoredItem item){
		   
	   }
	   public void collectData(HostNode node){
		   
	   }
	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode){//Òª¸ÄÎªAlarmIndicatorsNode alarmIndicatorsNode
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
			   					"1.3.6.1.4.1.7564.31.1.1",
			   					"1.3.6.1.4.1.7564.32.1.1",
			   					"1.3.6.1.4.1.7564.33.1.1",
			   					"1.3.6.1.4.1.7564.35.1.1",
			   					"1.3.6.1.4.1.7564.36.1.1",
			   					"1.3.6.1.4.1.7564.37.1.1",
			   					"1.3.6.1.4.1.7564.38.1.1",
			   					"1.3.6.1.4.1.7564.39.1.1",
			   					"1.3.6.1.4.1.7564.40.1.1",
			   					"1.3.6.1.4.1.7564.41.1.1",
			   					"1.3.6.1.4.1.7564.42.1.1",
			   					"1.3.6.1.4.1.7564.43.1.1",
			   					"1.3.6.1.4.1.7564.44.1.1"
			   			};
	   					
			   			valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), 3, 1000);
			   			String[] oids1 =                
							  new String[] {   
			   					"1.3.6.1.4.1.7564.18.1.1",
			   					"1.3.6.1.4.1.7564.18.1.2",};
			   			String[][] valueArray1 = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids1, node.getSnmpversion(), 3, 1000);
			   			String[] oids2 =                
							  new String[] {   
			   					"1.3.6.1.4.1.7564.34.1.1.1",
			   					"1.3.6.1.4.1.7564.34.1.2.1",};
			   			String[][] valueArray2 = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids2, node.getSnmpversion(), 3, 1000);
			   			String[] oids3 =                
							  new String[] {   
			   					"1.3.6.1.4.1.7564.23.1",};
			   			String[][] valueArray3 = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids3, node.getSnmpversion(), 3, 1000);
			   			
			   			int flag = 0;
						if(valueArray != null){
						   	  for(int i=0;i<valueArray.length;i++)
						   	  {   
						   		ArrayVPNCount arrayVPNCount = new ArrayVPNCount();
						   		
						   	   String virtualSiteCount = valueArray[i][0];
							   String  vpnCount = valueArray[i][1];
							   String webCount  = valueArray[i][2];
							   String vclientAppCount  = valueArray[i][3];
							   String virtualSiteGroupCount  = valueArray[i][4];
							   String tcsModuleCount  = valueArray[i][5];
							   String imapsCount  = valueArray[i][6];
							   String  smtpsCount  = valueArray[i][7];
							   String appFilterCount  = valueArray[i][8];
							   String  dvpnSiteCount  = valueArray[i][9];
							   String dvpnResourceCount  = valueArray[i][10];
							   String dvpnTunnelCount  = valueArray[i][11];
							   String dvpnAclCount  = valueArray[i][12];
							   
							   String  maxCluster  = valueArray1[i][0];
							   String clusterNum  = valueArray1[i][1];
							   
							   String rsCount  = valueArray2[i][0]; 
							   String vsCount  = valueArray2[i][1];
							   
							   String infNumber  = valueArray3[i][0];
							   arrayVPNCount.setAppFilterCount(Integer.parseInt(appFilterCount));
							   arrayVPNCount.setClusterNum(Integer.parseInt(clusterNum));
							   arrayVPNCount.setDvpnAclCount(Integer.parseInt(dvpnAclCount));
							   arrayVPNCount.setDvpnResourceCount(Integer.parseInt(dvpnResourceCount));
							   arrayVPNCount.setDvpnSiteCount(Integer.parseInt(dvpnSiteCount));
							   arrayVPNCount.setDvpnTunnelCount(Integer.parseInt(dvpnTunnelCount));
							   arrayVPNCount.setImapsCount(Integer.parseInt(imapsCount));
							   arrayVPNCount.setInfNumber(Integer.parseInt(infNumber));
							   arrayVPNCount.setMaxCluster(Integer.parseInt(maxCluster));
							   arrayVPNCount.setRsCount(Integer.parseInt(rsCount));
							   arrayVPNCount.setWebCount(Integer.parseInt(webCount));
							   arrayVPNCount.setVsCount(Integer.parseInt(vsCount));
							   arrayVPNCount.setVpnCount(Integer.parseInt(vpnCount));
							   arrayVPNCount.setVirtualSiteGroupCount(Integer.parseInt(virtualSiteGroupCount));
							   arrayVPNCount.setVirtualSiteCount(Integer.parseInt(virtualSiteCount));
							   arrayVPNCount.setVclientAppCount(Integer.parseInt(vclientAppCount));
							   arrayVPNCount.setTcsModuleCount(Integer.parseInt(tcsModuleCount));
							   arrayVPNCount.setSmtpsCount(Integer.parseInt(smtpsCount));
							   
							   arrayVPNCount.setIpaddress(node.getIpAddress());
							   arrayVPNCount.setCollecttime(date);
							   arrayVPNCount.setType("VPN");
							   arrayVPNCount.setSubtype("ArrayNetworks");
							   
							   
							 powerVector.addElement(arrayVPNCount);	
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
			ipAllData.put("VPNCount",powerVector);
		    ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		    returnHash.put("VPNCount", powerVector);
		    return returnHash;
		}
	

}


