package com.afunms.polling.task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.apache.commons.beanutils.BeanUtils;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmConstant;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.dao.PSTypeDao;
import com.afunms.application.dao.Socketmonitor_realtimeDao;
import com.afunms.application.model.PSTypeVo;
import com.afunms.application.model.Socketmonitor_realtime;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.SocketService;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.om.Pingcollectdata;

public class SocketDataCollector {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public void collect_Data(NodeGatherIndicators psIndicatorsNode) {
		Calendar date = Calendar.getInstance();
		String portID = psIndicatorsNode.getNodeid();
		PSTypeVo portVo = null;
		PSTypeDao portDao = null;
		try {
			portDao = new PSTypeDao();
			portVo = (PSTypeVo) portDao.findByID(portID);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			portDao.close();
		}
		if (portVo == null || portVo.getFlag() == 0)
			return;

		try {
			Socketmonitor_realtimeDao realTimeDao = new Socketmonitor_realtimeDao();
			try {
				boolean flag = false;
				String connectValue = "0";

				String ipaddress = "";
				com.afunms.polling.node.SocketService tnode = (com.afunms.polling.node.SocketService) PollingEngine.getInstance().getSocketByID(portVo.getId());
				if (tnode == null || !tnode.isManaged()) {
					return;
				} else {
					// 初始化Socket的状态
					tnode.setLastTime(sdf.format(date.getTime()));
					tnode.setAlarm(false);
					tnode.getAlarmMessage().clear();
					tnode.setStatus(0);
				}
				try {
					com.afunms.polling.node.SocketService socket = new com.afunms.polling.node.SocketService();
					BeanUtils.copyProperties(socket, tnode);
					ipaddress = socket.getIpAddress();
					try {
						flag = SocketService.checkService(tnode.getIpaddress(), Integer.parseInt(tnode.getPort()), tnode.getTimeout());
						boolean old = false;
						Hashtable realHash = realTimeDao.getAllReal();
						Socketmonitor_realtime urold = null;
						if (realHash.get(tnode.getId()) != null) {
							old = true;
							urold = (Socketmonitor_realtime) realHash.get(tnode.getId());
						}
						// 保存实时数据
						if (old == true) {
							urold.setMon_time(Calendar.getInstance());
							realTimeDao.update(urold);
						}
						if (old == false) {
							urold = new Socketmonitor_realtime();
							urold.setId(urold.getId());
							urold.setSocket_id(tnode.getId());
							if (flag) {
								urold.setIs_canconnected(1);
							} else {
								urold.setIs_canconnected(0);
							}
							urold.setMon_time(Calendar.getInstance());
							realTimeDao.save(urold);
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						realTimeDao.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (flag) {
					connectValue = "100";
				}
				Pingcollectdata hostdata = null;
				hostdata = new Pingcollectdata();
				hostdata.setIpaddress(ipaddress);
				hostdata.setCollecttime(date);
				hostdata.setCategory("SOCKETPing");
				hostdata.setEntity("Utilization");
				hostdata.setSubentity("ConnectUtilization");
				hostdata.setRestype("dynamic");
				hostdata.setUnit("%");
				hostdata.setThevalue(connectValue);
				PSTypeDao socketdao = new PSTypeDao();
				try {
					socketdao.createHostData(hostdata, tnode.getPort());
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					socketdao.close();
				}

				// 判断SOCKET连接告警
				Pingcollectdata alarmData = null;
				alarmData = new Pingcollectdata();
				alarmData.setIpaddress(ipaddress);
				alarmData.setCollecttime(date);
				alarmData.setCategory("SOCKETPing");
				alarmData.setEntity("Utilization");
				alarmData.setSubentity("ConnectUtilization");
				alarmData.setRestype("dynamic");
				alarmData.setUnit("%");
				if (flag) {
					alarmData.setThevalue("1");
				} else {
					alarmData.setThevalue("0");
				}
				Vector socketv = new Vector();
				socketv.add(alarmData);
				Hashtable collectHash = new Hashtable();
				collectHash.put("socket", socketv);
				NodeUtil nodeUtil = new NodeUtil();
				NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(portVo);
				try {
					AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
					List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(portVo.getId()), AlarmConstant.TYPE_SERVICE, "socket");
					for (int k = 0; k < list.size(); k++) {
						AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode) list.get(k);
						CheckEventUtil checkEventUtil = new CheckEventUtil();
						checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsnode, alarmData.getThevalue());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}