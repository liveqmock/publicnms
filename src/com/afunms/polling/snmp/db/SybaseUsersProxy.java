package com.afunms.polling.snmp.db;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
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
 * Oracle 内存统一采集 采集 使用JDBC采集
 */
public class SybaseUsersProxy extends SnmpMonitor {

	public SybaseUsersProxy() {
	}

	@SuppressWarnings("unchecked")
	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicators) {
		Hashtable returndata = new Hashtable();
		String htKey = "users";
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
					"users_name","id_in_db","group_name","login_name"
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
				sybaseHash.put("users", returndata.get(htKey));
			}

			// 保存至数据库中
			Vector extent_v = (Vector) returndata.get(htKey);
			if (extent_v != null && extent_v.size() >0 ) {
				String hex = IpTranslation.formIpToHex(dbmonitorlist
						.getIpAddress());
				String ip = hex + ":" + dbmonitorlist.getId();
				
				try {
					Calendar tempCal = Calendar.getInstance();
					Date cc = tempCal.getTime();
					SimpleDateFormat sdf = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					String montime = sdf.format(cc);
					String deletesql = "delete from nms_sybaseuserinfo where serverip='"
							+ ip + "'";
					// 加入队列
					GathersqlListManager.Addsql(deletesql);
					for (int k = 0; k < extent_v.size(); k++) {
						Hashtable infoValueHash = (Hashtable) extent_v.get(k);
						
						String users_name = String.valueOf(infoValueHash.get("users_name"));
						String id_in_db = String.valueOf(infoValueHash.get("id_in_db"));
						String group_name = String.valueOf(infoValueHash.get("group_name"));
						String login_name = String.valueOf(infoValueHash.get("login_name"));
						
						String insertsql = "insert into nms_sybaseuserinfo(serverip, users_name, id_in_db, group_name, login_name, mon_time) " + " values ('" + ip + "','" +
						users_name + "','" + id_in_db + "','" + group_name + "','" + login_name;
						if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
							insertsql = insertsql + "','" + montime + "')";
						} else if ("oracle"
								.equalsIgnoreCase(SystemConstant.DBType)) {
							insertsql = insertsql + "',to_date('" + montime
									+ "','YYYY-MM-DD HH24:MI:SS'))";
						}
						// 加入入库队列
						GathersqlListManager.Addsql(insertsql);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return returndata;
	}
}
