package com.afunms.detail.service.configInfo;

import java.util.List;

import com.afunms.detail.reomte.model.NetmediaConfigInfo;
import com.afunms.temp.dao.OthersTempDao;

public class NetmediaConfigInfoService {
	
	private String type;
	
	private String subtype;
	
	private String nodeid;
	

	/**
	 * @param type
	 * @param subtype
	 * @param nodeid
	 */
	public NetmediaConfigInfoService(String nodeid, String type, String subtype) {
		this.nodeid = nodeid;
		this.type = type;
		this.subtype = subtype;
	}
	
	
	public List<NetmediaConfigInfo> getCurrNetmediaConfigInfo(){
		return getNetmediaConfigInfo(null);
	}
	
	public List<NetmediaConfigInfo> getNetmediaConfigInfo(String[] subentities){
		OthersTempDao othersTempDao = new OthersTempDao();
		List<NetmediaConfigInfo> netmediaConfigInfoList = null;
		try {
			netmediaConfigInfoList = othersTempDao.getNetmediaConfigInfoList(nodeid, type, subtype, subentities);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			othersTempDao.close();
		}
		return netmediaConfigInfoList;
	}
	
	public List getNetmediaConfigInfo(){
		OthersTempDao othersTempDao = new OthersTempDao();
		List netmediaConfigInfoList = null;
		try {
			netmediaConfigInfoList = othersTempDao.getNetmediaConfigInfo(nodeid, type, subtype);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			othersTempDao.close();
		}
		return netmediaConfigInfoList;
	}
	


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
