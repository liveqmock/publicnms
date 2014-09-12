/**
 * <p>Description:logger,writes error and debug information within system running</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-06
 */

package com.afunms.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.alarm.dao.SendAlarmTimeDao;
import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.model.AlarmPort;
import com.afunms.alarm.send.SendAlarmUtil;
import com.afunms.alarm.util.AlarmConstant;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.alarm.util.AlarmResourceCenter;
import com.afunms.application.dao.TracertsDao;
import com.afunms.application.dao.TracertsDetailDao;
import com.afunms.application.model.HostServiceGroup;
import com.afunms.application.model.HostServiceGroupConfiguration;
import com.afunms.application.model.JobForAS400Group;
import com.afunms.application.model.JobForAS400GroupDetail;
import com.afunms.application.model.JobForAS400SubSystem;
import com.afunms.application.model.ProcessGroup;
import com.afunms.application.model.ProcessGroupConfiguration;
import com.afunms.application.model.SystemFlag;
import com.afunms.application.model.Tracerts;
import com.afunms.application.model.TracertsDetail;
import com.afunms.application.util.HostServiceGroupConfigurationUtil;
import com.afunms.application.util.JobForAS400GroupDetailUtil;
import com.afunms.application.util.ProcessGroupConfigurationUtil;
import com.afunms.common.base.BaseVo;
import com.afunms.config.dao.AclBaseDao;
import com.afunms.config.model.AclDetail;
import com.afunms.config.model.Diskconfig;
import com.afunms.config.model.PolicyInterface;
import com.afunms.config.model.QueueInfo;
import com.afunms.event.dao.CheckEventDao;
import com.afunms.event.dao.EventListDao;
import com.afunms.event.model.CheckEvent;
import com.afunms.event.model.EventList;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.LinkRoad;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.DHCP;
import com.afunms.polling.node.Ftp;
import com.afunms.polling.node.Host;
import com.afunms.polling.node.Mail;
import com.afunms.polling.node.TFtp;
import com.afunms.polling.node.Web;
import com.afunms.polling.om.AllUtilHdx;
import com.afunms.polling.om.CPUcollectdata;
import com.afunms.polling.om.Diskcollectdata;
import com.afunms.polling.om.Interfacecollectdata;
import com.afunms.polling.om.Memorycollectdata;
import com.afunms.polling.om.Pingcollectdata;
import com.afunms.polling.om.Processcollectdata;
import com.afunms.polling.om.Servicecollectdata;
import com.afunms.polling.om.UtilHdx;
import com.afunms.toolService.traceroute.TraceRouteExecute;
import com.afunms.topology.dao.EquipImageDao;
import com.afunms.topology.dao.ManageXmlDao;
import com.afunms.topology.dao.NodeEquipDao;
import com.afunms.topology.model.EquipImage;
import com.afunms.topology.model.JobForAS400;
import com.afunms.topology.model.ManageXml;
import com.afunms.topology.model.NodeEquip;
import com.afunms.topology.model.SubsystemForAS400;
import com.afunms.topology.util.ManageXmlOperator;
import com.afunms.topology.util.NodeHelper;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CheckEventUtil {
         
        public CheckEventUtil(){
        }
         
        /**
         * CPU,MEMORY,流量等信息检测
         */
        public void updateData(Object vo , Object collectingData,String type,String subtype,AlarmIndicatorsNode alarmIndicatorsNode){
        	//SysLogger.info(alarmIndicatorsNode.getName() + "===========CPU,MEMORY,流量等信息检测=================");
            try {				
            	Node node = (Node)vo;
				//将内存中相关的告警清除,最终实现从数据库表里删除相关数据 
            	//告警删除的代码已注释  同一类告警， 只告警一次 ，并且恢复正常后有提示信息
 //				deleteEvent(node.getId()+":"+alarmIndicatorsNode.getType()+":"+alarmIndicatorsNode.getName());
				Hashtable datahashtable = (Hashtable)collectingData;
      			//获取JOB,判断JOB数量
      			List joblist = new ArrayList();
      			//获取系统信息,得到CPU利用率
      			Hashtable systemStatushashtable = new Hashtable();
				if("1".equals(alarmIndicatorsNode.getEnabled())){
					String indicators = alarmIndicatorsNode.getName();
					String value = "0";
					String limvalue = "";
					if("jobnumber".equals(indicators)){
						if(datahashtable.get("Jobs") != null)joblist = (List)datahashtable.get("Jobs");
						if(joblist == null)joblist = new ArrayList();
						value = joblist.size()+"";
					}else if("cpu".equals(indicators)){
						//CUP利用率
						Vector cpuVector = new Vector();
						if(datahashtable.get("cpu") != null)cpuVector = (Vector)datahashtable.get("cpu");
						if(cpuVector != null && cpuVector.size()>0){
							
							CPUcollectdata cpudata=(CPUcollectdata)cpuVector.get(0);
							value = cpudata.getThevalue();
						}
						if(systemStatushashtable.get("cpu")!= null)value = (String)systemStatushashtable.get("cpu");
					}else if("memory".equals(indicators)){
						//memory利用率
						Vector memoryVector = new Vector();
						if(datahashtable.get("memory") != null)memoryVector = (Vector)datahashtable.get("memory");
						if(memoryVector != null && memoryVector.size()>0){
							Memorycollectdata memorydata=(Memorycollectdata)memoryVector.get(0);
							value = memorydata.getThevalue();
						}
						if(systemStatushashtable.get("memory")!= null)value = (String)systemStatushashtable.get("memory");
					}else if("pagingusage".equals(indicators)){
						//换页率利用率
						Hashtable paginghash = new Hashtable();
						if(datahashtable.get("pagingusage") != null)paginghash = (Hashtable)datahashtable.get("pagingusage");
						if(paginghash != null && paginghash.size()>0){
							if(paginghash.get("Percent_Used") != null){
								value=((String)paginghash.get("Percent_Used")).replaceAll("%", "");
							}
							
						}
						if(systemStatushashtable.get("cpu")!= null)value = (String)systemStatushashtable.get("cpu");
					}else if("physicalmemory".equals(indicators)){
						//物理内存利用率
						Vector memoryVector = new Vector();
						if(datahashtable.get("physicalmem") != null)memoryVector = (Vector)datahashtable.get("physicalmem");
						if(memoryVector != null && memoryVector.size()>0){
							for(int i=0;i<memoryVector.size();i++){
								Memorycollectdata memorydata = (Memorycollectdata)memoryVector.get(i);
								//SysLogger.info("windows=========="+memorydata.getSubentity()+"==="+memorydata.getEntity()+"==="+memorydata.getThevalue());
								if("PhysicalMemory".equalsIgnoreCase(memorydata.getSubentity()) && memorydata.getEntity().equalsIgnoreCase("Utilization")){
									value = memorydata.getThevalue();
									break;
								}
							}
						}else{
							return;
						}
					}else if("virtualmemory".equals(indicators)){
						//物理内存利用率
						Vector memoryVector = new Vector();
						if(datahashtable.get("virtalmem") != null)memoryVector = (Vector)datahashtable.get("virtalmem");
						if(memoryVector != null && memoryVector.size()>0){
							for(int i=0;i<memoryVector.size();i++){
								Memorycollectdata memorydata = (Memorycollectdata)memoryVector.get(i);
								if("VirtualMemory".equalsIgnoreCase(memorydata.getSubentity()) && memorydata.getEntity().equalsIgnoreCase("Utilization")){
									value = memorydata.getThevalue();
									break;
								}
							}
						}else{
							return;
						}
					}else if("swapmemory".equals(indicators)){
						//物理内存利用率
						Vector memoryVector = new Vector();
						if(datahashtable.get("swapmem") != null)memoryVector = (Vector)datahashtable.get("swapmem");
						if(memoryVector != null && memoryVector.size()>0){
							for(int i=0;i<memoryVector.size();i++){
								Memorycollectdata memorydata = (Memorycollectdata)memoryVector.get(i);
								if(memorydata.getEntity().equalsIgnoreCase("Utilization")){
									value = memorydata.getThevalue();
									break;
								}
							}
						}else{
							return;
						}
					}else if("iowait".equals(indicators)){
						//io平均等待时间
						//Vector avwaitVector = new Vector();
						Hashtable avwaitVector = new Hashtable();
						if(datahashtable.get("vmstat") != null){
							        Hashtable avwaiths = (Hashtable)datahashtable.get("vmstat");
							        if(avwaiths!=null && avwaiths.size()>0){
									value = (String)avwaiths.get("iw");
							        }else{
							        	return;
							        }
						}else{
							return;
						}
					}else if("AllInBandwidthUtilHdx".equals(indicators) || "utilhdx".equals(indicators) 
							|| "AllOutBandwidthUtilHdx".equals(indicators)){
						
						//入口利用率
						
						Vector allutilVector = new Vector();
						if(datahashtable.get("allutilhdx") != null)allutilVector = (Vector)datahashtable.get("allutilhdx");
						
						if(allutilVector != null && allutilVector.size()>0){
							
							for(int i=0;i<allutilVector.size();i++){
								AllUtilHdx allutilhdx = (AllUtilHdx)allutilVector.get(i);
								
								 
								if(allutilhdx.getEntity().equalsIgnoreCase(indicators)){
									
									value = allutilhdx.getThevalue();
									//System.out.println("=========indicators============="+indicators);
									//System.out.println("==="+value);
									break;
								}
											
							}
							
							
						}else{
							return;
						}
					}else if("diskperc".equals(indicators)){
						return;
					}else if("send".equals(indicators)){
						//邮件发送服务
						Vector mailVector = new Vector();
						if(datahashtable.get("mail") != null)mailVector = (Vector)datahashtable.get("mail");
						if(mailVector != null && mailVector.size()>0){
							for(int i=0;i<mailVector.size();i++){
								Interfacecollectdata maildata = (Interfacecollectdata)mailVector.get(i);
								if(maildata.getEntity().equalsIgnoreCase("Send")){
									value = maildata.getThevalue();
									break;
								}
							}
						}else{
							return;
						}
					}else if("receieve".equalsIgnoreCase(indicators)){
						//邮件发送服务
						Vector mailVector = new Vector();
						if(datahashtable.get("mail") != null)mailVector = (Vector)datahashtable.get("mail");
						if(mailVector != null && mailVector.size()>0){
							for(int i=0;i<mailVector.size();i++){
								Interfacecollectdata maildata = (Interfacecollectdata)mailVector.get(i);
								if(maildata.getEntity().equalsIgnoreCase("receieve")){
									value = maildata.getThevalue();
									break;
								}
							}
						}else{
							return;
						}
					}else if("upload".equals(indicators)){
						//FTP或TFTP服务
						Vector ftpVector = new Vector();
						if("ftp".equalsIgnoreCase(subtype)){
							if(datahashtable.get("ftp") != null)ftpVector = (Vector)datahashtable.get("ftp");
							if(ftpVector != null && ftpVector.size()>0){
								for(int i=0;i<ftpVector.size();i++){
									Interfacecollectdata ftpdata = (Interfacecollectdata)ftpVector.get(i);
									if(ftpdata.getEntity().equalsIgnoreCase("upload")){
										value = ftpdata.getThevalue();
										break;
									}
								}
							}else{
								return;
							}
						}else if("tftp".equalsIgnoreCase(subtype)){
							if(datahashtable.get("tftp") != null)ftpVector = (Vector)datahashtable.get("tftp");
							if(ftpVector != null && ftpVector.size()>0){
								for(int i=0;i<ftpVector.size();i++){
									Interfacecollectdata ftpdata = (Interfacecollectdata)ftpVector.get(i);
									if(ftpdata.getEntity().equalsIgnoreCase("upload")){
										value = ftpdata.getThevalue();
										break;
									}
								}
							}else{
								return;
							}
						}
						
					}else if("download".equalsIgnoreCase(indicators)){
						//FTP或TFTP服务
						Vector ftpVector = new Vector();
						if("ftp".equalsIgnoreCase(subtype)){
							if(datahashtable.get("ftp") != null)ftpVector = (Vector)datahashtable.get("ftp");
							if(ftpVector != null && ftpVector.size()>0){
								for(int i=0;i<ftpVector.size();i++){
									Interfacecollectdata ftpdata = (Interfacecollectdata)ftpVector.get(i);
									if(ftpdata.getEntity().equalsIgnoreCase("download")){
										value = ftpdata.getThevalue();
										break;
									}
								}
							}else{
								return;
							}
						}else if("tftp".equalsIgnoreCase(subtype)){	
							if(datahashtable.get("tftp") != null)ftpVector = (Vector)datahashtable.get("tftp");
							if(ftpVector != null && ftpVector.size()>0){
								for(int i=0;i<ftpVector.size();i++){
									Interfacecollectdata ftpdata = (Interfacecollectdata)ftpVector.get(i);
									if(ftpdata.getEntity().equalsIgnoreCase("download")){
										value = ftpdata.getThevalue();
										break;
									}
								}
							}else{
								return;
							}
						}
						
					}else if("socketping".equalsIgnoreCase(indicators)){
						//SOCKET服务
						Vector socketVector = new Vector();
						if(datahashtable.get("socket") != null)socketVector = (Vector)datahashtable.get("socket");
						if(socketVector != null && socketVector.size()>0){
							Pingcollectdata socketdata = (Pingcollectdata)socketVector.get(0);
								if(socketdata.getEntity().equalsIgnoreCase("Utilization")){
									value = socketdata.getThevalue();
								}
						}else{
							return;
						}
					}else if("webping".equalsIgnoreCase(indicators)){
						//web服务
						Vector webVector = new Vector();
						if(datahashtable.get("url") != null)webVector = (Vector)datahashtable.get("url");
						if(webVector != null && webVector.size()>0){
							for(int i=0;i<webVector.size();i++){
								Pingcollectdata webdata = (Pingcollectdata)webVector.get(i);
								if(webdata.getEntity().equalsIgnoreCase("Utilization")){
									value = webdata.getThevalue();
								}
							}
						}else{
							return;
						}
					}else if("webresponsetime".equalsIgnoreCase(indicators)){
						//web服务
						Vector webVector = new Vector();
						if(datahashtable.get("url") != null)webVector = (Vector)datahashtable.get("url");
						if(webVector != null && webVector.size()>0){
							for(int i=0;i<webVector.size();i++){
								Pingcollectdata webdata = (Pingcollectdata)webVector.get(i);
								if(webdata.getEntity().equalsIgnoreCase("webresponsetime")){
									value = webdata.getThevalue();
								}
							}
						}else{
							return;
						}
					}else if("webpagesize".equalsIgnoreCase(indicators)){
						//WEB服务
						Vector webVector = new Vector();
						if(datahashtable.get("url") != null)webVector = (Vector)datahashtable.get("url");
						if(webVector != null && webVector.size()>0){
							for(int i=0;i<webVector.size();i++){
								Pingcollectdata webdata = (Pingcollectdata)webVector.get(i);
								if(webdata.getEntity().equalsIgnoreCase("webpagesize")){
									value = webdata.getThevalue();
								}
							}
						}else{
							return;
						}
					}else if("webkeyword".equalsIgnoreCase(indicators)){
						//WEB的关键字检测服务
						Vector webVector = new Vector();
						if(datahashtable.get("url") != null)webVector = (Vector)datahashtable.get("url");
						if(webVector != null && webVector.size()>0){
							for(int i=0;i<webVector.size();i++){
								Pingcollectdata webdata = (Pingcollectdata)webVector.get(i);
								if(webdata.getEntity().equalsIgnoreCase("webkeyword")){
									value = webdata.getThevalue();
								}
							}
						}else{
							return;
						}
					}else if("droprate".equalsIgnoreCase(indicators)){
						//丢包率
						List<PolicyInterface> interfaceList = new ArrayList<PolicyInterface>();
						if(datahashtable.get("policy") != null)interfaceList = (List<PolicyInterface>)datahashtable.get("policy");
						if(interfaceList != null && interfaceList.size()>0){
							for(int i=0;i<interfaceList.size();i++){
								PolicyInterface interData = (PolicyInterface)interfaceList.get(i);
									value =interData.getDropRate()+"";
							}
						}else{
							return;
						}
					}else if("temperature".equals(indicators)){
						//温度
						Vector temperatureVector = new Vector();
						if(datahashtable.get("temperature") != null)temperatureVector = (Vector)datahashtable.get("temperature");
						if(temperatureVector != null && temperatureVector.size()>0){
							Interfacecollectdata temperaturedata=(Interfacecollectdata)temperatureVector.get(0);
							value = temperaturedata.getThevalue();
					      }else{
						  return;
					    }
					}else if("ping".equalsIgnoreCase(indicators)){
						//DHCP PING服务
						Vector dhcpPingVector = new Vector();
						if(datahashtable.get("dhcpping") != null)dhcpPingVector = (Vector)datahashtable.get("dhcpping");
						if(dhcpPingVector != null && dhcpPingVector.size()>0){
							//for(int i=0;i<dhcpPingVector.size();i++){
								Pingcollectdata pingdata = (Pingcollectdata)dhcpPingVector.get(0);
								if(pingdata.getEntity().equalsIgnoreCase("Utilization")){
									value = pingdata.getThevalue();
								}
							//}
						}else{
							return;
						}
					}else {
						return;
					}
					if(value == null){
						return;
					}
					if( AlarmConstant.DATATYPE_NUMBER.equals(alarmIndicatorsNode.getDatatype())){
						try {
							double value_int = Double.valueOf(value);
							double Limenvalue2 = Double.valueOf(alarmIndicatorsNode.getLimenvalue2());
							double Limenvalue1 = Double.valueOf(alarmIndicatorsNode.getLimenvalue1());
							double Limenvalue0 = Double.valueOf(alarmIndicatorsNode.getLimenvalue0());
							
							//SysLogger.info(alarmIndicatorsNode.getNodeid()+"=="+alarmIndicatorsNode.getName()+"=="+value_int+"=="+Limenvalue0+"=="+Limenvalue1+"=="+Limenvalue2);
							
							String level = "";
							String alarmTimes = "";
							
							// 是否超过阀值
							boolean result = true;     
							
							if(alarmIndicatorsNode.getCompare()==0){
								//降序比较
								if(value_int <= Limenvalue2){
									level = "3";
									alarmTimes = alarmIndicatorsNode.getTime2();
									limvalue = alarmIndicatorsNode.getLimenvalue2();
								}else if(value_int <= Limenvalue1){
									level = "2";
									alarmTimes = alarmIndicatorsNode.getTime1();
									limvalue = alarmIndicatorsNode.getLimenvalue1();
								}else if(value_int <= Limenvalue0){
									level = "1";
									alarmTimes = alarmIndicatorsNode.getTime0();
									limvalue = alarmIndicatorsNode.getLimenvalue0();
								}else{
									result = false;
								}
							}else{
								//升序比较
								if(value_int >= Limenvalue2){
									level = "3";
									alarmTimes = alarmIndicatorsNode.getTime2();
									limvalue = alarmIndicatorsNode.getLimenvalue2();
								}else if(value_int >= Limenvalue1){
									level = "2";
									alarmTimes = alarmIndicatorsNode.getTime1();
									limvalue = alarmIndicatorsNode.getLimenvalue1();
								}else if(value_int >= Limenvalue0){
									level = "1";
									alarmTimes = alarmIndicatorsNode.getTime0();
									limvalue = alarmIndicatorsNode.getLimenvalue0();
								}else{
									result = false;
								}
							}
							
							// 告警资源中的 事件次数
							String num = (String)AlarmResourceCenter.getInstance().getAttribute(String.valueOf(alarmIndicatorsNode.getId()));
							
							if(num == null || "".equals(num)){
								num = "0";
							}
							
							if(!result){
								// 未超过告警阀值 则删除告警发送的时间的记录
								String name = node.getId()+":"+alarmIndicatorsNode.getType()+":"+alarmIndicatorsNode.getName();
								if(ShareData.getSendAlarmTimes() != null && ShareData.getSendAlarmTimes().containsKey(name)){
									SendAlarmTimeDao sendAlarmTimeDao = new SendAlarmTimeDao();
									try {
										sendAlarmTimeDao.delete(name);
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} finally {
										sendAlarmTimeDao.close();
									}
								}
								
								// 如果此时未发生告警 则将 次数设置为 0 次
								num = "0";
								AlarmResourceCenter.getInstance().setAttribute(String.valueOf(alarmIndicatorsNode.getId()), num);
								//当前指标无告警，首先判断内存中是否有当前指标的告警信息，若有则清除告警信息，若无则不做任何处理
								Hashtable checkEventHash = ShareData.getCheckEventHash();
								if (checkEventHash != null && checkEventHash.size() > 0) {
									if (checkEventHash.containsKey(name)) {
										// 保存告警已恢复的事件信息
										EventList eventList = createEvent(alarmIndicatorsNode, vo, value,name);
										EventListDao eventListDao = new EventListDao();
										try {
											eventListDao.save(eventList);
										} catch (Exception e) {
											e.printStackTrace();
										} finally {
											eventListDao.close();
										}
										// 删除checkEvent告警信息
										deleteEvent(node.getId()+"",alarmIndicatorsNode.getType(),alarmIndicatorsNode.getSubtype(),alarmIndicatorsNode.getName(),null);
									}
								}
								return;
							}
							
							int num_int = 0;
							int alarmTimes_int = 0;
							try {
								num_int = Integer.valueOf(num);					//当前告警次数
								alarmTimes_int = Integer.valueOf(alarmTimes);	//允许的告警次数
							} catch (RuntimeException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							
							//SysLogger.info(alarmIndicatorsNode.getNodeid()+"   "+alarmIndicatorsNode.getName()+" value: "+value+"  num_int: "+num_int+"    alarmTimes_int:" +alarmTimes_int);
							if(num_int+1 >= alarmTimes_int){
								if(vo instanceof Web){
									//设置内存中WEB的状态
									Web _web = (Web)PollingEngine.getInstance().getWebByID(node.getId());	
									_web.setAlarm(true);
									_web.getAlarmMessage().add(alarmIndicatorsNode.getAlarm_info() + " 当前值为：" + value +  alarmIndicatorsNode.getThreshlod_unit()+" 阀值为:"+limvalue+alarmIndicatorsNode.getThreshlod_unit());
									//若大于之前的告警级别,则用最大级别
									if(Integer.valueOf(level)> _web.getStatus())_web.setStatus(Integer.valueOf(level));
									if(Integer.valueOf(level)> _web.getAlarmlevel())_web.setAlarmlevel(Integer.valueOf(level));
									//SysLogger.info("$$$$$$$$$$ indicators: "+indicators);
									if("webresponsetime".equalsIgnoreCase(indicators)){
										if(_web.getTracertflag() == 2){
											//路由跟踪超过阀值时启动,进行路由跟踪数据采集
											  TraceRouteExecute tre=new TraceRouteExecute();
											  List tracelist = new ArrayList();
											  Hashtable tracertHash = new Hashtable();
											  try{
												  String urls = _web.getStr();
												  
											  		tracelist = tre.executeTracert("tracert -h 10 -w 5000 "+_web.getStr().split("//")[1],_web.getIpAddress());
											  	}catch(Exception e){
											  		e.printStackTrace();
											  	}
											    if(tracelist != null && tracelist.size()>0){			    	
											    	Tracerts tracerts = new Tracerts();
											    	tracerts.setNodetype("url");
											    	tracerts.setConfigid(_web.getId());
											    	tracerts.setDotime(Calendar.getInstance());
											    	TracertsDao tradao = new TracertsDao();
											    	try{
											    		tradao.save(tracerts);
											    	}catch(Exception e){
											    		
											    	}finally{
											    		tradao.close();
											    	}
											    	List dolist = new ArrayList();
											    	TracertsDetail detailvo = null;
											    	for(int j=0;j<tracelist.size();j++){
											    		String cont =(String) tracelist.get(j);
											    		if(cont != null && cont.trim().length()>0){
											    			detailvo = new TracertsDetail();
											    			detailvo.setDetails(cont);
											    			detailvo.setNodetype("url");
											    			detailvo.setTracertsid(tracerts.getId());
											    			detailvo.setConfigid(node.getId());
											    			dolist.add(detailvo);
											    		}
											    		SysLogger.info(_web.getStr()+":tracert====="+tracelist.get(j));
											    	}
											    	TracertsDetailDao detaildao = new TracertsDetailDao();
											    	try{
											    		detaildao.save(dolist);
											    	}catch(Exception e){
											    		e.printStackTrace();
											    	}finally{
											    		detaildao.close();
											    	}
											    	tracertHash.put("details", dolist);
											    	tracertHash.put("tracert", tracerts);
											    	//放到内存里
											    	if(ShareData.getAlltracertsdata()!= null){
											    		ShareData.getAlltracertsdata().put("url:"+_web.getId(), tracertHash);
											    	}else{
											    		Hashtable temphash = new Hashtable();
											    		temphash.put("url:"+_web.getId(), tracertHash);
											    		ShareData.setAlltracertsdata(temphash);
											    	}

											    }				
										}
									}else if("webping".equalsIgnoreCase(indicators)){
										//web服务
										if(_web.getTracertflag() == 2){
										//路由跟踪超过阀值时启动,进行路由跟踪数据采集
										  TraceRouteExecute tre=new TraceRouteExecute();
										  List tracelist = new ArrayList();
										  Hashtable tracertHash = new Hashtable();
										  try{
											  String urls = _web.getStr();
											  
										  		tracelist = tre.executeTracert("tracert -h 10 -w 5000 "+_web.getStr().split("//")[1],_web.getIpAddress());
										  	}catch(Exception e){
										  		e.printStackTrace();
										  	}
										    if(tracelist != null && tracelist.size()>0){			    	
										    	Tracerts tracerts = new Tracerts();
										    	tracerts.setNodetype("url");
										    	tracerts.setConfigid(_web.getId());
										    	tracerts.setDotime(Calendar.getInstance());
										    	TracertsDao tradao = new TracertsDao();
										    	try{
										    		tradao.save(tracerts);
										    	}catch(Exception e){
										    		
										    	}finally{
										    		tradao.close();
										    	}
										    	List dolist = new ArrayList();
										    	TracertsDetail detailvo = null;
										    	for(int j=0;j<tracelist.size();j++){
										    		String cont =(String) tracelist.get(j);
										    		if(cont != null && cont.trim().length()>0){
										    			detailvo = new TracertsDetail();
										    			detailvo.setDetails(cont);
										    			detailvo.setNodetype("url");
										    			detailvo.setTracertsid(tracerts.getId());
										    			detailvo.setConfigid(node.getId());
										    			dolist.add(detailvo);
										    		}
										    		SysLogger.info(_web.getStr()+":tracert====="+tracelist.get(j));
										    	}
										    	TracertsDetailDao detaildao = new TracertsDetailDao();
										    	try{
										    		detaildao.save(dolist);
										    	}catch(Exception e){
										    		e.printStackTrace();
										    	}finally{
										    		detaildao.close();
										    	}
										    	tracertHash.put("details", dolist);
										    	tracertHash.put("tracert", tracerts);
										    	//放到内存里
										    	if(ShareData.getAlltracertsdata()!= null){
										    		ShareData.getAlltracertsdata().put("url:"+_web.getId(), tracertHash);
										    	}else{
										    		Hashtable temphash = new Hashtable();
										    		temphash.put("url:"+_web.getId(), tracertHash);
										    		ShareData.setAlltracertsdata(temphash);
										    	}

										    }				
									}
									}
									NodeUtil nodeUtil = new NodeUtil();
									NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(_web);
									sendAlarm(nodeDTO, alarmIndicatorsNode, value, Integer.valueOf(level),0);
								}else if(vo instanceof Host){
									// 事件连续超过告警次数的限制 则变成告警
									//SysLogger.info(alarmIndicatorsNode.getNodeid()+"=="+alarmIndicatorsNode.getName()+"=="+value_int+"=="+Limenvalue0+"=="+Limenvalue1+"=="+Limenvalue2);
									Host host = (Host) PollingEngine.getInstance().getNodeByID(node.getId());
									host.setAlarm(true);
									host.getAlarmMessage().add(alarmIndicatorsNode.getAlarm_info() + " 当前值为：" + value +  alarmIndicatorsNode.getThreshlod_unit()+" 阀值为:"+limvalue+alarmIndicatorsNode.getThreshlod_unit());
									//若大于之前的告警级别,则用最大级别
									if(Integer.valueOf(level)> host.getStatus())host.setStatus(Integer.valueOf(level));
									if(Integer.valueOf(level)> host.getAlarmlevel())host.setAlarmlevel(Integer.valueOf(level));
									NodeUtil nodeUtil = new NodeUtil();
									NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(host);
									sendAlarm(nodeDTO, alarmIndicatorsNode, value, Integer.valueOf(level),0);
								}else if(vo instanceof DHCP){
									// 事件连续超过告警次数的限制 则变成告警
									//SysLogger.info(alarmIndicatorsNode.getNodeid()+"=="+alarmIndicatorsNode.getName()+"=="+value_int+"=="+Limenvalue0+"=="+Limenvalue1+"=="+Limenvalue2);
									DHCP dhcp = (DHCP) PollingEngine.getInstance().getDHCPByID(node.getId());
									dhcp.setAlarm(true);
									dhcp.getAlarmMessage().add(alarmIndicatorsNode.getAlarm_info() + " 当前值为：" + value +  alarmIndicatorsNode.getThreshlod_unit()+" 阀值为:"+limvalue+alarmIndicatorsNode.getThreshlod_unit());
									//若大于之前的告警级别,则用最大级别
									if(Integer.valueOf(level)> dhcp.getStatus())dhcp.setStatus(Integer.valueOf(level));
									if(Integer.valueOf(level)> dhcp.getAlarmlevel())dhcp.setAlarmlevel(Integer.valueOf(level));
									NodeUtil nodeUtil = new NodeUtil();
									NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(dhcp);
									sendAlarm(nodeDTO, alarmIndicatorsNode, value, Integer.valueOf(level),0);
								}
								
							}else{
								num_int = num_int + 1;
								AlarmResourceCenter.getInstance().setAttribute(String.valueOf(alarmIndicatorsNode.getId()), String.valueOf(num_int));
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else if( AlarmConstant.DATATYPE_STRING.equals(alarmIndicatorsNode.getDatatype())){
						try {
							String value_str = value;
							String Limenvalue2 = alarmIndicatorsNode.getLimenvalue2();
							String Limenvalue1 = alarmIndicatorsNode.getLimenvalue1();
							String Limenvalue0 = alarmIndicatorsNode.getLimenvalue0();
							
							SysLogger.info(alarmIndicatorsNode.getNodeid()+"=="+alarmIndicatorsNode.getName()+"=="+value_str+"=="+Limenvalue0+"=="+Limenvalue1+"=="+Limenvalue2);
							
							String level = "";
							String alarmTimes = "";
							
							// 是否超过阀值
							boolean result = true;     
							
							if(alarmIndicatorsNode.getCompare()==0){
								//降序比较
								//起用该阀值判断指标
								if("1".equals(alarmIndicatorsNode.getEnabled())){
									if(value_str.equalsIgnoreCase(Limenvalue2)){
										level = "3";
										alarmTimes = alarmIndicatorsNode.getTime2();
										limvalue = alarmIndicatorsNode.getLimenvalue2();
									}else if(value_str.equalsIgnoreCase(Limenvalue1)){
										level = "2";
										alarmTimes = alarmIndicatorsNode.getTime1();
										limvalue = alarmIndicatorsNode.getLimenvalue1();
									}else if(value_str.equalsIgnoreCase(Limenvalue0)){
										level = "1";
										alarmTimes = alarmIndicatorsNode.getTime0();
										limvalue = alarmIndicatorsNode.getLimenvalue0();
									}else{
										result = false; 
									}
								}
							}else{
								//升序比较
								//起用该阀值判断指标
								if("1".equals(alarmIndicatorsNode.getEnabled())){
									if(value_str.equalsIgnoreCase(Limenvalue2)){
										level = "3";
										alarmTimes = alarmIndicatorsNode.getTime2();
										limvalue = alarmIndicatorsNode.getLimenvalue2();
									}else if(value_str.equalsIgnoreCase(Limenvalue1)){
										level = "2";
										alarmTimes = alarmIndicatorsNode.getTime1();
										limvalue = alarmIndicatorsNode.getLimenvalue1();
									}else if(value_str.equalsIgnoreCase(Limenvalue0)){
										level = "1";
										alarmTimes = alarmIndicatorsNode.getTime0();
										limvalue = alarmIndicatorsNode.getLimenvalue0();
									}else{
										result = false; 
									}
								}
							}
							
							// 告警资源中的 事件次数
							String num = (String)AlarmResourceCenter.getInstance().getAttribute(String.valueOf(alarmIndicatorsNode.getId()));
							
							if(num == null || "".equals(num)){
								num = "0";
							}
							
							if(!result){
								// 未超过告警阀值 则删除告警发送的时间的记录
								String name = node.getId()+":"+alarmIndicatorsNode.getType()+":"+alarmIndicatorsNode.getName();
//								if(ShareData.getSendAlarmTimes() != null && ShareData.getSendAlarmTimes().containsKey(name)){
//									SendAlarmTimeDao sendAlarmTimeDao = new SendAlarmTimeDao();
//									try {
//										sendAlarmTimeDao.delete(node.getId()+":"+alarmIndicatorsNode.getType()+":"+alarmIndicatorsNode.getName());
//									} catch (Exception e) {
//										// TODO Auto-generated catch block
//										e.printStackTrace();
//									} finally {
//										sendAlarmTimeDao.close();
//									}
//								}
								// 未超过告警阀值 则删除告警发送的时间的记录
				    			if (ShareData.getSendAlarmTimes() != null && (ShareData.getSendAlarmTimes().containsKey(name + ":3") || ShareData.getSendAlarmTimes().containsKey(name + ":2") || ShareData.getSendAlarmTimes().containsKey(name + ":1"))) {

				    				SendAlarmTimeDao sendAlarmTimeDao = new SendAlarmTimeDao();
				    				try {
				    					sendAlarmTimeDao.deleteByName(name);
				    				} catch (Exception e) {
				    					e.printStackTrace();
				    				} finally {
				    					sendAlarmTimeDao.close();
				    				}
				    				if(ShareData.getSendAlarmTimes().containsKey(name + ":3")){
				    					ShareData.getSendAlarmTimes().remove(name + ":3");
				    				}
				    				if(ShareData.getSendAlarmTimes().containsKey(name + ":2")){
				    					ShareData.getSendAlarmTimes().remove(name + ":2");
				    				}
				    				if(ShareData.getSendAlarmTimes().containsKey(name + ":1")){
				    					ShareData.getSendAlarmTimes().remove(name + ":1");
				    				}
				    			}
								// 如果此时未发生告警 则将 次数设置为 0 次
								num = "0";
								AlarmResourceCenter.getInstance().setAttribute(String.valueOf(alarmIndicatorsNode.getId()), num);
								
								//当前指标无告警，首先判断内存中是否有当前指标的告警信息，若有则清除告警信息，若无则不做任何处理
								Hashtable checkEventHash = ShareData.getCheckEventHash();
								if (checkEventHash != null && checkEventHash.size() > 0) {
									if (checkEventHash.containsKey(name)) {
										// 保存告警已恢复的事件信息
										EventList eventList = createEvent(alarmIndicatorsNode, vo, value,name);
										EventListDao eventListDao = new EventListDao();
										try {
											eventListDao.save(eventList);
										} catch (Exception e) {
											e.printStackTrace();
										} finally {
											eventListDao.close();
										}
										// 删除checkEvent告警信息
										SysLogger.info(node.getId()+"   "+alarmIndicatorsNode.getType()+"   "+alarmIndicatorsNode.getSubtype()+"   "+alarmIndicatorsNode.getName());
										deleteEvent(node.getId()+"",alarmIndicatorsNode.getType(),alarmIndicatorsNode.getSubtype(),alarmIndicatorsNode.getName(),null);
									}
								}
								return;
							}
							
							int num_int = 0;
							int alarmTimes_int = 0;
							try {
								num_int = Integer.valueOf(num);					//当前告警次数
								alarmTimes_int = Integer.valueOf(alarmTimes);	//允许的告警次数
							} catch (RuntimeException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							
							//SysLogger.info(alarmIndicatorsNode.getNodeid()+"   "+alarmIndicatorsNode.getName()+" value: "+value+"  num_int: "+num_int+"    alarmTimes_int:" +alarmTimes_int);
							if(num_int+1 >= alarmTimes_int){
								// 事件连续超过告警次数的限制 则变成告警
				            	if(vo instanceof Mail){
									//设置内存中MAIL的状态
				            		Mail mail = (Mail)PollingEngine.getInstance().getMailByID(node.getId());
				            		mail.setAlarm(true);
									mail.getAlarmMessage().add(alarmIndicatorsNode.getAlarm_info() + " 当前值为：" + value +  alarmIndicatorsNode.getThreshlod_unit()+" 阀值为:"+limvalue+alarmIndicatorsNode.getThreshlod_unit());
									//若大于之前的告警级别,则用最大级别
									if(Integer.valueOf(level)> mail.getStatus())mail.setStatus(Integer.valueOf(level));
									if(Integer.valueOf(level)> mail.getAlarmlevel())mail.setAlarmlevel(Integer.valueOf(level));
									
									NodeUtil nodeUtil = new NodeUtil();
									NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(mail);
									sendAlarm(nodeDTO, alarmIndicatorsNode, value, Integer.valueOf(level),1);
				            	}else if(vo instanceof Ftp){
									//设置内存中MAIL的状态
				            		Ftp ftp = (Ftp)PollingEngine.getInstance().getFtpByID(node.getId());
				            		ftp.setAlarm(true);
				            		ftp.getAlarmMessage().add(alarmIndicatorsNode.getAlarm_info() + " 当前值为：" + value +  alarmIndicatorsNode.getThreshlod_unit()+" 阀值为:"+limvalue+alarmIndicatorsNode.getThreshlod_unit());
									//若大于之前的告警级别,则用最大级别
									if(Integer.valueOf(level)> ftp.getStatus())ftp.setStatus(Integer.valueOf(level));
									if(Integer.valueOf(level)> ftp.getAlarmlevel())ftp.setAlarmlevel(Integer.valueOf(level));
									
									NodeUtil nodeUtil = new NodeUtil();
									NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(ftp);
									sendAlarm(nodeDTO, alarmIndicatorsNode, value, Integer.valueOf(level),1);
				            	}else if(vo instanceof TFtp){
									//设置内存中TFTP的状态
				            		TFtp tftp = (TFtp)PollingEngine.getInstance().getTftpByID(node.getId());
				            		tftp.setAlarm(true);
				            		tftp.getAlarmMessage().add(alarmIndicatorsNode.getAlarm_info() + " 当前值为：" + value +  alarmIndicatorsNode.getThreshlod_unit()+" 阀值为:"+limvalue+alarmIndicatorsNode.getThreshlod_unit());
									//若大于之前的告警级别,则用最大级别
									if(Integer.valueOf(level)> tftp.getStatus())tftp.setStatus(Integer.valueOf(level));
									if(Integer.valueOf(level)> tftp.getAlarmlevel())tftp.setAlarmlevel(Integer.valueOf(level));
									
									NodeUtil nodeUtil = new NodeUtil();
									NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(tftp);
									sendAlarm(nodeDTO, alarmIndicatorsNode, value, Integer.valueOf(level),1);
				            	}else if(vo instanceof com.afunms.polling.node.SocketService){									
									//设置内存中SOCKET的状态
									com.afunms.polling.node.SocketService _tnode=(com.afunms.polling.node.SocketService)PollingEngine.getInstance().getSocketByID(node.getId());
									//SocketService socket = (SocketService)PollingEngine.getInstance().getSocketByID(node.getId());
									_tnode.setAlarm(true);
									_tnode.getAlarmMessage().add(alarmIndicatorsNode.getAlarm_info() + " 当前值为：" + value +  alarmIndicatorsNode.getThreshlod_unit()+" 阀值为:"+limvalue+alarmIndicatorsNode.getThreshlod_unit());
									//若大于之前的告警级别,则用最大级别
									if(Integer.valueOf(level)> _tnode.getStatus())_tnode.setStatus(Integer.valueOf(level));
									if(Integer.valueOf(level)> _tnode.getAlarmlevel())_tnode.setAlarmlevel(Integer.valueOf(level));
									
									NodeUtil nodeUtil = new NodeUtil();
									NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(_tnode);
									sendAlarm(nodeDTO, alarmIndicatorsNode, value, Integer.valueOf(level),1);
				            	}else if(vo instanceof Web){
									//设置内存中WEB的状态
									Web _web = (Web)PollingEngine.getInstance().getWebByID(node.getId());	
									_web.setAlarm(true);
									_web.getAlarmMessage().add(alarmIndicatorsNode.getAlarm_info() + " 当前值为：" + value +  alarmIndicatorsNode.getThreshlod_unit()+" 阀值为:"+limvalue+alarmIndicatorsNode.getThreshlod_unit());
									//若大于之前的告警级别,则用最大级别
									if(Integer.valueOf(level)> _web.getStatus())_web.setStatus(Integer.valueOf(level));
									if(Integer.valueOf(level)> _web.getAlarmlevel())_web.setAlarmlevel(Integer.valueOf(level));
									
									//SysLogger.info("$$$$$$$$$$ indicators: "+indicators);
									if("webresponsetime".equalsIgnoreCase(indicators)){
										//起用URL监视的路由跟踪功能
										  TraceRouteExecute tre=new TraceRouteExecute();
										  List tracelist = new ArrayList();
										  try{
										  		tracelist = tre.executeTracert("tracert -h 10 -w 5000 "+_web.getStr(),_web.getIpAddress());
										  	}catch(Exception e){
										  	}
										    if(tracelist != null && tracelist.size()>0){
										    	for(int j=0;j<tracelist.size();j++){
										    		String cont =(String) tracelist.get(j);
										    		if(cont != null && cont.trim().length()>0)
										    		SysLogger.info(_web.getStr()+":tracert====="+tracelist.get(j));
										    	}
										    }
									}else if("webping".equalsIgnoreCase(indicators)){
										//web服务
										//起用URL监视的路由跟踪功能
										  TraceRouteExecute tre=new TraceRouteExecute();
										  List tracelist = new ArrayList();
										  try{
										  		tracelist = tre.executeTracert("tracert -h 10 -w 5000 "+_web.getStr(),_web.getIpAddress());
										  	}catch(Exception e){
										  	}
										    if(tracelist != null && tracelist.size()>0){
										    	for(int j=0;j<tracelist.size();j++){
										    		String cont =(String) tracelist.get(j);
//										    		if(cont != null && cont.trim().length()>0)
//										    		SysLogger.info(_web.getStr()+":tracert====="+tracelist.get(j));
										    	}
										    }
									}
									
									NodeUtil nodeUtil = new NodeUtil();
									NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(_web);
									sendAlarm(nodeDTO, alarmIndicatorsNode, value, Integer.valueOf(level),1);
				            	}
//								Host host = (Host) PollingEngine.getInstance().getNodeByID(node.getId());
//								host.setAlarm(true);
//								host.getAlarmMessage().add(alarmIndicatorsNode.getAlarm_info() + " 当前值为：" + value +  alarmIndicatorsNode.getThreshlod_unit()+" 阀值为:"+limvalue+alarmIndicatorsNode.getThreshlod_unit());
//								//若大于之前的告警级别,则用最大级别
//								if(Integer.valueOf(level)> host.getStatus())host.setStatus(Integer.valueOf(level));
//								if(Integer.valueOf(level)> host.getAlarmlevel())host.setAlarmlevel(Integer.valueOf(level));
//								NodeUtil nodeUtil = new NodeUtil();
//								NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(host);
//								sendAlarm(nodeDTO, alarmIndicatorsNode, value, Integer.valueOf(level));
							}else{
								num_int = num_int + 1;
								AlarmResourceCenter.getInstance().setAttribute(String.valueOf(alarmIndicatorsNode.getId()), String.valueOf(num_int));
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
				}else if("0".equals(alarmIndicatorsNode.getEnabled())){
					//**************************************************************************
					//该告警阀值未启用，需要判断CHECKEVENT表里是否有该指标的告警，有的话需要清除   ***
					//**************************************************************************
					String indicators = alarmIndicatorsNode.getName();
					if( AlarmConstant.DATATYPE_NUMBER.equals(alarmIndicatorsNode.getDatatype())){
						try {
								// 是否超过阀值
								// 未超过告警阀值 则删除告警发送的时间的记录
								String name = node.getId()+":"+alarmIndicatorsNode.getType()+":"+alarmIndicatorsNode.getName();
//								if(ShareData.getSendAlarmTimes() != null && ShareData.getSendAlarmTimes().containsKey(name)){
//									SendAlarmTimeDao sendAlarmTimeDao = new SendAlarmTimeDao();
//									try {
//										sendAlarmTimeDao.delete(name);
//									} catch (Exception e) {
//										// TODO Auto-generated catch block
//										e.printStackTrace();
//									} finally {
//										sendAlarmTimeDao.close();
//									}
//								}
								// 未超过告警阀值 则删除告警发送的时间的记录
				    			if (ShareData.getSendAlarmTimes() != null && (ShareData.getSendAlarmTimes().containsKey(name + ":3") || ShareData.getSendAlarmTimes().containsKey(name + ":2") || ShareData.getSendAlarmTimes().containsKey(name + ":1"))) {

				    				SendAlarmTimeDao sendAlarmTimeDao = new SendAlarmTimeDao();
				    				try {
				    					sendAlarmTimeDao.deleteByName(name);
				    				} catch (Exception e) {
				    					e.printStackTrace();
				    				} finally {
				    					sendAlarmTimeDao.close();
				    				}
				    				if(ShareData.getSendAlarmTimes().containsKey(name + ":3")){
				    					ShareData.getSendAlarmTimes().remove(name + ":3");
				    				}
				    				if(ShareData.getSendAlarmTimes().containsKey(name + ":2")){
				    					ShareData.getSendAlarmTimes().remove(name + ":2");
				    				}
				    				if(ShareData.getSendAlarmTimes().containsKey(name + ":1")){
				    					ShareData.getSendAlarmTimes().remove(name + ":1");
				    				}
				    			}
								//当前指标无告警，首先判断内存中是否有当前指标的告警信息，若有则清除告警信息，若无则不做任何处理
								Hashtable checkEventHash = ShareData.getCheckEventHash();
								if(checkEventHash != null && checkEventHash.size()>0){
									if(checkEventHash.containsKey(name)){
										//删除checkEvent告警信息
										deleteEvent(node.getId()+"",alarmIndicatorsNode.getType(),alarmIndicatorsNode.getSubtype(),alarmIndicatorsNode.getName(),null);
									}
								}
								return;
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else if( AlarmConstant.DATATYPE_STRING.equals(alarmIndicatorsNode.getDatatype())){
						try {
								// 未超过告警阀值 则删除告警发送的时间的记录
								String name = node.getId()+":"+alarmIndicatorsNode.getType()+":"+alarmIndicatorsNode.getName();
//								if(ShareData.getSendAlarmTimes() != null && ShareData.getSendAlarmTimes().containsKey(name)){
//									SendAlarmTimeDao sendAlarmTimeDao = new SendAlarmTimeDao();
//									try {
//										sendAlarmTimeDao.delete(name);
//									} catch (Exception e) {
//										// TODO Auto-generated catch block
//										e.printStackTrace();
//									} finally {
//										sendAlarmTimeDao.close();
//									}
//								}
				    			if (ShareData.getSendAlarmTimes() != null && (ShareData.getSendAlarmTimes().containsKey(name + ":3") || ShareData.getSendAlarmTimes().containsKey(name + ":2") || ShareData.getSendAlarmTimes().containsKey(name + ":1"))) {

				    				SendAlarmTimeDao sendAlarmTimeDao = new SendAlarmTimeDao();
				    				try {
				    					sendAlarmTimeDao.deleteByName(name);
				    				} catch (Exception e) {
				    					e.printStackTrace();
				    				} finally {
				    					sendAlarmTimeDao.close();
				    				}
				    				if(ShareData.getSendAlarmTimes().containsKey(name + ":3")){
				    					ShareData.getSendAlarmTimes().remove(name + ":3");
				    				}
				    				if(ShareData.getSendAlarmTimes().containsKey(name + ":2")){
				    					ShareData.getSendAlarmTimes().remove(name + ":2");
				    				}
				    				if(ShareData.getSendAlarmTimes().containsKey(name + ":1")){
				    					ShareData.getSendAlarmTimes().remove(name + ":1");
				    				}
				    			}
								//当前指标无告警，首先判断内存中是否有当前指标的告警信息，若有则清除告警信息，若无则不做任何处理
								Hashtable checkEventHash = ShareData.getCheckEventHash();
								if(checkEventHash != null && checkEventHash.size()>0){
									if(checkEventHash.containsKey(name)){
										//删除checkEvent告警信息
										deleteEvent(node.getId()+"",alarmIndicatorsNode.getType(),alarmIndicatorsNode.getSubtype(),alarmIndicatorsNode.getName(),null);
									}
								}
								return;
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
      		} catch (Exception e) {
      			// TODO Auto-generated catch block
      			e.printStackTrace();
      		}
      	}
        public void checkData(Object vo, Object collectingData, String type,String subtype, AlarmIndicatorsNode alarmIndicatorsNode) {
    		// SysLogger.info(alarmIndicatorsNode.getName() +
    		// "===========CPU,MEMORY,流量等信息检测=================");
    		try {
    			Node node = (Node) vo;
    			// 将内存中相关的告警清除,最终实现从数据库表里删除相关数据
    			deleteEvent(node.getId()+"",alarmIndicatorsNode.getType(),alarmIndicatorsNode.getSubtype(),alarmIndicatorsNode.getName(),null);
    			Hashtable datahashtable = (Hashtable) collectingData;
    			// 获取JOB,判断JOB数量
    			List joblist = new ArrayList();
    			if ("1".equals(alarmIndicatorsNode.getEnabled())) {
    				String indicators = alarmIndicatorsNode.getName();
    				String value = "0";
    				String limvalue = "";
    				if ("droprate".equalsIgnoreCase(indicators)) {
    					// 丢包率
    					List<PolicyInterface> interfaceList = new ArrayList<PolicyInterface>();
    					if (datahashtable.get("policy") != null)
    						interfaceList = (List<PolicyInterface>) datahashtable
    								.get("policy");
    					if (interfaceList != null && interfaceList.size() > 0) {
    						for (int i = 0; i < interfaceList.size(); i++) {
    							PolicyInterface interData = (PolicyInterface) interfaceList
    									.get(i);
    							value = interData.getDropRate() + "";
    							if (value == null) {
    								continue;
    							}
    							setAlarmEvent(vo, alarmIndicatorsNode, value,
    									interData, indicators);
    						}
    					} else {
    						return;
    					}
    				} else if ("dropbytes".equalsIgnoreCase(indicators)) {
    					// 丢包数
    					List<QueueInfo> queueList = new ArrayList<QueueInfo>();
    					if (datahashtable.get("queue") != null)
    						queueList = (List<QueueInfo>) datahashtable
    								.get("queue");
    					if (queueList != null && queueList.size() > 0) {
    						for (int i = 0; i < queueList.size(); i++) {
    							QueueInfo queueInfo = (QueueInfo) queueList.get(i);
    							value = queueInfo.getInputDrops() + "";
    							if (value == null) {
    								continue;
    							}
    							setAlarmEvent(vo, alarmIndicatorsNode,
    									"input queue", queueInfo, indicators);
    							value = queueInfo.getOutputDrops() + "";
    							if (value == null) {
    								continue;
    							}
    							setAlarmEvent(vo, alarmIndicatorsNode,
    									"output queue", queueInfo, indicators);
    						}
    					}
    				} else if ("matches".equalsIgnoreCase(indicators)) {
    					List<AclDetail> detailList = new ArrayList<AclDetail>();
    					if (datahashtable.get("detail") != null)
    						detailList = (List<AclDetail>) datahashtable
    								.get("detail");
    					if (detailList != null && detailList.size() > 0) {
    						AclBaseDao dao = null;
    						HashMap<Integer, String> map = new HashMap<Integer, String>();
    						try {
    							dao = new AclBaseDao();
    							map = dao.getDataByIp(node.getIpAddress());
    						} catch (Exception e) {
    							e.printStackTrace();
    						} finally {
    							dao.close();
    						}

    						for (int i = 0; i < detailList.size(); i++) {
    							AclDetail detail = (AclDetail) detailList.get(i);
    							value = map.get(detail.getBaseId());
    							setAlarmEvent(vo, alarmIndicatorsNode, value,
    									detail, indicators);
    						}
    					}
    				} else {
    					return;
    				}

    			}
    		} catch (Exception e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
        private void  setAlarmEvent(Object vo,AlarmIndicatorsNode alarmIndicatorsNode,String value,Object object,String objType) {
        	String limvalue = "";
        	Node node = (Node)vo;
        	PolicyInterface pInterface=null;
        	QueueInfo queueInfo=null;
        	AclDetail detail=null;
        	String temp=alarmIndicatorsNode.getAlarm_info();
        	String key="";
        	String realVal="";
        	String alamInfo="";
        	if (objType.equals("droprate")) {
        		pInterface=(PolicyInterface)object;
        		key=String.valueOf(alarmIndicatorsNode.getId())+":"+pInterface.getInterfaceName()+":"+pInterface.getClassName();
        		alamInfo=pInterface.getInterfaceName()+":"+pInterface.getClassName();
        		realVal=value;
			}else if (objType.equals("dropbytes")) {
				queueInfo=(QueueInfo)object;
				if(value.equals("input queue")){
				realVal=queueInfo.getInputDrops()+"";
				key=String.valueOf(node.getId()+":"+alarmIndicatorsNode.getId())+":"+queueInfo.getEntity()+":input";
				alamInfo=queueInfo.getEntity()+":输入队列";
				}else if(value.equals("output queue")) {
				realVal=queueInfo.getOutputDrops()+"";
				key=String.valueOf(node.getId()+":"+alarmIndicatorsNode.getId())+":"+queueInfo.getEntity()+":output";
				alamInfo=queueInfo.getEntity()+":输出队列";
				}
			}else if (objType.equals("matches")){
				detail=(AclDetail)object;
				key=String.valueOf(node.getId()+":"+alarmIndicatorsNode.getId())+":"+detail.getBaseId()+":"+detail.getName();
				alamInfo=value+":"+detail.getName();
				realVal=detail.getMatches()+"";
			}
    		if( AlarmConstant.DATATYPE_NUMBER.equals(alarmIndicatorsNode.getDatatype())){
				try {
					double value_int = Double.valueOf(realVal);
					double Limenvalue2 = Double.valueOf(alarmIndicatorsNode.getLimenvalue2());
					double Limenvalue1 = Double.valueOf(alarmIndicatorsNode.getLimenvalue1());
					double Limenvalue0 = Double.valueOf(alarmIndicatorsNode.getLimenvalue0());
					
					
					String level = "";
					String alarmTimes = "";
					
					// 是否超过阀值
					boolean result = true;     
					
					if(alarmIndicatorsNode.getCompare()==0){
						//降序比较
						if(value_int <= Limenvalue2){
							level = "3";
							alarmTimes = alarmIndicatorsNode.getTime2();
							limvalue = alarmIndicatorsNode.getLimenvalue2();
						}else if(value_int <= Limenvalue1){
							level = "2";
							alarmTimes = alarmIndicatorsNode.getTime1();
							limvalue = alarmIndicatorsNode.getLimenvalue1();
						}else if(value_int <= Limenvalue0){
							level = "1";
							alarmTimes = alarmIndicatorsNode.getTime0();
							limvalue = alarmIndicatorsNode.getLimenvalue0();
						}else{
							result = false;
						}
					}else{
						//升序比较
						if(value_int >= Limenvalue2){
							level = "3";
							alarmTimes = alarmIndicatorsNode.getTime2();
							limvalue = alarmIndicatorsNode.getLimenvalue2();
						}else if(value_int >= Limenvalue1){
							level = "2";
							alarmTimes = alarmIndicatorsNode.getTime1();
							limvalue = alarmIndicatorsNode.getLimenvalue1();
						}else if(value_int >= Limenvalue0){
							level = "1";
							alarmTimes = alarmIndicatorsNode.getTime0();
							limvalue = alarmIndicatorsNode.getLimenvalue0();
						}else{
							result = false;
						}
					}
					
					// 告警资源中的 事件次数
					
					String num = (String)AlarmResourceCenter.getInstance().getAttribute(key);
					
					if(num == null || "".equals(num)){
						num = "0";
					}
					
					if(!result){
						// 未超过告警阀值 则删除告警发送的时间的记录
						SendAlarmTimeDao sendAlarmTimeDao = new SendAlarmTimeDao();
						try {
							sendAlarmTimeDao.delete(node.getId()+":"+alarmIndicatorsNode.getType()+":"+alarmIndicatorsNode.getName());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally {
							sendAlarmTimeDao.close();
						}
						// 如果此时未发生告警 则将 次数设置为 0 次
						num = "0";
						AlarmResourceCenter.getInstance().setAttribute(key, num);
						return;
					}
					
					int num_int = 0;
					int alarmTimes_int = 0;
					try {
						num_int = Integer.valueOf(num);					//当前告警次数
						alarmTimes_int = Integer.valueOf(alarmTimes);	//允许的告警次数
					} catch (RuntimeException e1) {
						e1.printStackTrace();
					}
					
					//SysLogger.info(alarmIndicatorsNode.getNodeid()+"   "+alarmIndicatorsNode.getName()+" value: "+value+"  num_int: "+num_int+"    alarmTimes_int:" +alarmTimes_int);
					if(num_int+1 >= alarmTimes_int){
						
						
						 if(vo instanceof Host){
							 
							// 事件连续超过告警次数的限制 则变成告警
							//SysLogger.info(alarmIndicatorsNode.getNodeid()+"=="+alarmIndicatorsNode.getName()+"=="+value_int+"=="+Limenvalue0+"=="+Limenvalue1+"=="+Limenvalue2);
							Host host = (Host) PollingEngine.getInstance().getNodeByID(node.getId());
							host.setAlarm(true);
							host.getAlarmMessage().add(alarmIndicatorsNode.getAlarm_info()+ " 当前值为：" + realVal +  alarmIndicatorsNode.getThreshlod_unit()+" 阀值为:"+limvalue+alarmIndicatorsNode.getThreshlod_unit());
							//若大于之前的告警级别,则用最大级别
							if(Integer.valueOf(level)> host.getStatus())host.setStatus(Integer.valueOf(level));
							if(Integer.valueOf(level)> host.getAlarmlevel())host.setAlarmlevel(Integer.valueOf(level));
							NodeUtil nodeUtil = new NodeUtil();
							NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(host);
							sendAlarm(nodeDTO, alarmIndicatorsNode, realVal,alamInfo, Integer.valueOf(level),1);
						}
						
					}else{
						num_int = num_int + 1;
						AlarmResourceCenter.getInstance().setAttribute(key, String.valueOf(num_int));
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
//        public void updatePortData(Object vo,Object collectingData,List list){
//           				
//            	Node node = (Node)vo;
//            	
//            	String unit="kb";
//            	NodeUtil nodeUtil = new NodeUtil();
//				NodeDTO nodeDTO = null;
//				String value = "0";
//				int levelValue =0;
//					
//				//入口利用率
//				//Vector allutilVector = new Vector();
//						
//				//if(((Hashtable)collectingData).get("utilhdx") != null)allutilVector = (Vector)((Hashtable)collectingData).get("utilhdx");
//						
//						if((Vector)((Hashtable)collectingData).get("utilhdx") != null && ((Vector)((Hashtable)collectingData).get("utilhdx")).size()>0 ){
//							
//								double levelValue1=0;
//								double levelValue2=0;
//								double levelValue3=0;
//								String inOrOut="";
//								
//								if (list!=null&&list.size()>0) {
//									AlarmPort port=null;
//									UtilHdx utilhdx= null;
//								for (int j = 0; j < list.size(); j++) {
//									 port=(AlarmPort) list.get(j);
//									 boolean flag=false;
//									 for(int i=0;i<((Vector)((Hashtable)collectingData).get("utilhdx")).size();i++){
//											utilhdx = (UtilHdx)((Vector)((Hashtable)collectingData).get("utilhdx")).get(i);
//											if (utilhdx.getRestype().equals("dynamic")) {
//												value = utilhdx.getThevalue();
//											
//											int sindex=Integer.parseInt(utilhdx.getSubentity());
//										
//									if (port.getPortindex()==sindex) {
//										if (utilhdx.getEntity().equalsIgnoreCase("InBandwidthUtilHdx")) {//入口流速
//											 levelValue1 = Double.valueOf(port.getLevelinvalue1());
//											 levelValue2 = Double.valueOf(port.getLevelinvalue2());
//											 levelValue3 = Double.valueOf(port.getLevelinvalue3());	
//											 inOrOut="入口";
//											 flag=true;
//										}else {//出口流速
//											levelValue1 = Double.valueOf(port.getLeveloutvalue1());
//											 levelValue2 = Double.valueOf(port.getLeveloutvalue2());
//											 levelValue3 = Double.valueOf(port.getLeveloutvalue3());
//											 inOrOut="出口";
//											 flag=true;
//										}
//										break;
//									}
//								}
//									 }
//									//将内存中相关的告警清除,最终实现从数据库表里删除相关数据
//										deleteEvent(port.getId()+":"+port.getType()+":"+port.getName());
//									 if (flag) {
//								
//								double value_int = Double.valueOf(value);
//
//								String level = "";
//								int alarmTimes = 0;
//								
//								// 是否超过阀值
//								boolean result = true; 
//								if(port.getCompare()==0){
//									//降序比较
//									if(value_int <= levelValue3){
//										level = "3";
//										if (utilhdx.getEntity().equalsIgnoreCase("InBandwidthUtilHdx")){
//										alarmTimes = port.getLevelintimes3();
//										levelValue =  port.getLevelinvalue3();
//										}else {
//											alarmTimes = port.getLevelouttimes3();
//											levelValue =  port.getLeveloutvalue3();
//											level = "6";
//										}
//									}else if(value_int <= levelValue2){
//										level = "2";
//										if (utilhdx.getEntity().equalsIgnoreCase("InBandwidthUtilHdx")){
//										alarmTimes = port.getLevelintimes2();
//										levelValue = port.getLevelinvalue2();
//										}else {
//											alarmTimes = port.getLevelouttimes2();
//											levelValue =  port.getLeveloutvalue2();
//											level = "5";
//										}
//									}else if(value_int <= levelValue1){
//										level = "1";
//										if (utilhdx.getEntity().equalsIgnoreCase("InBandwidthUtilHdx")){
//										alarmTimes = port.getLevelintimes1();
//										levelValue =port.getLevelinvalue1();
//										}else {
//											alarmTimes = port.getLevelouttimes1();
//											levelValue =  port.getLeveloutvalue1();
//											level = "4";
//										}
//									}else{
//										result = false;
//									}
//								}else{
//									//升序比较
//									if(value_int >= levelValue3){
//										level = "3";
//										if (utilhdx.getEntity().equalsIgnoreCase("InBandwidthUtilHdx")){
//										alarmTimes = port.getLevelintimes3();
//										levelValue =  port.getLevelinvalue3();
//										}else {
//											alarmTimes = port.getLevelouttimes3();
//											levelValue =  port.getLeveloutvalue3();	
//											level = "6";
//										}
//									}else if(value_int >= levelValue2){
//										level = "2";
//										if (utilhdx.getEntity().equalsIgnoreCase("InBandwidthUtilHdx")){
//										alarmTimes = port.getLevelintimes2();
//										levelValue = port.getLevelinvalue2();
//										}else {
//											alarmTimes = port.getLevelouttimes2();
//											levelValue =  port.getLeveloutvalue2();
//											level = "5";
//										}
//									}else if(value_int >= levelValue1){
//										level = "1";
//										if (utilhdx.getEntity().equalsIgnoreCase("InBandwidthUtilHdx")){
//										alarmTimes = port.getLevelintimes1();
//										levelValue =port.getLevelinvalue1();
//										}else {
//											alarmTimes = port.getLevelouttimes1();
//											levelValue =  port.getLeveloutvalue1();
//											level = "4";
//										}
//									}else{
//										result = false;
//									}
//							}
//							
//								String id=node.getId()+":"+port.getId();
//								// 告警资源中的 事件次数
//								String num = (String)AlarmResourceCenter.getInstance().getAttribute(id);
//								
//								if(num == null || "".equals(num)){
//									num = "0";
//								}
//								
//								if(!result){
//									// 未超过告警阀值 则删除告警发送的时间的记录
//									SendAlarmTimeDao sendAlarmTimeDao = new SendAlarmTimeDao();
//									try {
//										sendAlarmTimeDao.delete(port.getId()+":"+port.getType()+":"+port.getName());
//									} catch (Exception e) {
//										// TODO Auto-generated catch block
//										e.printStackTrace();
//									} finally {
//										sendAlarmTimeDao.close();
//									}
//									// 如果此时未发生告警 则将 次数设置为 0 次
//									num = "0";
//									AlarmResourceCenter.getInstance().setAttribute(id, num);
//									continue;
//								}
//								
//								int num_int = 0;
//								int alarmTimes_int = 0;
//								try {
//									num_int = Integer.valueOf(num);					//当前告警次数
//									alarmTimes_int = Integer.valueOf(alarmTimes);	//允许的告警次数
//								} catch (RuntimeException e1) {
//									// TODO Auto-generated catch block
//									e1.printStackTrace();
//								}
//								
//								if(num_int+1 >= alarmTimes_int){
//									 if(vo instanceof Host){
//										// 事件连续超过告警次数的限制 则变成告警
//										//SysLogger.info(alarmIndicatorsNode.getNodeid()+"=="+alarmIndicatorsNode.getName()+"=="+value_int+"=="+Limenvalue0+"=="+Limenvalue1+"=="+Limenvalue2);
//										Host host = (Host) PollingEngine.getInstance().getNodeByID(node.getId());
//										host.setAlarm(true);
//										host.getAlarmMessage().add(port.getName()+":"+inOrOut+port.getAlarm_info() + " 当前值为：" + value+unit +" 阀值为:"+levelValue+unit);
//										//若大于之前的告警级别,则用最大级别
//										int realLevel=Integer.valueOf(level);
//										if (realLevel>3) {
//											realLevel=realLevel-3;
//										}
//										if(realLevel> host.getStatus())host.setStatus(realLevel);
//										if(realLevel> host.getAlarmlevel())host.setAlarmlevel(realLevel);
//										//NodeUtil nodeUtil = new NodeUtil();
//										nodeDTO = nodeUtil.conversionToNodeDTO(host);
//										sendAlarmPort(nodeDTO, port, value, Integer.valueOf(level),inOrOut);
//										nodeDTO = null;
//									}
//									
//								}else{
//									num_int = num_int + 1;
//									AlarmResourceCenter.getInstance().setAttribute(id, String.valueOf(num_int));
//								}
//									 }
//							  }
//								}
//						}
//						nodeUtil = null;
//						nodeDTO = null;
//        }
        /**
         * 检查是否为告警
         * <p>该方法以后可以做为一个通用的方法 来检查是否为一个告警并发送 可作为采集的统一入口
         *    该方法最后调用</p>
         * @see checkEvent(Host node,AlarmIndicatorsNode nm,String value, String sIndex)</a> 
         * <p>
         * @param node		设备
         * @param nm		指标
         * @param pingvalue 值
         */
        public void checkEvent(BaseVo baseVo,AlarmIndicatorsNode nm,String value){
        	NodeDTO node = null;
        	if( !(baseVo instanceof NodeDTO) ){
        		NodeUtil nodeUtil = new NodeUtil();
        		node = nodeUtil.conversionToNodeDTO(baseVo);
        	}else {
        		node = (NodeDTO)baseVo;
        	}
        	//SysLogger.info("=====检查Ping的告警===" + "===node===" + node.getId() + "===nm===" + nm.getLimenvalue0() + "==value====" + value);
        	checkEvent(node, nm, value, "");
        	return;
        }
        
        /**
         * 检查是否为告警
         * <p>该方法以后可以做为一个通用的方法 来检查是否为一个告警并发送 可作为采集的统一入口
         *    该方法最后调用</p>
         * @see checkEvent(Host node,AlarmIndicatorsNode nm,String value, String sIndex)</a> 
         * <p>
         * @param node		设备
         * @param nm		指标
         * @param pingvalue 值
         */
        public void checkEvent(Node node,AlarmIndicatorsNode nm,String value){
        	NodeDTO nodeDTO = null;
    		NodeUtil nodeUtil = new NodeUtil();
    		nodeDTO = nodeUtil.conversionToNodeDTO(node);
        	//SysLogger.info("=====检查Ping的告警===" + "===node===" + node.getId() + "===nm===" + nm.getName() + "==value====" + value);
        	//SysLogger.info("=====检查Ping的告警===" + "===node===" + node.getId() + "===nm===" + nm.getLimenvalue0() + "==value====" + value);
        	checkEvent(nodeDTO, nm, value, "");
        	return;
        }
        
        /**
         * 检查是否为告警
         * <p>该方法以后可以做为一个通用的方法 来检查是否为一个告警并发送 可作为采集的统一入口
         *    该方法最后调用</p>
         * @see checkEvent(Host node,AlarmIndicatorsNode nm,String value, String sIndex)</a> 
         * <p>
         * @param node		设备
         * @param nm		指标
         * @param pingvalue 值
         */
        public void checkMiddlewareEvent(BaseVo node,AlarmIndicatorsNode nm,String value){
        	NodeDTO nodeDTO = null;
    		NodeUtil nodeUtil = new NodeUtil();
    		nodeDTO = nodeUtil.conversionToNodeDTO(node);
        	//SysLogger.info("=====检查Ping的告警===" + "===node===" + node.getId() + "===nm===" + nm.getName() + "==value====" + value);
        	//SysLogger.info("=====检查Ping的告警===" + "===node===" + node.getId() + "===nm===" + nm.getLimenvalue0() + "==value====" + value);
    		checkMiddlewareEvent(nodeDTO, nm, value, "");
        	return;
        }
        
        /**
         * 检查是否为告警
         * <p>该方法以后可以做为一个通用的方法 来检查是否为一个告警并发送 可作为采集的统一入口<p>
         * @param node		设备
         * @param nm		指标
         * @param value 	值
         * @param sIndex 	多个值时可作为标志存入
         */
        public void checkMiddlewareEvent(BaseVo baseVo,AlarmIndicatorsNode nm,String value, String sIndex){
    		NodeDTO node = null;
    		if (!(baseVo instanceof NodeDTO)) {
    			NodeUtil nodeUtil = new NodeUtil();
    			node = nodeUtil.conversionToNodeDTO(baseVo);
    		} else {
    			node = (NodeDTO) baseVo;
    		}
    		int alarmLevel = 0; // 告警等级
    		// 此 name 做为 该告警的唯一标识符
    		String name = node.getId() + ":" + nm.getType() + ":" + nm.getSubtype() + ":" + nm.getName();
    		if (sIndex != null && sIndex.trim().length() > 0) {
    			name = name + ":" + sIndex;
    		}
    		// 将内存中相关的告警清除,最终实现从数据库表里删除相关数据
//    		CheckEvent lastCheckEvent = deleteEvent(name);
    		CheckEvent lastCheckEvent = deleteEvent(node.getId() + "", node.getType(), node.getSubtype(), nm.getName(), sIndex);
    		if (nm.getEnabled().equalsIgnoreCase("0")) {
    			// 告警指标未监控 不做任何事情 返回
    			return;
    		}
    		if (!AlarmConstant.DATATYPE_NUMBER.equals(nm.getDatatype())) {
    			// 非数字类型的返回
    			return;
    		}
    		if (value == null || value.trim().length() == 0) {
    			// 未采集值 不做任何事 直接返回
    			return;
    		}
    		// 判断是否发送告警 如果返回 >0 则发送
    		try {
    			alarmLevel = checkAlarm(node, nm, Double.valueOf(value), name);
    			// SysLogger.info("========发送告警返回=====================" +
    			// alarmLevel);
    		} catch (Exception e) {
    			// TODO Auto-generated catch block
    			SysLogger.error("判断是否发送告警出错", e);
    			e.printStackTrace();
    		}
    		if (alarmLevel > 0) {
    			// 需要发送告警产生
    			try {
    				sendMiddlewareAlarm(node, nm, value, alarmLevel, sIndex);
    			} catch (Exception e) {
    				e.printStackTrace();
    			}
    		} else {
    			// 未超过告警阀值 则删除告警发送的时间的记录
    			SendAlarmTimeDao sendAlarmTimeDao = new SendAlarmTimeDao();
    			try {
    				sendAlarmTimeDao.delete(name);
    			} catch (Exception e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} finally {
    				sendAlarmTimeDao.close();
    			}

    			// 判断之前是否有告警,若有则发送告警恢复信息
    			if (lastCheckEvent != null) {
    				// 之前有告警 ， 发送告警恢复信息
    				// TODO 这里调用告警恢复信息的发送

    			}
    		}
    	}
        /**
    	 * 从 数据库中 删除上次的告警
    	 * 如果存在则找出后 在删除并返回 ， 如果不存在则null
    	 * <p>将内存中相关的告警清除,最终实现从数据库表里删除相关数据<p>
    	 * @param name
    	 * @return CheckEvent
    	 */
    	public CheckEvent deleteEvent(String nodeId, String type, String subtype, String indicatorsName, String sIndex) {
    		//将内存中相关的告警清除,最终实现从数据库表里删除相关数据
    		CheckEvent checkEvent = null;
//    		List<CheckEvent> list = null;
    		String name = nodeId + ":" + type + ":" + subtype + ":" + indicatorsName;
//    		System.out.println("-----sIndex----------:"+sIndex);
    		if (sIndex != null && sIndex.trim().length() > 0) {
    			name = name + ":" + sIndex;
    		}
    		Hashtable checkEventHash = ShareData.getCheckEventHash();
//    		System.out.println(name+"-----checkEventHash----------:"+checkEventHash);
    		if (checkEventHash != null && checkEventHash.size() > 0) {
//    			System.out.println("-----checkEventHash.containsKey(name)----------:"+checkEventHash.containsKey(name));
    			if (checkEventHash.containsKey(name)) {
    				checkEvent = new CheckEvent();
    				checkEvent.setNodeid(nodeId);
    				checkEvent.setIndicatorsName(indicatorsName);
    				checkEvent.setType(type);
    				checkEvent.setSubtype(subtype);
    				checkEvent.setSindex(sIndex);
    				checkEvent.setAlarmlevel((Integer) checkEventHash.get(name));
    				CheckEventDao checkeventdao = new CheckEventDao();
    				try {
    					if (sIndex != null && sIndex.length() > 0) {
    						checkeventdao.deleteCheckEvent(nodeId, type, subtype, indicatorsName, sIndex);
    					} else {
    						checkeventdao.deleteCheckEvent(nodeId, type, subtype, indicatorsName);
    					}
    				} catch (Exception e) {
    					e.printStackTrace();
    				} finally {
    					checkeventdao.close();
    				}
    				checkEventHash.remove(name);
    			}
    		}
//    		CheckEventDao checkeventdao = new CheckEventDao();
//    		try {
//    			if (sIndex != null && sIndex.length() > 0) {
//    				//list里包含相应id的CheckEvent对象
//    				list = (List<CheckEvent>) checkeventdao.findCheckEvent(nodeId, type, subtype, indicatorsName, sindex);
//    			} else {
//    				list = (List<CheckEvent>) checkeventdao.findCheckEvent(nodeId, type, subtype, indicatorsName);
//    			}
//    			if (list != null && list.size() > 0) {
//    				checkEvent = list.get(0);//
//    			}
//    		} catch (Exception e) {
//    			e.printStackTrace();
//    		} finally {
//    			checkeventdao.close();
//    		}
    		
    		return checkEvent;//返回 CheckEvent对象
    	}
        /**
         * 检查磁盘是否为告警
         * <p>该方法以后可以做为一个通用的方法 来检查是否为一个告警并发送 可作为采集的统一入口<p>
         * @param node		设备
         * @param nm		指标
         * @param value 	值
         * @param sIndex 	多个值时可作为标志存入
         * @diskAlarmIndicatorType  告警类别
         */
        public void checkDiskEvent(BaseVo baseVo,AlarmIndicatorsNode nm,String value, String sIndex,String diskAlarmIndicatorType){
        	sIndex = diskAlarmIndicatorType + ":"+ sIndex;
        	checkEvent(baseVo,nm,value,sIndex);
        }
        public void checkEvent(BaseVo baseVo, AlarmIndicatorsNode nm, String value,String sIndex) {
    		NodeDTO node = null;
    		if (!(baseVo instanceof NodeDTO)) {
    			NodeUtil nodeUtil = new NodeUtil();
    			node = nodeUtil.conversionToNodeDTO(baseVo);
    		} else {
    			node = (NodeDTO) baseVo;
    		}
    		// 此 name 做为 该告警的唯一标识符
    		String name = node.getId() + ":" + node.getType() + ":" + node.getSubtype() + ":" + nm.getName();
    		if (sIndex != null && sIndex.trim().length() > 0) {
    			name = name + ":" + sIndex;
    		}
    		CheckEvent lastCheckEvent = deleteEvent(node.getId() + "", node.getType(), node.getSubtype(), nm.getName(), sIndex);
//    		SysLogger.info("========lastCheckEvent=====================" + lastCheckEvent);
    		// 将内存中相关的告警清除,最终实现从数据库表里删除相关数据
    		// 告警删除的代码已注释 同一类告警， 只告警一次 ，并且恢复正常后有提示信息
    		if (nm.getEnabled().equalsIgnoreCase("0")) {
    			// 告警指标未监控 不做任何事情 返回
    			return;
    		}
    		if (value == null || value.trim().length() == 0) {
    			// 未采集值 不做任何事 直接返回
    			return;
    		}
    		// 判断是否发送告警 如果返回 >0 则发送
    		int alarmLevel = 0; // 告警等级
//    		SysLogger.info("###### 告警指标的数据类型 ："+nm.getDatatype()+" ####### 描述 :"+nm.getDescr());
    		try {
    			if (AlarmConstant.DATATYPE_NUMBER.equals(nm.getDatatype())) {
    				// 数字类型
    				alarmLevel = checkAlarm(node, nm, Double.valueOf(value), name);
    			} else if (AlarmConstant.DATATYPE_STRING.equals(nm.getDatatype())) {
    				// 字符串类型
    				alarmLevel = checkAlarm(node, nm, value, name);
    			}
    		} catch (Exception e) {
    			SysLogger.error("判断是否发送告警出错", e);
    			e.printStackTrace();
    		}
    		if (alarmLevel > 0) {
    			//需要发送告警产生
    			try {
    				Hashtable vmData = new Hashtable();
    	    	    try{	
    	    		   if(nm.getSubtype().equalsIgnoreCase("vmware")){
    	                  vmData = (Hashtable) ShareData.getVmdata().get("getname");
    	                  sIndex = vmData.get(nm.getSubentity()).toString();
    	    		   }
    	    	    }catch(Exception e){
    	    		   
    	    	    }
    				sendAlarm(node, nm, value, alarmLevel, sIndex);
    			} catch (Exception e) {
    				e.printStackTrace();
    			}
    		} else {
    			// 未超过告警阀值 则删除告警发送的时间的记录
//    			if(ShareData.getSendAlarmTimes() != null && ShareData.getSendAlarmTimes().containsKey(name)){
//    				SendAlarmTimeDao sendAlarmTimeDao = new SendAlarmTimeDao();
//    				try {
//    					sendAlarmTimeDao.delete(name);
//    				} catch (Exception e) {
//    					e.printStackTrace();
//    				} finally {
//    					sendAlarmTimeDao.close();
//    				}
//    			}
    			// 未超过告警阀值 则删除告警发送的时间的记录
    			
    			if (ShareData.getSendAlarmTimes() != null && (ShareData.getSendAlarmTimes().containsKey(name)||ShareData.getSendAlarmTimes().containsKey(name + ":3") || ShareData.getSendAlarmTimes().containsKey(name + ":2") || ShareData.getSendAlarmTimes().containsKey(name + ":1"))) {

    				SendAlarmTimeDao sendAlarmTimeDao = new SendAlarmTimeDao();
    				try {
    					sendAlarmTimeDao.deleteByName(name);
    				} catch (Exception e) {
    					e.printStackTrace();
    				} finally {
    					sendAlarmTimeDao.close();
    				}
    				if(ShareData.getSendAlarmTimes().containsKey(name)){
    					ShareData.getSendAlarmTimes().remove(name);
    				}
    				if(ShareData.getSendAlarmTimes().containsKey(name + ":3")){
    					ShareData.getSendAlarmTimes().remove(name + ":3");
    				}
    				if(ShareData.getSendAlarmTimes().containsKey(name + ":2")){
    					ShareData.getSendAlarmTimes().remove(name + ":2");
    				}
    				if(ShareData.getSendAlarmTimes().containsKey(name + ":1")){
    					ShareData.getSendAlarmTimes().remove(name + ":1");
    				}
    			}

    			//判断之前是否有告警,若有则发送告警恢复信息
    			if (lastCheckEvent != null) {
    				// 之前有告警 ， 发送告警恢复信息
    				// TODO 这里调用告警恢复信息的发送
    				try {
    					sendAlert(node, nm, value, alarmLevel, sIndex);
    				} catch (Exception e) {
    					e.printStackTrace();
    				}
    			}
    		}
    	}
        
        /**
         * 检查是否为告警
         * <p>该方法以后可以做为一个通用的方法 来检查是否为一个告警并发送 可作为采集的统一入口<p>
         * @param node		设备
         * @param nm		指标
         * @param value 	值
         * @param sIndex 	多个值时可作为标志存入
         */
//        public void checkEvent(BaseVo baseVo,AlarmIndicatorsNode nm,String value, String sIndex){
//        	NodeDTO node = null;
//        	if( !(baseVo instanceof NodeDTO) ){
//        		NodeUtil nodeUtil = new NodeUtil();
//        		node = nodeUtil.conversionToNodeDTO(baseVo);
//        	}else {
//        		node = (NodeDTO)baseVo;
//        	}
//            int alarmLevel = 0;		// 告警等级
//            // 此 name 做为 该告警的唯一标识符
//            String name = node.getId()+":"+nm.getType()+":"+nm.getName();
//            if(sIndex!=null && sIndex.trim().length() > 0){
//            	name = name + ":" +sIndex;
//            }
//            //将内存中相关的告警清除,最终实现从数据库表里删除相关数据
//          //告警删除的代码已注释  同一类告警， 只告警一次 ，并且恢复正常后有提示信息
////			CheckEvent lastCheckEvent = deleteEvent(name);
//			if(nm.getEnabled().equalsIgnoreCase("0")){
//				//告警指标未监控 不做任何事情 返回
//				return;
//			}
////			if(!AlarmConstant.DATATYPE_NUMBER.equals(nm.getDatatype())){
////				// 非数字类型的返回
////				return;
////			}
//			if(value == null || value.trim().length()==0){
// 				//未采集值 不做任何事 直接返回
// 				return;
//			}
//			// 判断是否发送告警 如果返回 >0 则发送
////			 判断是否发送告警 如果返回 >0 则发送
//			SysLogger.info("###### 告警指标的数据类型 ："+nm.getDatatype()+" ####### 描述 :"+nm.getDescr());
//			try {
//				if (AlarmConstant.DATATYPE_NUMBER.equals(nm.getDatatype())) {
//					// 数字类型
//					alarmLevel = checkAlarm(node, nm, Double.valueOf(value), name);
//				} else if (AlarmConstant.DATATYPE_STRING.equals(nm.getDatatype())) {
//					// 字符串类型
//					alarmLevel = checkAlarm(node, nm, value, name);
//				}
//				//				SysLogger.info("========发送告警返回=====================" + alarmLevel);
//			} catch (Exception e) {
//				SysLogger.error("判断是否发送告警出错", e);
//				e.printStackTrace();
//			}
//			// 判断是否发送告警 如果返回 >0 则发送
//			try {
//				String nodename=nm.getName();
//				if(nodename.equalsIgnoreCase("power")||nodename.equalsIgnoreCase("fan")){
//					alarmLevel = checkDetailAlarm(node, nm, Double.valueOf(value), name);
//				}
////				else {
////					alarmLevel = checkAlarm(node, nm, Double.valueOf(value), name);
////				}
//				
////				SysLogger.info("========发送告警返回=====================" + alarmLevel);
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				SysLogger.error("判断是否发送告警出错" , e);
//				e.printStackTrace();
//			}
//			
// 			if(alarmLevel >0){
// 				//需要发送告警产生
// 				//if(baseVo instanceof HostNode){
//					// 事件连续超过告警次数的限制 则变成告警
//					Host host = (Host) PollingEngine.getInstance().getNodeByID(node.getId());
//					host.setAlarm(true);
//					String limvalue = "";
//					if(alarmLevel == 1)
//						limvalue = nm.getLimenvalue0();
//					else if (alarmLevel == 2)
//						limvalue = nm.getLimenvalue1();
//					else if (alarmLevel == 3)
//						limvalue = nm.getLimenvalue2();
//					host.getAlarmMessage().add(nm.getAlarm_info() + " 当前值为：" + value +  nm.getThreshlod_unit()+" 阀值为:"+limvalue+nm.getThreshlod_unit());
//					//若大于之前的告警级别,则用最大级别
//					if(Integer.valueOf(alarmLevel)> host.getStatus())host.setStatus(Integer.valueOf(alarmLevel));
//					if(Integer.valueOf(alarmLevel)> host.getAlarmlevel())host.setAlarmlevel(Integer.valueOf(alarmLevel));
// 				//}
// 				//当设备有告警的时候，写XML供拓扑图更新
//				if(nm.getName().equalsIgnoreCase("ping")){
//					//目前只对PING进行该操作
//					String imgPath = "";
//					ManageXmlDao managexmldao = new ManageXmlDao();
//					List xmlList = new ArrayList();
//					try{
//						xmlList = managexmldao.loadAll();
//					}catch(Exception e){
//						e.printStackTrace();
//					}finally{
//						managexmldao.close();
//					}
//					if(xmlList != null && xmlList.size()>0){
//						NodeEquipDao nodeEquipDao = new NodeEquipDao();
//						EquipImageDao equipImageDao = new EquipImageDao();
//						try{
//							for(int i=0;i<xmlList.size();i++){
//								ManageXml manageXml = (ManageXml)xmlList.get(i);
//								String xmlName = manageXml.getXmlName();
//								ManageXmlOperator mxmlOpr = new ManageXmlOperator();
//								mxmlOpr.setFile(xmlName);
//								mxmlOpr.init4editNodes();
//								NodeEquip Vo = null;
//								try{
//									Vo = (NodeEquip) nodeEquipDao.findByNodeAndXml(host.getId()+"", xmlName);
//								}catch(Exception e){
//									e.printStackTrace();
//								}
//								if(Vo != null){
//									EquipImage equipImage = null;
//									try{
//										equipImage = (EquipImage) equipImageDao.findImageById(Vo.getEquipId());
//									}catch(Exception e){
//										e.printStackTrace();
//									}
//									if(equipImage != null)imgPath = equipImage.getPath();
//								}
//
//								//SysLogger.info(xmlName+"=========="+imgPath.substring(17));
//								if(imgPath != null && imgPath.trim().length()>0){
//									if (mxmlOpr.isIdExist(host.getId()+"")){ // no exist
//										mxmlOpr.updateNode(host.getId()+"", "img", host.getCategory()==4?NodeHelper.getServerTopoImage(host.getSysOid()):imgPath.substring(17));
//									}
//								}else{
//									if (mxmlOpr.isIdExist(host.getId()+"")){ // no exist
//										mxmlOpr.updateNode(host.getId()+"", "img", host.getCategory()==4?NodeHelper.getServerTopoImage(host.getSysOid()):NodeHelper.getTopoImage(host.getCategory()));
//									}
//								}
//								
//								mxmlOpr.writeXml();
//							}
//						}catch(Exception e){
//							e.printStackTrace();
//						}finally{
//							nodeEquipDao.close();
//							equipImageDao.close();
//						}
//					}
//					
//					
//				}
//				
//					
//					
//					
// 				try{
//					sendAlarm(node, nm, value, alarmLevel , sIndex);
// 				}catch(Exception e){
// 					e.printStackTrace();
// 				}
// 			}else{
// 				// 未超过告警阀值 则删除告警发送的时间的记录
//				SendAlarmTimeDao sendAlarmTimeDao = new SendAlarmTimeDao();
//				try {
//					sendAlarmTimeDao.delete(name);
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} finally {
//					sendAlarmTimeDao.close();
//				}
//				//当前指标无告警，首先判断内存中是否有当前指标的告警信息，若有则清除告警信息，若无则不做任何处理
//				Hashtable checkEventHash = ShareData.getCheckEventHash();
//				if(checkEventHash != null && checkEventHash.size()>0){
//					if(checkEventHash.containsKey(name)){
//						//保存告警已恢复的事件信息
//						Host host = (Host) PollingEngine.getInstance().getNodeByID(node.getId());
//					    EventList eventList = createEvent(nm, host, value, name);
//						EventListDao eventListDao = new EventListDao();
//						try {
//							eventListDao.save(eventList);
//						} catch (Exception e) {
//							e.printStackTrace();
//						} finally {
//							eventListDao.close();
//						}
//						//删除checkEvent告警信息
//						deleteEvent(name);
//					}
//				}
// 				//判断之前是否有告警,若有则发送告警恢复信息
//// 				if(lastCheckEvent!=null){
// 					// 之前有告警 ， 发送告警恢复信息
// 					// TODO 这里调用告警恢复信息的发送
// 					
//// 				}
//		    }
//        }
//    	/告警恢复提示
    	public void sendAlert(NodeDTO node, AlarmIndicatorsNode alarmIndicatorsNode, String value, int alarmLevel, String sIndex) {
    		if (sIndex == null) {
    			sIndex = "";
    		}
    		String unit = alarmIndicatorsNode.getThreshlod_unit();
    		if ("无".equals(unit)) {
    			unit = "";
    		}
    		//			String threshold = getThresholdByLevel(alarmIndicatorsNode, alarmLevel);
    		String eventtype = "poll";
    		String eventlocation = node.getName() + "(" + node.getIpaddress() + ")";
    		String subentity = alarmIndicatorsNode.getName();
    		if (sIndex.trim().length() > 0) {
    			eventlocation = eventlocation + "(" + sIndex + ")";
    			subentity=subentity+":"+sIndex;
    		}
    		String bid = node.getBusinessId();
    		String content = node.getIpaddress() + " " + sIndex + " " + alarmIndicatorsNode.getAlarm_info() + " 的告警已恢复";
    		Integer level1 = alarmLevel;
    		String subtype = alarmIndicatorsNode.getType();
    		
    		String objid = node.getId() + "";
    		String ipaddress = node.getIpaddress();

    		// 创建最新告警信息
    		CheckEvent checkEvent = new CheckEvent();
    		checkEvent.setIndicatorsName(alarmIndicatorsNode.getName());
    		checkEvent.setType(alarmIndicatorsNode.getType());
    		checkEvent.setSubtype(alarmIndicatorsNode.getSubtype());
    		checkEvent.setNodeid(node.getNodeid() + "");
    		checkEvent.setAlarmlevel(alarmLevel);
    		checkEvent.setContent(content);
    		checkEvent.setSindex("");
    		checkEvent.setThevalue(value);
    		if (sIndex != null && sIndex.length() > 0) {
    			checkEvent.setSindex(sIndex);
    		}
    		//将该告警对比信息放到内存系统里,最终需要放到数据库表里
    		//			saveEvent(checkEvent);
    		// --------------------

    		// 创建 eventList 
    		EventList eventList = createEvent(eventtype, eventlocation, bid, content, level1, subtype, subentity, ipaddress, objid);
    		SendAlarmUtil alarmUtil = new SendAlarmUtil();
    		alarmUtil.sendAlarm(checkEvent, eventList, alarmIndicatorsNode);
    	}
        /**
    	 * 
    	 * 字符串类型判断
    	 * 确认是否为告警 
    	 * <p>当发生事件的次数大于规定的次数 则作为告警，并返回告警的等级
    	 * 	  如果不告警则返回<p>
    	 * @param node		设备
    	 * @param nm		指标
    	 * @param value		值
    	 * @param name		告警唯一标志	
    	 * @return
    	 */
    	private int checkAlarm(NodeDTO node, AlarmIndicatorsNode nm, String value, String name) {
    		//SysLogger.info("===========开始确认是否为告警=============");
    		int alarmLevel = 0;
    		int eventLevel = 0; // 事件等级
    		int eventTimes = 0; // 事件次数
    		String limenvalue0 = nm.getLimenvalue0();//一级阀值
    		String limenvalue1 = nm.getLimenvalue1();//二级阀值
    		String limenvalue2 = nm.getLimenvalue2();//三级阀值
    		// 检查事件等级
    		//			SysLogger.info(name + "========检查事件等级===========" + limenvalue0 + "==========" + limenvalue1 + "==========" + limenvalue2);
    		//			eventLevel = checkEventLevel(value, limenvalue0, limenvalue1, limenvalue2, nm.getCompare());
    		eventLevel = checkEventLevel(value, limenvalue0, limenvalue1, limenvalue2, nm.getSms0(), nm.getSms1(), nm.getSms2(), nm.getCompare());
    		//			SysLogger.info(name + "========检查事件等级===========" + eventLevel);
    		// 检查事件次数 
    		// 如果传入的事件等级为 0 则说明事件恢复 则进行清楚 事件次数
    		// 如果返回次数大于 0 则说明大于规定的事件次数 将事件升级为告警 如果返回次数不大于 0 则只是将事件次数 + 1;;
    		eventTimes = checkEventTimes(nm, eventLevel, name);
    		//			SysLogger.info(name + "======== 检查事件次数 ===========" + eventTimes);
    		if (eventTimes > 0) {
    			// 如果大于 0 将事件等级升级 为 告警等级
    			alarmLevel = eventLevel;
    		}
    		return alarmLevel;

    	}
    	/**
    	 * 
    	 * 字符串比较
    	 * 
    	 * 检查事件等级
    	 * 
    	 * 并检查该事件等级是否启动
    	 * 
    	 * <p>compare_type 比较方式，1 为升序 即大于比较，0 为降序 即小于比较</p>
    	 * <p>返回告警等级 如果不告警则返回 0</p>
    	 * 
    	 * @author nielin
    	 * @param value 	   值
    	 * @param limenvalue0  一级阀值
    	 * @param limenvalue1  二级阀值
    	 * @param limenvalue2  三级阀值
    	 * @param isAlarm0     一级阀值是否启动	1 启用
    	 * @param isAlarm1     二级阀值是否启动	1 启用
    	 * @param isAlarm2     三级阀值是否启动	1 启用
    	 * @param compare_type 比较方式
    	 * 
    	 * @return level
    	 */
    	private int checkEventLevel(String value, String limenvalue0, String limenvalue1, String limenvalue2, String isAlarm0, String isAlarm1, String isAlarm2, int compare_type) {
    		int level = 0; // 需要返回的等级
    		if (compare_type == 2) {
    			//相等比较
    			if (limenvalue2 != null && limenvalue2.trim().length() > 0 && !limenvalue2.trim().equalsIgnoreCase(value) && "1".equals(isAlarm2)) {
    				level = 3;
    			} else if (limenvalue1 != null && limenvalue1.trim().length() > 0 && !limenvalue1.trim().equalsIgnoreCase(value) && "1".equals(isAlarm1)) {
    				level = 2;
    			} else if (limenvalue0 != null && limenvalue0.trim().length() > 0 && !limenvalue0.trim().equalsIgnoreCase(value) && "1".equals(isAlarm0)) {
    				level = 1;
    			} else {
    				level = 0;
    			}
    		}
    		return level;
    	}
        /**
         * 检查是否为告警
         * <p>该方法以后可以做为一个通用的方法 来检查是否为一个告警并发送 可作为采集的统一入口
         *    该方法最后调用</p>
         * @see checkEvent(Host node,AlarmIndicatorsNode nm,String value, String sIndex)</a> 
         * <p>
         * @param node		设备
         * @param nm		指标
         * @param pingvalue 值
         */
    	public void checkEvent(Node node, AlarmIndicatorsNode nm, String value,String sIndex) {
    		NodeDTO nodeDTO = null;
    		NodeUtil nodeUtil = new NodeUtil();
    		nodeDTO = nodeUtil.conversionToNodeDTO(node);
    		// SysLogger.info("=====检查Ping的告警===" + "===node===" + node.getId() +
    		// "===nm===" + nm.getName() + "==value====" + value);
    		// SysLogger.info("=====检查Ping的告警===" + "===node===" + node.getId() +
    		// "===nm===" + nm.getLimenvalue0() + "==value====" + value);
    		checkEvent(nodeDTO, nm, value, sIndex);
    		return;
    	}
	    /** 	
	     * 检查磁盘告警信息
	     * @param node
	     * @param diskVector
	     * @param nm
	     */
        public void checkDisk(Host node,Vector diskVector,AlarmIndicatorsNode nm){
    		// 对 diskVector 磁盘信息
    		//SysLogger.info("### 开始运行检测磁盘是否告警... ###");
    		//			SysLogger.info(node.getIpAddress() + "============开始运行检测磁盘是否告警================="+diskVector.size() + "===="+nm.getName());
    		if ("0".equals(nm.getEnabled())) {
    			//告警指标未监控 不做任何事情 返回
    			return;
    		}
    		if (diskVector == null || diskVector.size() == 0) {
    			//未采集到数据 不做任何事情 返回
    			return;
    		}
    		for (int i = 0; i < diskVector.size(); i++) {
    			Diskcollectdata diskcollectdata = null;
    			diskcollectdata = (Diskcollectdata) diskVector.get(i);
    			if (diskcollectdata.getEntity().equalsIgnoreCase("Utilization") && "diskperc".equals(nm.getName())) {
    				//利用率
    				String diskname = diskcollectdata.getSubentity();
    				if (node.getSysOid().startsWith("1.3.6.1.4.1.311.1.1.3")) {
    					diskname = diskcollectdata.getSubentity().substring(0, 3);
    				}

    				Hashtable alldiskalarmdata = null;
    				try {
    					alldiskalarmdata = ShareData.getAlldiskalarmdata();
    				} catch (Exception e) {
    					e.printStackTrace();
    				}
    				if (alldiskalarmdata == null)
    					alldiskalarmdata = new Hashtable();
    				Diskconfig diskconfig = null;
    				if (node.getOstype() == 4 && node.getSysOid().startsWith("1.3.6.1.4.1.311.1.1.3")) {
    					diskconfig = (Diskconfig) alldiskalarmdata.get(node.getIpAddress() + ":" + diskcollectdata.getSubentity().substring(0, 3) + ":" + "利用率阈值");
    				} else
    					diskconfig = (Diskconfig) alldiskalarmdata.get(node.getIpAddress() + ":" + diskcollectdata.getSubentity() + ":" + "利用率阈值");
    				if (diskconfig == null) {
    					return;
    				}
    				int limevalue0 = diskconfig.getLimenvalue();
    				int limevalue1 = diskconfig.getLimenvalue1();
    				int limevalue2 = diskconfig.getLimenvalue2();
    				//					System.out.println(nm.getName() + "=========================" + limevalue0 + "====" + limevalue1 + "======" + limevalue2);
    				nm.setLimenvalue0(limevalue0 + "");
    				nm.setLimenvalue1(limevalue1 + "");
    				nm.setLimenvalue2(limevalue2 + "");
    				NodeUtil nodeUtil = new NodeUtil();
    				NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(node);
    				checkEvent(nodeDTO, nm, diskcollectdata.getThevalue(), diskname);
    			} else if (diskcollectdata.getEntity().equalsIgnoreCase("UtilizationInc") && "diskinc".equals(nm.getName())) {
    				//增长率
    				//					System.out.println("是否进入了//增长率");
    				String diskname = diskcollectdata.getSubentity();
    				if (node.getSysOid().startsWith("1.3.6.1.4.1.311.1.1.3")) {
    					diskname = diskcollectdata.getSubentity().substring(0, 3);
    				}
    				Hashtable alldiskalarmdata = null;
    				try {
    					alldiskalarmdata = ShareData.getAlldiskalarmdata();
    				} catch (Exception e) {
    					e.printStackTrace();
    				}
    				if (alldiskalarmdata == null)
    					alldiskalarmdata = new Hashtable();
    				Diskconfig diskconfig = null;
    				if (node.getOstype() == 4 || node.getSysOid().startsWith("1.3.6.1.4.1.311.1.1.3")) {
    					diskconfig = (Diskconfig) alldiskalarmdata.get(node.getIpAddress() + ":" + diskcollectdata.getSubentity().substring(0, 3) + ":" + "增长率阈值");
    				} else {
    					diskconfig = (Diskconfig) alldiskalarmdata.get(node.getIpAddress() + ":" + diskcollectdata.getSubentity() + ":" + "增长率阈值");
    				}
    				if (diskconfig == null) {
    					return;
    				}
    				int limevalue0 = diskconfig.getLimenvalue();
    				int limevalue1 = diskconfig.getLimenvalue1();
    				int limevalue2 = diskconfig.getLimenvalue2();
    				nm.setLimenvalue0(limevalue0 + "");
    				nm.setLimenvalue1(limevalue1 + "");
    				nm.setLimenvalue2(limevalue2 + "");
    				NodeUtil nodeUtil = new NodeUtil();
    				NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(node);
    				//    				System.out.println(nm.getName() + "=========================" + limevalue0 + "====" + limevalue1 + "======" + limevalue2);
    				checkEvent(nodeDTO, nm, diskcollectdata.getThevalue(), diskname);
    			}
    		}
    	}
	     	
		/**
		 * 
		 * 检查主机进程组告警
		 * nielin add
		 * @date 2010-08-18
		 * @param ip
		 * @param proVector
		 */
		public List createProcessGroupEventList(String ip , Vector proVector, AlarmIndicatorsNode alarmIndicatorsNode){
			// SysLogger.info("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
//			SysLogger.info("$$$$$ 开始判断进程告警 $$$$$$$$$$$$$$$$$$$$$$$$$$$$");
			// SysLogger.info("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
			if (alarmIndicatorsNode == null) {
				SysLogger.info("=========无进程告警指标不告警====================");
				return null;
			}
			List retList = new ArrayList();
			if (proVector == null || proVector.size() == 0)
				return retList;
			try {
				Node hostNode = PollingEngine.getInstance().getNodeByIP(ip);

				ProcessGroupConfigurationUtil processGroupConfigurationUtil = new ProcessGroupConfigurationUtil();
				List list = processGroupConfigurationUtil
						.getProcessGroupByIpAndMonFlag(ip, "1");

				if (list == null || list.size() == 0) {
					return null;
				}
				
				for (int i = 0; i < list.size(); i++) {
					ProcessGroup processGroup = (ProcessGroup) list.get(i);
					
					EventList eventList = new EventList();	//告警列表
					List wrongList = new ArrayList();	//错误列表
					
					List processGroupConfigurationList = processGroupConfigurationUtil
							.getProcessGroupConfigurationByGroupId(String
									.valueOf(processGroup.getId()));
					if (processGroupConfigurationList == null
							|| processGroupConfigurationList.size() == 0) {
						continue;
					}
					
					if("2".equals(processGroup.getId())){
						for(int j=0;j<processGroupConfigurationList.size();j++){
							ProcessGroupConfiguration processGroupConfiguration = (ProcessGroupConfiguration) processGroupConfigurationList.get(j);
						}
					}
					
					for (int j = 0; j < processGroupConfigurationList.size(); j++) {
						int num = 0;
						ProcessGroupConfiguration processGroupConfiguration = (ProcessGroupConfiguration) processGroupConfigurationList
								.get(j);
						for (int k = 0; k < proVector.size(); k++) {
							Processcollectdata processdata = (Processcollectdata) proVector
									.elementAt(k);
							if ("Name".equals(processdata.getEntity())) {
								if (processGroupConfiguration.getName().trim()
										.equals(processdata.getThevalue().trim())) {
									num++;
								}
							}
						}
						int times = Integer.parseInt(processGroupConfiguration
								.getTimes());
						String status = processGroupConfiguration.getStatus();
						//黑名单，不管数量多少，只要出现就报警
						if ("1".equals(status)) {
							eventList.setSubentity("proc:status1"+processGroupConfiguration.getName());
							if (num > 0) {
								//num = num - times;
								List wrongProlist = new ArrayList();
								wrongProlist.add(processGroupConfiguration
										.getName());
								wrongProlist.add(num);
								wrongProlist.add(status);
								wrongList.add(wrongProlist);
							}
						} else {
							eventList.setSubentity("proc:status2"+processGroupConfiguration.getName());
							//白名单
							if (num < times) {
								// 丢失的个数
								num = times - num;
								List wrongProlist = new ArrayList();
								wrongProlist.add(processGroupConfiguration
										.getName());
								wrongProlist.add(num);
								wrongProlist.add(status);
								wrongList.add(wrongProlist);
							}else if(num > times){
								// 多出的个数
								num = num - times;
								List wrongProlist = new ArrayList();
								wrongProlist.add(processGroupConfiguration
										.getName());
								wrongProlist.add(num);
								wrongProlist.add(status);
								wrongList.add(wrongProlist);
							}
						}
					}

					if (wrongList.size() > 0) {
						String message = ip + " 进程组为：" + processGroup.getName()
								+ " 出现进程异常!";
						for (int j = 0; j < wrongList.size(); j++) {
							List wrongProList = (List) wrongList.get(j);
							String status = (String) wrongProList.get(2);
							if ("1".equals(status)) {
								message = message + "进程：" + wrongProList.get(0)
										+ "超出个数为：" + wrongProList.get(1) + ";";
							} else {
								message = message + "进程：" + wrongProList.get(0)
										+ "丢失个数为：" + wrongProList.get(1) + ";";
							}
						}
						eventList.setEventtype("poll");
						eventList.setEventlocation(hostNode.getAlias() + "(" + ip
								+ ")");
						eventList.setContent(message);
						eventList.setLevel1(Integer.valueOf(processGroup
								.getAlarm_level()));
						eventList.setManagesign(0);
						eventList.setRecordtime(Calendar.getInstance());
						eventList.setReportman("系统轮询");
						eventList.setNodeid(hostNode.getId());
						eventList.setBusinessid(hostNode.getBid());
						eventList.setSubtype("host");
						
//						EventListDao eventlistdao = new EventListDao();
//						eventlistdao.save(eventList);

						retList.add(eventList);
						try {
							/*
							 * nielin modify 2010-10-25 start 将 createSMS 注释 改为
							 * 使用告警过滤后发送告警
							 */
							// createSMS(eventList.getSubtype(),
							// eventList.getSubentity(),ip , hostNode.getId() + "",
							// message , eventList.getLevel1() , 1 ,
							// processGroup.getName() ,
							// eventList.getBusinessid(),hostNode.getAlias() + "(" +
							// ip + ")");
							sendAlarm(eventList, alarmIndicatorsNode, processGroup.getName());
							/* nielin modify 2010-10-25 end */
						} catch (Exception e) {

						}
					}else {
						String name = alarmIndicatorsNode.getNodeid()+ ":" + alarmIndicatorsNode.getType() + ":" + alarmIndicatorsNode.getSubtype() + ":"+alarmIndicatorsNode.getName()+":" + processGroup.getName();
						this.deleteEvent( alarmIndicatorsNode.getNodeid()+"", alarmIndicatorsNode.getType(), alarmIndicatorsNode.getSubtype(), alarmIndicatorsNode.getName(), processGroup.getName());
						//在告警没有完全恢复到正常的状态，将不删除告警时间
						if (ShareData.getSendAlarmTimes() != null && (ShareData.getSendAlarmTimes().containsKey(name + ":3") || ShareData.getSendAlarmTimes().containsKey(name + ":2") || ShareData.getSendAlarmTimes().containsKey(name + ":1"))) {

							SendAlarmTimeDao sendAlarmTimeDao = new SendAlarmTimeDao();
							try {
								sendAlarmTimeDao.deleteByName(name);
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								sendAlarmTimeDao.close();
							}
							if(ShareData.getSendAlarmTimes().containsKey(name + ":3")){
								ShareData.getSendAlarmTimes().remove(name + ":3");
							}
							if(ShareData.getSendAlarmTimes().containsKey(name + ":2")){
								ShareData.getSendAlarmTimes().remove(name + ":2");
							}
							if(ShareData.getSendAlarmTimes().containsKey(name + ":1")){
								ShareData.getSendAlarmTimes().remove(name + ":1");
							}
						}
					}

				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// SysLogger.info("=========进程告警指标====================size:"+retList.size());
			return retList;
		}
	    
	    /**
	     * 检查主机服务组告警
	     * @param ip
	     * @param hostServiceVector
	     * @return
	     */
		public List createHostServiceGroupEventList(String ip , Vector hostServiceVector, AlarmIndicatorsNode alarmIndicatorsNode){
			if (alarmIndicatorsNode == null) {
				SysLogger.info("==========无主机服务组告警==========");
				return null;
			}
			List returnList = new ArrayList();
			if (hostServiceVector == null || hostServiceVector.size() == 0)
				return returnList;

			try {

				Node hostNode = PollingEngine.getInstance().getNodeByIP(ip);

				HostServiceGroupConfigurationUtil hostServiceGroupConfigurationUtil = new HostServiceGroupConfigurationUtil();
				List list = hostServiceGroupConfigurationUtil
						.gethostservicegroupByIpAndMonFlag(ip, "1");

				if (list == null || list.size() == 0) {
					return returnList;
				}

				for (int i = 0; i < list.size(); i++) {
					HostServiceGroup hostServiceGroup = (HostServiceGroup) list
							.get(i);
					List hostServiceList = hostServiceGroupConfigurationUtil
							.gethostservicegroupConfigurationByGroupId(String
									.valueOf(hostServiceGroup.getId()));

					if (hostServiceList == null || hostServiceList.size() == 0) {
						continue;
					}

					List wrongList = new ArrayList();

					// 有告警的黑名单服务列表
					List blackWrongList = new ArrayList();

					for (int j = 0; j < hostServiceList.size(); j++) {
						HostServiceGroupConfiguration hostServiceGroupConfiguration = (HostServiceGroupConfiguration) hostServiceList
								.get(j);
						String status = hostServiceGroupConfiguration.getStatus();// 1:活动（黑名单）
						// 0:不活动（白名单）
						boolean isLived = false;

						if (hostServiceVector != null) {
							for (int k = 0; k < hostServiceVector.size(); k++) {
								Servicecollectdata servicedata = (Servicecollectdata) hostServiceVector
										.get(k);
								if (hostServiceGroupConfiguration.getName().trim()
										.equals(servicedata.getName())) {
									isLived = true;
									break;
								}
							}
						}

						if (!isLived && "0".equals(status)) {// 增加白名单判断条件 (不存在该进程
							// 则告警)
							wrongList.add(hostServiceGroupConfiguration);
						}
						if (isLived && "1".equals(status)) {// 增加黑名单判断条件 (存在该进程则告警)
							blackWrongList.add(hostServiceGroupConfiguration);
						}
					}
					StringBuffer message = new StringBuffer();
					message.append(ip);
					message.append(" 主机服务组为：");
					message.append(hostServiceGroup.getName());
					message.append("出现主机服务告警! ");
//					if (wrongList.size() > 0) {
//						for (int j = 0; j < wrongList.size(); j++) {
//							HostServiceGroupConfiguration hostServiceGroupConfiguration = (HostServiceGroupConfiguration) wrongList
//									.get(j);
//							message.append("主机服务：");
//							message.append(hostServiceGroupConfiguration.getName());
//							message.append("丢失！; ");
//						}
//					}
//					if (blackWrongList.size() > 0) {
//						for (int j = 0; j < blackWrongList.size(); j++) {
//							HostServiceGroupConfiguration hostServiceGroupConfiguration = (HostServiceGroupConfiguration) blackWrongList
//									.get(j);
//							message.append("主机服务：");
//							message.append(hostServiceGroupConfiguration.getName());
//							message.append("已启动！; ");
//						}
//					}
//					EventList eventList = new EventList();
//					eventList.setEventtype("poll");
//					eventList
//							.setEventlocation(hostNode.getAlias() + "(" + ip + ")");
//					eventList.setContent(message.toString());
//					eventList.setLevel1(Integer.parseInt(hostServiceGroup
//							.getAlarm_level()));
//					eventList.setManagesign(0);
//					eventList.setRecordtime(Calendar.getInstance());
//					eventList.setReportman("系统轮询");
//					eventList.setNodeid(hostNode.getId());
//					eventList.setBusinessid(hostNode.getBid());
//					eventList.setSubtype("host");
//					eventList.setSubentity("hostservice");
//					returnList.add(eventList);
//					try {
//						sendAlarm(eventList, alarmIndicatorsNode,hostServiceGroup.getName());
//						// createSMS(eventList.getSubtype(),
//						// eventList.getSubentity(),ip , hostNode.getId() + "",
//						// message , eventList.getLevel1() , 1 ,
//						// hostServiceGroup.getName() ,
//						// eventList.getBusinessid(),hostNode.getAlias() + "(" + ip
//						// + ")");
//					} catch (Exception e) {
//
//					}
					if (wrongList.size() > 0||blackWrongList.size() > 0) {
						for (int j = 0; j < wrongList.size(); j++) {
							HostServiceGroupConfiguration hostServiceGroupConfiguration = (HostServiceGroupConfiguration) wrongList.get(j);
							message.append("主机服务：");
							message.append(hostServiceGroupConfiguration.getName());
							message.append("丢失！; ");
						}
						for (int j = 0; j < blackWrongList.size(); j++) {
							HostServiceGroupConfiguration hostServiceGroupConfiguration = (HostServiceGroupConfiguration) blackWrongList.get(j);
							message.append("主机服务：");
							message.append(hostServiceGroupConfiguration.getName());
							message.append("已启动！; ");
						}
						EventList eventList = new EventList();
						eventList.setEventtype("poll");
						eventList.setEventlocation(hostNode.getAlias() + "(" + ip + ")");
						eventList.setContent(message.toString());
						eventList.setLevel1(Integer.parseInt(hostServiceGroup.getAlarm_level()));
						eventList.setManagesign(0);
						eventList.setRecordtime(Calendar.getInstance());
						eventList.setReportman("系统轮询");
						eventList.setNodeid(hostNode.getId());
						eventList.setBusinessid(hostNode.getBid());
						eventList.setSubtype("host");
						eventList.setSubentity("hostservice");
						returnList.add(eventList);
						try {
							sendAlarm(eventList, alarmIndicatorsNode, hostServiceGroup.getName());
							
						} catch (Exception e) {
	                          SysLogger.error("CheckEventUtil.createHostServiceGroupEventList() sendAlarm告警error",e);
						}
					}else {
						String name = alarmIndicatorsNode.getNodeid()+ ":" + alarmIndicatorsNode.getType() + ":" + alarmIndicatorsNode.getSubtype() + ":"+alarmIndicatorsNode.getName()+":" + hostServiceGroup.getName();
						this.deleteEvent( alarmIndicatorsNode.getNodeid()+"", alarmIndicatorsNode.getType(), alarmIndicatorsNode.getSubtype(), alarmIndicatorsNode.getName(), hostServiceGroup.getName());
						//在告警没有完全恢复到正常的状态，将不删除告警时间
						if (ShareData.getSendAlarmTimes() != null && (ShareData.getSendAlarmTimes().containsKey(name + ":3") || ShareData.getSendAlarmTimes().containsKey(name + ":2") || ShareData.getSendAlarmTimes().containsKey(name + ":1"))) {

							SendAlarmTimeDao sendAlarmTimeDao = new SendAlarmTimeDao();
							try {
								sendAlarmTimeDao.deleteByName(name);
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								sendAlarmTimeDao.close();
							}
							if(ShareData.getSendAlarmTimes().containsKey(name + ":3")){
								ShareData.getSendAlarmTimes().remove(name + ":3");
							}
							if(ShareData.getSendAlarmTimes().containsKey(name + ":2")){
								ShareData.getSendAlarmTimes().remove(name + ":2");
							}
							if(ShareData.getSendAlarmTimes().containsKey(name + ":1")){
								ShareData.getSendAlarmTimes().remove(name + ":1");
							}
						}	
					}
				}
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return returnList;
		}
		/**
		 * 创建as400子系统告警
		 * @param ip
		 * @param hostServiceVector
		 * @return
		 */
		public List createJobForAS400SubSystemEventList(String ip , List jobForAS400list, List subSystemForAS400list, AlarmIndicatorsNode alarmIndicatorsNode){
			if(alarmIndicatorsNode==null){
//				SysLogger.info("==========无as400SubSystem告警指标==========");
				return null;
			}
			List returnList = new ArrayList();
//			System.out.println("jobForAS400list.size()="+jobForAS400list.size());
//			System.out.println("subSystemForAS400list.size()="+subSystemForAS400list.size());
			if(jobForAS400list == null || jobForAS400list.size()==0 || subSystemForAS400list == null || subSystemForAS400list.size()==0)return returnList;
			String path = "";
			try {
//				System.out.println("as400子系统开始告警");
				Node hostNode = PollingEngine.getInstance().getNodeByIP(ip);
				JobForAS400GroupDetailUtil jobForAS400GroupDetailUtil = new JobForAS400GroupDetailUtil();
				List list = jobForAS400GroupDetailUtil.getJobForAS400SubSystemByIpAndMonFlag(ip, "1");
//				System.out.println("as400子系统开始告警：list.size()="+list.size());
				if(list == null || list.size() == 0){
					return returnList;
				}
				NodeDTO node = null;
				NodeUtil nodeUtil = new NodeUtil();
        		node = nodeUtil.conversionToNodeDTO(hostNode);

				for(int i = 0 ; i < list.size() ; i++){
					try {
						JobForAS400SubSystem jobForAS400SubSystem = (JobForAS400SubSystem)list.get(i);
						
						for(int j = 0 ; j < subSystemForAS400list.size(); j++){
							SubsystemForAS400 subsystemForAS400 = (SubsystemForAS400)subSystemForAS400list.get(j);
							if(subsystemForAS400.getName().equalsIgnoreCase(jobForAS400SubSystem.getName())){
								path = subsystemForAS400.getPath();
								break;
							}
						}
//						System.out.println("as400子系统开始告警：path="+path);
						List wrongList = new ArrayList();
						
                        //将内存中相关的告警清除,最终实现从数据库表里删除相关数据
//						CheckEvent lastCheckEvent = deleteEvent(node.getId()+"", node.getType(), node.getSubtype(), alarmIndicatorsNode.getName(), jobForAS400SubSystem.getName());
						//将内存中相关的告警清除,最终实现从数据库表里删除相关数据
//						deleteValue(node, alarmIndicatorsNode, "", jobForAS400SubSystem.getName());
							
						boolean isLived = false;
						List jobForAS400List2 = new ArrayList();
						if(jobForAS400list != null){
							for(int k = 0 ; k < jobForAS400list.size() ; k++){
								JobForAS400 jobForAS400 = (JobForAS400)jobForAS400list.get(k);
								if(path.equals(jobForAS400.getSubsystem())){
									jobForAS400List2.add(jobForAS400);
									isLived = true;
								}
							}
						}
						
						String eventMessage = "";
						
						Vector perVector = new Vector();

						if(!"-1".equals(jobForAS400SubSystem.getActive_status_type())){
							// 如果 子系统作业的活动的监控状态不是不限 则进行判断
							try {
								int num = Integer.valueOf(jobForAS400SubSystem.getNum());
								if(num > jobForAS400List2.size()){
									eventMessage = "子系统：" + jobForAS400SubSystem.getName() + " 出现异常,作业个数少于监控数目,丢失：" + (num - jobForAS400List2.size()) + "个;";
								} else if(num < jobForAS400List2.size()){
									eventMessage = "子系统：" + jobForAS400SubSystem.getName() + " 出现异常,作业个数多于监控数目,增加：" + (jobForAS400List2.size() - num) + "个;";
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							
							String activeStatus = jobForAS400SubSystem.getActive_status();
							
							if(activeStatus!=null){
								for(int m = 0 ; m < jobForAS400List2.size() ; m++){
									JobForAS400 jobForAS400 = (JobForAS400)jobForAS400List2.get(m);
									// 判断每一个出现的作业
									if("1".equals(jobForAS400SubSystem.getActive_status_type())!= (activeStatus.indexOf(jobForAS400.getActiveStatus()) != -1) ){
										// 如果 作业活动状态类型为必须出现 则 活动状态必须在当前监控状态中 ；如果不对出现异常
										// 如果 作业活动状态类型为不允许出现 则 活动状态不能出现在当前监控状态中 ； 如果不对 出现异常
										eventMessage = eventMessage + "子系统：" + jobForAS400SubSystem.getName() + " 作业任务出现异常状态为; 其状态为：" + jobForAS400.getActiveStatus() + ";";
									}
								}
							}
							
							if(eventMessage.trim().length() > 1){
								perVector.add(eventMessage);
							}
						}
					
						if(perVector.size()>0){
							wrongList.add(perVector);
						}
//						System.out.println("as400子系统开始告警：wrongList.size()="+wrongList.size());
						if(wrongList.size() > 0){
							String message = ip + " 的子系统：" + jobForAS400SubSystem.getName() + " 出现异常!";
							for(int j = 0 ; j < wrongList.size() ; j ++){
								Vector perVector1 = (Vector)wrongList.get(j);
								message = message + perVector1.get(0);
							
							}
							EventList eventList = new EventList();
							eventList.setEventtype("poll");
							eventList.setEventlocation(hostNode.getAlias() + "(" + ip + ")" + " 子系统为：" + jobForAS400SubSystem.getName());
							eventList.setContent(message);
							eventList.setLevel1(Integer.parseInt(jobForAS400SubSystem.getAlarm_level()));
							eventList.setManagesign(0);
							eventList.setRecordtime(Calendar.getInstance());
							eventList.setReportman("系统轮询");
							eventList.setNodeid(hostNode.getId());
							eventList.setBusinessid(hostNode.getBid());
							eventList.setSubtype("host");
							eventList.setSubentity("subsystem");
							sendAlarm(eventList, alarmIndicatorsNode);
						} else {
//							判断之前是否有告警,若有则发送告警恢复信息
//							System.out.println("as400子系统开始告警：lastCheckEvent="+lastCheckEvent);
//			 				if(lastCheckEvent!=null){
//			 					// 之前有告警 ， 发送告警恢复信息
//			 					// TODO 这里调用告警恢复信息的发送
//			 					try{
////			 						System.out.println("as400子系统开始告警：jobForAS400SubSystem.getName()="+jobForAS400SubSystem.getName());
//			 						sendAlert(node, alarmIndicatorsNode, "", 0 , jobForAS400SubSystem.getName());
//			 	 				}catch(Exception e){
//			 	 					e.printStackTrace();
//			 	 				}
//			 				}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
	    	return returnList;
	    }		
		
		/**
		 * 创建as400工作组告警
		 * @param ip
		 * @param hostServiceVector
		 * @return
		 */
		public List createJobForAS400GroupEventList(String ip , List jobForAS400list, AlarmIndicatorsNode alarmIndicatorsNode){
			if(alarmIndicatorsNode==null){
				SysLogger.info("==========无as400JOB告警指标==========");
				return null;
			}
			List returnList = new ArrayList();
			if(jobForAS400list == null || jobForAS400list.size()==0)return returnList;
			
			try {
				
				Node hostNode = PollingEngine.getInstance().getNodeByIP(ip);
				JobForAS400GroupDetailUtil jobForAS400GroupDetailUtil = new JobForAS400GroupDetailUtil();
				List list = jobForAS400GroupDetailUtil.getJobForAS400GroupByIpAndMonFlag(ip, "1");
				
				if(list == null || list.size() == 0){
					return returnList;
				}
				
				for(int i = 0 ; i < list.size() ; i++){
					try {
						JobForAS400Group jobForAS400Group = (JobForAS400Group)list.get(i);
						List jobForAS400DetailList = jobForAS400GroupDetailUtil.getJobForAS400GroupDetailByGroupId(String.valueOf(jobForAS400Group.getId()));
						
						if(jobForAS400DetailList == null || jobForAS400DetailList.size() ==0){
							continue;
						}
						
						List wrongList = new ArrayList();
						
						
						for(int j = 0 ; j < jobForAS400DetailList.size() ; j++){
							JobForAS400GroupDetail jobForAS400GroupDetail = (JobForAS400GroupDetail) jobForAS400DetailList.get(j);
							
							boolean isLived = false;
							List jobForAS400List2 = new ArrayList();
							if(jobForAS400list != null){
								for(int k = 0 ; k < jobForAS400list.size() ; k++){
									JobForAS400 jobForAS400 = (JobForAS400)jobForAS400list.get(k);
									if(jobForAS400GroupDetail.getName().trim().equals(jobForAS400.getName())){
										jobForAS400List2.add(jobForAS400);
										isLived = true;
									}
								}
							}
							
							String eventMessage = "";
							
							Vector perVector = new Vector();
							if(jobForAS400GroupDetail.getStatus().equals("0") && isLived ){
								// 如果 作业出现 并且 作业的监控状态为不允许出现 则告警
								perVector.add(jobForAS400GroupDetail);
								perVector.add("作业：" + jobForAS400GroupDetail.getName() + " 出现活动,且个数为：" + jobForAS400List2.size() + ";");
							} else if(jobForAS400GroupDetail.getStatus().equals("1") && !isLived ){
								// 如果 作业未出现 并且 作业的监控状态为必须出现 则告警
								perVector.add(jobForAS400GroupDetail);
								perVector.add("作业：" + jobForAS400GroupDetail.getName() + " 未活动;");
							} else if(!jobForAS400GroupDetail.getStatus().equals("0") && isLived){
								// 如果 作业出现 并且 作业的监控状态为允许出现 则进一步判断
								if(!"-1".equals(jobForAS400GroupDetail.getActiveStatusType())){
									// 如果 作业的活动的监控状态不是不限 则进行判断
									
									try {
										int num = Integer.valueOf(jobForAS400GroupDetail.getNum());
										if(num > jobForAS400List2.size()){
											eventMessage = "作业：" + jobForAS400GroupDetail.getName() + " 出现异常,个数少于监控数目,丢失：" + (num - jobForAS400List2.size()) + "个";
										}
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
									String activeStatus = jobForAS400GroupDetail.getActiveStatus();
									if(activeStatus!=null){
										for(int m = 0 ; m < jobForAS400List2.size() ; m++){
											JobForAS400 jobForAS400 = (JobForAS400)jobForAS400List2.get(m);
											// 判断每一个出现的作业
											if("1".equals(jobForAS400GroupDetail.getActiveStatusType()) 
													!= (activeStatus.indexOf(jobForAS400.getActiveStatus()) != -1) ){
												// 如果 作业活动状态类型为必须出现 则 活动状态必须在当前监控状态中 ；如果不对出现异常
												// 如果 作业活动状态类型为不允许出现 则 活动状态不能出现在当前监控状态中 ； 如果不对 出现异常
												eventMessage = eventMessage + "作业：" + jobForAS400GroupDetail.getName() + " 出现异常状态为; 其状态为：" + jobForAS400.getActiveStatus() + ";";
											}
										}
									}
									
									if(eventMessage.trim().length() > 1){
										perVector.add(jobForAS400GroupDetail);
										perVector.add(eventMessage);
									}
								}
							}
							if(perVector.size()>1){
								wrongList.add(perVector);
							}
						}
						if(wrongList.size() > 0){
							String message = ip + " 的作业组：" + jobForAS400Group.getName() + " 出现异常!";
							for(int j = 0 ; j < wrongList.size() ; j ++){
								Vector perVector = (Vector)wrongList.get(j);
								JobForAS400GroupDetail jobForAS400GroupDetail = (JobForAS400GroupDetail)perVector.get(0);
								message = message + perVector.get(1);
							
							}
							EventList eventList = new EventList();
							eventList.setEventtype("poll");
							eventList.setEventlocation(hostNode.getAlias() + "(" + ip + ")" + " 作业组为：" + jobForAS400Group.getName());
							eventList.setContent(message);
							eventList.setLevel1(Integer.parseInt(jobForAS400Group.getAlarm_level()));
							eventList.setManagesign(0);
							eventList.setRecordtime(Calendar.getInstance());
							eventList.setReportman("系统轮询");
							eventList.setNodeid(hostNode.getId());
							eventList.setBusinessid(hostNode.getBid());
							eventList.setSubtype("host");
							eventList.setSubentity("jobForAS400Gourp");
							sendAlarm(eventList, alarmIndicatorsNode);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	return returnList;
	    }
		
		/**
		 * 确认是否为告警 
		 * <p>当发生事件的次数大于规定的次数 则作为告警，并返回告警的等级
		 * 	  如果不告警则返回<p>
		 * @param node		设备
		 * @param nm		指标
		 * @param value		值
		 * @param name		告警唯一标志	
		 * @return
		 */
		private int checkAlarm(NodeDTO node, AlarmIndicatorsNode nm, double value, String name){
			// SysLogger.info("===========开始确认是否为告警=============");
			int alarmLevel = 0;
			int eventLevel = 0; // 事件等级
			int eventTimes = 0; // 事件次数
			double limenvalue0 = Double.parseDouble(nm.getLimenvalue0());// 一级阀值
			double limenvalue1 = Double.parseDouble(nm.getLimenvalue1());// 二级阀值
			double limenvalue2 = Double.parseDouble(nm.getLimenvalue2());// 三级阀值
			// 检查事件等级
			eventLevel = checkEventLevel(value, limenvalue0, limenvalue1,limenvalue2, nm.getCompare());
			// SysLogger.info("========检查事件等级===========" + eventLevel);
			// 检查事件次数
			// 如果传入的事件等级为 0 则说明事件恢复 则进行清楚 事件次数
			// 如果返回次数大于 0 则说明大于规定的事件次数 将事件升级为告警 如果返回次数不大于 0 则只是将事件次数 + 1;;
			eventTimes = checkEventTimes(nm, eventLevel, name);
			// SysLogger.info("======== 检查事件次数 ===========" + eventTimes);
			if (eventTimes > 0) {
				// 如果大于 0 将事件等级升级 为 告警等级
				alarmLevel = eventLevel;
			}
			return alarmLevel;

		}
		private int checkDetailAlarm(NodeDTO node, AlarmIndicatorsNode nm, double value, String name){
			//SysLogger.info("===========开始确认是否为告警=============");
			int alarmLevel = 0;
	   	 	int eventLevel = 0;			// 事件等级
	   	 	int eventTimes = 0;			// 事件次数
			double limenvalue = Double.parseDouble(nm.getLimenvalue0());//阀值
			
			// 检查事件等级
			if(value == limenvalue){
				eventLevel=Integer.parseInt(nm.getAlarm_level());
			// 检查事件次数 
				eventTimes = checkEventTimes(nm, eventLevel, name);
			if(eventTimes > 0){
				// 如果大于 0 将事件等级升级 为 告警等级
				alarmLevel = eventLevel;
			}
			}
			return alarmLevel;
			
	}
		/**
		 * 进入告警发送
		 * 
		 * @param node			设备
		 * @param alarmIndicatorsNode	指标
		 * @param value			采集到的值
		 * @param alarmLevel	告警等级
		 */
//        public void sendAlarm(NodeDTO node, AlarmIndicatorsNode alarmIndicatorsNode, String value, int alarmLevel){
//			CheckEvent checkEvent = new CheckEvent();
//			checkEvent.setName(node.getId()+":"+alarmIndicatorsNode.getType()+":"+alarmIndicatorsNode.getName());
//			checkEvent.setAlarmlevel(alarmLevel);
//			//将该告警对比信息放到内存系统里,最终需要放到数据库表里
//			saveEvent(checkEvent);
//			// --------------------
//			String unit = alarmIndicatorsNode.getThreshlod_unit();
//			String threshold = getThresholdByLevel(alarmIndicatorsNode, alarmLevel);
//			String eventtype = "poll";
//			String eventlocation = node.getName() + "(" + node.getName() + ")";
//			String bid = node.getBusinessId();
//			String content = node.getName()+"(IP: "+node.getIpaddress()+") "+alarmIndicatorsNode.getAlarm_info()
//							 + " 当前值:"+ value + " " + unit 
//							 + " 阀值:" + threshold + " " + unit;
//			Integer level1 = alarmLevel;
//			String subtype = "";
//			if("service".equalsIgnoreCase(alarmIndicatorsNode.getType())){
//				subtype = alarmIndicatorsNode.getSubtype();
//			}else{
//				subtype = alarmIndicatorsNode.getType();
//			}
//			//String subtype = alarmIndicatorsNode.getType();
//			String subentity = alarmIndicatorsNode.getName();
//			String objid = node.getId() + "";
//			String ipaddress = node.getIpaddress();
//			// 创建 eventList 
//			EventList eventList = createEvent(eventtype, eventlocation, bid, content, level1, subtype, subentity, ipaddress, objid);
//        	SendAlarmUtil alarmUtil = new SendAlarmUtil();
//            alarmUtil.sendAlarm(checkEvent ,eventList, alarmIndicatorsNode);
//            
//            
//        }
        /**
		 * 进入端口告警发送
		 * 
		 * @param node			设备
		 * @param portNode	指标
		 * @param value			采集到的值
		 * @param alarmLevel	告警等级
		 * 
		 */
//        public void sendAlarmPort(NodeDTO node, AlarmPort portNode, String value, int alarmLevel,String inOrOut){
//			CheckEvent checkEvent = new CheckEvent();
//			
//			checkEvent.setName(portNode.getId()+":"+portNode.getType()+":"+portNode.getName());
//			int realLevel=Integer.valueOf(alarmLevel);
//			if (realLevel>3) {
//				realLevel=realLevel-3;
//			}
//			checkEvent.setAlarmlevel(realLevel);
//			//将该告警对比信息放到内存系统里,最终需要放到数据库表里
//			saveEvent(checkEvent);
//			// --------------------
//			String unit = "kb/s";
//			int threshold=0;
//			if(alarmLevel == 1){
//				threshold = portNode.getLevelinvalue1();
//			} else if (alarmLevel == 2){
//				threshold = portNode.getLevelinvalue2();
//			}else if (alarmLevel == 3){
//				threshold = portNode.getLevelinvalue3();
//			}else if (alarmLevel == 4){
//				threshold = portNode.getLeveloutvalue1();
//			}else if (alarmLevel == 5){
//				threshold = portNode.getLeveloutvalue2();
//			}else if (alarmLevel == 6){
//				threshold = portNode.getLeveloutvalue3();
//			}
//			
//			String eventtype = "poll";
//			String eventlocation = portNode.getName() + "(IP: "+node.getIpaddress()+") ";
//			String bid = node.getBusinessId();
//			String content = "";
//		//	if(checkflag == 0){
//				content = portNode.getName()+"(IP: "+node.getIpaddress()+") "+inOrOut+portNode.getAlarm_info()
//							+ " 当前值:"+ value + " " + unit 
//							 + " 阀值:" + threshold + " " + unit;
////			}else{
////				content = node.getName()+"(IP: "+node.getIpaddress()+") "+portNode.getAlarm_info();
////			}
//							 
//			Integer level1 = alarmLevel;
//			String subtype = "";
//			if("service".equalsIgnoreCase(portNode.getType())){
//				subtype = portNode.getSubtype();
//			}else{
//				subtype = portNode.getType();
//			}
//			
//			String subentity = "utilUdx";
//			String objid = node.getId() + "";
//			String ipaddress = node.getIpaddress();
//			// 创建 eventList 
//			EventList eventList = createEvent(eventtype, eventlocation, bid, content, level1, subtype, subentity, ipaddress, objid);
//       	SendAlarmUtil alarmUtil = new SendAlarmUtil();
//       	alarmUtil.sendPortAlarm(checkEvent ,eventList, alarmLevel,portNode);
//            
//            
//        }
		/**
		 * 进入告警发送
		 * 
		 * @param node
		 *            设备
		 * @param alarmIndicatorsNode
		 *            指标
		 * @param value
		 *            采集到的值
		 * @param alarmLevel
		 *            告警等级
		 * @param checkflag
		 *            判断是字符还是数字格式,若为字符,则不需要把具体的阀值写进事件里去,0:数字 1:字符
		 */
		public void sendAlarm(NodeDTO node,AlarmIndicatorsNode alarmIndicatorsNode, String value,int alarmLevel, int checkflag) {
			String unit = alarmIndicatorsNode.getThreshlod_unit();
			String threshold = getThresholdByLevel(alarmIndicatorsNode, alarmLevel);
			String eventtype = "poll";
			String eventlocation = node.getName() + "(" + node.getName() + ")";
			String bid = node.getBusinessId();
			String content = "";
			if (checkflag == 0) {
				content = node.getName() + "(IP: " + node.getIpaddress() + ") "
						+ alarmIndicatorsNode.getAlarm_info() + " 当前值:" + value
						+ " " + unit + " 阀值:" + threshold + " " + unit;
			} else {
				content = node.getName() + "(IP: " + node.getIpaddress() + ") "
						+ alarmIndicatorsNode.getAlarm_info();
			}

			Integer level1 = alarmLevel;
			String subtype = "";
			if ("service".equalsIgnoreCase(alarmIndicatorsNode.getType())) {
				subtype = alarmIndicatorsNode.getSubtype();
			} else {
				subtype = alarmIndicatorsNode.getType();
			}
			// String subtype = alarmIndicatorsNode.getType();
			String subentity = alarmIndicatorsNode.getName();
			String objid = node.getId() + "";
			String ipaddress = node.getIpaddress();
			// 创建 eventList
			EventList eventList = createEvent(eventtype, eventlocation, bid,
					content, level1, subtype, subentity, ipaddress, objid);
			// 做告警判断，判断内存中是否有告警，如果有则不存数据库
			CheckEvent checkEvent = new CheckEvent();
//			checkEvent.setName(node.getId() + ":" + alarmIndicatorsNode.getType()+ ":" + alarmIndicatorsNode.getName());
			checkEvent.setIndicatorsName(alarmIndicatorsNode.getName());
			checkEvent.setContent(eventList.getContent());
			checkEvent.setNodeid(eventList.getNodeid() + "");
			checkEvent.setSindex("");
			checkEvent.setThevalue(value);
			checkEvent.setBid(bid);
			checkEvent.setSubtype(alarmIndicatorsNode.getSubtype());
			checkEvent.setType(alarmIndicatorsNode.getType());
			checkEvent.setAlarmlevel(eventList.getLevel1());
			checkEvent.setAlarmlevel(alarmLevel);
			// 将该告警对比信息放到内存系统里,最终需要放到数据库表里
			// 判断是否保存checkevent事件信息
			boolean flag = true;
			Hashtable checkEventHash = ShareData.getCheckEventHash();
			if (checkEventHash != null && checkEventHash.size() > 0) {
				if (checkEventHash.containsKey(node.getId() + ":"
						+ alarmIndicatorsNode.getType() + ":" + alarmIndicatorsNode.getSubtype() + ":"
						+ alarmIndicatorsNode.getName())) {
					flag = false;// 存在时，checkevent不入库
				}
			}
			if (flag) {
				// 若内存中不存在告警，表明是第一次产生告警，存入数据库表中
				checkEvent.setCollecttime(CommonUtil.getDateAndTime());
				saveEvent(checkEvent);
			}
			// --------------------
			
			SendAlarmUtil alarmUtil = new SendAlarmUtil();
			alarmUtil.sendAlarm(checkEvent, eventList, alarmIndicatorsNode);

			// 将告警信息加入告警信息集合中
			NodeAlarmUtil.saveNodeAlarmInfo(eventList, subentity);
		}

		/**
		 * 进入告警发送
		 * 
		 * @param node
		 *            设备
		 * @param alarmIndicatorsNode
		 *            指标
		 * @param value
		 *            采集到的值
		 * @param value
		 *            相关告警信息
		 * @param alarmLevel
		 *            告警等级
		 * @param checkflag
		 *            判断是字符还是数字格式,若为字符,则不需要把具体的阀值写进事件里去,0:数字 1:字符
		 */
		public void sendAlarm(NodeDTO node,
				AlarmIndicatorsNode alarmIndicatorsNode, String value,
				String alarminfo, int alarmLevel, int checkflag) {
			String content = "";
			String unit = alarmIndicatorsNode.getThreshlod_unit();
			String threshold = getThresholdByLevel(alarmIndicatorsNode, alarmLevel);
			String eventtype = "poll";
			String eventlocation = node.getName() + "(" + node.getName() + ")";
			String bid = node.getBusinessId();
			if (checkflag == 0) {
				content = node.getName() + "(IP: " + node.getIpaddress() + ") "
						+ alarmIndicatorsNode.getAlarm_info() + " 当前值:" + value
						+ " " + unit + " 阀值:" + threshold + " " + unit;
			} else if (checkflag == 1) {
				content = node.getName() + "(IP: " + node.getIpaddress() + ":"
						+ alarminfo + ") " + alarmIndicatorsNode.getAlarm_info()
						+ " 当前值:" + value + " " + unit + " 阀值:" + threshold + " "
						+ unit;
			} else {
				content = node.getName() + "(IP: " + node.getIpaddress() + ") "
						+ alarmIndicatorsNode.getAlarm_info();
			}
			CheckEvent checkEvent = new CheckEvent();
			checkEvent.setIndicatorsName(alarmIndicatorsNode.getName());
			checkEvent.setType(alarmIndicatorsNode.getType());
			checkEvent.setSubtype(alarmIndicatorsNode.getSubtype());
			checkEvent.setNodeid(node.getNodeid() + "");
			checkEvent.setAlarmlevel(alarmLevel);
			checkEvent.setContent(content);
			checkEvent.setSindex("");
			checkEvent.setThevalue(value);
			checkEvent.setBid(bid);
			checkEvent.setAlarmlevel(alarmLevel);
			boolean flag = true;
			Hashtable checkEventHash = ShareData.getCheckEventHash();
			if (checkEventHash != null && checkEventHash.size() > 0) {
				if (checkEventHash.containsKey(node.getId() + ":"
						+ alarmIndicatorsNode.getType() + ":" + alarmIndicatorsNode.getSubtype() + ":"
						+ alarmIndicatorsNode.getName())) {
					flag = false;// 存在时，checkevent不入库
				}
			}
			if (flag) {
				// 若内存中不存在告警，表明是第一次产生告警，存入数据库表中
				checkEvent.setCollecttime(CommonUtil.getDateAndTime());
				saveEvent(checkEvent);
			}
			// --------------------

			Integer level1 = alarmLevel;
			String subtype = "";
			if ("service".equalsIgnoreCase(alarmIndicatorsNode.getType())) {
				subtype = alarmIndicatorsNode.getSubtype();
			} else {
				subtype = alarmIndicatorsNode.getType();
			}
			// String subtype = alarmIndicatorsNode.getType();
			String subentity = alarmIndicatorsNode.getName();
			String objid = node.getId() + "";
			String ipaddress = node.getIpaddress();
			// 创建 eventList
			EventList eventList = createEvent(eventtype, eventlocation, bid,
					content, level1, subtype, subentity, ipaddress, objid);
			SendAlarmUtil alarmUtil = new SendAlarmUtil();
			alarmUtil.sendAlarm(checkEvent, eventList, alarmIndicatorsNode);

		}
		
		/**
		 * wupinlong add 文件告警
		 * @param id
		 * @param ip
		 * @param eventtype
		 * @param eventlocation
		 * @param bid
		 * @param content
		 * @param level1
		 * @param type
		 * @param subtype
		 * @param subentity
		 * @param alarmWayId
		 * @param tag
		 */
	    public void sendAlarm(int id,String ip,String eventtype,String eventlocation,String bid,String content,Integer level1,String type,String subtype,String subentity,String alarmWayId,String tag){
			EventList eventlist = new EventList();
			eventlist.setNodeid(id);
			eventlist.setEventtype(eventtype);
			eventlist.setEventlocation(eventlocation);
			eventlist.setContent(content);
			eventlist.setLevel1(level1);
			eventlist.setManagesign(0);
			eventlist.setBak("");
			eventlist.setRecordtime(Calendar.getInstance());
			eventlist.setReportman("系统轮询");
			eventlist.setBusinessid(bid);
			eventlist.setOid(0);
			eventlist.setSubtype(subtype);
			eventlist.setSubentity(subentity);

	        //创建最新告警信息
			CheckEvent checkEvent = new CheckEvent();
			checkEvent.setAlarmlevel(1);	//告警级别
			checkEvent.setIndicatorsName(subentity);
			checkEvent.setType(type);
			checkEvent.setSubtype(subtype);
			checkEvent.setNodeid(id + "");
			checkEvent.setContent(content);
			checkEvent.setSindex("");
			checkEvent.setBid(bid);
			checkEvent.setThevalue("");
			try {
				if("0".equals(tag)){
					checkEvent.setCollecttime(CommonUtil.getDateAndTime());
					saveEvent(checkEvent);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			AlarmIndicatorsNode alarmIndicatorsNode = new AlarmIndicatorsNode();
			alarmIndicatorsNode.setNodeid(ip);
			alarmIndicatorsNode.setType(type);
			alarmIndicatorsNode.setName(subentity);
			
			SendAlarmUtil alarmUtil = new SendAlarmUtil();
	        alarmUtil.sendAlarm(checkEvent ,eventlist, alarmIndicatorsNode,alarmWayId);
		}
		

		/**
		 * 进入告警发送
		 * 
		 * @param node
		 *            设备
		 * @param alarmIndicatorsNode
		 *            指标
		 * @param value
		 *            采集到的值
		 * @param alarmLevel
		 *            告警等级
		 */
		public void sendAlarm(NodeDTO node,AlarmIndicatorsNode alarmIndicatorsNode, String value,int alarmLevel, String sIndex) {
			if (sIndex == null) {
				sIndex = "";
			}
			String unit = alarmIndicatorsNode.getThreshlod_unit();
			if ("无".equals(unit)) {
				unit = "";
			}
			String threshold = getThresholdByLevel(alarmIndicatorsNode, alarmLevel);
			String eventtype = "poll";
			String eventlocation = node.getName() + "(" + node.getIpaddress() + ")";
			String subentity = alarmIndicatorsNode.getName();
			if (sIndex.trim().length() > 0) {
				eventlocation = eventlocation + "(" + sIndex + ")";
				subentity=alarmIndicatorsNode.getName()+":"+sIndex;
			}
			String bid = node.getBusinessId();
			String content = eventlocation + " " + sIndex + " " + alarmIndicatorsNode.getAlarm_info() + " 当前值:" + value + " " + unit + " 阀值:" + threshold + " " + unit;
			Integer level1 = alarmLevel;
			String subtype = alarmIndicatorsNode.getType();
			
			String objid = node.getId() + "";
			String ipaddress = node.getIpaddress();

			// 创建最新告警信息
			CheckEvent checkEvent = new CheckEvent();
			checkEvent.setIndicatorsName(alarmIndicatorsNode.getName());
			checkEvent.setType(alarmIndicatorsNode.getType());
			checkEvent.setSubtype(alarmIndicatorsNode.getSubtype());
			checkEvent.setNodeid(node.getNodeid() + "");
			checkEvent.setAlarmlevel(alarmLevel);
			checkEvent.setContent(content);
			checkEvent.setSindex("");
			checkEvent.setBid(bid);
			checkEvent.setThevalue(value);
			checkEvent.setCollecttime(CommonUtil.getDateAndTime());
			if (sIndex != null && sIndex.length() > 0) {
				checkEvent.setSindex(sIndex);
			}
			//将该告警对比信息放到内存系统里,最终需要放到数据库表里
			saveEvent(checkEvent);
			// --------------------

			// 创建 eventList 
//			if("middleware".equalsIgnoreCase(subtype) || "service".equalsIgnoreCase(subtype))subtype=alarmIndicatorsNode.getSubtype();
			if("middleware".equalsIgnoreCase(subtype)||"service".equalsIgnoreCase(subtype))subtype=alarmIndicatorsNode.getSubtype();
			
			EventList eventList = createEvent(eventtype, eventlocation, bid, content, level1, subtype, subentity, ipaddress, objid);
			SendAlarmUtil alarmUtil = new SendAlarmUtil();
			alarmUtil.sendAlarm(checkEvent, eventList, alarmIndicatorsNode);
		}
        
		/**
		 * 进入告警发送
		 * 
		 * @param node
		 *            设备
		 * @param alarmIndicatorsNode
		 *            指标
		 * @param value
		 *            采集到的值
		 * @param alarmLevel
		 *            告警等级
		 */
		public void sendMiddlewareAlarm(NodeDTO node,
				AlarmIndicatorsNode alarmIndicatorsNode, String value,
				int alarmLevel, String sIndex) {
			// 此 name 做为 该告警的唯一标识符
//			String name = node.getId() + ":" + alarmIndicatorsNode.getType() + ":"
//					+ alarmIndicatorsNode.getSubtype() + ":"
//					+ alarmIndicatorsNode.getName();
//			if (sIndex != null && sIndex.trim().length() > 0) {
//				name = name + ":" + sIndex;
//			}
			String unit = alarmIndicatorsNode.getThreshlod_unit();
			String threshold = getThresholdByLevel(alarmIndicatorsNode, alarmLevel);
			String eventtype = "poll";
			String eventlocation = node.getName() + "(" + node.getName() + ")";
			String bid = node.getBusinessId();
			String content = node.getIpaddress() + " " + sIndex + " "
					+ alarmIndicatorsNode.getAlarm_info() + " 当前值:" + value + " "
					+ unit + " 阀值:" + threshold + " " + unit;
			CheckEvent checkEvent = new CheckEvent();
			checkEvent.setNodeid(String.valueOf(node.getId()));
			checkEvent.setIndicatorsName(alarmIndicatorsNode.getName());
			checkEvent.setType(alarmIndicatorsNode.getType());
			checkEvent.setSubtype(alarmIndicatorsNode.getSubtype());
			checkEvent.setSindex(sIndex);
			checkEvent.setThevalue(value);
			checkEvent.setContent(content);
			checkEvent.setBid(bid);
			checkEvent.setAlarmlevel(alarmLevel);
			boolean flag = true;
			Hashtable checkEventHash = ShareData.getCheckEventHash();
			if (checkEventHash != null && checkEventHash.size() > 0) {
				if (checkEventHash.containsKey(node.getId() + ":"
						+ alarmIndicatorsNode.getType() + ":" + alarmIndicatorsNode.getSubtype() + ":"
						+ alarmIndicatorsNode.getName())) {
					flag = false;// 存在时，checkevent不入库
				}
			}
			if (flag) {
				// 若内存中不存在告警，表明是第一次产生告警，存入数据库表中
				checkEvent.setCollecttime(CommonUtil.getDateAndTime());
				saveEvent(checkEvent);
			}
			// 将该告警对比信息放到内存系统里,最终需要放到数据库表里
			saveEvent(checkEvent);
			Integer level1 = alarmLevel;
			String subtype = alarmIndicatorsNode.getType();
			String subentity = alarmIndicatorsNode.getName();
			String objid = node.getId() + "";
			String ipaddress = node.getIpaddress();
			// 创建 eventList
			EventList eventList = createEvent(eventtype, eventlocation, bid,content, level1, subtype, subentity, ipaddress, objid);
			SendAlarmUtil alarmUtil = new SendAlarmUtil();
			alarmUtil.sendAlarm(checkEvent, eventList, alarmIndicatorsNode);
		}
		/**
		 * 进入告警发送
		 * 
		 * @param node			设备
		 * @param alarmIndicatorsNode	指标
		 * @param value			采集到的值
		 * @param alarmLevel	告警等级
		 */
		public void sendAlarm(EventList eventList, AlarmIndicatorsNode alarmIndicatorsNode, String sIndex) {
			CheckEvent checkEvent = new CheckEvent();
			//checkEvent.setName(eventList.getNodeid()+":"+alarmIndicatorsNode.getType()+":"+alarmIndicatorsNode.getName());
			checkEvent.setIndicatorsName(alarmIndicatorsNode.getName());
			checkEvent.setContent(eventList.getContent());
			checkEvent.setNodeid(eventList.getNodeid() + "");
			checkEvent.setSindex("");
			if (sIndex != null && sIndex.length() > 0) {
				checkEvent.setSindex(sIndex);
			}
			checkEvent.setThevalue("");
			checkEvent.setBid(eventList.getBusinessid());
			checkEvent.setSubtype(alarmIndicatorsNode.getSubtype());
			checkEvent.setType(alarmIndicatorsNode.getType());
			checkEvent.setAlarmlevel(eventList.getLevel1());
			boolean flag = true;
			Hashtable checkEventHash = ShareData.getCheckEventHash();
			if (checkEventHash != null && checkEventHash.size() > 0) {
				if (checkEventHash.containsKey(eventList.getNodeid() + ":"
						+ alarmIndicatorsNode.getType() + ":" + alarmIndicatorsNode.getSubtype() + ":"
						+ alarmIndicatorsNode.getName())) {
					flag = false;// 存在时，checkevent不入库
				}
			}
			if (flag) {
				// 若内存中不存在告警，表明是第一次产生告警，存入数据库表中
				checkEvent.setCollecttime(CommonUtil.getDateAndTime());
				saveEvent(checkEvent);
			}
			// --------------------
			SendAlarmUtil alarmUtil = new SendAlarmUtil();
			alarmUtil.sendAlarm(checkEvent, eventList, alarmIndicatorsNode);
		}
		/**
		 * 进入告警发送
		 * 
		 * @param node
		 *            设备
		 * @param alarmIndicatorsNode
		 *            指标
		 * @param value
		 *            采集到的值
		 * @param alarmLevel
		 *            告警等级
		 */
		public void sendAlarm(EventList eventList,
				AlarmIndicatorsNode alarmIndicatorsNode) {
			CheckEvent checkEvent = new CheckEvent();
			checkEvent.setIndicatorsName(alarmIndicatorsNode.getName());
			checkEvent.setContent(eventList.getContent());
			checkEvent.setNodeid(alarmIndicatorsNode.getNodeid() + "");
			checkEvent.setSindex("");
			checkEvent.setThevalue("");
			checkEvent.setBid(eventList.getBusinessid());
			checkEvent.setSubtype(alarmIndicatorsNode.getSubtype());
			checkEvent.setType(alarmIndicatorsNode.getType());
			checkEvent.setAlarmlevel(eventList.getLevel1());
			// 将该告警对比信息放到内存系统里,最终需要放到数据库表里
			boolean flag = true;
			Hashtable checkEventHash = ShareData.getCheckEventHash();
			if (checkEventHash != null && checkEventHash.size() > 0) {
				if (checkEventHash.containsKey(eventList.getNodeid() + ":"
						+ alarmIndicatorsNode.getType() + ":" + alarmIndicatorsNode.getSubtype() + ":"
						+ alarmIndicatorsNode.getName())) {
					flag = false;// 存在时，checkevent不入库
				}
			}
			if (flag) {
				// 若内存中不存在告警，表明是第一次产生告警，存入数据库表中
				checkEvent.setCollecttime(CommonUtil.getDateAndTime());
				saveEvent(checkEvent);
			}
			// --------------------
			SendAlarmUtil alarmUtil = new SendAlarmUtil();
			alarmUtil.sendAlarm(checkEvent, eventList, alarmIndicatorsNode);
		}
        
		/**
		 * 生成告警恢复事件
		 * 
		 * @param alarmIndicatorsNode
		 *            告警指标
		 * @param vo
		 *            设备VO
		 * @param value
		 *            当前指标值
		 * @return
		 */
		public synchronized EventList createEvent(
				AlarmIndicatorsNode alarmIndicatorsNode, Object vo, String value,
				String checkEventName) {
			NodeUtil nodeUtil = new NodeUtil();
			NodeDTO nodeDTO = null;
			Node node = (Node) vo;
			if (vo instanceof Web) {
				Web _web = (Web) PollingEngine.getInstance().getWebByID(node.getId());
				nodeDTO = nodeUtil.conversionToNodeDTO(_web);
			} else if (vo instanceof Host){
				Host host = (Host) PollingEngine.getInstance().getNodeByID(node.getId());
				nodeDTO = nodeUtil.conversionToNodeDTO(host);
			}
			if(nodeDTO!=null){
				String unit = alarmIndicatorsNode.getThreshlod_unit();
				String eventtype = "poll";
				String eventlocation = nodeDTO.getName() + "(" + nodeDTO.getName()
						+ ")";
				String bid = nodeDTO.getBusinessId();
				Hashtable checkEventHash = ShareData.getCheckEventHash();
				String time = null;// 告警持续时间，默认分钟为单位
				long timeLong = 0;
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				if (checkEventHash != null && checkEventHash.size() > 0) {
					if (checkEventHash.containsKey(checkEventName)) {
						CheckEventDao checkEventDao = new CheckEventDao();
						CheckEvent checkEvent = null;
						try {
							checkEvent = (CheckEvent) checkEventDao.findCheckEventByName(node.getId()+"",nodeDTO.getType(),nodeDTO.getSubtype(),alarmIndicatorsNode.getName(),null);
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							checkEventDao.close();
						}
						String collecttime = checkEvent.getCollecttime();
						Date firstAlarmDate = null;
						try {
							firstAlarmDate = formatter.parse(collecttime);
						} catch (ParseException e) {
							e.printStackTrace();
						}
						if (firstAlarmDate != null) {
							timeLong = new Date().getTime() - firstAlarmDate.getTime();
						}
					}
				}

				if (timeLong < 1000 * 60) {// 小于1分钟,秒
					time = timeLong / 1000 + "秒";
				} else {// 小于1小时,分
					time = timeLong / (60 * 1000) + "分";
				}
				String name = alarmIndicatorsNode.getName();
				if (alarmIndicatorsNode.getName().equals("diskperc")
						|| alarmIndicatorsNode.getName().equals("diskperc")) {
					int startNum = checkEventName.lastIndexOf(":");
					name = checkEventName.substring(startNum + 1);
				}
				String content = nodeDTO.getName() + "(IP: " + nodeDTO.getIpaddress()
						+ ") " + name + "当前值:" + value + " " + unit + " 告警已恢复，告警持续时间"
						+ time;
				Integer level1 = 0;
				String subtype = "";
				if ("service".equalsIgnoreCase(alarmIndicatorsNode.getType())) {
					subtype = alarmIndicatorsNode.getSubtype();
				} else {
					subtype = alarmIndicatorsNode.getType();
				}
				String subentity = alarmIndicatorsNode.getName();
				String objid = nodeDTO.getId() + "";
				String ipaddress = nodeDTO.getIpaddress();
				EventList eventlist = new EventList();
				eventlist.setEventtype(eventtype);
				eventlist.setEventlocation(eventlocation);
				eventlist.setContent(content);
				eventlist.setLevel1(level1);
				eventlist.setManagesign(0);
				eventlist.setBak("");
				eventlist.setRecordtime(Calendar.getInstance());
				eventlist.setReportman("系统轮询");
				eventlist.setBusinessid(bid);
				eventlist.setNodeid(Integer.parseInt(objid));
				eventlist.setOid(0);
				eventlist.setSubtype(subtype);
				eventlist.setSubentity(subentity);
				return eventlist;
			}
			return null;
		}
		/**
		 * 创建告警类
		 * 
		 * @param eventtype
		 * @param eventlocation
		 * @param bid
		 * @param content
		 * @param level1
		 * @param subtype
		 * @param subentity
		 * @param ipaddress
		 * @param objid
		 * @return
		 */
		private EventList createEvent(String eventtype, String eventlocation,
				String bid, String content, int level1, String subtype,
				String subentity, String ipaddress, String objid) {
			// 生成事件
//			SysLogger.info("##############开始生成事件############");
			EventList eventlist = new EventList();
			eventlist.setEventtype(eventtype);
			eventlist.setEventlocation(eventlocation);
			eventlist.setContent(content);
			eventlist.setLevel1(level1);
			eventlist.setManagesign(0);
			eventlist.setBak("");
			eventlist.setRecordtime(Calendar.getInstance());
			eventlist.setReportman("系统轮询");
			eventlist.setBusinessid(bid);
			eventlist.setNodeid(Integer.parseInt(objid));
			eventlist.setOid(0);
			eventlist.setSubtype(subtype);
			eventlist.setSubentity(subentity);
			return eventlist;
		}

		/**
		 * 
		 * 检查事件等级
		 * 
		 * <p>
		 * compare_type 比较方式，1 为升序 即大于比较，0 为降序 即小于比较
		 * </p>
		 * <p>
		 * 返回告警等级 如果不告警则返回 0
		 * </p>
		 * 
		 * @author nielin
		 * @param value
		 *            值
		 * @param limenvalue0
		 *            一级阀值
		 * @param limenvalue1
		 *            二级阀值
		 * @param limenvalue2
		 *            三级阀值
		 * @param compare_type
		 *            比较方式
		 * 
		 * @return level
		 */
		private int checkEventLevel(double value, double limenvalue0,
				double limenvalue1, double limenvalue2, int compare_type) {
			int level = 0; // 需要返回的等级
			// SysLogger.info(value + "=======" + compare_type);
			if (compare_type == 0) {
				// 降序比较
				if (value <= limenvalue2) {
					level = 3;
				} else if (value <= limenvalue1) {
					level = 2;
				} else if (value <= limenvalue0) {
					level = 1;
				} else {
					level = 0;
				}
			} else {
				// 升序比较
				if (value >= limenvalue2) {
					level = 3;
				} else if (value >= limenvalue1) {
					level = 2;
				} else if (value >= limenvalue0) {
					level = 1;
				} else {
					level = 0;
				}
			}
			return level;
		}

		/**
		 * 检查告警次数
		 * <p>
		 * 如果大于规定的告警次数，返回一个大于 0的数 否则返回 0 ，并同时将告警次数 + 1
		 * <p>;
		 * 
		 * @param alarmIndicatorsNode
		 *            告警指标
		 * @param alarmLevel
		 *            当前告警等级
		 * @return
		 */
		private int checkEventTimes(AlarmIndicatorsNode alarmIndicatorsNode,int eventLevel, String name) {
			int eventTimes = 0; // 发生事件的次数
			int defineTimes = 0; // 定义的连续发生事件次数
			int lastEventTimes = 0; // 之前发生的事件次数
			if (eventLevel == 0) {
				// 如果事件等级 为 0 说明事件恢复 则清除事件次数归为 0
				setEventTimes(name, 0);
				return eventTimes;
			}

			defineTimes = getTimesByLevel(alarmIndicatorsNode, eventLevel);
			lastEventTimes = getEventTimes(name, eventLevel);
			eventTimes = lastEventTimes + 1; // 上次加这次 然后保存
			setEventTimes(name, eventTimes);
			if (eventTimes < defineTimes) {
				// 如果小于定义的次数则不产生告警 返回 0
				eventTimes = 0;
			}
			return eventTimes;
		}

		/**
		 * 得到之前的告警次数
		 * 
		 * @param name
		 * @param alarmLevel
		 * @return
		 */
		private int getEventTimes(String name, int alarmLevel) {
			int times = 0;
			try {
				String num = (String) AlarmResourceCenter.getInstance()
						.getAttribute(name);
				if (num != null && num.length() > 0) {
					times = Integer.parseInt(num);
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return times;
		}

		/**
		 * 设置告警次数
		 * 
		 * @param name
		 * @param times
		 * @return
		 */
		private int setEventTimes(String name, int times) {
			try {
				AlarmResourceCenter.getInstance().setAttribute(name,String.valueOf(times));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return times;
		}

		/**
		 * 根据指标和告警等级返回阀值
		 * 
		 * @param nm
		 * @param alarmLevel
		 * @return
		 */
		private String getThresholdByLevel(AlarmIndicatorsNode nm, int alarmLevel) {
			String threshold = "";
			if (alarmLevel == 1) {
				threshold = nm.getLimenvalue0();
			} else if (alarmLevel == 2) {
				threshold = nm.getLimenvalue1();
			} else if (alarmLevel == 3) {
				threshold = nm.getLimenvalue2();
			}
			return threshold;
		}

		/**
		 * 根据指标和告警等级返回定义的告警次数
		 * 
		 * @param nm
		 * @param alarmLevel
		 * @return
		 */
		private int getTimesByLevel(AlarmIndicatorsNode nm, int eventLevel) {
			int times_int = 0;
			String times_str = "0";
			if (eventLevel == 1) {
				times_str = nm.getTime0();
			} else if (eventLevel == 2) {
				times_str = nm.getTime1();
			} else if (eventLevel == 3) {
				times_str = nm.getTime2();
			}
			try {
				times_int = Integer.parseInt(times_str);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return times_int;
		}

		/**
		 * 从 数据库中 删除上次的告警 如果存在则找出后 在删除并返回 ， 如果不存在则null
		 * <p>
		 * 将内存中相关的告警清除,最终实现从数据库表里删除相关数据
		 * <p>
		 * 
		 * @param name
		 * @return CheckEvent
		 */
//		public synchronized CheckEvent deleteEvent(String name) {
//			// 将内存中相关的告警清除,最终实现从数据库表里删除相关数据
//			CheckEvent checkEvent = null;
//			Hashtable checkEventHash = ShareData.getCheckEventHash();
//			if (checkEventHash != null && checkEventHash.size() > 0) {
//				if (checkEventHash.containsKey(name)) {
//					// 若存在告警
//					checkEvent = new CheckEvent();
//					checkEvent.setName(name);
//					checkEvent.setAlarmlevel((Integer) checkEventHash.get(name));
//					CheckEventDao checkeventdao = new CheckEventDao();
//					try {
//						checkeventdao.delete(name);
//					} catch (Exception e) {
//						e.printStackTrace();
//					} finally {
//						checkeventdao.close();
//					}
//					checkEventHash.remove(name);
//				}
//			}
	//
//			return checkEvent;
//		}

		/**
		 * 从 数据库中 删除上次的告警 如果存在则找出后 在删除并返回 ， 如果不存在则null
		 * <p>
		 * 将内存中相关的告警清除,最终实现从数据库表里删除相关数据
		 * <p>
		 * 
		 * @param name
		 * @return CheckEvent
		 */
//		public CheckEvent deleteEvents(List delList) {
//			// 将内存中相关的告警清除,最终实现从数据库表里删除相关数据
//			CheckEvent checkEvent = null;
//			Hashtable checkEventHash = ShareData.getCheckEventHash();
//			if (checkEventHash != null && checkEventHash.size() > 0) {
//				CheckEventDao checkeventdao = new CheckEventDao();
//				try {
//					for (int i = 0; i < delList.size(); i++) {
//						String name = (String) delList.get(i);
//						if (checkEventHash.containsKey(name)) {
//							// 若存在告警
//							checkEvent = new CheckEvent();
//							checkEvent.setName(name);
//							checkEvent.setAlarmlevel((Integer) checkEventHash
//									.get(name));
	//
//							try {
//								checkeventdao.delete(name);
//							} catch (Exception e) {
//								e.printStackTrace();
//							} finally {
//								// checkeventdao.close();
//							}
//							checkEventHash.remove(name);
//						}
//					}
//				} catch (Exception e) {
	//
//				} finally {
//					checkeventdao.close();
//				}
	//
//			}
	//
//			return checkEvent;
//		}

		/**
		 * 将本次告警信息保存到数据库中
		 * <p>
		 * 将该告警对比信息放到内存系统里,最终需要放到数据库表里
		 * <p>
		 * 
		 * @param name
		 */
		private void saveEvent(CheckEvent checkEvent) {
			// 将该告警对比信息放到内存系统里,最终需要放到数据库表里
			Hashtable checkEventHashtable = ShareData.getCheckEventHash();
			String name = checkEvent.getNodeid()+":"+checkEvent.getType()+":"+checkEvent.getSubtype()+":"+checkEvent.getIndicatorsName();
			if (checkEvent.getSindex() != null && checkEvent.getSindex().trim().length() > 0) {
				name = name + ":" + checkEvent.getSindex();
			}
			checkEventHashtable.put(name, checkEvent.getAlarmlevel());
			CheckEventDao checkeventdao = new CheckEventDao();
			try {
				checkeventdao.save(checkEvent);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				checkeventdao.close();
			}
		}
		/**
		 * 告警指标
		 * @param lr    告警链路
		 * @param content 告警内容
		 * @param target  告警指标名称
		 */
		public synchronized static void saveLinkEventList(LinkRoad lr, String content, String target){
			Calendar Cal=Calendar.getInstance();
			EventListDao dao = null;
			try {
				dao = new EventListDao();
	    		EventList vo = new EventList();
	    		vo.setEventtype("poll");
	    		vo.setEventlocation(lr.getStartIp()+"-"+lr.getEndIp());
	    		vo.setContent(content);
	    		vo.setLevel1(3);
	    		vo.setManagesign(1);
	    		vo.setBusinessid("");
	    		vo.setManagesign(0);
	    		vo.setReportman("系统轮询");
	    		vo.setNodeid(lr.getId());
	    		vo.setOid(0);
	    		vo.setRecordtime(Cal);
	    		vo.setSubtype("link");
	    		vo.setSubentity(target);
				dao.save(vo);
			} catch (Exception e) {
				e.printStackTrace();
			} finally{
				if(dao != null){
					dao.close();
				}
			}
		}
		/**
		 * 根据告警指标名称 获取告警内容
		 * 
		 * @param target
		 *            告警指标
		 * @param oldvalue
		 *            旧值
		 * @param value
		 *            新值
		 * @return
		 */
		public synchronized static String getAlarmInfo(String target,
				Object oldvalue, Object valueObj) {
			String content = null;
			String value = null;
			if ("cpu".equalsIgnoreCase(target.trim())) {
				value = (String) valueObj;
				content = "CPU数量有改变，之前是" + oldvalue + "个，现在是" + value + "个";
			} else if ("diskSize".equalsIgnoreCase(target.trim())) {
				value = (String) valueObj;
				content = "磁盘容量有改变，之前是" + oldvalue + "，现在是" + value;
			} else if ("diskArray".equalsIgnoreCase(target.trim())) {
				List<String> oldDiskArray = (ArrayList<String>) oldvalue;
				StringBuffer tempBuffer = new StringBuffer();
				tempBuffer.append("磁盘盘符有改变，之前是");
				for (int i = 0; i < oldDiskArray.size(); i++) {
					tempBuffer.append(oldDiskArray.get(i));
					if (i != oldDiskArray.size() - 1) {
						tempBuffer.append(",");
					}
				}
				tempBuffer.append("，现在的盘符为");
				List<String> diskArray = (ArrayList<String>) valueObj;
				for (int i = 0; i < diskArray.size(); i++) {
					tempBuffer.append(diskArray.get(i));
					if (i != diskArray.size() - 1) {
						tempBuffer.append(",");
					}
				}
				content = tempBuffer.toString();
			} else if ("PhysicalMemory".equalsIgnoreCase(target.trim())) {
				value = (String) valueObj;
				content = "物理内存有改变，之前是" + oldvalue + "，现在是" + value;
			}
			return content;
		}

		/**
		 * 告警指标
		 * 
		 * @param node
		 *            告警节点
		 * @param content
		 *            告警内容
		 * @param target
		 *            告警指标名称
		 */
		public synchronized static void saveEventList(Host node, String content,
				String target) {
			Calendar Cal = Calendar.getInstance();
			EventListDao dao = null;
			try {
				dao = new EventListDao();
				EventList vo = new EventList();
				vo.setEventtype("poll");
				vo.setEventlocation(node.getLocation());
				vo.setContent(content);
				vo.setLevel1(1);
				vo.setManagesign(1);
				vo.setBusinessid(node.getBid());
				vo.setManagesign(0);
				vo.setReportman("系统轮询");
				vo.setNodeid(node.getId());
				vo.setOid(0);
				vo.setRecordtime(Cal);
				vo.setSubtype("host");
				vo.setSubentity(target);
				dao.save(vo);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (dao != null) {
					dao.close();
				}
			}
		}

		/**
		 * 指标变更判断工具类
		 * 
		 * @param node
		 *            被监控的设备
		 * @param target
		 *            告警指标
		 * @param valueObj
		 *            指标值集合或者字符串
		 */
		public synchronized void hardwareInfo(Host node, String target,
				Object valueObj) {
			if (target == null) {
				return;
			}
			// 盘符个数，盘符值
			Hashtable<String, Object> diskInfo = null;
			// 指标变化历史信息集合
			Hashtable indicatorsInfoHash = ResourceCenter.getInstance()
					.getIndicatorsChangeInfoHash();
			boolean isSystemFirstStart = SystemFlag.getInstance().isFirstStart();// 系统默认不是第一次轮训
			if (!isSystemFirstStart) {
				// 告警内容
				String content = null;
				if ("cpu".equalsIgnoreCase(target)
						|| "PhysicalMemory".equalsIgnoreCase(target)) {
					// 个数值，如 cpu个数\物理内存大小\磁盘总大小
//					String value = (String) valueObj;
//					// ####################测试代码##########################
//					// value = "98";
//					// ####################测试代码##########################
//					String oldValue = null;
//					if (indicatorsInfoHash
//							.containsKey(node.getIpAddress() + target)) {
//						oldValue = (String) indicatorsInfoHash.get(node
//								.getIpAddress()
//								+ target);
//					}
//					if (oldValue != null
//							&& Math.floor(Double.parseDouble(oldValue.replace("M",
//									""))) != Math.floor(Double.parseDouble(value
//									.replace("M", "")))) {
//						content = getAlarmInfo(target, oldValue, value);
//						saveEventList(node, content, target);
//					}
				}
				if ("disk".equalsIgnoreCase(target)) {
					diskInfo = (Hashtable<String, Object>) valueObj;
					// 上一次保存的disk数据
					Hashtable<String, Object> oldDiskInfo = null;
					if (indicatorsInfoHash
							.containsKey(node.getIpAddress() + target)) {
						oldDiskInfo = (Hashtable<String, Object>) indicatorsInfoHash
								.get(node.getIpAddress() + target);
					}
					if (oldDiskInfo != null) {
						// 当前磁盘的总大小
//						String diskSize = null;
//						String oldDiskSize = null;
//						if (diskInfo.containsKey("diskSize")) {
//							diskSize = (String) diskInfo.get("diskSize");
//							// ####################测试代码##########################
//							// diskSize = "98";
//							// ####################测试代码##########################
//							if (oldDiskInfo.containsKey("diskSize")) {
//								oldDiskSize = (String) oldDiskInfo.get("diskSize");
//							}
//							if (diskSize != null
//									&& Math.floor(Double.parseDouble(diskSize
//											.replace("G", ""))) != Math
//											.floor(Double.parseDouble(oldDiskSize
//													.replace("G", "")))) {
//								content = getAlarmInfo("diskSize", oldDiskSize,
//										diskSize);
//								saveEventList(node, content, target);
//							}
//						}
//						// 当前盘符数组
//						List<String> diskNameList = null;
//						List<String> oldDiskNameList = null;// 盘符数组
//						if (diskInfo.containsKey("diskNameList")) {
//							diskNameList = (ArrayList<String>) diskInfo
//									.get("diskNameList");
//							// ####################测试代码##########################
//							// diskNameList = new ArrayList<String>();
//							// diskNameList.add("d:");
//							// diskNameList.add("e:");
//							// ####################测试代码##########################
//							if (oldDiskInfo.containsKey("diskNameList")) {
//								oldDiskNameList = (ArrayList<String>) oldDiskInfo
//										.get("diskNameList");
//							}
//							boolean containFlag = true;// 默认包含该盘符
//							boolean diskNumFlag = true;// 默认磁盘个数是无变化
//							if (oldDiskNameList.containsAll(diskNameList)) {
//								containFlag = true;
//							} else {// 磁盘名称不一致的情形
//								containFlag = false;
//							}
//							// 磁盘个数不一样的情形
//							if (diskNameList.size() != oldDiskNameList.size()) {
//								diskNumFlag = false;
//							}
//							if (!containFlag || !diskNumFlag) {// 不包含该盘符的情况 或者
//								// 个数不一致
//								content = getAlarmInfo("diskArray",
//										oldDiskNameList, diskNameList);
//								saveEventList(node, content, target);
//							}
//						}
					}
				}
			}
			// 当前采集数据存入历史数据 key：ipaddress+指标名称 value：变化的值
//			indicatorsInfoHash.put(node.getIpAddress() + target, valueObj);
		}   
//		 进入告警
		public void updateData(Host node, NodeGatherIndicators nodeGatherIndicators, String value) {
			updateData(node, nodeGatherIndicators, value, null);
		}
//		 进入告警
		public void updateData(Host node, NodeGatherIndicators nodeGatherIndicators, String value, String sIndex) {
			AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(node.getId() + "", nodeGatherIndicators.getType(), nodeGatherIndicators.getSubtype(), nodeGatherIndicators.getName());
			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					AlarmIndicatorsNode nm = (AlarmIndicatorsNode) list.get(i);
					//        			SysLogger.info(node.getId() + ":" + nm.getName() + ":" + nm.getType() + ":" + nm.getSubtype() + ":" + value);
					checkEvent(node, nm, value, sIndex);
				}
			}
		}
}
