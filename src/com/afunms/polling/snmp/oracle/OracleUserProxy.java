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
import com.afunms.application.util.IpTranslation;
import com.afunms.common.util.EncryptUtil;
import com.afunms.common.util.OracleJdbcUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SystemConstant;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.gatherdb.GathersqlListManager;

/**
 * Oracle userinfo 采集 使用JDBC采集
 * 
 * @author wupinlong 2012/09/17
 * 
 */
public class OracleUserProxy extends SnmpMonitor {

	public OracleUserProxy() {
	}

	@SuppressWarnings("unchecked")
	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicators) {
		Hashtable returndata = new Hashtable();
		String htKey = "user";
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
			String[] args = new String[]{"parsing_user_id", "cpu_time", "sorts", "buffer_gets", "runtime_mem", "version_count", "disk_reads", "label", "mon_time"};
			htKey = "returnVal0";
			Hashtable users = LogParser.parse(fileContent, htKey, args);

			args = new String[]{"users", "label", "mon_time"};
			htKey = "returnVal1";
			users.put(htKey, (Vector)LogParser.parse(fileContent, htKey, args).get(htKey));
			
			args = new String[]{"username", "user_id", "account_status", "label", "mon_time"};
			htKey = "returnVal2";
			users.put(htKey, (Vector)LogParser.parse(fileContent, htKey, args).get(htKey));
			
			returndata.put("user", users);

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
				oracleHash.put("userinfo_h", returndata.get("user"));
			}

			// 保存至数据库中
			Hashtable data = (Hashtable) returndata.get("user");
			if (data != null && !data.isEmpty()) {
				String hex = IpTranslation.formIpToHex(dbmonitorlist
						.getIpAddress());
				serverip = hex + ":" + dbmonitorlist.getId();

				try {
					Calendar tempCal = Calendar.getInstance();
					Date cc = tempCal.getTime();
					SimpleDateFormat sdf = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					String montime = sdf.format(cc);
					String deletesql = "delete from nms_orauserinfo where serverip='"
							+ serverip + "'";
					// 加入队列
					GathersqlListManager.Addsql(deletesql);

					Vector returnVal = null;
					Vector returnVal1 = null;
					Vector returnVal2 = null;
					if (data != null) {
						returnVal = (Vector) data.get("returnVal0");
						returnVal1 = (Vector) data.get("returnVal1");
						returnVal2 = (Vector) data.get("returnVal2");
					}
					StringBuffer sBuffer = null;
					try {
						// label:0 只插入returnVal的信息 label:1 只插入returnVal1的信息
						// label:2只插入returnVal2的信息
						String label = "0";
						if (returnVal != null && returnVal.size() > 0) {
							for (int i = 0; i < returnVal.size(); i++) {
								Hashtable returnHash = (Hashtable) returnVal
										.get(i);
								sBuffer = new StringBuffer();
								sBuffer = new StringBuffer();
								sBuffer
										.append("insert into nms_orauserinfo(serverip, parsing_user_id, cpu_time, sorts, buffer_gets, "
												+ "runtime_mem, version_count, disk_reads,label, mon_time) ");
								sBuffer.append(" values('");
								sBuffer.append(serverip);
								sBuffer.append("','");
								sBuffer.append(String.valueOf(returnHash
										.get("parsing_user_id")));
								sBuffer.append("','");
								sBuffer.append(String.valueOf(returnHash
										.get("cpu_time")));
								sBuffer.append("','");
								sBuffer.append(String.valueOf(returnHash
										.get("sorts")));
								sBuffer.append("','");
								sBuffer.append(String.valueOf(returnHash
										.get("buffer_gets")));
								sBuffer.append("','");
								sBuffer.append(String.valueOf(returnHash
										.get("runtime_mem")));
								sBuffer.append("','");
								sBuffer.append(String.valueOf(returnHash
										.get("version_count")));
								sBuffer.append("','");
								sBuffer.append(String.valueOf(returnHash
										.get("disk_reads")));
								sBuffer.append("','");
								sBuffer.append(label);
								if ("mysql"
										.equalsIgnoreCase(SystemConstant.DBType)) {
									sBuffer.append("','");
									sBuffer.append(montime);
									sBuffer.append("')");
								} else if ("oracle"
										.equalsIgnoreCase(SystemConstant.DBType)) {
									sBuffer.append("',");
									sBuffer.append("to_date('" + montime
											+ "','YYYY-MM-DD HH24:MI:SS')");
									sBuffer.append(")");
								}
								GathersqlListManager.Addsql(sBuffer.toString());
							}
						}

						label = "1";
						if (returnVal1 != null && returnVal1.size() > 0) {
							for (int i = 0; i < returnVal1.size(); i++) {
								sBuffer = new StringBuffer();
								Hashtable returnHash = (Hashtable) returnVal1
										.get(i);
								sBuffer = new StringBuffer();
								sBuffer
										.append("insert into nms_orauserinfo(serverip, users,label, mon_time) ");
								sBuffer.append(" values('");
								sBuffer.append(serverip);
								sBuffer.append("','");
								sBuffer.append(String.valueOf(returnHash
										.get("users")));
								sBuffer.append("','");
								sBuffer.append(label);
								if ("mysql"
										.equalsIgnoreCase(SystemConstant.DBType)) {
									sBuffer.append("','");
									sBuffer.append(montime);
									sBuffer.append("')");
								} else if ("oracle"
										.equalsIgnoreCase(SystemConstant.DBType)) {
									sBuffer.append("',");
									sBuffer.append("to_date('" + montime
											+ "','YYYY-MM-DD HH24:MI:SS')");
									sBuffer.append(")");
								}
								GathersqlListManager.Addsql(sBuffer.toString());
							}
						}
						label = "2";
						if (returnVal2 != null && returnVal2.size() > 0) {
							for (int i = 0; i < returnVal2.size(); i++) {
								sBuffer = new StringBuffer();
								Hashtable returnHash = (Hashtable) returnVal2
										.get(i);
								sBuffer = new StringBuffer();
								sBuffer
										.append("insert into nms_orauserinfo(serverip,username, user_id, account_status,label, mon_time) ");
								sBuffer.append(" values('");
								sBuffer.append(serverip);
								sBuffer.append("','");
								sBuffer.append(String.valueOf(returnHash
										.get("username")));
								sBuffer.append("','");
								sBuffer.append(String.valueOf(returnHash
										.get("user_id")));
								sBuffer.append("','");
								sBuffer.append(String.valueOf(returnHash
										.get("account_status")));
								sBuffer.append("','");
								sBuffer.append(label);
								if ("mysql"
										.equalsIgnoreCase(SystemConstant.DBType)) {
									sBuffer.append("','");
									sBuffer.append(montime);
									sBuffer.append("')");
								} else if ("oracle"
										.equalsIgnoreCase(SystemConstant.DBType)) {
									sBuffer.append("',");
									sBuffer.append("to_date('" + montime
											+ "','YYYY-MM-DD HH24:MI:SS')");
									sBuffer.append(")");
								}
								GathersqlListManager.Addsql(sBuffer.toString());
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					// ---------------------------------userinfo不需告警

				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		}
		return returndata;
	}

}
