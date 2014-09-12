/**
 * <p>Description:interface rx utilization</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-09-17
 */

package com.afunms.monitor.executor;

import java.util.*;
import java.text.DecimalFormat;

import com.afunms.common.util.*;
import com.afunms.monitor.executor.base.MonitorInterface;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.monitor.item.SnmpItem;
import com.afunms.monitor.item.base.MonitorResult;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.node.*;
import com.afunms.polling.base.*;
import com.afunms.topology.model.HostNode;

public class TrafficRxUtilization extends SnmpMonitor implements MonitorInterface
{   
   public TrafficRxUtilization()
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
   	  String[] oids = new String[]{"1.3.6.1.2.1.2.2.1.1",
   			  						"1.3.6.1.2.1.2.2.1.10"};
   	  String[][] valueArray = null;
   	  
   	  try
   	  {
   		 valueArray = snmp.getTableData(host.getIpAddress(),host.getCommunity(),oids);
   	  }
   	  catch(Exception e)
   	  {
   		 valueArray = null;
   		 SysLogger.error(host.getIpAddress() + "_TrafficRxUtilization");
   	  }   	  
   	  if(valueArray==null||valueArray.length==0)
   	  {	
   	     item.setMultiResults(null);    	    
   		 return;
   	  }	
   	  
   	  List list = new ArrayList(20); 	  
   	  for(int i=0;i<valueArray.length;i++)
   	  {   		 
         if(valueArray[i][0]==null||valueArray[i][1]==null) continue;
         if(host.getInterfaceHash().get(valueArray[i][0])==null) continue;	           			
       	 IfEntity ifEntity = (IfEntity)host.getInterfaceHash().get(valueArray[i][0]);       	        	 
         if(ifEntity.getOperStatus()==2||ifEntity.getSpeed()==0) continue; 
         
         long tempInOctets = Long.parseLong(valueArray[i][1]);
         if(item.getLastTime()==0) //第一次采集
         {
        	 ifEntity.setInOctets(tempInOctets);
        	 continue;
         }
         
   		 long diffOctets = tempInOctets - ifEntity.getInOctets();
   		 if(diffOctets < 0) 
   			 diffOctets = diffOctets + Long.parseLong("4294967295");
   		 
   		 /**
   		  * diffOctets的单位是B,乘8变成b,除1000变成kb (2006.11.4)
   		  */
   		 long rxTraffic = (long) (diffOctets * 8/((SysUtil.getCurrentLongTime()-item.getLastTime()) * 1024)); 
   		 float rxUtil = ((float)(diffOctets * 8 * 100))/(ifEntity.getSpeed()*(SysUtil.getCurrentLongTime()-item.getLastTime()));  
   		 
   		 if(rxUtil>100) rxUtil = -1;   		
   		 if(rxUtil>0)
   		 {
   		    DecimalFormat df = new DecimalFormat("#.00");
   		    rxUtil = Float.parseFloat(df.format(rxUtil));
   		 }      	        	     
   	     MonitorResult mr = new MonitorResult();
   	     mr.setEntity(ifEntity.getIndex()); 
   	     mr.setPercentage(rxUtil);
   	     mr.setValue(rxTraffic);
   	     SysLogger.info(host.getIpAddress()+" index:"+ifEntity.getIndex()+" inspeed:"+rxTraffic);
   		 ifEntity.setInOctets(tempInOctets);    
   		 ifEntity.setRxUtilization(rxUtil); //这是用于页面上显示
   		 list.add(mr);
   	  }
   	  item.setMultiResults(list);
   }   
}   