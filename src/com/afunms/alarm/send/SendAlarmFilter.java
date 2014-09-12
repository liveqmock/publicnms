package com.afunms.alarm.send;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.model.AlarmWay;
import com.afunms.alarm.model.AlarmWayDetail;
import com.afunms.event.model.CheckEvent;

/**
 * 告警发送过滤器 用于判断告警是否发送
 * 先判断告警发送的方式的类型 ， 然后判断日期 ， 再判断时间
 * @author nielin
 * @date 2010-10-22
 *
 */
public class SendAlarmFilter {
	
	public boolean isSendAlarm( CheckEvent checkEvent ,AlarmIndicatorsNode alarmIndicatorsNode , AlarmWay alarmWay , AlarmWayDetail alarmWayDetail){
		AlarmTimesFilter alarmTimesFilter = new AlarmTimesFilter();
		AlarmCatgoryFilter alarmCatgoryFilter = new AlarmCatgoryFilter();
		AlarmDateFilter alarmDateFilter = new AlarmDateFilter();
		AlarmTimeFilter alarmTimeFilter = new AlarmTimeFilter();
		alarmTimesFilter.setNextFilter(alarmCatgoryFilter);
		alarmCatgoryFilter.setNextFilter(alarmDateFilter);
		alarmDateFilter.setNextFilter(alarmTimeFilter);
		return alarmTimesFilter.isSendAlarm(checkEvent ,alarmIndicatorsNode , alarmWay, alarmWayDetail);
	}
}
