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

public class MonitItemsDetail extends SnmpMonitor{
	private double bufferCacheHitRatio; // *$ 缓存击中率
	private double planCacheHitRatio; // plan Cache 命中率
	private double cursorManagerByTypeHitRatio; // cursor manager 命中率
	private double catalogMetadataHitRatio; // catalogMetadata命中率
	private double dbPages; // * 数据库页
	private double totalPageLookups; // 查找页数
	private double totalPageLookupsRate; // * 查找页数/分
	private double totalPageReads; // 已读页数
	private double totalPageReadsRate; // * 已读页数/分
	private double totalPageWrites; // 已写页数
	private double totalPageWritesRate; // * 已写页数/分
	private double totalPages; // * 总页数
	private double freePages; // * 空闲页
	
	/* 数据库页连接统计 */
	private double connections; // * 活动连接
	private double totalLogins; // 登录
	private double totalLoginsRate; // * 登录/分
	private double totalLogouts; // 退出
	private double totalLogoutsRate; // * 退出/分
	
	/* 锁明细 */
	private double lockRequests; // 锁定请求
	private double lockRequestsRate; // *$ 锁定请求/分
	private double lockWaits; // 锁定等待
	private double lockWaitsRate; // * 锁定等待/分
	private double lockTimeouts; // 锁定超时
	private double lockTimeoutsRate; // * 锁定超时/分
	private double deadLocks; // 死锁数
	private double deadLocksRate; // * 死锁数/分
	private double avgWaitTime; // * 平均锁定等待时间
	private double avgWaitTimeBase; // 平均锁定等待时间计算的基数

	/* 闭锁的明细 */
	private double latchWaits; // 锁定等待
	private double latchWaitsRate; // * 锁定等待/分
	private double avgLatchWait; // *$ 平均闭锁等待时间;
	
	/* 缓存明细 */
	private double cacheHitRatio; // *$ 缓存击中率
	private double cacheHitRatioBase; // 缓存击中率计算的基数
	private double cacheCount; // * 缓存数
	private double cachePages; // * 缓存页
	private double cacheUsed; // 使用的缓存
	private double cacheUsedRate; // * 使用的缓存/分
	
	/* 内存利用情况 */
	private double totalMemory; // *$ 内存总数
	private double sqlMem; // *$ SQL缓存存储
	private double optMemory; // *$ 内存优化
	private double memGrantPending; // * 内存分配未决
	private double memGrantSuccess; // * 内存分配成功
	private double lockMem; // *$ 锁定内存
	private double conMemory; // *$ 连接内存
	private double grantedWorkspaceMem; // * 产生的工作空间内存

	/* SQL统计 */
	private double batchRequests; // 批请求
	private double batchRequestsRate; // *$ 批请求/分
	private double sqlCompilations;
	// SQL编辑
	private double sqlCompilationsRate; // * SQL编辑/分
	private double sqlRecompilation; // SQL重编辑
	private double sqlRecompilationRate; // * SQL重编辑/分
	private double autoParams; // 自动参数化尝试次数
	private double autoParamsRate; // * 自动参数化尝试次数/分
	private double failedAutoParams; // 自动参数化失败次数
	private double failedAutoParamsRate; // * 自动参数化失败次数/分

	/* 访问方法的明细 */
	private double fullScans; // 完全扫描
	private double fullScansRate; // *$ 完全扫描/分
	private double rangeScans; // 范围扫描
	private double rangeScansRate; // * 范围扫描/分
	private double probeScans; // 探针扫描
	private double probeScansRate; // * 探针扫描/分

	// 表锁情况

	private String Table_locks_immediate;
	private String Table_locks_waited;

	private String Key_read_requests;
	private String Key_reads;

	// 线程
	private String Threads_cached;
	private String Threads_connected;
	private String Threads_created;
	private String Threads_running;

	private Vector databaseNames; // 数据库系统中所有数据库的名称
	private HashMap dbNamesAndDetails; // key为数据库的名称(String),value为数据库详细信息(MsSqlServerDatabase)

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
		Hashtable retValue = getData(nodeGatherIndicators);
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
	
	public Hashtable getData(NodeGatherIndicators nodeGatherIndicators){

		boolean returnbool = false;
		String tmpResult[][] = null;
		double hitratio = 0;
		double hitratiobase = 0;
		double tmpDouble = 0.0D;

		double dbOfflineErrors = 0;
		double killConnectionErrors = 0;
		double userErrors = 0;
		double infoErrors = 0;
		double sqlServerErrors_total = 0;

		double cachedCursorCounts = 0; // cachedCursorCounts_total
		double cursorCacheUseCounts = 0;// cursorCacheUseCounts_total
		double cursorRequests_total = 0;
		double activeCursors = 0;// activeCursors_total
		double cursorMemoryUsage = 0;// cursorMemoryUsage_total
		double cursorWorktableUsage = 0;// cursorWorktableUsage_total
		double activeOfCursorPlans = 0;// Number of active cursor plans_total

		double pingjun_lockWaits = 0;
		double pingjun_memoryGrantQueueWaits = 0;
		double pingjun_threadSafeMemoryObjectWaits = 0;
		double pingjun_logWriteWaits = 0;
		double pingjun_logBufferWaits = 0;
		double pingjun_networkIOWaits = 0;
		double pingjun_pageIOLatchWaits = 0;
		double pingjun_pageLatchWaits = 0;
		double pingjun_nonPageLatchWaits = 0;
		double pingjun_waitForTheWorker = 0;
		double pingjun_workspaceSynchronizationWaits = 0;
		double pingjun_transactionOwnershipWaits = 0;

		double jingxing_lockWaits = 0;
		double jingxing_memoryGrantQueueWaits = 0;
		double jingxing_threadSafeMemoryObjectWaits = 0;
		double jingxing_logWriteWaits = 0;
		double jingxing_logBufferWaits = 0;
		double jingxing_networkIOWaits = 0;
		double jingxing_pageIOLatchWaits = 0;
		double jingxing_pageLatchWaits = 0;
		double jingxing_nonPageLatchWaits = 0;
		double jingxing_waitForTheWorker = 0;
		double jingxing_workspaceSynchronizationWaits = 0;
		double jingxing_transactionOwnershipWaits = 0;

		double qidong_lockWaits = 0;
		double qidong_memoryGrantQueueWaits = 0;
		double qidong_threadSafeMemoryObjectWaits = 0;
		double qidong_logWriteWaits = 0;
		double qidong_logBufferWaits = 0;
		double qidong_networkIOWaits = 0;
		double qidong_pageIOLatchWaits = 0;
		double qidong_pageLatchWaits = 0;
		double qidong_nonPageLatchWaits = 0;
		double qidong_waitForTheWorker = 0;
		double qidong_workspaceSynchronizationWaits = 0;
		double qidong_transactionOwnershipWaits = 0;

		double leiji_lockWaits = 0;
		double leiji_memoryGrantQueueWaits = 0;
		double leiji_threadSafeMemoryObjectWaits = 0;
		double leiji_logWriteWaits = 0;
		double leiji_logBufferWaits = 0;
		double leiji_networkIOWaits = 0;
		double leiji_pageIOLatchWaits = 0;
		double leiji_pageLatchWaits = 0;
		double leiji_nonPageLatchWaits = 0;
		double leiji_waitForTheWorker = 0;
		double leiji_workspaceSynchronizationWaits = 0;
		double leiji_transactionOwnershipWaits = 0;
		Hashtable gatherHash = new Hashtable();
		gatherHash.put(nodeGatherIndicators.getName(), nodeGatherIndicators);

		long tmpLong = 0L;
		double tmpdou = 0;
		
		JdbcUtil util = null;
		ResultSet rs = null;
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
		String username = dbnode.getUser();
		String port = dbnode.getPort();
		String password = "";
		try {
			password = EncryptUtil.decode(dbnode.getPassword());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String dburl = "jdbc:jtds:sqlserver://" + serverip + ":" + port + ";DatabaseName=master;charset=GBK;SelectMethod=CURSOR";
		
		String sql = "select a.name,b.rows from sysobjects a,sysindexes b where a.type='u' and b.id=object_id(a.name) and indid in(0,1) order by a.name;";


		// long tmpAppPollInterval = (System.currentTimeMillis()-
		// getLastUpdatetime())/1000;
		long tmpAppPollInterval = getTaskInterval();
		PersistenceServiceable service = PersistenceService.getInstance();

		/*
		 * 取得系统表master..sysperfinfo的信息 第一列为object_name: 性能对象名，如 SQL Server：锁管理器或
		 * SQL Server：缓冲区管理器 第二列为counter_name: 对象内的性能计数器名称，如页请求或请求的锁
		 * 第三列为instance_name:
		 * 计数器的命名实例。例如，有为各类型的锁（如表锁、页锁、键锁等）维护的计数器。实例名在相似的计数器之间是有区别的 第四列为value:
		 * 结合cntr_value，cntr_type属性，经过分析和计算得到的性能值
		 */
		String queryStr = "SELECT perf1.object_name, perf1.counter_name, perf1.instance_name, "
				+ "'value' = CASE perf1.cntr_type WHEN 537003008 THEN CONVERT(FLOAT,perf1.cntr_value) /(SELECT CASE perf2.cntr_value "
				+ "WHEN 0 THEN 1 ELSE perf2.cntr_value END "
				+ "FROM master..sysperfinfo perf2 "
				+ "WHERE (perf1.counter_name + ' '= SUBSTRING(perf2.counter_name,1,PATINDEX('% base%', perf2.counter_name))) "
				+ "AND (perf1.instance_name = perf2.instance_name)  AND (perf2.cntr_type = 1073939459)) "
				+ "ELSE perf1.cntr_value  END "
				+ "FROM master..sysperfinfo perf1 "
				+ "WHERE (perf1.cntr_type <> 1073939459)";

		try {
			util = new JdbcUtil(dburl, username, password);

			tmpResult = service.executeQuery(queryStr, util.jdbc());


			for (int i = 1; i < tmpResult.length; i++) {

				// 判断是否采集 缓存击中率
				if (gatherHash.containsKey("bufferhit")) {
					/* 缓存击中率 */
					tmpDouble = 0;
					hitratiobase = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Buffer cache hit ratio")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						hitratio = tmpDouble;
						// bufferCacheHitRatio = formatDouble(tmpDouble * 100D);
					}
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Buffer cache hit ratio base")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						hitratiobase = tmpDouble;
						// bufferCacheHitRatio = formatDouble(tmpDouble * 100D);

					}
					try {
						if (hitratiobase != 0)
							bufferCacheHitRatio = formatDouble((hitratio / hitratiobase) * 100D);
					} catch (Exception ex) {

					}
				}

				// 判断是否采集 plan Cache 命中率
				if (gatherHash.containsKey("planhit")) {
					// plan Cache 命中率
					tmpDouble = 0;
					hitratiobase = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Plan Cache")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"cache hit ratio")
							&& tmpResult[i][2].trim()
									.equalsIgnoreCase("_Total")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						hitratio = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Plan Cache")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"cache hit ratio base")
							&& tmpResult[i][2].trim()
									.equalsIgnoreCase("_Total")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						hitratiobase = tmpDouble;
						// bufferCacheHitRatio = formatDouble(tmpDouble * 100D);

					}
					try {
						if (hitratiobase != 0)
							planCacheHitRatio = formatDouble((hitratio / hitratiobase) * 100D);
					} catch (Exception ex) {

					}
				}

				// 判断是否采集 cursor manager 命中率
				if (gatherHash.containsKey("cursorhit")) {
					// cursor manager 命中率
					tmpDouble = 0;
					hitratiobase = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Cursor Manager by Type")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"cache hit ratio")
							&& tmpResult[i][2].trim()
									.equalsIgnoreCase("_Total")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						hitratio = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Cursor Manager by Type")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"cache hit ratio base")
							&& tmpResult[i][2].trim()
									.equalsIgnoreCase("_Total")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						hitratiobase = tmpDouble;
						// bufferCacheHitRatio = formatDouble(tmpDouble * 100D);

					}
					try {
						if (hitratiobase != 0)
							cursorManagerByTypeHitRatio = formatDouble((hitratio / hitratiobase) * 100D);
					} catch (Exception ex) {

					}
				}

				// 判断是否采集 catalogMetadata命中率
				if (gatherHash.containsKey("cataloghit")) {
					// catalogMetadata命中率
					tmpDouble = 0;
					hitratiobase = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Catalog Metadata")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Cache Hit Ratio")
							&& tmpResult[i][2].trim()
									.equalsIgnoreCase("_Total")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						hitratio = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Catalog Metadata")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Cache Hit Ratio Base")
							&& tmpResult[i][2].trim()
									.equalsIgnoreCase("_Total")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						hitratiobase = tmpDouble;
						// bufferCacheHitRatio = formatDouble(tmpDouble * 100D);

					}
					try {
						if (hitratiobase != 0)
							catalogMetadataHitRatio = formatDouble((hitratio / hitratiobase) * 100D);
					} catch (Exception ex) {

					}
				}

				// 判断是否采集 ERRORS
				if (gatherHash.containsKey("error")) {
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:SQL Errors")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Errors/sec")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"DB Offline Errors")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						dbOfflineErrors = tmpDouble;
					}
					tmpDouble = 0;
					//
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:SQL Errors")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Errors/sec")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"Kill Connection Errors")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						killConnectionErrors = tmpDouble;
					}
					tmpDouble = 0;
					//
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:SQL Errors")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Errors/sec")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"User Errors")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						userErrors = tmpDouble;
					}
					tmpDouble = 0;
					String ss = tmpResult[i][2];
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:SQL Errors")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Errors/sec")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"Info Errors")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						infoErrors = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:SQL Errors")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Errors/sec")
							&& tmpResult[i][2].trim()
									.equalsIgnoreCase("_Total")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						sqlServerErrors_total = tmpDouble;
					}
				}

				// 判断是否采集 游标
				if (gatherHash.containsKey("cursor")) {
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Cursor Manager by Type")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Cached Cursor Counts")
							&& tmpResult[i][2].trim()
									.equalsIgnoreCase("_Total")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						cachedCursorCounts = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Cursor Manager by Type")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Cursor Cache Use Counts/sec")
							&& tmpResult[i][2].trim()
									.equalsIgnoreCase("_Total")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						cursorCacheUseCounts = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Cursor Manager by Type")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Cursor Requests/sec")
							&& tmpResult[i][2].trim()
									.equalsIgnoreCase("_Total")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						cursorRequests_total = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Cursor Manager by Type")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Active cursors")
							&& tmpResult[i][2].trim()
									.equalsIgnoreCase("_Total")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						activeCursors = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Cursor Manager by Type")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Cursor memory usage")
							&& tmpResult[i][2].trim()
									.equalsIgnoreCase("_Total")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						cursorMemoryUsage = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Cursor Manager by Type")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Cursor worktable usage")
							&& tmpResult[i][2].trim()
									.equalsIgnoreCase("_Total")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						cursorWorktableUsage = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Cursor Manager by Type")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Number of active cursor plans")
							&& tmpResult[i][2].trim()
									.equalsIgnoreCase("_Total")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						activeOfCursorPlans = tmpDouble;
					}
				}

				// 判断是否采集 等待信息
				if (gatherHash.containsKey("wait")) {
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Lock waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"平均等待时间(ms)")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						pingjun_lockWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Memory grant queue waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"平均等待时间(ms)")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						pingjun_memoryGrantQueueWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Thread-safe momory objects waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"平均等待时间(ms)")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						pingjun_threadSafeMemoryObjectWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Log write waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"平均等待时间(ms)")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						pingjun_logWriteWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Log buffer waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"平均等待时间(ms)")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						pingjun_logBufferWaits = tmpDouble;
					}

					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Network IO waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"平均等待时间(ms)")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						pingjun_networkIOWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Page IO latch waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"平均等待时间(ms)")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						pingjun_pageIOLatchWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Page latch waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"平均等待时间(ms)")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						pingjun_pageLatchWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Non-Page latch waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"平均等待时间(ms)")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						pingjun_nonPageLatchWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Wait for the worker")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"平均等待时间(ms)")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						pingjun_waitForTheWorker = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Workspace synchronization waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"平均等待时间(ms)")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						pingjun_workspaceSynchronizationWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Transaction ownership waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"平均等待时间(ms)")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						pingjun_transactionOwnershipWaits = tmpDouble;
					}
					tmpDouble = 0;
					// /////////////////////////////////////////////

					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Lock waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"正在进行的等待数")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						jingxing_lockWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Memory grant queue waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"正在进行的等待数")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						jingxing_memoryGrantQueueWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Thread-safe momory objects waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"正在进行的等待数")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						jingxing_threadSafeMemoryObjectWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Log write waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"正在进行的等待数")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						jingxing_logWriteWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Log buffer waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"正在进行的等待数")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						jingxing_logBufferWaits = tmpDouble;
					}
					tmpDouble = 0;

					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Network IO waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"正在进行的等待数")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						jingxing_networkIOWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Page IO latch waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"正在进行的等待数")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						jingxing_pageIOLatchWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Page latch waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"正在进行的等待数")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						jingxing_pageLatchWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Non-Page latch waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"正在进行的等待数")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						jingxing_nonPageLatchWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Wait for the worker")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"正在进行的等待数")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						jingxing_waitForTheWorker = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Workspace synchronization waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"正在进行的等待数")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						jingxing_workspaceSynchronizationWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Transaction ownership waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"正在进行的等待数")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						jingxing_transactionOwnershipWaits = tmpDouble;
					}

					// //////////////////////////////////////
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Lock waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"每秒启动的等待数")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						qidong_lockWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Memory grant queue waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"每秒启动的等待数")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						qidong_memoryGrantQueueWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Thread-safe momory objects waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"每秒启动的等待数")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						qidong_threadSafeMemoryObjectWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Log write waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"每秒启动的等待数")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						qidong_logWriteWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Log buffer waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"每秒启动的等待数")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						qidong_logBufferWaits = tmpDouble;
					}

					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Network IO waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"每秒启动的等待数")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						qidong_networkIOWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Page IO latch waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"每秒启动的等待数")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						qidong_pageIOLatchWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Page latch waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"每秒启动的等待数")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						qidong_pageLatchWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Non-Page latch waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"每秒启动的等待数")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						qidong_nonPageLatchWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Wait for the worker")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"每秒启动的等待数")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						qidong_waitForTheWorker = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Workspace synchronization waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"每秒启动的等待数")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						qidong_workspaceSynchronizationWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Transaction ownership waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"每秒启动的等待数")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						qidong_transactionOwnershipWaits = tmpDouble;
					}
					// //////////////////////////////////////////////////////////////////////
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Lock waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"每秒的累积等待时间")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						leiji_lockWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Memory grant queue waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"每秒的累积等待时间")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						leiji_memoryGrantQueueWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Thread-safe momory objects waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"每秒的累积等待时间")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						leiji_threadSafeMemoryObjectWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Log write waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"每秒的累积等待时间")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						leiji_logWriteWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Log buffer waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"每秒的累积等待时间")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						leiji_logBufferWaits = tmpDouble;
					}

					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Network IO waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"每秒的累积等待时间")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						leiji_networkIOWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Page IO latch waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"每秒的累积等待时间")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						leiji_pageIOLatchWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Page latch waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"每秒的累积等待时间")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						leiji_pageLatchWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Non-Page latch waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"每秒的累积等待时间")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						leiji_nonPageLatchWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Wait for the worker")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"每秒的累积等待时间")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						leiji_waitForTheWorker = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Workspace synchronization waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"每秒的累积等待时间")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						leiji_workspaceSynchronizationWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Transaction ownership waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"每秒的累积等待时间")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						leiji_transactionOwnershipWaits = tmpDouble;
					}
				}

				// 判断是否采集 页信息
				if (gatherHash.containsKey("page")) {
					/* 数据库页 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Database pages")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						dbPages = tmpDouble;
					}
					/* 查找页数/分 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Page lookups/sec")
							|| tmpResult[i][1].trim().equalsIgnoreCase(
									"Page Requests/sec")) {
						if (totalPageLookups == 0L) {
							totalPageLookups = new Double(tmpResult[i][3])
									.doubleValue();
							totalPageLookupsRate = 0.0D;
						} else {
							tmpdou = totalPageLookups;
							totalPageLookups = new Double(tmpResult[i][3])
									.doubleValue();
							totalPageLookupsRate = calculateRate(
									tmpAppPollInterval, totalPageLookups,
									tmpdou);
						}
					}
					/* 已读页数/分 */

					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Page reads/sec")) {
						if (totalPageReads == 0) {
							totalPageReads = new Double(tmpResult[i][3])
									.doubleValue();
							totalPageReadsRate = 0.0D;
						} else {
							tmpdou = totalPageReads;
							totalPageReads = new Double(tmpResult[i][3])
									.doubleValue();
							totalPageReadsRate = calculateRate(
									tmpAppPollInterval, totalPageReads, tmpdou);
						}
					}
					/* 已写页数/分 */
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Page writes/sec")) {
						if (totalPageWrites == 0) {
							totalPageWrites = new Double(tmpResult[i][3])
									.doubleValue();
							totalPageWritesRate = 0.0D;
						} else {
							tmpdou = totalPageWrites;
							totalPageWrites = new Double(tmpResult[i][3])
									.doubleValue();
							totalPageWritesRate = calculateRate(
									tmpAppPollInterval, totalPageWrites, tmpdou);
						}
					}
					/* 总页数 */
					if (tmpResult[i][1].trim().equalsIgnoreCase("Total pages")) {
						tmpdou = new Double(tmpResult[i][3]).doubleValue();
						totalPages = tmpdou;
					}
					/* 空闲页 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase("Free pages")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						freePages = tmpDouble;
					}
				}

				// 判断是否采集 活动连接信息
				if (gatherHash.containsKey("connect")) {
					/* 活动连接 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"User Connections")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						connections = tmpDouble;
					}
					/* 登录/分 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase("Logins/sec")) {
						if (totalLogins == 0.0D) {
							totalLogins = new Double(tmpResult[i][3])
									.doubleValue();
							totalLoginsRate = 0.0D;
						} else {
							tmpDouble = totalLogins;
							totalLogins = new Double(tmpResult[i][3])
									.doubleValue();
							totalLoginsRate = calculateRate(tmpAppPollInterval,
									totalLogins, tmpDouble);
						}
					}
					/* 退出/分 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase("Logouts/sec")) {
						if (totalLogouts == 0.0D) {
							totalLogouts = new Double(tmpResult[i][3])
									.doubleValue();
							totalLogoutsRate = 0.0D;
						} else {
							tmpDouble = totalLogouts;
							totalLogouts = new Double(tmpResult[i][3])
									.doubleValue();
							totalLogoutsRate = calculateRate(
									tmpAppPollInterval, totalLogouts, tmpDouble);
						}
					}
				}

				// 判断是否采集 锁汇总信息
				if (gatherHash.containsKey("locktotal")) {
					/* 锁定请求/分 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Lock Requests/sec")
							&& tmpResult[i][2].trim()
									.equalsIgnoreCase("_Total")) {
						if (lockRequests == 0.0D) {
							lockRequests = new Double(tmpResult[i][3])
									.doubleValue();
							lockRequestsRate = 0.0D;
						} else {
							tmpDouble = lockRequests;
							lockRequests = new Double(tmpResult[i][3])
									.doubleValue();
							lockRequestsRate = calculateRate(
									tmpAppPollInterval, lockRequests, tmpDouble);
						}
					}
					/* 锁定等待/分 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Lock Waits/sec")
							&& tmpResult[i][2].trim()
									.equalsIgnoreCase("_Total")) {
						if (lockWaits == 0.0D) {
							lockWaits = new Double(tmpResult[i][3])
									.doubleValue();
							lockWaitsRate = 0.0D;
						} else {
							tmpDouble = lockWaits;
							lockWaits = new Double(tmpResult[i][3])
									.doubleValue();
							lockWaitsRate = calculateRate(tmpAppPollInterval,
									lockWaits, tmpDouble);
						}
					}
					/* 锁定超时/分 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Lock Timeouts/sec")
							&& tmpResult[i][2].trim()
									.equalsIgnoreCase("_Total")) {
						if (lockTimeouts == 0.0D) {
							lockTimeouts = new Double(tmpResult[i][3])
									.doubleValue();
							lockTimeoutsRate = 0.0D;
						} else {
							tmpDouble = lockTimeouts;
							lockTimeouts = new Double(tmpResult[i][3])
									.doubleValue();
							lockTimeoutsRate = calculateRate(
									tmpAppPollInterval, lockTimeouts, tmpDouble);
						}
					}
					/* 死锁数/分 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Number of Deadlocks/sec")
							&& tmpResult[i][2].trim()
									.equalsIgnoreCase("_Total")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						deadLocks = tmpDouble;
						if (totalLogouts == 0.0D) {
							totalLogouts = new Double(tmpResult[i][3])
									.doubleValue();
							totalLogoutsRate = 0.0D;
						} else {
							tmpDouble = totalLogouts;
							totalLogouts = new Double(tmpResult[i][3])
									.doubleValue();
							totalLogoutsRate = calculateRate(
									tmpAppPollInterval, totalLogouts, tmpDouble);
						}
					}
					/* 平均锁定等待时间 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Average Wait Time (ms)")
							&& tmpResult[i][2].trim()
									.equalsIgnoreCase("_Total")) {
						if (!tmpResult[i][3].equals("")) {
							tmpDouble = new Double(tmpResult[i][3])
									.doubleValue();
							avgWaitTime = formatDouble(tmpDouble);
						} else {
							avgWaitTime = 0;
						}
					}
					/* 锁定等待/分 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Latch Waits/sec")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						latchWaits = tmpDouble;
						if (latchWaits == 0.0D) {
							latchWaits = new Double(tmpResult[i][3])
									.doubleValue();
							latchWaitsRate = 0.0D;
						} else {
							tmpDouble = latchWaits;
							latchWaits = new Double(tmpResult[i][3])
									.doubleValue();
							latchWaitsRate = calculateRate(tmpAppPollInterval,
									latchWaits, tmpDouble);
						}
					}
					/* 平均闭锁等待时间 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Average Latch Wait Time")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						avgLatchWait = formatDouble(tmpDouble);
					}
				}

				// 判断是否采集 缓存信息
				if (gatherHash.containsKey("cache")) {
					/* 缓存击中率 */
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Cache Hit Ratio")
							&& tmpResult[i][2].trim()
									.equalsIgnoreCase("_Total")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						cacheHitRatio = formatDouble(tmpDouble * 100D);
					}

					/* 缓存数 */
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Cache Object Counts")
							&& tmpResult[i][2].trim()
									.equalsIgnoreCase("_Total")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						cacheCount = tmpDouble;
					}
					/* 缓存页 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase("Cache Pages")
							&& tmpResult[i][2].trim()
									.equalsIgnoreCase("_Total")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						cachePages = tmpDouble;
					}
					/* 使用的缓存/秒 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Cache Use Counts/sec")
							&& tmpResult[i][2].trim()
									.equalsIgnoreCase("_Total")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						cacheUsed = tmpDouble;
						if (cacheUsed == 0.0D) {
							cacheUsed = new Double(tmpResult[i][3])
									.doubleValue();
							cacheUsedRate = 0.0D;
						} else {
							tmpDouble = cacheUsed;
							cacheUsed = new Double(tmpResult[i][3])
									.doubleValue();
							cacheUsedRate = calculateRate(tmpAppPollInterval,
									cacheUsed, tmpDouble);
						}
					}
				}

				// 判断是否采集内存信息
				if (gatherHash.containsKey("memory")) {
					/* 内存总数 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Total Server Memory (KB)")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						totalMemory = tmpDouble;
					}
					/* SQL缓存存储 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"SQL Cache Memory (KB)")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						sqlMem = tmpDouble;
					}
					/* 内存优化 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Optimizer Memory (KB)")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						optMemory = tmpDouble;
					}
					/* 内存分配未决 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Memory Grants Pending")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						memGrantPending = tmpDouble;
					}
					/* 内存分配成功 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Memory Grants Outstanding")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						memGrantPending = tmpDouble;
					}
					/* 锁定内存 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Lock Memory (KB)")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						lockMem = tmpDouble;
					}
					/* 连接内存 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Connection Memory (KB)")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						conMemory = tmpDouble;
					}
					/* 产生的工作空间内存 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Granted Workspace Memory (KB)")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						grantedWorkspaceMem = tmpDouble;
					}
				}

				// 判断是否采集SQL信息
				if (gatherHash.containsKey("sql")) {
					/* 批请求/秒 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Batch Requests/sec")) {
						if (batchRequests == 0.0D) {
							batchRequests = new Double(tmpResult[i][3])
									.doubleValue();
							batchRequestsRate = 0.0D;
						} else {
							tmpDouble = batchRequests;
							batchRequests = new Double(tmpResult[i][3])
									.doubleValue();
							batchRequestsRate = calculateRate(
									tmpAppPollInterval, batchRequests,
									tmpDouble);
						}
					}
					/* SQL编辑/秒 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"SQL Compilations/sec")) {
						if (sqlCompilations == 0.0D) {
							sqlCompilations = new Double(tmpResult[i][3])
									.doubleValue();
							sqlCompilationsRate = 0.0D;
						} else {
							tmpDouble = sqlCompilations;
							sqlCompilations = new Double(tmpResult[i][3])
									.doubleValue();
							sqlCompilationsRate = calculateRate(
									tmpAppPollInterval, sqlCompilations,
									tmpDouble);
						}
					}
					/* SQL重编辑/秒 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"SQL Re-Compilations/sec")) {
						if (sqlRecompilation == 0.0D) {
							sqlRecompilation = new Double(tmpResult[i][3])
									.doubleValue();
							sqlRecompilationRate = 0.0D;
						} else {
							tmpDouble = sqlRecompilation;
							sqlRecompilation = new Double(tmpResult[i][3])
									.doubleValue();
							sqlRecompilationRate = calculateRate(
									tmpAppPollInterval, sqlRecompilation,
									tmpDouble);
						}
					}
					/* 自动参数化尝试次数/秒 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Auto-Param Attempts/sec")) {
						if (autoParams == 0.0D) {
							autoParams = new Double(tmpResult[i][3])
									.doubleValue();
							autoParamsRate = 0.0D;
						} else {
							tmpDouble = autoParams;
							autoParams = new Double(tmpResult[i][3])
									.doubleValue();
							autoParamsRate = calculateRate(tmpAppPollInterval,
									autoParams, tmpDouble);
						}
					}
					/* 自动参数化失败次数/秒 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Auto-Param Attempts/sec")) {
						if (failedAutoParams == 0.0D) {
							failedAutoParams = new Double(tmpResult[i][3])
									.doubleValue();
							failedAutoParamsRate = 0.0D;
						} else {
							tmpDouble = failedAutoParams;
							failedAutoParams = new Double(tmpResult[i][3])
									.doubleValue();
							failedAutoParamsRate = calculateRate(
									tmpAppPollInterval, failedAutoParams,
									tmpDouble);
						}
					}
				}

				// 判断是否采集SQL信息
				if (gatherHash.containsKey("scan")) {
					/* 完全扫描/分 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Full Scans/sec")) {
						if (fullScans == 0.0D) {
							fullScans = new Double(tmpResult[i][3])
									.doubleValue();
							fullScansRate = 0.0D;
						} else {
							tmpDouble = fullScans;
							fullScans = new Double(tmpResult[i][3])
									.doubleValue();
							fullScansRate = calculateRate(tmpAppPollInterval,
									fullScans, tmpDouble);
						}
					}
					/* 范围扫描/分 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Range Scans/sec")) {
						if (rangeScans == 0.0D) {
							rangeScans = new Double(tmpResult[i][3])
									.doubleValue();
							rangeScansRate = 0.0D;
						} else {
							tmpDouble = rangeScans;
							rangeScans = new Double(tmpResult[i][3])
									.doubleValue();
							rangeScansRate = calculateRate(tmpAppPollInterval,
									rangeScans, tmpDouble);
						}
					}
					/* 探针扫描/分 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Probe Scans/sec")) {
						if (probeScans == 0.0D) {
							probeScans = new Double(tmpResult[i][3])
									.doubleValue();
							probeScansRate = 0.0D;
						} else {
							tmpDouble = probeScans;
							probeScans = new Double(tmpResult[i][3])
									.doubleValue();
							probeScansRate = calculateRate(tmpAppPollInterval,
									probeScans, tmpDouble);
						}
					}
				}

			}
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			util.closeStmt();
			util.closeConn();

		}

		/* 未取到"_Total"库的值则取其他库的总和，锁明细，缓存管理统计 */
		if (cacheHitRatio == 0.0D) {

			queryStr = "select counter_name ,sum(cntr_value) from sysperfinfo "
					+ "where object_name in ('SQLServer:Locks','SQLServer:Cache Manager') "
					+ "and instance_name!='_Total' group by counter_name";
			try {
				util = new JdbcUtil(dburl, username, password);
				String[][] tmpResult2 = service.executeQuery(queryStr, util
						.jdbc());
				for (int i = 1; i < tmpResult2.length; i++) {
					/* 锁定请求/分 */
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Lock Requests/sec")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						lockRequests = tmpDouble;
					}
					/* 锁定等待/分 */
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Lock Waits/sec")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						lockWaits = tmpDouble;
					}
					/* 锁定超时/分 */
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Lock Timeouts/sec")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						lockTimeouts = tmpDouble;
					}
					/* 死锁数/分 */
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Number of Deadlocks/sec")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						deadLocks = tmpDouble;
					}
					/* 平均锁定等待时间 */
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Average Wait Time (ms)")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						avgWaitTime = formatDouble(tmpDouble);
					}
					/* 缓存击中率 */
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Cache Hit Ratio")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						cacheHitRatio = formatDouble(tmpDouble * 100D);
					}
					/* 缓存数 */
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Cache Object Counts")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						cacheCount = tmpDouble;
					}
					/* 缓存页 */
					if (tmpResult[i][1].trim().equalsIgnoreCase("Cache Pages")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						cachePages = tmpDouble;
					}
					/* 使用的缓存/秒 */
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Cache Use Counts/sec")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						cacheUsed = tmpDouble;
					}
					/* 平均锁定等待时间基数 */
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Average Wait Time Base")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						avgWaitTimeBase = tmpDouble;
					}
					/* 缓存击中率基数 */
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Cache Hit Ratio Base")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						cacheHitRatioBase = tmpDouble;
					}

					if (cacheHitRatio != 0.0D)
						cacheHitRatio = calculateValue(cacheHitRatio,
								cacheHitRatioBase, true);
					if (avgWaitTime != 0.0D)
						avgWaitTime = calculateValue(avgWaitTime,
								avgWaitTimeBase, false);
				}
				if (tmpResult2 != null)
					tmpResult2 = null;
			} catch (Exception e2) {
				e2.printStackTrace();
			} finally {
				if (rs != null) {
					try {
						rs.close();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
				util.closeStmt();
				util.closeConn();
			}
		}
		Hashtable retValue = new Hashtable();
		/* 缓存管理统计 */
		Hashtable pages = new Hashtable();
		pages.put("bufferCacheHitRatio", bufferCacheHitRatio + ""); // *$ 缓存击中率
		pages.put("planCacheHitRatio", planCacheHitRatio + ""); // *$ plan
		// cache击中率
		pages.put("cursorManagerByTypeHitRatio", cursorManagerByTypeHitRatio
				+ ""); // *$ Cursor Manager by Type击中率
		pages.put("catalogMetadataHitRatio", catalogMetadataHitRatio + ""); // *$
		// Catalog
		// Metadata击中率

		pages.put("dbOfflineErrors", dbOfflineErrors + "");
		pages.put("killConnectionErrors", killConnectionErrors + "");
		pages.put("userErrors", userErrors + "");
		pages.put("infoErrors", infoErrors + "");
		pages.put("sqlServerErrors_total", sqlServerErrors_total + "");

		pages.put("cachedCursorCounts", cachedCursorCounts + "");
		pages.put("cursorCacheUseCounts", cursorCacheUseCounts + "");
		pages.put("cursorRequests_total", cursorRequests_total + "");
		pages.put("activeCursors", activeCursors + "");
		pages.put("cursorMemoryUsage", cursorMemoryUsage + "");
		pages.put("cursorWorktableUsage", cursorWorktableUsage + "");
		pages.put("activeOfCursorPlans", activeOfCursorPlans + "");

		pages.put("dbPages", dbPages + "");// * 数据库页
		pages.put("totalPageLookups", totalPageLookups + "");// 查找页数
		pages.put("totalPageLookupsRate", totalPageLookupsRate + "");// *
		// 查找页数/分
		pages.put("totalPageReads", totalPageReads + "");// 已读页数
		pages.put("totalPageReadsRate", totalPageReadsRate + ""); // * 已读页数/分
		pages.put("totalPageWrites", totalPageWrites + "");// 已写页数
		pages.put("totalPageWritesRate", totalPageWritesRate + "");// * 已写页数/分
		pages.put("totalPages", totalPages + "");// * 总页数
		pages.put("freePages", freePages + "");// * 空闲页

		Hashtable statisticsHash = new Hashtable();
		statisticsHash.put("pingjun_lockWaits", pingjun_lockWaits + "");
		statisticsHash.put("pingjun_memoryGrantQueueWaits",
				pingjun_memoryGrantQueueWaits + "");
		statisticsHash.put("pingjun_threadSafeMemoryObjectWaits",
				pingjun_threadSafeMemoryObjectWaits + "");
		statisticsHash.put("pingjun_logWriteWaits", pingjun_logWriteWaits + "");
		statisticsHash.put("pingjun_logBufferWaits", pingjun_logBufferWaits
				+ "");
		statisticsHash.put("pingjun_networkIOWaits", pingjun_networkIOWaits
				+ "");
		statisticsHash.put("pingjun_pageIOLatchWaits", pingjun_pageIOLatchWaits
				+ "");
		statisticsHash.put("pingjun_pageLatchWaits", pingjun_pageLatchWaits
				+ "");
		statisticsHash.put("pingjun_nonPageLatchWaits",
				pingjun_nonPageLatchWaits + "");
		statisticsHash.put("pingjun_waitForTheWorker", pingjun_waitForTheWorker
				+ "");
		statisticsHash.put("pingjun_workspaceSynchronizationWaits",
				pingjun_workspaceSynchronizationWaits + "");
		statisticsHash.put("pingjun_transactionOwnershipWaits",
				pingjun_transactionOwnershipWaits + "");

		statisticsHash.put("jingxing_lockWaits", jingxing_lockWaits + "");
		statisticsHash.put("jingxing_memoryGrantQueueWaits",
				jingxing_memoryGrantQueueWaits + "");
		statisticsHash.put("jingxing_threadSafeMemoryObjectWaits",
				jingxing_threadSafeMemoryObjectWaits + "");
		statisticsHash.put("jingxing_logWriteWaits", jingxing_logWriteWaits
				+ "");
		statisticsHash.put("jingxing_logBufferWaits", jingxing_logBufferWaits
				+ "");
		statisticsHash.put("jingxing_networkIOWaits", jingxing_networkIOWaits
				+ "");
		statisticsHash.put("jingxing_pageIOLatchWaits",
				jingxing_pageIOLatchWaits + "");
		statisticsHash.put("jingxing_pageLatchWaits", jingxing_pageLatchWaits
				+ "");
		statisticsHash.put("jingxing_nonPageLatchWaits",
				jingxing_nonPageLatchWaits + "");
		statisticsHash.put("jingxing_waitForTheWorker",
				jingxing_waitForTheWorker + "");
		statisticsHash.put("jingxing_workspaceSynchronizationWaits",
				jingxing_workspaceSynchronizationWaits + "");
		statisticsHash.put("jingxing_transactionOwnershipWaits",
				jingxing_transactionOwnershipWaits + "");

		statisticsHash.put("qidong_lockWaits", qidong_lockWaits + "");
		statisticsHash.put("qidong_memoryGrantQueueWaits",
				qidong_memoryGrantQueueWaits + "");
		statisticsHash.put("qidong_threadSafeMemoryObjectWaits",
				qidong_threadSafeMemoryObjectWaits + "");
		statisticsHash.put("qidong_logWriteWaits", qidong_logWriteWaits + "");
		statisticsHash.put("qidong_logBufferWaits", qidong_logBufferWaits + "");
		statisticsHash.put("qidong_networkIOWaits", qidong_networkIOWaits + "");
		statisticsHash.put("qidong_pageIOLatchWaits", qidong_pageIOLatchWaits
				+ "");
		statisticsHash.put("qidong_pageLatchWaits", qidong_pageLatchWaits + "");
		statisticsHash.put("qidong_nonPageLatchWaits", qidong_nonPageLatchWaits
				+ "");
		statisticsHash.put("qidong_waitForTheWorker", qidong_waitForTheWorker
				+ "");
		statisticsHash.put("qidong_workspaceSynchronizationWaits",
				qidong_workspaceSynchronizationWaits + "");
		statisticsHash.put("qidong_transactionOwnershipWaits",
				qidong_transactionOwnershipWaits + "");

		statisticsHash.put("leiji_lockWaits", leiji_lockWaits + "");
		statisticsHash.put("leiji_memoryGrantQueueWaits",
				leiji_memoryGrantQueueWaits + "");
		statisticsHash.put("leiji_threadSafeMemoryObjectWaits",
				leiji_threadSafeMemoryObjectWaits + "");
		statisticsHash.put("leiji_logWriteWaits", leiji_logWriteWaits + "");
		statisticsHash.put("leiji_logBufferWaits", leiji_logBufferWaits + "");
		statisticsHash.put("leiji_networkIOWaits", leiji_networkIOWaits + "");
		statisticsHash.put("leiji_pageIOLatchWaits", leiji_pageIOLatchWaits
				+ "");
		statisticsHash.put("leiji_pageLatchWaits", leiji_pageLatchWaits + "");
		statisticsHash.put("leiji_nonPageLatchWaits", leiji_nonPageLatchWaits
				+ "");
		statisticsHash.put("leiji_waitForTheWorker", leiji_waitForTheWorker
				+ "");
		statisticsHash.put("leiji_workspaceSynchronizationWaits",
				leiji_workspaceSynchronizationWaits + "");
		statisticsHash.put("leiji_transactionOwnershipWaits",
				leiji_transactionOwnershipWaits + "");

		/* 数据库页连接统计 */
		Hashtable conns = new Hashtable();
		conns.put("connections", connections + ""); // * 活动连接
		conns.put("totalLogins", totalLogins + "");// 登录
		conns.put("totalLoginsRate", totalLoginsRate + "");// * 登录/分
		conns.put("totalLogouts", totalLogouts + "");// 退出
		conns.put("totalLogoutsRate", totalLogoutsRate + ""); // * 退出/分

		/* 锁明细 */
		Hashtable locks = new Hashtable();
		locks.put("lockRequests", lockRequests + ""); // 锁定请求
		locks.put("lockRequestsRate", lockRequestsRate + "");// *$ 锁定请求/分
		locks.put("lockWaits", lockWaits + "");// 锁定等待
		locks.put("lockWaitsRate", lockWaitsRate + "");// * 锁定等待/分
		locks.put("lockTimeouts", lockTimeouts + ""); // 锁定超时
		locks.put("lockTimeoutsRate", lockTimeoutsRate + ""); // * 锁定超时/分
		locks.put("deadLocks", deadLocks + "");// 死锁数
		locks.put("deadLocksRate", deadLocksRate + "");// * 死锁数/分
		locks.put("avgWaitTime", avgWaitTime + "");// * 平均锁定等待时间
		locks.put("avgWaitTimeBase", avgWaitTimeBase + ""); // 平均锁定等待时间计算的基数
		/* 闭锁的明细 */
		locks.put("latchWaits", latchWaits + "");// 闭锁等待
		locks.put("latchWaitsRate", latchWaitsRate + "");// * 闭锁等待/分
		locks.put("avgLatchWait", avgLatchWait + ""); // *$ 平均闭锁等待时间;

		/* 缓存明细 */
		Hashtable caches = new Hashtable();
		caches.put("cacheHitRatio", cacheHitRatio + ""); // *$ 缓存击中率
		caches.put("cacheHitRatioBase", cacheHitRatioBase + "");// 缓存击中率计算的基数
		caches.put("cacheCount", cacheCount + "");// * 缓存数
		caches.put("cachePages", cachePages + "");// * 缓存页
		caches.put("cacheUsed", cacheUsed + ""); // 使用的缓存
		caches.put("cacheUsedRate", cacheUsedRate + ""); // * 使用的缓存/分

		/* 内存利用情况 */
		Hashtable mems = new Hashtable();
		mems.put("totalMemory", totalMemory + ""); // *$ 内存总数
		mems.put("sqlMem", sqlMem + ""); // *$ SQL缓存存储
		mems.put("optMemory", optMemory + "");// *$ 内存优化
		mems.put("memGrantPending", memGrantPending + "");// * 内存分配未决
		mems.put("memGrantSuccess", memGrantSuccess + ""); // * 内存分配成功
		mems.put("lockMem", lockMem + ""); // *$ 锁定内存
		mems.put("conMemory", conMemory + "");// *$ 连接内存
		mems.put("grantedWorkspaceMem", grantedWorkspaceMem + ""); // *
		// 产生的工作空间内存

		/* SQL统计 */
		Hashtable sqls = new Hashtable();
		sqls.put("batchRequests", batchRequests + "" + ""); // 批请求
		sqls.put("batchRequestsRate", batchRequestsRate + "");// *$ 批请求/分
		sqls.put("sqlCompilations", sqlCompilations + "");// SQL编辑
		sqls.put("sqlCompilationsRate", sqlCompilationsRate + "");// * SQL编辑/分
		sqls.put("sqlRecompilation", sqlRecompilation + "");// //SQL重编辑
		sqls.put("sqlRecompilationRate", sqlRecompilationRate + ""); // *
		// SQL重编辑/分
		sqls.put("autoParams", autoParams + "");// 自动参数化尝试次数
		sqls.put("autoParamsRate", autoParamsRate + "");// * 自动参数化尝试次数/分
		sqls.put("failedAutoParams", failedAutoParams + "");// 自动参数化失败次数
		sqls.put("failedAutoParamsRate", failedAutoParamsRate + "");// *
		// 自动参数化失败次数/分

		/* 访问方法的明细 */
		Hashtable scans = new Hashtable();
		scans.put("fullScans", fullScans + ""); // 完全扫描
		scans.put("fullScansRate", fullScansRate + "");// *$ 完全扫描/分
		scans.put("rangeScans", rangeScans + "");// 范围扫描
		scans.put("rangeScansRate", rangeScansRate + "");// * 范围扫描/分
		scans.put("probeScans", probeScans + "");// 探针扫描
		scans.put("probeScansRate", probeScansRate + ""); // * 探针扫描/分
		/*
		 * Vector altfiles_v = new Vector(); try{ altfiles_v =
		 * getSqlserverAltfile(ip,username,password); }catch(Exception e){
		 * e.printStackTrace(); } Vector lockinfo_v = new Vector(); try{
		 * lockinfo_v = getSqlserverLockinfo(ip,username,password);
		 * }catch(Exception e){ e.printStackTrace(); } Vector process_v = new
		 * Vector(); try{ process_v =
		 * getSqlserverProcesses(ip,username,password); }catch(Exception e){
		 * e.printStackTrace(); }
		 * 
		 * Hashtable sysValue = new Hashtable(); try{ sysValue =
		 * getSqlServerSys(ip,username,password); }catch(Exception e){
		 * e.printStackTrace(); }
		 */
		retValue.put("pages", pages);// nms_sqlserverpages
		retValue.put("conns", conns);// nms_sqlserverconns
		retValue.put("locks", locks);// nms_sqlserverlocks
		retValue.put("caches", caches);// nms_sqlservercaches
		retValue.put("mems", mems);// nms_sqlservermems
		retValue.put("sqls", sqls);// nms_sqlserversqls
		retValue.put("scans", scans);// nms_sqlserverscans
		retValue.put("statisticsHash", statisticsHash);// nms_sqlserverstatisticshash
		return retValue;
		
	
	}
	
	public long getTaskInterval() {
		TaskXml taskxml = new TaskXml();
		List list = taskxml.ListXml();
		Task task = new Task();
		long retValue = 300;
		try {
			for (int i = 0; i < list.size(); i++) {
				BeanUtils.copyProperties(task, list.get(i));
				if (task.getTaskname().equalsIgnoreCase("dbtask")) {
					break;
				}
			}
			if (task.getPolltimeunit().equalsIgnoreCase("m")) {
				// 分钟
				retValue = task.getPolltime().longValue() * 60;
			} else if (task.getPolltimeunit().equalsIgnoreCase("h")) {
				// 小时
				retValue = task.getPolltime().longValue() * 60 * 60;
			} else if (task.getPolltimeunit().equalsIgnoreCase("d")) {
				// 天
				retValue = task.getPolltime().longValue() * 60 * 60 * 24;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return retValue;

	}
	
	
	/* 对于double型的数字，定其精度为小数点后四位小数 */
	public double formatDouble(double d) {
		String doubleStr = d + "";
		doubleStr = doubleStr.trim();
		int length = doubleStr.length();
		double doubleReturnd = 0.0D;
		if (doubleStr.indexOf(".") > -1) {
			if (length > doubleStr.indexOf(".") + 4)
				try {
					doubleStr = doubleStr.substring(0,
							doubleStr.indexOf(".") + 4);
					doubleReturnd = Double.parseDouble(doubleStr);
				} catch (Exception exception) {
					exception.printStackTrace();
					return doubleReturnd;
				}
			else
				try {
					doubleReturnd = Double.parseDouble(doubleStr);
				} catch (Exception exception1) {
					exception1.printStackTrace();
					return doubleReturnd;
				}
		} else {
			try {
				doubleReturnd = Double.parseDouble(doubleStr);
			} catch (Exception exception2) {
				exception2.printStackTrace();
				return doubleReturnd;
			}
		}
		return doubleReturnd;
	}
	
	/* 计算一段时间内某值的变化率(/分) */
	public double calculateRate(long timespan, double currentVal,
			double formerVal) {
		double rate = 0.0D;
		if (timespan < 60L)
			timespan = 60L;
		if (currentVal <= formerVal)
			rate = 0.0D;
		else
			rate = (currentVal - formerVal) / (double) (timespan / 60L);
		return rate;
	}

	/* 计算传入参数特定意义的百分比形式（命中率等） */
	public double calculateValue(double value, double valueBase, boolean flag) {
		double valueReturn = 0.0D;
		try {
			double valueD = value;
			double valueDBase = valueBase;
			if (valueDBase <= 0.0D)
				valueDBase = 1.0D;

			valueReturn = valueD / valueDBase;

			if (flag)
				valueReturn *= 100D;
			valueReturn = formatDouble(valueReturn);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return valueReturn;
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
				sBuffer.append(montime);
				sBuffer.append("')");
			} else if (SystemConstant.DBType.equals("oracle")) {
				sBuffer.append("',to_date('" + montime
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
				sBuffer.append(montime);
				sBuffer.append("')");
			} else if (SystemConstant.DBType.equals("oracle")) {
				sBuffer.append("',to_date('" + montime
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
				sBuffer.append(montime);
				sBuffer.append("')");
			} else if (SystemConstant.DBType.equals("oracle")) {
				sBuffer.append("',to_date('" + montime
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
				sBuffer.append(montime);
				sBuffer.append("')");
			} else if (SystemConstant.DBType.equals("oracle")) {
				sBuffer.append("',to_date('" + montime
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
				sBuffer.append(montime);
				sBuffer.append("')");
			} else if (SystemConstant.DBType.equals("oracle")) {
				sBuffer.append("',to_date('" + montime
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
				sBuffer.append(montime);
				sBuffer.append("')");
			} else if (SystemConstant.DBType.equals("oracle")) {
				sBuffer.append("',to_date('" + montime
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
				sBuffer.append(montime);
				sBuffer.append("')");
			} else if (SystemConstant.DBType.equals("oracle")) {
				sBuffer.append("',to_date('" + montime
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
