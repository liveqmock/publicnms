package com.afunms.application.model;

import java.sql.Timestamp;

import com.afunms.common.base.BaseVo;
//create table machine_up_and_down_log(id int,ip_address varchar(30),action int,oper_time datetime,oper_user varchar(20))
public class UpAndDownLog extends BaseVo
{
	int id;
	String ip_address;
	int action;
	Timestamp oper_time;
	String oper_user;
	public String operid;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getIp_address() {
		return ip_address;
	}
	public void setIp_address(String ip_address) {
		this.ip_address = ip_address;
	}
	public int getAction() {
		return action;
	}
	public void setAction(int action) {
		this.action = action;
	}
	public Timestamp getOper_time() {
		return oper_time;
	}
	public void setOper_time(Timestamp oper_time) {
		this.oper_time = oper_time;
	}
	public String getOper_user() {
		return oper_user;
	}
	public void setOper_user(String oper_user) {
		this.oper_user = oper_user;
	}
	public String getOperid() {
		return operid;
	}
	public void setOperid(String operid) {
		this.operid = operid;
	}
	
}
