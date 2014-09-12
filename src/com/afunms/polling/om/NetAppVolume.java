package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

import com.afunms.common.base.BaseVo;

public class NetAppVolume extends BaseVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String ipaddress;

	private String volIndex;

	private String volName;

	private String volFSID;

	private String volOwningHost;
	
	private String volState;
	
	private String volStatus;
	
	private String volOptions;
	
	private String volUUID;
	
	private String volAggrName;
	
	private String volType;

	private Calendar collectTime;

	public String getIpaddress() {
		return ipaddress;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public String getVolIndex() {
		return volIndex;
	}

	public void setVolIndex(String volIndex) {
		this.volIndex = volIndex;
	}

	public String getVolName() {
		return volName;
	}

	public void setVolName(String volName) {
		this.volName = volName;
	}

	public String getVolFSID() {
		return volFSID;
	}

	public void setVolFSID(String volFSID) {
		this.volFSID = volFSID;
	}

	public String getVolOwningHost() {
		return volOwningHost;
	}

	public void setVolOwningHost(String volOwningHost) {
		this.volOwningHost = volOwningHost;
	}

	public String getVolState() {
		return volState;
	}

	public void setVolState(String volState) {
		this.volState = volState;
	}

	public String getVolStatus() {
		return volStatus;
	}

	public void setVolStatus(String volStatus) {
		this.volStatus = volStatus;
	}

	public String getVolOptions() {
		return volOptions;
	}

	public void setVolOptions(String volOptions) {
		this.volOptions = volOptions;
	}

	public String getVolUUID() {
		return volUUID;
	}

	public void setVolUUID(String volUUID) {
		this.volUUID = volUUID;
	}

	public String getVolAggrName() {
		return volAggrName;
	}

	public void setVolAggrName(String volAggrName) {
		this.volAggrName = volAggrName;
	}

	public String getVolType() {
		return volType;
	}

	public void setVolType(String volType) {
		this.volType = volType;
	}

	public Calendar getCollectTime() {
		return collectTime;
	}

	public void setCollectTime(Calendar collectTime) {
		this.collectTime = collectTime;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	
}
