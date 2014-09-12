package com.afunms.application.model;

import com.afunms.common.base.BaseVo;

public class MidConfig extends BaseVo
{
	private int id;
	private int nodeid;
	private int category;
	private String entity;
	private String subentity;
	private String version;
	private String useing;
	private int venderid;
	private String contactor;
	private String contactphone;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getNodeid() {
		return nodeid;
	}
	public void setNodeid(int nodeid) {
		this.nodeid = nodeid;
	}
	public int getCategory() {
		return category;
	}
	public void setCategory(int category) {
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
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getUseing() {
		return useing;
	}
	public void setUseing(String useing) {
		this.useing = useing;
	}
	public int getVenderid() {
		return venderid;
	}
	public void setVenderid(int venderid) {
		this.venderid = venderid;
	}
	public String getContactor() {
		return contactor;
	}
	public void setContactor(String contactor) {
		this.contactor = contactor;
	}
	public String getContactphone() {
		return contactphone;
	}
	public void setContactphone(String contactphone) {
		this.contactphone = contactphone;
	}
}
