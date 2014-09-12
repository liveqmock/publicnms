package com.afunms.device.model;

import com.afunms.common.base.BaseVo;

public class Cabinet extends BaseVo{
/**
	 * 
	 */
	private static final long serialVersionUID = 9037106395148670649L;
private int id;
private String city;//城市
private String roadInfo;//路口信息
private String affiliation;//所属机构
private String belong;//所属辖区
private String latitude;//经度
private String longitude;//纬度
private String ipaddress;//ip地址
private String rackNumber;//机柜编号
public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}
public String getCity() {
	return city;
}
public void setCity(String city) {
	this.city = city;
}
public String getRoadInfo() {
	return roadInfo;
}
public void setRoadInfo(String roadInfo) {
	this.roadInfo = roadInfo;
}
public String getAffiliation() {
	return affiliation;
}
public void setAffiliation(String affiliation) {
	this.affiliation = affiliation;
}
public String getBelong() {
	return belong;
}
public void setBelong(String belong) {
	this.belong = belong;
}

public String getLatitude() {
	return latitude;
}
public void setLatitude(String latitude) {
	this.latitude = latitude;
}
public String getLongitude() {
	return longitude;
}
public void setLongitude(String longitude) {
	this.longitude = longitude;
}
public String getIpaddress() {
	return ipaddress;
}
public void setIpaddress(String ipaddress) {
	this.ipaddress = ipaddress;
}
public String getRackNumber() {
	return rackNumber;
}
public void setRackNumber(String rackNumber) {
	this.rackNumber = rackNumber;
}

}
