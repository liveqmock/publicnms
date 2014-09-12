package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

public class ArrayVPNVS implements Serializable{
	
	private int id;
	 
	private String ipaddress;
	 
	private String type;
	 
    private String subtype;
	 
	private Calendar Collecttime;
	
	private int vsIndex;

	private String vsID;

	private int vsProtocol;

	private String vsIpAddr;

	private int vsPort;

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

	public int getVsIndex() {
		return vsIndex;
	}

	public void setVsIndex(int vsIndex) {
		this.vsIndex = vsIndex;
	}

	public String getVsID() {
		return vsID;
	}

	public void setVsID(String vsID) {
		this.vsID = vsID;
	}

	public int getVsProtocol() {
		return vsProtocol;
	}

	public void setVsProtocol(int vsProtocol) {
		this.vsProtocol = vsProtocol;
	}

	public String getVsIpAddr() {
		return vsIpAddr;
	}

	public void setVsIpAddr(String vsIpAddr) {
		this.vsIpAddr = vsIpAddr;
	}

	public int getVsPort() {
		return vsPort;
	}

	public void setVsPort(int vsPort) {
		this.vsPort = vsPort;
	}

}
