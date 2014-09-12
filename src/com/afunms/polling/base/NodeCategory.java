/**
 * <p>Description:node category</p>
 * <p>Company: dhcc.com</p>
 * @author miiwill
 * @project afunms
 * @date 2007-1-5
 */

package com.afunms.polling.base;

public class NodeCategory
{
    private int id;
    private String cnName;
    private String enName;
    private String topoImage;
    private String alarmImage;
    private String alarmImage_1;//不重要的告警
    private String lostImage;
    
	public String getLostImage() {
		return lostImage;
	}
	public void setLostImage(String lostImage) {
		this.lostImage = lostImage;
	}
	public String getAlarmImage() {
		return alarmImage;
	}
	public void setAlarmImage(String alarmImage) {
		this.alarmImage = alarmImage;
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
	public String getTopoImage() {
		return topoImage;
	}
	public void setTopoImage(String topoImage) {
		this.topoImage = topoImage;
	}
	public String getAlarmImage_1() {
		return alarmImage_1;
	}
	public void setAlarmImage_1(String alarmImage_1) {
		this.alarmImage_1 = alarmImage_1;
	}    
}