package com.afunms.detail.service.cpuInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.afunms.temp.dao.CpuTempDao;
import com.afunms.temp.dao.OthersTempDao;
import com.afunms.temp.model.NodeTemp;

public class CpuInfoService {
	
	private String type;
	
	private String subtype;
	
	private String nodeid;
	

	/**
	 * @param type
	 * @param subtype
	 * @param nodeid
	 */
	public CpuInfoService(String nodeid, String type, String subtype) {
		this.nodeid = nodeid;
		this.type = type;
		this.subtype = subtype;
	}
	
	
	public CpuInfoService() {
		super();
	}

	public String getCurrCpuAvgInfo(){
		String currCpuAvgInfo = "0";
		CpuTempDao cpuTempDao = new CpuTempDao();
		try {
			List<NodeTemp> nodeTempList = cpuTempDao.getNodeTempList(nodeid, type, subtype);
			if(nodeTempList != null && nodeTempList.size() > 0){
				for(int i = 0; i < nodeTempList.size(); i++){
					NodeTemp nodeTemp = nodeTempList.get(i);
					//if("Utilization".equals(nodeTemp.getSubentity()) && "avg".equals(nodeTemp.getSindex())){ 
					if("Utilization".equals(nodeTemp.getSubentity()) ){
						currCpuAvgInfo = nodeTemp.getThevalue();
					}
				}
			}
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			cpuTempDao.close();
		}
		return currCpuAvgInfo;
	}
	
	public List<NodeTemp> getCurrPerCpuListInfo(){
		List<NodeTemp> nodeTempList = null;
		CpuTempDao cpuTempDao = new CpuTempDao();
		try {
			nodeTempList = cpuTempDao.getCurrPerCpuList(nodeid, type, subtype);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			cpuTempDao.close();
		}
		return nodeTempList;
	}
	
	public List getCpuPerListInfo(){
		List cpuperList = null;
		CpuTempDao cpuTempDao = new CpuTempDao();
		try {
			cpuperList = cpuTempDao.getCpuPerListInfo(nodeid,type,subtype);
		} catch (RuntimeException e) {
			e.printStackTrace();
		}finally{
			cpuTempDao.close();
		}
		return cpuperList;
	}
	
	public Vector getCpuInfo(){
		Vector cpuInfoVector = null;
		CpuTempDao cpuTempDao = new CpuTempDao();
		try {
			cpuInfoVector = cpuTempDao.getCpuInfo(nodeid,type,subtype);
		} catch (RuntimeException e) {
			e.printStackTrace();
		}finally{
			cpuTempDao.close();
		} 
		return cpuInfoVector;
	}
	
	public List getPerCpuList(String nodeids){
		List cpuList = null;
		CpuTempDao cpuTempDao = new CpuTempDao();
		try {
			cpuList = cpuTempDao.getPerCpuList(nodeids); 
		} catch (RuntimeException e) {
			e.printStackTrace();
		}finally{
			cpuTempDao.close();
		} 
		return cpuList;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	
	/**
	 * 根据监控的Node列表得到平均cpu利用率列表
	 * @param monitornodelist
	 * @return
	 */
	public List<NodeTemp> getCpuPerListInfo(List monitornodelist) {
		if(monitornodelist == null || monitornodelist.size() == 0){
			return null;
		}
		List<NodeTemp> nodeTempList = null;
		CpuTempDao cpuTempDao = new CpuTempDao();
		try {
			nodeTempList = cpuTempDao.getCurrPerCpuList(monitornodelist);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			cpuTempDao.close();
		}
		return nodeTempList;
	}

}
