/**
 * <p>Description:tomcat jvm利用率，freememory</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-12-03
 */

package com.afunms.monitor.executor;

import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

import com.afunms.monitor.executor.base.BaseMonitor;
import com.afunms.common.util.SysLogger;
import com.afunms.monitor.executor.base.MonitorInterface;
import com.afunms.monitor.item.CommonItem;
import com.afunms.monitor.item.base.MonitorResult;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Tomcat;
import com.afunms.topology.model.HostNode;
import com.afunms.application.util.TomcatHelper;

public class TomcatJVM extends BaseMonitor implements MonitorInterface
{
	public TomcatJVM()
	{
		
	}
	   public void collectData(HostNode node){
		   
	   }
	   public Hashtable collect_Data(HostNode node){
		   return null;
	   }
	public void collectData(Node node,MonitoredItem monitoredItem)
	{
		Tomcat tomcat = (Tomcat)node;		
		CommonItem item = (CommonItem)monitoredItem;
        HttpClient client = null;   
        MonitorResult mr = new MonitorResult();
        List list = new ArrayList();
        try
        {                       
            client = new HttpClient();
            HttpMethod method = new GetMethod(tomcat.getJspUrl()); 
            method.setDoAuthentication(true); 
            client.setConnectionTimeout(3000);
            client.executeMethod(method);                                               
            method.releaseConnection();            
            
            TomcatHelper th = new TomcatHelper(tomcat.getXmlUrl());            
            mr.setEntity("jvm");
            mr.setPercentage(th.getJVMUtil());
            mr.setValue((long)th.getFreeMemory());              
        }
        catch(Exception e)
        {
        	SysLogger.error("Tomcat_" + tomcat.getIpAddress() + "_收采jvm数据出错",e);
            mr.setEntity("jvm");
            mr.setPercentage(-1);
            mr.setValue(-1);
        }
        finally
        {            
            client = null;
        }
        list.add(mr);
        item.setMultiResults(list);
	}	
}