package com.afunms.subconfigcat.model;

import com.afunms.common.base.BaseVo;

public class SubconfigCatConfig extends BaseVo {
	private int id;
	private String name;    //用户名
	private String desc;  //表述
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
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

}
