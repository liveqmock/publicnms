package com.afunms.topology.util;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.afunms.application.model.DominoDisk;
import com.afunms.application.model.DominoMem;
import com.afunms.application.model.DominoServer;
import com.afunms.application.util.DbConversionUtil;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.PingUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.impl.ProcessDominoData;
import com.afunms.polling.impl.ProcessNetData;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.AllUtilHdx;
import com.afunms.polling.om.Buffercollectdata;
import com.afunms.polling.om.CPUcollectdata;
import com.afunms.polling.om.DiscardsPerc;
import com.afunms.polling.om.Diskcollectdata;
import com.afunms.polling.om.ErrorsPerc;
import com.afunms.polling.om.Flashcollectdata;
import com.afunms.polling.om.InPkts;
import com.afunms.polling.om.Interfacecollectdata;
import com.afunms.polling.om.IpMac;
import com.afunms.polling.om.IpMacBase;
import com.afunms.polling.om.IpMacChange;
import com.afunms.polling.om.IpRouter;
import com.afunms.polling.om.Memorycollectdata;
import com.afunms.polling.om.OutPkts;
import com.afunms.polling.om.Packs;
import com.afunms.polling.om.Pingcollectdata;
import com.afunms.polling.om.UtilHdx;
import com.afunms.polling.om.UtilHdxPerc;

public class HostCollectDataHelper {
java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

public void dealDominoData(Hashtable ipdata,String ip) {
	String runmodel = PollingEngine.getCollectwebflag();
	if(ipdata != null && ipdata.size()>0){
		if("1".equals(runmodel)){
        	//采集与访问是分离模式
			Date startdate = new Date();
			//实时数据入库处理
			ProcessDominoData porcessData = new ProcessDominoData();
			try{
				porcessData.processDominoData(ip, ipdata);
			}catch(Exception e){
				
			}
			Date enddate = new Date();
		}
		
		
		DBManager dbmanager = new DBManager();
		try{
			
			
			DominoServer server = null;
			StringBuffer sBuffer = null;
			
			DominoMem mem = null;
			
			
			Hashtable flashhash = null;
			Vector flashVector = null;
			Flashcollectdata flashdata = null;
			
			
			Hashtable bufferhash = null;
			Vector bufferVector = null;
			Buffercollectdata bufferdata = null;
			
			
			
				String allipstr = SysUtil.doip(ip);
				Calendar tempCal = Calendar.getInstance();
				Date cc = tempCal.getTime();
				String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
				if(ipdata != null){
					//处理网络设备的数据
					if(ipdata.containsKey("Server")){
						//CPU
						server = (DominoServer)ipdata.get("Server");
						StringBuffer sb=new StringBuffer();
						String cpuUtil=server.getCpupctutil();
						
						double util=Double.parseDouble(cpuUtil);
						
							//得到CPU平均
							
								
								String tablename = "dominocpu" + allipstr;
								String count ="0";
								if(server.getCpucount()!= null){
									count = server.getCpucount();
								}
								sBuffer = new StringBuffer();
								sBuffer.append("insert into ");
								sBuffer.append(tablename);
								sBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
								sBuffer.append("values('");
								sBuffer.append(ip);
								sBuffer.append("','");
								sBuffer.append("dynamic");
								sBuffer.append("','");
								sBuffer.append("dominoCpu");
								sBuffer.append("','");
								sBuffer.append("Utilization");
								sBuffer.append("','");
								sBuffer.append("Utilization");
								sBuffer.append("','");
								sBuffer.append("%");
								sBuffer.append("','");
								sBuffer.append("");
								sBuffer.append("','");
								sBuffer.append("");
								sBuffer.append("','");
								sBuffer.append(count);
								sBuffer.append("','");
								sBuffer.append(util);
								sBuffer.append("',");
								sBuffer.append(time);
								sBuffer.append(")");
								//状态入库
								tablename="domstatus"+allipstr;
								String imapsql=getSql(tablename, ip, "IMAP", server.getImapstatus(), time);
								String ldapsql=getSql(tablename, ip, "LDAP", server.getLdapstatus(), time);
								String pop3sql=getSql(tablename, ip, "POP3", server.getPop3status(), time);
								String smtpsql=getSql(tablename, ip, "SMTP", server.getSmtpstatus(), time);
								try {
									if(cpuUtil!=null&&!cpuUtil.equals("")){
									dbmanager.addBatch(sBuffer.toString());
									System.out.println(sBuffer.toString());
									    }
									dbmanager.addBatch(imapsql);
									dbmanager.addBatch(ldapsql);
									dbmanager.addBatch(pop3sql);
									dbmanager.addBatch(smtpsql);
								} catch (Exception ex) {
									ex.printStackTrace();
								}
								sBuffer = null;
								tablename = null;
							}
							server = null;
						
					
					
					
					if(ipdata.containsKey("Mem")){

						//Memory
						mem = (DominoMem)ipdata.get("Mem");
						String platMem=mem.getPlatformMemPhyPctUtil();
							
								String tablename = "domplatmem" + allipstr;
								
								sBuffer = new StringBuffer();
								sBuffer.append("insert into ");
								sBuffer.append(tablename);
								sBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
								sBuffer.append("values('");
								sBuffer.append(ip);
								sBuffer.append("','");
								sBuffer.append("dynamic");
								sBuffer.append("','");
								sBuffer.append("domplatmem");
								sBuffer.append("','");
								sBuffer.append("Utilization");
								sBuffer.append("','");
								sBuffer.append("Utilization");
								sBuffer.append("','");
								sBuffer.append("%");
								sBuffer.append("','");
								sBuffer.append("");
								sBuffer.append("','");
								sBuffer.append("");
								sBuffer.append("','");
								sBuffer.append("0");
								sBuffer.append("','");
								sBuffer.append(mem.getPlatformMemPhyPctUtil());
								sBuffer.append("',");
								sBuffer.append(time);
								sBuffer.append(")");
								try {
									if (!platMem.equals("")&&platMem!=null){
									dbmanager.addBatch(sBuffer.toString());
									System.out.println(sBuffer.toString());
									}
								} catch (Exception ex) {
									ex.printStackTrace();
								}
								sBuffer = null;
								tablename = null;
							////////物理内存//////////
								String serMem=mem.getMempctutil();
								
								tablename = "domservmem" + allipstr;
								sBuffer = new StringBuffer();
								sBuffer.append("insert into ");
								sBuffer.append(tablename);
								sBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
								sBuffer.append("values('");
								sBuffer.append(ip);
								sBuffer.append("','");
								sBuffer.append("dynamic");
								sBuffer.append("','");
								sBuffer.append("domservmem");
								sBuffer.append("','");
								sBuffer.append("Utilization");
								sBuffer.append("','");
								sBuffer.append("Utilization");
								sBuffer.append("','");
								sBuffer.append("%");
								sBuffer.append("','");
								sBuffer.append("");
								sBuffer.append("','");
								sBuffer.append("");
								sBuffer.append("','");
								sBuffer.append("0");
								sBuffer.append("','");
								sBuffer.append(mem.getMempctutil());
								sBuffer.append("',");
								sBuffer.append(time);
								sBuffer.append(")");
								try {
									if (!serMem.equals("")&&serMem!=null){
									dbmanager.addBatch(sBuffer.toString());
								 	System.out.println(sBuffer.toString());
									}
								} catch (Exception ex) {
									ex.printStackTrace();
								}
								sBuffer = null;
								tablename = null;
					}
					
					
					if (ipdata.containsKey("Disk")) {

					List diskList = (List) ipdata.get("Disk");
					String	tablename="domdisk"+allipstr;
						if (diskList != null && diskList.size() > 0) {
							for (int i = 0; i < diskList.size(); i++) {
								DominoDisk disk = (DominoDisk) diskList.get(i);
								if (disk != null) {
									String value=disk.getDiskusedpctutil();
									try {
										String sql=getSql(tablename, ip, disk.getDiskname(),value , time);
                                       if(!value.equals("")&&value!=null)
										dbmanager.addBatch(sql);
                                       	System.out.println(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}

						}

					}
					
					
				}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				
				dbmanager.executeBatch();
				dbmanager.commit();
			}catch(Exception e){
				e.printStackTrace();
			}
			dbmanager.close();
		}   			
	}

}
public String getSql(String tableName,String ip,String subentity,String value,String time) {
	StringBuffer sBuffer=new StringBuffer();
	sBuffer.append("insert into ");
	sBuffer.append(tableName);
	sBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
	sBuffer.append("values('");
	sBuffer.append(ip);
	sBuffer.append("','");
	sBuffer.append("static");
	sBuffer.append("','");
	sBuffer.append("Disk");
	sBuffer.append("','");
	sBuffer.append("Utilization");
	sBuffer.append("','");
	sBuffer.append(subentity);
	sBuffer.append("','");
	sBuffer.append("%");
	sBuffer.append("','");
	sBuffer.append("");
	sBuffer.append("','");
	sBuffer.append("");
	sBuffer.append("','");
	sBuffer.append(0);
	sBuffer.append("','");
	sBuffer.append(value);
	sBuffer.append("',");
	sBuffer.append(time);
	sBuffer.append(")");
	return sBuffer.toString();
}
}
