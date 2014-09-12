package com.afunms.config.model;

import com.afunms.common.base.BaseVo;

/**
 * @author HONGLI E-mail: ty564457881@163.com
 * @version 创建时间：Jun 16, 2011 9:04:55 AM
 * 类说明
 */
public class TimingBackupTelnetConfig extends BaseVo{
	private int id;//id
	
	private String telnetconfigips;
	
	private String backup_type;
	/**
	 * 备份时间
	 */
	private int backup_date;
	
	/**
	 * 备份频率
	 */
	private int backup_sendfrequency;
	
	private String backup_time_month;
	
	private String backup_time_week;
	
	private String backup_time_day;
	
	private String backup_time_hou;
	
	private String backup_day_stop;
	
	private String backup_week_stop;
	
	private String backup_month_stop;
	
	private String backup_season_stop;
	
	private String backup_year_stop;
	
	private String checkupdateflag;
	
	/**
	 * 1:定时  0：未定时
	 */
	private String status;
	
	/**
	 * 备份类型 run：备份运行文件  startup:备份启动文件
	 */
	private String bkpType;
	
	private String content;//所要执行的命令

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getBkpType() {
		return bkpType;
	}

	public void setBkpType(String bkpType) {
		this.bkpType = bkpType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setTelnetconfigips(String telnetconfigips) {
		this.telnetconfigips = telnetconfigips;
	}

	public String getTelnetconfigips() {
		return telnetconfigips;
	}

	public void setTelnetconfigids(String telnetconfigips) {
		this.telnetconfigips = telnetconfigips;
	}

	public String getBackup_type() {
		return backup_type;
	}

	public void setBackup_type(String backup_type) {
		this.backup_type = backup_type;
	}

	public int getBackup_date() {
		return backup_date;
	}

	public void setBackup_date(int backup_date) {
		this.backup_date = backup_date;
	}

	public int getBackup_sendfrequency() {
		return backup_sendfrequency;
	}

	public void setBackup_sendfrequency(int backup_sendfrequency) {
		this.backup_sendfrequency = backup_sendfrequency;
	}

	public String getBackup_time_month() {
		return backup_time_month;
	}

	public void setBackup_time_month(String backup_time_month) {
		this.backup_time_month = backup_time_month;
	}

	public String getBackup_time_week() {
		return backup_time_week;
	}

	public void setBackup_time_week(String backup_time_week) {
		this.backup_time_week = backup_time_week;
	}

	public String getBackup_time_day() {
		return backup_time_day;
	}

	public void setBackup_time_day(String backup_time_day) {
		this.backup_time_day = backup_time_day;
	}

	public String getBackup_time_hou() {
		return backup_time_hou;
	}

	public void setBackup_time_hou(String backup_time_hou) {
		this.backup_time_hou = backup_time_hou;
	}

	public String getBackup_day_stop() {
		return backup_day_stop;
	}

	public void setBackup_day_stop(String backup_day_stop) {
		this.backup_day_stop = backup_day_stop;
	}

	public String getBackup_week_stop() {
		return backup_week_stop;
	}

	public void setBackup_week_stop(String backup_week_stop) {
		this.backup_week_stop = backup_week_stop;
	}

	public String getBackup_month_stop() {
		return backup_month_stop;
	}

	public void setBackup_month_stop(String backup_month_stop) {
		this.backup_month_stop = backup_month_stop;
	}

	public String getBackup_season_stop() {
		return backup_season_stop;
	}

	public void setBackup_season_stop(String backup_season_stop) {
		this.backup_season_stop = backup_season_stop;
	}

	public String getBackup_year_stop() {
		return backup_year_stop;
	}

	public void setBackup_year_stop(String backup_year_stop) {
		this.backup_year_stop = backup_year_stop;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCheckupdateflag() {
		return checkupdateflag;
	}

	public void setCheckupdateflag(String checkupdateflag) {
		this.checkupdateflag = checkupdateflag;
	}
	
}
