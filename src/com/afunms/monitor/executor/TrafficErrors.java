/**
 * <p>Description:interface tx utilization</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-09-17
 */

package com.afunms.monitor.executor;

import java.util.*;

import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.monitor.executor.base.MonitorInterface;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.monitor.item.SnmpItem;
import com.afunms.monitor.item.base.MonitorResult;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.node.*;
import com.afunms.polling.base.*;
import com.afunms.topology.model.HostNode;

public class TrafficErrors extends SnmpMonitor implements MonitorInterface
{
   public TrafficErrors()
   {
   }
   public void collectData(HostNode node){
	   
   }
   public Hashtable collect_Data(HostNode node){
	   return null;
   }
   public void collectData(Node node,MonitoredItem monitoredItem)
   {
	  Host host = (Host)node;
	  if(host.getInterfaceHash()==null||host.getInterfaceHash().size()==0) return;
	   
	  SnmpItem item = (SnmpItem)monitoredItem;
   	  String[] oids = new String[]{"1.3.6.1.2.1.2.2.1.1","1.3.6.1.2.1.2.2.1.14","1.3.6.1.2.1.2.2.1.20"};
   	  String[][] valueArray = null;   	  
   	  try
   	  {
   		 valueArray = snmp.getTableData(host.getIpAddress(),host.getCommunity(),oids);
   	  }
   	  catch(Exception e)
   	  {
   		 valueArray = null;
   		 SysLogger.error(host.getIpAddress() + "_TrafficErrors");
   	  }   	  
   	  if(valueArray==null||valueArray.length==0)
   	  {	
   	     item.setMultiResults(null);    	    
   		 return;
   	  }	
   	
   	  List list = new ArrayList(20);
   	  for(int i=0;i<valueArray.length;i++)
   	  {
   		 if(valueArray[i][0]==null||valueArray[i][1]==null||valueArray[i][2]==null) continue;
         if(host.getInterfaceHash().get(valueArray[i][0])==null) continue;	           			
       	 IfEntity ifEntity = (IfEntity)host.getInterfaceHash().get(valueArray[i][0]);
         if(ifEntity.getOperStatus()==2) continue; 
       	 
         long tempErrors = Long.parseLong(valueArray[i][1]) + Long.parseLong(valueArray[i][2]);
         if(item.getLastTime()==0) //如果是第一次采集
         {
   		     ifEntity.setErrorPkts(tempErrors);        	 
        	 continue;
         }
         
         long diffErrors = tempErrors - ifEntity.getErrorPkts();
	     if(diffErrors < 0) 
	   		diffErrors = diffErrors + Long.parseLong("4294967295");
	     int rate = (int)(diffErrors /(SysUtil.getCurrentLongTime()-item.getLastTime()));
	     if(rate>100) rate = -1;
	     
   		 MonitorResult mr = new MonitorResult();
   		 mr.setEntity(ifEntity.getIndex());   		    		    		   
   		 mr.setPercentage(rate);   		    
   		 ifEntity.setErrorPkts(tempErrors);
   		 ifEntity.setErrors(rate);
   		 list.add(mr);
   	  }
   	  item.setMultiResults(list);
   }   
}   