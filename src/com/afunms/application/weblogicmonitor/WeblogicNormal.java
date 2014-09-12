package com.afunms.application.weblogicmonitor;

public class WeblogicNormal {
	String domainName = null;//活动域名
	String domainActive = null;	//WEBLOGIC当前域的活动状态
	String domainAdministrationPort = null;	//管理端口
	String domainConfigurationVersion = null;	//	版本

	
	
	
	public String getDomainName() {
		return domainName;
	}
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	public String getDomainActive() {
		return domainActive;
	}
	public void setDomainActive(String domainActive) {
		this.domainActive = domainActive;
	}
	public String getDomainAdministrationPort() {
		return domainAdministrationPort;
	}
	public void setDomainAdministrationPort(String domainAdministrationPort) {
		this.domainAdministrationPort = domainAdministrationPort;
	}
	public String getDomainConfigurationVersion() {
		return domainConfigurationVersion;
	}
	public void setDomainConfigurationVersion(String domainConfigurationVersion) {
		this.domainConfigurationVersion = domainConfigurationVersion;
	}
	
}
