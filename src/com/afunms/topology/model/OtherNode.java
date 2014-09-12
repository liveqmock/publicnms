/**
 * <p>Description:mapping table nms_hint_node</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2010-01-4
 */

package com.afunms.topology.model;

import com.afunms.common.base.BaseVo;

public class OtherNode extends BaseVo {
	
	private int id;
	
	private String name;
	
	private String ipAddress;

	private String alais;

	private int category;

	private String collecttype;
	
	private String sendmobiles;
	
	private String sendemail;
	
	private String sendphone;
	
	private String bid;
	
	private int managed;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAlais() {
		return alais;
	}

	public void setAlais(String alais) {
		this.alais = alais;
	}

	public String getBid() {
		return bid;
	}

	public void setBid(String bid) {
		this.bid = bid;
	}

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public String getCollecttype() {
		return collecttype;
	}

	public void setCollecttype(String collecttype) {
		this.collecttype = collecttype;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSendemail() {
		return sendemail;
	}

	public void setSendemail(String sendemail) {
		this.sendemail = sendemail;
	}

	public String getSendmobiles() {
		return sendmobiles;
	}

	public void setSendmobiles(String sendmobiles) {
		this.sendmobiles = sendmobiles;
	}

	public String getSendphone() {
		return sendphone;
	}

	public void setSendphone(String sendphone) {
		this.sendphone = sendphone;
	}

	public int getManaged() {
		return managed;
	}

	public void setManaged(int managed) {
		this.managed = managed;
	}

}
