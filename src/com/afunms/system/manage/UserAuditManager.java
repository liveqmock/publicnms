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
import com.afunms.system.model.User;
import com.afunms.system.model.SysLog;
import com.afunms.system.model.UserAudit;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.SysUtil;
import com.afunms.event.dao.*;
import com.afunms.event.model.*;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.dao.HostNodeDao;
import wfm.encode.MD5;

import com.afunms.config.model.Business;
import com.afunms.config.dao.BusinessDao;

import java.text.SimpleDateFormat;
import java.util.*;

public class UserAuditManager extends BaseManager implements ManagerInterface
{

	public String execute(String action) {
		// TODO Auto-generated method stub
		if("list".equals(action)){
			return list();
		}else if("delete".equals(action)){
			return delete();
		}else if("ready_add".equals(action)){
			return ready_add();
		}else if("add".equals(action)){
			return add();
		}
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
	    return null;
	}
	
	private String list(){
		UserAuditDao userAuditDao = null ;
		String jsp = null;
		try{
			userAuditDao = new UserAuditDao();
			setTarget("/system/useraudit/list.jsp");
			jsp = list(userAuditDao,"order by time desc");
		}catch(Exception e){
			
		}finally{
			userAuditDao.close();
		}
		List userList = null;
		UserDao userDao = new UserDao();
		try {
			userList = userDao.loadAll();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			userDao.close();
		}
		request.setAttribute("userList", userList);
		return jsp;
	}
	
	private String delete(){
		UserAuditDao userAuditDao = null ;
		boolean result = false;
		try{
			userAuditDao = new UserAuditDao();
			String[] ids = request.getParameterValues("checkbox");
			if(ids!=null&&ids.length>0){	
				result = userAuditDao.delete(ids);
			}
		}catch(Exception e){
			result = false;
		}finally{
			userAuditDao.close();
		}
		if(result){
			return list();
		}else{
			return "/system/useraudit/fail.jsp";
		}
		
	}
	
	private String ready_add(){
		return "/system/useraudit/add.jsp";
	}
	
	private String add(){
		UserAuditDao userAuditDao = null ;
		boolean result = false;
		try{
			userAuditDao = new UserAuditDao();
			UserAudit userAudit = createUserAudit();
			result = userAuditDao.save(userAudit);	
		}catch(Exception e){
			e.printStackTrace();
			result = false;
		}finally{
			userAuditDao.close();
		}
		if(result){
			return list();
		}else{
			return "/system/useraudit/fail.jsp";
		}
		
	}
	
	private UserAudit createUserAudit(){
		int userId = getParaIntValue("userid");
		String action = getParaValue("operation");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		Date date = new Date();
		UserAudit userAudit = new UserAudit();
		userAudit.setUserId(userId);
		userAudit.setAction(action);
		userAudit.setTime(simpleDateFormat.format(date));
		return userAudit;
	}
	
	
	
}
