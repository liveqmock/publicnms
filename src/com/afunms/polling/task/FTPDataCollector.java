package com.afunms.polling.task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.dao.FTPConfigDao;
import com.afunms.application.dao.FtpHistoryDao;
import com.afunms.application.dao.FtpRealTimeDao;
import com.afunms.application.model.FTPConfig;
import com.afunms.application.model.FtpHistory;
import com.afunms.application.model.FtpRealTime;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Ftp;
import com.afunms.polling.om.Interfacecollectdata;

public class FTPDataCollector {
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	List alarmIndicatorsList = new ArrayList();

	public FTPDataCollector() {
	}

	public void collect_Data(NodeGatherIndicators ftpIndicatorsNode) {

		FtpRealTimeDao ftpRealTimeDao = null;
		FtpHistoryDao ftpHistoryDao = null;
		String ftpId = ftpIndicatorsNode.getNodeid();
		try {
			Calendar date = Calendar.getInstance();
			FTPConfig ftpConfig = null;
			FTPConfigDao ftpConfigDao = null;
			try {
				ftpHistoryDao = new FtpHistoryDao();
				ftpRealTimeDao = new FtpRealTimeDao();
				Hashtable realHash = ftpRealTimeDao.getAllReal();
				Integer iscanconnected = new Integer(0);
				FtpRealTime lastRealTime = new FtpRealTime();
				String reason = "";
				ftpConfigDao = new FTPConfigDao();
				try {
					ftpConfig = (FTPConfig) ftpConfigDao.findByID(ftpId);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					ftpConfigDao.close();
				}

				NodeUtil nodeUtil = new NodeUtil();
				NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(ftpConfig);

				AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
				alarmIndicatorsList = alarmIndicatorsUtil.getAlarmIndicatorsForNode(nodeDTO.getId() + "", nodeDTO.getType(), nodeDTO.getSubtype());

				Ftp ftp = (Ftp) PollingEngine.getInstance().getFtpByID(ftpConfig.getId());
				if (ftp == null) {
					return;
				}
				if (ftp != null) {
					ftp.setStatus(0);
					ftp.setAlarm(false);
					ftp.getAlarmMessage().clear();
					ftp.setLastTime(sdf.format(date.getTime()));
				}

				boolean old = false;
				Integer smssign = new Integer(0);
				if (realHash != null && realHash.get(ftpId) != null) {
					old = true;
					lastRealTime = (FtpRealTime) realHash.get(ftpId);
					smssign = lastRealTime.getSms_sign();
				}
				FtpUtil ftpUtil = new FtpUtil(ftpConfig.getIpaddress(), ftpConfig.getUsername(), ftpConfig.getPassword());

				boolean downloadFlag = true;
				int downFlagInt = 0;
				boolean uploadFlag = true;
				int uploadFlagInt = 0;
				try {
					for (int i = 0; i < alarmIndicatorsList.size(); i++) {
						AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode) alarmIndicatorsList.get(i);
						if ("upload".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
							uploadFlag = ftpUtil.upload(ResourceCenter.getInstance().getSysPath() + "/ftpupload/", ftpConfig.getFilename());
							if (uploadFlag)
								uploadFlagInt = 1;
						} else if ("download".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
							downloadFlag = ftpUtil.download(ResourceCenter.getInstance().getSysPath() + "/ftpdownload/", ftpConfig.getFilename());
							if (downloadFlag)
								downFlagInt = 1;
						}
					}
					if (downloadFlag && uploadFlag) {
						reason = "FTP服务有效";
						iscanconnected = new Integer(1);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				Vector ftpvector = new Vector();
				// 开始设置采集值实体
				Interfacecollectdata interfacedata = new Interfacecollectdata();
				interfacedata.setIpaddress(ftp.getIpAddress());
				interfacedata.setCollecttime(date);
				interfacedata.setCategory("Ftp");
				interfacedata.setEntity("download");
				interfacedata.setSubentity(ftp.getId() + "");
				interfacedata.setRestype("static");
				interfacedata.setUnit("");
				interfacedata.setThevalue(downFlagInt + "");
				interfacedata.setChname("下载服务");
				ftpvector.add(interfacedata);

				interfacedata = new Interfacecollectdata();
				interfacedata.setIpaddress(ftp.getIpAddress());
				interfacedata.setCollecttime(date);
				interfacedata.setCategory("Ftp");
				interfacedata.setEntity("upload");
				interfacedata.setSubentity(ftp.getId() + "");
				interfacedata.setRestype("static");
				interfacedata.setUnit("");
				interfacedata.setThevalue(uploadFlagInt + "");
				interfacedata.setChname("上载服务");
				ftpvector.add(interfacedata);

				Hashtable collectHash = new Hashtable();
				collectHash.put("ftp", ftpvector);
				try {
					updateFtpData(nodeDTO, collectHash);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// 保存进历史数据
				FtpHistory ftpHistory = new FtpHistory();
				ftpHistory.setFtp_id(ftpConfig.getId());
				ftpHistory.setIs_canconnected(iscanconnected);
				ftpHistory.setMon_time(Calendar.getInstance());
				ftpHistory.setReason(reason);
				ftpHistoryDao.save(ftpHistory);

				FtpRealTime ftpRealTime = new FtpRealTime();
				ftpRealTime.setFtp_id(ftpConfig.getId());
				ftpRealTime.setIs_canconnected(iscanconnected);
				ftpRealTime.setReason(reason);
				ftpRealTime.setMon_time(Calendar.getInstance());
				// 实时数据
				if (old == true) {
					ftpRealTime.setSms_sign(1);
				} else {
					ftpRealTime.setSms_sign(smssign);
				}
				// 保存实时数据
				if (old == true) {
					ftpRealTime.setId(lastRealTime.getId());
					ftpRealTimeDao.update(ftpRealTime);
				}
				if (old == false) {
					ftpRealTimeDao.save(ftpRealTime);
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				ftpHistoryDao.close();
				ftpRealTimeDao.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
	}

	private void updateFtpData(NodeDTO nodeDTO, Hashtable hashtable) {
		if (alarmIndicatorsList == null || alarmIndicatorsList.size() == 0) {
			return;
		}
		Vector ftpvector = (Vector) hashtable.get("ftp");
		CheckEventUtil checkEventUtil = new CheckEventUtil();
		for (int i = 0; i < alarmIndicatorsList.size(); i++) {
			try {
				AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode) alarmIndicatorsList.get(i);
				if ("upload".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
					if (ftpvector != null && ftpvector.size() > 0) {
						for (int k = 0; k < ftpvector.size(); k++) {
							Interfacecollectdata ftpdata = (Interfacecollectdata) ftpvector.get(k);
							if ("upload".equalsIgnoreCase(ftpdata.getEntity())) {
								checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, ftpdata.getThevalue());
							}
						}
					}
				} else if ("download".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
					if (ftpvector != null && ftpvector.size() > 0) {
						for (int k = 0; k < ftpvector.size(); k++) {
							Interfacecollectdata ftpdata = (Interfacecollectdata) ftpvector.get(k);
							if ("download".equalsIgnoreCase(ftpdata.getEntity())) {
								checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, ftpdata.getThevalue());
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
