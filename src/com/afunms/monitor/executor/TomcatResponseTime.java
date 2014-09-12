/**
 * <p>Description:tomcat响应时间</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-12-08
 */

package com.afunms.monitor.executor;

import java.util.Hashtable;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.methods.GetMethod;

import com.afunms.common.util.SysLogger;
import com.afunms.monitor.executor.base.BaseMonitor;
import com.afunms.monitor.executor.base.MonitorInterface;
import com.afunms.monitor.item.CommonItem;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Tomcat;
import com.afunms.topology.model.HostNode;

public class TomcatResponseTime extends BaseMonitor implements MonitorInterface
{
	public TomcatResponseTime()
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
		String tomcatManagerURL = "http://" + tomcat.getIpAddress() + ":" + tomcat.getPort() + "/manager/status";
        
        HttpClient client = null;
        int result = 0;
        try
        {                       
            client = new HttpClient();
            UsernamePasswordCredentials upc = new UsernamePasswordCredentials(tomcat.getUser(),tomcat.getPassword());
            client.getState().setCredentials(null, null, upc);                                                     
            HttpMethod method = new GetMethod(tomcatManagerURL);             
            method.setDoAuthentication(true); 
            client.setConnectionTimeout(3000);
            long startTime = System.currentTimeMillis();
            client.executeMethod(method);                        
            result = (int)(System.currentTimeMillis() - startTime);
            
            int statusCode = method.getStatusCode();           
            if (statusCode == 401)
            {
            	SysLogger.info("Tomcat_" + tomcat.getIpAddress() + "_用户名或密码不正确");
            	result = -1;
            }
            else if(statusCode == 400 || statusCode == 403 || statusCode == 404)
            {
            	SysLogger.info("Tomcat_" + tomcat.getIpAddress() + "_管理应用模块没部署在Tomcat服务器上,这是管理Tomcat必须的模块");
            	result = -1;
            }
            if(statusCode == 500)
            {
            	SysLogger.info("Tomcat_" + tomcat.getIpAddress() + "_服务器忙");
            	result = -1;
            }
            method.releaseConnection();            
        }
        catch(Exception e)
        {
        	SysLogger.info("Tomcat_" + tomcat.getIpAddress() + "_不可用");
        	result = -1;                                       
        }
        finally
        {            
            client = null;
        }        
        item.setSingleResult(result);
        if(result!=-1)
           tomcat.setNormalTimes(tomcat.getNormalTimes() + 1);
        else
           tomcat.setFailTimes(tomcat.getFailTimes() + 1);	
	}
	
	public void analyseData(Node node,MonitoredItem item)
	{		
	}	
}