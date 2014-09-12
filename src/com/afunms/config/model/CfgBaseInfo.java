package com.afunms.config.model;

import com.afunms.common.base.BaseVo;

public class CfgBaseInfo extends BaseVo{
private int id;
private String policyName;//策略名称
private String name;      //类名称
private String value;     //值
private String priority;  //级别
private String type;      //类型
private String collecttime;
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

public String getPolicyName() {
	return policyName;
}
public void setPolicyName(String policyName) {
	this.policyName = policyName;
}
public String getValue() {
	return value;
}
public void setValue(String value) {
	this.value = value;
}
public String getPriority() {
	return priority;
}
public void setPriority(String priority) {
	this.priority = priority;
}
public String getType() {
	return type;
}
public void setType(String type) {
	this.type = type;
}
public String getCollecttime() {
	return collecttime;
}
public void setCollecttime(String collecttime) {
	this.collecttime = collecttime;
}

}
