/**
 * <p>Description:UserManager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2011-02-25
 */

package com.afunms.system.manage;

import java.util.ArrayList;
import java.util.List;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ManagerInterface;
import com.afunms.config.dao.KnowledgeDAO;
import com.afunms.config.model.Knowledge;
import com.afunms.system.dao.FtpTransConfigDao;
import com.afunms.system.dao.TFtpServerDao;
import com.afunms.system.model.FtpTransConfig;
import com.afunms.system.model.TFtpServer;

public class FtpTransConfigManager extends BaseManager implements ManagerInterface {
	
	public String list(){
		FtpTransConfigDao configdao = new FtpTransConfigDao();	
		List list = configdao.loadAll();
		request.setAttribute("list", list);
		return "/system/ftptransconfig/list.jsp";
	}
	
	public String add() {

		FtpTransConfig vo = new FtpTransConfig();
		FtpTransConfigDao dao = new FtpTransConfigDao();
		
		vo.setIp(getParaValue("ip"));
		vo.setUsername(getParaValue("username"));
		vo.setPassword(getParaValue("password"));
		vo.setFlag(getParaIntValue("flag"));
		
		try {
			dao.save(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		return "/ftptrans.do?action=list";
	}
	
	public String update(){
		FtpTransConfig vo=new FtpTransConfig();
		vo.setId(getParaIntValue("id"));
		vo.setIp(getParaValue("ip"));
		vo.setUsername(getParaValue("username"));
		vo.setPassword(getParaValue("password"));
		vo.setFlag(getParaIntValue("_flag"));
		
		FtpTransConfigDao dao=new FtpTransConfigDao();
		String target = null;
		if (dao.update(vo))
			target = "/ftptrans.do?action=list";
		return target;
	}
	
	public String cancelalert(){
		FtpTransConfig vo = new FtpTransConfig();
		FtpTransConfigDao configdao = new FtpTransConfigDao();
		List list = new ArrayList();
		List conflist = new ArrayList();
		List ips = new ArrayList();
		String ipaddress ="";
		try{
			vo = (FtpTransConfig)configdao.findByID(getParaValue("id"));
			vo.setFlag(0);
			configdao = new FtpTransConfigDao();
			configdao.update(vo);	
		}catch(Exception e){
			e.printStackTrace();
		}
		return "/ftptrans.do?action=list";
	}
	
	private String addalert()
    {    
		FtpTransConfig vo = new FtpTransConfig();
		FtpTransConfigDao configdao = new FtpTransConfigDao();
		List list = new ArrayList();
		List conflist = new ArrayList();
		List ips = new ArrayList();
		String ipaddress ="";
		try{
			vo = (FtpTransConfig)configdao.findByID(getParaValue("id"));
			vo.setFlag(1);
			configdao = new FtpTransConfigDao();
			configdao.update(vo);	
		}catch(Exception e){
			e.printStackTrace();
		}
		return "/ftptrans.do?action=list";
    }
	
	public String execute(String action) {
		  if (action.equals("list"))
	       {
			  return list();
	       } 
		  if(action.equals("ready_add"))
		  {
			  return "/system/ftptransconfig/add.jsp";
		  }
		  if(action.equals("add"))
		  {
			  return add();
		  }
		  if(action.equals("delete"))
		  {
			  DaoInterface dao=new FtpTransConfigDao();
			  setTarget("/ftptrans.do?action=list");
	           return delete(dao);
		  }
		  if(action.equals("ready_edit"))
		   {	   
			   DaoInterface dao = new FtpTransConfigDao();
		       setTarget("/system/ftptransconfig/edit.jsp");
	           return readyEdit(dao);
		   }
		  if(action.equals("update"))
	           return update();
		  if(action.equals("addalert"))
	           return addalert();
	       if(action.equals("cancelalert"))
	           return cancelalert();
		return null;
	}

}
