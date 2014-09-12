package com.afunms.home.user.model;

import com.afunms.common.base.BaseVo;
import com.afunms.home.role.model.HomeRoleModel;
import com.afunms.system.model.User;

public class HomeUserModel extends BaseVo {
    private int id;
    private String name;
    private String chName;
    private String enName;
    private String user_id;

    private int role_id;
    private int dept_id;
    private int visible;

    private String businessids;
    private String note;

    private int type;
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

    public String getUser_id() {
	return user_id;
    }

    public void setUser_id(String user_id) {
	this.user_id = user_id;
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

    public String getBusinessids() {
	return businessids;
    }

    public void setBusinessids(String businessids) {
	this.businessids = businessids;
    }

    public String getNote() {
	return note;
    }

    public void setNote(String note) {
	this.note = note;
    }

    public String getChName() {
	return chName;
    }

    public void setChName(String chName) {
	this.chName = chName;
    }

    public String getEnName() {
	return enName;
    }

    public void setEnName(String enName) {
	this.enName = enName;
    }
    public void setType(int type) {
	this.type = type;
    }

    public int getType() {
	return type;
    }


    public HomeUserModel GetHomeUserModelFromUserAndHomeRoleModel(User uservo,
	    HomeRoleModel roleModel) {
	HomeUserModel userModel = new HomeUserModel();
	userModel.setBusinessids(uservo.getBusinessids());
	userModel.setChName(roleModel.getChName());
	userModel.setDept_id(roleModel.getDept_id());
	userModel.setEnName(roleModel.getEnName());
	userModel.setName(uservo.getName());
	userModel.setNote(roleModel.getNote());
	userModel.setRole_id(uservo.getRole());
	userModel.setUser_id(uservo.getUserid());
	userModel.setVisible(roleModel.getVisible());

	return userModel;

    }

}
