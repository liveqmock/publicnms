package com.afunms.polling.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;


import com.afunms.application.dao.DnsConfigDao;
import com.afunms.application.dao.Dnsmonitor_historyDao;
import com.afunms.application.dao.Dnsmonitor_realtimeDao;
import com.afunms.application.dao.EmailHistoryDao;
import com.afunms.application.dao.EmailRealTimeDao;
import com.afunms.application.dao.FtpHistoryDao;
import com.afunms.application.dao.FtpRealTimeDao;
import com.afunms.application.manage.DnsManager;
import com.afunms.application.manage.FTPManager;
import com.afunms.application.manage.MailMonitorManager;
import com.afunms.application.model.DnsConfig;
import com.afunms.application.model.Dnsmonitor_history;
import com.afunms.application.model.Dnsmonitor_realtime;
import com.afunms.application.model.EmailMonitorConfig;
import com.afunms.application.model.EmailHistory;
import com.afunms.application.model.EmailRealtime;
import com.afunms.application.model.FTPConfig;
import com.afunms.application.model.FtpHistory;
import com.afunms.application.model.FtpRealTime;
import com.afunms.common.util.ReceiveMail;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SendMail;
import com.afunms.event.dao.AlarmInfoDao;
import com.afunms.event.dao.SmscontentDao;
import com.afunms.event.model.AlarmInfo;
import com.afunms.event.model.EventList;
import com.afunms.event.model.Smscontent;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.impl.ProcessDnsData;
import com.afunms.polling.node.Ftp;
import com.afunms.polling.node.Host;
import com.afunms.polling.node.Mail;
import com.afunms.polling.om.Pingcollectdata;

public class DNSTask extends MonitorTask{
	
	private Hashtable sendeddata = ShareData.getSendeddata();
	private DnsManager dnsconf = null;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
	
	
	public DNSTask(){
		super();
	}
		
	@Override
	public void run(){
		try{	
			List<DnsConfig> dnsConfigList = new ArrayList<DnsConfig>();
			//DnsManager dnsManager = new DnsManager();
			DnsConfigDao dnsConfigDao = null;
			try{
				dnsConfigDao = new DnsConfigDao();
				dnsConfigList = dnsConfigDao.getDNSConfigListByMonFlag(1);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				if(dnsConfigDao != null){
					dnsConfigDao.close();
				}
			}
			Calendar date=Calendar.getInstance();
			//System.out.println("realHash is "+realHash.size());
			if(dnsConfigList == null){
				return;
			}
			for (int i = 0; i < dnsConfigList.size(); i++) {
				SmscontentDao eventdao=null;
				Dnsmonitor_realtimeDao dnsmonitor_realtimeDao = null;
				Dnsmonitor_historyDao dnsmonitor_historyDao = null ;
				try{
					dnsmonitor_realtimeDao = new Dnsmonitor_realtimeDao();
					Hashtable realHash=dnsmonitor_realtimeDao.getAllReal();
					Integer iscanconnected = new Integer(0+"");
					Dnsmonitor_realtime dnsmonitor_realtimeold = new Dnsmonitor_realtime();
					String reason= "";
					DnsConfig dnsConfig = dnsConfigList.get(i);
					/*//System.out.println(dnsConfig+"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
					//Dns dns = (Dns)PollingEngine.getInstance().getDnsByID(dnsConfig.getId());	
					System.out.println(dns+"%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
					if(dns == null){
						continue;
					}
					if(dns != null){
						dns.setStatus(1);
						dns.setAlarm(false);
						dns.getAlarmMessage().clear();
						Calendar _tempCal = Calendar.getInstance();				
						Date _cc = _tempCal.getTime();
						String _time = sdf.format(_cc);
						dns.setLastTime(_time);
					}*/
				
				
					Integer dnsConfig_id = dnsConfig.getId();
					Integer dns_id = dnsConfig.getId();
					boolean old=false;
					Integer smssign=new Integer(0);
					if(realHash !=null&& realHash.get(dns_id)!=null){
						old=true;
						dnsmonitor_realtimeold=(Dnsmonitor_realtime)realHash.get(dns_id);
						smssign=dnsmonitor_realtimeold.getSms_sign();
					}
					//FtpUtil ftputil = new FtpUtil(ftpConfig.getIpaddress(),21,ftpConfig.getUsername(),ftpConfig.getPassword(),"",ResourceCenter.getInstance().getSysPath()+"/ftpdownload/",ftpConfig.getFilename());
					String str1=null;
			    	 String str2=null;
					Process process1=Runtime.getRuntime().exec("cmd /c nslookup "+dnsConfig.getHostip());
					BufferedReader bf1=new BufferedReader(new InputStreamReader(process1.getInputStream()));
			        while((str1=bf1.readLine())!=null)
			        {      
			        	str2+=str1;
			        }
					String time = sdf.format(date.getTime());
					try{					
						if (str2.contains(dnsConfig.getHostip()) ){
							reason="DNS服务有效";
							iscanconnected=new Integer(1);
							
							//轮训采集DNS的数据信息
							Hashtable dnsData = collectDNSData(dnsConfig);
							System.out.println("---结束采集id为："+dnsConfig.getId()+"的DNS信息---");
						}else{
							//需要增加邮件服务所在的服务器是否能连通
							reason="DNS服务无效";
							Host host = (Host)PollingEngine.getInstance().getNodeByIP(dnsConfig.getDnsip());
							Vector ipPingData = (Vector)ShareData.getPingdata().get(dnsConfig.getDnsip());
						      eventdao = new SmscontentDao();
			            	  String eventdesc = "DNS服务(IP:"+dnsConfig.getHostip()+")"+"的DNS服务停止";
			            	  eventdao.createEventWithReasion("poll",dnsConfig.getId()+"",dnsConfig.getDns()+"("+dnsConfig.getHostip()+")",eventdesc,3,"dns","ping","所在的服务器连接不上");
							if(ipPingData != null){
								Pingcollectdata pingdata = (Pingcollectdata)ipPingData.get(0);
								Calendar tempCal = (Calendar)pingdata.getCollecttime();							
								Date cc = tempCal.getTime();
								time = sdf.format(cc);		
								String lastTime = time;
								String pingvalue = pingdata.getThevalue();
								if(pingvalue == null || pingvalue.trim().length()==0)pingvalue="0";
								double pvalue = new Double(pingvalue);
								//
					
						      if(pvalue == 0){
									/*//主机服务器连接不上***********************************************
									dns.setAlarm(true);
									//dbnode.setStatus(3);
									List alarmList = dns.getAlarmMessage();
									if(alarmList == null)alarmList = new ArrayList();
									dns.getAlarmMessage().add("DNS服务无效");
						            String sysLocation = "";*/
						              try{
						            	 /* SmscontentDao eventdao = new SmscontentDao();
						            	  String eventdesc = "DNS服务(IP:"+dnsConfig.getDnsip()+")"+"的DNS服务停止";
						            	  //System.out.println(dnsConfig.getId()+dnsConfig.getDns()+"dnsConfig.getDns()!!!!!!!!!!!!!!");
						            	  eventdao.createEventWithReasion("poll",dnsConfig.getId()+"",dnsConfig.getDns()+"("+dnsConfig.getDnsip()+")",eventdesc,3,"dns","ping","所在的服务器连接不上");*/
						              }catch(Exception e){
						            	  e.printStackTrace();
						              }
						             
								}else{
									/*dns.setAlarm(true);
									//dbnode.setStatus(3);
									List alarmList = dns.getAlarmMessage();
									if(alarmList == null)alarmList = new ArrayList();
									dns.getAlarmMessage().add("DNS服务无效");
									reason = "DNS服务无效";
									createEvent(dnsConfig, reason);*/
								}
								
							}else{
								/*dns.setAlarm(true);
								//dbnode.setStatus(3);
								List alarmList = dns.getAlarmMessage();
								if(alarmList == null)alarmList = new ArrayList();
								dns.getAlarmMessage().add("DNS服务无效");
								reason = "DNS服务无效";
								createEvent(dnsConfig, reason);*/
							}
							
							
							
							
						}
						
					}catch(Exception ex){
						//不能进行FTP服务
						/*dns.setAlarm(true);
						//dbnode.setStatus(3);
						List alarmList = dns.getAlarmMessage();
						if(alarmList == null)alarmList = new ArrayList();
						dns.getAlarmMessage().add("DNS服务无效");
						reason = "DNS服务无效";
						createEvent(dnsConfig, reason);*/
						
					}
					
					//保存进历史数据
					
					Dnsmonitor_history dnsMonitor_history = new Dnsmonitor_history();
					dnsMonitor_history.setDns_id(dnsConfig.getId());
					dnsMonitor_history.setIs_canconnected(iscanconnected);
					dnsMonitor_history.setMon_time(Calendar.getInstance());
					dnsMonitor_history.setReason(reason);
					dnsmonitor_historyDao = new Dnsmonitor_historyDao();
					dnsmonitor_historyDao.save(dnsMonitor_history);	
					
					//UrlDataCollector udc = new UrlDataCollector();
					Dnsmonitor_realtime dnsmonitor_realtime = new Dnsmonitor_realtime();
					dnsmonitor_realtime.setDns_id(dnsConfig.getId());
					dnsmonitor_realtime.setIs_canconnected(iscanconnected);
					dnsmonitor_realtime.setReason(reason);
					dnsmonitor_realtime.setMon_time(Calendar.getInstance());
					//实时数据
					//ur.setUrl_id(url_id);
					if(old==true){
						dnsmonitor_realtime.setSms_sign(1);
					}
					else{
						dnsmonitor_realtime.setSms_sign(smssign);
					}
					
					
					//保存realtime
					if (old == true) {
						dnsmonitor_realtime.setId(dnsmonitor_realtimeold.getId());
						dnsmonitor_realtimeDao.update(dnsmonitor_realtime);
					}else {
						dnsmonitor_realtimeDao.save(dnsmonitor_realtime);
					}				
					
					//uh.setIs_refresh(ur.getIs_refresh());
					//uh.setIs_valid(ur.getIs_valid());
					
					if(sendeddata.containsKey("dnsserver:"+dnsConfig.getId())){
						sendeddata.remove("dnsserver:"+dnsConfig.getId());
					}
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					if(dnsmonitor_historyDao != null){
						dnsmonitor_historyDao.close();
					}
					if(dnsmonitor_realtimeDao != null){
						dnsmonitor_realtimeDao.close();
					}
				}	
			}
			
			//将采集到的DNS信息入库
			ProcessDnsData processDnsData = new ProcessDnsData();
			processDnsData.saveDnsData(dnsConfigList, ShareData.getAllDnsData());
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			
		}		
	}
	
	/**
	 * 采集DNS的数据
	 * @param dc  要采集的DNS  
	 * @return
	 */
	private Hashtable collectDNSData(DnsConfig dc) {
		Hashtable dnsDataHash = new Hashtable();
		List array = new ArrayList();
		String str = "";
		String defaultStr = "";
		String hostip = "";
		int zhuangtai = 0;
		String dnsip = "";
		String dns = "";
		String aaa = "";
		String primary = "";
		String responsible = "";
		String serial = "";
		String refresh = "";
		String retry = "";
		String expire = "";
		String dfault = "";
		String time = "";
		List mx = null;
		List ns = null;
		List cache = null;
		//得到服务器名 以及ip地址
		Process process = null;
		BufferedReader bf = null;
		try {
			process = Runtime.getRuntime().exec("cmd /c nslookup "+dc.getHostip()+"");
			bf = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while((str=bf.readLine())!=null){      
				array.add(str);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if (bf != null) {
				try {
					bf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(process != null){
				process.destroy();
			}
		}
		for(int i=0;i<array.size();i++){  
			if(array.get(i).toString().contains("Name:")){
				defaultStr = array.get(i).toString().substring(6);
				hostip = dc.getHostip();
				zhuangtai = 1;
			}
		}
      
		//得到域名所对应的地址
		List array1=new ArrayList();
		List arr1=new ArrayList();
		String str1=null;
		long lasting=System.currentTimeMillis();
		Process process1 = null;
		BufferedReader bf1 = null;
		try {
			process1 = Runtime.getRuntime().exec("cmd /c nslookup "+dc.getDns()+"");
			bf1 = new BufferedReader(new InputStreamReader(process1.getInputStream()));
			while((str1=bf1.readLine())!=null){      
				array1.add(str1);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if (bf1 != null) {
				try {
					bf1.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(process1 != null){
				process1.destroy();
			}
		}
       for(int j=0;j<array1.size();j++){  
    	   if(array1.get(j).toString().contains("Addresses:")){   
    		   dnsip = array1.get(j).toString().substring(10);
    		   dns = dc.getDns();
    	   }if(array1.get(j).toString().contains("Address:")){
    		   arr1.add(array1.get(j));
        	   if(arr1.size()==2){
        			dnsip = array1.get(j).toString().substring(8);
        			dns = dc.getDns();
        	   }
        	}
       }
       time = "响应时间："+(System.currentTimeMillis() - lasting)+"ms";
       
       //A记录
       List array2=new ArrayList();
       List arr2=new ArrayList();
       Process process2 = null;
       BufferedReader bf2 = null;
    	String str2=null;
    	if(dc.getDns().contains("www")){
			dns=dc.getDns().substring(4);
		} else {
			dns=dc.getDns();
		}
		try {
			process2 = Runtime.getRuntime().exec("cmd /c nslookup -qt=a "+dns+" "+dc.getHostip()+"");
			bf2 = new BufferedReader(new InputStreamReader(process2.getInputStream()));
			while((str2=bf2.readLine())!=null){      
				array2.add(str2);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if (bf2 != null) {
				try {
					bf2.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(process2 != null){
				process2.destroy();
			}
		}
		for(int j=0;j<array2.size();j++){  
			if(array2.get(j).toString().contains("Addresses:")){
			   aaa = array2.get(j).toString().substring(10);
			}if(array2.get(j).toString().contains("Address:")){
				arr2.add(array2.get(j));
				if(arr2.size()==2){
					aaa = array2.get(j).toString().substring(8);
				}
			}
		}
		
       //HINFO硬件配置信息
		List array3=new ArrayList();
		String str3=null;
		Process process3 = null;
		BufferedReader bf3 = null;
		try {
			process3 = Runtime.getRuntime().exec("cmd /c nslookup -qt=hinfo "+dns+" "+dc.getHostip()+"");
			bf3 = new BufferedReader(new InputStreamReader(process3.getInputStream()));
			while((str3 = bf3.readLine())!=null){      
				array3.add(str3);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if (bf3 != null) {
				try {
					bf3.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(process3 != null){
				process3.destroy();
			}
		}
		for(int j=0;j<array3.size();j++){  
    	   if(array3.get(j).toString().contains("primary")){
    		   primary = "主要名字服务器:"+array3.get(j).toString().substring(22);
    	   }
    	   if(array3.get(j).toString().contains("responsible")){
    		   responsible = "邮件地址:"+array3.get(j).toString().substring(24);
    	   }
    	   if(array3.get(j).toString().contains("serial")){
    		   serial = "文件版本:"+array3.get(j).toString().substring(10);
    	   }
    	   if(array3.get(j).toString().contains("refresh")){
    		   refresh = "重刷新时间:"+array3.get(j).toString().substring(10);
    	   }
    	   if(array3.get(j).toString().contains("retry")){
    		   retry = "重试时间:"+array3.get(j).toString().substring(10);
    	   }
    	   if(array3.get(j).toString().contains("expire")){
    		   expire = "有效时间:"+array3.get(j).toString().substring(10);
    	   }
    	   if(array3.get(j).toString().contains("default")){
    		   dfault = "TTL设置:"+array3.get(j).toString().substring(14);
    	   }
		}
       //MX记录
		List array4=new ArrayList();
    	String str4=null;
    	BufferedReader bf4 = null;
    	Process process4 = null;
    	if(dc.getDns().contains("www")){
			dns=dc.getDns().substring(4);
		} else {
			dns=dc.getDns();
		}
		try {
			process4 = Runtime.getRuntime().exec("cmd /c nslookup -qt=mx "+dns+" "+dc.getHostip()+"");
			bf4 = new BufferedReader(new InputStreamReader(process4.getInputStream()));
			while((str4=bf4.readLine())!=null){      
				array4.add(str4);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if (bf4 != null) {
				try {
					bf4.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(process4 != null){
				process4.destroy();
			}
		}
        List array5=new ArrayList();
        for(int j=0;j<array4.size();j++){    
        	if(array4.get(j).toString().contains(dns)){    
        		array5.add(array4.get(j).toString());
        		mx = array5;
        	}
        }
       
       //NS记录
        List array6=new ArrayList();
    	String str6=null;
    	Process process6 = null;
    	BufferedReader bf6 = null;
    	if(dc.getDns().contains("www")){
    		dns=dc.getDns().substring(6);
		} else {
			dns=dc.getDns();
		}
		try {
			process6 = Runtime.getRuntime().exec("cmd /c nslookup -qt=ns " + dns + " "+ dc.getHostip() + "");
			bf6 = new BufferedReader(new InputStreamReader(process6.getInputStream()));
			while ((str6 = bf6.readLine()) != null) {
				array6.add(str6);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bf6 != null) {
				try {
					bf6.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(process6 != null){
				process6.destroy();
			}
		}
        List array7=new ArrayList();
        for(int j=0;j<array6.size();j++){    
        	if(array6.get(j).toString().contains(dns)){    
        		array7.add(array6.get(j).toString());
        		ns = array7;
        	}
        }
        //缓存记录
        //NS记录
        List array8=new ArrayList();
    	String str8=null;
    	if(dc.getDns().contains("www")){
			dns=dc.getDns().substring(8);
		} else{
			dns=dc.getDns();
		}
		Process process8 = null;
		BufferedReader bf8 = null;
		try {
			process8 = Runtime.getRuntime().exec("cmd /c nslookup -d3 "+dc.getHostip()+"");
			bf8 = new BufferedReader(new InputStreamReader(process8.getInputStream()));
			while ((str8 = bf8.readLine()) != null) {
				array8.add(str8);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bf8 != null) {
				try {
					bf8.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(process8 != null){
				process8.destroy();
			}
		}
        for(int k=0;k<array8.size();k++){
        	if(array8.get(k).toString().contains("opcode")){  
        	}
        }
        cache = array8;
        dnsDataHash.put("time", time);
        dnsDataHash.put("default", defaultStr);
        dnsDataHash.put("hostip", hostip);
        dnsDataHash.put("zhuangtai", zhuangtai);
        dnsDataHash.put("dnsip", dnsip);
        dnsDataHash.put("dns", dns);
        dnsDataHash.put("aaa", aaa);
        dnsDataHash.put("primary", primary);
        dnsDataHash.put("responsible", responsible);
        dnsDataHash.put("serial", serial);
        dnsDataHash.put("refresh", refresh);
        dnsDataHash.put("retry", retry);
        dnsDataHash.put("expire", expire);
        dnsDataHash.put("dfault", dfault);
        dnsDataHash.put("mx", mx);
        dnsDataHash.put("ns", ns);
        dnsDataHash.put("cache", cache);
        //存入内存
        ShareData.getAllDnsData().put(dc.getId(), dnsDataHash);
		return dnsDataHash;
	}

	public void createEvent(DnsConfig dnsConfig, String reason){
		Calendar date=Calendar.getInstance();
		String time = sdf.format(date.getTime());
		EventList event = new EventList();
		event.setEventtype("dnsserver");
		event.setEventlocation(dnsConfig.getDnsip());
		event.setBusinessid(dnsConfig.getNetid());
		event.setManagesign(new Integer(0));
		event.setReportman("monitorpc");
		event.setRecordtime(new GregorianCalendar());		
		String errorcontent=time+" "+dnsConfig.getUsername()+"(IP:"+dnsConfig.getDnsip()+")：DNS服务故障";		 		
		event.setContent(errorcontent);
		Integer level = new Integer(2);
		event.setLevel1(level);
		//reason="FTP服务无效";
		//EventListDao eventListDao = null ;
		try{
			//eventListDao = new EventListDao();
			//eventListDao.save(event);
		}catch(Exception e){
			
		}finally{
			//eventListDao.close();
		}
		
		Vector eventtmpV = new Vector();
		eventtmpV.add(event);
		createSMS("dnsserver",dnsConfig.getId()+"",errorcontent,dnsConfig.getDnsip());
	}
	

	 public void createSMS(String dnsserver,String dns_id,String errmsg,String ftpstr){
	 	//建立短信		 	
	 	//从内存里获得当前这个IP的PING的值
	 	Calendar date=Calendar.getInstance();
	 	AlarmInfoDao alarmdao=null;
	 	try{
 			if (!sendeddata.containsKey(dnsserver+":"+dns_id)){
 				//若不在，则建立短信，并且添加到发送列表里
	 			Smscontent smscontent = new Smscontent();
	 			smscontent.setMessage(errmsg);
	 			smscontent.setObjid(dns_id);
	 			Calendar _tempCal = Calendar.getInstance();				
				Date _cc = _tempCal.getTime();
				String _time = sdf.format(_cc);
	 			smscontent.setRecordtime(_time);
	 			smscontent.setSubtype("dns");
	 			smscontent.setLevel(3+"");
	 			//发送短信
	 			SmscontentDao smsmanager=new SmscontentDao();
	 			smsmanager.sendURLSmscontent(smscontent);
				sendeddata.put(dnsserver+":"+dns_id,date);		 					 				
 			}else{
 				//若在，则从已发送短信列表里判断是否已经发送当天的短信
 				Calendar formerdate =(Calendar)sendeddata.get(dnsserver+":"+dns_id);		 				
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
	 			
	 			long subvalue = current.getTime()-last.getTime();			 			
	 			if (subvalue/(1000*60*60*24)>=1){
	 				//超过一天，则再发信息
		 			Smscontent smscontent = new Smscontent();
		 			//String time = sdf.format(date.getTime());
		 			smscontent.setMessage(errmsg);
		 			smscontent.setObjid(dns_id);
		 			smscontent.setLevel(3+"");
		 			//发送短信
		 			SmscontentDao smsmanager=new SmscontentDao();
		 			smsmanager.sendURLSmscontent(smscontent);
		 			Calendar _tempCal = Calendar.getInstance();				
					Date _cc = _tempCal.getTime();
					String _time = sdf.format(_cc);
		 			smscontent.setRecordtime(_time);
		 			smscontent.setSubtype("dns");
					//修改已经发送的短信记录	
					sendeddata.put(dnsserver+":"+dns_id,date);	
		 		}else{
		 			//则写声音告警数据
					//向声音告警表里写数据
					AlarmInfo alarminfo = new AlarmInfo();
					alarminfo.setContent(errmsg);
					alarminfo.setIpaddress(errmsg);
					alarminfo.setLevel1(new Integer(2));
					alarminfo.setRecordtime(Calendar.getInstance());
					alarmdao = new AlarmInfoDao();
	                alarmdao.save(alarminfo);
		 			
		 			/*
					Calendar tempCal = Calendar.getInstance();						
					java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String time = sdf.format(cc);					

					String queryStr = "insert into alarminfor(content,ipaddress,level1,recordtime) values('"+errmsg+"','"+ftpstr+"',2,to_date('"+time+"','YYYY-MM-DD HH24:MI:SS'))";					
					Connection con = null;			
					PreparedStatement stmt = null;
					ResultSet rs = null;
					try{
						con=DataGate.getCon();
						stmt = con.prepareStatement(queryStr);
						stmt.execute();
						stmt.close();						
					}catch(Exception ex){
						ex.printStackTrace();
						//rs.close();
					}finally{
						try{
						stmt.close();
						DataGate.freeCon(con);
						}catch(Exception exp){
							//
						}
					}
					*/
	
		 		}
 			}	 			 			 			 			 	
	 	}catch(Exception e){
	 		e.printStackTrace();
	 	}
	 	finally
	 	{
	 		alarmdao.close();
	 	}
	 }
	
}
