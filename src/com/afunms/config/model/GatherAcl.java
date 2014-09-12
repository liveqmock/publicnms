package com.afunms.config.model;

import com.afunms.common.base.BaseVo;

public class GatherAcl extends BaseVo{
private int id;
private String ipaddress;
private String command;
private int isMonitor;
public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}
public String getIpaddress() {
	return ipaddress;
}
public void setIpaddress(String ipaddress) {
	this.ipaddress = ipaddress;
}
public String getCommand() {
	return command;
}
public void setCommand(String command) {
	this.command = command;
}
public int getIsMonitor() {
	return isMonitor;
}
public void setIsMonitor(int isMonitor) {
	this.isMonitor = isMonitor;
}

}
