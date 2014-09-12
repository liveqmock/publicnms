package com.afunms.polling.snmp.sla;

/*
 * @author hukelei@dhcc.com.cn
 *
 */

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import montnets.SmsDao;

import com.afunms.alarm.send.SendMailAlarm;
import com.afunms.application.model.SlaNodeConfig;
import com.afunms.common.util.Arith;
import com.afunms.common.util.NodeAlarmUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.common.util.SysLogger;
import com.afunms.config.model.Huaweitelnetconf;
import com.afunms.config.model.Portconfig;
import com.afunms.event.dao.EventListDao;
import com.afunms.event.dao.SmscontentDao;
import com.afunms.event.model.EventList;
import com.afunms.event.model.Smscontent;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostLastCollectData;
import com.afunms.polling.base.Node;
import com.afunms.polling.impl.HostLastCollectDataManager;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.AllUtilHdx;
import com.afunms.polling.om.DiscardsPerc;
import com.afunms.polling.om.ErrorsPerc;
import com.afunms.polling.om.InPkts;
import com.afunms.polling.om.Interfacecollectdata;
import com.afunms.polling.om.OutPkts;
import com.afunms.polling.om.Packs;
import com.afunms.polling.om.Pingcollectdata;
import com.afunms.polling.om.Task;
import com.afunms.polling.om.UtilHdx;
import com.afunms.polling.om.UtilHdxPerc;
import com.afunms.polling.snmp.SnmpMibConstants;
import com.afunms.polling.task.TaskXml;
import com.afunms.system.util.TimeGratherConfigUtil;
import com.afunms.topology.model.HostNode;
import com.gatherResulttosql.NetInterfaceDataTemptosql;
import com.gatherResulttosql.NetinterfaceResultTosql;


/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ICMPSnmp extends SnmpMonitor {
	
	private static Hashtable ifEntity_ifStatus = null;
	static {
		ifEntity_ifStatus = new Hashtable();
		ifEntity_ifStatus.put("1", "up");
		ifEntity_ifStatus.put("2", "down");
		ifEntity_ifStatus.put("3", "testing");
		ifEntity_ifStatus.put("5", "unknow");
		ifEntity_ifStatus.put("7", "unknow");
	};
	
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 
	 */
	public ICMPSnmp() {
	}

	   public void collectData(Node node,MonitoredItem item){
		   
	   }
	   public void collectData(HostNode node){
		   
	   }
	public Hashtable collect_Data(SlaNodeConfig vo,Huaweitelnetconf telnetconfig) {
			
			Hashtable returnHash=new Hashtable();
			Host host = (Host)PollingEngine.getInstance().getNodeByIP(telnetconfig.getIpaddress());
			if(host == null)return returnHash;
			//判断是否在采集时间段内
	    	if(ShareData.getTimegatherhash() != null){
	    		if(ShareData.getTimegatherhash().containsKey(host.getId()+":equipment")){
	    			TimeGratherConfigUtil timeconfig = new TimeGratherConfigUtil();
	    			int _result = 0;
	    			_result = timeconfig.isBetween((List)ShareData.getTimegatherhash().get(host.getId()+":equipment"));
	    			if(_result ==1 ){
	    				//SysLogger.info("########时间段内: 开始采集 "+node.getIpAddress()+" PING数据信息##########");
	    			}else if(_result == 2){
	    				//SysLogger.info("########全天: 开始采集 "+node.getIpAddress()+" PING数据信息##########");
	    			}else {
	    				SysLogger.info("######## "+host.getIpAddress()+" 不在采集ICMPSnmp时间段内,退出##########");
//	    				//清除之前内存中产生的告警信息
//	    			    try{
//	    			    	//清除之前内存中产生的内存告警信息
//							CheckEventUtil checkutil = new CheckEventUtil();
//							checkutil.deleteEvent(node.getId()+":host:diskperc");
//							checkutil.deleteEvent(node.getId()+":host:diskinc");
//	    			    }catch(Exception e){
//	    			    	e.printStackTrace();
//	    			    }
	    				return returnHash;
	    			}
	    			
	    		}
	    	}
			try {
				Interfacecollectdata interfacedata=null;
				UtilHdx utilhdx=new UtilHdx();
				InPkts inpacks = new InPkts();
				OutPkts outpacks = new OutPkts();
				UtilHdxPerc utilhdxperc=new UtilHdxPerc();
				AllUtilHdx allutilhdx = new AllUtilHdx();
				Calendar date=Calendar.getInstance();
			
				try{
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					com.afunms.polling.base.Node snmpnode = (com.afunms.polling.base.Node)PollingEngine.getInstance().getNodeByIP(host.getIpAddress());
					Date cc = date.getTime();
					String time = sdf.format(cc);
					snmpnode.setLastTime(time);
				}catch(Exception e){
				  
				}			
			  try{
				I_HostLastCollectData lastCollectDataManager=new HostLastCollectDataManager();
				Hashtable hash=ShareData.getOctetsdata(host.getIpAddress());
				//取得轮询间隔时间
				TaskXml taskxml=new TaskXml();
				Task task=taskxml.GetXml("netcollecttask");
				int interval=getInterval(task.getPolltime().floatValue(),task.getPolltimeunit());
				Hashtable hashSpeed=new Hashtable();
				Hashtable octetsHash = new Hashtable();
				if (hash==null)hash=new Hashtable();					  
						
				String[] icmp_oids=                
					 new String[] {     	
						"1.3.6.1.4.1.9.9.42.1.2.10.1.2", //最后完成状态
						"1.3.6.1.4.1.9.9.42.1.2.10.1.1"//RTT值							
					};

				String[][] valueArrayICMP = null;   
				try {
					//SysLogger.info(host.getIpAddress()+"==="+host.getCommunity()+"===="+host.getSnmpversion());
					//valueArrayICMP = SnmpUtils.getTemperatureTableData(host.getIpAddress(), host.getCommunity(), icmp_oids, host.getSnmpversion(), 3, 1000*30);
					valueArrayICMP = SnmpUtils.getTemperatureTableData(host.getIpAddress(),host.getCommunity(),icmp_oids,host.getSnmpversion(),
							host.getSecuritylevel(),host.getSecurityName(),host.getV3_ap(),host.getAuthpassphrase(),host.getV3_privacy(),host.getPrivacyPassphrase(),3,1000*30);
					//valueArray = snmp.getTableData(host.getIpAddress(),host.getCommunity(),oids);
					
				} catch(Exception e){
				}
				Vector tempV=new Vector();
				Hashtable tempHash = new Hashtable();
				
				//开始测试ICMP
				if(valueArrayICMP != null){
					SysLogger.info(vo.getTelnetconfig_id()+"##############ICMP BEGIN");
					Hashtable dataHash = new Hashtable();
				   	  for(int i=0;i<valueArrayICMP.length;i++)
				   	  {		
				   		String RTT = valueArrayICMP[i][1];
				   		String index = valueArrayICMP[i][2];
				   		String RTT_Status = valueArrayICMP[i][0];
				   		Pingcollectdata hostdata = null;
						Calendar _date=Calendar.getInstance();
						if(!index.equalsIgnoreCase(vo.getEntrynumber()+""))continue;
						//RTT响应时间
						hostdata=new Pingcollectdata();
						hostdata.setIpaddress(telnetconfig.getIpaddress());
						hostdata.setCollecttime(date);
						hostdata.setCategory("Ping");
						hostdata.setEntity("ResponseTime");
						hostdata.setSubentity("ResponseTime");
						hostdata.setRestype("dynamic");
						hostdata.setUnit("毫秒");
						hostdata.setThevalue(RTT);
						dataHash.put(1, hostdata);
						
						//状态
						hostdata=new Pingcollectdata();
    					hostdata.setIpaddress(telnetconfig.getIpaddress());
    					hostdata.setCollecttime(date);
    					hostdata.setCategory("Ping");
    					hostdata.setEntity("Utilization");
    					hostdata.setSubentity("ConnectUtilization");
    					hostdata.setRestype("dynamic");
    					hostdata.setUnit("%");
						if("1".equalsIgnoreCase(RTT_Status)){
							hostdata.setThevalue("100");
						}else{
							hostdata.setThevalue("0");
						}
						dataHash.put(0, hostdata);						
						returnHash.put(vo.getId()+"", dataHash);
						ShareData.getSlaHash().put(vo.getId()+"", dataHash);				   		
				   		SysLogger.info(host.getIpAddress()+"===="+index+"====="+RTT_Status+"====="+RTT);
				   		break;				   		
				   	  }
				   	SysLogger.info(host.getIpAddress()+"####################ICMP END");
				}
				//结束测试ICMP								
			}catch(Exception e){e.printStackTrace();}
			}catch(Exception e){
			}finally{
				//System.gc();
			}  
//	    NetinterfaceResultTosql tosql =new NetinterfaceResultTosql();
//	    tosql.CreateResultTosql(returnHash, host.getIpAddress());
//	    NetInterfaceDataTemptosql datatemp=new NetInterfaceDataTemptosql();
//	    datatemp.CreateResultTosql(returnHash, host);
	    
	    
	    return returnHash;
	}
	
	public int getInterval(float d,String t){
		int interval=0;
		  if(t.equals("d"))
			 interval =(int) d*24*60*60; //天数
		  else if(t.equals("h"))
			 interval =(int) d*60*60;    //小时
		  else if(t.equals("m"))
			 interval = (int)d*60;       //分钟
		else if(t.equals("s"))
					 interval =(int) d;       //秒
		return interval;
	}
	
//	public void createSMS(String subtype,String subentity,String ipaddress,String objid,String content,int flag,int checkday,String sIndex,String bids){
//	 	//建立短信		 	
//	 	//从内存里获得当前这个IP的PING的值
//	 	Calendar date=Calendar.getInstance();
//	 	Hashtable sendeddata = ShareData.getSendeddata();
//	 	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//	 	//SysLogger.info("端口事件--------------------"+bids);
//	 	try{
// 			if (!sendeddata.containsKey(subtype+":"+subentity+":"+ipaddress+":"+sIndex)) {
// 				//若不在，则建立短信，并且添加到发送列表里
//	 			Smscontent smscontent = new Smscontent();
//	 			String time = sdf.format(date.getTime());
//	 			smscontent.setLevel(flag+"");
//	 			smscontent.setObjid(objid);
//	 			smscontent.setMessage(content);
//	 			smscontent.setRecordtime(time);
//	 			smscontent.setSubtype(subtype);
//	 			smscontent.setSubentity(subentity);
//	 			smscontent.setIp(ipaddress);
//	 			//发送短信
//	 			SmscontentDao smsmanager=new SmscontentDao();
//	 			smsmanager.sendURLSmscontent(smscontent);	
//				sendeddata.put(subtype+":"+subentity+":"+ipaddress+":"+sIndex,date);	
//				
// 			} else {
// 				//若在，则从已发送短信列表里判断是否已经发送当天的短信
// 				SmsDao smsDao = new SmsDao();
// 				List list = new ArrayList();
// 				String startTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " 00:00:00";
// 				String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
// 				try {
// 					list = smsDao.findByEvent(content,startTime,endTime);
//				} catch (RuntimeException e) {
//					e.printStackTrace();
//				} finally {
//					smsDao.close();
//				}
//				if(list!=null&&list.size()>0){//短信列表里已经发送当天的短信
//					Calendar formerdate =(Calendar)sendeddata.get(subtype+":"+subentity+":"+ipaddress+":"+sIndex);		 				
//		 			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//		 			Date last = null;
//		 			Date current = null;
//		 			Calendar sendcalen = formerdate;
//		 			Date cc = sendcalen.getTime();
//		 			String tempsenddate = formatter.format(cc);
//		 			
//		 			Calendar currentcalen = date;
//		 			Date ccc = currentcalen.getTime();
//		 			last = formatter.parse(tempsenddate);
//		 			String currentsenddate = formatter.format(ccc);
//		 			current = formatter.parse(currentsenddate);
//		 			
//		 			long subvalue = current.getTime()-last.getTime();	
//		 			if(checkday == 1){
//		 				//检查是否设置了当天发送限制,1为检查,0为不检查
//		 				if (subvalue/(1000*60*60*24)>=1){
//			 				//超过一天，则再发信息
//				 			Smscontent smscontent = new Smscontent();
//				 			String time = sdf.format(date.getTime());
//				 			smscontent.setLevel(flag+"");
//				 			smscontent.setObjid(objid);
//				 			smscontent.setMessage(content);
//				 			smscontent.setRecordtime(time);
//				 			smscontent.setSubtype(subtype);
//				 			smscontent.setSubentity(subentity);
//				 			smscontent.setIp(ipaddress);//发送短信
//				 			SmscontentDao smsmanager=new SmscontentDao();
//				 			smsmanager.sendURLSmscontent(smscontent);
//							//修改已经发送的短信记录	
//							sendeddata.put(subtype+":"+subentity+":"+ipaddress+":"+sIndex,date);
//				 		} else {
//	                        //开始写事件
//			 	            String sysLocation = "";
//			 				createEvent("poll",sysLocation,bids,content,flag,subtype,subentity,ipaddress,objid);
//				 		}
//		 			}
//				} else {
// 					Smscontent smscontent = new Smscontent();
// 		 			String time = sdf.format(date.getTime());
// 		 			smscontent.setLevel(flag+"");
// 		 			smscontent.setObjid(objid);
// 		 			smscontent.setMessage(content);
// 		 			smscontent.setRecordtime(time);
// 		 			smscontent.setSubtype(subtype);
// 		 			smscontent.setSubentity(subentity);
// 		 			smscontent.setIp(ipaddress);
// 		 			//发送短信
// 		 			SmscontentDao smsmanager=new SmscontentDao();
// 		 			smsmanager.sendURLSmscontent(smscontent);	
// 					sendeddata.put(subtype+":"+subentity+":"+ipaddress+":"+sIndex,date);
// 				}
// 				
// 			}	 			 			 			 			 	
//	 	}catch(Exception e){
//	 		e.printStackTrace();
//	 	}
//	 }
	
//	private void createEvent(String eventtype,String eventlocation,String bid,String content,int level1,String subtype,String subentity,String ipaddress,String objid){
//		//生成事件
//		SysLogger.info("##############开始生成事件############");
//		EventList eventlist = new EventList();
//		eventlist.setEventtype(eventtype);
//		eventlist.setEventlocation(eventlocation);
//		eventlist.setContent(content);
//		eventlist.setLevel1(level1);
//		eventlist.setManagesign(0);
//		eventlist.setBak("");
//		eventlist.setRecordtime(Calendar.getInstance());
//		eventlist.setReportman("系统轮询");
//		//SysLogger.info("bid============="+bid);
//		eventlist.setBusinessid(bid);
//		eventlist.setNodeid(Integer.parseInt(objid));
//		eventlist.setOid(0);
//		eventlist.setSubtype(subtype);
//		eventlist.setSubentity(subentity);
//		EventListDao eventlistdao = new EventListDao();
//		try{
//			eventlistdao.save(eventlist);
//		}catch(Exception e){
//			e.printStackTrace();
//		}finally{
//			eventlistdao.close();
//		}
//	}
}





