package com.afunms.automation.model;


import com.afunms.common.base.BaseVo;

public class SlaNodeProp extends BaseVo {
	private int id;
	private int telnetconfigid;
	private int entrynumber;//H3C:暂无；cisco:实体数字
	private String slatype;
	private String bak;
	private String createtime;
	private int operatorid;
	private String adminsign;// 备注1:( H3C:存放管理名；cisco:暂无）
	private String operatesign;// 备注2:( H3C:存放操作标识；cisco:暂无）

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

	public String getSlatype() {
		return slatype;
	}

	public void setSlatype(String slatype) {
		this.slatype = slatype;
	}

	public String getBak() {
		return bak;
	}

	public void setBak(String bak) {
		this.bak = bak;
	}

	public String getCreatetime() {
		return createtime;
	}

	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}

	public int getOperatorid() {
		return operatorid;
	}

	public void setOperatorid(int operatorid) {
		this.operatorid = operatorid;
	}

	public String getAdminsign() {
		return adminsign;
	}

	public void setAdminsign(String adminsign) {
		this.adminsign = adminsign;
	}

	public String getOperatesign() {
		return operatesign;
	}

	public void setOperatesign(String operatesign) {
		this.operatesign = operatesign;
	}

}
