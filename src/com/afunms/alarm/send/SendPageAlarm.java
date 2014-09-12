package com.afunms.alarm.send;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.afunms.alarm.model.AlarmWayDetail;
import com.afunms.common.util.SysLogger;
import com.afunms.event.dao.AlarmInfoDao;
import com.afunms.event.dao.EventListDao;
import com.afunms.event.model.AlarmInfo;
import com.afunms.event.model.EventList;

public class SendPageAlarm implements SendAlarm {
	
	public void sendAlarm(EventList eventList,String uid){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String startTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date())+ " 00:00:00";
        String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		EventListDao eventListDao = new EventListDao();
		try {
			Calendar tempCal = (Calendar)eventList.getRecordtime();			   				
			Date cc = tempCal.getTime();
			String recordtime = sdf.format(cc);
			if(eventList.getLevel1()>0){
				List list = eventListDao.getEventlist(startTime,endTime,eventList.getManagesign()+"",eventList.getLevel1()+"",eventList.getBusinessid(),eventList.getNodeid(),eventList.getSubentity());
				if(list!=null&&list.size()>0){   
					EventList vo = (EventList) list.get(0);
					eventListDao.update(recordtime,eventList.getContent(),vo.getId()+"");
				} else { 
					eventList.setLasttime(recordtime);
					eventListDao.save(eventList);
					//向服务台发送告警
				}
//				默认情况下,都是向向声音告警表里写数据
				// 向声音告警表里写数据
				AlarmInfo alarminfo = new AlarmInfo();
				alarminfo.setContent(eventList.getContent());
				alarminfo.setIpaddress(eventList.getEventlocation());
				alarminfo.setLevel1(new Integer(2));
				alarminfo.setRecordtime(Calendar.getInstance());
				alarminfo.setType("");
				AlarmInfoDao alarmdao = new AlarmInfoDao();
				try {
					alarmdao.save(alarminfo);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					alarmdao.close();
				}
			} else {
				///////////////////////////////
				List list = eventListDao.getEventlist(startTime,endTime,eventList.getManagesign()+"","99",eventList.getBusinessid(),eventList.getNodeid(),eventList.getSubentity());
				if(list!=null&&list.size()>0){
					for(int i=0; i<list.size(); i++){
						EventList vo = (EventList) list.get(i);
						if(vo.getLevel1()>0){
							String time = null;// 告警持续时间，默认分钟为单位
							long timeLong = 0;
							tempCal = (Calendar)vo.getRecordtime();
							cc = tempCal.getTime();
							String collecttime = sdf.format(cc);
							Date firstAlarmDate = null;
							try {
								firstAlarmDate = sdf.parse(collecttime);
							} catch (ParseException e) {
								e.printStackTrace();
							}
							if (firstAlarmDate != null) {
								timeLong = new Date().getTime() - firstAlarmDate.getTime();
							}
							if (timeLong < 1000 * 60) {// 小于1分钟,秒
								time = timeLong / 1000 + "秒";
							} else {// 小于1小时,分
								time = timeLong / (60 * 1000) + "分";
							}
							eventListDao.update(recordtime,"0",vo.getContent()+" (该告警已恢复，告警持续时间"+time+")", vo.getId()+"");
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			eventListDao.close();
		}
	}
	public void sendAlarm(List<EventList> list){
		EventListDao eventListDao = new EventListDao();
		try {
			eventListDao.save(list);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			eventListDao.close();
		}
	}
	public void sendAlarm(EventList eventList, AlarmWayDetail alarmWayDetail) {
		// TODO Auto-generated method stub
		
	}
}
