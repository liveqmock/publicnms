package com.afunms.polling.snmp.interfaces;

/*
 * @author hukelei@dhcc.com.cn
 *
 */

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.common.util.Arith;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.common.util.SysLogger;
import com.afunms.config.dao.NodeToBusinessDao;
import com.afunms.config.dao.ProcsDao;
import com.afunms.config.model.NodeToBusiness;
import com.afunms.config.model.Procs;
import com.afunms.event.dao.EventListDao;
import com.afunms.event.model.EventList;
import com.afunms.monitor.executor.base.MonitorInterface;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostLastCollectData;
import com.afunms.polling.base.Node;
import com.afunms.polling.impl.HostLastCollectDataManager;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.AllUtilHdx;
import com.afunms.polling.om.CPUcollectdata;
import com.afunms.polling.om.Devicecollectdata;
import com.afunms.polling.om.Diskcollectdata;
import com.afunms.polling.om.Interfacecollectdata;
import com.afunms.polling.om.IpMac;
import com.afunms.polling.om.Memorycollectdata;
import com.afunms.polling.om.Processcollectdata;
import com.afunms.polling.om.Servicecollectdata;
import com.afunms.polling.om.Softwarecollectdata;
import com.afunms.polling.om.Storagecollectdata;
import com.afunms.polling.om.Systemcollectdata;
import com.afunms.polling.om.Task;
import com.afunms.polling.om.Usercollectdata;
import com.afunms.polling.om.UtilHdx;
import com.afunms.polling.snmp.SnmpMibConstants;
import com.afunms.polling.task.TaskXml;
import com.afunms.topology.model.HostNode;


/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class UtilHdxSnmp extends SnmpMonitor {
	private Hashtable sendeddata = ShareData.getProcsendeddata();
	
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
	public UtilHdxSnmp() {
	}

	   public void collectData(Node node,MonitoredItem item){
		   
	   }
	   public void collectData(HostNode node){
		   
	   }
	/* (non-Javadoc)
	 * @see com.dhcc.webnms.host.snmp.AbstractSnmp#collectData()
	 */
	public Hashtable collect_Data(AlarmIndicatorsNode alarmIndicatorsNode) {
		Hashtable returnHash=new Hashtable();
		Vector interfaceVector=new Vector();
		Vector utilhdxVector = new Vector();
		Vector allutilhdxVector = new Vector();
		AllUtilHdx allutilhdx = new AllUtilHdx();
		Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		
		try {
			Interfacecollectdata interfacedata=null;
			UtilHdx utilhdx=new UtilHdx();
			Calendar date=Calendar.getInstance();
			Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(host.getIpAddress());
			if(ipAllData == null)ipAllData = new Hashtable();
			
			  try{
				  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				  com.afunms.polling.base.Node snmpnode = (com.afunms.polling.base.Node)PollingEngine.getInstance().getNodeByIP(host.getIpAddress());
				  Date cc = date.getTime();
				  String time = sdf.format(cc);
				  snmpnode.setLastTime(time);
			  }catch(Exception e){
				  
			  }
			//-------------------------------------------------------------------------------------------interface start			
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
								"1.3.6.1.2.1.2.2.1.1", 
								"1.3.6.1.2.1.2.2.1.2",
								"1.3.6.1.2.1.2.2.1.3",//ifType
								"1.3.6.1.2.1.2.2.1.4",
								"1.3.6.1.2.1.2.2.1.5",
								"1.3.6.1.2.1.2.2.1.6",
								"1.3.6.1.2.1.2.2.1.7",//ifAdminStatus 6
								"1.3.6.1.2.1.2.2.1.8",//ifOperStatus 7
								"1.3.6.1.2.1.2.2.1.9",//ifLastChange 8
								"1.3.6.1.2.1.31.1.1.1.1",				
								  };
				String[] oids1=                
					 new String[] {     
					//"1.3.6.1.2.1.2.2.1.1",	
					"1.3.6.1.2.1.2.2.1.10",  //ifInOctets 1        
					"1.3.6.1.2.1.2.2.1.11",//ifInUcastPkts 2
					"1.3.6.1.2.1.2.2.1.12",//ifInNUcastPkts 3
					"1.3.6.1.2.1.2.2.1.13",//ifInDiscards 4
					"1.3.6.1.2.1.2.2.1.14",//ifInErrors 5
					"1.3.6.1.2.1.2.2.1.16", //ifOutOctets 6
					"1.3.6.1.2.1.2.2.1.17",//ifOutUcastPkts 7
					"1.3.6.1.2.1.2.2.1.18",//ifOutNUcastPkts 8
					"1.3.6.1.2.1.2.2.1.19",	//ifOutDiscards 9
					"1.3.6.1.2.1.2.2.1.20"//ifOutErrors 10								
					};				 
								
				
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
					//valueArray = snmp.getTableData(host.getIpAddress(),host.getCommunity(),oids);
					valueArray = SnmpUtils.getTableData(host.getIpAddress(),host.getCommunity(),oids,host.getSnmpversion(),
		   		  			host.getSecuritylevel(),host.getSecurityName(),host.getV3_ap(),host.getAuthpassphrase(),host.getV3_privacy(),host.getPrivacyPassphrase(),3,1000*30);
				} catch(Exception e){
					valueArray = null;
					//e.printStackTrace();
					//SysLogger.error(host.getIpAddress() + "_CiscoSnmp");
				}
				String[][] valueArray1 = null;   	  
				try {
					valueArray1 = snmp.getTableData(host.getIpAddress(),host.getCommunity(),oids1);
				} catch(Exception e){
					valueArray1 = null;
					e.printStackTrace();
					SysLogger.error(host.getIpAddress() + "_CiscoSnmp");
				}
			
				long allSpeed=0;
				long allOutOctetsSpeed=0;
				long allInOctetsSpeed=0;
				long allOctetsSpeed=0;
				
				long allinpacks=0;
				long indiscards=0;
				long inerrors=0;
				long alloutpacks=0;
				long outdiscards=0;
				long outerrors=0;
				long alldiscards=0;
				long allerrors=0;
				long allpacks=0;
			
				Vector tempV=new Vector();
				Hashtable tempHash = new Hashtable();
				
			   	  for(int i=0;i<valueArray.length;i++)
			   	  {				   		  
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
		
									if (ifEntity_ifStatus.get(sValue) != null){
										interfacedata.setThevalue(ifEntity_ifStatus.get(sValue).toString());
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
				
				for(int i=0;i<valueArray1.length;i++){
					allinpacks=0;
					indiscards=0;
					inerrors=0;
					alloutpacks=0;
					outdiscards=0;
					outerrors=0;																		

					String sIndex = (String)tempHash.get(i);				
					if (tempV.contains(sIndex)){
										
						for(int j=0;j<10;j++){																														
							if(valueArray1[i][j]!=null){
								String sValue=valueArray1[i][j];
								//if (j==6)continue;
								if (j==1 || j==2){
									//入口单向传输数据包,入口非单向传输数据包												
									if (sValue != null) allinpacks=allinpacks+Long.parseLong(sValue);
										continue;
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
									if (sValue != null) alloutpacks=alloutpacks+Long.parseLong(sValue);
									continue;
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
								interfacedata=new Interfacecollectdata();
								if(scale1[j]==0){
									interfacedata.setThevalue(sValue);
								}
								else{
									interfacedata.setThevalue(Long.toString(Long.parseLong(sValue)/scale1[j]));
								}
											
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
									//SysLogger.info(host.getIpAddress()+" 第"+utilhdx.getSubentity()+"端口===== "+sValue);
									long currentOctets=Long.parseLong(sValue)/scale1[j];												
									long lastOctets=0;	
									long l=0;											
											
									//如果当前采集时间与上次采集时间的差小于采集间隔两倍，则计算带宽利用率，否则带宽利用率为0；
									if(longinterval<2*interval){
										String lastvalue="";
										
										if(hash.get(desc1[j]+":"+sIndex)!=null)lastvalue=hash.get(desc1[j]+":"+sIndex).toString();
										//取得上次获得的Octets
										if(lastvalue!=null && !lastvalue.equals(""))lastOctets=Long.parseLong(lastvalue);
									}
						
									if(longinterval!=0){
										if(currentOctets<lastOctets){
											currentOctets=currentOctets+4294967296L/scale1[j];
										}
										//现流量-前流量	
										long octetsBetween=currentOctets-lastOctets;
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
												
												
								} //end j=0 j=5
								octetsHash.put(desc1[j]+":"+sIndex,interfacedata.getThevalue());
							} //valueArray1[i][j]==null
						} //end for j
						//Hashtable packhash=lastCollectDataManager.getLastPacks(nethost);
						Hashtable packhash=ShareData.getPacksdata(host.getIpAddress()+":"+sIndex);
						//Hashtable discardshash=lastCollectDataManager.getLastDiscards(nethost);
						Hashtable discardshash=ShareData.getDiscardsdata(host.getIpAddress()+":"+sIndex);
						//Hashtable errorshash=lastCollectDataManager.getLastErrors(nethost);
						Hashtable errorshash=ShareData.getErrorsdata(host.getIpAddress()+":"+sIndex);									
									
					} //end for contains
							
				}
				allutilhdx = new AllUtilHdx();
				allutilhdx.setIpaddress(host.getIpAddress());
				allutilhdx.setCollecttime(date);
				allutilhdx.setCategory("Interface");
				allutilhdx.setEntity("AllInBandwidthUtilHdx");
				allutilhdx.setSubentity("AllInBandwidthUtilHdx");
				allutilhdx.setRestype("static");
				allutilhdx.setUnit(unit1[0]+"/秒");	
				allutilhdx.setChname("入口流速");
				
				allutilhdx.setThevalue(Long.toString(allInOctetsSpeed*8));	
				allutilhdxVector.addElement(allutilhdx);	
				
				
				allutilhdx = new AllUtilHdx();
				allutilhdx.setIpaddress(host.getIpAddress());
				allutilhdx.setCollecttime(date);
				allutilhdx.setCategory("Interface");
				allutilhdx.setEntity("AllOutBandwidthUtilHdx");
				allutilhdx.setSubentity("AllOutBandwidthUtilHdx");
				allutilhdx.setRestype("static");
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
			}catch(Exception e){
				//e.printStackTrace();
			}
			//-------------------------------------------------------------------------------------------interface end
			}catch(Exception e){
				//returnHash=null;
				//e.printStackTrace();
				//return null;
			}
		
		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(host.getIpAddress());
		if(ipAllData == null)ipAllData = new Hashtable();
		if (allutilhdxVector != null && allutilhdxVector.size()>0)ipAllData.put("allutilhdx",allutilhdxVector);
		if (utilhdxVector != null && utilhdxVector.size()>0)ipAllData.put("utilhdx",utilhdxVector);
		if (interfaceVector != null && interfaceVector.size()>0)ipAllData.put("interface",interfaceVector);
		//ipAllData.put("disk",diskVector);
	    ShareData.getSharedata().put(host.getIpAddress(), ipAllData);
	    
	    
	    returnHash.put("allutilhdx",allutilhdxVector);
	    returnHash.put("utilhdx",utilhdxVector);
	    returnHash.put("interface",interfaceVector);
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
}





