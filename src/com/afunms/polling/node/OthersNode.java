/**
 * <p>Description:mapping table nms_hint_node</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2010-01-4
 */

package com.afunms.polling.node;

public class OthersNode extends Application{
	
	private String name;
	
	private String ipaddress;

	private String alais;

	private String sendmobiles;
	
	private String sendemail;
	
	private String sendphone;
	
	private String bid;
	
	private int managed;

	public String getAlais() {
		return alais;
	}

	public void setAlais(String alais) {
		this.alais = alais;
	}

	public String getBid() {
		return bid;
	}

	public void setBid(String bid) {
		this.bid = bid;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
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

	public String getSendmobiles() {
		return sendmobiles;
	}

	public void setSendmobiles(String sendmobiles) {
		this.sendmobiles = sendmobiles;
	}

	public String getSendphone() {
		return sendphone;
	}

	public void setSendphone(String sendphone) {
		this.sendphone = sendphone;
	}

	public int getManaged() {
		return managed;
	}

	public void setManaged(int managed) {
		this.managed = managed;
	}

}
