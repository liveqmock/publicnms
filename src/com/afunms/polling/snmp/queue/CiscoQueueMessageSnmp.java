package com.afunms.polling.snmp.queue;

/*
 * @author yangjun@dhcc.com.cn
 * 队列信息数
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
import com.afunms.polling.om.Memorycollectdata;
import com.afunms.topology.model.HostNode;


/**   
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */  
public class CiscoQueueMessageSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 
	 */    
	public CiscoQueueMessageSnmp() {        
	}

	   public void collectData(Node node,MonitoredItem item){
		   
	   }
	   public void collectData(HostNode node){
		   
	   }
	/* (non-Javadoc)
	 * @see com.dhcc.webnms.host.snmp.AbstractSnmp#collectData()
	 */
	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {	
		//yangjun
		Hashtable returnHash=new Hashtable();
		Vector queueVector=new Vector();
		Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if(node == null)return null;
		
		try {
			Calendar date=Calendar.getInstance();
			
			  try{
				  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				  com.afunms.polling.base.Node snmpnode = (com.afunms.polling.base.Node)PollingEngine.getInstance().getNodeByIP(node.getIpAddress());
				  Date cc = date.getTime();
				  String time = sdf.format(cc);
				  snmpnode.setLastTime(time);
			  }catch(Exception e){
				  
			  }
			  try {
				  //-------------------------------------------------------------------------------------------内存 start
				  String[][] valueArray = null;
		   			String[] oids = new String[] {"1.3.6.1.4.1.15497.1.1.1.11"};
		   			//valueArray = SnmpUtils.getTemperatureTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), 3, 5000);
		   			valueArray = SnmpUtils.getTemperatureTableData(node.getIpAddress(),node.getCommunity(),oids,node.getSnmpversion(),
		   		  			node.getSecuritylevel(),node.getSecurityName(),node.getV3_ap(),node.getAuthpassphrase(),node.getV3_privacy(),node.getPrivacyPassphrase(),3,1000*30);
		   			if(valueArray != null){
					    for(int i=0;i<valueArray.length;i++) {
					   		String value = valueArray[i][0];
							if(value!=null && Integer.parseInt(value) >0){
								int intvalue = Integer.parseInt(value); 
						   		Memorycollectdata memorycollectdata = new Memorycollectdata();
						   		memorycollectdata.setIpaddress(node.getIpAddress());
						   		memorycollectdata.setCollecttime(date);
						   		memorycollectdata.setCategory("QueueMessage");
						   		memorycollectdata.setEntity("Number");
						   		memorycollectdata.setSubentity("1");
						   		memorycollectdata.setRestype("dynamic");
						   		memorycollectdata.setUnit("个");		
						   		memorycollectdata.setThevalue(intvalue+"");
						   		queueVector.addElement(memorycollectdata);
						   		
							}
					   	}
					}
		   	  }
		   	  catch(Exception e)
		   	  {
		   		  e.printStackTrace();
		   	  }	   	  
		   	  //-------------------------------------------------------------------------------------------内存 end	
		}
		catch(Exception e){
			//returnHash=null;
			e.printStackTrace();
			//return null;
		}
		
		Hashtable ipAllData = new Hashtable();
		try{
			ipAllData = (Hashtable)ShareData.getSharedata().get(node.getIpAddress());
		}catch(Exception e){
			
		}
		if(ipAllData == null)ipAllData = new Hashtable();
		if (queueVector != null && queueVector.size()>0)ipAllData.put("queuemessage",queueVector);
	    ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
	    returnHash.put("queuemessage",queueVector);
	    return returnHash;
	}
}





