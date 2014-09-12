package com.afunms.polling.node;

import java.util.List;

public class Controller {
	
	private String name;
	private String status;
	private String serialNumber;
	private String vendorID;
	private String productID;
	private String productRevision;
	private String firmwareRevision;
	private String manufacturingProductCode;
	private String controllerType;
	private String batteryChargerFirmwareRevision;
	List<CtrlPort> frontPortList;
	List<CtrlPort> backPortList;
	private Battery battery;
	private Processor processor;
	List<DIMM> dimmList;
	
	private String enclosureSwitchSetting;
	private String driveAddressBasis;
	private String enclosureID;
	private String loopPair;
	private String loopID;
	private String hardAddress;
	
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
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getVendorID() {
		return vendorID;
	}
	public void setVendorID(String vendorID) {
		this.vendorID = vendorID;
	}
	public String getProductID() {
		return productID;
	}
	public void setProductID(String productID) {
		this.productID = productID;
	}
	public String getProductRevision() {
		return productRevision;
	}
	public void setProductRevision(String productRevision) {
		this.productRevision = productRevision;
	}
	public String getFirmwareRevision() {
		return firmwareRevision;
	}
	public void setFirmwareRevision(String firmwareRevision) {
		this.firmwareRevision = firmwareRevision;
	}
	public String getManufacturingProductCode() {
		return manufacturingProductCode;
	}
	public void setManufacturingProductCode(String manufacturingProductCode) {
		this.manufacturingProductCode = manufacturingProductCode;
	}
	public String getControllerType() {
		return controllerType;
	}
	public void setControllerType(String controllerType) {
		this.controllerType = controllerType;
	}
	public String getBatteryChargerFirmwareRevision() {
		return batteryChargerFirmwareRevision;
	}
	public void setBatteryChargerFirmwareRevision(
			String batteryChargerFirmwareRevision) {
		this.batteryChargerFirmwareRevision = batteryChargerFirmwareRevision;
	}
	public List<CtrlPort> getFrontPortList() {
		return frontPortList;
	}
	public void setFrontPortList(List<CtrlPort> frontPortList) {
		this.frontPortList = frontPortList;
	}
	public List<CtrlPort> getBackPortList() {
		return backPortList;
	}
	public void setBackPortList(List<CtrlPort> backPortList) {
		this.backPortList = backPortList;
	}
	public Battery getBattery() {
		return battery;
	}
	public void setBattery(Battery battery) {
		this.battery = battery;
	}
	public Processor getProcessor() {
		return processor;
	}
	public void setProcessor(Processor processor) {
		this.processor = processor;
	}
	public List<DIMM> getDimmList() {
		return dimmList;
	}
	public void setDimmList(List<DIMM> dimmList) {
		this.dimmList = dimmList;
	}
	public String getEnclosureSwitchSetting() {
		return enclosureSwitchSetting;
	}
	public void setEnclosureSwitchSetting(String enclosureSwitchSetting) {
		this.enclosureSwitchSetting = enclosureSwitchSetting;
	}
	public String getDriveAddressBasis() {
		return driveAddressBasis;
	}
	public void setDriveAddressBasis(String driveAddressBasis) {
		this.driveAddressBasis = driveAddressBasis;
	}
	public String getEnclosureID() {
		return enclosureID;
	}
	public void setEnclosureID(String enclosureID) {
		this.enclosureID = enclosureID;
	}
	public String getLoopPair() {
		return loopPair;
	}
	public void setLoopPair(String loopPair) {
		this.loopPair = loopPair;
	}
	public String getLoopID() {
		return loopID;
	}
	public void setLoopID(String loopID) {
		this.loopID = loopID;
	}
	public String getHardAddress() {
		return hardAddress;
	}
	public void setHardAddress(String hardAddress) {
		this.hardAddress = hardAddress;
	}	
}