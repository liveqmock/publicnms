/**
 * <p>Description:IP Locate Manager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-10-12
 */

package com.afunms.polling.manage;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.common.base.*;
import com.afunms.common.util.NetworkUtil;
import com.afunms.common.util.SysLogger;
import com.afunms.ipresource.dao.*;
import com.afunms.ipresource.model.IpResource;
import com.afunms.ipresource.util.DrawIPTable;
import com.afunms.ipresource.util.IpResourceReport;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.report.abstraction.ExcelReport;
import com.afunms.report.base.AbstractionReport;

public class NetFlowManager extends BaseManager implements ManagerInterface
{
   public NetFlowManager()
   {
   }
   
   private String netflow()
   {
		Hashtable imgurlhash=new Hashtable();
		Vector vector = new Vector();
		Hashtable bandhash = new Hashtable();
		
		int status =99;
		int level1 = 99;
		String b_time ="";
		String t_time = "";
		String srcip = "";
		String ip="";
		String tmp ="";
		double cpuvalue = 0;
		String pingconavg ="0";
		String time = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;
		try {
			
	    	tmp = request.getParameter("id");
	    	
	    	Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			ip=host.getIpAddress();	
			
			status = getParaIntValue("status");
	    	level1 = getParaIntValue("level1");
	    	if(status == -1)status=99;
	    	if(level1 == -1)level1=99;
	    	request.setAttribute("status", status);
	    	request.setAttribute("level1", level1);
	    	
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
			if(!b_time.contains(" 00:00:00")){
				b_time = b_time + " 00:00:00";
			}
			if(!t_time.contains(" 23:59:59")){
				t_time = t_time + " 23:59:59";
			}
//			String starttime1 = b_time + " 00:00:00";
//			String totime1 = t_time + " 23:59:59";
			
			srcip = getParaValue("srcip");
			request.setAttribute("startdate", b_time);
			request.setAttribute("todate", t_time);	
			request.setAttribute("srcip", srcip);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		//request.setAttribute("vector",vector);
		request.setAttribute("ipaddress",ip);
		request.setAttribute("id", tmp);
//		request.setAttribute("cpuvalue", cpuvalue);
//		request.setAttribute("collecttime", time);
//		request.setAttribute("sysuptime", sysuptime);
//		request.setAttribute("sysservices", sysservices);
//		request.setAttribute("sysdescr", sysdescr);
//		request.setAttribute("pingconavg", new Double(pingconavg));
	    return "/detail/net_netflow.jsp";
   }
   
   private String list()
   {
	   IpResourceDao dao = new IpResourceDao();
	   List list = dao.listByPage(getCurrentPage());
	   
 	   request.setAttribute("page",dao.getPage());
 	   request.setAttribute("list",list);
 	      
       return "/ipresource/list.jsp";
   }

   private String find()
   {	   
	   String value = getParaValue("value");
	   String key = getParaValue("key");
	   IpResourceDao dao = new IpResourceDao();
	   IpResource ipr = dao.find(key, value);
	   request.setAttribute("vo",ipr);	   
	   return "/ipresource/find.jsp";	       
   }
   
   private String report()
   {
     	AbstractionReport report = new ExcelReport(new IpResourceReport());        
		report.createReport();
		
		return "/inform/report/download.jsp?filename=" + report.getFileName();
   }	
   
   /**
    * IP分布
    */
   private String detail()
   {
	  String jsp = null;  
      try
      {        
         String beginip = getParaValue("beginip");
         String endip = getParaValue("endip");
         if(beginip==null) beginip = "10.10.20.0";
         if(endip==null) endip = "10.10.20.255";

         String outPutInfo = null;
         if(!NetworkUtil.checkIp(beginip)||!NetworkUtil.checkIp(endip))
        	outPutInfo = "<font color='red'>无效IP地址,请正确输入IP地址!</font>";
         else
         {	 
            long temp1 = NetworkUtil.ip2long(beginip);
            long temp2 = NetworkUtil.ip2long(endip);
            if(temp1>=temp2) 
        	   outPutInfo = "<font color='red'>起点IP必须小于终点IP,请重新输入!</font>";            
            else if(temp2-temp1>255) 
        	   outPutInfo = "<font color='red'>输入的两个IP不在同一网段,请重新输入!</font>";
            else
            { 
               DrawIPTable ipTable = new DrawIPTable();
               outPutInfo = ipTable.drawTable(beginip,endip,request.getContextPath());
            }
         }   
         request.setAttribute("beginip",beginip);
         request.setAttribute("endip",endip);
         request.setAttribute("out_put_info",outPutInfo);
         jsp = "/ipresource/table.jsp";
      }
      catch(Exception sqle)
      {
         jsp = null;
      }
      return jsp;
  }   
   
   /**
	 * 跳转到流量详细页
	 * @return
	 */
	public String showNetflowDetail(){
		//源地址IP
		String srcip = getParaValue("srcip");
		//设备ID
		String nodeid = getParaValue("nodeid");
		//起始日期
		String startdate = getParaValue("startdate");
		String todate = getParaValue("todate");
		
		try {
			startdate = URLDecoder.decode(startdate, "UTF-8");//解码
			todate = URLDecoder.decode(todate, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		if(!startdate.contains(" 00:00:00")){
			startdate = startdate + " 00:00:00";
		}
		if(!todate.contains(" 23:59:59")){
			todate = todate + " 23:59:59";
		}
//		IpAccountingDao ipdao = new IpAccountingDao();
//		List flowDetailList = new ArrayList();
//		try {
//			flowDetailList = ipdao.getNetFLowDetail(nodeid, srcip, startdate, todate);
//		} catch (Exception e) {
//			SysLogger.error("获取流速详细信息出错", e);
//		} finally {
//			ipdao.close();
//		}
//		session.setAttribute("flowDetailList", flowDetailList);
//		request.setAttribute("flowDetailList", flowDetailList);
		request.setAttribute("nodeid", nodeid);
		request.setAttribute("srcip", srcip);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		return "/detail/net_netflow_detail.jsp";
	}
   
   public String execute(String action)
   {
      if(action.equals("netflow"))
      	 return netflow();
      if(action.equals("detail"))
       	 return detail();
      if(action.equals("list"))
	     return list();
      if(action.equals("find"))
	     return find();
      if(action.equals("report"))
	     return report();
      if(action.equals("showNetflowDetail"))
    	  return showNetflowDetail();
      setErrorCode(ErrorMessage.ACTION_NO_FOUND);
      return null;
   }
}