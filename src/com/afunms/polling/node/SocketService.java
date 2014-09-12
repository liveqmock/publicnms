/**
 * <p>Description:host,including server and exchange device</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-27
 */

package com.afunms.polling.node;

public class SocketService extends Application
{
	private int id;
	private String ipaddress;
	private String port;
	private String portdesc;
	private int monflag;
	private int flag;
	private int timeout;
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
	public String getIpaddress() {
		return ipaddress;
	}
	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getPortdesc() {
		return portdesc;
	}
	public void setPortdesc(String portdesc) {
		this.portdesc = portdesc;
	}
	public int getMonflag() {
		return monflag;
	}
	public void setMonflag(int monflag) {
		this.monflag = monflag;
	}
	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	public int getTimeout() {
		return timeout;
	}
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	public String getBid() {
		return bid;
	}
	
	public void setBid(String bid) {
		this.bid = bid;
	}
	public String getSendmobiles() {
		return sendmobiles;
	}
	public void setSendmobiles(String sendmobiles) {
		this.sendmobiles = sendmobiles;
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