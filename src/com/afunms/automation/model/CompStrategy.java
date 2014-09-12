package com.afunms.automation.model;

import com.afunms.common.base.BaseVo;
/**
 * 
 * @descrition 合规性策略MODEL
 * @author wangxiangyong
 * @date Sep 1, 2014 2:13:45 PM
 */
public class CompStrategy extends BaseVo {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5306248726145256322L;
	private int id; // 策略ID
	private String name; // 策略名称
	private String description;// 描述
	private int type; // 类型
	private int violateType;// 策略违反类型
	private String groupId; // 关联规则组ID集合
	private String createBy; // 创建人
	private String createTime; // 创建时间
	private String lastModifiedBy;// 最近一次修改人
	private String lastModifiedTime;// 最精一次修改时间

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

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getViolateType() {
		return violateType;
	}

	public void setViolateType(int violateType) {
		this.violateType = violateType;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
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
