/**
 * <p>Description:存放临时xml信息</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-10-21
 */

package com.afunms.topology.util;

public class XmlInfo
{
    private String id; 
    private String info;
    private boolean exist;
    
	public String getId() 
	{
		return id;
	}
	
	public void setId(String id) 
	{
		this.id = id;
	}   
    
	public boolean isExist() 
	{
		return exist;
	}
	
	public void setExist(boolean exist) 
	{
		this.exist = exist;
	}

	public String getInfo() 
	{
		return info;
	}
	
	public void setInfo(String info) 
	{
		this.info = info;
	}	
}
