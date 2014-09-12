package com.afunms.automation.model;

import com.afunms.common.base.BaseVo;
/**
 * 
 * @descrition 定时备份条件MODEL
 * @author wangxiangyong
 * @date Aug 29, 2014 3:50:26 PM
 */
public class TimingBackupCondition extends BaseVo{
	
	private static final long serialVersionUID = -7612858499011672708L;
	private int id;
	private int timingId;
	private int isContain;
	private String content;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getTimingId() {
		return timingId;
	}
	public void setTimingId(int timingId) {
		this.timingId = timingId;
	}
	public int getIsContain() {
		return isContain;
	}
	public void setIsContain(int isContain) {
		this.isContain = isContain;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

	}

