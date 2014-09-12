package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

import com.afunms.common.base.BaseVo;

public class NetAppVFilerProtocolEntity extends BaseVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String ipaddress;
	
	private Calendar collectTime;

	private String vfFpIndex;

	private String vfProIndex;

	private String vfProName;
	
	private String vfProStatus;

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

	public String getVfFpIndex() {
		return vfFpIndex;
	}

	public void setVfFpIndex(String vfFpIndex) {
		this.vfFpIndex = vfFpIndex;
	}

	public String getVfProIndex() {
		return vfProIndex;
	}

	public void setVfProIndex(String vfProIndex) {
		this.vfProIndex = vfProIndex;
	}

	public String getVfProName() {
		return vfProName;
	}

	public void setVfProName(String vfProName) {
		this.vfProName = vfProName;
	}

	public String getVfProStatus() {
		return vfProStatus;
	}

	public void setVfProStatus(String vfProStatus) {
		this.vfProStatus = vfProStatus;
	}

	
}
