/**
 * <p>Description:mapping app_db_node</p>
 * <p>Company: dhcc.com</p>
 * @author miiwill
 * @project afunms
 * @date 2007-1-7
 */

package com.afunms.application.model;

import com.afunms.common.base.BaseVo;

public class WasConfig extends BaseVo {

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
	private String version;
	private String nodename;
	private String servername;
	private int supperid;//π©”¶…Ãid snow add at 2010-5-20
	
	
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public int getSupperid() {
		return supperid;
	}
	public void setSupperid(int supperid) {
		this.supperid = supperid;
	}

	
	
	public String getNodename() {
		return nodename;
	}
	public void setNodename(String nodename) {
		this.nodename = nodename;
	}
	public String getServername() {
		return servername;
	}
	public void setServername(String servername) {
		this.servername = servername;
	}
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