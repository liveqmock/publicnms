package com.afunms.schedule.model;

import java.sql.Time;
import java.sql.Timestamp;

import com.afunms.common.base.BaseVo;

public class Schedule extends BaseVo{
	private String sid;
	private String id;
	private Timestamp on_date;
	private int watcher;
	private String name;
	private String description;
	private String period;
	private String position;
	private String created_by;
	private Timestamp created_on;
	private String updated_by;
	private Timestamp updated_on;
	private String log;
	private String status;
	private String cstatus;
	
	public String getSid() {
		return sid;
	}
	public void setSid(String sid) {
		this.sid = sid;
	}
	public Timestamp getOn_date() {
		return on_date;
	}
	public void setOn_date(Timestamp on_date) {
		this.on_date = on_date;
	}
	public int getWatcher() {
		return watcher;
	}
	public void setWatcher(int watcher) {
		this.watcher = watcher;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCreated_by() {
		return created_by;
	}
	public void setCreated_by(String created_by) {
		this.created_by = created_by;
	}
	public Timestamp getCreated_on() {
		return created_on;
	}
	public void setCreated_on(Timestamp created_on) {
		this.created_on = created_on;
	}
	public String getUpdated_by() {
		return updated_by;
	}
	public void setUpdated_by(String updated_by) {
		this.updated_by = updated_by;
	}
	public Timestamp getUpdated_on() {
		return updated_on;
	}
	public void setUpdated_on(Timestamp updated_on) {
		this.updated_on = updated_on;
	}
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLog() {
		return log;
	}
	public void setLog(String log) {
		this.log = log;
	}
	public String getCstatus() {
		return cstatus;
	}
	public void setCstatus(String cstatus) {
		this.cstatus = cstatus;
	}
	
}
