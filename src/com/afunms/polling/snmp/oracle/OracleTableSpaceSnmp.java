package com.afunms.polling.snmp.oracle;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.dao.OraspaceconfigDao;
import com.afunms.application.model.DBVo;
import com.afunms.application.model.Oraspaceconfig;
import com.afunms.application.util.IpTranslation;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.EncryptUtil;
import com.afunms.common.util.OracleJdbcUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SystemConstant;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.gatherdb.GathersqlListManager;

/**
 * Oracle 表空间信息 采集 使用JDBC采集
 * 
 * @author wupinlong 2012/09/17
 * 
 */
public class OracleTableSpaceSnmp extends SnmpMonitor {

	public OracleTableSpaceSnmp() {
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
				ResultSet rs = null;
				try {
					// 获取表空间信息
					String sqlTablespace = "SELECT UPPER(F.TABLESPACE_NAME) \"Tablespace\", D.TOT_GROOTTE_MB \"Size MB\",F.TOTAL_BYTES \"Free MB\",chunks,"
							+ "TO_CHAR(ROUND( F.TOTAL_BYTES / D.TOT_GROOTTE_MB * 100,2),'990.99') \"Percent Free\" ,c.file_name,c.status "
							+ "FROM (SELECT TABLESPACE_NAME,　ROUND(SUM(BYTES) / (1024 * 1024), 2) TOTAL_BYTES,count(*) chunks,"
							+ "ROUND(MAX(BYTES) / (1024 * 1024), 2) MAX_BYTES　　FROM SYS.DBA_FREE_SPACE GROUP BY TABLESPACE_NAME) F,　　"
							+ "(SELECT DD.TABLESPACE_NAME,　　ROUND(SUM(DD.BYTES) / (1024 * 1024), 2) TOT_GROOTTE_MB　　FROM SYS.DBA_DATA_FILES DD　　"
							+ "GROUP BY DD.TABLESPACE_NAME) D,    dba_data_files c　　WHERE D.TABLESPACE_NAME = F.TABLESPACE_NAME "
							+ "and D.tablespace_name=c.tablespace_name　ORDER BY 1";
					Vector returnVal2 = new Vector();
					Map<String, Integer> names = new HashMap<String, Integer>();
					rs = util.stmt.executeQuery(sqlTablespace);
					ResultSetMetaData rsmd2 = rs.getMetaData();
					while (rs.next()) {
						String name = rs.getString("Tablespace");
						if (names.get(name) == null) {
							Hashtable return_value = new Hashtable();
							names.put(name, 1);
							for (int i = 1; i <= rsmd2.getColumnCount(); i++) {
								String col = rsmd2.getColumnName(i);
								if (rs.getString(i) != null) {
									String tmp = rs.getString(i).toString();
									return_value.put(col.toLowerCase().replace(
											" ", "_"), tmp);
								} else
									return_value.put(col.toLowerCase(), "--");
							}
							returnVal2.addElement(return_value);
							return_value = null;
						} else {
							for (int i = 0; i < returnVal2.size(); i++) {
								Hashtable infos = (Hashtable) returnVal2.get(i);
								if (name.equals(infos.get("tablespace"))) {
									String tem1 = (String) infos.get("free_mb");
									String chunks = (String) infos
											.get("chunks");
									String tem2 = rs.getString("Free MB");
									float te1 = 0;
									float te2 = 0;
									if (tem1 != null && !"".equals(tem1))
										te1 = Float.parseFloat(tem1);
									if (tem2 != null && !"".equals(tem2))
										te2 = Float.parseFloat(tem2);
									String total1 = (String) infos
											.get("size_mb");
									String total2 = rs.getString("Size MB");
									float tt1 = 0;
									float tt2 = 0;
									if (total1 != null && !"".equals(total1)) {
										tt1 = Float.parseFloat(total1);
									}
									if (total2 != null && !"".equals(total2)) {
										tt2 = Float.parseFloat(total2);
									}
									float f = (te1 + te2) / (tt1 + tt2);
									infos.put("chunks", chunks);// 每个表空间空闲块数目
									infos.put("percent_free", String
											.valueOf(f * 100));
									if (i == returnVal2.size() - 1) {
										f = te2 / tt2;
										infos.put("size_mb", String
												.valueOf(tt2));
										infos.put("percent_free", String
												.valueOf(f * 100));
										infos.put("free_mb", String
												.valueOf(te2));
									} else {
										infos.put("size_mb", String.valueOf(tt1
												+ tt2));
										infos.put("percent_free", String
												.valueOf(f * 100));
										infos.put("free_mb", String.valueOf(te1
												+ te2));
									}
									String path = rs.getString("file_name");
									int len = path.lastIndexOf("\\");
									if (len != -1) {
										String tpath = path.substring(0, len);
										infos.put("file_name", tpath);
									}
									break;
								}
							}

						}

					}

					returndata.put("tablespace", returnVal2);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (rs != null)
						rs.close();
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
				oracleHash.put("tableinfo_v", returndata.get("tablespace"));
			}
			Vector tableinfo_v = (Vector)returndata.get("tablespace");
			if(tableinfo_v!=null){
				ShareData.setOraspacedata(serverip + ":" + dbmonitorlist.getId(), tableinfo_v);
			}

			// ----------------------------------保存到数据库及告警 start
			Vector tablespace_v = (Vector) returndata.get("tablespace");
			if (tablespace_v != null && !tablespace_v.isEmpty()) {
				String hex = IpTranslation.formIpToHex(dbmonitorlist
						.getIpAddress());
				serverip = hex + ":" + dbmonitorlist.getId();

				try {
					Calendar tempCal = Calendar.getInstance();
					Date cc = tempCal.getTime();
					SimpleDateFormat sdf = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					String montime = sdf.format(cc);

					String deletesql = "delete from nms_oraspaces where serverip='"
							+ serverip + "'";
					GathersqlListManager.Addsql(deletesql);

					String insertsql = null;
					for (int k = 0; k < tablespace_v.size(); k++) {
						Hashtable ht = (Hashtable) tablespace_v.get(k);
						String file_name = ht.get("file_name").toString();
						String tablespace = ht.get("tablespace").toString();
						String size_mb = ht.get("size_mb").toString();
						String free_mb = ht.get("free_mb").toString();
						String percent_free = ht.get("percent_free").toString();
						String status = ht.get("status").toString();
						String chunks = ht.get("chunks").toString();// 每个表空间空闲块数目
						file_name = file_name.replaceAll("\\\\", "/");
						insertsql = "";
						insertsql = "insert into nms_oraspaces(serverip,tablespace,free_mb,size_mb,percent_free,file_name,status,chunks,mon_time) "
								+ "values('"
								+ serverip
								+ "','"
								+ tablespace
								+ "','"
								+ free_mb
								+ "','"
								+ size_mb
								+ "','"
								+ percent_free
								+ "','"
								+ file_name
								+ "','"
								+ status + "','" + chunks;
						if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
							insertsql = insertsql + "','" + montime + "')";
						} else if ("oracle"
								.equalsIgnoreCase(SystemConstant.DBType)) {
							insertsql = insertsql + "',to_date('" + montime
									+ "','YYYY-MM-DD HH24:MI:SS'))";
						}
						GathersqlListManager.Addsql(insertsql);
					}
					// *****************tablespace告警 start
					try {
						NodeUtil nodeUtil = new NodeUtil();
						NodeDTO nodeDTO = nodeUtil
								.conversionToNodeDTO(dbmonitorlist);
						// 判断是否存在此告警指标
						AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
						List list = alarmIndicatorsUtil
								.getAlarmInicatorsThresholdForNode(nodeDTO
										.getId()
										+ "", nodeDTO.getType(), nodeDTO
										.getSubtype());
						CheckEventUtil checkEventUtil = new CheckEventUtil();
						for (int i = 0; i < list.size(); i++) {
							AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode) list
									.get(i);
							if ("tablespace".equalsIgnoreCase(alarmIndicatorsNode
									.getName())) {
								if(tablespace_v!=null&&tablespace_v.size()>0){
									OraspaceconfigDao oraspaceconfigManager = new OraspaceconfigDao();
									Hashtable oraspaces=null;
									try {
										oraspaces = oraspaceconfigManager.getByAlarmflag(1);
									} catch (Exception e1) {
										e1.printStackTrace();
									} finally {
										oraspaceconfigManager.close();
									}
//									Vector spaces = new Vector();
									for (int k = 0; k < tablespace_v.size(); k++) {
										Hashtable ht = (Hashtable) tablespace_v.get(k);
										String tablespace = ht.get("tablespace").toString();
//										if (spaces.contains(tablespace))
//											continue;
//										spaces.add(tablespace);
										String percent = ht.get("percent_free").toString();
										if (oraspaces!=null&&oraspaces.containsKey(nodeDTO.getIpaddress() + ":" + nodeDTO.getId() + ":" + tablespace)) {
											// 存在需要告警的表空间
											Integer free = 0;
											try {
												free = new Float(percent).intValue();
											} catch (Exception e) {
												e.printStackTrace();
											}
											// 依据表空间告警配置判断是否告警
											Oraspaceconfig oraspaceconfig = (Oraspaceconfig) oraspaces.get(nodeDTO.getIpaddress() + ":" + nodeDTO.getId() + ":" + tablespace);
											alarmIndicatorsNode.setLimenvalue0(oraspaceconfig.getAlarmvalue() + "");
											alarmIndicatorsNode.setLimenvalue1(oraspaceconfig.getAlarmvalue() + "");
											alarmIndicatorsNode.setLimenvalue2(oraspaceconfig.getAlarmvalue() + "");
											checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, (100 - free)+"",tablespace);
										}
									}
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					// *****************tablespace告警 end
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// ----------------------------------保存到数据库及告警 end

		}
		return returndata;
	}

}
