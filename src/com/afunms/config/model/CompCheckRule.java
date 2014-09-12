package com.afunms.config.model;

import com.afunms.common.base.BaseVo;

public class CompCheckRule extends BaseVo{
private  int id;
private int strategyId;
private int groupId;
private int ruleId;
private String ip;
private int isViolation;//是否违反规则
private int relation;////关系（与或）

private int isContain;////是否包含
private String content;//违反的规则内容

public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}
public int getStrategyId() {
	return strategyId;
}
public void setStrategyId(int strategyId) {
	this.strategyId = strategyId;
}
public int getGroupId() {
	return groupId;
}
public void setGroupId(int groupId) {
	this.groupId = groupId;
}
public int getRuleId() {
	return ruleId;
}
public void setRuleId(int ruleId) {
	this.ruleId = ruleId;
}

public String getIp() {
	return ip;
}
public void setIp(String ip) {
	this.ip = ip;
}
public int getIsViolation() {
	return isViolation;
}
public void setIsViolation(int isViolation) {
	this.isViolation = isViolation;
}

public int getRelation() {
	return relation;
}
public void setRelation(int relation) {
	this.relation = relation;
}
public int getIsContain() {
	return isContain;
}
public void setIsContain(int isContain) {
	this.isContain = isContain;
}
public String getContent() {
	return content;
}
public void setContent(String content) {
	this.content = content;
}

}
