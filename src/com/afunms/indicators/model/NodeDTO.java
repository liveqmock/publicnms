package com.afunms.indicators.model;

import com.afunms.common.base.BaseVo;

/**
 * 此类为基准性能监控指标
 * @author Administrator
 *
 */

public class NodeDTO extends BaseVo{
	
	private int id;		
	
	private String nodeid;				// 设备id
	
	private String name;				// 设备名称
	
	private String ipaddress;			// 设备ipaddress
	
	private String type;				// 设备类型
	
	private String subtype;				// 设备子类型
	
	private String sysOid;				// 系统oid
	
	private String businessId;			// 业务id
	
	private String businessName;		// 业务名称

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
	 * @return the nodeid
	 */
	public String getNodeid() {
		return nodeid;
	}

	/**
	 * @param nodeid the nodeid to set
	 */
	public void setNodeid(String nodeid) {
		this.nodeid = nodeid;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the ipaddress
	 */
	public String getIpaddress() {
		return ipaddress;
	}

	/**
	 * @param ipaddress the ipaddress to set
	 */
	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the subtype
	 */
	public String getSubtype() {
		return subtype;
	}

	/**
	 * @param subtype the subtype to set
	 */
	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}

	/**
	 * @return the sysOid
	 */
	public String getSysOid() {
		return sysOid;
	}

	/**
	 * @param sysOid the sysOid to set
	 */
	public void setSysOid(String sysOid) {
		this.sysOid = sysOid;
	}

	/**
	 * @return the businessId
	 */
	public String getBusinessId() {
		return businessId;
	}

	/**
	 * @param businessId the businessId to set
	 */
	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}

	/**
	 * @return the businessName
	 */
	public String getBusinessName() {
		return businessName;
	}

	/**
	 * @param businessName the businessName to set
	 */
	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	
}
