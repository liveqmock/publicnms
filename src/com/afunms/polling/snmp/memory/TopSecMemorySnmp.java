package com.afunms.polling.snmp.memory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmConstant;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.common.util.SysLogger;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.CPUcollectdata;
import com.afunms.polling.om.Memorycollectdata;
import com.gatherResulttosql.NetHostMemoryRtsql;
import com.gatherResulttosql.NetmemoryResultTosql;

public class TopSecMemorySnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public TopSecMemorySnmp(){
	
	}
    public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {	
		//
		Hashtable returnHash=new Hashtable();
		Vector<Memorycollectdata> memoryVector=new Vector<Memorycollectdata>();
		List memoryList = new ArrayList();
		Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if(node == null)
			return null;
		
		    CPUcollectdata cpudata=null;
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
		   		  String temp = "0";
		   		  if(node.getSysOid().startsWith("1.3.6.1.4.1.14331.")){
		   			String[][] valueArray = null;
		   			String[] oids =                
						  new String[] {               
							"1.3.6.1.4.1.14331.5.5.1.4.6"};//MemoryLoad
					
		   			//valueArray = SnmpUtils.getTemperatureTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), 3, 1000*30);
		   			valueArray = SnmpUtils.getTemperatureTableData(node.getIpAddress(),node.getCommunity(),oids,node.getSnmpversion(),
		   		  			node.getSecuritylevel(),node.getSecurityName(),node.getV3_ap(),node.getAuthpassphrase(),node.getV3_privacy(),node.getPrivacyPassphrase(),3,1000*30);
		   			int allvalue=0;
		   			int flag = 0;
		   			String intvalues="";
		   			String index="";
		   			String usedperc = "0";
					if(valueArray != null){
					   	  for(int i=0;i<valueArray.length;i++)
					   	  {
					   		String usedvalue = valueArray[i][0];	
					   		//SysLogger.info("memory valuestring:"+usedvalue);
					   		index = valueArray[i][1];
					   		int value=0;
				   	    	try{
					   			value = Integer.parseInt(usedvalue);
					   	 		 
					   		}catch(Exception e){
					   			
					   		}
							//if( value>0){
								//int intvalue = Math.round(value);								
								flag = flag +1;
						   		List alist = new ArrayList();
						   		alist.add("");
						   		alist.add(usedperc);
						   		//内存
						   		memoryList.add(alist);	
						   		Memorycollectdata memorycollectdata = new Memorycollectdata();
						   		memorycollectdata.setIpaddress(node.getIpAddress());
						   		memorycollectdata.setCollecttime(date);
						   		memorycollectdata.setCategory("Memory");
						   		memorycollectdata.setEntity("Utilization");
						   		memorycollectdata.setSubentity(index);
						   		memorycollectdata.setRestype("dynamic");
						   		memorycollectdata.setUnit("%");		
						   		memorycollectdata.setThevalue(value+"");
								memoryVector.addElement(memorycollectdata);	
								//SysLogger.info("=======Start collect data as ip VenusMemorySnmp=====intvalue:"+intvalue);
							//}
					   	  }
					 }
		   		  }
		   		//memoryVector.add(memoryList);
		   	  }
		   	  catch(Exception e)
		   	  {
		   		  e.printStackTrace();
		   	  }	   	  
	      	//-------------------------------------------------------------------------------------------内存 end
		   	  
		   	Hashtable collectHash = new Hashtable();
			collectHash.put("memory", memoryVector);
		   	  
				//对内存值进行告警检测
			    try{
					AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
					List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(node.getId()), AlarmConstant.TYPE_FIREWALL, "topsec","memory");
					for(int i = 0 ; i < list.size() ; i ++){
						AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode)list.get(i);
						//对虚拟内存值进行告警检测
						CheckEventUtil checkutil = new CheckEventUtil();
						checkutil.updateData(node,collectHash,"firewall","topsec",alarmIndicatorsnode);
					}
			    }catch(Exception e){
			    	e.printStackTrace();
			    }

		Hashtable ipAllData = new Hashtable();
		try{
			ipAllData = (Hashtable)ShareData.getSharedata().get(node.getIpAddress());
		}catch(Exception e){
			
		}
		if(ipAllData == null)ipAllData = new Hashtable();
		if (memoryVector != null && memoryVector.size()>0)ipAllData.put("memory",memoryVector);
	    ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
	    returnHash.put("memory",memoryVector);
	    ipAllData=null;
	    memoryVector=null;
	    //System.out.println("==把采集结果生成sql===");    
	    //把采集结果生成sql
	    NetmemoryResultTosql tosql=new NetmemoryResultTosql();
	    tosql.CreateResultTosql(returnHash, node.getIpAddress());
	    NetHostMemoryRtsql  totempsql=new NetHostMemoryRtsql();
	    totempsql.CreateResultTosql(returnHash, node);
	    
	    return returnHash;
    }
}
