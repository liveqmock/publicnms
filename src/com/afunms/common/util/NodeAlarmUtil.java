package com.afunms.common.util;

import com.afunms.application.dao.NodeIndicatorAlarmDao;
import com.afunms.application.dao.PerformancePanelDao;
import com.afunms.application.model.NodeIndicatorAlarm;
import com.afunms.event.model.EventList;
import com.afunms.topology.model.HostNode;

/**
 * @author HONGLI E-mail: ty564457881@163.com
 * @version 创建时间：Aug 11, 2011 2:38:59 PM
 * 类说明:设备告警信息统计工具类
 */
public class NodeAlarmUtil {
	
	/**
	 * 将告警信息添加到性能面板的数据库中
	 * @param eventList            事件对象
	 * @param alarmIndicatorName   告警指标名称
	 */
	public synchronized static void saveNodeAlarmInfo(EventList eventList, String alarmIndicatorName){
		NodeIndicatorAlarmDao nodeIndicatorAlarmDao = new NodeIndicatorAlarmDao();
		try{
			NodeIndicatorAlarm nodeIndicatorAlarm = new NodeIndicatorAlarm();
			nodeIndicatorAlarm.setAlarmDesc(eventList.getContent());
//			System.out.println(eventList.getLevel1()+"--------------------");
//			System.out.println("--------------------"+eventList.getLevel1().intValue());
			if(eventList.getLevel1()==null){
				nodeIndicatorAlarm.setAlarmLevel("");
			}else {
				nodeIndicatorAlarm.setAlarmLevel(eventList.getLevel1().intValue()+"");
			}
//			nodeIndicatorAlarm.setAlarmLevel(eventList.getLevel1().intValue()+"");
			nodeIndicatorAlarm.setDeviceId(eventList.getNodeid()+"");
			nodeIndicatorAlarm.setDeviceType(eventList.getSubtype());
			nodeIndicatorAlarm.setIndicatorName(alarmIndicatorName);
			boolean flag = nodeIndicatorAlarmDao.isExist(nodeIndicatorAlarm);
			//增加新信息到告警信息数据库
			if(flag){
				nodeIndicatorAlarmDao.update(nodeIndicatorAlarm);
			}else{
				nodeIndicatorAlarmDao.save(nodeIndicatorAlarm);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			nodeIndicatorAlarmDao.close();
		}
	}
	
	/**
	 * 根据类型和ID删除多条数据
	 * @param deviceId
	 * @param deviceType
	 * @return
	 */
	public synchronized static boolean deleteByDeviceIdAndDeviceType(String deviceId, String deviceType){
		boolean flag = false;
		NodeIndicatorAlarmDao nodeIndicatorAlarmDao = new NodeIndicatorAlarmDao();
		PerformancePanelDao performancePanelDao = new PerformancePanelDao();
		try{
			nodeIndicatorAlarmDao.deleteByIdAndType(deviceId, deviceType);
			performancePanelDao.deleteByIdAndType(deviceId, deviceType);
			flag = true;
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			nodeIndicatorAlarmDao.close();
			performancePanelDao.close();
		}
		return flag;
	}
	
	/**
	 * @param obj  根据设备，删除其在设备指标监控面板中的数据 
	 * @return
	 */
	public synchronized static boolean deleteByDeviceIdAndDeviceType(Object obj){
		boolean flag = false;
		if(obj == null){
			return flag;
		}
		String deviceType = null;
		String deviceId = null;
		if(obj instanceof HostNode){
			HostNode host = (HostNode)obj;
			if((host.getCategory()== 4)){
				deviceType = "host";
			}
			if(host.getCategory()< 4 || host.getCategory() == 7 || host.getCategory() == 8|| host.getCategory() == 9){
				deviceType = "net";
			}
			deviceId = host.getId()+"";
		}
		if(deviceType != null && deviceId != null){
			flag = deleteByDeviceIdAndDeviceType(deviceId, deviceType);
		}
		return flag;
	}
	
	/**
	 * 删除性能面板，告警信息列表中的该指标告警记录
	 * @param deviceId      设备ID
	 * @param deviceType    设备类型
	 * @param indicatorName 指标名称
	 * @return
	 */
	public synchronized boolean deleteByDeviceIdAndDeviceTypeAndIndicatorName(String deviceId, String deviceType, String indicatorName){
		boolean flag = false;
		NodeIndicatorAlarmDao nodeIndicatorAlarmDao = new NodeIndicatorAlarmDao();
		try{
			nodeIndicatorAlarmDao.deleteByIdAndTypeAndIndicatorName(deviceId, deviceType, indicatorName);
			flag = true;
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			nodeIndicatorAlarmDao.close();
		}
		return flag;
	}
}
  