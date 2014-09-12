package com.afunms.bpm.manage;

/**
 * HXL
 */
import java.util.List;

import com.afunms.common.base.*;
import com.afunms.common.util.SessionConstant;
import com.afunms.system.dao.DepartmentDao;
import com.afunms.system.dao.FunctionDao;
import com.afunms.system.model.Department;
import com.afunms.system.model.Function;
import com.afunms.system.model.User;
import com.afunms.system.util.CreateRoleFunctionTable;

public class BpmManager extends BaseManager implements ManagerInterface
{
	public String execute(String action) 
	{	
        if (action.equals("list"))
        {
        	User user = (User)session.getAttribute(SessionConstant.CURRENT_USER);
        	Function root = null;
        	FunctionDao functionDao = null;
        	try{
        		functionDao = new FunctionDao();
        		root = (Function)functionDao.findByID("295");
        	}catch(Exception e){
        		
        	}finally{
        		functionDao.close();
        	}
        	
        	CreateRoleFunctionTable crft = new CreateRoleFunctionTable();
        	List<Function> functionRoleList = crft.getRoleFunctionListByRoleId(String.valueOf(user.getRole()));
        	List<Function> functionList = crft.getAllFuctionChildByRoot(root, functionRoleList);
        	request.setAttribute("root", root);
        	request.setAttribute("functionList", functionList);
        	return "/bpm/list.jsp";
            //return list(dao);
        }    
		
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}
}

