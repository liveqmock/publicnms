package com.afunms.polling.om;

import java.io.Serializable;

public class Storagecollectdata implements Serializable {
	private Integer id;
	private String ipaddress;
	private String name;
	private String storageindex;
	private String type;
	private String cap;
	
	public Storagecollectdata(Integer id,String ipaddress,String name,String storageindex,String type,String cap){
		this.id = id;
		this.ipaddress = ipaddress;
		this.name = name;
		this.storageindex = storageindex;
		this.type = type;
		this.cap = cap;
	}

	public Storagecollectdata() {
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

	public String getStorageindex() {
		return storageindex;
	}

	public void setStorageindex(String storageindex) {
		this.storageindex = storageindex;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCap() {
		return cap;
	}

	public void setCap(String cap) {
		this.cap = cap;
	};
	
	
}
