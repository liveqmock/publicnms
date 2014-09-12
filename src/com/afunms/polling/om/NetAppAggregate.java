package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

import com.afunms.common.base.BaseVo;

public class NetAppAggregate extends BaseVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String ipaddress;

	private String aggrIndex;

	private String aggrName;

	private String aggrFSID;

	private String aggrOwningHost;
	
	private String aggrState;
	
	private String aggrStatus;
	
	private String aggrOptions;
	
	private String aggrUUID;
	
	private String aggrFlexvollist;
	
	private String aggrType;
	
	private String aggrRaidType;
	

	private Calendar collectTime;

	public String getIpaddress() {
		return ipaddress;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public String getAggrIndex() {
		return aggrIndex;
	}

	public void setAggrIndex(String aggrIndex) {
		this.aggrIndex = aggrIndex;
	}

	public String getAggrName() {
		return aggrName;
	}

	public void setAggrName(String aggrName) {
		this.aggrName = aggrName;
	}

	public String getAggrFSID() {
		return aggrFSID;
	}

	public void setAggrFSID(String aggrFSID) {
		this.aggrFSID = aggrFSID;
	}

	public String getAggrOwningHost() {
		return aggrOwningHost;
	}

	public void setAggrOwningHost(String aggrOwningHost) {
		this.aggrOwningHost = aggrOwningHost;
	}

	public String getAggrState() {
		return aggrState;
	}

	public void setAggrState(String aggrState) {
		this.aggrState = aggrState;
	}

	public String getAggrStatus() {
		return aggrStatus;
	}

	public void setAggrStatus(String aggrStatus) {
		this.aggrStatus = aggrStatus;
	}

	public String getAggrOptions() {
		return aggrOptions;
	}

	public void setAggrOptions(String aggrOptions) {
		this.aggrOptions = aggrOptions;
	}

	public String getAggrUUID() {
		return aggrUUID;
	}

	public void setAggrUUID(String aggrUUID) {
		this.aggrUUID = aggrUUID;
	}

	public String getAggrFlexvollist() {
		return aggrFlexvollist;
	}

	public void setAggrFlexvollist(String aggrFlexvollist) {
		this.aggrFlexvollist = aggrFlexvollist;
	}

	public String getAggrType() {
		return aggrType;
	}

	public void setAggrType(String aggrType) {
		this.aggrType = aggrType;
	}

	public String getAggrRaidType() {
		return aggrRaidType;
	}

	public void setAggrRaidType(String aggrRaidType) {
		this.aggrRaidType = aggrRaidType;
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
