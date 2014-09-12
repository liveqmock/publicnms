package com.afunms.polling.om;

import com.afunms.common.base.BaseVo;

public class Hpdpcollectdata extends BaseVo {
	private int id;
	private String ipaddress;
	private String sessionId;
	private String type;
	private String status;
	private String userGroup;
	private String collecttime;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getIpaddress() {
		return ipaddress;
	}
	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getUserGroup() {
		return userGroup;
	}
	public void setUserGroup(String userGroup) {
		this.userGroup = userGroup;
	}
	public String getCollecttime() {
		return collecttime;
	}
	public void setCollecttime(String collecttime) {
		this.collecttime = collecttime;
	}
}
