package com.afunms.alarm.util;

import java.util.ArrayList;
import java.util.List;

import com.afunms.alarm.dao.AlarmWayDao;
import com.afunms.alarm.dao.AlarmWayDetailDao;
import com.afunms.alarm.model.AlarmWay;
import com.afunms.alarm.model.AlarmWayDetail;

public class AlarmWayUtil {
	
	/**
	 * 日期类型为 月
	 */
	public static String DATE_TYPE_MONTH = "month";
	
	/**
	 * 日期类型为 周
	 */
	public static String DATE_TYPE_WEEK = "week";
	
	/**
	 * 告警类型 页面
	 */
	public static String ALARM_WAY_CATEGORY_PAGE = "page";
	
	/**
	 * 告警类型 声音
	 */
	public static String ALARM_WAY_CATEGORY_SOUND = "sound";
	
	/**
	 * 告警类型 邮件
	 */
	public static String ALARM_WAY_CATEGORY_MAIL = "mail";
	
	/**
	 * 告警类型 短信
	 */
	public static String ALARM_WAY_CATEGORY_SMS = "sms";
	
	/**
	 * 告警类型 电话
	 */
	public static String ALARM_WAY_CATEGORY_PHONE = "phone";
	
	/**
	 * 告警类型 桌面
	 */
	public static String ALARM_WAY_CATEGORY_DESKTOP = "desktop";
	
	/**
	 * 将告警方式的详细配置存入数据库中
	 * 此方法将 alarmWayDetailList 中 alarmWayDetail 的 alarmWayId 设置好后 
	 * 先根据 alarmWayId 删除 alarmWayDetail
	 * 后调用saveAlarmWayDetail(List alarmWayDetailList)方法
	 * @param alarmWay
	 * @param alarmWayDetailList
	 */
	public void saveAlarmWayDetail(AlarmWay alarmWay , List alarmWayDetailList){
		List list = new ArrayList();
		if(alarmWayDetailList != null){
			for(int i = 0 ; i < alarmWayDetailList.size() ; i++){
				AlarmWayDetail alarmWayDetail = (AlarmWayDetail)alarmWayDetailList.get(i);
				alarmWayDetail.setAlarmWayId(alarmWay.getId()+"");
				list.add(alarmWayDetail);
			}
			
			AlarmWayDetailDao alarmWayDetailDao = new AlarmWayDetailDao();
			try {
				alarmWayDetailDao.deleteByAlarmWayId(alarmWay.getId()+"");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				alarmWayDetailDao.close();
			}
			
			saveAlarmWayDetail(list);
		}
		
	}
	
	/**
	 * 将告警方式的详细配置存入数据库中
	 * @param alarmWayDetailList
	 */
	public void saveAlarmWayDetail(List alarmWayDetailList){
		AlarmWayDetailDao alarmWayDetailDao = new AlarmWayDetailDao();
		try {
			alarmWayDetailDao.save(alarmWayDetailList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			alarmWayDetailDao.close();
		}
		
	}
	
	/**
	 * 将根据 alarmWayId 删除数据库中告警方式的详细配置
	 * @param alarmWayId
	 */
	public void deleteAlarmWayDetail(String[] ids){
		if(ids !=null){
			for(int i = 0 ; i < ids.length ; i++){
				deleteAlarmWayDetail(ids[i]);
			}
		}
		
	}
	
	/**
	 * 将根据 alarmWayId 删除数据库中告警方式的详细配置
	 * @param alarmWayId
	 */
	public void deleteAlarmWayDetail(String alarmWayId){
		AlarmWayDetailDao alarmWayDetailDao = new AlarmWayDetailDao();
		try {
			alarmWayDetailDao.deleteByAlarmWayId(alarmWayId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			alarmWayDetailDao.close();
		}
		
	}
	
	
	/**
	 * 保存 AlarmWay 入库
	 * @param alarmWayId
	 */
	public boolean saveAlarmWay(AlarmWay alarmWay){
		
		if("1".equals(alarmWay.getIsDefault())){
			updateIsDefault("0", "1");
		}
		
		boolean result = false;
		AlarmWayDao alarmWayDao = new AlarmWayDao();
		try {
			result = alarmWayDao.save(alarmWay);
		} catch (RuntimeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			alarmWayDao.close();
		}
		
		return result;
		
	}
	
	/**
	 * 更新 AlarmWay 入库
	 * @param alarmWayId
	 */
	public boolean updateAlarmWay(AlarmWay alarmWay){
		
		if("1".equals(alarmWay.getIsDefault())){
			updateIsDefault("0", "1");
		}
		
		boolean result = false;
		AlarmWayDao alarmWayDao = new AlarmWayDao();
		try {
			result = alarmWayDao.update(alarmWay);
		} catch (RuntimeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			alarmWayDao.close();
		}
		
		return result;
		
	}
	
	
	/**
	 * 更新 isDefault 入库
	 * @param alarmWayId
	 */
	public boolean updateIsDefault(String newIsDefault , String oldIsDefault){
		boolean result = false;
		AlarmWayDao alarmWayDao = new AlarmWayDao();
		try {
			result = alarmWayDao.updateIsDefault(newIsDefault , oldIsDefault);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			alarmWayDao.close();
		}
		
		return result;
		
	}
	
	
	
}
