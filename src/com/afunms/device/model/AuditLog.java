package com.afunms.device.model;

import java.sql.Timestamp;

import com.afunms.common.base.BaseVo;

public class AuditLog extends BaseVo{
/**
	 * 
	 */
	private static final long serialVersionUID = 6558725710347836241L;
private int id;
private String ip;//ipµÿ÷∑
private String username;
private String operateType;
private Timestamp dotime;// ±º‰
private String type;
public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}
public String getIp() {
	return ip;
}
public void setIp(String ip) {
	this.ip = ip;
}
public String getUsername() {
	return username;
}
public void setUsername(String username) {
	this.username = username;
}
public String getOperateType() {
	return operateType;
}
public void setOperateType(String operateType) {
	this.operateType = operateType;
}
public Timestamp getDotime() {
	return dotime;
}
public void setDotime(Timestamp dotime) {
	this.dotime = dotime;
}
public String getType() {
	return type;
}
public void setType(String type) {
	this.type = type;
}

}
