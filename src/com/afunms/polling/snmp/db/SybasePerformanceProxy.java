package com.afunms.polling.snmp.db;

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
public class SybasePerformanceProxy extends SnmpMonitor {

	public SybasePerformanceProxy() {
	}

	@SuppressWarnings("unchecked")
	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicators) {
		Hashtable returndata = new Hashtable();
		String htKey = "performance";
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
			String[] args = new String[] { 
					"cpu_busy","idle","version","io_busy","sent_rate","received_rate",
					"write_rate","read_rate","serverName","cpu_busy_rate",
					"io_busy_rate","disk_count","locks_count","xact_count",
					"total_dataCache","total_physicalMemory","metadata_cache",
					"procedure_cache","total_logicalMemory","data_hitrate",
					"procedure_hitrate", //"nms_sybaseperformance"
				};
			returndata = LogParser.parse(this, dbmonitorlist, htKey, args);

			// 更新内存
			if (!ShareData.getSharedata().containsKey(
					dbmonitorlist.getIpAddress() + ":" + dbmonitorlist.getId())) {
				ShareData.getSharedata().put(
						dbmonitorlist.getIpAddress() + ":"
								+ dbmonitorlist.getId(), returndata);
			} else {
				Hashtable sybaseHash = (Hashtable) ShareData.getSharedata()
						.get(
								dbmonitorlist.getIpAddress() + ":"
										+ dbmonitorlist.getId());
				sybaseHash.put("performance", returndata.get(htKey));
			}

			// 保存至数据库中
			Hashtable infoValueHash = null;
			Vector extent_v = (Vector) returndata.get(htKey);
			if (extent_v != null && !extent_v.isEmpty()) {
				infoValueHash = (Hashtable) extent_v.get(0);

				if (infoValueHash != null && !infoValueHash.isEmpty()) {
					String hex = IpTranslation.formIpToHex(dbmonitorlist
							.getIpAddress());
					String ip = hex + ":" + dbmonitorlist.getId();

					try {
						Calendar tempCal = Calendar.getInstance();
						Date cc = tempCal.getTime();
						SimpleDateFormat sdf = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss");
						String montime = sdf.format(cc);
						String deletesql = "delete from nms_sybaseperformance where serverip='"
								+ ip + "'";
						// 加入队列
						GathersqlListManager.Addsql(deletesql);
						
						String cpu_busy = String.valueOf(infoValueHash.get("cpu_busy"));
						String idle = String.valueOf(infoValueHash.get("idle"));
						String version = String.valueOf(infoValueHash.get("version"));
						String io_busy = String.valueOf(infoValueHash.get("io_busy"));
						String sent_rate = String.valueOf(infoValueHash.get("sent_rate"));
						String received_rate = String.valueOf(infoValueHash.get("received_rate"));
						String write_rate = String.valueOf(infoValueHash.get("write_rate"));
						String read_rate = String.valueOf(infoValueHash.get("read_rate"));
						String serverName = String.valueOf(infoValueHash.get("serverName"));
						String cpu_busy_rate = String.valueOf(infoValueHash.get("cpu_busy_rate"));
						String io_busy_rate = String.valueOf(infoValueHash.get("io_busy_rate"));
						String disk_count = String.valueOf(infoValueHash.get("disk_count"));
						String locks_count = String.valueOf(infoValueHash.get("locks_count"));
						String xact_count = String.valueOf(infoValueHash.get("xact_count"));
						String total_dataCache = String.valueOf(infoValueHash.get("total_dataCache"));
						String total_physicalMemory = String.valueOf(infoValueHash.get("total_physicalMemory"));
						String metadata_cache = String.valueOf(infoValueHash.get("metadata_cache"));
						String procedure_cache = String.valueOf(infoValueHash.get("procedure_cache"));
						String total_logicalMemory = String.valueOf(infoValueHash.get("total_logicalMemory"));
						String data_hitrate = String.valueOf(infoValueHash.get("data_hitrate"));
						String procedure_hitrate = String.valueOf(infoValueHash.get("procedure_hitrate"));
						
						String insertsql = "insert into nms_sybaseperformance(serverip, cpu_busy, idle, version, io_busy, sent_rate," +
								"received_rate,write_rate,read_rate,serverName,cpu_busy_rate,io_busy_rate,disk_count,locks_count,xact_count," +
								"total_dataCache,total_physicalMemory,metadata_cache,procedure_cache,total_logicalMemory," +
								"data_hitrate,procedure_hitrate,mon_time) " + " values ('" + ip + "','" +
								cpu_busy + "','" + idle + "','" + version + "','" + io_busy + "','" + 
								sent_rate + "','" + received_rate + "','" + write_rate + "','" + read_rate +
								serverName + "','" + cpu_busy_rate + "','" + io_busy_rate + "','" + 
								disk_count + "','" + locks_count + "','" + xact_count + "','" + total_dataCache + "','" + 
								total_physicalMemory + "','" + metadata_cache + "','" + procedure_cache + "','" + 
								total_logicalMemory + "','" + data_hitrate + "','" + procedure_hitrate ;
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
								AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode) list.get(i);
								if ("cpu_busy".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
									if (cpu_busy != null) {
										checkEventUtil.checkEvent(nodeDTO,alarmIndicatorsNode,cpu_busy);
									}
								}
								if ("idle".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
									if (idle != null) {
										checkEventUtil.checkEvent(nodeDTO,alarmIndicatorsNode,idle);
									}
								}
								if ("version".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
									if (version != null) {
										checkEventUtil.checkEvent(nodeDTO,alarmIndicatorsNode,version);
									}
								}
								if ("io_busy".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
									if (io_busy != null) {
										checkEventUtil.checkEvent(nodeDTO,alarmIndicatorsNode,io_busy);
									}
								}
								if ("sent_rate".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
									if (sent_rate != null) {
										checkEventUtil.checkEvent(nodeDTO,alarmIndicatorsNode,sent_rate);
									}
								}
								if ("received_rate".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
									if (received_rate != null) {
										checkEventUtil.checkEvent(nodeDTO,alarmIndicatorsNode,received_rate);
									}
								}
								if ("write_rate".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
									if (write_rate != null) {
										checkEventUtil.checkEvent(nodeDTO,alarmIndicatorsNode,write_rate);
									}
								}
								if ("read_rate".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
									if (read_rate != null) {
										checkEventUtil.checkEvent(nodeDTO,alarmIndicatorsNode,read_rate);
									}
								}
								if ("serverName".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
									if (serverName != null) {
										checkEventUtil.checkEvent(nodeDTO,alarmIndicatorsNode,serverName);
									}
								}
								if ("cpu_busy_rate".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
									if (cpu_busy_rate != null) {
										checkEventUtil.checkEvent(nodeDTO,alarmIndicatorsNode,cpu_busy_rate);
									}
								}
								if ("io_busy_rate".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
									if (io_busy_rate != null) {
										checkEventUtil.checkEvent(nodeDTO,alarmIndicatorsNode,io_busy_rate);
									}
								}
								if ("disk_count".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
									if (disk_count != null) {
										checkEventUtil.checkEvent(nodeDTO,alarmIndicatorsNode,disk_count);
									}
								}
								if ("locks_count".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
									if (locks_count != null) {
										checkEventUtil.checkEvent(nodeDTO,alarmIndicatorsNode,locks_count);
									}
								}
								if ("xact_count".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
									if (xact_count != null) {
										checkEventUtil.checkEvent(nodeDTO,alarmIndicatorsNode,xact_count);
									}
								}
								if ("total_dataCache".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
									if (total_dataCache != null) {
										checkEventUtil.checkEvent(nodeDTO,alarmIndicatorsNode,total_dataCache);
									}
								}
								if ("total_physicalMemory".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
									if (total_physicalMemory != null) {
										checkEventUtil.checkEvent(nodeDTO,alarmIndicatorsNode,total_physicalMemory);
									}
								}
								if ("metadata_cache".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
									if (metadata_cache != null) {
										checkEventUtil.checkEvent(nodeDTO,alarmIndicatorsNode,metadata_cache);
									}
								}
								if ("procedure_cache".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
									if (procedure_cache != null) {
										checkEventUtil.checkEvent(nodeDTO,alarmIndicatorsNode,procedure_cache);
									}
								}
								if ("total_logicalMemory".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
									if (total_logicalMemory != null) {
										checkEventUtil.checkEvent(nodeDTO,alarmIndicatorsNode,total_logicalMemory);
									}
								}
								if ("data_hitrate".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
									if (data_hitrate != null) {
										checkEventUtil.checkEvent(nodeDTO,alarmIndicatorsNode,data_hitrate);
									}
								}
								if ("procedure_hitrate".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
									if (procedure_hitrate != null) {
										checkEventUtil.checkEvent(nodeDTO,alarmIndicatorsNode,procedure_hitrate);
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
