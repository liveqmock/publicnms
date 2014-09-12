/**
 * <p>Description:host,including server and exchange device</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-27
 */

package com.afunms.polling.node;

public class DBNode extends Application
{
	private String dbName;
	private String port;	
	private String user;
	private String password;
	private int dbtype;
	
	private int collecttype;

	public int getCollecttype() {
		return collecttype;
	}
	public void setCollecttype(int collecttype) {
		this.collecttype = collecttype;
	}
	
	public String getDbName() {
		return dbName;
	}
	
	public void setDbName(String dbName) {
		this.dbName = dbName;
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
	public int getDbtype() {
		return dbtype;
	}
	public void setDbtype(int dbtype) {
		this.dbtype = dbtype;
	}
}