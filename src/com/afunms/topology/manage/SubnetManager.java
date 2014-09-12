/**
 * <p>Description:SubnetManager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-09-20
 */

package com.afunms.topology.manage;

import java.util.*;

import com.afunms.common.base.*;
import com.afunms.topology.dao.SubnetDao;
import com.afunms.polling.*;
import com.afunms.polling.base.*;
import com.afunms.polling.node.*;

public class SubnetManager extends BaseManager implements ManagerInterface
{
	private String list()
	{
		SubnetDao dao = new SubnetDao();
		List list = dao.loadAll();
		
		request.setAttribute("list",list);
		return "/topology/subnet/list.jsp";
	}
	
	private String listDevice()
	{
		int netId = getParaIntValue("id");
		
		List hostList = PollingEngine.getInstance().getNodeList();
		List list = new ArrayList(20);
		for(int i=0;i<hostList.size();i++)
		{
			if(((Node)hostList.get(i)).getCategory()>=50) continue; //应用的监视器>50
			
			Host host = (Host)hostList.get(i);			
			if(host.getLocalNet()==netId)
			   list.add(host);
		}	
		request.setAttribute("list",list);
		request.setAttribute("address",getParaValue("ip"));
		return "/topology/subnet/list_device.jsp";		
	}
	
    public String execute(String action)
    {
       if(action.equals("list"))
          return list();     
       if(action.equals("list_device"))
          return listDevice();    
       setErrorCode(ErrorMessage.ACTION_NO_FOUND);
       return null;
    }
}