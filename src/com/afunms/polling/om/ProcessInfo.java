package com.afunms.polling.om;

public class ProcessInfo implements Cloneable
{
	private String pid;
	private Object CpuUtilization;
	private String Type;
	private String StartTime;
	private Object CpuTime;
	private String USER;
	private Object MemoryUtilization;
	private String Name;
	private String Status;
	private Object Memory;
	private String threadCount;
	private String handleCount;
	public String getThreadCount() {
		return threadCount;
	}
	public void setThreadCount(String threadCount) {
		this.threadCount = threadCount;
	}
	public String getHandleCount() {
		return handleCount;
	}
	public void setHandleCount(String handleCount) {
		this.handleCount = handleCount;
	}
	private int count;
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getType() {
		return Type;
	}
	public void setType(String type) {
		Type = type;
	}
	public String getStartTime() {
		return StartTime;
	}
	public void setStartTime(String startTime) {
		StartTime = startTime;
	}
	
	public String getUSER() {
		return USER;
	}
	public void setUSER(String user) {
		USER = user;
	}
	
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getStatus() {
		return Status;
	}
	public void setStatus(String status) {
		Status = status;
	}
	public Object getCpuUtilization() {
		return CpuUtilization;
	}
	public void setCpuUtilization(Object cpuUtilization) {
		CpuUtilization = cpuUtilization;
	}
	public Object getCpuTime() {
		return CpuTime;
	}
	public void setCpuTime(Object cpuTime) {
		CpuTime = cpuTime;
	}
	public Object getMemoryUtilization() {
		return MemoryUtilization;
	}
	public void setMemoryUtilization(Object memoryUtilization) {
		MemoryUtilization = memoryUtilization;
	}
	public Object getMemory() {
		return Memory;
	}
	public void setMemory(Object memory) {
		Memory = memory;
	}
	public ProcessInfo clone()
	{
		ProcessInfo p = null;
		try
		{
			p = (ProcessInfo)super.clone();
			return p;
		}catch(Exception e){e.printStackTrace();return p;}
	}
	
}
