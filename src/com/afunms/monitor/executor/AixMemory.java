/**
 * <p>Description:collect aix cpu utilization</p>
 * <p>Company: dhcc.com</p>
 * @author afu
 * @project afunms
 * @date 2007-03-08
 */

package com.afunms.monitor.executor;

import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.DecimalFormat;

import cn.org.xone.telnet.TelnetWrapper;

import com.afunms.monitor.executor.base.MonitorInterface;
import com.afunms.monitor.item.CommonItem;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.common.util.SysLogger;
import com.afunms.polling.base.*;
import com.afunms.polling.node.*;
import com.afunms.topology.model.HostNode;
import com.afunms.monitor.executor.base.BaseMonitor;

public class AixMemory extends BaseMonitor implements MonitorInterface
{
   public AixMemory()
   {
   }
   public void collectData(HostNode node){
	   
   }
   public Hashtable collect_Data(HostNode node){
	   return null;
   }
   public void collectData(Node node,MonitoredItem monitoredItem)
   {
	   CommonItem item = (CommonItem)monitoredItem;  
	   Host host = (Host)node;
	  // System.out.println(host.getIpAddress() + ":" + host.getUser() + "," + host.getPassword());
	   if(host.getUser()==null||"".equals(host.getUser()))
	   {
		   item.setMultiResults(null);
		   return;
	   }	   
   	   double result = getMemoryRate(host.getIpAddress(),host.getUser(),host.getPassword(),host.getPrompt());
   	   item.setSingleResult(result);
   }	
   
   public double getMemoryRate(String ip,String user,String password,String prompt)
   {
	   System.out.println(ip+","+user+","+password+","+prompt);
	   TelnetWrapper telnet = new TelnetWrapper();
       double memoryRate = 0;       
       try
       {
           telnet.connect(ip, 23, 3000);
		   telnet.login(user, password);			
		   telnet.setPrompt(prompt);
		   telnet.waitfor(prompt);			
		   
		   String memoryResponse = telnet.send("svmon -G");
	       DecimalFormat df = new DecimalFormat("#");
	       Pattern p = Pattern.compile("memory[ ]+[0-9]+[ ]+[0-9]+");
	       Matcher m = p.matcher(memoryResponse);
	       m.find();
	       String memoryStr = memoryResponse.substring(m.start(), m.end());
	       p = Pattern.compile("[ ]+[0-9]+");
	       m = p.matcher(memoryStr);
	       m.find();
	       double memorysize = Double.parseDouble(memoryStr.substring(m.start(), m.end()));
	       m.find();
	       double memoryused = Double.parseDouble(memoryStr.substring(m.start(), m.end()));	       
		   memoryRate = Double.parseDouble(df.format(memoryused / memorysize * 100));
       }
       catch(Exception e)
       {
    	   SysLogger.info("--------aix.getMemoryRate()---------");
    	   e.printStackTrace();
       }
	   finally
	   {
		   try
		   {
		       telnet.disconnect();
		   }
		   catch(Exception e){}		   
	   }              
       return memoryRate;
   }
   
   public static void main(String[] args)
   {
	   AixMemory ac = new AixMemory();
	   System.out.println(ac.getMemoryRate("192.168.5.99", "root", "root","#"));	   
   }
}
