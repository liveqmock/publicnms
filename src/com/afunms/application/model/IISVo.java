/**
 * <p>Description:mapping app_db_node</p>
 * <p>Company: dhcc.com</p>
 * @author miiwill
 * @project afunms
 * @date 2007-1-7
 */

package com.afunms.application.model;

import com.afunms.common.base.BaseVo;

public class IISVo {
	private String totalBytesSentHighWord= "";
	private String totalBytesSentLowWord= "";
	private String totalBytesReceivedHighWord= "";
	private String totalBytesReceivedLowWord = "";
	
	private String totalFilesSent = "";
	private String totalFilesReceived = "";
	private String currentAnonymousUsers = "";
	private String totalAnonymousUsers = "";
	
	private String maxAnonymousUsers = "";
	private String currentConnections = "";
	private String maxConnections = "";
	private String connectionAttempts = "";
	
	private String logonAttempts = "";
	private String totalGets = "";
	private String totalPosts = "";
	private String totalNotFoundErrors = "";
	
	public IISVo(){
		totalBytesSentHighWord= "";
		totalBytesSentLowWord= "";
		totalBytesReceivedHighWord= "";
		totalBytesReceivedLowWord = "";
		totalFilesSent = "";
		totalFilesReceived = "";
		currentAnonymousUsers = "";
		totalAnonymousUsers = "";
		maxAnonymousUsers = "";
		currentConnections = "";
		maxConnections = "";
		connectionAttempts = "";
		logonAttempts = "";
		totalGets = "";
		totalPosts = "";
		totalNotFoundErrors = "";		
	}
	public String getTotalBytesSentHighWord() {
		return totalBytesSentHighWord;
	}
	public void setTotalBytesSentHighWord(String totalBytesSentHighWord) {
		this.totalBytesSentHighWord = totalBytesSentHighWord;
	}
	public String getTotalBytesSentLowWord() {
		return totalBytesSentLowWord;
	}
	public void setTotalBytesSentLowWord(String totalBytesSentLowWord) {
		this.totalBytesSentLowWord = totalBytesSentLowWord;
	}
	public String getTotalBytesReceivedHighWord() {
		return totalBytesReceivedHighWord;
	}
	public void setTotalBytesReceivedHighWord(String totalBytesReceivedHighWord) {
		this.totalBytesReceivedHighWord = totalBytesReceivedHighWord;
	}
	public String getTotalBytesReceivedLowWord() {
		return totalBytesReceivedLowWord;
	}
	public void setTotalBytesReceivedLowWord(String totalBytesReceivedLowWord) {
		this.totalBytesReceivedLowWord = totalBytesReceivedLowWord;
	}
	
	
	public String getTotalFilesSent() {
		return totalFilesSent;
	}
	public void setTotalFilesSent(String totalFilesSent) {
		this.totalFilesSent = totalFilesSent;
	}
	public String getTotalFilesReceived() {
		return totalFilesReceived;
	}
	public void setTotalFilesReceived(String totalFilesReceived) {
		this.totalFilesReceived = totalFilesReceived;
	}
	public String getCurrentAnonymousUsers() {
		return currentAnonymousUsers;
	}
	public void setCurrentAnonymousUsers(String currentAnonymousUsers) {
		this.currentAnonymousUsers = currentAnonymousUsers;
	}
	public String getTotalAnonymousUsers() {
		return totalAnonymousUsers;
	}
	public void setTotalAnonymousUsers(String totalAnonymousUsers) {
		this.totalAnonymousUsers = totalAnonymousUsers;
	}
	
	public String getMaxAnonymousUsers() {
		return maxAnonymousUsers;
	}
	public void setMaxAnonymousUsers(String maxAnonymousUsers) {
		this.maxAnonymousUsers = maxAnonymousUsers;
	}
	public String getCurrentConnections() {
		return currentConnections;
	}
	public void setCurrentConnections(String currentConnections) {
		this.currentConnections = currentConnections;
	}
	public String getMaxConnections() {
		return maxConnections;
	}
	public void setMaxConnections(String maxConnections) {
		this.maxConnections = maxConnections;
	}
	public String getConnectionAttempts() {
		return connectionAttempts;
	}
	public void setConnectionAttempts(String connectionAttempts) {
		this.connectionAttempts = connectionAttempts;
	}
	
	public String getLogonAttempts() {
		return logonAttempts;
	}
	public void setLogonAttempts(String logonAttempts) {
		this.logonAttempts = logonAttempts;
	}
	public String getTotalGets() {
		return totalGets;
	}
	public void setTotalGets(String totalGets) {
		this.totalGets = totalGets;
	}
	public String getTotalPosts() {
		return totalPosts;
	}
	public void setTotalPosts(String totalPosts) {
		this.totalPosts = totalPosts;
	}
	public String getTotalNotFoundErrors() {
		return totalNotFoundErrors;
	}
	public void setTotalNotFoundErrors(String totalNotFoundErrors) {
		this.totalNotFoundErrors = totalNotFoundErrors;
	}

}