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

public class InformixIOSnmp extends SnmpMonitor {
	
	public InformixIOSnmp() {
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
			ArrayList iolist = new ArrayList();// 日志文件信息
			InformixJdbcUtil util = null;
			try {
				String passwords = EncryptUtil.decode(dbmonitorlist.getPassword());
				String dburl = "jdbc:informix-sqli://" + serverip + ":" + port + "/"
				+ "sysmaster" + ":INFORMIXSERVER=" + dbservername + "; user="
				+ username + ";password=" + passwords; // myDB为数据库名
				util = new InformixJdbcUtil(dburl, username, passwords);
				util.jdbc();

				// *********************************取数据 start
				String sqlLog = "select * from syschkio";
				ResultSet rs = null;
				try {
					// 日志文件
					rs = util.stmt.executeQuery(sqlLog);
					while (rs.next()) {
						Hashtable informixio = new Hashtable();
						informixio.put("chunknum", rs.getString("chunknum"));// 块编号
						informixio.put("reads", rs.getInt("reads"));// 物理读取数
						informixio.put("pagesread", rs.getInt("pagesread"));// 读取的页数
						informixio.put("writes", rs.getInt("writes"));// 物理写入数
						informixio.put("pageswritten", rs
								.getInt("pageswritten"));// 写入的页数
						informixio.put("mreads", rs.getInt("mreads"));// 物理读取（镜像）数
						informixio.put("mpagesread", rs.getInt("mpagesread"));// 读取（镜像）的页数
						informixio.put("mwrites", rs.getInt("mwrites"));// 物理写入（镜像）数
						informixio.put("mpageswritten", rs
								.getInt("mpageswritten"));// 写入（镜像）的页数
						// SysLogger.info("pagesread ====
						// "+informixio.get("pagesread"));
						// SysLogger.info("pageswritten ====
						// "+informixio.get("pageswritten"));
						iolist.add(informixio);
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
			returndata.put("iolist", iolist);// 日志信息
			// 更新内存
			Hashtable informixData = new Hashtable();
			informixData.put("informix", returndata);
			monitorValue.put(dbnames, informixData);
//			 更新内存
			if (!(ShareData.getInformixmonitordata().containsKey(serverip))) {
				ShareData.setInfomixmonitordata(serverip, monitorValue);
			} else {
				Hashtable informixHash = (Hashtable) ShareData.getInformixmonitordata().get(serverip);
				((Hashtable)((Hashtable)informixHash.get(dbnames)).get("informix")).put("iolist", returndata.get("iolist"));
			}
			// ----------------------------------保存到数据库及告警 start
			List ioList = (ArrayList)returndata.get("iolist");
			if(ioList != null && ioList.size()>0){

				String hex = IpTranslation.formIpToHex(dbmonitorlist.getIpAddress());
				serverip = hex + ":" + dbnames;
				try {
					Calendar tempCal = Calendar.getInstance();
					Date cc = tempCal.getTime();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String montime = sdf.format(cc);

					String deletesql = "delete from nms_informixio where serverip='"+serverip+"'";
					GathersqlListManager.Addsql(deletesql);
					String insertsql = null;
					for(int i=0;i<ioList.size();i++){
						Hashtable io = (Hashtable)ioList.get(i);
						if(io != null && io.size()>0){
							try {
								StringBuffer sBuffer = new StringBuffer();
								sBuffer
										.append("insert into nms_informixio(serverip, pagesread,readsstr,writes,mwrites,chunknum,mreads,pageswritten,mpagesread,mpageswritten,mon_time)");
								sBuffer.append(" values('");
								sBuffer.append(serverip);
								sBuffer.append("','");
								sBuffer.append(String.valueOf(io.get("pagesread")));
								sBuffer.append("','");
								sBuffer.append(String.valueOf(io.get("reads")));
								sBuffer.append("','");
								sBuffer.append(String.valueOf(io.get("writes")));
								sBuffer.append("','");
								sBuffer.append(String.valueOf(io.get("mwrites")));
								sBuffer.append("','");
								sBuffer.append(String.valueOf(io.get("chunknum")));
								sBuffer.append("','");
								sBuffer.append(String.valueOf(io.get("mreads")));
								sBuffer.append("','");
								sBuffer.append(String.valueOf(io.get("pageswritten")));
								sBuffer.append("','");
								sBuffer.append(String.valueOf(io.get("mpagesread")));
								sBuffer.append("','");
								sBuffer.append(String.valueOf(io.get("mpageswritten")));
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
