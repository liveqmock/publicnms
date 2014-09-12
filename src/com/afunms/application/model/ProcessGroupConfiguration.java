package com.afunms.application.model;

import com.afunms.common.base.BaseVo;

public class ProcessGroupConfiguration extends BaseVo{
	
	private int id;
	
	private String groupId;
	
	private String name;
	
	private String times;
	
	/**
	 * 进程的状态   1:黑名单  0:白名单
	 */
	private String status;

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the groupId
	 */
	public String getGroupId() {
		return groupId;
	}

	/**
	 * @param groupId the groupId to set
	 */
	public void setGroupId(String groupId) {
		this.groupId = groupId;
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
	 * @return the times
	 */
	public String getTimes() {
		return times;
	}

	/**
	 * @param times the times to set
	 */
	public void setTimes(String times) {
		this.times = times;
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
	
	
	
}	
