package com.afunms.config.model;

/**
 * 2009-12-14
 * CPU≈‰÷√
 */
import java.io.Serializable;

import com.afunms.common.base.BaseVo;

public class Nodecpuconfig extends BaseVo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4688636990777031079L;
	private int id;
	private int nodeid;
	private String dataWidth;
	private String processorId;
	private String name;
	private String l2CacheSize;
	private String l2CacheSpeed;
	private String processorType;
	private String processorSpeed;
	//quzhi
	private String descrOfProcessors;
	
	public String getDescrOfProcessors() {
		return descrOfProcessors;
	}
	public void setDescrOfProcessors(String descrOfProcessors) {
		this.descrOfProcessors = descrOfProcessors;
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
	 * @return the dataWidth
	 */
	public String getDataWidth() {
		return dataWidth;
	}
	/**
	 * @param dataWidth the dataWidth to set
	 */
	public void setDataWidth(String dataWidth) {
		this.dataWidth = dataWidth;
	}
	/**
	 * @return the processorId
	 */
	public String getProcessorId() {
		return processorId;
	}
	/**
	 * @param processorId the processorId to set
	 */
	public void setProcessorId(String processorId) {
		this.processorId = processorId;
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
	 * @return the l2CacheSize
	 */
	public String getL2CacheSize() {
		return l2CacheSize;
	}
	/**
	 * @param cacheSize the l2CacheSize to set
	 */
	public void setL2CacheSize(String cacheSize) {
		l2CacheSize = cacheSize;
	}
	/**
	 * @return the l2CacheSpeed
	 */
	public String getL2CacheSpeed() {
		return l2CacheSpeed;
	}
	/**
	 * @param cacheSpeed the l2CacheSpeed to set
	 */
	public void setL2CacheSpeed(String cacheSpeed) {
		l2CacheSpeed = cacheSpeed;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dataWidth == null) ? 0 : dataWidth.hashCode());
		result = prime * result
				+ ((l2CacheSize == null) ? 0 : l2CacheSize.hashCode());
		result = prime * result
				+ ((l2CacheSpeed == null) ? 0 : l2CacheSpeed.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + nodeid;
		result = prime * result
				+ ((processorId == null) ? 0 : processorId.hashCode());
		result = prime * result
		+ ((descrOfProcessors == null) ? 0 : descrOfProcessors.hashCode());
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
		if (!(obj instanceof Nodecpuconfig))
			return false;
		final Nodecpuconfig other = (Nodecpuconfig) obj;
		if (dataWidth == null) {
			if (other.dataWidth != null)
				return false;
		} else if (!dataWidth.equals(other.dataWidth))
			return false;
		if (l2CacheSize == null) {
			if (other.l2CacheSize != null)
				return false;
		} else if (!l2CacheSize.equals(other.l2CacheSize))
			return false;
		if (l2CacheSpeed == null) {
			if (other.l2CacheSpeed != null)
				return false;
		} else if (!l2CacheSpeed.equals(other.l2CacheSpeed))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (nodeid != other.nodeid)
			return false;
		if (processorId == null) {
			if (other.processorId != null)
				return false;
		} else if (!processorId.equals(other.processorId))
			return false;
		if (descrOfProcessors == null) {
			if (other.descrOfProcessors != null)
				return false;
		} else if (!descrOfProcessors.equals(other.descrOfProcessors))
			return false;
		return true;
	}
	public String getProcessorType() {
		return processorType;
	}
	public void setProcessorType(String processorType) {
		this.processorType = processorType;
	}
	public String getProcessorSpeed() {
		return processorSpeed;
	}
	public void setProcessorSpeed(String processorSpeed) {
		this.processorSpeed = processorSpeed;
	}
	
}
