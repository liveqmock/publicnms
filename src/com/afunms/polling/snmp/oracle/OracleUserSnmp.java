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
import com.afunms.application.util.IpTranslation;
import com.afunms.common.util.EncryptUtil;
import com.afunms.common.util.OracleJdbcUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SystemConstant;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.gatherdb.GathersqlListManager;

/**
 * Oracle userinfo 采集 使用JDBC采集
 * 
 * @author wupinlong 2012/09/17
 * 
 */
public class OracleUserSnmp extends SnmpMonitor {

	public OracleUserSnmp() {
	}

	@SuppressWarnings("unchecked")
	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicators) {
		Hashtable returndata = new Hashtable();
		List dbmonitorlists = new ArrayList();
		dbmonitorlists = ShareData.getDBList();
		Hashtable userhash = null;
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

				// 数据库用户信息
				Hashtable r_hash = new Hashtable();
				Vector returnVal_0 = new Vector();
				Vector returnVal_1 = new Vector();
				Vector returnVal_2 = new Vector();
				String sql1 = "Select USER# from v$session where type = 'USER' and username <>'null'";// 当前活动的用户
				String sql2 = "select USERNAME,USER_ID,ACCOUNT_STATUS from dba_users";// 所有用户
				String sql3 = "Select a.PARSING_USER_ID,sum(a.CPU_TIME),sum(a.SORTS),sum(a.BUFFER_GETS),sum(a.RUNTIME_MEM),sum(a.VERSION_COUNT),sum(a.DISK_READS) "
						+ "from v$sqlarea a,dba_users b where a.PARSING_USER_ID = b.user_id group by PARSING_USER_ID";

				ResultSet rs = null;
				try {
					rs = util.stmt.executeQuery(sql3);
					ResultSetMetaData rsmd11 = null;
					if (rs.next()) {
						rsmd11 = rs.getMetaData();
					}
					while (rs.next()) {
						Hashtable return_value = new Hashtable();
						for (int i = 1; i <= rsmd11.getColumnCount(); i++) {
							String col = rsmd11.getColumnName(i);
							if (rs.getString(i) != null) {
								String tmp = rs.getString(i).toString();
								return_value.put(col.toLowerCase(), tmp);
							} else
								return_value.put(col.toLowerCase(), "--");
						}
						returnVal_0.addElement(return_value);
						return_value = null;
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (rs != null)
						rs.close();
				}
				r_hash.put("returnVal0", returnVal_0);

				rs = null;
				ResultSetMetaData rsmd12 = null;
				try {
					rs = util.stmt.executeQuery(sql1);
					if (rs.next()) {
						rsmd12 = rs.getMetaData();
					}
					while (rs.next()) {
						Hashtable return_value = new Hashtable();
						for (int i = 1; i <= rsmd12.getColumnCount(); i++) {
							String col = rsmd12.getColumnName(i);
							if (rs.getString(i) != null) {
								String tmp = rs.getString(i).toString();
								return_value.put(col.toLowerCase(), tmp);
							} else
								return_value.put(col.toLowerCase(), "--");
						}
						returnVal_1.addElement(return_value);
						return_value = null;
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (rs != null)
						rs.close();
				}
				r_hash.put("returnVal1", returnVal_1);

				rs = null;
				try {
					rs = util.stmt.executeQuery(sql2);
					ResultSetMetaData rsmd13 = null;
					if (rs.next()) {
						rsmd13 = rs.getMetaData();
					}
					while (rs.next()) {
						Hashtable return_value = new Hashtable();
						for (int i = 1; i <= rsmd13.getColumnCount(); i++) {
							String col = rsmd13.getColumnName(i);
							if (rs.getString(i) != null) {
								String tmp = rs.getString(i).toString();
								return_value.put(col.toLowerCase(), tmp);
							} else
								return_value.put(col.toLowerCase(), "--");
						}
						returnVal_2.addElement(return_value);
						return_value = null;
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (rs != null)
						rs.close();
				}
				r_hash.put("returnVal2", returnVal_2);
				returndata.put("user", r_hash);

				if (userhash != null) {
					returndata.put("user", userhash);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
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
				oracleHash.put("userinfo_h", returndata.get("user"));
			}

			// 保存至数据库中
			Hashtable data = (Hashtable) returndata.get("user");
			if (data != null && !data.isEmpty()) {
				String hex = IpTranslation.formIpToHex(dbmonitorlist
						.getIpAddress());
				serverip = hex + ":" + dbmonitorlist.getId();

				try {
					Calendar tempCal = Calendar.getInstance();
					Date cc = tempCal.getTime();
					SimpleDateFormat sdf = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					String montime = sdf.format(cc);
					String deletesql = "delete from nms_orauserinfo where serverip='"
							+ serverip + "'";
					// 加入队列
					GathersqlListManager.Addsql(deletesql);

					Vector returnVal = null;
					Vector returnVal1 = null;
					Vector returnVal2 = null;
					if (data != null) {
						returnVal = (Vector) data.get("returnVal0");
						returnVal1 = (Vector) data.get("returnVal1");
						returnVal2 = (Vector) data.get("returnVal2");
					}
					StringBuffer sBuffer = null;
					try {
						// label:0 只插入returnVal的信息 label:1 只插入returnVal1的信息
						// label:2只插入returnVal2的信息
						String label = "0";
						if (returnVal != null && returnVal.size() > 0) {
							for (int i = 0; i < returnVal.size(); i++) {
								Hashtable returnHash = (Hashtable) returnVal
										.get(i);
								sBuffer = new StringBuffer();
								sBuffer = new StringBuffer();
								sBuffer
										.append("insert into nms_orauserinfo(serverip, parsing_user_id, cpu_time, sorts, buffer_gets, "
												+ "runtime_mem, version_count, disk_reads,label, mon_time) ");
								sBuffer.append(" values('");
								sBuffer.append(serverip);
								sBuffer.append("','");
								sBuffer.append(String.valueOf(returnHash
										.get("parsing_user_id")));
								sBuffer.append("','");
								sBuffer.append(String.valueOf(returnHash
										.get("sum(a.cpu_time)")));
								sBuffer.append("','");
								sBuffer.append(String.valueOf(returnHash
										.get("sum(a.sorts)")));
								sBuffer.append("','");
								sBuffer.append(String.valueOf(returnHash
										.get("sum(a.buffer_gets)")));
								sBuffer.append("','");
								sBuffer.append(String.valueOf(returnHash
										.get("sum(a.runtime_mem)")));
								sBuffer.append("','");
								sBuffer.append(String.valueOf(returnHash
										.get("sum(a.version_count)")));
								sBuffer.append("','");
								sBuffer.append(String.valueOf(returnHash
										.get("sum(a.disk_reads)")));
								sBuffer.append("','");
								sBuffer.append(label);
								if ("mysql"
										.equalsIgnoreCase(SystemConstant.DBType)) {
									sBuffer.append("','");
									sBuffer.append(montime);
									sBuffer.append("')");
								} else if ("oracle"
										.equalsIgnoreCase(SystemConstant.DBType)) {
									sBuffer.append("',");
									sBuffer.append("to_date('" + montime
											+ "','YYYY-MM-DD HH24:MI:SS')");
									sBuffer.append(")");
								}
								GathersqlListManager.Addsql(sBuffer.toString());
							}
						}

						label = "1";
						if (returnVal1 != null && returnVal1.size() > 0) {
							for (int i = 0; i < returnVal1.size(); i++) {
								sBuffer = new StringBuffer();
								Hashtable returnHash = (Hashtable) returnVal1
										.get(i);
								sBuffer = new StringBuffer();
								sBuffer
										.append("insert into nms_orauserinfo(serverip, users,label, mon_time) ");
								sBuffer.append(" values('");
								sBuffer.append(serverip);
								sBuffer.append("','");
								sBuffer.append(String.valueOf(returnHash
										.get("user#")));
								sBuffer.append("','");
								sBuffer.append(label);
								if ("mysql"
										.equalsIgnoreCase(SystemConstant.DBType)) {
									sBuffer.append("','");
									sBuffer.append(montime);
									sBuffer.append("')");
								} else if ("oracle"
										.equalsIgnoreCase(SystemConstant.DBType)) {
									sBuffer.append("',");
									sBuffer.append("to_date('" + montime
											+ "','YYYY-MM-DD HH24:MI:SS')");
									sBuffer.append(")");
								}
								GathersqlListManager.Addsql(sBuffer.toString());
							}
						}
						label = "2";
						if (returnVal2 != null && returnVal2.size() > 0) {
							for (int i = 0; i < returnVal2.size(); i++) {
								sBuffer = new StringBuffer();
								Hashtable returnHash = (Hashtable) returnVal2
										.get(i);
								sBuffer = new StringBuffer();
								sBuffer
										.append("insert into nms_orauserinfo(serverip,username, user_id, account_status,label, mon_time) ");
								sBuffer.append(" values('");
								sBuffer.append(serverip);
								sBuffer.append("','");
								sBuffer.append(String.valueOf(returnHash
										.get("username")));
								sBuffer.append("','");
								sBuffer.append(String.valueOf(returnHash
										.get("user_id")));
								sBuffer.append("','");
								sBuffer.append(String.valueOf(returnHash
										.get("account_status")));
								sBuffer.append("','");
								sBuffer.append(label);
								if ("mysql"
										.equalsIgnoreCase(SystemConstant.DBType)) {
									sBuffer.append("','");
									sBuffer.append(montime);
									sBuffer.append("')");
								} else if ("oracle"
										.equalsIgnoreCase(SystemConstant.DBType)) {
									sBuffer.append("',");
									sBuffer.append("to_date('" + montime
											+ "','YYYY-MM-DD HH24:MI:SS')");
									sBuffer.append(")");
								}
								GathersqlListManager.Addsql(sBuffer.toString());
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					// ---------------------------------userinfo不需告警

				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		}
		return returndata;
	}

}
