package com.afunms.application.wasmonitor;

import java.util.Calendar;

import com.afunms.common.base.BaseVo;

public class Was5system extends BaseVo{
	private String ipaddress;
	private int cpuUsageSinceLastMeasurement;
	private int cpuUsageSinceServerStarted;
	private int freeMemory;
	private Calendar recordtime;
	
	public String getIpaddress() {
		return ipaddress;
	}
	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}
	public int getCpuUsageSinceLastMeasurement() {
		return cpuUsageSinceLastMeasurement;
	}
	public void setCpuUsageSinceLastMeasurement(int cpuUsageSinceLastMeasurement) {
		this.cpuUsageSinceLastMeasurement = cpuUsageSinceLastMeasurement;
	}
	public int getCpuUsageSinceServerStarted() {
		return cpuUsageSinceServerStarted;
	}
	public void setCpuUsageSinceServerStarted(int cpuUsageSinceServerStarted) {
		this.cpuUsageSinceServerStarted = cpuUsageSinceServerStarted;
	}
	public int getFreeMemory() {
		return freeMemory;
	}
	public void setFreeMemory(int freeMemory) {
		this.freeMemory = freeMemory;
	}
	public Calendar getRecordtime() {
		return recordtime;
	}
	public void setRecordtime(Calendar recordtime) {
		this.recordtime = recordtime;
	}
}
