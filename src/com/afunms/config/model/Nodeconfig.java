package com.afunms.config.model;

/**
 * 2009-12-14
 * hostœµÕ≥≈‰÷√
 */
import java.io.Serializable;

import com.afunms.common.base.BaseVo;

public class Nodeconfig extends BaseVo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4510782414458174399L;
	private int id;
	private int nodeid;
	private String hostname;
	private String sysname;
	private String serialNumber;
	private String cSDVersion;
	private String numberOfProcessors;
	private String mac;
	/**
	 * @return the id
	 */
	
	//quzhi
	private String descrOfProcessors;
	
	public String getDescrOfProcessors() {
		return descrOfProcessors;
	}
	public void setDescrOfProcessors(String descrOfProcessors) {
		this.descrOfProcessors = descrOfProcessors;
	}
	//
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
	 * @return the hostname
	 */
	public String getHostname() {
		return hostname;
	}
	/**
	 * @param hostname the hostname to set
	 */
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	/**
	 * @return the sysname
	 */
	public String getSysname() {
		return sysname;
	}
	/**
	 * @param sysname the sysname to set
	 */
	public void setSysname(String sysname) {
		this.sysname = sysname;
	}
	/**
	 * @return the serialNumber
	 */
	public String getSerialNumber() {
		return serialNumber;
	}
	/**
	 * @param serialNumber the serialNumber to set
	 */
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	/**
	 * @return the cSDVersion
	 */
	public String getCSDVersion() {
		return cSDVersion;
	}
	/**
	 * @param version the cSDVersion to set
	 */
	public void setCSDVersion(String version) {
		cSDVersion = version;
	}
	/**
	 * @return the numberOfProcessors
	 */
	public String getNumberOfProcessors() {
		return numberOfProcessors;
	}
	/**
	 * @param numberOfProcessors the numberOfProcessors to set
	 */
	public void setNumberOfProcessors(String numberOfProcessors) {
		this.numberOfProcessors = numberOfProcessors;
	}
	/**
	 * @return the mac
	 */
	public String getMac() {
		return mac;
	}
	/**
	 * @param mac the mac to set
	 */
	public void setMac(String mac) {
		this.mac = mac;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((cSDVersion == null) ? 0 : cSDVersion.hashCode());
		result = prime * result
				+ ((hostname == null) ? 0 : hostname.hashCode());
		result = prime * result + ((mac == null) ? 0 : mac.hashCode());
		result = prime * result + nodeid;
		result = prime
				* result
				+ ((numberOfProcessors == null) ? 0 : numberOfProcessors
						.hashCode());
		result = prime * result
				+ ((serialNumber == null) ? 0 : serialNumber.hashCode());
		result = prime * result + ((sysname == null) ? 0 : sysname.hashCode());
		//quzhi
		result = prime
		* result
		+ ((descrOfProcessors == null) ? 0 : descrOfProcessors
				.hashCode());
		//
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
		if (!(obj instanceof Nodeconfig))
			return false;
		final Nodeconfig other = (Nodeconfig) obj;
		if (cSDVersion == null) {
			if (other.cSDVersion != null)
				return false;
		} else if (!cSDVersion.equals(other.cSDVersion))
			return false;
		if (hostname == null) {
			if (other.hostname != null)
				return false;
		} else if (!hostname.equals(other.hostname))
			return false;
		if (mac == null) {
			if (other.mac != null)
				return false;
		} else if (!mac.equals(other.mac))
			return false;
		if (nodeid != other.nodeid)
			return false;
		if (numberOfProcessors == null) {
			if (other.numberOfProcessors != null)
				return false;
		} else if (!numberOfProcessors.equals(other.numberOfProcessors))
			return false;
		if (serialNumber == null) {
			if (other.serialNumber != null)
				return false;
		} else if (!serialNumber.equals(other.serialNumber))
			return false;
		if (sysname == null) {
			if (other.sysname != null)
				return false;
		} else if (!sysname.equals(other.sysname))
			return false;
		//quzhi
		if (descrOfProcessors == null) {
			if (other.descrOfProcessors != null)
				return false;
		} else if (!descrOfProcessors.equals(other.descrOfProcessors))
			return false;
		//
		return true;
	}
	
	

}
