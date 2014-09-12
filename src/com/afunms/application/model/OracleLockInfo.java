package com.afunms.application.model;
/**
 * @author HONGLI E-mail: ty564457881@163.com
 * @version 创建时间：May 27, 2011 1:57:06 PM
 * 类说明：oracle死锁信息Model
 */
public class OracleLockInfo {
	private String id;
	
	/**
	 * 锁等待数
	 */
	private String lockwaitcount;
	
	/**
	 * 死锁数
	 */
	private String deadlockcount;
	
	/**
	 * 当前连接数
	 */
	private String processcount;
	
	/**
	 * 最大连接数
	 */
	private String maxprocesscount;

	
	/**
	 * 当前会话总数
	 */
	private String currentsessioncount;
	
	/**
	 * 最大允许会话数量
	 */
	private String useablesessioncount;
	
	/**
	 * 可用会话数百分比
	 */
	private String useablesessionpercent;
	
	/**
	 * 等待解锁的会话数量
	 */
	private String lockdsessioncount;
	
	
	/**
	 * 提交于回滚百分比
	 */
	private String rollbackcommitpercent;
	
	/**
	 * 回滚次数
	 */
	private String rollbacks;
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRollbacks() {
		return rollbacks;
	}

	public void setRollbacks(String rollbacks) {
		this.rollbacks = rollbacks;
	}

	public String getRollbackcommitpercent() {
		return rollbackcommitpercent;
	}

	public void setRollbackcommitpercent(String rollbackcommitpercent) {
		this.rollbackcommitpercent = rollbackcommitpercent;
	}

	public String getCurrentsessioncount() {
		return currentsessioncount;
	}

	public void setCurrentsessioncount(String currentsessioncount) {
		this.currentsessioncount = currentsessioncount;
	}

	public String getUseablesessioncount() {
		return useablesessioncount;
	}

	public void setUseablesessioncount(String useablesessioncount) {
		this.useablesessioncount = useablesessioncount;
	}

	public String getLockdsessioncount() {
		return lockdsessioncount;
	}

	public void setLockdsessioncount(String lockdsessioncount) {
		this.lockdsessioncount = lockdsessioncount;
	}

	public String getUseablesessionpercent() {
		return useablesessionpercent;
	}

	public void setUseablesessionpercent(String useablesessionpercent) {
		this.useablesessionpercent = useablesessionpercent;
	}


	public String getLockwaitcount() {
		return lockwaitcount;
	}

	public void setLockwaitcount(String lockwaitcount) {
		this.lockwaitcount = lockwaitcount;
	}

	public String getDeadlockcount() {
		return deadlockcount;
	}

	public void setDeadlockcount(String deadlockcount) {
		this.deadlockcount = deadlockcount;
	}

	public String getProcesscount() {
		return processcount;
	}

	public void setProcesscount(String processcount) {
		this.processcount = processcount;
	}

	public String getMaxprocesscount() {
		return maxprocesscount;
	}

	public void setMaxprocesscount(String maxprocesscount) {
		this.maxprocesscount = maxprocesscount;
	}
}
