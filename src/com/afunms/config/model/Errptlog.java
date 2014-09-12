package com.afunms.config.model;

import java.sql.Clob;

import com.afunms.common.base.BaseVo;

public class Errptlog extends BaseVo{
	private int id;
	private String labels;
	private String identifier;
	private java.util.Calendar collettime;
	private int seqnumber;
	private String nodeid;
	private String machineid;
	private String errptclass;
	private String errpttype;
	private String resourcename;
	private String resourceclass;
	private String rescourcetype;
	private String locations;
	private String vpd;
	private String descriptions;
	private String hostid;
	
	public String getHostid() {
		return hostid;
	}
	public void setHostid(String hostid) {
		this.hostid = hostid;
	}
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the labels
	 */
	public String getLabels() {
		return labels;
	}
	/**
	 * @param labels the labels to set
	 */
	public void setLabels(String labels) {
		this.labels = labels;
	}
	/**
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}
	/**
	 * @param identifier the identifier to set
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	/**
	 * @return the collettime
	 */
	public java.util.Calendar getCollettime() {
		return collettime;
	}
	/**
	 * @param collettime the collettime to set
	 */
	public void setCollettime(java.util.Calendar collettime) {
		this.collettime = collettime;
	}
	/**
	 * @return the seqnumber
	 */
	public int getSeqnumber() {
		return seqnumber;
	}
	/**
	 * @param seqnumber the seqnumber to set
	 */
	public void setSeqnumber(int seqnumber) {
		this.seqnumber = seqnumber;
	}
	/**
	 * @return the nodeid
	 */
	public String getNodeid() {
		return nodeid;
	}
	/**
	 * @param nodeid the nodeid to set
	 */
	public void setNodeid(String nodeid) {
		this.nodeid = nodeid;
	}
	/**
	 * @return the machineid
	 */
	public String getMachineid() {
		return machineid;
	}
	/**
	 * @param machineid the machineid to set
	 */
	public void setMachineid(String machineid) {
		this.machineid = machineid;
	}
	/**
	 * @return the errptclass
	 */
	public String getErrptclass() {
		return errptclass;
	}
	/**
	 * @param errptclass the errptclass to set
	 */
	public void setErrptclass(String errptclass) {
		this.errptclass = errptclass;
	}
	/**
	 * @return the errpttype
	 */
	public String getErrpttype() {
		return errpttype;
	}
	/**
	 * @param errpttype the errpttype to set
	 */
	public void setErrpttype(String errpttype) {
		this.errpttype = errpttype;
	}
	/**
	 * @return the resourcename
	 */
	public String getResourcename() {
		return resourcename;
	}
	/**
	 * @param resourcename the resourcename to set
	 */
	public void setResourcename(String resourcename) {
		this.resourcename = resourcename;
	}
	/**
	 * @return the resourceclass
	 */
	public String getResourceclass() {
		return resourceclass;
	}
	/**
	 * @param resourceclass the resourceclass to set
	 */
	public void setResourceclass(String resourceclass) {
		this.resourceclass = resourceclass;
	}
	/**
	 * @return the rescourcetype
	 */
	public String getRescourcetype() {
		return rescourcetype;
	}
	/**
	 * @param rescourcetype the rescourcetype to set
	 */
	public void setRescourcetype(String rescourcetype) {
		this.rescourcetype = rescourcetype;
	}
	/**
	 * @return the locations
	 */
	public String getLocations() {
		return locations;
	}
	/**
	 * @param locations the locations to set
	 */
	public void setLocations(String locations) {
		this.locations = locations;
	}
	/**
	 * @return the vpd
	 */
	public String getVpd() {
		return vpd;
	}
	/**
	 * @param vpd the vpd to set
	 */
	public void setVpd(String vpd) {
		this.vpd = vpd;
	}
	/**
	 * @return the descriptions
	 */
	public String getDescriptions() {
		return descriptions;
	}
	/**
	 * @param descriptions the descriptions to set
	 */
	public void setDescriptions(String descriptions) {
		this.descriptions = descriptions;
	}
	
	
}
