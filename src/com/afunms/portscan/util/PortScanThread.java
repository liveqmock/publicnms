package com.afunms.portscan.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import com.afunms.common.util.SocketService;
import com.afunms.portscan.model.PortConfig;
import com.afunms.portscan.model.PortScanConfig;

public class PortScanThread implements Runnable{
	
	private String ipaddress;
	

	/**
	 * 
	 */
	public PortScanThread() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param ipaddress
	 */
	public PortScanThread(String ipaddress) {
		this.ipaddress = ipaddress;
	}
	
	

	public String getIpaddress() {
		return ipaddress;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public void run() {
		// TODO Auto-generated method stub
		PortScanUtil portScanUtil= PortScanUtil.getInstance();
		Hashtable allHashtable = portScanUtil.getData();
		PortScanConfig portScanConfig= (PortScanConfig)allHashtable.get(ipaddress);
		List isScannedList = portScanConfig.getIsScannedList();
		List unScannedList = portScanConfig.getUnScannedList();
		
		String status = portScanConfig.getStatus();
		synchronized (unScannedList) {
			
		if("0".equals(status)){
			portScanConfig.setStatus("2");
			while(true){
					if (unScannedList == null || unScannedList.size() == 0) {
						break;
					}
					PortConfig portconfig = (PortConfig) unScannedList.get(0);
					scan(portconfig);
					isScannedList.add(portconfig);
					unScannedList.remove(0);
			}
			
			portScanConfig.setStatus("1");
		}
		}
		
	}
	
	private boolean scan(PortConfig portconfig){
		boolean result = false;
		try {
			String ipAddress = portconfig.getIpaddress();
			String port = portconfig.getPort();
			String timeout = portconfig.getTimeout();
			boolean isconnected = SocketService.checkService(ipAddress, Integer.parseInt(port), Integer.parseInt(timeout));
			if(isconnected){
				portconfig.setStatus("1");
			}else{
				portconfig.setStatus("0");
			}
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			Date date = new Date();
			String scantime = simpleDateFormat.format(date);
			portconfig.setScantime(scantime);
			portconfig.setIsScanned("1");
			
			result = true;
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return result;
	}
}
