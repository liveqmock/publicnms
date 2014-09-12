/**
 * <p>Description: mapping table nms_ip_mac</p>
 * <p>Company: 北京东华合创数码科技股份有限公司</p>
 * @author 王福民
 * @project 阿福网管
 * @date 2006-10-10
 */

package com.afunms.inform.model;

public class Performance
{
    private String ipAddress;
    private String entity;    
    private String id;
    private double value;
    
	public String getEntity() 
	{
		return entity;
	}

	public void setEntity(String entity) 
	{
		this.entity = entity;
	}

	public String getIpAddress() 
	{
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) 
	{
		this.ipAddress = ipAddress;
	}

	public double getValue() 
	{
		return value;
	}

	public void setValue(double value) 
	{
		this.value = value;
	}
	
	public String getId() 
	{
		return id;
	}
	
	public void setId(String id) 
	{
		this.id = id;
	}       	
}