package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

public class ArrayVPNTcs implements Serializable{
	
	private int id;
	 
	private String ipaddress;
	 
	private String type;
	 
    private String subtype;
	 
	private Calendar Collecttime;

	private int tcsModuleIndex;

	private String tcsVirtualSite;

	private long tcsBytesIn;

	private long tcsBytesOut;

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

	public int getTcsModuleIndex() {
		return tcsModuleIndex;
	}

	public void setTcsModuleIndex(int tcsModuleIndex) {
		this.tcsModuleIndex = tcsModuleIndex;
	}

	public String getTcsVirtualSite() {
		return tcsVirtualSite;
	}

	public void setTcsVirtualSite(String tcsVirtualSite) {
		this.tcsVirtualSite = tcsVirtualSite;
	}

	public long getTcsBytesIn() {
		return tcsBytesIn;
	}

	public void setTcsBytesIn(long tcsBytesIn) {
		this.tcsBytesIn = tcsBytesIn;
	}

	public long getTcsBytesOut() {
		return tcsBytesOut;
	}

	public void setTcsBytesOut(long tcsBytesOut) {
		this.tcsBytesOut = tcsBytesOut;
	}


}
