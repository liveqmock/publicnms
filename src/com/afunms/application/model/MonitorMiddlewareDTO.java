package com.afunms.application.model;

import java.util.Hashtable;


public class MonitorMiddlewareDTO {
	
	private int id; 
	
	private String ipAddress;
	
	private String alias;
	
	private String category;
	
	private String monflag;
	
	private String status;
	
	private String port;
	
	private Hashtable eventListSummary;
	
	private String pingValue;

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the ipAddress
	 */
	public String getIpAddress() {
		return ipAddress;
	}

	/**
	 * @param ipAddress the ipAddress to set
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	/**
	 * @return the alias
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * @param alias the alias to set
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}

	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * @return the monflag
	 */
	public String getMonflag() {
		return monflag;
	}

	/**
	 * @param monflag the monflag to set
	 */
	public void setMonflag(String monflag) {
		this.monflag = monflag;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the port
	 */
	public String getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(String port) {
		this.port = port;
	}

	/**
	 * @return the eventListSummary
	 */
	public Hashtable getEventListSummary() {
		return eventListSummary;
	}

	/**
	 * @param eventListSummary the eventListSummary to set
	 */
	public void setEventListSummary(Hashtable eventListSummary) {
		this.eventListSummary = eventListSummary;
	}

	/**
	 * @return the pingValue
	 */
	public String getPingValue() {
		return pingValue;
	}

	/**
	 * @param pingValue the pingValue to set
	 */
	public void setPingValue(String pingValue) {
		this.pingValue = pingValue;
	}

	
}
