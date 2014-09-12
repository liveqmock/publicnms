package com.afunms.polling.snmp.db;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmConstant;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.dao.DBDao;
import com.afunms.application.dao.SybspaceconfigDao;
import com.afunms.application.model.DBVo;
import com.afunms.application.model.SybaseVO;
import com.afunms.application.model.Sybspaceconfig;
import com.afunms.application.model.TablesVO;
import com.afunms.application.util.IpTranslation;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.EncryptUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.indicators.dao.NodeGatherIndicatorsDao;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.DBNode;
import com.afunms.system.util.TimeGratherConfigUtil;

public class SybaseDataCollector {
	
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
	    		monitorItemList = indicatorsdao.getByInterval("5", "m",1,"db","sybase");
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
			DBNode dbnode = (DBNode) PollingEngine.getInstance().getDbByID(dbmonitorlist.getId());
			//判断设备是否在采集时间段内 0:不在采集时间段内,则退出;1:在时间段内,进行采集;2:不存在采集时间段设置,则全天采集
			TimeGratherConfigUtil timeconfig = new TimeGratherConfigUtil();
			int result = 0;
			result = timeconfig.isBetween(dbnode.getId()+"", "db");
			if(result == 0){
				SysLogger.info("###### "+dbnode.getIpAddress()+" 不在采集时间段内,跳过######");
				return null;
			}

			String serverip = dbmonitorlist.getIpAddress();
			String username = dbmonitorlist.getUser();
			String passwords = EncryptUtil.decode(dbmonitorlist.getPassword());
//					SysLogger.info("#######################################################");
//					SysLogger.info("username: "+username+"===============passwords:"+passwords);
//					SysLogger.info("#######################################################");
			int port = Integer.parseInt(dbmonitorlist.getPort());
//					String dbnames = dbmonitorlist.getDbName();
//					Date d1 = new Date();
					//判断该数据库是否能连接上
			boolean sysbaseIsOK = false;
//					if (dbnode.getCollecttype() == SystemConstant.DBCOLLECTTYPE_SHELL) {
//						// 脚本采集方式
//						String filename = ResourceCenter.getInstance().getSysPath() + "/linuxserver/" + serverip + ".sysbase.log";
//						File file = new File(filename);
//						if (!file.exists()) {
//							// 文件不存在,则产生告警
//							try {
//								createFileNotExistSMS(serverip);
//							} catch (Exception e) {
//								e.printStackTrace();
//							}
//							return;
//						}
//						SysLogger.info("###开始解析SysBase:" + serverip + "数据文件###");
//
//						LoadSysbaseFile loadsysbase = new LoadSysbaseFile(filename);
//						Hashtable allSysbaseDatas = new Hashtable();
//						try {
//							allSysbaseDatas = loadsysbase.getSysbaseConfig();
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//						if (allSysbaseDatas != null && allSysbaseDatas.size() > 0) {
//							if (allSysbaseDatas.containsKey("status")) {
//								int status = Integer.parseInt(allSysbaseDatas.get("status").toString());
//								if (status == 1)
//									sysbaseIsOK = true;
//								if (!sysbaseIsOK) {
//									// 需要增加数据库所在的服务器是否能连通
//									Host host = (Host) PollingEngine.getInstance().getNodeByIP(serverip);
//									Vector ipPingData = (Vector) ShareData.getPingdata().get(serverip);
//									if (ipPingData != null) {
//										Pingcollectdata pingdata = (Pingcollectdata) ipPingData.get(0);
//										Calendar tempCal = (Calendar) pingdata.getCollecttime();
//										Date cc = tempCal.getTime();
//										String time = sdf.format(cc);
//										String lastTime = time;
//										String pingvalue = pingdata.getThevalue();
//										if (pingvalue == null || pingvalue.trim().length() == 0)
//											pingvalue = "0";
//										double pvalue = new Double(pingvalue);
//										if (pvalue == 0) {
//											// 主机服务器连接不上***********************************************
//											dbnode = (DBNode) PollingEngine.getInstance().getDbByID(dbmonitorlist.getId());
//											dbnode.setAlarm(true);
//											dbnode.setStatus(3);
//											List alarmList = dbnode.getAlarmMessage();
//											if (alarmList == null)
//												alarmList = new ArrayList();
//											dbnode.getAlarmMessage().add("数据库服务停止");
//											String sysLocation = "";
//											try {
//												SmscontentDao eventdao = new SmscontentDao();
//												String eventdesc = "SYBASE(" + dbmonitorlist.getDbName() + " IP:"
//														+ dbmonitorlist.getIpAddress() + ")" + "的数据库服务停止";
//												eventdao.createEventWithReasion("poll", dbmonitorlist.getId() + "", dbmonitorlist
//														.getAlias()
//														+ "(" + dbmonitorlist.getIpAddress() + ")", eventdesc, 3, "db", "ping",
//														"所在的服务器连接不上");
//											} catch (Exception e) {
//												e.printStackTrace();
//											}
//										} else {
//											Pingcollectdata hostdata = null;
//											hostdata = new Pingcollectdata();
//											hostdata.setIpaddress(serverip);
//											Calendar date = Calendar.getInstance();
//											hostdata.setCollecttime(date);
//											hostdata.setCategory("SYSPing");
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
//												dbnode.setStatus(3);
//												createSMS("sybase", dbmonitorlist);
//											} catch (Exception e) {
//												e.printStackTrace();
//											}
//										}
//
//									} else {
//										Pingcollectdata hostdata = null;
//										hostdata = new Pingcollectdata();
//										hostdata.setIpaddress(serverip);
//										Calendar date = Calendar.getInstance();
//										hostdata.setCollecttime(date);
//										hostdata.setCategory("SYSPing");
//										hostdata.setEntity("Utilization");
//										hostdata.setSubentity("ConnectUtilization");
//										hostdata.setRestype("dynamic");
//										hostdata.setUnit("%");
//										hostdata.setThevalue("0");
//										try {
//											dbdao.createHostData(hostdata);
//											// 发送短信
//											dbnode = (DBNode) PollingEngine.getInstance().getDbByID(dbmonitorlist.getId());
//											dbnode.setAlarm(true);
//											List alarmList = dbnode.getAlarmMessage();
//											if (alarmList == null)
//												alarmList = new ArrayList();
//											dbnode.getAlarmMessage().add("数据库服务停止");
//											dbnode.setStatus(3);
//											createSMS("sybase", dbmonitorlist);
//										} catch (Exception e) {
//											e.printStackTrace();
//										}
//									}
//								} else {
//									Pingcollectdata hostdata = null;
//									hostdata = new Pingcollectdata();
//									hostdata.setIpaddress(serverip);
//									Calendar date = Calendar.getInstance();
//									hostdata.setCollecttime(date);
//									hostdata.setCategory("SYSPing");
//									hostdata.setEntity("Utilization");
//									hostdata.setSubentity("ConnectUtilization");
//									hostdata.setRestype("dynamic");
//									hostdata.setUnit("%");
//									hostdata.setThevalue("100");
//									try {
//										dbdao.createHostData(hostdata);
//									} catch (Exception e) {
//										e.printStackTrace();
//									}
//								}
//								Hashtable retValue = new Hashtable();
//								if (sysbaseIsOK) {// 若数据库能连接上，则进行数据库数据的采集
//									SybaseVO sysbaseVO = new SybaseVO();
//
//									try {
//										// sysbaseVO =
//										// dbdao.getSysbaseInfo(serverip,
//										// port, username, passwords);
//										sysbaseVO = (SybaseVO) allSysbaseDatas.get("sysbase");
//									} catch (Exception e) {
//										e.printStackTrace();
//									}
//									if (sysbaseVO == null)
//										sysbaseVO = new SybaseVO();
//									
//									retValue.put("status", "1");
//									retValue.put("sysbaseVO", sysbaseVO);
//									
//									List allspace = sysbaseVO.getDbInfo();
//									if (allspace != null && allspace.size() > 0) {
//										for (int k = 0; k < allspace.size(); k++) {
//											TablesVO tvo = (TablesVO) allspace.get(k);
//											if (sybspaceconfig.containsKey(serverip + ":" + tvo.getDb_name())) {
//												// 告警判断
//												Sybspaceconfig sybconfig = (Sybspaceconfig) sybspaceconfig.get(serverip + ":"
//														+ tvo.getDb_name());
//												Integer usedperc = Integer.parseInt(tvo.getDb_usedperc());
//												if (usedperc > sybconfig.getAlarmvalue()) {
//													// 超过阀值告警
//													dbnode = (DBNode) PollingEngine.getInstance()
//															.getDbByID(dbmonitorlist.getId());
//													dbnode.setAlarm(true);
//													List alarmList = dbnode.getAlarmMessage();
//													if (alarmList == null)
//														alarmList = new ArrayList();
//													dbnode.getAlarmMessage().add(
//															sybconfig.getSpacename() + "表空间超过阀值" + sybconfig.getAlarmvalue());
//													dbnode.setStatus(3);
//													createSybSpaceSMS(dbmonitorlist, sybconfig);
//												}
//											}
//										}
//									}
//								}
//								if(retValue!=null)
//								   ShareData.setSysbasedata(serverip, retValue);
//							}
//						}
//						// ////////////////////////////////////////////////////////////
//					} else {
						//JDBC采集方式
						try {
//							System.out.println(serverip+"---"+username+"---"+passwords+"---"+port);
							dbdao = new DBDao();
							sysbaseIsOK = dbdao.getSysbaseIsOk(serverip, username, passwords, port);
						} catch (Exception e) {
							e.printStackTrace();
							sysbaseIsOK = false;
						} finally {
							dbdao.close();
						}
						Hashtable retValue = new Hashtable();
						if (sysbaseIsOK) {//若数据库能连接上，则进行数据库数据的采集
							SybaseVO sysbaseVO = new SybaseVO();
							try {
								dbdao = new DBDao();
								sysbaseVO = dbdao.getSysbaseInfo(serverip, port, username, passwords,gatherHash);
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								dbdao.close();
							}
							if (sysbaseVO == null)
								sysbaseVO = new SybaseVO();
						
							retValue.put("sysbaseVO", sysbaseVO);
							retValue.put("status", "1");
						}
						if(retValue!=null){
							ShareData.setSysbasedata(serverip, retValue);
							//保存sybase信息到数据库
							IpTranslation tranfer = new IpTranslation();
							String hex = tranfer.formIpToHex(serverip);
							saveSybaseData(hex+":"+dbmonitorlist.getId(), retValue);
						}
//					}
					updateData(dbmonitorlist,ShareData.getSysbasedata());
					SysLogger.info("#### 结束采集SYBASE数据库"+serverip+" ####" );
				//}
			//}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(dbdao!=null)
				dbdao.close();
			SysLogger.info("#### sysbasetask运行完毕 ####");
		}
		return returndata;
	}

	/**
	 * @author HONGLI 更新告警信息    
	 * @param vo 数据库实体
	 * @param collectingData 数据库实体中的各种数据信息
	 */
	public void updateData(Object vo , Object collectingData){
//		SysLogger.info("##############updateDate  开始###########");
		DBVo sybaseServer = (DBVo)vo;		
		Hashtable datahashtable = (Hashtable)collectingData;
//		SysLogger.info("######HONG  sybaseServer.getIpAddress()--"+sybaseServer.getIpAddress());
		Hashtable sysbasehashtable = (Hashtable)datahashtable.get(sybaseServer.getIpAddress());//得到采集sysbaseVO数据库的信息
		SybaseVO sybaseVO = (SybaseVO)sysbasehashtable.get("sysbaseVO");
//		SysLogger.info("######HONG sybaseVO--"+sybaseVO.getProcedure_hitrate());
		AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
		List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(sybaseServer.getId()), AlarmConstant.TYPE_DB, "sybase");//获取采集指标列表
//		SysLogger.info("##############HONG Sybase--list.size--"+list.size()+"###########");
		String serverip = sybaseServer.getIpAddress();
		CheckEventUtil checkEventUtil = new CheckEventUtil();
		NodeUtil nodeUtil = new NodeUtil();
		NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(sybaseServer);
		for(int i = 0 ; i < list.size() ; i ++){
			AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode)list.get(i);
//			SysLogger.info("##############HONG alarmIndicatorsNode.getEnabled--"+alarmIndicatorsNode.getEnabled()+"###########");
			if("1".equals(alarmIndicatorsNode.getEnabled())){
				String indicators = alarmIndicatorsNode.getName();
				String value = "";//value 是指实际数据库中的值，如 缓冲区命中率    HONGLI
//				SysLogger.info("##############HONG sybase-indicators--"+indicators+"##########");
				if("procedure_cache".equals(indicators)){
					value = sybaseVO.getProcedure_hitrate();
//					SysLogger.info("#######HONG sybase-sybaseVO.getProcedure_hitrate()-->  "+sybaseVO.getProcedure_hitrate()+"");
				}else if("cpu_busy_rate".equals(indicators)){
					value = sybaseVO.getCpu_busy_rate();
				}else if("io_busy_rate".equals(indicators)){
					value = sybaseVO.getIo_busy_rate();
				}else if("locks_count".equals(indicators)){
					value = sybaseVO.getLocks_count();
				}else if("data_hitrate".equals(indicators)){
					value = sybaseVO.getData_hitrate();//key 和nms_alarm_indicators_node 中sybase对应的name一致
				}else if ("tablespace".equalsIgnoreCase(indicators)){
					List allspace = sybaseVO.getDbInfo();
					SybspaceconfigDao sybspaceconfigManager = new SybspaceconfigDao();
					Hashtable sybspaceconfig = new Hashtable();
					try {
						sybspaceconfig = sybspaceconfigManager.getByAlarmflag(1);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						sybspaceconfigManager.close();
					}
					if (allspace != null && allspace.size() > 0) {
						for (int k = 0; k < allspace.size(); k++) {
							TablesVO tvo = (TablesVO) allspace.get(k);
							if (sybspaceconfig.containsKey(serverip + ":" + tvo.getDb_name())) {
								//告警判断
								Sybspaceconfig sybconfig = (Sybspaceconfig) sybspaceconfig.get(serverip + ":" + tvo.getDb_name());
								Integer usedperc = Integer.parseInt(tvo.getDb_usedperc());
								alarmIndicatorsNode.setLimenvalue0(sybconfig.getAlarmvalue() + "");
								alarmIndicatorsNode.setLimenvalue1(sybconfig.getAlarmvalue() + "");
								alarmIndicatorsNode.setLimenvalue2(sybconfig.getAlarmvalue() + "");
								checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, usedperc+"",tvo.getDb_name());
							}
						}
					}
				} else {
					continue;
				}
//				SysLogger.info("#######HONG sybase-indicator、value--"+indicators+"、"+value+"####");
				if(value == null)continue;
				checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, value,null);
			}
			
			
		}
	}
	
	
//	public void createFileNotExistSMS(String ipaddress) {
//		// 建立短信
//		// 从内存里获得当前这个IP的PING的值
//		Calendar date = Calendar.getInstance();
//		try {
//			Host host = (Host) PollingEngine.getInstance().getNodeByIP(ipaddress);
//			if (host == null)
//				return;
//
//			if (!sendeddata.containsKey(ipaddress + ":file:" + host.getId())) {
//				// 若不在，则建立短信，并且添加到发送列表里
//				Smscontent smscontent = new Smscontent();
//				String time = sdf.format(date.getTime());
//				smscontent.setLevel("3");
//				smscontent.setObjid(host.getId() + "");
//				smscontent.setMessage(host.getAlias() + " (" + host.getIpAddress() + ")" + "的日志文件无法正确上传到网管服务器");
//				smscontent.setRecordtime(time);
//				smscontent.setSubtype("host");
//				smscontent.setSubentity("ftp");
//				smscontent.setIp(host.getIpAddress());// 发送短信
//				SmscontentDao smsmanager = new SmscontentDao();
//				smsmanager.sendURLSmscontent(smscontent);
//				sendeddata.put(ipaddress + ":file" + host.getId(), date);
//			} else {
//				// 若在，则从已发送短信列表里判断是否已经发送当天的短信
//				Calendar formerdate = (Calendar) sendeddata.get(ipaddress + ":file:" + host.getId());
//				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//				Date last = null;
//				Date current = null;
//				Calendar sendcalen = formerdate;
//				Date cc = sendcalen.getTime();
//				String tempsenddate = formatter.format(cc);
//
//				Calendar currentcalen = date;
//				cc = currentcalen.getTime();
//				last = formatter.parse(tempsenddate);
//				String currentsenddate = formatter.format(cc);
//				current = formatter.parse(currentsenddate);
//
//				long subvalue = current.getTime() - last.getTime();
//				if (subvalue / (1000 * 60 * 60 * 24) >= 1) {
//					// 超过一天，则再发信息
//					Smscontent smscontent = new Smscontent();
//					String time = sdf.format(date.getTime());
//					smscontent.setLevel("3");
//					smscontent.setObjid(host.getId() + "");
//					smscontent.setMessage(host.getAlias() + " (" + host.getIpAddress() + ")" + "的日志文件无法正确上传到网管服务器");
//					smscontent.setRecordtime(time);
//					smscontent.setSubtype("host");
//					smscontent.setSubentity("ftp");
//					smscontent.setIp(host.getIpAddress());// 发送短信
//					SmscontentDao smsmanager = new SmscontentDao();
//					smsmanager.sendURLSmscontent(smscontent);
//					// 修改已经发送的短信记录
//					sendeddata.put(ipaddress + ":file:" + host.getId(), date);
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	/**
	 * 保存sybase的数据信息
	 * @param serverip  由转换后IP地址：数据库ID号  组成
	 * @param sybaseData 
	 */
	public void saveSybaseData(String serverip,Hashtable sybaseData){
		if(sybaseData == null || sybaseData.size() == 0){
			return ;
		}
		DBDao dbDao = new DBDao();
		try {
			String status = null;//数据库的状态信息
			SybaseVO sybaseVO = null;
			List dbInfo = null;//数据库信息
			List deviceInfo = null;//设备信息
			List userInfo = null;//用户信息
			List serversInfo = null;//服务器信息
			List processInfo = null;//进程信息engineInfo
			List dbsInfo = null;//进程信息
			List engineInfo = null;//进程信息
			if(sybaseData != null && sybaseData.containsKey("sysbaseVO")){
				sybaseVO = (SybaseVO)sybaseData.get("sysbaseVO");
			}
			if(sybaseData != null && sybaseData.containsKey("status")){
				status = (String)sybaseData.get("status");
				dbDao.clearTableData("nms_sybasestatus", serverip);
				dbDao.addSybase_nmsstatus(serverip, status);
			}
			
			if(sybaseVO != null){
				//保存性能信息
				dbDao.clearTableData("nms_sybaseperformance", serverip);
				dbDao.addSybase_nmsperformance(serverip, sybaseVO);
				dbInfo = sybaseVO.getDbInfo();
				deviceInfo = sybaseVO.getDeviceInfo();
				userInfo = sybaseVO.getUserInfo();
				serversInfo = sybaseVO.getServersInfo();
				processInfo = sybaseVO.getProcessInfo();
				dbsInfo = sybaseVO.getDbsInfo();
				engineInfo = sybaseVO.getEngineInfo();
				
				if(dbInfo != null && dbInfo.size() > 0){
					dbDao.clearTableData("nms_sybasedbinfo", serverip);
					dbDao.addSybase_nmsdbinfo(serverip, dbInfo);
				}
				if(dbsInfo != null && dbsInfo.size() > 0){
					dbDao.clearTableData("nms_sybasedbdetailinfo", serverip);
					dbDao.addSybase_nmsdbdetailinfo(serverip, dbsInfo);
				}
				if(deviceInfo != null && deviceInfo.size() > 0){
					dbDao.clearTableData("nms_sybasedeviceinfo", serverip);
					dbDao.addSybase_nmsdeviceinfo(serverip, deviceInfo);
				}
				if(processInfo != null && processInfo.size() > 0){
					dbDao.clearTableData("nms_sybaseprocessinfo", serverip);
					dbDao.addSybase_nmsprocessinfo(serverip, processInfo);
				}
				if(userInfo != null && userInfo.size() > 0){
					dbDao.clearTableData("nms_sybaseuserinfo", serverip);
					dbDao.addSybase_nmsuserinfo(serverip, userInfo);
				}
				if(serversInfo != null && serversInfo.size() > 0){
					dbDao.clearTableData("nms_sybaseserversinfo", serverip);
					dbDao.addSybase_nmsserversinfo(serverip, serversInfo);
				}
				if(engineInfo != null && engineInfo.size() > 0){
					dbDao.clearTableData("nms_sybaseengineinfo", serverip);
					dbDao.addSybase_nmsengineinfo(serverip, engineInfo);
				}
			}
			//取数据
			testGetSybaseDataFormDB(serverip);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			dbDao.close();
		}
	}
	
	public void testGetSybaseDataFormDB(String serverip){
//		String status = null;//数据库的状态信息
		SybaseVO sybaseVO = null;
		List dbInfo = null;//数据库信息
		List deviceInfo = null;//设备信息
		List userInfo = null;//用户信息
		List serversInfo = null;//服务器信息
		DBDao dao = new DBDao();
		try {
//			Hashtable tempStatusHashtable = dao.getSybase_nmsstatus(serverip);
//			if(tempStatusHashtable != null && tempStatusHashtable.containsKey("status")){
//				status = (String)tempStatusHashtable.get("status");
//			}
			sybaseVO = dao.getSybase_nmssybaseperformance(serverip);
			dbInfo = dao.getSybase_nmsdbinfo(serverip);
			deviceInfo = dao.getSybase_nmsdeviceinfo(serverip);
			userInfo = dao.getSybase_nmsuserinfo(serverip);
			serversInfo = dao.getSybase_nmsserversinfo(serverip);
			sybaseVO.setDbInfo(dbInfo);
			sybaseVO.setDeviceInfo(deviceInfo);
			sybaseVO.setUserInfo(userInfo);
			sybaseVO.setServersInfo(serversInfo);
			System.out.println("aaaaa");
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			dao.close();
		}
		
		
		
	}
}
