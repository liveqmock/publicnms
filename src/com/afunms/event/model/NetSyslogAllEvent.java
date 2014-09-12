package com.afunms.event.model;

/**
 * @author Administrator
 *
 */
public class NetSyslogAllEvent extends NetSyslogViewer {
	private String processname;

	public String getProcessname() {
		return processname;
	}

	public void setProcessname(String processname) {
		this.processname = processname;
	}
}