package com.afunms.inform.model;

public class NetworkPerformance
{ 
	private int nodeId;
	private String ipAddress;
	private String alias;
	private float cpuValue;
	private float memValue;
	private float ifUtil;
	
	/**
	 * @return the ifUtil
	 */
	public float getIfUtil() {
		return ifUtil;
	}

	/**
	 * @param ifUtil the ifUtil to set
	 */
	public void setIfUtil(float ifUtil) {
		this.ifUtil = ifUtil;
	}

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
