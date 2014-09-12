/**
 * <p>Description:network utilities</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-11
 */

package com.afunms.common.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.io.File;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.labels.StandardPieItemLabelGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;

import com.afunms.initialize.ResourceCenter;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ChartGraph {
	String path=ResourceCenter.getInstance().getSysPath()+"resource/image/jfreechart/";
	//String path="c:/";
	
	/**
	 *
	 */
	public ChartGraph() {
		super();
		// TODO Auto-generated constructor stub
	}

	//生成柱型图
	public String zhu(String map_title, String report_info,
			CategoryDataset dataset, int w, int h) {
		JFreeChart chart = ChartFactory.createBarChart3D(map_title, null, null,
				dataset, PlotOrientation.VERTICAL, true, false, false);
		Date d = new Date();
		String tmp = report_info + ".png";
		String fileName = path  + tmp;
		try {
			CategoryPlot plot = chart.getCategoryPlot();
			chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0,
					1000, Color.white));
			
			BarRenderer3D renderer = new BarRenderer3D();
			renderer.setWallPaint(new Color(Integer.parseInt("00", 16),Integer.parseInt("cc", 16),Integer.parseInt("00", 16),50));
			renderer.setSeriesPaint(0, new Color(Integer.parseInt("00", 16),Integer.parseInt("cc", 16),Integer.parseInt("00", 16),180));

			renderer.setItemLabelFont(new Font("黑体", Font.PLAIN, 15));
			renderer.setItemLabelsVisible(true);

			plot.setRenderer(renderer);
			plot.setForegroundAlpha(0.6f); //透明度
			plot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);

			ChartUtilities.saveChartAsPNG(new File(fileName), chart, w, h);//宽1000，高600
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileName;
	}

	//生成xy曲线图
	public String xywave(XYSeries[] series, String xName, String yName,
			String title, String report_info, int w, int h) {
		XYSeriesCollection xyDataset = new XYSeriesCollection();
		for (int i = 0; i < series.length; i++) {
			xyDataset.addSeries(series[i]);
		}
		String filename = "";
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy", Locale.CHINA);
		StandardXYToolTipGenerator ttg = new StandardXYToolTipGenerator(
				StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT, sdf,
				NumberFormat.getInstance());

		ValueAxis xAxis = new NumberAxis(xName);
		//				x轴坐标
		ValueAxis yAxis = new NumberAxis(yName);

		StandardXYItemRenderer renderer = new StandardXYItemRenderer(
				StandardXYItemRenderer.LINES, ttg);
		renderer.setShapesFilled(true);

		Font font = new Font("黑体", Font.PLAIN, 15);
		XYPlot plot = new XYPlot(xyDataset, xAxis, yAxis, renderer);
		JFreeChart chart = new JFreeChart(title, font, plot, true);
		chart.setBackgroundPaint(java.awt.Color.white);

		ChartRenderingInfo info = new ChartRenderingInfo(
				new StandardEntityCollection());
		String fileName = path + report_info + ".png";

		try {
			ChartUtilities
					.saveChartAsPNG(new File(fileName), chart, w, h, info);//宽1000，高600
		} catch (Exception e) {
			e.printStackTrace();
		}

		return fileName;
	}
	private double getMaxXAxis(TimeSeries series)
	{
		double max = 0;
		List list = series.getItems();
		for(int j = 0;j <list.size();j++)
		{
			 TimeSeriesDataItem item = (TimeSeriesDataItem)list.get(j);
			 double b = 0;
			 if(item.getValue()!=null)
			 {
				 b = item.getValue().doubleValue();
			 }
			 
			 if(b > max)
			 {
				 max = b;
			 }
		}
		return max;
	}

	//生成时间曲线图
	public String timewave(TimeSeries[] series, String xName, String yName,
			String title, String report_info, int w, int h) {
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		double maxY = 0;
		for (int i = 0; i < series.length; i++) {
			double b = getMaxXAxis(series[i]);
			if(maxY < b)
			{
				maxY = b;
			}
			dataset.addSeries(series[i]);
			
		}
		dataset.setDomainIsPointsInTime(true);
		JFreeChart chart = ChartFactory.createTimeSeriesChart(title, xName,
				yName, dataset, true, true, true);

		//StandardLegend sl = (StandardLegend) chart.getLegend();
		//sl.setd.setDisplaySeriesShapes(true);

		XYPlot plot = chart.getXYPlot();
		//plot.getDomainAxis().setLowerBound(16.0D);
		//plot.getDomainAxis().setAutoRangeMinimumSize(16.0D);
		
		//plot.getRangeAxis().setUpperBound(maxY);
		
		// plot.setBackgroundPaint(Color.lightGray);
		//plot.setDomainGridlinePaint(Color.white);
		//plot.setRangeGridlinePaint(Color.white);
		//plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
		//ddddddddddddddddddddddddddddddddddddddddd
		//XYPlot localXYPlot = (XYPlot)chart.getPlot();
		plot.setInsets(new RectangleInsets(0.0D, 0.0D, 0.0D, 20.0D));
	    double markerValue = 18.0D;
	    /*ValueMarker localValueMarker = new ValueMarker(markerValue);
	    if(maxY < markerValue)
	    {
	    	plot.getRangeAxis().setUpperBound(markerValue+2);
	    }
	    localValueMarker.setLabel("abcdefg");
	    
	    localValueMarker.setPaint(Color.green);
	    localValueMarker.setAlpha(0.8F);
	    plot.addRangeMarker(localValueMarker);*/
	    plot.setBackgroundPaint(null);
	    plot.setBackgroundImage(JFreeChart.INFO.getLogo());
	    plot.getDomainAxis().setLowerMargin(0.0D);
		//ddddddddddddddddddddddddddddddddddddddddd
		
		
		plot.setDomainCrosshairVisible(true);
		plot.setRangeCrosshairVisible(true);

		XYItemRenderer renderer = plot.getRenderer();
		if (renderer instanceof StandardXYItemRenderer) {
			StandardXYItemRenderer rr = (StandardXYItemRenderer) renderer;
			rr.setPlotShapes(true);
			rr.setShapesFilled(true);
		}

		DateAxis axis = (DateAxis) plot.getDomainAxis();
		//axis.setDateFormatOverride(new SimpleDateFormat("MM-yyyy"));
		chart.setBackgroundPaint(java.awt.Color.white);

		String fileName = path + report_info + ".png";
		try {
			ChartUtilities.saveChartAsPNG(new File(fileName), chart, w, h);//宽1000，高600
		} catch (Exception e) {
			e.printStackTrace();
		}

		return fileName;
	}

	public String otherwave(TimeSeries series, String name, String xName,
			String yName, String title, String subtitle, String report_info,
			int w, int h) {
		//String name="Series 1";
		//		double base=100.0;
		//		RegularTimePeriod start=new Minute();
		//		int count=200;
		//		 TimeSeries series = new TimeSeries(name, start.getClass());
		//		       RegularTimePeriod period = start;
		//		       double value = base;
		//		       for (int i = 0; i < count; i++) {
		//		           series.add(period, value);
		//		           period = period.next();
		//		           value = value * (1 + (Math.random() - 0.495) / 10.0);
		//		       }

		TimeSeriesCollection dataset = new TimeSeriesCollection();
		dataset.addSeries(series);

		JFreeChart chart = ChartFactory.createTimeSeriesChart(title, xName,
				yName, dataset, true, true, false);

		//chart.setBackgroundPaint(Color.white);
		chart.addSubtitle(new TextTitle(subtitle));
		XYPlot plot = chart.getXYPlot();
		plot.setOrientation(PlotOrientation.VERTICAL);
		//plot.setBackgroundPaint(Color.lightGray);
		//plot.setDomainGridlinePaint(Color.white);
		//plot.setRangeGridlinePaint(Color.white);

		//plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));

		//  StandardXYItemRenderer renderer = (StandardXYItemRenderer) plot.getRenderer();

		String fileName = path + report_info + ".png";

		try {
			ChartUtilities.saveChartAsPNG(new File(fileName), chart, w, h);//宽1000，高600
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileName;

	}

	public String pie(String title, DefaultPieDataset piedata,
			String report_info, int w, int h) {
		//		String title = "空调2002年市场占有率";
		//		DefaultPieDataset piedata = new DefaultPieDataset();
		////		第一个参数为名称，第二个参数是double数
		//		piedata.setValue("联想", 27.3);
		//		piedata.setValue("长城", 12.2);

		JFreeChart chart = ChartFactory.createPieChart(title, piedata, true,
				true, true);
		//			设定图片标题
		chart.setTitle(new TextTitle(title, new Font("隶书", Font.ITALIC, 15)));
		//			chart.addSubtitle(new TextTitle("2002财年分析", new Font("隶书", Font.ITALIC, 12)));
		//			设定背景
		chart.setBackgroundPaint(Color.white);
		//			chart.s
		//			饼图使用一个PiePlot
		PiePlot pie = (PiePlot) chart.getPlot();
		pie.setSectionPaint(1,Color.ORANGE);
		pie.setSectionPaint(0,Color.GREEN);
	
		pie.setLabelGenerator(new StandardPieItemLabelGenerator("{0} = {2}", NumberFormat.getNumberInstance(), NumberFormat.getPercentInstance())); 
		//			pie.setSectionLabelType(PiePlot.NAME_AND_PERCENT_LABELS);
		//pie.setSectionLabelType(PiePlot.NAME_AND_VALUE_LABELS);
		//			设定显示格式(名称加百分比或数值)
		//pie.setPercentFormatString("#,###0.0#%");
		//			设定百分比显示格式
		pie.setBackgroundPaint(Color.white);
		//pie.setSectionLabelFont(new Font("黑体", Font.TRUETYPE_FONT, 12));
		//			设定背景透明度（0-1.0之间）
		pie.setBackgroundAlpha(0.6f);
		//			设定前景透明度（0-1.0之间）
		pie.setForegroundAlpha(0.90f);
		String fileName = path+ report_info + ".png";

		try {
			ChartUtilities.saveChartAsPNG(new File(fileName), chart, w, h);//宽1000，高600
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileName;
	}
	
	//生成时间曲线图
	public String timeCurve(TimeSeries[] series, String xName, String yName,
			String title, String report_info, int w, int h) {
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		dataset.addSeries(series[0]);
		
		 TimeSeriesCollection dataset2 = new TimeSeriesCollection();
			dataset2.addSeries(series[1]);

		dataset.setDomainIsPointsInTime(true);
		JFreeChart chart = ChartFactory.createTimeSeriesChart(title, xName,
				yName, dataset, true, true, false);

		
		final XYPlot plot = chart.getXYPlot();
        plot.getDomainAxis().setLowerMargin(0.0);
        plot.getDomainAxis().setUpperMargin(0.0);
        plot.setRangeCrosshairVisible(true);
        plot.setDomainCrosshairVisible(true);
        plot.setBackgroundPaint(Color.WHITE);
 	    plot.setForegroundAlpha(0.5f);
 	    plot.setRangeGridlinesVisible(true);
 	    plot.setRangeGridlinePaint(Color.darkGray);
 	    plot.setDomainGridlinesVisible(true);
 	    plot.setDomainGridlinePaint(Color.darkGray);
 	    XYLineAndShapeRenderer render0 = (XYLineAndShapeRenderer)plot.getRenderer(0);
 	    render0.setSeriesPaint(0, Color.BLUE);
 	    
 	   
 	   XYAreaRenderer xyarearenderer = new XYAreaRenderer();
       xyarearenderer.setSeriesPaint(1, Color.GREEN);   //入口颜色
       xyarearenderer.setSeriesFillPaint(1, Color.GREEN);
       xyarearenderer.setPaint(Color.GREEN);
       plot.setDataset(1, dataset2);
       plot.setRenderer(1, xyarearenderer);
      
        String fileName = path + report_info + ".png";
        try {
			ChartUtilities.saveChartAsPNG(new File(fileName), chart, w, h);//宽1000，高600
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileName;
        
	}


}