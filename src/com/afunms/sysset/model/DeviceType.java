/**
 * <p>Description:mapping table NMS_DEVICE_TYPE</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-09
 */

package com.afunms.sysset.model;

import com.afunms.common.base.BaseVo;

public class DeviceType extends BaseVo
{
	private int id;
	private String sysOid; 
	private String descr; 
	private String image;     
	private int producer; 
    private int category;
	private String locate; 
	private String logTime;     
	    
	public DeviceType()
	{
		descr = null;
		image = null;	
		locate = null;
		logTime = null;
	}
	
	public int getCategory() {
		return category;
	}
	public void setCategory(int category) {
		this.category = category;
	}
	public String getDescr() {
		return descr;
	}
	public void setDescr(String descr) {
		this.descr = descr;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getSysOid() {
		return sysOid;
	}
	public void setSysOid(String sysOid) {
		this.sysOid = sysOid;
	}
	public int getProducer() {
		return producer;
	}
	public void setProducer(int producer) {
		this.producer = producer;
	}
	public String getLocate() {
		return locate;
	}
	public void setLocate(String locate) {
		this.locate = locate;
	}
	public String getLogTime() {
		return logTime;
	}
	public void setLogTime(String logTime) {
		this.logTime = logTime;
	} 	
}
