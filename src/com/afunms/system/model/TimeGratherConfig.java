/**
 * <p>Description:mapping table NMS_TIMEGRATHERCONFIG</p>
 * <p>Company: dhcc.com</p>
 * @author snow
 * @project afunms
 * @date 2010-05-14
 */

package com.afunms.system.model;

import com.afunms.common.base.BaseVo;

/**
 * 采集时间
 * 
 * @author snow
 * @since JDK1.5
 */
public class TimeGratherConfig extends BaseVo {
	/**
	 * 主键
	 */
	private int id;

	/**
	 * 设备或服务id Equipment or services id
	 */
	private String objectId;

	/**
	 * 设备或服务类型 Equipment or services type
	 */
	private String objectType;
	/**
	 * 信息采集开始时间
	 */
	private String beginTime;
	/**
	 * 信息采集结束时间
	 */
	private String endTime;

	/**
	 * @return 获得 id
	 */
	public int getId() {
		return id;
	}

	/**
	 * 设置id
	 * 
	 * @param id
	 *            要设置的 id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return 获得 objectId
	 */
	public String getObjectId() {
		return objectId;
	}

	/**
	 * 设置objectId
	 * 
	 * @param objectId
	 *            要设置的 objectId
	 */
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	/**
	 * @return 获得 objectType
	 */
	public String getObjectType() {
		return objectType;
	}

	/**
	 * 设置objectType
	 * 
	 * @param objectType
	 *            要设置的 objectType
	 */
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	/**
	 * @return 获得 beginTime
	 */
	public String getBeginTime() {
		return beginTime;
	}

	/**
	 * 设置beginTime
	 * 
	 * @param beginTime
	 *            要设置的 beginTime
	 */
	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}

	/**
	 * @return 获得 endTime
	 */
	public String getEndTime() {
		return endTime;
	}

	/**
	 * 设置endTime
	 * 
	 * @param endTime
	 *            要设置的 endTime
	 */
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	/*
	 * 只用于显示方便的属性
	 */
	private String startHour;
	private String startMin;
	private String endHour;
	private String endMin;

	public String getStartHour() {
		return startHour;
	}

	public void setStartHour(String startHour) {
		this.startHour = startHour;
	}

	public String getStartMin() {
		return startMin;
	}

	public void setStartMin(String startMin) {
		this.startMin = startMin;
	}

	public String getEndHour() {
		return endHour;
	}

	public void setEndHour(String endHour) {
		this.endHour = endHour;
	}

	public String getEndMin() {
		return endMin;
	}

	public void setEndMin(String endMin) {
		this.endMin = endMin;
	}

	public void setHourAndMin() {
		setStartHour(beginTime.split(":")[0]);
		setStartMin(beginTime.split(":")[1]);
		setEndHour(endTime.split(":")[0]);
		setEndMin(endTime.split(":")[1]);
	}

}
