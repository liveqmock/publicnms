package com.afunms.portscan.util;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtil;
import com.afunms.config.dao.PortconfigDao;
import com.afunms.portscan.dao.PortScanDao;
import com.afunms.portscan.model.PortConfig;
import com.afunms.portscan.model.PortScanConfig;
import com.afunms.sysset.dao.ServiceDao;
import com.afunms.sysset.model.Service;

public class PortScanUtil{
	
	private static PortScanUtil instance;
	
	private static Hashtable data;
	
	
	/**
	 * 
	 */
	private PortScanUtil() {
		// TODO Auto-generated constructor stub
		
	}

	/**
	 * 
	 */
	public static PortScanUtil getInstance(){
		if(instance == null){
			instance = new PortScanUtil();
		}
		if(data == null){
			data = new Hashtable();
		}
		return instance;
	}
	
	public static boolean init(List ipaddressList){
		boolean result = true;
		for(int i = 0 ; i < ipaddressList.size() ; i++){
			boolean result_init = init((String)ipaddressList.get(i));
			if(!result_init){
				result = false;
			}
		}
		return result;
	}
	
		
	public static boolean init(String ipaddress){
		boolean result = false;
		try {
			
			List list = null;
			PortScanDao portScanDao = new PortScanDao();
			try {
				list = portScanDao.findByIpaddress(ipaddress);
			} catch (RuntimeException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} finally{
				portScanDao.close();
			}
			if(list == null || list.size() == 0){
				
				list = new ArrayList();
				
				List serviceList = null;
				
				ServiceDao serviceDao = new ServiceDao();
				try {
					serviceList = serviceDao.loadAll();
				} catch (RuntimeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally{
					serviceDao.close();
				}
				
				if(serviceList != null && serviceList.size() > 0){
					for(int i = 0 ; i < serviceList.size() ; i ++){
						Service service = (Service)serviceList.get(i);
						int port = service.getPort();
						int timeout = service.getTimeOut();
						String description = service.getService();
						
						PortConfig portConfig = new PortConfig();
						
						portConfig.setIpaddress(ipaddress);
						portConfig.setPortName(description);
						portConfig.setDescription(description);
						portConfig.setIsScanned("0");
						portConfig.setPort(String.valueOf(port));
						portConfig.setStatus("0");
						portConfig.setTimeout(String.valueOf(timeout));
						list.add(portConfig);
					}
				}
				
				portScanDao = new PortScanDao();
				try {
					portScanDao.saveBatch(list);
				} catch (RuntimeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally{
					portScanDao.close();
				}
				
			}
			
			
			PortScanConfig portScanConfig = new PortScanConfig();
			portScanConfig.setIpaddress(ipaddress);
			portScanConfig.setStatus("0");
				
			portScanConfig.setUnScannedList(list);
				
			data.put(ipaddress, portScanConfig);
			result = true;
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	
	public static void scan(String ipaddress){
		PortScanThread portScanThread = new PortScanThread(ipaddress);
		Thread thread = new Thread(portScanThread);
		thread.start();
	}
	

	public static Hashtable getData() {
		return data;
	}

	public static void setData(Hashtable data) {
		PortScanUtil.data = data;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}



