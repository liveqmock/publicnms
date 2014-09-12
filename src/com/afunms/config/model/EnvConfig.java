package com.afunms.config.model;

import com.afunms.common.base.BaseVo;

/**
 * @description 动力环境告警配置
 * @author wangxiangyong
 * @date Dec 27, 2011
 */
public class EnvConfig extends BaseVo{
	private Integer id;
	private String ipaddress;// 设备IP
	private String name; // 模块名称
	private Integer enabled;// 是否发送告警
	private Integer alarmvalue;//告警限值
	private String alarmlevel;// 告警级别（1：普通；2：严重；3：紧急）
	private Integer alarmtimes;//告警次数
	private String entity;
	private String bak;//备注
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getEnabled() {
		return enabled;
	}

	public void setEnabled(Integer enabled) {
		this.enabled = enabled;
	}

	public Integer getAlarmvalue() {
		return alarmvalue;
	}

	public void setAlarmvalue(Integer alarmvalue) {
		this.alarmvalue = alarmvalue;
	}

	public String getAlarmlevel() {
		return alarmlevel;
	}

	public void setAlarmlevel(String alarmlevel) {
		this.alarmlevel = alarmlevel;
	}

	public Integer getAlarmtimes() {
		return alarmtimes;
	}

	public void setAlarmtimes(Integer alarmtimes) {
		this.alarmtimes = alarmtimes;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getBak() {
		return bak;
	}

	public void setBak(String bak) {
		this.bak = bak;
	}

}
