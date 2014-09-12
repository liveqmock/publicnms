package com.afunms.polling.om;
/**
 * author ChengFeng
 */ 
import java.io.Serializable;
import java.util.Calendar;

public class ArrayVPNSSL implements Serializable{
	
	private int Id;
	
	private String ipaddress;
	
	private String type;
	
	private String subType;

	private int sslStatus;
	
	private int vhostNum;
	
	private int totalOpenSSLConns;
	
	private int totalAcceptedConns;
	
	private int totalRequestedConns;
	
	private int sslIndex;
	
	private String vhostName;
	
	private int openSSLConns;
	 
	private int acceptedConns;
	
	private int requestedConns;
	
	private int resumedSess;
	
	private int resumableSess;
	 
	private int missSess;
	
	private Calendar Collecttime;

	public Calendar getCollecttime() {
		return Collecttime;
	}

	public void setCollecttime(Calendar collecttime) {
		Collecttime = collecttime;
	}

	public int getSslStatus() {
		return sslStatus;
	}

	public void setSslStatus(int sslStatus) {
		this.sslStatus = sslStatus;
	}

	public int getVhostNum() {
		return vhostNum;
	}

	public void setVhostNum(int vhostNum) {
		this.vhostNum = vhostNum;
	}

	public int getTotalOpenSSLConns() {
		return totalOpenSSLConns;
	}

	public void setTotalOpenSSLConns(int totalOpenSSLConns) {
		this.totalOpenSSLConns = totalOpenSSLConns;
	}

	public int getTotalAcceptedConns() {
		return totalAcceptedConns;
	}

	public void setTotalAcceptedConns(int totalAcceptedConns) {
		this.totalAcceptedConns = totalAcceptedConns;
	}

	public int getTotalRequestedConns() {
		return totalRequestedConns;
	}

	public void setTotalRequestedConns(int totalRequestedConns) {
		this.totalRequestedConns = totalRequestedConns;
	}

	public int getSslIndex() {
		return sslIndex;
	}

	public void setSslIndex(int sslIndex) {
		this.sslIndex = sslIndex;
	}

	public String getVhostName() {
		return vhostName;
	}

	public void setVhostName(String vhostName) {
		this.vhostName = vhostName;
	}

	public int getOpenSSLConns() {
		return openSSLConns;
	}

	public void setOpenSSLConns(int openSSLConns) {
		this.openSSLConns = openSSLConns;
	}

	public int getAcceptedConns() {
		return acceptedConns;
	}

	public void setAcceptedConns(int acceptedConns) {
		this.acceptedConns = acceptedConns;
	}

	public int getRequestedConns() {
		return requestedConns;
	}

	public void setRequestedConns(int requestedConns) {
		this.requestedConns = requestedConns;
	}

	public int getResumedSess() {
		return resumedSess;
	}

	public void setResumedSess(int resumedSess) {
		this.resumedSess = resumedSess;
	}

	public int getResumableSess() {
		return resumableSess;
	}

	public void setResumableSess(int resumableSess) {
		this.resumableSess = resumableSess;
	}

	public int getMissSess() {
		return missSess;
	}

	public void setMissSess(int missSess) {
		this.missSess = missSess;
	}

	public int getId() {
		return Id;
	}

	public void setId(int Id) {
		this.Id = Id;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public void setIpaddress(String ip) {
		this.ipaddress = ip;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSubType() {
		return subType;
	}

	public void setSubType(String subType) {
		this.subType = subType;
	}

	
}
