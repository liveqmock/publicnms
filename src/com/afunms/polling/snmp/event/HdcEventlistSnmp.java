package com.afunms.polling.snmp.event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmConstant;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.event.model.CheckEvent;
import com.afunms.event.model.EventList;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.HdcMessage;
import com.afunms.polling.om.Interfacecollectdata;
import com.afunms.topology.model.HostNode;
import com.gatherdb.GathersqlListManager;

public class HdcEventlistSnmp extends SnmpMonitor{

	public HdcEventlistSnmp() {
	}

	public void collectData(Node node, MonitoredItem item) {
	}

	public void collectData(HostNode node) {
	}

	public void CreateResultTosql(Hashtable dataresult, Host node) {
		// 处理hdc—sys-info
		if (dataresult != null && dataresult.size() > 0) {
			Vector sysInfoVector = null;
			HdcMessage hdcVo =null;
			NodeDTO nodeDTO = null;
			String ip = null;
			Interfacecollectdata vo = null;
			Calendar tempCal = null;
			Date cc = null;
			String time = null;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			NodeUtil nodeUtil = new NodeUtil();
			nodeDTO = nodeUtil.creatNodeDTOByNode(node);
			String hendsql = "insert into hdc_eventlist (eventListIndexSerialNumber,eventListNickname,eventListIndexRecordNo,eventListREFCODE,eventListDate,eventListTime,eventListDescription,nodeid) values(";
			String endsql = "')";
			String deleteSql = "delete from hdc_eventlist where nodeid='"
					+ node.getId() + "'";
			sysInfoVector = (Vector) dataresult.get("eventlist");
			Vector list = new Vector();
			if (sysInfoVector != null && sysInfoVector.size() > 0) {
				for (int i = 0; i < sysInfoVector.size(); i++) {
					hdcVo = (HdcMessage) sysInfoVector.elementAt(i);
					StringBuffer sbuffer = new StringBuffer(150);
					sbuffer.append(hendsql);
					sbuffer.append("'").append(hdcVo.getEventListIndexSerialNumber())
							.append("',");
					sbuffer.append("'").append(hdcVo.getEventListNickname())
							.append("',");
					sbuffer.append("'").append(hdcVo.getEventListIndexRecordNo()).append(
							"',");
					sbuffer.append("'").append(hdcVo.getEventListREFCODE()).append(
							"',");
					sbuffer.append("'").append(hdcVo.getEventListDate()).append(
							"',");
					sbuffer.append("'").append(hdcVo.getEventListTime()).append(
							"',");
					sbuffer.append("'").append(hdcVo.getEventListDescription()).append(
							"',");
					sbuffer.append("'").append(node.getId());
					sbuffer.append(endsql);
					list.add(sbuffer.toString());
					sbuffer = null;
				}
				GathersqlListManager.AdddateTempsql(deleteSql, list);
				list = null;
			}
		}
	}
	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash = new Hashtable();
		Vector eventlist = new Vector();
		HdcMessage hdcMessage;
		Host node = (Host) PollingEngine.getInstance().getNodeByID(
				Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if (node == null)
			return null;
		if (node.getIpAddress().equals(""))
			return null;
		try {
			Calendar date = Calendar.getInstance();
			String temp = "0";
			String[][] valueArray = null;
			String[] oids = new String[] { ".1.3.6.1.4.1.116.5.11.4.1.1.8.1.1",// eventListIndexSerialNumber
					// 索引号
					".1.3.6.1.4.1.116.5.11.4.1.1.8.1.2",// eventListNickname
					// 缺陷名称
					".1.3.6.1.4.1.116.5.11.4.1.1.8.1.3",// eventListIndexRecordNo
					// 事件记录号
					".1.3.6.1.4.1.116.5.11.4.1.1.8.1.4",// eventListREFCODE
					// 记录编码
					".1.3.6.1.4.1.116.5.11.4.1.1.8.1.5",// eventListDate
					// 日期
					".1.3.6.1.4.1.116.5.11.4.1.1.8.1.6",// eventListTime
					// 时间
					".1.3.6.1.4.1.116.5.11.4.1.1.8.1.7",// eventListDescription
					// 事件描述
			};
			//getTableData
//			valueArray = SnmpUtils.getTableData(node
//														.getIpAddress(), node
//															.getCommunity(), oids, node
//																.getSnmpversion(), 3, 1000 * 30);
			valueArray = SnmpUtils.getTableData(node.getIpAddress(),node.getCommunity(),oids,node.getSnmpversion(),
   		  			node.getSecuritylevel(),node.getSecurityName(),node.getV3_ap(),node.getAuthpassphrase(),node.getV3_privacy(),node.getPrivacyPassphrase(),3,1000*30);
			if (valueArray != null) {
				for (int i = 0; i < valueArray.length; i++) {
					String eventListIndexSerialNumber = valueArray[i][0];
					String eventListNickname = valueArray[i][1];
					String eventListIndexRecordNo = valueArray[i][2];
					String eventListREFCODE = valueArray[i][3];
					String eventListDate = valueArray[i][4];
					String eventListTime = valueArray[i][5];
					String eventListDescription = valueArray[i][6];
					String dataTime = eventListDate+eventListTime;
					String data_time = dataTime.replace("-", "/");
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
					String now_date_time = df.format(new Date());// new Date()为获取当前系统时间
				    int flag = 0;
					if(eventListDate != null && eventListTime != null){
						 flag = computeDateTime(now_date_time, data_time);
					}
//					if (time_flag == 0 || time_flag > 10) {
//						String content = "LSF-" + "(" + ipaddress + ").log "
//								+ " LSF日志文件采集时间超时,预定时间为：10分钟";
//						String name = host.getId() + ":host:proce:Lsflog";
//						this.check(host, content, name);
//					}
//					int time_flag = new LsfClassUtil().computeDateTime(rm_time,
//							collecttime);
					if (flag < 10) {
						String content = "hdc存储" + "(" + node.getIpAddress() + ") "
						+ eventListDescription;
						String name = node.getId() + ":storage:trap:"+eventListDescription;
						this.check(node, content, name);
					}
					
					hdcMessage = new HdcMessage();
					hdcMessage.setEventListDate(eventListDate);
					hdcMessage.setEventListDescription(eventListDescription);
					hdcMessage.setEventListIndexRecordNo(eventListIndexRecordNo);
					hdcMessage.setEventListIndexSerialNumber(eventListIndexSerialNumber);
					hdcMessage.setEventListNickname(eventListNickname);
					hdcMessage.setEventListREFCODE(eventListREFCODE);
					hdcMessage.setEventListTime(eventListTime);
//					hdcMessage.setEventListDate("2012-12-21");
//					hdcMessage.setEventListDescription("事件告警测试");
//					hdcMessage.setEventListIndexRecordNo("1234567");
//					hdcMessage.setEventListIndexSerialNumber("1");
//					hdcMessage.setEventListNickname("123");
//					hdcMessage.setEventListREFCODE("456");
//					hdcMessage.setEventListTime("12:30:22");
					eventlist.addElement(hdcMessage);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		 if(!(ShareData.getSharedata().containsKey(node.getIpAddress())))
			{
				Hashtable ipAllData = new Hashtable();
				if(ipAllData == null)ipAllData = new Hashtable();
				if(eventlist != null && eventlist.size()>0)ipAllData.put("eventlist",eventlist);
			    ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
			}else
			 {
				if(eventlist != null && eventlist.size()>0)((Hashtable)ShareData.getSharedata().get(node.getIpAddress())).put("eventlist",eventlist);
			 }
		
		
		returnHash.put("eventlist", eventlist);
		// 把采集结果生成sql
		this.CreateResultTosql(returnHash, node);
		return returnHash;
	}

	/**
	 * @param host
	 *            节点对象
	 * @param Content
	 *            告警的内容 #设备ip：192.168.0.1:（内容）LSF采集文件超时
	 * @param eventname
	 *            （nodeid：host：proce：procename） 58：host：proce：Lsflog
	 */
	public void check(Host host, String Content, String eventname) {
		NodeUtil nodeutil = new NodeUtil();
		String subtype = nodeutil.creatNodeDTOByHost(host).getSubtype();
		try {
			EventList eventlist = new EventList();
			eventlist.setEventtype("poll");
			eventlist.setEventlocation(host.getSysLocation());
			eventlist.setContent(Content);
			eventlist.setLevel1(1);
			eventlist.setManagesign(0);
			eventlist.setBak("");
			eventlist.setRecordtime(Calendar.getInstance());
			eventlist.setReportman("系统轮询");
			eventlist.setBusinessid(host.getBid());
			eventlist.setNodeid(host.getId());
			eventlist.setOid(0);
			eventlist.setSubtype(subtype);
			eventlist.setSubentity("proc");
			
			
			AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(
					String.valueOf(host.getId()), AlarmConstant.TYPE_STORAGE,
					"hdc2980", "traperrop");
//			for (int z = 0; z < list.size(); z++) {
//				// System.out.println("========阀值个数==="+list.size());
//				AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode) list
//						.get(z);
//				CheckEvent checkevent = new CheckEvent();
//				checkevent.setAlarmlevel(1);
//				checkevent.setName(eventname);
//				//SendAlarmUtil sendAlarmUtil = new SendAlarmUtil();
//				// 发送告警
//				//sendAlarmUtil.sendAlarmNoIndicatorOther(checkevent, eventlist,
//				//		alarmIndicatorsnode);//
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public int computeDateTime(String str_1,String str_2){
		int resultTime = 0;
//		System.out.println(str_1+"###########################################"+str_2);
		try {
			if(str_1!=null && str_2!=null){
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//				System.out.println(format+"###########################################"+format2);
//				System.out.println(format.parse(str_2.replaceAll("\n", "")).getTime()+"###########################################"+format2.parse(str_1.replaceAll("\n", "")).getTime());
				long result = (
							(format.parse(str_2.replaceAll("\n", "")).getTime())
							- 
							(format2.parse(str_1.replaceAll("\n", "")).getTime())
							)/60000;
					resultTime = new Long(result).intValue();
		        }
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return resultTime;
	}
}