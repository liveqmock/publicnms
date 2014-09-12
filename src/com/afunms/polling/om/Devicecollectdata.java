package com.afunms.polling.om;

import java.io.Serializable;

public class Devicecollectdata implements Serializable {
	private Integer id;
	private String ipaddress;
	private String name;
	private String deviceindex;
	private String type;
	private String status;
	
	public Devicecollectdata(Integer id,String ipaddress,String name,String deviceindex,String type,String status){
		this.id = id;
		this.ipaddress = ipaddress;
		this.name = name;
		this.deviceindex = deviceindex;
		this.type = type;
		this.status = status;
	}

	public Devicecollectdata() {
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

	public String getDeviceindex() {
		return deviceindex;
	}

	public void setDeviceindex(String deviceindex) {
		this.deviceindex = deviceindex;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	};
	
	
}
