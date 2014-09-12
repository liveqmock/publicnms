package com.afunms.detail.service.dnsInfo;

import java.util.Hashtable;

import com.afunms.application.dao.DnsConfigDao;

public class DnsInfoService {
	
	
	/**
	 * <p>从数据库中获取dns的采集信息</p>
	 * @param nodeid
	 * @return
	 */
	public Hashtable getDnsDataHashtable(String nodeid) {
		Hashtable retHashtable = null;
		DnsConfigDao dnsDao = new DnsConfigDao();
		try{
			retHashtable = dnsDao.getDnsDataHashtable(nodeid);
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			dnsDao.close();
		}
		return retHashtable;
	}
}
