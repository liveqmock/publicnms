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
 * @author  2013/05/10
 * 
 */
public class OracleAsmClientSnmp extends SnmpMonitor {

	public OracleAsmClientSnmp() {
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
		Hashtable asmHash = new Hashtable();
		Vector asm_vector = new Vector();
		if (dbmonitorlist != null) {
			if (dbmonitorlist.getManaged() == 0) {
				// 如果未被管理，不采集，user信息为空
				return returndata;
			}
			String serverip = dbmonitorlist.getIpAddress();
			int port = Integer.parseInt(dbmonitorlist.getPort());
			OracleJdbcUtil util = null;
			try {
			//	String dburl = "jdbc:oracle:oci:@"  + dbmonitorlist.getDbName();
				String dburl = "jdbc:oracle:thin:@" + serverip + ":" + port+ ":" + dbmonitorlist.getDbName();
				util = new OracleJdbcUtil(dburl, dbmonitorlist.getUser(),
						EncryptUtil.decode(dbmonitorlist.getPassword()));
				util.jdbc();

				// *********************************取数据 start
				ResultSet rs = null;
				try {
					// 数据库系统信息
					String sqlAsm = "select * from v$asm_client";
					try {
						rs = util.stmt.executeQuery(sqlAsm);
						while (rs.next()) {
								// 得到该数据库的disk
							asmHash = new Hashtable();
							asmHash.put("GROUP_NUMBER", rs.getString("GROUP_NUMBER")
										.toString());
							asmHash.put("DB_NAME", rs.getString("DB_NAME")
										.toString());
							asmHash.put("STATUS", rs.getString("STATUS")
									.toString());
							asmHash.put("SOFTWARE_VERSION", rs.getString("SOFTWARE_VERSION")
										.toString());
							asmHash.put("COMPATIBLE_VERSION", rs.getString("COMPATIBLE_VERSION")
									.toString());
							
							asm_vector.add(asmHash);
						}

					} catch (Exception e) {

					} finally {
						if (rs != null)
							rs.close();
					}

					returndata.put("asmClientkHash", asm_vector);
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
				oracleHash.put("asmClientkHash", returndata.get("asmClientkHash"));
			}

			// ----------------------------------保存到数据库及告警 start
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			// asmClient
			Vector asmClientVector = (Vector) returndata.get("asmClientkHash");
			if (asmClientVector != null && !asmClientVector.isEmpty()) {
				String hex = IpTranslation.formIpToHex(dbmonitorlist
						.getIpAddress());
				serverip = hex + ":" + dbmonitorlist.getId();

				try {
					Calendar tempCal = Calendar.getInstance();
					Date cc = tempCal.getTime();
					String montime = sdf.format(cc);

					String deletesql = "delete from nms_oraasmclient where serverip='"
							+ serverip + "'";
					GathersqlListManager.Addsql(deletesql);
					
					for (int k = 0; k < asmClientVector.size(); k++) {
						Hashtable asmClientValue = (Hashtable)asmClientVector.get(k);
						String GROUP_NUMBER = asmClientValue.get("GROUP_NUMBER")
								.toString();
						// String HOST_NAME = sysValue.get("HOST_NAME").toString();
						String DB_NAME = asmClientValue.get("DB_NAME").toString();
						String STATUS = asmClientValue.get("STATUS").toString();
						String SOFTWARE_VERSION = asmClientValue.get("SOFTWARE_VERSION").toString();
						String COMPATIBLE_VERSION = asmClientValue.get("COMPATIBLE_VERSION").toString();
						// Vector banners = (Vector) sysValue.get("BANNER");
						String insertsql = "insert into nms_oraasmclient(serverip,GROUP_NUMBER,DB_NAME,STATUS,SOFTWARE_VERSION,COMPATIBLE_VERSION,mon_time) "
								+ "values('"
								+ serverip
								+ "','"
								+ GROUP_NUMBER
								+ "','"
								+ DB_NAME
								+ "','"
								+ STATUS
								+ "','"
								+ SOFTWARE_VERSION
								+ "','"
								+ COMPATIBLE_VERSION;
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
							if ("asmclient"
									.equalsIgnoreCase(alarmIndicatorsNode
											.getName())) {
								String asmclientstatus = "1";
								for(int j = 0; j < asmClientVector.size(); j++){
									Hashtable asmClientValue = (Hashtable)asmClientVector.get(j);
									String STATUS = asmClientValue.get("STATUS").toString();
									if(!STATUS.trim().equals("CONNECTED")){
										asmclientstatus = "-1";
									}
								}
								checkEventUtil.checkEvent(nodeDTO,
										alarmIndicatorsNode, asmclientstatus, "asmclient");
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
