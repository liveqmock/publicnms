
package com.afunms.discovery;

/**
 * 
 * @author <a href="mailto:antonio@opennms.it">Antonio Russo</a>
 */
public class AtInterface {

	int nodeid;
	int ifindex;
	String macAddress;
	String ipAddress;


	public AtInterface() {
	}

	public AtInterface(int nodeid, String ipAddress, String macAddress) {
		this.nodeid = nodeid;
		this.macAddress = macAddress;
		this.ipAddress = ipAddress;
		this.ifindex=-1;
	}
	
	public AtInterface(int nodeid, String ipAddress, String macAddress,int ifindex) {
		this.nodeid = nodeid;
		this.macAddress = macAddress;
		this.ipAddress = ipAddress;
		this.ifindex=ifindex;
	}

	public AtInterface(int nodeid, String ipAddress) {
		this.nodeid = nodeid;
		this.macAddress = "";
		this.ipAddress = ipAddress;
		this.ifindex=-1;
	}

	public String toString() {
		StringBuffer str = new StringBuffer("Mac Address = " + macAddress + "\n");
		str.append("Node Id = " + nodeid + "\n");
		str.append("Ip Address = " + ipAddress + "\n");
		str.append("IfIndex = " + ifindex + "\n");
		return str.toString();
	}

	/**
	 * @return Returns the nodeparentid.
	 */
	public int getNodeId() {
		return nodeid;
	}
	/**
	 * @return Returns the ifindex.
	 */
	public String getMacAddress() {
		return macAddress;
	}
	
	/**
	 * @return Returns the ifindex.
	 */
	public String getIpAddress() {
		return ipAddress;
	}

	public int getIfindex() {
		return ifindex;
	}

	public void setIfindex(int ifindex) {
		this.ifindex = ifindex;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
}