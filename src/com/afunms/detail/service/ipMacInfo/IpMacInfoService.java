package com.afunms.detail.service.ipMacInfo;

import java.util.List;

import com.afunms.polling.om.IpMac;
import com.afunms.topology.dao.IpMacDao;

public class IpMacInfoService {
	
	private String type;
	
	private String subtype;
	
	private String nodeid;
	

	/**
	 * @param type
	 * @param subtype
	 * @param nodeid
	 */
	public IpMacInfoService(String nodeid, String type, String subtype) {
		this.nodeid = nodeid;
		this.type = type;
		this.subtype = subtype;
	}
	
	
	public List<IpMac> getCurrAllIpMacInfo(String relateipaddr){
		IpMacDao ipMacDao = new IpMacDao();
		List<IpMac> ipMacList = null;
		try {
			ipMacList = ipMacDao.loadIpMacByIP(relateipaddr);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			ipMacDao.close();
		}
		return ipMacList;
	}
	
	




	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
