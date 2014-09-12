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

public class AixCpu extends BaseMonitor implements MonitorInterface
{
   public AixCpu()
   {
   }
   public void collectData(HostNode node){
	   
   }
   public Hashtable collect_Data(HostNode node){
	   return null;
   }
   public void collectData(Node node,MonitoredItem monitoredItem)
   {
	   Host host = (Host)node;
	   CommonItem item = (CommonItem)monitoredItem;
	   if(host.getUser()==null||"".equals(host.getUser()))
	   {
		   item.setMultiResults(null);
		   return;
	   }
   	   List result = getCpuRate(host.getIpAddress(),host.getUser(),host.getPassword(),host.getPrompt());
   	   item.setMultiResults(result);
   }	
   
   private List getCpuRate(String ip,String user,String password,String prompt)
   {
       TelnetWrapper telnet = new TelnetWrapper();
       double cpuRate = 0;
       List list = new ArrayList(1);
       MonitorResult mr = new MonitorResult();
       try
       {
           telnet.connect(ip, 23, 3000);
		   telnet.login(user, password);			
		   telnet.setPrompt(prompt);
		   telnet.waitfor(prompt);			
		   
		   String cpuResponse = telnet.send("sar 1 3");
	       int loc = cpuResponse.lastIndexOf("\n");
	       cpuRate = 100 - Double.parseDouble(cpuResponse.substring(loc - 3, loc).trim());	       
	       mr.setPercentage(cpuRate);
	       mr.setEntity("1");
	       list.add(mr);
       }
       catch(Exception e)
       {
    	   SysLogger.info("--------aix.getCpuRate()---------");
    	   mr.setPercentage(-1);
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
	   AixCpu ac = new AixCpu();
	   System.out.println(ac.getCpuRate("10.10.152.213", "root", "123456","#"));	   
   }
}
