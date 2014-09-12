/**
 * <p>Description:mapping app_db_node</p>
 * <p>Company: dhcc.com</p>
 * @author miiwill
 * @project afunms
 * @date 2007-1-7
 */

package com.afunms.application.model;

import com.afunms.common.base.BaseVo;

public class DominoHttp extends BaseVo{
	private String httpAccept = "";//HTTP连接请求总数
	private String httpRefused = "";//被拒绝的HTTP连接请求数
	private String httpCurrentCon = "";//当前HTTP连接数
	private String httpMaxCon = "";//最大HTTP连接数
	private String httpPeakCon = "";//峰值HTTP连接数
	private String httpWorkerRequest = "";//HTTP请求数
	private String httpWorkerRequestTime = "";//HTTP请求时间
	private String httpWorkerBytesRead = "";//Worker总计读字节
	private String httpWorkerBytesWritten = "";//Worker总计写字节
	private String httpWorkerRequestProcess = "";//Worker总计处理的请求总数
	private String httpWorkerTotalRequest = "";//Worker总计请求时间
	private String httpErrorUrl = "";//Worker总计请求时间
	
	public DominoHttp(){
		httpAccept = "";//HTTP连接请求总数
		httpRefused = "";//被拒绝的HTTP连接请求数
		httpCurrentCon = "";//当前HTTP连接数
		httpMaxCon = "";//最大HTTP连接数
		httpPeakCon = "";//峰值HTTP连接数
		httpWorkerRequest = "";//HTTP请求数
		httpWorkerRequestTime = "";//HTTP请求时间
		httpWorkerBytesRead = "";//Worker总计读字节
		httpWorkerBytesWritten = "";//Worker总计写字节
		httpWorkerRequestProcess = "";//Worker总计处理的请求总数
		httpWorkerTotalRequest = "";//Worker总计请求时间		
	}
	
	public String getHttpAccept() {
		return httpAccept;
	}
	public void setHttpAccept(String httpAccept) {
		this.httpAccept = httpAccept;
	}
	public String getHttpCurrentCon() {
		return httpCurrentCon;
	}
	public void setHttpCurrentCon(String httpCurrentCon) {
		this.httpCurrentCon = httpCurrentCon;
	}
	public String getHttpMaxCon() {
		return httpMaxCon;
	}
	public void setHttpMaxCon(String httpMaxCon) {
		this.httpMaxCon = httpMaxCon;
	}
	public String getHttpPeakCon() {
		return httpPeakCon;
	}
	public void setHttpPeakCon(String httpPeakCon) {
		this.httpPeakCon = httpPeakCon;
	}
	public String getHttpRefused() {
		return httpRefused;
	}
	public void setHttpRefused(String httpRefused) {
		this.httpRefused = httpRefused;
	}
	public String getHttpWorkerBytesRead() {
		return httpWorkerBytesRead;
	}
	public void setHttpWorkerBytesRead(String httpWorkerBytesRead) {
		this.httpWorkerBytesRead = httpWorkerBytesRead;
	}
	public String getHttpWorkerBytesWritten() {
		return httpWorkerBytesWritten;
	}
	public void setHttpWorkerBytesWritten(String httpWorkerBytesWritten) {
		this.httpWorkerBytesWritten = httpWorkerBytesWritten;
	}
	public String getHttpWorkerRequest() {
		return httpWorkerRequest;
	}
	public void setHttpWorkerRequest(String httpWorkerRequest) {
		this.httpWorkerRequest = httpWorkerRequest;
	}
	public String getHttpWorkerRequestProcess() {
		return httpWorkerRequestProcess;
	}
	public void setHttpWorkerRequestProcess(String httpWorkerRequestProcess) {
		this.httpWorkerRequestProcess = httpWorkerRequestProcess;
	}
	public String getHttpWorkerRequestTime() {
		return httpWorkerRequestTime;
	}
	public void setHttpWorkerRequestTime(String httpWorkerRequestTime) {
		this.httpWorkerRequestTime = httpWorkerRequestTime;
	}
	public String getHttpWorkerTotalRequest() {
		return httpWorkerTotalRequest;
	}
	public void setHttpWorkerTotalRequest(String httpWorkerTotalRequest) {
		this.httpWorkerTotalRequest = httpWorkerTotalRequest;
	}

	public String getHttpErrorUrl() {
		return httpErrorUrl;
	}

	public void setHttpErrorUrl(String httpErrorUrl) {
		this.httpErrorUrl = httpErrorUrl;
	}

}