package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

public class ArrayVPNVClientApp implements Serializable{
	
	private int id;
	 
	private String ipaddress;
	 
	private String type;
	 
    private String subtype;
	 
	private Calendar Collecttime;

	private int vclientAppIndex;

	private String vclientAppVirtualSite;

	private long vclientAppBytesIn;

	private long vclientAppBytesOut;

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

	public int getVclientAppIndex() {
		return vclientAppIndex;
	}

	public void setVclientAppIndex(int vclientAppIndex) {
		this.vclientAppIndex = vclientAppIndex;
	}

	public String getVclientAppVirtualSite() {
		return vclientAppVirtualSite;
	}

	public void setVclientAppVirtualSite(String vclientAppVirtualSite) {
		this.vclientAppVirtualSite = vclientAppVirtualSite;
	}

	public long getVclientAppBytesIn() {
		return vclientAppBytesIn;
	}

	public void setVclientAppBytesIn(long vclientAppBytesIn) {
		this.vclientAppBytesIn = vclientAppBytesIn;
	}

	public long getVclientAppBytesOut() {
		return vclientAppBytesOut;
	}

	public void setVclientAppBytesOut(long vclientAppBytesOut) {
		this.vclientAppBytesOut = vclientAppBytesOut;
	}
	
}
