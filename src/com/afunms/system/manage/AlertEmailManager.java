/**
 * <p>Description:UserManager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.system.manage;

import com.afunms.system.dao.*;
import com.afunms.application.dao.*;
import com.afunms.application.model.*;
import com.afunms.common.base.*;
import com.afunms.common.util.*;
import com.afunms.system.model.AlertEmail;
import com.afunms.system.model.SysLog;
import com.afunms.system.model.User;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.SysUtil;
import com.afunms.event.dao.*;
import com.afunms.event.model.*;
import wfm.encode.MD5;

import com.afunms.config.model.Business;
import com.afunms.config.dao.BusinessDao;

import java.text.SimpleDateFormat;
import java.util.*;

public class AlertEmailManager extends BaseManager implements ManagerInterface
{
	
	private String list()
	{
		AlertEmailDao configdao = new AlertEmailDao();
		List list = configdao.loadAll();
		request.setAttribute("list", list);
		return "/system/alertemail/list.jsp";
	}
	
   private String save()
   {                   
	   AlertEmail vo = new AlertEmail();
       vo.setUsername(getParaValue("username"));
       vo.setPassword(getParaValue("password"));
       vo.setSmtp(getParaValue("smtp"));
       vo.setUsedflag(getParaIntValue("usedflag"));
       vo.setMailAddress(this.getParaValue("email_address"));
       AlertEmailDao dao = new AlertEmailDao();
       int result = dao.save(vo);
       return "/alertemail.do?action=list";
   }

   private String update()
   {
	   AlertEmail vo = new AlertEmail();
	   vo.setId(getParaIntValue("id"));
       vo.setUsername(getParaValue("username"));
       vo.setPassword(getParaValue("password"));
       vo.setSmtp(getParaValue("smtp"));
       vo.setUsedflag(getParaIntValue("usedflag"));
       vo.setMailAddress(getParaValue("email_address"));
       
       AlertEmailDao dao = new AlertEmailDao();
       try{
    	   dao.update(vo);
       }catch(Exception e){
    	   e.printStackTrace();
       }
       return "/alertemail.do?action=list";
   }

   private String readyAdd()
   {       	   
       if((new RoleDao()).loadAll().size()==0)
       {
           setErrorCode(ErrorMessage.NO_ROLE);
           return null;
       }
       if((new PositionDao()).loadAll().size()==0)
       {
           setErrorCode(ErrorMessage.NO_POSITION);
           return null;
       }
       if((new DepartmentDao()).loadAll().size()==0)
       {
           setErrorCode(ErrorMessage.NO_DEPARTMENT);
           return null;
       }
       return  "/system/user/add.jsp";    	      	  
   }
	private String addalert()
    {    
		AlertEmail vo = new AlertEmail();
		AlertEmailDao configdao = new AlertEmailDao();
		List list = new ArrayList();
		List conflist = new ArrayList();
		List ips = new ArrayList();
		String ipaddress ="";
		try{
			vo = (AlertEmail)configdao.findByID(getParaValue("id"));
			vo.setUsedflag(1);
			configdao = new AlertEmailDao();
			configdao.update(vo);	
		}catch(Exception e){
			e.printStackTrace();
		}
		return "/alertemail.do?action=list";
    }
	
	private String cancelalert()
    {    
		AlertEmail vo = new AlertEmail();
		AlertEmailDao configdao = new AlertEmailDao();
		List list = new ArrayList();
		List conflist = new ArrayList();
		List ips = new ArrayList();
		String ipaddress ="";
		try{
			vo = (AlertEmail)configdao.findByID(getParaValue("id"));
			vo.setUsedflag(0);
			configdao = new AlertEmailDao();
			configdao.update(vo);	
		}catch(Exception e){
			e.printStackTrace();
		}
		return "/alertemail.do?action=list";
    }
   public String execute(String action)
   {
       if(action.equals("ready_add"))
    	   return "/system/alertemail/add.jsp";
       if(action.equals("add"))
           return save();
       if(action.equals("update"))
           return update();

       if (action.equals("list"))
       {
   	       return list();
       } 
       if(action.equals("addalert"))
           return addalert();
       if(action.equals("cancelalert"))
           return cancelalert();
	   if (action.equals("delete"))
       {	  
		   DaoInterface dao = new AlertEmailDao();
    	   setTarget("/alertemail.do?action=list");
           return delete(dao);
       }    
	   if(action.equals("ready_edit"))
	   {	   
		   DaoInterface dao = new AlertEmailDao();
	       setTarget("/system/alertemail/edit.jsp");
           return readyEdit(dao);
	   }
	   if(action.equals("read"))
	   {	   
		   DaoInterface dao = new UserDao();
	       setTarget("/system/user/read.jsp");
           return readyEdit(dao);
	   }
       
       setErrorCode(ErrorMessage.ACTION_NO_FOUND);
       return null;
   }
   
   public void createxmlfile(List list){
		try{
		ChartXml chartxml;
		chartxml = new ChartXml("pie");
		chartxml.AddXML(list);
	  }catch(Exception e){
		e.printStackTrace();   	
	  }
	}
   
   public void createLinexmlfile(Hashtable lineHash){
		try{
		ChartXml chartxml;
		chartxml = new ChartXml("line");
		chartxml.AddLineXML(lineHash);
	  }catch(Exception e){
		e.printStackTrace();   	
	  }
	}
}
