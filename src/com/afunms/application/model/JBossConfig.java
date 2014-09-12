package com.afunms.application.model;

import com.afunms.common.base.BaseVo;

public class JBossConfig extends BaseVo{
	
	private int id;
	
	private String alias;
		   
	private String username;
		   
	private String password;
		   
	private String ipaddress;
		   
	private int port;
		   
	private int flag;
		   
    private String sendmobiles;
		   
    private String sendemail;
		   
    private String netid;
		   
    private String sendphone;

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

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

	public String getNetid() {
		return netid;
	}

	public void setNetid(String netid) {
		this.netid = netid;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getSendemail() {
		return sendemail;
	}

	public void setSendemail(String sendemail) {
		this.sendemail = sendemail;
	}

	public String getSendmobiles() {
		return sendmobiles;
	}

	public void setSendmobiles(String sendmobiles) {
		this.sendmobiles = sendmobiles;
	}

	public String getSendphone() {
		return sendphone;
	}

	public void setSendphone(String sendphone) {
		this.sendphone = sendphone;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

}
