package com.afunms.polling.task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmConstant;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.dao.EmailConfigDao;
import com.afunms.application.dao.EmailHistoryDao;
import com.afunms.application.dao.EmailRealTimeDao;
import com.afunms.application.model.EmailHistory;
import com.afunms.application.model.EmailMonitorConfig;
import com.afunms.application.model.EmailRealtime;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.ReceiveMail;
import com.afunms.common.util.SendMail;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Mail;

public class MailDataCollector {
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public void collect_Data(NodeGatherIndicators mailIndicatorsNode) {
		EmailRealTimeDao emailRealtimeDao = null;
		EmailHistoryDao emailHistoryDao = null;

		EmailHistory emailHistory = new EmailHistory();
		EmailRealtime emailRealTime = new EmailRealtime();
		String mailID = mailIndicatorsNode.getNodeid();
		try {
			Calendar date = Calendar.getInstance();
			EmailMonitorConfig mailConfig = null;
			EmailConfigDao emailConfigDao = null;
			try {
				emailConfigDao = new EmailConfigDao();
				mailConfig = (EmailMonitorConfig) emailConfigDao.findByID(mailID);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				emailConfigDao.close();
			}

			NodeUtil nodeUtil = new NodeUtil();
			NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(mailConfig);

			ReceiveMail receieveMail = new ReceiveMail();
			SendMail sendmail = new SendMail();

			try {
				emailRealtimeDao = new EmailRealTimeDao();
				Hashtable realHash = null;
				try {
					realHash = emailRealtimeDao.getAllReal();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					emailRealtimeDao.close();
				}

				Integer isCanConnected = new Integer(0);
				String reason = "";
				boolean old = false;
				Integer smssign = new Integer(0);

				Mail mail = (Mail) PollingEngine.getInstance().getMailByID(mailConfig.getId());
				if (mail == null) {
					return;
				} else if (mail != null) {
					mail.setStatus(0);
					mail.setAlarm(false);
					mail.getAlarmMessage().clear();
					mail.setLastTime(sdf.format(date.getTime()));
				}

				if (realHash != null && realHash.get(mailID) != null) {
					old = true;
					emailRealTime = (EmailRealtime) realHash.get(mailID);
					smssign = emailRealTime.getSms_sign();
				}

				boolean sendFlag = true;
				boolean receieveFlag = true;
				int sendInt = 0;
				int receieveInt = 0;
				try {
					try {
						AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
						List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(mailConfig.getId()), AlarmConstant.TYPE_SERVICE, "mail");
						for (int k = 0; k < list.size(); k++) {
							AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode) list.get(k);
							CheckEventUtil checkEventUtil = new CheckEventUtil();
							if ("receieve".equalsIgnoreCase(alarmIndicatorsnode.getName())) {
								receieveFlag = receieveMail.GetReceieveMail(mailConfig.getReceiveAddress(), mailConfig.getUsername(), mailConfig.getPassword());
								if (receieveFlag)
									receieveInt = 1;
								
								checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsnode, receieveInt + "");
							} else if ("send".equalsIgnoreCase(alarmIndicatorsnode.getName())) {
								if (configSendMail(sendmail, mailConfig)) {
									sendFlag = sendmail.sendmail();
									if (sendFlag)
										sendInt = 1;
								}
								checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsnode, sendInt + "");
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

					if (sendFlag && receieveFlag) {
						isCanConnected = 1;
						reason = "服务有效";
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				// 保存历史数据
				emailHistory.setEmail_id(mailConfig.getId());
				emailHistory.setIs_canconnected(isCanConnected);
				emailHistory.setMon_time(Calendar.getInstance());
				emailHistory.setReason(reason);
				emailHistoryDao = new EmailHistoryDao();
				try {
					emailHistoryDao.save(emailHistory);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					emailHistoryDao.close();
				}
				// 保存实时数据
				emailRealtimeDao = new EmailRealTimeDao();

				EmailRealtime thisTime = new EmailRealtime();
				thisTime.setEmail_id(mailConfig.getId());
				thisTime.setIs_canconnected(isCanConnected);
				thisTime.setMon_time(Calendar.getInstance());
				thisTime.setReason(reason);
				if (old == true) {
					thisTime.setId(emailRealTime.getId());
					thisTime.setSms_sign(1);
					try {
						emailRealtimeDao.update(thisTime);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						emailRealtimeDao.close();
					}
				} else {
					thisTime.setSms_sign(smssign);
					try {
						emailRealtimeDao.save(thisTime);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						emailRealtimeDao.close();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String configAddress(EmailMonitorConfig mailCnfig) {
		String fromAddress = "";
		fromAddress = mailCnfig.getAddress();
		return fromAddress;
	}

	public boolean configSendMail(SendMail sendmail, EmailMonitorConfig mailConfig) throws AddressException {
		Address[] ccAddress = { new InternetAddress("hukelei@dhcc.com.cn"), new InternetAddress("rhythm333@163.com") };
		sendmail.setMailaddress(configAddress(mailConfig));
		sendmail.setSendmail(mailConfig.getUsername());
		sendmail.setSendpasswd(mailConfig.getPassword());
		sendmail.setToAddr(mailConfig.getRecivemail());
		sendmail.setBody("邮件服务测试");
		sendmail.setSubject("邮件服务设置");
		sendmail.setFromAddr(mailConfig.getUsername() + "@" + mailConfig.getAddress());
		sendmail.setCcAddress(ccAddress);
		return true;
	}
}
