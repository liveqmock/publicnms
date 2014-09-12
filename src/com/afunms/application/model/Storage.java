/**
 * <p>Description:mapping app_db_node</p>
 * <p>Company: dhcc.com</p>
 * @author hkmw
 * @project afunms
 * @date 2007-1-7
 */

package com.afunms.application.model;

import com.afunms.common.base.BaseVo;

/**
 * 
 * @author nielin
 * @date 2010-06-24
 * 
 * 此类为存储model类 与 数据库表 nms_storage
 *
 */
public class Storage extends BaseVo
{
	/**
	 * 
	 */
	private int id;
	
	/**
	 * ipaddress
	 */
	private String ipaddress;
	
	/**
	 * 名称
	 */
	private String name;
	
	/**
	 * 用户名
	 */
	private String username;
	
	/**
	 * 密码
	 */
	private String password;
	
	/**
	 * 状态 0: 告警 1: 正常
	 * 
	 */
	private String status;
	
	/**
	 * 是否监视 0: 否 1: 是
	 * 
	 */
	private String mon_flag;
	
	/**
	 * 采集方式
	 */
	private String collecttype;
	
	/**
	 * 厂商
	 */
	private String company;
	
	/**
	 * 型号
	 */
	private String type;
	
	/**
	 * 型号
	 */
	private String serialNumber;
	
	/**
	 * 所属业务
	 */
	private String bid;
	
	/**
	 * 采集时间
	 */
	private String collectTime;
	
	/**
	 * 供应商
	 */
	private String supperid;// 供应商id snow add 2010-05-20
	
	/**
	 * 邮件
	 */
	private String sendemail;
	
	/**
	 * 短信
	 */
	private String sendmobiles;
	
	/**
	 * 电话
	 */
	private String sendphone;

	/**
	 * 
	 */
	public Storage() {
		// TODO Auto-generated constructor stub
	}
	
	public String snmpversion;
//	
	public String community;
//	
//	public String sys_name;
//	
//	public String net_mask;
//	
//	public String sys_descr;
//	
//	public String sys_oid;
//	
//	public String write_community;
//	
//	public String bridge_address;
//	
//	public String sys_location;
//	
//	public String sys_contact;

	public String getSnmpversion() {
		return snmpversion;
	}

	public void setSnmpversion(String snmpversion) {
		this.snmpversion = snmpversion;
	}

	public String getCommunity() {
		return community;
	}

	public void setCommunity(String community) {
		this.community = community;
	}

	/**
	 * @param id
	 * @param ipaddress
	 * @param name
	 * @param username
	 * @param password
	 * @param status
	 * @param mon_flag
	 * @param collecttype
	 * @param company
	 * @param type
	 * @param serialNumber
	 * @param bid
	 * @param collectTime
	 * @param supperid
	 * @param sendemail
	 * @param sendmobiles
	 * @param sendphone
	 */
	public Storage(int id, String ipaddress, String name, String username,
			String password, String status, String mon_flag,
			String collecttype, String company, String type,
			String serialNumber, String bid, String collectTime,
			String supperid, String sendemail, String sendmobiles,
			String sendphone) {
		this.id = id;
		this.ipaddress = ipaddress;
		this.name = name;
		this.username = username;
		this.password = password;
		this.status = status;
		this.mon_flag = mon_flag;
		this.collecttype = collecttype;
		this.company = company;
		this.type = type;
		this.serialNumber = serialNumber;
		this.bid = bid;
		this.collectTime = collectTime;
		this.supperid = supperid;
		this.sendemail = sendemail;
		this.sendmobiles = sendmobiles;
		this.sendphone = sendphone;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the ipaddress
	 */
	public String getIpaddress() {
		return ipaddress;
	}

	/**
	 * @param ipaddress the ipaddress to set
	 */
	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the mon_flag
	 */
	public String getMon_flag() {
		return mon_flag;
	}

	/**
	 * @param mon_flag the mon_flag to set
	 */
	public void setMon_flag(String mon_flag) {
		this.mon_flag = mon_flag;
	}

	/**
	 * @return the collecttype
	 */
	public String getCollecttype() {
		return collecttype;
	}

	/**
	 * @param collecttype the collecttype to set
	 */
	public void setCollecttype(String collecttype) {
		this.collecttype = collecttype;
	}

	/**
	 * @return the company
	 */
	public String getCompany() {
		return company;
	}

	/**
	 * @param company the company to set
	 */
	public void setCompany(String company) {
		this.company = company;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the serialNumber
	 */
	public String getSerialNumber() {
		return serialNumber;
	}

	/**
	 * @param serialNumber the serialNumber to set
	 */
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	/**
	 * @return the bid
	 */
	public String getBid() {
		return bid;
	}

	/**
	 * @param bid the bid to set
	 */
	public void setBid(String bid) {
		this.bid = bid;
	}

	/**
	 * @return the collectTime
	 */
	public String getCollectTime() {
		return collectTime;
	}

	/**
	 * @param collectTime the collectTime to set
	 */
	public void setCollectTime(String collectTime) {
		this.collectTime = collectTime;
	}

	/**
	 * @return the supperid
	 */
	public String getSupperid() {
		return supperid;
	}

	/**
	 * @param supperid the supperid to set
	 */
	public void setSupperid(String supperid) {
		this.supperid = supperid;
	}

	/**
	 * @return the sendemail
	 */
	public String getSendemail() {
		return sendemail;
	}

	/**
	 * @param sendemail the sendemail to set
	 */
	public void setSendemail(String sendemail) {
		this.sendemail = sendemail;
	}

	/**
	 * @return the sendmobiles
	 */
	public String getSendmobiles() {
		return sendmobiles;
	}

	/**
	 * @param sendmobiles the sendmobiles to set
	 */
	public void setSendmobiles(String sendmobiles) {
		this.sendmobiles = sendmobiles;
	}

	/**
	 * @return the sendphone
	 */
	public String getSendphone() {
		return sendphone;
	}

	/**
	 * @param sendphone the sendphone to set
	 */
	public void setSendphone(String sendphone) {
		this.sendphone = sendphone;
	}



	

	

	
}