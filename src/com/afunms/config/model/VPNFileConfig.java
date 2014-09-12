package com.afunms.config.model;

import java.sql.Timestamp;

import com.afunms.common.base.BaseVo;

public class VPNFileConfig extends BaseVo{
	private int id;
	private int timingId;
	private String ipaddress;
	private String fileName;
	private String content;
	private int fileSize;
	private Timestamp backupTime;
	private String bkpType;
	private int baseline;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public int getTimingId() {
		return timingId;
	}
	public void setTimingId(int timingId) {
		this.timingId = timingId;
	}
	public String getIpaddress() {
		return ipaddress;
	}
	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getFileSize() {
		return fileSize;
	}
	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}
	public Timestamp getBackupTime() {
		return backupTime;
	}
	public void setBackupTime(Timestamp backupTime) {
		this.backupTime = backupTime;
	}
	public String getBkpType() {
		return bkpType;
	}
	public void setBkpType(String bkpType) {
		this.bkpType = bkpType;
	}
	public int getBaseline() {
		return baseline;
	}
	public void setBaseline(int baseline) {
		this.baseline = baseline;
	}
	
}
