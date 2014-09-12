package com.afunms.application.model;

import java.io.Serializable;

import com.afunms.common.base.BaseVo;

public class MySqlSpaceConfig extends BaseVo implements Serializable{
	
	private Integer id;
	/** nullable persistent field */
	private String ipaddress;
	
	/** identifier field */
	private String dbname;

	/** nullable persistent field */
	private String linkuse;
	
	/** nullable persistent field */
	private Integer sms;
   
	/** nullable persistent field */
	private String bak;
	
	private Integer reportflag;
	
	private Integer alarmvalue;
	private Integer logflag; //0:数据文件 1:日志文件
	
	/** default constructor */
	public MySqlSpaceConfig() {
	}

	public Integer getReportflag(){
		return reportflag;
	}
	public void setReportflag(Integer reportflag){
		this.reportflag = reportflag;
	}
	/**
	 * @return
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @return
	 */
	public String getBak() {
		return bak;
	}

	/**
	 * @return
	 */
	public String getDbname() {
		return this.dbname;
	}

	/**
	 * @return
	 */
	public String getLinkuse() {
		return linkuse;
	}

	/**
	 * @return
	 */
	public Integer getSms() {
		return sms;
	}

	/**
	 * @return
	 */
	public String getIpaddress() {
		return ipaddress;
	}
	public Integer getAlarmvalue() {
		return alarmvalue;
	}

	/**
	 * @param string
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @param serializable
	 */
	public void setBak(String serializable) {
		bak = serializable;
	}

	/**
	 * @param calendar
	 */
	public void setDbname(String dbname) {
		this.dbname = dbname;
	}

	/**
	 * @param string
	 */
	public void setLinkuse(String string) {
		linkuse = string;
	}

	/**
	 * @param string
	 */
	public void setSms(Integer string) {
		sms = string;
	}

	/**
	 * @param string
	 */
	public void setIpaddress(String string) {
		ipaddress = string;
	}

	public void setAlarmvalue(Integer string) {
		this.alarmvalue = string;
	}
	public Integer getLogflag() {
		return this.logflag;
	}
	public void setLogflag(Integer logflag) {
		this.logflag = logflag;
	}
}
