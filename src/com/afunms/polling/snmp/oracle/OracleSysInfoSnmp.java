package com.afunms.polling.snmp.oracle;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.afunms.application.model.DBVo;
import com.afunms.application.util.IpTranslation;
import com.afunms.common.util.EncryptUtil;
import com.afunms.common.util.OracleJdbcUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SystemConstant;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.gatherdb.GathersqlListManager;

/**
 * Oracle 系统信息 采集 使用JDBC采集
 * 
 * @author wupinlong 2012/09/17
 * 
 */
public class OracleSysInfoSnmp extends SnmpMonitor {

	public OracleSysInfoSnmp() {
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
		Hashtable sys_hash = new Hashtable();
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
					// 数据库系统信息
					String sqlSys = "select * from v$instance";
					String sqlVer = "select * from v$version";
					String sqlCreateDate = "select created from v$database";
					try {
						//获取数据库创建日期
						try {
							rs = util.stmt.executeQuery(sqlCreateDate);
							//Vector bannerV = new Vector();
							while (rs.next()) {
								String createtime = rs.getDate("created")+ " " + rs.getTime("created");
								sys_hash.put("CREATEED_TIME",createtime); 
								break;
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							if (rs != null)
								rs.close();
						}
						
						rs = null;
						rs = util.stmt.executeQuery(sqlSys);
						while (rs.next()) {
							if (rs.getString(2) != null) {
								// 得到该数据库的实例
								sys_hash.put("INSTANCE_NAME", rs.getString(2)
										.toString());
								sys_hash.put("HOST_NAME", rs.getString(3)
										.toString());
								sys_hash.put("DBNAME", dbmonitorlist
										.getDbName());
								sys_hash.put("VERSION", rs.getString(4)
										.toString());
								sys_hash.put("STARTUP_TIME", rs.getDate(5)
										+ " " + rs.getTime(5));
								if (rs.getString(6).toString()
										.equalsIgnoreCase("open")) {
									sys_hash.put("STATUS", "打开");
								} else {
									sys_hash.put("STATUS", "关闭");
								}
								if (rs.getString(7).toString()
										.equalsIgnoreCase("stopped")) {
									sys_hash.put("ARCHIVER", "NOARCHIVERLOG");
								} else {
									sys_hash.put("ARCHIVER", "ARCHIVERLOG");
								}
								break;
							}
						}

					} catch (Exception e) {

					} finally {
						if (rs != null)
							rs.close();
					}

					rs = null;
					try {
						rs = util.stmt.executeQuery(sqlVer);
						Vector bannerV = new Vector();
						while (rs.next()) {
							bannerV.add(rs.getString("BANNER").toString());
						}
						sys_hash.put("BANNER", bannerV);
					} catch (Exception e) {

					} finally {
						if (rs != null)
							rs.close();
					}
					returndata.put("sysinfo", sys_hash);
					
					

					// 采集基本配置信息
					Hashtable<String, String> baseInfoHash = new Hashtable<String, String>();
					StringBuffer baseInfoSql = new StringBuffer();// 基本配置
					baseInfoSql.append(" SELECT name, value ");
					baseInfoSql.append(" FROM v$parameter ");
					baseInfoSql
							.append(" WHERE name in ('processes', 'sessions', 'cpu_count', 'control_files',  ");
					baseInfoSql
							.append(" 'compatible', 'db_files', 'db_files', 'log_checkpoint_interval', ");
					baseInfoSql
							.append(" 'db_create_file_dest', 'dml_locks', 'transactions', ");
					baseInfoSql
							.append(" 'transactions_per_rollback_segment', 'max_rollback_segments', ");
					baseInfoSql
							.append(" 'rollback_segments', 'undo_tablespace', ");
					baseInfoSql.append(" 'instance_name', 'service_names', ");
					baseInfoSql
							.append(" 'background_dump_dest', 'user_dump_dest', 'core_dump_dest', ");
					baseInfoSql.append(" 'db_name', 'open_cursors', ");
					baseInfoSql.append(" 'sort_area_size',  ");
					baseInfoSql.append(" 'statistic_level', ");
					baseInfoSql.append(" 'sga_max_size', ");
					baseInfoSql.append(" 'shared_pool_size', ");
					baseInfoSql.append(" 'log_buffer', ");
					baseInfoSql.append(" 'java_pool_size', ");
					baseInfoSql.append(" 'large_pool_size', ");
					baseInfoSql.append(" 'pga_aggregate_target', ");
					baseInfoSql
							.append(" 'db_block_size', 'db_block_buffers')  ");
					baseInfoSql.append(" OR name like 'log_archive_%' ");
					baseInfoSql.append(" OR name like 'db%cache_size' ");
					baseInfoSql.append(" UNION ALL ");
					baseInfoSql
							.append(" SELECT 'DB_DATA_CACHE_SIZE' name, TO_CHAR(GREATEST(v9o.value, v8b.value)) value ");
					baseInfoSql.append(" FROM (SELECT SUM(value) value ");
					baseInfoSql.append(" FROM v$parameter ");
					baseInfoSql.append(" WHERE name='db_cache_size' ");
					baseInfoSql.append(" OR name LIKE 'db__k_cache_size' ");
					baseInfoSql
							.append(" OR name LIKE 'db___k_cache_size') v9o, ");
					baseInfoSql.append(" (SELECT a.bufs*b.blksz value ");
					baseInfoSql
							.append(" FROM (SELECT value bufs FROM v$parameter WHERE name='db_block_buffers') a, ");
					baseInfoSql
							.append(" (SELECT value blksz FROM v$parameter WHERE name='db_block_size') b ) v8b ");
					baseInfoSql.append(" UNION ALL ");
					baseInfoSql.append(" SELECT parameter name, value ");
					baseInfoSql.append(" FROM v$nls_parameters ");
					baseInfoSql.append(" UNION ALL ");
					baseInfoSql.append(" SELECT 'DB_'||parameter name, value ");
					baseInfoSql.append(" FROM nls_database_parameters ");

					String keyString = "";
					String valueString = "";
					rs = null;
					try {
						rs = util.stmt.executeQuery(baseInfoSql.toString());
						while (rs.next()) {
							keyString = rs.getString(1);
							valueString = rs.getString(2);
							if (valueString == null) {
								valueString = "--";
							}
							baseInfoHash.put(keyString, valueString);
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (rs != null)
							rs.close();
					}
					returndata.put("baseInfoHash", baseInfoHash);
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
			if (!ShareData.getSharedata().containsKey(
					dbmonitorlist.getIpAddress() + ":" + dbmonitorlist.getId())) {
				ShareData.getSharedata().put(
						dbmonitorlist.getIpAddress() + ":"
								+ dbmonitorlist.getId(), returndata);
			} else {
				Hashtable oracleHash = (Hashtable) ShareData.getSharedata()
						.get(
								dbmonitorlist.getIpAddress() + ":"
										+ dbmonitorlist.getId());
				oracleHash.put("sysValue", returndata.get("sysinfo"));
				oracleHash.put("baseInfoHash", returndata.get("baseInfoHash"));
			}

			// ----------------------------------保存到数据库及告警 start
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			// sysinfo
			Hashtable sysValue = (Hashtable) returndata.get("sysinfo");
			if (sysValue != null && !sysValue.isEmpty()) {
				String hex = IpTranslation.formIpToHex(dbmonitorlist
						.getIpAddress());
				serverip = hex + ":" + dbmonitorlist.getId();

				try {
					Calendar tempCal = Calendar.getInstance();
					Date cc = tempCal.getTime();
					String montime = sdf.format(cc);

					String deletesql = "delete from nms_orasys where serverip='"
							+ serverip + "'";
					GathersqlListManager.Addsql(deletesql);

					String INSTANCE_NAME = sysValue.get("INSTANCE_NAME")
							.toString();
					// String HOST_NAME = sysValue.get("HOST_NAME").toString();
					String DBNAME = sysValue.get("DBNAME").toString();
					String VERSION = sysValue.get("VERSION").toString();
					String STARTUP_TIME = sysValue.get("STARTUP_TIME").toString();
					String CREATEED_TIME = sysValue.get("CREATEED_TIME").toString();
					String STATUS = sysValue.get("STATUS").toString();
					String ARCHIVER = sysValue.get("ARCHIVER").toString();
					String java_pool = "";
					if (sysValue.get("java_pool") != null) {
						java_pool = sysValue.get("java_pool").toString();
					}
					// Vector banners = (Vector) sysValue.get("BANNER");
					String insertsql = "insert into nms_orasys(serverip,INSTANCE_NAME,HOST_NAME,DBNAME,VERSION,STARTUP_TIME,created_time,status,ARCHIVER,BANNER,java_pool,mon_time) "
							+ "values('"
							+ serverip
							+ "','"
							+ INSTANCE_NAME
							+ "','"
							+ INSTANCE_NAME
							+ "','"
							+ DBNAME
							+ "','"
							+ VERSION
							+ "','"
							+ STARTUP_TIME
							+ "','"
							+ CREATEED_TIME
							+ "','"
							+ STATUS
							+ "','" + ARCHIVER + "','" + "" + "','" + java_pool;
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						insertsql = insertsql + "','" + montime + "')";
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						insertsql = insertsql + "',to_date('" + montime
								+ "','YYYY-MM-DD HH24:MI:SS'))";
					}
					GathersqlListManager.Addsql(insertsql);
					// ---------------------------------sysinfo不需告警

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			// baseInfoHash
			Hashtable<String, String> baseInfo = (Hashtable<String, String>) returndata
					.get("baseInfoHash");
			if (baseInfo != null && !baseInfo.isEmpty()) {
				String hex = IpTranslation.formIpToHex(dbmonitorlist
						.getIpAddress());
				serverip = hex + ":" + dbmonitorlist.getId();

				try {
					Calendar tempCal = Calendar.getInstance();
					Date cc = tempCal.getTime();
					String montime = sdf.format(cc);

					String deletesql = "delete from nms_orabaseinfo where serverip='"
							+ serverip + "'";
					GathersqlListManager.Addsql(deletesql);
					Iterator<String> keyIterator = baseInfo.keySet().iterator();
					StringBuffer insertsql = null;
					while (keyIterator.hasNext()) {
						String subentity = keyIterator.next();
						String thevalue = baseInfo.get(subentity);
						thevalue = thevalue.replaceAll("\\\\", "/");
						insertsql = new StringBuffer();
						insertsql
								.append("insert into nms_orabaseinfo(serverip,entity,subentity,thevalue,mon_time) values ('");
						insertsql.append(serverip);
						insertsql.append("','");
						insertsql.append("baseinfo");
						insertsql.append("','");
						insertsql.append(subentity);
						insertsql.append("','");
						insertsql.append(thevalue);
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
						insertsql = null;
					}
					// ---------------------------------sysinfo不需告警
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// ----------------------------------保存到数据库及告警 end

		}
		return returndata;
	}

}
