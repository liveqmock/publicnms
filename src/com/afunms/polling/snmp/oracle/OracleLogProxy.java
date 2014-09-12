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
 * Oracle 日志文件信息 采集 使用JDBC采集
 * 
 * @author wupinlong 2012/09/17
 * 
 */
public class OracleLogProxy extends SnmpMonitor {
	
	public OracleLogProxy() {
	}

	@SuppressWarnings("unchecked")
	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicators) {
		Hashtable returndata = new Hashtable();
		String htKey = "log";
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
			String[] args = new String[]{"groupstr","status","type","member","mon_time"};
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
				oracleHash.put("logFile_v", returndata.get(htKey));
			}
			
			// ----------------------------------保存到数据库及告警 start
			Vector logFile_v = (Vector) returndata.get(htKey);
			if (logFile_v != null && !logFile_v.isEmpty()) {
				String hex = IpTranslation.formIpToHex(dbmonitorlist
						.getIpAddress());
				serverip = hex + ":" + dbmonitorlist.getId();

				try {
					Calendar tempCal = Calendar.getInstance();
					Date cc = tempCal.getTime();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String montime = sdf.format(cc);

					String deletesql = "delete from nms_oralogfile where serverip='"+serverip+"'";
					GathersqlListManager.Addsql(deletesql);
					for (int k = 0; k < logFile_v.size(); k++) {
						Hashtable ht = (Hashtable)logFile_v.get(k);
						String group = ht.get("groupstr").toString();
						String status = ht.get("status").toString();
						String type = ht.get("type").toString();
						String member = ht.get("member").toString();
						//String is_recovery_dest_file = CommonUtil.getValue(ht, "is_recovery_dest_file", "--");
						member = member.replaceAll("\\\\","/");
						String insertsql =  "insert into nms_oralogfile(serverip,groupstr,status,type,member,mon_time) "
							+ "values('"
							+ serverip
							+ "','"
							+ group
							+ "','"
							+ status
							+ "','"
							+ type
							+ "','"
							+ member;
							if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
								insertsql = insertsql + "','" + montime + "')";
							}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
								insertsql = insertsql + "',to_date('"+montime+"','YYYY-MM-DD HH24:MI:SS'))";
							}
							GathersqlListManager.Addsql(insertsql);
					}
					// ---------------------------------log不需告警

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			// ----------------------------------保存到数据库及告警 end
			
		}
		return returndata;
	}

}
