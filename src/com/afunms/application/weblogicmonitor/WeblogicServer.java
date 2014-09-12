package com.afunms.application.weblogicmonitor;

public class WeblogicServer {
	String serverRuntimeName = null;	//服务名
	String serverRuntimeListenAddress = null;	//服务监听地址
	String serverRuntimeListenPort = null;	//服务监听端口
	String serverRuntimeOpenSocketsCurrentCount = null;	//当前Socket数
	String serverRuntimeState = null;	//服务器当前运行状态
	String ipaddress = "";	//服务器IP
	
	public String getIpaddress(){
		return this.ipaddress;
	}
	public void setIpaddress(String ip){
		this.ipaddress = ip;
	}
	public String getServerRuntimeName() {
		return serverRuntimeName;
	}
	public void setServerRuntimeName(String serverRuntimeName) {
		this.serverRuntimeName = serverRuntimeName;
	}
	public String getServerRuntimeListenAddress() {
		return serverRuntimeListenAddress;
	}
	public void setServerRuntimeListenAddress(String serverRuntimeListenAddress) {
		this.serverRuntimeListenAddress = serverRuntimeListenAddress;
	}
	public String getServerRuntimeListenPort() {
		return serverRuntimeListenPort;
	}
	public void setServerRuntimeListenPort(String serverRuntimeListenPort) {
		this.serverRuntimeListenPort = serverRuntimeListenPort;
	}
	public String getServerRuntimeOpenSocketsCurrentCount() {
		return serverRuntimeOpenSocketsCurrentCount;
	}
	public void setServerRuntimeOpenSocketsCurrentCount(
			String serverRuntimeOpenSocketsCurrentCount) {
		this.serverRuntimeOpenSocketsCurrentCount = serverRuntimeOpenSocketsCurrentCount;
	}
	public String getServerRuntimeState() {
		return serverRuntimeState;
	}
	public void setServerRuntimeState(String serverRuntimeState) {
		this.serverRuntimeState = serverRuntimeState;
	}

	
}
