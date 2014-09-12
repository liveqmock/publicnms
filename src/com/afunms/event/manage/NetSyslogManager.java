/**
 * <p>Description:DiscoverManager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-12
 */

package com.afunms.event.manage;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.JspPage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SyslogFinals;
import com.afunms.common.util.SystemConstant;
import com.afunms.config.dao.BusinessDao;
import com.afunms.config.dao.NodeToBusinessDao;
import com.afunms.config.model.NodeToBusiness;
import com.afunms.event.dao.NetSyslogDao;
import com.afunms.event.dao.NetSyslogEventDao;
import com.afunms.event.dao.NetSyslogImpEventDao;
import com.afunms.event.dao.NetSyslogRuleDao;
import com.afunms.event.dao.NetSyslogViewerDao;
import com.afunms.event.model.NetSyslog;
import com.afunms.event.model.NetSyslogEvent;
import com.afunms.event.model.NetSyslogImpEvent;
import com.afunms.event.model.NetSyslogRule;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.impl.IpResourceReport;
import com.afunms.polling.loader.HostLoader;
import com.afunms.polling.node.Host;
import com.afunms.report.abstraction.ExcelReport1;
import com.afunms.report.base.AbstractionReport1;
import com.afunms.system.model.User;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.util.TopoHelper;
import com.afunms.topology.util.XmlOperator;
/**
 * 增加一个节点，要在server.jsp增加一个节点;
 * 删除或更新一个节点信息，在这则不直接操作server.jsp,而是让任务updateXml来更新xml.
 */
public class NetSyslogManager extends BaseManager implements ManagerInterface
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
					if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
						s.append(" where category = 4 and recordtime>= '"+starttime1+"' " +"and recordtime<='"+totime1+"'");
					}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
						s.append(" where recordtime>= to_date('" + starttime1
								+ "','YYYY-MM-DD HH24:MI:SS') " + "and recordtime<=to_date('"
								+ totime1 + "','YYYY-MM-DD HH24:MI:SS') ");
					}
					
				}else if("2".equals(strclass)){
					if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
						s.append(" where category <> 4 and recordtime>= '"+starttime1+"' " +"and recordtime<='"+totime1+"'");
					}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
						s.append(" where category <> 4 and recordtime>= to_date('" + starttime1
								+ "','YYYY-MM-DD HH24:MI:SS') " + "and recordtime<=to_date('"
								+ totime1 + "','YYYY-MM-DD HH24:MI:SS')");
					}
					
				}
			}else{
				if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
					s.append("where recordtime>= '"+starttime1+"' " +"and recordtime<='"+totime1+"'");
				}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
					s.append("where recordtime>= to_date('" + starttime1
								+ "','YYYY-MM-DD HH24:MI:SS') " + "and recordtime<=to_date('"
								+ totime1 + "','YYYY-MM-DD HH24:MI:SS')");
				}
				
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
	
	private String filterlist()
	{
		
		NetSyslogRuleDao ruledao = new NetSyslogRuleDao();
		List rulelist = ruledao.loadAll();
		if(rulelist != null && rulelist.size()>0){
			NetSyslogRule logrule = (NetSyslogRule)rulelist.get(0);
			String facility = logrule.getFacility();
			String[] facilitys = facility.split(",");
			List flist = new ArrayList();
			if(facilitys != null && facilitys.length>0){
				for(int i = 0;i<facilitys.length;i++){
					flist.add(facilitys[i]);
				}
			}
			request.setAttribute("facilitys", flist);
			
			String priority = logrule.getPriority();
			String[] prioritys = priority.split(",");
			List plist = new ArrayList();
			if(prioritys != null && prioritys.length>0){
				for(int i = 0;i<prioritys.length;i++){
					plist.add(prioritys[i]);
				}
			}
			request.setAttribute("prioritys", plist);
		}
        return "/alarm/syslog/filterlist.jsp";
	}
	
	private String monitornodelist()
	{
		HostNodeDao dao = new HostNodeDao();	 
		setTarget("/topology/network/monitornodelist.jsp");
        return list(dao," where managed=1");
	}
	
    private String read()
    {
	    DaoInterface dao = new HostNodeDao();
	    setTarget("/topology/network/read.jsp");
        return readyEdit(dao);
    }
    
    private String telnet()
    {
    	request.setAttribute("ipaddress",getParaValue("ipaddress"));
	    
        return "/tool/telnet.jsp";
    }
    
	private String readyEdit()
	{
	    DaoInterface dao = new HostNodeDao();
	    setTarget("/topology/network/edit.jsp");
        return readyEdit(dao);
	}
	
	private String readyEditAlias()
	{
	    DaoInterface dao = new HostNodeDao();
	    setTarget("/topology/network/editalias.jsp");
        return readyEdit(dao);
	}
	
	private String readyEditSysGroup()
	{
	    DaoInterface dao = new HostNodeDao();
	    setTarget("/topology/network/editsysgroup.jsp");
        return readyEdit(dao);
	}
	
	private String readyEditSnmp()
	{
	    DaoInterface dao = new HostNodeDao();
	    setTarget("/topology/network/editsnmp.jsp");
        return readyEdit(dao);
	}
	
	private String update()
	{         	  
		HostNode vo = new HostNode();
        vo.setId(getParaIntValue("id"));
        vo.setAlias(getParaValue("alias"));
        vo.setManaged(getParaIntValue("managed")==1?true:false);
        
        NodeToBusinessDao ntbdao = new NodeToBusinessDao();
        ntbdao.deleteallbyNE(vo.getId(), "equipment");
        ntbdao.close();
        String[] businessids = getParaArrayValue("checkbox");
        if(businessids != null && businessids.length>0){
        	for(int i=0;i<businessids.length;i++){
        		
        		String bid = businessids[i];
        		//SysLogger.info(bid+"====");
        		NodeToBusiness ntb = new NodeToBusiness();
        		ntb.setBusinessid(Integer.parseInt(bid));
        		ntb.setNodeid(vo.getId());
        		ntb.setElementtype("equipment");
        		ntbdao = new NodeToBusinessDao();
        		ntbdao.save(ntb);
        		ntbdao.close();
        	}
        }        
        //更新内存
 	    Host host = (Host)PollingEngine.getInstance().getNodeByID(vo.getId()); 	
 	    if(host != null){
 	    	host.setAlias(vo.getAlias());
 	    	host.setManaged(vo.isManaged());
 	    }else{
 	    	if(getParaIntValue("managed")==1){
 	    		HostNodeDao dao = new HostNodeDao();
 	    		HostNode hostnode = dao.loadHost(vo.getId());
 	    		hostnode.setAlias(getParaValue("alias"));
 	    		hostnode.setManaged(getParaIntValue("managed")==1?true:false);
 	    		HostLoader loader = new HostLoader();
 	    		loader.loadOne(hostnode);
 	    		//PollingEngine.getInstance().addNode(node)
 	    	}
 	    }
 	     	    
        //更新数据库
 	    DaoInterface dao = new HostNodeDao(); 	    
	    setTarget("/network.do?action=list");
        return update(dao,vo);
    }
	
	private String updatealias()
	{        
		HostNode vo = new HostNode();
        vo.setId(getParaIntValue("id"));
        vo.setAlias(getParaValue("alias"));
        vo.setManaged(getParaIntValue("managed")==1?true:false);

        
        //更新内存
 	    Host host = (Host)PollingEngine.getInstance().getNodeByID(vo.getId()); 	   
 	    host.setAlias(vo.getAlias());
 	    host.setManaged(vo.isManaged());
 	     	    
        //更新数据库
 	    DaoInterface dao = new HostNodeDao(); 	    
	    setTarget("/topology/network/networkview.jsp?id="+vo.getId()+"&ipaddress="+vo.getIpAddress());
        return update(dao,vo);
    }
	
	private String updatesysgroup()
	{         	  
		HostNode vo = new HostNode();
		HostNodeDao dao = new HostNodeDao();
		
		vo = dao.loadHost(getParaIntValue("id"));
        vo.setId(getParaIntValue("id"));
        vo.setSysName(getParaValue("sysname"));
        vo.setSysContact(getParaValue("syscontact"));
        vo.setSysLocation(getParaValue("syslocation"));
        
        Hashtable mibvalues = new Hashtable();
        mibvalues.put("sysContact", getParaValue("syscontact"));
        mibvalues.put("sysName", getParaValue("sysname"));
        mibvalues.put("sysLocation", getParaValue("syslocation"));
        
        //更新数据库
        dao.close();
        dao = new HostNodeDao();
 	    boolean flag = false;
 	    flag = dao.updatesysgroup(vo,mibvalues);
 	    if(flag){
 	    	//更新内存
 	    	Host host = (Host)PollingEngine.getInstance().getNodeByID(vo.getId()); 	  
 	    	host.setSysName(getParaValue("sysname"));
 	    	host.setSysContact(getParaValue("syscontact"));
 	    	host.setSysLocation(getParaValue("syslocation"));
 	    }	   
        return "/topology/network/networkview.jsp?id="+vo.getId()+"&ipaddress="+vo.getIpAddress();
    }
	
	private String updatesnmp()
	{         	  
		HostNode vo = new HostNode();
		HostNodeDao dao = new HostNodeDao();
		vo = dao.loadHost(getParaIntValue("id"));
        vo.setId(getParaIntValue("id"));
        vo.setCommunity(getParaValue("readcommunity"));
        vo.setWriteCommunity(getParaValue("writecommunity"));
        vo.setSnmpversion(getParaIntValue("snmpversion"));
        
        //更新数据库
        dao.close();
        dao = new HostNodeDao();
	    //更新内存
	    Host host = (Host)PollingEngine.getInstance().getNodeByID(vo.getId()); 	 
	    host.setCommunity(getParaValue("readcommunity"));
	    host.setWritecommunity(getParaValue("writecommunity"));
	    host.setSnmpversion(getParaIntValue("snmpversion"));
	    dao.updatesnmp(vo);
    	//setTarget("/monitor.do?action=netif&id="+vo.getId()+"&ipaddress="+vo.getIpAddress());
    	return "/topology/network/networkview.jsp?id="+vo.getId()+"&ipaddress="+vo.getIpAddress();
    }
	
	private String refreshsysname()
	{         	  
		HostNodeDao dao = new HostNodeDao();
		String sysName = "";
		sysName = dao.refreshSysName(getParaIntValue("id"));
 	    
        //更新内存
 	    Host host = (Host)PollingEngine.getInstance().getNodeByID(getParaIntValue("id")); 	   
 	    if(host != null){
 	    	host.setSysName(sysName);
 	    	host.setAlias(sysName);
 	    }

 	   return "/network.do?action=list";
    }
	
    private String delete()
    {
    	
    	String[] ids = getParaArrayValue("checkbox");
    	if(ids != null && ids.length > 0){
    		//进行修改
    		for(int i=0;i<ids.length;i++){
    		       //String id = getParaValue("radio"); 
    		        String id = ids[i]; 
    		        
    		      
  			
    		}
            
    	}
    	
        
        return "/network.do?action=list";
    }
    
    private String cancelmanage()
    {
    	String[] ids = getParaArrayValue("checkbox");
    	if(ids != null && ids.length > 0){
    		//进行修改
    		for(int i=0;i<ids.length;i++){
    			HostNodeDao dao = new HostNodeDao();
                HostNode host = (HostNode)dao.findByID(ids[i]);
                host.setManaged(false);
                dao = new HostNodeDao();
                dao.update(host);
                PollingEngine.getInstance().deleteNodeByID(Integer.parseInt(ids[i]));
    		}
            
    	}
        
        return "/network.do?action=monitornodelist";
    }

    private String add() 
    {  	 	  
	    String ipAddress = getParaValue("ip_address");
	    String alias = getParaValue("alias");
	    String community = getParaValue("community");
	    String writecommunity = getParaValue("writecommunity");
	    int type = getParaIntValue("type");
	    
	    TopoHelper helper = new TopoHelper(); //包括更新数据库和更新内存
	    int addResult = helper.addHost(ipAddress,alias,community,writecommunity,type); //加入一台服务器
	    if(addResult==0)
	    {	  
	        setErrorCode(ErrorMessage.ADD_HOST_FAILURE);
	        return null;      
	    }   
	    if(addResult==-1)
	    {	  
	        setErrorCode(ErrorMessage.IP_ADDRESS_EXIST);
	        return null;      
	    }   	    
	    if(addResult==-2)
	    {	  
	        setErrorCode(ErrorMessage.PING_FAILURE);
	        return null;      
	    }   	    
	    if(addResult==-3)
	    {	  
	        setErrorCode(ErrorMessage.SNMP_FAILURE);
	        return null;      
	    }      
	    
 	    //2.更新xml
	    XmlOperator opr = new XmlOperator();
	    opr.setFile("network.jsp");
	    opr.init4updateXml();
	    opr.addNode(helper.getHost());   
        opr.writeXml();
        
        return "/network.do?action=list";
    }  

    private String find()
    {
	   String key = getParaValue("key");
	   String value = getParaValue("value");	 
	   HostNodeDao dao = new HostNodeDao();
       request.setAttribute("list",dao.findByCondition(key,value));
     
       return "/topology/network/find.jsp";
   }

   private String save()
   {
       String[] fs = getParaArrayValue("fcheckbox");
       String faci_str="";
       if(fs != null && fs.length>0){
       		for(int i=0;i<fs.length;i++){
       		
       			String fa = fs[i];
       			faci_str=faci_str+fa+",";
       		}
       }
       String[] ps = getParaArrayValue("checkbox");
       String pri_str="";
       if(ps != null && ps.length>0){
       		for(int i=0;i<ps.length;i++){
       		
       			String pa = ps[i];
       			pri_str=pri_str+pa+",";
       		}
       }
       NetSyslogRuleDao ruledao = new NetSyslogRuleDao();
		List rulelist = ruledao.loadAll();
		NetSyslogRule rule = null;
		ruledao = new NetSyslogRuleDao();
		if(rulelist != null && rulelist.size()>0){
			rule = (NetSyslogRule)rulelist.get(0);
		}
		if(rule == null){
			rule = new NetSyslogRule();
			rule.setFacility(faci_str);
			rule.setPriority(pri_str);
			ruledao.save(rule);
		}else{
			rule.setFacility(faci_str);
			rule.setPriority(pri_str);
			ruledao.update(rule);
		}
	   
	   return "/netsyslog.do?action=filter&jp=1";   
   }
   
   private String savevlan()
   {
	   String xmlString = request.getParameter("hidXml");			
	   xmlString = xmlString.replace("<?xml version=\"1.0\"?>", "<?xml version=\"1.0\" encoding=\"GB2312\"?>");		
	   XmlOperator xmlOpr = new XmlOperator();
	   xmlOpr.setFile("networkvlan.jsp");
	   xmlOpr.saveImage(xmlString);
	   
	   return "/topology/network/save.jsp";   
   }
   
   private String netsyslogdetail()
   {
		Hashtable imgurlhash=new Hashtable();
		Hashtable hash = new Hashtable();//"Cpu"--current
		Hashtable userhash = new Hashtable();//"user"--current
		Hashtable maxhash = new Hashtable();//"Cpu"--max
		List list = new ArrayList();
		String busi = getParaValue("busi");

		try{
			String ip=getParaValue("ipaddress");
			int id = getParaIntValue("id");
			if(isNetworkDev(ip) || (null != busi && "userAccout".equals(busi))){
				NetSyslog syslog = new NetSyslog();
				NetSyslogDao dao = new NetSyslogDao();
				syslog = (NetSyslog)dao.findByID(id+"");							
				request.setAttribute("syslog", syslog);	
			}else{
				NetSyslogEvent syslog = new NetSyslogEvent();
				NetSyslogEventDao dao = new NetSyslogEventDao();
				syslog = (NetSyslogEvent)dao.findByID("nms_netsyslog", id+"");
				request.setAttribute("syslog", syslog);	
				return "/alarm/syslog/netsyslogdetail.jsp";
			}
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
	  if(action.equals("filter"))
		     return filterlist();
	  if(action.equals("netsyslogdetail"))
		 return netsyslogdetail(); 
      if(action.equals("read"))
         return read();
	  if(action.equals("ready_edit"))
         return readyEdit();
	  if(action.equals("ready_editalias"))
		  return readyEditAlias();
	  if(action.equals("ready_editsysgroup"))
		  return readyEditSysGroup();
	  if(action.equals("ready_editsnmp"))
	         return readyEditSnmp();
      if(action.equals("update"))
         return update(); 
      if(action.equals("cancelmanage"))
          return cancelmanage(); 
      if(action.equals("updatealias"))
          return updatealias();
      if(action.equals("updatesysgroup"))
          return updatesysgroup();
      if(action.equals("updatesnmp"))
          return updatesnmp();
      if(action.equals("refreshsysname"))
          return refreshsysname();
	  if(action.equals("delete"))
	     return delete();     
      if(action.equals("find"))
         return find();           
	  if(action.equals("ready_add"))
	     return "/topology/network/add.jsp";
      if(action.equals("add"))
         return add();
      if(action.equals("telnet"))
          return telnet(); 
      if(action.equals("save"))
         return save();
      if(action.equals("downloadsyslogreport"))
          return downloadsyslogreport();
      if(action.equals("downloadsyslogreportall"))
          return downloadsyslogreportall();
      if(action.equals("statistic"))
    	  return statistic();
      if (action.equals("exportStatistic"))
    	  return exportStatistic();
      if (action.equals("exportStatisticall"))
		  return exportStatisticall();
      if (action.equals("catelist"))
    	  return cateList();
      if (action.equals("exportCateListall"))
    	  return exportCatelistall();
      if (action.equals("syslogimpevt"))
    	  return syslogimpevt();
      if (action.equals("exportEvtCatelist"))
    	  return exportEvtCatelist();
      if (action.equals("exportImpevtCatelistall"))
    	  return exportImpevtCatelistall();
      if (action.equals("syslogallevt"))
    	  return syslogallevt();
      if (action.equals("syslogimpevtcatelist"))
    	  return syslogImpEvtCatelist();
      if (action.equals("syslogallevtcatelist"))
    	  return syslogAllEvtCatelist();
      if (action.equals("exportAllEvtCatelistall"))
    	  return exportAllevtCatelistall();
/*      if (action.equals("netsyslogeventdetail"))
    	  return netsyslogeventdetail();*/
      if (action.equals("questionlist")) 
    	  return questionlist();
      
      setErrorCode(ErrorMessage.ACTION_NO_FOUND);
      return null;
   }

	private String questionlist() {
		QuestionManager manager = new QuestionManager(request, response);
		return manager.doAction();
	}

	private String syslogAllEvtCatelist() {
		NetSyslogEventDao dao = new NetSyslogEventDao();
			String ipaddress = getParaValue("ipaddress");
			String eventid = getParaValue("eventid");
			String b_time = getParaDate("startdate");
			String t_time = getParaDate("todate");
			String starttime = b_time + " 00:00:00";
			String totime = t_time + " 23:59:59";
			request.setAttribute("startdate", b_time);
			request.setAttribute("todate", t_time);
			int perpage = getPerPagenum();
			int currentpage = getCurrentPage();
			request.setAttribute("eventid", eventid);
			request.setAttribute("ipaddress", ipaddress);
			String message = getParaValue("message");
			request.setAttribute("message", message);
			String processname = getParaValue("processname");
			request.setAttribute("processname", processname);
			String priority = getParaValue("priority");
			request.setAttribute("priority", priority);
			String where = "";
			
			if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
				where = " where recordtime >= '" + starttime + "' and recordtime <= '" + totime + "'";
			}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
				where = " where recordtime>= to_date('" + starttime
				+ "','YYYY-MM-DD HH24:MI:SS') " + "and recordtime<=to_date('"
				+ totime + "','YYYY-MM-DD HH24:MI:SS')";
			}

			if (null != eventid && !"".equals(eventid)) {
				where += " and eventid in (" + eventid + ")";
			}
			if (null != message && !"".equals(message)) {
				where += " and message like '%" + message + "%'";
			}
			if (null != processname && !"".equals(processname)) {
				where += " and processname like '%" + processname + "%'";
			}
			if (null != ipaddress && !"".equals(ipaddress)) {
				where += " and ipaddress like '%" + ipaddress + "%'";
			}
			if(null == priority  || "".equals(priority) || "null".equalsIgnoreCase(priority)){
			}else if(null != priority && "others".equals(priority)){
				where += " and priorityname != 'error' and priorityname != 'warning'";
			}else{
				where += " and priorityname= '" + priority.trim() + "'";
			}
			where += " order by id desc";
		try {
			List list = dao.listByPage("log" + SysUtil.doip(ipaddress) ,currentpage, where, perpage);
			request.setAttribute("page", dao.getPage());
			request.setAttribute("list", list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/alarm/syslog/allevtcatelist.jsp";
	}

	
/*   private String netsyslogeventdetail(){
		try{
			int id = getParaIntValue("id");
			String ip=getParaValue("ipaddress");
			if(isNetworkDev(ip)){
				NetSyslog syslog = new NetSyslog();
				NetSyslogDao dao = new NetSyslogDao();
				syslog = (NetSyslog)dao.findByID(id+"");	
				request.setAttribute("syslog", syslog);					
			}else{
				netsyslogdetail();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return "/alarm/syslog/netsyslogdetail.jsp";
	    //return "/detail/host_syslogdetail.jsp";
   }*/
   
   private String syslogImpEvtCatelist() {
		String ipaddress = getParaValue("ipaddress");
//		String eventid = getParaValue("eventid");
		String b_time = getParaDate("startdate");
		String t_time = getParaDate("todate");
		String starttime = b_time + " 00:00:00";
		String totime = t_time + " 23:59:59";
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);
		int perpage = getPerPagenum();
		int currentpage = getCurrentPage();
//		request.setAttribute("eventid", eventid);
		request.setAttribute("ipaddress", ipaddress);
		String message = getParaValue("message");
		request.setAttribute("message", message);
		String processname = getParaValue("processname");
		request.setAttribute("processname", processname);
		String streventname = getParaValue("streventname");
		request.setAttribute("streventname", streventname);
		String eventMsg = SyslogFinals.getMsgClause(streventname);
		String busi = getParaValue("busi");
		request.setAttribute("busi", busi);
		//ip模糊匹配
		String strDevType = " (category<5 or category=7 or category=8) ";
		String where = getWhere(starttime, totime, null, null, processname, message, ipaddress, eventMsg, strDevType);
		if (null != streventname && streventname.startsWith("nwDev")) {
			strDevType = " (category<4 or category=7 or category=8) ";
			String facility = processname;
			where = getWhere(starttime, totime, null, facility, null, message, ipaddress, eventMsg, strDevType);
		}else if(null != streventname && streventname.startsWith("qs")) {
			String username = getParaValue("username");
			request.setAttribute("username", username);
			where = getWhere(starttime, totime, null, null, null, message, ipaddress, eventMsg, null);
			if (null != username && !"".equals(username) && !"null".equals(username)) {
				where += " and message like '%" + username + "%'";
			}
		}

		try {
			where += " order by id desc";
			List list = new ArrayList();
			NetSyslogDao dao = new NetSyslogDao();
			list = dao.listByPage(currentpage, where, perpage);
			request.setAttribute("page", dao.getPage());
			request.setAttribute("list", list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/alarm/syslog/impevtcatelist.jsp";
	}
   
	private String syslogallevt() {
		String b_time = getParaDate("startdate");
		String t_time = getParaDate("todate");
		String starttime = b_time + " 00:00:00";
		String totime = t_time + " 23:59:59";
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);
		String ipaddress = getParaValue("ipaddress");
		request.setAttribute("ipaddress", ipaddress);
		List list = new ArrayList();
		NetSyslogViewerDao dao = new NetSyslogViewerDao();
		list = dao.loadNetSyslogViewersByIP(ipaddress, starttime, totime);
		request.setAttribute("allEvtList", list);
		return "/alarm/syslog/allevtstatistic.jsp";
}

	private String getParaDate(String date){
		String rtnDate = getParaValue(date);
		if (rtnDate == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			rtnDate = sdf.format(new Date());
		}
		return rtnDate;
	}
	
/**
    * syslog重要事件统计表
    * @return
    */
   private String syslogimpevt() {
	   String ipaddress = getParaValue("ipaddress");
		String b_time = getParaDate("startdate");
		String t_time = getParaDate("todate");
		String starttime = b_time + " 00:00:00";
		String totime = t_time + " 23:59:59";
		NetSyslogImpEventDao dao = new NetSyslogImpEventDao();
		NetSyslogImpEvent event = dao.getNetSyslogImpEvent(ipaddress,starttime,totime);
		request.setAttribute("ipaddress", ipaddress);
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);
		request.setAttribute("event", event);
//		if (isNetworkDev(ipaddress)) {
//			request.setAttribute("isNetWorkDev", true);
//		}
		return "/alarm/syslog/impevtstatistic.jsp";
	}

	private String cateList() {
		int perpage = getPerPagenum();
		int currentpage = getCurrentPage();
		String b_time = getParaDate("startdate");
		String t_time = getParaDate("todate");
		String starttime = b_time + " 00:00:00";
		String totime = t_time + " 23:59:59";
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);
		String strclass = getParaValue("strclass");
		String ipaddress = getParaValue("ipaddress");
		request.setAttribute("ipaddress", ipaddress);
		String priority = getParaValue("priority");
		request.setAttribute("priority", priority);
		String message = getParaValue("message");
		request.setAttribute("message", message);
		String processname = getParaValue("processname");
		request.setAttribute("processname", processname);
		String devtype = (String)getParaValue("devtype");
		request.setAttribute("devtype", devtype);
		
		List list = new ArrayList();
		if (null != devtype && !"".equalsIgnoreCase(devtype) && !"null".equalsIgnoreCase(devtype)) {
			List<String> ipaddressList = getIPAddressList(devtype, ipaddress);
			if (null != ipaddressList && ipaddressList.size() > 0) {
				for (String strIPAddress : ipaddressList) {
					if ("1".equals(devtype)) {
						NetSyslogEventDao dao = new NetSyslogEventDao();
						String where = getWhere(starttime, totime, priority, null, processname, message, strIPAddress, null, null);
						where += " order by id desc";
						List eventList = dao.listByPage(SysUtil.doip("log" + strIPAddress), 1, where, Integer.MAX_VALUE);
						if (null!= eventList && eventList.size() > 0) {
							list.addAll(convertToSyslogObj(eventList));
						}
					}else if("2".equals(devtype)) {//网络设备
						NetSyslogDao dao = new NetSyslogDao();
						String facility = processname;
						String where = getWhere(starttime, totime, priority, facility, null, message, strIPAddress, null, null);
						where += " order by id desc";
						List eventList = dao.listByPage(1, where, Integer.MAX_VALUE);
						list.addAll(eventList);
					}else{
						if (isNetworkDev(strIPAddress)) {
							NetSyslogDao dao = new NetSyslogDao();
							String facility = processname;
							String where = getWhere(starttime, totime, priority, facility, null, message, strIPAddress, null, null);
							where += " order by id desc";
							List eventList = dao.listByPage(1, where, Integer.MAX_VALUE);
							list.addAll(eventList);
						}else{
							NetSyslogEventDao dao = new NetSyslogEventDao();
							String where = getWhere(starttime, totime, priority, null, processname, message, strIPAddress, null, null);
							where += " order by id desc";
							List eventList = dao.listByPage(SysUtil.doip("log" + strIPAddress), 1, where, Integer.MAX_VALUE);
							list.addAll(convertToSyslogObj(eventList));
						}
					}
				}
				NetSyslogDao dao = new NetSyslogDao();
				list = dao.listByPage(currentpage, list, perpage);
				request.setAttribute("page", dao.getPage());
			}
		}else{
			String streventname = getParaValue("streventname");
			request.setAttribute("streventname", streventname);
			String eventMsg = null;
			if (streventname != null && !"".equals(streventname) && !"null".equals(streventname)) {
				eventMsg = SyslogFinals.getMsgClause(streventname);
			}
			if (isNetworkDev(ipaddress)) {
				NetSyslogDao dao = new NetSyslogDao();
				String facility = processname;
				String where = getWhere(starttime, totime, priority, facility, null, message, ipaddress, eventMsg, null);
				where += " order by id desc";
				list = dao.listByPage(currentpage, where, perpage);
				request.setAttribute("page", dao.getPage());
			}else{
				NetSyslogEventDao dao = new NetSyslogEventDao();
				String where = getWhere(starttime, totime, priority, null, processname, message, ipaddress, eventMsg, null);
				where += " order by id desc";
				list = dao.listByPage(SysUtil.doip("log" + ipaddress), currentpage, where, perpage);
				list = convertToSyslogObj(list);
				request.setAttribute("page", dao.getPage());
			}
			
		}
		request.setAttribute("list", list);
		if (list.size() == 0) {
			JspPage jspPage = new JspPage(perpage,1,0);
			request.setAttribute("page", jspPage);
		}
		return "/alarm/syslog/catelist.jsp";
	}
	
	private List convertToSyslogObj(List eventList) {
		List syslogList = new ArrayList();
		for (int i = 0; i < eventList.size(); i++) {
			NetSyslogEvent event = (NetSyslogEvent)eventList.get(i);
			NetSyslog syslog = new NetSyslog();
			syslog.setFacility(event.getFacility());
			syslog.setFacilityName(event.getFacilityName());
			syslog.setHostname(event.getHostname());
			syslog.setId(event.getId());
			syslog.setIpaddress(event.getIpaddress());
			syslog.setMessage(event.getMessage());
			syslog.setPriority(event.getPriority());
			syslog.setPriorityName(event.getPriorityName());
			syslog.setRecordtime(event.getRecordtime());
			syslogList.add(syslog);
		}
		return syslogList;
	}

	private String exportStatistic(){
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
		
		report.createReport_statistic("temp" + File.separator + "syslogstatistic.xls");
		request.setAttribute("filename", report.getFileName());
		return "/alarm/syslog/download.jsp";	
	}

   private String exportStatisticall() {
		String b_time = getParaDate("startdate");
		String t_time = getParaDate("todate");
		String starttime = b_time + " 00:00:00";
		String totime = t_time + " 23:59:59";
//		request.setAttribute("startdate", b_time);
//		request.setAttribute("todate", t_time);
		String strclass = getParaValue("strclass");
		String ipaddress = getParaValue("ipaddress");
		NetSyslogViewerDao dao = new NetSyslogViewerDao();
		List list = dao.loadNetSyslogViewers(Integer.MAX_VALUE, 1, starttime, totime, strclass, ipaddress);
		Hashtable reporthash = new Hashtable();
		if (list != null) {
			reporthash.put("list", list);
		} else {
			list = new ArrayList();
		}
		AbstractionReport1 report = new ExcelReport1(new IpResourceReport(),
				reporthash);

		report.createReport_statistic("temp" + File.separator + "syslogallstatistic.xls");
		request.setAttribute("filename", report.getFileName());
		return "/alarm/syslog/download.jsp";
	}
   
   private String exportCatelistall() {
	    String b_time = getParaDate("startdate");
	    String t_time = getParaDate("todate");
	    String starttime = b_time + " 00:00:00";
        String totime = t_time + " 23:59:59";
		String ipaddress = getParaValue("ipaddress");
		String priority = getParaValue("priority");
		String message = getParaValue("message");
		String processname = getParaValue("processname");
		String devtype = (String)getParaValue("devtype");

/*		String where = getWhere(starttime, totime, priority, null, processname, message, ipaddress, null, null);
		NetSyslogDao netSyslogDao = new NetSyslogDao();
//			List list = netSyslogDao.loadByPriority(ipaddress, priority, starttime, totime, processname, message);
		List list = netSyslogDao.loadByPriority(where);
		Hashtable reporthash = new Hashtable();
		if (list != null) {
			reporthash.put("list", list);
		} else {
			list = new ArrayList();
		}
		// reporthash.put("startRow", startRow);
		AbstractionReport1 report = new ExcelReport1(new IpResourceReport(),
				reporthash);

		report.createReport_syslogall("temp" + File.separator + "syslogall.xls");
		request.setAttribute("filename", report.getFileName());
		return "/alarm/syslog/download.jsp";*/
		List list = new ArrayList();
		if (null != devtype && !"null".equalsIgnoreCase(devtype)) {
			List<String> ipaddressList = getIPAddressList(devtype, ipaddress);
			if (null != ipaddressList && ipaddressList.size() > 0) {
				for (String strIPAddress : ipaddressList) {
					if ("1".equals(devtype)) {
						NetSyslogEventDao dao = new NetSyslogEventDao();
						String where = getWhere(starttime, totime, priority, null, processname, message, strIPAddress, null, null);
						where += " order by id desc";
						List eventList = dao.listByPage(SysUtil.doip("log" + strIPAddress), 1, where, Integer.MAX_VALUE);
						if (null!= eventList && eventList.size() > 0) {
							list.addAll(convertToSyslogObj(eventList));
						}
					}else if("2".equals(devtype)) {//网络设备
						NetSyslogDao dao = new NetSyslogDao();
						String facility = processname;
						String where = getWhere(starttime, totime, priority, facility, null, message, strIPAddress, null, null);
						where += " order by id desc";
						List eventList = dao.listByPage(1, where, Integer.MAX_VALUE);
						list.addAll(eventList);
					}else{
						if (isNetworkDev(strIPAddress)) {
							NetSyslogDao dao = new NetSyslogDao();
							String facility = processname;
							String where = getWhere(starttime, totime, priority, facility, null, message, strIPAddress, null, null);
							where += " order by id desc";
							List eventList = dao.listByPage(1, where, Integer.MAX_VALUE);
							list.addAll(eventList);
						}else{
							NetSyslogEventDao dao = new NetSyslogEventDao();
							String where = getWhere(starttime, totime, priority, null, processname, message, strIPAddress, null, null);
							where += " order by id desc";
							List eventList = dao.listByPage(SysUtil.doip("log" + strIPAddress), 1, where, Integer.MAX_VALUE);
							list.addAll(convertToSyslogObj(eventList));
						}
					}
				}
				NetSyslogDao dao = new NetSyslogDao();
				list = dao.listByPage(1, list, Integer.MAX_VALUE);
//				request.setAttribute("page", dao.getPage());
			}
		}else{
			String streventname = getParaValue("streventname");
			request.setAttribute("streventname", streventname);
			String eventMsg = null;
			if (streventname != null && !"".equals(streventname) && !"null".equals(streventname)) {
				eventMsg = SyslogFinals.getMsgClause(streventname);
			}
			if (isNetworkDev(ipaddress)) {
				NetSyslogDao dao = new NetSyslogDao();
				String facility = processname;
				String where = getWhere(starttime, totime, priority, facility, null, message, ipaddress, eventMsg, null);
				where += " order by id desc";
				list = dao.listByPage(1, where, Integer.MAX_VALUE);
//				request.setAttribute("page", dao.getPage());
			}else{
				NetSyslogEventDao dao = new NetSyslogEventDao();
				String where = getWhere(starttime, totime, priority, null, processname, message, ipaddress, eventMsg, null);
				where += " order by id desc";
				list = dao.listByPage(SysUtil.doip("log" + ipaddress), 1, where, Integer.MAX_VALUE);
				list = convertToSyslogObj(list);
//				request.setAttribute("page", dao.getPage());
			}
			
		}
		Hashtable reporthash = new Hashtable();
		if (list != null) {
			reporthash.put("list", list);
		} else {
			list = new ArrayList();
		}
		// reporthash.put("startRow", startRow);
		AbstractionReport1 report = new ExcelReport1(new IpResourceReport(),
				reporthash);

		report.createReport_syslogall("temp" + File.separator + "syslogall.xls");
		request.setAttribute("filename", report.getFileName());
		return "/alarm/syslog/download.jsp";		
	}	

	private String exportEvtCatelist(){
		String ipaddress = getParaValue("ipaddress");
		boolean isNetworkDev = isNetworkDev(ipaddress);
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
		report.createReport_syslog("temp" + File.separator + "netsyslog.xls");
		request.setAttribute("filename", report.getFileName());
		return "/alarm/syslog/download.jsp";
	}     
   
	private String exportImpevtCatelistall() {
		String ipaddress = getParaValue("ipaddress");
		String priority = getParaValue("priority");
		String b_time = getParaDate("startdate");
		String t_time = getParaDate("todate");
		String starttime = b_time + " 00:00:00";
		String totime = t_time + " 23:59:59";
		String message = getParaValue("message");
		String processname = getParaValue("processname");
		String facility = "";

		String streventname = getParaValue("streventname");
		request.setAttribute("strEventname", streventname);
		String eventMsg = SyslogFinals.getMsgClause(streventname);
		//ip模糊匹配
		String strDevType = " (category<5 or category=7 or category=8) ";
		String where = getWhere(starttime, totime, null, null, processname, message, ipaddress, eventMsg, strDevType);
		if (null != streventname && streventname.startsWith("nwDev")) {
			strDevType = " (category<4 or category=7 or category=8) ";
			facility = processname;
			where = getWhere(starttime, totime, null, facility, null, message, ipaddress, eventMsg, strDevType);
		}else if(null != streventname && streventname.startsWith("qs")) {
			String username = getParaValue("username");
			request.setAttribute("username", username);
			where = getWhere(starttime, totime, null, null, null, message, ipaddress, eventMsg, null);
			where += " and message like '%" + username + "%'";
		}
		
		try {
			where += " order by id desc";
			List list = new ArrayList();
			NetSyslogDao dao = new NetSyslogDao();
			list = dao.listByPage(1, where, Integer.MAX_VALUE);
			request.setAttribute("page", dao.getPage());
			request.setAttribute("list", list);
		
			Hashtable reporthash = new Hashtable();
			if (list!=null) {
				reporthash.put("list", list);
			}
			else {
				list = new ArrayList();
			}
			AbstractionReport1 report = new ExcelReport1(new IpResourceReport(),
					reporthash);
			report.createReport_syslogall("temp" + File.separator + "networksyslogall.xls");
			request.setAttribute("filename", report.getFileName());
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/alarm/syslog/download.jsp";			
	}	

	private String exportAllevtCatelistall() {
		NetSyslogEventDao dao = new NetSyslogEventDao();
		String ipaddress = getParaValue("ipaddress");	
//		String where = getWhere(starttime, totime, priority, eventid, processname, message, ipaddress);
		String where = getWhere();
		try {
			where += " order by id";
			List list = dao.listByPage("log" + SysUtil.doip(ipaddress),1, where, Integer.MAX_VALUE);
			Hashtable reporthash = new Hashtable();
			if (list!=null) {
				reporthash.put("list", list);
			}
			else {
				list = new ArrayList();
			}
			AbstractionReport1 report = new ExcelReport1(new IpResourceReport(),
					reporthash);
//			report.createReport_impevt("temp" + File.separator + "syslogall.xls");
			request.setAttribute("filename", report.getFileName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/alarm/syslog/download.jsp";
	}	
 

	/**
	 * syslog详细统计主页
	 * @return
	 */
	private String statistic() {
		int perpage = getPerPagenum();
		int currentpage = getCurrentPage();
		List list = new ArrayList();
		String b_time = getParaDate("startdate");
		String t_time = getParaDate("todate");
		String strclass = getParaValue("strclass");
		String ipaddress = getParaValue("ipaddress");
		String starttime = b_time + " 00:00:00";
		String totime = t_time + " 23:59:59";
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);
		
		NetSyslogViewerDao dao = new NetSyslogViewerDao();
		list = dao.loadNetSyslogViewers(perpage, currentpage, starttime, totime, strclass, ipaddress);
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);
		request.setAttribute("ipaddress", ipaddress);
		request.setAttribute("page", dao.getPage());
		request.setAttribute("viewersList", list);
		return "/alarm/syslog/statistic.jsp";
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
		
		report.createReport_syslog("temp/syslog_report.xls");
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
   
   private String getWhere(){
		String ipaddress = getParaValue("ipaddress");
		String eventid = getParaValue("eventid");
		String priority = getParaValue("priority");
		String b_time = getParaDate("startdate");
		String t_time = getParaDate("todate");
		String starttime = b_time + " 00:00:00";
		String totime = t_time + " 23:59:59";
		String message = getParaValue("message");
		String processname = getParaValue("processname");		   
		String where = "";
		if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
			where = " where recordtime >= '" + starttime + "' and recordtime <= '" + totime + "'";
		}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
			where = " where recordtime>= to_date('" + starttime
				+ "','YYYY-MM-DD HH24:MI:SS') " + "and recordtime<=to_date('"
				+ totime + "','YYYY-MM-DD HH24:MI:SS')";
		}
		
		if (null != eventid && !"".equals(eventid) && !"null".equalsIgnoreCase(eventid)) {
			where += " and eventid in (" + eventid.trim() + ")";
		}
		if (null != message && !"".equals(message) && !"null".equalsIgnoreCase(message)) {
			where += " and message like '%" + message.trim() + "%'";
		}
		if (null != processname && !"".equals(processname) && !"null".equalsIgnoreCase(processname)) {
			where += " and processname like '%" + processname.trim() + "%'";
		}
		if (null != ipaddress && !"".equals(ipaddress) && !"null".equalsIgnoreCase(ipaddress)) {
			where += " and ipaddress like '%" + ipaddress.trim() + "%'";
		}
		if (null == priority || "".equals(priority) || "null".equalsIgnoreCase(priority)) {//全部
		}else if(null != priority && "others".equals(priority)){
			where += " and priorityname != 'error' and priorityname != 'warning'";
		}else{
			where += " and priorityname= '" + priority.trim() + "'";
		}
		
	   return where;
   }
	
	
   private String getWhere(String starttime, String totime, String priority, String facility, String processname, String message, String ipaddress, String eventMsg, String strDevType){
//		String where = " where recordtime >= '" + starttime + "' and recordtime <= '" + totime + "'";
		String where = "";
		if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
			where = " where recordtime >= '" + starttime + "' and recordtime <= '" + totime + "'";
		}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
			where = " where recordtime>= to_date('" + starttime
				+ "','YYYY-MM-DD HH24:MI:SS') " + "and recordtime<=to_date('"
				+ totime + "','YYYY-MM-DD HH24:MI:SS')";
		}
		if (null != message && !"".equals(message) && !"null".equalsIgnoreCase(message)) {
			where += " and message like '%" + message.trim() + "%'";
		}
		if (null != processname && !"".equals(processname) && !"null".equalsIgnoreCase(processname)) {
			where += " and processname like '%" + processname.trim() + "%'";
		}
		if (null != facility && !"".equals(facility) && !"null".equalsIgnoreCase(facility)) {
			where += " and facilityname like '%" + facility.trim() + "%'";
		}
		if (null != ipaddress && !"".equals(ipaddress) && !"null".equalsIgnoreCase(ipaddress)) {
			where += " and ipaddress like '%" + ipaddress.trim() + "%'";
		}
		if (null == priority || "".equals(priority) || "null".equalsIgnoreCase(priority)) {//全部
		}else if(null != priority && "others".equals(priority)){
			where += " and priorityname != 'error' and priorityname != 'warning'";
		}else{
			where += " and priorityname= '" + priority.trim() + "'";
		}
		if (null != eventMsg && !"".equals(eventMsg) && !"null".equalsIgnoreCase(eventMsg)) {
			where += " and " + eventMsg.trim();
		}
		
		if (null != strDevType && !"".equals(strDevType) && !"null".equalsIgnoreCase(strDevType)) {
			where += " and " + strDevType.trim();
		}

	   return where;
   }
   
	public static String getTableName(String ipaddress){
		String table = "nms_netsyslog";
		boolean isNetworkDev = isNetworkDev(ipaddress);
		if (!isNetworkDev) {
			table = "log" + SysUtil.doip(ipaddress);
		}
		return table;
	}
	
	public static boolean isNetworkDev(String ipaddress){
		boolean isNetworkDev = false;
		HostNodeDao dao = new HostNodeDao();
		List nodeList = dao.loadNetwork(1);
		if (null != nodeList && nodeList.size() > 0) {
			for (int i = 0; i < nodeList.size(); i++) {
				HostNode node = (HostNode)nodeList.get(i);
				if (node.getIpAddress().equals(ipaddress)) {
					isNetworkDev = true;
					break;
				}
			}
		}
		return isNetworkDev;
	}  
	
	public static List<String> getIPAddressList(String strclass, String ipaddress){
		List allNodeList = PollingEngine.getInstance().getNodeList();
		List<String> ipaddressList = new ArrayList<String>();
		if (null != strclass) {
			if ("-1".equals(strclass)) {
				for (int i = 0; i < allNodeList.size(); i++) {
					if (((Host)allNodeList.get(i)).getIpAddress().indexOf(ipaddress) != -1) {
						ipaddressList.add(((Host)allNodeList.get(i)).getIpAddress());
					}
				}
			}else if("1".equals(strclass)){//主机设备
				for (int i = 0; i < allNodeList.size(); i++) {
					int category = ((Host)allNodeList.get(i)).getCategory();
					if (category==4 || category==7 || category==8) {
						if (((Host)allNodeList.get(i)).getIpAddress().indexOf(ipaddress) != -1) {
							ipaddressList.add(((Host)allNodeList.get(i)).getIpAddress());
						}
					}
				}
			}else if("2".equals(strclass)){//网络设备
				for (int i = 0; i < allNodeList.size(); i++) {
					int category = ((Host)allNodeList.get(i)).getCategory();
					if (category<4 || category==7 || category==8) {
						if (((Host)allNodeList.get(i)).getIpAddress().indexOf(ipaddress) != -1) {
							ipaddressList.add(((Host)allNodeList.get(i)).getIpAddress());
						}
					}
				}
			}
		}
		return ipaddressList;
	}
}
