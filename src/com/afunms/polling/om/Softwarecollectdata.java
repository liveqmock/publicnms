package com.afunms.polling.om;

import java.io.Serializable;

public class Softwarecollectdata implements Serializable {
	private Integer id;
	private String ipaddress;
	private String name;
	private String swid;
	private String type;
	private String insdate;
	
	public Softwarecollectdata(Integer id,String ipaddress,String name,String swid,String type,String insdate){
		this.id = id;
		this.ipaddress = ipaddress;
		this.name = name;
		this.swid = swid;
		this.type = type;
		this.insdate = insdate;
	}

	public Softwarecollectdata() {
	}
	

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

	public String getSwid() {
		return swid;
	}

	public void setSwid(String swid) {
		this.swid = swid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getInsdate() {
		return insdate;
	}

	public void setInsdate(String insdate) {
		this.insdate = insdate;
	};
	
	
}
