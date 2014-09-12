package com.afunms.application.model;

import java.util.List;

import com.afunms.common.base.BaseVo;

/**
 * @author HONGLI E-mail: ty564457881@163.com
 * @version 创建时间：Oct 9, 2011 5:06:28 PM 
 * 类说明 页面展现服务器应用的model类
 */
public class HostApply extends BaseVo{
	
	private String oracleName;
	private String oracleStatus;
	private boolean oracleIsShow;
	private String sqlserverName;
	private String sqlserverStatus;
	private boolean sqlserverIsShow;
	private String db2Name;
	private boolean db2IsShow;
	private String db2Status;
	private boolean sybaseIsShow;
	private String sybaseName;
	private String sybaseStatus;
	private String mysqlName;
	private String mysqlStatus;
	private boolean mysqlIsShow;
	private String informixName;
	private String informixStatus;
	private boolean informixIsShow;
	private String ftpName;
	private String ftpStatus;
	private boolean ftpIsShow;
	private String emailName;
	private String emailStatus;
	private boolean emailShow;
	private String mqName;
	private String mqStatus;
	private boolean mqIsShow;
	private String dominoName;
	private String dominoStatus;
	private boolean dominoIsShow;
	private String wasName;
	private String wasStatus;
	private boolean wasIsShow;
	private String weblogicName;
	private String weblogicStatus;
	private boolean weblogicIsShow;
	private String tomcatName;
	private String tomcatStatus;
	private boolean tomcatIsShow;
	private String iisName;
	private String iisStatus;
	private boolean iisIsShow;
	private String cicsName;
	private String cicsStatus;
	private boolean cicsIsShow;
	private String dnsName;
	private String dnsStatus;
	private boolean dnsIsShow;
	private String jbossName;
	private String jbossStatus;
	private boolean jbossIsShow;
	private String apacheName;
	private String apacheStatus;
	private boolean apacheIsShow;
	private String tuxedoName;
	private String tuxedoStatus;
	private boolean tuxedoIsShow;
	

	public boolean isOracleIsShow() {
		return oracleIsShow;
	}

	public void setOracleIsShow(boolean oracleIsShow) {
		this.oracleIsShow = oracleIsShow;
	}

	public boolean isSqlserverIsShow() {
		return sqlserverIsShow;
	}

	public void setSqlserverIsShow(boolean sqlserverIsShow) {
		this.sqlserverIsShow = sqlserverIsShow;
	}

	public boolean isDb2IsShow() {
		return db2IsShow;
	}

	public void setDb2IsShow(boolean db2IsShow) {
		this.db2IsShow = db2IsShow;
	}

	public boolean isSybaseIsShow() {
		return sybaseIsShow;
	}

	public void setSybaseIsShow(boolean sybaseIsShow) {
		this.sybaseIsShow = sybaseIsShow;
	}

	public boolean isMysqlIsShow() {
		return mysqlIsShow;
	}

	public void setMysqlIsShow(boolean mysqlIsShow) {
		this.mysqlIsShow = mysqlIsShow;
	}

	public boolean isInformixIsShow() {
		return informixIsShow;
	}

	public void setInformixIsShow(boolean informixIsShow) {
		this.informixIsShow = informixIsShow;
	}

	public boolean isFtpIsShow() {
		return ftpIsShow;
	}

	public void setFtpIsShow(boolean ftpIsShow) {
		this.ftpIsShow = ftpIsShow;
	}

	public boolean isEmailShow() {
		return emailShow;
	}

	public void setEmailShow(boolean emailShow) {
		this.emailShow = emailShow;
	}

	public boolean isMqIsShow() {
		return mqIsShow;
	}

	public void setMqIsShow(boolean mqIsShow) {
		this.mqIsShow = mqIsShow;
	}

	public boolean isDominoIsShow() {
		return dominoIsShow;
	}

	public void setDominoIsShow(boolean dominoIsShow) {
		this.dominoIsShow = dominoIsShow;
	}

	public boolean isWasIsShow() {
		return wasIsShow;
	}

	public void setWasIsShow(boolean wasIsShow) {
		this.wasIsShow = wasIsShow;
	}

	public boolean isWeblogicIsShow() {
		return weblogicIsShow;
	}

	public void setWeblogicIsShow(boolean weblogicIsShow) {
		this.weblogicIsShow = weblogicIsShow;
	}

	public boolean isTomcatIsShow() {
		return tomcatIsShow;
	}

	public void setTomcatIsShow(boolean tomcatIsShow) {
		this.tomcatIsShow = tomcatIsShow;
	}

	public boolean isIisIsShow() {
		return iisIsShow;
	}

	public void setIisIsShow(boolean iisIsShow) {
		this.iisIsShow = iisIsShow;
	}

	public boolean isCicsIsShow() {
		return cicsIsShow;
	}

	public void setCicsIsShow(boolean cicsIsShow) {
		this.cicsIsShow = cicsIsShow;
	}

	public boolean isDnsIsShow() {
		return dnsIsShow;
	}

	public void setDnsIsShow(boolean dnsIsShow) {
		this.dnsIsShow = dnsIsShow;
	}

	public boolean isJbossIsShow() {
		return jbossIsShow;
	}

	public void setJbossIsShow(boolean jbossIsShow) {
		this.jbossIsShow = jbossIsShow;
	}

	public boolean isApacheIsShow() {
		return apacheIsShow;
	}

	public void setApacheIsShow(boolean apacheIsShow) {
		this.apacheIsShow = apacheIsShow;
	}

	public boolean isTuxedoIsShow() {
		return tuxedoIsShow;
	}

	public void setTuxedoIsShow(boolean tuxedoIsShow) {
		this.tuxedoIsShow = tuxedoIsShow;
	}

	public String getOracleName() {
		return oracleName;
	}

	public void setOracleName(String oracleName) {
		this.oracleName = oracleName;
	}

	public String getOracleStatus() {
		return oracleStatus;
	}

	public void setOracleStatus(String oracleStatus) {
		this.oracleStatus = oracleStatus;
	}

	public String getSqlserverName() {
		return sqlserverName;
	}

	public void setSqlserverName(String sqlserverName) {
		this.sqlserverName = sqlserverName;
	}

	public String getSqlserverStatus() {
		return sqlserverStatus;
	}

	public void setSqlserverStatus(String sqlserverStatus) {
		this.sqlserverStatus = sqlserverStatus;
	}

	public String getDb2Name() {
		return db2Name;
	}

	public void setDb2Name(String db2Name) {
		this.db2Name = db2Name;
	}

	public String getDb2Status() {
		return db2Status;
	}

	public void setDb2Status(String db2Status) {
		this.db2Status = db2Status;
	}

	public String getSybaseName() {
		return sybaseName;
	}

	public void setSybaseName(String sybaseName) {
		this.sybaseName = sybaseName;
	}

	public String getSybaseStatus() {
		return sybaseStatus;
	}

	public void setSybaseStatus(String sybaseStatus) {
		this.sybaseStatus = sybaseStatus;
	}

	public String getMysqlName() {
		return mysqlName;
	}

	public void setMysqlName(String mysqlName) {
		this.mysqlName = mysqlName;
	}

	public String getMysqlStatus() {
		return mysqlStatus;
	}

	public void setMysqlStatus(String mysqlStatus) {
		this.mysqlStatus = mysqlStatus;
	}

	public String getInformixName() {
		return informixName;
	}

	public void setInformixName(String informixName) {
		this.informixName = informixName;
	}

	public String getInformixStatus() {
		return informixStatus;
	}

	public void setInformixStatus(String informixStatus) {
		this.informixStatus = informixStatus;
	}

	public String getFtpName() {
		return ftpName;
	}

	public void setFtpName(String ftpName) {
		this.ftpName = ftpName;
	}

	public String getFtpStatus() {
		return ftpStatus;
	}

	public void setFtpStatus(String ftpStatus) {
		this.ftpStatus = ftpStatus;
	}

	public String getEmailName() {
		return emailName;
	}

	public void setEmailName(String emailName) {
		this.emailName = emailName;
	}

	public String getEmailStatus() {
		return emailStatus;
	}

	public void setEmailStatus(String emailStatus) {
		this.emailStatus = emailStatus;
	}

	public String getMqName() {
		return mqName;
	}

	public void setMqName(String mqName) {
		this.mqName = mqName;
	}

	public String getMqStatus() {
		return mqStatus;
	}

	public void setMqStatus(String mqStatus) {
		this.mqStatus = mqStatus;
	}

	public String getDominoName() {
		return dominoName;
	}

	public void setDominoName(String dominoName) {
		this.dominoName = dominoName;
	}

	public String getDominoStatus() {
		return dominoStatus;
	}

	public void setDominoStatus(String dominoStatus) {
		this.dominoStatus = dominoStatus;
	}

	public String getWasName() {
		return wasName;
	}

	public void setWasName(String wasName) {
		this.wasName = wasName;
	}

	public String getWasStatus() {
		return wasStatus;
	}

	public void setWasStatus(String wasStatus) {
		this.wasStatus = wasStatus;
	}

	public String getWeblogicName() {
		return weblogicName;
	}

	public void setWeblogicName(String weblogicName) {
		this.weblogicName = weblogicName;
	}

	public String getWeblogicStatus() {
		return weblogicStatus;
	}

	public void setWeblogicStatus(String weblogicStatus) {
		this.weblogicStatus = weblogicStatus;
	}

	public String getTomcatName() {
		return tomcatName;
	}

	public void setTomcatName(String tomcatName) {
		this.tomcatName = tomcatName;
	}

	public String getTomcatStatus() {
		return tomcatStatus;
	}

	public void setTomcatStatus(String tomcatStatus) {
		this.tomcatStatus= tomcatStatus;
	}

	public String getIisName() {
		return iisName;
	}

	public void setIisName(String iisName) {
		this.iisName = iisName;
	}

	public String getIisStatus() {
		return iisStatus;
	}

	public void setIisStatus(String iisStatus) {
		this.iisStatus = iisStatus;
	}

	public String getCicsName() {
		return cicsName;
	}

	public void setCicsName(String cicsName) {
		this.cicsName = cicsName;
	}

	public String getCicsStatus() {
		return cicsStatus;
	}

	public void setCicsStatus(String cicsStatus) {
		this.cicsStatus = cicsStatus;
	}

	public String getDnsName() {
		return dnsName;
	}

	public void setDnsName(String dnsName) {
		this.dnsName = dnsName;
	}

	public String getDnsStatus() {
		return dnsStatus;
	}

	public void setDnsStatus(String dnsStatus) {
		this.dnsStatus = dnsStatus;
	}

	public String getJbossName() {
		return jbossName;
	}

	public void setJbossName(String jbossName) {
		this.jbossName = jbossName;
	}

	public String getJbossStatus() {
		return jbossStatus;
	}

	public void setJbossStatus(String jbossStatus) {
		this.jbossStatus = jbossStatus;
	}

	public String getApacheName() {
		return apacheName;
	}

	public void setApacheName(String apacheName) {
		this.apacheName = apacheName;
	}

	public String getApacheStatus() {
		return apacheStatus;
	}

	public void setApacheStatus(String apacheStatus) {
		this.apacheStatus = apacheStatus;
	}

	public String getTuxedoName() {
		return tuxedoName;
	}

	public void setTuxedoName(String tuxedoName) {
		this.tuxedoName = tuxedoName;
	}

	public String getTuxedoStatus() {
		return tuxedoStatus;
	}

	public void setTuxedoStatus(String tuxedoStatus) {
		this.tuxedoStatus = tuxedoStatus;
	}
}
