/**
 * <p>Description:simple factory</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-06
 */

package com.afunms.common.base;

import java.util.Hashtable;

import com.afunms.initialize.ResourceCenter;
import com.afunms.common.util.SysLogger;

public class ManagerFactory
{
   private ManagerFactory()
   {
   }

   public static ManagerInterface getManager(String bean)
   {
   	   Hashtable managerMap = ResourceCenter.getInstance().getManagerMap();
   	   ManagerInterface manager = null;
   	   try
	   {
   		   
   	       manager = (ManagerInterface)managerMap.get(bean);
   	       
	   }
   	   catch(Exception e)
	   {
   	       SysLogger.error("ManageFactory.createBizLogic(),Manager不存在:" + bean);
   	       manager = null;
	   }
       return manager;
   }
   public static AjaxManagerInterface getAjaxManager(String bean)
   {
   	   Hashtable ajaxManagerMap = ResourceCenter.getInstance().getAjaxManagerMap();
   	   AjaxManagerInterface manager = null;
   	   try
	   {
   	       manager = (AjaxManagerInterface)ajaxManagerMap.get(bean);
	   }
   	   catch(Exception e)
	   {
   	       SysLogger.error("ManageFactory.createBizLogic(),Manager不存在:" + bean);
   	       manager = null;
	   }
       return manager;
   }
}
