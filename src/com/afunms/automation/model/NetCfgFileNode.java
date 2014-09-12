package com.afunms.automation.model;

import com.afunms.common.base.BaseVo;
/**
 * 
 * @descrition 自动化配置节点MODEL,topo_node_telnetconfig 表对应的model
 * @author wangxiangyong
 * @date Aug 26, 2014 11:21:32 AM
 */
public class NetCfgFileNode extends BaseVo {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;// id
	private String ipaddress;// ip地址
	private String alias;// 别名
	private String user;// 用户
	private String password;// 密码
	private String suuser;// su用户
	private String supassword;// su密码
	private int port;// 端口
//	private String defaultpromtp;// 系统提示符号
//	private int enablevpn; // 是否启动vpn配置信息采集
//	private int isSynchronized;// 是否同步 1同步 0不同步
	private String deviceRender;// 设备提供商 h3c cisco
//	private String threeA;// 3A认证
//	private int encrypt;// 是否加密 1加密 2不加密
	private String ostype;
	private int connecttype; // 登陆方式 0:telnet 1:ssh

	public String getOstype() {
		return ostype;
	}

	public void setOstype(String ostype) {
		this.ostype = ostype;
	}

	

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getDeviceRender() {
		return deviceRender;
	}

	public void setDeviceRender(String deviceRender) {
		this.deviceRender = deviceRender;
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



	public int getConnecttype() {
		return connecttype;
	}

	public void setConnecttype(int connecttype) {
		this.connecttype = connecttype;
	}

	



	public String getSuuser() {
		return suuser;
	}

	public void setSuuser(String suuser) {
		this.suuser = suuser;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}
}