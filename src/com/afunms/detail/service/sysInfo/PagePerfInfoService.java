package com.afunms.detail.service.sysInfo;

import java.util.List;

import com.afunms.detail.reomte.model.PagePerfInfo;
import com.afunms.temp.dao.OthersTempDao;

public class PagePerfInfoService {
	
	private String type;
	
	private String subtype;
	
	private String nodeid;
	

	/**
	 * @param type
	 * @param subtype
	 * @param nodeid
	 */
	public PagePerfInfoService(String nodeid, String type, String subtype) {
		this.nodeid = nodeid;
		this.type = type;
		this.subtype = subtype;
	}
	
	
	public List<PagePerfInfo> getCurrPagePerfInfo(){
		return getCurrPagePerfInfo(null);
	}
	
	public List<PagePerfInfo> getCurrPagePerfInfo(String[] subentities){
		OthersTempDao othersTempDao = new OthersTempDao();
		List<PagePerfInfo> pagePerfInfoList = null;
		try {
			pagePerfInfoList = othersTempDao.getPagePerfInfoList(nodeid, type, subtype, subentities);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			othersTempDao.close();
		}
		return pagePerfInfoList;
	}
	


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
