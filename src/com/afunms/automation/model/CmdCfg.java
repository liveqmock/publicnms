package com.afunms.automation.model;


import com.afunms.common.base.BaseVo;
/**
 * 
 * @descrition TODO
 * @author wangxiangyong
 * @date Aug 30, 2014 2:30:22 PM
 */
public class CmdCfg extends BaseVo {
	
	private static final long serialVersionUID = 4034338587903385608L;
	private int id;
	private String filename;
	private String createBy;
	private String createTime;
	private String fileDesc;
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

	public String getDevicetype() {
		return devicetype;
	}

	public void setDevicetype(String devicetype) {
		this.devicetype = devicetype;
	}

}
