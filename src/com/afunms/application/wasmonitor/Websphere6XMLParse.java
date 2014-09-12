package com.afunms.application.wasmonitor;



import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

import org.dom4j.Node;

import com.afunms.application.dao.WasConfigDao;
import com.afunms.polling.om.Interfacecollectdata;
import com.afunms.polling.om.Pingcollectdata;

public class Websphere6XMLParse {
	   Hashtable jvm7hst = new Hashtable();
	   Hashtable jdbc7hst = new Hashtable();
	   Hashtable thread7hst = new Hashtable();
	   Hashtable servlet7hst = new Hashtable();
	   Hashtable system7hst = new Hashtable();
	   Hashtable trans7hst = new Hashtable();
	   Hashtable extension7hst = new Hashtable();
	
	//要特别注意“添加节点对象”的时机，否则可能出现重复添加节点的情况
	public void getCacheConfAndPerf(Node listServer,String nodeName,String serverName,String version)
	{
		String xpathCache = "/PerformanceMonitor/"+"Node[@name='"+nodeName+"']/Server[@name='"+serverName+"']/Stat/Stat[@name=" +
				"'"+WebsphereConstants.WEBSPHERE_CACHE6+"']/Stat/following-sibling::*" +
				"|/PerformanceMonitor/"+"Node[@name='"+nodeName+"']/Server[@name='"+serverName+"']/Stat/Stat[@name='" +
				WebsphereConstants.WEBSPHERE_CACHE6+"']/Stat/self::*";
		List<Node> listCaches = listServer.getDocument().selectNodes(xpathCache);//拿到每个缓存的名称及总体高速缓存对应的指标。
		
		if (listCaches!= null && listCaches.size() > 0)
		{
			
			String part_id = nodeName+":"+serverName+":"+WebsphereConstants.WEBSPHERE_CACHE6;
			String mark_name = WebsphereConstants.WEBSPHERE_CACHE6;
			
			int inMemoryCacheCount =0;
			int maxInMemoryCacheCount =0;
			
			int missCount=0;
			int timeoutInvalidationCount = 0;
			int hitsInMemoryCount = 0;
			int inMemoryAndDiskCacheEntryCount = 0;
			int hitsOnDiskCount = 0;
			int clientRequestCount = 0;
			
			for (Node listCache:listCaches)
			{
				String cacheEntryName = listCache.valueOf("@name");
				System.out.println("cacheEntryName:---:"+cacheEntryName);
				String statName = listCache.getName();
				System.out.println("statName:---:"+statName);
				String countValue = listCache.valueOf("@count").toString();
				
				if (cacheEntryName.equalsIgnoreCase(WebsphereConstants.CACHE_HITSINMEM))
				{
					hitsInMemoryCount = Integer.parseInt(countValue);//得到总体高速缓存的指标
					System.out.println("hitsInMemoryCount:---:"+hitsInMemoryCount);
					
				}else if (cacheEntryName.equalsIgnoreCase(WebsphereConstants.CACHE_HITSONDISK))
				{
					hitsOnDiskCount = Integer.parseInt(countValue);//得到总体高速缓存的指标
				}else if (WebsphereConstants.CACHE_CLIENTREQUEST.equalsIgnoreCase(cacheEntryName))
				{
					clientRequestCount = Integer.parseInt(countValue);//得到总体高速缓存的指标
					
				}else if (WebsphereConstants.CACHE_HITSINMEMANDDISK.equalsIgnoreCase(cacheEntryName))
				{
					inMemoryAndDiskCacheEntryCount = Integer.parseInt(countValue);//得到总体高速缓存的指标
				}else if (WebsphereConstants.CACHE_MISSCOUNT.equalsIgnoreCase(cacheEntryName))
				{
					missCount = Integer.parseInt(countValue);//得到总体高速缓存的指标
				}else if (WebsphereConstants.CACHE_TIMEOUTINVALIDATION.equalsIgnoreCase(cacheEntryName))
				{
					timeoutInvalidationCount = Integer.parseInt(countValue);//得到总体高速缓存的指标
				}else if (WebsphereConstants.CACHE_MAXINMEMORYCACHEENTRYCOUNT.equalsIgnoreCase(cacheEntryName))
				{
					maxInMemoryCacheCount = Integer.parseInt(countValue);
				}else if (WebsphereConstants.CACHE_INMEMORYCACHEENTRYCOUNT.equalsIgnoreCase(cacheEntryName))
				{
					inMemoryCacheCount = Integer.parseInt(countValue);
				}
			}
			
		}
	}
	
	public Hashtable getExtensionConfAndPerf(String ip,Node listServer,String nodeName,String serverName,String version)
	{
		System.out.println("enter getExtensionConfAndPerf");
		String xpath_Extension =  "/PerformanceMonitor/"+"Node[@name='"+nodeName+"']/Server[@name='"+serverName+"']/Stat/Stat[@name=" +
			"'"+WebsphereConstants.WEBSPHERE_REGISTRY_CACHE6+"']/CountStatistic/following-sibling::*" +
			"|/PerformanceMonitor/"+"Node[@name='"+nodeName+"']/Server[@name='"+serverName+"']/Stat/Stat[@name='" +
			WebsphereConstants.WEBSPHERE_REGISTRY_CACHE6+"']/CountStatistic/self::*"+
			"|/PerformanceMonitor/"+"Node[@name='"+nodeName+"']/Server[@name='"+serverName+"']/Stat/Stat[@name='" +
			WebsphereConstants.WEBSPHERE_REGISTRY_CACHE6+"']/DoubleStatistic/self::*";
		
		List<Node> listExtensions = listServer.getDocument().selectNodes(xpath_Extension);
		
		if (null != listExtensions && listExtensions.size() > 0)
		{
			float hitRate = 0;
			//int hitRate = 0;
			int numRequests = 0;
			int numHits = 0 ;
			for (Node extensionNode: listExtensions)
			{
				String exNodeName = extensionNode.valueOf("@name");
				System.out.println("enter "+exNodeName);
				String itemValue = null;
				
				if (WebsphereConstants.EXTENSION_HITRATE.equalsIgnoreCase(exNodeName))
				{
					itemValue = extensionNode.valueOf("@double");
					hitRate =Float.parseFloat(itemValue);
				}else if (WebsphereConstants.EXTENSION_NUMHITS.equalsIgnoreCase(exNodeName))
				{
					itemValue = extensionNode.valueOf("@count");
					numHits = Integer.parseInt(itemValue);
					
				}else if (WebsphereConstants.EXTENSION_NUMQUESTS.equalsIgnoreCase(exNodeName))
				{
					itemValue = extensionNode.valueOf("@count");
					numRequests = Integer.parseInt(itemValue);
				}
			}
			Interfacecollectdata hostdata = new Interfacecollectdata();
			hostdata.setIpaddress(ip);
			Calendar date=Calendar.getInstance();
			hostdata.setCollecttime(date);
			hostdata.setCategory("WasRate");
			hostdata.setEntity("Utilization");
			hostdata.setSubentity("HitRate");
			hostdata.setRestype("dynamic");
			hostdata.setUnit("%");
			hostdata.setThevalue(Math.round(hitRate)+"");
			WasConfigDao wasconfigdao=new WasConfigDao();
			try{
				wasconfigdao.createHostData(hostdata,"wasrate");
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				wasconfigdao.close();
			}
			
			int nhitRate=(int)hitRate;
			Was5cache was5cache = new Was5cache();
			was5cache.setIpaddress(ip);
			was5cache.setInMemoryCacheCount(nhitRate);
			was5cache.setMaxInMemoryCacheCount(numRequests);
			was5cache.setTimeoutInvalidationCount(numHits);
			was5cache.setRecordtime(date);
			
			wasconfigdao=new WasConfigDao();
			try{
				wasconfigdao.createHostData(was5cache,"wascache");
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				wasconfigdao.close();
			}
			
//			extension7hst.put("hitRate", hitRate);
//			extension7hst.put("numHits", numHits);
//			extension7hst.put("numRequests", numRequests);
			extension7hst.put("inMemoryCacheCount", hitRate);
			extension7hst.put("timeoutInvalidationCount", numHits);
			extension7hst.put("maxInMemoryCacheCount", numRequests);
		}
		return extension7hst;
		
	}
	
	
	public Hashtable getSystemDataConfAndPerf(String ip,Node listServer,String nodeName,String serverName,String version)
	{
		String xpath_systemData =  "/PerformanceMonitor/"+"Node[@name='"+nodeName+"']/Server[@name='"+serverName+"']/Stat/Stat[@name=" +
		"'"+WebsphereConstants.WEBSPHERE_SYSTEM6+"']/CountStatistic/following-sibling::*" +
		"|/PerformanceMonitor/"+"Node[@name='"+nodeName+"']/Server[@name='"+serverName+"']/Stat/Stat[@name='" +
		WebsphereConstants.WEBSPHERE_SYSTEM6+"']/CountStatistic/self::*"+
		"|/PerformanceMonitor/"+"Node[@name='"+nodeName+"']/Server[@name='"+serverName+"']/Stat/Stat[@name='" +
		WebsphereConstants.WEBSPHERE_SYSTEM6+"']/AverageStatistic/self::*";;
		List<Node> listSystems = listServer.getDocument().selectNodes(xpath_systemData);
		String fPartID = nodeName + ":"+ serverName + ":" + WebsphereConstants.WEBSPHERE_SYSTEM6;
		
		if (null != listSystems && listSystems.size() > 0)
		{
			DecimalFormat df=new DecimalFormat("#");
			int cpuUsageSinceLastMeasurement = 0;
			int cpuUsageSinceServerStarted = 0;
			int freeMemory = 0;
			for (Node systemNode:listSystems)
			{
				String systemNodeName = systemNode.valueOf("@name");
				String itemValue = null;
				
				if (WebsphereConstants.SYSTEM_CPUUSAGESINCELAST.equalsIgnoreCase(systemNodeName))
				{
					itemValue = systemNode.valueOf("@count");
					itemValue = df.format(Double.parseDouble(itemValue));
					cpuUsageSinceLastMeasurement = Integer.parseInt(itemValue);
				}else if (WebsphereConstants.SYSTEM_CPUUSAGESINCESTARTED.equalsIgnoreCase(systemNodeName))
				{
					itemValue = systemNode.valueOf("@mean");
					itemValue = df.format(Double.parseDouble(itemValue));
					cpuUsageSinceServerStarted = Integer.parseInt(itemValue);
				}else if (WebsphereConstants.SYSTEM_FREEMEMORY.equalsIgnoreCase(systemNodeName))
				{
					itemValue = systemNode.valueOf("@count");
					freeMemory = Integer.parseInt(itemValue);
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
			
			system7hst.put("cpuUsageSinceLastMeasurement", cpuUsageSinceLastMeasurement);
			system7hst.put("cpuUsageSinceServerStarted", cpuUsageSinceServerStarted);
			system7hst.put("freeMemory", freeMemory);
		}
		return system7hst;
	}
	public Hashtable getTranscationConfAndPerf(String ip,Node listServer,String nodeName,String serverName,String version)
	{
		String xpath_transcation = "/PerformanceMonitor/"+"Node[@name='"+nodeName+"']/Server[@name='"+serverName+"']/Stat/Stat[@name=" +
			"'"+WebsphereConstants.WEBSPHERE_TRANSCATION6+"']/CountStatistic/following-sibling::*" +
			"|/PerformanceMonitor/"+"Node[@name='"+nodeName+"']/Server[@name='"+serverName+"']/Stat/Stat[@name='" +
			WebsphereConstants.WEBSPHERE_TRANSCATION6+"']/CountStatistic/self::*";
		
		List<Node> listTranscations = listServer.getDocument().selectNodes(xpath_transcation);
		String fPartID = nodeName + ":"+ serverName;
		if (null != listTranscations && listTranscations.size() > 0)
		{
			int activeCount = 0;
			int CountglobalTimeoutCount = 0;
			int committedCount = 0;
			int rolledbackCount = 0;
			int globalTranTime = 0;
			int globalBegunCount = 0;
			int localBegunCount = 0;
			int localActiveCount = 0;
			int localTranTime = 0;
			int localTimeoutCount = 0;
			int localRolledbackCount = 0;
			int globalTimeoutCount = 0;
			
			for(Node transNode: listTranscations)
			{
				String transNodeName = transNode.valueOf("@name");
				String itemValue = null;
				if (WebsphereConstants.TRANS_ACTIVE.equalsIgnoreCase(transNodeName))
				{   
					
					itemValue = transNode.valueOf("@count");
					if(itemValue==null)continue;
					activeCount = Integer.parseInt(itemValue);
				}else if (WebsphereConstants.TRANS_COMMITTED.equalsIgnoreCase(transNodeName))
				{
					itemValue = transNode.valueOf("@count");
					committedCount = Integer.parseInt(itemValue);
				}else if (WebsphereConstants.TRANS_GLOBALBEGUN.equalsIgnoreCase(transNodeName))
				{
					itemValue = transNode.valueOf("@count");
					globalBegunCount = Integer.parseInt(itemValue);
				}else if (WebsphereConstants.TRANS_GLOBALTIMEOUT.equalsIgnoreCase(transNodeName))
				{
					itemValue = transNode.valueOf("@count");
					globalTimeoutCount = Integer.parseInt(itemValue);
				}else if (WebsphereConstants.TRANS_GLOBALTRANTIME.equalsIgnoreCase(transNodeName))
				{
					itemValue = transNode.valueOf("@totalTime");
					globalTranTime = Integer.parseInt(itemValue);
				}else if (WebsphereConstants.TRANS_LOCALACTIVE.equalsIgnoreCase(transNodeName))
				{
					itemValue = transNode.valueOf("@count");
					localActiveCount = Integer.parseInt(itemValue);
					
				}else if (WebsphereConstants.TRANS_LOCALBEGUN.equalsIgnoreCase(transNodeName))
				{
					itemValue = transNode.valueOf("@count");
					localBegunCount = Integer.parseInt(itemValue);
					
				}else if (WebsphereConstants.TRANS_LOCALROLLBACK.equalsIgnoreCase(transNodeName))
				{
					itemValue = transNode.valueOf("@count");
					localRolledbackCount = Integer.parseInt(itemValue);
					
				}else if (WebsphereConstants.TRANS_LOCALTIMEOUT.equalsIgnoreCase(transNodeName))
				{
					itemValue = transNode.valueOf("@count");
					localTimeoutCount = Integer.parseInt(itemValue);
					
				}else if (WebsphereConstants.TRANS_LOCALTRANTIME.equalsIgnoreCase(transNodeName))
				{
					itemValue = transNode.valueOf("@totalTime");
					localTranTime = Integer.parseInt(itemValue);
					
				}else if (WebsphereConstants.TRANS_ROLLEDBACK.equalsIgnoreCase(transNodeName))
				{
					itemValue = transNode.valueOf("@count");
					if(itemValue==null)continue;
					rolledbackCount = Integer.parseInt(itemValue);
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
			
			trans7hst.put("activeCount", activeCount);
			trans7hst.put("committedCount", committedCount);
			trans7hst.put("globalBegunCount", globalBegunCount);
			trans7hst.put("globalTimeoutCount", globalTimeoutCount);
			trans7hst.put("globalTranTime", globalTranTime);
			trans7hst.put("localActiveCount", localActiveCount);
			trans7hst.put("localBegunCount", localBegunCount);
			trans7hst.put("localRolledbackCount", localRolledbackCount);
			trans7hst.put("localTimeoutCount", localTimeoutCount);
			trans7hst.put("localTranTime", localTranTime);
			trans7hst.put("rolledbackCount", rolledbackCount);
			
			
			
			
		}
		return trans7hst;
	}

	
	public Hashtable getThreadConfAndPerf(String ip,Node listServer,String nodeName,String serverName,String version)
	{
		String xpath_thread = "/PerformanceMonitor/"+"Node[@name='"+nodeName+"']/Server[@name='"+serverName+"']/Stat/Stat[@name=" +
		"'"+WebsphereConstants.WEBSPHERE_THREADPOOL6+"']/Stat/following-sibling::*" +
		"|/PerformanceMonitor/"+"Node[@name='"+nodeName+"']/Server[@name='"+serverName+"']/Stat/Stat[@name='" +
		WebsphereConstants.WEBSPHERE_THREADPOOL6+"']/Stat/self::*";
		List<Node> listThreads = listServer.getDocument().selectNodes(xpath_thread);//获得每个线程池的名称和总体的指标
		
		String fPartID = nodeName+":"+serverName+":"+WebsphereConstants.WEBSPHERE_THREADPOOL6;
		if (null != listThreads && listThreads.size() > 0)
		{
			int createCount =0 ;
			int destroyCount  =0;
			int poolSize  =0;
			int activeCount  =0;
			int activeTime =0;
			for(Node thread:listThreads)
			{
				String thread_name = thread.valueOf("@name");
				
				String stat_mark = thread.getName();
				
				String itemThreadValue = null;
				
				
				if (WebsphereConstants.THREADPOOL_ACTIVE.equalsIgnoreCase(thread_name))
				{
					itemThreadValue = thread.valueOf("@value");
					activeCount = Integer.parseInt(itemThreadValue);
					
				}else if (WebsphereConstants.THREADPOOL_ACTIVETIME.equalsIgnoreCase(thread_name))
				{
					itemThreadValue = thread.valueOf("@totalTime");
					activeTime = Integer.parseInt(itemThreadValue);
				}else if (WebsphereConstants.THREADPOOL_CREAT.equalsIgnoreCase(thread_name))
				{
					itemThreadValue = thread.valueOf("@count");
					createCount = Integer.parseInt(itemThreadValue);
					
				}else if (WebsphereConstants.THREADPOOL_DESTROY.equalsIgnoreCase(thread_name))
				{
					itemThreadValue = thread.valueOf("@count");
					destroyCount = Integer.parseInt(itemThreadValue);
					
				}else if (WebsphereConstants.THREADPOOL_SIZE.equalsIgnoreCase(thread_name))
				{
					itemThreadValue = thread.valueOf("@value");
					poolSize = Integer.parseInt(itemThreadValue);
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
			
			thread7hst.put("activeCount",activeCount);
			thread7hst.put("activeTime",activeTime);
			thread7hst.put("createCount",createCount);
			thread7hst.put("destroyCount",destroyCount);
			thread7hst.put("poolSize",poolSize);
		}
		return thread7hst;
	}
	
	public Hashtable getServletConfAndPerf(String ip,Node listServer,String nodeName,String serverName,String version)
	{
		String xpath_servlet = "/PerformanceMonitor/"+"Node[@name='"+nodeName+"']/Server[@name='"+serverName+"']/Stat/Stat[@name=" +
		"'"+WebsphereConstants.WEBSPHERE_SERVLET6+"']/Stat/following-sibling::*" +
		"|/PerformanceMonitor/"+"Node[@name='"+nodeName+"']/Server[@name='"+serverName+"']/Stat/Stat[@name='" +
		WebsphereConstants.WEBSPHERE_SERVLET6+"']/Stat/self::*";
		List<Node> listServlets = listServer.getDocument().selectNodes(xpath_servlet);
		
		
		if (null != listServlets && listServlets.size() > 0)
		{
			String fPartID = nodeName + ":" + serverName + ":" + WebsphereConstants.WEBSPHERE_SERVLET6;
			int liveCount=0;
			int createCount=0;
			int invalidateCount=0;
			int lifeTime=0;
			int activeCount=0;
			int timeoutInvalidationCount=0;
			
			for (Node servletNode : listServlets)
			{
				String servletNodeName = servletNode.valueOf("@name");
				
				String stat_mark = servletNode.getName();
				
				String itemValue = null;
				
				if (WebsphereConstants.SERVLET_ACTIVECOUNT.equalsIgnoreCase(servletNodeName))//此处要填写指标了
				{
					itemValue = servletNode.valueOf("@value");//
					activeCount = Integer.parseInt(itemValue);
				}else if (WebsphereConstants.SERVLET_CREATECOUNT.equalsIgnoreCase(servletNodeName))
				{
					itemValue = servletNode.valueOf("@count");
					createCount = Integer.parseInt(itemValue);
					
				}else if (WebsphereConstants.SERVLET_INVALIDATECOUNT.equalsIgnoreCase(servletNodeName))
				{
					itemValue = servletNode.valueOf("@count");
					invalidateCount = Integer.parseInt(itemValue);
					
				}else if (WebsphereConstants.SERVLET_LIFETIME.equalsIgnoreCase(servletNodeName))
				{
					itemValue = servletNode.valueOf("@totalTime");////
					lifeTime = Integer.parseInt(itemValue);
					
				}else if (WebsphereConstants.SERVLET_LIVECOUNT.equalsIgnoreCase(servletNodeName))
				{
					itemValue = servletNode.valueOf("@value");//
					liveCount = Integer.parseInt(itemValue);
				}else if (WebsphereConstants.SERVLET_TIMEOUTINVALIDATION.equalsIgnoreCase(servletNodeName))
				{
					itemValue = servletNode.valueOf("@count");
					timeoutInvalidationCount = Integer.parseInt(itemValue);
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
			
			servlet7hst.put("activeCount",activeCount);
			servlet7hst.put("createCount",createCount);
			servlet7hst.put("invalidateCount",invalidateCount);
			servlet7hst.put("lifeTime",lifeTime);
			servlet7hst.put("liveCount",liveCount);
			servlet7hst.put("timeoutInvalidationCount",timeoutInvalidationCount);
		}
		return servlet7hst;
	}
	
	public Hashtable getJDBCConfAndPerf(String ip,Node listServer,String nodeName,String serverName,String version)
	{
		String xpath_jdbc = "/PerformanceMonitor/"+"Node[@name='"+nodeName+"']/Server[@name='"+serverName+"']/Stat/Stat[@name=" +
			"'"+WebsphereConstants.WEBSPHERE_JDBC6+"']/Stat/following-sibling::*" +
			"|/PerformanceMonitor/"+"Node[@name='"+nodeName+"']/Server[@name='"+serverName+"']/Stat/Stat[@name='" +
			WebsphereConstants.WEBSPHERE_JDBC6+"']/Stat/self::*";
		List<Node> listJDBCs = listServer.getDocument().selectNodes(xpath_jdbc);//获得每个JDBC连接池的名称和总体的指标
		if (null != listJDBCs && listJDBCs.size() > 0)
		{
			
			int createCount = 0;
			int closeCount = 0;
			int poolSize = 0;
			int freePoolSize = 0;
			int waitingThreadCount = 0;
			float percentUsed = 0;
			int useTime = 0;
			int waitTime =0;
			int allocateCount = 0;
			int prepStmtCacheDiscardCount = 0;
			int jdbcTime = 0;
			int faultCount = 0;
			//......
			for (Node jdbcNode : listJDBCs)
			{
				String jdbcNodeName = jdbcNode.valueOf("@name");
				String markName = jdbcNode.getName();
				String itemValue = null;
				
				if (WebsphereConstants.JDBCPOOL_ALLOCATECOUNT.equalsIgnoreCase(jdbcNodeName))
				{
					itemValue = jdbcNode.valueOf("@count");
					allocateCount = Integer.parseInt(itemValue);
				}else if (WebsphereConstants.JDBCPOOL_CLOSECOUNT.equalsIgnoreCase(jdbcNodeName))
				{
					itemValue = jdbcNode.valueOf("@count");
					closeCount = Integer.parseInt(itemValue);
				}else if (WebsphereConstants.JDBCPOOL_CREATCOUNT.equalsIgnoreCase(jdbcNodeName))
				{
					itemValue = jdbcNode.valueOf("@count");
					createCount = Integer.parseInt(itemValue);
				}else if (WebsphereConstants.JDBCPOOL_FAULT.equalsIgnoreCase(jdbcNodeName))
				{
					itemValue = jdbcNode.valueOf("@count");
					faultCount = Integer.parseInt(itemValue);
				}else if (WebsphereConstants.JDBCPOOL_FREEPOOL.equalsIgnoreCase(jdbcNodeName))
				{
					itemValue = jdbcNode.valueOf("@value");
					freePoolSize = Integer.parseInt(itemValue);
				}else if (WebsphereConstants.JDBCPOOL_JDBCTIME.equalsIgnoreCase(jdbcNodeName))
				{
					itemValue = jdbcNode.valueOf("@totalTime");
					jdbcTime = Integer.parseInt(itemValue);
				}else if (WebsphereConstants.JDBCPOOL_PERCENTUSED.equalsIgnoreCase(jdbcNodeName))
				{
					itemValue = jdbcNode.valueOf("@value");
					percentUsed = Float.parseFloat(itemValue);
				}else if (WebsphereConstants.JDBCPOOL_PREPSTMTCACHE_DISCARD.equalsIgnoreCase(jdbcNodeName))
				{
					itemValue = jdbcNode.valueOf("@count");
					prepStmtCacheDiscardCount = Integer.parseInt(itemValue);
				}else if (WebsphereConstants.JDBCPOOL_SIZE.equalsIgnoreCase(jdbcNodeName))
				{
					itemValue = jdbcNode.valueOf("@value");
					poolSize = Integer.parseInt(itemValue);
				}else if (WebsphereConstants.JDBCPOOL_USETIME.equalsIgnoreCase(jdbcNodeName))
				{
					itemValue = jdbcNode.valueOf("@totalTime");
					useTime = Integer.parseInt(itemValue);
				}else if (WebsphereConstants.JDBCPOOL_WAITTHREAD.equalsIgnoreCase(jdbcNodeName))
				{
					itemValue = jdbcNode.valueOf("@value");
					waitingThreadCount = Integer.parseInt(itemValue);
				}else if (WebsphereConstants.JDBCPOOL_WAITTIME.equalsIgnoreCase(jdbcNodeName))
				{
					itemValue = jdbcNode.valueOf("@totalTime");
					waitTime = Integer.parseInt(itemValue);
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
			jdbc7hst.put("allocateCount",allocateCount);
			jdbc7hst.put("closeCount",closeCount);
			jdbc7hst.put("createCount",createCount);
			jdbc7hst.put("faultCount",faultCount);
			jdbc7hst.put("freePoolSize",freePoolSize);
			jdbc7hst.put("jdbcTime",jdbcTime);
			jdbc7hst.put("percentUsed",percentUsed);
			jdbc7hst.put("prepStmtCacheDiscardCount",prepStmtCacheDiscardCount);
			jdbc7hst.put("poolSize",poolSize);
			jdbc7hst.put("useTime",useTime);
			jdbc7hst.put("waitingThreadCount",waitingThreadCount);
			jdbc7hst.put("waitTime",waitTime);
			
		}
		return jdbc7hst;
	}
	public Hashtable getJVMConfAndPerf(String ip,Node listServer,String nodeName,String serverName,String version)
	{
		String xpathJVM = "/PerformanceMonitor/"+"Node[@name='"+nodeName+"']/Server[@name='"+serverName+"']/Stat/Stat[@name=" +
				"'"+WebsphereConstants.WEBSPHERE_JVM6+"']/CountStatistic/following-sibling::*" +
				"|/PerformanceMonitor/"+"Node[@name='"+nodeName+"']/Server[@name='"+serverName+"']/Stat/Stat[@name='" +
				WebsphereConstants.WEBSPHERE_JVM6+"']/CountStatistic/self::*"+
				"|/PerformanceMonitor/"+"Node[@name='"+nodeName+"']/Server[@name='"+serverName+"']/Stat/Stat[@name='" +
				WebsphereConstants.WEBSPHERE_JVM6+"']/BoundedRangeStatistic/self::*";
		List<Node> jvmLists = listServer.getDocument().selectNodes(xpathJVM);//拿到JVM节点下面的所有指标，不包括jvm这个节点，这个节点是固定的
		if (null != jvmLists && jvmLists.size() > 0)
		{
				int heapSize = 0;//Java虚拟机运行时总内存
				int freeMemory = 0; // java虚拟机运行时空闲的内存数量
				int usedMemory = 0;//Java虚拟机运行时使用的内存数量
				int upTime  =0;//已经运行的时间数
				int processCpuUsage = 0;
				int memPer = 0;
				for(Node jvmNode:jvmLists)
				{
					String jvmItemName = jvmNode.valueOf("@name");
					String itemValue = null;
					
					if (WebsphereConstants.JVM_FREEMEMORY.equalsIgnoreCase(jvmItemName))
					{
						itemValue = jvmNode.valueOf("@count");
						freeMemory = Integer.parseInt(itemValue);
					}else if (WebsphereConstants.JVM_HEAPSIZE.equalsIgnoreCase(jvmItemName))
					{
						itemValue = jvmNode.valueOf("@value");
						heapSize = Integer.parseInt(itemValue);
					}else if (WebsphereConstants.JVM_PROCESSCPU.equalsIgnoreCase(jvmItemName))
					{
						itemValue = jvmNode.valueOf("@count");
						processCpuUsage = Integer.parseInt(itemValue);
					}else if (WebsphereConstants.JVM_UPTIME.equalsIgnoreCase(jvmItemName))
					{
						itemValue = jvmNode.valueOf("@count");
						upTime = Integer.parseInt(itemValue);
					}else if (WebsphereConstants.JVM_USEDMEMORY.equalsIgnoreCase(jvmItemName))
					{
						itemValue = jvmNode.valueOf("@count");
						usedMemory = Integer.parseInt(itemValue);
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
			jvm7hst.put("freeMemory", freeMemory);
			jvm7hst.put("heapSize", heapSize);
			jvm7hst.put("processCpuUsage", processCpuUsage);
			jvm7hst.put("upTime", upTime);
			jvm7hst.put("usedMemory", usedMemory);
			jvm7hst.put("memPer", memPer);
		}
		return jvm7hst;
	}
}
