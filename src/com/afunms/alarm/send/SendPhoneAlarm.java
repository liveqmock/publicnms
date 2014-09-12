package com.afunms.alarm.send;

import com.afunms.alarm.model.AlarmWayDetail;
import com.afunms.common.util.SysLogger;
import com.afunms.event.model.EventList;

public class SendPhoneAlarm implements SendAlarm{

	public void sendAlarm(EventList eventList,AlarmWayDetail alarmWayDetail) {
		// TODO Auto-generated method stub
		SysLogger.info("==============发送电话告警==================");
	}

}
