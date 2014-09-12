package com.afunms.system.vo;

import java.io.Serializable;

public class FdbVo implements Serializable{ 
	
	private String ifindex;
	
	private String port;
	
	private String ipadress;
	
	private String mac;
	
	private String collecttime;
	
	public String getCollecttime() {
		return collecttime;
	}

	public void setCollecttime(String collecttime) {
		this.collecttime = collecttime;
	}


	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getIfindex() {
		return ifindex;
	}

	public void setIfindex(String ifindex) {
		this.ifindex = ifindex;
	}

	public String getIpadress() {
		return ipadress;
	}

	public void setIpadress(String ipadress) {
		this.ipadress = ipadress;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}


}