package com.afunms.sysset.manage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.SysLogger;
import com.afunms.sysset.dao.DeviceTypeDao;
import com.afunms.sysset.dao.MiddlewareDao;
import com.afunms.sysset.dao.ProducerDao;
import com.afunms.sysset.model.DeviceType;
import com.afunms.sysset.model.Middleware;

public class MiddlewareManager extends BaseManager implements ManagerInterface 
{
	/**
	 * 分页显示记录子节点
	 */
	@SuppressWarnings("unchecked")
	protected String list() 
	{
		MiddlewareDao dao1 = new MiddlewareDao();		
		//获得所有的父节点
		String sql = "select * from afunms.nms_manage_nodetype where father_id=0";
		List<Middleware> listFather = dao1.findByCriteria(sql);		
		Map<Integer,String> father = new HashMap<Integer,String>();
		for( int i=0;i<listFather.size();i++ )
		{
			Middleware mw = (Middleware)listFather.get(i);
			father.put(mw.getId(), mw.getText());
		}	
		dao1.close();
		
		//孩子结点，分页显示
		MiddlewareDao dao = new MiddlewareDao();
		String where = "where father_id!=0 order by father_id";
		int perpage = getPerPagenum();
		List<Middleware> listSub = dao.listByPage(getCurrentPage(), where, perpage); 		
		if (listSub == null)
		{
			return null;
		}
		request.setAttribute("page", dao.getPage());
		request.setAttribute("listSub", listSub);
		request.setAttribute("father", father);
		request.setAttribute("show", "显示所有父类型");
		dao.close();
		return "/sysset/middleware/list.jsp";
	}
	
	/**
	 * 分页显示记录父节点
	 */
	protected String listFather() 
	{
		MiddlewareDao dao = new MiddlewareDao();		
		//父亲结点，分页显示
		String where = "where father_id=0";
		int perpage = getPerPagenum();
		List<Middleware> listSub = dao.listByPage(getCurrentPage(), where, perpage);		
		if (listSub == null)
		{
			return null;
		}		
		Map<Integer,String> father = new HashMap<Integer,String>();
		father.put(0, "");		
		request.setAttribute("page", dao.getPage());
		request.setAttribute("listSub", listSub);
		request.setAttribute("father", father);
		request.setAttribute("show", "显示所有子类型");
		return "/sysset/middleware/list.jsp";
	}
	
	/**
	 * 向数据库中添加数据
	 */
	private String add()
	{
		String name = getParaValue("name").trim();        	
        MiddlewareDao dao = new MiddlewareDao();
	    if(dao.isNameExist(name))
	    {
	    	setErrorCode(-1);
	    	request.setAttribute(SessionConstant.ERROR_INFO, "该名称的前3个字母已经存在！");
	    	dao.close();
	    	return null;    	    	
	    }	
	    
    	Middleware vo = new Middleware();
    	vo.setName(name);
    	vo.setText(getParaValue("text"));
    	int father_id = getParaIntValue("fatherORchild")==1 ? 0 : getParaIntValue("father_id");
    	vo.setFather_id(father_id);       	
    	vo.setCategory(getParaValue("category"));
    	vo.setTable_name(getParaValue("table_name"));
    	vo.setNode_tag(name.substring(0,3));	        
    	if( "1".equals(getParaValue("faOrCh")) )
	    {
	    	setTarget("/middleware.do?action=listFather");
	    }
	    else
	    {
	    	setTarget("/middleware.do?action=list");
	    }
        return save(dao,vo);
	}
	
	/**
	 * 更新数据
	 */
	private String update()
	{
		String name = getParaValue("name"); 
    	int id = getParaIntValue("id");
    	MiddlewareDao dao = new MiddlewareDao();
    	if(dao.isNameExist(name,id))
	    {
    		setErrorCode(-1);
	    	request.setAttribute(SessionConstant.ERROR_INFO, "该名称前3个字符已经存在！");
	    	dao.close();
	    	return null;    	    	
	    }        	
    	Middleware vo = new Middleware();
    	vo.setId(id);
    	vo.setName(name);
    	vo.setText(getParaValue("text"));
    	int father_id = getParaIntValue("father_id"); 
    	if( -1==father_id )
    	{
    		vo.setFather_id(0);
    	}
    	else
    	{
    		vo.setFather_id(father_id);
    	}        	
    	
    	vo.setCategory(getParaValue("category"));
    	vo.setTable_name(getParaValue("table_name"));
    	vo.setNode_tag(name.substring(0,3));	        
    	if( "1".equals(getParaValue("faOrCh")) )
	    {
	    	setTarget("/middleware.do?action=listFather");
	    }
	    else
	    {
	    	setTarget("/middleware.do?action=list");
	    }
        return update(dao,vo);
	}

	public String execute(String action) {
		if (action.equals("list")) 
		{
			return list();
		}
		if (action.equals("listFather"))
		{
			return listFather();
		}
		if(action.equals("ready_add"))
		{
			request.setAttribute("faOrCh", getParaValue("faOrCh"));
			return "/sysset/middleware/add.jsp";
		}
		if (action.equals("add"))
        {  
			return add();
        }
		if (action.equals("delete"))
        {	  
		    DaoInterface dao = new MiddlewareDao();
		    if( "1".equals(getParaValue("faOrCh")) )
		    {
		    	setTarget("/middleware.do?action=listFather");
		    }
		    else
		    {
		    	setTarget("/middleware.do?action=list");
		    }
            return delete(dao);
        }  
		if(action.equals("ready_edit"))
        {	
 		    DaoInterface dao = new MiddlewareDao();
    	    setTarget("/sysset/middleware/edit.jsp");
            return readyEdit(dao);
        }
		if (action.equals("update"))
        { 
        	return update();
        }   
		return null;
	}

}
