package com.afunms.system.model;

import com.afunms.common.base.BaseVo;

public class Admin extends BaseVo {
	private int id;
	private String func_desc;
	private String ch_desc;
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
	 * @return the func_desc
	 */
	public String getFunc_desc() {
		return func_desc;
	}
	/**
	 * @param func_desc the func_desc to set
	 */
	public void setFunc_desc(String func_desc) {
		this.func_desc = func_desc;
	}
	/**
	 * @return the ch_desc
	 */
	public String getCh_desc() {
		return ch_desc;
	}
	/**
	 * @param ch_desc the ch_desc to set
	 */
	public void setCh_desc(String ch_desc) {
		this.ch_desc = ch_desc;
	}
	
}
