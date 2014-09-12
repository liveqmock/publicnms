package com.afunms.detail.reomte.model;

public class InterfaceInfo {
	
	/**
	 * 索引
	 */
	private String sindex;
	
	/**
	 * 描述
	 */
	private String ifDescr;
	
	/**
	 * 关联应用
	 */
	private String LinkUse;
	
	/**
	 * 每秒字节数(M)
	 */
	private String ifSpeed;
	
	/**
	 * 状态
	 */
	private String ifOperStatus;
	
	/**
	 * 出口广播数据包
	 */
	private String ifOutBroadcastPkts;
	
	/**
	 * 入口广播数据包
	 */
	private String ifInBroadcastPkts;
	
	/**
	 * 出口多播数据包
	 */
	private String ifOutMulticastPkts;
	
	/**
	 * 入口多播数据包
	 */
	private String ifInMulticastPkts;
	/**
	 * 出口流速
	 */
	private String outBandwidthUtilHdx;
	
	/**
	 * 入口流速
	 */
	private String inBandwidthUtilHdx;

	//######################HONG ADD
	/**
	 *端口总流速  
	 */
	private String  allBandwidthUtilHdx;
	
	/**
	 * 类型
	 */
	private String ifType;
	
	
	/**
	 * 最大数据包
	 */
	private String ifMtu;
	

	/**
	 * 预期状态
	 */
	private String ifAdminStatus;
	

	/**
	 * 86端口入口带宽利用率
	 */
	private String InBandwidthUtilHdxPerc;
	

	/**
	 * 86端口出口带宽利用率
	 */
	private String OutBandwidthUtilHdxPerc;
	

	/**
	 * @return the sindex
	 */
	public String getSindex() {
		return sindex;
	}

	/**
	 * @param sindex the sindex to set
	 */
	public void setSindex(String sindex) {
		this.sindex = sindex;
	}

	/**
	 * @return the ifDescr
	 */
	public String getIfDescr() {
		return ifDescr;
	}

	/**
	 * @param ifDescr the ifDescr to set
	 */
	public void setIfDescr(String ifDescr) {
		this.ifDescr = ifDescr;
	}

	/**
	 * @return the linkUse
	 */
	public String getLinkUse() {
		return LinkUse;
	}

	/**
	 * @param linkUse the linkUse to set
	 */
	public void setLinkUse(String linkUse) {
		LinkUse = linkUse;
	}

	/**
	 * @return the ifSpeed
	 */
	public String getIfSpeed() {
		return ifSpeed;
	}

	/**
	 * @param ifSpeed the ifSpeed to set
	 */
	public void setIfSpeed(String ifSpeed) {
		this.ifSpeed = ifSpeed;
	}

	/**
	 * @return the ifOperStatus
	 */
	public String getIfOperStatus() {
		return ifOperStatus;
	}

	/**
	 * @param ifOperStatus the ifOperStatus to set
	 */
	public void setIfOperStatus(String ifOperStatus) {
		this.ifOperStatus = ifOperStatus;
	}

	/**
	 * @return the ifOutBroadcastPkts
	 */
	public String getIfOutBroadcastPkts() {
		return ifOutBroadcastPkts;
	}

	/**
	 * @param ifOutBroadcastPkts the ifOutBroadcastPkts to set
	 */
	public void setIfOutBroadcastPkts(String ifOutBroadcastPkts) {
		this.ifOutBroadcastPkts = ifOutBroadcastPkts;
	}

	/**
	 * @return the ifInBroadcastPkts
	 */
	public String getIfInBroadcastPkts() {
		return ifInBroadcastPkts;
	}

	/**
	 * @param ifInBroadcastPkts the ifInBroadcastPkts to set
	 */
	public void setIfInBroadcastPkts(String ifInBroadcastPkts) {
		this.ifInBroadcastPkts = ifInBroadcastPkts;
	}

	/**
	 * @return the ifOutMulticastPkts
	 */
	public String getIfOutMulticastPkts() {
		return ifOutMulticastPkts;
	}

	/**
	 * @param ifOutMulticastPkts the ifOutMulticastPkts to set
	 */
	public void setIfOutMulticastPkts(String ifOutMulticastPkts) {
		this.ifOutMulticastPkts = ifOutMulticastPkts;
	}

	/**
	 * @return the ifInMulticastPkts
	 */
	public String getIfInMulticastPkts() {
		return ifInMulticastPkts;
	}

	/**
	 * @param ifInMulticastPkts the ifInMulticastPkts to set
	 */
	public void setIfInMulticastPkts(String ifInMulticastPkts) {
		this.ifInMulticastPkts = ifInMulticastPkts;
	}

	/**
	 * @return the outBandwidthUtilHdx
	 */
	public String getOutBandwidthUtilHdx() {
		return outBandwidthUtilHdx;
	}

	/**
	 * @param outBandwidthUtilHdx the outBandwidthUtilHdx to set
	 */
	public void setOutBandwidthUtilHdx(String outBandwidthUtilHdx) {
		this.outBandwidthUtilHdx = outBandwidthUtilHdx;
	}

	/**
	 * @return the inBandwidthUtilHdx
	 */
	public String getInBandwidthUtilHdx() {
		return inBandwidthUtilHdx;
	}

	/**
	 * @param inBandwidthUtilHdx the inBandwidthUtilHdx to set
	 */
	public void setInBandwidthUtilHdx(String inBandwidthUtilHdx) {
		this.inBandwidthUtilHdx = inBandwidthUtilHdx;
	}

	public String getAllBandwidthUtilHdx() {
		return allBandwidthUtilHdx;
	}

	public void setAllBandwidthUtilHdx(String allBandwidthUtilHdx) {
		this.allBandwidthUtilHdx = allBandwidthUtilHdx;
	}

	public String getIfType() {
		return ifType;
	}

	public void setIfType(String ifType) {
		this.ifType = ifType;
	}

	public String getIfMtu() {
		return ifMtu;
	}

	public void setIfMtu(String ifMtu) {
		this.ifMtu = ifMtu;
	}

	public String getIfAdminStatus() {
		return ifAdminStatus;
	}

	public void setIfAdminStatus(String ifAdminStatus) {
		this.ifAdminStatus = ifAdminStatus;
	}

	public String getInBandwidthUtilHdxPerc() {
		return InBandwidthUtilHdxPerc;
	}

	public void setInBandwidthUtilHdxPerc(String inBandwidthUtilHdxPerc) {
		InBandwidthUtilHdxPerc = inBandwidthUtilHdxPerc;
	}

	public String getOutBandwidthUtilHdxPerc() {
		return OutBandwidthUtilHdxPerc;
	}

	public void setOutBandwidthUtilHdxPerc(String outBandwidthUtilHdxPerc) {
		OutBandwidthUtilHdxPerc = outBandwidthUtilHdxPerc;
	}
}
