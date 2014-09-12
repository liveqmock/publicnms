/**
 * <p>Description:topology discovery resource</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-12
 */

package com.afunms.discovery;

import java.util.*;

import com.afunms.common.util.SysLogger;

public class DiscoverResource
{
    private int maxThreads;    //tomcat最大线程数
    private int perThreadIps;  //每个线程处理的IP数
    private Set communitySet;  //全局共同体集合
    private Set shieldSet;     //要屏蔽的网段集合
    private Map SpecifiedCommunity;  //指定设备的community,key为IP信息，value为读团体信息
    private Map deviceType;       //已知设备
    private List serviceList;
    private String community;//团体名称
    private List netshieldList;//需要屏蔽的网段
    private List netincludeList;//只需发现网段
    private Set failedList;     //要屏蔽的网段集合

	private static DiscoverResource instance = new DiscoverResource();
    public static DiscoverResource getInstance()
    {
    	if(instance == null) instance = new DiscoverResource();//增加重新发现,需要重新初始化该类
       return instance;
    }
    
	private DiscoverResource()
	{		
	}
    

	//卸载
	public void unload() 
	{
		instance = null;
		SysLogger.info("DiscoverResource.unload()");
	}
	
	public List getNetshieldList() 
	{
		return netshieldList;
	}
	
	public void setNetshieldList(List netshieldList) 
	{
		this.netshieldList = netshieldList;
	}
	
	public List getNetincludeList() 
	{
		return netincludeList;
	}
	
	public void setNetincludeList(List netincludeList) 
	{
		this.netincludeList = netincludeList;
	}
	
	public String getCommunity() 
	{
		return community;
	}
	
	public void setCommunity(String community) 
	{
		this.community = community;
	}
	
	public Map getDeviceType() 
	{
		return deviceType;
	}
	
	public void setDeviceType(Map deviceType) 
	{
		this.deviceType = deviceType;
	}
	
	public Set getCommunitySet() 
	{
		return communitySet;
	}
	
	public void setCommunitySet(Set communitySet) 
	{
		this.communitySet = communitySet;
	}
	
	public int getMaxThreads() 
	{
		return maxThreads;
	}
	
	public void setMaxThreads(int maxThreads) 
	{
		this.maxThreads = maxThreads;
	}
	
	public int getPerThreadIps() 
	{
		return perThreadIps;
	}
	
	public void setPerThreadIps(int perThreadIps) 
	{
		this.perThreadIps = perThreadIps;
	}
	
	public Set getShieldSet() 
	{	
		return shieldSet;
	}
	
	public void setShieldSet(Set shieldSet) 
	{
		this.shieldSet = shieldSet;
	}
	
	public Map getSpecifiedCommunity() 
	{
		return SpecifiedCommunity;
	}
	
	public void setSpecifiedCommunity(Map SpecifiedCommunity) 
	{
		this.SpecifiedCommunity = SpecifiedCommunity;
	}  
	
	public List getServiceList() 
	{
		return serviceList;
	}
	
	public void setServiceList(List serviceList) 
	{
		this.serviceList = serviceList;
	}

	public Set getFailedList() {
		return failedList;
	}

	public void setFailedList(Set failedList) {
		this.failedList = failedList;
	}	    
}