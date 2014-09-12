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

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.model.DBVo;
import com.afunms.application.util.IpTranslation;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.EncryptUtil;
import com.afunms.common.util.OracleJdbcUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SystemConstant;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.gatherdb.GathersqlListManager;

/**
 * Oracle 系统信息 采集 使用JDBC采集
 * 
 * @author wupinlong 2012/09/17
 * 
 */
public class OracleAsmDiskgroupSnmp extends SnmpMonitor {

	public OracleAsmDiskgroupSnmp() {
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
		Hashtable diskgroupHash = new Hashtable();
		Vector diskgroup_vector = new Vector();
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
				ResultSet rs = null;
				try {
					// 数据库系统信息
					String sqlDisk = "select * from v$asm_diskgroup";
					try {
						rs = util.stmt.executeQuery(sqlDisk);
						while (rs.next()) {
								// 得到该数据库的disk
							diskgroupHash = new Hashtable();
							diskgroupHash.put("GROUP_NUMBER", rs.getString("GROUP_NUMBER")
										.toString());
							diskgroupHash.put("NAME", rs.getString("NAME")
										.toString());
							diskgroupHash.put("STATE", rs.getString("STATE")
									.toString());
							diskgroupHash.put("TOTAL_MB", rs.getString("TOTAL_MB")
										.toString());
							diskgroupHash.put("FREE_MB", rs.getString("FREE_MB")
									.toString());
							
							diskgroup_vector.add(diskgroupHash);
						}

					} catch (Exception e) {

					} finally {
						if (rs != null)
							rs.close();
					}

					returndata.put("asmDiskgroupHash", diskgroup_vector);
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
				oracleHash.put("asmDiskgroupHash", returndata.get("asmDiskgroupHash"));
			}

			// ----------------------------------保存到数据库及告警 start
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			// asmdiskgroup
			Vector diskgroupVector = (Vector) returndata.get("asmDiskgroupHash");
			if (diskgroupVector != null && !diskgroupVector.isEmpty()) {
				String hex = IpTranslation.formIpToHex(dbmonitorlist
						.getIpAddress());
				serverip = hex + ":" + dbmonitorlist.getId();

				try {
					Calendar tempCal = Calendar.getInstance();
					Date cc = tempCal.getTime();
					String montime = sdf.format(cc);

					String deletesql = "delete from nms_oraasmdiskgroup where serverip='"
							+ serverip + "'";
					GathersqlListManager.Addsql(deletesql);
					
					for(int k = 0; k < diskgroupVector.size(); k++){
						Hashtable diskgroupValue = (Hashtable)diskgroupVector.get(k);
						String DISK_NUMBER = diskgroupValue.get("GROUP_NUMBER")
								.toString();
						// String HOST_NAME = sysValue.get("HOST_NAME").toString();
						String NAME = diskgroupValue.get("NAME").toString();
						String STATE = diskgroupValue.get("STATE").toString();
						String TOTAL_MB = diskgroupValue.get("TOTAL_MB").toString();
						String FREE_MB = diskgroupValue.get("FREE_MB").toString();
						// Vector banners = (Vector) sysValue.get("BANNER");
						String insertsql = "insert into nms_oraasmdiskgroup(serverip,GROUP_NUMBER,NAME,STATE,TOTAL_MB,FREE_MB,mon_time) "
								+ "values('"
								+ serverip
								+ "','"
								+ DISK_NUMBER
								+ "','"
								+ NAME
								+ "','"
								+ STATE
								+ "','"
								+ TOTAL_MB
								+ "','"
								+ FREE_MB;
						if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
							insertsql = insertsql + "','" + montime + "')";
						} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
							insertsql = insertsql + "',to_date('" + montime
									+ "','YYYY-MM-DD HH24:MI:SS'))";
						}
						GathersqlListManager.Addsql(insertsql);
					// ---------------------------------
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
							if ("asmdiskgroup"
									.equalsIgnoreCase(alarmIndicatorsNode
											.getName())) {
								String asmdiskgroupstatus = "1";
								for(int j = 0; j < diskgroupVector.size(); j++){
									Hashtable asmdiskgroupValue = (Hashtable)diskgroupVector.get(j);
									String STATUS = asmdiskgroupValue.get("STATE").toString();
									if(!STATUS.trim().equals("CONNECTED") && !STATUS.trim().equals("MOUNTED")){
										asmdiskgroupstatus = "-1";
									}
								}
								checkEventUtil.checkEvent(nodeDTO,
										alarmIndicatorsNode, asmdiskgroupstatus, "asmdiskgroup");
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
