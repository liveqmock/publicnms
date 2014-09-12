/**
 * <p>Description:mapping table NMS_TOPO_NODE</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-12
 */

package com.afunms.topology.model;

import com.afunms.common.base.BaseVo;

public class EquipImage extends BaseVo
{
	private int id; 
	
	private int category; 
	private String cnName;  
	private String enName;
	private String topoImage;
	private String lostImage;
	private String alarmImage;
	private String path;
	
	public String getAlarmImage() {
		return alarmImage;
	}
	public void setAlarmImage(String alarmImage) {
		this.alarmImage = alarmImage;
	}
	public int getCategory() {
		return category;
	}
	public void setCategory(int category) {
		this.category = category;
	}
	public String getCnName() {
		return cnName;
	}
	public void setCnName(String cnName) {
		this.cnName = cnName;
	}
	public String getEnName() {
		return enName;
	}
	public void setEnName(String enName) {
		this.enName = enName;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getLostImage() {
		return lostImage;
	}
	public void setLostImage(String lostImage) {
		this.lostImage = lostImage;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getTopoImage() {
		return topoImage;
	}
	public void setTopoImage(String topoImage) {
		this.topoImage = topoImage;
	}
	
}