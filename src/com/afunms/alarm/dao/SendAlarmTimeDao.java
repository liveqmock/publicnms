
package com.afunms.alarm.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.afunms.alarm.model.SendAlarmTime;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;


public class SendAlarmTimeDao extends BaseDao implements DaoInterface{

	public SendAlarmTimeDao() {
		super("nms_send_alarm_time");
		// TODO Auto-generated constructor stub
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		// TODO Auto-generated method stub
		SendAlarmTime sendAlarmTime = new SendAlarmTime();
		try {
			sendAlarmTime.setName(rs.getString("name"));
			sendAlarmTime.setAlarmWayDetailId(rs.getString("alarm_way_detail_id"));
			sendAlarmTime.setSendTimes(rs.getString("send_times"));
			sendAlarmTime.setLastSendTime(rs.getString("last_send_time"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sendAlarmTime;
	}

	public boolean save(BaseVo vo) {
		// TODO Auto-generated method stub
		SendAlarmTime sendAlarmTime = (SendAlarmTime)vo;
		StringBuffer sql = new StringBuffer();
		sql.append("insert into nms_send_alarm_time(name,alarm_way_detail_id,send_times," +
				"last_send_time" +
				") values('");
		sql.append(sendAlarmTime.getName());
		sql.append("','");
		sql.append(sendAlarmTime.getAlarmWayDetailId());
		sql.append("','");
		sql.append(sendAlarmTime.getSendTimes());
		sql.append("','");
		sql.append(sendAlarmTime.getLastSendTime());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo vo) {
		// TODO Auto-generated method stub
		
		SendAlarmTime sendAlarmTime = new SendAlarmTime();
		StringBuffer sql = new StringBuffer();
		sql.append("update nms_send_alarm_time set name ='");
		sql.append(sendAlarmTime.getName());
		sql.append("',alarm_way_detail_id='");
		sql.append(sendAlarmTime.getAlarmWayDetailId());
		sql.append("',send_times='");
		sql.append(sendAlarmTime.getSendTimes());
		sql.append("',last_send_ime='");
		sql.append(sendAlarmTime.getLastSendTime());
		sql.append("' where name=" + sendAlarmTime.getName());
		return saveOrUpdate(sql.toString());
	}
	
	
	public BaseVo findByNameAndId(String name , String alarmWayDetailId){
		BaseVo vo = null;
		try {
			rs = conn.executeQuery("select * from nms_send_alarm_time where name='" + name + "' and alarm_way_detail_id='" + alarmWayDetailId + "'"); 
			if(rs.next())
				vo = loadFromRS(rs);
		} catch(Exception ex) {
			ex.printStackTrace();
			SysLogger.error("SendAlarmTimeDao.findByNameAndId()",ex);
		} finally {
			conn.close();
		} 
		return vo;
	}
	
	public boolean empty()
	{
		boolean flag = true;
		String sql = "delete from nms_send_alarm_time";
		try{
			conn.executeUpdate(sql);
		}catch(Exception e){
			flag = false;
		}
		return flag;
	}
	
	public boolean delete(String name){
		return saveOrUpdate("delete from nms_send_alarm_time where name='" + name + "'"); 
	}
	
	public boolean deleteByNameAndId(String name , String alarmWayDetailId){
		return saveOrUpdate("delete from nms_send_alarm_time where name='" + name + "' and alarm_way_detail_id='" + alarmWayDetailId + "'"); 
	}
	/**
	 * 
	 * @description 根据名称删除记录
	 * @author wangxiangyong
	 * @date Feb 18, 2013 2:19:24 PM
	 * @return boolean  
	 * @param name
	 * @return
	 */
	public boolean deleteByName(String name){
		return saveOrUpdate("delete from nms_send_alarm_time where name like '" + name + "%'"); 
	}
}