package com.afunms.config.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.config.model.TimingBackupTelnetConfig;

/**
 * @author HONGLI E-mail: ty564457881@163.com
 * @version 创建时间：Jun 16, 2011 9:03:59 AM
 * 类说明
 */
public class TimingBackupTelnetConfigDao extends BaseDao implements DaoInterface {
	public TimingBackupTelnetConfigDao(){
		super("sys_timingbackup_telnetconfig");
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		TimingBackupTelnetConfig timingBackupTelnetConfig = new TimingBackupTelnetConfig();
		try {
			timingBackupTelnetConfig.setBackup_date(Integer.parseInt(rs.getString("BACKUP_DATE")));
			timingBackupTelnetConfig.setBackup_day_stop(rs.getString("BACKUP_DAY_STOP"));
			timingBackupTelnetConfig.setBackup_month_stop(rs.getString("BACKUP_MONTH_STOP"));
			timingBackupTelnetConfig.setBackup_season_stop(rs.getString("BACKUP_SEASON_STOP"));
			timingBackupTelnetConfig.setBackup_sendfrequency(Integer.parseInt(rs.getString("BACKUP_SENDFREQUENCY")));
			timingBackupTelnetConfig.setBackup_time_day(rs.getString("BACKUP_TIME_DAY"));
			timingBackupTelnetConfig.setBackup_time_hou(rs.getString("BACKUP_TIME_HOU"));
			timingBackupTelnetConfig.setBackup_time_month(rs.getString("BACKUP_TIME_MONTH"));
			timingBackupTelnetConfig.setBackup_time_week(rs.getString("BACKUP_TIME_WEEK"));
			timingBackupTelnetConfig.setBackup_type(rs.getString("BACKUP_TYPE"));
			timingBackupTelnetConfig.setBackup_week_stop(rs.getString("BACKUP_WEEK_STOP"));
			timingBackupTelnetConfig.setBackup_year_stop(rs.getString("BACKUP_YEAR_STOP"));
			timingBackupTelnetConfig.setId(Integer.parseInt(rs.getString("id")));
			timingBackupTelnetConfig.setStatus(rs.getString("status"));
			timingBackupTelnetConfig.setTelnetconfigids(rs.getString("telnetconfigips"));
			timingBackupTelnetConfig.setBkpType(rs.getString("bkpType"));
			timingBackupTelnetConfig.setContent(rs.getString("content"));
			timingBackupTelnetConfig.setCheckupdateflag(rs.getString("checkupdateflag"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return timingBackupTelnetConfig;
	}
	public boolean save(BaseVo vo) {
		TimingBackupTelnetConfig timingBackupTelnetConfig = (TimingBackupTelnetConfig)vo;
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("insert into sys_timingbackup_telnetconfig(telnetconfigips,BACKUP_TYPE,");
		sqlBuffer.append("BACKUP_DATE,BACKUP_SENDFREQUENCY,BACKUP_TIME_MONTH,BACKUP_TIME_WEEK,BACKUP_TIME_DAY,");
		sqlBuffer.append("BACKUP_TIME_HOU,BACKUP_DAY_STOP,BACKUP_WEEK_STOP,BACKUP_MONTH_STOP,BACKUP_SEASON_STOP,");
		sqlBuffer.append("BACKUP_YEAR_STOP,status,bkpType,content,checkupdateflag) values ('");
		sqlBuffer.append(timingBackupTelnetConfig.getTelnetconfigips());
		sqlBuffer.append("','");
		sqlBuffer.append(timingBackupTelnetConfig.getBackup_type());
		sqlBuffer.append("','");
		sqlBuffer.append(timingBackupTelnetConfig.getBackup_date());
		sqlBuffer.append("','");
		sqlBuffer.append(timingBackupTelnetConfig.getBackup_sendfrequency());
		sqlBuffer.append("','");
		sqlBuffer.append(timingBackupTelnetConfig.getBackup_time_month());
		sqlBuffer.append("','");
		sqlBuffer.append(timingBackupTelnetConfig.getBackup_time_week());
		sqlBuffer.append("','");
		sqlBuffer.append(timingBackupTelnetConfig.getBackup_time_day());
		sqlBuffer.append("','");
		sqlBuffer.append(timingBackupTelnetConfig.getBackup_time_hou());
		sqlBuffer.append("','");
		sqlBuffer.append(timingBackupTelnetConfig.getBackup_day_stop());
		sqlBuffer.append("','");
		sqlBuffer.append(timingBackupTelnetConfig.getBackup_week_stop());
		sqlBuffer.append("','");
		sqlBuffer.append(timingBackupTelnetConfig.getBackup_month_stop());
		sqlBuffer.append("','");
		sqlBuffer.append(timingBackupTelnetConfig.getBackup_season_stop());
		sqlBuffer.append("','");
		sqlBuffer.append(timingBackupTelnetConfig.getBackup_year_stop());
		sqlBuffer.append("','");
		sqlBuffer.append(timingBackupTelnetConfig.getStatus());
		sqlBuffer.append("','");
		sqlBuffer.append(timingBackupTelnetConfig.getBkpType());
		sqlBuffer.append("','");
		sqlBuffer.append(timingBackupTelnetConfig.getContent());
		sqlBuffer.append("','");
		sqlBuffer.append(timingBackupTelnetConfig.getCheckupdateflag());
		sqlBuffer.append("')");
		System.out.println(sqlBuffer.toString());
		return saveOrUpdate(sqlBuffer.toString());
	}
	
	public boolean update(BaseVo vo) {
		TimingBackupTelnetConfig timingBackupTelnetConfig = (TimingBackupTelnetConfig)vo;
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("update sys_timingbackup_telnetconfig set telnetconfigips = '");
		sqlBuffer.append(timingBackupTelnetConfig.getTelnetconfigips());
		sqlBuffer.append("',BACKUP_TYPE = '");
		sqlBuffer.append(timingBackupTelnetConfig.getBackup_type());
//		sqlBuffer.append("',BACKUP_DATE = '");
//		sqlBuffer.append(timingBackupTelnetConfig.getBackup_date());
		sqlBuffer.append("',BACKUP_SENDFREQUENCY = '");
		sqlBuffer.append(timingBackupTelnetConfig.getBackup_sendfrequency());
		sqlBuffer.append("',BACKUP_TIME_MONTH = '");
		sqlBuffer.append(timingBackupTelnetConfig.getBackup_time_month());
		sqlBuffer.append("',BACKUP_TIME_WEEK = '");
		sqlBuffer.append(timingBackupTelnetConfig.getBackup_time_week());
		sqlBuffer.append("',BACKUP_TIME_DAY = '");
		sqlBuffer.append(timingBackupTelnetConfig.getBackup_time_day());
		sqlBuffer.append("',BACKUP_TIME_HOU = '");
		sqlBuffer.append(timingBackupTelnetConfig.getBackup_time_hou());
		sqlBuffer.append("',BACKUP_DAY_STOP = '");
		sqlBuffer.append(timingBackupTelnetConfig.getBackup_day_stop());
		sqlBuffer.append("',BACKUP_WEEK_STOP = '");
		sqlBuffer.append(timingBackupTelnetConfig.getBackup_week_stop());
		sqlBuffer.append("',BACKUP_MONTH_STOP = '");
		sqlBuffer.append(timingBackupTelnetConfig.getBackup_month_stop());
		sqlBuffer.append("',BACKUP_SEASON_STOP = '");
		sqlBuffer.append(timingBackupTelnetConfig.getBackup_season_stop());
		sqlBuffer.append("',BACKUP_YEAR_STOP = '");
		sqlBuffer.append(timingBackupTelnetConfig.getBackup_year_stop());
		sqlBuffer.append("',status = '");
		sqlBuffer.append(timingBackupTelnetConfig.getStatus());
		sqlBuffer.append("',bkpType = '");
		sqlBuffer.append(timingBackupTelnetConfig.getBkpType());
		sqlBuffer.append("',content = '");
		sqlBuffer.append(timingBackupTelnetConfig.getContent());
		sqlBuffer.append("',checkupdateflag = '");
		sqlBuffer.append(timingBackupTelnetConfig.getCheckupdateflag());
		sqlBuffer.append("' where id = '");
		sqlBuffer.append(timingBackupTelnetConfig.getId());
		sqlBuffer.append("'");
		SysLogger.info(sqlBuffer.toString());
		return saveOrUpdate(sqlBuffer.toString());
	}
	
	/**
	 * 根据id删除这条记录
	 * 
	 * @param id
	 * @return
	 */
	public boolean deleteById(String id) {
		boolean result = false;
		try {
			conn.addBatch("delete from sys_timingbackup_telnetconfig where id=" + id);
			conn.executeBatch();
			result = true;
		} catch (Exception e) {
			SysLogger.error("TimingBackupTelnetConfigDao.delete()", e);
		} 
		return result;
	}
	
	/**
	 * 得到所有的备份配置文件的记录
	 * 
	 * @return
	 */
	public List getAlList(){
		String sqlString = "select * from sys_timingbackup_telnetconfig where bkpType!='0'";
		return findByCriteria(sqlString);
	}
	
	public List getFileList(){
		String sqlString = "select * from sys_timingbackup_telnetconfig where bkpType='0'";
		return findByCriteria(sqlString);
	}
	/**
	 * 更新定时备份的状态
	 * @param status
	 * @param id
	 * @return
	 */
	public boolean updateStatus(String status,String id){
		StringBuffer sql = new StringBuffer();
		sql.append("update sys_timingbackup_telnetconfig set status = '");
		sql.append(status);
		sql.append("' where id = '");
		sql.append(id);
		sql.append("'");
		boolean result = false;
		try {
			conn.executeUpdate(sql.toString()); 
			result = true;
		} catch (Exception e) {
			SysLogger.error("TimingBackupTelnetConfigDao.updateStatus(String status,String id)", e);
		} 
		return result;
	}
}
