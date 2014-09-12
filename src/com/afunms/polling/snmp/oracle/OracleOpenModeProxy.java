package com.afunms.polling.snmp.oracle;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.application.dao.DBDao;
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
 * Oracle 监听状态 采集 使用JDBC采集
 * 
 */
public class OracleOpenModeProxy extends SnmpMonitor {

	public OracleOpenModeProxy() {
	}

	@SuppressWarnings("unchecked")
	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicators) {
		Hashtable returndata = new Hashtable();
		String htKey = "openmode";
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
			String[] args = new String[]{"lstrnstatu","status","mon_time"};
			returndata = LogParser.parse(this, dbmonitorlist, htKey, args);

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
				oracleHash.put("lstrnStatu", returndata.get(htKey));
			}

			// ----------------------------------保存到数据库及告警 start
			Vector logFile_v = (Vector) returndata.get(htKey);
			if (logFile_v != null && !logFile_v.isEmpty()) {
				returndata = (Hashtable)logFile_v.get(0);
				String lstrnStatu = (String) returndata.get("lstrnstatu");
				String status = (String) returndata.get("status");
				if (lstrnStatu != null) {
					String hex = IpTranslation.formIpToHex(dbmonitorlist
							.getIpAddress());
					serverip = hex + ":" + dbmonitorlist.getId();
	
					try {
						Calendar tempCal = Calendar.getInstance();
						Date cc = tempCal.getTime();
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						String montime = sdf.format(cc);
	
						String deletesql = "delete from nms_orastatus where serverip='"
								+ serverip + "'";
						GathersqlListManager.Addsql(deletesql);
						String insertsql = "insert into nms_orastatus(serverip, lstrnstatu, status,mon_time) "
								+ "values('"
								+ serverip
								+ "','"
								+ lstrnStatu
								+ "','" + status;
						if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
							insertsql = insertsql + "','" + montime + "')";
						} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
							insertsql = insertsql + "',to_date('" + montime
									+ "','YYYY-MM-DD HH24:MI:SS'))";
						}
						GathersqlListManager.Addsql(insertsql);
						// ---------------------------------open_mode不需告警
	
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}
			// ----------------------------------保存到数据库及告警 end

		}
		return returndata;
	}

}
