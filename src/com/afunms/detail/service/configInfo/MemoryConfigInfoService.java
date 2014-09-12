package com.afunms.detail.service.configInfo;

import java.util.List;

import com.afunms.detail.reomte.model.MemoryConfigInfo;
import com.afunms.temp.dao.MemoryTempDao;

public class MemoryConfigInfoService {
	
	private String type;
	
	private String subtype;
	
	private String nodeid;
	

	/**
	 * @param type
	 * @param subtype
	 * @param nodeid
	 */
	public MemoryConfigInfoService(String nodeid, String type, String subtype) {
		this.nodeid = nodeid;
		this.type = type;
		this.subtype = subtype;
	}
	
	
	public List<MemoryConfigInfo> getCurrMemoryConfigInfo(){
		MemoryTempDao memoryTempDao = new MemoryTempDao();
		List<MemoryConfigInfo> memoryConfigInfoList = null;
		try {
			memoryConfigInfoList = memoryTempDao.getMemoryConfigInfoList(nodeid, type, subtype);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			memoryTempDao.close();
		}
		return memoryConfigInfoList;
	}
	


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
