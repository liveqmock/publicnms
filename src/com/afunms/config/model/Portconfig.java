package com.afunms.config.model;

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
	
	private String inportalarm;

	private String outportalarm;
	private String speed;
	private String alarmlevel;
	private String flag;
	
//
//	/** full constructor */
//	public Portconfig(Integer id,String ipaddress,Integer portindex, String name, String linkuse, Integer sms, String bak,Integer reportflag,String inportalarm,String outportalarm) {		
//		this.id = id;
//		this.ipaddress = ipaddress;
//		this.portindex = portindex;
//		this.name = name;
//		this.linkuse = linkuse;
//		//this.room = roomid;		
//		this.sms = sms;
//		this.bak=bak;
//		this.reportflag = reportflag;
//		this.inportalarm = inportalarm;
//		this.outportalarm = outportalarm;
//} 
	
	/** full constructor */
	public Portconfig(Integer id,String ipaddress,Integer portindex, String name, String linkuse, Integer sms, String bak,Integer reportflag,String inportalarm,String outportalarm,String alarmlevel) {		
		this.id = id;
		this.ipaddress = ipaddress;
		this.portindex = portindex;
		this.name = name;
		this.linkuse = linkuse;
		//this.room = roomid;		
		this.sms = sms;
		this.bak=bak;
		this.reportflag = reportflag;
		this.inportalarm = inportalarm;
		this.outportalarm = outportalarm;
		this.alarmlevel = alarmlevel;
}
	
	public Portconfig(Integer id,String ipaddress,Integer portindex, String name, String linkuse, Integer sms, String bak,Integer reportflag,String inportalarm,String outportalarm,String speed,String alarmlevel) {		
		this.id = id;
		this.ipaddress = ipaddress;
		this.portindex = portindex;
		this.name = name;
		this.linkuse = linkuse;
		//this.room = roomid;		
		this.sms = sms;
		this.bak=bak;
		this.reportflag = reportflag;
		this.inportalarm = inportalarm;
		this.outportalarm = outportalarm;
		this.speed = speed;
		this.alarmlevel = alarmlevel;
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
	
	public String getAlarmlevel() {
		return alarmlevel;
	}

	public void setAlarmlevel(String alarmlevel) {
		this.alarmlevel = alarmlevel;
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

	public String getInportalarm() {
		return inportalarm;
	}

	public void setInportalarm(String inportalarm) {
		this.inportalarm = inportalarm;
	}

	public String getOutportalarm() {
		return outportalarm;
	}

	public void setOutportalarm(String outportalarm) {
		this.outportalarm = outportalarm;
	}
	
	public String getSpeed() {
		return speed;
	}

	public void setSpeed(String speed) {
		this.speed = speed;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

}
