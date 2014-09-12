package com.afunms.alarm.model;

import com.afunms.common.base.BaseVo;

public class AlarmWayDetail extends BaseVo{
	
	private int id;
	
	/**
	 * 告警方式id
	 */
	private String alarmWayId;
	
	/**
	 * 告警方式的种类 ： 页面 , 声音 , 邮件 , 短信 , 电话 , ...
	 */
	private String alarmCategory;
	
	/**
	 * 类型 ： 按月 , 按周
	 */
	private String dateType;
	
	/**
	 * 连续发送的次数
	 */
	private String sendTimes;
	
	/**
	 * 开始日期
	 */
	private String startDate;
	
	/**
	 * 结束日期
	 */
	private String endDate;
	
	/**
	 * 开始时间
	 */
	private String startTime;
	
	/**
	 * 结束时间
	 */
	private String endTime;
	
	/**
	 * 接受人 id
	 */
	private String userIds;

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
	 * @return the alarmWayId
	 */
	public String getAlarmWayId() {
		return alarmWayId;
	}

	/**
	 * @param alarmWayId the alarmWayId to set
	 */
	public void setAlarmWayId(String alarmWayId) {
		this.alarmWayId = alarmWayId;
	}

	/**
	 * @return the alarmCategory
	 */
	public String getAlarmCategory() {
		return alarmCategory;
	}

	/**
	 * @param alarmCategory the alarmCategory to set
	 */
	public void setAlarmCategory(String alarmCategory) {
		this.alarmCategory = alarmCategory;
	}

	/**
	 * @return the dateType
	 */
	public String getDateType() {
		return dateType;
	}

	/**
	 * @param dateType the dateType to set
	 */
	public void setDateType(String dateType) {
		this.dateType = dateType;
	}

	/**
	 * @return the sendTimes
	 */
	public String getSendTimes() {
		return sendTimes;
	}

	/**
	 * @param sendTimes the sendTimes to set
	 */
	public void setSendTimes(String sendTimes) {
		this.sendTimes = sendTimes;
	}

	/**
	 * @return the startDate
	 */
	public String getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the endDate
	 */
	public String getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return the startTime
	 */
	public String getStartTime() {
		return startTime;
	}

	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	/**
	 * @return the endTime
	 */
	public String getEndTime() {
		return endTime;
	}

	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	/**
	 * @return the userIds
	 */
	public String getUserIds() {
		return userIds;
	}

	/**
	 * @param userIds the userIds to set
	 */
	public void setUserIds(String userIds) {
		this.userIds = userIds;
	}
	
	

	
	
}
