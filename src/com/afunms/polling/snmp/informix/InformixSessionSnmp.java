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

public class InformixSessionSnmp extends SnmpMonitor {
	
	public InformixSessionSnmp() {
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
			ArrayList sessionList = new ArrayList();// 日志文件信息
			InformixJdbcUtil util = null;
			try {
				String passwords = EncryptUtil.decode(dbmonitorlist.getPassword());
				String dburl = "jdbc:informix-sqli://" + serverip + ":" + port + "/"
				+ "sysmaster" + ":INFORMIXSERVER=" + dbservername + "; user="
				+ username + ";password=" + passwords; // myDB为数据库名
				util = new InformixJdbcUtil(dburl, username, passwords);
				util.jdbc();

				// *********************************取数据 start
				String sqlLog = "select username, hostname,connected,(isreads+bufreads+bufwrites+pagreads+pagwrites) access,lockreqs,locksheld,lockwts,deadlks,lktouts,logrecs,longtxs,bufreads,bufwrites,seqscans,pagreads,pagwrites,total_sorts,dsksorts,max_sortdiskspace from syssessions s, syssesprof f where s.sid =f.sid";
				ResultSet rs = null;
				try {
					// 日志文件
					rs = util.stmt.executeQuery(sqlLog);
					while (rs.next()) {
						Hashtable informixsession = new Hashtable();
						informixsession.put("username", rs
								.getString("username"));// 用户名
						informixsession.put("hostname", rs
								.getString("hostname"));// 主机
						informixsession.put("connected", rs
								.getString("connected"));// 用户连接到数据库服务器上的时间
						informixsession.put("access", rs.getString("access"));// 命中次数
						informixsession.put("lockreqs", rs.getInt("lockreqs"));// 所请求锁的数量
						informixsession
								.put("locksheld", rs.getInt("locksheld"));// 当前持有锁的数量
						informixsession.put("lockwts", rs.getInt("lockwts"));// 等待锁的次数
						informixsession.put("deadlks", rs.getInt("deadlks"));// 检测到的死锁数量
						informixsession.put("lktouts", rs.getInt("lktouts"));// 死锁超时数
						/*
						 * informixsession.put("logrecs",
						 * rs.getInt("logrecs").trim());//已写入逻辑日志的记录数
						 * informixsession.put("longtxs",
						 * rs.getInt("longtxs").trim());//长事物数
						 */
						informixsession.put("bufreads", rs.getInt("bufreads"));// 缓冲区读取数
						informixsession
								.put("bufwrites", rs.getInt("bufwrites"));// 缓冲区写入数
						// informixsession.put("seqscans",
						// rs.getInt("seqscans").trim());//顺序扫描数
						informixsession.put("pagreads", rs.getInt("pagreads"));// 页读取数
						informixsession
								.put("pagwrites", rs.getInt("pagwrites"));// 页写入数
						/*
						 * informixsession.put("total_sorts",
						 * rs.getInt("total_sorts").trim());//排序总数
						 * informixsession.put("dsksorts",
						 * rs.getInt("dsksorts").trim());//不适合内存的排序数
						 * informixsession.put("max_sortdiskspace",
						 * rs.getInt("max_sortdiskspace").trim());//排序所使用的最大空间
						 */
						sessionList.add(informixsession);
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
			returndata.put("sessionList", sessionList);// 日志信息
			// 更新内存
			Hashtable informixData = new Hashtable();
			informixData.put("informix", returndata);
			monitorValue.put(dbnames, informixData);
//			 更新内存
			if (!(ShareData.getInformixmonitordata().containsKey(serverip))) {
				ShareData.setInfomixmonitordata(serverip, monitorValue);
			} else {
				Hashtable informixHash = (Hashtable) ShareData.getInformixmonitordata().get(serverip);
				((Hashtable)((Hashtable)informixHash.get(dbnames)).get("informix")).put("sessionList", returndata.get("sessionList"));
			}
			// ----------------------------------保存到数据库及告警 start
			List sessionlist = (ArrayList)returndata.get("sessionList");
			if(sessionlist != null && sessionlist.size()>0){

				String hex = IpTranslation.formIpToHex(dbmonitorlist.getIpAddress());
				serverip = hex + ":" + dbnames;
				try {
					Calendar tempCal = Calendar.getInstance();
					Date cc = tempCal.getTime();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String montime = sdf.format(cc);

					String deletesql = "delete from nms_informixsession where serverip='"+serverip+"'";
					GathersqlListManager.Addsql(deletesql);
					String insertsql = null;
					for(int i=0;i<sessionlist.size();i++){
						Hashtable session = (Hashtable)sessionList.get(i);
						if(session != null && session.size()>0){
							try {
								StringBuffer sBuffer = new StringBuffer();
								sBuffer
										.append("insert into nms_informixsession(serverip, bufwrites, pagwrites,pagreads,locksheld ,bufreads,accesses,");
								sBuffer
										.append("connected,username,lktouts,lockreqs,hostname,lockwts,deadlks,mon_time)");
								sBuffer.append(" values('");
								sBuffer.append(serverip);
								sBuffer.append("','");
								sBuffer.append(String.valueOf(session.get("bufwrites")));
								sBuffer.append("','");
								sBuffer.append(String.valueOf(session.get("pagwrites")));
								sBuffer.append("','");
								sBuffer.append(String.valueOf(session.get("pagreads")));
								sBuffer.append("','");
								sBuffer.append(String.valueOf(session.get("locksheld")));
								sBuffer.append("','");
								sBuffer.append(String.valueOf(session.get("bufreads")));
								sBuffer.append("','");
								sBuffer.append(String.valueOf(session.get("access")));
								sBuffer.append("','");
								sBuffer.append(String.valueOf(session.get("connected")));
								sBuffer.append("','");
								sBuffer.append(String.valueOf(session.get("username")));
								sBuffer.append("','");
								sBuffer.append(String.valueOf(session.get("lktouts")));
								sBuffer.append("','");
								sBuffer.append(String.valueOf(session.get("lockreqs")));
								sBuffer.append("','");
								sBuffer.append(String.valueOf(session.get("hostname")));
								sBuffer.append("','");
								sBuffer.append(String.valueOf(session.get("lockwts")));
								sBuffer.append("','");
								sBuffer.append(String.valueOf(session.get("deadlks")));
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
