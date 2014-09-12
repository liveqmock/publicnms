package com.afunms.polling.snmp.memory;

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
import com.afunms.polling.task.TaskXml;
import com.afunms.topology.model.HostNode;


/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class WindowsMemorySnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 
	 */
	public WindowsMemorySnmp() {
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
		Vector memoryVector=new Vector();
		Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		
		try {
			Memorycollectdata memorydata=new Memorycollectdata();
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
					//valueArray = SnmpUtils.getTableData(host.getIpAddress(), host.getCommunity(), oids, host.getSnmpversion(), 3, 1000*30);
					valueArray = SnmpUtils.getTableData(host.getIpAddress(),host.getCommunity(),oids,host.getSnmpversion(),
		   		  			host.getSecuritylevel(),host.getSecurityName(),host.getV3_ap(),host.getAuthpassphrase(),host.getV3_privacy(),host.getPrivacyPassphrase(),3,1000*30);
				} catch(Exception e){
					valueArray = null;
					//SysLogger.error(host.getIpAddress() + "_WindowsMemorySnmp");
				}
				
				String[][] valueArray1 = null;
				try {
					//valueArray1 = SnmpUtils.getTableData(host.getIpAddress(), host.getCommunity(), oids1, host.getSnmpversion(), 3, 1000);
					valueArray = SnmpUtils.getTableData(host.getIpAddress(),host.getCommunity(),oids1,host.getSnmpversion(),
		   		  			host.getSecuritylevel(),host.getSecurityName(),host.getV3_ap(),host.getAuthpassphrase(),host.getV3_privacy(),host.getPrivacyPassphrase(),3,1000*30);
				} catch(Exception e){
					valueArray1 = null;
					//SysLogger.error(host.getIpAddress() + "_WindowsMemorySnmp");
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
			}catch(Exception e){
				//returnHash=null;
				//e.printStackTrace();
				//return null;
			}
		
		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(host.getIpAddress());
		if(ipAllData == null)ipAllData = new Hashtable();
		ipAllData.put("memory",memoryVector);
	    ShareData.getSharedata().put(host.getIpAddress(), ipAllData);
	    returnHash.put("memory", memoryVector);
	    
	    ipAllData=null;
	    memoryVector=null;
	    
	    
	    
	    return returnHash;
	}
}





