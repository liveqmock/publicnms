package com.afunms.slaaudit.manage;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.config.dao.HaweitelnetconfDao;
import com.afunms.config.dao.SlaCfgCmdFileDao;
import com.afunms.config.model.Huaweitelnetconf;
import com.afunms.slaaudit.dao.SlaAuditDao;
import com.afunms.slaaudit.model.SlaAudit;
import com.afunms.system.dao.UserDao;
import com.afunms.system.model.User;
import com.afunms.topology.dao.HostNodeDao;

public class SlaAuditManager extends BaseManager
		implements ManagerInterface {
	public String execute(String action) {
		if (action.equals("list")) {
			DaoInterface dao = new SlaAuditDao();
	        UserDao userDao = new UserDao();
	        HaweitelnetconfDao telnetdao = new HaweitelnetconfDao();
	        
	        List listOne= new ArrayList();
	        Hashtable userHash = new Hashtable();
	        Hashtable telnetHash = new Hashtable();
			try{
				listOne = userDao.loadAll();
				if(listOne != null && listOne.size()>0){
					for(int i=0;i<listOne.size();i++){
						User user = (User)listOne.get(i);
						userHash.put(user.getId(), user);
					}
				}
				List telnetlist = telnetdao.loadAll();
				if(telnetlist != null && telnetlist.size()>0){
					for(int i=0;i<telnetlist.size();i++){
						Huaweitelnetconf telnetconfig = (Huaweitelnetconf)telnetlist.get(i);
						telnetHash.put(telnetconfig.getId(), telnetconfig);
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				userDao.close();
			}
			request.setAttribute("listOne", listOne);
			request.setAttribute("userHash", userHash);
			request.setAttribute("telnetHash", telnetHash);
			SlaCfgCmdFileDao slaCfgCmdFileDao = new SlaCfgCmdFileDao();
//	        List listTwo= new ArrayList();
//			try{
//				listTwo = slaCfgCmdFileDao.loadAll();
//			}catch(Exception e){
//				e.printStackTrace();
//			}finally{
//				slaCfgCmdFileDao.close();
//			}
//			request.setAttribute("listTwo", listTwo);
			List alltypeList=new ArrayList<String>();
			try{
				alltypeList = slaCfgCmdFileDao.loadAllType();
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				slaCfgCmdFileDao.close();
			}
			request.setAttribute("alltypeList", alltypeList);
			HostNodeDao hostNodeDao = new HostNodeDao();
	        List listThree= new ArrayList();
			try{
				listOne = hostNodeDao.loadAll();
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				hostNodeDao.close();
			}
			request.setAttribute("listThree", listThree);
			String userid = getParaValue("userid");// 用户类型
			String slatype = getParaValue("slatype");// SLA类型
			String dotime = getParaValue("dotime");// 操作时间
			String startdate = getParaValue("startdate");// 开始日期
			String todate = getParaValue("todate");// 
			//jsp页面修改
			setTarget("/slaaudit/list.jsp");

			StringBuffer where = new StringBuffer();

			where.append(" where 1=1");
			if(userid == null){
				userid = "-1";
			}
			if(!userid.equals("-1")){
				where.append(" and userid='" + userid + "'");
			}
			request.setAttribute("userid ", userid );
			if(slatype == null){
				slatype = "-1";
			}
			if(!slatype.equals("-1")){
				where.append(" and slatype='" +slatype + "'");
			}
			request.setAttribute("slatype", slatype);
			if (startdate != null && todate != null && !"".equals(startdate)
					&& !"".equals(todate) && !"null".equals(startdate)
					&& !"null".equals(todate)) {
				where.append(" and dotime>'" + startdate
						+ " 00:00:00' and dotime<'" + todate + " 23:59:59'");
			} else {
				String currentDateString = "";//初始页面开始日期为空 
				String perWeekDateString = "";//初始页面结束日期为空 
				todate = currentDateString;
				startdate = perWeekDateString;
			}
			request.setAttribute("dotime", dotime);
			request.setAttribute("startdate", startdate);
			request.setAttribute("todate", todate);
			return list(dao, where + " order by dotime desc");
		}
		if (action.equals("delete")) {
			boolean result = false;
			String jsp = "/slaAuditManager.do?action=list";
			String[] id = getParaArrayValue("checkbox");
			SlaAuditDao dao = new SlaAuditDao();
			try {
				result = dao.delete(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			return jsp;
		}
		if (action.equals("read")) {
			return read();
		}
		
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}
	
	// 查看信息
	private String read() {

		// System.out.print("$$$$$$$welcome to here read the website !");
		String targetJsp = "/slaaudit/read.jsp";
		SlaAudit vo = null;
		SlaAuditDao dao = new SlaAuditDao();
		try {
			vo = (SlaAudit)dao.findByID(getParaValue("id"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		User user = null;
    	UserDao userdao = new UserDao();
    	try {
    	    user = (User)userdao.findByID(vo.getUserid()+"");
    	} catch (Exception e) {
    	    e.printStackTrace();
    	} finally {
    		userdao.close();
    	}
    	HaweitelnetconfDao haweitelnetconfDao = new HaweitelnetconfDao();
    	Huaweitelnetconf telnetconfig = null;
    	try{
			telnetconfig = (Huaweitelnetconf)haweitelnetconfDao.findByID(vo.getTelnetconfigid()+"");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			haweitelnetconfDao.close();
		}
    	request.setAttribute("username", user.getName());
		if (vo != null) {
			request.setAttribute("vo", vo);
		}
		if (telnetconfig != null) {
			request.setAttribute("ipaddress", telnetconfig.getIpaddress());
		}
		//request.setAttribute("map", getTranslate());
		return targetJsp;
	}
}