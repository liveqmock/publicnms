/**
 * <p>Description:mapping topo_ip_node</p>
 * <p>Company: dhcc.com</p>
 * @author miiwill
 * @project afunms
 * @date 2006-12-17
 */

package com.afunms.application.model;

import com.afunms.common.base.BaseVo;

public class IPNode extends BaseVo
{
	private int id;	        
    private String ipAddress;
    private String alias;
	private int status;
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
    
	public String getAlias() 
	{
		return alias;
	}
 
	public void setAlias(String alias) 
	{
		this.alias = alias;
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
}
