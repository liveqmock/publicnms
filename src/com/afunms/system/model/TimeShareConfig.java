/**
 * <p>Description:mapping table NMS_ROLE</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-06
 */

package com.afunms.system.model;

import com.afunms.common.base.BaseVo;

/**
 * @author nielin
 * @since 2009-12-31
 * 
 */
public class TimeShareConfig extends BaseVo {
	
	private int id;

	private String timeShareType;

	/**
	 * 设备或服务id Equipment or services id
	 */
	private String objectId;

	/**
	 * 设备或服务类型 Equipment or services type
	 * 
	 */
	private String objectType;
	private String beginTime;
	private String endTime;
	private String userIds;

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the timeShareType
	 */
	public String getTimeShareType() {
		return timeShareType;
	}

	/**
	 * @param timeShareType
	 *            the timeShareType to set
	 */
	public void setTimeShareType(String timeShareType) {
		this.timeShareType = timeShareType;
	}

	/**
	 * @return the objectId
	 */
	public String getObjectId() {
		return objectId;
	}

	/**
	 * @param objectId
	 *            the objectId to set
	 */
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	/**
	 * @return the objectType
	 */
	public String getObjectType() {
		return objectType;
	}

	/**
	 * @param objectType
	 *            the objectType to set
	 */
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	/**
	 * @return the beginTime
	 */
	public String getBeginTime() {
		return beginTime;
	}

	/**
	 * @param beginTime
	 *            the beginTime to set
	 */
	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}

	/**
	 * @return the endTime
	 */
	public String getEndTime() {
		return endTime;
	}

	/**
	 * @param endTime
	 *            the endTime to set
	 */
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	/**
	 * @return the userIds
	 */
	public String getUserIds() {
		return userIds;
	}

	/**
	 * @param userIds
	 *            the userIds to set
	 */
	public void setUserIds(String userIds) {
		this.userIds = userIds;
	}

}
