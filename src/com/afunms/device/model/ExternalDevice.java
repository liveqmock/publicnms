package com.afunms.device.model;

import com.afunms.common.base.BaseVo;

public class ExternalDevice extends BaseVo {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8941234230821527451L;
	private int id;// 设备ID
	private String osType;// 系统类型
	private String brand;// 品牌
	private String deviceType;// 设备类型
	private String specification;// 规格型号
	private String supplier;// 供应商
	private String insureder;// 承包单位
	 private String deviceId;//设备ID
	private String ipaddress;// ip
	private int cabinetId;// 机柜ID

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getOsType() {
		return osType;
	}

	public void setOsType(String osType) {
		this.osType = osType;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getSpecification() {
		return specification;
	}

	public void setSpecification(String specification) {
		this.specification = specification;
	}

	public String getSupplier() {
		return supplier;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public String getInsureder() {
		return insureder;
	}

	public void setInsureder(String insureder) {
		this.insureder = insureder;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public int getCabinetId() {
		return cabinetId;
	}

	public void setCabinetId(int cabinetId) {
		this.cabinetId = cabinetId;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

}
