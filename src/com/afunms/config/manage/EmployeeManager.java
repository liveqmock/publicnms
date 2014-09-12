/**
 * <p>Description:UserManager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.config.manage;

import com.afunms.system.dao.*;
import com.afunms.application.dao.DBDao;
import com.afunms.application.dao.*;
import com.afunms.application.model.DBVo;
import com.afunms.application.model.*;
import com.afunms.common.base.*;
import com.afunms.common.util.*;
import com.afunms.config.model.Employee;
import com.afunms.system.model.SysLog;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.SysUtil;
import com.afunms.event.dao.*;
import com.afunms.event.model.*;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.dao.HostNodeDao;
import wfm.encode.MD5;

import com.afunms.config.model.Business;
import com.afunms.config.dao.BusinessDao;
import com.afunms.config.dao.EmployeeDao;

import java.text.SimpleDateFormat;
import java.util.*;

public class EmployeeManager extends BaseManager implements ManagerInterface
{
   private String save()
   {                   
	   Employee vo = new Employee();
       vo.setName(getParaValue("name"));
       vo.setSex(getParaIntValue("sex"));
       vo.setDept(getParaIntValue("dept"));
       vo.setPosition(getParaIntValue("position"));
       vo.setPhone(getParaValue("phone"));
       vo.setMobile(getParaValue("mobile"));
       vo.setEmail(getParaValue("email"));
       //vo.setBusinessids(getParaValue("businessids"));
       String[] ids = getParaArrayValue("checkbox");
       if(ids != null && ids.length>0){
    	   String ids_str = ",";
    	   if(ids.length==1){
    		   vo.setBusinessids(","+ids[0]+",");
    	   }else{
    		   for(int i=0;i<ids.length;i++){
    			   ids_str= ids_str+ids[i]+",";
    		   }
    		   vo.setBusinessids(ids_str);
    	   }
       }
       EmployeeDao dao = new EmployeeDao();
       int result = dao.save(vo);
       
       String target = null;
       if(result==0)
       {
    	   target = null;
           setErrorCode(ErrorMessage.USER_EXIST);
       }
       else if(result==1)
          target = "/employee.do?action=list";
       else
          target = null;
       return target;
   }

   private String update()
   {
	   Employee vo = new Employee();
       vo.setId(getParaIntValue("id"));
       vo.setName(getParaValue("name"));
       vo.setId(getParaIntValue("id"));
       vo.setSex(getParaIntValue("sex"));
       vo.setDept(getParaIntValue("dept"));
       vo.setPosition(getParaIntValue("position"));
       vo.setPhone(getParaValue("phone"));
       vo.setMobile(getParaValue("mobile"));
       vo.setEmail(getParaValue("email"));     
       EmployeeDao dao = new EmployeeDao();
       String target = null;
       if(dao.update(vo))
    	   target = "/employee.do?action=list";
       return target;
   }

   private String readyAdd()
   {       	   
       return  "/config/employee/add.jsp";    	      	  
   }

   public String execute(String action)
   {
       if(action.equals("ready_add"))
           return readyAdd();
       if(action.equals("add"))
           return save();
       if(action.equals("update"))
           return update();
       if (action.equals("list"))
       {
   	       DaoInterface dao = new EmployeeDao();
  	       setTarget("/config/employee/list.jsp");
           return list(dao);
       }    
	   if (action.equals("delete"))
       {	  
		   DaoInterface dao = new EmployeeDao();
    	   setTarget("/employee.do?action=list");
           return delete(dao);
       }    
	   if(action.equals("ready_edit"))
	   {	   
		   DaoInterface dao = new EmployeeDao();
	       setTarget("/config/employee/edit.jsp");
           return readyEdit(dao);
	   }
	   if(action.equals("read"))
	   {	   
		   DaoInterface dao = new EmployeeDao();
	       setTarget("/config/employee/read.jsp");
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
