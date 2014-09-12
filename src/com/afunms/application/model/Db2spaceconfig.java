/**
 * <p>Description:mapping app_db_node</p>
 * <p>Company: dhcc.com</p>
 * @author miiwill
 * @project afunms
 * @date 2007-1-7
 */

package com.afunms.application.model;

import com.afunms.common.base.BaseVo;

public class Db2spaceconfig extends BaseVo
{
	private Integer id;
	/** nullable persistent field */
	private String ipaddress;
	
	/** identifier field */
	private String spacename;

	/** nullable persistent field */
	private String linkuse;
	
	/** nullable persistent field */
	private Integer sms;
   
	/** nullable persistent field */
	private String bak;
	
	private Integer reportflag;
	
	private Integer alarmvalue;
	
	private String dbname;
	
	/** default constructor */
	public Db2spaceconfig() {
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
	public String getSpacename() {
		return spacename;
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
	public void setSpacename(String spacename) {
		this.spacename = spacename;
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
	
	public void setDbname(String string) {
		this.dbname = string;
	}
	
	public String getDbname(){
		return this.dbname;
	}
}