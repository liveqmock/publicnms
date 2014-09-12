/**
 * <p>Description:DiscoverManager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-12
 */

package com.afunms.topology.manage;

import com.afunms.common.base.*;
import com.afunms.common.util.CreateTableManager;
import com.afunms.polling.*;
import com.afunms.polling.node.*;
import com.afunms.polling.om.IpMac;
import com.afunms.polling.om.IpMacBase;
import com.afunms.system.dao.DepartmentDao;
import com.afunms.topology.dao.IpMacChangeDao;
import com.afunms.topology.dao.IpMacDao;
import com.afunms.topology.dao.IpMacBaseDao;
import com.afunms.topology.util.*;
import com.afunms.common.util.DBManager;
import com.afunms.config.model.*;
import com.afunms.config.dao.*;
import com.afunms.common.util.*;
import java.util.*;

/**
 * 增加一个节点，要在server.jsp增加一个节点;
 * 删除或更新一个节点信息，在这则不直接操作server.jsp,而是让任务updateXml来更新xml.
 */
public class IpMacBaseManager extends BaseManager implements ManagerInterface
{
	private String list()
	{
		IpMacBaseDao dao = new IpMacBaseDao();
		setTarget("/config/ipmacbase/list.jsp");
        return list(dao);
	}
	
	private String monitornodelist()
	{
		IpMacDao dao = new IpMacDao();	 
		setTarget("/config/ipmacbase/ipmaclist.jsp");
        return list(dao," where managed=1");
	}

    
	private String readyEdit()
	{
		String key = getParaValue("key");
		   String value = getParaValue("value");	 
		   request.setAttribute("key", key);
		   request.setAttribute("value", value);
	    DaoInterface dao = new IpMacBaseDao();
	    setTarget("/config/ipmacbase/edit.jsp");
        return readyEdit(dao);
	}
	
	private String update()
	{  
		IpMacBase vo = new IpMacBase();
		int id = getParaIntValue("id");
		IpMacBaseDao dao = new IpMacBaseDao(); 	
		vo = (IpMacBase)dao.findByID(id+"");
		String ifemail = getParaValue("ifemail");
		String ifsms = getParaValue("ifsms");
		String iftel = getParaValue("iftel");
		int employee_id = getParaIntValue("employee_id");
		int flag = 0;
		if(ifemail != null && ifemail.trim().length()>0){
			vo.setIfemail(ifemail);
			flag = 1;
		}
		if(ifsms != null && ifsms.trim().length()>0){
			vo.setIfsms(ifsms);
			flag = 1;
		}
		if(iftel != null && iftel.trim().length()>0){
			vo.setIftel(iftel);
			flag = 1;
		}
		if(flag == 1){
			dao = new IpMacBaseDao(); 
			dao.update(vo);
			dao.close();
		}
		dao.close();
		return "/ipmacbase.do?action=list&jp=1";
	    //setTarget("/action=list");
        //return update(dao,vo);
    }
	
	private String updateemployee()
	{  
		IpMacBase vo = new IpMacBase();
		int id = getParaIntValue("id");
		IpMacBaseDao dao = new IpMacBaseDao(); 	
		vo = (IpMacBase)dao.findByID(id+"");
		int employee_id = getParaIntValue("employee_id");
		vo.setEmployee_id(employee_id);
			dao = new IpMacBaseDao(); 
			dao.update(vo);
			dao.close();
		return "/ipmacbase.do?action=list&jp=1";
    }
	
	private String selupdateemployee()
	{  
		String key = getParaValue("key");
	  	String value = getParaValue("value");	 
	  	request.setAttribute("key", key);
	  	request.setAttribute("value", value);
		IpMacBase vo = new IpMacBase();
		int id = getParaIntValue("id");
		IpMacBaseDao dao = new IpMacBaseDao(); 	
		vo = (IpMacBase)dao.findByID(id+"");
		int employee_id = getParaIntValue("employee_id");
		vo.setEmployee_id(employee_id);
		dao = new IpMacBaseDao(); 
		dao.update(vo);
		dao.close();
		dao = new IpMacBaseDao();
		setTarget("/config/ipmacbase/findlist.jsp");
		return list(dao," where "+key+" = '"+value+"'");
		//return "/ipmacbase.do?action=list&jp=1";
    }
  

    private String updateselect()
    {
	   String key = getParaValue("key");
	   String value = getParaValue("value");	 
	   IpMacBaseDao dao = new IpMacBaseDao();
	   request.setAttribute("key", key);
	   request.setAttribute("value", value);
	   int id = getParaIntValue("id"); 
	   
	   IpMacBase vo = (IpMacBase)dao.findByID(id+"");
		String ifemail = getParaValue("ifemail");
		String ifsms = getParaValue("ifsms");
		String iftel = getParaValue("iftel");
		int flag = 0;
		if(ifemail != null && ifemail.trim().length()>0){
			vo.setIfemail(ifemail);
			flag = 1;
		}
		if(ifsms != null && ifsms.trim().length()>0){
			vo.setIfsms(ifsms);
			flag = 1;
		}
		if(iftel != null && iftel.trim().length()>0){
			vo.setIftel(iftel);
			flag = 1;
		}
		if(flag == 1){
			dao = new IpMacBaseDao(); 
			dao.update(vo);
			dao.close();
		}
		
		
		dao = new IpMacBaseDao();
	   setTarget("/config/ipmacbase/findlist.jsp");
       return list(dao," where "+key+" = '"+value+"'");
   }
    
    private String setipmacbase()
    {	 
	   IpMacBaseDao dao = new IpMacBaseDao();
	   
	   IpMacBase vo = new IpMacBase();
	   int id = getParaIntValue("id"); 
	   int flag = getParaIntValue("flag");
	   //Host host = (Host)PollingEngine.getInstance().getNodeByID(id);
	   	vo = (IpMacBase)dao.findByID(id+"");
	    String mac = getParaValue("mac");
		String ifindex = getParaValue("ifindex");
		String macip = getParaValue("macip");
		
		dao = new IpMacBaseDao();
		vo.setIfband(flag);//0:设为基准表 1:IP-MAC 2;端口-MAC 3:IP-端口-MAC
		try{
			if(flag == -1){
				String[] ids = new String[1];
				ids[0] = vo.getId()+"";
				dao.delete(ids);
			}else
				dao.update(vo);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			dao.close();
			flag = 0;
		}
		return "/ipmacbase.do?action=list&jp=1&flag=0";
   }
    private String selsetipmacbase()
    {	 
    	String key = getParaValue("key");
 	   String value = getParaValue("value");	 
 	   IpMacBaseDao dao = new IpMacBaseDao();
 	   request.setAttribute("key", key);
 	   request.setAttribute("value", value);
 	   
	   //IpMacBaseDao dao = new IpMacBaseDao();
	   
	   IpMacBase vo = new IpMacBase();
	   int id = getParaIntValue("id"); 
	   int flag = getParaIntValue("flag");
	   //Host host = (Host)PollingEngine.getInstance().getNodeByID(id);
	   	vo = (IpMacBase)dao.findByID(id+"");
		
		dao = new IpMacBaseDao();
		vo.setIfband(flag);//0:设为基准表 1:IP-MAC 2;端口-MAC 3:IP-端口-MAC
		//SysLogger.info(flag+"============");
		try{
			if(flag == -1){
				String[] ids = new String[1];
				ids[0] = vo.getId()+"";
				dao.delete(ids);
			}else
				dao.update(vo);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			dao.close();
		}
		flag = 2;
		dao = new IpMacBaseDao();
		setTarget("/config/ipmacbase/findlist.jsp");
	       return list(dao," where "+key+" = '"+value+"'");
		//return "/ipmacbase.do?action=list&jp=1";
   }
    
    private String cancelipmacbase()
    {	 
	   IpMacBaseDao dao = new IpMacBaseDao();
	   
	   IpMacBase vo = new IpMacBase();
	   int id = getParaIntValue("id"); 
	   int flag = getParaIntValue("flag");
	   vo = (IpMacBase)dao.findByID(id+"");
	   //Host host = (Host)PollingEngine.getInstance().getNodeByID(id);
	    String mac = getParaValue("mac");
		String ifindex = getParaValue("ifindex");
		String macip = getParaValue("macip");
			
		dao = new IpMacBaseDao();
		vo.setIfband(flag);//0:设为基准表 1:IP-MAC 2;端口-MAC 3:IP-端口-MAC
		try{
			if(flag == -1){
				String[] ids = new String[1];
				ids[0] = vo.getId()+"";
				dao.delete(ids);
			}else
				dao.update(vo);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			dao.close();
		}
		return "/ipmacbase.do?action=list&jp=1";
   }
    
    private String selcancelipmacbase()
    {	 
    	String key = getParaValue("key");
  	   String value = getParaValue("value");	 
  	   IpMacBaseDao dao = new IpMacBaseDao();
  	   request.setAttribute("key", key);
  	   request.setAttribute("value", value);
	   
	   IpMacBase vo = new IpMacBase();
	   int id = getParaIntValue("id"); 
	   int flag = getParaIntValue("flag");
	   vo = (IpMacBase)dao.findByID(id+"");
	   //Host host = (Host)PollingEngine.getInstance().getNodeByID(id);
	    String mac = getParaValue("mac");
		String ifindex = getParaValue("ifindex");
		String macip = getParaValue("macip");
			
		dao = new IpMacBaseDao();
		vo.setIfband(flag);//0:设为基准表 1:IP-MAC 2;端口-MAC 3:IP-端口-MAC
		try{
			if(flag == -1){
				String[] ids = new String[1];
				ids[0] = vo.getId()+"";
				dao.delete(ids);
			}else
				dao.update(vo);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			dao.close();
		}
		dao = new IpMacBaseDao();
		setTarget("/config/ipmacbase/findlist.jsp");
	    return list(dao," where "+key+" = '"+value+"'");
   }

    private String find()
    {
	   String key = getParaValue("key");
	   String value = getParaValue("value");	 
	   IpMacBaseDao dao = new IpMacBaseDao();
	   request.setAttribute("key", key);
	   request.setAttribute("value", value);
	   setTarget("/config/ipmacbase/findlist.jsp");
       return list(dao," where "+key+" like '%"+value+"%'");
   }
	private String deleteall()
	{
		IpMacBaseDao dao = new IpMacBaseDao();
		dao.deleteall();
		dao.close();
		dao = new IpMacBaseDao();
		setTarget("/config/ipmacbase/list.jsp");
        return list(dao);
	}
    
   public String execute(String action)
   {
	  if(action.equals("list"))
	     return list(); 
	  if(action.equals("monitornodelist"))
		 return monitornodelist();      
	  if(action.equals("ready_edit"))
         return readyEdit();
      if(action.equals("update"))
         return update();
      if(action.equals("deleteall"))
          return deleteall();
      if(action.equals("updateemployee"))
          return updateemployee();
      if(action.equals("selupdateemployee"))
          return selupdateemployee();
      if(action.equals("find"))
         return find();  
      if(action.equals("updateselect"))
          return updateselect();
      if(action.equals("setipmacbase"))
          return setipmacbase();
      if(action.equals("selsetipmacbase"))
          return selsetipmacbase();
      if(action.equals("cancelipmacbase"))
          return cancelipmacbase();
      if(action.equals("selcancelipmacbase"))
          return selcancelipmacbase();
	  if(action.equals("ready_add"))
	     return "/topology/network/add.jsp";
	  if (action.equals("delete"))
      {	  
		    DaoInterface dao = new IpMacBaseDao();
    	    setTarget("/ipmacbase.do?action=list");
            return delete(dao);
        }
      setErrorCode(ErrorMessage.ACTION_NO_FOUND);
      return null;
   }
}
