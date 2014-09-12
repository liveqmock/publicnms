/**
 * <p>Description:DiscoverManager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-12
 */

package com.afunms.topology.manage;

import com.afunms.common.base.*;
import com.afunms.polling.*;
import com.afunms.polling.node.*;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.model.TelnetConfig;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.dao.TelnetDao;
import com.afunms.topology.util.*;

public class ServerManager extends BaseManager implements ManagerInterface
{	    
	private String list()
	{
		HostNodeDao dao = new HostNodeDao();
		request.setAttribute("list",dao.loadServer());	     
	    return "/topology/server/list.jsp";
	}

	private String read()
    {
	    DaoInterface dao = new HostNodeDao();
	    setTarget("/topology/server/read.jsp");
        return readyEdit(dao);
    }
    
	private String readyEdit()
	{
	    DaoInterface dao = new HostNodeDao();
	    setTarget("/topology/server/edit.jsp");
        return readyEdit(dao);
	}
	
	private String update()
	{         	  
		HostNode vo = new HostNode();
        vo.setId(getParaIntValue("id"));
        vo.setAlias(getParaValue("alias"));
 	            
        //更新内存
 	    Host host = (Host)PollingEngine.getInstance().getNodeByID(vo.getId()); 	   
 	    host.setAlias(vo.getAlias());
 	     	    
        //更新数据库
        HostNodeDao dao = new HostNodeDao(); 
        dao.update(vo);    	    
        return "/server.do?action=list";
    }    
	
    private String delete()
    {
        String id = getParaValue("radio"); 
                    
        PollingEngine.getInstance().deleteNodeByID(Integer.parseInt(id));        
        HostNodeDao dao = new HostNodeDao();
        dao.delete(id);       
        return "/server.do?action=list";
    }

    private String add() 
    {  	 	  
	    String ipAddress = getParaValue("ip_address");
	    String alias = getParaValue("alias");
	    String community = getParaValue("community");
	    String writecommunity = getParaValue("writecommunity");
	    
	    TopoHelper helper = new TopoHelper(); //包括更新数据库和更新内存
	    int addResult = helper.addHost(ipAddress,alias,writecommunity,community,4); //加入一台服务器
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
	    opr.setFile("server.jsp");
	    opr.init4updateXml();
	    opr.addNode(helper.getHost());   
        opr.writeXml();
        
        return "/server.do?action=list";
    }  
    
   private String save()
   {
 		String xmlString = request.getParameter("hidXml");				
 		xmlString = xmlString.replace("<?xml version=\"1.0\"?>", "<?xml version=\"1.0\" encoding=\"GB2312\"?>");
 		
 		XmlOperator xmlOpr = new XmlOperator(); 		
 		xmlOpr.setFile("server.jsp");
 		xmlOpr.saveImage(xmlString);
 		return "/topology/server/save.jsp";   
   }

   private String find()
   {
	   String key = getParaValue("key");
	   String value = getParaValue("value");	 
	   HostNodeDao dao = new HostNodeDao();
       request.setAttribute("list",dao.findByCondition(key,value));
    
       return "/topology/server/find.jsp";
  }
  
  private String readyConfig()
  {
	  return "/topology/server/config.jsp";  
  }
  
  /**
   * 衡水信用社2007.3.8
   */
  private String config()
  {
	  TelnetDao dao = new TelnetDao();
	  String user = getParaValue("user");
	  String password = getParaValue("user");
	  String prompt = getParaValue("prompt");
	  int nodeId = getParaIntValue("id");	 
	  
	  TelnetConfig vo = new TelnetConfig();
	  vo.setNodeID(nodeId);
	  vo.setUser(user);
	  vo.setPrompt(prompt);
	  vo.setPassword(password);
	  dao.update(vo);
	  
	  Host host = (Host)PollingEngine.getInstance().getNodeByID(nodeId);	  
	  host.setUser(user);
	  host.setPassword(password);
	  host.setPrompt(prompt);	  
	  return "/server.do?action=list";
  }
  
  public String execute(String action)
  {
      if(action.equals("list"))
         return list();      	   
      if(action.equals("read"))
         return read();      
	  if(action.equals("ready_edit"))
		 return readyEdit();
      if(action.equals("update"))
         return update();
	  if(action.equals("delete"))
		 return delete();
	  if(action.equals("ready_add"))
		 return "/topology/server/add.jsp";
      if(action.equals("add"))
         return add();
      if(action.equals("save"))
         return save();
      if(action.equals("find"))
         return find();           
      if(action.equals("ready_config"))
         return readyConfig();           
      if(action.equals("config"))
         return config();           
      
      setErrorCode(ErrorMessage.ACTION_NO_FOUND);
      return null;
  }
}
