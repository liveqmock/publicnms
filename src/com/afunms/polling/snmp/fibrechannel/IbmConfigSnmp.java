package com.afunms.polling.snmp.fibrechannel;

/*
 * @author yangjun@dhcc.com.cn
 * 2010-10-23
 * 光纤通道配置信息
 */

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.Fbconfigcollectdata;
import com.afunms.polling.snmp.SnmpMibConstants;
import com.afunms.topology.model.HostNode;


/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class IbmConfigSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static Hashtable ifEntity_ifStatus = null;
	static {
		ifEntity_ifStatus = new Hashtable();
		ifEntity_ifStatus.put("1", "online");
		ifEntity_ifStatus.put("2", "offline");
		ifEntity_ifStatus.put("3", "testing");
		ifEntity_ifStatus.put("4", "faulty");
	};
	/**
	 * 
	 */
	public IbmConfigSnmp() {
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
		Vector fbconfigVector=new Vector();
		Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if(node == null)return null;
		try {
            Fbconfigcollectdata fbconfigcollectdata=null;
			Calendar date=Calendar.getInstance();
			try{
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				com.afunms.polling.base.Node snmpnode = (com.afunms.polling.base.Node)PollingEngine.getInstance().getNodeByIP(node.getIpAddress());
				Date cc = date.getTime();
				String time = sdf.format(cc);
				snmpnode.setLastTime(time);
			} catch(Exception e){
				SysLogger.info("Config collect:设备节点信息获取失败!");
			}
			//-------------------------------------------------------------------------------------------config start			
			try{
				
				final String[] desc=SnmpMibConstants.NetWorkMibConfigDesc;
				final String[] chname=SnmpMibConstants.NetWorkMibConfigChname;
			    String[] oids =                
				    new String[] {               
					  "1.3.6.1.2.1.75.1.1.1.0" ,
					  "1.3.6.1.2.1.75.1.1.2.0" ,
					  "1.3.6.1.2.1.75.1.1.3.0" ,
					  "1.3.6.1.2.1.75.1.1.4.1.2.1" ,
					  "1.3.6.1.2.1.75.1.1.4.1.3.1" ,
					  "1.3.6.1.2.1.75.1.1.4.1.4.1" ,
					  "1.3.6.1.2.1.75.1.1.4.1.5.1" ,
					  "1.3.6.1.2.1.75.1.1.4.1.6.1" ,
					  "1.3.6.1.2.1.75.1.1.4.1.7.1"
			        };
						  
			    String[] valueArray = new String[9];   	
				for(int j=0;j<oids.length;j++){
					try {
						//valueArray[j] = snmp.getMibValue(node.getIpAddress(),node.getCommunity(),oids[j]);
						valueArray[j] = snmp.getMibValue(node.getIpAddress(),node.getSnmpversion(),node.getCommunity(),node.getSecuritylevel(),node.getSecurityName(),node.getV3_ap(),node.getAuthpassphrase(),node.getV3_privacy(),node.getPrivacyPassphrase(),oids[j]);
						
					} catch(Exception e){
						e.printStackTrace();
					}
				}
				if(valueArray != null&&valueArray.length>0){
			   	  for(int i=0;i<valueArray.length;i++)
			   	  {
			   		fbconfigcollectdata=new Fbconfigcollectdata();
		   			fbconfigcollectdata.setIpaddress(node.getIpAddress());
		   			fbconfigcollectdata.setCollecttime(date);
		   			fbconfigcollectdata.setCategory("fbconfig");
		   			fbconfigcollectdata.setEntity(desc[i]);
		   			fbconfigcollectdata.setSubentity("fbconfig");
		   			fbconfigcollectdata.setChname(chname[i]);
		   			fbconfigcollectdata.setRestype("static");
		   			fbconfigcollectdata.setUnit("");
					String value = valueArray[i];
					if(i==5&&value!=null){
						if (ifEntity_ifStatus.get(value) != null){
							fbconfigcollectdata.setThevalue(ifEntity_ifStatus.get(value).toString());
                           
						} else {
							fbconfigcollectdata.setThevalue(" ");
						}
					} else {
						fbconfigcollectdata.setThevalue(value);
					}
					fbconfigVector.addElement(fbconfigcollectdata);
			   	  }
				}
			  } catch(Exception e){
				  e.printStackTrace();
			}
			  //-------------------------------------------------------------------------------------------config end
			}catch(Exception e){
				//returnHash=null;
				//e.printStackTrace();
				//return null;
				SysLogger.info("光纤通道配置信息 collect:error!");
			}finally{
//				System.gc();
			}
		
//		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(node.getIpAddress());
//		if(ipAllData == null)ipAllData = new Hashtable();
//		ipAllData.put("fbconfig",fbconfigVector);
//	    ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
//	    returnHash.put("fbconfig", fbconfigVector);
	    
	    
	    if(!(ShareData.getSharedata().containsKey(node.getIpAddress())))
		{
			Hashtable ipAllData = new Hashtable();
			if(ipAllData == null)ipAllData = new Hashtable();
			if(fbconfigVector != null && fbconfigVector.size()>0)ipAllData.put("fbconfig",fbconfigVector);
		    ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		}else
		 {
			if(fbconfigVector != null && fbconfigVector.size()>0)((Hashtable)ShareData.getSharedata().get(node.getIpAddress())).put("fbconfig",fbconfigVector);
		   
		 }
	    returnHash.put("fbconfig", fbconfigVector);
	    
	    
	    
	    
	    return returnHash;
	}
}





