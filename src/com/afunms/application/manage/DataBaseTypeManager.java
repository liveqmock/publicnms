/**
 * <p>Description:Tomcat Manager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-12-07
 */

package com.afunms.application.manage;

import java.util.List;

import com.afunms.common.base.*;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.DBNode;
import com.afunms.polling.loader.*;
import com.afunms.application.model.DBTypeVo;
import com.afunms.topology.util.*;
import com.afunms.application.dao.DBTypeDao;
import com.afunms.application.util.DBPool;
import com.afunms.topology.dao.DiscoverCompleteDao;

public class DataBaseTypeManager extends BaseManager implements ManagerInterface
{
	private String list()
	{
		DBTypeDao dao = new DBTypeDao();
		List list = null;
		try{
			list = dao.loadAll();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			dao.close();
		}
		request.setAttribute("list",list);				
		return "/application/dbtype/list.jsp";
	}

	private String add()
    {    	   
		DBTypeVo vo = new DBTypeVo();
    	//vo.setId(KeyGenerator.getInstance().getNextKey());
    	vo.setDbtype(getParaValue("dbtype"));
    	vo.setDbdesc(getParaValue("dbdesc"));        
        
        DBTypeDao dao = new DBTypeDao();
        try{
        	dao.save(vo);
        }catch(Exception e){
        	e.printStackTrace();
        }finally{
        	dao.close();
        }
        return "/dbtype.do?action=list";
    }    
	
	public String delete()
	{
		String id = getParaValue("radio"); 
		DBTypeDao dao = new DBTypeDao();
		try{
			dao.delete(id);	
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			dao.close();
		}
        
        return "/dbtype.do?action=list";
	}
	
	private String update()
    {    	   
		DBTypeVo vo = new DBTypeVo();
    	vo.setId(getParaIntValue("id"));
    	vo.setDbtype(getParaValue("dbtype"));
    	vo.setDbdesc(getParaValue("dbdesc"));        
    	DBTypeDao dao = new DBTypeDao();
    	try{
    		dao.update(vo);	  
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		dao.close();
    	}
        return "/dbtype.do?action=list";
    }    
	
	public String execute(String action) 
	{	
        if(action.equals("list"))
            return list();    
        if(action.equals("ready_add"))
        	return "/application/dbtype/add.jsp";
        if(action.equals("add"))
        	return add();
        if(action.equals("delete"))
            return delete();
        if(action.equals("ready_edit"))
        {	  
 		    DaoInterface dao = new DBTypeDao();
    	    setTarget("/application/dbtype/edit.jsp");
            return readyEdit(dao);
        }
        if(action.equals("update"))
            return update();
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}	
}