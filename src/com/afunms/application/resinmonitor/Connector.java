package com.afunms.application.resinmonitor;

public class Connector {
	private String server;

	private String address;

	private String status;

	private String active;

	private String idle;

	private String connectionMiss;

	private String load;

	private String latency;
 
	private String failures;

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

	public String getConnectionMiss() {
		return connectionMiss;
	}

	public void setConnectionMiss(String connectionMiss) {
		this.connectionMiss = connectionMiss;
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

	public String getFailures() {
		return failures;
	}

	public void setFailures(String failures) {
		this.failures = failures;
	}

	public String getBusy() {
		return busy;
	}

	public void setBusy(String busy) {
		this.busy = busy;
	}
	
}
