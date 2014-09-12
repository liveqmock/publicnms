package com.afunms.polling.snmp.sqlserver;

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
import com.afunms.polling.om.Pingcollectdata;

public class collect_pingProxy {

	public collect_pingProxy() {
	}

	@SuppressWarnings("unchecked")
	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicators) {
		Hashtable returndata = new Hashtable();
		Calendar date = Calendar.getInstance();

		List dbmonitorlists = new ArrayList();
		dbmonitorlists = ShareData.getDBList();
		DBVo dbmonitorlist = new DBVo();
		if (dbmonitorlists != null && dbmonitorlists.size() > 0) {
			for (int i = 0; i < dbmonitorlists.size(); i++) {
				DBVo vo = (DBVo) dbmonitorlists.get(i);
				if (vo.getId() == Integer.parseInt(nodeGatherIndicators.getNodeid())) {
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
			boolean sqlserverIsOK = false;
			try {
				String[] args = new String[] { "status" };
				String htKey = "ping";
				returndata = LogParser.parse(this, dbmonitorlist, htKey, args);
				Vector v = (Vector) returndata.get(htKey);
				if (null != v && !v.isEmpty()) {
					returndata = (Hashtable) v.get(0);
					if (null != returndata) {
						String status = returndata.get("status").toString();
						if ("1".equals(status.trim())) {
							sqlserverIsOK = true;
						}
					}
				}
			} catch (Exception e) {
				sqlserverIsOK = false;
			}

			String status = "0";
			String theValue = "0";
			if (sqlserverIsOK) {
				returndata.put("ping", "100");
				status = "1";
				theValue = "100";
			} else {
				returndata.put("ping", "0");
				status = "0";
			}
			// 更新当前状态
			String hex = IpTranslation.formIpToHex(dbmonitorlist.getIpAddress());
			DBDao dbdao = new DBDao();
			try {
				dbdao.clearTableData("nms_sqlserverstatus", dbmonitorlist.getIpAddress());
				dbdao.addSqlserver_nmsstatus(dbmonitorlist.getIpAddress(), status);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dbdao.close();
			}
			// 更新内存
			if (!(ShareData.getSharedata().containsKey(dbmonitorlist.getIpAddress()))) {
				ShareData.getSharedata().put(dbmonitorlist.getIpAddress(), returndata);
			} else {
				Hashtable sqlserverHash = (Hashtable) ShareData.getSharedata().get(hex + ":" + dbmonitorlist.getAlias());
				sqlserverHash.put("ping", (String) returndata.get("ping"));
			}

			// 入库
			try {
				dbdao = new DBDao();
				Pingcollectdata hostdata = null;
				hostdata = new Pingcollectdata();
				hostdata.setIpaddress(dbmonitorlist.getIpAddress());
				hostdata.setCollecttime(date);
				hostdata.setCategory("SQLPing");
				hostdata.setEntity("Utilization");
				hostdata.setSubentity("ConnectUtilization");
				hostdata.setRestype("dynamic");
				hostdata.setUnit("%");
				hostdata.setThevalue(theValue);
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
					NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(dbmonitorlist);
					// 判断是否存在此告警指标
					AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
					List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(nodeDTO.getId() + "", nodeDTO.getType(), nodeDTO.getSubtype());
					CheckEventUtil checkEventUtil = new CheckEventUtil();
					for (int i = 0; i < list.size(); i++) {
						AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode) list.get(i);
						if ("ping".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
							if (pingvalue != null) {
								checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, pingvalue);
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
