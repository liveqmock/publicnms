package com.afunms.polling.snmp.oracle;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

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
 */
public class OracleMemoryProxy extends SnmpMonitor {

	public OracleMemoryProxy() {
	}

	@SuppressWarnings("unchecked")
	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicators) {
		Hashtable returndata = new Hashtable();
		String htKey = "memoryPerf";
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
			String[] args = new String[] { "pctmemorysorts", "pctbufgets",
					"dictionarycache", "buffercache", "librarycache",
					"mon_time" };
			returndata = LogParser.parse(this, dbmonitorlist, htKey, args);

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
				oracleHash.put("memPerfValue", returndata.get(htKey));
			}

			// 保存至数据库中
			Hashtable memPerfValueHash = null;
			Vector extent_v = (Vector) returndata.get(htKey);
			if (extent_v != null && !extent_v.isEmpty()) {
				memPerfValueHash = (Hashtable) extent_v.get(0);

				if (memPerfValueHash != null && !memPerfValueHash.isEmpty()) {
					String hex = IpTranslation.formIpToHex(dbmonitorlist
							.getIpAddress());
					String ip = hex + ":" + dbmonitorlist.getId();

					try {
						Calendar tempCal = Calendar.getInstance();
						Date cc = tempCal.getTime();
						SimpleDateFormat sdf = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss");
						String montime = sdf.format(cc);
						String deletesql = "delete from nms_oramemperfvalue where serverip='"
								+ ip + "'";
						// 加入队列
						 GathersqlListManager.Addsql(deletesql);

						String pctmemorysorts = String.valueOf(memPerfValueHash
								.get("pctmemorysorts"));
						String pctbufgets = String.valueOf(memPerfValueHash
								.get("pctbufgets"));
						String dictionarycache = String
								.valueOf(memPerfValueHash
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
								+ "','" + buffercache + "','" + librarycache;

						if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
							insertsql = insertsql + "','" + montime + "')";
						} else if ("oracle"
								.equalsIgnoreCase(SystemConstant.DBType)) {
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
												alarmIndicatorsNode,
												buffercache);
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
										checkEventUtil.checkEvent(nodeDTO,
												alarmIndicatorsNode,
												pctmemorysorts);
									}
								}
								if ("pctbufgets"
										.equalsIgnoreCase(alarmIndicatorsNode
												.getName())) {
									if (pctbufgets != null) {
										checkEventUtil
												.checkEvent(nodeDTO,
														alarmIndicatorsNode,
														pctbufgets);
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
		}
		return returndata;
	}
}
