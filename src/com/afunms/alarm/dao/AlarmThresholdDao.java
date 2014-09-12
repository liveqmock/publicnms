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
import java.util.Iterator;
import java.util.List;

import com.afunms.alarm.model.AlarmIndicators;
import com.afunms.alarm.model.AlarmThreshold;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.system.model.TimeShareConfig;

public class AlarmThresholdDao extends BaseDao implements DaoInterface{
    public AlarmThresholdDao(){
    	super("nms_alarm_threshold");
    }

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		// TODO Auto-generated method stub
		AlarmThreshold alarmThreshold = new AlarmThreshold();
		try {
			alarmThreshold.setId(rs.getInt("id"));
			alarmThreshold.setIndicatorsId(rs.getString("indicators_id"));
			alarmThreshold.setDatatype(rs.getString("datatype"));
			alarmThreshold.setLevel(rs.getString("level"));
			alarmThreshold.setAlarmTimes(rs.getString("alarm_times"));
			alarmThreshold.setValue(rs.getString("value"));
			alarmThreshold.setUnit(rs.getString("unit"));
			alarmThreshold.setIsAlarm(rs.getString("isAlarm"));
			alarmThreshold.setType(rs.getString("type"));
			alarmThreshold.setIsSendSMS(rs.getString("isSendSMS"));
			alarmThreshold.setBak(rs.getString("bak"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return alarmThreshold;
	}
	
	public boolean save(BaseVo vo) {
		// TODO Auto-generated method stub
		
		AlarmThreshold alarmThreshold = (AlarmThreshold)vo;
		StringBuffer sql = new StringBuffer();
		sql.append("insert into nms_alarm_threshold(indicators_id, datatype, level, alarm_times, value, unit, isAlarm, type, isSendSMS, bak) values('");
		sql.append(alarmThreshold.getIndicatorsId());
		sql.append("','");
		sql.append(alarmThreshold.getDatatype());
		sql.append("','");
		sql.append(alarmThreshold.getLevel());
		sql.append("','");
		sql.append(alarmThreshold.getAlarmTimes());
		sql.append("','");
		sql.append(alarmThreshold.getValue());
		sql.append("','");
		sql.append(alarmThreshold.getUnit());
		sql.append("','");
		sql.append(alarmThreshold.getIsAlarm());
		sql.append("','");
		sql.append(alarmThreshold.getType());
		sql.append("','");
		sql.append(alarmThreshold.getIsSendSMS());
		sql.append("','");
		sql.append(alarmThreshold.getBak());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}
	
	public boolean update(BaseVo vo) {
		// TODO Auto-generated method stub
		AlarmThreshold alarmThreshold = (AlarmThreshold)vo;
		
		StringBuffer sql = new StringBuffer();
		sql.append("update nms_alarm_threshold set indicators_id='");
		sql.append(alarmThreshold.getIndicatorsId());
		sql.append("',datatype='");
		sql.append(alarmThreshold.getDatatype());
		sql.append("',level='");
		sql.append(alarmThreshold.getLevel());
		sql.append("',alarm_times='");
		sql.append(alarmThreshold.getAlarmTimes());
		sql.append("',value='");
		sql.append(alarmThreshold.getValue());
		sql.append("',unit='");
		sql.append(alarmThreshold.getUnit());
		sql.append("',isAlarm='");
		sql.append(alarmThreshold.getIsAlarm());
		sql.append("',type='");
		sql.append(alarmThreshold.getType());
		sql.append("',isSendSMS='");
		sql.append(alarmThreshold.getIsSendSMS());
		sql.append("',bak='");
		sql.append(alarmThreshold.getBak());
		sql.append("' where id=" + alarmThreshold.getId());
		System.out.println(sql.toString());
		return saveOrUpdate(sql.toString());
	}
	
	public List getAlarmThresholdByIndicatorsId(String indicatorsId){
		StringBuffer sql = new StringBuffer();
		sql.append("select * from nms_alarm_threshold where indicators_id='");
		sql.append(indicatorsId);
		sql.append("'");
		return findByCriteria(sql.toString());
	}
	
	public boolean saveAlarmThresholdList(String indicatorsId , List alarmThresholdList) {
		try {
			String sql2 = "";
			sql2 = "delete from nms_alarm_threshold where indicators_id='" + indicatorsId + "'";
			try {
				conn.executeUpdate(sql2);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			Iterator iterator = alarmThresholdList.iterator();
			while (iterator.hasNext()) {
				AlarmThreshold alarmThreshold = (AlarmThreshold) iterator
						.next();
				
				StringBuffer sql = new StringBuffer();
				sql.append("insert into nms_alarm_threshold(indicators_id, datatype, level, alarm_times, value, unit, isAlarm, type) values('");
				sql.append(alarmThreshold.getIndicatorsId());
				sql.append("','");
				sql.append(alarmThreshold.getDatatype());
				sql.append("','");
				sql.append(alarmThreshold.getLevel());
				sql.append("','");
				sql.append(alarmThreshold.getAlarmTimes());
				sql.append("','");
				sql.append(alarmThreshold.getValue());
				sql.append("','");
				sql.append(alarmThreshold.getUnit());
				sql.append("','");
				sql.append(alarmThreshold.getIsAlarm());
				sql.append("','");
				sql.append(alarmThreshold.getType());
				sql.append("')");
				try {
					conn.addBatch(sql.toString());
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
			conn.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception ex) {
					ex.printStackTrace();
					return false;
				}
			}
		}
		return true;
	}
   
}   