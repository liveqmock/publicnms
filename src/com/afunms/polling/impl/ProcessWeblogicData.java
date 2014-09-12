package com.afunms.polling.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import com.afunms.application.model.WeblogicConfig;
import com.afunms.application.weblogicmonitor.WeblogicHeap;
import com.afunms.application.weblogicmonitor.WeblogicJdbc;
import com.afunms.application.weblogicmonitor.WeblogicNormal;
import com.afunms.application.weblogicmonitor.WeblogicQueue;
import com.afunms.application.weblogicmonitor.WeblogicServer;
import com.afunms.application.weblogicmonitor.WeblogicServlet;
import com.afunms.application.weblogicmonitor.WeblogicWeb;
import com.afunms.common.util.SystemConstant;
import com.afunms.util.DataGate;
/**
 * @author HONGLI  Mar 3, 2011
 */
public class ProcessWeblogicData {
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * 保存weblogic的信息
	 * @param weblogicConfigs   weblogicConfig的集合
	 * @param weblogicdatas  weblogic的数据集合
	 */
	public void saveWeblogicData(List weblogicConfigs, Hashtable weblogicdatas) {
		if(weblogicConfigs == null || weblogicConfigs.size() == 0 || weblogicdatas == null || weblogicdatas.isEmpty()){
			return ;
		}
		Connection conn = null;
		java.sql.Statement stmt = null;
		try {
			conn = DataGate.getCon();
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			//normalValue
			Hashtable normalValues = new Hashtable();
			//queueValue 队列
			Hashtable queueValues = new Hashtable();
			//jdbcValue
			Hashtable jdbcValues = new Hashtable();
			//webappValue
			Hashtable webappValues = new Hashtable();
			//heapValue堆栈信息
			Hashtable heapValues = new Hashtable();
			//serverValue服务信息
			Hashtable serverValues = new Hashtable();
			//servlet信息
			Hashtable servletValues = new Hashtable();
			//log信息
			Hashtable logValues = new Hashtable();
			
			for(int i=0; i<weblogicConfigs.size(); i++){
				WeblogicConfig weblogicConfig = (WeblogicConfig)weblogicConfigs.get(i);
				String nodeid = weblogicConfig.getId()+"";
				//取出单个IP对应的weblogic信息
				Hashtable weblogicData = (Hashtable)weblogicdatas.get(weblogicConfig.getIpAddress());
				if(weblogicData.containsKey("normalValue")){
					List normalValue = (ArrayList)weblogicData.get("normalValue");
					if(normalValue != null && normalValue.size() > 0){
						normalValues.put(nodeid, normalValue);
					}
				}
				if(weblogicData.containsKey("queueValue")){
					List queueValue = (ArrayList)weblogicData.get("queueValue");
					if(queueValue != null && queueValue.size() > 0){
						queueValues.put(nodeid, queueValue);
					}
				}
				if(weblogicData.containsKey("jdbcValue")){
					List jdbcValue = (ArrayList)weblogicData.get("jdbcValue");
					if(jdbcValue != null && jdbcValue.size() > 0){
						jdbcValues.put(nodeid, jdbcValue);
					}
				}
				if(weblogicData.containsKey("webappValue")){
					List webappValue = (ArrayList)weblogicData.get("webappValue");
					if(webappValue != null && webappValue.size() > 0){
						webappValues.put(nodeid, webappValue);
					}
				}
				if(weblogicData.containsKey("heapValue")){
					List heapValue = (ArrayList)weblogicData.get("heapValue");
					if(heapValue != null && heapValue.size() > 0){
						heapValues.put(nodeid, heapValue);
					}
				}
				if(weblogicData.containsKey("serverValue")){
					List serverValue = (ArrayList)weblogicData.get("serverValue");
					if(serverValue != null && serverValue.size() > 0){
						serverValues.put(nodeid, serverValue);
					}
				}
				if(weblogicData.containsKey("servletValue")){
					List servletValue = (ArrayList)weblogicData.get("servletValue");
					if(servletValue != null && servletValue.size() > 0){
						servletValues.put(nodeid, servletValue);
					}
				}
				if(weblogicData.containsKey("logValue")){
					List logValue = (ArrayList)weblogicData.get("logValue");
					if(logValue != null && logValue.size() > 0){
						logValues.put(nodeid, logValue);
					}
				}
			}
			//入库
			//处理队列信息入库  queueValues
			if(queueValues != null && queueValues.size()>0){
				Enumeration tempEnumeration = queueValues.keys(); 
				Calendar tempCal=Calendar.getInstance();
				Date cc = tempCal.getTime();
				String time = sdf.format(cc);
				while(tempEnumeration.hasMoreElements()){
					String nodeid = (String)tempEnumeration.nextElement();
					List weblogicQueueList = (ArrayList)queueValues.get(nodeid);
					String deleteSql = "delete from nms_weblogic_queue where nodeid='" +nodeid + "'";
					stmt.addBatch(deleteSql);
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
//								System.out.println(sql.toString());
							    stmt.addBatch(sql.toString());						    
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
			
			//处理队列信息入库  jdbcValues
			if(jdbcValues != null && jdbcValues.size()>0){
				Enumeration tempEnumeration = jdbcValues.keys(); 
				Calendar tempCal=Calendar.getInstance();
				Date cc = tempCal.getTime();
				String time = sdf.format(cc);
				while(tempEnumeration.hasMoreElements()){
					String nodeid = (String)tempEnumeration.nextElement();
					List jdbcValuesList = (ArrayList)jdbcValues.get(nodeid);
					String deleteSql = "delete from nms_weblogic_jdbc where nodeid='" +nodeid + "'";
					stmt.addBatch(deleteSql);
					if (jdbcValuesList != null && jdbcValuesList.size() > 0) {
						for (int i = 0; i < jdbcValuesList.size(); i++){
							WeblogicJdbc weblogicJdbc = (WeblogicJdbc) jdbcValuesList.get(i);
							try {
							    StringBuffer sql = new StringBuffer(500);
							    sql.append("insert into nms_weblogic_jdbc(nodeid, jdbcConnectionPoolName, ConPoolRunActConnsCurCount, ");
							    sql.append("ConPoolRunVerJDBCDriver, ConPoolRunMaxCapacity, ");
							    sql.append("ConPoolRunActConsAvgCount, ConPoolRunHighestNumAvai, collecttime)values('");
							    sql.append(nodeid);
							    sql.append("','");
							    sql.append(weblogicJdbc.getJdbcConnectionPoolName());//jdbcConnectionPoolName
							    sql.append("','");
							    sql.append(weblogicJdbc.getJdbcConnectionPoolRuntimeActiveConnectionsCurrentCount());//jdbcConnectionPoolRuntimeActiveConnectionsCurrentCount
							    sql.append("','");
							    sql.append(weblogicJdbc.getJdbcConnectionPoolRuntimeVersionJDBCDriver());//jdbcConnectionPoolRuntimeVersionJDBCDriver
							    sql.append("','");
							    sql.append(weblogicJdbc.getJdbcConnectionPoolRuntimeMaxCapacity());//jdbcConnectionPoolRuntimeMaxCapacity
							    sql.append("','");
							    sql.append(weblogicJdbc.getJdbcConnectionPoolRuntimeActiveConnectionsAverageCount());//jdbcConnectionPoolRuntimeActiveConnectionsAverageCount
							    sql.append("','");
							    sql.append(weblogicJdbc.getJdbcConnectionPoolRuntimeHighestNumAvailable());//jdbcConnectionPoolRuntimeHighestNumAvailable
							    sql.append("',");
							    if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
							    	sql.append("'");
							    	sql.append(time);//time
							    	sql.append("'");
							    }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
							    	sql.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");//time
							    }
							    sql.append(")");
//								System.out.println(sql.toString());
							    stmt.addBatch(sql.toString());						    
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
			
			//处理web服务信息入库  webappValues
			if(webappValues != null && webappValues.size()>0){
				Enumeration tempEnumeration = webappValues.keys(); 
				Calendar tempCal=Calendar.getInstance();
				Date cc = tempCal.getTime();
				String time = sdf.format(cc);
				while(tempEnumeration.hasMoreElements()){
					String nodeid = (String)tempEnumeration.nextElement();
					List webappValuesList = (ArrayList)webappValues.get(nodeid);
					String deleteSql = "delete from nms_weblogic_webapps where nodeid='" +nodeid + "'";
					stmt.addBatch(deleteSql);
					if (webappValuesList != null && webappValuesList.size() > 0) {
						for (int i = 0; i < webappValuesList.size(); i++){
							WeblogicWeb weblogicWeb = (WeblogicWeb) webappValuesList.get(i);
							try {
							    StringBuffer sql = new StringBuffer(500);
							    sql.append("insert into nms_weblogic_webapps(nodeid, CompRunComptName, CompRunStatus, ");
							    sql.append("CompRunOpenSessCurCount, CompRunOpenSessHighCount, ");
							    sql.append("CompRunSessOpenedTotCount, collecttime)values('");
							    sql.append(nodeid);
							    sql.append("','");
							    sql.append(weblogicWeb.getWebAppComponentRuntimeComponentName());//webAppComponentRuntimeComponentName
							    sql.append("','");
							    sql.append(weblogicWeb.getWebAppComponentRuntimeStatus());//webAppComponentRuntimeStatus
							    sql.append("','");
							    sql.append(weblogicWeb.getWebAppComponentRuntimeOpenSessionsCurrentCount());//webAppComponentRuntimeOpenSessionsCurrentCount
							    sql.append("','");
							    sql.append(weblogicWeb.getWebAppComponentRuntimeOpenSessionsHighCount());//webAppComponentRuntimeOpenSessionsHighCount
							    sql.append("','");
							    sql.append(weblogicWeb.getWebAppComponentRuntimeSessionsOpenedTotalCount());//webAppComponentRuntimeSessionsOpenedTotalCount
							    sql.append("',");
							    if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
							    	sql.append("'");
							    	sql.append(time);//time
							    	sql.append("'");
							    }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
							    	sql.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");//time
							    }
							    sql.append(")");
//								System.out.println(sql.toString());
							    stmt.addBatch(sql.toString());						    
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
			
			//处理队堆栈息入库  heapValue
			if(heapValues != null && heapValues.size()>0){
				Enumeration tempEnumeration = heapValues.keys(); 
				Calendar tempCal=Calendar.getInstance();
				Date cc = tempCal.getTime();
				String time = sdf.format(cc);
				while(tempEnumeration.hasMoreElements()){
					String nodeid = (String)tempEnumeration.nextElement();
					List heapValuesList = (ArrayList)heapValues.get(nodeid);
					String deleteSql = "delete from nms_weblogic_heap where nodeid='" +nodeid + "'";
					stmt.addBatch(deleteSql);
					if (heapValuesList != null && heapValuesList.size() > 0) {
						for (int i = 0; i < heapValuesList.size(); i++){
							WeblogicHeap weblogicHeap = (WeblogicHeap) heapValuesList.get(i);
							try {
							    StringBuffer sql = new StringBuffer(500);
							    sql.append("insert into nms_weblogic_heap(nodeid, jvmRuntimeName, jvmRuntimeHeapSizeCurrent, ");
							    sql.append("jvmRuntimeHeapFreeCurrent, collecttime)values('");
							    sql.append(nodeid);
							    sql.append("','");
							    sql.append(weblogicHeap.getJvmRuntimeName());//jvmRuntimeName
							    sql.append("','");
							    sql.append(weblogicHeap.getJvmRuntimeHeapSizeCurrent());//jvmRuntimeHeapSizeCurrent
							    sql.append("','");
							    sql.append(weblogicHeap.getJvmRuntimeHeapFreeCurrent());//jvmRuntimeHeapFreeCurrent
							    sql.append("',");
							    if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
							    	sql.append("'");
							    	sql.append(time);//time
							    	sql.append("'");
							    }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
							    	sql.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");//time
							    }
							    sql.append(")");
//								System.out.println(sql.toString());
							    stmt.addBatch(sql.toString());						    
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
			
			//处理服务信息入库  serverValues
			if(serverValues != null && serverValues.size()>0){
				Enumeration tempEnumeration = serverValues.keys(); 
				Calendar tempCal=Calendar.getInstance();
				Date cc = tempCal.getTime();
				String time = sdf.format(cc);
				while(tempEnumeration.hasMoreElements()){
					String nodeid = (String)tempEnumeration.nextElement();
					List serverValuesList = (ArrayList)serverValues.get(nodeid);
					String deleteSql = "delete from nms_weblogic_server where nodeid='" +nodeid + "'";
					stmt.addBatch(deleteSql);
					if (serverValuesList != null && serverValuesList.size() > 0) {
						for (int i = 0; i < serverValuesList.size(); i++){
							WeblogicServer weblogicServer = (WeblogicServer) serverValuesList.get(i);
							try {
							    StringBuffer sql = new StringBuffer(500);
							    sql.append("insert into nms_weblogic_server(nodeid, serverRuntimeName, serverRuntimeListenAddress, ");
							    sql.append("serverRuntimeListenPort, RunOpenSocketsCurCount, serverRuntimeState, ipaddress, collecttime)values('");
							    sql.append(nodeid);
							    sql.append("','");
							    sql.append(weblogicServer.getServerRuntimeName());//serverRuntimeName
							    sql.append("','");
							    sql.append(weblogicServer.getServerRuntimeListenAddress());//serverRuntimeListenAddress
							    sql.append("','");
							    sql.append(weblogicServer.getServerRuntimeListenPort());//serverRuntimeListenPort
							    sql.append("','");
							    sql.append(weblogicServer.getServerRuntimeOpenSocketsCurrentCount());//serverRuntimeOpenSocketsCurrentCount
							    sql.append("','");
							    sql.append(weblogicServer.getServerRuntimeState());//serverRuntimeState
							    sql.append("','");
							    sql.append(weblogicServer.getIpaddress());//ipaddress
							    sql.append("',");
							    if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
							    	sql.append("'");
							    	sql.append(time);//time
							    	sql.append("'");
							    }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
							    	sql.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");//time
							    }
							    sql.append(")");
//								System.out.println(sql.toString());
							    stmt.addBatch(sql.toString());						    
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
			//servletValues
			//处理服务信息入库  servletValues
			if(servletValues != null && servletValues.size()>0){
				Enumeration tempEnumeration = servletValues.keys(); 
				Calendar tempCal=Calendar.getInstance();
				Date cc = tempCal.getTime();
				String time = sdf.format(cc);
				while(tempEnumeration.hasMoreElements()){
					String nodeid = (String)tempEnumeration.nextElement();
					List servletValuesList = (ArrayList)servletValues.get(nodeid);
					String deleteSql = "delete from nms_weblogic_servlet where nodeid='" +nodeid + "'";
					stmt.addBatch(deleteSql);
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
//								System.out.println(sql.toString());
							    stmt.addBatch(sql.toString());						    
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
			
			//处理normal信息入库  logValues  如：domainName 
			if(normalValues != null && normalValues.size()>0){
				Enumeration tempEnumeration = normalValues.keys(); 
				Calendar tempCal=Calendar.getInstance();
				Date cc = tempCal.getTime();
				String time = sdf.format(cc).trim();
				while(tempEnumeration.hasMoreElements()){
					String nodeid = (String)tempEnumeration.nextElement();
					List logValuesList = (ArrayList)normalValues.get(nodeid);
					String deleteSql = "delete from nms_weblogic_normal where nodeid='" +nodeid + "'";
					stmt.addBatch(deleteSql);
					if (logValuesList != null && logValuesList.size() > 0) {
						for (int i = 0; i < logValuesList.size(); i++){
							WeblogicNormal weblogicNormal = (WeblogicNormal) logValuesList.get(i);
							try {
							    StringBuffer sql = new StringBuffer(100);
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
//								System.out.println(sql.toString());
							    stmt.addBatch(sql.toString());						    
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
			stmt.executeBatch();//批量插入
			conn.commit();
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally{
			if(stmt != null){
				try {
					stmt.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(conn != null){
				try {
					conn.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
	}
}
