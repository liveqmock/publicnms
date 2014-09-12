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
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.gatherdb.GathersqlListManager;

public class OracleBackupJobSnmp extends SnmpMonitor {
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
				//String dburl = "jdbc:oracle:oci:@"  + dbmonitorlist.getDbName();
				String dburl = "jdbc:oracle:thin:@" + serverip + ":" + port+ ":" + dbmonitorlist.getDbName();
				util = new OracleJdbcUtil(dburl, dbmonitorlist.getUser(),
						EncryptUtil.decode(dbmonitorlist.getPassword()));
				util.jdbc();

				// *********************************取数据 start
				String sqlcf = "select T.COMMAND_ID,T.START_TIME,T.TIME_TAKEN_DISPLAY,T.STATUS,T.INPUT_TYPE,T.OUTPUT_DEVICE_TYPE,T.INPUT_BYTES_DISPLAY,T.OUTPUT_BYTES_DISPLAY,T.OUTPUT_BYTES_PER_SEC_DISPLAY from V$RMAN_BACKUP_JOB_DETAILS t";
				ResultSet rs = null;
				try {
					// 文件备份
					Vector backupV = new Vector();
					Hashtable backuphash = new Hashtable();
					rs = util.stmt.executeQuery(sqlcf);
					while (rs.next()) {
						backuphash = new Hashtable();
						backuphash.put("COMMAND_ID", rs.getString("COMMAND_ID"));
						backuphash.put("START_TIME", rs.getString("START_TIME"));
						backuphash.put("TIME_TAKEN_DISPLAY", rs.getString("TIME_TAKEN_DISPLAY"));
						backuphash.put("STATUS", rs.getString("STATUS"));
						backuphash.put("INPUT_TYPE", rs.getString("INPUT_TYPE"));
						backuphash.put("OUTPUT_DEVICE_TYPE", rs.getString("OUTPUT_DEVICE_TYPE"));
						backuphash.put("INPUT_BYTES_DISPLAY", rs.getString("INPUT_BYTES_DISPLAY"));
						backuphash.put("OUTPUT_BYTES_DISPLAY", rs.getString("OUTPUT_BYTES_DISPLAY"));
						backuphash.put("OUTPUT_BYTES_PER_SEC_DISPLAY", rs.getString("OUTPUT_BYTES_PER_SEC_DISPLAY"));
						backupV.add(backuphash);
					}
					returndata.put("backup", backupV);
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
				oracleHash.put("backup_v", returndata.get("backup"));
			}

			// ----------------------------------保存到数据库及告警 start
			Vector backup_v = (Vector) returndata.get("backup");
			if (backup_v != null && !backup_v.isEmpty()) {
				String hex = IpTranslation.formIpToHex(dbmonitorlist
						.getIpAddress());
				serverip = hex + ":" + dbmonitorlist.getId();

				try {
					Calendar tempCal = Calendar.getInstance();
					Date cc = tempCal.getTime();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String montime = sdf.format(cc);

					String deletesql = "delete from nms_orabackup where serverip='"
							+ serverip + "'";
					GathersqlListManager.Addsql(deletesql);
					String insertsql = null;
					for (int k = 0; k < backup_v.size(); k++) {
						Hashtable ht = (Hashtable) backup_v.get(k);
						String COMMAND_ID = ht.get("COMMAND_ID").toString();
						String START_TIME = ht.get("START_TIME").toString();
						String TIME_TAKEN_DISPLAY = ht.get("TIME_TAKEN_DISPLAY").toString();
						String STATUS = ht.get("STATUS").toString();
						String INPUT_TYPE = ht.get("INPUT_TYPE").toString();
						String OUTPUT_DEVICE_TYPE = ht.get("OUTPUT_DEVICE_TYPE").toString().trim();
						String INPUT_BYTES_DISPLAY = ht.get("INPUT_BYTES_DISPLAY").toString().trim();
						String OUTPUT_BYTES_DISPLAY = ht.get("OUTPUT_BYTES_DISPLAY").toString().trim();
						String OUTPUT_BYTES_PER_SEC_DISPLAY = ht.get("OUTPUT_BYTES_PER_SEC_DISPLAY").toString();
						insertsql = "insert into nms_orabackup(serverip,COMMAND_ID,START_TIME,TIME_TAKEN_DISPLAY,STATUS,INPUT_TYPE,OUTPUT_DEVICE_TYPE,INPUT_BYTES_DISPLAY,OUTPUT_BYTES_DISPLAY,OUTPUT_BYTES_PER_SEC_DISPLAY) "
								+ "values('"
								+ serverip
								+ "','"
								+ COMMAND_ID
								+ "','"
								+ START_TIME
								+ "','"
								+ TIME_TAKEN_DISPLAY
								+ "','" + STATUS + "','" + INPUT_TYPE + "','" + OUTPUT_DEVICE_TYPE + "','" + INPUT_BYTES_DISPLAY + "','" + OUTPUT_BYTES_DISPLAY + "','" + OUTPUT_BYTES_PER_SEC_DISPLAY + "')";
						GathersqlListManager.Addsql(insertsql);
					}
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
							if ("backup"
									.equalsIgnoreCase(alarmIndicatorsNode
											.getName())) {
								SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
								String time = sdf1.format(new Date());
								for(int j = 0; j < backup_v.size(); j++){
									Hashtable backupValue = (Hashtable)backup_v.get(j);
									String starttime = backupValue.get("START_TIME").toString();
									String STATUS = backupValue.get("STATUS").toString();
									if(starttime.substring(0,10).equals(time)){
										checkEventUtil.checkEvent(nodeDTO,
												alarmIndicatorsNode, STATUS, "backup");
									}
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// ----------------------------------保存到数据库及告警 end

		}
		return returndata;
	}

}
