package com.afunms.application.dao;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.afunms.common.util.SystemConstant;
import com.afunms.polling.om.CPUcollectdata;
import com.afunms.polling.om.NetAppAggregate;
import com.afunms.polling.om.NetAppConsistencyPoint;
import com.afunms.polling.om.NetAppDisk;
import com.afunms.polling.om.NetAppDump;
import com.afunms.polling.om.NetAppDumpList;
import com.afunms.polling.om.NetAppEnvironment;
import com.afunms.polling.om.NetAppPlex;
import com.afunms.polling.om.NetAppQuota;
import com.afunms.polling.om.NetAppRaid;
import com.afunms.polling.om.NetAppRestore;
import com.afunms.polling.om.NetAppSnapshot;
import com.afunms.polling.om.NetAppSpare;
import com.afunms.polling.om.NetAppTree;
import com.afunms.polling.om.NetAppVFiler;
import com.afunms.polling.om.NetAppVFilerIpEntity;
import com.afunms.polling.om.NetAppVFilerPathEntity;
import com.afunms.polling.om.NetAppVFilerProtocolEntity;
import com.afunms.polling.om.NetAppVolume;
import com.afunms.polling.om.Systemcollectdata;
import com.gatherdb.GathersqlListManager;

public class NetAppDataOperator {

	/**
	 * 
	 * 把采集数据生成sql放入的内存列表中
	 */
	public void CreateResultTosql(Hashtable ipdata, String ip) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar tempCal = null;
		Date cc = null;
		String time = null;

		if (ipdata.containsKey("system") || ipdata.containsKey("productInfo")) {
			Vector tempVector = new Vector();
			if (ipdata.containsKey("system")) {
				String deleteSQL="delete from NetAppProductInformation where ipaddress='"+ip+"' and entity='System'";
				GathersqlListManager.Addsql(deleteSQL);
				tempVector = (Vector) ipdata.get("system");
			} else if (ipdata.containsKey("productInfo")) {
				String deleteSQL="delete from NetAppProductInformation where ipaddress='"+ip+"' and entity='ProductInfo'";
				GathersqlListManager.Addsql(deleteSQL);
				tempVector = (Vector) ipdata.get("productInfo");
			}
			if (tempVector != null && tempVector.size() > 0) {
				try {
					Systemcollectdata systemcollectdata = null;
					for (int i = 0; i < tempVector.size(); i++) {
						systemcollectdata = (Systemcollectdata) tempVector.elementAt(i);
						tempCal = (Calendar) systemcollectdata.getCollecttime();
						cc = tempCal.getTime();
						time = sdf.format(cc);
						tempCal = null;
						cc = null;
						StringBuffer sBuffer = new StringBuffer(150);
						sBuffer.append("insert into ");
						sBuffer.append("NetAppProductInformation");
						sBuffer.append("(ipaddress,category,entity,subentity,chname,restype,value,unit,collectTime) ");
						sBuffer.append("values('");
						sBuffer.append(ip);
						sBuffer.append("','");
						sBuffer.append(systemcollectdata.getCategory());
						sBuffer.append("','");
						sBuffer.append(systemcollectdata.getEntity());
						sBuffer.append("','");
						sBuffer.append(systemcollectdata.getSubentity());
						sBuffer.append("','");
						sBuffer.append(systemcollectdata.getChname());
						sBuffer.append("','");
						sBuffer.append(systemcollectdata.getRestype());
						sBuffer.append("','");
						sBuffer.append(systemcollectdata.getThevalue());
						sBuffer.append("','");
						sBuffer.append(systemcollectdata.getUnit());
						if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
							sBuffer.append("','");
							sBuffer.append(time);
							sBuffer.append("')");
						} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
							sBuffer.append("',");
							sBuffer.append("to_date('" + time + "','YYYY-MM-DD HH24:MI:SS')");
							sBuffer.append(")");
						}
						GathersqlListManager.Addsql(sBuffer.toString());
						sBuffer = null;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			tempVector = null;
		}
		// cpu
		if (ipdata.containsKey("cpu")) {
			Vector tempVector = new Vector();
			tempVector = (Vector) ipdata.get("cpu");
			if (tempVector != null && tempVector.size() > 0) {
				try {
					CPUcollectdata cpuCollectData = null;
					for (int i = 0; i < tempVector.size(); i++) {
						cpuCollectData = (CPUcollectdata) tempVector.elementAt(i);
						tempCal = (Calendar) cpuCollectData.getCollecttime();
						cc = tempCal.getTime();
						time = sdf.format(cc);
						tempCal = null;
						cc = null;
						StringBuffer sBuffer = new StringBuffer(150);
						sBuffer.append("insert into ");
						sBuffer.append("netappcpu");
						sBuffer.append("(ipaddress,category,entity,subentity,chname,restype,value,unit,collectTime) ");
						sBuffer.append("values('");
						sBuffer.append(ip);
						sBuffer.append("','");
						sBuffer.append(cpuCollectData.getCategory());
						sBuffer.append("','");
						sBuffer.append(cpuCollectData.getEntity());
						sBuffer.append("','");
						sBuffer.append(cpuCollectData.getSubentity());
						sBuffer.append("','");
						sBuffer.append(cpuCollectData.getChname());
						sBuffer.append("','");
						sBuffer.append(cpuCollectData.getRestype());
						sBuffer.append("','");
						sBuffer.append(cpuCollectData.getThevalue());
						sBuffer.append("','");
						sBuffer.append(cpuCollectData.getUnit());
						if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
							sBuffer.append("','");
							sBuffer.append(time);
							sBuffer.append("')");
						} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
							sBuffer.append("',");
							sBuffer.append("to_date('" + time + "','YYYY-MM-DD HH24:MI:SS')");
							sBuffer.append(")");
						}
						GathersqlListManager.Addsql(sBuffer.toString());
						sBuffer = null;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			tempVector = null;
		}

		// aggregate
		if (ipdata.containsKey("aggregate")) {
			
			String deleteSQL="delete from NetAppAggregate where ipaddress='"+ip+"'";
			GathersqlListManager.Addsql(deleteSQL);
			
			Vector aggregateVector = (Vector) ipdata.get("aggregate");
			if (aggregateVector != null && aggregateVector.size() > 0) {
				NetAppAggregate aggregate = null;
				for (int si = 0; si < aggregateVector.size(); si++) {
					aggregate = (NetAppAggregate) aggregateVector.elementAt(si);
					tempCal = (Calendar) aggregate.getCollectTime();
					cc = tempCal.getTime();
					time = sdf.format(cc);
					tempCal = null;
					cc = null;
					StringBuffer sBuffer = new StringBuffer(200);
					sBuffer.append("insert into ");
					sBuffer.append("NetAppAggregate");
					sBuffer.append("(ipaddress,aggrIndex,aggrName,aggrFSID,aggrOwningHost,aggrState,aggrStatus,aggrOptions,aggrUUID,aggrFlexvollist,aggrType,aggrRaidType,collectTime) ");
					sBuffer.append("values('");
					sBuffer.append(ip);
					sBuffer.append("','");
					sBuffer.append(aggregate.getAggrIndex());
					sBuffer.append("','");
					sBuffer.append(aggregate.getAggrName());
					sBuffer.append("','");
					sBuffer.append(aggregate.getAggrFSID());
					sBuffer.append("','");
					sBuffer.append(aggregate.getAggrOwningHost());
					sBuffer.append("','");
					sBuffer.append(aggregate.getAggrState());
					sBuffer.append("','");
					sBuffer.append(aggregate.getAggrStatus());
					sBuffer.append("','");
					sBuffer.append(aggregate.getAggrOptions());
					sBuffer.append("','");
					sBuffer.append(aggregate.getAggrUUID());
					sBuffer.append("','");
					sBuffer.append(aggregate.getAggrFlexvollist());
					sBuffer.append("','");
					sBuffer.append(aggregate.getAggrType());
					sBuffer.append("','");
					sBuffer.append(aggregate.getAggrRaidType());
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sBuffer.append("','");
						sBuffer.append(time);
						sBuffer.append("')");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sBuffer.append("',");
						sBuffer.append("to_date('" + time + "','YYYY-MM-DD HH24:MI:SS')");
						sBuffer.append(")");
					}
					GathersqlListManager.Addsql(sBuffer.toString());
					sBuffer = null;
					aggregate = null;
				}
			}
		}

		// consistencyPoint
		if (ipdata.containsKey("consistencyPoint")) {
			
			String deleteSQL="delete from netappconsistencypoint where ipaddress='"+ip+"'";
			GathersqlListManager.Addsql(deleteSQL);
			
			Vector consistencyPointVector = (Vector) ipdata.get("consistencyPoint");
			if (consistencyPointVector != null && consistencyPointVector.size() > 0) {
				NetAppConsistencyPoint netAppConsistencyPoint = null;
				for (int si = 0; si < consistencyPointVector.size(); si++) {
					netAppConsistencyPoint = (NetAppConsistencyPoint) consistencyPointVector.elementAt(si);
					tempCal = (Calendar) netAppConsistencyPoint.getCollectTime();
					cc = tempCal.getTime();
					time = sdf.format(cc);
					tempCal = null;
					cc = null;
					StringBuffer sBuffer = new StringBuffer(200);
					sBuffer.append("insert into ");
					sBuffer.append("netappconsistencypoint");
					sBuffer.append("(ipaddress,cpTime,cpFromTimerOps,cpFromSnapshotOps,cpFromLowWaterOps,cpFromHighWaterOps,cpFromLogFullOps,cpFromCpOps,cpTotalOps,cpFromFlushOps,cpFromSyncOps,cpFromLowVbufOps,cpFromCpDeferredOps,cpFromLowDatavecsOps,collectTime) ");
					sBuffer.append("values('");
					sBuffer.append(ip);
					sBuffer.append("','");
					sBuffer.append(netAppConsistencyPoint.getCpTime());
					sBuffer.append("','");
					sBuffer.append(netAppConsistencyPoint.getCpFromTimerOps());
					sBuffer.append("','");
					sBuffer.append(netAppConsistencyPoint.getCpFromSnapshotOps());
					sBuffer.append("','");
					sBuffer.append(netAppConsistencyPoint.getCpFromLowWaterOps());
					sBuffer.append("','");
					sBuffer.append(netAppConsistencyPoint.getCpFromHighWaterOps());
					sBuffer.append("','");
					sBuffer.append(netAppConsistencyPoint.getCpFromLogFullOps());
					sBuffer.append("','");
					sBuffer.append(netAppConsistencyPoint.getCpFromCpOps());
					sBuffer.append("','");
					sBuffer.append(netAppConsistencyPoint.getCpTotalOps());
					sBuffer.append("','");
					sBuffer.append(netAppConsistencyPoint.getCpFromFlushOps());
					sBuffer.append("','");
					sBuffer.append(netAppConsistencyPoint.getCpFromSyncOps());
					sBuffer.append("','");
					sBuffer.append(netAppConsistencyPoint.getCpFromLowVbufOps());
					sBuffer.append("','");
					sBuffer.append(netAppConsistencyPoint.getCpFromCpDeferredOps());
					sBuffer.append("','");
					sBuffer.append(netAppConsistencyPoint.getCpFromLowDatavecsOps());
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sBuffer.append("','");
						sBuffer.append(time);
						sBuffer.append("')");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sBuffer.append("',");
						sBuffer.append("to_date('" + time + "','YYYY-MM-DD HH24:MI:SS')");
						sBuffer.append(")");
					}
					GathersqlListManager.Addsql(sBuffer.toString());
					sBuffer = null;
					netAppConsistencyPoint = null;
				}
			}
		}
		
		//dump
		if (ipdata.containsKey("dump")) {
			
			String deleteSQL="delete from netappdump where ipaddress='"+ip+"'";
			GathersqlListManager.Addsql(deleteSQL);
			
			Vector dumpVector = (Vector) ipdata.get("dump");
			if (dumpVector != null && dumpVector.size() > 0) {
				NetAppDump netAppDump = null;
				for (int si = 0; si < dumpVector.size(); si++) {
					netAppDump = (NetAppDump) dumpVector.elementAt(si);
					tempCal = (Calendar) netAppDump.getCollectTime();
					cc = tempCal.getTime();
					time = sdf.format(cc);
					tempCal = null;
					cc = null;
					StringBuffer sBuffer = new StringBuffer(200);
					sBuffer.append("insert into ");
					sBuffer.append("netappdump");
					sBuffer.append("(ipaddress,dmpActives,dmpAttempts,dmpSuccesses,dmpFailures,collectTime) ");
					sBuffer.append("values('");
					sBuffer.append(ip);
					sBuffer.append("','");
					sBuffer.append(netAppDump.getDmpActives());
					sBuffer.append("','");
					sBuffer.append(netAppDump.getDmpAttempts());
					sBuffer.append("','");
					sBuffer.append(netAppDump.getDmpSuccesses());
					sBuffer.append("','");
					sBuffer.append(netAppDump.getDmpFailures());
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sBuffer.append("','");
						sBuffer.append(time);
						sBuffer.append("')");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sBuffer.append("',");
						sBuffer.append("to_date('" + time + "','YYYY-MM-DD HH24:MI:SS')");
						sBuffer.append(")");
					}
					GathersqlListManager.Addsql(sBuffer.toString());
					sBuffer = null;
					netAppDump = null;
				}
			}
		}
		
		//dumpList
		if (ipdata.containsKey("dumpList")) {
			
			String deleteSQL="delete from netappdumplist where ipaddress='"+ip+"'";
			GathersqlListManager.Addsql(deleteSQL);
			
			Vector dumpListVector = (Vector) ipdata.get("dumpList");
			if (dumpListVector != null && dumpListVector.size() > 0) {
				NetAppDumpList netAppDumpList = null;
				for (int si = 0; si < dumpListVector.size(); si++) {
					netAppDumpList = (NetAppDumpList) dumpListVector.elementAt(si);
					tempCal = (Calendar) netAppDumpList.getCollectTime();
					cc = tempCal.getTime();
					time = sdf.format(cc);
					tempCal = null;
					cc = null;
					StringBuffer sBuffer = new StringBuffer(200);
					sBuffer.append("insert into ");
					sBuffer.append("netappdumplist");
					sBuffer.append("(ipaddress,dmpIndex,dmpStPath,dmpStAttempts,dmpStSuccesses,dmpStFailures,dmpTime,dmpStatus,dmpLevel,dmpNumFiles,dmpDataAmount,dmpStartTime,dmpDuration,collectTime) ");
					sBuffer.append("values('");
					sBuffer.append(ip);
					sBuffer.append("','");
					sBuffer.append(netAppDumpList.getDmpIndex());
					sBuffer.append("','");
					sBuffer.append(netAppDumpList.getDmpStPath());
					sBuffer.append("','");
					sBuffer.append(netAppDumpList.getDmpStAttempts());
					sBuffer.append("','");
					sBuffer.append(netAppDumpList.getDmpStSuccesses());
					sBuffer.append("','");
					sBuffer.append(netAppDumpList.getDmpStFailures());
					sBuffer.append("','");
					sBuffer.append(netAppDumpList.getDmpTime());
					sBuffer.append("','");
					sBuffer.append(netAppDumpList.getDmpStatus());
					sBuffer.append("','");
					sBuffer.append(netAppDumpList.getDmpLevel());
					sBuffer.append("','");
					sBuffer.append(netAppDumpList.getDmpNumFiles());
					sBuffer.append("','");
					sBuffer.append(netAppDumpList.getDmpDataAmount());
					sBuffer.append("','");
					sBuffer.append(netAppDumpList.getDmpStartTime());
					sBuffer.append("','");
					sBuffer.append(netAppDumpList.getDmpDuration());
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sBuffer.append("','");
						sBuffer.append(time);
						sBuffer.append("')");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sBuffer.append("',");
						sBuffer.append("to_date('" + time + "','YYYY-MM-DD HH24:MI:SS')");
						sBuffer.append(")");
					}
					GathersqlListManager.Addsql(sBuffer.toString());
					sBuffer = null;
					netAppDumpList = null;
				}
			}
		}
		
		
		//environment
		if (ipdata.containsKey("environment")) {
			
			String deleteSQL="delete from netappenvironment where ipaddress='"+ip+"'";
			GathersqlListManager.Addsql(deleteSQL);
			
			Vector environmentVector = (Vector) ipdata.get("environment");
			if (environmentVector != null && environmentVector.size() > 0) {
				NetAppEnvironment netAppEnvironment = null;
				for (int si = 0; si < environmentVector.size(); si++) {
					netAppEnvironment = (NetAppEnvironment) environmentVector.elementAt(si);
					tempCal = (Calendar) netAppEnvironment.getCollectTime();
					cc = tempCal.getTime();
					time = sdf.format(cc);
					tempCal = null;
					cc = null;
					StringBuffer sBuffer = new StringBuffer(200);
					sBuffer.append("insert into ");
					sBuffer.append("netappenvironment");
					sBuffer.append("(ipaddress,envOverTemperature,envFailedFanCount,envFailedFanMessage,envFailedPowerSupplyCount,envFailedPowerSupplyMessage,nvramBatteryStatus,collectTime) ");
					sBuffer.append("values('");
					sBuffer.append(ip);
					sBuffer.append("','");
					sBuffer.append(netAppEnvironment.getEnvOverTemperature());
					sBuffer.append("','");
					sBuffer.append(netAppEnvironment.getEnvFailedFanCount());
					sBuffer.append("','");
					sBuffer.append(netAppEnvironment.getEnvFailedFanMessage());
					sBuffer.append("','");
					sBuffer.append(netAppEnvironment.getEnvFailedPowerSupplyCount());
					sBuffer.append("','");
					sBuffer.append(netAppEnvironment.getEnvFailedPowerSupplyMessage());
					sBuffer.append("','");
					sBuffer.append(netAppEnvironment.getNvramBatteryStatus());
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sBuffer.append("','");
						sBuffer.append(time);
						sBuffer.append("')");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sBuffer.append("',");
						sBuffer.append("to_date('" + time + "','YYYY-MM-DD HH24:MI:SS')");
						sBuffer.append(")");
					}
					GathersqlListManager.Addsql(sBuffer.toString());
					sBuffer = null;
					netAppEnvironment = null;
				}
			}
		}
		
		//plex
		if (ipdata.containsKey("plex")) {
			
			String deleteSQL="delete from netappplex where ipaddress='"+ip+"'";
			GathersqlListManager.Addsql(deleteSQL);
			
			Vector plexVector = (Vector) ipdata.get("plex");
			if (plexVector != null && plexVector.size() > 0) {
				NetAppPlex netAppPlex = null;
				for (int si = 0; si < plexVector.size(); si++) {
					netAppPlex = (NetAppPlex) plexVector.elementAt(si);
					tempCal = (Calendar) netAppPlex.getCollectTime();
					cc = tempCal.getTime();
					time = sdf.format(cc);
					tempCal = null;
					cc = null;
					StringBuffer sBuffer = new StringBuffer(200);
					sBuffer.append("insert into ");
					sBuffer.append("netappplex");
					sBuffer.append("(ipaddress,plexIndex,plexName,plexStatus,plexPercentResyncing,collectTime) ");
					sBuffer.append("values('");
					sBuffer.append(ip);
					sBuffer.append("','");
					sBuffer.append(netAppPlex.getPlexIndex());
					sBuffer.append("','");
					sBuffer.append(netAppPlex.getPlexName());
					sBuffer.append("','");
					sBuffer.append(netAppPlex.getPlexStatus());
					sBuffer.append("','");
					sBuffer.append(netAppPlex.getPlexPercentResyncing());
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sBuffer.append("','");
						sBuffer.append(time);
						sBuffer.append("')");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sBuffer.append("',");
						sBuffer.append("to_date('" + time + "','YYYY-MM-DD HH24:MI:SS')");
						sBuffer.append(")");
					}
					GathersqlListManager.Addsql(sBuffer.toString());
					sBuffer = null;
					netAppPlex = null;
				}
			}
		}
		
		//quota
		if (ipdata.containsKey("quota")) {
			
			String deleteSQL="delete from netappquota where ipaddress='"+ip+"'";
			GathersqlListManager.Addsql(deleteSQL);
			
			Vector quotaVector = (Vector) ipdata.get("quota");
			if (quotaVector != null && quotaVector.size() > 0) {
				NetAppQuota netAppQuota = null;
				for (int si = 0; si < quotaVector.size(); si++) {
					netAppQuota = (NetAppQuota) quotaVector.elementAt(si);
					tempCal = (Calendar) netAppQuota.getCollectTime();
					cc = tempCal.getTime();
					time = sdf.format(cc);
					tempCal = null;
					cc = null;
					StringBuffer sBuffer = new StringBuffer(200);
					sBuffer.append("insert into ");
					sBuffer.append("netappquota");
					sBuffer.append("(ipaddress,quotaId,quotaName,quotaState,quotaInitPercent,collectTime) ");
					sBuffer.append("values('");
					sBuffer.append(ip);
					sBuffer.append("','");
					sBuffer.append(netAppQuota.getQuotaId());
					sBuffer.append("','");
					sBuffer.append(netAppQuota.getQuotaName());
					sBuffer.append("','");
					sBuffer.append(netAppQuota.getQuotaState());
					sBuffer.append("','");
					sBuffer.append(netAppQuota.getQuotaInitPercent());
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sBuffer.append("','");
						sBuffer.append(time);
						sBuffer.append("')");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sBuffer.append("',");
						sBuffer.append("to_date('" + time + "','YYYY-MM-DD HH24:MI:SS')");
						sBuffer.append(")");
					}
					GathersqlListManager.Addsql(sBuffer.toString());
					sBuffer = null;
					netAppQuota = null;
				}
			}
		}
		
		//restore
		if (ipdata.containsKey("restore")) {
			
			String deleteSQL="delete from netapprestore where ipaddress='"+ip+"'";
			GathersqlListManager.Addsql(deleteSQL);
			
			Vector restoreVector = (Vector) ipdata.get("restore");
			if (restoreVector != null && restoreVector.size() > 0) {
				NetAppRestore netAppRestore = null;
				for (int si = 0; si < restoreVector.size(); si++) {
					netAppRestore = (NetAppRestore) restoreVector.elementAt(si);
					tempCal = (Calendar) netAppRestore.getCollectTime();
					cc = tempCal.getTime();
					time = sdf.format(cc);
					tempCal = null;
					cc = null;
					StringBuffer sBuffer = new StringBuffer(200);
					sBuffer.append("insert into ");
					sBuffer.append("netapprestore");
					sBuffer.append("(ipaddress,rstActives,rstAttempts,rstSuccesses,rstFailures,collectTime) ");
					sBuffer.append("values('");
					sBuffer.append(ip);
					sBuffer.append("','");
					sBuffer.append(netAppRestore.getRstActives());
					sBuffer.append("','");
					sBuffer.append(netAppRestore.getRstAttempts());
					sBuffer.append("','");
					sBuffer.append(netAppRestore.getRstSuccesses());
					sBuffer.append("','");
					sBuffer.append(netAppRestore.getRstFailures());
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sBuffer.append("','");
						sBuffer.append(time);
						sBuffer.append("')");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sBuffer.append("',");
						sBuffer.append("to_date('" + time + "','YYYY-MM-DD HH24:MI:SS')");
						sBuffer.append(")");
					}
					GathersqlListManager.Addsql(sBuffer.toString());
					sBuffer = null;
					netAppRestore = null;
				}
			}
		}
		
		//snapshot
		if (ipdata.containsKey("snapshot")) {
			
			String deleteSQL="delete from netappsnapshot where ipaddress='"+ip+"'";
			GathersqlListManager.Addsql(deleteSQL);
			
			Vector snapshotVector = (Vector) ipdata.get("snapshot");
			if (snapshotVector != null && snapshotVector.size() > 0) {
				NetAppSnapshot netAppSnapshot = null;
				for (int si = 0; si < snapshotVector.size(); si++) {
					netAppSnapshot = (NetAppSnapshot) snapshotVector.elementAt(si);
					tempCal = (Calendar) netAppSnapshot.getCollectTime();
					cc = tempCal.getTime();
					time = sdf.format(cc);
					tempCal = null;
					cc = null;
					StringBuffer sBuffer = new StringBuffer(200);
					sBuffer.append("insert into ");
					sBuffer.append("netappsnapshot");
					sBuffer.append("(ipaddress,slVIndex,slVMonth,slVDay,slVHour,slVMinutes,slVName,slVVolume,slVNumber,slVVolumeName,slVType,collectTime) ");
					sBuffer.append("values('");
					sBuffer.append(ip);
					sBuffer.append("','");
					sBuffer.append(netAppSnapshot.getSlVIndex());
					sBuffer.append("','");
					sBuffer.append(netAppSnapshot.getSlVMonth());
					sBuffer.append("','");
					sBuffer.append(netAppSnapshot.getSlVDay());
					sBuffer.append("','");
					sBuffer.append(netAppSnapshot.getSlVHour());
					sBuffer.append("','");
					sBuffer.append(netAppSnapshot.getSlVMinutes());
					sBuffer.append("','");
					sBuffer.append(netAppSnapshot.getSlVName());
					sBuffer.append("','");
					sBuffer.append(netAppSnapshot.getSlVVolume());
					sBuffer.append("','");
					sBuffer.append(netAppSnapshot.getSlVNumber());
					sBuffer.append("','");
					sBuffer.append(netAppSnapshot.getSlVVolumeName());
					sBuffer.append("','");
					sBuffer.append(netAppSnapshot.getSlVType());
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sBuffer.append("','");
						sBuffer.append(time);
						sBuffer.append("')");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sBuffer.append("',");
						sBuffer.append("to_date('" + time + "','YYYY-MM-DD HH24:MI:SS')");
						sBuffer.append(")");
					}
					GathersqlListManager.Addsql(sBuffer.toString());
					sBuffer = null;
					netAppSnapshot = null;
				}
			}
		}
		
		//tree
		if (ipdata.containsKey("tree")) {
			
			String deleteSQL="delete from netapptree where ipaddress='"+ip+"'";
			GathersqlListManager.Addsql(deleteSQL);
			
			
			Vector treeVector = (Vector) ipdata.get("tree");
			if (treeVector != null && treeVector.size() > 0) {
				NetAppTree netAppTree = null;
				for (int si = 0; si < treeVector.size(); si++) {
					netAppTree = (NetAppTree) treeVector.elementAt(si);
					tempCal = (Calendar) netAppTree.getCollectTime();
					cc = tempCal.getTime();
					time = sdf.format(cc);
					tempCal = null;
					cc = null;
					StringBuffer sBuffer = new StringBuffer(200);
					sBuffer.append("insert into ");
					sBuffer.append("netapptree");
					sBuffer.append("(ipaddress,treeIndex,treeVolume,treeVolumeName,treeId,treeName,treeStatus,treeStyle,treeOpLocks,collectTime) ");
					sBuffer.append("values('");
					sBuffer.append(ip);
					sBuffer.append("','");
					sBuffer.append(netAppTree.getTreeIndex());
					sBuffer.append("','");
					sBuffer.append(netAppTree.getTreeVolume());
					sBuffer.append("','");
					sBuffer.append(netAppTree.getTreeVolumeName());
					sBuffer.append("','");
					sBuffer.append(netAppTree.getTreeId());
					sBuffer.append("','");
					sBuffer.append(netAppTree.getTreeName());
					sBuffer.append("','");
					sBuffer.append(netAppTree.getTreeStatus());
					sBuffer.append("','");
					sBuffer.append(netAppTree.getTreeStyle());
					sBuffer.append("','");
					sBuffer.append(netAppTree.getTreeOpLocks());
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sBuffer.append("','");
						sBuffer.append(time);
						sBuffer.append("')");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sBuffer.append("',");
						sBuffer.append("to_date('" + time + "','YYYY-MM-DD HH24:MI:SS')");
						sBuffer.append(")");
					}
					GathersqlListManager.Addsql(sBuffer.toString());
					sBuffer = null;
					netAppTree = null;
				}
			}
		}
		
		//vfiler
		if (ipdata.containsKey("vfiler")) {
			

			String deleteSQL="delete from netappvfiler where ipaddress='"+ip+"'";
			GathersqlListManager.Addsql(deleteSQL);
			
			Vector vfilerVector = (Vector) ipdata.get("vfiler");
			if (vfilerVector != null && vfilerVector.size() > 0) {
				NetAppVFiler netAppVFiler = null;
				for (int si = 0; si < vfilerVector.size(); si++) {
					netAppVFiler = (NetAppVFiler) vfilerVector.elementAt(si);
					tempCal = (Calendar) netAppVFiler.getCollectTime();
					cc = tempCal.getTime();
					time = sdf.format(cc);
					tempCal = null;
					cc = null;
					StringBuffer sBuffer = new StringBuffer(200);
					sBuffer.append("insert into ");
					sBuffer.append("netappvfiler");
					sBuffer.append("(ipaddress,vfIndex,vfName,vfUuid,vfIpAddresses,vfStoragePaths,vfIpSpace,vfAllowedProtocols,vfDisallowedProtocols,vfState,collectTime) ");
					sBuffer.append("values('");
					sBuffer.append(ip);
					sBuffer.append("','");
					sBuffer.append(netAppVFiler.getVfIndex());
					sBuffer.append("','");
					sBuffer.append(netAppVFiler.getVfName());
					sBuffer.append("','");
					sBuffer.append(netAppVFiler.getVfUuid());
					sBuffer.append("','");
					sBuffer.append(netAppVFiler.getVfIpAddresses());
					sBuffer.append("','");
					sBuffer.append(netAppVFiler.getVfStoragePaths());
					sBuffer.append("','");
					sBuffer.append(netAppVFiler.getVfIpSpace());
					sBuffer.append("','");
					sBuffer.append(netAppVFiler.getVfAllowedProtocols());
					sBuffer.append("','");
					sBuffer.append(netAppVFiler.getVfDisallowedProtocols());
					sBuffer.append("','");
					sBuffer.append(netAppVFiler.getVfState());
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sBuffer.append("','");
						sBuffer.append(time);
						sBuffer.append("')");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sBuffer.append("',");
						sBuffer.append("to_date('" + time + "','YYYY-MM-DD HH24:MI:SS')");
						sBuffer.append(")");
					}
					GathersqlListManager.Addsql(sBuffer.toString());
					sBuffer = null;
					netAppVFiler = null;
				}
			}
		}
		
		//vfilerIp
		if (ipdata.containsKey("vfilerIp")) {
			
			String deleteSQL="delete from netappvfileripentity where ipaddress='"+ip+"'";
			GathersqlListManager.Addsql(deleteSQL);
			
			Vector vfilerIpVector = (Vector) ipdata.get("vfilerIp");
			if (vfilerIpVector != null && vfilerIpVector.size() > 0) {
				NetAppVFilerIpEntity netAppVFilerIpEntity = null;
				for (int si = 0; si < vfilerIpVector.size(); si++) {
					netAppVFilerIpEntity = (NetAppVFilerIpEntity) vfilerIpVector.elementAt(si);
					tempCal = (Calendar) netAppVFilerIpEntity.getCollectTime();
					cc = tempCal.getTime();
					time = sdf.format(cc);
					tempCal = null;
					cc = null;
					StringBuffer sBuffer = new StringBuffer(200);
					sBuffer.append("insert into ");
					sBuffer.append("netappvfileripentity");
					sBuffer.append("(ipaddress,vfFiIndex,vfIpIndex,vfIpAddr,collectTime) ");
					sBuffer.append("values('");
					sBuffer.append(ip);
					sBuffer.append("','");
					sBuffer.append(netAppVFilerIpEntity.getVfFiIndex());
					sBuffer.append("','");
					sBuffer.append(netAppVFilerIpEntity.getVfIpIndex());
					sBuffer.append("','");
					sBuffer.append(netAppVFilerIpEntity.getVfIpAddr());
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sBuffer.append("','");
						sBuffer.append(time);
						sBuffer.append("')");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sBuffer.append("',");
						sBuffer.append("to_date('" + time + "','YYYY-MM-DD HH24:MI:SS')");
						sBuffer.append(")");
					}
					GathersqlListManager.Addsql(sBuffer.toString());
					sBuffer = null;
					netAppVFilerIpEntity = null;
				}
			}
		}
		
		//vfilerPath
		if (ipdata.containsKey("vfilerPath")) {
			
			String deleteSQL="delete from netappvfilerpathentity where ipaddress='"+ip+"'";
			GathersqlListManager.Addsql(deleteSQL);
			
			Vector vfilerPathVector = (Vector) ipdata.get("vfilerPath");
			if (vfilerPathVector != null && vfilerPathVector.size() > 0) {
				NetAppVFilerPathEntity netAppVFilerPathEntity = null;
				for (int si = 0; si < vfilerPathVector.size(); si++) {
					netAppVFilerPathEntity = (NetAppVFilerPathEntity) vfilerPathVector.elementAt(si);
					tempCal = (Calendar) netAppVFilerPathEntity.getCollectTime();
					cc = tempCal.getTime();
					time = sdf.format(cc);
					tempCal = null;
					cc = null;
					StringBuffer sBuffer = new StringBuffer(200);
					sBuffer.append("insert into ");
					sBuffer.append("netappvfilerpathentity");
					sBuffer.append("(ipaddress,vfFsIndex,vfSpIndex,vfSpName,collectTime) ");
					sBuffer.append("values('");
					sBuffer.append(ip);
					sBuffer.append("','");
					sBuffer.append(netAppVFilerPathEntity.getVfFsIndex());
					sBuffer.append("','");
					sBuffer.append(netAppVFilerPathEntity.getVfSpIndex());
					sBuffer.append("','");
					sBuffer.append(netAppVFilerPathEntity.getVfSpName());
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sBuffer.append("','");
						sBuffer.append(time);
						sBuffer.append("')");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sBuffer.append("',");
						sBuffer.append("to_date('" + time + "','YYYY-MM-DD HH24:MI:SS')");
						sBuffer.append(")");
					}
					GathersqlListManager.Addsql(sBuffer.toString());
					sBuffer = null;
					netAppVFilerPathEntity = null;
				}
			}
		}
		
		//vfilerProtocol
		if (ipdata.containsKey("vfilerProtocol")) {
			
			String deleteSQL="delete from netappvfilerprotocolentity where ipaddress='"+ip+"'";
			GathersqlListManager.Addsql(deleteSQL);
			
			Vector vfilerProtocolVector = (Vector) ipdata.get("vfilerProtocol");
			if (vfilerProtocolVector != null && vfilerProtocolVector.size() > 0) {
				NetAppVFilerProtocolEntity netAppVFilerProtocolEntity = null;
				for (int si = 0; si < vfilerProtocolVector.size(); si++) {
					netAppVFilerProtocolEntity = (NetAppVFilerProtocolEntity) vfilerProtocolVector.elementAt(si);
					tempCal = (Calendar) netAppVFilerProtocolEntity.getCollectTime();
					cc = tempCal.getTime();
					time = sdf.format(cc);
					tempCal = null;
					cc = null;
					StringBuffer sBuffer = new StringBuffer(200);
					sBuffer.append("insert into ");
					sBuffer.append("netappvfilerprotocolentity");
					sBuffer.append("(ipaddress,vfFpIndex,vfProIndex,vfProName,vfProStatus,collectTime) ");
					sBuffer.append("values('");
					sBuffer.append(ip);
					sBuffer.append("','");
					sBuffer.append(netAppVFilerProtocolEntity.getVfFpIndex());
					sBuffer.append("','");
					sBuffer.append(netAppVFilerProtocolEntity.getVfProIndex());
					sBuffer.append("','");
					sBuffer.append(netAppVFilerProtocolEntity.getVfProName());
					sBuffer.append("','");
					sBuffer.append(netAppVFilerProtocolEntity.getVfProStatus());
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sBuffer.append("','");
						sBuffer.append(time);
						sBuffer.append("')");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sBuffer.append("',");
						sBuffer.append("to_date('" + time + "','YYYY-MM-DD HH24:MI:SS')");
						sBuffer.append(")");
					}
					GathersqlListManager.Addsql(sBuffer.toString());
					sBuffer = null;
					netAppVFilerProtocolEntity = null;
				}
			}
		}
		
		//volume
		if (ipdata.containsKey("volume")) {
			
			String deleteSQL="delete from netappvolume where ipaddress='"+ip+"'";
			GathersqlListManager.Addsql(deleteSQL);
			
			Vector volumeVector = (Vector) ipdata.get("volume");
			if (volumeVector != null && volumeVector.size() > 0) {
				NetAppVolume netAppVolume = null;
				for (int si = 0; si < volumeVector.size(); si++) {
					netAppVolume = (NetAppVolume) volumeVector.elementAt(si);
					tempCal = (Calendar) netAppVolume.getCollectTime();
					cc = tempCal.getTime();
					time = sdf.format(cc);
					tempCal = null;
					cc = null;
					StringBuffer sBuffer = new StringBuffer(200);
					sBuffer.append("insert into ");
					sBuffer.append("netappvolume");
					sBuffer.append("(ipaddress,volIndex,volName,volFSID,volOwningHost,volState,volStatus,volOptions,volUUID,volAggrName,volType,collectTime) ");
					sBuffer.append("values('");
					sBuffer.append(ip);
					sBuffer.append("','");
					sBuffer.append(netAppVolume.getVolIndex());
					sBuffer.append("','");
					sBuffer.append(netAppVolume.getVolName());
					sBuffer.append("','");
					sBuffer.append(netAppVolume.getVolFSID());
					sBuffer.append("','");
					sBuffer.append(netAppVolume.getVolOwningHost());
					sBuffer.append("','");
					sBuffer.append(netAppVolume.getVolState());
					sBuffer.append("','");
					sBuffer.append(netAppVolume.getVolStatus());
					sBuffer.append("','");
					sBuffer.append(netAppVolume.getVolOptions());
					sBuffer.append("','");
					sBuffer.append(netAppVolume.getVolUUID());
					sBuffer.append("','");
					sBuffer.append(netAppVolume.getVolAggrName());
					sBuffer.append("','");
					sBuffer.append(netAppVolume.getVolType());
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sBuffer.append("','");
						sBuffer.append(time);
						sBuffer.append("')");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sBuffer.append("',");
						sBuffer.append("to_date('" + time + "','YYYY-MM-DD HH24:MI:SS')");
						sBuffer.append(")");
					}
					GathersqlListManager.Addsql(sBuffer.toString());
					sBuffer = null;
					netAppVolume = null;
				}
			}
		}
		
		//spare
		if (ipdata.containsKey("spare")) {
			
			String deleteSQL="delete from netappspare where ipaddress='"+ip+"'";
			GathersqlListManager.Addsql(deleteSQL);
			
			Vector spareVector = (Vector) ipdata.get("spare");
			if (spareVector != null && spareVector.size() > 0) {
				NetAppSpare netAppSpare = null;
				for (int si = 0; si < spareVector.size(); si++) {
					netAppSpare = (NetAppSpare) spareVector.elementAt(si);
					tempCal = (Calendar) netAppSpare.getCollectTime();
					cc = tempCal.getTime();
					time = sdf.format(cc);
					tempCal = null;
					cc = null;
					StringBuffer sBuffer = new StringBuffer(200);
					sBuffer.append("insert into ");
					sBuffer.append("netappspare");
					sBuffer.append("(ipaddress,spareIndex,spareDiskName,spareStatus,spareDiskId,spareScsiAdapter,spareScsiId,spareTotalMb,spareTotalBlocks,spareDiskPort,spareSecondaryDiskName,spareSecondaryDiskPort,spareShelf,spareBay,sparePool,spareSectorSize,spareDiskSerialNumber,spareDiskVendor,spareDiskModel,spareDiskFirmwareRevision,spareDiskRPM,spareDiskType,collectTime) ");
					sBuffer.append("values('");
					sBuffer.append(ip);
					sBuffer.append("','");
					sBuffer.append(netAppSpare.getSpareIndex());
					sBuffer.append("','");
					sBuffer.append(netAppSpare.getSpareDiskName());
					sBuffer.append("','");
					sBuffer.append(netAppSpare.getSpareStatus());
					sBuffer.append("','");
					sBuffer.append(netAppSpare.getSpareDiskId());
					sBuffer.append("','");
					sBuffer.append(netAppSpare.getSpareScsiAdapter());
					sBuffer.append("','");
					sBuffer.append(netAppSpare.getSpareScsiId());
					sBuffer.append("','");
					sBuffer.append(netAppSpare.getSpareTotalMb());
					sBuffer.append("','");
					sBuffer.append(netAppSpare.getSpareTotalBlocks());
					sBuffer.append("','");
					sBuffer.append(netAppSpare.getSpareDiskPort());
					sBuffer.append("','");
					sBuffer.append(netAppSpare.getSpareSecondaryDiskName());
					sBuffer.append("','");
					sBuffer.append(netAppSpare.getSpareSecondaryDiskPort());
					sBuffer.append("','");
					sBuffer.append(netAppSpare.getSpareShelf());
					sBuffer.append("','");
					sBuffer.append(netAppSpare.getSpareBay());
					sBuffer.append("','");
					sBuffer.append(netAppSpare.getSparePool());
					sBuffer.append("','");
					sBuffer.append(netAppSpare.getSpareSectorSize());
					sBuffer.append("','");
					sBuffer.append(netAppSpare.getSpareDiskSerialNumber());
					sBuffer.append("','");
					sBuffer.append(netAppSpare.getSpareDiskVendor());
					sBuffer.append("','");
					sBuffer.append(netAppSpare.getSpareDiskModel());
					sBuffer.append("','");
					sBuffer.append(netAppSpare.getSpareDiskFirmwareRevision());
					sBuffer.append("','");
					sBuffer.append(netAppSpare.getSpareDiskRPM());
					sBuffer.append("','");
					sBuffer.append(netAppSpare.getSpareDiskType());
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sBuffer.append("','");
						sBuffer.append(time);
						sBuffer.append("')");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sBuffer.append("',");
						sBuffer.append("to_date('" + time + "','YYYY-MM-DD HH24:MI:SS')");
						sBuffer.append(")");
					}
					GathersqlListManager.Addsql(sBuffer.toString());
					sBuffer = null;
					netAppSpare = null;
				}
			}
		}
		
		//raid
		if (ipdata.containsKey("raid")) {
			
			String deleteSQL="delete from netappraid where ipaddress='"+ip+"'";
			GathersqlListManager.Addsql(deleteSQL);
			
			Vector raidVector = (Vector) ipdata.get("raid");
			if (raidVector != null && raidVector.size() > 0) {
				NetAppRaid netAppRaid = null;
				for (int si = 0; si < raidVector.size(); si++) {
					netAppRaid = (NetAppRaid) raidVector.elementAt(si);
					tempCal = (Calendar) netAppRaid.getCollectTime();
					cc = tempCal.getTime();
					time = sdf.format(cc);
					tempCal = null;
					cc = null;
					StringBuffer sBuffer = new StringBuffer(200);
					sBuffer.append("insert into ");
					sBuffer.append("netappraid");
					sBuffer.append("(ipaddress,raidVIndex,raidVDiskName,raidVStatus,raidVDiskId,raidVScsiAdapter,raidVScsiId,raidVUsedMb,raidVUsedBlocks,raidVTotalMb,raidVTotalBlocks,raidVCompletionPerCent,raidVVol,raidVGroup,raidVDiskNumber,raidVGroupNumber,raidVDiskPort,raidVSecondaryDiskName,raidVSecondaryDiskPort,raidVShelf,raidVBay,raidVPlex,raidVPlexGroup,raidVPlexNumber,raidVPlexName,raidVSectorSize,raidVDiskSerialNumber,raidVDiskVendor,raidVDiskModel,raidVDiskFirmwareRevision,raidVDiskRPM,raidVDiskType,raidVDiskPool,raidVDiskCopyDestDiskName,collectTime) ");
					sBuffer.append("values('");
					sBuffer.append(ip);
					sBuffer.append("','");
					sBuffer.append(netAppRaid.getRaidVIndex());
					sBuffer.append("','");
					sBuffer.append(netAppRaid.getRaidVDiskName());
					sBuffer.append("','");
					sBuffer.append(netAppRaid.getRaidVStatus());
					sBuffer.append("','");
					sBuffer.append(netAppRaid.getRaidVDiskId());
					sBuffer.append("','");
					sBuffer.append(netAppRaid.getRaidVScsiAdapter());
					sBuffer.append("','");
					sBuffer.append(netAppRaid.getRaidVScsiId());
					sBuffer.append("','");
					sBuffer.append(netAppRaid.getRaidVUsedMb());
					sBuffer.append("','");
					sBuffer.append(netAppRaid.getRaidVUsedBlocks());
					sBuffer.append("','");
					sBuffer.append(netAppRaid.getRaidVTotalMb());
					sBuffer.append("','");
					sBuffer.append(netAppRaid.getRaidVTotalBlocks());
					sBuffer.append("','");
					sBuffer.append(netAppRaid.getRaidVCompletionPerCent());
					sBuffer.append("','");
					sBuffer.append(netAppRaid.getRaidVVol());
					sBuffer.append("','");
					sBuffer.append(netAppRaid.getRaidVGroup());
					sBuffer.append("','");
					sBuffer.append(netAppRaid.getRaidVDiskNumber());
					sBuffer.append("','");
					sBuffer.append(netAppRaid.getRaidVGroupNumber());
					sBuffer.append("','");
					sBuffer.append(netAppRaid.getRaidVDiskPort());
					sBuffer.append("','");
					sBuffer.append(netAppRaid.getRaidVSecondaryDiskName());
					sBuffer.append("','");
					sBuffer.append(netAppRaid.getRaidVSecondaryDiskPort());
					sBuffer.append("','");
					sBuffer.append(netAppRaid.getRaidVShelf());
					sBuffer.append("','");
					sBuffer.append(netAppRaid.getRaidVBay());
					sBuffer.append("','");
					sBuffer.append(netAppRaid.getRaidVPlex());
					sBuffer.append("','");
					sBuffer.append(netAppRaid.getRaidVPlexGroup());
					sBuffer.append("','");
					sBuffer.append(netAppRaid.getRaidVPlexNumber());
					sBuffer.append("','");
					sBuffer.append(netAppRaid.getRaidVPlexName());
					sBuffer.append("','");
					sBuffer.append(netAppRaid.getRaidVSectorSize());
					sBuffer.append("','");
					sBuffer.append(netAppRaid.getRaidVDiskSerialNumber());
					sBuffer.append("','");
					sBuffer.append(netAppRaid.getRaidVDiskVendor());
					sBuffer.append("','");
					sBuffer.append(netAppRaid.getRaidVDiskModel());
					sBuffer.append("','");
					sBuffer.append(netAppRaid.getRaidVDiskFirmwareRevision());
					sBuffer.append("','");
					sBuffer.append(netAppRaid.getRaidVDiskRPM());
					sBuffer.append("','");
					sBuffer.append(netAppRaid.getRaidVDiskType());
					sBuffer.append("','");
					sBuffer.append(netAppRaid.getRaidVDiskPool());
					sBuffer.append("','");
					sBuffer.append(netAppRaid.getRaidVDiskCopyDestDiskName());
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sBuffer.append("','");
						sBuffer.append(time);
						sBuffer.append("')");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sBuffer.append("',");
						sBuffer.append("to_date('" + time + "','YYYY-MM-DD HH24:MI:SS')");
						sBuffer.append(")");
					}
					GathersqlListManager.Addsql(sBuffer.toString());
					sBuffer = null;
					netAppRaid = null;
				}
			}
		}
		
		// disk
		if (ipdata.containsKey("disk")) {
			
			String deleteSQL="delete from netappdisk where ipaddress='"+ip+"'";
			GathersqlListManager.Addsql(deleteSQL);
			
			Vector diskVector = (Vector) ipdata.get("disk");
			if (diskVector != null && diskVector.size() > 0) {
				NetAppDisk disk = null;
				for (int si = 0; si < diskVector.size(); si++) {
					disk = (NetAppDisk) diskVector.elementAt(si);
					tempCal = (Calendar) disk.getCollectTime();
					cc = tempCal.getTime();
					time = sdf.format(cc);
					tempCal = null;
					cc = null;
					StringBuffer sBuffer = new StringBuffer(200);
					sBuffer.append("insert into ");
					sBuffer.append("netappdisk");
					sBuffer.append("(ipaddress,dfIndex,dfFileSys,dfKBytesTotal,dfKBytesUsed,dfKBytesAvail,dfPerCentKBytesCapacity,dfInodesUsed,dfInodesFree,dfPerCentInodeCapacity,dfMountedOn,dfMaxFilesAvail,dfMaxFilesUsed,dfMaxFilesPossible,dfHighTotalKBytes,dfLowTotalKBytes,dfHighUsedKBytes,dfLowUsedKBytes,dfHighAvailKBytes,dfLowAvailKBytes,dfStatus,dfMirrorStatus,dfPlexCount,dfType,collectTime) ");
					sBuffer.append("values('");
					sBuffer.append(ip);
					sBuffer.append("','");
					sBuffer.append(disk.getDfIndex());
					sBuffer.append("','");
					sBuffer.append(disk.getDfFileSys());
					sBuffer.append("','");
					sBuffer.append(disk.getDfKBytesTotal());
					sBuffer.append("','");
					sBuffer.append(disk.getDfKBytesUsed());
					sBuffer.append("','");
					sBuffer.append(disk.getDfKBytesAvail());
					sBuffer.append("','");
					sBuffer.append(disk.getDfPerCentKBytesCapacity());
					sBuffer.append("','");
					sBuffer.append(disk.getDfInodesUsed());
					sBuffer.append("','");
					sBuffer.append(disk.getDfInodesFree());
					sBuffer.append("','");
					sBuffer.append(disk.getDfPerCentInodeCapacity());
					sBuffer.append("','");
					sBuffer.append(disk.getDfMountedOn());
					sBuffer.append("','");
					sBuffer.append(disk.getDfMaxFilesAvail());
					sBuffer.append("','");
					sBuffer.append(disk.getDfMaxFilesUsed());
					sBuffer.append("','");
					sBuffer.append(disk.getDfMaxFilesPossible());
					sBuffer.append("','");
					sBuffer.append(disk.getDfHighTotalKBytes());
					sBuffer.append("','");
					sBuffer.append(disk.getDfLowTotalKBytes());
					sBuffer.append("','");
					sBuffer.append(disk.getDfHighUsedKBytes());
					sBuffer.append("','");
					sBuffer.append(disk.getDfLowUsedKBytes());
					sBuffer.append("','");
					sBuffer.append(disk.getDfHighAvailKBytes());
					sBuffer.append("','");
					sBuffer.append(disk.getDfLowAvailKBytes());
					sBuffer.append("','");
					sBuffer.append(disk.getDfStatus());
					sBuffer.append("','");
					sBuffer.append(disk.getDfMirrorStatus());
					sBuffer.append("','");
					sBuffer.append(disk.getDfPlexCount());
					sBuffer.append("','");
					sBuffer.append(disk.getDfType());
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sBuffer.append("','");
						sBuffer.append(time);
						sBuffer.append("')");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sBuffer.append("',");
						sBuffer.append("to_date('" + time + "','YYYY-MM-DD HH24:MI:SS')");
						sBuffer.append(")");
					}
					GathersqlListManager.Addsql(sBuffer.toString());
					sBuffer = null;
					disk = null;
				}
			}
		}
	}

}