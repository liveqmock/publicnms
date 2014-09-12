package com.afunms.polling.impl;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.afunms.application.model.DominoCache;
import com.afunms.application.model.DominoDb;
import com.afunms.application.model.DominoDisk;
import com.afunms.application.model.DominoHttp;
import com.afunms.application.model.DominoLdap;
import com.afunms.application.model.DominoMail;
import com.afunms.application.model.DominoMem;
import com.afunms.application.model.DominoServer;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.SysLogger;
import com.afunms.polling.om.Pingcollectdata;

public class ProcessDominoData {

	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	public ProcessDominoData() {
	}

	/**
	 * windows
	 * 
	 * @param type
	 * @param datahash
	 * @return
	 */
	public boolean processDominoData(String ip, Hashtable datahash) {
		DominoMem mem = new DominoMem();
		DominoMail mail = new DominoMail();

		DominoServer server = new DominoServer();
		DominoDb db = new DominoDb();
		DominoCache cache = new DominoCache();
        Pingcollectdata ping=new Pingcollectdata();
		List diskList = new ArrayList();
		DBManager dbmanager = new DBManager();
		if (datahash != null && datahash.size() > 0) {
			if (datahash.containsKey("Mem")) {
				mem = (DominoMem) datahash.get("Mem");
				String deleteSql = "delete from nms_dominomem_realtime where ipaddress='"
						+ ip + "'";
				dbmanager.addBatch(deleteSql);
				if (mem != null) {
					try {
						StringBuffer sql = new StringBuffer(500);
						sql.append("insert into nms_dominomem_realtime(IPADDRESS,MEMALLOCATE,MEMALLOCATEPROCESS,MEMALLOCATESHARE,MEMPHYSICAL"+
								",MEMFREE,PLATFORMMEMPHYPCTUTIL,PLATFORMMEMPHYSICAL,MEMPCTUTIL)values('");
						sql.append(ip);
						sql.append("','");
						sql.append(mem.getMemAllocate());//
						sql.append("','");
						sql.append(mem.getMemAllocateProcess());//
						sql.append("','");
						sql.append(mem.getMemAllocateShare());//
						sql.append("','");
						sql.append(mem.getMemPhysical());// 
						 
						sql.append("','");
						sql.append(mem.getMemFree());//
						sql.append("','");
						sql.append(mem.getPlatformMemPhyPctUtil());//
						sql.append("','");
						sql.append(mem.getPlatformMemPhysical());//
						sql.append("','");
						sql.append(mem.getMempctutil());//
						sql.append("')");

						dbmanager.addBatch(sql.toString());

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			if (datahash.containsKey("Mail")) {
				mail = (DominoMail) datahash.get("Mail");
				String deleteSql = "delete from nms_dominomail_realtime where ipaddress='"
						+ ip + "'";
				dbmanager.addBatch(deleteSql);
				if (mail != null) {
					try {
						StringBuffer sql = new StringBuffer(500);
						sql.append("insert into nms_dominomail_realtime(IPADDRESS,MAILDEAD,MAILWAITING,MAILWAITINGRECIPIENTS,MAILDELIVERRATE,MAILTRANSFERRATE,MAILDELIVERTHREADSMAX,MAILDELIVERTHREADSTOTAL,MAILTRANSFERTHREADSMAX,MAILTRANSFERTHREADSTOTAL,MAILAVGSIZE,MAILAVGTIME)values('");
						sql.append(ip);
						sql.append("','");
						sql.append(mail.getMailDead());//
						sql.append("','");
						sql.append(mail.getMailWaiting());//
						sql.append("','");
						sql.append(mail.getMailWaitingRecipients());//
						sql.append("','");
						sql.append(mail.getMailDeliverRate());// cSDVersion
						sql.append("','");
						sql.append(mail.getMailTransferRate());//
						sql.append("','");
						sql.append(mail.getMailDeliverThreadsMax());//
						sql.append("','");
						sql.append(mail.getMailDeliverThreadsTotal());//
						sql.append("','");
						sql.append(mail.getMailTransferThreadsMax());//
						sql.append("','");
						sql.append(mail.getMailTransferThreadsTotal());//
						sql.append("','");
						sql.append(mail.getMailAvgSize());//
						sql.append("','");
						sql.append(mail.getMailAvgTime());//
						sql.append("')");

						dbmanager.addBatch(sql.toString());

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			if (datahash.containsKey("LDAP")) {
				DominoLdap ldap = new DominoLdap();
				ldap = (DominoLdap) datahash.get("LDAP");
				String deleteSql = "delete from nms_dominoldap_realtime where ipaddress='"
						+ ip + "'";
				dbmanager.addBatch(deleteSql);
				if (ldap != null) {
					try {
						StringBuffer sql = new StringBuffer(500);
						sql
								.append("insert into nms_dominoldap_realtime(IPADDRESS,LDAPRUNNING,LDAPINBOUNDQUE,LDAPINBOUNDACTIVE,LDAPINBOUNDACTIVESSL,LDAPINBOUNDBYTESRESEIVED,LDAPINBOUNDBYTESSENT,LDAPINBOUNDPEAK,LDAPINBOUNDPEAKSSL,LDAPINBOUNDTOTAL,LDAPINBOUNDTOTALSSL,LDAPBADHANDSHAKE,LDAPTHREADSBUSY,LDAPTHREADSLDLE,LDAPTHREADSINPOOL,LDAPTHREADSPEAK)values('");
						sql.append(ip);
						sql.append("','");
						sql.append(ldap.getLdapRunning());//
						sql.append("','");
						sql.append(ldap.getLdapInboundQue());//
						sql.append("','");
						sql.append(ldap.getLdapInboundActive());//
						sql.append("','");
						sql.append(ldap.getLdapInboundActiveSSL());//
						sql.append("','");
						sql.append(ldap.getLdapInboundBytesReseived());//
						sql.append("','");
						sql.append(ldap.getLdapInboundBytesSent());//
						sql.append("','");
						sql.append(ldap.getLdapInboundPeak());//
						sql.append("','");
						sql.append(ldap.getLdapInboundPeakSSL());//
						sql.append("','");
						sql.append(ldap.getLdapInboundTotal());//
						sql.append("','");
						sql.append(ldap.getLdapInboundTotalSSL());//
						sql.append("','");
						sql.append(ldap.getLdapBadHandShake());//
						sql.append("','");
						sql.append(ldap.getLdapThreadsBusy());//
						sql.append("','");
						sql.append(ldap.getLdapThreadsIdle());//
						sql.append("','");
						sql.append(ldap.getLdapThreadsInPool());//
						sql.append("','");
						sql.append(ldap.getLdapTHreadsPeak());//
						sql.append("')");

						dbmanager.addBatch(sql.toString());

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			if (datahash.containsKey("Disk")) {

				diskList = (List) datahash.get("Disk");
				String deleteSql = "delete from nms_dominodisk_realtime where ipaddress='"
						+ ip + "'";
				dbmanager.addBatch(deleteSql);
				if (diskList != null && diskList.size() > 0) {
					for (int i = 0; i < diskList.size(); i++) {
						DominoDisk disk = (DominoDisk) diskList.get(i);
						if (disk != null) {
							try {
								StringBuffer sql = new StringBuffer(500);
								sql
										.append("insert into nms_dominodisk_realtime(IPADDRESS,DISKNAME,DISKSIZE,DISKFREE,DISKUSEDPCTUTIL,DISKTYPE)values('");
								sql.append(ip);
								sql.append("','");
								sql.append(disk.getDiskname());//
								sql.append("','");
								sql.append(disk.getDisksize());//
								sql.append("','");
								sql.append(disk.getDiskfree());//
								sql.append("','");
								sql.append(disk.getDiskusedpctutil());//
								sql.append("','");
								sql.append(disk.getDisktype());//
								sql.append("')");

								dbmanager.addBatch(sql.toString());

							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}

				}

			}

			if (datahash.containsKey("HTTP")) {
				DominoHttp http = new DominoHttp();
				http = (DominoHttp) datahash.get("HTTP");
				String deleteSql = "delete from nms_dominohttp_realtime where ipaddress='"
						+ ip + "'";
				dbmanager.addBatch(deleteSql);
				if (http != null) {
					try {
						StringBuffer sql = new StringBuffer(500);
						sql
								.append("insert into nms_dominohttp_realtime(IPADDRESS,HTTPACCEPT,HTTPREFUSED,HTTPCURRENTCON,HTTPMAXCON,HTTPPEAKCON,HTTPWORKERREQUEST,HTTPWORKERREQUESTTIME,HTTPWORKERBYTESREAD,HTTPWORKERBYTESWRITTEN,HTTPWORKERREQUESTPROCESS,HTTPWORKERTOTALREQUEST,HTTPERRORURL)values('");
						sql.append(ip);
						sql.append("','");

						sql.append(http.getHttpAccept());
						sql.append("','");
						sql.append(http.getHttpRefused());
						sql.append("','");
						sql.append(http.getHttpCurrentCon());
						sql.append("','");
						sql.append(http.getHttpMaxCon());
						sql.append("','");
						sql.append(http.getHttpPeakCon());
						sql.append("','");
						sql.append(http.getHttpWorkerRequest());
						sql.append("','");
						sql.append(http.getHttpWorkerRequestTime());
						sql.append("','");
						sql.append(http.getHttpWorkerBytesRead());
						sql.append("','");
						sql.append(http.getHttpWorkerBytesWritten());
						sql.append("','");
						sql.append(http.getHttpWorkerRequestProcess());
						sql.append("','");
						sql.append(http.getHttpWorkerTotalRequest());
						sql.append("','");
						sql.append(http.getHttpErrorUrl());
						sql.append("')");

						dbmanager.addBatch(sql.toString());

					} catch (Exception e) {
						e.printStackTrace();

					}
				}

			}
			if (datahash.containsKey("Database")) {

				db = (DominoDb) datahash.get("Database");
				String deleteSql = "delete from nms_dominodb_realtime where ipaddress='"
						+ ip + "'";
				dbmanager.addBatch(deleteSql);
				if (db != null) {
					try {
						StringBuffer sql = new StringBuffer(500);
						sql
								.append("insert into nms_dominodb_realtime(IPADDRESS,DBBUFFPOOLMAX,DBBUFFPOOLPEAK,DBBUFFPOOLREADS,DBBUFFPOOLWRITES,DBBUFFPOOLREADHIT,DBCACHEENTRY,DBCACHEWATERMARK,DBCACHEHIT,DBCACHEDBOPEN,DBNIFPOOLPEAK,DBNIFPOOLUSE,DBNSFPOOLPEAK,DBNSFPOOLUSE)values('");
						sql.append(ip);
						sql.append("','");
						sql.append(db.getDbBuffPoolMax());
						sql.append("','");
						sql.append(db.getDbBuffPoolPeak());
						sql.append("','");
						sql.append(db.getDbBuffPoolReads());
						sql.append("','");
						sql.append(db.getDbBuffPoolWrites());
						sql.append("','");
						sql.append(db.getDbBuffPoolReadHit());
						sql.append("','");
						sql.append(db.getDbCacheEntry());
						sql.append("','");
						sql.append(db.getDbCacheWaterMark());
						sql.append("','");
						sql.append(db.getDbCacheHit());
						sql.append("','");
						sql.append(db.getDbCacheDbOpen());
						sql.append("','");
						sql.append(db.getDbNifPoolPeak());
						sql.append("','");
						sql.append(db.getDbNifPoolUse());
						sql.append("','");
						sql.append(db.getDbNsfPoolPeak());
						sql.append("','");
						sql.append(db.getDbNsfPoolUse());
						sql.append("')");

						dbmanager.addBatch(sql.toString());

					} catch (Exception e) {
						e.printStackTrace();

					}
				}

			}
			if (datahash.containsKey("Cache")) {

				cache = (DominoCache) datahash.get("Cache");
				String deleteSql = "delete from nms_dominocache_realtime where ipaddress='"
						+ ip + "'";
				dbmanager.addBatch(deleteSql);
				if (cache != null) {
					try {
						StringBuffer sql = new StringBuffer(500);
						sql.append("insert into nms_dominocache_realtime(IPADDRESS,CACHECOMMANDCOUNT,CACHECOMMANDDISRATE,CACHECOMMANDHITRATE," +
								"CACHECOMMANDSIZE,CACHEDBHITRATE,CACHESESSIONCOUNT,CACHESESSIONDISRATE,CACHESESSIONHITRATE,CACHESESSIONSIZE"+"" +
										",CACHEUSERCOUNT,CACHEUSERDISRATE,CACHEUSERHITRATE,CACHEUSRSIZE)values('");
						sql.append(ip);
						sql.append("','");
						sql.append(cache.getCacheCommandCount());
						sql.append("','");
						sql.append(cache.getCacheCommandDisRate());
						sql.append("','");
						sql.append(cache.getCacheCommandHitRate());
						sql.append("','");
						sql.append(cache.getCacheCommandSize());
						sql.append("','");
						sql.append(cache.getCacheDbHitRate());
						sql.append("','");
						sql.append(cache.getCacheSessionCount());
						sql.append("','");
						sql.append(cache.getCacheSessionDisRate());
						sql.append("','");
						sql.append(cache.getCacheSessionHitRate());
						sql.append("','");
						sql.append(cache.getCacheSessionSize());
						sql.append("','");
						sql.append(cache.getCacheUserCount());
						sql.append("','");
						sql.append(cache.getCacheUserDisRate());
						sql.append("','");
						sql.append(cache.getCacheUserHitRate());
						sql.append("','");
						sql.append(cache.getCacheUserSize());

						sql.append("')");

						dbmanager.addBatch(sql.toString());

					} catch (Exception e) {
						e.printStackTrace();

					}
				}

			}
			if (datahash.containsKey("Server")) {
				
				server = (DominoServer) datahash.get("Server");
				String deleteSql = "delete from nms_dominoserver_realtime where ipaddress='"
					+ ip + "'";
				dbmanager.addBatch(deleteSql);
				if (server != null) {
					try {
						StringBuffer sql = new StringBuffer(500);
						sql.append("insert into nms_dominoserver_realtime(IPADDRESS,NAME,TITLE,OS,ARCHITECTURE,STARTTIME,CPUTYPE,CPUCOUNT," +
								"PORTNUMBER,CPUPCTUTIL,IMAPSTATUS,LDAPSTATUS,POP3STATUS,SMTPSTATUS,AVAILABILITYINDEX,SESSIONSDROPPED,"+
						"TASKS,TRANSPERMINUTE,REQUESTSPER1HOUR)values('");
						sql.append(ip);
						sql.append("','");
						sql.append(server.getName());
						sql.append("','");
						sql.append(server.getTitle());
						sql.append("','");
						sql.append(server.getOs());
						sql.append("','");
						sql.append(server.getArchitecture());
						sql.append("','");
						sql.append(server.getStarttime());
						sql.append("','");
						sql.append(server.getCputype());
						sql.append("','");
						sql.append(server.getCpucount());
						sql.append("','");
						sql.append(server.getPortnumber());
						sql.append("','");
						sql.append(server.getCpupctutil());
						sql.append("','");
						sql.append(server.getImapstatus());
						sql.append("','");
						sql.append(server.getLdapstatus());
						sql.append("','");
						sql.append(server.getPop3status());
						sql.append("','");
						sql.append(server.getSmtpstatus());
						sql.append("','");
						sql.append(server.getAvailabilityIndex());
						sql.append("','");
						sql.append(server.getSessionsDropped());
						sql.append("','");
						sql.append(server.getTasks());
						sql.append("','");
						sql.append(server.getTransPerMinute());
						sql.append("','");
						sql.append(server.getRequestsPer1Hour());
						sql.append("')");
						
						dbmanager.addBatch(sql.toString());
						
					} catch (Exception e) {
						e.printStackTrace();
						
					}
				}
				
			}
			if(datahash.containsKey("Ping")){
				ping = (Pingcollectdata) datahash.get("Ping");
				String deleteSql = "delete from nms_dominoping_realtime where ipaddress='"
					+ ip + "'";
				dbmanager.addBatch(deleteSql);
				String tablename = "nms_dominoping_realtime" ;
				String value=ping.getThevalue();
				StringBuffer sBuffer = new StringBuffer();
				sBuffer.append("insert into ");
				sBuffer.append(tablename);
				sBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
				sBuffer.append("values('");
				sBuffer.append(ip);
				sBuffer.append("','");
				sBuffer.append("dynamic");
				sBuffer.append("','");
				sBuffer.append("domPing");
				sBuffer.append("','");
				sBuffer.append("Utilization");
				sBuffer.append("','");
				sBuffer.append("ConnectUtilization");
				sBuffer.append("','");
				sBuffer.append("%");
				sBuffer.append("','");
				sBuffer.append("");
				sBuffer.append("','");
				sBuffer.append("");
				sBuffer.append("','");
				sBuffer.append("0");
				sBuffer.append("','");
				sBuffer.append(value);
				sBuffer.append("','");
				sBuffer.append("");
				sBuffer.append("')");
				try {
					if (!value.equals("")&&value!=null){
					dbmanager.addBatch(sBuffer.toString());
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				sBuffer = null;
				tablename = null;
			}
			dbmanager.executeBatch();
		}

		return true;
	}
}
