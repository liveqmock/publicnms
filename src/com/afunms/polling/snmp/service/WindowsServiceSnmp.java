package com.afunms.polling.snmp.service;

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
import com.afunms.alarm.send.SendMailAlarm;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.common.util.SysLogger;
import com.afunms.event.model.EventList;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.Servicecollectdata;
import com.afunms.system.util.TimeGratherConfigUtil;
import com.afunms.topology.model.HostNode;
import com.gatherResulttosql.HostDatatempserciceRttosql;


/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class WindowsServiceSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 
	 */
	public WindowsServiceSnmp() {
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
		Vector serviceVector=new Vector();
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
			Servicecollectdata servicedata=null;
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
			//--------------------------------------------------------------------------------------------service start
			   try{
					  String[] oids =                
						  new String[] {               
							  "1.3.6.1.4.1.77.1.2.3.1.1",  //名称
							  "1.3.6.1.4.1.77.1.2.3.1.2",  
							  "1.3.6.1.4.1.77.1.2.3.1.3",   
							  "1.3.6.1.4.1.77.1.2.3.1.4",
							  "1.3.6.1.4.1.77.1.2.3.1.5" };   
					  String[][] valueArray = null;   	  
						try {
							//valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), 3, 1000*30);
							valueArray = SnmpUtils.getTableData(node.getIpAddress(),node.getCommunity(),oids,node.getSnmpversion(),
				   		  			node.getSecuritylevel(),node.getSecurityName(),node.getV3_ap(),node.getAuthpassphrase(),node.getV3_privacy(),node.getPrivacyPassphrase(),3,1000*30);
						} catch(Exception e){
							valueArray = null;
							//SysLogger.error(node.getIpAddress() + "_WindowsSnmp");
						}	
						for(int i=0;i<valueArray.length;i++){
							servicedata = new Servicecollectdata();
							String vbstring0 = valueArray[i][0];
							String vbstring1 = valueArray[i][1];
							if(vbstring1.equalsIgnoreCase("1")){
								vbstring1 = "已卸载";
							}else if(vbstring1.equalsIgnoreCase("2")){
								vbstring1 = "安装待批";
							}else if(vbstring1.equalsIgnoreCase("3")){
								vbstring1 = "卸载待批";
							}else{
								vbstring1 = "已安装";
							}
							String vbstring2 = valueArray[i][2];
							if(vbstring2.equalsIgnoreCase("1")){
								vbstring2 = "活动的";
							}else if(vbstring2.equalsIgnoreCase("2")){
								vbstring2 = "活动待批";
							}else if(vbstring2.equalsIgnoreCase("3")){
								vbstring2 = "暂停待批";
							}else{
								vbstring2 = "暂停的";
							}
						    String vbstring3 = valueArray[i][3];
						    if(vbstring3.equalsIgnoreCase("1")){
						    	vbstring3 = "不能卸载";
							}else{
								vbstring3 = "允许卸载";
							}
						    String vbstring4 = valueArray[i][4];
						    if(vbstring4.equalsIgnoreCase("1")){
						    	vbstring4 = "不能暂停";
							}else{
								vbstring4 = "允许暂停";
							}
						    servicedata.setIpaddress(node.getIpAddress());
						    servicedata.setName(vbstring0);
						    servicedata.setInstate(vbstring1);
						    servicedata.setOpstate(vbstring2);
						    servicedata.setUninst(vbstring3);
						    servicedata.setPaused(vbstring4);
						    serviceVector.addElement(servicedata);
						}
				   }catch(Exception e){
						  //e.printStackTrace();
					}
				   //--------------------------------------------------------------------------------------------service start	
			}catch(Exception e){
				//returnHash=null;
				//e.printStackTrace();
				//return null;
			}
		
			if(!(ShareData.getSharedata().containsKey(node.getIpAddress())))
			{
				Hashtable ipAllData = new Hashtable();
				if(ipAllData == null)ipAllData = new Hashtable();
				if(serviceVector != null && serviceVector.size()>0)ipAllData.put("winservice",serviceVector);
			    ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
			}else
			 {
				if(serviceVector != null && serviceVector.size()>0)((Hashtable)ShareData.getSharedata().get(node.getIpAddress())).put("winservice",serviceVector);
			 }
			
//		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(node.getIpAddress());
//		if(ipAllData == null)ipAllData = new Hashtable();
//		ipAllData.put("winservice",serviceVector);
//	    ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
	    returnHash.put("winservice", serviceVector);
	    //SysLogger.info("采集到的服务数目为:"+serviceVector.size());
	    
	    
        List hostServiceEventList = new ArrayList();       	
        Hashtable sharedata = ShareData.getSharedata();
		Hashtable datahash = (Hashtable)sharedata.get(node.getIpAddress());
		//service
		Vector winserviceVector = null;
		boolean alarm = false;
		if (datahash != null && datahash.size() > 0){
			winserviceVector = (Vector) datahash.get("winservice");
		}
		
		/*
		 * nielin add 2010-08-18
		 *
		 * 创建服务组告警
		 * 
		 * start ===============================
		 */
		try{
			if(winserviceVector != null && winserviceVector.size()>0){
				AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
				List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(node.getId()+"", "host", "windows");
				AlarmIndicatorsNode alarmIndicatorsNode2 = null;
				if(list != null){
					for(int i = 0 ; i < list.size() ; i++){
						AlarmIndicatorsNode alarmIndicatorsNode2_per = (AlarmIndicatorsNode)list.get(i);
						if(alarmIndicatorsNode2_per!=null && "service".equals(alarmIndicatorsNode2_per.getName())){
							alarmIndicatorsNode2 = alarmIndicatorsNode2_per;
							break;
						}
						
					}
					CheckEventUtil checkutil = new CheckEventUtil();
					hostServiceEventList = checkutil.createHostServiceGroupEventList(node.getIpAddress() , winserviceVector, alarmIndicatorsNode2);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		/*
		 * nielin add 2010-08-18
		 *
		 * 创建服务组告警
		 * 
		 * end ===============================
		 */
	    if(hostServiceEventList != null && hostServiceEventList.size()>0){
	    	alarm = true;
	    }
		if(alarm)
		{	
			Host host = (Host) PollingEngine.getInstance().getNodeByID(node.getId());
			String userids = node.getSendemail();
			StringBuffer msg = new StringBuffer(200);
		    msg.append("<font color='red'>--报警信息:--</font><br>");
		    msg.append(node.getAlarmMessage().toString());
		    if(hostServiceEventList != null && hostServiceEventList.size()>0){
	        	for(int i=0;i<hostServiceEventList.size();i++){
	        		EventList eventList = (EventList)hostServiceEventList.get(i);
	        		msg.append(eventList.getContent()+"<br>");
	        		if(eventList.getLevel1() > node.getAlarmlevel()){
	        			node.setAlarmlevel(eventList.getLevel1()) ;
		    		}
	        		//进行邮件告警
	        		if(userids != null && userids.trim().length()>0){
	        			SendMailAlarm sendMailAlarm = new SendMailAlarm();
		        		sendMailAlarm.sendAlarm(eventList, userids);
	        		}
	        	}
	        }
		    host.getAlarmMessage().clear();
		    host.getAlarmMessage().add(msg.toString());
		    host.setStatus(node.getAlarmlevel());
		    host.setAlarm(true);
		}
		
		
		
//		ipAllData=null;
		sharedata=null;
		datahash=null;
		
		String runmodel = PollingEngine.getCollectwebflag();//采集与访问模式
	    if(!"0".equals(runmodel)){
			//采集与访问是分离模式,则不需要将监视数据写入临时表格
	    	//把sql生成sql
			HostDatatempserciceRttosql totempsql=new HostDatatempserciceRttosql();
			totempsql.CreateResultTosql(returnHash, node);
	    }
	    
	    return returnHash;
	}
}





