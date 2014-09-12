/**
 * <p>Description:mapping table nms_hint_node</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2010-01-4
 */

package com.afunms.topology.model;

import java.io.Serializable;

import com.afunms.common.base.BaseVo;

public class InterfaceNode extends BaseVo implements Serializable{
	
	private int id;
	
	private int node_id;
	
	private String entity;

	private String descr;

	private String port;

	private String speed;
	
	private String alias;
	
	private String phys_address;
	
	private String ip_address;
	
	private int oper_status;
	
	private int type;
	
	private int chassis;
	
	private int slot;
	
	private int uport;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getChassis() {
		return chassis;
	}

	public void setChassis(int chassis) {
		this.chassis = chassis;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getIp_address() {
		return ip_address;
	}

	public void setIp_address(String ip_address) {
		this.ip_address = ip_address;
	}

	public int getNode_id() {
		return node_id;
	}

	public void setNode_id(int node_id) {
		this.node_id = node_id;
	}

	public int getOper_status() {
		return oper_status;
	}

	public void setOper_status(int oper_status) {
		this.oper_status = oper_status;
	}

	public String getPhys_address() {
		return phys_address;
	}

	public void setPhys_address(String phys_address) {
		this.phys_address = phys_address;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public int getSlot() {
		return slot;
	}

	public void setSlot(int slot) {
		this.slot = slot;
	}

	public String getSpeed() {
		return speed;
	}

	public void setSpeed(String speed) {
		this.speed = speed;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getUport() {
		return uport;
	}

	public void setUport(int uport) {
		this.uport = uport;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

}
