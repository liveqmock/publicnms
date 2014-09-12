package com.afunms.automation.model;

import java.sql.Timestamp;

import com.afunms.common.base.BaseVo;
/**
 * 
 * @descrition ×Ô¶¯»¯ÍøÂçÅäÖÃMODEL
 * @author wangxiangyong
 * @date Aug 26, 2014 11:07:25 AM
 */
public class NetCfgFile extends BaseVo
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String ipaddress;
	private String fileName;
	private String descri;
	private int fileSize;
	private Timestamp backupTime;
	private String bkpType;
	private int baseline;
	public String getBkpType() {
		return bkpType;
	}
	public void setBkpType(String bkpType) {
		this.bkpType = bkpType;
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
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public String getDescri() {
		return descri;
	}
	public void setDescri(String descri) {
		this.descri = descri;
	}
	public int getBaseline() {
		return baseline;
	}
	public void setBaseline(int baseline) {
		this.baseline = baseline;
	}
	
}

