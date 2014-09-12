package com.afunms.alarm.model;

import com.afunms.common.base.BaseVo;

public class AlarmWay extends BaseVo{
	
	private int id;
	
	/**
	 * 名称
	 */
	private String name;
	
	/**
	 * 描述
	 */
	private String description;
	
	/**
	 * 是否为默认方式
	 */
	private String isDefault;
	
	/**
	 * 页面是否告警
	 */
	private String isPageAlarm;
	
	/**
	 * 声音是否告警
	 */
	private String isSoundAlarm;
	
	/**
	 * 电话是否告警
	 */
	private String isPhoneAlarm;
	
	/**
	 * 短信是否告警
	 */
	private String isSMSAlarm;
	
	/**
	 * 邮件是否告警
	 */
	private String isMailAlarm;
	/**
	 * 邮件是否告警
	 */
	private String isDesktopAlarm;

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
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the isDefault
	 */
	public String getIsDefault() {
		return isDefault;
	}

	/**
	 * @param isDefault the isDefault to set
	 */
	public void setIsDefault(String isDefault) {
		this.isDefault = isDefault;
	}

	/**
	 * @return the isPageAlarm
	 */
	public String getIsPageAlarm() {
		return isPageAlarm;
	}

	/**
	 * @param isPageAlarm the isPageAlarm to set
	 */
	public void setIsPageAlarm(String isPageAlarm) {
		this.isPageAlarm = isPageAlarm;
	}

	/**
	 * @return the isSoundAlarm
	 */
	public String getIsSoundAlarm() {
		return isSoundAlarm;
	}

	/**
	 * @param isSoundAlarm the isSoundAlarm to set
	 */
	public void setIsSoundAlarm(String isSoundAlarm) {
		this.isSoundAlarm = isSoundAlarm;
	}

	/**
	 * @return the isPhoneAlarm
	 */
	public String getIsPhoneAlarm() {
		return isPhoneAlarm;
	}

	/**
	 * @param isPhoneAlarm the isPhoneAlarm to set
	 */
	public void setIsPhoneAlarm(String isPhoneAlarm) {
		this.isPhoneAlarm = isPhoneAlarm;
	}

	/**
	 * @return the isSMSAlarm
	 */
	public String getIsSMSAlarm() {
		return isSMSAlarm;
	}

	/**
	 * @param isSMSAlarm the isSMSAlarm to set
	 */
	public void setIsSMSAlarm(String isSMSAlarm) {
		this.isSMSAlarm = isSMSAlarm;
	}

	/**
	 * @return the isMailAlarm
	 */
	public String getIsMailAlarm() {
		return isMailAlarm;
	}

	/**
	 * @param isMailAlarm the isMailAlarm to set
	 */
	public void setIsMailAlarm(String isMailAlarm) {
		this.isMailAlarm = isMailAlarm;
	}

	/**
	 * @return the isDesktopAlarm
	 */
	public String getIsDesktopAlarm() {
		return isDesktopAlarm;
	}

	/**
	 * @param isDesktopAlarm the isDesktopAlarm to set
	 */
	public void setIsDesktopAlarm(String isDesktopAlarm) {
		this.isDesktopAlarm = isDesktopAlarm;
	}
	

}
