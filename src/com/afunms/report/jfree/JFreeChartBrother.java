package com.afunms.report.jfree;

import org.jfree.chart.JFreeChart;

/**
 * 封装jfreechart,再多加两个属性:宽和高
 */
public class JFreeChartBrother
{
    private JFreeChart chart;
    private int width;
    private int height;
	public JFreeChart getChart() {
		return chart;
	}
	public void setChart(JFreeChart chart) {
		this.chart = chart;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}     
}