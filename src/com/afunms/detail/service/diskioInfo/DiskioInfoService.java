package com.afunms.detail.service.diskioInfo;

import java.util.List;

import com.afunms.temp.dao.DiskioTempDao;

/**
 * @author HONGLI E-mail: ty564457881@163.com
 * @version 创建时间：Aug 22, 2011 1:39:16 PM
 * 类说明 “磁盘读写信息”业务类
 */
public class DiskioInfoService {

	private String type;
	
	private String subtype;
	
	private String nodeid;
	

	/**
	 * @param type
	 * @param subtype
	 * @param nodeid
	 */
	public DiskioInfoService(String nodeid, String type, String subtype) {
		this.nodeid = nodeid;
		this.type = type;
		this.subtype = subtype;
	}
	
	public DiskioInfoService(String nodeid) {
		super();
		this.nodeid = nodeid;
	}
	
	public List getDiskiolistInfo(){  
		List diskioInfoList = null;
		DiskioTempDao diskPeriofTempDao = new DiskioTempDao();
		try {
			
			//System.out.println("=====***====diskPeriofTempDao====***======");
			diskioInfoList = diskPeriofTempDao.getdiskiolistInfo(nodeid, type, subtype);
		} catch (RuntimeException e) { 
			e.printStackTrace(); 
		}finally{
			diskPeriofTempDao.close();
		}
		return diskioInfoList;
	}
}
