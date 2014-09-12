package com.afunms.application.resinmonitor;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.dao.ResinDao;
import com.afunms.application.model.Resin;
import com.afunms.application.resinmonitor.ConnectionPool;
import com.afunms.application.resinmonitor.Connector;
import com.afunms.application.resinmonitor.LoadBalance;
import com.afunms.application.resinmonitor.OpenConnection;
import com.afunms.application.resinmonitor.TCPPort;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.om.Pingcollectdata;

public class ResinServerStream {
	public int resinVersion;
	HashMap map;
	String nexttime = "";

	String time = "";
	public Hashtable resindata_ht;

	Hashtable resin_server;

	Hashtable resin_threadpool;
	Hashtable resin_cluster;
	/* Ver3.0 */
	List<TCPPort> tcpportList;

	List<Connector> serverConnectorsList;

	List<ConnectionPool> connectionPoolsList;

	/* Ver4.0 */
	List<OpenConnection> openConnectionsList;

	List<LoadBalance> loadBalanceList;

	List<ConnectionPool> databasePoolList;

	public void foundResinData(Resin node, String host, String port, String user, String pass, String version) {
		ResinServerConnector sc = new ResinServerConnector();
		sc.setWebServerHost(host);
		sc.setWebServerPort(Integer.parseInt(port));
		sc.setUser(user);
		sc.setPass(pass);
		if ("3".equals(version)) {
			sc.setStatusPath("/resin-admin/j_security_check?j_uri=status.php");
		} else if ("4".equals(version)) {
			sc.setStatusPath("/resin-admin/j_security_check?j_uri=index.php");
			// sc.setStatusPath("/resin-admin/index.php?q=thread&s=0");
		}
		try {
			// important
			sc.resinStart();
			map = sc.getMStream();
			StringBuffer content = new StringBuffer();
			if(map!=null){
			for (int i = 0; i < map.size(); i++) {
				content.append(map.get(String.valueOf(i)) + "\n");

			}
			}
			Pattern p = null;
			Matcher m = null;

			if ("3".equals(version)) {
				p = Pattern.compile("<h2>.*</h2>");
				m = p.matcher(content.toString());

				List<Integer> posList = new ArrayList<Integer>();
				while (m.find()) {
					posList.add(m.start());
				}
				if (posList.size() < 5) {
					return;
				}
				String[] models = new String[5];
				for (int i = 0; i < posList.size() - 1 && i < 5; i++) {
					models[i] = content.toString().substring(posList.get(i), posList.get(i + 1));
				}

				parseServer(version, models[0]);
				parseThreadpool(version, models[1]);
				parseTCPports(models[2], version);
				parseServerConnectors(models[3]);
				parseConnectionPools(models[4]);
			} else if ("4".equals(version)) {
				p = Pattern.compile("<div id='overview-tab'>\\n<table class=\"data\" summary=\"Overview\">(.*)</table>\\n</div><div id='health-tab'>", Pattern.DOTALL);
				m = p.matcher(content.toString());
				if (m.find()) {
					parseServer(version, m.group(1).trim());
				}

				p = Pattern.compile("<h2>.*</h2>");
				m = p.matcher(content.toString());

				List<Integer> posList = new ArrayList<Integer>();
				while (m.find()) {
					posList.add(m.start());
				}
				if (posList.size() != 4) {
					return;
				}
				String[] models = new String[4];
				for (int i = 0; i < posList.size() - 1; i++) {
					models[i] = content.toString().substring(posList.get(i), posList.get(i + 1));

				}
				models[3] = content.toString().substring(posList.get(3));
				parseOpenConnection(models[0]);
				parseTCPports(models[1], version);
				parseLoadBalance(models[2]);
				parseDatabasePool(models[3]);
			}
			sendResinParamToServer(node, host, port, version);

		} catch (Exception e) {
			SysLogger.error("", e);
		}
	}

	public void parseResinData(String host, String port, String user, String pass, String version) {
		ResinServerConnector sc = new ResinServerConnector();
		sc.setWebServerHost(host);
		sc.setWebServerPort(Integer.parseInt(port));
		sc.setUser(user);
		sc.setPass(pass);
		if ("3".equals(version)) {
			sc.setStatusPath("/resin-admin/j_security_check?j_uri=cluster.php");
		} else if ("4".equals(version)) {
			sc.setStatusPath("/resin-admin/index.php?q=thread");
		}
		try {
			sc.resinStart();
			map = sc.getMStream();
			StringBuffer content = new StringBuffer();
			for (int i = 0; i < map.size(); i++) {
				content.append(map.get(String.valueOf(i)) + "\n");

			}
			System.out.println("====" + content.toString());
			Pattern p = null;
			Matcher m = null;

			if ("3".equals(version)) {
				p = Pattern.compile("<h2>.*</h2>");
				m = p.matcher(content.toString());

				List<Integer> posList = new ArrayList<Integer>();
				while (m.find()) {
					posList.add(m.start());
				}
				// if (posList.size() < 5) {
				// return;
				// }
				String[] models = new String[5];
				for (int i = 0; i < posList.size() - 1 && i < 5; i++) {
					models[i] = content.toString().substring(posList.get(i), posList.get(i + 1));
				}
				System.out.println("====" + models[0]);
				parseServer(version, models[0]);

			} else if ("4".equals(version)) {

				p = Pattern.compile("<tr align='right'>*</tr>");
				m = p.matcher(content.toString());

				List<Integer> posList = new ArrayList<Integer>();
				while (m.find()) {
					posList.add(m.start());
				}

				String[] models = new String[1];
				for (int i = 0; i < posList.size() - 1; i++) {
					models[i] = content.toString().substring(posList.get(i), posList.get(i + 1));

				}
				System.out.println("::::" + content);
				parseThreadpool(version, models[0]);

			}

		} catch (Exception e) {
			SysLogger.error("", e);
		}
	}

	public String getCurrTime() {
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		time = format.format(date);
		return time;
	}

	public String getNextTime() {
		Date date = new Date();
		int curmin = date.getMinutes() + 5;
		date.setMinutes(curmin);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		nexttime = format.format(date);
		return nexttime;
	}

	private void parseDatabasePool(String databasePoolString) {
		Pattern p = Pattern.compile("<tr class='.+'>\\n" + "\\s+<td headers=.+>(.+)</td>\\n" + "\\s+<td headers=.+>(.+)</td>\\n" + "\\s+<td headers=.+>(.+)</td>\\n" + "\\s+<td headers=.+>(.+)</td>\\n" + "\\s+<td headers=.+>(.+)</td>\\n" + "\\s+<td headers=.+>\\n(.+)</td>\\n"
				+ "\\s+<td headers=.+>(.+)</td>\\n" + "\\s+<td headers=.+>(.+)</td>\\n" + "\\s*</tr>");
		Matcher m = p.matcher(databasePoolString);
		databasePoolList = new ArrayList<ConnectionPool>();
		while (m.find()) {
			ConnectionPool dbPool = new ConnectionPool();
			dbPool.setName(m.group(1));
			dbPool.setActive(m.group(2));
			dbPool.setIdle(m.group(3));
			dbPool.setCreated(m.group(4));
			dbPool.setFailed(m.group(5));
			dbPool.setMax_connections(m.group(7));
			dbPool.setIdle_time(m.group(8));
			databasePoolList.add(dbPool);
		}
	}

	private void parseLoadBalance(String loadBalanceString) {
		Pattern p = Pattern.compile("<tr class='.+'>\\n" + "\\s+<td class=.+>(.+)</td>\\n" + "\\s+<td headers=.+>(.+)</td>\\n" + "\\s+<td headers=.+>(.+)</td>\\n" + "\\s+<td headers=.+>(.+)</td>\\n" + "\\s+<td headers=.+>(.+)</td>\\n" + "\\s+<td headers=.+>(.+)</td>\\n"
				+ "\\s+<td headers=.+>(.+)</td>\\n" + "\\s+<td headers=.+>(.+)</td>\\n" + "\\s+<td headers=.+>(.+)</td>\\n" + "\\s*<td headers=.+>(.*)</td>\\n" + "\\s*<td headers=.+>(.*)</td>\\n" + "\\s*<td headers=.+>(.*)</td>\\n" + "\\s*</tr>");
		Matcher m = p.matcher(loadBalanceString);
		loadBalanceList = new ArrayList<LoadBalance>();
		while (m.find()) {
			LoadBalance loadBalance = new LoadBalance();
			loadBalance.setServer(m.group(1));
			loadBalance.setAddress(m.group(2));
			loadBalance.setStatus(m.group(3));
			loadBalance.setActive(m.group(4));
			loadBalance.setIdle(m.group(5));
			loadBalance.setMiss(m.group(6));
			loadBalance.setLoad(m.group(7));
			loadBalance.setLatency(m.group(8));
			loadBalance.setFailure(m.group(9));
			loadBalance.setBusy(m.group(11));
			loadBalanceList.add(loadBalance);
		}
	}

	private void parseOpenConnection(String openConnectionString) {
		Pattern p = Pattern.compile("<tr class='.+'>\\n" + "\\s*<td>\\n(.+)</td>\\n" + "\\s*<td>(.+)</td>\\n" + "\\s*<td>(.+)</td>\\n" + "\\s*<td>(.+)</td>\\n" + "\\s*<td>(.+)</td>\\n" + "\\s*</tr>");
		Matcher m = p.matcher(openConnectionString);
		openConnectionsList = new ArrayList<OpenConnection>();
		while (m.find()) {
			OpenConnection connection = new OpenConnection();
			String[] temp = m.group(1).split("</a>\\s+<a");
			String action = null;
			if (null != temp[0]) {
				action = temp[0].substring(temp[0].lastIndexOf(">") + 1);

			}
			connection.setAction(action.toString());
			connection.setTime(m.group(2).trim());
			connection.setId(m.group(3).trim());
			connection.setUrl(m.group(4).trim());
			connection.setIp(m.group(5).trim());
			openConnectionsList.add(connection);
			System.out.println("");
		}
	}

	private void sendResinParamToServer(Resin node, String host, String port, String version) {
		if ("3".equals(version)) {
			resindata_ht.put("resin_threadpool", resin_threadpool);
			resindata_ht.put("resin_tcpports", tcpportList);
			resindata_ht.put("resin_serverconnectors", serverConnectorsList);
			resindata_ht.put("resin_connectionpools", connectionPoolsList);
		} else {
			resindata_ht.put("resin_openconnections", openConnectionsList);
			resindata_ht.put("resin_tcpports", tcpportList);
			resindata_ht.put("resin_loadbalanceandclusters", loadBalanceList);
			resindata_ht.put("resin_databasepools", databasePoolList);
		}
		resindata_ht.put("version", version);
		resindata_ht.put("resin_server", resin_server);
		resindata_ht.put("ip", host);
		resindata_ht.put("port", port);
		resindata_ht.put("mon_time", time);
		resindata_ht.put("nexttime", nexttime);
		this.getResinMemoryUtil(node, host);
		ShareData.setResindata(host, resindata_ht);
	}

	private void getResinMemoryUtil(Resin node, String ip) {
		if (resin_server != null) {
			String totalMem = "0";
			String freeMem = "0";
			double memUtil = 0d;
			NodeUtil nodeUtil = new NodeUtil();
			NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(node);
			// 判断是否存在此告警指标
			AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(nodeDTO.getId() + "", nodeDTO.getType(), nodeDTO.getSubtype());
			CheckEventUtil checkEventUtil = new CheckEventUtil();
			if (resin_server.containsKey("Total memory:") && resin_server.containsKey("Free memory:")) {// V3
				totalMem = (String) resin_server.get("Total memory:");
				freeMem = (String) resin_server.get("Free memory:");
				totalMem = totalMem.replaceAll("Meg", "");
				freeMem = freeMem.replaceAll("Meg", "");
				if (totalMem != null && !totalMem.equals("0")) {
					memUtil = (Double.parseDouble(totalMem) - Double.parseDouble(freeMem)) / Float.parseFloat(totalMem);
					BigDecimal big = new BigDecimal(memUtil * 100);
					big = big.setScale(2, 4);
					memUtil = big.doubleValue();
				}
				resindata_ht.put("memUtil", memUtil);
				Pingcollectdata hostdata = null;
				hostdata = new Pingcollectdata();
				hostdata.setIpaddress(ip);
				Calendar date = Calendar.getInstance();
				hostdata.setCollecttime(date);
				hostdata.setCategory("resin_mem");
				hostdata.setEntity("Utilization");
				hostdata.setSubentity("mem_utilization");
				hostdata.setRestype("dynamic");
				hostdata.setUnit("%");
				hostdata.setThevalue(memUtil + "");
				ResinDao resindao = new ResinDao();
				try {
					resindao.createHostData(hostdata);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					resindao.close();
				}
				if(list!=null){
					for (int i = 0; i < list.size(); i++) {
						AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode) list.get(i);
						if ("memory".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
							checkEventUtil.checkEvent(node, alarmIndicatorsNode, memUtil+"",null);
						}
					}
					}
			}
			if (resin_server.containsKey("Total heap:") && resin_server.containsKey("Free heap:")) {// V4
				totalMem = (String) resin_server.get("Total heap:");
				freeMem = (String) resin_server.get("Free heap:");
				totalMem = totalMem.replaceAll("Meg", "");
				freeMem = freeMem.replaceAll("Meg", "");
				if (totalMem != null && !totalMem.equals("0")) {
					memUtil = (Double.parseDouble(totalMem) - Double.parseDouble(freeMem)) / Float.parseFloat(totalMem);
					BigDecimal big = new BigDecimal(memUtil * 100);
					big = big.setScale(2, 4);
					memUtil = big.doubleValue();
				}
				resindata_ht.put("heapUtil", memUtil);
				Pingcollectdata hostdata = null;
				hostdata = new Pingcollectdata();
				hostdata.setIpaddress(ip);
				Calendar date = Calendar.getInstance();
				hostdata.setCollecttime(date);
				hostdata.setCategory("resin_heap");
				hostdata.setEntity("Utilization");
				hostdata.setSubentity("heap_utilization");
				hostdata.setRestype("dynamic");
				hostdata.setUnit("%");
				hostdata.setThevalue(memUtil + "");
				ResinDao resindao = new ResinDao();
				try {
					resindao.createHostData(hostdata);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					resindao.close();
				}
				if(list!=null){
					for (int i = 0; i < list.size(); i++) {
						AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode) list.get(i);
						if ("heapmemory".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
							checkEventUtil.checkEvent(node, alarmIndicatorsNode, memUtil+"",null);
						}
					}
					}
			}
			if (resin_server.containsKey("Total Swap:") && resin_server.containsKey("Free Swap:")) {// V4
				totalMem = (String) resin_server.get("Total Swap:");
				freeMem = (String) resin_server.get("Free Swap:");
				totalMem = totalMem.replaceAll("Meg", "");
				freeMem = freeMem.replaceAll("Meg", "");
				if (totalMem != null && !totalMem.equals("0")) {
					memUtil = (Double.parseDouble(totalMem) - Double.parseDouble(freeMem)) / Float.parseFloat(totalMem);
					BigDecimal big = new BigDecimal(memUtil * 100);
					big = big.setScale(2, 4);
					memUtil = big.doubleValue();
				}
				resindata_ht.put("swapUtil", memUtil);
				Pingcollectdata hostdata = null;
				hostdata = new Pingcollectdata();
				hostdata.setIpaddress(ip);
				Calendar date = Calendar.getInstance();
				hostdata.setCollecttime(date);
				hostdata.setCategory("resin_swap");
				hostdata.setEntity("Utilization");
				hostdata.setSubentity("swap_utilization");
				hostdata.setRestype("dynamic");
				hostdata.setUnit("%");
				hostdata.setThevalue(memUtil + "");
				ResinDao resindao = new ResinDao();
				try {
					resindao.createHostData(hostdata);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					resindao.close();
				}
				if(list!=null){
					for (int i = 0; i < list.size(); i++) {
						AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode) list.get(i);
						if ("swapmemory".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
							checkEventUtil.checkEvent(node, alarmIndicatorsNode, memUtil+"",null);
						}
					}
					}
			}
			if (resin_server.containsKey("Total Physical:") && resin_server.containsKey("Free Physical:")) {// V4
				totalMem = (String) resin_server.get("Total Physical:");
				freeMem = (String) resin_server.get("Free Physical:");
				totalMem = totalMem.replaceAll("Meg", "");
				freeMem = freeMem.replaceAll("Meg", "");
				if (totalMem != null && !totalMem.equals("0")) {
					memUtil = (Double.parseDouble(totalMem) - Double.parseDouble(freeMem)) / Float.parseFloat(totalMem);
					BigDecimal big = new BigDecimal(memUtil * 100);
					big = big.setScale(2, 4);
					memUtil = big.doubleValue();
				}
				resindata_ht.put("phyUtil", memUtil);
				Pingcollectdata hostdata = null;
				hostdata = new Pingcollectdata();
				hostdata.setIpaddress(ip);
				Calendar date = Calendar.getInstance();
				hostdata.setCollecttime(date);
				hostdata.setCategory("resin_physical");
				hostdata.setEntity("Utilization");
				hostdata.setSubentity("physical_utilization");
				hostdata.setRestype("dynamic");
				hostdata.setUnit("%");
				hostdata.setThevalue(memUtil + "");
				ResinDao resindao = new ResinDao();
				try {
					resindao.createHostData(hostdata);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					resindao.close();
				}
			}

			if (list != null) {
				for (int i = 0; i < list.size(); i++) {
					AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode) list.get(i);
					if ("physicalmemory".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
						checkEventUtil.checkEvent(node, alarmIndicatorsNode, memUtil + "", null);
					}
				}
			}

		}

	}

	private void parseConnectionPools(String connectionPoolString) {
		Pattern p = Pattern.compile("<tr class='.+'>\\n" + "\\s+<td>(.+)</td>\\n" + "\\s+<td>(.+)</td>\\n" + "\\s+<td>(.+)</td>\\n" + "\\s+<td>(.+)</td>\\n" + "\\s+<td>(.+)</td>\\n" + "\\s+<td class=.*>(.*\\n\\s*)</td>\\n" + "\\s+<td>(.+)</td>\\n" + "\\s+<td>(.+)</td>\\n"
				+ "\\s*</tr>\\n");
		Matcher m = p.matcher(connectionPoolString);
		connectionPoolsList = new ArrayList<ConnectionPool>();
		while (m.find()) {
			ConnectionPool connPool = new ConnectionPool();
			connPool.setName(m.group(1));
			connPool.setActive(m.group(2));
			connPool.setIdle(m.group(3));
			connPool.setCreated(m.group(4));
			connPool.setFailed(m.group(5));
			connPool.setMax_connections(m.group(7));
			connPool.setIdle_time(m.group(8));
			connectionPoolsList.add(connPool);
		}
	}

	private void parseServerConnectors(String serverConnectorString) {
		serverConnectorsList = new ArrayList<Connector>();
		Pattern p = Pattern.compile("<tr class='.+'>");
		Matcher m = p.matcher(serverConnectorString);
		while (m.find()) {
		}
	}

	private void parseTCPports(String TCPportsString, String version) {
		String patternStr = "<tr class='.+'>\\n" + "\\s+<td class=.+>(.+)</td>\\n" + "\\s+<td class=.+>(.+)</td>\\n" + "\\s+<td>(-?\\d+)</td>\\n" + "\\s+<td>(-?\\d+)</td>\\n" + "\\s+<td>(-?\\d+)</td>\\n" + "\\s*\\n" + "\\s+<td>(-?\\d+)</td>\\n" + "\\s+<td>(-?\\d+)</td>\\n"
				+ "\\s+<td>(-?\\d+)</td>\\n" + "\\s+<td>(-?\\d+)\\s*</tr>\\n";
		if ("4".equals(version)) {
			patternStr = "<tr class='.+'>\\n" + "\\s+<td class=.+>(.+)</td>\\n" + "\\s+<td class=.+>(.+)</td>\\n" + "\\s+<td headers=.+>(-?\\d+)</td>\\n" + "\\s+<td headers=.+>(-?\\d+)</td>\\n" + "\\s+<td headers=.+>(-?\\d+)</td>\\n" + "\\s*\\n"
					+ "\\s+<td headers=.+>(-?\\d+)</td>\\n" + "\\s+<td headers=.+>(-?\\d+)</td>\\n" + "\\s+<td headers=.+>(-?\\d+)</td>\\n" + "\\s+<td headers=.+>(-?\\d+)</td>\\n\\s*</tr>\\n";
		}
		Pattern p = Pattern.compile(patternStr);
		Matcher m = p.matcher(TCPportsString);
		tcpportList = new ArrayList<TCPPort>();
		while (m.find()) {
			TCPPort port = new TCPPort();
			port.setListener(m.group(1));
			port.setStatus(m.group(2));
			port.setThreadActive(m.group(3));
			port.setThreadIdle(m.group(4));
			port.setThreadTotal(m.group(5));
			port.setKeepaliveTotal(m.group(6));
			port.setKeepaliveThread(m.group(7));
			port.setKeepaliveSelect(m.group(8));
			port.setKeepaliveComet(m.group(9));
			tcpportList.add(port);
		}
	}

	private void parseThreadpool(String version, String threadpoolString) {
		resin_threadpool = new Hashtable();
		Pattern p = null;
		Matcher m = null;
		if ("3".equals(version)) {
			p = Pattern.compile("<th title=.+>(.+)</th>");
			m = p.matcher(threadpoolString);
			List<String> keyList = new ArrayList<String>();
			while (m.find()) {
				keyList.add(m.group(1));
			}

			p = Pattern.compile("<td>(\\d+)</td>");
			m = p.matcher(threadpoolString);
			List<String> valueList = new ArrayList<String>();
			while (m.find()) {
				valueList.add(m.group(1));
			}
			if (keyList.size() != valueList.size()) {
				return;
			}
			for (int i = 0; i < keyList.size(); i++) {
				resin_threadpool.put(keyList.get(i), valueList.get(i));
			}
		} else if ("4".equals(version)) {
			String patternStr = "<tr class='.+'>\\n" + "\\s+<td class=.+>(.+)</td>\\n" + "\\s+<td class=.+>(.+)</td>\\n" + "\\s+<td headers=.+>(-?\\d+)</td>\\n" + "\\s+<td headers=.+>(-?\\d+)</td>\\n" + "\\s+<td headers=.+>(-?\\d+)</td>\\n" + "\\s*\\n"
					+ "\\s+<td headers=.+>(-?\\d+)</td>\\n" + "\\s+<td headers=.+>(-?\\d+)</td>\\n" + "\\s+<td headers=.+>(-?\\d+)</td>\\n" + "\\s+<td headers=.+>(-?\\d+)</td>\\n\\s*</tr>\\n";

			p = Pattern.compile(patternStr);
			m = p.matcher(threadpoolString);
			while (m.find()) {
				ThreadPool pool = new ThreadPool();
				pool.setActive(Integer.parseInt(m.group(1)));
				pool.setIdle(Integer.parseInt(m.group(2)));
				pool.setTotal(Integer.parseInt(m.group(3)));
				pool.setThread_max(Integer.parseInt(m.group(4)));
				pool.setThread_idle_min(Integer.parseInt(m.group(5)));
				resin_threadpool.put("pool", pool);
			}
		}
	}

	private void parseServer(String version, String serverString) {
		resin_server = new Hashtable();
		Pattern p = null;
		Matcher m = null;
		if ("3".equals(version)) {
			p = Pattern.compile("<tr.*>\n\\s+<th>(.+)</th>\n\\s+<td.*>(.+)</td>\n\\s+</tr>");
			m = p.matcher(serverString);
			while (m.find()) {
				String[] temp = m.group(1).split("<.*>");
				StringBuffer str = new StringBuffer();
				for (int i = 0; i < temp.length; i++) {
					str.append(temp[i].trim());
				}

				resin_server.put(str.toString(), m.group(2));
			}
		} else if ("4".equals(version)) {
			p = Pattern.compile("<th id=.+ class=.+\\n\\s+ title=.+>(.+)</th>\\n\\s+<td headers=.+>(.*)</td>");
			// p = Pattern.compile("<th id=.+
			// class=.+\\s+title=.+>(.+)</th>\\s+<td headers=.+>(.*)</td>");
			m = p.matcher(serverString);
			// serverString = getContext(serverString);
			while (m.find()) {
				resin_server.put(m.group(1).trim(), m.group(2).trim());
			}
			p = Pattern.compile("<th id=.+ class=.+\\n\\s+ title=.+>(.+)</th>\\n\\s+<td headers=.+>\\n(.*)</td>");
			m = p.matcher(serverString);
			while (m.find()) {
				resin_server.put(m.group(1).trim(), m.group(2).trim());
			}

			p = Pattern.compile("<th id=.+ class=.+\\n\\s+ title=.+>(.+)</th>\\n\\s+<td headers=.+>\\n(.*\\n.*)</td>");
			m = p.matcher(serverString);
			while (m.find()) {
				resin_server.put(m.group(1).trim(), m.group(2).trim());
			}
		}
	}

	private void parseCluster(String version, String serverString) {
		resin_cluster = new Hashtable();
		Pattern p = null;
		Matcher m = null;
		if ("3".equals(version)) {
			p = Pattern.compile("<tr.*>\n\\s+<th>(.+)</th>\n\\s+<td.*>(.+)</td>\n\\s+</tr>");
			m = p.matcher(serverString);
			while (m.find()) {
				String[] temp = m.group(1).split("<.*>");
				StringBuffer str = new StringBuffer();
				for (int i = 0; i < temp.length; i++) {
					str.append(temp[i].trim());
				}

				resin_cluster.put(str.toString(), m.group(2));
			}
		}
	}

	public String getContext(String str) {
		String returnStr = "";
		StringBuffer tmpBuf = new StringBuffer(str.trim());
		int pos;
		while (tmpBuf.toString().startsWith("<")) {
			pos = tmpBuf.indexOf(">");
			tmpBuf.delete(0, pos + 1);
		}
		while (tmpBuf.toString().endsWith(">")) {
			pos = tmpBuf.indexOf("<");
			tmpBuf.delete(pos, tmpBuf.length() + 1);
		}
		returnStr = tmpBuf.toString();
		return returnStr;
	}

	public String validResinServer(Hashtable serverlist) {
		StringBuffer pos = new StringBuffer();
		int listsize = serverlist.size();
		for (int list_i = 0; list_i < listsize; list_i++) {
			resindata_ht = new Hashtable();
			resin_server = new Hashtable();
			resin_threadpool = new Hashtable();
			tcpportList = new ArrayList<TCPPort>();
			serverConnectorsList = new ArrayList<Connector>();
			connectionPoolsList = new ArrayList<ConnectionPool>();
			openConnectionsList = new ArrayList<OpenConnection>();
			loadBalanceList = new ArrayList<LoadBalance>();
			databasePoolList = new ArrayList<ConnectionPool>();
			String tmps = serverlist.get(String.valueOf(list_i)).toString();
			String[] serverinfo = tmps.split(",");
			try {
				if (!isResinValid(serverinfo[0], serverinfo[1], serverinfo[2], serverinfo[3], serverinfo[4])) {

					serverlist.remove(String.valueOf(list_i));
				} else {
					pos.append(String.valueOf(list_i));
					pos.append(",");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		pos.append("huilet");
		if (serverlist.size() == 0) {
			return "";
		}
		return pos.toString();
	}

	public boolean isResinValid(String ip, String port, String user, String pass, String version) {
		String url = null;
		try {
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = null;

			if (null != version && "3".equals(version.trim())) {
				url = "http://" + ip + ":" + port + "/resin-admin/j_security_check?j_uri=status.php";
			} else {
				url = "http://" + ip + ":" + port + "/resin-admin/j_security_check?j_uri=index.php";
			}
			httppost = new HttpPost(url);

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("j_username", user));
			nameValuePairs.add(new BasicNameValuePair("j_password", pass));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse response = httpclient.execute(httppost);
			if (response != null) {
				return true;
			}
		} catch (HttpHostConnectException e) {
			// logger.warn("resin:" + url + "访问被拒绝！");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void main(String[] args) {
		ResinServerConnector sc = new ResinServerConnector();
		sc.setWebServerHost("10.10.152.35");
		sc.setWebServerPort(Integer.parseInt("8080"));
		sc.setUser("wxy");
		sc.setPass("wxy");

		// sc.setStatusPath("/resin-admin/j_security_check?j_uri=index.php");
		sc.setStatusPath("/resin-admin/index.php?q=thread&s=0");

		// important
		try {
			sc.resinStart();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Map map = sc.getMStream();
		StringBuffer content = new StringBuffer();
		for (int i = 0; i < 100; i++) {
			content.append(map.get(String.valueOf(i)) + "\n");
			System.out.println("==========================" + i + "==================");
			System.out.println(content);

		}
	}
}
