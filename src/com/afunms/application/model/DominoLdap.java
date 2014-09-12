/**
 * <p>Description:mapping app_db_node</p>
 * <p>Company: dhcc.com</p>
 * @author miiwill
 * @project afunms
 * @date 2007-1-7
 */

package com.afunms.application.model;

import com.afunms.common.base.BaseVo;

public class DominoLdap extends BaseVo{
	private String ldapRunning = "";//服务是否在运行
	private String ldapInboundQue = "";//等待处理的新连接数
	private String ldapInboundActive = "";//当前的外来连接数
	private String ldapInboundActiveSSL = "";//当前外来SSL连接数
	private String ldapInboundBytesReseived = "";//外来连接接收字节总数（Bytes）
	private String ldapInboundBytesSent = "";//外来连接发送字节总数（Bytes）
	private String ldapInboundPeak = "";//最大外来并发连接数
	private String ldapInboundPeakSSL = "";//最大外来并发 SSL 连接数
	private String ldapInboundTotal = "";//外来连接总数
	private String ldapInboundTotalSSL = "";//外来SSL连接总数
	private String ldapBadHandShake = "";//失败的外来SSL握手数
	private String ldapThreadsBusy = "";//当前繁忙线程数
	private String ldapThreadsIdle = "";//当前空闲线程数
	private String ldapThreadsInPool = "";//当前线程池中的线程数
	private String ldapTHreadsPeak = "";//最大并发线程数
	public String getLdapBadHandShake() {
		return ldapBadHandShake;
	}
	public void setLdapBadHandShake(String ldapBadHandShake) {
		this.ldapBadHandShake = ldapBadHandShake;
	}
	public String getLdapInboundActive() {
		return ldapInboundActive;
	}
	public void setLdapInboundActive(String ldapInboundActive) {
		this.ldapInboundActive = ldapInboundActive;
	}
	public String getLdapInboundActiveSSL() {
		return ldapInboundActiveSSL;
	}
	public void setLdapInboundActiveSSL(String ldapInboundActiveSSL) {
		this.ldapInboundActiveSSL = ldapInboundActiveSSL;
	}
	public String getLdapInboundBytesReseived() {
		return ldapInboundBytesReseived;
	}
	public void setLdapInboundBytesReseived(String ldapInboundBytesReseived) {
		this.ldapInboundBytesReseived = ldapInboundBytesReseived;
	}
	public String getLdapInboundBytesSent() {
		return ldapInboundBytesSent;
	}
	public void setLdapInboundBytesSent(String ldapInboundBytesSent) {
		this.ldapInboundBytesSent = ldapInboundBytesSent;
	}
	public String getLdapInboundPeak() {
		return ldapInboundPeak;
	}
	public void setLdapInboundPeak(String ldapInboundPeak) {
		this.ldapInboundPeak = ldapInboundPeak;
	}
	public String getLdapInboundPeakSSL() {
		return ldapInboundPeakSSL;
	}
	public void setLdapInboundPeakSSL(String ldapInboundPeakSSL) {
		this.ldapInboundPeakSSL = ldapInboundPeakSSL;
	}
	public String getLdapInboundQue() {
		return ldapInboundQue;
	}
	public void setLdapInboundQue(String ldapInboundQue) {
		this.ldapInboundQue = ldapInboundQue;
	}
	public String getLdapInboundTotal() {
		return ldapInboundTotal;
	}
	public void setLdapInboundTotal(String ldapInboundTotal) {
		this.ldapInboundTotal = ldapInboundTotal;
	}
	public String getLdapInboundTotalSSL() {
		return ldapInboundTotalSSL;
	}
	public void setLdapInboundTotalSSL(String ldapInboundTotalSSL) {
		this.ldapInboundTotalSSL = ldapInboundTotalSSL;
	}
	public String getLdapRunning() {
		return ldapRunning;
	}
	public void setLdapRunning(String ldapRunning) {
		this.ldapRunning = ldapRunning;
	}
	public String getLdapThreadsBusy() {
		return ldapThreadsBusy;
	}
	public void setLdapThreadsBusy(String ldapThreadsBusy) {
		this.ldapThreadsBusy = ldapThreadsBusy;
	}
	public String getLdapThreadsIdle() {
		return ldapThreadsIdle;
	}
	public void setLdapThreadsIdle(String ldapThreadsIdle) {
		this.ldapThreadsIdle = ldapThreadsIdle;
	}
	public String getLdapThreadsInPool() {
		return ldapThreadsInPool;
	}
	public void setLdapThreadsInPool(String ldapThreadsInPool) {
		this.ldapThreadsInPool = ldapThreadsInPool;
	}
	public String getLdapTHreadsPeak() {
		return ldapTHreadsPeak;
	}
	public void setLdapTHreadsPeak(String ldapTHreadsPeak) {
		this.ldapTHreadsPeak = ldapTHreadsPeak;
	}

}