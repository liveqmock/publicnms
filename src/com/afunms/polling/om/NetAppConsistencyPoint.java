package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

import com.afunms.common.base.BaseVo;

public class NetAppConsistencyPoint extends BaseVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String ipaddress;
	
	private Calendar collectTime;

	private String cpTime;

	private String cpFromTimerOps;

	private String cpFromSnapshotOps;
	
	private String cpFromLowWaterOps;
	
	private String cpFromHighWaterOps;
	
	private String cpFromLogFullOps;
	
	private String cpFromCpOps;
	
	private String cpTotalOps;
	
	private String cpFromFlushOps;
	
	private String cpFromSyncOps;
	
	private String cpFromLowVbufOps;
	
	private String cpFromCpDeferredOps;
	
	private String cpFromLowDatavecsOps;

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

	public String getCpTime() {
		return cpTime;
	}

	public void setCpTime(String cpTime) {
		this.cpTime = cpTime;
	}

	public String getCpFromTimerOps() {
		return cpFromTimerOps;
	}

	public void setCpFromTimerOps(String cpFromTimerOps) {
		this.cpFromTimerOps = cpFromTimerOps;
	}

	public String getCpFromSnapshotOps() {
		return cpFromSnapshotOps;
	}

	public void setCpFromSnapshotOps(String cpFromSnapshotOps) {
		this.cpFromSnapshotOps = cpFromSnapshotOps;
	}

	public String getCpFromLowWaterOps() {
		return cpFromLowWaterOps;
	}

	public void setCpFromLowWaterOps(String cpFromLowWaterOps) {
		this.cpFromLowWaterOps = cpFromLowWaterOps;
	}

	public String getCpFromHighWaterOps() {
		return cpFromHighWaterOps;
	}

	public void setCpFromHighWaterOps(String cpFromHighWaterOps) {
		this.cpFromHighWaterOps = cpFromHighWaterOps;
	}

	public String getCpFromLogFullOps() {
		return cpFromLogFullOps;
	}

	public void setCpFromLogFullOps(String cpFromLogFullOps) {
		this.cpFromLogFullOps = cpFromLogFullOps;
	}

	public String getCpFromCpOps() {
		return cpFromCpOps;
	}

	public void setCpFromCpOps(String cpFromCpOps) {
		this.cpFromCpOps = cpFromCpOps;
	}

	public String getCpTotalOps() {
		return cpTotalOps;
	}

	public void setCpTotalOps(String cpTotalOps) {
		this.cpTotalOps = cpTotalOps;
	}

	public String getCpFromFlushOps() {
		return cpFromFlushOps;
	}

	public void setCpFromFlushOps(String cpFromFlushOps) {
		this.cpFromFlushOps = cpFromFlushOps;
	}

	public String getCpFromSyncOps() {
		return cpFromSyncOps;
	}

	public void setCpFromSyncOps(String cpFromSyncOps) {
		this.cpFromSyncOps = cpFromSyncOps;
	}

	public String getCpFromLowVbufOps() {
		return cpFromLowVbufOps;
	}

	public void setCpFromLowVbufOps(String cpFromLowVbufOps) {
		this.cpFromLowVbufOps = cpFromLowVbufOps;
	}

	public String getCpFromCpDeferredOps() {
		return cpFromCpDeferredOps;
	}

	public void setCpFromCpDeferredOps(String cpFromCpDeferredOps) {
		this.cpFromCpDeferredOps = cpFromCpDeferredOps;
	}

	public String getCpFromLowDatavecsOps() {
		return cpFromLowDatavecsOps;
	}

	public void setCpFromLowDatavecsOps(String cpFromLowDatavecsOps) {
		this.cpFromLowDatavecsOps = cpFromLowDatavecsOps;
	}


	
}
