package com.afunms.initialize;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.send.SendAlarmUtil;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.common.util.CommonUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SyslogDefs;
import com.afunms.event.dao.CheckEventDao;
import com.afunms.event.dao.NetSyslogDao;
import com.afunms.event.dao.SyslogDao;
import com.afunms.event.model.CheckEvent;
import com.afunms.event.model.EventList;
import com.afunms.event.model.NetSyslog;
import com.afunms.event.model.Syslog;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.topology.dao.NetSyslogNodeAlarmKeyDao;
import com.afunms.topology.model.NetSyslogNodeAlarmKey;
/**
 * 
 * @descrition 处理syslog信息类（整理后）
 * @author wangxiangyong
 * @date Apr 22, 2013 11:04:26 AM
 */
public class ProcessSyslog {
	ArrayList nodeflist = null;

	static public int sport = 514;// 服务器端端口514；
	int processId;// 进程id
	String processName;// 进程名
	String processIdStr = "";// 进程id字符
	int facility;// 事件来源编码
	int priority;// 优先级编码
	String facilityName;// 事件来源名称
	String priorityName;// 优先级名称
	String hostname;// 主机名称
	String username;// 登陆用户
	Calendar timestamp;// 时间戳
	String message;// 得消息内容
	String ipaddress;// IP地址
	String businessid;// 业务ID
	boolean sign = false;
	int eventid;// 事件ID
	
	public void createTask(final DatagramPacket packet) {
		nodeflist = new ArrayList();
		InetAddress address;// 客户端IP地址；
		int cport;// 客户端端口；
		// SysLogger.info(new String(packet.getData()));
		cport = packet.getPort();// 获取客户端的端口；
		address = packet.getAddress(); // 获取客户端的IP地址
		String _sfc = new String(packet.getData());
		SysLogger.info(_sfc);
		hostname = address.getHostName();
		ipaddress = address.getHostAddress();
//		SysLogger.info("address========="+address);
//		SysLogger.info("ipaddress======="+ipaddress);
		Host host = null;
		try {
			host = (Host) PollingEngine.getInstance().getNodeByIP(ipaddress);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(host == null){
			if(ShareData.getAllipaliasVSip() != null){
					if(ShareData.getAllipaliasVSip().containsKey(ipaddress)){
						String _ipaddress = (String)ShareData.getAllipaliasVSip().get(ipaddress);
						ipaddress = _ipaddress;
						try {
							host = (Host) PollingEngine.getInstance().getNodeByIP(_ipaddress);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
			}
		}
		if (host != null) {
			hostname = host.getAlias();
			nodeflist.clear();
			//NetSyslogNodeRule noderule = null;
			//NetSyslogNodeRuleDao nodeRuleDao = new NetSyslogNodeRuleDao();
//			try {
//				//noderule = (NetSyslogNodeRule) nodeRuleDao.findByIpaddress(ipaddress);
//			} catch (Exception e) {
//				e.printStackTrace();
//			} finally {
//				nodeRuleDao.close();
//			}
			String noderule="";
			if(null!=ShareData.getSyslogruleNode() && ShareData.getSyslogruleNode().containsKey(host.getId()+""))
			{
				
				noderule=(String)ShareData.getSyslogruleNode().get(host.getId()+"");
			}
			
			if (noderule == null || noderule.trim().length()==0) {
				sign = false;
			} else {
				//String nodefacility = noderule.getFacility();
				if(noderule != null && noderule.trim().length()>0){
					String[] nodefacilitys = noderule.split(",");
					if (nodefacilitys != null && nodefacilitys.length > 0) {
						for (int i = 0; i < nodefacilitys.length; i++) {
							nodeflist.add(nodefacilitys[i]);
						}
					}
				}

				sign = true;
			}
			String bids = "," + host.getBid();
			businessid = bids;
			// SysLogger.info(packet.toString()+"&&&&");
			String sfc = new String(packet.getData());
			SysLogger.info("============================1");
			SysLogger.info(sfc);
			SysLogger.info("============================2");
			if (host.getCategory() == 1 || host.getCategory() == 2 || host.getCategory() == 3 || host.getCategory() == 7 || host.getCategory() == 8) {
				// 网络设备的SYSLOG
				this.processNetMessage(sfc.trim(), host.getCategory());
			} else {
				this.processMessage(sfc.trim());
			}
		}

	}
	public synchronized void processNetMessage(String message, int category) {
		//SysLogger.info("####################2222==="+message);
		int lbIdx = message.indexOf('<');
		int rbIdx = message.indexOf('>');
		// 如果无优先级，则消息格式不合法
		if (lbIdx < 0 || rbIdx < 0 || lbIdx >= (rbIdx - 1)) {
			System.err.println("BAD MSG {" + message + "}");
			return;
		}
		//SysLogger.info("####################333==="+message);
		// 是否优先级是合法数字
		int priCode = 0;
		String priStr = message.substring(lbIdx + 1, rbIdx);
		try {
			priCode = Integer.parseInt(priStr);
		} catch (NumberFormatException ex) {
			// SysLogger.info(ex.getMessage());
			return;
		}
		// 得事件来源和事件等级
		int facility = SyslogDefs.extractFacility(priCode);
		int priority = SyslogDefs.extractPriority(priCode);
		if (sign && nodeflist.contains(priority + "")) {
			// SysLogger.info("##################"+priority);
			// 得消息内容
			timestamp = Calendar.getInstance();
			message = message.substring(rbIdx + 1, (message.length()));
			NetSyslog syslog = new NetSyslog();
			syslog.setFacility(facility);
			syslog.setPriority(priority);
			syslog.setFacilityName(SyslogDefs.getFacilityName(facility));
			syslog.setPriorityName(SyslogDefs.getPriorityName(priority));
			syslog.setRecordtime(timestamp);
			syslog.setHostname(hostname);
			syslog.setMessage(message);
			syslog.setIpaddress(ipaddress);
			syslog.setBusinessid(businessid);
			syslog.setCategory(category);
			NetSyslogDao sdao = new NetSyslogDao();
			try {
				sdao.save(syslog);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				sdao.close();
			}
			// 产生syslog告警
			createSyslogAlarm(message);
		}

		SimpleDateFormat dfYear = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		timestamp = Calendar.getInstance();
		Date cc = timestamp.getTime();
		String time = dfYear.format(cc);
		// 执行保存
		try {
			
			int tt = 0;


		} catch (Exception e) {
			// SysLogger.info(e.getMessage());
			SysLogger.info("保存Syslog信息出错，可能数据库没有启动！");
			e.printStackTrace();
		} finally {
			// db.dbClose();
		}
	}

	public synchronized void processMessage(String message) {
		int lbIdx = message.indexOf('<');
		int rbIdx = message.indexOf('>');
		// 如果无优先级，则消息格式不合法
		if (lbIdx < 0 || rbIdx < 0 || lbIdx >= (rbIdx - 1)) {
			System.err.println("BAD MSG {" + message + "}");
			return;
		}

		// 是否优先级是合法数字
		int priCode = 0;
		String priStr = message.substring(lbIdx + 1, rbIdx);
		try {
			priCode = Integer.parseInt(priStr);
		} catch (NumberFormatException ex) {
			ex.printStackTrace();
			// SysLogger.info(ex.getMessage());
			return;
		}
		// 得事件来源和事件等级

		int facility = SyslogDefs.extractFacility(priCode);
		int priority = SyslogDefs.extractPriority(priCode);
		// 得消息内容
		if (sign && nodeflist.contains(priority + "")) {
			message = message.substring(rbIdx + 1, (message.length()));
			message = message.replaceAll("\"", "'");
			boolean stdMsg = true;
			String[] allMessages = new String[message.split(":").length];
			allMessages = message.split(":");
			try {
				if (allMessages[0].trim().equals("Security") || allMessages[0].trim().equals("W3SVC")) {
					processName = allMessages[0];
					processId = 0;
					eventid = Integer.parseInt(allMessages[1].trim());
					message = message.substring(allMessages[0].length() + allMessages[1].length() + 2);
					if (allMessages[0].trim().equals("Security")) {
						// 获得登陆用户信息
						username = allMessages[2].trim();

						if (message.indexOf("目标用户名") >= 0 && message.indexOf("目标域") >= 0 && message.indexOf("目标登录") >= 0) {
							// 判断登陆用户
							// System.out.println("--------"+message);
							String bname = message.substring(message.indexOf("目标用户名") + 6, message.indexOf("目标域")).trim();
							String dname = message.substring(message.indexOf("目标域") + 4, message.indexOf("目标登录")).trim();
							username = dname + "\\" + bname;
						} else {
							if (message.indexOf("用户名: 域: 登录 ID:") < 0) {
								if (message.indexOf("用户名:") >= 0 && message.indexOf("域:") >= 0 && message.indexOf("登录 ID:") >= 0) {
									String bname = message.substring(message.indexOf("用户名:") + 4, message.indexOf("域:")).trim();
									String dname = message.substring(message.indexOf("域:") + 2, message.indexOf("登录 ID:")).trim();
									username = dname + "\\" + bname;
								}
							}
						}

					
					}

				} else {
					if (message.split(":").length == 3) {
						// windows的SYSLOG
						processName = allMessages[0];
						processId = Integer.parseInt(allMessages[1].trim());
						eventid = Integer.parseInt(allMessages[1].trim());
						message = allMessages[2];
					} else if (message.split(":").length == 5) {
						// AIX系统
						processName = allMessages[3];
						message = allMessages[4];
					} else if (message.split(":").length == 4) {
						//
						processName = allMessages[0];
						eventid = Integer.parseInt(allMessages[1].trim());
						processId = Integer.parseInt(allMessages[1].trim());

						message = allMessages[3];
					} else if (message.split(":").length == 6) {
						// AIX系统的SYSLOG
						String proc = allMessages[3];
						lbIdx = proc.indexOf('[');
						rbIdx = proc.indexOf(']');
						// 若为FTP,则不处理相关信息
						if (allMessages[4].trim().equals("ftp"))
							return;
						if (lbIdx > -1) {
							processName = proc.substring(0, lbIdx);
							processId = Integer.parseInt(proc.substring(lbIdx + 1, rbIdx));
							eventid = Integer.parseInt(proc.substring(lbIdx + 1, rbIdx));
						} else {
							processName = proc;
							processId = 0;
							eventid = 0;
						}
						message = allMessages[5];
					} else {

						// 针对多个":"的情况
						processName = allMessages[0];// 来源,即进程名称
						processId = Integer.parseInt(allMessages[1].trim());// 进程ID
						eventid = Integer.parseInt(allMessages[1].trim());// 事件ID
						if (allMessages.length >= 2) {
							for (int k = 2; k < allMessages.length; k++) {
								message = message + allMessages[k];
							}
						}
						// SysLogger.info("针对多个=====
						// "+allMessages.length+"===="+message);
					}
				}
			} catch (Exception ex) {
				// SysLogger.info(ex.getMessage());
			}
			timestamp = Calendar.getInstance();
			/*
			 * if ( message.length() < 16 ){ stdMsg = false; } else if (
			 * message.charAt(3) != ' ' || message.charAt(6) != ' ' ||
			 * message.charAt(9) != ':' || message.charAt(12) != ':' ||
			 * message.charAt(15) != ' ' ) { stdMsg = false; }
			 * 
			 * if ( ! stdMsg ) { try { timestamp = Calendar.getInstance(); }
			 * catch ( IllegalArgumentException ex ) { System.err.println(
			 * "ERROR INTERNAL DATE ERROR!" ); timestamp = null; } } else {
			 * //timestamp = message.substring( 0, 15 ); message =
			 * message.substring( 16 ); } lbIdx = message.indexOf( '[' ); rbIdx =
			 * message.indexOf( ']' ); int colonIdx = message.indexOf( ':' );
			 * int spaceIdx = message.indexOf( ' ' );
			 * 
			 * 
			 * if ( lbIdx < (rbIdx - 1) && colonIdx == (rbIdx + 1) && spaceIdx ==
			 * (colonIdx + 1) ) { processName = message.substring( 0, lbIdx );
			 * processIdStr = message.substring( lbIdx + 1, rbIdx ); message =
			 * message.substring( colonIdx + 2 );
			 * 
			 * try { processId = Integer.parseInt( processIdStr ); } catch (
			 * NumberFormatException ex ) { System.err.println ( "ERROR Bad
			 * process id '" + processIdStr + "'" ); processId = 0; } } else if (
			 * lbIdx < 0 && rbIdx < 0 && colonIdx > 0 && spaceIdx == (colonIdx +
			 * 1) ) { processName = message.substring( 0, colonIdx ); message =
			 * message.substring( colonIdx + 2 ); }
			 */
			SimpleDateFormat dfYear = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			timestamp = Calendar.getInstance();
			Date cc = timestamp.getTime();
			String time = dfYear.format(cc);

			// System.out.println(time+"接收到"+hostname+"一条Syslog信息");
			// 设备类型
			// String devicetype="switcher";

			/*
			 * DBOpration db=new DBOpration("ExecuteCollectSyslog.java"); try{
			 * Vector typevc=new Vector(); typevc=db.executeQueryVt("select
			 * devicetype from syslog_monitor_host where
			 * hostip='"+hostname+"'");
			 * //System.out.println("typevc.size()="+typevc.size());
			 * //如果是需要监控的设备则采集 if(typevc.size()<1) { return ; }else {
			 * devicetype=((Vector)typevc.elementAt(0)).elementAt(0).toString(); }
			 * 
			 * }catch(Exception e){ e.printStackTrace(); }
			 */

			Syslog syslog = new Syslog();
			syslog.setFacility(facility);
			syslog.setPriority(priority);
			syslog.setFacilityName(SyslogDefs.getFacilityName(facility));
			syslog.setPriorityName(SyslogDefs.getPriorityName(priority));
			syslog.setRecordtime(timestamp);
			syslog.setProcessid(processId);
			syslog.setProcessidstr(processIdStr);
			syslog.setProcessname(processName);
			syslog.setHostname(hostname);
			if (username != null && username.trim().length() > 0) {
				syslog.setUsername(username);
			} else
				syslog.setUsername(hostname);
			syslog.setMessage(message);
			syslog.setIpaddress(ipaddress);
			syslog.setEventid(eventid);

			// 执行保存
			SyslogDao sdao = null;
			try {
				sdao = new SyslogDao();
				sdao.createSyslogData(syslog);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				sdao.close();
			}

			NetSyslog _syslog = new NetSyslog();
			_syslog.setFacility(facility);
			_syslog.setPriority(priority);
			_syslog.setFacilityName(SyslogDefs.getFacilityName(facility));
			_syslog.setPriorityName(SyslogDefs.getPriorityName(priority));
			_syslog.setRecordtime(timestamp);
			_syslog.setHostname(hostname);
			_syslog.setMessage(message);
			_syslog.setIpaddress(ipaddress);
			_syslog.setBusinessid(businessid);
			_syslog.setCategory(4);
			NetSyslogDao _sdao = new NetSyslogDao();
			try {
				_sdao.save(_syslog);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				_sdao.close();
			}

			// 产生syslog告警
			createSyslogAlarm(message);
		}
		try {
			/*
			 * //采集来的设备告警信息插入数据库 syslogManager.createSyslogData(syslog);
			 * 
			 * Equipment equipment = equipmanager.getByip(ipaddress);
			 * //判断该级别的SYSLOG是否已经设置为告警阀值 //Syslogconf syslogconf =
			 * (Syslogconf)syslogconfManager.getByip(ipaddress);
			 * 
			 * 
			 * 
			 * 
			 * Syslogconf syslogconf =
			 * (Syslogconf)ShareData.getAllsyslogconfdata().get(ipaddress);
			 * if(syslogconf != null){ if(syslogconf.getLimenvalues() != null){
			 * String[] levels = syslogconf.getLimenvalues().split(",");
			 * if(levels != null && levels.length>0){ for(int i=0;i<levels.length;i++){
			 * if(levels[i].equalsIgnoreCase(syslog.getPriorityname().toLowerCase())){
			 * //若已经设置的告警阀值跟当前采集的SYSLOG级别相等,则告警
			 * 
			 * Smscontent smscontent = new Smscontent(); //String time1 =
			 * sdf.format(date.getTime());
			 * smscontent.setMessage(time+"&"+equipment.getEquipname()+"&"+ipaddress+"&"+syslog.getPriorityname()+":"+message+"&level=2");
			 * //发送短信 Vector tosend = new Vector(); tosend.add(smscontent);
			 * smsmanager.sendSmscontent(tosend);
			 *  } } } } }
			 */
			/*
			 * if(syslog.getPriorityname().equals("")){
			 *  }
			 */
			int tt = 0;

		} catch (Exception e) {
			SysLogger.info("保存Syslog信息出错，可能数据库没有启动！");
			e.printStackTrace();
		} finally {
			// db.dbClose();
		}
	}

	/**
	 * syslog告警判断
	 * 
	 * @param message
	 *            syslog信息
	 */
	private void createSyslogAlarm(String message) {
		// ###########################判断是否要产生syslog告警 根据关键字告警过滤
		// START######################
		Host host = null;
		try {
			host = (Host) PollingEngine.getInstance().getNodeByIP(ipaddress);
		} catch (Exception e) {
			e.printStackTrace();
		}
		NetSyslogNodeAlarmKeyDao netSyslogNodeAlarmKeyDao = new NetSyslogNodeAlarmKeyDao();
		boolean containsKeyword = false;// 是否包含关键字
		int alarmLevel = 0;
		int tempLevel = 0;
		List netSyslogNodeAlarmList = null;
		try {
			netSyslogNodeAlarmList = (ArrayList) netSyslogNodeAlarmKeyDao.findByCondition(" where nodeid = '" + host.getId() + "'");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			netSyslogNodeAlarmKeyDao.close();
		}
		NetSyslogNodeAlarmKey netSyslogNodeAlarmKey = null;
		// 有设定syslog关键字，则判定syslog信息是否包含有关的关键字
		for (int i = 0; i < netSyslogNodeAlarmList.size(); i++) {
			netSyslogNodeAlarmKey = (NetSyslogNodeAlarmKey) netSyslogNodeAlarmList.get(i);
			if (netSyslogNodeAlarmKey != null) {
				String keywords = netSyslogNodeAlarmKey.getKeywords();
				if (keywords != null && !keywords.equals("") && message.contains(keywords)) {
					containsKeyword = true;
					tempLevel = Integer.parseInt(netSyslogNodeAlarmKey.getLevel());
					if (alarmLevel < tempLevel) {// 取告警级别中较大的一个
						alarmLevel = tempLevel;
					}
				}
			}
		}
		
		// 如果包含关键字 则产生syslog告警
		if (containsKeyword) {
			// 产生syslog告警
			NodeUtil nodeUtil = new NodeUtil();
			NodeDTO nodedto = nodeUtil.creatNodeDTOByNode(host);
			CheckEvent checkEvent = new CheckEvent();
			checkEvent.setNodeid(String.valueOf(host.getId()));
			checkEvent.setIndicatorsName("syslog");
			checkEvent.setType(nodedto.getType());
			checkEvent.setSubtype(nodedto.getSubtype());
			checkEvent.setSindex("");
			checkEvent.setThevalue("");
			checkEvent.setContent(message);
			checkEvent.setCollecttime(CommonUtil.getDateAndTime());
			checkEvent.setAlarmlevel(alarmLevel);
			// 将该告警对比信息放到内存系统里,最终需要放到数据库表里
			CheckEventDao checkeventdao = new CheckEventDao();
			try {
				checkeventdao.save(checkEvent);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				checkeventdao.close();
			}
			host.setStatus(alarmLevel);
			host.setAlarmlevel(alarmLevel);
			EventList eventlist = new EventList();
			eventlist.setLevel1(alarmLevel);
			eventlist.setEventtype("syslog");
			eventlist.setEventlocation(host.getAlias() + "(" + host.getIpAddress() + ")");
			eventlist.setLevel1(alarmLevel);
			eventlist.setManagesign(0);
			eventlist.setBak("");
			eventlist.setRecordtime(Calendar.getInstance());
			eventlist.setReportman("系统轮询");
			eventlist.setBusinessid(host.getBid());
			eventlist.setNodeid(host.getId());
			eventlist.setOid(0);
			eventlist.setSubtype(nodedto.getSubtype());
			eventlist.setSubentity("syslog");
			eventlist.setContent(message);
			
			
			
			AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(host.getId() + "", "net", null, "syslog");
			SysLogger.info("list size=============="+list.size());
			if(list != null && list.size()>0){
				for(int i=0;i<list.size();i++){
					SysLogger.info("===========================1111111112");
					AlarmIndicatorsNode nm = (AlarmIndicatorsNode) list.get(i);
					SendAlarmUtil alarmUtil = new SendAlarmUtil();
		    		try{
		    		
		    			alarmUtil.sendAlarm(checkEvent, eventlist, nm);
		    		}catch(Exception e){
		    			e.printStackTrace();
		    		}
				}
			}
			
			// 创建 eventList 
    		//EventList eventList = createEvent(eventtype, eventlocation, bid, content, level1, subtype, subentity, ipaddress, objid);
    		
			
			
//			AlarmWayDetail alarmWayDetail = null;
//			SendPageAlarm sendPageAlarm = new SendPageAlarm();
//			sendPageAlarm.sendAlarm(eventlist, alarmWayDetail);
		}
		// ###########################判断是否要产生syslog告警 根据关键字告警过滤
		// END######################
	}
}
