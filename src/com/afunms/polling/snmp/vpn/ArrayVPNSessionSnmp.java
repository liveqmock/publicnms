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
import com.afunms.polling.om.ArrayVPNSession;
import com.afunms.topology.model.HostNode;


/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ArrayVPNSessionSnmp extends SnmpMonitor {
	private Hashtable sendeddata = ShareData.getProcsendeddata();
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 
	 */
	public ArrayVPNSessionSnmp() {
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
					  //-------------------------------------------------------------------------------------------电源 start
			   		  String temp = "0";
			   			String[][] valueArray = null;
			   			String[] oids =                
							  new String[] {      

			   					"1.3.6.1.4.1.7564.21.1",
			   					"1.3.6.1.4.1.7564.21.2",
			   					"1.3.6.1.4.1.7564.21.3",
			   					"1.3.6.1.4.1.7564.21.4",
			   					"1.3.6.1.4.1.7564.21.5",
			   					"1.3.6.1.4.1.7564.21.6",
			   					"1.3.6.1.4.1.7564.21.7",
			   					"1.3.6.1.4.1.7564.21.8",
			   					"1.3.6.1.4.1.7564.21.9"
			   			};

			   			valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), 3, 1000);
						if(valueArray != null){
						   	  for(int i=0;i<valueArray.length;i++)
						   	  {
						   	   ArrayVPNSession arrayVPNSession = new ArrayVPNSession();
						   	   int id = i+1;
							 
							   String numSessions = valueArray[i][0];
							   
							   String successLogin = valueArray[i][1];
							   
							   String successLogout = valueArray[i][2];
							   
							   String failureLogin = valueArray[i][3];
							   
							   String totalBytesIn = valueArray[i][4];
							   
							   String totalBytesOut = valueArray[i][5];
							   
							   String maxActiveSessions = valueArray[i][6];
							   
							   String errorLogin = valueArray[i][7];
							   
							   String lockOutLogin = valueArray[i][8];
							   arrayVPNSession.setId(id); 
							   arrayVPNSession.setErrorLogin(Integer.parseInt(errorLogin));
							   arrayVPNSession.setFailureLogin(Integer.parseInt(failureLogin));
							   arrayVPNSession.setLockOutLogin(lockOutLogin.length());
							   arrayVPNSession.setSuccessLogout(Integer.parseInt(successLogout));
							   arrayVPNSession.setNumSessions(Integer.parseInt(numSessions));
							   arrayVPNSession.setMaxActiveSessions(Integer.parseInt(maxActiveSessions));
							   arrayVPNSession.setSuccessLogin(Integer.parseInt(successLogin));
							   arrayVPNSession.setTotalBytesIn(Long.parseLong(totalBytesIn));
							   arrayVPNSession.setTotalBytesOut(Long.parseLong(totalBytesOut));
								
							   arrayVPNSession.setCollecttime(date.getTime().toString());
							   arrayVPNSession.setIpaddress(node.getIpAddress());
							   arrayVPNSession.setType("VPN");
							   arrayVPNSession.setSubtype("ArrayNetworks");
							   SysLogger.info("errorLogin:"+errorLogin+"  failureLogin:"+failureLogin
							   			+" lockOutLogin :"+lockOutLogin+" successLogout:"+successLogout 
							   			+" numSessions:"+numSessions+" maxActiveSessions:"+maxActiveSessions
							   			+" successLogin:"+successLogin+" successLogin:"+successLogin 
							   			+" totalBytesOut:"+totalBytesOut);
								
							   	powerVector.addElement(arrayVPNSession);	
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
			ipAllData.put("Session",powerVector);
		    ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		    returnHash.put("Session", powerVector);
		    
		    return returnHash;
		}

	
}


