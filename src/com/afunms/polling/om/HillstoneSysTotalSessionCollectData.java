package com.afunms.polling.om;

import java.io.Serializable;
/**
 * 
 * @author GZM
 *
 */
public class HillstoneSysTotalSessionCollectData implements Serializable
{
	private Long id;
    private String ipaddress;
    private String category;
    private String entity;
	private String subentity;
    private String thevalue;
    private java.util.Calendar collecttime;
    private String unit;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getIpaddress() {
		return ipaddress;
	}
	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getEntity() {
		return entity;
	}
	public void setEntity(String entity) {
		this.entity = entity;
	}
	public String getSubentity() {
		return subentity;
	}
	public void setSubentity(String subentity) {
		this.subentity = subentity;
	}
	public String getThevalue() {
		return thevalue;
	}
	public void setThevalue(String thevalue) {
		this.thevalue = thevalue;
	}
	public java.util.Calendar getCollecttime() {
		return collecttime;
	}
	public void setCollecttime(java.util.Calendar collecttime) {
		this.collecttime = collecttime;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
}
