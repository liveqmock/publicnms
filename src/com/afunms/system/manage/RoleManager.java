/**
 * <p>Description:RoleManager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.system.manage;

import com.afunms.common.base.*;
import com.afunms.system.dao.RoleDao;
import com.afunms.common.base.ErrorMessage;
import com.afunms.system.model.Role;


public class RoleManager extends BaseManager implements ManagerInterface
{
   /**
    * 覆盖父类同名方法    
    */	
   private String delete()
   {	   
	   RoleDao dao = new RoleDao();
       if(dao.delete(getParaValue("radio")))
          return "/role.do?action=list";
       else
    	  return null;
   }
	 
   public String execute(String action)
   {
       if(action.equals("ready_add"))
           return "/system/role/add.jsp";
       if (action.equals("add"))
       {    	   
    	   Role vo = new Role();
    	   vo.setRole(getParaValue("role"));
    	   DaoInterface dao = new RoleDao();    	   
   	       setTarget("/role.do?action=list");
           return save(dao,vo);
       }    
       if (action.equals("delete"))
           return delete();
       if (action.equals("update"))
       {    	   
    	   Role vo = new Role();
    	   vo.setId(getParaIntValue("id"));
    	   vo.setRole(getParaValue("role"));
    	   DaoInterface dao = new RoleDao();    	   
   	       setTarget("/role.do?action=list");
           return update(dao,vo);
       }    
       if (action.equals("list"))
       {
    	   DaoInterface dao = new RoleDao();
   	       setTarget("/system/role/list.jsp");
           return list(dao);
       }    
       if(action.equals("ready_edit"))
       {	  
		   DaoInterface dao = new RoleDao();
   	       setTarget("/system/role/edit.jsp");
           return readyEdit(dao);
       }   
       if(action.equals("ready_auth"))
           return "/system/role/auth.jsp";       
       setErrorCode(ErrorMessage.ACTION_NO_FOUND);
       return null;
   }
}
