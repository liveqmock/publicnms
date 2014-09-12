/**
 * <p>Description:mapping table NMS_SUBNET</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-09-20
 */

package com.afunms.topology.model;

import com.afunms.common.base.BaseVo;

public class Subnet extends BaseVo
{
	private int id; 
	private String netAddress;
	private String netMask;  
	private long netLong;	
	private boolean managed;
	
	public int getId() 
	{
		return id;
	}
	
	public void setId(int id) 
	{
		this.id = id;
	}
	
	public String getNetAddress() 
	{
		return netAddress;
	}
	
	public void setNetAddress(String netAddress) 
	{
		this.netAddress = netAddress;
	}

	public String getNetMask() 
	{
		return netMask;
	}
	
	public void setNetMask(String netMask) 
	{
		this.netMask = netMask;
	}
	
	public boolean isManaged() 
	{
		return managed;
	}
	
	public void setManaged(boolean managed) 
	{
		this.managed = managed;
	}

	/**
	 * @return the netLong
	 */
	public long getNetLong() {
		return netLong;
	}

	/**
	 * @param netLong the netLong to set
	 */
	public void setNetLong(long netLong) {
		this.netLong = netLong;
	}	
}