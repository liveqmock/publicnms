package com.afunms.polling.snmp.raid;

/*
 * @author hukelei@dhcc.com.cn
 *
 */

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.Interfacecollectdata;
import com.afunms.topology.model.HostNode;


/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CiscoRaidSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 
	 */
	public CiscoRaidSnmp() {
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
		Vector raidVector=new Vector();
		Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if(node == null)return null;
		try {
			Interfacecollectdata interfacedata = new Interfacecollectdata();
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
			  try {
//					-------------------------------------------------------------------------------------------∂¿¡¢»ﬂ”‡¥≈≈Ã’Û¡– start
//				  INTEGER {driveHealthy(1),
//					  driveFailure(2),
//					  driveRebuild(3)
//					  }
		   		  if(node.getSysOid().startsWith("1.3.6.1.4.1.15497.")){
		   			String[][] valueArray = null;
		   			String[] oids =                
						  new String[] {               
							"1.3.6.1.4.1.15497.1.1.1.18.1.1",//À˜“˝
							"1.3.6.1.4.1.15497.1.1.1.18.1.2",//◊¥Ã¨
							"1.3.6.1.4.1.15497.1.1.1.18.1.3",//name
							"1.3.6.1.4.1.15497.1.1.1.18.1.4"//±®¥Ì–≈œ¢
		   			};
		   			//valueArray = SnmpUtils.getTemperatureTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), 3, 1000);
		   			valueArray = SnmpUtils.getTemperatureTableData(node.getIpAddress(),node.getCommunity(),oids,node.getSnmpversion(),
		   		  			node.getSecuritylevel(),node.getSecurityName(),node.getV3_ap(),node.getAuthpassphrase(),node.getV3_privacy(),node.getPrivacyPassphrase(),3,1000*30);
		   			int flag = 0;
					if(valueArray != null){
					   	  for(int i=0;i<valueArray.length;i++)
					   	  {
					   		String _value = valueArray[i][1];
					   		String index = valueArray[i][0];
					   		String desc = valueArray[i][2].replaceAll(",", "-").replaceAll(" ", "-");
					   		String errormsg = valueArray[i][3];
//					   		int value=0;
//					   		try{
//					   			value=Integer.parseInt(_value);
//					   		}catch(Exception e){
//					   			
//					   		}
							//if(value > 0){
								flag = flag +1;
						   		List alist = new ArrayList();
						   		alist.add(index);
						   		alist.add(_value);
						   		alist.add(desc);
						   		//∑Á…»
						   		//fanList.add(alist);				   		
						   		  interfacedata = new Interfacecollectdata();
						   		  interfacedata.setIpaddress(node.getIpAddress());
						   		  interfacedata.setCollecttime(date);
						   		  interfacedata.setCategory("Raid");
						   		  interfacedata.setEntity(index);
						   		  interfacedata.setSubentity(desc);
						   		  interfacedata.setRestype("dynamic");
						   		  interfacedata.setUnit("");	
						   		  interfacedata.setBak(errormsg);
						   		  interfacedata.setThevalue(_value);
						   		raidVector.addElement(interfacedata);		   		
							//}
					   		//SysLogger.info(host.getIpAddress()+"  "+index+"   value="+value);
					   	  }
					}
		   		  } 
				  //fanVector.add(fanList);
		   	  }
		   	  catch(Exception e)
		   	  {
		   	  }	   	  
		   	  //-------------------------------------------------------------------------------------------∂¿¡¢»ﬂ”‡¥≈≈Ã’Û¡– end
			}catch(Exception e){
			}
		
		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(node.getIpAddress());
		if(ipAllData == null)ipAllData = new Hashtable();
		ipAllData.put("raid",raidVector);
	    ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
	    returnHash.put("raid", raidVector);
	    return returnHash;
	}
}





