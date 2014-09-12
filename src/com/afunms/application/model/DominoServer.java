/**
 * <p>Description:mapping app_db_node</p>
 * <p>Company: dhcc.com</p>
 * @author miiwill
 * @project afunms
 * @date 2007-1-7
 */

package com.afunms.application.model;

import com.afunms.common.base.BaseVo;

public class DominoServer extends BaseVo{
	private String name = "";//服务器名称
	private String title = "";//服务器标题
	private String os = "";//服务器操作系统
	private String architecture = "";//位数32/64
	private String starttime = "";//监视启动时间
	private String cputype = "";//CPU类型
	private String cpucount = "";//CPU个数
	private String portnumber= "";//端口号
	private String cpupctutil = "0";//CPU利用率
	
	private String imapstatus = "0";//IMAP状态
	private String ldapstatus = "0";//LDAP状态
	private String pop3status = "0";//POP3状态
	private String smtpstatus = "0";//SMTP状态
	private String availabilityIndex = "0";//可用索引
	private String sessionsDropped = "0";//掉线的会话
	private String tasks = "0";//任务数量
	private String transPerMinute = "0";//一分钟的交易数量
	private String usersPeak = "0";//服务启动后在线用户数峰值
	private String requestsPer1Hour = "0";//每小时对Domino的请求数
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getOs() {
		return os;
	}
	public void setOs(String os) {
		this.os = os;
	}
	public String getArchitecture() {
		return architecture;
	}
	public void setArchitecture(String architecture) {
		this.architecture = architecture;
	}
	public String getStarttime() {
		return starttime;
	}
	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}
	public String getCputype() {
		return cputype;
	}
	public void setCputype(String cputype) {
		this.cputype = cputype;
	}
	public String getCpucount() {
		return cpucount;
	}
	public void setCpucount(String cpucount) {
		this.cpucount = cpucount;
	}
	public String getPortnumber() {
		return portnumber;
	}
	public void setPortnumber(String portnumber) {
		this.portnumber = portnumber;
	}
	public String getCpupctutil() {
		return cpupctutil;
	}
	public void setCpupctutil(String cpupctutil) {
		this.cpupctutil = cpupctutil;
	}
	public String getImapstatus() {
		return imapstatus;
	}
	public void setImapstatus(String imapstatus) {
		this.imapstatus = imapstatus;
	}
	public String getLdapstatus() {
		return ldapstatus;
	}
	public void setLdapstatus(String ldapstatus) {
		this.ldapstatus = ldapstatus;
	}
	public String getPop3status() {
		return pop3status;
	}
	public void setPop3status(String pop3status) {
		this.pop3status = pop3status;
	}
	public String getSmtpstatus() {
		return smtpstatus;
	}
	public void setSmtpstatus(String smtpstatus) {
		this.smtpstatus = smtpstatus;
	}
	public String getAvailabilityIndex() {
		return availabilityIndex;
	}
	public void setAvailabilityIndex(String availabilityIndex) {
		this.availabilityIndex = availabilityIndex;
	}
	public String getSessionsDropped() {
		return sessionsDropped;
	}
	public void setSessionsDropped(String sessionsDropped) {
		this.sessionsDropped = sessionsDropped;
	}
	public String getTasks() {
		return tasks;
	}
	public void setTasks(String tasks) {
		this.tasks = tasks;
	}
	public String getTransPerMinute() {
		return transPerMinute;
	}
	public void setTransPerMinute(String transPerMinute) {
		this.transPerMinute = transPerMinute;
	}
	public String getUsersPeak() {
		return usersPeak;
	}
	public void setUsersPeak(String usersPeak) {
		this.usersPeak = usersPeak;
	}
	public String getRequestsPer1Hour() {
		return requestsPer1Hour;
	}
	public void setRequestsPer1Hour(String requestsPer1Hour) {
		this.requestsPer1Hour = requestsPer1Hour;
	}
	


}