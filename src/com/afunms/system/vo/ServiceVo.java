package com.afunms.system.vo;

import java.io.Serializable;

public class ServiceVo implements Serializable{ 
	
	private String name;
	
	private String group;
	
	private String pid;
	
	private String status;

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	


}