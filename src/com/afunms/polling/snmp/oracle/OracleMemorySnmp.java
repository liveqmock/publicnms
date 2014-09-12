package com.afunms.polling.snmp.oracle;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.model.DBVo;
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
 * Oracle 内存统一采集 采集 使用JDBC采集
 * 
 * @author wupinlong 2012/09/18
 * 
 */
public class OracleMemorySnmp extends SnmpMonitor {

	public OracleMemorySnmp() {
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
		Hashtable memoryPerf = new Hashtable();
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
					try {
						// 缓存命中率
						String buffersql = "select round((1 - (sum(decode(name, 'physical reads', value, 0)) /(sum(decode(name, 'db block gets', value, 0)) +sum(decode(name, 'consistent gets', value, 0))))) * 100) as HitRatio from v$sysstat";
						rs = util.stmt.executeQuery(buffersql);
						if (rs.next()) {
							memoryPerf.put("buffercache", rs.getString(
									"HitRatio").toString());
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (rs != null)
							rs.close();
					}

					try {
						// 数据字典命中率
						String dictionarysql = "select round((1 - (sum(getmisses) / sum(gets))) * 100) as HitRatio from v$rowcache";
						rs = util.stmt.executeQuery(dictionarysql);
						if (rs.next()) {
							memoryPerf.put("dictionarycache", rs.getString(
									"HitRatio").toString());
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (rs != null)
							rs.close();
					}

					try {
						// 库缓存命中率
						String librarysql = "select round(sum(pins) / (sum(pins) + sum(reloads)) * 100) as HitRatio from v$librarycache";
						rs = util.stmt.executeQuery(librarysql);
						if (rs.next()) {
							memoryPerf.put("librarycache", rs.getString(
									"HitRatio").toString());
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (rs != null)
							rs.close();
					}

					try {
						// 最浪费内存的前10个语句占全部内存读取量的比例
						String pctsql = "select sum(pct_bufgets) as pctbufgets from (select rank() over(order by buffer_gets desc) as rank_bufgets,to_char(100 * ratio_to_report(buffer_gets) over(), '999.99') as pct_bufgets from v$sqlarea) where rank_bufgets < 11";
						rs = util.stmt.executeQuery(pctsql);
						if (rs.next()) {
							memoryPerf.put("pctbufgets", rs.getString(
									"pctbufgets").toString());
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (rs != null)
							rs.close();
					}

					try {
						// 内存中的排序
						String pctmemorysql = "select a.value as DiskSorts,b.value as MemorySorts,round((100 * b.value) /decode((a.value + b.value), 0, 1, (a.value + b.value)),2) as PctMemorySorts from v$sysstat a, v$sysstat b where a.name = 'sorts (disk)' and b.name = 'sorts (memory)'";
						rs = util.stmt.executeQuery(pctmemorysql);
						if (rs.next()) {
							memoryPerf.put("pctmemorysorts", rs.getString(
									"PctMemorySorts").toString());
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (rs != null)
							rs.close();
					}

					returndata.put("memoryPerf", memoryPerf);
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
				oracleHash.put("memPerfValue", returndata.get("memoryPerf"));
			}

			// 保存至数据库中
			Hashtable memPerfValueHash = (Hashtable) returndata
					.get("memoryPerf");
			if (memPerfValueHash != null && !memPerfValueHash.isEmpty()) {
				String hex = IpTranslation.formIpToHex(dbmonitorlist
						.getIpAddress());
				String ip = hex + ":" + dbmonitorlist.getId();

				try {
					Calendar tempCal = Calendar.getInstance();
					Date cc = tempCal.getTime();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String montime = sdf.format(cc);
					String deletesql = "delete from nms_oramemperfvalue where serverip='"
							+ ip + "'";
					// 加入队列
					GathersqlListManager.Addsql(deletesql);

					String pctmemorysorts = String.valueOf(memPerfValueHash
							.get("pctmemorysorts"));
					String pctbufgets = String.valueOf(memPerfValueHash
							.get("pctbufgets"));
					String dictionarycache = String.valueOf(memPerfValueHash
							.get("dictionarycache"));
					String buffercache = String.valueOf(memPerfValueHash
							.get("buffercache"));
					String librarycache = String.valueOf(memPerfValueHash
							.get("librarycache"));
					String insertsql = "insert into nms_oramemperfvalue(serverip, pctmemorysorts, pctbufgets, dictionarycache, buffercache, librarycache,mon_time) "
							+ "values('"
							+ ip
							+ "','"
							+ pctmemorysorts
							+ "','"
							+ pctbufgets
							+ "','"
							+ dictionarycache
							+ "','"
							+ buffercache + "','" + librarycache;

					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						insertsql = insertsql + "','" + montime + "')";
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						insertsql = insertsql + "',to_date('" + montime
								+ "','YYYY-MM-DD HH24:MI:SS'))";
					}
					// 加入入库队列
					GathersqlListManager.Addsql(insertsql);

					// ---------------------------------判断告警 start
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
							if ("buffercache"
									.equalsIgnoreCase(alarmIndicatorsNode
											.getName())) {
								if (buffercache != null) {
									checkEventUtil.checkEvent(nodeDTO,
											alarmIndicatorsNode, buffercache);
								}
							}
							if ("dictionarycache"
									.equalsIgnoreCase(alarmIndicatorsNode
											.getName())) {
								if (dictionarycache != null) {
									checkEventUtil.checkEvent(nodeDTO,
											alarmIndicatorsNode,
											dictionarycache);
								}
							}
							if ("pctmemorysorts"
									.equalsIgnoreCase(alarmIndicatorsNode
											.getName())) {
								if (pctmemorysorts != null) {
									checkEventUtil
											.checkEvent(nodeDTO,
													alarmIndicatorsNode,
													pctmemorysorts);
								}
							}
							if ("pctbufgets"
									.equalsIgnoreCase(alarmIndicatorsNode
											.getName())) {
								if (pctbufgets != null) {
									checkEventUtil.checkEvent(nodeDTO,
											alarmIndicatorsNode, pctbufgets);
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					// ---------------------------------判断告警 end

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
		return returndata;
	}
}
