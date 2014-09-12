package com.afunms.system.vo;

public class InformixVo {
	
	private String dbspace;
	
	private String owner;
	
	private String fname;
	
	private String pages_size;
	
	private String pages_used;
	
	private String pages_free;

	public String getDbspace() {
		return dbspace;
	}

	public void setDbspace(String dbspace) {
		this.dbspace = dbspace;
	}

	public String getFname() {
		return fname;
	}

	public void setFname(String fname) {
		this.fname = fname;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getPages_free() {
		return pages_free;
	}

	public void setPages_free(String pages_free) {
		this.pages_free = pages_free;
	}

	public String getPages_size() {
		return pages_size;
	}

	public void setPages_size(String pages_size) {
		this.pages_size = pages_size;
	}

	public String getPages_used() {
		return pages_used;
	}

	public void setPages_used(String pages_used) {
		this.pages_used = pages_used;
	}

}