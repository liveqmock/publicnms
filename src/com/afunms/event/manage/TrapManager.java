/**
 * <p>Description:DiscoverManager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-12
 */

package com.afunms.event.manage;

import com.afunms.common.base.*;
import com.afunms.common.util.CreateTableManager;
import com.afunms.common.util.SessionConstant;
import com.afunms.polling.*;
import com.afunms.polling.node.*;
import com.afunms.system.model.User;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.dao.DiscoverCompleteDao;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.util.*;
import com.afunms.common.util.DBManager;
import com.afunms.config.model.*;
import com.afunms.config.dao.*;
import com.afunms.event.dao.*;
import com.afunms.common.util.*;

import java.text.SimpleDateFormat;
import java.util.*;

import com.afunms.polling.loader.HostLoader;

/**
 * 增加一个节点，要在server.jsp增加一个节点;
 * 删除或更新一个节点信息，在这则不直接操作server.jsp,而是让任务updateXml来更新xml.
 */
public class TrapManager extends BaseManager implements ManagerInterface
{
	private String list()
	{
		int status =99;
		int level1 = 99;
		int bid = 0;
		String b_time ="";
		String t_time = "";
		String content = "";
		EventListDao dao = new EventListDao();
    	status = getParaIntValue("status");
    	level1 = getParaIntValue("level1");
    	if(status == -1)status=99;
    	if(level1 == -1)level1=99;
    	request.setAttribute("status", status);
    	request.setAttribute("level1", level1);
    	
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
			
			if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
				s.append("where recordtime>= '"+starttime1+"' " +
						"and recordtime<='"+totime1+"'");
			}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
				s.append("where recordtime>= to_date('"+starttime1+"','YYYY-MM-DD HH24:MI:SS') " +
						"and recordtime<=to_date('"+totime1+"','YYYY-MM-DD HH24:MI:SS')");
			}
			
			
			
			s.append(" and eventtype = 'trap' ");
			if(!"99".equals(level1+"")){
				s.append(" and level1="+level1);
			}
			if(!"99".equals(status+"")){
				s.append(" and managesign="+status);
			}
			String businessid = vo.getBusinessids();
			int flag = 0;
			if(bid > 0){
				s.append(" and businessid like '%,"+bid+",%'");
			}
			if(content != null && content.trim().length()>0){
				s.append(" and content like '%"+content+"%'");
			}
			/*
			if (businessid != null){
				if(businessid !="-1"){
					String[] bids = businessid.split(",");
					if(bids.length>0){
						for(int i=0;i<bids.length;i++){
							if(bids[i].trim().length()>0){
								if(flag==0){
									s.append(" and ( businessid = ',"+bids[i].trim()+",' ");
									flag = 1;
								}else{
									//flag = 1;
									s.append(" or businessid = ',"+bids[i].trim()+",' ");
								}
							}
						}
						s.append(") ") ;
					}
					
				}	
			}	
			*/
			sql = s.toString()+" order by id desc";
			
		}catch(Exception e){
			e.printStackTrace();
		}
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);
		setTarget("/alarm/trap/list.jsp");
        return list(dao,sql);
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
	   String xmlString = request.getParameter("hidXml");	
	   String vlanString = request.getParameter("vlan");	
	   xmlString = xmlString.replace("<?xml version=\"1.0\"?>", "<?xml version=\"1.0\" encoding=\"GB2312\"?>");		
	   XmlOperator xmlOpr = new XmlOperator();
	   if(vlanString != null && vlanString.equals("1")){
		   xmlOpr.setFile("networkvlan.jsp");
	   }else
		   xmlOpr.setFile("network.jsp");
	   xmlOpr.saveImage(xmlString);
	   
	   return "/topology/network/save.jsp";   
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
    
   public String execute(String action)
   {
	  if(action.equals("list"))
	     return list(); 
	  if(action.equals("monitornodelist"))
		 return monitornodelist(); 
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
      setErrorCode(ErrorMessage.ACTION_NO_FOUND);
      return null;
   }
}
