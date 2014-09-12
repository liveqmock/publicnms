/**
 * <p>Description:host,including server and exchange device</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-27
 */

package com.afunms.polling.node;

import com.afunms.polling.node.Application;

public class Bussiness extends Application {

	private int id;
	private String name;
	private String bid;
	private String sendmobiles;
	private String sendemail;
	private String sendphone;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getSendmobiles() {
		return sendmobiles;
	}
	public void setSendmobiles(String sendmobiles) {
		this.sendmobiles = sendmobiles;
	}
	public String getBid() {
		return bid;
	}
	public void setBid(String bid) {
		this.bid = bid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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