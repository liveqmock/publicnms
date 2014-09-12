package com.afunms.polling.snmp.sqlserver;

import java.util.Hashtable;
import java.util.Vector;

import com.afunms.application.dao.DBDao;
import com.afunms.application.model.DBVo;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SystemConstant;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.DBNode;
import com.gatherdb.GathersqlListManager;

public class collect_sysvalueProxy {
	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicators) {

		Hashtable sys_hash = new Hashtable();
		String htKey = "sysValue";
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
		if (sqlserverDataHash.get(serverip) == null) {
			sqlserverDataHash.put(serverip, new Hashtable());
		}
		Hashtable sqlserverdata = (Hashtable) sqlserverDataHash.get(serverip);
		Hashtable returndata = new Hashtable();
		String[] args = new String[] { "serverip", "productlevel", "version", "machinename", "issingleuser", "processid", "isintegratedsecurityonly", "isclustered", "mon_time" };
		returndata = LogParser.parse(this, dbmonitorlist, htKey, args);
		sys_hash = (Hashtable) ((Vector) returndata.get(htKey)).get(0);
		sqlserverdata.put("sysValue", sys_hash);

		// ¥Ê»Î ˝æ›ø‚
		try {
			String deletesql = "delete from nms_sqlserversysvalue where serverip ='" + serverip + "'";
			GathersqlListManager.Addsql(deletesql);
			String insertsql = addSqlserver_nmssysvalue(serverip, sys_hash);
			GathersqlListManager.Addsql(insertsql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sqlserverDataHash;
	}

	public String addSqlserver_nmssysvalue(String serverip, Hashtable sysvalue) throws Exception {
		StringBuffer sBuffer = new StringBuffer();
		try {

			sBuffer.append("insert into nms_sqlserversysvalue(serverip, productlevel, version, " + "machinename, issingleuser, processid,isintegratedsecurityonly,isclustered,mon_time)");
			sBuffer.append(" values('");
			sBuffer.append(serverip);
			sBuffer.append("','");
			sBuffer.append(String.valueOf(sysvalue.get("productlevel")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(sysvalue.get("version")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(sysvalue.get("machinename")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(sysvalue.get("issingleuser")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(sysvalue.get("processid")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(sysvalue.get("isintegratedsecurityonly")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(sysvalue.get("isclustered")));
			if (SystemConstant.DBType.equals("mysql")) {
				sBuffer.append("','");
				sBuffer.append(String.valueOf(sysvalue.get("mon_time")));
				sBuffer.append("')");
			} else if (SystemConstant.DBType.equals("oracle")) {
				sBuffer.append("',to_date('" + String.valueOf(sysvalue.get("mon_time")) + "','yyyy-mm-dd hh24:mi:ss'))");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return sBuffer.toString();
	}

}
