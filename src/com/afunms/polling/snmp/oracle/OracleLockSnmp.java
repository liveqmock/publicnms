package com.afunms.polling.snmp.oracle;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.application.model.DBVo;
import com.afunms.application.model.OracleLockInfo;
import com.afunms.application.util.IpTranslation;
import com.afunms.common.util.EncryptUtil;
import com.afunms.common.util.OracleJdbcUtil;
import com.afunms.common.util.ReflactUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SystemConstant;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.gatherdb.GathersqlListManager;

/**
 * Oracle 锁信息 采集 使用JDBC采集
 * 
 * @author wupinlong 2012/09/17
 * 
 */
public class OracleLockSnmp extends SnmpMonitor {

	public OracleLockSnmp() {
	}

	/**
	 * @param nodeGatherIndicators
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicators) {
		Hashtable returndata = new Hashtable();
		List dbmonitorlists = new ArrayList();
		dbmonitorlists = ShareData.getDBList();
		DBVo dbmonitorlist = new DBVo();
		if (dbmonitorlists != null && dbmonitorlists.size() > 0) {
			for (int i = 0; i < dbmonitorlists.size(); i++) {
				DBVo vo = (DBVo) dbmonitorlists.get(i);
				if (vo.getId() == Integer.parseInt(nodeGatherIndicators
						.getNodeid())) {
					dbmonitorlist = vo;
					break;
				}
			}
		}
		if (dbmonitorlist != null) {
			if (dbmonitorlist.getManaged() == 0) {
				// 如果未被管理，不采集，user信息为空
				return returndata;
			}
			String serverip = dbmonitorlist.getIpAddress();
			int port = Integer.parseInt(dbmonitorlist.getPort());
			OracleJdbcUtil util = null;
			try {
				String dburl = "jdbc:oracle:thin:@" + serverip + ":" + port
						+ ":" + dbmonitorlist.getDbName();
				util = new OracleJdbcUtil(dburl, dbmonitorlist.getUser(),
						EncryptUtil.decode(dbmonitorlist.getPassword()));
				util.jdbc();

				// *********************************取数据 start
				ResultSet rs = null;
				try {
					// 锁信息
					Vector returnVal4 = new Vector();
					Vector returnval_1 = new Vector();
					Vector returnval_2 = new Vector();
					String sql_1 = "select distinct substr(s.username,1,18) username,s.sid as sid,s.status status,s.MACHINE machine,"
							+ "s.type sessiontype,to_char(s.LOGON_TIME,'yyyy-mm-dd hh24-mi-ss') logontime,"
							+ "substr(s.program,1,15) program from v$session s";
					String sql_2 = "select a.sid as sid,a.type as locktype,a.lmode as lmode,a.request as request  from v$lock a";

					rs = null;
					try {
						rs = util.stmt.executeQuery(sql_1);
						ResultSetMetaData rsmd_1 = null;
						if (rs.next()) {
							rsmd_1 = rs.getMetaData();
						}
						while (rs.next()) {
							Hashtable return_value = new Hashtable();
							for (int i = 1; i <= rsmd_1.getColumnCount(); i++) {
								String col = rsmd_1.getColumnName(i);
								if (rs.getString(i) != null) {
									String tmp = rs.getString(i).toString();
									return_value.put(col.toLowerCase(), tmp);
								} else
									return_value.put(col.toLowerCase(), "--");
							}
							returnval_1.addElement(return_value);
							return_value = null;
						}
					} catch (Exception e) {

					} finally {
						if (rs != null)
							rs.close();
					}

					rs = null;
					try {
						rs = util.stmt.executeQuery(sql_2);
						ResultSetMetaData rsmd_2 = null;
						if (rs.next()) {
							rsmd_2 = rs.getMetaData();
						}
						while (rs.next()) {
							Hashtable return_value = new Hashtable();
							for (int i = 1; i <= rsmd_2.getColumnCount(); i++) {
								String col = rsmd_2.getColumnName(i);
								if (rs.getString(i) != null) {
									String tmp = rs.getString(i).toString();
									return_value.put(col.toLowerCase(), tmp);
								} else
									return_value.put(col.toLowerCase(), "--");
							}
							returnval_2.addElement(return_value);
							return_value = null;
						}
					} catch (Exception e) {

					} finally {
						if (rs != null)
							rs.close();
					}

					for (int i = 0; i < returnval_1.size(); i++) {
						Hashtable return_value = new Hashtable();
						Hashtable return_value1 = new Hashtable();
						return_value1 = (Hashtable) returnval_1.get(i);
						for (int j = 0; j < returnval_2.size(); j++) {
							Hashtable return_value2 = new Hashtable();
							return_value2 = (Hashtable) returnval_2.get(j);
							if (return_value2.get("sid").equals(
									return_value1.get("sid"))) {
								return_value.put("username", return_value1
										.get("username"));
								return_value.put("status", return_value1
										.get("status"));
								return_value.put("machine", return_value1
										.get("machine"));
								return_value.put("sessiontype", return_value1
										.get("sessiontype"));
								return_value.put("logontime", return_value1
										.get("logontime"));
								return_value.put("program", return_value1
										.get("program"));

								return_value.put("locktype", return_value2
										.get("locktype"));
								return_value.put("lmode", return_value2
										.get("lmode"));
								return_value.put("request", return_value2
										.get("request"));

								returnVal4.add(return_value);
								break;
							}
						}
					}
					returndata.put("lock", returnVal4);

					OracleLockInfo oracleLockInfo = new OracleLockInfo();
					StringBuffer lockSqlBuffer = new StringBuffer();
					lockSqlBuffer
							.append(" select currentsessioncount,useablesessioncount,  trunc(currentsessioncount*100/useablesessioncount,2)  useablesessionpercent,  lockdsessioncount , ");
					lockSqlBuffer
							.append(" lockwaitcount,deadlockcount,processcount,maxprocesscount,rollbacks,trunc(rollbacks*100/commits,5) rollbackcommitpercent   ");
					lockSqlBuffer
							.append(" from (select count(*) currentsessioncount from v$session) table1, ");
					lockSqlBuffer
							.append(" (SELECT display_value useablesessioncount FROM V$PARAMETER WHERE NAME = 'sessions') table2, ");
					lockSqlBuffer
							.append(" (select count(*) lockdsessioncount from v$locked_object t1,v$session t2,v$sqltext t3 ");
					lockSqlBuffer
							.append(" where t1.session_id=t2.sid and t2.sql_address=t3.address) table3, ");
					lockSqlBuffer.append(" (select count(*) lockwaitcount   ");
					lockSqlBuffer.append(" from v$session a, v$lock b  ");
					lockSqlBuffer
							.append(" where a.lockwait = b.kaddr ) table4,  ");
					lockSqlBuffer.append(" (select count(*) deadlockcount  ");
					lockSqlBuffer
							.append(" from v$locked_object t1,v$session t2,v$sqltext t3  ");
					lockSqlBuffer
							.append(" where t1.session_id=t2.sid and t2.sql_address=t3.address) table5,  ");
					lockSqlBuffer
							.append(" (select count(*) processcount from v$process) table6,  ");
					lockSqlBuffer
							.append(" (select value maxprocesscount from v$parameter where name = 'processes') table7,  ");
					lockSqlBuffer
							.append(" (select sum(shrinks) rollbacks from v$rollstat, v$rollname where v$rollstat.usn = v$rollname.usn) table8, ");
					lockSqlBuffer
							.append(" (select value  commits from v$sysstat where name = 'user commits') table9");
					rs = null;
					try {
						rs = util.stmt.executeQuery(lockSqlBuffer.toString());
						if (rs.next()) {
							oracleLockInfo.setDeadlockcount(rs
									.getString("deadlockcount"));
							oracleLockInfo.setLockwaitcount(rs
									.getString("lockwaitcount"));
							oracleLockInfo.setMaxprocesscount(rs
									.getString("maxprocesscount"));
							oracleLockInfo.setProcesscount(rs
									.getString("processcount"));
							oracleLockInfo.setCurrentsessioncount(rs
									.getString("currentsessioncount"));
							oracleLockInfo.setUseablesessioncount(rs
									.getString("useablesessioncount"));
							oracleLockInfo.setUseablesessionpercent(rs
									.getString("useablesessionpercent"));
							oracleLockInfo.setLockdsessioncount(rs
									.getString("lockdsessioncount"));
							oracleLockInfo.setRollbackcommitpercent(rs
									.getString("rollbackcommitpercent"));
							oracleLockInfo.setRollbacks(rs
									.getString("rollbacks"));
						}
					} catch (Exception e) {

					} finally {
						if (rs != null)
							rs.close();
					}
					returndata.put("oracleLockInfo", oracleLockInfo);

				} catch (Exception e) {
					e.printStackTrace();
				}
				// *********************************取数据 end
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				util.closeStmt();
				util.closeConn();
			}

			// 更新内存
			if (!(ShareData.getSharedata().containsKey(dbmonitorlist
					.getIpAddress()
					+ ":" + dbmonitorlist.getId()))) {
				ShareData.getSharedata().put(
						dbmonitorlist.getIpAddress() + ":"
								+ dbmonitorlist.getId(), returndata);
			} else {
				Hashtable oracleHash = (Hashtable) ShareData.getSharedata()
						.get(
								dbmonitorlist.getIpAddress() + ":"
										+ dbmonitorlist.getId());
				oracleHash.put("lockinfo_v", returndata.get("lock"));
				returndata.put("oracleLockInfo", returndata
						.get("oracleLockInfo"));
			}

			// ----------------------------------保存到数据库及告警 start
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			// lockinfo_v
			Vector lockinfo_v = (Vector) returndata.get("lock");
			if (lockinfo_v != null && !lockinfo_v.isEmpty()) {
				String hex = IpTranslation.formIpToHex(dbmonitorlist
						.getIpAddress());
				serverip = hex + ":" + dbmonitorlist.getId();

				try {
					Calendar tempCal = Calendar.getInstance();
					Date cc = tempCal.getTime();
					
					String montime = sdf.format(cc);

					String deletesql = "delete from nms_oralock where serverip='"
							+ serverip + "'";
					GathersqlListManager.Addsql(deletesql);

					String insertsql = null;
					for (int k = 0; k < lockinfo_v.size(); k++) {
						Hashtable ht = (Hashtable) lockinfo_v.get(k);
						String usernames = ht.get("username").toString().trim();
						String status = ht.get("status").toString().trim();
						String machine = ht.get("machine").toString().trim();
						String sessiontype = ht.get("sessiontype").toString()
								.trim();
						String logontime = ht.get("logontime").toString()
								.trim();
						String program = ht.get("program").toString().trim();
						String locktype = ht.get("locktype").toString().trim();
						String lmode = ht.get("lmode").toString().trim();
						String requeststr = ht.get("request").toString().trim();
						insertsql = "";
						insertsql = "insert into nms_oralock(serverip,username,status,machine,sessiontype,logontime,program,locktype,lmode,requeststr,mon_time) "
								+ "values('"
								+ serverip
								+ "','"
								+ usernames
								+ "','"
								+ status
								+ "','"
								+ machine
								+ "','"
								+ sessiontype
								+ "','"
								+ logontime
								+ "','"
								+ program
								+ "','"
								+ locktype
								+ "','"
								+ lmode
								+ "','" + requeststr;
						if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
							insertsql = insertsql + "','" + montime + "')";
						} else if ("oracle"
								.equalsIgnoreCase(SystemConstant.DBType)) {
							insertsql = insertsql + "',to_date('" + montime
									+ "','YYYY-MM-DD HH24:MI:SS'))";
						}
						GathersqlListManager.Addsql(insertsql);
					}
					// ---------------------------------lock不需告警
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			// oracleLockInfo
			OracleLockInfo oracleLockInfo = (OracleLockInfo) returndata
					.get("oracleLockInfo");
			if (oracleLockInfo != null) {
				String hex = IpTranslation.formIpToHex(dbmonitorlist
						.getIpAddress());
				serverip = hex + ":" + dbmonitorlist.getId();
				try {
					Calendar tempCal = Calendar.getInstance();
					Date cc = tempCal.getTime();
					String montime = sdf.format(cc);

					String deletesql = "delete from nms_oralockinfo where serverip='"
							+ serverip + "'";
					GathersqlListManager.Addsql(deletesql);
					String[] fields = { "lockwaitcount", "deadlockcount",
							"processcount", "maxprocesscount",
							"currentsessioncount", "useablesessioncount",
							"useablesessionpercent", "lockdsessioncount",
							"rollbackcommitpercent", "rollbacks" };
					StringBuffer insertsql = null;
					// 此处可以利用反射来插入对象...
					for (String field : fields) {
						String value = "--";
						Object obj = ReflactUtil.invokeGet(oracleLockInfo,
								field);
						if (obj != null && obj instanceof String) {
							value = (String) obj;
						}
						insertsql = new StringBuffer();
						insertsql
								.append("insert into nms_oralockinfo(serverip,entity,subentity,thevalue,mon_time) values ('");
						insertsql.append(serverip);
						insertsql.append("','");
						insertsql.append("lockinfo");
						insertsql.append("','");
						insertsql.append(field);
						insertsql.append("','");
						insertsql.append(value);
						if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
							insertsql.append("','");
							insertsql.append(montime);
							insertsql.append("')");
						} else if ("oracle"
								.equalsIgnoreCase(SystemConstant.DBType)) {
							insertsql.append("',");
							insertsql.append("to_date('" + montime
									+ "','YYYY-MM-DD HH24:MI:SS')");
							insertsql.append(")");
						}
						GathersqlListManager.Addsql(insertsql.toString());
					}
					// ---------------------------------oracleLockInfo不需告警
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// ----------------------------------保存到数据库及告警 end

		}
		return returndata;
	}

}
