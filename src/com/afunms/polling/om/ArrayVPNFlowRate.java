package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

public class ArrayVPNFlowRate  implements Serializable{
	
	private int id;
	 
	 private String ipaddress;
	 
	 private String type;
	 
	 private String subtype;
	 
	 private Calendar Collecttime;
	 
	private int totalBytesRcvd;

	private int totalBytesSent;

	private int rcvdBytesPerSec;

	private int sentBytesPerSec;

	private int peakRcvdBytesPerSec;

	private int peakSentBytesPerSec;

	private int activeTransac;

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

	public int getTotalBytesRcvd() {
		return totalBytesRcvd;
	}

	public void setTotalBytesRcvd(int totalBytesRcvd) {
		this.totalBytesRcvd = totalBytesRcvd;
	}

	public int getTotalBytesSent() {
		return totalBytesSent;
	}

	public void setTotalBytesSent(int totalBytesSent) {
		this.totalBytesSent = totalBytesSent;
	}

	public int getRcvdBytesPerSec() {
		return rcvdBytesPerSec;
	}

	public void setRcvdBytesPerSec(int rcvdBytesPerSec) {
		this.rcvdBytesPerSec = rcvdBytesPerSec;
	}

	public int getSentBytesPerSec() {
		return sentBytesPerSec;
	}

	public void setSentBytesPerSec(int sentBytesPerSec) {
		this.sentBytesPerSec = sentBytesPerSec;
	}

	public int getPeakRcvdBytesPerSec() {
		return peakRcvdBytesPerSec;
	}

	public void setPeakRcvdBytesPerSec(int peakRcvdBytesPerSec) {
		this.peakRcvdBytesPerSec = peakRcvdBytesPerSec;
	}

	public int getPeakSentBytesPerSec() {
		return peakSentBytesPerSec;
	}

	public void setPeakSentBytesPerSec(int peakSentBytesPerSec) {
		this.peakSentBytesPerSec = peakSentBytesPerSec;
	}

	public int getActiveTransac() {
		return activeTransac;
	}

	public void setActiveTransac(int activeTransac) {
		this.activeTransac = activeTransac;
	}

}
