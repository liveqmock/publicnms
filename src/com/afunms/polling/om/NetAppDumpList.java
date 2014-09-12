package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

import com.afunms.common.base.BaseVo;

public class NetAppDumpList extends BaseVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String ipaddress;
	
	private Calendar collectTime;

	private String dmpIndex;
	
	private String dmpStPath;

	private String dmpStAttempts;

	private String dmpStSuccesses;
	
	private String dmpStFailures;
	
	private String dmpTime;
	
	private String dmpStatus;
	
	private String dmpLevel;
	
	private String dmpNumFiles;
	
	private String dmpDataAmount;
	
	private String dmpStartTime;
	
	private String dmpDuration;

	public String getIpaddress() {
		return ipaddress;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public Calendar getCollectTime() {
		return collectTime;
	}

	public void setCollectTime(Calendar collectTime) {
		this.collectTime = collectTime;
	}

	public String getDmpIndex() {
		return dmpIndex;
	}

	public void setDmpIndex(String dmpIndex) {
		this.dmpIndex = dmpIndex;
	}

	public String getDmpStPath() {
		return dmpStPath;
	}

	public void setDmpStPath(String dmpStPath) {
		this.dmpStPath = dmpStPath;
	}

	public String getDmpStAttempts() {
		return dmpStAttempts;
	}

	public void setDmpStAttempts(String dmpStAttempts) {
		this.dmpStAttempts = dmpStAttempts;
	}

	public String getDmpStSuccesses() {
		return dmpStSuccesses;
	}

	public void setDmpStSuccesses(String dmpStSuccesses) {
		this.dmpStSuccesses = dmpStSuccesses;
	}

	public String getDmpStFailures() {
		return dmpStFailures;
	}

	public void setDmpStFailures(String dmpStFailures) {
		this.dmpStFailures = dmpStFailures;
	}

	public String getDmpTime() {
		return dmpTime;
	}

	public void setDmpTime(String dmpTime) {
		this.dmpTime = dmpTime;
	}

	public String getDmpStatus() {
		return dmpStatus;
	}

	public void setDmpStatus(String dmpStatus) {
		this.dmpStatus = dmpStatus;
	}

	public String getDmpLevel() {
		return dmpLevel;
	}

	public void setDmpLevel(String dmpLevel) {
		this.dmpLevel = dmpLevel;
	}

	public String getDmpNumFiles() {
		return dmpNumFiles;
	}

	public void setDmpNumFiles(String dmpNumFiles) {
		this.dmpNumFiles = dmpNumFiles;
	}

	public String getDmpDataAmount() {
		return dmpDataAmount;
	}

	public void setDmpDataAmount(String dmpDataAmount) {
		this.dmpDataAmount = dmpDataAmount;
	}

	public String getDmpStartTime() {
		return dmpStartTime;
	}

	public void setDmpStartTime(String dmpStartTime) {
		this.dmpStartTime = dmpStartTime;
	}

	public String getDmpDuration() {
		return dmpDuration;
	}

	public void setDmpDuration(String dmpDuration) {
		this.dmpDuration = dmpDuration;
	}


}
