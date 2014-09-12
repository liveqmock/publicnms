package com.afunms.application.model;

import com.afunms.common.base.BaseVo;

public class SubConfig extends BaseVo
{
	int id;
	int configid;
	String name;
	int categoryid;
	String subconf_desc;
	int subconf_num;
	String subconf_value;
	String subconf_unit;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getConfigid() {
		return configid;
	}
	public void setConfigid(int configid) {
		this.configid = configid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getCategoryid() {
		return categoryid;
	}
	public void setCategoryid(int categoryid) {
		this.categoryid = categoryid;
	}
	public String getSubconf_desc() {
		return subconf_desc;
	}
	public void setSubconf_desc(String subconf_desc) {
		this.subconf_desc = subconf_desc;
	}
	public int getSubconf_num() {
		return subconf_num;
	}
	public void setSubconf_num(int subconf_num) {
		this.subconf_num = subconf_num;
	}
	public String getSubconf_value() {
		return subconf_value;
	}
	public void setSubconf_value(String subconf_value) {
		this.subconf_value = subconf_value;
	}
	public String getSubconf_unit() {
		return subconf_unit;
	}
	public void setSubconf_unit(String subconf_unit) {
		this.subconf_unit = subconf_unit;
	}
	
}
