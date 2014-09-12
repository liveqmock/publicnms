package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

public class ArrayVPNSession implements Serializable{

	 private int id;
	 
	 private String ipaddress;
	 
	 private String type;
	 
	 private String subtype;
	 
	 private String Collecttime;
	 
	 private int numSessions;
	 
	 private int successLogin;
	 
	 private int successLogout;
	 
	 private int failureLogin;
	 
	 private long totalBytesIn; 
	 
	 private long totalBytesOut;
	 
	 private int maxActiveSessions;
	 
	 private int errorLogin;
	 
	 private int lockOutLogin;

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

	public String getCollecttime() {
		return Collecttime;
	}

	public void setCollecttime(String collecttime) {
		Collecttime = collecttime;
	}

	public int getNumSessions() {
		return numSessions;
	}

	public void setNumSessions(int numSessions) {
		this.numSessions = numSessions;
	}

	public int getSuccessLogin() {
		return successLogin;
	}

	public void setSuccessLogin(int successLogin) {
		this.successLogin = successLogin;
	}

	public int getSuccessLogout() {
		return successLogout;
	}

	public void setSuccessLogout(int successLogout) {
		this.successLogout = successLogout;
	}

	public int getFailureLogin() {
		return failureLogin;
	}

	public void setFailureLogin(int failureLogin) {
		this.failureLogin = failureLogin;
	}

	public long getTotalBytesIn() {
		return totalBytesIn;
	}

	public void setTotalBytesIn(long totalBytesIn) {
		this.totalBytesIn = totalBytesIn;
	}

	public long getTotalBytesOut() {
		return totalBytesOut;
	}

	public void setTotalBytesOut(long totalBytesOut) {
		this.totalBytesOut = totalBytesOut;
	}

	public int getMaxActiveSessions() {
		return maxActiveSessions;
	}

	public void setMaxActiveSessions(int maxActiveSessions) {
		this.maxActiveSessions = maxActiveSessions;
	}

	public int getErrorLogin() {
		return errorLogin;
	}

	public void setErrorLogin(int errorLogin) {
		this.errorLogin = errorLogin;
	}

	public int getLockOutLogin() {
		return lockOutLogin;
	}

	public void setLockOutLogin(int lockOutLogin) {
		this.lockOutLogin = lockOutLogin;
	}
}
