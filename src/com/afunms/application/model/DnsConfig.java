package com.afunms.application.model;

import com.afunms.common.base.BaseVo;

public class DnsConfig extends BaseVo{
	private int id;
	private String username;
	private String password;
	private String hostip;
	private int hostinter;
	private String dns;
	private String dnsip;
	private int flag;
	private String sendmobiles;
	private String sendemail;
	private String netid;
	private String sendphone;
	
	private int supperid;//π©”¶…Ãid snow add at 2010-5-20
	
	public int getSupperid() {
		return supperid;
	}
	public void setSupperid(int supperid) {
		this.supperid = supperid;
	}
	
	public String getNetid() {
		return netid;
	}
	public void setNetid(String netid) {
		this.netid = netid;
	}
	public String getDns() {
		return dns;
	}
	public void setDns(String dns) {
		this.dns = dns;
	}
	public String getDnsip() {
		return dnsip;
	}
	public void setDnsip(String dnsip) {
		this.dnsip = dnsip;
	}
	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	public int getHostinter() {
		return hostinter;
	}
	public void setHostinter(int hostinter) {
		this.hostinter = hostinter;
	}
	public String getHostip() {
		return hostip;
	}
	public void setHostip(String hostip) {
		this.hostip = hostip;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
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


}
