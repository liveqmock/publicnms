package com.afunms.application.model;

import com.afunms.common.base.BaseVo;

public class JobForAS400GroupDetail extends BaseVo{
	
	private int id;
	
	private String groupId;
	
	/**
	 * 个数
	 */
	private String num;
	
	/**
	 * 作业名称
	 */
	private String name;
	
	/**
	 * 状态
	 */
	private String status;
	
	/**
	 * 活动状态类型
	 */
	private String activeStatusType;
	
	/**
	 * 活动状态
	 */
	private String activeStatus;

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
	 * @return the num
	 */
	public String getNum() {
		return num;
	}

	/**
	 * @param num the num to set
	 */
	public void setNum(String num) {
		this.num = num;
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
	 * @return the activeStatusType
	 */
	public String getActiveStatusType() {
		return activeStatusType;
	}

	/**
	 * @param activeStatusType the activeStatusType to set
	 */
	public void setActiveStatusType(String activeStatusType) {
		this.activeStatusType = activeStatusType;
	}

	/**
	 * @return the activeStatus
	 */
	public String getActiveStatus() {
		return activeStatus;
	}

	/**
	 * @param activeStatus the activeStatus to set
	 */
	public void setActiveStatus(String activeStatus) {
		this.activeStatus = activeStatus;
	}

	
}	
