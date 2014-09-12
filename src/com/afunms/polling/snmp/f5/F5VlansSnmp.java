package com.afunms.polling.snmp.f5;

/*
 * @author yangjun@dhcc.com.cn
 *
 */

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Host;
import com.afunms.topology.model.HostNode;


/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class F5VlansSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 
	 */
	public F5VlansSnmp() {
	}

	public void collectData(Node node,MonitoredItem item){
		   
	}
	public void collectData(HostNode node){
		   
	}
	/* (non-Javadoc)
	 * @see com.dhcc.webnms.host.snmp.AbstractSnmp#collectData()
	 */
	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash=new Hashtable();
		Vector vlans = new Vector();
		Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if(node == null)return null;
		try {
			Calendar date=Calendar.getInstance();
			Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(node.getIpAddress());
			if(ipAllData == null)ipAllData = new Hashtable();
			
			  try{
				  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				  com.afunms.polling.base.Node snmpnode = (com.afunms.polling.base.Node)PollingEngine.getInstance().getNodeByIP(node.getIpAddress());
				  Date cc = date.getTime();
				  String time = sdf.format(cc);
				  snmpnode.setLastTime(time);
			  }catch(Exception e){
				  
			  }
			  try{
					String[] oids =                
						new String[] {               
							"1.3.6.1.4.1.3375.2.2.5.1.2.1.1",//name
							"1.3.6.1.4.1.3375.2.2.5.1.2.1.17",//User name
							"1.3.6.1.4.1.3375.2.2.5.1.2.1.18",//User login IP
							"1.3.6.1.4.1.3375.2.2.5.1.2.1.21"}; //User login time
					
					String[][] valueArray = null; 
					String index="";
					String valnid="";
					String status="";
					String ports="";
					
					try {
						//valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), 3, 5000);
						valueArray = SnmpUtils.getTableData(node.getIpAddress(),node.getCommunity(),oids,node.getSnmpversion(),
			   		  			node.getSecuritylevel(),node.getSecurityName(),node.getV3_ap(),node.getAuthpassphrase(),node.getV3_privacy(),node.getPrivacyPassphrase(),3,1000*30);
					} catch(Exception e){
						valueArray = null;
					}
					if(valueArray != null){
				   	  for(int i=0;i<valueArray.length;i++)
				   	  {
				   			try{
				   				index=valueArray[i][0];//index
				   				valnid=valueArray[i][1];//valnid
				   				status=valueArray[i][2];//status
				   				ports=valueArray[i][3];//ports
				   				Hashtable vlan = new Hashtable();
				   				vlan.put("index", index);
				   				vlan.put("valnid", valnid);
				   				vlan.put("status", status);
				   				vlan.put("ports", ports);
				   				vlans.add(vlan);
				   			  }catch(Exception e){
				   				 e.printStackTrace();
				   			  }
				   			  
				   		  }
				   		 
				   	  }
				}catch(Exception e){
						e.printStackTrace();
				}  
			}catch(Exception e){
				e.printStackTrace();
			}
		
//		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(node.getIpAddress());
//		if(ipAllData == null)ipAllData = new Hashtable();
//		ipAllData.put("vlans",vlans);
//	    ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
//	    returnHash.put("vlans", vlans);
	    
	    if(!(ShareData.getSharedata().containsKey(node.getIpAddress())))
		{
			Hashtable ipAllData = new Hashtable();
			if(ipAllData == null)ipAllData = new Hashtable();
			if(vlans != null && vlans.size()>0)ipAllData.put("vlans",vlans);
		    ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		}else
		 {
			if(vlans != null && vlans.size()>0)((Hashtable)ShareData.getSharedata().get(node.getIpAddress())).put("vlans",vlans);
		   
		 }
	    returnHash.put("vlans", vlans);
	    
	    
	    
	    
	    return returnHash;
	}
}





