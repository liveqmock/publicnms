/**
 * <p>Description:mapping table NMS_POSITION</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.event.model;

import java.util.Calendar;

import com.afunms.common.base.BaseVo;

public class NetSyslog extends BaseVo
{
	private Long id;
	
	
	private int facility;//事件来源
	private int priority;//优先级
	private String facilityName;//事件来源名称
	private String priorityName;//优先级名称
	private String hostname;//主机名
	private Calendar recordtime;//时间戳
	private String message;//得消息内容
	private String ipaddress;//IP地址
	private String businessid;//事件来源
	private int category;  //设备类型
	
	
	/** nullable persistent field */
	private String begin_date;

	/** nullable persistent field */
	private String end_date;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long l) {
		id = l;
	}
	
	public void setFacility(int facility){
		this.facility=facility;
	}
	public void setPriority(int priority){
		this.priority=priority;
	}
	public void setHostname(String hostname){
		this.hostname=hostname;
	}
	public void setRecordtime(Calendar recordtime){
		this.recordtime=recordtime;
	}
	public void setMessage(String message){
		this.message=message;
	}
	public void setFacilityName(String facilityName){
		this.facilityName=facilityName;
	}
	public void setPriorityName(String priorityName){
		this.priorityName=priorityName;
	}
	
	public int getFacility(){
		return facility;
	}
	public int getPriority(){
		return priority;
	}
	public String getHostname(){
		return hostname;
	}
	public Calendar getRecordtime(){
		return recordtime;
	}
	public String getMessage(){
		return message;
	}
	public String getFacilityName(){
		return facilityName;
	}
	public String getPriorityName(){
		return priorityName;
	}
	public String getIpaddress(){
		return ipaddress;
	}
	public void setIpaddress(String ipaddress){
		this.ipaddress=ipaddress;
	}
	public String getBusinessid(){
		return businessid;
	}
	public void setBusinessid(String businessid){
		this.businessid=businessid;
	}
	public int getCategory(){
		return category;
	}
	public void setCategory(int category){
		this.category=category;
	}

}
