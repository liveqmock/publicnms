package com.afunms.application.wasmonitor;

import com.afunms.common.base.BaseVo;

public class Was5jdbc extends BaseVo{
	String ipaddress;
    java.util.Calendar recordtime;
    int createCount;
	int closeCount;
	int poolSize;
	int freePoolSize;
	int waitingThreadCount;
	float percentUsed;
	int useTime;
	int waitTime;
	int allocateCount;
	int prepStmtCacheDiscardCount;
	int jdbcTime;
	int faultCount;
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
	public int getCreateCount() {
		return createCount;
	}
	public void setCreateCount(int createCount) {
		this.createCount = createCount;
	}
	public int getCloseCount() {
		return closeCount;
	}
	public void setCloseCount(int closeCount) {
		this.closeCount = closeCount;
	}
	public int getPoolSize() {
		return poolSize;
	}
	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}
	public int getFreePoolSize() {
		return freePoolSize;
	}
	public void setFreePoolSize(int freePoolSize) {
		this.freePoolSize = freePoolSize;
	}
	public int getWaitingThreadCount() {
		return waitingThreadCount;
	}
	public void setWaitingThreadCount(int waitingThreadCount) {
		this.waitingThreadCount = waitingThreadCount;
	}
	public float getPercentUsed() {
		return percentUsed;
	}
	public void setPercentUsed(float percentUsed) {
		this.percentUsed = percentUsed;
	}
	public int getUseTime() {
		return useTime;
	}
	public void setUseTime(int useTime) {
		this.useTime = useTime;
	}
	public int getWaitTime() {
		return waitTime;
	}
	public void setWaitTime(int waitTime) {
		this.waitTime = waitTime;
	}
	public int getAllocateCount() {
		return allocateCount;
	}
	public void setAllocateCount(int allocateCount) {
		this.allocateCount = allocateCount;
	}
	public int getPrepStmtCacheDiscardCount() {
		return prepStmtCacheDiscardCount;
	}
	public void setPrepStmtCacheDiscardCount(int prepStmtCacheDiscardCount) {
		this.prepStmtCacheDiscardCount = prepStmtCacheDiscardCount;
	}
	public int getJdbcTime() {
		return jdbcTime;
	}
	public void setJdbcTime(int jdbcTime) {
		this.jdbcTime = jdbcTime;
	}
	public int getFaultCount() {
		return faultCount;
	}
	public void setFaultCount(int faultCount) {
		this.faultCount = faultCount;
	}
	
}
