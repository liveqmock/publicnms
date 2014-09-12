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

public class InformixConfigSnmp extends SnmpMonitor {
	
	public InformixConfigSnmp() {
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
			ArrayList configList = new ArrayList();// 日志文件信息
			InformixJdbcUtil util = null;
			try {
				String passwords = EncryptUtil.decode(dbmonitorlist.getPassword());
				String dburl = "jdbc:informix-sqli://" + serverip + ":" + port + "/"
				+ "sysmaster" + ":INFORMIXSERVER=" + dbservername + "; user="
				+ username + ";password=" + passwords; // myDB为数据库名
				util = new InformixJdbcUtil(dburl, username, passwords);
				util.jdbc();

				// *********************************取数据 start
				String sqlLog = "select * from sysconfig";
				ResultSet rs = null;
				try {
					// 日志文件
					rs = util.stmt.executeQuery(sqlLog);
					while (rs.next()) {
						Hashtable informixconfig = new Hashtable();
						informixconfig.put("cf_name", rs.getString("cf_name")
								.trim());// 配置参数名
						informixconfig.put("cf_original", rs.getString(
								"cf_original").trim());// 引导时onconfig文件中的值
						informixconfig.put("cf_effective", rs.getString(
								"cf_effective").trim());// 当前正在使用的值
						informixconfig.put("cf_default", rs.getString(
								"cf_default").trim());// 如果onconfig文件没有指定值，数据库服务所提供的值
						configList.add(informixconfig);
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
			returndata.put("configList", configList);// 日志信息
			// 更新内存
			Hashtable informixData = new Hashtable();
			informixData.put("informix", returndata);
			monitorValue.put(dbnames, informixData);
//			 更新内存
			if (!(ShareData.getInformixmonitordata().containsKey(serverip))) {
				ShareData.setInfomixmonitordata(serverip, monitorValue);
			} else {
				Hashtable informixHash = (Hashtable) ShareData.getInformixmonitordata().get(serverip);
				((Hashtable)((Hashtable)informixHash.get(dbnames)).get("informix")).put("configList", returndata.get("configList"));
			}
			// ----------------------------------保存到数据库及告警 start
			List configlist = (ArrayList)returndata.get("configList");
			if(configlist != null && configlist.size()>0){

				String hex = IpTranslation.formIpToHex(dbmonitorlist.getIpAddress());
				serverip = hex + ":" + dbnames;
				try {
					Calendar tempCal = Calendar.getInstance();
					Date cc = tempCal.getTime();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String montime = sdf.format(cc);

					String deletesql = "delete from nms_informixconfig where serverip='"+serverip+"'";
					GathersqlListManager.Addsql(deletesql);
					String insertsql = null;
					for(int i=0;i<configList.size();i++){
						Hashtable configs = (Hashtable)configList.get(i);
						if(configs != null && configs.size()>0){
							try {
								StringBuffer sBuffer = new StringBuffer();
								String cf_default = String.valueOf(configs.get("cf_default"));
								String cf_original = String.valueOf(configs.get("cf_original"));
								String cf_name = String.valueOf(configs.get("cf_name"));
								String cf_effective = String.valueOf(configs.get("cf_effective"));
								cf_default = cf_default.replaceAll("\\\\", "/");
								cf_original = cf_original.replaceAll("\\\\", "/");
								cf_name = cf_name.replaceAll("\\\\", "/");
								cf_effective = cf_effective.replaceAll("\\\\", "/");
								sBuffer
										.append("insert into nms_informixconfig(serverip, cf_default, cf_original,cf_name,cf_effective ,mon_time)");
								sBuffer.append(" values('");
								sBuffer.append(serverip);
								sBuffer.append("','");
								sBuffer.append(cf_default);
								sBuffer.append("','");
								sBuffer.append(cf_original);
								sBuffer.append("','");
								sBuffer.append(cf_name);
								sBuffer.append("','");
								sBuffer.append(cf_effective);
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
