package com.afunms.device.util;

import java.util.List;

import com.afunms.device.dao.ExternalDeviceDao;
import com.afunms.device.model.ExternalDevice;

public class DeviceUtil {
	public ExternalDevice getDeviceById(String id){
	ExternalDeviceDao dao=null;
	ExternalDevice device=null;
	try {
		dao=new ExternalDeviceDao();
		device=(ExternalDevice) dao.findByID(id);
	} catch (Exception e) {
		e.printStackTrace();
	}finally{
		if (dao!=null) {
			dao.close();
		}
	}
	if(device==null)device=new ExternalDevice();
	return device;
}
	public List<ExternalDevice> getAllDevice(){
		ExternalDeviceDao dao=null;
		ExternalDevice device=null;
		List list=null;
		try {
			dao=new ExternalDeviceDao();
			list=(List) dao.loadAll();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (dao!=null) {
				dao.close();
			}
		}
		return list;
	}
}
