package com.afunms.system.manage;

import java.util.ArrayList;
import java.util.List;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.system.dao.PortTypeDao;
import com.afunms.system.vo.PortTypeVo;


public class PortTypeManager extends BaseManager implements ManagerInterface {
	private String list()
	{
		PortTypeDao fdao = new PortTypeDao();
		List list = new ArrayList();
		try{
			list = fdao.loadAll();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			fdao.close();
		}
		request.setAttribute("list",list);
		return "/system/porttype/list.jsp";
	}
	
	private String add()
    {    	   
		PortTypeVo vo = new PortTypeVo();
		vo.setTypeid(getParaIntValue("typeid"));
		vo.setChname(getParaValue("chname"));
		vo.setBak(getParaValue("bak"));
		
		PortTypeDao fdao = new PortTypeDao();
		try{
			fdao.save(vo);	
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			fdao.close();
		}
		return "/porttype.do?action=list";
    }  
	
	public String delete()
	{
		String[] ids = getParaArrayValue("checkbox");
		
    	if(ids != null && ids.length > 0){	
    		PortTypeDao fdao = new PortTypeDao();
    		try{
    			fdao.delete(ids);
    		}catch(Exception e){
    			e.printStackTrace();
    		}finally{
    			fdao.close();
    		}
    	}
		
		return list();
	}
	
	private String update()
    {    	   
		PortTypeVo vo = new PortTypeVo();
		
		
    	vo.setId(getParaIntValue("id"));
		vo.setTypeid(getParaIntValue("typeid"));
		vo.setChname(getParaValue("chname"));
		vo.setBak(getParaValue("bak"));
		
		PortTypeDao fdao = new PortTypeDao();
		try{
			fdao.update(vo);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			fdao.close();
		}
             
		return "/porttype.do?action=list";
    }
	
	public String execute(String action) {
	 	if(action.equals("list"))
            return list(); 
	 	if(action.equals("ready_add"))
	 		return "/system/porttype/add.jsp";
	 	if(action.equals("add"))
        	return add();
	 	if(action.equals("delete"))
            return delete();
	 	if(action.equals("ready_edit"))
	 	 {	  
 		    DaoInterface dao = new PortTypeDao();
    	    setTarget("/system/porttype/edit.jsp");
            return readyEdit(dao);
        }
	 	if(action.equals("update"))
            return update();
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		
	return null;
}
}
