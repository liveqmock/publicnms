package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

public class ArrayVPNVirtualSite implements Serializable{
	
	private int id;
	 
	 private String ipaddress;
	 
	 private String type;
	 
	 private String subtype;
	 
	 private Calendar Collecttime;

	private String virtualSiteId;

	private int virtualSiteActiveSessions;

	private int virtualSiteSuccessLogin;

	private int virtualSiteFailureLogin;

	private int virtualSiteErrorLogin;

	private int virtualSiteSuccessLogout;

	private long virtualSiteBytesIn;

	private long virtualSiteBytesOut;

	private int virtualSiteMaxActiveSessions;

	private int virtualSiteFileAuthorizedRequests;
	
	private int virtualSiteFileUnauthorizedRequests;

	private int virtualSiteFileBytesIn;

	private int virtualSiteFileBytesOut;

	private int virtualSiteLockedLogin;

	private int virtualSiteRejectedLogin;

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

	public String getVirtualSiteId() {
		return virtualSiteId;
	}

	public void setVirtualSiteId(String virtualSiteId) {
		this.virtualSiteId = virtualSiteId;
	}

	public int getVirtualSiteActiveSessions() {
		return virtualSiteActiveSessions;
	}

	public void setVirtualSiteActiveSessions(int virtualSiteActiveSessions) {
		this.virtualSiteActiveSessions = virtualSiteActiveSessions;
	}

	public int getVirtualSiteSuccessLogin() {
		return virtualSiteSuccessLogin;
	}

	public void setVirtualSiteSuccessLogin(int virtualSiteSuccessLogin) {
		this.virtualSiteSuccessLogin = virtualSiteSuccessLogin;
	}

	public int getVirtualSiteFailureLogin() {
		return virtualSiteFailureLogin;
	}

	public void setVirtualSiteFailureLogin(int virtualSiteFailureLogin) {
		this.virtualSiteFailureLogin = virtualSiteFailureLogin;
	}

	public int getVirtualSiteErrorLogin() {
		return virtualSiteErrorLogin;
	}

	public void setVirtualSiteErrorLogin(int virtualSiteErrorLogin) {
		this.virtualSiteErrorLogin = virtualSiteErrorLogin;
	}

	public int getVirtualSiteSuccessLogout() {
		return virtualSiteSuccessLogout;
	}

	public void setVirtualSiteSuccessLogout(int virtualSiteSuccessLogout) {
		this.virtualSiteSuccessLogout = virtualSiteSuccessLogout;
	}

	public long getVirtualSiteBytesIn() {
		return virtualSiteBytesIn;
	}

	public void setVirtualSiteBytesIn(long virtualSiteBytesIn) {
		this.virtualSiteBytesIn = virtualSiteBytesIn;
	}

	public long getVirtualSiteBytesOut() {
		return virtualSiteBytesOut;
	}

	public void setVirtualSiteBytesOut(long virtualSiteBytesOut) {
		this.virtualSiteBytesOut = virtualSiteBytesOut;
	}

	public int getVirtualSiteMaxActiveSessions() {
		return virtualSiteMaxActiveSessions;
	}

	public void setVirtualSiteMaxActiveSessions(int virtualSiteMaxActiveSessions) {
		this.virtualSiteMaxActiveSessions = virtualSiteMaxActiveSessions;
	}

	public int getVirtualSiteFileAuthorizedRequests() {
		return virtualSiteFileAuthorizedRequests;
	}

	public void setVirtualSiteFileAuthorizedRequests(
			int virtualSiteFileAuthorizedRequests) {
		this.virtualSiteFileAuthorizedRequests = virtualSiteFileAuthorizedRequests;
	}

	public int getVirtualSiteFileUnauthorizedRequests() {
		return virtualSiteFileUnauthorizedRequests;
	}

	public void setVirtualSiteFileUnauthorizedRequests(
			int virtualSiteFileUnauthorizedRequests) {
		this.virtualSiteFileUnauthorizedRequests = virtualSiteFileUnauthorizedRequests;
	}

	public int getVirtualSiteFileBytesIn() {
		return virtualSiteFileBytesIn;
	}

	public void setVirtualSiteFileBytesIn(int virtualSiteFileBytesIn) {
		this.virtualSiteFileBytesIn = virtualSiteFileBytesIn;
	}

	public int getVirtualSiteFileBytesOut() {
		return virtualSiteFileBytesOut;
	}

	public void setVirtualSiteFileBytesOut(int virtualSiteFileBytesOut) {
		this.virtualSiteFileBytesOut = virtualSiteFileBytesOut;
	}

	public int getVirtualSiteLockedLogin() {
		return virtualSiteLockedLogin;
	}

	public void setVirtualSiteLockedLogin(int virtualSiteLockedLogin) {
		this.virtualSiteLockedLogin = virtualSiteLockedLogin;
	}

	public int getVirtualSiteRejectedLogin() {
		return virtualSiteRejectedLogin;
	}

	public void setVirtualSiteRejectedLogin(int virtualSiteRejectedLogin) {
		this.virtualSiteRejectedLogin = virtualSiteRejectedLogin;
	}
}
