package com.afunms.polling.snmp.weblogic;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.dao.WeblogicConfigDao;
import com.afunms.application.model.WeblogicConfig;
import com.afunms.application.weblogicmonitor.WeblogicServer;
import com.afunms.application.weblogicmonitor.WeblogicSnmp;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.polling.om.Pingcollectdata;

/**
 * weblogic ping 采集
 * 
 * @author yangjun 2013/3/18
 * 
 */
public class WeblogicPing extends SnmpMonitor {

	public WeblogicPing() {
	}

	@SuppressWarnings("unchecked")
	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicators) {
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
			String pingValue="0";
			List serverValue = new ArrayList();
			WeblogicSnmp weblogicsnmp=null;
			try {
				weblogicsnmp = new WeblogicSnmp(weblogicconf.getIpAddress(),weblogicconf.getCommunity(),weblogicconf.getPortnum());
				serverValue = weblogicsnmp.collectServerData();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			if(serverValue != null && serverValue.size()>0){
				int flag = 0;
     			for(int i=0;i<serverValue.size();i++){
     				WeblogicServer server = (WeblogicServer)serverValue.get(i);
     				if(!server.getServerRuntimeState().equalsIgnoreCase("RUNNING")){
     					flag++;
     				}
     			}
     			if(flag>0){
     				pingValue="0";
     			} else {
     				pingValue="100";
     			}
			}
			//保存数据库
			Pingcollectdata hostdata=new Pingcollectdata();
			hostdata.setIpaddress(weblogicconf.getIpAddress());
			Calendar date=Calendar.getInstance();
			hostdata.setCollecttime(date);
			hostdata.setCategory("WeblogicPing");
			hostdata.setEntity("Utilization");
			hostdata.setSubentity("ConnectUtilization");
			hostdata.setRestype("dynamic");
			hostdata.setUnit("%");
			hostdata.setThevalue(pingValue);
			WeblogicConfigDao weblogicconfigdao=new WeblogicConfigDao();
			try{
				weblogicconfigdao.createHostData(hostdata);	        								
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				weblogicconfigdao.close();
			}
			//告警，只告警PING值
			if(pingValue!=null){
				NodeUtil nodeUtil = new NodeUtil();
				NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(weblogicconf);
				// 判断是否存在此告警指标
				AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
				List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(nodeDTO.getId() + "", nodeDTO.getType(), nodeDTO.getSubtype());
				CheckEventUtil checkEventUtil = new CheckEventUtil();
				for (int i = 0; i < list.size(); i++) {
					AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode) list.get(i);
					if ("ping".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
						if (pingValue != null) {
							checkEventUtil.checkEvent(nodeDTO,alarmIndicatorsNode, pingValue);
						}
					}
				}
			}
			weblogicsnmp=null;
			serverValue=null; 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
