package com.afunms.polling.om;

import java.io.Serializable;

public class Servicecollectdata implements Serializable{
	private Integer id;
	private String ipaddress;
	private String name;
	private String instate;
	private String opstate;
	private String uninst;
	private String paused;
	
	/**
	 * 启动模式
	 */
	private String startMode;
	
	/**
	 * 路径
	 */
	private String pathName;
	
	/**
	 * 描述
	 */
	private String description;
	
	/**
	 * 服务类型
	 */
	private String serviceType;
	
	private String pid;
	
	private String groupstr;
	
	private String collecttime;
	
	public String getStartMode() {
		return startMode;
	}
	public void setStartMode(String startMode) {
		this.startMode = startMode;
	}
	public String getPathName() {
		return pathName;
	}
	public void setPathName(String pathName) {
		this.pathName = pathName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getGroupstr() {
		return groupstr;
	}
	public void setGroupstr(String groupstr) {
		this.groupstr = groupstr;
	}
	public String getCollecttime() {
		return collecttime;
	}
	public void setCollecttime(String collecttime) {
		this.collecttime = collecttime;
	}
	public Servicecollectdata(Integer id,String ipaddress,String name,String instate,String opstate,String uninst,String paused){
		this.id = id;
		this.ipaddress = ipaddress;
		this.name = name;
		this.instate = instate;
		this.opstate = opstate;
		this.uninst = uninst;
		this.paused = paused;	
	}
	public Servicecollectdata(){
		
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getInstate() {
		return instate;
	}
	public void setInstate(String instate) {
		this.instate = instate;
	}
	public String getOpstate() {
		return opstate;
	}
	public void setOpstate(String opstate) {
		this.opstate = opstate;
	}
	public String getUninst() {
		return uninst;
	}
	public void setUninst(String uninst) {
		this.uninst = uninst;
	}
	public String getPaused() {
		return paused;
	}
	public void setPaused(String paused) {
		this.paused = paused;
	}
	public String getIpaddress() {
		return ipaddress;
	}
	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}
	
}
