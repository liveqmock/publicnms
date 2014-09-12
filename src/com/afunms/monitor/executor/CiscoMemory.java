/**
 * <p>Description:collect cisco device memory utilization</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-26
 */

package com.afunms.monitor.executor;

import java.text.DecimalFormat;
import java.util.Hashtable;

import com.afunms.common.util.SysLogger;
import com.afunms.monitor.executor.base.MonitorInterface;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.monitor.item.SnmpItem;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.node.*;
import com.afunms.polling.base.*;
import com.afunms.topology.model.HostNode;

public class CiscoMemory extends SnmpMonitor implements MonitorInterface
{
   public CiscoMemory()
   {
   }
   public void collectData(HostNode node){
	   
   }
   public Hashtable collect_Data(HostNode node){
	   return null;
   }
   public void collectData(Node node,MonitoredItem monitoredItem)
   {
	  SnmpItem item = (SnmpItem)monitoredItem;  
	  Host host = (Host)node;
	  int used = 0,free = 0;
  	  String[] oids = new String[]
  	     	              {"1.3.6.1.4.1.9.9.48.1.1.1.2",   //type,°üÀ¨Á½ÖÖ:Processor,I/O
  			               "1.3.6.1.4.1.9.9.48.1.1.1.5",   //used
  			               "1.3.6.1.4.1.9.9.48.1.1.1.6"};  //free  	                         
	  String[][] valueArray = null;
	  int result = -1;
   	  try 
   	  {
   		  valueArray = snmp.getTableData(node.getIpAddress(),host.getCommunity(),oids);   		  
   		  
   		  if(valueArray==null||valueArray.length==0) 
   			 result = -1;
   		  else
   		  {	  
   	  	     DecimalFormat df = new DecimalFormat("#");	  
   	 	     for(int i=0;i<valueArray.length;i++)
   	 	     {   	 		    
   	 		    used += Integer.parseInt(valueArray[i][1]);
   	 		    free += Integer.parseInt(valueArray[i][2]);
   	 	     } 			   	 	    	 	  
   	 	     if( used + free == 0)
   	 	    	result = 0;
   	 	     else
   	 	        result = Integer.parseInt(df.format(used * 100 / (used + free)));
   		  }   
   	  }
   	  catch(Exception e)
   	  {
   		  result = -1;
   		  SysLogger.error(host.getIpAddress() + "_CiscoMemory");   		
   	  }   	  
   	  item.setSingleResult(result);
   }
   
   public static void main(String[] args)
   {
	   CiscoMemory cm = new CiscoMemory();
	   cm.collectData(null,null);
   }
}
