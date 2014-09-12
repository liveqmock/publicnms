/**
 * <p>Description:implementor report,bridge pattern</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-11-18
 */

package com.afunms.report.base;

import com.afunms.report.jfree.JFreeChartBrother;


import com.afunms.report.jfree.JFreeChartBrother;

public abstract class ImplementorReport2 
{
   	protected String head;
   	protected String timeStamp;
   	protected String note;
   	protected String chartKey;   	
   	protected JFreeChartBrother chart;
   	protected String[][] table;
   	protected int[] colWidth; //ап╣д©М╤х   	
   	protected String[] tableHead;
   	   	
	public JFreeChartBrother getChart() {
		return chart;
	}

	public void setChart(JFreeChartBrother chart) {
		this.chart = chart;
	}

	public String getChartKey() {
		return chartKey;
	}

	public void setChartKey(String chartKey) {
		this.chartKey = chartKey;
	}

	public String getHead() {
		return head;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String[][] getTable() {
		return table;
	}

	public void setTable(String[][] table) {
		this.table = table;
	}

	public String[] getTableHead() {
		return tableHead;
	}

	public void setTableHead(String[] tableHead) {
		this.tableHead = tableHead;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	public int[] getColWidth() {
		return colWidth;
	}

	public void setColWidth(int[] colWidth) {
		this.colWidth = colWidth;
	}
	
	public abstract void createReport();
}