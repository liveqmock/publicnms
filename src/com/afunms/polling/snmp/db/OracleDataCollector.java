package com.afunms.polling.snmp.db;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.dao.DBDao;
import com.afunms.application.dao.OraspaceconfigDao;
import com.afunms.application.model.DBVo;
import com.afunms.application.model.OracleLockInfo;
import com.afunms.application.model.OracleTopSqlReadWrite;
import com.afunms.application.model.OracleTopSqlSort;
import com.afunms.application.model.Oracle_sessiondata;
import com.afunms.application.model.Oraspaceconfig;
import com.afunms.application.util.IpTranslation;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.EncryptUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SystemConstant;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.DBNode;
import com.afunms.polling.om.Pingcollectdata;

public class OracleDataCollector {

	private Hashtable sendeddata = ShareData.getSendeddata();

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
	
	public OracleDataCollector(){
		
	}
	
	public void collect_data(String dbid,Hashtable gatherHash) {
		DBDao dbdao = null;
		try{
			List dbmonitorlists = new ArrayList(); 
			dbmonitorlists = ShareData.getDBList();
			DBVo dbmonitorlist = new DBVo(); 
			if(dbmonitorlists != null && dbmonitorlists.size()>0){
				for(int i=0;i<dbmonitorlists.size();i++){
					DBVo vo = (DBVo)dbmonitorlists.get(i);
					if(vo.getId() == Integer.parseInt(dbid)){
						dbmonitorlist = vo;
						break;
					}
				}
			}
			if(dbmonitorlist.getManaged() == 0)return;
			String serverip = dbmonitorlist.getIpAddress();
			//String username = oracle.getUser();
			String passwords = EncryptUtil.decode(dbmonitorlist.getPassword());
			int port = Integer.parseInt(dbmonitorlist.getPort());
			String dbnames = dbmonitorlist.getDbName();
			//List dbList = PollingEngine.getInstance().getDbList();
			DBNode dbnode = (DBNode) PollingEngine.getInstance().getDbByID(dbmonitorlist.getId());
			//if(!dbnode.isManaged())return;
			if (dbnode != null) {
				//SysLogger.info("###### "+dbnode.getIpAddress()+" 采集ORACLE######");
				dbnode.setStatus(0);
				dbnode.setAlarm(false);
				dbnode.getAlarmMessage().clear();
				Calendar _tempCal = Calendar.getInstance();
				Date _cc = _tempCal.getTime();
				String _time = sdf.format(_cc);
				dbnode.setLastTime(_time);
				
		    	//判断是否在采集时间段内
//				SysLogger.info(dbnode.getIpAddress()+"=============判断是否在采集时间段内");
//		    	if(ShareData.getTimegatherhash() != null){
//		    		SysLogger.info(dbnode.getIpAddress()+"=============判断是否在采集时间段内=="+dbnode.getId()+":db");
//		    		if(ShareData.getTimegatherhash().containsKey(dbnode.getId()+":db")){
//		    			SysLogger.info(dbnode.getIpAddress()+"=============判断是否在采集时间段内2");
//		    			TimeGratherConfigUtil timeconfig = new TimeGratherConfigUtil();
//		    			int _result = 0;
//		    			_result = timeconfig.isBetween((List)ShareData.getTimegatherhash().get(dbnode.getId()+":db"));
//		    			SysLogger.info("_result============="+_result);
//		    			if(_result ==1 ){
//		    				SysLogger.info("########时间段内: 开始采集 "+dbnode.getIpAddress()+" 数据库数据信息##########");
//		    			}else if(_result == 2){
//		    				SysLogger.info("########全天: 开始采集 "+dbnode.getIpAddress()+" 数据库数据信息##########");
//		    			}else {
//		    				SysLogger.info("######## "+dbnode.getIpAddress()+" 数据库不在采集时间段内,退出##########");
//		    				//清除之前内存中产生的告警信息
//		    			    try{
//		    			    	//清除之前内存中产生的跟数据库相关的告警信息
//								List list = new ArrayList();
//								NodeDTO nodedto = null;
//		    					NodeUtil nodeUtil = new NodeUtil();
//		    					nodedto = nodeUtil.conversionToNodeDTO(dbmonitorlist);
//								CheckEventUtil checkutil = new CheckEventUtil();
//								checkutil.deleteEvent(dbnode.getId()+"", nodedto.getType(), nodedto.getSubtype(), "ping", null);
//								checkutil.deleteEvent(dbnode.getId()+"", nodedto.getType(), nodedto.getSubtype(), "dbspace", null);
//		    			    }catch(Exception e){
//		    			    	e.printStackTrace();
//		    			    }
//		    				return ;
//		    			}
//		    			
//		    		}
//		    	}
				
//				//判断设备是否在采集时间段内 0:不在采集时间段内,则退出;1:在时间段内,进行采集;2:不存在采集时间段设置,则全天采集
//    			TimeGratherConfigUtil timeconfig = new TimeGratherConfigUtil();
//    			int result = 0;
//    			result = timeconfig.isBetween(dbnode.getId()+"", "db");
//				if(result == 0){
//					SysLogger.info("###### "+dbnode.getIpAddress()+" 不在采集时间段内,跳过######");
//					return;
//				}
				
			} else 
				return;	
			
			// 判断该数据库是否能连接上
			boolean oracleIsOK = false;
			dbnode = (DBNode) PollingEngine.getInstance().getDbByID(dbmonitorlist.getId());
	
			if (dbnode != null) {
				dbnode.setStatus(0);
				dbnode.setAlarm(false);
				dbnode.getAlarmMessage().clear();
				Calendar _tempCal = Calendar.getInstance();
				Date _cc = _tempCal.getTime();
				String _time = sdf.format(_cc);
				dbnode.setLastTime(_time);
			}
			
			if (dbmonitorlist.getCollecttype() == SystemConstant.DBCOLLECTTYPE_SHELL) {
				// 脚本采集方式
			} else {
				// JDBC采集方式
//				IpTranslation tranfer = new IpTranslation();
				String hex = IpTranslation.formIpToHex(serverip);
				Hashtable oracledata = new Hashtable();
				int flag = 0;
				try {
					dbdao = new DBDao();
					oracleIsOK = dbdao.getOracleIsOK(serverip, port, dbmonitorlist.getDbName(), dbmonitorlist.getUser(), EncryptUtil.decode(dbmonitorlist.getPassword()));
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					dbdao.close();
				}
				if (!oracleIsOK) {
					flag = 1;
					try{
						dbnode.setAlarm(true);
						dbnode.setStatus(3);
						//将之前存储的指定serverip的oracle临时数据表都清空
//						this.clearOracleNmsTableData(dbdao,hex + ":" + oracle.getId());
					}catch(Exception e){
						e.printStackTrace();
					}					
				}
				if (flag == 1) {
					// 需要增加数据库所在的服务器是否能连通
					Vector ipPingData = (Vector) ShareData.getPingdata().get(serverip);
					if (ipPingData != null) {
						Pingcollectdata pingdata = (Pingcollectdata) ipPingData.get(0);
						String pingvalue = pingdata.getThevalue();
						if (pingvalue == null || pingvalue.trim().length() == 0)
							pingvalue = "0";
						double pvalue = new Double(pingvalue);
						//SysLogger.info("ORACLE数据库=================="+dbmonitorlist.getIpAddress()+"====服务停止pvalue:"+pvalue);
						if (pvalue == 0) {
							// 主机服务器连接不上***********************************************
							try {
								dbnode = (DBNode) PollingEngine.getInstance().getDbByID(dbmonitorlist.getId());
								dbnode.setAlarm(true);
								dbnode.setStatus(3);
								List alarmList = dbnode.getAlarmMessage();
								if (alarmList == null)
									alarmList = new ArrayList();
								dbnode.getAlarmMessage().add("数据库服务全部停止");
//								String eventdesc = "ORACLE(" + " IP:" + dbmonitorlist.getIpAddress() + ")"
//										+ "的数据库服务停止";
//								eventdao.createEventWithReasion("poll", dbmonitorlist.getId() + ":" + oracle.getId(),
//										dbmonitorlist.getAlias() + "(" + dbmonitorlist.getIpAddress() + ":"
//												+ oracle.getSid() + ")", eventdesc, 3, "db", "ping", "所在的服务器连接不上");
//								 连通率进行判断
//								checkEvent(dbid,"ping",dbmonitorlist,pingdata.getThevalue(),"",3,0);//yangjun add
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								// eventdao.close();
							}
							//SysLogger.info("ORACLE数据库==========999========"+dbmonitorlist.getIpAddress()+"====服务停止");
						} else {
							try {
								Pingcollectdata hostdata = null;
								hostdata = new Pingcollectdata();
								hostdata.setIpaddress(serverip + ":" + dbmonitorlist.getId());
								Calendar date = Calendar.getInstance();
								hostdata.setCollecttime(date);
								hostdata.setCategory("ORAPing");
								hostdata.setEntity("Utilization");
								hostdata.setSubentity("ConnectUtilization");
								hostdata.setRestype("dynamic");
								hostdata.setUnit("%");
								hostdata.setThevalue("0");
								dbdao.createHostData(hostdata);
								dbnode = (DBNode) PollingEngine.getInstance().getDbByID(dbmonitorlist.getId());
								dbnode.setAlarm(true);
								dbnode.setStatus(3);
								List alarmList = dbnode.getAlarmMessage();
								if (alarmList == null)
									alarmList = new ArrayList();
								dbnode.getAlarmMessage().add("数据库服务停止");

								// String
								// ip=hostdata.getIpaddress();
								// hostdata.setIpaddress(ip+":"+)

								// 发送短信
								//SysLogger.info("开始写事件=================="+dbmonitorlist.getIpAddress());
//								createSMS("oracle", dbmonitorlist, oracle);
//								checkEvent(dbid,"ping",dbmonitorlist,"0","",3,0);//yangjun add
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

					} else {
						try {
							// for (OracleEntity or : stops) {
							Pingcollectdata hostdata = null;
							hostdata = new Pingcollectdata();
							hostdata.setIpaddress(serverip + ":" + dbmonitorlist.getId());
							Calendar date = Calendar.getInstance();
							hostdata.setCollecttime(date);
							hostdata.setCategory("ORAPing");
							hostdata.setEntity("Utilization");
							hostdata.setSubentity("ConnectUtilization");
							hostdata.setRestype("dynamic");
							hostdata.setUnit("%");
							hostdata.setThevalue("0");
							dbdao.createHostData(hostdata);
							dbnode = (DBNode) PollingEngine.getInstance().getDbByID(dbmonitorlist.getId());
							dbnode.setAlarm(true);
							List alarmList = dbnode.getAlarmMessage();
							if (alarmList == null)
								alarmList = new ArrayList();
							dbnode.getAlarmMessage().add("数据库服务停止");
							dbnode.setStatus(3);
							// }
							// Set set=allConnec.entrySet();
							// 发送短信
							//SysLogger.info("开始写事件=====555============="+dbmonitorlist.getIpAddress());
//							createSMS("oracle", dbmonitorlist, oracle);
//							checkEvent(dbid,"ping",dbmonitorlist,"0","",3,0);//yangjun add
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				} else {
					try {
						Pingcollectdata hostdata = null;
						hostdata = new Pingcollectdata();
						hostdata.setIpaddress(serverip + ":" + dbmonitorlist.getId());
						Calendar date = Calendar.getInstance();
						hostdata.setCollecttime(date);
						hostdata.setCategory("ORAPing");
						hostdata.setEntity("Utilization");
						hostdata.setSubentity("ConnectUtilization");
						hostdata.setRestype("dynamic");
						hostdata.setUnit("%");
						hostdata.setThevalue("100");
						try{
							dbdao.createHostData(hostdata);
						}catch(Exception e){
							e.printStackTrace();
						}finally{
							dbdao.close();
						}
//						checkEvent(dbid,"ping",dbmonitorlist,"100","",0,0);//yangjun add
//						if (sendeddata.containsKey("oracle" + ":" + serverip + ":" + dbmonitorlist.getDbName()))
//							sendeddata.remove("oracle" + ":" + serverip + ":" + dbmonitorlist.getDbName());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if(flag == 0){
					Date tempDate = new Date();
					SysLogger.info("####开始采集-sid为"+dbmonitorlist.getId()+"的oracle数据库...");
					Hashtable allOraData =  dbdao.getAllOracleData(serverip, port, dbmonitorlist.getDbName(), dbmonitorlist.getUser(), passwords,gatherHash);
					SysLogger.info("####采集-sid为"+dbmonitorlist.getId()+"的oracle数据库耗时+"+(new Date().getTime() - tempDate.getTime()));
					Vector info = (Vector) allOraData.get("session");
					Vector tableinfo = (Vector) allOraData.get("tablespace");
					Vector rollbackinfo_v = (Vector) allOraData.get("rollback");
					Hashtable sysValue = (Hashtable) allOraData.get("sysinfo");
					Hashtable memValue = (Hashtable) allOraData.get("ga_hash");
					Vector lockinfo_v = (Vector) allOraData.get("lock");
					Hashtable memPerfValue = (Hashtable) allOraData.get("memoryPerf");
					Vector table_v = (Vector) allOraData.get("table");
					Vector sql_v = (Vector) allOraData.get("topsql");
					Vector contrFile_v = (Vector) allOraData.get("controlfile");
					Hashtable isArchive_h = (Hashtable) allOraData.get("sy_hash");
					Vector logFile_v = (Vector) allOraData.get("log");
					Vector keepObj_v = (Vector) allOraData.get("keepobj");  
					String lstrnStatu = (String) allOraData.get("open_mode");
					Vector extent_v = (Vector) allOraData.get("extent");
					Hashtable userinfo_h = (Hashtable) allOraData.get("user");
					Hashtable cursors = (Hashtable) allOraData.get("cursors");
					Vector wait = (Vector) allOraData.get("wait");
					Hashtable dbio = (Hashtable) allOraData.get("dbio");
					OracleLockInfo oracleLockInfo = (OracleLockInfo) allOraData.get("oracleLockInfo");
					Vector<OracleTopSqlReadWrite> oracleTopSqlReadWriteVector = (Vector<OracleTopSqlReadWrite>) allOraData.get("topSqlReadWriteVector");
					Vector<OracleTopSqlSort> oracleTopSqlSortVector = (Vector<OracleTopSqlSort>) allOraData.get("topSqlSortVector");
					Hashtable<String, String> baseInfoHash = (Hashtable<String, String>) allOraData.get("baseInfoHash");
					
					if (info == null)
						info = new Vector();
					if (sysValue == null)
						sysValue = new Hashtable();
					if (memValue == null)
						memValue = new Hashtable();
					if (memPerfValue == null)
						memPerfValue = new Hashtable();
					if (tableinfo == null)
						tableinfo = new Vector();
					if (rollbackinfo_v == null)
						rollbackinfo_v = new Vector();
					if (lockinfo_v == null)
						lockinfo_v = new Vector();
					if (table_v == null)
						table_v = new Vector();
					if (sql_v == null)
						sql_v = new Vector();
					if (contrFile_v == null)
						contrFile_v = new Vector();
					if (logFile_v == null)
						logFile_v = new Vector();
					if (keepObj_v == null)
						keepObj_v = new Vector();
					if (isArchive_h == null)
						isArchive_h = new Hashtable();
					if(lstrnStatu == null)
						lstrnStatu = "";
					if(extent_v == null)
						extent_v = new Vector();
					if(userinfo_h == null)
						userinfo_h = new Hashtable();
					if(cursors == null)
						cursors = new Hashtable();
					if(wait == null)
						wait = new Vector();
					if(dbio == null)
						dbio = new Hashtable();
					if(baseInfoHash == null){
						baseInfoHash = new Hashtable<String, String>();
					}
					if(oracleLockInfo == null){
						oracleLockInfo = new OracleLockInfo();
					}
					if(oracleTopSqlReadWriteVector == null){
						oracleTopSqlReadWriteVector = new Vector<OracleTopSqlReadWrite>();
					}
					if(oracleTopSqlSortVector == null){
						oracleTopSqlSortVector = new Vector<OracleTopSqlSort>();
					}
					oracledata.put("sysValue", sysValue);
					oracledata.put("memValue", memValue);//   nms_oramemvalue
					oracledata.put("memPerfValue", memPerfValue);// nms_oramemperfvalue
					oracledata.put("tableinfo_v", tableinfo);//表空间
					oracledata.put("rollbackinfo_v", rollbackinfo_v);//回滚段信息
					oracledata.put("lockinfo_v", lockinfo_v);//锁信息
					oracledata.put("info_v", info);//session信息
					oracledata.put("table_v", table_v);//表信息
					oracledata.put("sql_v", sql_v);//TOPN的SQL语句信息
					oracledata.put("contrFile_v", contrFile_v);//控制文件 nms_oracontrfile
					oracledata.put("isArchive_h", isArchive_h); // nms_oraisarchive
					oracledata.put("keepObj_v", keepObj_v);// nms_orakeepobj
					oracledata.put("lstrnStatu", lstrnStatu);//监听状态     nms_orastatus
					oracledata.put("extent_v", extent_v);//扩展信息 //nms_oraextent
					oracledata.put("logFile_v", logFile_v);//日志文件  //nms_oralogfile
					oracledata.put("userinfo_h", userinfo_h);//用户信息  //nms_orauserinfo
					oracledata.put("cursors", cursors);//指针信息   //nms_oracursors
					oracledata.put("wait", wait);//等待信息   //nms_orawait
					oracledata.put("dbio", dbio);//IO信息   //nms_oradbio
					oracledata.put("status", "1");//状态信息     nms_orastatus
					oracledata.put("baseInfoHash", baseInfoHash);//基本信息
					oracledata.put("oracleLockInfo", oracleLockInfo);//oracle锁指标等//重庆商行
					oracledata.put("oracleTopSqlReadWriteVector", oracleTopSqlReadWriteVector);
					oracledata.put("oracleTopSqlSortVector", oracleTopSqlSortVector);
					ShareData.setAlloracledata(serverip + ":" + dbmonitorlist.getId(), oracledata);
					
					/*
					 * 这里需要把所有采集数据放到数据库表里,用来支持分布式
					 */
					
					//将表空间数据插入数据库
					/*if(tableinfo != null && tableinfo.size()>0){
						try {
							//将原来实时表里的表空间数据清除
							dbdao.clear_nmsspacedata(hex + ":" + oracle.getId());
						} catch (Exception e) {
							e.printStackTrace();
						}
						for (int k = 0; k < tableinfo.size(); k++) {
							Hashtable ht = (Hashtable) tableinfo.get(k);
							String file_name = ht.get("file_name").toString();
							String tablespace = ht.get("tablespace").toString();
							String size_mb = ht.get("size_mb").toString();
							String free_mb = ht.get("free_mb").toString();
							String percent_free = ht.get("percent_free").toString();
							String status = ht.get("status").toString();
							file_name = file_name.replaceAll("\\\\","/");
							try{
								dbdao.addOracle_nmsoraspaces(hex + ":" + oracle.getId(), tablespace, free_mb, size_mb, percent_free, file_name, status);
							}catch(Exception e){
								e.printStackTrace();
							}
						}
					}
					
					//将系统信息替换到数据库
					if(sysValue != null && sysValue.size()>0){
						try {
							//将原来实时表里的表空间数据清除
							dbdao.clear_nmssys(hex + ":" + oracle.getId());
							dbdao.clear_nmsbanner(hex + ":" + oracle.getId());
						} catch (Exception e) {
							e.printStackTrace();
						}
						String INSTANCE_NAME = sysValue.get("INSTANCE_NAME").toString();
						String HOST_NAME = sysValue.get("HOST_NAME").toString();
						String DBNAME = sysValue.get("DBNAME").toString();
						String VERSION = sysValue.get("VERSION").toString();
						String STARTUP_TIME = sysValue.get("STARTUP_TIME").toString();
						String STATUS = sysValue.get("STATUS").toString();
						String ARCHIVER = sysValue.get("ARCHIVER").toString();
						String java_pool = sysValue.get("java_pool").toString();
						Vector banners = (Vector)sysValue.get("BANNER");	                  	 
						try{
							dbdao.addOracle_nmsorasys(hex + ":" + oracle.getId(), INSTANCE_NAME, HOST_NAME, DBNAME, VERSION, STARTUP_TIME, STATUS, ARCHIVER, "", java_pool);
						}catch(Exception e){
							e.printStackTrace();
						}
						if(banners != null && banners.size()>0){
							for(int i=0;i<banners.size();i++){
								try{
									dbdao.addOracle_nmsorabanner(hex + ":" + oracle.getId(), banners.get(i).toString());
								}catch(Exception e){
									e.printStackTrace();
								}								
							}
						}
					}
					
					//将回滚段数据插入数据库
					if(rollbackinfo_v != null && rollbackinfo_v.size()>0){
						try {
							//将原来实时表里的回滚段数据清除
							dbdao.clear_nmsorarollback(hex + ":" + oracle.getId());
						} catch (Exception e) {
							e.printStackTrace();
						}
						for (int k = 0; k < rollbackinfo_v.size(); k++) {
							Hashtable ht = (Hashtable)rollbackinfo_v.get(k);
							String rollback = ht.get("rollback_segment").toString().trim();
							String wraps = ht.get("wraps").toString();
							String shrink = ht.get("shrinks").toString();
							String ashrink = ht.get("average_shrink").toString();
							String extend = ht.get("extends").toString();
							try{
								dbdao.addOracle_nmsorarollback(hex + ":" + oracle.getId(), rollback, wraps, shrink, ashrink, extend);
							}catch(Exception e){
								e.printStackTrace();
							}
						}
					}
					
					//将锁数据插入数据库
					if(lockinfo_v != null && lockinfo_v.size()>0){
						try {
							//将原来实时表里的锁数据清除
							dbdao.clear_nmsoralock(hex + ":" + oracle.getId());
						} catch (Exception e) {
							e.printStackTrace();
						}
						for (int k = 0; k < lockinfo_v.size(); k++) {
							Hashtable ht = (Hashtable)lockinfo_v.get(k);
							String usernames = ht.get("username").toString().trim();
							String status = ht.get("status").toString().trim();
							String machine = ht.get("machine").toString().trim();
							String sessiontype = ht.get("sessiontype").toString().trim();
							String logontime = ht.get("logontime").toString().trim();
							String program = ht.get("program").toString().trim();
							String locktype = ht.get("locktype").toString().trim();
							String lmode = ht.get("lmode").toString().trim();
							String requeststr = ht.get("request").toString().trim();
							try{
								dbdao.addOracle_nmsoralock(hex + ":" + oracle.getId(), usernames, status, machine, sessiontype, logontime, program, locktype, lmode, requeststr);
							}catch(Exception e){
								e.printStackTrace();
							}
						}
					}
					
					//将表数据插入数据库
					if(table_v != null && table_v.size()>0){
						try {
							//将原来实时表里的表数据清除
							dbdao.clear_nmsoratables(hex + ":" + oracle.getId());
						} catch (Exception e) {
							e.printStackTrace();
						}
						for (int k = 0; k < table_v.size(); k++) {
							Hashtable ht = (Hashtable)table_v.get(k);
							String spaces = ht.get("spaces").toString().trim();
							String segment_name = ht.get("segment_name").toString();
							try{
								dbdao.addOracle_nmsoratables(hex + ":" + oracle.getId(), segment_name, spaces);
							}catch(Exception e){
								e.printStackTrace();
							}
						}
					}
					
					//将TOPSql数据插入数据库
					if(sql_v != null && sql_v.size()>0){
						try {
							//将原来实时表里的TOPSql数据清除
							dbdao.clear_nmsoratopsql(hex + ":" + oracle.getId());
						} catch (Exception e) {
							e.printStackTrace();
						}
						for (int k = 0; k < sql_v.size(); k++) {
							Hashtable ht = (Hashtable)sql_v.get(k);
							String sql_text = ht.get("sql_text").toString();
							String pct_bufgets = ht.get("pct_bufgets").toString();
							String usernames = ht.get("username").toString();
							try{
								dbdao.addOracle_nmsoratopsql(hex + ":" + oracle.getId(), sql_text, pct_bufgets, usernames);
							}catch(Exception e){
								e.printStackTrace();
							}
						}
					}
					
					//将控制文件数据插入数据库
					if(contrFile_v != null && contrFile_v.size()>0){
						try {
							dbdao.clear_nmsoracontrfile(hex + ":" + oracle.getId());
						} catch (Exception e) {
							e.printStackTrace();
						}
						for (int k = 0; k < contrFile_v.size(); k++) {
							Hashtable ht = (Hashtable)contrFile_v.get(k);
							String status = ht.get("status").toString();
							String name = ht.get("name").toString();
							String is_recovery_dest_file = CommonUtil.getValue(ht, "is_recovery_dest_file" ,"--");
							String block_size = CommonUtil.getValue(ht, "block_size" ,"--");
							String file_size_blks = CommonUtil.getValue(ht, "file_size_blks" ,"--");
							name = name.replaceAll("\\\\","/");
							try{
								dbdao.addOracle_nmsoracontrfile(hex + ":" + oracle.getId(), status, name, is_recovery_dest_file,block_size,file_size_blks);
							}catch(Exception e){
								e.printStackTrace();
							}
						}
					}
					
					//将数据库创建信息插入数据库
					if(isArchive_h != null && isArchive_h.size()>0){
						try {
							dbdao.clear_nmsoraisarchive(hex + ":" + oracle.getId());
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						String created = isArchive_h.get("CREATED").toString();
						String log_mode = isArchive_h.get("Log_Mode").toString();
						try{
							dbdao.addOracle_nmsoraisarchive(hex + ":" + oracle.getId(), created, log_mode);
						}catch(Exception e){
							e.printStackTrace();
						}
					}
					
					//将固定缓存对象信息插入数据库 
					if(keepObj_v != null && keepObj_v.size()>0){
						try {
							dbdao.clear_nmsorakeepobj(hex + ":" + oracle.getId());
						} catch (Exception e) {
							e.printStackTrace();
						}
						for (int k = 0; k < keepObj_v.size(); k++) {
							Hashtable ht = (Hashtable)keepObj_v.get(k);
							String owner = ht.get("owner").toString();
							String name = ht.get("name").toString();
							String db_link = ht.get("db_link").toString();
							String namespace = ht.get("namespace").toString();
							String type = ht.get("type").toString();
							String sharable_mem = ht.get("sharable_mem").toString();
							String loads = ht.get("loads").toString();
							String executions = ht.get("executions").toString();
							String locks = ht.get("locks").toString();
							String pins = ht.get("pins").toString();
							String kept = ht.get("kept").toString();
							String child_latch = ht.get("child_latch").toString();
							String invalidations = CommonUtil.getValue(ht, "invalidations", "--");
							try{
								dbdao.addOracle_nmsorakeepobj(hex + ":" + oracle.getId(), owner, name, db_link, namespace, type, sharable_mem, loads, executions, locks, pins, kept, child_latch, invalidations);
							}catch(Exception e){
								e.printStackTrace();
							}
						}
					}
					
					//将扩展信息插入数据库   
					if(extent_v != null && extent_v.size()>0){
						try {
							dbdao.clear_nmsoraextent(hex + ":" + oracle.getId());
						} catch (Exception e) {
							e.printStackTrace();
						}
						for (int k = 0; k < extent_v.size(); k++) {
							Hashtable ht = (Hashtable)extent_v.get(k);
							String tablespace_name = ht.get("tablespace_name").toString();
							String extents = ht.get("sum(a.extents)").toString();
							tablespace_name = tablespace_name.replaceAll("\\\\","/");
							try{
								dbdao.addOracle_nmsoraextent(hex + ":" + oracle.getId(), tablespace_name, extents);
							}catch(Exception e){
								e.printStackTrace();
							}
						}
					}
					
					//将日志文件信息插入数据库   
					if(logFile_v != null && logFile_v.size()>0){
						try {
							dbdao.clear_nmsoralogfile(hex + ":" + oracle.getId());
						} catch (Exception e) {
							e.printStackTrace();
						}
						for (int k = 0; k < logFile_v.size(); k++) {
							Hashtable ht = (Hashtable)logFile_v.get(k);
							String group = ht.get("group#").toString();
							String status = ht.get("status").toString();
							String type = ht.get("type").toString();
							String member = ht.get("member").toString();
							String is_recovery_dest_file = CommonUtil.getValue(ht, "is_recovery_dest_file", "--");
							member = member.replaceAll("\\\\","/");
							try{
								dbdao.addOracle_nmsoralogfile(hex + ":" + oracle.getId(), group, status, type, member);
							}catch(Exception e){
								e.printStackTrace();
							}
						}
					}
					
					//将用户信息插入数据库   
					if(userinfo_h != null && userinfo_h.size()>0){
						try {
							dbdao.clear_nmsorauserinfo(hex + ":" + oracle.getId());
						} catch (Exception e) {
							e.printStackTrace();
						}
						Vector returnVal = null;
						Vector returnVal1 = null;
						Vector returnVal2 = null;
						if(userinfo_h!=null){
						    returnVal = (Vector)userinfo_h.get("returnVal0");
						    returnVal1 = (Vector)userinfo_h.get("returnVal1");
						    returnVal2 = (Vector)userinfo_h.get("returnVal2");
						}
						try {
							//label:0 只插入returnVal的信息  label:1 只插入returnVal1的信息   label:2只插入returnVal2的信息
							String label = "0";
							for(int i=0;i<returnVal.size();i++){
								dbdao.addOracle_nmsorauserinfo(hex + ":" + oracle.getId(),(Hashtable)returnVal.get(i),label);
							}
							label = "1";
							for(int i=0;i<returnVal1.size();i++){
								dbdao.addOracle_nmsorauserinfo(hex + ":" + oracle.getId(),(Hashtable)returnVal1.get(i),label);
							}
							label = "2";
							for(int i=0;i<returnVal2.size();i++){
								dbdao.addOracle_nmsorauserinfo(hex + ":" + oracle.getId(),(Hashtable)returnVal2.get(i),label);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					
					//将指针信息 插入数据库   
					if(cursors != null && cursors.size()>0){
						try {
							dbdao.clear_nmsoracursors(hex + ":" + oracle.getId());
						} catch (Exception e) {
							e.printStackTrace();
						}
						String curconnect = cursors.get("curconnect").toString();
						String opencur = cursors.get("opencur").toString();
						try{
							dbdao.addOracle_nmsoracursors(hex + ":" + oracle.getId(), curconnect ,opencur);
						}catch(Exception e){
							e.printStackTrace();
						}
					}
					
					//将等待信息 插入数据库   
					if(wait != null && wait.size()>0){
						try {
							dbdao.clear_nmsorawait(hex + ":" + oracle.getId());
						} catch (Exception e) {
							e.printStackTrace();
						}
						for (int k = 0; k < wait.size(); k++) {
							Hashtable ht = (Hashtable)wait.get(k);
							String event = ht.get("event").toString();
							String prev = ht.get("prev").toString();
							String curr = ht.get("curr").toString();
							String tot = ht.get("tot").toString();
							try{
								dbdao.addOracle_nmsorawait(hex + ":" + oracle.getId(), event, prev, curr, tot);
							}catch(Exception e){
								e.printStackTrace();
							}
						}
					}
					
					//将IO信息插入数据库     
					if(tableinfo != null && tableinfo.size()>0){
						try {
							dbdao.clear_nmsoradbio(hex + ":" + oracle.getId());
						} catch (Exception e) {
							e.printStackTrace();
						}
						for(int i=0;i<tableinfo.size();i++){
							Hashtable ht = (Hashtable)tableinfo.get(i);
							String filename = ht.get("file_name").toString();
							String tablespace = ht.get("tablespace").toString();
							String size = ht.get("size_mb").toString();
							String free = ht.get("free_mb").toString();
							String percent = ht.get("percent_free").toString();
							String status = ht.get("status").toString();
							String name = "";
							String pyr = "";
							String pbr = "";
							String pyw = "";
							String pbw = "";
							if(dbio.containsKey(filename)){
								Hashtable iodetail = (Hashtable)dbio.get(filename);
								if(iodetail != null && iodetail.size()>0){
									name = (String)iodetail.get("name");
									pyr = (String)iodetail.get("pyr");
									pbr = (String)iodetail.get("pbr");
									pyw = (String)iodetail.get("pyw");
									pbw = (String)iodetail.get("pbw");
								}
								filename = filename.replaceAll("\\\\","/");
							}
							try{
								dbdao.addOracle_nmsoradbio(hex + ":" + oracle.getId(), name, filename, pyr, pbr, pyw, pbw);
							}catch(Exception e){
								e.printStackTrace();
							}
						}
					}
					
					//将memValue信息插入数据库 
					if(memValue != null && memValue.size()>0){
						try {
							dbdao.clear_nmsoramemvalue(hex + ":" + oracle.getId());
						} catch (Exception e) {
							e.printStackTrace();
						}
						try{
							if(memValue != null && memValue.size()>0){
								dbdao.addOracle_nmsoramemvalue(hex + ":" + oracle.getId(),memValue );
							}
						}catch(Exception e){
							e.printStackTrace();
						}
					}
					
					//将memPerfValue信息插入数据库  
					if(memPerfValue != null && memPerfValue.size()>0){
						try {
							dbdao.clear_nmsoramemperfvalue(hex + ":" + oracle.getId());
						} catch (Exception e) {
							e.printStackTrace();
						}
						try{
							if(memPerfValue != null && memPerfValue.size()>0){
								String pctmemorysorts = String.valueOf(memPerfValue.get("pctmemorysorts"));
								String pctbufgets = String.valueOf(memPerfValue.get("pctbufgets"));
								String dictionarycache = String.valueOf(memPerfValue.get("dictionarycache"));
								String buffercache = String.valueOf(memPerfValue.get("buffercache"));
								String librarycache = String.valueOf(memPerfValue.get("librarycache"));
								dbdao.addOracle_nmsoramemperfvalue(hex + ":" + oracle.getId(),pctmemorysorts,pctbufgets,dictionarycache,buffercache,librarycache);
							}
						}catch(Exception e){
							e.printStackTrace();
						}
					}
					
					//将status和lstrnStatu状态信息插入数据库   
					if(lstrnStatu != null && oracledata.get("status") != null){
						try {
							dbdao.clear_nmsorastatus(hex + ":" + oracle.getId());
						} catch (Exception e) {
							e.printStackTrace();
						}
						try{
							dbdao.addOracle_nmsorastatus(hex + ":" + oracle.getId(),lstrnStatu,String.valueOf(oracledata.get("status")));
						}catch(Exception e){
							e.printStackTrace();
						}
					}
					*/
					
					//批量将采集的oracle数据插入数据库
					Date startDate = new Date();
					dbdao.processOracleData(ShareData.getAlloracledata());
					Date endDate = new Date();
					SysLogger.info("###################---sid为"+dbmonitorlist.getId()+"的oracle入库耗时(ms)："+(endDate.getTime()-startDate.getTime()));
					
					//结束将采集数据放到数据库表里
					// }
					/*-----------modify  end  ------------------------------*/

					Vector tableinfo_v = null;
					Hashtable datas=oracledata;
					try {
						tableinfo_v = (Vector) datas.get("tableinfo_v");
					} catch (Exception e) {
						e.printStackTrace();
					}
					//对表空间进行告警验证操作
//					if (tableinfo_v != null && tableinfo_v.size() > 0) {}
					if (tableinfo_v != null && tableinfo_v.size() > 0)
						ShareData.setOraspacedata(serverip + ":" + dbmonitorlist.getId(), tableinfo_v);
					
					//数据库的SESSION入历史表里
					Vector info_v = null;
					info_v = (Vector) datas.get("info_v");
					if(info_v != null && info_v.size()>0){
						//将原来实时表里的SESSION数据清除
						try{
							dbdao.clear_nmssessiondata(IpTranslation.formIpToHex(serverip)+":" + dbmonitorlist.getId());
						}catch(Exception e){
							
						}
					}
					for (int j = 0; j < info_v.size(); j++) {
						Oracle_sessiondata os = new Oracle_sessiondata();
						Hashtable ht = (Hashtable) info_v.get(j);
						String machine = ht.get("machine").toString();
						String usernames = ht.get("username").toString();
						String program = ht.get("program").toString();
						String status = ht.get("status").toString();
						String sessiontype = ht.get("sessiontype").toString();
						String command = ht.get("command").toString();
						String logontime = ht.get("logontime").toString();
						os.setCommand(command);
						os.setLogontime(sdf1.parse(logontime));
						os.setMachine(machine);
						Calendar _tempCal = Calendar.getInstance();
						Date _cc = _tempCal.getTime();
						os.setMon_time(_cc);
						os.setProgram(program);
						os.setSessiontype(sessiontype);
						os.setStatus(status);
						os.setUsername(usernames);
						//IpTranslation tranfer = new IpTranslation();
						//String hex = tranfer.formIpToHex(serverip);
						os.setServerip(hex + ":" + dbmonitorlist.getId());
						os.setDbname(dbnames);
						try {
							//存入历史表
							dbdao.addOracle_sessiondata(os);
							//存入实时表
							dbdao.addOracle_nmssessiondata(os);
						} catch (Exception e) {
							e.printStackTrace();
						}
				}
			}
				
				String status = "0";
				String pingvalue = "0";
				if(flag == 0){
					status = "1";
					pingvalue = "100";
				}else {
					dbdao.updateNmsValueByUniquekeyAndTablenameAndKey("nms_orastatus", "serverip", hex + ":" + dbmonitorlist.getId(), "status", status);
				}
				if(oracledata == null){
					oracledata = new Hashtable();
				}
				oracledata.put("ping", pingvalue);
				//allFlag == 1
				if(oracledata!=null){
				    ShareData.setAlloracledata(serverip + ":" + dbmonitorlist.getId(), oracledata);
				    NodeUtil nodeUtil = new NodeUtil();
				    NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(dbmonitorlist);
				    updateData(nodeDTO, oracledata);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(dbdao!=null)
			  dbdao.close();
			SysLogger.info("*******OracleTask运行完毕---------- " );
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
//	public void createSMS(String db, DBVo dbmonitorlist) {
//		// 建立短信
//		// 从内存里获得当前这个IP的PING的值
//		Calendar date = Calendar.getInstance();
//		// for (OracleEntity oracle : oracles) {
//		try {
//			if (!sendeddata.containsKey(db + ":" + dbmonitorlist.getIpAddress() + ":" + dbmonitorlist.getDbName())) {
//				// 若不在，则建立短信，并且添加到发送列表里
//				Smscontent smscontent = new Smscontent();
//				String time = sdf.format(date.getTime());
//				smscontent.setLevel("2");
//				smscontent.setObjid(dbmonitorlist.getId()+"");
//				smscontent.setMessage(db + "(" + dbmonitorlist.getDbName() + " IP:" + dbmonitorlist.getIpAddress() + ")" + "的数据库服务停止");
//				smscontent.setRecordtime(time);
//				smscontent.setSubtype("db");
//				smscontent.setSubentity("ping");
//				smscontent.setIp(dbmonitorlist.getIpAddress() + ":" + dbmonitorlist.getDbName());
//				// 发送短信
//				SmscontentDao smsmanager = new SmscontentDao();
//				try {
//					smsmanager.sendDatabaseSmscontent(smscontent);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				sendeddata.put(db + ":" + dbmonitorlist.getIpAddress() + ":" + dbmonitorlist.getDbName(), date);
//			} else {
//				// 若在，则从已发送短信列表里判断是否已经发送当天的短信
//				Calendar formerdate = (Calendar) sendeddata.get(db + ":" + dbmonitorlist.getIpAddress() + ":" + dbmonitorlist.getDbName());
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
//					smscontent.setLevel("2");
//					smscontent.setObjid(dbmonitorlist.getId() + "");
//					smscontent.setMessage(db + "(" + dbmonitorlist.getDbName() + " IP:" + dbmonitorlist.getIpAddress() + ")"
//							+ "的数据库服务停止");
//					smscontent.setRecordtime(time);
//					smscontent.setSubtype("db");
//					smscontent.setSubentity("ping");
//					smscontent.setIp(dbmonitorlist.getIpAddress() + ":" + dbmonitorlist.getDbName());
//					// smscontent.setMessage("db&"+time+"&"+dbmonitorlist.getId()+"&"+db+"("+dbmonitorlist.getDbName()+"
//					// IP:"+dbmonitorlist.getIpAddress()+")"+"的数据库服务停止");
//					// 发送短信
//					SmscontentDao smsmanager = new SmscontentDao();
//					try {
//						smsmanager.sendDatabaseSmscontent(smscontent);
//					} catch (Throwable e) {
//
//					}
//					// 修改已经发送的短信记录
//					sendeddata.put(db + ":" + dbmonitorlist.getIpAddress() + ":" + dbmonitorlist.getDbName(), date);
//				}else{
//					String eventdesc = "ORACLE(" + " IP:" + dbmonitorlist.getIpAddress() + ")"
//					+ "的数据库服务停止";
//					SmscontentDao  eventdao=new SmscontentDao ();
//			       eventdao.createEventWithReasion("poll", dbmonitorlist.getId() + ":"
//					+ dbmonitorlist.getId(), dbmonitorlist.getAlias() + "("
//					+ dbmonitorlist.getIpAddress() + ":" + dbmonitorlist.getDbName() + ")",
//					eventdesc, 2, "db", "ping", "所在的服务器连接不上");
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		// }
//
//	}
//	public static void createSpaceSMS(DBVo dbmonitorlist, Oraspaceconfig oraspaceconfig) {
//		// 建立短信
//		// 从内存里获得当前这个IP的PING的值
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		SmscontentDao smsmanager = new SmscontentDao();
//		AlarmInfoDao alarminfomanager = new AlarmInfoDao();
//
//		String ipaddress = dbmonitorlist.getIpAddress();
//		Hashtable sendeddata = ShareData.getSendeddata();
//		Calendar date = Calendar.getInstance();
//		String time = sdf.format(date.getTime());
//		// String
//		// errorcontent="oraspace&"+time+"&"+dbmonitorlist.getId()+"&"+oraspaceconfig.getSpacename()+"("+dbmonitorlist.getDbName()+"
//		// IP:"+dbmonitorlist.getIpAddress()+")"+"的表空间超过阀值";
//		try {
//			if (!sendeddata.containsKey(ipaddress + ":" + dbmonitorlist.getId() + ":" + oraspaceconfig.getSpacename())) {
//				// 若不在，则建立短信，并且添加到发送列表里
//				Smscontent smscontent = new Smscontent();
//				smscontent.setLevel("2");
//				smscontent.setObjid(dbmonitorlist.getId() + ":oracle" + dbmonitorlist.getId());
//				smscontent.setMessage(dbmonitorlist.getIpAddress() + ":" + dbmonitorlist.getDbName() + "的数据库的"
//						+ oraspaceconfig.getSpacename() + "表空间超过" + oraspaceconfig.getAlarmvalue() + "%的阀值");
//				smscontent.setRecordtime(time);
//				smscontent.setSubtype("db");
//				smscontent.setSubentity("oraspace");
//				smscontent.setIp(dbmonitorlist.getIpAddress() + ":" + dbmonitorlist.getDbName());
//				// smscontent.setMessage(errorcontent);
//				// 发送短信
//				try {
//					smsmanager.sendDatabaseSmscontent(smscontent);
//				} catch (Exception e) {
//
//				}
//				sendeddata.put(ipaddress + ":" + dbmonitorlist.getId() + ":" + oraspaceconfig.getSpacename(), date);
//			} else {
//				// 若在，则从已发送短信列表里判断是否已经发送当天的短信
//				Calendar formerdate = (Calendar) sendeddata.get(ipaddress + ":" + dbmonitorlist.getId() + ":"
//						+ oraspaceconfig.getSpacename());
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
//					smscontent.setLevel("2");
//					smscontent.setObjid(dbmonitorlist.getId() + ":oracle" + dbmonitorlist.getId());
//					smscontent.setMessage(dbmonitorlist.getIpAddress() + "的数据库的" + oraspaceconfig.getSpacename() + "表空间超过"
//							+ oraspaceconfig.getAlarmvalue() + "%的阀值");
//					smscontent.setRecordtime(time);
//					smscontent.setSubtype("db");
//					smscontent.setSubentity("oraspace");
//					smscontent.setIp(dbmonitorlist.getIpAddress() + ":" + dbmonitorlist.getId());
//					// 发送短信
//					try {
//						smsmanager.sendDatabaseSmscontent(smscontent);
//					} catch (Exception e) {
//
//					}
//					sendeddata.put(ipaddress + ":" + dbmonitorlist.getId() + ":" + oraspaceconfig.getSpacename(), date);
//				} else {
//					// 则写声音告警数据
//					// 向声音告警表里写数据
//				
//					AlarmInfo alarminfo = new AlarmInfo();
//					alarminfo.setContent(dbmonitorlist.getIpAddress() + "的数据库的" + oraspaceconfig.getSpacename() + "表空间超过"
//							+ oraspaceconfig.getAlarmvalue() + "%的阀值");
//					alarminfo.setIpaddress(ipaddress);
//					alarminfo.setLevel1(new Integer(2));
//					alarminfo.setRecordtime(Calendar.getInstance());
//					alarminfomanager.save(alarminfo);
////					SmscontentDao content=new SmscontentDao();
////					String message=dbmonitorlist.getIpAddress() + ":" + oracle.getSid() + "的数据库的"
////					+ oraspaceconfig.getSpacename() + "表空间超过" + oraspaceconfig.getAlarmvalue() + "%的阀值";
////					content.createEventWithReasion("poll",dbmonitorlist.getId()+":"+oracle.getId(),dbmonitorlist.getAlias()+ "(" + dbmonitorlist.getIpAddress() + ")", message,
////							2, "db", "oraspace", "表空间超过阀值");
//					/*----------------------------------*/
//					//createEventWithReasion
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			alarminfomanager.close();
//		}
//	}
//	private void checkEvent(String nodeid,String alarmname,BaseVo baseVo,String value,String sIndex,int alarm,int alarmvalue){
//		SysLogger.info("oracle%%%%%%%%%%%%%%db%%%%%%%%%%%%%%%%%%%%%%%"+nodeid+"--"+alarmname+"--"+value+"--"+alarm);
//		AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
//		List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(nodeid,"db", "oracle",alarmname);
//		if(list!=null&&list.size()>0){
//			AlarmIndicatorsNode _alarmIndicatorsNode = (AlarmIndicatorsNode) list.get(0);
//			if(alarmvalue!=0){
//				_alarmIndicatorsNode.setLimenvalue0(alarmvalue+"");
//				_alarmIndicatorsNode.setLimenvalue1(alarmvalue+"");
//			    _alarmIndicatorsNode.setLimenvalue2(alarmvalue+"");
//			}
//			if ("1".equals(_alarmIndicatorsNode.getEnabled())) {
//				CheckEventUtil checkeventutil = new CheckEventUtil();
//				checkeventutil.checkEvent(baseVo,_alarmIndicatorsNode, value,sIndex,alarm);
//			}
//		}
//	}
	
	public void updateData(NodeDTO nodeDTO, Object collectingData){
		if(nodeDTO == null || collectingData == null){
			return ;
		}
		Hashtable datahashtable = (Hashtable)collectingData;
		
		Hashtable memeryHashtable = (Hashtable)datahashtable.get("memPerfValue");
		if(memeryHashtable == null)memeryHashtable = new Hashtable();
		
		Hashtable cursorsHashtable = (Hashtable)datahashtable.get("cursors");
		if(cursorsHashtable == null)cursorsHashtable = new Hashtable();
		
		Vector tableinfo_v = (Vector) datahashtable.get("tableinfo_v");
		
		String pingvalue = (String)datahashtable.get("ping");
		AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
    	List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(nodeDTO.getId()+"", nodeDTO.getType(), nodeDTO.getSubtype());
		CheckEventUtil checkEventUtil = new CheckEventUtil();
    	for(int i = 0 ; i < list.size(); i++){
    		try {
				AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode)list.get(i);
				if("ping".equalsIgnoreCase(alarmIndicatorsNode.getName())){
					if(pingvalue!=null){
						checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, pingvalue);
					}
				} else if ("opencur".equalsIgnoreCase(alarmIndicatorsNode.getName())){
					if(cursorsHashtable!=null&&cursorsHashtable.get("opencur")!=null){
						checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, (String)cursorsHashtable.get("opencur"));
					}
				} else if ("buffercache".equalsIgnoreCase(alarmIndicatorsNode.getName())){
					if(memeryHashtable!=null&&memeryHashtable.get("buffercache")!=null){
						checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, (String)memeryHashtable.get("buffercache"));
					}
				} else if ("dictionarycache".equalsIgnoreCase(alarmIndicatorsNode.getName())){
					if(memeryHashtable!=null&&memeryHashtable.get("dictionarycache")!=null){
						checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, (String)memeryHashtable.get("dictionarycache"));
					}
				} else if ("pctmemorysorts".equalsIgnoreCase(alarmIndicatorsNode.getName())){
					if(memeryHashtable!=null&&memeryHashtable.get("pctmemorysorts")!=null){
						checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, (String)memeryHashtable.get("pctmemorysorts"));
					}
				} else if ("pctbufgets".equalsIgnoreCase(alarmIndicatorsNode.getName())){
					if(memeryHashtable!=null&&memeryHashtable.get("pctbufgets")!=null){
						checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, (String)memeryHashtable.get("pctbufgets"));
					}
				} else if ("tablespace".equalsIgnoreCase(alarmIndicatorsNode.getName())){
					if(tableinfo_v!=null&&tableinfo_v.size()>0){
						OraspaceconfigDao oraspaceconfigManager = new OraspaceconfigDao();
						Hashtable oraspaces=null;
						try {
							oraspaces = oraspaceconfigManager.getByAlarmflag(1);
						} catch (Exception e1) {
							e1.printStackTrace();
						} finally {
							oraspaceconfigManager.close();
						}
						Vector spaces = new Vector();
						for (int k = 0; k < tableinfo_v.size(); k++) {
							Hashtable ht = (Hashtable) tableinfo_v.get(k);
							String tablespace = ht.get("tablespace").toString();
							if (spaces.contains(tablespace))
								continue;
							spaces.add(tablespace);
							String percent = ht.get("percent_free").toString();
							if (oraspaces!=null&&oraspaces.containsKey(nodeDTO.getIpaddress() + ":" + nodeDTO.getId() + ":" + tablespace)) {
								// 存在需要告警的表空间
								Integer free = 0;
								try {
									free = new Float(percent).intValue();
								} catch (Exception e) {
									e.printStackTrace();
								}
								// 依据表空间告警配置判断是否告警
								Oraspaceconfig oraspaceconfig = (Oraspaceconfig) oraspaces.get(nodeDTO.getIpaddress() + ":" + nodeDTO.getId() + ":" + tablespace);
								alarmIndicatorsNode.setLimenvalue0(oraspaceconfig.getAlarmvalue() + "");
								alarmIndicatorsNode.setLimenvalue1(oraspaceconfig.getAlarmvalue() + "");
								alarmIndicatorsNode.setLimenvalue2(oraspaceconfig.getAlarmvalue() + "");
								checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, (100 - free)+"",tablespace);
							}
						}
						
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
	}
	
//	public void updateData(Object vo , Object collectingData){
//		if(vo == null || collectingData == null){
//			return ;
//		}
//		try {
//			
//			DBVo dbmonitorlist = (DBVo)vo;
//			
//			Hashtable datahashtable = (Hashtable)collectingData;
//			
//			Hashtable memeryHashtable = (Hashtable)datahashtable.get("memPerfValue");
//			if(memeryHashtable == null)memeryHashtable = new Hashtable();
//			
//			Hashtable cursorsHashtable = (Hashtable)datahashtable.get("cursors");
//			if(cursorsHashtable == null)cursorsHashtable = new Hashtable();
//			
//			String pingvalue = (String)datahashtable.get("ping");
//			AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
//			
//			List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(dbmonitorlist.getId()), AlarmConstant.TYPE_DB, "oracle");
//			
//			CheckEventUtil checkEventUtil = new CheckEventUtil();
//			
//			for(int i = 0 ; i < list.size() ; i ++){
//				AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode)list.get(i);
//				if("1".equals(alarmIndicatorsNode.getEnabled())){
//					String indicators = alarmIndicatorsNode.getName();
//					
//					String value = "";
//					
//					if("buffercache".equals(indicators)){
//						value = (String)memeryHashtable.get(indicators);
//					}else if("dictionarycache".equals(indicators)){
//						value = (String)memeryHashtable.get(indicators);
//					}else if("pctmemorysorts".equals(indicators)){
//						value = (String)memeryHashtable.get(indicators);
//					}else if("pctbufgets".equals(indicators)){
//						value = (String)memeryHashtable.get(indicators);
//					}else if("ping".equals(indicators)){
//						value = pingvalue;
//					}else if("opencur".equals(indicators)){
//						value = (String)cursorsHashtable.get(indicators);
//					}else {
//						continue;
//					}
//					if(value == null)continue;
//					if( AlarmConstant.DATATYPE_NUMBER.equals(alarmIndicatorsNode.getDatatype())){
//						
//						try {
//							double value_int = Double.valueOf(value);
//							double Limenvalue2 = Double.valueOf(alarmIndicatorsNode.getLimenvalue2());
//							double Limenvalue1 = Double.valueOf(alarmIndicatorsNode.getLimenvalue1());
//							double Limenvalue0 = Double.valueOf(alarmIndicatorsNode.getLimenvalue0());
//							
//							String level = "";
//							String alarmTimes = "";
//							// 是否超过阀值
//							boolean result = true;
//							if(alarmIndicatorsNode.getCompare()==0){
//								//降序比较
//								if(value_int <= Limenvalue2){
//									level = "3";
//									alarmTimes = alarmIndicatorsNode.getTime2();
//								}else if(value_int <= Limenvalue1){
//									level = "2";
//									alarmTimes = alarmIndicatorsNode.getTime1();
//								}else if(value_int <= Limenvalue0){
//									level = "1";
//									alarmTimes = alarmIndicatorsNode.getTime0();
//								}else{
//									result = false;
////									continue;
//								}
//							}else{
//								//升序比较
//								if(value_int > Limenvalue2){
//									level = "3";
//									alarmTimes = alarmIndicatorsNode.getTime2();
//								}else if(value_int > Limenvalue1){
//									level = "2";
//									alarmTimes = alarmIndicatorsNode.getTime1();
//								}else if(value_int > Limenvalue0){
//									level = "1";
//									alarmTimes = alarmIndicatorsNode.getTime0();
//								}else{
//									result = false;
////									continue;
//								}
//							}
//							String num = (String)AlarmResourceCenter.getInstance().getAttribute(String.valueOf(alarmIndicatorsNode.getId()));
//							if(num == null || "".equals(num)){
//								num = "0";
//							}
//							if(!result){
//								// 未超过告警阀值 则删除告警发送的时间的记录
//								String name = dbmonitorlist.getId()+":"+alarmIndicatorsNode.getType()+":"+alarmIndicatorsNode.getSubtype()+":"+alarmIndicatorsNode.getName();
//								SendAlarmTimeDao sendAlarmTimeDao = new SendAlarmTimeDao();
//								try {
//									sendAlarmTimeDao.delete(name);
//									//删除node_indicator_alarm中的告警记录
//									NodeAlarmUtil nodeAlarmUtil = new NodeAlarmUtil();
//									nodeAlarmUtil.deleteByDeviceIdAndDeviceTypeAndIndicatorName(dbmonitorlist.getId()+"", alarmIndicatorsNode.getType(), alarmIndicatorsNode.getName());
//								} catch (Exception e) {
//									e.printStackTrace();
//								} finally {
//									sendAlarmTimeDao.close();
//								}
//								// 如果此时未发生告警 则将 次数设置为 0 次
//								num = "0";
//								AlarmResourceCenter.getInstance().setAttribute(String.valueOf(alarmIndicatorsNode.getId()), num);
//								//当前指标无告警，首先判断内存中是否有当前指标的告警信息，若有则清除告警信息，若无则不做任何处理
//								Hashtable checkEventHash = ShareData.getCheckEventHash();
//								if(checkEventHash != null && checkEventHash.size()>0){
//									if(checkEventHash.containsKey(name)){
//										//保存告警已恢复的事件信息
//										Node nodeVo=PollingEngine.getInstance().getNodeByID(dbmonitorlist.getId());
//									    EventList eventList = checkEventUtil.createEvent(alarmIndicatorsNode, nodeVo, value, name);
//										EventListDao eventListDao = new EventListDao(); 
//										try {
//											eventListDao.save(eventList);
//										} catch (Exception e) {
//											e.printStackTrace();
//										} finally {
//											eventListDao.close();
//										}
//										//删除checkEvent告警信息
//										checkEventUtil.deleteEvent(dbmonitorlist.getId()+"",alarmIndicatorsNode.getType(),alarmIndicatorsNode.getSubtype(),alarmIndicatorsNode.getName(),null);
//									}
//								}
//								return;
//							}
//							int num_int = Integer.valueOf(num);//当前告警次数
//							int alarmTimes_int = Integer.valueOf(alarmTimes);//允许的告警次数
//							if(num_int+1 >= alarmTimes_int){
//								// 告警
//								DBNode dbnode = (DBNode) PollingEngine.getInstance().getDbByID(dbmonitorlist.getId());
//								dbnode.setAlarm(true);
//								List alarmList = dbnode.getAlarmMessage();
//								if (alarmList == null){
//									alarmList = new ArrayList();
//								}
//								dbnode.getAlarmMessage().add(alarmIndicatorsNode.getAlarm_info() + " 当前值为：" + value +  alarmIndicatorsNode.getThreshlod_unit());
//								//若大于之前的告警级别,则用最大级别
//								if(Integer.valueOf(level)> dbnode.getStatus()){
//									dbnode.setStatus(Integer.valueOf(level));
//								}
//								
////								createSMS(alarmIndicatorsNode.getType(), alarmIndicatorsNode.getSubtype(), oracle.getAlias() , oracle.getId() + "", alarmIndicatorsNode.getAlarm_info() + " 当前值为：" + value +  alarmIndicatorsNode.getThreshlod_unit() , Integer.valueOf(level) , 1 , oracle.getAlias() , oracle.getBid(),oracle.getAlias() + "(" + oracle.getAlias() + ")");
//								NodeUtil nodeUtil = new NodeUtil();
//								NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(dbmonitorlist);
//								checkEventUtil.sendAlarm(nodeDTO, alarmIndicatorsNode, value, Integer.valueOf(level),0);
//							}else{
//								num_int = num_int + 1;
//								AlarmResourceCenter.getInstance().setAttribute(String.valueOf(alarmIndicatorsNode.getId()), String.valueOf(num_int));
//							}
//							
//						} catch (Exception e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					}
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	public void createSMS(String subtype,String subentity,String ipaddress,String objid,String content,int flag,int checkday,String sIndex,String bids,String sysLocation){
//	 	//建立短信		 	
//	 	//从内存里获得当前这个IP的PING的值
//	 	Calendar date=Calendar.getInstance();
//	 	Hashtable sendeddata = ShareData.getSendeddata();
//	 	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//	 	try{
// 			if (!sendeddata.containsKey(subtype+":"+subentity+":"+ipaddress+":"+sIndex)) {
// 				//若不在，则建立短信，并且添加到发送列表里
//	 			Smscontent smscontent = new Smscontent();
//	 			String time = sdf.format(date.getTime());
//	 			smscontent.setLevel(flag+"");
//	 			smscontent.setObjid(objid);
//	 			smscontent.setMessage(content);
//	 			smscontent.setRecordtime(time);
//	 			smscontent.setSubtype(subtype);
//	 			smscontent.setSubentity(subentity);
//	 			smscontent.setIp(ipaddress);
//	 			//发送短信
//	 			SmscontentDao smsmanager=new SmscontentDao();
//	 			smsmanager.sendURLSmscontent(smscontent);	
//				sendeddata.put(subtype+":"+subentity+":"+ipaddress+":"+sIndex,date);	
//				
// 			} else {
// 				//若在，则从已发送短信列表里判断是否已经发送当天的短信
// 				SmsDao smsDao = new SmsDao();
// 				List list = new ArrayList();
// 				String startTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " 00:00:00";
// 				String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
// 				try {
// 					list = smsDao.findByEvent(content,startTime,endTime);
//				} catch (RuntimeException e) {
//					e.printStackTrace();
//				} finally {
//					smsDao.close();
//				}
//				if(list!=null&&list.size()>0){//短信列表里已经发送当天的短信
//					Calendar formerdate =(Calendar)sendeddata.get(subtype+":"+subentity+":"+ipaddress+":"+sIndex);		 				
//		 			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//		 			Date last = null;
//		 			Date current = null;
//		 			Calendar sendcalen = formerdate;
//		 			Date cc = sendcalen.getTime();
//		 			String tempsenddate = formatter.format(cc);
//		 			
//		 			Calendar currentcalen = date;
//		 			Date ccc = currentcalen.getTime();
//		 			last = formatter.parse(tempsenddate);
//		 			String currentsenddate = formatter.format(ccc);
//		 			current = formatter.parse(currentsenddate);
//		 			
//		 			long subvalue = current.getTime()-last.getTime();	
//		 			if(checkday == 1){
//		 				//检查是否设置了当天发送限制,1为检查,0为不检查
//		 				if (subvalue/(1000*60*60*24)>=1){
//			 				//超过一天，则再发信息
//				 			Smscontent smscontent = new Smscontent();
//				 			String time = sdf.format(date.getTime());
//				 			smscontent.setLevel(flag+"");
//				 			smscontent.setObjid(objid);
//				 			smscontent.setMessage(content);
//				 			smscontent.setRecordtime(time);
//				 			smscontent.setSubtype(subtype);
//				 			smscontent.setSubentity(subentity);
//				 			smscontent.setIp(ipaddress);//发送短信
//				 			SmscontentDao smsmanager=new SmscontentDao();
//				 			smsmanager.sendURLSmscontent(smscontent);
//							//修改已经发送的短信记录	
//							sendeddata.put(subtype+":"+subentity+":"+ipaddress+":"+sIndex,date);
//				 		} else {
//	                        //开始写事件
//			 	            //String sysLocation = "";
//			 				createEvent("poll",sysLocation,bids,content,flag,subtype,subentity,ipaddress,objid);
//				 		}
//		 			}
//				} else {
// 					Smscontent smscontent = new Smscontent();
// 		 			String time = sdf.format(date.getTime());
// 		 			smscontent.setLevel(flag+"");
// 		 			smscontent.setObjid(objid);
// 		 			smscontent.setMessage(content);
// 		 			smscontent.setRecordtime(time);
// 		 			smscontent.setSubtype(subtype);
// 		 			smscontent.setSubentity(subentity);
// 		 			smscontent.setIp(ipaddress);
// 		 			//发送短信
// 		 			SmscontentDao smsmanager=new SmscontentDao();
// 		 			smsmanager.sendURLSmscontent(smscontent);	
// 					sendeddata.put(subtype+":"+subentity+":"+ipaddress+":"+sIndex,date);
// 				}
// 				
// 			}	 			 			 			 			 	
//	 	}catch(Exception e){
//	 		e.printStackTrace();
//	 	}
//	 }
	
//	private void createEvent(String eventtype,String eventlocation,String bid,String content,int level1,String subtype,String subentity,String ipaddress,String objid){
//		//生成事件
//		SysLogger.info("##############开始生成事件############");
//		EventList eventlist = new EventList();
//		eventlist.setEventtype(eventtype);
//		eventlist.setEventlocation(eventlocation);
//		eventlist.setContent(content);
//		eventlist.setLevel1(level1);
//		eventlist.setManagesign(0);
//		eventlist.setBak("");
//		eventlist.setRecordtime(Calendar.getInstance());
//		eventlist.setReportman("系统轮询");
//		eventlist.setBusinessid(bid);
//		eventlist.setNodeid(Integer.parseInt(objid));
//		eventlist.setOid(0);
//		eventlist.setSubtype(subtype);
//		eventlist.setSubentity(subentity);
//		EventListDao eventlistdao = new EventListDao();
//		try{
//			eventlistdao.save(eventlist);
//		}catch(Exception e){
//			e.printStackTrace();
//		}finally{
//			eventlistdao.close();
//		}
//	}
	
	/**
	 * 清空指定serverip的Oracle数据临时表的数据
	 * @param dbdao
	 * @param serverip
	 */
//	private void clearOracleNmsTableData(DBDao dbdao, String serverip) {
//		dbdao.clearTableData("nms_oracontrfile", serverip);
//		dbdao.clearTableData("nms_oracursors", serverip);
//		dbdao.clearTableData("nms_oracursors", serverip);
//		dbdao.clearTableData("nms_oradbio", serverip);
//		dbdao.clearTableData("nms_oraextent", serverip);
//		dbdao.clearTableData("nms_oraisarchive", serverip);
//		dbdao.clearTableData("nms_orakeepobj", serverip);
//		dbdao.clearTableData("nms_oralock", serverip);
//		dbdao.clearTableData("nms_oralogfile", serverip);
//		dbdao.clearTableData("nms_oramemperfvalue", serverip);
//		dbdao.clearTableData("nms_oramemvalue", serverip);
//		dbdao.clearTableData("nms_orarollback", serverip);
//		dbdao.clearTableData("nms_orasessiondata", serverip);
//		dbdao.clearTableData("nms_oraspaces", serverip);
//		dbdao.clearTableData("nms_orastatus", serverip);
//		dbdao.clearTableData("nms_orasys", serverip);
//		dbdao.clearTableData("nms_oratables", serverip);
//		dbdao.clearTableData("nms_oratopsql", serverip);
//		dbdao.clearTableData("nms_orauserinfo", serverip);
//		dbdao.clearTableData("nms_orawait", serverip);
//	}
}
