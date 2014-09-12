package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

public class ArrayVPNCount  implements Serializable{
	
	private int id;
	 
	 private String ipaddress;
	 
	 private String type;
	 
	 private String subtype;
	 
	 private Calendar Collecttime;
	 
	 private int virtualSiteCount;

	 private int  vpnCount;

	 private int webCount;

	 private int vclientAppCount;

	 private int virtualSiteGroupCount;

	 private int tcsModuleCount;

	 private int imapsCount;

	 private int  smtpsCount;

	 private int appFilterCount;

	 private int  dvpnSiteCount;

	 private int dvpnResourceCount;

	 private int dvpnTunnelCount;

	 private int dvpnAclCount;

	 private int  maxCluster;

	 private int clusterNum;

	 private int rsCount;
	 
	 private int vsCount;

	 private int infNumber;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSubtype() {
		return subtype;
	}

	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}

	public Calendar getCollecttime() {
		return Collecttime;
	}

	public void setCollecttime(Calendar collecttime) {
		Collecttime = collecttime;
	}

	public int getVirtualSiteCount() {
		return virtualSiteCount;
	}

	public void setVirtualSiteCount(int virtualSiteCount) {
		this.virtualSiteCount = virtualSiteCount;
	}

	public int getVpnCount() {
		return vpnCount;
	}

	public void setVpnCount(int vpnCount) {
		this.vpnCount = vpnCount;
	}

	public int getWebCount() {
		return webCount;
	}

	public void setWebCount(int webCount) {
		this.webCount = webCount;
	}

	public int getVclientAppCount() {
		return vclientAppCount;
	}

	public void setVclientAppCount(int vclientAppCount) {
		this.vclientAppCount = vclientAppCount;
	}

	public int getVirtualSiteGroupCount() {
		return virtualSiteGroupCount;
	}

	public void setVirtualSiteGroupCount(int virtualSiteGroupCount) {
		this.virtualSiteGroupCount = virtualSiteGroupCount;
	}

	public int getTcsModuleCount() {
		return tcsModuleCount;
	}

	public void setTcsModuleCount(int tcsModuleCount) {
		this.tcsModuleCount = tcsModuleCount;
	}

	public int getImapsCount() {
		return imapsCount;
	}

	public void setImapsCount(int imapsCount) {
		this.imapsCount = imapsCount;
	}

	public int getSmtpsCount() {
		return smtpsCount;
	}

	public void setSmtpsCount(int smtpsCount) {
		this.smtpsCount = smtpsCount;
	}

	public int getAppFilterCount() {
		return appFilterCount;
	}

	public void setAppFilterCount(int appFilterCount) {
		this.appFilterCount = appFilterCount;
	}

	public int getDvpnSiteCount() {
		return dvpnSiteCount;
	}

	public void setDvpnSiteCount(int dvpnSiteCount) {
		this.dvpnSiteCount = dvpnSiteCount;
	}

	public int getDvpnResourceCount() {
		return dvpnResourceCount;
	}

	public void setDvpnResourceCount(int dvpnResourceCount) {
		this.dvpnResourceCount = dvpnResourceCount;
	}

	public int getDvpnTunnelCount() {
		return dvpnTunnelCount;
	}

	public void setDvpnTunnelCount(int dvpnTunnelCount) {
		this.dvpnTunnelCount = dvpnTunnelCount;
	}

	public int getDvpnAclCount() {
		return dvpnAclCount;
	}

	public void setDvpnAclCount(int dvpnAclCount) {
		this.dvpnAclCount = dvpnAclCount;
	}

	public int getMaxCluster() {
		return maxCluster;
	}

	public void setMaxCluster(int maxCluster) {
		this.maxCluster = maxCluster;
	}

	public int getClusterNum() {
		return clusterNum;
	}

	public void setClusterNum(int clusterNum) {
		this.clusterNum = clusterNum;
	}

	public int getRsCount() {
		return rsCount;
	}

	public void setRsCount(int rsCount) {
		this.rsCount = rsCount;
	}

	public int getVsCount() {
		return vsCount;
	}

	public void setVsCount(int vsCount) {
		this.vsCount = vsCount;
	}

	public int getInfNumber() {
		return infNumber;
	}

	public void setInfNumber(int infNumber) {
		this.infNumber = infNumber;
	}

	 

}
