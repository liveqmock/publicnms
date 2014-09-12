package com.afunms.polling.task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.dao.IISConfigDao;
import com.afunms.application.model.IISConfig;
import com.afunms.application.model.IISVo;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.Pingcollectdata;
import com.afunms.polling.snmp.IISSnmp;

public class IISDataCollector {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public void collect_Data(NodeGatherIndicators iisIndicatorsNode) {
		String iisID = iisIndicatorsNode.getNodeid();
		
		IISConfig iisConfig = null;
		IISConfigDao iisDao = null;
		try {
			try {
				iisDao = new IISConfigDao();
				iisConfig = (IISConfig) iisDao.findByID(iisID);
				if (iisConfig == null || iisConfig.getMon_flag() == 1) {
					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				iisDao.close();
			}
			
			
			IISSnmp iissnmp = new IISSnmp();
			List resultList = null;
			try {
				resultList = iissnmp.collect_Data(iisConfig);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				// 判断告警
				IISVo iisvo = new IISVo();
				iisvo = (IISVo) resultList.get(0);
				if (resultList != null && resultList.size() > 0 && iisvo.getCurrentAnonymousUsers() != null) {
					ShareData.setIisdata(iisConfig.getIpaddress(), resultList);
					com.afunms.polling.node.IIS tnode = (com.afunms.polling.node.IIS) PollingEngine.getInstance().getIisByIP(iisConfig.getIpaddress());
					tnode.setAlarm(false);
					tnode.setStatus(0);
					Pingcollectdata hostdata = null;
					hostdata = new Pingcollectdata();
					hostdata.setIpaddress(iisConfig.getIpaddress());
					Calendar date = Calendar.getInstance();
					hostdata.setCollecttime(date);
					hostdata.setCategory("IISPing");
					hostdata.setEntity("Utilization");
					hostdata.setSubentity("ConnectUtilization");
					hostdata.setRestype("dynamic");
					hostdata.setUnit("%");
					hostdata.setThevalue("100");

					try {
						iisDao.createHostData(hostdata);
						Vector v = new Vector();
						v.add(hostdata);
						v.add(tnode);
						if (ShareData.getIISPingdata() != null) {
							ShareData.setIISPingdata(tnode.getIpaddress(), v);
						} else {
							Hashtable iispinghash = new Hashtable();
							iispinghash.put(tnode.getIpaddress(), v);
							ShareData.setIISPingdata(iispinghash);
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						iisDao.close();
					}
					try {
						NodeUtil nodeUtil = new NodeUtil();
						NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(tnode);
						AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
						List list1 = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(nodeDTO.getId() + "", nodeDTO.getType(), nodeDTO.getSubtype());
						CheckEventUtil checkEventUtil = new CheckEventUtil();
						for (int i = 0; i < list1.size(); i++) {
							AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode) list1.get(i);
							if ("ping".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
								checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, hostdata.getThevalue());
							} else if ("curconns".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
								checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, iisvo.getCurrentConnections());
							} else if ("logonatmps".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
								checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, iisvo.getLogonAttempts());
							} else if ("curanyusers".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
								checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, iisvo.getCurrentAnonymousUsers());
							} else if ("connatmps".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
								checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, iisvo.getConnectionAttempts());
							}
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				} else {
					// 需要增加邮件服务所在的服务器是否能连通
					Host host = (Host) PollingEngine.getInstance().getNodeByIP(iisConfig.getIpaddress());
					Vector ipPingData = (Vector) ShareData.getPingdata().get(iisConfig.getIpaddress());
					if (ipPingData != null) {
						Pingcollectdata pingdata = (Pingcollectdata) ipPingData.get(0);
						Calendar tempCal = (Calendar) pingdata.getCollecttime();
						Date cc = tempCal.getTime();
						String _time = sdf.format(cc);
						String lastTime = _time;
						String pingvalue = pingdata.getThevalue();
						if (pingvalue == null || pingvalue.trim().length() == 0)
							pingvalue = "0";
						double pvalue = new Double(pingvalue);
						if (pvalue == 0) {
							// //主机服务器连接不上***********************************************
							// com.afunms.polling.node.IIS
							// tnode=(com.afunms.polling.node.IIS)PollingEngine.getInstance().getIisByIP(iisconf.getIpaddress());
							// tnode.setAlarm(true);
							// tnode.setStatus(3);
							// List alarmList = tnode.getAlarmMessage();
							// if(alarmList == null)alarmList = new ArrayList();
							// tnode.getAlarmMessage().add("IIS服务停止,因为所在的服务器连接不上");
							// String sysLocation = "";
							// try{
							// SmscontentDao eventdao = new SmscontentDao();
							// String eventdesc = "IIS服务("+tnode.getAlias()+"
							// IP:"+tnode.getAdminIp()+")"+"的IIS服务停止";
							// eventdao.createEventWithReasion("poll",tnode.getId()+"",tnode.getAdminIp()+"("+tnode.getAdminIp()+")",eventdesc,3,"iis","ping","所在的服务器连接不上");
							// Pingcollectdata hostdata=null;
							// hostdata=new Pingcollectdata();
							// hostdata.setIpaddress(iisconf.getIpaddress());
							// Calendar date=Calendar.getInstance();
							// hostdata.setCollecttime(date);
							// hostdata.setCategory("IISPing");
							// hostdata.setEntity("Utilization");
							// hostdata.setSubentity("ConnectUtilization");
							// hostdata.setRestype("dynamic");
							// hostdata.setUnit("%");
							// hostdata.setThevalue("0");
							// IISConfigDao iisdao=new IISConfigDao();
							// try{
							// iisdao.createHostData(hostdata);
							// }catch(Exception e){
							// e.printStackTrace();
							// }finally{
							// iisdao.close();
							// }
							// Vector v = new Vector();
							// v.add(hostdata);
							// v.add(tnode);
							// ShareData.setIISPingdata(tnode.getIpaddress(),
							// v);
							// }catch(Exception e){
							// e.printStackTrace();
							// }
						} else {
							com.afunms.polling.node.IIS tnode = (com.afunms.polling.node.IIS) PollingEngine.getInstance().getIisByIP(iisConfig.getIpaddress());
							tnode.setAlarm(true);
							tnode.setStatus(3);
							tnode.setIpaddress(iisConfig.getIpaddress());
							List alarmList = tnode.getAlarmMessage();
							Pingcollectdata hostdata = null;
							hostdata = new Pingcollectdata();
							hostdata.setIpaddress(iisConfig.getIpaddress());
							Calendar date = Calendar.getInstance();
							hostdata.setCollecttime(date);
							hostdata.setCategory("IISPing");
							hostdata.setEntity("Utilization");
							hostdata.setSubentity("ConnectUtilization");
							hostdata.setRestype("dynamic");
							hostdata.setUnit("%");
							hostdata.setThevalue("0");
							IISConfigDao iisdao = new IISConfigDao();
							try {
								iisdao.createHostData(hostdata);
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								iisdao.close();
							}
							Vector v = new Vector();
							v.add(hostdata);
							v.add(tnode);
							if (ShareData.getIISPingdata() != null) {
								ShareData.getIISPingdata().put(tnode.getIpaddress(), v);
								// ShareData.setIISPingdata(tnode.getIpaddress(),
								// v);
							} else {
								Hashtable iispinghash = new Hashtable();
								iispinghash.put(tnode.getIpaddress(), v);
								ShareData.setIISPingdata(iispinghash);
							}

							try {
								NodeUtil nodeUtil = new NodeUtil();
								NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(tnode);
								// 判断是否存在此告警指标
								AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
								List list1 = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(nodeDTO.getId() + "", nodeDTO.getType(), nodeDTO.getSubtype());
								CheckEventUtil checkEventUtil = new CheckEventUtil();
								for (int i = 0; i < list1.size(); i++) {
									AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode) list1.get(i);
									if ("ping".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
										checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, hostdata.getThevalue());
									} else if ("curconns".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
										checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, iisvo.getCurrentConnections());
									} else if ("logonatmps".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
										checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, iisvo.getLogonAttempts());
									} else if ("curanyusers".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
										checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, iisvo.getCurrentAnonymousUsers());
									} else if ("connatmps".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
										checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, iisvo.getConnectionAttempts());
									}
								}
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}

					} else {
						com.afunms.polling.node.IIS tnode = (com.afunms.polling.node.IIS) PollingEngine.getInstance().getIisByID(iisConfig.getId());
						tnode.setAlarm(true);
						tnode.setStatus(3);
						tnode.setIpaddress(iisConfig.getIpaddress());
						List alarmList = tnode.getAlarmMessage();
						if (alarmList == null)
							alarmList = new ArrayList();
						tnode.getAlarmMessage().add("IIS服务停止");
						Pingcollectdata hostdata = null;
						hostdata = new Pingcollectdata();
						hostdata.setIpaddress(iisConfig.getIpaddress());
						Calendar date = Calendar.getInstance();
						hostdata.setCollecttime(date);
						hostdata.setCategory("IISPing");
						hostdata.setEntity("Utilization");
						hostdata.setSubentity("ConnectUtilization");
						hostdata.setRestype("dynamic");
						hostdata.setUnit("%");
						hostdata.setThevalue("0");
						IISConfigDao iisdao = new IISConfigDao();
						try {
							iisdao.createHostData(hostdata);
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							iisdao.close();
						}
						Vector v = new Vector();
						v.add(hostdata);
						v.add(tnode);
						// ShareData.setIISPingdata(iisconf.getIpaddress(), v);
						if (ShareData.getIISPingdata() != null) {
							ShareData.setIISPingdata(tnode.getIpaddress(), v);
						} else {
							Hashtable iispinghash = new Hashtable();
							iispinghash.put(tnode.getIpaddress(), v);
							ShareData.setIISPingdata(iispinghash);
						}

						try {
							NodeUtil nodeUtil = new NodeUtil();
							NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(tnode);
							// 判断是否存在此告警指标
							AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
							List list1 = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(nodeDTO.getId() + "", nodeDTO.getType(), nodeDTO.getSubtype());
							CheckEventUtil checkEventUtil = new CheckEventUtil();
							for (int i = 0; i < list1.size(); i++) {
								AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode) list1.get(i);
								if ("ping".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
									checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, hostdata.getThevalue());
								} else if ("curconns".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
									checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, iisvo.getCurrentConnections());
								} else if ("logonatmps".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
									checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, iisvo.getLogonAttempts());
								} else if ("curanyusers".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
									checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, iisvo.getCurrentAnonymousUsers());
								} else if ("connatmps".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
									checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, iisvo.getConnectionAttempts());
								}
							}
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				SysLogger.error(ex.getMessage());
			} finally {
				resultList = null;
			}

		} catch (Exception exc) {
			exc.printStackTrace();
		}

	}

}
