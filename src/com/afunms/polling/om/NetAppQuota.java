package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

import com.afunms.common.base.BaseVo;

public class NetAppQuota extends BaseVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String ipaddress; // ≈‰∂ÓId

	private String quotaId; // ≈‰∂ÓId

	private String quotaName;// ≈‰∂Ó√˚◊÷

	private String quotaState;// ≈‰∂Ó◊¥Ã¨

	private String quotaInitPercent;// ≈‰∂Ó≥ı ºªØ±»¿˝

	private Calendar collectTime; // ≈‰∂Ó≤…ºØ ±º‰

	public String getQuotaId() {
		return quotaId;
	}

	public void setQuotaId(String quotaId) {
		this.quotaId = quotaId;
	}

	public String getQuotaName() {
		return quotaName;
	}

	public void setQuotaName(String quotaName) {
		this.quotaName = quotaName;
	}

	public String getQuotaState() {
		return quotaState;
	}

	public void setQuotaState(String quotaState) {
		this.quotaState = quotaState;
	}

	public String getQuotaInitPercent() {
		return quotaInitPercent;
	}

	public void setQuotaInitPercent(String quotaInitPercent) {
		this.quotaInitPercent = quotaInitPercent;
	}

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

}
