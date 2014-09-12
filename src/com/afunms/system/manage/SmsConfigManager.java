/**
 * <p>Description:RoleManager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.system.manage;

import java.util.ArrayList;
import java.util.List;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.system.dao.RoleDao;
import com.afunms.system.dao.SmsConfigDao;
import com.afunms.system.model.Role;
import com.afunms.system.model.SmsConfig;


public class SmsConfigManager extends BaseManager implements ManagerInterface
{
   /**
    * 覆盖父类同名方法    
    */	
   private String delete()
   {	   
	   RoleDao dao = new RoleDao();
       if(dao.delete(getParaValue("radio")))
          return "/role.do?action=list";
       else
    	  return null;
   }
	 
   public String execute(String action)
   {
       if (action.equals("add")){    
    	   	ArrayList smsConfigList = new ArrayList();
       		String objectId = request.getParameter("objectId");
       		String objectType = request.getParameter("objectType");
       		String firewallconfid = request.getParameter("firewallconfid");
       		String confid = request.getParameter("confid");
       		System.out.println(objectId+"###########"+objectType+"=======");
       		request.setAttribute("objectId", objectId);
       		request.setAttribute("objectType", objectType);
       		request.setAttribute("firewallconfid", firewallconfid);
       		request.setAttribute("confid", confid);
       		int num = Integer.parseInt(request.getParameter("rowNum"));
       		System.out.println("num----"+num);            
       		for (int i = 1; i <= num; i++) {
       			System.out.println(i+"=================");            	
       			String partName = "";
       			if (i < 10) {
       				partName = "0" + String.valueOf(i);
       			} else {
       				partName = String.valueOf(i);
       			}
       			String beginTime = request.getParameter("beginTime" + partName);
       			System.out.println("beginTime----"+beginTime);                
       			if ((beginTime != null) && (!beginTime.equals(""))) {
       				String endTime = request.getParameter("endTime" + partName);
       				String userIds = request.getParameter("userIds" + partName);
       				SmsConfig smsConfig = new SmsConfig();
       				smsConfig.setObjectId(objectId);
       				smsConfig.setObjectType(objectType);
       				smsConfig.setBeginTime(beginTime);
       				smsConfig.setEndTime(endTime);
       				smsConfig.setUserIds(userIds);
       				smsConfigList.add(smsConfig);
       				System.out.println(objectId+"---"+objectType);
       			}
       		}
       		System.out.println("smsConfigList---"+smsConfigList.size()); 
       		SmsConfigDao smsdao = new SmsConfigDao();
       		try{
       			smsdao.saveSmsConfigList(objectId,objectType,smsConfigList);
       		}catch(Exception e){
       			e.printStackTrace();
       		}finally{
       			smsdao.close();
       		}
       		request.setAttribute("SmsConfigArrayList", smsConfigList);
       		return "/config/smsconfig/sms_item.jsp";
       	} 
       if (action.equals("query")) {
    	   	List smsConfigList = new ArrayList();
      		String objectId = request.getParameter("objectId");
      		String objectType = request.getParameter("objectType");
      		request.setAttribute("objectId", objectId);
       		request.setAttribute("objectType", objectType);
      		System.out.println(objectId+"###########"+objectType+"=======");
      		SmsConfigDao smsdao = new SmsConfigDao();
       		try{
       			smsConfigList = smsdao.getSmsConfigByObject(objectId, objectType);
       		}catch(Exception e){
       			e.printStackTrace();
       		}finally{
       			smsdao.close();
       		}
           	request.setAttribute("SmsConfigArrayList", smsConfigList);
      		return "/config/smsconfig/sms_item.jsp";
       }       
       setErrorCode(ErrorMessage.ACTION_NO_FOUND);
       return null;
   }
}
