package com.afunms.config.model;

/**
 * 2009-12-14
 * disk≈‰÷√
 */
import java.io.Serializable;

import com.afunms.common.base.BaseVo;

public class Nodediskconfig extends BaseVo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7977977256323516176L;
	private int id;
	private int nodeid;
	private String bytesPerSector;
	private String caption;
	private String interfaceType;
	private String size;
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
	 * @return the bytesPerSector
	 */
	public String getBytesPerSector() {
		return bytesPerSector;
	}
	/**
	 * @param bytesPerSector the bytesPerSector to set
	 */
	public void setBytesPerSector(String bytesPerSector) {
		this.bytesPerSector = bytesPerSector;
	}
	/**
	 * @return the caption
	 */
	public String getCaption() {
		return caption;
	}
	/**
	 * @param caption the caption to set
	 */
	public void setCaption(String caption) {
		this.caption = caption;
	}
	/**
	 * @return the interfaceType
	 */
	public String getInterfaceType() {
		return interfaceType;
	}
	/**
	 * @param interfaceType the interfaceType to set
	 */
	public void setInterfaceType(String interfaceType) {
		this.interfaceType = interfaceType;
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
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((bytesPerSector == null) ? 0 : bytesPerSector.hashCode());
		result = prime * result + ((caption == null) ? 0 : caption.hashCode());
		result = prime * result
				+ ((interfaceType == null) ? 0 : interfaceType.hashCode());
		result = prime * result + nodeid;
		result = prime * result + ((size == null) ? 0 : size.hashCode());
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
		if (!(obj instanceof Nodediskconfig))
			return false;
		final Nodediskconfig other = (Nodediskconfig) obj;
		if (bytesPerSector == null) {
			if (other.bytesPerSector != null)
				return false;
		} else if (!bytesPerSector.equals(other.bytesPerSector))
			return false;
		if (caption == null) {
			if (other.caption != null)
				return false;
		} else if (!caption.equals(other.caption))
			return false;
		if (interfaceType == null) {
			if (other.interfaceType != null)
				return false;
		} else if (!interfaceType.equals(other.interfaceType))
			return false;
		if (nodeid != other.nodeid)
			return false;
		if (size == null) {
			if (other.size != null)
				return false;
		} else if (!size.equals(other.size))
			return false;
		return true;
	}
	

}
