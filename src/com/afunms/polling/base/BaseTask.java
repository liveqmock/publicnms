/**
 * <p>Description:base task</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-09-24
 */

package com.afunms.polling.base;

import com.afunms.common.util.SysLogger;

public abstract class BaseTask implements Runnable
{	
	protected String descr;	
	public BaseTask()
	{		
	}	

	public void setDescr(String descr)
	{
	    this.descr = descr;	
	}

	public void run()
	{
		if(timeRestricted())
		{
		    SysLogger.info("[" + descr + "]任务开始");
		    executeTask();
		    SysLogger.info("[" + descr + "]任务结束");
		}
	}
		
	/**
	 * 时间限制,子类可覆盖这个方法
	 */
	public boolean timeRestricted()
	{
		return true;
	}
	
	public abstract void executeTask();
}