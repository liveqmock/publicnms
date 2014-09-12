package com.afunms.polling.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.afunms.application.model.DnsConfig;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.detail.service.tomcatInfo.TomcatInfoService;
import com.afunms.polling.node.Tomcat;
import com.afunms.polling.om.Pingcollectdata;
import com.afunms.util.DataGate;
/**
 * <p>处理采集的DNS数据信息</p>
 * @author HONGLI  Mar 9, 2011
 */
public class ProcessDnsData {
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * 保存dns的信息
	 * @param dnsconfigs   dns的集合
	 * @param dnsdatas  dns的数据集合
	 */
	public void saveDnsData(List<DnsConfig> dnsconfigs, Hashtable dnsdatas) {
		if(dnsconfigs == null || dnsconfigs.size() == 0 || dnsdatas == null || dnsdatas.isEmpty()){
			return ;
		}
		Connection conn = null;
		PreparedStatement pstmt = null;
		PreparedStatement deletePstmt = null;
		String sql = "insert into nms_dns_temp(nodeid, entity, value, collecttime) values(?, ?, ?, ?)";
		String deleteSql = "delete from nms_dns_temp where nodeid = ?";
		try {
			conn = DataGate.getCon();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(sql);
			deletePstmt = conn.prepareStatement(deleteSql);
			Calendar tempCal=Calendar.getInstance();
			Date cc = tempCal.getTime();
			String time = sdf.format(cc);
			for (int i = 0; i < dnsconfigs.size(); i++) {
				DnsConfig dnsconfig = (DnsConfig)dnsconfigs.get(i);
				if(dnsdatas.containsKey(dnsconfig.getId())){
					Hashtable dnsData = (Hashtable)dnsdatas.get(dnsconfig.getId());
					if(dnsData == null){
						continue;
					}
					deletePstmt.setString(1, dnsconfig.getId()+"");
					deletePstmt.execute();
					Iterator iterator = dnsData.keySet().iterator();
					while (iterator.hasNext()) {
						String key = (String)iterator.next();
						Object valueObject = dnsData.get(key);
						//key 为 String 类型的情况
						if(valueObject instanceof String){
							String value = String.valueOf(valueObject);
							pstmt.setString(1, dnsconfig.getId()+"");
							pstmt.setString(2, key);//键
							pstmt.setString(3, value.trim());//值
							if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
								pstmt.setString(4, time);
							}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
								//java.util.Date  Dates = new java.util.Date(time);
								java.text.DateFormat date = java.text.DateFormat.getDateInstance();
								Date dat = date.parse(time);
							    java.sql.Timestamp time1 = new java.sql.Timestamp(dat.getYear(),dat.getMonth(),dat.getDate(),dat.getHours(),dat.getMinutes(),dat.getSeconds(),0);
								pstmt.setTimestamp(4, time1);
							}
							pstmt.addBatch();
						}else if(valueObject instanceof ArrayList){//key 为 List集合 的情况
							List valueList = (ArrayList)valueObject;
							for(int j=0; j<valueList.size(); j++){
								String value = (String)valueList.get(j);
								pstmt.setString(1, dnsconfig.getId()+"");
								pstmt.setString(2, key);//类别  mx ns cache
								pstmt.setString(3, value.trim());//值
								if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
									pstmt.setString(4, time);
								}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
									//java.util.Date  Dates = new java.util.Date(time);
									java.text.DateFormat date = java.text.DateFormat.getDateInstance();
									Date dat = date.parse(time);
								    java.sql.Timestamp time1 = new java.sql.Timestamp(dat.getYear(),dat.getMonth(),dat.getDate(),dat.getHours(),dat.getMinutes(),dat.getSeconds(),0);
									pstmt.setTimestamp(4, time1);
								}
								pstmt.addBatch();
							}
						}
					}
				}
			}
			pstmt.executeBatch();//批量插入
			conn.commit();
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally{
			if(deletePstmt != null){
				try {
					deletePstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pstmt != null){
				try {
					pstmt.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(conn != null){
				try {
					conn.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	public void saveDnsData(DnsConfig dnsconfig, Hashtable dnsdatas) {
		if(dnsconfig == null ){
			return ;
		}
		Connection conn = null;
		PreparedStatement pstmt = null;
		PreparedStatement deletePstmt = null;
		String sql = "insert into nms_dns_temp(nodeid, entity, value, collecttime) values(?, ?, ?, ?)";
		String deleteSql = "delete from nms_dns_temp where nodeid = ?";
		try {
			conn = DataGate.getCon();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(sql);
			deletePstmt = conn.prepareStatement(deleteSql);
			Calendar tempCal=Calendar.getInstance();
			Date cc = tempCal.getTime();
			String time = sdf.format(cc);
			
				if(dnsdatas.containsKey(dnsconfig.getId())){
					Hashtable dnsData = (Hashtable)dnsdatas.get(dnsconfig.getId());
					if(dnsData == null){
						return;
					}
					deletePstmt.setString(1, dnsconfig.getId()+"");
					deletePstmt.execute();
					Iterator iterator = dnsData.keySet().iterator();
					while (iterator.hasNext()) {
						String key = (String)iterator.next();
						Object valueObject = dnsData.get(key);
						//key 为 String 类型的情况
						if(valueObject instanceof String){
							String value = String.valueOf(valueObject);
							pstmt.setString(1, dnsconfig.getId()+"");
							pstmt.setString(2, key);//键
							pstmt.setString(3, value.trim());//值
							if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
								pstmt.setString(4, time);
							}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
								//java.util.Date  Dates = new java.util.Date(time);
								java.text.DateFormat date = java.text.DateFormat.getDateInstance();
								Date dat = date.parse(time);
							    java.sql.Timestamp time1 = new java.sql.Timestamp(dat.getYear(),dat.getMonth(),dat.getDate(),dat.getHours(),dat.getMinutes(),dat.getSeconds(),0);
								pstmt.setTimestamp(4, time1);
							}
							pstmt.addBatch();
						}else if(valueObject instanceof ArrayList){//key 为 List集合 的情况
							List valueList = (ArrayList)valueObject;
							for(int j=0; j<valueList.size(); j++){
								String value = (String)valueList.get(j);
								pstmt.setString(1, dnsconfig.getId()+"");
								pstmt.setString(2, key);//类别  mx ns cache
								pstmt.setString(3, value.trim());//值
								if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
									pstmt.setString(4, time);
								}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
									//java.util.Date  Dates = new java.util.Date(time);
									java.text.DateFormat date = java.text.DateFormat.getDateInstance();
									Date dat = date.parse(time);
								    java.sql.Timestamp time1 = new java.sql.Timestamp(dat.getYear(),dat.getMonth(),dat.getDate(),dat.getHours(),dat.getMinutes(),dat.getSeconds(),0);
									pstmt.setTimestamp(4, time1);
								}
								pstmt.addBatch();
							}
						}
					}
				}
			
			pstmt.executeBatch();//批量插入
			conn.commit();
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally{
			if(deletePstmt != null){
				try {
					deletePstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pstmt != null){
				try {
					pstmt.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(conn != null){
				try {
					conn.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	 //处理Ping得到的数据，放到历史表里
	public synchronized boolean createHostData(Pingcollectdata pingdata) {
		if (pingdata == null )
			return false;	
		DBManager conn=null;
		try{	
			 conn=new DBManager();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			Vector v = new Vector();
			//for (int i = 0; i < hostdatavec.size(); i++) {
				String ip = pingdata.getIpaddress();				
				if (pingdata.getRestype().equals("dynamic")) {						
					String allipstr = SysUtil.doip(ip);
					Calendar tempCal = (Calendar)pingdata.getCollecttime();							
					Date cc = tempCal.getTime();
					String time = sdf.format(cc);
					String tablename = "";
					String type=pingdata.getCategory();
//					if("ApachePing".equals(type)){
						tablename = "dnsping"+allipstr;
//					}
					String sql="";
					if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
						sql = "insert into "+tablename+"(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) "
						+"values('"+ip+"','"+pingdata.getRestype()+"','"+pingdata.getCategory()+"','"+pingdata.getEntity()+"','"
						+pingdata.getSubentity()+"','"+pingdata.getUnit()+"','"+pingdata.getChname()+"','"+pingdata.getBak()+"',"
						+pingdata.getCount()+",'"+pingdata.getThevalue()+"','"+time+"')";
				
					}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
						sql = "insert into "+tablename+"(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) "
						+"values('"+ip+"','"+pingdata.getRestype()+"','"+pingdata.getCategory()+"','"+pingdata.getEntity()+"','"
						+pingdata.getSubentity()+"','"+pingdata.getUnit()+"','"+pingdata.getChname()+"','"+pingdata.getBak()+"',"
						+pingdata.getCount()+",'"+pingdata.getThevalue()+"',to_date('"+time+"','YYYY-MM-DD HH24:MI:SS'))";
				
					}
					
					conn.executeUpdate(sql);
																									
				}				
				
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} 
		finally {
			conn.close();
			
		}
		return true;
	}
}
