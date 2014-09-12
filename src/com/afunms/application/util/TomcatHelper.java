/**
 * <p>Description:tomcat util</p>
 * <p>Company: afunms</p>
 * @author miiwill
 * @project afunms
 * @date 2006-12-06
 */

package com.afunms.application.util;

import java.net.URL;
import java.net.URLConnection;

import org.jdom.input.SAXBuilder;
import org.jdom.Document;
import org.jdom.Element;

import com.afunms.common.util.*;
import com.afunms.inform.dao.NewDataDao;
import com.afunms.report.jfree.ChartCreator;

public class TomcatHelper
{
    private Element root;
    public TomcatHelper(String xmlUrl)
    {
    	URLConnection urlConn = null;
    	SAXBuilder builder = null;
    	try
    	{
    	    URL url = new URL(xmlUrl);       
    	    urlConn = url.openConnection();
    	    urlConn.connect();
    		
  		    builder = new SAXBuilder();
  		    Document doc = builder.build(urlConn.getInputStream());
  		    root = doc.getRootElement();
    	}
    	catch(Exception e)
    	{
    		SysLogger.error("TomcatHelper.TomcatHelper()",e); 
    	}
    } 
    
    public double getFreeMemory()
    {
        String temp = root.getChild("jvm_information").getChildText("free_memory");        
        return SysUtil.formatDouble(Double.parseDouble(temp), 1024 * 1024);
    }
    
    public double getTotalMemory()
    {
        String temp = root.getChild("jvm_information").getChildText("total_memory");
        return SysUtil.formatDouble(Double.parseDouble(temp), 1024 * 1024);
    }
    
    public double getMaxMemory()
    {
        String temp = root.getChild("jvm_information").getChildText("max_memory");        
        return SysUtil.formatDouble(Double.parseDouble(temp), 1024 * 1024); 
    }
        
    public double getJVMUtil()
    {
    	double fm = Double.parseDouble(root.getChild("jvm_information").getChildText("free_memory"));
    	double tm = Double.parseDouble(root.getChild("jvm_information").getChildText("total_memory"));
    		
        return SysUtil.formatDouble((tm - fm) * 100, tm); 
    } 
    
    public String getTomcatVersion()
    {
        return root.getChild("server_information").getChildText("tomcat_version");
    }

    public String getJVMVersion()
    {
        return root.getChild("server_information").getChildText("jvm_version");
    }

    public String getJVMVendor()
    {
        return root.getChild("server_information").getChildText("jvm_vendor");
    }
    
    public String getOSName()
    {
        return root.getChild("server_information").getChildText("os_name");
    }

    public String getOSVersion()
    {
        return root.getChild("server_information").getChildText("os_version");
    }
    
    public Element getAppList()
    {
        return root.getChild("applications");
    }
    
    public String getMemoryInfo()
    {
    	StringBuffer info = new StringBuffer(100);
    	info.append("可用内存：");
    	info.append(getFreeMemory());
    	info.append("MB,总内存：");
    	info.append(getTotalMemory());
    	info.append("MB,最大内存：");
    	info.append(getMaxMemory());
    	info.append("MB");    	
    	return info.toString();
    }
    
	/**
	 * 可用内存曲线图
	 */
	public String getFreeJVMChart(int nodeId)
	{
		NewDataDao dao = new NewDataDao();
		double[][] dataSet = dao.multiStat(SysUtil.getCurrentDate(),nodeId,"051002",true,3);
		if(dataSet==null) return null;
			
		String[] rowKeys = dao.getRowKeys();
		String[] colKeys = dao.getColKeys();

		int limit = 5;  //只取最新的5条数据
		if(colKeys.length<=limit)  
		   return ChartCreator.createLineChart(dataSet,rowKeys,colKeys,"时间","可用内存(M)","",300,200);
		
		String[] colKeys2 = new String[limit];    
		double[][] dataSet2 = new double[rowKeys.length][limit];
        for(int j=0;j<limit;j++)
           colKeys2[j] = colKeys[j];
           
        for(int i=0;i<rowKeys.length;i++)
           for(int j=0;j<limit;j++)
              dataSet2[i][j] = dataSet[i][j];	    
		return ChartCreator.createLineChart(dataSet2,rowKeys,colKeys2,"时间","可用内存(M)","",300,200);
	}    
}
