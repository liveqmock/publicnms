package com.afunms.polling.snmp.sqlserver;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.afunms.application.dao.DBDao;
import com.afunms.application.model.DBVo;
import com.afunms.application.util.IpTranslation;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SystemConstant;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.DBNode;
import com.gatherdb.GathersqlListManager;

public class collect_lockProxy extends SnmpMonitor {
	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicators) {
		Vector lockVector = new Vector();
		String htKey = "lockinfo_v";
		Hashtable sqlserverDataHash = ShareData.getSqlserverdata();

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

		String hex = IpTranslation.formIpToHex(serverip);

		if (sqlserverDataHash.get(serverip) == null) {
			sqlserverDataHash.put(serverip, new Hashtable());
		}
		Hashtable sqlserverdata = (Hashtable) sqlserverDataHash.get(serverip);
		Hashtable returndata = new Hashtable();
		String[] args = new String[] { "serverip", "rsc_text", "rsc_dbid", "dbname", "rsc_indid", "rsc_objid", "rsc_type", "rsc_flag", "req_mode", "req_status", "req_refcnt", "req_cryrefcnt", "req_lifetime", "req_spid", "req_ecid", "req_ownertype", "req_transactionID", "mon_time" };
		returndata = LogParser.parse(this, dbmonitorlist, htKey, args);
		// 采集锁信息
		lockVector = (Vector) returndata.get(htKey);
		// 写入内存
		sqlserverdata.put("lockinfo_v", lockVector);
		// 存入数据库
		try {
			if (lockVector != null && lockVector.size() > 0) {
				try {
					Hashtable lockinfoHash = null;
					String insertsql = "";
					String deletesql = "delete from nms_sqlserverlockinfo_v where serverip = '" + hex + ":" + dbmonitorlist.getAlias() + "'";
					GathersqlListManager.Addsql(deletesql);
					for (int i = 0; i < lockVector.size(); i++) {
						lockinfoHash = (Hashtable) lockVector.get(i);
						insertsql = addSqlserver_nmslockinfo_v(serverip, lockinfoHash);
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

	/**
	 * 将sqlserver锁信息插入数据库
	 * 
	 * @param serverip
	 * @param lockinfo
	 * @return
	 * @throws Exception
	 */
	public String addSqlserver_nmslockinfo_v(String serverip, Hashtable lockinfo) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		StringBuffer sBuffer = new StringBuffer();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);

			sBuffer.append("insert into nms_sqlserverlockinfo_v(serverip, rsc_text, rsc_dbid, ");
			sBuffer.append("dbname, rsc_indid, rsc_objid,rsc_type,rsc_flag,req_mode,req_status,req_refcnt,req_cryrefcnt,");
			sBuffer.append("req_lifetime,req_spid,req_ecid,req_ownertype,req_transactionID,mon_time)");
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
			if (SystemConstant.DBType.equals("mysql")) {
				sBuffer.append("','");
				sBuffer.append(montime);
				sBuffer.append("')");
			} else if (SystemConstant.DBType.equals("oracle")) {
				sBuffer.append("',to_date('" + montime + "','yyyy-mm-dd hh24:mi:ss'))");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return sBuffer.toString();
	}
}
