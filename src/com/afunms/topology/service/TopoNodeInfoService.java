package com.afunms.topology.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.common.util.CEIString;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.config.model.Diskconfig;
import com.afunms.config.model.Portconfig;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.AllUtilHdx;
import com.afunms.polling.om.CPUcollectdata;
import com.afunms.polling.om.DiscardsPerc;
import com.afunms.polling.om.Diskcollectdata;
import com.afunms.polling.om.ErrorsPerc;
import com.afunms.polling.om.InPkts;
import com.afunms.polling.om.Interfacecollectdata;
import com.afunms.polling.om.Memorycollectdata;
import com.afunms.polling.om.OutPkts;
import com.afunms.polling.om.Pingcollectdata;
import com.afunms.polling.om.Systemcollectdata;
import com.afunms.topology.util.NodeHelper;

public class TopoNodeInfoService {

	private static TopoNodeInfoService instance;

    private TopoNodeInfoService() {

    }


    public static TopoNodeInfoService getInstance() {
    	if (instance==null) {

            syncInit();

        }

        return instance;

    }
    
    private static synchronized void syncInit() {

        if (instance == null) {

            instance = new TopoNodeInfoService();

        }

    }

    public String getNodeInfo(List moidList,Host nodes) {
    	SysLogger.info("nodes.getCategory()==========="+nodes.getCategory());
    	if(nodes.getCategory()<=12){
    		return getPerformanceInfo(moidList,nodes);
    	} else {
    		return getPerformanceInfo(moidList,nodes) + "<br>" + getAlarmInfo(nodes);
    	}
    }   
    public String getOtherNodeInfo(List listalarm,Node nodes) {
//    	SysLogger.info("nodes.getCategory()==========="+nodes.getCategory());
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	StringBuffer msg = new StringBuffer(300);
    	StringBuffer alarmMsg = new StringBuffer(200);
		if(listalarm!=null&&listalarm.size()>0){
			Hashtable checkEventHash = ShareData.getCheckEventHash();	
			for(int i=0;i<listalarm.size();i++){
				try {
					AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode) listalarm.get(i);
					if(alarmIndicatorsNode!=null){
						int flag = 0;
						if(checkEventHash!=null&&checkEventHash.size()>0){
							if(checkEventHash.get(nodes.getId()+":"+alarmIndicatorsNode.getType()+":"+alarmIndicatorsNode.getSubtype()+":"+alarmIndicatorsNode.getName())!=null){
								flag = (Integer)checkEventHash.get(nodes.getId()+":"+alarmIndicatorsNode.getType()+":"+alarmIndicatorsNode.getSubtype()+":"+alarmIndicatorsNode.getName());//elist.size();
							}
						}
						try {
							if("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())){
								SysLogger.info(alarmIndicatorsNode.getName()+" "+alarmIndicatorsNode.getNodeid()+" flag="+flag);
								if(flag>0){
									//有告警产生
									alarmMsg.append(alarmIndicatorsNode.getAlarm_info()+"<br>");
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						if(alarmIndicatorsNode.getType().equalsIgnoreCase("db")&&alarmIndicatorsNode.getName().equalsIgnoreCase("tablespace")){
							String chexkname = nodes.getId()+":"+alarmIndicatorsNode.getType()+":"+alarmIndicatorsNode.getSubtype()+":"+alarmIndicatorsNode.getName()+":";
							for(Iterator it = checkEventHash.keySet().iterator();it.hasNext();){ 
							    String key = (String)it.next(); 
							    if(key.startsWith(chexkname)){
							    	alarmMsg.append(alarmIndicatorsNode.getAlarm_info()+"<br>");
							    	break;
							    }
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		if(alarmMsg.toString().length()>0){
			Calendar date=Calendar.getInstance();
			Date c1 = date.getTime();
			String lastTime = sdf.format(c1);
	        msg.append("<font color='red'>--报警信息:--</font><br>");
			msg.append(alarmMsg.toString());
			msg.append("更新时间:" + lastTime);	
	    }
		return msg.toString();
    }
    public String getBusClor(String value){
    	String st1="<table border=0 height=13 width='100%' bgcolor=#CAE6FA>" +
    	        "<tr><td width='50%'><table width='100%' height=13><tr><td>可用性:</td><td>"+value+"%</td></tr></table>" +
    			"<td width='50%'><table width='100%' height=12><tr><td width='"+value+"%' bgcolor='green'></td>" +
    			"<td width='"+(100-Integer.parseInt(value))+"%' bgcolor=red></td></tr></table></td></tr></table>";
		return st1;
    }
    public String getBusClor1(String value){
    	String st1="<table border=0 height=13 width='100%' bgcolor=#CAE6FA>" +
    	        "<tr><td width='50%'><table width='100%' height=13><tr><td>健康度:</td><td>"+value+"%</td></tr></table>" +
    			"<td width='50%'><table width='100%' height=12><tr><td width='"+value+"%' bgcolor='green'></td>" +
    			"<td width='"+(100-Integer.parseInt(value))+"%' bgcolor=red></td></tr></table></td></tr></table>";
		return st1;
    }
    private String getClor(String value){
    	String st1="<table border=0 height=13 width='100%' bgcolor=#CAE6FA>" +
    	        "<tr><td width='50%'>可用性:</td><td>"+value+"%</td>" +
    			"<td width='"+value+"%' bgcolor='green'></td>" +
    			"<td width='"+(100-Integer.parseInt(value))+"%' bgcolor=red></td></tr></table>";
		return st1;
    }
    public String getPerformanceInfo(List moidList,Host nodes){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		double cpuvalue = 0;
		double memoryvalue = 0;
		String pingvalue = null;
		String inhdx = "0";
		String outhdx = "0";
		String ifInUcastPkts = "0";
		String time = "";
		String lastTime = "";
		String sysuptime = "";
		String sysLocation = nodes.getAlias()+"("+nodes.getIpAddress()+")";
		Vector ipPingData = null;
		Vector memoryVector = null;
		Vector cpuV = null;
		Vector diskVector = null; 
		Vector systemV = null;
		Vector interfaceVector = new Vector();
		String runmodel = PollingEngine.getCollectwebflag(); 
		Hashtable ipAllData = null;
		String ipAddress = nodes.getIpAddress();
		if("0".equals(runmodel)){
			//采集与访问是集成模式
			ipAllData = (Hashtable)ShareData.getSharedata().get(ipAddress);
			ipPingData = (Vector)ShareData.getPingdata().get(ipAddress);
		}else{
			//采集与访问是分离模式  
			ipAllData = (Hashtable)ShareData.getAllNetworkData().get(ipAddress);
			ipPingData = (Vector)ShareData.getAllNetworkPingData().get(ipAddress);
		}
		
		if(ipAllData != null){
			cpuV = (Vector)ipAllData.get("cpu");
			memoryVector = (Vector)ipAllData.get("memory");
			diskVector = (Vector)ipAllData.get("disk");
			Vector allutil = (Vector)ipAllData.get("allutilhdx");
//			得到系统启动时间
			systemV = (Vector)ipAllData.get("system");
			if(allutil != null && allutil.size()==3){
				AllUtilHdx inutilhdx = (AllUtilHdx)allutil.get(0);
				inhdx = inutilhdx.getThevalue();
				AllUtilHdx oututilhdx = (AllUtilHdx)allutil.get(1);
				outhdx = oututilhdx.getThevalue();
			}
			interfaceVector = (Vector)ipAllData.get("interface");
		}
		if(systemV != null && systemV.size()>0){
			for(int i=0;i<systemV.size();i++){
				Systemcollectdata systemdata=(Systemcollectdata)systemV.get(i);
				if(systemdata.getSubentity().equalsIgnoreCase("sysUpTime")){
					sysuptime = systemdata.getThevalue();
				}
			}
		}
		if(cpuV != null && cpuV.size()>0){
			CPUcollectdata cpu = (CPUcollectdata)cpuV.get(0);
			if(cpu != null && cpu.getThevalue() != null){
				cpuvalue = new Double(cpu.getThevalue());
			}
		}
		if(memoryVector != null && memoryVector.size()>0){
			Memorycollectdata memory = (Memorycollectdata)memoryVector.get(0);
			if(memory != null && memory.getThevalue() != null){
				memoryvalue = new Double(memory.getThevalue());
			}
		}
		if(ipPingData != null && ipPingData.size() > 0){
			Pingcollectdata pingdata = (Pingcollectdata)ipPingData.get(0);
			pingvalue = pingdata.getThevalue();
			Calendar tempCal = (Calendar)pingdata.getCollecttime();							
			Date cc = tempCal.getTime();
			time = sdf.format(cc);		
			lastTime = time;
		}
		
		StringBuffer msg = new StringBuffer(300);
		msg.append("<font color='green'>类型:");		
		msg.append(NodeHelper.getNodeCategory(nodes.getCategory()));
		msg.append("</font><br>");
		if(nodes.getCategory()==4)
		{
			msg.append("机器名：");
			msg.append(nodes.getSysName());
			msg.append("<br>别名:");
			msg.append(nodes.getAlias());
			msg.append("<br>");
		}
		else
		{
		   msg.append("设备标签:");
		   msg.append(nodes.getAlias());
		   msg.append("<br>");
		}
		msg.append("IP地址:");
		msg.append(ipAddress);
		msg.append("<br>");
		msg.append("------------------------------");
		msg.append("<br>");
//		msg.append("<b>设备启动时间:</b>");
//		msg.append(sysuptime);
//		msg.append("<br>");
		
		if(!nodes.isManaged()){
			return msg.toString();
		}
		if(pingvalue == null || pingvalue.trim().length()==0){
			pingvalue="0";
		}
		StringBuffer alarmMsg = new StringBuffer(200);
		//SysLogger.info("ipAddress====="+ipAddress+"-------moidList.size()====="+moidList.size());
		if(moidList!=null&&moidList.size()>0){
//			CheckEventDao checkeventdao = null;
			Hashtable checkEventHash = ShareData.getCheckEventHash();	
			try{
//				checkeventdao = new CheckEventDao();
				for(int i=0;i<moidList.size();i++){
					
					AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode) moidList.get(i);
					//SysLogger.info("ipAddress====="+ipAddress+"-------"+alarmIndicatorsNode.getType()+"====="+alarmIndicatorsNode.getName());
					if(alarmIndicatorsNode!=null){
						int flag = 0;
						try {
							if(checkEventHash!=null&&checkEventHash.size()>0){
//								List<CheckEvent> elist = checkeventdao.findCheckEvent(id+"",alarmIndicatorsNode.getType(),alarmIndicatorsNode.getSubtype(),alarmIndicatorsNode.getName());
								if(checkEventHash.get(nodes.getId()+":"+alarmIndicatorsNode.getType()+":"+alarmIndicatorsNode.getSubtype()+":"+alarmIndicatorsNode.getName())!=null){
									flag = (Integer)checkEventHash.get(nodes.getId()+":"+alarmIndicatorsNode.getType()+":"+alarmIndicatorsNode.getSubtype()+":"+alarmIndicatorsNode.getName());//elist.size();
								}
							}
							if(alarmIndicatorsNode.getType().equalsIgnoreCase("net")){
				        		//网络设备
				        		if(alarmIndicatorsNode.getName().equals("cpu")){
				        			//CPU利用率
				        			if("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())){
//				        				int flag = 0;
//				        				flag = checkeventdao.findByName(nodes.getId()+":"+alarmIndicatorsNode.getType()+":"+"cpu");
				            			if(flag>0){
				            				//有告警产生
				            				alarmMsg.append(alarmIndicatorsNode.getAlarm_info()+"<br>");
				            			}  
				            			msg.append(alarmIndicatorsNode.getDescr() + ":" + cpuvalue + " "+alarmIndicatorsNode.getThreshlod_unit() + "<br>");
				        			}	
				        		}else if(alarmIndicatorsNode.getName().equals("memory")){
				        			//CPU利用率
				        			if("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())){
				            			if(flag>0){
				            				//有告警产生
				            				alarmMsg.append(alarmIndicatorsNode.getAlarm_info()+"<br>");
				            			}  
				            			msg.append(alarmIndicatorsNode.getDescr() + ":" + memoryvalue + " "+alarmIndicatorsNode.getThreshlod_unit() + "<br>");
				        			}	
				        		}else if(alarmIndicatorsNode.getName().equals("ping")){
				        			//网络连通性
				        			if(pingvalue == null || pingvalue.trim().length()==0){
				        				//没有对PING数据进行采集,则不需要告警检查
				        				pingvalue="0";
				        				if("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())){
				                			//msg.append(alarmIndicatorsNode.getDescr() + ":" + pingvalue + " "+alarmIndicatorsNode.getThreshlod_unit() + "<br>");
				                			msg.append(getClor(pingvalue));
				            			}
				        			}else{
				        				if("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())){
				                			if(flag>0){
				                				//有告警产生
				                				alarmMsg.append(alarmIndicatorsNode.getAlarm_info()+"<br>");
				                			}
				                			//msg.append(alarmIndicatorsNode.getDescr() + ":" + pingvalue + " "+alarmIndicatorsNode.getThreshlod_unit() + "<br>");
				                			msg.append(getClor(pingvalue));
				            			}
				        			}
				        		}else if(alarmIndicatorsNode.getName().equals("AllInBandwidthUtilHdx")){
				        			//接口信息
				        			//入口流速
				    				if("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())){
				    					if(inhdx != null )msg.append(alarmIndicatorsNode.getDescr() + ":" + inhdx + " "+alarmIndicatorsNode.getThreshlod_unit() + "<br>");
				        				if(flag>0){
				            				//有告警产生
				            				alarmMsg.append(alarmIndicatorsNode.getAlarm_info()+"当前值:"+inhdx+" 阀值:"+alarmIndicatorsNode.getLimenvalue0()+"<br>");
				            			}
				    				}
				        		}else if(alarmIndicatorsNode.getName().equals("AllOutBandwidthUtilHdx")){
				        			//接口信息
				        			//出口流速
				    				if("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())){
				    					if(outhdx != null )msg.append(alarmIndicatorsNode.getDescr() + ":" + outhdx + " "+alarmIndicatorsNode.getThreshlod_unit() + "<br>");
//				    					int flag = 0;
//				    					flag = checkeventdao.findByName(nodes.getId()+":"+alarmIndicatorsNode.getType()+":"+"AllOutBandwidthUtilHdx");
				        				if(flag>0){
				            				//有告警产生
				            				alarmMsg.append(alarmIndicatorsNode.getAlarm_info()+"当前值:"+outhdx+" 阀值:"+alarmIndicatorsNode.getLimenvalue0()+"<br>");
				            			}
				    				}
				        		}else if(alarmIndicatorsNode.getName().equals("interface")){
				        			//接口信息
				        			
				        			if (interfaceVector != null && interfaceVector.size()>0){
				        				//接口DOWN告警
					        			if("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())){
					        				List portconfiglist = new ArrayList();			
					        				Portconfig portconfig = null;			
					        				Hashtable allportconfighash = ShareData.getPortConfigHash();;
					        				if(allportconfighash != null && allportconfighash.size()>0){
					        					if(allportconfighash.containsKey(ipAddress)){
					        						portconfiglist = (List)allportconfighash.get(ipAddress);
					        					}
					        				}				
					        				Hashtable portconfigHash = new Hashtable();
					        				if(portconfiglist != null && portconfiglist.size()>0){
					        					for(int j=0;j<portconfiglist.size();j++){
					        						portconfig = (Portconfig)portconfiglist.get(j);
					        						portconfigHash.put(portconfig.getPortindex()+"", portconfig);
					        					}
					        				}
					        				portconfig = null;
					    					
					    					for(int m=0;m<interfaceVector.size();m++){
					    						Interfacecollectdata interfacedata=(Interfacecollectdata)interfaceVector.get(m);
					    						if(interfacedata != null){
					    							
					    							if(interfacedata.getCategory().equalsIgnoreCase("Interface")&& interfacedata.getEntity().equalsIgnoreCase("ifOperStatus") && interfacedata.getSubentity() != null){
					    								if(portconfigHash.containsKey(interfacedata.getSubentity())){
					    									//存在端口配置,则判断是否DOWN
					    									portconfig = (Portconfig)portconfigHash.get(interfacedata.getSubentity());
					    									if (!"up".equalsIgnoreCase(interfacedata.getThevalue())){
					    										//有告警产生
					    			            				alarmMsg.append("端口 "+portconfig.getName()+" "+portconfig.getLinkuse()+" down"+"<br>");
					    									}
					    								}
					    							}
					    						}
					    					}      			
					        			}
				        				
				        			}else if(alarmIndicatorsNode.getName().equals("ifInMulticastPkts")){
				        				//入口单向
				        				if("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())){
				        					Hashtable sharedata = ShareData.getSharedata();
				            			    Hashtable ipdata = (Hashtable)sharedata.get(ipAddress);
				            			    if(ipdata == null)ipdata = new Hashtable();
				            			    Vector tempv = (Vector)ipdata.get("inpacks");
				            			    if (tempv != null && tempv.size()>0){
				        						for(int k=0;k<tempv.size();k++){
				        							InPkts inpacks=(InPkts)tempv.elementAt(k);
				        							if(inpacks.getEntity().equalsIgnoreCase("ifInMulticastPkts")){
//				        								int flag = 0;
//				        								flag = checkeventdao.findByName(nodes.getId()+":"+alarmIndicatorsNode.getType()+":"+"ifInMulticastPkts");
				        		        				if(flag>0){
				        		            				//有告警产生
				        		            				alarmMsg.append("第"+inpacks.getSubentity()+"端口"+alarmIndicatorsNode.getAlarm_info()+alarmIndicatorsNode.getLimenvalue0()+"<br>");
				        		            			}
				        							}
				        							
				        						}
				            			    }
				        				}
				        				
				        			}else if(alarmIndicatorsNode.getName().equals("ifInBroadcastPkts")){
				        				//入口非单向
				        				if("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())){
				        					Hashtable sharedata = ShareData.getSharedata();
				            			    Hashtable ipdata = (Hashtable)sharedata.get(ipAddress);
				            			    if(ipdata == null)ipdata = new Hashtable();
				            			    Vector tempv = (Vector)ipdata.get("inpacks");
				            			    if (tempv != null && tempv.size()>0){
				        						for(int k=0;k<tempv.size();k++){
				        							InPkts inpacks=(InPkts)tempv.elementAt(k);
				        							if(inpacks.getEntity().equalsIgnoreCase("ifInBroadcastPkts")){
//				        								int flag = 0;
//				        								flag = checkeventdao.findByName(nodes.getId()+":"+alarmIndicatorsNode.getType()+":"+"ifInBroadcastPkts");
				        		        				if(flag>0){
				        		            				//有告警产生
				        		            				alarmMsg.append("第"+inpacks.getSubentity()+"端口"+alarmIndicatorsNode.getAlarm_info()+alarmIndicatorsNode.getLimenvalue0()+"<br>");
				        		            			}
				        							}
				        							
				        						}
				            			    }
				        				}
				        				
				        			}else if(alarmIndicatorsNode.getName().equals("ifOutMulticastPkts")){
				        				//出口单向
				        				if("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())){
				        					Hashtable sharedata = ShareData.getSharedata();
				            			    Hashtable ipdata = (Hashtable)sharedata.get(ipAddress);
				            			    if(ipdata == null)ipdata = new Hashtable();
				            			    Vector tempv = (Vector)ipdata.get("outpacks");
				            			    if (tempv != null && tempv.size()>0){
				        						for(int k=0;k<tempv.size();k++){
				        							OutPkts outpacks=(OutPkts)tempv.elementAt(k);
				        							if(outpacks.getEntity().equalsIgnoreCase("ifOutMulticastPkts")){
//				        								int flag = 0;
//				        								flag = checkeventdao.findByName(nodes.getId()+":"+alarmIndicatorsNode.getType()+":"+"ifOutMulticastPkts");
				        		        				if(flag>0){
				        		            				//有告警产生
				        		            				alarmMsg.append("第"+outpacks.getSubentity()+"端口"+alarmIndicatorsNode.getAlarm_info()+alarmIndicatorsNode.getLimenvalue0()+"<br>");
				        		            			}
				        							}
				        							
				        						}
				            			    }
				        				}
				        				
				        			}else if(alarmIndicatorsNode.getName().equals("ifOutBroadcastPkts")){
				        				//出口非单向
				        				if("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())){
				        					Hashtable sharedata = ShareData.getSharedata();
				            			    Hashtable ipdata = (Hashtable)sharedata.get(ipAddress);
				            			    if(ipdata == null)ipdata = new Hashtable();
				            			    Vector tempv = (Vector)ipdata.get("outpacks");
				            			    if (tempv != null && tempv.size()>0){
				        						for(int k=0;k<tempv.size();k++){
				        							OutPkts outpacks=(OutPkts)tempv.elementAt(k);
				        							if(outpacks.getEntity().equalsIgnoreCase("ifOutBroadcastPkts")){
//				        								int flag = 0;
//				        								flag = checkeventdao.findByName(nodes.getId()+":"+alarmIndicatorsNode.getType()+":"+"ifOutBroadcastPkts");
				        		        				if(flag>0){
				        		            				//有告警产生
				        		            				alarmMsg.append("第"+outpacks.getSubentity()+"端口"+alarmIndicatorsNode.getAlarm_info()+alarmIndicatorsNode.getLimenvalue0()+"<br>");
				        		            			}
				        							}
				        							
				        						}
				            			    }
				        				}
				        			} else if(alarmIndicatorsNode.getName().equals("discardsperc")){
										//端口丢包率
										Hashtable sharedata = ShareData.getSharedata();
									    Hashtable ipdata = (Hashtable)sharedata.get(ipAddress);
									    if(ipdata == null)ipdata = new Hashtable();
									    Vector tempv = (Vector)ipdata.get("discardsperc");
									    if (tempv != null && tempv.size()>0){
											for(int k=0;k<tempv.size();k++){
												DiscardsPerc discardsPerc=(DiscardsPerc)tempv.elementAt(k);
//												int flag = 0;
//		        								flag = checkeventdao.findByName(nodes.getId()+":"+alarmIndicatorsNode.getType()+":"+"discardsperc");
		        		        				if(flag>0){
		        		            				//有告警产生
		        		            				alarmMsg.append("第"+discardsPerc.getSubentity()+"端口"+alarmIndicatorsNode.getAlarm_info()+alarmIndicatorsNode.getLimenvalue0()+"<br>");
		        		            			} else {
													if(discardsPerc.getThevalue() != null)msg.append(alarmIndicatorsNode.getDescr() + ":" + discardsPerc.getThevalue() + " "+alarmIndicatorsNode.getThreshlod_unit() + "<br>");
												}
												
											}
									    }
									} else if(alarmIndicatorsNode.getName().equals("errorsperc")){
										//端口错误率
										Hashtable sharedata = ShareData.getSharedata();
									    Hashtable ipdata = (Hashtable)sharedata.get(ipAddress);
									    if(ipdata == null)ipdata = new Hashtable();
									    Vector tempv = (Vector)ipdata.get("errorsperc");
									    if (tempv != null && tempv.size()>0){
											for(int k=0;k<tempv.size();k++){
												ErrorsPerc errorsPerc=(ErrorsPerc)tempv.elementAt(k);
//												int flag = 0;
//		        								flag = checkeventdao.findByName(nodes.getId()+":"+alarmIndicatorsNode.getType()+":"+"errorsperc");
		        		        				if(flag>0){
		        		            				//有告警产生
		        		            				alarmMsg.append("第"+errorsPerc.getSubentity()+"端口"+alarmIndicatorsNode.getAlarm_info()+alarmIndicatorsNode.getLimenvalue0()+"<br>");
		        		            			} else {
													if(errorsPerc.getThevalue() != null)msg.append(alarmIndicatorsNode.getDescr() + ":" + errorsPerc.getThevalue() + " "+alarmIndicatorsNode.getThreshlod_unit() + "<br>");
												}
											}
									    }
									}
				        		}
				        	}else if(alarmIndicatorsNode.getType().equalsIgnoreCase("firewall")){
				        		//网络设备
				        		if(alarmIndicatorsNode.getName().equals("cpu")){
				        			//CPU利用率
				        			if("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())){
//				        				int flag = 0;
//				        				flag = checkeventdao.findByName(nodes.getId()+":"+alarmIndicatorsNode.getType()+":"+"cpu");
				            			if(flag>0){
				            				//有告警产生
				            				alarmMsg.append(alarmIndicatorsNode.getAlarm_info()+"<br>");
				            			}  
				            			msg.append(alarmIndicatorsNode.getDescr() + ":" + cpuvalue + " "+alarmIndicatorsNode.getThreshlod_unit() + "<br>");
				        			}	
				        		}else if(alarmIndicatorsNode.getName().equals("memory")){
				        			//CPU利用率
				        			if("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())){
//				        				int flag = 0;
//				        				flag = checkeventdao.findByName(nodes.getId()+":"+alarmIndicatorsNode.getType()+":"+"memory");
				            			if(flag>0){
				            				//有告警产生
				            				alarmMsg.append(alarmIndicatorsNode.getAlarm_info()+"<br>");
				            			}  
				            			msg.append(alarmIndicatorsNode.getDescr() + ":" + memoryvalue + " "+alarmIndicatorsNode.getThreshlod_unit() + "<br>");
				        			}	
				        		}else if(alarmIndicatorsNode.getName().equals("ping")){
				        			//网络连通性
				        			if(pingvalue == null || pingvalue.trim().length()==0){
				        				//没有对PING数据进行采集,则不需要告警检查
				        				pingvalue="0";
				        				if("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())){
				                			//msg.append(alarmIndicatorsNode.getDescr() + ":" + pingvalue + " "+alarmIndicatorsNode.getThreshlod_unit() + "<br>");
				                			msg.append(getClor(pingvalue));
				            			}
				        			}else{
				        				if("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())){
//				        					int flag = 0;
//				        					flag = checkeventdao.findByName(nodes.getId()+":"+alarmIndicatorsNode.getType()+":"+"ping");
				                			if(flag>0){
				                				//有告警产生
				                				alarmMsg.append(alarmIndicatorsNode.getAlarm_info()+"<br>");
				                			}
				                			//msg.append(alarmIndicatorsNode.getDescr() + ":" + pingvalue + " "+alarmIndicatorsNode.getThreshlod_unit() + "<br>");
				                			msg.append(getClor(pingvalue));
				            			}
				        			}
				        		}else if(alarmIndicatorsNode.getName().equals("AllInBandwidthUtilHdx")){
				        			//接口信息
				        			//入口流速
				    				if("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())){
				    					if(inhdx != null )msg.append(alarmIndicatorsNode.getDescr() + ":" + inhdx + " "+alarmIndicatorsNode.getThreshlod_unit() + "<br>");
//				    					int flag = 0;
//				    					flag = checkeventdao.findByName(nodes.getId()+":"+alarmIndicatorsNode.getType()+":"+"AllInBandwidthUtilHdx");
				        				if(flag>0){
				            				//有告警产生
				            				alarmMsg.append(alarmIndicatorsNode.getAlarm_info()+"当前值:"+inhdx+" 阀值:"+alarmIndicatorsNode.getLimenvalue0()+"<br>");
				            			}
				    				}
				        		}else if(alarmIndicatorsNode.getName().equals("AllOutBandwidthUtilHdx")){
				        			//接口信息
				        			//出口流速
				    				if("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())){
				    					if(inhdx != null )msg.append(alarmIndicatorsNode.getDescr() + ":" + inhdx + " "+alarmIndicatorsNode.getThreshlod_unit() + "<br>");
//				    					int flag = 0;
//				    					flag = checkeventdao.findByName(nodes.getId()+":"+alarmIndicatorsNode.getType()+":"+"AllOutBandwidthUtilHdx");
				        				if(flag>0){
				            				//有告警产生
				            				alarmMsg.append(alarmIndicatorsNode.getAlarm_info()+"当前值:"+inhdx+" 阀值:"+alarmIndicatorsNode.getLimenvalue0()+"<br>");
				            			}
				    				}
				        		}else if(alarmIndicatorsNode.getName().equals("interface")){
				        			//接口信息
				        			
				        			if (interfaceVector != null && interfaceVector.size()>0){
				        				//接口DOWN告警
					        			if("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())){
//					    					List portconfiglist = new ArrayList();
//					    					PortconfigDao configdao = new PortconfigDao(); 			
//					    					Portconfig portconfig = null;
//					    					Hashtable portconfigHash = new Hashtable();
//					    					try {
//					    						portconfiglist = configdao.getBySms(ipAddress);
//					    					} catch (RuntimeException e) {
//					    						e.printStackTrace();
//					    					} finally {
//					    						configdao.close();
//					    					}
//					    					if(portconfiglist != null && portconfiglist.size()>0){
//					    						for(int k=0;k<portconfiglist.size();k++){
//					    							portconfig = (Portconfig)portconfiglist.get(k);
//					    							portconfigHash.put(portconfig.getPortindex()+"", portconfig);
//					    							//SysLogger.info("add ===="+portconfig.getPortindex()+"");
//					    						}
//					    					}
//					    					portconfig = null;
					        				List portconfiglist = new ArrayList();			
					        				Portconfig portconfig = null;			
					        				Hashtable allportconfighash = ShareData.getPortConfigHash();;
					        				if(allportconfighash != null && allportconfighash.size()>0){
					        					if(allportconfighash.containsKey(nodes.getIpAddress())){
					        						portconfiglist = (List)allportconfighash.get(nodes.getIpAddress());
					        					}
					        				}				
					        				Hashtable portconfigHash = new Hashtable();
					        				if(portconfiglist != null && portconfiglist.size()>0){
					        					for(int k=0;k<portconfiglist.size();k++){
					        						portconfig = (Portconfig)portconfiglist.get(k);
					        						portconfigHash.put(portconfig.getPortindex()+"", portconfig);
					        					}
					        				}
					        				portconfig = null;
					    					
					    					for(int m=0;m<interfaceVector.size();m++){
					    						Interfacecollectdata interfacedata=(Interfacecollectdata)interfaceVector.get(m);
					    						if(interfacedata != null){
					    							
					    							if(interfacedata.getCategory().equalsIgnoreCase("Interface")&& interfacedata.getEntity().equalsIgnoreCase("ifOperStatus") && interfacedata.getSubentity() != null){
					    								if(portconfigHash.containsKey(interfacedata.getSubentity())){
					    									//存在端口配置,则判断是否DOWN
					    									portconfig = (Portconfig)portconfigHash.get(interfacedata.getSubentity());
					    									if (!"up".equalsIgnoreCase(interfacedata.getThevalue())){
					    										//有告警产生
					    			            				alarmMsg.append("端口 "+portconfig.getName()+" "+portconfig.getLinkuse()+" down"+"<br>");
					    									}
					    								}
					    							}
					    						}
					    					}      			
					        			}
				        				
				        			}else if(alarmIndicatorsNode.getName().equals("ifInMulticastPkts")){
				        				//入口单向
				        				if("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())){
				        					Hashtable sharedata = ShareData.getSharedata();
				            			    Hashtable ipdata = (Hashtable)sharedata.get(ipAddress);
				            			    if(ipdata == null)ipdata = new Hashtable();
				            			    Vector tempv = (Vector)ipdata.get("inpacks");
				            			    if (tempv != null && tempv.size()>0){
				        						for(int k=0;k<tempv.size();k++){
				        							InPkts inpacks=(InPkts)tempv.elementAt(k);
				        							if(inpacks.getEntity().equalsIgnoreCase("ifInMulticastPkts")){
//				        								int flag = 0;
//				        								flag = checkeventdao.findByName(nodes.getId()+":"+alarmIndicatorsNode.getType()+":"+"ifInMulticastPkts");
				        		        				if(flag>0){
				        		            				//有告警产生
				        		            				alarmMsg.append("第"+inpacks.getSubentity()+"端口"+alarmIndicatorsNode.getAlarm_info()+alarmIndicatorsNode.getLimenvalue0()+"<br>");
				        		            			}
				        							}
				        							
				        						}
				            			    }
				        				}
				        				
				        			}else if(alarmIndicatorsNode.getName().equals("ifInBroadcastPkts")){
				        				//入口非单向
				        				if("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())){
				        					Hashtable sharedata = ShareData.getSharedata();
				            			    Hashtable ipdata = (Hashtable)sharedata.get(ipAddress);
				            			    if(ipdata == null)ipdata = new Hashtable();
				            			    Vector tempv = (Vector)ipdata.get("inpacks");
				            			    if (tempv != null && tempv.size()>0){
				        						for(int k=0;k<tempv.size();k++){
				        							InPkts inpacks=(InPkts)tempv.elementAt(k);
				        							if(inpacks.getEntity().equalsIgnoreCase("ifInBroadcastPkts")){
//				        								int flag = 0;
//				        								flag = checkeventdao.findByName(nodes.getId()+":"+alarmIndicatorsNode.getType()+":"+"ifInBroadcastPkts");
				        		        				if(flag>0){
				        		            				//有告警产生
				        		            				alarmMsg.append("第"+inpacks.getSubentity()+"端口"+alarmIndicatorsNode.getAlarm_info()+alarmIndicatorsNode.getLimenvalue0()+"<br>");
				        		            			}
				        							}
				        							
				        						}
				            			    }
				        				}
				        				
				        			}else if(alarmIndicatorsNode.getName().equals("ifOutMulticastPkts")){
				        				//出口单向
				        				if("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())){
				        					Hashtable sharedata = ShareData.getSharedata();
				            			    Hashtable ipdata = (Hashtable)sharedata.get(ipAddress);
				            			    if(ipdata == null)ipdata = new Hashtable();
				            			    Vector tempv = (Vector)ipdata.get("outpacks");
				            			    if (tempv != null && tempv.size()>0){
				        						for(int k=0;k<tempv.size();k++){
				        							OutPkts outpacks=(OutPkts)tempv.elementAt(k);
				        							if(outpacks.getEntity().equalsIgnoreCase("ifOutMulticastPkts")){
//				        								int flag = 0;
//				        								flag = checkeventdao.findByName(nodes.getId()+":"+alarmIndicatorsNode.getType()+":"+"ifOutMulticastPkts");
				        		        				if(flag>0){
				        		            				//有告警产生
				        		            				alarmMsg.append("第"+outpacks.getSubentity()+"端口"+alarmIndicatorsNode.getAlarm_info()+alarmIndicatorsNode.getLimenvalue0()+"<br>");
				        		            			}
				        							}
				        							
				        						}
				            			    }
				        				}
				        				
				        			}else if(alarmIndicatorsNode.getName().equals("ifOutBroadcastPkts")){
				        				//出口非单向
				        				if("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())){
				        					Hashtable sharedata = ShareData.getSharedata();
				            			    Hashtable ipdata = (Hashtable)sharedata.get(ipAddress);
				            			    if(ipdata == null)ipdata = new Hashtable();
				            			    Vector tempv = (Vector)ipdata.get("outpacks");
				            			    if (tempv != null && tempv.size()>0){
				        						for(int k=0;k<tempv.size();k++){
				        							OutPkts outpacks=(OutPkts)tempv.elementAt(k);
				        							if(outpacks.getEntity().equalsIgnoreCase("ifOutBroadcastPkts")){
//				        								int flag = 0;
//				        								flag = checkeventdao.findByName(nodes.getId()+":"+alarmIndicatorsNode.getType()+":"+"ifOutBroadcastPkts");
				        		        				if(flag>0){
				        		            				//有告警产生
				        		            				alarmMsg.append("第"+outpacks.getSubentity()+"端口"+alarmIndicatorsNode.getAlarm_info()+alarmIndicatorsNode.getLimenvalue0()+"<br>");
				        		            			}
				        							}
				        							
				        						}
				            			    }
				        				}
				        			} else if(alarmIndicatorsNode.getName().equals("discardsperc")){
										//端口丢包率
										Hashtable sharedata = ShareData.getSharedata();
									    Hashtable ipdata = (Hashtable)sharedata.get(ipAddress);
									    if(ipdata == null)ipdata = new Hashtable();
									    Vector tempv = (Vector)ipdata.get("discardsperc");
									    if (tempv != null && tempv.size()>0){
											for(int k=0;k<tempv.size();k++){
												DiscardsPerc discardsPerc=(DiscardsPerc)tempv.elementAt(k);
//												int flag = 0;
//		        								flag = checkeventdao.findByName(nodes.getId()+":"+alarmIndicatorsNode.getType()+":"+"discardsperc");
		        		        				if(flag>0){
		        		            				//有告警产生
		        		            				alarmMsg.append("第"+discardsPerc.getSubentity()+"端口"+alarmIndicatorsNode.getAlarm_info()+alarmIndicatorsNode.getLimenvalue0()+"<br>");
		        		            			} else {
													if(discardsPerc.getThevalue() != null)msg.append(alarmIndicatorsNode.getDescr() + ":" + discardsPerc.getThevalue() + " "+alarmIndicatorsNode.getThreshlod_unit() + "<br>");
												}
												
											}
									    }
									} else if(alarmIndicatorsNode.getName().equals("errorsperc")){
										//端口错误率
										Hashtable sharedata = ShareData.getSharedata();
									    Hashtable ipdata = (Hashtable)sharedata.get(ipAddress);
									    if(ipdata == null)ipdata = new Hashtable();
									    Vector tempv = (Vector)ipdata.get("errorsperc");
									    if (tempv != null && tempv.size()>0){
											for(int k=0;k<tempv.size();k++){
												ErrorsPerc errorsPerc=(ErrorsPerc)tempv.elementAt(k);
//												int flag = 0;
//		        								flag = checkeventdao.findByName(nodes.getId()+":"+alarmIndicatorsNode.getType()+":"+"errorsperc");
		        		        				if(flag>0){
		        		            				//有告警产生
		        		            				alarmMsg.append("第"+errorsPerc.getSubentity()+"端口"+alarmIndicatorsNode.getAlarm_info()+alarmIndicatorsNode.getLimenvalue0()+"<br>");
		        		            			} else {
													if(errorsPerc.getThevalue() != null)msg.append(alarmIndicatorsNode.getDescr() + ":" + errorsPerc.getThevalue() + " "+alarmIndicatorsNode.getThreshlod_unit() + "<br>");
												}
											}
									    }
									}
				        		}
				        	}else if(alarmIndicatorsNode.getType().equalsIgnoreCase("host")){

				        		//网络设备
				        		if(alarmIndicatorsNode.getName().equals("cpu")){
				        			//CPU利用率
				        			if("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())){
//				        				int flag = 0;
//				        				flag = checkeventdao.findByName(nodes.getId()+":"+alarmIndicatorsNode.getType()+":"+"cpu");
				            			if(flag>0){
				            				//有告警产生
				            				alarmMsg.append(alarmIndicatorsNode.getAlarm_info()+"<br>");
				            			}  
				            			msg.append(alarmIndicatorsNode.getDescr() + ":" + cpuvalue + " "+alarmIndicatorsNode.getThreshlod_unit() + "<br>");
				        			}	
				        		}else if(alarmIndicatorsNode.getName().equals("memory")){
				        			//CPU利用率
				        			if("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())){
//				        				int flag = 0;
//				        				flag = checkeventdao.findByName(nodes.getId()+":"+alarmIndicatorsNode.getType()+":"+"memory");
				            			if(flag>0){
				            				//有告警产生
				            				alarmMsg.append(alarmIndicatorsNode.getAlarm_info()+"<br>");
				            			}  
				            			msg.append(alarmIndicatorsNode.getDescr() + ":" + memoryvalue + " "+alarmIndicatorsNode.getThreshlod_unit() + "<br>");
				        			}	
				        		}else if(alarmIndicatorsNode.getName().equals("ping")){
				        			//网络连通性
				        			if(pingvalue == null || pingvalue.trim().length()==0){
				        				//没有对PING数据进行采集,则不需要告警检查
				        				pingvalue="0";
				        				if("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())){
				                			//msg.append(alarmIndicatorsNode.getDescr() + ":" + pingvalue + " "+alarmIndicatorsNode.getThreshlod_unit() + "<br>");
				                			msg.append(getClor(pingvalue));
				            			}
				        			}else{
				        				if("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())){
//				        					int flag = 0;
//				        					flag = checkeventdao.findByName(nodes.getId()+":"+alarmIndicatorsNode.getType()+":"+"ping");
				                			if(flag>0){
				                				//有告警产生
				                				alarmMsg.append(alarmIndicatorsNode.getAlarm_info()+"<br>");
				                			}
				                			//msg.append(alarmIndicatorsNode.getDescr() + ":" + pingvalue + " "+alarmIndicatorsNode.getThreshlod_unit() + "<br>");
				                			msg.append(getClor(pingvalue));
				            			}
				        			}
				        		}else if(alarmIndicatorsNode.getName().equals("AllInBandwidthUtilHdx")){
				        			//接口信息
				        			//入口流速
				    				if("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())){
				    					if(inhdx != null )msg.append(alarmIndicatorsNode.getDescr() + ":" + inhdx + " "+alarmIndicatorsNode.getThreshlod_unit() + "<br>");
//				    					int flag = 0;
//				    					flag = checkeventdao.findByName(nodes.getId()+":"+alarmIndicatorsNode.getType()+":"+"AllInBandwidthUtilHdx");
				        				if(flag>0){
				            				//有告警产生
				            				alarmMsg.append(alarmIndicatorsNode.getAlarm_info()+"当前值:"+inhdx+" 阀值:"+alarmIndicatorsNode.getLimenvalue0()+"<br>");
				            			}
				    				}
				        		}else if(alarmIndicatorsNode.getName().equals("AllOutBandwidthUtilHdx")){
				        			//接口信息
				        			//出口流速
				    				if("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())){
				    					if(inhdx != null )msg.append(alarmIndicatorsNode.getDescr() + ":" + inhdx + " "+alarmIndicatorsNode.getThreshlod_unit() + "<br>");
//				    					int flag = 0;
//				    					flag = checkeventdao.findByName(nodes.getId()+":"+alarmIndicatorsNode.getType()+":"+"AllOutBandwidthUtilHdx");
				        				if(flag>0){
				            				//有告警产生
				            				alarmMsg.append(alarmIndicatorsNode.getAlarm_info()+"当前值:"+inhdx+" 阀值:"+alarmIndicatorsNode.getLimenvalue0()+"<br>");
				            			}
				    				}
				        		}else if(alarmIndicatorsNode.getName().equals("interface")){
				        			//接口信息
				        			
				        			if (interfaceVector != null && interfaceVector.size()>0){
				        				//接口DOWN告警
					        			if("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())){
//					    					List portconfiglist = new ArrayList();
//					    					PortconfigDao configdao = new PortconfigDao(); 			
//					    					Portconfig portconfig = null;
//					    					Hashtable portconfigHash = new Hashtable();
//					    					try {
//					    						portconfiglist = configdao.getBySms(ipAddress);
//					    					} catch (RuntimeException e) {
//					    						e.printStackTrace();
//					    					} finally {
//					    						configdao.close();
//					    					}
//					    					if(portconfiglist != null && portconfiglist.size()>0){
//					    						for(int k=0;k<portconfiglist.size();k++){
//					    							portconfig = (Portconfig)portconfiglist.get(k);
//					    							portconfigHash.put(portconfig.getPortindex()+"", portconfig);
//					    							//SysLogger.info("add ===="+portconfig.getPortindex()+"");
//					    						}
//					    					}
//					    					portconfig = null;
					        				List portconfiglist = new ArrayList();			
					        				Portconfig portconfig = null;			
					        				Hashtable allportconfighash = ShareData.getPortConfigHash();;
					        				if(allportconfighash != null && allportconfighash.size()>0){
					        					if(allportconfighash.containsKey(nodes.getIpAddress())){
					        						portconfiglist = (List)allportconfighash.get(nodes.getIpAddress());
					        					}
					        				}				
					        				Hashtable portconfigHash = new Hashtable();
					        				if(portconfiglist != null && portconfiglist.size()>0){
					        					for(int k=0;k<portconfiglist.size();k++){
					        						portconfig = (Portconfig)portconfiglist.get(k);
					        						portconfigHash.put(portconfig.getPortindex()+"", portconfig);
					        					}
					        				}
					        				portconfig = null;
					    					
					    					for(int m=0;m<interfaceVector.size();m++){
					    						Interfacecollectdata interfacedata=(Interfacecollectdata)interfaceVector.get(m);
					    						if(interfacedata != null){
					    							
					    							if(interfacedata.getCategory().equalsIgnoreCase("Interface")&& interfacedata.getEntity().equalsIgnoreCase("ifOperStatus") && interfacedata.getSubentity() != null){
					    								if(portconfigHash.containsKey(interfacedata.getSubentity())){
					    									//存在端口配置,则判断是否DOWN
					    									portconfig = (Portconfig)portconfigHash.get(interfacedata.getSubentity());
					    									if (!"up".equalsIgnoreCase(interfacedata.getThevalue())){
					    										//有告警产生
					    			            				alarmMsg.append("端口 "+portconfig.getName()+" "+portconfig.getLinkuse()+" down"+"<br>");
					    									}
					    								}
					    							}
					    						}
					    					}      			
					        			}
				        				
				        			}else if(alarmIndicatorsNode.getName().equals("ifInMulticastPkts")){
				        				//入口单向
				        				if("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())){
				        					Hashtable sharedata = ShareData.getSharedata();
				            			    Hashtable ipdata = (Hashtable)sharedata.get(ipAddress);
				            			    if(ipdata == null)ipdata = new Hashtable();
				            			    Vector tempv = (Vector)ipdata.get("inpacks");
				            			    if (tempv != null && tempv.size()>0){
				        						for(int k=0;k<tempv.size();k++){
				        							InPkts inpacks=(InPkts)tempv.elementAt(k);
				        							if(inpacks.getEntity().equalsIgnoreCase("ifInMulticastPkts")){
//				        								int flag = 0;
//				        								flag = checkeventdao.findByName(nodes.getId()+":"+alarmIndicatorsNode.getType()+":"+"ifInMulticastPkts");
				        		        				if(flag>0){
				        		            				//有告警产生
				        		            				alarmMsg.append("第"+inpacks.getSubentity()+"端口"+alarmIndicatorsNode.getAlarm_info()+alarmIndicatorsNode.getLimenvalue0()+"<br>");
				        		            			}
				        							}
				        							
				        						}
				            			    }
				        				}
				        				
				        			}else if(alarmIndicatorsNode.getName().equals("ifInBroadcastPkts")){
				        				//入口非单向
				        				if("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())){
				        					Hashtable sharedata = ShareData.getSharedata();
				            			    Hashtable ipdata = (Hashtable)sharedata.get(ipAddress);
				            			    if(ipdata == null)ipdata = new Hashtable();
				            			    Vector tempv = (Vector)ipdata.get("inpacks");
				            			    if (tempv != null && tempv.size()>0){
				        						for(int k=0;k<tempv.size();k++){
				        							InPkts inpacks=(InPkts)tempv.elementAt(k);
				        							if(inpacks.getEntity().equalsIgnoreCase("ifInBroadcastPkts")){
//				        								int flag = 0;
//				        								flag = checkeventdao.findByName(nodes.getId()+":"+alarmIndicatorsNode.getType()+":"+"ifInBroadcastPkts");
				        		        				if(flag>0){
				        		            				//有告警产生
				        		            				alarmMsg.append("第"+inpacks.getSubentity()+"端口"+alarmIndicatorsNode.getAlarm_info()+alarmIndicatorsNode.getLimenvalue0()+"<br>");
				        		            			}
				        							}
				        							
				        						}
				            			    }
				        				}
				        				
				        			}else if(alarmIndicatorsNode.getName().equals("ifOutMulticastPkts")){
				        				//出口单向
				        				if("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())){
				        					Hashtable sharedata = ShareData.getSharedata();
				            			    Hashtable ipdata = (Hashtable)sharedata.get(ipAddress);
				            			    if(ipdata == null)ipdata = new Hashtable();
				            			    Vector tempv = (Vector)ipdata.get("outpacks");
				            			    if (tempv != null && tempv.size()>0){
				        						for(int k=0;k<tempv.size();k++){
				        							OutPkts outpacks=(OutPkts)tempv.elementAt(k);
				        							if(outpacks.getEntity().equalsIgnoreCase("ifOutMulticastPkts")){
//				        								int flag = 0;
//				        								flag = checkeventdao.findByName(nodes.getId()+":"+alarmIndicatorsNode.getType()+":"+"ifOutMulticastPkts");
				        		        				if(flag>0){
				        		            				//有告警产生
				        		            				alarmMsg.append("第"+outpacks.getSubentity()+"端口"+alarmIndicatorsNode.getAlarm_info()+alarmIndicatorsNode.getLimenvalue0()+"<br>");
				        		            			}
				        							}
				        							
				        						}
				            			    }
				        				}
				        				
				        			}else if(alarmIndicatorsNode.getName().equals("ifOutBroadcastPkts")){
				        				//出口非单向
				        				if("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())){
				        					Hashtable sharedata = ShareData.getSharedata();
				            			    Hashtable ipdata = (Hashtable)sharedata.get(ipAddress);
				            			    if(ipdata == null)ipdata = new Hashtable();
				            			    Vector tempv = (Vector)ipdata.get("outpacks");
				            			    if (tempv != null && tempv.size()>0){
				        						for(int k=0;k<tempv.size();k++){
				        							OutPkts outpacks=(OutPkts)tempv.elementAt(k);
				        							if(outpacks.getEntity().equalsIgnoreCase("ifOutBroadcastPkts")){
//				        								int flag = 0;
//				        								flag = checkeventdao.findByName(nodes.getId()+":"+alarmIndicatorsNode.getType()+":"+"ifOutBroadcastPkts");
				        		        				if(flag>0){
				        		            				//有告警产生
				        		            				alarmMsg.append("第"+outpacks.getSubentity()+"端口"+alarmIndicatorsNode.getAlarm_info()+alarmIndicatorsNode.getLimenvalue0()+"<br>");
				        		            			}
				        							}
				        							
				        						}
				            			    }
				        				}
				        			} else if(alarmIndicatorsNode.getName().equals("discardsperc")){
										//端口丢包率
										Hashtable sharedata = ShareData.getSharedata();
									    Hashtable ipdata = (Hashtable)sharedata.get(ipAddress);
									    if(ipdata == null)ipdata = new Hashtable();
									    Vector tempv = (Vector)ipdata.get("discardsperc");
									    if (tempv != null && tempv.size()>0){
											for(int k=0;k<tempv.size();k++){
												DiscardsPerc discardsPerc=(DiscardsPerc)tempv.elementAt(k);
//												int flag = 0;
//		        								flag = checkeventdao.findByName(nodes.getId()+":"+alarmIndicatorsNode.getType()+":"+"discardsperc");
		        		        				if(flag>0){
		        		            				//有告警产生
		        		            				alarmMsg.append("第"+discardsPerc.getSubentity()+"端口"+alarmIndicatorsNode.getAlarm_info()+alarmIndicatorsNode.getLimenvalue0()+"<br>");
		        		            			} else {
													if(discardsPerc.getThevalue() != null)msg.append(alarmIndicatorsNode.getDescr() + ":" + discardsPerc.getThevalue() + " "+alarmIndicatorsNode.getThreshlod_unit() + "<br>");
												}
												
											}
									    }
									} else if(alarmIndicatorsNode.getName().equals("errorsperc")){
										//端口错误率
										Hashtable sharedata = ShareData.getSharedata();
									    Hashtable ipdata = (Hashtable)sharedata.get(ipAddress);
									    if(ipdata == null)ipdata = new Hashtable();
									    Vector tempv = (Vector)ipdata.get("errorsperc");
									    if (tempv != null && tempv.size()>0){
											for(int k=0;k<tempv.size();k++){
												ErrorsPerc errorsPerc=(ErrorsPerc)tempv.elementAt(k);
//												int flag = 0;
//		        								flag = checkeventdao.findByName(nodes.getId()+":"+alarmIndicatorsNode.getType()+":"+"errorsperc");
		        		        				if(flag>0){
		        		            				//有告警产生
		        		            				alarmMsg.append("第"+errorsPerc.getSubentity()+"端口"+alarmIndicatorsNode.getAlarm_info()+alarmIndicatorsNode.getLimenvalue0()+"<br>");
		        		            			} else {
													if(errorsPerc.getThevalue() != null)msg.append(alarmIndicatorsNode.getDescr() + ":" + errorsPerc.getThevalue() + " "+alarmIndicatorsNode.getThreshlod_unit() + "<br>");
												}
											}
									    }
									}
				        		}
				        	
				        	}else if(alarmIndicatorsNode.getType().equalsIgnoreCase("virtual")){
				        		//虚拟化设备
				        		SysLogger.info("虚拟化设备==="+alarmIndicatorsNode.getType()+"======"+alarmIndicatorsNode.getName());
				        		if(alarmIndicatorsNode.getName().equals("cpu")){
//				        			try {
//										//CPU利用率
//										if("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())){
//											if(flag>0){
//												//有告警产生
//												alarmMsg.append(alarmIndicatorsNode.getAlarm_info()+"<br>");
//											}       			
//											msg.append(alarmIndicatorsNode.getDescr() + ":" + cpuvalue + " "+alarmIndicatorsNode.getThreshlod_unit() + "<br>");
//										}
//									} catch (Exception e) {
//										e.printStackTrace();
//									}
				        		}else if(alarmIndicatorsNode.getName().equals("ping")){
				        			try {
										//连通性
										SysLogger.info("### 开始更新"+alarmIndicatorsNode.getName()+" PING数据的XML信息####");
										if("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())){
											if(pingvalue == null || pingvalue.trim().length()==0){
												pingvalue="0";
											}else{
//												int flag = 0;
//												flag = checkeventdao.findByName(nodes.getId()+":"+alarmIndicatorsNode.getType()+":"+"ping");
												SysLogger.info(alarmIndicatorsNode.getName()+" "+alarmIndicatorsNode.getNodeid()+" flag="+flag);
												if(flag>0){
													//有告警产生
													alarmMsg.append(alarmIndicatorsNode.getAlarm_info()+"<br>");
												}
											}
											msg.append(alarmIndicatorsNode.getDescr() + ":" + pingvalue + " "+alarmIndicatorsNode.getThreshlod_unit() + "<br>");
										}
									} catch (Exception e) {
										e.printStackTrace();
									}

				        		}else if(alarmIndicatorsNode.getName().equals("diskperc")){
				        			try {} catch (Exception e) {
										e.printStackTrace();
									}
				        		}else if(alarmIndicatorsNode.getName().equalsIgnoreCase("physicalmemory")){
				        			try {} catch (Exception e) {
										e.printStackTrace();
									}
				        		}else if(alarmIndicatorsNode.getName().equalsIgnoreCase("virtualmemory")){
				        			try {} catch (Exception e) {
										e.printStackTrace();
									}
				        		} else if(alarmIndicatorsNode.getName().equalsIgnoreCase("swapmemory")){
									try {} catch (Exception e) {
										e.printStackTrace();
									}
								} else if(alarmIndicatorsNode.getName().equals("AllInBandwidthUtilHdx")){//接口信息
									//入口流速
									if(inhdx != null)msg.append(alarmIndicatorsNode.getDescr() + ":" + inhdx + " "+alarmIndicatorsNode.getThreshlod_unit() + "<br>");
								} else if(alarmIndicatorsNode.getName().equals("AllOutBandwidthUtilHdx")){
									//出口流速
									if(outhdx != null)msg.append(alarmIndicatorsNode.getDescr() + ":" + outhdx + " "+alarmIndicatorsNode.getThreshlod_unit() + "<br>");
								} else if(alarmIndicatorsNode.getName().equals("process")){
								} else if(alarmIndicatorsNode.getName().equals("interface")){
								} else if(alarmIndicatorsNode.getName().equals("responsetime")){
								}
				        	}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
						}
					}
				
				
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
//				checkeventdao.close();
			}
			
		}
        if(alarmMsg.toString().length()>0){
        	msg.append("<font color='red'>----报警信息--------------</font><br>");
		    msg.append(alarmMsg.toString());
        }
        msg.append("------------------------------");
        msg.append("<br>");
        //msg.append("------------------------------");
		msg.append("更新时间:" + lastTime);	
        return msg.toString();		
	};

    public String getAlarmInfo(Host nodes) {
    	List alarmList = nodes.getAlarmMessage();
		String alarmmessage = "";
		
		if (alarmList != null && alarmList.size() > 0) {
			for (int k = 0; k < alarmList.size(); k++) {
				alarmmessage = alarmmessage + alarmList.get(k) + "<br>";
			}
		}
        return alarmmessage;
    }

}
