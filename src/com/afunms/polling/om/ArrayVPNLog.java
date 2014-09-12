package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

public class ArrayVPNLog implements Serializable{
	
	private int id;
	 
	private String ipaddress;
	 
	private String type;
	 
    private String subtype;
	 
	private Calendar Collecttime;
	
	private int logNotificationsSent;

	private int logNotificationsEnabled;

	private int logMaxSeverity;

	private int logHistTableMaxLength;

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

	public int getLogNotificationsSent() {
		return logNotificationsSent;
	}

	public void setLogNotificationsSent(int logNotificationsSent) {
		this.logNotificationsSent = logNotificationsSent;
	}

	public int getLogNotificationsEnabled() {
		return logNotificationsEnabled;
	}

	public void setLogNotificationsEnabled(int logNotificationsEnabled) {
		this.logNotificationsEnabled = logNotificationsEnabled;
	}

	public int getLogMaxSeverity() {
		return logMaxSeverity;
	}

	public void setLogMaxSeverity(int logMaxSeverity) {
		this.logMaxSeverity = logMaxSeverity;
	}

	public int getLogHistTableMaxLength() {
		return logHistTableMaxLength;
	}

	public void setLogHistTableMaxLength(int logHistTableMaxLength) {
		this.logHistTableMaxLength = logHistTableMaxLength;
	}

}
