package com.afunms.polling.snmp.disk;

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
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.common.util.SysLogger;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.Diskcollectdata;
import com.afunms.system.util.TimeGratherConfigUtil;
import com.afunms.topology.model.HostNode;
import com.gatherResulttosql.HostDatatempDiskRttosql;
import com.gatherResulttosql.HostdiskResultosql;


/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class WindowsDiskSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 
	 */
	public WindowsDiskSnmp() {
	}

	   public void collectData(Node node,MonitoredItem item){
		   
	   }
	   public void collectData(HostNode node){
		   
	   }
	/* (non-Javadoc)
	 * @see com.dhcc.webnms.host.snmp.AbstractSnmp#collectData()
	 */
	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicators) {
		Hashtable returnHash=new Hashtable();
		Vector diskVector=new Vector();
		List cpuList = new ArrayList();
		Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(nodeGatherIndicators.getNodeid()));
		if(node == null )return returnHash;
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
    				//清除之前内存中产生的告警信息
    			    try{
    			    	//清除之前内存中产生的内存告警信息
    			    	NodeDTO nodedto = null;
    					NodeUtil nodeUtil = new NodeUtil();
    					nodedto = nodeUtil.creatNodeDTOByHost(node);
						CheckEventUtil checkutil = new CheckEventUtil();
						checkutil.deleteEvent(node.getId()+"", nodedto.getType(), nodedto.getSubtype(), "diskperc", null);
						checkutil.deleteEvent(node.getId()+"", nodedto.getType(), nodedto.getSubtype(), "diskinc", null);
    			    }catch(Exception e){
    			    	e.printStackTrace();
    			    }
    				return returnHash;
    			}
    			
    		}
    	}
		try {
			Diskcollectdata diskdata=null;
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
						//valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), 3, 1000*30);
						valueArray = SnmpUtils.getTableData(node.getIpAddress(),node.getCommunity(),oids,node.getSnmpversion(),
			   		  			node.getSecuritylevel(),node.getSecurityName(),node.getV3_ap(),node.getAuthpassphrase(),node.getV3_privacy(),node.getPrivacyPassphrase(),3,1000*30);
					} catch(Exception e){
						valueArray = null;
						SysLogger.error(node.getIpAddress() + "_WindowsDiskSnmp");
					}	
					for(int i=0;i<valueArray.length;i++){
						diskdata=new Diskcollectdata();
						diskdata.setIpaddress(node.getIpAddress());
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
						}else{
							if(descriptions.trim().length()>2){
								desc = descriptions.substring(0, 3);
							}
						}
						if(descriptions.indexOf("\\")>=0){
						  desc = desc.replace("\\", "/");
						}
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
						  if(value<0){
							  value=-value;
						  }
						  diskdata.setThevalue(Float.toString(value));
						  
						  if (diskdata.getSubentity().equals("Physical Memory")){
						  }else if (diskdata.getSubentity().equals("Virtual Memory")){			  				  	
						  }else{
								  if(diskdata.getSubentity().trim().length()>0){
									  //SysLogger.info(diskdata.getSubentity()+"===="+diskdata.getThevalue());
									  diskVector.addElement(diskdata);
								  }
						  }
						  
						  diskdata=new Diskcollectdata();
						  diskdata.setIpaddress(node.getIpAddress());
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
						  if(size<0){
							  size = -size;
						  }
						  diskdata.setThevalue(Float.toString(size));
						  if (diskdata.getSubentity().equals("Virtual Memory")){			  	
						  }
						  
						  if (!diskdata.getSubentity().equals("Physical Memory") && !diskdata.getSubentity().equals("Virtual Memory")&& diskdata.getSubentity().trim().length()>0){
							  diskVector.addElement(diskdata);
						  }
						  diskdata=new Diskcollectdata();
						  diskdata.setIpaddress(node.getIpAddress());
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
							try {
								String diskinc = "0.0";
								float pastutil = 0.0f;
								Vector disk_v = (Vector)ipAllData.get("disk");
								if (disk_v != null && disk_v.size() > 0) {
									for (int si = 0; si < disk_v.size(); si++) {
										Diskcollectdata disk_data = (Diskcollectdata) disk_v.elementAt(si);
										if((desc).equals(disk_data.getSubentity())&&"Utilization".equals(disk_data.getEntity())){
											pastutil = Float.parseFloat(disk_data.getThevalue());
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
								//System.out.println("diskinc------------------"+diskinc);
								diskdata = new Diskcollectdata();
								diskdata.setIpaddress(node.getIpAddress());
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
						}
					}catch(Exception e){
						//e.printStackTrace();
					}
					//--------------------------------------------------------------------------------------disk end
			}catch(Exception e){
				//returnHash=null;
				//e.printStackTrace();
				//return null;
			}
		
//		Hashtable ipAllData = new Hashtable();
//		try{
//			ipAllData = (Hashtable)ShareData.getSharedata().get(node.getIpAddress());
//		}catch(Exception e){
//			
//		}
//		if(ipAllData == null)ipAllData = new Hashtable();
//		ipAllData.put("disk",diskVector);
//	    ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
//	    returnHash.put("disk", diskVector);
	    
	    
	    if(!(ShareData.getSharedata().containsKey(node.getIpAddress())))
		{
			Hashtable ipAllData = new Hashtable();
			if(ipAllData == null)ipAllData = new Hashtable();
			if(diskVector != null && diskVector.size()>0)ipAllData.put("disk",diskVector);
		    ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		}else
		 {
			if(diskVector != null && diskVector.size()>0)((Hashtable)ShareData.getSharedata().get(node.getIpAddress())).put("disk",diskVector);
		   
		 }
	    returnHash.put("disk", diskVector);
	    
	    
	    //进行磁盘告警检测
	    //SysLogger.info("### 开始运行检测磁盘是否告警### ... ###");
	    try{
			AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(node.getId()), AlarmConstant.TYPE_HOST, "windows");
			for(int i = 0 ; i < list.size() ; i ++){
				AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode)list.get(i);
				//SysLogger.info("alarmIndicatorsnode name ======"+alarmIndicatorsnode.getName());
				if(alarmIndicatorsnode.getName().equalsIgnoreCase("diskperc")){
					CheckEventUtil checkutil = new CheckEventUtil();
				    checkutil.checkDisk(node,diskVector,alarmIndicatorsnode);
				    break;
				}
			}
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
	    
	    //进行磁盘告警检测
	    //SysLogger.info("### 开始运行检测磁盘是否告警### ... ###");
	    try{
			AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(node.getId()), AlarmConstant.TYPE_HOST, "windows");
			for(int i = 0 ; i < list.size() ; i ++){
				AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode)list.get(i);
				//SysLogger.info("alarmIndicatorsnode name ======"+alarmIndicatorsnode.getName());
				if(alarmIndicatorsnode.getName().equalsIgnoreCase("diskinc")){
					CheckEventUtil checkutil = new CheckEventUtil();
				    checkutil.checkDisk(node,diskVector,alarmIndicatorsnode);
				    break;
				}
			}
	    }catch(Exception e){
	    	e.printStackTrace();
	    }

	    
	    //把采集结果生成sql
	    HostdiskResultosql tosql=new HostdiskResultosql();
	    tosql.CreateResultTosql(returnHash, node.getIpAddress());
	    
	    String runmodel = PollingEngine.getCollectwebflag();//采集与访问模式
	    if(!"0".equals(runmodel)){
			//采集与访问是分离模式,则不需要将监视数据写入临时表格
	    	HostDatatempDiskRttosql temptosql=new HostDatatempDiskRttosql();
		    temptosql.CreateResultTosql(returnHash, node);
	    }
	    
	    
	    return returnHash;
	}
}





