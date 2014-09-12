/*
 * Created on 2005-7-4
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.afunms.initialize;

import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.PDU;
import org.snmp4j.PDUv1;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.config.dao.PortconfigDao;
import com.afunms.config.model.IpAlias;
import com.afunms.config.model.Portconfig;
import com.afunms.event.dao.AlarmInfoDao;
import com.afunms.event.dao.SmscontentDao;
import com.afunms.event.model.AlarmInfo;
import com.afunms.event.model.Smscontent;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.system.dao.TimeShareConfigDao;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SnmpTrapsListener {
	private static SnmpTrapsListener instance = null;
	final static int RECEIVE_PORT = 162;
	final static int SYSLOG_PORT = 3072;
	private Snmp snmp = null;
	private Snmp syslogsnmp = null;
	private CommandResponder trapPrinter = null;
	private TransportMapping transport = null;
	public static synchronized SnmpTrapsListener getInstance() {
		if (instance == null)
			instance = new SnmpTrapsListener();
		return instance;
	}
	/**声明全局变量 用于发送短信时的新线程 */
	public String upOrDownStr = "";
	public Portconfig Sendportconfig ;
	public String Sendtime;
	public Host Sendhost;
	public static Hashtable Sendht;
	/**声明end*/
	public void syslogListen(){
//		try {
//			//ExecuteCollectSyslog ecs=new ExecuteCollectSyslog();
//			//ecs.start();
//		} catch (Exception e) {
//			System.out.println("Syslog采集任务启动不成功！");
//			e.printStackTrace();
//		}
	}
	public void listen() {
		
		try {
			UdpAddress udpAddress = new UdpAddress(RECEIVE_PORT);
			transport = new DefaultUdpTransportMapping(udpAddress);
			snmp = new Snmp(transport);
			transport.listen();
			SysLogger.info("===========================================");
			SysLogger.info("Snmp Listener start at port " + RECEIVE_PORT);
			SysLogger.info("===========================================");
			trapPrinter = new CommandResponder() {
				public synchronized void processPdu(CommandResponderEvent e) {
					String trapType =e.getPDU().getTypeString(e.getPDU().getType());		
//					SysLogger.info("===traptpye ===="+trapType);					
					if (trapType.equals("V1TRAP")) {						
						PDUv1 command = (PDUv1) e.getPDU();
						if (command != null) {
							try{															
//								SysLogger.info(e.getPeerAddress().toString());							
//								SysLogger.info(command.toString());
//								SysLogger.info("=====Enterprise oid:"+command.getEnterprise());
								java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM-dd HH:mm:ss");								 											 			
					 			Calendar date=Calendar.getInstance();
					 			String time = sdf.format(date.getTime());								
					 			String address = e.getPeerAddress().toString();	
					 			
					 			Vector v = command.getVariableBindings();	
//					 			SysLogger.info("v size ============= "+v.size());
					 			String ttaddress = address.substring(0, address.indexOf("/"));
					 			Host host = (Host)PollingEngine.getInstance().getNodeByIP(ttaddress);
					 			
					 			if(host == null){
					 				if(ShareData.getAllipaliasVSip() != null){
					 						if(ShareData.getAllipaliasVSip().containsKey(ttaddress)){
					 							String _ipaddress = (String)ShareData.getAllipaliasVSip().get(ttaddress);
					 							ttaddress = _ipaddress;
					 							try {
					 								host = (Host) PollingEngine.getInstance().getNodeByIP(_ipaddress);
					 							} catch (Exception ex) {
					 								ex.printStackTrace();
					 							}
					 						}
					 				}
					 			}
					 			
//					 			Hashtable allipaliasHash = ShareData.getAllipalias();
//					 			
//					 			if(host == null){
//					 				//AlarmInfoDao alermdao = new AlarmInfoDao();
//					 				if(allipaliasHash != null && allipaliasHash.size()>0){
//						 				Enumeration ips = allipaliasHash.keys();
//						 				int flag = 0;
//						 				//allipaliasHash
//						 				while (ips.hasMoreElements()) {
//
//											List iplist = (List) ips.nextElement();
//											if(iplist != null && iplist.size()>0){
//												for(int k=0;k<iplist.size();k++){
//													IpAlias vo = (IpAlias)iplist.get(k);
//													if(vo.getAliasip().equalsIgnoreCase(ttaddress)){
//														String ipchange = vo.getIpaddress();
//											 			host = (Host)PollingEngine.getInstance().getNodeByIP(ipchange);
//											 			ttaddress = ipchange;
//											 			flag = 1;
//											 			break;
//													}
//												}
//												
//											}
//											if(flag == 1)break;
//						 				}
//					 				}
//
//					 			}
					 			//ttaddress = ipchange;
					 			if(host == null){
					 				return;
					 			}
					 			
					 			
					 			
//								TrapOIDConfigDao trapdao = new TrapOIDConfigDao();
//								List oidlist = trapdao.loadByEnterpriseOID(command.getEnterprise().toString());
//								//没有配置TRAP OID
//								if(oidlist == null || oidlist.size()==0){
//									return;
//								}else{
//									if(v.size() != oidlist.size()){
//										//没有生成相应的OID数目
//										return;
//									}else{
//										int flag = 0;
//										int mixflag = 0;
//										String mixvalue="";
//										String index = "";
//										TrapOIDConfig transoidconfig = new TrapOIDConfig();
//										for(int i=0;i<oidlist.size();i++){
//											if(i==0){
//												mixvalue=((VariableBinding)v.get(0)).getVariable().toString().trim();
//											}
//											TrapOIDConfig oidconfig = (TrapOIDConfig)oidlist.get(i);
//											if(oidconfig.getTransflag() == 1)transoidconfig = oidconfig;
//											if(!oidconfig.getOid().equalsIgnoreCase(((VariableBinding)v.get(i)).getOid().toString())){
//												if(mixvalue != null && mixvalue.trim().length()>0){
//													if(!(oidconfig.getOid()+"."+mixvalue).equalsIgnoreCase(((VariableBinding)v.get(i)).getOid().toString())){
//														flag = 1;
//													}else{
//														mixflag = 1;
//													}
//												}else{
//													flag = 1;
//												}
//												//flag = 1;
//											}
//										}
//										if(flag == 1){
//											return;
//										}else{
//											String traptransvalue = "";
//											String value = ((VariableBinding)v.get(transoidconfig.getOrders()-1)).getVariable().toString().trim();
//											if(transoidconfig.getValue1().equalsIgnoreCase(value)){
//												traptransvalue = transoidconfig.getTransvalue1();
//											}else if(transoidconfig.getValue2().equalsIgnoreCase(value)){
//												traptransvalue = transoidconfig.getTransvalue2();
//											}else
//												return;
//											String firststr = "";
//											for(int i=0;i<oidlist.size();i++){
//												TrapOIDConfig oidconfig = (TrapOIDConfig)oidlist.get(i);
//												if(oidconfig.getDesc().equalsIgnoreCase("index")){
//													if(mixflag == 1){
//														index = mixvalue;
//													}else
//														index = ((VariableBinding)v.get(oidconfig.getOrders()-1)).getVariable().toString().trim();
//												}
//												if(1 != oidconfig.getTransflag() || 0 != oidconfig.getTransflag()){
//													//为2的时候需要写到TRAP告警字符串里
//													firststr=firststr+oidconfig.getValue1()+((VariableBinding)v.get(oidconfig.getOrders()-1)).getVariable().toString().trim()+" ";
//													
//												}
//											}
//											traptransvalue = firststr+traptransvalue;
//											SysLogger.info("========================================="+index);
//											SysLogger.info(traptransvalue);
//											SysLogger.info("========================================="+transoidconfig.getTraptype());
//											if("port".equalsIgnoreCase(transoidconfig.getTraptype())){
//												PortconfigDao configdao = new PortconfigDao(); 	
//												SysLogger.info("--------------------------------------------");
//					 							Portconfig portconfig = configdao.getByipandindex(ttaddress,index);
//					 							if (portconfig != null){
//					 								SysLogger.info("--------------------------------------------");
//				 									if (portconfig.getSms().intValue()==1){
//				 										SysLogger.info("--------------------------------------------2");
//				 										//Thread one = new Thread();
//				 										//one.sleep(10000);
//				 										//SysLogger.info("#############");
//				 										//SysLogger.info(traptransvalue+"=================");
//				 										//createTask(host, portconfig, traptransvalue);
//				 										//SmscontentDao dao = new SmscontentDao();
//				 										//dao.sendSms(time,host, portconfig, traptransvalue);
//				 										//smscontent.setMessage(time+"&"+monitorip.getEquipname()+"&"+ttaddress+"&"+portconfig.getName()+"&"+portconfig.getLinkuse()+"&"+upOrDown+"&level=2");
//				 										//发送短信
//				 										//Vector tosend = new Vector();
//				 										//tosend.add(smscontent);											 			
//				 										//smsmanager.sendSmscontent(tosend);	
//				 									}
//				 								}
//											}
//											
//				 							
//										}
//									}
//								}
								
					 			
//					 			SysLogger.info("getOid() "+((VariableBinding) v.get(1)).getOid().toString());
					 			//begin 处理端口是否DOWN
					 			if (v!=null && v.size()>1){
					 			if (((VariableBinding) v.get(1))
									.getOid()
									.toString()
									.startsWith("1.3.6.1.6.3.1.1.4.1.0")) {
									//.startsWith("1.3.6.1")) {	
					 				if (v.size() >= 2) {											
					 					try{
					 						String upOrDown = "";//1.3.6.1.6.3.1.1.5.4 up 1.3.6.1.6.3.1.1.5.3 down
					 						VariableBinding vb = (VariableBinding) v.get(1);
					 						//VariableBinding vb2 = (VariableBinding) v.get(2);
					 						//VariableBinding vb3 = (VariableBinding) v.get(3);
					 						//SysLogger.info("V1TRAP "+host+"   vb   ==="+vb.getVariable().toString());
					 						//SysLogger.info("V1TRAP "+host+"    vb2  ==="+vb2.getVariable().toString());
					 						if (vb.getVariable().toString().startsWith("1.3.6.1.6.3.1.1.5.1")){
					 							upOrDown = "ColdStart";//coldStart
					 							//Smscontent smscontent = new Smscontent();
					 							//smscontent.setMessage(time+"&"+monitorip.getEquipname()+"&"+ttaddress+"&"+upOrDown+"&level=2");
					 							//发送短信
					 							Vector tosend = new Vector();
					 							//tosend.add(smscontent);										 			
					 							//smsmanager.sendSmscontent(tosend);
					 						}else if (vb.getVariable().toString().startsWith("1.3.6.1.6.3.1.1.5.2")){
					 							upOrDown = "WarmStart";//coldStart
												//Smscontent smscontent = new Smscontent();
										 		//smscontent.setMessage(time+"&"+monitorip.getEquipname()+"&"+ttaddress+"&"+upOrDown+"&level=2");
										 		//发送短信
										 		Vector tosend = new Vector();
										 		//tosend.add(smscontent);											 			
										 		//smsmanager.sendSmscontent(tosend);	
											  
					 						}else if (vb.getVariable().toString().startsWith("1.3.6.1.4.1.52.2501.10.2.1")){
					 							upOrDown = "PS-failure";//PS-failure
					 							//Smscontent smscontent = new Smscontent();
					 							//smscontent.setMessage(time+"&"+monitorip.getEquipname()+"&"+ttaddress+"&"+upOrDown+"&level=2");
					 							//发送短信
					 							Vector tosend = new Vector();
					 							//tosend.add(smscontent);											 			
					 							//smsmanager.sendSmscontent(tosend);
								 			
					 						}else if (vb.getVariable().toString().startsWith("1.3.6.1.4.1.52.2501.10.2.6")){
					 							upOrDown = "reboot";//PS-failure
					 							//Smscontent smscontent = new Smscontent();
					 							//smscontent.setMessage(time+"&"+monitorip.getEquipname()+"&"+ttaddress+"&"+upOrDown+"&level=2");
					 							//发送短信
					 							Vector tosend = new Vector();
					 							//tosend.add(smscontent);											 			
					 							//smsmanager.sendSmscontent(tosend);	
								 			
					 						}else if (vb.getVariable().toString().startsWith("1.3.6.1.4.1.52.2501.10.2.2")){
					 							upOrDown = "PS-recover";//PS-recover	
					 							//Smscontent smscontent = new Smscontent();
					 							//smscontent.setMessage(time+"&"+monitorip.getEquipname()+"&"+ttaddress+"&"+upOrDown+"&level=2");
					 							//发送短信
					 							Vector tosend = new Vector();
					 							//tosend.add(smscontent);											 			
					 							//smsmanager.sendSmscontent(tosend);
					 						}else{	
					 							if (vb.getVariable().toString().startsWith("1.3.6.1.6.3.1.1.5.4")){
					 								upOrDown = "UP";//up
					 							}else if (vb.getVariable().toString().startsWith("1.3.6.1.6.3.1.1.5.3")){
					 								upOrDown = "DOWN";//down
					 							}else if (vb.getVariable().toString().startsWith("1.3.6.1.2.1.11.0.3")){
					 								upOrDown = "UP";//down											
					 							}else if (vb.getVariable().toString().startsWith("1.3.6.1.2.1.11.0.2")){
					 								upOrDown = "DOWN";//down
					 							}else{
					 								return;
					 							}
					 							vb=(VariableBinding) v.get(2);
					 							String portIndex = vb.getVariable().toString();
					 							//Smscontent smscontent = new Smscontent();
					 							SysLogger.info("未经过短信配置前的TRAP信息:  地址为"+ttaddress+"的第"+portIndex+"号端口状态发生改变,值为"+upOrDown);
					 							//判断是否包含配置端口
					 							Portconfig portconfig = null;
					 							if(ShareData.getAllportconfigs() != null){
					 								if(ShareData.getAllportconfigs().containsKey(ttaddress+":"+portIndex)){
						 								portconfig = (Portconfig)ShareData.getAllportconfigs().get(ttaddress+":"+portIndex);
						 							}
												}
					 							
//					 							PortconfigDao configdao = new PortconfigDao(); 			
//					 							Portconfig portconfig = configdao.getByipandindex(ttaddress,portIndex);
							 											 			
					 							if (upOrDown != null && upOrDown.trim().length()>0){												
					 								if (portconfig != null){
					 									if (portconfig.getSms().intValue()==1){
					 										SmscontentDao dao = new SmscontentDao();
					 										dao.sendSms(time,host, portconfig, upOrDown);
					 										//开始发送短信
					 										//赋当前UP OR DOWN值
					 										//判断DOWN时
					 										
					 										upOrDownStr = upOrDown.toUpperCase();
					 										
					 										boolean f = false;
					 										if(!Sendht.containsKey(ttaddress+portIndex)){
					 											Sendht.put(ttaddress+portIndex, upOrDownStr);
					 											f = true;
					 										}
					 										
					 										String nn = Sendht.get(ttaddress+portIndex).equals("UP")?"DOWN":"UP";
					 										Sendht.remove(ttaddress+portIndex);
					 										Sendht.put(ttaddress+portIndex, nn);
					 										
					 										upOrDownStr = ttaddress+portIndex;
					 										
					 										if(f && nn.equals("DOWN")){
					 											//赋值给全局变量 用于新线程
					 											Sendhost = host;
					 											Sendportconfig = portconfig;
					 											Sendtime = time;
					 											//启动发送短信线程
					 											Thread t1 =  new Thread(new SnmpTrapThread(Sendht,upOrDownStr));
					 											t1.start();
					 										}
					 									}
					 								}
					 							}
										
					 						}
					 					} catch(Exception ex){
											
										}
									}//end for v.size()>2
								}
							}
							
							
					 			if (v!=null && v.size()>1){
									//begin 处理端口是否DOWN
					 				//H3C设备
									if (((VariableBinding) v.get(0))
											.getOid()
											.toString()
											.startsWith("1.3.6.1.2.1.2.2.1.1")) {
											if (v.size() == 3) {
												int flag = 0;
												try{
												String upOrDown = "";//1.3.6.1.6.3.1.1.5.4 up 1.3.6.1.6.3.1.1.5.3 down
												VariableBinding vb = (VariableBinding) v.get(0);
												//vb[0]是portindex vb[1]是端口描述 vb[3]是端口状态改变的值
												//vb=(VariableBinding) v.get(0);
												String portIndex = vb.getVariable()+"";
												
												vb=(VariableBinding) v.get(2);//OperateStatus
												upOrDown = (vb.getVariable()+"").trim();
												if(upOrDown.equalsIgnoreCase("1")){
													upOrDown = "up";
												}else if(upOrDown.equalsIgnoreCase("2")){
													upOrDown = "down";
												}else{
													flag = 1;
												}
												if(flag == 0){
													SysLogger.info("未经过短信配置前的TRAP信息:  地址为"+ttaddress+"的第"+portIndex+"号端口状态发生改变,值为"+upOrDown);
													//PortconfigDao configdao = new PortconfigDao(); 			
						 							//Portconfig portconfig = configdao.getByipandindex(ttaddress,portIndex);
										 				
													//判断是否包含配置端口
													Portconfig portconfig = null;
						 							if(ShareData.getAllportconfigs() != null){
						 								if(ShareData.getAllportconfigs().containsKey(ttaddress+":"+portIndex)){
							 								portconfig = (Portconfig)ShareData.getAllportconfigs().get(ttaddress+":"+portIndex);
							 							}
													}
						 							
													if (upOrDown != null && upOrDown.trim().length()>0){												
										 			if (portconfig != null){
										 				if (portconfig.getSms().intValue()==1){
										 					SmscontentDao dao = new SmscontentDao();
					 										dao.sendSms(time,host, portconfig, upOrDown);
					 										
					 										upOrDownStr = upOrDown.toUpperCase();
					 										
					 										boolean f = false;
					 										if(!Sendht.containsKey(ttaddress+portIndex)){
					 											Sendht.put(ttaddress+portIndex, upOrDownStr);
					 											f = true;
					 										}
					 										
					 										String nn = Sendht.get(ttaddress+portIndex).equals("UP")?"DOWN":"UP";
					 										Sendht.remove(ttaddress+portIndex);
					 										Sendht.put(ttaddress+portIndex, nn);
					 										
					 										upOrDownStr = ttaddress+portIndex;
					 										
					 										if(f && nn.equals("DOWN")){
					 											//赋值给全局变量 用于新线程
					 											Sendhost = host;
					 											Sendportconfig = portconfig;
					 											Sendtime = time;
					 											//启动发送短信线程
					 											Thread t1 =  new Thread(new SnmpTrapThread(Sendht,upOrDownStr));
					 											t1.start();
					 										}	
										 				}
										 			}
													}
												}
												
												
												//}
												
												
												}catch(Exception ex){
													
												}
											} //end for v.size()>2
											
											if (v.size() == 4) {											
												try{
												String upOrDown = "";//1.3.6.1.6.3.1.1.5.4 up 1.3.6.1.6.3.1.1.5.3 down
												VariableBinding vb = (VariableBinding) v.get(0);
												//vb[0]是portindex vb[1]是端口描述 vb[2]是端口类型 vb[3]是端口状态改变的值
												String portIndex = vb.getVariable()+"";
												
												vb=(VariableBinding) v.get(1);//端口描述
												String portDesc = (vb.getVariable()+"").trim();
												
												vb=(VariableBinding) v.get(3);//原因
												String locIfReason = (vb.getVariable()+"").trim();
												
												if(locIfReason.contains("Keepalive OK")){
													upOrDown = "up";
												}else if(locIfReason.contains("Keepalive failed")){
													upOrDown = "down";
												}else if(locIfReason.contains("up")){
													upOrDown = "up";
												}else if(locIfReason.contains("down")){
													upOrDown = "down";
												}
												
												SysLogger.info("未经过短信配置前的TRAP信息:  地址为"+ttaddress+"的第"+portIndex+"号端口状态发生改变,值为"+upOrDown);
												//PortconfigDao configdao = new PortconfigDao(); 			
					 							//Portconfig portconfig = configdao.getByipandindex(ttaddress,portIndex);
												//判断是否包含配置端口
												Portconfig portconfig = null;
					 							if(ShareData.getAllportconfigs() != null){
					 								if(ShareData.getAllportconfigs().containsKey(ttaddress+":"+portIndex)){
						 								portconfig = (Portconfig)ShareData.getAllportconfigs().get(ttaddress+":"+portIndex);
						 							}
												}
									 											 			
												if (upOrDown != null && upOrDown.trim().length()>0){												
									 			if (portconfig != null){
									 				if (portconfig.getSms().intValue()==1){
									 					SmscontentDao dao = new SmscontentDao();
				 										dao.sendSms(time,host, portconfig, upOrDown);
				 										
				 										upOrDownStr = upOrDown.toUpperCase();
				 										
				 										boolean f = false;
				 										if(!Sendht.containsKey(ttaddress+portIndex)){
				 											Sendht.put(ttaddress+portIndex, upOrDownStr);
				 											f = true;
				 										}
				 										
				 										String nn = Sendht.get(ttaddress+portIndex).equals("UP")?"DOWN":"UP";
				 										Sendht.remove(ttaddress+portIndex);
				 										Sendht.put(ttaddress+portIndex, nn);
				 										
				 										upOrDownStr = ttaddress+portIndex;
				 										
				 										if(f && nn.equals("DOWN")){
				 											//赋值给全局变量 用于新线程
				 											Sendhost = host;
				 											Sendportconfig = portconfig;
				 											Sendtime = time;
				 											//启动发送短信线程
				 											Thread t1 =  new Thread(new SnmpTrapThread(Sendht,upOrDownStr));
				 											t1.start();
				 										}	
									 				}
									 			}
												}
												
												//}
												
												
												}catch(Exception ex){
													
												}
											}
										}																
									//end 开始处理端口是否DOWN
									
									}							
							
							
							
							
						}catch(Exception iep){
							iep.printStackTrace();
						}							
						}//end command null
						
					}	

					
					if (trapType.equals("TRAP")) {
						PDU command = (PDU)e.getPDU();
						if (command != null) {
							try{								
								String address = e.getPeerAddress().toString();		
//								SysLogger.info(address);	
//								SysLogger.info(command.toString());
								
								Vector v = command.getVariableBindings();																																		
//								SysLogger.info("=====Enterprise oid::"+((VariableBinding) v.get(1))
//										.getOid()
//										.toString());
								String ttaddress = address.substring(0, address.indexOf("/"));
								
								//System.out.println("======ttaddress====="+ttaddress);
					 			Host host = (Host)PollingEngine.getInstance().getNodeByIP(ttaddress);
					 			if (host == null) return;
					 			
					 			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM-dd HH:mm:ss");								 											 			
					 			Calendar date=Calendar.getInstance();
					 			
					 			String time = sdf.format(date.getTime());
					 			//Calendar pdate = sdf.parse(time);
								/*
								I_MonitorIpList monitormanager=new MonitoriplistManager();
								I_Smscontent smsmanager=new SmscontentManager();							
								Monitoriplist monitorip = (Monitoriplist)monitormanager.getByIpaddress(ttaddress);
								if (!ttaddress.equals("10.217.252.234")&&!ttaddress.equals("10.216.1.252")){
					 			if (monitorip == null ||monitorip.getEquipname() == null ) {
									I_PortRelation pManager = new PortRelationManager();								 			
						 			PortRelation portRelate = new PortRelation();
						 			//if (portconfig == null){
						 				//从关联IP里查找					 			
						 				portRelate = pManager.getByrelateip(ttaddress);
						 				if (portRelate == null)return;
						 				String ipAddress = portRelate.getIpaddress();
						 				monitorip = (Monitoriplist)monitormanager.getByIpaddress(ipAddress);	
						 				if (monitorip == null)return;
						 				ttaddress = ipAddress;
						 			//}				 				
					 				//return ;
					 			}
								}
								*/
				
								 			
									//begin 处理端口是否DOWN
									if (((VariableBinding) v.get(1))
											.getOid()
											.toString()
											.startsWith("1.3.6.1.6.3.1.1.4.1.0")) {									
											if (v.size() >= 2) {											
												try{
													String upOrDown = "";//1.3.6.1.6.3.1.1.5.4 up 1.3.6.1.6.3.1.1.5.3 down
													VariableBinding vb = (VariableBinding) v.get(1);
													String cancelStr = "";
													if(v.size() == 6){
														VariableBinding _vb = (VariableBinding) v.get(5);
														cancelStr = _vb.getVariable()+"";
													}
													if(cancelStr != null && cancelStr.trim().length()>0){
														if(cancelStr.indexOf("Keepalive")>=0)return;
													}
//													SysLogger.info("TRAP "+host+"      ==="+vb.getVariable().toString());
													if ((vb.getVariable()+"").startsWith("1.3.6.1.6.3.1.1.5.1")){
														upOrDown = "ColdStart";//coldStart
														//Smscontent smscontent = new Smscontent();
														//smscontent.setMessage(time+"&"+monitorip.getEquipname()+"&"+ttaddress+"&"+upOrDown+"&level=2");
														//发送短信
														Vector tosend = new Vector();
														//tosend.add(smscontent);	
														//System.out.println(smscontent.getMessage());										 			
														//smsmanager.sendSmscontent(tosend);
													}else if ((vb.getVariable()+"").startsWith("1.3.6.1.6.3.1.1.5.2")){
														upOrDown = "WarmStart";//coldStart
														//Smscontent smscontent = new Smscontent();
												 		//smscontent.setMessage(time+"&"+monitorip.getEquipname()+"&"+ttaddress+"&"+upOrDown+"&level=2");
												 		//发送短信
												 		Vector tosend = new Vector();
												 		//tosend.add(smscontent);	
												 		//System.out.println(smscontent.getMessage());										 			
												 		//smsmanager.sendSmscontent(tosend);	
													  
													}else if ((vb.getVariable()+"").startsWith("1.3.6.1.4.1.52.2501.10.2.1")){
														upOrDown = "PS-failure";//PS-failure
														//Smscontent smscontent = new Smscontent();
														//smscontent.setMessage(time+"&"+monitorip.getEquipname()+"&"+ttaddress+"&"+upOrDown+"&level=2");
														//发送短信
														Vector tosend = new Vector();
														//tosend.add(smscontent);	
														//System.out.println(smscontent.getMessage());										 			
														//smsmanager.sendSmscontent(tosend);
										 			
													}else if ((vb.getVariable()+"").startsWith("1.3.6.1.4.1.52.2501.10.2.6")){
														upOrDown = "reboot";//PS-failure
														//Smscontent smscontent = new Smscontent();
														//smscontent.setMessage(time+"&"+monitorip.getEquipname()+"&"+ttaddress+"&"+upOrDown+"&level=2");
														//发送短信
														Vector tosend = new Vector();
														//tosend.add(smscontent);	
														//System.out.println(smscontent.getMessage());										 			
														//smsmanager.sendSmscontent(tosend);	
										 			
												}else if ((vb.getVariable()+"").startsWith("1.3.6.1.4.1.52.2501.10.2.2")){
													upOrDown = "PS-recover";//PS-recover	
													//Smscontent smscontent = new Smscontent();
										 			//smscontent.setMessage(time+"&"+monitorip.getEquipname()+"&"+ttaddress+"&"+upOrDown+"&level=2");
										 			//发送短信
										 			Vector tosend = new Vector();
										 			//tosend.add(smscontent);	
										 			//System.out.println(smscontent.getMessage());										 			
										 			//smsmanager.sendSmscontent(tosend);
												}else{	
												if ((vb.getVariable()+"").startsWith("1.3.6.1.6.3.1.1.5.4")){
													
													upOrDown = "UP";//up
												}else if ((vb.getVariable()+"").toString().startsWith("1.3.6.1.6.3.1.1.5.3")){
													upOrDown = "DOWN";//down
												}else if ((vb.getVariable()+"").startsWith("1.3.6.1.2.1.11.0.3")){
													upOrDown = "UP";//down											
												}else if ((vb.getVariable()+"").startsWith("1.3.6.1.2.1.11.0.2")){
													upOrDown = "DOWN";//down																																		
												}else{
													return;
												}
												vb=(VariableBinding) v.get(2);
												String portIndex = vb.getVariable()+"";
												//Smscontent smscontent = new Smscontent();
												SysLogger.info("未经过短信配置前的TRAP信息:  地址为"+ttaddress+"的第"+portIndex+"号端口状态发生改变,值为"+upOrDown);
												//PortconfigDao configdao = new PortconfigDao(); 			
					 							//Portconfig portconfig = configdao.getByipandindex(ttaddress,portIndex);
									 				
												//判断是否包含配置端口
												
					 							Portconfig portconfig = null;
					 							if(ShareData.getAllportconfigs() != null){
					 								if(ShareData.getAllportconfigs().containsKey(ttaddress+":"+portIndex)){
						 								portconfig = (Portconfig)ShareData.getAllportconfigs().get(ttaddress+":"+portIndex);
						 							}
												}
					 							
					 							
												if (upOrDown != null && upOrDown.trim().length()>0){												
									 			if (portconfig != null){
									 				if (portconfig.getSms().intValue()==1){
									 					SmscontentDao dao = new SmscontentDao();
				 										dao.sendSms(time,host, portconfig, upOrDown);
				 										
				 										upOrDownStr = upOrDown.toUpperCase();
				 										
				 										boolean f = false;
				 										if(!Sendht.containsKey(ttaddress+portIndex)){
				 											Sendht.put(ttaddress+portIndex, upOrDownStr);
				 											f = true;
				 										}
				 										
				 										String nn = Sendht.get(ttaddress+portIndex).equals("UP")?"DOWN":"UP";
				 										Sendht.remove(ttaddress+portIndex);
				 										Sendht.put(ttaddress+portIndex, nn);
				 										
				 										upOrDownStr = ttaddress+portIndex;
				 										
				 										if(f && nn.equals("DOWN")){
				 											//赋值给全局变量 用于新线程
				 											Sendhost = host;
				 											Sendportconfig = portconfig;
				 											Sendtime = time;
				 											//启动发送短信线程
				 											Thread t1 =  new Thread(new SnmpTrapThread(Sendht,upOrDownStr));
				 											t1.start();
				 										}	
									 				}
									 			}
												}
												
												}
												
												
												}catch(Exception ex){
													
												}
											}//end for v.size()>2
										}																
									//end 开始处理端口是否DOWN
									if (v!=null && v.size()>1){
										//begin 处理端口是否DOWN
										if (((VariableBinding) v.get(0))
												.getOid()
												.toString()
												.startsWith("1.3.6.1.2.1.2.2.1.1")) {
												if (v.size() == 4) {											
													try{
													String upOrDown = "";//1.3.6.1.6.3.1.1.5.4 up 1.3.6.1.6.3.1.1.5.3 down
													VariableBinding vb = (VariableBinding) v.get(0);
													//vb[0]是portindex vb[1]是端口描述 vb[3]是端口状态改变的值
													//vb=(VariableBinding) v.get(0);
													String portIndex = vb.getVariable().toString();
													
													vb=(VariableBinding) v.get(3);
													upOrDown = vb.getVariable().toString().trim();
													
													//PortconfigDao configdao = new PortconfigDao(); 			
						 							//Portconfig portconfig = configdao.getByipandindex(ttaddress,portIndex);
													Portconfig portconfig = null;
										 										 			
													if(ShareData.getAllportconfigs() != null){
														if(ShareData.getAllportconfigs().containsKey(ttaddress+":"+portIndex)){
															portconfig = (Portconfig)ShareData.getAllportconfigs().get(ttaddress+":"+portIndex);
														}
														//Portconfig portconfig = 
													}
													if (upOrDown != null && upOrDown.trim().length()>0){
										 			if (portconfig != null){
										 				if (portconfig.getSms()==1){
										 					upOrDownStr = upOrDown.toUpperCase();
					 										
					 										boolean f = false;
					 										if(!Sendht.containsKey(ttaddress+portIndex)){
					 											Sendht.put(ttaddress+portIndex, upOrDownStr);
					 											f = true;
					 										}
					 										
					 										String nn = Sendht.get(ttaddress+portIndex).equals("UP")?"DOWN":"UP";
					 										Sendht.remove(ttaddress+portIndex);
					 										Sendht.put(ttaddress+portIndex, nn);
					 										
					 										upOrDownStr = ttaddress+portIndex;
					 										
					 										if(f && nn.equals("DOWN")){
					 											//赋值给全局变量 用于新线程
					 											Sendhost = host;
					 											Sendportconfig = portconfig;
					 											Sendtime = time;
					 											//启动发送短信线程
					 											Thread t1 =  new Thread(new SnmpTrapThread(Sendht,upOrDownStr));
					 											t1.start();
					 										}	
										 				}
										 			}
													}
													
													//}
													
													
													}catch(Exception ex){
														
													}
												}//end for v.size()>2
											}																
										//end 开始处理端口是否DOWN
										
										}
									
							}catch(Exception ex){
							}
						}//end command null

					}else{						
					}
				}
			};
			snmp.addCommandResponder(trapPrinter);
			
			
			
			
			
		} catch (Exception ex) {

			ex.printStackTrace();
		}
	}
	public void close() {
		try {
			if (snmp != null) {
				snmp.close();
				transport.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		SnmpTrapsListener.getInstance().listen();

	}
    /**
    创建任务
     */	
	private static Runnable createTask(final Host host,final Portconfig portconfig,final String trapvalue) {
		
		return new Runnable() {
			public void run() {
				try {                	
					//SysConfigFileUtil snmputil = new SysConfigFileUtil();
					try{
						//snmputil.deleteArp(ipaddress,writecommunity,version,oid);
						//Thread.sleep(30000);
//						SysLogger.info(trapvalue+"=================");
					}catch(Exception e){
						e.printStackTrace();
					}
            }catch(Exception exc){
            	
            }

            //System.out.println("Task " + taskID + ": end");
        }
		};
	}

}
class SnmpTrapThread extends SnmpTrapsListener implements Runnable{

	String key = "";
	public SnmpTrapThread(Hashtable ht,String key){
		this.Sendht = ht;
		this.key = key;
	}
	public void run() {
		try {
			Thread.sleep(new Random().nextInt(10000));//设定时间间隔10s
			if(Sendht.get(key).equals("DOWN")){
				//开始发送短信
				Smscontent smscontent = new Smscontent();
				String errorcontent = Sendtime+" "+Sendhost.getAlias()+"("+Sendhost.getIpAddress()+")"+Sendportconfig.getLinkuse()+"(第"+Sendportconfig.getPortindex()+"号)端口状态改变为"+upOrDownStr;
				if(Sendportconfig.getAlarmlevel() != null)
	 				smscontent.setLevel(Sendportconfig.getAlarmlevel());
	 			else
	 				smscontent.setLevel("1");
	 			smscontent.setObjid(Sendhost.getId()+"");
	 			smscontent.setMessage(errorcontent);
	 			smscontent.setRecordtime(Sendtime);
	 			smscontent.setSubtype("equipment");
	 			smscontent.setSubentity("network");
	 			smscontent.setIp(Sendhost.getIpAddress());
	 			List list = null;
	              TimeShareConfigDao timeconfigdao = new TimeShareConfigDao();
	              try{
	            	  list = timeconfigdao.getTimeShareConfigByObject(smscontent.getObjid(), smscontent.getSubtype());
	              }catch(Exception ex){
	                	ex.printStackTrace();
	              }finally{
	            	 timeconfigdao.close();
	              }
	             SmscontentDao senddao = new SmscontentDao();
	              try{
	            	 senddao.sentDetailSMS(list, errorcontent);
	              }catch(Exception ext){
	              	ext.printStackTrace();
	              }
	              //结束发送短信	
			}else{
				
			}
		}catch(InterruptedException e){}	
	}
}