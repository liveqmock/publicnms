package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

public class ArrayVPNSystem implements Serializable{
	
	private int id;
	 
	private String ipaddress;
	 
	private String type;
	 
    private String subtype;
	 
	private Calendar Collecttime;
	
	private int cpuUtilization;

	private int connectionsPerSec;

	private int requestsPerSec;

	private String sysDescr;

	private String sysObjectID;

	private String sysUpTime;

	private String sysContact;

	private String sysName;

	private String sysLocation;

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

	public int getCpuUtilization() {
		return cpuUtilization;
	}

	public void setCpuUtilization(int cpuUtilization) {
		this.cpuUtilization = cpuUtilization;
	}

	public int getConnectionsPerSec() {
		return connectionsPerSec;
	}

	public void setConnectionsPerSec(int connectionsPerSec) {
		this.connectionsPerSec = connectionsPerSec;
	}

	public int getRequestsPerSec() {
		return requestsPerSec;
	}

	public void setRequestsPerSec(int requestsPerSec) {
		this.requestsPerSec = requestsPerSec;
	}

	public String getSysDescr() {
		return sysDescr;
	}

	public void setSysDescr(String sysDescr) {
		this.sysDescr = sysDescr;
	}

	public String getSysObjectID() {
		return sysObjectID;
	}

	public void setSysObjectID(String sysObjectID) {
		this.sysObjectID = sysObjectID;
	}

	public String getSysUpTime() {
		return sysUpTime;
	}

	public void setSysUpTime(String sysUpTime) {
		this.sysUpTime = sysUpTime;
	}

	public String getSysContact() {
		return sysContact;
	}

	public void setSysContact(String sysContact) {
		this.sysContact = sysContact;
	}

	public String getSysName() {
		return sysName;
	}

	public void setSysName(String sysName) {
		this.sysName = sysName;
	}

	public String getSysLocation() {
		return sysLocation;
	}

	public void setSysLocation(String sysLocation) {
		this.sysLocation = sysLocation;
	}
	

}
