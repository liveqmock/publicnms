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
import com.afunms.application.model.OracleTopSqlReadWrite;
import com.afunms.application.model.OracleTopSqlSort;
import com.afunms.application.util.IpTranslation;
import com.afunms.common.util.EncryptUtil;
import com.afunms.common.util.OracleJdbcUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SystemConstant;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.gatherdb.GathersqlListManager;

/**
 * Oracle 最浪费内存的前10个语句 采集 使用JDBC采集
 * 
 * @author wupinlong 2012/09/17
 * 
 */
public class OracleTopSqlSnmp extends SnmpMonitor {

	public OracleTopSqlSnmp() {
	}

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
				String sqlTsql = "select sql_text,pct_bufgets,username from (select rank() over(order by disk_reads desc) as rank_bufgets,"
						+ "to_char(100 * ratio_to_report(disk_reads) over(), '999.99') pct_bufgets,sql_text,b.username as username from"
						+ " v$sqlarea,dba_users b where b.user_id = PARSING_USER_ID) where rank_bufgets < 11";
				ResultSet rs = null;
				try {
					// 最浪费内存的前10个语句
					Vector returnVal6 = new Vector();
					rs = util.stmt.executeQuery(sqlTsql);
					ResultSetMetaData rsmd6 = rs.getMetaData();
					while (rs.next()) {
						Hashtable return_value = new Hashtable();
						for (int i = 1; i <= rsmd6.getColumnCount(); i++) {
							String col = rsmd6.getColumnName(i);
							if (rs.getString(i) != null) {
								String tmp = rs.getString(i).toString();
								return_value.put(col.toLowerCase(), tmp);
							} else
								return_value.put(col.toLowerCase(), "--");
						}
						returnVal6.addElement(return_value);
						return_value = null;
					}
					returndata.put("topsql", returnVal6);
					rs.close();

					StringBuffer topReadAndWriteSql = new StringBuffer();
					topReadAndWriteSql
							.append(" SELECT * FROM (SELECT sql_text sqltext,disk_reads totaldisk ,executions totalexec, ");
					topReadAndWriteSql
							.append(" trunc(disk_reads/executions,2) diskreads FROM v$sql WHERE executions>0 ");
					topReadAndWriteSql
							.append(" AND is_obsolete='N' ORDER BY 4 desc) WHERE ROWNUM<11");

					Vector<OracleTopSqlReadWrite> topSqlReadWriteVector = new Vector<OracleTopSqlReadWrite>();
					rs = util.stmt.executeQuery(topReadAndWriteSql.toString());
					while (rs.next()) {
						OracleTopSqlReadWrite oracleTopSqlReadWrite = new OracleTopSqlReadWrite();
						oracleTopSqlReadWrite.setSqltext(rs
								.getString("sqltext"));
						oracleTopSqlReadWrite.setTotaldisk(rs
								.getString("totaldisk"));
						oracleTopSqlReadWrite.setDiskreads(rs
								.getString("diskreads"));
						oracleTopSqlReadWrite.setTotalexec(rs
								.getString("totalexec"));
						topSqlReadWriteVector.add(oracleTopSqlReadWrite);
						oracleTopSqlReadWrite = null;
					}
					rs.close();
					returndata.put("topSqlReadWriteVector",
							topSqlReadWriteVector);

					StringBuffer topSortSql = new StringBuffer();
					topSortSql
							.append(" SELECT sql_text sqltext, sorts,executions,trunc(\"Sorts/Exec\",2) sortsexec  ");
					topSortSql
							.append(" FROM (SELECT SQL_TEXT, SORTS, EXECUTIONS, SORTS/EXECUTIONS \"Sorts/Exec\" ");
					topSortSql.append(" FROM V$SQLAREA ");
					topSortSql.append(" WHERE EXECUTIONS>0 ");
					topSortSql.append(" AND SORTS>0 ");
					topSortSql.append(" ORDER BY ");
					topSortSql.append(" 4 DESC) ");
					topSortSql.append(" WHERE ROWNUM<11 ");
					Vector<OracleTopSqlSort> topSqlSortVector = new Vector<OracleTopSqlSort>();
					rs = null;
					try {
						rs = util.stmt.executeQuery(topSortSql.toString());
						while (rs.next()) {
							OracleTopSqlSort oracleTopSqlSort = new OracleTopSqlSort();
							oracleTopSqlSort
									.setSqltext(rs.getString("sqltext"));
							oracleTopSqlSort.setExecutions(rs
									.getString("executions"));
							oracleTopSqlSort.setSorts(rs.getString("sorts"));
							oracleTopSqlSort.setSortsexec(rs
									.getString("sortsexec"));
							topSqlSortVector.add(oracleTopSqlSort);
							oracleTopSqlSort = null;
						}
					} catch (Exception e) {

					} finally {
						if (rs != null)
							rs.close();
					}
					returndata.put("topSqlSortVector", topSqlSortVector);

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
				oracleHash.put("sql_v", returndata.get("topsql"));
				oracleHash.put("oracleTopSqlReadWriteVector", returndata
						.get("topSqlReadWriteVector"));
				oracleHash.put("oracleTopSqlSortVector", returndata
						.get("topSqlSortVector"));
			}

			// ----------------------------------保存到数据库及告警 start

			String hex = IpTranslation
					.formIpToHex(dbmonitorlist.getIpAddress());
			serverip = hex + ":" + dbmonitorlist.getId(); // serverip转换

			try {
				Calendar tempCal = Calendar.getInstance();
				Date cc = tempCal.getTime();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String montime = sdf.format(cc);

				// topsql
				Vector sql_v = (Vector) returndata.get("topsql");
				if (sql_v != null && sql_v.size() > 0) {
					String sql_text = null;
					String sql = null;

					String deletesql = "delete from nms_oratopsql where serverip='"
							+ serverip + "'";
					GathersqlListManager.Addsql(deletesql);
					for (int k = 0; k < sql_v.size(); k++) {
						Hashtable ht = (Hashtable) sql_v.get(k);
						sql_text = ht.get("sql_text").toString();
						String pct_bufgets = ht.get("pct_bufgets").toString();
						String usernames = ht.get("username").toString();
						if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
							sql_text = sql_text.replaceAll("\\\\", "/");
							sql_text = sql_text.replaceAll("\"", "\'");
						} else if ("oracle"
								.equalsIgnoreCase(SystemConstant.DBType)) {
							sql_text = sql_text.replaceAll("\\\\", "/");
							sql_text = sql_text.replaceAll("\'", "''");
							sql_text = sql_text.replaceAll("\"", "''");
						}

						sql = "";
						sql = "insert into nms_oratopsql(serverip,sql_text,pct_bufgets,username,mon_time) "
								+ "values('" + serverip;
						if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
							sql = sql + "',\"" + sql_text + "\",'"
									+ pct_bufgets + "','" + usernames;
						} else if ("oracle"
								.equalsIgnoreCase(SystemConstant.DBType)) {
							sql = sql + "','" + sql_text + "','" + pct_bufgets
									+ "','" + usernames;
						}

						if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
							sql = sql + "','" + montime + "')";
						} else if ("oracle"
								.equalsIgnoreCase(SystemConstant.DBType)) {
							sql = sql + "',to_date('" + montime
									+ "','YYYY-MM-DD HH24:MI:SS'))";
						}
						GathersqlListManager.Addsql(sql);
					}
				}

				// topSqlReadWriteVector
				Vector<OracleTopSqlReadWrite> topSqlReadWriteVector = (Vector<OracleTopSqlReadWrite>) returndata
						.get("topSqlReadWriteVector");
				if (topSqlReadWriteVector != null
						&& topSqlReadWriteVector.size() > 0) {
					String deletesql = "delete from nms_oratopsql_readwrite where serverip='"
							+ serverip + "'";
					GathersqlListManager.Addsql(deletesql);

					String sqltext = null;
					StringBuffer insertsql = null;
					for (int i = 0; i < topSqlReadWriteVector.size(); i++) {
						OracleTopSqlReadWrite oracleTopSqlReadWrite = topSqlReadWriteVector
								.get(i);
						if (oracleTopSqlReadWrite != null) {
							sqltext = oracleTopSqlReadWrite.getSqltext();
							if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
								sqltext = sqltext.replaceAll("\\\\", "/");
								sqltext = sqltext.replaceAll("\'", "\\\\'");
								sqltext = sqltext.replaceAll("\"", "\\\\'");
							} else if ("oracle"
									.equalsIgnoreCase(SystemConstant.DBType)) {
								sqltext = sqltext.replaceAll("\\\\", "/");
								sqltext = sqltext.replaceAll("\'", "''");
								sqltext = sqltext.replaceAll("\"", "''");
							}

							insertsql = new StringBuffer();
							insertsql
									.append("insert into nms_oratopsql_readwrite(serverip,sqltext,totaldisk,totalexec,diskreads,mon_time) values ('");
							insertsql.append(serverip);
							insertsql.append("','");
							insertsql.append(sqltext);
							insertsql.append("','");
							insertsql.append(oracleTopSqlReadWrite
									.getTotaldisk());
							insertsql.append("','");
							insertsql.append(oracleTopSqlReadWrite
									.getTotalexec());
							insertsql.append("','");
							insertsql.append(oracleTopSqlReadWrite
									.getDiskreads());
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
					}
				}

				// topSqlSortVector
				Vector<OracleTopSqlSort> topSqlSortVector = (Vector<OracleTopSqlSort>) returndata
						.get("topSqlSortVector");
				if (topSqlSortVector != null && topSqlSortVector.size() > 0) {
					String deletesql = "delete from nms_oratopsql_sort where serverip='"
							+ serverip + "'";
					GathersqlListManager.Addsql(deletesql);

					String sqltext = "";
					StringBuffer insertsql = null;
					for (int i = 0; i < topSqlSortVector.size(); i++) {
						OracleTopSqlSort oracleTopSqlSort = topSqlSortVector
								.get(i);
						if (oracleTopSqlSort != null) {
							sqltext = oracleTopSqlSort.getSqltext();

							if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
								sqltext = sqltext.replaceAll("\\\\", "/");
								sqltext = sqltext.replaceAll("\'", "\\\\'");
								sqltext = sqltext.replaceAll("\"", "\\\\'");
							} else if ("oracle"
									.equalsIgnoreCase(SystemConstant.DBType)) {
								sqltext = sqltext.replaceAll("\\\\", "/");
								sqltext = sqltext.replaceAll("\'", "''");
								sqltext = sqltext.replaceAll("\"", "''");
							}

							insertsql = new StringBuffer();
							insertsql
									.append("insert into nms_oratopsql_sort(serverip,sqltext,sorts,executions,sortsexec,mon_time) values ('");
							insertsql.append(serverip);
							insertsql.append("','");
							insertsql.append(sqltext);
							insertsql.append("','");
							insertsql.append(oracleTopSqlSort.getSorts());
							insertsql.append("','");
							insertsql.append(oracleTopSqlSort.getExecutions());
							insertsql.append("','");
							insertsql.append(oracleTopSqlSort.getSortsexec());
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
					}
				}

				// ---------------------------------topsql不需告警

			} catch (Exception e) {
				e.printStackTrace();
			}

			// ----------------------------------保存到数据库及告警 end

		}
		return returndata;
	}

}
