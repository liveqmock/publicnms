
package com.afunms.discovery;

/**
 * 
 * @author <a href="mailto:hukelei@dhcc.com.cn">Hukelei</a>
 */
public class NodeToNodeLink {

	int nodeId;
	int ifindex;
	int nodeparentid;
	int parentifindex;
	int assistant;
	int findtype;


	private NodeToNodeLink() {
		throw new UnsupportedOperationException(
		"default constructor not supported");
	}

	public NodeToNodeLink(int nodeId, int ifindex) {
		this.nodeId = nodeId;
		this.ifindex = ifindex;
	}

	public String toString() {
		StringBuffer str = new StringBuffer("Node Id = " + nodeId);
		str.append(" IfIndex = " + ifindex);
		str.append(" Node ParentId = " + nodeparentid );
		str.append(" Parent IfIndex = " + parentifindex );
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
	public int getIfindex() {
		return ifindex;
	}
	/**
	 * @return
	 */
	public int getNodeId() {
		return nodeId;
	}
	/**
	 * @return
	 */
	public int getAssistant() {
		return assistant;
	}
	public void setAssistant(int assistant){
		this.assistant = assistant;
	}
	
	public int getFindtype() {
		return findtype;
	}
	public void setFindtype(int findtype){
		this.findtype = findtype;
	}
	
	public boolean equals(NodeToNodeLink nodelink) {
		if (this.nodeId == nodelink.getNodeId() && 
			this.ifindex == nodelink.getIfindex()	&&
			this.nodeparentid == nodelink.getNodeparentid() &&
			this.parentifindex == nodelink.getParentifindex()) return true;

		if (this.nodeId == nodelink.getNodeparentid() && 
			this.ifindex == nodelink.getParentifindex()	&&
			this.nodeparentid == nodelink.getNodeId() &&
			this.parentifindex == nodelink.getIfindex()) return true;
		
		return false;

	}
	
	public boolean assistantequals(NodeToNodeLink nodelink) {
		if (this.nodeId == nodelink.getNodeId() &&
			this.nodeparentid == nodelink.getNodeparentid()) return true;

		if (this.nodeId == nodelink.getNodeparentid() && 
			this.nodeparentid == nodelink.getNodeId() ) return true;
		
		return false;

	}
 
}