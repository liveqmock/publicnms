package com.afunms.polling.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.ArrayVPNCount;
import com.afunms.polling.om.ArrayVPNFlowRate;
import com.afunms.polling.om.ArrayVPNInfor;
import com.afunms.polling.om.ArrayVPNInterface;
import com.afunms.polling.om.ArrayVPNLog;
import com.afunms.polling.om.ArrayVPNSSL;
import com.afunms.polling.om.ArrayVPNSSLSysInfor;
import com.afunms.polling.om.ArrayVPNSession;
import com.afunms.polling.om.ArrayVPNSystem;
import com.afunms.polling.om.ArrayVPNTCP;
import com.afunms.polling.om.ArrayVPNTcs;
import com.afunms.polling.om.ArrayVPNVClientApp;
import com.afunms.polling.om.ArrayVPNVIPData;
import com.afunms.polling.om.ArrayVPNVS;
import com.afunms.polling.om.ArrayVPNVirtualSite;
import com.afunms.polling.om.ArrayVPNWeb;
import com.afunms.polling.om.CPUcollectdata;
import com.afunms.polling.om.VpnCluster;
import com.afunms.util.DataGate;
/**
 * <p>处理采集的VPN数据信息</p>
 * @author ChengFeng  2011
 */
public class ProcessVPNData {
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * 保存Cluster的信息
	 */
	public void saveVPNClusterData(Hashtable returnHash) {
		
		int count = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		PreparedStatement deletePstmt = null;
		String sql = "insert into nms_vpn_cluster_data_temp(ipaddress,type,subtype," +
				"Collecttime,clusterVirIndex,clusterId,clusterVirState," +
				"clusterVirIfname,clusterVirAddr,clusterVirAuthType,clusterVirAuthPasswd," +
				"clusterVirPreempt,clusterVirInterval,clusterVirPriority,id)values(?,?,?,?,?,?,?,?,?,?, ?, ?, ?,?,?);";
		Vector vector =(Vector)returnHash.get("Cluster");
//		System.out.print("&&delivered vector is "+vector);
		String deleteSql = "delete from nms_vpn_cluster_data_temp";
		try {
			conn = DataGate.getCon();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(sql);
			pstmt.execute(deleteSql);
			Calendar tempCal=Calendar.getInstance();
			Date cc = tempCal.getTime();
			String time = sdf.format(cc);
			VpnCluster vpnCluster = null;
			for (int i = 0; i < vector.size(); i++) {
				 vpnCluster =(VpnCluster)vector.get(i);
				 int id  = i+1;
//			      System.out.println("%%%%%%%%%%%%%%%%%%getIpaddress "+ vpnCluster.getIpaddress());
			      String ipaddress = vpnCluster.getIpaddress();
				  String type = vpnCluster.getType();
				  String subtype = vpnCluster.getSubtype();
				  String Collecttime = vpnCluster.getCollecttime().getTime().toString();
				  int clusterVirIndex = vpnCluster.getClusterVirIndex();
//				  System.out.println("收集数据clusterVirIndex为："+clusterVirIndex);
				  int clusterId = vpnCluster.getClusterId();
				  int clusterVirState = vpnCluster.getClusterVirState();
				  String clusterVirIfname = vpnCluster.getClusterVirIfname();
				  String clusterVirAddr = vpnCluster.getClusterVirAddr();
				  int clusterVirAuthType = vpnCluster.getClusterVirAuthType();
				  String clusterVirAuthPasswd = vpnCluster.getClusterVirAuthPasswd();
				  int clusterVirPreempt = vpnCluster.getClusterVirPreempt();
				  int clusterVirInterval = vpnCluster.getClusterVirInterval();
				  int clusterVirPriority = vpnCluster.getClusterVirPriority();
				
				pstmt = setPreperstatements(ipaddress,type,subtype,
						Collecttime,clusterVirIndex,clusterId,clusterVirState,
						clusterVirIfname,clusterVirAddr,clusterVirAuthType,clusterVirAuthPasswd,
						clusterVirPreempt,clusterVirInterval,clusterVirPriority,vpnCluster,pstmt,id);
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

	/**
	 * 利用反射机制 添加Cluster对象信息入库
	 
	 */
	private PreparedStatement setPreperstatements(//Long id,
      String ipaddress,
	  String type,
	  String subtype,
	  String Collecttime,
	  int clusterVirIndex,
	  int clusterId,
	  int clusterVirState,
	  String clusterVirIfname,
	  String clusterVirAddr,
	  int clusterVirAuthType,
	  String clusterVirAuthPasswd,
	  int clusterVirPreempt,
	  int clusterVirInterval,
	  int clusterVirPriority, VpnCluster vpnCluster,PreparedStatement pstmt,int id) throws Exception {
			pstmt.setString(1, ipaddress);
			pstmt.setString(2, type);
			pstmt.setString(3, subtype);
			pstmt.setString(4, Collecttime);
			pstmt.setInt(5, clusterVirIndex);
			pstmt.setInt(6, clusterId);
			pstmt.setInt(7, clusterVirState);
			pstmt.setString(8, clusterVirIfname);
			pstmt.setString(9, clusterVirAddr);
			pstmt.setInt(10, clusterVirAuthType);
			pstmt.setString(11, clusterVirAuthPasswd);
			pstmt.setInt(12, clusterVirPreempt);
			pstmt.setInt(13, clusterVirInterval);
			pstmt.setInt(14, clusterVirPriority);
			pstmt.setInt(15, id);
			pstmt.addBatch();
		return pstmt;
	}
	/**
	 * 保存SSL的信息
	 */
  public void saveVPNSSLData(Hashtable returnHash) {

	  
	   
		int count = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		PreparedStatement deletePstmt = null;
		String sql = "insert into nms_vpn_SSL_data_temp(ipaddress,type,subType,Collecttime,"
					  +"sslIndex,vhostName,openSSLConns,acceptedConns,requestedConns,"
					  +"resumedSess,resumableSess,missSess,id)" +
					  		"values(?,?,?,?,?,?,?,?,?,?, ?, ?, ?);";
		Vector vector =(Vector)returnHash.get("SSL");
		String deleteSql = "delete from nms_vpn_SSL_data_temp";
		try {
			conn = DataGate.getCon();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(sql);
			pstmt.execute(deleteSql);
			Calendar tempCal=Calendar.getInstance();
			Date cc = tempCal.getTime();
			String time = sdf.format(cc);
			ArrayVPNSSL arrayVPNSSL = null;
			for (int i = 0; i < vector.size(); i++) {
				arrayVPNSSL =(ArrayVPNSSL)vector.get(i);
				  int id  = i+1;
				  int sslIndex = arrayVPNSSL.getSslIndex();
				  String vhostName = arrayVPNSSL.getVhostName();
				  int openSSLConns  = arrayVPNSSL.getOpenSSLConns();
				  int acceptedConns = arrayVPNSSL.getAcceptedConns();
				  int requestedConns = arrayVPNSSL.getRequestedConns();
				  int resumedSess = arrayVPNSSL.getResumedSess();
				  int resumableSess = arrayVPNSSL.getResumableSess();
				  int missSess = arrayVPNSSL.getMissSess();
				  String ipaddress = arrayVPNSSL.getIpaddress();
				  String type = arrayVPNSSL.getType();
				  String subType = arrayVPNSSL.getSubType();
				  String Collecttime = arrayVPNSSL.getCollecttime().getTime().toString();
				  
				pstmt = setPreperstatementsSSL(ipaddress,type,subType,Collecttime,
						    sslIndex,
						    vhostName,
						    openSSLConns,
						    acceptedConns,
						    requestedConns,
						    resumedSess,
						    resumableSess,
						    missSess,
						    arrayVPNSSL,pstmt,id);
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

	/**
	 * 利用反射机制 添加Cluster对象信息入库
	 
	 */
	private PreparedStatement setPreperstatementsSSL(//Long id,
	  String ipaddress,
	 String type,
	  String subType,  String Collecttime, 
	  int sslIndex,
	  String vhostName,
	  int openSSLConns,
	  int acceptedConns,
	  int requestedConns,
	  int resumedSess,
	  int resumableSess,
	  int missSess,
	 ArrayVPNSSL arrayVPNSSL,PreparedStatement pstmt,int id) throws Exception {
	pstmt.setString(1, ipaddress);
	pstmt.setString(2, type);
	pstmt.setString(3, subType);
	pstmt.setString(4, Collecttime);
	pstmt.setInt(5, sslIndex);
	pstmt.setString(6, vhostName);
	pstmt.setInt(7, openSSLConns);
	pstmt.setInt(8, acceptedConns);
	pstmt.setInt(9, requestedConns);
	pstmt.setInt(10, resumedSess);
	pstmt.setInt(11, resumableSess);
	pstmt.setInt(12, missSess);
	pstmt.setInt(13, id);
	pstmt.addBatch();
	return pstmt;
	}
	
	
	/**
	 * 保存Session的信息
	 */
  public void saveVPNSessionData(Hashtable returnHash) {
		int count = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		PreparedStatement deletePstmt = null;
		String sql = "insert into nms_vpn_session_data_temp(ipaddress,type,subType,Collecttime,"
					  +"numSessions,successLogin,successLogout,failureLogin,totalBytesIn,totalBytesOut,"
					  +"maxActiveSessions,errorLogin,lockOutLogin,id)" +
					  		"values(?,?,?,?,?,?,?,?,?,?, ?, ?, ?, ?);";
		Vector vector =(Vector)returnHash.get("Session");
		String deleteSql = "delete from nms_vpn_session_data_temp";
		try {
			conn = DataGate.getCon();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(sql);
			pstmt.execute(deleteSql);
			Calendar tempCal=Calendar.getInstance();
			Date cc = tempCal.getTime();
			String time = sdf.format(cc);
			ArrayVPNSession arrayVPNSession = null;
			for (int i = 0; i < vector.size(); i++) {
				arrayVPNSession =(ArrayVPNSession)vector.get(i);
				  int id  = i+1;
				  int numSessions = arrayVPNSession.getNumSessions();
				  int successLogin = arrayVPNSession.getSuccessLogin();
				  int successLogout  = arrayVPNSession.getSuccessLogout();
				  int failureLogin = arrayVPNSession.getFailureLogin();
				  long totalBytesIn = arrayVPNSession.getTotalBytesIn();
				  long totalBytesOut = arrayVPNSession.getTotalBytesOut();
				  int maxActiveSessions = arrayVPNSession.getMaxActiveSessions();
				  int errorLogin = arrayVPNSession.getErrorLogin();
				  int lockOutLogin = arrayVPNSession.getLockOutLogin();
				  String ipaddress = arrayVPNSession.getIpaddress();
				  String type = arrayVPNSession.getType();
				  String subType = arrayVPNSession.getSubtype();
				  String Collecttime = arrayVPNSession.getCollecttime();
				  
				pstmt = setPreperstatementsSession(ipaddress,type,subType,Collecttime
						  ,numSessions,successLogin,successLogout,failureLogin,totalBytesIn,totalBytesOut,
						  maxActiveSessions,errorLogin,lockOutLogin,pstmt,id);
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

	/**
	 * 利用反射机制 添加Cluster对象信息入库
	 
	 */
	private PreparedStatement setPreperstatementsSession(//Long id,
	  String ipaddress,
	 String type,
	  String subType,  String Collecttime, 
	  int numSessions,int successLogin,int successLogout,int failureLogin,long totalBytesIn,long totalBytesOut,
	  int maxActiveSessions,int errorLogin,int lockOutLogin,PreparedStatement pstmt,int id) throws Exception {
	pstmt.setString(1, ipaddress);
	pstmt.setString(2, type);
	pstmt.setString(3, subType);
	pstmt.setString(4, Collecttime);
	pstmt.setInt(5, numSessions);
	pstmt.setInt(6, successLogin);
	pstmt.setInt(7, successLogout);
	pstmt.setInt(8, failureLogin);
	pstmt.setLong(9, totalBytesIn);
	pstmt.setLong(10, totalBytesOut);
	pstmt.setInt(11, maxActiveSessions);
	pstmt.setInt(12, errorLogin);
	pstmt.setInt(13, lockOutLogin);
	pstmt.setInt(14, id);
	pstmt.addBatch();
	return pstmt;
	}
	
	/**
	 * 保存TCP的信息
	 */
  public void saveVPNTCPData(Hashtable returnHash) {
		int count = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		PreparedStatement deletePstmt = null;
		String sql = "insert into nms_vpn_TCP_data_temp(ipaddress,type,subType,Collecttime,"
					  +"ctcpActiveOpens,ctcpPassiveOpens,ctcpAttemptFails,ctcpEstabResets,ctcpCurrEstab,ctcpInSegs,ctcpOutSegs,ctcpRetransSegs,ctcpInErrs,ctcpOutRsts,"
					  +"id)" +
					  		"values(?,?,?,?,?,?,?,?,?,?, ?, ?, ?, ?,?);";
		
		Vector vector =(Vector)returnHash.get("TCP");
		String deleteSql = "delete from nms_vpn_TCP_data_temp";
		try {
			conn = DataGate.getCon();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(sql);
			pstmt.execute(deleteSql);
			Calendar tempCal=Calendar.getInstance();
			Date cc = tempCal.getTime();
			String time = sdf.format(cc);
			ArrayVPNTCP arrayVPNTCP = null;
			for (int i = 0; i < vector.size(); i++) {
				arrayVPNTCP =(ArrayVPNTCP)vector.get(i);
				  int id  = i+1;
				  int ctcpActiveOpens = arrayVPNTCP.getCtcpActiveOpens();
				  long ctcpPassiveOpens = arrayVPNTCP.getCtcpPassiveOpens();
				  int ctcpAttemptFails = arrayVPNTCP.getCtcpAttemptFails();
				  long ctcpEstabResets = arrayVPNTCP.getCtcpEstabResets();
				  int ctcpCurrEstab = arrayVPNTCP.getCtcpCurrEstab();
				  long ctcpInSegs = arrayVPNTCP.getCtcpInSegs();
				  long ctcpOutSegs = arrayVPNTCP.getCtcpOutSegs();
				  long ctcpRetransSegs = arrayVPNTCP.getCtcpRetransSegs();
				  int ctcpInErrs = arrayVPNTCP.getCtcpInErrs();
				  long ctcpOutRsts = arrayVPNTCP.getCtcpOutRsts();
				  String ipaddress = arrayVPNTCP.getIpaddress();
				  String type = arrayVPNTCP.getType();
				  String subType = arrayVPNTCP.getSubtype();
				  String Collecttime = arrayVPNTCP.getCollecttime().getTime().toString();
				  
				pstmt = setPreperstatementsTCP(ipaddress,type,subType,Collecttime
						  ,ctcpActiveOpens,ctcpPassiveOpens,ctcpAttemptFails,ctcpEstabResets,ctcpCurrEstab,ctcpInSegs,
						  ctcpOutSegs,ctcpRetransSegs,ctcpInErrs,ctcpOutRsts,pstmt,id);
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

	/**
	 * 利用反射机制 添加TCP对象信息入库
	 
	 */
	private PreparedStatement setPreperstatementsTCP(//Long id,
	  String ipaddress,
	 String type,
	  String subType,  String Collecttime, 
	  int ctcpActiveOpens,
	  long ctcpPassiveOpens,
		 int ctcpAttemptFails,
		 long ctcpEstabResets,
		 int ctcpCurrEstab,
		 long ctcpInSegs,
		 long ctcpOutSegs,
		 long ctcpRetransSegs,
		 int ctcpInErrs,
		 long ctcpOutRsts,PreparedStatement pstmt,int id) throws Exception {
	pstmt.setString(1, ipaddress);
	pstmt.setString(2, type);
	pstmt.setString(3, subType);
	pstmt.setString(4, Collecttime);
	pstmt.setInt(5, ctcpActiveOpens);
	pstmt.setLong(6, ctcpPassiveOpens);
	pstmt.setInt(7, ctcpAttemptFails);
	pstmt.setLong(8, ctcpEstabResets);
	pstmt.setInt(9, ctcpCurrEstab);
	pstmt.setLong(10, ctcpInSegs);
	pstmt.setLong(11, ctcpOutSegs);
	pstmt.setLong(12, ctcpRetransSegs);
	pstmt.setInt(13, ctcpInErrs);
	pstmt.setLong(14, ctcpOutRsts);
	pstmt.setInt(15, id);
	pstmt.addBatch();
	return pstmt;
	}
	
	/**
	 * 保存Interface的信息
	 */
	
	public void saveVPNInterfaceData(Hashtable returnHash) {
		int count = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		PreparedStatement deletePstmt = null;
		String sql = "insert into nms_vpn_Interface_data_temp(ipaddress,type,subtype,Collecttime,"
					  +"infIndex,infDescr,infOperStatus,infAddress,"
					  +"id)" +
					  		"values(?,?,?,?,?,?,?,?,?);";
		
		Vector vector =(Vector)returnHash.get("Interface");
		String deleteSql = "delete from nms_vpn_Interface_data_temp";
		try {
			conn = DataGate.getCon();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(sql);
			pstmt.execute(deleteSql);
			Calendar tempCal=Calendar.getInstance();
			Date cc = tempCal.getTime();
			String time = sdf.format(cc);
			ArrayVPNInterface arrayVPNInterface = null;
			for (int i = 0; i < vector.size(); i++) {
				arrayVPNInterface =(ArrayVPNInterface)vector.get(i);
				  int id  = i+1;
				  int infIndex = arrayVPNInterface.getInfIndex();
				  String infDescr = arrayVPNInterface.getInfDescr();
				  String OperStatus = arrayVPNInterface.getInfOperStatus();
				  String infAddress = arrayVPNInterface.getInfAddress(); 
				  String ipaddress = arrayVPNInterface.getIpaddress();
				  String type = arrayVPNInterface.getType();
				  String subType = arrayVPNInterface.getSubtype();
				  String Collecttime = arrayVPNInterface.getCollecttime().getTime().toString();
				  
				pstmt = setPreperstatementsInterface(ipaddress,type,subType,Collecttime
						,infIndex,infDescr,OperStatus,infAddress,pstmt,id);
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

	/**
	 * 利用反射机制 添加TCP对象信息入库
	 
	 */
	private PreparedStatement setPreperstatementsInterface(
	  String ipaddress,
	 String type,
	  String subType,  String Collecttime, 
	  int infIndex,
	  String infDescr ,
	  String OperStatus ,
	  String infAddress ,PreparedStatement pstmt,int id) throws Exception {
	pstmt.setString(1, ipaddress);
	pstmt.setString(2, type);
	pstmt.setString(3, subType);
	pstmt.setString(4, Collecttime);
	pstmt.setInt(5, infIndex);
	pstmt.setString(6, infDescr);
	pstmt.setString(7, OperStatus);
	pstmt.setString(8, infAddress);
	pstmt.setInt(9, id);
	pstmt.addBatch();
	return pstmt;
	}
	
	/**
	 * 保存FlowRate的信息
	 */
	
	public void saveVPNFlowRateData(Hashtable returnHash) {
		int count = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		PreparedStatement deletePstmt = null;
		String sql = "insert into nms_vpn_FlowRate_data_temp(ipaddress,type,subtype,Collecttime,"
					  +"totalBytesRcvd,totalBytesSent,rcvdBytesPerSec,sentBytesPerSec,peakRcvdBytesPerSec,peakSentBytesPerSec,activeTransac,"
					  +"id)" +
					  		"values(?,?,?,?,?,?,?,?,?,?,?,?);";
		
		Vector vector =(Vector)returnHash.get("FlowRate");
		String deleteSql = "delete from nms_vpn_FlowRate_data_temp";
		try {
			conn = DataGate.getCon();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(sql);
			pstmt.execute(deleteSql);
			Calendar tempCal=Calendar.getInstance();
			Date cc = tempCal.getTime();
			String time = sdf.format(cc);
			ArrayVPNFlowRate arrayVPNFlowRate = null;
			for (int i = 0; i < vector.size(); i++) {
				arrayVPNFlowRate =(ArrayVPNFlowRate)vector.get(i);
				  int id  = i+1;
				  int totalBytesRcvd = arrayVPNFlowRate.getTotalBytesRcvd();
				  int totalBytesSent = arrayVPNFlowRate.getTotalBytesSent();
				  int rcvdBytesPerSec = arrayVPNFlowRate.getRcvdBytesPerSec();
				  int sentBytesPerSec = arrayVPNFlowRate.getSentBytesPerSec();
				  int peakRcvdBytesPerSec = arrayVPNFlowRate.getPeakRcvdBytesPerSec();
				  int peakSentBytesPerSec = arrayVPNFlowRate.getPeakSentBytesPerSec();
				  int activeTransac = arrayVPNFlowRate.getActiveTransac();
				  
				  String ipaddress = arrayVPNFlowRate.getIpaddress();
				  String type = arrayVPNFlowRate.getType();
				  String subType = arrayVPNFlowRate.getSubtype();
				  String Collecttime = arrayVPNFlowRate.getCollecttime().getTime().toString();
				  
				pstmt = setPreperstatementsFlowRate(ipaddress,type,subType,Collecttime
						,totalBytesRcvd,totalBytesSent,rcvdBytesPerSec,sentBytesPerSec,peakRcvdBytesPerSec,peakSentBytesPerSec,
						activeTransac,pstmt,id);
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

	/**
	 * 利用反射机制 添加Flow对象信息入库
	 
	 */
	private PreparedStatement setPreperstatementsFlowRate(
	  String ipaddress,
	 String type,
	  String subType,  String Collecttime, 
	  int totalBytesRcvd,int totalBytesSent,int rcvdBytesPerSec,int sentBytesPerSec,int peakRcvdBytesPerSec,int peakSentBytesPerSec,
	  int activeTransac,PreparedStatement pstmt,int id) throws Exception {
	pstmt.setString(1, ipaddress);
	pstmt.setString(2, type);
	pstmt.setString(3, subType);
	pstmt.setString(4, Collecttime);
	pstmt.setInt(5, totalBytesRcvd);
	pstmt.setInt(6, totalBytesSent);
	pstmt.setInt(7, rcvdBytesPerSec);
	pstmt.setInt(8, sentBytesPerSec);
	pstmt.setInt(9, peakRcvdBytesPerSec);
	pstmt.setInt(10, peakSentBytesPerSec);
	pstmt.setInt(11, activeTransac);
	pstmt.setInt(12, id);
	pstmt.addBatch();
	return pstmt;
	}

	
	
	/**
	 * 保存VirtualSite的信息
	 */
	
	public void saveVPNVirtualSiteData(Hashtable returnHash) {
		int count = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		PreparedStatement deletePstmt = null; 
		String sql = "insert into nms_vpn_VSite_data_temp(ipaddress,type,subtype,Collecttime,"
					  +"virtualSiteId,virtualSiteActiveSessions,virtualSiteSuccessLogin,virtualSiteFailureLogin,virtualSiteErrorLogin,virtualSiteSuccessLogout,virtualSiteBytesIn"
					  +" ,virtualSiteBytesOut,vSiteMaxActSess,vSiteFileAuthReq,"
					  +" vSiteFileUnauthReq,virtualSiteFileBytesIn,virtualSiteFileBytesOut,"
					  +" virtualSiteLockedLogin,virtualSiteRejectedLogin,"
					  +"id)" +
					  		"values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
		
		Vector vector =(Vector)returnHash.get("VirtualSite");
		String deleteSql = "delete from nms_vpn_VSite_data_temp";
		try {
			conn = DataGate.getCon();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(sql);
			pstmt.execute(deleteSql);
			Calendar tempCal=Calendar.getInstance();
			Date cc = tempCal.getTime();
			String time = sdf.format(cc);
			ArrayVPNVirtualSite arrayVPNVirtualSite = null;
			for (int i = 0; i < vector.size(); i++) {
				arrayVPNVirtualSite =(ArrayVPNVirtualSite)vector.get(i);
				  int id  = i+1;
				 
				  String virtualSiteId = arrayVPNVirtualSite.getVirtualSiteId();
				  int virtualSiteActiveSessions = arrayVPNVirtualSite.getVirtualSiteActiveSessions();
				  int virtualSiteSuccessLogin = arrayVPNVirtualSite.getVirtualSiteSuccessLogin();
				  int virtualSiteFailureLogin = arrayVPNVirtualSite.getVirtualSiteFailureLogin();
				  int virtualSiteErrorLogin = arrayVPNVirtualSite.getVirtualSiteErrorLogin();
					 int virtualSiteSuccessLogout = arrayVPNVirtualSite.getVirtualSiteSuccessLogout();
					 long virtualSiteBytesIn = arrayVPNVirtualSite.getVirtualSiteBytesIn();
					 long virtualSiteBytesOut = arrayVPNVirtualSite.getVirtualSiteBytesOut();
					 int virtualSiteMaxActiveSessions = arrayVPNVirtualSite.getVirtualSiteMaxActiveSessions();
					 int virtualSiteFileAuthorizedRequests = arrayVPNVirtualSite.getVirtualSiteFileAuthorizedRequests();
					 int virtualSiteFileUnauthorizedRequests = arrayVPNVirtualSite.getVirtualSiteFileUnauthorizedRequests();
					 int virtualSiteFileBytesIn = arrayVPNVirtualSite.getVirtualSiteFileBytesIn();
					 int virtualSiteFileBytesOut = arrayVPNVirtualSite.getVirtualSiteFileBytesOut();
					 int virtualSiteLockedLogin = arrayVPNVirtualSite.getVirtualSiteLockedLogin();
					 int virtualSiteRejectedLogin = arrayVPNVirtualSite.getVirtualSiteRejectedLogin();
				  String ipaddress = arrayVPNVirtualSite.getIpaddress();
				  String type = arrayVPNVirtualSite.getType();
				  String subType = arrayVPNVirtualSite.getSubtype();
				  String Collecttime = arrayVPNVirtualSite.getCollecttime().getTime().toString();
				  
				pstmt = setPreperstatementsVirtualSite(ipaddress,type,subType,Collecttime,
						virtualSiteId,virtualSiteActiveSessions,virtualSiteSuccessLogin,virtualSiteFailureLogin,
						virtualSiteErrorLogin,virtualSiteSuccessLogout,virtualSiteBytesIn
						  ,virtualSiteBytesOut,virtualSiteMaxActiveSessions,virtualSiteFileAuthorizedRequests,
						  virtualSiteFileUnauthorizedRequests,virtualSiteFileBytesIn,virtualSiteFileBytesOut,
						  virtualSiteLockedLogin,virtualSiteRejectedLogin,pstmt,id);
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

	/**
	 * 利用反射机制 添加Flow对象信息入库
	 
	 */
	 PreparedStatement setPreperstatementsVirtualSite(
	  String ipaddress,
	 String type,
	  String subType,  String Collecttime, 
	  String virtualSiteId,
		  int virtualSiteActiveSessions,
		  int virtualSiteSuccessLogin,
		  int virtualSiteFailureLogin,
		  int virtualSiteErrorLogin,
		  int virtualSiteSuccessLogout,
		  long virtualSiteBytesIn,
		  long virtualSiteBytesOut,
		  int virtualSiteMaxActiveSessions,
		  int virtualSiteFileAuthorizedRequests,
		  int virtualSiteFileUnauthorizedRequests,
		  int virtualSiteFileBytesIn,
		  int virtualSiteFileBytesOut,
		  int virtualSiteLockedLogin,
		  int virtualSiteRejectedLogin,PreparedStatement pstmt,int id) throws Exception {
	pstmt.setString(1, ipaddress);
	pstmt.setString(2, type);
	pstmt.setString(3, subType);
	pstmt.setString(4, Collecttime);
	pstmt.setString(5, virtualSiteId);
	pstmt.setInt(6, virtualSiteActiveSessions);
	pstmt.setInt(7, virtualSiteSuccessLogin);
	pstmt.setInt(8, virtualSiteFailureLogin);
	pstmt.setInt(9, virtualSiteErrorLogin);
	pstmt.setInt(10, virtualSiteSuccessLogout);
	pstmt.setLong(11, virtualSiteBytesIn);
	pstmt.setLong(12, virtualSiteBytesOut);
	pstmt.setInt(13, virtualSiteMaxActiveSessions);
	pstmt.setInt(14, virtualSiteFileAuthorizedRequests);
	pstmt.setInt(15, virtualSiteFileUnauthorizedRequests);
	pstmt.setInt(16, virtualSiteFileBytesIn);
	pstmt.setInt(17, virtualSiteFileBytesOut);
	pstmt.setInt(18, virtualSiteLockedLogin);
	pstmt.setInt(19, virtualSiteRejectedLogin);
	pstmt.setInt(20, id);
	pstmt.addBatch();
	return pstmt;
	}
	 
	 /** 
		 * 保存VNPweb的信息
		 */
		
		public void saveVPNInforData(Hashtable returnHash) {
			int count = 0;
			Connection conn = null;
			PreparedStatement pstmt = null;
			PreparedStatement deletePstmt = null;
			String sql = "insert into nms_vpn_VPN_data_temp(ipaddress,type,subtype,Collecttime,"
						+"vpnId,vpnTunnelsOpen,vpnTunnelsEst, vpnTunnelsRejected, vpnTunnelsTerminated,vpnBytesIn,"
						  +"vpnBytesOut,vpnUnauthPacketsIn,id)" +
						  		"values(?,?,?,?,?,?,?,?,?,?,?,?,?);";
			
			Vector vector =(Vector)returnHash.get("VPNInfor");
			String deleteSql = "delete from nms_vpn_VPN_data_temp";
			try {
				conn = DataGate.getCon();
				conn.setAutoCommit(false);
				pstmt = conn.prepareStatement(sql);
				pstmt.execute(deleteSql);
				Calendar tempCal=Calendar.getInstance();
				Date cc = tempCal.getTime();
				String time = sdf.format(cc);
				ArrayVPNInfor arrayVPNInfor = null;
				for (int i = 0; i < vector.size(); i++) {
					arrayVPNInfor =(ArrayVPNInfor)vector.get(i);
					  int id  = i+1;
					 
					  String vpnId = arrayVPNInfor.getVpnId();
					  int vpnTunnelsOpen = arrayVPNInfor.getVpnTunnelsOpen();
					  int vpnTunnelsEst = arrayVPNInfor.getVpnTunnelsEst();
					  int vpnTunnelsRejected = arrayVPNInfor.getVpnTunnelsRejected();
					  int vpnTunnelsTerminated = arrayVPNInfor.getVpnTunnelsTerminated();
					  long vpnBytesIn = arrayVPNInfor.getVpnBytesIn();
					  long vpnBytesOut = arrayVPNInfor.getVpnBytesOut();
					  long vpnUnauthPacketsIn = arrayVPNInfor.getVpnUnauthPacketsIn();
					  String ipaddress = arrayVPNInfor.getIpaddress();
					  String type = arrayVPNInfor.getType();
					  String subType = arrayVPNInfor.getSubtype();
					  String Collecttime = arrayVPNInfor.getCollecttime().getTime().toString();
					  
					pstmt = setPreperstatementsVPNInfor(ipaddress,type,subType,Collecttime,
							 vpnId , vpnTunnelsOpen,vpnTunnelsEst, vpnTunnelsRejected, vpnTunnelsTerminated,vpnBytesIn,
							  vpnBytesOut,vpnUnauthPacketsIn,pstmt,id);
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

		/**
		 * 利用反射机制 添加Flow对象信息入库
		 
		 */
		 PreparedStatement setPreperstatementsVPNInfor(
		  String ipaddress,
		 String type,
		  String subType,  String Collecttime, 
		  String vpnId ,
			  int vpnTunnelsOpen,
			  int vpnTunnelsEst,
			  int vpnTunnelsRejected,
			  int vpnTunnelsTerminated,
			  long vpnBytesIn,
			  long vpnBytesOut,
			  long vpnUnauthPacketsIn,
			  PreparedStatement pstmt,int id) throws Exception {
		pstmt.setString(1, ipaddress);
		pstmt.setString(2, type);
		pstmt.setString(3, subType);
		pstmt.setString(4, Collecttime);
		pstmt.setString(5, vpnId);
		pstmt.setInt(6, vpnTunnelsOpen);
		pstmt.setInt(7, vpnTunnelsEst);
		pstmt.setInt(8, vpnTunnelsRejected);
		pstmt.setInt(9, vpnTunnelsTerminated);
		pstmt.setLong(10, vpnBytesIn);
		pstmt.setLong(11, vpnBytesOut);
		pstmt.setLong(12, vpnUnauthPacketsIn);
		pstmt.setInt(13, id);
		pstmt.addBatch();
		return pstmt;
		}
		 
		 
		 /**
			 * 保存Web的信息
			 */
			
			public void saveVPNWebData(Hashtable returnHash) {
				int count = 0;
				Connection conn = null;
				PreparedStatement pstmt = null;
				PreparedStatement deletePstmt = null;
				String sql = "insert into nms_vpn_Web_data_temp(ipaddress,type,subtype,Collecttime,"
							+"webId,webAuthorizedReq,webUnauthorizedReq,webBytesIn,webBytesOut,"
							  +"id)" +
							  		"values(?,?,?,?,?,?,?,?,?,?);";
				
				Vector vector =(Vector)returnHash.get("VPNWeb");
				String deleteSql = "delete from nms_vpn_Web_data_temp";
				try {
					conn = DataGate.getCon();
					conn.setAutoCommit(false);
					pstmt = conn.prepareStatement(sql);
					pstmt.execute(deleteSql);
					Calendar tempCal=Calendar.getInstance();
					Date cc = tempCal.getTime();
					String time = sdf.format(cc);
					ArrayVPNWeb arrayVPNWeb = null;
					for (int i = 0; i < vector.size(); i++) {
						arrayVPNWeb =(ArrayVPNWeb)vector.get(i);
						  int id  = i+1;
						  String webId = arrayVPNWeb.getWebId();
						  int webAuthorizedReq = arrayVPNWeb.getWebAuthorizedReq();
						  int webUnauthorizedReq = arrayVPNWeb.getWebUnauthorizedReq();
						  long webBytesIn = arrayVPNWeb.getWebBytesIn();
						  long webBytesOut = arrayVPNWeb.getWebBytesOut();
						  String ipaddress = arrayVPNWeb.getIpaddress();
						  String type = arrayVPNWeb.getType();
						  String subType = arrayVPNWeb.getSubtype();
						  String Collecttime = arrayVPNWeb.getCollecttime().getTime().toString();
						  
						pstmt = setPreperstatementsVPNWeb(ipaddress,type,subType,Collecttime,
								webId,webAuthorizedReq,webUnauthorizedReq,webBytesIn,webBytesOut,pstmt,id);
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

			/**
			 * 利用反射机制 添加Flow对象信息入库
			 
			 */
			 PreparedStatement setPreperstatementsVPNWeb(
			  String ipaddress,
			 String type,
			  String subType,  String Collecttime, 
			  String webId ,int webAuthorizedReq,int webUnauthorizedReq,long webBytesIn,long webBytesOut,
				  PreparedStatement pstmt,int id) throws Exception {
			pstmt.setString(1, ipaddress);
			pstmt.setString(2, type);
			pstmt.setString(3, subType);
			pstmt.setString(4, Collecttime);
			pstmt.setString(5, webId);
			pstmt.setInt(6, webAuthorizedReq);
			pstmt.setInt(7, webUnauthorizedReq);
			pstmt.setLong(8, webBytesIn);
			pstmt.setLong(9, webBytesOut);
			pstmt.setInt(10, id);
			pstmt.addBatch();
			return pstmt;
			}
			 
			 /**
				 * 保存VS的信息
				 */
				
				public void saveVPNVSData(Hashtable returnHash) {
					int count = 0;
					Connection conn = null;
					PreparedStatement pstmt = null;
					PreparedStatement deletePstmt = null;
					String sql = "insert into nms_vpn_VS_data_temp(ipaddress,type,subtype,Collecttime,"
								+"vsIndex,vsID,vsProtocol,vsIpAddr,vsPort,"
								  +"id)" +
								  		"values(?,?,?,?,?,?,?,?,?,?);";
					
					Vector vector =(Vector)returnHash.get("VPNVS");
					String deleteSql = "delete from nms_vpn_VS_data_temp";
					try {
						conn = DataGate.getCon();
						conn.setAutoCommit(false);
						pstmt = conn.prepareStatement(sql);
						pstmt.execute(deleteSql);
						Calendar tempCal=Calendar.getInstance();
						Date cc = tempCal.getTime();
						String time = sdf.format(cc);
						ArrayVPNVS arrayVPNVS = null;
						for (int i = 0; i < vector.size(); i++) {
							arrayVPNVS =(ArrayVPNVS)vector.get(i);
							  int id  = i+1;
							  int vsIndex = arrayVPNVS.getVsIndex();
							  String vsID = arrayVPNVS.getVsID();
							  int vsProtocol = arrayVPNVS.getVsProtocol();
							  String vsIpAddr = arrayVPNVS.getVsIpAddr();
							  int vsPort = arrayVPNVS.getVsPort();
							  String ipaddress = arrayVPNVS.getIpaddress();
							  String type = arrayVPNVS.getType();
							  String subType = arrayVPNVS.getSubtype();
							  String Collecttime = arrayVPNVS.getCollecttime().getTime().toString();
							  
							pstmt = setPreperstatementsVPNVS(ipaddress,type,subType,Collecttime,
									vsIndex,vsID,vsProtocol,vsIpAddr,vsPort,pstmt,id);
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

				/**
				 * 利用反射机制 添加Flow对象信息入库
				 
				 */
				 PreparedStatement setPreperstatementsVPNVS(
				  String ipaddress,
				 String type,
				  String subType,  String Collecttime, 
				  int vsIndex,String vsID,int vsProtocol,String vsIpAddr,int vsPort,
					  PreparedStatement pstmt,int id) throws Exception {
				pstmt.setString(1, ipaddress);
				pstmt.setString(2, type);
				pstmt.setString(3, subType);
				pstmt.setString(4, Collecttime);
				pstmt.setInt(5, vsIndex);
				pstmt.setString(6, vsID);
				pstmt.setInt(7, vsProtocol);
				pstmt.setString(8, vsIpAddr);
				pstmt.setInt(9, vsPort);
				pstmt.setInt(10, id);
				pstmt.addBatch();
				return pstmt;
				}
			 
				 /**
					 * 保存VirtualClientApp的信息
					 */
					
					public void saveVPNVCData(Hashtable returnHash) {
						int count = 0;
						Connection conn = null;
						PreparedStatement pstmt = null;
						PreparedStatement deletePstmt = null;
						String sql = "insert into nms_vpn_VC_data_temp(ipaddress,type,subtype,Collecttime,"
									+"vclientAppIndex,vclientAppVirtualSite,vclientAppBytesIn,vclientAppBytesOut,"
									  +"id)" +
									  		"values(?,?,?,?,?,?,?,?,?);";
						
						Vector vector =(Vector)returnHash.get("VPNVClient");
						String deleteSql = "delete from nms_vpn_VC_data_temp";
						try {
							conn = DataGate.getCon();
							conn.setAutoCommit(false);
							pstmt = conn.prepareStatement(sql);
							pstmt.execute(deleteSql);
							Calendar tempCal=Calendar.getInstance();
							Date cc = tempCal.getTime();
							String time = sdf.format(cc);
							ArrayVPNVClientApp arrayVPNVClientApp = null;
							for (int i = 0; i < vector.size(); i++) {
								arrayVPNVClientApp =(ArrayVPNVClientApp)vector.get(i);
								  int id  = i+1;
							      int vclientAppIndex = arrayVPNVClientApp.getVclientAppIndex();
								  String vclientAppVirtualSite = arrayVPNVClientApp.getVclientAppVirtualSite();
								  long vclientAppBytesIn = arrayVPNVClientApp.getVclientAppBytesIn();
								  long vclientAppBytesOut = arrayVPNVClientApp.getVclientAppBytesOut();
								  String ipaddress = arrayVPNVClientApp.getIpaddress();
								  String type = arrayVPNVClientApp.getType();
								  String subType = arrayVPNVClientApp.getSubtype();
								  String Collecttime = arrayVPNVClientApp.getCollecttime().getTime().toString();
								  
								pstmt = setPreperstatementsVPNVC(ipaddress,type,subType,Collecttime,
										vclientAppIndex,vclientAppVirtualSite,vclientAppBytesIn,vclientAppBytesOut,pstmt,id);
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

					/**
					 * 利用反射机制 添加Flow对象信息入库
					 
					 */
					 PreparedStatement setPreperstatementsVPNVC(
					  String ipaddress,
					 String type,
					  String subType,  String Collecttime, 
					  int vclientAppIndex,String vclientAppVirtualSite,long vclientAppBytesIn,long vclientAppBytesOut,
						  PreparedStatement pstmt,int id) throws Exception {
					pstmt.setString(1, ipaddress);
					pstmt.setString(2, type);
					pstmt.setString(3, subType);
					pstmt.setString(4, Collecttime);
					pstmt.setInt(5, vclientAppIndex);
					pstmt.setString(6, vclientAppVirtualSite);
					pstmt.setLong(7, vclientAppBytesIn);
					pstmt.setLong(8, vclientAppBytesOut);
					pstmt.setInt(9, id);
					pstmt.addBatch();
					return pstmt;
					}
					 
					 
 /**
	 * 保存VIP的信息
	 */
	
	public void saveVPNVIPData(Hashtable returnHash) {
		int count = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		PreparedStatement deletePstmt = null;
		String sql = "insert into nms_vpn_VIP_data_temp(ipaddress,type,subtype,Collecttime,"
					+"vipStatus,hostName,currentTime,"
					  +"id)" +
					  		"values(?,?,?,?,?,?,?,?);";
		
		Vector vector =(Vector)returnHash.get("VIP");
		String deleteSql = "delete from nms_vpn_VIP_data_temp";
		try {
			conn = DataGate.getCon();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(sql);
			pstmt.execute(deleteSql);
			Calendar tempCal=Calendar.getInstance();
			Date cc = tempCal.getTime();
			String time = sdf.format(cc);
			ArrayVPNVIPData arrayVPNVIPData = null;
			for (int i = 0; i < vector.size(); i++) {
				arrayVPNVIPData =(ArrayVPNVIPData)vector.get(i);
				  int id  = i+1;
			      int vipStatus = arrayVPNVIPData.getVipStatus();
				  String hostName = arrayVPNVIPData.getHostName();
				  String currentTime = arrayVPNVIPData.getCurrentTime();
				  String ipaddress = arrayVPNVIPData.getIpaddress();
				  String type = arrayVPNVIPData.getType();
				  String subType = arrayVPNVIPData.getSubtype();
				  String Collecttime = arrayVPNVIPData.getCollecttime().getTime().toString();
				  
				pstmt = setPreperstatementsVPNVIP(ipaddress,type,subType,Collecttime,
						vipStatus,hostName,currentTime,pstmt,id);
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

	/**
	 * 利用反射机制 添加Flow对象信息入库
	 
	 */
	 PreparedStatement setPreperstatementsVPNVIP(
	  String ipaddress,
	 String type,
	  String subType,  String Collecttime, 
	  int vipStatus,String hostName,String currentTime,
		  PreparedStatement pstmt,int id) throws Exception {
	pstmt.setString(1, ipaddress);
	pstmt.setString(2, type);
	pstmt.setString(3, subType);
	pstmt.setString(4, Collecttime);
	pstmt.setInt(5, vipStatus);
	pstmt.setString(6, hostName);
	pstmt.setString(7, currentTime);
	pstmt.setInt(8, id);
	pstmt.addBatch();
	return pstmt;
	}
			 
	 /**
		 * 保存Log的信息
		 */
		
		public void saveVPNLogData(Hashtable returnHash) {
			int count = 0;
			Connection conn = null;
			PreparedStatement pstmt = null;
			PreparedStatement deletePstmt = null;
			
			String sql = "insert into nms_vpn_Log_data_temp(ipaddress,type,subtype,Collecttime,"
						+"logNotificationsSent,logNotificationsEnabled,logMaxSeverity,logHistTableMaxLength,"
						  +"id)" +
						  		"values(?,?,?,?,?,?,?,?,?);";
			
			Vector vector =(Vector)returnHash.get("VPNLog");
			String deleteSql = "delete from nms_vpn_Log_data_temp";
			try {
				conn = DataGate.getCon();
				conn.setAutoCommit(false);
				pstmt = conn.prepareStatement(sql);
				pstmt.execute(deleteSql);
				Calendar tempCal=Calendar.getInstance();
				Date cc = tempCal.getTime();
				String time = sdf.format(cc);
				ArrayVPNLog arrayVPNLog = null;
				for (int i = 0; i < vector.size(); i++) {
					arrayVPNLog =(ArrayVPNLog)vector.get(i);
					  int id  = i+1;
				      int logNotificationsSent = arrayVPNLog.getLogNotificationsSent();
					  int logNotificationsEnabled = arrayVPNLog.getLogNotificationsEnabled();
					  int logMaxSeverity = arrayVPNLog.getLogMaxSeverity();
					  int logHistTableMaxLength = arrayVPNLog.getLogHistTableMaxLength();
					  String ipaddress = arrayVPNLog.getIpaddress();
					  String type = arrayVPNLog.getType();
					  String subType = arrayVPNLog.getSubtype();
					  String Collecttime = arrayVPNLog.getCollecttime().getTime().toString();
					  
					pstmt = setPreperstatementsVPNLog(ipaddress,type,subType,Collecttime,
							logNotificationsSent,logNotificationsEnabled,logMaxSeverity,logHistTableMaxLength,pstmt,id);
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

		/**
		 * 利用反射机制 添加Flow对象信息入库
		 
		 */
		 PreparedStatement setPreperstatementsVPNLog(
		  String ipaddress,
		 String type,
		  String subType,  String Collecttime, 
		  int logNotificationsSent,int logNotificationsEnabled,int logMaxSeverity,int logHistTableMaxLength,
			  PreparedStatement pstmt,int id) throws Exception {
		pstmt.setString(1, ipaddress);
		pstmt.setString(2, type);
		pstmt.setString(3, subType);
		pstmt.setString(4, Collecttime);
		pstmt.setInt(5, logNotificationsSent);
		pstmt.setInt(6, logNotificationsEnabled);
		pstmt.setInt(7, logMaxSeverity);
		pstmt.setInt(8, logHistTableMaxLength);
		pstmt.setInt(9, id);
		pstmt.addBatch();
		return pstmt;
		}
		 
		 
		 /**
			 * 保存Count的信息
			 */
			
			public void saveVPNCountData(Hashtable returnHash) {
				int count = 0;
				Connection conn = null;
				PreparedStatement pstmt = null;
				PreparedStatement deletePstmt = null;
				
				String sql = "insert into nms_vpn_Count_data_temp(ipaddress,type,subtype,Collecttime,"
							+"virtualSiteCount,vpnCount,webCount,vclientAppCount,virtualSiteGroupCount,tcsModuleCount,imapsCount,smtpsCount,appFilterCount,dvpnSiteCount,dvpnResourceCount,dvpnTunnelCount," +
									"dvpnAclCount,maxCluster,clusterNum,rsCount,vsCount,infNumber,"
							  +"id)" +
							  		"values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
				
				Vector vector =(Vector)returnHash.get("VPNCount");
				String deleteSql = "delete from nms_vpn_Count_data_temp";
				try {
					conn = DataGate.getCon();
					conn.setAutoCommit(false);
					pstmt = conn.prepareStatement(sql);
					pstmt.execute(deleteSql);
					Calendar tempCal=Calendar.getInstance();
					Date cc = tempCal.getTime();
					String time = sdf.format(cc);
					ArrayVPNCount arrayVPNCount = null;
					for (int i = 0; i < vector.size(); i++) {
						arrayVPNCount =(ArrayVPNCount)vector.get(i);
						  int id  = i+1;
						   int virtualSiteCount = arrayVPNCount.getVirtualSiteCount();
						   int  vpnCount = arrayVPNCount.getVpnCount();
						   int webCount = arrayVPNCount.getWebCount();
						   int vclientAppCount = arrayVPNCount.getVclientAppCount();
						   int virtualSiteGroupCount = arrayVPNCount.getVirtualSiteGroupCount();
						   int tcsModuleCount = arrayVPNCount.getTcsModuleCount();
						   int imapsCount = arrayVPNCount.getImapsCount();
						   int  smtpsCount = arrayVPNCount.getSmtpsCount();
						   int appFilterCount = arrayVPNCount.getAppFilterCount();
						   int  dvpnSiteCount = arrayVPNCount.getDvpnSiteCount();
						   int dvpnResourceCount = arrayVPNCount.getDvpnResourceCount();
						   int dvpnTunnelCount = arrayVPNCount.getDvpnTunnelCount();
						   int dvpnAclCount = arrayVPNCount.getDvpnAclCount();
						   int  maxCluster = arrayVPNCount.getMaxCluster();
						   int clusterNum = arrayVPNCount.getClusterNum();
						   int rsCount = arrayVPNCount.getRsCount();
						   int vsCount = arrayVPNCount.getVsCount();
						   int infNumber = arrayVPNCount.getInfNumber();
					   						  
						  String ipaddress = arrayVPNCount.getIpaddress();
						  String type = arrayVPNCount.getType();
						  String subType = arrayVPNCount.getSubtype();
						  String Collecttime = arrayVPNCount.getCollecttime().getTime().toString();
						  
						pstmt = setPreperstatementsVPNCount(ipaddress,type,subType,Collecttime,
								virtualSiteCount,vpnCount,webCount,vclientAppCount,virtualSiteGroupCount,tcsModuleCount,imapsCount,smtpsCount,appFilterCount,dvpnSiteCount,dvpnResourceCount,dvpnTunnelCount,
								dvpnAclCount,maxCluster,clusterNum,rsCount,vsCount,infNumber,
								pstmt,id);
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

			/**
			 * 利用反射机制 添加Flow对象信息入库
			 
			 */
			 PreparedStatement setPreperstatementsVPNCount(
			  String ipaddress,
			 String type,
			  String subType,  String Collecttime, 
			   int virtualSiteCount,int vpnCount,int webCount,int vclientAppCount,
			   int virtualSiteGroupCount,int tcsModuleCount,int imapsCount,int smtpsCount,
			   int appFilterCount,int dvpnSiteCount,int dvpnResourceCount,int dvpnTunnelCount,
			   int dvpnAclCount,int maxCluster,int clusterNum,int rsCount,int vsCount,
			   int infNumber,PreparedStatement pstmt,int id) throws Exception {
			pstmt.setString(1, ipaddress);
			pstmt.setString(2, type);
			pstmt.setString(3, subType);
			pstmt.setString(4, Collecttime);
			pstmt.setInt(5, virtualSiteCount);
			pstmt.setInt(6, vpnCount);
			pstmt.setInt(7, webCount);
			pstmt.setInt(8, vclientAppCount);
			pstmt.setInt(9, virtualSiteGroupCount);
			pstmt.setInt(10, tcsModuleCount);
			pstmt.setInt(11, imapsCount);
			pstmt.setInt(12, smtpsCount);
			pstmt.setInt(13, appFilterCount);
			pstmt.setInt(14, dvpnSiteCount);
			pstmt.setInt(15, dvpnResourceCount);
			pstmt.setInt(16, dvpnTunnelCount);
			pstmt.setInt(17, dvpnAclCount);
			pstmt.setInt(18, maxCluster);
			pstmt.setInt(19, clusterNum);
			pstmt.setInt(20, rsCount);
			pstmt.setInt(21, vsCount);
			pstmt.setInt(22, infNumber);
			pstmt.setInt(23, id);
			pstmt.addBatch();
			return pstmt;
			}
			 
			 /**
				 * 保存TCS的信息
				 */
				
				public void saveVPNTCSData(Hashtable returnHash) {
					int count = 0;
					Connection conn = null;
					PreparedStatement pstmt = null;
					PreparedStatement deletePstmt = null;
					
					String sql = "insert into nms_vpn_TCS_data_temp(ipaddress,type,subtype,Collecttime,"
								+"tcsModuleIndex,tcsVirtualSite,tcsBytesIn,tcsBytesOut,"
								  +"id)" +
								  		"values(?,?,?,?,?,?,?,?,?);";
					
					Vector vector =(Vector)returnHash.get("VPNTCS");
					String deleteSql = "delete from nms_vpn_TCS_data_temp";
					try {
						conn = DataGate.getCon();
						conn.setAutoCommit(false);
						pstmt = conn.prepareStatement(sql);
						pstmt.execute(deleteSql);
						Calendar tempCal=Calendar.getInstance();
						Date cc = tempCal.getTime();
						String time = sdf.format(cc);
						ArrayVPNTcs arrayVPNTcs = null;
						for (int i = 0; i < vector.size(); i++) {
							arrayVPNTcs = (ArrayVPNTcs)vector.get(i);
							  int id  = i+1;
							   int tcsModuleIndex = arrayVPNTcs.getTcsModuleIndex();
							   String tcsVirtualSite = arrayVPNTcs.getTcsVirtualSite();
							   long tcsBytesIn = arrayVPNTcs.getTcsBytesIn();
							   long tcsBytesOut = arrayVPNTcs.getTcsBytesOut();
							  String ipaddress = arrayVPNTcs.getIpaddress();
							  String type = arrayVPNTcs.getType();
							  String subType = arrayVPNTcs.getSubtype();
							  String Collecttime = arrayVPNTcs.getCollecttime().getTime().toString();
							  
							pstmt = setPreperstatementsVPNTCS(ipaddress,type,subType,Collecttime,
									tcsModuleIndex,tcsVirtualSite,tcsBytesIn,tcsBytesOut,pstmt,id);
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

				/**
				 * 利用反射机制 添加Flow对象信息入库
				 
				 */
				 PreparedStatement setPreperstatementsVPNTCS(
				  String ipaddress,
				 String type,
				  String subType,  String Collecttime, 
				  int tcsModuleIndex,String tcsVirtualSite,long tcsBytesIn,long tcsBytesOut,
					  PreparedStatement pstmt,int id) throws Exception {
				pstmt.setString(1, ipaddress);
				pstmt.setString(2, type);
				pstmt.setString(3, subType);
				pstmt.setString(4, Collecttime);
				pstmt.setInt(5, tcsModuleIndex);
				pstmt.setString(6, tcsVirtualSite);
				pstmt.setLong(7, tcsBytesIn);
				pstmt.setLong(8, tcsBytesOut);
				pstmt.setInt(9, id);
				pstmt.addBatch();
				return pstmt;
				}	
			 /**
				 * 保存System的信息
				 */
				
				public void saveVPNSystemData(Hashtable returnHash) {
					int count = 0;
					Connection conn = null;
					PreparedStatement pstmt = null;
					PreparedStatement deletePstmt = null;
					
					String sql = "insert into nms_vpn_System_data_temp(ipaddress,type,subtype,Collecttime,"
								+"connectionsPerSec,requestsPerSec,"
								  +"id)" +
								  		"values(?,?,?,?,?,?,?);";
					
					Vector vector =(Vector)returnHash.get("VPNSystem");
					String deleteSql = "delete from nms_vpn_System_data_temp";
					try {
						conn = DataGate.getCon();
						conn.setAutoCommit(false);
						pstmt = conn.prepareStatement(sql);
						pstmt.execute(deleteSql);
						Calendar tempCal=Calendar.getInstance();
						Date cc = tempCal.getTime();
						String time = sdf.format(cc);
						ArrayVPNSystem arrayVPNSystem = null;
						for (int i = 0; i < vector.size(); i++) {
							  arrayVPNSystem = (ArrayVPNSystem)vector.get(i);
							  int id  = i+1;
							  int connectionsPerSec = arrayVPNSystem.getConnectionsPerSec();
							  int requestsPerSec = arrayVPNSystem.getRequestsPerSec();

							  String ipaddress = arrayVPNSystem.getIpaddress();
							  String type = arrayVPNSystem.getType();
							  String subType = arrayVPNSystem.getSubtype();
							  String Collecttime = arrayVPNSystem.getCollecttime().getTime().toString();
							  
							pstmt = setPreperstatementsVPNSystem(ipaddress,type,subType,Collecttime,
									connectionsPerSec,requestsPerSec,pstmt,id);
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

				/**
				 * 利用反射机制 添加Flow对象信息入库
				 
				 */
				 PreparedStatement setPreperstatementsVPNSystem(
				  String ipaddress,
				 String type,
				  String subType,  String Collecttime, 
				  int connectionsPerSec,int requestsPerSec,
				  PreparedStatement pstmt,int id) throws Exception {
				pstmt.setString(1, ipaddress);
				pstmt.setString(2, type);
				pstmt.setString(3, subType);
				pstmt.setString(4, Collecttime);
				pstmt.setInt(5, connectionsPerSec);
				pstmt.setInt(6, requestsPerSec);
				pstmt.setInt(7, id);
				pstmt.addBatch();
				return pstmt;
				}	 
				 /**
					 * 保存cpu的信息
					 */
					
					public void saveVPNCpuData(Hashtable returnHash) {
						int count = 0;
						Connection conn = null;
						PreparedStatement pstmt = null;
						PreparedStatement deletePstmt = null;
						Hashtable allprocesshash = new Hashtable();
						Enumeration processhash = allprocesshash.keys();
						String sql = "insert into nms_cpu_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)" +
									  		"values(?,?,?,?,?,?,?,?,?,?,?,?,?);";
						
						Vector vector =(Vector)returnHash.get("cpu");
						String deleteSql = "delete from nms_cpu_data_temp";
						try {
//							String ip = (String)processhash.nextElement();
		    				
							conn = DataGate.getCon();
							conn.setAutoCommit(false);
							pstmt = conn.prepareStatement(sql);
							pstmt.execute(deleteSql);
							
							
							Calendar tempCal=Calendar.getInstance();
							Date cc = tempCal.getTime();
							String time = sdf.format(cc);
							CPUcollectdata cpuCollectdata = null;
							for (int i = 0; i < vector.size(); i++) {
								cpuCollectdata =(CPUcollectdata)vector.get(i);
								
								String ip = cpuCollectdata.getIpaddress();
								Host host = (Host)PollingEngine.getInstance().getNodeByIP(ip);    
								long nodeid = host.getId();
								String type = "VPN";
								String subtype = "ArrayNetworks";
								String entity = cpuCollectdata.getEntity();
								String subentity = cpuCollectdata.getSubentity();
								String sindex = cpuCollectdata.getCategory();
								String thevalue = cpuCollectdata.getThevalue();
								String chname = cpuCollectdata.getChname();
								String restype = cpuCollectdata.getRestype();
								Calendar collecttime = tempCal;//cpuCollectdata.getCollecttime();
								String unit = cpuCollectdata.getUnit();
								String bak = cpuCollectdata.getBak();
//								System.out.println(nodeid+ip+type+subtype+entity+subentity+sindex+thevalue+chname+restype+collecttime+unit+bak);
								pstmt = setPreperstatementsVPNCpu(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak,pstmt);
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

					/**
					 * 利用反射机制 添加Flow对象信息入库
					 
					 */
					 PreparedStatement setPreperstatementsVPNCpu(
						long nodeid,String ip,String type,String subtype,String entity,String subentity,
						String sindex,String thevalue,String chname,String restype,Calendar collecttime,
						String unit,String bak,PreparedStatement pstmt) throws Exception {
					pstmt.setLong(1, nodeid);
					pstmt.setString(2, ip);
					pstmt.setString(3, type);
					pstmt.setString(4, subtype);
					pstmt.setString(5, entity);
					pstmt.setString(6, subentity);
					pstmt.setString(7, sindex);
					pstmt.setString(8, thevalue);
					pstmt.setString(9, chname);
					pstmt.setString(10, restype);
					pstmt.setString(11, collecttime.getTime().toGMTString());
					pstmt.setString(12, unit);
					pstmt.setString(13, bak);
					pstmt.addBatch();
					return pstmt;
					}	 
					 /**
						 * 保存VS的信息
						 */
						
						public void saveVPNSSLSysInforData(Hashtable returnHash) {
							int count = 0;
							Connection conn = null;
							PreparedStatement pstmt = null;
							PreparedStatement deletePstmt = null;
							String sql = "insert into nms_vpn_SSLSysInfor_data_temp(ipaddress,type,subtype,Collecttime,"
										+"sslStatus,vhostNum,totalOpenSSLConns,totalAcceptedConns,totalRequestedConns,"
										  +"id)" +
										  		"values(?,?,?,?,?,?,?,?,?,?);";
							
							Vector vector =(Vector)returnHash.get("VPNSSLInfor");
							String deleteSql = "delete from nms_vpn_SSLSysInfor_data_temp";
							try {
								conn = DataGate.getCon();
								conn.setAutoCommit(false);
								pstmt = conn.prepareStatement(sql);
								pstmt.execute(deleteSql);
								Calendar tempCal=Calendar.getInstance();
								Date cc = tempCal.getTime();
								String time = sdf.format(cc);
								ArrayVPNSSLSysInfor arrayVPNSSLSysInfor = null;
								for (int i = 0; i < vector.size(); i++) {
									arrayVPNSSLSysInfor =(ArrayVPNSSLSysInfor)vector.get(i);
									  int id  = i+1;
								      String sslStatus = arrayVPNSSLSysInfor.getSslStatus();
									  int vhostNum = arrayVPNSSLSysInfor.getVhostNum();
									  long totalOpenSSLConns = arrayVPNSSLSysInfor.getTotalOpenSSLConns();
									  long totalAcceptedConns = arrayVPNSSLSysInfor.getTotalAcceptedConns();
									  long totalRequestedConns = arrayVPNSSLSysInfor.getTotalRequestedConns();
									  String ipaddress = arrayVPNSSLSysInfor.getIpaddress();
									  String type = arrayVPNSSLSysInfor.getType();
									  String subType = arrayVPNSSLSysInfor.getSubtype();
									  String Collecttime = arrayVPNSSLSysInfor.getCollecttime().getTime().toString();
									  
									pstmt = setPreperstatementsVPNSSLSysInfor(ipaddress,type,subType,Collecttime,
											sslStatus,vhostNum,totalOpenSSLConns,totalAcceptedConns,totalRequestedConns,pstmt,id);
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

						/**
						 * 利用反射机制 添加Flow对象信息入库
						 
						 */
						 PreparedStatement setPreperstatementsVPNSSLSysInfor(
						  String ipaddress,
						 String type,
						  String subType,  String Collecttime, 
						    String sslStatus,int vhostNum,long totalOpenSSLConns,long totalAcceptedConns
						    ,long totalRequestedConns,PreparedStatement pstmt,int id) throws Exception {
						pstmt.setString(1, ipaddress);
						pstmt.setString(2, type);
						pstmt.setString(3, subType);
						pstmt.setString(4, Collecttime);
						pstmt.setString(5, sslStatus);
						pstmt.setInt(6, vhostNum);
						pstmt.setLong(7, totalOpenSSLConns);
						pstmt.setLong(8, totalAcceptedConns);
						pstmt.setLong(9, totalRequestedConns);
						pstmt.setInt(10, id);
						pstmt.addBatch();
						return pstmt;
						}		  
}



