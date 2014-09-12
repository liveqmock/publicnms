package com.afunms.polling.om.oraclerac;

import com.afunms.common.base.BaseVo;

public class Raccrstatusdata extends BaseVo {
	private int id;
	private String ipaddress;
	private String numNo;
	private String name;
	private String status;
	private String collecttime;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNumNo() {
		return numNo;
	}
	public void setNumNo(String numNo) {
		this.numNo = numNo;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCollecttime() {
		return collecttime;
	}
	public void setCollecttime(String collecttime) {
		this.collecttime = collecttime;
	}
	public String getIpaddress() {
		return ipaddress;
	}
	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}
}
