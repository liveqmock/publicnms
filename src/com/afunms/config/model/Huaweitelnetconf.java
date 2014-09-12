package com.afunms.config.model;

import com.afunms.common.base.BaseVo;


/**
 * 
 * topo_node_telnetconfig 表对应的model
 * 
 * @author konglq
 *
 */
public class Huaweitelnetconf extends BaseVo
{
    private int id;//id
    private String ipaddress;//ip地址
    private String user;//用户
    private String password;//密码
    private String suuser;//su用户
    private String supassword;//su密码
    private int port;//端口
    private String defaultpromtp;//系统提示符号
    private int enablevpn; //是否启动vpn配置信息采集
    private int isSynchronized;//是否同步 1同步  0不同步
    private String deviceRender;//设备提供商   h3c cisco
    private String threeA;//3A认证
    private int encrypt;//是否加密 1加密 2不加密
    private String ostype;
    private int connecttype; //登陆方式  0:telnet 1:ssh
    
	public String getOstype() {
		return ostype;
	}
	public void setOstype(String ostype) {
		this.ostype = ostype;
	}
	public int getEncrypt() {
		return encrypt;
	}
	public void setEncrypt(int encrypt) {
		this.encrypt = encrypt;
	}
	public String getThreeA() {
		return threeA;
	}
	public void setThreeA(String threeA) {
		this.threeA = threeA;
	}
	public String getDeviceRender() {
		return deviceRender;
	}
	public void setDeviceRender(String deviceRender) {
		this.deviceRender = deviceRender;
	}
	public int getIsSynchronized() {
		return isSynchronized;
	}
	public void setIsSynchronized(int isSynchronized) {
		this.isSynchronized = isSynchronized;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getIpaddress() {
		return ipaddress;
	}
	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getSuuser() {
		return suuser;
	}
	public void setSuuser(String suuser) {
		this.suuser = suuser;
	}
	public String getSupassword() {
		return supassword;
	}
	public void setSupassword(String supassword) {
		this.supassword = supassword;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getDefaultpromtp() {
		return defaultpromtp;
	}
	public void setDefaultpromtp(String defaultpromtp) {
		this.defaultpromtp = defaultpromtp;
	}
	public int getEnablevpn() {
		return enablevpn;
	}
	public void setEnablevpn(int enablevpn) {
		this.enablevpn = enablevpn;
	}
	public int getConnecttype() {
		return connecttype;
	}
	public void setConnecttype(int connecttype) {
		this.connecttype = connecttype;
	}  
}