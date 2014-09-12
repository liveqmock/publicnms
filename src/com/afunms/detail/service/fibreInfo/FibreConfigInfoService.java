package com.afunms.detail.service.fibreInfo;

import java.util.List;

import com.afunms.detail.reomte.model.FibreConfigInfo;
import com.afunms.temp.dao.FbconfigTempDao;

public class FibreConfigInfoService {
	
	private String type;
	
	private String subtype;
	
	private String nodeid;
	

	/**
	 * @param type
	 * @param subtype
	 * @param nodeid
	 */
	public FibreConfigInfoService(String nodeid, String type, String subtype) {
		this.nodeid = nodeid;
		this.type = type;
		this.subtype = subtype;
	}
	
	
	public List<FibreConfigInfo> getFibreConfigInfo(){
		return getFibreConfigInfo(null);
	}
	
	public List<FibreConfigInfo> getFibreConfigInfo(String[] subentities){
		FbconfigTempDao fbconfigTempDao = new FbconfigTempDao();
		List<FibreConfigInfo> fibreConfigInfoList = null;
		try {
			fibreConfigInfoList = (List<FibreConfigInfo>)fbconfigTempDao.getFibreConfigInfoList(nodeid, type, subtype, subentities);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			fbconfigTempDao.close();
		}
		return fibreConfigInfoList;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
