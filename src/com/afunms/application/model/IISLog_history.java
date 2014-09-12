package com.afunms.application.model;

import java.util.Calendar;

import com.afunms.common.base.BaseVo;

public class IISLog_history extends BaseVo{
	
	private int id;
	private int configid;
	private String ssitename;
	private String sip;
	private String csmethod;
	private String csuristem;
	private String csuriquery;
	private String sport;
	private String csusername;
	private String cip;
	private String csagent;
	private Calendar recordtime;
	private int scstatus;
	private int scsubstatus;
	private int scwin32status;
	public String getCip() {
		return cip;
	}
	public void setCip(String cip) {
		this.cip = cip;
	}
	public int getConfigid() {
		return configid;
	}
	public void setConfigid(int configid) {
		this.configid = configid;
	}
	public String getCsagent() {
		return csagent;
	}
	public void setCsagent(String csagent) {
		this.csagent = csagent;
	}
	public String getCsuriquery() {
		return csuriquery;
	}
	public void setCsuriquery(String csuriquery) {
		this.csuriquery = csuriquery;
	}
	public String getCsuristem() {
		return csuristem;
	}
	public void setCsuristem(String csuristem) {
		this.csuristem = csuristem;
	}
	public String getCsusername() {
		return csusername;
	}
	public void setCsusername(String csusername) {
		this.csusername = csusername;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Calendar getRecordtime() {
		return recordtime;
	}
	public void setRecordtime(Calendar recordtime) {
		this.recordtime = recordtime;
	}
	
	public String getCsmethod() {
		return csmethod;
	}
	public void setCsmethod(String csmethod) {
		this.csmethod = csmethod;
	}
	public int getScstatus() {
		return scstatus;
	}
	public void setScstatus(int scstatus) {
		this.scstatus = scstatus;
	}
	public int getScsubstatus() {
		return scsubstatus;
	}
	public void setScsubstatus(int scsubstatus) {
		this.scsubstatus = scsubstatus;
	}
	public int getScwin32status() {
		return scwin32status;
	}
	public void setScwin32status(int scwin32status) {
		this.scwin32status = scwin32status;
	}
	public String getSip() {
		return sip;
	}
	public void setSip(String sip) {
		this.sip = sip;
	}
	public String getSport() {
		return sport;
	}
	public void setSport(String sport) {
		this.sport = sport;
	}
	public String getSsitename() {
		return ssitename;
	}
	public void setSsitename(String ssitename) {
		this.ssitename = ssitename;
	}

}
