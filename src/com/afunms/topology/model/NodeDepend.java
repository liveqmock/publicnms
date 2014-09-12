/**
 * <p>Description:mapping table nms_hint_line</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2010-01-4
 */

package com.afunms.topology.model;

import com.afunms.common.base.BaseVo;

public class NodeDepend extends BaseVo {
	
	private int id;
	
	private String nodeId;

	private String alias;
	
	private String xmlfile;

	private String location;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getXmlfile() {
		return xmlfile;
	}

	public void setXmlfile(String xmlfile) {
		this.xmlfile = xmlfile;
	}

}
