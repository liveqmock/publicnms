package com.afunms.system.model;

import com.afunms.common.base.BaseVo;

public class RoleFunction extends BaseVo {
	private int id;
	private String roleid;
	private String funcid;
	/**
	 * @return the id
	 */
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
	 * @return the roleid
	 */
	public String getRoleid() {
		return roleid;
	}
	/**
	 * @param roleid the roleid to set
	 */
	public void setRoleid(String roleid) {
		this.roleid = roleid;
	}
	/**
	 * @return the funcid
	 */
	public String getFuncid() {
		return funcid;
	}
	/**
	 * @param funcid the funcid to set
	 */
	public void setFuncid(String funcid) {
		this.funcid = funcid;
	}
	
}
