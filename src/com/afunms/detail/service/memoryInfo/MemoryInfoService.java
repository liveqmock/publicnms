package com.afunms.detail.service.memoryInfo;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.temp.dao.CpuTempDao;
import com.afunms.temp.dao.DiskTempDao;
import com.afunms.temp.dao.MemoryTempDao;
import com.afunms.temp.model.NodeTemp;

public class MemoryInfoService {
	private String type;
	
	private String subtype;
	
	private String nodeid;
	

	/**
	 * @param type
	 * @param subtype
	 * @param nodeid
	 */
	public MemoryInfoService(String nodeid, String type, String subtype) {
		this.nodeid = nodeid;
		this.type = type;
		this.subtype = subtype;
	}
	
	public MemoryInfoService() {
	}

	/**
	 * 获取当前内存的值
	 * @return
	 */
	public List<NodeTemp> getCurrPerMemoryListInfo(){
		List<NodeTemp> nodeTempList = null;
		MemoryTempDao memoryTempDao = new MemoryTempDao();
		try {
			nodeTempList = memoryTempDao.getCurrMemoryListInfo(nodeid, type, subtype);
		} catch (RuntimeException e) {
			e.printStackTrace();
		}finally{
			memoryTempDao.close();
		}
		return nodeTempList;
	}
	
	/**
	 * 得到所有sindex
	 * @return
	 */
	public List getCurrMemorySindex(){
		List sindexList = null;
		MemoryTempDao memoryTempDao = new MemoryTempDao();
		try {
				sindexList = memoryTempDao.getCurrMemorySindex(nodeid, type, subtype);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
		} finally{
			memoryTempDao.close();
		}
		return sindexList;
	}
	
	/**
	 * 获取当前内存的信息
	 * @param sindex
	 * @return
	 */
	public List<NodeTemp> getCurrMemoryListInfo(String sindex){
		List<NodeTemp> nodeTempList = null;
		MemoryTempDao memoryTempDao = new MemoryTempDao();
		try {
			nodeTempList = memoryTempDao.getCurrMemoryListInfo(nodeid, type, subtype,sindex);
		} catch (RuntimeException e) {
			e.printStackTrace();
		}finally{
			memoryTempDao.close();
		}
		return nodeTempList;
	}
	
	/**
	 * 获取当前内存的信息
	 * @return
	 */
	public Hashtable getCurrMemoryListInfo(){
		Hashtable memoryHashtable = new Hashtable();
		List sindexsList = getCurrMemorySindex();
		DecimalFormat df=new DecimalFormat("#.##");
		for(int i=0;i<sindexsList.size();i++){
			Hashtable memoryItemHashtable = new Hashtable();
			List<NodeTemp> diskList = getCurrMemoryListInfo(String.valueOf(sindexsList.get(i)));
			for(int j=0;j<diskList.size();j++){
				NodeTemp nodeTemp = diskList.get(j);
				String subentity = nodeTemp.getSubentity();
				String thevalue = nodeTemp.getThevalue();
				String unit = nodeTemp.getUnit();
				memoryItemHashtable.put(subentity, df.format(Double.valueOf(thevalue))+unit);
			}
			memoryItemHashtable.put("name", String.valueOf(sindexsList.get(i)));
			memoryHashtable.put(i, memoryItemHashtable);
		}
		return memoryHashtable;
	}
	
	public Vector getMemoryInfo(){
		Vector retVector = new Vector();
		MemoryTempDao memoryTempDao = new MemoryTempDao();
		try {
			retVector = memoryTempDao.getMemoryInfo(nodeid, type, subtype);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			memoryTempDao.close();
		}
		return retVector;
	}

	/**
	 * 根据监控的Node列表得到内存利用率列表
	 * @param monitornodelist
	 * @return
	 */
	public List<NodeTemp> getMemoryInfo(List monitornodelist) {
		if(monitornodelist == null || monitornodelist.size() == 0){
			return null;
		}
		List<NodeTemp> nodeTempList = null;
		MemoryTempDao memoryTempDao = new MemoryTempDao();
		try {
			nodeTempList = memoryTempDao.getMemoryInfo(monitornodelist);
		} catch (RuntimeException e) {
			e.printStackTrace();
		}finally{
			memoryTempDao.close();
		}
		return nodeTempList;
	}
}
