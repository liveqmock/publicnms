/**
 * <p>Description:collect windows disk utilization</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-26
 */

package com.afunms.monitor.executor;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.afunms.common.util.SysLogger;
import com.afunms.monitor.item.SnmpItem;
import com.afunms.monitor.item.base.MonitorResult;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.monitor.executor.base.*;
import com.afunms.polling.node.*;
import com.afunms.polling.base.*;
import com.afunms.topology.model.HostNode;

public class WindowsDisk extends SnmpMonitor implements MonitorInterface
{      
    public WindowsDisk()
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
    	String[] oids = new String[]
    	                {"1.3.6.1.2.1.25.2.3.1.2",  //type
                         "1.3.6.1.2.1.25.2.3.1.3",  //descr
                         "1.3.6.1.2.1.25.2.3.1.5",  //size
                         "1.3.6.1.2.1.25.2.3.1.6"}; //used 
    	                           
    	String[][] valueArray = null;    	
    	try
    	{
    		valueArray = snmp.getTableData(host.getIpAddress(),host.getCommunity(),oids);    		
    	}
    	catch(Exception e)
    	{
    		valueArray = null; 
    		SysLogger.error(host.getIpAddress() + "_WindowsDisk");
    	}
    	
    	if(valueArray==null||valueArray.length==0)
    	{	
    		item.setMultiResults(null);
    		return;
    	}	
    	    	
    	List list = new ArrayList(3);
    	int usedSize = 0;
    	int totalSize = 0;
    	for(int i=0;i<valueArray.length;i++)
    	{	
    		if(!"1.3.6.1.2.1.25.2.1.4".equals(valueArray[i][0])) continue;
    		
    		MonitorResult mr = new MonitorResult();    		
    		mr.setEntity(valueArray[i][1].substring(0,1)); //盘符
    		try
    		{
    		   usedSize = Integer.parseInt(valueArray[i][3]);
    		   totalSize = Integer.parseInt(valueArray[i][2]);
    		       		  
    		   if(totalSize!=0)
    		   {	   
    		       mr.setValue((int)(usedSize/1024/1024)); //单位是GB
    		       long temp = (long)usedSize*100; //不转换为long可能会溢出
    		       mr.setPercentage((int)(temp/totalSize));
    		   }   
    		}
    		catch(NumberFormatException nfe)
    		{
    			mr.setPercentage(-1);
    			mr.setValue(-1);
    		}    		
    		list.add(mr);
    	}
    	item.setMultiResults(list);
    }        
}