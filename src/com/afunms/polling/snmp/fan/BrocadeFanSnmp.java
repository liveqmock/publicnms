package com.afunms.polling.snmp.fan;

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
import com.afunms.common.util.AlarmHelper;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.common.util.SysLogger;
import com.afunms.config.model.EnvConfig;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.Interfacecollectdata;
import com.afunms.system.util.TimeGratherConfigUtil;
import com.afunms.topology.model.HostNode;
import com.gatherResulttosql.NetDatatempfanRtosql;
import com.gatherResulttosql.NetfanResultTosql;


/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class BrocadeFanSnmp extends SnmpMonitor {
	private Hashtable sendeddata = ShareData.getProcsendeddata();
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 
	 */
	public BrocadeFanSnmp() {
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
		Vector fanVector=new Vector();
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
//					-------------------------------------------------------------------------------------------风扇 start
		   		  String temp = "0";
		   		  if(node.getSysOid().startsWith("1.3.6.1.4.1.1588.2.1.1.")){
		   			String[][] valueArray = null;
//		   			String[] oids =                
//						  new String[] {               
//							"1.3.6.1.4.1.9.9.13.1.4.1.2",//描述
//							"1.3.6.1.4.1.9.9.13.1.4.1.3"//状态
//		   			};
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
		   			if(valueArray != null){
					   	  for(int i=0;i<valueArray.length;i++)
					   	  {
					   		String swSensorIndex = valueArray[i][0];
					   		String swSensorType = valueArray[i][1];
					   		String swSensorStatus = valueArray[i][2];
					   		String swSensorValue = valueArray[i][3];
					   		String swSensorInfo = valueArray[i][4];
						   	String index = valueArray[i][5];
					   		int value=0;
							if(swSensorValue != null){
								value=Integer.parseInt(swSensorValue);
								if(value > 0 && "2".equals(swSensorType)){// 1-5 为温度
						   		//风扇
						   		//fanList.add(alist);			
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
						   		  interfacedata.setCategory("Fan");
						   		  interfacedata.setEntity(index);
						   		  interfacedata.setSubentity(swSensorInfo);
						   		  interfacedata.setRestype("dynamic");
						   		  interfacedata.setUnit("");		
						   		  interfacedata.setThevalue(swSensorValue);
						   		  interfacedata.setBak(swSensorStatus);
								  SysLogger.info(node.getIpAddress()+"风扇:"+swSensorInfo+" value： "+swSensorValue);
								  fanVector.addElement(interfacedata);	
								}
							}
//					   		SysLogger.info(node.getIpAddress()+"  "+swSensorIndex+"   风扇状态value="+value);
					   	  }
					}
		   		  } 
				  //fanVector.add(fanList);
		   	  }
		   	  catch(Exception e)
		   	  {
		   	  }	   	  
		   	  //-------------------------------------------------------------------------------------------风扇 end
			}catch(Exception e){
			}
		
//		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(node.getIpAddress());
//		if(ipAllData == null)ipAllData = new Hashtable();
//		ipAllData.put("fan",fanVector);
//	    ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
//	    returnHash.put("fan", fanVector);
	    
	    
	    if(!(ShareData.getSharedata().containsKey(node.getIpAddress())))
		{
			Hashtable ipAllData = new Hashtable();
			if(ipAllData == null)ipAllData = new Hashtable();
			if(fanVector != null && fanVector.size()>0)ipAllData.put("fan",fanVector);
		    ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		}else
		 {
			if(fanVector != null && fanVector.size()>0)((Hashtable)ShareData.getSharedata().get(node.getIpAddress())).put("fan",fanVector);
		   
		 }
	    returnHash.put("fan", fanVector);
	    
	    
	    try{
			AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(node.getId()), AlarmConstant.TYPE_NET, "brocade","fan");
			

			AlarmHelper helper=new AlarmHelper();
			Hashtable<String, EnvConfig> envHashtable=helper.getAlarmConfig(node.getIpAddress(), "Fan");
			for(int i = 0 ; i < list.size() ; i ++){
				 
				AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode)list.get(i);
				
				//对风扇值进行告警检测
				CheckEventUtil checkutil = new CheckEventUtil();
				if (fanVector.size()>0) {
					for (int j = 0; j < fanVector.size(); j++) {
						Interfacecollectdata data=(Interfacecollectdata)fanVector.get(j);
						if (data!=null) {
							EnvConfig config=envHashtable.get(data.getEntity());
							if(config!=null&&config.getEnabled()==1){
								alarmIndicatorsnode.setAlarm_level(config.getAlarmlevel());
								alarmIndicatorsnode.setAlarm_times(config.getAlarmtimes()+"");
								alarmIndicatorsnode.setLimenvalue0(config.getAlarmvalue()+"");
							   checkutil.checkEvent(node,alarmIndicatorsnode,data.getThevalue(),data.getSubentity());
							  
							}
							}
						
					}
					
				}
				
			}
			
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
	    //把采集结果生成sql
	    NetfanResultTosql tosql=new NetfanResultTosql();
	    tosql.CreateResultTosql(returnHash, node.getIpAddress());
	    String runmodel = PollingEngine.getCollectwebflag();//采集与访问模式
	    if(!"0".equals(runmodel)){
			//采集与访问是分离模式,则不需要将监视数据写入临时表格
	    	NetDatatempfanRtosql totempsql=new NetDatatempfanRtosql();
		    totempsql.CreateResultTosql(returnHash, node);
	    }
	    
	    
	    
	    return returnHash;
	}
}





