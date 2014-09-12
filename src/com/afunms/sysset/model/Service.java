/**
 * <p>Description:mapping table NMS_SERVICE</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-09
 */

package com.afunms.sysset.model;

import com.afunms.common.base.BaseVo;

public class Service extends BaseVo
{ 
    private int id;
    private String service;    
    private int port;
    private int scan;
    private int timeOut;
    
	public int getTimeOut() {
		return timeOut;
	}
	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}    
	public int isScan() {
		return scan;
	}
	public void setScan(int scan) {
		this.scan = scan;
	}	
}
