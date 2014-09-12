/**
 * <p>Description:mapping table nms_hint_line</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2010-01-4
 */

package com.afunms.topology.model;

import com.afunms.common.base.BaseVo;

public class HintLine extends BaseVo {
	
	private int id;
	
	private String lineId;

	private String lineName;

	private String fatherId;

	private String childId;
	
	private String xmlfile;
	
	private int width;
	
	private String fatherXy;

	private String childXy;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLineName() {
		return lineName;
	}

	public void setLineName(String lineName) {
		this.lineName = lineName;
	}

	public String getChildId() {
		return childId;
	}

	public void setChildId(String childId) {
		this.childId = childId;
	}

	public String getChildXy() {
		return childXy;
	}

	public void setChildXy(String childXy) {
		this.childXy = childXy;
	}

	public String getFatherId() {
		return fatherId;
	}

	public void setFatherId(String fatherId) {
		this.fatherId = fatherId;
	}

	public String getFatherXy() {
		return fatherXy;
	}

	public void setFatherXy(String fatherXy) {
		this.fatherXy = fatherXy;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public String getXmlfile() {
		return xmlfile;
	}

	public void setXmlfile(String xmlfile) {
		this.xmlfile = xmlfile;
	}

	public String getLineId() {
		return lineId;
	}

	public void setLineId(String lineId) {
		this.lineId = lineId;
	}
}
