/**
 * <p>Description:mapping table NMS_TOPO_NODE</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-12
 */

package com.afunms.topology.model;

import com.afunms.common.base.BaseVo;

/**
 * 此类为封装ip所属区域与当前区域匹配的持久化model类 存储到对应的数据库 nms_ip_district_match 表内
 * @author nielin
 * @date 2010-05-13
 *
 */
public class IpDistrictMatchConfig extends BaseVo{
	
	/**
	 * 默认id (以备后来持久化存入数据库中使用 ， 暂时没有使用)
	 */
	private int id; 
	
	/**
	 * 网络设备ip
	 */
	private String relateipaddr;
	
	/**
	 * 设备ipaddress
	 */
	private String nodeIp;
	
	/**
	 * 设备名称
	 */
	private String nodeName;
	
	/**
	 * 当前在线状态
	 */
	private String isOnline;
	
	/**
	 * 所属区域
	 */
	private String originalDistrict;
	
	/**
	 * 当前判断区域
	 */
	private String currentDistrict;
	
	/**
	 * 是否匹配
	 */
	private String isMatch;
	
	/**
	 * 采集时间
	 */
	private String time;

	/**
	 * 
	 */
	public IpDistrictMatchConfig() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param id
	 * @param relateipaddr
	 * @param nodeIp
	 * @param nodeName
	 * @param isOnline
	 * @param originalDistrict
	 * @param currentDistrict
	 * @param isMatch
	 * @param date
	 */
	public IpDistrictMatchConfig(int id, String relateipaddr, String nodeIp,
			String nodeName, String isOnline, String originalDistrict,
			String currentDistrict, String isMatch, String time) {
		this.id = id;
		this.relateipaddr = relateipaddr;
		this.nodeIp = nodeIp;
		this.nodeName = nodeName;
		this.isOnline = isOnline;
		this.originalDistrict = originalDistrict;
		this.currentDistrict = currentDistrict;
		this.isMatch = isMatch;
		this.time = time;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the relateipaddr
	 */
	public String getRelateipaddr() {
		return relateipaddr;
	}

	/**
	 * @param relateipaddr the relateipaddr to set
	 */
	public void setRelateipaddr(String relateipaddr) {
		this.relateipaddr = relateipaddr;
	}

	/**
	 * @return the nodeIp
	 */
	public String getNodeIp() {
		return nodeIp;
	}

	/**
	 * @param nodeIp the nodeIp to set
	 */
	public void setNodeIp(String nodeIp) {
		this.nodeIp = nodeIp;
	}

	/**
	 * @return the nodeName
	 */
	public String getNodeName() {
		return nodeName;
	}

	/**
	 * @param nodeName the nodeName to set
	 */
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	/**
	 * @return the isOnline
	 */
	public String getIsOnline() {
		return isOnline;
	}

	/**
	 * @param isOnline the isOnline to set
	 */
	public void setIsOnline(String isOnline) {
		this.isOnline = isOnline;
	}

	/**
	 * @return the originalDistrict
	 */
	public String getOriginalDistrict() {
		return originalDistrict;
	}

	/**
	 * @param originalDistrict the originalDistrict to set
	 */
	public void setOriginalDistrict(String originalDistrict) {
		this.originalDistrict = originalDistrict;
	}

	/**
	 * @return the currentDistrict
	 */
	public String getCurrentDistrict() {
		return currentDistrict;
	}

	/**
	 * @param currentDistrict the currentDistrict to set
	 */
	public void setCurrentDistrict(String currentDistrict) {
		this.currentDistrict = currentDistrict;
	}

	/**
	 * @return the isMatch
	 */
	public String getIsMatch() {
		return isMatch;
	}

	/**
	 * @param isMatch the isMatch to set
	 */
	public void setIsMatch(String isMatch) {
		this.isMatch = isMatch;
	}

	/**
	 * @return the time
	 */
	public String getTime() {
		return time;
	}

	/**
	 * @param date the time to set
	 */
	public void setTime(String time) {
		this.time = time;
	}

	

	

	
}