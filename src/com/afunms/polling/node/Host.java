/**
 * <p>Description:host,including server and exchange device</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-27
 */

package com.afunms.polling.node;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import montnets.SmsDao;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.model.HostServiceGroup;
import com.afunms.application.model.HostServiceGroupConfiguration;
import com.afunms.application.model.JobForAS400Group;
import com.afunms.application.model.JobForAS400GroupDetail;
import com.afunms.application.model.ProcessGroup;
import com.afunms.application.model.ProcessGroupConfiguration;
import com.afunms.application.util.HostServiceGroupConfigurationUtil;
import com.afunms.application.util.JobForAS400GroupDetailUtil;
import com.afunms.application.util.ProcessGroupConfigurationUtil;
import com.afunms.common.util.CEIString;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.config.dao.PortconfigDao;
import com.afunms.config.model.Diskconfig;
import com.afunms.config.model.Portconfig;
import com.afunms.event.dao.EventListDao;
import com.afunms.event.dao.SmscontentDao;
import com.afunms.event.model.EventList;
import com.afunms.event.model.Smscontent;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
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
import com.afunms.polling.om.Processcollectdata;
import com.afunms.polling.om.Servicecollectdata;
import com.afunms.topology.model.JobForAS400;
import com.afunms.topology.model.NodeMonitor;
import com.afunms.topology.util.NodeHelper;

public class Host extends Node
{
	private int localNet;     //所在子网
    private String netMask;   //子网掩码
    private String community; //共同体
    private String writecommunity; //共同体
    private int snmpversion;//SNMP版本
    private String sysOid;    //系统oid 
    private String sysName;    //系统名    
    private Hashtable interfaceHash;       
    private String user;
    private String password;
    private String prompt="";
    public String showmessage;//yangjun 修改
    private String mac;
    private int layer; //hukelei 修改，增加当前设备的层，便于拓扑图生成
    
	//SNMP V3
	private int securitylevel; //安全级别  1:noAuthNopriv 2:authNoPriv 3:authPriv
	private String securityName; //用户名
	private int v3_ap;           //认证协议  1:MD5 2:SHA
	private String authpassphrase; //通行码
	private int v3_privacy;			//加密协议 1:DES 2:AES128 3:AES196 4:AES256
	private String privacyPassphrase; //加密协议码
	
       
	public Host()
    {
    }
  
	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}
	
	public String getPrompt() {
		return prompt;
	}

	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getCommunity() {
		return community;
	}

	public void setCommunity(String community) {
		this.community = community;
	}
	
	public String getWritecommunity() {
		return writecommunity;
	}

	public void setWritecommunity(String writecommunity) {
		this.writecommunity = writecommunity;
	}
	
	public int getSnmpversion() {
		return snmpversion;
	}

	public void setSnmpversion(int snmpversion) {
		this.snmpversion = snmpversion;
	}

	public Hashtable getInterfaceHash() {
		return interfaceHash;
	}

	public void setInterfaceHash(Hashtable interfaceHash) {
		this.interfaceHash = interfaceHash;
	}

	public int getLocalNet() {
		return localNet;
	}

	public void setLocalNet(int localNet) {
		this.localNet = localNet;
	}

	public String getNetMask() {
		return netMask;
	}

	public void setNetMask(String netMask) {
		this.netMask = netMask;
	}

	public String getSysOid() {
		return sysOid;
	}

	public void setSysOid(String sysOid) {
		this.sysOid = sysOid;
	}
	
    /**
     * 按接口索引找到接口
     */
    public IfEntity getIfEntityByIndex(String ifIndex)
    {    
    	if(interfaceHash==null||interfaceHash.size()==0)
    		return null;
    	
    	if(interfaceHash.get(ifIndex)==null)
    	{
    		System.out.println(ipAddress + "中没有索引为" + ifIndex + "的接口");
    		return null;
    	}
		return (IfEntity)interfaceHash.get(ifIndex);
    }

	public String getSysName() {
		return sysName;
	}

	public void setSysName(String sysName) {
		this.sysName = sysName;
	}
	
	public String doPoll()
	{
//		AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
//		List monitorItemList = new ArrayList();//alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(id+"", "net", "");
//    	try{
//    		//获取被启用的所有被监视指标
//    		if(category==4){
//    			monitorItemList = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(id+"", "host", "");
//    		}else{
//    			monitorItemList = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(id+"", "net", "");
//    		}   		
//    	}catch(Exception e){
//    		e.printStackTrace();
//    	}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		double cpuvalue = 0;
		double memoryvalue = 0;
		String pingvalue = null;
		String inhdx = "0";
		String outhdx = "0";
		String ifInUcastPkts = "0";
		String time = "";
		sysLocation = alias+"("+ipAddress+")";
		Vector ipPingData = null;
		Vector memoryVector = null;
		Vector cpuV = null;
		Vector diskVector = null; 
		Vector interfaceVector = new Vector();
		String runmodel = PollingEngine.getCollectwebflag(); 
		Hashtable ipAllData = null;
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
			if(allutil != null && allutil.size()==3){
				AllUtilHdx inutilhdx = (AllUtilHdx)allutil.get(0);
				inhdx = inutilhdx.getThevalue();
				AllUtilHdx oututilhdx = (AllUtilHdx)allutil.get(1);
				outhdx = oututilhdx.getThevalue();
			}
			interfaceVector = (Vector)ipAllData.get("interface");
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
		msg.append(NodeHelper.getNodeCategory(category));
		msg.append("</font><br>");
		if(category==4)
		{
			msg.append("机器名：");
			msg.append(sysName);
			msg.append("<br>别名:");
			msg.append(alias);
			msg.append("<br>");
		}
		else
		{
		   msg.append("设备标签:");
		   msg.append(alias);
		   msg.append("<br>");
		}
		msg.append("IP地址:");
		msg.append(ipAddress);
		msg.append("<br>");
//		alarm = false;
//		status = 0;	
		
		if(!managed){
			setPrompt(msg.toString());
			return msg.toString();
		}
		StringBuffer alarmMsg = new StringBuffer(200);
//		System.out.println("ipAddress====="+ipAddress+"-------moidList.size()====="+moidList.size());
		if(moidList!=null&&moidList.size()>0){
//			CheckEventDao checkeventdao = null;
			Hashtable checkEventHash = ShareData.getCheckEventHash();	
			try{
//				checkeventdao = new CheckEventDao();
				for(int i=0;i<moidList.size();i++){
					AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode) moidList.get(i);
					if(alarmIndicatorsNode!=null){
						int flag = 0;
						try {
							if(checkEventHash!=null&&checkEventHash.size()>0){
//								List<CheckEvent> elist = checkeventdao.findCheckEvent(id+"",alarmIndicatorsNode.getType(),alarmIndicatorsNode.getSubtype(),alarmIndicatorsNode.getName());
								if(checkEventHash.get(id+":"+alarmIndicatorsNode.getType()+":"+alarmIndicatorsNode.getSubtype()+":"+alarmIndicatorsNode.getName())!=null){
									flag = (Integer)checkEventHash.get(id+":"+alarmIndicatorsNode.getType()+":"+alarmIndicatorsNode.getSubtype()+":"+alarmIndicatorsNode.getName());//elist.size();
								}
							}
							if(alarmIndicatorsNode.getType().equalsIgnoreCase("net")){
				        		//网络设备
				        		if(alarmIndicatorsNode.getName().equals("cpu")){
				        			//CPU利用率
				        			if("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())){
				        				if(flag>0){
				            				//有告警产生
				            				alarmMsg.append(alarmIndicatorsNode.getAlarm_info()+"<br>");
//				            				alarm = true;
//				            				if(status < flag)status = flag;
//				            				if(this.alarmlevel < flag)this.alarmlevel=flag;
				            			}  
				            			msg.append(alarmIndicatorsNode.getDescr() + ":" + cpuvalue + " "+alarmIndicatorsNode.getThreshlod_unit() + "<br>");
				        			}	
				        		}else if(alarmIndicatorsNode.getName().equals("memory")){
				        			//CPU利用率
				        			if("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())){
				            			if(flag>0){
				            				//有告警产生
				            				alarmMsg.append(alarmIndicatorsNode.getAlarm_info()+"<br>");
//				            				alarm = true;
//				            				if(status < flag)status = flag;
//				            				if(this.alarmlevel < flag)this.alarmlevel=flag;
				            			}  
				            			msg.append(alarmIndicatorsNode.getDescr() + ":" + memoryvalue + " "+alarmIndicatorsNode.getThreshlod_unit() + "<br>");
				        			}	
				        		}else if(alarmIndicatorsNode.getName().equals("ping")){
				        			//网络连通性
				        			if(pingvalue == null || pingvalue.trim().length()==0){
				        				//没有对PING数据进行采集,则不需要告警检查
				        				pingvalue="0";
				        				if("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())){
				                			msg.append(alarmIndicatorsNode.getDescr() + ":" + pingvalue + " "+alarmIndicatorsNode.getThreshlod_unit() + "<br>");
				            			}
				        			}else{
				        				if("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())){
				                			if(flag>0){
				                				//有告警产生
				                				alarmMsg.append(alarmIndicatorsNode.getAlarm_info()+"<br>");
//				                				alarm = true;
//				                				if(status < flag)status = flag;
//				                				if(this.alarmlevel < flag)this.alarmlevel=flag;
				                			}else{
				                				//判断之前是否有告警,若有则发送告警恢复信息
//				                				Hashtable createeventdata = ShareData.getCreateEventdata();
//				                				if (createeventdata.containsKey("network:ping:"+ipAddress)){
//				                					//发送告警恢复信息,并写告警恢复提示
//				                					createeventdata.remove("network:ping:"+ipAddress);
//				                					try{
//				                						createSMS("network","ping",ipAddress,getId()+"","故障恢复:"+ipAddress+" "+alarmIndicatorsNode.getAlarm_info()+"的故障已恢复",0,0,0);
//				                					}catch(Exception e){
//				                    					e.printStackTrace();
//				                    				}
//				                				}
				                				
				                			}
				                			msg.append(alarmIndicatorsNode.getDescr() + ":" + pingvalue + " "+alarmIndicatorsNode.getThreshlod_unit() + "<br>");
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
//				            				alarm = true;
//				            				if(status < flag)status = flag;
//				            				if(this.alarmlevel < flag)this.alarmlevel=flag;
				            			}
				    				}
				        		}else if(alarmIndicatorsNode.getName().equals("AllOutBandwidthUtilHdx")){
				        			//接口信息
				        			//出口流速
				    				if("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())){
				    					if(inhdx != null )msg.append(alarmIndicatorsNode.getDescr() + ":" + inhdx + " "+alarmIndicatorsNode.getThreshlod_unit() + "<br>");
//				    					int flag = 0;
//				    					flag = checkeventdao.findByName(id+":"+alarmIndicatorsNode.getType()+":"+"AllOutBandwidthUtilHdx");
				        				if(flag>0){
				            				//有告警产生
				            				alarmMsg.append(alarmIndicatorsNode.getAlarm_info()+"当前值:"+inhdx+" 阀值:"+alarmIndicatorsNode.getLimenvalue0()+"<br>");
//				            				alarm = true;
//				            				if(status < flag)status = flag;
//				            				if(this.alarmlevel < flag)this.alarmlevel=flag;
				            			}
				    				}
				        		}else if(alarmIndicatorsNode.getName().equals("interface")){
				        			//接口信息
				        			
				        			if (interfaceVector != null && interfaceVector.size()>0){
				        				//接口DOWN告警
					        			if("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())){
					    					List portconfiglist = new ArrayList();
					    					PortconfigDao configdao = new PortconfigDao(); 			
					    					Portconfig portconfig = null;
					    					Hashtable portconfigHash = new Hashtable();
					    					try {
					    						portconfiglist = configdao.getBySms(ipAddress);
					    					} catch (RuntimeException e) {
					    						e.printStackTrace();
					    					} finally {
					    						configdao.close();
					    					}
					    					if(portconfiglist != null && portconfiglist.size()>0){
					    						for(int k=0;k<portconfiglist.size();k++){
					    							portconfig = (Portconfig)portconfiglist.get(k);
					    							portconfigHash.put(portconfig.getPortindex()+"", portconfig);
					    							//SysLogger.info("add ===="+portconfig.getPortindex()+"");
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
//					    			            				alarm = true;
//					    			            				if(status < 3)status = 3;
//					    			            				if(this.alarmlevel < 3)this.alarmlevel=3;
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
//				        								flag = checkeventdao.findByName(id+":"+alarmIndicatorsNode.getType()+":"+"ifInMulticastPkts");
				        		        				if(flag>0){
				        		            				//有告警产生
				        		            				alarmMsg.append("第"+inpacks.getSubentity()+"端口"+alarmIndicatorsNode.getAlarm_info()+alarmIndicatorsNode.getLimenvalue0()+"<br>");
//				        		            				alarm = true;
//				        		            				if(status < flag)status = flag;
//				        		            				if(this.alarmlevel < flag)this.alarmlevel=flag;
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
//				        								flag = checkeventdao.findByName(id+":"+alarmIndicatorsNode.getType()+":"+"ifInBroadcastPkts");
				        		        				if(flag>0){
				        		            				//有告警产生
				        		            				alarmMsg.append("第"+inpacks.getSubentity()+"端口"+alarmIndicatorsNode.getAlarm_info()+alarmIndicatorsNode.getLimenvalue0()+"<br>");
//				        		            				alarm = true;
//				        		            				if(status < flag)status = flag;
//				        		            				if(this.alarmlevel < flag)this.alarmlevel=flag;
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
//				        								flag = checkeventdao.findByName(id+":"+alarmIndicatorsNode.getType()+":"+"ifOutMulticastPkts");
				        		        				if(flag>0){
				        		            				//有告警产生
				        		            				alarmMsg.append("第"+outpacks.getSubentity()+"端口"+alarmIndicatorsNode.getAlarm_info()+alarmIndicatorsNode.getLimenvalue0()+"<br>");
//				        		            				alarm = true;
//				        		            				if(status < flag)status = flag;
//				        		            				if(this.alarmlevel < flag)this.alarmlevel=flag;
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
//				        								flag = checkeventdao.findByName(id+":"+alarmIndicatorsNode.getType()+":"+"ifOutBroadcastPkts");
				        		        				if(flag>0){
				        		            				//有告警产生
				        		            				alarmMsg.append("第"+outpacks.getSubentity()+"端口"+alarmIndicatorsNode.getAlarm_info()+alarmIndicatorsNode.getLimenvalue0()+"<br>");
//				        		            				alarm = true;
//				        		            				if(status < flag)status = flag;
//				        		            				if(this.alarmlevel < flag)this.alarmlevel=flag;
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
//		        								flag = checkeventdao.findByName(id+":"+alarmIndicatorsNode.getType()+":"+"discardsperc");
		        		        				if(flag>0){
		        		            				//有告警产生
		        		            				alarmMsg.append("第"+discardsPerc.getSubentity()+"端口"+alarmIndicatorsNode.getAlarm_info()+alarmIndicatorsNode.getLimenvalue0()+"<br>");
//		        		            				alarm = true;
//		        		            				if(status < flag)status = flag;
//		        		            				if(this.alarmlevel < flag)this.alarmlevel=flag;
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
//		        								flag = checkeventdao.findByName(id+":"+alarmIndicatorsNode.getType()+":"+"errorsperc");
		        		        				if(flag>0){
		        		            				//有告警产生
		        		            				alarmMsg.append("第"+errorsPerc.getSubentity()+"端口"+alarmIndicatorsNode.getAlarm_info()+alarmIndicatorsNode.getLimenvalue0()+"<br>");
//		        		            				alarm = true;
//		        		            				if(status < flag)status = flag;
//		        		            				if(this.alarmlevel < flag)this.alarmlevel=flag;
		        		            			} else {
													if(errorsPerc.getThevalue() != null)msg.append(alarmIndicatorsNode.getDescr() + ":" + errorsPerc.getThevalue() + " "+alarmIndicatorsNode.getThreshlod_unit() + "<br>");
												}
											}
									    }
									}
				        		}
				        	}else if(alarmIndicatorsNode.getType().equalsIgnoreCase("host")){
				        		//主机设备
				        		//SysLogger.info(nm.getType()+"======"+nm.getName());
//				        		System.out.println(ipAddress+"=="+alarmIndicatorsNode.getType()+"======"+alarmIndicatorsNode.getName());
				        		if(alarmIndicatorsNode.getName().equals("cpu")){
				        			try {
										//CPU利用率
										if("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())){
//											int flag = 0;
//											flag = checkeventdao.findByName(id+":"+alarmIndicatorsNode.getType()+":"+"cpu");
											if(flag>0){
												//有告警产生
												alarmMsg.append(alarmIndicatorsNode.getAlarm_info()+"<br>");
//				            				alarm = true;
//				            				if(status < flag)status = flag;
//				            				if(this.alarmlevel < flag)this.alarmlevel=flag;
											}       			
											msg.append(alarmIndicatorsNode.getDescr() + ":" + cpuvalue + " "+alarmIndicatorsNode.getThreshlod_unit() + "<br>");
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
//									System.out.println(ipAddress+"======"+alarmIndicatorsNode.getName()+"==="+alarmMsg);
				        		}else if(alarmIndicatorsNode.getName().equals("ping")){
				        			try {
										//连通性
										//SysLogger.info("### 开始更新"+nm.getName()+" PING数据的XML信息####");
										if("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())){
											if(pingvalue == null || pingvalue.trim().length()==0){
												pingvalue="0";
											}else{
//												int flag = 0;
//												flag = checkeventdao.findByName(id+":"+alarmIndicatorsNode.getType()+":"+"ping");
												SysLogger.info(alarmIndicatorsNode.getName()+" "+alarmIndicatorsNode.getNodeid()+" flag="+flag);
												if(flag>0){
													//有告警产生
													alarmMsg.append(alarmIndicatorsNode.getAlarm_info()+"<br>");
//				                				alarm = true;
//				                				if(status < flag)status = flag;
//				                				if(this.alarmlevel < flag)this.alarmlevel=flag;
//				                				SysLogger.info(alarmMsg.toString());
//				                				SysLogger.info("alarm:"+alarm+"   alarmlevel:"+alarmlevel);
												}else{
													//判断之前是否有告警,若有则发送告警恢复信息
													Hashtable createeventdata = ShareData.getCreateEventdata();
													if (createeventdata.containsKey("host:ping:"+ipAddress)){
														//发送告警恢复信息,并写告警恢复提示
														createeventdata.remove("host:ping:"+ipAddress);
													}               				
												}
											}
											msg.append(alarmIndicatorsNode.getDescr() + ":" + pingvalue + " "+alarmIndicatorsNode.getThreshlod_unit() + "<br>");
										}
									} catch (Exception e) {
										e.printStackTrace();
									}

				        		}else if(alarmIndicatorsNode.getName().equals("diskperc")){
				        			try {
										//磁盘信息
										if("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())){
											if (diskVector != null && diskVector.size()>0){
												Hashtable alldiskalarmdata = new Hashtable();
												try{
													alldiskalarmdata = ShareData.getAlldiskalarmdata();
												}catch(Exception e){
													e.printStackTrace();
												}
												if (alldiskalarmdata == null )alldiskalarmdata = new Hashtable();
												for(int si=0;si<diskVector.size();si++){
													Diskcollectdata diskdata = null;
													diskdata = (Diskcollectdata)diskVector.elementAt(si);
													if(diskdata.getEntity().equalsIgnoreCase("Utilization")){
														//利用率
														flag = 0;
														if(getOstype() == 5 || getSysOid().startsWith("1.3.6.1.4.1.311.1.1.3")){
//															System.out.println(ipAddress+"===flag==="+id+":"+alarmIndicatorsNode.getType()+":"+"diskperc:diskperc:"+diskdata.getSubentity().substring(0, 3));
//															elist = checkeventdao.findCheckEvent(id+"",alarmIndicatorsNode.getType(),alarmIndicatorsNode.getSubtype(),alarmIndicatorsNode.getName(),diskdata.getSubentity().substring(0, 3));
															if(checkEventHash!=null&&checkEventHash.size()>0){
//																List<CheckEvent> elist = checkeventdao.findCheckEvent(id+"",alarmIndicatorsNode.getType(),alarmIndicatorsNode.getSubtype(),alarmIndicatorsNode.getName());
																if(checkEventHash.get(id+":"+alarmIndicatorsNode.getType()+":"+alarmIndicatorsNode.getSubtype()+":"+alarmIndicatorsNode.getName()+":"+diskdata.getSubentity().substring(0, 3))!=null){
																	flag = (Integer)checkEventHash.get(id+":"+alarmIndicatorsNode.getType()+":"+alarmIndicatorsNode.getSubtype()+":"+alarmIndicatorsNode.getName()+":"+diskdata.getSubentity().substring(0, 3));//elist.size();
																}
															}
														}else{
//															System.out.println(ipAddress+"===flag==="+id+":"+alarmIndicatorsNode.getType()+":"+"diskperc:diskperc:"+diskdata.getSubentity());
//															elist = checkeventdao.findCheckEvent(id+"",alarmIndicatorsNode.getType(),alarmIndicatorsNode.getSubtype(),alarmIndicatorsNode.getName(),diskdata.getSubentity());
															if(checkEventHash!=null&&checkEventHash.size()>0){
//																List<CheckEvent> elist = checkeventdao.findCheckEvent(id+"",alarmIndicatorsNode.getType(),alarmIndicatorsNode.getSubtype(),alarmIndicatorsNode.getName());
																if(checkEventHash.get(id+":"+alarmIndicatorsNode.getType()+":"+alarmIndicatorsNode.getSubtype()+":"+alarmIndicatorsNode.getName()+":"+diskdata.getSubentity())!=null){
																	flag = (Integer)checkEventHash.get(id+":"+alarmIndicatorsNode.getType()+":"+alarmIndicatorsNode.getSubtype()+":"+alarmIndicatorsNode.getName()+":"+diskdata.getSubentity());//elist.size();
																}
															}
														}
//														System.out.println(ipAddress+"===flag==="+flag);
										    			if(flag>0){
										    				//有告警产生
										    				Diskconfig diskconfig = null;
										    				if(getOstype() == 5 || getSysOid().startsWith("1.3.6.1.4.1.311.1.1.3")){
										    					diskconfig = (Diskconfig)alldiskalarmdata.get(ipAddress+":"+diskdata.getSubentity().substring(0, 3)+":"+"利用率阈值");
										    				}else{
										    					diskconfig = (Diskconfig)alldiskalarmdata.get(ipAddress+":"+diskdata.getSubentity()+":"+"利用率阈值");
										    				}
										    				
										    				int limevalue = 0;
										    				if(flag == 1){
										    					limevalue = diskconfig.getLimenvalue();
										    				}else if(flag == 2){
										    					limevalue = diskconfig.getLimenvalue1();
										    				}else
										    					limevalue = diskconfig.getLimenvalue2();
										    				
										    				if(getSysOid().startsWith("1.3.6.1.4.1.311.1.1.3")){
										    					//有告警产生
										        				alarmMsg.append(alarmIndicatorsNode.getAlarm_info()+" "+diskdata.getSubentity().substring(0, 3)+" 阀值:"+limevalue+"<br>");
										    				}else{
										    					//有告警产生
										    					alarmMsg.append(alarmIndicatorsNode.getAlarm_info()+" "+diskdata.getSubentity()+" 阀值:"+limevalue+"<br>");
										    				}
										    			}
//				            	        			DiskconfigDao diskconfigDao = new DiskconfigDao();
//						    						Diskconfig vo = diskconfigDao.loadByIpNameBak(ipAddress, diskdata.getSubentity(), "利用率阈值");
//						    	        			if(getSysOid().startsWith("1.3.6.1.4.1.311.1.1.3")){
//						    	        				if(vo!=null&&vo.getIs_show()==1)msg.append(alarmIndicatorsNode.getDescr() + ":" +diskdata.getSubentity().substring(0, 3)+" "+ CEIString.round(new Double(diskdata.getThevalue()).doubleValue(),2) + " "+alarmIndicatorsNode.getThreshlod_unit() + "<br>");
//						    	        			} else {
//						    	        				//UNIX服务器
//						    	        				if(vo!=null&&vo.getIs_show()==1)msg.append(alarmIndicatorsNode.getDescr() + ":" +diskdata.getSubentity()+" "+ CEIString.round(new Double(diskdata.getThevalue()).doubleValue(),2) + " "+alarmIndicatorsNode.getThreshlod_unit() + "<br>");
//						    	        			}
										    			//SysLogger.info("ostype ==== "+getOstype());
										    			if(getSysOid().startsWith("1.3.6.1.4.1.311.1.1.3")){
										    				//WINDOWS服务器
										    				msg.append(alarmIndicatorsNode.getDescr() + ":" +diskdata.getSubentity().substring(0, 3)+" "+ CEIString.round(new Double(diskdata.getThevalue()).doubleValue(),2) + " "+alarmIndicatorsNode.getThreshlod_unit() + "<br>");
										    			}else{
										    				//UNIX服务器
										    				msg.append(alarmIndicatorsNode.getDescr() + ":" +diskdata.getSubentity()+" "+ CEIString.round(new Double(diskdata.getThevalue()).doubleValue(),2) + " "+alarmIndicatorsNode.getThreshlod_unit() + "<br>");
										    			}
										    			//msg.append(nm.getDescr() + ":" +diskdata.getSubentity()+" "+ CEIString.round(new Double(diskdata.getThevalue()).doubleValue(),2) + " "+nm.getUnit() + "<br>");
													} 
//													System.out.println(ipAddress+"======"+alarmIndicatorsNode.getName()+"==="+alarmMsg);
													if(diskdata.getEntity().equalsIgnoreCase("UtilizationInc")){
//				            						//磁盘增长率 yangjun
//				            						int flag = checkeventdao.findByName(id+":"+nm.getType()+":"+"diskinc:"+diskdata.getSubentity());
//				            				        //int flag = checkeventutil.checkEvent("disk",new Double(diskdata.getThevalue()),nm,diskdata.getSubentity(),"增长率阈值");
////				            				        System.out.println("flag===="+flag);
//				            				        if(flag>0){
//				            	        				//有告警产生
//				            	        				//生成事件
//				            	        				Diskconfig diskconfig = null;
//				            	        				if(getOstype() ==4 || getSysOid().startsWith("1.3.6.1.4.1.311.1.1.3")){
//				            	        					diskconfig = (Diskconfig)alldiskalarmdata.get(ipAddress+":"+diskdata.getSubentity().substring(0, 3)+":"+"增长率阈值");
//				            	        				}else{
//				            	        					diskconfig = (Diskconfig)alldiskalarmdata.get(ipAddress+":"+diskdata.getSubentity()+":"+"增长率阈值");
//				            	        				}
//				            	        				
//				            	        				int limevalue = 0;
//				            	        				if(flag == 1){
//				            	        					limevalue = diskconfig.getLimenvalue();
//				            	        				}else if(flag == 2){
//				            	        					limevalue = diskconfig.getLimenvalue1();
//				            	        				}else
//				            	        					limevalue = diskconfig.getLimenvalue2();
//				            	        				
//				            	        				if(getSysOid().startsWith("1.3.6.1.4.1.311.1.1.3")){
//				            	        					//有告警产生
//				                	        				alarmMsg.append("硬盘增长率超过阈值 "+diskdata.getSubentity().substring(0, 3)+" 阀值:"+limevalue+"<br>");
//				                	        				alarm = true;
//				                	        				if(status < flag)status = flag;
//				            	        				}else{
//				            	        					//有告警产生
//				                	        				alarmMsg.append("硬盘增长率超过阈值 "+diskdata.getSubentity()+" 阀值:"+limevalue+"<br>");
//				                	        				alarm = true;
//				                	        				if(status < flag)status = flag;
//				            	        				}
//				            	        				if(this.alarmlevel < flag)this.alarmlevel=flag;
//				            	        			}
//				            	        			if(getSysOid().startsWith("1.3.6.1.4.1.311.1.1.3")){
//				            	        				//WINDOWS服务器
//				            	        				msg.append("硬盘增长率:" +diskdata.getSubentity().substring(0, 3)+" "+ CEIString.round(new Double(diskdata.getThevalue()).doubleValue(),2) + " "+nm.getThreshlod_unit() + "<br>");
//				            	        			}else{
//				            	        				//UNIX服务器
//				            	        				msg.append("硬盘增长率:" +diskdata.getSubentity()+" "+ CEIString.round(new Double(diskdata.getThevalue()).doubleValue(),2) + " "+nm.getThreshlod_unit() + "<br>");
//				            	        			}
													}
												}
											}
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
				        		}else if(alarmIndicatorsNode.getName().equalsIgnoreCase("physicalmemory")){
				        			try {
										//物理内存信息
										if("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())){
											if (memoryVector != null && memoryVector.size()>0){
												for(int si=0;si<memoryVector.size();si++){
													Memorycollectdata memorydata = (Memorycollectdata)memoryVector.elementAt(si);
													if(memorydata.getEntity().equalsIgnoreCase("Utilization")&& memorydata.getSubentity().equalsIgnoreCase("PhysicalMemory")){
														//利用率
//														System.out.println(ipAddress+"===flag==="+flag);
										    			if(flag>0){
										    				//有告警产生
										    				alarmMsg.append(alarmIndicatorsNode.getAlarm_info()+"<br>");
										    			}       			
										    			msg.append(alarmIndicatorsNode.getDescr() + ":" + memorydata.getSubentity()+" "+CEIString.round(new Double(memorydata.getThevalue()).doubleValue(),2) + " "+alarmIndicatorsNode.getThreshlod_unit() + "<br>");
													}
												}
											}
//											System.out.println(ipAddress+"======"+alarmIndicatorsNode.getName()+"==="+alarmMsg);
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
				        		}else if(alarmIndicatorsNode.getName().equalsIgnoreCase("virtualmemory")){
				        			try {
										//物理内存信息
										if("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())){
											if (memoryVector != null && memoryVector.size()>0){
												for(int si=0;si<memoryVector.size();si++){
													Memorycollectdata memorydata = (Memorycollectdata)memoryVector.elementAt(si);
													if(memorydata.getEntity().equalsIgnoreCase("Utilization")&& memorydata.getSubentity().equalsIgnoreCase("VirtualMemory")){
														//利用率
										    			if(flag>0){
										    				//有告警产生
										    				alarmMsg.append(alarmIndicatorsNode.getAlarm_info()+"<br>");
//				            	        				alarm = true;
//				            	        				if(status < flag)status = flag;
//				            	        				if(this.alarmlevel < flag)this.alarmlevel=flag;
										    			}       			
										    			msg.append(alarmIndicatorsNode.getDescr() + ":" + memorydata.getSubentity()+" "+CEIString.round(new Double(memorydata.getThevalue()).doubleValue(),2) + " "+alarmIndicatorsNode.getThreshlod_unit() + "<br>");
													}
												}
											}
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
				        		} else if(alarmIndicatorsNode.getName().equalsIgnoreCase("swapmemory")){
									try {
										//交换内存信息
										if("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())){
											if (memoryVector != null && memoryVector.size()>0){
												for(int si=0;si<memoryVector.size();si++){
													Memorycollectdata memorydata = (Memorycollectdata)memoryVector.elementAt(si);
													if(memorydata.getEntity().equalsIgnoreCase("Utilization")&&memorydata.getSubentity().equalsIgnoreCase("SwapMemory")){
														//利用率
//														int flag = 0;
//														flag = checkeventdao.findByName(id+":"+alarmIndicatorsNode.getType()+":"+alarmIndicatorsNode.getName());
										    			if(flag>0){
										    				//有告警产生
										    				alarmMsg.append(alarmIndicatorsNode.getAlarm_info()+"<br>");
//				            	        				alarm = true;
//				            	        				if(status < flag)status = flag;
//				            	        				if(this.alarmlevel < flag)this.alarmlevel=flag;
										    			}       			
										    			msg.append(alarmIndicatorsNode.getDescr() + ":" + memorydata.getSubentity()+" "+CEIString.round(new Double(memorydata.getThevalue()).doubleValue(),2) + " "+alarmIndicatorsNode.getThreshlod_unit() + "<br>");
													}
												}
											}
										}
									} catch (Exception e) {
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
        //这里只检测,不写事件
//        List proEventList = new ArrayList();
//        if(category == 4){
//        	Hashtable sharedata = ShareData.getSharedata();
//			Hashtable datahash = (Hashtable)sharedata.get(ipAddress);
//			Vector proVector = null;
//			if (datahash != null && datahash.size() > 0){
//				proVector = (Vector) datahash.get("process");
//			}   		
//    		/*
//    		 * nielin add 2010-08-18
//    		 *
//    		 * 创建进程组告警
//    		 * 
//    		 * start ===============================
//    		 */
//    		try{
//    			if(proVector != null && proVector.size()>0)
//    				proEventList = createProcessGroupEventList(ipAddress , proVector);
//    		}catch(Exception e){
//    			
//    		}
//    		/*
//    		 * nielin add 2010-08-18
//    		 *
//    		 * 创建进程组告警
//    		 * 
//    		 * end ===============================
//    		 */
//        }
//        if(proEventList != null && proEventList.size()>0){
//        	alarm = true;
//        }
//        
//        
//        
//        List hostServiceEventList = new ArrayList();
//        if(category == 4){       	
//        	Hashtable sharedata = ShareData.getSharedata();
//			Hashtable datahash = (Hashtable)sharedata.get(ipAddress);
//    		//service
//			Vector winserviceVector = null;
//			if (datahash != null && datahash.size() > 0){
//				winserviceVector = (Vector) datahash.get("winservice");
//			}
//    		
//    		/*
//    		 * nielin add 2010-08-18
//    		 *
//    		 * 创建服务组告警
//    		 * 
//    		 * start ===============================
//    		 */
//    		try{
//    			if(winserviceVector != null && winserviceVector.size()>0)
//    			hostServiceEventList = createHostServiceGroupEventList(ipAddress , winserviceVector);
//    		}catch(Exception e){
//    			e.printStackTrace();
//    		}
//    		/*
//    		 * nielin add 2010-08-18
//    		 *
//    		 * 创建服务组告警
//    		 * 
//    		 * end ===============================
//    		 */
//        }
//        if(hostServiceEventList != null && hostServiceEventList.size()>0){
//        	alarm = true;
//        } 
//        
//        
//        List jobForAS400EventList = new ArrayList();
//        if(category == 4){
//        	
//        	Hashtable sharedata = ShareData.getSharedata();
//			//if (datahash != null && datahash.size() > 0)
//			Hashtable datahash = (Hashtable)sharedata.get(ipAddress);
//        	//Hashtable allData = ShareData.getd();
//            //
//    		//service
//			List jobForAS400List = null;
//			if (datahash != null && datahash.size() > 0){
//				jobForAS400List = (List) datahash.get("Jobs");
//			}
//    		
//    		/*
//    		 * nielin add 2010-08-18
//    		 *
//    		 * 创建作业组告警
//    		 * 
//    		 * start ===============================
//    		 */
//    		try{
//    			if(jobForAS400List != null && jobForAS400List.size()>0){
//    				jobForAS400EventList = createJobForAS400GroupEventList(ipAddress , jobForAS400List);
//    			}
//    		}catch(Exception e){
//    			e.printStackTrace();
//    		}
//    		/*
//    		 * nielin add 2010-08-18
//    		 *
//    		 * 创建作业组告警
//    		 * 
//    		 * end ===============================
//    		 */
//        }
//        if(jobForAS400EventList != null && jobForAS400EventList.size()>0){
//        	alarm = true;
//        	//this.alarmlevel=3;
//        }
//        
        
        
        
//		if(alarmMsg.toString().length()>0)
//		{	
//		    msg.append("<font color='red'>--报警信息:--</font><br>");
//		    msg.append(alarmMsg.toString());
//		    if(proEventList != null && proEventList.size()>0){
//	        	for(int i=0;i<proEventList.size();i++){
//	        		EventList eventList = (EventList)proEventList.get(i);
//	        		msg.append(eventList.getContent()+"<br>");
//	        		if(eventList.getLevel1() > this.alarmlevel){
//		    			this.alarmlevel = eventList.getLevel1();
//		    		}
//	        	}
//	        }
//		    
//		    if(hostServiceEventList != null && hostServiceEventList.size() > 0){
//		    	for(int i = 0 ; i < hostServiceEventList.size() ; i++){
//		    		EventList eventList = (EventList)hostServiceEventList.get(i);
//		    		
//		    		msg.append(eventList.getContent() + "<br>");
//		    		if(eventList.getLevel1() > this.alarmlevel){
//		    			this.alarmlevel = eventList.getLevel1();
//		    		}
//		    	}
//		    	
//		    }
//		    
//		    if(jobForAS400EventList != null && jobForAS400EventList.size() > 0){
//		    	for(int i = 0 ; i < jobForAS400EventList.size() ; i++){
//		    		EventList eventList = (EventList)jobForAS400EventList.get(i);
//		    		
//		    		msg.append(eventList.getContent() + "<br>");
//		    		if(eventList.getLevel1() > this.alarmlevel){
//		    			this.alarmlevel = eventList.getLevel1();
//		    		}
//		    	}
//		    	
//		    }
//		    
//		    
//		    Host host = (Host)PollingEngine.getInstance().getNodeByIP(ipAddress);
//		    host.setStatus(alarmlevel);
//		    //SysLogger.info(alarmMsg.toString());
//		    this.setAlarm(true);
//		}   
		msg.append("更新时间:" + lastTime);	
		//SysLogger.info("===="+msg.toString());
		setPrompt(msg.toString());
        return msg.toString();		
	}
	
	/**
	 * 刷新节点的状态（告警状态、告警等级） 
	 */
	public synchronized void refreshNodeState(){
		AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
		List monitorItemList = new ArrayList();//alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(id+"", "net", "");
    	try{
    		//获取被启用的所有被监视指标
    		if(category==4){
    			monitorItemList = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(id+"", "host", "");
    		}else{
    			monitorItemList = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(id+"", "net", "");
    		}   		
    	}catch(Exception e){
    		e.printStackTrace();
    	}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		double cpuvalue = 0;
		String pingvalue = null;
		String inhdx = "0";
		String outhdx = "0";
		String ifInUcastPkts = "0";
		String time = "";
		sysLocation = alias+"("+ipAddress+")";
		Vector ipPingData = null;
		Vector memoryVector = null;
		Vector cpuV = null;
		Vector diskVector = null; 
		Vector interfaceVector = new Vector();
		String runmodel = PollingEngine.getCollectwebflag(); 
		Hashtable ipAllData = null;
		if("0".equals(runmodel)){
			//采集与访问是分离模式 
			ipAllData = (Hashtable)ShareData.getSharedata().get(ipAddress);
			ipPingData = (Vector)ShareData.getPingdata().get(ipAddress);
		}else{
			//采集与访问是集成模式 
			ipAllData = (Hashtable)ShareData.getAllNetworkData().get(ipAddress);
			ipPingData = (Vector)ShareData.getAllNetworkPingData().get(ipAddress);
		}
		
		if(ipAllData != null){
			cpuV = (Vector)ipAllData.get("cpu");
			memoryVector = (Vector)ipAllData.get("memory");
			diskVector = (Vector)ipAllData.get("disk");
			Vector allutil = (Vector)ipAllData.get("allutilhdx");
			if(allutil != null && allutil.size()==3){
				AllUtilHdx inutilhdx = (AllUtilHdx)allutil.get(0);
				inhdx = inutilhdx.getThevalue();
				AllUtilHdx oututilhdx = (AllUtilHdx)allutil.get(1);
				outhdx = oututilhdx.getThevalue();
			}
			interfaceVector = (Vector)ipAllData.get("interface");
		}
		if(cpuV != null && cpuV.size()>0){
			CPUcollectdata cpu = (CPUcollectdata)cpuV.get(0);
			if(cpu != null && cpu.getThevalue() != null){
				cpuvalue = new Double(cpu.getThevalue());
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
		
		alarm = false;
		status = 0;	
		
		//CheckEventUtil checkeventutil = new CheckEventUtil();			
		Hashtable checkEventHash = ShareData.getCheckEventHash();	
		if(checkEventHash == null)checkEventHash = new Hashtable();		
		try{
			for(int i=0;i<monitorItemList.size();i++){  
	        	AlarmIndicatorsNode nm = (AlarmIndicatorsNode)monitorItemList.get(i);
	        	if(nm.getType().equalsIgnoreCase("net")){
	        		//网络设备
	        		if(nm.getName().equals("cpu")){
	        			//CPU利用率
	        			//SysLogger.info("limenvalue0:"+nm.getLimenvalue0()+"   cpuvalue:"+cpuvalue);
	        			if("1".equalsIgnoreCase(nm.getEnabled())){
	        				//int flag = checkeventutil.checkEvent("cpu",cpuvalue,nm,"","");
	        				int flag = 0;
	        				if(checkEventHash.containsKey(id+":"+nm.getType()+":"+"cpu")){
	        					flag = (Integer)checkEventHash.get(id+":"+nm.getType()+":"+"cpu");
	        				}
	        				//flag = checkeventdao.findByName(id+":"+nm.getType()+":"+"cpu");
	            			if(flag>0){
	            				//有告警产生
	            				alarm = true;
	            				if(status < flag)status = flag;
	            				if(this.alarmlevel < flag)this.alarmlevel=flag;
	            			}  
	        			}	
	        		}else if(nm.getName().equals("ping")){
	        			//网络连通性
	        			//SysLogger.info("### 开始更新"+nm.getName()+" PING数据的XML信息####");
	        			if(pingvalue == null || pingvalue.trim().length()==0){
	        				//没有对PING数据进行采集,则不需要告警检查
	        				pingvalue="0";
	        			}else{
	        				if("1".equalsIgnoreCase(nm.getEnabled())){
	        					int flag = 0;
	        					if(checkEventHash.containsKey(id+":"+nm.getType()+":"+"ping")){
		        					flag = (Integer)checkEventHash.get(id+":"+nm.getType()+":"+"ping");
		        				}
	        					//flag = checkeventdao.findByName(id+":"+nm.getType()+":"+"ping");
	                			if(flag>0){
	                				alarm = true;
	                				if(status < flag)status = flag;
	                				if(this.alarmlevel < flag)this.alarmlevel=flag;
	                			}
	            			}
	        			}
	        		}else if(nm.getName().equals("AllInBandwidthUtilHdx")){
	        			//接口信息
	        			//入口流速
	    				if("1".equalsIgnoreCase(nm.getEnabled())){
	    					int flag = 0;
	    					if(checkEventHash.containsKey(id+":"+nm.getType()+":"+"AllInBandwidthUtilHdx")){
	        					flag = (Integer)checkEventHash.get(id+":"+nm.getType()+":"+"AllInBandwidthUtilHdx");
	        				}
	    					//flag = checkeventdao.findByName(id+":"+nm.getType()+":"+"AllInBandwidthUtilHdx");
	        				if(flag>0){
	            				//有告警产生
	            				alarm = true;
	            				if(status < flag)status = flag;
	            				if(this.alarmlevel < flag)this.alarmlevel=flag;
	            			}
	    				}
	        		}else if(nm.getName().equals("AllOutBandwidthUtilHdx")){
	        			//接口信息
	        			//出口流速
	    				if("1".equalsIgnoreCase(nm.getEnabled())){
	    					int flag = 0;
	    					if(checkEventHash.containsKey(id+":"+nm.getType()+":"+"AllOutBandwidthUtilHdx")){
	        					flag = (Integer)checkEventHash.get(id+":"+nm.getType()+":"+"AllOutBandwidthUtilHdx");
	        				}
	    					//flag = checkeventdao.findByName(id+":"+nm.getType()+":"+"AllOutBandwidthUtilHdx");
	        				if(flag>0){
	            				//有告警产生
	            				alarm = true;
	            				if(status < flag)status = flag;
	            				if(this.alarmlevel < flag)this.alarmlevel=flag;
	            			}
	    				}
	        		}else if(nm.getName().equals("interface")){
	        			//接口信息
	        			if (interfaceVector != null && interfaceVector.size()>0){
	        				//接口DOWN告警
		        			if("1".equalsIgnoreCase(nm.getEnabled())){
//		    					List portconfiglist = new ArrayList();
//		    					
//		    								
//		    					Portconfig portconfig = null;			
//		    					Hashtable allportconfighash = ShareData.getPortConfigHash();;
//		    					if(allportconfighash != null && allportconfighash.size()>0){
//		    						if(allportconfighash.containsKey(ipAddress)){
//		    							portconfiglist = (List)allportconfighash.get(ipAddress);
//		    						}
//		    					}				
//		    					Hashtable portconfigHash = new Hashtable();
//		    					if(portconfiglist != null && portconfiglist.size()>0){
//		    						for(int k=0;k<portconfiglist.size();k++){
//		    							portconfig = (Portconfig)portconfiglist.get(k);
//		    							portconfigHash.put(portconfig.getPortindex()+"", portconfig);
//		    						}
//		    					}
//		    					portconfig = null;
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
		        					for(int k=0;k<portconfiglist.size();k++){
		        						portconfig = (Portconfig)portconfiglist.get(k);
		        						portconfigHash.put(portconfig.getPortindex()+"", portconfig);
		        					}
		        				}
		        				portconfig = null;
//		    					List portconfiglist = new ArrayList();
//		    					PortconfigDao configdao = new PortconfigDao(); 			
//		    					Portconfig portconfig = null;
//		    					Hashtable portconfigHash = new Hashtable();
//		    					try {
//		    						portconfiglist = configdao.getBySms(ipAddress);
//		    					} catch (RuntimeException e) {
//		    						e.printStackTrace();
//		    					} finally {
//		    						configdao.close();
//		    					}
//		    					if(portconfiglist != null && portconfiglist.size()>0){
//		    						for(int k=0;k<portconfiglist.size();k++){
//		    							portconfig = (Portconfig)portconfiglist.get(k);
//		    							portconfigHash.put(portconfig.getPortindex()+"", portconfig);
//		    						}
//		    					}
//		    					portconfig = null;
		    					
		    					
		    					for(int m=0;m<interfaceVector.size();m++){
		    						Interfacecollectdata interfacedata=(Interfacecollectdata)interfaceVector.get(m);
		    						if(interfacedata != null){
		    							if(interfacedata.getCategory().equalsIgnoreCase("Interface")&& interfacedata.getEntity().equalsIgnoreCase("ifOperStatus") && interfacedata.getSubentity() != null){
		    								if(portconfigHash.containsKey(interfacedata.getSubentity())){
		    									//存在端口配置,则判断是否DOWN
		    									portconfig = (Portconfig)portconfigHash.get(interfacedata.getSubentity());
		    									if (!"up".equalsIgnoreCase(interfacedata.getThevalue())){
		    										//有告警产生
		    			            				alarm = true;
		    			            				if(status < 3)status = 3;
		    			            				if(this.alarmlevel < 3)this.alarmlevel=3;
		    									}
		    								}
		    							}
		    						}
		    					}      			
		        			}
	        				
	        			}
	        			
	        			if(nm.getSubentity().equals("AllInBandwidthUtilHdx")){
	        			}else if(nm.getName().equals("AllOutBandwidthUtilHdx")){      				
	        			}else if(nm.getName().equals("ifInMulticastPkts")){
	        				//入口单向
	        				if("1".equalsIgnoreCase(nm.getEnabled())){
	        					Hashtable sharedata = ShareData.getSharedata();
	            			    Hashtable ipdata = (Hashtable)sharedata.get(ipAddress);
	            			    if(ipdata == null)ipdata = new Hashtable();
	            			    Vector tempv = (Vector)ipdata.get("inpacks");
	            			    if (tempv != null && tempv.size()>0){
	        						for(int k=0;k<tempv.size();k++){
	        							InPkts inpacks=(InPkts)tempv.elementAt(k);
	        							if(inpacks.getEntity().equalsIgnoreCase("ifInMulticastPkts")){
	        								int flag = 0;
	        								if(checkEventHash.containsKey(id+":"+nm.getType()+":"+"ifInMulticastPkts")){
	        		        					flag = (Integer)checkEventHash.get(id+":"+nm.getType()+":"+"ifInMulticastPkts");
	        		        				}
	        								//flag = checkeventdao.findByName(id+":"+nm.getType()+":"+"ifInMulticastPkts");
	        		        				if(flag>0){
	        		            				//有告警产生
	        		            				alarm = true;
	        		            				if(status < flag)status = flag;
	        		            				if(this.alarmlevel < flag)this.alarmlevel=flag;
	        		            			}
	        							}
	        							
	        						}
	            			    }
	        				}
	        				
	        			}else if(nm.getName().equals("ifInBroadcastPkts")){
	        				//入口非单向
	        				//if(ifInUcastPkts != null )msg.append(nm.getDescr() + ":" + outhdx + " "+nm.getUnit() + "<br>");
	        				if("1".equalsIgnoreCase(nm.getEnabled())){
	        					Hashtable sharedata = ShareData.getSharedata();
	            			    Hashtable ipdata = (Hashtable)sharedata.get(ipAddress);
	            			    if(ipdata == null)ipdata = new Hashtable();
	            			    Vector tempv = (Vector)ipdata.get("inpacks");
	            			    if (tempv != null && tempv.size()>0){
	        						for(int k=0;k<tempv.size();k++){
	        							InPkts inpacks=(InPkts)tempv.elementAt(k);
	        							if(inpacks.getEntity().equalsIgnoreCase("ifInBroadcastPkts")){
	        								int flag = 0;
	        								if(checkEventHash.containsKey(id+":"+nm.getType()+":"+"ifInBroadcastPkts")){
	        		        					flag = (Integer)checkEventHash.get(id+":"+nm.getType()+":"+"ifInBroadcastPkts");
	        		        				}
	        								//flag = checkeventdao.findByName(id+":"+nm.getType()+":"+"ifInBroadcastPkts");
	        		        				if(flag>0){
	        		            				//有告警产生
	        		            				alarm = true;
	        		            				if(status < flag)status = flag;
	        		            				//生成事件
	        		            				if(this.alarmlevel < flag)this.alarmlevel=flag;
	        		            			}
	        							}
	        							
	        						}
	            			    }
	        				}
	        			}else if(nm.getName().equals("ifOutMulticastPkts")){
	        				//出口单向
	        				//if(ifInUcastPkts != null )msg.append(nm.getDescr() + ":" + outhdx + " "+nm.getUnit() + "<br>");
	        				if("1".equalsIgnoreCase(nm.getEnabled())){
	        					Hashtable sharedata = ShareData.getSharedata();
	            			    Hashtable ipdata = (Hashtable)sharedata.get(ipAddress);
	            			    if(ipdata == null)ipdata = new Hashtable();
	            			    Vector tempv = (Vector)ipdata.get("outpacks");
	            			    if (tempv != null && tempv.size()>0){
	        						for(int k=0;k<tempv.size();k++){
	        							OutPkts outpacks=(OutPkts)tempv.elementAt(k);
	        							if(outpacks.getEntity().equalsIgnoreCase("ifOutMulticastPkts")){
	        								//int flag = checkeventutil.checkEvent("ifOutMulticastPkts",Double.parseDouble(outpacks.getThevalue()),nm,"","");
	        								int flag = 0;
	        								if(checkEventHash.containsKey(id+":"+nm.getType()+":"+"ifOutMulticastPkts")){
	        		        					flag = (Integer)checkEventHash.get(id+":"+nm.getType()+":"+"ifOutMulticastPkts");
	        		        				}
	        								//flag = checkeventdao.findByName(id+":"+nm.getType()+":"+"ifOutMulticastPkts");
	        		        				if(flag>0){
	        		            				//有告警产生
	        		            				alarm = true;
	        		            				if(status < flag)status = flag;
	        		            				//生成事件
	        		            				if(this.alarmlevel < flag)this.alarmlevel=flag;
	        		            			}
	        							}
	        							
	        						}
	            			    }
	        				}
	        				
	        			}else if(nm.getName().equals("ifOutBroadcastPkts")){
	        				//出口非单向
	        				if("1".equalsIgnoreCase(nm.getEnabled())){
	        					Hashtable sharedata = ShareData.getSharedata();
	            			    Hashtable ipdata = (Hashtable)sharedata.get(ipAddress);
	            			    if(ipdata == null)ipdata = new Hashtable();
	            			    Vector tempv = (Vector)ipdata.get("outpacks");
	            			    if (tempv != null && tempv.size()>0){
	        						for(int k=0;k<tempv.size();k++){
	        							OutPkts outpacks=(OutPkts)tempv.elementAt(k);
	        							if(outpacks.getEntity().equalsIgnoreCase("ifOutBroadcastPkts")){
	        								//int flag = checkeventutil.checkEvent("ifOutBroadcastPkts",Double.parseDouble(outpacks.getThevalue()),nm,"","");
	        								int flag = 0;
	        								if(checkEventHash.containsKey(id+":"+nm.getType()+":"+"ifOutBroadcastPkts")){
	        		        					flag = (Integer)checkEventHash.get(id+":"+nm.getType()+":"+"ifOutBroadcastPkts");
	        		        				}
	        								//flag = checkeventdao.findByName(id+":"+nm.getType()+":"+"ifOutBroadcastPkts");
	        		        				if(flag>0){
	        		            				//有告警产生
	        		            				alarm = true;
	        		            				if(status < flag)status = flag;
//	        		            				//生成事件
	        		            				if(this.alarmlevel < flag)this.alarmlevel=flag;
	        		            			}
	        							}
	        							
	        						}
	            			    }
	        				}
	        				
	        			}
	        		}
	        	}else if(nm.getType().equalsIgnoreCase("host")){
	        		//主机设备
	        		if(nm.getName().equals("cpu")){
	        			//CPU利用率
	        			if("1".equalsIgnoreCase(nm.getEnabled())){
	        				int flag = 0;
	        				if(checkEventHash.containsKey(id+":"+nm.getType()+":"+"cpu")){
	        					flag = (Integer)checkEventHash.get(id+":"+nm.getType()+":"+"cpu");
	        				}
	        				//flag = checkeventdao.findByName(id+":"+nm.getType()+":"+"cpu");
	            			if(flag>0){
	            				//有告警产生
	            				alarm = true;
	            				if(status < flag)status = flag;
	            				if(this.alarmlevel < flag)this.alarmlevel=flag;
	            			}       			
	        			}
	        			
	        		}else if(nm.getName().equals("ping")){
	        			//连通性
	        			if("1".equalsIgnoreCase(nm.getEnabled())){
	            			if(pingvalue == null || pingvalue.trim().length()==0){
	            				pingvalue="0";
	            			}else{
	            				int flag = 0;
	            				if(checkEventHash.containsKey(id+":"+nm.getType()+":"+"ping")){
		        					flag = (Integer)checkEventHash.get(id+":"+nm.getType()+":"+"ping");
		        				}
	            				//int flag = checkeventdao.findByName(id+":"+nm.getType()+":"+"ping");
	                			if(flag>0){
	                				//有告警产生
	                				alarm = true;
	                				if(status < flag)status = flag;
	                				if(this.alarmlevel < flag)this.alarmlevel=flag;
	                			}
	            			}
	        			}

	        		}else if(nm.getName().equals("diskperc")){
	        			//磁盘信息
	        			if("1".equalsIgnoreCase(nm.getEnabled())){
	        				if (diskVector != null && diskVector.size()>0){
	        					//Hashtable hostdata = ShareData.getHostdata();
	            				Hashtable alldiskalarmdata = new Hashtable();
	            				try{
	            					alldiskalarmdata = ShareData.getAlldiskalarmdata();
	            				}catch(Exception e){
	            					e.printStackTrace();
	            				}
	            				if (alldiskalarmdata == null )alldiskalarmdata = new Hashtable();
	            				for(int si=0;si<diskVector.size();si++){
	            					Diskcollectdata diskdata = null;
	            					diskdata = (Diskcollectdata)diskVector.elementAt(si);
	            					if(diskdata.getEntity().equalsIgnoreCase("Utilization")){
	            						//利用率
	            						int flag = 0;
	            						if(getOstype() ==4 || getSysOid().startsWith("1.3.6.1.4.1.311.1.1.3")){
	            							if(checkEventHash.containsKey(id+":"+nm.getType()+":"+"diskperc:diskperc:"+diskdata.getSubentity().substring(0, 3))){
	        		        					flag = (Integer)checkEventHash.get(id+":"+nm.getType()+":"+"diskperc:diskperc:"+diskdata.getSubentity().substring(0, 3));
	        		        				}
	            							//flag = checkeventdao.findByName(id+":"+nm.getType()+":"+"diskperc:diskperc:"+diskdata.getSubentity().substring(0, 3));
	            						}else{
	            							if(checkEventHash.containsKey(id+":"+nm.getType()+":"+"ping")){
	            								if(checkEventHash.containsKey(id+":"+nm.getType()+":"+"ping")){
	            		        					flag = (Integer)checkEventHash.get(id+":"+nm.getType()+":"+"ping");
	            		        				}
	        		        					//flag = (Integer)checkEventHash.get(id+":"+nm.getType()+":"+"ping");
	        		        				}
	            							//flag = checkeventdao.findByName(id+":"+nm.getType()+":"+"diskperc:diskperc:"+diskdata.getSubentity());
	            						}
	            	        			if(flag>0){
	            	        				//有告警产生
	            	        				//生成事件
	            	        				Diskconfig diskconfig = null;
	            	        				if(getOstype() ==4 || getSysOid().startsWith("1.3.6.1.4.1.311.1.1.3")){
	            	        					diskconfig = (Diskconfig)alldiskalarmdata.get(ipAddress+":"+diskdata.getSubentity().substring(0, 3)+":"+"利用率阈值");
	            	        				}else{
	            	        					diskconfig = (Diskconfig)alldiskalarmdata.get(ipAddress+":"+diskdata.getSubentity()+":"+"利用率阈值");
	            	        				}
	            	        				
//	            	        				int limevalue = 0;
//	            	        				if(flag == 1){
//	            	        					limevalue = diskconfig.getLimenvalue();
//	            	        				}else if(flag == 2){
//	            	        					limevalue = diskconfig.getLimenvalue1();
//	            	        				}else
//	            	        					if(diskconfig == null){
//	            	        						
//	            	        					}else{
//	            	        						limevalue = diskconfig.getLimenvalue2();
//	            	        					}
	            	        				if(getSysOid().startsWith("1.3.6.1.4.1.311.1.1.3")){
	            	        					//有告警产生
	                	        				alarm = true;
	                	        				if(status < flag)status = flag;
	            	        				}else{
	            	        					//有告警产生
//	                	        				alarmMsg.append(nm.getAlarmInfo()+" "+diskdata.getSubentity()+" 阀值:"+limevalue+"<br>");
	                	        				alarm = true;
	                	        				if(status < flag)status = flag;
	            	        				}
	            	        				if(this.alarmlevel < flag)this.alarmlevel=flag;
	            	        			}
	            					} 
	            					if(diskdata.getEntity().equalsIgnoreCase("UtilizationInc")){
	            					}
	            				}
	            			}
	        			}
	        			
	        		}else if(nm.getName().equals("physicalmemory")){
	        			//物理内存信息
	        			if("1".equalsIgnoreCase(nm.getEnabled())){
	            			if (memoryVector != null && memoryVector.size()>0){
	            				for(int si=0;si<memoryVector.size();si++){
	            					Memorycollectdata memorydata = (Memorycollectdata)memoryVector.elementAt(si);
	            					if(memorydata.getEntity().equalsIgnoreCase("Utilization")&& memorydata.getSubentity().equalsIgnoreCase("PhysicalMemory")){
	            						//利用率
	            						int flag = 0;
	            						if(checkEventHash.containsKey(id+":"+nm.getType()+":"+nm.getName())){
        		        					flag = (Integer)checkEventHash.get(id+":"+nm.getType()+":"+nm.getName());
        		        				}
	            						//flag = checkeventdao.findByName(id+":"+nm.getType()+":"+nm.getName());
	            	        			if(flag>0){
	            	        				//有告警产生
	            	        				alarm = true;
	            	        				if(status < flag)status = flag;
	            	        				//生成事件
	            	        				if(this.alarmlevel < flag)this.alarmlevel=flag;
	            	        			}       			
	            					}
	            				}
	            			}
	        			}
	        		}else if(nm.getName().equals("virtualmemory")){
	        			//物理内存信息
	        			if("1".equalsIgnoreCase(nm.getEnabled())){
	            			if (memoryVector != null && memoryVector.size()>0){
	            				for(int si=0;si<memoryVector.size();si++){
	            					Memorycollectdata memorydata = (Memorycollectdata)memoryVector.elementAt(si);
	            					if(memorydata.getEntity().equalsIgnoreCase("Utilization")&& memorydata.getSubentity().equalsIgnoreCase("VirtualMemory")){
	            						//利用率
	            						int flag = 0;
	            						if(checkEventHash.containsKey(id+":"+nm.getType()+":"+nm.getName())){
        		        					flag = (Integer)checkEventHash.get(id+":"+nm.getType()+":"+nm.getName());
        		        				}
	            						//flag = checkeventdao.findByName(id+":"+nm.getType()+":"+nm.getName());
	            	        			if(flag>0){
	            	        				//有告警产生
	            	        				alarm = true;
	            	        				if(status < flag)status = flag;
	            	        				//生成事件
	            	        				if(this.alarmlevel < flag)this.alarmlevel=flag;
	            	        			}       			
	            					}
	            				}
	            			}
	        			}
	        		}
	        	}
	        }
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			//checkeventdao.close();
		}
        //这里只检测,不写事件
        List proEventList = new ArrayList();
        if(category == 4){
        	Hashtable sharedata = ShareData.getSharedata();
			Hashtable datahash = (Hashtable)sharedata.get(ipAddress);
			Vector proVector = null;
			if (datahash != null && datahash.size() > 0){
				proVector = (Vector) datahash.get("process");
			}   		
    		try{
    			if(proVector != null && proVector.size()>0)
    				proEventList = createProcessGroupEventList(ipAddress , proVector);
    		}catch(Exception e){
    			
    		}
        }
        if(proEventList != null && proEventList.size()>0){
        	alarm = true;
        }
        List hostServiceEventList = new ArrayList();
        if(category == 4){       	
        	Hashtable sharedata = ShareData.getSharedata();
			Hashtable datahash = (Hashtable)sharedata.get(ipAddress);
    		//service
			Vector winserviceVector = null;
			if (datahash != null && datahash.size() > 0){
				winserviceVector = (Vector) datahash.get("winservice");
			}
    		try{
    			if(winserviceVector != null && winserviceVector.size()>0)
    			hostServiceEventList = createHostServiceGroupEventList(ipAddress , winserviceVector);
    		}catch(Exception e){
    			e.printStackTrace();
    		}
        }
        if(hostServiceEventList != null && hostServiceEventList.size()>0){
        	alarm = true;
        } 
        List jobForAS400EventList = new ArrayList();
        if(category == 4){
        	Hashtable sharedata = ShareData.getSharedata();
			Hashtable datahash = (Hashtable)sharedata.get(ipAddress);
			List jobForAS400List = null;
			if (datahash != null && datahash.size() > 0){
				jobForAS400List = (List) datahash.get("Jobs");
			}
    		try{
    			if(jobForAS400List != null && jobForAS400List.size()>0){
    				jobForAS400EventList = createJobForAS400GroupEventList(ipAddress , jobForAS400List);
    			}
    		}catch(Exception e){
    			e.printStackTrace();
    		}
        }
        if(jobForAS400EventList != null && jobForAS400EventList.size()>0){
        	alarm = true;
        	//this.alarmlevel=3;
        }
	}
	
	public String getShowMessage()
	{
        return prompt;		
	}
	private int checkEvent(String subentity,double thevalue,NodeMonitor nm,String name,String bak){
		String diskname="";
		int level = 0;
		int returnflag = 0;
		try{
		Vector limenV = new Vector();
		
		if(subentity.equalsIgnoreCase("ping")){
			if(thevalue <= nm.getLimenvalue0()){
				if(thevalue <= nm.getLimenvalue1()){
					if(thevalue <= nm.getLimenvalue2()){
						level = 3;
					}else{
						level = 2;
					}
				}else{
					level = 1;
				}
			}else{
				level = 0;
			}
			//SysLogger.info(nm.getIp()+" ping level ===="+level);
		}else if(subentity.equalsIgnoreCase("disk")){
			//依据裁判告警阀值进行判断
			//判断该磁盘是否需要告警	
//			Hashtable hostdata = ShareData.getHostdata();
			Hashtable alldiskalarmdata = new Hashtable();
			try{
				alldiskalarmdata = ShareData.getAlldiskalarmdata();
			}catch(Exception e){
				e.printStackTrace();
			}
			if (alldiskalarmdata == null )alldiskalarmdata = new Hashtable();
			//判断是否为WINDOWS机器
			int limenvalue = 0;
			int limenvalue1 = 0;
			int limenvalue2 = 0;
			int monflag = 0;
			int smsflag = 0;
			int smsflag1 = 0;
			int smsflag2 = 0;
			
			Host host = (Host)PollingEngine.getInstance().getNodeByIP(nm.getIp());
			if(host == null)return level;
			
			//SysLogger.info(host.getIpAddress()+"====Ostype:"+host.getOstype());
			
			if(host.getOstype()==4){
				//SysLogger.info(host.getIpAddress()+"--------"+name+"-----------"+name.substring(0, 3));
				//SysLogger.info(host.getIpAddress()+"====ip:name: "+nm.getIp()+":"+name.substring(0, 3));
				if(alldiskalarmdata.containsKey(nm.getIp()+":"+name.substring(0, 3)+":"+bak)){//yangjun
					//SysLogger.info(host.getIpAddress()+"====存在 ip:name: "+nm.getIp()+":"+name.substring(0, 3));
					Diskconfig diskconfig = (Diskconfig)alldiskalarmdata.get(nm.getIp()+":"+name.substring(0, 3)+":"+bak);
					limenvalue = diskconfig.getLimenvalue();
					limenvalue1 = diskconfig.getLimenvalue1();
					limenvalue2 = diskconfig.getLimenvalue2();
					smsflag = diskconfig.getSms1();
					smsflag1 = diskconfig.getSms2();
					smsflag2 = diskconfig.getSms3();
					monflag = diskconfig.getMonflag();
					diskname=name.substring(0, 3);
				}
			}else if(getSysOid() != null && getSysOid().startsWith("1.3.6.1.4.1.311.1.1.3")){
				//WINDOWS服务器
				//SysLogger.info(host.getIpAddress()+"--------"+name+"-----------"+name.substring(0, 3));
				//SysLogger.info(host.getIpAddress()+"====ip:name: "+nm.getIp()+":"+name.substring(0, 3));
				if(alldiskalarmdata.containsKey(nm.getIp()+":"+name.substring(0, 3)+":"+bak)){//yangjun
					//SysLogger.info(host.getIpAddress()+"====存在 ip:name: "+nm.getIp()+":"+name.substring(0, 3));
					Diskconfig diskconfig = (Diskconfig)alldiskalarmdata.get(nm.getIp()+":"+name.substring(0, 3)+":"+bak);
					limenvalue = diskconfig.getLimenvalue();
					limenvalue1 = diskconfig.getLimenvalue1();
					limenvalue2 = diskconfig.getLimenvalue2();
					smsflag = diskconfig.getSms1();
					smsflag1 = diskconfig.getSms2();
					smsflag2 = diskconfig.getSms3();
					monflag = diskconfig.getMonflag();
					diskname=name.substring(0, 3);
				}
			}else{
				//SysLogger.info(host.getIpAddress()+"====ip:name: "+nm.getIp()+":"+name);
				if(alldiskalarmdata.containsKey(nm.getIp()+":"+name+":"+bak)){//yangjun
					//SysLogger.info(host.getIpAddress()+"====存在 ip:name: "+nm.getIp()+":"+name);
					Diskconfig diskconfig = (Diskconfig)alldiskalarmdata.get(nm.getIp()+":"+name+":"+bak);
					limenvalue = diskconfig.getLimenvalue();
					limenvalue1 = diskconfig.getLimenvalue1();
					limenvalue2 = diskconfig.getLimenvalue2();
					smsflag = diskconfig.getSms1();
					smsflag1 = diskconfig.getSms2();
					smsflag2 = diskconfig.getSms3();
					monflag = diskconfig.getMonflag();
					diskname=name;
				}
			}
			//SysLogger.info(host.getIpAddress()+"====存在 ip:name: "+nm.getIp()+":"+diskname+"     "+" value:"+thevalue+" "+limenvalue+" "+limenvalue1+" "+limenvalue2+" monflag:"+monflag);
			if(monflag ==0){
				//不需要告警判断
				return level;
			}else{
//				System.out.println(nm.getIp()+":"+name+":"+bak+"======="+thevalue);
				//进行告警级别判断
				if(thevalue >=limenvalue){
					//大于一级
					if(thevalue >=limenvalue1){
						//大于二级
						if(thevalue >=limenvalue2){
							//大于三级
							level = 3;
						}else{
							level = 2;
						}
					}else{
						level = 1;
					}
				}else{
					level =  0;
				}
			}
		
		}else{
			if(thevalue >=nm.getLimenvalue0()){
				//大于一级
				if(thevalue >=nm.getLimenvalue1()){
					//大于二级
					if(thevalue >=nm.getLimenvalue2()){
						//大于三级
						level = 3;
					}else{
						level = 2;
					}
				}else{
					level = 1;
				}
			}else{
				level =  0;
			}
		}
		//SysLogger.info(nm.getIp()+" "+subentity+" level ############## "+level);
		if(level == 0) return level;
		//SysLogger.info(nm.getIp()+" "+subentity+" level=="+level);
		if(this.alarmHash != null && this.alarmHash.size()>0){
			//判断是否已经含有该事件类型
			Hashtable subentityhash = null;
			if(subentity.equalsIgnoreCase("disk")){
				subentityhash = (Hashtable)this.alarmHash.get(subentity+":"+diskname);
			}else{
				subentityhash = (Hashtable)this.alarmHash.get(subentity);
			}
				
			if(subentityhash != null && subentityhash.size()>0){
				//含有该事件类型,判断是否是同级别告警
				//SysLogger.info(nm.getIp()+"已经判断过该"+subentity+"值");
				if(subentityhash.get(level) != null){
					//同级别存在,判断超过次数
					//SysLogger.info(nm.getIp()+"######已经判断过该"+subentity+"值");
					int times = (Integer)subentityhash.get(level);
					int limentimes = 0;
					if(level == 1){
						//第一级别
						limentimes = nm.getTime0();
					}else if (level == 2){
						//第二级别
						limentimes = nm.getTime1();
					}else{
						//第三级别
						limentimes = nm.getTime2();
					}
					if(times >= limentimes){
						//产生事件
						//flag = true;
						returnflag = level;
					}else{
						//不产生事件,但需要把告警产生次数加1
						subentityhash = new Hashtable();
						subentityhash.put(level, times+1);
						//SysLogger.info(nm.getIp()+" put====subentity:"+subentity+"===level:"+level);
						if(subentity.equalsIgnoreCase("disk")){
							this.alarmHash.put(subentity+":"+diskname, subentityhash);
							//SysLogger.info(nm.getIp()+" put====subentity:"+subentity+":"+diskname+"===level:"+level+"==times:"+(times+1));
						}else{
							this.alarmHash.put(subentity, subentityhash);
							//SysLogger.info(nm.getIp()+" put====subentity:"+subentity+"===level:"+level+"==times:"+(times+1));
						}
					}
					
				}else{
					//同级别不存在,则第一次产生该级别的告警,存入,同时清除该事件类型的其他级别告警
					//SysLogger.info(nm.getIp()+"同级别不存在该"+subentity+"值");
					//同级别存在,判断超过次数
					int times = 1;
					int limentimes = 0;
					if(level == 1){
						//第一级别
						limentimes = nm.getTime0();
					}else if (level == 2){
						//第二级别
						limentimes = nm.getTime1();
					}else{
						//第三级别
						limentimes = nm.getTime2();
					}
					if(times >= limentimes){
						//产生事件
						//flag = true;
						returnflag = level;
					}
					//this.alarmHash = new Hashtable();
					//Hashtable subentityhash = new Hashtable();
					subentityhash.put(level, 1);
					if(subentity.equalsIgnoreCase("disk")){
						this.alarmHash.put(subentity+":"+diskname, subentityhash);
						//SysLogger.info(nm.getIp()+" put====subentity:"+subentity+":"+diskname+"===level:"+level+"===times:"+1);
					}else{
						this.alarmHash.put(subentity, subentityhash);
						//SysLogger.info(nm.getIp()+" put====subentity:"+subentity+"===level:"+level+"===times:"+1);
					}
				}
			}else{
				//第一次
				//同级别存在,判断超过次数
				//SysLogger.info(nm.getIp()+"%%%%%%%%%%%第一次判断该"+subentity+"值");
				int times = 1;
				int limentimes = 0;
				if(level == 1){
					//第一级别
					limentimes = nm.getTime0();
				}else if (level == 2){
					//第二级别
					limentimes = nm.getTime1();
				}else{
					//第三级别
					limentimes = nm.getTime2();
				}
				if(times >= limentimes){
					//产生事件
					//flag = true;
					returnflag = level;
				}
				if(alarmHash == null)alarmHash = new Hashtable();
				//this.alarmHash = new Hashtable();
				subentityhash = new Hashtable();
				subentityhash.put(level, 1);
				if(subentity.equalsIgnoreCase("disk")){
					this.alarmHash.put(subentity+":"+diskname, subentityhash);
					//SysLogger.info(nm.getIp()+" put====subentity:"+subentity+":"+diskname+"===level:"+level+"===times:"+1);
				}else{
					this.alarmHash.put(subentity, subentityhash);
					//SysLogger.info(nm.getIp()+" put====subentity:"+subentity+"===level:"+level+"===times:"+1);
				}
			}
		}else{
			//第一次
			//SysLogger.info(nm.getIp()+"第一次判断该"+subentity+"值");
			//同级别存在,判断超过次数
			int times = 1;
			int limentimes = 0;
			if(level == 1){
				//第一级别
				limentimes = nm.getTime0();
			}else if (level == 2){
				//第二级别
				limentimes = nm.getTime1();
			}else{
				//第三级别
				limentimes = nm.getTime2();
			}
			if(times >= limentimes){
				//产生事件
				//flag = true;
				returnflag = level;
			}
			if(alarmHash == null)alarmHash = new Hashtable();
			//this.alarmHash = new Hashtable();
			Hashtable subentityhash = new Hashtable();
			subentityhash.put(level, 1);
			if(subentity.equalsIgnoreCase("disk")){
				this.alarmHash.put(subentity+":"+diskname, subentityhash);
				//SysLogger.info(nm.getIp()+" put====subentity:"+subentity+":"+diskname+"===level:"+level+"===times:"+1);
			}else{
				this.alarmHash.put(subentity, subentityhash);
				//SysLogger.info(nm.getIp()+" put====subentity:"+subentity+"===level:"+level+"===times:"+1);
			}
		}
		}catch(Exception e){
			e.printStackTrace();
		}
		return returnflag;
		
	}
	
//	public void createSMS(String subtype,String subentity,String ipaddress,String objid,String content,int flag,int checkday,int recover){
//	 	//建立短信		 	
//	 	//从内存里获得当前这个IP的PING的值
//	 	Calendar date=Calendar.getInstance();
//	 	Hashtable sendeddata = ShareData.getSendeddata();
//	 	Hashtable createeventdata = ShareData.getCreateEventdata();
//	 	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//	 	try{
// 			if (!sendeddata.containsKey(subtype+":"+subentity+":"+ipaddress)){
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
//				sendeddata.put(subtype+":"+subentity+":"+ipaddress,date);	
//				if(recover == 1){
//					if(subentity.equalsIgnoreCase("ping"))
//						createeventdata.put(subtype+":"+subentity+":"+ipaddress, date);
//				}
//				
// 			}else{
// 				//若在，则从已发送短信列表里判断是否已经发送当天的短信
// 				//yangjun
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
//					Calendar formerdate =(Calendar)sendeddata.get(subtype+":"+subentity+":"+ipaddress);		 				
//		 			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//		 			Date last = null;
//		 			Date current = null;
//		 			Calendar sendcalen = formerdate;
//		 			Date cc = sendcalen.getTime();
//		 			String tempsenddate = formatter.format(cc);
//		 			
//		 			Calendar currentcalen = date;
//		 			cc = currentcalen.getTime();
//		 			last = formatter.parse(tempsenddate);
//		 			String currentsenddate = formatter.format(cc);
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
//							sendeddata.put(subtype+":"+subentity+":"+ipaddress,date);
//							if(recover == 1){
//								if(subentity.equalsIgnoreCase("ping"))
//									createeventdata.put(subtype+":"+subentity+":"+ipaddress, date);
//							}
//				 		}
//		 			}else{
//		 				createEvent("poll",sysLocation,getBid(),content,flag,subtype,subentity,ipaddress,objid);
//		 				if(recover == 1){
//							if(subentity.equalsIgnoreCase("ping"))
//								createeventdata.put(subtype+":"+subentity+":"+ipaddress, date);
//						}
//		 				//createEvent("poll",sysLocation,getBid(),nm.getAlarmInfo()+"当前值:"+CEIString.round(new Double(memorydata.getThevalue()).doubleValue(),2)+" 阀值:"+nm.getLimenvalue0(),flag,"host","memory");
//		 			}
//				} else {
//                    //若不在，则建立短信，并且添加到发送列表里
//		 			Smscontent smscontent = new Smscontent();
//		 			String time = sdf.format(date.getTime());
//		 			smscontent.setLevel(flag+"");
//		 			smscontent.setObjid(objid);
//		 			smscontent.setMessage(content);
//		 			smscontent.setRecordtime(time);
//		 			smscontent.setSubtype(subtype);
//		 			smscontent.setSubentity(subentity);
//		 			smscontent.setIp(ipaddress);
//		 			//发送短信
//		 			SmscontentDao smsmanager=new SmscontentDao();
//		 			smsmanager.sendURLSmscontent(smscontent);	
//					sendeddata.put(subtype+":"+subentity+":"+ipaddress,date);	
//					if(recover == 1){
//						if(subentity.equalsIgnoreCase("ping"))
//							createeventdata.put(subtype+":"+subentity+":"+ipaddress, date);
//					}
// 				}
//				//
// 			}	 			 			 			 			 	
//	 	}catch(Exception e){
//	 		e.printStackTrace();
//	 	}
//	 }
	
	/**
	 * nielin add
	 * @date 2010-08-18
	 * @param ip
	 * @param proVector
	 */
	private List createProcessGroupEventList(String ip , Vector proVector){
		List retList = new ArrayList();
		if(proVector == null || proVector.size()==0)return retList;
		try {
			Node hostNode = PollingEngine.getInstance().getNodeByIP(ip);
			
			ProcessGroupConfigurationUtil processGroupConfigurationUtil = new ProcessGroupConfigurationUtil();
			List list = processGroupConfigurationUtil.getProcessGroupByIpAndMonFlag(ip, "1");
			
			if(list == null || list.size() ==0){
				return null;
			}
			
			
			for(int i = 0; i < list.size() ; i ++){
				ProcessGroup processGroup = (ProcessGroup)list.get(i);
				List processGroupConfigurationList = processGroupConfigurationUtil.getProcessGroupConfigurationByGroupId(String .valueOf(processGroup.getId()));
				
				if(processGroupConfigurationList == null || processGroupConfigurationList.size() ==0){
					continue;
				}
				
				List wrongList = new ArrayList();
				
				for(int j = 0 ; j < processGroupConfigurationList.size() ; j++){
					int num = 0;
					ProcessGroupConfiguration processGroupConfiguration = (ProcessGroupConfiguration)processGroupConfigurationList.get(j);
					for(int k = 0  ; k < proVector.size() ; k ++){
						Processcollectdata processdata = (Processcollectdata) proVector.elementAt(k);
						if("Name".equals(processdata.getEntity())){
							if(processGroupConfiguration.getName().trim().equals(processdata.getThevalue().trim())){
								num++;
							}
						}
						
						
					}
					
					int times = Integer.parseInt(processGroupConfiguration.getTimes());
					
					String status = processGroupConfiguration.getStatus();
					
					if("1".equals(status)){
						if(num > times){
							// 多出的个数
							num = num - times;
							
							List wrongProlist = new ArrayList();
							wrongProlist.add(processGroupConfiguration.getName());
							wrongProlist.add(num);
							wrongProlist.add(status);
							
							wrongList.add(wrongProlist);
							
						}
					}else{
						if(num < times){
							// 丢失的个数
							num = times - num;
							
							List wrongProlist = new ArrayList();
							wrongProlist.add(processGroupConfiguration.getName());
							wrongProlist.add(num);
							wrongProlist.add(status);
							
							wrongList.add(wrongProlist);
							
						}
					}
					
					
					
				}
				
				if(wrongList.size() > 0){
					String message = ip + " 进程组为：" + processGroup.getName() + " 出现进程异常!";
					for(int j = 0 ; j < wrongList.size() ; j ++){
						List wrongProList = (List)wrongList.get(j);
						String status = (String)wrongProList.get(2);
						if("1".equals(status)){
							message = message + "进程：" + wrongProList.get(0) + "超出个数为：" + wrongProList.get(1) + ";";
						}else {
							message = message + "进程：" + wrongProList.get(0) + "丢失个数为：" + wrongProList.get(1) + ";";
						}
						
					
					}
					
					EventList eventList = new EventList();
					eventList.setEventtype("poll");
					eventList.setEventlocation(hostNode.getAlias() + "(" + ip + ")");
					eventList.setContent(message);
					eventList.setLevel1(Integer.valueOf(processGroup.getAlarm_level()));
					eventList.setManagesign(0);
					eventList.setRecordtime(Calendar.getInstance());
					eventList.setReportman("系统轮询");
					eventList.setNodeid(hostNode.getId());
					eventList.setBusinessid(hostNode.getBid());
					eventList.setSubtype("host");
					eventList.setSubentity("proc");
					
					retList.add(eventList);
					
//					hostNode = PollingEngine.getInstance().getNodeByID(hostNode.getId());
//					hostNode.setAlarm(true);
//					hostNode.setAlarmlevel(3);
//					hostNode.getAlarmMessage().add(message);
					
					/*
					 * 需要增加发送短信的功能
					 */
//					EventListDao eventListDao = new EventListDao();
//					try {
//						eventListDao.save(eventList);
//					} catch (RuntimeException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} finally{
//						eventListDao.close();
//					}
					
//					/*
//					 * 在这里只检测,不做告警,告警的工作在采集任务里做
//					 */
//					try{
//						createSMS(eventList.getSubtype(), eventList.getSubentity(),ip , hostNode.getId() + "", message , eventList.getLevel1() , 1 , processGroup.getName() , eventList.getBusinessid(),hostNode.getAlias() + "(" + ip + ")");
//					}catch(Exception e){
//						
//					}
					
				}
				
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retList;
	}
	
	
	
	public List createHostServiceGroupEventList(String ip , Vector hostServiceVector){
		List returnList = new ArrayList();
		if(hostServiceVector == null || hostServiceVector.size()==0)return returnList;
		
		try {
			
			Node hostNode = PollingEngine.getInstance().getNodeByIP(ip);
			
			HostServiceGroupConfigurationUtil hostServiceGroupConfigurationUtil = new HostServiceGroupConfigurationUtil();
			List list = hostServiceGroupConfigurationUtil.gethostservicegroupByIpAndMonFlag(ip, "1");
			
			if(list == null || list.size() == 0){
				return returnList;
			}
			
			for(int i = 0 ; i < list.size() ; i++){
				HostServiceGroup hostServiceGroup = (HostServiceGroup)list.get(i);
				List hostServiceList = hostServiceGroupConfigurationUtil.gethostservicegroupConfigurationByGroupId(String.valueOf(hostServiceGroup.getId()));
				
				if(hostServiceList == null || hostServiceList.size() ==0){
					continue;
				}
				
				List wrongList = new ArrayList();
				
				
				for(int j = 0 ; j < hostServiceList.size() ; j++){
					HostServiceGroupConfiguration hostServiceGroupConfiguration = (HostServiceGroupConfiguration) hostServiceList.get(j);
					
					boolean isLived = false;
					
					if(hostServiceVector != null){
						for(int k = 0 ; k < hostServiceVector.size() ; k++){
							Servicecollectdata servicedata = (Servicecollectdata)hostServiceVector.get(k);
							if(hostServiceGroupConfiguration.getName().trim().equals(servicedata.getName())){
								isLived = true;
								break;
							}
						}
					}
					
					if(!isLived){
						wrongList.add(hostServiceGroupConfiguration);
					}
				}
				
				if(wrongList.size() > 0){
					String message = ip + " 主机服务组为：" + hostServiceGroup.getName() + " 出现主机服务丢失!";
					for(int j = 0 ; j < wrongList.size() ; j ++){
						HostServiceGroupConfiguration hostServiceGroupConfiguration = (HostServiceGroupConfiguration)wrongList.get(j);
						message = message + "主机服务：" + hostServiceGroupConfiguration.getName() + "丢失;";
					
					}
					EventList eventList = new EventList();
					eventList.setEventtype("poll");
					eventList.setEventlocation(hostNode.getAlias() + "(" + ip + ")");
					eventList.setContent(message);
					eventList.setLevel1(Integer.parseInt(hostServiceGroup.getAlarm_level()));
					eventList.setManagesign(0);
					eventList.setRecordtime(Calendar.getInstance());
					eventList.setReportman("系统轮询");
					eventList.setNodeid(hostNode.getId());
					eventList.setBusinessid(hostNode.getBid());
					eventList.setSubtype("host");
					eventList.setSubentity("hostservice");
					
//				hostNode = PollingEngine.getInstance().getNodeByID(hostNode.getId());
//				hostNode.setAlarm(true);
//				hostNode.setAlarmlevel(3);
//				hostNode.getAlarmMessage().add(message);
					
					/*
					 * 需要增加发送短信的功能
					 */
//				EventListDao eventListDao = new EventListDao();
//				try {
//					eventListDao.save(eventList);
//				} catch (RuntimeException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} finally{
//					eventListDao.close();
//				}
					
					//System.out.println(message+ "===================================");
					
					
					returnList.add(eventList);
					
//					/*
//					 * 这里只检测,告警的工作在相关的采集任务里完成
//					 */
//					try{
//						createSMS(eventList.getSubtype(), eventList.getSubentity(),ip , hostNode.getId() + "", message , eventList.getLevel1() , 1 , hostServiceGroup.getName() , eventList.getBusinessid(),hostNode.getAlias() + "(" + ip + ")");
//					}catch(Exception e){
//						
//					}
				}
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return returnList;
	}
	
	
//	public void createSMS(String subtype,String subentity,String ipaddress,String objid,String content,int flag,int checkday,String sIndex,String bids,String sysLocation){
//	 	//建立短信		 	
//	 	//从内存里获得当前这个IP的PING的值
//	 	Calendar date=Calendar.getInstance();
//	 	Hashtable sendeddata = ShareData.getSendeddata();
//	 	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//	 	System.out.println("端口事件--------------------");
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
//			 	            //String sysLocation = "";
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
	
	private void createEvent(String eventtype,String eventlocation,String bid,String content,int level1,String subtype,String subentity,String ipaddress,String objid){
		//生成事件
//		SysLogger.info("##############开始生成事件############");
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
		EventListDao eventlistdao = new EventListDao();
		try{
			eventlistdao.save(eventlist);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			eventlistdao.close();
		}
	}
	
	public void setShowmessage(String showmessage) {
		this.showmessage = showmessage;
	}
	
	/**
	 * 创建as400告警
	 * @param ip
	 * @param hostServiceVector
	 * @return
	 */
	public List createJobForAS400GroupEventList(String ip , List jobForAS400list){
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
						
//				hostNode = PollingEngine.getInstance().getNodeByID(hostNode.getId());
//				hostNode.setAlarm(true);
//				hostNode.setAlarmlevel(3);
//				hostNode.getAlarmMessage().add(message);
						
						/*
						 * 需要增加发送短信的功能
						 */
//				EventListDao eventListDao = new EventListDao();
//				try {
//					eventListDao.save(eventList);
//				} catch (RuntimeException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} finally{
//					eventListDao.close();
//				}
						
						//System.out.println(message+ "===================================");
						
						
//						returnList.add(eventList);
//						try{
//							createSMS(eventList.getSubtype(), eventList.getSubentity(),ip , hostNode.getId() + "", message , eventList.getLevel1() , 1 , jobForAS400Group.getName() , eventList.getBusinessid(),hostNode.getAlias() + "(" + ip + ")" + " 作业组为：" + jobForAS400Group.getName());
//						}catch(Exception e){
//							
//						}
					}
				} catch (RuntimeException e) {
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

	public int getSecuritylevel() {
		return securitylevel;
	}

	public void setSecuritylevel(int securitylevel) {
		this.securitylevel = securitylevel;
	}

	public String getSecurityName() {
		return securityName;
	}

	public void setSecurityName(String securityName) {
		this.securityName = securityName;
	}

	public int getV3_ap() {
		return v3_ap;
	}

	public void setV3_ap(int v3_ap) {
		this.v3_ap = v3_ap;
	}

	public String getAuthpassphrase() {
		return authpassphrase;
	}

	public void setAuthpassphrase(String authpassphrase) {
		this.authpassphrase = authpassphrase;
	}

	public int getV3_privacy() {
		return v3_privacy;
	}

	public void setV3_privacy(int v3_privacy) {
		this.v3_privacy = v3_privacy;
	}

	public String getPrivacyPassphrase() {
		return privacyPassphrase;
	}

	public void setPrivacyPassphrase(String privacyPassphrase) {
		this.privacyPassphrase = privacyPassphrase;
	}

	public int getLayer() {
		return layer;
	}

	public void setLayer(int layer) {
		this.layer = layer;
	}
}

