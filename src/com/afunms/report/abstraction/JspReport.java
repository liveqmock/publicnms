/**
 * <p>Description:create jsp report,bridge pattern</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-11-18
 */

package com.afunms.report.abstraction;

import com.afunms.report.base.*;
import com.afunms.common.util.SysLogger;

public class JspReport extends AbstractionReport
{
	private String table;
	
	/**
	 * 这里的chart只是JFreeChart的键值,真正的chart在ResoucreCenter中
	 */
	private String chart; 

	public JspReport(ImplementorReport impReport)
	{
		super(impReport);
		chart = impReport.getChartKey();
	}
	
	public void createReport()
	{
		createTable();
	}
		
	private void createTable()
	{
		if(impReport.getTable()==null)
		{
			setTable("没有可用数据，不能形成报表!");
			return;
		}
		if(impReport.getColWidth().length!=impReport.getTableHead().length)
		{
			SysLogger.error("colWidth[].length != tableHead[].length");
			return;
		}			
		StringBuffer tableStr = new StringBuffer(1000);
		tableStr.append("<table class='microsoftLook' width='90%'>");
		tableStr.append("<tr>");
		
	    int row = impReport.getTable().length;
		int col = impReport.getTableHead().length;	
		
		int totalWidth = 0;
		for(int i=0;i<col;i++)
			totalWidth += impReport.getColWidth()[i];
		
		for(int i=0;i<col;i++)
		{
			tableStr.append("<th width='");
			tableStr.append(impReport.getColWidth()[i]/totalWidth);
			tableStr.append("%'>");
			tableStr.append(impReport.getTableHead()[i]);
			tableStr.append("</th>");
		}
		tableStr.append("</tr>");
		
		for(int i=0; i<row; i++)
		{
			tableStr.append("<tr>");		
		    for(int j=0; j<col; j++)
		    {
		    	tableStr.append("<td class='microsoftLook0'>");
		    	tableStr.append(impReport.getTable()[i][j]);
		    	tableStr.append("</td>");
		    }
		    tableStr.append("</tr>");
		}
		tableStr.append("</table>");
		table = tableStr.toString();
	}
	
	public String getChart() 
	{
		return chart;
	}

	public void setChart(String chart) 
	{
		this.chart = chart;
	}
	
	public String getTable()
	{
	    return table;
	}

	public void setTable(String table)
	{
		this.table = table;
	}
	
	public String getHead()
	{
	    return impReport.getHead();
	}
	
	public String getTimeStamp()
	{
	    return impReport.getTimeStamp();	
	}	
}	