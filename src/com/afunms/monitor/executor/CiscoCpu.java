/**
 * <p>Description:collect cisco device cpu utilization</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-26
 */

package com.afunms.monitor.executor;

import java.util.Hashtable;

import com.afunms.monitor.executor.base.MonitorInterface;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.monitor.item.SnmpItem;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.common.util.SysLogger;
import com.afunms.polling.base.*;
import com.afunms.polling.node.*;
import com.afunms.topology.model.HostNode;

public class CiscoCpu extends SnmpMonitor implements MonitorInterface
{
   public CiscoCpu()
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
   	  int result = 0;
   	  try
   	  {
   		  String temp = snmp.getMibValue(host.getIpAddress(),host.getCommunity(),"1.3.6.1.4.1.9.2.1.57.0");   		  
   		  result = Integer.parseInt(temp);    	
   	  }
   	  catch(Exception e)
   	  {
   		  result = -1;    		  
   		  SysLogger.error(host.getIpAddress() + "_CiscoCpu");
   	  }
   	  item.setSingleResult(result);
   }	   
}
