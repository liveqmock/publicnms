/**
 * <p>Description:SysLogManager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-06
 */

package com.afunms.system.manage;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.system.dao.SysLogDao;
import com.afunms.common.base.DaoInterface;

public class SysLogManager extends BaseManager implements ManagerInterface
{
   public String execute(String action)
   {
       if (action.equals("list"))
       {
    	   DaoInterface dao = new SysLogDao();
    	   setTarget("/system/syslog/list.jsp");
           return list(dao);
       }    
	   if (action.equals("delete"))
       {	  
		   DaoInterface dao = new SysLogDao();
    	   setTarget("/syslog.do?action=list");
           return delete(dao);
       }    
       setErrorCode(ErrorMessage.ACTION_NO_FOUND);
       return null;
   }
}
