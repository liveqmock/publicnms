package com.afunms.application.wasmonitor;

import com.afunms.common.base.BaseVo;

public class Was5session extends BaseVo{
	String ipaddress;
    java.util.Calendar recordtime;
	int liveCount;
	int createCount;
	int invalidateCount;
	int lifeTime;
	int activeCount;
	int timeoutInvalidationCount;
	public String getIpaddress() {
		return ipaddress;
	}
	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}
	public java.util.Calendar getRecordtime() {
		return recordtime;
	}
	public void setRecordtime(java.util.Calendar recordtime) {
		this.recordtime = recordtime;
	}
	public int getLiveCount() {
		return liveCount;
	}
	public void setLiveCount(int liveCount) {
		this.liveCount = liveCount;
	}
	public int getCreateCount() {
		return createCount;
	}
	public void setCreateCount(int createCount) {
		this.createCount = createCount;
	}
	public int getInvalidateCount() {
		return invalidateCount;
	}
	public void setInvalidateCount(int invalidateCount) {
		this.invalidateCount = invalidateCount;
	}
	public int getLifeTime() {
		return lifeTime;
	}
	public void setLifeTime(int lifeTime) {
		this.lifeTime = lifeTime;
	}
	public int getActiveCount() {
		return activeCount;
	}
	public void setActiveCount(int activeCount) {
		this.activeCount = activeCount;
	}
	public int getTimeoutInvalidationCount() {
		return timeoutInvalidationCount;
	}
	public void setTimeoutInvalidationCount(int timeoutInvalidationCount) {
		this.timeoutInvalidationCount = timeoutInvalidationCount;
	}
	
	
}
