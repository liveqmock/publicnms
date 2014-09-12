package com.afunms.polling.snmp.sqlserver;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.dao.DBDao;
import com.afunms.application.dao.SqldbconfigDao;
import com.afunms.application.model.DBVo;
import com.afunms.application.model.Sqldbconfig;
import com.afunms.application.util.IpTranslation;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SystemConstant;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.DBNode;
import com.gatherdb.GathersqlListManager;

public class collect_dbProxy {
	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicators) {
		Hashtable dbHashValue = new Hashtable(); // 存放db信息
		Hashtable retHashValue = new Hashtable();
		String htKey = "dbValue";
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
		String hexip = hex + ":" + dbmonitorlist.getAlias();

		if (sqlserverDataHash.get(serverip) == null) {
			sqlserverDataHash.put(serverip, new Hashtable());
		}
		Hashtable sqlserverdata = (Hashtable) sqlserverDataHash.get(serverip);

		String[] args = new String[] { "serverip", "usedperc", "usedsize", "size", "logname", "dbname", "instance_name", "label", "mon_time" };
		retHashValue = LogParser.parse(this, dbmonitorlist, htKey, args);
		String label = null;
		String insertsql = null;
		Vector extent_v = (Vector) retHashValue.get(htKey);
		Hashtable retdatabase = new Hashtable();
		Hashtable retlogfile = new Hashtable();
		Vector names = new Vector();
		for (int k = 0; k < extent_v.size(); k++) {
			Hashtable ht = (Hashtable) extent_v.get(k);
			label = ht.get("label").toString();
			if ("0".equals(label)) {
				Hashtable l = new Hashtable();
				l.put("usedperc", ht.get("usedperc").toString());
				l.put("usedsize", ht.get("usedsize").toString());
				l.put("size", ht.get("size").toString());
				l.put("logname", ht.get("logname").toString());
				retlogfile.put(ht.get("logname").toString(), l);
				l = null;
			} else if ("1".equals(label)) {
				Hashtable d = new Hashtable();
				d.put("usedperc", ht.get("usedperc").toString());
				d.put("usedsize", ht.get("usedsize").toString());
				d.put("size", ht.get("size").toString());
				d.put("dbname", ht.get("dbname").toString());
				retdatabase.put(ht.get("dbname").toString(), d);
				d = null;
			} else if ("2".equals(label)) {
				names.add(ht.get("instance_name").toString());
			}
		}
		dbHashValue.put("database", retdatabase);
		dbHashValue.put("logfile", retlogfile);
		dbHashValue.put("names", names);
		// 写入内存
		sqlserverdata.put("dbValue", dbHashValue);
		ShareData.setSqldbdata(serverip, dbHashValue);

		// 存入数据库
		try {
			String deletesql = "delete from nms_sqlserverdbvalue where serverip = '" + hexip + "'";
			GathersqlListManager.Addsql(deletesql);
			Hashtable logfile = (Hashtable) dbHashValue.get("logfile");
			Hashtable database = (Hashtable) dbHashValue.get("database");
			names = (Vector) dbHashValue.get("names");
			Iterator iter = logfile.entrySet().iterator();
			label = "0";
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				Hashtable val = (Hashtable) entry.getValue();
				insertsql = addSqlserver_nmsdbvalue(hexip, val, "", label);
				GathersqlListManager.Addsql(insertsql);
			}
			label = "1";
			iter = database.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				Hashtable val = (Hashtable) entry.getValue();
				insertsql = addSqlserver_nmsdbvalue(hexip, val, "", label);
				GathersqlListManager.Addsql(insertsql);
			}
			label = "2";
			for (int i = 0; i < names.size(); i++) {
				insertsql = addSqlserver_nmsdbvalue(hexip, null, String.valueOf(names.get(i)), label);
				GathersqlListManager.Addsql(insertsql);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 告警
		NodeUtil nodeUtil = new NodeUtil();
		NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(dbmonitorlist);
		checkToAlarm(nodeDTO, dbHashValue);

		return sqlserverDataHash;
	}

	public void checkToAlarm(NodeDTO nodeDTO, Hashtable dbValue) {
		AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
		List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(nodeDTO.getId() + "", nodeDTO.getType(), nodeDTO.getSubtype());
		CheckEventUtil checkEventUtil = new CheckEventUtil();
		for (int i = 0; i < list.size(); i++) {
			AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode) list.get(i);
			if ("tablespace".equalsIgnoreCase(alarmIndicatorsNode.getName())) {

				if (dbValue != null && dbValue.size() > 0) {
					SqldbconfigDao sqldbconfigManager = new SqldbconfigDao();
					Hashtable alarmdbs = sqldbconfigManager.getByAlarmflag(1);
					sqldbconfigManager.close();
					Hashtable database = (Hashtable) dbValue.get("database");
					Hashtable logfile = (Hashtable) dbValue.get("logfile");
					Vector names = (Vector) dbValue.get("names");
					if (alarmdbs == null)
						alarmdbs = new Hashtable();
					if (database == null)
						database = new Hashtable();
					if (logfile == null)
						logfile = new Hashtable();
					if (names != null && names.size() > 0) {
						for (int k = 0; k < names.size(); k++) {
							String dbname = (String) names.get(k);
							if (database.get(dbname) != null) {
								Hashtable db = (Hashtable) database.get(dbname);
								String usedperc = (String) db.get("usedperc");
								if (alarmdbs.containsKey(nodeDTO.getIpaddress() + ":" + dbname + ":0")) {
									Sqldbconfig sqldbconfig = (Sqldbconfig) alarmdbs.get(nodeDTO.getIpaddress() + ":" + dbname + ":0");
									if (sqldbconfig == null)
										continue;
									if (usedperc == null)
										continue;
									alarmIndicatorsNode.setLimenvalue0(sqldbconfig.getAlarmvalue() + "");
									alarmIndicatorsNode.setLimenvalue1(sqldbconfig.getAlarmvalue() + "");
									alarmIndicatorsNode.setLimenvalue2(sqldbconfig.getAlarmvalue() + "");
									checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, usedperc + "", sqldbconfig.getDbname());
								}
							}
							if (logfile.get(dbname) != null) {
								Hashtable db = (Hashtable) logfile.get(dbname);
								String usedperc = (String) db.get("usedperc");
								if (alarmdbs.containsKey(nodeDTO.getIpaddress() + ":" + dbname + ":0")) {
									Sqldbconfig sqldbconfig = (Sqldbconfig) alarmdbs.get(nodeDTO.getIpaddress() + ":" + dbname + ":1");
									if (sqldbconfig == null)
										continue;
									alarmIndicatorsNode.setLimenvalue0(sqldbconfig.getAlarmvalue() + "");
									alarmIndicatorsNode.setLimenvalue1(sqldbconfig.getAlarmvalue() + "");
									alarmIndicatorsNode.setLimenvalue2(sqldbconfig.getAlarmvalue() + "");
									checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, usedperc + "", sqldbconfig.getDbname());
								}
							}
						}
					}
				}

			}
		}
	}

	public String addSqlserver_nmsdbvalue(String serverip, Hashtable scans, String instance_name, String label) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		StringBuffer sBuffer = new StringBuffer();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);

			if (!"2".equals(label)) {
				sBuffer.append("insert into nms_sqlserverdbvalue(serverip, usedperc, usedsize, " + "sizes, logname, dbname,label,mon_time)");
				sBuffer.append(" values('");
				sBuffer.append(serverip);
				sBuffer.append("','");
				sBuffer.append(String.valueOf(scans.get("usedperc")));
				sBuffer.append("','");
				sBuffer.append(String.valueOf(scans.get("usedsize")));
				sBuffer.append("','");
				sBuffer.append(String.valueOf(scans.get("size")));
				sBuffer.append("','");
				sBuffer.append(String.valueOf(scans.get("logname")));
				sBuffer.append("','");
				sBuffer.append(String.valueOf(scans.get("dbname")));
				sBuffer.append("','");
				sBuffer.append(label);
				if (SystemConstant.DBType.equals("mysql")) {
					sBuffer.append("','");
					sBuffer.append(montime);
					sBuffer.append("')");
				} else if (SystemConstant.DBType.equals("oracle")) {
					sBuffer.append("',to_date('" + montime + "','yyyy-mm-dd hh24:mi:ss'))");
				}
			} else {
				sBuffer = new StringBuffer();
				sBuffer.append("insert into nms_sqlserverdbvalue(serverip,instance_name,label,mon_time)");
				sBuffer.append(" values('");
				sBuffer.append(serverip);
				sBuffer.append("','");
				sBuffer.append(instance_name);
				sBuffer.append("','");
				sBuffer.append(label);
				if (SystemConstant.DBType.equals("mysql")) {
					sBuffer.append("','");
					sBuffer.append(montime);
					sBuffer.append("')");
				} else if (SystemConstant.DBType.equals("oracle")) {
					sBuffer.append("',to_date('" + montime + "','yyyy-mm-dd hh24:mi:ss'))");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return sBuffer.toString();
	}
}
