package com.afunms.polling.snmp.hdc;

import java.io.Serializable;

public class HdcMessage implements Serializable {
	/**
	 * 
	 */
	private String raidlistSerialNumber;
	private String raidlistMibNickName;
	private String raidlistDKCMainVersion;
	private String raidlistDKCProductName;
	private String dkuRaidListIndexSerialNumber;
	private String dkuHWPS;
	private String dkuHWFan;
	private String dkuHWEnvironment;
	private String dkuHWDrive;
	private String eventListIndexSerialNumber;
	private String eventListNickname;
	private String eventListIndexRecordNo;
	private String eventListREFCODE;
	private String eventListDate;
	private String eventListTime;
	private String eventListDescription;
	private String dkcRaidListIndexSerialNumber;
	private String dkcHWProcessor;
	private String dkcHWCSW;
	private String dkcHWCache;
	private String dkcHWSM;
	private String dkcHWPS;
	private String dkcHWBattery;
	private String dkcHWFan;
	private String dkcHWEnvironment;
	private int nodeid;
	
	public int getNodeid() {
		return nodeid;
	}

	public void setNodeid(int nodeid) {
		this.nodeid = nodeid;
	}

	public String getRaidlistSerialNumber() {
		return raidlistSerialNumber;
	}

	public void setRaidlistSerialNumber(String raidlistSerialNumber) {
		this.raidlistSerialNumber = raidlistSerialNumber;
	}

	public String getRaidlistMibNickName() {
		return raidlistMibNickName;
	}

	public void setRaidlistMibNickName(String raidlistMibNickName) {
		this.raidlistMibNickName = raidlistMibNickName;
	}

	public String getRaidlistDKCMainVersion() {
		return raidlistDKCMainVersion;
	}

	public void setRaidlistDKCMainVersion(String raidlistDKCMainVersion) {
		this.raidlistDKCMainVersion = raidlistDKCMainVersion;
	}

	public String getRaidlistDKCProductName() {
		return raidlistDKCProductName;
	}

	public void setRaidlistDKCProductName(String raidlistDKCProductName) {
		this.raidlistDKCProductName = raidlistDKCProductName;
	}

	public String getDkuRaidListIndexSerialNumber() {
		return dkuRaidListIndexSerialNumber;
	}

	public void setDkuRaidListIndexSerialNumber(String dkuRaidListIndexSerialNumber) {
		this.dkuRaidListIndexSerialNumber = dkuRaidListIndexSerialNumber;
	}

	public String getDkuHWPS() {
		return dkuHWPS;
	}

	public void setDkuHWPS(String dkuHWPS) {
		this.dkuHWPS = dkuHWPS;
	}

	public String getDkuHWFan() {
		return dkuHWFan;
	}

	public void setDkuHWFan(String dkuHWFan) {
		this.dkuHWFan = dkuHWFan;
	}

	public String getDkuHWEnvironment() {
		return dkuHWEnvironment;
	}

	public void setDkuHWEnvironment(String dkuHWEnvironment) {
		this.dkuHWEnvironment = dkuHWEnvironment;
	}

	public String getDkuHWDrive() {
		return dkuHWDrive;
	}

	public void setDkuHWDrive(String dkuHWDrive) {
		this.dkuHWDrive = dkuHWDrive;
	}

	public String getEventListIndexSerialNumber() {
		return eventListIndexSerialNumber;
	}

	public void setEventListIndexSerialNumber(String eventListIndexSerialNumber) {
		this.eventListIndexSerialNumber = eventListIndexSerialNumber;
	}

	public String getEventListNickname() {
		return eventListNickname;
	}

	public void setEventListNickname(String eventListNickname) {
		this.eventListNickname = eventListNickname;
	}

	public String getEventListIndexRecordNo() {
		return eventListIndexRecordNo;
	}

	public void setEventListIndexRecordNo(String eventListIndexRecordNo) {
		this.eventListIndexRecordNo = eventListIndexRecordNo;
	}

	public String getEventListREFCODE() {
		return eventListREFCODE;
	}

	public void setEventListREFCODE(String eventListREFCODE) {
		this.eventListREFCODE = eventListREFCODE;
	}

	public String getEventListDate() {
		return eventListDate;
	}

	public void setEventListDate(String eventListDate) {
		this.eventListDate = eventListDate;
	}

	public String getEventListTime() {
		return eventListTime;
	}

	public void setEventListTime(String eventListTime) {
		this.eventListTime = eventListTime;
	}

	public String getEventListDescription() {
		return eventListDescription;
	}

	public void setEventListDescription(String eventListDescription) {
		this.eventListDescription = eventListDescription;
	}

	public String getDkcRaidListIndexSerialNumber() {
		return dkcRaidListIndexSerialNumber;
	}

	public void setDkcRaidListIndexSerialNumber(String dkcRaidListIndexSerialNumber) {
		this.dkcRaidListIndexSerialNumber = dkcRaidListIndexSerialNumber;
	}

	public String getDkcHWProcessor() {
		return dkcHWProcessor;
	}

	public void setDkcHWProcessor(String dkcHWProcessor) {
		this.dkcHWProcessor = dkcHWProcessor;
	}

	public String getDkcHWCSW() {
		return dkcHWCSW;
	}

	public void setDkcHWCSW(String dkcHWCSW) {
		this.dkcHWCSW = dkcHWCSW;
	}

	public String getDkcHWCache() {
		return dkcHWCache;
	}

	public void setDkcHWCache(String dkcHWCache) {
		this.dkcHWCache = dkcHWCache;
	}

	public String getDkcHWSM() {
		return dkcHWSM;
	}

	public void setDkcHWSM(String dkcHWSM) {
		this.dkcHWSM = dkcHWSM;
	}

	public String getDkcHWPS() {
		return dkcHWPS;
	}

	public void setDkcHWPS(String dkcHWPS) {
		this.dkcHWPS = dkcHWPS;
	}

	public String getDkcHWBattery() {
		return dkcHWBattery;
	}

	public void setDkcHWBattery(String dkcHWBattery) {
		this.dkcHWBattery = dkcHWBattery;
	}

	public String getDkcHWFan() {
		return dkcHWFan;
	}

	public void setDkcHWFan(String dkcHWFan) {
		this.dkcHWFan = dkcHWFan;
	}

	public String getDkcHWEnvironment() {
		return dkcHWEnvironment;
	}

	public void setDkcHWEnvironment(String dkcHWEnvironment) {
		this.dkcHWEnvironment = dkcHWEnvironment;
	}
}
