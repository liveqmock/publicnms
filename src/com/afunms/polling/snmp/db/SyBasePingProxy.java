package com.afunms.polling.snmp.db;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.dao.DBDao;
import com.afunms.application.model.DBVo;
import com.afunms.application.util.IpTranslation;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.ShareData;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.polling.om.Pingcollectdata;

/**
 * Oracle ping 采集 使用JDBC采集
 * 
 * @author wupinlong 2012/09/17
 * 
 */
public class SyBasePingProxy extends SnmpMonitor {

	public SyBasePingProxy() {
	}

	@SuppressWarnings("unchecked")
	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicators) {
		Hashtable returndata = new Hashtable();

		DBDao dbdao = new DBDao();
		List dbmonitorlists = new ArrayList();
		dbmonitorlists = ShareData.getDBList();
		DBVo dbmonitorlist = new DBVo();
		if (dbmonitorlists != null && dbmonitorlists.size() > 0) {
			for (int i = 0; i < dbmonitorlists.size(); i++) {
				DBVo vo = (DBVo) dbmonitorlists.get(i);
				if (vo.getId() == Integer.parseInt(nodeGatherIndicators
						.getNodeid())) {
					dbmonitorlist = vo;
					break;
				}
			}
		}
		if (dbmonitorlist != null) {
			if (dbmonitorlist.getManaged() == 0) {
				// 如果未被管理，不采集，ping值为0
				returndata.put("ping", 0);
				return returndata;
			}
			boolean sybaseIsOK = false;
			try {
				String[] args = new String[]{"status"};
				String htKey = "ping";
				returndata = LogParser.parse(this, dbmonitorlist, htKey, args);
				Vector v = (Vector)returndata.get(htKey);
				if (null != v && !v.isEmpty()) {
					returndata = (Hashtable)v.get(0);
					if (null != returndata) {
						String status = (String)returndata.get("status");
						if ("1".equals(status.trim())) {
							sybaseIsOK = true;
						}
					}
				}
				
			} catch (Exception e) {
				sybaseIsOK = false;
			} finally {
				dbdao.close();
			}
			
			returndata = new Hashtable();
			String status = "0";
			if (sybaseIsOK) {
				returndata.put("ping", "100");
				status = "1";
			} else {
				returndata.put("ping", "0");
				status = "0";
			}
			// 更新当前状态
			dbdao = new DBDao();
			String hex = IpTranslation
					.formIpToHex(dbmonitorlist.getIpAddress());
			try {
				dbdao.updateNmsValueByUniquekeyAndTablenameAndKey(
						"nms_sybasestatus", "serverip", hex + ":"
								+ dbmonitorlist.getId(), "status", status);
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				dbdao.close();
			}

			// 更新内存
			if (!(ShareData.getSharedata().containsKey(dbmonitorlist
					.getIpAddress()
					+ ":" + dbmonitorlist.getId()))) {
				ShareData.getSharedata().put(
						dbmonitorlist.getIpAddress() + ":"
								+ dbmonitorlist.getId(), returndata);
			} else {
				Hashtable sybaseHash = (Hashtable) ShareData.getSharedata()
						.get(
								dbmonitorlist.getIpAddress() + ":"
										+ dbmonitorlist.getId());
				sybaseHash.put("ping", (String) returndata.get("ping"));
			}

			// 入库
			try {
				Pingcollectdata hostdata = null;
				hostdata = new Pingcollectdata();
				hostdata.setIpaddress(dbmonitorlist.getIpAddress() + ":"
						+ dbmonitorlist.getId());
				Calendar date = Calendar.getInstance();
				hostdata.setCollecttime(date);
				hostdata.setCategory("SybasePing");
				hostdata.setEntity("Utilization");
				hostdata.setSubentity("ConnectUtilization");
				hostdata.setRestype("dynamic");
				hostdata.setUnit("%");
				hostdata.setThevalue((String) returndata.get("ping"));
				try {
					dbdao.createHostData(hostdata);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					dbdao.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			String pingvalue = (String) returndata.get("ping");
			// 判断告警
			try {
				if (pingvalue != null) {
					NodeUtil nodeUtil = new NodeUtil();
					NodeDTO nodeDTO = nodeUtil
							.conversionToNodeDTO(dbmonitorlist);
					// 判断是否存在此告警指标
					AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
					List list = alarmIndicatorsUtil
							.getAlarmInicatorsThresholdForNode(nodeDTO.getId()
									+ "", nodeDTO.getType(), nodeDTO
									.getSubtype());
					CheckEventUtil checkEventUtil = new CheckEventUtil();
					for (int i = 0; i < list.size(); i++) {
						AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode) list
								.get(i);
						if ("ping".equalsIgnoreCase(alarmIndicatorsNode
								.getName())) {
							if (pingvalue != null) {
								checkEventUtil.checkEvent(nodeDTO,
										alarmIndicatorsNode, pingvalue);
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return returndata;
	}
}
