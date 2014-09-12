/**
 * <p>Description:interface tx utilization</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-09-17
 */

package com.afunms.monitor.executor;

import java.text.DecimalFormat;
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

public class TrafficTxUtilization extends SnmpMonitor implements MonitorInterface
{
   public TrafficTxUtilization()
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
   			  "1.3.6.1.2.1.2.2.1.16"};
   	  String[][] valueArray = null;
   	  
   	  try
   	  {
   		 valueArray = snmp.getTableData(host.getIpAddress(),host.getCommunity(),oids);
   	  }
   	  catch(Exception e)
   	  {
   		 valueArray = null;
   		 SysLogger.error(host.getIpAddress() + "_TrafficTxUtilization");
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
                  
         long tempOutOctets = Long.parseLong(valueArray[i][1]);
         if(item.getLastTime()==0) //第一次采集
         {
        	 ifEntity.setOutOctets(tempOutOctets);
        	 continue;
         }
         
         long diffOctets = tempOutOctets - ifEntity.getOutOctets();
   		 if(diffOctets < 0) 
   			diffOctets = diffOctets + Long.parseLong("4294967295");
   		 
   		 long txTraffic = (long) (diffOctets * 8/ ((SysUtil.getCurrentLongTime()-item.getLastTime()) * 1024)); 
   		 float txUtil = ((float)(diffOctets * 8 * 100))/(ifEntity.getSpeed()*(SysUtil.getCurrentLongTime()-item.getLastTime()));  
   		 
   		 if(txUtil>100) txUtil = -1;   		 
   		 if(txUtil>0)
   		 {
   		    DecimalFormat df = new DecimalFormat("#.00");
   		    txUtil = Float.parseFloat(df.format(txUtil));
   		 }      		    		    		 
   		 MonitorResult mr = new MonitorResult();
   		 mr.setEntity(ifEntity.getIndex());
   		 mr.setPercentage(txUtil);
   		 mr.setValue(txTraffic);  
   		
   		 ifEntity.setOutOctets(tempOutOctets);
   		 ifEntity.setTxUtilization(txUtil);
   		 list.add(mr);
   	  }
   	  item.setMultiResults(list);
   }   
}   