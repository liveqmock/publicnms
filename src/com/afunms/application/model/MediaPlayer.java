package com.afunms.application.model;

import com.afunms.common.base.BaseVo;

public class MediaPlayer extends BaseVo {

	private int id;
	private String name;
	private String fileName;
	private String desc;
	private int bsid;
	private int operid;
	private String dotime;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public int getBsid() {
		return bsid;
	}
	public void setBsid(int bsid) {
		this.bsid = bsid;
	}
	public int getOperid() {
		return operid;
	}
	public void setOperid(int operid) {
		this.operid = operid;
	}
	public String getDotime() {
		return dotime;
	}
	public void setDotime(String dotime) {
		this.dotime = dotime;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	
}
