package com.afunms.polling.snmp.oracle;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.afunms.application.model.DBVo;
import com.afunms.application.util.IpTranslation;
import com.afunms.common.util.ShareData;
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
public class OracleSysInfoProxy extends SnmpMonitor {

	public OracleSysInfoProxy() {
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
			String[] args = new String[] { "INSTANCE_NAME", "HOST_NAME",
					"DBNAME", "VERSION", "STARTUP_TIME", "CREATEED_TIME",
					"STATUS", "ARCHIVER", "java_pool", "mon_time" };
			String htKey = "sysinfo";
			Hashtable returndata1 = LogParser.parse(fileContent, htKey, args);

			// ----------------------------------保存到数据库及告警 start
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			Vector logFile_v = (Vector) returndata1.get(htKey);
			if (logFile_v != null && !logFile_v.isEmpty()) {
				returndata1 = (Hashtable) logFile_v.get(0);
				// sysinfo
				returndata.put(htKey, returndata1);
				if (returndata1 != null && !returndata1.isEmpty()) {
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

						String INSTANCE_NAME = returndata1.get("INSTANCE_NAME")
								.toString();
						String HOST_NAME = returndata1.get("HOST_NAME")
								.toString();
						String DBNAME = returndata1.get("DBNAME").toString();
						String VERSION = returndata1.get("VERSION").toString();
						String STARTUP_TIME = returndata1.get("STARTUP_TIME")
								.toString();
						String CREATEED_TIME = returndata1.get("CREATEED_TIME")
								.toString();
						String STATUS = returndata1.get("STATUS").toString();
						String ARCHIVER = returndata1.get("ARCHIVER")
								.toString();
						String java_pool = "";
						if (returndata1.get("java_pool") != null) {
							java_pool = returndata1.get("java_pool").toString();
						}
						String insertsql = "insert into nms_orasys(serverip,INSTANCE_NAME,HOST_NAME,DBNAME,VERSION,STARTUP_TIME,created_time,status,ARCHIVER,BANNER,java_pool,mon_time) "
								+ "values('"
								+ serverip
								+ "','"
								+ INSTANCE_NAME
								+ "','"
								+ HOST_NAME
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
								+ "','"
								+ ARCHIVER + "','" + "" + "','" + java_pool;
						if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
							insertsql = insertsql + "','" + montime + "')";
						} else if ("oracle"
								.equalsIgnoreCase(SystemConstant.DBType)) {
							insertsql = insertsql + "',to_date('" + montime
									+ "','YYYY-MM-DD HH24:MI:SS'))";
						}
						GathersqlListManager.Addsql(insertsql);
						// ---------------------------------sysinfo不需告警

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			// baseInfoHash
			args = new String[] { ".*" };
			htKey = "baseInfoHash";
			Hashtable returndata2 = LogParser.parse(fileContent, htKey,
					LogParser.lineseperator, args, "");
			returndata.put(htKey, returndata2);
			logFile_v = (Vector) returndata2.get(htKey);
			if (logFile_v != null && !logFile_v.isEmpty()) {
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

					StringBuffer insertsql = null;
					for (int k = 0; k < logFile_v.size(); k++) {
						Hashtable ht = (Hashtable) logFile_v.get(k);
						Iterator<String> keyIterator = ht.keySet().iterator();
						String subentity = keyIterator.next();
						String thevalue = (String) ht.get(subentity);
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
				} catch (Exception e) {
					e.printStackTrace();
				}
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
				oracleHash.put("baseInfoHash", returndata.get("baseInfoHash"));// TODO
			}
			// ----------------------------------保存到数据库及告警 end

		}
		return returndata;
	}

}
