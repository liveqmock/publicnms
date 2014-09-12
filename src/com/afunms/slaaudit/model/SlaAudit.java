package com.afunms.slaaudit.model;

import com.afunms.common.base.BaseVo;

public class SlaAudit extends BaseVo {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	private int id;// ID
	private int userid;// 用户ID
	private int telnetconfigid;//telnetconfig Id与topo_node_telnetconfig表 id关联
	private String slatype;// sla 类型
	private String operation;// 操作类型
	private String cmdcontent;// 命令集合内容
	private String dotime;// 操作时间
	private int dostatus;// 操作状态 0：失败 1：成功
   
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public int getTelnetconfigid() {
		return telnetconfigid;
	}

	public void setTelnetconfigid(int telnetconfigid) {
		this.telnetconfigid = telnetconfigid;
	}

	public String getSlatype() {
		return slatype;
	}

	public void setSlatype(String slatype) {
		this.slatype = slatype;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getCmdcontent() {
		return cmdcontent;
	}

	public void setCmdcontent(String cmdcontent) {
		this.cmdcontent = cmdcontent;
	}

	public String getDotime() {
		return dotime;
	}

	public void setDotime(String dotime) {
		this.dotime = dotime;
	}

	public int getDostatus() {
		return dostatus;
	}

	public void setDostatus(int dostatus) {
		this.dostatus = dostatus;
	}

	

}
