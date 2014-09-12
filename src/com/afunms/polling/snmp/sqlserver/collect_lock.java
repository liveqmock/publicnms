package com.afunms.polling.snmp.sqlserver;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.dao.DBDao;
import com.afunms.application.model.DBVo;
import com.afunms.application.util.IpTranslation;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.EncryptUtil;
import com.afunms.common.util.JdbcUtil;
import com.afunms.common.util.OracleJdbcUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SystemConstant;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.DBNode;
import com.gatherdb.GathersqlListManager;

public class collect_lock extends SnmpMonitor{
	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicators) {
		String sql = "";
		JdbcUtil util = null;
		ResultSet rs = null;
		Vector lockVector = new Vector();
		Hashtable sqlserverDataHash = ShareData.getSqlserverdata();
		
		
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
		
		IpTranslation tranfer = new IpTranslation();
		String hex = tranfer.formIpToHex(serverip);
		String hexip = hex + ":" + dbmonitorlist.getAlias();
		
		if(sqlserverDataHash.get(serverip) == null){
			sqlserverDataHash.put(serverip, new Hashtable());
		}
		Hashtable sqlserverdata = (Hashtable)sqlserverDataHash.get(serverip);
		
		// 采集锁信息
		
		try {
			sql = "select distinct a.rsc_text,a.rsc_dbid,b.name as dbname,a.rsc_indid,a.rsc_objid,a.rsc_type,a.rsc_flag,a.req_mode,a.req_status,"
				+ "a.req_refcnt,a.req_cryrefcnt,a.req_lifetime,a.req_spid,a.req_ecid,a.req_ownertype,a.req_transactionID from syslockinfo a,"
				+ "sysdatabases b where a.rsc_dbid=b.dbid;";
			String dburl = "jdbc:jtds:sqlserver://" + serverip + ":1433;DatabaseName=master;charset=GBK;SelectMethod=CURSOR";
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
						//SysLogger.info(col.toLowerCase()+"============"+tmp);
						return_value.put(col.toLowerCase(), tmp);
					} else
						return_value.put(col.toLowerCase(), "--");
				}
				lockVector.addElement(return_value);
				return_value = null;
			}
			//写入内存
			sqlserverdata.put("lockinfo_v",lockVector);
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
		//写入内存
		//ShareData.setSqlserverdata(serverip, sqlserverDataHash);
		
		//存入数据库
		try{
			if(lockVector != null && lockVector.size()>0){
				try {
					Hashtable lockinfoHash = null;
					String insertsql = "";
					String deletesql = "delete from nms_sqlserverlockinfo_v where serverip = '" + hex + ":" + dbmonitorlist.getAlias() + "'";
					GathersqlListManager.Addsql(deletesql);
					for(int i=0;i<lockVector.size();i++){
						lockinfoHash = (Hashtable)lockVector.get(i);
						insertsql = addSqlserver_nmslockinfo_v(hex+":"+dbmonitorlist.getAlias(),lockinfoHash);
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
	
	
	/**
	 * 将sqlserver锁信息插入数据库
	 * 
	 * @param serverip
	 * @param lockinfo
	 * @return
	 * @throws Exception
	 */
	public String addSqlserver_nmslockinfo_v(String serverip,Hashtable lockinfo) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		StringBuffer sBuffer = new StringBuffer();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			
			sBuffer
					.append("insert into nms_sqlserverlockinfo_v(serverip, rsc_text, rsc_dbid, ");
			sBuffer
					.append("dbname, rsc_indid, rsc_objid,rsc_type,rsc_flag,req_mode,req_status,req_refcnt,req_cryrefcnt,");
			sBuffer
					.append("req_lifetime,req_spid,req_ecid,req_ownertype,req_transactionID,mon_time)");
			sBuffer.append(" values('");
			sBuffer.append(serverip);
			sBuffer.append("','");
			sBuffer.append(String.valueOf(lockinfo.get("rsc_text")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(lockinfo.get("rsc_dbid")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(lockinfo.get("dbname")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(lockinfo.get("rsc_indid")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(lockinfo.get("rsc_objid")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(lockinfo.get("rsc_type")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(lockinfo.get("rsc_flag")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(lockinfo.get("req_mode")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(lockinfo.get("req_status")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(lockinfo.get("req_refcnt")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(lockinfo.get("req_cryrefcnt")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(lockinfo.get("req_lifetime")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(lockinfo.get("req_spid")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(lockinfo.get("req_ecid")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(lockinfo.get("req_ownertype")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(lockinfo.get("req_transactionID")));
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
