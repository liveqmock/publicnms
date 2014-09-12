package com.afunms.detail.service.sysInfo;

import java.util.Hashtable;
import java.util.List;

import com.afunms.detail.reomte.model.DiskPerfInfo;
import com.afunms.temp.dao.DiskPerfTempDao;
import com.afunms.temp.dao.DiskTempDao;

public class DiskPerfInfoService {
	
	private String type;
	
	private String subtype;
	
	private String nodeid;
	

	/**
	 * @param type
	 * @param subtype
	 * @param nodeid
	 */
	public DiskPerfInfoService(String nodeid, String type, String subtype) {
		this.nodeid = nodeid;
		this.type = type;
		this.subtype = subtype;
	}
	
	
	public List<DiskPerfInfo> getCurrDiskPerfInfo(){
		return getCurrDiskPerfInfo(null);
	}
	
	public List<DiskPerfInfo> getCurrDiskPerfInfo(String[] subentities){
		DiskPerfTempDao diskPerfTempDao = new DiskPerfTempDao();
		List<DiskPerfInfo> diskPerfInfoList = null;
		try {
			diskPerfInfoList = diskPerfTempDao.getDiskInfoList(nodeid, type, subtype, subentities);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			diskPerfTempDao.close();
		}
		return diskPerfInfoList;
	}
	
	public List getDiskperflistInfo(){  
		List diskperfInfoList = null;
		DiskPerfTempDao diskPerfTempDao = new DiskPerfTempDao();
		try {
			diskperfInfoList = diskPerfTempDao.getDiskperflistInfo(nodeid, type, subtype);
		} catch (RuntimeException e) { 
			e.printStackTrace(); 
		}finally{
			diskPerfTempDao.close();
		}
		return diskperfInfoList;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
