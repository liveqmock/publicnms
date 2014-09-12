package com.afunms.config.model;

import java.sql.Timestamp;

public class ConfiguringDevice 
{
	private int id;
	private String ipaddress;
	private String alias;
	private int category;//设备类型，交换机 路由器 其他
	private Timestamp lastUpdateTime;
	private String prompt;
	private int enablevpn;
	private int isSynchronized;//1同步 0不同步
	private String deviceRender;//具体设备厂商，h3c cisco
	
	
	public String getDeviceRender() {
		return deviceRender;
	}
	public void setDeviceRender(String deviceRender) {
		this.deviceRender = deviceRender;
	}
	public int getIsSynchronized() 
	{
		return isSynchronized;
	}
	public void setIsSynchronized(int isSynchronized) {
		this.isSynchronized = isSynchronized;
	}
	public String getPrompt() {
		return prompt;
	}
	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}
	public int getEnablevpn() {
		return enablevpn;
	}
	public void setEnablevpn(int enablevpn) {
		this.enablevpn = enablevpn;
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
