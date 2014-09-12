package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

import com.afunms.common.base.BaseVo;

public class NetAppPlex extends BaseVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String ipaddress;
	
	private Calendar collectTime;

	private String plexIndex;

	private String plexName;

	private String plexVolName;

	private String plexStatus;
	
	private String plexPercentResyncing;

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

	public String getPlexIndex() {
		return plexIndex;
	}

	public void setPlexIndex(String plexIndex) {
		this.plexIndex = plexIndex;
	}

	public String getPlexName() {
		return plexName;
	}

	public void setPlexName(String plexName) {
		this.plexName = plexName;
	}

	public String getPlexVolName() {
		return plexVolName;
	}

	public void setPlexVolName(String plexVolName) {
		this.plexVolName = plexVolName;
	}

	public String getPlexStatus() {
		return plexStatus;
	}

	public void setPlexStatus(String plexStatus) {
		this.plexStatus = plexStatus;
	}

	public String getPlexPercentResyncing() {
		return plexPercentResyncing;
	}

	public void setPlexPercentResyncing(String plexPercentResyncing) {
		this.plexPercentResyncing = plexPercentResyncing;
	}
	
		
	
}
