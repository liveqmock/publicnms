/**
 * <p>Description:mapping app_db_node</p>
 * <p>Company: dhcc.com</p>
 * @author miiwill
 * @project afunms
 * @date 2007-1-7
 */

package com.afunms.application.model;

import com.afunms.common.base.BaseVo;

public class DBVo extends BaseVo
{
	private int id;
	private String alias;
	private int category;
	private String ipAddress;
	private String dbName;
	private String port;	
	private String user;
	private String password;
	private int status;
	private String dbuse;
	private String sendmobiles;
	private String sendemail;
	private String sendphone;
	private String bid;
	private int managed;
	private int dbtype;
	private int collecttype;
	private int supperid;// π©”¶…Ãid snow add 2010-05-20
	private String db_url;

	public int getSupperid() {
		return supperid;
	}

	public void setSupperid(int supperid) {
		this.supperid = supperid;
	}

	public int getCollecttype() {
		return collecttype;
	}
	public void setCollecttype(int collecttype) {
		this.collecttype = collecttype;
	}
	public int getDbtype() {
		return dbtype;
	}
	public void setDbtype(int dbtype) {
		this.dbtype = dbtype;
	}
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getManaged() {
		return managed;
	}
	public void setManaged(int managed) {
		this.managed = managed;
	}
	public String getDbName() {
		return dbName;
	}
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
	public int getCategory() {
		return category;
	}
	public void setCategory(int category) {
		this.category = category;
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
	public String getAlias() {
		return alias;
	}	
	public void setAlias(String alias) {
		this.alias = alias;
	}	
	public String getDbuse() {
		return dbuse;
	}	
	public void setDbuse(String dbuse) {
		this.dbuse = dbuse;
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
	public String getBid() {
		return bid;
	}	
	public void setBid(String bid) {
		this.bid = bid;
	}

	public String getDb_url() {
		return db_url;
	}

	public void setDb_url(String db_url) {
		this.db_url = db_url;
	}
}