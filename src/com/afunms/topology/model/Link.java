/**
 * <p>Description:mapping table NMS_TOPO_LINK</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-17
 */

package com.afunms.topology.model;

import com.afunms.common.base.BaseVo;

public class Link extends BaseVo {
	private int id;

	private String linkName;

	private int startId;

	private int endId;

	private String startPort;

	private String endPort;

	private String startIndex;

	private String endIndex;

	private String startDescr;

	private String endDescr;

	private String startIp;

	private String endIp;
	
	private String startMac;
	
	private String endMac;

	private String startAlias;

	private String endAlias;

	private int assistant;

	private int type;
	
	private int showinterf;

	private int findtype;

	private int linktype;// 连接类型(physical,logical)

	private String maxSpeed;// yangjun add

	private String maxPer;// yangjun add
	
	private String linkAliasName;
	

	public String getLinkAliasName() {
		return linkAliasName;
	}

	public void setLinkAliasName(String linkAliasName) {
		this.linkAliasName = linkAliasName;
	}

	public String getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(String maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	public int getLinktype() {
		return linktype;
	}

	public void setLinktype(int linktype) {
		this.linktype = linktype;
	}

	public int getFindtype() {
		return findtype;
	}

	public void setFindtype(int findtype) {
		this.findtype = findtype;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getAssistant() {
		return assistant;
	}

	public void setAssistant(int assistant) {
		this.assistant = assistant;
	}

	/**
	 * @return the endAlias
	 */
	public String getEndAlias() {
		return endAlias;
	}

	/**
	 * @param endAlias
	 *            the endAlias to set
	 */
	public void setEndAlias(String endAlias) {
		this.endAlias = endAlias;
	}

	/**
	 * @return the startAlias
	 */
	public String getStartAlias() {
		return startAlias;
	}

	/**
	 * @param startAlias
	 *            the startAlias to set
	 */
	public void setStartAlias(String startAlias) {
		this.startAlias = startAlias;
	}

	/**
	 * @return Returns the endDescr.
	 */
	public String getEndDescr() {
		return endDescr;
	}

	/**
	 * @param endDescr
	 *            The endDescr to set.
	 */
	public void setEndDescr(String endDescr) {
		this.endDescr = endDescr;
	}

	/**
	 * @return Returns the endId.
	 */
	public int getEndId() {
		return endId;
	}

	/**
	 * @param endId
	 *            The endId to set.
	 */
	public void setEndId(int endId) {
		this.endId = endId;
	}

	/**
	 * @return Returns the endIndex.
	 */
	public String getEndIndex() {
		return endIndex;
	}

	/**
	 * @param endIndex
	 *            The endIndex to set.
	 */
	public void setEndIndex(String endIndex) {
		this.endIndex = endIndex;
	}

	/**
	 * @return Returns the endIp.
	 */
	public String getEndIp() {
		return endIp;
	}

	/**
	 * @param endIp
	 *            The endIp to set.
	 */
	public void setEndIp(String endIp) {
		this.endIp = endIp;
	}

	/**
	 * @return Returns the startDescr.
	 */
	public String getStartDescr() {
		return startDescr;
	}

	/**
	 * @param startDescr
	 *            The startDescr to set.
	 */
	public void setStartDescr(String startDescr) {
		this.startDescr = startDescr;
	}

	/**
	 * @return Returns the startId.
	 */
	public int getStartId() {
		return startId;
	}

	/**
	 * @param startId
	 *            The startId to set.
	 */
	public void setStartId(int startId) {
		this.startId = startId;
	}

	/**
	 * @return Returns the startIndex.
	 */
	public String getStartIndex() {
		return startIndex;
	}

	/**
	 * @param startIndex
	 *            The startIndex to set.
	 */
	public void setStartIndex(String startIndex) {
		this.startIndex = startIndex;
	}

	/**
	 * @return Returns the startIp.
	 */
	public String getStartIp() {
		return startIp;
	}

	/**
	 * @param startIp
	 *            The startIp to set.
	 */
	public void setStartIp(String startIp) {
		this.startIp = startIp;
	}

	public String getEndPort() {
		return endPort;
	}

	public void setEndPort(String endPort) {
		this.endPort = endPort;
	}

	public String getStartPort() {
		return startPort;
	}

	public void setStartPort(String startPort) {
		this.startPort = startPort;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	public String getLinkName() {
		return linkName;
	}

	public void setLinkName(String linkName) {
		this.linkName = linkName;
	}

	public String getMaxPer() {
		return maxPer;
	}

	public void setMaxPer(String maxPer) {
		this.maxPer = maxPer;
	}

	public int getShowinterf() {
		return showinterf;
	}

	public void setShowinterf(int showinterf) {
		this.showinterf = showinterf;
	}

	public String getStartMac() {
		return startMac;
	}

	public void setStartMac(String startMac) {
		this.startMac = startMac;
	}

	public String getEndMac() {
		return endMac;
	}

	public void setEndMac(String endMac) {
		this.endMac = endMac;
	}


	
}
