package com.afunms.application.weblogicmonitor;

public class WeblogicJdbc {
	  String jdbcConnectionPoolName = null; // 名称
	  String jdbcConnectionPoolRuntimeActiveConnectionsCurrentCount = null;//	当前活动连接数
	  String jdbcConnectionPoolRuntimeVersionJDBCDriver = null;//	连接池驱动的版本
	  String jdbcConnectionPoolRuntimeMaxCapacity = null;//	连接池最大容量
	  String jdbcConnectionPoolRuntimeActiveConnectionsAverageCount = null;//	连接池平均活动连接数
	  String jdbcConnectionPoolRuntimeHighestNumAvailable = null;//	最高可活动连接数
	  String jdbcLeaked = null;
	  String jdbcWaitMaxTime = null;
	  String jdbcWaitCurrent = null;
	  
	public String getJdbcLeaked() {
		return jdbcLeaked;
	}
	public void setJdbcLeaked(String jdbcLeaked) {
		this.jdbcLeaked = jdbcLeaked;
	}
	public String getJdbcWaitMaxTime() {
		return jdbcWaitMaxTime;
	}
	public void setJdbcWaitMaxTime(String jdbcWaitMaxTime) {
		this.jdbcWaitMaxTime = jdbcWaitMaxTime;
	}
	public String getJdbcWaitCurrent() {
		return jdbcWaitCurrent;
	}
	public void setJdbcWaitCurrent(String jdbcWaitCurrent) {
		this.jdbcWaitCurrent = jdbcWaitCurrent;
	}
	public String getJdbcConnectionPoolName() {
		return jdbcConnectionPoolName;
	}
	public void setJdbcConnectionPoolName(String jdbcConnectionPoolName) {
		this.jdbcConnectionPoolName = jdbcConnectionPoolName;
	}
	public String getJdbcConnectionPoolRuntimeActiveConnectionsCurrentCount() {
		return jdbcConnectionPoolRuntimeActiveConnectionsCurrentCount;
	}
	public void setJdbcConnectionPoolRuntimeActiveConnectionsCurrentCount(
			String jdbcConnectionPoolRuntimeActiveConnectionsCurrentCount) {
		this.jdbcConnectionPoolRuntimeActiveConnectionsCurrentCount = jdbcConnectionPoolRuntimeActiveConnectionsCurrentCount;
	}
	public String getJdbcConnectionPoolRuntimeVersionJDBCDriver() {
		return jdbcConnectionPoolRuntimeVersionJDBCDriver;
	}
	public void setJdbcConnectionPoolRuntimeVersionJDBCDriver(
			String jdbcConnectionPoolRuntimeVersionJDBCDriver) {
		this.jdbcConnectionPoolRuntimeVersionJDBCDriver = jdbcConnectionPoolRuntimeVersionJDBCDriver;
	}
	public String getJdbcConnectionPoolRuntimeMaxCapacity() {
		return jdbcConnectionPoolRuntimeMaxCapacity;
	}
	public void setJdbcConnectionPoolRuntimeMaxCapacity(
			String jdbcConnectionPoolRuntimeMaxCapacity) {
		this.jdbcConnectionPoolRuntimeMaxCapacity = jdbcConnectionPoolRuntimeMaxCapacity;
	}
	public String getJdbcConnectionPoolRuntimeActiveConnectionsAverageCount() {
		return jdbcConnectionPoolRuntimeActiveConnectionsAverageCount;
	}
	public void setJdbcConnectionPoolRuntimeActiveConnectionsAverageCount(
			String jdbcConnectionPoolRuntimeActiveConnectionsAverageCount) {
		this.jdbcConnectionPoolRuntimeActiveConnectionsAverageCount = jdbcConnectionPoolRuntimeActiveConnectionsAverageCount;
	}
	public String getJdbcConnectionPoolRuntimeHighestNumAvailable() {
		return jdbcConnectionPoolRuntimeHighestNumAvailable;
	}
	public void setJdbcConnectionPoolRuntimeHighestNumAvailable(
			String jdbcConnectionPoolRuntimeHighestNumAvailable) {
		this.jdbcConnectionPoolRuntimeHighestNumAvailable = jdbcConnectionPoolRuntimeHighestNumAvailable;
	}

}
