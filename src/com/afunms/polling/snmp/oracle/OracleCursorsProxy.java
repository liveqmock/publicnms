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
 * Oracle 游标统一采集 采集 使用JDBC采集
 * 
 * @author wupinlong 2012/09/19
 * 
 */
public class OracleCursorsProxy extends SnmpMonitor {

	public OracleCursorsProxy() {
	}

	@SuppressWarnings("unchecked")
	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicators) {
		Hashtable returndata = new Hashtable();
		String htKey = "cursors";
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
				// 如果未被管理，不采集，
				return returndata;
			}
			String serverip = dbmonitorlist.getIpAddress();
			String[] args = new String[]{"opencur","curconnect","mon_time"};
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
				oracleHash.put("cursors", returndata.get(htKey));
			}

			// 保存至数据库中
			Hashtable cursorsHash = null;
			Vector extent_v = (Vector) returndata.get(htKey);
			if (extent_v != null && !extent_v.isEmpty()) {
				cursorsHash = (Hashtable) extent_v.get(0);
				if (cursorsHash != null && !cursorsHash.isEmpty()) {
					String hex = IpTranslation.formIpToHex(serverip);
					String ip = hex + ":" + dbmonitorlist.getId();
	
					try {
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Calendar tempCal = Calendar.getInstance();
						Date cc = tempCal.getTime();
						String montime = sdf.format(cc);
						String deletesql = "delete from nms_oracursors where serverip='"
								+ ip + "'";
						// 加入队列
						GathersqlListManager.Addsql(deletesql);
	
						String curconnect = String.valueOf(cursorsHash
								.get("curconnect"));
						String opencur = String.valueOf(cursorsHash.get("opencur"));
						String insertsql = "insert into nms_oracursors(serverip, opencur, curconnect, mon_time) "
								+ "values('"
								+ ip
								+ "','"
								+ opencur
								+ "','"
								+ curconnect;
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
								if ("opencur".equalsIgnoreCase(alarmIndicatorsNode
										.getName())) {
									if (opencur != null) {
										checkEventUtil.checkEvent(nodeDTO,
												alarmIndicatorsNode, opencur);
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
