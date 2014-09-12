package com.afunms.polling.snmp.sqlserver;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.afunms.application.dao.DBDao;
import com.afunms.application.model.DBVo;
import com.afunms.application.model.Sqlserver_processdata;
import com.afunms.application.util.IpTranslation;
import com.afunms.common.util.DB2JdbcUtil;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.EncryptUtil;
import com.afunms.common.util.JdbcUtil;
import com.afunms.common.util.OracleJdbcUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SystemConstant;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.DBNode;
import com.gatherdb.GathersqlListManager;


public class collect_process extends SnmpMonitor{
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicators) {
		
		
		Vector processVector = new Vector();
		
		String sql = "";
		Hashtable sqlserverDataHash = ShareData.getSqlserverdata();
		
		JdbcUtil util = null;
		ResultSet rs = null;
		DBVo dbmonitorlist = null; 
		DBDao dbdao = new DBDao();
		try{
			String dbid = nodeGatherIndicators.getNodeid();
			dbmonitorlist = (DBVo)dbdao.findByID(dbid);
		}catch(Exception e){
			
		}finally{
			dbdao.close();
		}
		if(dbmonitorlist == null)return null;
		if(dbmonitorlist.getManaged() == 0)return null;
		DBNode dbnode = (DBNode) PollingEngine.getInstance().getDbByID(dbmonitorlist.getId());
		String serverip = dbnode.getIpAddress();
		String username = dbnode.getUser();
		String port = dbnode.getPort();
		IpTranslation tranfer = new IpTranslation();
		String hex = tranfer.formIpToHex(serverip);
		String hexip = hex + ":" + dbmonitorlist.getAlias();
		
		if(sqlserverDataHash.get(serverip) == null){
			sqlserverDataHash.put(serverip, new Hashtable());
		}
		Hashtable sqlserverdata = (Hashtable)sqlserverDataHash.get(serverip);
		
		//采集数据
		try {
			sql = "select distinct a.spid,a.waittime,a.lastwaittype,a.waitresource,b.name as dbname,c.name as "
				+ "username,a.cpu,a.physical_io,a.memusage,a.login_time,a.last_batch,a.status,a.hostname,"
				+ "a.program_name,a.hostprocess,a.cmd,a.nt_domain,a.nt_username,a.net_library,a.loginame from "
				+ "sysprocesses a,sysdatabases b,sysusers c where a.dbid= b.dbid and a.uid=c.uid";
			String dburl = "jdbc:jtds:sqlserver://" + serverip + ":" + port + ";DatabaseName=master;charset=GBK;SelectMethod=CURSOR";
			String passwords = EncryptUtil.decode(dbnode.getPassword());
			util = new JdbcUtil(dburl, username, passwords);
			util.jdbc();
			rs = util.stmt.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();

			while (rs.next()) {
				Hashtable return_value = new Hashtable();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					String col = rsmd.getColumnName(i);
					if (rs.getString(i) != null) {
						String tmp = rs.getString(i).toString();
						//SysLogger.info(col.toLowerCase()+"=======process ================ "+tmp);
						return_value.put(col.toLowerCase(), tmp);
					} else
						return_value.put(col.toLowerCase(), "--");
				}
				processVector.addElement(return_value);
				return_value = null;
			}
			//写入内存
			sqlserverdata.put("info_v",processVector);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			util.closeStmt();
			util.closeConn();
		}
		
//		for (int j = 0; j < processVector.size(); j++) {
//			
//			try {
//				Sqlserver_processdata sp = new Sqlserver_processdata();
//				Hashtable ht = (Hashtable) processVector.get(j);
//				String spid = ht.get("spid").toString();
//				String dbname = ht.get("dbname").toString();
//				String usernames = ht.get("username").toString();
//				String cpu = ht.get("cpu").toString();
//				String memusage = ht.get("memusage").toString();
//				String physical_io = ht.get("physical_io").toString();
//
//				String status = ht.get("status").toString();
//				String hostname = ht.get("hostname").toString();
//				String program_name = ht.get("program_name").toString();
//				String login_time = ht.get("login_time").toString();
//
//				sp.setCpu(Integer.parseInt(cpu));
//				sp.setDbname(dbname);
//				sp.setHostname(hostname);
//				sp.setMemusage(Integer.parseInt(memusage));
//				Date date = new Date();
//				sp.setMon_time(date);
//				sp.setPhysical_io(Long.parseLong(physical_io));
//				sp.setProgram_name(program_name);
//				sp.setSpid(spid);
//				sp.setStatus(status);
//				sp.setUsername(usernames);
//				sp.setLogin_time(sdf.parse(login_time));
//				sp.setServerip(serverip);
//				dbdao.addSqlserver_processdata(sp);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
		
		//存入数据库
		try{
			if(processVector != null && processVector.size()>0){
				
				
				try {
					Hashtable infoHash = null;
					String insertsql = "";
					String deletesql = "delete from nms_sqlserverinfo_v where serverip = '" + hex + ":" + dbmonitorlist.getAlias() + "'";
					GathersqlListManager.Addsql(deletesql);
					for(int i=0;i<processVector.size();i++){
						infoHash = (Hashtable)processVector.get(i);
						insertsql = addSqlserver_nmsinfo_v(hex+":"+dbmonitorlist.getAlias(),infoHash);
						GathersqlListManager.Addsql(insertsql);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return sqlserverDataHash;
		
	}
	
	/*
	 * 封装插入sql语句
	 */
	public String addSqlserver_nmsinfo_v(String serverip,Hashtable info) throws Exception {
		
		DBManager dbmanager = new DBManager();
		StringBuffer sBuffer = new StringBuffer();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			
			sBuffer.append("insert into nms_sqlserverinfo_v(serverip, spid, waittime, " );
			sBuffer.append("lastwaittype, waitresource, dbname,username,cpu,physical_io,memusage,login_time,last_batch,");
			sBuffer.append("status,hostname,program_name,hostprocess,cmd,nt_domain,nt_username,net_library,loginame,mon_time)");
			sBuffer.append(" values('");
			sBuffer.append(serverip);
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("spid")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("waittime")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("lastwaittype")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("waitresource")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("dbname")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("username")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("cpu")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("physical_io")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("memusage")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("login_time")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("last_batch")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("status")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("hostname")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("program_name")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("hostprocess")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("cmd")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("nt_domain")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("nt_username")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("net_library")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("loginame")));
//			sBuffer.append("','");
//			sBuffer.append(montime);
//			sBuffer.append("')");
			if(SystemConstant.DBType.equals("mysql")){
				sBuffer.append("','");
				sBuffer.append(montime);
				sBuffer.append("')");
			}else if(SystemConstant.DBType.equals("oracle")){
				sBuffer.append( "',to_date('" + montime + "','yyyy-mm-dd hh24:mi:ss'))");
			}
//			System.out.println("DBDao.java==========4232 hang--------->>>>>."+sBuffer.toString());
			//dbmanager.executeUpdate(sBuffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			dbmanager.close();
		}
		return sBuffer.toString();
	}
	
}
