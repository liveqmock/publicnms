package com.afunms.polling.om;

import java.io.Serializable;
import java.sql.Clob;

import com.afunms.common.base.BaseVo;

/** @author Hibernate CodeGenerator */
public class Portconfig extends BaseVo implements Serializable {
	private Integer id;
	/** nullable persistent field */
	private String ipaddress;
	
	/** nullable persistent field */
	private Integer portindex;		

	/** identifier field */
	private String name;

	/** nullable persistent field */
	private String linkuse;
	
	/** nullable persistent field */
	private Integer sms;
   
	/** nullable persistent field */
	private String bak;
	
	private Integer reportflag;
	
	/** full constructor */
	public Portconfig(Integer id,String ipaddress,Integer portindex, String name, String linkuse, Integer sms, String bak,Integer reportflag) {		
		this.id = id;
		this.ipaddress = ipaddress;
		this.portindex = portindex;
		this.name = name;
		this.linkuse = linkuse;
		//this.room = roomid;		
		this.sms = sms;
		this.bak=bak;
		this.reportflag = reportflag;
}

	/** default constructor */
	public Portconfig() {
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
	public Integer getPortindex() {
		return portindex;
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
	public String getName() {
		return name;
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
	public void setName(String string) {
		name = string;
	}

	/**
	 * @param string
	 */
	public void setPortindex(Integer string) {
		portindex = string;
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


}
