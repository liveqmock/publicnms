package com.afunms.system.vo;

public class oraVo {
	
	private String file_name;
	
	private String tablespace;
	
	private String size;
	
	private String free;
	
	private String percent;
	
	private String status;

	public String getFile_name() {
		return file_name;
	}

	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}

	public String getFree() {
		return free;
	}

	public void setFree(String free) {
		this.free = free;
	}

	public String getPercent() {
		return percent;
	}

	public void setPercent(String percent) {
		this.percent = percent;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTablespace() {
		return tablespace;
	}

	public void setTablespace(String tablespace) {
		this.tablespace = tablespace;
	}

	

}