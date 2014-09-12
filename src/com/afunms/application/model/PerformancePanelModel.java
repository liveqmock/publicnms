package com.afunms.application.model;

import com.afunms.common.base.BaseVo;

/**
 * @author HONGLI E-mail: ty564457881@163.com
 * @version 创建时间：Aug 16, 2011 3:26:49 PM
 * 类说明 :性能面板模型类
 */
public class PerformancePanelModel extends BaseVo{
	private String id;
	/**
	 * 性能面板名称
	 */
	private String name;
	
	/**
	 * 面板中设备类型 
	 */
	private String deviceType;
	
	/**
	 * 设备ID
	 */
	private String deviceId;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
}

