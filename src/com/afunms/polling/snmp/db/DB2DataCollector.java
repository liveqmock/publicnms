package com.afunms.polling.snmp.db;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.apache.commons.beanutils.BeanUtils;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.dao.DBDao;
import com.afunms.application.dao.DBTypeDao;
import com.afunms.application.dao.Db2spaceconfigDao;
import com.afunms.application.dao.OraspaceconfigDao;
import com.afunms.application.model.DBTypeVo;
import com.afunms.application.model.DBVo;
import com.afunms.application.model.Db2spaceconfig;
import com.afunms.application.model.Oraspaceconfig;
import com.afunms.application.util.IpTranslation;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.EncryptUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SystemConstant;
import com.afunms.event.dao.AlarmInfoDao;
import com.afunms.event.dao.SmscontentDao;
import com.afunms.event.model.AlarmInfo;
import com.afunms.event.model.Smscontent;
import com.afunms.indicators.dao.NodeGatherIndicatorsDao;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.DBNode;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.Pingcollectdata;
import com.afunms.polling.snmp.LoadDB2File;
import com.afunms.system.util.TimeGratherConfigUtil;

public class DB2DataCollector{

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicator) {
		DBDao dbdao = null;
		Hashtable returndata = new Hashtable();
		List dbmonitorlists = new ArrayList();
		NodeGatherIndicatorsDao indicatorsdao = new NodeGatherIndicatorsDao();
    	List<NodeGatherIndicators> monitorItemList = new ArrayList<NodeGatherIndicators>(); 
		dbmonitorlists = ShareData.getDBList();
		try{	
			DBVo dbmonitorlist = new DBVo();
			if (dbmonitorlists != null && dbmonitorlists.size() > 0) {
				for (int i = 0; i < dbmonitorlists.size(); i++) {
					DBVo vo = (DBVo) dbmonitorlists.get(i);
					if (vo.getId() == Integer.parseInt(nodeGatherIndicator
							.getNodeid())) {
						dbmonitorlist = vo;
						break;
					}
				}
			}
			//未管理
			if (dbmonitorlist.getManaged() == 0) {
				// 如果未被管理，不采集，信息为空
				return returndata;
			}
			try{
	    		//获取被启用的DB2所有被监视指标
	    		monitorItemList = indicatorsdao.getByInterval("5", "m",1,"db","db2");
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}finally{
	    		indicatorsdao.close();
	    	}
			
	    	if(monitorItemList == null)monitorItemList = new ArrayList<NodeGatherIndicators>();
	    	Hashtable gatherHash = new Hashtable();
	    	for(int i=0;i<monitorItemList.size();i++){
	    		NodeGatherIndicators nodeGatherIndicators = (NodeGatherIndicators)monitorItemList.get(i);
	    		if(nodeGatherIndicators.getNodeid().equals(nodeGatherIndicator.getNodeid())){
	    			gatherHash.put(nodeGatherIndicators.getName(), nodeGatherIndicators);
	    		}
	    	}
			//判断设备是否在采集时间段内 0:不在采集时间段内,则退出;1:在时间段内,进行采集;2:不存在采集时间段设置,则全天采集
//			TimeGratherConfigUtil timeconfig = new TimeGratherConfigUtil();
//			int result = 0;
//			result = timeconfig.isBetween(dbnode.getId()+"", "db");
//			if(result == 0){
//				SysLogger.info("###### "+dbnode.getIpAddress()+" 不在采集时间段内,跳过######");
//				return;
//			}
			

			String serverip = dbmonitorlist.getIpAddress();
			String username = dbmonitorlist.getUser();
			String passwords = EncryptUtil.decode(dbmonitorlist.getPassword());
			int port = Integer.parseInt(dbmonitorlist.getPort());
			String dbnames = dbmonitorlist.getDbName();
			//判断该数据库是否能连接上
//			String[] dbs = dbnames.split(",");
			//SysLogger.info("process db2 ====== "+serverip);
//			int allFlag = 0;
			
//					if (dbnode.getCollecttype() == SystemConstant.DBCOLLECTTYPE_SHELL) {
//						//	// 脚本采集方式
//						String filename = ResourceCenter.getInstance().getSysPath() + "linuxserver/" + serverip + ".db2.log";
//						File file = new File(filename);
//						if (!file.exists()) {
//							// 文件不存在,则产生告警
//							System.out.println("文件不存在");
//							try {
//								createFileNotExistSMS(serverip);
//							} catch (Exception e) {
//								e.printStackTrace();
//							}
//							return;
//						}
//						SysLogger.info("###开始解析DB2:" + serverip + "数据文件###");
//						LoadDB2File load = new LoadDB2File(filename);
//						Hashtable db2Data = new Hashtable();
//						try {
//							db2Data = load.getDB2Init();
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//						boolean db2IsOK = false;
//						if (db2Data != null && db2Data.size() > 0) {
//							if (db2Data.containsKey("status")) {
//								System.out.println(db2Data.get("status"));
//								int status = Integer.parseInt((String) db2Data.get("status"));
//								if (status == 1)
//									db2IsOK = true;
//								if (!db2IsOK) {
//									dbnode = (DBNode) PollingEngine.getInstance().getDbByID(dbmonitorlist.getId());
//									dbnode.setAlarm(true);
//									dbnode.setStatus(3);
//									// createSMS("db2",dbmonitorlist);
//									allFlag = 1;
//								}
//								if (gatherHash.containsKey("ping")) {
//									if (allFlag == 1) {
//										// 有一个数据库是不通的
//										// 需要增加数据库所在的服务器是否能连通
//										Host host = (Host) PollingEngine.getInstance().getNodeByIP(serverip);
//										Vector ipPingData = (Vector) ShareData.getPingdata().get(serverip);
//										if (ipPingData != null) {
//											Pingcollectdata pingdata = (Pingcollectdata) ipPingData.get(0);
//											Calendar tempCal = (Calendar) pingdata.getCollecttime();
//											Date cc = tempCal.getTime();
//											String time = sdf.format(cc);
//											String lastTime = time;
//											String pingvalue = pingdata.getThevalue();
//											if (pingvalue == null || pingvalue.trim().length() == 0)
//												pingvalue = "0";
//											double pvalue = new Double(pingvalue);
//											if (pvalue == 0) {
//												// 主机服务器连接不上***********************************************
//												dbnode = (DBNode) PollingEngine.getInstance().getDbByID(dbmonitorlist.getId());
//												dbnode.setAlarm(true);
//												dbnode.setStatus(3);
//												List alarmList = dbnode.getAlarmMessage();
//												if (alarmList == null)
//													alarmList = new ArrayList();
//												dbnode.getAlarmMessage().add("数据库服务停止");
//												String sysLocation = "";
//												try {
//													SmscontentDao eventdao = new SmscontentDao();
//													String eventdesc = "DB2(" + dbmonitorlist.getDbName() + " IP:"
//															+ dbmonitorlist.getIpAddress() + ")" + "的数据库服务停止";
//													eventdao.createEventWithReasion("poll", dbmonitorlist.getId() + "", dbmonitorlist
//															.getAlias()
//															+ "(" + dbmonitorlist.getIpAddress() + ")", eventdesc, 3, "db", "ping",
//															"所在的服务器连接不上");
//												} catch (Exception e) {
//													e.printStackTrace();
//												}
//											} else {
//												Pingcollectdata hostdata = null;
//												hostdata = new Pingcollectdata();
//												hostdata.setIpaddress(serverip);
//												Calendar date = Calendar.getInstance();
//												hostdata.setCollecttime(date);
//												hostdata.setCategory("DB2Ping");
//												hostdata.setEntity("Utilization");
//												hostdata.setSubentity("ConnectUtilization");
//												hostdata.setRestype("dynamic");
//												hostdata.setUnit("%");
//												hostdata.setThevalue("0");
//												try {
//													dbdao.createHostData(hostdata);
//													// 发送短信
//													dbnode = (DBNode) PollingEngine.getInstance().getDbByID(dbmonitorlist.getId());
//													dbnode.setAlarm(true);
//													List alarmList = dbnode.getAlarmMessage();
//													if (alarmList == null)
//														alarmList = new ArrayList();
//													dbnode.getAlarmMessage().add("数据库服务停止");
//													dbnode.setStatus(3);
//													createSMS("db2", dbmonitorlist);
//												} catch (Exception e) {
//													e.printStackTrace();
//												}
//											}
//	
//										} else {
//											Pingcollectdata hostdata = null;
//											hostdata = new Pingcollectdata();
//											hostdata.setIpaddress(serverip);
//											Calendar date = Calendar.getInstance();
//											hostdata.setCollecttime(date);
//											hostdata.setCategory("DB2Ping");
//											hostdata.setEntity("Utilization");
//											hostdata.setSubentity("ConnectUtilization");
//											hostdata.setRestype("dynamic");
//											hostdata.setUnit("%");
//											hostdata.setThevalue("0");
//											try {
//												dbdao.createHostData(hostdata);
//												// 发送短信
//												dbnode = (DBNode) PollingEngine.getInstance().getDbByID(dbmonitorlist.getId());
//												dbnode.setAlarm(true);
//												List alarmList = dbnode.getAlarmMessage();
//												if (alarmList == null)
//													alarmList = new ArrayList();
//												dbnode.getAlarmMessage().add("数据库服务停止");
//	
//												dbnode.setStatus(3);
//												createSMS("db2", dbmonitorlist);
//											} catch (Exception e) {
//												e.printStackTrace();
//											}
//										}
//	
//									} else {
//										// 所有数据库是连通的
//										Pingcollectdata hostdata = null;
//										hostdata = new Pingcollectdata();
//										hostdata.setIpaddress(serverip);
//										Calendar date = Calendar.getInstance();
//										hostdata.setCollecttime(date);
//										hostdata.setCategory("DB2Ping");
//										hostdata.setEntity("Utilization");
//										hostdata.setSubentity("ConnectUtilization");
//										hostdata.setRestype("dynamic");
//										hostdata.setUnit("%");
//										hostdata.setThevalue("100");
//										try {
//											dbdao.createHostData(hostdata);
//											if (sendeddata.containsKey("db2" + ":" + serverip))
//												sendeddata.remove("db2" + ":" + serverip);
//										} catch (Exception e) {
//											e.printStackTrace();
//										}
//									}
//								
//							}
//								
//								Hashtable spaceHash = (Hashtable) db2Data.get("spaceInfo");
//								String[] alldbs = dbnames.split(",");
//								Hashtable alltype6spaceHash = new Hashtable();
//								Hashtable type6spaceHash = new Hashtable();
//								for (int k = 0; k < alldbs.length; k++) {
//									String dbStr = alldbs[k];
//									List type6space = new ArrayList();
//									if (spaceHash.containsKey(dbStr)) {
//										List retList = (List) spaceHash.get(dbStr);
//										if (retList != null && retList.size() > 0) {
//											for (int j = 0; j < retList.size(); j++) {
//												Hashtable sys_hash = (Hashtable) retList.get(j);
//												if (sys_hash != null && sys_hash.size() > 0) {
//													// if(sys_hash.get("container_type")!=null){
//													// 只有当container_type=6的时候该数据库才是用户管理的表大小
//													// if(sys_hash.get("container_type").toString().equals("6")){
//													type6space.add(sys_hash);
//													// 判断告警
//													Db2spaceconfigDao db2spaceconfigManager = new Db2spaceconfigDao();
//													Hashtable db2alarm = db2spaceconfigManager.getByAlarmflag(1);
//													db2spaceconfigManager.close();
//													if (db2alarm != null && db2alarm.size() > 0) {
//														SysLogger.info("判断    "+serverip + ":" + dbStr + ":"+ sys_hash.get("tablespace_name").toString());
//														if (db2alarm.containsKey(serverip + ":" + dbStr + ":"
//																+ sys_hash.get("tablespace_name").toString())) {
//															// 判断值是否越界
//															Db2spaceconfig db2spaceconfig = (Db2spaceconfig) db2alarm
//																	.get(serverip + ":" + dbStr + ":"
//																			+ sys_hash.get("tablespace_name").toString());
//															String usableper = (String) sys_hash.get("usableper");
//															if (usableper.trim().length() == 0)
//																usableper = "0";
//															float usablefloatper = new Float(usableper);
//
//															if (db2spaceconfig.getAlarmvalue() < (100 - new Float(usablefloatper)
//																	.intValue())) {
//																// 告警
//																dbnode = (DBNode) PollingEngine.getInstance().getDbByID(
//																		dbmonitorlist.getId());
//																dbnode.setAlarm(true);
//																dbnode.setStatus(3);
//																List alarmList = dbnode.getAlarmMessage();
//																if (alarmList == null)
//																	alarmList = new ArrayList();
//																dbnode.getAlarmMessage().add(
//																		db2spaceconfig.getSpacename() + "表空间超过阀值"
//																				+ db2spaceconfig.getAlarmvalue());
//																createDb2SpaceSMS(dbmonitorlist, db2spaceconfig);
//															}
//														}
//													}
//												}
//											}
//										}
//									}
//									if (type6space != null && type6space.size() > 0) {
//										// 将type为6的表空间加进容器
//										type6spaceHash.put(dbStr, type6space);
//									}
//								}
//								if (type6spaceHash != null && type6spaceHash.size() > 0) {
//									alltype6spaceHash.put(serverip, type6spaceHash);
//									ShareData.setDb2type6spacedata(serverip, alltype6spaceHash);
//								}
//								ShareData.setAlldb2data(serverip, db2Data);
//							}
//						}
//						// ////////////////////////////////////////////
//					} else {
						//JDBC采集方式
						Hashtable allDb2Data = new Hashtable();
						// 对DB2数据进行采集
//						System.out.println(serverip+"---"+port+"---"+dbnames+"---"+username+"---"+passwords+"---"+gatherHash.size());
						dbdao = new DBDao();
						try {
							allDb2Data = dbdao.getDB2Data(serverip, port, dbnames, username, passwords,gatherHash);
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
						}
						Hashtable spaceHash = new Hashtable();
						if(allDb2Data != null && allDb2Data.containsKey("spaceInfo")){
							spaceHash = (Hashtable)allDb2Data.get("spaceInfo");
						}
						IpTranslation tranfer = new IpTranslation();
						String hex = tranfer.formIpToHex(dbmonitorlist.getIpAddress());
						String sip = hex+":"+dbmonitorlist.getId();
						//更新状态信息
						String[] alldbs = dbnames.split(",");
							
//						// 对space信息进行采集
//						Hashtable spaceHash = dbdao.getDB2Space(serverip, port, dbnames, username, passwords);
//						allDb2Data.put("spaceInfo", spaceHash);
//
//						// 对sys信息进行采集
//						Hashtable sysHash = dbdao.getDB2Sys(serverip, port, dbnames, username, passwords);
//						allDb2Data.put("sysInfo", sysHash);
//
//						// 对pool信息进行采集
//						Hashtable poolHash = dbdao.getDB2Pool(serverip, port, dbnames, username, passwords);
//						allDb2Data.put("poolInfo", poolHash);
//
//						// 对session信息进行采集
//						Hashtable sessionHash = dbdao.getDB2Session(serverip, port, dbnames, username, passwords);
//						allDb2Data.put("session", sessionHash);

//						String[] alldbs = dbnames.split(",");
//
//						// 对lock信息进行采集
//						Hashtable lockInfo = new Hashtable<String, List>();
//						for (String db : alldbs) {
//							List lockHash = dbdao.getDB2Lock(serverip, port, dbnames, username, passwords);
//							lockInfo.put(db, lockHash);
//						}
//						allDb2Data.put("lock", lockInfo);
						
						Hashtable alltype6spaceHash = new Hashtable();
						Hashtable type6spaceHash = new Hashtable();
						try {
							for (int k = 0; k < alldbs.length; k++) {
								String dbStr = alldbs[k];
								List type6space = new ArrayList();
								if (spaceHash.containsKey(dbStr)) {
									List retList = (List) spaceHash.get(dbStr);
									if (retList != null && retList.size() > 0) {
										for (int j = 0; j < retList.size(); j++) {
											Hashtable sys_hash = (Hashtable) retList.get(j);
											if (sys_hash != null && sys_hash.size() > 0) {
												//if(sys_hash.get("container_type")!=null){
												//只有当container_type=6的时候该数据库才是用户管理的表大小
												//if(sys_hash.get("container_type").toString().equals("6")){
												type6space.add(sys_hash);
												//}
												//}
											}
										}
									}
									createDb2SpaceSMS(dbmonitorlist,retList,dbStr);
								}
								if (type6space != null && type6space.size() > 0) {
									//将type为6的表空间加进容器
									type6spaceHash.put(dbStr, type6space);
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							if (type6spaceHash != null && type6spaceHash.size() > 0) {
								alltype6spaceHash.put(serverip, type6spaceHash);
								ShareData.setDb2type6spacedata(serverip, alltype6spaceHash);
							}
							ShareData.setAlldb2data(serverip, allDb2Data);
							Hashtable monitorDB2Data = new Hashtable();
							monitorDB2Data.put("allDb2Data", allDb2Data);
							monitorDB2Data.put("alltype6spaceHash", alltype6spaceHash);
							monitorDB2Data.put("ip", serverip);
							//删除之前采集的DB2数据信息
							String[] tableNames = {"nms_db2tablespace","nms_db2common","nms_db2conn","nms_db2sysinfo",
									"nms_db2spaceinfo","nms_db2log","nms_db2write","nms_db2pool","nms_db2lock",
									"nms_db2read","nms_db2session","nms_db2cach"};
							dbdao.clearTablesData(tableNames, sip);
							//保存采集的DB2数据信息
							dbdao.addDB2_nmsinfo(sip, monitorDB2Data, alldbs);
							SysLogger.info("#### 结束采集 db2 " + serverip+" ####");
						} catch (Exception e) {
							e.printStackTrace();
						}
				//}
			//}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(dbdao!=null)
				dbdao.close();
			SysLogger.info("#### DB2采集任务运行完毕 ####");
		}
		return returndata;
	}

	public static void createDb2SpaceSMS(DBVo dbmonitorlist, List retList,String dbStr) {
		try {
			NodeUtil nodeUtil = new NodeUtil();
			NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(dbmonitorlist);
			// 判断是否存在此告警指标
			AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			List list = alarmIndicatorsUtil
					.getAlarmInicatorsThresholdForNode(nodeDTO
							.getId()
							+ "", nodeDTO.getType(), nodeDTO
							.getSubtype());
			CheckEventUtil checkEventUtil = new CheckEventUtil();
			for (int i = 0; i < list.size(); i++) {
				AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode) list.get(i);
				if ("tablespace".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
					if (retList != null && retList.size() > 0) {
						for (int j = 0; j < retList.size(); j++) {
							Hashtable sys_hash = (Hashtable) retList.get(j);
							if (sys_hash != null && sys_hash.size() > 0) {
								//判断告警
								String tablespace = sys_hash.get("tablespace_name").toString();
								Db2spaceconfigDao db2spaceconfigManager = new Db2spaceconfigDao();
								Hashtable db2alarm=null;
								try {
									db2alarm = db2spaceconfigManager.getByAlarmflag(1);
								} catch (Exception e) {
									e.printStackTrace();
								}finally {
									db2spaceconfigManager.close();
								}
								if (db2alarm != null && db2alarm.size() > 0) {
									if (db2alarm.containsKey(dbmonitorlist.getIpAddress() + ":" + dbStr + ":" + sys_hash.get("tablespace_name").toString())) {
										//判断值是否越界
										Db2spaceconfig db2spaceconfig = (Db2spaceconfig) db2alarm.get(dbmonitorlist.getIpAddress() + ":" + dbStr + ":" + sys_hash.get("tablespace_name").toString());
										String usableper = (String) sys_hash.get("usableper");
										if (usableper.trim().length() == 0)
											usableper = "0";
										float usablefloatper = new Float(usableper);
										alarmIndicatorsNode.setLimenvalue0(db2spaceconfig.getAlarmvalue() + "");
										alarmIndicatorsNode.setLimenvalue1(db2spaceconfig.getAlarmvalue() + "");
										alarmIndicatorsNode.setLimenvalue2(db2spaceconfig.getAlarmvalue() + "");
										checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, (100 - new Float(usablefloatper).intValue())+"",tablespace);
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
