package com.afunms.polling.snmp.sqlserver;

import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.Vector;

import com.afunms.application.dao.DBDao;
import com.afunms.application.model.DBVo;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SystemConstant;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.DBNode;
import com.gatherdb.GathersqlListManager;

public class collect_processProxy extends SnmpMonitor {

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicators) {

		Vector processVector = new Vector();

		String htKey = "info_v";
		Hashtable sqlserverDataHash = ShareData.getSqlserverdata();
		Hashtable returndata = new Hashtable();

		DBVo dbmonitorlist = null;
		DBDao dbdao = new DBDao();
		try {
			String dbid = nodeGatherIndicators.getNodeid();
			dbmonitorlist = (DBVo) dbdao.findByID(dbid);
		} catch (Exception e) {

		} finally {
			dbdao.close();
		}
		if (dbmonitorlist == null)
			return null;
		if (dbmonitorlist.getManaged() == 0)
			return null;
		DBNode dbnode = (DBNode) PollingEngine.getInstance().getDbByID(dbmonitorlist.getId());
		String serverip = dbnode.getIpAddress();
		if (sqlserverDataHash.get(serverip) == null) {
			sqlserverDataHash.put(serverip, new Hashtable());
		}
		Hashtable sqlserverdata = (Hashtable) sqlserverDataHash.get(serverip);

		String[] args = new String[] { "serverip", "spid", "waittime", "lastwaittype", "waitresource", "dbname", "username", "cpu", "physical_io", "memusage", "login_time", "last_batch", "status", "hostname", "program_name", "hostprocess", "cmd", "nt_domain", "nt_username", "net_library", "loginame", "mon_time" };
		returndata = LogParser.parse(this, dbmonitorlist, htKey, args);
		processVector = (Vector) returndata.get(htKey);
		// 写入内存
		sqlserverdata.put("info_v", processVector);
		// 存入数据库
		try {
			if (processVector != null && processVector.size() > 0) {

				try {
					Hashtable infoHash = null;
					String insertsql = "";
					String deletesql = "delete from nms_sqlserverinfo_v where serverip = '" + serverip + "'";
					GathersqlListManager.Addsql(deletesql);
					for (int i = 0; i < processVector.size(); i++) {
						infoHash = (Hashtable) processVector.get(i);
						insertsql = addSqlserver_nmsinfo_v(serverip, infoHash);
						GathersqlListManager.Addsql(insertsql);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sqlserverDataHash;
	}

	/*
	 * 封装插入sql语句
	 */
	public String addSqlserver_nmsinfo_v(String serverip, Hashtable info) throws Exception {

		DBManager dbmanager = new DBManager();
		StringBuffer sBuffer = new StringBuffer();
		try {
			sBuffer.append("insert into nms_sqlserverinfo_v(serverip, spid, waittime, ");
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
			if (SystemConstant.DBType.equals("mysql")) {
				sBuffer.append("','");
				sBuffer.append(String.valueOf(info.get("mon_time")));
				sBuffer.append("')");
			} else if (SystemConstant.DBType.equals("oracle")) {
				sBuffer.append("',to_date('" + String.valueOf(info.get("mon_time")) + "','yyyy-mm-dd hh24:mi:ss'))");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			dbmanager.close();
		}
		return sBuffer.toString();
	}

}
