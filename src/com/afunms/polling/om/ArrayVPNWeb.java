package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

public class ArrayVPNWeb implements Serializable{
	
	private int id;
	 
	private String ipaddress;
	 
	private String type;
	 
    private String subtype;
	 
	private Calendar Collecttime;

	private String webId;

	private int  webAuthorizedReq;

	private int webUnauthorizedReq;

	private long webBytesIn;

	private long webBytesOut;

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

	public String getWebId() {
		return webId;
	}

	public void setWebId(String webId) {
		this.webId = webId;
	}

	public int getWebAuthorizedReq() {
		return webAuthorizedReq;
	}

	public void setWebAuthorizedReq(int webAuthorizedReq) {
		this.webAuthorizedReq = webAuthorizedReq;
	}

	public int getWebUnauthorizedReq() {
		return webUnauthorizedReq;
	}

	public void setWebUnauthorizedReq(int webUnauthorizedReq) {
		this.webUnauthorizedReq = webUnauthorizedReq;
	}

	public long getWebBytesIn() {
		return webBytesIn;
	}

	public void setWebBytesIn(long webBytesIn) {
		this.webBytesIn = webBytesIn;
	}

	public long getWebBytesOut() {
		return webBytesOut;
	}

	public void setWebBytesOut(long webBytesOut) {
		this.webBytesOut = webBytesOut;
	}

	
}
