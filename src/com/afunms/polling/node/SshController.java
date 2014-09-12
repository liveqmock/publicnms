package com.afunms.polling.node;
/**
 * 
 * @descrition TODO
 * @author wangxiangyong
 * @date Jun 15, 2013 6:06:04 PM
 */
public class SshController {
	private String id;
	private String SerialNum;
	private String hardwareVersion;
	private String cpldVersion;
	private String mac;
	private String wwnn;
	private String ip;
	private String mask;
	private String gateway;
	private String disks;
	private String vdisks;
	private String cache;
	private String hostPorts;
	private String diskChannels;
	private String diskBusType;
	private String status;
	private String failedOver;
	private String failOverReason;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSerialNum() {
		return SerialNum;
	}

	public void setSerialNum(String serialNum) {
		SerialNum = serialNum;
	}

	public String getHardwareVersion() {
		return hardwareVersion;
	}

	public void setHardwareVersion(String hardwareVersion) {
		this.hardwareVersion = hardwareVersion;
	}

	public String getCpldVersion() {
		return cpldVersion;
	}

	public void setCpldVersion(String cpldVersion) {
		this.cpldVersion = cpldVersion;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getWwnn() {
		return wwnn;
	}

	public void setWwnn(String wwnn) {
		this.wwnn = wwnn;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getMask() {
		return mask;
	}

	public void setMask(String mask) {
		this.mask = mask;
	}

	public String getGateway() {
		return gateway;
	}

	public void setGateway(String gateway) {
		this.gateway = gateway;
	}

	public String getDisks() {
		return disks;
	}

	public void setDisks(String disks) {
		this.disks = disks;
	}

	public String getVdisks() {
		return vdisks;
	}

	public void setVdisks(String vdisks) {
		this.vdisks = vdisks;
	}

	public String getCache() {
		return cache;
	}

	public void setCache(String cache) {
		this.cache = cache;
	}

	public String getHostPorts() {
		return hostPorts;
	}

	public void setHostPorts(String hostPorts) {
		this.hostPorts = hostPorts;
	}

	public String getDiskChannels() {
		return diskChannels;
	}

	public void setDiskChannels(String diskChannels) {
		this.diskChannels = diskChannels;
	}

	public String getDiskBusType() {
		return diskBusType;
	}

	public void setDiskBusType(String diskBusType) {
		this.diskBusType = diskBusType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFailedOver() {
		return failedOver;
	}

	public void setFailedOver(String failedOver) {
		this.failedOver = failedOver;
	}

	public String getFailOverReason() {
		return failOverReason;
	}

	public void setFailOverReason(String failOverReason) {
		this.failOverReason = failOverReason;
	}

}
