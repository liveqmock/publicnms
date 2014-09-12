package com.afunms.config.model;

import com.afunms.common.base.BaseVo;

public class GatherTelnetConfig extends BaseVo{
private int id;
private String telnetIps;
private String commands;
private String create_time;
private int status;
public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}
public String getTelnetIps() {
	return telnetIps;
}
public void setTelnetIps(String telnetIps) {
	this.telnetIps = telnetIps;
}
public String getCommands() {
	return commands;
}
public void setCommands(String commands) {
	this.commands = commands;
}
public String getCreate_time() {
	return create_time;
}
public void setCreate_time(String create_time) {
	this.create_time = create_time;
}
public int getStatus() {
	return status;
}
public void setStatus(int status) {
	this.status = status;
} 

}
