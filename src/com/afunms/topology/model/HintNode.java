/**
 * <p>Description:mapping table nms_hint_node</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2010-01-4
 */

package com.afunms.topology.model;

import com.afunms.common.base.BaseVo;

public class HintNode extends BaseVo {
	
	private int id;
	
	private String name;
	
	private String nodeId;

	private String xmlfile;

	private String type;

	private String image;
	
	private String alias;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getXmlfile() {
		return xmlfile;
	}

	public void setXmlfile(String xmlfile) {
		this.xmlfile = xmlfile;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

}
