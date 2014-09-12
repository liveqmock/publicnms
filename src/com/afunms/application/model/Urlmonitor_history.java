/**
 * <p>Description:mapping app_db_node</p>
 * <p>Company: dhcc.com</p>
 * @author miiwill
 * @project afunms
 * @date 2007-1-7
 */

package com.afunms.application.model;

import com.afunms.common.base.BaseVo;
import java.util.Calendar;

public class Urlmonitor_history extends BaseVo
{
    /** identifier field */
    private int id;

    /** nullable persistent field */
    private int url_id;

    /** nullable persistent field */
    private int is_canconnected;

    /** nullable persistent field */
    private int is_valid;

    /** nullable persistent field */
    private int is_refresh;

    /** nullable persistent field */
    private String reason;

    /** nullable persistent field */
    private Calendar mon_time;
    
    private int condelay;
    
    private String pagesize;
    
    private String key_exist;
    
    private String change_rate;

    /** default constructor */
    public Urlmonitor_history() {
    }

    public int getCondelay() {
        return this.condelay;
    }

	public void setCondelay(int condelay) {
		this.condelay = condelay;
	}
    
    
    public int getId() {
        return this.id;
    }

	public void setId(int id) {
		this.id = id;
	}

    public int getUrl_id() {
        return this.url_id;
    }

	public void setUrl_id(int url_id) {
		this.url_id = url_id;
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

    public Calendar getMon_time() {
        return this.mon_time;
    }

	public void setMon_time(Calendar mon_time) {
		this.mon_time = mon_time;
	}

	public String getKey_exist() {
		return key_exist;
	}

	public void setKey_exist(String key_exist) {
		this.key_exist = key_exist;
	}

	public String getPagesize() {
		return pagesize;
	}

	public void setPagesize(String pagesize) {
		this.pagesize = pagesize;
	}

	public String getChange_rate() {
		return change_rate;
	}

	public void setChange_rate(String change_rate) {
		this.change_rate = change_rate;
	}
}