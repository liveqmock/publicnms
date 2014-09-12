package com.afunms.config.model;

import com.afunms.common.base.BaseVo;

public class BusinessSystem extends BaseVo {
	private int id;					
	private String name;		
	private String descr;			
	private String contactname;		
	private String contactemail;		
	private String contactphone;
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
	public String getDescr() {
		return descr;
	}
	public void setDescr(String descr) {
		this.descr = descr;
	}
	public String getContactname() {
		return contactname;
	}
	public void setContactname(String contactname) {
		this.contactname = contactname;
	}
	public String getContactemail() {
		return contactemail;
	}
	public void setContactemail(String contactemail) {
		this.contactemail = contactemail;
	}
	public String getContactphone() {
		return contactphone;
	}
	public void setContactphone(String contactphone) {
		this.contactphone = contactphone;
	}	

}
