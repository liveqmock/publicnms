/**
 * <p>Description:mapping app_db_node</p>
 * <p>Company: dhcc.com</p>
 * @author miiwill
 * @project afunms
 * @date 2007-1-7
 */

package com.afunms.application.model;

import com.afunms.common.base.BaseVo;

public class WebConfig extends BaseVo {

	private int id;
	private String str;
	private String user_name;
	private String user_password;
	private String query_string;
	private String method;
	private String availability_string;
	private int poll_interval;
	private String unavailability_string;
	private int timeout;
	private int verify;
	private int flag;
	private int mon_flag;
	private String alias;
	private String sendmobiles;
	private String netid;
	private String sendemail;
	private String sendphone;
	private String ipAddress;
	private int supperid;//π©”¶…Ãid snow add at 2010-5-21
	private String keyword;
	private String pagesize_min;
	private int tracertflag;
	
	public int getSupperid() {
		return supperid;
	}
	public void setSupperid(int supperid) {
		this.supperid = supperid;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getStr() {
		return str;
	}
	public void setStr(String str) {
		this.str = str;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getUser_password() {
		return user_password;
	}
	public void setUser_password(String user_password) {
		this.user_password = user_password;
	}
	public String getQuery_string() {
		return query_string;
	}
	public void setQuery_string(String query_string) {
		this.query_string = query_string;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getAvailability_string() {
		return availability_string;
	}
	public void setAvailability_string(String availability_string) {
		this.availability_string = availability_string;
	}
	public int getPoll_interval() {
		return poll_interval;
	}
	public void setPoll_interval(int poll_interval) {
		this.poll_interval = poll_interval;
	}
	public String getUnavailability_string() {
		return unavailability_string;
	}
	public void setUnavailability_string(String unavailability_string) {
		this.unavailability_string = unavailability_string;
	}
	public int getTimeout() {
		return timeout;
	}
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	public int getVerify() {
		return verify;
	}
	public void setVerify(int verify) {
		this.verify = verify;
	}
	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	public int getMon_flag() {
		return mon_flag;
	}
	public void setMon_flag(int mon_flag) {
		this.mon_flag = mon_flag;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getSendmobiles() {
		return sendmobiles;
	}
	public void setSendmobiles(String sendmobiles) {
		this.sendmobiles = sendmobiles;
	}
	public String getNetid() {
		return netid;
	}
	public void setNetid(String netid) {
		this.netid = netid;
	}
	public String getSendemail() {
		return sendemail;
	}
	public void setSendemail(String sendemail) {
		this.sendemail = sendemail;
	}
	public String getSendphone() {
		return sendphone;
	}
	public void setSendphone(String sendphone) {
		this.sendphone = sendphone;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public String getPagesize_min() {
		return pagesize_min;
	}
	public void setPagesize_min(String pagesize_min) {
		this.pagesize_min = pagesize_min;
	}
	public int getTracertflag() {
		return tracertflag;
	}
	public void setTracertflag(int tracertflag) {
		this.tracertflag = tracertflag;
	}
	
}