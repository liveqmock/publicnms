/**
 * <p>Description:mapping table NMS_TOPO_XML</p>
 * <p>Company: dhcc.com</p>
 * @author 杨军
 * @project afunms
 * @date 2011-8-5
 */

package com.afunms.topology.model;

import  com.afunms.common.base.BaseVo;

public class ManageXml extends BaseVo
{
	private int id;
	private String xmlName;
	private String topoName;//拓扑图名称
	private String aliasName;//别名
	private String topoTitle;//标题
	private String topoArea;//地域
	private String topoBg;//背景图片
	private int topoType;//(拓扑图类型:0.默认 1.业务拓扑图 2.示意拓扑图 3.缩略拓扑图 4.子图)
	private String bid;//业务id
	private int home_view;
	private int bus_home_view;
	private float percent;
	private String utilhdx;
	private String utilhdxperc;
	private String supperid;
	private String fatherid;

	public String getFatherid() {
		return fatherid;
	}

	public void setFatherid(String fatherid) {
		this.fatherid = fatherid;
	}

	public String getSupperid() {
		return supperid;
	}

	public void setSupperid(String supperid) {
		this.supperid = supperid;
	}

	public String getUtilhdx() {
		return utilhdx;
	}

	public void setUtilhdx(String utilhdx) {
		this.utilhdx = utilhdx;
	}

	public String getUtilhdxperc() {
		return utilhdxperc;
	}

	public void setUtilhdxperc(String utilhdxperc) {
		this.utilhdxperc = utilhdxperc;
	}

	public float getPercent() {
		return percent;
	}

	public void setPercent(float percent) {
		this.percent = percent;
	}

	public int getHome_view() {
		return home_view;
	}

	public void setHome_view(int home_view) {
		this.home_view = home_view;
	}

	public int getId() 
	{
		return id;
	}

	public void setId(int id) 
	{
		this.id = id;
	}

	public String getXmlName() 
	{
		return xmlName;
	}

    public void setXmlName(String xmlName) 
    {
		this.xmlName = xmlName;
	}

	public String getAliasName() {
		return aliasName;
	}

	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}

	public String getTopoArea() {
		return topoArea;
	}

	public void setTopoArea(String topoArea) {
		this.topoArea = topoArea;
	}

	public String getTopoBg() {
		return topoBg;
	}

	public void setTopoBg(String topoBg) {
		this.topoBg = topoBg;
	}

	public String getTopoName() {
		return topoName;
	}

	public void setTopoName(String topoName) {
		this.topoName = topoName;
	}

	public String getTopoTitle() {
		return topoTitle;
	}

	public void setTopoTitle(String topoTitle) {
		this.topoTitle = topoTitle;
	}

	public String getBid() {
		return bid;
	}

	public void setBid(String bid) {
		this.bid = bid;
	}

	public int getTopoType() {
		return topoType;
	}

	public void setTopoType(int topoType) {
		this.topoType = topoType;
	}

	public int getBus_home_view() {
		return bus_home_view;
	}

	public void setBus_home_view(int bus_home_view) {
		this.bus_home_view = bus_home_view;
	}
}
