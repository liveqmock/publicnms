package com.afunms.detail.service.pingInfo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.polling.api.I_HostCollectData;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.temp.dao.PingTempDao;
import com.afunms.temp.dao.SystemTempDao;
import com.afunms.temp.model.NodeTemp;

public class PingInfoService {
	
	private String type;
	
	private String subtype;
	
	private String nodeid;
	

	public PingInfoService() {
		super();
	}

	/**
	 * @param type
	 * @param subtype
	 * @param nodeid
	 */
	public PingInfoService(String nodeid, String type, String subtype) {
		this.nodeid = nodeid;
		this.type = type;
		this.subtype = subtype;
	}
	
	public String getCurrDayPingAvgInfo(String ipaddress){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String currDay = simpleDateFormat.format(new Date());
		String startTime = currDay + " 00:00:00";
		String toTime = currDay + " 23:59:59";
		return getPingAvgInfo(ipaddress, startTime, toTime);
	}
	
	public String getPingAvgInfo(String ipaddress, String startTime, String toTime){
		String pingconavg = "0";
		I_HostCollectData hostmanager = new HostCollectDataManager();
		Hashtable ConnectUtilizationhash = new Hashtable();
		try{
			ConnectUtilizationhash = hostmanager.getCategory(ipaddress, "Ping", "ConnectUtilization", startTime, toTime);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		if (ConnectUtilizationhash.get("avgpingcon")!=null){
			pingconavg = (String)ConnectUtilizationhash.get("avgpingcon");
			if(pingconavg == null){
				pingconavg = "0";
			}
			pingconavg = pingconavg.replace("%", "");
		}
		return pingconavg;
	}
	
	public List<NodeTemp> getCurrPingInfo(String[] subentities){
		PingTempDao pingTempDao = new PingTempDao();
		List<NodeTemp> nodeTempList = null;
		try {
			nodeTempList = pingTempDao.getNodeTempList(nodeid, type, subtype, subentities);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			pingTempDao.close();
		}
		return nodeTempList;
	}
	
	/**
	 * 得到实时的连通率和响应时间
	 * @return
	 */
	public Vector getPingInfo(){
		Vector retVector = null;
		PingTempDao pingTempDao = new PingTempDao();
		try {
			retVector = pingTempDao.getPingInfo(nodeid, type, subtype);
		} catch (Exception e) { 
			e.printStackTrace();
		} finally {
			pingTempDao.close();
		}
		return retVector;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	/**
	 * 根据监控的Node列表得到平均连通率列表
	 * @param monitorNodelist
	 * @return
	 */
	public List<NodeTemp> getPingInfo(List monitorNodelist) {
		if(monitorNodelist == null || monitorNodelist.size() == 0){
			return null;
		}
		PingTempDao pingTempDao = new PingTempDao();
		List<NodeTemp> nodeTempList = null;
		try {
			nodeTempList = pingTempDao.getNodeTempList(monitorNodelist);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			pingTempDao.close();
		}
		return nodeTempList;
	}
	
	public List<NodeTemp> getResponseInfo(List monitorNodelist) {
		if(monitorNodelist == null || monitorNodelist.size() == 0){
			return null;
		}
		PingTempDao pingTempDao = new PingTempDao();
		List<NodeTemp> nodeTempList = null;
		try {
			nodeTempList = pingTempDao.getNodeTempResList(monitorNodelist);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			pingTempDao.close();
		}
		return nodeTempList;
	}
	
}
