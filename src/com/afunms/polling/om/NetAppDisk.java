package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

import com.afunms.common.base.BaseVo;

public class NetAppDisk extends BaseVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String ipaddress;

	private String dfIndex;

	private String dfFileSys;

	private String dfKBytesTotal;

	private String dfKBytesUsed;
	
	private String dfKBytesAvail;
	
	private String dfPerCentKBytesCapacity;
	
	private String dfInodesUsed;
	
	private String dfInodesFree;
	
	private String dfPerCentInodeCapacity;
	
	private String dfMountedOn;
	
	private String dfMaxFilesAvail;
	
	private String dfMaxFilesUsed;
	
	private String dfMaxFilesPossible;
	
	private String dfHighTotalKBytes;
	
	private String dfLowTotalKBytes;
	
	private String dfHighUsedKBytes;
	
	private String dfLowUsedKBytes;
	
	private String dfHighAvailKBytes;
	
	private String dfLowAvailKBytes;
	
	private String dfStatus;
	
	private String dfMirrorStatus;
	
	private String dfPlexCount;
	
	private String dfType;
	
	private Calendar collectTime;



	public String getIpaddress() {
		return ipaddress;
	}



	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}



	public String getDfIndex() {
		return dfIndex;
	}



	public void setDfIndex(String dfIndex) {
		this.dfIndex = dfIndex;
	}



	public String getDfFileSys() {
		return dfFileSys;
	}



	public void setDfFileSys(String dfFileSys) {
		this.dfFileSys = dfFileSys;
	}



	public String getDfKBytesTotal() {
		return dfKBytesTotal;
	}



	public void setDfKBytesTotal(String dfKBytesTotal) {
		this.dfKBytesTotal = dfKBytesTotal;
	}



	public String getDfKBytesUsed() {
		return dfKBytesUsed;
	}



	public void setDfKBytesUsed(String dfKBytesUsed) {
		this.dfKBytesUsed = dfKBytesUsed;
	}



	public String getDfKBytesAvail() {
		return dfKBytesAvail;
	}



	public void setDfKBytesAvail(String dfKBytesAvail) {
		this.dfKBytesAvail = dfKBytesAvail;
	}



	public String getDfPerCentKBytesCapacity() {
		return dfPerCentKBytesCapacity;
	}



	public void setDfPerCentKBytesCapacity(String dfPerCentKBytesCapacity) {
		this.dfPerCentKBytesCapacity = dfPerCentKBytesCapacity;
	}



	public String getDfInodesUsed() {
		return dfInodesUsed;
	}



	public void setDfInodesUsed(String dfInodesUsed) {
		this.dfInodesUsed = dfInodesUsed;
	}



	public String getDfInodesFree() {
		return dfInodesFree;
	}



	public void setDfInodesFree(String dfInodesFree) {
		this.dfInodesFree = dfInodesFree;
	}



	public String getDfPerCentInodeCapacity() {
		return dfPerCentInodeCapacity;
	}



	public void setDfPerCentInodeCapacity(String dfPerCentInodeCapacity) {
		this.dfPerCentInodeCapacity = dfPerCentInodeCapacity;
	}



	public String getDfMountedOn() {
		return dfMountedOn;
	}



	public void setDfMountedOn(String dfMountedOn) {
		this.dfMountedOn = dfMountedOn;
	}



	public String getDfMaxFilesAvail() {
		return dfMaxFilesAvail;
	}



	public void setDfMaxFilesAvail(String dfMaxFilesAvail) {
		this.dfMaxFilesAvail = dfMaxFilesAvail;
	}



	public String getDfMaxFilesUsed() {
		return dfMaxFilesUsed;
	}



	public void setDfMaxFilesUsed(String dfMaxFilesUsed) {
		this.dfMaxFilesUsed = dfMaxFilesUsed;
	}



	public String getDfMaxFilesPossible() {
		return dfMaxFilesPossible;
	}



	public void setDfMaxFilesPossible(String dfMaxFilesPossible) {
		this.dfMaxFilesPossible = dfMaxFilesPossible;
	}



	public String getDfHighTotalKBytes() {
		return dfHighTotalKBytes;
	}



	public void setDfHighTotalKBytes(String dfHighTotalKBytes) {
		this.dfHighTotalKBytes = dfHighTotalKBytes;
	}



	public String getDfLowTotalKBytes() {
		return dfLowTotalKBytes;
	}



	public void setDfLowTotalKBytes(String dfLowTotalKBytes) {
		this.dfLowTotalKBytes = dfLowTotalKBytes;
	}



	public String getDfHighUsedKBytes() {
		return dfHighUsedKBytes;
	}



	public void setDfHighUsedKBytes(String dfHighUsedKBytes) {
		this.dfHighUsedKBytes = dfHighUsedKBytes;
	}



	public String getDfLowUsedKBytes() {
		return dfLowUsedKBytes;
	}



	public void setDfLowUsedKBytes(String dfLowUsedKBytes) {
		this.dfLowUsedKBytes = dfLowUsedKBytes;
	}



	public String getDfHighAvailKBytes() {
		return dfHighAvailKBytes;
	}



	public void setDfHighAvailKBytes(String dfHighAvailKBytes) {
		this.dfHighAvailKBytes = dfHighAvailKBytes;
	}



	public String getDfLowAvailKBytes() {
		return dfLowAvailKBytes;
	}



	public void setDfLowAvailKBytes(String dfLowAvailKBytes) {
		this.dfLowAvailKBytes = dfLowAvailKBytes;
	}



	public String getDfStatus() {
		return dfStatus;
	}



	public void setDfStatus(String dfStatus) {
		this.dfStatus = dfStatus;
	}



	public String getDfMirrorStatus() {
		return dfMirrorStatus;
	}



	public void setDfMirrorStatus(String dfMirrorStatus) {
		this.dfMirrorStatus = dfMirrorStatus;
	}



	public String getDfPlexCount() {
		return dfPlexCount;
	}



	public void setDfPlexCount(String dfPlexCount) {
		this.dfPlexCount = dfPlexCount;
	}



	public String getDfType() {
		return dfType;
	}



	public void setDfType(String dfType) {
		this.dfType = dfType;
	}



	public Calendar getCollectTime() {
		return collectTime;
	}



	public void setCollectTime(Calendar collectTime) {
		this.collectTime = collectTime;
	}



	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	
	
	
}
