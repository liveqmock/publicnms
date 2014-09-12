package com.afunms.system.vo;

import java.io.Serializable;

public class EventsVo implements Serializable{ 
	
	private String level1;
	
	private String content;
	
	private String rtime1;
	
	private String rptman;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getLevel1() {
		return level1;
	}

	public void setLevel1(String level1) {
		this.level1 = level1;
	}

	public String getRptman() {
		return rptman;
	}

	public void setRptman(String rptman) {
		this.rptman = rptman;
	}

	public String getRtime1() {
		return rtime1;
	}

	public void setRtime1(String rtime1) {
		this.rtime1 = rtime1;
	}
	
}