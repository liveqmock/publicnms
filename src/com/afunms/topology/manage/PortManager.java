/**
 * <p>Description:端口流量/利用率报表</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-11-20
 */

package com.afunms.topology.manage;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.SysUtil;

import com.afunms.report.base.*;
import com.afunms.report.abstraction.*;
import com.afunms.inform.util.*;
import com.afunms.polling.*;
import com.afunms.polling.node.*;

public class PortManager extends BaseManager implements ManagerInterface
{    
	private String getNote(int nodeId,String ifIndex)
	{
        Host host = (Host)PollingEngine.getInstance().getNodeByID(nodeId);
        IfEntity ifObj = (IfEntity)host.getInterfaceHash().get(ifIndex);        
    	StringBuffer note = new StringBuffer(100);
    	note.append("数据来源：东华网管软件");    	
    	note.append("\n设备：");
    	note.append(host.getAlias());
    	note.append("\nIP地址：");
    	note.append(host.getIpAddress());
    	note.append("\n端口号：");
    	note.append(ifObj.getPort());
    	note.append("\n端口索引：");
    	note.append(ifObj.getIndex());
    	note.append("\n端口描述：");
    	note.append(ifObj.getDescr());

    	return note.toString();
	}
	
	/* -------------------------流量报表------------------------------*/
	private PortTrafficReport trafficReport()
    {
		String timeStamp = getParaValue("day");
		if(timeStamp == null) timeStamp = SysUtil.getCurrentDate();
		int nodeId = Integer.parseInt(getParaValue("node_id"));
		String ifIndex = getParaValue("index");
		
		PortTrafficReport ptr = new PortTrafficReport();
		ptr.setTimeStamp(timeStamp);		
		ptr.setNodeId(nodeId);
		ptr.setIfIndex(ifIndex);
        ptr.setNote(getNote(nodeId,ifIndex));
        
		request.setAttribute("day",timeStamp);	    
		request.setAttribute("node_id",new Integer(nodeId));
		request.setAttribute("index",ifIndex);
		
	    return ptr;
    }
	
    private String portJsp()
	{
	    JspReport report1 = new JspReport(trafficReport());	    
	    report1.createReport();
	    
	    JspReport report2 = new JspReport(utilReport());	    
	    report2.createReport();
	    
		request.setAttribute("report1",report1);	    
		request.setAttribute("report2",report2);
		
		return "/inform/report/port_traffic.jsp";
	}
	
    private String trafficExcel()
	{
    	AbstractionReport report = new ExcelReport(trafficReport());        
		report.createReport();
		
		return "/inform/report/download.jsp?filename=" + report.getFileName();
	}	
	
	public String trafficPdf()
	{
		AbstractionReport report = new PdfReport(trafficReport());        
		report.createReport();
		
		return "/inform/report/download.jsp?filename=" + report.getFileName();
	}
	
	/* -----------------------利用率报表------------------------------- */
	private PortUtilReport utilReport()
    {
		String timeStamp = getParaValue("day");
		if(timeStamp == null) timeStamp = SysUtil.getCurrentDate();
		int nodeId = Integer.parseInt(getParaValue("node_id"));
		String ifIndex = getParaValue("index");
		
		PortUtilReport ptr = new PortUtilReport();
		ptr.setTimeStamp(timeStamp);		
		ptr.setNodeId(nodeId);
		ptr.setIfIndex(ifIndex);
        ptr.setNote(getNote(nodeId,ifIndex));
        
		request.setAttribute("day",timeStamp);	    
		request.setAttribute("node_id",new Integer(nodeId));
		request.setAttribute("index",ifIndex);
		
	    return ptr;
    }
		
    private String utilExcel()
	{
    	AbstractionReport report = new ExcelReport(utilReport());        
		report.createReport();
		
		return "/inform/report/download.jsp?filename=" + report.getFileName();
	}	
	
	public String utilPdf()
	{
		AbstractionReport report = new PdfReport(utilReport());        
		report.createReport();
		
		return "/inform/report/download.jsp?filename=" + report.getFileName();
	}

	public String execute(String action) 
	{		
		if(action.equals("port_jsp"))
			return portJsp();
	    if(action.equals("port_traffic_excel"))
    	    return trafficExcel();
	    if(action.equals("port_traffic_pdf"))
	    	return trafficPdf();		
	    if(action.equals("port_util_excel"))
	    	return utilExcel();		
	    if(action.equals("port_util_pdf"))
    	    return utilPdf();		
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
    }
}
