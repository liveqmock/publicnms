package com.afunms.automation.model;

import com.afunms.common.base.BaseVo;

/**
 * 
 * @descrition 合规性规则MODEL
 * @author wangxiangyong
 * @date Sep 1, 2014 2:07:43 PM
 */
public class CompRule extends BaseVo {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3115757664116106783L;
	private int id;// 规则ID
	private String device_type;// 设备类型(h3c,cisco)
	private String comprule_name;// 规则名称
	private String description;// 规则描述
	private int violation_severity;// 违反严重度(0-严重,1-重要,2-警告
	private int select_type;// 选择标准(0-简单，1-高级，2-高级自定义)
	private String remediation_descr;// 补充描述
	private String created_by;// 创建人
	private String create_time;// 创建时间
	private String last_modified_by;// 最近一次修改人
	private String last_modified_time;// 最近一次修改时间
	private String rule_content;// 规则内容
	// private String beiyong1;
	// private String beiyong2;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getComprule_name() {
		return comprule_name;
	}

	public void setComprule_name(String comprule_name) {
		this.comprule_name = comprule_name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getViolation_severity() {
		return violation_severity;
	}

	public void setViolation_severity(int violation_severity) {
		this.violation_severity = violation_severity;
	}

	public int getSelect_type() {
		return select_type;
	}

	public void setSelect_type(int select_type) {
		this.select_type = select_type;
	}

	public String getRemediation_descr() {
		return remediation_descr;
	}

	public void setRemediation_descr(String remediation_descr) {
		this.remediation_descr = remediation_descr;
	}

	public String getDevice_type() {
		return device_type;
	}

	public void setDevice_type(String device_type) {
		this.device_type = device_type;
	}

	public String getCreated_by() {
		return created_by;
	}

	public void setCreated_by(String created_by) {
		this.created_by = created_by;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public String getLast_modified_by() {
		return last_modified_by;
	}

	public void setLast_modified_by(String last_modified_by) {
		this.last_modified_by = last_modified_by;
	}

	public String getLast_modified_time() {
		return last_modified_time;
	}

	public void setLast_modified_time(String last_modified_time) {
		this.last_modified_time = last_modified_time;
	}

	public String getRule_content() {
		return rule_content;
	}

	public void setRule_content(String rule_content) {
		this.rule_content = rule_content;
	}

}
