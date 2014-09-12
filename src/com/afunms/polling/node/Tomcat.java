/**
 * <p>Description:host,including server and exchange device</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-27
 */

package com.afunms.polling.node;

public class Tomcat extends Application
{
	private String user;
    private String password;
    private String port;    
        
	private String bid;
	private int monflag;
	private String sendmobiles;
	private String sendemail;
	private String sendphone;
	
	private String version;
	private String jvmversion;
	private String jvmvender;
	private String os;
	private String osversion;
	private String lastAlarm;
	
	public String getLastAlarm() {
		return lastAlarm;
	}
	public void setLastAlarm(String lastAlarm) {
		this.lastAlarm = lastAlarm;
	}
	public String getVersion(){
		return version;
	}
	public void setVersion(String version){
		this.version = version;
	}
	public String getJvmversion(){
		return jvmversion;
	}
	public void setJvmversion(String jvmversion){
		this.jvmversion = jvmversion;
	}
	public String getJvmvender(){
		return jvmvender;
	}
	public void setJvmvender(String jvmvender){
		this.jvmvender = jvmvender;
	}
	public String getOs(){
		return os;
	}
	public void setOs(String os){
		this.os = os;
	}
	public String getOsversion(){
		return osversion;
	}
	public void setOsversion(String osversion){
		this.osversion = osversion;
	}
	
	public String getBid() {
		return bid;
	}
	
	public void setBid(String bid) {
		this.bid = bid;
	}
	public int getMonflag() {
		return monflag;
	}
	public void setMonflag(int monflag) {
		this.monflag = monflag;
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
    
    public Tomcat()
    {
    	category = 51;  
    }

	public String getJspUrl() {
		return "http://" + getIpAddress() + ":" + getPort() + "/manager/tomcat_monitor.jsp";
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getXmlUrl() {
		return "http://" + getIpAddress() + ":" + getPort() + "/manager/tomcat_monitor.xml";
	}
}