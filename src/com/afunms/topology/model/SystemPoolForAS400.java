/**
 * <p>Description:mapping table NMS_TOPO_NODE</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-12
 */

package com.afunms.topology.model;

import com.afunms.common.base.BaseVo;

public class SystemPoolForAS400 extends BaseVo {
	
	/**
	 * id
	 */
	private int id;
	
	/**
	 * 系统池所属设备id
	 */
	private String nodeid;
	
	/**
	 * 系统池所属设备ip
	 */
	private String ipaddress;
	
	/**
	 * 系统池自身Id
	 */
	private String systemPool;
	
	/**
	 * 系统池名称
	 */
	private String name;
	
	/**
	 * 系统池大小
	 */
	private String size;
	
	/**
	 * 系统池的保留池大小
	 */
	private String reservedSize;
	
	/**
	 * 系统池最大活动数
	 */
	private String maximumActiveThreads;
	
	/**
	 * 采集时间
	 */
	private String collectTime;

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
	 * @return the systemPool
	 */
	public String getSystemPool() {
		return systemPool;
	}

	/**
	 * @param systemPool the systemPool to set
	 */
	public void setSystemPool(String systemPool) {
		this.systemPool = systemPool;
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
	 * @return the size
	 */
	public String getSize() {
		return size;
	}

	/**
	 * @param size the size to set
	 */
	public void setSize(String size) {
		this.size = size;
	}

	/**
	 * @return the reservedSize
	 */
	public String getReservedSize() {
		return reservedSize;
	}

	/**
	 * @param reservedSize the reservedSize to set
	 */
	public void setReservedSize(String reservedSize) {
		this.reservedSize = reservedSize;
	}

	/**
	 * @return the maximumActiveThreads
	 */
	public String getMaximumActiveThreads() {
		return maximumActiveThreads;
	}

	/**
	 * @param maximumActiveThreads the maximumActiveThreads to set
	 */
	public void setMaximumActiveThreads(String maximumActiveThreads) {
		this.maximumActiveThreads = maximumActiveThreads;
	}

	/**
	 * @return the collectTime
	 */
	public String getCollectTime() {
		return collectTime;
	}

	/**
	 * @param collectTime the collectTime to set
	 */
	public void setCollectTime(String collectTime) {
		this.collectTime = collectTime;
	}

	
}