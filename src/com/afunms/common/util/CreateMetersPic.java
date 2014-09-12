package com.afunms.common.util;

import java.util.ArrayList;
import java.util.List;
import com.afunms.initialize.ResourceCenter;
import ChartDirector.AngularMeter;
import ChartDirector.Chart;

public class CreateMetersPic {

	/**
	 * @author lgw
	 * @date Feb 28, 2011 5:09:48 PM
	 * @param args
	 *            void
	 * @Description: TODO(描述这个方法的作用)
	 */
	public static void main(String[] args) {
		try{
		CreateMetersPic c=new CreateMetersPic();
		
		c.createPic("127001", 50.0, "D:/apache-tomcat-afunms/webapps/afunms/resource/image/jfreechart",
				"内存利用率","avgpmemory") ;
		System.out.println("画图完毕！" );
		}catch(Exception ex){
			System.out.println(""+ex.getMessage().toString());
			
		} 
	} 

	int [] x=Chart.blueMetalGradient ;
	/**
	 * 指针内部颜色
	 */
	int pointFillColor=0x80cccccc; 
	/**
	 * 指针边框颜色
	 */
	int pointBorderColor=0x26261a; 
	 
	int innerAreaColor1=0xFF9900;
	int innerAreaColor2=0xffff00;
	int innerAreaColor3=0x234793;
	
	
	
	public void createChartByParam(String ip, String value,String bgImagePath,String title,String type)
    {
		Chart.setLicenseCode(CommonMethod.keycode); 
        AngularMeter m = new AngularMeter(150, 150 , -2); 
        m.setDefaultFonts("Times New Roman","Bold");  //设置了Bold 但是显示却不是粗体，？？ 
		//m.setDefaultFonts("simsun.ttc"); 
        m.setColors(x);//仪表盘颜色样式 
        m.setBackground(0xffffff); //设置背景颜色
        m.setMeter(79, 79, 60, -135, 135);//设置仪表盘中心为80，宽度为60  
        m.setScale(0, 100, 10, 5, 0);//设置刻度为0-100；10格拥有一个最大刻度标志，5格一个小刻度刻度标志，最小刻度为1  
        m.setLineWidth(0, 2, 1); //参数意义为 环线的宽度,  最大刻度的宽度，小刻度的宽度  
//      m.addRing(0, 40, Chart.metalColor(0xfff0ff));// 可以设置多个环 后添加环覆盖前面的环 
//      m.addRing(2, 40, 0x6666FF); 
        //TODO
        //区域划分  
        m.addZone(0, 20, innerAreaColor1);
        m.addZone(20, 40, innerAreaColor2); 
        m.addZone(40, 100, innerAreaColor3);//区域划分：从 0-60度，背景颜色为： 
        double valueD=0;
        try{
        	valueD=Double.valueOf(value);
        }catch (Exception e){
        	e.printStackTrace();
        }
        //TODO
      
        m.addText(79, 122,  title, "宋体", 10, Chart.TextColor, Chart.Center);  //添加文本  
        m.addText(79, 105, m.formatValue(valueD, "2"), "Arial", 8, 0x000000, Chart.Center).setBackground(0xffffff, 0xffffff, -1);//添加文本 
 
        //TODO 
       // setShape  可以设置值 [ 0,1 ,2 ,3 ,4]
        m.addPointer(valueD, pointFillColor,pointBorderColor ).setShape(Chart.ArrowPointer2);  //添加指针颜色和形状  
      
        if (bgImagePath == "") {
			m.setBgImage(ResourceCenter.getInstance().getSysPath()+ "resource/image/dashboard.png");
		} else {
			m.setBgImage(bgImagePath);
		} 
        String picname =ip+type+ ".png";
		String str = CommonMethod.checkFile() + "/" + picname;
		m.makeChart(str);  
    }
	/** 
	 * @param ip//IP
	 * @param value//仪表盘值
	 * @param bgImagePath //背景图片路径
	 * @param title 仪表盘标题
	 * @param type (生成文件名称的组成部分：前为[IP+type]例如（127.0.0.1cup）
	 * @Description: TODO(绘制简单的仪表盘图)
	 */
	public void createPic(String ip, Double value, String bgImagePath,
			String title, String type) {
		MeterModel mm = new MeterModel();
		mm.setBgColor(0xffffff);
		mm.setInnerRoundColor(0xececec);
		mm.setOutRingColor(0x80ff80);
		mm.setTitle(title);
		mm.setPicx(150);//
		mm.setPicy(150);//
		mm.setMeterX(80);//
		mm.setMeterY(80);//
		mm.setPicName(ip + type);//
		mm.setValue(value);//
		mm.setMeterSize(60);// 设置仪表盘大小
		mm.setTitleY(79);// 设置标题离左边距离
		mm.setTitleTop(122);// 设置标题离顶部距离
		mm.setValueY(78);// 设置值离左边距离
		mm.setValueTop(105);// 设置值离顶部距离 
		mm.setOutPointerColor(pointBorderColor);// 设置指针外部颜色
		mm.setInPointerColor(pointFillColor);// 设置指针内部颜色
		mm.setFontSize(10);// 设置字体大小
		List<StageColor> sm = new ArrayList<StageColor>();
		StageColor sc1 = new StageColor();
		sc1.setColor(innerAreaColor3);
		sc1.setStart(0);
		sc1.setEnd(60);
		StageColor sc2 = new StageColor();
		sc2.setColor(innerAreaColor2);
		sc2.setStart(60);
		sc2.setEnd(80);
		StageColor sc3 = new StageColor();
		sc3.setColor(innerAreaColor1);
		sc3.setStart(80);
		sc3.setEnd(100);
		sm.add(sc1);
		sm.add(sc2);
		sm.add(sc3);
		mm.setList(sm);
		this.createSimpleMeter(mm,bgImagePath);

	}  
	/**
	 *  
	 * @param mm
	 * @param bgImagePath
	 */
	public void createSimpleMeter(MeterModel mm, String bgImagePath) {
		Chart.setLicenseCode(CommonMethod.keycode);
		if (!mm.equals("")) {
			AngularMeter m = new AngularMeter(mm.getPicx(), mm.getPicy(),
					0x80ff80, 0xffffff, -2); 
			m.setDefaultFonts("Times New Roman","Bold"); 

	        m.setColors(x);//仪表盘颜色样式 
			m.setBackground(mm.getBgColor());
			m.setMeter(mm.getMeterX(), mm.getMeterY(), mm.getMeterSize(), -135,
					135);
			m.setScale(0, 100, 10, 5, 0);
			m.setLineWidth(0, 2, 1);
			if (bgImagePath == "") {
				m.setBgImage(ResourceCenter.getInstance().getSysPath()
						+ "resource/image/dashboard.png");
			} else {
				m.setBgImage(bgImagePath);
			}
			// m.addRing(0, 90,
			// Chart.metalColor(mm.getInnerRoundColor()));//内圆颜色
			// m.addRing(88, 90, mm.getOutRingColor());//外圈颜色
			if (!mm.getList().isEmpty()) {
				for (int i = 0; i < mm.getList().size(); i++) {
					m.addZone(mm.getList().get(i).getStart(), mm.getList().get(i).getEnd(), mm.getList().get(i).getColor());
				}
			}
			m.addText(mm.getTitleY(), mm.getTitleTop(), mm.getTitle(), "宋体", mm.getFontSize(), 0x000000, Chart.Center);
			m.addPointer(mm.getValue(), mm.getInPointerColor(),mm.getOutPointerColor()).setShape(Chart.ArrowPointer2);
			m.addText(mm.getValueY(), mm.getValueTop(),m.formatValue(mm.getValue(), "2"), "宋体", mm.getFontSize(),0x000000, Chart.Center).setBackground(0xffffff, 0xffffff,-1);

			String picname = mm.getPicName() + ".png";
			String str = CommonMethod.checkFile() + "/" + picname;
			m.makeChart(str); 
		}
	}
	/**
	 * 
	 * @author wxy
	 * @date Mar 12, 2011 9:26:05 AM
	 * @param mm
	 * @return String
	 * @Description: TODO(绘制简单的仪表盘图-CPU利用率)
	 */
	public void createCpuPic(String ip, int cpuper) {
		// CreateMetersPic cmp = new CreateMetersPic();
		MeterModel mm = new MeterModel();
		mm.setBgColor(0xffffff);
		mm.setInnerRoundColor(0xececec);
		mm.setOutRingColor(0x80ff80);
		mm.setTitle("CPU利用率");
		mm.setPicx(150);//
		mm.setPicy(150);//
		mm.setMeterX(80);//
		mm.setMeterY(80);//
		mm.setPicName(ip + "cpu");//
		mm.setValue(cpuper);//
		mm.setMeterSize(60);// 设置仪表盘大小
		mm.setTitleY(79);// 设置标题离左边距离
		mm.setTitleTop(122);// 设置标题离顶部距离
		mm.setValueY(78);// 设置值离左边距离
		mm.setValueTop(105);// 设置值离顶部距离 
		mm.setOutPointerColor(pointBorderColor);// 设置指针外部颜色
		mm.setInPointerColor(pointFillColor);// 设置指针内部颜色
		mm.setFontSize(10);// 设置字体大小
		List<StageColor> sm = new ArrayList<StageColor>();
		StageColor sc1 = new StageColor();
		sc1.setColor(innerAreaColor3);
		sc1.setStart(0);
		sc1.setEnd(60);
		StageColor sc2 = new StageColor();
		sc2.setColor(innerAreaColor2);
		sc2.setStart(60);
		sc2.setEnd(80);
		StageColor sc3 = new StageColor();
		sc3.setColor(innerAreaColor1);
		sc3.setStart(80);
		sc3.setEnd(100);
		sm.add(sc1);
		sm.add(sc2);
		sm.add(sc3);
		mm.setList(sm);
		this.createSimpleMeter(mm);
	}

	/**
	 * 
	 * @author wxy
	 * @date Mar 12, 2011 9:26:05 AM
	 * @param mm
	 * @return String
	 * @Description: TODO(绘制简单的仪表盘图-CPU最大利用率)
	 */
	public void createMaxCpuPic(String ip, String cpumax) {

		MeterModel mm = new MeterModel();
		mm = new MeterModel();
		mm.setBgColor(0xffffff);
		mm.setInnerRoundColor(0xececec);
		mm.setOutRingColor(0x80ff80);
		mm.setTitle("CPU利用率");
		mm.setPicx(150);//
		mm.setPicy(150);//
		mm.setMeterX(80);//
		mm.setMeterY(80);//
		mm.setPicName(ip + "cpumax");//
		mm.setValue(new Double(cpumax.replaceAll("%", "")));//
		mm.setMeterSize(60);// 设置仪表盘大小
		mm.setTitleY(79);// 设置标题离左边距离
		mm.setTitleTop(122);// 设置标题离顶部距离
		mm.setValueY(78);// 设置值离左边距离
		mm.setValueTop(105);// 设置值离顶部距离 
		mm.setOutPointerColor(pointBorderColor);// 设置指针外部颜色
		mm.setInPointerColor(pointFillColor);// 设置指针内部颜色
		mm.setFontSize(10);// 设置字体大小
		List<StageColor> sm = new ArrayList<StageColor>();
		StageColor sc1 = new StageColor();
		sc1.setColor(innerAreaColor3);
		sc1.setStart(0);
		sc1.setEnd(60);
		StageColor sc2 = new StageColor();
		sc2.setColor(innerAreaColor2);
		sc2.setStart(60);
		sc2.setEnd(80);
		StageColor sc3 = new StageColor();
		sc3.setColor(innerAreaColor1);
		sc3.setStart(80);
		sc3.setEnd(100);
		sm.add(sc1);
		sm.add(sc2);
		sm.add(sc3);
		mm.setList(sm);
		createSimpleMeter(mm);
	}


	/**
	 * 
	 * @author wxy
	 * @date Mar 12, 2011 9:26:05 AM
	 * @param mm
	 * @return String
	 * @Description: TODO(绘制简单的仪表盘图-CPU平均利用率)
	 */
	public void createAvgCpuPic(String ip, String avgcpu) {
		MeterModel mm = new MeterModel();
		mm = new MeterModel();
		mm.setBgColor(0xffffff);
		mm.setInnerRoundColor(0xececec);
		mm.setOutRingColor(0x80ff80);
		mm.setTitle("CPU利用率");
		mm.setPicx(150);//
		mm.setPicy(150);//
		mm.setMeterX(80);//
		mm.setMeterY(80);//
		mm.setPicName(ip + "cpuavg");//
		mm.setValue(new Double(avgcpu.replaceAll("%", "")));//
		mm.setMeterSize(60);// 设置仪表盘大小
		mm.setTitleY(79);// 设置标题离左边距离
		mm.setTitleTop(122);// 设置标题离顶部距离
		mm.setValueY(78);// 设置值离左边距离
		mm.setValueTop(105);// 设置值离顶部距离
		mm.setOutPointerColor(pointBorderColor);// 设置指针外部颜色
		mm.setInPointerColor(pointFillColor);// 设置指针内部颜色
		mm.setFontSize(10);// 设置字体大小
		List<StageColor> sm = new ArrayList<StageColor>();
		StageColor sc1 = new StageColor();
		sc1.setColor(innerAreaColor3);
		sc1.setStart(0);
		sc1.setEnd(60);
		StageColor sc2 = new StageColor();
		sc2.setColor(innerAreaColor2);
		sc2.setStart(60);
		sc2.setEnd(80);
		StageColor sc3 = new StageColor();
		sc3.setColor(innerAreaColor1);
		sc3.setStart(80);
		sc3.setEnd(100);
		sm.add(sc1);
		sm.add(sc2);
		sm.add(sc3);
		mm.setList(sm);
		createSimpleMeter(mm);
	}

	/**
	 * 
	 * @author lgw
	 * @date Mar 1, 2011 9:26:05 AM
	 * @param mm
	 * @return String
	 * @Description: TODO(绘制简单的仪表盘图)
	 */
	public String createSimpleMeter(MeterModel mm) {
		Chart.setLicenseCode(CommonMethod.keycode);
		if (!mm.equals("")) {
			AngularMeter m = new AngularMeter(mm.getPicx(), mm.getPicy(),
					0x80ff80, 0xffffff, -2);
			m.setDefaultFonts("Times New Roman","Bold");   
	        m.setColors(x);//仪表盘颜色样式 
			m.setBackground(mm.getBgColor());
			m.setMeter(mm.getMeterX(), mm.getMeterY(), mm.getMeterSize(), -135,
					135);
			m.setScale(0, 100, 10, 5, 0);
			m.setLineWidth(0, 2, 1);
			m.setBgImage(ResourceCenter.getInstance().getSysPath()
					+ "resource/image/dashboard.png");
			// m.addRing(0, 90,
			// Chart.metalColor(mm.getInnerRoundColor()));//内圆颜色
			// m.addRing(88, 90, mm.getOutRingColor());//外圈颜色
			if (!mm.getList().isEmpty()) {
				for (int i = 0; i < mm.getList().size(); i++) {
					m.addZone(mm.getList().get(i).getStart(), mm.getList().get(
							i).getEnd(), mm.getList().get(i).getColor());
				}
			}
			m.addText(mm.getTitleY(), mm.getTitleTop(), mm.getTitle(), "宋体", mm
					.getFontSize(), 0x000000, Chart.Center);
			m.addPointer(mm.getValue(), mm.getInPointerColor(),
					mm.getOutPointerColor()).setShape(Chart.ArrowPointer2);
			m.addText(mm.getValueY(), mm.getValueTop(),
					m.formatValue(mm.getValue(), "2"), "宋体", mm.getFontSize(),
					0x000000, Chart.Center).setBackground(0xffffff, 0xffffff,
					-1);

			String picname = mm.getPicName() + ".png";
			String str = CommonMethod.checkFile() + "/" + picname;
			m.makeChart(str);
			return picname;
		}
		return null;
	}

	/**
	 * 
	 * @author konglq
	 * @date Mar 12, 2011 9:26:05 AM
	 * @param mm
	 * @return String
	 * @Description: TODO(绘制简单的仪表盘图-通用方法)
	 * @ip ip地址
	 * @cpuper 利用率
	 * @title 标题
	 * 
	 * 
	 * 生成的图片是以ip+public为标记的图片
	 * 
	 */
	public void createpubliccpuPic(String ip, int cpuper, String title) {
		// CreateMetersPic cmp = new CreateMetersPic();
		MeterModel mm = new MeterModel();
		mm.setBgColor(0xffffff);
		mm.setInnerRoundColor(0xececec);
		mm.setOutRingColor(0x80ff80);
		mm.setTitle(title);
		mm.setPicx(150);//
		mm.setPicy(150);//
		mm.setMeterX(80);//
		mm.setMeterY(80);//
		mm.setPicName(ip + "public");//
		mm.setValue(cpuper);//
		mm.setMeterSize(60);// 设置仪表盘大小
		mm.setTitleY(79);// 设置标题离左边距离
		mm.setTitleTop(122);// 设置标题离顶部距离
		mm.setValueY(78);// 设置值离左边距离
		mm.setValueTop(105);// 设置值离顶部距离
		mm.setOutPointerColor(pointBorderColor);// 设置指针外部颜色
		mm.setInPointerColor(pointFillColor);// 设置指针内部颜色
		mm.setFontSize(10);// 设置字体大小
		List<StageColor> sm = new ArrayList<StageColor>();
		StageColor sc1 = new StageColor();
		sc1.setColor(innerAreaColor3);
		sc1.setStart(0);
		sc1.setEnd(60);
		StageColor sc2 = new StageColor();
		sc2.setColor(innerAreaColor2);
		sc2.setStart(60);
		sc2.setEnd(80);
		StageColor sc3 = new StageColor();
		sc3.setColor(innerAreaColor1);
		sc3.setStart(80);
		sc3.setEnd(100);
		sm.add(sc1);
		sm.add(sc2);
		sm.add(sc3);
		mm.setList(sm);
		this.createSimpleMeter(mm);
	}

}
