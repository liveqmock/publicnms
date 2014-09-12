package com.afunms.application.wasmonitor;

import com.afunms.common.base.BaseVo;

public class Was5jvminfo  extends BaseVo{
	int heapSize;//Java虚拟机运行时总内存
	int freeMemory; // java虚拟机运行时空闲的内存数量
	int usedMemory;//Java虚拟机运行时使用的内存数量
	int upTime;//已经运行的时间数
	int memPer;
	String ipaddress;
    java.util.Calendar recordtime;
	public int getHeapSize() {
		return heapSize;
	}
	public void setHeapSize(int heapSize) {
		this.heapSize = heapSize;
	}
	public int getFreeMemory() {
		return freeMemory;
	}
	public void setFreeMemory(int freeMemory) {
		this.freeMemory = freeMemory;
	}
	public int getUsedMemory() {
		return usedMemory;
	}
	public void setUsedMemory(int usedMemory) {
		this.usedMemory = usedMemory;
	}
	public int getUpTime() {
		return upTime;
	}
	public void setUpTime(int upTime) {
		this.upTime = upTime;
	}
	public int getMemPer() {
		return memPer;
	}
	public void setMemPer(int memPer) {
		this.memPer = memPer;
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
