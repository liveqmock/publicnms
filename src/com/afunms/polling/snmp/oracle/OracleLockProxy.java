package com.afunms.polling.snmp.oracle;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.application.model.DBVo;
import com.afunms.application.model.OracleLockInfo;
import com.afunms.application.util.IpTranslation;
import com.afunms.common.util.EncryptUtil;
import com.afunms.common.util.OracleJdbcUtil;
import com.afunms.common.util.ReflactUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SystemConstant;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.gatherdb.GathersqlListManager;

/**
 * Oracle 锁信息 采集 使用JDBC采集
 * 
 * @author wupinlong 2012/09/17
 * 
 */
public class OracleLockProxy extends SnmpMonitor {

	public OracleLockProxy() {
	}

	/**
	 * @param nodeGatherIndicators
	 * @return
	 */
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
			fileContent = LogParser.getDataFromLogfile(this,dbmonitorlist);
			String[] args = new String[]{"username","status","machine","sessiontype","logontime","program","locktype","lmode","requeststr","mon_time"};
			String htKey = "lock";
			Hashtable returndata1 = LogParser.parse(fileContent, htKey, args);
			returndata.put(htKey, (Vector)returndata1.get(htKey));
			
			// ----------------------------------保存到数据库及告警 start
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			// lockinfo_v
			Vector lockinfo_v = (Vector) returndata1.get(htKey);
			if (lockinfo_v != null && !lockinfo_v.isEmpty()) {
				String hex = IpTranslation.formIpToHex(dbmonitorlist
						.getIpAddress());
				serverip = hex + ":" + dbmonitorlist.getId();

				try {
					Calendar tempCal = Calendar.getInstance();
					Date cc = tempCal.getTime();
					
					String montime = sdf.format(cc);

					String deletesql = "delete from nms_oralock where serverip='"
							+ serverip + "'";
					GathersqlListManager.Addsql(deletesql);

					String insertsql = null;
					for (int k = 0; k < lockinfo_v.size(); k++) {
						Hashtable ht = (Hashtable) lockinfo_v.get(k);
						String usernames = ht.get("username").toString().trim();
						String status = ht.get("status").toString().trim();
						String machine = ht.get("machine").toString().trim();
						String sessiontype = ht.get("sessiontype").toString()
								.trim();
						String logontime = ht.get("logontime").toString()
								.trim();
						String program = ht.get("program").toString().trim();
						String locktype = ht.get("locktype").toString().trim();
						String lmode = ht.get("lmode").toString().trim();
						String requeststr = ht.get("requeststr").toString().trim();
						insertsql = "";
						insertsql = "insert into nms_oralock(serverip,username,status,machine,sessiontype,logontime,program,locktype,lmode,requeststr,mon_time) "
								+ "values('"
								+ serverip
								+ "','"
								+ usernames
								+ "','"
								+ status
								+ "','"
								+ machine
								+ "','"
								+ sessiontype
								+ "','"
								+ logontime
								+ "','"
								+ program
								+ "','"
								+ locktype
								+ "','"
								+ lmode
								+ "','" + requeststr;
						if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
							insertsql = insertsql + "','" + montime + "')";
						} else if ("oracle"
								.equalsIgnoreCase(SystemConstant.DBType)) {
							insertsql = insertsql + "',to_date('" + montime
									+ "','YYYY-MM-DD HH24:MI:SS'))";
						}
						GathersqlListManager.Addsql(insertsql);
					}
					// ---------------------------------lock不需告警
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			args = new String[]{"entity", "subentity", "thevalue", "mon_time"};
			htKey = "oracleLockInfo";
			Hashtable returndata2 = LogParser.parse(fileContent, htKey, args);
			returndata.put(htKey, (Vector)returndata2.get(htKey));
			
			// oracleLockInfo
			lockinfo_v = (Vector) returndata2.get(htKey);
			if (lockinfo_v != null && !lockinfo_v.isEmpty()) {
				String hex = IpTranslation.formIpToHex(dbmonitorlist
						.getIpAddress());
				serverip = hex + ":" + dbmonitorlist.getId();
				try {
					Calendar tempCal = Calendar.getInstance();
					Date cc = tempCal.getTime();
					String montime = sdf.format(cc);

					String deletesql = "delete from nms_oralockinfo where serverip='"
							+ serverip + "'";
					GathersqlListManager.Addsql(deletesql);
					
					StringBuffer insertsql = null;
					for (int k = 0; k < lockinfo_v.size(); k++) {
						Hashtable ht = (Hashtable) lockinfo_v.get(k);
						insertsql = new StringBuffer();
						insertsql
								.append("insert into nms_oralockinfo(serverip,entity,subentity,thevalue,mon_time) values ('");
						insertsql.append(serverip);
						insertsql.append("','");
						insertsql.append(ht.get("entity").toString().trim());
						insertsql.append("','");
						insertsql.append(ht.get("subentity").toString().trim());
						insertsql.append("','");
						insertsql.append(ht.get("thevalue").toString().trim());
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
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
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
				oracleHash.put("lockinfo_v", returndata.get("lock"));
				returndata.put("oracleLockInfo", returndata.get("oracleLockInfo"));
			}
			// ----------------------------------保存到数据库及告警 end

		}
		return returndata;
	}

}
