package com.afunms.application.wasmonitor;

import com.afunms.common.base.BaseVo;

public class Was5trans extends BaseVo {
	int activeCount;
	int committedCount;
	int rolledbackCount;
	int globalTranTime;
	int globalBegunCount;
	int localBegunCount;
	int localActiveCount;
	int localTranTime;
	int localTimeoutCount;
	int localRolledbackCount;
	int globalTimeoutCount;
	String ipaddress;
    java.util.Calendar recordtime;
	public int getActiveCount() {
		return activeCount;
	}
	public void setActiveCount(int activeCount) {
		this.activeCount = activeCount;
	}
	public int getCommittedCount() {
		return committedCount;
	}
	public void setCommittedCount(int committedCount) {
		this.committedCount = committedCount;
	}
	public int getRolledbackCount() {
		return rolledbackCount;
	}
	public void setRolledbackCount(int rolledbackCount) {
		this.rolledbackCount = rolledbackCount;
	}
	public int getGlobalTranTime() {
		return globalTranTime;
	}
	public void setGlobalTranTime(int globalTranTime) {
		this.globalTranTime = globalTranTime;
	}
	public int getGlobalBegunCount() {
		return globalBegunCount;
	}
	public void setGlobalBegunCount(int globalBegunCount) {
		this.globalBegunCount = globalBegunCount;
	}
	public int getLocalBegunCount() {
		return localBegunCount;
	}
	public void setLocalBegunCount(int localBegunCount) {
		this.localBegunCount = localBegunCount;
	}
	public int getLocalActiveCount() {
		return localActiveCount;
	}
	public void setLocalActiveCount(int localActiveCount) {
		this.localActiveCount = localActiveCount;
	}
	public int getLocalTranTime() {
		return localTranTime;
	}
	public void setLocalTranTime(int localTranTime) {
		this.localTranTime = localTranTime;
	}
	public int getLocalTimeoutCount() {
		return localTimeoutCount;
	}
	public void setLocalTimeoutCount(int localTimeoutCount) {
		this.localTimeoutCount = localTimeoutCount;
	}
	public int getLocalRolledbackCount() {
		return localRolledbackCount;
	}
	public void setLocalRolledbackCount(int localRolledbackCount) {
		this.localRolledbackCount = localRolledbackCount;
	}
	public int getGlobalTimeoutCount() {
		return globalTimeoutCount;
	}
	public void setGlobalTimeoutCount(int globalTimeoutCount) {
		this.globalTimeoutCount = globalTimeoutCount;
	}
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
    
}
