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

import com.afunms.application.dao.DBDao;
import com.afunms.application.model.DBVo;
import com.afunms.application.model.Oracle_sessiondata;
import com.afunms.application.util.IpTranslation;
import com.afunms.common.util.EncryptUtil;
import com.afunms.common.util.OracleJdbcUtil;
import com.afunms.common.util.ShareData;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;

/**
 * Oracle 会话信息 采集 使用JDBC采集
 * 
 * @author wupinlong 2012/09/17
 * 
 */
public class OracleSessionProxy extends SnmpMonitor {

	public OracleSessionProxy() {
	}

	@SuppressWarnings("unchecked")
	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicators) {
		Hashtable returndata = new Hashtable();
		String htKey = "session";
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
			String[] args = new String[]{"command","logontime","machine","program","sessiontype","status","username","dbname","mon_time"};
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
				oracleHash.put("info_v", returndata.get(htKey));
			}

			// ----------------------------------保存到数据库及告警 start
			Vector info_v = (Vector) returndata.get(htKey);
			if (info_v != null && !info_v.isEmpty()) {
				String hex = IpTranslation.formIpToHex(dbmonitorlist
						.getIpAddress());
				serverip = hex + ":" + dbmonitorlist.getId();

				try {
					// 将原来实时表里的SESSION数据清除
					DBDao dbdao = new DBDao();
					try {
						dbdao.clear_nmssessiondata(serverip);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						dbdao.close();
					}
					// 数据库的SESSION入历史表里
					SimpleDateFormat sdf1 = new SimpleDateFormat(
							"yyyy-MM-dd HH-mm-ss");
					for (int j = 0; j < info_v.size(); j++) {
						Oracle_sessiondata os = new Oracle_sessiondata();
						Hashtable ht = (Hashtable) info_v.get(j);
						String machine = ht.get("machine").toString();
						String usernames = ht.get("username").toString();
						String program = ht.get("program").toString();
						String status = ht.get("status").toString();
						String sessiontype = ht.get("sessiontype").toString();
						String command = ht.get("command").toString();
						String logontime = ht.get("logontime").toString();
						String dbname = ht.get("dbname").toString();
						os.setCommand(command);
						os.setLogontime(sdf1.parse(logontime));
						os.setMachine(machine);
						Calendar _tempCal = Calendar.getInstance();
						Date _cc = _tempCal.getTime();
						os.setMon_time(_cc);
						os.setProgram(program);
						os.setSessiontype(sessiontype);
						os.setStatus(status);
						os.setUsername(usernames);
						os.setServerip(serverip);
						os.setDbname(dbname);
						
						dbdao=new DBDao();
						try {
							// 存入历史表
							dbdao.addOracle_sessiondata(os);
							// 存入实时表
							dbdao.addOracle_nmssessiondata(os);
						} catch (Exception e) {
							e.printStackTrace();
						}finally{
							dbdao.close();
						}
						// ---------------------------------session不需告警
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
