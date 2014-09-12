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

import com.afunms.common.base.BaseVo;

import com.afunms.common.base.BaseVo;

public class Was extends Application {

	private int id;
	private String name;
	private String ipaddress;
	private String community;
	private int portnum;
	private String sendmobiles;
	private int mon_flag;
	private String netid;
	private String sendemail;
	private String sendphone;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIpaddress() {
		return ipaddress;
	}
	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
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
	
}