package com.afunms.polling.snmp.db;

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
 * DB2 ping 采集 使用JDBC采集
 * 
 * @author yangjun 2013/02/23
 * 
 */
public class Db2PingSnmp extends SnmpMonitor {

	public Db2PingSnmp() {
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
			String dbnames = dbmonitorlist.getDbName();
			String serverip = dbmonitorlist.getIpAddress();
			String username = dbmonitorlist.getUser();
			String passwords = "";
			int allFlag = 0;
			int port = Integer.parseInt(dbmonitorlist.getPort());
			String[] dbs = dbnames.split(",");
			boolean db2IsOK = false;
			Hashtable allDb2Data = new Hashtable();
			if(dbs!=null&&dbs.length>0){
				for (int k = 0; k < dbs.length; k++){
					String dbStr = dbs[k];
					try {
						passwords = EncryptUtil.decode(dbmonitorlist.getPassword());
						db2IsOK = dbdao.getDB2IsOK(serverip, port, dbStr, username, passwords);
					} catch (Exception e) {
						db2IsOK = false;
					} finally {
						dbdao.close();
					}
					if(!db2IsOK){
						allFlag++;
					}
				}
			}
			if(allFlag>0){//有一个数据库是不通的
				allDb2Data.put("status", "0");
				returndata.put("ping", "0");
			} else {//所有数据库是连通的
				allDb2Data.put("status", "1");
				returndata.put("ping", "100");
			}
			// 更新当前状态
			dbdao = new DBDao();
			String hex = IpTranslation.formIpToHex(dbmonitorlist.getIpAddress());
			String sip = hex+":"+dbmonitorlist.getId();
			try {
				dbdao.addOrUpdateDB2_nmsstatus(sip,String.valueOf(allDb2Data.get("status")));
				dbdao.addOrUpdateDB2_nmsdbnames(sip,dbs);
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
				Hashtable db2Hash = (Hashtable) ShareData.getSharedata().get(
								dbmonitorlist.getIpAddress() + ":"
										+ dbmonitorlist.getId());
				db2Hash.put("ping", (String) returndata.get("ping"));
			}

			// 入库
			try {
				Pingcollectdata hostdata = null;
				hostdata = new Pingcollectdata();
				hostdata.setIpaddress(serverip);
				Calendar date = Calendar.getInstance();
				hostdata.setCollecttime(date);
				hostdata.setCategory("DB2Ping");
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
