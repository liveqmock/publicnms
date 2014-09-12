/**
 * <p>Description:mapping app_db_node</p>
 * <p>Company: dhcc.com</p>
 * @author miiwill
 * @project afunms
 * @date 2007-1-7
 */

package com.afunms.application.model;

public class TablesVO {
	// 用户表
	String Users_name = null;//用户名称
	String ID_in_db = null;//数据库中的id
	String Group_name = null;//所属组组名
	String Login_name = null;//登陆名称
	//远程服务器
	String Server_name = null; //-服务器名称
	String Server_network_name = null; //-服务器网络名称
	String Server_class = null; //-所属类别
	String Server_status = null; //-服务器状态
	//转储设备或数据库设备信息
	String device_name = null; //-设备名称
	String device_physical_name = null; //-设备物理名称
	String device_description = null; //-设备描述"
    //服务器数据库信息
	String db_name = null; //-数据库名称
	String db_size = null; //-数据库大小
	String db_owner = null; //-数据库所有者
	String db_created = null; //-数据库创建时间
	String db_status = null; //-数据库状态
	String db_freesize = null; //-数据库可使用大小
	String db_usedperc = null; //-数据库利用率
	
	//擎数
	String engine = null;
	String status = null;
	String starttime = null;
	
	//进程信息
	String spid = null;
	String hostname = null;
	String prostatus = null;
	String hostprocess = null;
	String program_name = null;
	
	//长事务
	String dbid = null;
	String name = null;
	
	//库信息
	String createdate = null;
	String dumptrdate = null;
	

	
	public String getDb_freesize() {
		return db_freesize;
	}
	public void setDb_freesize(String db_freesize) {
		this.db_freesize = db_freesize;
	}	
	public String getDb_usedperc() {
		return db_usedperc;
	}
	public void setDb_usedperc(String db_usedperc) {
		this.db_usedperc = db_usedperc;
	}	
	
	public String getDevice_name() {
		return device_name;
	}
	public void setDevice_name(String device_name) {
		this.device_name = device_name;
	}
	public String getDevice_physical_name() {
		return device_physical_name;
	}
	public void setDevice_physical_name(String device_physical_name) {
		this.device_physical_name = device_physical_name;
	}
	public String getDevice_description() {
		return device_description;
	}
	public void setDevice_description(String device_description) {
		this.device_description = device_description;
	}
	public String getServer_name() {
		return Server_name;
	}
	public void setServer_name(String server_name) {
		Server_name = server_name;
	}
	public String getServer_network_name() {
		return Server_network_name;
	}
	public void setServer_network_name(String server_network_name) {
		Server_network_name = server_network_name;
	}
	public String getServer_class() {
		return Server_class;
	}
	public void setServer_class(String server_class) {
		Server_class = server_class;
	}
	public String getServer_status() {
		return Server_status;
	}
	public void setServer_status(String server_status) {
		Server_status = server_status;
	}
	public String getUsers_name() {
		return Users_name;
	}
	public void setUsers_name(String users_name) {
		Users_name = users_name;
	}
	public String getID_in_db() {
		return ID_in_db;
	}
	public void setID_in_db(String id_in_db) {
		ID_in_db = id_in_db;
	}
	public String getGroup_name() {
		return Group_name;
	}
	public void setGroup_name(String group_name) {
		Group_name = group_name;
	}
	public String getLogin_name() {
		return Login_name;
	}
	public void setLogin_name(String login_name) {
		Login_name = login_name;
	}
	public String getDb_name() {
		return db_name;
	}
	public void setDb_name(String db_name) {
		this.db_name = db_name;
	}
	public String getDb_size() {
		return db_size;
	}
	public void setDb_size(String db_size) {
		this.db_size = db_size;
	}
	public String getDb_owner() {
		return db_owner;
	}
	public void setDb_owner(String db_owner) {
		this.db_owner = db_owner;
	}
	public String getDb_created() {
		return db_created;
	}
	public void setDb_created(String db_created) {
		this.db_created = db_created;
	}
	public String getDb_status() {
		return db_status;
	}
	public void setDb_status(String db_status) {
		this.db_status = db_status;
	}
	public String getEngine() {
		return engine;
	}
	public void setEngine(String engine) {
		this.engine = engine;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStarttime() {
		return starttime;
	}
	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}
	public String getSpid() {
		return spid;
	}
	public void setSpid(String spid) {
		this.spid = spid;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public String getProstatus() {
		return prostatus;
	}
	public void setProstatus(String prostatus) {
		this.prostatus = prostatus;
	}
	public String getHostprocess() {
		return hostprocess;
	}
	public void setHostprocess(String hostprocess) {
		this.hostprocess = hostprocess;
	}
	public String getProgram_name() {
		return program_name;
	}
	public void setProgram_name(String program_name) {
		this.program_name = program_name;
	}
	public String getDbid() {
		return dbid;
	}
	public void setDbid(String dbid) {
		this.dbid = dbid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDumptrdate() {
		return dumptrdate;
	}
	public void setDumptrdate(String dumptrdate) {
		this.dumptrdate = dumptrdate;
	}
	public String getCreatedate() {
		return createdate;
	}
	public void setCreatedate(String createdate) {
		this.createdate = createdate;
	}
	
}
