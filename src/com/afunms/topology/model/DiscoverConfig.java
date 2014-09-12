/**
 * <p>Description:mapping table NMS_DISCOVER_CONDITION</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-12
 */

package com.afunms.topology.model;

import com.afunms.common.base.BaseVo;

public class DiscoverConfig extends BaseVo
{
    private int id;
    private String address;
    private String community;
    private String flag;
    private String shieldnetstart;
    private String shieldnetend;
    private String includenetstart;
    private String includenetend;
   
	public String getIncludenetstart() {
		return includenetstart;
	}
	public void setIncludenetstart(String includenetstart) {
		this.includenetstart = includenetstart;
	}
	public String getIncludenetend() {
		return includenetend;
	}
	public void setIncludenetend(String includenetend) {
		this.includenetend = includenetend;
	}
	
	
	
	public String getShieldnetend() {
		return shieldnetend;
	}
	public void setShieldnetend(String shieldnetend) {
		this.shieldnetend = shieldnetend;
	}
	
	public String getShieldnetstart() {
		return shieldnetstart;
	}
	public void setShieldnetstart(String shieldnetstart) {
		this.shieldnetstart = shieldnetstart;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCommunity() {
		return community;
	}
	public void setCommunity(String community) {
		this.community = community;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}    
}
