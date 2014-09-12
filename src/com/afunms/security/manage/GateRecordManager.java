/**
 * <p>Description:Gate Record Manager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project 齐鲁石化
 * @date 2007-01-18
 */

package com.afunms.security.manage;

import java.util.List;

import com.afunms.common.base.*;
import com.afunms.security.dao.GateRecordDao;
import com.afunms.security.util.GateRecordReport;
import com.afunms.report.abstraction.ExcelReport;
import com.afunms.report.base.AbstractionReport;
import com.afunms.common.base.ErrorMessage;

public class GateRecordManager extends BaseManager implements ManagerInterface
{
   public GateRecordManager()
   {
   }

   private String list()
   {
	   GateRecordDao dao = new GateRecordDao();
       List list = dao.listByPage(getCurrentPage());	       
       if(list==null)
       {
    	   setErrorCode(ErrorMessage.CAN_NOT_CONNECT_DB);
    	   return null;
       }
       request.setAttribute("page",dao.getPage());
       request.setAttribute("list",list);
       return "/security/gate/list.jsp";
   }

   /**
    * 生成Excel文件
    */
   private String report()
   {
	   String[] queryItems = getParaArrayValue("query_item");
	   boolean[] itemSwitchs = new boolean[4];
	   for(int i=0;i<queryItems.length;i++)
		  itemSwitchs[Integer.parseInt(queryItems[i])] = true;
	   
	   String startTime = getParaValue("start_time");
	   String endTime = getParaValue("end_time");	   
	   String person = getParaValue("person");
	   String io = getParaValue("io");
	   String event = getParaValue("event");
     	
	   GateRecordReport gr = new GateRecordReport();
	   gr.setParameter(itemSwitchs, startTime, endTime, person, io, event);
	   AbstractionReport report = new ExcelReport(gr);        
	   report.createReport();
		
	   if(report.getFileName()==null)
	   {
    	   setErrorCode(ErrorMessage.CAN_NOT_CONNECT_DB);
    	   return null;
	   }
	   return "/inform/report/download.jsp?filename=" + report.getFileName();
   }	
   
   /**
    *  0=时间,1=人员姓名,2=进出,3=事件 
    */
   private String find()
   {	   
	   String[] queryItems = getParaArrayValue("query_item");
	   boolean[] itemSwitchs = new boolean[4];
	   for(int i=0;i<queryItems.length;i++)
		  itemSwitchs[Integer.parseInt(queryItems[i])] = true;
	   
	   String startTime = getParaValue("start_time");
	   String endTime = getParaValue("end_time");	   
	   String person = getParaValue("person");
	   String io = getParaValue("io");
	   String event = getParaValue("event");

	   GateRecordDao dao = new GateRecordDao();
       List list = dao.combinQuery(itemSwitchs, startTime, endTime,person,io,event);
       if(list==null)
       {
    	   setErrorCode(ErrorMessage.CAN_NOT_CONNECT_DB);
    	   return null;
       }       
       request.setAttribute("list",list);
	   return "/security/gate/find.jsp";	       
   }

   public String execute(String action)
   {
      if(action.equals("list"))
    	 return list();
      if(action.equals("report"))
     	 return report();
      if(action.equals("find"))
     	 return find();
      setErrorCode(ErrorMessage.ACTION_NO_FOUND);
      return null;
   }
}
