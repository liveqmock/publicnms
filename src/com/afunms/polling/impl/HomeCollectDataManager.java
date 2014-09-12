/*
 * Created on 2010-03-24
 *
 */
package com.afunms.polling.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.common.util.DBManager;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysUtil;
import com.afunms.polling.om.Diskcollectdata;
import com.afunms.topology.model.HostNode;

/**
 * @author Administrator
 *
 */
public class HomeCollectDataManager{
	java.text.SimpleDateFormat sdfDate = new java.text.SimpleDateFormat("yyyy-MM-dd");
	java.text.SimpleDateFormat sdfTime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * 
	 */
	public HomeCollectDataManager() {
		//super();
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * 查询 设备 cpu 的平均值
	 * 默认时间
	 * @param id
	 * @param ip
	 * @return
	 */
	public String getCpuAvg(String id , String ip){
		Date date = new Date();
		
		String startTime = sdfDate.format(date) + " 00:00:00";
		String endTime = sdfTime.format(date) ;
		return getCpuAvg(id , ip , startTime , endTime);
	}
	
	/**
	 * 
	 * 查询 设备 入口流速 的平均值
	 * 默认时间
	 * @param id
	 * @param ip
	 * @return
	 */
	public String getAllInutilhdxAvg(String id , String ip){
		Date date = new Date();
		
		String startTime = sdfDate.format(date) + " 00:00:00";
		String endTime = sdfTime.format(date) ;
		return getAllInutilhdxAvg(id , ip , startTime , endTime);
	}
	
	/**
	 * 
	 * 查询 设备 出口流速 的平均值
	 * 默认时间
	 * @param id
	 * @param ip
	 * @return
	 */
	public String getAllOututilhdxAvg(String id , String ip){
		Date date = new Date();
		
		String startTime = sdfDate.format(date) + " 00:00:00";
		String endTime = sdfTime.format(date) ;
		return getAllOututilhdxAvg(id , ip , startTime , endTime);
	}
	
	/**
	 * 
	 * 查询 设备 内存 的平均值
	 * 默认时间
	 * @param id
	 * @param ip
	 * @return
	 */
	public String getMemoryAvg(String id , String ip){
		Date date = new Date();
		
		String startTime = sdfDate.format(date) + " 00:00:00";
		String endTime = sdfTime.format(date) ;
		return getMemoryAvg(id , ip , startTime , endTime);
	}
	
	/**
	 * 
	 * 查询 设备 磁盘 
	 * 默认时间
	 * @param node
	 * @return
	 */
	public List getDisk(HostNode node){
		List hostdisklist = new ArrayList();
		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(node.getIpAddress());
		if (ipAllData != null) {
			Vector diskVector = (Vector) ipAllData.get("disk");
			if (diskVector != null && diskVector.size() > 0) {
				Hashtable hostdata = ShareData.getHostdata();
				for (int si = 0; si < diskVector.size(); si++) {
					Diskcollectdata diskdata = (Diskcollectdata) diskVector.elementAt(si);
					if (diskdata.getEntity().equalsIgnoreCase("Utilization")) {
						// 利用率
						if (node.getOstype() == 4 || node.getSysOid().startsWith("1.3.6.1.4.1.311.1.1.3")) {
							diskdata.setSubentity(diskdata.getSubentity().substring(0, 3));
						}
						hostdisklist.add(diskdata);
					}
				}
			}
		}
		return hostdisklist;
	}
	
	public String getCpuAvg(String id , String ip , String startTime , String endTime){
		String allipstr = praseIp(ip);
		String avg = "";
		
//		HostCollectDataDao hostCollectDataDao = new HostCollectDataDao("cpu" + allipstr);
//		try {
//			avg = hostCollectDataDao.getAvgByTime(startTime, endTime);
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		avg = getAvgByTime( "cpu"+allipstr , startTime , endTime);
		
		
		return avg;
	}
	
	public String getAllInutilhdxAvg(String id , String ip , String startTime , String endTime){
		String allipstr = praseIp(ip);
		String avg = "";
		
//		HostCollectDataDao hostCollectDataDao = new HostCollectDataDao("allutilhdx" + allipstr);
//		try {
//			avg = hostCollectDataDao.getAvgByTime(startTime, endTime);
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		avg = getAvgByTimeAndWhere( "allutilhdx" + allipstr , startTime , endTime , "and subentity='AllInBandwidthUtilHdx'");
		
		return avg;
	}
	
	public String getAllOututilhdxAvg(String id , String ip , String startTime , String endTime){
		String allipstr = praseIp(ip);
		String avg = "";
		
//		HostCollectDataDao hostCollectDataDao = new HostCollectDataDao("allutilhdx" + allipstr);
//		try {
//			avg = hostCollectDataDao.getAvgByTime(startTime, endTime);
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		avg = getAvgByTimeAndWhere( "allutilhdx" + allipstr , startTime , endTime , "and subentity='AllOutBandwidthUtilHdx'");
		
		return avg;
	}
	
	public String getMemoryAvg(String id , String ip , String startTime , String endTime){
		String allipstr = praseIp(ip);
		String avg = "";
		
//		HostCollectDataDao hostCollectDataDao = new HostCollectDataDao("allutilhdx" + allipstr);
//		try {
//			avg = hostCollectDataDao.getAvgByTime(startTime, endTime);
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		avg = getAvgByTime( "memory" + allipstr , startTime , endTime);
		
		return avg;
	}
	
	public String praseIp(String ip){
//		String ip1 ="",ip2="",ip3="",ip4="";	
//		String tempStr = "";
//		String allipstr = "";
//		if (ip.indexOf(".")>0){
//			ip1=ip.substring(0,ip.indexOf("."));
//			ip4=ip.substring(ip.lastIndexOf(".")+1,ip.length());			
//			tempStr = ip.substring(ip.indexOf(".")+1,ip.lastIndexOf("."));
//		}
//		ip2=tempStr.substring(0,tempStr.indexOf("."));
//		ip3=tempStr.substring(tempStr.indexOf(".")+1,tempStr.length());
//		allipstr=ip1+ip2+ip3+ip4;
		String allipstr = SysUtil.doip(ip);
		return allipstr;
	}
	
	
	
	private String getAvgByTimeAndWhere(String allipstr , String startTime , String endTime , String where) {
		String avg = "0";
		String sql = "select avg(thevalue) from "+ allipstr +" where collecttime between '" + startTime 
		+ "' and '" + endTime +"' " + where;
		DBManager conn = new DBManager();
		ResultSet rs = null;
		try {
			conn.executeQuery(sql);
			if(rs != null && rs.next()){
				avg = rs.getString("avg(thevalue)");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			avg = "0";
		} finally{
			if(rs != null){
				try{
					rs.close();
				}catch(Exception e){
					
				}
			}
			conn.close();
		}
		return avg;
	}
	
	private String getAvgByTime(String allipstr , String startTime , String endTime) {
		String avg = "0";
		String sql = "select avg(thevalue) from "+ allipstr +" where collecttime between '" + startTime 
		+ "' and '" + endTime +"'";
		DBManager conn = new DBManager();
		ResultSet rs = null;
		try {
			rs = conn.executeQuery(sql);
			if(rs != null && rs.next()){
				avg = rs.getString("avg(thevalue)");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			avg = "0";
		} finally{
			if(rs != null){
				try{
					rs.close();
				}catch(Exception e){
					
				}
			}
			conn.close();
		}
		return avg;
	}
	
//	public static void main(String[] args){
//		new HomeCollectDataManager().getCpuAvg("1","10.10.152.59");
//	}

}
