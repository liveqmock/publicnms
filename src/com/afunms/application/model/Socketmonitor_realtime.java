/**
 * <p>Description:mapping app_db_node</p>
 * <p>Company: dhcc.com</p>
 * @author miiwill
 * @project afunms
 * @date 2007-1-7
 */

package com.afunms.application.model;

import java.util.Calendar;

import com.afunms.common.base.BaseVo;

public class Socketmonitor_realtime extends BaseVo
{
    /** identifier field */
    private int id;
    
    /** identifier field */
    private int socket_id;

    /** nullable persistent field */
    private int is_canconnected;

    /** nullable persistent field */
    private int is_valid;

    /** nullable persistent field */
    private int is_refresh;

    /** nullable persistent field */
    private String reason;

    /** nullable persistent field */
    private String page_context;

    /** nullable persistent field */
    private Calendar mon_time;

	/** nullable persistent field */
	private int sms_sign;
		
	private int condelay;

    /** default constructor */
    public Socketmonitor_realtime() {
    }
    public int getId() {
        return this.id;
    }

	public void setId(int id) {
		this.id = id;
	}
    public Integer getCondelay() {
        return this.condelay;
    }

	public void setCondelay(Integer condelay) {
		this.condelay = condelay;
	}

    public int getSocket_id() {
        return this.socket_id;
    }

	public void setSocket_id(int socket_id) {
		this.socket_id = socket_id;
	}

    public int getIs_canconnected() {
        return this.is_canconnected;
    }

	public void setIs_canconnected(int is_canconnected) {
		this.is_canconnected = is_canconnected;
	}

    public int getIs_valid() {
        return this.is_valid;
    }

	public void setIs_valid(int is_valid) {
		this.is_valid = is_valid;
	}

    public int getIs_refresh() {
        return this.is_refresh;
    }

	public void setIs_refresh(int is_refresh) {
		this.is_refresh = is_refresh;
	}

    public java.lang.String getReason() {
        return this.reason;
    }

	public void setReason(java.lang.String reason) {
		this.reason = reason;
	}

    public String getPage_context() {
        return this.page_context;
    }

	public void setPage_context(String page_context) {
		this.page_context = page_context;
	}

    public Calendar getMon_time() {
        return this.mon_time;
    }

	public void setMon_time(Calendar mon_time) {
		this.mon_time = mon_time;
	}


	/**
	 * @return
	 */
	public int getSms_sign() {
		return sms_sign;
	}

	/**
	 * @param integer
	 */
	public void setSms_sign(int integer) {
		sms_sign = integer;
	}
}