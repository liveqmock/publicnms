package com.afunms.application.wasmonitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.afunms.application.dao.WasConfigDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.util.SysLogger;


public class UrlConncetWas {
   public Hashtable ConncetWas(String ip,String port,String username,String password,String version){
	   Hashtable washst = new Hashtable();
	   if(!"".equalsIgnoreCase(username)){
			//如果对perfServletApp设置了访问权限，需要利用Authenticator类来保存访问所需要的用户名和密码，如果对全部用户开放访问权限，则不需要此步骤
			Authenticator.setDefault(new MyAuthenticators(username,password));
		}
       StringBuffer sb = new StringBuffer();
       int responseTime = 0;//服务器响应时间
       BufferedReader stdIn = null;
       InputStream input = null;
       boolean connectFlag = true;
       try {
		long timeB = System.currentTimeMillis();
		
		String urlstr = "http://"+ip+":"+port+"/wasPerfTool/servlet/perfservlet?refreshConfig=true";
		URL url = new URL(urlstr);
		
		HttpURLConnection urlCon = (HttpURLConnection)url.openConnection(); 
		
		urlCon.setConnectTimeout(30000);//设置超时
		
		// 打开到此 URL 的连接并返回一个用于从该连接读入的 InputStream。
		//input = url.openStream();
		input = urlCon.getInputStream();
		
		stdIn = new BufferedReader(new InputStreamReader(input,"UTF-8"));

		long timeE = System.currentTimeMillis();
		responseTime = (int) (timeE - timeB);
		//System.out.println("本次采集连接服务器响应时间为：" +responseTime+"毫秒");
		
		String strLine;
		while ((strLine = stdIn.readLine()) != null) {
			if (strLine.indexOf("performancemonitor.dtd")!= -1)
		        		  continue;
			sb.append(strLine);
			
		}
	
		Document docRoot = getDocumentFromXML(sb.toString());
		if (version.indexOf("V5") != -1)
		{
			
			System.out.println("调用V5版本方法！");
			washst = getWebsphere5XML(ip,docRoot,version);
			return washst;
		}else if(version.indexOf("V6") != -1){
			System.out.println("调用V6版本方法！");
			washst = getWebsphere61XML(ip,docRoot,version);
			return washst;
		}else
		{
			
			System.out.println("开始监控WAS-7.0");
			washst = getWebsphere7XML(ip,docRoot,version);
			System.out.println("was-7 washst =====:"+washst.size());
			return washst;
		}
       }catch(Exception e){
    	   connectFlag = false;
       }finally {
			if (stdIn != null) {
				try {
					stdIn.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if( input != null){
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
       
	return washst;
   }
   
   public Hashtable ConncetWas(String ip,String port,String username,String password,String version,Hashtable gatherhash){
	   Hashtable<String,String> washst = new Hashtable<String,String>();
	   if(!"".equalsIgnoreCase(username)){
			//如果对perfServletApp设置了访问权限，需要利用Authenticator类来保存访问所需要的用户名和密码，如果对全部用户开放访问权限，则不需要此步骤
			Authenticator.setDefault(new MyAuthenticators(username,password));
		}
       StringBuffer sb = new StringBuffer();
       int responseTime = 0;//服务器响应时间
       BufferedReader stdIn = null;
       InputStream input = null;
       boolean connectFlag = true;
       try {
			long timeB = System.currentTimeMillis();

			String urlstr = "http://" + ip + ":" + port
					+ "/wasPerfTool/servlet/perfservlet?refreshConfig=true";
			URL url = new URL(urlstr);

			HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();

			urlCon.setConnectTimeout(30000);// 设置超时

			// 打开到此 URL 的连接并返回一个用于从该连接读入的 InputStream。
			// input = url.openStream();
			input = urlCon.getInputStream();

			stdIn = new BufferedReader(new InputStreamReader(input, "UTF-8"));

			long timeE = System.currentTimeMillis();
			responseTime = (int) (timeE - timeB);
			// System.out.println("本次采集连接服务器响应时间为：" +responseTime+"毫秒");

			String strLine;
			while ((strLine = stdIn.readLine()) != null) {
				if (strLine.indexOf("performancemonitor.dtd") != -1)
					continue;
				sb.append(strLine);
			}
			Document docRoot = getDocumentFromXML(sb.toString());

			
			if (version.indexOf("V5") != -1) {
				System.out.println("开始监控WAS-5");
				washst = getWebsphere5XML(ip, docRoot, version, gatherhash);
			} else if (version.indexOf("V6") != -1){
				System.out.println("开始监控WAS-6 OR WAS-7");
				washst = getWebsphere6XML(ip, docRoot, version, gatherhash);
			} else {
				System.out.println("开始监控WAS-7.0");
				washst = getWebsphere7XML(ip, docRoot, version, gatherhash);
			}
//			washst.put("ping", "100");
			return washst;
		} catch (Exception e) {
			connectFlag = false;
//			washst.put("ping", "0");
		} finally {
			if (stdIn != null) {
				try {
					stdIn.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if( input != null){
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return washst;
   }
   
   public void  parsexml(String ip,String xmlView,String version)
	{
		Document docRoot = getDocumentFromXML(xmlView);
		//1:首先取得版本号
		//如果版本是5.x,则调用5版本的XML文件解析方法,其它情况暂时调用6版本的XML文件解析方法

		if (version.indexOf("V5") != -1)
		{
			
			
			getWebsphere5XML(ip,docRoot,version);
			
		}
		else
		{
			
			//System.out.println("开始监控WAS-7.0");
			getWebsphere7XML(ip,docRoot,version);
		}
	}
   
   private String getResponseStatus(String xmlView,String version)
	{
		System.out.println("进入getResponseStatus方法");
		Document docRoot = getDocumentFromXML(xmlView);
		String responseStatus = "failed";
		String responseStatus_Temp = docRoot.getRootElement().attributeValue("responseStatus");
		if (null == responseStatus_Temp)
		{
			return "success";//如果为空，是5.1版本，5.1版本中没有这个属性。暂时先这样！还样优化处理，通过是否有<Comments>标签来判断
		}
		
		if (responseStatus.equalsIgnoreCase(responseStatus_Temp))
		{
			return responseStatus;
		}
		else 
		{
			return "success";
		}
		
	}
   
   private Document getDocumentFromXML(String xmlView)
	{
		if (xmlView == null )
			return null;
		Document resultXMLDoc = null;
		SAXReader saxReader =  new SAXReader();
		try {
			resultXMLDoc = saxReader.read(new StringReader(xmlView));
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return resultXMLDoc;
	}
   
   private Hashtable getWebsphere61XML(String ip,Document doc,String version){
	   Hashtable<String, Hashtable<?, ?>> was6hst = new Hashtable();
	   Hashtable jvm7hst = new Hashtable();
	   Hashtable jdbc7hst = new Hashtable();
	   Hashtable thread7hst = new Hashtable();
	   Hashtable servlet7hst = new Hashtable();
	   Hashtable system7hst = new Hashtable();
	   Hashtable trans7hst = new Hashtable();
	   Hashtable extension7hst = new Hashtable();
	   
		
		List<Node> listNodes = doc.selectNodes("//Node");
		
		if (listNodes == null || listNodes.size() == 0)
		{
			return null;
		}
		for (Node nodeTmp : listNodes) {
			String nodeName = nodeTmp.valueOf("@name");//获取到节点的名称
			//1:获取node
			
			//2:获取server
			String xpathServer = "/PerformanceMonitor/"+"Node[@name='"+nodeName+"']/Server/following-sibling::*"+
								"|/PerformanceMonitor/"+"Node[@name='"+nodeName+"']/Server/self::*";
			
			List<Node> listServers = nodeTmp.getDocument().selectNodes(xpathServer);
			if (listServers == null || listServers.size() == 0)
			{
				//如果得到的server为Null,或者得到的个数为0,不要直接返回Null,还需把节点的名称返回过去，可以考虑返回null.
				return  null;
			}
			for(Node listServer : listServers)
			{
				String serverName = listServer.valueOf("@name");//得到了serverName,其父节点为Node;
				
				System.out.println("serverName----:"+serverName);
				
				//还有好几个监控指标的类型，此处还要添加几个，go on......
				
				//3:获取Cache
				
				Websphere6XMLParse xml6Parse = new Websphere6XMLParse();
//				xml6Parse.getCacheConfAndPerf(listServer,nodeName,serverName,version,
//						cacheList,cachePerfList,cacheInstanceList,cacheInstancePerfList);
//				//4:得到JVM，及其指标
				jvm7hst = xml6Parse.getJVMConfAndPerf(ip,listServer,nodeName,serverName,version);
//				5:得到webapplication应用，及其指标，取法和cache相似		
//				xml6Parse.getWebapplicationConfAndPerf(listServer,nodeName,serverName,version,
//							webapplicationTotalList,webapplicationInstancelList,webapplicationTotalPerfList,
//							webapplicationInstancePerfList);
				//6：得到JDBC连接池，取法和Cache相似
				jdbc7hst = xml6Parse.getJDBCConfAndPerf( ip,listServer, nodeName, serverName, version);
				//7:得到Thread连接池，取法和cache相似
				thread7hst = xml6Parse.getThreadConfAndPerf(ip, listServer, nodeName, serverName, version);
				//8:得到Servlet Session，取法cache相似
				servlet7hst = xml6Parse.getServletConfAndPerf(ip, listServer, nodeName, serverName, version);
				//9:得到System数据，主要是CPU数据，和JVM取法一样
				system7hst = xml6Parse.getSystemDataConfAndPerf(ip, listServer, nodeName, serverName, version);
				//10:得到transcation事务，和JVM取法一下，不需要再去判断下一级
				trans7hst = xml6Parse.getTranscationConfAndPerf(ip, listServer, nodeName, serverName, version);
				//11:得到缓存的相关指标，如：所有命中数，所有请求数，命中率
				extension7hst = xml6Parse.getExtensionConfAndPerf(ip, listServer, nodeName, serverName, version);
				
			}
			was6hst.put("jvm7hst", jvm7hst);
			was6hst.put("jdbc7hst", jdbc7hst);
			was6hst.put("thread7hst", thread7hst);
			was6hst.put("servlet7hst", servlet7hst);
			was6hst.put("system7hst", system7hst);
			was6hst.put("trans7hst", trans7hst);
			was6hst.put("extension7hst", extension7hst);
		}
		return was6hst;
	   
   }
   private Hashtable getWebsphere7XML(String ip, Document doc, String version, Hashtable gatherhash) {
		Hashtable<String, Hashtable<?, ?>> was7hst = new Hashtable();
		Hashtable jvm7hst = new Hashtable();
		Hashtable jdbc7hst = new Hashtable();
		Hashtable thread7hst = new Hashtable();
		Hashtable servlet7hst = new Hashtable();
		Hashtable system7hst = new Hashtable();
		Hashtable trans7hst = new Hashtable();
		Hashtable extension7hst = new Hashtable();

		// 1:获取node
		List<Node> listNodes = doc.selectNodes("//Node");// 所有Node元素
		System.out.println("###### Was 7 listNodes.size() : " + listNodes.size());
		if (listNodes == null || listNodes.size() == 0) {
			return null;
		}
		for (Node nodeTmp : listNodes) {
			String nodeName = nodeTmp.valueOf("@name");// 获取到节点的名称

			// 2:获取server
			String xpathServer = "/PerformanceMonitor/" + "Node[@name='" + nodeName + "']/Server/following-sibling::*" + "|/PerformanceMonitor/" + "Node[@name='" + nodeName + "']/Server/self::*";

			List<Node> listServers = nodeTmp.getDocument().selectNodes(xpathServer);
			System.out.println("###### Was 7 listServers size: " + listServers.size());
			if (listServers == null || listServers.size() == 0) {
				// 如果得到的server为Null,或者得到的个数为0,不要直接返回Null,还需把节点的名称返回过去，可以考虑返回null.
				return null;
			}
			for (Node listServer : listServers) {
				String serverName = listServer.valueOf("@name");// 得到了serverName,其父节点为Node;
				Websphere7XMLParse xml7Parse = new Websphere7XMLParse();
				// 3:获取Cache
				// xml7Parse.getCacheConfAndPerf(listServer, nodeName,
				// serverName, version, cacheList, cachePerfList,
				// cacheInstanceList, cacheInstancePerfList);
				// 4:得到JVM，及其指标
				//if (gatherhash.containsKey("jvm"))
					jvm7hst = xml7Parse.getJVMConfAndPerf(ip, listServer, nodeName, serverName, version);
				System.out.println("###### Was 7 jvm7hst.size() : " + jvm7hst.size());
				// 5:得到webapplication应用，及其指标，取法和cache相似
				// xml7Parse.getWebapplicationConfAndPerf(listServer, nodeName,
				// serverName, version, webapplicationTotalList,
				// webapplicationInstancelList, webapplicationTotalPerfList,
				// webapplicationInstancePerfList);
				// 6：得到JDBC连接池，取法和Cache相似
				//if (gatherhash.containsKey("jdbc"))
					jdbc7hst = xml7Parse.getJDBCConfAndPerf(ip, listServer, nodeName, serverName, version);
				System.out.println("###### Was 7 jdbc7hst.size() : " + jdbc7hst.size());
				// 7:得到Thread连接池，取法和cache相似
				//if (gatherhash.containsKey("thread"))
					thread7hst = xml7Parse.getThreadConfAndPerf(ip, listServer, nodeName, serverName, version);
				System.out.println("###### Was 7 thread7hst.size() : " + thread7hst.size());
				// 8:得到Servlet Session，取法cache相似
				//if (gatherhash.containsKey("session"))
					servlet7hst = xml7Parse.getServletConfAndPerf(ip, listServer, nodeName, serverName, version);
				System.out.println("###### Was 7 servlet7hst.size() : " + servlet7hst.size());
				// 9:得到System数据，主要是CPU数据，和JVM取法一样
				//if (gatherhash.containsKey("system"))
					system7hst = xml7Parse.getSystemDataConfAndPerf(ip, listServer, nodeName, serverName, version);
				System.out.println("###### Was 7 system7hst.size() : " + system7hst.size());
				// 10:得到transcation事务，和JVM取法一下，不需要再去判断下一级
				//if (gatherhash.containsKey("orb"))
					trans7hst = xml7Parse.getTranscationConfAndPerf(ip, listServer, nodeName, serverName, version);
				//System.out.println("###### Was 7 trans7hst.size() : " + trans7hst.size());
				// 11:得到缓存的相关指标，如：所有命中数，所有请求数，命中率
				//if (gatherhash.containsKey("cache"))
					extension7hst = xml7Parse.getExtensionConfAndPerf(ip, listServer, nodeName, serverName, version);
				System.out.println("###### Was 7 extension7hst.size() : " + extension7hst.size());

			}
			was7hst.put("jvm7hst", jvm7hst);
			was7hst.put("jdbc7hst", jdbc7hst);
			was7hst.put("thread7hst", thread7hst);
			was7hst.put("servlet7hst", servlet7hst);
			was7hst.put("system7hst", system7hst);
			was7hst.put("trans7hst", trans7hst);
			was7hst.put("extension7hst", extension7hst);
		}
		return was7hst;
	}
   private Hashtable getWebsphere5XML(String ip,Document doc,String version){
	    Hashtable was5hst = new Hashtable();
	    Hashtable cachehst = null;
	    Hashtable systemDatahst = null;
	    Hashtable servlethst = null;
	    Hashtable threadhst = null;
	    Hashtable transhst = null;
	    Hashtable jvmhst = null;
	    Hashtable jdbchst = null;
	    
	    List<Node> list = doc.selectNodes("//Node");

		if (list == null || list.size() == 0)
		return null;
		for (Node nodeTmp : list) {
			String nodeName = nodeTmp.valueOf("@name");//获取到节点的名称
			
			//1:获取node
			//2:获取server
			String xpathServer = "/PerformanceMonitor/"+"Node[@name='"+nodeName+"']/Server/following-sibling::*"+
								"|/PerformanceMonitor/"+"Node[@name='"+nodeName+"']/Server/self::*";
			List<Node> listServers = nodeTmp.getDocument().selectNodes(xpathServer);
			if (listServers == null || listServers.size() == 0)
			{
				//如果得到的server为Null,或者得到的个数为0,不要直接返回Null,还需把节点的名称返回过去，可以考虑返回null.
				return null;
			}
			for(Node listServer : listServers)
			{
				String serverName = listServer.valueOf("@name");//得到了serverName,其父节点为Node;
				System.out.println("serverName.size()==================:"+serverName);

				Websphere5XMLParse webpshere5XML = new Websphere5XMLParse();
				systemDatahst = webpshere5XML.getSystemData5ConfAndPerf(ip,listServer, nodeName, serverName,  version);
				servlethst = webpshere5XML.getServlet5ConfAndPerf(ip,listServer, nodeName, serverName,  version);
				threadhst = webpshere5XML.getThread5ConfAndPerf(ip,listServer, nodeName, serverName, version);
				cachehst = webpshere5XML.getCache5ConfAndPerf(ip,listServer, nodeName, serverName, version);
				transhst = webpshere5XML.getTranscation5ConfAndPerf(ip,listServer, nodeName, serverName, version);
				jvmhst = webpshere5XML.getJVM5ConfAndPerf(ip,listServer, nodeName, serverName, version);
				jdbchst = webpshere5XML.getJDBC5ConfAndPerf(ip,listServer, nodeName, serverName, version);		
				
				}
			
			was5hst.put("cachehst", cachehst);
			was5hst.put("systemDatahst", systemDatahst);
			was5hst.put("servlethst", servlethst);
			was5hst.put("threadhst", threadhst);
			was5hst.put("transhst", transhst);
			was5hst.put("jvmhst", jvmhst);
			was5hst.put("jdbchst", jdbchst);
			}
		
		return was5hst;
		
	}
   
   private Hashtable getWebsphere5XML(String ip,Document doc,String version,Hashtable gatherhash){
	    Hashtable was5hst = new Hashtable();
	    Hashtable cachehst = null;
	    Hashtable systemDatahst = null;
	    Hashtable servlethst = null;
	    Hashtable threadhst = null;
	    Hashtable transhst = null;
	    Hashtable jvmhst = null;
	    Hashtable jdbchst = null;
	    
	    List<Node> list = doc.selectNodes("//Node");

		if (list == null || list.size() == 0)
		return null;
		for (Node nodeTmp : list) {
			String nodeName = nodeTmp.valueOf("@name");//获取到节点的名称
			
			//1:获取node
			//2:获取server
			String xpathServer = "/PerformanceMonitor/"+"Node[@name='"+nodeName+"']/Server/following-sibling::*"+
								"|/PerformanceMonitor/"+"Node[@name='"+nodeName+"']/Server/self::*";
			List<Node> listServers = nodeTmp.getDocument().selectNodes(xpathServer);
			if (listServers == null || listServers.size() == 0)
			{
				//如果得到的server为Null,或者得到的个数为0,不要直接返回Null,还需把节点的名称返回过去，可以考虑返回null.
				return null;
			}
			for(Node listServer : listServers)
			{
				String serverName = listServer.valueOf("@name");//得到了serverName,其父节点为Node;
				Websphere5XMLParse webpshere5XML = new Websphere5XMLParse();
				
				systemDatahst = webpshere5XML.getSystemData5ConfAndPerf(ip,listServer, nodeName, serverName,  version);//system
				jdbchst = webpshere5XML.getJDBC5ConfAndPerf(ip,listServer, nodeName, serverName, version);//jdbc
				servlethst = webpshere5XML.getServlet5ConfAndPerf(ip,listServer, nodeName, serverName,  version);//session
				jvmhst = webpshere5XML.getJVM5ConfAndPerf(ip,listServer, nodeName, serverName, version);//jvm
				cachehst = webpshere5XML.getCache5ConfAndPerf(ip,listServer, nodeName, serverName, version);//cache
				threadhst = webpshere5XML.getThread5ConfAndPerf(ip,listServer, nodeName, serverName, version);//thread
				transhst = webpshere5XML.getTranscation5ConfAndPerf(ip,listServer, nodeName, serverName, version);//orb
				/*
				if(gatherhash.containsKey("system")){
					systemDatahst = webpshere5XML.getSystemData5ConfAndPerf(ip,listServer, nodeName, serverName,  version);
				}
				if(gatherhash.containsKey("jdbc")){
					jdbchst = webpshere5XML.getJDBC5ConfAndPerf(ip,listServer, nodeName, serverName, version);
				}
				if(gatherhash.containsKey("session")){
					servlethst = webpshere5XML.getServlet5ConfAndPerf(ip,listServer, nodeName, serverName,  version);
				}
				if(gatherhash.containsKey("jvm")){
					jvmhst = webpshere5XML.getJVM5ConfAndPerf(ip,listServer, nodeName, serverName, version);
				}
				if(gatherhash.containsKey("cache")){
					cachehst = webpshere5XML.getCache5ConfAndPerf(ip,listServer, nodeName, serverName, version);
				}
				if(gatherhash.containsKey("thread")){
					threadhst = webpshere5XML.getThread5ConfAndPerf(ip,listServer, nodeName, serverName, version);
				}
				if(gatherhash.containsKey("orb")){
					transhst = webpshere5XML.getTranscation5ConfAndPerf(ip,listServer, nodeName, serverName, version);
				}
				*/
			}
			
			was5hst.put("cachehst", cachehst);
			was5hst.put("systemDatahst", systemDatahst);
			was5hst.put("servlethst", servlethst);
			was5hst.put("threadhst", threadhst);
			was5hst.put("transhst", transhst);
			was5hst.put("jvmhst", jvmhst);
			was5hst.put("jdbchst", jdbchst);
			}
		return was5hst;
		
	}
   
   
   
   private Hashtable getWebsphere7XML(String ip,Document doc,String version)
	{
	   Hashtable<String, Hashtable<?, ?>> was7hst = new Hashtable();
	   Hashtable jvm7hst = new Hashtable();
	   Hashtable jdbc7hst = new Hashtable();
	   Hashtable thread7hst = new Hashtable();
	   Hashtable servlet7hst = new Hashtable();
	   Hashtable system7hst = new Hashtable();
	   Hashtable trans7hst = new Hashtable();
	   Hashtable extension7hst = new Hashtable();
	   
		
		List<Node> listNodes = doc.selectNodes("//Node");
		
		if (listNodes == null || listNodes.size() == 0)
		{
			return null;
		}
		for (Node nodeTmp : listNodes) {
			String nodeName = nodeTmp.valueOf("@name");//获取到节点的名称
			//1:获取node
			
			//2:获取server
			String xpathServer = "/PerformanceMonitor/"+"Node[@name='"+nodeName+"']/Server/following-sibling::*"+
								"|/PerformanceMonitor/"+"Node[@name='"+nodeName+"']/Server/self::*";
			
			List<Node> listServers = nodeTmp.getDocument().selectNodes(xpathServer);
			if (listServers == null || listServers.size() == 0)
			{
				//如果得到的server为Null,或者得到的个数为0,不要直接返回Null,还需把节点的名称返回过去，可以考虑返回null.
				return  null;
			}
			for(Node listServer : listServers)
			{
				String serverName = listServer.valueOf("@name");//得到了serverName,其父节点为Node;
				
				System.out.println("serverName----:"+serverName);
				
				//还有好几个监控指标的类型，此处还要添加几个，go on......
				
				//3:获取Cache
				
				Websphere6XMLParse xml6Parse = new Websphere6XMLParse();
//				xml6Parse.getCacheConfAndPerf(listServer,nodeName,serverName,version,
//						cacheList,cachePerfList,cacheInstanceList,cacheInstancePerfList);
//				//4:得到JVM，及其指标
				jvm7hst = xml6Parse.getJVMConfAndPerf(ip,listServer,nodeName,serverName,version);
//				5:得到webapplication应用，及其指标，取法和cache相似		
//				xml6Parse.getWebapplicationConfAndPerf(listServer,nodeName,serverName,version,
//							webapplicationTotalList,webapplicationInstancelList,webapplicationTotalPerfList,
//							webapplicationInstancePerfList);
				//6：得到JDBC连接池，取法和Cache相似
				jdbc7hst = xml6Parse.getJDBCConfAndPerf(ip, listServer, nodeName, serverName, version);
				//7:得到Thread连接池，取法和cache相似
				thread7hst = xml6Parse.getThreadConfAndPerf(ip, listServer, nodeName, serverName, version);
				//8:得到Servlet Session，取法cache相似
				servlet7hst = xml6Parse.getServletConfAndPerf(ip, listServer, nodeName, serverName, version);
				//9:得到System数据，主要是CPU数据，和JVM取法一样
				system7hst = xml6Parse.getSystemDataConfAndPerf(ip, listServer, nodeName, serverName, version);
				//10:得到transcation事务，和JVM取法一下，不需要再去判断下一级
				trans7hst = xml6Parse.getTranscationConfAndPerf(ip, listServer, nodeName, serverName, version);
				//11:得到缓存的相关指标，如：所有命中数，所有请求数，命中率
				extension7hst = xml6Parse.getExtensionConfAndPerf(ip, listServer, nodeName, serverName, version);
				
			}
			   was7hst.put("jvm7hst", jvm7hst);
			   was7hst.put("jdbc7hst", jdbc7hst);
			   was7hst.put("thread7hst", thread7hst);
			   was7hst.put("servlet7hst", servlet7hst);
			   was7hst.put("system7hst", system7hst);
			   was7hst.put("trans7hst", trans7hst);
			   was7hst.put("extension7hst", extension7hst);
		}
		return was7hst;
	}
   
   private Hashtable getWebsphere6XML(String ip,Document doc,String version,Hashtable gatherhash)
	{
	   Hashtable<String, Hashtable<?, ?>> was7hst = new Hashtable();
	   Hashtable jvm7hst = new Hashtable();
	   Hashtable jdbc7hst = new Hashtable();
	   Hashtable thread7hst = new Hashtable();
	   Hashtable servlet7hst = new Hashtable();
	   Hashtable system7hst = new Hashtable();
	   Hashtable trans7hst = new Hashtable();
	   Hashtable extension7hst = new Hashtable();
		
		
		List<Node> listNodes = doc.selectNodes("//Node");
		
		if (listNodes == null || listNodes.size() == 0)
		{
			return null;
		}
		for (Node nodeTmp : listNodes) {
			String nodeName = nodeTmp.valueOf("@name");//获取到节点的名称
			//1:获取node
			
			//2:获取server
			String xpathServer = "/PerformanceMonitor/"+"Node[@name='"+nodeName+"']/Server/following-sibling::*"+
								"|/PerformanceMonitor/"+"Node[@name='"+nodeName+"']/Server/self::*";
			
			List<Node> listServers = nodeTmp.getDocument().selectNodes(xpathServer);
			if (listServers == null || listServers.size() == 0)
			{
				//如果得到的server为Null,或者得到的个数为0,不要直接返回Null,还需把节点的名称返回过去，可以考虑返回null.
				return  null;
			}
			for(Node listServer : listServers)
			{
				String serverName = listServer.valueOf("@name");//得到了serverName,其父节点为Node;
				
				System.out.println("was7 serverName----:"+serverName);
				
				//还有好几个监控指标的类型，此处还要添加几个，go on......
				
				//3:获取Cache
				
				Websphere6XMLParse xml6Parse = new Websphere6XMLParse();
//				xml6Parse.getCacheConfAndPerf(listServer,nodeName,serverName,version,
//						cacheList,cachePerfList,cacheInstanceList,cacheInstancePerfList);
//				//4:得到JVM，及其指标
				//if(gatherhash.containsKey("jvm"))
					jvm7hst = xml6Parse.getJVMConfAndPerf(ip,listServer,nodeName,serverName,version);
//				5:得到webapplication应用，及其指标，取法和cache相似		
//				xml6Parse.getWebapplicationConfAndPerf(listServer,nodeName,serverName,version,
//							webapplicationTotalList,webapplicationInstancelList,webapplicationTotalPerfList,
//							webapplicationInstancePerfList);
				//6：得到JDBC连接池，取法和Cache相似
				//if(gatherhash.containsKey("jdbc"))
					jdbc7hst = xml6Parse.getJDBCConfAndPerf(ip, listServer, nodeName, serverName, version);
				//7:得到Thread连接池，取法和cache相似
				//if(gatherhash.containsKey("thread"))
					thread7hst = xml6Parse.getThreadConfAndPerf(ip, listServer, nodeName, serverName, version);
				//8:得到Servlet Session，取法cache相似
				//if(gatherhash.containsKey("session"))
					servlet7hst = xml6Parse.getServletConfAndPerf(ip, listServer, nodeName, serverName, version);
				//9:得到System数据，主要是CPU数据，和JVM取法一样
				//if(gatherhash.containsKey("system"))
					system7hst = xml6Parse.getSystemDataConfAndPerf(ip, listServer, nodeName, serverName, version);
				//10:得到transcation事务，和JVM取法一下，不需要再去判断下一级
				//if(gatherhash.containsKey("orb"))
					trans7hst = xml6Parse.getTranscationConfAndPerf(ip, listServer, nodeName, serverName, version);
				//11:得到缓存的相关指标，如：所有命中数，所有请求数，命中率
				//if(gatherhash.containsKey("cache"))
				
					extension7hst = xml6Parse.getExtensionConfAndPerf(ip, listServer, nodeName, serverName, version);
				
			}
			   was7hst.put("jvm7hst", jvm7hst);
			   was7hst.put("jdbc7hst", jdbc7hst);
			   was7hst.put("thread7hst", thread7hst);
			   was7hst.put("servlet7hst", servlet7hst);
			   was7hst.put("system7hst", system7hst);
			   was7hst.put("trans7hst", trans7hst);
			   was7hst.put("extension7hst", extension7hst);
		}
		return was7hst;
	}
   
   public boolean connectWasIsOK(String ip,int port){
	   
	   String urlstr = "http://"+ip+":"+port+"/wasPerfTool/servlet/perfservlet?refreshConfig=true";		
		try
		{
			InputStream input = null;
			URL url = new URL(urlstr);
			HttpURLConnection urlCon = (HttpURLConnection)url.openConnection(); 
			urlCon.setConnectTimeout(30000);//设置超时			
			// 打开到此 URL 的连接并返回一个用于从该连接读入的 InputStream。
			input = urlCon.getInputStream();
	       return true;	  
		} catch (Exception e) {	
			e.printStackTrace();
			return false;
		}
		
	}
   
   
   public static void main(String args[]){
//	   UrlConncetWas ucw = new UrlConncetWas();
//	  // String xmlview = ucw.ConncetWas("10.10.151.166","9080","","","");
//	   ucw.ConncetWas("10.10.151.166","9080","","","V5");
	  
//		   WasConfigDao wasconf = new WasConfigDao();
//		   Was5system wassys = new Was5system();
//		   wassys = (Was5system) wasconf.findWasInfo("wassystem10101571");
//		   System.out.println("===============getFreeMemory=======================:"+wassys.getFreeMemory());
	
	
   }
}

