/**
 * <p>Description: mapping table nms_alarm_message</p>
 * <p>Company:dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-10-13
 */

package com.afunms.inform.model;

import com.afunms.common.base.BaseVo;

public class Alarm extends BaseVo
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
