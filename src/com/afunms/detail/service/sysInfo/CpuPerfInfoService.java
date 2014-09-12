package com.afunms.detail.service.sysInfo;

import java.util.List;

import com.afunms.detail.reomte.model.CpuPerfInfo;
import com.afunms.temp.dao.CpuTempDao;
import com.afunms.temp.dao.OthersTempDao;

public class CpuPerfInfoService {
	
	private String type;
	
	private String subtype;
	
	private String nodeid;
	

	/**
	 * @param type
	 * @param subtype
	 * @param nodeid
	 */
	public CpuPerfInfoService(String nodeid, String type, String subtype) {
		this.nodeid = nodeid;
		this.type = type;
		this.subtype = subtype;
	}
	
	
	public List<CpuPerfInfo> getCurrCpuPerfInfo(){
		return getCurrCpuPerfInfo(null);
	}
	
	public List<CpuPerfInfo> getCurrCpuPerfInfo(String[] subentities){
		OthersTempDao othersTempDao = new OthersTempDao();
		List<CpuPerfInfo> cpuPerfInfoList = null;
		try {
			cpuPerfInfoList = othersTempDao.getCpuPerfInfoList(nodeid, type, subtype, subentities);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			othersTempDao.close();
		}
		return cpuPerfInfoList;
	}
	

	public List getCpuPerListInfo(){
		List cpuperList = null;
		OthersTempDao othersTempDao = new OthersTempDao();
		try {
			cpuperList = othersTempDao.getCpuPerListInfo(nodeid,type,subtype);
		} catch (RuntimeException e) { 
			e.printStackTrace();
		} finally {
			othersTempDao.close();
		}
		return cpuperList;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
