package com.afunms.polling.task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.dao.ApacheConfigDao;
import com.afunms.application.dao.Apachemonitor_historyDao;
import com.afunms.application.dao.Apachemonitor_realtimeDao;
import com.afunms.application.dao.TomcatDao;
import com.afunms.application.jbossmonitor.HttpClientJBoss;
import com.afunms.application.manage.ApacheManager;
import com.afunms.application.model.ApacheConfig;
import com.afunms.application.model.Apachemonitor_history;
import com.afunms.application.model.Apachemonitor_realtime;
import com.afunms.application.model.IISConfig;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.PingUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.Pingcollectdata;

public class ApacheDataCollector {
	public ApacheDataCollector() {

	}

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public void collect_Data(NodeGatherIndicators apacheIndicators) {
		String id = apacheIndicators.getNodeid();
		this.collectData(id);
	}

	public void collectData(String id) {

		Hashtable returnhash = new Hashtable();
		Apachemonitor_realtimeDao apachemonitor_realtimeDao = null;
		List<ApacheConfig> apacheConfigList = null;
		ApacheConfig apacheConfig = null;
		String reason = "";
		Calendar date = Calendar.getInstance();
		Integer iscanconnected = new Integer(0 + "");
		ApacheManager apacheManager = new ApacheManager();
		Hashtable hst = null;
		String pingValue = "0";
		String statusValue = "0";
		try {
			if (ShareData.getApachlist() != null) {
				List apachList = ShareData.getApachlist();
				ApacheConfig vo = null;
				if (apachList != null && apachList.size() > 0) {
					for (int i = 0; i < apachList.size(); i++) {
						vo = (ApacheConfig) apachList.get(i);
						if (vo.getFlag() == 0)
							continue;
						if (vo.getId() == Integer.parseInt(id))
							apacheConfig = vo;
						break;
					}
				} else
					return;
			} else {
				return;
			}
			String ipaddress = "";
			int port = 0;
			boolean isSucess = false;
			if (apacheConfig != null) {
				ipaddress = apacheConfig.getIpaddress();
				port = apacheConfig.getPort();
				HttpClientJBoss apache = new HttpClientJBoss();
				String response = apache.getGetResponseWithHttpClient("http://" + ipaddress + ":" + port, "GBK");
				if (response.toLowerCase().contains("it works")) {
					reason = "Apache服务有效";
					isSucess = true;
				}
				if (isSucess)
					hst = apacheManager.collectapachedata(apacheConfig);
				if (hst == null)
					hst = new Hashtable();
			
			String time = sdf.format(date.getTime());
			try {
				if (isSucess) {
					reason = "Apache服务有效";
					iscanconnected = new Integer(1);
					hst.put("ping", "100");
					pingValue = "100";
					statusValue = "100";
				} else {
					// 需要增加服务所在的服务器是否能连通
					reason = "Apache服务无效";
					Vector ipPingData = (Vector) ShareData.getPingdata().get(apacheConfig.getIpaddress());
					String eventdesc = "Apache服务(IP:" + apacheConfig.getIpaddress() + ")" + "的Apache服务停止";
					if (ipPingData == null) {
						PingUtil pingU = new PingUtil(ipaddress);
						Integer[] packet = pingU.ping();
						Vector vector = null;
						try {
							vector = pingU.addhis(packet);
						} catch (Exception e) {
							e.printStackTrace();
						}
//						if (vector != null) {
//							ShareData.setPingdata(ipaddress, vector);
//							returnhash.put("ping", vector);
//						}
						ipPingData = vector;
					}
					if (ipPingData != null) {
						Pingcollectdata pingdata = (Pingcollectdata) ipPingData.get(0);
						Calendar tempCal = (Calendar) pingdata.getCollecttime();
						Date cc = tempCal.getTime();
						time = sdf.format(cc);
						String lastTime = time;
						String pingvalue = pingdata.getThevalue();
						if (pingvalue == null || pingvalue.trim().length() == 0)
							pingvalue = "0";
						if (pingvalue.equals("0")) {
							reason = eventdesc;
						}

					}
					statusValue = "0";
					hst.put("ping", "0");
				}

			} catch (Exception ex) {
				SysLogger.error("ApacheTask error", ex);

			}
			try {
				NodeUtil nodeUtil = new NodeUtil();
				NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(apacheConfig);
				// 判断是否存在此告警指标
				AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
				List list1 = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(nodeDTO.getId() + "", nodeDTO.getType(), nodeDTO.getSubtype());
				CheckEventUtil checkEventUtil = new CheckEventUtil();
				for (int i = 0; i < list1.size(); i++) {
					AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode) list1.get(i);
					if ("ping".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
						checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, pingValue);
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			Pingcollectdata hostdata = null;
			
			ApacheConfigDao apachedao = null;
			try {
				hostdata = new Pingcollectdata();
				hostdata.setIpaddress(ipaddress);
				hostdata.setCollecttime(date);
				hostdata.setCategory("ApachePing");
				hostdata.setEntity("Utilization");
				hostdata.setSubentity("ConnectUtilization");
				hostdata.setRestype("dynamic");
				hostdata.setUnit("%");
				hostdata.setThevalue(statusValue);
				
				apachedao = new ApacheConfigDao();
				apachedao.createHostData(hostdata);

				
//				hostdata = new Pingcollectdata();
//				hostdata.setIpaddress(ipaddress);
//				hostdata.setCollecttime(date);
//				hostdata.setCategory("thevalue");
//				hostdata.setEntity("Utilization");
//				hostdata.setSubentity("ConnectUtilization");
//				hostdata.setRestype("dynamic");
//				hostdata.setUnit("%");
//				hostdata.setThevalue(statusValue);
//
//				apachedao.createHostData(hostdata);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				apachedao.close();
			}
			// 保存进历史数据
//			Apachemonitor_historyDao apachemonitor_historyDao = new Apachemonitor_historyDao();
//			Apachemonitor_history apacheMonitor_history = new Apachemonitor_history();
//			apacheMonitor_history.setApache_id(apacheConfig.getId());
//			apacheMonitor_history.setIs_canconnected(iscanconnected);
//			apacheMonitor_history.setMon_time(Calendar.getInstance());
//			apacheMonitor_history.setReason(reason);
//			apachemonitor_historyDao.save(apacheMonitor_history);

			// UrlDataCollector udc = new UrlDataCollector();
			Apachemonitor_realtime apachemonitor_realtime = new Apachemonitor_realtime();
			apachemonitor_realtime.setApache_id(apacheConfig.getId());
			apachemonitor_realtime.setIs_canconnected(iscanconnected);
			apachemonitor_realtime.setReason(reason);
			apachemonitor_realtime.setMon_time(Calendar.getInstance());

			boolean old = false;
			Hashtable realHash = new Hashtable();

			apachemonitor_realtimeDao = new Apachemonitor_realtimeDao();
			realHash = apachemonitor_realtimeDao.getAllReal();//
			Apachemonitor_realtime apachemonitor_realtimeold = new Apachemonitor_realtime();
			if (realHash != null && realHash.get(id) != null) {
				old = true;
				apachemonitor_realtimeold = (Apachemonitor_realtime) realHash.get(id);
			}
			// 保存realtime
			if (old == true) {
				apachemonitor_realtime.setId(apachemonitor_realtimeold.getId());
				apachemonitor_realtimeDao.update(apachemonitor_realtime);

			} else {
				apachemonitor_realtimeDao.save(apachemonitor_realtime);
			}
			hst.put("realtime", apachemonitor_realtime);
			ShareData.setApachedata("apache:" + ipaddress, hst);
			}
		} catch (Exception e) {
			SysLogger.error("ApacheDataCollector.collect_Data()", e);
		} finally {
			apachemonitor_realtimeDao.close();
		}

	}

	private void updateAppacheData(NodeDTO nodeDTO, Hashtable hashtable) {
		AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
		List list = alarmIndicatorsUtil.getAlarmIndicatorsForNode(nodeDTO.getId() + "", nodeDTO.getType(), nodeDTO.getSubtype());
		if (list == null || list.size() == 0) {
			return;
		}
		String value = (String) hashtable.get("ping");
		CheckEventUtil checkEventUtil = new CheckEventUtil();
		for (int i = 0; i < list.size(); i++) {
			try {
				AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode) list.get(i);
				if ("ping".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
					if (value != null && !"".equals(value)) {
						checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, value);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
