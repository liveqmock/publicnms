package com.afunms.detail.service.sysInfo;

import java.util.List;

import com.afunms.detail.reomte.model.PageSpaceInfo;
import com.afunms.temp.dao.OthersTempDao;

public class PageSpaceInfoService {
	
	private String type;
	
	private String subtype;
	
	private String nodeid;
	

	/**
	 * @param type
	 * @param subtype
	 * @param nodeid
	 */
	public PageSpaceInfoService(String nodeid, String type, String subtype) {
		this.nodeid = nodeid;
		this.type = type;
		this.subtype = subtype;
	}
	
	
	public List<PageSpaceInfo> getCurrPageSpaceInfo(){
		return getCurrPageSpaceInfo(null);
	}
	
	public List<PageSpaceInfo> getCurrPageSpaceInfo(String[] subentities){
		OthersTempDao othersTempDao = new OthersTempDao();
		List<PageSpaceInfo> pageSpacefInfoList = null;
		try {
			pageSpacefInfoList = othersTempDao.getPageSpaceInfoList(nodeid, type, subtype, subentities);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			othersTempDao.close();
		}
		return pageSpacefInfoList;
	}
	


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
