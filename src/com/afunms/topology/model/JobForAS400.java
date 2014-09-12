/**
 * <p>Description:mapping table NMS_TOPO_NODE</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-12
 */

package com.afunms.topology.model;

import com.afunms.common.base.BaseVo;

public class JobForAS400 extends BaseVo {
	
	/**
	 * id
	 */
	private int id;
	
	/**
	 * 作业所属设备id
	 */
	private String nodeid;
	
	/**
	 * 作业所属设备ip
	 */
	private String ipaddress;
	
	
	/**
	 * 作业名称
	 */
	private String name;
	
	/**
	 * 作业子系统
	 */
	private String subsystem;
	
	/**
	 * 作业状态
	 */
	private String status;
	
	/**
	 * 作业活动状态
	 */
	private String activeStatus;
	
	/**
	 * 作业类型
	 */
	private String type;
	
	/**
	 * 作业子类型
	 */
	private String subtype;
	
	/**
	 * 作业 CPU 使用时间
	 */
	private String CPUUsedTime;
	
	/**
	 * 作业所属用户
	 */
	private String user;

	/**
	 * 作业采集时间
	 */
	private String collectTime;

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
	 * @return the ipaddress
	 */
	public String getIpaddress() {
		return ipaddress;
	}

	/**
	 * @param ipaddress the ipaddress to set
	 */
	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the subsystem
	 */
	public String getSubsystem() {
		return subsystem;
	}

	/**
	 * @param subsystem the subsystem to set
	 */
	public void setSubsystem(String subsystem) {
		this.subsystem = subsystem;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the activeStatus
	 */
	public String getActiveStatus() {
		return activeStatus;
	}

	/**
	 * @param activeStatus the activeStatus to set
	 */
	public void setActiveStatus(String activeStatus) {
		this.activeStatus = activeStatus;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the subtype
	 */
	public String getSubtype() {
		return subtype;
	}

	/**
	 * @param subtype the subtype to set
	 */
	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}

	/**
	 * @return the cPUUsedTime
	 */
	public String getCPUUsedTime() {
		return CPUUsedTime;
	}

	/**
	 * @param usedTime the cPUUsedTime to set
	 */
	public void setCPUUsedTime(String usedTime) {
		CPUUsedTime = usedTime;
	}

	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * @return the collectTime
	 */
	public String getCollectTime() {
		return collectTime;
	}

	/**
	 * @param collectTime the collectTime to set
	 */
	public void setCollectTime(String collectTime) {
		this.collectTime = collectTime;
	}

	
}