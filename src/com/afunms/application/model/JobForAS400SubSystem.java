package com.afunms.application.model;

import com.afunms.common.base.BaseVo;

public class JobForAS400SubSystem extends BaseVo{
	
	private int id ;
	
	private String name;
	
	private String nodeid;
	
	private String ipaddress;
	
	private String mon_flag;
	
	private String alarm_level;
	
	private String num;
	
	private String active_status;
	
	private String active_status_type;

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the nodeid
	 */
	public String getNodeid() {
		return nodeid;
	}

	/**
	 * @param nodeid the nodeid to set
	 */
	public void setNodeid(String nodeid) {
		this.nodeid = nodeid;
	}

	/**
	 * @return the ipaddress
	 */
	public String getIpaddress() {
		return ipaddress;
	}

	/**
	 * @param ipaddress the ipaddress to set
	 */
	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	/**
	 * @return the mon_flag
	 */
	public String getMon_flag() {
		return mon_flag;
	}

	/**
	 * @param mon_flag the mon_flag to set
	 */
	public void setMon_flag(String mon_flag) {
		this.mon_flag = mon_flag;
	}

	/**
	 * @return the alarm_level
	 */
	public String getAlarm_level() {
		return alarm_level;
	}

	/**
	 * @param alarm_level the alarm_level to set
	 */
	public void setAlarm_level(String alarm_level) {
		this.alarm_level = alarm_level;
	}

	public String getActive_status() {
		return active_status;
	}

	public void setActive_status(String active_status) {
		this.active_status = active_status;
	}

	public String getActive_status_type() {
		return active_status_type;
	}

	public void setActive_status_type(String active_status_type) {
		this.active_status_type = active_status_type;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	
	
}
