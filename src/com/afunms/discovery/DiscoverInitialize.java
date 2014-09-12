/**
 * <p>Description:topology discovery initialize</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-12
 */

package com.afunms.discovery;

import java.io.File;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;

import com.afunms.topology.dao.DiscoverConfigDao;
import com.afunms.sysset.dao.DeviceTypeDao;
import com.afunms.sysset.dao.ServiceDao;
import com.afunms.common.util.SysLogger;
import com.afunms.initialize.ResourceCenter;

public class DiscoverInitialize
{
   private DiscoverResource dr;
  
   public DiscoverInitialize()
   {
	  try{
      dr = DiscoverResource.getInstance();	  
      if(dr == null)SysLogger.error("##########初始化DiscoverResource错误##############");
	  }catch(Exception e){
		  e.printStackTrace();
	  }
   }
  
   public void init()
   {
	  loadThreadInfo();
	  loadDiscoverConfig();
	  loadDeviceType();
	  loadService();
   }

   private void loadThreadInfo()
   {
      SAXBuilder builder = new SAXBuilder();
      try
      {        	
          Document doc = builder.build(new File(ResourceCenter.getInstance().getSysPath() + "WEB-INF/classes/system-config.xml")); 
          int mt = Integer.parseInt(doc.getRootElement().getChildText("max_threads"));   
          dr.setMaxThreads(mt);//设置最大线程数
         
          int pti = Integer.parseInt(doc.getRootElement().getChildText("discover_per_thread_ips"));         
       	  dr.setPerThreadIps(pti); //设置每个线程允许执行的最大IP数量
	  }
      catch(Exception e)
	  {
    	  e.printStackTrace();
    	  //dr.setMaxThreads(200);
    	  //dr.setPerThreadIps(30);
    	  SysLogger.error("DiscoverInitializtion.loadThreadInfo()",e);
	  }
      SysLogger.info("$$$$$$$$$$$$$$$$$");
      SysLogger.info("$$$$  设置每个线程允许执行的最大IP数量   "+dr.getPerThreadIps()+" $$$$");
      SysLogger.info("$$$$$$$$$$$$$$$$$");
   }

   public void loadDiscoverConfig()
   {
	   DiscoverConfigDao dao = new DiscoverConfigDao();
	   dr.setCommunitySet(dao.loadCommunity());
	   dr.getCommunitySet().add("public");
	   
	   dr.setShieldSet(dao.loadShield());
	   dr.setNetshieldList(dao.loadNetShield());
	   dr.setNetincludeList(dao.loadIncludeNet());
	   dr.setSpecifiedCommunity(dao.loadSpecified());
	   dao.close();
   }
   
   private void loadDeviceType()
   {
	   DeviceTypeDao dao = new DeviceTypeDao();
	   dr.setDeviceType(dao.loadDeviceType());
   } 

   private void loadService()
   {
	   ServiceDao dao = new ServiceDao();
	   dr.setServiceList(dao.loadService(0));
   }        
}
