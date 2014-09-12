package com.afunms.polling.snmp.weblogic;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import com.afunms.application.dao.WeblogicConfigDao;
import com.afunms.application.model.WeblogicConfig;
import com.afunms.application.weblogicmonitor.WeblogicQueue;
import com.afunms.application.weblogicmonitor.WeblogicSnmp;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SystemConstant;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.gatherdb.GathersqlListManager;

/**
 * weblogic queue采集
 * 
 * @author yangjun 2013/3/18
 * 
 */
public class QueueSnmp extends SnmpMonitor {

	public QueueSnmp() {
	}

	@SuppressWarnings("unchecked")
	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicators) {
		Hashtable returndata = new Hashtable();
		WeblogicConfig weblogicconf = null;
		String id = nodeGatherIndicators.getNodeid();
		try {
			WeblogicConfigDao dao = new WeblogicConfigDao();
			try {
				weblogicconf = (WeblogicConfig) dao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			List queueValue = new ArrayList();
			WeblogicSnmp weblogicsnmp=null;
			try {
				weblogicsnmp = new WeblogicSnmp(weblogicconf.getIpAddress(),weblogicconf.getCommunity(),weblogicconf.getPortnum());
				queueValue = weblogicsnmp.collectQueueData();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(queueValue!=null){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				returndata.put("queueValue", queueValue);
				if (!(ShareData.getWeblogicdata().containsKey(weblogicconf.getIpAddress()))) {
					ShareData.getWeblogicdata().put(weblogicconf.getIpAddress(), returndata);
				} else {
					Hashtable hash = (Hashtable) ShareData.getWeblogicdata().get(weblogicconf.getIpAddress());
					hash.put("queueValue", returndata.get("queueValue"));
				}

				Calendar tempCal=Calendar.getInstance();
				Date cc = tempCal.getTime();
				String time = sdf.format(cc);

				String nodeid = id;
				List weblogicQueueList = (ArrayList)queueValue;
				String deleteSql = "delete from nms_weblogic_queue where nodeid='" +nodeid + "'";
				GathersqlListManager.Addsql(deleteSql);
				if (weblogicQueueList != null && weblogicQueueList.size() > 0) {
					for (int i = 0; i < weblogicQueueList.size(); i++){
						WeblogicQueue weblogicQueue = (WeblogicQueue) weblogicQueueList.get(i);
						try {
						    StringBuffer sql = new StringBuffer(500);
						    sql.append("insert into nms_weblogic_queue(nodeid, executeQueueRuntimeName, thdPoolRunExeThdIdleCnt, ");
						    sql.append("exeQueRunPendReqOldTime, exeQueRunPendReqCurCount, ");
						    sql.append("exeQueRunPendReqTotCount,  collecttime)values('");
						    sql.append(nodeid);
						    sql.append("','");
						    sql.append(weblogicQueue.getExecuteQueueRuntimeName());//executeQueueRuntimeName
						    sql.append("','");
						    sql.append(weblogicQueue.getThreadPoolRuntimeExecuteThreadIdleCount());//threadPoolRuntimeExecuteThreadIdleCount
						    sql.append("','");
					    	sql.append(weblogicQueue.getExecuteQueueRuntimePendingRequestOldestTime());//executeQueueRuntimePendingRequestOldestTime
						    sql.append("','");
						    sql.append(weblogicQueue.getExecuteQueueRuntimePendingRequestCurrentCount());//executeQueueRuntimePendingRequestCurrentCount
						    sql.append("','");
						    sql.append(weblogicQueue.getExecuteQueueRuntimePendingRequestTotalCount());//executeQueueRuntimePendingRequestTotalCount
						    if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
							    sql.append("','");
							    sql.append(time);//time
							    sql.append("')");
						    }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
							    sql.append("',");
							    sql.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");//time
							    sql.append(")");
						    }
//							System.out.println(sql.toString());
						    GathersqlListManager.Addsql(sql.toString());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			
			}
			//保存数据库
			//告警，只告警PING值
//			if(pingValue!=null){
//				NodeUtil nodeUtil = new NodeUtil();
//				NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(weblogicconf);
//				// 判断是否存在此告警指标
//				AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
//				List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(nodeDTO.getId() + "", nodeDTO.getType(), nodeDTO.getSubtype());
//				CheckEventUtil checkEventUtil = new CheckEventUtil();
//				for (int i = 0; i < list.size(); i++) {
//					AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode) list.get(i);
//					if ("ping".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
//						if (pingValue != null) {
//							checkEventUtil.checkEvent(nodeDTO,alarmIndicatorsNode, pingValue);
//						}
//					}
//				}
//			}
			weblogicsnmp=null;
			queueValue=null; 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returndata;
	}
}
