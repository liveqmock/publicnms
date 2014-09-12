package com.afunms.inform.model;

import com.afunms.common.base.BaseVo;

public class ServerPerformance extends BaseVo 
{
	private int nodeId;
	private String ipAddress;
	private String alias;
	private float cpuValue;
	private float memValue;
	private float diskValue;
		
	public String getAlias() {
		return alias;
	}
	
	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	public float getCpuValue() {
		return cpuValue;
	}
	
	public void setCpuValue(float cpuValue) {
		this.cpuValue = cpuValue;
	}
	
	public float getDiskValue() {
		return diskValue;
	}
	
	public void setDiskValue(float diskValue) {
		this.diskValue = diskValue;
	}
	
	public String getIpAddress() {
		return ipAddress;
	}
	
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	
	public float getMemValue() {
		return memValue;
	}
	
	public void setMemValue(float memValue) {
		this.memValue = memValue;
	}
	
	public int getNodeId() {
		return nodeId;
	}
	
	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}		
}
