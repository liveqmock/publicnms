package com.afunms.config.model;

import com.afunms.common.base.BaseVo;


public class StrategyIp extends BaseVo{
private int id;
private int StrategyId;
private String strategyName;
private String ip;
private String deviceType;
private int availability;

public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}
public int getStrategyId() {
	return StrategyId;
}
public void setStrategyId(int strategyId) {
	StrategyId = strategyId;
}

public String getStrategyName() {
	return strategyName;
}
public void setStrategyName(String strategyName) {
	this.strategyName = strategyName;
}
public String getIp() {
	return ip;
}
public void setIp(String ip) {
	this.ip = ip;
}
public String getDeviceType() {
	return deviceType;
}
public void setDeviceType(String deviceType) {
	this.deviceType = deviceType;
}
public int getAvailability() {
	return availability;
}
public void setAvailability(int availability) {
	this.availability = availability;
}

}
