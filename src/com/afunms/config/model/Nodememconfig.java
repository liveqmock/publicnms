package com.afunms.config.model;

/**
 * 2009-12-14
 * ƒ⁄¥Ê≈‰÷√
 */
import java.io.Serializable;

import com.afunms.common.base.BaseVo;

public class Nodememconfig extends BaseVo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2987373799665792460L;
	private int id;
	private int nodeid;
	private String totalVisibleMemorySize;
	private String totalVirtualMemorySize;
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
	public int getNodeid() {
		return nodeid;
	}
	/**
	 * @param nodeid the nodeid to set
	 */
	public void setNodeid(int nodeid) {
		this.nodeid = nodeid;
	}
	/**
	 * @return the totalVisibleMemorySize
	 */
	public String getTotalVisibleMemorySize() {
		return totalVisibleMemorySize;
	}
	/**
	 * @param totalVisibleMemorySize the totalVisibleMemorySize to set
	 */
	public void setTotalVisibleMemorySize(String totalVisibleMemorySize) {
		this.totalVisibleMemorySize = totalVisibleMemorySize;
	}
	/**
	 * @return the totalVirtualMemorySize
	 */
	public String getTotalVirtualMemorySize() {
		return totalVirtualMemorySize;
	}
	/**
	 * @param totalVirtualMemorySize the totalVirtualMemorySize to set
	 */
	public void setTotalVirtualMemorySize(String totalVirtualMemorySize) {
		this.totalVirtualMemorySize = totalVirtualMemorySize;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + nodeid;
		result = prime
				* result
				+ ((totalVirtualMemorySize == null) ? 0
						: totalVirtualMemorySize.hashCode());
		result = prime
				* result
				+ ((totalVisibleMemorySize == null) ? 0
						: totalVisibleMemorySize.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Nodememconfig))
			return false;
		final Nodememconfig other = (Nodememconfig) obj;
		if (nodeid != other.nodeid)
			return false;
		if (totalVirtualMemorySize == null) {
			if (other.totalVirtualMemorySize != null)
				return false;
		} else if (!totalVirtualMemorySize.equals(other.totalVirtualMemorySize))
			return false;
		if (totalVisibleMemorySize == null) {
			if (other.totalVisibleMemorySize != null)
				return false;
		} else if (!totalVisibleMemorySize.equals(other.totalVisibleMemorySize))
			return false;
		return true;
	}
	

}
