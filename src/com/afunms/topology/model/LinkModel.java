package com.afunms.topology.model;

public class LinkModel {
	private String id;
	private String from;//链路的起始坐标
	private String to;//链路的目标坐标
	private String linkInfo;//链路提示信息
	private String linkStatus;//链路状态
	private Float linkWeight;//链路线的宽度
	private String linkMenuInfo;//链路菜单信息
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getLinkInfo() {
		return linkInfo;
	}
	public void setLinkInfo(String linkInfo) {
		this.linkInfo = linkInfo;
	}
	public String getLinkStatus() {
		return linkStatus;
	}
	public void setLinkStatus(String linkStatus) {
		this.linkStatus = linkStatus;
	}
	public Float getLinkWeight() {
		return linkWeight;
	}
	public void setLinkWeight(Float linkWeight) {
		this.linkWeight = linkWeight;
	}
	public String getLinkMenuInfo() {
		return linkMenuInfo;
	}
	public void setLinkMenuInfo(String linkMenuInfo) {
		this.linkMenuInfo = linkMenuInfo;
	}

}
