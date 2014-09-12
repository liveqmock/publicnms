/**
 * <p>Description:collect huawei device cpu utilization</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-26
 */

package com.afunms.monitor.executor;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.TableEvent;

import com.afunms.monitor.executor.base.MonitorInterface;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.monitor.item.SnmpItem;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SnmpMibConstants;
import com.afunms.common.util.ShareData;
import com.afunms.polling.base.*;
import com.afunms.polling.node.*;
import com.afunms.topology.model.HostNode;

public class HuaweiSnmp extends SnmpMonitor implements MonitorInterface
{
   public HuaweiSnmp()
   {
   }
   public void collectData(Node node,MonitoredItem item){
	   
   }
   public Hashtable collect_Data(HostNode node){
	   return null;
   }
   public void collectData(HostNode node)
   {
	  //SnmpItem item = (SnmpItem)monitoredItem;   
	   HostNode host = (HostNode)node;
   	  int result = 0;
   	  try
   	  {
   		  String temp = snmp.getMibValue(host.getIpAddress(),host.getCommunity(),"1.3.6.1.4.1.2011.6.1.1.1.4.0");
   		  if(temp == null){
   			temp = snmp.getMibValue(host.getIpAddress(),host.getCommunity(),"1.3.6.1.4.2011.10.2.6.1.1.1.1.6.0");
   			if(temp == null)
   			  result = -1;
   		  }else
   			  result = Integer.parseInt(temp); 
   		SysLogger.info(host.getIpAddress() + "_HuaweiSnmp value="+result );
   	  }
   	  catch(Exception e)
   	  {
   		  e.printStackTrace();
   		  result = -1;    		  
   		  SysLogger.error(host.getIpAddress() + "_HuaweiSnmp",e);
   	  }

   	  
   	  //item.setSingleResult(result);
   }	   
}
