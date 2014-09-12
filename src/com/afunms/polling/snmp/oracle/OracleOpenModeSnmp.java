package com.afunms.polling.snmp.oracle;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

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
 * @author wupinlong 2012/09/17
 * 
 */
public class OracleOpenModeSnmp extends SnmpMonitor {

	public OracleOpenModeSnmp() {
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
		if (dbmonitorlist != null) {
			if (dbmonitorlist.getManaged() == 0) {
				// 如果未被管理，不采集，user信息为空
				return returndata;
			}
			String serverip = dbmonitorlist.getIpAddress();
			int port = Integer.parseInt(dbmonitorlist.getPort());
			OracleJdbcUtil util = null;
			try {
				String dburl = "jdbc:oracle:thin:@" + serverip + ":" + port
						+ ":" + dbmonitorlist.getDbName();
				util = new OracleJdbcUtil(dburl, dbmonitorlist.getUser(),
						EncryptUtil.decode(dbmonitorlist.getPassword()));
				util.jdbc();

				// *********************************取数据 start
				String sqllstr = "select open_mode from v$database";
				ResultSet rs = null;
				// 监听状态
				String open_mode = null;
				try {
					rs = util.stmt.executeQuery(sqllstr);
					if (rs.next()) {
						open_mode = rs.getString("OPEN_MODE").toString();
					}
					returndata.put("open_mode", open_mode);
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
			// 取得是否能ping通
			boolean oracleIsOK = false;
			DBDao dbdao = new DBDao();
			String status = "0";
			try {
				oracleIsOK = dbdao.getOracleIsOK(serverip, port, dbmonitorlist
						.getDbName(), dbmonitorlist.getUser(), EncryptUtil
						.decode(dbmonitorlist.getPassword()));
			} catch (Exception e) {
				oracleIsOK = false;
			} finally {
				dbdao.close();
			}
			if (oracleIsOK) {
				status = "1";
			} else {
				status = "0";
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
				oracleHash.put("lstrnStatu", returndata.get("open_mode"));
			}

			// ----------------------------------保存到数据库及告警 start
			String lstrnStatu = (String) returndata.get("open_mode");

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
			// ----------------------------------保存到数据库及告警 end

		}
		return returndata;
	}

}
