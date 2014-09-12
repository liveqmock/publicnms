package com.afunms.detail.service.fdbInfo;

import java.util.List;

import com.afunms.temp.dao.FdbTempDao;
import com.afunms.temp.model.FdbNodeTemp;

public class FDBInfoService {
	
	private String type;
	
	private String subtype;
	
	private String nodeid;
	

	/**
	 * @param type
	 * @param subtype
	 * @param nodeid
	 */
	public FDBInfoService(String nodeid, String type, String subtype) {
		this.nodeid = nodeid;
		this.type = type;
		this.subtype = subtype;
	}
	
	
	public List<FdbNodeTemp> getCurrAllFDBInfo(){
		FdbTempDao fdbTempDao = new FdbTempDao();
		List<FdbNodeTemp> nodeTempList = null;
		try {
			nodeTempList = fdbTempDao.getFdbNodeTempList(nodeid, type, subtype);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			fdbTempDao.close();
		}
		return nodeTempList;
	}
	
	
//	public List<NodeTemp> getAllPortConfigListInfo(){
//		PortconfigDao portconfigDao = new PortconfigDao();
//		portconfigDao.loadPortconfig(id)£»
//		
//		return nodeTempList;
//	}




	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
