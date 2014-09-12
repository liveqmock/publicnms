/**
 * <p>Description:AccreditManager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-08
 */

package com.afunms.system.manage;

import java.util.ArrayList;
import java.util.List;

import com.afunms.common.base.*;
import com.afunms.common.util.SysLogger;
import com.afunms.system.dao.FunctionDao;
import com.afunms.system.dao.RoleFunctionDao;
import com.afunms.system.dao.AccreditDao;

import com.afunms.system.dao.RoleDao;

import com.afunms.system.model.Function;
import com.afunms.system.model.Role;
import com.afunms.system.model.RoleFunction;
import com.afunms.system.util.CreateRoleTable;
import com.dhcc.util.NewOrgNode;

public class AccreditManager extends BaseManager implements ManagerInterface
{
	public String execute(String action)
	{
	   if(action.equals("list")){
		   return roleList();
	   }
	      
	   if (action.equals("admin_update")){
		   return adminUpdate();
	   }
	      
	   if (action.equals("admin_set")){
		   return update();
	   }
	      
	   setErrorCode(ErrorMessage.ACTION_NO_FOUND);
	   return null;
	}
	/**
	 * 显示出所有角色列表
	 * @return
	 */
	private String roleList()
	{
		RoleDao rd = null;
		try{
			rd = new RoleDao();
			List<Role> Rolelist = (List<Role>)rd.loadAll(); 
			request.setAttribute("Rolelist", Rolelist);
		}catch(Exception e){
			SysLogger.error("Error in AccreditManager.roleList()",e);
			e.printStackTrace();
	    }finally{
	    	rd.close();
	    }
	    return "/system/admin/list.jsp";
   }
   /**
    * 	显示角色的权限
    */
   private String adminUpdate()
   {
	   String RoleId = getParaValue("RoleId");
	   RoleDao rd = null ;
	   RoleFunctionDao roleFunctionDao = null;
	   FunctionDao functionDao = null;	 
	   List<RoleFunction> roleFunction = null ;
	   Role role = null;
	   List<Function> allFunction = null;
	   try{
		   rd = new RoleDao();
		   roleFunctionDao = new RoleFunctionDao();
		   role = (Role)rd.findByID(RoleId);
		   roleFunction = roleFunctionDao.findByRoleId(RoleId); 
		   functionDao = new FunctionDao();
		   allFunction = functionDao.loadAll(); 
	   }catch(Exception e){
		   SysLogger.error("Error in AccreditManager.adminUpdate()",e);
	       e.printStackTrace();
	   }finally{
		   rd.close();
		   roleFunctionDao.close();
		   functionDao.close();
	   }
	   try{
		   functionDao = new FunctionDao();
		   if(allFunction!=null){
			   request.setAttribute("allFunction", allFunction);
		   }
		   List<Function> role_Function_list = new ArrayList<Function>();
		   if(roleFunction!=null){
			   for(int i = 0 ; i < roleFunction.size(); i++){
				   Function function = (Function)functionDao.findByID(roleFunction.get(i).getFuncid());
				   if(function !=null){
					   role_Function_list.add(function);
				   } 
			   }   
		   }
		   request.setAttribute("roleFunction", role_Function_list);
		   request.setAttribute("role", role);
	   }catch(Exception e){
		   SysLogger.error("Error in AccreditManager.adminUpdate()",e);
	       e.printStackTrace();
	   }finally{
		   functionDao.close();
	   }
	   
	   return "/system/admin/list2.jsp";
   }
   /**
    * * 对角色权限进行修改
    */
   private String update() {
	   String roleId = getParaValue("RoleId");
	   String[] oprvalue = getParaArrayValue("checkbox");
	   List<RoleFunction> roleFunctionlist = null;
	   RoleFunctionDao roleFunctionDao =null;
	   try{
		   boolean temp = false;
		   if(oprvalue!=null){
			   temp =true;
			   roleFunctionlist = new ArrayList<RoleFunction>();
			   for(int i = 0;i<oprvalue.length;i++){
				   RoleFunction roleFunction = new RoleFunction();
				   roleFunction.setId(i);
				   roleFunction.setRoleid(roleId);
				   roleFunction.setFuncid(oprvalue[i]);
				   roleFunctionlist.add(roleFunction);
			   }
		   }
		   roleFunctionDao = new RoleFunctionDao();
		   boolean flag = roleFunctionDao.deleteByRoleId(roleId);
		   if(flag&&temp){	  
			   roleFunctionDao.roleFunctionUpadte(roleFunctionlist);
		   }
	   }catch(Exception e){
		   SysLogger.error("Error in AccreditManager.update()",e);
	       e.printStackTrace();
	   }finally{
		   roleFunctionDao.close();
	   }
	   return "/system/admin/saveok.jsp";
	}
	
}
