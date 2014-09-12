/**
 * <p>Description:PositionManager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.system.manage;

import com.afunms.common.base.*;
import com.afunms.system.dao.PositionDao;
import com.afunms.system.model.Position;

public class PositionManager extends BaseManager implements ManagerInterface
{
	public String execute(String action) 
	{	
        if (action.equals("list"))
        {
    	    DaoInterface dao = new PositionDao();
   	        setTarget("/system/position/list.jsp");
            return list(dao);
        }    
		if(action.equals("ready_add"))
			return "/system/position/add.jsp";
        if (action.equals("add"))
        {    	   
        	Position vo = new Position();
    	    vo.setName(getParaValue("name"));

    	    DaoInterface dao = new PositionDao();    	   
   	        setTarget("/position.do?action=list");
            return save(dao,vo);
        }    
  	    if (action.equals("delete"))
        {	  
		    DaoInterface dao = new PositionDao();
    	    setTarget("/position.do?action=list");
            return delete(dao);
        }    
        if (action.equals("update"))
        {    	   
        	Position vo = new Position();
      	    vo.setId(getParaIntValue("id"));
   	        vo.setName(getParaValue("name"));

     	    DaoInterface dao = new PositionDao();    	   
    	    setTarget("/position.do?action=list");
            return update(dao,vo);
        }    
        if(action.equals("ready_edit"))
        {	  
 		    DaoInterface dao = new PositionDao();
    	    setTarget("/system/position/edit.jsp");
            return readyEdit(dao);
        }    
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}
}