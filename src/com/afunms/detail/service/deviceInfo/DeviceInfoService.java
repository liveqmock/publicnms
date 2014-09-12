package com.afunms.detail.service.deviceInfo;

import java.util.List;

import com.afunms.temp.dao.DeviceTempDao;
import com.afunms.temp.model.DeviceNodeTemp;


public class DeviceInfoService {
	
	private String type;
	
	private String subtype;
	
	private String nodeid;
	

	/**
	 * @param type
	 * @param subtype
	 * @param nodeid
	 */
	public DeviceInfoService(String nodeid, String type, String subtype) {
		this.nodeid = nodeid;
		this.type = type;
		this.subtype = subtype;
	}
	
	
	public List<DeviceNodeTemp> getCurrDeviceInfo(){
		DeviceTempDao deviceTempDao = new DeviceTempDao();
		List<DeviceNodeTemp> list = null;
		try {
			list = deviceTempDao.getDeviceNodeTemp(nodeid, type, subtype);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			deviceTempDao.close();
		}
		return list;
	}
	
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
