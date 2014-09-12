/**
 * <p>Description:mapping table NMS_MONITOR_OBJECT</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-16
 */

package com.afunms.monitor.item.base;

import com.afunms.common.base.BaseVo;

public class MonitorObject extends BaseVo
{
	private static final long serialVersionUID = 472967150279L;
	
	private int id;  
	private String moid;
	private String name;
	private String descr;
	private String category;
	private boolean isDefault;	
	private int threshold; 
	private String unit; 	
	private int compare; 
	private int compareType;	
	private int upperTimes; 
	private String alarmInfo;
	private boolean enabled;
	private int alarmLevel;
	private int pollInterval;
	private String intervalUnit;
	private int resultType;
	private boolean showInTopo;
	private String nodetype;
	private String subentity;
	private int limenvalue0;
	private int limenvalue1;
	private int limenvalue2;
	private int time0;
	private int time1;
	private int time2;
	private int sms0;
	private int sms1;
	private int sms2;

	public String getNodetype() {
		return nodetype;
	}
	
	public void setNodetype(String nodetype) {
		this.nodetype = nodetype;
	}
	
	public String getSubentity() {
		return subentity;
	}
	
	public void setSubentity(String subentity) {
		this.subentity = subentity;
	}
	
	public int getLimenvalue0() {
		return limenvalue0;
	}
	
	public void setLimenvalue0(int limenvalue0) {
		this.limenvalue0 = limenvalue0;
	}
	
	public int getLimenvalue1() {
		return limenvalue1;
	}
	
	public void setLimenvalue1(int limenvalue1) {
		this.limenvalue1 = limenvalue1;
	}
	
	public int getLimenvalue2() {
		return limenvalue2;
	}
	
	public void setLimenvalue2(int limenvalue2) {
		this.limenvalue2 = limenvalue2;
	}	
	
	public int getTime0() {
		return time0;
	}
	
	public void setTime0(int time0) {
		this.time0 = time0;
	}	
	
	public int getTime1() {
		return time1;
	}
	
	public void setTime1(int time1) {
		this.time1 = time1;
	}
	
	public int getTime2() {
		return time2;
	}
	
	public void setTime2(int time2) {
		this.time2 = time2;
	}
	
	public int getSms0() {
		return sms0;
	}
	
	public void setSms0(int sms0) {
		this.sms0 = sms0;
	}
	
	public int getSms1() {
		return sms1;
	}
	
	public void setSms1(int sms1) {
		this.sms1 = sms1;
	}
	public int getSms2() {
		return sms2;
	}
	
	public void setSms2(int sms2) {
		this.sms2 = sms2;
	}
	
	public String getAlarmInfo() {
		return alarmInfo;
	}
	
	public void setAlarmInfo(String alarmInfo) {
		this.alarmInfo = alarmInfo;
	}
	
	public int getAlarmLevel() {
		return alarmLevel;
	}
	
	public void setAlarmLevel(int alarmLevel) {
		this.alarmLevel = alarmLevel;
	}
	public String getCategory() {
		return category;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}
	
	public int getCompare() {
		return compare;
	}
	
	public void setCompare(int compare) {
		this.compare = compare;
	}
	
	public String getDescr() {
		return descr;
	}
	
	public void setDescr(String descr) {
		this.descr = descr;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getIntervalUnit() {
		return intervalUnit;
	}
	
	public void setIntervalUnit(String intervalUnit) {
		this.intervalUnit = intervalUnit;
	}
	
	public boolean isDefault() {
		return isDefault;
	}
	
	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}
	
	public String getMoid() {
		return moid;
	}
	
	public void setMoid(String moid) {
		this.moid = moid;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public int getPollInterval() {
		return pollInterval;
	}
	
	public void setPollInterval(int pollInterval) {
		this.pollInterval = pollInterval;
	}
	
	public int getResultType() {
		return resultType;
	}
	public void setResultType(int resultType) {
		this.resultType = resultType;
	}
	
	public int getThreshold() {
		return threshold;
	}
	
	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}
		
	public int getUpperTimes() {
		return upperTimes;
	}
	
	public void setUpperTimes(int upperTimes) {
		this.upperTimes = upperTimes;
	}
	
	public boolean isShowInTopo() {
		return showInTopo;
	}
	
	public void setShowInTopo(boolean showInTopo) {
		this.showInTopo = showInTopo;
	}

	public int getCompareType() {
		return compareType;
	}

	public void setCompareType(int compareType) {
		this.compareType = compareType;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}    
}
