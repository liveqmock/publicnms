package com.afunms.detail.reomte.model;

import com.afunms.common.base.BaseVo;

public class ProcessInfo extends BaseVo {
	
	private String pid;					// 进程id
	
	private String name; 				// 进程名称
	
	private String count;				// 进程个数
	
	private String type;				// 进程类型
	
	private String cpuTime;				// CPU时间
	
	private String memoryUtilization;	// 内存占用率
	
	private String memory;				// 内存占用量
	
	private String status;				// 当前状态
	
	private String averageUSecs;		// 平均消耗CPU运行时间(光纤交换机)
	
	private String extPriorityRev;		// 优先级(光纤交换机)
	
	private String runtime;				// 运行时间(光纤交换机)

	/**
	 * @return the pid
	 */
	public String getPid() {
		return pid;
	}

	/**
	 * @param pid the pid to set
	 */
	public void setPid(String pid) {
		this.pid = pid;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the count
	 */
	public String getCount() {
		return count;
	}

	/**
	 * @param count the count to set
	 */
	public void setCount(String count) {
		this.count = count;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the cpuTime
	 */
	public String getCpuTime() {
		return cpuTime;
	}

	/**
	 * @param cpuTime the cpuTime to set
	 */
	public void setCpuTime(String cpuTime) {
		this.cpuTime = cpuTime;
	}

	/**
	 * @return the memoryUtilization
	 */
	public String getMemoryUtilization() {
		return memoryUtilization;
	}

	/**
	 * @param memoryUtilization the memoryUtilization to set
	 */
	public void setMemoryUtilization(String memoryUtilization) {
		this.memoryUtilization = memoryUtilization;
	}

	/**
	 * @return the memory
	 */
	public String getMemory() {
		return memory;
	}

	/**
	 * @param memory the memory to set
	 */
	public void setMemory(String memory) {
		this.memory = memory;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the averageUSecs
	 */
	public String getAverageUSecs() {
		return averageUSecs;
	}

	/**
	 * @param averageUSecs the averageUSecs to set
	 */
	public void setAverageUSecs(String averageUSecs) {
		this.averageUSecs = averageUSecs;
	}

	/**
	 * @return the extPriorityRev
	 */
	public String getExtPriorityRev() {
		return extPriorityRev;
	}

	/**
	 * @param extPriorityRev the extPriorityRev to set
	 */
	public void setExtPriorityRev(String extPriorityRev) {
		this.extPriorityRev = extPriorityRev;
	}

	/**
	 * @return the runtime
	 */
	public String getRuntime() {
		return runtime;
	}

	/**
	 * @param runtime the runtime to set
	 */
	public void setRuntime(String runtime) {
		this.runtime = runtime;
	}

	
}
