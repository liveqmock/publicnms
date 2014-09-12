/**
 * <p>Description:discovery process monitor</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-13
 */

package com.afunms.discovery;

import java.util.List;

import com.afunms.topology.dao.*;
import com.afunms.topology.model.*;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;

public class DiscoverMonitor implements DiscoverMonitorInterface
{		
    private static DiscoverMonitor instance = new DiscoverMonitor();
	
	private String startTime = null;
	private String endTime = null;
	private String elapseTime = null;
	private int subNetTotal;
	private int hostTotal;
	private int discoverNode;
	private int refreshTimes;

	private boolean completed = false;
    
	public static DiscoverMonitor getInstance()
	{
       return instance;       
    }
		
	public void unload() 
	{		
		SysLogger.info("DiscoverMonitor.unload()");
		instance = null;
	}
	
	public String getResultTable()		
	{
		if(completed)
		   return createTableFromDB();
		else
		   return createTableFromMemory();
	}
	
	private String createTableFromMemory()
	{
		StringBuffer tableSb = new StringBuffer(1000);		
		tableSb.append("<table width='100%' border=1 cellspacing=0 cellpadding=0 bordercolorlight='#000000' bordercolordark='#FFFFFF'>");
		tableSb.append("<tr bgcolor='#D4E1D5'><td><b>ID</b></td><td><b>IP</b></td><td><b>名称</b></td>");
		tableSb.append("<td><b>类型</b></td><td><b>状态</b></td></tr>");
				
		List hostList = DiscoverEngine.getInstance().getHostList();	
		if(hostList!=null&&hostList.size()!=0)
		{
		   tableSb.append("<tr class='othertr'><td colspan=5 align='center'>---设备---</td></tr>");
	       for(int i=0;i<hostList.size();i++)
	       {
  	           Host node = (Host)hostList.get(i);
	           tableSb.append("<tr class='othertr'><td>");
		       tableSb.append(node.getId());
		       tableSb.append("</td><td>");
		       tableSb.append(node.getIpAddress());
		       tableSb.append("</td><td>");
		       tableSb.append(node.getSysName());
		       tableSb.append("</td><td>");
		       if(node.getCategory()==1)
		          tableSb.append("路由器");
		       else if(node.getCategory()==2)
		       	  tableSb.append("三层交换");
		       else if(node.getCategory()==3)
		       	  tableSb.append("2层交换");
		       else if(node.getCategory()==4)
			      tableSb.append("服务器");
		       else if(node.getCategory()==6)
				      tableSb.append("防火墙");
		       else if(node.getCategory()==7)
				      tableSb.append("无线接入设备");
		       else
			      tableSb.append("打印机");	       
		       tableSb.append("</td><td>");
		       
		       if(node.isDiscovered())
		       	  tableSb.append("1</td></tr>");
		       else
		       	  tableSb.append("<font color='red'>0</font></td></tr>");
	       }    
	    }	
		tableSb.append("</table>\n");
		tableSb.append("<table width='100%' border=1 cellspacing=0 cellpadding=0 bordercolorlight='#000000' bordercolordark='#FFFFFF'>\n");
	    
		List subNetList = DiscoverEngine.getInstance().getSubNetList();	    	    
	    if(subNetList!=null&&subNetList.size()!=0)
	    {	
		    tableSb.append("<tr class='othertr'><td colspan=3 align='center'>---子网---</td></tr>");  
		    tableSb.append("<tr class='firsttr'><td><b>ID</b></td><td><b>网络地址</b></td><td><b>子网掩码</b></td></tr>");
		    for(int i=0;i<subNetList.size();i++)
		    {
		       SubNet node = (SubNet)subNetList.get(i);
		       tableSb.append("<tr class='othertr'><td>");
		       tableSb.append(node.getId());
		       tableSb.append("</td><td>");
		       tableSb.append(node.getNetAddress());
		       tableSb.append("</td><td>");
		       tableSb.append(node.getNetMask());
		       tableSb.append("</td></tr>");
		    }	 
		    tableSb.append("</table>");
	    }    
	    return tableSb.toString();
	}

	private String createTableFromDB()
	{
		StringBuffer tableSb = new StringBuffer(1000);		
		discoverNode = 0;		
		
		tableSb.append("<table width='100%' border=1 cellspacing=0 cellpadding=0 bordercolorlight='#000000' bordercolordark='#FFFFFF'>\n");
		tableSb.append("<tr bgcolor='#D4E1D5'><td><b>ID</b></td><td><b>IP</b></td><td><b>名称</b></td>");
		tableSb.append("<td><b>类型</b></td></tr>\n");
				
		HostNodeDao dao = new HostNodeDao();
		List hostList = dao.loadAll();	
		if(hostList!=null&&hostList.size()!=0)
		{
		   tableSb.append("<tr class='othertr'><td colspan=4 align='center'>---设备---</td></tr>\n");
	       for(int i=0;i<hostList.size();i++)
	       {
  	           HostNode host = (HostNode)hostList.get(i);
	           tableSb.append("<tr class='othertr'><td>");
		       tableSb.append(host.getId());
		       tableSb.append("</td><td>");
		       tableSb.append(host.getIpAddress());
		       tableSb.append("</td><td>");
		       tableSb.append(host.getSysName());
		       tableSb.append("</td><td>");
		       if(host.getCategory()==1)
		          tableSb.append("路由器");
		       else if(host.getCategory()==2)
		       	  tableSb.append("三层交换");
		       else if(host.getCategory()==3)
		       	  tableSb.append("2层交换");
		       else if(host.getCategory()==4)
			      tableSb.append("服务器");
		       else if(host.getCategory()==6)
				      tableSb.append("防火墙");
		       else if(host.getCategory()==7)
				      tableSb.append("无线接入设备");
		       else
			      tableSb.append("打印机");	       
		       tableSb.append("</td></tr>\n");
	       }    
	    }	
	    tableSb.append("</table>\n");
	       
		SubnetDao netDao = new SubnetDao();
	    List subNetList = netDao.loadAll();	
	    tableSb.append("<table width='100%' border=1 cellspacing=0 cellpadding=0 bordercolorlight='#000000' bordercolordark='#FFFFFF'>\n");
	    if(subNetList!=null&&subNetList.size()!=0)
	    {	
		    tableSb.append("<tr class='othertr'><td colspan=3 align='center'>---子网---</td></tr>");  
		    tableSb.append("<tr class='firsttr'><td><b>ID</b></td><td><b>网络地址</b></td><td><b>子网掩码</b></td></tr>\n");
		    for(int i=0;i<subNetList.size();i++)
		    {
		       Subnet node = (Subnet)subNetList.get(i);
		       tableSb.append("<tr class='othertr'><td>");
		       tableSb.append(node.getId());
		       tableSb.append("</td><td>");
		       tableSb.append(node.getNetAddress());
		       tableSb.append("</td><td>");
		       tableSb.append(node.getNetMask());
		       tableSb.append("</td></tr>\n");
		    }	 
		    tableSb.append("</table>\n");
	    }    
	    return tableSb.toString();
	}
	
	public String getStartTime()
	{
		if(startTime==null)
		   return SysUtil.getCurrentTime();
		else
	       return startTime;
	}
	
	public void setStartTime(String startTime)
	{
	    this.startTime = startTime;
	}

	public String getEndTime()
	{
		if(endTime==null)
		   return "发现未完成...";
		else
		   return endTime;
	}
	
	public void setEndTime(String endTime)
	{
	    this.endTime = endTime;
	}
		
	public String getElapseTime()
	{
		if(endTime==null)			
		   elapseTime = SysUtil.diffTwoTime(startTime,SysUtil.getCurrentTime());
		else
		   elapseTime = SysUtil.diffTwoTime(startTime,endTime);
		return elapseTime;
	}
	
	public int getSubNetTotal()
	{
	    if(completed)
	       return subNetTotal;
	    else
	    {	
	    	subNetTotal = DiscoverEngine.getInstance().getSubNetList().size();
	    	return subNetTotal;
	    }	
	}
	
	public int getHostTotal()
	{
	    if(completed)
	       return hostTotal;
	    else
	    {	
	    	hostTotal = DiscoverEngine.getInstance().getHostList().size();
		    return hostTotal;
	    }   
	}
	
	public int getDiscoveredNodeTotal()
	{
		if(completed)
	       return discoverNode;
		else
		{	
		   List hostList = DiscoverEngine.getInstance().getHostList();
		   int total = 0;
		   if(hostList!=null&&hostList.size()!=0)
		   {
			   for(int i=0;i<hostList.size();i++)
			   {	   
			      Host node = (Host)hostList.get(i);
			      if(node.isDiscovered())
				     total++;
			   }			   
		   }
		   discoverNode = total;
		   return discoverNode;
		}   
	}
	
	public boolean isCompleted() 
	{
		return completed;
	}

	public void setCompleted(boolean completed) 
	{
		this.completed = completed;
	}

	public int getRefreshTimes() {
		return refreshTimes;
	}

	public void setRefreshTimes() {
		refreshTimes++;
	}
}