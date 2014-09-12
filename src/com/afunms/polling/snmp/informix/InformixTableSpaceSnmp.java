package com.afunms.polling.snmp.informix;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.dao.InformixspaceconfigDao;
import com.afunms.application.model.DBVo;
import com.afunms.application.model.Informixspaceconfig;
import com.afunms.application.util.IpTranslation;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.EncryptUtil;
import com.afunms.common.util.InformixJdbcUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SystemConstant;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.gatherdb.GathersqlListManager;

/**
 * Oracle 表空间信息 采集 使用JDBC采集
 * 
 * @author wupinlong 2012/09/17
 * 
 */
public class InformixTableSpaceSnmp extends SnmpMonitor {

	public InformixTableSpaceSnmp() {
	}

	@SuppressWarnings("unchecked")
	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicators) {
		Hashtable returndata = new Hashtable();
		Hashtable monitorValue = new Hashtable();
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
			String username = dbmonitorlist.getUser();
			int port = Integer.parseInt(dbmonitorlist.getPort());
			String dbnames = dbmonitorlist.getDbName();
			String dbservername = dbmonitorlist.getAlias();//临时的服务名称
			ArrayList spaceList = new ArrayList();// 空间信息
			InformixJdbcUtil util = null;
			try {
				String passwords = EncryptUtil.decode(dbmonitorlist.getPassword());
				String dburl = "jdbc:informix-sqli://" + serverip + ":" + port + "/"
				+ "sysmaster" + ":INFORMIXSERVER=" + dbservername + "; user="
				+ username + ";password=" + passwords; // myDB为数据库名
				util = new InformixJdbcUtil(dburl, username, passwords);
				util.jdbc();
				// *********************************取数据 start
				ResultSet rs = null;
				try {
					// 获取表空间信息
					String sqlTablespace = "select d.name[1,8] dbspace,d.owner,c.fname,sum(c.chksize) Pages_size,sum(c.chksize) - sum(c.nfree) Pages_used,sum(c.nfree) Pages_free,round ((sum(c.nfree)) / (sum(c.chksize)) * 100, 2) percent_free from      sysdbspaces d, syschunks c where d.dbsnum = c.dbsnum group by 1,2,3 order by 1";
					Vector returnVal2 = new Vector();
					Map<String, Integer> names = new HashMap<String, Integer>();
					rs = util.stmt.executeQuery(sqlTablespace);
					while (rs.next()) {
						Hashtable informixspaces = new Hashtable();
						String name = rs.getString("dbspace").trim();
						// if (names.get(name) == null) {
						informixspaces.put("dbspace", name);// 空间名
						informixspaces.put("owner", rs.getString("owner")
								.trim());// 空间的所有者
						String fname = rs.getString("fname").trim();
						// informixspaces.put("fname", );//该块文件的路径
						String page_size = rs.getString("pages_size").trim();
						float p_size = 0;
						if (page_size != null && !page_size.equals(""))
							p_size = Float.parseFloat(page_size);
						informixspaces.put("pages_size", String
								.valueOf(p_size / 1024));// 空间大小
						String page_used = rs.getString("pages_used").trim();
						float p_used = 0;
						if (page_used != null && !"".equals(page_used))
							p_used = Float.parseFloat(page_used);
						informixspaces.put("pages_used", String
								.valueOf(p_used / 1024));// 已使用空间
						String page_free = rs.getString("pages_free").trim();
						float p_free = 0;
						if (page_free != null && !"".equals(page_free))
							p_free = Float.parseFloat(page_free);
						informixspaces.put("pages_free", String
								.valueOf(p_free / 1024));// 空闲空间

						String percent_free = rs.getString("percent_free")
								.trim();// 空闲百分比
						informixspaces.put("percent_free", percent_free);// 已使用空间百分比
						int len = 0;
						if (fname.lastIndexOf("/") != -1) {
							len = fname.lastIndexOf("/");
						} else {
							len = fname.length();
						}
						informixspaces.put("fname", fname);
						if (len != -1) {
							String tpath = fname.substring(0, len);
							informixspaces.put("file_name", tpath);
						}
						spaceList.add(informixspaces);
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
			returndata.put("informixspaces", spaceList);// 空间信息
			Hashtable informixData = new Hashtable();
			informixData.put("informix", returndata);
			monitorValue.put(dbnames, informixData);
//			 更新内存
			if (!(ShareData.getInformixmonitordata().containsKey(serverip))) {
				ShareData.setInfomixmonitordata(serverip, monitorValue);
			} else {
				Hashtable informixHash = (Hashtable) ShareData.getInformixmonitordata().get(serverip);
				((Hashtable)((Hashtable)informixHash.get(dbnames)).get("informix")).put("informixspaces", returndata.get("informixspaces"));
			}
			
			// ----------------------------------保存到数据库及告警 start
			spaceList = (ArrayList)returndata.get("informixspaces");
			if (spaceList != null && spaceList.size()>0) {
				String hex = IpTranslation.formIpToHex(dbmonitorlist.getIpAddress());
				serverip = hex + ":" + dbnames;

				try {
					Calendar tempCal = Calendar.getInstance();
					Date cc = tempCal.getTime();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String montime = sdf.format(cc);

					String deletesql = "delete from nms_informixspace where serverip='" + serverip + "'";
					GathersqlListManager.Addsql(deletesql);

					String insertsql = null;
					for(int i=0;i<spaceList.size();i++){
						Hashtable space = (Hashtable)spaceList.get(i);
						if(space != null && space.size()>0){
							DBManager dbmanager = new DBManager();
							try {
								StringBuffer sBuffer = new StringBuffer();
								sBuffer.append("insert into nms_informixspace(serverip, owner,pages_free,dbspace,pages_size,pages_used,file_name,fname,percent_free,mon_time)");
								sBuffer.append(" values('");
								sBuffer.append(serverip);
								sBuffer.append("','");
								sBuffer.append(String.valueOf(space.get("owner")));
								sBuffer.append("','");
								sBuffer.append(String.valueOf(space.get("pages_free")));
								sBuffer.append("','");
								sBuffer.append(String.valueOf(space.get("dbspace")));
								sBuffer.append("','");
								sBuffer.append(String.valueOf(space.get("pages_size")));
								sBuffer.append("','");
								sBuffer.append(String.valueOf(space.get("pages_used")));
								sBuffer.append("','");
								sBuffer.append(String.valueOf(space.get("file_name")).replaceAll(
										"\\\\", "/"));
								sBuffer.append("','");
								sBuffer.append(String.valueOf(space.get("fname")).replaceAll(
										"\\\\", "/"));
								sBuffer.append("','");
								sBuffer.append(String.valueOf(space.get("percent_free")));
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
								// System.out.println(sBuffer.toString());
								insertsql = sBuffer.toString();
								GathersqlListManager.Addsql(insertsql);
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								dbmanager.close();
							}
						}
					}
					// *****************tablespace告警 start
					try {
						NodeUtil nodeUtil = new NodeUtil();
						NodeDTO nodeDTO = nodeUtil
								.conversionToNodeDTO(dbmonitorlist);
						// 判断是否存在此告警指标
						AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
						List list = alarmIndicatorsUtil
								.getAlarmInicatorsThresholdForNode(nodeDTO
										.getId()
										+ "", nodeDTO.getType(), nodeDTO
										.getSubtype());
						CheckEventUtil checkEventUtil = new CheckEventUtil();
//						System.out.println("informix告警开始-----list.size()===="+list.size());
						for (int i = 0; i < list.size(); i++) {
							AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode) list.get(i);
							if ("tablespace".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
//								System.out.println("informix告警开始-----spaceList.size()===="+spaceList.size());
								if (spaceList != null && spaceList.size() > 0) {
									//替换原来的表空间数据		
									//SysLogger.info("add infromix space size====="+spaceList.size());
									ShareData.setInformixspacedata(serverip, spaceList);
									InformixspaceconfigDao informixspaceconfigdao = new InformixspaceconfigDao();
									Hashtable informixspaces = new Hashtable();
									try {
										informixspaces = informixspaceconfigdao.getByAlarmflag(1);
									} catch (Exception e) {
										e.printStackTrace();
									} finally {
										informixspaceconfigdao.close();
									}
									for (int k = 0; k < spaceList.size(); k++) {
										Hashtable ht = (Hashtable) spaceList.get(k);
										String tablespace = ht.get("dbspace").toString();
										String percent = ht.get("percent_free").toString();
//										System.out.println("informix告警开始-----"+dbmonitorlist.getIpAddress() + ":" + tablespace);
//										System.out.println("informix告警开始-----tablespace==="+informixspaces.containsKey(dbmonitorlist.getIpAddress() + ":" + tablespace));
										if (informixspaces.containsKey(dbmonitorlist.getIpAddress() + ":" + tablespace)) {
											//存在需要告警的表空间								
											Integer free = 0;
											try {
												free = new Float(percent).intValue();
											} catch (Exception e) {
												e.printStackTrace();
											}
											//依据表空间告警配置判断是否告警
											Informixspaceconfig informixspaceconfig = (Informixspaceconfig) informixspaces.get(dbmonitorlist.getIpAddress() + ":" + tablespace);
//											 依据表空间告警配置判断是否告警
											alarmIndicatorsNode.setLimenvalue0(informixspaceconfig.getAlarmvalue() + "");
											alarmIndicatorsNode.setLimenvalue1(informixspaceconfig.getAlarmvalue() + "");
											alarmIndicatorsNode.setLimenvalue2(informixspaceconfig.getAlarmvalue() + "");
											checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, (100 - free)+"",tablespace);
										}
									}
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					// *****************tablespace告警 end
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// ----------------------------------保存到数据库及告警 end

		}
		return returndata;
	}

}
