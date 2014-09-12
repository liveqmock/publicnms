package com.afunms.config.model;

import com.afunms.common.base.BaseVo;

public class CiscoSlaNodeProp extends BaseVo {
	private int id;
	private int telnetconfigid;
	private int entrynumber;
	private String createBy;
	private String slatype;
	private String createTime;
	private String bak;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTelnetconfigid() {
		return telnetconfigid;
	}

	public void setTelnetconfigid(int telnetconfigid) {
		this.telnetconfigid = telnetconfigid;
	}

	public int getEntrynumber() {
		return entrynumber;
	}

	public void setEntrynumber(int entrynumber) {
		this.entrynumber = entrynumber;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public String getSlatype() {
		return slatype;
	}

	public void setSlatype(String slatype) {
		this.slatype = slatype;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getBak() {
		return bak;
	}

	public void setBak(String bak) {
		this.bak = bak;
	}

	

}
