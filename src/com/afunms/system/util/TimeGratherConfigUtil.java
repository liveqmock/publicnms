package com.afunms.system.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.system.dao.TimeGratherConfigDao;
import com.afunms.system.model.TimeGratherConfig;

/**
 * 采集时间操作类
 * 
 * @author Snow
 * @since JDK1.5
 * @version 1.0
 * @date 2010-5-19
 */
public class TimeGratherConfigUtil {

	/**
	 * 验证设备id与类型是否为空
	 * 
	 * @param objectId
	 * @param objectType
	 */
	private void validate(String objectId, String objectType) {
		if (objectId == null || "".equals(objectId)) {
			throw new NullPointerException("objectId is null");
		}
		if (objectType == null || "".equals(objectType)) {
			throw new NullPointerException("objectType is null");
		}
	}

	/**
	 * 获取设备类型
	 * 
	 * @param type
	 * @return
	 */
	public String getObjectType(String type) {
		if (type == null) {
			throw new NullPointerException("type is null");
		}
		if ("0".equals(type)) {
			return "equipment";
		}
		if ("1".equals(type)) {
			return "db";
		}
		if ("2".equals(type)) {
			return "ftpservice";
		}
		if ("3".equals(type)) {
			return "emailservice";
		}
		if ("4".equals(type)) {
			return "webservice";
		}
		if ("5".equals(type)) {
			return "grapesservice";
		}
		if ("6".equals(type)) {
			return "radarservice";
		}
		if ("7".equals(type)) {
			return "plotservice";
		}
		if ("8".equals(type)) {
			return "portservice";
		}
		if ("9".equals(type)) {
			return "mq";
		}
		if ("10".equals(type)) {
			return "domino";
		}
		if ("11".equals(type)) {
			return "was";
		}
		if ("12".equals(type)) {
			return "weblogic";
		}
		if ("13".equals(type)) {
			return "tomcat";
		}
		if ("14".equals(type)) {
			return "iis";
		}
		if ("15".equals(type)) {
			return "temperaturehumidity";
		}
		if ("17".equals(type)) {
			return "dns";
		}
		if ("18".equals(type)) {
			return "iislog";
		}
		if ("19".equals(type)) {
			return "process";
		}
		if ("20".equals(type)) {
			return "storage";
		}
		if ("21".equals(type)) {
			return "line";
		}
		if ("22".equals(type)) {
			return "tonglink";
		}
		if ("23".equals(type)) {
			return "ups";
		}
		if ("24".equals(type)) {
			return "tftpservice";
		}
		if ("25".equals(type)) {
			return "resin";
		}
		if ("26".equals(type)) {
			return "weblogin";
		}		
		return null;
	}

	/**
	 * 保存采集时间
	 * 
	 * @param request
	 * @param objectId
	 * @param objectType
	 * @return
	 */
	public boolean saveTimeGratherConfigList(HttpServletRequest request,
			String objectId, String objectType) {
		//SysLogger.info("----------------------------------------");
		if (request == null) {
			throw new NullPointerException("request is null");
		}
		validate(objectId, objectType);
		boolean result = false;
		List<TimeGratherConfig> timeGratherConfigList = new ArrayList<TimeGratherConfig>();
		String[] startHour = request.getParameterValues("startHour");
		String[] startMin = request.getParameterValues("startMin");
		String[] endHour = request.getParameterValues("endHour");
		String[] endMin = request.getParameterValues("endMin");
		
		if (startHour == null || startHour.length == 0) {
			TimeGratherConfigDao dao = new TimeGratherConfigDao();
			try{
				dao.deleteTimeGratherConfigByObject(objectId, objectType);
			}catch(Exception e){
				
			}finally{
				dao.close();
			}
			return true;
		}
		for (int i = 0; i < startHour.length; i++) {
			TimeGratherConfig t = new TimeGratherConfig();
			t.setBeginTime(startHour[i] + ":" + startMin[i]);
			t.setEndTime(endHour[i] + ":" + endMin[i]);
			t.setObjectId(objectId);
			t.setObjectType(objectType);
			if (t.getBeginTime().equals(t.getEndTime())) {
				continue;
			}
			timeGratherConfigList.add(t);
		}
		TimeGratherConfigDao dao = new TimeGratherConfigDao();
		try {
			//SysLogger.info("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&1111");
			result = dao.saveTimeGratherConfigList(objectId, objectType,
					timeGratherConfigList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		//更新内存中的采集时间
		Hashtable timergatherhash = new Hashtable();
		dao = new TimeGratherConfigDao();
		List timerlsit = new ArrayList();
		ShareData.setTimegatherhash(new Hashtable());
		try{
			timerlsit = dao.loadAll();
			if(timerlsit != null && timerlsit.size()>0){
				for(int i=0;i<timerlsit.size();i++){
					TimeGratherConfig timerconfig = (TimeGratherConfig)timerlsit.get(i);
					
					if(ShareData.getTimegatherhash() != null){
						if(ShareData.getTimegatherhash().containsKey(timerconfig.getObjectId()+":"+timerconfig.getObjectType())){
							//已经存在主键，则先获取，然后在追加进去
							SysLogger.info("add============="+timerconfig.getObjectId()+":"+timerconfig.getObjectType());
							((List)ShareData.getTimegatherhash().get(timerconfig.getObjectId()+":"+timerconfig.getObjectType())).add(timerconfig);
						}else{
							List timerlist = new ArrayList();
							timerlist.add(timerconfig);
							SysLogger.info("add============="+timerconfig.getObjectId()+":"+timerconfig.getObjectType());
							ShareData.getTimegatherhash().put(timerconfig.getObjectId()+":"+timerconfig.getObjectType(), timerlist);
						}
					}else{
						List timerlist = new ArrayList();
						timerlist.add(timerconfig);
						Hashtable _timegatherhash = new Hashtable();
						SysLogger.info("add============="+timerconfig.getObjectId()+":"+timerconfig.getObjectType());
						_timegatherhash.put(timerconfig.getObjectId()+":"+timerconfig.getObjectType(), timerlist);
						ShareData.setTimegatherhash(_timegatherhash);
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			dao.close();
		}
		return result;
	}

	/**
	 * 根据设备id与类型获得对应的采集时间
	 * 
	 * @param objectId
	 * @param objectType
	 * @return
	 */
	public List<TimeGratherConfig> getTimeGratherConfig(String objectId,
			String objectType) {
		validate(objectId, objectType);
		TimeGratherConfigDao dao = new TimeGratherConfigDao();
		List<TimeGratherConfig> result = new ArrayList<TimeGratherConfig>();
		try {
			result = dao.findTimeGratherConfigByObject(objectId, objectType);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		return result;
	}

	/**
	 * 根据设备id与类型删除对应的采集时间
	 * 
	 * @param objectId
	 * @param objectType
	 * @return
	 */
	public boolean deleteTimeGratherConfig(String objectId, String objectType) {
		validate(objectId, objectType);
		TimeGratherConfigDao dao = new TimeGratherConfigDao();
		boolean result = false;
		try {
			result = dao.deleteTimeGratherConfigByObject(objectId, objectType);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		return result;
	}

	/**
	 * 判断当前时间是否在设备的采集时间内
	 * 
	 * @param objectId
	 *            设备id
	 * @return 0 当前时间不在设备采集时间内 1 当前时间在设备采集时间内 2 没有发现设备
	 * 
	 */
	public int isBetween(String objectId, String objectType) {
		TimeGratherConfigDao dao = new TimeGratherConfigDao();
		List<TimeGratherConfig> timeGratherConfig = dao
				.findTimeGratherConfigByObject(objectId, objectType);
		if (timeGratherConfig == null || timeGratherConfig.size() == 0) {
			return 2;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("HH-mm");
		String date = sdf.format(new Date());
		int hour = Integer.parseInt(date.split("-")[0]);
		int min = Integer.parseInt(date.split("-")[1]);
		for (TimeGratherConfig tg : timeGratherConfig) {
			tg.setHourAndMin();
			if (hour >= Integer.parseInt(tg.getStartHour())
					&& hour < Integer.parseInt(tg.getEndHour())) {
				return 1;
			} else if (hour == Integer.parseInt(tg.getEndHour())
					&& tg.getStartHour().equals(tg.getEndHour())) {
				if (min >= Integer.parseInt(tg.getStartMin())
						&& min < Integer.parseInt(tg.getEndMin())) {
					return 1;
				}
			} else if (hour == Integer.parseInt(tg.getEndHour())) {
				if (min < Integer.parseInt(tg.getEndMin())) {
					return 1;
				}
			}
		}
		return 0;
	}
	
	/**
	 * 判断当前时间是否在设备的采集时间内
	 * 
	 * @param objectId
	 *            设备id
	 * @return 0 当前时间不在设备采集时间内 1 当前时间在设备采集时间内 2 没有发现设备
	 * 
	 */
	public int isBetween(List timeGratherConfigList) {
		List<TimeGratherConfig> timeGratherConfig = timeGratherConfigList;
		if (timeGratherConfigList == null || timeGratherConfigList.size() == 0) {
			return 2;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("HH-mm");
		String date = sdf.format(new Date());
		int hour = Integer.parseInt(date.split("-")[0]);
		int min = Integer.parseInt(date.split("-")[1]);
		for (TimeGratherConfig tg : timeGratherConfig) {
			tg.setHourAndMin();
			if (hour >= Integer.parseInt(tg.getStartHour())
					&& hour < Integer.parseInt(tg.getEndHour())) {
				if (min >= Integer.parseInt(tg.getStartMin())) {
					return 1;
				}
			} else if (hour == Integer.parseInt(tg.getEndHour())
					&& tg.getStartHour().equals(tg.getEndHour())) {
				if (min >= Integer.parseInt(tg.getStartMin())
						&& min < Integer.parseInt(tg.getEndMin())) {
					return 1;
				}
			} else if (hour == Integer.parseInt(tg.getEndHour())) {
				if (min < Integer.parseInt(tg.getEndMin())) {
					return 1;
				}
			}
		}
		return 0;
	}
}
