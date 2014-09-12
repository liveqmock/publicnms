package com.afunms.detail.service.tomcatInfo;

import java.util.Hashtable;

import com.afunms.application.dao.TomcatDao;

public class TomcatInfoService {
	
	
	/**
	 * <p>从数据库中获取tomcat的采集信息</p>
	 * @param nodeid
	 * @return
	 */
	public Hashtable getTomcatDataHashtable(String nodeid) {
		Hashtable retHashtable = null;
		TomcatDao tomcatDao = new TomcatDao();
		try{
			retHashtable = tomcatDao.getTomcatDataHashtable(nodeid);
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			tomcatDao.close();
		}
		return retHashtable;
	}
}
