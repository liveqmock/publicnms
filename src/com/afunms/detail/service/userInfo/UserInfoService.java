package com.afunms.detail.service.userInfo;

import java.util.List;
import java.util.Vector;

import com.afunms.temp.dao.UserTempDao;

/**
 * @author HONGLI E-mail: ty564457881@163.com
 * @version 创建时间：Mar 14, 2011 2:09:13 AM
 * 类说明
 */
public class UserInfoService {
	private String type;
	
	private String subtype;
	
	private String nodeid;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSubtype() {
		return subtype;
	}

	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}

	public String getNodeid() {
		return nodeid;
	}

	public void setNodeid(String nodeid) {
		this.nodeid = nodeid;
	}

	public UserInfoService(String nodeid, String type, String subtype) {
		super();
		this.type = type;
		this.subtype = subtype;
		this.nodeid = nodeid;
	}
	
	/**
	 * 得到用户信息
	 * @return
	 */
	public Vector getUserInfo(){
		Vector retVector = null;
		UserTempDao userTempDao = null;
		try{
			userTempDao = new UserTempDao();
			retVector = userTempDao.getUserInfo(nodeid, type, subtype);
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(userTempDao != null){
				userTempDao.close();
			}
		}
		return retVector;
	}
	
	/**
	 * <p>得到userlist 集合类型为 List<Hashtable<String,String></p>
	 * <p>windows wmi</p>
	 * @return
	 */
	public List getUserInfoList(){ 
		List retList = null;
		UserTempDao userTempDao = null;
		try{
			userTempDao = new UserTempDao();
	    	retList = userTempDao.getUserInfoList(nodeid, type, subtype);
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(userTempDao != null){
				userTempDao.close();
			}
		}
		return retList;
	}
}
