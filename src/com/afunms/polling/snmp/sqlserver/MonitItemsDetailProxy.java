package com.afunms.polling.snmp.sqlserver;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.apache.commons.beanutils.BeanUtils;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.dao.DBDao;
import com.afunms.application.model.DBVo;
import com.afunms.application.util.IpTranslation;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.EncryptUtil;
import com.afunms.common.util.JdbcUtil;
import com.afunms.common.util.PersistenceService;
import com.afunms.common.util.PersistenceServiceable;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SystemConstant;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.DBNode;
import com.afunms.polling.om.Task;
import com.afunms.polling.task.TaskXml;
import com.gatherdb.GathersqlListManager;

public class MonitItemsDetailProxy extends SnmpMonitor{

	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicators){
		Hashtable sqlserverDataHash = ShareData.getSqlserverdata();

		DBVo dbmonitorlist = null; 
		DBDao dbdao = new DBDao();
		try{
			String dbid = nodeGatherIndicators.getNodeid();
			dbmonitorlist = (DBVo)dbdao.findByID(dbid);
		}catch(Exception e){
			
		}finally{
			dbdao.close();
		}
		if(dbmonitorlist == null)return null;
		if(dbmonitorlist.getManaged() == 0)return null;
		DBNode dbnode = (DBNode) PollingEngine.getInstance().getDbByID(dbmonitorlist.getId());
		String serverip = dbnode.getIpAddress();
		IpTranslation tranfer = new IpTranslation();
		String hex = tranfer.formIpToHex(serverip);
		String hexip = hex + ":" + dbmonitorlist.getAlias();
		
		if(sqlserverDataHash.get(serverip) == null){
			sqlserverDataHash.put(serverip, new Hashtable());
		}
		Hashtable sqlserverdata = (Hashtable)sqlserverDataHash.get(serverip);
		
		//采集数据
		Hashtable returndata = new Hashtable();
		Hashtable pages = new Hashtable();
		Hashtable conns = new Hashtable();
		Hashtable locks = new Hashtable();
		Hashtable caches = new Hashtable();
		Hashtable mems = new Hashtable();
		Hashtable sqls = new Hashtable();
		Hashtable scans = new Hashtable();
		Hashtable statisticsHash = new Hashtable();
		String[] args;
		args = new String[]{"serverip", "bufferCacheHitRatio", "planCacheHitRatio", 
				"cursorManagerByTypeHitRatio", "catalogMetadataHitRatio", "dbOfflineErrors", "killConnectionErrors", "userErrors",
				"infoErrors", "sqlServerErrors_total", "cachedCursorCounts", "cursorCacheUseCounts", "cursorRequests_total", 
				"activeCursors","cursorMemoryUsage","cursorWorktableUsage","activeOfCursorPlans","dbPages","totalPageLookups",
				"totalPageLookupsRate","totalPageReads","totalPageReadsRate","totalPageWrites","totalPageWritesRate","totalPages","freePages","mon_time"};
		returndata = LogParser.parse(dbmonitorlist, "pages", args);
		pages =  (Hashtable) ((Vector) returndata.get("pages")).get(0);
		args = new String[]{"serverip", "connections", "totalLogins", 
				"totalLoginsRate", "totalLogouts", "totalLogoutsRate","mon_time"};
		returndata = LogParser.parse(dbmonitorlist, "conns", args);
		conns = (Hashtable) ((Vector) returndata.get("conns")).get(0);
		args = new String[]{"serverip", "lockRequests", "lockRequestsRate", 
				"lockWaits", "lockWaitsRate", "lockTimeouts","lockTimeoutsRate","deadLocks","deadLocksRate","avgWaitTime",
				"avgWaitTimeBase","latchWaits","latchWaitsRate","avgLatchWait","mon_time"};
		returndata = LogParser.parse(dbmonitorlist, "locks", args);
		locks = (Hashtable) ((Vector) returndata.get("locks")).get(0);
		args = new String[]{"serverip", "cacheHitRatio", "cacheHitRatioBase", 
				"cacheCount", "cachePages", "cacheUsed","cacheUsedRate","mon_time"};
		returndata = LogParser.parse(dbmonitorlist, "caches", args);
		caches = (Hashtable) ((Vector) returndata.get("caches")).get(0);
		args = new String[]{"serverip", "totalMemory", "sqlMem", 
				"optMemory", "memGrantPending", "memGrantSuccess",
				"lockMem","conMemory","grantedWorkspaceMem","mon_time"};
		returndata = LogParser.parse(dbmonitorlist, "mems", args);
		mems = (Hashtable) ((Vector) returndata.get("mems")).get(0);
		args = new String[]{"serverip", "batchRequests", "batchRequestsRate", 
				"sqlCompilations", "sqlCompilationsRate", "sqlRecompilation","sqlRecompilationRate",
				"autoParams","autoParamsRate","failedAutoParams",
				"failedAutoParamsRate","mon_time"};
		returndata = LogParser.parse(dbmonitorlist, "sqls", args);
		sqls = (Hashtable) ((Vector) returndata.get("sqls")).get(0);
		args = new String[]{"serverip", "fullScans", "fullScansRate", 
				"rangeScans", "rangeScansRate", "probeScans","probeScansRate","mon_time"};
		returndata = LogParser.parse(dbmonitorlist, "scans", args);
		scans = (Hashtable) ((Vector) returndata.get("scans")).get(0);
		args = new String[]{"serverip","pj_lockWaits","pj_memGrantQueWaits","pj_thdSafeMemObjWaits","pj_logWriteWaits","pj_logBufferWaits",
				"pj_networkIOWaits","pj_pageIOLatchWaits","pj_pageLatchWaits","pj_nonPageLatchWaits","pj_waitForTheWorker",
				"pj_workspaceSynWaits","pj_traOwnershipWaits","jx_lockWaits","jx_memGrantQueWaits","jx_thdSafeMemObjWaits",
				"jx_logWriteWaits","jx_logBufferWaits","jx_networkIOWaits","jx_pageIOLatchWaits","jx_pageLatchWaits",
				"jx_nonPageLatchWaits","jx_waitForTheWorker","jx_workspaceSynWaits","jx_traOwnershipWaits",
				"qd_lockWaits","qd_memGrantQueWaits","qd_thdSafeMemObjWaits","qd_logWriteWaits","qd_logBufferWaits",
				"qd_networkIOWaits","qd_pageIOLatchWaits","qd_pageLatchWaits","qd_nonPageLatchWaits",
				"qd_waitForTheWorker","qd_workspaceSynWaits","qd_traOwnershipWaits","lj_lockWaits",
				"lj_memGrantQueWaits","lj_thdSafeMemObjWaits","lj_logWriteWaits","lj_logBufferWaits",
				"lj_networkIOWaits","lj_pageIOLatchWaits","lj_pageLatchWaits","lj_nonPageLatchWaits",
				"lj_waitForTheWorker","lj_workspaceSynWaits","lj_traOwnershipWaits","mon_time"};
		returndata = LogParser.parse(dbmonitorlist, "statisticsHash", args);
		statisticsHash = (Hashtable) ((Vector) returndata.get("statisticsHash")).get(0);
		
		Hashtable retValue = new Hashtable();
		retValue.put("pages", pages);// nms_sqlserverpages
		retValue.put("conns", conns);// nms_sqlserverconns
		retValue.put("locks", locks);// nms_sqlserverlocks
		retValue.put("caches", caches);// nms_sqlservercaches
		retValue.put("mems", mems);// nms_sqlservermems
		retValue.put("sqls", sqls);// nms_sqlserversqls
		retValue.put("scans", scans);// nms_sqlserverscans
		retValue.put("statisticsHash", statisticsHash);
		//sqlserverDataHash.put("retValue", retValue);
		
		//写入内存
		//ShareData.setSqlserverdata(serverip, sqlserverDataHash);
		sqlserverdata.put("retValue", retValue);
		
		//存入数据库
		
		saveSqlServerData(hexip, retValue);
		
		//告警
		NodeUtil nodeUtil = new NodeUtil();
	    NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(dbmonitorlist);
		checkToAlarm(nodeDTO,retValue);
		return retValue;
	}
	
	/*
	 * 存入数据库
	 */
	public void saveSqlServerData(String serverip,Hashtable retValue){
		DBDao dbDao = new DBDao();
		try{
			if(retValue.containsKey("pages")){
				Hashtable pages = (Hashtable)retValue.get("pages");
				addSqlserver_nmspages(serverip,pages);
			}
			if(retValue.containsKey("conns")){
				Hashtable conns = (Hashtable)retValue.get("conns");
				addSqlserver_nmsconns(serverip,conns);
			}
			if(retValue.containsKey("locks")){
				Hashtable locks = (Hashtable)retValue.get("locks");
				addSqlserver_nmslocks(serverip,locks);
			}
			if(retValue.containsKey("caches")){
				Hashtable caches = (Hashtable)retValue.get("caches");
				addSqlserver_nmscaches(serverip,caches);
			}
			if(retValue.containsKey("mems")){
				Hashtable mems = (Hashtable)retValue.get("mems");
				addSqlserver_nmsmems(serverip,mems);
			}
			if(retValue.containsKey("sqls")){
				Hashtable sqls = (Hashtable)retValue.get("sqls");
				addSqlserver_nmssqls(serverip,sqls);
			}
			if(retValue.containsKey("scans")){
				Hashtable scans = (Hashtable)retValue.get("scans");
				addSqlserver_nmsscans(serverip,scans);
			}
			if(retValue.containsKey("statisticsHash")){
				Hashtable statisticsHash = (Hashtable)retValue.get("statisticsHash");
				addSqlserver_nmsstatisticsHash(serverip,statisticsHash);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(dbDao != null){
				dbDao.close();
			}
		}
	}
	
	/**
	 * 将SqlServer缓存信息放入数据库
	 * 
	 * @param serverip
	 * @param caches
	 * @return
	 * @throws Exception
	 */
	public void addSqlserver_nmscaches(String serverip, Hashtable caches)
			throws Exception {
		try {
			String deletesql = "delete from nms_sqlservercaches where serverip = '" + serverip +  "'";
			GathersqlListManager.Addsql(deletesql);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			StringBuffer sBuffer = new StringBuffer();
			sBuffer
					.append("insert into nms_sqlservercaches(serverip, cacheHitRatio, cacheHitRatioBase, "
							+ "cacheCount, cachePages, cacheUsed,cacheUsedRate,mon_time)");
			sBuffer.append(" values('");
			sBuffer.append(serverip);
			sBuffer.append("','");
			sBuffer.append(String.valueOf(caches.get("cacheHitRatio")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(caches.get("cacheHitRatioBase")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(caches.get("cacheCount")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(caches.get("cachePages")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(caches.get("cacheUsed")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(caches.get("cacheUsedRate")));
			// sBuffer.append("','");
			// sBuffer.append(montime);
			// sBuffer.append("')");
			if (SystemConstant.DBType.equals("mysql")) {
				sBuffer.append("','");
				sBuffer.append(String.valueOf(caches.get("mon_time")));
				sBuffer.append("')");
			} else if (SystemConstant.DBType.equals("oracle")) {
				sBuffer.append("',to_date('" + String.valueOf(caches.get("mon_time"))
						+ "','yyyy-mm-dd hh24:mi:ss'))");
			}
			GathersqlListManager.Addsql(sBuffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ;
		}
		return ;
	}

	/**
	 * 将SqlServer的sql统计信息插入数据库
	 * 
	 * @param serverip
	 * @param sqls
	 * @return
	 * @throws Exception
	 */
	public void addSqlserver_nmssqls(String serverip, Hashtable sqls)
			throws Exception {
		try {
			String deletesql = "delete from nms_sqlserversqls where serverip = '" + serverip +  "'";
			GathersqlListManager.Addsql(deletesql);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			StringBuffer sBuffer = new StringBuffer();
			sBuffer
					.append("insert into nms_sqlserversqls(serverip, batchRequests, batchRequestsRate, "
							+ "sqlCompilations, sqlCompilationsRate, sqlRecompilation,sqlRecompilationRate,autoParams,autoParamsRate,failedAutoParams,"
							+ "failedAutoParamsRate,mon_time)");
			sBuffer.append(" values('");
			sBuffer.append(serverip);
			sBuffer.append("','");
			sBuffer.append(String.valueOf(sqls.get("batchRequests")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(sqls.get("batchRequestsRate")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(sqls.get("sqlCompilations")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(sqls.get("sqlCompilationsRate")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(sqls.get("sqlRecompilation")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(sqls.get("sqlRecompilationRate")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(sqls.get("autoParams")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(sqls.get("autoParamsRate")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(sqls.get("failedAutoParams")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(sqls.get("failedAutoParamsRate")));
			// sBuffer.append("','");
			// sBuffer.append(montime);
			// sBuffer.append("')");
			if (SystemConstant.DBType.equals("mysql")) {
				sBuffer.append("','");
				sBuffer.append(String.valueOf(sqls.get("mon_time")));
				sBuffer.append("')");
			} else if (SystemConstant.DBType.equals("oracle")) {
				sBuffer.append("',to_date('" + String.valueOf(sqls.get("mon_time"))
						+ "','yyyy-mm-dd hh24:mi:ss'))");
			}
			GathersqlListManager.Addsql(sBuffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ;
		}
		return ;
	}

	/**
	 * 将SqlServer的内存利用情况插入数据库
	 * 
	 * @param serverip
	 * @param mems
	 * @return
	 * @throws Exception
	 */
	public void addSqlserver_nmsmems(String serverip, Hashtable mems)
			throws Exception {
		try {
			String deletesql = "delete from nms_sqlservermems where serverip = '" + serverip +  "'";
			GathersqlListManager.Addsql(deletesql);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			StringBuffer sBuffer = new StringBuffer();
			sBuffer
					.append("insert into nms_sqlservermems(serverip, totalMemory, sqlMem, "
							+ "optMemory, memGrantPending, memGrantSuccess,lockMem,conMemory,grantedWorkspaceMem,mon_time)");
			sBuffer.append(" values('");
			sBuffer.append(serverip);
			sBuffer.append("','");
			sBuffer.append(String.valueOf(mems.get("totalMemory")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(mems.get("sqlMem")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(mems.get("optMemory")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(mems.get("memGrantPending")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(mems.get("memGrantSuccess")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(mems.get("lockMem")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(mems.get("conMemory")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(mems.get("grantedWorkspaceMem")));
			// sBuffer.append("','");
			// sBuffer.append(montime);
			// sBuffer.append("')");
			if (SystemConstant.DBType.equals("mysql")) {
				sBuffer.append("','");
				sBuffer.append(String.valueOf(mems.get("mon_time")));
				sBuffer.append("')");
			} else if (SystemConstant.DBType.equals("oracle")) {
				sBuffer.append("',to_date('" + String.valueOf(mems.get("mon_time"))
						+ "','yyyy-mm-dd hh24:mi:ss'))");
			}
			GathersqlListManager.Addsql(sBuffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ;
		} 
		return ;
	}

	/**
	 * 将SqlServer的访问方法的明细插入数据库
	 * 
	 * @param serverip
	 * @param scans
	 * @return
	 * @throws Exception
	 */
	public void addSqlserver_nmsscans(String serverip, Hashtable scans)
			throws Exception {
		try {
			String deletesql = "delete from nms_sqlserverscans where serverip = '" + serverip +  "'";
			GathersqlListManager.Addsql(deletesql);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			StringBuffer sBuffer = new StringBuffer();
			sBuffer
					.append("insert into nms_sqlserverscans(serverip, fullScans, fullScansRate, "
							+ "rangeScans, rangeScansRate, probeScans,probeScansRate,mon_time)");
			sBuffer.append(" values('");
			sBuffer.append(serverip);
			sBuffer.append("','");
			sBuffer.append(String.valueOf(scans.get("fullScans")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(scans.get("fullScansRate")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(scans.get("rangeScans")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(scans.get("rangeScansRate")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(scans.get("probeScans")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(scans.get("probeScansRate")));
			// sBuffer.append("','");
			// sBuffer.append(String.valueOf(pages.get("mon_time")));
			// sBuffer.append("')");
			if (SystemConstant.DBType.equals("mysql")) {
				sBuffer.append("','");
				sBuffer.append(String.valueOf(scans.get("mon_time")));
				sBuffer.append("')");
			} else if (SystemConstant.DBType.equals("oracle")) {
				sBuffer.append("',to_date('" + String.valueOf(scans.get("mon_time"))
						+ "','yyyy-mm-dd hh24:mi:ss'))");
			}
			GathersqlListManager.Addsql(sBuffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ;
		}
		return ;
	}

	/**
	 * 
	 * @param serverip
	 * @param statisticsHash
	 * @return
	 * @throws Exception
	 */
	public void addSqlserver_nmsstatisticsHash(String serverip,
			Hashtable statisticsHash) throws Exception {
		try {
			String deletesql = "delete from nms_sqlserverstatisticshash where serverip = '" + serverip +  "'";
			GathersqlListManager.Addsql(deletesql);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			StringBuffer sBuffer = new StringBuffer();
			sBuffer
					.append("insert into nms_sqlserverstatisticshash(serverip, pj_lockWaits , pj_memGrantQueWaits , ");
			sBuffer
					.append("pj_thdSafeMemObjWaits , pj_logWriteWaits, pj_logBufferWaits, pj_networkIOWaits, pj_pageIOLatchWaits,");
			sBuffer
					.append(" pj_pageLatchWaits, pj_nonPageLatchWaits, pj_waitForTheWorker, pj_workspaceSynWaits , pj_traOwnershipWaits , ");
			sBuffer
					.append("jx_lockWaits,jx_memGrantQueWaits ,jx_thdSafeMemObjWaits ,jx_logWriteWaits,jx_logBufferWaits,jx_networkIOWaits,");
			sBuffer
					.append("jx_pageIOLatchWaits,jx_pageLatchWaits,jx_nonPageLatchWaits,jx_waitForTheWorker,jx_workspaceSynWaits ,jx_traOwnershipWaits ,");
			sBuffer
					.append("qd_lockWaits,qd_memGrantQueWaits ,qd_thdSafeMemObjWaits ,qd_logWriteWaits,qd_logBufferWaits,qd_networkIOWaits,qd_pageIOLatchWaits,qd_pageLatchWaits,");
			sBuffer
					.append("qd_nonPageLatchWaits,qd_waitForTheWorker,qd_workspaceSynWaits ,qd_traOwnershipWaits ,lj_lockWaits,lj_memGrantQueWaits ,lj_thdSafeMemObjWaits ,");
			sBuffer
					.append("lj_logWriteWaits,lj_logBufferWaits,lj_networkIOWaits,lj_pageIOLatchWaits,lj_pageLatchWaits,lj_nonPageLatchWaits,lj_waitForTheWorker,lj_workspaceSynWaits ,lj_traOwnershipWaits ,mon_time)");
			sBuffer.append(" values('");
			sBuffer.append(serverip);
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("pingjun_lockWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("pingjun_memoryGrantQueueWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("pingjun_threadSafeMemoryObjectWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("pingjun_logWriteWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("pingjun_logBufferWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("pingjun_networkIOWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("pingjun_pageIOLatchWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("pingjun_pageLatchWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("pingjun_nonPageLatchWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("pingjun_waitForTheWorker")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("pingjun_workspaceSynchronizationWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("pingjun_transactionOwnershipWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("jingxing_lockWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("jingxing_memoryGrantQueueWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("jingxing_threadSafeMemoryObjectWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("jingxing_logWriteWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("jingxing_logBufferWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("jingxing_networkIOWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("jingxing_pageIOLatchWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("jingxing_pageLatchWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("jingxing_nonPageLatchWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("jingxing_waitForTheWorker")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("jingxing_workspaceSynchronizationWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("jingxing_transactionOwnershipWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("qidong_lockWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("qidong_memoryGrantQueueWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("qidong_threadSafeMemoryObjectWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("qidong_logWriteWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("qidong_logBufferWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("qidong_networkIOWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("qidong_pageIOLatchWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("qidong_pageLatchWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("qidong_nonPageLatchWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("qidong_waitForTheWorker")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("qidong_workspaceSynchronizationWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("qidong_transactionOwnershipWaits")));
			sBuffer.append("','");
			sBuffer.append(String
					.valueOf(statisticsHash.get("leiji_lockWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("leiji_memoryGrantQueueWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("leiji_threadSafeMemoryObjectWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("leiji_logWriteWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("leiji_logBufferWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("leiji_networkIOWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("leiji_pageIOLatchWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("leiji_pageLatchWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("leiji_nonPageLatchWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("leiji_waitForTheWorker")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("leiji_workspaceSynchronizationWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("leiji_transactionOwnershipWaits")));
			// sBuffer.append("','");
			// sBuffer.append(montime);
			// sBuffer.append("')");
			if (SystemConstant.DBType.equals("mysql")) {
				sBuffer.append("','");
				sBuffer.append(String.valueOf(statisticsHash.get("mon_time")));
				sBuffer.append("')");
			} else if (SystemConstant.DBType.equals("oracle")) {
				sBuffer.append("',to_date('" + String.valueOf(statisticsHash.get("mon_time"))
						+ "','yyyy-mm-dd hh24:mi:ss'))");
			}
			GathersqlListManager.Addsql(sBuffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ;
		}
		return ;
	}
	
	public boolean addSqlserver_nmslocks(String serverip, Hashtable locks) throws Exception {
		try {
			String deletesql = "delete from nms_sqlserverlocks where serverip = '" + serverip +  "'";
			GathersqlListManager.Addsql(deletesql);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			StringBuffer sBuffer = new StringBuffer();
			sBuffer
					.append("insert into nms_sqlserverlocks(serverip, lockRequests, lockRequestsRate, "
							+ "lockWaits, lockWaitsRate, lockTimeouts,lockTimeoutsRate,deadLocks,deadLocksRate,avgWaitTime,"
							+ "avgWaitTimeBase,latchWaits,latchWaitsRate,avgLatchWait,mon_time)");
			sBuffer.append(" values('");
			sBuffer.append(serverip);
			sBuffer.append("','");
			sBuffer.append(String.valueOf(locks.get("lockRequests")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(locks.get("lockRequestsRate")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(locks.get("lockWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(locks.get("lockWaitsRate")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(locks.get("lockTimeouts")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(locks.get("lockTimeoutsRate")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(locks.get("deadLocks")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(locks.get("deadLocksRate")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(locks.get("avgWaitTime")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(locks.get("avgWaitTimeBase")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(locks.get("latchWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(locks.get("latchWaitsRate")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(locks.get("avgLatchWait")));
			// sBuffer.append("','");
			// sBuffer.append(montime);
			// sBuffer.append("')");
			if (SystemConstant.DBType.equals("mysql")) {
				sBuffer.append("','");
				sBuffer.append(String.valueOf(locks.get("mon_time")));
				sBuffer.append("')");
			} else if (SystemConstant.DBType.equals("oracle")) {
				sBuffer.append("',to_date('" + String.valueOf(locks.get("mon_time"))
						+ "','yyyy-mm-dd hh24:mi:ss'))");
			}
			GathersqlListManager.Addsql(sBuffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
}
	
	public void addSqlserver_nmsconns(String serverip, Hashtable conns)
	throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			String deletesql = "delete from nms_sqlserverconns where serverip = '" + serverip +  "'";
			GathersqlListManager.Addsql(deletesql);
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			StringBuffer sBuffer = new StringBuffer();
			sBuffer
					.append("insert into nms_sqlserverconns(serverip, connections, totalLogins, "
							+ "totalLoginsRate, totalLogouts, totalLogoutsRate,mon_time)");
			sBuffer.append(" values('");
			sBuffer.append(serverip);
			sBuffer.append("','");
			sBuffer.append(String.valueOf(conns.get("connections")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(conns.get("totalLogins")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(conns.get("totalLoginsRate")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(conns.get("totalLogouts")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(conns.get("totalLogoutsRate")));
			// sBuffer.append("','");
			// sBuffer.append(montime);
			// sBuffer.append("')");
			if (SystemConstant.DBType.equals("mysql")) {
				sBuffer.append("','");
				sBuffer.append(String.valueOf(conns.get("mon_time")));
				sBuffer.append("')");
			} else if (SystemConstant.DBType.equals("oracle")) {
				sBuffer.append("',to_date('" + String.valueOf(conns.get("mon_time"))
						+ "','yyyy-mm-dd hh24:mi:ss'))");
			}
			GathersqlListManager.Addsql(sBuffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ;
		}
		return;
		
	}
	
	public void addSqlserver_nmspages(String serverip, Hashtable pages) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			String deletesql = "delete from nms_sqlserverpages where serverip = '" + serverip +  "'";
			GathersqlListManager.Addsql(deletesql);
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			StringBuffer sBuffer = new StringBuffer();
			sBuffer
					.append("insert into nms_sqlserverpages(serverip, bufferCacheHitRatio, planCacheHitRatio, "
							+ "cursorManagerByTypeHitRatio, catalogMetadataHitRatio, dbOfflineErrors, killConnectionErrors, userErrors,"
							+ " infoErrors, sqlServerErrors_total, cachedCursorCounts, cursorCacheUseCounts, cursorRequests_total, "
							+ "activeCursors,cursorMemoryUsage,cursorWorktableUsage,activeOfCursorPlans,dbPages,totalPageLookups,"
							+ "totalPageLookupsRate,totalPageReads,totalPageReadsRate,totalPageWrites,totalPageWritesRate,totalPages,freePages,mon_time)");
			sBuffer.append(" values('");
			sBuffer.append(serverip);
			sBuffer.append("','");
			sBuffer.append(String.valueOf(pages.get("bufferCacheHitRatio")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(pages.get("planCacheHitRatio")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(pages
					.get("cursorManagerByTypeHitRatio")));
			sBuffer.append("','");
			sBuffer
					.append(String
							.valueOf(pages.get("catalogMetadataHitRatio")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(pages.get("dbOfflineErrors")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(pages.get("killConnectionErrors")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(pages.get("userErrors")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(pages.get("infoErrors")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(pages.get("sqlServerErrors_total")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(pages.get("cachedCursorCounts")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(pages.get("cursorCacheUseCounts")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(pages.get("cursorRequests_total")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(pages.get("activeCursors")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(pages.get("cursorMemoryUsage")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(pages.get("cursorWorktableUsage")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(pages.get("activeOfCursorPlans")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(pages.get("dbPages")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(pages.get("totalPageLookups")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(pages.get("totalPageLookupsRate")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(pages.get("totalPageReads")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(pages.get("totalPageReadsRate")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(pages.get("totalPageWrites")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(pages.get("totalPageWritesRate")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(pages.get("totalPages")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(pages.get("freePages")));

			// sBuffer.append("','");
			// sBuffer.append(montime);
			// sBuffer.append("')");
			if (SystemConstant.DBType.equals("mysql")) {
				sBuffer.append("','");
				sBuffer.append(String.valueOf(pages.get("mon_time")));
				sBuffer.append("')");
			} else if (SystemConstant.DBType.equals("oracle")) {
				sBuffer.append("',to_date('" + String.valueOf(pages.get("mon_time"))
						+ "','yyyy-mm-dd hh24:mi:ss'))");
			}
			// //System.out.println(sBuffer.toString());
			GathersqlListManager.Addsql(sBuffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return;
		} 
		return;
	}

	public void checkToAlarm(NodeDTO nodeDTO, Hashtable retValue){
		
		Hashtable memeryHashtable = (Hashtable)retValue.get("pages");//得到缓存管理统计信息
		
		Hashtable locksHashtable = (Hashtable)retValue.get("locks");//得到锁明细信息
		
		Hashtable connsHashtable = (Hashtable)retValue.get("conns");//得到数据库页连接统计
		
		AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
    	List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(nodeDTO.getId()+"", nodeDTO.getType(), nodeDTO.getSubtype());
		CheckEventUtil checkEventUtil = new CheckEventUtil();
		AlarmIndicatorsNode alarmIndicatorsNode = null;
    	for(int i = 0 ; i < list.size(); i++){
    		alarmIndicatorsNode = (AlarmIndicatorsNode)list.get(i);
    		if ("buffercache".equalsIgnoreCase(alarmIndicatorsNode.getName())){
				if(memeryHashtable!=null&&memeryHashtable.get("bufferCacheHitRatio")!=null){
					checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, (String)memeryHashtable.get("bufferCacheHitRatio"));
				}
			} else if ("plancache".equalsIgnoreCase(alarmIndicatorsNode.getName())){
				if(memeryHashtable!=null&&memeryHashtable.get("planCacheHitRatio")!=null){
					checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, (String)memeryHashtable.get("planCacheHitRatio"));
				}
			} else if ("cursormanager".equalsIgnoreCase(alarmIndicatorsNode.getName())){
				if(memeryHashtable!=null&&memeryHashtable.get("cursorManagerByTypeHitRatio")!=null){
					checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, (String)memeryHashtable.get("cursorManagerByTypeHitRatio"));
				}
			} else if ("catalogMetadata".equalsIgnoreCase(alarmIndicatorsNode.getName())){
				if(memeryHashtable!=null&&memeryHashtable.get("catalogMetadataHitRatio")!=null){
					checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, (String)memeryHashtable.get("catalogMetadataHitRatio"));
				}
			} else if ("deadLocks".equalsIgnoreCase(alarmIndicatorsNode.getName())){
				if(locksHashtable!=null&&locksHashtable.get("deadLocks")!=null){
					checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, (String)locksHashtable.get("deadLocks"));
				}
			} else if ("connections".equalsIgnoreCase(alarmIndicatorsNode.getName())){
				if(connsHashtable!=null&&connsHashtable.get("connections")!=null){
					checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, (String)connsHashtable.get("connections"));
				}
			}
    	}
	}
}
