/**
 * <p>Description:mapping table NMS_ALARM_INFO</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-28
 */

package com.afunms.topology.model;

import com.afunms.common.base.BaseVo;

public class DBConfig extends BaseVo
{
	private int id;
	private int nodeID;
	private String alias;
	private String dbName;
	private String user;
	private String password;
	private int dbType;
	
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getDbName() {
		return dbName;
	}
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
	public int getDbType() {
		return dbType;
	}
	public void setDbType(int dbType) {
		this.dbType = dbType;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getNodeID() {
		return nodeID;
	}
	public void setNodeID(int nodeID) {
		this.nodeID = nodeID;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}		
}