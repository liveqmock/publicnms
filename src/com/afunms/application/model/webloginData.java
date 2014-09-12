package com.afunms.application.model;

import java.util.Calendar;

import com.afunms.common.base.BaseVo;

public class webloginData extends BaseVo{
	
	private int id;
	private int weblogin_id;
	private int is_connected;
	private String is_response;
	private Calendar mon_time;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getWeblogin_id() {
		return weblogin_id;
	}
	public void setWeblogin_id(int weblogin_id) {
		this.weblogin_id = weblogin_id;
	}
	public int getIs_connected() {
		return is_connected;
	}
	public void setIs_connected(int is_connected) {
		this.is_connected = is_connected;
	}
	public String getIs_response() {
		return is_response;
	}
	public void setIs_response(String is_response) {
		this.is_response = is_response;
	}
	public Calendar getMon_time() {
		return mon_time;
	}
	public void setMon_time(Calendar mon_time) {
		this.mon_time = mon_time;
	}
	
	

}
