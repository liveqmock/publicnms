/**
 * <p>Description:a artist who draw various chart</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-18
 */

package com.afunms.report.jfree;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.labels.StandardPieItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DialShape;
import org.jfree.chart.plot.MeterPlot;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer3D;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.DefaultValueDataset;
import org.jfree.data.time.Minute;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.Rotation;

import com.afunms.common.util.ChartGraph;
import com.afunms.common.util.SysUtil;
import com.afunms.initialize.ResourceCenter;

public class ChartCreator
{
  /**
   * 画钟形图 
   */	
   public static String createMeterChart(double value,String title,int width,int height)
   {
	   DefaultValueDataset defaultDataSet = new DefaultValueDataset(value);
	   MeterPlot meterplot = new MeterPlot(defaultDataSet);
       meterplot.setUnits("%");       
       meterplot.setDialOutlinePaint(Color.green);
       meterplot.setOutlineStroke(new BasicStroke(10F, 1, 1, 1.0F, new float[] {2.0F, 6F}, 0.0F));
       meterplot.setDialShape(DialShape.CHORD);
       meterplot.setTickLabelFont(new Font("SansSerif", 1, 8));
       meterplot.setTickLabelsVisible(true);       
       JFreeChart jfreechart = new JFreeChart(title, new Font("Verdena", 1, 0), meterplot, false);
       jfreechart.setTitle(new TextTitle(title, new Font("宋体", 0, 12)));
       jfreechart.setBackgroundPaint(Color.white);
              
	   return generateKey(jfreechart,width,height);   
   }
   
   /**
    * 画饼状图 
    */
   public static String createPieChart(DefaultPieDataset dataSet,String title,int width,int height)
   {
       JFreeChart jfreechart = ChartFactory.createPieChart3D(title, dataSet, true, true, false);
       jfreechart.setBackgroundPaint(Color.white);
       jfreechart.setTitle(new TextTitle(title, new Font("宋体", 0, 12)));

       PiePlot3D pie = (PiePlot3D)jfreechart.getPlot();
       pie.setSectionPaint(0, new Color(0, 255, 0));
       pie.setSectionPaint(1, new Color(255, 0, 0));       
       pie.setSectionOutlinePaint(0, Color.gray);
       pie.setBackgroundPaint(Color.white);
       pie.setForegroundAlpha(0.8F);
       pie.setDataAreaRatio(0.10000000000000001D);
       pie.setLabelBackgroundPaint(Color.white);
       pie.setLabelGap(0.001D);
       pie.setInteriorGap(0.10000000000000001D);
       pie.setLegendLabelGenerator(new StandardPieItemLabelGenerator("{0}"));
       pie.setCircular(true);
       pie.setOutlinePaint(Color.white);
       pie.setLabelShadowPaint(Color.white);
       pie.setLabelOutlinePaint(Color.white);
       pie.setLabelLinksVisible(true);
       pie.setLabelGenerator(new StandardPieItemLabelGenerator("{2}", NumberFormat.getNumberInstance(), NumberFormat.getPercentInstance()));
       pie.setDepthFactor(0.16D);
       pie.setStartAngle(290D);
       pie.setDirection(Rotation.CLOCKWISE);
       pie.setForegroundAlpha(0.5F);
       LegendTitle legend = jfreechart.getLegend();
       legend.setItemFont(new Font("Verdena", 0, 9));
       
       return generateKey(jfreechart,width,height);
    }
   
   /**
    * 画重叠柱状图 
    */
   public static String createStackeBarChart(CategoryDataset dataSet,String xLabel,String yLabel,String title,int width,int height)
   {
       JFreeChart jfreechart = ChartFactory.createStackedBarChart3D("", xLabel, yLabel, dataSet, PlotOrientation.VERTICAL, true, true, true);
       jfreechart.setTitle(new TextTitle(title, new Font("宋体", 0, 12)));
       jfreechart.setBackgroundPaint(Color.white);
       CategoryPlot plot = jfreechart.getCategoryPlot();
       CategoryAxis axis = plot.getDomainAxis();
       axis.setLowerMargin(0.10000000000000001D);
       axis.setCategoryMargin(0.02D);
       plot.setForegroundAlpha(0.8F);
       StackedBarRenderer3D barrenderer = new StackedBarRenderer3D();
       barrenderer.setMaxBarWidth(0.070000000000000007D);
       barrenderer.setSeriesPaint(0, new Color(255, 89, 89));
       barrenderer.setSeriesPaint(1, new Color(100, 255, 100));
       barrenderer.setToolTipGenerator(new StandardCategoryToolTipGenerator());
       plot.setRenderer(barrenderer);
       NumberAxis numAxis = new NumberAxis(yLabel);
       numAxis.setNumberFormatOverride(new DecimalFormat("####"));
       plot.setRangeAxis(numAxis);
       CategoryAxis categoryaxis = plot.getDomainAxis();
       categoryaxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(0.39269908169872414D));
       LegendTitle legend = jfreechart.getLegend();
       legend.setItemFont(new Font("Verdena", 0, 9));
       
       return generateKey(jfreechart,width,height);
   }   
   
   /**
    * 画曲线+面积图
    */
   public static String createTimeSeriesChart(TimeSeriesCollection datasetOut,TimeSeriesCollection datasetIn,String xLabel,String yLabel,String title,int width,int height)
   {
	   JFreeChart chart = ChartFactory.createTimeSeriesChart(
               title /*题目*/, xLabel /*X坐标描述*/, yLabel /*Y坐标描述*/,datasetOut,true,false,false);
	   chart.setBackgroundPaint(Color.WHITE);
	   XYPlot xyplot = chart.getXYPlot();
	   xyplot.setBackgroundPaint(Color.WHITE);
	   xyplot.setForegroundAlpha(0.5f);

	   XYLineAndShapeRenderer xylineandshaperenderer = new XYLineAndShapeRenderer();
       
       xylineandshaperenderer.setSeriesFillPaint(0, Color.BLUE); //出口颜色
       xylineandshaperenderer.setSeriesPaint(0, Color.BLUE);
       xylineandshaperenderer.setShapesVisible(true);
       xylineandshaperenderer.setShapesFilled(true);
       xyplot.setRenderer(xylineandshaperenderer);  
       
       XYAreaRenderer xyarearenderer = new XYAreaRenderer();
       xyarearenderer.setSeriesPaint(1, Color.GREEN);   //入口颜色
       xyarearenderer.setSeriesFillPaint(1, Color.GREEN);
       xyarearenderer.setPaint(Color.GREEN);
       xyplot.setDataset(1, datasetIn);
       xyplot.setRenderer(1, xyarearenderer);
	          
	   xyplot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
	   DateAxis dateaxis = (DateAxis) xyplot.getDomainAxis();
	   dateaxis.setTickUnit(new DateTickUnit(DateTickUnit.HOUR,1 /*步长*/));
       dateaxis.setDateFormatOverride(new SimpleDateFormat("HH':00'" /*timeFormat*/));
       dateaxis.setVerticalTickLabels(true);
       
       LegendTitle legend = chart.getLegend();
       legend.setItemFont(new Font("Verdena", 0, 9));
       
	   return generateKey(chart,width,height);
   }
   
	/**
	 * 创建折线图
	 * @param title
	 * @param XCordUnitName
	 * @param YCordUnitName
	 * @param dataset
	 * @param width
	 * @param height
	 * @return
	 */
	public static String createLineChart(String title,String XCordUnitName,String YCordUnitName,DefaultCategoryDataset dataset,int width,int height) {
		JFreeChart chart = ChartFactory.createLineChart(title,// 大标题
			XCordUnitName, // X轴的单位
			YCordUnitName, // Y轴的单位
			dataset, // 数据
			PlotOrientation.VERTICAL, // orientation
			true, // include legend
			true, // tooltips
			false // urls
				);
		chart.setTitle(new TextTitle(title,new Font("宋体", 1, 13)));
		chart.setBackgroundPaint(Color.white);
		CategoryPlot plot = chart.getCategoryPlot();
		plot.setDomainGridlinesVisible(false);//是否设置垂直网格线
		LineAndShapeRenderer render = (LineAndShapeRenderer) plot.getRenderer();
		render.setShapesVisible(false);//设置折线图点的形状是否显示 
		render.setShape(render.getBaseShape());
		CategoryPlot categoryplot = chart.getCategoryPlot();
		CategoryAxis domainAxis = categoryplot.getDomainAxis();
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(0.54));
		domainAxis.setMaximumCategoryLabelWidthRatio(20F);
//		XYPlot xyplot = chart.getXYPlot();
//		xyplot.setBackgroundPaint(Color.white);  
//		xyplot.setAxisOffset(new RectangleInsets(4D, 4D, 4D, 4D));
//		DateAxis dateaxis = (DateAxis) xyplot.getDomainAxis();
//        dateaxis.setVerticalTickLabels(true);	        
//		NumberAxis numAxis = (NumberAxis)xyplot.getRangeAxis();
//		numAxis.setNumberFormatOverride(new DecimalFormat("####.#"));
//        //对各条线进行着色			
//		XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer)xyplot.getRenderer();
//		xylineandshaperenderer.setShapesVisible(true);
//        xylineandshaperenderer.setShapesFilled(true);
        LegendTitle legend = chart.getLegend();
        legend.setItemFont(new Font("Verdena", 0, 9));
        return generateKey(chart,width,height);
	}
	
	/**
	 * 创建柱状图
	 * @param title
	 * @param XCordUnitName
	 * @param YCordUnitName
	 * @param dataset
	 * @param width
	 * @param height
	 * @return
	 */
	public static String createBarChart(String title,String XCordUnitName,String YCordUnitName,DefaultCategoryDataset dataset,int width,int height) {
		JFreeChart chart = ChartFactory.createBarChart3D(title,// 大标题
			XCordUnitName, // X轴的单位
			YCordUnitName, // Y轴的单位
			dataset, // 数据
			PlotOrientation.VERTICAL, true, true, false);
		chart.setTitle(new TextTitle(title,new Font("宋体", 1, 13)));
		chart.setBackgroundPaint(Color.white);
		CategoryPlot plot = chart.getCategoryPlot();
		plot.setDomainGridlinesVisible(false);//是否设置垂直网格线
		CategoryItemRenderer render = plot.getRenderer();
		render.setShape(render.getBaseShape());
		//设置柱子的颜色
		GradientPaint gradientpaint = new GradientPaint(0.0F, 0.0F, new Color(113, 95, 160), 0.0F, 0.0F, new Color(255, 153, 0));
		render.setSeriesPaint(0, gradientpaint);
		CategoryPlot categoryplot = chart.getCategoryPlot();
		CategoryAxis domainAxis = categoryplot.getDomainAxis();
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(0.54));
		domainAxis.setMaximumCategoryLabelWidthRatio(20F);
		LegendTitle legend = chart.getLegend();
		legend.setItemFont(new Font("Verdena", 0, 9));
		return generateKey(chart,width,height);
	}
	
   /**
    * 画曲线
    */
   public static String createLineChart(double[][] data,String[] rowKeys,String[] columnKeys,String xLabel,String yLabel,String title,int width,int height)
   {
	   CategoryDataset dataset = DatasetUtilities.createCategoryDataset(rowKeys,columnKeys,data);
	   JFreeChart chart = ChartFactory.createLineChart(title,xLabel,yLabel,dataset,PlotOrientation.VERTICAL,true, true, true);
       chart.setBackgroundPaint(Color.WHITE);
       CategoryPlot plot = chart.getCategoryPlot();

       CategoryAxis domainAxis = plot.getDomainAxis();
       plot.setDomainAxis(domainAxis);

       ValueAxis rangeAxis = plot.getRangeAxis();
       rangeAxis.setUpperMargin(0.05);  //设置最高的一个 Item 与图片顶端的距离
       rangeAxis.setLowerMargin(0.15);  //设置最低的一个 Item 与图片底端的距离
       plot.setRangeAxis(rangeAxis);
       
	   return generateKey(chart,width,height);
   }
   
   /**
    * 画多曲线图
    */
   /**
 * @param series
 * @param periodType
 * @param xLabel X坐标描述
 * @param yLabel Y坐标描述
 * @param title 题目
 * @param width 宽度
 * @param height 高度
 * @return
 */
public static String createMultiTimeSeriesChart(TimeSeries[] series,String periodType,String xLabel,String yLabel,String title,int width,int height)
   {
	   
	    TimeSeriesCollection timedataset = new TimeSeriesCollection();
	    JFreeChart chart = null;

		if(series != null)
		{
			for (int i = 0; i < series.length; i++)
			{
				timedataset.addSeries(series[i]);
			}
			
			chart = ChartFactory.createTimeSeriesChart(
	               title /*题目*/, xLabel /*X坐标描述*/, yLabel /*Y坐标描述*/,timedataset,true,false,false);
			chart.setTitle(new TextTitle(title,new Font("宋体", 1, 13)));
		  
			chart.setBackgroundPaint(Color.white);
			XYPlot xyplot = chart.getXYPlot();
			xyplot.setBackgroundPaint(Color.white);  
			xyplot.setAxisOffset(new RectangleInsets(4D, 4D, 4D, 4D));
			
		    DateAxis dateaxis = (DateAxis) xyplot.getDomainAxis();
		    //这儿要进行选择 分日、月、年、小时
		    if(periodType.equalsIgnoreCase("MONTH"))
		    {
	            dateaxis.setTickUnit(new DateTickUnit(DateTickUnit.MONTH,1));
	            dateaxis.setDateFormatOverride(new SimpleDateFormat("yy-MM'月'" /*timeFormat*/));
	        } else if(periodType.equalsIgnoreCase("DAY"))
	        {
	            dateaxis.setTickUnit(new DateTickUnit(DateTickUnit.DAY,2));
	            dateaxis.setDateFormatOverride(new SimpleDateFormat("MM-dd'日'"));
	        } else if(periodType.equalsIgnoreCase("HOUR"))
	        {
	            dateaxis.setTickUnit(new DateTickUnit(DateTickUnit.HOUR,1));
	            dateaxis.setDateFormatOverride(new SimpleDateFormat("HH':00'"));
	        } else if(periodType.equalsIgnoreCase("YEAR"))
	        {
	            dateaxis.setTickUnit(new DateTickUnit(DateTickUnit.YEAR,1));
	            dateaxis.setDateFormatOverride(new SimpleDateFormat("yyyy'年'"));
	        }
	        dateaxis.setVerticalTickLabels(true);	        
			NumberAxis numAxis = (NumberAxis)xyplot.getRangeAxis();
			numAxis.setNumberFormatOverride(new DecimalFormat("####.#"));
			
	        //对各条线进行着色			
			XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer)xyplot.getRenderer();
			xylineandshaperenderer.setShapesVisible(true);
	        xylineandshaperenderer.setShapesFilled(true);
	        LegendTitle legend = chart.getLegend();
	        legend.setItemFont(new Font("Verdena", 0, 9));
		}
	    return generateKey(chart,width,height);
   }
   
   private static String generateKey(JFreeChart jfreechart,int width,int height)
   {
	   //先干了一件无关的事....
	   if(ResourceCenter.getInstance().getChartStorage().size()>100)
		  ResourceCenter.getInstance().getChartStorage().clear();
	          
       JFreeChartBrother jfb = new JFreeChartBrother();
       jfb.setChart(jfreechart);
       jfb.setWidth(width);
       jfb.setHeight(height);
       String seriesKey = SysUtil.getLongID();
       ResourceCenter.getInstance().getChartStorage().put(seriesKey,jfb);	    
       return seriesKey;	      
   }
   
   /**
    * Creates a sample dataset.
    *
    * @param count  the item count.
    * 
    * @return the dataset.
    */
   public static XYDataset createForceDataset(Hashtable hash,String title) {
       final TimeSeriesCollection dataset = new TimeSeriesCollection();
       final TimeSeries s1 = new TimeSeries(title, Minute.class);
   	   List list = (List)hash.get("list");
   	
       RegularTimePeriod start = new Minute();
       double force = 3.0;
       if(list != null && list.size()>0){
       	for(int j=0; j<list.size(); j++){
       		try{
           	Vector v = (Vector)list.get(j);
   			Double	d=new Double((String)v.get(0));			
   			String dt = (String)v.get(1);
   			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
   			Date time1 = sdf.parse(dt);				
   			Calendar temp = Calendar.getInstance();
   			temp.setTime(time1);
   			Minute minute=new Minute(temp.get(Calendar.MINUTE),temp.get(Calendar.HOUR_OF_DAY),temp.get(Calendar.DAY_OF_MONTH),temp.get(Calendar.MONTH)+1,temp.get(Calendar.YEAR));
   			s1.add(minute, d);
       		}catch(Exception e){
       			e.printStackTrace();
       		}
           }
           dataset.addSeries(s1);
       }
       return dataset;
   }
   
   /**
    * Creates a sample dataset.
    *
    * @param count  the item count.
    * 
    * @return the dataset.
    */
   public static Vector createMultiDataset(Hashtable hash,String[] bandch,String[] bandch1) {
	   Vector datasetV = new Vector();
	   //=====
	   String unit="";
		String[] keys = (String[])hash.get("key");
		ChartGraph cg = new ChartGraph();
		TimeSeries[] s=new TimeSeries[keys.length];
		try{
			for(int i=0; i<keys.length; i++){
				String key = keys[i];
				TimeSeriesCollection dataset = new TimeSeriesCollection();
			    TimeSeries s1 = new TimeSeries(bandch[i], Minute.class);
			       
				String[] value = (String[])hash.get(key);		
				//流速
				for(int j=0; j<value.length; j++){			
					String val = value[j];
					if (val!=null && val.indexOf("&")>=0){									
						String[] splitstr = val.split("&");
						String splittime = splitstr[0];				
						Double	v=new Double(splitstr[1]);			
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Date da = sdf.parse(splittime);
						Calendar tempCal = Calendar.getInstance();
						tempCal.setTime(da);						
						Minute minute=new Minute(tempCal.get(Calendar.MINUTE),tempCal.get(Calendar.HOUR_OF_DAY),tempCal.get(Calendar.DAY_OF_MONTH),tempCal.get(Calendar.MONTH)+1,tempCal.get(Calendar.YEAR));
						s1.addOrUpdate(minute,v);							
					}
				}
				dataset.addSeries(s1);
				datasetV.add(dataset);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return datasetV;
	   //======
	   
	   
	   
	   
	   
//       final TimeSeriesCollection dataset = new TimeSeriesCollection();
//       final TimeSeries s1 = new TimeSeries(title, Minute.class);
//   	   List list = (List)hash.get("list");
//   	
//       RegularTimePeriod start = new Minute();
//       if(list != null && list.size()>0){
//	       	for(int j=0; j<list.size(); j++){
//	       		try{
//		           	Vector v = (Vector)list.get(j);
//		   			Double	d=new Double((String)v.get(0));			
//		   			String dt = (String)v.get(1);
//		   			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		   			Date time1 = sdf.parse(dt);				
//		   			Calendar temp = Calendar.getInstance();
//		   			temp.setTime(time1);
//		   			Minute minute=new Minute(temp.get(Calendar.MINUTE),temp.get(Calendar.HOUR_OF_DAY),temp.get(Calendar.DAY_OF_MONTH),temp.get(Calendar.MONTH)+1,temp.get(Calendar.YEAR));
//		   			s1.add(minute, d);
//	       		}catch(Exception e){
//	       			e.printStackTrace();
//	       		}
//	        }
//	        dataset.addSeries(s1);
//       }
//       return dataset;
   }
   
   public static String area_p_draw_line(Hashtable hash,String title1,String title2,int w,int h){
   	String seriesKey = SysUtil.getLongID();
   	List list = (List)hash.get("list");
   	try{
	    	if(list==null || list.size()==0){
	    		//draw_blank(title1,title2,w,h);
	    	}
	    	else{
	    		//设置数据
	    		final TimeSeriesCollection dataset = new TimeSeriesCollection();
	            final TimeSeries s1 = new TimeSeries(title1, Minute.class);
	            final JFreeChart chart = ChartFactory.createTimeSeriesChart(
	                title1, 
	                "时间", 
	                title2,
	                dataset,
	                true,
	                true,
	                false
	            );
	            
	            //设置全图背景色为白色
		        chart.setBackgroundPaint(Color.WHITE);
		        //chart.getTitle().setFont(new Font("Verdena", 1, 12));
		           
	            final XYPlot plot = chart.getXYPlot();
	            
	            //设置底色和线
	            plot.getDomainAxis().setLowerMargin(0.0);
	            plot.getDomainAxis().setUpperMargin(0.0);
	            plot.setRangeCrosshairVisible(true);
	            plot.setDomainCrosshairVisible(true);
	            plot.setBackgroundPaint(Color.WHITE);
	     	    plot.setForegroundAlpha(0.8f);
	     	    plot.setRangeGridlinesVisible(true);
	     	    plot.setRangeGridlinePaint(Color.darkGray);
	     	    plot.setDomainGridlinesVisible(true);
	     	    plot.setDomainGridlinePaint(new Color(139,69,19));
	    		
	     	   
	           
	           // 设置显示的数据坐标值范围...
	           final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
	           rangeAxis.setAutoRangeIncludesZero(true);
	           rangeAxis.setRange(0,100);
	           plot.setRangeAxis(rangeAxis);
	           
	           //设置显示的颜色
	           XYAreaRenderer xyarearenderer = new XYAreaRenderer();
	           xyarearenderer.setSeriesPaint(0, new Color(135,206,250));
	           xyarearenderer.setSeriesFillPaint(0, new Color(135,206,250));
	           xyarearenderer.setPaint(new Color(135,206,250));
	           plot.setRenderer(0, xyarearenderer);
	           
	           //设置显示的数据
	           plot.mapDatasetToRangeAxis(0, 0);
	           plot.setDataset(0, createForceDataset(hash,title1));
	           
	           //开始画图
	           LegendTitle legend = chart.getLegend();
	           //chart.setBorderPaint(Color.RED);
	           legend.setItemFont(new Font("Verdena", 0, 9));
	           legend.setBackgroundPaint(Color.WHITE);
	           JFreeChartBrother jfb = new JFreeChartBrother();
	           jfb.setChart(chart);
	           jfb.setWidth(w);
	           jfb.setHeight(h);
	           
	           ResourceCenter.getInstance().getChartStorage().put(seriesKey,jfb);
	    	}
	    	hash = null;
	    }catch(Exception e){
   		e.printStackTrace();
   	}
	    return seriesKey;
   }
}