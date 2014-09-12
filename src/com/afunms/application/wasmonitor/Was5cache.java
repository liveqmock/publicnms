package com.afunms.application.wasmonitor;

import com.afunms.common.base.BaseVo;

public class Was5cache extends BaseVo{
	int  inMemoryCacheCount;
	int  maxInMemoryCacheCount;
	int timeoutInvalidationCount;
	String ipaddress;
    java.util.Calendar recordtime;
	public int getInMemoryCacheCount() {
		return inMemoryCacheCount;
	}
	public void setInMemoryCacheCount(int inMemoryCacheCount) {
		this.inMemoryCacheCount = inMemoryCacheCount;
	}
	public int getMaxInMemoryCacheCount() {
		return maxInMemoryCacheCount;
	}
	public void setMaxInMemoryCacheCount(int maxInMemoryCacheCount) {
		this.maxInMemoryCacheCount = maxInMemoryCacheCount;
	}
	public int getTimeoutInvalidationCount() {
		return timeoutInvalidationCount;
	}
	public void setTimeoutInvalidationCount(int timeoutInvalidationCount) {
		this.timeoutInvalidationCount = timeoutInvalidationCount;
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
