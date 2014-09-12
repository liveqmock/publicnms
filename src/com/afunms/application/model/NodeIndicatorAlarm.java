package com.afunms.application.model;

import com.afunms.common.base.BaseVo;

/**
 * @author HONGLI E-mail: ty564457881@163.com
 * @version 创建时间：Aug 17, 2011 2:56:07 PM
 * 类说明 : 设备告警信息模型类
 */
public class NodeIndicatorAlarm extends BaseVo{

	private String id;
	
	/**
	 * 设备id
	 */
	private String deviceId;
	
	/**
	 * 设备类型
	 */
	private String deviceType;
	
	/**
	 * 指标英文名称
	 */
	private String indicatorName;
	
	/**
	 * 告警级别
	 */
	private String alarmLevel;
	
	/**
	 * 告警描述
	 */
	private String alarmDesc;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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

	public String getIndicatorName() {
		return indicatorName;
	}

	public void setIndicatorName(String indicatorName) {
		this.indicatorName = indicatorName;
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
}
