/**
 * <p>Description:tomcat监视器，这个类要放在被监视的tomcat</p>
 * <p>tomcat\server\webapps\manager\WEB-INF下,目的是生成一个xml</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-12-03
 */

package com.afunms.application.util;

import java.io.FileOutputStream;
import javax.management.*;
import java.util.*;

import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.Document;
import org.jdom.Element;
import org.apache.catalina.util.ServerInfo;
import org.apache.commons.modeler.Registry;

public class TomcatMonitor
{
	public TomcatMonitor()
	{		
	}
	
	public void createMonitorXml(String realPath)
	{
		String fullPath = realPath + "tomcat_monitor.xml";
		try
		{
		    //------------创建xml-------------------
			Element tomcatMonitor = new Element("tomcat_monitor");
		    tomcatMonitor.addContent(createServerInformation());
		    tomcatMonitor.addContent(createJVMInformation());
		    tomcatMonitor.addContent(createApplications());
		    
		    //------------写xml文件-------------------
			Format format = Format.getCompactFormat();
		    format.setEncoding("GB2312");
		    format.setIndent(" ");
		    XMLOutputter serializer = new XMLOutputter(format);
		    FileOutputStream fos = new FileOutputStream(fullPath);
		    
		    Document doc = new Document(tomcatMonitor);
		    serializer.output(doc, fos);
		    fos.close();
		}
		catch(Exception e)
		{
			System.out.println("Error in TomcatMonitor.createMonitorXml()");
		}
	}
	
	/**
	 * 生成server_information节点
	 */
	private Element createServerInformation()
	{
	    Element tomcatVersion = new Element("tomcat_version");
	    tomcatVersion.setText(ServerInfo.getServerInfo());
	    
	    Element jvmVersion = new Element("jvm_version");
	    jvmVersion.setText(System.getProperty("java.runtime.version"));
		
	    Element jvmVendor = new Element("jvm_vendor");
	    jvmVendor.setText(System.getProperty("java.vm.vendor"));

	    Element OSName = new Element("os_name");
	    OSName.setText(System.getProperty("os.name"));
	    
	    Element OSVersion = new Element("os_version");
	    OSVersion.setText(System.getProperty("os.version"));
	    
	    Element serverInfo = new Element("server_information");
	    serverInfo.addContent(tomcatVersion);
	    serverInfo.addContent(jvmVersion);
	    serverInfo.addContent(jvmVendor);
	    serverInfo.addContent(OSName);
	    serverInfo.addContent(OSVersion);
	    
	    return serverInfo;
	}
	
	/**
	 * 生成jvm_information节点
	 */
	private Element createJVMInformation()
	{
	    Element freeMemory = new Element("free_memory");
	    freeMemory.setText(String.valueOf(Runtime.getRuntime().freeMemory()));
	    
	    Element totalMemory = new Element("total_memory");
	    totalMemory.setText(String.valueOf(Runtime.getRuntime().totalMemory()));
		
	    Element maxMemory = new Element("max_memory");
	    maxMemory.setText(String.valueOf(Runtime.getRuntime().maxMemory()));

	    Element jvmInfo = new Element("jvm_information");
	    jvmInfo.addContent(freeMemory);
	    jvmInfo.addContent(totalMemory);
	    jvmInfo.addContent(maxMemory);
	    
	    return jvmInfo;
	}
	
	/**
	 * 生成应用列表
	 */
	private Element createApplications()
	{
		Element applications = null;
		try
		{
			applications = new Element("applications");
			
			MBeanServer mBeanServer = Registry.getServer();
	        ObjectName queryHosts = new ObjectName("*:j2eeType=WebModule,*");
	        Set hostsON = mBeanServer.queryNames(queryHosts, null);

			Iterator iterator = hostsON.iterator();
			while(iterator.hasNext())
			{
				ObjectName contextON = (ObjectName)iterator.next();
		        String webModuleName = contextON.getKeyProperty("name");	        	            
				String hostName = null;
		        String contextName = null;
		        if(webModuleName.startsWith("//"))
		             webModuleName = webModuleName.substring(2);
		        
		        int slash = webModuleName.indexOf("/");
		        if(slash != -1)
		        {
		            hostName = webModuleName.substring(0, slash);
		            contextName = webModuleName.substring(slash);
		        } 
				else continue;
	
		        if("/".equals(contextName)) continue;
		        
		        Element oneApp = new Element("application_information");
		        try
		        {	       
		            ObjectName queryManager = new ObjectName(contextON.getDomain() + ":type=Manager,path=" + contextName + ",host=" + hostName + ",*");
		            Set managersON = mBeanServer.queryNames(queryManager, null);
		            ObjectName managerON = null;
		            for(Iterator iterator2 = managersON.iterator(); iterator2.hasNext();)
		               managerON = (ObjectName)iterator2.next();
	
		            Element wmn = new Element("web_module_name");
		            wmn.setText(contextName.substring(1));
			       
		            Element as = new Element("active_sessions");
			        as.setText(mBeanServer.getAttribute(managerON, "activeSessions").toString());
			       
			        Element sc = new Element("session_count");
			        sc.setText(mBeanServer.getAttribute(managerON, "sessionCounter").toString());
			       
			        Element mas = new Element("max_active_sessions");
			        mas.setText(mBeanServer.getAttribute(managerON, "maxActive").toString());	
			        oneApp.addContent(wmn);
			        oneApp.addContent(as);
			        oneApp.addContent(sc);
			        oneApp.addContent(mas);
			        applications.addContent(oneApp);
		        }
		        catch(Exception e)
		        {
		        	System.out.println("Error in TomcatMonitor.createApplications()-2");
		        }//end_try
			}//end_while
		}
        catch(Exception e)
        {
        	System.out.println("Error in TomcatMonitor.createApplications()-1");
        }//end_try
        return applications;
	}
}