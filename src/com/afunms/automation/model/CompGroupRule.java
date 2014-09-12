package com.afunms.automation.model;


import com.afunms.common.base.BaseVo;

public class CompGroupRule extends BaseVo{
/**
	 * 
	 */
	private static final long serialVersionUID = -2281544712923018558L;
private int id;
private String name;
private String description;
private String deviceType;
private String ruleId;
private String createdBy;
private String createdTime;
private String lastModifiedBy;
private String lastModifiedTime;
public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getDescription() {
	return description;
}
public void setDescription(String description) {
	this.description = description;
}
public String getDeviceType() {
	return deviceType;
}
public void setDeviceType(String deviceType) {
	this.deviceType = deviceType;
}
public String getRuleId() {
	return ruleId;
}
public void setRuleId(String ruleId) {
	this.ruleId = ruleId;
}
public String getCreatedBy() {
	return createdBy;
}
public void setCreatedBy(String createdBy) {
	this.createdBy = createdBy;
}
public String getCreatedTime() {
	return createdTime;
}
public void setCreatedTime(String createdTime) {
	this.createdTime = createdTime;
}
public String getLastModifiedBy() {
	return lastModifiedBy;
}
public void setLastModifiedBy(String lastModifiedBy) {
	this.lastModifiedBy = lastModifiedBy;
}
public String getLastModifiedTime() {
	return lastModifiedTime;
}
public void setLastModifiedTime(String lastModifiedTime) {
	this.lastModifiedTime = lastModifiedTime;
}


}
