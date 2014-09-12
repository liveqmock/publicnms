package com.afunms.common.util;

import ChartDirector.BarLayer;
import ChartDirector.Chart;
import ChartDirector.LegendBox;
import ChartDirector.XYChart;

public class CreateBarPic {

	/**
	 * @author lgw
	 * @date Feb 28, 2011 1:30:27 PM
	 * @param args void
	 * @Description: TODO(描述这个方法的作用) 
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		CreateBarPic cbp = new CreateBarPic();
//		double[] data1 = {85, 156, 179, 211, 123,50,70};
//		 double[] data2 = {97, 87, 56, 267, 157,40,120};
//		 String[] labels = {"中国", "韩国", "朝鲜", "新加坡", "日本","俄国","德国"};
//		 TitleModel tm = new TitleModel();
//		 tm.setBgcolor(0xffffff);
//		 tm.setXpic(280);
//		 tm.setYpic(200);
//		 tm.setX1(30);
//		 tm.setX2(15);
//		 tm.setX3(230);
//		 tm.setX4(140);
//		 tm.setX5(90);
//		 tm.setX6(170);
//		 int color1 = 0x80ff80;
//		 int color2 = 0x8080ff;
//		cbp.createCylindricalPic(data1,data2,labels,tm,"中国","日本",color1,color2);
		CreateBarPic cbp = new CreateBarPic();

		 TitleModel tm = new TitleModel();
		 tm.setBgcolor(0xffffff);
		 tm.setXpic(280);//图片宽度
		 tm.setYpic(200);//图片高度
		 tm.setX1(35);//图左边距离
		 tm.setX2(25);//图上边距离
		 tm.setX3(230);//内图宽度
		 tm.setX4(140);//内图高度
		 tm.setX5(20);//调整图例与左边之间的距离
		 tm.setX6(170);//调整图例与顶部之间的距离
		 int barwidth = 60;//图片中柱子的宽度
		 int color1 = 0x80ff80;
		 int color2 = 0x8080ff;
		double[] data0 = {2000,3000,4000};//数据
		double[] data1 = {5000,6000,7000};
		String[] dataName = {"现在流量","平均流量","最高流量"};//每种数据对应的名称
		String[] labels = {"进口","出口"};//数据的种类
		int[] color = {0x80ff80,0x8080ff,0xffffff};//每种数据对应的颜色
		cbp.createCompareThreeBarPic(data0,data1,dataName,labels,color,tm,barwidth);
	}
	/**
	 * 
	 * @author lgw
	 * @date Feb 28, 2011 4:28:50 PM
	 * @param data1
	 * @param data2
	 * @param labels
	 * @param tm
	 * @param a1
	 * @param a2
	 * @param color1
	 * @param color2
	 * @return String
	 * @Description: TODO(生成柱状图)
	 */
//	public String createCylindricalPic(double[] data1,double[] data2,String[] labels,TitleModel tm,String a1,String a2,int color1,int color2)
//	{
//		Chart.setLicenseCode(CommonMethod.keycode);
//		if(data1.length!=0 && data2.length!=0 && labels.length!=0 )
//		{
//			XYChart c = new XYChart(tm.getXpic(), tm.getYpic());
//			c.setBackground(tm.getBgcolor());
//			c.setPlotArea(tm.getX1(), tm.getX2(), tm.getX3(), tm.getX4(), 0xf8f8f8, 0xffffff,c.dashLineColor(0xcccccc, Chart.DotLine),c.dashLineColor(0xcccccc, Chart.DotLine));
//			 c.addLegend(tm.getX5(), tm.getX6(), false, "宋体", 9).setBackground(Chart.Transparent);
//			c.addTitle(tm.getTopTitle(), "宋体", 14);
//			c.yAxis().setTitle(tm.getLeftTitle()).setFontStyle("宋体");
//			c.xAxis().setLabelStyle("宋体");
//			c.xAxis().setLabels(labels);
//			//c.xAxis().setLabels(labels).setFontAngle(45);	
//			c.setTransparentColor(0xff8080);		
//			BarLayer layer = c.addBarLayer2(Chart.Stack, 8);
//			layer.addDataSet(data1, color1, a1);
//			layer.addDataSet(data2, color2, a2);
//	        layer.setDataLabelStyle("宋体", 8);
//	        layer.setBarShape(Chart.CircleShape);
//	        String picname = CommonMethod.getPicName()+".png";
//			String str = CommonMethod.checkFile()+"/"+picname;
//			c.makeChart(str);
//			System.out.println("生成图片路径：  "+str);
//			return picname;
//		}else{
//			return null;
//		}
//		
//	}
	public void createResponseTimePic(String ip,String responsevalue,String maxresponse,String avgresponse){
		double[] r_data1 = {new Double(responsevalue), new Double(maxresponse),new Double(avgresponse)};
		 String[] r_labels = {"当前响应时间(ms)", "最大响应时间(ms)","平均响应时间(ms)"};		 
		 TitleModel tm = new TitleModel();
		 tm.setPicName(ip+"response");//
		 tm.setBgcolor(0xffffff);
		 tm.setXpic(450);//图片长度
		 tm.setYpic(180);//图片高度
		 tm.setX1(30);//左面距离
		 tm.setX2(20);//上面距离
		 tm.setX3(400);//内图宽度
		 tm.setX4(130);//内图高度
		 tm.setX5(10);
		 tm.setX6(115);
		 createTimeBarPic(r_data1,r_labels,tm,40);
		 
	}
	public String createCylindricalPic(double[] data0,double[] data1,String[] labels,TitleModel tm,String a1,String a2,int color1,int color2)
	{
		Chart.setLicenseCode(CommonMethod.keycode);
 
		 XYChart c = new XYChart(tm.getXpic(), tm.getYpic(), 0xffffff, 0, 1);
		 c.setPlotArea(tm.getX1(), tm.getX2(), tm.getX3(), tm.getX4(), 0xf8f8f8, 0xffffff);
	        //c.setPlotArea(tm.getX1(), tm.getX2(), tm.getX3(), tm.getX4(), 0xffffff);
	        LegendBox legendBox = c.addLegend(tm.getX5(), tm.getX6(), false, "宋体", 8);
	        legendBox.setBackground(Chart.Transparent, Chart.Transparent);
	        
        //XYChart c = new XYChart(tm.getXpic(), tm.getYpic(), 0xffffcc, 0, 1);
		//c.setPlotArea(tm.getX1(), tm.getX2(), tm.getX3(), tm.getX4(), c.linearGradientColor(60, 40, 60, 280, 0xffffff,0xffffff), -1, 0xffffff, 0xffffff);
		//c.addLegend(tm.getX5(), tm.getX6(), false, "宋体", 9).setBackground(Chart.Transparent);
		c.xAxis().setColors(0x000000);
        c.xAxis().setLabels(labels);
        c.setBackground(0xffffff);
        c.xAxis().setLabelStyle("宋体");
        
        
        //c.yAxis().setColors(0x000000);
        //c.setLabelFormat("{percent|0}%");
        
        BarLayer layer = c.addBarLayer2(Chart.Percentage);
        layer.addDataSet(data0, color1, a1);
		layer.addDataSet(data1, color2, a2);
        layer.setBorderColor(Chart.Transparent);
        layer.setDataLabelStyle().setAlignment(Chart.Center);
        layer.setDataLabelStyle("宋体", 8);
        layer.setLegend(Chart.ReverseLegend);
        layer.setBarShape(Chart.CircleShape);
        layer.setDataLabelFormat("{percent|0}%");
        //layer.setAggregateLabelStyle("Times New Roman Bold Italic", 10, 0x663300);
        //layer.setBarWidth(50)
        layer.set3D(15);
        String picname = tm.getPicName()+".png";
		String str = CommonMethod.checkFile()+"/"+picname;
		c.makeChart(str);
        return picname;
	}
	
	public String createBarPic(double[] data0,double[] data1,String[] labels,TitleModel tm,String a1,String a2,int color1,int color2)
	{
		Chart.setLicenseCode(CommonMethod.keycode);
 
        XYChart c = new XYChart(tm.getXpic(), tm.getYpic(), 0xffffcc, 0, 1);
		//c.setPlotArea(tm.getX1(), tm.getX2(), tm.getX3(), tm.getX4(), 0xffffff, 0xffffff);
		c.setPlotArea(tm.getX1(), tm.getX2(), tm.getX3(), tm.getX4(), c.linearGradientColor(60, 40, 60, 280, 0xffffff,0xffffff), -1, 0xffffff, 0xffffff);
		c.addLegend(tm.getX5(), tm.getX6(), false, "宋体", 9).setBackground(Chart.Transparent);
		c.xAxis().setColors(0x000000);
        c.xAxis().setLabels(labels);
        c.setBackground(0xffffff);
        //c.xAxis().setLabelStyle("宋体");
        //c.yAxis().setColors(0x000000);
        //c.setLabelFormat("{percent|0}%");
        
        c.xAxis().setLabelStyle("宋体");
        c.xAxis().setLabels(labels).setFontAngle(10);
        BarLayer layer = c.addBarLayer2(Chart.Percentage);
        layer.addDataSet(data0, color1, a1);
		layer.addDataSet(data1, color2, a2);
        layer.setBorderColor(Chart.Transparent);
        layer.setDataLabelStyle().setAlignment(Chart.Center);
        layer.setLegend(Chart.ReverseLegend);
        layer.setBarShape(Chart.CircleShape);
        //layer.setBarShape(Chart.CircleShape);
        layer.setDataLabelFormat("{percent|0}%");
        layer.set3D(10);
        String picname = tm.getPicName()+".png";
		String str = CommonMethod.checkFile()+"/"+picname;
		c.makeChart(str);
        return picname;
	}
	
	public String createCylindricalPicc(double[] data0,double[] data1,double[] data2,double[] data3,String[] labels,TitleModel tm,String a1,String a2,String a3,String a4,int color1,int color2,int color3,int color4)
	{
		Chart.setLicenseCode(CommonMethod.keycode);
 
        XYChart c = new XYChart(tm.getXpic(), tm.getYpic(), 0xffffcc, 0, 1);
		//c.setPlotArea(tm.getX1(), tm.getX2(), tm.getX3(), tm.getX4(), 0xffffff, 0xffffff);
		c.setPlotArea(tm.getX1(), tm.getX2(), tm.getX3(), tm.getX4(), c.linearGradientColor(60, 40, 60, 280, 0xffffff,0xffffff), -1, 0xffffff, 0xffffff);
		c.addLegend(tm.getX5(), tm.getX6(), false, "宋体", 9).setBackground(Chart.Transparent);
		c.xAxis().setColors(0x000000);
        c.xAxis().setLabels(labels);
        c.setBackground(0xffffff);
        c.xAxis().setLabelStyle("宋体");
        c.yAxis().setColors(0x000000);
        
        BarLayer layer = c.addBarLayer2(Chart.Percentage);
        layer.addDataSet(data0, color1, a1);
		layer.addDataSet(data1, color2, a2);
		layer.addDataSet(data2, color3, a3);
		layer.addDataSet(data3, color4, a4);
        layer.setBorderColor(Chart.Transparent);
        layer.setDataLabelStyle().setAlignment(Chart.Center);
        layer.setDataLabelStyle("宋体", 8);
        layer.setLegend(Chart.ReverseLegend);
        layer.setBarShape(Chart.CircleShape);
        layer.setDataLabelFormat("{percent|0}%");
        layer.setBarWidth(60);
        layer.set3D(10);
        String picname = tm.getPicName()+".png";
		String str = CommonMethod.checkFile()+"/"+picname;
		c.makeChart(str);
        return picname;
	}
	public String createCylindricalPiccc(double[] data0,double[] data1,double[] data2,double[] data3,double[] data4,double[] data5,String[] labels,TitleModel tm,String a1,String a2,String a3,String a4,String a5,String a6,int color1,int color2,int color3,int color4,int color5,int color6)
	{
		Chart.setLicenseCode(CommonMethod.keycode);
		
		XYChart c = new XYChart(tm.getXpic(), tm.getYpic(), 0xffffcc, 0, 1);
		//c.setPlotArea(tm.getX1(), tm.getX2(), tm.getX3(), tm.getX4(), 0xffffff, 0xffffff);
		c.setPlotArea(tm.getX1(), tm.getX2(), tm.getX3(), tm.getX4(), c.linearGradientColor(60, 40, 60, 280, 0xffffff,0xffffff), -1, 0xffffff, 0xffffff);
		c.addLegend(tm.getX5(), tm.getX6(), false, "宋体", 9).setBackground(Chart.Transparent);
		c.xAxis().setColors(0x000000);
		c.xAxis().setLabels(labels);
		c.setBackground(0xffffff);
		c.xAxis().setLabelStyle("宋体");
		c.yAxis().setColors(0x000000);
		
		BarLayer layer = c.addBarLayer2(Chart.Percentage);
		layer.addDataSet(data0, color1, a1);
		layer.addDataSet(data1, color2, a2);
		layer.addDataSet(data2, color3, a3);
		layer.addDataSet(data3, color4, a4);
		layer.addDataSet(data4, color5, a5);
		layer.addDataSet(data5, color6, a6);
		layer.setBorderColor(Chart.Transparent);
		layer.setDataLabelStyle().setAlignment(Chart.Center);
		layer.setDataLabelStyle("宋体", 8);
		layer.setLegend(Chart.ReverseLegend);
		layer.setBarShape(Chart.CircleShape);
		layer.setDataLabelFormat("{percent|2}%");
		layer.setBarWidth(60);
		layer.set3D(10);
		String picname = tm.getPicName()+".png";
		String str = CommonMethod.checkFile()+"/"+picname;
		c.makeChart(str);
		return picname;
	}
	
	/**
	 * 
	 * @author lgw
	 * @date Mar 8, 2011 11:01:56 AM
	 * @param data
	 * @param labels
	 * @param tm
	 * @param barwidth
	 * @return String
	 * @Description: TODO(用来做时间显示的应用)
	 */
	public String createTimeBarPic(double[] data,String[] labels,TitleModel tm,int barwidth)
	{
		Chart.setLicenseCode(CommonMethod.keycode);
        XYChart c = new XYChart(tm.getXpic(), tm.getYpic());
        c.setPlotArea(tm.getX1(), tm.getX2(), tm.getX3(), tm.getX4(), 0xf8f8f8, 0xffffff);
        BarLayer layer = c.addBarLayer3(data);
        layer.set3D(10);
        c.xAxis().setLabelStyle("宋体");

        layer.setBarShape(Chart.CircleShape);
        layer.setBarWidth(barwidth);
        layer.setAggregateLabelStyle("Times New Roman Bold Italic", 10, 0x663300);
        c.xAxis().setLabels(labels);
        c.xAxis().setTitle(tm.getTopTitle(),"宋体");
        String picname = tm.getPicName()+".png";
		String str = CommonMethod.checkFile()+"/"+picname;
		c.makeChart(str);
        return picname;
	}
	
	/**
	 * 
	 * @author lgw
	 * @date Mar 8, 2011 11:01:56 AM
	 * @param data
	 * @param labels
	 * @param tm
	 * @param barwidth
	 * @return String
	 * @Description: TODO(用来做时间显示的应用)
	 */
	public String createRoundTimeBarPic(double[] data,String[] labels,TitleModel tm,int barwidth)
	{
		Chart.setLicenseCode(CommonMethod.keycode);
        XYChart c = new XYChart(tm.getXpic(), tm.getYpic());
        c.setPlotArea(tm.getX1(), tm.getX2(), tm.getX3(), tm.getX4(), 0xf8f8f8, 0xffffff);
        BarLayer layer = c.addBarLayer3(data);
        layer.set3D(10);
        c.xAxis().setLabelStyle("宋体");

        layer.setBarShape(Chart.DiamondShape);
        layer.setBarWidth(barwidth);
        //layer.setBarShape(10);
        layer.setAggregateLabelStyle("Times New Roman Bold Italic", 10, 0x663300);
        c.xAxis().setLabels(labels);
        c.xAxis().setTitle(tm.getTopTitle(),"宋体");
        String picname = tm.getPicName()+".png";
		String str = CommonMethod.checkFile()+"/"+picname;
		c.makeChart(str);
        return picname;
	}
	
	/**
	 * 
	 * @author lgw
	 * @date Mar 9, 2011 9:17:19 AM
	 * @param data0 数据一
	 * @param data1 数据二
	 * @param labels  x轴坐标对应的名称
	 * @param tm 
	 * @param a1 
	 * @param a2
	 * @param color1 a1 对应的颜色
	 * @param color2 a2 对应的颜色
	 * @param barwidth  柱子的宽度
	 * @param angle x轴字体的角度
	 * @return String
	 * @Description: TODO(普通的3D柱图)
	 */
	public String createNormalBarPic(double[] data0,double[] data1,String[] labels,TitleModel tm,String a1,String a2,int color1,int color2,int barwidth,int angle)
	{
		    Chart.setLicenseCode(CommonMethod.keycode);
	        XYChart c = new XYChart(tm.getXpic(), tm.getYpic(), 0xffffff, 0, 1);
	        c.setPlotArea(tm.getX1(), tm.getX2(), tm.getX3(), tm.getX4(), 0xffffff);
	        LegendBox legendBox = c.addLegend(tm.getX5(), tm.getX6(), false, "宋体", 8);
	        legendBox.setBackground(Chart.Transparent, Chart.Transparent);
	        //legendBox.setKeySize(16, 32);
	        
	        c.xAxis().setLabelStyle("宋体");
	        c.xAxis().setLabels(labels).setFontAngle(angle);
	        BarLayer layer = c.addBarLayer2(Chart.Percentage);
	        layer.addDataSet(data0, color1, a1);
			layer.addDataSet(data1, color2, a2);
	        layer.setBorderColor(Chart.Transparent);
	        layer.setDataLabelStyle().setAlignment(Chart.Center);
	        layer.setLegend(Chart.ReverseLegend);
	        layer.setBarShape(Chart.CircleShape);
	        layer.setDataLabelFormat("{percent|0}%");
	        layer.set3D(barwidth);
	        layer.setBarWidth(barwidth);
	        String picname = tm.getPicName()+".png";
			String str = CommonMethod.checkFile()+"/"+picname;
			c.makeChart(str);

		return picname;
	}
	/**
	 * 
	 * @author lgw
	 * @date Mar 15, 2011 6:18:57 PM
	 * @param data0
	 * @param data1
	 * @param dataName
	 * @param labels
	 * @param color
	 * @param tm
	 * @param barwidth
	 * @return String
	 * @Description: TODO(三个柱子比较的图)
	 */
	public String createCompareThreeBarPic(double[] data0, double[] data1,String[] dataName,String[] labels,int[] color,TitleModel tm,int barwidth)
	{
			Chart.setLicenseCode(CommonMethod.keycode);
			double[] data2 = {data0[0],data1[0]};
			double[] data3 = {data0[1],data1[1]};
			double[] data4 = {data0[2],data1[2]};
			double[] data5 = {data0[3],data1[3]};
	        XYChart c = new XYChart(tm.getXpic(), tm.getYpic());
	       	c.setPlotArea(tm.getX1(), tm.getX2(), tm.getX3(), tm.getX4(), 0xf8f8f8, 0xffffff);
	        c.addLegend(tm.getX5(), tm.getX6(), false,"宋体",9).setBackground(Chart.Transparent);
	        c.xAxis().setLabels(labels);
	        c.xAxis().setTickOffset(0.5);
	        c.xAxis().setLabelStyle("宋体");
	        BarLayer layer = c.addBarLayer2(Chart.Side, 9);
	        layer.addDataSet(data2, color[0], dataName[0]);
	        layer.addDataSet(data3, color[1], dataName[1]);
	        layer.addDataSet(data4, color[2], dataName[2]);
	        layer.addDataSet(data5, color[3], dataName[3]);
	        layer.setBarShape(Chart.NoShape, 0);
	        layer.setBarShape(Chart.NoShape, 1);
	        layer.setBarShape(Chart.NoShape, 2);
	        layer.setBarShape(Chart.NoShape, 3);
	        layer.setBarWidth(barwidth);
	        layer.setDataLabelStyle();
	        String picname = tm.getPicName()+".png";
			String str = CommonMethod.checkFile()+"/"+picname;
			c.makeChart(str);
			return picname;

	}
}
