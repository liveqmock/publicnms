package com.afunms.polling.snmp.informix;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.dao.DBDao;
import com.afunms.application.model.DBVo;
import com.afunms.application.util.IpTranslation;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.EncryptUtil;
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
public class InformixPingSnmp extends SnmpMonitor {

	public InformixPingSnmp() {
	}

	@SuppressWarnings("unchecked")
	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicators) {
		Hashtable returndata = new Hashtable();

		DBDao dbDao = new DBDao();
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
			boolean informixIsOK = false;
			Hashtable monitorValue = new Hashtable();
			String serverip = dbmonitorlist.getIpAddress();
			int port = Integer.parseInt(dbmonitorlist.getPort());
			String username = dbmonitorlist.getUser();
			String passwords="";
			try {
				passwords = EncryptUtil.decode(dbmonitorlist.getPassword());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			String dbnames = dbmonitorlist.getDbName();
			String dbservername = dbmonitorlist.getAlias();//临时的服务名称
			try {
				informixIsOK = dbDao.getInformixIsOk(serverip, port + "", username, passwords, dbnames, dbservername);
			} catch (Exception e) {
				e.printStackTrace();
				informixIsOK = false;
			} finally {
				dbDao.close();
			}
			
			String status = "0";
			if (informixIsOK) {
				returndata.put("ping", "100");
				status = "1";
			} else {
				returndata.put("ping", "0");
				status = "0";
			}
			// 更新当前状态
			dbDao = new DBDao();
			String hex = IpTranslation.formIpToHex(dbmonitorlist.getIpAddress());
			try {
				if(status != null && !status.equals("")){
					dbDao.clearTableData("nms_informixstatus", hex+":"+dbnames);
					try {
						dbDao.addInformix_nmsstatus(hex+":"+dbnames,status);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				dbDao.close();
			}
			Hashtable informixData = new Hashtable();
			informixData.put("status", status);
			informixData.put("informix", returndata);
			monitorValue.put(dbnames, informixData);
			if (informixIsOK) {
				monitorValue.put("runningflag", "正在运行");
			} else {
				monitorValue.put("runningflag", "<font color=red>服务停止</font>");
			}

			// 更新内存
			if (!(ShareData.getInformixmonitordata().containsKey(serverip))) {
				ShareData.setInfomixmonitordata(serverip, monitorValue);
			} else {
				Hashtable informixHash = (Hashtable) ShareData.getInformixmonitordata().get(serverip);
				((Hashtable)((Hashtable)informixHash.get(dbnames)).get("informix")).put("ping", (String) returndata.get("ping"));
			}

			// 入库
			try {
				dbDao = new DBDao();
				Pingcollectdata hostdata = null;
				hostdata = new Pingcollectdata();
				hostdata.setIpaddress(serverip);
				Calendar date = Calendar.getInstance();
				hostdata.setCollecttime(date);
				hostdata.setCategory("INFORMIXPing");
				hostdata.setEntity("Utilization");
				hostdata.setSubentity("ConnectUtilization");
				hostdata.setRestype("dynamic");
				hostdata.setUnit("%");
				hostdata.setThevalue((String) returndata.get("ping"));
				try {
					dbDao.createHostData(hostdata);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					dbDao.close();
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
