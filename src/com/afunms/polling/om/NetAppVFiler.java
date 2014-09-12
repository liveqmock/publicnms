package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

import com.afunms.common.base.BaseVo;

public class NetAppVFiler extends BaseVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String ipaddress;
	
	private Calendar collectTime;

	private String vfIndex;

	private String vfName;

	private String vfUuid;

	private String vfIpAddresses;
	
	private String vfStoragePaths;
	
	private String vfIpSpace; // 
	
	private String vfAllowedProtocols; // 
	
	private String vfDisallowedProtocols;// 
	
	private String vfState;// 

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

	public String getVfIndex() {
		return vfIndex;
	}

	public void setVfIndex(String vfIndex) {
		this.vfIndex = vfIndex;
	}

	public String getVfName() {
		return vfName;
	}

	public void setVfName(String vfName) {
		this.vfName = vfName;
	}

	public String getVfUuid() {
		return vfUuid;
	}

	public void setVfUuid(String vfUuid) {
		this.vfUuid = vfUuid;
	}

	public String getVfIpAddresses() {
		return vfIpAddresses;
	}

	public void setVfIpAddresses(String vfIpAddresses) {
		this.vfIpAddresses = vfIpAddresses;
	}

	public String getVfStoragePaths() {
		return vfStoragePaths;
	}

	public void setVfStoragePaths(String vfStoragePaths) {
		this.vfStoragePaths = vfStoragePaths;
	}

	public String getVfIpSpace() {
		return vfIpSpace;
	}

	public void setVfIpSpace(String vfIpSpace) {
		this.vfIpSpace = vfIpSpace;
	}

	public String getVfAllowedProtocols() {
		return vfAllowedProtocols;
	}

	public void setVfAllowedProtocols(String vfAllowedProtocols) {
		this.vfAllowedProtocols = vfAllowedProtocols;
	}

	public String getVfDisallowedProtocols() {
		return vfDisallowedProtocols;
	}

	public void setVfDisallowedProtocols(String vfDisallowedProtocols) {
		this.vfDisallowedProtocols = vfDisallowedProtocols;
	}

	public String getVfState() {
		return vfState;
	}

	public void setVfState(String vfState) {
		this.vfState = vfState;
	}
	
	
	
}
