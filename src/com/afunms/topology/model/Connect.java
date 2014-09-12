package com.afunms.topology.model;

import java.sql.Timestamp;

import com.afunms.common.base.BaseVo;

/**
 * @author Administrator
 *
 */
public class Connect extends BaseVo{
	private Long id;
	private Long nodeid;
	private String type;
	private String subtype;
	private String username;
	private String pwd;
	private String ipaddress;
	public String getIpaddress() {
		return ipaddress;
	}
	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getNodeid() {
		return nodeid;
	}
	public void setNodeid(Long nodeid) {
		this.nodeid = nodeid;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSubtype() {
		return subtype;
	}
	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	
}
