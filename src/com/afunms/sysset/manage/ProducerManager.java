/**
 * <p>Description:ProducerManager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-16
 */

package com.afunms.sysset.manage;

import com.afunms.common.base.*;
import com.afunms.sysset.dao.ProducerDao;
import com.afunms.sysset.model.Producer;

public class ProducerManager extends BaseManager implements ManagerInterface
{
	public String execute(String action) 
	{	
        if (action.equals("list"))
        {
    	    DaoInterface dao = new ProducerDao();
   	        setTarget("/sysset/producer/list.jsp");
            return list(dao);
        }    
		if(action.equals("ready_add"))
			return "/sysset/producer/add.jsp";
        if (action.equals("add"))
        {    	   
        	Producer vo = new Producer();
	        vo.setProducer(getParaValue("producer"));
	        vo.setEnterpriseOid(getParaValue("oid"));
	        vo.setWebsite(getParaValue("website"));
    	    DaoInterface dao = new ProducerDao();    	   
   	        setTarget("/producer.do?action=list");
            return save(dao,vo);
        }    
  	    if (action.equals("delete"))
        {	  
		    DaoInterface dao = new ProducerDao();
    	    setTarget("/producer.do?action=list");
            return delete(dao);
        }    
        if (action.equals("update"))
        {    	   
        	Producer vo = new Producer();
	        vo.setId(getParaIntValue("id"));
	        vo.setProducer(getParaValue("producer"));
	        vo.setEnterpriseOid(getParaValue("oid"));
	        vo.setWebsite(getParaValue("website"));
     	    DaoInterface dao = new ProducerDao();    	   
    	    setTarget("/producer.do?action=list");
            return update(dao,vo);
        }    
        if(action.equals("ready_edit"))
        {	  
 		    DaoInterface dao = new ProducerDao();
    	    setTarget("/sysset/producer/edit.jsp");
            return readyEdit(dao);
        }    
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}
}