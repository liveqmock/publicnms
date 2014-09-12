package com.afunms.polling.snmp.flash;


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
import com.afunms.common.util.SysLogger;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.Flashcollectdata;
import com.afunms.system.util.TimeGratherConfigUtil;
import com.afunms.topology.model.HostNode;
import com.gatherResulttosql.NetDatetempFlashRtosql;
import com.gatherResulttosql.NetflashResultTosql;


/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MaipuFlashSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 
	 */   
	public MaipuFlashSnmp() {
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
		Vector flashVector=new Vector();
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
    				SysLogger.info("######## "+node.getIpAddress()+" 不在采集flash时间段内,退出##########");
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
//					-------------------------------------------------------------------------------------------闪存 start
		   		  String temp = "0";
		   		  if(node.getSysOid().startsWith("1.3.6.1.4.1.5651.")){
		   			String[][] valueArray = null;
		   			String[] oids =                
						  new String[] {               
							"1.3.6.1.4.1.5651.3.600.2.1.1.1",//flash partition
							"1.3.6.1.4.1.5651.3.600.2.1.1.12",//Size of flash partition
							"1.3.6.1.4.1.5651.3.600.2.1.1.13"//Free space of the flash device partition
		   			};
		   			//valueArray = SnmpUtils.getTemperatureTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), 3, 1000*30);
		   			valueArray = SnmpUtils.getTemperatureTableData(node.getIpAddress(),node.getCommunity(),oids,node.getSnmpversion(),
		   		  			node.getSecuritylevel(),node.getSecurityName(),node.getV3_ap(),node.getAuthpassphrase(),node.getV3_privacy(),node.getPrivacyPassphrase(),3,1000*30);
		   			int flag = 0;
					if(valueArray != null){
					   	  for(int i=0;i<valueArray.length;i++)
					   	  {
//					   		String partitionname = valueArray[i][0];
					   		String allflashvalue = valueArray[i][1];
					   		String freevalue = valueArray[i][2];
					   		String index = valueArray[i][3];
					   		//SysLogger.info(host.getIpAddress()+"   usedvalue==="+usedvalue);
					   		float value=0.0f;
					   		//int value=0;
					   		String usedperc = "0";
					   		try{
					   		if(Long.parseLong(allflashvalue) > 0)
					   			value = (Long.parseLong(allflashvalue)-Long.parseLong(freevalue))*100/(Long.parseLong(allflashvalue));
					   		}catch(Exception e){
					   			
					   		}
							if( value >0){
								int intvalue = Math.round(value); 
								//intvalue = value/intvalue;
								flag = flag +1;
						   		List alist = new ArrayList();
						   		alist.add("");
						   		alist.add(usedperc);
						   		//内存
						   		//memoryList.add(alist);	
						   		Flashcollectdata flashcollectdata = new Flashcollectdata();
						   		flashcollectdata.setIpaddress(node.getIpAddress());
						   		flashcollectdata.setCollecttime(date);
						   		flashcollectdata.setCategory("Flash");
						   		flashcollectdata.setEntity("Utilization");
						   		flashcollectdata.setSubentity(index);
						   		flashcollectdata.setRestype("dynamic");
						   		flashcollectdata.setUnit("");
						   		flashcollectdata.setThevalue(intvalue+"");
								//SysLogger.info(node.getIpAddress()+"======== "+partitionname+" "+index +" 闪存： "+Integer.parseInt(intvalue+""));
								flashVector.addElement(flashcollectdata);
						   		
							}
					   		//SysLogger.info(host.getIpAddress()+"  "+index+"   value="+value);
					   	  }
					}
		   		  }
		   		//memoryVector.add(memoryList);
		   	  }
		   	  catch(Exception e)
		   	  {
		   	  }	   	  
		   	  //-------------------------------------------------------------------------------------------闪存 end
		}
		catch(Exception e){
			//returnHash=null;
			//e.printStackTrace();
			//return null;
		}
		
	    if(!(ShareData.getSharedata().containsKey(node.getIpAddress())))
		{
			Hashtable ipAllData = new Hashtable();
			if(ipAllData == null)ipAllData = new Hashtable();
			if(flashVector != null && flashVector.size()>0)ipAllData.put("flash",flashVector);
		    ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		}else
		 {
			if(flashVector != null && flashVector.size()>0)((Hashtable)ShareData.getSharedata().get(node.getIpAddress())).put("flash",flashVector);
		   
		 }
	    returnHash.put("flash",flashVector);
	    //把采集结果生成sql
	    NetflashResultTosql tosql=new NetflashResultTosql();
	    tosql.CreateResultTosql(returnHash, node.getIpAddress());
	    String runmodel = PollingEngine.getCollectwebflag();//采集与访问模式
	    if(!"0".equals(runmodel)){
			//采集与访问是分离模式,则不需要将监视数据写入临时表格
	    	NetDatetempFlashRtosql totempsql =new NetDatetempFlashRtosql();
		    totempsql.CreateResultTosql(returnHash, node);
	    }
	    
	    
	    return returnHash;
	}
}





