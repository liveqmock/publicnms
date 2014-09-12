package com.afunms.config.model;

import java.io.Serializable;
import java.sql.Clob;

import com.afunms.common.base.BaseVo;

/** @author Hibernate CodeGenerator */
public class IpaddressPanel extends BaseVo implements Serializable {
	private Integer id;
	/** nullable persistent field */
	private String ipaddress;
	
	/** nullable persistent field */
	private String status;
	
	/** 
	 * identifier field
	 * This attribute is used to identify the same oid networking, 
	 * but different panels pictures
	 * 此属性用于标识具有相同oid的网络设备,其使用不同的面板模板图片
	 * @author nielin 
	 * add at 2010-01-08
	 * */
	private String imageType;
	
	/** full constructor */
	public IpaddressPanel(Integer id,String ipaddress,String status , String imageType) {		
		this.id = id;
		this.ipaddress = ipaddress;
		this.status = status;
		this.imageType = imageType;    // nielin add 2010-01-14
	}

	/** default constructor */
	public IpaddressPanel() {
	}

	/**
	 * @return
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @return
	 */
	public String getIpaddress() {
		return ipaddress;
	}

	/**
	 * @return
	 */
	public String getStatus() {
		return this.status;
	}

	/**
	 * @param id
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @param ipaddress
	 */
	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	/**
	 * @param status
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the imageType
	 */
	public String getImageType() {
		return imageType;
	}

	/**
	 * @param imageType the imageType to set
	 */
	public void setImageType(String imageType) {
		this.imageType = imageType;
	}


}
