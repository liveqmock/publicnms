/**
 * <p>Description:mapping table nms_remote_ping_node</p>
 * <p>Company: dhcc.com</p>
 * @author nielin
 * @project afunms
 * @date 2010-03-16
 */

package com.afunms.topology.model;

import com.afunms.common.base.BaseVo;


public class RemotePingNode extends BaseVo
{
	/**
	 * 自增长id
	 */
	private int id;   
	
	/**
	 * 设备id
	 */
	private String node_id;
	
	/**
	 * 子节点id
	 */
	private String childNodeId;

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
	 * @return the node_id
	 */
	public String getNode_id() {
		return node_id;
	}

	/**
	 * @param node_id the node_id to set
	 */
	public void setNode_id(String node_id) {
		this.node_id = node_id;
	}

	/**
	 * @return the childNodeId
	 */
	public String getChildNodeId() {
		return childNodeId;
	}

	/**
	 * @param childNodeId the childNodeId to set
	 */
	public void setChildNodeId(String childNodeId) {
		this.childNodeId = childNodeId;
	}
	
}