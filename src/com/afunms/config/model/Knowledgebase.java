package com.afunms.config.model;

import com.afunms.common.base.BaseVo;

public class Knowledgebase extends BaseVo {
	private int id;					
	private String category;		
	private String entity;			
	private String subentity;
	private String titles;
	private String contents;
	private String bak;
	private String attachfiles;
	private String userid;
	private String ktime;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getEntity() {
		return entity;
	}
	public void setEntity(String entity) {
		this.entity = entity;
	}
	public String getSubentity() {
		return subentity;
	}
	public void setSubentity(String subentity) {
		this.subentity = subentity;
	}
	public String getTitles() {
		return titles;
	}
	public void setTitles(String titles) {
		this.titles = titles;
	}
	public String getContents() {
		return contents;
	}
	public void setContents(String contents) {
		this.contents = contents;
	}
	public String getBak() {
		return bak;
	}
	public void setBak(String bak) {
		this.bak = bak;
	}
	public String getAttachfiles() {
		return attachfiles;
	}
	public void setAttachfiles(String attachfiles) {
		this.attachfiles = attachfiles;
	}

	public String getKtime() {
		return ktime;
	}
	public void setKtime(String ktime) {
		this.ktime = ktime;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}

	
	
}
