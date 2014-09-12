/**
 * <p>Description:DiscoverManager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-12
 */

package com.afunms.topology.manage;

import com.afunms.common.base.*;
import com.afunms.topology.model.*;
import com.afunms.topology.dao.*;
import com.afunms.topology.util.*;
import com.afunms.common.util.SysUtil;

public class CustomXmlManager extends BaseManager implements ManagerInterface
{
	private String delete()
	{	   
		CustomXmlDao dao = new CustomXmlDao();	   
	    String id = getParaValue("radio");
	   
	    String xmlName = dao.delete(id); 
	    XmlOperator xmlOpr = new XmlOperator();
	    xmlOpr.setFile(xmlName);
	    xmlOpr.deleteXml();
	    return "/customxml.do?action=list";
	}

	private synchronized String add()
	{
		CustomXml vo = new CustomXml();
		String xmlName = "custom" + SysUtil.getCurrentLongTime() + ".jsp";
	    vo.setXmlName(xmlName);
	    vo.setViewName(getParaValue("view_name"));
	     
	    XmlOperator xmlOpr = new XmlOperator();
	    xmlOpr.setFile(vo.getXmlName());
	    xmlOpr.init4createXml();
	    xmlOpr.createXml();
	     
	    DaoInterface dao = new CustomXmlDao();    	   
	   	setTarget("/customxml.do?action=list");
	    return save(dao,vo);
	}

	private String update()
	{
		CustomXml vo = new CustomXml();
	    vo.setId(getParaIntValue("id"));
	    vo.setViewName(getParaValue("view_name"));
	    DaoInterface dao = new CustomXmlDao();    	   
	   	setTarget("/customxml.do?action=list");
	    return update(dao,vo);
	}
		
	public String execute(String action) 
	{	
//		System.out.println("com.afunms.topolopy.manager-----CustomXmlManager.java--60------->"+action);
        if (action.equals("list"))
        {
    	    DaoInterface dao = new CustomXmlDao();
   	        setTarget("/topology/view/xml/list.jsp");
//   	        System.out.println("com.afunms.topolopy.manager-----CustomXmlManager.java--65------->"+getTarget()+"=======");
            return list(dao);
        }    
        if (action.equals("relationList"))
        {
    	    DaoInterface dao = new CustomXmlDao();
    	    String nodeId = getParaValue("nodeId");
    	    request.setAttribute("nodeId", nodeId);
   	        setTarget("/topology/view/relation.jsp");
            return list(dao);
        }
		if(action.equals("ready_add"))
			return "/topology/view/xml/add.jsp";
        if (action.equals("add"))       
            return add();          
  	    if (action.equals("delete"))
            return delete();       
        if (action.equals("update"))
            return update();    
        if(action.equals("ready_edit"))
        {	  
 		    DaoInterface dao = new CustomXmlDao();
    	    setTarget("/topology/view/xml/edit.jsp");
            return readyEdit(dao);
        } 
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}
}
