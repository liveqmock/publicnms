package com.afunms.detail.service.fibreInfo;

import java.util.List;

import com.afunms.detail.reomte.model.LightInfo;
import com.afunms.temp.dao.LightTempDao;

public class LightInfoService {
	
	private String type;
	
	private String subtype;
	
	private String nodeid;
	

	/**
	 * @param type
	 * @param subtype
	 * @param nodeid
	 */
	public LightInfoService(String nodeid, String type, String subtype) {
		this.nodeid = nodeid;
		this.type = type;
		this.subtype = subtype;
	}
	
	
	public List<LightInfo> getCurrLightInfo(){
		return getLightInfo(null);
	}
	
	public List<LightInfo> getLightInfo(String[] subentities){
		LightTempDao lightTempDao = new LightTempDao();
		List<LightInfo> lightInfoList = null;
		try {
			lightInfoList = (List<LightInfo>)lightTempDao.getLightInfoList(nodeid, type, subtype, subentities);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			lightTempDao.close();
		}
		return lightInfoList;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
