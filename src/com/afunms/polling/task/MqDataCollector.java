package com.afunms.polling.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.dao.MQConfigDao;
import com.afunms.application.dao.MQchannelConfigDao;
import com.afunms.application.model.MQConfig;
import com.afunms.application.model.MQchannelConfig;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.om.Pingcollectdata;

public class MqDataCollector {
	public MqDataCollector() {

	}

	private static DecimalFormat df4 = new DecimalFormat("#.##");

	public void collect_Data(NodeGatherIndicators apacheIndicators) {
		String id = apacheIndicators.getNodeid();
		this.collectData(id);
	}

	public void collectData(String id) {

		Hashtable retHashtable = new Hashtable();
		MQConfig mqconf = null;
		String ipaddress = "";
		MQConfigDao configdao = null;
		try {
			configdao = new MQConfigDao();
			mqconf = (MQConfig) configdao.findByID(id);
			ipaddress = mqconf.getIpaddress();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			configdao.close();
		}

		StringBuffer fileContent = new StringBuffer();

		String collecttime = "";// 采集时间
		Hashtable basicInfoHashtable = new Hashtable(); // 基本信息
		List chstatusList = new ArrayList(); // 通道信息
		List localQueueList = new ArrayList(); // 本地队列信息
		List remoteQueueList = new ArrayList(); // 远程队列信息

		try {
			String filename = ResourceCenter.getInstance().getSysPath() + "linuxserver\\" + ipaddress + ".mq.log";
//			SysLogger.info(filename);
			// String filename =
			// "E:\\apache-tomcat-6.0.35\\webapps\\afunms\\linuxserver\\10.254.112.91.mq.log";

			// File file=new File(filename);
			// if(!file.exists()){
			// //文件不存在,则产生告警
			// try{
			// createFileNotExistSMS(mqconf);
			// }catch(Exception e){
			// e.printStackTrace();
			// }
			// return null;
			// }

			// file = null;
			FileInputStream fis = new FileInputStream(filename);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);
			String strLine = null;
			// 读入文件内容
			while ((strLine = br.readLine()) != null) {
				fileContent.append(strLine + "\n");
			}
			isr.close();
			fis.close();
			br.close();
			try {
				copyFile(mqconf.getIpaddress(), getMaxNum(mqconf.getIpaddress()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Pattern tmpPt = null;
		Matcher mr = null;

		SysLogger.info("----------------解析数据采集时间内容--创建监控项---------------------");
		tmpPt = Pattern.compile("(cmdbegin:collecttimestart)(.*)(cmdbegin:collecttimeend)", Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if (mr.find()) {
			collecttime = mr.group(2);
		}
		if (collecttime != null && collecttime.length() > 0) {
			collecttime = collecttime.trim();
		}
		// ----------------解析version内容--创建监控项---------------------
		String versionContent = "";
		tmpPt = Pattern.compile("(cmdbegin:versionstart)(.*)(cmdbegin:versionend)", Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if (mr.find()) {
			versionContent = mr.group(2);
		}
		String[] vmstat_LineArr = null;
		try {
			vmstat_LineArr = versionContent.split("\n");
			for (int i = 1; i < vmstat_LineArr.length; i++) {
				if (vmstat_LineArr[i].trim().indexOf("Name:") == 0) {
					String name = (vmstat_LineArr[i].trim().substring(vmstat_LineArr[i].trim().indexOf("Name:") + 5, vmstat_LineArr[i].trim().length())).trim();
					basicInfoHashtable.put("name", name);
				}
				if (vmstat_LineArr[i].trim().indexOf("Version:") == 0) {
					String version = (vmstat_LineArr[i].trim().substring(vmstat_LineArr[i].trim().indexOf("Version:") + 8, vmstat_LineArr[i].trim().length())).trim();
					basicInfoHashtable.put("version", version);
				}
				if (vmstat_LineArr[i].trim().indexOf("Platform:") == 0) {
					String platform = (vmstat_LineArr[i].trim().substring(vmstat_LineArr[i].trim().indexOf("Platform:") + 9, vmstat_LineArr[i].trim().length())).trim();
					basicInfoHashtable.put("platform", platform);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// ----------------解析qname内容--创建监控项---------------------
		String qnameContent = "";
		tmpPt = Pattern.compile("(cmdbegin:qnamestart)(.*)(cmdbegin:qnameend)", Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if (mr.find()) {
			qnameContent = mr.group(2);
		}
		String[] qname_LineArr = null;
		try {
			qname_LineArr = qnameContent.split("\n");
			for (int i = 1; i < qname_LineArr.length; i++) {
				if (qname_LineArr[i].trim().indexOf("QMNAME:") == 0) {
					String qmname = (qname_LineArr[i].trim().substring(qname_LineArr[i].trim().indexOf("QMNAME:") + 7, qname_LineArr[i].trim().length())).trim();
					basicInfoHashtable.put("qmname", qmname);
				}
				if (qname_LineArr[i].trim().indexOf("STATUS:") == 0) {
					String status = (qname_LineArr[i].trim().substring(qname_LineArr[i].trim().indexOf("STATUS:") + 7, qname_LineArr[i].trim().length())).trim();
					basicInfoHashtable.put("status", status);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// ----------------解析lsstatus内容--创建监控项---------------------
		String lsstatusContent = "";
		tmpPt = Pattern.compile("(cmdbegin:lsstatusstart)(.*)(cmdbegin:lsstatusend)", Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if (mr.find()) {
			lsstatusContent = mr.group(2);
		}
		String[] lsstatus_LineArr = null;
		try {
			lsstatus_LineArr = lsstatusContent.split("\n");
			for (int i = 1; i < lsstatus_LineArr.length; i++) {
				if (lsstatus_LineArr[i].trim().indexOf("LISTENER:") == 0) {
					String listener = (lsstatus_LineArr[i].trim().substring(lsstatus_LineArr[i].trim().indexOf("LISTENER:") + 9, lsstatus_LineArr[i].trim().length())).trim();
					basicInfoHashtable.put("listener", listener);
				}
				if (lsstatus_LineArr[i].trim().indexOf("BACKLOG:") == 0) {
					String backlog = (lsstatus_LineArr[i].trim().substring(lsstatus_LineArr[i].trim().indexOf("BACKLOG:") + 8, lsstatus_LineArr[i].trim().length())).trim();
					basicInfoHashtable.put("backlog", backlog);
				}
				if (lsstatus_LineArr[i].trim().indexOf("STATU:") == 0) {
					String statu = (lsstatus_LineArr[i].trim().substring(lsstatus_LineArr[i].trim().indexOf("STATU:") + 6, lsstatus_LineArr[i].trim().length())).trim();
					basicInfoHashtable.put("statu", statu);
				}
				if (lsstatus_LineArr[i].trim().indexOf("PORT:") == 0) {
					String port = (lsstatus_LineArr[i].trim().substring(lsstatus_LineArr[i].trim().indexOf("PORT:") + 5, lsstatus_LineArr[i].trim().length())).trim();
					basicInfoHashtable.put("port", port);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// ----------------解析通道内容--创建监控项---------------------
		String chstatusContent = "";
		tmpPt = Pattern.compile("(cmdbegin:chstatusstart)(.*)(cmdbegin:chstatusend)", Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if (mr.find()) {
			chstatusContent = mr.group(2);
		}
		String[] chstatus_LineArr = null;
		String[] vmstat_tmpData = null;
		try {
			chstatus_LineArr = chstatusContent.split("\n");
			List<String> chstatusnameList = new ArrayList<String>();// 通道名字相同标记
			for (int i = 1; i < chstatus_LineArr.length; i = i + 6) {
				String chstatusname = (chstatus_LineArr[i].trim().substring(chstatus_LineArr[i].trim().indexOf("chstatusname:") + 13, chstatus_LineArr[i].trim().length())).trim();
				vmstat_tmpData = chstatusname.split("\\s++");
				chstatusname = vmstat_tmpData[0];

				if (chstatusnameList.contains(chstatusname)) {
					// 如果通道名字相同，则跳过此次循环
					continue;
				} else {
					chstatusnameList.add(chstatusname);
				}

				String bufsrcvd = (chstatus_LineArr[i + 1].trim().substring(chstatus_LineArr[i + 1].trim().indexOf("BUFSRCVD:") + 9, chstatus_LineArr[i + 1].trim().length())).trim();
				vmstat_tmpData = bufsrcvd.split("\\s++");
				bufsrcvd = vmstat_tmpData[0];
				String bufssent = (chstatus_LineArr[i + 2].trim().substring(chstatus_LineArr[i + 2].trim().indexOf("BUFSSENT:") + 9, chstatus_LineArr[i + 2].trim().length())).trim();
				vmstat_tmpData = bufssent.split("\\s++");
				bufssent = vmstat_tmpData[0];
				String bytsrcvd = (chstatus_LineArr[i + 3].trim().substring(chstatus_LineArr[i + 3].trim().indexOf("BYTSRCVD:") + 9, chstatus_LineArr[i + 3].trim().length())).trim();
				vmstat_tmpData = bytsrcvd.split("\\s++");
				bytsrcvd = vmstat_tmpData[0];
				String bytssent = (chstatus_LineArr[i + 4].trim().substring(chstatus_LineArr[i + 4].trim().indexOf("BYTSSENT:") + 9, chstatus_LineArr[i + 4].trim().length())).trim();
				vmstat_tmpData = bytssent.split("\\s++");
				bytssent = vmstat_tmpData[0];
				String status = (chstatus_LineArr[i + 5].trim().substring(chstatus_LineArr[i + 5].trim().indexOf("STATUS:") + 7, chstatus_LineArr[i + 5].trim().length())).trim();
				vmstat_tmpData = status.split("\\s++");
				status = vmstat_tmpData[0];

				Hashtable tmpHashtable = new Hashtable();
				tmpHashtable.put("chstatusname", chstatusname);
				tmpHashtable.put("bufsrcvd", bufsrcvd);
				tmpHashtable.put("bufssent", bufssent);
				tmpHashtable.put("bytsrcvd", bytsrcvd);
				tmpHashtable.put("bytssent", bytssent);
				tmpHashtable.put("status", status);
				chstatusList.add(tmpHashtable);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// ----------------解析本地队列内容--创建监控项---------------------
		String localqueueContent = "";
		tmpPt = Pattern.compile("(cmdbegin:localqueuestart)(.*)(cmdbegin:localqueueend)", Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if (mr.find()) {
			localqueueContent = mr.group(2);
		}
		String[] localqueue_LineArr = null;
		try {
			localqueue_LineArr = localqueueContent.split("\n");
			for (int i = 1; i < localqueue_LineArr.length; i = i + 4) {
				String queue = (localqueue_LineArr[i].trim().substring(localqueue_LineArr[i].trim().indexOf("QUEUE:") + 6, localqueue_LineArr[i].trim().length())).trim();
				String curdepth = (localqueue_LineArr[i + 1].trim().substring(localqueue_LineArr[i + 1].trim().indexOf("CURDEPTH:") + 9, localqueue_LineArr[i + 1].trim().length())).trim();
				// 不是本地队列，循环下一条
				if (curdepth.indexOf(".") >= 0) {
					continue;
				}
				String maxdepth = (localqueue_LineArr[i + 2].trim().substring(localqueue_LineArr[i + 2].trim().indexOf("MAXDEPTH:") + 9, localqueue_LineArr[i + 2].trim().length())).trim();
				String type = (localqueue_LineArr[i + 3].trim().substring(localqueue_LineArr[i + 3].trim().indexOf("TYPE:") + 5, localqueue_LineArr[i + 3].trim().length())).trim();
				// 不是本地队列，循环下一条
				if (type.indexOf("QLOCAL") >= 0) {
				} else {
					continue;
				}

				Hashtable tmpHashtable = new Hashtable();
				tmpHashtable.put("queue", queue);
				tmpHashtable.put("curdepth", curdepth);
				tmpHashtable.put("maxdepth", maxdepth);
				tmpHashtable.put("type", type);

				Double tmpDouble = 0.0;
				tmpDouble = Double.parseDouble(curdepth) / Double.parseDouble(maxdepth);

				tmpHashtable.put("percent", df4.format(tmpDouble * 100) + "%");

				localQueueList.add(tmpHashtable);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// ----------------解析本地队列内容--创建监控项---------------------
		String remotequeueContent = "";
		tmpPt = Pattern.compile("(cmdbegin:remotequeuestart)(.*)(cmdbegin:remotequeueend)", Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if (mr.find()) {
			remotequeueContent = mr.group(2);
		}
		String[] remotequeue_LineArr = null;
		try {
			remotequeue_LineArr = remotequeueContent.split("\n");
			for (int i = 1; i < remotequeue_LineArr.length; i = i + 5) {
				String queue = (remotequeue_LineArr[i].trim().substring(remotequeue_LineArr[i].trim().indexOf("QUEUE:") + 6, remotequeue_LineArr[i].trim().length())).trim();
				String type = (remotequeue_LineArr[i + 1].trim().substring(remotequeue_LineArr[i + 1].trim().indexOf("TYPE:") + 5, remotequeue_LineArr[i + 1].trim().length())).trim();
				// 不是远程队列，循环下一条
				if (type.indexOf("QREMOTE") >= 0) {
				} else {
					continue;
				}
				String rqmname = (remotequeue_LineArr[i + 2].trim().substring(remotequeue_LineArr[i + 2].trim().indexOf("RQMNAME:") + 8, remotequeue_LineArr[i + 2].trim().length())).trim();
				String rname = (remotequeue_LineArr[i + 3].trim().substring(remotequeue_LineArr[i + 3].trim().indexOf("RNAME:") + 6, remotequeue_LineArr[i + 3].trim().length())).trim();
				String xmitq = (remotequeue_LineArr[i + 4].trim().substring(remotequeue_LineArr[i + 4].trim().indexOf("XMITQ:") + 6, remotequeue_LineArr[i + 4].trim().length())).trim();

				Hashtable tmpHashtable = new Hashtable();
				tmpHashtable.put("queue", queue);
				tmpHashtable.put("type", type);
				tmpHashtable.put("rqmname", rqmname);
				tmpHashtable.put("rname", rname);
				tmpHashtable.put("xmitq", xmitq);
				remoteQueueList.add(tmpHashtable);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 基本信息
		if (basicInfoHashtable != null && basicInfoHashtable.size() > 0) {
			retHashtable.put("basicInfoHashtable", basicInfoHashtable);
			if (basicInfoHashtable.containsKey("statu")) {
				String flag = (String) basicInfoHashtable.get("statu");
				Pingcollectdata hostdata = new Pingcollectdata();
				Calendar date = Calendar.getInstance();
				hostdata.setIpaddress(ipaddress);
				hostdata.setCollecttime(date);
				hostdata.setCategory("MqPing");
				hostdata.setEntity("Utilization");
				hostdata.setSubentity("ConnectUtilization");
				hostdata.setRestype("dynamic");
				hostdata.setUnit("%");
				if (flag.equalsIgnoreCase("running")) {
					hostdata.setThevalue("100");
				} else {
					hostdata.setThevalue("0");
				}
				MQConfigDao ping = new MQConfigDao();
				try {
					ping.createHostData(hostdata);
				} catch (Exception e) {
					e.printStackTrace();
					SysLogger.info(e.getMessage());
				} finally {
					ping.close();
				}
				NodeUtil nodeUtil = new NodeUtil();
				NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(mqconf);

				try {
					AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
					List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(mqconf.getId()), "middleware", "mq");
					for (int k = 0; k < list.size(); k++) {
						AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode) list.get(k);
						// 对MQ监听状态进行告警检测
						CheckEventUtil checkEventUtil = new CheckEventUtil();
						if ("ping".equalsIgnoreCase(alarmIndicatorsnode.getName())) {
							checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsnode, hostdata.getThevalue());
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		// 通道信息
		if (chstatusList != null && chstatusList.size() > 0) {
			retHashtable.put("chstatusList", chstatusList);
		}
		// 本地队列信息
		if (localQueueList != null && localQueueList.size() > 0) {
			retHashtable.put("localQueueList", localQueueList);
		}
		// 远程队列信息
		if (remoteQueueList != null && remoteQueueList.size() > 0) {
			retHashtable.put("remoteQueueList", remoteQueueList);
		}
		retHashtable.put("collecttime", collecttime);
		NodeUtil nodeUtil = new NodeUtil();
		NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(mqconf);
		try {
			updateMqData(ipaddress, nodeDTO, retHashtable);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ShareData.addMqdata(mqconf, retHashtable);

	}

	private static void updateMqData(String ipaddress, NodeDTO nodeDTO, Hashtable hashtable) {
		// Host host =
		// (Host)PollingEngine.getInstance().getNodeByID(nodeDTO.getId());
		AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
		List list = alarmIndicatorsUtil.getAlarmIndicatorsForNode(nodeDTO.getId() + "", nodeDTO.getType(), nodeDTO.getSubtype());
		if (list == null || list.size() == 0) {
			SysLogger.info("无告警指标 不告警=======================");
			return;
		}
		CheckEventUtil checkEventUtil = new CheckEventUtil();
		for (int i = 0; i < list.size(); i++) {
			try {
				AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode) list.get(i);
				if ("file".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
					String filename = ResourceCenter.getInstance().getSysPath() + "/linuxserver/" + ipaddress + ".mq.log";
					if (filename != null) {
						File file = new File(filename);
						long lasttime = file.lastModified();
						// long size = file.length();
						Date date = new Date(lasttime);
						java.util.Date date2 = new java.util.Date();
						long btmes = (date2.getTime() - date.getTime()) / 1000;
						if (file.exists()) {
							checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, btmes + "");
						} else {
							checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, "999999");
						}
					}
				} else if ("status".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
					String filename = ResourceCenter.getInstance().getSysPath() + "/linuxserver/" + ipaddress + ".mq.log";
					if (filename != null) {
						File file = new File(filename);
						long lasttime = file.lastModified();
						// long size = file.length();
						Date date = new Date(lasttime);
						java.util.Date date2 = new java.util.Date();
						long btmes = (date2.getTime() - date.getTime()) / 1000;
						if (file.exists()) {
							List chstatusList = (List) hashtable.get("chstatusList");

							checkStatus(nodeDTO, chstatusList, alarmIndicatorsNode, btmes);
						} else {
							checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, "999999");
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void checkStatus(NodeDTO node, List statusList, AlarmIndicatorsNode alarmIndicatorsNode, long btimes) {
		if ("0".equals(alarmIndicatorsNode.getEnabled())) {
			// 告警指标未监控 不做任何事情 返回
			return;
		}
		if (statusList != null && statusList.size() > 0) {
			for (int i = 0; i < statusList.size(); i++) {
				Hashtable table = (Hashtable) statusList.get(i);
				String status = (String) table.get("status");
				String channelName = (String) table.get("chstatusname");
				int value = 0;
				if ("status".equals(alarmIndicatorsNode.getName())) {

					if (status != null) {
						status = status.toUpperCase();
					} else {
						return;
					}

					// Hashtable allqchannelalalarmdata = null;
					// try {
					// allqchannelalalarmdata =
					// ShareData.getAllmqchannelalarmdata();
					// } catch (Exception e) {
					// e.printStackTrace();
					// }
					// if (allqchannelalalarmdata == null)return;
					MQchannelConfigDao configdao = null;
					List list = null;
					try {
						configdao = new MQchannelConfigDao();
						list = configdao.loadAll();
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						configdao.close();
					}

					if (list != null && list.size() > 0) {
						CheckEventUtil checkutil = new CheckEventUtil();
						for (int j = 0; j < list.size(); j++) {
							MQchannelConfig channelconfig = (MQchannelConfig) list.get(j);
							int monflag = channelconfig.getSms();
							if (monflag == 1) {
								alarmIndicatorsNode.setEnabled(monflag + "");
								alarmIndicatorsNode.setLimenvalue0("RUNNING");
								// alarmIndicatorsNode.setSms0("1");
								alarmIndicatorsNode.setLimenvalue1("RUNNING");
								// alarmIndicatorsNode.setSms1("1");
								alarmIndicatorsNode.setLimenvalue2("RUNNING");
								// alarmIndicatorsNode.setSms2("1");
								// alarmIndicatorsNode.setCompare(2);
								checkutil.checkEvent(node, alarmIndicatorsNode, status, channelName.trim());
							}
						}
					}

				}
			}
		}

	}

	private static String getMaxNum(String ipAddress) {
		String maxStr = null;
		File logFolder = new File(ResourceCenter.getInstance().getSysPath() + "linuxserver/");
		String[] fileList = logFolder.list();

		for (int i = 0; i < fileList.length; i++) // 找一个最新的文件
		{
			if (!fileList[i].startsWith(ipAddress))
				continue;

			return ipAddress;
		}
		return maxStr;
	}

	private static void copyFile(String ipAddress, String max) {
		try {
			String currenttime = SysUtil.getCurrentTime();
			currenttime = currenttime.replaceAll("-", "");
			currenttime = currenttime.replaceAll(" ", "");
			currenttime = currenttime.replaceAll(":", "");
			String ipdir = ipAddress.replaceAll("\\.", "-");
			String filename = ResourceCenter.getInstance().getSysPath() + "linuxserver_bak\\" + ipdir;
			File file = new File(filename);
			if (!file.exists())
				file.mkdir();
			String cmd = "cmd   /c   copy   " + ResourceCenter.getInstance().getSysPath() + "linuxserver\\" + ipAddress + ".mq.log" + " " + ResourceCenter.getInstance().getSysPath() + "linuxserver_bak\\" + ipdir + "\\" + ipAddress + "-" + currenttime + ".mq.log";
			Process child = Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
