package com.afunms.common.util;

import java.util.ArrayList;
import java.util.List;
import com.afunms.initialize.ResourceCenter;
import ChartDirector.AngularMeter;
import ChartDirector.Chart;
/********************************************************
 *Title:CreateAlarmMetersPic
 *Description:生成告警关闭率表盘图片
 *Company  dhcc
 *@author zhangcw
 * Mar 28, 2011 3:02:48 PM
 ********************************************************
 */
public class CreateAlarmMetersPic {
	public String  createClosedAlarmPic(String fileName,double value) {
		MeterModel mm = new MeterModel();
		mm.setBgColor(0xffffff);
		mm.setInnerRoundColor(0xececec);
		mm.setOutRingColor(0x80ff80);
		mm.setTitle("告警关闭率");
		mm.setPicx(286);//
		mm.setPicy(300);//
		mm.setMeterX(141);// 仪表盘宽度
		mm.setMeterY(143);// 仪表盘高度
		mm.setPicName(fileName);//
		mm.setValue(value);//
		mm.setMeterSize(85);// 设置仪表盘大小
		mm.setTitleY(139);// 设置标题离左边距离
		mm.setTitleTop(208);// 设置标题离顶部距离
		mm.setValueY(139);// 设置值离左边距离
		mm.setValueTop(183);// 设置值离顶部距离
		mm.setOutPointerColor(0x80ff80);// 设置指针外部颜色
		mm.setInPointerColor(0x8080ff);// 设置指针内部颜色
		mm.setFontSize(12);// 设置字体大小
		
		List<StageColor> sm = new ArrayList<StageColor>();
		StageColor sc1 = new StageColor();
		sc1.setColor(0x99ff99);
		sc1.setStart(firstValue);
		sc1.setEnd(secondValue);
		
		StageColor sc2 = new StageColor();
		sc2.setColor(0xffff00);
		sc2.setStart(secondValue);
		sc2.setEnd(thirdValue);
		
		StageColor sc3 = new StageColor();
		sc3.setColor(0xff3333);
		sc3.setStart(thirdValue);
		sc3.setEnd(forthValue);
		
		sm.add(sc1);
		sm.add(sc2);
		sm.add(sc3);
		mm.setList(sm);
		
		return createSimpleMeter(mm);
	}
	//仪表盘的分段值
	private int firstValue=0;
	private int secondValue=60;
	private int thirdValue=80;
	private int forthValue=100;
	
	public String createSimpleMeter(MeterModel mm) {
		Chart.setLicenseCode(CommonMethod.keycode);
		if (!mm.equals("")) {
			AngularMeter m = new AngularMeter(mm.getPicx(), mm.getPicy(),
					0x80ff80, 0xffffff, -2);
			m.setBackground(mm.getBgColor());
			m.setMeter(mm.getMeterX(), mm.getMeterY(), mm.getMeterSize(), -135,
					135);
			m.setScale(0, 100, 10, 5, 0);
			m.setLineWidth(0, 2, 1);
			m.setBgImage(ResourceCenter.getInstance().getSysPath()
					+ "resource\\image\\dashboard2.png");
			if (!mm.getList().isEmpty()) {
				for (int i = 0; i < mm.getList().size(); i++) {
					m.addZone(mm.getList().get(i).getStart(), mm.getList().get(i).getEnd(), mm.getList().get(i).getColor());
				}
			}
			int valuefontcolor=0x000000;//值字体颜色
			if(mm.getValue()<secondValue&&mm.getValue()>firstValue){
				valuefontcolor=0x9900FF;
			}else if(mm.getValue()>=secondValue&&mm.getValue()<thirdValue){
				valuefontcolor=0xffff00;
			}else if(mm.getValue()>=thirdValue&&mm.getValue()<=forthValue){
				valuefontcolor=0xff3333;
			}else{
				valuefontcolor=0x000000;
			}
			int titlefontcolor=0x000080;//标题字体颜色 
			m.addText(mm.getTitleY(), mm.getTitleTop(), mm.getTitle(), "宋体", mm
					.getFontSize(), titlefontcolor, Chart.Center);
			m.addPointer(mm.getValue(), mm.getInPointerColor(),
					mm.getOutPointerColor()).setShape(Chart.ArrowPointer2);
			m.addText(mm.getValueY(), mm.getValueTop(),
					m.formatValue(mm.getValue(), "2"), "宋体", mm.getFontSize(),
					valuefontcolor, Chart.Center).setBackground(0xffffff, 0xffffff,
					-1);
			String picname = mm.getPicName() + ".png";
			String str = CommonMethod.checkFile() + "/"+picname;
			m.makeChart(str);
			return picname;
		}
		return null;
	}
	public static void main(String[] args) {
		CreateAlarmMetersPic cmp = new CreateAlarmMetersPic();
		cmp.createClosedAlarmPic("sadf", 80);
	}
}
