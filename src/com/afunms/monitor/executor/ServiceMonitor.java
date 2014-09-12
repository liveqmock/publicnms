/**
 * <p>Description:test port</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-26
 */

package com.afunms.monitor.executor;

import java.net.*;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;

import com.afunms.monitor.executor.base.*;
import com.afunms.monitor.item.ServiceItem;
import com.afunms.sysset.model.Service;
import com.afunms.topology.model.HostNode;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.base.*;
import com.afunms.initialize.ResourceCenter;

public class ServiceMonitor extends BaseMonitor implements MonitorInterface
{
	public ServiceMonitor()
	{	
	}
	   public void collectData(HostNode node){
		   
	   }
	   public Hashtable collect_Data(HostNode node){
		   return null;
	   }
	public void collectData(Node node,MonitoredItem monitoredItem)
	{
		ServiceItem item = (ServiceItem)monitoredItem;
		List list = ResourceCenter.getInstance().getServiceList();		
		for(int i=0;i<list.size();i++)
		{
			Service service = (Service)list.get(i);
            Socket socket = new Socket();
            int result = 0;
	        try
	        {
               InetAddress addr = InetAddress.getByName(node.getIpAddress());           
               SocketAddress sockaddr = new InetSocketAddress(addr,service.getPort());            
               socket.connect(sockaddr,1000);
               result = 1;
            }
	 	    catch(Exception ioe)
	 	    {	 		
	 	    }	      
 	        finally
 	        {
 	    	   try
 	    	   {
 	    	      socket.close();
 	    	   }
 	    	   catch(IOException ioe){}
 	        }
 	        item.getServicesStatus()[i] = result;
		}  	    
	}	
}