package com.afunms.alarm.send;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.model.AlarmWay;
import com.afunms.alarm.model.AlarmWayDetail;
import com.afunms.event.model.CheckEvent;

public interface AlarmFilter {
	
	public boolean isSendAlarm(CheckEvent checkEvent, AlarmIndicatorsNode alarmIndicatorsNode , AlarmWay alarmWay , AlarmWayDetail alarmWayDetail);
}
