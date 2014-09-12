package com.afunms.application.wasmonitor;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Node;

import com.afunms.application.dao.WasConfigDao;
import com.afunms.polling.om.Interfacecollectdata;

public class Websphere5XMLParse {
	Hashtable cachehst = new Hashtable();
	Hashtable jvmhst = new Hashtable();
	Hashtable jdbchst = new Hashtable();
	Hashtable systemdatahst = new Hashtable();
	
    Hashtable servlethst = new Hashtable();
    Hashtable threadhst = new Hashtable();
    Hashtable transhst = new Hashtable();
	
	protected Hashtable getCache5ConfAndPerf(String ip,Node listServer,String nodeName,String serverName,String version)
	{
		
		String xpath5_cache = "/PerformanceMonitor/" +
		"Node[@name='"+nodeName+"']/Server[@name='"+serverName+"']/cacheModule/descendant-or-self::*";
		
		List<Node> listCaches = listServer.getDocument().selectNodes(xpath5_cache);//拿到每个缓存的名称及总体高速缓存对应的指标。
		
		if (listCaches!= null && listCaches.size() > 0)
		{
				int  inMemoryCacheCount =-1;
				int  maxInMemoryCacheCount =-1;
				int timeoutInvalidationCount = -1;
				
				for (Node listCache:listCaches)
				{
					String parentTypeName = listCache.getParent().getName();
					if (WebsphereConstants.CACHE5_INMEMORYCACHEENTRYCOUNT.equalsIgnoreCase(parentTypeName))
					{
						String itemValue = listCache.valueOf("@val");
						inMemoryCacheCount = (int)Float.parseFloat(itemValue);
						
					}else if (WebsphereConstants.CACHE5_MAXINMEMORYCACHEENTRYCOUNT.equalsIgnoreCase(parentTypeName))
					{
						String itemValue = listCache.valueOf("@val");
						maxInMemoryCacheCount = (int)Float.parseFloat(itemValue);
					}else if (WebsphereConstants.CACHE5_TIMEOUTINVALIDATION.equalsIgnoreCase(parentTypeName))
					{
						String itemValue = listCache.valueOf("@val");
						timeoutInvalidationCount = (int)Float.parseFloat(itemValue);
					}
					

			}
				
				Was5cache was5cache = new Was5cache();
				Calendar date=Calendar.getInstance();
				was5cache.setIpaddress(ip);
				was5cache.setInMemoryCacheCount(maxInMemoryCacheCount);
				was5cache.setMaxInMemoryCacheCount(maxInMemoryCacheCount);
				was5cache.setTimeoutInvalidationCount(timeoutInvalidationCount);
				was5cache.setRecordtime(date);
				
				WasConfigDao wasconfigdao=new WasConfigDao();
				try{
					wasconfigdao.createHostData(was5cache,"wascache");
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					wasconfigdao.close();
				}
				
				cachehst.put("inMemoryCacheCount", inMemoryCacheCount);
				cachehst.put("maxInMemoryCacheCount", maxInMemoryCacheCount);
				cachehst.put("timeoutInvalidationCount", timeoutInvalidationCount);
		}
		return cachehst;
	}
	protected void getServerState(Node listServer,String nodeName,String serverName,String version)
	{
		/**
		 * 把servername和nodename传入，判断是否能够取到JVM的指标，如果有则说明，此server肯定为启动状态，如果不能则为停止状态stopped
		 */
		String xpath5_JVM = "/PerformanceMonitor/" +
		"Node[@name='"+nodeName+"']/Server[@name='"+serverName+"']/jvmRuntimeModule/descendant-or-self::*";
		List<Node> listJVM = listServer.getDocument().selectNodes(xpath5_JVM);
		
		int state = 0;
		if (null != listJVM && listJVM.size() > 0)
		{
			state  = 1;
		
		}
		
	}
	protected Hashtable getJVM5ConfAndPerf(String ip,Node listServer,String nodeName,String serverName,String version)
	{
		String xpath5_JVM = "/PerformanceMonitor/" +
		"Node[@name='"+nodeName+"']/Server[@name='"+serverName+"']/jvmRuntimeModule/descendant-or-self::*";
		
		List<Node> listJVM = listServer.getDocument().selectNodes(xpath5_JVM);
		if (null != listJVM && listJVM.size() > 0)
		{
			
				int heapSize = -1;//Java虚拟机运行时总内存
				int freeMemory = -1; // java虚拟机运行时空闲的内存数量
				int usedMemory = -1;//Java虚拟机运行时使用的内存数量
				int upTime  =-1;//已经运行的时间数
				int memPer = -1;
				for(Node jvmNode:listJVM)
				{
					String parentTypeName = jvmNode.getParent().getName();
					
					if (WebsphereConstants.JVM5_FREEMEMORY.equalsIgnoreCase(parentTypeName))
					{
						String itemValue = jvmNode.valueOf("@val");
						freeMemory = (int)Float.parseFloat(itemValue);
					}else if (WebsphereConstants.JVM5_HEAPSIZE.equalsIgnoreCase(parentTypeName))
					{
						String itemValue = jvmNode.valueOf("@currentValue");
						heapSize = (int)Float.parseFloat(itemValue);
					}else if (WebsphereConstants.JVM5_UPTIME.equalsIgnoreCase(parentTypeName))
					{
						String itemValue = jvmNode.valueOf("@val");
						upTime = (int)Float.parseFloat(itemValue);
					}else if (WebsphereConstants.JVM5_USEDMEMORY.equalsIgnoreCase(parentTypeName))
					{
						String itemValue = jvmNode.valueOf("@val");
						usedMemory = (int)Float.parseFloat(itemValue);
					}
				}
				if(heapSize != 0)
					memPer = usedMemory*100/heapSize;
				
				Interfacecollectdata hostdata = new Interfacecollectdata();
				hostdata.setIpaddress(ip);
				Calendar date=Calendar.getInstance();
				hostdata.setCollecttime(date);
				hostdata.setCategory("WasJVM");
				hostdata.setEntity("Utilization");
				hostdata.setSubentity("jvm");
				hostdata.setRestype("dynamic");
				hostdata.setUnit("%");
				hostdata.setThevalue(Math.round(memPer)+"");
				WasConfigDao wasconfigdao=new WasConfigDao();
				try{
					wasconfigdao.createHostData(hostdata,"wasjvm");
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					wasconfigdao.close();
				}
				
				
				Was5jvminfo was5jvminfo = new Was5jvminfo();
				was5jvminfo.setIpaddress(ip);
				was5jvminfo.setFreeMemory(freeMemory);
				was5jvminfo.setHeapSize(heapSize);
				was5jvminfo.setMemPer(memPer);
				was5jvminfo.setUpTime(upTime);
				was5jvminfo.setUsedMemory(usedMemory);
				was5jvminfo.setRecordtime(date);
				
				wasconfigdao=new WasConfigDao();
				try{
					wasconfigdao.createHostData(was5jvminfo,"wasjvminfo");
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					wasconfigdao.close();
				}
				
				jvmhst.put("freeMemory", freeMemory);
				jvmhst.put("heapSize", heapSize);
				jvmhst.put("upTime", upTime);
				jvmhst.put("usedMemory", usedMemory);
				jvmhst.put("memPer", memPer);
		}
		return jvmhst;
	}
	
	protected Hashtable getJDBC5ConfAndPerf(String ip,Node listServer,String nodeName,String serverName,String version)
	{
		String xpath5_JDBC = "/PerformanceMonitor/"+"Node[@name='"+nodeName+"']/Server[@name='"+serverName+"']/connectionPoolModule/numCreates/descendant-or-self::*"+
		"|/PerformanceMonitor/"+"Node[@name='"+nodeName+"']/Server[@name='"+serverName+"']/connectionPoolModule/numDestroys/descendant-or-self::*"+"|/PerformanceMonitor/"+"Node[@name='"+nodeName+"']/Server[@name='"+serverName+"']/connectionPoolModule/numAllocates/descendant-or-self::*"
		+"|/PerformanceMonitor/"+"Node[@name='"+nodeName+"']/Server[@name='"+serverName+"']/connectionPoolModule/numReturns/descendant-or-self::*"
		+"|/PerformanceMonitor/"+"Node[@name='"+nodeName+"']/Server[@name='"+serverName+"']/connectionPoolModule/poolSize/descendant-or-self::*"
		+"|/PerformanceMonitor/"+"Node[@name='"+nodeName+"']/Server[@name='"+serverName+"']/connectionPoolModule/freePoolSize/descendant-or-self::*"
		+"|/PerformanceMonitor/"+"Node[@name='"+nodeName+"']/Server[@name='"+serverName+"']/connectionPoolModule/concurrentWaiters/descendant-or-self::*"
		+"|/PerformanceMonitor/"+"Node[@name='"+nodeName+"']/Server[@name='"+serverName+"']/connectionPoolModule/faults/descendant-or-self::*"
		+"|/PerformanceMonitor/"+"Node[@name='"+nodeName+"']/Server[@name='"+serverName+"']/connectionPoolModule/percentUsed/descendant-or-self::*"
		+"|/PerformanceMonitor/"+"Node[@name='"+nodeName+"']/Server[@name='"+serverName+"']/connectionPoolModule/percentMaxed/descendant-or-self::*"
		+"|/PerformanceMonitor/"+"Node[@name='"+nodeName+"']/Server[@name='"+serverName+"']/connectionPoolModule/avgUseTime/descendant-or-self::*"
		+"|/PerformanceMonitor/"+"Node[@name='"+nodeName+"']/Server[@name='"+serverName+"']/connectionPoolModule/avgWaitTime/descendant-or-self::*"
		+"|/PerformanceMonitor/"+"Node[@name='"+nodeName+"']/Server[@name='"+serverName+"']/connectionPoolModule/numManagedConnections/descendant-or-self::*"
		+"|/PerformanceMonitor/"+"Node[@name='"+nodeName+"']/Server[@name='"+serverName+"']/connectionPoolModule/numConnectionHandles/descendant-or-self::*"
		+"|/PerformanceMonitor/"+"Node[@name='"+nodeName+"']/Server[@name='"+serverName+"']/connectionPoolModule/prepStmtCacheDiscards/descendant-or-self::*"
		+"|/PerformanceMonitor/"+"Node[@name='"+nodeName+"']/Server[@name='"+serverName+"']/connectionPoolModule/jdbcOperationTimer/descendant-or-self::*";

		//String xpath5_JDBC = "/PerformanceMonitor/"+"Node[@name='"+nodeName+"']/Server[@name='"+serverName+"']/connectionPoolModule/descendant::*";
		List<Node> listJDBC = listServer.getDocument().selectNodes(xpath5_JDBC);
		if (null != listJDBC && listJDBC.size() > 0)
		{
			int createCount = -1;
			int closeCount = -1;
			int poolSize = -1;
			int freePoolSize = -1;
			int waitingThreadCount = -1;
			float percentUsed = -1;
			int useTime = -1;
			int waitTime =-1;
			int allocateCount = -1;
			int prepStmtCacheDiscardCount = -1;
			int jdbcTime = -1;
			int faultCount = -1;
			for (Node jdbcNode : listJDBC)
			{
				String typeName = jdbcNode.getParent().getName();
				if (WebsphereConstants.JDBCPOOL5_ALLOCATECOUNT.equalsIgnoreCase(typeName))
				{
					String itemValue = jdbcNode.valueOf("@val");
					allocateCount = (int)Float.parseFloat(itemValue);
				}else if (WebsphereConstants.JDBCPOOL5_CLOSECOUNT.equalsIgnoreCase(typeName))
				{
					String itemValue = jdbcNode.valueOf("@val");
					closeCount = (int)Float.parseFloat(itemValue);
				}else if (WebsphereConstants.JDBCPOOL5_CREATCOUNT.equalsIgnoreCase(typeName))
				{
					String itemValue = jdbcNode.valueOf("@val");
					createCount = (int)Float.parseFloat(itemValue);
				}else if (WebsphereConstants.JDBCPOOL5_FAULT.equalsIgnoreCase(typeName))
				{
					String itemValue = jdbcNode.valueOf("@val");
					faultCount = (int)Float.parseFloat(itemValue);
				}else if (WebsphereConstants.JDBCPOOL5_FREEPOOL.equalsIgnoreCase(typeName))
				{
					String itemValue = jdbcNode.valueOf("@currentValue");
					freePoolSize = (int)Float.parseFloat(itemValue);
				}else if (WebsphereConstants.JDBCPOOL5_JDBCTIME.equalsIgnoreCase(typeName))
				{
					String itemValue = jdbcNode.valueOf("@mean");
					jdbcTime = (int)Float.parseFloat(itemValue);
				}else if (WebsphereConstants.JDBCPOOL5_PERCENTUSED.equalsIgnoreCase(typeName))
				{
					String itemValue =jdbcNode.valueOf("@currentValue");
					percentUsed = Float.parseFloat(itemValue);
				}else if (WebsphereConstants.JDBCPOOL5_PREPSTMTCACHE_DISCARD.equalsIgnoreCase(typeName))
				{
					String itemValue = jdbcNode.valueOf("@val");
					prepStmtCacheDiscardCount =(int)Float.parseFloat(itemValue);
				}else if (WebsphereConstants.JDBCPOOL5_SIZE.equalsIgnoreCase(typeName))
				{
					String itemValue = jdbcNode.valueOf("@currentValue");
					poolSize = (int)Float.parseFloat(itemValue);
				}else if (WebsphereConstants.JDBCPOOL5_USETIME.equalsIgnoreCase(typeName))
				{
					String itemValue = jdbcNode.valueOf("@mean");
					useTime = (int)Float.parseFloat(itemValue);
				}else if (WebsphereConstants.JDBCPOOL5_WAITTHREAD.equalsIgnoreCase(typeName))
				{
					String itemValue = jdbcNode.valueOf("@currentValue");
					waitingThreadCount = (int)Float.parseFloat(itemValue);
				}else if (WebsphereConstants.JDBCPOOL5_WAITTIME.equalsIgnoreCase(typeName))
				{
					String itemValue = jdbcNode.valueOf("@mean");
					waitTime = (int)Float.parseFloat(itemValue);
				}
				
				
			}
			Was5jdbc was5jdbc = new Was5jdbc();
			Calendar date=Calendar.getInstance();
			was5jdbc.setIpaddress(ip);
			was5jdbc.setAllocateCount(allocateCount);
			was5jdbc.setCloseCount(closeCount);
			was5jdbc.setCreateCount(createCount);
			was5jdbc.setFaultCount(faultCount);
			was5jdbc.setFreePoolSize(freePoolSize);
			was5jdbc.setJdbcTime(jdbcTime);
			was5jdbc.setPercentUsed(percentUsed);
			was5jdbc.setPoolSize(freePoolSize);
			was5jdbc.setPrepStmtCacheDiscardCount(prepStmtCacheDiscardCount);
			was5jdbc.setUseTime(useTime);
			was5jdbc.setWaitingThreadCount(waitingThreadCount);
			was5jdbc.setWaitTime(waitTime);
			was5jdbc.setRecordtime(date);
			
			WasConfigDao wasconfigdao=new WasConfigDao();
			try{
				wasconfigdao.createHostData(was5jdbc,"wasjdbc");
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				wasconfigdao.close();
			}
			jdbchst.put("allocateCount", allocateCount);
			jdbchst.put("closeCount", closeCount);
			jdbchst.put("createCount", createCount);
			jdbchst.put("faultCount", faultCount);
			jdbchst.put("freePoolSize", freePoolSize);
			jdbchst.put("jdbcTime", jdbcTime);
			jdbchst.put("percentUsed", percentUsed);
			jdbchst.put("prepStmtCacheDiscardCount", prepStmtCacheDiscardCount);
			jdbchst.put("poolSize", poolSize);
			jdbchst.put("useTime", useTime);
			jdbchst.put("waitingThreadCount", waitingThreadCount);
			jdbchst.put("waitTime", waitTime);
		}
		return jdbchst;
	}
	protected Hashtable getThread5ConfAndPerf(String ip,Node listServer,String nodeName,String serverName,String version)
	{
		String xpath5_Thread = "/PerformanceMonitor/Node[@name='"+nodeName+"']/Server[@name='"+serverName+"']/threadPoolModule/threadCreates/descendant::*"+
			"|/PerformanceMonitor/Node[@name='"+nodeName+"']/Server[@name='"+serverName+"']/threadPoolModule/threadDestroys/descendant::*"+
			"|/PerformanceMonitor/Node[@name='"+nodeName+"']/Server[@name='"+serverName+"']/threadPoolModule/activeThreads/descendant::*"+
			"|/PerformanceMonitor/Node[@name='"+nodeName+"']/Server[@name='"+serverName+"']/threadPoolModule/poolSize/descendant::*"+
			"|/PerformanceMonitor/Node[@name='"+nodeName+"']/Server[@name='"+serverName+"']/threadPoolModule/threadCreates/descendant::*";
		List<Node> threadList = listServer.getDocument().selectNodes(xpath5_Thread);
		
		if (null != threadList && threadList.size() > 0)
		{
				int createCount =0 ;
				int destroyCount  =0;
				int poolSize  =0;
				int activeCount  =0;
				
				for (Node threadNode:threadList)
				{
					String typeName = threadNode.getParent().getName();
					if (WebsphereConstants.THREADPOOL5_ACTIVE.equalsIgnoreCase(typeName))
					{
						String itemThreadValue = threadNode.valueOf("@currentValue");
						activeCount = (int)Float.parseFloat(itemThreadValue);
					}else if (WebsphereConstants.THREADPOOL5_CREAT.equalsIgnoreCase(typeName))
					{
						String itemThreadValue = threadNode.valueOf("@val");
						createCount = (int)Float.parseFloat(itemThreadValue);
					}else if (WebsphereConstants.THREADPOOL5_DESTROY.equalsIgnoreCase(typeName))
					{
						String itemThreadValue = threadNode.valueOf("@val");
						destroyCount = (int)Float.parseFloat(itemThreadValue);
					}else if (WebsphereConstants.THREADPOOL5_SIZE.equalsIgnoreCase(typeName))
					{
						String itemThreadValue = threadNode.valueOf("@currentValue");
						poolSize = (int)Float.parseFloat(itemThreadValue);
					}
				}
				Was5thread was5tread = new Was5thread();
				Calendar date=Calendar.getInstance();
				was5tread.setIpaddress(ip);
				was5tread.setActiveCount(activeCount);
				was5tread.setCreateCount(createCount);
				was5tread.setDestroyCount(destroyCount);
				was5tread.setPoolSize(poolSize);
				was5tread.setRecordtime(date);
				
				WasConfigDao wasconfigdao=new WasConfigDao();
				try{
					wasconfigdao.createHostData(was5tread,"wasthread");
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					wasconfigdao.close();
				}
				
				threadhst.put("activeCount", activeCount);
				threadhst.put("createCount", createCount);
				threadhst.put("destroyCount", destroyCount);
				threadhst.put("poolSize", poolSize);
				
		}
		return threadhst;
	}
	protected Hashtable getServlet5ConfAndPerf(String ip,Node listServer,String nodeName,String serverName,String version)
	{
		String xpath5_Servlet = "/PerformanceMonitor/" +
		"Node[@name='"+nodeName+"']/Server[@name='"+serverName+"']/servletSessionsModule/descendant-or-self::*";
		
		List<Node> listServlet = listServer.getDocument().selectNodes(xpath5_Servlet);
		
		if (null != listServlet && listServlet.size() > 0)
		{
			
				int liveCount= -1;
				int createCount=-1;
				int invalidateCount=-1;
				int lifeTime=-1;
				int activeCount=-1;
				int timeoutInvalidationCount=-1;
				
				for(Node servletNode:listServlet)
				{
					String typeName = servletNode.getParent().getName();
					if (WebsphereConstants.SERVLET5_ACTIVECOUNT.equalsIgnoreCase(typeName))//此处要填写指标了
					{
						String itemValue = servletNode.valueOf("@currentValue");//
						activeCount = (int)Float.parseFloat(itemValue);
						
					}else if (WebsphereConstants.SERVLET5_CREATECOUNT.equalsIgnoreCase(typeName))
					{
						String itemValue = servletNode.valueOf("@val");
						createCount = (int)Float.parseFloat(itemValue);
						
					}else if (WebsphereConstants.SERVLET5_INVALIDATECOUNT.equalsIgnoreCase(typeName))
					{
						String itemValue = servletNode.valueOf("@val");
						invalidateCount = (int)Float.parseFloat(itemValue);
						
					}else if (WebsphereConstants.SERVLET5_LIFETIME.equalsIgnoreCase(typeName))
					{
						String itemValue = servletNode.valueOf("@mean");////
						lifeTime = (int)Float.parseFloat(itemValue);
						
					}else if (WebsphereConstants.SERVLET5_LIVECOUNT.equalsIgnoreCase(typeName))
					{
						String itemValue = servletNode.valueOf("@currentValue");//
						liveCount = (int)Float.parseFloat(itemValue);
					}else if (WebsphereConstants.SERVLET5_TIMEOUTINVALIDATION.equalsIgnoreCase(typeName))
					{
						String itemValue = servletNode.valueOf("@val");
						timeoutInvalidationCount = (int)Float.parseFloat(itemValue);
					}
				}
				
				Was5session was5session = new Was5session();
				Calendar date=Calendar.getInstance();
				was5session.setIpaddress(ip);
				was5session.setActiveCount(activeCount);
				was5session.setCreateCount(createCount);
				was5session.setInvalidateCount(invalidateCount);
				was5session.setLifeTime(lifeTime);
				was5session.setLiveCount(liveCount);
				was5session.setTimeoutInvalidationCount(timeoutInvalidationCount);
				was5session.setRecordtime(date);
				
				WasConfigDao wasconfigdao=new WasConfigDao();
				try{
					wasconfigdao.createHostData(was5session,"wassession");
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					wasconfigdao.close();
				}
				
				servlethst.put("activeCount", activeCount);
				servlethst.put("createCount", createCount);
				servlethst.put("invalidateCount", invalidateCount);
				servlethst.put("lifeTime", lifeTime);
				servlethst.put("liveCount", liveCount);
				servlethst.put("timeoutInvalidationCount", timeoutInvalidationCount);
				
		}
		return servlethst;
	}
	protected Hashtable getSystemData5ConfAndPerf(String ip,Node listServer,String nodeName,String serverName,String version)
	{
		String xpath5_System = "/PerformanceMonitor/" +
		"Node[@name='"+nodeName+"']/Server[@name='"+serverName+"']/systemModule/descendant-or-self::*";
		List<Node> listSystem = listServer.getDocument().selectNodes(xpath5_System);
		if (null !=listSystem && listSystem.size() > 0)
		{		
				DecimalFormat df=new DecimalFormat("#");
				int cpuUsageSinceLastMeasurement = -1;
				int cpuUsageSinceServerStarted = -1;
				int freeMemory = -1;
				for (Node systemNode:listSystem)
				{
					String parentTypeName = systemNode.getParent().getName();
					if (WebsphereConstants.SYSTEM5_CPUUSAGESINCELAST.equalsIgnoreCase(parentTypeName))
					{
						String itemValue = systemNode.valueOf("@val");
						itemValue = df.format(Double.parseDouble(itemValue));
						cpuUsageSinceLastMeasurement = Integer.parseInt(itemValue);
						
					}else if (WebsphereConstants.SYSTEM5_CPUUSAGESINCESTARTED.equalsIgnoreCase(parentTypeName))
					{
						String itemValue = systemNode.valueOf("@mean");
						itemValue = df.format(Double.parseDouble(itemValue));
						cpuUsageSinceServerStarted = Integer.parseInt(itemValue);
					}else if (WebsphereConstants.SYSTEM5_FREEMEMORY.equalsIgnoreCase(parentTypeName))
					{
						String itemValue = systemNode.valueOf("@val");
						freeMemory = (int)Float.parseFloat(itemValue);
					}
				}
				
				Interfacecollectdata hostdata = new Interfacecollectdata();
				hostdata.setIpaddress(ip);
				Calendar date=Calendar.getInstance();
				hostdata.setCollecttime(date);
				hostdata.setCategory("WasrCpu");
				hostdata.setEntity("Utilization");
				hostdata.setSubentity("RunCPU");
				hostdata.setRestype("dynamic");
				hostdata.setUnit("%");
				hostdata.setThevalue(Math.round(cpuUsageSinceLastMeasurement)+"");
				
				
				
				WasConfigDao wasconfigdao=new WasConfigDao();
				try{
					wasconfigdao.createHostData(hostdata,"wasrcpu");
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					wasconfigdao.close();
				}
				
				hostdata = new Interfacecollectdata();
				hostdata.setIpaddress(ip);
				//Calendar date=Calendar.getInstance();
				hostdata.setCollecttime(date);
				hostdata.setCategory("WassCpu");
				hostdata.setEntity("Utilization");
				hostdata.setSubentity("SelCPU");
				hostdata.setRestype("dynamic");
				hostdata.setUnit("%");
				hostdata.setThevalue(Math.round(cpuUsageSinceServerStarted)+"");
				wasconfigdao=new WasConfigDao();
				try{
					wasconfigdao.createHostData(hostdata,"wasscpu");
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					wasconfigdao.close();
				}
				
				Was5system was5system = new Was5system();
				was5system.setIpaddress(ip);
				was5system.setCpuUsageSinceLastMeasurement(cpuUsageSinceLastMeasurement);
				was5system.setCpuUsageSinceServerStarted(cpuUsageSinceServerStarted);
				was5system.setFreeMemory(freeMemory);
				was5system.setRecordtime(date);
				
				wasconfigdao=new WasConfigDao();
				try{
					wasconfigdao.createHostData(was5system,"wassystem");
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					wasconfigdao.close();
				}
				
				systemdatahst.put("cpuUsageSinceLastMeasurement", cpuUsageSinceLastMeasurement);
				systemdatahst.put("cpuUsageSinceServerStarted", cpuUsageSinceServerStarted);
				systemdatahst.put("freeMemory", freeMemory);
				
				
				
		}
		return systemdatahst;
	}
	protected Hashtable getTranscation5ConfAndPerf(String ip,Node listServer,String nodeName,String serverName,String version)
	{
		String xpath5_Trans = "/PerformanceMonitor/" +
		"Node[@name='"+nodeName+"']/Server[@name='"+serverName+"']/transactionModule/descendant-or-self::*";
		
		List<Node> listTrans = listServer.getDocument().selectNodes(xpath5_Trans);
		
		if (null !=listTrans && listTrans.size() > 0)
		{
				int activeCount = -1;
				int committedCount = -1;
				int rolledbackCount = -1;
				int globalTranTime = -1;
				int globalBegunCount = -1;
				int localBegunCount = -1;
				int localActiveCount = -1;
				int localTranTime = -1;
				int localTimeoutCount = -1;
				int localRolledbackCount = -1;
				int globalTimeoutCount = -1;
				
				for (Node transNode:listTrans)
				{
					String parentName = transNode.getParent().getName();
					String itemValue = null;
					if (WebsphereConstants.TRANS5_ACTIVE.equalsIgnoreCase(parentName))
					{
						itemValue = transNode.valueOf("@val");
						activeCount =(int)Float.parseFloat(itemValue);
					}else if (WebsphereConstants.TRANS5_COMMITTED.equalsIgnoreCase(parentName))
					{
						itemValue = transNode.valueOf("@val");
						committedCount = (int)Float.parseFloat(itemValue);
					}else if (WebsphereConstants.TRANS5_GLOBALBEGUN.equalsIgnoreCase(parentName))
					{
						itemValue = transNode.valueOf("@val");
						globalBegunCount = (int)Float.parseFloat(itemValue);
					}else if (WebsphereConstants.TRANS5_GLOBALTIMEOUT.equalsIgnoreCase(parentName))
					{
						itemValue = transNode.valueOf("@val");
						globalTimeoutCount = (int)Float.parseFloat(itemValue);
					}else if (WebsphereConstants.TRANS5_GLOBALTRANTIME.equalsIgnoreCase(parentName))
					{
						itemValue = transNode.valueOf("@mean");
						globalTranTime = (int)Float.parseFloat(itemValue);
					}else if (WebsphereConstants.TRANS5_LOCALACTIVE.equalsIgnoreCase(parentName))
					{
						itemValue = transNode.valueOf("@val");
						localActiveCount = (int)Float.parseFloat(itemValue);
						
					}else if (WebsphereConstants.TRANS5_LOCALBEGUN.equalsIgnoreCase(parentName))
					{
						itemValue = transNode.valueOf("@val");
						localBegunCount = (int)Float.parseFloat(itemValue);
						
					}else if (WebsphereConstants.TRANS5_LOCALROLLBACK.equalsIgnoreCase(parentName))
					{
						itemValue = transNode.valueOf("@val");
						localRolledbackCount = (int)Float.parseFloat(itemValue);
						
					}else if (WebsphereConstants.TRANS5_LOCALTIMEOUT.equalsIgnoreCase(parentName))
					{
						itemValue = transNode.valueOf("@val");
						localTimeoutCount = (int)Float.parseFloat(itemValue);
						
					}else if (WebsphereConstants.TRANS5_LOCALTRANTIME.equalsIgnoreCase(parentName))
					{
						itemValue = transNode.valueOf("@mean");
						localTranTime = (int)Float.parseFloat(itemValue);
						
					}else if (WebsphereConstants.TRANS5_ROLLEDBACK.equalsIgnoreCase(parentName))
					{
						itemValue = transNode.valueOf("@val");
						rolledbackCount = (int)Float.parseFloat(itemValue);
						
					}
				}
				
				Was5trans was5trans = new Was5trans();
				Calendar date=Calendar.getInstance();
				was5trans.setIpaddress(ip);
				was5trans.setActiveCount(localActiveCount);
				was5trans.setCommittedCount(committedCount);
				was5trans.setGlobalBegunCount(globalBegunCount);
				was5trans.setGlobalTimeoutCount(globalTimeoutCount);
				was5trans.setGlobalTranTime(globalTranTime);
				was5trans.setLocalActiveCount(localActiveCount);
				was5trans.setLocalBegunCount(localBegunCount);
				was5trans.setLocalRolledbackCount(localRolledbackCount);
				was5trans.setLocalTimeoutCount(localTimeoutCount);
				was5trans.setLocalTranTime(localTranTime);
				was5trans.setRolledbackCount(localRolledbackCount);
				was5trans.setRecordtime(date);
				
				WasConfigDao wasconfigdao=new WasConfigDao();
				try{
					wasconfigdao.createHostData(was5trans,"wastrans");
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					wasconfigdao.close();
				}
				transhst.put("activeCount", activeCount);
				transhst.put("committedCount", committedCount);
				transhst.put("globalBegunCount", globalBegunCount);
				transhst.put("globalTimeoutCount", globalTimeoutCount);
				transhst.put("globalTranTime", globalTranTime);
				transhst.put("localActiveCount", localActiveCount);
				transhst.put("localBegunCount", localBegunCount);
				transhst.put("localRolledbackCount", localRolledbackCount);
				transhst.put("localTimeoutCount", localTimeoutCount);
				transhst.put("localTranTime", localTranTime);
				transhst.put("rolledbackCount", rolledbackCount);
		}
		return transhst;
	}
}
