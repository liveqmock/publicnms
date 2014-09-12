package com.afunms.application.model;

import java.util.Hashtable;

import com.afunms.common.base.BaseVo;

public class MonitorDBDTO extends BaseVo{
	
	private int id; 
	
	private String ipAddress;
	
	private String alias;
	
	private String sid;
	
	private String status;
	
	private String dbtype;
	
	private String mon_flag;  
	
	private String dbname;
	
	private String port;
	
	private String pingValue;  
	
	private Hashtable eventListSummary;

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
	 * @return the sid
	 */
	public String getSid() {
		return sid;
	}

	/**
	 * @param sid the sid to set
	 */
	public void setSid(String sid) {
		this.sid = sid;
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
	 * @return the dbtype
	 */
	public String getDbtype() {
		return dbtype;
	}

	/**
	 * @param dbtype the dbtype to set
	 */
	public void setDbtype(String dbtype) {
		this.dbtype = dbtype;
	}

	/**
	 * @return the mon_flag
	 */
	public String getMon_flag() {
		return mon_flag;
	}

	/**
	 * @param mon_flag the mon_flag to set
	 */
	public void setMon_flag(String mon_flag) {
		this.mon_flag = mon_flag;
	}

	/**
	 * @return the dbname
	 */
	public String getDbname() {
		return dbname;
	}

	/**
	 * @param dbname the dbname to set
	 */
	public void setDbname(String dbname) {
		this.dbname = dbname;
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

	
}
