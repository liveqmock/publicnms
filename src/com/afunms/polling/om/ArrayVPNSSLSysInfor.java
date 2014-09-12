package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

public class ArrayVPNSSLSysInfor implements Serializable{
	
	private int id;
	 
	private String ipaddress;
	 
	private String type;
	 
    private String subtype;
	 
	private Calendar Collecttime;

	private String sslStatus;

	private int vhostNum;

	private long totalOpenSSLConns;

	private long totalAcceptedConns;

	private long totalRequestedConns;

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSubtype() {
		return subtype;
	}

	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}

	public Calendar getCollecttime() {
		return Collecttime;
	}

	public void setCollecttime(Calendar collecttime) {
		Collecttime = collecttime;
	}

	public String getSslStatus() {
		return sslStatus;
	}

	public void setSslStatus(String sslStatus) {
		this.sslStatus = sslStatus;
	}

	public int getVhostNum() {
		return vhostNum;
	}

	public void setVhostNum(int vhostNum) {
		this.vhostNum = vhostNum;
	}

	public long getTotalOpenSSLConns() {
		return totalOpenSSLConns;
	}

	public void setTotalOpenSSLConns(long totalOpenSSLConns) {
		this.totalOpenSSLConns = totalOpenSSLConns;
	}

	public long getTotalAcceptedConns() {
		return totalAcceptedConns;
	}

	public void setTotalAcceptedConns(long totalAcceptedConns) {
		this.totalAcceptedConns = totalAcceptedConns;
	}

	public long getTotalRequestedConns() {
		return totalRequestedConns;
	}

	public void setTotalRequestedConns(long totalRequestedConns) {
		this.totalRequestedConns = totalRequestedConns;
	}
}
