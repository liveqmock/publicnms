/**
 * <p>Description:mapping app_ups_node</p>
 * <p>Company: dhcc.com</p>
 * @author miiwill
 * @project afunms
 * @date 2007-1-23
 */

package com.afunms.security.model;

import com.afunms.common.base.BaseVo;

public class MgeUps extends BaseVo
{
	private int id;
	private String alias;	
	private String ipAddress;
	private String location;
	private String sysName;
	private String sysDescr;	
	private String sysOid;
	private String type;	
	private String subtype;	
	private String ismanaged;	
	private String community;
	private String bid;
	private String collecttype;
	private int status;
	
	public String getBid() {
		return bid;
	}
	public void setBid(String bid) {
		this.bid = bid;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getCommunity() {
		return community;
	}
	public void setCommunity(String community) {
		this.community = community;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getSysDescr() {
		return sysDescr;
	}
	public void setSysDescr(String sysDescr) {
		this.sysDescr = sysDescr;
	}
	public String getSysName() {
		return sysName;
	}
	public void setSysName(String sysName) {
		this.sysName = sysName;
	}
	public String getSysOid() {
		return sysOid;
	}
	public void setSysOid(String sysOid) {
		this.sysOid = sysOid;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getIsmanaged() {
		return ismanaged;
	}
	public void setIsmanaged(String ismanaged) {
		this.ismanaged = ismanaged;
	}
	public String getCollecttype() {
		return collecttype;
	}
	public void setCollecttype(String collecttype) {
		this.collecttype = collecttype;
	}
	public String getSubtype() {
		return subtype;
	}
	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}	    
}