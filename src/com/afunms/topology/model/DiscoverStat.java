/**
 * <p>Description:mapping table NMS_DISCOVER_STAT</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-17
 */

package com.afunms.topology.model;

import com.afunms.common.base.BaseVo;

public class DiscoverStat extends BaseVo
{
	private int id;   
	private String startTime;
	private String endTime;
	private String elapseTime;
	private int hostTotal;
	private int subnetTotal;
	public String getElapseTime() {
		return elapseTime;
	}
	public void setElapseTime(String elapseTime) {
		this.elapseTime = elapseTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public int getHostTotal() {
		return hostTotal;
	}
	public void setHostTotal(int hostTotal) {
		this.hostTotal = hostTotal;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public int getSubnetTotal() {
		return subnetTotal;
	}
	public void setSubnetTotal(int subnetTotal) {
		this.subnetTotal = subnetTotal;
	}   	
}