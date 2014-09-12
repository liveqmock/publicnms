/**
 * <p>Description: active_server_alarm</p>
 * <p>Company:dhcc.com</p>
 * @author miiwill
 * @project 衡水信用社
 * @date 2007-3-23
 */

package com.afunms.alarm.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.afunms.alarm.model.AlarmWay;
import com.afunms.alarm.model.OutBox;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;

public class OutBoxDao extends BaseDao implements DaoInterface{
    public OutBoxDao(){
    	super("outbox");
    }

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		// TODO Auto-generated method stub
		AlarmWay alarmWay = new AlarmWay();
		try {
			alarmWay.setId(rs.getInt("id"));
			alarmWay.setName(rs.getString("name"));
			alarmWay.setDescription(rs.getString("description"));
			alarmWay.setIsDefault(rs.getString("is_default"));
			alarmWay.setIsPageAlarm(rs.getString("is_page_alarm"));
			alarmWay.setIsSoundAlarm(rs.getString("is_sound_alarm"));
			alarmWay.setIsMailAlarm(rs.getString("is_mail_alarm"));
			alarmWay.setIsPhoneAlarm(rs.getString("is_phone_alarm"));
			alarmWay.setIsSMSAlarm(rs.getString("is_sms_alarm"));
			alarmWay.setIsDesktopAlarm(rs.getString("is_desktop_alarm"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return alarmWay;
	}
	
	public boolean save(BaseVo vo) {
		// TODO Auto-generated method stub
		
		OutBox outbox = (OutBox)vo;
		StringBuffer sql = new StringBuffer();
		sql.append("insert into outbox(sender,ReceiverMobileNo,msg,sendtime) values('");
		sql.append(outbox.getSender());
		sql.append("','");
		sql.append(outbox.getReceiverMobileNo());
		sql.append("','");
		sql.append(outbox.getMsg());
		sql.append("','");
		sql.append(outbox.getSendTime());
		sql.append("')");
		//SysLogger.info(sql.toString());
		return saveOrUpdate(sql.toString());
	}
	
	public boolean update(BaseVo vo) {
		// TODO Auto-generated method stub
		AlarmWay alarmWay = (AlarmWay)vo;
		
		StringBuffer sql = new StringBuffer();
		sql.append("update nms_alarm_way set name ='");
		sql.append(alarmWay.getName());
		sql.append("',description='");
		sql.append(alarmWay.getDescription());
		sql.append("',is_default='");
		sql.append(alarmWay.getIsDefault());
		sql.append("',is_page_alarm='");
		sql.append(alarmWay.getIsPageAlarm());
		sql.append("',is_sound_alarm='");
		sql.append(alarmWay.getIsSoundAlarm());
		sql.append("',is_phone_alarm='");
		sql.append(alarmWay.getIsPhoneAlarm());
		sql.append("',is_sms_alarm='");
		sql.append(alarmWay.getIsSMSAlarm());
		sql.append("',is_mail_alarm='");
		sql.append(alarmWay.getIsMailAlarm());
		sql.append("',is_desktop_alarm='");
		sql.append(alarmWay.getIsDesktopAlarm());
		sql.append("' where id=" + alarmWay.getId());
		return saveOrUpdate(sql.toString());
	}
	
	public AlarmWay getAlarmWayByName(String name)
	{
		AlarmWay alarmWay = new AlarmWay();
		
		rs = conn.executeQuery("select * from nms_alarm_way where name ='"+name);
		try {
			if (rs.next())
				alarmWay = (AlarmWay)rs;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return alarmWay;
	}
	
	public boolean updateIsDefault(String newIsDefault , String oldIsDefault) {
		// TODO Auto-generated method stub
		String sql = "update nms_alarm_way set is_default ='" + newIsDefault + "' where is_default='" + oldIsDefault + "'";
		return saveOrUpdate(sql.toString());
	}
   
}   