package com.afunms.polling.snmp.temperature;

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
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.Interfacecollectdata;
import com.afunms.system.util.TimeGratherConfigUtil;
import com.afunms.topology.model.HostNode;
import com.gatherResulttosql.NetDatatempTemperatureRtosql;
import com.gatherResulttosql.NetTemperatureResultTosql;


/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class BrocadeTemperatureSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 
	 */
	public BrocadeTemperatureSnmp() {
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
		Vector temperatureVector=new Vector();
		Vector alarmVector=new Vector();
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
				  //-------------------------------------------------------------------------------------------温度 start
		   		  String temp = "0";
		   		  //if(host.getSysOid().startsWith("1.3.6.1.4.1.9.")){
		   			String[][] valueArray = null;
		   			String[] oids =                
						  new String[] {               
							"1.3.6.1.4.1.1588.2.1.1.1.1.22.1.1",//swSensorIndex   温度、风扇、电源
							"1.3.6.1.4.1.1588.2.1.1.1.1.22.1.2",//swSensorType    类别  temperature(1),fan(2),power-supply(3)
							"1.3.6.1.4.1.1588.2.1.1.1.1.22.1.3",//swSensorStatus  状态
							"1.3.6.1.4.1.1588.2.1.1.1.1.22.1.4",//swSensorValue   值
							"1.3.6.1.4.1.1588.2.1.1.1.1.22.1.5",//swSensorInfo
		   			};
		   			//valueArray = SnmpUtils.getTemperatureTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), 3, 1000*30);
		   			valueArray = SnmpUtils.getTemperatureTableData(node.getIpAddress(),node.getCommunity(),oids,node.getSnmpversion(),
		   		  			node.getSecuritylevel(),node.getSecurityName(),node.getV3_ap(),node.getAuthpassphrase(),node.getV3_privacy(),node.getPrivacyPassphrase(),3,1000*30);
		   			int allvalue=0;
		   			int flag = 0;
		   			int result = 0;
		   			if(valueArray != null){
					   	  for(int i=0;i<valueArray.length;i++)
					   	  {
//					   		String _value = valueArray[i][1];
//					   		String index = valueArray[i][2];
//					   		String desc = valueArray[i][0];
					   		String swSensorIndex = valueArray[i][0];
					   		String swSensorType = valueArray[i][1];
					   		String swSensorStatus = valueArray[i][2];
					   		String swSensorValue = valueArray[i][3];
					   		String swSensorInfo = valueArray[i][4];
						   	String index = valueArray[i][5];
					   		int value=0;
							if(swSensorValue != null){
								value=Integer.parseInt(swSensorValue);
								if(value > 0 && "1".equals(swSensorType)){// 1-5 为温度
								flag = flag +1;
						   		//内存
						   		//temperatureList.add(alist);
									if(swSensorStatus.equals("1")){//unknown
										swSensorStatus = "未知";
									}else if(swSensorStatus.equals("2")){//faulty
										swSensorStatus = "错误";
									}else if(swSensorStatus.equals("3")){//below-min
										swSensorStatus = "低于最小值";
									}else if(swSensorStatus.equals("4")){//nominal
										swSensorStatus = "正常";
									}else if(swSensorStatus.equals("5")){//above-max
										swSensorStatus = "超过最大值";
									}else if(swSensorStatus.equals("6")){//absent
										swSensorStatus = "缺失";
									}
						   		  interfacedata = new Interfacecollectdata();
						   		  interfacedata.setIpaddress(node.getIpAddress());
						   		  interfacedata.setCollecttime(date);
						   		  interfacedata.setCategory("Temperature");
						   		  interfacedata.setEntity(index);
						   		  interfacedata.setSubentity(swSensorInfo);
						   		  interfacedata.setRestype("dynamic");
						   		  interfacedata.setUnit("度");		
						   		  interfacedata.setThevalue(value+"");
						   		  interfacedata.setBak(swSensorStatus);
								  SysLogger.info(node.getIpAddress()+"index:"+index+"   温度： "+value);
								  temperatureVector.addElement(interfacedata);	
								}
							}
					   		SysLogger.info(node.getIpAddress()+"  index:"+swSensorIndex+"  swSensorType:"+swSensorType+ "  value:"+value+ "  swSensorStatus="+swSensorStatus);

					   	  }
							////////////////////begin:告警的数据求平均值/////////////
							if(flag >0){
								int intvalue = (allvalue/flag);
								temp = intvalue+"";
								//SysLogger.info(host.getIpAddress()+" cpu "+allvalue/flag);
							}
							if(temp == null){
					   			  result = 0;
					   		  }else{
					   			  try{
					   				  if(temp.equalsIgnoreCase("noSuchObject")){
					   					result = 0;
					   				  }else
					   					  result = Integer.parseInt(temp); 
					   			  }catch(Exception ex){
					   				  ex.printStackTrace();
					   				  result = 0;
					   			  }
					   		  }
							 interfacedata = new Interfacecollectdata();
					   		  interfacedata.setIpaddress(node.getIpAddress());
					   		  interfacedata.setCollecttime(date);
					   		  interfacedata.setCategory("Temperature");
					   		  interfacedata.setEntity("");
					   		  interfacedata.setSubentity("");
					   		  interfacedata.setRestype("dynamic");
					   		  interfacedata.setUnit("度");		
					   		  interfacedata.setThevalue(result+"");
							  alarmVector.add(0, interfacedata);
			               ////////////////////end/////////////
					}
		   		  //} 
				  //cpuVector.add(3, temperatureList);
		   	  }
		   	  catch(Exception e)
		   	  {
		   	  }	   	  
		   	  //-------------------------------------------------------------------------------------------温度 end
			}catch(Exception e){
			}
		
			if(!(ShareData.getSharedata().containsKey(node.getIpAddress())))
			{
				Hashtable ipAllData = new Hashtable();
				if(ipAllData == null)ipAllData = new Hashtable();
				if(temperatureVector != null && temperatureVector.size()>0)ipAllData.put("temperature",temperatureVector);
			    ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
			}else
			 {
				if(temperatureVector != null && temperatureVector.size()>0)((Hashtable)ShareData.getSharedata().get(node.getIpAddress())).put("temperature",temperatureVector);
			 }
			
//		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(node.getIpAddress());
//		if(ipAllData == null)ipAllData = new Hashtable();
//		ipAllData.put("temperature",temperatureVector);
//	    ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
	    returnHash.put("temperature", temperatureVector);
	  //对温度进行告警检测
	    Hashtable collectHash = new Hashtable();
		collectHash.put("temperature", temperatureVector);
	    try{
			AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(node.getId()), AlarmConstant.TYPE_NET, "brocade","temperature");
			
			for(int i = 0 ; i < list.size() ; i ++){
				 
				AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode)list.get(i);
				//对温度值进行告警检测
				CheckEventUtil checkutil = new CheckEventUtil();
				checkutil.updateData(node,collectHash,"net","temperature",alarmIndicatorsnode);
				//}
			}
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
	    //ipAllData=null;
	    temperatureVector=null;
	    
	    //把采集结果生成sql
	    NetTemperatureResultTosql tosql=new NetTemperatureResultTosql();
	    tosql.CreateResultTosql(returnHash, node.getIpAddress());
	    String runmodel = PollingEngine.getCollectwebflag();//采集与访问模式
	    if(!"0".equals(runmodel)){
			//采集与访问是分离模式,则不需要将监视数据写入临时表格
	    	NetDatatempTemperatureRtosql temptosql=new NetDatatempTemperatureRtosql();
		    temptosql.CreateResultTosql(returnHash, node);
	    }
	    
	    
	    return returnHash;
	}
}





