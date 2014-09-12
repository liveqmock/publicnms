package com.afunms.polling.om;

import com.afunms.common.base.BaseVo;

public class Ggscicollectdata extends BaseVo {
	private int id;
	private String ipaddress;
	private String programName;
	private String status;
	private String group;
	private String lagAtChkpt;
	private String timeSinceChkpt;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getProgramName() {
		return programName;
	}
	public void setProgramName(String programName) {
		this.programName = programName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getLagAtChkpt() {
		return lagAtChkpt;
	}
	public void setLagAtChkpt(String lagAtChkpt) {
		this.lagAtChkpt = lagAtChkpt;
	}
	public String getTimeSinceChkpt() {
		return timeSinceChkpt;
	}
	public void setTimeSinceChkpt(String timeSinceChkpt) {
		this.timeSinceChkpt = timeSinceChkpt;
	}
	public String getIpaddress() {
		return ipaddress;
	}
	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}
}
