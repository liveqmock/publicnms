package com.afunms.detail.service.configInfo;

import java.util.List;

import com.afunms.detail.reomte.model.UserConfigInfo;
import com.afunms.temp.dao.UserTempDao;

public class UserConfigInfoService {
	
	private String type;
	
	private String subtype;
	
	private String nodeid;
	

	/**
	 * @param type
	 * @param subtype
	 * @param nodeid
	 */
	public UserConfigInfoService(String nodeid, String type, String subtype) {
		this.nodeid = nodeid;
		this.type = type;
		this.subtype = subtype;
	}
	
	
	public List<UserConfigInfo> getCurrUserConfigInfo(){
		return getUserConfigInfo(null);
	}
	
	public List<UserConfigInfo> getUserConfigInfo(String[] subentities){
		UserTempDao userTempDao = new UserTempDao();
		List<UserConfigInfo> userConfigInfoList = null;
		try {
			userConfigInfoList = userTempDao.getUserConfigInfoList(nodeid, type, subtype, subentities);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			userTempDao.close();
		}
		return userConfigInfoList;
	}
	


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
