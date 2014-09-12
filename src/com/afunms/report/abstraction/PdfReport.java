/**
 * <p>Description:create pdf report,bridge pattern</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-11-18
 */

package com.afunms.report.abstraction;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.FileOutputStream;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import com.afunms.report.base.*;
import com.afunms.common.util.SysLogger;
import com.afunms.initialize.ResourceCenter;

public class PdfReport extends AbstractionReport
{
	public PdfReport(ImplementorReport impReport)
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
		
		Document doc = null;			
		try
		{	
            //----------------画表格-------------------
			BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
			Font headFont = new Font(bfChinese, 15, Font.BOLD);
			Font tableHeadFont = new Font(bfChinese,12, Font.BOLD);
		    Font normalFont = new Font(bfChinese, 12, Font.NORMAL);
		    		    		       
		    fileName = ResourceCenter.getInstance().getSysPath() + "temp/dhcnms_report.pdf"; 
			doc = new Document();
			PdfWriter.getInstance(doc, new FileOutputStream(fileName));
		    PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(fileName));
			doc.open();
			doc.newPage();
			doc.setPageSize((PageSize.A4));

		    Paragraph headPh = new Paragraph(impReport.getHead(),headFont);
		    headPh.setAlignment("center");
		    doc.add(headPh);
		    
		    Paragraph notePh = new Paragraph("生成时间：" + impReport.getTimeStamp(),normalFont);
		    notePh.setIndentationLeft(80);
		    doc.add(notePh);
		    
		    notePh = new Paragraph(impReport.getNote(),normalFont);
		    notePh.setIndentationLeft(80);
		    doc.add(notePh);
		        
			Table table = new Table(impReport.getTableHead().length);
			table.setAbsWidth("600");
			table.setBorderWidth(1);
			table.setPadding(1);				
			table.setWidths(impReport.getColWidth());
			
		    int row = impReport.getTable().length;
			int col = impReport.getTableHead().length;			    
			for(int i=0;i<col;i++)			
			   table.addCell(new Cell(new Paragraph(impReport.getTableHead()[i],tableHeadFont)));
							
			for(int i=0; i<row; i++)
			   for(int j=0; j<col; j++)
			   {				 					
					Cell cell = new Cell(new Paragraph(impReport.getTable()[i][j],normalFont));
					table.addCell(cell);
			   }		  
		    doc.add(table);		    
		    
		    //----------------画图-------------------
		    if(impReport.getChart()!=null)
		    {
		        doc.newPage();
		        int chartWidth = impReport.getChart().getWidth();
		        int chartHeight = impReport.getChart().getHeight();
				PdfContentByte pcb = writer.getDirectContent();
				PdfTemplate ptl = pcb.createTemplate(chartWidth,chartHeight);
				Graphics2D g2d = ptl.createGraphics(chartWidth,chartHeight, new DefaultFontMapper());
				Rectangle2D r2d = new Rectangle2D.Double(0,0, chartWidth,chartHeight);
				impReport.getChart().getChart().draw(g2d, r2d);
				g2d.dispose();
				pcb.addTemplate(ptl, 50, 450);
		    }	
		}
		catch(Exception e)
		{
			SysLogger.error("Error in PdfReport.createReport()",e);
		}
		finally
		{
			if(doc!=null) doc.close();
		}		
	}
}	