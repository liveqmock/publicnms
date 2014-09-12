/**
 * <p>Description:DeviceTypeManager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-09
 */

package com.afunms.sysset.manage;

import java.util.List;

import com.afunms.common.base.*;
import com.afunms.common.util.SysLogger;
import com.afunms.sysset.dao.ServiceDao;
import com.afunms.sysset.model.Service;
import com.afunms.polling.*;
import com.afunms.polling.base.*;
import com.afunms.monitor.item.ServiceItem;
import com.afunms.monitor.item.base.MoidConstants;
import com.afunms.initialize.ResourceCenter;

public class ServiceManager extends BaseManager implements ManagerInterface
{
   private String list()
   {
	   ServiceDao dao = new ServiceDao();
       List list = dao.loadAll();       
       request.setAttribute("list",list);
       return "/sysset/service/list.jsp";
   }

   private String updateScan()
   {
	   ServiceDao dao = new ServiceDao();
	   if(dao.updateScan(getParaArrayValue("scan")))
	      return "/service.do?action=list";
	   else
		  return null; 
   }
   
   private String add()
   {
	   ServiceDao dao = new ServiceDao();
	   if(dao.isServiceExist(getParaValue("port")))
	   {
	       setErrorCode(ErrorMessage.SERVICE_EXIST);
	       dao.close();
	       return null;    	    	
	   }		   
	  
       Service vo = new Service();
       vo.setService(getParaValue("service"));
       vo.setPort(getParaIntValue("port"));       
       vo.setTimeOut(getParaIntValue("time_out"));
       String tmp = getParaValue("scan");       
       if(tmp!=null)
          vo.setScan(1);
       else
    	  vo.setScan(0);               
       dao.save(vo);
       
       reSetService();
       
       return "/service.do?action=list";	   
   }
   
   private void reSetService()
   {
	   ServiceDao dao = new ServiceDao();
       List servicesList = dao.loadService(1);
       try
       {
    	   List nodeList = PollingEngine.getInstance().getNodeList();
    	   for(int i=0;i<nodeList.size();i++)
    	   {
    		   Node node = (Node)nodeList.get(i);
    		   if(node.getCategory()!=4) continue;
    		   
    		   ServiceItem item = (ServiceItem)node.getItemByMoid(MoidConstants.TEST_SERVICE);
    		   item.setServicesStatus(new int[servicesList.size()]);
    	   }
    	   ResourceCenter.getInstance().setServiceList(servicesList);
       }
       catch(Exception e)
       {
    	   SysLogger.error("ServiceManager.reSetService()"); 
       }	   
   }
   
   private String delete()
   {
		DaoInterface dao = new ServiceDao();
	    String[] id = getParaArrayValue("checkbox");
	    dao.delete(id);
	    
	    reSetService();
	    
        return "/service.do?action=list";
   }
   
   public String execute(String action)
   {
      if(action.equals("list"))
         return list();
      if(action.equals("ready_add"))
         return "/sysset/service/add.jsp";
      if(action.equals("ready_edit"))
      {
  	      DaoInterface dao = new ServiceDao();
	      setTarget("/sysset/service/edit.jsp");
          return readyEdit(dao);
      }
	  if (action.equals("delete"))
      	  return delete();              
      if (action.equals("update"))
      {    	   
  	      Service vo = new Service();
  	      vo.setId(getParaIntValue("id"));
          vo.setService(getParaValue("service"));
          vo.setPort(getParaIntValue("port")); 
          vo.setTimeOut(getParaIntValue("time_out"));
          vo.setScan(getParaValue("scan")==null?0:1);
          DaoInterface dao = new ServiceDao();	   
  	      setTarget("/service.do?action=list");
          return update(dao,vo);
      }    	  
      if(action.equals("add"))
          return add();
      if(action.equals("update_scan"))
    	  return updateScan();
      setErrorCode(ErrorMessage.ACTION_NO_FOUND);
      return null;
   }
}
