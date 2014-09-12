package com.afunms.config.model;

import com.afunms.common.base.BaseVo;

public class VpnCfgCmdFile extends BaseVo {
	private int id;
	private String name;
	private String filename;
	private String createBy;
	private String createTime;
	private String fileDesc;
	private String vpnType;
	private String deviceType;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getFileDesc() {
		return fileDesc;
	}

	public void setFileDesc(String fileDesc) {
		this.fileDesc = fileDesc;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVpnType() {
		return vpnType;
	}

	public void setVpnType(String vpnType) {
		this.vpnType = vpnType;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	

}
