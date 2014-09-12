/**
 * <p>Description:collect windows memory utilization</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-26
 */

package com.afunms.monitor.executor;

import java.util.Hashtable;

import com.afunms.common.util.SysLogger;
import com.afunms.monitor.item.SnmpItem;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.monitor.executor.base.*;
import com.afunms.polling.node.*;
import com.afunms.polling.base.*;
import com.afunms.topology.model.HostNode;

public class WindowsMemory extends SnmpMonitor implements MonitorInterface
{      
    public WindowsMemory()
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
    	int result = 0;    	
    	try
    	{
    		String[][] valueArray = snmp.getTableData(host.getIpAddress(),host.getCommunity(),new String[]{"1.3.6.1.2.1.25.5.1.1.2"});
    		String temp = snmp.getMibValue(host.getIpAddress(),host.getCommunity(),"1.3.6.1.2.1.25.2.2.0");    		

    		int memorySize = 0,usedSize = 0;     		    		
    		if(temp==null||valueArray==null||valueArray.length==0)
    		{
    			item.setSingleResult(-1);
    			return;
    		}	
    			
    		memorySize = Integer.parseInt(temp);    		
        	if(memorySize==0)
        	   result = 0;
        	else	
        	{
        	    for(int i=0;i<valueArray.length;i++)
        	        usedSize += Integer.parseInt(valueArray[i][0]);   
        	    result = (int)(usedSize*100/memorySize);
        	    
        	    if(result>100) //2006.10.13
        	    {
        	    	SysLogger.error(host.getIpAddress() + "_WindowsMemory=" + result);
        	    	result = 95;
        	    }	
        	} 
    	}
    	catch(Exception e)
    	{
    		result = -1;  
    		SysLogger.error(host.getIpAddress() + "_WindowsMemory");
    	}
    	item.setSingleResult(result);
    }      
    
    public static void main(String[] args)
    {
       WindowsMemory cm = new WindowsMemory();
 	   cm.collectData(null,null);
    }
}