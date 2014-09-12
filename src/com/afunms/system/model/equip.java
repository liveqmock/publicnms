/**
 * <p>Description:mapping table NMS_USER</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.system.model;

import com.afunms.common.base.BaseVo;

public class equip extends BaseVo
{
    private int id;
    private int category;
    private String cn_name;
    private String en_name;
    private String topo_image;
    private String lost_image;
    private String alarm_image;
    private String path;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getCategory() {
		return category;
	}
	public void setCategory(int category) {
		this.category = category;
	}
	public String getCn_name() {
		return cn_name;
	}
	public void setCn_name(String cn_name) {
		this.cn_name = cn_name;
	}
	public String getEn_name() {
		return en_name;
	}
	public void setEn_name(String en_name) {
		this.en_name = en_name;
	}
	public String getTopo_image() {
		return topo_image;
	}
	public void setTopo_image(String topo_image) {
		this.topo_image = topo_image;
	}
	public String getLost_image() {
		return lost_image;
	}
	public void setLost_image(String lost_image) {
		this.lost_image = lost_image;
	}
	public String getAlarm_image() {
		return alarm_image;
	}
	public void setAlarm_image(String alarm_image) {
		this.alarm_image = alarm_image;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
}
