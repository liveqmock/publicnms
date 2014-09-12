package com.afunms.schedule.model;

import java.sql.Timestamp;

import com.afunms.common.base.BaseVo;

public class Position extends BaseVo{
	private String id;
	private String name;
	private String description;
	private String created_by;
	private Timestamp created_on;
	private String updated_by;
	private Timestamp updated_on;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}
