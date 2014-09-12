/**
 * <p>Description:DiscoverManager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-12
 */

package com.afunms.topology.manage;

import java.util.List;

import com.afunms.common.base.*;
import com.afunms.polling.*;
import com.afunms.polling.base.LinkRoad;
import com.afunms.polling.node.*;
import com.afunms.topology.model.*;
import com.afunms.topology.dao.*;
import com.afunms.topology.util.*;

public class ServerLinkManager extends BaseManager implements ManagerInterface
{
	private String list()
	{
		LinkDao dao = new LinkDao();
		request.setAttribute("list",dao.loadServerLinks());	     
	    return "/topology/server/link_list.jsp";
	}
	
    private String readyAdd()
    {
    	HostNodeDao dao = new HostNodeDao();
    	List list1 = dao.loadNetwork(0);
    	dao = new HostNodeDao();
    	List list2 = dao.loadServer();
    	
    	int startId = getParaIntValue("start_id");
    	int endId = getParaIntValue("end_id");
    	String startIndex = getParaValue("start_index");
    	String endIndex = getParaValue("end_index");
    	
    	if(startId==-1)
    	{
    		HostNode netNode = (HostNode)list1.get(0);
    		HostNode serverNode = (HostNode)list2.get(0);
    		startId = netNode.getId();
    		endId = serverNode.getId();
    		startIndex = "";
    		endIndex = "";
    	}
    	Host host1 = (Host)PollingEngine.getInstance().getNodeByID(startId);
    	Host host2 = (Host)PollingEngine.getInstance().getNodeByID(endId);
    	
    	request.setAttribute("start_if",host1.getInterfaceHash().values().iterator());
    	request.setAttribute("end_if",host2.getInterfaceHash().values().iterator());
    	request.setAttribute("start_id",new Integer(startId));
    	request.setAttribute("end_id",new Integer(endId));
    	request.setAttribute("start_index",startIndex);
    	request.setAttribute("end_index",endIndex);    	
	    request.setAttribute("list1",list1);	
	    request.setAttribute("list2",list2);		    
        return "/topology/server/link_add.jsp";
    }
    
    private String add() 
    {  	 	  
	    String startIndex = getParaValue("start_index");
	    String endIndex = getParaValue("end_index");
	    int startId = getParaIntValue("start_id");
	    int endId = getParaIntValue("end_id");
	    if(startId==endId)
	    {
	    	setErrorCode(ErrorMessage.DEVICES_SAME);
	    	return null;
	    }
	    
	    LinkDao dao = new LinkDao();
	    int exist = dao.linkExist(startId,endId);	    
	    if(exist==1)
	    {
	    	setErrorCode(ErrorMessage.LINK_EXIST);
	    	dao.close();
	    	return null;
	    }	

		Host startHost = (Host)PollingEngine.getInstance().getNodeByID(startId);
		IfEntity if1 = startHost.getIfEntityByIndex(startIndex);
		Host endHost = (Host)PollingEngine.getInstance().getNodeByID(endId);
		IfEntity if2 = endHost.getIfEntityByIndex(endIndex);
	    
	    Link link = new Link();
	    link.setStartId(startId);
	    link.setEndId(endId);
        link.setStartIndex(startIndex);
        link.setEndIndex(endIndex);
        link.setStartIp(if1.getIpAddress());
        link.setEndIp(endHost.getIpAddress());
        link.setStartDescr(if1.getDescr());
        link.setEndDescr(if2.getDescr());
        link.setType(2);
	    Link newLink = dao.save(link);
	    
 	    //更新xml
	    XmlOperator opr = new XmlOperator();
	    opr.setFile("server.jsp");
	    opr.init4updateXml();
	    String nodeId = String.valueOf(startId);
	    if(!opr.isNodeExist(nodeId))
	       opr.addNode(nodeId,startHost.getCategory(),null,startHost.getIpAddress(),startHost.getAlias(),"10","20");	    
	    opr.addLine(String.valueOf(newLink.getId()),String.valueOf(startId),String.valueOf(endId));
	    opr.writeXml();
	    
		LinkRoad lr = new LinkRoad();
		lr.setId(newLink.getId());
		lr.setStartId(startId);
		if("".equals(if1.getIpAddress()))
		   lr.setStartIp(startHost.getIpAddress());
		else
		   lr.setStartIp(if1.getIpAddress());			
		lr.setStartIndex(startIndex);
		lr.setStartDescr(if1.getDescr());
			    
		lr.setEndIp(endHost.getIpAddress());
		lr.setEndId(endId);		
		lr.setEndIndex(endIndex);
		lr.setEndDescr(if2.getDescr());
		lr.setAssistant(newLink.getAssistant());
		PollingEngine.getInstance().getLinkList().add(lr);

        return "/serverlink.do?action=list";
    }  
    	
    private String delete()
    {
        String id = getParaValue("radio"); 

        //更新数据库
        LinkDao dao = new LinkDao();
        dao.delete(id);
        
 	    //更新xml
        XmlOperator opr = new XmlOperator();
        opr.setFile("server.jsp");
        opr.init4updateXml();
        opr.deleteLineByID(id);
        opr.writeXml();
        
        //更新内存
        PollingEngine.getInstance().deleteLinkByID(Integer.parseInt(id));
        return "/serverlink.do?action=list";
    }
      
   public String execute(String action)
   {
	  if(action.equals("list"))
	     return list();     
	  if(action.equals("delete"))
	     return delete();     
	  if(action.equals("ready_add"))
	     return readyAdd();
      if(action.equals("add"))
         return add();
      setErrorCode(ErrorMessage.ACTION_NO_FOUND);
      return null;
   }
}
