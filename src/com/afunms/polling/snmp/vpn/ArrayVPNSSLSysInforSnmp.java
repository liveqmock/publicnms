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
import com.afunms.polling.om.ArrayVPNSSLSysInfor;
import com.afunms.topology.model.HostNode;


/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ArrayVPNSSLSysInforSnmp extends SnmpMonitor {
	private Hashtable sendeddata = ShareData.getProcsendeddata();
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 
	 */
	public ArrayVPNSSLSysInforSnmp() {
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
			   					"1.3.6.1.4.1.7564.20.1.1",
			   					"1.3.6.1.4.1.7564.20.1.2",
			   					"1.3.6.1.4.1.7564.20.2.1",
			   					"1.3.6.1.4.1.7564.20.2.2",
			   					"1.3.6.1.4.1.7564.20.2.3",
			   					
			   			};
			   			
			   			valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), 3, 1000);
			   			int flag = 0;
						if(valueArray != null){
						   	  for(int i=0;i<valueArray.length;i++)
						   	  {   
						   		ArrayVPNSSLSysInfor arrayVPNSSLSysInfor = new ArrayVPNSSLSysInfor();
						   		String sslStatus  = valueArray[i][0];
						   		String vhostNum = valueArray[i][1];
						   		String totalOpenSSLConns = valueArray[i][2];
						   		String totalAcceptedConns = valueArray[i][3];
						   		String totalRequestedConns = valueArray[i][4];
						   		arrayVPNSSLSysInfor.setSslStatus(sslStatus);
						   		arrayVPNSSLSysInfor.setTotalAcceptedConns(Long.parseLong(totalAcceptedConns));
						   		arrayVPNSSLSysInfor.setTotalOpenSSLConns(Long.parseLong(totalOpenSSLConns));
						   		arrayVPNSSLSysInfor.setTotalRequestedConns(Long.parseLong(totalRequestedConns));
						   		arrayVPNSSLSysInfor.setVhostNum(Integer.parseInt(vhostNum));
							   	
						   		arrayVPNSSLSysInfor.setIpaddress(node.getIpAddress());
						   		arrayVPNSSLSysInfor.setCollecttime(date);
						   		arrayVPNSSLSysInfor.setType("VPN");
						   		arrayVPNSSLSysInfor.setSubtype("ArrayNetworks");
							   	SysLogger.info("sslStatus:"+sslStatus
							   			+"   vhostNum:"+vhostNum
							   			+"  totalOpenSSLConns :"+totalOpenSSLConns
							   			+"  totalAcceptedConns:"+totalAcceptedConns
							   			+"  totalRequestedConns :"+totalRequestedConns
							   			);  
								powerVector.addElement(arrayVPNSSLSysInfor);	
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
			ipAllData.put("VPNSSLInfor",powerVector);
		    ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		    returnHash.put("VPNSSLInfor", powerVector);
		    return returnHash;
		}
	
//	public static void main(String[] args){
//		ArrayVPNSSLSysInforSnmp arrayVPNCountSnmp = new ArrayVPNSSLSysInforSnmp();
//		Host node = new Host();
//		node.setIpAddress("10.204.3.254");
//		node.setCommunity("oavpn-1"); 
//		node.setSnmpversion(1);
//		Hashtable returnHash = arrayVPNCountSnmp.collect_Data(node);
//		System.out.println(returnHash);
//	}

}


