package com.afunms.polling.snmp.memory;


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

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmConstant;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.common.util.SysLogger;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.CPUcollectdata;
import com.afunms.polling.om.Memorycollectdata;
import com.afunms.system.util.TimeGratherConfigUtil;
import com.afunms.topology.model.HostNode;
import com.gatherResulttosql.NetHostMemoryRtsql;
import com.gatherResulttosql.NetmemoryResultTosql;


/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DLinkMemorySnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 
	 */
	public DLinkMemorySnmp() {
	}

	   public void collectData(Node node,MonitoredItem item){
		   
	   }
	   public void collectData(HostNode node){
		   
	   }
	/* (non-Javadoc)
	 * @see com.dhcc.webnms.host.snmp.AbstractSnmp#collectData()
	 */
	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicators) {	
		//yangjun
		Hashtable returnHash=new Hashtable();
		Vector memoryVector=new Vector();
		List memoryList = new ArrayList();
		Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(nodeGatherIndicators.getNodeid()));
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
    			    try{
    			    	//清除之前内存中产生的内存告警信息
    			    	NodeDTO nodedto = null;
    					NodeUtil nodeUtil = new NodeUtil();
    					nodedto = nodeUtil.creatNodeDTOByHost(node);
						CheckEventUtil checkutil = new CheckEventUtil();
						checkutil.deleteEvent(node.getId()+"", nodedto.getType(), nodedto.getSubtype(), "memory", null);
    			    }catch(Exception e){
    			    	e.printStackTrace();
    			    }
    				return returnHash;
    			}
    			
    		}
    	}
    	
		try {
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
		   		  int usedvalueperc = 0;
		   		  if(node.getSysOid().startsWith("1.3.6.1.4.1.171.")){
			   			String[][] valueArray = null;
			   			String[] oids =                
							  new String[] {               
								"1.3.6.1.4.1.171.12.1.1.9.1.2",//hwMemSize
								"1.3.6.1.4.1.171.12.1.1.9.1.3"//hwMemFree
			   			};
			   			//valueArray = SnmpUtils.getTemperatureTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), 3, 1000*30);
			   			valueArray = SnmpUtils.getTemperatureTableData(node.getIpAddress(),node.getCommunity(),oids,node.getSnmpversion(),
			   		  			node.getSecuritylevel(),node.getSecurityName(),node.getV3_ap(),node.getAuthpassphrase(),node.getV3_privacy(),node.getPrivacyPassphrase(),3,1000*30);
			   			int allvalue=0;
			   			
			   			int flag = 0;
						if(valueArray != null && valueArray.length > 0){
						   	  for(int i=0;i<valueArray.length;i++)
						   	  {
							   		String sizevalue = valueArray[i][0];
							   		String freevalue = valueArray[i][1];
							   		String index = valueArray[i][2];
							   		float value=0.0f;
							   		String usedperc = "0";
							   		if(Long.parseLong(sizevalue) > 0)
							   			value = (Long.parseLong(sizevalue)-Long.parseLong(freevalue))*100/(Long.parseLong(sizevalue));
							   		
									if( value >0){
										int intvalue = Math.round(value); 
										allvalue = allvalue +intvalue;
										//SysLogger.info(host.getIpAddress()+" 内存： "+Integer.parseInt(intvalue+"")+" 总内存:"+allvalue);
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
								   		memorycollectdata.setUnit("");		
								   		memorycollectdata.setThevalue(intvalue+"");
										SysLogger.info(node.getIpAddress()+" 内存： "+Integer.parseInt(intvalue+""));
										memoryVector.addElement(memorycollectdata);
									}
						   	  }
//						   	if(flag > 0)usedvalueperc = allvalue/flag;
//						   	//内存
//					   		Memorycollectdata memorycollectdata = new Memorycollectdata();
//					   		memorycollectdata.setIpaddress(node.getIpAddress());
//					   		memorycollectdata.setCollecttime(date);
//					   		memorycollectdata.setCategory("Memory");
//					   		memorycollectdata.setEntity("Utilization");
//					   		memorycollectdata.setSubentity("Utilization");
//					   		memorycollectdata.setRestype("dynamic");
//					   		memorycollectdata.setUnit("");		
//					   		memorycollectdata.setThevalue(usedvalueperc+"");
//							//SysLogger.info(host.getIpAddress()+" 内存： "+Integer.parseInt(usedvalueperc+""));
//							memoryVector.addElement(memorycollectdata);
						}	   			  
		   		  } 
		   	  }
		   	  catch(Exception e)
		   	  {
		   	  }	   	  
		   	  //-------------------------------------------------------------------------------------------内存 end
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
			if(memoryVector != null && memoryVector.size()>0)ipAllData.put("memory",memoryVector);
		    ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		}else{
			if(memoryVector != null && memoryVector.size()>0)((Hashtable)ShareData.getSharedata().get(node.getIpAddress())).put("memory",memoryVector);
		   
		 }  	    
	    returnHash.put("memory",memoryVector);
	    
	    Hashtable collectHash = new Hashtable();
		collectHash.put("memory", memoryVector);

	    try {
	    	if(memoryVector != null && memoryVector.size() > 0){
				int thevalue = 0;
				for(int i = 0 ; i < memoryVector.size(); i++){
					Memorycollectdata memorycollectdata = (Memorycollectdata)memoryVector.get(i);
					if("Utilization".equals(memorycollectdata.getEntity())){
						if(Integer.parseInt(memorycollectdata.getThevalue())>thevalue){
							thevalue = Integer.parseInt(memorycollectdata.getThevalue());
						}
					}
				}
				CheckEventUtil checkutil = new CheckEventUtil();
				checkutil.updateData(node, nodeGatherIndicators, thevalue+"",null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			SysLogger.error("dlink 内存 告警出错" , e);
		}

	    memoryVector=null;
	    //把采集结果生成sql
	    NetmemoryResultTosql tosql=new NetmemoryResultTosql();
	    tosql.CreateResultTosql(returnHash, node.getIpAddress());
	    String runmodel = PollingEngine.getCollectwebflag();//采集与访问模式
	    if(!"0".equals(runmodel)){
			//采集与访问是分离模式,则不需要将监视数据写入临时表格
		    NetHostMemoryRtsql  totempsql=new NetHostMemoryRtsql();
		    totempsql.CreateResultTosql(returnHash, node);
	    }

	    return returnHash;
	}
}





