package com.afunms.application.resinmonitor;

public class LoadBalance {
	private String server;
	private String address;
	private String status;
	private String active;
	private String idle;
	private String miss;
	private String load;
	private String latency;
	private String failure;
	private String busy;
	public String getServer() {
		return server;
	}
	public void setServer(String server) {
		this.server = server;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getActive() {
		return active;
	}
	public void setActive(String active) {
		this.active = active;
	}
	public String getIdle() {
		return idle;
	}
	public void setIdle(String idle) {
		this.idle = idle;
	}
	public String getMiss() {
		return miss;
	}
	public void setMiss(String miss) {
		this.miss = miss;
	}
	public String getLoad() {
		return load;
	}
	public void setLoad(String load) {
		this.load = load;
	}
	public String getLatency() {
		return latency;
	}
	public void setLatency(String latency) {
		this.latency = latency;
	}
	public String getFailure() {
		return failure;
	}
	public void setFailure(String failure) {
		this.failure = failure;
	}
	public String getBusy() {
		return busy;
	}
	public void setBusy(String busy) {
		this.busy = busy;
	}
	
}
