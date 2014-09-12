package com.afunms.application.model;

import com.afunms.common.base.BaseVo;

/**
 * @author HONGLI E-mail: ty564457881@163.com
 * @version 创建时间：Aug 16, 2011 3:26:49 PM
 * 类说明 :性能面板和指标告警信息模型
 */
public class PerformancePanelIndicatorsAlarmModel extends BaseVo{

	private String id;
	
	/**
	 * 设备的id
	 */
	private String deviceId;
	
	/**
	 * 设备的描述
	 */
	private String deviceType;
	
	/**
	 * 指标的名称
	 */
	private String indicatorName;
	
	/**
	 * 指标的描述
	 */
	private String indicatorDesc;
	
	/**
	 * 告警等级
	 */
	private String alarmLevel;
	
	/**
	 * 告警描述
	 */
	private String alarmDesc;
	
	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getAlarmLevel() {
		return alarmLevel;
	}

	public void setAlarmLevel(String alarmLevel) {
		this.alarmLevel = alarmLevel;
	}

	public String getAlarmDesc() {
		return alarmDesc;
	}

	public void setAlarmDesc(String alarmDesc) {
		this.alarmDesc = alarmDesc;
	}

	public String getIndicatorName() {
		return indicatorName;
	}

	public void setIndicatorName(String indicatorName) {
		this.indicatorName = indicatorName;
	}

	public String getIndicatorDesc() {
		return indicatorDesc;
	}

	public void setIndicatorDesc(String indicatorDesc) {
		this.indicatorDesc = indicatorDesc;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}

