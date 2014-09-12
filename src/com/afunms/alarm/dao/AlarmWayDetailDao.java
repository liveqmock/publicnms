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
import java.util.List;

import com.afunms.alarm.model.AlarmWay;
import com.afunms.alarm.model.AlarmWayDetail;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;

public class AlarmWayDetailDao extends BaseDao implements DaoInterface{
    public AlarmWayDetailDao(){
    	super("nms_alarm_way_detail");
    }

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		// TODO Auto-generated method stub
		AlarmWayDetail alarmWayDetail = new AlarmWayDetail();
		try {
			alarmWayDetail.setId(rs.getInt("id"));
			alarmWayDetail.setAlarmWayId(rs.getString("alarm_way_id"));
			alarmWayDetail.setAlarmCategory(rs.getString("alarm_category"));
			alarmWayDetail.setDateType(rs.getString("date_type"));
			alarmWayDetail.setSendTimes(rs.getString("send_times"));
			alarmWayDetail.setStartDate(rs.getString("start_date"));
			alarmWayDetail.setEndDate(rs.getString("end_date"));
			alarmWayDetail.setStartTime(rs.getString("start_time"));
			alarmWayDetail.setEndTime(rs.getString("end_time"));
			alarmWayDetail.setUserIds(rs.getString("user_ids"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return alarmWayDetail;
	}
	
	public boolean save(BaseVo vo) {
		// TODO Auto-generated method stub
		
		AlarmWayDetail alarmWayDetail = (AlarmWayDetail)vo;
		StringBuffer sql = new StringBuffer();
		sql.append("insert into nms_alarm_way_detail(alarm_way_id,alarm_category,date_type," +
				"send_times,start_date,end_date,start_time,end_time,user_ids" +
				") values('");
		sql.append(alarmWayDetail.getAlarmCategory());
		sql.append("','");
		sql.append(alarmWayDetail.getAlarmCategory());
		sql.append("','");
		sql.append(alarmWayDetail.getDateType());
		sql.append("','");
		sql.append(alarmWayDetail.getSendTimes());
		sql.append("','");
		sql.append(alarmWayDetail.getStartDate());
		sql.append("','");
		sql.append(alarmWayDetail.getEndDate());
		sql.append("','");
		sql.append(alarmWayDetail.getStartTime());
		sql.append("','");
		sql.append(alarmWayDetail.getEndTime());
		sql.append("','");
		sql.append(alarmWayDetail.getUserIds());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}
	
	public boolean save(List list) {
		// TODO Auto-generated method stub
		try {
			if(list != null){
				for(int i = 0 ; i <list.size() ; i++){
					StringBuffer sql = new StringBuffer();
					AlarmWayDetail alarmWayDetail = (AlarmWayDetail)list.get(i);
					sql.append("insert into nms_alarm_way_detail(alarm_way_id,alarm_category,date_type," +
							"send_times,start_date,end_date,start_time,end_time,user_ids" +
							") values('");
					sql.append(alarmWayDetail.getAlarmWayId());
					sql.append("','");
					sql.append(alarmWayDetail.getAlarmCategory());
					sql.append("','");
					sql.append(alarmWayDetail.getDateType());
					sql.append("','");
					sql.append(alarmWayDetail.getSendTimes());
					sql.append("','");
					sql.append(alarmWayDetail.getStartDate());
					sql.append("','");
					sql.append(alarmWayDetail.getEndDate());
					sql.append("','");
					sql.append(alarmWayDetail.getStartTime());
					sql.append("','");
					sql.append(alarmWayDetail.getEndTime());
					sql.append("','");
					sql.append(alarmWayDetail.getUserIds());
					sql.append("')");
					conn.addBatch(sql.toString());
				}
				conn.executeBatch();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(conn!=null){
				conn.close();
			}
		}
		return true;
	}
	
	
	public boolean deleteByAlarmWayId(String alarmWayId){
		String sql = "delete from nms_alarm_way_detail where alarm_way_id='" + alarmWayId + "'";
		return saveOrUpdate(sql);
	}
	
	public List<AlarmWayDetail> findByAlarmWayId(String alarmWayId){
		String sql = "select * from nms_alarm_way_detail where alarm_way_id='" + alarmWayId + "'";
		return findByCriteria(sql);
	}


	public boolean update(BaseVo vo) {
		// TODO Auto-generated method stub
		return false;
	}
	
   
}   