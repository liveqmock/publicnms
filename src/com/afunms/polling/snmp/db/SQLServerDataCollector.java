package com.afunms.polling.snmp.db;

import java.awt.Label;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import montnets.SmsDao;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmConstant;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.alarm.util.AlarmResourceCenter;
import com.afunms.application.dao.DBDao;
import com.afunms.application.dao.DBTypeDao;
import com.afunms.application.dao.SqldbconfigDao;
import com.afunms.application.model.DBTypeVo;
import com.afunms.application.model.DBVo;
import com.afunms.application.model.Sqldbconfig;
import com.afunms.application.model.Sqlserver_processdata;
import com.afunms.application.util.IpTranslation;
import com.afunms.common.util.EncryptUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SystemConstant;
import com.afunms.event.dao.AlarmInfoDao;
import com.afunms.event.dao.EventListDao;
import com.afunms.event.dao.SmscontentDao;
import com.afunms.event.model.AlarmInfo;
import com.afunms.event.model.EventList;
import com.afunms.event.model.Smscontent;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.DBNode;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.Pingcollectdata;
import com.afunms.polling.snmp.LoadSQLServerFile;
import com.afunms.system.util.TimeGratherConfigUtil;

public class SQLServerDataCollector {

	private Hashtable sendeddata = ShareData.getSendeddata();

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

	public SQLServerDataCollector() {

	}
	public void collect_data(String dbid,Hashtable gatherHash) {
		DBDao dbdao = null;
		try {

			dbdao = new DBDao();
			DBVo dbmonitorlist = null; 
			try{
				dbmonitorlist = (DBVo)dbdao.findByID(dbid);
			}catch(Exception e){
				
			}finally{
				dbdao.close();
			}
			if(dbmonitorlist == null)return;
			if(dbmonitorlist.getManaged() == 0)return;
			
			Hashtable sqlserverdata = new Hashtable();
			DBNode dbnode = (DBNode) PollingEngine.getInstance().getDbByID(dbmonitorlist.getId());
			
			// 初始化数据库节点状态
			dbnode.setAlarm(false);
			dbnode.setStatus(0);
			Calendar _tempCal = Calendar.getInstance();
			Date _cc = _tempCal.getTime();
			String _time = sdf.format(_cc);
			dbnode.setLastTime(_time);
			dbnode.getAlarmMessage().clear();
					
			//判断设备是否在采集时间段内 0:不在采集时间段内,则退出;1:在时间段内,进行采集;2:不存在采集时间段设置,则全天采集
			TimeGratherConfigUtil timeconfig = new TimeGratherConfigUtil();
			int result = 0;
			result = timeconfig.isBetween(dbnode.getId()+"", "db");
			if(result == 0){
				SysLogger.info("###### "+dbnode.getIpAddress()+" 不在采集时间段内,跳过######");
				return;
			}
			int id=dbnode.getId();
			String serverip = dbnode.getIpAddress();
			String username = dbnode.getUser();
			String passwords = EncryptUtil.decode(dbnode.getPassword());
			
			IpTranslation tranfer = new IpTranslation();
			String hex = tranfer.formIpToHex(serverip);
			Calendar date = Calendar.getInstance();
			Date d = new Date();
			// 判断该数据库是否能连接上
			boolean sqlserverIsOK = false;					
					if (dbnode.getCollecttype() == SystemConstant.DBCOLLECTTYPE_SHELL) {
						// 脚本采集方式
						String filename = ResourceCenter.getInstance().getSysPath() + "/linuxserver/" + serverip
								+ ".sqlserver.log";
						File file = new File(filename);
						if (!file.exists()) {
							// 文件不存在,则产生告警
							try {
								createFileNotExistSMS(serverip);
							} catch (Exception e) {
								e.printStackTrace();
							}
							return;
						}
						SysLogger.info("###开始解析SQLSERVER:" + serverip + "数据文件###");
						LoadSQLServerFile loadsqlserver = new LoadSQLServerFile(filename);

						try {
							sqlserverdata = loadsqlserver.getSQLInital();
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (sqlserverdata != null && sqlserverdata.size() > 0) {
							// 判断数据库连接状态
							if (sqlserverdata.containsKey("status")) {
								int status = Integer.parseInt((String) sqlserverdata.get("status"));
								if (status == 1)
									sqlserverIsOK = true;
								if (!sqlserverIsOK) {
									// 需要增加数据库所在的服务器是否能连通
									Host host = (Host) PollingEngine.getInstance().getNodeByIP(serverip);
									Vector ipPingData = (Vector) ShareData.getPingdata().get(serverip);
									if (ipPingData != null) {
										Pingcollectdata pingdata = (Pingcollectdata) ipPingData.get(0);
										Calendar tempCal = (Calendar) pingdata.getCollecttime();
										Date cc = tempCal.getTime();
										String time = sdf.format(cc);// .format(cc);
										String lastTime = time;
										String pingvalue = pingdata.getThevalue();
										if (pingvalue == null || pingvalue.trim().length() == 0)
											pingvalue = "0";
										double pvalue = new Double(pingvalue);
										if (pvalue == 0) {
											// 主机服务器连接不上***********************************************
											dbnode = (DBNode) PollingEngine.getInstance().getDbByID(dbmonitorlist.getId());
											dbnode.setAlarm(true);
											dbnode.setLastTime(lastTime);
											dbnode.setStatus(3);
											List alarmList = dbnode.getAlarmMessage();
											if (alarmList == null)
												alarmList = new ArrayList();
											dbnode.getAlarmMessage().add("数据库服务停止");
											String sysLocation = "";
											try {
												SmscontentDao eventdao = new SmscontentDao();
												String eventdesc = "SQLSERVER(" + dbmonitorlist.getDbName() + " IP:"
														+ dbmonitorlist.getIpAddress() + ")" + "的数据库服务停止";
												eventdao.createEventWithReasion("poll", dbmonitorlist.getId() + "", dbmonitorlist
														.getAlias()
														+ "(" + dbmonitorlist.getIpAddress() + ")", eventdesc, 3, "db", "ping",
														"所在的服务器连接不上");
											} catch (Exception e) {
												e.printStackTrace();
											}
										} else {
											Pingcollectdata hostdata = null;
											hostdata = new Pingcollectdata();
											hostdata.setId(Long.parseLong(id+""));
											hostdata.setIpaddress(serverip);
											hostdata.setCollecttime(date);
											hostdata.setCategory("SQLPing");
											hostdata.setEntity("Utilization");
											hostdata.setSubentity("ConnectUtilization");
											hostdata.setRestype("dynamic");
											hostdata.setUnit("%");
											hostdata.setThevalue("0");
											try {
												dbdao.createHostData(hostdata);
												// 发送短信
												dbnode = (DBNode) PollingEngine.getInstance().getDbByID(dbmonitorlist.getId());
												dbnode.setAlarm(true);
												List alarmList = dbnode.getAlarmMessage();
												if (alarmList == null)
													alarmList = new ArrayList();
												dbnode.getAlarmMessage().add("数据库服务停止");
												dbnode.setStatus(3);
												createSMS("sqlserver", dbmonitorlist);
											} catch (Exception e) {
												e.printStackTrace();
											}
										}

									} else {
										Pingcollectdata hostdata = null;
										hostdata = new Pingcollectdata();
										hostdata.setId(Long.parseLong(id+""));
										hostdata.setIpaddress(serverip);
										hostdata.setCollecttime(date);
										hostdata.setCategory("SQLPing");
										hostdata.setEntity("Utilization");
										hostdata.setSubentity("ConnectUtilization");
										hostdata.setRestype("dynamic");
										hostdata.setUnit("%");
										hostdata.setThevalue("0");
										try {
											dbdao.createHostData(hostdata);
											// 发送短信
											dbnode = (DBNode) PollingEngine.getInstance().getDbByID(dbmonitorlist.getId());
											dbnode.setAlarm(true);
											List alarmList = dbnode.getAlarmMessage();
											if (alarmList == null)
												alarmList = new ArrayList();
											dbnode.getAlarmMessage().add("数据库服务停止");
											dbnode.setStatus(3);
											createSMS("sqlserver", dbmonitorlist);
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								} else {
									// 连通的情况下,将连通率数据插入表里
									Pingcollectdata hostdata = null;
									hostdata = new Pingcollectdata();
									hostdata.setId(Long.parseLong(id+""));
									hostdata.setIpaddress(serverip);
									hostdata.setCollecttime(date);
									hostdata.setCategory("SQLPing");
									hostdata.setEntity("Utilization");
									hostdata.setSubentity("ConnectUtilization");
									hostdata.setRestype("dynamic");
									hostdata.setUnit("%");
									hostdata.setThevalue("100");
									try {
										dbdao.createHostData(hostdata);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
								if (sqlserverIsOK) {
									// 若数据库能连接上，则进行数据库数据的采集
									Vector info_v = new Vector();
									Hashtable sysValue = new Hashtable();
									Vector altfiles_v = new Vector();
									Vector process_v = new Vector();
									Vector sysuser_v = new Vector();
									Vector lockinfo_v = new Vector();
									Hashtable sqlservervalue = new Hashtable();
									try {
										if (sqlserverdata.containsKey("info_v")) {
											info_v = (Vector) sqlserverdata.get("info_v");
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
									try {
										if (sqlserverdata.containsKey("sysValue")) {
											sysValue = (Hashtable) sqlserverdata.get("sysValue");
										}
									} catch (Exception e) {
										e.printStackTrace();
									}

									if (info_v == null)
										info_v = new Vector();

									for (int j = 0; j < info_v.size(); j++) {
										Sqlserver_processdata sp = new Sqlserver_processdata();
										Hashtable ht = (Hashtable) info_v.get(j);
										String spid = ht.get("spid").toString();
										String dbname = ht.get("dbname").toString();
										String usernames = ht.get("username").toString();
										String cpu = ht.get("cpu").toString();
										String memusage = ht.get("memusage").toString();
										String physical_io = ht.get("physical_io").toString();

										String p_status = ht.get("status").toString();
										String hostname = ht.get("hostname").toString();
										String program_name = ht.get("program_name").toString();
										String login_time = ht.get("login_time").toString();
										sp.setCpu(Integer.parseInt(cpu));
										sp.setDbname(dbname);
										sp.setHostname(hostname);
										sp.setMemusage(Integer.parseInt(memusage));
										sp.setMon_time(d);
										sp.setPhysical_io(Long.parseLong(physical_io));
										sp.setProgram_name(program_name);
										sp.setSpid(spid);
										sp.setStatus(p_status);
										sp.setUsername(usernames);
										sp.setLogin_time(sdf.parse(login_time));
										sp.setServerip(serverip);
										try {
											dbdao.addSqlserver_processdata(sp);
										} catch (Exception e) {
											e.printStackTrace();
										}
									}

									// Hashtable retValue = new Hashtable();
									// retValue =
									// dbdao.collectSQLServerMonitItemsDetail(serverip,
									// "", username, passwords);
									// sqlserverdata.put("retValue", retValue);

									Hashtable dbValue = new Hashtable();
									// 得到数据库表的信息
									try {
										if (sqlserverdata.containsKey("dbValue")) {
											dbValue = (Hashtable) sqlserverdata.get("dbValue");
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
									// dbValue = dbdao.getSqlserverDB(serverip,
									// username, passwords);
									// ShareData.setSqldbdata(serverip,
									// dbValue);

									// 对数据库空间进行告警检查
									if (dbValue != null && dbValue.size() > 0) {
										SqldbconfigDao sqldbconfigManager = new SqldbconfigDao();
										Hashtable alarmdbs = sqldbconfigManager.getByAlarmflag(1);
										sqldbconfigManager.close();
										Hashtable database = (Hashtable) dbValue.get("database");
										// SysLogger.info("database
										// size===="+database.size());
										Hashtable logfile = (Hashtable) dbValue.get("logfile");
										Vector names = (Vector) dbValue.get("names");
										if (alarmdbs == null)
											alarmdbs = new Hashtable();
										if (database == null)
											database = new Hashtable();
										if (logfile == null)
											logfile = new Hashtable();
										if (names != null && names.size() > 0) {
											for (int k = 0; k < names.size(); k++) {
												String dbname = (String) names.get(k);
												if (database.get(dbname) != null) {
													Hashtable db = (Hashtable) database.get(dbname);
													String usedperc = (String) db.get("usedperc");
													if (alarmdbs.containsKey(serverip + ":" + dbname + ":0")) {
														Sqldbconfig sqldbconfig = (Sqldbconfig) alarmdbs.get(serverip + ":"
																+ dbname + ":0");
														if (sqldbconfig == null)
															continue;
														if (usedperc == null)
															continue;
														if (sqldbconfig.getAlarmvalue() < new Integer(usedperc)) {
															// 告警
															SysLogger.info("### 开始告警 ###");
															dbnode = (DBNode) PollingEngine.getInstance().getDbByID(
																	dbmonitorlist.getId());
															dbnode.setAlarm(true);
															dbnode.setStatus(3);
															List alarmList = dbnode.getAlarmMessage();
															if (alarmList == null)
																alarmList = new ArrayList();
															dbnode.getAlarmMessage().add(sqldbconfig.getDbname() + "表空间超过阀值"+ sqldbconfig.getAlarmvalue());
															createSqldbSMS(dbmonitorlist, sqldbconfig);
														}
													}
												}
												if (logfile.get(dbname) != null) {
													Hashtable db = (Hashtable) logfile.get(dbname);
													String usedperc = (String) db.get("usedperc");
													if (alarmdbs.containsKey(serverip + ":" + dbname + ":0")) {
														Sqldbconfig sqldbconfig = (Sqldbconfig) alarmdbs.get(serverip + ":"
																+ dbname + ":1");
														if (sqldbconfig == null)
															continue;
														if (sqldbconfig.getAlarmvalue() < new Integer(usedperc)) {
															// 告警
															SysLogger.info("$$$ 开始告警 $$$");
															dbnode = (DBNode) PollingEngine.getInstance().getDbByID(
																	dbmonitorlist.getId());
															dbnode.setAlarm(true);
															dbnode.setStatus(3);
															List alarmList = dbnode.getAlarmMessage();
															if (alarmList == null)
																alarmList = new ArrayList();
															dbnode.getAlarmMessage().add(sqldbconfig.getDbname() + "表空间超过阀值"+ sqldbconfig.getAlarmvalue());
															createSqldbSMS(dbmonitorlist, sqldbconfig);
														}
													}
												}

											}
										}
									}
									ShareData.setSqlserverdata(serverip, sqlserverdata);
								}
							}
						}
					} else {
						// JDBC采集方式
						try {
							sqlserverIsOK = dbdao.getSqlserverIsOk(serverip, username, passwords);
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							// dbdao.close();
						}
						if (gatherHash.containsKey("ping")) {
							if (!sqlserverIsOK) {
								sqlserverdata.put("status", "0");
								// 需要增加数据库所在的服务器是否能连通
								Host host = (Host) PollingEngine.getInstance().getNodeByIP(serverip);
								Vector ipPingData = (Vector) ShareData.getPingdata().get(serverip);
								
								if (ipPingData != null) {
									Pingcollectdata pingdata = (Pingcollectdata) ipPingData.get(0);
									Calendar tempCal = (Calendar) pingdata.getCollecttime();
									Date cc = tempCal.getTime();
									String time = sdf.format(cc);// .format(cc);
									String lastTime = time;
									String pingvalue = pingdata.getThevalue();
									if (pingvalue == null || pingvalue.trim().length() == 0)
										pingvalue = "0";
									double pvalue = new Double(pingvalue);
									if (pvalue == 0) {
										// 主机服务器连接不上***********************************************
										dbnode = (DBNode) PollingEngine.getInstance().getDbByID(dbmonitorlist.getId());
										dbnode.setAlarm(true);
										dbnode.setLastTime(lastTime);
										dbnode.setStatus(3);
										List alarmList = dbnode.getAlarmMessage();
										if (alarmList == null)
											alarmList = new ArrayList();
										dbnode.getAlarmMessage().add("数据库服务停止");
										String sysLocation = "";
										try {
											SmscontentDao eventdao = new SmscontentDao();
											String eventdesc = "SQLSERVER(" + dbmonitorlist.getDbName() + " IP:"+ dbmonitorlist.getIpAddress() + ")" + "的数据库服务停止";
											eventdao.createEventWithReasion("poll", dbmonitorlist.getId() + "", dbmonitorlist
													.getAlias()
													+ "(" + dbmonitorlist.getIpAddress() + ")", eventdesc, 3, "db", "ping",
													"所在的服务器连接不上");
										} catch (Exception e) {
											e.printStackTrace();
										}
									} else {
										Pingcollectdata hostdata = null;
										hostdata = new Pingcollectdata();
										hostdata.setId(Long.parseLong(id+""));
										hostdata.setIpaddress(serverip);
										hostdata.setCollecttime(date);
										hostdata.setCategory("SQLPing");
										hostdata.setEntity("Utilization");
										hostdata.setSubentity("ConnectUtilization");
										hostdata.setRestype("dynamic");
										hostdata.setUnit("%");
										hostdata.setThevalue("0");
										try {
											dbdao.createHostData(hostdata);
											// 发送短信
											dbnode = (DBNode) PollingEngine.getInstance().getDbByID(dbmonitorlist.getId());
											dbnode.setAlarm(true);
											List alarmList = dbnode.getAlarmMessage();
											if (alarmList == null)
												alarmList = new ArrayList();
											dbnode.getAlarmMessage().add("数据库服务停止");
											dbnode.setStatus(3);
											createSMS("sqlserver", dbmonitorlist);
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
	
								} else {
									Pingcollectdata hostdata = null;
									hostdata = new Pingcollectdata();
									hostdata.setId(Long.parseLong(id+""));
									hostdata.setIpaddress(serverip);
									hostdata.setCollecttime(date);
									hostdata.setCategory("SQLPing");
									hostdata.setEntity("Utilization");
									hostdata.setSubentity("ConnectUtilization");
									hostdata.setRestype("dynamic");
									hostdata.setUnit("%");
									hostdata.setThevalue("0");
									try {
										dbdao.createHostData(hostdata);
										// 发送短信
										dbnode = (DBNode) PollingEngine.getInstance().getDbByID(dbmonitorlist.getId());
										dbnode.setAlarm(true);
										List alarmList = dbnode.getAlarmMessage();
										if (alarmList == null)
											alarmList = new ArrayList();
										dbnode.getAlarmMessage().add("数据库服务停止");
										dbnode.setStatus(3);
										createSMS("sqlserver", dbmonitorlist);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
								
								
								//将之前存储的指定server的sqlserver临时数据表都清空
	//							this.clearSqlserverNmsTableData(dbdao,serverip);
							} else {
								// 连通的情况下,将连通率数据插入表里
								Pingcollectdata hostdata = null;
								hostdata = new Pingcollectdata();
								hostdata.setId(Long.parseLong(id+""));
								hostdata.setIpaddress(serverip);
								hostdata.setCollecttime(date);
								hostdata.setCategory("SQLPing");
								hostdata.setEntity("Utilization");
								hostdata.setSubentity("ConnectUtilization");
								hostdata.setRestype("dynamic");
								hostdata.setUnit("%");
								hostdata.setThevalue("100");
								try {
									dbdao.createHostData(hostdata);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}// end if (gatherHash.containsKey("ping"))

						if (sqlserverIsOK) {
							// 若数据库能连接上，则进行数据库数据的采集
							
							sqlserverdata.put("status", "1");//nms_sqlserverstatus
							
							Vector info_v = new Vector();
							Hashtable sysValue = new Hashtable();
							// Vector altfiles_v = new Vector();
							Vector process_v = new Vector();
							Vector sysuser_v = new Vector();
							Vector lockinfo_v = new Vector();
							
							
							Hashtable retValue = new Hashtable();
							retValue = dbdao.collectSQLServerMonitItemsDetail(serverip, "", username, passwords,gatherHash);
							/*
							 * nms_sqlserverpages 、nms_sqlserverconns 、nms_sqlserverlocks、nms_sqlservercaches、
							 * nms_sqlservermems、nms_sqlserversqls、nms_sqlserverscans、nms_sqlserverstatisticshash
							 */
							sqlserverdata.put("retValue", retValue);
							Hashtable sqlserverDataHash = new Hashtable();
							//获取SQLSERVER数据信息
							try{
								sqlserverDataHash = dbdao.getSqlServerData(serverip, username, passwords,gatherHash);
							}catch(Exception e){
								e.printStackTrace();
							}
							if (sqlserverDataHash == null)
								sqlserverDataHash = new Hashtable();
							
							//库信息
							Hashtable dbValue = new Hashtable();
							if(sqlserverDataHash.get("dbValue") != null){
								dbValue = (Hashtable)sqlserverDataHash.get("dbValue");
								sqlserverdata.put("dbValue", (Hashtable)sqlserverDataHash.get("dbValue"));//nms_sqlserverdbvalue
								ShareData.setSqldbdata(serverip, (Hashtable)sqlserverDataHash.get("dbValue"));
							}
							//系统信息
							if(sqlserverDataHash.get("sysValue") != null){
								sqlserverdata.put("sysValue", (Hashtable)sqlserverDataHash.get("sysValue"));//nms_sqlserversysvalue
							}
							//锁信息
							if(sqlserverDataHash.get("lockinfo_v") != null){
								sqlserverdata.put("lockinfo_v", (Vector)sqlserverDataHash.get("lockinfo_v"));//nms_sqlserverlockinfo_v
							}
							//进程信息
							if(sqlserverDataHash.get("info_v") != null){
								sqlserverdata.put("info_v", (Vector)sqlserverDataHash.get("info_v"));//nms_sqlserverinfo_v
							}					

							for (int j = 0; j < info_v.size(); j++) {
								Sqlserver_processdata sp = new Sqlserver_processdata();
								Hashtable ht = (Hashtable) info_v.get(j);
								String spid = ht.get("spid").toString();
								String dbname = ht.get("dbname").toString();
								String usernames = ht.get("username").toString();
								String cpu = ht.get("cpu").toString();
								String memusage = ht.get("memusage").toString();
								String physical_io = ht.get("physical_io").toString();

								String status = ht.get("status").toString();
								String hostname = ht.get("hostname").toString();
								String program_name = ht.get("program_name").toString();
								String login_time = ht.get("login_time").toString();

								sp.setCpu(Integer.parseInt(cpu));
								sp.setDbname(dbname);
								sp.setHostname(hostname);
								sp.setMemusage(Integer.parseInt(memusage));
								sp.setMon_time(d);
								sp.setPhysical_io(Long.parseLong(physical_io));
								sp.setProgram_name(program_name);
								sp.setSpid(spid);
								sp.setStatus(status);
								sp.setUsername(usernames);
								sp.setLogin_time(sdf.parse(login_time));
								sp.setServerip(serverip);
								try {
									dbdao.addSqlserver_processdata(sp);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
							
							// 对数据库空间进行告警检查
							if (dbValue != null && dbValue.size() > 0) {
								SqldbconfigDao sqldbconfigManager = new SqldbconfigDao();
								Hashtable alarmdbs = sqldbconfigManager.getByAlarmflag(1);
								sqldbconfigManager.close();
								Hashtable database = (Hashtable) dbValue.get("database");
								Hashtable logfile = (Hashtable) dbValue.get("logfile");
								Vector names = (Vector) dbValue.get("names");
								if (alarmdbs == null)
									alarmdbs = new Hashtable();
								if (database == null)
									database = new Hashtable();
								if (logfile == null)
									logfile = new Hashtable();
								if (names != null && names.size() > 0) {
									for (int k = 0; k < names.size(); k++) {
										String dbname = (String) names.get(k);
										if (database.get(dbname) != null) {
											Hashtable db = (Hashtable) database.get(dbname);
											String usedperc = (String) db.get("usedperc");
											if (alarmdbs.containsKey(serverip + ":" + dbname + ":0")) {
												Sqldbconfig sqldbconfig = (Sqldbconfig) alarmdbs.get(serverip + ":" + dbname
														+ ":0");
												if (sqldbconfig == null)
													continue;
												if (usedperc == null)
													continue;
												if (sqldbconfig.getAlarmvalue() < new Integer(usedperc)) {
													// 告警
													SysLogger.info("### 开始告警 ###");
													dbnode = (DBNode) PollingEngine.getInstance()
															.getDbByID(dbmonitorlist.getId());
													dbnode.setAlarm(true);
													dbnode.setStatus(3);
													List alarmList = dbnode.getAlarmMessage();
													if (alarmList == null)
														alarmList = new ArrayList();
													dbnode.getAlarmMessage().add(
															sqldbconfig.getDbname() + "表空间超过阀值" + sqldbconfig.getAlarmvalue());
													createSqldbSMS(dbmonitorlist, sqldbconfig);
												}
											}
										}
										if (logfile.get(dbname) != null) {
											Hashtable db = (Hashtable) logfile.get(dbname);
											String usedperc = (String) db.get("usedperc");
											if (alarmdbs.containsKey(serverip + ":" + dbname + ":0")) {
												Sqldbconfig sqldbconfig = (Sqldbconfig) alarmdbs.get(serverip + ":" + dbname
														+ ":1");
												if (sqldbconfig == null)
													continue;
												if (sqldbconfig.getAlarmvalue() < new Integer(usedperc)) {
													// 告警
													SysLogger.info("$$$ 开始告警 $$$");
													dbnode = (DBNode) PollingEngine.getInstance()
															.getDbByID(dbmonitorlist.getId());
													dbnode.setAlarm(true);
													dbnode.setStatus(3);
													List alarmList = dbnode.getAlarmMessage();
													if (alarmList == null)
														alarmList = new ArrayList();
													dbnode.getAlarmMessage().add(
															sqldbconfig.getDbname() + "表空间超过阀值" + sqldbconfig.getAlarmvalue());
													createSqldbSMS(dbmonitorlist, sqldbconfig);
												}
											}
										}
									}
								}
							}
							ShareData.setSqlserverdata(serverip, sqlserverdata);
							//将采集到的数据插入到数据库
							this.saveSqlServerData(hex+":"+dbmonitorlist.getAlias(), sqlserverdata);
						}
					}
					// 若能连接上数据库，添加告警信息   HONGLI 		
					if(sqlserverIsOK){													
						SysLogger.info("#### 可以连接上sqlserver数据库"+serverip+"，添加指标告警信息 ####");
						updateData(dbmonitorlist,sqlserverdata);
					}
					String status = "0";
					if(sqlserverIsOK){
						status = "1";
					}else {
						dbdao.updateNmsValueByUniquekeyAndTablenameAndKey("nms_sqlserverstatus", "serverip", hex+":"+dbmonitorlist.getAlias(), "status", status);
					}
				//}
			//}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(dbdao!=null)
				dbdao.close();
			SysLogger.info("#### sqlserver task 运行完毕 ####");
		}
	}
	
	/**
	 * 更新告警信息    HONGLI
	 * @param vo 数据库实体
	 * @param collectingData 数据库实体中的各种数据信息
	 */
	public void updateData(Object vo , Object collectingData){
		if(collectingData == null || vo == null){
			return ;
		}
		DBVo sqlserver = (DBVo)vo;		
		SysLogger.info("##############开始检测SqlServer数据库:"+sqlserver.getIpAddress()+"告警指标等..###########");
		Hashtable datahashtable = (Hashtable)collectingData;
		
		Hashtable sqlserverHashtable = (Hashtable)datahashtable.get("retValue");//得到采集sqlserver数据库的信息
		
		Hashtable memeryHashtable = (Hashtable)sqlserverHashtable.get("pages");//得到缓存管理统计信息
		
		Hashtable locksHashtable = (Hashtable)sqlserverHashtable.get("locks");//得到锁明细信息
		
		Hashtable connsHashtable = (Hashtable)sqlserverHashtable.get("conns");//得到数据库页连接统计
		
		AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
		List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(sqlserver.getId()), AlarmConstant.TYPE_DB, "sqlserver");//获取采集指标列表
//		SysLogger.info("##############HONG list.size--"+list.size()+"###########");
		for(int i = 0 ; i < list.size() ; i ++){
			AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode)list.get(i);
//			SysLogger.info("##############HONG alarmIndicatorsNode.getEnabled--"+alarmIndicatorsNode.getEnabled()+"###########");
			if("1".equals(alarmIndicatorsNode.getEnabled())){
				String indicators = alarmIndicatorsNode.getName();
				String value = "";//value 是指实际数据库中的值，如 缓冲区命中率    HONGLI
//				SysLogger.info("##############HONG indicators--"+indicators+"##########");
				if("buffercache".equals(indicators)){
					value = (String)memeryHashtable.get("bufferCacheHitRatio");//key 和DBDao collectSQLServerMonitItemsDetail 方法中的pages的key一致
//					SysLogger.info("#######HONG memeryHashtable.get(bufferCacheHitRatio)-->  "+memeryHashtable.get("bufferCacheHitRatio")+"");
				}else if("plancache".equals(indicators)){
					value = (String)memeryHashtable.get("planCacheHitRatio");
				}else if("cursormanager".equals(indicators)){
					value = (String)memeryHashtable.get("cursorManagerByTypeHitRatio");
				}else if("catalogMetadata".equals(indicators)){
					value = (String)memeryHashtable.get("catalogMetadataHitRatio");
				}else if("deadLocks".equals(indicators)){
					value = (String)locksHashtable.get("deadLocks");//key 和DBDao collectSQLServerMonitItemsDetail 方法中的locks的key一致
				}else if("connections".equals(indicators)){
					value = (String)connsHashtable.get("connections");//key 和DBDao collectSQLServerMonitItemsDetail 方法中的conns的key一致
				}else {					
					continue;
				}
//				SysLogger.info("#######HONG indicator、value--"+indicators+"、"+value+"####");
				if(value == null)continue;
				if( AlarmConstant.DATATYPE_NUMBER.equals(alarmIndicatorsNode.getDatatype())){
					
					try {
						double value_int = Double.valueOf(value);//实际值  HONGLI
						double Limenvalue2 = Double.valueOf(alarmIndicatorsNode.getLimenvalue2());//阀值2 
						double Limenvalue1 = Double.valueOf(alarmIndicatorsNode.getLimenvalue1());//阀值1
						double Limenvalue0 = Double.valueOf(alarmIndicatorsNode.getLimenvalue0());//阀值0
						
//						SysLogger.info("#######HONG indicator、value_int--"+indicators+"、"+value_int+"####");
						
						String level = "";
						String alarmTimes = "";
						if(value_int > Limenvalue2){
							level = "3";
							alarmTimes = alarmIndicatorsNode.getTime2();
						}else if(value_int > Limenvalue1){
							level = "2";
							alarmTimes = alarmIndicatorsNode.getTime1();
						}else if(value_int > Limenvalue0){
							level = "1";
							alarmTimes = alarmIndicatorsNode.getTime0();
						}else{
							continue;
						}
						//?
						String num = (String)AlarmResourceCenter.getInstance().getAttribute(String.valueOf(alarmIndicatorsNode.getId()));
						
						if(num == null || "".equals(num)){
							num = "0";
						}
						
						int num_int = Integer.valueOf(num);
						
						
						int alarmTimes_int = Integer.valueOf(alarmTimes);
						
//						SysLogger.info("#######HONG indicators：num_int、level、alarmTimes--"+indicators+":"+num_int+"、"+level+"、"+alarmTimes+"##");
						
						if(num_int >= alarmTimes_int){//实际告警次数 >= 告警阀值   ，则提示报警信息 和 发送短信   HONGLI
							// 告警
							DBNode dbnode = (DBNode) PollingEngine.getInstance().getDbByID(sqlserver.getId());
							dbnode.setAlarm(true);
							List alarmList = dbnode.getAlarmMessage();
							if (alarmList == null)
								alarmList = new ArrayList();
							dbnode.getAlarmMessage().add(alarmIndicatorsNode.getAlarm_info() + " 当前值为123：" + value +  alarmIndicatorsNode.getThreshlod_unit());
							//若大于之前的告警级别,则用最大级别
							if(Integer.valueOf(level)> dbnode.getStatus())dbnode.setStatus(Integer.valueOf(level));
							SysLogger.info("##############updatedate0000000发送短信############");
							//HONGLI
							createSMS(alarmIndicatorsNode.getType(), alarmIndicatorsNode.getSubtype(), sqlserver.getAlias() , sqlserver.getId() + "", alarmIndicatorsNode.getAlarm_info() + " 当前值为：" + value +  alarmIndicatorsNode.getThreshlod_unit() , Integer.valueOf(level) , 1 , sqlserver.getAlias() , sqlserver.getBid(),sqlserver.getAlias() + "(" + sqlserver.getAlias() + ")");
						}else{
							num_int = num_int + 1;
							AlarmResourceCenter.getInstance().setAttribute(String.valueOf(alarmIndicatorsNode.getId()), String.valueOf(num_int));
						}
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
			
			
		}
	}
	
	/**
	 * @author HONG 给告警指标发送告警提示和告警短信
	 * @param subtype
	 * @param subentity
	 * @param ipaddress
	 * @param objid
	 * @param content
	 * @param flag
	 * @param checkday
	 * @param sIndex
	 * @param bids
	 * @param sysLocation
	 */
	public void createSMS(String subtype,String subentity,String ipaddress,String objid,String content,int flag,int checkday,String sIndex,String bids,String sysLocation){
	 	//建立短信		 	
	 	//从内存里获得当前这个IP的PING的值
	 	Calendar date=Calendar.getInstance();
	 	Hashtable sendeddata = ShareData.getSendeddata();
	 	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	 	//System.out.println("端口事件--------------------");
	 	try{
 			if (!sendeddata.containsKey(subtype+":"+subentity+":"+ipaddress+":"+sIndex)) {
 				//若不在，则建立短信，并且添加到发送列表里
	 			Smscontent smscontent = new Smscontent();
	 			String time = sdf.format(date.getTime());
	 			smscontent.setLevel(flag+"");
	 			smscontent.setObjid(objid);
	 			smscontent.setMessage(content);
	 			smscontent.setRecordtime(time);
	 			smscontent.setSubtype(subtype);
	 			smscontent.setSubentity(subentity);
	 			smscontent.setIp(ipaddress);
	 			//发送短信
	 			SmscontentDao smsmanager=new SmscontentDao();
	 			smsmanager.sendURLSmscontent(smscontent);	
				sendeddata.put(subtype+":"+subentity+":"+ipaddress+":"+sIndex,date);	
				
 			} else {
 				//若在，则从已发送短信列表里判断是否已经发送当天的短信
 				SmsDao smsDao = new SmsDao();
 				List list = new ArrayList();
 				String startTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " 00:00:00";
 				String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
 				try {
 					list = smsDao.findByEvent(content,startTime,endTime);
				} catch (RuntimeException e) {
					e.printStackTrace();
				} finally {
					smsDao.close();
				}
				if(list!=null&&list.size()>0){//短信列表里已经发送当天的短信
					Calendar formerdate =(Calendar)sendeddata.get(subtype+":"+subentity+":"+ipaddress+":"+sIndex);		 				
		 			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		 			Date last = null;
		 			Date current = null;
		 			Calendar sendcalen = formerdate;
		 			Date cc = sendcalen.getTime();
		 			String tempsenddate = formatter.format(cc);
		 			
		 			Calendar currentcalen = date;
		 			Date ccc = currentcalen.getTime();
		 			last = formatter.parse(tempsenddate);
		 			String currentsenddate = formatter.format(ccc);
		 			current = formatter.parse(currentsenddate);
		 			
		 			long subvalue = current.getTime()-last.getTime();	
		 			if(checkday == 1){
		 				//检查是否设置了当天发送限制,1为检查,0为不检查
		 				if (subvalue/(1000*60*60*24)>=1){
			 				//超过一天，则再发信息
				 			Smscontent smscontent = new Smscontent();
				 			String time = sdf.format(date.getTime());
				 			smscontent.setLevel(flag+"");
				 			smscontent.setObjid(objid);
				 			smscontent.setMessage(content);
				 			smscontent.setRecordtime(time);
				 			smscontent.setSubtype(subtype);
				 			smscontent.setSubentity(subentity);
				 			smscontent.setIp(ipaddress);//发送短信
				 			SmscontentDao smsmanager=new SmscontentDao();
				 			smsmanager.sendURLSmscontent(smscontent);
							//修改已经发送的短信记录	
							sendeddata.put(subtype+":"+subentity+":"+ipaddress+":"+sIndex,date);
				 		} else {
	                        //开始写事件
			 	            //String sysLocation = "";
			 				createEvent("poll",sysLocation,bids,content,flag,subtype,subentity,ipaddress,objid);
				 		}
		 			}
				} else {
 					Smscontent smscontent = new Smscontent();
 		 			String time = sdf.format(date.getTime());
 		 			smscontent.setLevel(flag+"");
 		 			smscontent.setObjid(objid);
 		 			smscontent.setMessage(content);
 		 			smscontent.setRecordtime(time);
 		 			smscontent.setSubtype(subtype);
 		 			smscontent.setSubentity(subentity);
 		 			smscontent.setIp(ipaddress);
 		 			//发送短信
 		 			SmscontentDao smsmanager=new SmscontentDao();
 		 			smsmanager.sendURLSmscontent(smscontent);	
 					sendeddata.put(subtype+":"+subentity+":"+ipaddress+":"+sIndex,date);
 				}
 				
 			}	 			 			 			 			 	
	 	}catch(Exception e){
	 		e.printStackTrace();
	 	}
	 }
	

	/**
	 * @author HONGLI 生成告警事件
	 * @param eventtype
	 * @param eventlocation
	 * @param bid
	 * @param content
	 * @param level1
	 * @param subtype
	 * @param subentity
	 * @param ipaddress
	 * @param objid
	 */
	private void createEvent(String eventtype,String eventlocation,String bid,String content,int level1,String subtype,String subentity,String ipaddress,String objid){
		//生成事件
		SysLogger.info("##############开始生成事件############");
		EventList eventlist = new EventList();
		eventlist.setEventtype(eventtype);
		eventlist.setEventlocation(eventlocation);
		eventlist.setContent(content);
		eventlist.setLevel1(level1);
		eventlist.setManagesign(0);
		eventlist.setBak("");
		eventlist.setRecordtime(Calendar.getInstance());
		eventlist.setReportman("系统轮询");
		eventlist.setBusinessid(bid);
		eventlist.setNodeid(Integer.parseInt(objid));
		eventlist.setOid(0);
		eventlist.setSubtype(subtype);
		eventlist.setSubentity(subentity);
		EventListDao eventlistdao = new EventListDao();
		try{
			eventlistdao.save(eventlist);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			eventlistdao.close();
		}
	}

	public static void createSqldbSMS(DBVo dbmonitorlist, Sqldbconfig sqldbconfig) {
		// 建立短信
		// 从内存里获得当前这个IP的PING的值
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SmscontentDao smsmanager = new SmscontentDao();
		AlarmInfoDao alarminfomanager = new AlarmInfoDao();

		String ipaddress = dbmonitorlist.getIpAddress();
		Hashtable sendeddata = ShareData.getSendeddata();
		Calendar date = Calendar.getInstance();
		String time = sdf.format(date.getTime());
		String errorcontent = "";
		if (sqldbconfig.getLogflag() == 0) {
			// 库文件
			errorcontent = dbmonitorlist.getIpAddress() + "的" + dbmonitorlist.getDbName() + "的" + sqldbconfig.getDbname()
					+ "的库空间超过" + sqldbconfig.getAlarmvalue() + "%的阀值";
		} else {
			// 日志文件
			errorcontent = dbmonitorlist.getIpAddress() + "的" + dbmonitorlist.getDbName() + "的" + sqldbconfig.getDbname()
					+ "的日志超过" + sqldbconfig.getAlarmvalue() + "%的阀值";
		}

		try {
			if (!sendeddata.containsKey(ipaddress + ":" + sqldbconfig.getDbname() + ":" + sqldbconfig.getLogflag())) {
				// 若不在，则建立短信，并且添加到发送列表里
				Smscontent smscontent = new Smscontent();
				// String time1 = sdf.format(date.getTime());
				smscontent.setLevel("2");
				smscontent.setObjid(dbmonitorlist.getId() + "");
				smscontent.setMessage(errorcontent);
				smscontent.setRecordtime(time);
				smscontent.setSubtype("db");
				smscontent.setSubentity("sqldb");
				smscontent.setIp(dbmonitorlist.getIpAddress());
				// 发送短信
				try {
					smsmanager.sendURLSmscontent(smscontent);
				} catch (Exception e) {

				}
				sendeddata.put(ipaddress + ":" + sqldbconfig.getDbname() + ":" + sqldbconfig.getLogflag(), date);
			} else {
				// 若在，则从已发送短信列表里判断是否已经发送当天的短信
				Calendar formerdate = (Calendar) sendeddata.get(ipaddress + ":" + sqldbconfig.getDbname() + ":"
						+ sqldbconfig.getLogflag());
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				Date last = null;
				Date current = null;
				Calendar sendcalen = formerdate;
				Date cc = sendcalen.getTime();
				String tempsenddate = formatter.format(cc);

				Calendar currentcalen = date;
				cc = currentcalen.getTime();
				last = formatter.parse(tempsenddate);
				String currentsenddate = formatter.format(cc);
				current = formatter.parse(currentsenddate);

				long subvalue = current.getTime() - last.getTime();
				if (subvalue / (1000 * 60 * 60 * 24) >= 1) {
					// 超过一天，则再发信息
					Smscontent smscontent = new Smscontent();
					// String time1 = sdf.format(date.getTime());
					smscontent.setLevel("2");
					smscontent.setObjid(dbmonitorlist.getId() + "");
					smscontent.setMessage(errorcontent);
					smscontent.setRecordtime(time);
					smscontent.setSubtype("db");
					smscontent.setSubentity("sqldb");
					smscontent.setIp(dbmonitorlist.getIpAddress());
					// 发送短信
					try {
						smsmanager.sendURLSmscontent(smscontent);
					} catch (Exception e) {

					}
					// 修改已经发送的短信记录
					sendeddata.put(ipaddress + ":" + sqldbconfig.getDbname() + ":" + sqldbconfig.getLogflag(), date);
				} else {
					// 则写声音告警数据
					// 向声音告警表里写数据
					/* modify by zhao 2010-5-7 */
					AlarmInfo alarminfo = new AlarmInfo();
					alarminfo.setContent(errorcontent);
					alarminfo.setIpaddress(ipaddress);
					alarminfo.setLevel1(new Integer(2));
					alarminfo.setRecordtime(Calendar.getInstance());
					alarminfomanager.save(alarminfo);
					// ///
//					 SmscontentDao content=new SmscontentDao();
//					 content.createEventWithReasion("poll",dbmonitorlist.getId()+"",dbmonitorlist.getAlias()+
//					 "(" + dbmonitorlist.getIpAddress() + ")", errorcontent,
//					 2, "db", "sqldb", "表空间超过阀值");
					/* modify end --------------- */

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createFileNotExistSMS(String ipaddress) {
		// 建立短信
		// 从内存里获得当前这个IP的PING的值
		Calendar date = Calendar.getInstance();
		try {
			Host host = (Host) PollingEngine.getInstance().getNodeByIP(ipaddress);
			if (host == null)
				return;

			if (!sendeddata.containsKey(ipaddress + ":file:" + host.getId())) {
				// 若不在，则建立短信，并且添加到发送列表里
				Smscontent smscontent = new Smscontent();
				String time = sdf.format(date.getTime());
				smscontent.setLevel("3");
				smscontent.setObjid(host.getId() + "");
				smscontent.setMessage(host.getAlias() + " (" + host.getIpAddress() + ")" + "的日志文件无法正确上传到网管服务器");
				smscontent.setRecordtime(time);
				smscontent.setSubtype("host");
				smscontent.setSubentity("ftp");
				smscontent.setIp(host.getIpAddress());// 发送短信
				SmscontentDao smsmanager = new SmscontentDao();
				smsmanager.sendURLSmscontent(smscontent);
				sendeddata.put(ipaddress + ":file" + host.getId(), date);
			} else {
				// 若在，则从已发送短信列表里判断是否已经发送当天的短信
				Calendar formerdate = (Calendar) sendeddata.get(ipaddress + ":file:" + host.getId());
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				Date last = null;
				Date current = null;
				Calendar sendcalen = formerdate;
				Date cc = sendcalen.getTime();
				String tempsenddate = formatter.format(cc);

				Calendar currentcalen = date;
				cc = currentcalen.getTime();
				last = formatter.parse(tempsenddate);
				String currentsenddate = formatter.format(cc);
				current = formatter.parse(currentsenddate);

				long subvalue = current.getTime() - last.getTime();
				if (subvalue / (1000 * 60 * 60 * 24) >= 1) {
					// 超过一天，则再发信息
					Smscontent smscontent = new Smscontent();
					String time = sdf.format(date.getTime());
					smscontent.setLevel("3");
					smscontent.setObjid(host.getId() + "");
					smscontent.setMessage(host.getAlias() + " (" + host.getIpAddress() + ")" + "的日志文件无法正确上传到网管服务器");
					smscontent.setRecordtime(time);
					smscontent.setSubtype("host");
					smscontent.setSubentity("ftp");
					smscontent.setIp(host.getIpAddress());// 发送短信
					SmscontentDao smsmanager = new SmscontentDao();
					smsmanager.sendURLSmscontent(smscontent);
					// 修改已经发送的短信记录
					sendeddata.put(ipaddress + ":file:" + host.getId(), date);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createSMS(String db, DBVo dbmonitorlist) {
		// 建立短信
		// 从内存里获得当前这个IP的PING的值
		Calendar date = Calendar.getInstance();
		try {
			if (!sendeddata.containsKey(db + ":" + dbmonitorlist.getIpAddress())) {
				// 若不在，则建立短信，并且添加到发送列表里
				Smscontent smscontent = new Smscontent();
				String time = sdf.format(date.getTime());
				smscontent.setLevel("2");
				smscontent.setObjid(dbmonitorlist.getId() + "");
				smscontent.setMessage(db + "(" + dbmonitorlist.getDbName() + " IP:" + dbmonitorlist.getIpAddress() + ")"
						+ "的数据库服务停止");
				smscontent.setRecordtime(time);
				smscontent.setSubtype("db");
				smscontent.setSubentity("ping");
				smscontent.setIp(dbmonitorlist.getIpAddress());
				// 发送短信
				SmscontentDao smsmanager = new SmscontentDao();
				try {
					smsmanager.sendDatabaseSmscontent(smscontent);
				} catch (Exception e) {

				}
				sendeddata.put(db + ":" + dbmonitorlist.getIpAddress(), date);
			} else {
				// 若在，则从已发送短信列表里判断是否已经发送当天的短信
				Calendar formerdate = (Calendar) sendeddata.get(db + ":" + dbmonitorlist.getIpAddress());
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				Date last = null;
				Date current = null;
				Calendar sendcalen = formerdate;
				Date cc = sendcalen.getTime();
				String tempsenddate = formatter.format(cc);

				Calendar currentcalen = date;
				cc = currentcalen.getTime();
				last = formatter.parse(tempsenddate);
				String currentsenddate = formatter.format(cc);
				current = formatter.parse(currentsenddate);

				long subvalue = current.getTime() - last.getTime();
				if (subvalue / (1000 * 60 * 60 * 24) >= 1) {
					// 超过一天，则再发信息
					Smscontent smscontent = new Smscontent();
					String time = sdf.format(date.getTime());
					smscontent.setLevel("2");
					smscontent.setObjid(dbmonitorlist.getId() + "");
					smscontent.setMessage(db + "(" + dbmonitorlist.getDbName() + " IP:" + dbmonitorlist.getIpAddress() + ")"
							+ "的数据库服务停止");
					smscontent.setRecordtime(time);
					smscontent.setSubtype("db");
					smscontent.setSubentity("ping");
					smscontent.setIp(dbmonitorlist.getIpAddress());
					// smscontent.setMessage("db&"+time+"&"+dbmonitorlist.getId()+"&"+db+"("+dbmonitorlist.getDbName()+"
					// IP:"+dbmonitorlist.getIpAddress()+")"+"的数据库服务停止");
					// 发送短信
					SmscontentDao smsmanager = new SmscontentDao();
					try {
						smsmanager.sendDatabaseSmscontent(smscontent);
					} catch (Exception e) {

					}
					// 修改已经发送的短信记录
					sendeddata.put(db + ":" + dbmonitorlist.getIpAddress(), date);
				} else {
					/*-------modify  zhao--------------------------*/
					String eventdesc = db + "(" + dbmonitorlist.getDbName() + " IP:" + dbmonitorlist.getIpAddress() + ")"
							+ "的数据库服务停止";
					SmscontentDao eventdao = new SmscontentDao();
					eventdao.createEvent("poll", dbmonitorlist.getId() + "", dbmonitorlist.getAlias() + "("+ dbmonitorlist.getIpAddress() + ")", eventdesc, 2, "db", "ping");
					/*----------------------------------------------------*/
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * 将采集到的SqlServer数据库信息保存到数据库中
	 * @param serverip
	 * @param sqlserverdata
	 */
	public void saveSqlServerData(String serverip,Hashtable sqlserverdata){
		if(sqlserverdata == null || sqlserverdata.size() == 0){
			return ;
		}
		String status = String.valueOf(sqlserverdata.get("status"));//状态信息
		Hashtable retValue = (Hashtable)sqlserverdata.get("retValue");
		Hashtable dbValue = (Hashtable)sqlserverdata.get("dbValue");//库信息
		Hashtable sysValue = (Hashtable)sqlserverdata.get("sysValue");//系统信息
		Vector lockinfo_v = (Vector)sqlserverdata.get("lockinfo_v");//锁信息
		Vector info_v = (Vector)sqlserverdata.get("info_v");//进程信息
		DBDao dbDao = null;
		try {
			dbDao = new DBDao();
			if(sqlserverdata.containsKey("status")){
				try {
					dbDao.clearTableData("nms_sqlserverstatus", serverip);
					dbDao.addSqlserver_nmsstatus(serverip,status);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(retValue.containsKey("pages")){
				Hashtable pages = (Hashtable)retValue.get("pages");
				dbDao.clearTableData("nms_sqlserverpages", serverip);
				try {
					dbDao.addSqlserver_nmspages(serverip,pages);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(retValue.containsKey("conns")){
				Hashtable conns = (Hashtable)retValue.get("conns");
				dbDao.clearTableData("nms_sqlserverconns", serverip);
				try {
					dbDao.addSqlserver_nmsconns(serverip,conns);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(retValue.containsKey("locks")){
				Hashtable locks = (Hashtable)retValue.get("locks");
				dbDao.clearTableData("nms_sqlserverlocks", serverip);
				try {
					dbDao.addSqlserver_nmslocks(serverip,locks);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(retValue.containsKey("caches")){
				Hashtable caches = (Hashtable)retValue.get("caches");
				dbDao.clearTableData("nms_sqlservercaches", serverip);
				try {
					dbDao.addSqlserver_nmscaches(serverip,caches);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(retValue.containsKey("mems")){
				Hashtable mems = (Hashtable)retValue.get("mems");
				dbDao.clearTableData("nms_sqlservermems", serverip);
				try {
					dbDao.addSqlserver_nmsmems(serverip,mems);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(retValue.containsKey("sqls")){
				Hashtable sqls = (Hashtable)retValue.get("sqls");
				dbDao.clearTableData("nms_sqlserversqls", serverip);
				try {
					dbDao.addSqlserver_nmssqls(serverip,sqls);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(retValue.containsKey("scans")){
				Hashtable scans = (Hashtable)retValue.get("scans");
				dbDao.clearTableData("nms_sqlserverscans", serverip);
				try {
					dbDao.addSqlserver_nmsscans(serverip,scans);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(retValue.containsKey("statisticsHash")){
				Hashtable statisticsHash = (Hashtable)retValue.get("statisticsHash");
				dbDao.clearTableData("nms_sqlserverstatisticshash", serverip);
				try {
					dbDao.addSqlserver_nmsstatisticsHash(serverip,statisticsHash);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(dbValue != null && dbValue.size() != 0){
				dbDao.clearTableData("nms_sqlserverdbvalue", serverip);
				try {
					Hashtable logfile = (Hashtable)dbValue.get("logfile");
					Hashtable database = (Hashtable)dbValue.get("database");
					Vector names = (Vector)dbValue.get("names");
					Iterator iter = logfile.entrySet().iterator(); 
					String label = "0";
					while (iter.hasNext()) { 
					    Map.Entry entry = (Map.Entry) iter.next(); 
					    String key = String.valueOf(entry.getKey()); 
					    Hashtable val = (Hashtable)entry.getValue(); 
					    dbDao.addSqlserver_nmsdbvalue(serverip,val,"",label);
					} 
					label = "1";
					iter = database.entrySet().iterator(); 
					while (iter.hasNext()) { 
					    Map.Entry entry = (Map.Entry) iter.next(); 
					    String key = String.valueOf(entry.getKey()); 
					    Hashtable val = (Hashtable)entry.getValue(); 
					    dbDao.addSqlserver_nmsdbvalue(serverip,val,"",label);
					} 
					label = "2";
					for(int i=0;i<names.size();i++){
						dbDao.addSqlserver_nmsdbvalue(serverip, null, String.valueOf(names.get(i)), label);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(sysValue != null && sysValue.size()>0){
				dbDao.clearTableData("nms_sqlserversysvalue", serverip);
				try {
					dbDao.addSqlserver_nmssysvalue(serverip,sysValue);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(lockinfo_v != null && lockinfo_v.size()>0){
				dbDao.clearTableData("nms_sqlserverlockinfo_v", serverip);
				try {
					Hashtable lockinfoHash = null;
					for(int i=0;i<lockinfo_v.size();i++){
						lockinfoHash = (Hashtable)lockinfo_v.get(i);
						dbDao.addSqlserver_nmslockinfo_v(serverip,lockinfoHash);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(info_v != null && info_v.size()>0){
				dbDao.clearTableData("nms_sqlserverinfo_v", serverip);
				try {
					Hashtable infoHash = null;
					for(int i=0;i<info_v.size();i++){
						infoHash = (Hashtable)info_v.get(i);
						dbDao.addSqlserver_nmsinfo_v(serverip,infoHash);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			dbDao.close();
		}
	}
	
	/**
	 * 清空指定serverip的sqlserver数据临时表的数据
	 * @param dbdao
	 * @param serverip
	 */
	private void clearSqlserverNmsTableData(DBDao dbdao, String serverip) {
		dbdao.clearTableData("nms_sqlservercaches", serverip);
		dbdao.clearTableData("nms_sqlserverconns", serverip);
		dbdao.clearTableData("nms_sqlserverdbvalue", serverip);
		dbdao.clearTableData("nms_sqlserverinfo_v", serverip);
		dbdao.clearTableData("nms_sqlserverlockinfo_v", serverip);
		dbdao.clearTableData("nms_sqlserverlocks", serverip);
		dbdao.clearTableData("nms_sqlservermems", serverip);
		dbdao.clearTableData("nms_sqlserverpages", serverip);
		dbdao.clearTableData("nms_sqlserverscans", serverip);
		dbdao.clearTableData("nms_sqlserversqls", serverip);
		dbdao.clearTableData("nms_sqlserverstatisticshash", serverip);
	}
}
