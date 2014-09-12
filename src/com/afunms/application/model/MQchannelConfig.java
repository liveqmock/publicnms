/**
 * <p>Description:mapping app_db_node</p>
 * <p>Company: dhcc.com</p>
 * @author miiwill
 * @project afunms
 * @date 2007-1-7
 */

package com.afunms.application.model;

import com.afunms.common.base.BaseVo;

public class MQchannelConfig extends BaseVo {

	private int id;
	private String ipaddress;
	private int chlindex;
	private String chlname;
	private String linkuse;
	private int sms;
	private String bak;
	private int reportflag;
	private String connipaddress;
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
	public int getChlindex() {
		return chlindex;
	}
	public void setChlindex(int chlindex) {
		this.chlindex = chlindex;
	}
	public String getChlname() {
		return chlname;
	}
	public void setChlname(String chlname) {
		this.chlname = chlname;
	}
	public String getLinkuse() {
		return linkuse;
	}
	public void setLinkuse(String linkuse) {
		this.linkuse = linkuse;
	}
	public int getSms() {
		return sms;
	}
	public void setSms(int sms) {
		this.sms = sms;
	}
	public String getBak() {
		return bak;
	}
	public void setBak(String bak) {
		this.bak = bak;
	}
	public int getReportflag() {
		return reportflag;
	}
	public void setReportflag(int reportflag) {
		this.reportflag = reportflag;
	}
	public String getConnipaddress() {
		return connipaddress;
	}
	public void setConnipaddress(String connipaddress) {
		this.connipaddress = connipaddress;
	}
}
