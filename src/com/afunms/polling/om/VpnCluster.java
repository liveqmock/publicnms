package com.afunms.polling.om;

/**
 * author ChengFeng
 */
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class VpnCluster implements Serializable { 
	
    private Long id;

    private String ipaddress;
	
	private String type;
	
	private String subtype;
	
	private Calendar Collecttime; 
	
	private int clusterVirIndex;
	
	private int clusterId;
	
	private int clusterVirState;
	
	private String clusterVirIfname;
	
	private java.lang.String clusterVirAddr;
	
	private int clusterVirAuthType;
	
	private String clusterVirAuthPasswd;
	
	private int clusterVirPreempt;
	
	private int clusterVirInterval;
	
	private int clusterVirPriority;
	
	public VpnCluster(){}

//	public VpnCluster(Long id,String ipaddress,String type,String subtype,int clusterVirIndex
//			,int clusterId,int clusterVirState,String clusterVirIfname,java.lang.String clusterVirAddr
//			,int clusterVirAuthType,String clusterVirAuthPasswd,int clusterVirPreempt
//			,int clusterVirInterval,int clusterVirPriority,Calendar Collecttime){
//		
//    }

	public int getClusterVirIndex() {
		return clusterVirIndex;
	}

	public void setClusterVirIndex(int clusterVirIndex) {
		this.clusterVirIndex = clusterVirIndex;
	}

	public int getClusterId() {
		return clusterId;
	}

	public void setClusterId(int clusterId) {
		this.clusterId = clusterId;
	}

	public int getClusterVirState() {
		return clusterVirState;
	}

	public void setClusterVirState(int clusterVirState) {
		this.clusterVirState = clusterVirState;
	}

	public String getClusterVirIfname() {
		return clusterVirIfname;
	}

	public void setClusterVirIfname(String clusterVirIfname) {
		this.clusterVirIfname = clusterVirIfname;
	}

	public String getClusterVirAddr() {
		return clusterVirAddr;
	}

	public void setClusterVirAddr(String clusterVirAddr) {
		this.clusterVirAddr = clusterVirAddr;
	}

	public int getClusterVirAuthType() {
		return clusterVirAuthType;
	}

	public void setClusterVirAuthType(int clusterVirAuthType) {
		this.clusterVirAuthType = clusterVirAuthType;
	}

	public String getClusterVirAuthPasswd() {
		return clusterVirAuthPasswd;
	}

	public void setClusterVirAuthPasswd(String clusterVirAuthPasswd) {
		this.clusterVirAuthPasswd = clusterVirAuthPasswd;
	}

	public int getClusterVirPreempt() {
		return clusterVirPreempt;
	}

	public void setClusterVirPreempt(int clusterVirPreempt) {
		this.clusterVirPreempt = clusterVirPreempt;
	}

	public int getClusterVirInterval() {
		return clusterVirInterval;
	}

	public void setClusterVirInterval(int clusterVirInterval) {
		this.clusterVirInterval = clusterVirInterval;
	}

	public int getClusterVirPriority() {
		return clusterVirPriority;
	}

	public void setClusterVirPriority(int clusterVirPriority) {
		this.clusterVirPriority = clusterVirPriority;
	}


	public Calendar getCollecttime() {
		return Collecttime;
	}

	public void setCollecttime(Calendar collecttime) {
		Collecttime = collecttime;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
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
	

}
