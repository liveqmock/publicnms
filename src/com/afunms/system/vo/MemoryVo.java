package com.afunms.system.vo;

public class MemoryVo {
	
	private String date;
	
	private String virtualMemory;
	
	private String physicalMemory;
	
	public String getDate() {
		return date;
	}
	
	public void setDate(String date) {
		this.date = date;
	}

	public String getPhysicalMemory() {
		return physicalMemory;
	}

	public void setPhysicalMemory(String physicalMemory) {
		this.physicalMemory = physicalMemory;
	}

	public String getVirtualMemory() {
		return virtualMemory;
	}

	public void setVirtualMemory(String virtualMemory) {
		this.virtualMemory = virtualMemory;
	}
	
}