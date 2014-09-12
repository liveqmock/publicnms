/**
 * <p>Description:mapping app_db_node</p>
 * <p>Company: dhcc.com</p>
 * @author miiwill
 * @project afunms
 * @date 2007-1-7
 */

package com.afunms.application.model;

import com.afunms.common.base.BaseVo;
import com.afunms.polling.node.Application;

public class TuxedoConfig extends BaseVo {

	/**
	 * id
	 */
	private int id;
	
	/**
	 * name
	 */
	private String name;
	
	/**
	 * ip
	 */
	private String ipAddress;
	
	/**
	 * 共同体
	 */
	private String community;
	
	/**
	 * 端口号
	 */
	private String port;
	
	/**
	 * 状态
	 */
	private String status;
	
	/**
	 * 监控状态
	 */
	private String mon_flag;
	
	/**
	 * 业务权限
	 */
	private String bid;
	
	/**
	 * 告警发送手机号
	 */
	private String sendmobiles;
	
	/**
	 * 告警发送email
	 */
	private String sendemail;
	
	/**
	 * 告警发送电话
	 */
	private String sendphone;
	
	/**
	 * 以下三个为保留字
	 */
	private String reservation1;
	private String reservation2;
	private String reservation3;
	
	
	/**
	 * 
	 */
	public TuxedoConfig() {
		// TODO Auto-generated constructor stub
	}


	/**
	 * @param id
	 * @param name
	 * @param ipAddress
	 * @param community
	 * @param port
	 * @param status
	 * @param mon_flag
	 * @param bid
	 * @param sendmobiles
	 * @param sendemail
	 * @param sendphone
	 * @param reservation1
	 * @param reservation2
	 * @param reservation3
	 */
	public TuxedoConfig(int id, String name, String ipAddress,
			String community, String port, String status, String mon_flag,
			String bid, String sendmobiles, String sendemail, String sendphone,
			String reservation1, String reservation2, String reservation3) {
		this.id = id;
		this.name = name;
		this.ipAddress = ipAddress;
		this.community = community;
		this.port = port;
		this.status = status;
		this.mon_flag = mon_flag;
		this.bid = bid;
		this.sendmobiles = sendmobiles;
		this.sendemail = sendemail;
		this.sendphone = sendphone;
		this.reservation1 = reservation1;
		this.reservation2 = reservation2;
		this.reservation3 = reservation3;
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
	 * @return the ipAddress
	 */
	public String getIpAddress() {
		return ipAddress;
	}


	/**
	 * @param ipAddress the ipAddress to set
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}


	/**
	 * @return the community
	 */
	public String getCommunity() {
		return community;
	}


	/**
	 * @param community the community to set
	 */
	public void setCommunity(String community) {
		this.community = community;
	}


	/**
	 * @return the port
	 */
	public String getPort() {
		return port;
	}


	/**
	 * @param port the port to set
	 */
	public void setPort(String port) {
		this.port = port;
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


	/**
	 * @return the reservation1
	 */
	public String getReservation1() {
		return reservation1;
	}


	/**
	 * @param reservation1 the reservation1 to set
	 */
	public void setReservation1(String reservation1) {
		this.reservation1 = reservation1;
	}


	/**
	 * @return the reservation2
	 */
	public String getReservation2() {
		return reservation2;
	}


	/**
	 * @param reservation2 the reservation2 to set
	 */
	public void setReservation2(String reservation2) {
		this.reservation2 = reservation2;
	}


	/**
	 * @return the reservation3
	 */
	public String getReservation3() {
		return reservation3;
	}


	/**
	 * @param reservation3 the reservation3 to set
	 */
	public void setReservation3(String reservation3) {
		this.reservation3 = reservation3;
	}


	
	
	
	
	
}