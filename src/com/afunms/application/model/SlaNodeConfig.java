/**
 * <p>Description:mapping app_db_node</p>
 * <p>Company: dhcc.com</p>
 * @author miiwill
 * @project afunms
 * @date 2007-1-7
 */

package com.afunms.application.model;

import com.afunms.common.base.BaseVo;

public class SlaNodeConfig extends BaseVo {

	private int id;
	private int telnetconfig_id;
	private String name;
	private String slatype;
	private int intervals;
	private String intervalunit;
	private String descr;
	private String bak;
	private int mon_flag;
	private String bid;
	private String entrynumber;
	private String destip;
	private String devicetype;
	private String collecttype;
	private String adminsign;
	private String operatesign;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getTelnetconfig_id() {
		return telnetconfig_id;
	}
	public void setTelnetconfig_id(int telnetconfig_id) {
		this.telnetconfig_id = telnetconfig_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSlatype() {
		return slatype;
	}
	public void setSlatype(String slatype) {
		this.slatype = slatype;
	}
	public int getIntervals() {
		return intervals;
	}
	public void setIntervals(int intervals) {
		this.intervals = intervals;
	}
	public String getIntervalunit() {
		return intervalunit;
	}
	public void setIntervalunit(String intervalunit) {
		this.intervalunit = intervalunit;
	}
	public String getDescr() {
		return descr;
	}
	public void setDescr(String descr) {
		this.descr = descr;
	}
	public String getBak() {
		return bak;
	}
	public void setBak(String bak) {
		this.bak = bak;
	}
	public int getMon_flag() {
		return mon_flag;
	}
	public void setMon_flag(int mon_flag) {
		this.mon_flag = mon_flag;
	}
	public String getBid() {
		return bid;
	}
	public void setBid(String bid) {
		this.bid = bid;
	}
	public String getEntrynumber() {
		return entrynumber;
	}
	public void setEntrynumber(String entrynumber) {
		this.entrynumber = entrynumber;
	}
	public String getDestip() {
		return destip;
	}
	public void setDestip(String destip) {
		this.destip = destip;
	}
	public String getDevicetype() {
		return devicetype;
	}
	public void setDevicetype(String devicetype) {
		this.devicetype = devicetype;
	}
	public String getCollecttype() {
		return collecttype;
	}
	public void setCollecttype(String collecttype) {
		this.collecttype = collecttype;
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