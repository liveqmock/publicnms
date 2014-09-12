/**
 * <p>Description:Monitor Factory</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-27
 */

package com.afunms.monitor.executor.base;

import java.io.File;
import java.util.*;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.afunms.monitor.executor.base.MonitorInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.initialize.ResourceCenter;
import com.afunms.monitor.item.base.*;

public class MonitorFactory
{
   private static Hashtable monitorClassMap;
   private static Hashtable moClassMap;
   private static Hashtable itemClassNameMap;
   private static List moClassList;
   
   static
   {
	   monitorClassMap = new Hashtable();
	   moClassMap = new Hashtable();
	   itemClassNameMap = new Hashtable();
	   moClassList = new ArrayList();
	   
	   SAXBuilder builder = new SAXBuilder();
	   try
	   {        			   
		   Document doc = builder.build(new File(ResourceCenter.getInstance().getSysPath() + "WEB-INF/classes/monitor.xml"));          
           List list = doc.getRootElement().getChildren("monitor");
           Iterator it = list.iterator();
           while(it.hasNext())
           {            	
        	   Element element = (Element)it.next();        	
        	   String moid = element.getChildText("moid");  
        	   //monitorClassMap.put(moid,Class.forName(element.getChildText("executor_class")).newInstance());
        	   //itemClassNameMap.put(moid,element.getChildText("item_class"));
        	   
        	   MonitorObject mo = new MonitorObject();
    		   mo.setMoid(moid);
     		   mo.setName(element.getChildText("name"));
     		   mo.setDescr(element.getChildText("descr"));
     		   mo.setCategory(element.getChildText("category"));
     		   mo.setResultType(Integer.parseInt(element.getChildText("result_type")));     		   
     		   mo.setThreshold(Integer.parseInt(element.getChildText("threshold")));     		   
     		   mo.setUnit(element.getChildText("unit"));      			
     		   mo.setCompare(Integer.parseInt(element.getChildText("compare")));
     		   mo.setCompareType(Integer.parseInt(element.getChild("compare").getAttributeValue("type")));
     		   mo.setUpperTimes(Integer.parseInt(element.getChildText("upper_times"))); 
     		   mo.setAlarmInfo(element.getChildText("alarm_info"));
     		   mo.setAlarmLevel(Integer.parseInt(element.getChildText("alarm_level"))); 
     		   mo.setPollInterval(Integer.parseInt(element.getChildText("poll_interval")));
     		   mo.setIntervalUnit(element.getChild("poll_interval").getAttributeValue("unit"));
     		   mo.setDefault(Boolean.parseBoolean(element.getChildText("default")));
     		   mo.setEnabled(Boolean.parseBoolean(element.getChildText("enabled")));     			     			     			     
     		   mo.setShowInTopo(Boolean.parseBoolean(element.getChildText("show_in_topo"))); 
     		   mo.setNodetype(element.getChildText("nodetype"));
     		   mo.setSubentity(element.getChildText("subentity"));
     		   mo.setLimenvalue0(Integer.parseInt(element.getChildText("limenvalue0")));
     		   mo.setLimenvalue1(Integer.parseInt(element.getChildText("limenvalue1")));
     		   mo.setLimenvalue2(Integer.parseInt(element.getChildText("limenvalue2")));
     		   mo.setTime0(Integer.parseInt(element.getChildText("time0")));
     		   mo.setTime1(Integer.parseInt(element.getChildText("time1")));
     		   mo.setTime2(Integer.parseInt(element.getChildText("time2")));
     		   mo.setSms0(Integer.parseInt(element.getChildText("sms0")));
     		   mo.setSms1(Integer.parseInt(element.getChildText("sms1")));
     		   mo.setSms2(Integer.parseInt(element.getChildText("sms2")));
     		   //mo.setUnit(element.getChildText("unit"));
     		   moClassMap.put(moid, mo);
     		   moClassList.add(mo);
           }                   
	   }
       catch(Exception e)
	   {
    	    SysLogger.error("MonitorFactory.static",e);
	   }        
   }
   
   private MonitorFactory()
   {	   
   }

   public static MonitorInterface getMonitor(String moid)
   {   	   
	   return (MonitorInterface)monitorClassMap.get(moid);
   }
   
   public static MonitorObject getMonitorObject(String moid)
   {   	   
	   return (MonitorObject)moClassMap.get(moid);
   }

   public static MonitoredItem createItem(String moid)
   {   	 
	   MonitoredItem item = null; 
	   try
	   {
		   item = (MonitoredItem)Class.forName((String)itemClassNameMap.get(moid)).newInstance();			   
	   }
	   catch(Exception e)
	   {
		   SysLogger.error("MonitorFactory.createItem()",e);
	   }
	   return item;
   }
   
   
   public static List getMonitorObjectList()
   {
	   return moClassList;
   }
}