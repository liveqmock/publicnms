package com.afunms.alarm.send;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.model.AlarmWay;
import com.afunms.alarm.model.AlarmWayDetail;
import com.afunms.alarm.util.AlarmWayUtil;
import com.afunms.common.util.SysLogger;
import com.afunms.event.model.CheckEvent;

public class AlarmCatgoryFilter implements AlarmFilter{
	
	public AlarmFilter alarmFilter = null;
	
	public boolean isSendAlarm(CheckEvent checkEvent, AlarmIndicatorsNode alarmIndicatorsNode , AlarmWay alarmWay , AlarmWayDetail alarmWayDetail){
		boolean result = false;
		if(AlarmWayUtil.ALARM_WAY_CATEGORY_PAGE.equals(alarmWayDetail.getAlarmCategory())){
			if("1".equals(alarmWay.getIsPageAlarm())){
				//result = true;
				return true;
			}
		} else if (AlarmWayUtil.ALARM_WAY_CATEGORY_SOUND.equals(alarmWayDetail.getAlarmCategory())){
			if("1".equals(alarmWay.getIsSoundAlarm())){
				result = true;
			}
		} else if (AlarmWayUtil.ALARM_WAY_CATEGORY_MAIL.equals(alarmWayDetail.getAlarmCategory())){
			if("1".equals(alarmWay.getIsMailAlarm())){
				result = true;
			}
		} else if (AlarmWayUtil.ALARM_WAY_CATEGORY_SMS.equals(alarmWayDetail.getAlarmCategory())){
			if("1".equals(alarmWay.getIsSMSAlarm())){
				result = true;
			}
		} else if (AlarmWayUtil.ALARM_WAY_CATEGORY_PHONE.equals(alarmWayDetail.getAlarmCategory())){
			if("1".equals(alarmWay.getIsPhoneAlarm())){
				result = true;
			}
		}else if (AlarmWayUtil.ALARM_WAY_CATEGORY_DESKTOP.equals(alarmWayDetail.getAlarmCategory())){
			if("1".equals(alarmWay.getIsDesktopAlarm())){
				result = true;
			}
		}
		
		if(!result){
			SysLogger.info( alarmWay.getName() + "====告警类型===" + alarmWayDetail.getAlarmCategory() + "===未选中===不告警=====");
			return result;
		} else if(alarmFilter != null){
			return alarmFilter.isSendAlarm(checkEvent, alarmIndicatorsNode, alarmWay, alarmWayDetail);
		} 
		
		return result;
		
	}
	
	public void setNextFilter(AlarmFilter alarmFilter){
		this.alarmFilter = alarmFilter;
	}
	
}
