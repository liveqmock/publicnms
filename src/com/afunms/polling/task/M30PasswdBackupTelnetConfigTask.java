package com.afunms.polling.task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;


import org.apache.log4j.Logger;

import com.afunms.alarm.dao.AlarmWayDetailDao;
import com.afunms.alarm.model.AlarmWayDetail;
import com.afunms.alarm.send.SendAlarmDispatcher;
import com.afunms.alarm.send.SendAlarmFilter;
import com.afunms.capreport.common.DateTime;
import com.afunms.capreport.dao.BaseDaoImp;
import com.afunms.event.model.EventList;
import com.database.config.SystemConfig;
/**
 * 定时提醒修改密码
 * @author ChengFeng
 *
 */
public class M30PasswdBackupTelnetConfigTask  extends MonitorTask{

	private static Logger log = Logger.getLogger(M30PasswdBackupTelnetConfigTask.class);

	private final String hms = " 00:00:00";

	/*
	 * (non-Javadoc)
	 *  
	 * @see java.util.TimerTask#run() 
	 */
	@Override
	public void run() {
		subscribe();
//		System.gc();
	}

	/**
	 * 定时 
	 */
	private void subscribe() { 
		DateTime dt = new DateTime(); 
		String time = dt.getMyDateTime(DateTime.Datetime_Format_14);
		
		String sql = "SELECT * FROM sys_pwdbackup_telnetconfig s WHERE status = '是' and s.BACKUP_DATE > 10000 AND s.BACKUP_DATE <= "
				+ time;
		ArrayList<Map<String, String>> ssconfAL=null;
		//在配置文件中设置是否启动定时巡检 wxy add
		String flag= SystemConfig.getConfigInfomation("Agentconfig","Pwdserver");
		
		if(flag!=null&&flag.equals("enable")){
			BaseDaoImp cd = new BaseDaoImp();
			ssconfAL = cd.executeQuery(sql);
			
		}
		Map<String, String> ssidAL = null;
		if (ssconfAL != null) {
			log.info("-------------------------------(定时提醒更新密码)定时器执行时间：" + dt.getMyDateTime(DateTime.Datetime_Format_2)
					+ "-------------------------------");
			try {
				for (int i = 0; i < ssconfAL.size(); i++) {
					ssidAL = ssconfAL.get(i);
					String status = ssidAL.get("status");//状态
					String telnetconfigips = ssidAL.get("telnetconfigips");//IP地址
					String backup_sendfrequency = ssidAL.get("BACKUP_SENDFREQUENCY");//提醒的频率
					String backup_time_month = ssidAL.get("BACKUP_TIME_MONTH");//
					String backup_time_week = ssidAL.get("BACKUP_TIME_WEEK");//
					String backup_time_day = ssidAL.get("BACKUP_TIME_DAY");//
					String backup_time_hou = ssidAL.get("BACKUP_TIME_HOU");//
					String warntype = ssidAL.get("warntype");////提醒方式
					//String bkpType = ssidAL.get("bkpType");//
					boolean istrue = false;
					// 发送频率，1:每天;2:每周;3:每月;4每季度;5每年
					/**
					 * 判断时间间隔
					 * 获取小时，范围0-23。
					 * 获取一月中的第几天取值范围1-31。
					 * 取一个星期的第几天。 周末为一个星期的第一天；
					 * 获取月份，范围1-12。
					 */
					if ("每天".equals(backup_sendfrequency)) {
							if (backup_time_hou.contains("/" + (dt.getHours() < 10 ? "0" + dt.getHours() : dt.getHours())
									+ "/")) {
								istrue = true;
							}
						} else if ("每周".equals(backup_sendfrequency)) {
							if (backup_time_week.contains("/" + (dt.getDay() - 1) + "/")
									&& backup_time_hou.contains("/"
											+ (dt.getHours() < 10 ? "0" + dt.getHours() : dt.getHours()) + "/")) {
								istrue = true;
							}
						} else if ("每月".equals(backup_sendfrequency)) {
							if (backup_time_day.contains("/" + (dt.getDate() < 10 ? "0" + dt.getDate() : dt.getDate())
									+ "/")
									&& backup_time_hou.contains("/"
											+ (dt.getHours() < 10 ? "0" + dt.getHours() : dt.getHours()) + "/")) {
								istrue = true;
							}
						} else if ("每季".equals(backup_sendfrequency)) {
							if (backup_time_month.contains("/" + (dt.getMonth() < 10 ? "0" + dt.getMonth() : dt.getMonth())
									+ "/")
									&& backup_time_day.contains("/"
											+ (dt.getDate() < 10 ? "0" + dt.getDate() : dt.getDate()) + "/")
									&& backup_time_hou.contains("/"
											+ (dt.getHours() < 10 ? "0" + dt.getHours() : dt.getHours()) + "/")) {
								istrue = true;
							}
						} else if ("每年".equals(backup_sendfrequency)) {
							if (backup_time_month.contains("/" + (dt.getMonth() < 10 ? "0" + dt.getMonth() : dt.getMonth())
									+ "/")
									&& backup_time_day.contains("/"
											+ (dt.getDate() < 10 ? "0" + dt.getDate() : dt.getDate()) + "/")
									&& backup_time_hou.contains("/"
											+ (dt.getHours() < 10 ? "0" + dt.getHours() : dt.getHours()) + "/")) {
								istrue = true;
							}
						}
					
					if (istrue) {
						log.info("定时提醒更新密码开始--passwdtimingbackup_telnetconfig=" + telnetconfigips);
					    
						//收集要发的短信内容
						if(!telnetconfigips.equals("") && telnetconfigips != null){
							String[] ips = telnetconfigips.split(",");//取出要提醒的设备IP
							EventList eventList = new EventList();	
							eventList.setRecordtime(Calendar.getInstance());
							 
							for(String ip : ips){
								if(ip != null && !ip.equals("") && !ip.equals(",") && "是".equals(status)){
									//根据提示的类型进行提醒
									//1、根据提醒方式提取出提醒的内容，以及提醒细节alarmWayDetail
									//2、新建一个EvenList
									eventList.setContent("IP地址为："+ip+"的设备登陆密码已经超过了您所设定的有效时间，请您确认是否需要修改密码！");
									List<AlarmWayDetail> list = getAlarmWayDetail(warntype);
									if(list == null || list.size() == 0){
										//SysLogger.info("### 告警指标: " + alarmIndicatorsNode.getName() +  " 无告警详细配置 不告警 ###");
									}else{
										SendAlarmFilter sendAlarmFilter = new SendAlarmFilter();
										for(int k = 0 ; k < list.size(); k++){
											AlarmWayDetail alarmWayDetail = list.get(k); 
											//System.out.println("!!!!!!!!!!!!!!i do it !&!!!!!!!"); 
											SendAlarmDispatcher.sendAlarm(eventList , alarmWayDetail);
											
										}
									}   	
						
									}
								}	
							}	 
						}
					}
			} catch (Exception e) {
				log.error("", e);
			}
		}
	}
	
	private List<AlarmWayDetail> getAlarmWayDetail(String alarmWayId){
		List<AlarmWayDetail> list = null;
		AlarmWayDetailDao alarmWayDetailDao = new AlarmWayDetailDao();
		try {
			list = (List<AlarmWayDetail>)alarmWayDetailDao.findByAlarmWayId(alarmWayId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			alarmWayDetailDao.close();
		}
		return list;
	}


}
