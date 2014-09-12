package com.afunms.application.resinmonitor;

public class ConnectionPool {
	private String name;
	private String active;
	private String idle;
	private String created;
	private String failed;
	private String max_connections;
	private String idle_time;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public String getCreated() {
		return created;
	}
	public void setCreated(String created) {
		this.created = created;
	}
	public String getFailed() {
		return failed;
	}
	public void setFailed(String failed) {
		this.failed = failed;
	}
	public String getMax_connections() {
		return max_connections;
	}
	public void setMax_connections(String max_connections) {
		this.max_connections = max_connections;
	}
	public String getIdle_time() {
		return idle_time;
	}
	public void setIdle_time(String idle_time) {
		this.idle_time = idle_time;
	}
	
}
