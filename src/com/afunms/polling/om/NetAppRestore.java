package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

import com.afunms.common.base.BaseVo;

public class NetAppRestore extends BaseVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String ipaddress;
	
	private Calendar collectTime;

	private String rstActives;

	private String rstAttempts;

	private String rstSuccesses;
	
	private String rstFailures;

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

	public String getRstActives() {
		return rstActives;
	}

	public void setRstActives(String rstActives) {
		this.rstActives = rstActives;
	}

	public String getRstAttempts() {
		return rstAttempts;
	}

	public void setRstAttempts(String rstAttempts) {
		this.rstAttempts = rstAttempts;
	}

	public String getRstSuccesses() {
		return rstSuccesses;
	}

	public void setRstSuccesses(String rstSuccesses) {
		this.rstSuccesses = rstSuccesses;
	}

	public String getRstFailures() {
		return rstFailures;
	}

	public void setRstFailures(String rstFailures) {
		this.rstFailures = rstFailures;
	}


}
