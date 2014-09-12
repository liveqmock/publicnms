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
import java.util.ArrayList;
import java.util.List;

import com.afunms.alarm.model.AlarmIndicators;
import com.afunms.alarm.model.AlarmThresholdDefault;
import com.afunms.application.model.ApacheConfig;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;

public class AlarmIndicatorsDao extends BaseDao implements DaoInterface{
    public AlarmIndicatorsDao(){
    	super("nms_alarm_indicators");
    }

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		// TODO Auto-generated method stub
		AlarmIndicators alarmIndicators = new AlarmIndicators();
		try {
			alarmIndicators.setId(rs.getInt("id"));
			alarmIndicators.setName(rs.getString("name"));
			alarmIndicators.setType(rs.getString("type"));
			alarmIndicators.setSubtype(rs.getString("subtype"));
			alarmIndicators.setDatatype(rs.getString("datatype"));
			alarmIndicators.setMoid(rs.getString("moid"));
			alarmIndicators.setThreshlod(rs.getInt("threshold"));
			alarmIndicators.setThreshlod_unit(rs.getString("threshold_unit"));
			alarmIndicators.setCompare(rs.getInt("compare"));
			alarmIndicators.setCompare_type(rs.getInt("compare_type"));
			alarmIndicators.setAlarm_times(rs.getString("alarm_times"));
			alarmIndicators.setAlarm_info(rs.getString("alarm_info"));
			alarmIndicators.setAlarm_level(rs.getString("alarm_level"));
			alarmIndicators.setEnabled(rs.getString("enabled"));
			alarmIndicators.setPoll_interval(rs.getString("poll_interval"));
			alarmIndicators.setInterval_unit(rs.getString("interval_unit"));
			alarmIndicators.setSubentity(rs.getString("subentity"));
			alarmIndicators.setLimenvalue0(rs.getString("limenvalue0"));
			alarmIndicators.setLimenvalue1(rs.getString("limenvalue1"));
			alarmIndicators.setLimenvalue2(rs.getString("limenvalue2"));
			alarmIndicators.setTime0(rs.getString("time0"));
			alarmIndicators.setTime1(rs.getString("time1"));
			alarmIndicators.setTime2(rs.getString("time2"));
			alarmIndicators.setSms0(rs.getString("sms0"));
			alarmIndicators.setSms1(rs.getString("sms1"));
			alarmIndicators.setSms2(rs.getString("sms2"));
			alarmIndicators.setCategory(rs.getString("category"));
			alarmIndicators.setDescr(rs.getString("descr"));
			alarmIndicators.setUnit(rs.getString("unit"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return alarmIndicators;
	}
	
	public boolean save(BaseVo vo) {
		// TODO Auto-generated method stub
		
		AlarmIndicators alarmIndicators = (AlarmIndicators)vo;
		StringBuffer sql = new StringBuffer();
		sql.append("insert into nms_alarm_indicators(name, type, subtype, datatype, moid, threshold, " +
				"threshold_unit, compare, compare_type, alarm_times, alarm_info, alarm_level," +
				" enabled, poll_interval, interval_unit, subentity, limenvalue0, limenvalue1, " +
				"limenvalue2, time0, time1, time2, sms0, sms1, sms2, category, descr, unit) values('");
		sql.append(alarmIndicators.getName());
		sql.append("','");
		sql.append(alarmIndicators.getType());
		sql.append("','");
		sql.append(alarmIndicators.getSubtype());
		sql.append("','");
		sql.append(alarmIndicators.getDatatype());
		sql.append("','");
		sql.append(alarmIndicators.getMoid());
		sql.append("','");
		sql.append(alarmIndicators.getThreshlod());
		sql.append("','");
		sql.append(alarmIndicators.getThreshlod_unit());
		sql.append("','");
		sql.append(alarmIndicators.getCompare());
		sql.append("','");
		sql.append(alarmIndicators.getCompare_type());
		sql.append("','");
		sql.append(alarmIndicators.getAlarm_times());
		sql.append("','");
		sql.append(alarmIndicators.getAlarm_info());
		sql.append("','");
		sql.append(alarmIndicators.getAlarm_level());
		sql.append("','");
		sql.append(alarmIndicators.getEnabled());
		sql.append("','");
		sql.append(alarmIndicators.getPoll_interval());
		sql.append("','");
		sql.append(alarmIndicators.getInterval_unit());
		sql.append("','");
		sql.append(alarmIndicators.getSubentity());
		sql.append("','");
		sql.append(alarmIndicators.getLimenvalue0());
		sql.append("','");
		sql.append(alarmIndicators.getLimenvalue1());
		sql.append("','");
		sql.append(alarmIndicators.getLimenvalue2());
		sql.append("','");
		sql.append(alarmIndicators.getTime0());
		sql.append("','");
		sql.append(alarmIndicators.getTime1());
		sql.append("','");
		sql.append(alarmIndicators.getTime2());
		sql.append("','");
		sql.append(alarmIndicators.getSms0());
		sql.append("','");
		sql.append(alarmIndicators.getSms1());
		sql.append("','");
		sql.append(alarmIndicators.getSms2());
		sql.append("','");
		sql.append(alarmIndicators.getCategory());
		sql.append("','");
		sql.append(alarmIndicators.getDescr());
		sql.append("','");
		sql.append(alarmIndicators.getUnit());
		sql.append("')");
		//System.out.println(sql);
		return saveOrUpdate(sql.toString());
	}
	
	public boolean update(BaseVo vo) {
		// TODO Auto-generated method stub
		AlarmIndicators alarmIndicators = (AlarmIndicators)vo;
		
		StringBuffer sql = new StringBuffer();
		sql.append("update nms_alarm_indicators set name ='");
		sql.append(alarmIndicators.getName());
		sql.append("',type='");
		sql.append(alarmIndicators.getType());
		sql.append("',subtype='");
		sql.append(alarmIndicators.getSubtype());
		sql.append("',datatype='");
		sql.append(alarmIndicators.getDatatype());
		sql.append("',moid='");
		sql.append(alarmIndicators.getMoid());
		sql.append("',threshold='");
		sql.append(alarmIndicators.getThreshlod());
		sql.append("',threshold_unit='");
		sql.append(alarmIndicators.getThreshlod_unit());
		sql.append("',compare='");
		sql.append(alarmIndicators.getCompare());
		sql.append("',compare_type='");
		sql.append(alarmIndicators.getCompare_type());
		sql.append("',alarm_times='");
		sql.append(alarmIndicators.getAlarm_times());
		sql.append("',alarm_info='");
		sql.append(alarmIndicators.getAlarm_info());
		sql.append("',alarm_level='");
		sql.append(alarmIndicators.getAlarm_level());
		sql.append("',enabled='");
		sql.append(alarmIndicators.getEnabled());
		sql.append("',poll_interval='");
		sql.append(alarmIndicators.getPoll_interval());
		sql.append("',interval_unit='");
		sql.append(alarmIndicators.getInterval_unit());
		sql.append("',subentity='");
		sql.append(alarmIndicators.getSubentity());
		sql.append("',limenvalue0='");
		sql.append(alarmIndicators.getLimenvalue0());
		sql.append("',limenvalue1='");
		sql.append(alarmIndicators.getLimenvalue1());
		sql.append("',limenvalue2='");
		sql.append(alarmIndicators.getLimenvalue2());
		sql.append("',time0='");
		sql.append(alarmIndicators.getTime0());
		sql.append("',time1='");
		sql.append(alarmIndicators.getTime1());
		sql.append("',time2='");
		sql.append(alarmIndicators.getTime2());
		sql.append("',sms0='");
		sql.append(alarmIndicators.getSms0());
		sql.append("',sms1='");
		sql.append(alarmIndicators.getSms1());
		sql.append("',sms2='");
		sql.append(alarmIndicators.getSms2());
		sql.append("',category='");
		sql.append(alarmIndicators.getCategory());
		sql.append("',descr='");
		sql.append(alarmIndicators.getDescr());
		sql.append("',unit='");
		sql.append(alarmIndicators.getUnit());
		sql.append("' where id=" + alarmIndicators.getId());
//		System.out.println(sql.toString());
		return saveOrUpdate(sql.toString());
	}
	
//	public AlarmIndicators getByTypeAndSubTypeAndName(String type ,String subtype ,String name){
//		
//		AlarmIndicators alarmIndicators = null;
//		
//		String sql = "select * from nms_alarm_indicators where type='" + type + "' and subtype='" + subtype
//			+ "' and name='" + name + "'";
//		List list = findByCriteria(sql);
//		
//		if(list!=null && list.size() ==1){
//			alarmIndicators = (AlarmIndicators)list.get(0);
//		}
//		
//		return alarmIndicators;
//	}
	
	public List getByTypeAndSubType(String type ,String subtype){
		String sql = "select * from nms_alarm_indicators where type='" + type + "' and subtype='" + subtype + "'";
		
//		System.out.println("---告警配置----------"+sql);
		
		List list = findByCriteria(sql);
		
//		System.out.println("---告警配置-----list.size-----"+list.size());
		return list;
	}
	
	//VMWare 
	public List VMgetByTypeAndSubType(String type ,String subtype,String category){
		String sql = "select * from nms_alarm_indicators where type='" + type + "' and subtype='" + subtype + "' and category='"+category+"'";
		
		
		List list = findByCriteria(sql);
		
		return list;
	}
	
	
	public List getByTypeAndSubType(String type ,String subtype,String indiname){
		String sql = "select * from nms_alarm_indicators where type='" + type + "' and subtype='" + subtype + "' and name='"+indiname+"'";
		System.out.println("----------------sql---------------"+sql);
		List list = findByCriteria(sql);
		return list;
	}
	
	/**
	 * 根据设备类型得到设备的告警指标
	 * @param type
	 * @return
	 */
	public List getByType(String type){
		 List list = new ArrayList();
		 try {
			rs = conn.executeQuery("select distinct name,descr from nms_alarm_indicators where type='"
							+ type + "' group by name");
			if (rs == null){
				return null;
			}
			while (rs.next()){
				AlarmIndicators alarmIndicators = new AlarmIndicators();
				alarmIndicators.setName(rs.getString("name"));
				alarmIndicators.setDescr(rs.getString("descr"));
				list.add(alarmIndicators);
			}
		} catch (Exception e) {
			list = null;
			e.printStackTrace();
			SysLogger.error("BaseDao.findByCondition()", e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			conn.close();
		}
		return list;
	}
	
	public List alarmfind(String con1,String con2){
		if(con1.equals("全部")&&con2.equals("全部")){
			return findByCriteria("select * from nms_alarm_indicators");
		}
		else if(!con1.equals("全部")&&con2.equals("全部")){
			return findByCriteria("select * from nms_alarm_indicators where type='"+con1+"'");
		}
		else if(!con1.equals("全部")&&!con2.equals("全部")){
			return findByCriteria("select * from nms_alarm_indicators where type='"+con1+"' and subtype='"+con2+"'");
		}
		else{
			return findByCriteria("select * from nms_alarm_indicators");
		}
	}
	
	/**
	 * @author HONGLI 
	 * @param name  告警指标的名称
	 * @param type  类型
	 * @param subtype 子类型
	 * @return 
	 */
	public AlarmIndicators getAlarmIndicatorsByNameAndTypeAndSubType(String name,String type,String subtype){
		AlarmIndicators alarmIndicators = new AlarmIndicators();
		String sql =  "select * from nms_alarm_indicators where name ='"+name+"' and type='" + type + "' and subtype='" + subtype + "'";
		List list = findByCriteria(sql);
		if(list == null || list.size()==0){
			return null;
		}
		alarmIndicators = (AlarmIndicators)list.get(0);
		return alarmIndicators;
	}
	public List updatelist(int id){
		String type = "";
		String subtype = "";
		try {
			rs = conn.executeQuery("select * from nms_alarm_indicators where id='"
							+ id + "';");
			while(rs.next()){
			type = rs.getString("type");
			subtype = rs.getString("subtype");
			}
		} catch (Exception e) {
		}
		return findByCriteria("select * from nms_alarm_indicators where type='"+type+"' and subtype='"+subtype+"';");
	}
	public String type(int id){
		String type="";
		try {
					rs=conn.executeQuery("select * from nms_alarm_indicators where id='"
				+ id + "';");
		while(rs.next()){
			type=rs.getString("type");
		}
		} catch (Exception e) {
		}
		return type;
	}
	
	public String subtype(int id){
		String subtype="";
		try {
			rs=conn.executeQuery("select * from nms_alarm_indicators where id='"
					+ id + "';");
			while(rs.next()){
				subtype=rs.getString("subtype");
			}
		} catch (Exception e) {
		}
		return subtype;
	}
}   
