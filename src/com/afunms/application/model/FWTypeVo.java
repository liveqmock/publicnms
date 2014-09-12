package com.afunms.application.model;

import com.afunms.common.base.BaseVo;

public class FWTypeVo extends BaseVo
{
	private int id;
	private String firewalltype;
	private String firewalldesc;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFirewalltype() {
		return firewalltype;
	}
	public void setFirewalltype(String firewalltype) {
		this.firewalltype = firewalltype;
	}
	public String getFirewalldesc() {
		return firewalldesc;
	}
	public void setFirewalldesc(String firewalldesc) {
		this.firewalldesc = firewalldesc;
	}
	

	
}
