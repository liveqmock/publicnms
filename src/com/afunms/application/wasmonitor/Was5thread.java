package com.afunms.application.wasmonitor;

import com.afunms.common.base.BaseVo;

public class Was5thread extends BaseVo {
	String ipaddress;
    java.util.Calendar recordtime;
    int createCount =0 ;
	int destroyCount  =0;
	int poolSize  =0;
	int activeCount  =0;
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
	public int getDestroyCount() {
		return destroyCount;
	}
	public void setDestroyCount(int destroyCount) {
		this.destroyCount = destroyCount;
	}
	public int getPoolSize() {
		return poolSize;
	}
	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}
	public int getActiveCount() {
		return activeCount;
	}
	public void setActiveCount(int activeCount) {
		this.activeCount = activeCount;
	}
	
	
}
