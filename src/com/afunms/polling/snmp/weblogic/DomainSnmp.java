package com.afunms.polling.snmp.weblogic;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import com.afunms.application.dao.WeblogicConfigDao;
import com.afunms.application.model.WeblogicConfig;
import com.afunms.application.weblogicmonitor.WeblogicNormal;
import com.afunms.application.weblogicmonitor.WeblogicSnmp;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SystemConstant;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.gatherdb.GathersqlListManager;

/**
 * was ping 采集
 * 
 * @author wupinlong 2012/10/27
 * 
 */
public class DomainSnmp extends SnmpMonitor {

	public DomainSnmp() {
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
			List normalValue = new ArrayList();
			WeblogicSnmp weblogicsnmp=null;
			try {
				weblogicsnmp = new WeblogicSnmp(weblogicconf.getIpAddress(),weblogicconf.getCommunity(),weblogicconf.getPortnum());
				normalValue = weblogicsnmp.collectNormalData();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(normalValue!=null){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				returndata.put("normalValue", normalValue);
				if (!(ShareData.getWeblogicdata().containsKey(weblogicconf.getIpAddress()))) {
					ShareData.getWeblogicdata().put(weblogicconf.getIpAddress(), returndata);
				} else {
					Hashtable hash = (Hashtable) ShareData.getWeblogicdata().get(weblogicconf.getIpAddress());
					hash.put("normalValue", returndata.get("normalValue"));
				}

				Calendar tempCal=Calendar.getInstance();
				Date cc = tempCal.getTime();
				String time = sdf.format(cc);

				String nodeid = id;
				List logValuesList = (ArrayList)normalValue;
				String deleteSql = "delete from nms_weblogic_normal where nodeid='" +nodeid + "'";
				GathersqlListManager.Addsql(deleteSql);
				if (logValuesList != null && logValuesList.size() > 0) {
					for (int i = 0; i < logValuesList.size(); i++){
						WeblogicNormal weblogicNormal = (WeblogicNormal) logValuesList.get(i);
						try {
						    StringBuffer sql = new StringBuffer(500);
						    sql.append("insert into nms_weblogic_normal(nodeid, domainName, domainActive,domainAdministrationPort, ");
						    sql.append("domainConfigurationVersion, collecttime)values('");
						    sql.append(nodeid);
						    sql.append("','");
						    sql.append(weblogicNormal.getDomainName());//domainName
						    sql.append("','");
						    sql.append(weblogicNormal.getDomainActive());//domainActive
						    sql.append("','");
						    sql.append(weblogicNormal.getDomainAdministrationPort());//domainAdministrationPort
						    sql.append("','");
						    sql.append(weblogicNormal.getDomainConfigurationVersion());//domainConfigurationVersion
							if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
								sql.append("','");
								sql.append(time);
								sql.append("')");
							}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
								sql.append("',");
								sql.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");
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
			normalValue=null; 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returndata;
	}
}
