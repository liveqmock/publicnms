package com.afunms.detail.service.routerInfo;

import java.util.ArrayList;
import java.util.List;

import com.afunms.temp.dao.RouteTempDao;
import com.afunms.temp.model.RouterNodeTemp;

public class RouterInfoService {
	
	private String type;
	
	private String subtype;
	
	private String nodeid;
	

	/**
	 * @param type
	 * @param subtype
	 * @param nodeid
	 */
	public RouterInfoService(String nodeid, String type, String subtype) {
		this.nodeid = nodeid;
		this.type = type;
		this.subtype = subtype;
	}
	
	
	public List<RouterNodeTemp> getCurrAllRouterInfo(){
		RouteTempDao routeTempDao = new RouteTempDao();
		List<RouterNodeTemp> routerNodeTempList = null;
		try {
			routerNodeTempList = (List<RouterNodeTemp>)routeTempDao.getNodeTempList(nodeid, type, subtype);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			routeTempDao.close();
		}
		return routerNodeTempList;
	}
	
	/**
	 * 得到路由列表
	 * @param routerNodeTempList
	 * @return
	 */
	public List<String> getAllRouterInfo(){
		List<RouterNodeTemp> routerNodeTempList = getCurrAllRouterInfo();
		List<String> retList = new ArrayList<String>();
		for(RouterNodeTemp routerNodeTemp:routerNodeTempList){
			retList.add(routerNodeTemp.getRtype());
		}
		return retList;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
