package com.afunms.polling.snmp.weblogic;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import com.afunms.application.dao.WeblogicConfigDao;
import com.afunms.application.model.WeblogicConfig;
import com.afunms.application.weblogicmonitor.WeblogicServlet;
import com.afunms.application.weblogicmonitor.WeblogicSnmp;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SystemConstant;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.gatherdb.GathersqlListManager;

/**
 * weblogic servlet 采集
 * 
 * @author yangjun 2013/3/18
 * 
 */
public class ServletSnmp extends SnmpMonitor {

	public ServletSnmp() {
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
			List servletValue = new ArrayList();
			WeblogicSnmp weblogicsnmp=null;
			try {
				weblogicsnmp = new WeblogicSnmp(weblogicconf.getIpAddress(),weblogicconf.getCommunity(),weblogicconf.getPortnum());
				servletValue = weblogicsnmp.collectServletData();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(servletValue!=null){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				returndata.put("servletValue", servletValue);
				if (!(ShareData.getWeblogicdata().containsKey(weblogicconf.getIpAddress()))) {
					ShareData.getWeblogicdata().put(weblogicconf.getIpAddress(), returndata);
				} else {
					Hashtable hash = (Hashtable) ShareData.getWeblogicdata().get(weblogicconf.getIpAddress());
					hash.put("servletValue", returndata.get("servletValue"));
				}

				Calendar tempCal=Calendar.getInstance();
				Date cc = tempCal.getTime();
				String time = sdf.format(cc);

				String nodeid = id;
				List servletValuesList = (ArrayList)servletValue;
				String deleteSql = "delete from nms_weblogic_servlet where nodeid='" +nodeid + "'";
				GathersqlListManager.Addsql(deleteSql);
				if (servletValuesList != null && servletValuesList.size() > 0) {
					for (int i = 0; i < servletValuesList.size(); i++){
						WeblogicServlet weblogicServlet = (WeblogicServlet) servletValuesList.get(i);
						try {
						    StringBuffer sql = new StringBuffer(500);
						    sql.append("insert into nms_weblogic_servlet(nodeid, RunType, RunName,RunServletName, ");
						    sql.append("RunReloadTotalCnt, RunInvoTotCnt, RunPoolMaxCapacity, RunExecTimeTotal,");
						    sql.append("RunExecTimeHigh, RunExecTimeLow, RunExecTimeAvg, RunURL, collecttime)values('");
						    sql.append(nodeid);
						    sql.append("','");
						    sql.append(weblogicServlet.getServletRuntimeType());//servletRuntimeType
						    sql.append("','");
						    sql.append(weblogicServlet.getServletRuntimeName());//servletRuntimeName
						    sql.append("','");
						    sql.append(weblogicServlet.getServletRuntimeServletName());//servletRuntimeServletName
						    sql.append("','");
						    sql.append(weblogicServlet.getServletRuntimeReloadTotalCount());//servletRuntimeReloadTotalCount
						    sql.append("','");
						    sql.append(weblogicServlet.getServletRuntimeInvocationTotalCount());//servletRuntimeInvocationTotalCount
						    sql.append("','");
						    sql.append(weblogicServlet.getServletRuntimePoolMaxCapacity());//servletRuntimePoolMaxCapacity
						    sql.append("','");
						    sql.append(weblogicServlet.getServletRuntimeExecutionTimeTotal());//servletRuntimeExecutionTimeTotal
						    sql.append("','");
						    sql.append(weblogicServlet.getServletRuntimeExecutionTimeHigh());//servletRuntimeExecutionTimeHigh
						    sql.append("','");
						    sql.append(weblogicServlet.getServletRuntimeExecutionTimeLow());//servletRuntimeExecutionTimeLow
						    sql.append("','");
						    sql.append(weblogicServlet.getServletRuntimeExecutionTimeAverage());//servletRuntimeExecutionTimeAverage
						    sql.append("','");
						    sql.append(weblogicServlet.getServletRuntimeURL());//servletRuntimeURL
						    sql.append("',");
						    if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
						    	sql.append("'");
						    	sql.append(time);//time
						    	sql.append("'");
						    }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
						    	sql.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");//time
						    }
						    sql.append(")");
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
			servletValue=null; 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returndata;
	}
}
