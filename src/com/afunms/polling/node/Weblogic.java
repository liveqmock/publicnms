/**
 * <p>Description:host,including server and exchange device</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-27
 */

package com.afunms.polling.node;

import com.afunms.common.base.BaseVo;
import com.afunms.polling.node.Application;

public class Weblogic extends Application {

	private int id;
	//private String alias;
	//private String ipAddress;
	private String community;
	private int portnum;
	private String sendmobiles;
	private int mon_flag;
	private String netid;
	private String sendemail;
	private String sendphone;
	private String serverName;
	private String serverAddr;
	private String serverPort;
	private String domainName;
	private String domainPort;
	private String domainVersion;
	
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCommunity() {
		return community;
	}
	public void setCommunity(String community) {
		this.community = community;
	}
	public int getPortnum() {
		return portnum;
	}
	public void setPortnum(int portnum) {
		this.portnum = portnum;
	}
	public String getSendmobiles() {
		return sendmobiles;
	}
	public void setSendmobiles(String sendmobiles) {
		this.sendmobiles = sendmobiles;
	}
	public int getMon_flag() {
		return mon_flag;
	}
	public void setMon_flag(int mon_flag) {
		this.mon_flag = mon_flag;
	}
	public String getNetid() {
		return netid;
	}
	public void setNetid(String netid) {
		this.netid = netid;
	}
	public String getSendemail() {
		return sendemail;
	}
	public void setSendemail(String sendemail) {
		this.sendemail = sendemail;
	}
	public String getSendphone() {
		return sendphone;
	}
	public void setSendphone(String sendphone) {
		this.sendphone = sendphone;
	}
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public String getServerAddr() {
		return serverAddr;
	}
	public void setServerAddr(String serverAddr) {
		this.serverAddr = serverAddr;
	}
	public String getServerPort() {
		return serverPort;
	}
	public void setServerPort(String serverPort) {
		this.serverPort = serverPort;
	}
	public String getDomainName() {
		return domainName;
	}
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	public String getDomainPort() {
		return domainPort;
	}
	public void setDomainPort(String domainPort) {
		this.domainPort = domainPort;
	}
	public String getDomainVersion() {
		return domainVersion;
	}
	public void setDomainVersion(String domainVersion) {
		this.domainVersion = domainVersion;
	}
	
}