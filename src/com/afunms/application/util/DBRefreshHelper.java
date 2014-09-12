/**
 * <p>Description:tomcat util</p>
 * <p>Company: afunms</p>
 * @author miiwill
 * @project afunms
 * @date 2006-12-06
 */

package com.afunms.application.util;

import java.io.File;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.apache.commons.beanutils.BeanUtils;
import org.jdom.input.SAXBuilder;
import org.jdom.Document;
import org.jdom.Element;

import com.afunms.application.dao.DBDao;
import com.afunms.application.dao.DBTypeDao;
import com.afunms.application.dao.Db2spaceconfigDao;
import com.afunms.application.dao.InformixspaceconfigDao;
import com.afunms.application.dao.OraclePartsDao;
import com.afunms.application.dao.OraspaceconfigDao;
import com.afunms.application.dao.SqldbconfigDao;
import com.afunms.application.dao.SybspaceconfigDao;
import com.afunms.application.model.DBTypeVo;
import com.afunms.application.model.DBVo;
import com.afunms.application.model.Db2spaceconfig;
import com.afunms.application.model.Informixspaceconfig;
import com.afunms.application.model.OracleEntity;
import com.afunms.application.model.Oracle_sessiondata;
import com.afunms.application.model.Oraspaceconfig;
import com.afunms.application.model.Sqldbconfig;
import com.afunms.application.model.Sqlserver_processdata;
import com.afunms.application.model.SybaseVO;
import com.afunms.application.model.Sybspaceconfig;
import com.afunms.application.model.TablesVO;
import com.afunms.common.util.*;
import com.afunms.event.dao.AlarmInfoDao;
import com.afunms.event.dao.SmscontentDao;
import com.afunms.event.model.AlarmInfo;
import com.afunms.event.model.Smscontent;
import com.afunms.indicators.dao.NodeGatherIndicatorsDao;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.inform.dao.NewDataDao;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.DBNode;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.Pingcollectdata;
import com.afunms.polling.snmp.LoadDB2File;
import com.afunms.polling.snmp.LoadInformixFile;
import com.afunms.polling.snmp.LoadMySqlFile;
import com.afunms.polling.snmp.LoadOracleFile;
import com.afunms.polling.snmp.LoadSQLServerFile;
import com.afunms.polling.snmp.LoadSysbaseFile;
import com.afunms.polling.snmp.db.DB2DataCollector;
import com.afunms.polling.snmp.db.InformixDataCollector;
import com.afunms.polling.snmp.db.MySqlDataCollector;
import com.afunms.polling.snmp.db.OracleDataCollector;
import com.afunms.polling.snmp.db.SQLServerDataCollector;
import com.afunms.polling.snmp.db.SybaseDataCollector;
import com.afunms.report.jfree.ChartCreator;
import com.afunms.system.util.TimeGratherConfigUtil;

public class DBRefreshHelper
{
    private Element root;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
    private Hashtable sendeddata = ShareData.getSendeddata();
    public DBRefreshHelper()
    {
    } 
    
    /**
	 * nielin add 2010-08-05
	 */
	public void execute(DBVo vo){
		//System.out.println("=====================开始采集");
		DBDao dbdao = null;
		try {

			List mslist = null;
			
			List oclist = null;
			
			List sysbaselist = null;
			
			List informixlist = null;
			
			List db2list = null;

			List mysqllist = null;
			
			if(vo != null){
				dbdao = new DBDao();
				try {
					//vo = (DBVo)dbdao.findByID(String.valueOf(vo.getId()));
					String password = EncryptUtil.decode(vo.getPassword());
					vo.setPassword(password);
					
				} catch (RuntimeException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}finally{
					dbdao.close();
				}
				if(vo != null ){
					DBTypeDao typeDao = new DBTypeDao();
					DBTypeVo type = null;
					try {
						type = (DBTypeVo)typeDao.findByID(String.valueOf(vo.getDbtype()));
					} catch (RuntimeException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}finally{
						typeDao.close();
					}
					//SysLogger.info("type================="+type.getDbtype());
					if("MySql".equals(type.getDbtype())){
						mysqllist = new ArrayList();
						mysqllist.add(vo);
					}else if("SQLServer".equals(type.getDbtype())){
						mslist = new ArrayList();
						mslist.add(vo);
					}
					else if("Oracle".equals(type.getDbtype())){
						oclist = new ArrayList();
						oclist.add(vo);
					}
					else if("Sybase".equals(type.getDbtype())){
						sysbaselist = new ArrayList();
						sysbaselist.add(vo);
					}
					else if("Informix".equals(type.getDbtype())){
						informixlist = new ArrayList();
						informixlist.add(vo);
					}
					else if("DB2".equals(type.getDbtype())){
						db2list = new ArrayList();
						db2list.add(vo);
					}
				}
			}
			
			//sqlserver采集
			if (mslist != null) {
				for (int i = 0; i < mslist.size(); i++) {
					Hashtable sqlserverdata = new Hashtable();
					DBVo dbmonitorlist = (DBVo) mslist.get(i);
					DBNode dbnode = (DBNode) PollingEngine.getInstance().getDbByID(dbmonitorlist.getId());
					//初始化数据库节点状态
					dbnode.setAlarm(false);
					dbnode.setStatus(0);
					Calendar _tempCal = Calendar.getInstance();
					Date _cc = _tempCal.getTime();
					String _time = sdf.format(_cc);
					dbnode.setLastTime(_time);
					dbnode.getAlarmMessage().clear();

					String serverip = dbmonitorlist.getIpAddress();
					String username = dbmonitorlist.getUser();
					String passwords = dbmonitorlist.getPassword();

					Calendar date = Calendar.getInstance();
					Date d = new Date();
					//判断该数据库是否能连接上
					boolean sqlserverIsOK = false;
					
					NodeGatherIndicatorsDao indicatorsdao = new NodeGatherIndicatorsDao();
			    	List<NodeGatherIndicators> monitorItemList = new ArrayList<NodeGatherIndicators>(); 
			    	try{
			    		//获取被启用的ORACLE所有被监视指标
			    		monitorItemList = indicatorsdao.getByNodeId(dbnode.getId()+"",1,"db","sqlserver");
			    	}catch(Exception e){
			    		e.printStackTrace();
			    	}finally{
			    		indicatorsdao.close();
			    	}
			    	if(monitorItemList == null)monitorItemList = new ArrayList<NodeGatherIndicators>();
			    	Hashtable gatherHash = new Hashtable();
			    	for(int k=0;k<monitorItemList.size();k++){
			    		NodeGatherIndicators nodeGatherIndicators = (NodeGatherIndicators)monitorItemList.get(k);
			    		gatherHash.put(nodeGatherIndicators.getName(), nodeGatherIndicators);
			    	}
			    	SQLServerDataCollector sqlservercollector = new SQLServerDataCollector();
	            	SysLogger.info("##############################");
	            	SysLogger.info("### 开始采集ID为"+dbnode.getId()+"的SQLSERVER数据 ");
	            	SysLogger.info("##############################");
	            	sqlservercollector.collect_data(dbnode.getId()+"", gatherHash);
				}
			}

			//取得oracle采集
			if (oclist != null) {
				for (int i = 0; i < oclist.size(); i++) {
					Object obj = oclist.get(i);
					DBVo dbmonitorlist = new DBVo();
					BeanUtils.copyProperties(dbmonitorlist, obj);
					DBNode dbnode = (DBNode) PollingEngine.getInstance().getDbByID(dbmonitorlist.getId());
					if (dbnode != null) {
						
						dbnode.setStatus(0);
						dbnode.setAlarm(false);
						dbnode.getAlarmMessage().clear();
						Calendar _tempCal = Calendar.getInstance();
						Date _cc = _tempCal.getTime();
						String _time = sdf.format(_cc);
						dbnode.setLastTime(_time);
						
//						//判断设备是否在采集时间段内 0:不在采集时间段内,则退出;1:在时间段内,进行采集;2:不存在采集时间段设置,则全天采集
//	        			TimeGratherConfigUtil timeconfig = new TimeGratherConfigUtil();
//	        			int result = 0;
//	        			result = timeconfig.isBetween(dbnode.getId()+"", "db");
//						if(result == 0){
//							SysLogger.info("###### "+dbnode.getIpAddress()+" 不在采集时间段内,跳过######");
//							continue;
//						}
						
					}else 
						continue;
					
					NodeGatherIndicatorsDao indicatorsdao = new NodeGatherIndicatorsDao();
			    	List<NodeGatherIndicators> monitorItemList = new ArrayList<NodeGatherIndicators>(); 
			    	try{
			    		//获取被启用的ORACLE所有被监视指标
			    		monitorItemList = indicatorsdao.getByNodeId(dbnode.getId()+"",1,"db","oracle");
			    	}catch(Exception e){
			    		e.printStackTrace();
			    	}finally{
			    		indicatorsdao.close();
			    	}
			    	if(monitorItemList == null)monitorItemList = new ArrayList<NodeGatherIndicators>();
			    	Hashtable gatherHash = new Hashtable();
			    	for(int k=0;k<monitorItemList.size();k++){
			    		NodeGatherIndicators nodeGatherIndicators = (NodeGatherIndicators)monitorItemList.get(k);
			    		gatherHash.put(nodeGatherIndicators.getName(), nodeGatherIndicators);
			    	}
			    	
			    	
					OracleDataCollector oraclecollector = new OracleDataCollector();
					SysLogger.info("##############################");
	            	SysLogger.info("### 开始采集ID为"+dbnode.getId()+"的ORACLE数据 ");
	            	SysLogger.info("##############################");
	            	oraclecollector.collect_data(dbnode.getId()+"", gatherHash);
				}

			}

			//取得sysbase采集
			if (sysbaselist != null) {
				SybspaceconfigDao sybspaceconfigManager = new SybspaceconfigDao();
				Hashtable sybspaceconfig = new Hashtable();
				try {
					sybspaceconfig = sybspaceconfigManager.getByAlarmflag(1);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					sybspaceconfigManager.close();
				}

				for (int i = 0; i < sysbaselist.size(); i++) {
					Object obj = sysbaselist.get(i);

					DBVo dbmonitorlist = new DBVo();
					BeanUtils.copyProperties(dbmonitorlist, obj);
					DBNode dbnode = (DBNode) PollingEngine.getInstance().getDbByID(dbmonitorlist.getId());
					dbnode.setAlarm(false);
					dbnode.setStatus(0);
					Calendar _tempCal = Calendar.getInstance();
					Date _cc = _tempCal.getTime();
					String _time = sdf.format(_cc);
					dbnode.setLastTime(_time);
					dbnode.getAlarmMessage().clear();

					String serverip = dbmonitorlist.getIpAddress();
					String username = dbmonitorlist.getUser();
					String passwords = dbmonitorlist.getPassword();
					int port = Integer.parseInt(dbmonitorlist.getPort());
					String dbnames = dbmonitorlist.getDbName();
					Date d1 = new Date();
					//判断该数据库是否能连接上
					NodeGatherIndicatorsDao indicatorsdao = new NodeGatherIndicatorsDao();
			    	List<NodeGatherIndicators> monitorItemList = new ArrayList<NodeGatherIndicators>(); 
			    	try{
			    		//获取被启用的ORACLE所有被监视指标
			    		monitorItemList = indicatorsdao.getByNodeId(dbnode.getId()+"",1,"db","sybase");
			    	}catch(Exception e){
			    		e.printStackTrace();
			    	}finally{
			    		indicatorsdao.close();
			    	}
			    	if(monitorItemList == null)monitorItemList = new ArrayList<NodeGatherIndicators>();
			    	Hashtable gatherHash = new Hashtable();
			    	NodeGatherIndicators nodeGatherIndicators = null;
			    	for(int k=0;k<monitorItemList.size();k++){
			    		nodeGatherIndicators = (NodeGatherIndicators)monitorItemList.get(k);
			    		gatherHash.put(nodeGatherIndicators.getName(), nodeGatherIndicators);
			    	}
			    	try {
			    		SybaseDataCollector sybasecollector = new SybaseDataCollector();
		            	SysLogger.info("##############################");
		            	SysLogger.info("### 开始采集ID为"+dbnode.getId()+"的SYBASE数据 ");
		            	SysLogger.info("##############################");
		            	sybasecollector.collect_Data(nodeGatherIndicators);
		            }catch(Exception exc){
		            	
		            }

					SysLogger.info("end collect sysbase --------- " + serverip);
				}
			}

			//取得informix采集
			if (informixlist != null) {
				for (int i = 0; i < informixlist.size(); i++) {
					Hashtable monitorValue = new Hashtable();

					Object obj = informixlist.get(i);

					DBVo dbmonitorlist = new DBVo();
					BeanUtils.copyProperties(dbmonitorlist, obj);
					DBNode dbnode = (DBNode) PollingEngine.getInstance().getDbByID(dbmonitorlist.getId());
					dbnode.setAlarm(false);
					dbnode.setStatus(0);
					Calendar _tempCal = Calendar.getInstance();
					Date _cc = _tempCal.getTime();
					String _time = sdf.format(_cc);
					dbnode.setLastTime(_time);
					dbnode.getAlarmMessage().clear();

					String serverip = dbmonitorlist.getIpAddress();
					String username = dbmonitorlist.getUser();
					String passwords = dbmonitorlist.getPassword();
					int port = Integer.parseInt(dbmonitorlist.getPort());
					String dbnames = dbmonitorlist.getDbName();
					String dbservername = dbmonitorlist.getAlias();//临时的服务名称
					Date d1 = new Date();
					//判断该数据库是否能连接上
					int allFlag = 0;
					
					SysLogger.info("begin collect informix--" + dbnames + " --------- " + serverip);
					NodeGatherIndicatorsDao indicatorsdao = new NodeGatherIndicatorsDao();
			    	List<NodeGatherIndicators> monitorItemList = new ArrayList<NodeGatherIndicators>(); 
			    	try{
			    		//获取被启用的ORACLE所有被监视指标
			    		monitorItemList = indicatorsdao.getByNodeId(dbnode.getId()+"",1,"db","informix");
			    	}catch(Exception e){
			    		e.printStackTrace();
			    	}finally{
			    		indicatorsdao.close();
			    	}
			    	if(monitorItemList == null)monitorItemList = new ArrayList<NodeGatherIndicators>();
			    	Hashtable gatherHash = new Hashtable();
			    	for(int k=0;k<monitorItemList.size();k++){
			    		NodeGatherIndicators nodeGatherIndicators = (NodeGatherIndicators)monitorItemList.get(k);
			    		gatherHash.put(nodeGatherIndicators.getName(), nodeGatherIndicators);
			    	}
			    	InformixDataCollector informixcollector = new InformixDataCollector();
	            	SysLogger.info("##############################");
	            	SysLogger.info("### 开始采集ID为"+dbnode.getId()+"的INFORMIX数据 ");
	            	SysLogger.info("##############################");
	            	informixcollector.collect_data(dbnode.getId()+"", gatherHash);

					SysLogger.info("end collect informix --------- " + serverip);
				}
			}

			//取得db2采集
			if (db2list != null) {
				for (int i = 0; i < db2list.size(); i++) {
					Object obj = db2list.get(i);
					DBVo dbmonitorlist = new DBVo();
					BeanUtils.copyProperties(dbmonitorlist, obj);
					DBNode dbnode = (DBNode) PollingEngine.getInstance().getDbByID(dbmonitorlist.getId());
					dbnode.setAlarm(false);
					dbnode.setStatus(0);
					Calendar _tempCal = Calendar.getInstance();
					Date _cc = _tempCal.getTime();
					String _time = sdf.format(_cc);
					dbnode.setLastTime(_time);
					dbnode.getAlarmMessage().clear();

					String serverip = dbmonitorlist.getIpAddress();
					String username = dbmonitorlist.getUser();
					String passwords = dbmonitorlist.getPassword();
					int port = Integer.parseInt(dbmonitorlist.getPort());
					String dbnames = dbmonitorlist.getDbName();
					Date d1 = new Date();
					//判断该数据库是否能连接上
					String[] dbs = dbnames.split(",");
					//SysLogger.info("process db2 ====== "+serverip);
					int allFlag = 0;

					SysLogger.info("begin collect db2--" + dbnames + " --------- " + serverip);
					NodeGatherIndicatorsDao indicatorsdao = new NodeGatherIndicatorsDao();
			    	List<NodeGatherIndicators> monitorItemList = new ArrayList<NodeGatherIndicators>(); 
			    	try{
			    		//获取被启用的ORACLE所有被监视指标
			    		monitorItemList = indicatorsdao.getByNodeId(dbnode.getId()+"",1,"db","db2");
			    	}catch(Exception e){
			    		e.printStackTrace();
			    	}finally{
			    		indicatorsdao.close();
			    	}
			    	if(monitorItemList == null)monitorItemList = new ArrayList<NodeGatherIndicators>();
			    	Hashtable gatherHash = new Hashtable();
			    	NodeGatherIndicators nodeGatherIndicators = null;
			    	for(int k=0;k<monitorItemList.size();k++){
			    		nodeGatherIndicators = (NodeGatherIndicators)monitorItemList.get(k);
			    		gatherHash.put(nodeGatherIndicators.getName(), nodeGatherIndicators);
			    	}
			    	DB2DataCollector db2collector = new DB2DataCollector();
	            	SysLogger.info("##############################");
	            	SysLogger.info("### 开始采集ID为"+dbnode.getId()+"的DB2数据 ");
	            	SysLogger.info("##############################");
	            	db2collector.collect_Data(nodeGatherIndicators);
					SysLogger.info("end process db2 ====== " + serverip);
				}
			}

			//取得mysql采集
			if (mysqllist != null) {
				//SybspaceconfigDao sybspaceconfigManager=new SybspaceconfigDao();
				//Hashtable sybspaceconfig = sybspaceconfigManager.getByAlarmflag(1);
				//sybspaceconfigManager.close();
				for (int i = 0; i < mysqllist.size(); i++) {
					Hashtable monitorValue = new Hashtable();

					Object obj = mysqllist.get(i);

					DBVo dbmonitorlist = new DBVo();
					BeanUtils.copyProperties(dbmonitorlist, obj);
					DBNode dbnode = (DBNode) PollingEngine.getInstance().getDbByID(dbmonitorlist.getId());
					dbnode.setAlarm(false);
					dbnode.setStatus(0);
					Calendar _tempCal = Calendar.getInstance();
					Date _cc = _tempCal.getTime();
					String _time = sdf.format(_cc);
					dbnode.setLastTime(_time);
					dbnode.getAlarmMessage().clear();

					String serverip = dbmonitorlist.getIpAddress();
					String username = dbmonitorlist.getUser();
					String passwords = dbmonitorlist.getPassword();
					int port = Integer.parseInt(dbmonitorlist.getPort());
					String dbnames = dbmonitorlist.getDbName();
					Date d1 = new Date();
					//判断该数据库是否能连接上
					String[] dbs = dbnames.split(",");
					//判断该数据库是否能连接上
					
					NodeGatherIndicatorsDao indicatorsdao = new NodeGatherIndicatorsDao();
			    	List<NodeGatherIndicators> monitorItemList = new ArrayList<NodeGatherIndicators>(); 
			    	try{
			    		//获取被启用的ORACLE所有被监视指标
			    		monitorItemList = indicatorsdao.getByNodeId(dbnode.getId()+"",1,"db","mysql");
			    	}catch(Exception e){
			    		e.printStackTrace();
			    	}finally{
			    		indicatorsdao.close();
			    	}
			    	if(monitorItemList == null)monitorItemList = new ArrayList<NodeGatherIndicators>();
			    	Hashtable gatherHash = new Hashtable();
			    	NodeGatherIndicators nodeGatherIndicators = null;
			    	for(int k=0;k<monitorItemList.size();k++){
			    		nodeGatherIndicators = (NodeGatherIndicators)monitorItemList.get(k);
			    		gatherHash.put(nodeGatherIndicators.getName(), nodeGatherIndicators);
			    	}
			    	try {
		            	MySqlDataCollector mysqlcollector = new MySqlDataCollector();
		            	SysLogger.info("##############################");
		            	SysLogger.info("### 开始采集ID为"+dbnode.getId()+"的MYSQL数据 ");
		            	SysLogger.info("##############################");
		            	mysqlcollector.collect_Data(nodeGatherIndicators);
		            }catch(Exception exc){
		            	
		            }
					SysLogger.info("end collect mysql --------- " + serverip);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbdao.close();
			//System.out.println("********DB Thread Count : " + Thread.activeCount());
		}
	} 
	
	/*--------modify   zhao -------------------------*/
//	public void createSMS(String db, DBVo dbmonitorlist, OracleEntity oracle) {
//		// 建立短信
//		// 从内存里获得当前这个IP的PING的值
//		Calendar date = Calendar.getInstance();
//		// for (OracleEntity oracle : oracles) {
//		try {
//			if (!sendeddata.containsKey(db + ":" + dbmonitorlist.getIpAddress() + ":" + oracle.getSid())) {
//				// 若不在，则建立短信，并且添加到发送列表里
//				Smscontent smscontent = new Smscontent();
//				String time = sdf.format(date.getTime());
//				smscontent.setLevel("2");
//				smscontent.setObjid(dbmonitorlist.getId() + ":oracle" + oracle.getId());
//				smscontent.setMessage(db + "(" + oracle.getSid() + " IP:" + dbmonitorlist.getIpAddress() + ")" + "的数据库服务停止");
//				smscontent.setRecordtime(time);
//				smscontent.setSubtype("db");
//				smscontent.setSubentity("ping");
//				smscontent.setIp(dbmonitorlist.getIpAddress() + ":" + oracle.getSid());
//				// 发送短信
//				SmscontentDao smsmanager = new SmscontentDao();
//				try {
//					smsmanager.sendDatabaseSmscontent(smscontent);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				sendeddata.put(db + ":" + dbmonitorlist.getIpAddress() + ":" + oracle.getSid(), date);
//			} else {
//				// 若在，则从已发送短信列表里判断是否已经发送当天的短信
//				Calendar formerdate = (Calendar) sendeddata.get(db + ":" + dbmonitorlist.getIpAddress() + ":" + oracle.getSid());
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
//					smscontent.setIp(dbmonitorlist.getIpAddress() + ":" + oracle.getSid());
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
//					sendeddata.put(db + ":" + dbmonitorlist.getIpAddress() + ":" + oracle.getSid(), date);
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		// }
//
//	}

//	public void createSMS(String db, DBVo dbmonitorlist) {
//		//建立短信		 	
//		//从内存里获得当前这个IP的PING的值
//		Calendar date = Calendar.getInstance();
//		try {
//			if (!sendeddata.containsKey(db + ":" + dbmonitorlist.getIpAddress())) {
//				//若不在，则建立短信，并且添加到发送列表里
//				Smscontent smscontent = new Smscontent();
//				String time = sdf.format(date.getTime());
//				smscontent.setLevel("2");
//				smscontent.setObjid(dbmonitorlist.getId() + "");
//				smscontent.setMessage(db + "(" + dbmonitorlist.getDbName() + " IP:" + dbmonitorlist.getIpAddress() + ")"
//						+ "的数据库服务停止");
//				smscontent.setRecordtime(time);
//				smscontent.setSubtype("db");
//				smscontent.setSubentity("ping");
//				smscontent.setIp(dbmonitorlist.getIpAddress());
//				//发送短信
//				SmscontentDao smsmanager = new SmscontentDao();
//				try {
//					smsmanager.sendDatabaseSmscontent(smscontent);
//				} catch (Exception e) {
//
//				}
//				sendeddata.put(db + ":" + dbmonitorlist.getIpAddress(), date);
//			} else {
//				//若在，则从已发送短信列表里判断是否已经发送当天的短信
//				Calendar formerdate = (Calendar) sendeddata.get(db + ":" + dbmonitorlist.getIpAddress());
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
//					//超过一天，则再发信息
//					Smscontent smscontent = new Smscontent();
//					String time = sdf.format(date.getTime());
//					smscontent.setLevel("2");
//					smscontent.setObjid(dbmonitorlist.getId() + "");
//					smscontent.setMessage(db + "(" + dbmonitorlist.getDbName() + " IP:" + dbmonitorlist.getIpAddress() + ")"
//							+ "的数据库服务停止");
//					smscontent.setRecordtime(time);
//					smscontent.setSubtype("db");
//					smscontent.setSubentity("ping");
//					smscontent.setIp(dbmonitorlist.getIpAddress());
//					//smscontent.setMessage("db&"+time+"&"+dbmonitorlist.getId()+"&"+db+"("+dbmonitorlist.getDbName()+" IP:"+dbmonitorlist.getIpAddress()+")"+"的数据库服务停止");
//					//发送短信
//					SmscontentDao smsmanager = new SmscontentDao();
//					try {
//						smsmanager.sendDatabaseSmscontent(smscontent);
//					} catch (Exception e) {
//
//					}
//					//修改已经发送的短信记录	
//					sendeddata.put(db + ":" + dbmonitorlist.getIpAddress(), date);
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

//	public static void createSpaceSMS(DBVo dbmonitorlist, Oraspaceconfig oraspaceconfig, OracleEntity oracle) {
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
//			if (!sendeddata.containsKey(ipaddress + ":" + oracle.getId() + ":" + oraspaceconfig.getSpacename())) {
//				// 若不在，则建立短信，并且添加到发送列表里
//				Smscontent smscontent = new Smscontent();
//				smscontent.setLevel("2");
//				smscontent.setObjid(dbmonitorlist.getId() + ":oracle" + oracle.getId());
//				smscontent.setMessage(dbmonitorlist.getIpAddress() + ":" + oracle.getSid() + "的数据库的"
//						+ oraspaceconfig.getSpacename() + "表空间超过" + oraspaceconfig.getAlarmvalue() + "%的阀值");
//				smscontent.setRecordtime(time);
//				smscontent.setSubtype("db");
//				smscontent.setSubentity("oraspace");
//				smscontent.setIp(dbmonitorlist.getIpAddress() + ":" + oracle.getSid());
//				// smscontent.setMessage(errorcontent);
//				// 发送短信
//				try {
//					smsmanager.sendDatabaseSmscontent(smscontent);
//				} catch (Exception e) {
//
//				}
//				sendeddata.put(ipaddress + ":" + oracle.getId() + ":" + oraspaceconfig.getSpacename(), date);
//			} else {
//				// 若在，则从已发送短信列表里判断是否已经发送当天的短信
//				Calendar formerdate = (Calendar) sendeddata.get(ipaddress + ":" + oracle.getId() + ":"
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
//					smscontent.setObjid(dbmonitorlist.getId() + ":oracle" + oracle.getId());
//					smscontent.setMessage(dbmonitorlist.getIpAddress() + "的数据库的" + oraspaceconfig.getSpacename() + "表空间超过"
//							+ oraspaceconfig.getAlarmvalue() + "%的阀值");
//					smscontent.setRecordtime(time);
//					smscontent.setSubtype("db");
//					smscontent.setSubentity("oraspace");
//					smscontent.setIp(dbmonitorlist.getIpAddress() + ":" + oracle.getId());
//					// 发送短信
//					try {
//						smsmanager.sendDatabaseSmscontent(smscontent);
//					} catch (Exception e) {
//
//					}
//					sendeddata.put(ipaddress + ":" + oracle.getId() + ":" + oraspaceconfig.getSpacename(), date);
//				} else {
//					// 则写声音告警数据
//					// 向声音告警表里写数据
//					AlarmInfo alarminfo = new AlarmInfo();
//					alarminfo.setContent(dbmonitorlist.getIpAddress() + "的数据库的" + oraspaceconfig.getSpacename() + "表空间超过"
//							+ oraspaceconfig.getAlarmvalue() + "%的阀值");
//					alarminfo.setIpaddress(ipaddress);
//					alarminfo.setLevel1(new Integer(2));
//					alarminfo.setRecordtime(Calendar.getInstance());
//					alarminfomanager.save(alarminfo);
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			alarminfomanager.close();
//		}
//	}

//	public static void createInformixSpaceSMS(DBVo dbmonitorlist, Informixspaceconfig informixspaceconfig) {
//		//建立短信		 	
//		//从内存里获得当前这个IP的PING的值
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		SmscontentDao smsmanager = new SmscontentDao();
//		AlarmInfoDao alarminfomanager = new AlarmInfoDao();
//
//		String ipaddress = dbmonitorlist.getIpAddress();
//		Hashtable sendeddata = ShareData.getSendeddata();
//		Calendar date = Calendar.getInstance();
//		String time = sdf.format(date.getTime());
//		try {
//			if (!sendeddata.containsKey(ipaddress + ":" + informixspaceconfig.getSpacename())) {
//				//若不在，则建立短信，并且添加到发送列表里
//				Smscontent smscontent = new Smscontent();
//				smscontent.setLevel("2");
//				smscontent.setObjid(dbmonitorlist.getId() + "");
//				smscontent.setMessage(dbmonitorlist.getIpAddress() + "的数据库的" + informixspaceconfig.getSpacename() + "表空间超过"
//						+ informixspaceconfig.getAlarmvalue() + "%的阀值");
//				smscontent.setRecordtime(time);
//				smscontent.setSubtype("db");
//				smscontent.setSubentity("informixspace");
//				smscontent.setIp(dbmonitorlist.getIpAddress());
//				//发送短信
//				try {
//					SysLogger.info("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
//					smsmanager.sendDatabaseSmscontent(smscontent);
//				} catch (Exception e) {
//
//				}
//				sendeddata.put(ipaddress + ":" + informixspaceconfig.getSpacename(), date);
//			} else {
//				//若在，则从已发送短信列表里判断是否已经发送当天的短信
//				Calendar formerdate = (Calendar) sendeddata.get(ipaddress + ":" + informixspaceconfig.getSpacename());
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
//					//超过一天，则再发信息
//					Smscontent smscontent = new Smscontent();
//					smscontent.setLevel("2");
//					smscontent.setObjid(dbmonitorlist.getId() + "");
//					smscontent.setMessage(dbmonitorlist.getIpAddress() + "的数据库的" + informixspaceconfig.getSpacename() + "表空间超过"
//							+ informixspaceconfig.getAlarmvalue() + "%的阀值");
//					smscontent.setRecordtime(time);
//					smscontent.setSubtype("db");
//					smscontent.setSubentity("informixspace");
//					smscontent.setIp(dbmonitorlist.getIpAddress());
//					//发送短信
//					try {
//						smsmanager.sendDatabaseSmscontent(smscontent);
//					} catch (Exception e) {
//
//					}
//					sendeddata.put(ipaddress + ":" + informixspaceconfig.getSpacename(), date);
//				} else {
//					//则写声音告警数据
//					//向声音告警表里写数据
//					AlarmInfo alarminfo = new AlarmInfo();
//					alarminfo.setContent(dbmonitorlist.getIpAddress() + "的数据库的" + informixspaceconfig.getSpacename() + "表空间超过"
//							+ informixspaceconfig.getAlarmvalue() + "%的阀值");
//					alarminfo.setIpaddress(ipaddress);
//					alarminfo.setLevel1(new Integer(2));
//					alarminfo.setRecordtime(Calendar.getInstance());
//					alarminfomanager.save(alarminfo);
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			alarminfomanager.close();
//		}
//	}

//	public static void createSqldbSMS(DBVo dbmonitorlist, Sqldbconfig sqldbconfig) {
//		//建立短信		 	
//		//从内存里获得当前这个IP的PING的值
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		SmscontentDao smsmanager = new SmscontentDao();
//		AlarmInfoDao alarminfomanager = new AlarmInfoDao();
//
//		String ipaddress = dbmonitorlist.getIpAddress();
//		Hashtable sendeddata = ShareData.getSendeddata();
//		Calendar date = Calendar.getInstance();
//		String time = sdf.format(date.getTime());
//		String errorcontent = "";
//		if (sqldbconfig.getLogflag() == 0) {
//			//库文件
//			errorcontent = dbmonitorlist.getIpAddress() + "的" + dbmonitorlist.getDbName() + "的" + sqldbconfig.getDbname()
//					+ "的库空间超过" + sqldbconfig.getAlarmvalue() + "%的阀值";
//		} else {
//			//日志文件
//			errorcontent = dbmonitorlist.getIpAddress() + "的" + dbmonitorlist.getDbName() + "的" + sqldbconfig.getDbname()
//					+ "的日志超过" + sqldbconfig.getAlarmvalue() + "%的阀值";
//		}
//
//		try {
//			if (!sendeddata.containsKey(ipaddress + ":" + sqldbconfig.getDbname() + ":" + sqldbconfig.getLogflag())) {
//				//若不在，则建立短信，并且添加到发送列表里
//				Smscontent smscontent = new Smscontent();
//				//String time1 = sdf.format(date.getTime());
//				smscontent.setLevel("2");
//				smscontent.setObjid(dbmonitorlist.getId() + "");
//				smscontent.setMessage(errorcontent);
//				smscontent.setRecordtime(time);
//				smscontent.setSubtype("db");
//				smscontent.setSubentity("sqldb");
//				smscontent.setIp(dbmonitorlist.getIpAddress());
//				//发送短信
//				try {
//					smsmanager.sendURLSmscontent(smscontent);
//				} catch (Exception e) {
//
//				}
//				sendeddata.put(ipaddress + ":" + sqldbconfig.getDbname() + ":" + sqldbconfig.getLogflag(), date);
//			} else {
//				//若在，则从已发送短信列表里判断是否已经发送当天的短信
//				Calendar formerdate = (Calendar) sendeddata.get(ipaddress + ":" + sqldbconfig.getDbname() + ":"
//						+ sqldbconfig.getLogflag());
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
//					//超过一天，则再发信息
//					Smscontent smscontent = new Smscontent();
//					//String time1 = sdf.format(date.getTime());
//					smscontent.setLevel("2");
//					smscontent.setObjid(dbmonitorlist.getId() + "");
//					smscontent.setMessage(errorcontent);
//					smscontent.setRecordtime(time);
//					smscontent.setSubtype("db");
//					smscontent.setSubentity("sqldb");
//					smscontent.setIp(dbmonitorlist.getIpAddress());
//					//发送短信
//					try {
//						smsmanager.sendURLSmscontent(smscontent);
//					} catch (Exception e) {
//
//					}
//					//修改已经发送的短信记录	
//					sendeddata.put(ipaddress + ":" + sqldbconfig.getDbname() + ":" + sqldbconfig.getLogflag(), date);
//				} else {
//					//则写声音告警数据
//					//向声音告警表里写数据
//					AlarmInfo alarminfo = new AlarmInfo();
//					alarminfo.setContent(errorcontent);
//					alarminfo.setIpaddress(ipaddress);
//					alarminfo.setLevel1(new Integer(2));
//					alarminfo.setRecordtime(Calendar.getInstance());
//					alarminfomanager.save(alarminfo);
//
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

//	public static void createDb2SpaceSMS(DBVo dbmonitorlist, Db2spaceconfig db2spaceconfig) {
//		//建立短信		 	
//		//从内存里获得当前这个IP的PING的值
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		SmscontentDao smsmanager = new SmscontentDao();
//		AlarmInfoDao alarminfomanager = new AlarmInfoDao();
//
//		String ipaddress = dbmonitorlist.getIpAddress();
//		Hashtable sendeddata = ShareData.getSendeddata();
//		//EventListDao eventmanager=new EventListDao();
//		Calendar date = Calendar.getInstance();
//		String time = sdf.format(date.getTime());
//		String errorcontent = dbmonitorlist.getIpAddress() + "的" + db2spaceconfig.getDbname() + "的"
//				+ db2spaceconfig.getSpacename() + "的表空间超过" + db2spaceconfig.getAlarmvalue() + "%阀值";
//		try {
//			if (!sendeddata.containsKey(ipaddress + ":" + db2spaceconfig.getDbname() + ":" + db2spaceconfig.getSpacename())) {
//				//若不在，则建立短信，并且添加到发送列表里
//				Smscontent smscontent = new Smscontent();
//				smscontent.setLevel("2");
//				smscontent.setObjid(dbmonitorlist.getId() + "");
//				smscontent.setMessage(errorcontent);
//				smscontent.setRecordtime(time);
//				smscontent.setSubtype("db");
//				smscontent.setSubentity("db2space");
//				smscontent.setIp(dbmonitorlist.getIpAddress());
//				//发送短信
//				try {
//					smsmanager.sendURLSmscontent(smscontent);
//				} catch (Exception e) {
//
//				}
//				sendeddata.put(ipaddress + ":" + db2spaceconfig.getDbname() + ":" + db2spaceconfig.getSpacename(), date);
//			} else {
//				//若在，则从已发送短信列表里判断是否已经发送当天的短信
//				Calendar formerdate = (Calendar) sendeddata.get(ipaddress + ":" + db2spaceconfig.getDbname() + ":"
//						+ db2spaceconfig.getSpacename());
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
//					//超过一天，则再发信息
//					Smscontent smscontent = new Smscontent();
//					smscontent.setLevel("2");
//					smscontent.setObjid(dbmonitorlist.getId() + "");
//					smscontent.setMessage(errorcontent);
//					smscontent.setRecordtime(time);
//					smscontent.setSubtype("db");
//					smscontent.setSubentity("db2space");
//					smscontent.setIp(dbmonitorlist.getIpAddress());
//					//发送短信
//					try {
//						smsmanager.sendURLSmscontent(smscontent);
//					} catch (Exception e) {
//
//					}
//					//修改已经发送的短信记录	
//					sendeddata.put(ipaddress + ":" + db2spaceconfig.getDbname() + ":" + db2spaceconfig.getSpacename(), date);
//				} else {
//					//则写声音告警数据
//					//向声音告警表里写数据
//					AlarmInfo alarminfo = new AlarmInfo();
//					alarminfo.setContent(errorcontent);
//					alarminfo.setIpaddress(ipaddress);
//					alarminfo.setLevel1(new Integer(2));
//					alarminfo.setRecordtime(Calendar.getInstance());
//					alarminfomanager.save(alarminfo);
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	

//	public static void createSybSpaceSMS(DBVo dbvo, Sybspaceconfig sybconfig) {
//		//建立短信		 	
//		//从内存里获得当前这个IP的PING的值
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		SmscontentDao smsmanager = new SmscontentDao();
//		AlarmInfoDao alarminfomanager = new AlarmInfoDao();
//
//		String ipaddress = dbvo.getIpAddress();
//		Hashtable sendeddata = ShareData.getSendeddata();
//		//I_Eventlist eventmanager=new EventlistManager();
//		Calendar date = Calendar.getInstance();
//		String time = sdf.format(date.getTime());
//		String errorcontent = dbvo.getIpAddress() + "的" + dbvo.getDbName() + "的" + sybconfig.getSpacename() + "的表空间超过"
//				+ sybconfig.getAlarmvalue() + "%阀值";
//		try {
//			if (!sendeddata.containsKey(ipaddress + ":" + sybconfig.getSpacename())) {
//				//若不在，则建立短信，并且添加到发送列表里
//				Smscontent smscontent = new Smscontent();
//				//String time1 = sdf.format(date.getTime());
//				smscontent.setLevel("2");
//				smscontent.setObjid(dbvo.getId() + "");
//				smscontent.setMessage(errorcontent);
//				smscontent.setRecordtime(time);
//				smscontent.setSubtype("db");
//				smscontent.setSubentity("sybspace");
//				smscontent.setIp(dbvo.getIpAddress());
//				//发送短信
//				try {
//					smsmanager.sendURLSmscontent(smscontent);
//				} catch (Exception e) {
//
//				}
//				sendeddata.put(ipaddress + ":" + sybconfig.getSpacename(), date);
//			} else {
//				//若在，则从已发送短信列表里判断是否已经发送当天的短信
//				Calendar formerdate = (Calendar) sendeddata.get(ipaddress + ":" + sybconfig.getSpacename());
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
//					//超过一天，则再发信息
//					Smscontent smscontent = new Smscontent();
//					smscontent.setLevel("2");
//					smscontent.setObjid(dbvo.getId() + "");
//					smscontent.setMessage(errorcontent);
//					smscontent.setRecordtime(time);
//					smscontent.setSubtype("db");
//					smscontent.setSubentity("sybspace");
//					smscontent.setIp(dbvo.getIpAddress());
//					//发送短信
//					try {
//						smsmanager.sendURLSmscontent(smscontent);
//					} catch (Exception e) {
//
//					}
//					//修改已经发送的短信记录	
//					sendeddata.put(ipaddress + ":" + sybconfig.getSpacename(), date);
//				} else {
//					//则写声音告警数据
//					//向声音告警表里写数据
//					AlarmInfo alarminfo = new AlarmInfo();
//					alarminfo.setContent(errorcontent);
//					alarminfo.setIpaddress(ipaddress);
//					alarminfo.setLevel1(new Integer(2));
//					alarminfo.setRecordtime(Calendar.getInstance());
//					alarminfomanager.save(alarminfo);
//
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

//	public void createFileNotExistSMS(String ipaddress) {
//		//建立短信		 	
//		//从内存里获得当前这个IP的PING的值
//		Calendar date = Calendar.getInstance();
//		try {
//			Host host = (Host) PollingEngine.getInstance().getNodeByIP(ipaddress);
//			if (host == null)
//				return;
//
//			if (!sendeddata.containsKey(ipaddress + ":file:" + host.getId())) {
//				//若不在，则建立短信，并且添加到发送列表里
//				Smscontent smscontent = new Smscontent();
//				String time = sdf.format(date.getTime());
//				smscontent.setLevel("3");
//				smscontent.setObjid(host.getId() + "");
//				smscontent.setMessage(host.getAlias() + " (" + host.getIpAddress() + ")" + "的日志文件无法正确上传到网管服务器");
//				smscontent.setRecordtime(time);
//				smscontent.setSubtype("host");
//				smscontent.setSubentity("ftp");
//				smscontent.setIp(host.getIpAddress());//发送短信
//				SmscontentDao smsmanager = new SmscontentDao();
//				smsmanager.sendURLSmscontent(smscontent);
//				sendeddata.put(ipaddress + ":file" + host.getId(), date);
//			} else {
//				//若在，则从已发送短信列表里判断是否已经发送当天的短信
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
//					//超过一天，则再发信息
//					Smscontent smscontent = new Smscontent();
//					String time = sdf.format(date.getTime());
//					smscontent.setLevel("3");
//					smscontent.setObjid(host.getId() + "");
//					smscontent.setMessage(host.getAlias() + " (" + host.getIpAddress() + ")" + "的日志文件无法正确上传到网管服务器");
//					smscontent.setRecordtime(time);
//					smscontent.setSubtype("host");
//					smscontent.setSubentity("ftp");
//					smscontent.setIp(host.getIpAddress());//发送短信
//					SmscontentDao smsmanager = new SmscontentDao();
//					smsmanager.sendURLSmscontent(smscontent);
//					//修改已经发送的短信记录	
//					sendeddata.put(ipaddress + ":file:" + host.getId(), date);
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
}
