package com.afunms.detail.service.ipListInfo;

import java.util.List;

import com.afunms.config.dao.IpAliasDao;
import com.afunms.config.model.IpAlias;

public class IpListInfoService {
	
	private String type;
	
	private String subtype;
	
	private String nodeid;
	

	/**
	 * @param type
	 * @param subtype
	 * @param nodeid
	 */
	public IpListInfoService(String nodeid, String type, String subtype) {
		this.nodeid = nodeid;
		this.type = type;
		this.subtype = subtype;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<IpAlias> getCurrAllIpListInfo(String ipaddress){
		IpAliasDao ipdao = new IpAliasDao();
	    List<IpAlias> ipList = null;
		try {
			ipList = (List<IpAlias>)ipdao.loadByIpaddress(ipaddress);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			ipdao.close();
		}
	    return ipList;
	     
	}
	
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
