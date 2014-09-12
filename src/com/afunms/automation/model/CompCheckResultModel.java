package com.afunms.automation.model;



import com.afunms.common.base.BaseVo;

public class CompCheckResultModel extends BaseVo{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2439438360908737771L;

	private int id;
	
	private int strategyId;
	
	private String strategyName;
	
	private String ip;
	
	private int groupId;
	
	private String groupName;
	
	private int ruleId;
	
	private String ruleName;
	
	private String description;
	
	private int violationSeverity;//规则违反度(普通：0;重要：1；严重：2)
	
	private int isViolation;//是否违反规则
	private String checkTime;

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

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public int getRuleId() {
		return ruleId;
	}

	public void setRuleId(int ruleId) {
		this.ruleId = ruleId;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getViolationSeverity() {
		return violationSeverity;
	}

	public void setViolationSeverity(int violationSeverity) {
		this.violationSeverity = violationSeverity;
	}

	public int getIsViolation() {
		return isViolation;
	}

	public void setIsViolation(int isViolation) {
		this.isViolation = isViolation;
	}

	public String getCheckTime() {
		return checkTime;
	}

	public void setCheckTime(String checkTime) {
		this.checkTime = checkTime;
	}
}
