package com.afunms.alarm.send;

import com.afunms.alarm.model.AlarmWayDetail;
import com.afunms.alarm.util.AlarmWayUtil;
import com.afunms.common.util.SysLogger;
import com.afunms.event.model.EventList;

/**
 * 告警发送转发器
 * @author nielin
 * @date 2010-10-22
 *
 */
public class SendAlarmDispatcher {
	
	public static void sendAlarm(EventList eventList , AlarmWayDetail alarmWayDetail){
		SendAlarm sendAlarm = getSendAlarm(alarmWayDetail.getAlarmCategory());
		sendAlarm.sendAlarm(eventList,alarmWayDetail);
	}
	
	private static SendAlarm getSendAlarm(String category){
		if(category == null){
			SysLogger.info("告警发送类型为空");
		}else if(AlarmWayUtil.ALARM_WAY_CATEGORY_PAGE.equals(category)){
			return new SendPageAlarm();
		} else if(AlarmWayUtil.ALARM_WAY_CATEGORY_SOUND.equals(category)){
			return new SendSoundAlarm();
		} else if(AlarmWayUtil.ALARM_WAY_CATEGORY_MAIL.equals(category)){
			return new SendMailAlarm();
		} else if(AlarmWayUtil.ALARM_WAY_CATEGORY_SMS.equals(category)){
			return new SendSMSAlarm();
		} else if(AlarmWayUtil.ALARM_WAY_CATEGORY_PHONE.equals(category)){
			return new SendPhoneAlarm();
		} else if(AlarmWayUtil.ALARM_WAY_CATEGORY_DESKTOP.equals(category)){
			return new SendDesktopAlarm();
		} else {
			SysLogger.info("没有此类告警发送类型");
		}
		return null;
	}
	
}
