package com.afunms.config.model;

import com.afunms.common.base.BaseVo;

public class PolicyInterface extends BaseVo{
	private int id;
	private String interfaceName;
	private String policyName;
	private String className;
	private int offeredRate;
	private int dropRate;
	private String matchGroup;
	private int matchedPkts;
	private int matchedBytes;
	private int dropsTotal;
	private int dropsBytes;
	private int depth;
	private int totalQueued; 
	private int noBufferDrop;
    private String collecttime;
    
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public String getPolicyName() {
		return policyName;
	}

	public void setPolicyName(String policyName) {
		this.policyName = policyName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public int getOfferedRate() {
		return offeredRate;
	}

	public void setOfferedRate(int offeredRate) {
		this.offeredRate = offeredRate;
	}

	public int getDropRate() {
		return dropRate;
	}

	public void setDropRate(int dropRate) {
		this.dropRate = dropRate;
	}

	

	public String getMatchGroup() {
		return matchGroup;
	}

	public void setMatchGroup(String matchGroup) {
		this.matchGroup = matchGroup;
	}

	public int getMatchedPkts() {
		return matchedPkts;
	}

	public void setMatchedPkts(int matchedPkts) {
		this.matchedPkts = matchedPkts;
	}

	public int getMatchedBytes() {
		return matchedBytes;
	}

	public void setMatchedBytes(int matchedBytes) {
		this.matchedBytes = matchedBytes;
	}

	public int getDropsTotal() {
		return dropsTotal;
	}

	public void setDropsTotal(int dropsTotal) {
		this.dropsTotal = dropsTotal;
	}

	public int getDropsBytes() {
		return dropsBytes;
	}

	public void setDropsBytes(int dropsBytes) {
		this.dropsBytes = dropsBytes;
	}
    
	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public int getTotalQueued() {
		return totalQueued;
	}

	public void setTotalQueued(int totalQueued) {
		this.totalQueued = totalQueued;
	}

	public int getNoBufferDrop() {
		return noBufferDrop;
	}

	public void setNoBufferDrop(int noBufferDrop) {
		this.noBufferDrop = noBufferDrop;
	}

	public String getCollecttime() {
		return collecttime;
	}

	public void setCollecttime(String collecttime) {
		this.collecttime = collecttime;
	}
	

}
