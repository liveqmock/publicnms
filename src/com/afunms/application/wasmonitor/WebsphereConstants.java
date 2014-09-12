package com.afunms.application.wasmonitor;


public class WebsphereConstants {
	
	
	//配置
	public static final String CONFIG_INFO = "WebsphereConf";//采集原子ID，有时间修改成WEBSPHERE_COFNIG
	
	public static final String WEBSPHERE_PERF = "WebspherePerf";//采集原子ID，性能
	
	public static final String WEBSPHERE_STATE= "WebsphereState";//状态采集原子ID
	
	
	public static final String CONFIG_NODE = "NodeConf";
	public static final String CONFIG_SERVER = "ServerConf";
    public static final String CONFIG_TRANSCATION = "TransactionConf";//事务
	public static final String CONFIG_SERVLET = "ServletConf";//servlet
	public static final String CONFIG_SYSTEM = "SystemDataConf";//CPU配置信息
	public static final String CONFIG_JVM = "JVMConf";
	
    public static final String CONFIG_JDBCPOOL_TOTAL = "JDBCPoolTotalConf";
    public static final String CONFIG_JDBCPOOL_INSTANCE = "JDBCPoolInstanceConf";
    
    public static final String CONFIG_THREADPOOL_TOTAL = "ThreadPoolTotalConf";//ThreadPoolTotalConf
    public static final String CONFIG_THREADPOOL_INSTANCE = "ThreadPoolInstanceConf";
    public static final String CONFIG_CACHE_TOTAL = "CacheTotalConf";  
    public static final String CONFIG_CACHE_INSTANCE= "CacheInstanceConf";
    
    public static final String CONFIG_REGISTRY_CACHE = "RegistryCacheConf";
    
    public static final String CONFIG_WEBAPPLICATION_TOTAL = "WebapplicationTotalConf";//总体应用配置
    public static final String CONFIG_WEBAPPLICATION_INSTANCE = "WebapplicationInstanceConf";//应用实例配置
    
    public static final String CONFIG_SERVLET_TOTAL= "ServletTotalConf";
    
    public static final String CONFIG_SERVLET_INSTANCE = "ServletInstanceConf";

    //性能
    //和partTypeID相同
	public static final String PERF_CACHE_TOTAL = "CacheTotalPerf";
	public static final String PERF_CACHE_INSTANCE = "CacheInstancePerf";
	public static final String PERF_REGISTRYCACHE = "RegistryPerf";
	
	public static final String PERF_WEBAPPLICATION_TOTAL = "WebapplicationTotalPerf";
	public static final String PERF_WEBAPPLICATION_INSTANCE = "WebapplicationInstancePerf";
	
	//jvm
	public static final String PERF_JVM = "JVMPerf";
	//jdbc
	public static final String PERF_JDBC_TOTAL = "JDBCPoolTotalPerf";
	public static final String PERF_JDBC_INSTANCE = "JDBCPoolInstancePerf";
	
	//system
	public static final String PERF_SYSTEM = "SystemDataPerf";
	//servlet
	public static final String PERF_SERVLET_TOTAL = "ServletTotalPerf";
	public static final String PERF_SERVLET_INSTANCE = "ServletInstancePerf";
	//threadpool
	public static final String PERF_THREADPOOL_TOTAL ="ThreadPoolTotalPerf";
	public static final String PERF_THREADPOOL_INSTANCE = "ThreadPoolInstancePerf";
	public static final String PERF_WEBSPHER_BASE = "BaseInfo";
	//事务
	public static final String PERF_TRANS = "TranscationPerf";
	
	public static final String STATE_SERVER = "ServerState";
	//CacheTotalPerf|RegistryPerf|WebapplicationTotalPerf|WebapplicationInstancePerf|JVMPerf|JDBCPoolTotalPerf|JDBCPoolInstancePerf|SystemDataPerf|ServletTotalPerf|ServletInstancePerf|ThreadPoolTotalPerf|ThreadPoolInstancePerf|TranscationPerf
	
	
	public static final String JDBC_CONNECTPOOL = "";
	public static final String JVM="";
	public static final String SERVLET_SESSION = "";
	public static final String SYSTEM_DATA= "";
	public static final String THREAD_POOL="";
	public static final String TRANCE_MANAGER = "";
	public static final String WEB_APP = "";
	
	//下面是websphere各个指标对应的常量
	
	
	//高速缓存标识
	public static final String CACHE_MAXINMEMORYCACHEENTRYCOUNT = "MaxInMemoryCacheEntryCount";
		
	public static final String CACHE_INMEMORYCACHEENTRYCOUNT = "InMemoryCacheEntryCount";
	
	// 
	public static final String CACHE_MISSCOUNT= "MissCount";
	public static final String CACHE_TIMEOUTINVALIDATION = "TimeoutInvalidationCount";
	public static final String CACHE_HITSINMEM = "HitsInMemoryCount";
	public static final String CACHE_HITSINMEMANDDISK = "InMemoryAndDiskCacheEntryCount";
	public static final String CACHE_HITSONDISK = "HitsOnDiskCount";
	public static final String CACHE_CLIENTREQUEST = "ClientRequestCount";
	
	
	
	
	public static final String JVM_HEAPSIZE = "HeapSize";//HeapSize
	public static final String JVM_USEDMEMORY = "UsedMemory";//UsedMemory
	public static final String JVM_UPTIME = "UpTime";
	public static final String JVM_FREEMEMORY = "FreeMemory";
	public static final String JVM_PROCESSCPU = "ProcessCpuUsage";
	
	//webapplication对应的指标
	//LoadedServletCount,ReloadCount,ErrorCount,ConcurrentRequests
	
	public static final String WEBAPPLICATION_REQUESTCOUNT = "RequestCount";
	public static final String WEBAPPLICATION_SERVICETIME = "ServiceTime";
	public static final String WEBAPPLICATION_LOADEDSERVLET = "LoadedServletCount";
	public static final String WEBAPPLICATION_RELOAD = "ReloadCount";
	public static final String WEBAPPLICATION_ERROR = "ErrorCount";
	public static final String WEBAPPLICATION_CONCURRENT = "ConcurrentRequests";
	
	//jdbc连接池对应的指标的标识
	
	public static final String JDBCPOOL_CREATCOUNT = "CreateCount";
	public static final String JDBCPOOL_CLOSECOUNT = "CloseCount";
	public static final String JDBCPOOL_ALLOCATECOUNT = "AllocateCount";
	public static final String JDBCPOOL_SIZE = "PoolSize";
	public static final String JDBCPOOL_FREEPOOL = "FreePoolSize";
	public static final String JDBCPOOL_WAITTHREAD = "WaitingThreadCount";
	public static final String JDBCPOOL_FAULT = "FaultCount";
	public static final String JDBCPOOL_PERCENTUSED = "PercentUsed";
	public static final String JDBCPOOL_USETIME = "UseTime";
	public static final String JDBCPOOL_JDBCTIME = "JDBCTime";
	public static final String JDBCPOOL_WAITTIME = "WaitTime";
	public static final String JDBCPOOL_PREPSTMTCACHE_DISCARD= "PrepStmtCacheDiscardCount";
	//threadpool对应指标的标识
	public static final String THREADPOOL_SIZE = "PoolSize";
	public static final String THREADPOOL_CREAT = "CreateCount";
	public static final String THREADPOOL_DESTROY = "DestroyCount";
	public static final String THREADPOOL_ACTIVE = "ActiveCount";
	public static final String THREADPOOL_ACTIVETIME = "ActiveTime";
	
	//transcation事务对应指标的标识
	//ActiveCount,CommittedCount,RolledbackCount,GlobalTranTime,
	//GlobalBegunCount,LocalBegunCount,LocalActiveCount,LocalTranTime ,LocalTimeoutCount ,LocalRolledbackCount ,GlobalTimeoutCount 
	public static final String TRANS_ACTIVE = "ActiveCount";
	public static final String TRANS_COMMITTED = "CommittedCount";
	public static final String TRANS_ROLLEDBACK = "RolledbackCount";
	public static final String TRANS_GLOBALTRANTIME = "GlobalTranTime";
	public static final String TRANS_GLOBALBEGUN = "GlobalBegunCount";
	public static final String TRANS_LOCALBEGUN = "LocalBegunCount";
	public static final String TRANS_LOCALACTIVE = "LocalActiveCount";
	public static final String TRANS_LOCALTRANTIME = "LocalTranTime";
	public static final String TRANS_LOCALTIMEOUT = "LocalTimeoutCount";
	public static final String TRANS_LOCALROLLBACK = "LocalRolledbackCount";
	public static final String TRANS_GLOBALTIMEOUT = "GlobalTimeoutCount";
	
	//系统数据指标标识
	public static final String SYSTEM_CPUUSAGESINCELAST = "CPUUsageSinceLastMeasurement";//cpuUsageSinceLastMeasurement
	public static final String SYSTEM_CPUUSAGESINCESTARTED = "CPUUsageSinceServerStarted"; //CPUUsageSinceServerStarted
	public static final String SYSTEM_FREEMEMORY = "FreeMemory"; //FreeMemory
	
	//缓存指标标识
	public static final String EXTENSION_NUMQUESTS = "RequestCount";
	public static final String EXTENSION_NUMHITS = "HitCount";
	public static final String EXTENSION_HITRATE = "HitRate";
	
	//下面是servlet指标标识
	//LiveCount,CreateCount,InvalidateCount,LifeTime,ActiveCount,TimeoutInvalidationCount
	public static final String SERVLET_LIVECOUNT = "LiveCount";
	public static final String SERVLET_CREATECOUNT = "CreateCount";
	public static final String SERVLET_INVALIDATECOUNT = "InvalidateCount";
	public static final String SERVLET_LIFETIME = "LifeTime";
	public static final String SERVLET_ACTIVECOUNT = "ActiveCount";
	public static final String SERVLET_TIMEOUTINVALIDATION = "TimeoutInvalidationCount";
	
	
	//websphere5.1版本中标识
	
	public static final String CACHE5_MAXINMEMORYCACHEENTRYCOUNT = "maxInMemoryCacheSize";
	public static final String CACHE5_INMEMORYCACHEENTRYCOUNT = "inMemoryCacheSize";
	public static final String CACHE5_TIMEOUTINVALIDATION = "totalTimeoutInvalidations";
	
	public static final String JVM5_HEAPSIZE = "totalMemory";//HeapSize
	public static final String JVM5_USEDMEMORY = "usedMemory";//UsedMemory
	public static final String JVM5_UPTIME = "upTime";
	public static final String JVM5_FREEMEMORY = "freeMemory";
//	public static final String JVM5_PROCESSCPU = "ProcessCpuUsage";
	
	public static final String WEBAPPLICATION5_REQUESTCOUNT = "totalRequests";
	public static final String WEBAPPLICATION5_SERVICETIME = "responseTime";
	public static final String WEBAPPLICATION5_LOADEDSERVLET = "numLoadedServlets";//numLoadedServlets
	public static final String WEBAPPLICATION5_RELOAD = "numReloads";
	public static final String WEBAPPLICATION5_ERROR = "numErrors";
	public static final String WEBAPPLICATION5_CONCURRENT = "concurrentRequests";
	
	public static final String JDBCPOOL5_CREATCOUNT = "numCreates";
	public static final String JDBCPOOL5_CLOSECOUNT = "numDestroys";
	public static final String JDBCPOOL5_ALLOCATECOUNT = "numAllocates";
	public static final String JDBCPOOL5_SIZE = "poolSize";
	public static final String JDBCPOOL5_FREEPOOL = "freePoolSize";
	public static final String JDBCPOOL5_WAITTHREAD = "concurrentWaiters";
	public static final String JDBCPOOL5_FAULT = "faults";
	public static final String JDBCPOOL5_PERCENTUSED = "percentUsed";
	public static final String JDBCPOOL5_USETIME = "avgUseTime";
	public static final String JDBCPOOL5_JDBCTIME = "jdbcOperationTimer";
	public static final String JDBCPOOL5_WAITTIME = "avgWaitTime";
	public static final String JDBCPOOL5_PREPSTMTCACHE_DISCARD= "prepStmtCacheDiscards";
	
	//threadpool对应指标的标识
	public static final String THREADPOOL5_SIZE = "poolSize";
	public static final String THREADPOOL5_CREAT = "threadCreates";
	public static final String THREADPOOL5_DESTROY = "threadDestroys";
	public static final String THREADPOOL5_ACTIVE = "activeThreads";
//	public static final String THREADPOOL5_ACTIVETIME = "ActiveTime";
	
	public static final String SERVLET5_LIVECOUNT = "liveSessions";
	public static final String SERVLET5_CREATECOUNT = "createdSessions";
	public static final String SERVLET5_INVALIDATECOUNT = "invalidatedSessions";
	public static final String SERVLET5_LIFETIME = "sessionLifeTime";
	public static final String SERVLET5_ACTIVECOUNT = "activeSessions";
	public static final String SERVLET5_TIMEOUTINVALIDATION = "invalidatedViaTimeout";
	
	//系统数据指标标识
	public static final String SYSTEM5_CPUUSAGESINCELAST = "cpuUtilization";//cpuUsageSinceLastMeasurement
	public static final String SYSTEM5_CPUUSAGESINCESTARTED = "avgCpuUtilization"; //CPUUsageSinceServerStarted
	public static final String SYSTEM5_FREEMEMORY = "freeMemory"; //FreeMemory
	
	public static final String TRANS5_ACTIVE = "activeGlobalTrans";
	public static final String TRANS5_COMMITTED = "globalTransCommitted";
	public static final String TRANS5_ROLLEDBACK = "globalTransRolledBack";
	public static final String TRANS5_GLOBALTRANTIME = "globalTranDuration";
	public static final String TRANS5_GLOBALBEGUN = "globalTransBegun";
	public static final String TRANS5_LOCALBEGUN = "localTransBegun";
	public static final String TRANS5_LOCALACTIVE = "activeLocalTrans";
	public static final String TRANS5_LOCALTRANTIME = "localTranDuration";
	public static final String TRANS5_LOCALTIMEOUT = "localTransTimeout";
	public static final String TRANS5_LOCALROLLBACK = "localTransRolledBack";
	public static final String TRANS5_GLOBALTIMEOUT = "globalTransTimeout";
	
	//下面对应websphere监控的指标类型的常量，是固定的一个标识符,以下是版本6.0,6.1的
	
	public static final String WEBSPHERE_CACHE6 = "动态高速缓存";
	
	public static final String WEBSPHERE_JDBC6 = "JDBC 连接池";//此处可以只写JDBC四个字母
	public static final String WEBSPHERE_JDBC7 = "JDBC Connection Pools";

	
	public static final String WEBSPHERE_JVM6 = "JVM 运行时";
	public static final String WEBSPHERE_JVM7 = "JVM Runtime";

	public static final String WEBSPHERE_THREADPOOL6 = "线程池";
	public static final String WEBSPHERE_THREADPOOL7 = "Thread Pools";

	
	public static final String WEBSPHERE_TRANSCATION6= "事务管理器";
	public static final String WEBSPHERE_TRANSCATION7="Transaction Manager";

	public static final String WEBSPHERE_SYSTEM6 = "系统数据";
	public static final String WEBSPHERE_SYSTEM7 = "System Data";

	public static final String WEBSPHERE_SERVLET6 = "Servlet 会话管理器";
	public static final String WEBSPHERE_SERVLET7 = "Servlet Session Manager";

	public static final String WEBSPHERE_WEBAPPLICATION6 = "Web Applications";
	
	public static final String WEBSPHERE_REGISTRY_CACHE6 = "ExtensionRegistryStats.name";
	
	//下面是websphere5.1指标类型常量，是固定的一个标识符。
	public static final String WEBSPHERE_CACHE5 = "cacheModule";
	public static final String WEBSPHERE_JDBC5 = "connectionPoolModule";
	public static final String WEBSPHERE_THREADPOOL5 = "threadPoolModule";
	public static final String WEBSPHERE_WEBAPPLICATION5 = "webAppModule";
    public static final String WEBSPHERE_JVM5 = "jvmRuntimeModule";
	public static final String WEBSPHERE_TRANSCATION5= "transactionModule";
	
	public static final String WEBSPHERE_SYSTEM5 = "systemModule";

	public static final String WEBSPHERE_SERVLET5 = "servletSessionsModule";
	
	//public static final String WEBSPHERE_REGISTRY_CACHE5 = "ExtensionRegistryStats.name"; v5暂时没有这一项
	
	
}
