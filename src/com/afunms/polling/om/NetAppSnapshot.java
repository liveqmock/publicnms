package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

import com.afunms.common.base.BaseVo;

public class NetAppSnapshot extends BaseVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String ipaddress;
	
	private Calendar collectTime;

	private String slVIndex;

	private String slVMonth;

	private String slVDay;

	private String slVHour;
	
	private String slVMinutes;
	
	private String slVName; //快照名字
	
	private String slVVolume; //包含此快照的卷
	
	private String slVNumber;//卷序号
	
	private String slVVolumeName;//包含此快照的卷名
	
	private String slVType;//包含此快照的卷类型

	public String getIpaddress() {
		return ipaddress;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public Calendar getCollectTime() {
		return collectTime;
	}

	public void setCollectTime(Calendar collectTime) {
		this.collectTime = collectTime;
	}

	public String getSlVIndex() {
		return slVIndex;
	}

	public void setSlVIndex(String slVIndex) {
		this.slVIndex = slVIndex;
	}

	public String getSlVMonth() {
		return slVMonth;
	}

	public void setSlVMonth(String slVMonth) {
		this.slVMonth = slVMonth;
	}

	public String getSlVDay() {
		return slVDay;
	}

	public void setSlVDay(String slVDay) {
		this.slVDay = slVDay;
	}

	public String getSlVHour() {
		return slVHour;
	}

	public void setSlVHour(String slVHour) {
		this.slVHour = slVHour;
	}

	public String getSlVMinutes() {
		return slVMinutes;
	}

	public void setSlVMinutes(String slVMinutes) {
		this.slVMinutes = slVMinutes;
	}

	public String getSlVName() {
		return slVName;
	}

	public void setSlVName(String slVName) {
		this.slVName = slVName;
	}

	public String getSlVVolume() {
		return slVVolume;
	}

	public void setSlVVolume(String slVVolume) {
		this.slVVolume = slVVolume;
	}

	public String getSlVNumber() {
		return slVNumber;
	}

	public void setSlVNumber(String slVNumber) {
		this.slVNumber = slVNumber;
	}

	public String getSlVVolumeName() {
		return slVVolumeName;
	}

	public void setSlVVolumeName(String slVVolumeName) {
		this.slVVolumeName = slVVolumeName;
	}

	public String getSlVType() {
		return slVType;
	}

	public void setSlVType(String slVType) {
		this.slVType = slVType;
	}
	
	
}
