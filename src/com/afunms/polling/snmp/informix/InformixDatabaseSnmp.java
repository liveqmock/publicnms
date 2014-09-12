package com.afunms.polling.snmp.informix;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import com.afunms.application.model.DBVo;
import com.afunms.application.util.IpTranslation;
import com.afunms.common.util.EncryptUtil;
import com.afunms.common.util.InformixJdbcUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SystemConstant;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.gatherdb.GathersqlListManager;

public class InformixDatabaseSnmp extends SnmpMonitor {
	
	public InformixDatabaseSnmp() {
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
			String createuser = "";// 创建者
			String createtime = "";// 创建时间
			String log = "";// 日志记录是否活动
			String bufflog = "";// 日志记录是否已缓存
			String ansi = "";// 是否符合ansi
			String gls = "";// 是否启动gls
			String serverip = dbmonitorlist.getIpAddress();
			String dbnames = dbmonitorlist.getDbName();
			String username = dbmonitorlist.getUser();
			int port = Integer.parseInt(dbmonitorlist.getPort());
			String dbservername = dbmonitorlist.getAlias();//临时的服务名称
			ArrayList databaselist = new ArrayList();// 日志文件信息
			String dbname = dbnames; // 数据库名称
			String dbserver = dbservername;// 对应的服务名称
			InformixJdbcUtil util = null;
			try {
				String passwords = EncryptUtil.decode(dbmonitorlist.getPassword());
				String dburl = "jdbc:informix-sqli://" + serverip + ":" + port + "/"
				+ "sysmaster" + ":INFORMIXSERVER=" + dbservername + "; user="
				+ username + ";password=" + passwords; // myDB为数据库名
				util = new InformixJdbcUtil(dburl, username, passwords);
				util.jdbc();

				// *********************************取数据 start
				String sqlLog = "select * from sysdatabases where name='" + dbnames + "'";
				ResultSet rs = null;
				try {
					// 日志文件
					rs = util.stmt.executeQuery(sqlLog);
					while (rs.next()) {
						Hashtable databaseVal = new Hashtable();
						createuser = rs.getString("owner");// 创建者
						createtime = rs.getString("created");// 创建时间
						if (rs.getInt("is_logging") == 1) {
							log = "日志记录是活动的";// 日志记录是否活动
						} else {
							log = "日志记录是不活动的";
						}
						if (rs.getInt("is_buff_log") == 1) {
							bufflog = "日志记录是已缓存";
						} else {
							bufflog = "日志记录未缓存";
						}
						if (rs.getInt("is_ansi") == 1) {
							ansi = "符合ansi";
						} else {
							ansi = "不符合ansi";
						}
						if (rs.getInt("is_nls") == 1) {
							gls = "已启动gls";
						} else {
							gls = "没有启动gls";
						}
						databaseVal.put("dbname", dbname);
						databaseVal.put("dbserver", dbserver);
						databaseVal.put("createuser", createuser);
						databaseVal.put("createtime", createtime);
						databaseVal.put("log", log);
						databaseVal.put("bufflog", bufflog);
						databaseVal.put("ansi", ansi);
						databaseVal.put("gls", gls);
						databaselist.add(databaseVal);
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
			returndata.put("databaselist", databaselist);// 日志信息
			// 更新内存
			Hashtable informixData = new Hashtable();
			informixData.put("informix", returndata);
			monitorValue.put(dbnames, informixData);
//			 更新内存
			if (!(ShareData.getInformixmonitordata().containsKey(serverip))) {
				ShareData.setInfomixmonitordata(serverip, monitorValue);
			} else {
				Hashtable informixHash = (Hashtable) ShareData.getInformixmonitordata().get(serverip);
				((Hashtable)((Hashtable)informixHash.get(dbnames)).get("informix")).put("databaselist", returndata.get("databaselist"));
			}
			// ----------------------------------保存到数据库及告警 start
			ArrayList databaseList = (ArrayList)returndata.get("databaselist");
			if(databaseList != null && databaseList.size()>0){

				String hex = IpTranslation.formIpToHex(dbmonitorlist.getIpAddress());
				serverip = hex + ":" + dbnames;
				try {
					Calendar tempCal = Calendar.getInstance();
					Date cc = tempCal.getTime();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String montime = sdf.format(cc);

					String deletesql = "delete from nms_informixdatabase where serverip='"+serverip+"'";
					GathersqlListManager.Addsql(deletesql);
					String insertsql = null;
					for(int i=0;i<databaseList.size();i++){
						Hashtable database = (Hashtable)databaseList.get(i);
						if(database != null && database.size()>0){
							try {
								StringBuffer sBuffer = new StringBuffer();
								sBuffer
										.append("insert into nms_informixdatabase(serverip, bufflog, createtime,log,dbserver ,gls,createuser,ansi,dbname,mon_time)");
								sBuffer.append(" values('");
								sBuffer.append(serverip);
								sBuffer.append("','");
								sBuffer.append(String.valueOf(database.get("bufflog")));
								sBuffer.append("','");
								sBuffer.append(String.valueOf(database.get("createtime")));
								sBuffer.append("','");
								sBuffer.append(String.valueOf(database.get("log")));
								sBuffer.append("','");
								sBuffer.append(String.valueOf(database.get("dbserver")));
								sBuffer.append("','");
								sBuffer.append(String.valueOf(database.get("gls")));
								sBuffer.append("','");
								sBuffer.append(String.valueOf(database.get("createuser")));
								sBuffer.append("','");
								sBuffer.append(String.valueOf(database.get("ansi")));
								sBuffer.append("','");
								sBuffer.append(String.valueOf(database.get("dbname")));
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
