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

public class Ftpmonitor_realtime extends BaseVo
{
    /** identifier field */
    private int id;

    /** nullable persistent field */
    private int ftp_id;

    /** nullable persistent field */
    private int is_canconnected;

    /** nullable persistent field */
    private String reason;

    /** nullable persistent field */
    private Calendar mon_time;
    
    private int sms_sign;

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
	 * @return the ftp_id
	 */
	public int getFtp_id() {
		return ftp_id;
	}

	/**
	 * @param ftp_id the ftp_id to set
	 */
	public void setFtp_id(int ftp_id) {
		this.ftp_id = ftp_id;
	}

	/**
	 * @return the is_canconnected
	 */
	public int getIs_canconnected() {
		return is_canconnected;
	}

	/**
	 * @param is_canconnected the is_canconnected to set
	 */
	public void setIs_canconnected(int is_canconnected) {
		this.is_canconnected = is_canconnected;
	}

	/**
	 * @return the reason
	 */
	public String getReason() {
		return reason;
	}

	/**
	 * @param reason the reason to set
	 */
	public void setReason(String reason) {
		this.reason = reason;
	}

	/**
	 * @return the mon_time
	 */
	public Calendar getMon_time() {
		return mon_time;
	}

	/**
	 * @param mon_time the mon_time to set
	 */
	public void setMon_time(Calendar mon_time) {
		this.mon_time = mon_time;
	}

	/**
	 * @return the sms_sign
	 */
	public int getSms_sign() {
		return sms_sign;
	}

	/**
	 * @param sms_sign the sms_sign to set
	 */
	public void setSms_sign(int sms_sign) {
		this.sms_sign = sms_sign;
	}

	/**
	 * @return the id
	 */
	
	
    
}
    

    