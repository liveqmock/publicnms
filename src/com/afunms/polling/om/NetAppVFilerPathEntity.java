package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

import com.afunms.common.base.BaseVo;

public class NetAppVFilerPathEntity extends BaseVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String ipaddress;
	
	private Calendar collectTime;

	private String vfFsIndex;

	private String vfSpIndex;

	private String vfSpName;

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

	public String getVfFsIndex() {
		return vfFsIndex;
	}

	public void setVfFsIndex(String vfFsIndex) {
		this.vfFsIndex = vfFsIndex;
	}

	public String getVfSpIndex() {
		return vfSpIndex;
	}

	public void setVfSpIndex(String vfSpIndex) {
		this.vfSpIndex = vfSpIndex;
	}

	public String getVfSpName() {
		return vfSpName;
	}

	public void setVfSpName(String vfSpName) {
		this.vfSpName = vfSpName;
	}

}
