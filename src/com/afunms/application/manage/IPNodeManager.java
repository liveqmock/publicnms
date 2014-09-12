/**
 * <p>Description:DiscoverManager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-12
 */

package com.afunms.application.manage;

import java.util.*;

import com.afunms.common.base.*;
import com.afunms.common.util.ShareData;
import com.afunms.application.model.IPNode;
import com.afunms.application.dao.IPNodeDao;
import com.afunms.topology.util.*;
import com.afunms.polling.loader.IISLoader;
import com.afunms.polling.loader.IPNodeLoader;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;

public class IPNodeManager extends BaseManager implements ManagerInterface
{
	private String list()
	{
		IPNodeDao dao = new IPNodeDao();
		List list = null;
		try{
			list = dao.loadAll();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			dao.close();
		}
		for(int i=0;i<list.size();i++)
		{
			IPNode vo = (IPNode)list.get(i);
			Node ipNode = PollingEngine.getInstance().getNodeByID(vo.getId());
			if(ipNode==null)
			   vo.setStatus(0);
			else
			   vo.setStatus(ipNode.getStatus());	
		}
		request.setAttribute("list",list);				
		return "/application/ip_node/list.jsp";
	}
	
	private String update()
	{
		IPNode vo = new IPNode();
		vo.setIpAddress(getParaValue("ip"));
        vo.setId(getParaIntValue("id"));        
        vo.setAlias(getParaValue("alias"));

        Node node = PollingEngine.getInstance().getNodeByID(vo.getId());
        node.setIpAddress(vo.getIpAddress());
        node.setAlias(vo.getAlias());
        
 	    DaoInterface dao = new IPNodeDao();    	   
	    setTarget("/ipnode.do?action=list");
        return update(dao,vo);
	}
	
	private String delete()
	{		    
		String id = getParaValue("radio"); 
		IPNodeDao dao = new IPNodeDao();
		try{
			dao.delete(id);		
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			dao.close();
		}
	      try{
	    	  dao = new IPNodeDao();
	        	 List _list = dao.loadAll();
	     		IPNodeLoader ipnodeloader = new IPNodeLoader();
	     		ipnodeloader.clearRubbish(_list);
			}catch(Exception e){
					
			}finally{
				dao.close();
			}
        PollingEngine.getInstance().deleteNodeByID(Integer.parseInt(id));        
		return "/ipnode.do?action=list";
	}
	
	private String add()
    {    	   
    	IPNode vo = new IPNode();
    	vo.setId(KeyGenerator.getInstance().getNextKey());
        vo.setIpAddress(getParaValue("ip"));
        vo.setAlias(getParaValue("alias"));
        
        IPNodeLoader loader = new IPNodeLoader();
        loader.loadOne(vo);
	    DaoInterface dao = new IPNodeDao();    	   
	    setTarget("/ipnode.do?action=list");
        return save(dao,vo);

    }    
	
	public String execute(String action) 
	{	
        if (action.equals("list"))
            return list();    
		if(action.equals("ready_add"))
			return "/application/ip_node/add.jsp";
        if (action.equals("add"))
        	return add();
  	    if (action.equals("delete"))
            return delete();       
        if (action.equals("update"))
        	return update();  
        if(action.equals("ready_edit"))
        {	  
 		    DaoInterface dao = new IPNodeDao();
    	    setTarget("/application/ip_node/edit.jsp");
            return readyEdit(dao);
        }    
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}
}
