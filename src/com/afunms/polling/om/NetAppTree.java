package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

import com.afunms.common.base.BaseVo;

public class NetAppTree extends BaseVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String ipaddress;

	private String treeIndex;

	private String treeVolume;

	private String treeVolumeName;

	private String treeId;
	
	private String treeName;
	
	private String treeStatus;
	
	private String treeStyle;
	
	private String treeOpLocks;

	private Calendar collectTime;

	public String getIpaddress() {
		return ipaddress;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public String getTreeIndex() {
		return treeIndex;
	}

	public void setTreeIndex(String treeIndex) {
		this.treeIndex = treeIndex;
	}

	public String getTreeVolume() {
		return treeVolume;
	}

	public void setTreeVolume(String treeVolume) {
		this.treeVolume = treeVolume;
	}

	public String getTreeVolumeName() {
		return treeVolumeName;
	}

	public void setTreeVolumeName(String treeVolumeName) {
		this.treeVolumeName = treeVolumeName;
	}

	public String getTreeId() {
		return treeId;
	}

	public void setTreeId(String treeId) {
		this.treeId = treeId;
	}

	public String getTreeName() {
		return treeName;
	}

	public void setTreeName(String treeName) {
		this.treeName = treeName;
	}

	public String getTreeStatus() {
		return treeStatus;
	}

	public void setTreeStatus(String treeStatus) {
		this.treeStatus = treeStatus;
	}

	public String getTreeStyle() {
		return treeStyle;
	}

	public void setTreeStyle(String treeStyle) {
		this.treeStyle = treeStyle;
	}

	public String getTreeOpLocks() {
		return treeOpLocks;
	}

	public void setTreeOpLocks(String treeOpLocks) {
		this.treeOpLocks = treeOpLocks;
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
