package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

import com.afunms.common.base.BaseVo;

public class NetAppDump extends BaseVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String ipaddress;
	
	private Calendar collectTime;

	private String dmpActives;

	private String dmpAttempts;

	private String dmpSuccesses;
	
	private String dmpFailures;

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

	public String getDmpActives() {
		return dmpActives;
	}

	public void setDmpActives(String dmpActives) {
		this.dmpActives = dmpActives;
	}

	public String getDmpAttempts() {
		return dmpAttempts;
	}

	public void setDmpAttempts(String dmpAttempts) {
		this.dmpAttempts = dmpAttempts;
	}

	public String getDmpSuccesses() {
		return dmpSuccesses;
	}

	public void setDmpSuccesses(String dmpSuccesses) {
		this.dmpSuccesses = dmpSuccesses;
	}

	public String getDmpFailures() {
		return dmpFailures;
	}

	public void setDmpFailures(String dmpFailures) {
		this.dmpFailures = dmpFailures;
	}


}
