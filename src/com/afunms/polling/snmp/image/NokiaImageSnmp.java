package com.afunms.polling.snmp.image;

/*
 * @author yangjun@dhcc.com.cn
 */

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.common.util.SysLogger;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.Memorycollectdata;
import com.afunms.system.util.TimeGratherConfigUtil;
import com.afunms.topology.model.HostNode;


/**   
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */  
public class NokiaImageSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 
	 */    
	public NokiaImageSnmp() {        
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
		Hashtable imageHash=new Hashtable();
		Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if(node == null)return returnHash;
		//判断是否在采集时间段内
    	if(ShareData.getTimegatherhash() != null){
    		if(ShareData.getTimegatherhash().containsKey(node.getId()+":equipment")){
    			TimeGratherConfigUtil timeconfig = new TimeGratherConfigUtil();
    			int _result = 0;
    			_result = timeconfig.isBetween((List)ShareData.getTimegatherhash().get(node.getId()+":equipment"));
    			if(_result ==1 ){
    				//SysLogger.info("########时间段内: 开始采集 "+node.getIpAddress()+" PING数据信息##########");
    			}else if(_result == 2){
    				//SysLogger.info("########全天: 开始采集 "+node.getIpAddress()+" PING数据信息##########");
    			}else {
    				SysLogger.info("######## "+node.getIpAddress()+" 不在采集image时间段内,退出##########");
//    				//清除之前内存中产生的告警信息
//    			    try{
//    			    	//清除之前内存中产生的内存告警信息
//						CheckEventUtil checkutil = new CheckEventUtil();
//						checkutil.deleteEvent(node.getId()+":host:diskperc");
//						checkutil.deleteEvent(node.getId()+":host:diskinc");
//    			    }catch(Exception e){
//    			    	e.printStackTrace();
//    			    }
    				return returnHash;
    			}
    			
    		}
    	}
    	
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
		   		  if(node.getSysOid().startsWith("1.3.6.1.4.1.94.")){
		   			String[][] valueArray = null;
		   			String[] oids = new String[] {
	   					"1.3.6.1.4.1.94.1.21.1.5.1.1.1",
	   					"1.3.6.1.4.1.94.1.21.1.5.1.1.2",
	   					"1.3.6.1.4.1.94.1.21.1.5.1.1.3",
	   					"1.3.6.1.4.1.94.1.21.1.5.1.1.4"
		   			};
		   			try {
						//valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), 3, 5000);
		   				valueArray = SnmpUtils.getTableData(node.getIpAddress(),node.getCommunity(),oids,node.getSnmpversion(),
			   		  			node.getSecuritylevel(),node.getSecurityName(),node.getV3_ap(),node.getAuthpassphrase(),node.getV3_privacy(),node.getPrivacyPassphrase(),3,1000*30);
					} catch (Exception e) {
						valueArray = null;
						e.printStackTrace();
					}
					if(valueArray != null){
					    for(int i=0;i<valueArray.length;i++) {
					   		String index = valueArray[i][0];
					   		String version = valueArray[i][1];
					   		String serial = valueArray[i][2];
					   		String datetime = valueArray[i][3];
					   		Vector imageVector=new Vector();
							if(index!=null&&version!=null){
						   		Memorycollectdata memorycollectdata = new Memorycollectdata();
						   		memorycollectdata.setIpaddress(node.getIpAddress());
						   		memorycollectdata.setCollecttime(date);
						   		memorycollectdata.setCategory("image");
						   		memorycollectdata.setEntity("version");
						   		memorycollectdata.setSubentity(index);
						   		memorycollectdata.setRestype("dynamic");
						   		memorycollectdata.setUnit("");		
						   		memorycollectdata.setThevalue(version);
						   		imageVector.addElement(memorycollectdata);
							}
							if(index!=null&&serial!=null){
						   		Memorycollectdata memorycollectdata = new Memorycollectdata();
						   		memorycollectdata.setIpaddress(node.getIpAddress());
						   		memorycollectdata.setCollecttime(date);
						   		memorycollectdata.setCategory("image");
						   		memorycollectdata.setEntity("serial");
						   		memorycollectdata.setSubentity(index);
						   		memorycollectdata.setRestype("dynamic");
						   		memorycollectdata.setUnit("");		
						   		memorycollectdata.setThevalue(serial);
						   		imageVector.addElement(memorycollectdata);
							}
							if(index!=null&&datetime!=null){
						   		Memorycollectdata memorycollectdata = new Memorycollectdata();
						   		memorycollectdata.setIpaddress(node.getIpAddress());
						   		memorycollectdata.setCollecttime(date);
						   		memorycollectdata.setCategory("image");
						   		memorycollectdata.setEntity("datetime");
						   		memorycollectdata.setSubentity(index);
						   		memorycollectdata.setRestype("dynamic");
						   		memorycollectdata.setUnit("");		
						   		memorycollectdata.setThevalue(datetime);
						   		imageVector.addElement(memorycollectdata);
							}
							imageHash.put(index, imageVector);
					   	}
					 }
		   		 }
		   	  }
		   	  catch(Exception e)
		   	  {
		   		  e.printStackTrace();
		   	  }	
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
		if (imageHash != null && imageHash.size()>0)ipAllData.put("image",imageHash);
	    ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
	    returnHash.put("image",imageHash);
	    return returnHash;
	}
}





