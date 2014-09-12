/**
 * @author sunqichang/孙启昌
 * Created on May 20, 2011 3:07:48 PM
 */
package com.afunms.report.export;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableImage;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.log4j.Logger;

/**
 * @author sunqichang/孙启昌
 * 
 */
public class Excel implements ExportInterface {
	private static Logger log = Logger.getLogger(Excel.class);

	private WritableWorkbook wb = null;

	private WritableSheet sheet = null;

	/**
	 * 标题样式
	 */
	private WritableFont titlefont = new WritableFont(WritableFont.createFont("宋体"), 14, WritableFont.BOLD, false);

	/**
	 * 时间样式
	 */
	private WritableFont timefont = new WritableFont(WritableFont.createFont("宋体"), 12, WritableFont.NO_BOLD, false);

	private WritableFont tableTitleFont = new WritableFont(WritableFont.createFont("宋体"), 11, WritableFont.BOLD, false);

	private WritableFont tableFont = new WritableFont(WritableFont.createFont("宋体"), 11, WritableFont.NO_BOLD, false);

	private int x = 0;

	private int y = 0;

	/**
	 * 占excel的总列数
	 */
	private int excelCols = 12;

	/**
	 * @param path
	 */
	public Excel(String path) {
		try {
			wb = Workbook.createWorkbook(new File(path));
		} catch (IOException e) {
			log.error("", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.afunms.report.export.ExportInterface#insertTitle(java.lang.String,
	 *      int, java.lang.String)
	 */
	public void insertTitle(String title, int cols, String timefromto) throws Exception {
		if (sheet == null) {
			sheet = wb.createSheet(title, 0);
		}
		WritableCellFormat titleFormat = new WritableCellFormat(titlefont);
		WritableCellFormat timeFormat = new WritableCellFormat(timefont);
		titleFormat.setBackground(Colour.GRAY_25);
		titleFormat.setAlignment(Alignment.CENTRE);
		titleFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
		timeFormat.setBackground(Colour.GRAY_25);
		timeFormat.setAlignment(Alignment.CENTRE);
		timeFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
		Label tmpLabel = new Label(0, 0, title, titleFormat);
		Label timeLabel = new Label(0, 1, "时间：" + timefromto, timeFormat);
		y += 2;
		sheet.addCell(tmpLabel);
		sheet.addCell(timeLabel);
		if (cols > 0) {
			sheet.mergeCells(0, 0, excelCols - 1, 0);
			sheet.mergeCells(0, 1, excelCols - 1, 1);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.afunms.report.export.ExportInterface#insertChart(java.lang.String)
	 */
	public void insertChart(String path) throws Exception {
		if (sheet == null) {
			sheet = wb.createSheet("sheet", 1);
		}
		File file = new File(path);
		// 向sheet里面增加图片,0, 10, 10, 12分别代表列,行,图片宽度占多少列,高度占位多少行
		if (file != null && file.exists()) {
			x = 0;
			sheet.addImage(new WritableImage(x, y, excelCols, 14, file));
			x += excelCols;
			y += 14;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.afunms.report.export.ExportInterface#insertTable(java.util.ArrayList)
	 */
	public void insertTable(ArrayList<String[]> tableal) throws Exception {
		if (sheet == null) {
			sheet = wb.createSheet("sheet", 1);
		}
		try {
			WritableCellFormat labelFormat0 = new WritableCellFormat(tableTitleFont);
			labelFormat0.setShrinkToFit(true);
			labelFormat0.setBackground(jxl.format.Colour.GRAY_25);
			labelFormat0.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN, jxl.format.Colour.BLACK);
			labelFormat0.setAlignment(Alignment.CENTRE);
			labelFormat0.setVerticalAlignment(VerticalAlignment.CENTRE);
			WritableCellFormat labelFormat = new WritableCellFormat(tableFont);
			labelFormat.setShrinkToFit(true);
			labelFormat.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN, jxl.format.Colour.BLACK);
			labelFormat.setAlignment(Alignment.CENTRE);
			labelFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
			WritableCellFormat labelFormats = new WritableCellFormat(tableFont);
			labelFormats.setShrinkToFit(true);
			labelFormats.setBackground(Colour.ICE_BLUE);
			labelFormats.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN, jxl.format.Colour.BLACK);
			labelFormats.setAlignment(Alignment.CENTRE);
			labelFormats.setVerticalAlignment(VerticalAlignment.CENTRE);
			Label tmpLabel = null;
			for (int k = 0; k < tableal.size(); k++) {
				String[] row = tableal.get(k);
				int colspan = excelCols / row.length;
				x = 0;
				for (int j = 0; j < row.length; j++) {
					if (k == 0) {
						tmpLabel = new Label(x, y, row[j], labelFormat0);
					} else {
						if (k % 2 == 0) {
							tmpLabel = new Label(x, y, row[j], labelFormats);
						} else {
							tmpLabel = new Label(x, y, row[j], labelFormat);
						}
					}
					if (j == row.length - 1) {
						sheet.addCell(tmpLabel);
						sheet.mergeCells(x, y, x + colspan + excelCols % row.length - 1, y);
						x += colspan + excelCols % row.length;
					} else {
						sheet.addCell(tmpLabel);
						sheet.mergeCells(x, y, x + colspan - 1, y);
						x += colspan;
					}
				}
				y++;
			}
			// CellView cv = new CellView();
			// cv.setAutosize(true);
			// for (int i = 0; i < tableal.get(0).length; i++) {
			// // 设置列宽自适应
			// sheet.setColumnView(i, cv);
			// }
		} catch (Exception e) {
			log.error("", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.afunms.report.export.ExportInterface#save()
	 */
	public void save() throws Exception {
		wb.write();
		wb.close();
		log.info("------Excel saved successfully!------");
	}

}
