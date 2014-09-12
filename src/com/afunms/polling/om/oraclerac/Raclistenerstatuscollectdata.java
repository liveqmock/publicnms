package com.afunms.polling.om.oraclerac;

import com.afunms.common.base.BaseVo;

public class Raclistenerstatuscollectdata extends BaseVo {
	private int id;
	private String ipaddress;
	private String listenerstatus;
	private String listenerstatusrun;
	private String listenerstatusnode;
	private String collecttime;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getIpaddress() {
		return ipaddress;
	}
	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}
	public String getListenerstatus() {
		return listenerstatus;
	}
	public void setListenerstatus(String listenerstatus) {
		this.listenerstatus = listenerstatus;
	}
	public String getListenerstatusrun() {
		return listenerstatusrun;
	}
	public void setListenerstatusrun(String listenerstatusrun) {
		this.listenerstatusrun = listenerstatusrun;
	}
	public String getListenerstatusnode() {
		return listenerstatusnode;
	}
	public void setListenerstatusnode(String listenerstatusnode) {
		this.listenerstatusnode = listenerstatusnode;
	}
	public String getCollecttime() {
		return collecttime;
	}
	public void setCollecttime(String collecttime) {
		this.collecttime = collecttime;
	}
}
