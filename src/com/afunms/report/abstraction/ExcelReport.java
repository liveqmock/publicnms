/**
 * <p>Description:create excel report,bridge pattern</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-11-18
 */

package com.afunms.report.abstraction;

import java.io.*;

import jxl.*;
import jxl.write.*;
import org.jfree.chart.ChartUtilities;

import com.afunms.report.base.*;
import com.afunms.common.util.SysLogger;
import com.afunms.initialize.ResourceCenter;

public class ExcelReport extends AbstractionReport
{
	public ExcelReport(ImplementorReport impReport)
	{
		super(impReport);
	}
	
	public void createReport()
	{
		if(impReport.getTable()==null)
		{
			fileName = null;
			return;
		}
		if(impReport.getColWidth().length!=impReport.getTableHead().length)
		{
			SysLogger.error("colWidth[].length != tableHead[].length");
			return;
		}
		
		WritableWorkbook wb = null;		
		try
		{
			fileName = ResourceCenter.getInstance().getSysPath() + "temp/dhcnms_report.xls"; 
			wb = Workbook.createWorkbook(new File(fileName));
			WritableSheet sheet = wb.createSheet("网管报表", 0);
			
			WritableFont labelFont = new WritableFont(WritableFont.createFont("宋体"), 12, WritableFont.BOLD, false); 
			WritableCellFormat labelFormat = new WritableCellFormat (labelFont); 
			
			Label tmpLabel = null;
		    tmpLabel = new Label(1, 0, impReport.getHead(),labelFormat); 
		    sheet.addCell(tmpLabel);
		    tmpLabel = new Label(0, 1, "生成时间:" + impReport.getTimeStamp());
		    sheet.addCell(tmpLabel);
		    tmpLabel = new Label(0, 2, impReport.getNote()); 
		    sheet.addCell(tmpLabel);
		     		    		    
		    int row = impReport.getTable().length;
			int col = impReport.getTableHead().length;			    
			for(int i=0;i<col;i++)
			{
			    Label label = new Label(i, 3, impReport.getTableHead()[i],labelFormat); 
			    sheet.addCell(label);
			}
			
			for(int i=0; i<row; i++)
			   for(int j=0; j<col; j++)
			   {				 
			 	   Label label = new Label(j, i + 4, impReport.getTable()[i][j]);			 	 
				   sheet.addCell(label);
			   }	
			
			if(impReport.getChart()!=null)
			{
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				try 
				{
					 ChartUtilities.writeChartAsPNG(baos, impReport.getChart().getChart(),impReport.getChart().getWidth(),impReport.getChart().getHeight());
				}
				catch (IOException ioe){}
				WritableImage wi = new WritableImage(2,row + 5, 8, 12, baos.toByteArray());
				sheet.addImage(wi);					
			}
			wb.write(); 	
		}
		catch(Exception e)
		{
			SysLogger.error("Error in ExcelReport.createReport()",e);			
		}
		finally
		{
			try
			{
			    if(wb!= null) wb.close();
			}  
			catch(Exception e){}
		}
	}
}