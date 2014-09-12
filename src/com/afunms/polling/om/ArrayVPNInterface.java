package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

public class ArrayVPNInterface implements Serializable{
	
	private int id;
	 
	private String ipaddress;
	 
	private String type;
	 
    private String subtype;
	 
	private Calendar Collecttime;
	
	private int infIndex;

	private String infDescr;

	private String infOperStatus;

	private String infAddress;

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

	public int getInfIndex() {
		return infIndex;
	}

	public void setInfIndex(int infIndex) {
		this.infIndex = infIndex;
	}

	public String getInfDescr() {
		return infDescr;
	}

	public void setInfDescr(String infDescr) {
		this.infDescr = infDescr;
	}

	public String getInfOperStatus() {
		return infOperStatus;
	}

	public void setInfOperStatus(String infOperStatus) {
		this.infOperStatus = infOperStatus;
	}

	public String getInfAddress() {
		return infAddress;
	}

	public void setInfAddress(String infAddress) {
		this.infAddress = infAddress;
	}

}
