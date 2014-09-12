/**
 * <p>Description:collect aix cpu utilization</p>
 * <p>Company: dhcc.com</p>
 * @author afu
 * @project afunms
 * @date 2007-03-08
 */

package com.afunms.monitor.executor;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.File;

import cn.org.xone.telnet.TelnetWrapper;
import org.jdom.Element;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;

import com.afunms.monitor.executor.base.*;
import com.afunms.monitor.item.CommonItem;
import com.afunms.monitor.item.base.*;
import com.afunms.common.util.SysLogger;
import com.afunms.polling.base.*;
import com.afunms.polling.node.*;

public class DiskArray2 
{
   private static HashMap<String,String> disksMap;
   static
   {
	   SAXBuilder builder = new SAXBuilder();
	   disksMap = new HashMap<String,String>();
       try
	   {        	
           Document doc = builder.build(new File("D:/afunms/src/disks.xml"));            
           List disks = doc.getRootElement().getChildren("disk");
           for(int i=0;i<disks.size();i++)
           {
        	   Element ele = (Element)disks.get(i);
        	   disksMap.put(ele.getChildText("ip"),ele.getChildText("tag"));   
           }
	   }
       catch(Exception e)
	   {
     	    SysLogger.error("DiskArray.static",e);
	   }
   
   }
   
   public DiskArray2()
   {
   }

//   public void collectData(Node node,MonitoredItem monitoredItem)
//   {
//	   CommonItem item = (CommonItem)monitoredItem;  
//	   Host host = (Host)node;	   
//	   item.setSingleResult(-1);
//	   item.setAlarm(false);
//	   
//	   boolean exist = false;
//	   String diskTag = null;
//	   if(disksMap.get(host.getIpAddress())!=null)
//		   diskTag = disksMap.get(host.getIpAddress());
//	   if(diskTag==null) return;
//	   
//	   if(host.getSysOid().startsWith("1.3.6.1.4.1.311."))
//		  exist = diskExist(host.getIpAddress(),host.getCommunity(),diskTag);
//	   else
//   	      exist = fileSystemExist(host.getIpAddress(),host.getUser(),host.getPassword(),host.getPrompt(),diskTag);  
//	   if(!exist)
//	   {
//		   item.setAlarm(true);
//		   System.out.println(node.getIpAddress() + "不能连接磁盘阵列");
//		   item.setAlarmInfo("不能连接磁盘阵列");
//	   }
//   }	
   
   public void vgExist(String ip,String user,String password,String prompt,String command)
   {
       TelnetWrapper telnet = new TelnetWrapper();
       boolean result = false;
       try
       {
           telnet.connect(ip, 23, 5000);
		   telnet.login(user, password);			
		   telnet.setPrompt(prompt);
		   telnet.waitfor(prompt);			
		   
	       String response = telnet.send(command);
	       
           String[] temp = response.split("\n");
       }
       catch(Exception e)
       {
    	   SysLogger.error("--------DiskArray.fileSystemExist()---------",e);
       }
	   finally
	   {
		   try
		   {
		       telnet.disconnect();
		   }
		   catch(Exception e){}		   
	   }              
       //return result;
   }
      
   public void analyseData(Node node,MonitoredItem item)
   {
	   return;
   } 
   
   public static void main(String[] args)
   {
	   DiskArray2 dr = new DiskArray2();
	   dr.vgExist("10.10.10.3", "wg","wg", "$", "lsvg -p oravg");	   
   }
}
