package com.afunms.realtime;
import java.util.Date;
import java.util.LinkedList;
/********************************************************
 *Title:Queue
 *Description:CPU 利用率_队列模拟
 *Company  dhcc
 *@author zhangcw
 * 2011-3-4 下午12:47:29
 ********************************************************
 */
public class Queue {
	private int LENGTH=30;
	private LinkedList<DataModel> list=new LinkedList<DataModel>();
	private boolean isInited=false;//是否初始化过
	private boolean isDataList=false;//list里的值是否是真实数据值
	/**
	 * 进入队列
	 * @param dataModel
	 */
	public void enqueue(DataModel dataModel)
	 {
		if(list.size()==0){
			 init();
			 enqueue(dataModel);
		 }else if(list.size()<LENGTH){
			 list.addLast(dataModel);
		 }else if(list.size()==LENGTH){
			list.removeFirst();
			list.addLast(dataModel);
		 }
	 }
	/**
	 * 初始化数据
	 */
	 @SuppressWarnings("deprecation")
	public void init(){
		 this.list.clear();
		 for(int i=0;i<LENGTH;i++){
			 DataModel dm=new DataModel();
			 dm.setData(null); 
			 //可以直接 dm.setDate(new Date());
			 Date date=new Date();
			 date.setSeconds((i*5)%60);
			 dm.setDate(date);
			 list.addLast(dm);
		 }
		 this.setInited(true);
	 }
	 /**
	  * 初始化数据,将最后一个数初始化为 d
	 */
	 @SuppressWarnings("deprecation")
	public void initWithLastData(Double d){
		 this.list.clear();
		 for(int i=0;i<LENGTH-1;i++){
			 DataModel dm=new DataModel();
			 dm.setData(null); 
			 //可以直接 dm.setDate(new Date());
			 Date date=new Date();
			 date.setSeconds((i*5)%60);
			 dm.setDate(date);
			 list.addLast(dm);
		 }
		 DataModel dm=new DataModel();
		 dm.setData(d); 
		 //可以直接 dm.setDate(new Date());
		 Date date=new Date();
		 dm.setDate(date);
		 list.addLast(dm);
		 this.setInited(true);
	 }
	public int getLENGTH() {
		return LENGTH;
	}
	public void setLENGTH(int length) {
		LENGTH = length;
	}
	public LinkedList<DataModel> getList() {
		return list;
	}
	public void setList(LinkedList<DataModel> list) {
		this.list = list;
	}
	public boolean isInited() {
		return isInited;
	}
	public void setInited(boolean isInited) {
		this.isInited = isInited;
	}
	public boolean isDataList() {
		return isDataList;
	}
	public void setDataList(boolean isDataList) {
		this.isDataList = isDataList;
	}
	
}
