/**
 * <p>Description:collect aix cpu utilization</p>
 * <p>Company: dhcc.com</p>
 * @author afu
 * @project afunms
 * @date 2007-03-08
 */

package com.afunms.monitor.executor;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.org.xone.telnet.TelnetWrapper;

import com.afunms.monitor.executor.base.MonitorInterface;
import com.afunms.monitor.item.CommonItem;
import com.afunms.monitor.item.base.MonitorResult;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.common.util.SysLogger;
import com.afunms.polling.base.*;
import com.afunms.polling.node.*;
import com.afunms.topology.model.HostNode;
import com.afunms.monitor.executor.base.BaseMonitor;

public class AixFileSystem extends BaseMonitor implements MonitorInterface
{
   public AixFileSystem()
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
	   if(host.getUser()==null||"".equals(host.getUser()))
	   {
		   item.setMultiResults(null);
		   return;
	   }	   
	  // System.out.println(node.getIpAddress() + "-----------AixFileSystem-----------");
   	   List list = getFileSystem(host.getIpAddress(),host.getUser(),host.getPassword(),host.getPrompt());
   	   item.setMultiResults(list);
   }	
   
   public List getFileSystem(String ip,String user,String password,String prompt)
   {
       TelnetWrapper telnet = new TelnetWrapper();
       List list = new ArrayList();
       try
       {
           telnet.connect(ip, 23, 5000);
		   telnet.login(user, password);			
		   telnet.setPrompt(prompt);
		   telnet.waitfor(prompt);			
		   
	       String response = telnet.send("df -k");
	       Pattern p = Pattern.compile("[\\S ]+\r\n");
	       Matcher m = p.matcher(response);
	       boolean first = true;
	       
	       while(m.find())
	          if(first)
	             first = false;
	          else
	          {
	        	  MonitorResult mr = new MonitorResult(); 
	        	  String filesystem = response.substring(m.start(), m.end());
	              Pattern pFilesystem = Pattern.compile("[\\S]+");
	              Matcher mFilesystem = pFilesystem.matcher(filesystem);
	              mFilesystem.find();
	              String filesystemname = filesystem.substring(mFilesystem.start(), mFilesystem.end());
	              mFilesystem.find();
	              String blocks = filesystem.substring(mFilesystem.start(), mFilesystem.end());
	              mFilesystem.find();
	              String free = filesystem.substring(mFilesystem.start(), mFilesystem.end());
	              mFilesystem.find();
	              String used = filesystem.substring(mFilesystem.start(), mFilesystem.end());
	              int index = used.indexOf(37);
	              if(index > 0)
	                 used = used.substring(0, index);
	              mFilesystem.find();
	              String iused = filesystem.substring(mFilesystem.start(), mFilesystem.end());
	              mFilesystem.find();
	              String iusedpercent = filesystem.substring(mFilesystem.start(), mFilesystem.end());
	              index = iusedpercent.indexOf(37);
	              if(index > 0)
	                 iusedpercent = iusedpercent.substring(0, index);
	              mFilesystem.find();
	              String mounted = filesystem.substring(mFilesystem.start(), mFilesystem.end());
	              if("Mounted".equals(mounted)||"/proc".equals(mounted)) continue;
	              
	              mr.setEntity(mounted);
	              mr.setPercentage(Double.parseDouble(used));
	              mr.setValue(Double.parseDouble(iused));
	              list.add(mr);
	          }
       }
       catch(Exception e)
       {
    	   SysLogger.info("--------aix.getFileSystem()---------");
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
       return list;
   }
   
   public static void main(String[] args)
   {
	   AixFileSystem ac = new AixFileSystem();
	   ac.getFileSystem("192.168.5.99", "root", "root","#");	   
   }
}
