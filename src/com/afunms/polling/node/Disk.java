package com.afunms.polling.node;

public class Disk {
	private String name;
	private String Status;
	private String diskState;
	private String vendorID;
	private String productID;
	private String productRevision;
	private String dataCapacity;
	private String blockLength;
	private String address;
	private String nodeWWN;
	private String initializeState;
	private String redundancyGroup;
	private String volumeSetSerialNumber;
	private String serialNumber;
	private String firmwareRevision;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStatus() {
		return Status;
	}
	public void setStatus(String status) {
		Status = status;
	}
	public String getDiskState() {
		return diskState;
	}
	public void setDiskState(String diskState) {
		this.diskState = diskState;
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
	public String getDataCapacity() {
		return dataCapacity;
	}
	public void setDataCapacity(String dataCapacity) {
		this.dataCapacity = dataCapacity;
	}
	public String getBlockLength() {
		return blockLength;
	}
	public void setBlockLength(String blockLength) {
		this.blockLength = blockLength;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getNodeWWN() {
		return nodeWWN;
	}
	public void setNodeWWN(String nodeWWN) {
		this.nodeWWN = nodeWWN;
	}
	public String getInitializeState() {
		return initializeState;
	}
	public void setInitializeState(String initializeState) {
		this.initializeState = initializeState;
	}
	public String getRedundancyGroup() {
		return redundancyGroup;
	}
	public void setRedundancyGroup(String redundancyGroup) {
		this.redundancyGroup = redundancyGroup;
	}
	public String getVolumeSetSerialNumber() {
		return volumeSetSerialNumber;
	}
	public void setVolumeSetSerialNumber(String volumeSetSerialNumber) {
		this.volumeSetSerialNumber = volumeSetSerialNumber;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getFirmwareRevision() {
		return firmwareRevision;
	}
	public void setFirmwareRevision(String firmwareRevision) {
		this.firmwareRevision = firmwareRevision;
	}
	
}
