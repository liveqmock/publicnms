package com.afunms.application.resinmonitor;

public class ThreadPool {
	private int active;
	private int idle;
	private int total;
	private int thread_max;
	private int thread_idle_min;
	public int getActive() {
		return active;
	}
	public void setActive(int active) {
		this.active = active;
	}
	public int getIdle() {
		return idle;
	}
	public void setIdle(int idle) {
		this.idle = idle;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public int getThread_max() {
		return thread_max;
	}
	public void setThread_max(int thread_max) {
		this.thread_max = thread_max;
	}
	public int getThread_idle_min() {
		return thread_idle_min;
	}
	public void setThread_idle_min(int thread_idle_min) {
		this.thread_idle_min = thread_idle_min;
	}

}
