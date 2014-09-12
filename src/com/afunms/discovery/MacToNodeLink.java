

package com.afunms.discovery;

/**
 * 
 * @author <a href="mailto:hukelei@dhcc.com.cn">Hukelei</a>
 */
public class MacToNodeLink {

	String macAddress;
	int nodeparentid;
	int parentifindex;


	private MacToNodeLink() {
		throw new UnsupportedOperationException(
		"default constructor not supported");
	}

	public MacToNodeLink(String macAddress) {
		this.macAddress = macAddress;
	}

	public String toString() {
		StringBuffer str = new StringBuffer("Mac Address = " + macAddress + "\n");
		str.append("Node ParentId = " + nodeparentid + "\n");
		str.append("Parent IfIndex = " + parentifindex + "\n");
		return str.toString();
	}

	/**
	 * @return Returns the nodeparentid.
	 */
	public int getNodeparentid() {
		return nodeparentid;
	}
	/**
	 * @param nodeparentid The nodeparentid to set.
	 */
	public void setNodeparentid(int nodeparentid) {
		this.nodeparentid = nodeparentid;
	}
	/**
	 * @return Returns the parentifindex.
	 */
	public int getParentifindex() {
		return parentifindex;
	}
	/**
	 * @param parentifindex The parentifindex to set.
	 */
	public void setParentifindex(int parentifindex) {
		this.parentifindex = parentifindex;
	}
	/**
	 * @return Returns the ifindex.
	 */
	public String getMacAddress() {
		return macAddress;
	}
}