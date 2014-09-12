package com.afunms.system.vo;

import java.io.Serializable;

public class EventVo implements Serializable{ 
	
	private String level1;
	
	private String eventlocation;
	
	private String content;
	
	private String recordtime;
	
	private int nodeid;

	public int getNodeid() {
		return nodeid;
	}

	public void setNodeid(int nodeid) {
		this.nodeid = nodeid;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getEventlocation() {
		return eventlocation;
	}

	public void setEventlocation(String eventlocation) {
		this.eventlocation = eventlocation;
	}

	public String getLevel1() {
		return level1;
	}

	public void setLevel1(String level1) {
		this.level1 = level1;
	}

	public String getRecordtime() {
		return recordtime;
	}

	public void setRecordtime(String recordtime) {
		this.recordtime = recordtime;
	}

}