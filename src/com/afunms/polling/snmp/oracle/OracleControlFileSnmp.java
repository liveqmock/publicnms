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
import com.afunms.common.util.CommonUtil;
import com.afunms.common.util.EncryptUtil;
import com.afunms.common.util.OracleJdbcUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SystemConstant;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.gatherdb.GathersqlListManager;

/**
 * Oracle 控制文件 采集 使用JDBC采集
 * 
 * @author wupinlong 2012/09/17
 * 
 */
public class OracleControlFileSnmp extends SnmpMonitor {

	public OracleControlFileSnmp() {
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
				String sqlcf = "select * from v$controlfile";
				ResultSet rs = null;
				try {
					// 控制文件
					Vector returnVal7 = new Vector();
					rs = util.stmt.executeQuery(sqlcf);
					ResultSetMetaData rsmd7 = rs.getMetaData();
					while (rs.next()) {
						Hashtable return_value = new Hashtable();
						for (int i = 1; i <= rsmd7.getColumnCount(); i++) {
							String col = rsmd7.getColumnName(i);
							if (rs.getString(i) != null) {
								String tmp = rs.getString(i).toString();
								return_value.put(col.toLowerCase(), tmp);
							} else
								return_value.put(col.toLowerCase(), "--");
						}
						returnVal7.addElement(return_value);
						return_value = null;
					}
					returndata.put("controlfile", returnVal7);
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
				oracleHash.put("contrFile_v", returndata.get("controlfile"));
			}

			// ----------------------------------保存到数据库及告警 start
			Vector contrFile_v = (Vector) returndata.get("controlfile");
			if (contrFile_v != null && !contrFile_v.isEmpty()) {
				String hex = IpTranslation.formIpToHex(dbmonitorlist
						.getIpAddress());
				serverip = hex + ":" + dbmonitorlist.getId();

				try {
					Calendar tempCal = Calendar.getInstance();
					Date cc = tempCal.getTime();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String montime = sdf.format(cc);

					String deletesql = "delete from nms_oracontrfile where serverip='"
							+ serverip + "'";
					GathersqlListManager.Addsql(deletesql);
					String insertsql = null;
					for (int k = 0; k < contrFile_v.size(); k++) {
						Hashtable ht = (Hashtable) contrFile_v.get(k);
						String status = ht.get("status").toString();
						String name = ht.get("name").toString();
						String is_recovery_dest_file = CommonUtil.getValue(ht,
								"is_recovery_dest_file", "--");
						String block_size = CommonUtil.getValue(ht,
								"block_size", "--");
						String file_size_blks = CommonUtil.getValue(ht,
								"file_size_blks", "--");
						name = name.replaceAll("\\\\", "/");
						insertsql = "insert into nms_oracontrfile(serverip,status,name,is_recovery_dest_file,block_size,file_size_blks,mon_time) "
								+ "values('"
								+ serverip
								+ "','"
								+ status
								+ "','"
								+ name
								+ "','"
								+ is_recovery_dest_file
								+ "','" + block_size + "','" + file_size_blks;
						if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
							insertsql = insertsql + "','" + montime + "')";
						} else if ("oracle"
								.equalsIgnoreCase(SystemConstant.DBType)) {
							insertsql = insertsql + "',to_date('" + montime
									+ "','YYYY-MM-DD HH24:MI:SS'))";
						}
						GathersqlListManager.Addsql(insertsql);
					}
					// ---------------------------------controlfile不需告警

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			// ----------------------------------保存到数据库及告警 end

		}
		return returndata;
	}

}
