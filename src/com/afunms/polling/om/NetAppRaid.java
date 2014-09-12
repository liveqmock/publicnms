package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

import com.afunms.common.base.BaseVo;

public class NetAppRaid extends BaseVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String ipaddress;
	
	private Calendar collectTime;

	private String raidVIndex;

	private String raidVDiskName;

	private String raidVStatus;

	private String raidVDiskId;
	
	private String raidVScsiAdapter;
	
	private String raidVScsiId;
	
	private String raidVUsedMb;
	
	private String raidVUsedBlocks;
	
	private String raidVTotalMb;
	
	private String raidVTotalBlocks;
	
	private String raidVCompletionPerCent;
	
	private String raidVVol;
	
	private String raidVGroup;
	
	private String raidVDiskNumber;
	
	private String raidVGroupNumber;
	
	private String raidVDiskPort;
	
	private String raidVSecondaryDiskName;
	
	private String raidVSecondaryDiskPort;
	
	private String raidVShelf;
	
	private String raidVBay;
	
	private String raidVPlex;
	
	private String raidVPlexGroup;
	
	private String raidVPlexNumber;
	
	private String raidVPlexName;
	
	private String raidVSectorSize; //¿é´óÐ¡
	
	private String raidVDiskSerialNumber;
	
	private String raidVDiskVendor;
	
	private String raidVDiskModel;
	
	private String  raidVDiskFirmwareRevision;
	
	private String raidVDiskRPM;
	
	private String raidVDiskType;
	
	private String raidVDiskPool;
	
	private String raidVDiskCopyDestDiskName;

	public String getIpaddress() {
		return ipaddress;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public String getRaidVIndex() {
		return raidVIndex;
	}

	public void setRaidVIndex(String raidVIndex) {
		this.raidVIndex = raidVIndex;
	}

	public String getRaidVDiskName() {
		return raidVDiskName;
	}

	public void setRaidVDiskName(String raidVDiskName) {
		this.raidVDiskName = raidVDiskName;
	}

	public String getRaidVStatus() {
		return raidVStatus;
	}

	public void setRaidVStatus(String raidVStatus) {
		this.raidVStatus = raidVStatus;
	}

	public String getRaidVDiskId() {
		return raidVDiskId;
	}

	public void setRaidVDiskId(String raidVDiskId) {
		this.raidVDiskId = raidVDiskId;
	}

	public String getRaidVScsiAdapter() {
		return raidVScsiAdapter;
	}

	public void setRaidVScsiAdapter(String raidVScsiAdapter) {
		this.raidVScsiAdapter = raidVScsiAdapter;
	}

	public String getRaidVScsiId() {
		return raidVScsiId;
	}

	public void setRaidVScsiId(String raidVScsiId) {
		this.raidVScsiId = raidVScsiId;
	}

	public String getRaidVUsedMb() {
		return raidVUsedMb;
	}

	public void setRaidVUsedMb(String raidVUsedMb) {
		this.raidVUsedMb = raidVUsedMb;
	}

	public String getRaidVUsedBlocks() {
		return raidVUsedBlocks;
	}

	public void setRaidVUsedBlocks(String raidVUsedBlocks) {
		this.raidVUsedBlocks = raidVUsedBlocks;
	}

	public String getRaidVTotalMb() {
		return raidVTotalMb;
	}

	public void setRaidVTotalMb(String raidVTotalMb) {
		this.raidVTotalMb = raidVTotalMb;
	}

	public String getRaidVTotalBlocks() {
		return raidVTotalBlocks;
	}

	public void setRaidVTotalBlocks(String raidVTotalBlocks) {
		this.raidVTotalBlocks = raidVTotalBlocks;
	}

	public String getRaidVCompletionPerCent() {
		return raidVCompletionPerCent;
	}

	public void setRaidVCompletionPerCent(String raidVCompletionPerCent) {
		this.raidVCompletionPerCent = raidVCompletionPerCent;
	}

	public String getRaidVVol() {
		return raidVVol;
	}

	public void setRaidVVol(String raidVVol) {
		this.raidVVol = raidVVol;
	}

	public String getRaidVGroup() {
		return raidVGroup;
	}

	public void setRaidVGroup(String raidVGroup) {
		this.raidVGroup = raidVGroup;
	}

	public String getRaidVDiskNumber() {
		return raidVDiskNumber;
	}

	public void setRaidVDiskNumber(String raidVDiskNumber) {
		this.raidVDiskNumber = raidVDiskNumber;
	}

	public String getRaidVGroupNumber() {
		return raidVGroupNumber;
	}

	public void setRaidVGroupNumber(String raidVGroupNumber) {
		this.raidVGroupNumber = raidVGroupNumber;
	}

	public String getRaidVDiskPort() {
		return raidVDiskPort;
	}

	public void setRaidVDiskPort(String raidVDiskPort) {
		this.raidVDiskPort = raidVDiskPort;
	}

	public String getRaidVSecondaryDiskName() {
		return raidVSecondaryDiskName;
	}

	public void setRaidVSecondaryDiskName(String raidVSecondaryDiskName) {
		this.raidVSecondaryDiskName = raidVSecondaryDiskName;
	}

	public String getRaidVSecondaryDiskPort() {
		return raidVSecondaryDiskPort;
	}

	public void setRaidVSecondaryDiskPort(String raidVSecondaryDiskPort) {
		this.raidVSecondaryDiskPort = raidVSecondaryDiskPort;
	}

	public String getRaidVShelf() {
		return raidVShelf;
	}

	public void setRaidVShelf(String raidVShelf) {
		this.raidVShelf = raidVShelf;
	}

	public String getRaidVBay() {
		return raidVBay;
	}

	public void setRaidVBay(String raidVBay) {
		this.raidVBay = raidVBay;
	}

	public String getRaidVPlex() {
		return raidVPlex;
	}

	public void setRaidVPlex(String raidVPlex) {
		this.raidVPlex = raidVPlex;
	}

	public String getRaidVPlexGroup() {
		return raidVPlexGroup;
	}

	public void setRaidVPlexGroup(String raidVPlexGroup) {
		this.raidVPlexGroup = raidVPlexGroup;
	}

	public String getRaidVPlexNumber() {
		return raidVPlexNumber;
	}

	public void setRaidVPlexNumber(String raidVPlexNumber) {
		this.raidVPlexNumber = raidVPlexNumber;
	}

	public String getRaidVPlexName() {
		return raidVPlexName;
	}

	public void setRaidVPlexName(String raidVPlexName) {
		this.raidVPlexName = raidVPlexName;
	}

	public String getRaidVSectorSize() {
		return raidVSectorSize;
	}

	public void setRaidVSectorSize(String raidVSectorSize) {
		this.raidVSectorSize = raidVSectorSize;
	}

	public String getRaidVDiskSerialNumber() {
		return raidVDiskSerialNumber;
	}

	public void setRaidVDiskSerialNumber(String raidVDiskSerialNumber) {
		this.raidVDiskSerialNumber = raidVDiskSerialNumber;
	}

	public String getRaidVDiskVendor() {
		return raidVDiskVendor;
	}

	public void setRaidVDiskVendor(String raidVDiskVendor) {
		this.raidVDiskVendor = raidVDiskVendor;
	}

	public String getRaidVDiskModel() {
		return raidVDiskModel;
	}

	public void setRaidVDiskModel(String raidVDiskModel) {
		this.raidVDiskModel = raidVDiskModel;
	}

	public String getRaidVDiskRPM() {
		return raidVDiskRPM;
	}

	public void setRaidVDiskRPM(String raidVDiskRPM) {
		this.raidVDiskRPM = raidVDiskRPM;
	}

	public String getRaidVDiskType() {
		return raidVDiskType;
	}

	public void setRaidVDiskType(String raidVDiskType) {
		this.raidVDiskType = raidVDiskType;
	}

	public String getRaidVDiskPool() {
		return raidVDiskPool;
	}

	public void setRaidVDiskPool(String raidVDiskPool) {
		this.raidVDiskPool = raidVDiskPool;
	}

	public String getRaidVDiskCopyDestDiskName() {
		return raidVDiskCopyDestDiskName;
	}

	public void setRaidVDiskCopyDestDiskName(String raidVDiskCopyDestDiskName) {
		this.raidVDiskCopyDestDiskName = raidVDiskCopyDestDiskName;
	}

	public Calendar getCollectTime() {
		return collectTime;
	}

	public void setCollectTime(Calendar collectTime) {
		this.collectTime = collectTime;
	}

	public String getRaidVDiskFirmwareRevision() {
		return raidVDiskFirmwareRevision;
	}

	public void setRaidVDiskFirmwareRevision(String raidVDiskFirmwareRevision) {
		this.raidVDiskFirmwareRevision = raidVDiskFirmwareRevision;
	}
	
	
	
	
	
	
}
