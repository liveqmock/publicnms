/**
 * <p>Description:拓扑图所有节点的父类</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-11-25
 */

package com.afunms.polling.base;

import java.util.*;

import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysUtil;
import com.afunms.monitor.item.base.*; 
import com.afunms.topology.util.NodeHelper;
import com.afunms.monitor.executor.base.MonitorFactory;
import com.afunms.polling.node.Host;
import com.afunms.common.util.*;
import com.afunms.topology.model.*;
import com.afunms.polling.om.*;

public class Node
{
	protected int id;           //id
    protected int category;     //类别
    protected String type;      //类型    
    protected String ipAddress; //IP地址  
    protected String adminIp; 	//管理IP地址
    protected String lastAlarm; //主机最后报警信息    
    protected String lastTime;  //上次轮询时间
    protected String nextTime;  //下次轮询时间    
    protected String sysDescr;  //系统描述         
    protected String alias;     //系统别名  
    protected boolean managed;  //是否被管理    
    protected boolean alarm;    //是否报警    
    protected int normalTimes;
    protected int alarmlevel;
    protected int failTimes;    
    protected int status; //当前状态0=不被管理,1=正常,2=设备忙,3=关机    
    protected List moidList;     //所有被监视对象       
    protected List alarmMessage; //报警信息
    protected int discoverstatus; //多次发现的状态
    protected String sysLocation;  //系统位置
    protected String sysContact;  //系统联系人
    protected Hashtable alarmHash;//存放先前告警的数据
    protected Hashtable alarmPksHash;//存放先前告警的数据
    protected int transfer;//传输方向（0：无，1：南向）
    protected String assetid;//设备编号
    protected String location;//机房位置
    private int collecttype;//数据采集方式  1:snmp 2:shell
	int ostype;//操作系统类型  1:snmp 2:shell
	private String sendmobiles;
	private String sendemail;
	private String sendphone;
	
	private String bid;
	private int endpoint;//末端设备
	private int supperid;//供应商id snow add at 2010-5-18
	 
	 
	public int getSupperid() {
		return supperid;
	}
	public void setSupperid(int supperid) {
		this.supperid = supperid;
	}
	
	public int getCollecttype() {
		return this.collecttype;
	}
	public void setCollecttype(int collecttype) {
		this.collecttype = collecttype;
	}
	
	public int getOstype() {
		return this.ostype;
	}
	public void setOstype(int ostype) {
		this.ostype = ostype;
	}
    
	public Node()
    {
		alarmMessage = new ArrayList();
		moidList = new ArrayList();
		lastAlarm = "";
		status = 1; //初始化时，状态都是正常的
		lastTime = SysUtil.getCurrentTime();
		alarmlevel = 0;
    }
    	
    public boolean equals(Object obj)
    {
       if (obj == null)
          return false;
       if (!(obj instanceof Node))
          return false;
       
       Node that = (Node) obj;
       if (this.id==that.id)    	                  
          return true;
       else 
          return false;
    }

    public int hashCode()
    {   	      	   
   	   int result = 31 + this.id;
   	   return result;
    }
	    
	public String toString()
	{
		StringBuffer info = new StringBuffer(100);
		info.append(id);
		info.append(".");
		info.append(NodeHelper.getNodeCategory(category));
		info.append("ip=");
		info.append(ipAddress);
		info.append(",alias=");
		info.append(alias);
		info.append(",sysDescr=");
		info.append(sysDescr);	
		info.append(",sysContact=");
		info.append(sysContact);
		info.append(",sysLocation=");
		info.append(sysLocation);
		return info.toString();		
	}

	/**
	 * 今天的可用率
	 */	
    public int getAvailability() 
    {
    	if(failTimes + normalTimes==0)
    	   return 0;
    	else
		   return (int)(normalTimes * 100/( failTimes + normalTimes));
	}
	
    /**
     * 按moid找到一个被监视对象
     */
    public MonitoredItem getItemByMoid(String moid)
    {
    	MonitoredItem result = null;
        for(int i=0;i<moidList.size();i++)
        {
        	MonitoredItem tmp = (MonitoredItem)moidList.get(i); 
            if(tmp.getMoid().equals(moid))
            {
            	result = tmp;
            	break;
            }	
        }
        return result;
    }
      
	/**
	 * 为拓扑图显示提供信息
	 */
	public String getShowMessage()
	{
		//Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(ipAddress);
		//if(ipAllData == null)ipAllData = new Hashtable();
		//Vector ipPingData = (Vector)ShareData.getPingdata().get(ipAddress);
		
		StringBuffer msg = new StringBuffer(200);
		msg.append("<font color='green'>类型:");		
		msg.append(NodeHelper.getNodeCategory(category));
		msg.append("</font><br>");
		msg.append("设备标签:");
		msg.append(alias);
		msg.append("<br>");
		String ipaddress[] = ipAddress.split(":");
		msg.append("IP地址:");
		//System.out.println("ipAddress==============="+ipAddress);
		//System.out.println("ipaddress.length==============="+ipaddress.length);
		if(ipaddress.length==1){
			msg.append(ipaddress[0]);
			msg.append("<br>");
		} else {
			msg.append("<br>");
			for(int i=0;i<ipaddress.length;i++){
				msg.append(ipaddress[i]);
				msg.append("<br>");
			}
		}
		/*
		if(status==0||status>1)
		{			
			msg.append("<font color='red'>");
			if(status==0)
			   msg.append("不被管理</font>");
			else if(status==2)
			   msg.append("设备忙</font>");
			else
			   msg.append("Ping不通</font>");	
			return msg.toString();
		}	
		
		alarm = false;
		StringBuffer alarmMsg = new StringBuffer(100);
		//SysLogger.info("############################################");
        for(int i=0;i<moidList.size();i++)
        {    
        	
        	NodeMonitor nm = (NodeMonitor)moidList.get(i);
        	if(nm.getNodetype().equalsIgnoreCase("net")){
        		//网络设备
        		if(nm.getCategory().equals("cpu")){
        			//CPU利用率
        			SysLogger.info("limenvalue0:"+nm.getLimenvalue0()+"   cpuvalue:"+cpuvalue);
        			if(nm.getLimenvalue0()<cpuvalue){
        				//产生告警
        				alarmMsg.append(nm.getAlarmInfo());
        				alarm = true;
        			}
        			msg.append(nm.getDescr() + ":" + cpuvalue + nm.getUnit() + "<br>");
        		}else if(nm.getCategory().equals("ping")){
        			//网络连通性
        			msg.append(nm.getDescr() + ":" + pingvalue + nm.getUnit() + "<br>");
        		}else if(nm.getCategory().equals("interface")){
        			//接口信息
        			if(nm.getSubentity().equals("AllInBandwidthUtilHdx")){
        				//入口流速
        				if(inhdx != null )msg.append(nm.getDescr() + ":" + inhdx + nm.getUnit() + "<br>");
        			}else if(nm.getSubentity().equals("AllOutBandwidthUtilHdx")){
        				//出口流速
        				if(outhdx != null )msg.append(nm.getDescr() + ":" + outhdx + nm.getUnit() + "<br>");
        			}
        			
        			//msg.append(nm.getDescr() + ":" + pingvalue + nm.getUnit() + "<br>");	
        		}
        	}
        	
        	
        }
	*/
		if(alarm)
		{	
		    msg.append("<font color='red'>--报警信息:--</font><br>");
		    //msg.append(alarmMsg.toString());
		    //SysLogger.info(alarmMsg.toString());
		}   
		msg.append("更新时间:" + lastTime);	
		//SysLogger.info(ipAddress+"-----------"+lastTime);
        return msg.toString();		
	}
	
	public int getFailTimes() 
	{
		return failTimes;
	}

	public void setFailTimes(int failTimes) 
	{
		this.failTimes = failTimes;
	}

	public int getNormalTimes() 
	{
		return normalTimes;
	}

	public void setNormalTimes(int normalTimes) 
	{
		this.normalTimes = normalTimes;
	}
            	
    public boolean isAlarm() 
    {
		return alarm;
	}

	public void setAlarm(boolean alarm) 
	{
		this.alarm = alarm;
	}

	public String getAlias() 
	{
		return alias;
	}

	public void setAlias(String alias) 
	{
		this.alias = alias;
	}

	public List getAlarmMessage() 
	{
		return alarmMessage;
	}
	
	public int getCategory() 
	{
		return category;
	}

	public void setCategory(int category) 
	{
		this.category = category;
	}

	public int getId() 
	{
		return id;
	}

	public void setId(int id) 
	{
		this.id = id;
	}

	public String getIpAddress() 
	{
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) 
	{
		this.ipAddress = ipAddress;
	}

	public String getAdminIp() {
		return adminIp;
	}

	public void setAdminIp(String adminIp) {
		this.adminIp = adminIp;
	}
	public String getSysDescr() 
	{
		return sysDescr;
	}

	public void setSysDescr(String sysDescr) 
	{
		this.sysDescr = sysDescr;
	}
	
	public String getSysContact() 
	{
		return sysContact;
	}

	public void setSysContact(String sysContact) 
	{
		this.sysContact = sysContact;
	}
	
	public String getSysLocation() 
	{
		return sysLocation;
	}

	public void setSysLocation(String sysLocation) 
	{
		this.sysLocation = sysLocation;
	}

	public void setMoidList(List list) 
	{
		this.moidList = list;
	}

	public List getMoidList() 
	{
		return moidList;
	}
	
	public String getLastAlarm() 
	{
		return lastAlarm;
	}

	public void setLastAlarm(String lastAlarm) 
	{
		this.lastAlarm = lastAlarm;
	}
	
	public String getType() 
	{
		return type;
	}

	public void setType(String type) 
	{
		this.type = type;
	}

	public boolean isManaged() 
	{
		return managed;
	}

	public void setManaged(boolean managed) 
	{
		this.managed = managed;
	}

	public int getStatus() 
	{
		return status;
	}

	public void setStatus(int status) 
	{
		this.status = status;
	}
	
	public int getDiscoverstatus() 
	{
		return discoverstatus;
	}

	public void setDiscoverstatus(int discoverstatus) 
	{
		this.discoverstatus = discoverstatus;
	}

	public String getLastTime() {
		return lastTime;
	}

	public void setLastTime(String lastTime) {
		this.lastTime = lastTime;
	}

	public String getNextTime() {
		return nextTime;
	}

	public void setNextTime(String nextTime) {
		this.nextTime = nextTime;
	}
	public Hashtable getAlarmHash() {
		return alarmHash;
	}

	public void setAlarmHash(Hashtable alarmHash) {
		this.alarmHash = alarmHash;
	}
	
	public Hashtable getAlarmPksHash() {
		return alarmPksHash;
	}

	public void setAlarmPksHash(Hashtable alarmPksHash) {
		this.alarmPksHash = alarmPksHash;
	}
	
	public int getTransfer() {
		return transfer;
	}
	public void setTransfer(int transfer) {
		this.transfer = transfer;
	}
	
	public String getAssetid() {
		return assetid;
	}
	public void setAssetid(String assetid) {
		this.assetid = assetid;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public int getAlarmlevel() 
	{
		return alarmlevel;
	}

	public void setAlarmlevel(int alarmlevel) 
	{
		this.alarmlevel = alarmlevel;
	}
	public String getSendmobiles() {
		return sendmobiles;
	}	
	public void setSendmobiles(String sendmobiles) {
		this.sendmobiles = sendmobiles;
	}
	public String getSendemail() {
		return sendemail;
	}	
	public void setSendemail(String sendemail) {
		this.sendemail = sendemail;
	}
	public String getSendphone() {
		return sendphone;
	}	
	public void setSendphone(String sendphone) {
		this.sendphone = sendphone;
	}
	public String getBid() {
		return bid;
	}	
	public void setBid(String bid) {
		this.bid = bid;
	}
	public int getEndpoint() {
		return this.endpoint;
	}
	public void setEndpoint(int endpoint) {
		this.endpoint = endpoint;
	}
}

