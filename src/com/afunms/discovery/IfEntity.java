/**
 * <p>Description:interface entity</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-10
 */

package com.afunms.discovery;

import java.util.List;
import java.util.ArrayList;

import com.afunms.common.util.NetworkUtil;

public class IfEntity  
{
	private String index;	
	private String descr;  
	private String ipAddress;  
	private String physAddress; 
	private String port;
	private String speed;
	private int type;	
	private int chassis;
	private int slot;
	private int uport;
	private String ipList;	
	private int operStatus;
	private List subNetList;
	
	public IfEntity()
	{
	   index = null;
	   descr = null;
	   ipAddress = null;
	   ipList = null;
	   physAddress = null;
	   port = null;
	   subNetList = null;
	   chassis = -1;
	   slot=-1;
	   uport = -1;
	}
		
	public String getPhysAddress() {
		return physAddress;
	}

	public void setPhysAddress(String physAddress) {
		this.physAddress = physAddress;
	}

	public IfEntity(String index)
	{
	   	this.index = index;
	}
	
	public String getSpeed() 
	{
		return speed;
	}
	
	public void setSpeed(String speed) 
	{
		this.speed = speed;
	}
	
	public String getDescr() 
	{
		return descr;
	}

	public void setDescr(String descr) 
	{
		this.descr = descr;
	}

	public String getIndex() 
	{
		return index;
	}

	public void setIndex(String index) 
	{
		this.index = index;
	}

	public String getIpAddress() 
	{
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) 
	{
		this.ipAddress = ipAddress;
	}
	
	public int getOperStatus() 
	{
		return operStatus;
	}

	public void setOperStatus(int operStatus) 
	{
		this.operStatus = operStatus;
	}

	public List getSubNetList() 
	{
		if(subNetList==null)
			subNetList = new ArrayList(3);	
		return subNetList;
	}

	public void setSubNetList(List subNetList) 
	{
		this.subNetList = subNetList;
	}   	

	/**
	 * 确定一个IP地址是否要该接口所相连子网内
	 * 如果在,则返回它所在子网
	 */
	public SubNet isValidIP(String ipAddress)
	{		
		if(subNetList==null||subNetList.size()==0)
    		return null;
	
		SubNet result = null;
		for(int i=0;i<subNetList.size();i++)
		{	
			SubNet subnet = (SubNet)subNetList.get(i);
		    if(NetworkUtil.isValidIP(subnet.getNetAddress(),subnet.getNetMask(),ipAddress))
		    {
		    	result = subnet;
		    	break;
		    }	
		}		
		return result;
	}
	
    public boolean equals(Object obj)
    {
       if (obj == null)
          return false;
       if (!(obj instanceof IfEntity))
          return false;
       
       IfEntity that = (IfEntity) obj;
       if (this.index.equals(that.index)&&this.descr.equals(that.descr)
    	   &&this.ipAddress.equals(that.ipAddress)&&this.physAddress.equals(that.physAddress))                
          return true;
       else 
          return false;
    }

    public int hashCode()
    {
   	   int result = 1;   	   
   	   result = result * 31 + this.index.hashCode();
   	   result = result * 31 + this.descr.hashCode();
   	   result = result * 31 + this.ipAddress.hashCode();
   	   result = result * 31 + this.physAddress.hashCode();
   	   return result;
    }

	public String getPort() 
	{
		return port;
	}

	public void setPort(String port) 
	{
		this.port = port;
	}

	public String getIpList() 
	{
		return ipList;
	}

	public void setIpList(String ipList) 
	{
		this.ipList = ipList;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public int getChassis() {
		return chassis;
	}

	public void setChassis(int chassis) {
		this.chassis = chassis;
	}
	public int getSlot() {
		return slot;
	}

	public void setSlot(int slot) {
		this.slot = slot;
	}
	public int getUport() {
		return uport;
	}

	public void setUport(int uport) {
		this.uport = uport;
	}
}