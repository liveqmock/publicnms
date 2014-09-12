package com.afunms.polling.snmp.informix;

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
import com.afunms.common.util.InformixJdbcUtil;
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
public class InformixLogSnmp extends SnmpMonitor {
	
	public InformixLogSnmp() {
	}

	@SuppressWarnings("unchecked")
	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicators) {
		Hashtable returndata = new Hashtable();
		List dbmonitorlists = new ArrayList();
		Hashtable monitorValue = new Hashtable();
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
			String dbnames = dbmonitorlist.getDbName();
			String username = dbmonitorlist.getUser();
			int port = Integer.parseInt(dbmonitorlist.getPort());
			String dbservername = dbmonitorlist.getAlias();//临时的服务名称
			ArrayList loglist = new ArrayList();// 日志文件信息
			InformixJdbcUtil util = null;
			try {
				String passwords = EncryptUtil.decode(dbmonitorlist.getPassword());
				String dburl = "jdbc:informix-sqli://" + serverip + ":" + port + "/"
				+ "sysmaster" + ":INFORMIXSERVER=" + dbservername + "; user="
				+ username + ";password=" + passwords; // myDB为数据库名
				util = new InformixJdbcUtil(dburl, username, passwords);
				util.jdbc();

				// *********************************取数据 start
				String sqlLog = "select * from syslogs";
				ResultSet rs = null;
				try {
					// 日志文件
					rs = util.stmt.executeQuery(sqlLog);
					int uniqid = 0;// 日志文件标示
					int size = 0;// 日志文件的页数
					int used = 0;// 日志文件中已用的页数
					int is_used = 0;// 是否被使用（当前状态）
					int is_current = 0;// 是否是当前文件
					int is_backed_up = 0;// 是否已经备份过
					int is_archived = 0;// 是否已置于备份磁盘上
					int is_temp = 0;// 是否为临时日志文件
					while (rs.next()) {
						Hashtable informixlog = new Hashtable();
						try {
							uniqid = rs.getInt("uniqid");
							size = rs.getInt("size");
							used = rs.getInt("used");
							is_used = rs.getInt("is_used");
							is_current = rs.getInt("is_current");
							is_backed_up = rs.getInt("is_backed_up");
							is_archived = rs.getInt("is_archived");
							is_temp = rs.getInt("is_temp");
							informixlog.put("uniqid", uniqid);
							informixlog.put("size", size);
							informixlog.put("used", used);
							if (is_used == 1) {
								informixlog.put("is_used", "true");
							} else {
								informixlog.put("is_used", "false");
							}
							if (is_current == 1) {
								informixlog.put("is_current", "true");
							} else {
								informixlog.put("is_current", "false");
							}
							if (is_backed_up == 1) {
								informixlog.put("is_backed_up", "true");
							} else {
								informixlog.put("is_backed_up", "false");
							}
							if (is_archived == 1) {
								informixlog.put("is_archived", "true");
							} else {
								informixlog.put("is_archived", "false");
							}
							if (is_temp == 1) {
								informixlog.put("is_temp", "true");
							} else {
								informixlog.put("is_temp", "false");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						loglist.add(informixlog);
					}

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
			returndata.put("informixlog", loglist);// 日志信息
			// 更新内存
			Hashtable informixData = new Hashtable();
			informixData.put("informix", returndata);
			monitorValue.put(dbnames, informixData);
//			 更新内存
			if (!(ShareData.getInformixmonitordata().containsKey(serverip))) {
				ShareData.setInfomixmonitordata(serverip, monitorValue);
			} else {
				Hashtable informixHash = (Hashtable) ShareData.getInformixmonitordata().get(serverip);
				((Hashtable)((Hashtable)informixHash.get(dbnames)).get("informix")).put("informixlog", returndata.get("informixlog"));
			}
			// ----------------------------------保存到数据库及告警 start
			List informixlog = (ArrayList)returndata.get("informixlog");
			if(informixlog != null && informixlog.size()>0){

				String hex = IpTranslation.formIpToHex(dbmonitorlist.getIpAddress());
				serverip = hex + ":" + dbnames;

				try {
					Calendar tempCal = Calendar.getInstance();
					Date cc = tempCal.getTime();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String montime = sdf.format(cc);

					String deletesql = "delete from nms_informixlog where serverip='"+serverip+"'";
					GathersqlListManager.Addsql(deletesql);
					String insertsql = null;
					for(int i=0;i<informixlog.size();i++){
						Hashtable log = (Hashtable)informixlog.get(i);
						if(log != null && log.size()>0){
							try {
								StringBuffer sBuffer = new StringBuffer();
								sBuffer.append("insert into nms_informixlog(serverip, is_backed_up, is_current,sizes,used ,is_temp,uniqid,is_archived,is_used,mon_time)");
								sBuffer.append(" values('");
								sBuffer.append(serverip);
								sBuffer.append("','");
								sBuffer.append(String.valueOf(log.get("is_backed_up")));
								sBuffer.append("','");
								sBuffer.append(String.valueOf(log.get("is_current")));
								sBuffer.append("','");
								sBuffer.append(String.valueOf(log.get("size")));
								sBuffer.append("','");
								sBuffer.append(String.valueOf(log.get("used")));
								sBuffer.append("','");
								sBuffer.append(String.valueOf(log.get("is_temp")));
								sBuffer.append("','");
								sBuffer.append(String.valueOf(log.get("uniqid")));
								sBuffer.append("','");
								sBuffer.append(String.valueOf(log.get("is_archived")));
								sBuffer.append("','");
								sBuffer.append(String.valueOf(log.get("is_used")));
								// sBuffer.append("','");
								// sBuffer.append(montime);
								// sBuffer.append("')");
								if (SystemConstant.DBType.equals("mysql")) {
									sBuffer.append("','");
									sBuffer.append(montime);
									sBuffer.append("')");
								} else if (SystemConstant.DBType.equals("oracle")) {
									sBuffer.append("',to_date('" + montime
											+ "','yyyy-mm-dd hh24:mi:ss'))");
								}
								insertsql = sBuffer.toString();
								GathersqlListManager.Addsql(insertsql);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
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
