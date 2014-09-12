package com.afunms.polling.snmp.oracle;

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
public class OracleTopSqlProxy extends SnmpMonitor {

	public OracleTopSqlProxy() {
	}

	@SuppressWarnings("unchecked")
	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicators) {
		Hashtable returndata = new Hashtable();
		String fileContent = "";
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
			fileContent = LogParser.getDataFromLogfile(this, dbmonitorlist);
			String[] args = new String[]{"sql_text","pct_bufgets","username","mon_time"};
			String htKey = "topsql";
			Hashtable returndata1 = LogParser.parse(fileContent, htKey, args);
			returndata.put(htKey, returndata1.get(htKey));
			
			args = new String[]{"sqltext","totaldisk","totalexec","diskreads","mon_time"};
			htKey = "topSqlReadWriteVector";
			Hashtable returndata2 = LogParser.parse(fileContent, htKey, args);
			returndata.put(htKey, returndata2.get(htKey));
			
			args = new String[]{"sqltext","sorts","executions","sortsexec","mon_time"};
			htKey = "topSqlSortVector";
			Hashtable returndata3 = LogParser.parse(fileContent, htKey, args);
			returndata.put(htKey, returndata3.get(htKey));
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
				oracleHash.put("oracleTopSqlReadWriteVector", returndata.get("topSqlReadWriteVector"));
				oracleHash.put("oracleTopSqlSortVector", returndata.get("topSqlSortVector"));
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
				Vector topSqlReadWriteVector = (Vector) returndata.get("topSqlReadWriteVector");
				if (topSqlReadWriteVector != null
						&& topSqlReadWriteVector.size() > 0) {
					String deletesql = "delete from nms_oratopsql_readwrite where serverip='"
							+ serverip + "'";
					GathersqlListManager.Addsql(deletesql);

					String sqltext = null;
					StringBuffer insertsql = null;
					for (int i = 0; i < topSqlReadWriteVector.size(); i++) {
						Hashtable ht = (Hashtable)topSqlReadWriteVector.get(i);
						if (ht != null) {
							sqltext = (String)ht.get("sqltext");
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
							insertsql.append((String)ht.get("totaldisk"));
							insertsql.append("','");
							insertsql.append((String)ht.get("totalexec"));
							insertsql.append("','");
							insertsql.append((String)ht.get("diskreads"));
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
				Vector topSqlSortVector = (Vector) returndata.get("topSqlSortVector");
				if (topSqlSortVector != null && topSqlSortVector.size() > 0) {
					String deletesql = "delete from nms_oratopsql_sort where serverip='"
							+ serverip + "'";
					GathersqlListManager.Addsql(deletesql);

					String sqltext = "";
					StringBuffer insertsql = null;
					for (int i = 0; i < topSqlSortVector.size(); i++) {
						Hashtable ht = (Hashtable)topSqlSortVector.get(i);
						if (ht != null) {
							sqltext = (String)ht.get("sqltext");

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
							insertsql.append((String)ht.get("sorts"));
							insertsql.append("','");
							insertsql.append((String)ht.get("executions"));
							insertsql.append("','");
							insertsql.append((String)ht.get("sortsexec"));
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
