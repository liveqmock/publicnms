package com.afunms.automation.model;



import com.afunms.common.base.BaseVo;

public class CiscoSlaCfgCmdFile extends BaseVo {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3419954968448187055L;
	private int id;
	private String name;
	private String filename;
	private String createBy;
	private String createTime;
	private String fileDesc;
	private String slatype;
	private String devicetype;

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

	public String getSlatype() {
		return slatype;
	}

	public void setSlatype(String slatype) {
		this.slatype = slatype;
	}

	public String getDevicetype() {
		return devicetype;
	}

	public void setDevicetype(String devicetype) {
		this.devicetype = devicetype;
	}

}
