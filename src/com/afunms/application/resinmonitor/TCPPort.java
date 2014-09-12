package com.afunms.application.resinmonitor;

public class TCPPort {
	private String listener;
	private String status;
	private String threadActive;
	private String threadIdle;
	private String threadTotal;
	private String keepaliveTotal;
	private String keepaliveThread;
	private String keepaliveSelect;
	private String keepaliveComet;
	
	public String getListener() {
		return listener;
	}
	public void setListener(String url) {
		this.listener = url;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getThreadActive() {
		return threadActive;
	}
	public void setThreadActive(String threadActive) {
		this.threadActive = threadActive;
	}
	public String getThreadIdle() {
		return threadIdle;
	}
	public void setThreadIdle(String threadIdle) {
		this.threadIdle = threadIdle;
	}
	public String getThreadTotal() {
		return threadTotal;
	}
	public void setThreadTotal(String threadTotal) {
		this.threadTotal = threadTotal;
	}
	public String getKeepaliveTotal() {
		return keepaliveTotal;
	}
	public void setKeepaliveTotal(String keepaliveTotal) {
		this.keepaliveTotal = keepaliveTotal;
	}
	public String getKeepaliveThread() {
		return keepaliveThread;
	}
	public void setKeepaliveThread(String keepaliveThread) {
		this.keepaliveThread = keepaliveThread;
	}
	public String getKeepaliveSelect() {
		return keepaliveSelect;
	}
	public void setKeepaliveSelect(String keepaliveSelect) {
		this.keepaliveSelect = keepaliveSelect;
	}
	public String getKeepaliveComet() {
		return keepaliveComet;
	}
	public void setKeepaliveComet(String keepaliveComet) {
		this.keepaliveComet = keepaliveComet;
	}
}
