package com.afunms.polling.task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmConstant;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.dao.TFtpmonitor_historyDao;
import com.afunms.application.dao.TFtpmonitor_realtimeDao;
import com.afunms.application.model.TFTPConfig;
import com.afunms.application.model.TFtpmonitor_history;
import com.afunms.application.model.TFtpmonitor_realtime;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.event.dao.AlarmInfoDao;
import com.afunms.event.dao.SmscontentDao;
import com.afunms.event.model.AlarmInfo;
import com.afunms.event.model.EventList;
import com.afunms.event.model.Smscontent;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.TFtp;
import com.afunms.polling.om.Interfacecollectdata;
import com.afunms.system.util.TimeGratherConfigUtil;

/**
 * @author GANYI
 * @since 2012-04-23 11:00:00
 */
public class TFTPDataCollector {
	private Hashtable sendeddata = ShareData.getSendeddata();
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public TFTPDataCollector() {
	}

	public void collect_Data(NodeGatherIndicators tftpIndicatorsNode) {
		TFtpmonitor_realtimeDao tftpmonitor_realtimeDao = null;
		TFtpmonitor_historyDao tftpmonitor_historyDao = null;
		String ftpid = tftpIndicatorsNode.getNodeid();
		try {
			Calendar date = Calendar.getInstance();
			TFTPConfig tftpConfig = null;
			try {
				tftpmonitor_historyDao = new TFtpmonitor_historyDao();
				tftpmonitor_realtimeDao = new TFtpmonitor_realtimeDao();
				Hashtable realHash = tftpmonitor_realtimeDao.getAllReal();
				Integer iscanconnected = new Integer(0 + "");
				TFtpmonitor_realtime tftpmonitor_realtimeold = new TFtpmonitor_realtime();
				String reason = "";
				if (ShareData.getTftplist() != null) {
					List tftpList = ShareData.getTftplist();
					TFTPConfig vo = null;
					if (tftpList != null && tftpList.size() > 0) {
						for (int i = 0; i < tftpList.size(); i++) {
							vo = (TFTPConfig) tftpList.get(i);
							if (vo.getMonflag() == 0)
								continue;
							if (vo.getId() == Integer.parseInt(ftpid))
								tftpConfig = vo;
							break;
						}
					} else
						return;
				} else {
					return;
				}


				TFtp tftp = (TFtp) PollingEngine.getInstance().getTftpByID(tftpConfig.getId());
				if (tftp == null) {
					return;
				}
				if (tftp != null) {
					// 初始化被监视的TFTP状态
					tftp.setStatus(0);
					tftp.setAlarm(false);
					tftp.getAlarmMessage().clear();
					Calendar _tempCal = Calendar.getInstance();
					Date _cc = _tempCal.getTime();
					String _time = sdf.format(_cc);
					tftp.setLastTime(_time);
				}

				NodeUtil nodeUtil = new NodeUtil();
				NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(tftpConfig);

				Integer tftpConfig_id = tftpConfig.getId();
				Integer tftp_id = tftpConfig.getId();
				boolean old = false;
				String str = "";
				Integer smssign = new Integer(0);
				if (realHash != null && realHash.get(tftp_id) != null) {
					old = true;
					tftpmonitor_realtimeold = (TFtpmonitor_realtime) realHash.get(tftp_id);
					smssign = tftpmonitor_realtimeold.getSms_sign();
				}
				TFtpUtil tftputil = new TFtpUtil(tftpConfig.getIpaddress(), 69, tftpConfig.getUsername(), tftpConfig.getPassword(), "", ResourceCenter.getInstance().getSysPath() + "/tftpdownload/", tftpConfig.getFilename());
				boolean downloadflag = true;
				int downflagint = 0;
				boolean uploadsuccess = true;
				int uploadflagint = 0;
				try {
					uploadsuccess = tftputil.uploadFile(tftpConfig.getIpaddress(), ResourceCenter.getInstance().getSysPath() + "tftpupload/" + tftpConfig.getFilename());
					if (uploadsuccess)
						uploadflagint = 1;

					downloadflag = tftputil.tftpOne(tftpConfig.getIpaddress(), tftpConfig.getFilename(), ResourceCenter.getInstance().getSysPath() + "tftpupload/" + tftpConfig.getFilename());
					if (downloadflag)
						downflagint = 1;

					if (downloadflag && uploadsuccess) {
						reason = "TFTP服务有效";
						iscanconnected = new Integer(1);
					}

				} catch (Exception ex) {
					// //不能进行FTP服务
					// ftp.setAlarm(true);
					// ftp.setStatus(3);
					// //dbnode.setStatus(3);
					// List alarmList = ftp.getAlarmMessage();
					// if(alarmList == null)alarmList = new ArrayList();
					// ftp.getAlarmMessage().add("FTP下载服务无效");
					// reason = "FTP服务无效";
					// createEvent(ftpConfig, reason);
				}

				Vector tftpvector = new Vector();
				// 开始设置采集值实体
				Interfacecollectdata interfacedata = new Interfacecollectdata();
				interfacedata.setIpaddress(tftp.getIpAddress());
				interfacedata.setCollecttime(date);
				interfacedata.setCategory("Tftp");
				interfacedata.setEntity("download");
				interfacedata.setSubentity(tftp.getId() + "");
				interfacedata.setRestype("static");
				interfacedata.setUnit("");
				interfacedata.setThevalue(downflagint + "");
				interfacedata.setChname("下载服务");
				tftpvector.add(interfacedata);

				interfacedata = new Interfacecollectdata();
				interfacedata.setIpaddress(tftp.getIpAddress());
				interfacedata.setCollecttime(date);
				interfacedata.setCategory("Tftp");
				interfacedata.setEntity("upload");
				interfacedata.setSubentity(tftp.getId() + "");
				interfacedata.setRestype("static");
				interfacedata.setUnit("");
				interfacedata.setThevalue(uploadflagint + "");
				interfacedata.setChname("上载服务");
				tftpvector.add(interfacedata);

				Hashtable collectHash = new Hashtable();
				collectHash.put("tftp", tftpvector);

				// mail.setStatus(3);
				// mail.setAlarm(true);

				// createEvent(mailconfig, reason);
				// try{
				// AlarmIndicatorsUtil alarmIndicatorsUtil = new
				// AlarmIndicatorsUtil();
				// List list =
				// alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(tftpConfig.getId()),
				// AlarmConstant.TYPE_SERVICE, "tftp");
				// for(int k = 0 ; k < list.size() ; k ++){
				// AlarmIndicatorsNode alarmIndicatorsnode =
				// (AlarmIndicatorsNode)list.get(k);
				// //对邮件服务值进行告警检测
				// CheckEventUtil checkutil = new CheckEventUtil();
				// checkutil.updateData(tftp,collectHash,"service","tftp",alarmIndicatorsnode);
				// //}
				// }
				// }catch(Exception e){
				// e.printStackTrace();
				// }

				try {
					AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
					List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(tftpConfig.getId()), AlarmConstant.TYPE_SERVICE, "tftp");
					for (int k = 0; k < list.size(); k++) {
						AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode) list.get(k);
						// 对TFTP服务值进行告警检测
						CheckEventUtil checkEventUtil = new CheckEventUtil();
						if ("upload".equalsIgnoreCase(alarmIndicatorsnode.getName())) {
							checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsnode, uploadflagint + "");
						} else if ("download".equalsIgnoreCase(alarmIndicatorsnode.getName())) {
							checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsnode, downflagint + "");
						}
						// CheckEventUtil checkutil = new CheckEventUtil();
						// checkutil.updateData(mail,collectHash,"service","mail",alarmIndicatorsnode);
						// }
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				// 保存进历史数据

				TFtpmonitor_history tftpMonitor_history = new TFtpmonitor_history();
				tftpMonitor_history.setTftp_id(tftpConfig.getId());
				tftpMonitor_history.setIs_canconnected(iscanconnected);
				tftpMonitor_history.setMon_time(Calendar.getInstance());
				tftpMonitor_history.setReason(reason);
				tftpmonitor_historyDao.save(tftpMonitor_history);

				// UrlDataCollector udc = new UrlDataCollector();
				TFtpmonitor_realtime tftpmonitor_realtime = new TFtpmonitor_realtime();
				tftpmonitor_realtime.setTftp_id(tftpConfig.getId());
				tftpmonitor_realtime.setIs_canconnected(iscanconnected);
				tftpmonitor_realtime.setReason(reason);
				tftpmonitor_realtime.setMon_time(Calendar.getInstance());
				// 实时数据
				// ur.setUrl_id(url_id);
				if (old == true) {
					tftpmonitor_realtime.setSms_sign(1);
				} else {
					tftpmonitor_realtime.setSms_sign(smssign);
				}

				// 保存realtime
				if (old == true) {
					tftpmonitor_realtime.setId(tftpmonitor_realtimeold.getId());
					tftpmonitor_realtimeDao.update(tftpmonitor_realtime);
				}
				if (old == false) {
					tftpmonitor_realtimeDao.save(tftpmonitor_realtime);
				}

				// uh.setIs_refresh(ur.getIs_refresh());
				// uh.setIs_valid(ur.getIs_valid());

				// if(sendeddata.containsKey("ftpserver:"+ftpConfig.getId())){
				// sendeddata.remove("ftpserver:"+ftpConfig.getId());
				// }
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				tftpmonitor_historyDao.close();
				tftpmonitor_realtimeDao.close();
			}

			// }

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}

	}

	public void createEvent(TFTPConfig tftpConfig, String reason) {
		Calendar date = Calendar.getInstance();
		String time = sdf.format(date.getTime());
		EventList event = new EventList();
		event.setEventtype("tftpserver");
		event.setEventlocation(tftpConfig.getIpaddress());
		event.setBusinessid(tftpConfig.getBid());
		event.setManagesign(new Integer(0));
		event.setReportman("monitorpc");
		event.setRecordtime(new GregorianCalendar());
		String errorcontent = time + " " + tftpConfig.getName() + "(IP:" + tftpConfig.getIpaddress() + ")：FTP服务故障";
		event.setContent(errorcontent);
		Integer level = new Integer(2);
		event.setLevel1(level);
		// reason="FTP服务无效";
		// EventListDao eventListDao = null ;
		try {
			// eventListDao = new EventListDao();
			// eventListDao.save(event);
		} catch (Exception e) {

		} finally {
			// eventListDao.close();
		}

		Vector eventtmpV = new Vector();
		eventtmpV.add(event);
		createSMS("ftpserver", tftpConfig.getId() + "", errorcontent, tftpConfig.getIpaddress());
	}

	public void createSMS(String tftpserver, String ftp_id, String errmsg, String tftpstr) {
		// 建立短信
		// 从内存里获得当前这个IP的PING的值
		Calendar date = Calendar.getInstance();
		try {
			if (!sendeddata.containsKey(tftpserver + ":" + ftp_id)) {
				// 若不在，则建立短信，并且添加到发送列表里
				Smscontent smscontent = new Smscontent();
				smscontent.setMessage(errmsg);
				smscontent.setObjid(ftp_id);
				Calendar _tempCal = Calendar.getInstance();
				Date _cc = _tempCal.getTime();
				String _time = sdf.format(_cc);
				smscontent.setRecordtime(_time);
				smscontent.setSubtype("ftp");
				smscontent.setLevel(3 + "");
				// 发送短信
				SmscontentDao smsmanager = new SmscontentDao();
				smsmanager.sendURLSmscontent(smscontent);
				sendeddata.put(tftpserver + ":" + ftp_id, date);
			} else {
				// 若在，则从已发送短信列表里判断是否已经发送当天的短信
				Calendar formerdate = (Calendar) sendeddata.get(tftpserver + ":" + ftp_id);
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				Date last = null;
				Date current = null;
				Calendar sendcalen = formerdate;
				Date cc = sendcalen.getTime();
				String tempsenddate = formatter.format(cc);

				Calendar currentcalen = date;
				cc = currentcalen.getTime();
				last = formatter.parse(tempsenddate);
				String currentsenddate = formatter.format(cc);
				current = formatter.parse(currentsenddate);

				long subvalue = current.getTime() - last.getTime();
				if (subvalue / (1000 * 60 * 60 * 24) >= 1) {
					// 超过一天，则再发信息
					Smscontent smscontent = new Smscontent();
					// String time = sdf.format(date.getTime());
					smscontent.setMessage(errmsg);
					smscontent.setObjid(ftp_id);
					smscontent.setLevel(3 + "");
					// 发送短信
					SmscontentDao smsmanager = new SmscontentDao();
					smsmanager.sendURLSmscontent(smscontent);
					Calendar _tempCal = Calendar.getInstance();
					Date _cc = _tempCal.getTime();
					String _time = sdf.format(_cc);
					smscontent.setRecordtime(_time);
					smscontent.setSubtype("ftp");
					// 修改已经发送的短信记录
					sendeddata.put(tftpserver + ":" + ftp_id, date);
				} else {
					// 则写声音告警数据
					// 向声音告警表里写数据
					AlarmInfo alarminfo = new AlarmInfo();
					alarminfo.setContent(errmsg);
					alarminfo.setIpaddress(errmsg);
					alarminfo.setLevel1(new Integer(2));
					alarminfo.setRecordtime(Calendar.getInstance());
					AlarmInfoDao alarmdao = new AlarmInfoDao();
					alarmdao.save(alarminfo);

					/*
					 * Calendar tempCal = Calendar.getInstance();
					 * java.text.SimpleDateFormat sdf = new
					 * java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); String
					 * time = sdf.format(cc);
					 * 
					 * String queryStr = "insert into
					 * alarminfor(content,ipaddress,level1,recordtime)
					 * values('"+errmsg+"','"+ftpstr+"',2,to_date('"+time+"','YYYY-MM-DD
					 * HH24:MI:SS'))"; Connection con = null; PreparedStatement
					 * stmt = null; ResultSet rs = null; try{
					 * con=DataGate.getCon(); stmt =
					 * con.prepareStatement(queryStr); stmt.execute();
					 * stmt.close(); }catch(Exception ex){ ex.printStackTrace();
					 * //rs.close(); }finally{ try{ stmt.close();
					 * DataGate.freeCon(con); }catch(Exception exp){ // } }
					 */

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
