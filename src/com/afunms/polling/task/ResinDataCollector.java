package com.afunms.polling.task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.apache.commons.beanutils.BeanUtils;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.dao.ResinDao;
import com.afunms.application.model.Resin;
import com.afunms.application.resinmonitor.ResinServerStream;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.ShareData;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.om.Pingcollectdata;

public class ResinDataCollector {

	private Hashtable sendeddata = ShareData.getSendeddata();

	public ResinDataCollector() {

	}

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public void collect_Data(NodeGatherIndicators dnsIndicators) {
		String id = dnsIndicators.getNodeid();
		this.collectResinData(id);
	}

	public void collectResinData(String id) {
		try {
			ResinServerStream serverstream = new ResinServerStream();
			Hashtable returnVal = new Hashtable();
			String ipaddress = "";
			Resin node = new Resin();
			ResinDao dao = new ResinDao();
			try {
				node = (Resin) dao.findByID(id);
			} catch (Exception e) {
			} finally {
				dao.close();
			}
			if (node.getMonflag() == 0)
				return;
			try {
				ipaddress = node.getIpAddress();
				com.afunms.polling.node.Resin tnode = (com.afunms.polling.node.Resin) PollingEngine.getInstance().getResinByIP(ipaddress);
				Calendar date = Calendar.getInstance();
				Date cc = date.getTime();
				String tempsenddate = sdf.format(cc);
				// 初始化Resin服务的状态
				tnode.setLastTime(tempsenddate);
				tnode.setAlarm(false);
				tnode.getAlarmMessage().clear();
				tnode.setStatus(0);

				StringBuffer tmp = new StringBuffer();
				tmp.append(node.getIpAddress());
				tmp.append(",");
				tmp.append(node.getPort());
				tmp.append(",");
				tmp.append(node.getUser());
				tmp.append(",");
				tmp.append(node.getPassword());
				tmp.append(",");
				tmp.append(node.getVersion());
				returnVal.put(String.valueOf(0), tmp.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}

			// 验证resin服务器是否有效
			String liststr = serverstream.validResinServer(returnVal);
			String pingValue = "0";
			// 在无效的情况下,进行ping
			if ("".equals(liststr)) {

				try {
					com.afunms.polling.node.Resin tc = new com.afunms.polling.node.Resin();
					BeanUtils.copyProperties(tc, node);
					// 需要增加邮件服务所在的服务器是否能连通
					Vector ipPingData = (Vector) ShareData.getPingdata().get(node.getIpAddress());
					if (ipPingData != null) {// 有返回数据的情况
						Pingcollectdata pingdata = (Pingcollectdata) ipPingData.get(0);
						String pingvalue = pingdata.getThevalue();
						if (pingvalue == null || pingvalue.trim().length() == 0)
							pingvalue = "0";
						double pvalue = new Double(pingvalue);
						if (pvalue == 0) {
							// 主机服务器连接不上
							com.afunms.polling.node.Resin tnode = (com.afunms.polling.node.Resin) PollingEngine.getInstance().getResinByIP(ipaddress);
							tnode.setAlarm(true);
							tnode.setStatus(1);
							List alarmList = tnode.getAlarmMessage();
							if (alarmList == null)
								alarmList = new ArrayList();
							tnode.getAlarmMessage().add("Resin服务停止");

						} else {
							com.afunms.polling.node.Resin tnode = (com.afunms.polling.node.Resin) PollingEngine.getInstance().getResinByIP(ipaddress);
							tnode.setAlarm(true);
							tnode.setStatus(3);
							List alarmList = tnode.getAlarmMessage();
							if (alarmList == null)
								alarmList = new ArrayList();
							tnode.getAlarmMessage().add("Resin服务停止");
							Pingcollectdata hostdata = null;
							hostdata = new Pingcollectdata();
							hostdata.setIpaddress(ipaddress);
							Calendar date = Calendar.getInstance();
							hostdata.setCollecttime(date);
							hostdata.setCategory("ResinPing");
							hostdata.setEntity("Utilization");
							hostdata.setSubentity("ConnectUtilization");
							hostdata.setRestype("dynamic");
							hostdata.setUnit("%");
							hostdata.setThevalue("0");
							ResinDao resindao = new ResinDao();
							try {
								resindao.createHostData(hostdata);
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								resindao.close();
							}
						}

					} else {
						com.afunms.polling.node.Resin tnode = (com.afunms.polling.node.Resin) PollingEngine.getInstance().getResinByIP(ipaddress);
						tnode.setAlarm(true);
						tnode.setStatus(3);

						List alarmList = tnode.getAlarmMessage();
						if (alarmList == null)
							alarmList = new ArrayList();
						tnode.getAlarmMessage().add("Resin服务停止");
						Pingcollectdata hostdata = null;
						hostdata = new Pingcollectdata();
						hostdata.setIpaddress(ipaddress);
						Calendar date = Calendar.getInstance();
						hostdata.setCollecttime(date);
						hostdata.setCategory("ResinPing");
						hostdata.setEntity("Utilization");
						hostdata.setSubentity("ConnectUtilization");
						hostdata.setRestype("dynamic");
						hostdata.setUnit("%");
						hostdata.setThevalue("0");
						ResinDao resindao = new ResinDao();
						try {
							resindao.createHostData(hostdata);
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							resindao.close();
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				pingValue = "100";
				Pingcollectdata hostdata = null;
				hostdata = new Pingcollectdata();
				hostdata.setIpaddress(ipaddress);
				Calendar date = Calendar.getInstance();
				hostdata.setCollecttime(date);
				hostdata.setCategory("ResinPing");
				hostdata.setEntity("Utilization");
				hostdata.setSubentity("ConnectUtilization");
				hostdata.setRestype("dynamic");
				hostdata.setUnit("%");
				hostdata.setThevalue("100");
				ResinDao resindao = new ResinDao();
				try {
					resindao.createHostData(hostdata);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					resindao.close();
				}
				String[] pos_s = liststr.split(",");
				if (pos_s != null) {
					for (int list_i = 0; list_i < pos_s.length - 1; list_i++) {
						String tmps = returnVal.get(pos_s[list_i]).toString();
						String[] serverinfo = tmps.split(",");

						// 获取resin信息
						if (serverinfo != null && serverinfo.length >= 4){
							serverstream.foundResinData(node,serverinfo[0], serverinfo[1], serverinfo[2], serverinfo[3], serverinfo[4]);
							
//							serverstream.parseResinData(serverinfo[0], serverinfo[1], serverinfo[2], serverinfo[3], serverinfo[4]);
						}
						}

				}
			}
			try {
				NodeUtil nodeUtil = new NodeUtil();
				NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(node);
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
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
	}

}