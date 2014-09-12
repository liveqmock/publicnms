package com.afunms.polling.snmp;

/*
 * @author hukelei@dhcc.com.cn
 *
 */

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import com.afunms.application.dao.DBDao;
import com.afunms.application.model.DominoCache;
import com.afunms.application.model.DominoConfig;
import com.afunms.application.model.DominoDb;
import com.afunms.application.model.DominoDisk;
import com.afunms.application.model.DominoHttp;
import com.afunms.application.model.DominoLdap;
import com.afunms.application.model.DominoMail;
import com.afunms.application.model.DominoMem;
import com.afunms.application.model.DominoServer;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.common.util.SysLogger;
import com.afunms.config.model.Procs;
import com.afunms.detail.service.syslogInfo.SyslogInfoService;
import com.afunms.monitor.executor.base.MonitorInterface;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.om.Pingcollectdata;
import com.afunms.topology.model.HostNode;


/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DominoSnmp extends SnmpMonitor implements MonitorInterface {
	private  String host="1.1.1.1";
	private Hashtable sendeddata = ShareData.getProcsendeddata();
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 
	 */
	public DominoSnmp() {
	}

	   public void collectData(Node node,MonitoredItem item){
		   
	   }
	   public Hashtable collect_Data(HostNode node){
		   return null;
	   }
	   public void collectData(HostNode node){
		   
	   }
	/* (non-Javadoc)
	 * @see com.dhcc.webnms.host.snmp.AbstractSnmp#collectData()
	 */
	public Hashtable collect_Data(DominoConfig dominoconf) {		
		Hashtable returnHas = new Hashtable();
		DominoMail dominoMail = new DominoMail();
		DominoLdap dominoLdap = new DominoLdap();
		DominoHttp dominoHttp = new DominoHttp();;
		DominoDb dominoDb = new DominoDb();
		DominoMem dominoMem = new DominoMem();
		DominoCache dominoCache = new DominoCache();
		DominoServer dominoServer = new DominoServer();
		DominoDisk dominoDisk = new DominoDisk();
		List disklist = new ArrayList();
		Pingcollectdata hostdata = null;
		try {
			
			Calendar date=Calendar.getInstance();
			
			  try{
				  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				  com.afunms.polling.base.Node snmpnode = (com.afunms.polling.base.Node)PollingEngine.getInstance().getDominoByIP(dominoconf.getIpaddress());
				  Date cc = date.getTime();
				  String time = sdf.format(cc);
				  snmpnode.setLastTime(time);
			  }catch(Exception e){
				  
			  }			
			try{	
				String[] oids =                
						  new String[] {  
							  "1.3.6.1.4.1.334.72" };
				
				
				String[][] results = null;
				
				try {
					Date startdate = new Date();
					//results = SnmpUtils.getTemperatureTableData(dominoconf.getIpaddress(), dominoconf.getCommunity(), oids, 0, 3, 1000);
					results = SnmpUtils.getTemperatureTableData(dominoconf.getIpaddress(), dominoconf.getCommunity(), oids, 0, 
		   					1,"",1,"",1,"",
		   					3, 1000*30);
					Date enddate = new Date();
					
					hostdata = new Pingcollectdata();
					hostdata.setIpaddress(dominoconf.getIpaddress());
					Calendar _date = Calendar.getInstance();
					hostdata.setCollecttime(_date);
					hostdata.setCategory("DomPing");
					hostdata.setEntity("Utilization");
					hostdata.setSubentity("ConnectUtilization");
					hostdata.setRestype("dynamic");
					hostdata.setUnit("%");
					hostdata.setThevalue("100");
					DBDao dbdao = new DBDao();
					try {
						
						dbdao.createHostData(hostdata);
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						dbdao.close();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (results != null) {
					double disksize = 0.0;
					double diskfree = 0.0;
					for (int i = 0; i < results.length; ++i) {
						String value = results[i][0];
						//SysLogger.info(value+"==="+results[i][1]);
						if(value == null || value.trim().length()==0)continue;
						if(value.indexOf(".")<=0 || value.indexOf("=")<=0)continue;
						//String value=vb[j].toString().substring(vb[j].toString().indexOf("=")+1,vb[j].toString().length()).trim();						
						String name=value.substring(0, value.indexOf("."));
						String dname=value.substring(0, value.indexOf("=")).trim();
						String dvalue = value.substring(value.indexOf("=")+1,value.length()).trim();
						if(name.equalsIgnoreCase("Disk")){
							if(dname.indexOf("Free")>=0){
								//SysLogger.info(dname);
								String diskname = dname.substring(5,dname.length()-5);
								dominoDisk.setDiskname(diskname);
								dvalue = dvalue.replaceAll(",", "");
								double bb = Double.parseDouble(dvalue);
								diskfree = bb;
								bb= bb/1024.00/1024.00/1024.00;
								BigDecimal bd=new BigDecimal(bb); 
								BigDecimal bd1=bd.setScale(3,bd.ROUND_HALF_UP);
								bb=bd1.doubleValue();
								if(bb>1.0) dvalue=String.valueOf(bb)+" GB";
								else if(bb<1.0) {
									bb=bb*1024;
									dvalue=String.valueOf(bb)+" MB";
								}
								dominoDisk.setDiskfree(dvalue);
								continue;
							}else if(dname.indexOf("Size")>=0){
								dvalue = dvalue.replaceAll(",", "");
								double bb = Double.parseDouble(dvalue);
								disksize = bb;
								bb= bb/1024.00/1024.00/1024.00;
								BigDecimal bd=new BigDecimal(bb); 
							
								BigDecimal bd1=bd.setScale(3,bd.ROUND_HALF_UP);
								bb=bd1.doubleValue();
								if(bb>1.0) dvalue=String.valueOf(bb)+" GB";
								else if(bb<1.0) {
									bb=bb*1024;
									dvalue=String.valueOf(bb)+" MB";
								}
								dominoDisk.setDisksize(dvalue);
								continue;
							}else if(dname.indexOf("Type")>=0){
								dvalue = dvalue.replaceAll(",", "");
								dominoDisk.setDisktype(dvalue);
								BigDecimal bd=new BigDecimal(1-diskfree/disksize); 
								
								BigDecimal bd1=bd.setScale(3,bd.ROUND_HALF_UP);
								float  diskUtil=Float.parseFloat(bd1.toString())*100;//将小数转换为百分率
								dominoDisk.setDiskusedpctutil(diskUtil+"");
								disklist.add(dominoDisk);
								
								dominoDisk = new DominoDisk();
								continue;
							}
						}
						if(name.equalsIgnoreCase("mail")){
							//dominoMail = new DominoMail();
							if("MAIL.Dead".equalsIgnoreCase(dname)) dominoMail.setMailDead(dvalue);
							if("MAIL.Waiting".equalsIgnoreCase(dname)) dominoMail.setMailWaiting(dvalue);
							if("MAIL.WaitingRecipients".equalsIgnoreCase(dname)) dominoMail.setMailWaitingRecipients(dvalue);
							if("Mail.CurrentByteDeliveryRate".equalsIgnoreCase(dname)) dominoMail.setMailDeliverRate(dvalue);
							if("Mail.CurrentByteTransferRate".equalsIgnoreCase(dname)) dominoMail.setMailTransferRate(dvalue);
							if("Mail.DeliveryThreads.Max".equalsIgnoreCase(dname)) dominoMail.setMailDeliverThreadsMax(dvalue);
							if("Mail.DeliveryThreads.Total".equalsIgnoreCase(dname)) dominoMail.setMailDeliverThreadsTotal(dvalue);
							if("Mail.TransferThreads.Max".equalsIgnoreCase(dname)) dominoMail.setMailTransferThreadsMax(dvalue);
							if("Mail.TransferThreads.Total".equalsIgnoreCase(dname)) dominoMail.setMailTransferThreadsTotal(dvalue);	
							if("1.3.6.1.4.1.334.72.1.1.4.11".equalsIgnoreCase(results[i][1]))dominoMail.setMailAvgSize(dvalue);
							if("1.3.6.1.4.1.334.72.1.1.4.1".equalsIgnoreCase(results[i][1]))dominoMail.setMailAvgTime(dvalue);
						}
						if(name.equalsIgnoreCase("ldap")){
							//dominoLdap = new DominoLdap();
							if("LDAP.Server.Running".equalsIgnoreCase(dname)) dominoLdap.setLdapRunning(dvalue);
							if("LDAP.Sessions.Inbound.Accept.Queue".equalsIgnoreCase(dname)) dominoLdap.setLdapInboundQue(dvalue);
							if("LDAP.Sessions.Inbound.Active".equalsIgnoreCase(dname)) dominoLdap.setLdapInboundActive(dvalue);
							if("LDAP.Sessions.Inbound.Active.SSL".equalsIgnoreCase(dname)) dominoLdap.setLdapInboundActiveSSL(dvalue);
							if("LDAP.Sessions.Inbound.BytesReceived".equalsIgnoreCase(dname)) dominoLdap.setLdapInboundBytesReseived(dvalue);
							if("LDAP.Sessions.Inbound.BytesSent".equalsIgnoreCase(dname)) dominoLdap.setLdapInboundBytesSent(dvalue);
							if("LDAP.Sessions.Inbound.Peak ".equalsIgnoreCase(dname)) dominoLdap.setLdapInboundPeak(dvalue);
							if("LDAP.Sessions.Inbound.Peak.SSL".equalsIgnoreCase(dname)) dominoLdap.setLdapInboundPeakSSL(dvalue);
							if("LDAP.Sessions.Inbound.Total".equalsIgnoreCase(dname)) dominoLdap.setLdapInboundTotal(dvalue);
							if("LDAP.Sessions.Inbound.Total.SSL".equalsIgnoreCase(dname)) dominoLdap.setLdapInboundTotalSSL(dvalue);
							if("LDAP.Sessions.Inbound.Total.SSL.Bad_Handshake".equalsIgnoreCase(dname)) dominoLdap.setLdapBadHandShake(dvalue);
							if("LDAP.Sessions.Threads.Busy".equalsIgnoreCase(dname)) dominoLdap.setLdapThreadsBusy(dvalue);
							if("LDAP.Sessions.Threads.Idle".equalsIgnoreCase(dname)) dominoLdap.setLdapThreadsIdle(dvalue);
							if("LDAP.Sessions.Threads.InThreadPool".equalsIgnoreCase(dname)) dominoLdap.setLdapThreadsInPool(dvalue);
							if("LDAP.Sessions.Threads.Peak".equalsIgnoreCase(dname)) dominoLdap.setLdapTHreadsPeak(dvalue);					
						}
						if(name.equalsIgnoreCase("http")){
							if("Http.Accept.ConnectionsAccepted".equalsIgnoreCase(dname)) dominoHttp.setHttpAccept(dvalue);
							if("Http.Accept.ConnectionsRefused".equalsIgnoreCase(dname)) dominoHttp.setHttpRefused(dvalue);
							if("Http.CurrentConnections".equalsIgnoreCase(dname)) dominoHttp.setHttpCurrentCon(dvalue);
							if("Http.MaxConnections".equalsIgnoreCase(dname)) dominoHttp.setHttpMaxCon(dvalue);
							if("Http.PeakConnections".equalsIgnoreCase(dname)) dominoHttp.setHttpPeakCon(dvalue);
							if("Http.Worker.Total.Http.Requests".equalsIgnoreCase(dname)) dominoHttp.setHttpWorkerRequest(dvalue);
							if("Http.Worker.Total.Http.RequestTime".equalsIgnoreCase(dname)) dominoHttp.setHttpWorkerRequestTime(dvalue);
							if("Http.Worker.Total.BytesRead".equalsIgnoreCase(dname)) dominoHttp.setHttpWorkerBytesRead(dvalue);
							if("Http.Worker.Total.BytesWritten".equalsIgnoreCase(dname)) dominoHttp.setHttpWorkerBytesWritten(dvalue);
							if("Http.Worker.Total.RequestsProcessed".equalsIgnoreCase(dname)) dominoHttp.setHttpWorkerRequestProcess(dvalue);
							if("Http.Worker.Total.TotalRequestTime".equalsIgnoreCase(dname)) dominoHttp.setHttpWorkerTotalRequest(dvalue);
							if("Http.Accept.Errors".equalsIgnoreCase(dname)) dominoHttp.setHttpErrorUrl(dvalue);
						}
						if(name.equalsIgnoreCase("Database")){
							//dominoDb = new DominoDb();
							if("Database.Database.BufferPool.Maximum.Megabytes".equalsIgnoreCase(dname)) dominoDb.setDbBuffPoolMax(dvalue);
							if("Database.Database.BufferPool.Peak.Megabytes".equalsIgnoreCase(dname)) dominoDb.setDbBuffPoolPeak(dvalue);
							if("Database.Database.BufferPool.MM.Reads".equalsIgnoreCase(dname)) dominoDb.setDbBuffPoolReads(dvalue);
							if("Database.Database.BufferPool.MM.Writes".equalsIgnoreCase(dname)) dominoDb.setDbBuffPoolWrites(dvalue);
							if("Database.Database.BufferPool.PerCentReadsInBuffer".equalsIgnoreCase(dname)) dominoDb.setDbBuffPoolReadHit(dvalue);
							if("Database.DbCache.CurrentEntries".equalsIgnoreCase(dname)){
								dominoDb.setDbCacheEntry(dvalue);									
							}
							if("Database.DbCache.HighWaterMark".equalsIgnoreCase(dname)) dominoDb.setDbCacheWaterMark(dvalue);
							if("Database.DbCache.Hits".equalsIgnoreCase(dname)) dominoDb.setDbCacheHit(dvalue);
							if("Database.DbCache.InitialDbOpens".equalsIgnoreCase(dname)) dominoDb.setDbCacheDbOpen(dvalue);
							if("Database.NIFPool.Peak".equalsIgnoreCase(dname)) dominoDb.setDbNifPoolPeak(dvalue);
							if("Database.NIFPool.Used".equalsIgnoreCase(dname)) dominoDb.setDbNifPoolUse(dvalue);
							if("Database.NSFPool.Peak".equalsIgnoreCase(dname)) dominoDb.setDbNsfPoolPeak(dvalue);
							if("Database.NSFPool.Used".equalsIgnoreCase(dname)) dominoDb.setDbNsfPoolUse(dvalue);
						}
						
						if(name.equalsIgnoreCase("Server")){
							if("Server.Name".equalsIgnoreCase(dname)) dominoServer.setName(dvalue);
							if("Server.Title".equalsIgnoreCase(dname)) dominoServer.setTitle(dvalue);
							if("Server.Version.OS".equalsIgnoreCase(dname)) dominoServer.setOs(dvalue);
							if("Server.Version.Architecture".equalsIgnoreCase(dname)) dominoServer.setArchitecture(dvalue);
							if("Server.Monitor.Start".equalsIgnoreCase(dname)) dominoServer.setStarttime(dvalue);
							if("Server.CPU.Count".equalsIgnoreCase(dname))dominoServer.setCpucount(dvalue);
							if("Server.CPU.Type".equalsIgnoreCase(dname)) dominoServer.setCputype(dvalue);
							if("Server.AvailabilityIndex".equalsIgnoreCase(dname)) dominoServer.setAvailabilityIndex(dvalue);
							if("Server.Sessions.Dropped".equalsIgnoreCase(dname)) dominoServer.setSessionsDropped(dvalue);
							if("Server.Tasks".equalsIgnoreCase(dname)) dominoServer.setTasks(dvalue);
							if("Server.Trans.PerMinute".equalsIgnoreCase(dname)) dominoServer.setTransPerMinute(dvalue);
							if("Server.Users.Peak".equalsIgnoreCase(dname)) dominoServer.setUsersPeak(dvalue);
						}
						if(name.equalsIgnoreCase("Platform")){
							//入库
							if("Platform.Memory.RAM.PctUtil".equalsIgnoreCase(dname)) dominoMem.setPlatformMemPhyPctUtil(dvalue);
							
							if("Platform.Memory.RAM.TotalMBytes".equalsIgnoreCase(dname)){
								double bb = Double.parseDouble(dvalue);
								bb= bb/1024.00/1024.00/1024.00;
								BigDecimal bd=new BigDecimal(bb); 
								BigDecimal bd1=bd.setScale(3,bd.ROUND_HALF_UP);
								bb=bd1.doubleValue();
								if(bb>1.0) dvalue=String.valueOf(bb)+" GB";
								else if(bb<1.0) {
									bb=bb*1024;
									dvalue=String.valueOf(bb)+" MB";
								}
								dominoMem.setPlatformMemPhysical(dvalue);
								//dominoServer.setTitle(dvalue);
							}
							if(dname.indexOf("Platform.Process.server")>=0 && dname.indexOf("PctCpuUtil")>=0){
								//入库
								dominoServer.setCpupctutil(dvalue);
							}
						}
						
						//入库
						if(name.equalsIgnoreCase("IMAP")){
							if("IMAP.Server.Running".equalsIgnoreCase(dname)) {
								if("TRUE".equalsIgnoreCase(dvalue)){
									dominoServer.setImapstatus("运行");
								}else{
									dominoServer.setImapstatus("停止");
								}
							}
						}
						//入库
						if(name.equalsIgnoreCase("LDAP")){
							if("LDAP.Server.Running".equalsIgnoreCase(dname)) {
								if("TRUE".equalsIgnoreCase(dvalue)){
									dominoServer.setLdapstatus("运行");
								}else{
									dominoServer.setLdapstatus("停止");
								}
							}
						}
						//入库
						if(name.equalsIgnoreCase("POP3")){
							if("POP3.Server.Running".equalsIgnoreCase(dname)) {
								if("TRUE".equalsIgnoreCase(dvalue)){
									dominoServer.setPop3status("运行");
								}else{
									dominoServer.setPop3status("停止");
								}
							}
						}
						//入库
						if(name.equalsIgnoreCase("SMTP")){
							if("SMTP.Server.Running".equalsIgnoreCase(dname)) {
								if("TRUE".equalsIgnoreCase(dvalue)){
									dominoServer.setSmtpstatus("运行");
								}else{
									dominoServer.setSmtpstatus("停止");
								}
							}
						}
						
						if(name.equalsIgnoreCase("Mem")){
							//dominoMem = new DominoMem();
							if("Mem.Allocated".equalsIgnoreCase(dname)) {
								double bb = Double.parseDouble(dvalue);
								disksize = bb*1024.00;
								bb= bb/1024.00/1024.00;
								BigDecimal bd=new BigDecimal(bb); 
								BigDecimal bd1=bd.setScale(3,bd.ROUND_HALF_UP);
								bb=bd1.doubleValue();
								if(bb>1.0) dvalue=String.valueOf(bb)+" GB";
								else if(bb<1.0) {
									bb=bb*1024;
									dvalue=String.valueOf(bb)+" MB";
								}
								dominoMem.setMemAllocate(dvalue);
							}
							if("Mem.Free".equalsIgnoreCase(dname)) {
								dvalue = dvalue.replaceAll(",", "");
								double bb = Double.parseDouble(dvalue);
								diskfree = bb;
								bb= bb/1024.00/1024.00/1024.00;
								BigDecimal bd=new BigDecimal(bb); 
								BigDecimal bd1=bd.setScale(3,bd.ROUND_HALF_UP);
								bb=bd1.doubleValue();
								if(bb>1.0) dvalue=String.valueOf(bb)+" GB";
								else if(bb<1.0) {
									bb=bb*1024;
									dvalue=String.valueOf(bb)+" MB";
								}
								dominoMem.setMemFree(dvalue);
								//入库,内存利用率
								bd=new BigDecimal(1-diskfree/disksize);
								
								bd1=bd.setScale(3,bd.ROUND_HALF_UP);
								float  memUtil=Float.parseFloat(bd1.toString())*100;//将小数转换为百分率
								dominoMem.setMempctutil(memUtil+"");
								
							}
							
							
							if("Mem.Allocated.Process".equalsIgnoreCase(dname)) {
								double bb = Double.parseDouble(dvalue);
								bb= bb/1024.00/1024.00/1024.00;
								BigDecimal bd=new BigDecimal(bb); 
								BigDecimal bd1=bd.setScale(3,bd.ROUND_HALF_UP);
								bb=bd1.doubleValue();
								if(bb>1.0) dvalue=String.valueOf(bb)+" GB";
								else if(bb<1.0) {
									bb=bb*1024;
									dvalue=String.valueOf(bb)+" MB";
								}
								dominoMem.setMemAllocateProcess(dvalue);
							}
							if("Mem.Allocated.Shared".equalsIgnoreCase(dname)) {
								double bb = Double.parseDouble(dvalue);
								bb= bb/1024.00/1024.00/1024.00;
								BigDecimal bd=new BigDecimal(bb); 
								BigDecimal bd1=bd.setScale(3,bd.ROUND_HALF_UP);
								bb=bd1.doubleValue();
								if(bb>1.0) dvalue=String.valueOf(bb)+" GB";
								else if(bb<1.0) {
									bb=bb*1024;
									dvalue=String.valueOf(bb)+" MB";
								}
								dominoMem.setMemAllocateShare(dvalue);
							}
							if("Mem.PhysicalRAM".equalsIgnoreCase(dname)) {
								dvalue = dvalue.replaceAll(",", "");
								double bb = Double.parseDouble(dvalue);
								bb= bb/1024.00/1024.00/1024.00;
								BigDecimal bd=new BigDecimal(bb); 
								BigDecimal bd1=bd.setScale(3,bd.ROUND_HALF_UP);
								bb=bd1.doubleValue();
								if(bb>1.0) dvalue=String.valueOf(bb)+" GB";
								else if(bb<1.0) {
									bb=bb*1024;
									dvalue=String.valueOf(bb)+" MB";
								}
								dominoMem.setMemPhysical(dvalue);
							}
						}
						if(name.equalsIgnoreCase("Domino")){
							//dominoCache = new DominoCache();
							if("Domino.Cache.Command.Count".equalsIgnoreCase(dname)) dominoCache.setCacheCommandCount(dvalue);
							if("Domino.Cache.Command.DisplaceRate".equalsIgnoreCase(dname)) {
								double bb = Double.parseDouble(dvalue);
								BigDecimal bd=new BigDecimal(bb); 
								BigDecimal bd1=bd.setScale(2,bd.ROUND_HALF_UP);
								bb=bd1.doubleValue();
								dvalue=String.valueOf(bb);
								dominoMem.setMemAllocate(dvalue);dominoCache.setCacheCommandDisRate(dvalue);
							}
							if("Domino.Cache.Command.HitRate".equalsIgnoreCase(dname)) {
								double bb = Double.parseDouble(dvalue);
								BigDecimal bd=new BigDecimal(bb); 
								BigDecimal bd1=bd.setScale(2,bd.ROUND_HALF_UP);
								bb=bd1.doubleValue();
								dvalue=String.valueOf(bb);
								dominoCache.setCacheCommandHitRate(dvalue);
							}
							if("Domino.Cache.Command.MaxSize".equalsIgnoreCase(dname)) dominoCache.setCacheCommandSize(dvalue);
							if("Domino.Cache.Database.HitRate".equalsIgnoreCase(dname)) {
								double bb = Double.parseDouble(dvalue);
								BigDecimal bd=new BigDecimal(bb); 
								BigDecimal bd1=bd.setScale(2,bd.ROUND_HALF_UP);
								bb=bd1.doubleValue();
								dvalue=String.valueOf(bb);
								dominoCache.setCacheDbHitRate(dvalue);
							}
							if("Domino.Cache.Session.Count".equalsIgnoreCase(dname)) dominoCache.setCacheSessionCount(dvalue);
							if("Domino.Cache.Session.DisplaceRate".equalsIgnoreCase(dname)) {
								double bb = Double.parseDouble(dvalue);
								BigDecimal bd=new BigDecimal(bb); 
								BigDecimal bd1=bd.setScale(2,bd.ROUND_HALF_UP);
								bb=bd1.doubleValue();
								dvalue=String.valueOf(bb);
								dominoCache.setCacheSessionDisRate(dvalue);
							}
							if("Domino.Cache.Session.HitRate".equalsIgnoreCase(dname)) {
								double bb = Double.parseDouble(dvalue);
								BigDecimal bd=new BigDecimal(bb); 
								BigDecimal bd1=bd.setScale(2,bd.ROUND_HALF_UP);
								bb=bd1.doubleValue();
								dvalue=String.valueOf(bb);
								dominoCache.setCacheSessionHitRate(dvalue);
							}
							if("Domino.Cache.Session.MaxSize".equalsIgnoreCase(dname)) dominoCache.setCacheSessionSize(dvalue);
							if("Domino.Cache.User.Count".equalsIgnoreCase(dname)) dominoCache.setCacheUserCount(dvalue);
							if("Domino.Cache.User.DisplaceRate".equalsIgnoreCase(dname)) {
								double bb = Double.parseDouble(dvalue);
								BigDecimal bd=new BigDecimal(bb); 
								BigDecimal bd1=bd.setScale(2,bd.ROUND_HALF_UP);
								bb=bd1.doubleValue();
								dvalue=String.valueOf(bb);
								dominoCache.setCacheUserDisRate(dvalue);
							}
							if("Domino.Cache.User.HitRate".equalsIgnoreCase(dname)) {
								double bb = Double.parseDouble(dvalue);
								BigDecimal bd=new BigDecimal(bb); 
								BigDecimal bd1=bd.setScale(2,bd.ROUND_HALF_UP);
								bb=bd1.doubleValue();
								dvalue=String.valueOf(bb);
								dominoCache.setCacheUserHitRate(dvalue);
							}
							if("Domino.Cache.User.MaxSize".equalsIgnoreCase(dname)) dominoCache.setCacheUserSize(dvalue);
							if("Domino.Config.PortNumber".equalsIgnoreCase(dname)) dominoServer.setPortnumber(dvalue);
							if("Domino.Requests.Per1Hour.Total".equalsIgnoreCase(dname)) dominoServer.setRequestsPer1Hour(dvalue);
						}
						
					
					}
				}
				returnHas = new Hashtable();
				if (hostdata !=null) returnHas.put("Ping", hostdata);
				if (dominoMail !=null) returnHas.put("Mail", dominoMail);
				if (dominoLdap !=null) returnHas.put("LDAP", dominoLdap);
				if (dominoHttp !=null) returnHas.put("HTTP", dominoHttp);
				if (dominoDb !=null) returnHas.put("Database", dominoDb);
				if (dominoMem !=null) returnHas.put("Mem", dominoMem);
				if (dominoCache !=null) returnHas.put("Cache", dominoCache);
				if (dominoServer != null)returnHas.put("Server", dominoServer);
				if (disklist != null)returnHas.put("Disk", disklist);
		  }
		  catch(Exception e){
			  e.printStackTrace();
			  returnHas=null;
		  }
		}catch(Exception e){
			returnHas=null;
			e.printStackTrace();
			return null;
		}finally{
			}

		return returnHas;
	
		//return returnHas;
	}
	
	
	 public void createSMS(Procs procs){
		 	Procs lastprocs = null;
		 	//建立短信	
		 	procs.setCollecttime(Calendar.getInstance());
		 	//从已经发送的短信列表里获得当前该PROC已经发送的短信
		 	lastprocs = (Procs)sendeddata.get(procs.getIpaddress()+":"+procs.getProcname());	
		 	/*
		 	try{		 				 		
		 		if (lastprocs==null){
		 			//内存中不存在	,表明没发过短信,则发短信
		 			Equipment equipment = equipmentManager.getByip(procs.getIpaddress());
		 			Smscontent smscontent = new Smscontent();
		 			String time = sdf.format(procs.getCollecttime().getTime());
		 			smscontent.setMessage(time+"&"+equipment.getEquipname()+"&"+equipment.getIpaddress()+"&"+procs.getProcname()+"&进程丢失&level=2");
		 			//发送短信
		 			Vector tosend = new Vector();
		 			tosend.add(smscontent);		 			
		 			smsmanager.sendSmscontent(tosend);
		 			//把该进程信息添加到已经发送的进程短信列表里,以IP:进程名作为key
		 			sendeddata.put(procs.getIpaddress()+":"+procs.getProcname(),procs);		 						 				
		 		}else{
		 			//若已经发送的短信列表存在这个IP的PROC进程
		 			//若在，则从已发送短信列表里判断是否已经发送当天的短信		 				
		 			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		 			Date last = null;
		 			Date current = null;
		 			Calendar sendcalen = (Calendar)lastprocs.getCollecttime();
		 			Date cc = sendcalen.getTime();
		 			String tempsenddate = formatter.format(cc);
		 			
		 			Calendar currentcalen = (Calendar)procs.getCollecttime();
		 			cc = currentcalen.getTime();
		 			last = formatter.parse(tempsenddate);
		 			String currentsenddate = formatter.format(cc);
		 			current = formatter.parse(currentsenddate);
		 			
		 			long subvalue = current.getTime()-last.getTime();			 			
		 			
		 			if (subvalue/(1000*60*60*24)>=1){
		 				//超过一天，则再发信息
			 			Smscontent smscontent = new Smscontent();
			 			String time = sdf.format(procs.getCollecttime().getTime());
			 			Equipment equipment = equipmentManager.getByip(procs.getIpaddress());
			 			if (equipment == null){
			 				return;
			 			}else
			 				smscontent.setMessage(time+"&"+equipment.getEquipname()+"&"+equipment.getIpaddress()+"&"+procs.getProcname()+"&进程丢失&level=2");
			 			
			 			//发送短信
			 			Vector tosend = new Vector();
			 			tosend.add(smscontent);		 			
			 			smsmanager.sendSmscontent(tosend);
			 			//把该进程信息添加到已经发送的进程短信列表里,以IP:进程名作为key
			 			sendeddata.put(procs.getIpaddress()+":"+procs.getProcname(),procs);		 						 				
			 		}else{
			 			//没超过一天,则只写事件
			 			Vector eventtmpV = new Vector();
						Eventlist event = new Eventlist();
						  Monitoriplist monitoriplist = (Monitoriplist)monitormanager.getByIpaddress(host);
						  event.setEventtype("host");
						  event.setEventlocation(host);
						  event.setManagesign(new Integer(0));
						  event.setReportman("monitorpc");
						  event.setRecordtime(Calendar.getInstance());
						  event.setLevel1(new Integer(1));
						  event.setEquipment(equipmentManager.getByip(monitoriplist.getIpaddress()));
						  event.setNetlocation(equipmentManager.getByip(monitoriplist.getIpaddress()).getNetlocation());
						  String time = sdf.format(Calendar.getInstance().getTime());
						  event.setContent(monitoriplist.getEquipname()+"&"+monitoriplist.getIpaddress()+"&"+time+"进程"+procs.getProcname()+"丢失&level=1");
						  eventtmpV.add(event);
						  try{
							  eventmanager.createEventlist(eventtmpV);
						  }catch(Exception e){
							  e.printStackTrace();
						  }
						  
			 		}
		 		}
		 	}catch(Exception e){
		 		e.printStackTrace();
		 	}
		 	*/
		 }
		public int getInterval(float d,String t){
			int interval=0;
			  if(t.equals("d"))
				 interval =(int) d*24*60*60; //天数
			  else if(t.equals("h"))
				 interval =(int) d*60*60;    //小时
			  else if(t.equals("m"))
				 interval = (int)d*60;       //分钟
			else if(t.equals("s"))
						 interval =(int) d;       //秒
			return interval;
}
}





