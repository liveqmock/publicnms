package com.afunms.polling.snmp.device;

/*
 * @author yangjun@dhcc.com.cn
 * cisco光纤交换机硬件信息
 */

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.common.util.SysLogger;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.Systemcollectdata;
import com.afunms.polling.snmp.SnmpMibConstants;
import com.afunms.system.util.TimeGratherConfigUtil;
import com.afunms.topology.model.HostNode;


/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CiscoHardWareSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 
	 */
	public CiscoHardWareSnmp() {
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
		Vector hardwareVector=new Vector();
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
    				SysLogger.info("######## "+node.getIpAddress()+" 不在采集内存时间段内,退出##########");
    				//清除之前内存中产生的告警信息
//    			    try{
//    			    	//清除之前内存中产生的内存告警信息
//						CheckEventUtil checkutil = new CheckEventUtil();
//						checkutil.deleteEvent(node.getId()+":net:memory");
//    			    }catch(Exception e){
//    			    	e.printStackTrace();
//    			    }
    				return returnHash;
    			}
    			
    		}
    	}
		
		
		try {
            Systemcollectdata systemdata=null;
			Calendar date=Calendar.getInstance();
			try {
		        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				com.afunms.polling.base.Node snmpnode = (com.afunms.polling.base.Node)PollingEngine.getInstance().getNodeByIP(node.getIpAddress());
				Date cc = date.getTime();
				String time = sdf.format(cc);
				snmpnode.setLastTime(time);
			} catch(Exception e){
		        SysLogger.info("HardWare collect:设备节点信息获取失败!");
				e.printStackTrace();
			}
			//-------------------------------------------------------------------------------------------HardWare start			
			try {
				final String[] desc=SnmpMibConstants.NetWorkMibHardwareDesc;
				final String[] chname=SnmpMibConstants.NetWorkMibHardwareChname;
						  String[] oids =                
							  new String[] {               
								"1.3.6.1.2.1.47.1.1.1.1.2.278" ,
								"1.3.6.1.2.1.47.1.1.1.1.2.279" ,
								"1.3.6.1.2.1.47.1.1.1.1.2.470" ,
								"1.3.6.1.2.1.47.1.1.1.1.2.471" 
						  };
						  
				String[][] valueArray = null;   	  
				try {
					//valueArray = snmp.getTableData(node.getIpAddress(),node.getCommunity(),oids);
					valueArray = SnmpUtils.getTableData(node.getIpAddress(),node.getCommunity(),oids,node.getSnmpversion(),
		   		  			node.getSecuritylevel(),node.getSecurityName(),node.getV3_ap(),node.getAuthpassphrase(),node.getV3_privacy(),node.getPrivacyPassphrase(),3,1000*30);
				} catch(Exception e){
					valueArray = null;
				}
				if(valueArray != null && valueArray.length > 0){
			   	  for(int i=0;i<valueArray.length;i++)
			   	  {
			   		 for(int j=0;j<6;j++){
			   			systemdata=new Systemcollectdata();
						systemdata.setIpaddress(node.getIpAddress());
						systemdata.setCollecttime(date);
						systemdata.setCategory("device");
						systemdata.setEntity(desc[i]);
						systemdata.setSubentity(desc[j]);
						systemdata.setChname(chname[j]);
						systemdata.setRestype("static");
						systemdata.setUnit("");
						String value = valueArray[i][j];
						if (j==0){
								systemdata.setThevalue(value);
						} else
							systemdata.setThevalue(value);
						hardwareVector.addElement(systemdata);
			   		 }
			   	  }
				}
			  } catch(Exception e){
				  //e.printStackTrace();
				  SysLogger.info("HardWare collect:error!");
			  }
			  //-------------------------------------------------------------------------------------------system end
			  
			  
			//-------------------------------------------------------------------------------------------mac start			
//				try{
//				
//				  String[] oids =                
//					  new String[] {                
//						  "1.3.6.1.2.1.2.2.1.6"
//						  };
//				  String[][] valueArray = null;
//					try {
//						valueArray = snmp.getTableData(node.getIpAddress(),node.getCommunity(),oids);
//					} catch(Exception e){
//						valueArray = null;
//						//SysLogger.error(node.getIpAddress() + "_WindowsSnmp");
//					}
//					systemdata=new Systemcollectdata();
//					systemdata.setIpaddress(node.getIpAddress());
//					systemdata.setCollecttime(date);
//					systemdata.setCategory("System");
//					systemdata.setEntity("MacAddr");
//					systemdata.setSubentity("MacAddr");
//					systemdata.setRestype("static");
//					systemdata.setUnit(" ");
//					if(valueArray != null){
//						for(int i=0;i<valueArray.length;i++){
//							String value=valueArray[i][0];
//							if (value == null || value.length()==0)continue;
//							systemdata.setThevalue(value);
//							break;
//						}
//					}				
//					systemVector.addElement(systemdata);
//		
//				}
//				catch(Exception e){
//					//e.printStackTrace();
//				}
				//-------------------------------------------------------------------------------------------mac end	
			  
			} catch(Exception e){
				//returnHash=null;
				//e.printStackTrace();
				//return null;
			} finally{
//				System.gc();
			}
		
//		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(node.getIpAddress());
//		if(ipAllData == null)ipAllData = new Hashtable();
//		ipAllData.put("device",hardwareVector);
//	    ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
	    
	    
	    if(!(ShareData.getSharedata().containsKey(node.getIpAddress())))
		{
			Hashtable ipAllData = new Hashtable();
			if(ipAllData == null)ipAllData = new Hashtable();
			if(hardwareVector != null && hardwareVector.size()>0)ipAllData.put("device",hardwareVector);
			
		    ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		}else
		 {
			if(hardwareVector != null && hardwareVector.size()>0)((Hashtable)ShareData.getSharedata().get(node.getIpAddress())).put("device",hardwareVector);
		 }
	    	    
	    returnHash.put("device", hardwareVector);
	    return returnHash;
	}
}





