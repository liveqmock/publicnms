package com.afunms.polling.snmp;

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

import com.afunms.common.util.Arith;
import com.afunms.common.util.ShareData;
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
import com.afunms.polling.task.TaskXml;
import com.afunms.topology.model.HostNode;


/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class WindowsSnmp extends SnmpMonitor implements MonitorInterface {
	private static Hashtable ifEntity_ifStatus = null;
	static {
		ifEntity_ifStatus = new Hashtable();
		ifEntity_ifStatus.put("1", "up");
		ifEntity_ifStatus.put("2", "down");
		ifEntity_ifStatus.put("3", "testing");
		ifEntity_ifStatus.put("5", "unknow");
		ifEntity_ifStatus.put("7", "unknow");
	};
	private static Hashtable device_Status = null;
	static {
		device_Status = new Hashtable();
		device_Status.put("1", "未知");
		device_Status.put("2", "运行");
		device_Status.put("3", "告警");
		device_Status.put("4", "测试");
		device_Status.put("5", "停止");
	};
	private static Hashtable device_Type = null;
	static {
		device_Type = new Hashtable();
		device_Type.put("1.3.6.1.2.1.25.3.1.1", "其他");
		device_Type.put("1.3.6.1.2.1.25.3.1.2", "未知");
		device_Type.put("1.3.6.1.2.1.25.3.1.3", "CPU");
		device_Type.put("1.3.6.1.2.1.25.3.1.4", "网络");
		device_Type.put("1.3.6.1.2.1.25.3.1.5", "打印机");
		device_Type.put("1.3.6.1.2.1.25.3.1.6", "磁盘");
		device_Type.put("1.3.6.1.2.1.25.3.1.10", "显卡");
		device_Type.put("1.3.6.1.2.1.25.3.1.11", "声卡");
		device_Type.put("1.3.6.1.2.1.25.3.1.12", "协处理器");
		device_Type.put("1.3.6.1.2.1.25.3.1.13", "键盘");
		device_Type.put("1.3.6.1.2.1.25.3.1.14", "调制解调器");
		device_Type.put("1.3.6.1.2.1.25.3.1.15", "并口");
		device_Type.put("1.3.6.1.2.1.25.3.1.16", "打印口");
		device_Type.put("1.3.6.1.2.1.25.3.1.17", "串口");
		device_Type.put("1.3.6.1.2.1.25.3.1.18", "磁带");
		device_Type.put("1.3.6.1.2.1.25.3.1.19", "时钟");
		device_Type.put("1.3.6.1.2.1.25.3.1.20", "动态内存");
		device_Type.put("1.3.6.1.2.1.25.3.1.21", "固定内存");
	};
	
	private static Hashtable storage_Type = null;
	static {
		storage_Type = new Hashtable();
		storage_Type.put("1.3.6.1.2.1.25.2.1.1", "其他");
		storage_Type.put("1.3.6.1.2.1.25.2.1.2", "物理内存");
		storage_Type.put("1.3.6.1.2.1.25.2.1.3", "虚拟内存");
		storage_Type.put("1.3.6.1.2.1.25.2.1.4", "硬盘");
		storage_Type.put("1.3.6.1.2.1.25.2.1.5", "移动硬盘");
		storage_Type.put("1.3.6.1.2.1.25.2.1.6", "软盘");
		storage_Type.put("1.3.6.1.2.1.25.2.1.7", "光盘");
		storage_Type.put("1.3.6.1.2.1.25.2.1.8", "内存盘");
	};
	//private ProcsDao procsManager=new ProcsDao();
	private  String host="1.1.1.1";
	private Hashtable sendeddata = ShareData.getProcsendeddata();
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 
	 */
	public WindowsSnmp() {
	}

	   public void collectData(Node node,MonitoredItem item){
		   
	   }
	   public void collectData(HostNode node){
		   
	   }
	/* (non-Javadoc)
	 * @see com.dhcc.webnms.host.snmp.AbstractSnmp#collectData()
	 */
	public Hashtable collect_Data(HostNode node) {	
		//yangjun
		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(node.getIpAddress());
		if(ipAllData == null)ipAllData = new Hashtable();
		Hashtable returnHash = new Hashtable();
		//Vector pingVector=new Vector();
		Vector systemVector=new Vector();
		Vector ipmacVector = new Vector();
		Vector memoryVector=new Vector();
		Vector cpuVector=new Vector();
		List cpuList = new ArrayList();
		Vector interfaceVector=new Vector();
		Vector diskVector=new Vector();
		Vector userVector=new Vector();
		Vector processVector=new Vector();
		Vector utilhdxVector=new Vector();
		Vector allutilhdxVector=new Vector();
		Vector softwareVector = new Vector();
		Vector serviceVector = new Vector();
		Vector deviceVector = new Vector();
		Vector storageVector = new Vector();
		HostNode host = (HostNode)node;
		UtilHdx utilhdx=null;
		AllUtilHdx allutilhdx=null;
		Devicecollectdata devicedata = null;
		Storagecollectdata storagedata = null;
		
		try {
			//System.out.println("Start collect data as ip "+host);
			Processcollectdata processdata=null;
			Systemcollectdata systemdata=null;
			Memorycollectdata memorydata=null;
			CPUcollectdata cpudata=null;
			Diskcollectdata diskdata=null;
			Usercollectdata userdata=null;
			Interfacecollectdata interfacedata=null;
			Softwarecollectdata softwaredata = null;
			Servicecollectdata servicedata = null;
			Calendar date=Calendar.getInstance();
			
			  try{
				  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				  com.afunms.polling.base.Node snmpnode = (com.afunms.polling.base.Node)PollingEngine.getInstance().getNodeByIP(node.getIpAddress());
				  Date cc = date.getTime();
				  String time = sdf.format(cc);
				  snmpnode.setLastTime(time);
			  }catch(Exception e){
				  
			  }
			  //-------------------------------------------------------------------------------------------software start
			  try{
			  String[] oids =                
				  new String[] {               
					  "1.3.6.1.2.1.25.6.3.1.2",  //名称
					  "1.3.6.1.2.1.25.6.3.1.3",  //id
					  "1.3.6.1.2.1.25.6.3.1.4",    //类别
					  "1.3.6.1.2.1.25.6.3.1.5" };   //安装日期

			  String[][] valueArray = null;   	  
				try {
					valueArray = snmp.getTableData(host.getIpAddress(),host.getCommunity(),oids);
				} catch(Exception e){
					valueArray = null;
					SysLogger.error(host.getIpAddress() + "_WindowsSnmp");
				}	
				for(int i=0;i<valueArray.length;i++){
					softwaredata = new Softwarecollectdata();	
					String name = valueArray[i][0];
					String swid = valueArray[i][1];
					String type = valueArray[i][2];
					if(type.equalsIgnoreCase("4")){
						type="应用软件";
					}else{
						type="系统软件";
					}
				    String insdate = valueArray[i][3];
				    String swdate = getDate(insdate);
				    softwaredata.setIpaddress(host.getIpAddress());
				    softwaredata.setName(name);
				    softwaredata.setSwid(swid);
				    softwaredata.setType(type);
				    softwaredata.setInsdate(swdate);
				    softwareVector.addElement(softwaredata);
					//System.out.println(name+"######"+id+"######"+type+"######"+dates);
				    
				}
			  
			  
			  
			  
			  
		   }catch(Exception e){
				  //e.printStackTrace();
			  }
		   
		   //-------------------------------------------------------------------------------------------software end
		   
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
						valueArray = snmp.getTableData(host.getIpAddress(),host.getCommunity(),oids);
					} catch(Exception e){
						valueArray = null;
						SysLogger.error(host.getIpAddress() + "_WindowsSnmp");
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
					    servicedata.setIpaddress(host.getIpAddress());
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
			   
			   
				
			   //-------------------------------------------------------------------------------------------device start
				  try{
				  String[] oids =                
					  new String[] {               
						  "1.3.6.1.2.1.25.3.2.1.1",  //hrDeviceIndex
						  "1.3.6.1.2.1.25.3.2.1.2",  //hrDeviceType
						  "1.3.6.1.2.1.25.3.2.1.3",    //hrDeviceDescr
						  "1.3.6.1.2.1.25.3.2.1.5" };   //hrDeviceStatus

				  String[][] valueArray = null;   	  
					try {
						valueArray = snmp.getTableData(host.getIpAddress(),host.getCommunity(),oids);
					} catch(Exception e){
						valueArray = null;
						SysLogger.error(host.getIpAddress() + "_WindowsSnmp");
					}	
					for(int i=0;i<valueArray.length;i++){
						devicedata = new Devicecollectdata();
						String devindex = valueArray[i][0];
						String type = valueArray[i][1];
						String name = valueArray[i][2];
						String status = valueArray[i][3];
						if(status == null)status="";
						if(device_Status.containsKey(status))status = (String)device_Status.get(status);
						devicedata.setDeviceindex(devindex);
						devicedata.setIpaddress(host.getIpAddress());
						devicedata.setName(name);
						devicedata.setStatus(status);
						devicedata.setType((String)device_Type.get(type));
						deviceVector.addElement(devicedata);
						//SysLogger.info(name+"######"+devindex+"######"+(String)device_Type.get(type)+"######"+status);
					    
					}  
			   }catch(Exception e){
					  //e.printStackTrace();
			   }			   
			   //-------------------------------------------------------------------------------------------device end
			   
			   //-------------------------------------------------------------------------------------------Storage start
				  try{
				  String[] oids =                
					  new String[] {               
						  "1.3.6.1.2.1.25.2.3.1.1",  //hrStorageIndex
						  "1.3.6.1.2.1.25.2.3.1.2",  //hrStorageType
						  "1.3.6.1.2.1.25.2.3.1.3",    //hrStorageDescr
						  "1.3.6.1.2.1.25.2.3.1.4",    //hrStorageAllocationUnits
						  "1.3.6.1.2.1.25.2.3.1.5" };   //hrStorageSize

				  String[][] valueArray = null;   	  
					try {
						valueArray = snmp.getTableData(host.getIpAddress(),host.getCommunity(),oids);
					} catch(Exception e){
						valueArray = null;
						SysLogger.error(host.getIpAddress() + "_WindowsSnmp");
					}	
					for(int i=0;i<valueArray.length;i++){
						storagedata = new Storagecollectdata();
						String storageindex = valueArray[i][0];
						String type = valueArray[i][1];
						String name = valueArray[i][2];
						String byteunit = valueArray[i][3];
						String cap = valueArray[i][4];
						int allsize=0;
						try{
							allsize = Integer.parseInt(cap.trim());
						}catch(Exception e){
							
						}
						
						float size=0.0f;
						  try{
							  size=allsize*Long.parseLong(byteunit)*1.0f/1024/1024;
						  }catch(Exception e){
							  
						  }
						  String unit = ""; 
						  if(size>=1024.0f){
							  size=size/1024;
							  //diskdata.setUnit("G");
							  unit = "G"; 
						  }
						  else{
						  	//diskdata.setUnit("M");
						  	unit = "M"; 
						  }
						storagedata.setStorageindex(storageindex);
						storagedata.setIpaddress(host.getIpAddress());
						storagedata.setName(name);
						storagedata.setCap(Arith.floatToStr(size+"", 1, 0)+unit);
						//SysLogger.info("Type====="+type);
						try{
							storagedata.setType((String)storage_Type.get(type));
						}catch(Exception e){
							
						}
						storageVector.addElement(storagedata);
						//SysLogger.info(name+"######"+storageindex+"######"+(String)storage_Type.get(type)+"######"+size+unit);					    
					}  
			   }catch(Exception e){
					  e.printStackTrace();
			   }			   
			   //-------------------------------------------------------------------------------------------Storage end
			   
			   //---------------------------------------------------得到所有IpNetToMedia,即直接与该设备连接的ip start
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
						SysLogger.error(host.getIpAddress() + "_WindowsSnmp");
					}
				   	  for(int i=0;i<valueArray.length;i++)
				   	  {
				   		  IpMac ipmac = new IpMac();
				   		  for(int j=0;j<4;j++){
				   			String sValue = valueArray[i][j];
				   			//SysLogger.info("MAC===="+sValue);
				   			if(sValue == null)continue;
							if(j==0){
								ipmac.setIfindex(sValue);
							}else if (j==1){
								ipmac.setMac(sValue);
							}else if (j==2){
								ipmac.setIpaddress(sValue);									
							}
				   		 }
				   		ipmac.setIfband("0");
				   		ipmac.setIfsms("0");
						ipmac.setCollecttime(new GregorianCalendar());
						ipmac.setRelateipaddr(host.getIpAddress());
						ipmacVector.addElement(ipmac);
						//SysLogger.info("ARP hostip==>"+host.getIpAddress()+"=="+ipmac.getMac()+"====="+ipmac.getIpaddress());
						//MACVSIP.put(ipmac.getMac(), ipmac.getIpaddress());
				   	  }	
			    }
			    catch (Exception e)
			    {
			    	//SysLogger.error("getIpNetToMediaTable(),ip=" + address + ",community=" + community);
			        //tableValues = null;
			        e.printStackTrace();
			    }
			  
			    //---------------------------------------------------得到所有IpNetToMedia,即直接与该设备连接的ip end	
		   
			    //-------------------------------------------------------------------------------------------disk start			
			try{			
				String[] oids =                
						  new String[] {               
							  "1.3.6.1.2.1.25.2.3.1.1",  
							  "1.3.6.1.2.1.25.2.3.1.2",  
							  "1.3.6.1.2.1.25.2.3.1.3",  
							  "1.3.6.1.2.1.25.2.3.1.4",  
							  "1.3.6.1.2.1.25.2.3.1.5",  
							  "1.3.6.1.2.1.25.2.3.1.6",  
							  "1.3.6.1.2.1.25.2.3.1.7" };
				
				String[][] valueArray = null;   	  
				try {
					valueArray = snmp.getTableData(host.getIpAddress(),host.getCommunity(),oids);
				} catch(Exception e){
					valueArray = null;
					SysLogger.error(host.getIpAddress() + "_WindowsSnmp");
				}	
				for(int i=0;i<valueArray.length;i++){
					diskdata=new Diskcollectdata();
					diskdata.setIpaddress(host.getIpAddress());
					diskdata.setCollecttime(date);
					diskdata.setCategory("Disk");
					diskdata.setEntity("Utilization");	
					diskdata.setRestype("static");
					diskdata.setUnit("%");
					String descriptions=valueArray[i][2];
					String byteunit=valueArray[i][3];
					String desc="";
					if(descriptions == null)descriptions="";
					
					
					
					if(descriptions.indexOf("Memory")>=0){
						desc = descriptions;
					}else{
						if(descriptions.trim().length()>2){
							desc = descriptions.substring(0, 3);
						}
					}
					if(descriptions.indexOf("\\")>=0){
					  desc = desc.replace("\\", "/");
					}
					//SysLogger.info("desc: "+desc);
//					  if(descriptions.indexOf("\\")>=0){
//						  desc = desc.replace("\\", "/");
//						  desc=descriptions.substring(0,descriptions.indexOf("\\"))+"/"+descriptions.substring(descriptions.indexOf("\\")+1,descriptions.length());
//					  }
//					  else{
//						  desc=descriptions;
//					  }
					  diskdata.setSubentity(desc);
					  float value=0.0f;
					  String svb4=valueArray[i][4];
					  String svb5=valueArray[i][5];
					  int allsize=Integer.parseInt(svb4.trim());
					  int used=Integer.parseInt(svb5.trim());
					  if(allsize!=0){
						  value=used*100.0f/allsize;
					  }
					  else{
						  value=0.0f;
					  }
					  diskdata.setThevalue(Float.toString(value));
					  //SysLogger.info(desc+"==============="+value);
					  //SysLogger.info(((OctetString.fromHexString(desc))).toHexString());
					  
					  if (diskdata.getSubentity().equals("Physical Memory")){
							/* 
							memorydata=new Memorycollectdata();
							memorydata.setIpaddress(diskdata.getIpaddress());
							memorydata.setCollecttime(diskdata.getCollecttime());
							memorydata.setCategory("Memory");
							memorydata.setEntity("Utilization");
							memorydata.setSubentity("PhysicalMemory");
							memorydata.setRestype("dynamic");
							memorydata.setUnit("%");
							memorydata.setThevalue(diskdata.getThevalue());	
							
							memoryVector.addElement(memorydata);
							*/
						  }else if (diskdata.getSubentity().equals("Virtual Memory")){
							memorydata=new Memorycollectdata();
							memorydata.setIpaddress(diskdata.getIpaddress());
							memorydata.setCollecttime(diskdata.getCollecttime());
							memorydata.setCategory("Memory");
							memorydata.setEntity("Utilization");
							memorydata.setSubentity("VirtualMemory");
							memorydata.setRestype("dynamic");
							memorydata.setUnit("%");
							memorydata.setThevalue(diskdata.getThevalue());
							//SysLogger.info("开始增加虚拟内存数据...");
							memoryVector.addElement(memorydata);			  				  	
						  }else{
							  if(diskdata.getSubentity().trim().length()>0){
								  diskVector.addElement(diskdata);
							  }
						  }
					  
					  diskdata=new Diskcollectdata();
					  diskdata.setIpaddress(host.getIpAddress());
					  diskdata.setCollecttime(date);
					  diskdata.setCategory("Disk");
					  diskdata.setEntity("AllSize");
					  diskdata.setRestype("static");
					  diskdata.setSubentity(desc);
					  float size=0.0f;
					  size=allsize*Long.parseLong(byteunit)*1.0f/1024/1024;
					  String unit = ""; 
					  if(size>=1024.0f){
						  size=size/1024;
						  diskdata.setUnit("G");
						  unit = "G"; 
					  }
					  else{
					  	diskdata.setUnit("M");
					  	unit = "M"; 
					  }
					  diskdata.setThevalue(Float.toString(size));
					  if (diskdata.getSubentity().equals("Virtual Memory")){
						memorydata=new Memorycollectdata();
						memorydata.setIpaddress(diskdata.getIpaddress());
						memorydata.setCollecttime(diskdata.getCollecttime());
						memorydata.setCategory("Memory");
						memorydata.setEntity("Capability");
						memorydata.setRestype("static");
						memorydata.setSubentity("VirtualMemory");
						memorydata.setUnit(diskdata.getUnit());
						memorydata.setThevalue(Float.toString(size));				
						memoryVector.addElement(memorydata);			  	
					  }
					  
					  if (!diskdata.getSubentity().equals("Physical Memory") && !diskdata.getSubentity().equals("Virtual Memory")&& diskdata.getSubentity().trim().length()>0){
						  diskVector.addElement(diskdata);
					  }
					  diskdata=new Diskcollectdata();
					  diskdata.setIpaddress(host.getIpAddress());
					  diskdata.setCollecttime(date);
					  diskdata.setCategory("Disk");
					  diskdata.setEntity("UsedSize");
					  diskdata.setRestype("static");
					  diskdata.setSubentity(desc);
					  size=used*Long.parseLong(byteunit)*1.0f/1024/1024;
					  if("G".equals(unit)){
						  size=size/1024;
						  diskdata.setUnit("G");
					  }else{
						     diskdata.setUnit("M");
					 }
					  diskdata.setThevalue(Float.toString(size));
					  if (!diskdata.getSubentity().equals("Physical Memory") && !diskdata.getSubentity().equals("Virtual Memory") && diskdata.getSubentity().trim().length()>0){
						  diskVector.addElement(diskdata);
					  }
					  
					//yangjun 
						try {
							String diskinc = "0.0";
							float pastutil = 0.0f;
							Vector disk_v = (Vector)ipAllData.get("disk");
							if (disk_v != null && disk_v.size() > 0) {
								for (int si = 0; si < disk_v.size(); si++) {
									Diskcollectdata disk_data = (Diskcollectdata) disk_v.elementAt(si);
									if((desc).equals(disk_data.getSubentity())&&"Utilization".equals(disk_data.getEntity())){
										pastutil = Float.parseFloat(disk_data.getThevalue());
//										System.out.println("pastutil--------------------"+pastutil);
									}
								}
							} else {
								pastutil = value;
							}
							if (pastutil == 0) {
								pastutil = value;
							}
							if(value-pastutil>0){
								diskinc = (value-pastutil)+"";
							}
//							System.out.println("diskinc------------------"+diskinc);
							diskdata = new Diskcollectdata();
							diskdata.setIpaddress(host.getIpAddress());
							diskdata.setCollecttime(date);
							diskdata.setCategory("Disk");
							diskdata.setEntity("UtilizationInc");// 利用增长率百分比
							diskdata.setSubentity(desc);
							diskdata.setRestype("dynamic");
							diskdata.setUnit("%");
							diskdata.setThevalue(diskinc);
							if(diskdata.getSubentity().equals("Physical Memory")||diskdata.getSubentity().equals("Virtual Memory")){
								
							} else {
								
								if(diskdata.getSubentity().trim().length()>0){
									diskVector.addElement(diskdata);
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						//
					  
				}
		  }
		  catch(Exception e){
			  //e.printStackTrace();
		  }
		  //-------------------------------------------------------------------------------------------disk end


		  //-------------------------------------------------------------------------------------------cpu start	
		  int result = 0;
	   	  
		  try{
			  String temp = "0";
			String[] oids =                
				new String[] {               
					"1.3.6.1.2.1.25.3.3.1.2" 
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
			   		value=Integer.parseInt(_value);
					allvalue = allvalue+Integer.parseInt(_value);
					//if(value >0){
						flag = flag +1;
				   		List alist = new ArrayList();
				   		alist.add(index);
				   		alist.add(_value);
				   		cpuList.add(alist);
					//}
			   		//SysLogger.info(host.getIpAddress()+"  "+index+"   value="+value);
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
			  //e.printStackTrace();
		  }
		  //-------------------------------------------------------------------------------------------cpu end	
	
		  //-------------------------------------------------------------------------------------------memory start			
			try{
			
			String[] oids =                
				new String[] {               
					"1.3.6.1.2.1.25.5.1.1.2" };
			String[] oids1 =                
				new String[] {               
					"1.3.6.1.2.1.25.2.2" };
			
			String[][] valueArray = null;
			try {
				valueArray = snmp.getTableData(host.getIpAddress(),host.getCommunity(),oids);
			} catch(Exception e){
				valueArray = null;
				SysLogger.error(host.getIpAddress() + "_WindowsSnmp");
			}
			
			String[][] valueArray1 = null;
			try {
				valueArray1 = snmp.getTableData(host.getIpAddress(),host.getCommunity(),oids1);
			} catch(Exception e){
				valueArray1 = null;
				SysLogger.error(host.getIpAddress() + "_WindowsSnmp");
			}

			int allMemorySize=0;
			if(valueArray1 != null){
				for(int i=0;i<valueArray1.length;i++){
					if(valueArray1[i][0]==null)continue;
					allMemorySize = Integer.parseInt(valueArray1[i][0]);
				}
			}
			//System.out.println("list "+list.size());
			float value=0.0f;
			int allUsedSize=0;
			if(valueArray != null){
				for(int i=0;i<valueArray.length;i++){
					if(valueArray[i][0] == null)continue;
					int processUsedSize = Integer.parseInt(valueArray[i][0]);
					allUsedSize=allUsedSize+processUsedSize;
				}
			}
			if(allMemorySize!=0){
				value=allUsedSize*100f/allMemorySize;
			}
			else{
				throw new Exception("allMemorySize is 0");
			}
			memorydata=new Memorycollectdata();
			memorydata.setIpaddress(host.getIpAddress());
			memorydata.setCollecttime(date);
			memorydata.setCategory("Memory");
			memorydata.setEntity("Utilization");
			memorydata.setSubentity("PhysicalMemory");
			memorydata.setRestype("dynamic");
			memorydata.setUnit("%");
			memorydata.setThevalue(Float.toString(value));
			memoryVector.addElement(memorydata);
			
			
			
			
			memorydata=new Memorycollectdata();
			memorydata.setIpaddress(host.getIpAddress());
			memorydata.setCollecttime(date);
			memorydata.setCategory("Memory");
			memorydata.setEntity("Capability");
			memorydata.setRestype("static");
			memorydata.setSubentity("PhysicalMemory");
		
			float size=0.0f;
			size=allMemorySize*1.0f/1024;			
			if(size>=1024.0f){
				size=size/1024;
				memorydata.setUnit("G");
			}
			else{
				memorydata.setUnit("M");
			}				
			memorydata.setThevalue(Float.toString(size));
			memoryVector.addElement(memorydata);
			memorydata=new Memorycollectdata();
			memorydata.setIpaddress(host.getIpAddress());
			memorydata.setCollecttime(date);
			memorydata.setCategory("Memory");
			memorydata.setEntity("UsedSize");
			memorydata.setRestype("static");
			memorydata.setSubentity("PhysicalMemory");
					size=allUsedSize*1.0f/1024;						
					if(size>=1024.0f){
						size=size/1024;
						memorydata.setUnit("G");
					}
					else{
						memorydata.setUnit("M");
					}	
					memorydata.setThevalue(Float.toString(size));
					memoryVector.addElement(memorydata);
			}
			catch(Exception e){
				//System.out.println(e.getMessage());
				//e.printStackTrace();
			}
			//-------------------------------------------------------------------------------------------memory end
	
			//-------------------------------------------------------------------------------------------sysuser start			
			try{
			
			String[] oids =                
				new String[] {               
					"1.3.6.1.4.1.77.1.2.25.1.1" };
			String[][] valueArray = null;
			try {
				valueArray = snmp.getTableData(host.getIpAddress(),host.getCommunity(),oids);
			} catch(Exception e){
				valueArray = null;
				SysLogger.error(host.getIpAddress() + "_WindowsSnmp");
			}
			if(valueArray != null){
				for(int i=0;i<valueArray.length;i++){
					String user = valueArray[i][0];
					userdata=new Usercollectdata();
					userdata.setIpaddress(host.getIpAddress());
					userdata.setCollecttime(date);
					userdata.setCategory("User");
					userdata.setEntity("Sysuser");
					userdata.setSubentity(user);
					userdata.setRestype("static");
					userdata.setUnit(" ");
					userdata.setThevalue(user);
					userVector.addElement(userdata);
				}
			}

		
			}
			catch(Exception e){
				//e.printStackTrace();
			}
			//-------------------------------------------------------------------------------------------sysuser end
	
	
			//-------------------------------------------------------------------------------------------process start			
			try{
			
			String[] oids =                
				new String[] {      
					"1.3.6.1.2.1.25.4.2.1.1",         
					"1.3.6.1.2.1.25.4.2.1.2" ,
					"1.3.6.1.2.1.25.4.2.1.5",
					"1.3.6.1.2.1.25.4.2.1.6" ,
					"1.3.6.1.2.1.25.4.2.1.7",
					"1.3.6.1.2.1.25.5.1.1.2" ,
					"1.3.6.1.2.1.25.5.1.1.1" ,
					
					};
			String[] oids1 =                
					new String[] {               
						"1.3.6.1.2.1.25.2.2" };
			String[][] valueArray1 = null;
			try {
				valueArray1 = snmp.getTableData(host.getIpAddress(),host.getCommunity(),oids1);
			} catch(Exception e){
				valueArray1 = null;
				SysLogger.error(host.getIpAddress() + "_WindowsSnmp");
			}
			int allMemorySize=0;
			if(valueArray1 != null){
				for(int i=0;i<valueArray1.length;i++){
					String svb0 = valueArray1[i][0];
					if(svb0 == null)continue;
					allMemorySize=Integer.parseInt(svb0);
				}
			}

			String[][] valueArray = null;
			try {
				valueArray = snmp.getTableData(host.getIpAddress(),host.getCommunity(),oids);
			} catch(Exception e){
				valueArray = null;
				SysLogger.error(host.getIpAddress() + "_WindowsSnmp");
			}
			Vector vecIndex=new Vector();
			
			List procslist = new ArrayList();
			ProcsDao procsdao=new ProcsDao();
			try{
				procslist = procsdao.loadByIp(host.getIpAddress());
			}catch(Exception ex){
				ex.printStackTrace();
			}finally{
				procsdao.close();
			}
			
			//List procslist = procsManager.loadByIp(host.getIpAddress());
			List procs_list = new ArrayList();
			Hashtable procshash = new Hashtable();
			Vector procsV = new Vector();
			if (procslist != null && procslist.size()>0){
				for(int i=0;i<procslist.size();i++){
					Procs procs = (Procs)procslist.get(i);
					procshash.put(procs.getProcname(),procs);
					procsV.add(procs.getProcname());					
				}
			}
			if(valueArray != null){
				for(int i=0;i<valueArray.length;i++){
					if(allMemorySize!=0){
						String vbstring0=valueArray[i][0];
						String vbstring1=valueArray[i][1];
						String vbstring2=valueArray[i][2];
						String vbstring3=valueArray[i][3];
						String vbstring4=valueArray[i][4];
						String vbstring5=valueArray[i][5];
						String vbstring6=valueArray[i][6];
						String processIndex=vbstring0.trim();
						
						float value=0.0f;
						value=Integer.parseInt(vbstring5.trim())*100.0f/allMemorySize;
			
						processdata=new Processcollectdata();
						processdata.setIpaddress(host.getIpAddress());
						processdata.setCollecttime(date);
						processdata.setCategory("Process");
						processdata.setEntity("MemoryUtilization");
						processdata.setSubentity(processIndex);
						processdata.setRestype("dynamic");
						processdata.setUnit("%");
						processdata.setThevalue(Float.toString(value));
						processVector.addElement(processdata);	
						
						String processMemory=vbstring5.trim();
						processdata=new Processcollectdata();
						processdata.setIpaddress(host.getIpAddress());
						processdata.setCollecttime(date);
						processdata.setCategory("Process");
						processdata.setEntity("Memory");
						processdata.setSubentity(processIndex);
						processdata.setRestype("static");
						processdata.setUnit("K");
						processdata.setThevalue(processMemory);
						processVector.addElement(processdata);
						
						String processType=vbstring3.trim();
						processdata=new Processcollectdata();
						processdata.setIpaddress(host.getIpAddress());
						processdata.setCollecttime(date);
						processdata.setCategory("Process");
						processdata.setEntity("Type");
						processdata.setSubentity(processIndex);
						processdata.setRestype("static");
						processdata.setUnit(" ");
						processdata.setThevalue(HOST_hrSWRun_hrSWRunType.get(processType).toString());
						processVector.addElement(processdata);
						
						String processPath=vbstring2.trim();
						processdata=new Processcollectdata();
						processdata.setIpaddress(host.getIpAddress());
						processdata.setCollecttime(date);
						processdata.setCategory("Process");
						processdata.setEntity("Path");
						processdata.setSubentity(processIndex);
						processdata.setRestype("static");
						processdata.setUnit(" ");
						processdata.setThevalue(processPath);
						processVector.addElement(processdata);
						
						String processStatus=vbstring4.trim();
						processdata=new Processcollectdata();
						processdata.setIpaddress(host.getIpAddress());
						processdata.setCollecttime(date);
						processdata.setCategory("Process");
						processdata.setEntity("Status");
						processdata.setSubentity(processIndex);
						processdata.setRestype("static");
						processdata.setUnit(" ");
						processdata.setThevalue(HOST_hrSWRun_hrSWRunStatus.get(processStatus).toString());
						processVector.addElement(processdata);
						
						String processName=vbstring1.trim();
						processdata=new Processcollectdata();
						processdata.setIpaddress(host.getIpAddress());
						processdata.setCollecttime(date);
						processdata.setCategory("Process");
						processdata.setEntity("Name");
						processdata.setSubentity(processIndex);
						processdata.setRestype("static");
						processdata.setUnit(" ");
						processdata.setThevalue(processName);
						processVector.addElement(processdata);
						
						String processCpu=vbstring6.trim();
						processdata=new Processcollectdata();
						processdata.setIpaddress(host.getIpAddress());
						processdata.setCollecttime(date);
						processdata.setCategory("Process");
						processdata.setEntity("CpuTime");
						processdata.setSubentity(processIndex);
						processdata.setRestype("static");
						processdata.setUnit("秒");
						processdata.setThevalue(Integer.toString((Integer.parseInt(processCpu)/100)));
						processVector.addElement(processdata);
						
						//判断是否有需要监视的进程，若取得的列表里包含监视进程，则从Vector里去掉
						if (procsV !=null && procsV.size()>0){
							if (procsV.contains(processName)){
								//procshash.remove(vbstring1);						
								procsV.remove(processName);
								//判断已经发送的进程短信列表里是否有该进程,若有,则从已发送列表里去掉该短信信息
								if(sendeddata.containsKey(host+":"+processName)){
									sendeddata.remove(host+":"+processName);
								}
								//判断进程丢失列表里是否有该进程,若有,则从该列表里去掉该信息
					     		Hashtable iplostprocdata = (Hashtable)ShareData.getLostprocdata(host.getIpAddress());
					     		if(iplostprocdata == null)iplostprocdata = new Hashtable();
					     		if (iplostprocdata.containsKey(processName)){
					     			iplostprocdata.remove(processName);
					     			ShareData.setLostprocdata(host.getIpAddress(), iplostprocdata);
					     		}
					     		
								
							}
						}
					}else{
						throw new Exception("Process is 0");
					}
				}
			}
			
			
			 //判断ProcsV里还有没有需要监视的进程，若有，则说明当前没有启动该进程，则用命令重新启动该进程，同时写入事件
		     Vector eventtmpV = new Vector();
		     if (procsV !=null && procsV.size()>0){
		     	for(int i=0;i<procsV.size();i++){
		     		Procs procs = (Procs)procshash.get((String)procsV.get(i));
		     		
		     		Hashtable iplostprocdata = (Hashtable)ShareData.getLostprocdata(host.getIpAddress());
		     		if(iplostprocdata == null)iplostprocdata = new Hashtable();
		     		iplostprocdata.put(procs.getProcname(), procs);
		     		ShareData.setLostprocdata(host.getIpAddress(), iplostprocdata);
		     		EventList eventlist = new EventList();
		    		eventlist.setEventtype("poll");
		    		eventlist.setEventlocation(host.getSysLocation());
		    		eventlist.setContent(procs.getProcname()+"进程丢失");
		    		eventlist.setLevel1(1);
		    		eventlist.setManagesign(0);
		    		eventlist.setBak("");
		    		eventlist.setRecordtime(Calendar.getInstance());
		    		eventlist.setReportman("系统轮询");
		    		eventlist.setEventlocation(host.getAlias()+"("+host.getIpAddress()+")");
		    		NodeToBusinessDao ntbdao = new NodeToBusinessDao();
		    		List ntblist = new ArrayList();
		    		try{
		    			ntblist = ntbdao.loadByNodeAndEtype(host.getId(), "equipment");
		    		}catch(Exception e){
		    			e.printStackTrace();
		    		}finally{
		    			ntbdao.close();
		    		}
		    		String bids = ",";
		    		if(ntblist != null && ntblist.size()>0){
		    			
		    			for(int k=0;k<ntblist.size();k++){
		    				NodeToBusiness ntb = (NodeToBusiness)ntblist.get(k);
		    				bids=bids+ntb.getBusinessid()+",";
		    			}
		    		}
		    		eventlist.setBusinessid(bids);
		    		eventlist.setNodeid(host.getId());
		    		eventlist.setOid(0);
		    		eventlist.setSubtype("host");
		    		eventlist.setSubentity("proc");
		    		EventListDao eventlistdao = new EventListDao();
		    		eventlistdao.save(eventlist);	
		    		eventtmpV.add(eventlist);
					//发送手机短信并写事件和声音告警
					createSMS(procs);
		     	}
		     	//if (eventtmpV != null && eventtmpV.size()>0)
		     		//eventmanager.createEventlist(eventtmpV);
		     }
			
			}
			catch(Exception e){
	
				//e.printStackTrace();
			}
			//-------------------------------------------------------------------------------------------process end	

			//-------------------------------------------------------------------------------------------ip mac start			
			try{
			
			  String[] oids =                
				  new String[] {                
					  "1.3.6.1.2.1.2.2.1.6"
					  };
			  String[][] valueArray = null;
				try {
					valueArray = snmp.getTableData(host.getIpAddress(),host.getCommunity(),oids);
				} catch(Exception e){
					valueArray = null;
					SysLogger.error(host.getIpAddress() + "_WindowsSnmp");
				}
				systemdata=new Systemcollectdata();
				systemdata.setIpaddress(host.getIpAddress());
				systemdata.setCollecttime(date);
				systemdata.setCategory("System");
				systemdata.setEntity("MacAddr");
				systemdata.setSubentity("MacAddr");
				systemdata.setRestype("static");
				systemdata.setUnit(" ");
				if(valueArray != null){
					for(int i=0;i<valueArray.length;i++){
						String value=valueArray[i][0];
						if (value == null || value.length()==0)continue;
						systemdata.setThevalue(value);
						break;
					}
				}				
				systemVector.addElement(systemdata);
	
			}
			catch(Exception e){
				//e.printStackTrace();
			}
			//-------------------------------------------------------------------------------------------ip mac end	
	/*
//-------------------------------------------------------------------------------------------system start			
			try{
			
			 String[] oids =                
				  new String[] {               
					  "1.3.6.1.2.1.1.1.0" 
					  };
			 String[][] valueArray = null;
				try {
					valueArray = snmp.getTableData(host.getIpAddress(),host.getCommunity(),oids);
				} catch(Exception e){
					valueArray = null;
					SysLogger.error(host.getIpAddress() + "_WindowsSnmp");
				}

				  systemdata=new Systemcollectdata();
				  systemdata.setIpaddress(host.getIpAddress());
				  systemdata.setCollecttime(date);
				  systemdata.setCategory("System");
				  systemdata.setEntity("operatSystem");
				  systemdata.setSubentity("operatSystem");
				  systemdata.setRestype("static");
				  systemdata.setUnit(" ");
				  systemdata.setThevalue(valueArray[0][0]);
			  systemVector.addElement(systemdata);
	
			}
			catch(Exception e){
				e.printStackTrace();
			}
//	-------------------------------------------------------------------------------------------system end	
			
//	-------------------------------------------------------------------------------------------sysname start			
			try{
			
				  String[] oids =                
					  new String[] {               
						  "1.3.6.1.2.1.1.5.0" 
						  };
				  String[][] valueArray = null;
					try {
						valueArray = snmp.getTableData(host.getIpAddress(),host.getCommunity(),oids);
					} catch(Exception e){
						valueArray = null;
						SysLogger.error(host.getIpAddress() + "_WindowsSnmp");
					}
					systemdata=new Systemcollectdata();
					systemdata.setIpaddress(host.getIpAddress());
					systemdata.setCollecttime(date);
					systemdata.setCategory("System");
					systemdata.setEntity("SysName");
					systemdata.setSubentity("SysName");
					systemdata.setRestype("static");
					systemdata.setUnit(" ");
					systemdata.setThevalue(valueArray[0][0]);
					systemVector.addElement(systemdata);
	
			}
			catch(Exception e){
				e.printStackTrace();
			}
//	  -------------------------------------------------------------------------------------------sysname end	
			*/
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
					SysLogger.error(host.getIpAddress() + "_WindowsSnmp");
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
		  /*
//	  -------------------------------------------------------------------------------------------uptime start			
			try{
			
					String[] oids =                
						new String[] {               
							"1.3.6.1.2.1.1.3.0" 
							};
					String sysuptime=this.getResponse(oids,host);
					systemdata=new Systemcollectdata();
					systemdata.setIpaddress(host.getIpAddress());
					systemdata.setCollecttime(date);
					systemdata.setCategory("System");
					systemdata.setEntity("SysUptime");
					systemdata.setSubentity("SysUptime");
					systemdata.setRestype("static");
					systemdata.setUnit(" ");
					systemdata.setThevalue(sysuptime);
					systemVector.addElement(systemdata);
	
			}
			catch(Exception e){
				e.printStackTrace();
			}
//		-------------------------------------------------------------------------------------------uptime end	
			
//		-------------------------------------------------------------------------------------------systime start			
			try{
			
					  String[] oids =                
						  new String[] {               
							  "1.3.6.1.2.1.25.1.2.0" 
							  };
					  String systime=this.getResponse(oids,host);
						systemdata=new Systemcollectdata();
						systemdata.setIpaddress(host.getIpAddress());
						systemdata.setCollecttime(date);
						systemdata.setCategory("System");
						systemdata.setEntity("Systime");
						systemdata.setSubentity("Systime");
						systemdata.setRestype("static");
						systemdata.setUnit(" ");
						systemdata.setThevalue(this.hexStringToDateTime(systime));
					systemVector.addElement(systemdata);
	
			}
			catch(Exception e){
				e.printStackTrace();
			}
//		  -------------------------------------------------------------------------------------------systime end
			*/
			  //-------------------------------------------------------------------------------------------processcount start			
			try{
			
						String[] oids =                
							new String[] {               
								"1.3.6.1.2.1.25.1.6.0" 
								};
						String[][] valueArray = null;   	  
						try {
							valueArray = snmp.getTableData(host.getIpAddress(),host.getCommunity(),oids);
						} catch(Exception e){
							valueArray = null;
							SysLogger.error(host.getIpAddress() + "_WindowsSnmp");
						}
						if(valueArray != null){
						   	  for(int i=0;i<valueArray.length;i++){
						   		String processcount = valueArray[i][0];
						   		systemdata=new Systemcollectdata();
								systemdata.setIpaddress(host.getIpAddress());
								systemdata.setCollecttime(date);
								systemdata.setCategory("System");
								systemdata.setEntity("ProcessCount");
								systemdata.setSubentity("ProcessCount");
								systemdata.setRestype("static");
								systemdata.setUnit(" ");
								systemdata.setThevalue(processcount);
								systemVector.addElement(systemdata);
								break;
						   	  }
						}   		  
						
	
				}
				catch(Exception e){
					//e.printStackTrace();
					//System.out.println(e.getMessage());
				}
				//-------------------------------------------------------------------------------------------processcount end	
				
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
						valueArray = snmp.getTableData(host.getIpAddress(),host.getCommunity(),oids);
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
		}
		catch(Exception e){
			returnHash=null;
			//e.printStackTrace();
			return null;
			}
		finally{
			}
		//if (diskVector != null && diskVector.size()>0)returnHash.put("disk",diskVector);
		//if (cpuVector != null && cpuVector.size()>0)returnHash.put("cpu",cpuVector);
		//if (cpuList != null && cpuList.size()>0)returnHash.put("cpulist",cpuList);
		//if (memoryVector != null && memoryVector.size()>0)returnHash.put("memory",memoryVector);
		if (userVector != null && userVector.size()>0)returnHash.put("user",userVector);
		//if (processVector != null && processVector.size()>0)returnHash.put("process",processVector);	
		//if (systemVector != null && systemVector.size()>0)returnHash.put("system",systemVector);	
		//if (ipmacVector != null && ipmacVector.size()>0)returnHash.put("ipmac",ipmacVector);
		//if (allutilhdxVector != null && allutilhdxVector.size()>0)returnHash.put("allutilhdx",allutilhdxVector);
		//if (utilhdxpercVector != null && utilhdxpercVector.size()>0)returnHas.put("utilhdxperc",utilhdxpercVector);
		//if (utilhdxVector != null && utilhdxVector.size()>0)returnHash.put("utilhdx",utilhdxVector);
		//if (interfaceVector != null && interfaceVector.size()>0)returnHash.put("interface",interfaceVector);	
		//if (softwareVector != null && softwareVector.size()>0)returnHash.put("software",softwareVector);
		//if (serviceVector != null && serviceVector.size()>0)returnHash.put("winservice",serviceVector);
		//if (deviceVector != null && deviceVector.size()>0)returnHash.put("device",deviceVector);
		//if (storageVector != null && storageVector.size()>0)returnHash.put("storage",storageVector);
		return returnHash;
	}
	
	
	 public void createSMS(Procs procs){
		 	Procs lastprocs = null;
		 	//建立短信	
		 	procs.setCollecttime(Calendar.getInstance());
		 	//从已经发送的短信列表里获得当前该PROC已经发送的短信
		 	lastprocs = (Procs)sendeddata.get(procs.getIpaddress()+":"+procs.getProcname());	
		 	/*
		 	try{		 				 		
		 		if (lastprocs==null){
		 			//内存中不存在	,表明没发过短信,则发短信
		 			Equipment equipment = equipmentManager.getByip(procs.getIpaddress());
		 			Smscontent smscontent = new Smscontent();
		 			String time = sdf.format(procs.getCollecttime().getTime());
		 			smscontent.setMessage(time+"&"+equipment.getEquipname()+"&"+equipment.getIpaddress()+"&"+procs.getProcname()+"&进程丢失&level=2");
		 			//发送短信
		 			Vector tosend = new Vector();
		 			tosend.add(smscontent);		 			
		 			smsmanager.sendSmscontent(tosend);
		 			//把该进程信息添加到已经发送的进程短信列表里,以IP:进程名作为key
		 			sendeddata.put(procs.getIpaddress()+":"+procs.getProcname(),procs);		 						 				
		 		}else{
		 			//若已经发送的短信列表存在这个IP的PROC进程
		 			//若在，则从已发送短信列表里判断是否已经发送当天的短信		 				
		 			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		 			Date last = null;
		 			Date current = null;
		 			Calendar sendcalen = (Calendar)lastprocs.getCollecttime();
		 			Date cc = sendcalen.getTime();
		 			String tempsenddate = formatter.format(cc);
		 			
		 			Calendar currentcalen = (Calendar)procs.getCollecttime();
		 			cc = currentcalen.getTime();
		 			last = formatter.parse(tempsenddate);
		 			String currentsenddate = formatter.format(cc);
		 			current = formatter.parse(currentsenddate);
		 			
		 			long subvalue = current.getTime()-last.getTime();			 			
		 			
		 			if (subvalue/(1000*60*60*24)>=1){
		 				//超过一天，则再发信息
			 			Smscontent smscontent = new Smscontent();
			 			String time = sdf.format(procs.getCollecttime().getTime());
			 			Equipment equipment = equipmentManager.getByip(procs.getIpaddress());
			 			if (equipment == null){
			 				return;
			 			}else
			 				smscontent.setMessage(time+"&"+equipment.getEquipname()+"&"+equipment.getIpaddress()+"&"+procs.getProcname()+"&进程丢失&level=2");
			 			
			 			//发送短信
			 			Vector tosend = new Vector();
			 			tosend.add(smscontent);		 			
			 			smsmanager.sendSmscontent(tosend);
			 			//把该进程信息添加到已经发送的进程短信列表里,以IP:进程名作为key
			 			sendeddata.put(procs.getIpaddress()+":"+procs.getProcname(),procs);		 						 				
			 		}else{
			 			//没超过一天,则只写事件
			 			Vector eventtmpV = new Vector();
						Eventlist event = new Eventlist();
						  Monitoriplist monitoriplist = (Monitoriplist)monitormanager.getByIpaddress(host);
						  event.setEventtype("host");
						  event.setEventlocation(host);
						  event.setManagesign(new Integer(0));
						  event.setReportman("monitorpc");
						  event.setRecordtime(Calendar.getInstance());
						  event.setLevel1(new Integer(1));
						  event.setEquipment(equipmentManager.getByip(monitoriplist.getIpaddress()));
						  event.setNetlocation(equipmentManager.getByip(monitoriplist.getIpaddress()).getNetlocation());
						  String time = sdf.format(Calendar.getInstance().getTime());
						  event.setContent(monitoriplist.getEquipname()+"&"+monitoriplist.getIpaddress()+"&"+time+"进程"+procs.getProcname()+"丢失&level=1");
						  eventtmpV.add(event);
						  try{
							  eventmanager.createEventlist(eventtmpV);
						  }catch(Exception e){
							  e.printStackTrace();
						  }
						  
			 		}
		 		}
		 	}catch(Exception e){
		 		e.printStackTrace();
		 	}
		 	*/
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
		public String getDate(String swdate){
			String[] num = swdate.split(":");
			String num1 = Integer.valueOf(num[0],16).toString();
			String num2 = Integer.valueOf(num[1],16).toString();
			String num3 = Integer.valueOf(num[2],16).toString();
			String num4 = Integer.valueOf(num[3],16).toString();
			String num5 = Integer.valueOf(num[4],16).toString();
			String num6 = Integer.valueOf(num[5],16).toString();
			String num7 = Integer.valueOf(num[6],16).toString();
			String num8 = Integer.valueOf(num[7],16).toString();
			String swyear = Integer.parseInt(num1)*256+Integer.parseInt(num2)+"";
			String swnewdate = swyear+"-"+num3+"-"+num4+" "+num5+":"+num6+":"+num7+":"+num8;
			return swnewdate;
			
		}
		
		
		


}





