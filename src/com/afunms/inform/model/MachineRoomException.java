/**
 * <p>Description: 机房异常记录，包括ups和空调</p>
 * <p>Company:dhcc.com</p>
 * @author afunms
 * @project 齐鲁石化
 * @date 2007-1-24
 */

package com.afunms.inform.model;

import com.afunms.common.base.BaseVo;

public class MachineRoomException extends BaseVo
{
	private String id;
	private String ipAddress;
	private String message;
	private int level;
	private int category;
	private String logTime;
	
	public String getMessage() 
	{
		return message;
	}

	public void setMessage(String message) 
	{
		this.message = message;
	}

	public int getCategory() 
	{
		return category;
	}
	
	public void setCategory(int category) 
	{
		this.category = category;
	}

	public String getId() 
	{
		return id;
	}

	public void setId(String id) 
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

	public int getLevel() 
	{
		return level;
	}

	public void setLevel(int level) 
	{
		this.level = level;
	}

	public String getLogTime() 
	{
		return logTime;
	}

	public void setLogTime(String logTime) 
	{
		this.logTime = logTime;
	}
}
