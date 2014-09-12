/**
 * <p>Description:collect windows cpu utilization</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-26
 */

package com.afunms.monitor.executor;

import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;

import com.afunms.common.util.SysLogger;
import com.afunms.monitor.item.SnmpItem;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.monitor.item.base.MonitorResult;
import com.afunms.monitor.executor.base.*;
import com.afunms.polling.node.*;
import com.afunms.polling.base.*;
import com.afunms.topology.model.HostNode;

public class WindowsCpu extends SnmpMonitor implements MonitorInterface
{      
    public WindowsCpu()
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
    	SnmpItem item = (SnmpItem)monitoredItem;
    	String[] oids = new String[]{"1.3.6.1.2.1.25.3.3.1.2"};
    	String[][] valueArray = null;
    	
    	try
    	{
    		valueArray = snmp.getTableData(host.getIpAddress(),host.getCommunity(),oids);
    	}
    	catch(Exception e)
    	{
    		valueArray = null;
    		SysLogger.error(host.getIpAddress() + "_WindowsCpu");
    	}
    	
    	if(valueArray==null||valueArray.length==0)
    	{	
    	    item.setMultiResults(null);    	    
    		return;
    	}	
    	
    	List list = new ArrayList(3);
    	for(int i=0;i<valueArray.length;i++)
    	{
    		MonitorResult mr = new MonitorResult();
    		mr.setEntity(String.valueOf(i + 1));
    		try
    		{    			
    			mr.setPercentage(Integer.parseInt(valueArray[i][0]));        			
    		}
    		catch(NumberFormatException nfe)
    		{
    			mr.setPercentage(-1);
    		}
    		list.add(mr);
    	}
    	item.setMultiResults(list);
    }
}