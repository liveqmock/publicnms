package com.afunms.application.model;
/**
 * @author HONGLI E-mail: ty564457881@163.com
 * @version 创建时间：May 27, 2011 6:00:48 PM
 * 类说明 :磁盘读写最多的sql
 */
public class OracleTopSqlReadWrite {
	private String id;
	
	private String sqltext;
	
	private String totaldisk;
	
	private String totalexec;
	
	private String diskreads;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSqltext() {
		return sqltext;
	}

	public void setSqltext(String sqltext) {
		this.sqltext = sqltext;
	}

	public String getTotaldisk() {
		return totaldisk;
	}

	public void setTotaldisk(String totaldisk) {
		this.totaldisk = totaldisk;
	}

	public String getTotalexec() {
		return totalexec;
	}

	public void setTotalexec(String totalexec) {
		this.totalexec = totalexec;
	}

	public String getDiskreads() {
		return diskreads;
	}

	public void setDiskreads(String diskreads) {
		this.diskreads = diskreads;
	}
}
