package com.afunms.polling.om.oraclerac;

import com.afunms.common.base.BaseVo;

public class Raccrstatuscollectdata extends BaseVo {
	private int id;
	private String ipaddress;
	private String raccrstatus;
	private String collecttime;
	public String getRaccrstatus() {
		return raccrstatus;
	}
	public void setRaccrstatus(String raccrstatus) {
		this.raccrstatus = raccrstatus;
	}
	public String getCollecttime() {
		return collecttime;
	}
	public void setCollecttime(String collecttime) {
		this.collecttime = collecttime;
	}
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

}
