package com.afunms.alarm.send;

import java.util.Calendar;
import java.util.Date;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.model.AlarmWay;
import com.afunms.alarm.model.AlarmWayDetail;
import com.afunms.alarm.util.AlarmWayUtil;
import com.afunms.common.util.SysLogger;
import com.afunms.event.model.CheckEvent;

public class AlarmDateFilter implements AlarmFilter{
	
	public AlarmFilter alarmFilter = null;
	
	public boolean isSendAlarm(CheckEvent checkEvent, AlarmIndicatorsNode alarmIndicatorsNode , AlarmWay alarmWay , AlarmWayDetail alarmWayDetail){
		boolean result = false;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		int currentDate = 0;
		if(AlarmWayUtil.DATE_TYPE_WEEK.equals(alarmWayDetail.getDateType())){
			currentDate = calendar.get(Calendar.DAY_OF_WEEK);
		}else if (AlarmWayUtil.DATE_TYPE_MONTH.equals(alarmWayDetail.getDateType())){
			currentDate = calendar.get(Calendar.DAY_OF_MONTH);
		}
		int startDate = Integer.valueOf(alarmWayDetail.getStartDate());
		int endDate = Integer.valueOf(alarmWayDetail.getEndDate());
		//SysLogger.info("datatype:"+alarmWayDetail.getDateType()+"==currentDate:"+currentDate+"===startDate:"+startDate+"==="+endDate);
		
		if(currentDate >= startDate && currentDate <= endDate){
			result = true;
		}
		if(!result){
			//SysLogger.info( alarmWay.getName() + "====告警类型===" + alarmWayDetail.getAlarmCategory() + "===不再告警日期内==不告警===");
			return result;
		} else if(alarmFilter != null){
			return alarmFilter.isSendAlarm(checkEvent ,alarmIndicatorsNode , alarmWay, alarmWayDetail);
		} 
		return result;
		
	}
	
	public void setNextFilter(AlarmFilter alarmFilter){
		this.alarmFilter = alarmFilter;
	}
	
	public static void main(String[] args){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		System.out.println(calendar.get(Calendar.DAY_OF_WEEK));
	}
	
}
