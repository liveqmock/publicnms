package com.afunms.system.model;

import java.util.Date;

import com.afunms.common.base.BaseVo;

public class UserAudit extends BaseVo {
	private int id;
	
	private int userId;
	
	private String action;
	
	private String time;

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
	 * @return the userId
	 */
	public int getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}

	/**
	 * @return the action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * @return the date
	 */
	public String getTime() {
		return time;
	}

	/**
	 * @param String the date to set
	 */
	public void setTime(String time) {
		this.time = time;
	}
}
