package com.afunms.application.model;

import com.afunms.common.base.BaseVo;

public class OracleEntity extends BaseVo{

	private int id;
	private int dbid;
	private String sid;
	private String user;
	private String password;
	private String gzerid;
	private int collectType;
	private String alias;
	private int managed;
	private String bid;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getDbid() {
		return dbid;
	}
	public void setDbid(int dbid) {
		this.dbid = dbid;
	}
	public String getSid() {
		return sid;
	}
	public void setSid(String sid) {
		this.sid = sid;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getGzerid() {
		return gzerid;
	}
	public void setGzerid(String gzerid) {
		this.gzerid = gzerid;
	}
	public int getCollectType() {
		return collectType;
	}
	public void setCollectType(int collectType) {
		this.collectType = collectType;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public int getManaged() {
		return managed;
	}
	public void setManaged(int managed) {
		this.managed = managed;
	}
	public String getBid() {
		return bid;
	}
	public void setBid(String bid) {
		this.bid = bid;
	}
}
