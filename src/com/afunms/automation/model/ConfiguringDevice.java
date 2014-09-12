package com.afunms.automation.model;


import java.sql.Timestamp;

import com.afunms.common.base.BaseVo;

public class ConfiguringDevice extends BaseVo
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 0L;
	private int id;
	private String ipaddress;
	private String alias;
	private int category;//设备类型，交换机 路由器 其他
	private Timestamp lastUpdateTime;
//	private String prompt;
//	private int enablevpn;
//	private int isSynchronized;//1同步 0不同步
	private String deviceRender;//具体设备厂商，h3c cisco
	
	
	public String getDeviceRender() {
		return deviceRender;
	}
	public void setDeviceRender(String deviceRender) {
		this.deviceRender = deviceRender;
	}
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getIpaddress() {
		return ipaddress;
	}
	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	public Timestamp getLastUpdateTime() {
		return lastUpdateTime;
	}
	public void setLastUpdateTime(Timestamp lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	public int getCategory() {
		return category;
	}
	public void setCategory(int category) {
		this.category = category;
	}
}
