package com.afunms.polling.node;

import java.util.Map;

public class ArrayInfo {
	private String arrayStatus;
	private String firmwareRevision;
	private String productRevision;
	private String localControllerProductRevision;
	private String remoteControllerProductRevision;
	private Map<String,String> lastEventLogEntry;
	
	public String getArrayStatus() {
		return arrayStatus;
	}
	public void setArrayStatus(String arrayStatus) {
		this.arrayStatus = arrayStatus;
	}
	public String getFirmwareRevision() {
		return firmwareRevision;
	}
	public void setFirmwareRevision(String firmwareRevision) {
		this.firmwareRevision = firmwareRevision;
	}
	public String getProductRevision() {
		return productRevision;
	}
	public void setProductRevision(String productRevision) {
		this.productRevision = productRevision;
	}
	public String getLocalControllerProductRevision() {
		return localControllerProductRevision;
	}
	public void setLocalControllerProductRevision(
			String localControllerProductRevision) {
		this.localControllerProductRevision = localControllerProductRevision;
	}
	public String getRemoteControllerProductRevision() {
		return remoteControllerProductRevision;
	}
	public void setRemoteControllerProductRevision(
			String remoteControllerProductRevision) {
		this.remoteControllerProductRevision = remoteControllerProductRevision;
	}
	public Map<String, String> getLastEventLogEntry() {
		return lastEventLogEntry;
	}
	public void setLastEventLogEntry(Map<String, String> lastEventLogEntry) {
		this.lastEventLogEntry = lastEventLogEntry;
	}
	
}
