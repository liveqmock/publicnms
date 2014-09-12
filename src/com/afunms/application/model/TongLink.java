package com.afunms.application.model;

import com.afunms.common.base.BaseVo;

public class TongLink extends BaseVo
{
	private int id;
	private String alias;
	private String ipadress;
	private String username;
	private String password;
	private String filename;
	private String filepath; 
	private String keyword;
	private String businessid;
	private String loginPrompt;
	private String passwordPrompt;
	private String shellPrompt;
	private String ismanaged;
	private String alarmway;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getFilepath() {
		return filepath;
	}
	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
	public String getIpadress() {
		return ipadress;
	}
	public void setIpadress(String ipadress) {
		this.ipadress = ipadress;
	}
	public String getIsmanaged() {
		return ismanaged;
	}
	public void setIsmanaged(String ismanaged) {
		this.ismanaged = ismanaged;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getBusinessid() {
		return businessid;
	}
	public void setBusinessid(String businessid) {
		this.businessid = businessid;
	}
	public String getLoginPrompt() {
		return loginPrompt;
	}
	public void setLoginPrompt(String loginPrompt) {
		this.loginPrompt = loginPrompt;
	}
	public String getPasswordPrompt() {
		return passwordPrompt;
	}
	public void setPasswordPrompt(String passwordPrompt) {
		this.passwordPrompt = passwordPrompt;
	}
	public String getShellPrompt() {
		return shellPrompt;
	}
	public void setShellPrompt(String shellPrompt) {
		this.shellPrompt = shellPrompt;
	}
	public String getAlarmway() {
		return alarmway;
	}
	public void setAlarmway(String alarmway) {
		this.alarmway = alarmway;
	}
}
