package com.afunms.config.model;

import com.afunms.common.base.BaseVo;

public class DistrictConfig extends BaseVo {
	private int id;
	private String name;    //用户名
	private String desc;  //表述
	private String descolor;  //颜色
	
	public String getDescolor() {
		return descolor;
	}
	public void setDescolor(String descolor) {
		this.descolor = descolor;
	}
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
