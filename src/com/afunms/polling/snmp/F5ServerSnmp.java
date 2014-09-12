package com.afunms.polling.snmp;

/*
 * @author quzhi@dhcc.com.cn
 *
 */

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;
import java.util.Vector;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.IpAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.TableEvent;

import com.afunms.common.util.Arith;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtil;
import com.afunms.monitor.executor.base.MonitorInterface;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.monitor.item.base.MonitorResult;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.IfEntity;
import com.afunms.topology.model.HostNode;
import com.afunms.polling.om.*;
import com.afunms.polling.impl.*;
import com.afunms.polling.api.*;
import com.afunms.polling.task.TaskXml;




/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class F5ServerSnmp extends SnmpMonitor implements MonitorInterface {
	private static Hashtable ifTopsec_ifStatus = null;
	static {
		ifTopsec_ifStatus = new Hashtable();
		ifTopsec_ifStatus.put("1", "up");
		ifTopsec_ifStatus.put("2", "down");
		ifTopsec_ifStatus.put("3", "testing");
		ifTopsec_ifStatus.put("5", "unknow");
		ifTopsec_ifStatus.put("7", "unknow");
	};
	
	   public F5ServerSnmp()
	   {
	   }
	   public void collectData(Node node,MonitoredItem item){
		   
	   }
	   public void collectData(HostNode node){
		   
	   }
	   
	   public Hashtable collect_Data(HostNode node)
	   {
		  //SnmpItem item = (SnmpItem)monitoredItem;  
		   Calendar date=Calendar.getInstance();
		   Vector cpuVector=new Vector();
		   Vector memoryVector=new Vector();
		   Vector fanVector=new Vector();
		   Vector powerVector=new Vector();
		   Vector systemVector=new Vector();
		   Vector ipmacVector = new Vector();
		   Vector iprouterVector = new Vector();
		   Vector interfaceVector=new Vector();
		   Vector utilhdxpercVector = new Vector();
		   Vector utilhdxVector=new Vector();
		   Vector packsVector = new Vector();
		   Vector inpacksVector = new Vector();
		   Vector outpacksVector = new Vector();
		   Vector inpksVector = new Vector();
		   Vector outpksVector = new Vector();
			Vector discardspercVector = new Vector();
			Vector errorspercVector = new Vector();
			Vector allerrorspercVector = new Vector();
			Vector alldiscardspercVector = new Vector();
			Vector allutilhdxpercVector=new Vector();
			Vector allutilhdxVector=new Vector();
			List ifEntityList = new ArrayList();
			Vector fdbVector=new Vector();
			Vector temperatureVector=new Vector();
			Hashtable conns = new Hashtable();
			Vector userlogin = new Vector();
			Vector arpproxy = new Vector();
			Vector arps = new Vector();
			Vector vlans = new Vector();
			
		   HostNode host = (HostNode)node;
	   	   CPUcollectdata cpudata=null;	
	   	   Systemcollectdata systemdata=null;
	   	   Interfacecollectdata interfacedata=null;
			AllUtilHdx allutilhdx=null;
			UtilHdxPerc utilhdxperc=null;
			InPkts inpacks = null;
			OutPkts outpacks = null;
			UtilHdx utilhdx=null;
			SnmpUtil snmputil = null;
			Hashtable MACVSIP = new Hashtable();
			
		  try{
			  snmputil = SnmpUtil.getInstance();
			  ifEntityList = snmputil.getIfEntityList(host.getIpAddress(), host.getCommunity(), host.getCategory());
		  }catch(Exception e){
			  
		  }
		   
	   	  int result = 0;
	   	  List cpuList = new ArrayList();
	   	List memoryList = new ArrayList();
	   	List temperatureList = new ArrayList();
	   	List fanList = new ArrayList();
	   	List powerList = new ArrayList();
		  try{
			  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			  com.afunms.polling.base.Node snmpnode = (com.afunms.polling.base.Node)PollingEngine.getInstance().getNodeByIP(node.getIpAddress());
			  Date cc = date.getTime();
			  String time = sdf.format(cc);
			  snmpnode.setLastTime(time);
		  }catch(Exception e){
			  
		  }
			Vector pingVector=new Vector();
			Hashtable allService = new Hashtable();
			//List policys = new ArrayList();
			Hashtable policys = new Hashtable();
			
			Hashtable returnHas = new Hashtable();
			try {			
				Hostcollectdata hostdata=null;
				Pingcollectdata pingdata=null;
				Policydata policydata = null;
				Memorycollectdata memorydata=null;
				
				
				//-------------------------------------------------------------------------------------------cpu start	
				  try{
					  String temp = "0";
					String[] oids =                
						new String[] {               
							"1.3.6.1.4.1.14331.5.5.1.4.2" 
							};
					String[][] valueArray = null;
					valueArray = snmp.getCpuTableData(host.getIpAddress(),host.getCommunity(),oids);
		   			int allvalue=0;
		   			int flag = 0;
					if(valueArray != null){
					   	  for(int i=0;i<valueArray.length;i++)
					   	  {
					   		String _value = valueArray[i][0];
					   		String index = valueArray[i][1];
					   		int value=0;
					   		try{
					   			value=Integer.parseInt(_value);
					   		}catch(Exception e){
					   			
					   		}
							allvalue = allvalue+value/10;
							if(value >0){
								flag = flag +1;
						   		List alist = new ArrayList();
						   		alist.add(index);
						   		alist.add(_value);
						   		cpuList.add(alist);
							}
					   	  }
					}
					if(flag >0){
						int intvalue = (allvalue/flag);
						temp = intvalue+"";
						SysLogger.info(host.getIpAddress()+" cpu "+allvalue/flag);
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
					cpudata=new CPUcollectdata();
					cpudata.setIpaddress(host.getIpAddress());
					cpudata.setCollecttime(date);
					cpudata.setCategory("CPU");
					cpudata.setEntity("Utilization");
					cpudata.setSubentity("Utilization");
					cpudata.setRestype("dynamic");
					cpudata.setUnit("%");
					cpudata.setThevalue(result+"");

					cpuVector.addElement(cpudata);
			
				  }
				  catch(Exception e){
					  e.printStackTrace();
				  }
				  //-------------------------------------------------------------------------------------------cpu end
				
				  //-------------------------------------------------------------------------------------------system start			
				  try{
					
					final String[] desc=SnmpMibConstants.NetWorkMibSystemDesc;
					final String[] chname=SnmpMibConstants.NetWorkMibSystemChname;
							  String[] oids =                
								  new String[] {               
									"1.3.6.1.2.1.1.1" ,
									"1.3.6.1.2.1.1.3" ,
									"1.3.6.1.2.1.1.4" ,
									"1.3.6.1.2.1.1.5" ,
									"1.3.6.1.2.1.1.6" ,
									"1.3.6.1.2.1.1.7" 
									
									  };
							  
					String[][] valueArray = null;   	  
					try {
						valueArray = snmp.getTableData(host.getIpAddress(),host.getCommunity(),oids);
					} catch(Exception e){
						valueArray = null;
						SysLogger.error(host.getIpAddress() + "_TopsecSnmp");
					}
					if(valueArray != null){
				   	  for(int i=0;i<valueArray.length;i++)
				   	  {
				   		 for(int j=0;j<6;j++){
				   			systemdata=new Systemcollectdata();
							systemdata.setIpaddress(host.getIpAddress());
							systemdata.setCollecttime(date);
							systemdata.setCategory("System");
							systemdata.setEntity(desc[i]);
							systemdata.setSubentity(desc[j]);
							systemdata.setChname(chname[j]);
							systemdata.setRestype("static");
							systemdata.setUnit("");
							String value = valueArray[i][j];
							if (j==0){
								//if (value.length()>100 && value.split(":").length>5){
									systemdata.setThevalue(value);
								//}
							}else
								systemdata.setThevalue(value);
							systemVector.addElement(systemdata);
				   		 }
				   	  }
					}
				  }
				  catch(Exception e){
					  //e.printStackTrace();
				}
				  //-------------------------------------------------------------------------------------------system end			  


				  //-------------------------------------------------------------------------------------------service start			
				  try{
						
								  String[] oids =                
									  new String[] {               
										"1.3.6.1.4.1.3224.13.3.1.2" ,
										"1.3.6.1.4.1.3224.13.3.1.3" 																	
										  };
								  String[][] valueArray = null;   	  
									try {
										valueArray = snmp.getTableData(host.getIpAddress(),host.getCommunity(),oids);
									} catch(Exception e){
										valueArray = null;
										SysLogger.error(host.getIpAddress() + "_TopsecSnmp");
									}
									if(valueArray != null){
									   	  for(int i=0;i<valueArray.length;i++)
									   	  {
									   		if (valueArray[i][0] != null && valueArray[i][0].startsWith("_")&&valueArray[i][0].endsWith("_")){							
	   											if (!allService.containsKey(valueArray[i][0])){
	   												Vector itValue = new Vector();
	   												itValue.add(valueArray[i][1]);
	   												allService.put(valueArray[i][0], itValue);
	   											}else{
	   												Vector itValue = (Vector)allService.get(valueArray[i][0]);
	   												if (itValue != null){
	   													itValue.add(valueArray[i][1]);									
	   													allService.put(valueArray[i][0], itValue);
	   												}
	   											}															
	   										}
									   		  
									   	  }
									}		  				
					  }
					  catch(Exception e){e.printStackTrace();}
					  //-------------------------------------------------------------------------------------------service end
					  
					  //-------------------------------------------------------------------------------------------policy start			
					  try{						
									  String[] oids =                
										  new String[] {               
											"1.3.6.1.4.1.3224.10.1.1.1" ,//ID
											"1.3.6.1.4.1.3224.10.1.1.3" ,//Untrust
											"1.3.6.1.4.1.3224.10.1.1.4" ,//trust
											"1.3.6.1.4.1.3224.10.1.1.5" ,//source
											"1.3.6.1.4.1.3224.10.1.1.6" ,//destination
											"1.3.6.1.4.1.3224.10.1.1.25" //service								
											  };
									  String[][] valueArray = null;   	 
									  List untoTrustPolicys = new ArrayList();
										List trustToUNPolicys = new ArrayList();
										
										try {
											valueArray = snmp.getTableData(host.getIpAddress(),host.getCommunity(),oids);
										} catch(Exception e){
											valueArray = null;
											e.printStackTrace();
											//SysLogger.error(host.getIpAddress() + "_NetscreenSnmp");
										}
										
										if(valueArray != null){
										   	  for(int i=0;i<valueArray.length;i++)
										   	  {
										   		policydata = new Policydata();
												String pid = valueArray[i][0];							
												policydata.setPid(pid);
												policydata.setSource(valueArray[i][3]);
												policydata.setDestination(valueArray[i][4]);
												String service = valueArray[i][5];
												if(service.startsWith("_")&&service.endsWith("_")){									
													Vector itValue = (Vector)allService.get(service);								
													String p_services ="";
													if (itValue != null){
														for(int j=0;j<itValue.size();j++){
															if (p_services.trim().length()>0){
																p_services = p_services +","+(String)itValue.get(j);
															}else{
																p_services = p_services +(String)itValue.get(j);
															}
															
														}
													}
													policydata.setServices(p_services);
												}else{
													policydata.setServices(service);
												}
												
												String untrust = valueArray[i][1];
												String trust = valueArray[i][2];
												if (untrust.equalsIgnoreCase("trust")&&trust.equalsIgnoreCase("dmz")){
													policydata.setTrustflag(0);
													trustToUNPolicys.add(policydata);
												}else if (untrust.equalsIgnoreCase("dmz")&&trust.equalsIgnoreCase("trust")){
													policydata.setTrustflag(1);
													untoTrustPolicys.add(policydata);
												}
										   		  
										   	  }
										}
							policys.put("untotrust", untoTrustPolicys);
							policys.put("trusttoun", trustToUNPolicys);
						  }
						  catch(Exception e){e.printStackTrace();}
						  //-------------------------------------------------------------------------------------------policy end			  
				  
				  
					//-------------------------------------------------------------------------------------------memory start	
				  
					try{
						//SysLogger.info("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
						String[] oids =                
							new String[] {               
								"1.3.6.1.4.1.14331.5.5.1.4.3",//Memory Load
								"1.3.6.1.4.1.14331.5.5.1.4.4",//Memory Free
								"1.3.6.1.4.1.14331.5.5.1.4.5",//current connections
								"1.3.6.1.4.1.14331.5.5.1.4.6"}; //new connections per second
						
						String[][] valueArray = null; 
						int allMemorySize=0;
						int usedperc = 0;
						float value=0.0f;
						int leftsize=0;
						int allUsedSize=0;
						int currentcon = 0;
						int newconpersec = 0;
						
						try {
							valueArray = snmp.getTableData(host.getIpAddress(),host.getCommunity(),oids);
						} catch(Exception e){
							valueArray = null;
							SysLogger.error(host.getIpAddress() + "_TopsecSnmp");
						}
						//SysLogger.info(valueArray.length+"#################################################################3");
						if(valueArray != null){
						   	  for(int i=0;i<valueArray.length;i++)
						   	  {
						   		  //if(valueArray[i][0] != null && valueArray[i][1] != null && valueArray[i][2] != null && valueArray[i][3] != null){
						   			  try{
						   				  //SysLogger.info(valueArray[i][0]+"===="+valueArray[i][1]+"==="+valueArray[i][2]+"==="+valueArray[i][3]);
						   				  if(valueArray[i][0] != null){
						   					value=Integer.parseInt(valueArray[i][0])/10;//MIB取出来的值是千分值
						   				  }
						   				  if(valueArray[i][1] != null){
						   					leftsize=Integer.parseInt(valueArray[i][1]); //KB
						   				  }
						   				  if(valueArray[i][2] != null){
						   					currentcon=Integer.parseInt(valueArray[i][2]);
						   				  }
						   				  if(valueArray[i][3] != null){
						   					newconpersec=Integer.parseInt(valueArray[i][3]); 
						   				  }
								   		
								   		break;
						   			  }catch(Exception e){
						   				 break; 
						   			  }
						   			
						   		  //}
						   		 
						   	  }
						}
						conns.put("currentcon", currentcon);
						conns.put("newconpersec", newconpersec);
						memorydata=new Memorycollectdata();
						memorydata.setIpaddress(host.getIpAddress());
						memorydata.setCollecttime(date);
						memorydata.setCategory("Memory");
						memorydata.setEntity("Utilization");
						memorydata.setSubentity("PhysicalMemory");
						memorydata.setRestype("dynamic");
						memorydata.setUnit("%");
						memorydata.setThevalue((int)Math.rint(value/1)+"");
						memoryVector.addElement(memorydata);
						
						memorydata=new Memorycollectdata();
						memorydata.setIpaddress(host.getIpAddress());
						memorydata.setCollecttime(date);
						memorydata.setCategory("Memory");
						memorydata.setEntity("Allocate");
						memorydata.setRestype("static");
						memorydata.setSubentity("PhysicalMemory");
					
						float size=0.0f;
						size=(leftsize*100/(100-value))*1.0f/1024;			
						if(size>=1024.0f){
							size=size/1024;
							memorydata.setUnit("G");
						}
						else{
							memorydata.setUnit("M");
						}				
						memorydata.setThevalue((int)Math.rint(size/1)+"");
						memoryVector.addElement(memorydata);
						memorydata=new Memorycollectdata();
						memorydata.setIpaddress(host.getIpAddress());
						memorydata.setCollecttime(date);
						memorydata.setCategory("Memory");
						memorydata.setEntity("UsedSize");
						memorydata.setRestype("static");
						memorydata.setSubentity("PhysicalMemory");
						size= ((leftsize*100/(100-value))-leftsize)*1.0f/1024;						
								if(size>=1024.0f){
									size=size/1024;
									memorydata.setUnit("G");
								}
								else{
									memorydata.setUnit("M");
								}	
								memorydata.setThevalue((int)Math.rint(size/1)+"");
								memoryVector.addElement(memorydata);
						}
						catch(Exception e){
							e.printStackTrace();
						}
			
						//-------------------------------------------------------------------------------------------memory end
						
						
						//---------------------------------------------------得到所有IpNetToMedia start
					     try
					     {
					        String[] oids = new String[]
					                    {"1.3.6.1.2.1.4.22.1.1",   //1.ifIndex
					        		     "1.3.6.1.2.1.4.22.1.2",   //2.mac
					                     "1.3.6.1.2.1.4.22.1.3",   //3.ip
					                     "1.3.6.1.2.1.4.22.1.4"};  //4.type
							String[][] valueArray = null;   	  
							try {
								valueArray = snmp.getTableData(host.getIpAddress(),host.getCommunity(),oids);
							} catch(Exception e){
								valueArray = null;
								e.printStackTrace();
								SysLogger.error(host.getIpAddress() + "_TopsecSnmp");
							}
							if(valueArray != null){
						   	  for(int i=0;i<valueArray.length;i++)
						   	  {
						   		  IpMac ipmac = new IpMac();
						   		  for(int j=0;j<4;j++){
						   			String sValue = valueArray[i][j];
						   			if(sValue == null)continue;
									if(j==0){
										ipmac.setIfindex(sValue);
									}else if (j==1){
										ipmac.setMac(sValue);
									}else if (j==2){
										ipmac.setIpaddress(sValue);									
									}
						   		 }
						   		//SysLogger.info(host.getIpAddress()+" ARP hostip==>"+"=="+ipmac.getMac()+"====="+ipmac.getIpaddress());
						   		ipmac.setIfband("0");
						   		ipmac.setIfsms("0");
								ipmac.setCollecttime(new GregorianCalendar());
								ipmac.setRelateipaddr(host.getIpAddress());
								MACVSIP.put(ipmac.getMac(), ipmac.getIpaddress());
								ipmacVector.addElement(ipmac);
						   	  }	
							}
					    }
					    catch (Exception e)
					    {
					        e.printStackTrace();
					    }
					  
					    //---------------------------------------------------得到所有IpNetToMedia end	
					    
					   
				   	  
					    //---------------------------------------------------从ip router table中得到与该设备相连的路由器 start
					     try
					     {
					        String[] oids = new String[]
									                    {"1.3.6.1.2.1.4.21.1.2",       //0.if index
									                	 "1.3.6.1.2.1.4.21.1.1",       //1.ipRouterDest        		       
									                     "1.3.6.1.2.1.4.21.1.7",       //7.ipRouterNextHop
									                     "1.3.6.1.2.1.4.21.1.8",       //8.ipRouterType
									                     "1.3.6.1.2.1.4.21.1.9",       //9.ipRouterProto
									                     "1.3.6.1.2.1.4.21.1.11"};     //11.ipRouterMask
					        
							String[][] valueArray = null;   	  
							try {
								valueArray = snmp.getTableData(host.getIpAddress(),host.getCommunity(),oids);
							} catch(Exception e){
								valueArray = null;
								e.printStackTrace();
								SysLogger.error(host.getIpAddress() + "_H3CSnmp");
							}
					        if(valueArray != null){
						   	  for(int i=0;i<valueArray.length;i++)
						   	  {
						   		  if(valueArray[i][1] == null)continue;
									String _vbString = valueArray[i][1].toString();
									
									if (_vbString!=null){
										if (_vbString.equals("0.0.0.0")||_vbString.startsWith("127.0"))continue;
									}
									_vbString = valueArray[i][5].toString();
									if (_vbString!=null){
										if(_vbString.equals("0.0.0.0"))continue;
									}
									IpRouter iprouter = new IpRouter();
									iprouter.setRelateipaddr(host.getIpAddress());
									for(int j=0;j<6;j++){
										if(valueArray[i][j]!=null){
											String sValue=valueArray[i][j].toString();								
											if(j==0){
												iprouter.setIfindex(sValue);
											}else if (j==1){
												iprouter.setDest(sValue);
											}else if (j==2){
												iprouter.setNexthop(sValue);
											}else if (j==3){
												iprouter.setType(new Long(sValue));
											}else if (j==4){
												iprouter.setProto(new Long(sValue));
											}else if (j==5){
												iprouter.setMask(sValue);
											}
										}
									}
									iprouterVector.addElement(iprouter);
						   	  }
					        }
					    }
					    catch (Exception e)
					    {
					    	//SysLogger.error("getIpNetToMediaTable(),ip=" + address + ",community=" + community);
					        e.printStackTrace();
					    }			    		  
					    //---------------------------------------------------从ip router table中得到与该设备相连的路由器 end							
						
						//---------------------------------------------------interface start			
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
									  String[] oids =                
										  new String[] {               
											"1.3.6.1.2.1.2.2.1.1", //index
											"1.3.6.1.2.1.2.2.1.2",//ifDesc
											"1.3.6.1.2.1.2.2.1.3",//ifType
											"1.3.6.1.2.1.2.2.1.4",//ifMtu
											"1.3.6.1.2.1.2.2.1.5",//ifSpeed
											"1.3.6.1.2.1.2.2.1.6",//ifPhysAddr
											"1.3.6.1.2.1.2.2.1.7",//ifAdminStatus 6
											"1.3.6.1.2.1.2.2.1.8",//ifOperStatus 7
											"1.3.6.1.2.1.2.2.1.9",//ifLastChange 8
											"1.3.6.1.2.1.31.1.1.1.1",				
											  };
							String[] oids1=                
								 new String[] {     
								//"1.3.6.1.2.1.2.2.1.1",	
								"1.3.6.1.2.1.2.2.1.10",  //ifInOctets 0        
								"1.3.6.1.2.1.2.2.1.11",//ifInUcastPkts 1
								"1.3.6.1.2.1.2.2.1.12",//ifInNUcastPkts 2
								"1.3.6.1.2.1.2.2.1.13",//ifInDiscards 3
								"1.3.6.1.2.1.2.2.1.14",//ifInErrors 4
								"1.3.6.1.2.1.2.2.1.16", //ifOutOctets 5
								"1.3.6.1.2.1.2.2.1.17",//ifOutUcastPkts 6
								"1.3.6.1.2.1.2.2.1.18",//ifOutNUcastPkts 7
								"1.3.6.1.2.1.2.2.1.19",	//ifOutDiscards 8
								"1.3.6.1.2.1.2.2.1.20"//ifOutErrors 9								
								};				 				 
									
					
							final String[] desc3=SnmpMibConstants.NetWorkMibInterfaceDesc3;
							final String[] desc=SnmpMibConstants.NetWorkMibInterfaceDesc0;
							final String[] unit=SnmpMibConstants.NetWorkMibInterfaceUnit0;
							final String[] chname=SnmpMibConstants.NetWorkMibInterfaceChname0;
							final int[] scale=SnmpMibConstants.NetWorkMibInterfaceScale0;
							final String[] desc1=SnmpMibConstants.NetWorkMibInterfaceDesc1;
							final String[] chname1=SnmpMibConstants.NetWorkMibInterfaceChname1;
							final String[] unit1=SnmpMibConstants.NetWorkMibInterfaceUnit1;
							final int[] scale1=SnmpMibConstants.NetWorkMibInterfaceScale1;
							
							String[][] valueArray = null;   	  
							try {
								valueArray = snmp.getTableData(host.getIpAddress(),host.getCommunity(),oids);
							} catch(Exception e){
								valueArray = null;
								e.printStackTrace();
								SysLogger.error(host.getIpAddress() + "_NetscreenSnmp");
							}
							String[][] valueArray1 = null;   	  
							try {
								valueArray1 = snmp.getTableData(host.getIpAddress(),host.getCommunity(),oids1);
							} catch(Exception e){
								valueArray1 = null;
								e.printStackTrace();
								SysLogger.error(host.getIpAddress() + "_NetscreenSnmp");
							}
						
							long allSpeed=0;
							long allOutOctetsSpeed=0;
							long allInOctetsSpeed=0;
							long allOctetsSpeed=0;
							
							long allinpacks=0;
							long inupacks=0;//入口单向和
							long innupacks=0;//非单向
							long indiscards=0;
							long inerrors=0;
							long alloutpacks=0;
							long outupacks=0;//出口单向
							long outnupacks=0;//非单向
							long outdiscards=0;
							long outerrors=0;
							long alldiscards=0;
							long allerrors=0;
							long allpacks=0;
						
							Vector tempV=new Vector();
							Hashtable tempHash = new Hashtable();
							if(valueArray != null){
						   	  for(int i=0;i<valueArray.length;i++)
						   	  {				
						   		  System.out.println("===========valueArray=============:"+valueArray.length);
									//String vbString=valueArray[i][0].toString();
						   		  if(valueArray[i][0] == null)continue;
									String sIndex=valueArray[i][0].toString();				
									tempV.add(sIndex);
									tempHash.put(i, sIndex);
									
									for(int j=0;j<10;j++){
											//把预期状态和ifLastChange过滤掉
											//if (j==6 || j==8)continue;
											if (j==8)continue;
											
											String sValue=valueArray[i][j];	
											
											interfacedata=new Interfacecollectdata();
											interfacedata.setIpaddress(host.getIpAddress());
											interfacedata.setCollecttime(date);
											interfacedata.setCategory("Interface");
											//if (desc[j].equals("ifAdminStatus"))continue;
											interfacedata.setEntity(desc[j]);
											interfacedata.setSubentity(sIndex);
											//端口状态不保存，只作为静态数据放到临时表里
											if(j==7)
												interfacedata.setRestype("static");
											else {
												interfacedata.setRestype("static");
											} 
											interfacedata.setUnit(unit[j]);
											

											if((j==4)&&sValue!=null){//流速
												long lValue=Long.parseLong(sValue)/scale[j];								
													hashSpeed.put(sIndex,Long.toString(lValue));
												allSpeed=allSpeed+lValue;					
											}
											if((j==6 || j==7)&&sValue!=null){//预期状态和当前状态
					
												if (ifTopsec_ifStatus.get(sValue) != null){
													interfacedata.setThevalue(ifTopsec_ifStatus.get(sValue).toString());
												}else{
													interfacedata.setThevalue("0.0");
												}
											}
											else if((j==2)&&sValue!=null){//断口类型
												if (Interface_IfType.get(sValue) != null){
													interfacedata.setThevalue(Interface_IfType.get(sValue).toString());
												}else{
													interfacedata.setThevalue("0.0");	
												}
											}
											else{
												if(scale[j]==0){
													interfacedata.setThevalue(sValue);
												}
												else{
													interfacedata.setThevalue(Long.toString(Long.parseLong(sValue)/scale[j]));
												}
											}
										
											interfacedata.setChname(chname[j]);
											interfaceVector.addElement(interfacedata);
									   } //end for j
						   	  } //end for valueArray
							}
							if(valueArray1!= null){
							for(int i=0;i<valueArray1.length;i++){
								allinpacks=0;
								inupacks=0;//入口单向和
								innupacks=0;//非单向
								indiscards=0;
								inerrors=0;
								alloutpacks=0;
								outupacks=0;//出口单向
								outnupacks=0;//非单向
								outdiscards=0;
								outerrors=0;																		

								String sIndex = (String)tempHash.get(i);				
								if (tempV.contains(sIndex)){
													
									for(int j=0;j<10;j++){																														
										if(valueArray1[i][j]!=null){
											String sValue=valueArray1[i][j];
											//if (j==6)continue;
											interfacedata=new Interfacecollectdata();
											if(scale1[j]==0){
												interfacedata.setThevalue(sValue);
											}
											else{
												interfacedata.setThevalue(Long.toString(Long.parseLong(sValue)/scale1[j]));
											}
											if (j==1 || j==2){
												//入口单向传输数据包,入口非单向传输数据包												
												if (sValue != null){
													allinpacks=allinpacks+Long.parseLong(sValue);
													
													Calendar cal=(Calendar)hash.get("collecttime");
													long timeInMillis=0;
													if(cal!=null)timeInMillis=cal.getTimeInMillis();
													long longinterval=(date.getTimeInMillis()-timeInMillis)/1000;
													
													inpacks=new InPkts();
													inpacks.setIpaddress(host.getIpAddress());
													inpacks.setCollecttime(date);
													inpacks.setCategory("Interface");
													String chnameBand="";
													if(j==1){
														inpacks.setEntity("ifInUcastPkts");
														chnameBand="单向";
													}
													if(j==2){
														inpacks.setEntity("ifInNUcastPkts");
														chnameBand="非单向";
													}
													inpacks.setSubentity(sIndex);
													inpacks.setRestype("dynamic");
													inpacks.setUnit("个");	
													inpacks.setChname(chnameBand);
													long currentPacks=Long.parseLong(sValue);												
													long lastPacks=0;	
													long l=0;											
															
													//如果当前采集时间与上次采集时间的差小于采集间隔两倍，则计算带宽利用率，否则带宽利用率为0；
													if(longinterval<2*interval){
														String lastvalue="";
														
														if(hash.get(desc1[j]+":"+sIndex)!=null)lastvalue=hash.get(desc1[j]+":"+sIndex).toString();
														//取得上次获得的Octets
														if(lastvalue!=null && !lastvalue.equals(""))lastPacks=Long.parseLong(lastvalue);
													}
										
													if(longinterval!=0){
														if(currentPacks<lastPacks){
															currentPacks=currentPacks+4294967296L;
														}
														//现流量-前流量
														//SysLogger.info(host.getIpAddress()+"==="+sIndex+"断口==="+currentPacks+"===="+lastPacks);
														long octetsBetween=currentPacks-lastPacks;
														l=octetsBetween;
														if(lastPacks == 0)l=0;
													}
													inpacks.setThevalue(Long.toString(l));	
													//SysLogger.info(host.getIpAddress()+" 第"+utilhdx.getSubentity()+"端口 "+"Speed "+Long.toString(l*8));
													if (cal != null)
														inpacksVector.addElement(inpacks);	
												}
													//continue;
											}
											if (j==3){
												//入口丢弃的数据包
												if (sValue != null) indiscards=Long.parseLong(sValue);
													continue;
											}
											if (j==4){
												//入口错误的数据包
												if (sValue != null) inerrors=Long.parseLong(sValue);
												continue;
											}	
											if (j==6 || j==7){
												//入口单向传输数据包,入口非单向传输数据包
												if (sValue != null){
													alloutpacks=alloutpacks+Long.parseLong(sValue);
													
													Calendar cal=(Calendar)hash.get("collecttime");
													long timeInMillis=0;
													if(cal!=null)timeInMillis=cal.getTimeInMillis();
													long longinterval=(date.getTimeInMillis()-timeInMillis)/1000;
													
													outpacks=new OutPkts();
													outpacks.setIpaddress(host.getIpAddress());
													outpacks.setCollecttime(date);
													outpacks.setCategory("Interface");
													String chnameBand="";
													
													if(j==6){
														outpacks.setEntity("ifOutUcastPkts");
														chnameBand="单向";
													}
													if(j==7){
														outpacks.setEntity("ifOutNUcastPkts");
														chnameBand="非单向";
													}
													outpacks.setSubentity(sIndex);
													outpacks.setRestype("dynamic");
													outpacks.setUnit("个");	
													outpacks.setChname(chnameBand);
													long currentPacks=Long.parseLong(sValue);												
													long lastPacks=0;	
													long l=0;											
															
													//如果当前采集时间与上次采集时间的差小于采集间隔两倍，则计算带宽利用率，否则带宽利用率为0；
													if(longinterval<2*interval){
														String lastvalue="";
														
														if(hash.get(desc1[j]+":"+sIndex)!=null)lastvalue=hash.get(desc1[j]+":"+sIndex).toString();
														//取得上次获得的Octets
														if(lastvalue!=null && !lastvalue.equals(""))lastPacks=Long.parseLong(lastvalue);
													}
										
													if(longinterval!=0){
														if(currentPacks<lastPacks){
															currentPacks=currentPacks+4294967296L;
														}
														//现流量-前流量	
														long octetsBetween=currentPacks-lastPacks;
														l=octetsBetween;
														if(lastPacks == 0)l=0;
													}
													outpacks.setThevalue(Long.toString(l));	
													//SysLogger.info(host.getIpAddress()+" 第"+utilhdx.getSubentity()+"端口 "+"Speed "+Long.toString(l*8));
													if (cal != null)
														outpacksVector.addElement(outpacks);
													
												}
												//continue;
											}
											if (j==8){
												//入口丢弃的数据包
												if (sValue != null) outdiscards=Long.parseLong(sValue);
												continue;
											}
											if (j==9){
												//入口错误的数据包
												if (sValue != null) outerrors=Long.parseLong(sValue);
												continue;
											}
											/*
											interfacedata=new Interfacecollectdata();
											if(scale1[j]==0){
												interfacedata.setThevalue(sValue);
											}
											else{
												interfacedata.setThevalue(Long.toString(Long.parseLong(sValue)/scale1[j]));
											}
											*/			
											Calendar cal=(Calendar)hash.get("collecttime");
											long timeInMillis=0;
											if(cal!=null)timeInMillis=cal.getTimeInMillis();
											long longinterval=(date.getTimeInMillis()-timeInMillis)/1000;											

											//计算每个端口流速及利用率
											if(j==0 || j==5){
												utilhdx=new UtilHdx();
												utilhdx.setIpaddress(host.getIpAddress());
												utilhdx.setCollecttime(date);
												utilhdx.setCategory("Interface");
												String chnameBand="";
												if(j==0){
													utilhdx.setEntity("InBandwidthUtilHdx");
													chnameBand="入口";
												}
												if(j==5){
													utilhdx.setEntity("OutBandwidthUtilHdx");
													chnameBand="出口";
													}
												utilhdx.setSubentity(sIndex);
												utilhdx.setRestype("dynamic");
												utilhdx.setUnit(unit1[j]+"/秒");	
												utilhdx.setChname(sIndex+"端口"+"流速");
												long currentOctets=Long.parseLong(sValue)/scale1[j];
												long lastOctets=0;	
												long l=0;											
														
												//如果当前采集时间与上次采集时间的差小于采集间隔两倍，则计算带宽利用率，否则带宽利用率为0；
												if(longinterval<2*interval){
													String lastvalue="";
													
													if(hash.get(desc1[j]+":"+sIndex)!=null)lastvalue=hash.get(desc1[j]+":"+sIndex).toString();
													//SysLogger.info(desc1[j]+":"+sIndex+"===="+lastvalue);
													//取得上次获得的Octets
													if(lastvalue!=null && !lastvalue.equals(""))lastOctets=Long.parseLong(lastvalue);
												}
									
												if(longinterval!=0){
													if(currentOctets<lastOctets){
														currentOctets=currentOctets+4294967296L/scale1[j];
													}
													//现流量-前流量	
													long octetsBetween=currentOctets-lastOctets;
													//SysLogger.info(sIndex+"===currentOctets:"+currentOctets+"===lastOctets:"+lastOctets+"===octetsBetween:"+octetsBetween);
													//计算端口速率
													l=octetsBetween/longinterval;
													//统计总出入字节利用率,备用计算（出、入、综合）带宽利用率
													if(j==0 && lastOctets!=0)allInOctetsSpeed=allInOctetsSpeed+l;
													if(j==5 && lastOctets!=0)allOutOctetsSpeed=allOutOctetsSpeed+l;
													if(lastOctets!=0)allOctetsSpeed=allOctetsSpeed+l;
													
												}
												utilhdx.setThevalue(Long.toString(l*8));	
												//SysLogger.info(host.getIpAddress()+" 第"+utilhdx.getSubentity()+"端口 "+"Speed "+Long.toString(l*8));
												if (cal != null)
												utilhdxVector.addElement(utilhdx);												
												utilhdxperc=new UtilHdxPerc();
												utilhdxperc.setIpaddress(host.getIpAddress());
												utilhdxperc.setCollecttime(date);
												utilhdxperc.setCategory("Interface");
												if(j==0)utilhdxperc.setEntity("InBandwidthUtilHdxPerc");
												if(j==5)utilhdxperc.setEntity("OutBandwidthUtilHdxPerc");
												utilhdxperc.setSubentity(sIndex);
												utilhdxperc.setRestype("dynamic");
												utilhdxperc.setUnit("%");	
												utilhdxperc.setChname(sIndex+"端口"+chnameBand+"带宽利用率");												
												double speed=0.0;
												if (hashSpeed.get(sIndex) != null){
												speed = Double.parseDouble(hashSpeed.get(sIndex).toString());
												}else{
													speed = Double.parseDouble("0.0");
												}
												double d=0.0;
												if(speed>0){
													//带宽利用率＝流速×8*100/ifspeed%
													d=Arith.div(l*800,speed);
													//d=l*800/speed;
												}
												utilhdxperc.setThevalue(Double.toString(d));
												if (cal != null)
												utilhdxpercVector.addElement(utilhdxperc);	
															
															
											} //end j=0 j=5
											//SysLogger.info(host.getIpAddress()+"==="+desc1[j]+":"+sIndex+"===="+sValue);
											octetsHash.put(desc1[j]+":"+sIndex,interfacedata.getThevalue());
										} //valueArray1[i][j]==null
									} //end for j
									//Hashtable packhash=lastCollectDataManager.getLastPacks(nethost);
									Hashtable packhash=ShareData.getPacksdata(host.getIpAddress()+":"+sIndex);
									//Hashtable discardshash=lastCollectDataManager.getLastDiscards(nethost);
									Hashtable discardshash=ShareData.getDiscardsdata(host.getIpAddress()+":"+sIndex);
									//Hashtable errorshash=lastCollectDataManager.getLastErrors(nethost);
									Hashtable errorshash=ShareData.getErrorsdata(host.getIpAddress()+":"+sIndex);									
									//计算传输的数据包
									interfacedata=new Interfacecollectdata();
									interfacedata.setIpaddress(host.getIpAddress());
									interfacedata.setCollecttime(date);
									interfacedata.setCategory("Interface");
									interfacedata.setEntity("AllInCastPkts");
									interfacedata.setSubentity(sIndex);									
									interfacedata.setRestype("static");
									interfacedata.setUnit("个");
									interfacedata.setThevalue(allinpacks+"");
									interfacedata.setChname("入口总数据包");
									/******************/
									Hashtable hasht = new Hashtable();									
									hasht.put("AllInCastPkts"+":"+sIndex,allinpacks+"");																		


									interfacedata=new Interfacecollectdata();
									interfacedata.setIpaddress(host.getIpAddress());
									interfacedata.setCollecttime(date);
									interfacedata.setCategory("Interface");
									interfacedata.setEntity("AllOutCastPkts");
									interfacedata.setSubentity(sIndex);									
									interfacedata.setRestype("static");
									interfacedata.setUnit("个");
									interfacedata.setThevalue(alloutpacks+"");
									interfacedata.setChname("出口总数据包");
									/******************/									
									hasht.put("AllOutCastPkts"+":"+sIndex,alloutpacks+"");
									
									interfacedata=new Interfacecollectdata();
									interfacedata.setIpaddress(host.getIpAddress());
									interfacedata.setCollecttime(date);
									interfacedata.setCategory("Interface");
									interfacedata.setEntity("AllInDiscards");
									interfacedata.setSubentity(sIndex);									
									interfacedata.setRestype("static");
									interfacedata.setUnit("个");
									interfacedata.setThevalue(indiscards+"");
									interfacedata.setChname("入口总丢包数");
									Hashtable tempDiscards = new Hashtable();
									tempDiscards.put("AllInDiscards"+":"+sIndex,indiscards+"");

									interfacedata=new Interfacecollectdata();
									interfacedata.setIpaddress(host.getIpAddress());
									interfacedata.setCollecttime(date);
									interfacedata.setCategory("Interface");
									interfacedata.setEntity("AllOutDiscards");
									interfacedata.setSubentity(sIndex);									
									interfacedata.setRestype("static");
									interfacedata.setUnit("个");
									interfacedata.setThevalue(outdiscards+"");
									interfacedata.setChname("出口总丢包数");
									tempDiscards.put("AllOutDiscards"+":"+sIndex,outdiscards+"");
									
									interfacedata=new Interfacecollectdata();
									interfacedata.setIpaddress(host.getIpAddress());
									interfacedata.setCollecttime(date);
									interfacedata.setCategory("Interface");
									interfacedata.setEntity("AllInErrors");
									interfacedata.setSubentity(sIndex);									
									interfacedata.setRestype("static");
									interfacedata.setUnit("个");
									interfacedata.setThevalue(inerrors+"");
									interfacedata.setChname("入口错误包数");
									Hashtable errHash = new Hashtable();
									errHash.put("AllInErrors"+":"+sIndex,inerrors+"");
									//interfaceVector.addElement(interfacedata);

									interfacedata=new Interfacecollectdata();
									interfacedata.setIpaddress(host.getIpAddress());
									interfacedata.setCollecttime(date);
									interfacedata.setCategory("Interface");
									interfacedata.setEntity("AllOutErrors");
									interfacedata.setSubentity(sIndex);									
									interfacedata.setRestype("static");
									interfacedata.setUnit("个");
									interfacedata.setThevalue(outerrors+"");
									interfacedata.setChname("出口错误包数");
									errHash.put("AllOutErrors"+":"+sIndex,outerrors+"");
									//interfaceVector.addElement(interfacedata);

									String lastvalue="";
									long lastpacks=0;
									//入口传输数据包
									if (packhash != null){
										if(packhash.get("AllInCastPkts"+":"+sIndex)!=null)lastvalue=packhash.get("AllInCastPkts"+":"+sIndex).toString();
									}
									
									//取得上次获得的packs
									if(lastvalue!=null && !lastvalue.equals("")){										
										lastpacks=Long.parseLong(lastvalue);									
									}
												
									Packs packs = new Packs();
									packs.setIpaddress(host.getIpAddress());
									packs.setCollecttime(date);
									packs.setCategory("Interface");
									packs.setEntity("InCastPkts");
									packs.setSubentity(sIndex);
									packs.setRestype("dynamic");
									packs.setUnit("个");
									packs.setChname("入口数据包");
									if (lastpacks > 0){
										packs.setThevalue(allinpacks-lastpacks+"");
									}else{
										packs.setThevalue("0");
									}									
									packsVector.add(packs);
									
									//入口丢包率
									lastvalue="";
									long lastdiscards=0;
									if (discardshash != null){
										if(discardshash.get("AllInDiscards"+":"+sIndex)!=null)lastvalue=discardshash.get("AllInDiscards"+":"+sIndex).toString();
									}
									
									//取得上次获得的packs
									if(lastvalue!=null && !lastvalue.equals("")){
										lastdiscards=Long.parseLong(lastvalue);									
									}
												
									DiscardsPerc discardsPerc = new DiscardsPerc();
									discardsPerc.setIpaddress(host.getIpAddress());
									discardsPerc.setCollecttime(date);
									discardsPerc.setCategory("Interface");
									discardsPerc.setEntity("InDiscardsPerc");
									discardsPerc.setSubentity(sIndex);
									discardsPerc.setRestype("dynamic");
									discardsPerc.setUnit("%");
									discardsPerc.setChname("入口丢包率");
									double indiscardserc=0.0;
									if (allinpacks ==0){
										indiscardserc=0;
									}else{
										if (allinpacks-lastpacks > 0){
											indiscardserc = (indiscards-lastdiscards)/(allinpacks-lastpacks);
										}else{
											indiscardserc = 0;
										}
										
									}									
									discardsPerc.setThevalue(Double.toString(indiscardserc));
									discardspercVector.add(discardsPerc);
												
									//入口错误率
									lastvalue="";
									long lasterrors=0;
									if (errorshash != null){
										if(errorshash.get("AllInErrors"+":"+sIndex)!=null)lastvalue=errorshash.get("AllInErrors"+":"+sIndex).toString();
									}									
									//取得上次获得的error
									if(lastvalue!=null && !lastvalue.equals("")){
										lasterrors=Long.parseLong(lastvalue);									
									}																		
									ErrorsPerc errorsPerc = new  ErrorsPerc();
									errorsPerc.setIpaddress(host.getIpAddress());
									errorsPerc.setCollecttime(date);
									errorsPerc.setCategory("Interface");
									errorsPerc.setEntity("InErrorsPerc");
									errorsPerc.setSubentity(sIndex);
									errorsPerc.setRestype("dynamic");
									errorsPerc.setUnit("%");
									errorsPerc.setChname("入口错误率");
									double inerrorsperc=0.0;
									if (allinpacks==0){
										inerrorsperc=0;
									}else{
										if (allinpacks-lastpacks > 0){
											inerrorsperc=(inerrors-lasterrors)/(allinpacks-lastpacks);
										}else{
											inerrorsperc=0;
										}
										
									}									
									errorsPerc.setThevalue(Double.toString(inerrorsperc));
									errorspercVector.add(errorsPerc);
												
									lastvalue="";
									lastpacks=0;
									lastdiscards=0;
									lasterrors=0;
									//出口传输数据包
									if (packhash != null){
										if(packhash.get("AllOutCastPkts"+":"+sIndex)!=null)lastvalue=packhash.get("AllOutCastPkts"+":"+sIndex).toString();
									}
									
									//取得上次获得的packs
									if(lastvalue!=null && !lastvalue.equals("")){
										lastpacks=Long.parseLong(lastvalue);																		
									}
									packs = new Packs();
									packs.setIpaddress(host.getIpAddress());
									packs.setCollecttime(date);
									packs.setCategory("Interface");
									packs.setEntity("OutCastPkts");
									packs.setSubentity(sIndex);
									packs.setRestype("dynamic");
									packs.setUnit("个");
									packs.setChname("出口数据包");
									if (lastpacks>0){
										packs.setThevalue(alloutpacks-lastpacks+"");
									}else{
										packs.setThevalue("0");
									}
									
									packsVector.add(packs);
												
												
									//计算丢包率和错误率
									if (discardshash != null){
										if(discardshash.get("AllOutDiscards"+":"+sIndex)!=null)lastvalue=discardshash.get("AllOutDiscards"+":"+sIndex).toString();
									}									
									//取得上次获得的packs
									if(lastvalue!=null && !lastvalue.equals("")){
										lastdiscards=Long.parseLong(lastvalue);									
									}																		
									discardsPerc = new DiscardsPerc();
									discardsPerc.setIpaddress(host.getIpAddress());
									discardsPerc.setCollecttime(date);
									discardsPerc.setCategory("Interface");
									discardsPerc.setEntity("OutDiscardsPerc");
									discardsPerc.setSubentity(sIndex);
									discardsPerc.setRestype("dynamic");
									discardsPerc.setUnit("%");
									discardsPerc.setChname("出口丢包率");
									double outdiscardserc=0.0;
									if (alloutpacks==0){
										outdiscardserc = 0;
									}else{
										if (alloutpacks-lastpacks>0){
											outdiscardserc = (outdiscards-lastdiscards)/(alloutpacks-lastpacks);
										}else{
											outdiscardserc = 0;
										}
										
									}
												
									discardsPerc.setThevalue(Double.toString(outdiscardserc));
									discardspercVector.add(discardsPerc);
									
									lastvalue="";
									if (errorshash != null){
										if(errorshash.get("AllOutErrors"+":"+sIndex)!=null)lastvalue=errorshash.get("AllOutErrors"+":"+sIndex).toString();
									}									
									//取得上次获得的packs
									if(lastvalue!=null && !lastvalue.equals("")){
										lasterrors=Long.parseLong(lastvalue);									
									}																											
									errorsPerc = new  ErrorsPerc();
									errorsPerc.setIpaddress(host.getIpAddress());
									errorsPerc.setCollecttime(date);
									errorsPerc.setCategory("Interface");
									errorsPerc.setEntity("OutErrorsPerc");
									errorsPerc.setSubentity(sIndex);
									errorsPerc.setRestype("dynamic");
									errorsPerc.setUnit("%");
									errorsPerc.setChname("出口错误率");
									double outerrorsperc=0.0;
									if (alloutpacks>0){
										if ((alloutpacks-lastpacks)>0){
											outerrorsperc=(outerrors-lasterrors)/(alloutpacks-lastpacks);
										}else{
											outerrorsperc=0;
										}
										
									}									
									errorsPerc.setThevalue(Double.toString(outerrorsperc));
									errorspercVector.add(errorsPerc);									
												
									/* 添加到内存里*/
									ShareData.setPacksdata(host.getIpAddress()+":"+sIndex,hasht);
									ShareData.setDiscardsdata(host.getIpAddress()+":"+sIndex,tempDiscards);
									ShareData.setErrorsdata(host.getIpAddress()+":"+sIndex,errHash);
												
									/*
									//当前端口综合丢包率
									AllDiscardsPerc alldiscardsperc = new AllDiscardsPerc();
									alldiscardsperc.setIpaddress(nethost);
									alldiscardsperc.setCollecttime(date);
									alldiscardsperc.setCategory("Interface");
									alldiscardsperc.setEntity("AllDiscardsPerc");
									alldiscardsperc.setSubentity(sIndex);
									alldiscardsperc.setRestype("dynamic");
									alldiscardsperc.setUnit("%");
									alldiscardsperc.setChname("综合丢包率");
									double alldiscards_perc=0.0;
									if ((allinpacks+alloutpacks)>0)
										alldiscards_perc=(indiscards+outdiscards)/(allinpacks+alloutpacks);
									alldiscardsperc.setThevalue(Double.toString(alldiscards_perc));
									alldiscardspercVector.add(alldiscardsperc);									
									*/
									/*
									//当前端口综合错误率
									AllErrorsPerc allerrorsperc = new AllErrorsPerc();
									allerrorsperc.setIpaddress(nethost);
									allerrorsperc.setCollecttime(date);
									allerrorsperc.setCategory("Interface");
									allerrorsperc.setEntity("AllErrorsPerc");
									allerrorsperc.setSubentity(sIndex);
									allerrorsperc.setRestype("dynamic");
									allerrorsperc.setUnit("%");
									allerrorsperc.setChname("综合错误率");
									double allerrors_perc=0.0;
									if ((allinpacks+alloutpacks)>0)
										allerrors_perc=(inerrors+outerrors)/(allinpacks+alloutpacks);
									allerrorsperc.setThevalue(Double.toString(allerrors_perc));
									allerrorspercVector.add(allerrorsperc);
									*/
												
								} //end for contains
										
							}
							}
							allutilhdx = new AllUtilHdx();
							allutilhdx.setIpaddress(host.getIpAddress());
							allutilhdx.setCollecttime(date);
							allutilhdx.setCategory("Interface");
							allutilhdx.setEntity("AllInBandwidthUtilHdx");
							allutilhdx.setSubentity("AllInBandwidthUtilHdx");
							allutilhdx.setRestype("dynamic");
							allutilhdx.setUnit(unit1[0]+"/秒");	
							allutilhdx.setChname("入口流速");
							
							allutilhdx.setThevalue(Long.toString(allInOctetsSpeed*8));	
							allutilhdxVector.addElement(allutilhdx);	
							
							/*
							allutilhdxperc = new AllUtilHdxPerc();
							allutilhdxperc.setIpaddress(nethost);
							allutilhdxperc.setCollecttime(date);
							allutilhdxperc.setCategory("Interface");
							allutilhdxperc.setEntity("AllInBandwidthUtilHdx");
							allutilhdxperc.setSubentity("AllInBandwidthUtilHdxPerc");
							allutilhdxperc.setRestype("static");
							allutilhdxperc.setUnit("%");	
							allutilhdxperc.setChname("入口带宽利用率");
							double lInUsagePerc=0;
							if(allSpeed>0){
								lInUsagePerc=Arith.div(allInOctetsSpeed*800.0,allSpeed);
								//lInUsagePerc=allInOctetsSpeed*800.0/allSpeed;
								if(lInUsagePerc>95)lInUsagePerc=95;
							}
							allutilhdxperc.setThevalue(Double.toString(lInUsagePerc));	
							allutilhdxpercVector.addElement(allutilhdxperc);	
							*/
							//out
							
							allutilhdx = new AllUtilHdx();
							allutilhdx.setIpaddress(host.getIpAddress());
							allutilhdx.setCollecttime(date);
							allutilhdx.setCategory("Interface");
							allutilhdx.setEntity("AllOutBandwidthUtilHdx");
							allutilhdx.setSubentity("AllOutBandwidthUtilHdx");
							allutilhdx.setRestype("dynamic");
							allutilhdx.setUnit(unit1[0]+"/秒");
							allutilhdx.setChname("出口流速");	
							allutilhdx.setThevalue(Long.toString(allOutOctetsSpeed*8));	
							allutilhdxVector.addElement(allutilhdx);	
							
							/*
							allutilhdxperc = new AllUtilHdxPerc();
							allutilhdxperc.setIpaddress(nethost);
							allutilhdxperc.setCollecttime(date);
							allutilhdxperc.setCategory("Interface");
							allutilhdxperc.setEntity("AllOutBandwidthUtilHdx");
							allutilhdxperc.setSubentity("AllOutBandwidthUtilHdxPerc");
							allutilhdxperc.setRestype("static");
							allutilhdxperc.setUnit("%");	
							allutilhdxperc.setChname("出口带宽利用率");
							double lOutUsagePerc=0;
							if(allSpeed>0){
								lOutUsagePerc=Arith.div(allOutOctetsSpeed*800.0,allSpeed);
								//lOutUsagePerc=allOutOctetsSpeed*800.0/allSpeed;
								if(lOutUsagePerc>96)lOutUsagePerc=96;
							}
							allutilhdxperc.setThevalue(Double.toString(lOutUsagePerc));	
							allutilhdxpercVector.addElement(allutilhdxperc);
							*/	
							
							allutilhdx = new AllUtilHdx();
							allutilhdx.setIpaddress(host.getIpAddress());
							allutilhdx.setCollecttime(date);
							allutilhdx.setCategory("Interface");
							allutilhdx.setEntity("AllBandwidthUtilHdx");
							allutilhdx.setSubentity("AllBandwidthUtilHdx");
							allutilhdx.setRestype("static");
							allutilhdx.setUnit(unit1[0]+"/秒");	
							allutilhdx.setChname("综合流速");
							allutilhdx.setThevalue(Long.toString(allOctetsSpeed));	
							allutilhdxVector.addElement(allutilhdx);	

							/*
							allutilhdxperc = new AllUtilHdxPerc();
							allutilhdxperc.setIpaddress(nethost);
							allutilhdxperc.setCollecttime(date);
							allutilhdxperc.setCategory("Interface");
							allutilhdxperc.setEntity("AllBandwidthUtilHdx");
							allutilhdxperc.setSubentity("AllBandwidthUtilHdxPerc");
							allutilhdxperc.setRestype("static");
							allutilhdxperc.setUnit("%");	
							allutilhdxperc.setChname("综合带宽利用率");
							double lAllUsagePerc=0;
							if(allSpeed>0){
								lAllUsagePerc=allOctetsSpeed*800.0/allSpeed;
								if(lAllUsagePerc>196)lAllUsagePerc=196;
							 }
							allutilhdxperc.setThevalue(Double.toString(lAllUsagePerc));	
							allutilhdxpercVector.addElement(allutilhdxperc);	
							*/
							String flag ="0";
							//hash=null;
							hashSpeed=null;
							octetsHash.put("collecttime",date);	
							if (hash != null){					
								flag = (String)hash.get("flag");
								if (flag == null){
									flag="0";
								}else{
									if (flag.equals("0")){
										flag = "1";
									}else{
										flag = "0";
									}
								}
							}				
							octetsHash.put("flag",flag);
							ShareData.setOctetsdata(host.getIpAddress(),octetsHash);				
						}catch(Exception e){e.printStackTrace();}
						//-----------------------------------------------------interface end
						
						//-----------------------------------------------------interface pkts start			
						  try{
							I_HostLastCollectData lastCollectDataManager=new HostLastCollectDataManager();
							Hashtable hash=ShareData.getPksdata(host.getIpAddress());
							//取得轮询间隔时间
							TaskXml taskxml=new TaskXml();
							Task task=taskxml.GetXml("netcollecttask");
							int interval=getInterval(task.getPolltime().floatValue(),task.getPolltimeunit());
							Hashtable hashSpeed=new Hashtable();
							Hashtable pksHash = new Hashtable();
							if (hash==null)hash=new Hashtable();
									  String[] oids1 =                
										  new String[] {               
											"1.3.6.1.2.1.2.2.1.1", 
											"1.3.6.1.2.1.31.1.1.1.2",//ifInMulticastPkts 接收的多播数据包数目
											"1.3.6.1.2.1.31.1.1.1.3",//ifInBroadcastPkts 接收的广播数据包数目
											"1.3.6.1.2.1.31.1.1.1.4",//ifOutMulticastPkts 发送的多播数据包数目
											"1.3.6.1.2.1.31.1.1.1.5" //ifOutBroadcastPkts 发送的广播数据包数目				
											  };				 
											
							
							final String[] desc=SnmpMibConstants.NetWorkMibInterfaceDesc3;

							String[][] valueArray1 = null;   	  
							try {
								valueArray1 = snmp.getTableData(host.getIpAddress(),host.getCommunity(),oids1);
							} catch(Exception e){
								valueArray1 = null;
								e.printStackTrace();
								SysLogger.error(host.getIpAddress() + "_NetscreenSnmp");
							}
							
							long allinpacks=0;
							long InMultiPks=0;
							long InBroadPks=0;
							long OutMultiPks=0;
							long OutBroadPks=0;
							long alloutpacks=0;
						
							Vector tempV=new Vector();
							Hashtable tempHash = new Hashtable();
							if(valueArray1!= null){
							for(int i=0;i<valueArray1.length;i++){
								allinpacks=0;
								InMultiPks=0;//入口单向和
								InBroadPks=0;//非单向
								OutMultiPks=0;
								OutBroadPks=0;
								alloutpacks=0;
																					
								if(valueArray1[i][0] == null)continue;
								String sIndex = valueArray1[i][0];
								//if (tempV.contains(sIndex)){
								if (sIndex != null && sIndex.trim().length()>0){
													
									for(int j=0;j<5;j++){																														
										if(valueArray1[i][j]!=null){
											String sValue=valueArray1[i][j];
											//if (j==6)continue;
											interfacedata=new Interfacecollectdata();
											interfacedata.setThevalue(sValue);
											if (j==1 || j==2){
												//接收的多播数据包数目,接收的广播数据包数目												
												if (sValue != null){
													allinpacks=allinpacks+Long.parseLong(sValue);
													
													Calendar cal=(Calendar)hash.get("collecttime");
													long timeInMillis=0;
													if(cal!=null)timeInMillis=cal.getTimeInMillis();
													long longinterval=(date.getTimeInMillis()-timeInMillis)/1000;
													
													inpacks=new InPkts();
													inpacks.setIpaddress(host.getIpAddress());
													inpacks.setCollecttime(date);
													inpacks.setCategory("Interface");
													String chnameBand="";
													if(j==1){
														inpacks.setEntity("ifInMulticastPkts");
														chnameBand="多播";
													}
													if(j==2){
														inpacks.setEntity("ifInBroadcastPkts");
														chnameBand="广播";
													}
													inpacks.setSubentity(sIndex);
													inpacks.setRestype("dynamic");
													inpacks.setUnit("");	
													inpacks.setChname(chnameBand);
													long currentPacks=Long.parseLong(sValue);												
													long lastPacks=0;	
													long l=0;											
															
													//如果当前采集时间与上次采集时间的差小于采集间隔两倍，则计算带宽利用率，否则带宽利用率为0；
													if(longinterval<2*interval){
														String lastvalue="";
														
														if(hash.get(desc[j]+":"+sIndex)!=null)lastvalue=hash.get(desc[j]+":"+sIndex).toString();
														//取得上次获得的Octets
														if(lastvalue!=null && !lastvalue.equals(""))lastPacks=Long.parseLong(lastvalue);
													}
										
													if(longinterval!=0){
														if(currentPacks<lastPacks){
															currentPacks=currentPacks+4294967296L;
														}
														//现流量-前流量
														//SysLogger.info(host.getIpAddress()+"==="+sIndex+"断口==="+currentPacks+"===="+lastPacks);
														long octetsBetween=currentPacks-lastPacks;
														l=octetsBetween;
														if(lastPacks == 0)l=0;
													}
													inpacks.setThevalue(Long.toString(l));	
													//SysLogger.info(host.getIpAddress()+" 第"+utilhdx.getSubentity()+"端口 "+"Speed "+Long.toString(l*8));
													if (cal != null)
														inpksVector.addElement(inpacks);	
												}
													//continue;
											}	
											if (j==3 || j==4){
												//发送的多播数据包数目,发送的广播数据包数目
												if (sValue != null){
													alloutpacks=alloutpacks+Long.parseLong(sValue);
													
													Calendar cal=(Calendar)hash.get("collecttime");
													long timeInMillis=0;
													if(cal!=null)timeInMillis=cal.getTimeInMillis();
													long longinterval=(date.getTimeInMillis()-timeInMillis)/1000;
													
													outpacks=new OutPkts();
													outpacks.setIpaddress(host.getIpAddress());
													outpacks.setCollecttime(date);
													outpacks.setCategory("Interface");
													String chnameBand="";
													
													if(j==3){
														outpacks.setEntity("ifOutMulticastPkts");
														chnameBand="多播";
													}
													if(j==4){
														outpacks.setEntity("ifOutBroadcastPkts");
														chnameBand="广播";
													}
													outpacks.setSubentity(sIndex);
													outpacks.setRestype("dynamic");
													outpacks.setUnit("");	
													outpacks.setChname(chnameBand);
													long currentPacks=Long.parseLong(sValue);												
													long lastPacks=0;	
													long l=0;											
															
													//如果当前采集时间与上次采集时间的差小于采集间隔两倍，则计算带宽利用率，否则带宽利用率为0；
													if(longinterval<2*interval){
														String lastvalue="";
														
														if(hash.get(desc[j]+":"+sIndex)!=null)lastvalue=hash.get(desc[j]+":"+sIndex).toString();
														//取得上次获得的Octets
														if(lastvalue!=null && !lastvalue.equals(""))lastPacks=Long.parseLong(lastvalue);
													}
										
													if(longinterval!=0){
														if(currentPacks<lastPacks){
															currentPacks=currentPacks+4294967296L;
														}
														//现流量-前流量	
														long octetsBetween=currentPacks-lastPacks;
														l=octetsBetween;
														if(lastPacks == 0)l=0;
													}
													outpacks.setThevalue(Long.toString(l));	
													//SysLogger.info(host.getIpAddress()+" 第"+utilhdx.getSubentity()+"端口 "+"Speed "+Long.toString(l*8));
													if (cal != null)
														outpksVector.addElement(outpacks);
													
												}
												//continue;
											}														
											//SysLogger.info(host.getIpAddress()+"==="+desc1[j]+":"+sIndex+"===="+sValue);
											pksHash.put(desc[j]+":"+sIndex,interfacedata.getThevalue());
										} //valueArray1[i][j]==null
									} //end for j																																		
								} //end for contains
										
							}
							}	
								
							String flag ="0";
							//hash=null;
							hashSpeed=null;
							pksHash.put("collecttime",date);	
							if (hash != null){					
								flag = (String)hash.get("flag");
								if (flag == null){
									flag="0";
								}else{
									if (flag.equals("0")){
										flag = "1";
									}else{
										flag = "0";
									}
								}
							}				
							pksHash.put("flag",flag);
							ShareData.setPksdata(host.getIpAddress(),pksHash);				
						}catch(Exception e){e.printStackTrace();}
						//------------------------------------------------interface pkts end
						
						//-------------------------------------------------Tos vlan table start	
						try{
							
							String[] oids =                
								new String[] {               
									"1.3.6.1.4.1.14331.5.5.1.9.1.1",//Index
									"1.3.6.1.4.1.14331.5.5.1.9.1.2",//ARP dev name
									"1.3.6.1.4.1.14331.5.5.1.9.1.3",//ARP IP address
									"1.3.6.1.4.1.14331.5.5.1.9.1.4",//ARP Mac address
									"1.3.6.1.4.1.14331.5.5.1.9.1.5"}; //ARP status
							
							String[][] valueArray = null; 
							String index="";
							String devname="";
							String ipaddress="";
							String mac="";
							String arpstatus = "";
							
							try {
								valueArray = snmp.getTableData(host.getIpAddress(),host.getCommunity(),oids);
							} catch(Exception e){
								valueArray = null;
								SysLogger.error(host.getIpAddress() + "_TopsecSnmp");
							}
							if(valueArray != null){
							   	  for(int i=0;i<valueArray.length;i++)
							   	  {
							   		  //if(valueArray[i][0] != null && valueArray[i][1] != null && valueArray[i][2] != null 
							   			//	  && valueArray[i][3] != null && valueArray[i][4] != null){
							   			try{
							   				index=valueArray[i][0];//index
							   				devname=valueArray[i][1];//devname
							   				ipaddress=valueArray[i][2];//ipaddress
							   				mac=valueArray[i][3];//mac
							   				arpstatus=valueArray[i][4];//arpstatus
							   				Hashtable arp = new Hashtable();
							   				arp.put("index", index);
							   				arp.put("devname", devname);
							   				arp.put("ipaddress", ipaddress);
							   				arp.put("mac", mac);
							   				arp.put("arpstatus", arpstatus);
							   				arps.add(arp);
							   			  }catch(Exception e){
							   				 e.printStackTrace();
							   			  }
							   			  
							   		 // }
							   		 
							   	  }
							}
						}catch(Exception e){
								e.printStackTrace();
						}
				
						//--------------------------------------------------------Tos vlan table end
						
						//-------------------------------------------------Tos ARP table start	
						try{
							
							String[] oids =                
								new String[] {               
									"1.3.6.1.4.1.3375.2.2.5.1.2.1.1",//name
									"1.3.6.1.4.1.3375.2.2.5.1.2.1.17",//User name
									"1.3.6.1.4.1.3375.2.2.5.1.2.1.18",//User login IP
									"1.3.6.1.4.1.3375.2.2.5.1.2.1.21"}; //User login time
							
							String[][] valueArray = null; 
							String index="";
							String valnid="";
							String status="";
							String ports="";
							
							try {
								valueArray = snmp.getTableData(host.getIpAddress(),host.getCommunity(),oids);
							} catch(Exception e){
								valueArray = null;
								SysLogger.error(host.getIpAddress() + "_TopsecSnmp");
							}
							if(valueArray != null){
							   	  for(int i=0;i<valueArray.length;i++)
							   	  {
//							   		  if(valueArray[i][0] != null && valueArray[i][1] != null && valueArray[i][2] != null 
//							   				  && valueArray[i][3] != null && valueArray[i][4] != null){
							   			try{
							   				index=valueArray[i][0];//index
							   				valnid=valueArray[i][1];//valnid
							   				status=valueArray[i][2];//status
							   				ports=valueArray[i][3];//ports
							   				Hashtable vlan = new Hashtable();
							   				vlan.put("index", index);
							   				vlan.put("valnid", valnid);
							   				vlan.put("status", status);
							   				vlan.put("ports", ports);
							   				vlans.add(vlan);
							   			  }catch(Exception e){
							   				 e.printStackTrace();
							   			  }
							   			  
							   		  }
							   		 
							   	  }
				//			}
						}catch(Exception e){
								e.printStackTrace();
						}
				
						//--------------------------------------------------------Tos ARP table end
						
						//-------------------------------------------------Tos ARP proxy table start	
						try{
							
							String[] oids =                
								new String[] {               
									"1.3.6.1.4.1.14331.5.5.1.10.1.1",//Index
									"1.3.6.1.4.1.14331.5.5.1.10.1.2",//ARP max IP address
									"1.3.6.1.4.1.14331.5.5.1.10.1.3",//ARP min IP address
									"1.3.6.1.4.1.14331.5.5.1.10.1.4",//ARP HWaddress
									"1.3.6.1.4.1.14331.5.5.1.10.1.5"}; //ARP proxy status
							
							String[][] valueArray = null; 
							String index="";
							String maxip="";
							String minip="";
							String hwaddress="";
							String proxystatus = "";
							
							try {
								valueArray = snmp.getTableData(host.getIpAddress(),host.getCommunity(),oids);
							} catch(Exception e){
								valueArray = null;
								SysLogger.error(host.getIpAddress() + "_TopsecSnmp");
							}
							if(valueArray != null){
							   	  for(int i=0;i<valueArray.length;i++)
							   	  {
							   		  //if(valueArray[i][0] != null && valueArray[i][1] != null && valueArray[i][2] != null 
							   			//	  && valueArray[i][3] != null && valueArray[i][4] != null){
							   			try{
							   				index=valueArray[i][0];//index
							   				maxip=valueArray[i][1];//maxip
							   				minip=valueArray[i][2];//minip
							   				hwaddress=valueArray[i][3];//hwaddress
							   				proxystatus=valueArray[i][4];//proxystatus
							   				Hashtable arp = new Hashtable();
							   				arp.put("index", index);
							   				arp.put("maxip", maxip);
							   				arp.put("minip", minip);
							   				arp.put("hwaddress", hwaddress);
							   				arp.put("proxystatus", proxystatus);
							   				arpproxy.add(arp);
							   			  }catch(Exception e){
							   				 e.printStackTrace();
							   			  }
							   			  
							   		  //}
							   		 
							   	  }
							}
						}catch(Exception e){
								e.printStackTrace();
						}
				
						//--------------------------------------------------------Tos ARP proxy table end
						
						
						//-------------------------------------------------Tos user table start	
						try{
							
							String[] oids =                
								new String[] {               
									"1.3.6.1.4.1.3375.2.2.8.1.2.1.1",//name
									"1.3.6.1.4.1.3375.2.2.8.1.2.1.2",//User name
									"1.3.6.1.4.1.3375.2.2.8.1.2.1.3"};//User login IP
							
							String[][] valueArray = null; 
							String index="";
							String username="";
							String loginip="";
							
							try {
								valueArray = snmp.getTableData(host.getIpAddress(),host.getCommunity(),oids);
							} catch(Exception e){
								valueArray = null;
								SysLogger.error(host.getIpAddress() + "_TopsecSnmp");
							}
							if(valueArray != null){
							   	  for(int i=0;i<valueArray.length;i++)
							   	  {
							   		  //if(valueArray[i][0] != null && valueArray[i][1] != null && valueArray[i][2] != null && valueArray[i][3] != null){
							   			try{
							   				index=valueArray[i][0];//index
							   				username=valueArray[i][1];//username
							   				loginip=valueArray[i][2];//loginip
							   				Hashtable users = new Hashtable();
							   				users.put("index", index);
							   				users.put("username", username);
							   				users.put("loginip", loginip);
							   				userlogin.add(users);
							   			  }catch(Exception e){
							   				 e.printStackTrace();
							   			  }
							   			  
							   		  //}
							   		 
							   	  }
							}
						}catch(Exception e){
								e.printStackTrace();
						}
				
						//--------------------------------------------------------Tos user table end
						
						
						
						
			}
			catch(Exception e){
				e.printStackTrace();
			}
			finally{
			}
				if (systemVector != null && systemVector.size()>0){
					returnHas.put("system",systemVector);
				}
				//if (memoryVector != null && memoryVector.size()>0)returnHas.put("memory",memoryVector);
				if (cpuVector != null && cpuVector.size()>0)returnHas.put("cpu",cpuVector);
				if (policys!=null && policys.size()>0)returnHas.put("policys", policys);
				if (interfaceVector != null && interfaceVector.size()>0)returnHas.put("interface",interfaceVector);		
				if (allutilhdxpercVector != null && allutilhdxpercVector.size()>0)returnHas.put("allutilhdxperc",allutilhdxpercVector);
				if (allutilhdxVector != null && allutilhdxVector.size()>0)returnHas.put("allutilhdx",allutilhdxVector);
				if (utilhdxpercVector != null && utilhdxpercVector.size()>0)returnHas.put("utilhdxperc",utilhdxpercVector);
				if (utilhdxVector != null && utilhdxVector.size()>0)returnHas.put("utilhdx",utilhdxVector);		
				//if (inpacksVector != null && inpacksVector.size()>0)returnHas.put("inpacks",inpacksVector);	
				//if (outpacksVector != null && outpacksVector.size()>0)returnHas.put("outpacks",outpacksVector);	
				if (inpksVector != null && inpksVector.size()>0)returnHas.put("inpacks",inpksVector);	
				if (outpksVector != null && outpksVector.size()>0)returnHas.put("outpacks",outpksVector);
				if (discardspercVector != null && discardspercVector.size()>0)returnHas.put("discardsperc",discardspercVector);
				if (errorspercVector != null && errorspercVector.size()>0)returnHas.put("errorsperc",errorspercVector);
				if (allerrorspercVector != null && allerrorspercVector.size()>0)returnHas.put("allerrorsperc",allerrorspercVector);
				if (alldiscardspercVector != null && alldiscardspercVector.size()>0)returnHas.put("alldiscardsperc",alldiscardspercVector);
				if (packsVector != null && packsVector.size()>0)returnHas.put("packs",packsVector);
				if (ipmacVector != null && ipmacVector.size()>0)returnHas.put("ipmac",ipmacVector);
				if (iprouterVector != null && iprouterVector.size()>0)returnHas.put("iprouter",iprouterVector);
				if (ifEntityList != null && ifEntityList.size()>0)returnHas.put("ifentitylist",ifEntityList);
				if (fdbVector != null && fdbVector.size()>0)returnHas.put("fdb",fdbVector);
				if (memoryVector != null && memoryVector.size()>0)returnHas.put("memory",memoryVector);
				if (conns != null && conns.size()>0)returnHas.put("conns",conns);
				if (userlogin != null && userlogin.size()>0)returnHas.put("userlogin",userlogin);
				if (arpproxy != null && arpproxy.size()>0)returnHas.put("arpproxy",arpproxy);
				if (arps != null && arps.size()>0)returnHas.put("arps",arps);
				if (vlans != null && vlans.size()>0)returnHas.put("vlans",vlans);
				return returnHas;
	   }
	   public void run(){
		   //collectData();
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
}





