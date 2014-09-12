/**
 * @author wxy add
 * Created on Jun 11, 2011 3:53:08 PM
 */
package com.afunms.report.export;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;


public class ExportPdf implements ExportInterface {

	private final static Log log = LogFactory.getLog(Pdf.class);

	private BaseFont bfChinese = null;

	private Font FontChineseTitle = null;

	private Font FontChineseRow = null;

	private Document document = null;
	Table table =null;
	/**
	 * 
	 * @param fileName
	 */
	public ExportPdf(String fileName) {
		try {
			bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
			FontChineseTitle = new Font(bfChinese, 14, Font.BOLD);
			FontChineseRow = new Font(bfChinese, 12, Font.NORMAL);
			table = new Table(1);
			table.setAutoFillEmptyCells(true);
			table.setAlignment(Element.ALIGN_CENTER);
			table.setCellsFitPage(true);
			table.setWidth(100);
			table.setBorder(0);
		} catch (DocumentException e) {
			log.error("", e);
		} catch (IOException e) {
			log.error("", e);
		}
		// step 1: creation of a document-object
		Rectangle pageSize = new Rectangle(PageSize.A4);
		document = new Document(pageSize);
		try {
			// step 2:
			// we create a writer that listens to the document
			// and directs a PDF-stream to a file
			PdfWriter.getInstance(document, new FileOutputStream(fileName));
			document.open();
		} catch (DocumentException de) {
			log.error("", de);
		} catch (IOException ioe) {
			log.error("", ioe);
		} catch (Exception e) {
			log.error("", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.afunms.report.ExportInterface#insertTitle(java.lang.String, int,
	 *      java.lang.String)
	 */
	public void insertTitle(String title, int colspan, String timefromto) throws Exception {
		if (!document.isOpen()) {
			document.open();
		}
		Paragraph par = new Paragraph(title, FontChineseTitle);
		Paragraph time = new Paragraph("日期：" + timefromto, FontChineseTitle);
		par.setAlignment(Element.ALIGN_CENTER);
		time.setAlignment(Element.ALIGN_CENTER);
		document.add(par);
		document.add(time);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.afunms.report.ExportInterface#insertTable(java.util.ArrayList)
	 */
	public void insertTable(ArrayList<String[]> tableal) throws Exception {
		// step 3: we open the document
		if (!document.isOpen()) {
			document.open();
		}
		Table pdfTable = new Table(tableal.get(0).length);
		for (int k = 0; k < tableal.size(); k++) {
			String[] row = tableal.get(k);
			for (int j = 0; j < row.length; j++) {
				Cell pdfcell = new Cell();
				pdfcell.setLeading(6);
				// pdfcell.setMaxLines(1);
				if (k == 0) {
					pdfcell.addElement(new Paragraph(row[j], FontChineseTitle));
					pdfcell.setBackgroundColor(Color.gray);
					pdfTable.endHeaders();
				} else {
					pdfcell.addElement(new Paragraph(row[j], FontChineseRow));
					if (k % 2 == 0) {
						pdfcell.setBackgroundColor(Color.LIGHT_GRAY);
					}
				}
				// 合并单元格
				// pdfcell.setColspan(1);
				// pdfcell.setRowspan(1);
				// 对齐方式
				pdfcell.setHorizontalAlignment(Element.ALIGN_CENTER);
				pdfcell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				pdfTable.addCell(pdfcell);
			}
		}
		pdfTable.setWidth(100);
		// 设置表格填距
		pdfTable.setPadding(5);
		pdfTable.setAutoFillEmptyCells(true);
		pdfTable.setAlignment(Element.ALIGN_CENTER);
		document.add(pdfTable);
//		document.newPage();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.afunms.report.ExportInterface#insertChart(java.lang.String)
	 */
	public void insertChart(String path) throws Exception {
		if (!document.isOpen()) {
			document.open();
		}
		Image png = Image.getInstance(path);
		// png.scaleAbsolute(540, 156);
		png.scalePercent(90);
		//Table pngtable = new Table(1);
//		pngtable.setAutoFillEmptyCells(true);
//		pngtable.setAlignment(Element.ALIGN_CENTER);
//		pngtable.setCellsFitPage(true);
//		pngtable.setWidth(100);
//		pngtable.setBorder(0);
		Cell cell = new Cell(png);
		cell.setBorder(0);
		table .addCell(cell);
		//document.add(table);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.afunms.report.ExportInterface#save()
	 */
	public void save() {
		// step 5: we close the document
		try {
			document.add(table);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		document.close();
		log.info("------Pdf saved successfully!------");
	}

}
