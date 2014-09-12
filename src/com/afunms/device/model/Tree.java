package com.afunms.device.model;

import com.afunms.common.base.BaseVo;

public class Tree extends BaseVo {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7801148284751679632L;
	private int id;
	private int cabinetId;
	private int externalDeviceId;
	private String pid;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCabinetId() {
		return cabinetId;
	}

	public void setCabinetId(int cabinetId) {
		this.cabinetId = cabinetId;
	}

	public int getExternalDeviceId() {
		return externalDeviceId;
	}

	public void setExternalDeviceId(int externalDeviceId) {
		this.externalDeviceId = externalDeviceId;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

}
