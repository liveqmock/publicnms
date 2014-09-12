/**
 * <p>Description:mapping topo_tomcat_node</p>
 * <p>Company: afunms</p>
 * @author miiwill
 * @project afunms
 * @date 2006-12-06
 */

package com.afunms.application.model;

import com.afunms.common.base.BaseVo;

public class TomcatPre extends BaseVo
{
	private int nodeid;
	private String MaxThread ;
	private String MinSThread ;
	private String MaxSThread;
	private String CurCount ;
	private String CurThBusy ;
	private String MaxProTime ;
	private String ProTime;
	private String RequestCount;
	private String ErrorCount;
	private String BytesReceived ;
	private String BytesSent ;
	public int getNodeid() {
		return nodeid;
	}
	public void setNodeid(int nodeid) {
		this.nodeid = nodeid;
	}
	public String getMaxThread() {
		return MaxThread;
	}
	public void setMaxThread(String maxThread) {
		MaxThread = maxThread;
	}
	public String getMinSThread() {
		return MinSThread;
	}
	public void setMinSThread(String minSThread) {
		MinSThread = minSThread;
	}
	public String getMaxSThread() {
		return MaxSThread;
	}
	public void setMaxSThread(String maxSThread) {
		MaxSThread = maxSThread;
	}
	public String getCurCount() {
		return CurCount;
	}
	public void setCurCount(String curCount) {
		CurCount = curCount;
	}
	public String getCurThBusy() {
		return CurThBusy;
	}
	public void setCurThBusy(String curThBusy) {
		CurThBusy = curThBusy;
	}
	public String getMaxProTime() {
		return MaxProTime;
	}
	public void setMaxProTime(String maxProTime) {
		MaxProTime = maxProTime;
	}
	public String getProTime() {
		return ProTime;
	}
	public void setProTime(String proTime) {
		ProTime = proTime;
	}
	public String getRequestCount() {
		return RequestCount;
	}
	public void setRequestCount(String requestCount) {
		RequestCount = requestCount;
	}
	public String getErrorCount() {
		return ErrorCount;
	}
	public void setErrorCount(String errorCount) {
		ErrorCount = errorCount;
	}
	public String getBytesReceived() {
		return BytesReceived;
	}
	public void setBytesReceived(String bytesReceived) {
		BytesReceived = bytesReceived;
	}
	public String getBytesSent() {
		return BytesSent;
	}
	public void setBytesSent(String bytesSent) {
		BytesSent = bytesSent;
	}

}
