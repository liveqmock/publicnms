package com.afunms.detail.service.serviceInfo;

import java.util.List;

import com.afunms.temp.dao.OthersTempDao;
import com.afunms.temp.dao.ServiceTempDao;
import com.afunms.temp.model.ServiceNodeTemp;


public class ServiceInfoService {
	
	private String type;
	
	private String subtype;
	
	private String nodeid;
	

	/**
	 * @param type
	 * @param subtype
	 * @param nodeid
	 */
	public ServiceInfoService(String nodeid, String type, String subtype) {
		this.nodeid = nodeid;
		this.type = type;
		this.subtype = subtype;
	}
	
	public List<ServiceNodeTemp> getCurrServiceInfo(){
		ServiceTempDao serviceTempDao = new ServiceTempDao();
		List<ServiceNodeTemp> list = null;
		try {
			list = serviceTempDao.getNodeTempList(nodeid, type, subtype);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			serviceTempDao.close();
		}
		return list;
	}
	
	/**
	 * <p>得到servicelist</p> 
	 * <p>表nms_other_data_temp</p>
	 * <p>windows wmi</p>
	 * @return
	 */
	public List getServicelistInfo(){
		ServiceTempDao serviceTempDao = null;
		List retList = null;
		try{
			serviceTempDao = new ServiceTempDao();
			retList = serviceTempDao.getServicelistInfo(nodeid, type, subtype);
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			serviceTempDao.close();
		}
		return retList;
	}
	
	/**
	 * 得到该设备的所有字段信息组成的服务列表
	 * <p>windows snmp</p>
	 * @return
	 */
	public List getServicelistInfoAll(){
		ServiceTempDao serviceTempDao = null;
		List retList = null;
		try{
			serviceTempDao = new ServiceTempDao();
			retList = serviceTempDao.getServicelistInfoAll(nodeid, type, subtype);
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			serviceTempDao.close();
		}
		return retList;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
