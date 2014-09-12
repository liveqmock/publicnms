/**
 * <p>Description:abstraction report,bridge pattern</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-11-18
 */

package com.afunms.report.base;

/**
 * <p>
 * Description:abstraction report,bridge pattern
 * </p>
 * <p>
 * Company: dhcc.com
 * </p>
 * 
 * @author afunms
 * @project afunms
 * @date 2006-11-18
 */

import java.awt.Color;
import java.util.Hashtable;
import java.util.List;

import jxl.format.Alignment;
import jxl.format.VerticalAlignment;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WriteException;

import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.system.model.User;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.Element;
import com.lowagie.text.Phrase;
import com.lowagie.text.Table;

public abstract class AbstractionReport2 {
	protected String fileName;

	protected Hashtable<?, ?> reportHash;

	protected ImplementorReport2 impReport;

	protected WritableFont labelFont;

	protected WritableCellFormat labelFormat;

	protected WritableCellFormat labelFormat1;

	protected WritableCellFormat _labelFormat;

	protected WritableCellFormat p_labelFormat;

	protected WritableCellFormat b_labelFormat;

	public AbstractionReport2(ImplementorReport2 impReport) {
		this.impReport = impReport;
		impReport.createReport();

		labelFont = new WritableFont(WritableFont.createFont("宋体"), 12, WritableFont.BOLD, false);
		labelFormat = new WritableCellFormat(labelFont);
		labelFormat1 = new WritableCellFormat(labelFont);
		_labelFormat = new WritableCellFormat();
		p_labelFormat = new WritableCellFormat();
		b_labelFormat = new WritableCellFormat();
		try {
			labelFormat.setShrinkToFit(true);
			labelFormat.setBackground(jxl.format.Colour.GRAY_25);
			labelFormat.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN, jxl.format.Colour.BLACK);
			labelFormat.setAlignment(Alignment.CENTRE);
			labelFormat.setVerticalAlignment(VerticalAlignment.CENTRE);

			labelFormat1.setShrinkToFit(true);
			labelFormat1.setBackground(jxl.format.Colour.GRAY_25);
			labelFormat1.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN, jxl.format.Colour.BLACK);
			labelFormat1.setAlignment(Alignment.CENTRE);
			labelFormat1.setVerticalAlignment(VerticalAlignment.CENTRE);

			_labelFormat.setShrinkToFit(true);
			_labelFormat.setBackground(jxl.format.Colour.GRAY_25);
			_labelFormat.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN, jxl.format.Colour.BLACK);
			_labelFormat.setAlignment(Alignment.CENTRE);
			_labelFormat.setVerticalAlignment(VerticalAlignment.CENTRE);

			p_labelFormat.setShrinkToFit(true);
			p_labelFormat.setBackground(jxl.format.Colour.ICE_BLUE);
			p_labelFormat.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN, jxl.format.Colour.BLACK);
			p_labelFormat.setAlignment(Alignment.CENTRE);
			p_labelFormat.setVerticalAlignment(VerticalAlignment.CENTRE);

			b_labelFormat.setShrinkToFit(true);
			b_labelFormat.setBackground(jxl.format.Colour.GRAY_25);
			b_labelFormat.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN, jxl.format.Colour.BLACK);
			b_labelFormat.setAlignment(Alignment.CENTRE);
			b_labelFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
		} catch (WriteException e) {
			SysLogger.error("", e);
		}
	}

	public String getFileName() {
		return fileName;
	}
	public abstract void createReport_cabinet(String filename,String room);
	public abstract void createReport_Allcabinet(String filename);
	public abstract void createReport_equipment(String filename,String roomname,String cabinetname,String uselect,String unumber,String rate);
	public abstract void createReport_OperEquipment(String filename,String opername,String contactname,String contactphone);


	/**
	 * @param ip
	 * @return
	 */
	protected String doip(String ip) {
		String allipstr = SysUtil.doip(ip);
		return allipstr;
	}

	/**
	 * @author sunqichang/孙启昌
	 * 
	 * 隔行换色用
	 * @param num
	 * @return
	 */
	protected WritableCellFormat colorChange(final int num) {
		WritableCellFormat lf = new WritableCellFormat();
		try {
			lf.setShrinkToFit(true);
			lf.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN, jxl.format.Colour.BLACK);
			lf.setAlignment(Alignment.CENTRE);
			lf.setVerticalAlignment(VerticalAlignment.CENTRE);
			if (num % 2 == 0) {
				lf.setBackground(jxl.format.Colour.ICE_BLUE);
			}
		} catch (WriteException e) {
			SysLogger.error("", e);
		}
		return lf;
	}

	/**
	 * @author sunqichang/孙启昌
	 * 
	 * 设置单元格的格式
	 * 
	 * @param cell
	 *            单元格
	 * @param flag
	 *            是否设置灰色背景色
	 * @return cell
	 */
	protected Cell setCellFormat(Object obj, boolean flag) {
		Cell cell = null;
		Phrase p = null;
		if (obj instanceof Cell) {
			cell = (Cell) obj;
		} else if (obj instanceof Phrase) {
			p = (Phrase) obj;
			try {
				cell = new Cell(p);
			} catch (BadElementException e) {
				SysLogger.error("", e);
			}
		}
		if (cell != null) {
			if (flag) {
				cell.setBackgroundColor(Color.LIGHT_GRAY);
			}
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setLeading(6);
		}
		return cell;
	}

	/**
	 * @author sunqichang/jiruifei
	 * 
	 * 设置表格格式
	 * 
	 * @param aTable
	 */
	protected void setTableFormat(Table aTable) {
		aTable.setWidth(100);
		aTable.setAutoFillEmptyCells(true);
		aTable.setPadding(5);
		aTable.setAlignment(Element.ALIGN_CENTER);
	}
}