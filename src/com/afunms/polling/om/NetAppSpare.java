package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

import com.afunms.common.base.BaseVo;

public class NetAppSpare extends BaseVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String ipaddress;
	
	private Calendar collectTime;

	private String spareIndex;

	private String spareDiskName;

	private String spareStatus;

	private String spareDiskId;
	
	private String spareScsiAdapter;
	
	private String spareScsiId;
	
	private String spareTotalMb;
	
	private String spareTotalBlocks;
	
	private String spareDiskPort;
	
	private String spareSecondaryDiskName;
	
	private String spareSecondaryDiskPort;
	
	private String spareShelf;
	
	private String spareBay;
	
	private String sparePool;
	
	private String spareSectorSize;
	
	private String spareDiskSerialNumber;
	
	private String spareDiskVendor;
	
	private String spareDiskModel;
	
	private String spareDiskFirmwareRevision;
	
	private String spareDiskRPM;
	
	private String spareDiskType;

	public String getIpaddress() {
		return ipaddress;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public Calendar getCollectTime() {
		return collectTime;
	}

	public void setCollectTime(Calendar collectTime) {
		this.collectTime = collectTime;
	}

	public String getSpareIndex() {
		return spareIndex;
	}

	public void setSpareIndex(String spareIndex) {
		this.spareIndex = spareIndex;
	}

	public String getSpareDiskName() {
		return spareDiskName;
	}

	public void setSpareDiskName(String spareDiskName) {
		this.spareDiskName = spareDiskName;
	}

	public String getSpareStatus() {
		return spareStatus;
	}

	public void setSpareStatus(String spareStatus) {
		this.spareStatus = spareStatus;
	}

	public String getSpareDiskId() {
		return spareDiskId;
	}

	public void setSpareDiskId(String spareDiskId) {
		this.spareDiskId = spareDiskId;
	}

	public String getSpareScsiAdapter() {
		return spareScsiAdapter;
	}

	public void setSpareScsiAdapter(String spareScsiAdapter) {
		this.spareScsiAdapter = spareScsiAdapter;
	}

	public String getSpareScsiId() {
		return spareScsiId;
	}

	public void setSpareScsiId(String spareScsiId) {
		this.spareScsiId = spareScsiId;
	}

	public String getSpareTotalMb() {
		return spareTotalMb;
	}

	public void setSpareTotalMb(String spareTotalMb) {
		this.spareTotalMb = spareTotalMb;
	}

	public String getSpareTotalBlocks() {
		return spareTotalBlocks;
	}

	public void setSpareTotalBlocks(String spareTotalBlocks) {
		this.spareTotalBlocks = spareTotalBlocks;
	}

	public String getSpareDiskPort() {
		return spareDiskPort;
	}

	public void setSpareDiskPort(String spareDiskPort) {
		this.spareDiskPort = spareDiskPort;
	}

	public String getSpareSecondaryDiskName() {
		return spareSecondaryDiskName;
	}

	public void setSpareSecondaryDiskName(String spareSecondaryDiskName) {
		this.spareSecondaryDiskName = spareSecondaryDiskName;
	}

	public String getSpareSecondaryDiskPort() {
		return spareSecondaryDiskPort;
	}

	public void setSpareSecondaryDiskPort(String spareSecondaryDiskPort) {
		this.spareSecondaryDiskPort = spareSecondaryDiskPort;
	}

	public String getSpareShelf() {
		return spareShelf;
	}

	public void setSpareShelf(String spareShelf) {
		this.spareShelf = spareShelf;
	}

	public String getSpareBay() {
		return spareBay;
	}

	public void setSpareBay(String spareBay) {
		this.spareBay = spareBay;
	}

	public String getSparePool() {
		return sparePool;
	}

	public void setSparePool(String sparePool) {
		this.sparePool = sparePool;
	}

	public String getSpareSectorSize() {
		return spareSectorSize;
	}

	public void setSpareSectorSize(String spareSectorSize) {
		this.spareSectorSize = spareSectorSize;
	}

	public String getSpareDiskSerialNumber() {
		return spareDiskSerialNumber;
	}

	public void setSpareDiskSerialNumber(String spareDiskSerialNumber) {
		this.spareDiskSerialNumber = spareDiskSerialNumber;
	}

	public String getSpareDiskVendor() {
		return spareDiskVendor;
	}

	public void setSpareDiskVendor(String spareDiskVendor) {
		this.spareDiskVendor = spareDiskVendor;
	}

	public String getSpareDiskModel() {
		return spareDiskModel;
	}

	public void setSpareDiskModel(String spareDiskModel) {
		this.spareDiskModel = spareDiskModel;
	}

	public String getSpareDiskFirmwareRevision() {
		return spareDiskFirmwareRevision;
	}

	public void setSpareDiskFirmwareRevision(String spareDiskFirmwareRevision) {
		this.spareDiskFirmwareRevision = spareDiskFirmwareRevision;
	}

	public String getSpareDiskRPM() {
		return spareDiskRPM;
	}

	public void setSpareDiskRPM(String spareDiskRPM) {
		this.spareDiskRPM = spareDiskRPM;
	}

	public String getSpareDiskType() {
		return spareDiskType;
	}

	public void setSpareDiskType(String spareDiskType) {
		this.spareDiskType = spareDiskType;
	}
	
	
	
	
}
