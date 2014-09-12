package com.afunms.alarm.send;

import java.util.Calendar;
import java.util.Date;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.model.AlarmWay;
import com.afunms.alarm.model.AlarmWayDetail;
import com.afunms.common.util.SysLogger;
import com.afunms.event.model.CheckEvent;

public class AlarmTimeFilter implements AlarmFilter{
	
	public AlarmFilter alarmFilter = null;
	
	public boolean isSendAlarm(CheckEvent checkEvent, AlarmIndicatorsNode alarmIndicatorsNode , AlarmWay alarmWay , AlarmWayDetail alarmWayDetail){
		boolean result = false;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		int currentTime = calendar.get(Calendar.HOUR_OF_DAY);
		int startTime = Integer.valueOf(alarmWayDetail.getStartTime());
		int endTime = Integer.valueOf(alarmWayDetail.getEndTime());
		if(currentTime >= startTime && currentTime <= endTime){
			result = true;
		}
		if(!result){
			SysLogger.info( alarmWay.getName() + "====告警类型===" + alarmWayDetail.getAlarmCategory() + "===不再告警时间段内==不告警==");
			return result;
		} else if(alarmFilter != null){
			return alarmFilter.isSendAlarm(checkEvent ,alarmIndicatorsNode , alarmWay, alarmWayDetail);
		} 
		return result;
		
	}
	
	public void setNextFilter(AlarmFilter alarmFilter){
		this.alarmFilter = alarmFilter;
	}
	
}
