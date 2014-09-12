package com.afunms.home.module.model;

import com.afunms.common.base.BaseVo;

public class ModuleModel extends BaseVo {
    public ModuleModel() {

    }

    private int id;
    private String enName;
    private String chName;
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
