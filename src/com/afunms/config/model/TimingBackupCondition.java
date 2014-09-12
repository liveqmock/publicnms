package com.afunms.config.model;

import com.afunms.common.base.BaseVo;

public class TimingBackupCondition extends BaseVo{
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
