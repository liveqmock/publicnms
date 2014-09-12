/**
 * <p>Description:host,including server and exchange device</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-27
 */

package com.afunms.polling.node;

import com.afunms.polling.node.Application;

public class Proces extends Application {

	private int id;
	private String name;
	private String ipaddress;
	private String sendmobiles;
	private String sendemail;
	private String sendphone;
	private int supperid;//π©”¶…Ãid snow add at 2010-5-27
	
	public int getSupperid() {
		return supperid;
	}
	public void setSupperid(int supperid) {
		this.supperid = supperid;
	}
	
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
	public String getIpaddress() {
		return ipaddress;
	}
	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}
	
}