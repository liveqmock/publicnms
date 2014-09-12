/**
 * <p>Description:DeviceTypeManager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-09
 */

package com.afunms.sysset.manage;

import com.afunms.common.base.*;
import com.afunms.sysset.dao.DeviceTypeDao;
import com.afunms.sysset.model.DeviceType;

public class DeviceTypeManager extends BaseManager implements ManagerInterface
{
	private String find()
	{
    	DeviceTypeDao dao = new DeviceTypeDao();
    	
	    DeviceType vo = dao.findBySysOid(getParaValue("sys_oid"));
        request.setAttribute("vo",vo);
        return "/sysset/device_type/find.jsp";		
	}
	
	public String execute(String action) 
	{	
        if (action.equals("list"))
        {
    	    DaoInterface dao = new DeviceTypeDao();
   	        setTarget("/sysset/device_type/list.jsp");
            return list(dao);
        }    
		if (action.equals("ready_add"))
			return "/sysset/device_type/add.jsp";
        if (action.equals("add"))
        {    	   
        	String sysOid = getParaValue("sys_oid");        	
	        DeviceTypeDao dao = new DeviceTypeDao();
    	    if(dao.isSysOidExist(sysOid,0))
    	    {
    	    	setErrorCode(ErrorMessage.SYS_OID_EXIST);
    	    	dao.close();
    	    	return null;    	    	
    	    }	
        	DeviceType vo = new DeviceType();
	        vo.setProducer(getParaIntValue("producer"));
	        vo.setSysOid(sysOid);
	        vo.setDescr(getParaValue("descr"));
	        vo.setImage(getParaValue("image"));
	        vo.setCategory(getParaIntValue("category"));
	        vo.setLocate(getParaValue("locate"));
   	        setTarget("/devicetype.do?action=list");
            return save(dao,vo);
        }    
  	    if (action.equals("delete"))
        {	  
		    DaoInterface dao = new DeviceTypeDao();
    	    setTarget("/devicetype.do?action=list");
            return delete(dao);
        }    
        if (action.equals("update"))
        {    	   
        	String sysOid = getParaValue("sys_oid");
        	int id = getParaIntValue("id");
	        DeviceTypeDao dao = new DeviceTypeDao();
    	    if(dao.isSysOidExist(sysOid,id))
    	    {
    	    	setErrorCode(ErrorMessage.SYS_OID_EXIST);
    	    	dao.close();
    	    	return null;    	    	
    	    }	        	
        	DeviceType vo = new DeviceType();
	        vo.setId(id);
	        vo.setProducer(getParaIntValue("producer"));
	        vo.setSysOid(sysOid);
	        vo.setDescr(getParaValue("descr"));
	        vo.setImage(getParaValue("image"));
	        vo.setCategory(getParaIntValue("category"));
	        vo.setLocate(getParaValue("locate"));
    	    setTarget("/devicetype.do?action=list");
            return update(dao,vo);
        }    
        if(action.equals("ready_edit"))
        {	  
 		    DaoInterface dao = new DeviceTypeDao();
    	    setTarget("/sysset/device_type/edit.jsp");
            return readyEdit(dao);
        }
        if(action.equals("find"))
            return find();
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}
}
