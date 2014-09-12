/**
 * <p>Description:task of collect symantec logs</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-10-09
 */

package com.afunms.polling.task;

import com.afunms.polling.base.BaseTask;
import com.afunms.security.util.HttpSymantecLog;
import com.afunms.common.util.SysUtil;
public class HttpSymantec extends BaseTask
{		
	public HttpSymantec()
	{		
	}
	
	public void executeTask() 
	{
        HttpSymantecLog symantec = new HttpSymantecLog();
        symantec.init(1);
        symantec.beginTransaction();

        symantec.init(2);
        symantec.beginTransaction();        		    
	}
	
	public boolean timeRestricted()
	{
		int hour = SysUtil.getCurrentHour();
	    //一天最多执行一次
	    if(hour <= 7|| hour >= 17) 
	       return false; 
	    else
	       return true;
	}		
}