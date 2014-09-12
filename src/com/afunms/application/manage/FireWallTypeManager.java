package com.afunms.application.manage;

import java.util.List;

import com.afunms.application.dao.FWTypeDao;
import com.afunms.application.model.FWTypeVo;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;

public class FireWallTypeManager  extends BaseManager implements ManagerInterface
{
	private String list()
	{
		FWTypeDao dao = new FWTypeDao();
		List list = null;
		try{
			list = dao.loadAll();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			dao.close();
		}
		
		request.setAttribute("list",list);				
		return "/application/fwtype/list.jsp";
	}

	private String add()
    {    	   
		FWTypeVo vo = new FWTypeVo();
    	//vo.setId(KeyGenerator.getInstance().getNextKey());
    	vo.setFirewalltype(getParaValue("firewalltype"));
    	vo.setFirewalldesc(getParaValue("firewalldesc"));        
        
    	FWTypeDao dao = new FWTypeDao();
        try{
        	dao.save(vo);	    
        }catch(Exception e){
			e.printStackTrace();
		}finally{
			dao.close();
		}
        return "/fwtype.do?action=list";
    }    
	
	public String delete()
	{
		String id = getParaValue("radio"); 
		FWTypeDao dao = new FWTypeDao();
		try{
			dao.delete(id);		
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			dao.close();
		}
        
        return "/fwtype.do?action=list";
	}
	
	private String update()
    {    	   
		FWTypeVo vo = new FWTypeVo();
    	vo.setId(getParaIntValue("id"));
    	vo.setFirewalltype(getParaValue("firewalltype"));
    	vo.setFirewalldesc(getParaValue("firewalldesc"));        
    	FWTypeDao dao = new FWTypeDao();
        try{
        	dao.update(vo);	    
        }catch(Exception e){
			e.printStackTrace();
		}finally{
			dao.close();
		}
        return "/fwtype.do?action=list";
    }    
	
	public String execute(String action) 
	{	
        if(action.equals("list"))
            return list();    
        if(action.equals("ready_add"))
        	return "/application/fwtype/add.jsp";
        if(action.equals("add"))
        	return add();
        if(action.equals("delete"))
            return delete();
        if(action.equals("ready_edit"))
        {	  
 		    DaoInterface dao = new FWTypeDao();
    	    setTarget("/application/fwtype/edit.jsp");
            return readyEdit(dao);
        }
        if(action.equals("update"))
            return update();
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}
}
