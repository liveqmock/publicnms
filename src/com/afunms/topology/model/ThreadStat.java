/**
 * <p>Description:mapping table NMS_THREAD_RUNNING_STAT</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-27
 */

package com.afunms.topology.model;

import com.afunms.common.base.BaseVo;

public class ThreadStat extends BaseVo
{
	private int id;
	private String name;
	private String startTime;	
	private String endTime;
	private String elapseTime;
	private int moidTotal;
	
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
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getMoidTotal() {
		return moidTotal;
	}
	public void setMoidTotal(int moidTotal) {
		this.moidTotal = moidTotal;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}    		
}