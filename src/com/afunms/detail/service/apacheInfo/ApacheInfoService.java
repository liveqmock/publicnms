package com.afunms.detail.service.apacheInfo;

import java.util.Hashtable;

import com.afunms.application.dao.ApacheConfigDao;
import com.afunms.application.dao.TomcatDao;
/**
 * <p>中间件Apache Http Server service</p>
 * @author HONGLI  Mar 8, 2011
 *
 */
public class ApacheInfoService {
	
	
	/**
	 * <p>从数据库中获取Apache HttpServer的采集信息</p>
	 * @param nodeid
	 * @return
	 */
	public Hashtable getApacheDataHashtable(String nodeid) {
		Hashtable retHashtable = null;
		ApacheConfigDao apacheConfigDao = new ApacheConfigDao();
		try{
			retHashtable = apacheConfigDao.getApacheDataHashtable(nodeid);
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			apacheConfigDao.close();
		}
		return retHashtable;
	}
}
