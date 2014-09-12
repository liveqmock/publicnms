package com.afunms.config.model;

import java.io.Serializable;
import java.sql.Clob;

import com.afunms.common.base.BaseVo;

/** @author Hibernate CodeGenerator */
public class Errptconfig extends BaseVo implements Serializable {
	private Integer id;

	private Integer nodeid;
		
	private String errpttype;

	private String errptclass;
	
	private String alarmwayid;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getNodeid() {
		return nodeid;
	}

	public void setNodeid(Integer nodeid) {
		this.nodeid = nodeid;
	}

	public String getErrpttype() {
		return errpttype;
	}

	public void setErrpttype(String errpttype) {
		this.errpttype = errpttype;
	}

	public String getErrptclass() {
		return errptclass;
	}

	public void setErrptclass(String errptclass) {
		this.errptclass = errptclass;
	}

	public String getAlarmwayid() {
		return alarmwayid;
	}

	public void setAlarmwayid(String alarmwayid) {
		this.alarmwayid = alarmwayid;
	}
	

}
