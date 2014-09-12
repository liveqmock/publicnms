package com.afunms.portscan.model;

import java.util.ArrayList;
import java.util.List;

public class PortScanConfig {
	
	private String id;
	
	private String ipaddress;
	
	private List isScannedList;
	
	private List unScannedList;
	
	private String timeout;
	
	private int total;
	
	/**
	 * 对此ip地址的端口扫描的状态 现在分三种：
	 * "0" 为 未进行扫描 
	 * "1" 为 扫描完成 
	 * "2" 为 扫描正在继续
	 */
	private String status; 

	/**
	 * 
	 */
	public PortScanConfig() {
		// TODO Auto-generated constructor stub
		
		isScannedList = new ArrayList();
		
		unScannedList = new ArrayList();
		
		total = isScannedList.size() + unScannedList.size();
	}

	/**
	 * @param id
	 * @param ipaddress
	 * @param isScannedList
	 * @param unScannedList
	 * @param timeout
	 * @param total
	 * @param status
	 */
	public PortScanConfig(String id, String ipaddress, List isScannedList,
			List unScannedList, String timeout, int total, String status) {
		this.id = id;
		this.ipaddress = ipaddress;
		this.isScannedList = isScannedList;
		this.unScannedList = unScannedList;
		this.timeout = timeout;
		this.total = total;
		this.status = status;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public List getIsScannedList() {
		return isScannedList;
	}

	public void setIsScannedList(List isScannedList) {
		this.isScannedList = isScannedList;
	}

	public List getUnScannedList() {
		return unScannedList;
	}

	public void setUnScannedList(List unScannedList) {
		this.unScannedList = unScannedList;
	}

	public String getTimeout() {
		return timeout;
	}

	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	
	
}
