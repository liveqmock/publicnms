package com.afunms.common.util;
import java.util.ArrayList;
import java.util.List;

public class MeterModel {

	private int picx;//图片长度
	private int picy;//图片宽度
	private  int meterX;//仪表盘宽度
	private int meterY;//仪表盘高度
	
	private String picName;//图片名称
	
	private int innerRoundColor;//内圆颜色
	private int outRingColor;//外环颜色
	private int bgColor;//背景色
	
	private int meterSize;//仪表盘大小
	
	private String title;//图标
	private double value;//值
	
	private int fontSize;//字体大小
	
	private int outPointerColor;//指针外部颜色
	private int inPointerColor;//指针内部颜色
	
	private int titleY;//标题离左边的距离
	private int titleTop;//标题离上边的距离
	
	private int valueY;//值离左边的距离
	private int valueTop;//值离上边的距离
		
	private List<StageColor> stagelist = new ArrayList<StageColor>();
	public List<StageColor> getList() {
		return stagelist;
	}
	public void setList(List<StageColor> stagelist) {
		this.stagelist = stagelist;
	}
	public int getPicx() {
		return picx;
	}
	public void setPicx(int picx) {
		this.picx = picx;
	}
	public int getPicy() {
		return picy;
	}
	public void setPicy(int picy) {
		this.picy = picy;
	}
	public int getMeterX() {
		return meterX;
	}
	public void setMeterX(int meterX) {
		this.meterX = meterX;
	}
	public int getMeterY() {
		return meterY;
	}
	public void setMeterY(int meterY) {
		this.meterY = meterY;
	}
	public int getInnerRoundColor() {
		return innerRoundColor;
	}
	public void setInnerRoundColor(int innerRoundColor) {
		this.innerRoundColor = innerRoundColor;
	}
	public int getOutRingColor() {
		return outRingColor;
	}
	public void setOutRingColor(int outRingColor) {
		this.outRingColor = outRingColor;
	}
	public int getBgColor() {
		return bgColor;
	}
	public void setBgColor(int bgColor) {
		this.bgColor = bgColor;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public int getMeterSize() {
		return meterSize;
	}
	public void setMeterSize(int meterSize) {
		this.meterSize = meterSize;
	}
	public int getFontSize() {
		return fontSize;
	}
	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}
	public int getOutPointerColor() {
		return outPointerColor;
	}
	public void setOutPointerColor(int outPointerColor) {
		this.outPointerColor = outPointerColor;
	}
	public int getInPointerColor() {
		return inPointerColor;
	}
	public void setInPointerColor(int inPointerColor) {
		this.inPointerColor = inPointerColor;
	}
	public int getTitleY() {
		return titleY;
	}
	public void setTitleY(int titleY) {
		this.titleY = titleY;
	}
	public int getTitleTop() {
		return titleTop;
	}
	public void setTitleTop(int titleTop) {
		this.titleTop = titleTop;
	}
	public int getValueY() {
		return valueY;
	}
	public void setValueY(int valueY) {
		this.valueY = valueY;
	}
	public int getValueTop() {
		return valueTop;
	}
	public void setValueTop(int valueTop) {
		this.valueTop = valueTop;
	}
	public String getPicName() {
		return picName;
	}
	public void setPicName(String picName) {
		this.picName = picName;
	}
	
	
	
	
}
