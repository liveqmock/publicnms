/*
 * Created on 2010-01-28
 *
 * 此类用于轮询温湿度传感器的 task 类 
 * 
 * The class is a polling task for temperature humidity 
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.afunms.polling.task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.afunms.application.dao.SerialNodeDao;
import com.afunms.application.dao.TemperatureHumidityDao;
import com.afunms.application.dao.TemperatureHumidityThresholdDao;
import com.afunms.application.model.SerialNode;
import com.afunms.application.model.TemperatureHumidityConfig;
import com.afunms.application.model.TemperatureHumidityThresholdConfig;
import com.afunms.common.util.SysLogger;
import com.afunms.event.dao.EventListDao;
import com.afunms.event.model.EventList;



/**
 * @author nielin
 * 
 * created on 2010-01-28
 * 
 * 此类用于轮询温湿度传感器的 task 类 
 * 
 * The class is a polling task for temperature humidity 
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class TemperatureHumidityTask extends MonitorTask {
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public TemperatureHumidityTask(){
		super();
	}
		
	public void run(){
		
		// 告警原因
		String reason = "";
		
		SerialNodeDao serialNodeDao = new SerialNodeDao();
		
		// 获得所有需要监控的温湿度传感器设备
		// Get all the necessary equipment to monitor temperature and humidity sensor
		List list = null;
		try {
			list = serialNodeDao.findByMonflag("1");
		} catch (RuntimeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}finally{
			serialNodeDao.close();
		}
		
		if(list == null || list.size() == 0){
			return;
		}
		
		
		TemperatureHumidityUtil temperatureHumidityUtil = new TemperatureHumidityUtil();
		for(int i = 0; i < list.size(); i++){
			SerialNode serialNode = null;
			try {
				// 对每个设备进行循环取温湿度数据
				// Loop for each device to get temperature and humidity data
				serialNode = (SerialNode)list.get(i);
				
				// 初始化配置温湿度传感器的参数：端口号 , 设备地址 , 波特率 , 数据位 , 停止位 , 奇偶校验位
				// Initialization, configuration parameters of temperature and humidity sensors: 
				//    port number, device address, baud rate, data bits, stop bits, parity bit
				temperatureHumidityUtil.initialize(serialNode.getSerialPortId(), serialNode.getAddress(),
						Integer.valueOf(serialNode.getBaudRate()), Integer.valueOf(serialNode.getDatabits()),
						Integer.valueOf(serialNode.getStopbits()), Integer.valueOf(serialNode.getParity()));
				
				// 执行命令
				// execute
				boolean result = temperatureHumidityUtil.execute();
				String temperature = "";
				String humidity = "";
				if(result){
					// 如果执行成功 获得温度和湿度
					// If successfully execute, then to get temperature and humidity
					temperature = temperatureHumidityUtil.getTemperature();
					humidity = temperatureHumidityUtil.getHumidity();
				}else{
					reason = "温湿度传感器服务无效";
					createEvent(serialNode, reason);
				}
				if(temperature != null && humidity != null && temperature.length()>0 && humidity.length() >0){
					// 如果温湿度都不为空 则将其放入到一个持久化对象中 并存入数据库中
					// If the temperature and humidity are not null ,
					// put them into a JavaBean and save in the database
					TemperatureHumidityConfig temperatureHumidityConfig = new TemperatureHumidityConfig();
					temperatureHumidityConfig.setNode_id(String.valueOf(serialNode.getId()));
					temperatureHumidityConfig.setTemperature(temperature);
					temperatureHumidityConfig.setHumidity(humidity);
					temperatureHumidityConfig.setTime(sdf.format(new Date()));
					
					TemperatureHumidityDao temperatureHumidityDao = new TemperatureHumidityDao();
					try{
						
						temperatureHumidityDao.save(temperatureHumidityConfig);
					}catch(Exception e){
						e.printStackTrace();
					}finally{
						temperatureHumidityDao.close();
					}
					
					TemperatureHumidityThresholdConfig temperatureHumidityThresholdConfig = null;
					
					TemperatureHumidityThresholdDao temperatureHumidityThresholdDao = new TemperatureHumidityThresholdDao();
					try {
						temperatureHumidityThresholdConfig =  temperatureHumidityThresholdDao.findByNodeId(String.valueOf(serialNode.getId()));
					} catch (RuntimeException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}finally{
						temperatureHumidityThresholdDao.close();
					}
					
					
					try {
						reason = getReason(temperatureHumidityConfig, temperatureHumidityThresholdConfig);
						
						if(reason !=null && (!"".equals(reason)) && reason.length() >0){
							createEvent(serialNode, reason);
						}
					} catch (RuntimeException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						reason = "温湿度传感器服务无效";
						createEvent(serialNode, reason);
					}
					
				}else{
					reason = "温湿度传感器数据无效";
					createEvent(serialNode, reason);
				}
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				reason = "温湿度传感器服务无效";
				createEvent(serialNode, reason);
			}
			
		}
	}
	
	/**
	 * 
	 * This function is used to create alarm causes
	 * 
	 * @author nielin
	 * create on 2010-02-22
	 * @param temperatureHumidityConfig
	 * @param temperatureHumidityThresholdConfig
	 * @return
	 */
	private String getReason(TemperatureHumidityConfig temperatureHumidityConfig, TemperatureHumidityThresholdConfig temperatureHumidityThresholdConfig){
		double temperature = Double.valueOf(temperatureHumidityConfig.getTemperature());
		double humidity = Double.valueOf(temperatureHumidityConfig.getHumidity());
		double minTemperature = Double.valueOf(temperatureHumidityThresholdConfig.getMinTemperature());
		double maxTemperature = Double.valueOf(temperatureHumidityThresholdConfig.getMaxTemperature());
		double minHumidity = Double.valueOf(temperatureHumidityThresholdConfig.getMinHumidity());
		double maxHumidity = Double.valueOf(temperatureHumidityThresholdConfig.getMaxHumidity());
		
		String reason = "";
		
		if(temperature > maxTemperature){
			reason =  reason + "   温度超过最大阀值"; 
		}
		if(temperature < minTemperature){
			reason =  reason + "   温度超过最小阀值"; 
		}
		if(humidity > maxHumidity){
			reason =  reason + "   湿度超过最大阀值"; 
		}
		if(humidity < minHumidity){
			reason =  reason + "   湿度超过最小阀值"; 
		}
		
		return reason;
	}
	
	/**
	 * This function is through the serialNode and reason to create alarm events
	 * 
	 * @author nielin
	 * create on 2010-02-05
	 * @param serialNode
	 * @param reason
	 */
	public void createEvent(SerialNode serialNode , String reason){
		Calendar date=Calendar.getInstance();
		String time = sdf.format(date.getTime());
		EventList eventList = new EventList();
		String content = time+ "  " +  serialNode.getName() + "地址：" + serialNode.getName() + reason ;
		eventList.setEventlocation(serialNode.getName());
		eventList.setEventtype("poll");
		eventList.setContent(content);
		eventList.setLevel1(3);
		eventList.setManagesign(0);
		eventList.setRecordtime(new GregorianCalendar());
		eventList.setReportman("系统轮询");
		eventList.setNodeid(serialNode.getId());
		eventList.setSubtype("temperaturehumidity");
		EventListDao  eventListDao = new EventListDao();
		try {
			eventListDao.save(eventList);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			eventListDao.close();
		}
	}
	
}
	
//	public void createEvent(FTPConfig ftpConfig, String reason){
//		Calendar date=Calendar.getInstance();
//		String time = sdf.format(date.getTime());
//		EventList event = new EventList();
//		event.setEventtype("ftpserver");
//		event.setEventlocation(ftpConfig.getIpaddress());
//		event.setBusinessid(ftpConfig.getBid());
//		event.setManagesign(new Integer(0));
//		event.setReportman("monitorpc");
//		event.setRecordtime(new GregorianCalendar());		
//		String errorcontent=time+" "+ftpConfig.getName()+"(IP:"+ftpConfig.getIpaddress()+")：FTP服务故障";		 		
//		event.setContent(errorcontent);
//		Integer level = new Integer(2);
//		event.setLevel1(level);
//		//reason="FTP服务无效";
//		//EventListDao eventListDao = null ;
//		try{
//			//eventListDao = new EventListDao();
//			//eventListDao.save(event);
//		}catch(Exception e){
//			
//		}finally{
//			//eventListDao.close();
//		}
//		
//		Vector eventtmpV = new Vector();
//		eventtmpV.add(event);
//		createSMS("ftpserver",ftpConfig.getId()+"",errorcontent,ftpConfig.getIpaddress());
//	}
//	
//
//	 public void createSMS(String ftpserver,String ftp_id,String errmsg,String ftpstr){
//	 	//建立短信		 	
//	 	//从内存里获得当前这个IP的PING的值
//	 	Calendar date=Calendar.getInstance();
//	 	try{
// 			if (!sendeddata.containsKey(ftpserver+":"+ftp_id)){
// 				//若不在，则建立短信，并且添加到发送列表里
//	 			Smscontent smscontent = new Smscontent();
//	 			smscontent.setMessage(errmsg);
//	 			smscontent.setObjid(ftp_id);
//	 			Calendar _tempCal = Calendar.getInstance();				
//				Date _cc = _tempCal.getTime();
//				String _time = sdf.format(_cc);
//	 			smscontent.setRecordtime(_time);
//	 			smscontent.setSubtype("ftp");
//	 			smscontent.setLevel(3+"");
//	 			//发送短信
//	 			SmscontentDao smsmanager=new SmscontentDao();
//	 			smsmanager.sendURLSmscontent(smscontent);
//				sendeddata.put(ftpserver+":"+ftp_id,date);		 					 				
// 			}else{
// 				//若在，则从已发送短信列表里判断是否已经发送当天的短信
// 				Calendar formerdate =(Calendar)sendeddata.get(ftpserver+":"+ftp_id);		 				
//	 			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//	 			Date last = null;
//	 			Date current = null;
//	 			Calendar sendcalen = formerdate;
//	 			Date cc = sendcalen.getTime();
//	 			String tempsenddate = formatter.format(cc);
//	 			
//	 			Calendar currentcalen = date;
//	 			cc = currentcalen.getTime();
//	 			last = formatter.parse(tempsenddate);
//	 			String currentsenddate = formatter.format(cc);
//	 			current = formatter.parse(currentsenddate);
//	 			
//	 			long subvalue = current.getTime()-last.getTime();			 			
//	 			if (subvalue/(1000*60*60*24)>=1){
//	 				//超过一天，则再发信息
//		 			Smscontent smscontent = new Smscontent();
//		 			//String time = sdf.format(date.getTime());
//		 			smscontent.setMessage(errmsg);
//		 			smscontent.setObjid(ftp_id);
//		 			smscontent.setLevel(3+"");
//		 			//发送短信
//		 			SmscontentDao smsmanager=new SmscontentDao();
//		 			smsmanager.sendURLSmscontent(smscontent);
//		 			Calendar _tempCal = Calendar.getInstance();				
//					Date _cc = _tempCal.getTime();
//					String _time = sdf.format(_cc);
//		 			smscontent.setRecordtime(_time);
//		 			smscontent.setSubtype("ftp");
//					//修改已经发送的短信记录	
//					sendeddata.put(ftpserver+":"+ftp_id,date);	
//		 		}else{
//		 			//则写声音告警数据
//					//向声音告警表里写数据
//					AlarmInfo alarminfo = new AlarmInfo();
//					alarminfo.setContent(errmsg);
//					alarminfo.setIpaddress(errmsg);
//					alarminfo.setLevel1(new Integer(2));
//					alarminfo.setRecordtime(Calendar.getInstance());
//					AlarmInfoDao alarmdao = new AlarmInfoDao();
//	                alarmdao.save(alarminfo);
//		 			
//		 			/*
//					Calendar tempCal = Calendar.getInstance();						
//					java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//					String time = sdf.format(cc);					
//
//					String queryStr = "insert into alarminfor(content,ipaddress,level1,recordtime) values('"+errmsg+"','"+ftpstr+"',2,to_date('"+time+"','YYYY-MM-DD HH24:MI:SS'))";					
//					Connection con = null;			
//					PreparedStatement stmt = null;
//					ResultSet rs = null;
//					try{
//						con=DataGate.getCon();
//						stmt = con.prepareStatement(queryStr);
//						stmt.execute();
//						stmt.close();						
//					}catch(Exception ex){
//						ex.printStackTrace();
//						//rs.close();
//					}finally{
//						try{
//						stmt.close();
//						DataGate.freeCon(con);
//						}catch(Exception exp){
//							//
//						}
//					}
//					*/
//	
//		 		}
// 			}	 			 			 			 			 	
//	 	}catch(Exception e){
//	 		e.printStackTrace();
//	 	}
//	 }
//	
//	

