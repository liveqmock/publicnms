package com.afunms.event.model;

import com.afunms.common.base.BaseVo;
/**
 * @author Administrator
 *
 */
public class NetSyslogImpEvent extends BaseVo {
	private int loginSuccess;//登录成功的用户
	private int logoutSuccess;//登出成功的用户
	private int loginFailure;//登录失败的用户
	private int clearLog;//清除审计日志
	private int strategyModified;//审计策略变更
	private int accoutModified;//用户帐号变更
	private int accoutLocked;//锁定的用户帐号
	private int sceCli;//SceCli组策略
	public int getLoginSuccess() {
		return loginSuccess;
	}
	public void setLoginSuccess(int loginSuccess) {
		this.loginSuccess = loginSuccess;
	}
	public int getLogoutSuccess() {
		return logoutSuccess;
	}
	public void setLogoutSuccess(int logoutSuccess) {
		this.logoutSuccess = logoutSuccess;
	}
	public int getLoginFailure() {
		return loginFailure;
	}
	public void setLoginFailure(int loginFail) {
		this.loginFailure = loginFail;
	}
	public int getClearLog() {
		return clearLog;
	}
	public void setClearLog(int cleareLog) {
		this.clearLog = cleareLog;
	}
	public int getStrategyModified() {
		return strategyModified;
	}
	public void setStrategyModified(int strategyModified) {
		this.strategyModified = strategyModified;
	}
	public int getAccoutModified() {
		return accoutModified;
	}
	public void setAccoutModified(int accoutModified) {
		this.accoutModified = accoutModified;
	}
	public int getAccoutLocked() {
		return accoutLocked;
	}
	public void setAccoutLocked(int accoutLocked) {
		this.accoutLocked = accoutLocked;
	}
	public int getSceCli() {
		return sceCli;
	}
	public void setSceCli(int sceCli) {
		this.sceCli = sceCli;
	}
}