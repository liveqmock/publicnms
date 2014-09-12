package com.afunms.polling.snmp.sqlserver;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.afunms.application.dao.DBDao;
import com.afunms.application.model.DBVo;
import com.afunms.application.util.IpTranslation;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.EncryptUtil;
import com.afunms.common.util.JdbcUtil;
import com.afunms.common.util.OracleJdbcUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SystemConstant;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.DBNode;
import com.gatherdb.GathersqlListManager;

public class collect_sysvalue {
	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicators) {
		
		Hashtable sys_hash = new Hashtable();
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
			String dburl = "jdbc:jtds:sqlserver://" + serverip + ":" + port + ";DatabaseName=master;charset=GBK;SelectMethod=CURSOR";
			sql = "SELECT SERVERPROPERTY ('productlevel') as productlevel, @@VERSION as VERSION,SERVERPROPERTY('MACHINENAME') as MACHINENAME,SERVERPROPERTY('IsSingleUser') as IsSingleUser,SERVERPROPERTY('ProcessID') as ProcessID,SERVERPROPERTY('IsIntegratedSecurityOnly') as IsIntegratedSecurityOnly,SERVERPROPERTY('IsClustered') as IsClustered";
			String passwords = EncryptUtil.decode(dbnode.getPassword());
			util = new JdbcUtil(dburl, username, passwords);
			util.jdbc();
			rs = util.stmt.executeQuery(sql);
			while (rs.next()) {
				if (rs.getString("productlevel") != null) {
					sys_hash.put("productlevel", rs
							.getString("productlevel"));
				}
				if (rs.getString("VERSION") != null) {
					sys_hash.put("VERSION", rs.getString("VERSION"));
				}
				if (rs.getString("MACHINENAME") != null) {
					sys_hash.put("MACHINENAME", rs
							.getString("MACHINENAME"));
				}
				if (rs.getString("IsSingleUser") != null) {
					String IsSingleUser = rs.getString("IsSingleUser");
					if (IsSingleUser.equalsIgnoreCase("1")) {
						sys_hash.put("IsSingleUser", "单个用户");
					} else {
						sys_hash.put("IsSingleUser", "非单个用户");
					}
				}
				if (rs.getString("ProcessID") != null) {
					sys_hash
							.put("ProcessID", rs.getString("ProcessID"));
				}
				if (rs.getString("IsIntegratedSecurityOnly") != null) {
					String IsSingleUser = rs
							.getString("IsIntegratedSecurityOnly");
					if (IsSingleUser.equalsIgnoreCase("1")) {
						sys_hash.put("IsIntegratedSecurityOnly",
								"集成安全性");
					} else {
						sys_hash.put("IsIntegratedSecurityOnly",
								"非集成安全性");
					}
				}
				if (rs.getString("IsClustered") != null) {
					String IsSingleUser = rs.getString("IsClustered");
					if (IsSingleUser.equalsIgnoreCase("1")) {
						sys_hash.put("IsClustered", "群集");
					} else {
						sys_hash.put("IsClustered", "非群集");
					}
				}
				//写入内存
				sqlserverdata.put("sysValue", sys_hash);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			
			try {
				if(rs != null)
					rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			util.closeStmt();
			util.closeConn();
		}
		//写入内存
		//ShareData.setSqlserverdata(serverip, sqlserverDataHash);
		
		//存入数据库
		try{
			String deletesql = "delete from nms_sqlserversysvalue where serverip ='" + hex + ":" + dbmonitorlist.getAlias() + "'";
			GathersqlListManager.Addsql(deletesql);
			String insertsql = addSqlserver_nmssysvalue(hex + ":" + dbmonitorlist.getAlias() , sys_hash);
			GathersqlListManager.Addsql(insertsql);
		}catch (Exception e) {
			// TODO: handle exception
		}
		return sqlserverDataHash;
	}

	public String addSqlserver_nmssysvalue(String serverip, Hashtable sysvalue) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		StringBuffer sBuffer = new StringBuffer();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			
			sBuffer
					.append("insert into nms_sqlserversysvalue(serverip, productlevel, version, "
							+ "machinename, issingleuser, processid,isintegratedsecurityonly,isclustered,mon_time)");
			sBuffer.append(" values('");
			sBuffer.append(serverip);
			sBuffer.append("','");
			sBuffer.append(String.valueOf(sysvalue.get("productlevel")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(sysvalue.get("VERSION")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(sysvalue.get("MACHINENAME")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(sysvalue.get("IsSingleUser")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(sysvalue.get("ProcessID")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(sysvalue
					.get("IsIntegratedSecurityOnly")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(sysvalue.get("IsClustered")));
			// sBuffer.append("','");
			// sBuffer.append(montime);
			// sBuffer.append("')");
			if (SystemConstant.DBType.equals("mysql")) {
				sBuffer.append("','");
				sBuffer.append(montime);
				sBuffer.append("')");
			} else if (SystemConstant.DBType.equals("oracle")) {
				sBuffer.append("',to_date('" + montime
						+ "','yyyy-mm-dd hh24:mi:ss'))");
			}
			// System.out.println(sBuffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} 
		return sBuffer.toString();
	}
	
}
