package com.afunms.home.role.model;

import com.afunms.common.base.BaseVo;

public class HomeRoleModel extends BaseVo {
    private int id;
    private String enName;
    private String chName;
    private int role_id;
    private int dept_id;
    private int visible;
    private String note;
    private int type;

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public String getEnName() {
	return enName;
    }

    public void setEnName(String enName) {
	this.enName = enName;
    }

    public String getChName() {
	return chName;
    }

    public void setChName(String chName) {
	this.chName = chName;
    }

    public int getRole_id() {
	return role_id;
    }

    public void setRole_id(int role_id) {
	this.role_id = role_id;
    }

    public int getDept_id() {
	return dept_id;
    }

    public void setDept_id(int dept_id) {
	this.dept_id = dept_id;
    }

    public int getVisible() {
	return visible;
    }

    public void setVisible(int visible) {
	this.visible = visible;
    }

    public String getNote() {
	return note;
    }

    public void setNote(String note) {
	this.note = note;
    }

    public void setType(int type) {
	this.type = type;
    }

    public int getType() {
	return type;
    }
}
