/**
 * <p>Description:mapping topo_tomcat_node</p>
 * <p>Company: afunms</p>
 * @author miiwill
 * @project afunms
 * @date 2006-12-06
 */

package com.afunms.application.model;

import com.afunms.common.base.BaseVo;

public class Tomcat extends BaseVo
{
	private int id;
	private String alias;
	private String ipAddress;
	private String port;
	private String user;
	private String password;
	private int status;
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
	private int supperid;//π©”¶…Ãid snow add at 2010-5-20
	
	public int getSupperid() {
		return supperid;
	}
	public void setSupperid(int supperid) {
		this.supperid = supperid;
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
	
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getAlias() {
		return alias;
	}
	
	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getIpAddress() {
		return ipAddress;
	}
	
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
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

}
