/**
 * <p>Description:mapping table NMS_TOPO_XML</p>
 * <p>Company: dhcc.com</p>
 * @author Äô³Éº£
 * @project afunms
 * @date 2006-10-20
 */

package com.afunms.topology.model;

import com.afunms.common.base.BaseVo;

public class Relation extends BaseVo {
	
	private int id;

	private String xmlName;

	private String nodeId;

	private String category;

	private String mapId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMapId() {
		return mapId;
	}

	public void setMapId(String mapId) {
		this.mapId = mapId;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getXmlName() {
		return xmlName;
	}

	public void setXmlName(String xmlName) {
		this.xmlName = xmlName;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

}
