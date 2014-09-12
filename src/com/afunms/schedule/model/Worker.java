package com.afunms.schedule.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Worker implements Comparable<Worker> {
	private int index;
	private String name;
	private String place;
	private String workTime;
	private boolean continuous;
	private Date date;
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Worker(){
	}
	
	public Worker(String name){
		this.name = name;
	}
	
	public Worker(String name, String place, String workTime) {
		super();
		this.name = name;
		this.place = place;
		this.workTime = workTime;
	}
	
	public Worker(int index, String name, String place, String workTime) {
		super();
		this.index = index;
		this.name = name;
		this.place = place;
		this.workTime = workTime;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}
	public String getWorkTime() {
		return workTime;
	}
	public void setWorkTime(String workTime) {
		this.workTime = workTime;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public boolean isContinuous() {
		return continuous;
	}

	public void setContinuous(boolean continuous) {
		this.continuous = continuous;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String strDate = "";
		if (null != this.date) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			strDate = sdf.format(this.date);
		}
		return "date:" + strDate + " day:"+ this.index + ":" + this.name+":"+this.place+":"+this.workTime;
	}

	public int compareTo(Worker worker) {
		String str1 = this.getName() + "_" + this.getPlace() + "_" + this.getWorkTime();
		String str2 = worker.getName() + "_" + worker.getPlace() + "_" + worker.getWorkTime();
		return str1.compareTo(str2);
	}
	
}
