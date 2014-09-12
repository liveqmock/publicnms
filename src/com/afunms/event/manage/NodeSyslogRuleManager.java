/**
 * <p>Description:DiscoverManager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-12
 */

package com.afunms.event.manage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import com.afunms.alarm.model.AlarmWayDetail;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.SessionConstant;
import com.afunms.config.dao.BusinessDao;
import com.afunms.event.dao.NetSyslogDao;
import com.afunms.event.model.NetSyslog;
import com.afunms.polling.impl.IpResourceReport;
import com.afunms.report.abstraction.ExcelReport1;
import com.afunms.report.base.AbstractionReport1;
import com.afunms.system.model.User;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.dao.NetSyslogNodeAlarmKeyDao;
import com.afunms.topology.dao.NetSyslogNodeRuleDao;
import com.afunms.topology.model.NetSyslogNodeAlarmKey;
import com.afunms.topology.model.NetSyslogNodeRule;

/**
 * 增加一个节点，要在server.jsp增加一个节点;
 * 删除或更新一个节点信息，在这则不直接操作server.jsp,而是让任务updateXml来更新xml.
 */
public class NodeSyslogRuleManager extends BaseManager implements ManagerInterface
{
	private String list()
	{
		int status =99;
		String priority = "99";
		int bid = 0;
		String ip = "";
		String b_time ="";
		String t_time = "";
		String content = "";
		String strclass = "-1";
		strclass = getParaValue("strclass");
		request.setAttribute("strclass", strclass);
		NetSyslogDao dao = new NetSyslogDao();
    	status = getParaIntValue("status");
    	priority = getParaValue("priority");
    	ip = getParaValue("ipaddress");
    	if(status == -1)status=99;
    	//if(priority == -1)priority=99;
    	request.setAttribute("status", status);
    	request.setAttribute("priority", priority);
    	
    	bid = getParaIntValue("businessid");
    	request.setAttribute("businessid", bid);
    	BusinessDao bdao = new BusinessDao();
    	List businesslist = bdao.loadAll();
    	request.setAttribute("businesslist", businesslist);
    	content = getParaValue("content");
    	if(content == null)content = "";
    	request.setAttribute("content", content);
    	
    	b_time = getParaValue("startdate");
		t_time = getParaValue("todate");
	
		if (b_time == null){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			b_time = sdf.format(new Date());
		}
		if (t_time == null){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			t_time = sdf.format(new Date());
		}
		String starttime1 = b_time + " 00:00:00";
		String totime1 = t_time + " 23:59:59";
		String sql="";
		try{
			User vo = (User)session.getAttribute(SessionConstant.CURRENT_USER);      //用户姓名
			StringBuffer s = new StringBuffer();
			if(!"-1".equals(strclass) && strclass != null && !"".equals(strclass) && !"null".equals(strclass)){
				if("1".equals(strclass)){
					s.append(" where category = 4 and recordtime>= '"+starttime1+"' " +"and recordtime<='"+totime1+"'");
				}else if("2".equals(strclass)){
					s.append(" where category <> 4 and recordtime>= '"+starttime1+"' " +"and recordtime<='"+totime1+"'");
				}
			}else{
				s.append("where recordtime>= '"+starttime1+"' " +"and recordtime<='"+totime1+"'");
			}
			if(!"-1".equals(ip) && ip != null){
				s.append(" and ipaddress = '"+ip+"'");
			}
			if(priority != null && !"null".equals(priority) && !"".equals(priority) && !"8,1,2,3,4,5,6,7".equals(priority)){
				if(priority.indexOf('8')!=-1){
					priority = priority.replace('8', '0');
				}
				s.append(" and priority in ("+priority+")");
			}
			String businessid = vo.getBusinessids();
			int flag = 0;
			if(bid > -1){
				s.append(" and businessid like '%,"+bid+",%'");
			}
			if(content != null && content.trim().length()>0){
				s.append(" and message like '%"+content+"%'");
			}
			sql = s.toString()+" order by id desc";
		}catch(Exception e){
			e.printStackTrace();
		}
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);
		setTarget("/alarm/syslog/list.jsp");
        return list(dao,sql);
	}
	
	private String toolbarfilter()
	{
		String nodeid = getParaValue("nodeid");
		request.setAttribute("nodeid", nodeid);
		//String id = getParaValue("id");
		NetSyslogNodeRuleDao ruledao = new NetSyslogNodeRuleDao();
		NetSyslogNodeRule logrule = (NetSyslogNodeRule)ruledao.findByID(nodeid);
		if(logrule != null ){
			String facility = logrule.getFacility();
			List flist = new ArrayList();
			if(facility != null && facility.trim().length()>0){
				String[] facilitys = facility.split(",");
				
				if(facilitys != null && facilitys.length>0){
					for(int i = 0;i<facilitys.length;i++){
						flist.add(facilitys[i]);
					}
				}
			}

			request.setAttribute("facilitys", flist);
			
			
//			String priority = logrule.getPriority();
//			String[] prioritys = priority.split(",");
//			List plist = new ArrayList();
//			if(prioritys != null && prioritys.length>0){
//				for(int i = 0;i<prioritys.length;i++){
//					plist.add(prioritys[i]);
//				}
//			}
//			request.setAttribute("prioritys", plist);
		}
		String keywords = null;
//		NetSyslogNodeAlarmKeyDao netSyslogNodeAlarmKeyDao = new NetSyslogNodeAlarmKeyDao();
//		try{
//			NetSyslogNodeAlarmKey netSyslogNodeAlarmKey = (NetSyslogNodeAlarmKey)netSyslogNodeAlarmKeyDao.findByID(nodeid);
//			if(netSyslogNodeAlarmKey != null){
//				keywords = netSyslogNodeAlarmKey.getKeywords();
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//		}finally{
//			netSyslogNodeAlarmKeyDao.close();
//		}
		NetSyslogNodeAlarmKeyDao netSyslogNodeAlarmKeyDao = new NetSyslogNodeAlarmKeyDao();
		List netSyslogNodeAlarmList = null;
		try{
			netSyslogNodeAlarmList = (ArrayList)netSyslogNodeAlarmKeyDao.findByCondition(" where nodeid = '" + nodeid + "'");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			netSyslogNodeAlarmKeyDao.close();
		}
		request.setAttribute("netSyslogNodeAlarmList", netSyslogNodeAlarmList);
        return "/alarm/syslog/toolbarfilterlist.jsp";
	}
	
	private String toolbarsave()
	{
		//String id = getParaValue("id");
		String nodeid = getParaValue("nodeid");
		String keywords = getParaValue("keywords");
		if(keywords != null){
			keywords = keywords.trim();
		}
		NetSyslogNodeRuleDao ruledao = new NetSyslogNodeRuleDao();
		NetSyslogNodeRule logrule = null;;       
	       String[] pt = getParaArrayValue("fcheckbox");
	       String pc_str="";
	       if(pt != null && pt.length>0){
	       		for(int i=0;i<pt.length;i++){
	       		
	       			String p_t = pt[i];
	       			pc_str=pc_str+p_t+",";
	       		}
	       }
	       
	       
	       //String nodeid = getParaValue("nodeid");
	       //ErrptconfigDao errptdao = new ErrptconfigDao();
	       //Errptconfig errptconfig = null;
			try{
				logrule = (NetSyslogNodeRule)ruledao.findByID(nodeid);
				//ruledao = new NetSyslogNodeRuleDao();
				if(logrule == null){
					logrule = new NetSyslogNodeRule();
					logrule.setNodeid(nodeid);
					logrule.setFacility(pc_str);
					try{
						ruledao.save(logrule);
					}catch(Exception e){
						e.printStackTrace();
					}finally{
						ruledao.close();
					}
				}else{
					logrule.setFacility(pc_str);
					try{
						ruledao.update(logrule);
					}catch(Exception e){
						e.printStackTrace();
					}finally{
						ruledao.close();
					}
				}
			}catch(Exception e){
				
			}finally{
				ruledao.close();
			}
		//保存告警关键字
//		NetSyslogNodeAlarmKeyDao netSyslogNodeAlarmKeyDao = new NetSyslogNodeAlarmKeyDao();
//		try{
//			NetSyslogNodeAlarmKey netSyslogNodeAlarmKey = (NetSyslogNodeAlarmKey)netSyslogNodeAlarmKeyDao.findByID(nodeid);
//			if(netSyslogNodeAlarmKey != null){
//				netSyslogNodeAlarmKey.setKeywords(keywords);
//				netSyslogNodeAlarmKey.setNodeid(nodeid);
//				netSyslogNodeAlarmKeyDao.update(netSyslogNodeAlarmKey);
//			}else{
//				netSyslogNodeAlarmKey = new NetSyslogNodeAlarmKey();
//				netSyslogNodeAlarmKey.setKeywords(keywords);
//				netSyslogNodeAlarmKey.setNodeid(nodeid);
//				netSyslogNodeAlarmKeyDao.save(netSyslogNodeAlarmKey);
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//		}finally{
//			netSyslogNodeAlarmKeyDao.close();
//		}	
		List alarmkeyDetailList = createAlarmkeyDetailList();
		//保存告警关键字
		NetSyslogNodeAlarmKeyDao netSyslogNodeAlarmKeyDao = new NetSyslogNodeAlarmKeyDao();
		try{
			netSyslogNodeAlarmKeyDao.delete(nodeid);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(netSyslogNodeAlarmKeyDao != null){
				netSyslogNodeAlarmKeyDao.close();
			}
		}	
		try{
			netSyslogNodeAlarmKeyDao = new NetSyslogNodeAlarmKeyDao();
			netSyslogNodeAlarmKeyDao.save(alarmkeyDetailList);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(netSyslogNodeAlarmKeyDao != null){
				netSyslogNodeAlarmKeyDao.close();
			}
		}	
        return "/alarm/syslog/saveok.jsp";
	}
	
	/**
	 * 得到告警关键字列表
	 * @return
	 */
	public List createAlarmkeyDetailList(){
		String nodeid = getParaValue("nodeid");
		List alarmkeyDetailList = new ArrayList(); 
		int num = 0;
		if(request.getParameter("rowNum")!=null && request.getParameter("rowNum").trim().length() > 0 ){
			num = Integer.parseInt(request.getParameter("rowNum"));
		}
		for (int i = 1; i <= num; i++) {
   			String partName = "";
   			if (i < 10) {
   				partName = "0" + String.valueOf(i);
   			} else {
   				partName = String.valueOf(i);
   			}
			String keywords = request.getParameter("keywords-" + partName);
			String level = request.getParameter("level-" + partName);
			if(keywords == null || level == null){
				continue;
			}
			NetSyslogNodeAlarmKey netSyslogNodeAlarmKey = new NetSyslogNodeAlarmKey();
			netSyslogNodeAlarmKey.setKeywords(keywords);
			netSyslogNodeAlarmKey.setLevel(level);
			netSyslogNodeAlarmKey.setNodeid(nodeid);
			alarmkeyDetailList.add(netSyslogNodeAlarmKey);
   		}
		return alarmkeyDetailList;
	}
	
	private String monitornodelist()
	{
		HostNodeDao dao = new HostNodeDao();	 
		setTarget("/topology/network/monitornodelist.jsp");
        return list(dao," where managed=1");
	}
	
	
    
   

   
   
   private String netsyslogdetail()
   {
		Hashtable imgurlhash=new Hashtable();
		Hashtable hash = new Hashtable();//"Cpu"--current
		Hashtable userhash = new Hashtable();//"user"--current
		Hashtable maxhash = new Hashtable();//"Cpu"--max
		List list = new ArrayList();
		try{
			//String ip=getParaValue("ipaddress");
			int id = getParaIntValue("id");
			NetSyslog syslog = new NetSyslog();
			NetSyslogDao dao = new NetSyslogDao();
			syslog = (NetSyslog)dao.findByID(id+"");							
			request.setAttribute("syslog", syslog);	
		}catch(Exception e){
			e.printStackTrace();
		}
		return "/alarm/syslog/net_syslogdetail.jsp";
	    //return "/detail/host_syslogdetail.jsp";
   }
    
   public String execute(String action)
   {
	  if(action.equals("list"))
		  return list();
	  if(action.equals("toolbarfilter"))
		  return toolbarfilter();
	  if(action.equals("toolbarsave"))
		  return toolbarsave();
	  if(action.equals("netsyslogdetail"))
		 return netsyslogdetail();           
	  if(action.equals("ready_add"))
	     return "/topology/network/add.jsp";
      if(action.equals("downloadsyslogreport"))
          return downloadsyslogreport();
      if(action.equals("downloadsyslogreportall"))
          return downloadsyslogreportall();
      setErrorCode(ErrorMessage.ACTION_NO_FOUND);
      return null;
   }
   
   public String downloadsyslogreport()
	{
		//Hashtable allcpuhash = new Hashtable();
		List list = (List) session.getAttribute("list");
		int startRow = ((Integer)session.getAttribute("startRow")).intValue();
		Hashtable reporthash = new Hashtable();
		if (list!=null) {
			reporthash.put("list", list);
		}
		else {
			list = new ArrayList();
		}
		reporthash.put("startRow", startRow);
		AbstractionReport1 report = new ExcelReport1(new IpResourceReport(),
				reporthash);
		
		report.createReport_syslog("/temp/syslog_report.xls");
		request.setAttribute("filename", report.getFileName());
		return "/alarm/syslog/download.jsp";
	}
   public String downloadsyslogreportall()
	{
		//Hashtable allcpuhash = new Hashtable();
	   NetSyslogDao netSyslogDao = new NetSyslogDao();
		List list = netSyslogDao.loadAll();
		//int startRow = ((Integer)session.getAttribute("startRow")).intValue();
		Hashtable reporthash = new Hashtable();
		if (list!=null) {
			reporthash.put("list", list);
		}
		else {
			list = new ArrayList();
		}
		//reporthash.put("startRow", startRow);
		AbstractionReport1 report = new ExcelReport1(new IpResourceReport(),
				reporthash);
		
		report.createReport_syslogall("/temp/syslogall_report.xls");
		request.setAttribute("filename", report.getFileName());
		return "/alarm/syslog/download.jsp";
	}
   
}
