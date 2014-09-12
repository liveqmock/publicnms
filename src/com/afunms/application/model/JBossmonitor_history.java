package com.afunms.application.model;

import java.util.Calendar;

import com.afunms.common.base.BaseVo;

public class JBossmonitor_history extends BaseVo{
	 /** identifier field */
    private int id;

    /** nullable persistent field */
    private int jboss_id;

    /** nullable persistent field */
    private int is_canconnected;

    /** nullable persistent field */
    private String reason;

    /** nullable persistent field */
    private Calendar mon_time;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIs_canconnected() {
		return is_canconnected;
	}

	public void setIs_canconnected(int is_canconnected) {
		this.is_canconnected = is_canconnected;
	}

	public int getJboss_id() {
		return jboss_id;
	}

	public void setJboss_id(int jboss_id) {
		this.jboss_id = jboss_id;
	}

	public Calendar getMon_time() {
		return mon_time;
	}

	public void setMon_time(Calendar mon_time) {
		this.mon_time = mon_time;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

}
