package com.afunms.polling.node;

public class Battery {
	private String name;
	private String status;
	private String identification;
	private String manufacturerName;
	private String deviceName;
	private String manufacturerDate;
	private String remainingCapacity;
	private String pctRemainingCapacity;
	private String voltage;
	private String dischargeCycles;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getIdentification() {
		return identification;
	}
	public void setIdentification(String identification) {
		this.identification = identification;
	}
	public String getManufacturerName() {
		return manufacturerName;
	}
	public void setManufacturerName(String manufacturerName) {
		this.manufacturerName = manufacturerName;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public String getManufacturerDate() {
		return manufacturerDate;
	}
	public void setManufacturerDate(String manufacturerDate) {
		this.manufacturerDate = manufacturerDate;
	}
	public String getRemainingCapacity() {
		return remainingCapacity;
	}
	public void setRemainingCapacity(String remainingCapacity) {
		this.remainingCapacity = remainingCapacity;
	}
	public String getPctRemainingCapacity() {
		return pctRemainingCapacity;
	}
	public void setPctRemainingCapacity(String pctRemainingCapacity) {
		this.pctRemainingCapacity = pctRemainingCapacity;
	}
	public String getVoltage() {
		return voltage;
	}
	public void setVoltage(String voltage) {
		this.voltage = voltage;
	}
	public String getDischargeCycles() {
		return dischargeCycles;
	}
	public void setDischargeCycles(String dischargeCycles) {
		this.dischargeCycles = dischargeCycles;
	}
	
}
