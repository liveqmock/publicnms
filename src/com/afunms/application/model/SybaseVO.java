/**
 * <p>Description:mapping app_db_node</p>
 * <p>Company: dhcc.com</p>
 * @author miiwill
 * @project afunms
 * @date 2007-1-7
 */

package com.afunms.application.model;

import java.util.*;

public class SybaseVO {
	String cpu_busy = "";//cpu工作时间
	String idle = ""; //数据库空闲时间
	String version = "";//版本
	String io_busy = "";//输入数出工作时间
	String Sent_rate = "";//写到网络上的输出数据速率
	String Received_rate = "";//从网络上读取的输入数据速率
	String Write_rate = "";//写入磁盘速率
	String Read_rate = "";//读取磁盘速率
    String ServerName = "";//服务器名称
    
    
    String Cpu_busy_rate = "";    //	%	cpu利用率(已经乘以了100)	
    String Io_busy_rate = "";    //  	%	IO利用率(已经乘以了100)	
    String Disk_count = "";	//	转储设备或数据库设备个数	
    String Locks_count = "";	//	活动锁个数	
    String long_locks_count = "";	//	持有锁并且阻塞其他进程的进程数
    String Xact_count = "";	 //	事务的个数	
    String offlineengine = ""; //离线状态的引擎数
    String maxconn = "";	 //	数据库最大连接数
    String usedconn = "";    //已经使用的连接数
    String processcount = ""; //总进程数
    String sleepingproccount = "";//空闲进程
    String maxlock = ""; //允许的最大锁
    String longtrans = ""; //长事务个数
    String maxPageSize = ""; //页大小
    String totallog = "";
    String freelog = "";
    String usedlog = "";
    String reservedlog = "";
    String logusedperc = "0"; //日志空间利用率
    String maxsegusedperc = "0"; //最满段空间利用率
    String curdate = "";  //数据库当前日期
    String boottime = ""; //数据库启动时间
    
    
    String Total_dataCache = "";	 //  MB	总数据高速缓存大小
    String Total_physicalMemory	= ""; //   MB	总物理内存大小	
    String Metadata_cache = "";      //MB	Metadata缓存     
    String Procedure_cache = "";   //	MB	存储过程缓存大小   
    String Total_logicalMemory = "";   //	MB	总逻辑内存大小	
    
    String Data_hitrate	= "";  //   % 	数据缓存匹配度	
    String Procedure_hitrate = "";   // %	存储缓存匹配度	
    List deviceInfo= new ArrayList();
    List engineInfo= new ArrayList();
    List userInfo = new ArrayList();
    List dbInfo = new ArrayList();
    List serversInfo = new ArrayList();
    List processInfo = new ArrayList();
    List longtransInfo = new ArrayList();
    Hashtable seglogInfo = new Hashtable();
    List dbsInfo = new ArrayList();
    
    public List getServersInfo(){
    	return serversInfo;
    }
    public void setServersInfo(List serversInfo){
    	this.serversInfo=serversInfo;
    }
    

    public List getDbInfo(){
    	return dbInfo;
    }
    public void setDbInfo(List dbInfo){
    	this.dbInfo=dbInfo;
    }
    
    public List getDeviceInfo(){
    	return deviceInfo;
    }
    public void setDeviceInfo(List deviceInfo){
    	this.deviceInfo=deviceInfo;
    }
	
    public List getUserInfo(){
    	return userInfo;
    }
    public void setUserInfo(List userInfo){
    	this.userInfo=userInfo;
    }
    
	public String getCpu_busy_rate() {
		return Cpu_busy_rate;
	}
	public void setCpu_busy_rate(String cpu_busy_rate) {
		Cpu_busy_rate = cpu_busy_rate;
	}
	public String getServerName() {
		return ServerName;
	}
	public void setServerName(String serverName) {
		ServerName = serverName;
	}
	public String getSent_rate() {
		return Sent_rate;
	}
	public void setSent_rate(String sent_rate) {
		Sent_rate = sent_rate;
	}
	public String getReceived_rate() {
		return Received_rate;
	}
	public void setReceived_rate(String received_rate) {
		Received_rate = received_rate;
	}
	public String getWrite_rate() {
		return Write_rate;
	}
	public void setWrite_rate(String write_rate) {
		Write_rate = write_rate;
	}
	public String getRead_rate() {
		return Read_rate;
	}
	public void setRead_rate(String read_rate) {
		Read_rate = read_rate;
	}
	public String getCpu_busy() {
		return cpu_busy;
	}
	public void setCpu_busy(String cpu_busy) {
		this.cpu_busy = cpu_busy;
	}
	public String getIdle() {
		return idle;
	}
	public void setIdle(String idle) {
		this.idle = idle;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getIo_busy() {
		return io_busy;
	}
	public void setIo_busy(String io_busy) {
		this.io_busy = io_busy;
	}
	public String getIo_busy_rate() {
		return Io_busy_rate;
	}
	public void setIo_busy_rate(String io_busy_rate) {
		Io_busy_rate = io_busy_rate;
	}
	public String getDisk_count() {
		return Disk_count;
	}
	public void setDisk_count(String disk_count) {
		Disk_count = disk_count;
	}
	public String getLocks_count() {
		return Locks_count;
	}
	public void setLocks_count(String locks_count) {
		Locks_count = locks_count;
	}
	public String getXact_count() {
		return Xact_count;
	}
	public void setXact_count(String xact_count) {
		Xact_count = xact_count;
	}
	public String getTotal_dataCache() {
		return Total_dataCache;
	}
	public void setTotal_dataCache(String total_dataCache) {
		Total_dataCache = total_dataCache;
	}
	public String getTotal_physicalMemory() {
		return Total_physicalMemory;
	}
	public void setTotal_physicalMemory(String total_physicalMemory) {
		Total_physicalMemory = total_physicalMemory;
	}
	public String getMetadata_cache() {
		return Metadata_cache;
	}
	public void setMetadata_cache(String metadata_cache) {
		Metadata_cache = metadata_cache;
	}
	public String getProcedure_cache() {
		return Procedure_cache;
	}
	public void setProcedure_cache(String procedure_cache) {
		Procedure_cache = procedure_cache;
	}
	public String getTotal_logicalMemory() {
		return Total_logicalMemory;
	}
	public void setTotal_logicalMemory(String total_logicalMemory) {
		Total_logicalMemory = total_logicalMemory;
	}
	public String getData_hitrate() {
		return Data_hitrate;
	}
	public void setData_hitrate(String data_hitrate) {
		Data_hitrate = data_hitrate;
	}
	public String getProcedure_hitrate() {
		return Procedure_hitrate;
	}
	public void setProcedure_hitrate(String procedure_hitrate) {
		Procedure_hitrate = procedure_hitrate;
	}
	public String getLong_locks_count() {
		return long_locks_count;
	}
	public void setLong_locks_count(String long_locks_count) {
		this.long_locks_count = long_locks_count;
	}
	public String getOfflineengine() {
		return offlineengine;
	}
	public void setOfflineengine(String offlineengine) {
		this.offlineengine = offlineengine;
	}
	public List getEngineInfo() {
		return engineInfo;
	}
	public void setEngineInfo(List engineInfo) {
		this.engineInfo = engineInfo;
	}
	public String getMaxconn() {
		return maxconn;
	}
	public void setMaxconn(String maxconn) {
		this.maxconn = maxconn;
	}
	public String getUsedconn() {
		return usedconn;
	}
	public void setUsedconn(String usedconn) {
		this.usedconn = usedconn;
	}
	public List getProcessInfo() {
		return processInfo;
	}
	public void setProcessInfo(List processInfo) {
		this.processInfo = processInfo;
	}
	public String getProcesscount() {
		return processcount;
	}
	public void setProcesscount(String processcount) {
		this.processcount = processcount;
	}
	public String getSleepingproccount() {
		return sleepingproccount;
	}
	public void setSleepingproccount(String sleepingproccount) {
		this.sleepingproccount = sleepingproccount;
	}
	public String getMaxlock() {
		return maxlock;
	}
	public void setMaxlock(String maxlock) {
		this.maxlock = maxlock;
	}
	public String getLongtrans() {
		return longtrans;
	}
	public void setLongtrans(String longtrans) {
		this.longtrans = longtrans;
	}
	public List getLongtransInfo() {
		return longtransInfo;
	}
	public void setLongtransInfo(List longtransInfo) {
		this.longtransInfo = longtransInfo;
	}
	public String getMaxPageSize() {
		return maxPageSize;
	}
	public void setMaxPageSize(String maxPageSize) {
		this.maxPageSize = maxPageSize;
	}
	public String getTotallog() {
		return totallog;
	}
	public void setTotallog(String totallog) {
		this.totallog = totallog;
	}
	public String getFreelog() {
		return freelog;
	}
	public void setFreelog(String freelog) {
		this.freelog = freelog;
	}
	public String getUsedlog() {
		return usedlog;
	}
	public void setUsedlog(String usedlog) {
		this.usedlog = usedlog;
	}
	public String getReservedlog() {
		return reservedlog;
	}
	public void setReservedlog(String reservedlog) {
		this.reservedlog = reservedlog;
	}
	public String getLogusedperc() {
		return logusedperc;
	}
	public void setLogusedperc(String logusedperc) {
		this.logusedperc = logusedperc;
	}
	public Hashtable getSeglogInfo() {
		return seglogInfo;
	}
	public void setSeglogInfo(Hashtable seglogInfo) {
		this.seglogInfo = seglogInfo;
	}
	public String getMaxsegusedperc() {
		return maxsegusedperc;
	}
	public void setMaxsegusedperc(String maxsegusedperc) {
		this.maxsegusedperc = maxsegusedperc;
	}
	public List getDbsInfo() {
		return dbsInfo;
	}
	public void setDbsInfo(List dbsInfo) {
		this.dbsInfo = dbsInfo;
	}
	public String getCurdate() {
		return curdate;
	}
	public void setCurdate(String curdate) {
		this.curdate = curdate;
	}
	public String getBoottime() {
		return boottime;
	}
	public void setBoottime(String boottime) {
		this.boottime = boottime;
	}
	
	
}
